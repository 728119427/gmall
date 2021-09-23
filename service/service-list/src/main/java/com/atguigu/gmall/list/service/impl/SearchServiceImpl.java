package com.atguigu.gmall.list.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.list.service.SearchService;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.list.SearchResponseAttrVo;
import com.atguigu.gmall.model.list.SearchResponseTmVo;
import jodd.util.StringUtil;
import org.apache.lucene.queryparser.xml.builders.BooleanQueryBuilder;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.InetAddress;
import java.util.*;

@Service
public class SearchServiceImpl implements SearchService {
    @Autowired
    private RestHighLevelClient restHighLevelClient;
    @Value("${es.search.param}")
    private String searchParam;

    /**
     * 根据条件查询商品
     *
     * @param searchMap
     * @return
     */
    @Override
    public Map<String, Object> search(Map<String, String> searchMap) {
        Map<String,Object> resultMap= new HashMap<>();
        //校验参数
        if(searchMap==null){
            return null;
        }
        try {
            //构建商品查询条件
            SearchRequest searchRequest = buildQueryRequest(searchMap,resultMap);
            //查询商品
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            //解析返回结果，获取查询商品
            resultMap.putAll(getSearchData(searchResponse));
            resultMap.put("totals",searchResponse.getHits().totalHits);
            return resultMap;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 解析查询结果
     * @param searchResponse
     * @return
     */
    private Map<String, Object> getSearchData(SearchResponse searchResponse) {
        Map<String,Object> resultMap = new HashMap<>();
        List<Goods> goodsList = new ArrayList<>();
        //获取命中结果
        SearchHits hits = searchResponse.getHits();
        //遍历取值
        for (SearchHit hit : hits) {
            String sourceAsString = hit.getSourceAsString();
            Goods goods = JSONObject.parseObject(sourceAsString, Goods.class);
            //解析高亮数据  Todo
            HighlightField titleHighlightField = hit.getHighlightFields().get("title");
            if(!ObjectUtils.isEmpty(titleHighlightField)){
                StringBuilder titleBuilder = new StringBuilder("");
                for (Text fragment : titleHighlightField.getFragments()) {
                    titleBuilder.append(fragment);
                }
                goods.setTitle(titleBuilder.toString());
            }

            goodsList.add(goods);
        }
        //获取品牌聚合的结果
        Map<String, Aggregation> aggregationMap = searchResponse.getAggregations().asMap();
        List<SearchResponseTmVo> trademarkAggResult =getTrademarkAggResult(aggregationMap);
        //获取平台属性聚合的结果
        List<SearchResponseAttrVo> searchResponseAttrVos=getAttrInfoAggResult(aggregationMap);
        //添加到结果集
        resultMap.put("attrInfoAgg",searchResponseAttrVos);
        resultMap.put("goodsList",goodsList);
        resultMap.put("trademarkAgg",trademarkAggResult);
        return resultMap;
    }

    /**
     * 获取平台属性的聚合结果
     * @param aggregationMap
     * @return
     */
    private List<SearchResponseAttrVo> getAttrInfoAggResult(Map<String, Aggregation> aggregationMap) {
        List<SearchResponseAttrVo> searchResponseAttrVos = new ArrayList<>();
        //获取平台属性聚合结果
        ParsedNested aggAttrs=(ParsedNested)aggregationMap.get("aggAttrs");
        //解析aggAttrs
        Aggregations attrsAggregations = aggAttrs.getAggregations();
        ParsedLongTerms aggAttrId=attrsAggregations.get("aggAttrId");
        for (Terms.Bucket bucket : aggAttrId.getBuckets()) {
            SearchResponseAttrVo searchResponseAttrVo = new SearchResponseAttrVo();
            //获取attrId
            long attrId = bucket.getKeyAsNumber().longValue();
            searchResponseAttrVo.setAttrId(attrId);
            //获取attrName
            Aggregations bucketAggregations = bucket.getAggregations();
            ParsedStringTerms aggAttrName=bucketAggregations.get("aggAttrName");
            if(aggAttrName.getBuckets().size()>0){
                String attrName = aggAttrName.getBuckets().get(0).getKeyAsString();
                searchResponseAttrVo.setAttrName(attrName);
            }
            //获取attrValue
            ParsedStringTerms aggAttrValue=bucketAggregations.get("aggAttrValue");
            if(aggAttrValue.getBuckets().size()>0){
                List<String> attrValueList = new ArrayList<>();
                for (Terms.Bucket aggAttrValueBucket : aggAttrValue.getBuckets()) {
                    String attrValue = aggAttrValueBucket.getKeyAsString();
                    attrValueList.add(attrValue);
                }
                searchResponseAttrVo.setAttrValueList(attrValueList);
            }
            //添加到结果集中
            searchResponseAttrVos.add(searchResponseAttrVo);
        }

        return searchResponseAttrVos;

    }

    /**
     * 解析聚合查询结果
     * @param aggregationMap
     * @return
     */
    private List<SearchResponseTmVo> getTrademarkAggResult(Map<String, Aggregation> aggregationMap) {
        List<SearchResponseTmVo> searchResponseTmVos = new ArrayList<>();
        ParsedLongTerms aggBucket=(ParsedLongTerms)aggregationMap.get("aggTmId");
        for (Terms.Bucket bucket : aggBucket.getBuckets()) {
            SearchResponseTmVo searchResponseTmVo = new SearchResponseTmVo();
            //获取品牌Id
            long tmId = bucket.getKeyAsNumber().longValue();
            searchResponseTmVo.setTmId(tmId);
            //获取品牌名称
            Aggregations aggregations = bucket.getAggregations();
            ParsedStringTerms aggTmName = aggregations.get("aggTmName");
            if(aggTmName.getBuckets().size()>0){
                String tmName = aggTmName.getBuckets().get(0).getKeyAsString();
                searchResponseTmVo.setTmName(tmName);
            }
            //获取品牌logoUrl
            ParsedStringTerms aggTmLogoUrl =aggregations.get("aggTmLogoUrl");
            if(aggTmLogoUrl.getBuckets().size()>0){
                String tmLogoUrl = aggTmLogoUrl.getBuckets().get(0).getKeyAsString();
                searchResponseTmVo.setTmLogoUrl(tmLogoUrl);
            }
            searchResponseTmVos.add(searchResponseTmVo);
        }

        return searchResponseTmVos;
    }

    /**
     * 构建商品查询条件
     * @param searchMap
     * @return
     */
    private SearchRequest buildQueryRequest(Map<String, String> searchMap,Map<String,Object> resultMap) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //关键字查询
        if(!StringUtils.isEmpty(searchMap.get("keywords"))){
            boolQueryBuilder.must(QueryBuilders.matchQuery("title",searchMap.get("keywords")));
        }
        //品牌查询
        String tradeMark = searchMap.get("tradeMark");
        if(!StringUtils.isEmpty(tradeMark)){
            String tmName = searchMap.get("tradeMark").split(":")[1];
            boolQueryBuilder.must(QueryBuilders.termQuery("tmName",tmName));
        }
        //平台属性查询
        Set<String> searchMapKeys = searchMap.keySet();
        for (String searchMapKey : searchMapKeys) {
            if(searchMapKey.startsWith("attr_")){
                BoolQueryBuilder nestedBoolQuery = new BoolQueryBuilder();
                String attrValue = searchMap.get(searchMapKey);
                String attrName = searchMapKey.replace("attr_","");
                //构建查询条件
                nestedBoolQuery.must(QueryBuilders.termQuery("attrs.attrName",attrName))
                               .must(QueryBuilders.termQuery("attrs.attrValue",attrValue));
                boolQueryBuilder.must(QueryBuilders.nestedQuery("attrs",nestedBoolQuery, ScoreMode.None));
            }
        }

        //价格查询
        String price = searchMap.get("price");
        if(!StringUtils.isEmpty(price)){
            price=price.replace("元","").replace("以上","");
            String[] prices = price.split("-");
            if(prices.length==2){
                boolQueryBuilder.must(QueryBuilders.rangeQuery("price").gte(prices[0]).lte(prices[1]));
            }else {
                boolQueryBuilder.must(QueryBuilders.rangeQuery("price").gte(prices[0]));
            }

        }

        //分页查询
        Integer size = 10;//后台确定每页的查询数量
        Integer pageNum = getPageNum(searchMap.get("pageNum"));
        //讲分页参数添加到resultMap
        resultMap.put("pageSize",size);
        resultMap.put("pageNum",pageNum);
        sourceBuilder.from((pageNum-1)*size);
        sourceBuilder.size(size);

        //排序
        String sortField = searchMap.get("sortField");
        String sortRule = searchMap.get("sortRule");
        if(!StringUtils.isEmpty(sortField) && !StringUtils.isEmpty(sortRule)){
            sourceBuilder.sort(sortField,"ASC".equals(sortRule)? SortOrder.ASC:SortOrder.DESC);
        }else {
            sourceBuilder.sort("createTime",SortOrder.DESC);
        }

        //高亮查询
        HighlightBuilder highlightBuilder = new HighlightBuilder().field("title")
                .preTags("<font style='color:red'>")
                .postTags("</font>");
        sourceBuilder.highlighter(highlightBuilder);


        //品牌聚合查询
         sourceBuilder.aggregation(AggregationBuilders.terms("aggTmId").field("tmId")
                      .subAggregation(AggregationBuilders.terms("aggTmName").field("tmName"))
                      .subAggregation(AggregationBuilders.terms("aggTmLogoUrl").field("tmLogoUrl"))
                      .size(10000));
        //平台属性聚合查询
        sourceBuilder.aggregation(AggregationBuilders.nested("aggAttrs","attrs")
                                    .subAggregation(AggregationBuilders.terms("aggAttrId").field("attrs.attrId")
                                    .subAggregation(AggregationBuilders.terms("aggAttrName").field("attrs.attrName"))
                                    .subAggregation(AggregationBuilders.terms("aggAttrValue").field("attrs.attrValue"))
                                    .size(10000)
                                    ));

        //构建查询条件
        sourceBuilder.query(boolQueryBuilder);
        return new SearchRequest().indices("goods").types("info").source(sourceBuilder);

    }

    /**
     * 确定当前查询第几页的数据
     * @param pageNum
     * @return
     */
    private Integer getPageNum(String pageNum) {
        try {
            return Integer.parseInt(pageNum)>0?Integer.parseInt(pageNum):1;
        } catch (NumberFormatException e) {
            return 1;
        }

    }
}

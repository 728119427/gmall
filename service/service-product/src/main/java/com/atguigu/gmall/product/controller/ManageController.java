package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.constant.ProductConst;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.service.ManageService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "ManageController",tags = "分类与属性管理控制层")
@RestController
@RequestMapping("/admin/product")
public class ManageController {
    @Autowired
    private ManageService manageService;

    /**
     * 查询所有一级分类列表
     * @return
     */
    @ApiOperation("查询所有一级分类列表")
    @GetMapping("/getCategory1List")
    public Result<List<BaseCategory1>> getBaseCategory1List(){
        List<BaseCategory1> category1List = manageService.getCategory1List();
        return Result.ok(category1List);
    }

    /**
     * 根据一级分类id查询所有二级分类列表
     * @param category1Id
     * @return
     */
    @ApiOperation("根据一级分类id查询所有二级分类列表")
    @GetMapping("/getCategory2List/{category1Id}")
    public Result<List<BaseCategory2>> getBaseCategory2List(@PathVariable("category1Id") Long category1Id){
        List<BaseCategory2> category2List= manageService.getCategory2ListByCategory1Id(category1Id);
        return Result.ok(category2List);
    }

    /**
     * 根据二级分类id查询所有三级分类列表
     * @param category2Id
     * @return
     */
    @ApiOperation("根据二级分类id查询所有三级分类列表")
    @GetMapping("/getCategory3List/{category2Id}")
    public Result<List<BaseCategory3>> getBaseCategory3List(@PathVariable("category2Id") Long category2Id){
        List<BaseCategory3> category3List = manageService.getCategory3ListByCategory2Id(category2Id);
        return Result.ok(category3List);
    }

    @ApiOperation("根据分类查询所有规格信息列表")
    @GetMapping("/getBaseAttrInfoList/{category1Id}/{category2Id}/{category3Id}")
    public Result<List<BaseAttrInfo>> getBaseAttrInfoList(@PathVariable("category1Id") Long category1Id,
                                                          @PathVariable("category2Id") Long category2Id,
                                                          @PathVariable("category3Id") Long category3Id){
        List<BaseAttrInfo> baseAttrInfoList = manageService.getBaseAttrInfoListByCategoryId(category1Id, category2Id, category3Id);
        return Result.ok(baseAttrInfoList);

    }

    /**
     * 新增规格
     * @param baseAttrInfo
     * @return
     */
    @ApiOperation("新增规格")
    @PostMapping("/saveAttrInfo")
    public Result<BaseAttrInfo> saveAttrInfo(@RequestBody BaseAttrInfo baseAttrInfo){
        BaseAttrInfo baseAttrInfo1 = manageService.saveAttrInfo(baseAttrInfo);
        return Result.ok(baseAttrInfo1);
    }

    /**
     * 根据平台属性名称的id查询平台属性的值列表
     * @param attrInfoId
     * @return
     */
    @ApiOperation("根据平台属性名称的id查询平台属性的值列表")
    @GetMapping("/getAttrValueList/{id}")
    public Result<List<BaseAttrValue>> getAttrValueList(@PathVariable("id") Long attrInfoId){
        List<BaseAttrValue> attrValueList = manageService.getAttrValueList(attrInfoId);
        return Result.ok(attrValueList);
    }

    /**
     * 分页查询spuInfo的信息
     * @param category3Id
     * @param page
     * @param size
     * @return
     */
    @ApiOperation("分页查询spuInfo的信息")
    @GetMapping("/{page}/{size}")
    public Result<IPage<SpuInfo>> getSpuInfoList(@RequestParam("cid") Long category3Id,
                                                 @PathVariable("page") Integer page,
                                                 @PathVariable("size") Integer size){
        IPage<SpuInfo> spuInfoIPage = manageService.selectSpuInfoList(category3Id, page, size);
        return Result.ok(spuInfoIPage);
    }

    /**
     * 分页查询品牌信息
     * @param page
     * @param size
     * @return
     */
    @ApiOperation("分页查询品牌信息")
    @GetMapping("/baseTrademark/{page}/{size}")
    public Result<IPage<BaseTrademark>> getBaseTrademarkList(@PathVariable("page") Integer page,
                                                      @PathVariable("size") Integer size){
        IPage<BaseTrademark> baseTrademarkList = manageService.getBaseTrademarkPageList(page, size);
        return Result.ok(baseTrademarkList);
    }

    /**
     * 查询所有品牌信息
     * @return
     */
    @ApiOperation("查询所有品牌信息")
    @GetMapping("/baseTrademark/getTrademarkList")
    public Result<List<BaseTrademark>> trademarkList(){
        List<BaseTrademark> trademarkList = manageService.getTrademarkList();
        return Result.ok(trademarkList);
    }

    /**
     * 查询所有销售属性信息
     * @return
     */
    @ApiOperation("查询所有销售属性信息")
    @GetMapping("/baseSaleAttrList")
    public Result<List<BaseSaleAttr>> baseSaleAttrList(){
        List<BaseSaleAttr> baseSaleAttrList = manageService.getBaseSaleAttrList();
        return Result.ok(baseSaleAttrList);
    }

    /**
     * 新增或更新spu
     * @param spuInfo
     * @return
     */
    @ApiOperation("保存spuInfo")
    @PostMapping("/saveSpuInfo")
    public Result<SpuInfo> saveSpuInfo(@RequestBody SpuInfo spuInfo){
        SpuInfo saveSpuInfo = manageService.saveSpuInfo(spuInfo);
        return Result.ok(saveSpuInfo);
    }

    /**
     * 查询所有spu图片列表
     * @param spuId
     * @return
     */
    @ApiOperation("查询所有spu图片列表")
    @GetMapping("/spuImageList/{spuId}")
    public Result<List<SpuImage>> getSpuImageList(@PathVariable("spuId") Long spuId){
        List<SpuImage> imageList = manageService.getSpuImageListBySpuId(spuId);
        return Result.ok(imageList);
    }

    /**
     * 根据spuId查询销售属性列表
     * @param spuId
     * @return
     */
    @ApiOperation("根据spuId查询销售属性列表")
    @GetMapping("/spuSaleAttrList/{spuId}")
    public Result<List<SpuSaleAttr>> getSpuSaleAttrList(@PathVariable("spuId") Long spuId){
        List<SpuSaleAttr> spuSaleAttrListBySpuId = manageService.getSpuSaleAttrListBySpuId(spuId);
        return Result.ok(spuSaleAttrListBySpuId);
    }

    /**
     * 新增sku信息
     * @param skuInfo
     * @return
     */
    @ApiOperation("新增sku信息")
    @PostMapping("/saveSkuInfo")
    public Result<SkuInfo> saveSkuInfo(@RequestBody SkuInfo skuInfo){
        SkuInfo saveSkuInfo = manageService.saveSkuInfo(skuInfo);
        return Result.ok(saveSkuInfo);
    }

    /**
     * 分页查询sku列表信息
     * @param page
     * @param size
     * @return
     */
    @ApiOperation("分页查询sku列表信息")
    @GetMapping("/skuList/{page}/{size}")
    public Result<IPage<SkuInfo>> getSkuPageList(@PathVariable("page") Integer page,
                                                 @PathVariable("size") Integer size){
        IPage<SkuInfo> skuInfoIPage = manageService.skuInfoPageList(page, size);
        return Result.ok(skuInfoIPage);
    }

    /**
     * 上架商品
     * @param skuId
     * @return
     */
    @ApiOperation("上架商品")
    @PutMapping("/onSale/{id}")
    public Result onSale(@PathVariable("id") Long skuId){
        manageService.upOrDown(skuId, ProductConst.ON_SALE);
        return Result.ok();
    }

    /**
     * 下架商品
     * @param skuId
     * @return
     */
    @ApiOperation("下架商品")
    @PutMapping("/cancelSale/{id}")
    public Result cancelSale(@PathVariable("id") Long skuId){
        manageService.upOrDown(skuId, ProductConst.CANCEL_SALE);
        return Result.ok();
    }

}

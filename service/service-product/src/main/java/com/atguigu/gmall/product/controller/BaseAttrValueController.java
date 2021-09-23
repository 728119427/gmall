package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseAttrValue;
import com.atguigu.gmall.product.service.BaseAttrInfoService;
import com.atguigu.gmall.product.service.BaseAttrValueService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product/attrValue")
public class BaseAttrValueController {
    @Autowired
    private BaseAttrValueService baseAttrValueService;

    /**
     * 根据id查询产品属性值
     * @param id
     * @return
     */
    @GetMapping("/findById/{id}")
    public Result<BaseAttrValue> findById(@PathVariable("id") Long id){
        BaseAttrValue baseAttrValue = baseAttrValueService.getById(id);
        return Result.ok(baseAttrValue);
    }


    /**
     * 查询所有产品属性值
     * @return
     */
    @GetMapping("/findAll")
    public Result<List<BaseAttrValue>> findAll(){
        List<BaseAttrValue> list = baseAttrValueService.list(null);
        return Result.ok(list);
    }

    /**
     * 新增产品属性值
     * @param baseAttrValue
     * @return
     */
    @PostMapping("/insert")
    public Result insert(@RequestBody BaseAttrValue baseAttrValue){
        baseAttrValueService.save(baseAttrValue);
        return Result.ok();
    }

    /**
     * 更新产品属性值
     * @param baseAttrValue
     * @return
     */
    @PutMapping("/update")
    public Result update(@RequestBody BaseAttrValue baseAttrValue){
        baseAttrValueService.saveOrUpdate(baseAttrValue);
        return Result.ok();
    }

    /**
     * 根据id删除产品属性值
     * @param id
     * @return
     */
    @DeleteMapping("/del/{id}")
    public Result delete(@PathVariable("id") Long id){
        baseAttrValueService.removeById(id);
        return Result.ok();
    }

    /**
     * 根据条件查询
     * @param baseAttrValue
     * @return
     */
    @PostMapping("/findByCriteria")
    public Result<List<BaseAttrValue>> findByCriteria(@RequestBody BaseAttrValue baseAttrValue){
        List<BaseAttrValue> baseCategory1List=baseAttrValueService.findByCriteria(baseAttrValue);
        return Result.ok(baseCategory1List);
    }


    /**
     * 分页查询
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/page/{page}/{size}")
    public Result<IPage<BaseAttrValue>> selectPage(@PathVariable("page") Integer page,@PathVariable("size") Integer size){
        IPage<BaseAttrValue> iPage = baseAttrValueService.selectPage(page,size);
        return Result.ok(iPage);
    }

    @PostMapping("/findByCriteria/{page}/{size}")
    public Result<IPage<BaseAttrValue>> selectPageByCriteria(
            @PathVariable("page") Integer page,
            @PathVariable("size") Integer size,
            @RequestBody BaseAttrValue baseAttrValue){
        IPage<BaseAttrValue> iPage = baseAttrValueService.selectPageByCriteira(page,size,baseAttrValue);
        return Result.ok(iPage);

    }

}

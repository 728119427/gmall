package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.product.service.BaseAttrInfoService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product/attrInfo")
public class BaseAttrInfoController {
    @Autowired
    private BaseAttrInfoService baseAttrInfoService;

    /**
     * 根据id查询产品属性
     * @param id
     * @return
     */
    @GetMapping("/findById/{id}")
    public Result<BaseAttrInfo> findById(@PathVariable("id") Long id){
        BaseAttrInfo baseAttrInfo = baseAttrInfoService.getById(id);
        return Result.ok(baseAttrInfo);
    }


    /**
     * 查询所有产品属性
     * @return
     */
    @GetMapping("/findAll")
    public Result<List<BaseAttrInfo>> findAll(){
        List<BaseAttrInfo> list = baseAttrInfoService.list(null);
        return Result.ok(list);
    }

    /**
     * 新增产品属性
     * @param baseAttrInfo
     * @return
     */
    @PostMapping("/insert")
    public Result insert(@RequestBody BaseAttrInfo baseAttrInfo){
        baseAttrInfoService.save(baseAttrInfo);
        return Result.ok();
    }

    /**
     * 更新产品属性
     * @param baseAttrInfo
     * @return
     */
    @PutMapping("/update")
    public Result update(@RequestBody BaseAttrInfo baseAttrInfo){
        baseAttrInfoService.saveOrUpdate(baseAttrInfo);
        return Result.ok();
    }

    /**
     * 根据id删除产品属性
     * @param id
     * @return
     */
    @DeleteMapping("/del/{id}")
    public Result delete(@PathVariable("id") Long id){
        baseAttrInfoService.removeById(id);
        return Result.ok();
    }

    /**
     * 根据条件查询
     * @param baseAttrInfo
     * @return
     */
    @PostMapping("/findByCriteria")
    public Result<List<BaseAttrInfo>> findByCriteria(@RequestBody BaseAttrInfo baseAttrInfo){
        List<BaseAttrInfo> baseCategory1List=baseAttrInfoService.findByCriteria(baseAttrInfo);
        return Result.ok(baseCategory1List);
    }


    /**
     * 分页查询
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/page/{page}/{size}")
    public Result<IPage<BaseAttrInfo>> selectPage(@PathVariable("page") Integer page,@PathVariable("size") Integer size){
        IPage<BaseAttrInfo> iPage = baseAttrInfoService.selectPage(page,size);
        return Result.ok(iPage);
    }

    @PostMapping("/findByCriteria/{page}/{size}")
    public Result<IPage<BaseAttrInfo>> selectPageByCriteria(
            @PathVariable("page") Integer page,
            @PathVariable("size") Integer size,
            @RequestBody BaseAttrInfo baseAttrInfo){
        IPage<BaseAttrInfo> iPage = baseAttrInfoService.selectPageByCriteira(page,size,baseAttrInfo);
        return Result.ok(iPage);

    }

}

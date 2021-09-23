package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseCategory1;
import com.atguigu.gmall.product.service.BaseCategory1Service;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sun.misc.Cache;

import java.util.List;

@RestController
@RequestMapping("/api/product/category1")
public class BaseCategory1Controller {
    @Autowired
    private BaseCategory1Service baseCategory1Service;

    /**
     * 根据id查询一级分类
     * @param id
     * @return
     */
    @GetMapping("/findById/{id}")
    public Result<BaseCategory1> findById(@PathVariable("id") Long id){
        BaseCategory1 baseCategory1 = baseCategory1Service.getById(id);
        return Result.ok(baseCategory1);
    }


    /**
     * 查询所有一级分类
     * @return
     */
    @GetMapping("/findAll")
    public Result<List<BaseCategory1>> findAll(){
        List<BaseCategory1> list = baseCategory1Service.list(null);
        return Result.ok(list);
    }

    /**
     * 新增一级分类
     * @param baseCategory1
     * @return
     */
    @PostMapping("/insert")
    public Result insert(@RequestBody BaseCategory1 baseCategory1){
        baseCategory1Service.save(baseCategory1);
        return Result.ok();
    }

    /**
     * 更新一级分类
     * @param baseCategory1
     * @return
     */
    @PutMapping("/update")
    public Result update(@RequestBody BaseCategory1 baseCategory1){
        baseCategory1Service.saveOrUpdate(baseCategory1);
        return Result.ok();
    }

    /**
     * 根据id删除一级分类
     * @param id
     * @return
     */
    @DeleteMapping("/del/{id}")
    public Result delete(@PathVariable("id") Long id){
        baseCategory1Service.removeById(id);
        return Result.ok();
    }

    /**
     * 根据条件查询
     * @param baseCategory1
     * @return
     */
    @PostMapping("/findByCriteria")
    public Result<List<BaseCategory1>> findByCriteria(@RequestBody BaseCategory1 baseCategory1){
        List<BaseCategory1> baseCategory1List=baseCategory1Service.findByCriteria(baseCategory1);
        return Result.ok(baseCategory1List);
    }


    /**
     * 分页查询
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/page/{page}/{size}")
    public Result<IPage<BaseCategory1>> selectPage(@PathVariable("page") Integer page,@PathVariable("size") Integer size){
        IPage<BaseCategory1> iPage = baseCategory1Service.selectPage(page,size);
        return Result.ok(iPage);
    }

    @PostMapping("/findByCriteria/{page}/{size}")
    public Result<IPage<BaseCategory1>> selectPageByCriteria(
            @PathVariable("page") Integer page,
            @PathVariable("size") Integer size,
            @RequestBody BaseCategory1 baseCategory1){
        IPage<BaseCategory1> iPage = baseCategory1Service.selectPageByCriteira(page,size,baseCategory1);
        return Result.ok(iPage);

    }

}

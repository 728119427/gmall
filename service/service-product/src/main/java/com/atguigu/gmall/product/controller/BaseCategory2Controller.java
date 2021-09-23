package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseCategory2;
import com.atguigu.gmall.product.service.BaseCategory2Service;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product/category2")
public class BaseCategory2Controller {
    @Autowired
    private BaseCategory2Service baseCategory2Service;

    /**
     * 根据id查询二级分类
     * @param id
     * @return
     */
    @GetMapping("/findById/{id}")
    public Result<BaseCategory2> findById(@PathVariable("id") Long id){
        BaseCategory2 baseCategory2 = baseCategory2Service.getById(id);
        return Result.ok(baseCategory2);
    }


    /**
     * 查询所有二级分类
     * @return
     */
    @GetMapping("/findAll")
    public Result<List<BaseCategory2>> findAll(){
        List<BaseCategory2> list = baseCategory2Service.list(null);
        return Result.ok(list);
    }

    /**
     * 新增二级分类
     * @param baseCategory2
     * @return
     */
    @PostMapping("/insert")
    public Result insert(@RequestBody BaseCategory2 baseCategory2){
        baseCategory2Service.save(baseCategory2);
        return Result.ok();
    }

    /**
     * 更新二级分类
     * @param baseCategory2
     * @return
     */
    @PutMapping("/update")
    public Result update(@RequestBody BaseCategory2 baseCategory2){
        baseCategory2Service.saveOrUpdate(baseCategory2);
        return Result.ok();
    }

    /**
     * 根据id删除二级分类
     * @param id
     * @return
     */
    @DeleteMapping("/del/{id}")
    public Result delete(@PathVariable("id") Long id){
        baseCategory2Service.removeById(id);
        return Result.ok();
    }

    /**
     * 根据条件查询
     * @param baseCategory2
     * @return
     */
    @PostMapping("/findByCriteria")
    public Result<List<BaseCategory2>> findByCriteria(@RequestBody BaseCategory2 baseCategory2){
        List<BaseCategory2> baseCategory2List=baseCategory2Service.findByCriteria(baseCategory2);
        return Result.ok(baseCategory2List);
    }


    /**
     * 分页查询
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/page/{page}/{size}")
    public Result<IPage<BaseCategory2>> selectPage(@PathVariable("page") Integer page,@PathVariable("size") Integer size){
        IPage<BaseCategory2> iPage = baseCategory2Service.selectPage(page,size);
        return Result.ok(iPage);
    }

    /**
     * 分页条件查询
     * @param page
     * @param size
     * @param baseCategory2
     * @return
     */
    @PostMapping("/findByCriteria/{page}/{size}")
    public Result<IPage<BaseCategory2>> selectPageByCriteria(
            @PathVariable("page") Integer page,
            @PathVariable("size") Integer size,
            @RequestBody BaseCategory2 baseCategory2){
        IPage<BaseCategory2> iPage = baseCategory2Service.selectPageByCriteira(page,size,baseCategory2);
        return Result.ok(iPage);

    }

}

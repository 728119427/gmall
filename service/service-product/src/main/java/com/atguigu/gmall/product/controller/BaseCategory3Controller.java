package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseCategory3;
import com.atguigu.gmall.product.service.BaseCategory3Service;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product/category3")
public class BaseCategory3Controller {
    @Autowired
    private BaseCategory3Service baseCategory3Service;

    /**
     * 根据id查询三级分类
     * @param id
     * @return
     */
    @GetMapping("/findById/{id}")
    public Result<BaseCategory3> findById(@PathVariable("id") Long id){
        BaseCategory3 baseCategory3 = baseCategory3Service.getById(id);
        return Result.ok(baseCategory3);
    }


    /**
     * 查询所有三级分类
     * @return
     */
    @GetMapping("/findAll")
    public Result<List<BaseCategory3>> findAll(){
        List<BaseCategory3> list = baseCategory3Service.list(null);
        return Result.ok(list);
    }

    /**
     * 新增三级分类
     * @param baseCategory3
     * @return
     */
    @PostMapping("/insert")
    public Result insert(@RequestBody BaseCategory3 baseCategory3){
        baseCategory3Service.save(baseCategory3);
        return Result.ok();
    }

    /**
     * 更新三级分类
     * @param baseCategory3
     * @return
     */
    @PutMapping("/update")
    public Result update(@RequestBody BaseCategory3 baseCategory3){
        baseCategory3Service.saveOrUpdate(baseCategory3);
        return Result.ok();
    }

    /**
     * 根据id删除三级分类
     * @param id
     * @return
     */
    @DeleteMapping("/del/{id}")
    public Result delete(@PathVariable("id") Long id){
        baseCategory3Service.removeById(id);
        return Result.ok();
    }

    /**
     * 根据条件查询
     * @param baseCategory3
     * @return
     */
    @PostMapping("/findByCriteria")
    public Result<List<BaseCategory3>> findByCriteria(@RequestBody BaseCategory3 baseCategory3){
        List<BaseCategory3> baseCategory2List=baseCategory3Service.findByCriteria(baseCategory3);
        return Result.ok(baseCategory2List);
    }


    /**
     * 分页查询
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/page/{page}/{size}")
    public Result<IPage<BaseCategory3>> selectPage(@PathVariable("page") Integer page,@PathVariable("size") Integer size){
        IPage<BaseCategory3> iPage = baseCategory3Service.selectPage(page,size);
        return Result.ok(iPage);
    }

    /**
     * 分页条件查询
     * @param page
     * @param size
     * @param baseCategory3
     * @return
     */
    @PostMapping("/findByCriteria/{page}/{size}")
    public Result<IPage<BaseCategory3>> selectPageByCriteria(
            @PathVariable("page") Integer page,
            @PathVariable("size") Integer size,
            @RequestBody BaseCategory3 baseCategory3){
        IPage<BaseCategory3> iPage = baseCategory3Service.selectPageByCriteira(page,size,baseCategory3);
        return Result.ok(iPage);

    }

}

package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.product.service.BaseTrademarkService;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product/trademark")
public class BaseTrademarkController {
    @Autowired
    private BaseTrademarkService baseTrademarkService;

    /**
     * 根据id查询品牌标记
     * @param id
     * @return
     */
    @GetMapping("findById/{id}")
    public Result<BaseTrademark> findById(@PathVariable("id") Long id){
        BaseTrademark trademark = baseTrademarkService.getById(id);
        return Result.ok(trademark);
    }

    /**
     * 查询所有品牌标记
     * @return
     */
    @GetMapping("findAll")
    public Result<List<BaseTrademark>> findAll(){
        List<BaseTrademark> list = baseTrademarkService.list(null);
        return Result.ok(list);
    }

    /**
     * 新增品牌标记
     * @param baseTrademark
     * @return
     */
    @PostMapping("/insert")
    public Result insert(@RequestBody BaseTrademark baseTrademark){
        baseTrademarkService.save(baseTrademark);
        return Result.ok();
    }

    /**
     * 更新品牌标记
     * @param baseTrademark
     * @return
     */
    @PutMapping("/update")
    public Result update(@RequestBody BaseTrademark baseTrademark){
        baseTrademarkService.saveOrUpdate(baseTrademark);
        return Result.ok();
    }

    /**
     * 根据id删除
     * @param id
     * @return
     */
    @DeleteMapping("/del/{id}")
    public Result delete(@PathVariable("id") Long id){
        baseTrademarkService.removeById(id);
        return Result.ok();
    }

    /**
     * 根据条件查询
     * @param baseTrademark
     * @return
     */
    @PostMapping("/findByCriteria")
    public Result<List<BaseTrademark>> findByCriteria(@RequestBody BaseTrademark baseTrademark){
        List<BaseTrademark> baseTrademarks=baseTrademarkService.findByCriteria(baseTrademark);
        return Result.ok(baseTrademarks);
    }

    @GetMapping("/page/{page}/{size}")
    public Result<IPage<BaseTrademark>> selectPage(@PathVariable("page") Integer page,@PathVariable("size") Integer size){
        IPage<BaseTrademark> iPage = baseTrademarkService.selectPage(page,size);
        return Result.ok(iPage);
    }

    /**
     * 分页条件查询
     */
    @PostMapping("/findByCriteria/{page}/{size}")
    public Result<IPage<BaseTrademark>> selectPageByCriteria(
            @PathVariable("page") Integer page,
            @PathVariable("size") Integer size,
            @RequestBody BaseTrademark baseTrademark){
        IPage<BaseTrademark> iPage = baseTrademarkService.selectPageByCriteria(page,size,baseTrademark);
        return Result.ok(iPage);
    }

}

package com.offcn.seckill.controller;

import com.offcn.entity.PageResult;
import com.offcn.entity.Result;
import com.offcn.entity.StatusCode;
import com.offcn.seckill.pojo.SeckillGoods;
import com.offcn.seckill.service.SeckillGoodsService;
import com.offcn.seckill.utils.DateUtil;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/****
 * @Author:ujiuye
 * @Description:
 * @Date 2021/2/1 14:19
 *****/
@Api(tags = "SeckillGoodsController")
@RestController
@RequestMapping("/seckillGoods")
@CrossOrigin
public class SeckillGoodsController {

    @Autowired
    private SeckillGoodsService seckillGoodsService;

    /***
     * SeckillGoods分页条件搜索实现
     * @param seckillGoods
     * @param page
     * @param size
     * @return
     */
    @ApiOperation(value = "SeckillGoods条件分页查询",notes = "分页条件查询SeckillGoods方法详情",tags = {"SeckillGoodsController"})
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "page", value = "当前页", required = true),
            @ApiImplicitParam(paramType = "path", name = "size", value = "每页显示条数", required = true)
    })
    @PostMapping(value = "/search/{page}/{size}" )
    public Result<PageResult<SeckillGoods>> findPage(@RequestBody(required = false) @ApiParam(name = "SeckillGoods对象",value = "传入JSON数据",required = false) SeckillGoods seckillGoods, @PathVariable  int page, @PathVariable  int size){
        //调用SeckillGoodsService实现分页条件查询SeckillGoods
        PageResult<SeckillGoods> pageResult = seckillGoodsService.findPage(seckillGoods, page, size);
        return new Result(true,StatusCode.OK,"查询成功",pageResult);
    }

    /***
     * SeckillGoods分页搜索实现
     * @param page:当前页
     * @param size:每页显示多少条
     * @return
     */
    @ApiOperation(value = "SeckillGoods分页查询",notes = "分页查询SeckillGoods方法详情",tags = {"SeckillGoodsController"})
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "page", value = "当前页", required = true),
            @ApiImplicitParam(paramType = "path", name = "size", value = "每页显示条数", required = true)
    })
    @GetMapping(value = "/search/{page}/{size}" )
    public Result<PageResult<SeckillGoods>> findPage(@PathVariable  int page, @PathVariable  int size){
        //调用SeckillGoodsService实现分页查询SeckillGoods
        PageResult<SeckillGoods> pageResult = seckillGoodsService.findPage(page, size);
        return new Result<PageResult<SeckillGoods>>(true,StatusCode.OK,"查询成功",pageResult);
    }

    /***
     * 多条件搜索品牌数据
     * @param seckillGoods
     * @return
     */
    @ApiOperation(value = "SeckillGoods条件查询",notes = "条件查询SeckillGoods方法详情",tags = {"SeckillGoodsController"})
    @PostMapping(value = "/search" )
    public Result<List<SeckillGoods>> findList(@RequestBody(required = false) @ApiParam(name = "SeckillGoods对象",value = "传入JSON数据",required = false) SeckillGoods seckillGoods){
        //调用SeckillGoodsService实现条件查询SeckillGoods
        List<SeckillGoods> list = seckillGoodsService.findList(seckillGoods);
        return new Result<List<SeckillGoods>>(true,StatusCode.OK,"查询成功",list);
    }

    /***
     * 根据ID删除品牌数据
     * @param id
     * @return
     */
    @ApiOperation(value = "SeckillGoods根据ID删除",notes = "根据ID删除SeckillGoods方法详情",tags = {"SeckillGoodsController"})
    @ApiImplicitParam(paramType = "path", name = "id", value = "主键ID", required = true, dataType = "Long")
    @DeleteMapping(value = "/{id}" )
    public Result delete(@PathVariable Long id){
        //调用SeckillGoodsService实现根据主键删除
        seckillGoodsService.delete(id);
        return new Result(true,StatusCode.OK,"删除成功");
    }

    /***
     * 修改SeckillGoods数据
     * @param seckillGoods
     * @param id
     * @return
     */
    @ApiOperation(value = "SeckillGoods根据ID修改",notes = "根据ID修改SeckillGoods方法详情",tags = {"SeckillGoodsController"})
    @ApiImplicitParam(paramType = "path", name = "id", value = "主键ID", required = true, dataType = "Long")
    @PutMapping(value="/{id}")
    public Result update(@RequestBody @ApiParam(name = "SeckillGoods对象",value = "传入JSON数据",required = false) SeckillGoods seckillGoods,@PathVariable Long id){
        //设置主键值
        seckillGoods.setId(id);
        //调用SeckillGoodsService实现修改SeckillGoods
        seckillGoodsService.update(seckillGoods);
        return new Result(true,StatusCode.OK,"修改成功");
    }

    /***
     * 新增SeckillGoods数据
     * @param seckillGoods
     * @return
     */
    @ApiOperation(value = "SeckillGoods添加",notes = "添加SeckillGoods方法详情",tags = {"SeckillGoodsController"})
    @PostMapping
    public Result add(@RequestBody  @ApiParam(name = "SeckillGoods对象",value = "传入JSON数据",required = true) SeckillGoods seckillGoods){
        //调用SeckillGoodsService实现添加SeckillGoods
        seckillGoodsService.add(seckillGoods);
        return new Result(true,StatusCode.OK,"添加成功");
    }

    /***
     * 根据ID查询SeckillGoods数据
     * @param id
     * @return
     */
    @ApiOperation(value = "SeckillGoods根据ID查询",notes = "根据ID查询SeckillGoods方法详情",tags = {"SeckillGoodsController"})
    @ApiImplicitParam(paramType = "path", name = "id", value = "主键ID", required = true, dataType = "Long")
    @GetMapping("/{id}")
    public Result<SeckillGoods> findById(@PathVariable Long id){
        //调用SeckillGoodsService实现根据主键查询SeckillGoods
        SeckillGoods seckillGoods = seckillGoodsService.findById(id);
        return new Result<SeckillGoods>(true,StatusCode.OK,"查询成功",seckillGoods);
    }

    /***
     * 查询SeckillGoods全部数据
     * @return
     */
    @ApiOperation(value = "查询所有SeckillGoods",notes = "查询所SeckillGoods有方法详情",tags = {"SeckillGoodsController"})
    @GetMapping
    public Result<List<SeckillGoods>> findAll(){
        //调用SeckillGoodsService实现查询所有SeckillGoods
        List<SeckillGoods> list = seckillGoodsService.findAll();
        return new Result<List<SeckillGoods>>(true, StatusCode.OK,"查询成功",list) ;
    }

//    获取时间段
    @GetMapping("/getMenus")
    public Result<List<Date>> getMenus(){
        List<Date> dateList = DateUtil.getDateMenus();
        return new Result<List<Date>>(true,StatusCode.OK,"获取时间段成功！",dateList);
    }

//    获取指定时间段秒杀商品成功
    @GetMapping("/list")
    public Result<List<SeckillGoods>> list(String timeStr){
        List<SeckillGoods> list = seckillGoodsService.list(timeStr);
        if(list != null && list.size() > 0){
        return new Result<>(true,StatusCode.OK,"获取成功！",list);
        }
        return new Result<>(false,StatusCode.ERROR,"指定时间段没有秒杀商品");
    }

    @RequestMapping("/one")
    public SeckillGoods one(String time,Long id){
        return seckillGoodsService.one(time, id);
    }
}

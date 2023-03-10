package com.offcn.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.offcn.entity.PageResult;
import com.offcn.seckill.pojo.SeckillGoods;

import java.util.List;
/****
 * @Author:ujiuye
 * @Description:SeckillGoods业务层接口
 * @Date 2021/2/1 14:19
 *****/

public interface SeckillGoodsService extends IService<SeckillGoods> {

    /***
     * SeckillGoods多条件分页查询
     * @param seckillGoods
     * @param page
     * @param size
     * @return
     */
    PageResult<SeckillGoods> findPage(SeckillGoods seckillGoods, int page, int size);

    /***
     * SeckillGoods分页查询
     * @param page
     * @param size
     * @return
     */
    PageResult<SeckillGoods> findPage(int page, int size);

    /***
     * SeckillGoods多条件搜索方法
     * @param seckillGoods
     * @return
     */
    List<SeckillGoods> findList(SeckillGoods seckillGoods);

    /***
     * 删除SeckillGoods
     * @param id
     */
    void delete(Long id);

    /***
     * 修改SeckillGoods数据
     * @param seckillGoods
     */
    void update(SeckillGoods seckillGoods);

    /***
     * 新增SeckillGoods
     * @param seckillGoods
     */
    void add(SeckillGoods seckillGoods);

    /**
     * 根据ID查询SeckillGoods
     * @param id
     * @return
     */
     SeckillGoods findById(Long id);

    /***
     * 查询所有SeckillGoods
     * @return
     */
    List<SeckillGoods> findAll();

    //读取指定时间段，获取秒杀商品集合
    List<SeckillGoods> list(String timeStr);

    /**
     *
     * @param timeStr 时间段
     * @param id 商品编号
     * @return
     */
    SeckillGoods one (String timeStr,Long id);


}

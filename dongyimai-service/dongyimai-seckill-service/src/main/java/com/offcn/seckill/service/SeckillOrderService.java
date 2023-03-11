package com.offcn.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.offcn.entity.PageResult;
import com.offcn.seckill.pojo.SeckillOrder;
import com.offcn.seckill.entity.SeckillStatus;

import java.util.List;
/****
 * @Author:ujiuye
 * @Description:SeckillOrder业务层接口
 * @Date 2021/2/1 14:19
 *****/

public interface SeckillOrderService extends IService<SeckillOrder> {

    /***
     * SeckillOrder多条件分页查询
     * @param seckillOrder
     * @param page
     * @param size
     * @return
     */
    PageResult<SeckillOrder> findPage(SeckillOrder seckillOrder, int page, int size);

    /***
     * SeckillOrder分页查询
     * @param page
     * @param size
     * @return
     */
    PageResult<SeckillOrder> findPage(int page, int size);

    /***
     * SeckillOrder多条件搜索方法
     * @param seckillOrder
     * @return
     */
    List<SeckillOrder> findList(SeckillOrder seckillOrder);

    /***
     * 删除SeckillOrder
     * @param id
     */
    void delete(Long id);

    /***
     * 修改SeckillOrder数据
     * @param seckillOrder
     */
    void update(SeckillOrder seckillOrder);

    /***
     * 新增SeckillOrder
     * @param seckillOrder
     */
    void add(SeckillOrder seckillOrder);

    /**
     * 根据ID查询SeckillOrder
     * @param id
     * @return
     */
     SeckillOrder findById(Long id);

    /***
     * 查询所有SeckillOrder
     * @return
     */
    List<SeckillOrder> findAll();

    /**
     *
     * @param id 商品编号
     * @param time  时间段
     * @param username   购买用户名
     * @return
     */
    boolean add(Long id,String time, String username);

    /**
     * 抢单状态查询
     * @param username
     * @return
     */
    SeckillStatus queryStatus(String username);

    void updatePayStatus(String out_trade_no,String trade_no,String username);


    void closeOrder(String username);
}

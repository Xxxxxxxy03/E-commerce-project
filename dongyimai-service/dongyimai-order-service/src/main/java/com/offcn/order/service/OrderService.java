package com.offcn.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.offcn.entity.PageResult;
import com.offcn.order.pojo.Order;

import java.util.List;
/****
 * @Author:ujiuye
 * @Description:Order业务层接口
 * @Date 2021/2/1 14:19
 *****/

public interface OrderService extends IService<Order> {

    /***
     * Order多条件分页查询
     * @param order
     * @param page
     * @param size
     * @return
     */
    PageResult<Order> findPage(Order order, int page, int size);

    /***
     * Order分页查询
     * @param page
     * @param size
     * @return
     */
    PageResult<Order> findPage(int page, int size);

    /***
     * Order多条件搜索方法
     * @param order
     * @return
     */
    List<Order> findList(Order order);

    /***
     * 删除Order
     * @param id
     */
    void delete(Long id);

    /***
     * 修改Order数据
     * @param order
     */
    void update(Order order);

    /***
     * 新增Order
     * @param order
     */
    void add(Order order);

    /**
     * 根据ID查询Order
     * @param id
     * @return
     */
     Order findById(Long id);

    /***
     * 查询所有Order
     * @return
     */
    List<Order> findAll();

    /**
     * 修改订单状态
     *
     * @param out_trade_no   支付订单号
     * @param trade_no 支付宝返回的交易流水号
     */
     void updateStatus(String out_trade_no,String trade_no);
}

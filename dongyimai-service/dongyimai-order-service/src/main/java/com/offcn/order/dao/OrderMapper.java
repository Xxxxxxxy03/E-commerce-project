package com.offcn.order.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.offcn.order.pojo.Order;
import org.springframework.stereotype.Repository;

/****
 * @Author:ujiuye
 * @Description:Order的Dao
 * @Date 2021/2/1 14:19
 *****/
@Repository
public interface OrderMapper extends BaseMapper<Order> {
}

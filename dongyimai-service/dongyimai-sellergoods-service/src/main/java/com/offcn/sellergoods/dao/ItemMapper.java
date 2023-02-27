package com.offcn.sellergoods.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.offcn.order.pojo.OrderItem;
import com.offcn.sellergoods.pojo.Item;
import org.apache.ibatis.annotations.Update;

/****
 * @Author:ujiuye
 * @Description:Item的Dao
 * @Date 2021/2/1 14:19
 *****/
public interface ItemMapper extends BaseMapper<Item> {

//    定义一个减少指定ksu编号商品库存
    @Update("update tb_item set num = num - #{num} where id = #{itemId} and num >= #{num}" )
    public int decrCount(OrderItem orderItem);
}

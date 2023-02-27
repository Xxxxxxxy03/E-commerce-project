package com.offcn.order.service;

import com.offcn.order.group.Cart;

import java.util.List;

public interface CartService {
    /**
     * 添加商品到购物车
     * @param cartList  现有购物车集合
     * @param itemId  添加到购物车商品sku编号
     * @param num    购买数量
     * @return    新购物车集合数据
     */
    public List<Cart> addGoodsToCartList(List<Cart> cartList,Long itemId,Integer num);

    /**
     * 从redis中读取指定用户购物车数据
     * @param username
     * @return
     */
    public List<Cart> findCartListFromRedis(String username);

    /**
     * 将购物车保存到redis缓存
     * @param username  用户账号
     * @param cartList  当前用户购物车集合
     */
    public void saveCartListToRedis(String username,List<Cart> cartList);

}

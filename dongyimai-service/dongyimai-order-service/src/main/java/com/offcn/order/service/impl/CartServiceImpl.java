package com.offcn.order.service.impl;

import com.offcn.entity.Result;
import com.offcn.order.group.Cart;
import com.offcn.order.pojo.OrderItem;
import com.offcn.order.service.CartService;
import com.offcn.sellergoods.feign.ItemFeign;
import com.offcn.sellergoods.pojo.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    //注入sku feign接口
    @Autowired
    private ItemFeign itemFeign;

    //注入redis模板操作工具对象
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {

        //调用itemFeign获取sku信息
        Result<Item> itemResult = itemFeign.findById(itemId);
        if (itemResult != null && itemResult.isFlag()) {
            //获取sku数据
            Item item = itemResult.getData();
            //判断sku对象是否为空
            if (item == null) {
                throw new RuntimeException("商品不存在");
            }
            //判断商品状态
            if (!item.getStatus().equals("1")) {
                throw new RuntimeException("商品状态无效");
            }

            //从快速对象获取商品对应商家id
            String sellerId = item.getSellerId();

            //根据商家id判断购物车列表是否存在该商家的购物车对象
            Cart cart = searchCartListBySellerID(cartList, sellerId);

            //如果购物车列表不存在该商家的购物车
            if (cart == null) {
                //要添加到购物车的商品不存在对应的购物车对象
                //新建购物车对象
                cart = new Cart();
                //设置购物车属性
                cart.setSellerId(sellerId);
                cart.setSellerName(item.getSeller());

                //创建购物明细对象
                OrderItem orderItem = createOrderItem(item, num);

                //创建集合存储购物明细集合
                List orderItemList = new ArrayList<>();
                //把创建好购物明细对象，加入到购物明细集合
                orderItemList.add(orderItem);
                //购物明细集合关联到购物车对象
                cart.setOrderItemList(orderItemList);

                //将购物车对象添加到购物车列表集合
                cartList.add(cart);
            } else {
                //如果购物车列表中存在该商家的购物车，判断购物车明细列表中是否存在该商品
                OrderItem orderItem = searchOrderItemByItemId(cart.getOrderItemList(), itemId);

                //判断是否为空
                if (orderItem == null) {
                    //如果没有，新增购物车明细
                    orderItem = createOrderItem(item, num);
                    cart.getOrderItemList().add(orderItem);
                } else {
                    //如果有，在原购物车而明细上添加数量，更改金额
                    orderItem.setNum(orderItem.getNum() + num);
                    orderItem.setTotalFee(orderItem.getPrice().multiply(new BigDecimal(orderItem.getNum())));

                    //如果数量操作小于等于0，则移除
                    if (orderItem.getNum() == 0) {
                        cart.getOrderItemList().remove(orderItem);//移除购物车明细
                    }
                    //如果移除后cart的明细数量为0，则将cart移除
                    if (cart.getOrderItemList().size() == 0) {
                        cartList.remove(cart);
                    }
                }
            }
        }
        return cartList;
    }


    /**
     * 从redis中查询购物车
     *
     * @param username
     * @return
     */
    @Override
    public List<Cart> findCartListFromRedis(String username) {
        System.out.println("从redis中提取购物车数据....." + username);
        //从redis读取指定username的购物车数据
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(username);

        if (cartList == null) {
            //返回一个空的list集合
            cartList = new ArrayList();
        }
        return cartList;

    }

    /**
     * 将购物车保存到redis
     *
     * @param username 用户账号
     * @param cartList 当前购物车集合
     */
    @Override
    public void saveCartListToRedis(String username, List<Cart> cartList) {
        System.out.println("向redis存入购物车数据....." + username);
        redisTemplate.boundHashOps("cartList").put(username, cartList);

    }

    /**
     * 根据商品明细ID查询
     *
     * @param orderItemList
     * @param itemId
     * @return
     */
    private OrderItem searchOrderItemByItemId(List<OrderItem> orderItemList, Long itemId) {

        //遍历购物明细集合
        for (OrderItem orderItem : orderItemList) {
            //比对商品编号和指定skuid是否想同
            if (orderItem.getItemId().longValue() == itemId.longValue()) {
                //返回当前购物明细对象
                return orderItem;
            }
        }
        return null;
    }


    /**
     * 根据商家ID查询购物车对象
     *
     * @param cartList
     * @param sellerId
     * @return
     */
    private Cart searchCartListBySellerID(List<Cart> cartList, String sellerId) {
       //遍历现有购物车集合
        for (Cart cart : cartList) {
            //比对购物车对象的商家编号是否一致
            if (cart.getSellerId().equals(sellerId)) {
                return cart;
            }
        }
        return null;
    }


    /**
     * 创建订单明细
     *
     * @param item
     * @param num
     * @return
     */
    private OrderItem createOrderItem(Item item, Integer num) {
        //判断购买数量是否小于等于0
        if (num < 0) {
            throw new RuntimeException("购买数量非法");
        }
        //创建购物明细对象
        OrderItem orderItem = new OrderItem();
        //设置spu编号
        orderItem.setGoodsId(item.getGoodsId());
        //设置sku编号
        orderItem.setItemId(item.getId());
        //设置购买数量
        orderItem.setNum(num);
        //设置商品配图
        orderItem.setPicPath(item.getImage());
        //商品价格
        orderItem.setPrice(item.getPrice());
        //商家编号
        orderItem.setSellerId(item.getSellerId());
        //标题
        orderItem.setTitle(item.getTitle());
        //计算商品购买要支付总金额   num*price
        orderItem.setTotalFee(orderItem.getPrice().multiply(new BigDecimal(num)));
        return orderItem;
    }
}

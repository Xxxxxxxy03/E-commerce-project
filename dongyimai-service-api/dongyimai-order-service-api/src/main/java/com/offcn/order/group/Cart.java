package com.offcn.order.group;

import com.offcn.order.pojo.OrderItem;

import java.io.Serializable;
import java.util.List;

public class Cart implements Serializable {
    //商家id
    private String sellerId;

    //商家名称
    private String sellerName;

    //购物车明细集合
    private List<OrderItem> orderItemList;

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public List<OrderItem> getOrderItemList() {
        return orderItemList;
    }

    public void setOrderItemList(List<OrderItem> orderItemList) {
        this.orderItemList = orderItemList;
    }
}

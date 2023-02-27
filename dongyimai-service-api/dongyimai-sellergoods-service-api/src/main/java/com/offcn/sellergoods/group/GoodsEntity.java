package com.offcn.sellergoods.group;

import com.offcn.sellergoods.pojo.Goods;
import com.offcn.sellergoods.pojo.GoodsDesc;
import com.offcn.sellergoods.pojo.Item;
import com.offcn.sellergoods.pojo.ItemCat;

import java.io.Serializable;
import java.util.List;

public class GoodsEntity implements Serializable {
    //商品基本信息对象
    private Goods goods;

    //商品扩展信息对象
    private GoodsDesc goodsDesc;

    //商品sku数据集合
    private List<Item> itemList;

    public Goods getGoods() {
        return goods;
    }

    public void setGoods(Goods goods) {
        this.goods = goods;
    }

    public GoodsDesc getGoodsDesc() {
        return goodsDesc;
    }

    public void setGoodsDesc(GoodsDesc goodsDesc) {
        this.goodsDesc = goodsDesc;
    }

    public List<Item> getItemList() {
        return itemList;
    }

    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
    }
}

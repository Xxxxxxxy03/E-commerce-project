package com.offcn.item.service.impl;

import com.alibaba.fastjson.JSON;
import com.offcn.entity.Result;
import com.offcn.item.service.PageService;
import com.offcn.sellergoods.feign.GoodsFeign;
import com.offcn.sellergoods.feign.ItemCatFeign;
import com.offcn.sellergoods.group.GoodsEntity;
import com.offcn.sellergoods.pojo.Goods;
import com.offcn.sellergoods.pojo.GoodsDesc;
import com.offcn.sellergoods.pojo.Item;
import com.offcn.sellergoods.pojo.ItemCat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PageServiceImpl implements PageService {
    //注入获取商品数据feign接口
    @Autowired
    private GoodsFeign goodsFeign;
    //注入分类feign
    @Autowired
    private ItemCatFeign itemCatFeign;
    //注入模板引擎对象
    @Autowired
    private TemplateEngine templateEngine;

    //生成静态文件路径
    @Value("${pagepath}")
    private String pagepath;



    private Map<String,Object> buildDataModel(Long spuId){
        //创建返回的一个map
        HashMap<String, Object> dataMap = new HashMap<>();
        Result<GoodsEntity> goodsEntityResult = goodsFeign.findById(spuId);
        if(goodsEntityResult != null &&goodsEntityResult.isFlag()){
            //获取响应数据
            GoodsEntity goodsEntity = goodsEntityResult.getData();
            //获取商品基本信息
            Goods goods = goodsEntity.getGoods();
            dataMap.put("goods",goods);
            //获取商品扩展信息
            GoodsDesc goodsDesc = goodsEntity.getGoodsDesc();
            dataMap.put("goodsDesc",goodsDesc);
            //获取sku数据集合
            List<Item> itemList = goodsEntity.getItemList();
            dataMap.put("itemList",itemList);
            //获取商品配图数据集合,需要把配图json转换解析成集合
            List<Map> imageList = JSON.parseArray(goodsDesc.getItemImages(), Map.class);
            dataMap.put("imageList",imageList);
            //获取商品全部规格信息
            List<Map> specificationList = JSON.parseArray(goodsDesc.getSpecificationItems(), Map.class);
            ItemCat itemCat1 = itemCatFeign.findById(goods.getCategory1Id().intValue()).getData();
            ItemCat itemCat2 = itemCatFeign.findById(goods.getCategory2Id().intValue()).getData();
            ItemCat itemCat3 = itemCatFeign.findById(goods.getCategory3Id().intValue()).getData();
            dataMap.put("specificationList",specificationList);
            dataMap.put("category1",itemCat1);
            dataMap.put("category2",itemCat2);
            dataMap.put("category3",itemCat3);
        }
        return dataMap;
    }

    //   定义生成静态页面方法
    @Override
    public void createPageHtml(Long spuId) {
    //创建模板引擎上下文对象
        Context context = new Context();
        Map<String, Object> dataModel = this.buildDataModel(spuId);
        //关联数据到上下文对象
        context.setVariables(dataModel);
        File dir = new File(pagepath);
        //判断目录是否为空
        if(!dir.exists()){
            //不存在创建
            dir.mkdir();
        }

        //在该目录。指定要生成文件
        File destFile = new File(dir, spuId + ".html");
        PrintWriter printWriter = null;
        //把文件对象封装到流对象  打印流
        try {
            printWriter = new PrintWriter(destFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //调用模板引擎，执行渲染
        templateEngine.process("item",context,printWriter);
        System.out.println("建立成功！");
    }
}
package com.offcn.canal.listener;


import com.alibaba.fastjson.JSON;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.offcn.content.feign.ContentFeign;
import com.offcn.content.pojo.Content;
import com.offcn.entity.Result;
import com.offcn.item.feign.PageFeign;
import com.xpand.starter.canal.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;

import java.util.List;



@CanalEventListener
public class CanalDataEventListener {
    //注入feign接口
    @Autowired
    private ContentFeign contentFeign;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    //注入页面生成feign接口
    @Autowired
    private PageFeign pageFeign;

    //监听新增
//    @InsertListenPoint
    public void onEventInsert(CanalEntry.EventType eventType, CanalEntry.RowData rowData){
        //获取新增的数据
        for (CanalEntry.Column column : rowData.getAfterColumnsList()) {
            System.out.println("列名："+ column.getName() +"值："+column.getValue());
        }

    }

    //监听修改
//    @UpdateListenPoint
    public void onEventUpdate(CanalEntry.EventType eventType, CanalEntry.RowData rowData){
        //获取修改的数据
        for (CanalEntry.Column column : rowData.getAfterColumnsList()) {
            System.out.println("列名："+ column.getName() +"值："+column.getValue());
        }
    }

    //监听修改
//    @DeleteListenPoint
    public void onEventDelete(CanalEntry.EventType eventType, CanalEntry.RowData rowData){
        //获取删除的数据
        for (CanalEntry.Column column : rowData.getBeforeColumnsList()) {
            System.out.println("列名："+ column.getName() +"值："+column.getValue());
        }
    }
    //自定义数据修改监听
    @ListenPoint(destination = "example",schema = "dongyimaidb",table={"tb_content"},eventType = {CanalEntry.EventType.INSERT,CanalEntry.EventType.UPDATE,CanalEntry.EventType.DELETE})
        public void customEvent(CanalEntry.EventType eventType,CanalEntry.RowData rowData){

        //1.获取列名 为category_id的值
        String categoryId = getColumnValue(eventType, rowData);
        //2.调用feign 获取该分类下的所有的广告集合
        Result<List<Content>> result = contentFeign.findByCategoryId(Long.valueOf(categoryId));
        //判断result是否为空
        if(result!=null&&result.isFlag()){
            //获取封装的返回数据
            List<Content> data = result.getData();
            //3.使用redisTemplate存储到redis中
            stringRedisTemplate.boundValueOps("content_" + categoryId).set(JSON.toJSONString(data));
        }
        }


    //定义一个方法，把新增或修改或删除所影响到的categoryId获取到
    private String getColumnValue(CanalEntry.EventType eventType,CanalEntry.RowData rowData) {
        //定义一个广告分类编号
        String categoryId = "";
        if (eventType.getNumber() == CanalEntry.EventType.INSERT.getNumber() || eventType.getNumber() == CanalEntry.EventType.UPDATE.getNumber()) {
            System.out.println("发现新增操作");
            for (CanalEntry.Column column : rowData.getAfterColumnsList()) {
//                System.out.println("列名："+ column.getName() +"值："+column.getValue());
                //判断列名是否等于category_id
                if (column.getName().toLowerCase().equals("category_id")) {
                    //获取到值
                    categoryId = column.getValue();
                }
            }
        } else if (eventType.getNumber() == CanalEntry.EventType.DELETE.getNumber()) {
            System.out.println("发现删除操作");
            for (CanalEntry.Column column : rowData.getBeforeColumnsList()) {
                //判断列名是否等于category_id
                if (column.getName().toLowerCase().equals("category_id")) {
                    //获取到值
                    categoryId = column.getValue();
                }
            }
        }
        return categoryId;
    }

    //定义一个监听商品表方法，调用生成静态页面服务
    @ListenPoint(destination = "example",
            schema = "dongyimaidb",
            table = {"tb_goods"},
            eventType = {CanalEntry.EventType.UPDATE, CanalEntry.EventType.INSERT, CanalEntry.EventType.DELETE})
    public void onEventCustomSpu(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {

        //判断事件类型
        if (eventType.getNumber() == CanalEntry.EventType.UPDATE.getNumber()) {
            String goodsId = "";
            String audit_status = "";
            List<CanalEntry.Column> beforeColumnsList = rowData.getBeforeColumnsList();
            for (CanalEntry.Column column : beforeColumnsList) {
                //判断列名是否是id
                if (column.getName().toLowerCase().equals("id")) {
                    goodsId = column.getValue();//goodsId
                    break;
                }
                if(column.getName().toLowerCase().equals("audit_status")){
                    audit_status = column.getValue();
                }
            }
            //判断是否为空
            if(!StringUtils.isEmpty(goodsId)){
               if(audit_status.equals("1")){
                   try {
                       pageFeign.createHtml(Long.parseLong(goodsId));
                       System.out.println("监听到修改商品事件，静态页面生成成功！");
                   } catch (NumberFormatException e) {
                       e.printStackTrace();
                   }
               }
            }
        }else{
            System.out.println("事件类型不支持");
        }
    }
}

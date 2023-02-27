package com.offcn.sellergoods.feign;

import com.offcn.entity.Result;
import com.offcn.sellergoods.pojo.Item;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "dym-sellergoods",path="item")
public interface ItemFeign {

    @GetMapping("/findByStatus/{status}")
    public Result<List<Item>> findByStatus(@PathVariable("status") String status);


    //根据sku编号，获取sku详细数据
    @GetMapping("/{id}")
    public Result<Item> findById(@PathVariable Long id);

    @PostMapping("/decr/count")
    public Result decrCount(@RequestParam("username") String username);
}

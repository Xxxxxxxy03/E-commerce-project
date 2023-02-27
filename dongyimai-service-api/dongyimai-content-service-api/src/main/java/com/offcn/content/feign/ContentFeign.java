package com.offcn.content.feign;

import com.offcn.content.pojo.Content;
import com.offcn.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(value = "CONTENT",path = "content")
public interface ContentFeign {

    //调用远程服务，读取指定分类广告数据集合
    @GetMapping("findByCategoryId/{id}")
    public Result<List<Content>> findByCategoryId(@PathVariable("id") Long id);
}

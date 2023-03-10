package com.offcn.sellergoods.feign;

import com.offcn.entity.Result;
import com.offcn.sellergoods.group.GoodsEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value="dym-sellergoods",path = "goods")
public interface GoodsFeign {
    /***
     * 根据ID查询Spu数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<GoodsEntity> findById(@PathVariable Long id);
}
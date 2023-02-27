package com.offcn.feign;

import com.offcn.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(value="search",path = "search")
public interface SearchSkuFeign {
    @PostMapping("/")
    public Result<Map> search(@RequestBody(required = false) Map map);
}

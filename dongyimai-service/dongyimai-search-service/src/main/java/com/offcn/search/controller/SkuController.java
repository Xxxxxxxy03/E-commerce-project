package com.offcn.search.controller;

import com.offcn.entity.Page;
import com.offcn.entity.Result;
import com.offcn.entity.StatusCode;
import com.offcn.pojo.SkuInfo;
import com.offcn.search.service.SkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/search")
public class SkuController {
    @Autowired
    private SkuService service;

    @GetMapping("/import")
    public Result importSku(){
        service.importSku();
        return new Result(true, StatusCode.OK,"成功！");
    }

    @PostMapping("/")
    public Result<Map> search(@RequestBody(required = false) Map map){
        Map searchMap = service.search(map);
        //创建一个分页对象  获取当前页 和总记录数及显示的页码
        Page<SkuInfo> infoPage = new Page<>(Long.valueOf(searchMap.get("total").toString()), Integer.valueOf(searchMap.get("pageNum").toString()), Integer.valueOf(searchMap.get("pageSize").toString()));
        searchMap.put("page",infoPage);
        return new Result(true, StatusCode.OK,"查询成功！",searchMap);
    }
}

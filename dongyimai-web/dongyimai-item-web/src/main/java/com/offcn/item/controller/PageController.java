package com.offcn.item.controller;

import com.offcn.entity.Result;
import com.offcn.entity.StatusCode;
import com.offcn.item.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/page")
public class PageController {
    @Autowired
    private PageService pageService;
   @GetMapping("/createHtml/{id}")
    public Result createPageHtml(@PathVariable Long id) {
        pageService.createPageHtml(id);
        return new Result(true, StatusCode.OK, "成功");
    }
}

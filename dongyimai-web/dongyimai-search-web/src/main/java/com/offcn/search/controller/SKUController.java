package com.offcn.search.controller;

import com.offcn.entity.Result;
import com.offcn.feign.SearchSkuFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
@RequestMapping("search")
public class SKUController {

    //注入搜索服务的Feign接口
    @Autowired
    private SearchSkuFeign searchSkuFeign;

    //搜索调用方法
    @GetMapping("list")
    public String search(@RequestParam(required = false) Map searchMap, Model model){
        //调用搜索服务feign接口，发出查询
        Result<Map> result = searchSkuFeign.search(searchMap);
        //判断result是否为空，状态是否成功
        if(result!=null&& result.isFlag()){
            //获取返回的搜索结果数据
            Map map = result.getData();
            //把获取到搜索结果数据封装到model
            model.addAttribute("result",map);

            //把查询条件数据searchMap封装model
            model.addAttribute("searchMap",searchMap);

            //把搜索条件转换成拼接参数的url地址
            String url = this.setUrl(searchMap);
            //把获取到url参数，封装到model
            model.addAttribute("url",url);
        }

        //调整到搜索结果模板
        return "search";
    }

    //定义一个方法：把searchMap封装的搜索条件，拼接到url作为查询条件
    //t?keywords=电视&category=平板电视&brand=三星&
    private String setUrl(Map<String, String> searchMap){

        //定义一个url变量，赋予初始化值，就是搜索接口路径
        String url="/search/list";
        //判断searchMap是否为空
        if(searchMap!=null&&searchMap.size()>0){
            //首次现有加一个？
            url+="?";
            //遍历searchMap
            for (Map.Entry<String, String> entry : searchMap.entrySet()) {
                //获取key
                String key = entry.getKey();
                //获取value
                String value = entry.getValue();
                //把获取到key和value拼接到url
                url+=key+"="+value+"&";
            }
        }
        //判断最后是否是&号，移除
        if(url.lastIndexOf("&")!=-1){
            //把最后&去除
            url=url.substring(0,url.lastIndexOf("&"));
        }
        return url;
    }
}

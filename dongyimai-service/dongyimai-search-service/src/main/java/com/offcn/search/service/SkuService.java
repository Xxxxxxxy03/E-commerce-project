package com.offcn.search.service;

import java.util.Map;

public interface SkuService {
    //数据导入
    void importSku();

    //搜索方法
    Map search(Map<String,String> searchMap);
}

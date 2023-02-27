package com.offcn.search.dao;

import com.offcn.pojo.SkuInfo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface SkuMapper extends ElasticsearchRepository<SkuInfo,Long> {
}

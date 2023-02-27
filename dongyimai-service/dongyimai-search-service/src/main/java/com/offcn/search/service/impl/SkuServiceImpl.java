package com.offcn.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.offcn.entity.Result;
import com.offcn.pojo.SkuInfo;
import com.offcn.search.dao.SkuMapper;


import com.offcn.search.service.SkuService;
import com.offcn.sellergoods.feign.ItemFeign;
import com.offcn.sellergoods.pojo.Item;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.*;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class SkuServiceImpl implements SkuService {

    //把es数据操作接口注入
    @Autowired
    private SkuMapper skuMapper;

    //注入一个Spring data Es提供的一个es查询工具对象
    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    //注入itemFeign获取sku数据集合feign接口
    @Autowired
    private ItemFeign itemFeign;

    @Override
    public void importSku() {

        //调用feign接口，获取最新的sku数据集合
        Result<List<Item>> result = itemFeign.findByStatus("1");
        //判断结果是否为true
        if(result!=null&&result.isFlag()){
            //获取响应数据
            List<Item> itemList = result.getData();

            //把读取到数据集合转换成json字符串
            List<SkuInfo> skuInfoList = JSON.parseArray(JSON.toJSONString(itemList), SkuInfo.class);

            //遍历集合
            for (SkuInfo skuInfo : skuInfoList) {
                //获取规格数据：json字符串
                String specJsonStr = skuInfo.getSpec();
                //把规格json字符串解析转换成Map
                Map<String, Object> specMap = JSON.parseObject(specJsonStr, Map.class);
                //把specMap关联设置到skuInfo属性
                skuInfo.setSpecMap(specMap);
            }

            //保存sku集合数据到es
            skuMapper.saveAll(skuInfoList);
            //先导入一条数据,自动建立映射.
//            skuMapper.save(skuInfoList.get(0));
            System.out.println("导入数据到ES成功");
        }else {
            System.out.println("获取sku数据集合失败");
        }
    }

    @Override
    public Map search(Map<String, String> searchMap) {

        //创建一个返回的Map对象
        Map resultMap=new HashMap<>();

        //从searchMAP获取查询关键字
        String keywords = searchMap.get("keywords");
        //判断查询关键字是否为空
        if(StringUtils.isEmpty(keywords)){
            //设置一个默认查询关键字
            keywords="华为";
        }
        //创建查询条件封装构建器对象
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();

        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("title", keywords);

        //设置多关键词搜索
//        nativeSearchQueryBuilder.withQuery(QueryBuilders.multiMatchQuery(keywords,"title","brand","category"));//把设置查询条件关联到查询条件封装对象
        nativeSearchQueryBuilder.withQuery(matchQueryBuilder);

        //设置按照商品分类来进行分组
        TermsAggregationBuilder categoryAggregationBuilder = AggregationBuilders.terms("skuCategorygroup").field("category.keyword").size(100);

        //设置按照品牌来进行分组
        TermsAggregationBuilder brandAggregationBuilder  = AggregationBuilders.terms("skuBrandGroup").field("brand.keyword").size(100);

        //设置按照规格来进行分组
        TermsAggregationBuilder specAggregationBuilder= AggregationBuilders.terms("skuSpecGroup").field("spec.keyword").size(200);
        //关联到查询条件封装对象
        nativeSearchQueryBuilder.addAggregation(categoryAggregationBuilder);
        //关联品牌分组条件到查询条件构建器对象
        nativeSearchQueryBuilder.addAggregation(brandAggregationBuilder);
        //关联规格分组条件到封装对象
        nativeSearchQueryBuilder.addAggregation(specAggregationBuilder);

        //创建bool查询器对象
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //判断前端传递的分类查询条件是否为空
        if(!StringUtils.isEmpty(searchMap.get("category"))){
            //创建按照分类进行过滤条件
            boolQueryBuilder.filter(QueryBuilders.termQuery("category.keyword",searchMap.get("category")));
        }
        //判断前端传递品牌查询条件是否为空
        if(!StringUtils.isEmpty(searchMap.get("brand"))){
            //创建安装品牌进行过滤
            boolQueryBuilder.filter(QueryBuilders.termQuery("brand.keyword",searchMap.get("brand")));
        }


        //设置规格和规格选项过滤条件
        //判断搜索条件封装searchMap是否为空
        if(searchMap != null){
            //遍历searchMap
            for (String key : searchMap.keySet()) {
                //判断key是否剩余规格条件
                if(key.startsWith("spec_")){
                    //设置按照规格和选项进行过滤
                    boolQueryBuilder.filter(QueryBuilders.termQuery("specMap."+key.substring(5)+".keyword",searchMap.get(key)));
                }
            }
        }

        //获取价格区间字符串  1-500  500-1000   3000-*
        String priceStr = searchMap.get("price");
        //判断价格查询条件是否为空
        if(!StringUtils.isEmpty(priceStr)){
            //使用 -切开字符串
            String[] split = priceStr.split("-");
            //判断结束价格是否等于*
            if(split[1].equals("*")){
                //只需要设置价格下限 大于指定价格
                boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").from(split[0],true));//设置开始价格 true就是包含
            }else {
                //需要设置开始价格  最大价格
                boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").from(split[0],true).to(split[1],false));//设置结束价格  false就是不包含
            }
        }

        //获取分页相关参数1：页码 pageNum
        //定义一个页码变量
        Integer pageNum=1;

        if(!StringUtils.isEmpty(searchMap.get("pageNum"))){
            try {
                //把获取到值设置给pageNum
                pageNum=Integer.parseInt(searchMap.get("pageNum"));
            } catch (NumberFormatException e) {
                e.printStackTrace();
                pageNum=1;
            }
        }
        //获取分页相关参数2：每页显示记录数 pageSize
        Integer pageSize=10;
        if(!StringUtils.isEmpty(searchMap.get("pageSize"))){
            try {
                pageSize=Integer.parseInt(searchMap.get("pageSize"));
            } catch (NumberFormatException e) {
                e.printStackTrace();
                pageSize=10;
            }
        }

        //设置分页参数到查询条件构建器对象
        nativeSearchQueryBuilder.withPageable(PageRequest.of(pageNum-1,pageSize));


        //接收前端传递过来排序相关参数

        //排序参数1：排序的字段名称 sortField
        //排序参数2：排序方式 sortRule 升序 ASC  降序  DESC
        String sortField = searchMap.get("sortField");
        String sortRule = searchMap.get("sortRule");

        //判断前端是否传递排序相关参数
        if(!StringUtils.isEmpty(sortField)&&!StringUtils.isEmpty(sortRule)){
            //设置排序条件，到主查询器构建器对象
            nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort(sortField).order(sortRule.equals("ASC")? SortOrder.ASC:SortOrder.DESC));
        }

        //设置高亮参数

        //设置要处理高亮字段名称
        nativeSearchQueryBuilder.withHighlightFields(new HighlightBuilder.Field("title"));
        //设置高亮前后缀参数
        nativeSearchQueryBuilder.withHighlightBuilder(new HighlightBuilder().preTags("<span style='color:red'>").postTags("</span>"));



        //把bool查询对象，关联到主查询器构建器对象
        nativeSearchQueryBuilder.withFilter(boolQueryBuilder);


        //从查询条件封装构建器对象获取查询条件对象
        NativeSearchQuery nativeSearchQuery = nativeSearchQueryBuilder.build();

        //调用es 模板工具类
        SearchHits<SkuInfo> searchHits = elasticsearchRestTemplate.search(nativeSearchQuery, SkuInfo.class);

        //把查询到结果对象，做分页处理
        SearchPage<SkuInfo> searchPage = SearchHitSupport.searchPageFor(searchHits, nativeSearchQuery.getPageable());

        //单独处理分类分组结果
        Terms categoryTerms = searchHits.getAggregations().get("skuCategorygroup");
        List<String> categoryList = getStringList(categoryTerms);
        //获取品牌分组结果对象
        Terms brandTerms= searchHits.getAggregations().get("skuBrandGroup");
        //调用获取分组结果方法
        List<String> brandList = getStringList(brandTerms);

        //获取规格的分组结果
        Terms specTerms= searchHits.getAggregations().get("skuSpecGroup");
        //调用方法
        Map<String, Set<String>> specMap = getStringSpecMap(specTerms);


        //创建一个集合，封装当前页数据集合
        List<SkuInfo> skuInfoList=new ArrayList<>();
        //从分页结果封装对象，获取当前页数据集合
        for (SearchHit<SkuInfo> searchHit : searchPage.getContent()) {
            //获取skuInfo
            SkuInfo content = searchHit.getContent();

            //获取高亮结果
            List<String> highlightFieldList = searchHit.getHighlightField("title");
            //判断高亮集合是否为空
            if(!CollectionUtils.isEmpty(highlightFieldList)){
                //提取第一组高亮数据，替换到商品标题
                content.setTitle(highlightFieldList.get(0));
            }

            //把对象值克隆复制到另外一个skuInfo
            SkuInfo skuInfo = new SkuInfo();
            BeanUtils.copyProperties(content,skuInfo);
            //把skuInfo对象加入集合
            skuInfoList.add(skuInfo);
        }

        //把查询到当前页数据集合封装到map
        resultMap.put("rows",skuInfoList);
        //把查询到总记录数
        resultMap.put("total",searchHits.getTotalHits());
        //把获取到总页码
        resultMap.put("totalPages",searchPage.getTotalPages());

        //把分类集合封装到map
        resultMap.put("categoryList",categoryList);

        //把获取到品牌的分组结果封装到map
        resultMap.put("brandList",brandList);
        //把获取到规格和规格选项数据封装到map
        resultMap.put("specMap",specMap);
        //分页数据保存
        //设置当前页码
        resultMap.put("pageNum",pageNum);
        resultMap.put("pageSize",pageSize);
        return resultMap;
    }


    //定义一个方法，获取规格的分组结果
    private Map<String, Set<String>> getStringSpecMap(Terms terms){
        //创建一个封装全部返回数据map集合
        Map<String, Set<String>> specMap=new HashMap<>();
        //创建一个Set集合来存储分组后规格字符串
        Set<String> specSet=new HashSet<>();
        //获取分组结果的存储桶集合对象
        for (Terms.Bucket bucket : terms.getBuckets()) {
            //"{"机身内存":"16G","网络":"联通3G"}"  json
            specSet.add(bucket.getKeyAsString());
        }
        //判断specSet是否为空
        if(!CollectionUtils.isEmpty(specSet)) {

            //遍历specSet
            for (String s : specSet) {
                //把json字符串转换成MAP  "机身内存"--->"16G"    "网络"-->"联通3G"
                Map<String, String> map = JSON.parseObject(s, Map.class);
                //遍历map
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    //获取key 规格名
                    String key = entry.getKey();
                    //获取value 规格选项值
                    String value = entry.getValue();

                    //按照key规格名称去返回map查询
                    Set<String> specValues = specMap.get(key);
                    //判断sepcSet是否为空
                    if (specValues == null) {
                        //初始化创建一个空set集合
                        specValues = new HashSet<>();
                    }
                    //把value规格选项值加入到specSet
                    specValues.add(value);
                    //把规格和规格选项set集合存储到返回specMap
                    specMap.put(key, specValues);

                }
            }
        }

        return specMap;
    }

    private List<String> getStringList(Terms categoryTerms) {
        //定义一个集合封装分类名称
        List<String> categoryList=new ArrayList<>();
        //从分类分组结果对象获取buckets
        for (Terms.Bucket bucket : categoryTerms.getBuckets()) {
            //获取名字
            categoryList.add(bucket.getKeyAsString());
        }
        return categoryList;
    }
}

package com.offcn.sellergoods.group;

import com.offcn.sellergoods.pojo.Specification;
import com.offcn.sellergoods.pojo.SpecificationOption;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;
@ApiModel(description = "规格的组合实体类",value = "SpecEntity")
public class SpecEntity {

    //规格对象
    @ApiModelProperty(value = "规格对象",required = false)
    private Specification specification;

    //规格选项集合
    @ApiModelProperty(value = "规格选项集合",required = false)
    private List<SpecificationOption> specificationOptionList;

    //封装
    //把属性设置为私有的，禁止使用对象.属性名访问属性
    //编写对应的get方法访问对象内容
    //编写set方法用来设置对象值


    public Specification getSpecification() {
        return specification;
    }

    public void setSpecification(Specification specification) {
        this.specification = specification;
    }

    public List<SpecificationOption> getSpecificationOptionList() {
        return specificationOptionList;
    }

    public void setSpecificationOptionList(List<SpecificationOption> specificationOptionList) {
        this.specificationOptionList = specificationOptionList;
    }
}

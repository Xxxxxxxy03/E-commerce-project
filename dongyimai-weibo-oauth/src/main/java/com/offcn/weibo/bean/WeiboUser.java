package com.offcn.weibo.bean;

import lombok.Data;
/*封装认证后的令牌信息*/
@Data
public class WeiboUser {

    //令牌
    private String access_token ;

    //令牌过期时间,该参数即将抛弃
    private String remind_in;

    //令牌过期时间,单位是秒数
    private long expires_in;

    //该社交用户唯一标识
    private String uid;

    //是否记住我
    private  String isRealName;
}

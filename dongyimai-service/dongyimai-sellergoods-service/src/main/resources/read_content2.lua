--设置响应头
ngx.header.content_type="application/json;charset=utf8"
--引入nginx获取url连接参数方法
local uri_args=ngx.req.get_uri_args()
--读取传递广告分类编号
local categoryid=uri_args["id"]
--引入所需类库
local mysql=require("resty.mysql")
local redis=require("resty.redis")
local cjson=require("cjson")
--获取nginx缓存
local cache_ngx=ngx.shared.dis_cache
local contentCache=cache_ngx:get('content_cache_'..categoryid)
--判断nginx本地缓存是否读取到数据
if contentCache==nil or contentCache==''
then
    --表示从nginx本地缓存未读取到数据
    --创建redis链接客户端
    local red=redis:new()
    local ip ="192.168.188.129"
    local port=6379
    --设置连接超时时间
    red:set_timeout(50000)
    --连接到redis
    red:connect(ip,port)
    --从redis读取对应数据
    local redis_context=red:get('connect_'..categoryid)
   --判断从redis读取数据是否为空
    if redis_context==ngx.null or redis_context==''
    then
        --redis读取数据为空
        --从mysql数据库读取数据
        --建立mysql客户端
        local db=mysql:new()
        --设置超时时间  单位毫秒
        db:set_timeout(50000)
        --设置连接参数
        local props = {
            host="192.168.188.129",
            port=3307,
            database="dongyimaidb",
            user="root",
            password="root"
        }
        --和数据库建立连接
        local res =db:connect(props)
        --准备查询sql
        local sql="SELECT url,pic FROM tb_content WHERE STATUS=1 AND category_id="..categoryid
        --发出查询
        local res2=db:query(sql)
        --把从数据库读取到数据转换json字符串
        local jsonValue=cjson.encode(res2)
        --把数据写入redis缓存
        red:set("content_"..categoryid,jsonValue)
        --把数据写入本地缓存
        cache_ngx:set("content_cache"..categoryid,redis_context,30)
        --关闭数据库连接
        db:close()
        --关闭redis连接
        red:close()
        --把读取内容响应给网页
        ngx.say(jsonValue)
    else
        --redis读取数据不为空
        --把redis读取到数据写入nginx本地缓存，注意要设置一个缓存有效时间 单位是秒
        cache_ngx:set('content_cache'..categoryid,redis_context,30)
        --关闭redis连接
        red:close()
        --把读取到内容响应网页响应
        ngx.say(redis_context)
    end
else
    --表示从nginx缓存读取到了数据
    --直接把数据范湖给浏览器显示
    ngx.say(contentCache)
end
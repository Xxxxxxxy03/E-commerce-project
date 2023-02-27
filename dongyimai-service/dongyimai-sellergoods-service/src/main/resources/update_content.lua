--设置nginx响应头
ngx.header.content_type="application/json;charset=utf8"
--引入连接到mysql数据库类库
local mysql=require("resty.mysql")
--引入json类库
local cjson=require("cjson")

--引入redis类库
local redis=require("resty.redis")

--调用nginx本身一个函数，可以用来获取到url传递的参数
local uri_args=ngx.req.get_uri_args()

--捕获url传递的  分类编号
local categoryid=uri_args["id"]

--连接到数据库读取指定分类广告数据

--创建连接到数据库客户端对象
local db=mysql:new()
--设置超时时间 单位是毫秒
db:set_timeout(50000)
--创建连接到数据库参数对象
local props={
    host="192.168.188.129",
    port=3307,
    database="dongyimaidb",
    user="root",
    password="root"
}
--使用客户端对象和数据库建立连接
local res=db:connect(props)

--准备一条sql
local sql="SELECT  url,pic FROM tb_content WHERE status=1 and category_id="..categoryid

--执行sql查询
local res2=db:query(sql)

--关闭数据库连接
db:close()

--连接到redis缓存，把从数据库读取到的数据写入redis中
local red=redis:new()
--设置连接到redis超时时间
red:set_timeout(50000)
local ip="192.168.188.129"
local port=6379
--和redis建立连接
red:connect(ip,port)

--要把从数据库读取到的数据转换成json字符串
local jsonValue=cjson.encode(res2)

--把json字符串写入redis
red:set('content_'..categoryid,jsonValue)

--关闭redis连接
red:close()

--给网页返回提示内容
ngx.say("true")
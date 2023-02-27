--设置响应头
ngx.header.content_type="application/json;charset=utf8"
--引入redis类库
local redis=require("resty.redis")
--引入nginx获取url连接参数方法
local uri_args=ngx.req.get_uri_args()
--读取传递广告分类编号
local categoryid=uri_args["id"]
--创建连接到redis客户端对象
local red =redis:new()
--设置redis服务器ip
local ip="192.168.188.129"
--端口号
local port=6379

--设置连接超时时间
red:set_timeout(50000)
--和redis建立连接
local ok,err=red:connect(ip,port)
--读取redis指定广告分类数据
local rescontent=red:get('content_'..categoryid)
--关闭redis连接
red:close()
--显示到网页
ngx.say("true")
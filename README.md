# weixin-login
一个微信登陆的微服务
#布署
配置application.properties
```sh
$ cd weixin-login
$ vim src/main/resources/application.properties
$ mvn package
$ java -jar target/weixin-login-1.0.jar
```

配置好域名转发.必须用80或443端口.

将application.properties中的weixin.login属性配置到微信公众号的回调url.


#使用说明
假如需要在  http://www.baidu.com/userInfo 这个页面获取用户微信的信息.
使用Url安全的base64将域名编码成 aHR0cDovL3d3dy5iYWlkdS5jb20vdXNlckluZm8
然后  直接在微信浏览器内请求  http://domain/weixin/login.do?redirect=aHR0cDovL3d3dy5iYWlkdS5jb20vdXNlckluZm8
最终,请求会被重定向到 http://www.baidu.com/userInfo?weixinData=xxxxxxxx
然后,使用AESUtils这个类来解码weixinData即可获得weixin openid信息.


#安全
建议对可转发的域名进行限定,以防止第三方获取到用户的openid等信息.

授权中心服务
1、主要进行鉴权、认证

操作流程：
1、浏览器请求user-service的登录接口（登录接口不设置权限认证），将用户名和密码传给user-service。user-service根据用户名查看DB，判断密码准确性。
2、用户密码判断无误后，通过Feign远程调用uaa-service获取token，需要传clientId（和uaa服务设置一致的clientId）、用户名、密码。
3、uaa-service接受请求之后，会判断请求传参clientId、用户名和密码的正确性。
4、判断上一步请求参数准确无误后，uaa-service根据配置策略返回JWT给user-service.JWT是通过RSA加密，包括了用户名、权限信息和过期时间。
5、user-service获取JWT之后，连同用户信息一起返回给浏览器。
6、当浏览器需要访问有权限认证的资源服务，需要在请求的Header加参数名“Authorization”和参数值“Bearer {token}”。
7、资源服务器通过用SpringMVC的拦截器对请求进行拦截，将Header中的token取出，用公钥解密token，解密之后能够得到该token包含的用户信息和权限信息，从而进行权限认证。
8、当该token对应的用户有权限访问该资源，资源服务器会返回该资源；若没有权限，则返回没有权限访问该资源。


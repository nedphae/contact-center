# 客服系统 权限设计

依托于 Spring OAuth2 进行权限设计，使用 Spring Security GlobalMethodSecurity 根据设计好的角色在开发时就可以进行API权限设计.

同时使用 JWT 作为认证 Token，降低了服务端进行鉴权时的资源消耗.

OAuth2 + JWT 支持跨域传输，因此也可用于整个系统的单点验证，后期也便于和现有系统的 ERP模块打通.

GlobalMethodSecurity 的缺点是需要开发时期就要确定权限

~~另外一个风险为：当前 Spring Cloud OAuth2 没有与 Spring Cloud Gateway 进行集成，网关无法鉴权.~~

#### 权限模块具体实现

每个业务服务可以定义自己所属的资源模块，通过开启 @EnableResourceServer 并继承实现 ResourceServerConfigurerAdapter 来实现

业务服务通过指定自己的 resourceId 来判断 AuthorizationServer 赋予的权限(resourceIds)是否包含本服务

resource 鉴权后，进入业务API，业务API 通过 @PreAuthorize 注解判断用户角色

resource 可以通过数据库修改，API 权限编码时确定

#### 备注

* 不再使用网关鉴权，改为服务自己进行鉴权
* 考虑了现有的网关鉴权方案，没有能满足本地校验的
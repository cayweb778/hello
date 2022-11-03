# 下一代响应式微服务架构
## 特性 

- vite实现无编译速度影响：（即不会随着文件代码变多而编译慢，按需编译）
- \<script setup> 简写组件,更快实现开发，减少高额代码量
- 下代开发新概念语法：webflux（reactor）或rxjava2 实现《异步单向数据流》
  - 代码量减少，优雅的代码，直观逻辑本质的代码，使开发维护代码更轻松
    - 减少高额代码量
    - 优雅的代码，使维护代码更轻松
- r2dbc极大提升开发数据库框架效率
- RBAC角色控制框架
- 低代码自动化开发平台，极致的开发业务体验
- 大型单体微服务设计目录结构（最简洁的前后端的前后端项目结构）
- 项目重构为maven提升编译响应，Gradle 作为设计【boozsoft-nc-designs项目】后端架构环境
- 响应式微服务,Spring CloudAlibaba，完美微服务集成
- Oauth2，前后端Token认证

## 精简版架构
- 移除微服务与单点登录与redis与消息中间件
- 生产环境：jdk15+jar+postgres

## 后端
```text
Spring Cloud Alibaba（微服务）
Feign Reactive 
WebFlux 
JDK15
R2dbc
postgres13
```

## 前端：
```text
ts   7.4
vite 2.0
vue3 3.11
vben 4.5.1
antd 2.2.1
```


- 特性
    - 拓展异步编程支持并主导异步编程
    - 最先进的开源异步编程
    - 充分发挥线程异步作用
    - 更优雅的编程语法糖


- 解决的问题 / 痛点
    - 更优雅的编程语法糖,有助于排逻辑
    - 远离书写sql: spring data单表概念，注重java层开发，而不是结构化查询语言与java逻辑并行


- 时间轴
    - 2020年网上报销Reactive架构（Spring Webflux+JPA+R2dbc）
    - NC架构计划
    - 首次架构开始于2020年，实现 完全Reactive架构
    - 架构定型 Spring Cloud Alibaba（微服务） + WebFlux + R2dbc + Vue3(vite + ts + vben + antd)
    - NC融入spring cloud alibaba、nacos
    - oauth2 oidc模式 SSO单点服务器
    - 数据源路由
    - NC角色权限页面
    - 2021-04-26 加入R2dbc审计功能，解决【ID、唯一码概念问题】，创建时间 修改时间等字段自动填充,全局ID采用雪花算法
    - 2021-06-30 完全完善Spring Cloud Alibaba + Nacos + Feign Reactive

UOS、Deepin
- echo fs.inotify.max_user_watches=524288 | sudo tee -a /etc/sysctl.conf && sudo sysctl -p
- sudo sysctl --system

第一次down 停止gradle引入,配置jdk版本 配置gradle 然后 重载gradle
\- 禁止使用JPA动态数据库
- (原因:1.与webflux框架概念冲突,必须要返回Mono链,如果返回jpa数据是无法被管理的,2.切换逻辑复杂，不可预判性问题)
- r2dbc路由依赖:
    - org.springbooz.boot:booz-boot-starter-data-r2dbc-router:2.4.1-RELEASE
    - r2dbc路由框架,无兼容性的复杂性问题,所以框架逻辑更加简单
      org.springbooz.boot:spring-boot-starter-data-r2dbc:2.4.1'
- 遵守Reactive反应编程开发规范`
- IDEA开发 setting=> gradle IDEA模式
- IDEA开发 setting=> annotation 开启注解
- IDEA开发 setting=>开启setting=>eslint auto configuration 与fix on save


- VUE 菜单与路由与区域
    - [菜单配置](./web/src/router/menus/index.ts "标题")
        - import modules from 'globby!/@/router/menus/modules/**/*.@(ts)'
        - 改成想要的位置
        - [菜单目录](./web/src/router/menus "标题")
    - [路由配置](./web/src/router/routes/index.ts "标题")
        - import modules from 'globby!/@/router/routes/modules/**/*.@(ts)'
        - 改成想要的位置
        - [路由目录](./web/src/router/routes "标题")
    - [区域配置](./web/src/locales/lang/zh_CN/routes "标题")
- 架构
    - 后端
        - webflux
        - spring cloud alibaba dubbo 微服务 单机运行
        - 动态数据源 路由(无兼容性的复杂性问题,理论比较稳定)
          方法级
          业务级
          ```
          schemaRouterService.bind("nc", "public1", () ->

                                entityTemplate.select(Jigou.class).from("jigou").all()
                        )
              ```
    - 目录
    - 后台src项目目录
        - domain
          entity
          vo
          dto
        - feign 微服务接口
        - repo 数据仓库
        - rest rest controler

    - 前端
        - vue3  composition api : 反应式语法
        - vben: admin模板
        - vue ant: 组件库
        - typescript： 高阶js
        - sass: 高阶css


- 创建模块
    - modules 新建模块(复制其他模块)
    - setting.gradle 使模块有效
    - build.gradle 在主模块中引入模块
- 微服务
```
    webflux微服务方案
        openfeign方案
            目前webflux不支持feign
        dubbo方案
    层级: dubbo - rest - service - r2dbc
    service必须返回Mono或fulx（非阻塞线程中的方法，不能使用）
```
- 服务
```
Spring Boot Admin 2.3.1 localhost:端口/applications
Swagger 3.0  localhost:端口/doc.html
```

- vue idea 语法设置
  开启eslint
  关闭tslint:原因 eslint包含tslint检测，不需要tslint检测


统一本地开发域名调整(用utools写在hosts中):
127.0.0.1 apincdev.chinabooz.com
127.0.0.1 accountdev.chinabooz.com
前台访问:
apincdev.chinabooz.com:3103
后端接口:
- 授权服务器 资源服务器 保持同域
- 后端服务器  前台服务器 保持同域
- 后端服务器 授权服务器保证二级同域

- 指定好版本后,清除IDEA缓存重启

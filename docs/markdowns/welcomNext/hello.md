# NC App
### 使用下一代Reactive微服务
## 模块
- 财税达 Oauth2 Sso
- 财税达 Oauth2 Resource
- NC平台
- 总账
- 现金银行
- 固定资产

## 开发环境
- 开发人员：jdk15 + node14~
- 线上环境(podman)：
    - postgre13
    - redis
    - nacos
    - Boozsoft  Admin
    - Boozsoft Oauth2 Sso
    - BoozSoft Oauth2 Resource
    - Kafka

- 持续集成
    - jenkins集成开发环境



## 生产环境
- 线下服务器主机
- Centos8
- podman
    - postgre13
    - redis
    - nacos
    - Boozsoft  Admin
    - Boozsoft Oauth2 Sso
    - BoozSoft Oauth2 Resource
    - BoozSoft Nc App
    - Kafka


- 目录结构

```text
# dist 生产环境包，pods 产品目录 scripts 脚本目录

[root@localhost boozdata]# pwd
/boozdata
[root@localhost boozdata]# ls
dist  pods  scripts
```


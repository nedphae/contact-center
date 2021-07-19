# 客服系统

最高级的基于 Spring Cloud 的高并发，高可用开源微服务客服系统

### 项目图示

![流程图](doc/img/系统架构图.png)

[客服系统架构心得](doc/架构详解.md)
### 详细介绍

子系统（本项目） :

| 系统名称     | 系统地址                                  | 详细说明                             |
| :---------: | :--------------------------------------- | :--------------------------------- |
| 机器人服务   | [bot](bot)                               | 客服系统QA（base on ElasticSearch）机器人 |
| 调度服务     | [dispatching-center](dispatching-center) | 客服调度服务，根据配置策略分配客服 |
| 网关        | [gateway](gateway)                       | 微服务网关，提供统一的系统入口 |
| 接入服务     | [im-access](im-access)                   | IM WebSocket 接入服务，包括 客户端 http 接入 |
| 消息服务     | [message-server](message-server)         | IM 消息路由，消息存储服务，客服/客户在线状态管理服务（base on hazelcast embedded） |
| 授权服务     | [oauth2-auth-server](oauth2-auth-server) | oauth2 授权服务，签发加密JWT |
| 客服信息     | [staff-admin](staff-admin)               | 客服信息管理服务，包括客服账号，客服分组，客服分流（接待组），配置管理等 |
| graphql 聚合服务 | [GraphQLBFF](GraphQLBFF)             | GraphQL 接口服务，提供后台 http 的 GraphQL 聚合封装接口 |

其他项目 :

| 项目名称     | 项目地址                             | 详细说明                             |
| :---------: | :--------------------------------- | :--------------------------------- |
| 客服系统桌面客户端 | [contact-center-client](https://github.com/nedphae/contact-center-client) | 基于 Electron + React 开发的桌面客户端，UI 使用 material-ui |
| 客服系统用户端静态服务 | [customer-web-client](https://github.com/nedphae/customer-web-client) | 客服系统用户端，基于 chatUI 开发 |
| 会话式 AI 机器人 | [chat-bot](https://github.com/nedphae/chat-bot) | 基于 Rasa，项目进行ing，未开放|

### 开发计划注解图例

| Mark     | Description      |
| :------: | :--------------- |
| 🏃       | work in progress |
| ✋       | blocked task     |
| ❌       | deprecated       |
| ⚫        | planning         |

### 后续开发计划

- [ ] ⚫ Apache Kylin + Saiku / SuperSet + Apache Druid 进行 OLAP 数据分析模块开发

- [ ] ⚫ 数据使用 kafka 导入到 kylin / hbase / hadoop
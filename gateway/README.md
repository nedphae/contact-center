# 客服系统 网关

Deprecated

- [ ] ❌ 网关项目延期维护，不在推荐使用，使用 k8s kong/Istio 代替

如果不使用云（k8s）部署项目，也可以使用该网关项目，支持可能不佳

consul 删除服务

consul services deregister -id={Your Service Id}

`
consul services deregister -id=im-server-8701
consul services deregister -id=message-server-8703
consul services deregister -id=oauth2-auth-server-8704
consul services deregister -id=staff-admin-8705
consul services deregister -id=websocket-server:8702
consul services deregister -id=bot-8707
consul services deregister -id=graphql-bff-8780
consul services deregister -id=gateway-server-8700
consul services deregister -id=dispatching-center-8706
`

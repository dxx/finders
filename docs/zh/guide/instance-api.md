# 实例 API

## 查询实例列表

### 描述

查询指定服务的所有实例。

### 请求方式

GET

### 请求路径

```
/api/instance/list
```

### 请求参数

参数类型：Query 参数

参数列表：

| 参数名      | 类型   | 是否必填 | 说明                                    |
| ----------- | ------ | -------- | --------------------------------------- |
| namespace   | string | 否       | 命名空间。默认为 `default`              |
| clusters    | string | 否       | 集群名称。多个集群使用`,`分割。默认为空 |
| serviceName | string | 是       | 服务名称                                |

### 请求示例

```shell
GET http://localhost:9080/api/instance/list?namespace=&clusters=&serviceName=testService
```

返回：

```json
{
  "serviceName": "testService",
  "clusters": [
    "DEFAULT_CLUSTER"
  ],
  "instances": [
    {
      "instanceId": "DEFAULT_CLUSTER#testService@127.0.0.1:8080",
      "cluster": "DEFAULT_CLUSTER",
      "serviceName": "testService",
      "ip": "127.0.0.1",
      "port": 8080,
      "status": "HEALTHY"
    }
  ]
}
```



## 查询实例

### 描述

查询指定实例。

### 请求方式

GET

### 请求路径

```
/api/instance
```

### 请求参数

参数类型：Query 参数

参数列表：

| 参数名      | 类型   | 是否必填 | 说明                               |
| ----------- | ------ | -------- | ---------------------------------- |
| namespace   | string | 否       | 命名空间。默认为 `default`         |
| cluster     | string | 否       | 集群名称。默认为 `DEFAULT_CLUSTER` |
| serviceName | string | 是       | 服务名称                           |
| ip          | string | 是       | 实例 IP                            |
| port        | int    | 是       | 实例端口                           |

### 请求示例

```shell
GET http://localhost:9080/api/instance?namespace=&cluster=&serviceName=testService&ip=127.0.0.1&port=8080
```

返回：

```json
{
  "instanceId": "DEFAULT_CLUSTER#testService@127.0.0.1:8080",
  "cluster": "DEFAULT_CLUSTER",
  "serviceName": "testService",
  "ip": "127.0.0.1",
  "port": 8080,
  "status": "HEALTHY"
}
```



## 注册实例

### 描述

注册一个服务实例，服务不存在时自动创建。

### 请求方式

POST

### 请求路径

```
/api/instance
```

### 请求参数

参数类型：`Content-Type: application/json`

参数列表：

| 参数名      | 类型   | 是否必填 | 说明                               |
| ----------- | ------ | -------- | ---------------------------------- |
| namespace   | string | 否       | 命名空间。默认为 `default`         |
| cluster     | string | 否       | 集群名称。默认为 `DEFAULT_CLUSTER` |
| serviceName | string | 是       | 服务名称                           |
| ip          | string | 是       | 实例 IP                            |
| port        | int    | 是       | 实例端口                           |

### 请求示例

```shell
POST http://localhost:9080/api/instance
Content-Type: application/json

{
  "namespace": "default",
  "cluster": "DEFAULT_CLUSTER",
  "serviceName": "testService",
  "ip": "127.0.0.1",
  "port": 8080
}
```

返回：

```
success
```



## 注销实例

### 描述

注销一个服务实例。

### 请求方式

DELETE

### 请求路径

```
/api/instance
```

### 请求参数

参数类型：`Content-Type: application/json`

参数列表：

| 参数名      | 类型   | 是否必填 | 说明                               |
| ----------- | ------ | -------- | ---------------------------------- |
| namespace   | string | 否       | 命名空间。默认为 `default`         |
| cluster     | string | 否       | 集群名称。默认为 `DEFAULT_CLUSTER` |
| serviceName | string | 是       | 服务名称                           |
| ip          | string | 是       | 实例 IP                            |
| port        | int    | 是       | 实例端口                           |

### 请求示例

```shell
DELETE http://localhost:9080/api/instance
Content-Type: application/json

{
  "namespace": "default",
  "cluster": "DEFAULT_CLUSTER",
  "serviceName": "testService",
  "ip": "127.0.0.1",
  "port": 8080
}
```

返回：

```
success
```


## 发送实例心跳

### 描述

给指定实例发送一个心跳。

> 注意：禁用状态的实例，心跳请求无效。

### 请求方式

PUT

### 请求路径

```
/api/instance/beat
```

### 请求参数

参数类型：`Content-Type: application/json`

参数列表：

| 参数名      | 类型   | 是否必填 | 说明                               |
| ----------- | ------ | -------- | ---------------------------------- |
| namespace   | string | 否       | 命名空间。默认为 `default`         |
| cluster     | string | 否       | 集群名称。默认为 `DEFAULT_CLUSTER` |
| serviceName | string | 是       | 服务名称                           |
| ip          | string | 是       | 实例 IP                            |
| port        | int    | 是       | 实例端口                           |

### 请求示例

```shell
PUT http://localhost:9080/api/instance/beat
Content-Type: application/json

{
  "namespace": "default",
  "cluster": "DEFAULT_CLUSTER",
  "serviceName": "testService",
  "ip": "127.0.0.1",
  "port": 8080
}
```

返回：

```
success
```



## 修改实例状态

### 描述

修改指定实例的状态。

### 请求方式

PUT

### 请求路径

```
/api/instance/status
```

### 请求参数

参数类型：`Content-Type: application/json`

参数列表：

| 参数名      | 类型   | 是否必填 | 说明                                                       |
| ----------- | ------ | -------- | ---------------------------------------------------------- |
| namespace   | string | 否       | 命名空间。默认为 `default`                                 |
| cluster     | string | 否       | 集群名称。默认为 `DEFAULT_CLUSTER`                         |
| serviceName | string | 是       | 服务名称                                                   |
| ip          | string | 是       | 实例 IP                                                    |
| port        | int    | 是       | 实例端口                                                   |
| status      | string | 是       | 实例状态。HEALTHY：健康，UN_HEALTHY：不健康，DISABLE：禁用 |

### 请求示例

```shell
PUT http://localhost:9080/api/instance/status
Content-Type: application/json

{
  "namespace": "default",
  "cluster": "DEFAULT_CLUSTER",
  "serviceName": "testService",
  "ip": "127.0.0.1",
  "port": 8080,
  "status": "DISABLE"
}
```

返回：

```
success
```
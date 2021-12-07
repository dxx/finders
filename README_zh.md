# Finders

![JDK8+](https://img.shields.io/badge/JDK-1.8%2B-%23b07219)
[![Apache-2.0 licensed](https://img.shields.io/github/license/dxx/finders.svg?color=blue)](./LICENSE)

[English](./README.md)

分布式服务管理。灵感来自于 [Nacos](https://github.com/alibaba/nacos) 。

Finders 是一个分布式服务管理平台，帮助你实现微服务的治理，如服务发现、服务注册、服务监控检查。

## 快速开始

### 下载安装包

Finders 发布在 Github，可直接从[此处](https://github.com/dxx/finders/releases)下载。

下载完成后，解压压缩文件：

```shell
unzip finders-<version>.zip
```

### 源代码构建

从源代码构建需要 `JDK8+`。

#### 获取源代码

```shell
git clone https://github.com/dxx/finders.git
cd finders
```

#### 构建二进制包

```shell
./gradlew -p ./distribution build
```

然后在 `./distribution/build/distributions` 目录中生成二进制包。进入后，可以看到有两个压缩包文件，`.zip` 和 `.tar` 文件。

解压压缩文件：

```shell
unzip finders-<version>.zip
```

#### 启动服务

```shell
cd finders/bin
```

Linux/Unix/Mac：

```shell
./startup
```

Windows：

```shell
startup.bat
```

或双击 `startup.bat` 文件。

#### 关闭服务

Linux/Unix/Mac：

```shell
./shutdown
```

Windows：

```
shutdown.bat
```

或双击 `shutdown.bat` 文件。

> 注意：如果没有找到 `shutdown.bat` 文件，请重新构建。

## 文档

你可以从[这里](https://dxx.github.io/finders)查看完整的文档。

## 许可证

Finders 在 [Apache 2.0 license](./LICENSE) 下发布。


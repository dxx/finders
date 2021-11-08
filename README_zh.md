# Finders

[![Apache-2.0 licensed](https://img.shields.io/github/license/dxx/finders.svg?color=blue)](./LICENSE)

分布式服务管理。灵感来自于 [Nacos](https://github.com/alibaba/nacos)。

[English](./README.md)

## 快速开始

从源代码构建。该构建需要 `java8`。

### 获取源代码

```shell
git clone https://github.com/dxx/finders.git
cd finders
```

### 构建二进制包

```shell
./gradlew -p ./distribution build
```

然后在 `./build/distributions` 目录中生成二进制包。进入后，可以看到有两个压缩包文件，`.zip` 和 `.tar` 文件。

减压压缩文件：

```shell
unzip finders-<version>.zip
```

### 启动服务

```shell
cd finders/bin
```

#### Linux/Unix/Mac

运行以下命令来启动：

```shell
./startup
```

#### Windows

运行以下命令来启动：

```shell
startup.bat
```

或双击 `startup.bat` 文件。

### 关闭服务

#### Linux/Unix/Mac

```shell
./shutdown
```

#### Windows

```
shutdown.bat
```

或双击 `shutdown.bat` 文件。

> 注意：如果没有找到 `shutdown.bat` 文件，请重新构建。

## 许可证

Finders 在 [Apache 2.0 license](./LICENSE) 下发布。


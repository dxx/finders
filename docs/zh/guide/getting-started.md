# 快速开始

安装包可直接下载或使用源代码构建。

## 前提

* 安装 JDK 8+
* 配置 `JAVA_HOME` 环境变量

## 下载安装包

Finders 发布在 Github，可直接从[此处](https://github.com/dxx/finders/releases)下载。

下载完成后，解压压缩文件：

```shell
unzip finders-<version>.zip
```

## 源代码构建

### 获取源代码

```shell
git clone https://github.com/dxx/finders.git
cd finders
```

### 构建二进制包

```shell
./gradlew -p ./distribution build
```

然后在 `./distribution/build/distributions` 目录中生成二进制包。进入后，可以看到有两个压缩包文件，`.zip` 和 `.tar` 文件。

解压压缩文件：

```shell
unzip finders-<version>.zip
```

## 启动服务

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

## 关闭服务

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

## 访问控制台

打开浏览器，输入 `http://localhost:9080/finders`。

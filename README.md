# Finders

![JDK8+](https://img.shields.io/badge/JDK-1.8%2B-%23b07219)
[![Apache-2.0 licensed](https://img.shields.io/github/license/dxx/finders.svg?color=blue)](./LICENSE)

[中文](./README_zh.md)

Distributed service management. Inspired by [Nacos](https://github.com/alibaba/nacos).

Finders is a distributed service management platform that help you achieve the governance of microservices, such as service discovery, service registration, service health check. 

## Getting Started

### Download Installation Package

Finders is released on the github and can be downloaded directly from [here](https://github.com/dxx/finders/releases).

After the download is completed, decompression zip file:

```shell
unzip finders-<version>.zip
```

### Build from Source Code

Build from source code requires `JDK8+`.

#### Get the Source Code

```shell
git clone https://github.com/dxx/finders.git
cd finders
```

#### Build Binary Package

```shell
./gradlew -p ./distribution build
```

The binary package is then generated in the `./distribution/build/distributions` directory. Go in and you can see that there are two compressed package files,  `.zip` and `.tar` file.

Decompression zip file:

```shell
unzip finders-<version>.zip
```

#### Start Server

```shell
cd finders/bin
```

Linux/Unix/Mac:

```shell
./startup
```

Windows :

```shell
startup.bat
```

Or double-click the `startup.bat` file.

#### Shutdown Server

Linux/Unix/Mac:

```shell
./shutdown
```

Windows:

```
shutdown.bat
```

Or double-click the `shutdown.bat` file.

> Note: if the `shutdown.bat` file is not found, please build again.

## Documentation

Your can view the full documentation from [here](https://dxx.github.io/finders).

## License

Finders is released under the [Apache 2.0 license](./LICENSE).

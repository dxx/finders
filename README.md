# Finders

[![Apache-2.0 licensed](https://img.shields.io/github/license/dxx/finders.svg?color=blue)](./LICENSE)

[中文](./README_zh.md)

Distributed service management. Inspired by [Nacos](https://github.com/alibaba/nacos).

## Getting Started

Build from source code. The build requires `java8`.

### Get the Source Code

```shell
git clone https://github.com/dxx/finders.git
cd finders
```

### Build Binary Package

```shell
./gradlew -p ./distribution build
```

The binary package is then generated in the `./distribution/build/distributions` directory. Go in and you can see that there are two compressed package files,  `.zip` and `.tar` file.

Decompression zip file:

```shell
unzip finders-<version>.zip
```

### Start Server

```shell
cd finders/bin
```

#### Linux/Unix/Mac

Run the following command to start:

```shell
./startup
```

#### Windows

Run the following command to start:

```shell
startup.bat
```

Or double click the `startup.bat` file.

### Shutdown Server

#### Linux/Unix/Mac

```shell
./shutdown
```

#### Windows

```
shutdown.bat
```

Or double click the `shutdown.bat` file.

> Note: if the `shutdown.bat` file is not found, please build again.

## License

Finders is released under the [Apache 2.0 license](./LICENSE).

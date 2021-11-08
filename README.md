# Finders

[![Apache-2.0 licensed](https://img.shields.io/github/license/dxx/finders.svg?color=blue)](./LICENSE)

Distributed service management. Inspired by [Nacos](https://github.com/alibaba/nacos).

## Quick Start

Build from source code. The build requires `java8`.

### Getting the Source Code

```shell
git clone https://github.com/dxx/finders.git
```

### Build Binary Package

```shell
cd finders
./gradlew -p ./distribution build
```

The binary package is then generated in the `./build/distributions` directory. Go in and you can see that there are two compressed package files,  `.zip` or `.tar`.

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

Or click the `startup.bat` file.

### Shutdown Server

#### Linux/Unix/Mac

```shell
./shutdown
```

#### Windows

```
shutdown.bat
```

Or click the `shutdown.bat` file.

> Note: if the `shutdown.bat` file is not found, please build again.

## License

Finders is released under the [Apache 2.0 license](./LICENSE).

# Getting Started

The installation package can be downloaded directly or built using the source code.

## Prerequisites

* JDK 8+ installed
* `JAVA_HOME` environment variable configured

## Download Installation Package

Finders is released on the github and can be downloaded directly from [here](https://github.com/dxx/finders/releases).

After the download is completed, decompression zip file:

```shell
unzip finders-<version>.zip
```

## Build from Source Code

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

## Start Server

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

## Shutdown Server

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

## Access the Console

Open the browser and enter `http://localhost:9080/finders`.

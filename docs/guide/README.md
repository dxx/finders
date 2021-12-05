# Introduction

Finders is a distributed service management platform that helps you achieve the governance of microservices, such as service discovery, service registration, service healthy check. Finders provides a console UI to visualize viewing of service instances, instance status, and cluster node status.

## Features

* Easy to install, fast
* Cluster deployment to ensure high availability
* Support for multiple-language client access

## Concepts

### Namespace

For coarse-grained service isolation, the same service can exist for different namespaces.Service distinctions are often used in different environments, such as development environment, test environment, and production environment. 

### Service

Services providing a collection of software functions with a unique identification by which specific services can be identified. 

### Cluster

All instances of a service can make up of one or more clusters.In addition to the namespace, the clusters can be further divided. 

### Instance

Represents ote the accessible network address of the arch software service, one service may have one or more instances. 

### Cluster Node

Cluster nodes are a node deployed in a cluster by finders services that provide distributed service management. Finders supports multiple nodes that form a cluster by configuration. Each node in the cluster, the external provides the same api service. 

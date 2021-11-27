import { requestJSON, requestString, Method } from "./fetch";
import { URL_INSTANCES, URL_INSTANCE } from "./url";

export enum InstanceStatus {
  HEALTHY = "HEALTHY",
  UN_HEALTHY = "UN_HEALTHY",
  DISABLE = "DISABLE",
  UNKNOWN = "UNKNOWN",
}

export interface InstancesReqData {
  namespace?: string,
  serviceName: string,
}

interface InstancesResData {
  serviceName: string,
  instanceList: Array<InstanceInfo>,
}

export interface InstanceInfo {
  instanceId: string,
  cluster: string,
  serviceName: string,
  ip: string,
  port: number,
  status: InstanceStatus,
}

interface InstanceDeregisterData {
  namespace?: string,
  cluster?: string,
  serviceName: string,
  ip: string,
  port: number,
}

export function getInstances(data: InstancesReqData) {
  return requestJSON<InstancesReqData, InstancesResData>(URL_INSTANCES, Method.GET, data);
}

export function deregisterInstance(data: InstanceDeregisterData) {
  return requestString<InstanceDeregisterData>(URL_INSTANCE, Method.DELETE, data);
}

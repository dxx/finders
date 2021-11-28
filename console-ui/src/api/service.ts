import { requestJSON, Method } from "./fetch";
import { URL_SERVICES } from "./url";

export interface ServicesReqData {
  namespace?: string,
  serviceName?: string,
  page: number,
  size: number,
}

interface ServicesResData {
  page: number,
  size: number,
  count: number,
  serviceList: Array<ServiceInfo>,
}

export interface ServiceInfo {
  serviceName: string,
  clusterCount: number,
  instanceCount: number,
  healthyInstanceCount: number,
}

export function getServices(data: ServicesReqData) {
  return requestJSON<ServicesReqData, ServicesResData>(URL_SERVICES, Method.GET, data);
}

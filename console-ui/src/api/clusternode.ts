import { requestJSON, Method } from "./fetch";
import { URL_CLUSTER_NODES } from "./url";

export enum NodeStatus {
  UP = "UP",
  DOWN = "DOWN",
}

export interface ClusterNodeInfo {
  id: string,
  ip: string,
  port: number,
  address: string,
  status: NodeStatus,
}

export function getClusterNodes() {
  return requestJSON<void, Array<ClusterNodeInfo>>(URL_CLUSTER_NODES, Method.GET, undefined);
}

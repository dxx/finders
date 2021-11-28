import { requestJSON, Method } from "./fetch";
import { URL_NAMESPACE_NAMES, URL_NAMESPACES } from "./url";

export interface NamespaceInfo {
  namespace: string,
  serviceCount: number,
}

export function getNamespaceNames() {
  return requestJSON<void, Array<string>>(URL_NAMESPACE_NAMES, Method.GET, undefined);
}

export function getNamespaces() {
  return requestJSON<void, Array<NamespaceInfo>>(URL_NAMESPACES, Method.GET, undefined);
}

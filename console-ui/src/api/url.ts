let prefix = "http://localhost:9080";

if (process.env.NODE_ENV === "production") {
  prefix = "";
}


const URL_NAMESPACE_NAMES = prefix + "/console/namespace/names";
const URL_NAMESPACES = prefix + "/console/namespaces";
const URL_CLUSTER_NODES = prefix + "/console/cluster/nodes";
const URL_SERVICES = prefix + "/console/services";
const URL_INSTANCES = prefix + "/console/instances";

const URL_INSTANCE = prefix + "/api/instance";


export {
  URL_NAMESPACE_NAMES,
  URL_NAMESPACES,
  URL_CLUSTER_NODES,
  URL_SERVICES,
  URL_INSTANCES,
  URL_INSTANCE
}

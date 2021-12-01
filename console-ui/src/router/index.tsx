import React from "react";
import { useRoutes, Navigate } from "react-router-dom";

const withSuspense = (Component: React.LazyExoticComponent<any>) => {
  return (props: any) => (
    <React.Suspense fallback={null}>
      <Component {...props} />
    </React.Suspense>
  );
}

const NamespaceList = withSuspense(React.lazy(() => import("../views/namespace/NamespaceList")));
const ClusterNodeList = withSuspense(React.lazy(() => import("../views/clusternode/ClusterNodeList")));
const ServiceList = withSuspense(React.lazy(() => import("../views/service/ServiceList")));
const InstanceList = withSuspense(React.lazy(() => import("../views/instance/InstanceList")));

const router = [
  {
    path: "/",
    element: <ServiceList />,
  },
  {
    path: "/finders/namespaces",
    element: <NamespaceList />
  },
  {
    path: "/finders/cluster/nodes",
    element: <ClusterNodeList />
  },
  {
    path: "/finders/services",
    element: <ServiceList />
  },
  {
    path: "/finders/:namespace/:serviceName/instances",
    element: <InstanceList />
  },
  {
    path: "*",
    element: <Navigate to={"/"} />
  }
];

const Router = () => useRoutes(router);

export default Router;

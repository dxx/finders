import React from "react";
import { useRoutes } from "react-router-dom";

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

const NotFound = () => (<div>请求的页面不存在！</div>)

const router = [
  {
    path: "/static/index.html",
    element: <ServiceList />,
  },
  {
    path: "/namespaces",
    element: <NamespaceList />
  },
  {
    path: "/cluster/nodes",
    element: <ClusterNodeList />
  },
  {
    path: "/services",
    element: <ServiceList />
  },
  {
    path: "/:namespace/:serviceName/instances",
    element: <InstanceList />
  },
  {
    path: "*",
    element: <NotFound />
  }
];

const Router = () => useRoutes(router);

export default Router;

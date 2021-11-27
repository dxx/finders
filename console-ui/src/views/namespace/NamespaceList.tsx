import React from "react";
import { Table, message } from "antd";
import { getNamespaces, NamespaceInfo } from "../../api/namespace";

const { useState, useEffect } = React;

const columns = [
  {
    title: "命名空间",
    dataIndex: "namespace",
    key: "namespace",
  },
  {
    title: "服务数量",
    dataIndex: "serviceCount",
    key: "serviceCount",
  }
];

function NamespaceList() {

  const [namespaces, setNamespaces] = useState([] as Array<NamespaceInfo>);

  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getNamespaces().then(data => {
      setNamespaces(data);
      setLoading(false);
    }).catch(e => {
      message.error("获取命名空间失败：" + e.message);
    });
  }, []);

  return (
    <>
      <Table
        rowKey={record => record.namespace}
        columns={columns}
        dataSource={namespaces}
        loading={loading}
        pagination={false} />
    </>
  );
}

export default NamespaceList;

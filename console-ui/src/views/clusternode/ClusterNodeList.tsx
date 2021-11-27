import React from "react";
import { Table, Tag, message } from "antd";
import { getClusterNodes, ClusterNodeInfo, NodeStatus } from "../../api/clusternode";

const { useState, useEffect } = React;

const columns = [
  {
    title: "节点 ID",
    dataIndex: "id",
    key: "id",
  },
  {
    title: "IP",
    dataIndex: "ip",
    key: "ip",
  },
  {
    title: "端口",
    dataIndex: "port",
    key: "port",
  },
  {
    title: "状态",
    dataIndex: "status",
    key: "status",
    render: (status: NodeStatus) => {
      let color = "green";
      if (status === NodeStatus.DOWN) {
        color = "red";
      }
      return (
        <Tag color={color}>
          {status}
        </Tag>
      )
    }
  }
];

function CluserNodeList() {
  const [clusterNodes, setClusterNodes] = useState([] as Array<ClusterNodeInfo>);

  const [loading, setLoading] = useState(true);
  
  useEffect(() => {
    getClusterNodes().then(data => {
      setClusterNodes(data);
      setLoading(false);
    }).catch(e => {
      message.error("获取集群节点失败：" + e.message);
    });
  }, []);

  return (
    <>
      <Table
        rowKey={record => record.id}
        columns={columns}
        dataSource={clusterNodes}
        loading={loading}
        pagination={false} />
    </>
  );
}

export default CluserNodeList;

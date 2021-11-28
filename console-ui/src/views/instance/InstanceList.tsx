import React from "react";
import { useParams } from "react-router-dom";
import { Table, Tag, Space, Modal, message } from  "antd";
import {
  getInstances,
  deregisterInstance,
  InstancesReqData,
  InstanceInfo,
  InstanceStatus
} from "../../api/instance";

import { ExclamationCircleOutlined } from "@ant-design/icons";

const { Column } = Table;

const { useState, useEffect } = React;

function InstanceList() {

  const { namespace, serviceName } = useParams();

  const [instances, setInstances] = useState([] as Array<InstanceInfo>);

  const [loading, setLoading] = useState(true);

  const deregister = (record: InstanceInfo) => {
    Modal.confirm({
      title: "你确定要删除吗？",
      icon: <ExclamationCircleOutlined />,
      okText: "确定",
      cancelText: "取消",
      onOk() {
        deregisterInstance({
          namespace,
          cluster: record.cluster,
          serviceName: record.serviceName,
          ip: record.ip,
          port: record.port,
        }).then(() => {
          fetchInstances({ namespace, serviceName: serviceName as string });
        }).catch(e => {
          message.error("删除实例失败：" + e.message);
        });
      }
    });
  }

  const fetchInstances = (param: InstancesReqData) => {
    getInstances(param).then(data => {
      setInstances(data.instanceList);
      setLoading(false);
    }).catch(e => {
      message.error("获取实例列表失败：" + e.message);
    });
  }

  useEffect(() => {
    fetchInstances({ namespace, serviceName: serviceName as string });
  }, [namespace, serviceName]);

  return (
    <>
      <Table
        rowKey={record => record.instanceId}
        dataSource={instances}
        loading={loading}
        pagination={false}>
        <Column title="实例 ID" dataIndex="instanceId" key="instanceId" />
        <Column title="集群" dataIndex="cluster" key="cluster" />
        <Column title="服务名" dataIndex="serviceName" key="serviceName" />
        <Column title="IP" dataIndex="ip" key="ip" />
        <Column title="端口" dataIndex="port" key="port" />
        <Column title="状态" dataIndex="status" key="status" render={(status: InstanceStatus)  =>  {
            let  color  =  "green";
            if (status === InstanceStatus.UN_HEALTHY) {
              color = "orange";
            } else if (status === InstanceStatus.DISABLE)  {
              color = "red";
            }
            return (
              <Tag color={color}>
                {status}
              </Tag>
            )
          }} />
        <Column title="操作" key="action" render={(text: any, record: InstanceInfo) => (
            <Space size="middle">
              {/* eslint-disable-next-line */}
              <a href="javascript:void(0)" onClick={() => { deregister(record) }}>删除实例</a>
            </Space>
          )} />
      </Table>
    </>
  );
}

export default InstanceList;

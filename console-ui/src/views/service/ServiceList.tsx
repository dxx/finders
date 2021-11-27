import React, { ChangeEvent } from "react";
import { Link } from "react-router-dom";
import { Button, Table, Select, Space, Input, message} from "antd";
import { SearchOutlined } from "@ant-design/icons";
import { getNamespaceNames } from "../../api/namespace";
import { getServices, ServicesReqData, ServiceInfo } from "../../api/service";

const { Column } = Table;

const { Option } = Select;

const { useState, useEffect } = React;

const defaultNamespace = "default";

const defaultPagination = {
  current: 1,
  pageSize: 20,
  defaultCurrent: 1,
  defaultPageSize: 20,
  showTotal: (total: number) => `共 ${total} 个服务`
};

function ServiceList() {

  const [namespaceNames, setNamespaceNames] = useState([] as Array<string>);

  const [services, setServices] = useState([] as Array<ServiceInfo>);

  const [pagination, setPagination] = useState(defaultPagination as any);

  const [namespace, setNamespace] = useState(defaultNamespace);

  const [serviceName, setServiceName] = useState("");

  const [loading, setLoading] = useState(true);

  const fetchServices = (param: ServicesReqData) => {
    getServices(param).then(data => {
      setServices(data.serviceList);
      setPagination({
        ...pagination,
        current: data.page,
        pageSize: data.size,
        total: data.count
      });
      setLoading(false);
    }).catch(e => {
      message.error("获取服务列表失败：" + e.message);
    });
  }

  const handleSelectChange = (value: string) => {
    setNamespace(value);
  }

  const handleInputChange = (e: ChangeEvent<HTMLInputElement>) => {
    setServiceName(e.currentTarget.value);
  }

  const handleButtonClick = () => {
    setLoading(true);
    fetchServices({page: pagination.current, size: pagination.pageSize, namespace, serviceName});
  }

  const handleTableChange = (pagination: any) => {
    setLoading(true);
    fetchServices({page: pagination.current, size: pagination.pageSize, namespace, serviceName});
  }

  useEffect(() => {
    getNamespaceNames().then(data => {
      setNamespaceNames(data);
    }).catch(e => {
      message.error("获取集群名称失败：" + e.message);
    });

    fetchServices({page: pagination.current, size: pagination.pageSize, namespace, serviceName});
  }, []);

  return (
    <>
      <div>
        <Select defaultValue={defaultNamespace} style={{ width: 120 }} onChange={handleSelectChange}>
          {
            namespaceNames.map((name, i) => {
              return (
                <Option value={name} key={i}>{name}</Option>
              );
            })
          }
        </Select>
        <Input placeholder="服务名" allowClear style={{ width: 150, marginLeft: 25 }} onChange={handleInputChange}/>
        <Button type="primary" icon={<SearchOutlined />} style={{ marginLeft: 50 }} onClick={handleButtonClick}>查询</Button>
      </div>
      <Table
        rowKey={record => record.serviceName}
        dataSource={services}
        pagination={pagination}
        loading={loading}
        onChange={handleTableChange}
        style={{ marginTop: 20 }}>
        <Column title="服务名" dataIndex="serviceName" key="serviceName" />
        <Column title="集群数量" dataIndex="clusterCount" key="clusterCount" />
        <Column title="实例数量" dataIndex="instanceCount" key="instanceCount" />
        <Column title="健康实例数量" dataIndex="healthyInstanceCount" key="healthyInstanceCount" />
        <Column title="操作" key="action" render={(text: any, record: ServiceInfo) => (
          <Space size="middle">
            <Link to={`/${namespace}/${record.serviceName}/instances`}>实例列表</Link>
          </Space>
        )} />
      </Table>
    </>
  );
}

export default ServiceList;

import React from "react";
import { BrowserRouter, Link } from "react-router-dom";
import { Layout, Menu } from 'antd';
import Router from "./router";
import "./App.css";
import "antd/dist/antd.css";

import {
  GithubOutlined,
  BarsOutlined,
  CloudOutlined,
  ApartmentOutlined,
} from "@ant-design/icons";

const { Header, Content, Sider } = Layout;

function App() {
  return (
    <BrowserRouter>
      <Layout className="app">
        <Header className="header">
          <span className="logo">Finders</span>
          <a className="github" href="https://github.com/dxx/finders" target="_blank" rel="noreferrer"><GithubOutlined/></a>
        </Header>
        <Sider width={200} className="sider" style={{ position: 'fixed' }}>
          <Menu
            mode="inline"
            defaultSelectedKeys={['1']}
            style={{ height: '100%', borderRight: 0 }}
          >
            <Menu.Item key="1" icon={<BarsOutlined />}><Link to="/services">服务列表</Link></Menu.Item>
            <Menu.Item key="2" icon={<CloudOutlined />}><Link to="/namespaces">命名空间</Link></Menu.Item>
            <Menu.Item key="3" icon={<ApartmentOutlined />}><Link to="/cluster/nodes">集群节点</Link></Menu.Item>
          </Menu>
        </Sider>
        <Layout className="main-layout">
          <Content className="main-content" style={{ minHeight: 'auto' }}>
            <Router />
          </Content>
        </Layout>
      </Layout>
    </BrowserRouter>
  );
}

export default App;

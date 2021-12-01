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

const menus = [
  {
    key: "services",
    path: "/services",
    name: "服务列表",
    icon: <BarsOutlined />
  },
  {
    key: "namespaces",
    path: "/namespaces",
    name: "命名空间",
    icon: <CloudOutlined />
  },
  {
    key: "nodes",
    path: "/cluster/nodes",
    name: "集群节点",
    icon: <ApartmentOutlined />
  }
];

function App() {
  let defaultKey = menus[0].key;
  let path = menus.find(item => item.path === window.location.pathname);
  if (path) {
    defaultKey = path.key;
  }
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
            defaultSelectedKeys={[defaultKey]}
            style={{ height: '100%', borderRight: 0 }}
          >
            {
              menus.map(menu => {
                return (
                  <Menu.Item key={menu.key} icon={menu.icon}><Link to={menu.path}>{menu.name}</Link></Menu.Item>
                );
              })
            }
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

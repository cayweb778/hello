// 总账
import { createMenu, createModel} from '/@/../pages/rbac/utils/mockMenuUtil';
import {PLATFORM_DevComponents, usePlatform} from '../platforms/platformMenus';

export const platformId = 1013;

function useId(num) {
  return platformId * 1000 + num
}

function createPlatformMenu(params) {
  return createMenu({...params, platformId})
}


export const menus = [
  createModel({
    id: platformId,
    isCloud: false,
    isTargetBlank: false,
    isOutLink: false,
    name: '开发模块库',
    category: 102,
    sortNo: 6,
  }),
  createPlatformMenu({
    id: 9901,
    path: '/',
    redirect: '/devComponents/home/welcome',
    component: 'LAYOUT',
    hidden: true,
    parentId: 0,
    name: '根目录'
  }),
  createPlatformMenu({
    id: useId(300),
    path: '/devComponents/home',
    component: 'LAYOUT',
    parentId: 0,
    name: '工作台'
  }),
  createPlatformMenu({
    id: useId(301),
    path: 'welcome',
    component: '/dashboard/analysis/index',
    parentId: useId(300),
    name: '首页'
  }),

  createPlatformMenu({
    id: useId(100),
    path: '/devComponents/pingzheng',
    component: 'Layout',
    parentId: 0,
    name: '凭证'
  }),
  createPlatformMenu({
    id: useId(101),
    path: 'fillIn',
    component: '/boozsoft/autosystem/pingzheng-fillin',
    parentId: useId(100),
    name: '填制凭证'
  }),

  createPlatformMenu({
    id: useId(200),
    path: '/devComponents/boozsoft/autosystem',
    component: '/dashboard/analysis/index',
    parentId: 0,
    name: '自动化系统'
  }),
  createPlatformMenu({
    id: useId(201),
    path: 'tableDesign',
    component: '/boozsoft/autosystem/table-design',
    parentId: useId(200),
    name: '表单设计'
  }),
  createPlatformMenu({
    id: useId(202),
    path: 'recordDesign',
    component: '/boozsoft/autosystem/record-design',
    parentId: useId(200),
    name: '档案设计'
  }),
  createPlatformMenu({
    id: useId(203),
    path: 'recordApi',
    component: '/boozsoft/autosystem/record-design',
    parentId: useId(200),
    name: '生成档案API'
  }),
  createPlatformMenu({
    id: useId(204),
    path: 'liucheng',
    component: '/boozsoft/autosystem/liucheng',
    parentId: useId(200),
    name: '流程管理'
  }),

  createPlatformMenu({
    id: useId(400),
    path: '/devComponents/devUser',
    component: '/boozsoft/autosystem/record-design',
    parentId: 0,
    name: '开发用户管理'
  }),
  createPlatformMenu({
    id: useId(401),
    path: 'menu',
    component: '/boozsoft/autosystem/record-design',
    parentId: useId(400),
    name: '用户菜单与权限'
  }),
  createPlatformMenu({
    id: useId(402),
    path: 'data',
    component: '/boozsoft/autosystem/record-design',
    parentId: useId(400),
    name: '数据权限'
  }),
  createPlatformMenu({
    id: useId(403),
    path: 'role',
    component: '/boozsoft/autosystem/record-design',
    parentId: useId(400),
    name: '用户角色'
  }),
  createPlatformMenu({
    id: useId(404),
    path: 'info',
    component: '/boozsoft/autosystem/record-design',
    parentId: useId(400),
    name: '关联的用户信息列表'
  }),

  createPlatformMenu({
    id: useId(500),
    path: 'aaaaa4',
    component: '/boozsoft/system/selfPemission/index',
    parentId: 0,
    name: '个人权限查看'
  }),
  createPlatformMenu({
    id: useId(600),
    path: 'aaaaa5',
    component: '/boozsoft/system/devUser/index',
    parentId: 0,
    name: '切换开发账号'
  }),
];

export const platformMenuIds = {
  key: 'devComponents',
  id: platformId,
  sort:18,
  menuIds: []
  // menuIds: menus.map(item => item.id)
};

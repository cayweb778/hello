import {
  usePlatform
} from '../platforms/platformMenus';
import {PLATFORM_NCDESKTOP} from '../platforms/platformMenus';
import { createMenu, createModel} from '/@/../pages/rbac/utils/mockMenuUtil';

const platformId = 20006

function createPlatformMenu(params) {
  return createMenu({...params, platformId})
}
export const menus = [
  createModel({
    id: platformId,
    isCloud: false,
    isTargetBlank: false,
    isOutLink: false,
    name: 'NC桌面',
    category: 106,
    sortNo: 100,
  }),
  createPlatformMenu({
    id: 131000,
    path: '/',
    redirect:'/ncDesktop/home/welcome',
    component: 'LAYOUT',
    parentId: 0,
    hidden:true,
    name: '根目录'
  }),
  createPlatformMenu({
    id: 130000,
    path: '/ncDesktop/home',
    component: 'LAYOUT',
    parentId: 0,
    name: '工作台'
  }),
  createPlatformMenu({
    id: 130001,
    path: 'welcome',
    component: '/dashboard/analysis/index',
    parentId: 130000,
    name: '首页',
  }),
/*  createPlatformMenu({
    id: 1300000,
    path: 'welcome',
    component: '/platforms/account/home/index',
    parentId: 0,
    name: '首页',
  }),*/
  createPlatformMenu({
    id: 130010,
    path: '/ncDesktop/mall',
    component: 'LAYOUT',
    parentId: 0,
    name: '应用商城'
  }),
  createPlatformMenu({
    id: 130020,
    path: '/ncDesktop/study',
    component: 'LAYOUT',
    parentId: 0,
    name: '操作学习'
  }),
  createPlatformMenu({
    id: 130030,
    path: '/ncDesktop/serving',
    component: 'LAYOUT',
    parentId: 0,
    name: '获取服务'
  }),
  createPlatformMenu({
    id: 130040,
    path: '/ncDesktop/message',
    component: 'LAYOUT',
    parentId: 0,
    name: '消息任务'
  }),
  createPlatformMenu({
    id: 130041,
    path: 'message-list',
    component: '/boozsoft/system/message/index',
    parentId: 130040,
    name: '消息公告'
  }),
  createPlatformMenu({
    id: 130042,
    path: 'task-list',
    component: '/boozsoft/system/sys-task/index',
    parentId: 130040,
    name: '任务协作'
  }),

  createPlatformMenu({
    id: 130011,
    path: 'use-app',
    component: '/boozsoft/system/blocktable/index',
    parentId: 130010,
    name: '已使用应用'
  }),
  createPlatformMenu({
    id: 130012,
    path: 'save-app',
    component: '/boozsoft/system/blocktable/index',
    parentId: 130010,
    name: '添加新应用'
  }),
  createPlatformMenu({
    id: 130013,
    path: 'online-renewal',
    component: '/boozsoft/system/blocktable/index',
    parentId: 130010,
    name: '在线续费'
  }),

  createPlatformMenu({
    id: 130021,
    path: 'internet-video',
    component: '/boozsoft/system/blocktable/index',
    parentId: 130020,
    name: '教学视频'
  }),
  createPlatformMenu({
    id: 130022,
    path: 'doc-file',
    component: '/boozsoft/system/blocktable/index',
    parentId: 130020,
    name: '操作文档'
  }),

  createPlatformMenu({
    id: 1300301,
    path: 'online-service',
    component: '/boozsoft/system/blocktable/index',
    parentId: 130030,
    name: '联系在线客服'
  }),
  createPlatformMenu({
    id: 1300302,
    path: 'submit-ticket',
    component: '/boozsoft/system/blocktable/index',
    parentId: 130030,
    name: '提交工单'
  }),
  createPlatformMenu({
    id: 1300303,
    path: 'internet-demo',
    component: '/boozsoft/system/blocktable/index',
    parentId: 130030,
    name: '安排演示'
  }),
  createPlatformMenu({
    id: 1300304,
    path: 'software-upgrade',
    component: '/boozsoft/system/blocktable/index',
    parentId: 130030,
    name: '软件升级'
  }),
];

export const platformMenuIds = {
  key:'ncDesktop',
  id: platformId,
  menuIds: menus.map(item => item.id)
};


// 总账
import { createMenu, createModel} from '/@/../pages/rbac/utils/mockMenuUtil';

export const platformId = 10133300;

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
    name: '操作日志',
    category: 101,
    sortNo: 10,
  }),
  createPlatformMenu({
    id: 9901152,
    path: '/',
    redirect: '/log/home/welcome',
    component: 'LAYOUT',
    hidden: true,
    parentId: 0,
    name: '根目录'
  }),
  createPlatformMenu({
    id: useId(300),
    path: '/log/home',
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
];

export const platformMenuIds = {
  key: 'log',
  id: platformId,
  sort:12,
  menuIds: menus.map(item => item.id)
};

// 总账
import { toRoleMenuList } from '../../utils/_sys_role_menu_util';
import { createMenu, createModel} from '/@/../pages/rbac/utils/mockMenuUtil';
import {
  PLATFORM_DevComponents,
  PLATFORM_FOUR,
  PLATFORM_ZHONGZHANG,
  usePlatform
} from '../platforms/platformMenus';
const platformId = 20010;
function createPlatformMenu(params){
  return createMenu({...params,platformId})
}
export const menus = [
  createModel({
    id: platformId,
    isCloud: false,
    isTargetBlank: false,
    isOutLink: false,
    name: '供应商',
    category: 102,
    sortNo: 100,
  }),
  createPlatformMenu({
    id: 9903,
    path: '/',
    redirect:'/four/home/welcome',
    component: 'LAYOUT',
    parentId: 0,
    hidden:true,
    name: '根目录'
  }),
  createPlatformMenu({ id: 410000, path: '/four/home', component: 'LAYOUT', parentId: 0, name: '工作台' }),
  createPlatformMenu({
    id: 410001,
    path: 'welcome',
    component: '/dashboard/analysis/index',
    parentId: 410000,
    name: '首页',
  }),
];


export const platformMenuIds = {
  key:'four',
  sort:15,
  id:platformId,
  menuIds:menus.map(item=>item.id)
};


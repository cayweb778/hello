// 总账
import { toRoleMenuList } from '../../utils/_sys_role_menu_util';
import { createMenu, createModel} from '/@/../pages/rbac/utils/mockMenuUtil';
import {
  PLATFORM_DevComponents,
  PLATFORM_THREE,
  PLATFORM_ZHONGZHANG,
  usePlatform
} from '../platforms/platformMenus';
const platformId = 20009;
function createPlatformMenu(params){
  return createMenu({...params,platformId})
}

export const menus = [
  createModel({
    id: platformId,
    isCloud: false,
    isTargetBlank: false,
    isOutLink: false,
    name: '客户',
    category: 102,
    sortNo: 100,
  }),
  createPlatformMenu({
    id: 9907,
    path: '/',
    redirect:'/three/home/welcome',
    component: 'LAYOUT',
    parentId: 0,
    hidden:true,
    name: '根目录'
  }),
  createPlatformMenu({ id: 310000, path: '/three/home', component: 'LAYOUT', parentId: 0, name: '工作台' }),
  createPlatformMenu({
    id: 310001,
    path: 'welcome',
    component: '/dashboard/analysis/index',
    parentId: 310000,
    name: '首页',
  }),
];


export const platformMenuIds = {
  key:'three',
  id:platformId,
  sort:17,
  menuIds:menus.map(item=>item.id)
};


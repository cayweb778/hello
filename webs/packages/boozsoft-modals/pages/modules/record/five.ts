// 总账
import { createMenu, createModel} from '/@/../pages/rbac/utils/mockMenuUtil';
import {
  PLATFORM_DevComponents, PLATFORM_FIVE,
  PLATFORM_FOUR,
  PLATFORM_ZHONGZHANG,
  usePlatform
} from '../platforms/platformMenus';
const platformId = 20011;
function createPlatformMenu(params){
  return createMenu({...params,platformId})
}
export const menus = [
  createModel({
    id: platformId,
    isCloud: false,
    isTargetBlank: false,
    isOutLink: false,
    name: 'five',
    category: 102,
    sortNo: 100,
  }),
  createPlatformMenu({
    id: 9902,
    path: '/',
    redirect:'/five/home/welcome',
    component: 'LAYOUT',
    parentId: 0,
    hidden:true,
    name: '根目录'
  }),
  createPlatformMenu({ id: 510000, path: '/five/home', component: 'LAYOUT', parentId: 0, name: '工作台' }),
  createPlatformMenu({
    id: 510001,
    path: 'welcome',
    component: '/boozsoft/layouts/platforms/five/home/index',
    parentId: 510000,
    name: 'welcome',
  }),
  createPlatformMenu({
    id: 510100,
    path: 'one',
    component: '/boozsoft/layouts/platforms/five/one',
    parentId: 510000,
    name: 'one',
  }),
  createPlatformMenu({
    id: 510200,
    path: 'two',
    component: '/boozsoft/layouts/platforms/five/two',
    parentId: 510000,
    name: 'two',
  }),
  createPlatformMenu({
    id: 510300,
    path: 'three',
    component: '/boozsoft/layouts/platforms/five/three',
    parentId: 510000,
    name: 'three',
  }),
  createPlatformMenu({
    id: 510400,
    path: 'four',
    component: '/boozsoft/layouts/platforms/five/four',
    parentId: 510000,
    name: 'four',
  }),
  createPlatformMenu({
    id: 510500,
    path: 'five',
    component: '/boozsoft/layouts/platforms/five/five',
    parentId: 510000,
    name: 'five',
  }),
  createPlatformMenu({
    id: 510600,
    path: 'six',
    component: '/boozsoft/layouts/platforms/five/six',
    parentId: 510000,
    name: 'six',
  }),

];


export const platformMenuIds = {
  id:platformId,
  sort:14,
  menuIds:menus.map(item=>item.id)
};


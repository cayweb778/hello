// 总账
import { toRoleMenuList } from '../../utils/_sys_role_menu_util';
import { createMenu, createModel} from '/@/../pages/rbac/utils/mockMenuUtil';
const platformId = 20008;
function createPlatformMenu(params){
  return createMenu({...params,platformId})
}
export const menus = [
  createModel({
    id: platformId,
    isCloud: false,
    isTargetBlank: false,
    isOutLink: false,
    name: '机构',
    category: 102,
    sortNo: 100,
  }),
  createPlatformMenu({
    id: 9908,
    path: '/',
    redirect:'/two/home/welcome',
    component: 'LAYOUT',
    parentId: 0,
    hidden:true,
    name: '根目录'
  }),
  createPlatformMenu({ id: 210000, path: '/two/home', component: 'LAYOUT', parentId: 0, name: '工作台' }),
  createPlatformMenu({
    id: 210001,
    path: 'welcome',
    component: '/dashboard/analysis/index',
    parentId: 210000,
    name: '首页',
  }),
];


export const platformMenuIds = {
  key:'two',
  id:platformId,
  sort:13,
  menuIds:menus.map(item=>item.id)
};


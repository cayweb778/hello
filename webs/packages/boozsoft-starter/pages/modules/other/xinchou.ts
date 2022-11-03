import { createMenu, createModel} from '/@/../pages/rbac/utils/mockMenuUtil';
import {
  PLATFORM_CAISHUIDA, PLATFORM_XINCHOU, usePlatform
} from '../../platforms/platformMenus';
const platformId=20013
export const menus = [
  createModel({
    id: platformId,
    isCloud: false,
    isTargetBlank: false,
    isOutLink: true,
    name: '薪酬管理',
    category: 102,
    sortNo: 100,
  }),
];

export const platformMenuIds = {
  key:'xinchou',
  id:platformId,
  sort:5,
  menuIds:menus.map(item=>item.id)
};


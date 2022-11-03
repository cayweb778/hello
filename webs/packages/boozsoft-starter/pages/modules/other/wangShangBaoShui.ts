import { createMenu, createModel} from '/@/../pages/rbac/utils/mockMenuUtil';
import {
  PLATFORM_CAISHUIDA, PLATFORM_GUDINGZICHAN, PLATFORM_WANGSHANGBAOSHUI, usePlatform
} from '../../platforms/platformMenus';
const platformId=20005
export const menus = [
  createModel({
    id: platformId,
    isCloud: true,
    isTargetBlank: false,
    isOutLink: true,
    name: '网上报销',
    category: 102,
    sortNo: 500,
  }),
];

export const platformMenuIds = {
  key:'wangShangBaoShui',
  id:platformId,
  sort:4,
  menuIds:menus.map(item=>item.id)
};


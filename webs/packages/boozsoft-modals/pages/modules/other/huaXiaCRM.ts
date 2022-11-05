import { createMenu, createModel} from '/@/../pages/rbac/utils/mockMenuUtil';
import {
  PLATFORM_CAISHUIDA, PLATFORM_HUAXIACRM, usePlatform
} from '../../platforms/platformMenus';
const platformId=20002
export const menus = [
  createModel({
    id: platformId,
    isCloud: false,
    isTargetBlank: false,
    isOutLink: true,
    name: '华夏CRM',
    category: 102,
    sortNo: 500,
  })
  ,
];

export const platformMenuIds = {
  id:platformId,
  menuIds:menus.map(item=>item.id)
};


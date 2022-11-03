import { createMenu, createModel} from '/@/../pages/rbac/utils/mockMenuUtil';


const platformId = 20003
export const menus = [
  createModel({
    id: platformId,
    isCloud: false,
    isTargetBlank: false,
    isOutLink: false,
    name: '电子票据',
    category: 102,
    sortNo: 500,
  }),
];

export const platformMenuIds = {
  key: 'dianZiPiaoJu',
  id: platformId,
  sort:10,
  menuIds: []
  // menuIds: menus.map(item => item.id)
};


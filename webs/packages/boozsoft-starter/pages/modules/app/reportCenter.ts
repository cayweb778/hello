// 总账
import { createMenu, createModel} from '/@/../pages/rbac/utils/mockMenuUtil';

export const platformId = 10131200;

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
    category: 102,
    isTargetBlank: false,
    isOutLink: false,
    name: '报表中心',
    sortNo: 3,
    path:'log'
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

  createPlatformMenu({ id: 910001, path: '/report/shuwu', component: 'LAYOUT', parentId: 0, name: '税务报表' }),
  createPlatformMenu({ id: 910002, path: '/report/guanli', component: 'LAYOUT', parentId: 0, name: '管理报表' }),
  createPlatformMenu({ id: 910003, path: '/report/juesuan', component: 'LAYOUT', parentId: 0, name: '决算报表' }),
  createPlatformMenu({ id: 910004, path: '/report/template', component: 'LAYOUT', parentId: 0, name: '模板设计' }),
  // createPlatformMenu({ id: 910005, path: '/report/setting', component: 'LAYOUT', parentId: 0, name: '设置' }),

  createPlatformMenu({id:910011,path:'zcfzb',component:'/boozsoft/system/report-zcfzb/index',parentId:910001,name:'资产负债表'}),
  createPlatformMenu({id:910012,path:'lrb',component:'/boozsoft/system/report-lrb/index',parentId:910001,name:'利润表'}),
  createPlatformMenu({id:910013,path:'xjllb',component:'/boozsoft/system/report-xjllb/index',parentId:910001,name:'现金流量表'}),
  createPlatformMenu({id:910014,path:'srzcb',component:'/boozsoft/system/report-zcfzb/index',parentId:910001,name:'收入支出表'}),

  createPlatformMenu({id:910021,path:'fyfxb',component:'/boozsoft/system/report-zcfzb/index',parentId:910002,name:'费用分析表'}),
  createPlatformMenu({id:910022,path:'srqxb',component:'/boozsoft/system/report-zcfzb/index',parentId:910002,name:'收入曲线表'}),
  createPlatformMenu({id:910023,path:'ambjybb',component:'/boozsoft/system/report-zcfzb/index',parentId:910002,name:'阿米经营巴报表'}),

  createPlatformMenu({id:910031,path:'zcjsb',component:'/boozsoft/system/report-zcfzb/index',parentId:910003,name:'支出决算表'}),
  createPlatformMenu({id:910032,path:'srjsb',component:'/boozsoft/system/report-zcfzb/index',parentId:910003,name:'收入决算表'}),
  createPlatformMenu({id:910033,path:'czbksrzcjsb',component:'/boozsoft/system/report-zcfzb/index',parentId:910003,name:'财政拨款收入支出决算表'}),
  createPlatformMenu({id:910034,path:'srzcjszb',component:'/boozsoft/system/report-zcfzb/index',parentId:910003,name:'收入支出决算总表'}),

  createPlatformMenu({id:910041,path:'system',component:'/boozsoft/system/report-template-system/index',parentId:910004,name:'系统模板'}),
  createPlatformMenu({id:910042,path:'custom',component:'/boozsoft/system/report-template-custom/index',parentId:910004,name:'自定义模板'}),
];

export const platformMenuIds = {
  key: 'reportCenter',
  id: platformId,
  sort:3,
  menuIds: menus.map(item => item.id)
};

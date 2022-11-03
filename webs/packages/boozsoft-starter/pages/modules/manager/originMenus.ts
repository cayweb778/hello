
// @ts-ignore
import { createMenu, createModel} from '/@/../pages/rbac/utils/mockMenuUtil';
const platformId=1014
let i=1
function createPlatformMenu(params){
  return createMenu({...params,platformId})
}
export const menus = [

  createModel({
    id: platformId,
    isCloud: false,
    isTargetBlank: false,
    isOutLink: false,
    name: '组织管理',
    category: 101,
    sortNo: 7,
  }),
  createPlatformMenu({
    id: 9905,
    path: '/',
    redirect:'/origin/home/welcome',
    component: 'LAYOUT',
    hidden:true,
    parentId: 0,
    name: '根目录'
  }),
  createPlatformMenu({
    id: 52001,
    path: 'welcome',
    component: '/dashboard/analysis/index',
    parentId: 50100,
    name: '首页',
  }),
  createPlatformMenu({ id: 50100, path: '/origin/home', component: 'LAYOUT', parentId: 0, name: '工作台' }),

  createPlatformMenu({ id: 50000, path: '/origin/institution-management', component: 'Layout', parentId: 0, name: '机构' }),
  createPlatformMenu({ id: 50001, path: '/origin/create-account', component: 'Layout', parentId: 0, name: '建账' }),
  createPlatformMenu({ id: 50002, path: '/origin/auth-management', component: 'Layout', parentId: 0, name: '权限' }),
  createPlatformMenu({ id: 50004, path: '/origin/focus-data', component: 'Layout', parentId: 0, name: '公共数据' }),
  createPlatformMenu({ id: 50300, path: '/origin/master-data', component: 'Layout', parentId: 0, name: '主数据' }),
  createPlatformMenu({ id: 50005, path: '/origin/accountancy', component: 'Layout', parentId: 0, name: '财务会计' }),
/*
  createPlatformMenu({ id: 50009, path: '/origin/budget-accountancy', component: 'Layout', parentId: 0, name: '预算会计' }),
*/
  createPlatformMenu({ id: 50500, path: '/origin/report', component: 'Layout', parentId: 0, name: '报表模板' }),

  createPlatformMenu({ id: 50006, path: '/origin/joint', component: 'Layout', parentId: 0, name: '协同' }),
  createPlatformMenu({ id: 50007, path: '/origin/merge-data', component: 'Layout', parentId: 0, name: '合并报表' }),
  createPlatformMenu({ id: 50008, path: '/origin/pram', component: 'Layout', parentId: 0, name: '参数' }),

  createPlatformMenu({id:5010+i++,path:'ca-account',component:'/boozsoft/management/im-unit-two/index',parentId:50001,name:'财务会计账套'}),
  createPlatformMenu({id:5010+i++,path:'ca-fixed-assets',component:'/boozsoft/management/im-unit-three/index',parentId:50001,name:'固定资产账套'}),
  createPlatformMenu({id:5010+i++,path:'ca-stock',component:'/boozsoft/management/im-unit-four/index',parentId:50001,name:'存货管理账套'}),
  createPlatformMenu({
    id: 50402,
    path: 'im-organize',
    component: '/boozsoft/management/im-organize/index',
    parentId: 50000,
    name: '组织',
  }),
  createPlatformMenu({
    id: 50403,
    path: 'im-unit',
    component: '/boozsoft/management/im-unit/index',
    parentId: 50000,
    name: '公司',
  }),
  createPlatformMenu({
    id: 50404,
    path: 'im-factory',
    component: '/boozsoft/management/im-factory/index',
    parentId: 50000,
    name: '工厂',
  }),
/*  createPlatformMenu({
    id: 50405,
    path: 'im-sale',
    component: '/boozsoft/management/im-sale/index',
    parentId: 50000,
    name: '业务组',
  }),*/

/*  createPlatformMenu({id: 5010+i++, path: 'opearManager', component: '/boozsoft/system/user/index', parentId: 50002, name: '操作员',}),
  createPlatformMenu({ id: 5010+i++, path: 'role',  component: '/boozsoft/system/system/role/index', parentId: 50002, name: '角色' }),*/
  createPlatformMenu({ id: 5010+i++, path: 'operation-author',  component: '/boozsoft/system/operation-author/index', parentId: 50002, name: '操作权限设置' }),
  createPlatformMenu({id: 5010+i++, path: 'role-author',  component: '/boozsoft/system/role-author/index', parentId: 50002, name: '角色权限设置' }),
  createPlatformMenu({id: 5010+i++, path: 'archives-author',  component: '/boozsoft/system/archives-author/index', parentId: 50002, name: '档案审批授权' }),
  createPlatformMenu({id: 5010+i++, path: 'data-author',  component: '/boozsoft/system/data-author-control/index', parentId: 50002, name: '数据权限控制' }),
  // createPlatformMenu({ id: 5010+i++, path: 'menu',  component: '/boozsoft/system/system/menu/index', parentId: 50002, name: '菜单权限' }),
  createPlatformMenu({ id: 5010+i++, path: 'data',  component: '/boozsoft/system/system/data-author/index', parentId: 50002, name: '数据权限分配' }),
  createPlatformMenu({id: 5010+i++, path: 'im-sale', component: '/boozsoft/system/blocktable/index', parentId: 50004, name: '公司参数',}),
  createPlatformMenu({id:5010+i++,   path: 'UsedForeignCurrency', component: '/boozsoft/organize/UsedForeignCurrency/index',parentId:50004,name:'常用外币'}),
  createPlatformMenu({id:5010+i++,path:'894213',component:'/boozsoft/system/blocktable/index',parentId:50004,name:'外币汇率'}),
  createPlatformMenu({id:5010+i++,path:'sett-modes',component:'/boozsoft/system/sett-modes/index',parentId:50004,name:'结算方式'}),
  createPlatformMenu({id:5010+i++,path:'org-task',component:'/boozsoft/system/task/org-index',parentId:50004,name:'操作锁定管理'}),

  createPlatformMenu({id: 50310, path: '/user', component: '', parentId: 50300, name: '人员' }),
  createPlatformMenu({id: 50311, path: 'originPsn',component:'/boozsoft/origin/psn/index',componentName: 'OriginPsn',parentId:50310,name:'人员信息'}),
  createPlatformMenu({id: 50312, path: 'originPsnType',component:'/boozsoft/origin/psn-type/index',componentName: 'OriginPsnType',parentId:50310,name:'人员分类'}),

  createPlatformMenu({id: 50410, path: '/cus', component: '', parentId: 50300, name: '客户' }),
  createPlatformMenu({id: 50411, path: 'originCustomer',component:'/boozsoft/origin/customer_info/index',componentName: 'originCustomer',parentId:50410,name:'客户信息'}),
  createPlatformMenu({id: 50412, path: 'originCustomerClass',component:'/boozsoft/origin/customer_class/index',componentName: 'originCustomerClass',parentId:50410,name:'客户分类'}),

  createPlatformMenu({id: 50420, path: '/supplier', component: '', parentId: 50300, name: '供应商' }),
  createPlatformMenu({id: 50421, path: 'originSupplier',component:'/boozsoft/origin/supplier_info/index',componentName: 'originSupplier',parentId:50420,name:'供应商信息'}),
  createPlatformMenu({id: 50422, path: 'originSupClass',component:'/boozsoft/origin/sup_class/index',componentName: 'originSupClass',parentId:50420,name:'供应商分类'}),

  createPlatformMenu({id: 50320, path: '/project', component: '', parentId: 50300, name: '项目' }),
  createPlatformMenu({id: 50321, path: 'origin-project-item', component: '/boozsoft/origin/project-item/index',componentName: 'OriginProjectItem', parentId: 50320, name: '项目大类'}),
  createPlatformMenu({id: 50322, path: 'origin-project-category', component: '/boozsoft/origin/project-category/index',componentName: 'OriginProjectCate', parentId: 50320, name: '项目样式'}),
  createPlatformMenu({id: 50323, path: 'origin-project', component: '/boozsoft/origin/project/index',componentName: 'OriginProject', parentId: 50320, name: '项目目录'}),
  createPlatformMenu({id: 50324, path: 'origin-project-class', component: '/boozsoft/origin/project-class/index',componentName: 'OriginProjectClass', parentId: 50320, name: '项目分类'}),

  createPlatformMenu({id: 50330, path: '/defined', component: '', parentId: 50300, name: '自定义项' }),
  // createPlatformMenu({id: 50331, path: 'origin-defined-file',component:'/boozsoft/origin/defined-file/index',componentName: 'OriginDefinedFile',parentId:50330,name:'自定义项设置'}),
  createPlatformMenu({id: 50332, path: 'origin-defined-record',component:'/boozsoft/origin/defined-record/index',componentName: 'OriginDefinedRecord',parentId:50330,name:'自定义项'}),
  // createPlatformMenu({id: 50333, path: 'origin-fuzhu-hesuan',component:'/boozsoft/origin/fuzhu-hesuan/index',componentName: 'OriginFuzhuHesuan',parentId:50330,name:'辅助核算定义'}),

  createPlatformMenu({id:5010+i++, path: 'organization-period', component: '/boozsoft/organize/iperiod/index', parentId: 50005, name: '会计期间',}),
  createPlatformMenu({id:5010+i++, path: 'organization-template', component: '/boozsoft/system/acctemplate/organization_index', parentId: 50005, name: '组织会计制度',}),
  createPlatformMenu({id:5010+i++, path: 'organization-code', component: '/boozsoft/system/acccode/organization_index', parentId: 50005, name: '组织会计科目',hidden:true,}),
  createPlatformMenu({id:5010+i++, path: 'origin-voucher-type',component: '/boozsoft/origin/voucher-type/index',parentId:50005,name:'凭证类别'}),

  createPlatformMenu({id:5010+i++, path: 'project-cash', component: '/boozsoft/origin/project-cash/index', parentId: 50005, name: '现金流量项目',}),

  // createPlatformMenu({id:50600, path: '/system/defined', component: '', parentId: 50005, name: '自定义项' }),
  // createPlatformMenu({id: 5010+i++, path: 'origin-defined-file',component:'/boozsoft/origin/defined-file/index',componentName: 'OriginDefinedFile',parentId:50600,name:'自定义项设置'}),
  // createPlatformMenu({id: 5010+i++, path: 'origin-defined-record',component:'/boozsoft/origin/defined-record/index',componentName: 'OriginDefinedRecord',parentId:50600,name:'自定义项档案'}),
  // createPlatformMenu({id: 5010+i++, path: 'origin-fuzhu-hesuan',component:'/boozsoft/origin/fuzhu-hesuan/index',componentName: 'OriginFuzhuHesuan',parentId:50600,name:'辅助核算定义'}),

  createPlatformMenu({ id: 50501, path: 'report-zcfzb', component: '/boozsoft/origin/report/report-zcfzb/index',componentName:'OriginZCFZB', parentId: 50500, name: '资产负债表' }),
  createPlatformMenu({ id: 50502, path: 'report-lrb', component: '/boozsoft/origin/report/report-lrb/index',componentName:'OriginLRB', parentId: 50500, name: '利润表' }),
  createPlatformMenu({ id: 50503, path: 'report-xjllb', component: '/boozsoft/origin/report/report-xjllb/index',componentName:'OriginXJLLB', parentId: 50500, name: '现金流量表' }),
  createPlatformMenu({ id: 50504, path: 'report-srfyb', component: '/boozsoft/origin/report/report-srfyb/index',componentName:'OriginSRFYB', parentId: 50500, name: '收入费用表' }),
  createPlatformMenu({ id: 50505, path: 'report-yssrzcb', component: '/boozsoft/origin/report/report-yssrzcb/index',componentName:'OriginYSSRZCB', parentId: 50500, name: '预算收入支出表' }),
  createPlatformMenu({ id: 50506, path: 'report-ysjzjybdb', component: '/boozsoft/origin/report/report-ysjzjybdb/index',componentName:'OriginYSJZJYBDB', parentId: 50500, name: '预算结转结余变动表' }),
  createPlatformMenu({ id: 50507, path: 'report-czbkyssrzcb', component: '/boozsoft/origin/report/report-czbkyssrzcb/index',componentName:'OriginCZBKYSSRZCB', parentId: 50500, name: '财政拨款预算收入支出表' }),
  createPlatformMenu({ id: 50508, path: 'report-jzcbdb', component: '/boozsoft/origin/report/report-jzcbdb/index',componentName:'OriginJZCBDB', parentId: 50500, name: '净资产变动表' }),

  createPlatformMenu({id:5010+i++,path:'credential-parm',component:'/boozsoft/system/blocktable/index',parentId:50006,name:'协同中心管理'}),
  createPlatformMenu({id:5010+i++,path:'credential-record',component:'/boozsoft/system/blocktable/index',parentId:50006,name:'协同科目'}),
  createPlatformMenu({id:5010+i++,path:'credential-record',component:'/boozsoft/system/blocktable/index',parentId:50006,name:'协同单位'}),


  createPlatformMenu({id:5010+i++,path:'transaction-setting',component:'/boozsoft/system/blocktable/index',parentId:50007,name:'抵消分类设置'}),
  createPlatformMenu({id:5010+i++,path:'transaction-zctable',component:'/boozsoft/system/blocktable/index',parentId:50007,name:'合并资产负债表'}),
  createPlatformMenu({id:5010+i++,path:'transaction-sytable',component:'/boozsoft/system/blocktable/index',parentId:50007,name:'合并损益表'}),
  createPlatformMenu({id:5010+i++,path:'transaction-xjtable',component:'/boozsoft/system/blocktable/index',parentId:50007,name:'合并现金流量表'}),
  createPlatformMenu({id:5010+i++,path:'transaction-cwtable',component:'/boozsoft/system/blocktable/index',parentId:50007,name:'合并财务状况变动表'}),

  createPlatformMenu({id:5010+i++,path:'control-parm',component:'/boozsoft/system/blocktable/index',parentId:50008,name:'控制参数'}),
];

export const platformMenuIds = {
  key:'origin',
  id:platformId,
  sort:8,
  menuIds:menus.map(item=>item.id)
};


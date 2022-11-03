// 主数据
// @ts-ignore
import { createMenu, createModel} from '/@/../pages/rbac/utils/mockMenuUtil';
const platformId = 20007;
function createPlatformMenu(params){
  return createMenu({...params,platformId})
}
export const menus = [
  createModel({id: platformId, isCloud: false, isTargetBlank: false, isOutLink: false, name: '主数据', category: 102, sortNo: 100,}),
  createPlatformMenu({ id: 9999,  path: '/',  redirect:'/one/home/welcome',  component: 'LAYOUT',  hidden:true,  parentId: 0,  name: '根目录' }),
  createPlatformMenu({ id: 9901, path: '/one/home', component: 'LAYOUT', parentId: 0, name: '工作台' }),
  createPlatformMenu({ id: 9902, path: 'welcome', component: '/dashboard/analysis/index', parentId: 9901, name: '首页',}),

  createPlatformMenu({ id: 110001, path: '/one/one', component: 'LAYOUT', parentId: 0, name: '公共数据' }),
  createPlatformMenu({ id: 110002, path: '/one/two', component: 'LAYOUT', parentId: 0, name: '机构' }),
  createPlatformMenu({ id: 110003, path: '/one/three', component: 'LAYOUT', parentId: 0, name: '往来单位' }),
  createPlatformMenu({ id: 110004, path: '/one/four', component: 'LAYOUT', parentId: 0, name: '项目' }),
  createPlatformMenu({ id: 110005, path: '/one/five', component: 'LAYOUT', parentId: 0, name: '会计' }),
  createPlatformMenu({ id: 110006, path: '/one/six', component: 'LAYOUT', parentId: 0, name: '固定资产' }),
  createPlatformMenu({ id: 110007, path: '/one/seven', component: 'LAYOUT', parentId: 0, name: '存货' }),

  createPlatformMenu({ id: 210001, path: '/one-setting', component: '', parentId: 110001, name: '编码规则' }),
  createPlatformMenu({ id: 210002, path: '/one-archives', component: '', parentId: 110001, name: '档案' }),


  createPlatformMenu({id:310101, path: 'encoding_rules', component:'/boozsoft/system/encoding_rules/index', parentId:210001, componentName: 'EncodeingRules', name:'档案编码规则'}),
  createPlatformMenu({id:310101, path: 'encoding_rulesreport', component:'/boozsoft/system/encoding_rules_repot/index', parentId:210001, componentName: 'EncodeingRulesReport', name:'单据编码规则'}),

  createPlatformMenu({id:310201,path: 'accUsedForeignCurrency', component: '/boozsoft/system/AccUsedForeignCurrency/index', componentName: 'ChangYongWaiBi',parentId:210002,name:'常用外币'}),
  createPlatformMenu({id:310202,path: 'exchange-rate',component:'/boozsoft/system/exchange-rate/index',parentId:210002, componentName: 'WaiBiHuiLv',name:'外币汇率'}),
  createPlatformMenu({id:310203, path: 'sett-modes', component: '/boozsoft/system/sett-modes/index', parentId: 210002, name: '结算方式'}),
  createPlatformMenu({id:310204, path: 'bank-account', component: '/boozsoft/system/bank-account/index', parentId: 210002, name: '账户'}),
  createPlatformMenu({id:310205, path: 'expense', component: '/boozsoft/system/sys-expense/index', parentId: 210002, name: '费用'}),
  createPlatformMenu({id:310206,path:'defined-record',component:'/boozsoft/system/defined-record/index',componentName: 'DefinedRecord',parentId:210002,name:'自定义项'}),

  createPlatformMenu({ id: 220001, path: '/two-depts', component: '', parentId: 110002, name: '部门' }),
  createPlatformMenu({ id: 220002, path: '/two-users', component: '', parentId: 110002, name: '人员' }),

  createPlatformMenu({id: 320101, path: 'dept', component: '/boozsoft/system/dept/index', componentName: 'Dept', parentId: 220001, name: '部门档案'}),
  createPlatformMenu({id: 320201, path: 'psntype', component: '/boozsoft/system/psntype/index', componentName: 'PsnType', parentId: 220002, name: '人员类别',}),
  createPlatformMenu({id: 320202, path: 'department', component: '/boozsoft/system/department/index', componentName: 'Department', parentId: 220002, name: '人员信息',}),
  createPlatformMenu({id: 320203, path: 'jonfile', component: '/boozsoft/system/job-file/index', componentName: 'JonFile', parentId: 220002, name: '职务信息',}),

  createPlatformMenu({ id: 230001, path: '/three-custs', component: '', parentId: 110003, name: '客户' }),
  createPlatformMenu({ id: 230002, path: '/three-sups', component: '', parentId: 110003, name: '供应商' }),

  createPlatformMenu({id: 330101, path: 'customer_class', component: '/boozsoft/system/customer_class/index', componentName: 'customerClass', parentId: 230001, name: '客户分类',}),
  createPlatformMenu({id: 330102, path: 'customer_info', component: '/boozsoft/system/customer_info/index', componentName: 'CustomerInfo', parentId: 230001, name: '客户信息',}),
  createPlatformMenu({id: 330103, path: 'customer_zu', component: '/boozsoft/system/customer_zu/index', componentName: 'CustomerZu', parentId: 230001, name: '客户组',}),
  createPlatformMenu({id: 330201, path: 'sup_class', component: '/boozsoft/system/sup_class/index', componentName: 'SupClass', parentId: 230002, name: '供应商分类',}),
  createPlatformMenu({id: 330202, path: 'supplier', component: '/boozsoft/system/supplier/index', componentName: 'SupplierInfo', parentId: 230002, name: '供应商信息',}),

  createPlatformMenu({ id: 240001, path: '/four-custs', component: '', parentId: 110004, name: '分类' }),
  createPlatformMenu({ id: 240002, path: '/four-archives', component: '', parentId: 110004, name: '档案' }),

  createPlatformMenu({id: 340101, path: 'project-item', component: '/boozsoft/system/project-item/index', componentName: 'ProjectItem', parentId: 240001, name: '项目大类',}),
  createPlatformMenu({id: 340102, path: 'project-class', component: '/boozsoft/system/project-class/index', componentName: 'ProjectClassAcc', parentId: 240001, name: '项目分类',}),
  createPlatformMenu({id: 340201, path: 'project-category', component: '/boozsoft/system/project-category/index', componentName: 'ProjectCateAcc', parentId: 240002, name: '项目样式',}),
  createPlatformMenu({id: 340202, path: 'project-acc', component: '/boozsoft/system/project/index', componentName: 'ProjectAcc', parentId: 240002, name: '项目',}),

  createPlatformMenu({ id: 250001, path: '/five-common', component: '', parentId: 110005, name: '公共' }),
  createPlatformMenu({ id: 250002, path: '/five-finance', component: '', parentId: 110005, name: '财务会计' }),
  createPlatformMenu({ id: 250003, path: '/five-budget', component: '', parentId: 110005, name: '预算会计' }),

  createPlatformMenu({id: 350101, path: 'setting-basis-info', component: '/boozsoft/system/acccode2/index', componentName: 'acccode2', parentId: 250001, name: '会计科目',}),
  createPlatformMenu({id: 350102, path: 'voucher-type',component:'/boozsoft/system/voucher-type/index', componentName: 'PingzhengType',parentId:250001,name:'凭证类型'}),
  createPlatformMenu({id: 350103, path: 'fuzhu-hesuan', component: '/boozsoft/system/fuzhu-hesuan/index', componentName: 'FuzhuHesuan', parentId: 250001, name: '辅助核算定义'}),
  createPlatformMenu({id: 350104, path: 'accvoucher-cdigest', component: '/boozsoft/system/accvoucher-cdigest/index', componentName: 'ChangYongZaiYao', parentId: 250001, name: '常用摘要',}),
  createPlatformMenu({id: 350105, path: 'accvoucher-setting', component: '/boozsoft/system/accvoucher-setting/index', componentName: 'ChangYongVoucher', parentId: 250001, name: '常用凭证',}),

  createPlatformMenu({id: 350201, path: 'project-cash', component: '/boozsoft/system/project-cash/index', componentName: 'ProjectCashAcc', parentId: 250002, name: '现金流量项目',}),

  createPlatformMenu({id:3503010, path: 'organization-budget', component: '/boozsoft/system/budget-source/index', parentId: 250003, name: '预算来源',}),
  createPlatformMenu({id:3503011, path: 'organization-exclass', component: '/boozsoft/system/expenditure-class/index', parentId: 250003, name: '支出经济分类',}),
  createPlatformMenu({id:3503012, path: 'organization-zfclass', component: '/boozsoft/system/zf-class/index', parentId: 250003, name: '政府支出经济分类',}),
  createPlatformMenu({id:3503013, path: 'organization-deptclass', component: '/boozsoft/system/dept-class/index', parentId: 250003, name: '部门支出经济分类',}),


  createPlatformMenu({ id: 260001, path: '/six-setting', component: '', parentId: 110006, name: '设置' }),
  createPlatformMenu({ id: 260002, path: '/six-archives', component: '', parentId: 110006, name: '档案' }),

  createPlatformMenu({ id: 360101, path:'card-column-setting',component:'/boozsoft/system/fa-card-column/index',parentId:260001,name:'卡片栏目设置'}),
  createPlatformMenu({ id: 360101, path: 'qc-kemu-set', component: '/gdzc/dashboard/analysis/index', parentId: 260001, name: '折旧科目设置'}),

  createPlatformMenu({id:360201,path:'assets-type',component:'/boozsoft/system/fa-asset-type/index',parentId:260002,name:'资产类别'}),
  createPlatformMenu({id:360202,path:'assets-property',component:'/boozsoft/system/faProperty/index',parentId:260002,name:'资产属性'}),
  createPlatformMenu({id:360203,path:'assets-depreciation',component:'/boozsoft/system/fa-zj-method/index',parentId:260002,name:'折旧方法'}),
  createPlatformMenu({id:360204,path:'assets-increase-decrease',component:'/boozsoft/system/fa-add-cut-mode/index',parentId:260002,name:'增减方式'}),
  createPlatformMenu({id:360205,path:'assets-situation',component:'/boozsoft/system/fa-usage-status/index',parentId:260002,name:'使用状况'}),
  createPlatformMenu({id:360206,path:'assets-use',component:'/boozsoft/system/fa-economy-use/index',parentId:260002,name:'经济用途'}),
  createPlatformMenu({id:360207,path:'assets-column-setting',component:'/boozsoft/system/fa-location/index',parentId:260002,name:'存放设置'}),
  createPlatformMenu({id:360208,path:'assets-asset-group',component:'/boozsoft/system/fa-asset-group/index',parentId:260002,name:'资产组'}),
  createPlatformMenu({id:360208,path:'assets-handle-reason', component: '/gdzc/dashboard/analysis/index',parentId: 260002, name:'处置原因'}),

  createPlatformMenu({ id: 270001, path: '/seven-current', component: '', parentId: 110007, name: '通用' }),
  createPlatformMenu({ id: 270002, path: '/seven-warehousing', component: '', parentId: 110007, name: '仓储' }),
  createPlatformMenu({ id: 270003, path: '/seven-materiel', component: '', parentId: 110007, name: '物料' }),

  createPlatformMenu({id:370101,path: 'stock_shou_zhi_type',component: '/boozsoft/stock/stock_shou_fa_type/index',parentId: 270001,name: '收发方式'}),

  createPlatformMenu({id:370201,path: 'stock_cangku',component: '/boozsoft/stock/stock_cangku/index',parentId: 270002,name: '仓库信息'}),
  createPlatformMenu({id:370202,path: 'stock_cangku_level',component: '/boozsoft/stock/stock_cangku_level/index',parentId: 270002,name: '仓库级别'}),
  createPlatformMenu({id:370203,path: 'stock_cangku_level_record',component: '/boozsoft/stock/stock_cangku_level_record/index',parentId: 270002,name: '仓库级别档案'}),

  createPlatformMenu({id:370301,path: 'unit-mea',component:'/boozsoft/system/unit-mea/index',parentId:270003,name:'计量单位'}),
  createPlatformMenu({id:370302,path: 'stock_class',component: '/boozsoft/stock/stock_class/index',parentId: 270003,name: '存货分类'}),
  createPlatformMenu({id:370303,path: 'stock_info',component: '/boozsoft/stock/stock_info/index',parentId: 270003,componentName: 'StockInfo',name: '存货'}),
  createPlatformMenu({ id: 370304, path: 'stock_bom', component: '/boozsoft/stock/stock_bom_add/index',componentName: 'WuLiaoQingDan', parentId: 270003, name: 'BOM物料清单' }),
  createPlatformMenu({ id: 370305, path: 'stock_bom_list', component: '/boozsoft/stock/stock_bom_list/index',componentName: 'WuLiaoQingDanList', parentId: 270003, name: 'BOM物料清单列表' }),
];

export const platformMenuIds = {
  key:'one',
  sort:16,
  id:platformId,
  menuIds:menus.map(item=>item.id)
};


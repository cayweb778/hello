import { createMenu, createModel} from '/@/../pages/rbac/utils/mockMenuUtil';
const platformId=1003
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
    name: '集团管理',
    category: 101,
    sortNo: 6,
  }),
  createPlatformMenu({
    id: 9906,
    path: '/',
    redirect:'/system/home/welcome',
    component: 'LAYOUT',
    parentId: 0,
    hidden:true,
    name: '根目录'
  }),
  createPlatformMenu({
    id: 10000,
    path: 'welcome',
    component: '/dashboard/analysis/index',
    parentId: 10001,
    name: '首页',
  }),
  createPlatformMenu({ id: 10001, path: '/system/home', component: 'LAYOUT', parentId: 0, name: '工作台' }),

  createPlatformMenu({ id: 11001, path: '/system/one', component: 'Layout', parentId: 0, name: '机构管理' }),
  createPlatformMenu({ id: 11002, path: '/system/two', component: 'Layout', parentId: 0, name: '操作员' }),
  createPlatformMenu({ id: 11003, path: '/system/three', component: 'Layout', parentId: 0, name: '操作权限' }),
  createPlatformMenu({ id: 11004, path: '/system/four', component: 'Layout', parentId: 0, name: '数据权限' }),
  createPlatformMenu({ id: 11005, path: '/system/five', component: 'Layout', parentId: 0, name: '账套管理' }),
  createPlatformMenu({ id: 11006, path: '/system/six', component: 'Layout', parentId: 0, name: '主数据' }),
  createPlatformMenu({ id: 11007, path: '/system/seven', component: 'Layout', parentId: 0, name: '集团财务' }),
  createPlatformMenu({ id: 11008, path: '/system/eight', component: 'Layout', parentId: 0, name: '接口配置' }),
  createPlatformMenu({ id: 11009, path: '/system/nine', component: 'Layout', parentId: 0, name: '消息任务' }),
  createPlatformMenu({ id: 11010, path: '/system/ten', component: 'Layout', parentId: 0, name: '审核日志' }),

  /***************** 待定 ******************/
  // createPlatformMenu({id: 5010+i++, path: 'menu',  component: '/boozsoft/system/system/menu/index', parentId: 22004, name: '菜单权限' }),
  /***************** 待定 ******************/

  createPlatformMenu({ id: 21001, path: '/one-management', component: '', parentId: 11001, name: '集团' }),
  createPlatformMenu({ id: 21002, path: '/one-institution', component: '', parentId: 11001, name: '机构' }),

  createPlatformMenu({id: 50401, path: 'im-group', component: '/boozsoft/management/im-group/index', parentId: 21001, name: '集团',}),
  createPlatformMenu({id: 50402, path: 'im-organize', component: '/boozsoft/management/im-organize/index', parentId: 21002, name: '组织',}),
  createPlatformMenu({id: 50403, path: 'im-unit', component: '/boozsoft/management/im-unit/index', parentId: 21002, name: '公司',}),
  createPlatformMenu({id: 50404, path: 'im-factory', component: '/boozsoft/management/im-factory/index', parentId: 21002, name: '工厂',}),

  createPlatformMenu({ id: 22001, path: '/two-pass', component: '', parentId: 11002, name: '安全' }),
  createPlatformMenu({ id: 22002, path: '/two-role', component: '', parentId: 11002, name: '角色' }),
  createPlatformMenu({ id: 22003, path: '/two-oper', component: '', parentId: 11002, name: '操作员' }),

  createPlatformMenu({id: 5010+i++,path:'pwd-rule',component:'/boozsoft/system/pwd-rule/index',parentId:22001,componentName: 'PwdRule',name:'密码策略'}),
  createPlatformMenu({id: 5010+i++, path: 'sys-role',  component: '/boozsoft/system/sys-role/index', parentId: 22002,componentName: 'JiaoSe', name: '角色' }),
  createPlatformMenu({id: 5010+i++, path: 'opearManager',componentName: 'CaoZuoYuan', component: '/boozsoft/system/user/index', parentId: 22003, name: '操作员',}),

  createPlatformMenu({ id: 22004, path: '/two-role-auth', component: '', parentId: 11003, name: '角色' }),
  createPlatformMenu({ id: 22005, path: '/two-oper-auth', component: '', parentId: 11003, name: '操作员' }),

  createPlatformMenu({id: 5010+i++, path: 'role-author',componentName: 'JiaoSeQuanXian', component: '/boozsoft/system/role-author/index', parentId: 22004, name: '角色权限' }),
  createPlatformMenu({id: 5010+i++, path: 'operation-author',componentName: 'CaoZuoYuanQuanXian', component: '/boozsoft/system/operation-author/index', parentId: 22005, name: '操作员权限' }),

  createPlatformMenu({ id: 22007, path: '/two-data-assign', component: '', parentId: 11004, name: '分配' }),
  createPlatformMenu({ id: 22008, path: '/two-data-setting', component: '', parentId: 11004, name: '操作员' }),

  createPlatformMenu({id: 5010+i++, path: 'data',  component: '/boozsoft/system/data-author/index', parentId: 22007,componentName: 'ShuJuQuanXianFenPei', name: '数据权限分配' }),
  createPlatformMenu({id: 5010+i++, path: 'data-author',  component: '/boozsoft/system/data-author-control/index', parentId: 22008,componentName: 'ShuJuQuanXianKongZhi', name: '数据权限控制' }),
  createPlatformMenu({id: 5010+i++, path: 'archives-author',  component: '/boozsoft/system/archives-author/index', parentId: 22008,componentName: 'ShuJuQuanXianShenPiKongZhi', name: '集团主数据审批授权' }),


  createPlatformMenu({ id: 23001, path: '/three-list', component: '', parentId: 11005, name: '列表' }),

  createPlatformMenu({id:5010+i++,path:'ca-account-list',component:'/boozsoft/management/im-unit-two/index',parentId:23001,componentName: 'CaiWuKuanJiZhangTaoList',name:'财务会计账套列表'}),
  createPlatformMenu({id:5010+i++,path:'ca-fixed-assets-list',component:'/boozsoft/management/im-unit-three/index',parentId:23001,componentName: 'GuDingZiChanZhangTaoList',name:'固定资产账套列表'}),
  createPlatformMenu({id:5010+i++,path:'ca-stock-list',component:'/boozsoft/management/im-unit-four/index',parentId:23001,componentName: 'CunHuoGuanLiZhangTaoList',name:'存货管理账套列表'}),

  createPlatformMenu({ id: 24001, path: '/four-common', component: '', parentId: 11006, name: '公共数据' }),
  createPlatformMenu({ id: 24002, path: '/four-users', component: '', parentId: 11006, name: '人员' }),
  createPlatformMenu({ id: 24003, path: '/four-unit', component: '', parentId: 11006, name: '往来单位' }),
  createPlatformMenu({ id: 24004, path: '/four-pro', component: '', parentId: 11006, name: '项目' }),
  createPlatformMenu({ id: 24005, path: '/four-customize', component: '', parentId: 11006, name: '自定义项' }),

  createPlatformMenu({id:50548,path:'encoding_rules',component:'/boozsoft/management/encoding_rules/index',parentId:24001,name:'编码规则'}),
  createPlatformMenu({id:50547, path: 'unit-mea', component: '/boozsoft/management/unit-mea/index', parentId: 24001, name: '计量单位' }),
  createPlatformMenu({id:5010+i++,path:'sett-modes',component:'/boozsoft/management/sett-modes/index',parentId:24001,name:'结算方式'}),
  createPlatformMenu({id:5010+i++,path: 'UsedForeignCurrency', component: '/boozsoft/group/UsedForeignCurrency/index',parentId:24001,name:'常用外币'}),
  createPlatformMenu({id:5010+i++,path:'894213',component:'/boozsoft/management/exchange-rate/index',parentId:24001,name:'外币汇率'}),

  createPlatformMenu({id: 5010+i++, path: 'groupPsnType', component: '/boozsoft/management/psntype/index',componentName: 'GroupPsnType', parentId: 24002, name: '人员分类',}),
  createPlatformMenu({id: 5010+i++, path: 'groupPsn', component: '/boozsoft/management/psn/index',componentName: 'GroupPsn', parentId: 24002, name: '人员信息',}),
  createPlatformMenu({id: 5010+i++, path: 'jonfile', component: '/boozsoft/management/job-file/index',componentName: 'JonFile', parentId: 24002, name: '职务信息',}),
  /*createPlatformMenu({id: 5010+i++, path: 'psntype-assign', component: '/boozsoft/system/psntype/index', parentId: 50542, name: '员工分配',}),*/

  createPlatformMenu({id: 5010+i++, path: 'customer_class_group', component: '/boozsoft/system/customer_class_group/index', parentId: 24003,componentName: 'CustomerClassGroup', name: '客户分类'}),
  createPlatformMenu({id: 5010+i++, path: 'cus-info-group', component: '/boozsoft/system/customer_info_group/index', parentId: 24003,componentName: 'CustomerInfoGroup', name: '客户信息',}),
  // createPlatformMenu({id: 5010+i++, path: 'customer-assign', component: '/boozsoft/system/customer_class_group/index', parentId: 50543, name: '客户分配'}),

  createPlatformMenu({id: 5010+i++, path: 'sup_class_group', component: '/boozsoft/system/sup_class_group/index', parentId: 24003,componentName: 'SupClassGroup', name: '供应商分类'}),
  createPlatformMenu({id: 5010+i++, path: 'sup-info-group', component: '/boozsoft/system/supplier_group/index', parentId: 24003,componentName: 'SupplierInfoGroup', name: '供应商信息',}),
  // createPlatformMenu({id: 5010+i++, path: 'sup-assign', component: '/boozsoft/system/sup_class_group/index', parentId: 50544, name: '供应商分配'}),

  createPlatformMenu({id: 5010+i++, path: 'sys-project-item', component: '/boozsoft/management/project-item/index',componentName: 'GroupProjectItem', parentId: 24004, name: '项目大类'}),
  createPlatformMenu({id: 5010+i++, path: 'project-class', component: '/boozsoft/management/project-class/index',componentName: 'GroupProjectClass', parentId: 24004, name: '项目分类'}),
  createPlatformMenu({id: 5010+i++, path: 'sys-project-category', component: '/boozsoft/management/project-category/index',componentName: 'GroupProjectCate', parentId: 24004, name: '项目样式'}),
  createPlatformMenu({id: 5010+i++, path: 'sys-project', component: '/boozsoft/management/project/index',componentName: 'GroupProject', parentId: 24004, name: '项目目录'}),
  createPlatformMenu({id: 5010+i++, path: 'project-assign', component: '/boozsoft/system/project-class/index', parentId: 24004, name: '项目分配'}),


  // createPlatformMenu({id: 5010+i++, path: 'group-defined-file',component:'/boozsoft/management/defined-file/index',componentName: 'GroupDefinedFile',parentId:50546,name:'自定义项设置'}),
  createPlatformMenu({id: 5010+i++, path: 'group-defined-record',component:'/boozsoft/management/defined-record/index',componentName: 'GroupDefinedRecord',parentId:24005,name:'自定义项'}),
  // createPlatformMenu({id: 5010+i++, path: 'group-defined-assign',component:'/boozsoft/management/defined-record/index',parentId:50546,name:'自定义项分配'}),
  // createPlatformMenu({id: 5010+i++, path: 'group-fuzhu-hesuan',component:'/boozsoft/management/fuzhu-hesuan/index',componentName: 'GroupFuzhuHesuan',parentId:50546,name:'辅助核算定义'}),

  createPlatformMenu({ id: 25001, path: '/five-archives', component: '', parentId: 11007, name: '会计档案' }),
  createPlatformMenu({ id: 25002, path: '/five-finance', component: '', parentId: 11007, name: '财务会计' }),
  createPlatformMenu({ id: 25003, path: '/five-budget', component: '', parentId: 11007, name: '预算会计' }),
  createPlatformMenu({ id: 25004, path: '/five-report', component: '', parentId: 11007, name: '财务报表' }),

  createPlatformMenu({id:5010+i++, path: 'gj-accstandard', component: '/boozsoft/system/accstandard/index', parentId: 25001, name: '国家（地区）会计制度',}),
  createPlatformMenu({id:5010+i++, path: 'acctemplate', component: '/boozsoft/system/acctemplate/index', parentId: 25001, name: '集团会计制度',}),
  createPlatformMenu({id:5010+i++,path:'group-voucher-type',component:'/boozsoft/management/voucher-type/index',parentId:25001,name:'凭证类别'}),

  createPlatformMenu({id:5010+i++, path: 'group-project-cash', component: '/boozsoft/management/project-cash/index',componentName: 'GroupProjectCash', parentId: 25002, name: '现金流量项目'}),

  createPlatformMenu({id:5010+i++, path: 'organization-budget', component: '/boozsoft/management/budget-source/index', parentId: 25003, name: '预算来源',}),
  createPlatformMenu({id:5010+i++, path: 'organization-exclass', component: '/boozsoft/management/expenditure-class/index', parentId: 25003, name: '支出功能分类',}),
  createPlatformMenu({id:5010+i++, path: 'organization-zfclass', component: '/boozsoft/management/zf-class/index', parentId: 25003, name: '政府支出经济分类',}),
  createPlatformMenu({id:5010+i++, path: 'organization-deptclass', component: '/boozsoft/management/dept-class/index', parentId: 25003, name: '部门支出经济分类',}),

  createPlatformMenu({ id: 5010+i++, path: 'report-zcfzb', component: '/boozsoft/management/report/report-zcfzb/index',componentName: 'GroupZCFZB', parentId: 25004, name: '资产负债表' }),
  createPlatformMenu({ id: 5010+i++, path: 'report-lrb', component: '/boozsoft/management/report/report-lrb/index',componentName: 'GroupLRB', parentId: 25004, name: '利润表' }),
  createPlatformMenu({ id: 5010+i++, path: 'report-xjllb', component: '/boozsoft/management/report/report-xjllb/index',componentName: 'GroupXJLLB', parentId: 25004, name: '现金流量表' }),
  createPlatformMenu({ id: 5010+i++, path: 'report-srfyb', component: '/boozsoft/management/report/report-srfyb/index',componentName: 'GroupSRFYB', parentId: 25004, name: '收入费用表' }),
  createPlatformMenu({ id: 5010+i++, path: 'report-jzcbdb', component: '/boozsoft/management/report/report-jzcbdb/index',componentName: 'GroupJZCBDB', parentId: 25004, name: '净资产变动表' }),
  createPlatformMenu({ id: 5010+i++, path: 'report-yssrzcb', component: '/boozsoft/management/report/report-yssrzcb/index',componentName: 'GroupYSSRZCB', parentId: 25004, name: '预算收入支出表' }),
  createPlatformMenu({ id: 5010+i++, path: 'report-ysjzjybdb', component: '/boozsoft/management/report/report-ysjzjybdb/index',componentName: 'GroupYSJZJYBDB', parentId: 25004, name: '预算结转结余变动表' }),
  createPlatformMenu({ id: 5010+i++, path: 'report-czbkyssrzcb', component: '/boozsoft/management/report/report-czbkyssrzcb/index',componentName: 'GroupCZBKYSSRZCB', parentId: 25004, name: '财政拨款预算收入支出表' }),

  createPlatformMenu({ id: 26001, path: '/six-api', component: '', parentId: 11008, name: 'API参数' }),
  createPlatformMenu({ id: 26002, path: '/six-task', component: '', parentId: 11009, name: '综合' }),
  createPlatformMenu({ id: 26003, path: '/six-log', component: '', parentId: 11010, name: '日志' }),

  createPlatformMenu({id: 5010+i++, path: 'im-sale1', component: '/boozsoft/system/blocktable/index', parentId: 26001, name: '邮件发送接口参数',}),
  createPlatformMenu({id: 5010+i++, path: 'im-sale2', component: '/boozsoft/system/blocktable/index', parentId: 26001, name: '短信平台接口参数',}),
  createPlatformMenu({id: 5010+i++, path: 'im-sale3', component: '/boozsoft/management/ocr-setting/index', parentId: 26001, name: '票据识别接口参数',}),
  createPlatformMenu({id: 5010+i++, path: 'im-sale4', component: '/boozsoft/management/verification-setting/index', parentId: 26001, name: '发票验真接口参数',}),

  createPlatformMenu({id: 5010+i++, path: 'orgmessage', component: '/boozsoft/system/message/org-index', parentId: 26002, name: '消息管理',}),
  createPlatformMenu({id: 5010+i++, path: 'orgtask', component: '/boozsoft/system/sys-task/org-index', parentId: 26002, name: '任务协作',}),
  createPlatformMenu({id: 5010+i++, path: 'groptask', component: '/boozsoft/system/task/sys-index', parentId: 26002, name: '清除锁定',}),
  createPlatformMenu({id: 5010+i++, path: 'data2', component: '/boozsoft/system/dataManager/index', parentId: 26002, name: '覆盖数据',}),

  createPlatformMenu({id: 5010+i++, path: 'system-logger', component: '/boozsoft/group/SysLogger/index', parentId: 26003, name: '系统日志',}),
  createPlatformMenu({id: 5010+i++, path: 'oper-logger', component: '/boozsoft/group/oper-logger/index', parentId: 26003, name: '操作日志',}),
];

export const platformMenuIds = {
  key:'system',
  id:platformId,
  sort:7,
  menuIds:menus.map(item=>item.id)
};


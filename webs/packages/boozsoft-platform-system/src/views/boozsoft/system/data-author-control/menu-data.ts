const platformMenuDataModel = [
  {
    ctrlRange: '',
    uniqueCode: '',
    dataName: '人员分类',
    databaseName: 'sys_psn_type',
    isCtrl: '',
    isCtrlYear: '',
    moduleName: '主数据',databaseColCode: 'psn_type_code',databaseColName: 'psn_type_name',uniqueColCode: 'unique_code',databaseColCondition: 'tenant_id',relation: '',
  },
  {
     ctrlRange: '',
     uniqueCode: '',
     dataName: '人员信息',
     databaseName: 'sys_psn',
     isCtrl: '',
     isCtrlYear: '',
     moduleName: '主数据',databaseColCode: 'psn_code',databaseColName: 'psn_name',uniqueColCode: 'unique_code',databaseColCondition: 'tenant_id',relation: ''
  },
  {
    ctrlRange: '',
    uniqueCode: '',
    dataName: '部门',
    databaseName: 'sys_department',
    isCtrl: '',
    isCtrlYear: '',
    moduleName: '主数据',databaseColCode: 'dept_code',databaseColName: 'dept_name',uniqueColCode: 'unique_code',databaseColCondition: 'tenant_id',relation: 'sys_department'
  },{
    ctrlRange: '',
    uniqueCode: '',
    dataName: '客户分类',
    databaseName: 'customer_class',
    isCtrl: '',
    isCtrlYear: '',
    moduleName: '主数据',databaseColCode: 'cus_class',databaseColName: 'cus_cclass_name',uniqueColCode: 'unique_custclass',databaseColCondition: 'tenant_id',relation: 'parent_id'
  },{
    ctrlRange: '',
    uniqueCode: '',
    dataName: '客户',
    databaseName: 'customer',
    isCtrl: '',
    isCtrlYear: '',
    moduleName: '主数据',databaseColCode: 'cust_code',databaseColName: 'cust_abbname',uniqueColCode: 'unique_code',databaseColCondition: 'tenant_id',relation: ''
  },{
    ctrlRange: '',
    uniqueCode: '',
    dataName: '供应商分类',
    databaseName: 'supplier_class',
    isCtrl: '',
    isCtrlYear: '',
    moduleName: '主数据',databaseColCode: 'sup_class',databaseColName: 'sup_class_name',uniqueColCode: 'unique_supclass',databaseColCondition: 'tenant_id',relation: 'parent_id'
  },{
    ctrlRange: '',
    uniqueCode: '',
    dataName: '供应商',
    databaseName: 'customer',
    isCtrl: '',
    isCtrlYear: '',
    moduleName: '主数据',databaseColCode: 'sup_code',databaseColName: 'sup_abbname',uniqueColCode: 'unique_code',databaseColCondition: 'tenant_id',relation: ''
  },{
    ctrlRange: '',
    uniqueCode: '',
    dataName: '项目大类',
    databaseName: 'project_class',
    isCtrl: '',
    isCtrlYear: '',
    moduleName: '主数据',databaseColCode: 'project_code',databaseColName: 'project_name',uniqueColCode: 'id',databaseColCondition: 'tenant_id',relation: ''
  },{
    ctrlRange: '',
    uniqueCode: '',
    dataName: '项目分类',
    databaseName: 'project_class',
    isCtrl: '',
    isCtrlYear: '',
    moduleName: '主数据',databaseColCode: 'project_class_code',databaseColName: 'project_class_name',uniqueColCode: 'unique_code',databaseColCondition: 'tenant_id',relation: 'parent_id'
  },{
    ctrlRange: '',
    uniqueCode: '',
    dataName: '项目',
    databaseName: 'project',
    isCtrl: '',
    isCtrlYear: '',
    moduleName: '主数据',databaseColCode: 'project_code',databaseColName: 'project_name',uniqueColCode: 'id',databaseColCondition: 'tenant_id',relation: ''
  },{
    ctrlRange: '',
    uniqueCode: '',
    dataName: '自定义项档案',
    databaseName: 'defined_record',
    isCtrl: '',
    isCtrlYear: '',
    moduleName: '主数据',databaseColCode: 'record_code',databaseColName: 'record_name',uniqueColCode: 'id',databaseColCondition: 'tenant_id',relation: ''
  },
  {
    ctrlRange: '',
    uniqueCode: '',
    dataName: '凭证类别',
    databaseName: 'sys_voucher_type',
    isCtrl: '',
    isCtrlYear: '',
    moduleName: '主数据',databaseColCode: 'voucher_num',databaseColName: 'voucher_type_name',uniqueColCode: 'id',databaseColCondition: 'tenant_id',relation: ''
  },/*{
    ctrlRange: '',
    uniqueCode: '',
    dataName: '会计科目',
    databaseName: 'code_kemu',
    isCtrl: '',
    isCtrlYear: '',
    moduleName: '主数据',databaseColCode: 'ccode',databaseColName: 'ccode_name',uniqueColCode: 'unique_code',databaseColCondition: 'tenant_id',relation: 'superior_ccode'
  },*/
  {
    ctrlRange: '',
    uniqueCode: '',
    dataName: '存货分类',
    databaseName: 'stock_class',
    isCtrl: '',
    isCtrlYear: '',
    moduleName: '主数据',databaseColCode: 'stock_class',databaseColName: 'stock_class_name',uniqueColCode: 'unique_stockclass',databaseColCondition: 'tenant_id',relation: 'parent_id'
  },{
    ctrlRange: '',
    uniqueCode: '',
    dataName: '存货档案',
    databaseName: 'stock',
    isCtrl: '',
    isCtrlYear: '',
    moduleName: '主数据',databaseColCode: 'stock_num',databaseColName: 'stock_name',uniqueColCode: 'id',databaseColCondition: 'tenant_id',relation: ''
  },{
    ctrlRange: '',
    uniqueCode: '',
    dataName: '仓库档案',
    databaseName: 'stock_cangku',
    isCtrl: '',
    isCtrlYear: '',
    moduleName: '主数据',databaseColCode: 'ck_num',databaseColName: 'ck_name',uniqueColCode: 'id',databaseColCondition: 'tenant_id',relation: ''
  },
  {
    ctrlRange: '',
    uniqueCode: '',
    dataName: '资产类别',
    databaseName: 'fa_asset_type',
    isCtrl: '',
    isCtrlYear: '',
    moduleName: '固定资产',databaseColCode: 'ec_code',databaseColName: 'ec_name',uniqueColCode: 'unique_code',databaseColCondition: 'tenant_id',relation: 'parent_id'
  },{
    ctrlRange: '',
    uniqueCode: '',
    dataName: '资产组',
    databaseName: 'fa_asset_group',
    isCtrl: '',
    isCtrlYear: '',
    moduleName: '固定资产',databaseColCode: 'bs_code',databaseColName: 'bs_name',uniqueColCode: 'id',databaseColCondition: 'tenant_id',relation: ''
  },
  {
    ctrlRange: '',
    uniqueCode: '',
    dataName: '资产卡片',
    databaseName: 'fa_card_master',
    isCtrl: '',
    isCtrlYear: '',
    moduleName: '固定资产',databaseColCode: 'card_code',databaseColName: 'fa_name',uniqueColCode: 'card_unique',databaseColCondition: 'tenant_id',relation: ''
  },
  {
    ctrlRange: '',
    uniqueCode: '',
    dataName: '记账凭证',
    databaseName: 'accvoucher',
    isCtrl: '',
    isCtrlYear: '',
    moduleName: '财务会计',databaseColCode: 'ino_id',databaseColName: 'cdigest',uniqueColCode: 'id',databaseColCondition: 'tenant_id',relation: ''
  },
  {
    ctrlRange: '',
    uniqueCode: '',
    dataName: '预算来源',
    databaseName: 'budget_source',
    isCtrl: '',
    isCtrlYear: '',
    moduleName: '预算会计',databaseColCode: 'bs_code',databaseColName: 'bs_name',uniqueColCode: 'id',databaseColCondition: 'tenant_id',relation: ''
  },{
    ctrlRange: '',
    uniqueCode: '',
    dataName: '支出功能分类',
    databaseName: 'expenditure_class',
    isCtrl: '',
    isCtrlYear: '',
    moduleName: '预算会计',databaseColCode: 'ec_code',databaseColName: 'bs_name',ec_name: 'unique_code',databaseColCondition: 'tenant_id',relation: 'parent_id'
  },{
    ctrlRange: '',
    uniqueCode: '',
    dataName: '部门支出功能分类',
    databaseName: 'sys_dept_class',
    isCtrl: '',
    isCtrlYear: '',
    moduleName: '预算会计',databaseColCode: 'ec_code',databaseColName: 'bs_name',ec_name: 'unique_code',databaseColCondition: 'tenant_id',relation: 'parent_id'
  },{
    ctrlRange: '',
    uniqueCode: '',
    dataName: '部门支出功能分类',
    databaseName: 'sys_zf_class',
    isCtrl: '',
    isCtrlYear: '',
    moduleName: '预算会计',databaseColCode: 'ec_code',databaseColName: 'bs_name',ec_name: 'unique_code',databaseColCondition: 'tenant_id',relation: 'parent_id'
  },
]
export function getTheMenuOfTheSpecifiedPlatform() {
  let list = platformMenuDataModel
  return null == list ? [] : list
}

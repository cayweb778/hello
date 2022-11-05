const platformMenuDataModel = [
 /* {
    userId: null,
    originId: null,
    tenantId: null,
    isAuto: false,
    baseName: '机构-集团',
    baseEnName: '_app_group_sys_group',
    ctype: null, 、、、、
  },*/{
    userId: null,
    originId: null,
    tenantId: null,
    isAuto: false,
    baseName: '人员信息',
    baseEnName: 'sys_psn',
    ctype: null,
  },{
    userId: null,
    originId: null,
    tenantId: null,
    isAuto: false,
    baseName: '客户',
    baseEnName: 'customer',
    ctype: null,
  },{
    userId: null,
    originId: null,
    tenantId: null,
    isAuto: false,
    baseName: '供应商',
    baseEnName: 'supplier',
    ctype: null,
  },{
    userId: null,
    originId: null,
    tenantId: null,
    isAuto: false,
    baseName: '项目',
    baseEnName: 'project',
    ctype: null,
  },{
    userId: null,
    originId: null,
    tenantId: null,
    isAuto: false,
    baseName: '自定义项',
    baseEnName: 'defined_record',
    ctype: null,
  },
]

export function getTheMenuOfTheSpecifiedPlatform() {
  let list = platformMenuDataModel
  return null == list ? [] : list
}

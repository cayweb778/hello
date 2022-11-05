import {reactive} from "vue";

const dynamicColumnAndDataModel = reactive({
  DEFAULT: [
    {
      key: '0',
      title: '栏目名称',
      dataIndex: 'name',
      align: 'left',
      width: 150,
    },
    {
      key: '1',
      title: '显示',
      dataIndex: 'check',
      align: 'center',
      width: 60,
      slots: { customRender: 'checkBox' },
    },
    {
      key: '3',
      title: '显示名称',
      dataIndex: 'nameNew',
      width: 150,
      align: 'center',
      slots: { customRender: 'nameInput', },
    },
    {
      key: '4',
      title: '宽度(范围)',
      dataIndex: 'width',
      align: 'center',
      width: 140,
      slots: { customRender: 'widthInput', },
    },
    {
      key: '5',
      title: '对齐方式',
      dataIndex: 'align',
      align: 'center',
      width: 150,
      slots: { customRender: 'alignRadio' },
    }
  ],
  DATA: [
    {
      key: '0',
      name: '申请状态',
      nameNew: '申请状态',
      check: true,
      width: 120,
      max: 120,
      min: 80,
      align: 'left',
    },{
      key: '1',
      name: '状态',
      nameNew: '状态',
      check: true,
      width: 50,
      max: 80,
      min: 50,
      align: 'left',
    },{
      key: '2',
      name: '客户编码',
      nameNew: '客户编码',
      check: true,
      width: 80,
      max: 200,
      min: 80,
      align: 'left',
    },{
      key: '3',
      name: '客户全称',
      nameNew: '客户全称',
      check: true,
      width: 200,
      max: 300,
      min: 100,
      align: 'left',
    },{
      key: '4',
      name: '客户简称',
      nameNew: '客户简称',
      check: true,
      width: 200,
      max: 300,
      min: 100,
      align: 'left',
    },{
      key: '5',
      name: '客户分类',
      nameNew: '客户分类',
      check: true,
      width: 80,
      max: 150,
      min: 80,
      align: 'left',
    },{
      key: '6',
      name: '管理部门',
      nameNew: '管理部门',
      check: true,
      width: 80,
      max: 150,
      min: 80,
      align: 'left',
    },{
      key: '7',
      name: '业务员',
      nameNew: '业务员',
      check: true,
      width: 80,
      max: 100,
      min: 80,
      align: 'left',
    },{
      key: '8',
      name: '税号',
      nameNew: '税号',
      check: true,
      width: 80,
      max: 150,
      min: 80,
      align: 'left',
    },{
      key: '9',
      name: '价格级次',
      nameNew: '价格级次',
      check: true,
      width: 80,
      max: 150,
      min: 80,
      align: 'center',
    },{
      key: '10',
      name: '联系人',
      nameNew: '联系人',
      check: true,
      width: 80,
      max: 100,
      min: 80,
      align: 'left',
    },{
      key: '11',
      name: '联系电话',
      nameNew: '联系电话',
      check: true,
      width: 80,
      max: 100,
      min: 80,
      align: 'left',
    },{
      key: '12',
      name: '手机',
      nameNew: '手机',
      check: true,
      width: 80,
      max: 100,
      min: 80,
      align: 'left',
    },{
      key: '13',
      name: 'Email',
      nameNew: 'Email',
      check: true,
      width: 80,
      max: 100,
      min: 80,
      align: 'left',
    },{
      key: '14',
      name: '省市区',
      nameNew: '省市区',
      check: true,
      width: 100,
      max: 150,
      min: 100,
      align: 'left',
    },{
      key: '15',
      name: '详细地址',
      nameNew: '详细地址',
      check: true,
      width: 80,
      max: 300,
      min: 80,
      align: 'left',
    },{
      key: '16',
      name: '开户银行',
      nameNew: '开户银行',
      check: true,
      width: 80,
      max: 150,
      min: 80,
      align: 'left',
    },{
      key: '17',
      name: '开户地',
      nameNew: '开户地',
      check: true,
      width: 80,
      max: 150,
      min: 80,
      align: 'left',
    },{
      key: '18',
      name: '银行账号',
      nameNew: '银行账号',
      check: true,
      width: 80,
      max: 150,
      min: 80,
      align: 'left',
    },{
      key: '19',
      name: '银行行号',
      nameNew: '银行行号',
      check: true,
      width: 80,
      max: 150,
      min: 80,
      align: 'left',
    },{
      key: '20',
      name: '税率',
      nameNew: '税率',
      check: true,
      width: 50,
      max: 80,
      min: 50,
      align: 'left',
    },{
      key: '21',
      name: '母公司',
      nameNew: '母公司',
      check: true,
      width: 100,
      max: 300,
      min: 100,
      align: 'left',
    },{
      key: '22',
      name: '对应供应商',
      nameNew: '对应供应商',
      check: true,
      width: 100,
      max: 300,
      min: 100,
      align: 'left',
    },{
      key: '23',
      name: '开票抬头全称',
      nameNew: '开票抬头全称',
      check: true,
      width: 120,
      max: 300,
      min: 120,
      align: 'left',
    },{
      key: '24',
      name: '纳税人识别号',
      nameNew: '纳税人识别号',
      check: true,
      width: 120,
      max: 300,
      min: 120,
      align: 'left',
    },{
      key: '25',
      name: '地址电话',
      nameNew: '地址电话',
      check: true,
      width: 100,
      max: 300,
      min: 100,
      align: 'left',
    },{
      key: '26',
      name: '开户行及账号',
      nameNew: '开户行及账号',
      check: true,
      width: 120,
      max: 300,
      min: 120,
      align: 'left',
    },{
      key: '27',
      name: '管理类型',
      nameNew: '管理类型',
      check: true,
      width: 80,
      max: 130,
      min: 80,
      align: 'center',
    },{
      key: '28',
      name: '操作',
      nameNew: '操作',
      check: true,
      isFixed: true,
      width: 60,
      max: 60,
      min: 60,
      align: 'center',
    },
  ]
})

export function changeDefaultDynamics(list) {
  // 改变默认数据
  dynamicColumnAndDataModel['DATA'] = list
}

export function initDynamics() {
  return dynamicColumnAndDataModel
}

export function assemblyDynamicColumn(lanmuList: any, columnList: any) {
  columnList.forEach(cObj => {
    if (cObj != null) {
      lanmuList.forEach((lObj, index) => {
        if (cObj.title === lObj.name) {
          cObj.title = thisName(index + '', lanmuList)
          cObj.width = thisWidth(index + '', lanmuList)
          cObj.align = thisAlign(index + '', lanmuList)
          cObj.ifShow = thisIsShow(index + '', lanmuList)
          cObj.pkey = thisSort(index + '', lanmuList)
        }
      })
    }
  })
  return columnList.sort((a, b) => a.pkey - b.pkey);
}

/*********************** 静态方法 ***********************/
// 动态名称
const thisName = (index, thisData) => {
  let value = ''
  if (index.toString().indexOf('-') != -1) {
    let arr = index.split('-');
    if (arr.length == 2) {
      value = thisData[parseInt(arr[0])].children[parseInt(arr[1]) - 1].nameNew
      if ('' == value) value = thisData[parseInt(arr[0])].children[parseInt(arr[1]) - 1].name
    } else {
      value = thisData[parseInt(arr[0])].children[parseInt(arr[1]) - 1].children[parseInt(arr[2]) - 1].nameNew
      if ('' == value) value = thisData[parseInt(arr[0])].children[parseInt(arr[1]) - 1].children[parseInt(arr[2]) - 1].name
    }
  } else {
    value = thisData[index].nameNew
    if ('' == value) value = thisData[index].name
  }
  return value
}
// 当前宽度
const thisWidth = (index, thisData) => {
  let value: any = 0
  if (index.toString().indexOf('-') != -1) {
    let arr = index.split('-');
    if (arr.length == 2) {
      value = thisData[parseInt(arr[0])].children[parseInt(arr[1]) - 1].width
    } else {
      value = thisData[parseInt(arr[0])].children[parseInt(arr[1]) - 1].children[parseInt(arr[2]) - 1].width
    }
  } else {
    value = thisData[index].width
  }
  return parseInt(value)
}
// 是否显示
const thisIsShow = (index, thisData) => {
  let value = false
  if (index.toString().indexOf('-') != -1) {
    let arr = index.split('-');
    if (arr.length == 2) {
      value = thisData[parseInt(arr[0])].children[parseInt(arr[1]) - 1].check
    } else {
      value = thisData[parseInt(arr[0])].children[parseInt(arr[1]) - 1].children[parseInt(arr[2]) - 1].check
    }
  } else {
    value = thisData[index].check
  }
  return value
}
// 对齐方式
const thisAlign = (index, thisData) => {
  let value = 'center';
  if (index.toString().indexOf('-') != -1) {
    let arr = index.split('-');
    if (arr.length == 2) {
      value = thisData[parseInt(arr[0])].children[parseInt(arr[1]) - 1].align
    } else {
      value = thisData[parseInt(arr[0])].children[parseInt(arr[1]) - 1].children[parseInt(arr[2]) - 1].align
    }
  } else {
    value = thisData[index].align
  }
  return value
}
// 排序标识
const thisSort = (index, thisData) => {
  let value = null;
  if (index.toString().indexOf('-') != -1) {
    let arr = index.split('-');
    if (arr.length == 2) {
      value = thisData[parseInt(arr[0])].children[parseInt(arr[1]) - 1].pkey
    } else {
      value = thisData[parseInt(arr[0])].children[parseInt(arr[1]) - 1].children[parseInt(arr[2]) - 1].pkey
    }
  } else {
    value = thisData[index].pkey
  }
  return value
}

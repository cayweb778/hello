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
    // {
    //   key: '0',
    //   name: '申请状态',
    //   nameNew: '申请状态',
    //   check: true,
    //   width: 120,
    //   max: 120,
    //   min: 80,
    //   align: 'left',
    // },
    // {
    //   key: '19',
    //   name: '状态',
    //   nameNew: '状态',
    //   check: true,
    //   width: 120,
    //   max: 120,
    //   min: 80,
    //   align: 'left',
    // },
    {
      key: '1',
      name: '客户编码',
      nameNew: '客户编码',
      check: true,
      width: 120,
      max: 150,
      min: 80,
      align: 'left',
    }
    ,
    {
      key: '2',
      name: '客户全称',
      nameNew: '客户全称',
      check: true,
      width: 200,
      max: 350,
      min: 150,
      align: 'left',
    }
    ,{
      key: '3',
      name: '客户简称',
      nameNew: '客户简称',
      check: true,
      width: 200,
      max: 350,
      min: 150,
      align: 'left',
    }
    ,
    {
      key: '4',
      name: '税号',
      nameNew: '税号',
      check: true,
      width: 200,
      max: 200,
      min: 150,
      align: 'left'
    },
    {
      key: '18',
      name: '分类名称',
      nameNew: '分类名称',
      check: true,
      width: 200,
      max: 200,
      min: 150,
      align: 'left'
    },
    {
      key: '5',
      name: '管理类型',
      nameNew: '管理类型',
      check: false,
      width: 120,
      max: 150,
      min: 100,
      align: 'center'
    }
    ,{
      key: '6',
      name: '母公司',
      nameNew: '母公司',
      check: false,
      width: 200,
      max: 200,
      min: 80,
      align: 'center'
    }
    ,{
      key: '7',
      name: '对应供应商',
      nameNew: '对应供应商',
      check: false,
      width: 200,
      max: 200,
      min: 80,
      align: 'center'
    }
    ,{
      key: '8',
      name: '联系人',
      nameNew: '联系人',
      check: true,
      width: 200,
      max: 200,
      min: 80,
      align: 'center'
    },
    {
      key: '9',
      name: '电话',
      nameNew: '电话',
      check: true,
      width: 120,
      max: 150,
      min: 100,
      align: 'center'
    }
    ,
    {
      key: '10',
      name: '手机',
      nameNew: '手机',
      check: false,
      width: 120,
      max: 150,
      min: 100,
      align: 'center'
    }
    ,{
      key: '11',
      name: '邮箱',
      nameNew: '邮箱',
      check: false,
      width: 120,
      max: 200,
      min: 100,
      align: 'center'
    },{
      key: '12',
      name: '行业性质',
      nameNew: '行业性质',
      check: true,
      width: 120,
      max: 200,
      min: 100,
      align: 'center'
    }
    ,{
      key: '13',
      name: '公司地址',
      nameNew: '公司地址',
      check: false,
      width: 200,
      max: 300,
      min: 150,
      align: 'left'
    },
    {
      key: '14',
      name: '开户银行',
      nameNew: '开户银行',
      check: false,
      width: 200,
      max: 250,
      min: 200,
      align: 'center'
    },{
      key: '15',
      name: '开户地',
      nameNew: '开户地',
      check: false,
      width: 200,
      max: 300,
      min: 200,
      align: 'left'
    },{
      key: '16',
      name: '银行账户',
      nameNew: '银行账户',
      check: false,
      width: 200,
      max: 250,
      min: 200,
      align: 'left'
    },{
      key: '17',
      name: '银行行号',
      nameNew: '银行行号',
      check: false,
      width: 200,
      max: 250,
      min: 200,
      align: 'center'
    }
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

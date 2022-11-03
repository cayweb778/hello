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
  DATA1: [
    {
      key: '0',
      name: '状态',
      nameNew: '状态',
      check: true,
      width: 60,
      max: 80,
      min: 60,
      align: 'center'
    },
    {
      key: '1',
      name: '父件编码',
      nameNew: '父件编码',
      check: true,
      width: 80,
      max: 150,
      min: 80,
      align: 'left'
    },
    {
      key: '2',
      name: '父件名称',
      nameNew: '父件名称',
      check: true,
      width: 100,
      max: 300,
      min: 100,
      align: 'left'
    },
    {
      key: '3',
      name: '规格型号',
      nameNew: '规格型号',
      check: true,
      width: 80,
      max: 150,
      min: 80,
      align: 'left'
    },
    {
      key: '4',
      name: '版本号',
      nameNew: '版本号',
      check: true,
      width: 80,
      max: 150,
      min: 80,
      align: 'center'
    },
    {
      key: '5',
      name: '计量单位',
      nameNew: '计量单位',
      check: true,
      width: 80,
      max: 150,
      min: 80,
      align: 'center'
    },
    {
      key: '6',
      name: '数量',
      nameNew: '数量',
      check: true,
      width: 120,
      max: 150,
      min: 100,
      align: 'center'
    }, {
      key: '7',
      name: '成品率',
      nameNew: '成品率%',
      check: true,
      width: 120,
      max: 150,
      min: 100,
      align: 'center'
    },{
      key: '8',
      name: '默认BOM',
      nameNew: '默认BOM',
      check: true,
      width: 120,
      max: 150,
      min: 100,
      align: 'center'
    },{
      key: '9',
      name: '是否停用',
      nameNew: '是否停用',
      check: true,
      width: 120,
      max: 150,
      min: 100,
      align: 'center'
    },{
      key: '10',
      name: '制单人',
      nameNew: '制单人',
      check: true,
      width: 120,
      max: 150,
      min: 100,
      align: 'center'
    },{
      key: '11',
      name: '审核日期',
      nameNew: '审核日期',
      check: true,
      width: 120,
      max: 150,
      min: 100,
      align: 'center'
    },
  ],
  DATA2: [
    {
      key: '0',
      name: '子件编码',
      nameNew: '子件编码',
      check: true,
      width: 80,
      max: 150,
      min: 80,
      align: 'left'
    },
    {
      key: '1',
      name: '子件名称',
      nameNew: '子件名称',
      check: true,
      width: 100,
      max: 300,
      min: 100,
      align: 'left'
    },
    {
      key: '2',
      name: '规格型号',
      nameNew: '规格型号',
      check: true,
      width: 80,
      max: 150,
      min: 80,
      align: 'left'
    },
    {
      key: '3',
      name: '子件BOM',
      nameNew: '子件BOM',
      check: true,
      width: 80,
      max: 150,
      min: 80,
      align: 'center'
    },
    {
      key: '4',
      name: '默认仓库',
      nameNew: '默认仓库',
      check: true,
      width: 80,
      max: 150,
      min: 80,
      align: 'left'
    },
    {
      key: '5',
      name: '计量单位',
      nameNew: '计量单位',
      check: true,
      width: 120,
      max: 150,
      min: 100,
      align: 'center'
    }, {
      key: '6',
      name: '数量',
      nameNew: '数量',
      check: true,
      width: 120,
      max: 150,
      min: 100,
      align: 'center'
    },{
      key: '7',
      name: '损耗率',
      nameNew: '损耗率%',
      check: true,
      width: 120,
      max: 150,
      min: 100,
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

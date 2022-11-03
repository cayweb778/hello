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
      width: 140,
      slots: { customRender: 'alignRadio' },
    }
  ],
  J:[
    {
      key: '14',
      name: '状态',
      nameNew: '状态',
      check: true,
      width: 50,
      max: 100,
      min: 50,
      align: 'center'
    },
    {
      key: '0',
      name: '科目编码',
      nameNew: '科目编码',
      check: true,
      width: 300,
      max: 500,
      min: 100,
      align: 'left',
    },
    {
      key: '1',
      name: '科目名称',
      nameNew: '科目名称',
      check: true,
      width: 300,
      max: 500,
      min: 100,
      align: 'left',
    },
    {
      key: '2',
      name: '方向',
      nameNew: '方向',
      check: true,
      width: 80,
      max: 180,
      min: 80,
      align: 'center',
    },
    {
      key: '3',
      name: '科目类型',
      nameNew: '科目类型',
      check: true,
      width: 100,
      max: 180,
      min: 100,
      align: 'center',
    },
    {
      key: '4',
      name: '辅助核算项',
      nameNew: '辅助核算项',
      check: true,
      width: 220,
      max: 500,
      min: 160,
      align: 'center'
    },
    {
      key: '5',
      name: '现金账',
      nameNew: '现金账',
      check: true,
      width: 80,
      max: 80,
      min: 80,
      align: 'center'
    },
    {
      key: '6',
      name: '银行账',
      nameNew: '银行账',
      check: true,
      width: 80,
      max: 80,
      min: 80,
      align: 'center'
    },
    {
      key: '7',
      name: '日记账',
      nameNew: '日记账',
      check: true,
      width: 80,
      max: 80,
      min: 80,
      align: 'center'
    },
    {
      key: '8',
      name: '数量/单位',
      nameNew: '数量/单位',
      check: true,
      width: 100,
      max: 100,
      min: 100,
      align: 'center'
    },
    {
      key: '9',
      name: '外币/名称',
      nameNew: '外币/名称',
      check: true,
      width: 100,
      max: 300,
      min: 100,
      align: 'center'
    },
    {
      key: '10',
      name: '下级控制',
      nameNew: '下级控制',
      check: false,
      width: 100,
      max: 100,
      min: 100,
      align: 'center',
    },
    {
      key: '11',
      name: '辅助控制',
      nameNew: '辅助控制',
      check: false,
      width: 100,
      max: 100,
      min: 100,
      align: 'center',
    },

  ],
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


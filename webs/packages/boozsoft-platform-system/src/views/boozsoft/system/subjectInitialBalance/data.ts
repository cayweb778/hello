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
 /*   {
      key: '1',
      title: '显示',
      dataIndex: 'check',
      align: 'center',
      width: 80,
      slots: {customRender: 'checkBox'},
    },*/
    {
      key: '3',
      title: '显示名称',
      dataIndex: 'nameNew',
      width: 150,
      align: 'left',
      slots: {customRender: 'nameInput',},
    },
    {
      key: '4',
      title: '宽度(范围)',
      dataIndex: 'width',
      align: 'center',
      width: 140,
      slots: {customRender: 'widthInput',},
    },
    {
      key: '5',
      title: '对齐方式',
      dataIndex: 'align',
      align: 'center',
      width: 140,
      slots: {customRender: 'alignRadio'},
    }
  ],
  DATA1: [
    {
      key: '0',
      name: '操作',
      nameNew: '操作',
      check: true,
      width: 60,
      max: 80,
      min: 50,
      align: 'center'
    },
    {
      key: '1',
      name: '科目编码',
      nameNew: '科目编码',
      check: true,
      width: 150,
      max: 200,
      min: 60,
      align: 'left'
    }, {
      key: '2',
      name: '科目名称',
      nameNew: '科目名称',
      check: true,
      width: 250,
      max: 300,
      min: 100,
      align: 'left'
    },{
      key: '3',
      name: '方向',
      nameNew: '方向',
      check: true,
      width: 60,
      max: 80,
      min: 60,
      align: 'center'
    },{
      key: '4',
      name: '辅助项',
      nameNew: '辅助项',
      check: true,
      width: 200,
      max: 350,
      min: 100,
      align: 'left'
    },{
      key: '5',
      name: '计量单位',
      nameNew: '计量单位',
      check: false,
      width: 100,
      max: 150,
      min: 60,
      align: 'center'
    },{
      key: '6',
      name: '外币币种',
      nameNew: '外币币种',
      check: false,
      width: 200,
      max: 300,
      min: 60,
      align: 'center'
    },{
      key: '7',
      name: '数量',
      nameNew: '数量',
      check: false,
      width: 120,
      max: 150,
      min: 60,
      align: 'center'
    },{
      key: '8',
      name: '外币金额',
      nameNew: '外币金额',
      check: false,
      width: 200,
      max: 300,
      min: 80,
      align: 'right'
    },{
      key: '9',
      name: '本币借方金额',
      nameNew: '本币借方金额',
      check: true,
      width: 200,
      max: 300,
      min: 80,
      align: 'right'
    },{
      key: '10',
      name: '本币贷方金额',
      nameNew: '本币贷方金额',
      check: true,
      width: 200,
      max: 300,
      min: 80,
      align: 'right'
    },
  ],
  DATA2: [
    {
    key: '0',
    name: '操作',
    nameNew: '操作',
    check: true,
    width: 60,
    max: 80,
    min: 50,
    align: 'center'
    },
    {
      key: '1',
      name: '科目编码',
      nameNew: '科目编码',
      check: true,
      width: 150,
      max: 200,
      min: 80,
      align: 'left'
    }, {
      key: '2',
      name: '科目名称',
      nameNew: '科目名称',
      check: true,
      width: 250,
      max: 300,
      min: 100,
      align: 'left'
    },{
      key: '7',
      name: '方向',
      nameNew: '方向',
      check: true,
      width: 80,
      max: 80,
      min: 50,
      align: 'center'
    },
    {
      key: '3',
      name: '期初借方',
      nameNew: '期初借方',
      check: true,
      width: 200,
      max: 300,
      min: 100,
      align: 'right',
    },{
      key: '4',
      name: '期初贷方',
      nameNew: '期初贷方',
      check: true,
      width: 200,
      max: 300,
      min: 100,
      align: 'right',
    },
    {
      key: '5',
      name: '累计借方',
      nameNew: '累计借方',
      check: true,
      width: 200,
      max: 300,
      min: 100,
      align: 'right',
    },
    {
      key: '6',
      name: '累计贷方',
      nameNew: '累计贷方',
      check: true,
      width: 200,
      max: 300,
      min: 100,
      align: 'right',
    },
    {
      key: '8',
      name: '年初余额',
      nameNew: '年初余额',
      check: true,
      width: 200,
      max: 300,
      min: 100,
      align: 'right',
    }
  ],
  DATA22: [
    {
      key: '1',
      name: '录入',
      nameNew: '录入',
      check: true,
      width: 60,
      max: 80,
      min: 50,
      align: 'center'
    },
    {
      key: '2',
      name: '科目编码',
      nameNew: '科目编码',
      check: true,
      width: 150,
      max: 200,
      min: 80,
      align: 'left'
    }, {
      key: '3',
      name: '科目名称',
      nameNew: '科目名称',
      check: true,
      width: 250,
      max: 300,
      min: 100,
      align: 'left'
    },{
      key: '4',
      name: '核算项',
      nameNew: '核算项',
      check: true,
      width: 200,
      max: 300,
      min: 80,
      align: 'left'
    },
    {
      key: '5',
      name: '期初余额',
      nameNew: '期初余额',
      check: true,
      width: 475,
      max: 650,
      min: 300,
      align: 'center',
      children: [
        {
          key: '5-1',
          name: '数量',
          nameNew: '数量',
          check: true,
          width: 100,
          max: 125,
          min: 60,
          align: 'center',
        },
        {
          key: '5-2',
          name: '外币金额',
          nameNew: '外币金额',
          check: true,
          width: 125,
          max: 175,
          min: 80,
          align: 'right',
        },
        {
          key: '5-3',
          name: '本币借方',
          nameNew: '本币金额',
          check: true,
          width: 125,
          max: 175,
          min: 80,
          align: 'right',
        },
        {
          key: '5-4',
          name: '本币贷方',
          nameNew: '本币金额',
          check: true,
          width: 125,
          max: 175,
          min: 80,
          align: 'right',
        }
      ],
    },
    {
      key: '6',
      name: '累计发生',
      nameNew: '累计发生',
      check: true,
      width: 575,
      max: 940,
      min: 480,
      align: 'center',
      children: [
        {
          key: '6-1',
          name: '数量借方',
          nameNew: '数量借方',
          check: true,
          width: 100,
          max: 120,
          min: 80,
          align: 'center',
        },{
          key: '6-2',
          name: '数量贷方',
          nameNew: '数量贷方',
          check: true,
          width: 100,
          max: 120,
          min: 80,
          align: 'center',
        },
        {
          key: '6-3',
          name: '外币借方',
          nameNew: '外币借方',
          check: true,
          width: 125,
          max: 175,
          min: 80,
          align: 'right',
        },{
          key: '6-4',
          name: '外币贷方',
          nameNew: '外币贷方',
          check: true,
          width: 125,
          max: 175,
          min: 80,
          align: 'right',
        },
        {
          key: '6-5',
          name: '本币借方',
          nameNew: '本币金额',
          check: true,
          width: 125,
          max: 175,
          min: 80,
          align: 'right',
        },
        {
          key: '6-6',
          name: '本币贷方',
          nameNew: '本币金额',
          check: true,
          width: 125,
          max: 175,
          min: 80,
          align: 'right',
        }
      ],
    },
    {
      key: '7',
      name: '年初余额',
      nameNew: '年初余额',
      check: true,
      width: 475,
      max: 650,
      min: 300,
      align: 'center',
      children: [
        {
          key: '7-1',
          name: '数量',
          nameNew: '数量',
          check: true,
          width: 100,
          max: 125,
          min: 60,
          align: 'center',
        },
        {
          key: '7-2',
          name: '外币金额',
          nameNew: '外币金额',
          check: true,
          width: 125,
          max: 175,
          min: 80,
          align: 'right',
        },
        {
          key: '7-3',
          name: '本币借方',
          nameNew: '本币金额',
          check: true,
          width: 125,
          max: 175,
          min: 80,
          align: 'right',
        },
        {
          key: '7-4',
          name: '本币贷方',
          nameNew: '本币金额',
          check: true,
          width: 125,
          max: 175,
          min: 80,
          align: 'right',
        }
      ],
    }
  ]
})
export const voucherNoAutocomplete = (val, rule) => {
  let numberStr = parseInt(val) + ''
  let difference = rule - numberStr.length
  let prefix = ''
  let i = 0
  while (i < difference) {
    prefix += '0'
    i++;
  }
  return prefix + numberStr
}
export function isRealNum(val) {
  // isNaN()函数 把空串 空格 以及NUll 按照0来处理 所以先去除，
  if (val === "" || val == null) {
    return false;
  }
  if (!isNaN(val)) {
    //对于空数组和只有一个数值成员的数组或全是数字组成的字符串，isNaN返回false，例如：'123'、[]、[2]、['123'],isNaN返回false,   //所以如果不需要val包含这些特殊情况，则这个判断改写为if(!isNaN(val) && typeof val === 'number' )
    return true;
  } else {
    return false;
  }
}
export function money(val: any) { // 金额格式化
  if (val == null) val = ''
  val = val.toString().replace(/\$|\,/g, '')
  if (isNaN(val)) {
    val = '0'
  }
  const sign = (val === (val = Math.abs(val)))
  val = Math.floor(val * 100 + 0.50000000001)
  let cents: string = (val % 100) + ''
  val = Math.floor(val / 100).toString()
  if (parseInt(cents) < 10) {
    cents = '0' + cents
  }
  for (let i = 0; i < Math.floor((val.length - (1 + i)) / 3); i++) {
    val = val.substring(0, val.length - (4 * i + 3)) + ',' + val.substring(val.length - (4 * i + 3))
  }
  let re = (((sign) ? '' : '') + val + '.' + cents)
  return re == ('0.00' || '0') ? '' : re
}
export function combineParameters(staticList: any, dbList: any) {
  staticList.forEach(item => {
    dbList.forEach(item2 => {
      if (item.key === item2.key && item.name === item2.name) {
        item.nameNew = item2.nameNew
        item.width = parseInt(item2.width)
        item.check = item2.check == 'true'
        item.align = item2.align
      }
    })
  })
  return staticList
}
export const intervalWorking = (val) => {
  return val.replace(/-/g,'.').replace(/~/g,'-')
}


//////////////////////// 新的 /////////////////////////
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

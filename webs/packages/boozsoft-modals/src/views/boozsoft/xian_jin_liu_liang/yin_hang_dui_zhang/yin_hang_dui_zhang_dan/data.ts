// @ts-ignore
import {findByKemuAndDate} from "/@/api/record/system/bank-statement";

export const CrudApi = {
  list: findByKemuAndDate,
  columns: [
    {
      title: '是否对账',
      dataIndex: 'flag',
      width: 100,
      ellipsis: true,
      slots: { customRender: 'flag' }
    },
    {
      title: '日期',
      dataIndex: 'statementDate',
      width: 100,
      customCell: () => {		// 在此处可以修改单元格中的样式
        return { style: { 'color': 'blue'}} // return 想要设置的样式
      },
      ellipsis: true
    },
    {
      title: '结算方式',
      dataIndex: 'settModes',
      width: 100,
      ellipsis: true,
      slots: { customRender: 'settModes'}
    },
    {
      title: '票号',
      dataIndex: 'piaohao',
      width: 100,
      ellipsis: true
    },
    {
      title: '对方单位',
      dataIndex: 'duifangUnit',
      customCell: () => {		// 在此处可以修改单元格中的样式
        return { style: { 'text-align': 'left'}} // return 想要设置的样式
      },
      ellipsis: true
    },
    {
      title: '摘要',
      dataIndex: 'remarks',
      customCell: () => {		// 在此处可以修改单元格中的样式
        return { style: { 'text-align': 'left'}} // return 想要设置的样式
      },
      ellipsis: true
    },
    {
      title: '借方金额',
      dataIndex: 'jie',
      width: 100,
      customCell: () => {		// 在此处可以修改单元格中的样式
        return { style: { 'text-align': 'right', '-webkit-line-clamp': 2 }} // return 想要设置的样式
      },
      ellipsis: true,
      slots: { customRender: 'jie' }
    },
    {
      title: '贷方金额',
      dataIndex: 'dai',
      width: 100,
      customCell: () => {		// 在此处可以修改单元格中的样式
        return { style: { 'text-align': 'right', '-webkit-line-clamp': 2 }} // return 想要设置的样式
      },
      ellipsis: true,
      slots: { customRender: 'dai' }
    },
    {
      title: '余额',
      dataIndex: 'yue',
      width: 100,
      customCell: () => {		// 在此处可以修改单元格中的样式
        return { style: { 'text-align': 'right', '-webkit-line-clamp': 2 }} // return 想要设置的样式
      },
      ellipsis: true
    },
    /*{
      title: '币种',
      dataIndex: 'currencyId',
      defaultHidden: true,
      width: 100,
      customCell: () => {		// 在此处可以修改单元格中的样式
        return { style: { 'text-align': 'right', '-webkit-line-clamp': 2 }} // return 想要设置的样式
      },
      ellipsis: true
    }*/
    {
      title: '对账单号',
      dataIndex: 'statementNum',
      width: 120,
      ellipsis: true
    },
  ],
  editData: {
    id: '',
    statementDate: '',
    kemuCode: '',
    duifangUnit: '',
    settModes: '',
    piaohao: '',
    jie: '',
    dai: '',
    fangxiang: '',
    flag: '',
    statementNum: '',
    remarks: '',
    currencyId: ''
  }
}

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
  DATA: [
    {
      key: '0',
      name: '是否对账',
      nameNew: '是否对账',
      check: true,
      width: 100,
      max: 150,
      min: 80,
      align: 'center'
    },
    {
      key: '1',
      name: '日期',
      nameNew: '日期',
      check: true,
      width: 120,
      max: 150,
      min: 80,
      align: 'center'
    },
    {
      key: '2',
      name: '结算方式',
      nameNew: '结算方式',
      check: true,
      width: 120,
      max: 150,
      min: 80,
      align: 'center'
    },
    {
      key: '3',
      name: '票号',
      nameNew: '票号',
      check: true,
      width: 120,
      max: 150,
      min: 80,
      align: 'center'
    },
    {
      key: '4',
      name: '对方单位',
      nameNew: '对方单位',
      check: true,
      width: 200,
      max: 400,
      min: 150,
      align: 'left'
    },
    {
      key: '5',
      name: '摘要',
      nameNew: '摘要',
      check: true,
      width: 200,
      max: 400,
      min: 150,
      align: 'left'
    },
    {
      key: '6',
      name: '借方金额',
      nameNew: '借方金额',
      check: true,
      width: 120,
      max: 200,
      min: 80,
      align: 'right'
    },
    {
      key: '7',
      name: '贷方金额',
      nameNew: '贷方金额',
      check: true,
      width: 120,
      max: 200,
      min: 80,
      align: 'right'
    },
    {
      key: '8',
      name: '余额',
      nameNew: '余额',
      check: true,
      width: 120,
      max: 200,
      min: 80,
      align: 'right'
    },
    {
      key: '9',
      name: '对账单号',
      nameNew: '对账单号',
      check: true,
      width: 100,
      max: 200,
      min: 80,
      align: 'center'
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

export function assemblyDynamicColumn(lanmuList:any,columnList:any){
  columnList.forEach(cObj=>{
    lanmuList.forEach((lObj,index)=>{
      if (cObj.title === lObj.name){
        cObj.title = thisName(index+'',lanmuList)
        cObj.width = thisWidth(index+'',lanmuList)
        cObj.align = thisAlign(index+'',lanmuList)
        cObj.ifShow = thisIsShow(index+'',lanmuList)
      }
    })
  })
  return columnList;
}

/*********************** 静态方法 ***********************/
// 动态名称
const thisName = (index,thisData)=>{
  let value = ''
  if (index.toString().indexOf('-') != -1){
    let arr = index.split('-');
    if (arr.length == 2)  {
      value = thisData[parseInt(arr[0])].children[parseInt(arr[1])-1].nameNew
      if ('' == value)value = thisData[parseInt(arr[0])].children[parseInt(arr[1])-1].name
    }else{
      value = thisData[parseInt(arr[0])].children[parseInt(arr[1])-1].children[parseInt(arr[2])-1].nameNew
      if ('' == value)value = thisData[parseInt(arr[0])].children[parseInt(arr[1])-1].children[parseInt(arr[2])-1].name
    }
  }else{
    value = thisData[index].nameNew
    if ('' == value)value = thisData[index].name
  }
  return value
}
// 当前宽度
const thisWidth = (index,thisData)=>{
  let value = 0
  if (index.toString().indexOf('-') != -1){
    let arr = index.split('-');
    if (arr.length == 2)  {
      value = thisData[parseInt(arr[0])].children[parseInt(arr[1])-1].width
    }else{
      value = thisData[parseInt(arr[0])].children[parseInt(arr[1])-1].children[parseInt(arr[2])-1].width
    }
  }else{
    value = thisData[index].width
  }
  return  parseInt(value)
}
// 是否显示
const thisIsShow = (index,thisData)=>{
  let value = false
  if (index.toString().indexOf('-') != -1){
    let arr = index.split('-');
    if (arr.length == 2)  {
      value = thisData[parseInt(arr[0])].children[parseInt(arr[1])-1].check
    }else{
      value = thisData[parseInt(arr[0])].children[parseInt(arr[1])-1].children[parseInt(arr[2])-1].check
    }
  }else{
    value = thisData[index].check
  }
  return  value
}
// 对齐方式
const thisAlign = (index,thisData)=>{
  let value = 'center';
  if (index.toString().indexOf('-') != -1){
    let arr = index.split('-');
    if (arr.length == 2)  {
      value = thisData[parseInt(arr[0])].children[parseInt(arr[1])-1].align
    }else{
      value = thisData[parseInt(arr[0])].children[parseInt(arr[1])-1].children[parseInt(arr[2])-1].align
    }
  }else{
    value = thisData[index].align
  }
  return  value
}


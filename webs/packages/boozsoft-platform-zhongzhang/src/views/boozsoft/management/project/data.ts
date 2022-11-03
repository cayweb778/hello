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
      width: 80,
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
      name: '生效状态',
      nameNew: '生效状态',
      check: true,
      width: 100,
      max: 120,
      min: 80,
      align: 'center'
    },
    {
      key: '1',
      name: '项目编码',
      nameNew: '项目编码',
      check: true,
      width: 120,
      max: 150,
      min: 80,
      align: 'center'
    },
    {
      key: '2',
      name: '项目名称',
      nameNew: '项目名称',
      check: true,
      width: 300,
      max: 400,
      min: 150,
      align: 'left'
    },
    {
      key: '3',
      name: '项目分类',
      nameNew: '项目分类',
      check: true,
      width: 200,
      max: 200,
      min: 80,
      align: 'left'
    },
    /*{
      key: '3',
      name: '所属部门',
      nameNew: '所属部门',
      check: true,
      width: 150,
      max: 200,
      min: 80,
      align: 'center'
    },*/
    {
      key: '4',
      name: '开始日期',
      nameNew: '开始日期',
      check: true,
      width: 120,
      max: 150,
      min: 100,
      align: 'center'
    },
    {
      key: '5',
      name: '结束日期',
      nameNew: '开始日期',
      check: true,
      width: 120,
      max: 150,
      min: 100,
      align: 'center'
    },
    {
      key: '6',
      name: '项目负责人',
      nameNew: '项目负责人',
      check: true,
      width: 120,
      max: 150,
      min: 80,
      align: 'center'
    },
    {
      key: '7',
      name: '是否结算',
      nameNew: '是否结算',
      check: true,
      width: 120,
      max: 150,
      min: 100,
      align: 'center'
    },
/*    {
      key: '9',
      name: '项目预算',
      nameNew: '项目预算',
      check: false,
      width: 150,
      max: 200,
      min: 100,
      align: 'center'
    },*/
    {
      key: '8',
      name: '是否生效',
      nameNew: '是否生效',
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
  let value:any = 0
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

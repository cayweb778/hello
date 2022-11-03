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
      name: '审核状态',
      nameNew: '审核状态',
      check: true,
      width: 100,
      max: 150,
      min: 80,
      align: 'center',
      isFixed: true,
    },
    {
      key: '1',
      name: '报表编码',
      nameNew: '报表编码',
      check: true,
      width: 200,
      max: 300,
      min: 100,
      align: 'left',
      isFixed: true,
    }
    ,
    {
      key: '2',
      name: '模板名称',
      nameNew: '模板名称',
      check: true,
      width: 200,
      max: 300,
      min: 100,
      align: 'left'
    }
    ,{
      key: '3',
      name: '年',
      nameNew: '年',
      check: true,
      width: 80,
      max: 150,
      min: 80,
      align: 'center',
      isFixed: true,
    }
    ,{
      key: '4',
      name: '月',
      nameNew: '月',
      check: true,
      width: 80,
      max: 150,
      min: 80,
      align: 'center',
      isFixed: true,
    },
    {
      key: '5',
      name: '季度',
      nameNew: '季度',
      check: true,
      width: 80,
      max: 150,
      min: 80,
      align: 'center',
      isFixed: true,
    },
    {
      key: '6',
      name: '类型',
      nameNew: '类型',
      check: true,
      width: 80,
      max: 150,
      min: 80,
      align: 'center',
      isFixed: true,
    },
    {
      key: '7',
      name: '创建人',
      nameNew: '创建人',
      check: true,
      width: 120,
      max: 150,
      min: 100,
      align: 'center'
    }
    ,{
      key: '8',
      name: '创建日期',
      nameNew: '创建日期',
      check: true,
      width: 120,
      max: 150,
      min: 100,
      align: 'center'
    },{
      key: '9',
      name: '审核人',
      nameNew: '审核人',
      check: true,
      width: 120,
      max: 150,
      min: 100,
      align: 'center'
    }
    ,{
      key: '10',
      name: '审核日期',
      nameNew: '审核日期',
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

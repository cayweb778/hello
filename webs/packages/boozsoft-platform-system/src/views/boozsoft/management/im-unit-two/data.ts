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
      key: '2',
      title: '显示名称',
      dataIndex: 'nameNew',
      width: 150,
      align: 'left',
      slots: { customRender: 'nameInput', },
    },
    {
      key: '3',
      title: '宽度(范围)',
      dataIndex: 'width',
      align: 'center',
      width: 140,
      slots: { customRender: 'widthInput', },
    },
    {
      key: '4',
      title: '对齐方式',
      dataIndex: 'align',
      align: 'center',
      width: 140,
      slots: { customRender: 'alignRadio' },
    }
  ],
  DATA: [
    {
      key: '13',
      name: '状态',
      nameNew: '状态',
      check: true,
      isFixed: true,
      width: 60,
      max: 100,
      min: 60,
      align: 'center'
    },{
      key: '0',
      name: '账套代码',
      nameNew: '账套代码',
      check: true,
      isFixed: true,
      width: 80,
      max: 120,
      min: 80,
      align: 'center'
    },{
      key: '1',
      name: '账套名称',
      nameNew: '账套名称',
      check: true,
      isFixed: true,
      width: 300,
      max: 350,
      min: 150,
      align: 'left'
    }
    ,
    {
      key: '2',
      name: '公司代码',
      nameNew: '公司代码',
      check: true,
      width: 80,
      isFixed: false,
      max: 120,
      min: 80,
      align: 'center'
    }
    ,
    {
      key: '3',
      name: '公司名称',
      nameNew: '公司名称',
      isFixed: false,
      check: true,
      width: 300,
      max: 350,
      min: 150,
      align: 'left'
    }
    ,{
      key: '4',
      name: '所属组织',
      nameNew: '所属组织',
      check: true,
      width: 300,
      max: 300,
      min: 80,
      align: 'center'
    }
    ,{
      key: '5',
      name: '会计准则',
      nameNew: '会计准则',
      check: true,
      width: 200,
      max: 250,
      min: 150,
      align: 'left'
    },{
      key: '6',
      name: '科目级次',
      nameNew: '科目级次',
      check: true,
      width: 250,
      max: 300,
      min: 150,
      align: 'left'
    },{
      key: '7',
      name: '启用日期',
      nameNew: '启用日期',
      check: true,
      width: 120,
      max: 150,
      min: 100,
      align: 'center'
    },{
      key: '8',
      name: '本位币',
      nameNew: '本位币',
      check: true,
      width: 150,
      max: 150,
      min: 100,
      align: 'center'
    },{
      key: '9',
      name: "管理性质",
      nameNew: "管理性质",
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

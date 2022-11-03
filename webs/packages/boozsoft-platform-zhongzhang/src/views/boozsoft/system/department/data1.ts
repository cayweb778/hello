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
      name: '状态',
      nameNew: '状态',
      check: true,
      width: 70,
      max: 100,
      min: 50,
      isFixed: true,
      align: 'center'
    },
    {
      key: '1',
      name: '编码',
      nameNew: '编码',
      check: true,
      width: 120,
      max: 100,
      min: 80,
      isFixed: true,
      align: 'left'
    },
    {
      key: '2',
      name: '姓名',
      nameNew: '姓名',
      check: true,
      width: 120,
      max: 120,
      min: 100,
      isFixed: true,
      align: 'left'
    },
    {
      key: '3',
      name: '工号',
      nameNew: '工号',
      check: false,
      width: 80,
      max: 100,
      min: 50,
      align: 'left'
    },
    {
      key: '4',
      name: '性别',
      nameNew: '性别',
      check: true,
      width: 100,
      max: 150,
      min: 80,
      isFixed: true,
      align: 'center'
    },
    {
      key: '5',
      name: '部门名称',
      nameNew: '部门名称',
      check: false,
      width: 200,
      max: 300,
      min: 100,
      align: 'left'
    },
    {
      key: '6',
      name: '人员类别',
      nameNew: '人员类别',
      check: false,
      width: 150,
      max: 200,
      min: 100,
      align: 'left'
    },
    {
      key: '7',
      name: '职务',
      nameNew: '职务',
      check: false,
      width: 100,
      max: 150,
      min: 80,
      align: 'left'
    },
    {
      key: '8',
      name: '手机号',
      nameNew: '手机号',
      check: true,
      width: 100,
      max: 150,
      min: 80,
      align: 'left'
    },
    {
      key: '9',
      name: '微信',
      nameNew: '微信',
      check: false,
      width: 100,
      max: 150,
      min: 80,
      align: 'left'
    },
    {
      key: '10',
      name: 'Email',
      nameNew: 'Email',
      check: true,
      width: 100,
      max: 150,
      min: 80,
      align: 'left'
    },
    {
      key: '11',
      name: '钉钉',
      nameNew: '钉钉',
      check: false,
      width: 100,
      max: 150,
      min: 80,
      align: 'left'
    },
    {
      key: '12',
      name: '证件类型',
      nameNew: '证件类型',
      check: false,
      width: 100,
      max: 150,
      min: 80,
      align: 'left'
    },
    {
      key: '13',
      name: '证件号码',
      nameNew: '证件号码',
      check: false,
      width: 100,
      max: 150,
      min: 80,
      align: 'left'
    },
    {
      key: '14',
      name: '出生日期',
      nameNew: '出生日期',
      check: false,
      width: 100,
      max: 150,
      min: 80,
      align: 'left'
    },
    {
      key: '15',
      name: '行政区划',
      nameNew: '行政区划',
      check: true,
      width: 100,
      max: 150,
      min: 80,
      align: 'left'
    },
    {
      key: '16',
      name: '详细地址',
      nameNew: '详细地址',
      check: false,
      width: 150,
      max: 300,
      min: 100,
      align: 'left'
    },
    {
      key: '17',
      name: '开户银行',
      nameNew: '开户银行',
      check: false,
      width: 100,
      max: 150,
      min: 80,
      align: 'left'
    },
    {
      key: '18',
      name: '开户地',
      nameNew: '开户地',
      check: false,
      width: 100,
      max: 150,
      min: 80,
      align: 'left'
    },
    {
      key: '19',
      name: '银行账号',
      nameNew: '银行账号',
      check: false,
      width: 100,
      max: 150,
      min: 80,
      align: 'left'
    },
    {
      key: '20',
      name: '银行行号',
      nameNew: '银行行号',
      check: false,
      width: 100,
      max: 150,
      min: 80,
      align: 'left'
    },
    {
      key: '21',
      name: '管理类型',
      nameNew: '管理类型',
      check: false,
      width: 100,
      max: 150,
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

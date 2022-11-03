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
  DATA1: [
    {
      key: '0',
      name: '序号',
      nameNew: '序号',
      check: true,
      width: 60,
      max: 80,
      min: 60,
      align: 'center'
    },
    {
      key: '1',
      name: '制单日期',
      nameNew: '制单日期',
      check: true,
      width: 120,
      max: 140,
      min: 100,
      align: 'center'
    },
    {
      key: '2',
      name: '凭证字号',
      nameNew: '凭证字号',
      check: true,
      width: 80,
      max: 100,
      min: 60,
      align: 'center'
    },
    {
      key: '3',
      name: '摘要',
      nameNew: '摘要',
      check: true,
      width: 180,
      max: 180,
      min: 120,
      align: 'left'
    },
    {
      key: '4',
      name: '往来单位简称',
      nameNew: '往来单位简称',
      check: true,
      width: 120,
      max: 150,
      min: 100,
      align: 'center'
    },
    {
      key: '5',
      name: '借方金额',
      nameNew: '借方金额',
      check: true,
      width: 120,
      max: 150,
      min: 100,
      align: 'right'
    },
    {
      key: '6',
      name: '贷方金额',
      nameNew: '贷方金额',
      check: true,
      width: 120,
      max: 150,
      min: 100,
      align: 'right'
    },
    {
      key: '7',
      name: '未核销金额',
      nameNew: '未核销金额',
      check: true,
      width: 120,
      max: 150,
      min: 100,
      align: 'right'
    }, {
      key: '8',
      name: '两清',
      nameNew: '两清',
      check: true,
      width: 100,
      max: 120,
      min: 80,
      align: 'center'
    }, {
      key: '9',
      name: '核销标记号',
      nameNew: '核销标记号',
      check: true,
      width: 80,
      max: 100,
      min: 60,
      align: 'center'
    },
  ],
})
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

export function changeDefaultDynamics(list,num) {
  // 改变默认数据
  dynamicColumnAndDataModel['DATA'+num] = list
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


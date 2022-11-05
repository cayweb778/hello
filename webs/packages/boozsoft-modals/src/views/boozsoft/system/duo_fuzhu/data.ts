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
      key: '0',
      name: '期间',
      nameNew: '期间',
      check: true,
      width: 120,
      max: 180,
      min: 120,
      align: 'center'
    },
    {
      key: '1',
      name: '凭证号',
      nameNew: '凭证号',
      check: true,
      width: 80,
      max: 120,
      min: 80,
      align: 'center'
    },
    {
      key: '2',
      name: '客户编码',
      nameNew: '客户编码',
      check: true,
      width: 150,
      max: 180,
      min: 80,
      align: 'left'
    },
    {
      key: '3',
      name: '客户名称',
      nameNew: '客户名称',
      check: true,
      width: 150,
      max: 180,
      min: 80,
      align: 'left'
    },
    {
      key: '4',
      name: '摘要',
      nameNew: '摘要',
      check: true,
      width: 600,
      max: 600,
      min: 200,
      align: 'left'
    },
    {
      key: '5',
      name: '借方',
      nameNew: '借方',
      check: true,
      width: 180,
      max: 220,
      min: 180,
      align: 'center'
    },
    {
      key: '6',
      name: '贷方',
      nameNew: '贷方',
      check: true,
      width: 180,
      max: 220,
      min: 180,
      align: 'center'
    },
    {
      key: '7',
      name: '方向',
      nameNew: '方向',
      check: true,
      width: 50,
      max: 100,
      min: 50,
      align: 'center'
    },
    {
      key: '8',
      name: '余额',
      nameNew: '余额',
      check: true,
      width: 180,
      max: 220,
      min: 180,
      align: 'center'
    }
  ],
  SJ: [
    {
      key: '0',
      name: '期间',
      nameNew: '期间',
      check: true,
      width: 120,
      max: 180,
      min: 120,
      align: 'center'
    },
    {
      key: '1',
      name: '凭证号',
      nameNew: '凭证号',
      check: true,
      width: 80,
      max: 120,
      min: 80,
      align: 'center'
    },
    {
      key: '2',
      name: '客户编码',
      nameNew: '客户编码',
      check: true,
      width: 150,
      max: 180,
      min: 80,
      align: 'left'
    },
    {
      key: '3',
      name: '客户名称',
      nameNew: '客户名称',
      check: true,
      width: 150,
      max: 180,
      min: 80,
      align: 'left'
    },
    {
      key: '4',
      name: '摘要',
      nameNew: '摘要',
      check: true,
      width: 600,
      max: 600,
      min: 200,
      align: 'left'
    },
    {
      key: '5',
      name: '计量单位',
      nameNew: '计量单位',
      check: true,
      width: 100,
      max: 120,
      min: 80,
      align: 'center'
    },
    {
      key: '6',
      name: '借方',
      nameNew: '借方',
      check: true,
      width: 350,
      max: 350,
      min: 280,
      align: 'center',
      children: [
        {
          key: '6-1',
          name: '数量',
          nameNew: '数量',
          width: 180,
          max: 180,
          min: 100,
          check: true,
          align: 'center',
        },
        {
          key: '6-2',
          name: '本币金额',
          nameNew: '本币金额',
          check: true,
          width: 180,
          max: 180,
          min: 100,
          align: 'center',
        }
      ],
    },
    {
      key: '7',
      name: '贷方',
      nameNew: '贷方',
      check: true,
      width: 350,
      max: 350,
      min: 280,
      align: 'center',
      children: [
        {
          key: '7-1',
          name: '数量',
          nameNew: '数量',
          check: true,
          width: 180,
          max: 180,
          min: 100,
          align: 'center',
        },
        {
          key: '7-2',
          name: '本币金额',
          nameNew: '本币金额',
          check: true,
          width: 180,
          max: 180,
          min: 100,
          align: 'center',
        }
      ],
    },
    {
      key: '8',
      name: '方向',
      nameNew: '方向',
      check: true,
      width: 50,
      max: 100,
      min: 50,
      align: 'center',
    },
    {
      key: '9',
      name: '余额',
      nameNew: '余额',
      check: true,
      width: 350,
      max: 350,
      min: 280,
      align: 'center',
      children: [
        {
          key: '9-1',
          name: '数量',
          nameNew: '数量',
          check: true,
          width: 180,
          max: 180,
          min: 100,
          align: 'center',
        },
        {
          key: '9-2',
          name: '本币金额',
          nameNew: '本币金额',
          check: true,
          width: 180,
          max: 180,
          min: 100,
          align: 'center',
        }
      ],
    }
  ],
  WJ: [
    {
      key: '0',
      name: '期间',
      nameNew: '期间',
      check: true,
      width: 120,
      max: 180,
      min: 120,
      align: 'center'
    },
    {
      key: '1',
      name: '凭证号',
      nameNew: '凭证号',
      check: true,
      width: 80,
      max: 120,
      min: 80,
      align: 'center'
    },
    {
      key: '2',
      name: '客户编码',
      nameNew: '客户编码',
      check: true,
      width: 150,
      max: 180,
      min: 80,
      align: 'left'
    },
    {
      key: '3',
      name: '客户名称',
      nameNew: '客户名称',
      check: true,
      width: 150,
      max: 180,
      min: 80,
      align: 'left'
    },
    {
      key: '4',
      name: '摘要',
      nameNew: '摘要',
      check: true,
      width: 600,
      max: 600,
      min: 200,
      align: 'left'
    },
    {
      key: '5',
      name: '原币名称',
      nameNew: '原币名称',
      check: true,
      width: 100,
      max: 120,
      min: 80,
      align: 'center'
    },
    {
      key: '6',
      name: '借方',
      nameNew: '借方',
      check: true,
      width: 350,
      max: 350,
      min: 280,
      align: 'center',
      children: [
        {
          key: '6-1',
          name: '原币金额',
          nameNew: '原币金额',
          check: true,
          width: 180,
          max: 220,
          min: 180,
          align: 'center',
        },
        {
          key: '6-2',
          name: '本币金额',
          nameNew: '本币金额',
          check: true,
          width: 180,
          max: 220,
          min: 180,
          align: 'center',
        }
      ],
    },
    {
      key: '7',
      name: '贷方',
      nameNew: '贷方',
      check: true,
      width: 350,
      max: 350,
      min: 280,
      align: 'center',
      children: [
        {
          key: '7-1',
          name: '原币金额',
          nameNew: '原币金额',
          check: true,
          width: 180,
          max: 220,
          min: 180,
          align: 'center',
        },
        {
          key: '7-2',
          name: '本币金额',
          nameNew: '本币金额',
          check: true,
          width: 180,
          max: 220,
          min: 180,
          align: 'center',
        }
      ],
    },
    {
      key: '8',
      name: '方向',
      nameNew: '方向',
      check: true,
      width: 50,
      max: 100,
      min: 50,
      align: 'center',
    },
    {
      key: '9',
      name: '余额',
      nameNew: '余额',
      check: true,
      width: 350,
      max: 350,
      min: 280,
      align: 'center',
      children: [
        {
          key: '9-1',
          name: '原币金额',
          nameNew: '原币金额',
          check: true,
          width: 180,
          max: 220,
          min: 180,
          align: 'center',
        },
        {
          key: '9-2',
          name: '本币金额',
          nameNew: '本币金额',
          check: true,
          width: 180,
          max: 220,
          min: 180,
          align: 'center',
        }
      ],
    }
  ],
  SWJ: [
    {
      key: '0',
      name: '期间',
      nameNew: '期间',
      check: true,
      width: 120,
      max: 180,
      min: 120,
      align: 'center'
    },
    {
      key: '1',
      name: '凭证号',
      nameNew: '凭证号',
      check: true,
      width: 80,
      max: 120,
      min: 80,
      align: 'center'
    },
    {
      key: '2',
      name: '客户编码',
      nameNew: '客户编码',
      check: true,
      width: 150,
      max: 180,
      min: 80,
      align: 'left'
    },
    {
      key: '3',
      name: '客户名称',
      nameNew: '客户名称',
      check: true,
      width: 150,
      max: 180,
      min: 80,
      align: 'left'
    },
    {
      key: '4',
      name: '摘要',
      nameNew: '摘要',
      check: true,
      width: 600,
      max: 600,
      min: 200,
      align: 'left'
    },
    {
      key: '5',
      name: '计量单位',
      nameNew: '计量单位',
      check: true,
      width: 100,
      max: 120,
      min: 80,
      align: 'center'
    },
    {
      key: '6',
      name: '原币名称',
      nameNew: '原币名称',
      check: true,
      width: 100,
      max: 120,
      min: 100,
      align: 'center'
    },
    {
      key: '7',
      name: '借方',
      nameNew: '借方',
      check: true,
      width: 350,
      max: 350,
      min: 280,
      align: 'center',
      children: [
        {
          key: '7-1',
          name: '数量',
          nameNew: '数量',
          check: true,
          width: 180,
          max: 220,
          min: 180,
          align: 'center',
        },
        {
          key: '7-2',
          name: '原币金额',
          nameNew: '原币金额',
          check: true,
          width: 180,
          max: 220,
          min: 180,
          align: 'center',
        },
        {
          key: '7-3',
          name: '本币金额',
          nameNew: '本币金额',
          check: true,
          width: 180,
          max: 220,
          min: 180,
          align: 'center',
        }
      ],
    },
    {
      key: '8',
      name: '贷方',
      nameNew: '贷方',
      check: true,
      width: 350,
      max: 350,
      min: 280,
      align: 'center',
      children: [
        {
          key: '8-1',
          name: '数量',
          nameNew: '数量',
          check: true,
          width: 180,
          max: 220,
          min: 180,
          align: 'center',
        },
        {
          key: '8-2',
          name: '原币金额',
          nameNew: '原币金额',
          check: true,
          width: 180,
          max: 220,
          min: 180,
          align: 'center',
        },
        {
          key: '8-3',
          name: '本币金额',
          nameNew: '本币金额',
          check: true,
          width: 180,
          max: 220,
          min: 180,
          align: 'center',
        }
      ],
    },
    {
      key: '9',
      name: '方向',
      nameNew: '方向',
      check: true,
      width: 50,
      max: 100,
      min: 50,
      align: 'center',
    },
    {
      key: '10',
      name: '余额',
      nameNew: '余额',
      check: true,
      width: 350,
      max: 350,
      min: 280,
      align: 'center',
      children: [
        {
          key: '10-1',
          name: '数量',
          nameNew: '数量',
          check: true,
          width: 180,
          max: 220,
          min: 180,
          align: 'center',
        },
        {
          key: '10-2',
          name: '原币金额',
          nameNew: '原币金额',
          check: true,
          width: 180,
          max: 220,
          min: 180,
          align: 'center',
        },
        {
          key: '10-3',
          name: '本币金额',
          nameNew: '本币金额',
          check: true,
          width: 180,
          max: 220,
          min: 180,
          align: 'center',
        }
      ],
    }
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
  dynamicColumnAndDataModel[num] = list
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
        //便利子节点
        if(cObj.children){
          cObj.children.forEach((o)=>{
            o.title = thisName(o.key+'',lanmuList)
            o.width = thisWidth(o.key+'',lanmuList)
            o.align = thisAlign(o.key+'',lanmuList)
            o.ifShow = thisIsShow(o.key+'',lanmuList)
          })
        }
      }
    })
  })
  return columnList;
}


import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {findStockColumnList} from "/@/api/record/stock/stock-caigou";

const defaultRows = () => {
  return [
    {
      columnType: '1',
      field: 'bomSysId',
      label: 'BOM识别码',
      component: 'Input',
      componentProps: null,
      required: true,
      readonly: false,
      serial: 1,
      isShow: true
    }, {
      columnType: '1',
      field: 'bomId',
      label: '父件编码',
      component: 'Select',
      componentProps: 'stock',
      required: true,
      readonly: false,
      serial: 2,
      isShow: true
    },
    {
      columnType: '1',
      field: 'bomName',
      label: '父件名称',
      component: 'Input',
      componentProps: null,
      required: false,
      readonly: true,
      serial: 3,
      isShow: true
    },
    {
      columnType: '1',
      field: 'unitId',
      label: '计量单位',
      component: 'Select',
      componentProps: 'unit',
      required: true,
      readonly: false,
      serial: 4,
      isShow: true
    },
    {
      columnType: '1',
      field: 'bomVersion',
      label: 'BOM版本号',
      component: 'Input',
      componentProps: null,
      required: true,
      readonly: false,
      serial: 5,
      isShow: true
    },{
      columnType: '1',
      field: 'bomVerName',
      label: '版本名称',
      component: 'Input',
      componentProps: null,
      required: false,
      readonly: false,
      serial: 6,
      isShow: true
    },
    {
      columnType: '1',
      field: 'chengpingLv',
      label: '成品率%',
      component: 'InputNumber',
      componentProps: null,
      required: false,
      readonly: false,
      serial: 7,
      isShow: true
    },
    {
      columnType: '1',
      field: 'quantity',
      label: '数量',
      component: 'InputNumber',
      componentProps: null,
      required: true,
      readonly: false,
      serial: 8,
      isShow: true
    },
    {
      columnType: '1',
      field: 'cdepcode',
      label: '编制部门',
      component: 'Select',
      componentProps: 'dept',
      required: false,
      readonly: false,
      serial: 9,
      isShow: true
    },
    {
      columnType: '1',
      field: 'cpersoncode',
      label: '编制人',
      component: 'Select',
      componentProps: 'user',
      required: false,
      readonly: false,
      serial: 10,
      isShow: true
    },{
      columnType: '1',
      field: 'sunhaoLv',
      label: '损耗率%',
      component: 'InputNumber',
      componentProps: null,
      required: false,
      readonly: false,
      serial: 11,
      isShow: true
    },{
      columnType: '1',
      field: 'bomExplain',
      label: '版本说明',
      component: 'Input',
      componentProps: null,
      required: false,
      readonly: false,
      serial: 12,
      isShow: true
    }
  ]
}
export const lanMuData = {
  'menuName': '物料清单表头栏目',
  objects: '',
}
export async function GenerateDynamicColumn(schemaName) {
  let list = defaultRows()
  let res = await useRouteApi(findStockColumnList, {schemaName: schemaName})(lanMuData)
  if (res.length != 0) { // 新账套
    let arr1 = res.filter(it => it.columnType == '1')
    if (arr1.length > 0) { // 修改默认
      list = list.map(it1 => {
        let vrr = arr1.filter(it => it.field == it1.field)
        if (vrr.length > 0) {
          it1['label'] = vrr[0]['label']
          it1['required'] = vrr[0]['required'] =='true'?true:false
          it1['isShow'] = vrr[0]['isShow'] == 'true'?true:false
          it1['serial'] = vrr[0]['serial']
        }
        return it1
      })
    }
    let arr2 = res.filter(it => it.columnType == '2').map(it=>{
      it['required'] = it['required'] =='true'?true:false
      it['isShow'] = it['isShow'] == 'true'?true:false
      return it
    })
    if (arr2.length > 0) list.push(...arr2)
    // 排序
    list.sort((a, b)=>a.serial - b.serial)
  }
  return list
}

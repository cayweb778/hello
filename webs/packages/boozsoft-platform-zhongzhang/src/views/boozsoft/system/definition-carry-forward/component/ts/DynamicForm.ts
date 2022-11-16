// @ts-ignore
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
// @ts-ignore
import {findStockColumnList} from "/@/api/record/stock/stock-caigou";

const defaultRows = () => {
  return [
    {
      columnType: '1',
      field: 'ddate',
      label: '结转期间',
      component: 'Select',
      componentProps: null,
      required: true,
      readonly: true,
      serial: 1,
      isShow: true
    }, {
      columnType: '1',
      field: 'kmLevel',
      label: '损益科目级次',
      component: 'Select',
      componentProps: null,
      required: true,
      readonly: false,
      serial: 2,
      isShow: true
    },
    {
      columnType: '1',
      field: 'dataType',
      label: '收支类型',
      component: 'Select',
      componentProps: null,
      required: true,
      readonly: true,
      serial: 3,
      isShow: true
    },
    {
      columnType: '1',
      field: 'lirunKm',
      label: '本年利润科目',
      component: 'Select',
      componentProps: null,
      required: true,
      readonly: true,
      serial: 4,
      isShow: true
    },
    {
      columnType: '1',
      field: 'jz',
      label: '包含未记账',
      component: 'Select',
      componentProps: null,
      required: true,
      readonly: true,
      serial: 5,
      isShow: true
    }
  ]
}
export const lanMuData = {
  'menuName': '期间损益结转表头栏目',
  objects: '',
}
export async function GenerateDynamicColumn() {
  let list = defaultRows()
  // let res = await useRouteApi(findStockColumnList, {schemaName: schemaName})(lanMuData)
  // console.log(res)
  // if (res.length != 0) { // 新账套
  //   let arr1 = res.filter(it => it.columnType == '1')
  //   if (arr1.length > 0) { // 修改默认
  //     list = list.map(it1 => {
  //       let vrr = arr1.filter(it => it.field == it1.field)
  //       if (vrr.length > 0) {
  //         it1['label'] = vrr[0]['label']
  //         it1['required'] = vrr[0]['required'] =='true'?true:false
  //         it1['isShow'] = vrr[0]['isShow'] == 'true'?true:false
  //         it1['serial'] = vrr[0]['serial']
  //       }
  //       return it1
  //     })
  //   }
  //   let arr2 = res.filter(it => it.columnType == '2').map(it=>{
  //     it['required'] = it['required'] =='true'?true:false
  //     it['isShow'] = it['isShow'] == 'true'?true:false
  //     return it
  //   })
  //   if (arr2.length > 0) list.push(...arr2)
  //   // 排序
  //   list.sort((a, b)=>a.serial - b.serial)
  // }
  return list
}

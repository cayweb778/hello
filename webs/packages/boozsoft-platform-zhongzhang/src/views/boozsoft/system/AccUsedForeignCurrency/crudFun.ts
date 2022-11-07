import {message} from "ant-design-vue";
//import {exportExcel} from "/@/views/boozsoft/group/SysLogger/popup/excel";
const exportExcel=null
//import {useThisPrint} from "/@/views/boozsoft/group/SysLogger/popup/print";
const useThisPrint=null
//import {saveApi} from "/@/api/group/SysLogger";
const saveApi=null
import {saveTableAuditApi} from "/@/api/group/TableAudit";

export function openEditPageByAddFun(params,recordData) {
  recordData.value.openEditPage(true, 2)
  saveApi({
    type:'0',
    title:'进入了日志档案页面',
    creatby:'木子桉易洋',
    creattime:'2021-09-20',
    ip:'198.0.2.1',
    agent:'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/93.0.4577.63 Safari/537.36 Edg/93.0.961.38',
    chrome:'/goods/merge/buy',
    method:'GET',
    commitdata:'amount=%5B1%5D',
    exectime:'59',
    errorinfo:'',
    appflag:'pig',

  })

}
export function openEditPageByEditFun(params,recordData) {
  // if (!(params instanceof MouseEvent)) {
  //   recordData.value.openEditPage(true, {
  //     params
  //   })
  //   return
  // } else {
    if (recordData.value.tableSelectedRowKeys.length == 0) {
      message.info("请选择需要进行修改的行且只能是一行！")
      return
    }
    saveApi({
      type:'0',
      title:'进入了日志档案页面',
      creatby:'木子桉易洋',
      creattime:'2021-09-20',
      ip:'198.0.2.1',
      agent:'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/93.0.4577.63 Safari/537.36 Edg/93.0.961.38',
      chrome:'/goods/merge/buy',
      method:'GET',
      commitdata:'amount=%5B1%5D',
      exectime:'59',
      errorinfo:'',
      appflag:'pig',

    })
    recordData.value.openEditPage(true, {

      params: recordData.value.tableSelectedRowObjs[0]
    })
  // }

}

export async function deleteFun(params,recordData) {
  // if (!(params instanceof MouseEvent)) {
  //   await recordData.value.crud[3](params)
  // } else {
    if (recordData.value.tableSelectedRowKeys.length == 0) {
      message.info("请选择需要进行修改的行且只能是一行！")
      return
    }
    await recordData.value.crud[3]({id: recordData.value.tableSelectedRowObjs[0].id})
  // }
  const [registerTable, {
    reload,
    setTableData,
    getDataSource,
    setColumns,
    getColumns
  }] = recordData.value.useTableParams
  reload()
}

export function printFun(params,recordData) {
  useThisPrint()
}

export function excelFun(params,recordData) {
  exportExcel()
  return 'editBefore'
}

export function importFun(params,recordData) {
  return 'edit'
}


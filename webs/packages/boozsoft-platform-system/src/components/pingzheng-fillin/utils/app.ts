import {message} from "ant-design-vue";

export function verfiyData(tableData){
  const errorRows=[]
  const rows= JSON.parse(JSON.stringify(tableData.value.rows))

  // code: ""
  // name: ""

  let apiData=  tableData.value.rowDefines
    .map((item,i)=>{
      item.rowIndex=i+1
      return item
    })
    .filter(rowDefine=>{
      const item=rowDefine.rowData
      return!(item.zhaiYao.value==""
        &&item.kuaiJiKeMu.value.name==""
        &&item.jieMoney.value=="0.00"
        &&item.daiMoney.value=="0.00")
    });


  if(apiData.length ==0){
    message.error('0行分录，不符合规则')
    return
  }
  console.log(apiData)
  debugger

  if(apiData.length ==1){
    message.error('1行分录，不符合规则')
    return
  }

  apiData.filter(rowDefine=>{
    const item=rowDefine.rowData
    const errorRow=[]
    if(item.zhaiYao.value==''){
      errorRow.push('摘要为空')
    }
    if(item.kuaiJiKeMu.value.name==''){
      errorRow.push('会计为空')
    }
    if(item.jieMoney.value=='0.00' && item.daiMoney.value=='0.00'){
      errorRow.push('借贷都为空')
    }
    if(item.jieMoney.value==null || item.daiMoney.value==null){
      errorRow.push('金额格式错误')
    }
    if(errorRow.length!=0){
      errorRows.push({
        rowIndex:item.rowIndex,
        info:errorRow
      })
    }

    // 辅助项：客户不能为空
    // 数量不能为空


    // if(item.jieMoney.value==='0.00' && item.daiMoney.value==='0.00')
  })
  if(tableData.value.jieSumMoney!==tableData.value.daiSumMoney){
    errorRows.push({
      rowIndex:-1,
      info:'单据借贷不平'
    })
  }
  if(errorRows.length!=0){
    const errStr=errorRows.map(item=>'错误行'+item.rowIndex+'：'+item.info)
    console.error(errStr);
    return []
    // throw new Error(errStr)
  }
  return apiData
}

import {
  askTask,
  compareTime,
  findAccCloseListByYaer,
  findByFunctionModule, offsetToStr
} from "/@/api/task-api/tast-bus-api";
import {useMessage} from "/@/hooks/web/useMessage";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
const { createWarningModal} = useMessage();
export const checkMeetSpecifyConditions = async (pageVariable, yearMonths,flag,thisDbName) => {
  // 判断是否存在相关任务 结账 与 审核
  let msg = ''
  let pageMark = flag
  let yearMonthsObj = {year: '', months: []}
  yearMonthsObj = yearMonths
  // 判断当前年月 选择的月份是否已结账
  // 获取指定年度 的所有结账数据
  let closeList = await useRouteApi(findAccCloseListByYaer, {schemaName: thisDbName})({iyear: yearMonthsObj.year})
  let closeMonth = []
  closeList.forEach(item => {
    if (yearMonthsObj.months.indexOf(item.imonth) != -1 && item.gl == '1') {
      closeMonth.push(item.imonth)
    }
  })
  if (closeMonth.length > 0) {
    createWarningModal({
      title: '开始审批前检测',
      content: '凭证' + closeMonth.toString() + '月份已经结账，不能进行凭证审核操作！'
    })
    return false
  }
  let res = await useRouteApi(findByFunctionModule, {schemaName: thisDbName})({iyear: yearMonthsObj.year})
  let checkMenu = ['月末结账', '审核凭证', '凭证记账', '主管签字']
  for (let i = 0; i < res.length; i++) {
    let item = res[i]
    if (checkMenu.indexOf(item.functionModule) != -1 && item.state == '1') {
      // 校验时间
      if (item.functionModule == '月末结账' && item.time != '' && !compareTime(offsetToStr(item.time))
        && yearMonthsObj.months.indexOf(item.imonth) != -1) { // 月末处理单独
        msg = '提示：任务冲突！操作员【' +
          item.caozuoUnique +
          '】正在进行' + item.imonth + '月份' + item.functionModule + '处理!不能进行审核凭证操作，请销后再试，或联系财务主管清理该月末结账任务后再进行相关操作!'
        break;
      } else if (item.functionModule == '月末结账' && item.time != '' && compareTime(offsetToStr(item.time))) {
        msg = '提示：发现操作员【' +
          item.caozuoUnique +
          '】正在进行' + item.imonth + '月份' + item.functionModule + '操作已经超过30分钟!可能发生任务异常导致任务超时，是否强制清除月末结账任务？'
        if (await askTask({msg: msg, taskId: item.id, year: yearMonthsObj.year})) {
          msg = ''
          continue
        } else {
          return false
        }
      } else if (!pageMark && item.functionModule == '凭证记账' && item.time != '' && !compareTime(offsetToStr(item.time))) { // 超时
        msg = '提示：任务冲突！操作员【' +
          item.caozuoUnique +
          '】正在进行' + item.functionModule + '处理!不能继续进行凭证弃审操作，该操作不能多人同时并发进行，请销后再试，或联系财务主管清理该凭证记账任务后再进行操作!'
        break;
      } else if (!pageMark && item.functionModule == '凭证记账' && item.time != '' && compareTime(offsetToStr(item.time))) { // 超时
        msg = '提示：发现操作员【' +
          item.caozuoUnique +
          '】正在进行' + item.functionModule + '操作已经超过30分钟!可能发生任务异常导致任务超时，是否强制清除' +
          '凭证记账任务？'
        if (await askTask({msg: msg, taskId: item.id, year: yearMonthsObj.year})) {
          msg = ''
          continue
        } else {
          return false
        }
      } else if (pageMark && item.functionModule == '审核凭证' && item.time != '' && !compareTime(offsetToStr(item.time))) { // 超时
        msg = '提示：任务冲突！操作员【' +
          item.caozuoUnique +
          '】正在进行' + item.functionModule + '处理!不能继续进行凭证审核操作，该操作不能多人同时并发进行，请销后再试，或联系财务主管清理该审核凭证任务后再进行操作!'
        break;
      } else if (pageMark && item.functionModule == '审核凭证' && item.time != '' && compareTime(offsetToStr(item.time))) { // 超时
        msg = '提示：发现操作员【' +
          item.caozuoUnique +
          '】正在进行' + item.functionModule + '操作已经超过30分钟!可能发生任务异常导致任务超时，是否强制清除审核凭证任务？'
        if (await askTask({msg: msg, taskId: item.id, year: yearMonthsObj.year})) {
          msg = ''
          continue
        } else {
          return false
        }
      } else if (!pageMark && item.functionModule == '主管签字' && item.time != '' && !compareTime(offsetToStr(item.time))) { // 超时
        msg = '提示：任务冲突！操作员【' +
          item.caozuoUnique +
          '】正在进行' + item.functionModule + '处理!不能继续进行凭证弃审操作，该操作不能多人同时并发进行，请销后再试，或联系财务主管清理该主管签字任务后再进行操作!'
        break;
      } else if (!pageMark && item.functionModule == '主管签字' && item.time != '' && compareTime(offsetToStr(item.time))) { // 超时
        msg = '提示：发现操作员【' +
          item.caozuoUnique +
          '】正在进行' + item.functionModule + '操作已经超过30分钟!可能发生任务异常导致任务超时，是否强制清除主管签字任务？'
        if (await askTask({msg: msg, taskId: item.id, year: yearMonthsObj.year})) {
          msg = ''
          continue
        } else {
          return false
        }
      }
    }
  }
  if (msg != '') {
    createWarningModal({title: '开始审批前检测', content: msg})
    return false
  }
  return true;
}

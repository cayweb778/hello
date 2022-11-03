<template>
  <slot></slot>
</template>
<script setup>
import {inject, ref, nextTick, onMounted} from "vue";
import {apiDataToShowModel} from "/@/components/pingzheng-fillin/hooks/models/datas/model";
import {creatApiRow} from "/@/components/pingzheng-fillin/hooks/models/apiData";
import {
  defaultInoidApi,
  queryDefaultPingZhengDate
} from "/@/api/pingzheng/pingzheng";
import {usePingZhengResult} from "/@/components/pingzheng-fillin/hooks/okFun";

const emit = defineEmits(['ok', 'searchRow', 'checkAll', 'delRows', 'saveTempDanJu'])
const pingzhengType = inject('type')

const viewModel = inject('viewModel');
const tableData = viewModel.value.instances[0].params.pingzhengData
const pingzhengData = viewModel.value.instances[0].params.pingzhengData

async function openAddPage(params) {
  function toApiDataModel(apiData, params) {

    const a = apiDataToShowModel(apiData, params)
    return a

  }

  if (params.settings.typeLabel == '插入') {
    const tableData2 = toApiDataModel([creatApiRow()], {
      options: {
        originPingZheng: params.options.originPingZheng,
        optionInoId: params.options.optionInoId,
        optionDate: params.options.optionDate
      }, settings: {titleName: "记账凭证", typeLabel: '插入'}
    })
    tableData.value.reloadPingZheng(tableData2)
  } else if (params.settings.typeLabel == '冲销') {

    tableData.value.settings.onlyShow = false
    tableData.value.settings.typeLabel = params.settings.typeLabel
    if (params.options != null) {
      tableData.value.options.originPingZheng = params.options.originPingZheng
    }
    tableData.value.options.optionId2 = null
    tableData.value.options.optionPzId = null
    tableData.value.rowDefines.forEach(it => {
      if (it.rowData.zhaiYao.value != '') {
        it.target.zhaiYao.setValue(`[冲销]${it.rowData.zhaiYao.value}`)
      }
      it.rowData.jieMoney.value = (parseFloat(it.rowData.jieMoney.value) * -1).toFixed(2)
      it.rowData.daiMoney.value = (parseFloat(it.rowData.daiMoney.value) * -1).toFixed(2)
    })

    const aa = await queryDefaultPingZhengDate()
    const defaultInoid = viewModel.value.findDefaultInoId(aa.dbillDate)
    tableData.value.options.optionInoId =viewModel.value.toInoIdText(defaultInoid)

  } else if (params.settings.typeLabel == '复制') {
    const aa = await queryDefaultPingZhengDate()
    const defaultInoid = viewModel.value.findDefaultInoId(aa.dbillDate)
    tableData.value.options.optionInoId =viewModel.value.toInoIdText(defaultInoid)


    tableData.value.settings.onlyShow = false
    tableData.value.settings.typeLabel = params.settings.typeLabel
    if (params.options != null) {
      tableData.value.options.originPingZheng = params.options.originPingZheng
    }
    tableData.value.options.optionId2 = null
    tableData.value.options.optionPzId = null


  } else {
    tableData.value.settings.onlyShow = false
    tableData.value.settings.typeLabel = params.settings.typeLabel
    if (params.options != null) {
      tableData.value.options.originPingZheng = params.options.originPingZheng
    }
  }

}
// const closePingzhengModal=inject('closePingzhengModal')

function exitPingzhengModal() {
  if(pingzhengData.settings.enableModelSave){
    // closePingzhengModal()
  }
}
function openEditPage() {
  tableData.value.settings.onlyShow = false
  tableData.value.settings.typeLabel = '修改'
}




function backEdit() {
  function toApiDataModel(apiData, params) {
    tableData.value.pageStore.page = tableData.value.pageStore.inoIdIn.length + 1
    const a = apiDataToShowModel(apiData, params)
    return a

  }

  const apiData = [creatApiRow()]
  apiData[0].inoId = tableData.value.defaultInoid
  const tableData2 = toApiDataModel(apiData, {
    settings: {titleName: "记账凭证", typeLabel: '填制', onlyShow: false}
  })
  tableData2.options.optionDate = tableData.value.optionDate
  tableData.value.reloadPingZheng(tableData2,{type:'新增'})
}


function updateOkFun() {
  emit('ok')
  tableData.value.settings.onlyShow = true
  tableData.value.settings.typeLabel = '查看'
}


const pingZhengResultFun = usePingZhengResult({
  value:pingzhengData
})
const {
  okFun,
  saveTempDanJu,
  okAndShowFun,
  deletePingZhengByUniqueCode
} = pingZhengResultFun




function thisSaveFun(){
  saveFun()
}

function thisTempSave(){
  emit('saveTempDanJu')
}

</script>

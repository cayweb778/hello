<template>
  <div>

    <Print  v-if="showPrint" ></Print>
    <Excel  v-if="showExcel" ></Excel>
  </div>
</template>
<script setup>
import Print from './Print/index.vue'
import Excel from './Print/excel.vue'
import {tableStyle} from "/@/utils/boozsoft/print/abc-print";
import {useNewPrintLang} from "/@/utils/boozsoft/print/print";
import {useNewPrintStoreWidthOut} from "/@/store/modules/new-print";
import XLSX from "xlsx-js-style";
import {ref,computed} from 'vue'
const showPrint=computed(()=>useHelloPrintStoreWidthOut().getShowPrint)
const showExcel=computed(()=>useHelloPrintStoreWidthOut().getShowExcel)
import {
  defaultV,
  sheet_from_array_of_arrays,
  Workbook,
  writeExcel
} from "/@/utils/boozsoft/excel/excel2";
import {useHelloPrintStoreWidthOut} from "/@/store/modules/hello-print";



window.$wujie.bus.$emit("registerNcDataExport", {
  usePrint(){
    return {
      tableStyle,
      print(params,fun){
        const aaaa= useNewPrintLang(params,fun).getBase64()
        useHelloPrintStoreWidthOut().setShowPrint(true)
        useHelloPrintStoreWidthOut().setShowModals(true)
        useHelloPrintStoreWidthOut().setShowPreview(false)
        useNewPrintStoreWidthOut().setPrintBase64(aaaa)
        window.$wujie.bus.$emit('setCurrentPluginName', 'ncPrint')
      }
    }
  },

  useNewPrintLang,
  useExcel(){
    return {
      XLSX,
      defaultV,
      sheet_from_array_of_arrays,
      Workbook,
      writeExcel,
      excel(fun){
        window.$wujie.bus.$emit('setCurrentPluginName', 'ncPrint')
        useHelloPrintStoreWidthOut().setShowExcel(true)
        useHelloPrintStoreWidthOut().setShowExcelModal(true)
        useHelloPrintStoreWidthOut().setShowExcelPreview(false)
        fun(  )
      }
    }
  }
})
// window.$wujie.$on('startprint',function(){
//
// })
</script>

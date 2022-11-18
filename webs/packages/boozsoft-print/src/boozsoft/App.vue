<template>
  <div>

    <Print  v-if="showPrint" ></Print>
  </div>
</template>
<script setup>
import Print from './Print/index.vue'
import {tableStyle} from "/@/utils/boozsoft/print/abc-print";
import {useNewPrintLang} from "/@/utils/boozsoft/print/print";
import {useNewPrintStoreWidthOut} from "/@/store/modules/new-print";
import XLSX from "xlsx-js-style";
import {ref,computed} from 'vue'
const showPrint=computed(()=>useHelloPrintStoreWidthOut().getShowPrint)
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
    }
  }
})
// window.$wujie.$on('startprint',function(){
//
// })
</script>

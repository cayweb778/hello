<template>

    <button @click="testPrint">test print222</button>
    <iframe style="width:50vw;height:80vh" src="http://127.0.0.1:5001"></iframe>
</template>
<script setup>
import {savePdf, useNewPrint} from "/@/utils/boozsoft/print/print";
window.helloK='https://pdftron.s3.amazonaws.com/downloads/pl/demo-annotated.pdf'

function testPrint(){
  useNewPrint({data: ['l', 'px', 'a4', true]}, (doc) => {
      doc.autoTable({
        head: [['客户应收余额表','', '', '',  '', ''],
          ['单位：', '', '期间：', '', '', ''],
          ['结算客户编码', '结算客户名称', '期初余额', '应收金额', '收款金额', '期末余额']
        ],
        body: [],
        // startY: 60,
        margin: {
          left: 30,
          top: 20,
        },

        columnStyles: {
          0: {maxHeight: 10, cellWidth: 70, halign: 'left'},
          1: {cellWidth: 130, halign: 'left'},
          2: {cellWidth: 90, halign: 'right'},
          3: {cellWidth: 90, halign: 'right'},
          4: {cellWidth: 90, halign: 'right'},
          5: {cellWidth: 90, halign: 'right'},
        }
      })
    return doc
    })

}
</script>

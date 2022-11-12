<template>


  <div style="height:100vh">
    <!--    <div v-for=" obj in prints">-->
    <!--      {{obj.name}}-->
    <!--    </div>-->
    <div style="height:100vh;display:flex">
      <div style="border-radius:10px;background:rgb(219, 240, 255)">
        <Form :label-col="{ span: 8 }">
          <Item>
            <div>NC 打印控件</div>
          </Item>

          <Item>
            <div>当前环境: 图形化 可选:[函数，图形化,演示]</div>
          </Item>
              <Divider style="border-color: #7cb305" dashed />
          <Item>
            <div>
              <Item>
                <div>纸张类型: a4</div>
              </Item>
            </div>
          </Item>
          <Item label="选择打印机">
            <div> <Select :options="prints" :value="'Mi All in One Inkjet'"></Select></div>
          </Item>
          <Item label="纸张类型">
            <div> <Select :value="'A4'"></Select></div>
          </Item>
          <Item label="边距">
            <div> <Select :value="'0'"></Select></div>
          </Item>
          <Item label="页眉">
            <div> <Select :value="'显示'"></Select></div>
          </Item>
          <Item label="页尾">
            <div> <Select :value="'显示'"></Select></div>
          </Item>
            <Item>
              <Button @click="testPrint">开始打印</Button>
            </Item>
        </Form>
      </div>
      <div style="height:100vh;width:64vw;background:white">
          <PdfViewer v-model="dataaaa"/>
      </div>
    </div>
  </div>

</template>
<script setup>
import {savePdf, useNewPrint, useNewPrintLang} from "/@/utils/boozsoft/print/print";
import {Button, Select, Form,Divider} from "ant-design-vue";
import PdfViewer from './SubApps/PdfViewer/index.vue'
const Item = Form.Item

window.helloK = 'https://pdftron.s3.amazonaws.com/downloads/pl/demo-annotated.pdf'
const dataaaa=ref()
function testPrint() {
  dataaaa.value=useNewPrintLang({data: ['l', 'px', 'a4', true]}, (doc) => {
    doc.setFont('fuhuiR')
    let bbb=[]
    for(let i=0;i<200;i++){
      bbb.push(  ['结算客户编码', '结算客户名称', '期初余额', '应收金额', '收款金额', '期末余额'])
    }
    doc.autoTable({
      head: [['客户应收余额表', '', '', '', '', ''],
        ['单位：', '', '期间：', '', '', ''],
        ['结算客户编码', '结算客户名称', '期初余额', '应收金额', '收款金额', '期末余额']
      ],
      body:bbb,
      styles:tableStyle(),
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
  }).getBase64()

}

import {ref, onMounted} from 'vue'
import {tableStyle} from "/@/utils/boozsoft/print/abc-print";

const prints = ref()
onMounted(async () => {
  function strToJson(str) {
    var json = (new Function("return " + str.replaceAll("Printer", '')))();
    return json;
  }

  prints.value = strToJson(await window.top.__TAURI_INVOKE__("get_printers_all")).map(it => {
    return {
      label: it.name,
      option: it.name,
      value: it.name
    }
  })
})
</script>

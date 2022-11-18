<template>


  <div style="height:100vh">
    <!--    <div v-for=" obj in prints">-->
    <!--      {{obj.name}}-->
    <!--    </div>-->
    <div style="height:100vh;display:flex">
      <div style="border-radius:10px;background:rgb(219, 240, 255)">
        <Form :label-col="{ span: 8 }">
          <Item>
            <div>NC 表格预览控件</div>
          </Item>

          <Item>
            <div>当前环境222: 图形化 可选:[函数，图形化,演示]</div>
          </Item>
          <Item>
            <Button @click="testPrint">保存过的表格(C:/Users/boozsoft/Downloads)</Button>
            <Row><Button @click="closePrint">客户应收余额表_中途启用财务账套.xlsx</Button></Row>
            <Row><Button @click="closePrint">客户应收余额表_中途启用财务账套.xlsx（1）</Button></Row>
            <Row><Button @click="closePrint">客户应收余额表_中途启用财务账套.xlsx（2）</Button></Row>
            <Row><Button @click="closePrint">客户应收余额表_中途启用财务账套.xlsx（3）</Button></Row>
          </Item>
          <Divider style="border-color: #7cb305" dashed/>
          <Item>
            <div>
              <Item>
                <div>类型: xslx</div>
              </Item>
            </div>
          </Item>

          <Item label="类型">
            <div><Select :value="'xlsx'"></Select></div>
          </Item>

          <Item label="页眉">
            <div><Select :value="'显示'"></Select></div>
          </Item>
          <Item label="页尾">
            <div><Select :value="'显示'"></Select></div>
          </Item>
          <Item>
            <Button @click="testPrint">保存</Button>
            <Button @click="closePrint">下载</Button>
            <Button @click="closePrint">关闭</Button>
          </Item>
        </Form>
      </div>
      <div style="height:100vh;width:100vw;background:white">
        <ExcelPdfViewer :value="printBase64"/>
      </div>
    </div>
  </div>

</template>
<script setup>
import {savePdf, useNewPrint, useNewPrintLang} from "/@/utils/boozsoft/print/print";
import {Button, Select, Form, Divider,Row} from "ant-design-vue";
import ExcelPdfViewer from './SubApps/ExcelViewer/index.vue'

const Item = Form.Item
import {ref, computed} from 'vue'

window.helloK = 'https://pdftron.s3.amazonaws.com/downloads/pl/demo-annotated.pdf'
const dataaaa = ref()
const printBase64=computed(()=>useNewPrintStoreWidthOut().getPrintBase64)
function testPrint() {
  dataaaa.value = useNewPrintLang({data: ['l', 'px', 'a4', true]}, (doc) => {
    doc.setFont('fuhuiR')
    let bbb = []
    for (let i = 0; i < 200; i++) {
      bbb.push(['结算客户编码', '结算客户名称', '期初余额', '应收金额', '收款金额', '期末余额'])
    }
    doc.autoTable({
      head: [['客户应收余额表', '', '', '', '', ''],
        ['单位：', '', '期间：', '', '', ''],
        ['结算客户编码', '结算客户名称', '期初余额', '应收金额', '收款金额', '期末余额']
      ],
      body: bbb,
      styles: tableStyle(),
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

import {tableStyle} from "/@/utils/boozsoft/print/abc-print";
import {useNewPrintStore, useNewPrintStoreWidthOut} from "/@/store/modules/new-print";

function closePrint() {
  window.$wujie.bus.$emit("closePluginShadow")
}

const prints = computed(() => useNewPrintStoreWidthOut().getPrinters)


</script>

<template>
  <div
      style="position:fixed;height:30px;width:100%;background:#8da6c7;font-weight:900;color:white;bottom:0;z-index:100;"
      class="thisBottom">
    <Row style="font-size:17px;">
      <Col :span="8">
        <div style="width:100%;font-size: 14px;padding-top:3px;padding-left:5px;color:#c2ccdc;">
          &emsp;&emsp;
          <Badge @click="goPrint()" :count="4" :number-style="{ backgroundColor: '#52c41a' }">
           <span class="hoverSvg">
              <svg width="24px" height="24px" viewBox="0 0 24 24" version="1.1"
                   xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">
              <title>icon/announcement</title>
              <desc>Created with Sketch.</desc>
              <g id="icon/announcement" stroke="none" stroke-width="1" fill="none"
                 fill-rule="evenodd" stroke-linecap="round" stroke-linejoin="round">
                <path
                    d="M4.5,7.5 C3.396,7.5 2.5,8.396 2.5,9.5 C2.5,10.604 3.396,11.5 4.5,11.5 L4.5,7.5 Z M10.5,12.5 C15.5,12.5 20.5,15.5 20.5,15.5 L20.5,3.5 C20.5,3.5 15.5,6.5 10.5,6.5 L10.5,12.5 Z M10.5,6.5 L5.5,6.5 C4.948,6.5 4.5,6.948 4.5,7.5 L4.5,11.5 C4.5,12.052 4.948,12.5 5.5,12.5 L10.5,12.5 L10.5,6.5 Z M10.5,18.5 L8.5,18.5 L6.5,12.5 L10.5,12.5 L10.5,18.5 Z"
                    stroke-width="2" id="lineart" stroke="white"></path>
              </g>
            </svg>
           </span>

          </Badge>&emsp;&emsp;
          <!--            <MessagePop @throwData="modalData" @register="registerModalPopPage"/>-->
          <Badge :count="4" :number-style="{ backgroundColor: '#52c41a' }">
<span class="hoverSvg" @click="goMessage()">
           <svg width="24px" height="24px" viewBox="0 0 24 24" version="1.1"
                xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">
    <title>icon/text-message</title>
    <desc>Created with Sketch.</desc>
    <g id="icon/text-message" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd"
       stroke-linecap="round" stroke-linejoin="round">
        <path
            d="M17.5,10.5 L17.5,4.5 C17.5,3.948 17.052,3.5 16.5,3.5 L3.5,3.5 C2.948,3.5 2.5,3.948 2.5,4.5 L2.5,14.5 C2.5,15.052 2.948,15.5 3.5,15.5 L4.5,15.5 L4.5,17.5 L7.5,15.5 L10.5,15.5 M20.5,11.5 L20.5,17.5 C20.5,18.052 20.052,18.5 19.5,18.5 L18.5,18.5 L18.5,20.5 L15.5,18.5 L11.5,18.5 C10.948,18.5 10.5,18.052 10.5,17.5 L10.5,11.5 C10.5,10.948 10.948,10.5 11.5,10.5 L19.5,10.5 C20.052,10.5 20.5,10.948 20.5,11.5 Z"
            id="lineart" stroke-width="2" stroke="white"></path>
    </g>
</svg>

</span>
            <!--            <MessageOutlined style="font-size: 1em"/>-->
          </Badge>
          <Button @click="goPrint">打印测试</Button>
          <Button @click="goExcel">excel测试</Button>
          <Button @click="goMessage">弹出窗测试</Button>
        </div>
      </Col>
      <Col :span="8">
        <div style="position: absolute;left: calc((100% - 184px) / 2);">
          <div style="margin:0 auto;padding-top:2px">
            <span style="font-size: 14px;">业务日期：</span>
            <!--    @blur="changeOpenStatus(false)"      -->
            <DatePicker :value="loginDate" @change="dateChange" :showToday="false" :allowClear="false"
                        :inputReadOnly="true"
                        style="padding:2px 4px 0;width:114px;text-align-last: center;"
                        value-format="YYYY-MM-DD"
                        format="YYYY-MM-DD">
              <template #renderExtraFooter>
                <div style="display: inline-flex;justify-content: space-between;width: 100%;">
                  <Button type="primary" size="small" @click="btnCheck('1')">今天</Button>
                  <Button type="primary" size="small" @click="btnCheck('2')">昨天</Button>
                  <Button type="primary" size="small" @click="btnCheck('3')">上月</Button>
                  <Button type="primary" size="small" @click="btnCheck('4')">本月</Button>
                  <Button type="primary" size="small" @click="btnCheck('5')">下月</Button>
                </div>
              </template>
            </DatePicker>
          </div>
        </div>
      </Col>
      <Col :span="8" style="min-width:100px">
        <!--        <div @click="downFile">下载</div>-->
        <!--        <div style="min-width:57px;padding-top:1px;text-align:center;color:white;font-size:14px;background:black">财务版</div>-->


        <!--        <div style="min-width:38px;padding-top:1px"><SoundOutlined/></div>-->
        <!--        <div style="min-width:38px;padding-top:1px"><SearchOutlined/></div>-->
        <!--        <div  style="min-width:38px;padding-top:1px"  @click="goTask()">-->
        <!--          <Badge :count="2" :number-style="{ backgroundColor: '#52c41a' }">-->


        <!--&lt;!&ndash;            <HistoryOutlined style="font-size: 1em" />&ndash;&gt;-->
        <!--          </Badge>-->
        <!--        </div>-->


        <!--      <div style="min-width:48px;padding-top:4px">
                  <span class="hoverSvg" style="color:red">
                            <svg width="24px" height="24px" viewBox="0 0 24 24" version="1.1"
                                 xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">
                              &lt;!&ndash; Generator: Sketch 52.5 (67469) - http://www.bohemiancoding.com/sketch &ndash;&gt;
                              <title>icon/help</title>
                              <desc>Created with Sketch.</desc>
                              <g id="icon/help" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd"
                                 stroke-linecap="round" stroke-linejoin="round">
                                <path
                                  d="M11.5,14.5 C11.224,14.5 11,14.724 11,15 C11,15.276 11.224,15.5 11.5,15.5 C11.776,15.5 12,15.276 12,15 C12,14.724 11.776,14.5 11.5,14.5 Z M11.5,12.7 L11.5,12.181 C11.5,11.826 11.735,11.518 12.075,11.416 C12.899,11.169 13.5,10.405 13.5,9.5 C13.5,8.419 12.581,7.5 11.5,7.5 C10.396,7.5 9.5,8.396 9.5,9.5 M20.5,11.5 C20.5,16.471 16.471,20.5 11.5,20.5 C6.529,20.5 2.5,16.471 2.5,11.5 C2.5,6.529 6.529,2.5 11.5,2.5 C16.471,2.5 20.5,6.529 20.5,11.5 Z"
                                  id="lineart" stroke="#1a7575" stroke-width="2"></path>
                              </g>
                            </svg>
                  </span>
              </div>-->
        <!--        <CustomizeInfo style="min-width:90px;" />-->
        <Row @click="outSystemBefore" style="float:right;margin-right:10px">
          <Col style="padding-top:2px;margin-right:6px">
            <LogoutOutlined style="font-size: 14px;cursor:pointer"/>
          </Col>
          <Col style="cursor:pointer;padding-top:1px"><span style="font-size: 12px;padding-right:0px;">退出系统</span>
          </Col>
        </Row>


      </Col>
    </Row>
  </div>
</template>
<script setup>
import {computed, ref, watch} from "vue"
import {Row, Col} from "ant-design-vue"
import {useCounterStoreWidthOut} from "/@/store/modules/counter"
import {LogoutOutlined} from '@ant-design/icons-vue'
import {useMessage} from "../../../hooks/web/useMessage"
import {Badge, DatePicker, Button} from 'ant-design-vue'

const {createConfirm} = useMessage();
let mark = false;
const loginDate = ref()
const loginDateCompoted = computed(() => useGlobalStoreWidthOut().getLoginDate)
watch(() => loginDateCompoted.value, (e) => {
  loginDate.value = e
}, {immediate: true})
const dateChange = async (v) => {
  useGlobalStoreWidthOut().setLoginDate(v)
  window.localStorage.setItem('loginDate', v)

}

const showOpen = ref(false)
const windowWidth = document.documentElement.clientWidth - 170;
const windowHeight = (window.innerHeight - (325))

const showFooter = computed(() => useCounterStoreWidthOut().showFooterShow)
// import { appDataDir } from '@tauri-apps/api/path';
// import { window as window2 } from '@tauri-apps/api';
// import { readDir, BaseDirectory } from '@tauri-apps/api/fs';
import router from "../../../router";
import {useGlobalStoreWidthOut} from "../../../store/modules/global";

const changeOpenStatus = () => {
  showOpen.value = !showOpen.value
}

async function goMessage() {
  let {useNcModalData,To} = useNcBusStoreWidthOut().useModals()
  const data = await useNcModalData(To.SYSTEM_MESSAGE, {})
  console.log(data)
}

import MyWorker from './abbbb?worker'


async function goPrint() {
  // const aaaa=new MyWorker()
  // aaaa.postMessage({id})
  // return
  // useNcBusStoreWidthOut().usePrint()
  const {print, tableStyle} = useNcBusStoreWidthOut().usePrint()
  const data = print({data: ['l', 'px', 'a4', true]}, (doc) => {
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
  })
  console.log(data)
}

async function goExcel() {
  let {excel} = useNcBusStoreWidthOut().useExcel()
  excel(() => {
  })
  // const excelData = () => {
  //   // console.log("导出成功！")
  //
  //   const wb:any = Workbook()
  //
  //
  //   const bookType = 'xlsx'
  //   // 类型默认为xlsx
  //   writeExcel(wb, bookType, '客户应收余额表_'+pageParameter.companyName)
  // }

}

import {appLocalDataDir} from '@tauri-apps/api/path';
if(window.__TAURI_POST_MESSAGE__==null){
  // router.push('/login')
}
const outSystemBefore = async () => {
  // console.log(await appLocalDataDir())
  window.localStorage.clear()
  router.push("/login");
  // const Document=await readDir('', { dir: BaseDirectory. Document});
  // const Download=await readDir('', { dir: BaseDirectory. Download, });
  // const Executable=await readDir('', { dir: BaseDirectory. Executable, });
  // const Runtime=await readDir('', { dir: BaseDirectory. Runtime, });

  // const Template=await readDir('', { dir: BaseDirectory. Template, });
  // const Video=await readDir('', { dir: BaseDirectory. Video, });
  // console.log(3)
  // const Resource=await readDir('', { dir: BaseDirectory. Resource, });
  // const App=await readDir('', { dir: BaseDirectory. App, });
  // const Log=await readDir('', { dir: BaseDirectory. Log, });
  // debugger
  // const Temp=await readDir('', { dir: BaseDirectory., });
  // console.log(Temp)
  // debugger
  // const abc={
  //   Audio,
  //   Cache,
  //   Config,
  //   Data,
  //   LocalData,
  //   Desktop,
  //   Document,
  //   Download,
  //   Executable,
  //   Font,
  //   Home,
  //   Picture,
  //   Public,
  //   Runtime,
  //   Template,
  //   Video,
  //   Resource,
  //   App,
  //   Log,
  //   Temp
  // }
  // console.log(abc)
  // window2.getCurrent().close()


  // await removeDir('财税达', { dir: BaseDirectory.Config, });
  // await removeDir('财税达', { dir: BaseDirectory.Data, });
  // await removeDir('财税达', { dir: BaseDirectory.AppCache, });
  // await removeDir('财税达', { dir: BaseDirectory.AppLocalData, });
  // await removeDir('财税达', { dir: BaseDirectory.Cache, });

  // if (mark) return false
  // mark = true
  // createConfirm({
  //   iconType: 'info',
  //   title: '退出登录',
  //   content: '您确定要退出登录吗?退出后将前往登录页面！',
  //   onOk: async () => {
  //     useCounterStoreWidthOut().setShowFooter(false)
  //     router.push("/login")
  //   }
  // });
}

import {save} from '@tauri-apps/api/dialog';
import {writeBinaryFile,} from '@tauri-apps/api/fs';
import {path, dialog} from '@tauri-apps/api';
import {open} from '@tauri-apps/api/shell';
import {useNcBusStoreWidthOut} from "../../../store/modules/nc-bus";

window.ncSaveFile = async function (e1, filename) {
  // const reader = new FileReader();
  // let selPath = await dialog.save({
  //   title:'财税达-'+filename,
  //       filters: [{
  //     name: 'Image222',
  //     extensions: ['png', 'jpeg']
  //   }],
  //   defaultPath: await path.downloadDir(),
  // });
  // reader.readAsArrayBuffer(e1);
  // debugger
  let fileU8A = new Uint8Array(e1);

  let downLoadDirStr = await path.downloadDir()

  writeBinaryFile({contents: fileU8A, path: `${downLoadDirStr}${filename}`});

  await open(`${downLoadDirStr}${filename}`)
  // reader.onload = function (e) {
  //   let fileU8A = new Uint8Array(e.target.result);
  //
  //
  //   datas.tip = '图片保存成功';
  // };
}
// async function downFile(){
//   writeBinaryFile({ contents: fileU8A, path: `${selPath}${file.data.name}` });
//   const filePath = await save({
//     filters: [{
//       name: 'Image',
//       extensions: ['png', 'jpeg']
//     }]
//   });
// }
</script>
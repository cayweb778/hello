<template>
  <BasicModal
    width="900px"
    v-bind="$attrs"
    title="部门导入"
    @ok="handleOk()"
    @register="register"
    okText="开始导入"
    :canFullscreen="false"
  >
    <template #title>
      <div style="display: flex;" class="vben-basic-title">
        <span style="line-height:40px;font-size: 28px;">
          <CloudUploadOutlined style="font-size: 34px;font-weight: bold"/>&nbsp;&nbsp;数据导入
        </span>
      </div>
    </template>
    <div class="import-centent-div" >
      <div class="import-info-div">
        <div class="import-div-top">
          <div>
          </div>
          <div>
            <span style="font-size: 16px;">导入内容：</span><span style="font-weight: bold;font-size: 16px;">部门档案导入</span><br/><br/>
            <ul style="list-style-type: disc;color:#0096c7;margin-left: 20px;font-size: 24px;">
              <li><span style="font-size: 16px;color: #000000;">全新添加导入</span></li>
            </ul>
          </div>
          <div>
            <Tooltip placement="top" >
              <Button size="small" style="color: #3eadbe">查看帮助</Button>
              <EllipsisOutlined style="cursor: pointer;margin-left: 10%;color: #3eadbe"/>
            </Tooltip>
            <br>
            <br>
            <Tooltip placement="top" >
              <DownloadOutlined style="font-size: 30px;"/>
              <a @click="exportExcel()">&emsp;模板下载</a>
            </Tooltip>
          </div>
        </div>
      </div>
      <div class="import-div-bottom">
        <Tabs v-model:activeKey="excelValue">
          <TabPane key="1" tab="全新添加导入">
          </TabPane>
          <TabPane key="2" :disabled="true" tab="字段覆盖导入">
          </TabPane>
        </Tabs>
        <br/>
        <ImpExcel v-if="isActiveImpExcel" @success="loadDataSuccess">
          <a-button class="m-3"> 导入Excel </a-button>
        </ImpExcel>
      </div>
    </div>

    <template #footer>
      <div>
        <Button @click="closeModal()">放弃</Button>
        <Button @click="handleOk()" :disabled="saveClick" type="primary">开始导入</Button>
      </div>
    </template>
<!--    <div class="nc-open-content">
      <div class="open-content-up" style="height: 240px;margin-left: 5%;width:90%;">

        <div style="position: relative;background: #ffffff;border: 1px solid #999999;padding: 10px;margin-top: 20px;">
          <div style="position: absolute;background: #ffffff; top: -15px;left: 20px;padding: 0 10px;">导入说明</div>
          <p>1.导入文件格式必须为xls或xlsx，且数据表内容必须放置在第一个页签sheet1中；</p>
          <p>2.部门编码和名称不允许与当前库重复；</p>
        </div>

        <br/>
        <label style="width: 150px;">
          <a @click="exportExcel()"><DownloadOutlined/>下载导入模板</a>
        </label>

        <div style="margin-left: 40px;margin-top: 30px;">
          <ImpExcel v-if="isActiveImpExcel" @success="loadDataSuccess">
            <a-button class="m-3"> 导入Excel </a-button>
          </ImpExcel>
        </div>
        <br>

      </div>
    </div>-->

  </BasicModal>
</template>

<script setup="props, {content}" lang="ts">
import {nextTick, ref, unref} from 'vue'
import { BasicModal, useModalInner } from '/@/components/Modal'
import { ImpExcel } from '/@/components/Excel'
import {
  Upload as AUpload,
  Spin as ASpin,
  Select,
  Input as AInput,
  Modal as AModal, Badge, Button, Tabs, Radio,Checkbox,Tooltip
} from 'ant-design-vue';
import { DownloadOutlined,CloudUploadOutlined,CaretDownOutlined,EllipsisOutlined } from '@ant-design/icons-vue';
const AInputSearch=AInput.Search
const SelectOption=Select.Option
const RadioGroup = Radio.Group
const TabPane = Tabs.TabPane
import {useMessage} from "/@/hooks/web/useMessage";
import {aoaToSheetXlsx} from "/@/views/boozsoft/xian_jin_liu_liang/yin_hang_dui_zhang/yin_hang_dui_zhang_dan/excel/components/importexcel";
import {getDeptList} from "/@/api/record/system/dept";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
const {
  createErrorModal
} = useMessage()

const saveClick:any = ref(false)

const excelValue:any = ref(1)

const emit=defineEmits(['register','save'])
const isActiveImpExcel=ref(false)
const list:any = ref([])
function loadDataSuccess(excelDataList) {
  // console.log(excelDataList);
  // console.log(excelDataList[0].results);
  list.value = []
  const items = excelDataList[0].results
  if (items.length>0) {
    for (let i = 0; i < items.length; i++) {
      const item = items[i]
      const item1: any = {}
      item1.deptCode = item['部门编码']
      item1.deptName = item['部门名称']
      item1.parentId = item['上级部门']
      // item1.uniqueCodeUser = item['部门负责人']
      item1.flag = '1'
      list.value.push(item1)
    }
    for (let i=0; i<list.value.length; i++){
      const item1 = list.value[i];
      //判断导入编码是否为空
      const deptCode = item1.deptCode
      if (deptCode==null || deptCode==''){
        createErrorModal({
          iconType: 'warning',
          title: '提示',
          content: '第'+(i+1)+'行部门编码为空,不能进行信息导入'
        })
        list.value = []
        return false
      }
      //判断导入名称是否为空
      const deptName = item1.deptName
      if (deptName==null || deptName==''){
        createErrorModal({
          iconType: 'warning',
          title: '提示',
          content: '第'+(i+1)+'行部门名称为空,不能进行信息导入'
        })
        list.value = []
        return false
      }
      for (let j=0; j<list.value.length; j++) {
        const item2 = list.value[j];
        if (i!=j){
          if (item1.deptCode==item2.deptCode){
            createErrorModal({
              iconType: 'warning',
              title: '提示',
              content: '第'+i+'行部门编码信息与第'+j+'行的信息重复，请修改后重新导入！'
            })
            list.value = []
            return false
          }
          if (item1.deptName==item2.deptName){
            createErrorModal({
              iconType: 'warning',
              title: '提示',
              content: '第'+i+'行部门名称信息与第'+j+'行的信息重复，请修改后重新导入！'
            })
            list.value = []
            return false
          }
        }
      }
    }
  } else {
    createErrorModal({
      iconType: 'warning',
      title: '提示',
      content: '未发现导入数据，请检查数据是否在sheet1页签中'
    })
  }
}
//导入时判断
let msg=''
function checkExcel(){
  msg=''
  if (list.value.length>0) {
    for (let i = 0; i < list.value.length; i++) {
      const item = list.value[i];
      //判断导入编码是否为空
      /*const deptCode = item['deptCode']
      if (deptCode==null || deptCode==''){
        msg="第"+(i+1)+'行部门编码为空,不能进行信息导入'
        return false
      }
      //判断导入名称是否为空
      const deptName = item['deptName']
      if (deptName==null || deptName==''){
        msg="第"+(i+1)+'行部门名称为空,不能进行信息导入'
        return false
      }*/
      for (let j = 0; j < deptList.value.length; j++) {
        const dept = deptList.value[j];
        if (dept.deptCode == item.deptCode) {
          msg = '第' + (i + 1) + '行部门编码重复,不能进行部门信息导入'
          return false
        }
        if (dept.deptName == item.deptName) {
          msg = '第' + (i + 1) + '行部门名称重复,不能进行部门信息导入'
          return false
        }
      }
    }
    return true
  } else {
    msg = '请选择您需要导入的文件!'
    return false
  }
}

const deptList:any = ref([])
const dynamicTenantId = ref()
const [register, { closeModal }] = useModalInner((data) => {
  saveClick.value=false
  dynamicTenantId.value = data.dynamicTenantId
  useRouteApi(getDeptList,{schemaName: dynamicTenantId})({}).then(res=>{
    deptList.value = res.items
  })

  isActiveImpExcel.value=false
  nextTick(()=>{
    isActiveImpExcel.value=true
  })
})
async function handleOk() {
  saveClick.value=true
  // formItems.value.excelValue = excelValue.value
  // formItems.value.object = list.value
  // formItems.value.cateCode = cateCode.value
  checkExcel()
  console.log(msg)
  if (msg=='') {
    emit('save', unref(list))
    closeModal()
    saveClick.value=false
    return true
  } else {
    createErrorModal({
      iconType: 'warning',
      title: '导入失败',
      content: msg
    })
    saveClick.value=false
    return false
  }
}

//下载导入模板
function exportExcel() {
  const arrHeader = ['部门编码','部门名称','上级部门'];
  console.log(arrHeader)
  // 保证data顺序与header一致
  aoaToSheetXlsx({
    data: [],
    header: arrHeader,
    filename: '部门导入模板.xlsx',
  });
}

</script>
<style lang="less" scoped>
.import-centent-div{
  .import-info-div {
    width: 90%;
    margin-left: 2%;
    height: 180px;
    border-radius: 4px;

    .import-div-top {
      width: 100%;
      height: 70%;
      display: inline-flex;
      justify-content: space-between;

      > div:nth-of-type(1) {
        width: 25%;
        height: auto;
        background-image: url(/nc/download2.png);
        background-size: 80% 80%;
        background-repeat: no-repeat;
        background-position: 12px;
      }

      > div:nth-of-type(2) {
        width: 55%;
        height: auto;
        padding: 18px 2%;

        > span {
          color: black;
          font-size: 20px;
        }

        > span:nth-of-type(1) {
          font-size: 20px;
        }
      }

      > div:nth-of-type(3) {
        width: 20%;
        height: auto;
        padding: 3.5% 0;
      }

    }
  }
  .import-div-bottom {
    margin-left: 2%;
    width: 96%;
    height: 30%;
  }
  .import-download-div{
    width: 100%;height: 60px;display: inline-flex; justify-content: center; line-height: 50px;
    .download-div {
      width: 50px;
      height: 50px;
      display: block;
      background-color: #6a6a6a;
      border-radius: 50%;
      padding: 2px 10px;
      font-size: 30px;
      color: white;
      cursor: pointer;
    }

    .download-div:hover {
      color: #0096c7;
      background-color: #b4b4b4;
    }
  }
}
:deep(.ant-select-selector) {
  border: none !important;
  border-bottom: 1px solid #bdb9b9 !important;
}
</style>

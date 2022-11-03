<template>
  <BasicModal
    width="800px"
    :height="350"
    v-bind="$attrs"
    :canFullscreen="false"
    @ok="handleOk()"
    @register="register"
  >
    <Loading :loading="compState.loading" :absolute="compState.absolute" :tip="compState.tip" />
    <template #title>
      <div style="height: 30px;width: 100%;background-color: #5f375c;color: white;line-height: 30px;text-align: left;">
        <AppstoreOutlined  style="margin: 0 2px;font-size: 14px;"/>
        <span style="font-size: 14px">数据导入</span>
      </div>
    </template>
    <div style="width: 100%;display: table;">
      <div  style="text-align: right;width: 44%;float: left;">
        <CloudUploadOutlined style="margin: 0 2px;font-size: 40px;color: #0096c7;"/>
      </div>
      <div style="text-align: left;width: 54%;float: right;font-size: 24px;font-weight: bold;color: #0096c7;">
        计量单位 
      </div>
    </div>
    <div class="import-centent-div" >
      <div class="import-info-div">
        <div class="import-div-top">
          <div style="width: 0%;">
          </div>
          <div>
            <span>导入内容：</span><span style="font-weight: bold;font-size: 20px;">计量单位</span><br/><br/>
            <span>模板样式：</span>
            <Select
              v-model:value="codeTemplateId"
              placeholder=""
              style="width: 50%;margin-right: 2%"
              allow-clear>
              <SelectOption v-for="tem in templateList" :key="tem.id" :value="tem.id">
                {{ tem.tname }}
              </SelectOption>
              <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
            </Select>
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
        <Tabs v-model:activeKey="typeKey">
          <TabPane key="1" tab="单计量导入">
            <ImpExcel v-if="typeKey == '1'" @success="loadDataSuccess">
              <a-button class="m-3"> 导入Excel </a-button>
            </ImpExcel>
          </TabPane>
          <TabPane key="2" tab="多计量导入">
            <ImpExcel v-if="typeKey == '2'" @success="loadDataSuccess">
              <a-button class="m-3"> 导入Excel </a-button>
            </ImpExcel>
          </TabPane>
        </Tabs>
      </div>
    </div>
    <a-modal v-model:visible="visible" title="导入结果" @ok="ModalClose" @cancel="ModalClose" style="top: 0px">
      <p>文件数据：{{excelResultInfo.excellistLength-1}}个</p>
      <p>导入成功：{{excelResultInfo.successInfo}}个</p>
      <p>导入失败：{{excelResultInfo.errorInfo}}个 <a v-if="excelResultInfo.errorInfo>0" style="margin-left: 30px;" @click="errorExcel">下载描述错误excel</a></p>
    </a-modal>
    <template #footer>
      <div style="height: 35px">
        <div style="float: right">
          <a-button @click="handleClose" :disabled="uploading">放弃</a-button>
          <a-button @click="handleOk(true)" type="primary" :loading="uploading" >开始导入
          </a-button>
        </div>
      </div>
    </template>
  </BasicModal>
</template>

<script setup="props, {content}" lang="ts">
import {nextTick, reactive, ref, unref} from 'vue'
import { BasicModal, useModalInner } from '/@/components/Modal'
import { ImpExcel } from '/@/components/Excel'
import {message, Select} from 'ant-design-vue';
import { DownloadOutlined,CloudUploadOutlined,
  CaretDownOutlined,EllipsisOutlined } from '@ant-design/icons-vue';
import {
  Upload as AUpload,
  Select as ASelect,
  Input as AInput,
  Modal as AModal,
  Button,
  Tabs,Tooltip
} from 'ant-design-vue'
const AInputSearch=AInput.Search
const ASelectOption=ASelect.Option
const SelectOption = Select.Option;
import {useMessage} from "/@/hooks/web/useMessage";
import {aoaToSheetXlsx} from "/@/views/boozsoft/xian_jin_liu_liang/yin_hang_dui_zhang/yin_hang_dui_zhang_dan/excel/components/importexcel";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {
  excelVoucherType, excelVoucherType1,
  findAllSysUnitOfMea,
  findMeaTypeAll,
  findMeaTypeList
} from '/@/api/record/system/unit-mea'
const {
  createErrorModal
} = useMessage()

const codeTemplateId = ref('1');
const typeKey = ref('1');
const templateList = ref([{tname:'单计量',id:'1'},{tname:'多计量',id:'2'}]);
const emit=defineEmits(['register','save'])
const isActiveImpExcel=ref(false)
const list:any = ref([])
function loadDataSuccess(excelDataList) {
  if(typeKey.value == '1') {
    list.value = []
    const items = excelDataList[0].results
    if (items.length>0) {
      for (let i = 0; i < items.length; i++) {
        const item = items[i]
        const item1: any = {}
        item1.unitName = item['计量单位名称']
        let s = item['计量类型']
        if(s == '重量'){
          item1.unitType = 1
        }else if(s == '数量'){
          item1.unitType = 2
        }else if(s == '面积'){
          item1.unitType = 3
        }else if(s == '长度'){
          item1.unitType = 4
        }else if(s == '体积'){
          item1.unitType = 5
        }else if(s == '容积'){
          item1.unitType = 6
        }else{
          createErrorModal({
            iconType: 'warning',
            title: '提示',
            content: '第'+(i+1)+'行不存在该计量类型,不能进行信息导入'
          })
        }
        item1.flag = '1'
        list.value.push(item1)
      }
      for (let i=0; i<list.value.length; i++){
        const item1 = list.value[i];
        //判断导入编码是否为空
        const deptCode = item1.unitName
        if (deptCode==null || deptCode==''){
          createErrorModal({
            iconType: 'warning',
            title: '提示',
            content: '第'+(i+1)+'行计量单位名称为空,不能进行信息导入'
          })
          list.value = []
          return false
        }
        //判断导入名称是否为空
        const deptName = item1.unitType
        if (deptName==null || deptName==''){
          createErrorModal({
            iconType: 'warning',
            title: '提示',
            content: '第'+(i+1)+'行计量类型为空,不能进行信息导入'
          })
          list.value = []
          return false
        }
        for (let j=0; j<list.value.length; j++) {
          const item2 = list.value[j];
          if (i!=j){
            if (item1.unitName==item2.unitName){
              createErrorModal({
                iconType: 'warning',
                title: '提示',
                content: '第'+(i+1)+'行计量单位名称与第'+(j+1)+'行的信息重复，请修改后重新导入！'
              })
              list.value = []
              return false
            }
            /*if (item1.unitType==item2.unitType){
              createErrorModal({
                iconType: 'warning',
                title: '提示',
                content: '第'+i+'行计量类型信息与第'+j+'行的信息重复，请修改后重新导入！'
              })
              list.value = []
              return false
            }*/
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
  }else{
    list.value = []
    const items = excelDataList[0].results
    if (items.length>0) {
      for (let i = 0; i < items.length; i++) {
        const item = items[i]
        const item1: any = {}
        item1.unitNameg = item['计量单位组名称']
        item1.unitName = item['主计量']
        item1.unitName1 = item['计量1名称']
        item1.conversionRate1 = item['换算率1']
        item1.unitName2 = item['计量2名称']
        item1.conversionRate2 = item['换算率2']
        let s = item['计量类型']
        if(s == '重量'){
          item1.unitType = 1
        }else if(s == '数量'){
          item1.unitType = 2
        }else if(s == '面积'){
          item1.unitType = 3
        }else if(s == '长度'){
          item1.unitType = 4
        }else if(s == '体积'){
          item1.unitType = 5
        }else if(s == '容积'){
          item1.unitType = 6
        }else{
          createErrorModal({
            iconType: 'warning',
            title: '提示',
            content: '第'+(i+1)+'行不存在该计量类型,不能进行信息导入'
          })
        }
        item1.flag = '1'
        list.value.push(item1)
      }
      for (let i=0; i<list.value.length; i++){
        const item1 = list.value[i];
        const deptCode = item1.unitNameg
        if (deptCode==null || deptCode==''){
          createErrorModal({
            iconType: 'warning',
            title: '提示',
            content: '第'+(i+1)+'行计量单位组名称为空,不能进行信息导入'
          })
          list.value = []
          return false
        }
        const deptName = item1.unitType
        if (deptName==null || deptName==''){
          createErrorModal({
            iconType: 'warning',
            title: '提示',
            content: '第'+(i+1)+'行计量类型为空,不能进行信息导入'
          })
          list.value = []
          return false
        }

        const unitName = item1.unitName
        if (unitName==null || unitName==''){
          createErrorModal({
            iconType: 'warning',
            title: '提示',
            content: '第'+(i+1)+'行主计量为空,不能进行信息导入'
          })
          list.value = []
          return false
        }
        const unitName1 = item1.unitName1
        if (unitName1==null || unitName1==''){
          createErrorModal({
            iconType: 'warning',
            title: '提示',
            content: '第'+(i+1)+'行计量1名称为空,不能进行信息导入'
          })
          list.value = []
          return false
        }
        const conversionRate1 = item1.conversionRate1
        if (conversionRate1==null || conversionRate1==''){
          createErrorModal({
            iconType: 'warning',
            title: '提示',
            content: '第'+(i+1)+'行换算率1名称为空,不能进行信息导入'
          })
          list.value = []
          return false
        }
        /* const conversionExplain1 = item1.conversionExplain1
                if (conversionExplain1==null || conversionExplain1==''){
                  createErrorModal({
                    iconType: 'warning',
                    title: '提示',
                    content: '第'+(i+1)+'行换算说明1名称为空,不能进行信息导入'
                  })
                  list.value = []
                  return false
                }
               const unitName2 = item1.unitName2
                if (unitName2==null || unitName2==''){
                  createErrorModal({
                    iconType: 'warning',
                    title: '提示',
                    content: '第'+(i+1)+'行计量2名称为空,不能进行信息导入'
                  })
                  list.value = []
                  return false
                }
                const conversionRate2 = item1.conversionRate2
                if (conversionRate2==null || conversionRate2==''){
                  createErrorModal({
                    iconType: 'warning',
                    title: '提示',
                    content: '第'+(i+1)+'行换算率2名称为空,不能进行信息导入'
                  })
                  list.value = []
                  return false
                }
                const conversionExplain2 = item1.conversionExplain2
                if (conversionExplain2==null || conversionExplain2==''){
                  createErrorModal({
                    iconType: 'warning',
                    title: '提示',
                    content: '第'+(i+1)+'行换算说明2名称为空,不能进行信息导入'
                  })
                  list.value = []
                  return false
                }*/


        for (let j=0; j<list.value.length; j++) {
          const item2 = list.value[j];
          if (i!=j){
            if (item1.unitNameg==item2.unitNameg){
              createErrorModal({
                iconType: 'warning',
                title: '提示',
                content: '第'+i+'行计量单位组名称与第'+j+'行的信息重复，请修改后重新导入！'
              })
              list.value = []
              return false
            }
            if (item1.unitType==item2.unitType){
              createErrorModal({
                iconType: 'warning',
                title: '提示',
                content: '第'+i+'行计量类型信息与第'+j+'行的信息重复，请修改后重新导入！'
              })
              list.value = []
              return false
            }

            if (item1.unitName==item2.unitName){
              createErrorModal({
                iconType: 'warning',
                title: '提示',
                content: '第'+i+'行主计量与第'+j+'行的信息重复，请修改后重新导入！'
              })
              list.value = []
              return false
            }

            if (item1.unitName1==item2.unitName1){
              createErrorModal({
                iconType: 'warning',
                title: '提示',
                content: '第'+i+'行计量1与第'+j+'行的信息重复，请修改后重新导入！'
              })
              list.value = []
              return false
            }
            if (item1.conversionRate1==item2.conversionRate1){
              createErrorModal({
                iconType: 'warning',
                title: '提示',
                content: '第'+i+'行换算率1与第'+j+'行的信息重复，请修改后重新导入！'
              })
              list.value = []
              return false
            }
            /*if (item1.conversionExplain1==item2.conversionExplain1){
              createErrorModal({
                iconType: 'warning',
                title: '提示',
                content: '第'+i+'行换算说明1与第'+j+'行的信息重复，请修改后重新导入！'
              })
              list.value = []
              return false
            }*/
            if (item1.unitName2==item2.unitName2){
              createErrorModal({
                iconType: 'warning',
                title: '提示',
                content: '第'+i+'行计量2与第'+j+'行的信息重复，请修改后重新导入！'
              })
              list.value = []
              return false
            }
            if (item1.conversionRate2==item2.conversionRate2){
              createErrorModal({
                iconType: 'warning',
                title: '提示',
                content: '第'+i+'行换算率2与第'+j+'行的信息重复，请修改后重新导入！'
              })
              list.value = []
              return false
            }
            /*if (item1.conversionExplain2==item2.conversionExplain2){
              createErrorModal({
                iconType: 'warning',
                title: '提示',
                content: '第'+i+'行换算说明2与第'+j+'行的信息重复，请修改后重新导入！'
              })
              list.value = []
              return false
            }*/
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
}

function loadDataSuccess2(excelDataList) {
  console.log('loadDataSuccess2')
  list.value = []
  const items = excelDataList[0].results
  if (items.length>0) {
    for (let i = 0; i < items.length; i++) {
      const item = items[i]
      const item1: any = {}
      item1.unitNameg = item['计量单位组名称']
      item1.unitName = item['主计量']
      item1.unitName1 = item['计量1名称']
      item1.conversionRate1 = item['换算率1']
      item1.unitName2 = item['计量2名称']
      item1.conversionRate2 = item['换算率2']
      let s = item['计量类型']
      if(s == '重量'){
        item1.unitType = 1
      }else if(s == '数量'){
        item1.unitType = 2
      }else if(s == '面积'){
        item1.unitType = 3
      }else if(s == '长度'){
        item1.unitType = 4
      }else if(s == '体积'){
        item1.unitType = 5
      }else if(s == '容积'){
        item1.unitType = 6
      }else{
        createErrorModal({
          iconType: 'warning',
          title: '提示',
          content: '第'+(i+1)+'行不存在该计量类型,不能进行信息导入'
        })
      }
      item1.flag = '1'
      list.value.push(item1)
    }
    for (let i=0; i<list.value.length; i++){
      const item1 = list.value[i];
      const deptCode = item1.unitNameg
      if (deptCode==null || deptCode==''){
        createErrorModal({
          iconType: 'warning',
          title: '提示',
          content: '第'+(i+1)+'行计量单位组名称为空,不能进行信息导入'
        })
        list.value = []
        return false
      }
      const deptName = item1.unitType
      if (deptName==null || deptName==''){
        createErrorModal({
          iconType: 'warning',
          title: '提示',
          content: '第'+(i+1)+'行计量类型为空,不能进行信息导入'
        })
        list.value = []
        return false
      }

      const unitName = item1.unitName
      if (unitName==null || unitName==''){
        createErrorModal({
          iconType: 'warning',
          title: '提示',
          content: '第'+(i+1)+'行主计量为空,不能进行信息导入'
        })
        list.value = []
        return false
      }
      const unitName1 = item1.unitName1
      if (unitName1==null || unitName1==''){
        createErrorModal({
          iconType: 'warning',
          title: '提示',
          content: '第'+(i+1)+'行计量1名称为空,不能进行信息导入'
        })
        list.value = []
        return false
      }
      const conversionRate1 = item1.conversionRate1
      if (conversionRate1==null || conversionRate1==''){
        createErrorModal({
          iconType: 'warning',
          title: '提示',
          content: '第'+(i+1)+'行换算率1名称为空,不能进行信息导入'
        })
        list.value = []
        return false
      }
      /* const conversionExplain1 = item1.conversionExplain1
              if (conversionExplain1==null || conversionExplain1==''){
                createErrorModal({
                  iconType: 'warning',
                  title: '提示',
                  content: '第'+(i+1)+'行换算说明1名称为空,不能进行信息导入'
                })
                list.value = []
                return false
              }
             const unitName2 = item1.unitName2
              if (unitName2==null || unitName2==''){
                createErrorModal({
                  iconType: 'warning',
                  title: '提示',
                  content: '第'+(i+1)+'行计量2名称为空,不能进行信息导入'
                })
                list.value = []
                return false
              }
              const conversionRate2 = item1.conversionRate2
              if (conversionRate2==null || conversionRate2==''){
                createErrorModal({
                  iconType: 'warning',
                  title: '提示',
                  content: '第'+(i+1)+'行换算率2名称为空,不能进行信息导入'
                })
                list.value = []
                return false
              }
              const conversionExplain2 = item1.conversionExplain2
              if (conversionExplain2==null || conversionExplain2==''){
                createErrorModal({
                  iconType: 'warning',
                  title: '提示',
                  content: '第'+(i+1)+'行换算说明2名称为空,不能进行信息导入'
                })
                list.value = []
                return false
              }*/


      for (let j=0; j<list.value.length; j++) {
        const item2 = list.value[j];
        if (i!=j){
          if (item1.unitNameg==item2.unitNameg){
            createErrorModal({
              iconType: 'warning',
              title: '提示',
              content: '第'+i+'行计量单位组名称与第'+j+'行的信息重复，请修改后重新导入！'
            })
            list.value = []
            return false
          }
          if (item1.unitType==item2.unitType){
            createErrorModal({
              iconType: 'warning',
              title: '提示',
              content: '第'+i+'行计量类型信息与第'+j+'行的信息重复，请修改后重新导入！'
            })
            list.value = []
            return false
          }

          if (item1.unitName==item2.unitName){
            createErrorModal({
              iconType: 'warning',
              title: '提示',
              content: '第'+i+'行主计量与第'+j+'行的信息重复，请修改后重新导入！'
            })
            list.value = []
            return false
          }

          if (item1.unitName1==item2.unitName1){
            createErrorModal({
              iconType: 'warning',
              title: '提示',
              content: '第'+i+'行计量1与第'+j+'行的信息重复，请修改后重新导入！'
            })
            list.value = []
            return false
          }
          if (item1.conversionRate1==item2.conversionRate1){
            createErrorModal({
              iconType: 'warning',
              title: '提示',
              content: '第'+i+'行换算率1与第'+j+'行的信息重复，请修改后重新导入！'
            })
            list.value = []
            return false
          }
          /*if (item1.conversionExplain1==item2.conversionExplain1){
            createErrorModal({
              iconType: 'warning',
              title: '提示',
              content: '第'+i+'行换算说明1与第'+j+'行的信息重复，请修改后重新导入！'
            })
            list.value = []
            return false
          }*/
          if (item1.unitName2==item2.unitName2){
            createErrorModal({
              iconType: 'warning',
              title: '提示',
              content: '第'+i+'行计量2与第'+j+'行的信息重复，请修改后重新导入！'
            })
            list.value = []
            return false
          }
          if (item1.conversionRate2==item2.conversionRate2){
            createErrorModal({
              iconType: 'warning',
              title: '提示',
              content: '第'+i+'行换算率2与第'+j+'行的信息重复，请修改后重新导入！'
            })
            list.value = []
            return false
          }
          /*if (item1.conversionExplain2==item2.conversionExplain2){
            createErrorModal({
              iconType: 'warning',
              title: '提示',
              content: '第'+i+'行换算说明2与第'+j+'行的信息重复，请修改后重新导入！'
            })
            list.value = []
            return false
          }*/
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
      for (let j = 0; j < deptList.value.length; j++) {
        const dept = deptList.value[j];
        if (dept.unitName == item.unitName) {
          msg = '第'+(i+1)+'行计量单位组名称重复,不能进行信息导入'
          return msg
        }
      }
    }
    return msg
  } else {
    msg = '请选择您需要导入的文件!'
    return msg
  }
}
function checkExcel1(){
  msg=''
  if (list.value.length>0) {
    for (let i = 0; i < list.value.length; i++) {
      const item = list.value[i];
      for (let j = 0; j < dlists.value.length; j++) {
        const dept = dlists.value[j];
        if (dept.unitName == item.unitName) {
          msg = '第'+(i+1)+'行计量单位组名称重复,不能进行信息导入'
          return msg
        }
      }
    }
    return msg
  } else {
    msg = '请选择您需要导入的文件!'
    return msg
  }
}
function checkExcel2(){
  msg=''
  if (list.value.length>0) {
    for (let i = 0; i < list.value.length; i++) {
      const item = list.value[i];
      for (let j = 0; j < deptList.value.length; j++) {
        const dept = deptList.value[j];
        if (dept.unitName == item.unitNameg) {
          console.log('计量单位组名称重复,不能进行信息导入')
          msg = '第'+(i+1)+'行计量单位组名称重复,不能进行信息导入'
          return msg
        }
      }
    }
    return msg
  } else {
    msg = '请选择您需要导入的文件!'
    return msg
  }
}
function checkExcel3(){
  msg=''
  if (list.value.length>0) {
    console.log(list.value)
    console.log(dlists.value)

    for (let i = 0; i < list.value.length; i++) {
      const item = list.value[i];
      for (let j = 0; j < dlists.value.length; j++) {
        const dept = dlists.value[j];
        if (dept.unitName == item.unitNameg) {
          msg = '第'+(i+1)+'行计量单位组名称重复,不能进行信息导入'
          return msg
        }
      }
    }
    return msg
  } else {
    msg = '请选择您需要导入的文件!'
    return msg
  }
}
const uploading=ref(false)
const loadingRef = ref(false);
const excelValue:any = ref(1)
const excelResultInfo:any = reactive({
  excellistInfo:[],
  excellistLength:'',
  successInfo:'',
  errorInfo:''
})
/******************* 弹框加载中 **************************/
import { Loading } from '/@/components/Loading';
const compState = reactive({
  absolute: false,
  loading: false,
  tip: '处理中...',
});
function openCompFullLoading() {
  openLoading(false);
}
function openLoading(absolute: boolean) {
  compState.absolute = absolute;
  compState.loading = true;
}
/*******************END**************************/

const deptList:any = ref([])
const zfList:any = ref([])
const dynamicTenantId = ref()
const lists:any = ref([])
const dlists:any = ref([])
const [register, { closeModal }] = useModalInner((data) => {
  dynamicTenantId.value = data.data.dynamicTenantId
  useRouteApi(findAllSysUnitOfMea,{schemaName: dynamicTenantId})().then(res=>{
    deptList.value = res
  })
  useRouteApi(findMeaTypeAll,{schemaName: dynamicTenantId})().then(res=>{
    dlists.value = res
  })
  useRouteApi(findMeaTypeList,{schemaName: dynamicTenantId})().then(res=>{
    lists.value = res
  })
  isActiveImpExcel.value=false
  nextTick(()=>{
    isActiveImpExcel.value=true
  })
})
async function handleOk() {
 if(typeKey.value == '1') {
   if(checkExcel()==''){
     checkExcel1()
   }else{
     createErrorModal({
       iconType: 'warning',
       title: '导入失败',
       content: checkExcel()
     })
     return false
   }
 }else{
   if(checkExcel2()==''){
     checkExcel3()
   }else{
     createErrorModal({
       iconType: 'warning',
       title: '导入失败',
       content: checkExcel2()
     })
     return false
   }
 }
  if (msg=='') {
    if(typeKey.value == '1') {
      await useRouteApi(excelVoucherType, {schemaName: dynamicTenantId})(unref(list)).then(v=>{
        message.success("导入成功！")
      })
    }else{
      await useRouteApi(excelVoucherType1, {schemaName: dynamicTenantId})(unref(list)).then(v=>{
        message.success("导入成功！")
      })
    }
    emit('save', unref(list))
    closeModal()
    return true
  } else {
    createErrorModal({
      iconType: 'warning',
      title: '导入失败',
      content: msg
    })
    return false
  }
}

//下载导入模板
function exportExcel() {
  if(codeTemplateId.value == '1'){
    const arrHeader = ['计量单位名称','计量类型'];
    // 保证data顺序与header一致
    aoaToSheetXlsx({
      data: [],
      header: arrHeader,
      filename: '单计量导入模板.xlsx',
    });
  }else{
    const arrHeader = ['计量单位组名称', '计量类型', '主计量','计量1名称','换算率1','计量2名称','换算率2'];
    // 保证data顺序与header一致
    aoaToSheetXlsx({
      data: [],
      header: arrHeader,
      filename: '多计量导入模板.xlsx',
    });
  }
}
const handleClose = () => {
  closeModal()
}
</script>
<style src="../../../../../assets/styles/global-import-open3.less" lang="less"></style>
<style lang="less" scoped>
:deep(.ant-select-selector) {
  border: none !important;
  border-bottom: 1px solid #bdb9b9 !important;
}
</style>

<template>
  <div>
  <div class="app-container">
    <div class="app-container-head">
<!--      <img src="/img/menu/index_dept.png" class="container-head-img">-->
      <div class="container-head-title" style="margin-left: 20px">
        <b class="noneSpan"><TeamOutlined style="font-size: 26px;" />&nbsp;&nbsp;人员类别</b>
      </div>
      <div class="ant-btn-group" style="float: right">
<!--        <button
          type="button"
          class="ant-btn ant-btn-me"
          ant-click-animating-without-extra-node="false"
          @click="openAddPage()"
        ><span>新增</span></button>-->
<!--        <button
          type="button"
          class="ant-btn ant-btn-me"
          ant-click-animating-without-extra-node="false"
          @click="editOpen()"
        ><span>修改</span></button>
        <button
          type="button"
          class="ant-btn ant-btn-me"
          ant-click-animating-without-extra-node="false"
          @click="delList()"
        ><span>删除</span></button>-->
<!--        <button
          type="button"
          class="ant-btn ant-btn-me"
          @click="openExcel"
        ><span>导入</span></button>
        <button
          type="button"
          class="ant-btn ant-btn-me"
        ><span>导出</span></button>-->
        <button
          type="button"
          class="ant-btn ant-btn-me"
          @click="openInto()"
        ><span>引入</span></button>
        <button
          type="button"
          class="ant-btn ant-btn-me"
          @click="openAssign()"
        ><span>分配</span></button>
<!--        <button
          type="button"
          class="ant-btn ant-btn-me"
          ant-click-animating-without-extra-node="false"
        ><span>打印</span></button>-->
        <button
          type="button"
          class="ant-btn ant-btn-me"
          @click="closeCurrent()"
        ><span>退出</span></button>
      </div>
<!--      <div style="clear: none"/>
      <div style="display: inline-block;float: left;margin-left: 1%;font-size: 14px;width: 50%;">
        <AccountPicker theme="one" @reloadTable="dynamicAdReload"/>
      </div>-->
      </div>
    <div style="display: inline-block;float: left;margin-top: .2%;" class="select-div">
      &ensp;<span>所属组织：</span>
      <a-select v-model:value="originId" @change="changeOrigin()" style="width: 250px;margin-top: -10px;margin-left: 10px">
        <a-select-option v-for="item in organizeList" :key="item.uniqueCode">
          {{ item.orgSimpName }}
        </a-select-option>
      </a-select>
    </div>
    <div style="float: left; margin-left: 10px;line-height: 30px;">共 {{cardList.length}} 条记录</div>
      <div class="app-container-neck">

<!--      <div>-->
<!--        <div style="float: left; line-height: 30px">
          <span style="font-size: 14px; color: black">共{{ cardList.length }}条</span>
        </div>-->
        <div style="float: right; margin-left: 10px">
          <a-button @click="reloadCurrentPage()">
            <SyncOutlined :style="{ fontSize: '14px' }"/>
          </a-button>
<!--          <a-popover class="ant-btn-default" placement="bottom">
            <template #content>
              <span class="group-btn-span-special2" @click="pageParameter.showRulesSize = 'MAX'"
                    :style="pageParameter.showRulesSize==='MAX'?{backgroundColor: '#0096c7',color: 'white'}:''">
                <SortDescendingOutlined/>&nbsp;大号字体&ensp;<CheckOutlined
                v-if="pageParameter.showRulesSize==='MAX'"/></span><br/>
              <span class="group-btn-span-special2" @click="pageParameter.showRulesSize = 'MIN'"
                    :style="pageParameter.showRulesSize==='MIN'?{backgroundColor: '#0096c7',color: 'white'}:''">
                <SortAscendingOutlined/>&nbsp;小号字体&ensp;<CheckOutlined
                v-if="pageParameter.showRulesSize==='MIN'"/></span>
            </template>
            <a-button>
              <SettingFilled :style="{ fontSize: '14px' }"/>
            </a-button>
          </a-popover>-->
<!--          <a-popover placement="bottom">
            <a-button>
              <PicLeftOutlined :style="{ fontSize: '14px' }" />
            </a-button>
          </a-popover>

          <a-button>
            <EditFilled :style="{ fontSize: '14px' }" />
          </a-button>-->

<!--          <a-button @click="activeKey=!activeKey">
            <PieChartFilled :style="{ fontSize: '14px' }" />
          </a-button>-->
<!--          <a-button>
            <FilterFilled :style="{ fontSize: '14px' }" />
          </a-button>-->
        </div>
        <div style="float: right; position: relative">
<!--          <label style="font-size: 14px">检索条件：</label>-->
          <a-select v-model:value="formItems.selectType" style="width: 120px;font-size: 12px;" class="special_select">
            <a-select-option style="font-size: 12px;" value="1">类别编码</a-select-option>
            <a-select-option style="font-size: 12px;" value="2">类别名称</a-select-option>
          </a-select>
          <!-- 搜索 -->
          <a-input-search
            placeholder=""
            style="width: 200px; border-radius: 4px"
            @search="onSearch"
          />
        </div>
      </div>
    </div>
<!--    <div style="clear:both" />-->
  <div class="app-container">
    <Edit
      @save="saveData"
      @register="registerEditPage"
    />
    <Excel @save="saveExcel" @register="registerExcelPage"/>
    <Into @save="saveInto" @register="registerIntoPage"/>
    <Assign @save="saveAssign" @register="registerAssignPage"/>

    <div
      v-if="!activeKey"
      :class="cardList"
    >
      <div :class="`${cardList}__content`">
        <!--        <div>ID:{{ item.id }}</div>-->
        <a-list :style="{height: treeHeight,overflowY: 'scroll',overflowX: 'hidden'}">
          <a-row :gutter="16">
            <template
              v-for="(item,index) in cardList"
              :key="index"
            >
              <a-col :span="6">
                <a-list-item style="width: 95%">
                  <a-card
                    :hoverable="true"
                    :class="`${cardList}__card`"
                    style="width: 100%"
                  >
                    <div style="width: 100%;float: right;">
                      <div :class="`${cardList}__card-title`">
                        <Icon
                          v-if="item.icon"
                          class="icon"
                          :icon="item.icon"
                          :color="item.color"
                        />
                        <!--                        {{ index+1 }}-->
                      </div>
                      <div style="float: right; border: 1px solid rgb(1, 143, 251);border-radius: 10px;padding: 0 10px; margin-top: -5px;">
                        {{ item.psnTypeCode }}
                      </div>
                      <div style="font-size: 24px;margin-top: 20px;">
                        {{ item.psnTypeName }}
                      </div>
                      <div v-if="item.psnTypeCode!='9999'" style="float: right;margin-top: -20px;">
                        <a-popover placement="bottom" v-if="item.sysFlag!='1'">
                          <a-button style="padding: 0px 4px; height: 20px;margin-right: 10px;">
                            <EllipsisOutlined />
                          </a-button>
                          <template #content>
<!--                            <p style="cursor: pointer" class="p_specifics" @click="openEdit(item)"><FormOutlined /> 修改</p>-->
                            <p style="cursor: pointer" class="p_specifics" @click="del(item)"><DeleteOutlined /> 删除</p>
                          </template>
                        </a-popover>
                      </div>
                    </div>
                  </a-card>
                </a-list-item>
              </a-col>
            </template>
          </a-row>
        </a-list>
      </div>

    </div>
  </div>
  </div>
</template>
<script setup="props, {emit}" lang="ts">
import {psnTypeFindAll, deletePsnType, excelPsnType, savePsnType} from '/@/api/record/system/origin-psn-type'
import {
  DatePicker as ADatePicker,
  Select as ASelect,
  Input as AInput,
  Popover as APopover,
  Switch as ASwitch,
  Radio as ARadio,
  Upload as AUpload,
  List as AList,
  Row as ARow,
  Menu as AMenu,
  Dropdown as ADropdown,
  Card as ACard,
  Col as ACol,
  Divider as ADivider,
  message
} from "ant-design-vue"
const ARangePicker=ADatePicker.RangePicker
const ASelectOption=ASelect.Option
const AInputSearch=AInput.Search
const ARadioGroup=ARadio.Group
const AListItem=AList.Item
const AMenuItem=AMenu.Item
import { BasicTable, useTable } from '/@/components/Table'
import Edit from './popup/edit.vue'
import Excel from './popup/excel.vue'
import Into from './popup/into.vue'
import Assign from './popup/assign.vue'
import {useModal} from '/@/components/Modal'
import {onMounted, reactive, ref} from 'vue'
import Icon from '/@/components/Icon/index'
import {
  CaretDownFilled,
  FormOutlined,
  DeleteOutlined,
  SettingFilled,
  SyncOutlined,
  PicLeftOutlined,
  EditFilled,
  PieChartFilled,
  FilterFilled,
  SortDescendingOutlined,
  CheckOutlined,
  SortAscendingOutlined,
  EllipsisOutlined,
  TeamOutlined
} from '@ant-design/icons-vue'

import {useMessage} from "/@/hooks/web/useMessage";
import {psnFindAll} from "/@/api/record/system/origin-psn";
import {useTabs} from "/@/hooks/web/useTabs";
import router from "/@/router";
import {getOrganizeAll} from "/@/api/record/group/im-organize";
import {
  findByUniqueCode,
  findByOriginIdAndUniqueCode,
  save
} from "/@/api/record/system/group-psn-type-account";

const {
  createConfirm,
  createWarningModal
} = useMessage()

const {closeCurrent} = useTabs(router);

//查询组织
const organizeList:any = ref([])
const originId:any = ref('')
async function reloadOrigin(){
  organizeList.value = await getOrganizeAll()
  if (organizeList.value.length>0){
    originId.value = organizeList.value[0].uniqueCode
  }
}
//改变组织
async function changeOrigin() {
  await reloadCurrentPage()
}

const formItems = ref({
  selectType: '1'
})

const [registerEditPage, {openModal: openEditPage}] = useModal()
const [registerExcelPage, {openModal: openExcelPage}] = useModal()
const val = {
  id: null,
  uniqueCode: '',
  psnTypeCode: '',
  psnTypeName: '',
  originId: ''
}
const treeHeight = (document.documentElement.clientHeight - 180) + 'px'
const openAddPage = () => {
  val.originId = originId.value
  openEditPage(true, {
    data: val,
    isState: '0'
  })
}
const openEdit = (data:any) => {
  openEditPage(true, {
    data: data,
    isState: '1'
  })
}
const del = async (data: any) => {
  // await useRouteApi(deletePsnType, {schemaName: dynamicTenantId})(data)
  // message.success('删除成功！')
  // // alert('删除成功！')
  // await reload()
  // checkRow.value = []
  // state.selectedRowKeys = []
  const res = await findByUniqueCode(data.uniqueCode)
  if (res.length > 0){
    const list = res.filter(aa=> aa.ctype=='2' && aa.originId==originId.value)
    if (list.length>0) {
      createWarningModal({
        iconType: 'warning',
        title: '删除',
        content: '人员类别已分配不允许删除！'
      })
      return false
    }
  }
  const psnList = await psnFindAll({originId:originId.value})
  for (let a = 0; a < psnList.length; a++) {
    const psn = psnList[a]
    if (data.uniqueCode == '9999') {
      createWarningModal({
        iconType: 'warning',
        title: '删除',
        content: '编码为“9999”的人员类别为系统预制类别不允许删除！'
      })
      return false
    }
    if (psn.uniquePsnType == data.uniqueCode) {
      createWarningModal({
        iconType: 'warning',
        title: '删除',
        content: '<span style="font-weight: bold;color: red;">[' + data.psnTypeName + ']</span>人员类别中经已存在人员，不能进行删除操作！'
      })
      return false
    }
  }
  createConfirm({
    iconType: 'error',
    title: '警告',
    content: '删除后数据将不能恢复，你确认要删除吗?',
    onOk: async () => {
      const item1 = await findByOriginIdAndUniqueCode(originId.value,data.uniqueCode)
      if (item1.length>0) {
        item1[0].flag = '0'
        item1[0].acceptUser = ''
        item1[0].acceptDate = ''
        await save(item1[0])
      }
      await deletePsnType(data)
      message.success('删除成功！')
      await reloadCurrentPage()
      return true
    },
    onCancel: () => {
      return false
    }
  })
}

//选中内容
const state = reactive<{
  selectedRowKeys: [];
  loading: boolean;
}>({
  selectedRowKeys: [], // Check here to configure the default column
  loading: false,
});
const checkRow: any = ref([])
const onSelectChange = (selectedRowKeys, row) => {
  // console.log('selectedRowKeys changed: ', row);
  state.selectedRowKeys = selectedRowKeys;
  checkRow.value = row
};
const editOpen = () => {
  if (checkRow.value.length == 1) {
    openEditPage(true, {
      data: checkRow.value[0],
      isState: '1'
    })
  } else {
    createWarningModal({
      iconType: 'warning',
      title: '编辑',
      content: '请选择一条进行编辑！'
    })
  }
}

async function delList() {
  if (checkRow.value.length > 0) {
    const psnList = await psnFindAll({originId:originId.value})
    for (let i = 0; i < checkRow.value.length; i++) {
      const item = checkRow.value[i]
      for (let a = 0; a < psnList.length; a++) {
        const psn = psnList[a]
        if (item.uniqueCode == '9999') {
          createWarningModal({
            iconType: 'warning',
            title: '删除',
            content: '编码为“9999”的人员类别为系统预制类别不允许删除！'
          })
          return false
        }
        if (psn.uniquePsnType == item.uniqueCode) {
          createWarningModal({
            iconType: 'warning',
            title: '删除',
            content: '<span style="font-weight: bold;color: red;">[' + item.psnTypeName + ']</span>人员类别中经已存在人员，不能进行删除操作！'
          })
          return false
        }
      }
    }
    createConfirm({
      iconType: 'error',
      title: '警告',
      content: '删除后数据将不能恢复，你确认要删除吗?',
      onOk: async () => {
        for (let i = 0; i < checkRow.value.length; i++) {
          const item = checkRow.value[i]
          const item1 = await findByOriginIdAndUniqueCode(originId.value,item.uniqueCode)
          if (item1.length>0) {
            item1[0].flag = '0'
            item1[0].acceptUser = ''
            item1[0].acceptDate = ''
            await save(item1[0])
          }
          await deletePsnType(item)
        }
        message.success('删除成功！')
        await reloadCurrentPage()
      },
      onCancel: () => {
        return false
      }
    })
  } else {
    createWarningModal({
      iconType: 'warning',
      title: '删除',
      content: '请选择需要删除的内容！'
    })
  }
}

//导入
const openExcel = () => {
  openExcelPage(true, {
    originId: originId.value
  })
}

async function saveExcel(data) {
  await excelPsnType(data)
  message.success('导入成功！')
  await reloadCurrentPage()
}

const activeKey = ref(false)

const cardList: any = ref({})

async function reloadCurrentPage() {
  const res = await psnTypeFindAll(originId.value)
  cardList.value = res.items
  // console.log(cardList.value)
}

onMounted(async () => {
  await reloadOrigin()
  await reloadCurrentPage()
})
async function saveData(data:any) {
  await savePsnType(data)
  await reloadCurrentPage()
}

function onSearch(){}

//分配
const [registerIntoPage, {openModal: openIntoPage}] = useModal()
const openInto = () => {
  openIntoPage(true, {
    originId: originId.value
  })
}

async function saveInto(data) {
  await reloadCurrentPage()
}

//分配
const [registerAssignPage, {openModal: openAssignPage}] = useModal()
const openAssign = () => {
  openAssignPage(true, {
    originId: originId.value
  })
}

const loadMark = ref(false);
async function saveAssign(data) {
}

</script>
<style src="./global-menu-index.less" lang="less" scoped></style>
<style scoped lang="less">
:deep(.ant-card-body) {
  padding: 16px;
  border-left: 2px solid rgb(1, 143, 251);
  box-shadow: rgb(72 113 140) -3px 1px 7px -1px;
}
.a-table-font-size-16 :deep(td),
.a-table-font-size-16 :deep(th) {
  font-size: 16px !important;
  padding: 5px 8px !important;
}

.a-table-font-size-12 :deep(td),
.a-table-font-size-12 :deep(th) {
  font-size: 14px !important;
  padding: 2px 8px !important;
}

.app-container:nth-of-type(1) {
  background-color: #f2f2f2;
  padding: 10px 5px;
  margin: 10px 10px 5px;
}

.app-container:nth-of-type(2) {
  padding: 0px;
  margin: 5px 10px;
  background-color: #b4c8e3 !important;
  background-image: url("/@/assets/images/homes/bg-pattern.png");
  background-repeat: no-repeat;
  background-position: 50% 50%;
}
</style>

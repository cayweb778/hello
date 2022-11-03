<template>
  <div>
    <div class="app-container">

      <div class="app-container-head">
<!--        <img :src="getThisIndexImg()" class="container-head-img" draggable="false">-->
<!--        <div class="container-head-title" style="margin-left: 20px;">
          <b class="noneSpan"><UnorderedListOutlined />&nbsp;&nbsp;银行对账单模板</b>
        </div>-->
        <div class="container-head-title" style="float: left;">
          <b class="noneSpan" style="font-size: 60px;"><ProfileOutlined /></b>
        </div>
        <div class="container-head-title" style="padding-left: 40%;text-align: center;margin-top: 30px;">
          <b class="noneSpan" style="font-size: 26px;">银行对账单模板</b>
        </div>

        <!--      <div style="display: inline">
                <select class="head-index-select">
                  <option
                    style="border: none; outline: none"
                    value=""
                  >查看全部 </option>
                </select>
              </div>-->

        <div
          class="ant-btn-group"
          data-v-a1ccd506=""
          style="float: right"
        >
          <button
            type="button"
            class="ant-btn ant-btn-me"
            ant-click-animating-without-extra-node="false"
            @click="openAddPage()"
          ><span>新建</span></button>
<!--          <button
            type="button"
            class="ant-btn ant-btn-me"
            @click="editOpen()"
          ><span>修改</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            ant-click-animating-without-extra-node="false"
            @click="delList()"
          ><span>删除</span></button>-->
          <button
            type="button"
            class="ant-btn ant-btn-me"
            @click="closeCurrent(),router.push('/home/welcome')"
          ><span>退出</span></button>
          <!--        <button
                    type="button"
                    class="ant-btn"
                    ant-click-animating-without-extra-node="false"
                  ><span>打印</span></button>
                  <button
                    type="button"
                    class="ant-btn"
                  ><span>导入</span></button>
                  <button
                    type="button"
                    class="ant-btn"
                  ><span>导出</span></button>-->
        </div>
      </div>

      <!--    <UnitChange v-if="!defaultPage" @reload="dynamicAdReload" v-model="pageParameter"/>-->
      <div style="clear: none;"></div>
      <div style="margin-top: -30px;margin-left: 90px;">
        <div style="display: inline-block;float: left;margin-top: -20px;">
          <AccountPicker theme="three" @reloadTable="dynamicAdReload"/>
        </div>
        <div style="display: inline-block;position:absolute;top:85px;left:110px;font-size: 10px;color:#999999;">共 {{tableDataAll.length}} 条记录</div>
      <div class="app-container-neck">
<!--              <div style="float: left; line-height: 30px">
                <span style="font-size: 14px; color: black">共{{ tableDataAll.length }}条</span>
              </div>-->
        <div style="float: right; margin-left: 10px">
          <a-button class="ant-btn-me">
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
          <a-popover class="ant-btn-default" placement="bottom">
            <template #content>
            <span class="group-btn-span-special2" @click="onChangeSwitch('')"
                  :style="flag==''?{backgroundColor: '#0096c7',color: 'white'}:''">
                <ReadOutlined/>&nbsp;&emsp;全部&emsp;&ensp;<CheckOutlined
              v-if="flag==''"/></span><br/>
              <span class="group-btn-span-special2" @click="onChangeSwitch('1')"
                    :style="flag=='1'?{backgroundColor: '#0096c7',color: 'white'}:''">
                <SafetyOutlined/>&nbsp;&emsp;启用&emsp;&ensp;<CheckOutlined
                v-if="flag=='1'"/></span><br/>
              <span class="group-btn-span-special2" @click="onChangeSwitch('0')"
                    :style="flag=='0'?{backgroundColor: '#0096c7',color: 'white'}:''">
                <RestOutlined/>&nbsp;&emsp;停用&emsp;&ensp;<CheckOutlined
                v-if="flag=='0'"/></span>
            </template>
            <a-button>
              <PicLeftOutlined :style="{ fontSize: '14px' }"/>
            </a-button>
          </a-popover>
          <!--        <a-popover placement="bottom">
                    <a-button>
                      <PicLeftOutlined :style="{ fontSize: '14px' }" />
                    </a-button>
                  </a-popover>

                  <a-button>
                    <EditFilled :style="{ fontSize: '14px' }" />
                  </a-button>-->

<!--          <a-button @click="activeKey=!activeKey">
            <PieChartFilled :style="{ fontSize: '14px' }"/>
          </a-button>-->
          <!--        <a-button>
                    <FilterFilled :style="{ fontSize: '14px' }" />
                  </a-button>-->
        </div>
<!--        <div style="float: right; position: relative">
          <a-select v-model:value="formItems.selectType" style="width: 120px;font-size: 12px;"
                    class="special_select">
            <a-select-option style="font-size: 12px;" value="1">模板名称</a-select-option>
            <a-select-option style="font-size: 12px;" value="2">对应账户</a-select-option>
            <a-select-option style="font-size: 12px;" value="3">状态</a-select-option>
          </a-select>
          <a-input-search
            placeholder=""
            style="width: 200px; border-radius: 4px"
            @search="onSearch"
          />
        </div>-->
      </div>
      </div>
    </div>

    <div class="app-container">
      <Edit
        @save="saveData"
        @register="registerEditPage"
      />
      <BasicTable
        v-if="activeKey"
        :row-selection="{ type: 'checkbox', selectedRowKeys: state.selectedRowKeys, onChange: onSelectChange}"
        @row-click="condClick"
        :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
        @register="registerTable"
      >
        <template #flag="{ record }">
        <span>
          <a-tag
            :color="record.flag === '1' ? 'green' : 'volcano'"
          >
            {{ record.flag === '1' ? '启用' : '停用' }}
          </a-tag>
        </span>
        </template>
        <template #action="{ record }">

          <div v-if="record.sysFlag!='1'">
            <a-popover placement="bottom">
              <a-button style="padding: 0px 4px; height: 20px;">
                <CaretDownFilled/>
              </a-button>
              <template #content>
                <!--              <p v-if="record.flag=='1'" style="cursor: pointer" @click="openEdit(record)"><FormOutlined /> 编辑</p>-->
                <p v-if="record.flag=='0'" class="p_specifics" style="cursor: pointer"
                   @click="editFlagData(record)">
                  <CheckCircleOutlined/>
                  启用
                </p>
                <p v-if="record.flag=='1'" class="p_specifics" style="cursor: pointer"
                   @click="editFlagData(record)">
                  <CloseCircleOutlined/>
                  停用
                </p>
                <!--              <p v-if="record.flag=='0'" style="cursor: pointer" @click="del(record)"><DeleteOutlined /> 删除</p>-->
              </template>
            </a-popover>
          </div>
        </template>

      </BasicTable>
      <div
        v-if="!activeKey"
        :class="cardList"
        style="width: 100%;"
      >
        <div :class="`${cardList}__content`">
          <!--        <div>ID:{{ item.id }}</div>-->
          <a-list style="height: 700px;overflow-y: scroll;overflow-x: hidden;">
            <a-row :gutter="16">
              <template
                v-for="(item) in cardList"
              >
                <a-col :span="8">
                  <a-list-item style="width: 95%">
                    <a-card
                      :hoverable="true"
                      style="width: 100%"
                    >
                      <div class="abc" style="width: 100%;float: right;" @click="openEdit(item,false)">
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
                          {{ item.flag === '1' ? '启用' : '停用' }}
                        </div>
                        <div style="font-size: 24px;margin-top: 25px;">
                          {{ item.templateName }}
                        </div>
                        <div style="float: right;margin-top: -20px;">
<!--                          <a-popover placement="bottom" v-if="item.sysFlag!='1'">-->
                          <a-popover placement="bottom" v-if="item.templateName!='系统模板'">
                            <a-button style="padding: 0px 4px; height: 20px;margin-right: 10px;">
                              <EllipsisOutlined/>
                            </a-button>
<!--                            <a-button style="padding: 0px 4px; height: 20px;width:30px;margin-right: 10px;">
                              <DashOutlined/>
                            </a-button>-->
                            <template #content>
                              <p style="cursor: pointer" class="p_specifics" @click="openEdit(item,true)"><FormOutlined /> 修改</p>
                              <p style="cursor: pointer" class="p_specifics" @click="del(item)"><DeleteOutlined /> 删除</p>
                              <p v-if="item.flag=='0'" class="p_specifics" @click="editFlagData(item)" style="cursor: pointer">
                                <CheckCircleOutlined/>启用
                              </p>
                              <p v-if="item.flag=='1'" class="p_specifics" @click="editFlagData(item)" style="cursor: pointer">
                                <CloseCircleOutlined/>停用
                              </p>
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
import {
  getBankTemplateList,
  findBankTemplateByFlag,
  deleteBankTemplate,
  saveBankTemplate,
  editFlag
} from '/@/api/record/system/bank-template'

import {BasicTable, useTable} from '/@/components/Table'
import Edit from './popup/edit.vue'
import UnitChange from '../../../system/department/UnitChange.vue';
import {useModal} from '/@/components/Modal'
import {onMounted, reactive, ref} from 'vue'
import Icon from '/@/components/Icon/index'
import {
  CaretDownFilled,
  FormOutlined,
  DeleteOutlined,
  CheckCircleOutlined,
  CloseCircleOutlined,
  SettingFilled,
  SyncOutlined,
  PicLeftOutlined,
  EditFilled,
  PieChartFilled,
  FilterFilled,
  SortDescendingOutlined,
  CheckOutlined,
  SortAscendingOutlined,
  DashOutlined,
  EllipsisOutlined,
  UnorderedListOutlined,
  ReadOutlined,
  SafetyOutlined,
  RestOutlined,
  ProfileOutlined
} from '@ant-design/icons-vue'
import {
  Select as ASelect,
  Input as AInput,
  List as AList,
  Row as ARow,
  Col as ACol,
  Card as ACard,
  Popover as APopover,
  Button as AButton,
  Tag as ATag
} from 'ant-design-vue'
import {getThisAdInfoData} from "/@/api/record/system/financial-settings";
import {getCurrentAccountName, getThisIndexImg} from "/@/api/task-api/tast-bus-api";
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker.vue";
import {useCompanyOperateStoreWidthOut} from "/@/store/modules/operate-company";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {useMessage} from "/@/hooks/web/useMessage";
import {message} from "ant-design-vue/lib/components";
import {useTabs} from "/@/hooks/web/useTabs";
import router from "/@/router";

const AListItem = AList.Item
const AInputSearch = AInput.Search
const ASelectOption = ASelect.Option

const {
  createErrorModal,
  createWarningModal,
  createConfirm
} = useMessage()

const {closeCurrent} = useTabs(router);

const formItems = ref({
  selectType: '1'
})

const dynamicTenantId = ref(getCurrentAccountName(true))
const defaultAdName = ref(useCompanyOperateStoreWidthOut().getSchemaName)
const CrudApi = {
  list: useRouteApi(getBankTemplateList, {schemaName: dynamicTenantId}),
  columns: [
    {
      title: 'ID',
      dataIndex: 'id',
      defaultHidden: true,
      ellipsis: true
    },
    {
      title: '模板名称',
      dataIndex: 'templateName',
      customCell: () => {		// 在此处可以修改单元格中的样式
        return {style: {'color': 'blue', 'text-align': 'left'}} // return 想要设置的样式
      },
      ellipsis: true
    },
    /*{
      title: '对应账户',
      dataIndex: 'templateTitleStart',
      ellipsis: true
    },*/
    {
      title: '是否启用',
      dataIndex: 'flag',
      ellipsis: true,
      slots: {customRender: 'flag'}
    }
  ],
  editData: {
    id: '',
    templateName: '',
    templateEnName: '',
    templateTitleStart: '',
    flag: ''
  }
}
// 这是示例组件
const [registerTable, {
  reload,
  getDataSource,
  setTableData,
  setColumns,
  getColumns,
  getPaginationRef,
  setPagination
}] = useTable({
  api: CrudApi.list,
  columns: CrudApi.columns,
  bordered: true,
  showIndexColumn: false,
  actionColumn: {
    width: 160,
    title: '操作',
    dataIndex: 'action',
    slots: {customRender: 'action'}
  }
})
const [registerEditPage, {openModal: openEditPage}] = useModal()
const val = {
  templateName: '',
  templateEnName: '',
  templateTitleStart: '',
  flag: ''
}
const condClick = (data: any, index: any, e: any) => {
  if (e.target.cellIndex == 1) {
    openEditPage(true, {
      data: data,
      dynamicTenantId: dynamicTenantId.value,
      defaultAdName: defaultAdName.value,
      isEdit: false,
      isState: '2'
    })
  }
}
const openAddPage = () => {
  openEditPage(true, {
    data: val,
    dynamicTenantId: dynamicTenantId.value,
    defaultAdName: defaultAdName.value,
    isEdit: true,
    isState: '0'
  })
}
const openEdit = (data: any,flag) => {

  openEditPage(true, {
    data: data,
    dynamicTenantId: dynamicTenantId.value,
    defaultAdName: defaultAdName.value,
    isEdit: flag,
    isState: '1'
  })
}
const del = async (data: any) => {
  createConfirm({
    iconType: 'error',
    title: '警告',
    content: '删除后数据将不能恢复，你确认要删除吗?',
    onOk: async () => {
      await useRouteApi(deleteBankTemplate, {schemaName: dynamicTenantId})(data)
      // alert('删除成功！')
      message.success('删除成功！')
      await reloadTable()
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
    console.log(checkRow.value[0].sysFlag)
    if (checkRow.value[0].sysFlag == '1') {
      createErrorModal({
        iconType: 'warning',
        title: '修改',
        content: '系统模板不允许修改！'
      })
      return false
    } else {
      openEditPage(true, {
        data: checkRow.value[0],
        dynamicTenantId: dynamicTenantId.value,
        defaultAdName: defaultAdName.value,
        isEdit: true
      })
    }
  } else {
    createWarningModal({
      iconType: 'warning',
      title: '修改',
      content: '请选择一条进行编辑！'
    })
  }
}

async function delList() {
  if (checkRow.value.length > 0) {
    let num = 0
    for (let i = 0; i < checkRow.value.length; i++) {
      const item = checkRow.value[i]
      if (item.sysFlag == '1') {
        num++
        createErrorModal({
          iconType: 'warning',
          title: '删除',
          content: '系统模板不允许删除！'
        })
        return false
      }
    }
    if (num == 0) {
      createConfirm({
        iconType: 'error',
        title: '警告',
        content: '删除后数据将不能恢复，你确认要删除吗?',
        onOk: async () => {
          for (let i = 0; i < checkRow.value.length; i++) {
            const item = checkRow.value[i]
            await useRouteApi(deleteBankTemplate, {schemaName: dynamicTenantId})(item.id)
          }
          message.success('删除成功！')
          await reloadTable()
        },
        onCancel: () => {
          return false
        }
      })
    }
  } else {
    createErrorModal({
      iconType: 'warning',
      title: '删除',
      content: '请选择需要删除的内容！'
    })
    return false
  }
}

async function reloadTable() {
  await reloadCurrentPage()
  if (activeKey.value) {
    await reload()
  }
  checkRow.value = []
  state.selectedRowKeys = []
}

const activeKey = ref(false)

const cardList: any = ref({})
const tableDataAll: any = ref({})
const flag: any = ref("1")

async function reloadCurrentPage() {
  const res: any = await useRouteApi(getBankTemplateList, {schemaName: dynamicTenantId})({})
  tableDataAll.value = res.items
  if (flag.value=='1'){
    cardList.value = tableDataAll.value.filter(item => item.flag=='1')
  } else if(flag.value=='0'){
    cardList.value = tableDataAll.value.filter(item => item.flag=='0')
  } else {
    cardList.value = tableDataAll.value
  }
  console.log(cardList.value)
}

function onChangeSwitch(str) {
  flag.value = str
  if (flag.value=='1'){
    cardList.value = tableDataAll.value.filter(item => item.flag=='1')
  } else if(flag.value=='0'){
    cardList.value = tableDataAll.value.filter(item => item.flag=='0')
  } else {
    cardList.value = tableDataAll.value
  }
}

onMounted(async () => {
  await reloadCurrentPage()
})

async function saveData(data: any) {
  await useRouteApi(saveBankTemplate, {schemaName: dynamicTenantId})(data)
  await reloadTable()
}

async function editFlagData(data: any) {
  await useRouteApi(editFlag, {schemaName: dynamicTenantId})(data)
  await reloadTable()
}

function onSearch() {
}

const pageParameter: any = reactive({
  showRulesSize: 'MIN',
  companyCode: '100',
  companyName: '北京希格科技',
  ifUnit: false,
})
const defaultPage = ref(false)
onMounted(async () => {
  // 进页面执行
  getThisAdInfoData({'accId': defaultAdName.value}).then(res => {
    if (null != res && res.independent == 0) {
      defaultPage.value = false
      pageParameter.companyName = res.accNameCn
      pageParameter.companyCode = res.coCode
      pageParameter.ifUnit = res.icorp == '1'
    }
  })
})
const dynamicAdReload = async (obj) => {
  dynamicTenantId.value = obj.accountMode
  defaultAdName.value = obj.accId
  await reloadCurrentPage()
  if (activeKey.value) {
    let data: any = {}
    data.page = getPaginationRef().current
    data.size = getPaginationRef().pageSize
    let res: any = await useRouteApi(CrudApi.list, {schemaName: obj.accountMode})(data)
    setTableData([]) // 清空可能残留的数据
    setTableData(res.items)
    // 底部分页信息
    pageParameter.thisAdInfo = {}
    pageParameter.thisAdInfo = obj
    pageParameter.total = res.total
    setPagination({total: res.total})
  }
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

.abc:hover {
  color: blue; /*DiV背景颜色*/
}

.app-container:nth-of-type(1) {
  background-color: #f2f2f2;
  padding: 10px 5px;
  margin: 10px 10px 5px;
}

.app-container:nth-of-type(2) {
  padding: 0px;
  margin: 5px 10px;
  background: #b4c8e3 !important;
}
</style>

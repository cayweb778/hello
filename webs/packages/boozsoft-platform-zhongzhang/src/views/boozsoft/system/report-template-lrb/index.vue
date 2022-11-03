<template>
  <div>

    <Edit
      @save="saveData"
      @register="registerEdit"
    />
    <AddSelect
      @save="addModelPage"
      @register="registerAddSelectPage"
    />

    <div class="app-container">

      <div class="app-container-head">
        <img src="/img/menu/index_dept.png">

        <span style="    margin-left: 10px;"><b>利润表模板</b></span>
        <div style="display: inline">
          <select class="head-index-select">
            <option
              style="border: none; outline: none"
              value=""
            >查看全部 </option>
          </select>
        </div>

        <div
          class="ant-btn-group"
          data-v-a1ccd506=""
          style="float: right"
        >
          <button
            type="button"
            class="ant-btn"
            ant-click-animating-without-extra-node="false"
            @click="openAddPage()"
          ><span>新建</span></button>
          <button
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
          ><span>导出</span></button>
        </div>
      </div>
      <div class="app-container-neck">
        <div style="float: left; line-height: 30px">
          <span style="font-size: 14px; color: black">共{{ cardList.length }}条</span>
        </div>
        <div style="float: right; margin-left: 10px">
          <a-button class="ant-btn-me">
            <SyncOutlined :style="{ fontSize: '14px' }" />
          </a-button>
          <a-popover placement="bottom">
            <a-button>
              <SettingFilled :style="{ fontSize: '14px' }" />
            </a-button>
          </a-popover>
          <a-popover placement="bottom">
            <a-button>
              <PicLeftOutlined :style="{ fontSize: '14px' }" />
            </a-button>
          </a-popover>

          <a-button>
            <EditFilled :style="{ fontSize: '14px' }" />
          </a-button>

          <a-button @click="activeKey=!activeKey">
            <PieChartFilled :style="{ fontSize: '14px' }" />
          </a-button>
          <a-button>
            <FilterFilled :style="{ fontSize: '14px' }" />
          </a-button>
        </div>
        <div style="float: right; position: relative">
          <!-- 搜索 -->
          <a-input-search
            placeholder=""
            style="width: 200px; border-radius: 4px"
            @search="onSearch"
          />
        </div>
      </div>
      <!--    <Edit
            @save="saveData"
            @register="registerEditPage"
          />-->
      <BasicTable
        v-if="activeKey"
        :row-selection="{ type: 'checkbox' ,fixed: true}"
        @row-click="condClick"
        @register="registerTable"
      >
        <template #accStandard="{ record }"> {{ formatAccStandard(record.accStandard) }} </template>
        <template #flag="{ record }"> {{ record.flag === '1' ? '启用' : '停用' }} </template>
        <template #sysFlag="{ record }"> {{ record.sysFlag === '1' ? '是' : '否' }} </template>
        <template #action="{ record }">

          <div v-if="record.sysFlag!='1'">
            <a-popover placement="bottom">
              <a-button style="padding: 0px 4px; height: 20px;">
                <CaretDownFilled />
              </a-button>
              <template #content>
                <p v-if="record.flag=='1'" style="cursor: pointer" @click="openEdit(record)"><FormOutlined /> 编辑</p>
                <p v-if="record.flag=='0'" style="cursor: pointer" @click="editFlagData(record)"><CheckCircleOutlined /> 启用</p>
                <p v-if="record.flag=='1'" style="cursor: pointer" @click="editFlagData(record)"><CloseCircleOutlined /> 停用</p>
                <p style="cursor: pointer" @click="del(record)"><DeleteOutlined /> 删除</p>
              </template>
            </a-popover>
          </div>
        </template>

      </BasicTable>
      <div
        v-if="!activeKey"
        :class="cardList"
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
                      :class="`${cardList}__card`"
                      style="width: 100%"
                    >
                      <div style="width: 100%;float: right;" @click="openEdit(item)">
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
                          {{ item.titleName }}
                        </div>
                        <div style="font-size: 24px;margin-top: 20px;">
                          {{ item.templateName }}
                        </div>
                        <div style="font-size: 14px;">
                          会计准则：{{ formatAccStandard(item.accStandard) }}
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
import { findByReportName, findByReportNameAndFlag, deleteTemplate, saveTemplate, editFlag } from '/@/api/record/system/report-template'

import { BasicTable, useTable } from '/@/components/Table'
import Edit from './popup/edit.vue'
import AddSelect from './popup/add-select.vue'
import { useModal } from '/@/components/Modal'
import { onMounted, ref } from 'vue'
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
  FilterFilled
} from '@ant-design/icons-vue'
import {findAllAcctandard, findAllAcctandardList} from "/@/api/accstandard/accstandard";
import {Select as ASelect,Input as AInput,List as AList,Row as ARow,Col as ACol ,Card as ACard,Popover as APopover,Button as AButton,Tag as ATag } from 'ant-design-vue'

const AListItem=AList.Item
const AInputSearch=AInput.Search
const ASelectOption=ASelect.Option

//打开编辑页
function openEditPage(){
  let thisInstance:any={}
  let thisOpen:any=():any=>{}
  return {
    register({instance,open}){
      thisInstance=instance
      thisOpen=open
    },
    open:(params)=>{
      thisOpen(params)
    }
  }
}
//给编辑页设置注册事件
const {register:registerEdit,open :openEditPage2}=openEditPage()
const CrudApi = {
  list: findByReportName,
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
      ellipsis: true
    },
    {
      title: '会计准则',
      dataIndex: 'accStandard',
      ellipsis: true,
      slots: { customRender: 'accStandard' }
    },
    {
      title: '标题名称',
      dataIndex: 'titleName',
      ellipsis: true
    },
    {
      title: '是否启用',
      dataIndex: 'flag',
      ellipsis: true,
      slots: { customRender: 'flag' }
    },
    {
      title: '是否系统模板',
      dataIndex: 'sysFlag',
      ellipsis: true,
      slots: { customRender: 'sysFlag' }
    }
  ],
  editData: {
    id: '',
    reportName: '',
    templateName: '',
    accStandard: '',
    kemuTemplateId: '',
    titleName: '',
    flag: '',
    menu1: '',
    menu2: '',
    menu3: '',
    menu4: '',
    menu5: '',
    sysFlag: ''
  }
}
// 这是示例组件
const [registerTable, { reload }] = useTable({
  api: CrudApi.list,
  columns: CrudApi.columns,
  bordered: true,
  showIndexColumn: false,
  actionColumn: {
    width: 160,
    title: '操作',
    dataIndex: 'action',
    slots: { customRender: 'action' }
  },
  searchInfo: {
    reportName: 'lrb'
  }
})

const accStandardList:any = ref({})
async function reloadAccStandard(){
  // 会计准则list
  const res = await findAllAcctandardList()
  accStandardList.value = res
}
function formatAccStandard(accStandard:any){
  let str = accStandard
  accStandardList.value.forEach(
    function (item:any) {
    if (item.uniqueAccStandard == accStandard){
      // console.log(item)
      str = item.accStandardName
    }
  })
  return str
}

const [registerAddSelectPage, { openModal: openAddSelectPage }] = useModal()
// const [registerEditPage, { openModal: openEditPage }] = useModal()
/*setTimeout(()=>{
  openEditPage2({
    data:{abc:1,dd:2} ,
    ok(){

    },
    cancel(){

    }
  })
},3000)*/
const val = {
  id: '',
  reportName: '',
  templateName: '',
  accStandard: '',
  kemuTemplateId: '',
  titleName: '',
  flag: '',
  menu1: '',
  menu2: '',
  menu3: '',
  menu4: '',
  menu5: '',
  sysFlag: ''
}
const condClick = (data:any, index:any, e:any) => {
  if (e.target.cellIndex == 1) {
    /*openEditPage(true, {
      data: data
    })*/
    openEditPage2({
      data: data
    })
  }
}
const openAddPage = () => {
  /*openEditPage(true, {
    data: val
  })*/
  // showEdit.value=true
  /*openEditPage2({
    data: val
  })*/
  openAddSelectPage(true, {
    data: val
  })
}
const addModelPage = (data) => {
  openEditPage2({
    data: data
  })
}
const openEdit = (data:any) => {
  /*openEditPage(true, {
    data: data
  })*/
  // showEdit.value=true
  openEditPage2({
    data:data ,
    ok(){

    },
    cancel(){

    }
  })
}
const del = async(data:any) => {
  await deleteTemplate(data)
  await reloadCurrentPage()
  alert('删除成功！')
  reload()
}
const activeKey = ref(false)

const cardList:any = ref({})
async function reloadCurrentPage() {
  const res = await findByReportNameAndFlag("lrb")
  cardList.value = res.items
  // console.log(cardList.value)
}
onMounted(async() => {
  await reloadAccStandard()
  await reloadCurrentPage()
})
async function saveData(data:any) {
  await saveTemplate(data)
  await reloadCurrentPage()
  reload()
}
async function editFlagData(data:any) {
  await editFlag(data)
  await reloadCurrentPage()
  reload()
}

function onSearch(){}
</script>
<style src="../../../../assets/styles/global-menu-index-block.less" lang="less" scoped></style><style lang='less' scoped ></style>
<style src="../../../../assets/styles/global-menu-index.less" lang="less" scoped></style><style lang='less' scoped ></style>
<style scoped lang="less">
:deep(.ant-card-body) {
  padding: 16px;
  border-left: 2px solid rgb(1, 143, 251);
  box-shadow: rgb(72 113 140) -3px 1px 7px -1px;
}
</style>

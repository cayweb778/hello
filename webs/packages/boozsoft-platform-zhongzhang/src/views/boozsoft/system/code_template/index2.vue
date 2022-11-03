<template>
  <div>
    <div class="app-container">
      <div class="app-container-head">
        <div class="container-head-title" style="margin-left: 20px">
          <b class="noneSpan"><SettingOutlined style="font-size: 26px;" />&nbsp;&nbsp;会计科目导入模板</b>
        </div>
        <div class="ant-btn-group" style="float: right">
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
            @click="openAddPage()"
          ><span>退出</span></button>
        </div>
      </div>
      <div class="app-container-neck">
        <div style="float: right; position: relative">
          <!-- 搜索 -->
          <a-input-search
            placeholder=""
            style="width: 200px; border-radius: 4px"
            @search="onSearch"
          />
        </div>
      </div>
      <Edit
        @save="saveData"
        @closeOk="reload(),clearSelectedRowKeys()"
        @register="registerEditPage"
      />

    </div>
    <div class="app-container">
      <div
        :class="cardList"
      >
        <div :class="`${cardList}__content`">
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
                        <div style="float: right; border: 1px solid rgb(1, 143, 251);border-radius: 10px;padding: 0 10px; margin-top: -5px;">
                          {{index+1}}
                        </div>
                        <div style="font-size: 24px;margin-top: 20px;">
                          <a style="cursor: pointer;" @click="openLook(item,false)">{{ item.tname}}</a>
                        </div>
                        <div style="float: right;margin-top: -20px;">
                          <a-popover placement="bottom" v-if="item.ttype!='0'">
                            <a-button style="padding: 0px 4px; height: 20px;margin-right: 10px;">
                              <EllipsisOutlined />
                            </a-button>
                            <template #content>
                              <p style="cursor: pointer" class="p_specifics" @click="openEdit(item)"><FormOutlined /> 修改</p>
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
import { SettingOutlined,EllipsisOutlined } from '@ant-design/icons-vue'
import {
  findAll,
  findByIdDel,
  save as saveCodeTemplate,
  save
} from '/@/api/record/system/code-import-template'
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
import { useModal } from '/@/components/Modal'
import { onMounted, ref } from 'vue'
import {useMessage} from "/@/hooks/web/useMessage";

const { createErrorModal,createConfirm } = useMessage()
const CrudApi = {
  list: findAll,
  columns: [
    {
      title: 'ID',
      dataIndex: 'id',
      defaultHidden: true,
      ellipsis: true
    },
    {
      title: '模板名称',
      dataIndex: 'tname',
      ellipsis: true,align:'left',
      slots: { customRender: 'tname' }
    },
    {
      title: '来源软件版本',
      dataIndex: 'remark',align:'left',
      ellipsis: true
    },
    {
      title: '类型',
      dataIndex: 'ttype',
      ellipsis: true,width: 80,
      slots: { customRender: 'ttype' }
    },
    {
      title: '状态',
      dataIndex: 'flag',
      ellipsis: true,  width: 80,
      slots: { customRender: 'flag' }
    }
  ],
}

// 这是示例组件
const rowSelection = {
  getCheckboxProps: (record) => ({
    disabled: record.ttype === 0
  }),
};
const [registerTable, { reload,getSelectRows,clearSelectedRowKeys }] = useTable({
  api: CrudApi.list,
  columns: CrudApi.columns,
  rowSelection: { type: 'checkbox' ,getCheckboxProps:rowSelection.getCheckboxProps},
  bordered: true,
  showIndexColumn: false,
})
const [registerEditPage, { openModal: openEditPage }] = useModal()

async function onSearch(data) {
  let temp= await findAll()
  cardList.value=data!==''?temp.filter(a=>a.tname.indexOf(data)!=-1):temp
}
const openAddPage = () => {
  openEditPage(true, {
    data: '',
    isEdit:true,
    type:'add'
  })
}

const openLook = (data) => {
  openEditPage(true, {
    data: data,
    isEdit:false,
    type:'look'
  })
}

const openEdit = (data) => {
  openEditPage(true, {
    data: data,
    isEdit:true,
    type:'edit'
  })
}
const del = async(data) => {
  createConfirm({
    iconType: 'warning',
    title: '提示',
    content: '确定删除吗?',
    onOk: async () => {
      let map={ id:data.id.join(',') }
      await findByIdDel(map)
      reloadCurrentPage()
    }
  })
}

onMounted(async () => {
 await reloadCurrentPage()
})

const cardList:any = ref({})
async function reloadCurrentPage() {
  cardList.value= await findAll()
  if(cardList.value.length>0){
    let a=cardList.value.filter(a=>a.ttype===0)
    if(a.length==0){
      let savetemplate = {
        tname: '系统标准模板',
        remark: '财税达NC',
        ttype: 0,
        flag: 1,
        tjson:
          '[{"titel":"类型","value":"类型"},{"titel":"科目编码","value":"科目编码"},{"titel":"科目名称","value":"科目名称"},' +
          '{"titel":"方向","value":"方向"},{"titel":"现金账","value":"现金账"},{"titel":"银行账","value":"银行账"},{"titel":"日记账","value":"日记账"},' +
          '{"titel":"外币名称","value":"外币名称"},{"titel":"计量单位","value":"计量单位"},{"titel":"辅助账类型","value":"辅助账类型"},' +
          '{"titel":"受控系统","value":"受控系统"},{"titel":"封存","value":"封存"}]',
      };
      await saveCodeTemplate(savetemplate)
      reloadCurrentPage()
    }
  }
}
async function saveData(data:any) {
  await save(data)
  reloadCurrentPage()
}

function createErrorPop(text) {
  createErrorModal({
    iconType: 'warning',
    title: '提示',
    content: text
  })
  return false
}
</script>
<style src="../../../../assets/styles/global-menu-index.less" lang="less" scoped></style>
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
  background: #b4c8e3 !important;
}
</style>

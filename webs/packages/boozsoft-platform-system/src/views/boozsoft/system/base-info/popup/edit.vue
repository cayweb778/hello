<template>
  <BasicModal
    width="900px"
    v-bind="$attrs"
    title="基础档案"
    @ok="handleOk()"
    @register="register"
  >
    <div
      class="nc-open-content"
    >
      <div class="open-content-up" style="height: 600px">

        <label>基础档案名称</label>
        <a-input v-model:value="itemConfig.base.baseName" placeholder="" />
        <label>基础档案表名</label>
        <a-input v-model:value="itemConfig.base.baseTable" placeholder="" />
        <br/><br/>

        <div style="border-bottom: 1px solid rgb(1, 129, 226)">
          栏目设置
          <a-button @click="createRow()"
                    style="padding: 0px 4px; height: 20px;float: right;background-color: rgb(1, 129, 226);color: #fff">
            <PlusOutlined/>
          </a-button>
        </div>
        <div v-for="(row,rowIndex) in itemConfig.base.table" style="margin-top: 5px;">
          <a-input v-model:value="row.cname" type="text" placeholder="字段名称" style="width: 30%"/>&nbsp;
          <a-input v-model:value="row.ctitle" type="text" placeholder="字段标题" style="width: 30%"/>&nbsp;
          <a-select v-model:value="row.isflag" placeholder="是否可选" style="width: 30%;">
            <a-select-option value="1">可选</a-select-option>
            <a-select-option value="0">不可选</a-select-option>
          </a-select>&nbsp;&nbsp;
          <DeleteOutlined @click="delRow(row,rowIndex)" style="color: rgb(1, 129, 226)"/>
        </div>

      </div>
    </div>

  </BasicModal>
</template>
<script setup="props, {content}" lang="ts">
import { ref, unref } from 'vue';
import {
  Input as AInput, Select as ASelect
} from "ant-design-vue";
const ASelectOption = ASelect.Option
const AInputSearch = AInput.Search
import { BasicModal, useModalInner } from '/@/components/Modal'
import {
  PlusOutlined,
  DeleteOutlined
} from '@ant-design/icons-vue'
import baseInfoItemConfigModelHelper
  from "./helper/baseInfoItemConfigModelHelper"
import {columnFindTable} from "/@/api/base-info/base-info";
import {deleteColumn} from '/@/api/record/system/baseInfo'

const emit=defineEmits(['register'])

async function handleOk() {
  emit('save', unref(itemConfig.value.base))
  closeModal()
}

const {createBaseRow, createBaseInfoItemConfig} = baseInfoItemConfigModelHelper()
const itemConfig:any = ref(createBaseInfoItemConfig())
const [register, {closeModal}] = useModalInner((data) => {
  //给头部赋值
  itemConfig.value.base.id = data.data.id
  itemConfig.value.base.baseName = data.data.baseName
  itemConfig.value.base.baseTable = data.data.baseTable
  //给栏目赋值
  if (data.data.baseTable!=null&&data.data.baseTable!='') {
    columnFindTable(data.data.baseTable)
      .then(list => {
        itemConfig.value.base.table = list
        //如果没有值默认给一行
        if (!list){
          createRow()
        }
    })
  } else {
    itemConfig.value.base.table=[]
    createRow()
  }
})
//添加行
function createRow() {
  itemConfig.value.base.table.push(createBaseRow())
}
//删除行
function delRow(row:any ,rowIndex:any) {
  itemConfig.value.base.table.splice(rowIndex, 1);
  //数据库删除
  deleteColumn(row.id)
}

</script>
<style lang="less" scoped>
.vben-basic-title {
  color: rgb(1, 129, 226) !important;
}

.ant-modal-body {
  padding: 0px;
  border: 1px solid rgb(1, 129, 226);
  border-left: none;
  border-right: none;
}

.nc-open-content {
  input {
    width: 35%;
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
  }

  .ant-input:focus {
    box-shadow: none;
  }

  .ant-select-selector {
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
  }

  label {
    text-align: right;
    width: 110px;
    display: inline-block;
    padding: 5px 10px;
  }

  .open-content-down {
    margin-top: 5%;

    .ant-tabs-tab-active::before {
      position: absolute;
      top: 0px;
      left: 0;
      width: 100%;
      border-top: 2px solid transparent;
      border-radius: 2px 2px 0 0;
      transition: all 0.3s;
      content: '';
      pointer-events: none;
      background-color: rgb(1, 143, 251);
    }
  }
}
</style>

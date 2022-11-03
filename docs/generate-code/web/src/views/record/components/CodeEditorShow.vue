<template>
  <div
      class="codeEditor"
      v-if="editorData">
    <!--  {{arr3}}-->
    <div v-for=" item in arr3">
      <span>{{ item }}</span>
    </div>
    <div style="margin-top:20px">
    </div>
    <div style="position: absolute;right:10px">
      <!--      [数据库模式]-->
      <Popover v-if="false">
        <span style="cursor: pointer">[数据库模式]</span>
        <template #content>
          <div>生成结构不可变基础档案（仅生成sql）</div>
        </template>
      </Popover>

      <Popover v-else>
        <span style="cursor: pointer">[代码模式]</span>
        <template #content>
          <div>生成代码</div>
        </template>
      </Popover>
    </div>
    <div style="margin-top:20px">
      输入label名:
      <span style="padding-top:10px">
      </span>
      <Input style="margin-left:-4px;width:100px;border:none;border-bottom: solid 1px black;"
             v-model:value="editorData.label" @input="emit('labelInput',$event)"/>
    </div>

    <div style="margin-top:20px">
      输入档案名:
      <span v-if="editorData.type==1" style="padding-top:10px">
        GROUP
      </span>
      <span v-if="editorData.type==2" style="padding-top:10px">
        ORIGIN
      </span>
      <span v-if="editorData.type==3" style="padding-top:10px">
        ACCOUNT
      </span>
      <Input style="margin-left:-4px;width:100px;border:none;border-bottom: solid 1px black;"
             v-model:value="editorData.recordName"/>
      <Button @click="getTableProperties(info)">检查</Button>
    </div>
    <!--    <Tabs style="margin-top:20px" @change="tabsChange">-->
    <!--      <TabPane v-for="it in [{name:'新增表'},{name:'已有表'}]" :key="it.name" :tab="it.name"></TabPane>-->
    <!--    </Tabs>-->

    <div style="width:95%;margin:20px auto 20px;color:white;border:solid 1px gray">
      <div style="margin-bottom:10px;background:gray;">属性设置
        <!--        [<span style="color:blue">数据库存在该表</span>]-->
      </div>
<!--      id int<br>-->
      <div style="width:95%;box-sizing:border-box;margin:0 auto">
        <div style="color:black;background-color: #F0EBCC;;display: flex;margin:0 auto;">
          <div style="width:25%;border:solid 1px gray">字段</div>
          <div style="width:25%;border:solid 1px gray">名字</div>
          <div style="width:25%;border:solid 1px gray">描述</div>
          <div style="width:25%;border:solid 1px gray">类型</div>
        </div>
        <div  >
          <div v-for="it in editorData.tableInfo" style="display: flex;color:black" >
            <div style="width:25%;border:solid 1px gray">{{ it.field }}</div>
            <div style="width:25%;border:solid 1px gray">{{ it.label }}</div>
            <div style="width:25%;border:solid 1px gray">{{ it.des }}</div>
            <div style="width:25%;border:solid 1px gray">String</div>
            <br>
          </div>
        </div>
      </div>

      <div>
      </div>
    </div>
  </div>
</template>
<script setup>
import {computed, inject, ref} from 'vue'
import {Select, Button, Input, Tabs, Popover} from 'ant-design-vue'

const props = defineProps(['modelValue', 'readonly'])
const editorData = computed(() => props.modelValue)

console.log(editorData)
const TabPane = Tabs.TabPane


const emit = defineEmits(['labelInput'])
// const genCodeInfo = inject('genCodeInfo')

import {makeCode, getTablePropertiesApi, getDefTablePropertiesApi} from '../index';

const arr3 = ref([])

async function getDefTableProperties(info2) {
  const result = (await getDefTablePropertiesApi())
  arr3.value = [...new Set(result.map(item => item.recordLabel))]
  console.log(result)
  if (result.b.length != 0) {
    editorData.value.tableInfo = {
      type: 'defTableDist',
      info: result.b.map(it => {
        console.log(it)

        // colDes: "无描述信息"
        // colKey: "id"
        // colLabel: "未设置"
        // colType: "int4"
        // createby: "木子桉易洋"
        // createdate: "2021-09-17"
        // id: 1
        // recordKey: "SysLogger"
        // recordLabel: "日志档案"
        // recordType: "1"
        return {
          ...it,

          field: it.colKey,
          des: it.colDes,
          label: it.colLabel
        }
      })
    }
    return
  }
  if (result.a.length != 0) {

    editorData.value.tableInfo = {
      type: 'existTable',
      info: result.a.map(it => {
        return {
          ...it,
          des: '无描述信息',
          label: '未设置'
        }
      })
    }
    return
  }
  console.log(editorData.value.tableInfo, 222)
}

// getDefTableProperties()


async function getTableProperties(info2) {
  const result = (await getTablePropertiesApi(info2))

  if (result.b.length != 0) {
    editorData.value.tableInfo = {
      type: 'defTableDist',
      info: result.b.map(it => {
        console.log(it)

        // colDes: "无描述信息"
        // colKey: "id"
        // colLabel: "未设置"
        // colType: "int4"
        // createby: "木子桉易洋"
        // createdate: "2021-09-17"
        // id: 1
        // recordKey: "SysLogger"
        // recordLabel: "日志档案"
        // recordType: "1"
        return {
          ...it,

          field: it.colKey,
          des: it.colDes,
          label: it.colLabel
        }
      })
    }
    return
  }
  if (result.a.length != 0) {

    editorData.value.tableInfo = {
      type: 'existTable',
      info: result.a.map(it => {
        return {
          ...it,
          des: '无描述信息',
          label: '未设置'
        }
      })
    }
    return
  }
  console.log(editorData.value.tableInfo)
}

const showAddTable = ref(true)
console.log(import.meta.env)


function tabsChange(e) {
  if (e === '新增表') {
    showAddTable.value = true
  } else {
    getTableProperties(editorData.value)
    showAddTable.value = false
  }
}

function abc(e) {
  console.log(e)
}

function addRow() {
  editorData.value.tableInfo.push({
    des: '无描述信息',
    label: '未设置'
  })
}
</script>

<style scoped>
.codeEditor {

  background-color:rgb(255, 254, 249) !important;
  width: 620px;
  height: 100%;

  box-shadow: #134872 0px 0px 5px 0px;
  overflow-y: auto;
  text-align: center;
  position: relative;
}
</style>
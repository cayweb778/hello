<template>
  <div v-if="props.readonly==null">
    <div v-if="editorData" class="codeEditor">
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
        <Input placeholder="输入label名" style="margin-left:-4px;width:100px;border:none;border-bottom: solid 1px black;"
               v-model:value="editorData.label" @input="emit('labelInput',$event)"/>
      </div>

      <div style="margin-top:20px">

        <Input :placeholder="'输入档案名: '+getTypeName(editorData.type)"
               @input="recodeKeyInput($event,editorData)"
               style="margin-left:-4px;width:160px;border:none;border-bottom: solid 1px black;"
               v-model:value="editorData.recordName"/>
        <Button @click="getTableProperties(info)" style="position:fixed;margin-left:10px">检查</Button>
      </div>
      <!--    <Tabs style="margin-top:20px" @change="tabsChange">-->
      <!--      <TabPane v-for="it in [{name:'新增表'},{name:'已有表'}]" :key="it.name" :tab="it.name"></TabPane>-->
      <!--    </Tabs>-->

      <div style="width:95%;margin:20px auto 20px;color:padding-top:20px;white;border:solid 1px gray;height:550px;overflow-y: auto">
        <div style="background:gray;margin-bottom:10px;">属性设置
          <!--        [<span style="color:blue">数据库存在该表</span>]-->
        </div>
<!--        id int<br>-->
        <div style="width:95%;box-sizing:border-box;margin:0 auto">
          <div style="color:black;background-color: #F0EBCC;;display: flex;margin:0 auto;">
            <div style="width:25%;border:solid 1px gray">字段</div>
            <div style="width:25%;border:solid 1px gray">名字</div>
            <div style="width:25%;border:solid 1px gray">描述</div>
            <div style="width:25%;border:solid 1px gray">类型</div>
          </div>
          <div v-for="it in editorData.tableInfo" style="color:black;display: flex;">
            <div style="width:25%;border:solid 1px gray"> <Input v-model:value="it.field" @input="it.field=it.field.slice(0,12).replaceAll(/[\u4e00-\u9fa5]/g,'')" style=""/></div>
            <div style="width:25%;border:solid 1px gray"> <Input v-model:value="it.label"  @input="it.label=it.label.slice(0,12)"  style=""/></div>
            <div style="width:25%;border:solid 1px gray"> <Input v-model:value="it.des"  @input="it.des=it.des.slice(0,30)"  style="color:gray;"/></div>
            <div style="width:25%;border:solid 1px gray">  String</div>
            <br>
          </div>
        </div>

      </div>
      <div>
        <Button @click="addRow">新增字段</Button>
      </div>
    </div>
  </div>

  <div v-else>
    <CodeEditorShow v-model="props.modelValue" @labelInput="$emit('labelInput',$event)"></CodeEditorShow>
  </div>
</template>
<script setup>
import {computed, inject, ref} from 'vue'
import {Select, Button, Input, Tabs, Popover} from 'ant-design-vue'
import CodeEditorShow from './CodeEditorShow.vue'

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

function getTypeName(type){
  if(type==1){
    return 'GROUP'
  }else if(type==2){
    return 'ORIGIN'
  }else if(type==3){
    return 'ACCOUNT'
  }
}

function recodeKeyInput(e,editorData){
  const typeName=getTypeName(editorData.type)
  if(editorData.recordName.slice(0,typeName.length)!=typeName){
    console.log(editorData.recordName)
    editorData.recordName=getTypeName(editorData.type)+editorData.recordName.replaceAll(typeName,'').replaceAll(typeName.slice(0,typeName.length-1),'')
  }
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
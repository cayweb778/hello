<template>
  <div class="gen-box">
    <div class="gen-popover-container">
      <div style="display: flex;height:calc(100% - 50px);">
        <div style="width:150px;height: 100%;background-color: #5C7AEA;;color:white;border-right:solid 1px black">
          <ul>
            <li style="background-color: #3D56B2;cursor: context-menu;box-shadow:rgb(49 49 49) 0px 2px 5px 0px">类型</li>
            <li v-for="it in genCodeInfo.data" :style="{background:it.enable?'rgb(0 0 0 / 0.4)':''}" @click="platformClick(it)"
                style="cursor: pointer">
              {{ it.label }}
            </li>
          </ul>
        </div>
        <div style="width:150px;height: 100%;background-color: #5089C6;color:white">
          <ul>
          <li style="background-color: #3D56B2;cursor: context-menu;box-shadow:rgb(49 49 49) 0px 2px 5px 0px">字典档案</li>
            <template v-for="item2 in enableRecords">
              <li @click="useRecord(item2)" :style="{background:item2.enable?'rgb(0 0 0 / 0.4)':''}" style="cursor: pointer">
                {{ item2.label }}
                <span v-if="item2.type==2" style="color:#4dff4d">[New]</span>
              </li>
            </template>
            <!--            <li @click="createNewRecord()" style="background: white">新增档案</li>-->
          </ul>
          <!--          <ul v-if="zuzhi">-->
          <!--            <li>组织</li>-->
          <!--            <li @click="createNewRecord()">新增档案</li>-->
          <!--          </ul>-->
          <!--          <ul v-if="zhangtao">-->
          <!--            <li>账套</li>-->
          <!--            <li @click="createNewRecord()">新增档案</li>-->
          <!--          </ul>-->
        </div>
        <CodeEditor v-if="enableRecord!=null" :readonly="enableRecord.type==2?null:''" v-model="enableRecord.editorData"
                    @labelInput="labelInputFun"></CodeEditor>
      </div>
      <div style="padding-top:10px">
        <Button @click="aaabbb" type="primary" style="padding:0 40px">生成所有</Button>
        <div style="position:absolute;bottom:10px;right:10px;cursor: pointer">
          <Popover>
            生成说明@
            <template #content>
              <div style="color:green">
                生成表（可选）<br>
                生成Entity(实体类)<br>
                生成Rest(后端接口)<br>
                生成Service（后端接口服务）<br>
                生成Repo（后端sql仓库）<br>
                生成Vo（实体类的VO对象）<br>
                生成前端api接口<br>
                生成页面文件（index.vue）<br>
                生成增删改查页面（包含分页功能）<br>
                生成打印功能<br>
                生成表格(excel)导出功能<br>
                生成回滚记录功能<br>
                生成日志功能<br>
                生成任务管理功能<br>
                生成账套切换功能<br>
              </div>
            </template>
          </Popover>

        </div>
        <div>

        </div>
      </div>
    </div>
  </div>
</template>
<script setup>
import CodeEditor from './components/CodeEditor.vue'
import {Select, Button, Input, Tabs, Popover} from 'ant-design-vue'
import {makeCode, getTablePropertiesApi, getDefTablePropertiesApi} from './index';
import {computed, onMounted, provide, ref} from "vue";

const jituan = ref(true)
const zhangtao = ref(false)
const zuzhi = ref(false)

const arr3 = ref([])
const recordData = ref([])

async function getDefTableProperties(info2) {
  const result = (await getDefTablePropertiesApi())
  arr3.value = [...new Set(result.map(item => item.recordLabel))]
  console.log(result)
  if (result.b.length != 0) {
    info.value.tableInfo = {
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
        // recordType: "1"
        // recordType: "1"
        // 组织档案 - 20账套
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

    info.value.tableInfo = {
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
  console.log(info.value.tableInfo, 222)
}

// getDefTableProperties()


function aaabbb() {
  const allNewRecord = genCodeInfo.value.data.flatMap(it => it.records).filter(it => it.type == 2).map(it => it.editorData).filter(it => it != null)
  allNewRecord.forEach(it => it.tableInfo = it.tableInfo.filter(it => it.field != null))
  makeCode({allNewRecord})
}

function jiTuan() {
  jituan.value = true
  zhangtao.value = false
  zuzhi.value = false
}

function zuZhi() {
  jituan.value = false
  zhangtao.value = false
  zuzhi.value = true
}

function zhangTao() {
  jituan.value = false
  zuzhi.value = false
  zhangtao.value = true
}

function createNewRecord() {
  return [
    {
      field: '',
      des: '',
      label: ''
    },
    {
      field: '',
      des: '',
      label: ''
    },
    {
      field: '',
      des: '',
      label: ''
    },
    {
      field: '',
      des: '',
      label: ''
    },
    {
      field: '',
      des: '',
      label: ''
    },
    {
      field: '',
      des: '',
      label: ''
    },
    {
      field: '',
      des: '',
      label: ''
    },
  ]
}


const genCodeInfo = ref({
  data: [
    {
      key: 'group',
      label: '集团',
      enable: true,
      records: []
    },
    {
      key: 'origin',
      label: '组织',
      enable: false,
      records: []
    },
    {
      key: 'account',
      label: '账套',
      enable: false,
      records: []
    },
  ]
})


const arr4 = ref([])

const enableRecords = computed(() => {
  const currentEnablePaltform = genCodeInfo.value.data.filter(it => it.enable)[0]
  return currentEnablePaltform.records
})

const allRecord = ref({})
onMounted(async () => {
  const result = (await getDefTablePropertiesApi())
  const aaa = [...new Set(result.map(item => item))]
  const record2 = {}
  aaa.forEach(it => {
    if (record2[it.recordType] == null) {
      record2[it.recordType] = {}
      record2[it.recordType].arr = []
    }
    const arr4 = record2[it.recordType].arr.filter(it2 => it2.key == it.recordKey)
    if (arr4.length == 0) {
      record2[it.recordType].arr.push({
        key: it.recordKey,
        label: it.recordLabel,
        data: []
      })
    } else {
      arr4[0].data.push(it)
    }
  })
  allRecord.value = record2
  const newRow = createNewRecord()
  genCodeInfo.value.data.forEach(it => {
    if (it.key == 'group') {
      it.records = record2['1']?.arr

      it.records == null && (it.records = [])
      it.records.push({
        key: 'new',
        label: '新增档案',
        type: 2,
        enable: true,
        data: newRow
      })
      useRecord(enableRecords.value[enableRecords.value.length - 1])
    }
    if (it.key == 'origin') {
      it.records = record2['2']?.arr
      it.records == null && (it.records = [])
      it.records.push({
        key: 'new',
        label: '新增档案',
        type: 2,
        enable: true,
        data: newRow
      })
    }
    if (it.key == 'account') {
      it.records = record2['3']?.arr
      it.records == null && (it.records = [])
      it.records.push({
        key: 'new',
        label: '新增档案',
        type: 2,
        enable: true,
        data: newRow
      })
    }
  })

  // const recrod3 = []
  // Object.keys(record2).forEach(it => {
  //   recrod3.push({
  //     key: it,
  //     label: record2[it].recordLabel,
  //     data: record2[it].data,
  //     enable:true
  //   })
  // })
})

async function platformClick(it) {
  genCodeInfo.value.data.forEach(
      it => it.enable = false
  )
  it.enable = true
  useRecord(enableRecords.value[enableRecords.value.length - 1])
}

const enablePlatform = computed(() => {
  return genCodeInfo.value.data.filter(it => it.enable)[0]
})

function useRecord(item2) {
  if(item2.label=='新增档案'){
    item2.type=2
  }
  let type = null
  if (enablePlatform.value.key == 'group') {
    type = 1
  } else if (enablePlatform.value.key == 'origin') {
    type = 2
  } else if (enablePlatform.value.key == 'account') {
    type = 3
  }
  if (item2.editorData == null) {
    item2.editorData = {
      type,
      recordName: '',
      tableInfo: item2.data.map(it => {
        return {
          field: it.colKey,
          des: it.colDes,
          label: it.colLabel
        }
      }),
      label: ''
    }
  }

  genCodeInfo.value.data.forEach(it => {
    it.records.forEach(it2 => it2.enable = false)
  })
  item2.enable = true
  // genCodeInfo.editorData.value.tableInfo.info = item2.data.map(it => {
  //   return {
  //     field: it.colKey,
  //     des: it.colDes,
  //     label: it.colLabel
  //   }
  // })
}

const enableRecord = computed(() => {
  return enableRecords.value.filter(it => it.enable)[0]
})

function labelInputFun(e) {

  if (enableRecords.value[enableRecords.value.length - 1].label != '新增档案') {
    const newRow = createNewRecord()
    enableRecords.value.push({
      key: 'new',
      label: '新增档案',
      data: newRow
    })
  }
  enableRecord.value.label = e.target.value
  enableRecord.value.type = 2
}

provide('genCodeInfo', genCodeInfo)
</script>

<style>
.gen-popover-container {
  background-color: #0F52BA;
  border-radius: 10px;
  padding:10px;
  top: calc((100vh - 800px) / 2);
  left: calc((100% - 800px) / 2);;
  width: 900px;

  box-shadow: #0F52BA 0px 0px 20px;
  border:solid 1px #5c7aea;
  height: 800px;
  overflow-y: auto;
  text-align: center;
  position: relative

}

.gen-box {

  background-color: #193498;
  /*background-color: rgb(20, 74, 116);*/
  background-image: url('/assets/images/texture.png');
  position: fixed;
  width: 100%;
  height: 100%
}
</style>
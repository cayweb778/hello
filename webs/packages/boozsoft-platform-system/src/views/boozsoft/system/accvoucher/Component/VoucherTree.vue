<template>
  <div class="voucher-list-tree">
    <Tabs type="card" size="small" :tabBarGutter="0" v-model:activeKey="activeKey">
      <TabPane key="1" tab="按月显示" :style="activeKey=='1'?{height: (windowHeight-285)+'px'}:{}">
        <div style="text-align: center;height: 50px;background-color: #f1f1f1"></div>
        <BasicTree
          title=""
          :tree-data="treeData"
          :replace-fields="{ key: 'value', title: 'title' }"
          v-model:checkedKeys="checkedKeys"
          v-model:expandedKeys="expandedKeys"
          :height="650"
          checkable
          :selectable="false"
          @check="handleSelect"
        />
      </TabPane>
      <TabPane key="2" tab="按天显示" :style="activeKey=='2'?{height: (windowHeight-285)+'px'}:{}">
        <div style="text-align: center;height: 50px;line-height: 50px;background-color: #f1f1f1">
<!--          <span>{{ compParameter.year }}年</span>-->
          <Select style="width: 74px;text-align: center;" v-model:value="compParameter.year"
                  @change="monthChange">
            <SelectOption v-for="i in yearList" :value="i">{{ i }}</SelectOption>
          </Select>年
          <Select style="width: 56px;text-align: center;" v-model:value="compParameter.dayMonth"
                  @change="monthChange">
            <SelectOption v-for="i in showMonthList" :value="i">{{ i }}</SelectOption>
          </Select>月
        </div>
        <BasicTree
          title=""
          v-model:checkedKeys="checkedKeys2"
          v-model:expandedKeys="expandedKeys2"
          :replace-fields="{ key: 'value', title: 'title' }"
          checkable
          :height="650"
          :selectable="false"
          :tree-data="treeData2"
          @check="handleSelect"
        />
      </TabPane>
    </Tabs>
  </div>
</template>
<script setup lang="ts">
import {onMounted, ref, watch} from "vue";
import {BasicTree} from '/@/components/Tree';
import {Select, Tabs} from 'ant-design-vue';
import {hasBlank} from "/@/api/task-api/tast-bus-api";
import {findAllAccvoucher, findAllAccvoucherTree} from "/@/api/record/system/accvoucher";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {JsonTool} from "/@/api/task-api/tools/universal-tools";

const TabPane = Tabs.TabPane
const SelectOption = Select.Option
const emit = defineEmits(['select'])
const props = defineProps(['treeInterval', 'database'])

const compParameter = ref({
  year: '2021',
  dayMonth: '01',
})

const activeKey = ref('1')
const treeData: any = ref([])
const checkedKeys: any = ref([])
const expandedKeys: any = ref([])

const treeData2: any = ref([])
const checkedKeys2: any = ref([])
const expandedKeys2: any = ref([])
const windowHeight = (window.innerHeight)
const yearList = ref([])
const monthList: any = ['01', '02', '03', '04', '05', '06', '07', '08', '09', '10', '11', '12']
const showMonthList = ref([])
let reQ = false; // 是否再次查询
watch(() => props.treeInterval, async () => {
  if (activeKey.value == '2'){activeKey.value = '1';reQ = true}
  function generateAvailablePeriod() {
    let sM = props.treeInterval[0].substring(5, 7)
    let sY = props.treeInterval[0].substring(0, 4)
    let eM = props.treeInterval[1].substring(5, 7)
    let eY = props.treeInterval[1].substring(0, 4)
    let arr = []
    for (let i = parseInt(sM); i <= parseInt(eM); i++) {
      let tM = i > 9 ? i + '' : '0' + i
      if (monthList.indexOf(tM) != -1)
        arr.push(tM)
    }
    return arr;
  }
  function generateAnnualInterval(arr){
    let start =  parseInt(arr[0].substring(0,4))
    let end =  parseInt(arr[1].substring(0,4))
    let list = []
    while (start<=end){
      list.push(start)
      start++
    }
    return list
  }
  if (null != props.treeInterval && props.treeInterval.length > 0) {
    let arr = generateAvailablePeriod();
    showMonthList.value = arr
    yearList.value = generateAnnualInterval(JsonTool.parseProxy(props.treeInterval))
    compParameter.value.year = props.treeInterval[0].substring(0, 4)
    compParameter.value.dayMonth = arr[arr.length - 1]
    if (activeKey.value == '2') {
      fetch2()
    } else {
      fetch()
    }
  }
})
watch(() => activeKey.value, async () => {
  if (activeKey.value == '2') await fetch2()
  checkedKeys.value = []
  checkedKeys2.value = []
  if (!reQ){
    handleSelect([], [])
  }else {
    reQ = false
  }

})

async function fetch() {
  treeData.value = generateAnnualIntervalTree()
  expandedKeys.value = [compParameter.value.year]
  //emit('select', {uniqueCode: checks[0], deptList: deptTree})
}

async function fetch2() {
    let res = await useRouteApi(findAllAccvoucherTree, {schemaName: props.database})({yearMonth: `${compParameter.value.year}-${compParameter.value.dayMonth}`})
  let keys = Object.keys(res)
  let list = []
  let exps = []
  for (let i = 0;i<keys.length;i++) {
    let arr = []
    let keys2 = Object.keys(res[keys[i]])
      for (let j =0 ;j < keys2.length;j++) {
      let dates =  keys2[j].split('-')
      let values = res[keys[i]][keys2[j]]
      let els = []
        for (let x =0 ;x< values.length;x++) {
        els.push({value: `${keys2[j]}-${values[x]}`, title: `${values[x]}号`,})
      }
      let newArr = els.sort((a, b) => parseInt(a.title.substring(0, a.title.length - 1)) - parseInt(b.title.substring(0, b.title.length - 1)))
      arr.push({value: `${keys2[j]}`, title: `${dates[1]}月${dates[2]}日`, children: newArr})
      exps.push(keys2[j])
    }
    let newArr2 = arr.sort((a, b) => parseInt(a.value.substring(a.value.length - 2)) - parseInt(b.value.substring(b.value.length - 2)))
    list.push({value: keys[i], title: keys[i], children: newArr2,})
    exps.push(keys[i])
  }
  treeData2.value = list
  expandedKeys2.value = exps
  /*  selectedKeys2.value = ['j','01','02']
    expandedKeys2.value = ['j','01','02']*/
  //emit('select', {uniqueCode: checks[0], deptList: deptTree})
}

function handleSelect(keys: string, e: any) {
  let arr = []
  if (activeKey.value == '1') {
    keys.forEach(v => {
      if (v != compParameter.value.year)
        arr.push(`${compParameter.value.year}-${v}`)
    })
  } else {
    keys.forEach(v => {
      if (v.length > 10)
        arr.push(`${v}`)
    })
  }
  emit('select', {type: (activeKey.value == 1 ? 'month' : 'day'),monthVal: compParameter.value.dayMonth, treeCondition: arr})
}

const monthChange = (v) => {
  fetch2()
  handleSelect([], [])
}


const generateAnnualIntervalTree = () => {
  let deptTree = monthList.map(i => ({
    'value': i,
    'title': `${i}月`,
    'disabled': showMonthList.value.indexOf(i) == -1
  }))
 return yearList.value.map(y=>({
    value: y,
    title: `${y}年`,
    children: deptTree
  }))
}
</script>
<style lang="less" scoped>
.voucher-list-tree {
  display: inline-block;
  height: calc(100%);
  min-width: 181px;
  float: left;
  :deep(.ant-select-selector) {
    background-color: #f1f1f1;
    border: none;
    border-bottom: solid 1px rgb(191, 191, 191) !important;
  }

  :deep(.ant-tabs-nav) {
    margin-bottom: 0px;

    .ant-tabs-tab-active {
      background-color: #f1f1f1;
      border-top: 2px solid #0096c7;
      border-bottom: none;
    }
  }

  :deep(.ant-tabs-content) {
    padding: 0 10px 10px;
    background-color: #f1f1f1;

    .scroll-container {
      border: 2px groove #cac2c2;
      background-color: #f1f1f1;
      height: calc(100% - 60px) !important;
      .ant-tree-list{
         background-color: #f1f1f1 !important;
      }
    }
  }
}
</style>

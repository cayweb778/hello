<template>
  <div style="padding: 20px 10px;width: 300px;height: auto">
    辅助核算类型：<br/>
    <Select style="width: 100%"  mode="multiple" v-model:value="openParameter.cdfineValue"
            @change="cdfineRuleChange"
            :options="fzItemsShow.map(fz => ({ value: fz.key,label: fz.title }))"/><br/>
    <div v-for="key in openParameter.cdfineValue" style="line-height: 32px">
     <span> {{currName(key)}}：</span><Select style="width: 60%;float: right" v-model:value="openParameter.before[key]"
                     :options="currList(key).map(fz => ({ value: fz.key,label: fz.label }))"
                     placeholder="请选择内容"/><br/>
    </div>
    <div style="text-align: right;padding: 5px 0 0;">
      <span style="color: red">{{textContent || ''}}</span><Button type="primary" size="small" @click="setValue">确定</Button>
    </div>
  </div>
</template>

<script setup lang="ts">
import {computed, inject, onMounted, ref, unref, watch} from 'vue';
import {
  Select,Button
} from "ant-design-vue";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {findKeyLabelAll} from "/@/api/record/system/fuZhuHeSuan";
import {findAllFuzhuHesuanList} from "/@/api/record/system/fuzhu-hesuan";
import {onUpdated} from "vue";
import {hasBlank} from "/@/api/task-api/tast-bus-api";
const emits = defineEmits(['setValue'])
const props = defineProps(['allKemuList','entry'])
const pageParameter = inject('pageParameter')
const windowWidth = (document.documentElement.clientWidth - 70)
const windowHeight = (window.innerHeight - 290)
// 组件实例区
const openParameter = ref({
  before: {},
  cdfineValue: [],
})
const deptList = ref([])
const userList = ref([])
const custList = ref([])
const supList = ref([])
const proMaxList = ref([])
/*自定义辅助核算*/
const otherData = ref({})
const textContent = ref(null)
const cdfineRuleChange = async (arr) => {
  for (let i=0;i<arr.length;i++){
    let v = arr[0]
    if (v.indexOf('cdeptId') != -1) {
      if (deptList.value.length === 0) deptList.value = (await useRouteApi(findKeyLabelAll, {schemaName: pageParameter.dynamicTenantId})({
        require: 'fzDept',
        toTarget: 'false'
      }))[0]['list']
    } else if (v.indexOf('cpersonId') != -1) {
      if (userList.value.length === 0) userList.value = (await useRouteApi(findKeyLabelAll, {schemaName: pageParameter.dynamicTenantId})({
        require: 'fzEmp',
        toTarget: 'false'
      }))[0]['list']
    } else if (v.indexOf('ccusId') != -1) {
      if (custList.value.length === 0) custList.value = (await useRouteApi(findKeyLabelAll, {schemaName: pageParameter.dynamicTenantId})({
        require: 'fzCustom',
        toTarget: 'false'
      }))[0]['list']
    } else if (v.indexOf('csupId') != -1) {
      if (supList.value.length === 0) supList.value = (await useRouteApi(findKeyLabelAll, {schemaName: pageParameter.dynamicTenantId})({
        require: 'fzGys',
        toTarget: 'false'
      }))[0]['list']
    } else if (v.indexOf('projectId') != -1) {
      if (proMaxList.value.length === 0) proMaxList.value = (await useRouteApi(findKeyLabelAll, {schemaName: pageParameter.dynamicTenantId})({
        require: 'fzItemClass',
        toTarget: 'false'
      }))[0]['list']
    } else if (v.indexOf('cdfine') != -1) {
      if (otherData.value[v] == null || otherData.value[v].length === 0) {
        let str = parseInt(v.replace('cdfine', '')) + ''
        otherData.value[v] = (await useRouteApi(findKeyLabelAll, {schemaName: pageParameter.dynamicTenantId})({
          require: str,
          toTarget: 'false'
        }))[0]['list']
      }
    }
  }
  if (arr.length == 0) openParameter.value.before = {}
}
const fzItemsShow = ref([])
const fzItems = ref([{
  key: 'cdeptId',
  title: `部门`
}
  , {
    key: 'cpersonId',
    title: `员工`
  },
  {
    key: 'ccusId',
    title: `客户`
  },
  {
    key: 'csupId',
    title: `供应商`
  },
  {
    key: 'projectId',
    title: `项目`
  }])
const currList = (key) => {
  switch (key) {
    case ('cdeptId'):
      return deptList.value;
    case ('cpersonId'):
      return userList.value;
    case ('ccusId'):
      return custList.value;
    case ('csupId'):
      return supList.value;
    case ('projectId'):
      return proMaxList.value;
    default:
      return otherData[key] ||[];
  }
}
const matchKey = (key) => {
  switch (key) {
    case ('cdeptId'):
      return 'bdept';
    case ('cpersonId'):
      return 'bperson';
    case ('ccusId'):
      return 'bcus';
    case ('csupId'):
      return 'bsup';
    case ('projectId'):
      return 'bitem';
    default:
      return key;
  }
}
const currName = (key) => {
  return fzItems.value.filter(item=>item.key == key)[0]['title']
}
const initFzList = async () => {
  if (fzItems.value.length == 5) {
    let fzL = await useRouteApi(findAllFuzhuHesuanList, {schemaName: pageParameter.dynamicTenantId})({})
    if (fzL.length > 0) fzL.forEach(item => fzItems.value.push({
      key: 'cdfine' + item.cdfine,
      title: item.cname
    }))
  }
}
const setValue = () => {
  let keys = Object.values(openParameter.value.cdfineValue)
  let resilt = {
    name:'',
    value: ''
  }
  for (let i =0;i<keys.length;i++){
    let key = keys[i]
    let v = openParameter.value.before[key]
    if (hasBlank(v)){
      textContent.value = '请完善辅助核算内容！'
      return false;
    }else {
      textContent.value = ''
      resilt.value += `,[${key}:${v}]`
      resilt.name += `,[${currName(key)}:${currList(key).filter(item=>item.key == v)[0].label}]`
    }
  }
  if (!hasBlank(resilt.value)){
    resilt.value = resilt.value.substring(1,resilt.value.length)
    resilt.name = resilt.name.substring(1,resilt.name.length)
  }
  emits('setValue',resilt)
}
onMounted(async ()=>{
 await initFzList()
 await reloadFzList()
})
const accountSubjectNumChange = computed(()=>props.entry?.accountSubjectNum)
watch(accountSubjectNumChange, async (abc) => {
 await reloadFzList()
})
async function reloadFzList() {
  let currCode =  props.allKemuList.filter(item=>item.ccode == props.entry.accountSubjectNum.split('--')[0])[0]
  console.log(currCode)
  if (null != currCode) fzItemsShow.value =  fzItems.value.filter(item=>currCode[matchKey(item.key)] == '1')
}
</script>
<style lang="less" scoped="scoped">

</style>

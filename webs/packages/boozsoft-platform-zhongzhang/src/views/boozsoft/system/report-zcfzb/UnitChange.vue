<template>
  <div>
    <div style="display: inline-block;float: left;margin-left: 1%;font-size: 14px;">
      <AccountInfo
        @modify="modify"
        @register="registerInfo"
      />
      <label>{{pageParameter.ifUnit?'单位':'公司'}}代码：</label>
      <a-input
        placeholder=""
        v-model:value="pageParameter.companyCode"
        @keyup.enter="pageSearch2"
        @focus="dynamicallyMonitorChanges('close')"
        @blur="dynamicallyMonitorChanges('set')"
        style="width: 100px;border-radius: 4px"
      >
        <template #suffix> <SearchOutlined @click="openSelection"/></template>
      </a-input>
      &emsp;&emsp;{{pageParameter.ifUnit?'单位':'公司'}}简称：<span style="color: black;font-weight: bold">{{pageParameter.companyName}}</span>
    </div>
  </div>
</template>
<script setup lang="ts">
import {computed, onMounted, reactive, ref, unref, watch} from 'vue';
import {message, Input as AInput} from "ant-design-vue";
const AInputSearch = AInput.Search
import {getAdInfoDatas, getAllAccAuths} from "/@/api/record/system/financial-settings";
import AccountInfo from "/@/views/boozsoft/system/accvoucher/popup/AccountInfo.vue";
import {useUserStore} from "/@/store/modules/user";
const userStore = useUserStore();
import {SearchOutlined} from '@ant-design/icons-vue';
import {useModal} from "/@/components/Modal";
const emit = defineEmits(['reloadTable'])
// const { value, mode } = props.data;
const props=defineProps(['modelValue'])

const pageParameter = reactive({
  companyCode: '100',
  companyName: '湖北万亚软件技术有限公司',
  ifUnit: false,
  accountsYear: '2021'
})

// 默认账套编码
let defalutCompanyCodeValue = ''
// 切换后账套编码
const dynamicCompanyCodeValue = ref(defalutCompanyCodeValue)
// 动态数据库名称
const dynamicManipulateDbName = ref('')
// 已授权账套列表
const authorizedList = ref([])
// 账套授权详细信息
const authorizationDetails = ref([])
// 动态监听变化
const tempValue = ref(defalutCompanyCodeValue)

const dynamicallyMonitorChanges = async (type)=>{
  if (type == 'set'){
    if ((pageParameter.companyCode == '' || pageParameter.companyCode != '' ) && dynamicManipulateDbName.value == ''){
      pageParameter.companyCode = defalutCompanyCodeValue
    }else if (dynamicManipulateDbName.value != '' && pageParameter.companyCode == ''){
      pageParameter.companyCode =tempValue.value
    }
  }else {
    tempValue.value = pageParameter.companyCode
    pageParameter.companyCode = ''
  }
}

const switchDateBaseInfo = reactive({
  accId: '',
  year: '',
  baseName: '',
  coCode: '',
})
// 切换事件
const pageSearch2 = async ()=>{
    // 检查是否存在 查看是否拥有权限
    /*if ((defalutCompanyCodeValue == pageParameter.companyCode && dynamicManipulateDbName.value == '')
      || pageParameter.companyCode == temp.value  && dynamicManipulateDbName.value != '') return false*/
  if (pageParameter.companyCode == tempValue.value) return false
    let accObj = authorizedList.value.filter(item=>item.coCode == pageParameter.companyCode)
    if (accObj.length == 0){
      message.warn('代码不存在停止切换！')
      pageParameter.companyCode = defalutCompanyCodeValue
    }else if(accObj.length > 0){
      let iyear = pageParameter.accountsYear
      let authObj = authorizationDetails.value.filter(item=>accObj[0].accId == item.accId && item.iyear == iyear)
      if (authObj.length == 0){
        message.warn('无该代码'+iyear+'年度数据权限！')
        pageParameter.companyCode = defalutCompanyCodeValue
      }else {
        dynamicManipulateDbName.value = accObj[0].accId+'-'+iyear
        pageParameter.companyName = accObj[0].accNameCn

        switchDateBaseInfo.accId=accObj[0].accId
        switchDateBaseInfo.baseName=accObj[0].accNameCn
        switchDateBaseInfo.year=iyear
        switchDateBaseInfo.coCode=pageParameter.companyCode

        emit('reloadTable',unref(switchDateBaseInfo))
      }
    }
}
const filterAccListByAuth = (acclist,authList)=> {
  return acclist.filter(item=>{
    for(let i = 0;i < authList.length;i++){
      if (item.accId ==  authList[i].accId){
        return true
      }
    }
    return  false;
  })
}
onMounted(async () => {
  // 获取所有账套信息 并过滤已授权的账套
  let accList = await getAdInfoDatas()
  // 获取当前用户的权限根据用户信息汇率
  authorizationDetails.value = await getAllAccAuths({'userId': userStore.getUserInfo.id})
  authorizedList.value = filterAccListByAuth(accList,authorizationDetails.value )
  pageParameter.companyCode = props.modelValue.companyCode
  pageParameter.companyName = props.modelValue.companyName
  pageParameter.ifUnit = props.modelValue.ifUnit
  defalutCompanyCodeValue = props.modelValue.companyCode
})

const [registerInfo, { openModal: openInfoPageM }] = useModal()
const openSelection =()=>{
  openInfoPageM(true, {
    data: {accAuthList:authorizedList.value}
  })
}
const modify = (code)=>{
  if (pageParameter.companyCode != code) {
    pageParameter.companyCode = code
    pageSearch2()
  }
}
const totalChange=computed(()=>props.modelValue.total)
watch(totalChange, async (a)=>{
    if (a < 0){
      props.modelValue.total = 0
      let defaultObj = authorizedList.value.filter(item=>item.coCode == tempValue.value)[0]
      pageParameter.companyCode = defaultObj.coCode
      pageParameter.companyName = defaultObj.accNameCn
    }
  }
)

</script>
<style lang="less" scoped>

</style>

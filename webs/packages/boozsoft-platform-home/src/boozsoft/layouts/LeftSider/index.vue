<template></template>
<script setup>
import {computed, provide, ref} from 'vue';
import {usePlatformsStoreWidthOut} from '/@/store/modules/platforms';
// import {layoutsStore} from '../../../store/modules/layouts';
import {LogoutOutlined} from '@ant-design/icons-vue';
import SideBar from './sitebar/index.vue'
import MessagePop from './sitebar/messagePop.vue'
import OpenButtonBar from './sitebar/openbutton.vue'
import router from "/@/router";
import {useMessage} from "/@/hooks/web/useMessage";
import {Badge, Button, DatePicker} from 'ant-design-vue';
import {useUserStore} from "/@/store/modules/user";
import {useCompanyOperateStoreWidthOut} from "/@/store/modules/operate-company";
import {useModal} from "../../../components/Modal";
import dayjs from 'dayjs';
import {
  useAccountPickerCache
} from "../../../store/modules/boozsoft/components/AccountPicker/cache";

const layoutsStore = usePlatformsStoreWidthOut()


const loginDate = ref(useCompanyOperateStoreWidthOut().getLoginDate)
const {getShowPromissSiteBar, changeApp, changeNcDashBoard} = layoutsStore
const showOption = ref(false)
const showOpen = ref(false)
const windowHeight = (window.innerHeight)
const dateChange = async (v) => {
  showOpen.value = false
  await useCompanyOperateStoreWidthOut().commitLoginDate(v)
  loginDate.value = useCompanyOperateStoreWidthOut().getLoginDate
}
const btnCheck = (type) => {
  let theD = loginDate.value
  switch (type) {
    case '1':
      theD = dayjs().format('YYYY-MM-DD')
      break
    case '2':
      theD = dayjs().add(-1, 'day').format('YYYY-MM-DD')
      break
    case '3':
      theD = dayjs().add(-1, 'month').format('YYYY-MM-DD')
      break
    case '4':
      theD = dayjs().format('YYYY-MM-DD')
      break
    case '5':
      theD = dayjs().add(1, 'month').format('YYYY-MM-DD')
      break
  }
  dateChange(theD)
}

const changeOpenStatus = (b) => {
  showOpen.value = b
}
const testChange = () => {
}
const showPromissSiteBar = computed(() => layoutsStore.getShowPromissSiteBar)
const openButtonBarRef = ref(null)
provide('showOption', showOption)


function showOpenButton() {
  showOption.value = false
  layoutsStore.commitShowPromissSiteBar(false)
}

provide('SystemOption',{
  showOpenButton
})

function openOptionPage(){
  showOption.value=true
  layoutsStore.commitShowPromissSiteBar(true)
}
const goMessage = () => {
  //router.push('/zhongZhang/cashiers/ab-xjll/message-list');
 // router.push('/system/institution-management/im-group')
  //弹窗
  openMoalPopPage(true, {
  });

}
const goTask = () => {
  //router.push('/zhongZhang/cashiers/ab-xjll/task-list');
  router.push('/system/institution-management/im-group')
  //router.push('/one/one/unit-info')

}
const {createConfirm} = useMessage();
let mark = false;
const outSystemBefore = () => {
  if (mark) return false
  mark = true
  createConfirm({
    iconType: 'info',
    title: '退出登录',
    content: '您确定要退出登录吗?退出后将前往登录页面！',
    onOk: async() => {
      mark = false
      useUserStore().logout(true)
      // 清理当前用户操作产生的账套缓存
      useAccountPickerCache().closeAllAccountPickerInfo()
      // 清理 已储存账套列表
      const companyOperateStore = useCompanyOperateStoreWidthOut()
      companyOperateStore.commitAuthorizeList(null);
      companyOperateStore.commitAccountList(null)
      companyOperateStore.commitAssetsAccountList(null);
      companyOperateStore.commitStockAccountList(null)
      companyOperateStore.commitAllAuthorizeAccountList(null);
    }
  });
}

const [registerModalPopPage, { openModal: openMoalPopPage }] = useModal();

const modalShow = () => {
  openMoalPopPage(true, {
    database: dynamicTenantId.value,
  });
}

// 弹选回调
const modalData = async (data) => {
  console.log(data)
}

</script>
<style lang="less" scoped>
// 人为干预 徽标数 大小
:deep(.ant-badge){
  .ant-badge-count{
    height: 10px;
    top: -3px;
    min-width: 10px;
    line-height: 10px;
  }
}
.hoverSvg{
  cursor:pointer;
}
.hoverSvg:hover path{
  stroke:#1a7575 !important;
}
</style>

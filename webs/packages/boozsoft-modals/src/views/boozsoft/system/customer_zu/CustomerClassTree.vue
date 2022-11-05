<template>
  <div class="bg-white m-4 mr-0 overflow-hidden">
    <div style="width: 100%; height: 50px">
      <label style="font-size: 20px; line-height: 50px; margin-left: 20px">客户分类</label>
      <div style="float: right; margin-right: 20px; margin-top: 10px">
        <a style="cursor: pointer; color: blue" @click="openAddPage()">
<!--          <PlusOutlined /> -->
          添加</a>
      </div>
    </div>
    <BasicTree
      defaultExpandAll
      :click-row-to-expand="false"
      :tree-data="treeData"
      v-if="treeData.length"
      :replace-fields="{ key: 'uniqueCustclass', title: 'value' }"
      @select="handleSelect"
    />
  </div>
  <CustomerAdd @save="saveData" @register="registerEditPage" />
</template>
<script setup lang="ts">
import {computed, onMounted, ref, watch} from 'vue';
  import { BasicTree } from '/@/components/Tree';
  import { GetCustomerClassTree, customerSaveApi } from '/@/api/record/system/customer_class';
  import CustomerAdd from '../customer_class/popup/save.vue';
  import { useModal } from '/@/components/Modal';
  import { findAllIperiodIyear } from '/@/api/subjectInitialBalance/subjectInitialBalance';
  import { useCompanyOperateStoreWidthOut } from '/@/store/modules/operate-company';
  import {getCurrentAccountName} from "/@/api/task-api/tast-bus-api";
  import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
  import {useUserStoreWidthOut} from "/@/store/modules/user";
import {findDataBase} from "/@/api/record/system/account";
  // 数据库模式名称
  const database = ref(getCurrentAccountName(false));
  const emit=defineEmits(['select','register']);
  const treeData = ref([]);
  const props=defineProps(['modelValue'])
  // 默认账套编码
  let defalutCompany = getCurrentAccountName(true)
  // 切换后账套编码
  const dynamicCompany = ref(defalutCompany)
  async function fetch() {
    const deptTree = await useRouteApi(GetCustomerClassTree,{schemaName: getCurrentAccountName(true)})('')
    function a(customerTree) {
      customerTree.forEach((item) => {
        if (item.children != null) {
          a(item.children);
        }
        item.value = '[' + item.cusClass + ']__' + item.cusCclassName;
      });
    }
    a(deptTree);
    treeData.value = deptTree;
    props.modelValue.thisLeftKey = treeData.value === null?'':treeData.value[0].uniqueCustclass
  }

  function handleSelect(keys: string, e) {
    let data = {
      key:  keys[0],
      val: getCurrentAccountName(true)
    }
    emit('select', data);
  }
  const [registerEditPage, { openModal: openEditPage }] = useModal();
  const openAddPage = (data, index, e) => {
    openEditPage(true, {
      data: '',
    });
  };

  onMounted(async () => {
    // const qwe =
    //   database.value.toString().split('-').length > 2
    //     ? database.value.toString().substring(0, database.value.toString().length - 5)
    //     : database.value;
    // // 会计期间年度
    const res: any = await findAllIperiodIyear(getCurrentAccountName(false));
    // 默认最大年度
    useCompanyOperateStoreWidthOut().commitSchemaName(res[0].accountId );

    await fetch();
  });
  async function saveData(data) {
    await useRouteApi(customerSaveApi,{schemaName: getCurrentAccountName(true)})({params:data, username:useUserStoreWidthOut().getUserInfo.username})
    await fetch();
  }
const totalChange=computed(()=>props.modelValue.thisAdInfo)
watch(totalChange, async (a)=>{
    if (Object.keys(a).length > 0){
      dynamicCompany.value = a.accId + '-' +defalutCompany.split('-')[2]
      fetch()
    }
  }
)
</script>
<style scoped>
  .bg-white{
    width: 250px !important;
    min-height: 250px !important;
    height: calc(100% - 180px);
    border: 1px #cccccc solid;
    background:white !important;
    margin-top: .5%;
  }
</style>

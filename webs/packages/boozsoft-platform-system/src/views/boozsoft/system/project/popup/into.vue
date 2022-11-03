<template>
  <BasicModal
    width="900px"
    v-bind="$attrs"
    title="组织授权引入"
    wrapClassName="head-title"
    @register="register"
  >
    <template #title>
      <div style="display: flex;height:30px;margin-top: 10px;margin-left: 10px;" class="vben-basic-title">
        <span style="font-size: 24px;line-height:30px;">
          <ShareAltOutlined style="font-size: 28px;font-weight: bold"/>&nbsp;&nbsp;账套授权引入 － 项目信息
        </span>
      </div>
    </template>
    <div class="nc-open-content">
      <div class="open-content-up" style="margin-top: 10px;">
        <div style="background:#0096c7;padding:10px;border-radius: 2px;margin-bottom:10px;display: flex;justify-content : space-between;">
          <div class="a1">
            <a-select v-model:value="formItems.selectType" style="width: 120px;font-size: 12px;" class="special_select">
              <a-select-option style="font-size: 12px;" value="projectCode">项目编码</a-select-option>
              <a-select-option style="font-size: 12px;" value="projectName">项目名称</a-select-option>
            </a-select>
            <a-input-search
              placeholder=""
              style="width: 200px; border-radius: 4px"
              v-model:value="formItems.selectValue"
              @search="onSearch"
            />
          </div>
          <div class="a2">
            <button
              type="button"
              class="ant-btn ant-btn-me"
              @click="assignData()"
            ><span>引入</span></button>
            <button
              type="button"
              class="ant-btn ant-btn-me"
              @click="reloadProject()"
            ><span>刷新</span></button>
          </div>
        </div>

        <div style="height: 350px;padding:0;display: flex;justify-content : space-between;">
          <BasicTable
            :row-selection="{ type: 'checkbox', selectedRowKeys: state.selectedRowKeys, onChange: onSelectChange }"
            :scroll="{ y: 270 }"
            @register="registerTable"
            :dataSource="tableData"
            class="tables"
          >
            <template #flag="{ record }">
              <span>
                <a-tag :color="record.flag == '1' ? 'green' : 'volcano'">
                  {{ record.flag == '1' ? '已引入' : '未引入' }}
                </a-tag>
              </span>
            </template>
          </BasicTable>
        </div>
      </div>
    </div>

    <template #footer>
      <div>
        <a-button @click="closeModal()" type="primary">关闭</a-button>
      </div>
    </template>

  </BasicModal>
</template>

<script setup="props, {content}" lang="ts">
import { PageWrapper } from '/@/components/Page'
import {nextTick, onMounted, reactive, ref, unref} from 'vue'
import {BasicModal, useModal, useModalInner} from '/@/components/Modal'
import {
  ShareAltOutlined
} from '@ant-design/icons-vue'
import {Select as ASelect, Input as AInput, Checkbox as ACheckbox,Button as AButton,Tabs as ATabs,Tag as ATag , message} from 'ant-design-vue'

const AInputSearch = AInput.Search
const ASelectOption = ASelect.Option
const ATabPane = ATabs.TabPane
import {useMessage} from "/@/hooks/web/useMessage";
import {BasicTable, useTable} from "/@/components/Table";
import {
  findByOriginId, findByTenantId,
  save
} from "/@/api/record/system/group-project-account";
import {useUserStoreWidthOut} from "/@/store/modules/user";
import {saveProject} from "/@/api/record/system/project";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {findByUniqueCode} from "/@/api/record/system/origin-project";
import {findById, findProClassByCode} from "/@/api/record/system/origin-project-class";
import {
  findByTenantIdAndUniqueCode,
  save as saveProClassAccount
} from "/@/api/record/system/group-project-class-account";
import {saveProClass} from "/@/api/record/system/project_class";

const {
  createErrorModal,
  createConfirm,
  createWarningModal
} = useMessage()

const emit = defineEmits(['register', 'save'])

const formItems: any = ref({
  selectType: 'projectCode',
  selectValue: ''
})

const activeKey = ref(1)

const accountList: any = ref([])
const tableData:any = ref([]);
const tableDataAll:any = ref([]);

const dynamicTenantId = ref('')
const projectClassCtl = ref('')
const itemCode = ref('')
const [register, {closeModal,setModalProps}] = useModalInner(async(data) => {
  dynamicTenantId.value = data.dynamicTenantId
  projectClassCtl.value = data.projectClassCtl
  itemCode.value = data.itemCode
  await reloadProject()
  state.selectedRowKeys = []
  checkRow.value = []
})

//改变组织
async function changeOrigin() {
  await reloadProject()
}

const columns = [
  {
    title: '项目编码',
    dataIndex: 'projectCode',
    width: 100,
    ellipsis: true
  },
  {
    title: '项目名称',
    dataIndex: 'projectName',
    ellipsis: true,
    align: 'left',
  },
  {
    title: '使用状态',
    dataIndex: 'flag',
    width: 100,
    ellipsis: true,
    slots: { customRender: 'flag'}
  },
  {
    title: '分配人',
    dataIndex: 'assignUserName',
    ellipsis: true,
  },
  {
    title: '分配时间',
    dataIndex: 'assignDate',
    ellipsis: true,
  },
]

// 这是示例组件
const [registerTable, {reload,getColumns,setPagination}] = useTable({
  columns: columns,
  bordered: true,
  showIndexColumn: false,
  pagination: false,
})

const projectAccountList:any = ref([])
async function reloadProject(){
  checkRow.value = []
  state.selectedRowKeys = []
  const projectAccount = await findByTenantId(dynamicTenantId.value)
  projectAccountList.value = projectAccount.items
  tableDataAll.value = projectAccountList.value.filter(item=>{
    return item.flag != '1'
  })
  tableData.value = tableDataAll.value
  await setPagination({
    total: tableData.value.length
  })
}

onMounted(async () => {
})

//选中内容
const state = reactive<{
  selectedRowKeys: [];
  loading: boolean;
}>({
  selectedRowKeys: [], // Check here to configure the default column
  loading: false,
});
const checkRow: any = ref([])
const onSelectChange = (selectedRowKeys, row) => {
  // console.log('selectedRowKeys changed: ', row);
  state.selectedRowKeys = selectedRowKeys;
  checkRow.value = row
};

async function handleOk() {
  emit('save', unref(formItems));
  closeModal();
}

async function onSearch(){
  tableData.value = tableDataAll.value.filter(item => {
    //通过部门编码过滤
    if (formItems.value.selectType == 'projectCode' && formItems.value.selectValue != '') {
      return item.itemCode.indexOf(formItems.value.selectValue) != -1
    }
    //通过部门名称过滤
    if (formItems.value.selectType == 'projectName' && formItems.value.selectValue != '') {
      return item.itemName.indexOf(formItems.value.selectValue) != -1
    }
    return item
  })
  await setPagination({
    total: tableData.value.length
  })
}

async function assignData() {
  if (checkRow.value.length > 0) {
    let dateTime = new_Date()
    for (let i = 0; i < checkRow.value.length; i++) {
      const item = checkRow.value[i]
      let item1 = await findByUniqueCode(item.uniqueCode,item.originId)
      item1.id = null
      item1.itemCode = itemCode.value
      await useRouteApi(saveProject,{schemaName: dynamicTenantId})(item1);
      item.flag = '1'
      item.acceptUser = useUserStoreWidthOut().getUserInfo.id
      item.acceptDate = dateTime
      await save(item)
      const projectClass = await findProClassByCode(item.projectClassCode,item.originId)
      if (projectClass.length>0) {
        const res = await findByTenantIdAndUniqueCode(dynamicTenantId.value, projectClass[0].uniqueCode)
        if (res.length>0){
          if(res[0].flag=='0'){
            if (item.jici=='1') {//保存1条
              const item1 = {
                id: null,
                uniqueCode: item.uniqueCode,
                projectClassCode: item.projectClassCode,
                projectClassName: item.projectClassName,
                projectCateCode: item.projectCateCode,
                parentId: '0',
                bend: item.bend,
                jici: item.jici,
              }
              await useRouteApi(saveProClass,{schemaName: dynamicTenantId})(item1);
            } else if (item.jici=='2') {//保存2条
              let projectClass1 = await findById(item.parentId)

              projectClass1.id = null
              projectClass1 = await useRouteApi(saveProClass,{schemaName: dynamicTenantId})(projectClass1);
              const item1 = {
                id: null,
                uniqueCode: item.uniqueCode,
                projectClassCode: item.projectClassCode,
                projectClassName: item.projectClassName,
                projectCateCode: item.projectCateCode,
                parentId: projectClass1.id,
                bend: item.bend,
                jici: item.jici,
              }
              await useRouteApi(saveProClass,{schemaName: dynamicTenantId})(item1);
            } else if (item.jici=='3') {//保存3条
              let projectClass1 = await findById(item.parentId)
              let projectClass2 = await findById(projectClass1.parentId)

              projectClass2.id = null
              projectClass2 = await useRouteApi(saveProClass,{schemaName: dynamicTenantId})(projectClass2);
              projectClass1.id = null
              projectClass1.parentId = projectClass2.parentId
              projectClass1 = await useRouteApi(saveProClass,{schemaName: dynamicTenantId})(projectClass1);
              const item1 = {
                id: null,
                uniqueCode: item.uniqueCode,
                projectClassCode: item.projectClassCode,
                projectClassName: item.projectClassName,
                projectCateCode: item.projectCateCode,
                parentId: projectClass1.id,
                bend: item.bend,
                jici: item.jici,
              }
              await useRouteApi(saveProClass,{schemaName: dynamicTenantId})(item1);
            } else if (item.jici=='4'){//保存4条
              let projectClass1 = await findById(item.parentId)
              let projectClass2 = await findById(projectClass1.parentId)
              let projectClass3 = await findById(projectClass2.parentId)

              projectClass3.id = null
              projectClass3 = await useRouteApi(saveProClass,{schemaName: dynamicTenantId})(projectClass3);
              projectClass2.id = null
              projectClass2.parentId = projectClass3.parentId
              projectClass2 = await useRouteApi(saveProClass,{schemaName: dynamicTenantId})(projectClass2);
              projectClass1.id = null
              projectClass1.parentId = projectClass2.parentId
              projectClass1 = await useRouteApi(saveProClass,{schemaName: dynamicTenantId})(projectClass1);
              const item1 = {
                id: null,
                uniqueCode: item.uniqueCode,
                projectClassCode: item.projectClassCode,
                projectClassName: item.projectClassName,
                projectCateCode: item.projectCateCode,
                parentId: projectClass1.id,
                bend: item.bend,
                jici: item.jici,
              }
              await useRouteApi(saveProClass,{schemaName: dynamicTenantId})(item1);
            }
            res[0].flag = '1'
            res[0].acceptUser = useUserStoreWidthOut().getUserInfo.id
            res[0].acceptDate = dateTime
            await saveProClassAccount(res[0])
          }
        }
      }
    }
    emit('save', unref(formItems));
    message.success('引入成功!')
    await reloadProject()
  } else {
    createWarningModal({
      iconType: 'warning',
      title: '引入',
      content: '请选择需要引入的内容！'
    })
  }
}

//获取当年月日
function new_Date() {
  let dateTime
  let yy = new Date().getFullYear()
  let mm = new Date().getMonth() + 1 < 10 ? '0' + (new Date().getMonth()+ 1) : (new Date().getMonth()+ 1)
  let dd = new Date().getDate() < 10 ? '0' + new Date().getDate() : new Date().getDate()
  let hh = new Date().getHours() < 10 ? '0' + new Date().getHours() : new Date().getHours()
  let mf = new Date().getMinutes() < 10 ? '0' + new Date().getMinutes() : new Date().getMinutes()
  let ss = new Date().getSeconds() < 10 ? '0' + new Date().getSeconds() : new Date().getSeconds()
  dateTime = yy + '-' + mm + '-' + dd + ' ' + hh + ':' + mf + ':' + ss
  // console.log(dateTime)
  return dateTime
}

</script>
<style scoped lang="less">
.ant-modal-header{
  border: none !important;
}
.vben-basic-title {
  color: #0096c7 !important;
  border: none !important;
  margin-bottom: -20px !important;
}

:global(.ant-modal-body) {
  padding: 0px;
  border-bottom: 1px solid rgb(1, 129, 226);
  border-left: none;
  border-right: none;
  margin-bottom: 0!important;
  .scrollbar{
    padding: 0px;
    .scroll-container{
      margin-bottom: 0!important;
    }
  }
}

.tables :deep(td),
.tables :deep(th) {
  font-size: 14px !important;
  padding: 0 5px !important;
}

.tables :deep(th) {
  text-align: center !important;
  font-weight: bold;
  background-color: #cccccc;
  line-height:30px;
}

:deep(.ant-table-row-selected) td{
  background: #0096c7 !important;
}

:deep(.ant-tabs-left) > .ant-tabs-nav{
  padding-top: 10px;
  width: 120px;
  text-align: center;
  background-color: #eeeeee;
  font-weight: bold;
  font-size: 14px;
}
</style>
<style lang="less">
.nc-open-content {
  label {
    text-align: left;
    width: 110px;
    display: inline-block;
    padding-top: 2px;
    padding-bottom: 2px;
    color: #535353;
  }
}
.ant-select,.ant-input{
  line-height: 25px;
  //border: 1px solid #000000;
}
</style>
<style>
.head-title .scroll-container .scrollbar__wrap {
  margin-bottom: 0 !important;
}

</style>

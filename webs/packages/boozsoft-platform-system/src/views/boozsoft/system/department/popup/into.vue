<template>
  <BasicModal
    width="900px"
    v-bind="$attrs"
    title="组织授权引入"
    wrapClassName="head-title"
    @register="register"
  >
    <template #title>
      <div style="display: flex;" class="vben-basic-title">
        <span style="font-size: 24px;line-height:30px;margin-top: 10px;margin-left: 10px;">
          <ShareAltOutlined style="font-size: 28px;font-weight: bold"/>&nbsp;&nbsp;组织授权引入 － 人员信息
        </span>
      </div>
    </template>
    <div class="nc-open-content">
      <div class="open-content-up" style="margin-top: 10px;">
        <div style="background:#0096c7;padding:10px;border-radius: 2px;margin-bottom:10px;display: flex;justify-content : space-between;">
          <div class="a1">
            <a-select v-model:value="formItems.selectType" style="width: 120px;font-size: 12px;" class="special_select">
              <a-select-option style="font-size: 12px;" value="psnCode">人员编码</a-select-option>
              <a-select-option style="font-size: 12px;" value="psnName">人员姓名</a-select-option>
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
<!--            <button
              type="button"
              class="ant-btn ant-btn-me"
              @click="unAssignData()"
              v-if="activeKey=='1'"
            ><span>取消引入</span></button>-->
            <button
              type="button"
              class="ant-btn ant-btn-me"
              @click="reloadPsnType()"
            ><span>刷新</span></button>
          </div>
        </div>

        <div style="height: 400px;padding:0;display: flex;justify-content : space-between;">
          <BasicTable
            :row-selection="{ type: 'checkbox', selectedRowKeys: state.selectedRowKeys, onChange: onSelectChange }"
            :scroll="{ y: 290 }"
            @register="registerTable"
            :dataSource="tableData"
            class="tables"
          >
            <template #uniquePsnType="{ record }"> {{ formatUniquePsnType(record.uniquePsnType) }}</template>
            <template #psnSex="{ record }"> {{ formatPsnSex(record.psnSex) }}</template>
            <template #psnType="{ record }"> {{ formatPsnType(record.psnType) }}</template>
            <template #cellPhoneNum="{ record }">
              <span v-if="record.cellPhoneNum!=null&&record.cellPhoneNum!=''">{{plusStr(record.cellPhoneNum,3,4,'****')}}</span>
            </template>
            <template #flag="{ record }">
              <span>
                <a-tag :color="record.flag == '1' ? 'green' : 'volcano'">
                  {{ record.flag == '1' ? '已引入' : '未引入' }}
                </a-tag>
              </span>
            </template>
          </BasicTable>
<!--          <a-tabs size="small" tab-position="left" v-model:activeKey="activeKey" @change="changeOrigin">
            <a-tab-pane key="1" tab="已引入">
              <div style="margin-top: -10px;overflow:auto;max-height: 350px;margin-left: -15px;">
                <BasicTable
                  :row-selection="{ type: 'checkbox', selectedRowKeys: state1.selectedRowKeys, onChange: onSelectChange1 }"
                  :scroll="{ y: 350 }"
                  @register="registerTable1"
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
            </a-tab-pane>
            <a-tab-pane key="2" tab="未引入">
              <div style="margin-top: -10px;overflow:auto;max-height: 350px;margin-left: -15px;">
                <BasicTable
                  :row-selection="{ type: 'checkbox', selectedRowKeys: state.selectedRowKeys, onChange: onSelectChange }"
                  :scroll="{ y: 350 }"
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
            </a-tab-pane>
          </a-tabs>-->
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
  findByTenantId,
  save
} from "/@/api/record/system/group-psn-account";
import {useUserStoreWidthOut} from "/@/store/modules/user";
import {savePsn} from "/@/api/record/system/psn";
import {psnTypeFindAll} from "/@/api/record/system/group-psn-type";
import {findByOriginIdAndUniqueCode} from "/@/api/record/system/origin-psn";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {savePsnType} from "/@/api/record/system/psn-type";
import {
  findByTenantIdAndUniqueCode,
  save as savePsnTypeAccount
} from "/@/api/record/system/group-psn-type-account";

const {
  createErrorModal,
  createConfirm,
  createWarningModal
} = useMessage()

const emit = defineEmits(['register', 'save'])

const formItems: any = ref({
  selectType: 'psnCode',
  selectValue: ''
})

const activeKey = ref(1)

const accountList: any = ref([])
const tableData:any = ref([]);
const tableDataAll:any = ref([]);
//查询组织
const dynamicTenantId:any = ref('')
const [register, {closeModal,setModalProps}] = useModalInner(async(data) => {
  dynamicTenantId.value = data.dynamicTenantId
  await reloadPsnType()
  state.selectedRowKeys = []
  checkRow.value = []
})

//改变组织
async function changeOrigin() {
  await reloadPsnType()
}

const columns = [
  {
    title: '使用状态',
    dataIndex: 'flag',
    width: 100,
    ellipsis: true,
    slots: { customRender: 'flag'}
  },
  {
    title: '人员编码',
    dataIndex: 'psnCode',
    width: 100,
    ellipsis: true
  },
  {
    title: '人员姓名',
    dataIndex: 'psnName',
    ellipsis: true,
    align: 'left',
  },
  {
    title: '人员类别',
    dataIndex: 'uniquePsnType',
    width: 100,
    ellipsis: true,
    slots: {customRender: 'uniquePsnType'}
  },
  {
    title: '性别',
    dataIndex: 'psnSex',
    width: 100,
    ellipsis: true,
    slots: {customRender: 'psnSex'}
  },
  {
    title: '手机号',
    dataIndex: 'cellPhoneNum',
    width: 120,
    ellipsis: true,
    slots: {customRender: 'cellPhoneNum'}
  },
  {
    title: '人员属性',
    dataIndex: 'psnType',
    width: 100,
    ellipsis: true,
    slots: {customRender: 'psnType'}
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
  pagination: {
    pageSize: 50,
    showSizeChanger: true,
    pageSizeOptions: ['10', '30', '50', '100'],
    showTotal: t => `总共${t}条数据`
  },
})

const psnTypeList:any = ref([])
onMounted(async () => {
  const psnTypeList1 = await psnTypeFindAll()
  psnTypeList.value = psnTypeList1.items
})

function formatUniquePsnType(uniquePsnType: any){
  let str = uniquePsnType
  psnTypeList.value.forEach(item=>{
    if (item.uniqueCode == uniquePsnType) {
      str = item.psnTypeName
    }
  })
  return str
}

function formatPsnSex(sex: String) {
  let str = '男'
  switch (sex) {
    case '0':
      str = '未知的性别'
      break
    case '1':
      str = '男'
      break
    case '2':
      str = '女'
      break
    case '9':
      str = '未说明的性别'
  }
  return str
}

function formatPsnType(type: any) {
  let str = '内部员工'
  switch (type) {
    case '1':
      str = '内部员工'
      break
    case '2':
      str = '外部员工'
      break
  }
  return str
}

const psnTypeAccountList:any = ref([])
async function reloadPsnType(){
  checkRow.value = []
  state.selectedRowKeys = []
  const psnTypeAccount = await findByTenantId(dynamicTenantId.value)
  psnTypeAccountList.value = psnTypeAccount.items
  tableDataAll.value = psnTypeAccountList.value.filter(item=>{
    return item.flag != '1'
  })
  tableData.value = tableDataAll.value
  await setPagination({
    total: tableData.value.length
  })
}

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
    //通过人员编码过滤
    if (formItems.value.selectType == 'psnCode' && formItems.value.selectValue != '') {
      return item.psnCode.indexOf(formItems.value.selectValue) != -1
    }
    //通过人员名称过滤
    if (formItems.value.selectType == 'psnName' && formItems.value.selectValue != '') {
      return item.psnName.indexOf(formItems.value.selectValue) != -1
    }
    //通过性别过滤
    if (formItems.value.selectType == 'psnSex' && formItems.value.selectValue != '') {
      if ('未知的性别'.indexOf(formItems.value.selectValue)!=-1) {
        return item.psnSex == '0' || item.psnSex.indexOf(formItems.value.selectValue) != -1
      }
      if (formItems.value.selectValue == '男') {
        return item.psnSex == '1' || item.psnSex.indexOf(formItems.value.selectValue) != -1
      }
      if (formItems.value.selectValue == '女') {
        return item.psnSex == '2' || item.psnSex.indexOf(formItems.value.selectValue) != -1
      }
      if ('未说明的性别'.indexOf(formItems.value.selectValue)!=-1) {
        return item.psnSex == '9' || item.psnSex.indexOf(formItems.value.selectValue) != -1
      }
      return item.psnSex.indexOf(formItems.value.selectValue) != -1
    }
    //通过手机过滤
    if (formItems.value.selectType == 'cellPhoneNum' && formItems.value.selectValue != '') {
      return item.cellPhoneNum.indexOf(formItems.value.selectValue) != -1
    }
    //通过证件类型过滤
    if (formItems.value.selectType == 'uniquePsnType' && formItems.value.selectValue != '') {
      const b = psnTypeList.value.filter(bb => {
        return bb.psnTypeName.indexOf(formItems.value.selectValue) != -1
      })
      if(b.map(bb=>bb.uniqueCode).filter(dd=>dd==item.psnTypeName).length>0){
        return true
      }
      return item.uniquePsnType.indexOf(b.map(bb=>bb.uniqueCode)) != -1
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
      const item1 = await findByOriginIdAndUniqueCode(item.originId,item.uniqueCode)
      item1.id = null
      await useRouteApi(savePsn,{schemaName: dynamicTenantId})(item1);
      item.flag = '1'
      item.acceptUser = useUserStoreWidthOut().getUserInfo.id
      item.acceptDate = dateTime
      await save(item)
      const res = await findByTenantIdAndUniqueCode(dynamicTenantId.value,item.uniquePsnType)
      if (res.length>0){
        if(res[0].flag=='0'){
          for (const psnType of psnTypeList.value) {
            if (psnType.uniqueCode==item.uniquePsnType){
              const item2 = {
                id: null,
                uniqueCode: psnType.uniqueCode,
                psnTypeCode: psnType.psnTypeCode,
                psnTypeName: psnType.psnTypeName,
              }
              await useRouteApi(savePsnType,{schemaName: dynamicTenantId})(item2);
              res[0].flag = '1'
              res[0].acceptUser = useUserStoreWidthOut().getUserInfo.id
              res[0].acceptDate = dateTime
              await savePsnTypeAccount(res[0])
            }
          }
        }
      }
    }
    emit('save', unref(formItems));
    message.success('引入成功!')
    await reloadPsnType()
  } else {
    createWarningModal({
      iconType: 'warning',
      title: '引入',
      content: '请选择需要引入的内容！'
    })
  }
}

async function unAssignData() {
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

/* 部分隐藏处理
** str 需要处理的字符串
** frontLen  保留的前几位
** endLen  保留的后几位
** cha  替换的字符串
*/
function plusXing(str, frontLen, endLen,cha) {
  let len = str.length - frontLen - endLen;
  let xing = '';
  for (let i = 0; i < len; i++) {
    xing += cha;
  }
  return str.substring(0, frontLen) + xing + str.substring(str.length - endLen);
}
function plusStr(str, frontLen, endLen,cha) {
  return str.substring(0, frontLen) + cha + str.substring(str.length - endLen);
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

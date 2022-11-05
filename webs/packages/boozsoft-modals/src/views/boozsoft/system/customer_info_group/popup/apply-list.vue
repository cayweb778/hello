<template>
    <BasicModal
      width="900px"
      v-bind="$attrs"
      @cancel="handleOk()"
      wrapClassName="head-title"
      @register="register"
    >
      <template #title>
        <div style="display: flex;height:30px;margin-top: 10px;" class="vben-basic-title">
        <span style="font-size: 24px;line-height:30px;">
          <FormOutlined style="font-size: 28px;font-weight: bold"/>&nbsp;&nbsp;生效审批 － 客户信息
        </span>
        </div>
      </template>
      <div class="nc-open-content">
        <div class="open-content-up" style="margin-top: 10px;">
          <div style="background:#0096c7;padding:10px;border-radius: 2px;margin-bottom:10px;display: flex;justify-content : space-between;">
            <div class="a1">
              <a-select v-model:value="pageParameter.searchConditon.requirement" style="width: 120px;font-size: 12px;" class="special_select">
                <a-select-option style="font-size: 12px;" value="custCode">客户编码</a-select-option>
                <a-select-option style="font-size: 12px;" value="custName">客户全称</a-select-option>
              </a-select>
              <a-input-search
                v-model:value="pageParameter.searchConditon.value"
                placeholder=""
                style="width: 200px; border-radius: 4px"
                @search="reload"
              />
            </div>
            <div class="a2">
              <button type="button" class="ant-btn ant-btn-me" @click="openLook()" ><span>查看</span></button>
              <button type="button" class="ant-btn ant-btn-me" @click="applyOk()" ><span>同意</span></button>
              <button type="button" class="ant-btn ant-btn-me" @click="applyNo()" ><span>驳回</span></button>
              <button type="button" class="ant-btn ant-btn-me" @click="delList()" ><span>删除</span></button>
            </div>
          </div>

          <div style="height: 350px;padding:0;display: flex;justify-content : space-between;">
            <BasicTable
              :row-selection="{ type: 'checkbox',}"
              :scroll="{ y: 270 }"
              @register="registerTable"
              @fetch-success="fetchData"
              class="tables"
            >
              <template #successState="{ record }">
                <span>
                  <a-tag :color="record.successState == '0' ? 'warning' : 'volcano'">
                    {{ record.successState == '0' ? '未生效' : '已驳回' }}
                  </a-tag>
                </span>
              </template>
              <template #ctype="{ record }">
                  {{ record.ctype == '1' ? '组织' : '账套' }}
              </template>

              <template #custCode="{ record }"> <a @click="openPageData(record)">{{ record.custCode }}</a> </template>
              <template #custName="{ record }"> <a @click="openPageData(record)">{{ record.custName }}</a> </template>
              <template #custAbbname="{ record }"> <a @click="openPageData(record)">{{ record.custAbbname }}</a> </template>
            </BasicTable>
            <Edit @register="registerEditPage" />
          </div>
        </div>
      </div>


      <a-modal v-model:visible="visibleModel2" :closable="false" @ok="modalSubmit" style="margin-top: 10px;" width="850px">
        <template #title>
          <div style="display: flex;height:30px;margin-top: 10px;" class="vben-basic-title">
        <span style="font-size: 24px;line-height:30px;">
          <FileSearchOutlined style="font-size: 28px;font-weight: bold"/>&nbsp;&nbsp;集团客户
        </span>
          </div>
        </template>
        <br>
        <BasicTable
          :columns="groupColumns"
          :dataSource="groupData"
          :class="'a-table-font-size-12'"
          :scroll="{ y: 250 }"
          :row-selection="{ type: 'radio', selectedRowKeys: state.selectedRowKeys, onChange: onSelectChange }"
          @register="registerTable2"
        />

      </a-modal>

      <a-modal v-model:visible="visibleModel" @ok="applyNoSubmit">
        <template #title>
          <div style="display: flex;height:30px;margin-top: 10px;" class="vben-basic-title">
        <span style="font-size: 24px;line-height:30px;">
          <FormOutlined style="font-size: 28px;font-weight: bold"/>&nbsp;&nbsp;驳回原因
        </span>
          </div>
        </template>
        <div class="nc-open-content" style="text-align: center;margin-top: 30px;margin-bottom: 0">
          <div class="open-content-up">
            <label style="font-size: 18px;margin-left: 0;width:130px;">驳回原因：</label>
            <a-input v-model:value="applyNoReason" placeholder="" class="abc" style="width: 60%;" />
          </div>
        </div>
        <br>
      </a-modal>
      <template #footer>
        <div>
          <a-button @click="handleOk()" type="primary">关闭</a-button>
        </div>
      </template>
    </BasicModal>
</template>

<script setup="props, {content}" lang="ts">
import { useUserStoreWidthOut } from '/@/store/modules/user';
import {nextTick, onMounted, reactive, ref, unref} from 'vue'
import {BasicModal, useModal, useModalInner} from '/@/components/Modal'
import { PlusCircleOutlined,FormOutlined,FileSearchOutlined } from '@ant-design/icons-vue'
import {
  Modal as AModal,
  Select as ASelect,
  Input as AInput,
  Checkbox as ACheckbox,
  Button as AButton,
  Tag as ATag,
  message
} from 'ant-design-vue'
import {useMessage} from "/@/hooks/web/useMessage";
import {BasicTable, useTable} from "/@/components/Table";
import {
  GroupCustomerAddApi,
  findAll,
  delCusArrGroup,
  editGroupCusAll,
  findAllByGroupCusNameAndCustAbbnameLike,
  editByIdSuccessState,
  groupCusAssignOrgSave,
  delByIdCustNameAndCustAbbnameAndSuccessState
} from "/@/api/record/system/customer_group";
import {findByUserIdAndOriginId} from "/@/api/record/system/group-permission";
import {findAllOrgCusClass, saveAll as orgCusClassSave} from "/@/api/org/cus_class/org_cus_class";
import {saveLog} from "/@/api/record/system/group-sys-login-log";
import Edit from '/@/views/boozsoft/system/customer_info_group/popup/edit.vue';
import {originCustomerSaveAll} from "/@/api/org/cus/org_cus";
import {columnProps} from "ant-design-vue/es/table/interface";
import {
  assignOrgSave,
  findAllCustomerClass,
  findByOrgAssigAccount as findByOrgCusClassAssigAccount
} from "/@/api/record/system/customer_class_group";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {duLiSaveApi} from "/@/api/record/costomer_data/customer";

const [registerEditPage, { openModal: openEditPage }] = useModal();
const AInputSearch = AInput.Search
const ASelectOption = ASelect.Option
const {createErrorModal,createConfirm,createWarningModal} = useMessage()

const emit = defineEmits(['register', 'save'])
//选中内容
type Key = columnProps['id'];
const state = reactive<{
  selectedRowKeys: Key[];
  loading: boolean;
}>({
  selectedRowKeys: [], // Check here to configure the default column
  loading: false,
});
const visibleModel2=ref(false)
const visibleModel=ref(false)
const applyNoReason=ref('')
const orgUnique=ref('')
const pageParameter = reactive({
  searchConditon: {
    requirement: 'custName',
    value: '',
  },
  uniqueCustclass:'0',
  flag:'1',page:'1',size:'10000'
})

const [register, {closeModal,setModalProps}] = useModalInner(async(data) => {
  clearSelectedRowKeys()
  reload()
})

const groupData=ref([])
const groupColumns = [
  {
    title: '客户编码',
    dataIndex: 'custCode',align:'left',
    ellipsis: true,
  },
  {
    title: '客户全称',
    dataIndex: 'custName',align:'left',
    ellipsis: true,
  },
  {
    title: '客户简称',
    dataIndex: 'custAbbname',align:'left',
    ellipsis: true,
  },
]
const columns = [
  {
    title: '生效状态',
    dataIndex: 'successState',
    ellipsis: true,
    width: 100,
    slots: { customRender: 'successState'}
  },
  {
    title: '审批情况',
    dataIndex: 'reason',
    ellipsis: true,
  },
  {
    title: '客户编码',
    dataIndex: 'custCode',align:'left',
    ellipsis: true,slots: { customRender: 'custCode'}
  },
  {
    title: '客户全称',
    dataIndex: 'custName',align:'left',
    ellipsis: true,slots: { customRender: 'custName'}
  },
  {
    title: '客户简称',
    dataIndex: 'custAbbname',align:'left',
    ellipsis: true,slots: { customRender: 'custAbbname'}
  },
  {
    title: '税号',
    dataIndex: 'custSregcode',
    ellipsis: true,
  },
  {
    title: '分类名称',
    dataIndex: 'uniqueCustclassName',
    ellipsis: true,
  },
  {
    title: '申请类型',
    dataIndex: 'ctype',
    ellipsis: true,
    slots: { customRender: 'ctype'}
  },
  {
    title: '申请来源',
    dataIndex: 'orgName',
    ellipsis: true,
  },
  {
    title: '申请人',
    dataIndex: 'applyUser',
    ellipsis: true,
  },{
    title: '申请时间',
    dataIndex: 'applyDate',
    ellipsis: true,
  },
]
// 这是示例组件
const [registerTable, {reload,getColumns,setPagination,getSelectRows,clearSelectedRowKeys,setTableData}] = useTable({
  api:findAll,
  columns: columns,
  bordered: true,
  showIndexColumn: false,
  pagination: false,
  searchInfo:pageParameter
})
const [registerTable2] = useTable({
  bordered: true,
  showIndexColumn: false,
  pagination: false,
})
const checkRow: any = ref([])
const modalOrgArr: any = ref([])

const onSelectChange = (selectedRowKeys, row) => {
  state.selectedRowKeys = selectedRowKeys;
  checkRow.value = row
};

async function modalSubmit() {
  if(checkRow.value.length==0){
    return createWarningModal({ content: '请选择需要引入的数据！' });
  }

  let data1=[]
  modalOrgArr.value.forEach(async (orgArr)=>{
    checkRow.value[0].originUnique=orgArr
    checkRow.value[0].isDel='0'
    checkRow.value[0].successState='1'
    checkRow.value[0].applyUser=useUserStoreWidthOut().getUserInfo.name
    checkRow.value[0].applyDate=getNowDate()
    checkRow.value[0].assignUserId=useUserStoreWidthOut().getUserInfo.id
    checkRow.value[0].assignDate=getNowDate()
    checkRow.value[0].acceptUserId=useUserStoreWidthOut().getUserInfo.id
    checkRow.value[0].acceptDate=getNowDate()
    checkRow.value[0].dataUnique=checkRow.value[0].uniqueCode

    data1.push(checkRow.value[0])

    let cusClassUnique:any=[]
    // 所有集团客户分类
    let map={
      searchValue: '',
      superid: '',
      page: '1',
      size: '10000000',
    }
    let dataSource=await findAllCustomerClass(map)
    // 分配给组织的客户分类
    let assignData=await findByOrgCusClassAssigAccount(orgArr,'1')
    // 集团客户档案分配表数据
    let groupAssignData:any=[]
    // 所属分类是否分配给组织：没有 查询分类 一并分配
    let findByOrgCusLcass= assignData.filter(cl=>cl.dataUnique==checkRow.value[0].uniqueCustclass)
    if(findByOrgCusLcass.length==0){
      let aa=dataSource.items.filter(f=>f.uniqueCustclass==checkRow.value[0].uniqueCustclass)
      cusClassUnique.push(aa[0])
    }

    checkRow.value[0].dataUnique=checkRow.value[0].uniqueCode
    checkRow.value[0].orgUnique=orgArr
    checkRow.value[0].flag='1'
    checkRow.value[0].ctype='1'
    checkRow.value[0].assignUserId=useUserStoreWidthOut().getUserInfo.id
    checkRow.value[0].assignDate=getNowDate()
    checkRow.value[0].acceptUserId=useUserStoreWidthOut().getUserInfo.id
    checkRow.value[0].acceptDate=getNowDate()
    groupAssignData.push(checkRow.value[0])
    //****************** 查找分类 ****************
    let data:any=[]
    cusClassUnique.forEach(a=>{
      if(a.cusClassGrade=='1'){
        data.push(a)
      }
      else{
        if(a.cusClassGrade=='2'){
          // 2找1级
          let temp= dataSource.items.filter(b=>b.uniqueCustclass==a.parentId)
          let t=assignData.filter(c=>c.dataUnique==temp[0].uniqueCustclass)
          if(t.length==0){
            data.push(temp[0])
          }
        }
        else if(a.cusClassGrade=='3'){
          // 3找2级
          let temp= dataSource.items.filter(b=>b.uniqueCustclass==a.parentId)
          let t=assignData.filter(c=>c.dataUnique==temp[0].uniqueCustclass)
          if(t.length==0){
            data.push(temp[0])
          }
          // 2找1级
          let temp2= dataSource.items.filter(b=>b.uniqueCustclass==temp[0].parentId)
          let t2=assignData.filter(c=>c.dataUnique==temp2[0].uniqueCustclass)
          if(t2.length==0){
            data.push(temp2[0])
          }
        }
      }
    })
    // 往组织客户分类表添加
    let orgCusClassAssignArr= data.concat(cusClassUnique)
    if(orgCusClassAssignArr.length>0){
      orgCusClassAssignArr.forEach(async (a)=>{
        a.dataUnique=a.uniqueCustclass
        a.id=null
        a.orgUnique=orgUnique.value
        a.flag='1'
        a.ctype='1'
        a.assignUserId=useUserStoreWidthOut().getUserInfo.id
        a.assignDate=getNowDate()
        a.acceptUserId=useUserStoreWidthOut().getUserInfo.id
        a.acceptDate=getNowDate()

      })
      // 添加集团客户分类分配表
      let map={ list:JSON.stringify([...new Set(orgCusClassAssignArr)])}
      await assignOrgSave(map)

      // 往组织客户分类表添加
      await orgCusClassSave(orgCusClassAssignArr)
    }
    //****************** 查找分类END ****************
  })

  console.log(data1)
  data1.forEach(async (data)=>{
    data.id=null
    // 集团客户信息分配记录表
    await GroupCustomerAddApi(data).then(async (a)=>{
      /************** 记录操作日志 ****************/
      let map={
        loginTime:new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," "),
        userId:useUserStoreWidthOut().getUserInfo.username,
        userName:useUserStoreWidthOut().getUserInfo.realName,
        optModule:'group',
        optFunction:'客户',
        optRange:'2',
        uniqueCode:orgUnique.value,
        optAction:'新增',
        optContent:'操作内容【新增客户信息】,客户编码【'+a.custCode+'】,客户全称【'+a.custName+'】,客户简称【'+a.custAbbname+'】',
      }
      // 集团不存在的需要加日志
      if(data.id==undefined){
        await saveLog(map)
      }
      /************** 记录操作日志 End ****************/
    })
  })
  // 添加到组织客户信息表
  await originCustomerSaveAll(data1).then(async (a)=>{
    /************** 记录操作日志 ****************/
    let map={
      loginTime:new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," "),
      userId:useUserStoreWidthOut().getUserInfo.username,
      userName:useUserStoreWidthOut().getUserInfo.realName,
      optModule:'org',
      optFunction:'客户',
      optRange:'0',
      uniqueCode:orgUnique.value,
      optAction:'引入',
      optContent:'操作内容【引入客户信息】,客户编码【'+a.custCode+'】,客户全称【'+a.custName+'】,客户简称【'+a.custAbbname+'】',
    }
    await saveLog(map)
    /************** 记录操作日志 End ****************/
  })
  visibleModel2.value=false
  reload()
}
async function applyNoSubmit() {
  if(applyNoReason.value==''){
    return createWarningModal({ content: '驳回原因不能为空!' });
  }
  getSelectRows().forEach(a=>{
    a.successState='-1'
    a.approveUser=''
    a.approveDate=''
    a.reason=applyNoReason.value
  })
  await editGroupCusAll(getSelectRows()).then(async (a)=>{
    /************** 记录操作日志 ****************/
    let map={
      loginTime:new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," "),
      userId: useUserStoreWidthOut().getUserInfo.username,
      userName: useUserStoreWidthOut().getUserInfo.realName,
      optModule:'group',
      optFunction:'客户',
      optRange:'2',
      optAction:'生效审批',
      optContent:'操作内容【驳回】,客户编码【'+a.custCode+'】,客户全称【'+a.custName+'】,客户简称【'+a.custAbbname+'】',
    }
    await saveLog(map)
    /************** 记录操作日志 END ****************/
  })
  visibleModel.value=false
  reload()
}
function applyNo() {
  visibleModel.value=true
}
async function applyOk() {
  if(getSelectRows().length==0){
    return createWarningModal({ content: '至少选择一条数据进行同意' });
  }
  createConfirm({
    iconType: 'warning',
    title: '提示',
    content: '确定批量同意勾选的数据吗?',
    onOk: async () => {
      let tenantId=''
      for (let i = 0; i < getSelectRows().length; i++) {
        getSelectRows()[i].successState='1'
        getSelectRows()[i].approveUser=useUserStoreWidthOut().getUserInfo.username
        getSelectRows()[i].approveDate=new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," ")
        getSelectRows()[i].dataUnique=getSelectRows()[i].uniqueCode

        // 根据组织查询生效数据是否重复
        let temp=await findAllByGroupCusNameAndCustAbbnameLike(getSelectRows()[i].custName,getSelectRows()[i].custAbbname)
        if(temp.length==0){
          await editByIdSuccessState(getSelectRows()[i].id,useUserStoreWidthOut().getUserInfo.realName).then(async ()=>{
            /************** 记录操作日志 ****************/
            let map={
              loginTime:new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," "),
              userId: useUserStoreWidthOut().getUserInfo.username,
              userName: useUserStoreWidthOut().getUserInfo.realName,
              optModule:'group',
              optFunction:'客户',
              optRange:'2',
              optAction:'审批',
              optContent:'操作内容【审批同意】,客户编码【'+getSelectRows()[i].custCode+'】,客户全称【'+getSelectRows()[i].custName+'】,客户简称【'+getSelectRows()[i].custAbbname+'】',
            }
            await saveLog(map)
            /************** 记录操作日志 END ****************/

            getSelectRows()[i].assignUserId=useUserStoreWidthOut().getUserInfo.id
            getSelectRows()[i].assignDate=getNowDate()
            getSelectRows()[i].acceptUserId=useUserStoreWidthOut().getUserInfo.id
            getSelectRows()[i].acceptDate=getNowDate()
            getSelectRows()[i].orgUnique=getSelectRows()[i].originUnique
            getSelectRows()[i].id=null
            // 集团客户信息分配记录表
            await groupCusAssignOrgSave([getSelectRows()[i]]).then(async (tx)=>{
              /************** 记录操作日志 ****************/
              let map={
                loginTime:new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," "),
                userId: useUserStoreWidthOut().getUserInfo.username,
                userName: useUserStoreWidthOut().getUserInfo.realName,
                optModule:'group',
                optFunction:'客户',
                optRange:'2',
                optAction:'分配',
                optContent:'操作内容【分配给组织客户】,客户编码【'+getSelectRows()[i].custCode+'】,客户全称【'+getSelectRows()[i].custName+'】,客户简称【'+getSelectRows()[i].custAbbname+'】',
              }
              await saveLog(map)
              /************** 记录操作日志 END ****************/
            })

            // 是账套申请的
            if(getSelectRows()[i].ctype=='2'){
              tenantId=getSelectRows()[i].tenantId+'-'+getSelectRows()[i].accStartDate.split('-')[0]

              // 往组织客户信息表增加
              getSelectRows()[i].tenantId=tenantId
              await originCustomerSaveAll([getSelectRows()[i]]).then(async ()=>{
                /************** 记录操作日志 ****************/
                let map={
                  loginTime:new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," "),
                  userId: useUserStoreWidthOut().getUserInfo.username,
                  userName: useUserStoreWidthOut().getUserInfo.realName,
                  optModule:'org',
                  optFunction:'客户',
                  optRange:'0',
                  optAction:'新增',
                  optContent:'操作内容【新增客户信息】,客户编码【'+getSelectRows()[i].custCode+'】,客户全称【'+getSelectRows()[i].custName+'】,客户简称【'+getSelectRows()[i].custAbbname+'】',
                }
                await saveLog(map)
                /************** 记录操作日志 END ****************/
              })
              // 往账套添加
              await useRouteApi(duLiSaveApi,{schemaName: tenantId})(getSelectRows()[i]).then(async ()=>{
                /************** 记录操作日志 ****************/
                let map={
                  loginTime:new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," "),
                  userId: useUserStoreWidthOut().getUserInfo.username,
                  userName: useUserStoreWidthOut().getUserInfo.realName,
                  optModule:'master_data',
                  optFunction:'客户',
                  optRange:'1',
                  optAction:'新增',
                  optContent:'操作内容【新增客户信息】,客户编码【'+getSelectRows()[i].custCode+'】,客户全称【'+getSelectRows()[i].custName+'】,客户简称【'+getSelectRows()[i].custAbbname+'】',
                }
                await saveLog(map)
                /************** 记录操作日志 END ****************/
              })

              getSelectRows()[i].ctype='1'
              getSelectRows()[i].tenantId=null
              // 集团客户信息分配记录表
              await groupCusAssignOrgSave([getSelectRows()[i]]).then(async (tx)=>{
                /************** 记录操作日志 ****************/
                let map={
                  loginTime:new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," "),
                  userId: useUserStoreWidthOut().getUserInfo.username,
                  userName: useUserStoreWidthOut().getUserInfo.realName,
                  optModule:'group',
                  optFunction:'客户',
                  optRange:'2',
                  optAction:'分配',
                  optContent:'操作内容【分配给组织客户】,客户编码【'+getSelectRows()[i].custCode+'】,客户全称【'+getSelectRows()[i].custName+'】,客户简称【'+getSelectRows()[i].custAbbname+'】',
                }
                await saveLog(map)
                /************** 记录操作日志 END ****************/
              })
            }
            else{
              // 往组织客户信息表增加
              await originCustomerSaveAll([getSelectRows()[i]]).then(async ()=>{
                /************** 记录操作日志 ****************/
                let map={
                  loginTime:new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," "),
                  userId: useUserStoreWidthOut().getUserInfo.username,
                  userName: useUserStoreWidthOut().getUserInfo.realName,
                  optModule:'org',
                  optFunction:'客户',
                  optRange:'0',
                  optAction:'新增',
                  optContent:'操作内容【新增客户信息】,客户编码【'+getSelectRows()[i].custCode+'】,客户全称【'+getSelectRows()[i].custName+'】,客户简称【'+getSelectRows()[i].custAbbname+'】',
                }
                await saveLog(map)
                /************** 记录操作日志 END ****************/
              })
            }
          })
        }
        else{
          createConfirm({
            iconType: 'warning',
            title: '提示',
            content: '客户全称【' + getSelectRows()[i].custName + '】,客户简称【' + getSelectRows()[i].custAbbname + '】。在集团中已存在,请选择引用',
            onOk: async () => {
              // 同组织客户全称、客户简称重复删除
              await delByIdCustNameAndCustAbbnameAndSuccessState(getSelectRows()[i].custName,getSelectRows()[i].custAbbname,'0')
              // 修改集团假删除状态
              temp[0].isDel='0'
              await GroupCustomerAddApi(temp[0])

              getSelectRows()[i].assignUserId=useUserStoreWidthOut().getUserInfo.id
              getSelectRows()[i].assignDate=getNowDate()
              getSelectRows()[i].acceptUserId=useUserStoreWidthOut().getUserInfo.id
              getSelectRows()[i].acceptDate=getNowDate()
              getSelectRows()[i].orgUnique=getSelectRows()[i].originUnique
              getSelectRows()[i].dataUnique=temp[0].uniqueCode
              getSelectRows()[i].uniqueCode=temp[0].uniqueCode
              getSelectRows()[i].id=null
              // 集团客户信息分配记录表
              await groupCusAssignOrgSave([getSelectRows()[i]]).then(async (tx)=>{
                /************** 记录操作日志 ****************/
                let map={
                  loginTime:new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," "),
                  userId: useUserStoreWidthOut().getUserInfo.username,
                  userName: useUserStoreWidthOut().getUserInfo.realName,
                  optModule:'group',
                  optFunction:'客户',
                  optRange:'2',
                  optAction:'分配',
                  optContent:'操作内容【分配给组织客户】,客户编码【'+getSelectRows()[i].custCode+'】,客户全称【'+getSelectRows()[i].custName+'】,客户简称【'+getSelectRows()[i].custAbbname+'】',
                }
                await saveLog(map)
                /************** 记录操作日志 END ****************/
              })
              // 是账套申请的
              if(getSelectRows()[i].ctype=='2'){
                tenantId=getSelectRows()[i].tenantId+'-'+getSelectRows()[i].accStartDate.split('-')[0]
                // 往组织客户信息表增加
                getSelectRows()[i].tenantId=tenantId
                getSelectRows()[i].custCode=temp[0].custCode
                await originCustomerSaveAll([getSelectRows()[i]]).then(async ()=>{
                  /************** 记录操作日志 ****************/
                  let map={
                    loginTime:new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," "),
                    userId: useUserStoreWidthOut().getUserInfo.username,
                    userName: useUserStoreWidthOut().getUserInfo.realName,
                    optModule:'org',
                    optFunction:'客户',
                    optRange:'0',
                    optAction:'新增',
                    optContent:'操作内容【新增客户信息】,客户编码【'+getSelectRows()[i].custCode+'】,客户全称【'+getSelectRows()[i].custName+'】,客户简称【'+getSelectRows()[i].custAbbname+'】',
                  }
                  await saveLog(map)
                  /************** 记录操作日志 END ****************/
                })
                // 往账套添加
                await useRouteApi(duLiSaveApi,{schemaName: tenantId})(getSelectRows()[i]).then(async ()=>{
                  /************** 记录操作日志 ****************/
                  let map={
                    loginTime:new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," "),
                    userId: useUserStoreWidthOut().getUserInfo.username,
                    userName: useUserStoreWidthOut().getUserInfo.realName,
                    optModule:'master_data',
                    optFunction:'客户',
                    optRange:'1',
                    optAction:'新增',
                    optContent:'操作内容【新增客户信息】,客户编码【'+getSelectRows()[i].custCode+'】,客户全称【'+getSelectRows()[i].custName+'】,客户简称【'+getSelectRows()[i].custAbbname+'】',
                  }
                  await saveLog(map)
                  /************** 记录操作日志 END ****************/
                })
              }
              else{
                // 往组织客户信息表增加
                await originCustomerSaveAll([getSelectRows()[i]]).then(async ()=>{
                  /************** 记录操作日志 ****************/
                  let map={
                    loginTime:new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," "),
                    userId: useUserStoreWidthOut().getUserInfo.username,
                    userName: useUserStoreWidthOut().getUserInfo.realName,
                    optModule:'org',
                    optFunction:'客户',
                    optRange:'0',
                    optAction:'新增',
                    optContent:'操作内容【新增客户信息】,客户编码【'+getSelectRows()[i].custCode+'】,客户全称【'+getSelectRows()[i].custName+'】,客户简称【'+getSelectRows()[i].custAbbname+'】',
                  }
                  await saveLog(map)
                  /************** 记录操作日志 END ****************/
                })
              }
              reload()
            }
          })
          break
        }
      }
      reload()
    }
  })
}
function deWeight(arr) {
  for (var i = 0; i < arr.length - 1; i++) {
    for (var j = i + 1; j < arr.length; j++) {
      if (arr[i].custName == arr[j].custName && arr[i].custAbbname == arr[j].custAbbname) {
        arr.splice(j, 1);
        //因为数组长度减小1，所以直接 j++ 会漏掉一个元素，所以要 j--
        j--;
      }
    }
  }
  return arr;
}
function openLook() {
  if(getSelectRows().length>1||getSelectRows().length==0){
    return createWarningModal({ content: '请选择一条数据进行查看' });
  }
  openPageData(getSelectRows()[0])
}
async function openPageData(data) {
  /************** 记录操作日志 ****************/
  let map={
    loginTime:new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," "),
    userId: useUserStoreWidthOut().getUserInfo.username,
    userName: useUserStoreWidthOut().getUserInfo.realName,
    optModule:'group',
    optFunction:'客户',
    optRange:'2',
    optAction:'生效审批',
    optContent:'操作内容【点击生效审批-查看】,客户编码【'+data.custCode+'】,客户全称【'+data.custName+'】,客户简称【'+data.custAbbname+'】',
  }
  await saveLog(map)
  /************** 记录操作日志 END ****************/
  openEditPage(true, {
    data: data,
    type:'look',
    isEdit:false
  });
}
function fetchData(data) {
  let temp=data.items.filter(a=>a.successState!=='1')
  setTableData(temp)
  // 底部分页信息
  setPagination({total: temp.length})
}
// 组织客户分类
async function findByOrgClass() {
  let map={
    searchValue: '',
    superid: '',
    orgUnique: orgUnique.value,
  }
  let temp=await findAllOrgCusClass(map)
  return temp.items.length
}

function delList() {
  if(getSelectRows().length==0){
    return createWarningModal({content:'请选择需要删除的数据'})
  }
  createConfirm({
    iconType: 'warning',
    title: '提示',
    content: '是否删除选中数据,确定继续吗？',
    onOk: async () => {
      await delCusArrGroup(getSelectRows().map(a=>a.id).join(',')).then(()=>{
        reload()
      })
      // 删除日志
      getSelectRows().forEach(async (a)=>{
        let map={
          loginTime:new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," "),
          userId: useUserStoreWidthOut().getUserInfo.username,
          userName: useUserStoreWidthOut().getUserInfo.realName,
          optModule:'group',
          optFunction:'客户',
          optRange:'2',
          optAction:'生效审批',
          optContent:'操作内容【点击生效审批-删除】,客户编码【'+a.custCode+'】,客户全称【'+a.custName+'】,客户简称【'+a.custAbbname+'】',
        }
        await saveLog(map)
      })
    }
  })
}

// 当前时间
function getNowDate() {
  return new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," ");
}
async function handleOk() {
  /************** 记录操作日志 ****************/
  let map={
    loginTime:new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," "),
    userId: useUserStoreWidthOut().getUserInfo.username,
    userName: useUserStoreWidthOut().getUserInfo.realName,
    optModule:'group',
    optFunction:'客户',
    optRange:'2',
    optAction:'生效审批',
    optContent:'操作内容【关闭生效审批-查看】',
  }
  await saveLog(map)
  /************** 记录操作日志 ****************/
  emit('save', '');
  closeModal();
}
</script>
<style>
.vben-basic-title {
  color: #0096c7 !important;
  border:none !important;
}

.ant-modal-body {
  padding: 0px;
  border-bottom: 1px solid rgb(1, 129, 226);
  border-left: none;
  border-right: none;
  border-top: none !important;
}
.ant-modal-header{
  border: none !important;
}
</style>
<style scoped lang="less">
input {
  width: 50%;
  border: none !important;
  border-bottom: 1px solid #bdb9b9 !important;
  font-weight: bold;
  font-size: 14px;
}

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

.nc-open-content {
  input:not(.ant-select-selection-search-input,.ant-input){
    width: 50%;
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
  }

  .ant-input:focus {
    box-shadow: none;
  }

  .ant-select-selector {
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
  }

  label {
    text-align: left;
    width: 110px;
    display: inline-block;
    padding-top: 5px;
    padding-bottom: 5px;
    color: #535353;
  }
}

.search input {
  width: 100%;
  border: none !important;
}

.tables :deep(td),
.tables :deep(th) {
  font-size: 14px !important;
  padding: 2px 8px !important;
}

.tables :deep(th) {
  text-align: center !important;
  font-weight: bold;
  background-color: #cccccc;
  line-height:30px;
}

.bg-white{
  width: 250px !important;
  min-height: 385px !important;
  height: calc(100%);
  border: 1px #cccccc solid;
  background:white !important;
}

:deep(.ant-table-row-selected) td{
  background: #0096c7 !important;
  a {
    color: #ffffff;
  }
}

:deep(.ant-tree-list){
  margin: 0 !important;
  padding: 0 !important;
}

.a-table-font-size-12 :deep(td),
.a-table-font-size-12 :deep(th) {
  font-size: 14px !important;
  padding: 4px 8px !important;
}

</style>
<style>
.head-title .scroll-container .scrollbar__wrap {
  margin-bottom: -45px !important;
}

</style>

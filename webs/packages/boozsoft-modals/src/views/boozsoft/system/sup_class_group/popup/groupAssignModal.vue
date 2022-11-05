<template>
  <BasicModal
    width="900px"
    :minHeight="500"
    :height="500"
    v-bind="$attrs"
    title="集团-供应商分类分配"
    :showOkBtn="false"
    :closable="false"
    @ok="handleOk()"
    @register="register"
  >
    <template #title>
      <div style="display: flex;" class="vben-basic-title">
        <span style="line-height: 25px;font-size: 20px;">
          <SearchOutlined style="width:25px;margin-right: 10px;"/>
          集团-供应商分类分配
        </span>
      </div>
    </template>
    <div
      style="background-color: #158eb8;height: 50px;padding-top: 10px;padding-left:10px;border-radius: 5px;">
      <span style="color: white;">组织：</span>
        <a-select
          v-model:value="orgUnique"
          option-filter-prop="children"
          show-search
          style="width: 25%"
          @change="orgChange"
        >
          <a-select-option
            v-for="item in orientationlist"
            :key="item.uniqueCode"
            :value="item.uniqueCode"
          >
            {{ item.orgName }}
          </a-select-option>
          <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
        </a-select>
      <div style="float: right;margin-right: 5px;">
        <button type="button" class="ant-btn ant-btn-me" @click="handleOk" v-if="tabsflag=='0'"><span>分配</span></button>
        <button type="button" class="ant-btn ant-btn-me" @click="delGiveOrgData" v-else><span>取消分配</span></button>
        <button type="button" class="ant-btn ant-btn-me" @click="reload"><span>刷新</span></button>
      </div>
    </div>

    <div style="display: inline;width: 11%;height:400px;float: left;margin-top: 5px;">
      <a-tabs v-model:activeKey="tabsflag" @change="tabsCheck" tab-position="left">
        <a-tab-pane v-for="(item, i) in typylist" :key="item.value" :tab="`${item.title}`"/>
      </a-tabs>
    </div>
    <div style="display: inline;width: 89%;float: right;margin-top: 5px;">
      <BasicTable @fetch-success="finByOrgAssign" @register="registerTable" :scroll="{ y: 400 }"
                  :class="'a-table-font-size-12'" >
        <template #cusCclassName="{ record }">
          {{ record.cusCclassName }}
        </template>
        <template #flag="{ record }">
          <a-tag :color="record.flag == '1' ? 'green' : 'volcano'">
            {{ record.flag == '1' ? '已引入' : '未引入' }}
          </a-tag>
        </template>
      </BasicTable>
    </div>
  </BasicModal>
</template>
<script setup="props, { content }" lang="ts">
import {BasicTable, useTable} from '/@/components/Table';
import {SearchOutlined,CaretDownOutlined,QuestionCircleOutlined} from '@ant-design/icons-vue';
import {onMounted, reactive, ref, toRaw, unref} from 'vue';
import {BasicModal, useModal, useModalInner} from '/@/components/Modal';
import {
  Tag as ATag,
  Tabs as ATabs,
  Select as ASelect,
  Form as AForm,
  Input as AInput,
  Statistic as AStatistic,
} from 'ant-design-vue';
import {getCurrentAccountName} from "/@/api/task-api/tast-bus-api";
import {useMessage} from "/@/hooks/web/useMessage";
import {
  assignOrgSave, delByCtypeAndOrgUnique, delByCtypeAndOrgUniqueAndFlag,
  findAllCustomerClass,
  findByOrgAssigAccount} from "/@/api/record/system/supplier_class_group";
import {getOrganizeList} from "/@/api/record/group/im-organize";
import {useUserStoreWidthOut} from "/@/store/modules/user";
import {saveAll} from "/@/api/org/sup_class/org_sup_class";
import {findByUserIdAndOriginId} from "/@/api/record/system/group-permission";
import {saveLog} from "/@/api/record/system/group-sys-login-log";


const ASelectOption = ASelect.Option;
const AInputSearch = AInput.Search;
const AStatisticCountdown = AStatistic.Countdown;
const AFormItem = AForm.Item;
const ATabPane = ATabs.TabPane;

const typylist=reactive([
  {title:'已分配',value:'1'},
  {title:'待分配',value:'0'},
])
const tabsflag = ref('1');

const {createSuccessModal,createWarningModal,createConfirm} = useMessage()
const emit = defineEmits(['register']);
const database = ref(getCurrentAccountName(true));
const accId = ref('');
const loading = ref(true);
const treeData: any = ref([]);
const selectedKeys2: any = ref([]);
const expandedKeys: any = ref([]);
const dataSource: any = ref([]);
const columns = [
  {
    title: '分类编码',
    dataIndex: 'cusClass',
    key: 'cusClass',
    ellipsis: true,
    align: 'left',
    width: 150
  },
  {
    title: '分类名称',
    dataIndex: 'cusCclassName',
    key: 'cusCclassName',
    align: 'left', slots: {customRender: 'cusCclassName'},
    ellipsis: true
  }, {
    title: '上级名称',
    dataIndex: 'superClassName',
    key: 'superClassName',
    ellipsis: true, align: 'left',
  }
];
const columns2 = [
  {
    title: '分类编码',
    dataIndex: 'cusClass',
    key: 'cusClass',
    ellipsis: true,
    align: 'left',
    width: 150
  },
  {
    title: '分类名称',
    dataIndex: 'cusCclassName',
    key: 'cusCclassName',
    align: 'left', slots: {customRender: 'cusCclassName'},
    ellipsis: true
  }, {
    title: '上级名称',
    dataIndex: 'superClassName',
    key: 'superClassName',
    ellipsis: true, align: 'left',
  }, {
    title: '使用状态',
    dataIndex: 'flag',
    key: 'flag',
    ellipsis: true,width: 80, slots: {customRender: 'flag'},
  }
];
const orgAssignAccountData: any = ref([]);
const orgAssignData: any = ref([]);
const orientationlist: any = ref([]);
const orgUnique: any = ref('');
// 是否强制添加到组织供应商分类表-1自动分配,0不是
const orgSaveFlag: any = ref('0');


const pageParameter = reactive({
  searchValue: '',
  superid: '',
  page: '1',
  size: '10000000',
})

// 这是示例组件
const [registerTable, {reload, getSelectRows, getDataSource, setTableData,setColumns,clearSelectedRowKeys,setSelectedRowKeys,setPagination}] = useTable({
  bordered: true,
  rowSelection: { type: 'checkbox' },
  pagination: {
    pageSize: 50,
    showSizeChanger: true,
    pageSizeOptions: ['50', '100'],
    showTotal: t => `总共${t}条数据`
  },
  searchInfo: pageParameter
});
const [register, {closeModal}] = useModalInner(async (data) => {
  tabsflag.value='1'
  // 组织列表
  const oration = await getOrganizeList('');
  orientationlist.value = oration.items;
  if(orgUnique.value==''){
    orgUnique.value=orientationlist.value[0].uniqueCode
  }

  findByOrgPermission()
  clearSelectedRowKeys()
  findByGiveOrgData()
});

// 集团档案权限分配表-集团供应商分类
async function findByOrgPermission() {
  // 集团档案权限分配表-集团供应商分类
  let findByPermission=await findByUserIdAndOriginId(useUserStoreWidthOut().getUserInfo.id,orgUnique.value,'supplier_class')
  if(findByPermission.length>0){
    orgSaveFlag.value=findByPermission[0].isAuto
  }
}

// 分配给组织的
async function findByGiveOrgData() {
  setColumns(columns2)
  let dataSource=await findAllCustomerClass(pageParameter)
  orgAssignData.value=await findByOrgAssigAccount(orgUnique.value,'1')
  if(orgAssignData.value.length>0){
    // 已分配给组织
    let arr=getArrDifSameValue(orgAssignData.value,dataSource.items.filter(a=>a.cusBend=='1'))
    setTableData(arr)
  }
}
// 没分配给组织的
async function findByNoGiveOrgData() {
  let dataSource=await findAllCustomerClass(pageParameter)
  // 过滤已分配的
  let temp=await findByOrgAssigAccount(orgUnique.value,'1')
  setTableData(dataSource.items.filter(a=>a.cusBend=='1'&&temp.map(a=>a.dataUnique).indexOf(a.uniqueCustclass)==-1))
}

// 按组织查询集团分配给组织的数据
async function finByOrgAssign() {
  findByGiveOrgData()
}

function getArrDifSameValue(arr1,arr2){
  var result:any = [];
  for(var i = 0; i < arr2.length; i++){
    var obj:any = arr2[i];
    var id = obj.uniqueCustclass;
    var isExist = false;
    for(var j = 0; j < arr1.length; j++){
      var aj = arr1[j];
      var n = aj.dataUnique;
      if(n == id){
        isExist = true;
        break;
      }
    }
    // !isExist 去除已有的。isExist 获取已有的
    if(isExist){
      obj.flag=aj.flag
      obj.tenantId=aj.tenantId
      result.push(obj);
    }
  }
  return result;
}

async function handleOk() {
  if(getSelectRows().length==0){
    warningModal('请选择供应商分类')
    return false
  }

  createConfirm({
    iconType: 'warning',
    title: '提示',
    content: '确定继续分配吗?',
    onOk: async () => {
      let dataSource=await findAllCustomerClass(pageParameter)
      // 查询已经分配的数据
      let assignData=await findByOrgAssigAccount(orgUnique.value,'1')
      let data:any=[]
      getSelectRows().forEach(a=>{
        if(a.cusClassGrade=='1'){
          data.push(a)
        }else{
          if(a.cusClassGrade=='2'){
            // 2找1级
            let temp= dataSource.items.filter(b=>b.uniqueCustclass==a.parentId)
            let t=assignData.filter(c=>c.dataUnique==temp[0].uniqueCustclass)
            if(t.length==0){
              data.push(temp[0])
            }
          }else if(a.cusClassGrade=='3'){
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
      let arr=  data.concat(getSelectRows())
      // 分配表
      arr.forEach(async (a)=>{
        a.dataUnique=a.uniqueCustclass
        a.id=null
        a.orgUnique=orgUnique.value
        a.flag=orgSaveFlag.value
        a.ctype='1'
        a.assignUserId=useUserStoreWidthOut().getUserInfo.id
        a.assignDate=getNowDate()
        a.acceptUserId=useUserStoreWidthOut().getUserInfo.id
        a.acceptDate=getNowDate()
      })
      // 添加集团供应商分类分配表
      let map={ list:JSON.stringify([...new Set(arr)])}
      await assignOrgSave(map)
      // 埋点-操作日志
      for (let i = 0; i < [...new Set(arr)].map(a=>a.cusCclassName).length; i++) {
        let log='【集团供应商分类分配】,分类名称【'+[...new Set(arr)].map(a=>a.cusCclassName)[i]+'】'
        /************** 记录操作日志 ****************/
        let logmap={
          loginTime:new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," "),
          userId: useUserStoreWidthOut().getUserInfo.username,
          userName: useUserStoreWidthOut().getUserInfo.realName,
          optModule:'group',
          optFunction:'供应商分类',
          optRange:'2',
          uniqueCode:orgUnique.value,
          optAction:'分配',
          optContent:log,
        }
        await saveLog(logmap)
        /************** 记录操作日志 ****************/
      }
      // 直接添加到组织供应商分类表
      if(orgSaveFlag.value==='1'){
        await saveAll(arr)
        // 埋点-操作日志
        for (let i = 0; i < [...new Set(arr)].map(a=>a.cusCclassName).length; i++) {
          let log='【集团设置自动分配-组织供应商分类分配】,分类名称【'+[...new Set(arr)].map(a=>a.cusCclassName)[i]+'】'
          /************** 记录操作日志 ****************/
          let logmap={
            loginTime:new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," "),
            userId: useUserStoreWidthOut().getUserInfo.username,
            userName: useUserStoreWidthOut().getUserInfo.realName,
            optModule:'org',
            optFunction:'供应商分类',
            optRange:'0',
            uniqueCode:orgUnique.value,
            optAction:'分配',
            optContent:log,
          }
          await saveLog(logmap)
          /************** 记录操作日志 ****************/
        }
        // 所属组织的账套
        // let orgAccount=await findAllByAccGroup(orgUnique.value)
        // console.log(orgAccount)
      }
      createSuccessModal({
        iconType: 'success',
        title: '提示',
        content: '分配成功',
        onOk: async () => {
          findByNoGiveOrgData()
        }
      })
      clearSelectedRowKeys()
    }
  })
}

async function delGiveOrgData() {
  if(getSelectRows().length==0){
    warningModal('请选择供应商分类')
    return false
  }
  createConfirm({
    iconType: 'warning',
    title: '提示',
    content: '确定取消分配吗?',
    onOk: async () =>{
      let a=0
      for (let i = 0; i < getSelectRows().length; i++) {
        if(getSelectRows()[i].flag=='1'){
          a++
          warningModal('供应商分类已被引用,不能取消分配!!')
          break;
        }
      }
      if(a==0){
        let dataSource=await findAllCustomerClass(pageParameter)
        dataSource=dataSource.items
        let a1:any=[]
        let a2:any=[]
        let a3:any=[]
        getSelectRows().forEach(w=>{
          if(w.cusClassGrade=='1'){
            a1.push(w.uniqueCustclass)
          }
          else if(w.cusClassGrade=='2'){
            a2.push(w.uniqueCustclass)
            // 过滤出一级
            let yicode=w.cusGradeCode.substring(0,3)
            // 选中的2级
            let v2= getSelectRows().filter(v2=>v2.cusClassGrade=='2'&&v2.cusGradeCode.substring(0,3)==yicode)
            // 分配2级条数
            let datav2= getDataSource().filter(v=>v.cusGradeCode.substring(0,3)==yicode&&v.cusBend=='1'&&v.cusClassGrade=='2')
            if(v2.length==datav2.length){
              // 3级是否分配在列表中，存在 不删除一级
              let findby3ji=getDataSource().filter(v=>v.cusGradeCode.substring(0,3)==yicode&&v.cusBend=='1'&&v.cusClassGrade=='3')
              if(findby3ji.length==0){
                let findby1ji=dataSource.filter(v=>v.cusGradeCode.substring(0,3)==yicode&&v.cusClassGrade=='1')
                a1.push(findby1ji[0].uniqueCustclass)
              }
            }
          }
          else if(w.cusClassGrade=='3'){
            a3.push(w.uniqueCustclass)
            // 过滤出一级
            let yicode=w.cusGradeCode.substring(0,3)
            // 选中的3级
            let v3= getSelectRows().filter(v2=>v2.cusClassGrade=='3')
            // 分配3级条数
            let datav3= getDataSource().filter(v=>v.cusGradeCode.substring(0,3)==yicode&&v.cusBend=='1'&&v.cusClassGrade=='3')
            if(v3.length==datav3.length){
              // 末级2级是否分配在列表中，存在 不删除一级
              let findby2ji=getDataSource().filter(v=>v.cusGradeCode.substring(0,3)==yicode&&v.cusBend=='1'&&v.cusClassGrade=='2')
              if(findby2ji.length==0){
                // 删除一级
                let findby1ji=dataSource.filter(v=>v.cusGradeCode.substring(0,3)==yicode&&v.cusClassGrade=='1')
                a1.push(findby1ji[0].uniqueCustclass)
              }
              // 找出上级进行删除
              let findbybend02ji=dataSource.filter(v=>v.cusGradeCode.substring(0,3)==yicode&&v.cusBend=='0'&&v.cusClassGrade=='2')
              a2.push(findbybend02ji[0].uniqueCustclass)
            }
            // 选中的2级
            let v2= getSelectRows().filter(v2=>v2.cusClassGrade=='2')
            // 分配2级条数
            let datav2= getDataSource().filter(v=>v.cusGradeCode.substring(0,3)==yicode&&v.cusBend=='1'&&v.cusClassGrade=='2')
            if(v2.length==datav2.length){
              v2.forEach(aa=>{
                a2.push(aa.uniqueCustclass)
              })
              // 删除一级
              let findby1ji=dataSource.filter(v=>v.cusGradeCode.substring(0,3)==yicode&&v.cusClassGrade=='1')
              a1.push(findby1ji[0].uniqueCustclass)
            }
          }
        })

        // console.log(a1,'1')
        // console.log(a2,'2')
        // console.log(a3,'3')
        let arr:any=[]
        // 选中与分配条数一样,全删
        if(getSelectRows().length==getDataSource().length){
          await delByCtypeAndOrgUniqueAndFlag(orgUnique.value,'1','0').then(()=>{
            createSuccessModal({
              iconType: 'success',
              title: '提示',
              content: '取消分配成功',
              onOk: async () => {
                setTableData([])
                findByGiveOrgData()
              }
            })
            clearSelectedRowKeys()
          })
        }
        else{
          arr=[...new Set(a1)].concat([...new Set(a2)]).concat([...new Set(a3)])
          // console.log(arr)
          let map2={ orgUnique:orgUnique.value,orgDelList:arr}
          await delByCtypeAndOrgUnique(map2)
          createSuccessModal({
            iconType: 'success',
            title: '提示',
            content: '取消分配成功',
            onOk: async () => {
              setTableData([])
              findByGiveOrgData()
            }
          })
          clearSelectedRowKeys()
        }
      }
    }
  })
}
async function orgChange() {
  tabsCheck(tabsflag.value)
  findByOrgPermission()
}
// 当前时间
function getNowDate() {
  return new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," ");
}

function tabsCheck(a) {
  tabsflag.value=a

  if(tabsflag.value==='1'){
    setTableData([])
    findByGiveOrgData()
  }else{
    setColumns(columns)
    findByNoGiveOrgData()
  }
}

function warningModal(text) {
  createWarningModal({
    iconType: 'warning',
    title: '提示',
    content: text,
    onOk: async () => {}
  })
}
</script>
<style>
.ant-modal-title {
  margin-top: -10px;
}

.ant-modal-header {
  height: 10px;
}

.scrollbar__view {
  overflow-y: hidden;
}

.vben-basic-title {
  color: rgb(1, 129, 226) !important;
}

.ant-modal-body {
  padding: 0px;
  border: 1px solid rgb(1, 129, 226);
  border-left: none;
  border-right: none;
}

.scroll-container .scrollbar__wrap {
  margin-bottom: -5px !important;
}
</style>
<style lang="less" scoped>
:deep(.ant-table-thead th) {
  background-color: #d8d8d8 !important;
  font-weight: bold !important;
  border-left: 1px solid #d8d8d8 !important;
  border-bottom: 1px solid #d8d8d8 !important;
  border-top: 1px solid #d8d8d8 !important;
}

.bg-white {
  width: 220px;
  min-height: 462px;
  height: calc(100% - 462px);
  border: 1px #cccccc solid;
  background: white;
}

.a-table-font-size-12 :deep(td),
.a-table-font-size-12 :deep(th) {
  font-size: 14px !important;
  padding: 2px 8px !important;
}

:deep(.ant-tree .ant-tree-node-content-wrapper.ant-tree-node-selected),
:deep(.ant-table-tbody > tr.ant-table-row-selected > td) {
  background-color: #1488b1;
  color: white;
}
</style>

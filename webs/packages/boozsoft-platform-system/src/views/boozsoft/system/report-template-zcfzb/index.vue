<template>
  <div>
    <Edit
      @save="saveData"
      @register="registerEdit"
    />
    <AddSelect
      @save="addModelPage"
      @register="registerAddSelectPage"
    />

    <div class="app-container">

      <div class="app-container-head">
        <img src="/img/menu/index_dept.png" style="display: inline-block;">

        <span style="    margin-left: 10px;"><b>资产负债表模板</b></span>
        <!--      <div style="display: inline">
                <select class="head-index-select">
                  <option
                    style="border: none; outline: none"
                    value=""
                  >查看全部 </option>
                </select>
              </div>-->

        <div
          class="ant-btn-group"
          data-v-a1ccd506=""
          style="float: right"
        >
          <button
            type="button"
            class="ant-btn"
            ant-click-animating-without-extra-node="false"
            @click="openAddPage()"
          ><span>新建</span></button>
          <button
            type="button"
            class="ant-btn"
            ant-click-animating-without-extra-node="false"
            @click="editOpen()"
          ><span>编辑</span></button>
          <button
            type="button"
            class="ant-btn"
            @click="delList()"
          ><span>删除</span></button>
        </div>
        <div class="app-container-neck">
          <div style="float: left; line-height: 30px">
            <span style="font-size: 14px; color: black">共{{ cardList.length }}条</span>
          </div>
          <div style="float: right; margin-left: 10px">
            <a-button class="ant-btn-me">
              <SyncOutlined :style="{ fontSize: '14px' }" />
            </a-button>
            <a-popover placement="bottom">
              <template #content>
            <span @click="pageParameter.showRulesSize = 'MAX'"
                  :style="{backgroundColor: (pageParameter.showRulesSize==='MAX')?'#e3e0e0':'white',cursor: 'pointer'}">&emsp;&emsp;<SortDescendingOutlined
              :style="{ fontSize: '14px' }"/>&emsp;大号字体&emsp;<CheckOutlined v-if="pageParameter.showRulesSize==='MAX'"
                                                                            :style="{ fontSize: '14px' }"/></span><br/>
                <span @click="pageParameter.showRulesSize = 'MIN'"
                      :style="{backgroundColor: (pageParameter.showRulesSize==='MIN')?'#e3e0e0':'white',cursor: 'pointer'}">&emsp;&emsp;<SortAscendingOutlined
                  :style="{ fontSize: '14px' }"/>&emsp;小号字体&emsp;<CheckOutlined v-if="pageParameter.showRulesSize==='MIN'"
                                                                                :style="{ fontSize: '14px' }"/></span>
              </template>
              <template #title>
                <b>设置表格字号</b>
              </template>
              <a-button>
                <SettingFilled :style="{ fontSize: '14px' }"/>
              </a-button>
            </a-popover>
            <!--        <a-popover placement="bottom">
                      <a-button>
                        <PicLeftOutlined :style="{ fontSize: '14px' }" />
                      </a-button>
                    </a-popover>

                    <a-button>
                      <EditFilled :style="{ fontSize: '14px' }" />
                    </a-button>-->

            <a-button @click="activeKey=!activeKey">
              <PieChartFilled :style="{ fontSize: '14px' }" />
            </a-button>
            <!--        <a-button>
                      <FilterFilled :style="{ fontSize: '14px' }" />
                    </a-button>-->
          </div>
          <div style="float: right; position: relative">
            <label style="font-size: 14px">检索条件：</label>
            <a-select v-model:value="formItems.selectType" style="width: 120px;font-size: 12px;" class="special_select">
              <a-select-option style="font-size: 12px;" value="titleName">模板标识</a-select-option>
              <a-select-option style="font-size: 12px;" value="templateName">模板名称</a-select-option>
              <a-select-option style="font-size: 12px;" value="accStandard">会计准则</a-select-option>
              <a-select-option style="font-size: 12px;" value="flag">状态</a-select-option>
              <a-select-option style="font-size: 12px;" value="sysFlag">是否系统模板</a-select-option>
            </a-select>
            <!-- 搜索 -->
            <a-input-search
              v-model:value="formItems.selectValue"
              placeholder=""
              style="width: 200px; border-radius: 4px"
              @search="onSearch"
            />
          </div>
        </div>
        <!--    <Edit
              @save="saveData"
              @register="registerEditPage"
            />-->
        <BasicTable
          v-if="activeKey"
          :row-selection="{ type: 'checkbox', selectedRowKeys: state.selectedRowKeys, onChange: onSelectChange }"
          :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
          @row-click="condClick"
          @register="registerTable"
        >
          <template #accStandard="{ record }"> {{ formatAccStandard(record.accStandard) }} </template>
          <template #flag="{ record }"> {{ record.flag === '1' ? '启用' : '停用' }} </template>
          <template #sysFlag="{ record }"> {{ record.sysFlag === '1' ? '是' : '否' }} </template>
          <template #action="{ record }">

            <div v-if="record.sysFlag!='1'">
              <a-popover placement="bottom">
                <a-button style="padding: 0px 4px; height: 20px;">
                  <CaretDownFilled />
                </a-button>
                <template #content>
                  <p v-if="record.flag=='1'" style="cursor: pointer" @click="openEdit(record)"><FormOutlined /> 编辑</p>
                  <p v-if="record.flag=='0'" style="cursor: pointer" @click="editFlagData(record)"><CheckCircleOutlined /> 启用</p>
                  <p v-if="record.flag=='1'" style="cursor: pointer" @click="editFlagData(record)"><CloseCircleOutlined /> 停用</p>
                  <p style="cursor: pointer" @click="del(record)"><DeleteOutlined /> 删除</p>
                </template>
              </a-popover>
            </div>
          </template>

        </BasicTable>
        <div
          v-if="!activeKey"
          :class="cardList"
        >
          <div :class="`${cardList}__content`">
            <!--        <div>ID:{{ item.id }}</div>-->
            <a-list style="height: 700px;overflow-y: scroll;overflow-x: hidden;">
              <a-row :gutter="16">
                <template
                  v-for="(item) in cardList"
                >
                  <a-col :span="8">
                    <a-list-item style="width: 95%">
                      <a-card
                        :hoverable="true"
                        :class="`${cardList}__card`"
                        style="width: 100%"
                      >
                        <div class="abc" style="width: 100%;float: right;" @click="openEdit(item)">
                          <div :class="`${cardList}__card-title`">
                            <Icon
                              v-if="item.icon"
                              class="icon"
                              :icon="item.icon"
                              :color="item.color"
                            />
                            <!--                        {{ index+1 }}-->
                          </div>
                          <div style="float: right; border: 1px solid rgb(1, 143, 251);border-radius: 10px;padding: 0 10px; margin-top: -5px;">
                            {{ item.templateName }}
                          </div>
                          <div style="font-size: 24px;margin-top: 20px;">
                            {{ item.titleName }}
                          </div>
                          <div style="font-size: 14px;">
                            会计准则：{{ formatAccStandard(item.accStandard) }}
                          </div>
                          <div style="float: right;">
                            {{ item.sysFlag === '1' ? '系统模板' : '自定义模板' }}
                          </div>
                        </div>
                      </a-card>
                    </a-list-item>
                  </a-col>
                </template>
              </a-row>
            </a-list>
          </div>

        </div>
      </div>

    </div>
  </div>
</template>
<script setup="props, {emit}" lang="ts">
import { findByReportName, findByReportNameAndFlag, deleteTemplate, saveTemplate, editFlag } from '/@/api/record/system/report-template'

import { BasicTable, useTable } from '/@/components/Table'
import Edit from './popup/edit.vue'
import AddSelect from './popup/add-select.vue'
import { useModal } from '/@/components/Modal'
import {onMounted, reactive, ref} from 'vue'
import Icon from '/@/components/Icon/index'
import {
  CaretDownFilled,
  FormOutlined,
  DeleteOutlined,
  CheckCircleOutlined,
  CloseCircleOutlined,
  SettingFilled,
  SyncOutlined,
  PicLeftOutlined,
  EditFilled,
  PieChartFilled,
  FilterFilled,
  SortDescendingOutlined,
  CheckOutlined,
  SortAscendingOutlined
} from '@ant-design/icons-vue'
import {findAllAcctandard, findAllAcctandardList} from "/@/api/accstandard/accstandard";

import {Select as ASelect,Input as AInput,List as AList,Row as ARow,Col as ACol ,Card as ACard,Popover as APopover,Button as AButton,Tag as ATag } from 'ant-design-vue'
import {useMessage} from "/@/hooks/web/useMessage";
import {columnProps} from "ant-design-vue/es/table/interface";

const AListItem=AList.Item
const AInputSearch=AInput.Search
const ASelectOption=ASelect.Option

const {
  createSuccessModal,
  createErrorModal
} = useMessage()

const formItems = ref({
  selectType: 'titleName',
  selectValue: ''
})

//打开编辑页
function openEditPage(){
  let thisInstance:any={}
  let thisOpen:any=():any=>{}
  return {
    register({instance,open}){
      thisInstance=instance
      thisOpen=open
    },
    open:(params)=>{
      thisOpen(params)
    }
  }
}
//给编辑页设置注册事件
const {register:registerEdit,open :openEditPage2}=openEditPage()
const CrudApi = {
  list: findByReportName,
  columns: [
    {
      title: 'ID',
      dataIndex: 'id',
      defaultHidden: true,
      ellipsis: true
    },
    {
      title: '模板标识',
      dataIndex: 'titleName',
      customCell: () => {		// 在此处可以修改单元格中的样式
        return { style: { 'text-align': 'left', '-webkit-line-clamp': 2,'color': 'blue' }} // return 想要设置的样式
      },
      ellipsis: true
    },
    {
      title: '会计准则',
      dataIndex: 'accStandard',
      customCell: () => {		// 在此处可以修改单元格中的样式
        return { style: { 'text-align': 'left', '-webkit-line-clamp': 2 }} // return 想要设置的样式
      },
      ellipsis: true,
      slots: { customRender: 'accStandard' }
    },
    {
      title: '模板名称',
      dataIndex: 'templateName',
      customCell: () => {		// 在此处可以修改单元格中的样式
        return { style: { 'text-align': 'left', '-webkit-line-clamp': 2 }} // return 想要设置的样式
      },
      ellipsis: true
    },
    {
      title: '是否启用',
      dataIndex: 'flag',
      width: 120,
      ellipsis: true,
      slots: { customRender: 'flag' }
    },
    {
      title: '是否系统模板',
      dataIndex: 'sysFlag',
      width: 120,
      ellipsis: true,
      slots: { customRender: 'sysFlag' }
    }
  ],
  editData: {
    id: '',
    reportName: '',
    templateName: '',
    accStandard: '',
    kemuTemplateId: '',
    titleName: '',
    flag: '',
    menu1: '',
    menu2: '',
    menu3: '',
    menu4: '',
    menu5: '',
    sysFlag: ''
  }
}
// 这是示例组件
const [registerTable, { reload,getDataSource,setTableData }] = useTable({
  api: CrudApi.list,
  columns: CrudApi.columns,
  bordered: true,
  showIndexColumn: true,
  pagination: {
    pageSize: 50,
    showSizeChanger: true,
    pageSizeOptions: ['10', '30', '50', '100'],
    showTotal: t => `总共${t}条数据`
  },
  actionColumn: {
    width: 120,
    title: '操作',
    dataIndex: 'action',
    slots: { customRender: 'action' }
  },
  searchInfo: {
    reportName: 'zcfzb'
  }
})

const accStandardList:any = ref([])
async function reloadAccStandard(){
  // 会计准则list
  const res = await findAllAcctandardList()
  accStandardList.value = res
}
function formatAccStandard(accStandard:any){
  let str = accStandard
  accStandardList.value.forEach(
    function (item:any) {
      if (item.uniqueAccStandard == accStandard){
        // console.log(item)
        str = item.accStandardName
      }
    })
  return str
}

const [registerAddSelectPage, { openModal: openAddSelectPage }] = useModal()
// const [registerEditPage, { openModal: openEditPage }] = useModal()
/*setTimeout(()=>{
  openEditPage2({
    data:{abc:1,dd:2} ,
    ok(){

    },
    cancel(){

    }
  })
},3000)*/
const val = {
  id: '',
  reportName: '',
  templateName: '',
  accStandard: '',
  kemuTemplateId: '',
  titleName: '',
  flag: '',
  menu1: '',
  menu2: '',
  menu3: '',
  menu4: '',
  menu5: '',
  sysFlag: ''
}
const condClick = (data:any, index:any, e:any) => {
  if (e.target.cellIndex == 2) {
    /*openEditPage(true, {
      data: data
    })*/
    openEditPage2({
      data: data
    })
  }
}

//选中内容
type Key = columnProps['id'];

const state = reactive<{
  selectedRowKeys: Key[];
  loading: boolean;
}>({
  selectedRowKeys: [], // Check here to configure the default column
  loading: false,
});
const checkRow:any = ref([])
const onSelectChange = (selectedRowKeys,row) => {
  // console.log('selectedRowKeys changed: ', row);
  state.selectedRowKeys = selectedRowKeys;
  checkRow.value = row
};

const openAddPage = () => {
  openAddSelectPage(true, {
    data: accStandardList.value
  })
}
const addModelPage = (data) => {
  openEditPage2({
    data: data
  })
}
const openEdit = (data:any) => {
  /*openEditPage(true, {
    data: data
  })*/
  // showEdit.value=true
  openEditPage2({
    data:data ,
    ok(){

    },
    cancel(){

    }
  })
}
const editOpen = (data:any) => {
  if (checkRow.value.length==1) {
    if (checkRow.value[0].sysFlag!='1') {
      openEditPage2({
        data: checkRow.value[0],
        ok() {
        },
        cancel() {
        }
      })
    } else {
      createErrorModal({
        iconType: 'warning',
        title: '编辑',
        content: '系统模板不允许编辑！'
      })
    }
  } else {
    createErrorModal({
      iconType: 'warning',
      title: '编辑',
      content: '请选择一条进行编辑！'
    })
  }
}
const del = async(data:any) => {
  await deleteTemplate(data)
  await reloadCurrentPage()
  // alert('删除成功！')
  createSuccessModal({
    iconType: 'success',
    title: '删除',
    content: '删除成功！'
  })
  reload({
    searchInfo: {
      reportName: 'zcfzb'
    }
  })
}

async function delList() {
  if (checkRow.value.length>0) {
    let num=0
    for (let i=0; i<checkRow.value.length; i++){
      const item = checkRow.value[i]
      debugger
      if (item.sysFlag=='1'){
        debugger
        num++
        createErrorModal({
          iconType: 'warning',
          title: '删除',
          content: '系统模板不允许删除！'
        })
        return false
      }
    }
    if (num==0) {
      for (let i = 0; i < checkRow.value.length; i++) {
        const item = checkRow.value[i]
        await deleteTemplate(item)
      }
      createSuccessModal({
        iconType: 'success',
        title: '删除',
        content: '删除成功！'
      })
      await reloadCurrentPage()
      reload({
        searchInfo: {
          reportName: 'zcfzb'
        }
      })
      return true
    }
  } else{
    createErrorModal({
      iconType: 'warning',
      title: '删除',
      content: '请选择需要删除的内容！'
    })
    return false
  }
}

const activeKey = ref(false)

const cardList:any = ref([])
async function reloadCurrentPage() {
  const res = await findByReportNameAndFlag("zcfzb")
  cardList.value = res.items
  // console.log(cardList.value)
}
onMounted(async() => {
  await reloadAccStandard()
  await reloadCurrentPage()
})
async function saveData(data:any) {
  await saveTemplate(data)
  await reloadCurrentPage()
  reload({
    searchInfo: {
      reportName: 'zcfzb'
    }
  })
}
async function editFlagData(data:any) {
  await editFlag(data)
  await reloadCurrentPage()
  reload({
    searchInfo: {
      reportName: 'zcfzb'
    }
  })
}

function onSearch(){
  let a:any = []
  a = cardList.value.filter(item=> {
    //通过模板标识过滤
    if (formItems.value.selectType=='titleName' && formItems.value.selectValue!=''){
      return item.titleName.indexOf(formItems.value.selectValue) != -1
    }
    //通过模板名称过滤
    if (formItems.value.selectType=='templateName' && formItems.value.selectValue!=''){
      return item.templateName.indexOf(formItems.value.selectValue) != -1
    }
    //通过会计准则过滤
    if (formItems.value.selectType=='accStandard' && formItems.value.selectValue!=''){
      const b = accStandardList.value.filter(bb => {
        return bb.accStandardName.indexOf(formItems.value.selectValue) != -1
      })
      if(b.map(bb=>bb.uniqueAccStandard).filter(dd=>dd==item.accStandard).length>0){
        return true
      }
      return item.accStandard.indexOf(b.map(bb=>bb.uniqueAccStandard)) != -1
    }
    //通过状态过滤
    if (formItems.value.selectType=='flag' && formItems.value.selectValue!=''){
      if (formItems.value.selectValue=='启用' || formItems.value.selectValue=='1'){
        return item.flag=='1'
      }
      if (formItems.value.selectValue=='停用' || formItems.value.selectValue=='0'){
        return item.flag!='1'
      }
      return item.flag.indexOf(formItems.value.selectValue) != -1
    }
    //通过是否系统模板过滤
    if (formItems.value.selectType=='sysFlag' && formItems.value.selectValue!=''){
      if (formItems.value.selectValue=='是' || formItems.value.selectValue=='系统模板' || formItems.value.selectValue=='1'){
        return item.sysFlag=='1'
      }
      if (formItems.value.selectValue=='否' || formItems.value.selectValue=='自定义模板' || formItems.value.selectValue=='0'){
        return item.sysFlag!='1'
      }
      return item.sysFlag.indexOf(formItems.value.selectValue) != -1
    }
    return item
  })
  // console.log(a)
  // setDataSource(a)
  setTableData([])
  setTableData(a)
}

const pageParameter = reactive({
  showRulesSize: 'MIN',
})
</script>
<style src="../../../../assets/styles/global-menu-index-block.less" lang="less" scoped></style><style lang='less' scoped ></style>
<style src="../../../../assets/styles/global-menu-index.less" lang="less" scoped></style><style lang='less' scoped ></style>
<style scoped lang="less">
:deep(.ant-card-body) {
  padding: 16px;
  border-left: 2px solid rgb(1, 143, 251);
  box-shadow: rgb(72 113 140) -3px 1px 7px -1px;
}

.a-table-font-size-16 :deep(td),
.a-table-font-size-16 :deep(th) {
  font-size: 16px !important;
  padding: 5px 8px !important;
}

.a-table-font-size-12 :deep(td),
.a-table-font-size-12 :deep(th) {
  font-size: 14px !important;
  padding: 2px 8px !important;
}

.abc:hover {
  color:blue;  /*DiV背景颜色*/
}
</style>

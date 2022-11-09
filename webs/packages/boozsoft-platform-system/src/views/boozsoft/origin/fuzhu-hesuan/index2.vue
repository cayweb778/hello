<template>
  <div>
    <div class="app-container">

      <div class="app-container-head">
        <img :src="getThisIndexImg()" class="container-head-img" draggable="false">
        <div class="container-head-title">
          <b class="noneSpan">辅助核算定义</b>
        </div>
        <div class="ant-btn-group" style="float: right">
          <button
            type="button"
            class="ant-btn ant-btn-me"
            ant-click-animating-without-extra-node="false"
            @click="openAdd()"
          ><span>新增</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            @click="editOpen()"
          ><span>修改</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            @click="delList()"
          ><span>删除</span></button>
          <!--          <button
                      type="button"
                      class="ant-btn ant-btn-me"
                      ant-click-animating-without-extra-node="false"
                    ><span>打印</span></button>-->
          <!--          <button
                      type="button"
                      class="ant-btn ant-btn-me"
                      @click="openExcel()"
                    ><span>导入</span></button>-->
          <button
            type="button"
            class="ant-btn ant-btn-me"
            @click="exportExcel()"
          ><span>导出</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            @click="closeCurrent()"
          ><span>退出</span></button>
        </div>
      </div>

      <div style="clear: none"/>
      <div style="display: inline-block;float: left;margin-top: .2%;" class="select-div">
        &ensp;<span>所属组织：</span>
        <a-select v-model:value="originId" @change="changeOrigin()" style="width: 250px;margin-top: -10px;margin-left: 10px">
          <a-select-option v-for="item in organizeList" :key="item.uniqueCode">
            {{ item.orgSimpName }}
          </a-select-option>
        </a-select>
      </div>
      <div class="app-container-neck">
        <div style="float: right; margin-left: 10px">
          <a-button class="ant-btn-me">
            <SyncOutlined :style="{ fontSize: '14px' }"/>
          </a-button>
          <a-popover class="ant-btn-default" placement="bottom">
            <template #content>
              <span class="group-btn-span-special2" @click="pageParameter.showRulesSize = 'MAX'"
                    :style="pageParameter.showRulesSize==='MAX'?{backgroundColor: '#0096c7',color: 'white'}:''">
                <SortDescendingOutlined/>&nbsp;大号字体&ensp;<CheckOutlined
                v-if="pageParameter.showRulesSize==='MAX'"/></span><br/>
              <span class="group-btn-span-special2" @click="pageParameter.showRulesSize = 'MIN'"
                    :style="pageParameter.showRulesSize==='MIN'?{backgroundColor: '#0096c7',color: 'white'}:''">
                <SortAscendingOutlined/>&nbsp;小号字体&ensp;<CheckOutlined
                v-if="pageParameter.showRulesSize==='MIN'"/></span>
            </template>
            <!--            <template #title>
                          <b>设置表格字号</b>
                        </template>-->
            <a-button>
              <SettingFilled :style="{ fontSize: '14px' }"/>
            </a-button>
          </a-popover>
          <!--        <a-popover placement="bottom">
                    <a-button>
                      <SettingFilled :style="{ fontSize: '14px' }" />
                    </a-button>
                  </a-popover>-->
          <!--        <a-button>
                    <PicLeftOutlined :style="{ fontSize: '14px' }" />
                  </a-button>-->
          <!--        <a-button>
                    <EditFilled :style="{ fontSize: '14px' }" />
                  </a-button>-->
          <!--        <a-popover placement="bottom">
                  </a-popover>

                  <a-button>
                    <EditFilled :style="{ fontSize: '14px' }" />
                  </a-button>

                  <a-button @click="activeKey=!activeKey">
                    <PieChartFilled :style="{ fontSize: '14px' }" />
                  </a-button>
                  <a-button>
                    <FilterFilled :style="{ fontSize: '14px' }" />
                  </a-button>-->
        </div>
        <div style="float: right; position: relative">
          <!--        <label style="font-size: 14px">检索条件：</label>-->
          <a-select v-model:value="formItems.selectType" style="width: 120px;font-size: 12px;" class="special_select">
            <a-select-option style="font-size: 12px;" value="1">编码</a-select-option>
            <a-select-option style="font-size: 12px;" value="2">名称</a-select-option>
            <a-select-option style="font-size: 12px;" value="3">数据来源</a-select-option>
            <a-select-option style="font-size: 12px;" value="4">对应凭证字段</a-select-option>
            <a-select-option style="font-size: 12px;" value="5">说明</a-select-option>
            <a-select-option style="font-size: 12px;" value="6">状态</a-select-option>
          </a-select>
          <!-- 搜索 -->
          <a-input-search
            placeholder=""
            style="width: 200px; border-radius: 4px"
            @search="onSearch"
          />
        </div>
      </div>

    </div>
    <div class="app-container">
      <Edit
        @save="saveData"
        @register="registerEditPage"
      />
      <Excel
        @save="saveExcel"
        @register="registerExcelPage"
      />
      <BasicTable
        :row-selection="{ type: 'checkbox', selectedRowKeys: state.selectedRowKeys, onChange: onSelectChange }"
        :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
        @register="registerTable"
      >
        <!--        <template #cdfine="{ record }">
                  cdfine{{ record.cdfine }}
                </template>-->
        <template #flag="{ record }">
          <span>
            <a-tag
              :color="record.flag === '1' ? 'green' : 'volcano'"
            >
              {{ record.flag === '1' ? '启用' : '停用' }}
            </a-tag>
          </span>
        </template>
        <template #bend="{ record }">{{ record.bend == '1' ? '末级档案' : '标准档案' }}</template>
        <template #ctype="{ record }">{{ record.ctype == '1' ? '系统档案' : '自定义档案' }}</template>
        <template #action="{ record }">

          <div>
            <a-popover placement="bottom">
              <a-button style="padding: 0px 4px; height: 20px;">
                <CaretDownFilled />
              </a-button>
              <template #content>
                <!--              <p v-if="record.flag=='1'" style="cursor: pointer" @click="openEdit(record)"><FormOutlined /> 编辑</p>-->
                <p v-if="record.flag=='0'" class="p_specifics" @click="editFlagData(record)"
                   style="cursor: pointer">
                  <CheckCircleOutlined/>
                  启用
                </p>
                <p v-if="record.flag=='1'" class="p_specifics" @click="editFlagData(record)"
                   style="cursor: pointer">
                  <CloseCircleOutlined/>
                  停用
                </p>
                <!--              <p v-if="record.flag=='0'" style="cursor: pointer" @click="del(record)"><DeleteOutlined /> 删除</p>-->
              </template>
            </a-popover>
          </div>
        </template>

      </BasicTable>
    </div>
  </div>
</template>
<script setup="props, {emit}" lang="ts">
import { BasicTable, useTable } from '/@/components/Table'
import Edit from './popup/edit.vue'
import Excel from './popup/excel.vue'
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
import {
  Select as ASelect,
  Input as AInput,
  List as AList,
  Row as ARow,
  Col as ACol,
  Card as ACard,
  Popover as APopover,
  Button as AButton,
  Tag as ATag,
  message
} from 'ant-design-vue'
import {
  deleteFuzhuHesuan, editByFlag,
  findFuzhuHesuanList,
  saveFuzhuHesuan,
  excelFuzhuHesuan, findAllFuzhuHesuanList
} from "/@/api/record/system/origin-fuzhu-hesuan";
import {useMessage} from "/@/hooks/web/useMessage";
//import {aoaToSheetXlsx} from "/@/views/boozsoft/xian_jin_liu_liang/yin_hang_dui_zhang/yin_hang_dui_zhang_dan/excel/components/importexcel";
const aoaToSheetXlsx=null
import {getThisIndexImg} from "/@/api/task-api/tast-bus-api";
import {getOrganizeAll} from "/@/api/record/group/im-organize";
import {useTabs} from "/@/hooks/web/useTabs";
import router from "/@/router";

const AListItem = AList.Item
const AInputSearch = AInput.Search
const ASelectOption = ASelect.Option

const {
  createSuccessModal,
  createErrorModal,
  createWarningModal,
  createConfirm
} = useMessage()

const {closeCurrent} = useTabs(router);

const formItems = ref({
  selectType: '1'
})

//查询组织
const organizeList:any = ref([])
const originId:any = ref('')
async function reloadOrigin(){
  organizeList.value = await getOrganizeAll()
  if (organizeList.value.length>0){
    originId.value = organizeList.value[0].uniqueCode
  }
}
//改变组织
async function changeOrigin() {
  await reload()
  await reloadFuzhuHesuan()
}

const CrudApi = {
  // list: findFuzhuHesuanList,
  columns: [
    {
      title: '编码',
      dataIndex: 'ccode',
      width: 100,
      ellipsis: true
    },
    {
      title: '名称',
      dataIndex: 'cname',
      width: 200,
      customCell: () => {		// 在此处可以修改单元格中的样式
        return { style: { 'text-align': 'left', '-webkit-line-clamp': 2 }} // return 想要设置的样式
      },
      ellipsis: true
    },
    {
      title: '数据来源',
      dataIndex: 'cankaoDuixiang',
      width: 200,
      customCell: () => {		// 在此处可以修改单元格中的样式
        return { style: { 'text-align': 'left', '-webkit-line-clamp': 2 }} // return 想要设置的样式
      },
      ellipsis: true
    },
    {
      title: '对应凭证字段',
      dataIndex: 'cfield',
      width: 150,
      customCell: () => {		// 在此处可以修改单元格中的样式
        return { style: { 'text-align': 'left', '-webkit-line-clamp': 2 }} // return 想要设置的样式
      },
      ellipsis: true,
      // slots: { customRender: 'cdfine' }
    },
    {
      title: '取值方式',
      dataIndex: 'bend',
      width: 150,
      ellipsis: true,
      slots: { customRender: 'bend' }
    },
    {
      title: '类型',
      dataIndex: 'ctype',
      width: 150,
      ellipsis: true,
      slots: { customRender: 'ctype' }
    },
    {
      title: '说明',
      dataIndex: 'remarks',
      customCell: () => {		// 在此处可以修改单元格中的样式
        return { style: { 'text-align': 'left', '-webkit-line-clamp': 2 }} // return 想要设置的样式
      },
      ellipsis: true
    },
    /*{
      title: '参照对象表名',
      dataIndex: 'cankaoDuixiangTable',
      customCell: () => {		// 在此处可以修改单元格中的样式
        return { style: { 'text-align': 'left', '-webkit-line-clamp': 2 }} // return 想要设置的样式
      },
      ellipsis: true
    },*/
    {
      title: '状态',
      dataIndex: 'flag',
      ellipsis: true,
      width: 100,
      slots: { customRender: 'flag' }
    },
  ],
  editData: {
    ccode: '',
    cname: '',
    cankaoDuixiang: '',
    cdfine: '',
    flag: '',
    remarks: '',
    cankaoDuixiangTable: ''
  }
}
// 这是示例组件
const [registerTable, {
  reload,
  getDataSource,
  setTableData,
  setPagination,
  getPaginationRef
}] = useTable({
  api: async()=>{
    return await findFuzhuHesuanList({originId: originId.value})
  },
  columns: CrudApi.columns,
  bordered: true,
  showIndexColumn: false,
  pagination: {
    pageSize: 50,
    showSizeChanger: true,
    pageSizeOptions: ['10', '25', '50', '100'],
    showTotal: t => `总共${t}条数据`
  },
  actionColumn: {
    width: 120,
    title: '操作',
    dataIndex: 'action',
    slots: {customRender: 'action'}
  }
})
const [registerEditPage, { openModal: openEditPage }] = useModal()
const [registerExcelPage, { openModal: openExcelPage }] = useModal()
const val = {
  ccode: '',
  cname: '',
  cankaoDuixiang: '',
  cdfine: '',
  flag: '',
  remarks: '',
  cankaoDuixiangTable: '',
  originId: ''
}

const openAdd = () => {
  if (fuzhuHesuanList.value.length<40) {
    val.originId = originId.value
    openEditPage(true, {
      data: val
    })
  } else {
    // message.error('辅助核算定义最多不能超过三十个！');
    createErrorModal({
      iconType: 'success',
      title: '新增',
      content: '辅助核算定义最多不能超过四十个！！'
    })
  }
}
const openEdit = (data:any) => {
  openEditPage(true, {
    data: data
  })
}

const openExcel = () => {
  if (fuzhuHesuanList.value.length<30) {
    openExcelPage(true, {
      data: {}
    })
  } else {
    // message.error('辅助核算定义最多不能超过三十个！');
    createErrorModal({
      iconType: 'success',
      title: '导入',
      content: '辅助核算定义最多不能超过三十个！！'
    })
  }
}
//导入Excel
async function saveExcel(data:any) {
  await excelFuzhuHesuan(data)
  await reload()
}

//文件导出
async function exportExcel() {
  // debugger
  const arrHeader = ['编码','名称','数据来源','对应凭证字段','取值方式','类型','说明'];
  const columnList = ['ccode','cname','cankaoDuixiang','cfield','bend','ctype','remarks']
  const arrData = getDataSource().map(item=>{
    item.bend = item.bend=='1'?'末级档案':'标准档案'
    item.ctype = item.ctype=='1'?'系统档案':'自定义档案'
    return item
  }).map((item) => columnList.map(column=>item[column]));
  // console.log(arrHeader)
  // 保证data顺序与header一致
  aoaToSheetXlsx({
    data: arrData,
    header: arrHeader,
    filename: '辅助核算定义.xlsx',
  });
}

const del = async(data:any) => {
  await deleteFuzhuHesuan(data)
  // alert('删除成功！')
  createSuccessModal({
    iconType: 'success',
    title: '删除',
    content: '删除成功！'
  })
  await reload()
}

async function saveData(data: any) {
  await saveFuzhuHesuan(data)
  await reload()
}

async function editFlagData(data: any) {
  await editByFlag(data);
  await reload()
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

const editOpen = () => {
  if (checkRow.value.length == 1) {
    openEditPage(true, {
      data: checkRow.value[0]
    })
  } else {
    createWarningModal({
      iconType: 'warning',
      title: '修改',
      content: '请选择一条进行编辑！'
    })
  }
}

async function delList() {
  if (checkRow.value.length > 0) {
    createConfirm({
      iconType: 'error',
      title: '警告',
      content: '删除后数据将不能恢复，你确认要删除吗?',
      onOk: async () => {
        for (let i = 0; i < checkRow.value.length; i++) {
          const item = checkRow.value[i]
          await deleteFuzhuHesuan(item)
        }
        message.success('删除成功！')
        checkRow.value = []
        state.selectedRowKeys = []
        await reload()
      },
      onCancel: () => {
        return false
      }
    })
  } else {
    createErrorModal({
      iconType: 'warning',
      title: '删除',
      content: '请选择需要删除的内容！'
    })
    return false
  }
}

function onSearch() {
}

const fuzhuHesuanList = ref([])

async function reloadFuzhuHesuan() {
  fuzhuHesuanList.value = await findAllFuzhuHesuanList(originId.value)
}

const pageParameter: any = reactive({
  showRulesSize: 'MIN',
})
const defaultPage = ref(false)
onMounted(async() => {
  await reloadOrigin()
  await reloadFuzhuHesuan()
})

</script>
<style src="./global-menu-index.less" lang="less" scoped></style>
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

.app-container:nth-of-type(1) {
  background-color: #f2f2f2;
  padding: 10px 5px;
  margin: 10px 10px 5px;
}

.app-container:nth-of-type(2) {
  padding: 0px;
  margin: 5px 10px;
  background: #b4c8e3 !important;
}
</style>

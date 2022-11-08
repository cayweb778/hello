<template>
  <div>
    <div class="app-container">

      <div class="app-container-head">
        <img :src="getThisIndexImg()" class="container-head-img" draggable="false">
        <div class="container-head-title">
          <b class="noneSpan">自定义项设置</b>
        </div>
        <div class="ant-btn-group" style="float: right">
          <button
            type="button"
            class="ant-btn ant-btn-me"
            ant-click-animating-without-extra-node="false"
            @click="openAdd()"
          ><span>新建</span></button>
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
          <button
            type="button"
            class="ant-btn ant-btn-me"
            ant-click-animating-without-extra-node="false"
          ><span>打印</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            @click="openExcel()"
          ><span>导入</span></button>
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
          <a-button @click="activeKey=!activeKey">
            <PieChartFilled :style="{ fontSize: '14px' }"/>
          </a-button>
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
            <a-select-option style="font-size: 12px;" value="3">说明</a-select-option>
            <a-select-option style="font-size: 12px;" value="4">状态</a-select-option>
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
        v-if="activeKey"
        :row-selection="{ type: 'checkbox', selectedRowKeys: state.selectedRowKeys, onChange: onSelectChange }"
        :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
        @register="registerTable"
      >
        <template #flag="{ record }">
          <span>
            <a-tag
              :color="record.flag === '1' ? 'green' : 'volcano'"
            >
              {{ record.flag === '1' ? '启用' : '停用' }}
            </a-tag>
          </span>
        </template>
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
                      <div style="width: 100%;float: right;" class="abc" @click="openEdit(item)">
                        <div :class="`${cardList}__card-title`">
                          <Icon
                            v-if="item.icon"
                            class="icon"
                            :icon="item.icon"
                            :color="item.color"
                          />
                          <!--                        {{ index+1 }}-->
                        </div>
                        <div
                          style="float: right; border: 1px solid rgb(1, 143, 251);border-radius: 10px;padding: 0 10px; margin-top: -5px;">
                          {{ item.definedCode }}
                        </div>
                        <div style="font-size: 24px; text-align:center">
                          {{ item.definedName }}
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
import {Select as ASelect,Input as AInput,List as AList,Row as ARow,Col as ACol ,Card as ACard,Popover as APopover,Button as AButton,Tag as ATag } from 'ant-design-vue'
import {
  deleteDefinedFile, editByFlag,
  findDefinedFileList,
  getDefinedFileList,
  saveDefinedFile,
  excelDefinedFile
} from "/@/api/record/system/group-defined-file";
import {useMessage} from "/@/hooks/web/useMessage";
//import {aoaToSheetXlsx} from "/@/views/boozsoft/xian_jin_liu_liang/yin_hang_dui_zhang/yin_hang_dui_zhang_dan/excel/components/importexcel";
const aoaToSheetXlsx=null
import {getThisAdInfoData} from "/@/api/record/system/financial-settings";
import {getCurrentAccountName, getThisIndexImg} from "/@/api/task-api/tast-bus-api";
import {message} from "ant-design-vue/lib/components";
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

const CrudApi = {
  list: findDefinedFileList,
  columns: [
    {
      title: '设置编码',
      dataIndex: 'definedCode',
      width: 100,
      ellipsis: true
    },
    {
      title: '设置名称',
      dataIndex: 'definedName',
      customCell: () => {		// 在此处可以修改单元格中的样式
        return { style: { 'text-align': 'left', '-webkit-line-clamp': 2 }} // return 想要设置的样式
      },
      width: 200,
      ellipsis: true
    },
    {
      title: '说明',
      dataIndex: 'remarks',
      customCell: () => {		// 在此处可以修改单元格中的样式
        return { style: { 'text-align': 'left', '-webkit-line-clamp': 2 }} // return 想要设置的样式
      },
      ellipsis: true
    },
    {
      title: '状态',
      dataIndex: 'flag',
      width: 100,
      ellipsis: true,
      slots: { customRender: 'flag' }
    }
  ],
  editData: {
    definedCode: '',
    definedName: '',
    flag: '',
    remarks: ''
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
  api: CrudApi.list,
  columns: CrudApi.columns,
  bordered: true,
  showIndexColumn: false,
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
  definedCode: '',
  definedName: '',
  flag: '',
  remarks: ''
}

const openAdd = () => {
  openEditPage(true, {
    data: val
  })
}
const openEdit = (data:any) => {
  openEditPage(true, {
    data: data
  })
}
const del = async(data:any) => {
  await deleteDefinedFile(data)
  // await deleteCate(id, data)
  // alert('删除成功！')
  createSuccessModal({
    iconType: 'success',
    title: '删除',
    content: '删除成功！'
  })
  await reload()
}
const openExcel = () => {
  openExcelPage(true, {
    data: {}
  })
}
//导入Excel
async function saveExcel(data:any) {
  await excelDefinedFile(data)
  await reloadCurrentPage()
  if (activeKey.value == true) {
    await reload()
  }
}

//文件导出
async function exportExcel() {
  // debugger
  const arrHeader = ['编码','名称','说明'];
  const columnList = ['definedCode','definedName','remarks']
  const arrData = getDataSource().map((item) => columnList.map(column=>item[column]));
  // console.log(arrHeader)
  // 保证data顺序与header一致
  aoaToSheetXlsx({
    data: arrData,
    header: arrHeader,
    filename: '自定义项设置.xlsx',
  });
}

const activeKey = ref(false)
const cardList:any = ref([])
async function reloadCurrentPage() {
  const res: any = await getDefinedFileList()
  cardList.value = res
  console.log(cardList.value)
}
onMounted(async() => {
  await reloadCurrentPage()
})
async function saveData(data:any) {
  await saveDefinedFile(data)
  await reloadCurrentPage()
  if (activeKey.value == true) {
    await reload()
  }
}

async function editFlagData(data: any) {
  await editByFlag(data);
  await reloadCurrentPage()
  if (activeKey.value == true) {
    await reload()
  }
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
          await deleteDefinedFile(item)
        }
        message.success('删除成功！')
        checkRow.value = []
        state.selectedRowKeys = []
        await reloadCurrentPage()
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

const pageParameter: any = reactive({
  showRulesSize: 'MIN'
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
  color: blue; /*DiV背景颜色*/
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

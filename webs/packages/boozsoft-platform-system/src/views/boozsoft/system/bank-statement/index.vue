<template>
  <div class="app-container">

    <div class="app-container-head">
      <img src="/img/menu/index_dept.png">

      <span style="    margin-left: 10px;"><b>银行对账单</b></span>
      <div style="display: inline">
<!--        <select class="head-index-select">
          <option
            style="border: none; outline: none"
            value=""
          >查看全部 </option>
        </select>-->
        <a-select v-model:value="kemu" @change="checkDate()" class="head-index-select" placeholder="请选择科目">
          <a-select-option v-for="item in codeKemu" :key="item.ccode" :value="item.ccode">
            {{ item.ccode }}-{{ item.ccodeName }}
          </a-select-option>
        </a-select>
        <a-select v-model:value="year" @change="checkDate()" class="head-index-select" placeholder="请选择年度">
          <a-select-option v-for="item in yearList" :key="item.accountYear" :value="item.accountYear">
            {{ item.accountYear }}
          </a-select-option>
        </a-select>
      </div>

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
<!--        <button
          type="button"
          class="ant-btn"
          ant-click-animating-without-extra-node="false"
          @click="openChecked()"
        ><span>选择</span></button>-->
        <button
          type="button"
          class="ant-btn"
          ant-click-animating-without-extra-node="false"
        ><span>打印</span></button>
        <button
          type="button"
          class="ant-btn"
        ><span>导入</span></button>
        <button
          type="button"
          class="ant-btn"
        ><span>导出</span></button>
      </div>
    </div>
    <div class="app-container-neck">
      <div style="float: right; margin-left: 10px">
        <a-button class="ant-btn-me">
          <SyncOutlined :style="{ fontSize: '14px' }" />
        </a-button>
        <a-popover placement="bottom">
          <a-button>
            <SettingFilled :style="{ fontSize: '14px' }" />
          </a-button>
        </a-popover>
        <a-popover placement="bottom">
          <a-button>
            <PicLeftOutlined :style="{ fontSize: '14px' }" />
          </a-button>
        </a-popover>

        <a-button>
          <EditFilled :style="{ fontSize: '14px' }" />
        </a-button>

        <a-button>
          <PieChartFilled :style="{ fontSize: '14px' }" />
        </a-button>
        <a-button>
          <FilterFilled :style="{ fontSize: '14px' }" />
        </a-button>
      </div>
      <div style="float: right; position: relative">
        <!-- 搜索 -->
        <a-input-search
          placeholder=""
          style="width: 200px; border-radius: 4px"
          @search="onSearch"
        />
      </div>
    </div>
    <Edit
      @save="saveData"
      @register="registerEditPage"
    />
    <!-- <Checked
      @check="checkDate"
      @register="registerCheckedPage"
    /> -->
    <BasicTable
      :row-selection="{ type: 'checkbox' ,fixed: true}"
      @row-click="condClick"
      @register="registerTable"
    >
      <template #jie="{ record }"> {{ toThousandFilter(record.jie) }} </template>
      <template #dai="{ record }"> {{ toThousandFilter(record.dai) }} </template>
      <template #flag="{ record }"> {{ record.flag === '1' ? '已对账' : '未对账' }} </template>
      <template #action="{ record }">

        <div>
          <a-popover placement="bottom">
            <a-button style="padding: 0px 4px; height: 20px;">
              <CaretDownFilled />
            </a-button>
            <template #content>
              <p style="cursor: pointer" @click="openEdit(record)"><FormOutlined /> 编辑</p>
              <p style="cursor: pointer" @click="del(record)"><DeleteOutlined /> 删除</p>
            </template>
          </a-popover>
        </div>
      </template>

    </BasicTable>
  </div>
</template>
<script setup="props, {emit}" lang="ts">
import {
  // getBankStatementList,
  deleteBankStatement,
  saveBankStatement,
  findByKemuAndDate,
  findCodeKemuByBr, findYearByAccount
} from '/@/api/record/system/bank-statement'

import { BasicTable, useTable } from '/@/components/Table'
import Edit from './popup/edit.vue'
import { useModal } from '/@/components/Modal'
import {
  CaretDownFilled,
  FormOutlined,
  DeleteOutlined,
  SettingFilled,
  SyncOutlined,
  PicLeftOutlined,
  EditFilled,
  PieChartFilled,
  FilterFilled
} from '@ant-design/icons-vue'
import {onMounted, ref} from "vue";
const CrudApi = {
  list: findByKemuAndDate,
  columns: [
    {
      title: 'ID',
      dataIndex: 'id',
      defaultHidden: true,
      ellipsis: true
    },
    {
      title: '日期',
      dataIndex: 'statementDate',
      ellipsis: true
    },
    {
      title: '票号',
      dataIndex: 'piaohao',
      ellipsis: true
    },
    {
      title: '摘要',
      dataIndex: 'remarks',
      ellipsis: true
    },
    {
      title: '借方金额',
      dataIndex: 'jie',
      customCell: () => {		// 在此处可以修改单元格中的样式
        return { style: { 'text-align': 'right', '-webkit-line-clamp': 2 }} // return 想要设置的样式
      },
      ellipsis: true,
      slots: { customRender: 'jie' }
    },
    {
      title: '贷方金额',
      dataIndex: 'dai',
      customCell: () => {		// 在此处可以修改单元格中的样式
        return { style: { 'text-align': 'right', '-webkit-line-clamp': 2 }} // return 想要设置的样式
      },
      ellipsis: true,
      slots: { customRender: 'dai' }
    },
    {
      title: '余额',
      dataIndex: 'fangxiang',
      customCell: () => {		// 在此处可以修改单元格中的样式
        return { style: { 'text-align': 'right', '-webkit-line-clamp': 2 }} // return 想要设置的样式
      },
      ellipsis: true
    },
    {
      title: '对账状态',
      dataIndex: 'flag',
      ellipsis: true,
      slots: { customRender: 'flag' }
    },
    {
      title: '对账标识号',
      dataIndex: 'statementNum',
      ellipsis: true
    }
  ],
  editData: {
    id: '',
    statementDate: '',
    kemuCode: '',
    duifangUnit: '',
    settModes: '',
    piaohao: '',
    jie: '',
    dai: '',
    fangxiang: '',
    flag: '',
    statementNum: '',
    remarks: '',
    currencyId: ''
  }
}
const kemu:any = ref()
const year:any = ref()
// 这是示例组件
const [registerTable, { reload }] = useTable({
  api: CrudApi.list,
  columns: CrudApi.columns,
  bordered: true,
  showIndexColumn: false,
  actionColumn: {
    width: 160,
    title: '操作',
    dataIndex: 'action',
    slots: { customRender: 'action' }
  },
  // searchInfo: {
  //   kemuCode: '',
  //   year: ''
  // }
})

const [registerEditPage, { openModal: openEditPage }] = useModal()
/* const [registerCheckedPage, { openModal: openCheckedPage }] = useModal() */
const val = {
  id: '',
  statementDate: '',
  kemuCode: kemu.value,
  duifangUnit: '',
  settModes: '',
  piaohao: '',
  jie: '',
  dai: '',
  fangxiang: '',
  flag: '',
  statementNum: '',
  remarks: '',
  currencyId: ''
}
const condClick = (data:any,index:any, e:any) => {
  console.log(index)
  if (e.target.cellIndex == 1) {
    openEditPage(true, {
      data: data
    })
  }
}
const openAddPage = () => {
  openEditPage(true, {
    data: {val,kemuCode: kemu.value}
  })
}
/* const checked:any = ref({})
const openChecked = () => {
  openCheckedPage(true, {
    data: checked.value
  })
} */
const openEdit = (data:any) => {
  openEditPage(true, {
    data: data
  })
}

const codeKemu:any = ref({})
async function reloadCurrentPage() {
  const res = await findCodeKemuByBr()
  codeKemu.value = res
  console.log(codeKemu.value)
  if (res.length>0) {
    kemu.value = res[0].ccode
  }
}
const yearList:any = ref({})
async function reloadYear() {
  const res = await findYearByAccount("1")
  yearList.value = res
  console.log(yearList.value)
  if (res.length>0) {
    year.value = res[0].accountYear
  }
}
onMounted(async() => {
  await reloadCurrentPage()
  await reloadYear()
  await checkDate()
})

const del = async(data:any) => {
  await deleteBankStatement(data)
  alert('删除成功！')
  reload()
}

async function saveData(data:any) {
  await saveBankStatement(data)
  reload()
}

// async function checkDate(data:any) {
//   checked.value.kemuCode = data.kemuCode
//   checked.value.date1 = data.date1
//   checked.value.date2 = data.date2
//   console.log(checked.value)
//   reload(
//     {
//       searchInfo: {
//         kemuCode: data.kemuCode,
//         date1: data.date1,
//         date2: data.date2
//       }
//     }
//   )
// }
async function checkDate() {
  reload(
    {
      searchInfo: {
        kemuCode: kemu.value,
        year: year.value
      }
    }
  )
}
function toThousandFilter(num:any) {
  if (num=='' || num==null){
    return ''
  }
  return (+num || 0).toFixed(2).replace(/\d{1,3}(?=(\d{3})+(\.\d*)?$)/g, '$&,')
}

function onSearch(){}
</script>
<style src="../../../../assets/styles/global-menu-index-block.less" lang="less" scoped></style><style lang='less' scoped ></style>
<style src="../../../../assets/styles/global-menu-index.less" lang="less" scoped></style><style lang='less' scoped ></style>
<style scoped lang="less">
:deep(.ant-card-body) {
  padding: 16px;
  border-left: 2px solid rgb(1, 143, 251);
  box-shadow: rgb(72 113 140) -3px 1px 7px -1px;
}
</style>

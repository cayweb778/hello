<template>
  <div class="app-container">

    <div class="app-container-head">

      <span style="margin-left: 40%"><b>发生额及余额表</b></span>

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
        ><span>查询</span></button>
        <button
          type="button"
          class="ant-btn"
          ant-click-animating-without-extra-node="false"
        ><span>发送</span></button>
        <button
          type="button"
          class="ant-btn"
        ><span>图表</span></button>
        <button
          type="button"
          class="ant-btn"
        ><span>导出</span></button>
        <button
          type="button"
          class="ant-btn"
        ><span>打印</span></button>
      </div>
    </div>
    <div class="app-container-neck" style="margin-top: 8px;">

      <div style="display: inline-block;float: left">
        期间：
        <a-range-picker
          mode="decade"
          valueFormat="YYYY-MM"
          placeholder="选择结转期间"
          class="head-index-year-month"
        >
        </a-range-picker>
      </div >
      <div style="display: inline-block;float: left;margin-left: 20%">币种： 人民币</div>
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
    <a-table
      :columns="columns"
      :data-source="data"
      bordered
      size="middle"
      :pagination="{ pageSize: 200,showSizeChanger: true, pageSizeOptions: ['200','500','1000'] }"
      :scroll="{ x: 'calc(700px + 50%)', y: '600px' }"
    />
  </div>
</template>
<script setup="props, {emit}" lang="ts">
import {getBankCheckList, deleteBankCheck, saveBankCheck} from '/@/api/record/system/bank-check'

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
const CrudApi = {
  list: getBankCheckList,
  columns: [
    {
      title: 'ID',
      dataIndex: 'id',
      defaultHidden: true,
      ellipsis: true
    },
    {
      title: '支票编号',
      dataIndex: 'checkNum',
      ellipsis: true
    },
    {
      title: '支票银行',
      dataIndex: 'checkName',
      ellipsis: true
    },
    {
      title: '出票日期',
      dataIndex: 'checkDate',
      ellipsis: true
    },
    {
      title: '付款行名称',
      dataIndex: 'payName',
      ellipsis: true
    },
    {
      title: '出票人账户',
      dataIndex: 'payAccount',
      ellipsis: true
    },
    {
      title: '收款人全称',
      dataIndex: 'payeeName',
      ellipsis: true
    },
    {
      title: '金额',
      dataIndex: 'amount',
      customCell: () => {		// 在此处可以修改单元格中的样式
        return { style: { 'text-align': 'right', '-webkit-line-clamp': 2 }} // return 想要设置的样式
      },
      ellipsis: true
    }
  ],
  editData: {
    id: '',
    qrCode: '',
    checkDate: '',
    checkType: '',
    checkNum: '',
    checkName: '',
    payName: '',
    payAccount: '',
    payeeName: '',
    purpose: '',
    amount: '',
    encryCode: '',
    voucherNumber: ''
  }
}

const [registerEditPage, { openModal: openEditPage }] = useModal()
const val = {
  id: '',
  qrCode: '',
  checkDate: '',
  checkType: '',
  checkNum: '',
  checkName: '',
  payName: '',
  payAccount: '',
  payeeName: '',
  purpose: '',
  amount: '',
  encryCode: '',
  voucherNumber: ''
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
    data: val
  })
}
const openEdit = (data:any) => {
  openEditPage(true, {
    data: data
  })
}
const del = async(data:any) => {
  await deleteBankCheck(data)
  alert('删除成功！')
}

async function saveData(data:any) {
  await saveBankCheck(data)
}

function onSearch(){}
const columns = [
 /* {
    title: '科目编码',
    dataIndex: 'name',
    key: 'name',
    width: 100,
    fixed: 'left',
    filters: [
      {
        text: 'Joe',
        value: 'Joe',
      },
      {
        text: 'John',
        value: 'John',
      },
    ],
    onFilter: (value, record) => record.name.indexOf(value) === 0,
  },*/
  {
    title: '序号',
    dataIndex: 'number',
    key: 'number',
    width: 100,
    fixed: 'left',
    align: 'center'
  },
  {
    title: '科目编码',
    dataIndex: 'num',
    key: 'num',
    width: 200,
    fixed: 'left',
    align: 'center'
  },
  {
    title: '科目名称',
    dataIndex: 'name',
    key: 'name',
    width: 320,
    fixed: 'left',
    align: 'center'
  },
  {
    title: '期初余额',
    children: [
      {
        title: '借方',
        dataIndex: 'jy',
        key: 'jy',
        width: 200,
        sorter: (a, b) => a.age - b.age,
        align: 'center'
      },
      {
        title: '贷方',
        dataIndex: 'dy',
        key: 'dy',
        width: 200,
        align: 'center',
        sorter: (a, b) => a.age - b.age,
      }
    ],
  },
  {
    title: '本期发生',
    children: [
      {
        title: '借方',
        dataIndex: 'jf',
        key: 'jf',
        width: 200,
        align: 'center',
        sorter: (a, b) => a.age - b.age,
      },
      {
        title: '贷方',
        dataIndex: 'df',
        key: 'df',
        width: 200,
        align: 'center',
        sorter: (a, b) => a.age - b.age,
      }
    ],
  },{
    title: '期末余额',
    children: [
      {
        title: '借方',
        dataIndex: 'qjy',
        key: 'qjy',
        width: 200,
        align: 'center',
        sorter: (a, b) => a.age - b.age,
      },
      {
        title: '贷方',
        dataIndex: 'qdy',
        key: 'qdy',
        width: 200,
        align: 'center',
        sorter: (a, b) => a.age - b.age,
      }
    ],
  }
];

const data = [];
for (let i = 0; i < 1100; i++) {
  data.push({
    key: i,
    number: i + 1,
    num: 'KACA',
    name: 'John Brown',
    jy: i + 1,
    dy: i + 1,
    jf: i + 1,
    df: i + 1,
    qjy: i + 1,
    qdy: i + 1
  });
}
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

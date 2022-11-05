<template>
  <div class="app-container">

    <div class="app-container-head">
      <img src="/img/menu/index_dept.png">

      <span style="    margin-left: 10px;"><b>人员信息</b></span>
      <div style="display: inline">
        <select class="head-index-select">
          <option
            style="border: none; outline: none"
            value=""
          >查看全部 </option>
        </select>
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
      <div style="float: left; line-height: 30px">
        <span style="font-size: 14px; color: black">共{{ cardList.length }}条</span>
      </div>
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

        <a-button @click="activeKey=!activeKey">
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
    <BasicTable
      v-if="activeKey"
      :row-selection="{ type: 'checkbox' ,fixed: true}"
      @row-click="condClick"
      @register="registerTable"
    >
      <template #psnSex="{ record }"> {{ formatPsnSex(record.psnSex) }} </template>
      <template #psnType="{ record }"> {{ formatPsnType(record.psnType) }} </template>
      <template #flag="{ record }"> {{ record.flag === '1' ? '启用' : '停用' }} </template>
      <template #uniqueCodeDept="{ record }"> {{ formatUniqueCodeDept(record.uniqueCodeDept) }} </template>
      <template #action="{ record }">

        <div>
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
        <a-list style="height: 700px;overflow-y: scroll;overflow-x: hidden;">
          <a-row :gutter="16">
            <template
              v-for="(item) in cardList"
              :key="index"
            >
              <a-col :span="6">
                <a-list-item style="width: 100%;">
                  <a-card
                    :hoverable="true"
                    :class="`${cardList}__card`"
                    style="width: 100%;"
                  >

                    <div style="width: 100%;padding: 0;">
                      <label style="float: right; width: 70px; border: 1px solid rgb(1, 143, 251);border-radius: 10px;padding: 0 10px;font-size: 10px">
                        {{ item.psnType == 1 ? '内部员工':'外部员工' }}
                      </label>
                      <label style="font-size: 12px; width: 100px;float: left;">
                        工号：{{ item.jobNum }}
                      </label>
                      <label style="font-size: 20px; width: auto;float: left;">
                        {{ item.psnName }}
                      </label>
                    </div><br><br>
                    <div style="width: 100%;padding: 0;">
                      <div style="float: left; width: 20%;">
<!--                        <img src="public/favicon.ico" style="width: 70%;">-->
                      </div>
                      <div style="float: left; width: 100%;">
                        <label style="float: left;width: 50%;">性别：{{ item.psnSex == 1 ? '男':'女' }}</label>
                        <label style="float: left;width: 50%;">QQ：{{ item.psnQq }}</label>
                        <label style="float: left;width: 50%;">手机号：{{ item.cellPhoneNum }}</label>
                        <label style="float: left;width: 50%;">微信号：{{ item.psnWechat }}</label>
                        <label style="float: left;">Email：{{ item.psnEmail }}</label>
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
</template>
<script setup lang="ts">
import {deletePsn, editFlag, savePsn} from '/@/api/record/system/psn'
import { BasicTable, useTable } from '/@/components/Table'
import Edit from './popup/edit.vue'
import { useModal } from '/@/components/Modal'
import { onMounted, ref } from 'vue'
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
  FilterFilled
} from '@ant-design/icons-vue'
import { psnFindAll } from '/@/api/psn/psn'
import {getDeptList} from "/@/api/record/system/dept";

const CrudApi = {
  list: psnFindAll,
  columns: [
    {
      title: 'ID',
      dataIndex: 'id',
      defaultHidden: true,
      ellipsis: true
    },
    {
      title: '员工编码',
      dataIndex: 'psnCode',
      width: 100,
      ellipsis: true
    },
    {
      title: '员工姓名',
      dataIndex: 'psnName',
      width: 100,
      ellipsis: true
    },
    {
      title: '部门',
      dataIndex: 'uniqueCodeDept',
      customCell: () => {		// 在此处可以修改单元格中的样式
        return { style: { 'text-align': 'left', '-webkit-line-clamp': 2 }} // return 想要设置的样式
      },
      ellipsis: true,
      slots: { customRender: 'uniqueCodeDept' }
    },
    {
      title: '员工性别',
      dataIndex: 'psnSex',
      width: 80,
      ellipsis: true,
      slots: { customRender: 'psnSex' }
    },
    {
      title: '员工属性',
      dataIndex: 'psnType',
      width: 100,
      ellipsis: true,
      slots: { customRender: 'psnType' }
    },
    {
      title: '手机号',
      dataIndex: 'cellPhoneNum',
      ellipsis: true
    },
    {
      title: '入职日期',
      dataIndex: 'entryDate',
      ellipsis: true
    },
    {
      title: '是否启用',
      dataIndex: 'flag',
      width: 80,
      ellipsis: true,
      slots: { customRender: 'flag' }
    }
  ],
  editData: {
    id: '',
    uniqueCode: '',
    psnCode: '',
    psnName: '',
    psnSex: '',
    psnType: '',
    psnPost: '',
    psnPhone: '',
    entryDate: ''
  }
}
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
  }
})
const [registerEditPage, { openModal: openEditPage }] = useModal()
const val = {
  id: '',
  uniqueCode: '',
  psnName: '',
  psnSex: '0',
  psnType: '1',
  psnCode: '',
  uniqueCodeDept: '',
  jobNum: '',
  psnPost: '',
  uniquePsnType: '',
  cellPhoneNum: '',
  countryId: '',
  psnEmail: '',
  psnAddress: '',
  province: '',
  city: '',
  district: '',
  psnQq: '',
  psnWechat: '',
  documentType: '1',
  documentCode: '',
  psnStation: '',
  entryDate: '',
  psnBank: '',
  bankArea: '',
  bankAccount: '',
  bankNum: ''
}
function formatPsnSex(sex:any) {
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
      break
  }
  return str
}
function formatPsnType(type:any) {
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
const deptList = ref([])
async function reloadDept() {
  const res:any = await getDeptList()
  deptList.value = res.items
  console.log(deptList.value)
}
function formatUniqueCodeDept(uniqueCodeDept:any) {
  let str = uniqueCodeDept
  console.log(uniqueCodeDept)
  deptList.value.forEach(
    function (dept:any) {
      if (dept.uniqueCode == uniqueCodeDept){
        console.log(dept)
        str = dept.deptName
      }
    }
  )
  return str
}
const condClick = (data:any, e:any) => {
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
  await deletePsn(data)
  alert('删除成功！')
  reload()
}
const activeKey = ref(false)

const cardList:any = ref({})
onMounted(async() => {
   await reloadDept()
})
async function reloadCurrentPage() {
  const res:any = await psnFindAll()
  cardList.value = res.items
  console.log(cardList.value)
}
onMounted(async() => {
  await reloadCurrentPage()
})
async function saveData(data:any) {
  await savePsn(data)
  await reloadCurrentPage()
  reload()
}
async function editFlagData(data:any) {
  await editFlag(data)
  reload()
}
function onSearch(){}
</script>
<style src="../../../../assets/styles/global-menu-index-block.less" lang="less" scoped></style><style lang='less' scoped ></style>
<style src="../../../../assets/styles/global-menu-index.less" lang="less" scoped></style><style lang='less' scoped ></style>
<style scoped lang="less">
 :deep(.ant-card-body) {
  padding: 10px;
  border-left: 2px solid rgb(1, 143, 251);
  box-shadow: rgb(72 113 140) -3px 1px 7px -1px;
}
</style>

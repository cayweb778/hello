<template>
  <div>
    <div class="app-container">
      <div class="app-container-head">
        <div class="container-head-title" style="float: left;">
          <b class="noneSpan" style="font-size: 50px;"><ProfileOutlined /></b>
        </div>
        <div class="container-head-title" style="padding-left: 35%;text-align: center;margin-top: 15px;">
          <b class="noneSpan" style="font-size: 36px;">密码策略</b>
        </div>
        <div class="ant-btn-group" data-v-a1ccd506="" style="float: right">
          <button
            type="button"
            class="ant-btn ant-btn-me"
            ant-click-animating-without-extra-node="false"
            @click="openAddPage('add')"
          ><span>新建</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            ant-click-animating-without-extra-node="false"
            @click="openAddPage('edit')"
          ><span>修改</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            ant-click-animating-without-extra-node="false"
            @click="delData()"
          ><span>删除</span></button>
          <button type="button" class="ant-btn ant-btn-me" @click="closeCurrent"><span>退出</span></button>
        </div>
      </div>
    </div>
    <div class="app-container">
      <BasicTable @register="registerTable" @fetch-success="fetchSuccess" :scroll="{ y: windowHeight }" :class="'a-table-font-size-12'">
        <template #pwdUpperCase="{ record }">
          <span v-if="record.cusCclassName!=''">
            {{ record.pwdUpperCase ==='1'?'是':'否' }}
          </span>
        </template>
        <template #pwdTe="{ record }">
          <span v-if="record.cusCclassName!=''">
            {{ record.pwdTe ==='1'?'是':'否' }}
          </span>
        </template>
        <template #pwdUpdate="{ record }">
          <span v-if="record.cusCclassName!=''">
            {{ record.pwdUpdate ==='1'?'是':'否' }}
          </span>
        </template>
        <template #pwdLock="{ record }">
          <span v-if="record.cusCclassName!=''">
            {{ record.pwdLock ==='1'?'是':'否' }}
          </span>
        </template>
        <template #pwdType="{ record }">
          <span v-if="record.cusCclassName!=''">
            {{ record.pwdType ==='1'?'系统内置':'自定义' }}
          </span>
        </template>
        <template #pwdErrorTime="{ record }">
          <span v-if="record.pwdErrorTime!=null">
            {{ record.pwdErrorTime =='all'?'永久':record.pwdErrorTime=='24'?record.pwdErrorTime+'小时':record.pwdErrorTime+'分钟' }}
          </span>
        </template>
      </BasicTable>
<!--      <div class="pagination-text" :style="{top: (windowHeight+41)+'px'}">-->
<!--        {{`共 ${tableTotal}条记录&emsp;每页 200 条`}}-->
<!--      </div>-->
    </div>
    <Edit @save="saveData" @handleClose="reload(),clearSelectedRowKeys()" @register="registerSavePage"/>
  </div>
</template>
<script setup lang="ts">
import {delPwdRule, findAll, savePwdRule} from '/@/api/record/pwd-rule/data';
import {BasicTable, useTable} from '/@/components/Table';
import {useModal} from '/@/components/Modal';
import {EditFilled, ProfileOutlined, SyncOutlined,} from '@ant-design/icons-vue';
import {
  Input as AInput,
  Select as ASelect,
  Statistic as AStatistic,
  Tabs as ATabs,
} from 'ant-design-vue';
import {reactive, ref} from 'vue';
import {useUserStoreWidthOut} from '/@/store/modules/user';
import Edit from './popup/edit.vue';
import {useTabs} from "/@/hooks/web/useTabs";
import {useMessage} from "/@/hooks/web/useMessage";

const ASelectOption = ASelect.Option;
const AInputSearch = AInput.Search;
const AStatisticCountdown = AStatistic.Countdown;
const ATabPane = ATabs.TabPane;
const { closeCurrent } =useTabs();
const username = ref(useUserStoreWidthOut().getUserInfo.username);
const usernum = ref(useUserStoreWidthOut().getUserInfo.username);
const {createConfirm, createWarningModal, createMessage} = useMessage();

const columns = [
  {
    title: 'ID',
    dataIndex: 'id',
    defaultHidden: true,
    ellipsis: true,
  },
  {
    title: '策略名称',
    dataIndex: 'pwdName',
    ellipsis: true,align: 'left',
  },
  {
    title: '密码最小长度',
    dataIndex: 'pwdLength',
    ellipsis: true,
  },
  {
    title: '强制大写字母',
    dataIndex: 'pwdUpperCase',
    ellipsis: true,
    slots: { customRender: 'pwdUpperCase' },
  },
  {
    title: '强制特殊符号',
    dataIndex: 'pwdTe',
    ellipsis: true,
    slots: { customRender: 'pwdTe' },
  },
  {
    title: '密码有效天数',
    dataIndex: 'pwdValidity',
    ellipsis: true,
  },
  {
    title: '密码到期强制修改',
    dataIndex: 'pwdUpdate',
    ellipsis: true,
    slots: { customRender: 'pwdUpdate' },
  },
  {
    title: '密码到期锁定',
    dataIndex: 'pwdLock',
    ellipsis: true,
    slots: { customRender: 'pwdLock' },
  },{
    title: '允许登录错误次数',
    dataIndex: 'pwdErrorNumber',
    ellipsis: true,
  },{
    title: '锁定密码时长',
    dataIndex: 'pwdErrorTime',
    ellipsis: true,
    width: 120, slots: { customRender: 'pwdErrorTime' },
  },{
    title: '类型',
    dataIndex: 'pwdType',
    ellipsis: true,
    slots: { customRender: 'pwdType' },
  },
];

const rowSelection = {
  getCheckboxProps: (record) => ({
    disabled: record.pwdType === '1',
    name: record.pwdType,
  }),
};
const pageParameter = reactive({
  searchValue: '',
});
const tableTotal=ref(0)

const windowHeight = (window.innerHeight - 280)
// 这是示例组件
const [registerSavePage, {openModal: openSavePage}] = useModal();
const [registerTable, { reload,getSelectRows,clearSelectedRowKeys,getDataSource,setTableData }] = useTable({
  api: findAll,
  columns: columns,
  rowSelection: {type:'checkbox',getCheckboxProps:rowSelection.getCheckboxProps},
  bordered: true,
  showIndexColumn: true,
  indexColumnProps:{ fixed:true },
  pagination:{ pageSize: 200,simple:true},
  searchInfo: pageParameter,
});

function fetchSuccess(data) {
  tableTotal.value=data.items.length
  if (data.items.length < 21) {
    for (let i = data.items.length; i < 50; i++) {
      data.items.push({cusCclassName: ''})
    }
  }
  setTableData(data.items)
}
const openAddPage = (ttype) => {
  if(ttype=='edit' && getSelectRows().length==0){
    return createWarningModal({ content: '请选择一条数据进行编辑！' });
  }
  openSavePage(true, {
    data: ttype==='add'?'':getSelectRows()[0],
    type:ttype
  });
}
const saveData = async (data) => {
  await savePwdRule(data);
  clearSelectedRowKeys()
  reload()
  if(data.flag==='add'){
    openAddPage(data.flag)
  }
}
const delData = async () => {
  await delPwdRule(getSelectRows())
  clearSelectedRowKeys()
  reload()
}

</script>
<style src="../../../../assets/styles/global-menu-index-block.less" lang="less" ></style>
<style src="../../../../assets/styles/global-menu-index.less" lang="less" ></style>
<style src="../../../../assets/styles/global-container-index.less" lang="less" ></style>
<style scoped>
.a-table-font-size-12 :deep(td),
.a-table-font-size-12 :deep(th) {
  font-size: 14px !important;
  padding: 2px 8px !important;
}
.app-container:nth-of-type(2) {
  padding: 0px;
  margin: 5px 10px;
  background: #b4c8e3 !important;
  position: relative;
  .pagination-text {
    position: absolute;
    bottom: 6px;
    right: 10%;
    font-size: 13px;
    color: black;
    z-index: 99999999;
  }
}
</style>

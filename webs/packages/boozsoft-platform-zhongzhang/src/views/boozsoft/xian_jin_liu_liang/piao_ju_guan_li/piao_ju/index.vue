<template>
  <div class="app-container">
    <div class="app-container-head">
      <img src="/img/menu/index_open_invoice.png" class="container-head-img" />
      <div class="container-head-title">
        <span style="margin-left: 10px"><b>票据管理</b></span>
      </div>
      <div class="ant-btn-group" data-v-a1ccd506="" style="float: right">
        <button
          type="button"
          class="ant-btn"
          ant-click-animating-without-extra-node="false"
          @click="openAddPage()"
          ><span>新建</span></button
        >
        <button type="button" class="ant-btn" ant-click-animating-without-extra-node="false"
          ><span>删除</span></button
        >
      </div>
    </div>

    <div style="clear: both"></div>
    <PageWrapper dense content-full-height fixed-height content-class="flex">
      <BasicTable
        class="w-4/5 xl:w-5/6"
        :row-selection="{ type: 'checkbox' ,fixed: true}"
        @register="registerTable"
      >
        <template #fapiaoNumber="{ record }">
          <span>
            <a-button type="link" :size="size" @click="openEdit(record)">{{
              record.fapiaoNumber
            }}</a-button>
          </span>
        </template>

        <template #action="{ record }">
          <div>
            <a-popover placement="bottom">
              <a-button style="padding: 0px 4px; height: 20px">
                <CaretDownFilled />
              </a-button>
              <template #content>
                <p style="cursor: pointer" @click="openEdit(record)"><FormOutlined /> 编辑</p>
                <p style="cursor: pointer"><CheckCircleOutlined /> 启用</p>
                <p style="cursor: pointer"><CloseCircleOutlined /> 停用</p>
                <p style="cursor: pointer" @click="del()"><DeleteOutlined /> 删除</p>
              </template>
            </a-popover>
          </div>
        </template>
      </BasicTable>
      <Edit @save="saveData" @register="registerEditPage" />
    </PageWrapper>
  </div>
</template>
<script setup lang="ts">
  import { BasicTable, useTable } from '/@/components/Table';
  import { useModal } from '/@/components/Modal';
  import { ref } from 'vue';
  import {
    Select as ASelect,
    Input as AInput,
    Popover as APopover,
    Checkbox as ACheckbox,
    Modal as AModal,
    Upload as AUpload,
  } from 'ant-design-vue';
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
  } from '@ant-design/icons-vue';
  import {
    findAllElectronicInvoice,
    eletronicInvoiceSaveApi,
  } from '/@/api/record/bank/eletronic_invoice';
  import Edit from './popup/edit.vue';

  // 超链接大小
  const size = ref('small');

  const CrudApi = {
    list: findAllElectronicInvoice,
    columns: [
      {
        title: 'ID',
        dataIndex: 'id',
        defaultHidden: true,
        ellipsis: true,
      },
      {
        title: '购买方单位名称',
        dataIndex: 'buyerSupplier',
        width: 100,
        ellipsis: true,
      },
      {
        title: '销售方单位名称',
        dataIndex: 'sellSupplier',
        width: 100,
        ellipsis: true,
      },
      {
        title: '发票代码',
        dataIndex: 'fapiaoCode',
        width: 100,
        ellipsis: true,
      },
      {
        title: '发票号码',
        dataIndex: 'fapiaoNumber',
        width: 100,
        ellipsis: true,
        slots: { customRender: 'fapiaoNumber' },
      },
      {
        title: '机器编号',
        dataIndex: 'machineCode',
        width: 100,
        ellipsis: true,
      },
      {
        title: '开票内容',
        dataIndex: 'fapiaoContent',
        width: 100,
        ellipsis: true,
      },
      {
        title: '发票金额',
        dataIndex: 'fapiaoMoney',
        width: 100,
        ellipsis: true,
      },
      {
        title: '发票类型',
        dataIndex: 'fapiaoType',
        width: 100,
        ellipsis: true,
      },
      {
        title: '收款人',
        dataIndex: 'fapiaoPayee',
        width: 100,
        ellipsis: true,
      },
      {
        title: '开票人',
        dataIndex: 'fapiaoDrawer',
        width: 100,
        ellipsis: true,
      },
      {
        title: '复核人',
        dataIndex: 'fapiaoCheckPsn',
        width: 100,
        ellipsis: true,
      },
      {
        title: '备注',
        dataIndex: 'fapiaoRemarks',
        width: 100,
        ellipsis: true,
      },
    ],
  };
  // 这是示例组件
  const [registerTable, { reload }] = useTable({
    api: CrudApi.list,
    columns: CrudApi.columns,
    bordered: true,
    showIndexColumn: false,
    actionColumn: {
      width: 80,
      title: '操作',
      dataIndex: 'action',
      slots: { customRender: 'action' },
    },
  });
  const [registerEditPage, { openModal: openEditPage }] = useModal();
  const val = {
    id: '',
    uniqueCode: '',
    operationDate: '',
    userUniqueCode: '',
    fapiaoSum: '',
    fapiaoType: '',
    buyerSupplier: '',
    buyerShuihao: '',
    buyerAddrPhone: '',
    buyerBankAccount: '',
    fapiaoDate: '',
    fapiaoCode: '',
    fapiaoNumber: '',
    fapiaoCheckCode: '',
    machineCode: '',
    fapiaoDrawer: '',
    fapiaoCheckPsn: '',
    fapiaoPayee: '',
    fapiaoContent: '',
    fapiaoMoney: '',
    fapiaoTaxAmount: '',
    fapiaoTotalAmount: '',
    fapiaoRemarks: '',
    fapiaoQrCode: '',
    fapiaoSave_Dir: '',
    sellSupplier: '',
    sellShuihao: '',
    sellAddrPhone: '',
    sellBankAccount: '',
    imgName: '',
    fapiaoCheck: '0',
    tableData: '',
  };

  const openAddPage = () => {
    openEditPage(true, {
      data: val,
    });
  };

  const openEdit = (data: any) => {
    openEditPage(true, {
      data: data,
      height: '900px',
    });
  };
  const del = async () => {
    // await deletePsn(id, data)
    alert('删除成功！');
    reload();
  };

  async function saveData(data: any) {
    console.log(data);
    await eletronicInvoiceSaveApi(data);
    reload();
  }
</script>
<style src="../../../../../assets/styles/global-menu-index-block.less" lang="less" scope></style
>
<style src="../../../../../assets/styles/global-menu-index.less" lang="less" scope></style
>

<style scoped>
  .vben-basic-table {
    width: 99%;
  }
</style>

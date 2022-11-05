<template>
  <div class="app-container">
    <div class="app-container-head">
      <img src="/img/menu/index_dept.png" />

      <span style="margin-left: 10px"><b>SSO用户管理</b></span>
      <div style="display: inline">
        <select class="head-index-select">
          <option style="border: none; outline: none" value="">查看全部 </option>
        </select>
      </div>

      <div class="ant-btn-group" data-v-a1ccd506="" style="float: right">
        <button
          type="button"
          class="ant-btn"
          ant-click-animating-without-extra-node="false"
          @click="openAddPage()"
          ><span>新建</span></button
        >
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

        <a-button @click="activeKey = !activeKey">
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
    <Edit @save="saveData" @register="registerEditPage" />
    <BasicTable
      v-if="activeKey"
      :row-selection="{ type: 'checkbox' ,fixed: true}"
      @row-click="condClick"
      @register="registerTable"
    >
      <template #userType="{ record }"> {{ formatUserType(record.userType) }} </template>

      <template #action="{ record }">
        <div>
          <a-popover placement="bottom">
            <template #content>
              <p style="cursor: pointer" @click="openEdit(record)"><FormOutlined /> 编辑</p>
              <p v-if="record.flag === '0'" style="cursor: pointer" @click="upFlag(record.id, '1')"
                ><CheckCircleOutlined /> 启用</p
              >
              <p v-if="record.flag === '1'" style="cursor: pointer" @click="upFlag(record.id, '0')"
                ><CloseCircleOutlined /> 停用</p
              >
              <p style="cursor: pointer" @click="del(record.id)"><DeleteOutlined /> 删除</p>
            </template>
            <a-button>
              <CaretDownFilled />
            </a-button>
          </a-popover>
        </div>
      </template>
    </BasicTable>
    <div v-if="!activeKey" :class="cardList">
      <div :class="`${cardList}__content`">
        <!--        <div>ID:{{ item.id }}</div>-->
        <a-list style="height: 700px; overflow-y: scroll; overflow-x: hidden">
          <a-row :gutter="16">
            <template v-for="(item, index) in cardList" :key="index">
              <a-col :span="6">
                <a-list-item style="width: 100%">
                  <a-card
                    :hoverable="true"
                    :class="`${cardList}__card`"
                    :style="
                      item.flag === '1' ? 'width: 100%;' : 'width: 100%;background-color: #b7b5b5;'
                    "
                  >
                    <div style="width: 30%; float: left"></div>
                    <div style="width: 100%; float: left">
                      <div :class="`${cardList}__card-title`">
                        <Icon v-if="item.icon" class="icon" :icon="item.icon" :color="item.color" />
                      </div>
                      <div
                        style="
                          float: right;
                          border: 1px solid rgb(1, 143, 251);
                          border-radius: 10px;
                          padding: 0 10px;
                        "
                      >
                        {{ formatUserType(item.userType) }}
                      </div>
                      <div style="font-size: 24px; width: 100%; text-align: center">
                        {{ item.userName }}
                      </div>
                      <div style="font-size: 16px; width: 100%; text-align: left">
                        手机号（用于登陆）: {{ item.phone }}
                      </div>
                      <div style="font-size: 16px; width: 100%; text-align: left">
                        邮箱: {{ item.email }}
                        <div style="float: right">
                          <Icon
                            class="icon"
                            icon="ant-design:ellipsis-outlined"
                            size="16"
                            @click="del(item.id)"
                          />
                        </div>
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
  import { saveCao, deletById, updateFlag } from '/@/api/record/system/caozuoyuan';
  import { BasicTable, useTable, TableAction } from '/@/components/Table';
  import Edit from './popup/edit.vue';
  import { useModal } from '/@/components/Modal';
  import { onMounted, reactive, ref, unref } from 'vue';
  import Icon from '/@/components/Icon/index';
  import { useMessage } from '/@/hooks/web/useMessage';

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
    TaobaoCircleFilled,
    CodepenCircleFilled,
  } from '@ant-design/icons-vue';
  import {Col as ACol,Popover as APopover,Input as AInput,Checkbox as ACheckbox, Row as ARow,Card as ACard,List as AList} from 'ant-design-vue'
  const AInputSearch=AInput.Search
  const AListItem=AList.Item
  const ACheckboxGroup=ACheckbox.Group
  import { findAllBoozUser, saveBoozUserApi} from '/@/api/sys/user';


  const CrudApi = {
    list: findAllBoozUser,
    columns: [
      {
        title: 'ID',
        dataIndex: 'id',
        defaultHidden: true,
        ellipsis: true,
      },
      {
        title: '用户名',
        dataIndex: 'userName',
        ellipsis: true,
      },
      {
        title: '手机号',
        dataIndex: 'phone',
        ellipsis: true,
      },
      {
        title: '邮箱',
        dataIndex: 'email',
        ellipsis: true,
      },
      {
        title: '类型',
        dataIndex: 'userType',
        ellipsis: true,
        slots: { customRender: 'userType' },
      },
    ],
    editData: {
      id: '',
      uniqueCode: '',
      account: '',
      password: '',
      userName: '',
      phone: '',
      email: '',
      roleName: '',
      flag: '',
      userType: '',
    },
  };
  // 这是示例组件
  const [registerTable, { instance, formInstance, reload }] = useTable({
    api: CrudApi.list,
    columns: CrudApi.columns,
    bordered: true,
    showIndexColumn: false,
    showTableSetting: true,
    actionColumn: {
      width: 160,
      title: '操作',
      dataIndex: 'action',
      slots: { customRender: 'action' },
    },
  });
  const [registerEditPage, { openModal: openEditPage }] = useModal();
  const { createConfirm } = useMessage();
  const val = {
    id: '',
    account: '',
    password: '',
    userName: '',
    phone: '',
    email: '',
    flag: '',
  };
  //table row  click
  const condClick = (data, index, e) => {
    if (e.target.cellIndex === 1) {
      openEditPage(true, {
        data: CrudApi.editData,
      });
    }
  };
  //add
  const openAddPage = (data, index, e) => {
    openEditPage(true, {
      data: val,
    });
  };
  //edit
  const openEdit = (data) => {
    openEditPage(true, {
      data: data,
    });
  };

  const activeKey = ref(false);
  const prefixCls = 'list-card';
  const cardList = ref({});

  onMounted(async () => {
    await reloadCurrentPage();
  });
  //list
  async function reloadCurrentPage() {
    const res = await findAllBoozUser();
    cardList.value = res.items;
  }
  //save
  async function saveData(data) {
    await saveBoozUserApi(data);
    await reloadCurrentPage();
    reload();
  }

  //format data
  function formatUserType(type) {
    let str = '默认用户';
    switch (type) {
      case '1':
        str = '默认用户';
        break;
      case '2':
        str = '默认用户';
        break;
      case '3':
        str = '默认用户';
        break;
    }
    return str;
  }

  //edit updateFlag
  async function upFlag(id, flag) {
    let str = '启用';
    if (flag === '0') {
      str = '停用';
    }
    createConfirm({
      iconType: 'warning',
      title: str,
      content: '请确认是否' + str + '操作!',
      onOk: async () => {
        await updateFlag(id, flag);
        await reloadCurrentPage();
        reload();
      },
    });
  }

  //del
  async function del(id) {
    createConfirm({
      iconType: 'warning',
      title: '删除',
      content: '请确认是否删除!',
      onOk: async () => {
        await deletById(id);
        await reloadCurrentPage();
        reload();
      },
    });
  }
  function onSearch(){

  }
</script>
<style src="../../../../assets/styles/global-menu-index-block.less" lang="less" scoped></style
>
<style src="../../../../assets/styles/global-menu-index.less" lang="less" scoped></style
>
<style scoped lang="less">
  :deep(.ant-card-body) {
    padding: 10px;
    border-left: 2px solid rgb(1, 143, 251);
    box-shadow: rgb(72 113 140) -3px 1px 7px -1px;
  }
</style>

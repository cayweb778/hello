<template>
  <div>
    <div class="app-container">
      <div class="app-container-head">
        <div class="container-head-title" style="float: left;margin-top: 10px;">
          <b class="noneSpan" style="font-size: 60px;"><ProfileOutlined /></b>
        </div>
        <div class="container-head-title" style="padding-left: 35%;text-align: center;margin-top: 30px;">
          <b class="noneSpan" style="font-size: 36px;">会计科目</b>
        </div>
        <div class="ant-btn-group" style="float: right">
          <button type="button" class="ant-btn ant-btn-me" ><span>在线同步</span></button>
          <button type="button" class="ant-btn ant-btn-me" @click="openImportExcel"><span>新增</span></button>
          <button type="button" class="ant-btn ant-btn-me" @click="closeCurrent(),router.push('/system/home/welcome')"><span>退出</span></button>
        </div>
      </div>
      <div style="clear: none"/>
      <div style="margin-top: -30px;margin-left: 80px;">
        <div style="display: inline-block;position:absolute;top:85px;left:110px;font-size: 10px;color:#999999;">共 {{cardList.length}} 条记录</div>
        <div style="float: right; margin-left: 10px;">
          <a-button @click="findAll()">
            <SyncOutlined :style="{ fontSize: '14px' }"/>
          </a-button>
        </div>
        <div style="float: right; position: relative">
          <a-input-search placeholder="" style="width: 200px; border-radius: 4px" @search="findByinput"/>
        </div>
      </div>
      <div style="clear:both"/>
    </div>
    <div class="app-container">
      <!--方块矩阵-->
      <div v-if="!activeKey" :class="cardList" style="height: 700px">
            <div :class="`${cardList}__content`">
              <a-list >
                <a-row :gutter="16">
                  <template v-for="(item, index) in cardList" :key="index">
                    <a-col :span="6">
                      <a-list-item style="width: 100%">
                        <a-card :hoverable="true" :class="`${cardList}__card`" style="width: 100%">
                          <div style="width: 100%; padding: 0">
                            <div>
                              <div style="float: left; width: 12%">
                                <img src="/payment.svg" style="width: 70%;margin-top: -5px;" />
                              </div>
                              <div>
                                <label style="font-size: 16px; font-weight: bolder">
                                  <a
                                    target="_blank"
                                    rel="noopener noreferrer"
                                    @click="toRouter(item.uniqueAccStandard,item.accStyleUnique,item.codeFirst)"
                                  >
                                    {{ item.accStandardName }}</a
                                  >
                                    <a-tooltip color="white">
                                      <template #title><span style="color: black;"><a @click="confirmDel(item.uniqueAccStandard)">删除</a></span></template>
                                      <a-button style="float: right; height: 25px; border-radius: 8px;padding-top: inherit;">{{
                                          item.num
                                        }}</a-button>
                                    </a-tooltip>
                                </label>
                                <br />
                                <label style="font-size: 13px;">科目类型：{{ setString(item.styleName,28) }}</label>
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
    <Edit @register="registerEditPage" />
    <ImprotExcel @save="saveExcelData" @register="registerExcelPage" />
  </div>
</template>
<script setup lang="ts">
  import { BasicTable, useTable, TableAction } from '/@/components/Table';
  import Edit from './popup/edit.vue';
  import ImprotExcel from './popup/improtExcel.vue';
  import { useModal } from '/@/components/Modal';
  import { onMounted, reactive, ref, toRaw } from 'vue';
  import {
    SyncOutlined,UnorderedListOutlined,ProfileOutlined
  } from '@ant-design/icons-vue';
  import {
    findAllAcctandard,
    findByUniqueAccStandardDelStandard,
    saveStandard
  } from '/@/api/accstandard/accstandard';
  import {
    Tooltip as ATooltip,
    Card as ACard,
    List as AList,
    Menu as AMenu,
    Tabs as ATabs,
    Select as ASelect,
    Input as AInput,
    Row as ARow,
    Col as ACol,
    Statistic as AStatistic,
  } from 'ant-design-vue';
  import {useTabs} from "/@/hooks/web/useTabs";
  import router from "/@/router";
  import {findByUniqueAccStandard} from "/@/api/acctemplate/acctemplate";
  import {useMessage} from "/@/hooks/web/useMessage";
  const { closeCurrent } =useTabs();
  const ASelectOption = ASelect.Option;
  const AInputSearch = AInput.Search;
  const AStatisticCountdown = AStatistic.Countdown;
  const ATabPane = ATabs.TabPane;
  const AMenuItem = AMenu.Item;
  const AListItem = AList.Item;
  const { createConfirm,createWarningModal,createMessage } = useMessage();
  // 这是示例组件
  const [registerTable, { instance, formInstance, reload }] = useTable({});
  const [registerEditPage, { openModal: openEditPage }] = useModal();
  const [registerExcelPage, { openModal: openExcelPage }] = useModal();

  const activeKey = ref(false);
  const prefixCls = 'list-card';
  const cardList = ref({});

  onMounted(async () => {
    findAll()
  });

  function toRouter(uniqueAccStandard,accStyleUnique,codeFirst) {
    let map={
      data:uniqueAccStandard+'-'+accStyleUnique+'BOOZ'+codeFirst,templateId:uniqueAccStandard,tType: '系统模板',jici: '4-2-2-2-2-2-2-2-2-2-2-2-2-2-2-2'
    }
    router.push({name: 'GJKeMu',params: map})
  }
  //js切割字符串
  function setString(str, len) {
    var strlen = 0;
    var s = "";
    for (var i = 0; i < str.length; i++) {
      if (str.charCodeAt(i) > 128) {
        strlen += 2;
      } else {
        strlen += 1.2;
      }
      s += str.charAt(i);
      if (strlen >= len) {
        return s+"...";
      }
    }
    return s;
  }

  async function findAll() {
    cardList.value=[]
    const res = await findAllAcctandard();
    cardList.value = res.items;
  }
  function openImportExcel() {
    openExcelPage(true, {
      data: '',
    });
  }
  async function saveExcelData(data) {
    await saveStandard(data.standardName,data.codeTyle)
    .then(item=>{
      findAll()
    })
  }

  async function confirmDel(data) {

    createConfirm({
      iconType: 'error',
      title: '警告',
      content: '删除后数据不能恢复，你确认要删除吗？',
      onOk: async() => {
        let a=await findByUniqueAccStandard(data)
        await findByUniqueAccStandardDelStandard(data,a.items[0].id).then(item=>{
          findAll()
        })
      }
    })
  }

  async function findByinput(data) {
    let temp=cardList.value
    if(data!==''){
      cardList.value=temp.filter(v=>v.accStandardName.indexOf(data)!=-1)
    }else{
      findAll()
    }
  }
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
  background-color: #b4c8e3 !important;
  background-image: url("/@/assets/images/homes/bg-pattern.png");
  background-repeat: no-repeat;
  background-position: 50% 50%;
}

:deep(.ant-input),:deep(.ant-select),:deep(.ant-btn){
  border: 1px solid #c9c9c9;
}
</style>


<template>
  <div>
    <div class="app-container">
      <div class="app-container-head">
        <div class="container-head-title" style="float: left;margin-top: 10px;">
          <b class="noneSpan" style="font-size: 60px;"><ProfileOutlined /></b>
        </div>
        <div class="container-head-title" style="padding-left: 35%;text-align: center;margin-top: 30px;">
          <b class="noneSpan" style="font-size: 36px;">集团会计科目制度</b>
        </div>
        <div class="ant-btn-group" style="float: right">
          <button
            type="button"
            class="ant-btn ant-btn-me"
            ant-click-animating-without-extra-node="false"
            @click="openAddPage()"
          ><span>新建</span></button>
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
      <div v-if="!activeKey" :class="cardList">
            <div :class="`${cardList}__content`">
              <a-list style="height: 1000px;">
                <a-row :gutter="16">
                  <template v-for="(item, index) in cardList" :key="index">
                    <a-col :span="6">
                      <a-list-item style="width: 100%">
                        <a-card :hoverable="true" :class="`${cardList}__card`" style="width: 100%">
                          <div style="width: 100%; padding: 0">
                            <div>
                              <div style="float: left; width: 12%">
                                <img src="/payment.svg" style="width: 70%;" />
                              </div>
                              <div>
                                <label style="font-size: 14px">准则：{{ item.accStandardName }}</label>
                                <label style="font-size: 14px;float: right;" title="删除">
                                  <a class="btnHover" @click="deltemplate(item)"><DeleteOutlined /></a>
                                </label>
                                <br>
                                <label style="font-size: 14px;">
                                  名称：
                                  <a
                                    target="_blank"
                                    rel="noopener noreferrer"
                                    @click="toRouter(item.uniqueAccStandard,item.accStyleUnique,item.codeFirst,item.id,item.ttype,item.tjici)"
                                  >
                                    {{ item.tname }}</a>
                                </label>
                                <label style="font-size: 14px;float: right;" title="复制"><a @click="openCopyView(item)" class="btnHover"><CopyOutlined /></a></label>
                                <br/>
                                <label>科目类型：<span style="font-size: 4px">{{ setString(item.styleName,35) }}</span></label>
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
    <AddPage @save="saveData" @register="registerSavePage" />
    <CopyPage @save="saveCopyData" @register="registerCopyPage" />
  </div>
</template>
<script setup lang="ts">
import {BasicTable, useTable, TableAction} from '/@/components/Table';
import AddPage from './popup/save.vue';
import CopyPage from './popup/copy.vue';
import {useModal} from '/@/components/Modal';
import {onMounted, reactive, ref, toRaw, unref} from 'vue';
import {
  UnorderedListOutlined,
  CopyOutlined,
  DeleteOutlined,
  SyncOutlined,ProfileOutlined
} from '@ant-design/icons-vue';
import {
  findByCustomTemplate,
  accTemplateSave,
  accTemplateDel,
  codekemuDel,
} from '/@/api/acctemplate/acctemplate';

import {
  copyCode
} from '/@/api/codekemu/codekemu';
import {
  Popconfirm as APopconfirm,
  Form as AForm,
  Card as ACard,
  List as AList,
  Dropdown as ADropdown,
  Menu as AMenu,
  Tabs as ATabs,
  Select as ASelect,
  Input as AInput,
  Popover as APopover,
  Checkbox as ACheckbox,
  Modal as AModal,
  Upload as AUpload,
  Row as ARow,
  Col as ACol,
  Statistic as AStatistic,
} from 'ant-design-vue';
import { message } from 'ant-design-vue';
import {useTabs} from "/@/hooks/web/useTabs";
import router from '/@/router';
import {useMessage} from "/@/hooks/web/useMessage";
import {countAllByAccStandard} from "/@/api/record/system/account";
import {countAllByUniqueAccStandard} from "/@/api/record/group/im-organize";

const ASelectOption = ASelect.Option;
const AInputSearch = AInput.Search;
const AStatisticCountdown = AStatistic.Countdown;
const ATabPane = ATabs.TabPane;
const AMenuItem = AMenu.Item;
const AListItem = AList.Item;
const { createConfirm,createWarningModal,createMessage } = useMessage();
// 这是示例组件
const [registerTable, {instance, formInstance, reload}] = useTable({});
const [registerSavePage, { openModal: openSavePage }] = useModal();
const [registerCopyPage, { openModal: openCopyPage }] = useModal();
const activeKey = ref(false);
const prefixCls = 'list-card';
const cardList = ref({});
const { closeCurrent } =useTabs();

async function saveCopyData(data:any) {
  const map = {
    tFlg: data.tFlg,
    tName: data.tName,
    uniqueAccStandard: data.uniqueAccStandard,
    tType: '自定义模板',
    tJici: data.tJici,
  };
  let copyflg=data.tFlg
  const a = await accTemplateSave(map);
  // 预置科目
  if(copyflg=='1'){
    // 复制科目
    await copyCode(data.uniqueAccStandard,data.copytemplateid,a.id)
  }
  await findAll();
}

function toRouter(uniqueAccStandard,accStyleUnique,codeFirst,id,ttype,tjici) {
  let map={
    data:uniqueAccStandard+'-'+accStyleUnique+'BOOZ'+codeFirst,templateId:id,tType: ttype,jici: tjici
  }
  router.push({name: 'JiTuanKeMu',params: map})
}

async function deltemplate(data) {
  // 是否账套在用
  let findByTemplate=await countAllByAccStandard(data.id)
  if(findByTemplate>0){
    createConfirm({
      iconType: 'warning',
      title: '警告',
      content: '已有账套在使用,无法删除!',
      onOk: async () => {}
    })
    return false;
  }

  // 是否组织在用
  let findByOrg=await countAllByUniqueAccStandard(data.id)
  if(findByOrg>0){
    createConfirm({
      iconType: 'warning',
      title: '警告',
      content: '已有组织在使用,无法删除!',
      onOk: async () => {}
    })
    return false;
  }
  createConfirm({
    iconType: 'error',
    title: '警告',
    content: '确定删除此模板及所有科目吗？',
    onOk: async () => {
      await accTemplateDel(data.id);
      await codekemuDel(data.id);
      await findAll();
    }
  })
}

function openCopyView(data:any){
  openCopyPage(true, {
    data: data,
  });
}
function openAddPage(){
  openSavePage(true, {
    data: '',
  });
}

const saveData = async (data: any) => {
  const map = {
    tFlg: data.tFlg,
    tName: data.tName,
    uniqueAccStandard: data.uniqueAccStandard,
    tType: '自定义模板',
    tJici: data.tJici,
  };
  const a = await accTemplateSave(map);
  if (a.code === '400') {
    await accTemplateDel(a.templateId);
    await codekemuDel(a.templateId);
    message.error(a.msg);
    return;
  }
  findAll();
};
async function findAll() {
  cardList.value=[]
  const res = await findByCustomTemplate('自定义模板');
  cardList.value = res.items;
}
onMounted(async () => {
  await findAll();
});
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

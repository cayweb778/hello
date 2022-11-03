<template>
  <div>
    <div class="app-container">
      <div class="app-container-head">
        <div class="ant-btn-group" style="float: right">
          <button type="button" class="ant-btn ant-btn-me" @click="closeCurrent(),router.push('/origin/home/welcome')"><span>退出</span></button>
        </div>
        <div class="container-head-title" style="float: left;">
          <b class="noneSpan" style="font-size: 60px;color: rgb(0 150 199)"><ProfileOutlined /></b>
        </div>
        <div class="container-head-title" style="text-align: center;">
          <b class="noneSpan" style="font-size: 36px;color: rgb(0 150 199)">组织会计科目制度</b>
        </div>

      </div>
      <div style="margin-top: -30px;margin-left: 80px;">
        <div style="display: inline-block;position:absolute;top:85px;left:110px;font-size: 10px;color:#999999;">共 {{cardList.length}} 条记录</div>
      </div>
      <div style="clear:both"/>
    </div>
    <div class="app-container">
      <div v-if="!activeKey" :class="cardList">
        <div :class="`${cardList}__content`">
          <a-list>
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
                            <label style="font-size: 14px">集团：{{ item.parentName }}
                              <label style="font-size: 14px;float: right;" title="编辑">
                                <a class="btnHover" @click="editView(item)"><EditTwoTone /></a>
                              </label>
                            </label><br>
                            <label style="font-size: 14px">组织：{{ item.orgName }}</label><br>
                            <!--                          <label style="font-size: 14px;margin-left: 10%;">准则：{{ item.accStandardName }}</label><br>-->
                            <label style="font-size: 14px;margin-left: 12%;">
                              名称：
                              <a
                                target="_blank"
                                rel="noopener noreferrer"
                                @click="
                                $router.push({
                                  path: '/origin/accountancy/organization-code',
                                  query: {
                                    torganization: item.torganization,
                                    templateId: item.id,
                                    uniqueAccStandard: item.uniqueAccStandard,
                                    tjici: '',
                                    styleName: item.styleName,
                                    accStyleUnique: item.accStyleUnique,
                                    tname: item.tname,
                                  },
                                })
                              "
                              >
                                {{ item.tname }}</a
                              >
                            </label>
                            <br/>
                            <!--                          <label style="margin-left: 18px;">科目类型：<span style="font-size: 4px">{{-->
                            <!--                              item.styleName-->
                            <!--                            }}</span></label>-->
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
  </div>

</template>
<script setup lang="ts">
import {BasicTable, useTable, TableAction} from '/@/components/Table';
import EditPage from './popup/organization_edit.vue';
import {useModal} from '/@/components/Modal';
import {onMounted, reactive, ref, toRaw} from 'vue';
import {
  ProfileOutlined,
  UnorderedListOutlined,
  EditTwoTone,
  SyncOutlined
} from '@ant-design/icons-vue';
import {
  findByCustomTemplate,
  accTemplateSave,
  accTemplateZZSave,
  accTemplateDel,
  codekemuDel,
  editTemplateName, editTemplateNameById,
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
import router from '/@/router';
const ASelectOption = ASelect.Option;
const AInputSearch = AInput.Search;
const AStatisticCountdown = AStatistic.Countdown;
const ATabPane = ATabs.TabPane;
const AMenuItem = AMenu.Item;
const AListItem = AList.Item;
// 这是示例组件
const [registerTable, {instance, formInstance, reload,closeCurrent}] = useTable({});
const [registerEditPage, { openModal: openEditPage }] = useModal();
const activeKey = ref(false);
const prefixCls = 'list-card';
const cardList = ref({});


async function deltemplate(data) {
  await accTemplateDel(data.id);
  await codekemuDel(data.id);
  await findAll();
}

function editView(data) {
  openEditPage(true, {
    data:  data,
  });
}

const editData = async (data:any) => {
  await editTemplateNameById(data.id,data.tName)
  findAll();
}
async function findAll() {
  cardList.value=[]
  const res = await findByCustomTemplate('组织模板');
  cardList.value = res.items;
}
onMounted(async () => {
  await findAll();
});
function closeCur() {
  closeCurrent()
}
async function findByinput(data) {
  let temp=cardList.value
  if(data!==''){
    cardList.value=temp.filter(v=>v.tname.indexOf(data)!=-1)
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

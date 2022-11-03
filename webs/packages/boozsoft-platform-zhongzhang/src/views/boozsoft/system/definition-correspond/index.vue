<template>
  <div class="app-container">
    <EditPage @save="saveData" @register="registerEditPage"/>
    <AddPage @save="saveData" @register="registerSavePage"/>
      <div class="app-container-head"><img :src="getThisIndexImg()" class="container-head-img" draggable="false" >
      <div class="container-head-title">
        <b>系统模板</b>
      </div>
      <div style="display: inline-block;margin: .3%;">
        <a-month-picker
          mode="month"
          format="YYYY-MM"
          valueFormat="YYYY-MM"
          placeholder="选择会计期间"
          class="head-index-year-month"
        >
        </a-month-picker>
      </div>
      <div class="ant-btn-group">
        <button
          type="button"
          class="ant-btn"
          ant-click-animating-without-extra-node="false"
          @click="openAddPage()"
        ><span>新增结转模板</span></button>
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
      <div style="position: relative">
        <!-- 搜索 -->
        <a-input-search
          placeholder=""
          style="width: 200px; border-radius: 4px"
          @search="onSearch"
        />
      </div>
    </div>
    <div class="app-container-content">
      <div
      >
        <div>
          <a-list style="overflow-y: auto;overflow-x: hidden;height: 635px;">
            <a-row :gutter="16">
              <template
                v-for="(item, index) in pageParameter.templateList"
                :key="index"
              >
                <a-col :span="6">
                  <a-list-item>
                    <a-card
                      :hoverable="true"
                      style="width: 100%;"
                    >
                      <div style="background-color: aquamarine;color: black">
                        <span>&emsp;{{ item.orderNum }}&emsp;&emsp;{{ item.templateName }}</span>
                        <a-dropdown placement="bottomLeft">
                          <a-button size="small" style="float: right;">更多</a-button>
                          <template #overlay>
                            <a-menu>
                              <a-menu-item>
                                <a target="_blank" @click="openModifyPage(item)">
                                  修改
                                </a>
                                <a target="_blank" @click="delData(item)">
                                  删除
                                </a>
                              </a-menu-item>
                            </a-menu>
                          </template>
                        </a-dropdown>
                      </div>
                      <div style="height: 100px;background-color: #c9c9c9;text-align: center;">
                        <h2 style="color: white">金额： {{ item.carryOverSum }}</h2>
                        <a-button v-if="item.carryOverIsShow=='0'" type="primary" :disabled="item.carryOverSum=='?'">
                          生成凭证
                        </a-button>
                        <a-button v-else>查看凭证</a-button>
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

import {findAllCorresponds, carryoverSaveApi, carryoverDeleteApi} from '/@/api/record/system/definition-correspond';
import { onMounted, reactive} from 'vue';
import {useModal} from "/@/components/Modal";
import AddPage from './popup/save.vue';
import EditPage from './popup/edit.vue';
import {useMessage} from "/@/hooks/web/useMessage";

import {
  Dropdown as ADropdown,
  Menu as AMenu,
  Input as AInput,
  Select as ASelect,
  Button as AButton,
  Row as ARow,
  Col as ACol,
  List as AList,
  Card as ACard,
  DatePicker as ADatePicker,
  Dropdown,
} from "ant-design-vue";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {getCurrentAccountName,getThisIndexImg} from "/@/api/task-api/tast-bus-api";
const AMenuItem = AMenu.Item;
const AMonthPicker = ADatePicker.MonthPicker
const AInputSearch= AInput.Search;
const ASelectOption= ASelect.Option;
const AListItem= AList.Item;

const {
  createConfirm
} = useMessage()
const pageParameter = reactive({
  yearMonth: '',      // 期间
  templateList: [],
})

onMounted(async () => {
  await fetch()
})

async function fetch() {
  const res = await useRouteApi(findAllCorresponds, {schemaName: getCurrentAccountName(true)})({carryOverModule: '1'})

  //const res = await findAllCorresponds({carryOverModule: '1'})
  pageParameter.templateList = res
}

const [registerEditPage, {openModal: openEditPage}] = useModal()
const [registerSavePage, {openModal: openSavePage}] = useModal()

const openAddPage = () => {
  openSavePage(true, {
    data: {}
  })
}
const openModifyPage = (data: any) => {
  openEditPage(true, {
    data: data
  })
}

async function saveData(data: any) {
  await carryoverSaveApi(data)
  await fetch()
}

async function delData(data: any) {
  createConfirm({
    iconType: 'warning',
    title: '模板删除',
    content: '请确认是否删除【'+ data.templateName +'模板】操作!',
    onOk: async() => {
      await carryoverDeleteApi({id: data.id})
      await fetch()
    }
  });
}
async function onSearch(){

}
</script>
<style src="../../../../assets/styles/global-menu-index.less" lang="less" scoped></style>
<style scoped lang="less">
:deep(.ant-card-body) {
  padding: 5px;
  border-left: 2px solid rgb(1, 143, 251);
  box-shadow: rgb(72 113 140) -3px 1px 7px -1px;
}
</style>

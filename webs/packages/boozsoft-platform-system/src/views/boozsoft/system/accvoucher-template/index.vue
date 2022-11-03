<template>
  <div>
    <div class="app-container">
      <div class="app-container-head">
        <div class="container-head-title" style="margin-left: 20px">
          <b class="noneSpan"><SettingOutlined style="font-size: 26px;" />&nbsp;&nbsp;凭证模板导入</b>
        </div>
        <div class="ant-btn-group" style="float: right">
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
            @click="openAddPage()"
          ><span>退出</span></button>
        </div>
      </div>
      <div class="app-container-neck">
        <div style="float: right; position: relative">
          <!-- 搜索 -->
          <a-input-search
            placeholder=""
            style="width: 200px; border-radius: 4px"
            @search="selectSearch"
          />
        </div>
      </div>
    </div>
    <div class="app-container">
      <div
        :class="cardList"
      >
        <div :class="`${cardList}__content`">
          <a-list :style="{height: treeHeight,overflowY: 'scroll',overflowX: 'hidden'}">
            <a-row :gutter="16">
              <template
                v-for="(item,index) in cardList"
                :key="index"
              >
                <a-col :span="6">
                  <a-list-item style="width: 95%">
                    <a-card
                      :hoverable="true"
                      :class="`${cardList}__card`"
                      style="width: 100%"
                    >
                      <div style="width: 100%;float: right;">
                        <div style="float: right; border: 1px solid rgb(1, 143, 251);border-radius: 10px;padding: 0 10px; margin-top: -5px;">
                          {{index+1}}
                        </div>
                        <div style="font-size: 24px;margin-top: 20px;">
                          {{ item.templateName}}
                        </div>
                        <div style="float: right;margin-top: -20px;">
                          <a-popover placement="bottom">
                            <a-button style="padding: 0px 4px; height: 20px;margin-right: 10px;">
                              <EllipsisOutlined />
                            </a-button>
                            <template #content>
                              <p style="cursor: pointer" class="p_specifics" @click="openEdit(item)"><FormOutlined /> 修改</p>
                              <p style="cursor: pointer" class="p_specifics" @click="del(item)"><DeleteOutlined /> 删除</p>
                            </template>
                          </a-popover>
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
      <!--方块矩阵-->
<!--      <div-->
<!--        :class="cardList"-->
<!--      >-->
<!--        <a-list style="height: 1000px;overflow-y: auto;">-->
<!--          <a-row :gutter="16">-->
<!--            <template-->
<!--              v-for="(item, index) in cardList"-->
<!--              :key="index"-->
<!--            >-->
<!--              <a-col :span="6">-->
<!--                <a-list-item style="width: 95%;">-->
<!--                  <a-card-->
<!--                    :hoverable="true"-->
<!--                    :class="`${cardList}__card`"-->
<!--                    style="width: 100%;"-->
<!--                  >-->
<!--                    <div style="width: 100%;float: right;">-->
<!--                      <div>-->
<!--                        <div style="float: left; width: 20%;">-->
<!--                          <img src="/favicon.ico" style="width: 70%;">-->
<!--                        </div>-->
<!--                        <div>-->
<!--                          <label style="font-size: 14px;font-weight: bolder">-->
<!--                            <a-dropdown placement="bottomLeft">-->
<!--                              <a-button  style="float: right;height: 20px;line-height: 6px;padding-bottom: 8px;border-radius: 8px;">...</a-button>-->
<!--                              <template #overlay>-->
<!--                                <a-menu>-->
<!--                                  <a-menu-item>-->
<!--                                    <a target="_blank" rel="noopener noreferrer" @click="openEdit(item)">-->
<!--                                      编辑-->
<!--                                    </a>-->
<!--                                  </a-menu-item>-->
<!--                                  <a-menu-item>-->
<!--                                    <a target="_blank" rel="noopener noreferrer" @click="del(item)">-->
<!--                                      删除-->
<!--                                    </a>-->
<!--                                  </a-menu-item>-->
<!--                                </a-menu>-->
<!--                              </template>-->
<!--                            </a-dropdown>-->
<!--                          </label>-->
<!--                          <h2>模板名称：{{ item.templateName }}</h2>-->
<!--                          <label style="font-size: 14px">模板类型：{{ item.templateType==0?'简约':'标准' }}</label>-->
<!--                        </div>-->
<!--                      </div>-->
<!--                    </div>-->
<!--                  </a-card>-->
<!--                </a-list-item>-->
<!--              </a-col>-->
<!--            </template>-->
<!--          </a-row>-->
<!--        </a-list>-->
<!--      </div>-->
      <Edit
        @save="saveData"
        @register="registerEditPage"
      />
    </div>
  </div>
</template>
<script setup lang="ts">
import { PageWrapper } from '/@/components/Page'
import {DatePicker as ADatePicker,Select as ASelect,Input as AInput,Popover as APopover,Switch as ASwitch,
  Radio as ARadio,Upload as AUpload,List as AList,Row as ARow,Menu as AMenu,Dropdown as ADropdown,Card as ACard
  ,Col as ACol,Divider as ADivider} from "ant-design-vue"
const ARangePicker=ADatePicker.RangePicker
const ASelectOption=ASelect.Option
const AInputSearch=AInput.Search
const ARadioGroup=ARadio.Group
const AListItem=AList.Item
const AMenuItem=AMenu.Item
import { SettingOutlined,EllipsisOutlined } from '@ant-design/icons-vue'
import { AccvoucherTemplateSaveApi, findAllAccvoucherTemplate,accvoucherTemplateNewDeleteApi } from '/@/api/record/system/accvoucher_template'
import { useModal } from '/@/components/Modal'
import Edit from './popup/edit.vue'
import { message } from 'ant-design-vue'
import {useMessage} from "/@/hooks/web/useMessage";
import {onMounted, ref} from "vue";
const cardList = ref({})
const copyCardList = ref({})

onMounted(async() => {
  await reloadRows()
})

async function reloadRows() {
  await findAllAccvoucherTemplate().then(res => {
    cardList.value = res
    copyCardList.value = res
  })
}
const {
  createConfirm,createSuccessModal
} = useMessage()

const [registerEditPage, { openModal: openEditPage }] = useModal()

const val = {
  id: '',
  templateName: '',
  templateNumber: '',
  templateType: '1',
  entryList: '',
}

const openAddPage = () => {
  openEditPage(true, {
    data: val
  })
}

const openEdit = (data: any) => {
  openEditPage(true, {
    data: data
  })
}
const del = async(data) => {
  createConfirm({
    iconType: 'warning',
    title: '模板删除',
    content: '请确认是否删除【'+ data.templateName +'模板】操作!',
    onOk: async(close) => {
      try {
        message.info("数据后台更新中。。。请稍候！")
        await accvoucherTemplateNewDeleteApi({id: data.id})
        close()
      }catch (e) {
        await reloadRows()
      }
    }
  });
}
async function selectSearch(obj) {
  let value  = obj.target._value
  if (value == null || value == ''){
    cardList.value =   copyCardList.value
  }else {
    cardList.value =   copyCardList.value.filter((item)=>item.templateName.indexOf(value) != -1 || (value=='简约' && item.templateType=='0') || (value=='标准' && item.templateType=='1'))
  }
}
async function saveData(data: any) {
  message.info("数据后台更新中。。。请稍候！")
  AccvoucherTemplateSaveApi(data).then(res=>{
    message.info("更新成功！")
    reloadRows()
  })
}
</script>
<style src="../../../../assets/styles/global-menu-index.less" lang="less" scoped></style>
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
  background: #b4c8e3 !important;
}
</style>

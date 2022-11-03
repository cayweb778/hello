<template>
  <BasicModal
    width="500px"
    v-bind="$attrs"
    @ok="handleOk()"
    @register="register"
    :canFullscreen="false"
  >
    <template #title>
      <div style="display: flex;" class="vben-basic-title">
        <TeamOutlined style="font-size: 50px;color: #0096c7;"/>
        <span style="line-height:50px;font-size: 30px;color: #0096c7;">&ensp;权限复制</span>
      </div>
    </template>
    <div
      class="nc-open-content"
      style="height: 100%"
    >
      <div class="open-content-up" style="text-align: center;padding: 5% 0;">
        <label>权限来源操作员：</label>
        <Select v-model:value="formItems.userId" style="width: 60%;"
                :options="operList.filter(it=>it.id!=formItems.targetUserId).map(it=>({value:it.id,label: it.realName}))"
                show-search :filter-option="filterOption"/>
        <span class="red_span">⋆</span><br/>
        <label>授权目标操作员：</label>
        <Select v-model:value="formItems.targetUserId" style="width: 60%;"
                :options="operList.filter(it=>it.id!=formItems.userId).map(it=>({value:it.id,label: it.realName}))"
                show-search :filter-option="filterOption"/>
        <span class="red_span">⋆</span>
      </div>
    </div>
  </BasicModal>
</template>
<script setup="props, {content}" lang="ts">
import {inject, onMounted, ref, unref} from 'vue';
import {BasicModal, useModalInner} from '/@/components/Modal'
import {useMessage} from "/@/hooks/web/useMessage";
import {hasBlank} from "/@/api/task-api/tast-bus-api";
import {Select, message} from 'ant-design-vue'
import {TeamOutlined} from '@ant-design/icons-vue';
import {copyAuthor} from "/@/api/caozuoyuan/caozuoyuan";

const SelectOption = Select.Option
const {createWarningModal} = useMessage()
const emit = defineEmits(['register'])

const formItems = ref({
  userId: '',
  targetUserId: '',
})
const operList = ref([])
const [register, {closeModal}] = useModalInner((data) => {
  operList.value = data.userList
  formItems.value.userId = ''
  formItems.value.targetUserId = ''
})
const filterOption = (input: string, option: any) => {
  return option.label.indexOf(input) >= 0;
};

async function handleOk() {
  if (hasBlank(formItems.value.userId) || hasBlank(formItems.value.targetUserId) || formItems.value.userId == formItems.value.targetUserId) {
    createWarningModal({title: '温馨提醒', content: "来源与目标操作员为必填项！"});
  } else {
    await copyAuthor({
      userUniqueCode: formItems.value.userId,
      defaultLogin: formItems.value.targetUserId
    }).then(res => {
      message.success('已成功完成复制操作！')
      closeModal()
    }).catch(() => {
      message.success('执行复制操作失败！')
    })
  }
}
</script>
<style src="../../../../../assets/styles/caozuoyuan_edit.less" lang="less" scoped></style>
<style scoped lang="less">
label:not(.ant-radio-button-wrapper) {
  width: 140px !important;
  font-weight: bold;
}

:deep(.ant-select-selector) {
  border: none !important;
  border-bottom: 1px solid #bdb9b9 !important;
}

.red_span {
  color: red;
  font-weight: bold;
  display: inline-block;
  width: 20px;
  text-align: right;

  > span {
    font-size: 14px;
  }
}
</style>

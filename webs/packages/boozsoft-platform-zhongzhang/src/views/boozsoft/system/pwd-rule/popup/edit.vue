<template>
  <BasicModal
    centered=true
    width="700px"
    v-bind="$attrs"
    title="编辑密码策略"
    @ok="handleOk()"
    @cancel="handleClose()"
    @register="register"
  >
    <template v-if="dataType=='add'" #title>
      <div style="display: flex;color: #0096c7;margin: 5px 10px 5px;">
        <span style="line-height:40px;font-size: 28px;">
          <PlusCircleOutlined style="font-size: 34px;font-weight: bold"/>
        </span>
        <span style="line-height:40px;font-size: 28px;margin-top:-5px;">
          &nbsp;&nbsp;密码策略
        </span>
      </div>
      <div style="display: inline-block;position:absolute;right: 40px;top: 10px;z-index: 100000;background:#ffffff">
        <img src="/009.png" style="height:65px;margin-right: 55px;"/>
      </div>
    </template>
    <template v-if="dataType=='edit'" #title>
      <div style="display: flex;color: #0096c7;margin: 5px 10px 5px;">
        <span style="line-height:40px;font-size: 28px;">
          <FormOutlined style="font-size: 34px;font-weight: bold"/>
        </span>
        <span style="line-height:40px;font-size: 28px;margin-top:-5px;">
          &nbsp;&nbsp;密码策略
        </span>
      </div>
      <div style="display: inline-block;position:absolute;right: 40px;top: 10px;z-index: 100000;background:#ffffff">
        <img src="/009.png" style="height:76px;margin-right: 55px;"/>
      </div>
    </template>
    <div class="nc-open-content" style="height: 100%;margin-top: 10px;">
      <div class="open-content-up">
        <br/><br/>
        <label style="font-size: 18px;margin-left: 70px;width:150px;color: #535353;font-weight: bold;">策略名称：</label>
          <a-input
            v-model:value="formItems.pwdName"
            class="abc"
            style="width: 35%;border-bottom: 2px solid #000000;"
            autocomplete="off"
          />
          <span class="red_span">*</span>
          <br/><br/>
            <label>密码最小长度：</label>
            <a-input-number
              v-model:value="formItems.pwdLength"
              autocomplete="off"
              min="1"
              style="width: 10%;
              border: none;
              border-bottom: 1px solid #bdb9b9;margin-left: 30px;"
            /><span style="font-weight: bold;">位</span>

          <label>强制大写字母：</label>
          <a-select
            v-model:value="formItems.pwdUpperCase"
            style="width: 10%;text-align: center;"
            allow-clear
          >
            <a-select-option value="0">否</a-select-option>
            <a-select-option value="1">是</a-select-option>
            <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
          </a-select>
          <br/>

            <label>强制特殊符号：</label>
            <a-select
              v-model:value="formItems.pwdTe"
              style="width: 10%;text-align: center;margin-left: 30px;"
              allow-clear
            >
              <a-select-option value="0">否</a-select-option>
              <a-select-option value="1">是</a-select-option>
              <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
            </a-select>

          <span style="margin-left: 14px;">
            <label>密码到期锁定：</label>
            <a-select
              v-model:value="formItems.pwdLock"
              show-search
              option-filter-prop="children"
              style="width: 10%;text-align: center;"
              allow-clear
            >
              <a-select-option value="0">否</a-select-option>
              <a-select-option value="1">是</a-select-option>
              <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
            </a-select>
          </span>
          <br/>
          <label>密码到期强制修改：</label>
          <a-select
            v-model:value="formItems.pwdUpdate"
            style="width: 10%;text-align: center;"
            allow-clear
          >
            <a-select-option value="0">否</a-select-option>
            <a-select-option value="1">是</a-select-option>
            <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
          </a-select>

          <span style="margin-left: 14px;">
            <label>密码有效天数：</label>
            <a-input-number
              v-model:value="formItems.pwdValidity"
              min="1"
              max="365"
              autocomplete="off"
              style="width: 10%;border: none;border-bottom: 1px solid #bdb9b9 ;"
            /><span style="font-weight: bold;">天</span>
          </span>
          <br/>
          <label>允许登录错误次数：</label>
          <a-input-number
            v-model:value="formItems.pwdErrorNumber"
            min="1"
            max="10"
            autocomplete="off"
            style="width: 10%;
            border: none;
            border-bottom: 1px solid #bdb9b9; "
          /><span style="font-weight: bold;">次</span>
        <label>锁定密码时长：</label>
        <a-select
          v-model:value="formItems.pwdErrorTime"
          style="width: 13%;text-align: center;"
          allow-clear
        >
          <a-select-option value="15">15分钟</a-select-option>
          <a-select-option value="60">60分钟</a-select-option>
          <a-select-option value="24">24小时</a-select-option>
          <a-select-option value="all">永久</a-select-option>
          <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
        </a-select>
      </div>
    </div>
    <template #footer>
      <a-button @click="handleClose()">取消</a-button>&nbsp;
      <span v-if="dataType=='add'">
        <a-button @click="handleOk('close')" :disabled="saveClick">保存</a-button>&nbsp;
        <a-button @click="handleOk('add')"  :disabled="saveClick" type="primary">保存并新增</a-button>
      </span>
      <span v-else>
        <a-button @click="handleOk('close')" :disabled="saveClick" type="primary">保存</a-button>
      </span>
    </template>
  </BasicModal>
</template>

<script setup="props, { content }" lang="ts">
import { CaretDownOutlined,PlusCircleOutlined,FormOutlined,FileSearchOutlined } from '@ant-design/icons-vue'
import {ref, unref, onMounted, reactive, toRaw} from 'vue';
import { BasicModal, useModalInner } from '/@/components/Modal';
import {
  InputNumber as AInputNumber,
  DatePicker as ADatePicker,
  Select as ASelect,
  Input as AInput,
  Radio as ARadio,
  Tabs as ATabs,
  message,
} from 'ant-design-vue';
import { findByPwdName } from '/@/api/record/pwd-rule/data';
import {useMessage} from "/@/hooks/web/useMessage";

const ARangePicker = ADatePicker.RangePicker;
const ASelectOption = ASelect.Option;
const AInputSearch = AInput.Search;
const ARadioGroup = ARadio.Group;
const ARadioButton = ARadio.Button;
const ATabPane = ATabs.TabPane;
const saveClick: any = ref(false);
const emit=defineEmits([]);

const formItems: any = ref({
  id:null,
  pwdName:'',
  pwdLength:'1',
  pwdUpperCase:'0',
  pwdTe:'0',
  pwdAddTime:'',
  pwdValidity:'365',
  pwdExpireTime:'',
  pwdUpdate:'0',
  pwdLock:'0',
  pwdType:'',
  pwdErrorNumber:'5',
  pwdErrorTime:'',
});
const oldformItems: any = ref({
  id:null,
  pwdName:'',
  pwdLength:'1',
  pwdUpperCase:'0',
  pwdTe:'0',
  pwdAddTime:'',
  pwdValidity:'365',
  pwdExpireTime:'',
  pwdUpdate:'0',
  pwdLock:'0',
  pwdType:'',
  pwdErrorNumber:'5',
  pwdErrorTime:'',
});
const dataType=ref('')
const [register, { closeModal, setModalProps }] = useModalInner((data) => {
  dataType.value=data.type
  if(data.data!==''){
    formItems.value.id=data.data.id
    formItems.value.pwdName=data.data.pwdName
    formItems.value.pwdLength=data.data.pwdLength
    formItems.value.pwdUpperCase=data.data.pwdUpperCase
    formItems.value.pwdTe=data.data.pwdTe
    formItems.value.pwdAddTime=data.data.pwdAddTime
    formItems.value.pwdValidity=data.data.pwdValidity
    formItems.value.pwdExpireTime=data.data.pwdExpireTime
    formItems.value.pwdUpdate=data.data.pwdUpdate
    formItems.value.pwdLock=data.data.pwdLock
    formItems.value.pwdType=data.data.pwdType
    formItems.value.pwdErrorNumber=data.data.pwdErrorNumber
    oldformItems.value.pwdName=data.data.pwdName
  }else{
    formItems.value={
      id:null,
      pwdName:'',
      pwdLength:'1',
      pwdUpperCase:'0',
      pwdTe:'0',
      pwdAddTime:'',
      pwdValidity:'365',
      pwdExpireTime:'',
      pwdUpdate:'0',
      pwdLock:'0',
      pwdType:'',
      pwdErrorNumber:'5',
    }
  }
});

const handleOk = async (flag) => {
  if(formItems.value.pwdName===''){
    return message.error('请填写名称');
  }else if(dataType.value=='add'){
    let a=await findByPwdName(formItems.value.pwdName)
    if(a>0){
      return message.error('名称已存在，请重新输入！');
    }
  }else if(dataType.value=='edit' && oldformItems.value.pwdName!==formItems.value.pwdName){
    let a=await findByPwdName(formItems.value.pwdName)
    if(a>0){
      return message.error('名称已存在，请重新输入！');
    }
  }
  if(formItems.value.pwdLength===''){
    return message.error('密码长度必须大于0');
  }
  if(formItems.value.pwdValidity===''){
    return message.error('请填写有效天数');
  }
  if(formItems.value.pwdErrorNumber===''){
    return message.error('请填写允许错误次数');
  }
  formItems.value.flag=flag
  emit('save', toRaw(formItems.value));
  closeModal();
}
const handleClose = () => {
  emit('handleClose');
  closeModal();
}
</script>
<style>
.ant-modal-body {
  padding: 0px;
  border-bottom: 1px solid #0096c7;
  border-left: none;
  border-right: none;
  border-top: none !important;
}
.ant-modal-header{
  border: none !important;
}
</style>
<style lang="less" scoped>
:deep(.ant-input-number-input){
  text-align: center;
  font-weight: 600;
}
:deep(.ant-calendar-picker-input.ant-input), :deep(.ant-select-single:not(.ant-select-customize-input) .ant-select-selector .ant-select-selection-search-input) {
  border: none;
  border-bottom: solid 1px rgb(191, 191, 191) !important;
  width: 100%;
  font-weight: bold;
}

:deep(.ant-select-single:not(.ant-select-customize-input)
.ant-select-selector
.ant-select-selection-search-input) {
  border: none !important;
}

.vben-basic-title {
  color: rgb(1, 129, 226) !important;
}

.ant-modal-body {
  padding: 0px;
  border: 1px solid rgb(1, 129, 226);
  border-left: none;
  border-right: none;
}

.red_span {
  color: red;
  font-weight: bold;
  display: inline-block;
  width: 20px;
  text-align: right;
}

.nc-open-content {

  input {
    width: 30%;
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
    font-weight: bold;
    font-size: 14px;
  }

  .abc{
    border-bottom: 2px solid #666666 !important;
    font-size: 18px;
  }

  .ant-input:focus {
    box-shadow: none;
  }

  :deep(.ant-select-selector) {
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
    font-weight: bold;
    font-size: 14px;
  }

  label {
    text-align: left;
    //width: 120px;
    display: inline-block;
    font-size: 14px;
    margin-left: 3em;
    color: #404040;
    padding: 0px 30px;
  }

  .open-content-down {
    margin-top: 5%;

    .ant-tabs-tab-active::before {
      position: absolute;
      top: 0px;
      left: 0;
      width: 100%;
      border-top: 2px solid transparent;
      border-radius: 2px 2px 0 0;
      transition: all 0.3s;
      content: '';
      pointer-events: none;
      background-color: rgb(1, 143, 251);
    }
  }
}

:deep(.ant-tabs.ant-tabs-card .ant-tabs-card-bar .ant-tabs-tab) {
  height: 40px;
  margin: 0;
  margin-right: 2px;
  padding: 0 16px;
  line-height: 38px;
  background: #fafafa;
  border: 1px solid #f0f0f0;
  border-radius: 2px 2px 0 0;
  transition: all 0.3s cubic-bezier(0.645, 0.045, 0.355, 1);
  font-weight: bold;
  font-size: 13px;
}

</style>

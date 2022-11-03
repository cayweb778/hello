<template>
  <BasicModal
    width="400px"
    v-bind="$attrs"
    title="操作员"
    @ok="handleOk()"
    @register="register"
  >
    <div
      class="nc-open-content"
      style="height: 100%"
    >
      <div class="open-content-up">
        <label style="    margin-left: -89px;
    width: 201px;">
          <Button  :class="(!isPhoneBtn?'clicked ':'')+' accountItem'" @click="isPhoneBtn=false">账号</Button>
          <Button :class="(isPhoneBtn?'clicked ':'')+'accountItem'" @click="isPhoneBtn=true">手机号</Button>
        </label>

        <a-input v-model:value="formItems.phone" placeholder="任意输入账号，用于登陆" maxlength="11" minlength="11" />
        <br><br>
        <label>密 码</label>
        <a-input v-model:value="formItems.password" placeholder="请输入密码" maxlength="6" minlength="9" />
        <br><br>
        <label>姓 名</label>
        <a-input v-model:value="formItems.userName" placeholder="请输入姓名" />
        <br><br>
        <a-checkbox-group style="display: inline-block" v-model:value="formItems.userType">
          <label>类 型</label>
          <a-checkbox style="width:123px" value="3">公司操作员</a-checkbox>
          <a-checkbox style="width:123px" value="2">组织操作员</a-checkbox>
          <a-checkbox style="margin-left:110px;width:123px" value="1">集团操作员</a-checkbox>
          <a-checkbox style="width:123px" value="1000">超级管理员</a-checkbox>
        </a-checkbox-group>

<!--        <a-select v-model:value="formItems.userType" placeholder="请选择操作员类型" style="width: 52%;">-->
<!--          <a-select-option value="1">系统操作员</a-select-option>-->
<!--          <a-select-option value="2">组织操作员</a-select-option>-->
<!--          <a-select-option value="3">公司操作员</a-select-option>-->
<!--        </a-select>-->
        <br><br>

        <label>邮 箱</label>
        <a-input v-model:value="formItems.email" placeholder="请输入邮箱" maxlength="20" minlength="10" />
      </div>
    </div>
  </BasicModal>
</template>
<script setup="props, {content}" lang="ts">
import { defineComponent, reactive, ref, toRefs, unref } from 'vue'
import { BasicModal, useModalInner } from '/@/components/Modal'
import { BasicForm, useForm } from '/@/components/Form/index'
import {Col as ACol,Popover as APopover,Input as AInput,Checkbox as ACheckbox, Row as ARow,Card as ACard,List as AList,Button} from 'ant-design-vue'
const ACheckboxGroup=ACheckbox.Group
const emit=defineEmits([])

const data = ref({})
const formItems = ref({
  id: '',
  uniqueCode: '',
  account: '',
  password: '123456',
  userName: '',
  phone: '',
  email: '',
  flag: '',
  userType:''
})
const [register, { closeModal }] = useModalInner((data) => {
  // 方式2
  formItems.value = {
    id: data.data.id,
    uniqueCode: data.data.uniqueCode,
    account: data.data.account,
    password: data.data.password,
    userName: data.data.userName,
    phone: data.data.phone,
    email: data.data.email,
    flag: data.data.flag,
    userType: data.data.userType
  }
})

async function handleOk() {
  emit('save', unref(formItems))
  closeModal()
}
function validateMail(mail) {//验证邮件规则
  return /^\w{3,15}\@\w+\.[a-z]{2,3}$/.test(mail);//前缀可以是字母或者数字，在3位以上15位以下，后缀是2位或者3位   \w:表示字母数字或者下划线
}
function handleCancel() {
}
function createUser(){
  createUser({
    name:'',
    nick:''
  })
}
const activeKey = ref('2')
const isPhoneBtn=ref(false)
</script>
<style src="../../../../../assets/styles/caozuoyuan_edit.less" lang="less" scoped></style><style lang='less' scoped ></style>
<style scoped>
.accountItem{
  padding:3px 5px
}
.clicked{
background:gray;color:white;

}
</style>

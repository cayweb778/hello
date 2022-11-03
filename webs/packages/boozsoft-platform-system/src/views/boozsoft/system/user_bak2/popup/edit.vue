<template>
  <BasicModal
    width="400px"
    v-bind="$attrs"
    title="财税达用户"
    @ok="handleOk()"
    @register="register"
  >
    <div
      class="nc-open-content"
      style="height: 100%"
    >
      <div class="open-content-up">
        <div>
          <label>账 号</label>
          {{ formItems.userLoginNum }}
          <br><br>
        </div>
        <label>手机号</label>
        <a-input v-model:value="formItems.phone" placeholder="请输入手机号,可用于登陆" :maxlength="11" :minlength="11"/>
        <br><br>

        <label>密 码</label>
        <a-input v-model:value="formItems.password" placeholder="请输入密码" :maxlength="6" :minlength="9"/>
        <br><br>
        <label>姓 名</label>
        <a-input v-model:value="formItems.userName" placeholder="请输入姓名"/>
        <br><br>
        <a-checkbox-group style="display: inline-block" v-model:value="formItems.userType">
          <label>类 型</label>
          <a-checkbox style="width:123px" value="100">超级管理员</a-checkbox>
          <a-checkbox style="width:123px;margin-left:-10px" value="200">普通管理员</a-checkbox>
          <a-checkbox style="margin-left:81px;width:123px" value="300">操作员</a-checkbox>
          <a-checkbox style="margin-left:-23px;width:123px" value="400">用户</a-checkbox>
        </a-checkbox-group>

        <!--        <a-select v-model:value="formItems.userType" placeholder="请选择操作员类型" style="width: 52%;">-->
        <!--          <a-select-option value="1">系统操作员</a-select-option>-->
        <!--          <a-select-option value="2">组织操作员</a-select-option>-->
        <!--          <a-select-option value="3">公司操作员</a-select-option>-->
        <!--        </a-select>-->
        <br><br>
        <label>邮 箱</label>
        <a-input v-model:value="formItems.email" placeholder="请输入邮箱" :maxlength="20" :minlength="10"/>
      </div>
    </div>
  </BasicModal>
</template>
<script setup="props, {content}" lang="ts">
import { makeNum} from '/@/api/sys/user';
import {defineComponent, onMounted, reactive, ref, toRefs, unref} from 'vue';
import { BasicModal, useModalInner } from '/@/components/Modal'
import { BasicForm, useForm } from '/@/components/Form/index'
import {Col as ACol,Popover as APopover,Input as AInput,Checkbox as ACheckbox, Row as ARow,Card as ACard,List as AList} from 'ant-design-vue'
const ACheckboxGroup=ACheckbox.Group
const emit=defineEmits([])

const data = ref({})


async function createBoozUser(user) {
  const userLoginNum = await makeNum();
  const formItems = {
    id: '',
    userLoginNum: 0,
    userName: '',
    nickName: '',
    password: '123456',
    phone: '',
    email: '',
    flag: '',
    userType: ['400']
  };
  formItems.userLoginNum = userLoginNum;
  formItems.userName = '用户' + userLoginNum;
  return formItems;
}

const formItems = ref({});


onMounted(async () => {
    formItems.value = await createBoozUser(null);
});
const [register, {closeModal}] = useModalInner((data) => {

});

async function handleOk() {
  function check(formItems) {
    if (formItems.userType == null) {
      alert('类型不能为空');
      return false;
    }
  }

  if (!check(formItems)) {
    return;
  }
  emit('save', unref(formItems));
  closeModal();
}

function validateMail(mail) {//验证邮件规则
  return /^\w{3,15}\@\w+\.[a-z]{2,3}$/.test(mail);//前缀可以是字母或者数字，在3位以上15位以下，后缀是2位或者3位   \w:表示字母数字或者下划线
}

function handleCancel() {
}

function createUser() {
  createUser({
    name: '',
    nick: ''
  });
}

const activeKey = ref('2');

</script>
<style src="../../../../../assets/styles/caozuoyuan_edit.less" lang="less" scoped></style>
<style lang='less' scoped></style>

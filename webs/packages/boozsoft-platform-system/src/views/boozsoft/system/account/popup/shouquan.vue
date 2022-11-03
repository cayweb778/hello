<template>
  <BasicModal
    width="500px"
    v-bind="$attrs"
    title="集团信息"
    @ok="handleOk()"
    @register="register"
  >
    <div
      class="nc-open-content"
      style="height: 100%"
    >
      <div class="open-content-up">

        <label>选择用户</label>
        <a-select v-model:value="formItems.userId" @change="changeUser()" placeholder="" style="width: 35%;">
          <a-select-option v-for="user in userList" :key="user.username" :value="user.id">
            {{ user.username }}
          </a-select-option>
        </a-select>
        <br/><br/>
      </div>

      <a-transfer
        :titles="['未授权', '已授权']"
        :data-source="mockData"
        show-search
        :filter-option="filterOption"
        :target-keys="targetKeys"
        :render="item => item.title"
        @change="handleChange"
        @search="handleSearch"
      />

    </div>

  </BasicModal>
</template>

<script setup="props, {content}" lang="ts">
import { ref, unref } from 'vue'
import { BasicModal, useModalInner } from '/@/components/Modal'
import {findByUserAccount} from "/@/api/record/system/account";
import {findUserByRoleId} from "/@/api/sys/user";
import { Select as ASelect, Input as AInput, Transfer as ATransfer } from 'ant-design-vue';

const emit=defineEmits([])

const formItems:any = ref({})

const userList:any = ref([])

const [register, { closeModal }] = useModalInner((data) => {
  // 方式2
  formItems.value.userId = data.data.userId
  formItems.value.accountList = targetKeys.value

  findUserByRoleId().then((res:any) => {
    userList.value = res
    formItems.value.userId = res[0].id
    getMock(formItems.value.userId)
  })

})

async function handleOk() {
  console.log(formItems.value)
  // console.log(targetKeys.value)
  // console.log(mockData.value)
  formItems.value.accountList = targetKeys.value
  emit('shouquan', unref(formItems))
  closeModal()
}

function changeUser(){
  getMock(formItems.value.userId)
}

//选中key值
const targetKeys:any = ref([]);
//所有集合
const mockData:any = ref([]);

async function getMock(userId:any) {
  targetKeys.value = [];
  mockData.value = [];
  console.log(formItems.value.userId)
  findByUserAccount(userId).then(res => {
    console.log(res)
    res.forEach(function (item:any){
      const data = {
        key: item.id,
        title: item.accId,
        description: item.accName
      }
      if (item.beiyong1!=null && item.beiyong1!="" ) {
        targetKeys.value.push(data.key);
      }
      mockData.value.push(data);
    })
  })
  // for (let i = 0; i < 20; i++) {
  //   const data = {
  //     key: i.toString(),
  //     title: 'content'+i,
  //     description: 'description of content'+i,
  //     chosen: Math.random() * 2 > 1,
  //   };
  //   if (data.chosen) {
  //     targetKeys.value.push(data.key);
  //   }
  //   mockData.value.push(data);
  // }
}
function filterOption(inputValue:any, option:any) {
  return option.description.indexOf(inputValue) > -1;
}
function handleChange(targetKey:any, direction:any, moveKeys:any) {
  console.log(targetKey, direction, moveKeys);
  targetKeys.value = targetKey
  // this.targetKeys = targetKeys;
}
function handleSearch(dir:any, value:any) {
  console.log('search:', dir, value);
}
</script>
<style lang="less" scoped>
:deep(.ant-transfer-list) {
  height: 400px;
}

:deep(.ant-calendar-picker-input.ant-input),:deep(.ant-select-single:not(.ant-select-customize-input) .ant-select-selector .ant-select-selection-search-input){
  border: none;
  border-bottom: solid 1px rgb(191, 191, 191) !important;
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

.nc-open-content {
  input {
    width: 35%;
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
  }

  .ant-input:focus {
    box-shadow: none;
  }

 :deep(.ant-select-selector) {
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
  }

  label {
    text-align: right;
    width: 110px;
    display: inline-block;
    padding: 5px 10px;
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
</style>

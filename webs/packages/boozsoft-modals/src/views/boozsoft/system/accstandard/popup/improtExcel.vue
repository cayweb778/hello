<template>
  <BasicModal
    width="600px"
    v-bind="$attrs"
    title="国家(地区)会计制度"
    @ok="handleOk()"
    @register="register"
  >
    <a-spin :spinning="uploading">
      <br>
      <br>
      <div style="margin-left: 10%;">
        <label>模板名称： </label>
        <a-input style="width: 70%;margin-left: 27px;" v-model:value="formState.standardName" placeholder="模板名称" @blur="findByName"/>
        <br>
        <br>
        <label>选择科目类型： </label>
        <a-select
          v-model:value="formState.codeTyle"
          placeholder="科目类型"
          style="width: 70%;text-align: center;"
          allow-clear
        >
          <a-select-option :value="item.value+'-'+item.yusuan " :key="item.value" v-for="(item, i) in codeTyleList">{{
              item.title
            }}</a-select-option>
        </a-select>
      </div>
    </a-spin>
    <template #footer>
      <div style="height: 35px">
        <div style="float: right">
          <a-button @click="handleClose" :disabled="uploading">放弃</a-button>
          <a-button @click="handleOk(true)" type="primary" :loading="uploading">确定
          </a-button>
        </div>
      </div>
    </template>
  </BasicModal>
</template>
<script setup="props, { content }" lang="ts">
  import { ref,reactive,unref} from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { message } from 'ant-design-vue';
  import {
    Input as AInput,
    Spin as ASpin,
    Select as ASelect,
  } from 'ant-design-vue';
  import {findByAccStyleStr, findByStandardName} from "/@/api/accstandard/accstandard";

  const ASelectOption = ASelect.Option;
  const AInputSearch = AInput.Search;

  const standardName = ref('')
  const codeTyle = ref('')
  const codeTyleList = ref([]);
  const fileList = ref([]);
  const uploading = ref(false)
  const emit=defineEmits([]);
  const excelcolumn=ref('')
  const formState = reactive({
    standardName: '',
    codeTyle: '',
  })

  const [register, { closeModal }] = useModalInner(async (data) => {
    codeTyleList.value= await findByAccStyleStr()
  });

  const handleClose = () => {
    formState.standardName=''
    formState.codeTyle=''
    closeModal()
  }

  const handleOk = async () => {
    if(formState.standardName===''){
      message.error('请填写模板名称!')
      return false;
    }
    if(formState.codeTyle===''){
      message.error('请选择科目类型!')
      return false;
    }
    if(await findByName()){
      uploading.value=true
      emit('save',unref(formState));
      handleClose();
      uploading.value=false
    }
  };

  async function findByName() {
    if(formState.standardName!=''){
      let a=await findByStandardName(formState.standardName)
      if(a>0){
        message.error('模板名称已存在!')
        return false;
      }
      return true;
    }
  }
</script>
<style>
.vben-basic-title {
  color: rgb(1, 129, 226) !important;
}

.ant-modal-body {
  padding: 0px;
  border: 1px solid rgb(1, 129, 226);
  border-left: none;
  border-right: none;
}
</style>
<style lang="less" scoped>
.vben-basic-title {
  color: rgb(1, 129, 226) !important;
}

.ant-modal-body {
  padding: 0px;
  border: 1px solid rgb(1, 129, 226);
  border-left: none;
  border-right: none;
}

:deep(.ant-select-selector) {
  border: none !important;
  border-bottom: 1px solid #bdb9b9 !important;
}

input {
  width: 35%;
  border: none !important;
  border-bottom: 1px solid #bdb9b9 !important;
}
</style>

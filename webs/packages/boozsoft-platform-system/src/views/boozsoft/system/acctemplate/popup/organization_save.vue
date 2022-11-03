<template>
  <BasicModal
    width="900px"
    v-bind="$attrs"
    title="新建组织会计科目制度"
    @ok="handleOk()"
    @cancel="handleCancel()"
    @register="register"
  >
    <a-form ref="formRef" :model="formState" :rules="rules">
      <p>
        <label>组织</label>
        <a-select
          v-model:value="zz"
          option-filter-prop="children"
          style="width: 35%;margin-left: 20%;"
          show-search
        >
          <a-select-option
            v-for="(item,i) in zzlist"
            :key="item.id"
            :value="item.id"
          >
            {{ item.orgName }}
          </a-select-option>
        </a-select>
      </p>
      <p>
        <label>所属集团会计科目制度</label>
        <a-select
          v-model:value="standardSelected"
          option-filter-prop="children"
          style="width: 35%;margin-left: 7%;"
          show-search
          @change="standardCheck"
        >
          <a-select-option
            v-for="item in accStandardList"
            :key="item.id"
            :value="item.id"
          >
            {{ item.tname }}
          </a-select-option>
        </a-select>
      </p>

      <p>
      <label>模板名称</label>
        <a-input class="input" v-model:value.trim="formState.tName" autocomplete="off" style="margin-left: 16.5%;width:310px;"/>
      </p>
      <p>
        <label>科目级次</label>
        <a-select v-model:value="formState.tJici1" :disabled="disabledflg.tJici1" style="width: 50px; font-size: 12px">
          <a-select-option value="4">{{ codeFirst }}</a-select-option>
        </a-select>
        <a-select v-model:value="formState.tJici2" :disabled="disabledflg.tJici2" style="width: 50px; font-size: 12px">
          <a-select-option value="" />
          <a-select-option style="font-size: 12px" value="1">1</a-select-option>
          <a-select-option style="font-size: 12px" value="2">2</a-select-option>
          <a-select-option style="font-size: 12px" value="3">3</a-select-option>
          <a-select-option style="font-size: 12px" value="4">4</a-select-option>
          <a-select-option style="font-size: 12px" value="5">5</a-select-option>
          <a-select-option style="font-size: 12px" value="6">6</a-select-option>
          <a-select-option style="font-size: 12px" value="7">7</a-select-option>
          <a-select-option style="font-size: 12px" value="8">8</a-select-option>
          <a-select-option style="font-size: 12px" value="9">0</a-select-option>
        </a-select>
        <a-select v-model:value="formState.tJici3" :disabled="disabledflg.tJici3" style="width: 50px; font-size: 12px">
          <a-select-option value="" />
          <a-select-option style="font-size: 12px" value="1">1</a-select-option>
          <a-select-option style="font-size: 12px" value="2">2</a-select-option>
          <a-select-option style="font-size: 12px" value="3">3</a-select-option>
          <a-select-option style="font-size: 12px" value="4">4</a-select-option>
          <a-select-option style="font-size: 12px" value="5">5</a-select-option>
          <a-select-option style="font-size: 12px" value="6">6</a-select-option>
          <a-select-option style="font-size: 12px" value="7">7</a-select-option>
          <a-select-option style="font-size: 12px" value="8">8</a-select-option>
          <a-select-option style="font-size: 12px" value="9">0</a-select-option>
        </a-select>
        <a-select v-model:value="formState.tJici4" :disabled="disabledflg.tJici4" style="width: 50px; font-size: 12px">
          <a-select-option value="" />
          <a-select-option style="font-size: 12px" value="1">1</a-select-option>
          <a-select-option style="font-size: 12px" value="2">2</a-select-option>
          <a-select-option style="font-size: 12px" value="3">3</a-select-option>
          <a-select-option style="font-size: 12px" value="4">4</a-select-option>
          <a-select-option style="font-size: 12px" value="5">5</a-select-option>
          <a-select-option style="font-size: 12px" value="6">6</a-select-option>
          <a-select-option style="font-size: 12px" value="7">7</a-select-option>
          <a-select-option style="font-size: 12px" value="8">8</a-select-option>
          <a-select-option style="font-size: 12px" value="9">0</a-select-option>
        </a-select>
        <a-select v-model:value="formState.tJici5" :disabled="disabledflg.tJici5" style="width: 50px; font-size: 12px">
          <a-select-option value="" />
          <a-select-option style="font-size: 12px" value="1">1</a-select-option>
          <a-select-option style="font-size: 12px" value="2">2</a-select-option>
          <a-select-option style="font-size: 12px" value="3">3</a-select-option>
          <a-select-option style="font-size: 12px" value="4">4</a-select-option>
          <a-select-option style="font-size: 12px" value="5">5</a-select-option>
          <a-select-option style="font-size: 12px" value="6">6</a-select-option>
          <a-select-option style="font-size: 12px" value="7">7</a-select-option>
          <a-select-option style="font-size: 12px" value="8">8</a-select-option>
          <a-select-option style="font-size: 12px" value="9">0</a-select-option>
        </a-select>
        <a-select v-model:value="formState.tJici6" :disabled="disabledflg.tJici6" style="width: 50px; font-size: 12px">
          <a-select-option value="" />
          <a-select-option style="font-size: 12px" value="1">1</a-select-option>
          <a-select-option style="font-size: 12px" value="2">2</a-select-option>
          <a-select-option style="font-size: 12px" value="3">3</a-select-option>
          <a-select-option style="font-size: 12px" value="4">4</a-select-option>
          <a-select-option style="font-size: 12px" value="5">5</a-select-option>
          <a-select-option style="font-size: 12px" value="6">6</a-select-option>
          <a-select-option style="font-size: 12px" value="7">7</a-select-option>
          <a-select-option style="font-size: 12px" value="8">8</a-select-option>
          <a-select-option style="font-size: 12px" value="9">0</a-select-option>
        </a-select>
        <a-select v-model:value="formState.tJici7" :disabled="disabledflg.tJici7" style="width: 50px; font-size: 12px">
          <a-select-option value="" />
          <a-select-option style="font-size: 12px" value="1">1</a-select-option>
          <a-select-option style="font-size: 12px" value="2">2</a-select-option>
          <a-select-option style="font-size: 12px" value="3">3</a-select-option>
          <a-select-option style="font-size: 12px" value="4">4</a-select-option>
          <a-select-option style="font-size: 12px" value="5">5</a-select-option>
          <a-select-option style="font-size: 12px" value="6">6</a-select-option>
          <a-select-option style="font-size: 12px" value="7">7</a-select-option>
          <a-select-option style="font-size: 12px" value="8">8</a-select-option>
          <a-select-option style="font-size: 12px" value="9">0</a-select-option>
        </a-select>
        <a-select v-model:value="formState.tJici8" :disabled="disabledflg.tJici8" style="width: 50px; font-size: 12px">
          <a-select-option value="" />
          <a-select-option style="font-size: 12px" value="1">1</a-select-option>
          <a-select-option style="font-size: 12px" value="2">2</a-select-option>
          <a-select-option style="font-size: 12px" value="3">3</a-select-option>
          <a-select-option style="font-size: 12px" value="4">4</a-select-option>
          <a-select-option style="font-size: 12px" value="5">5</a-select-option>
          <a-select-option style="font-size: 12px" value="6">6</a-select-option>
          <a-select-option style="font-size: 12px" value="7">7</a-select-option>
          <a-select-option style="font-size: 12px" value="8">8</a-select-option>
          <a-select-option style="font-size: 12px" value="9">0</a-select-option>
        </a-select>
        <a-select v-model:value="formState.tJici9" :disabled="disabledflg.tJici9" style="width: 50px; font-size: 12px">
          <a-select-option value="" />
          <a-select-option style="font-size: 12px" value="1">1</a-select-option>
          <a-select-option style="font-size: 12px" value="2">2</a-select-option>
          <a-select-option style="font-size: 12px" value="3">3</a-select-option>
          <a-select-option style="font-size: 12px" value="4">4</a-select-option>
          <a-select-option style="font-size: 12px" value="5">5</a-select-option>
          <a-select-option style="font-size: 12px" value="6">6</a-select-option>
          <a-select-option style="font-size: 12px" value="7">7</a-select-option>
          <a-select-option style="font-size: 12px" value="8">8</a-select-option>
          <a-select-option style="font-size: 12px" value="9">0</a-select-option>
        </a-select>
        <a-select v-model:value="formState.tJici10" :disabled="disabledflg.tJici10" style="width: 50px; font-size: 12px">
          <a-select-option value="" />
          <a-select-option style="font-size: 12px" value="1">1</a-select-option>
          <a-select-option style="font-size: 12px" value="2">2</a-select-option>
          <a-select-option style="font-size: 12px" value="3">3</a-select-option>
          <a-select-option style="font-size: 12px" value="4">4</a-select-option>
          <a-select-option style="font-size: 12px" value="5">5</a-select-option>
          <a-select-option style="font-size: 12px" value="6">6</a-select-option>
          <a-select-option style="font-size: 12px" value="7">7</a-select-option>
          <a-select-option style="font-size: 12px" value="8">8</a-select-option>
          <a-select-option style="font-size: 12px" value="9">0</a-select-option>
        </a-select>
        <a-select v-model:value="formState.tJici11" :disabled="disabledflg.tJici11" style="width: 50px; font-size: 12px">
          <a-select-option value="" />
          <a-select-option style="font-size: 12px" value="1">1</a-select-option>
          <a-select-option style="font-size: 12px" value="2">2</a-select-option>
          <a-select-option style="font-size: 12px" value="3">3</a-select-option>
          <a-select-option style="font-size: 12px" value="4">4</a-select-option>
          <a-select-option style="font-size: 12px" value="5">5</a-select-option>
          <a-select-option style="font-size: 12px" value="6">6</a-select-option>
          <a-select-option style="font-size: 12px" value="7">7</a-select-option>
          <a-select-option style="font-size: 12px" value="8">8</a-select-option>
          <a-select-option style="font-size: 12px" value="9">0</a-select-option>
        </a-select>
        <a-select v-model:value="formState.tJici12" :disabled="disabledflg.tJici12" style="width: 50px; font-size: 12px">
          <a-select-option value="" />
          <a-select-option style="font-size: 12px" value="1">1</a-select-option>
          <a-select-option style="font-size: 12px" value="2">2</a-select-option>
          <a-select-option style="font-size: 12px" value="3">3</a-select-option>
          <a-select-option style="font-size: 12px" value="4">4</a-select-option>
          <a-select-option style="font-size: 12px" value="5">5</a-select-option>
          <a-select-option style="font-size: 12px" value="6">6</a-select-option>
          <a-select-option style="font-size: 12px" value="7">7</a-select-option>
          <a-select-option style="font-size: 12px" value="8">8</a-select-option>
          <a-select-option style="font-size: 12px" value="9">0</a-select-option>
        </a-select>
        <a-select v-model:value="formState.tJici13" :disabled="disabledflg.tJici13" style="width: 50px; font-size: 12px">
          <a-select-option value="" />
          <a-select-option style="font-size: 12px" value="1">1</a-select-option>
          <a-select-option style="font-size: 12px" value="2">2</a-select-option>
          <a-select-option style="font-size: 12px" value="3">3</a-select-option>
          <a-select-option style="font-size: 12px" value="4">4</a-select-option>
          <a-select-option style="font-size: 12px" value="5">5</a-select-option>
          <a-select-option style="font-size: 12px" value="6">6</a-select-option>
          <a-select-option style="font-size: 12px" value="7">7</a-select-option>
          <a-select-option style="font-size: 12px" value="8">8</a-select-option>
          <a-select-option style="font-size: 12px" value="9">0</a-select-option>
        </a-select>
        <a-select v-model:value="formState.tJici14" :disabled="disabledflg.tJici14" style="width: 50px; font-size: 12px">
          <a-select-option value="" />
          <a-select-option style="font-size: 12px" value="1">1</a-select-option>
          <a-select-option style="font-size: 12px" value="2">2</a-select-option>
          <a-select-option style="font-size: 12px" value="3">3</a-select-option>
          <a-select-option style="font-size: 12px" value="4">4</a-select-option>
          <a-select-option style="font-size: 12px" value="5">5</a-select-option>
          <a-select-option style="font-size: 12px" value="6">6</a-select-option>
          <a-select-option style="font-size: 12px" value="7">7</a-select-option>
          <a-select-option style="font-size: 12px" value="8">8</a-select-option>
          <a-select-option style="font-size: 12px" value="9">0</a-select-option>
        </a-select>
        <a-select v-model:value="formState.tJici15" :disabled="disabledflg.tJici15" style="width: 50px; font-size: 12px">
          <a-select-option value="" />
          <a-select-option style="font-size: 12px" value="1">1</a-select-option>
          <a-select-option style="font-size: 12px" value="2">2</a-select-option>
          <a-select-option style="font-size: 12px" value="3">3</a-select-option>
          <a-select-option style="font-size: 12px" value="4">4</a-select-option>
          <a-select-option style="font-size: 12px" value="5">5</a-select-option>
          <a-select-option style="font-size: 12px" value="6">6</a-select-option>
          <a-select-option style="font-size: 12px" value="7">7</a-select-option>
          <a-select-option style="font-size: 12px" value="8">8</a-select-option>
          <a-select-option style="font-size: 12px" value="9">0</a-select-option>
        </a-select>
      </p>
    </a-form>
  </BasicModal>
</template>
<script setup="props, { content }" lang="ts">
  import { message } from 'ant-design-vue';
  import { onMounted, reactive, ref, toRaw, unref } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import {
    countByName,
    countByUniqueAccStandardAndTName,
    findByCustomTemplate
  } from '/@/api/acctemplate/acctemplate';
  import {
    Switch as ASwitch,
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
  import {getOrganizeList} from "/@/api/record/group/im-organize";
  const ASelectOption = ASelect.Option;
  const AInputSearch = AInput.Search;
  const AStatisticCountdown = AStatistic.Countdown;
  const ATabPane = ATabs.TabPane;
  const AMenuItem = AMenu.Item;
  const AListItem = AList.Item;
  const emit=defineEmits([]);
  const standardSelected = ref('');

  const zz = ref(''); // 组织
  const zzlist = ref([]); // 组织
  const codeFirst = ref('');
  const accStandardList = ref([]);
  const formRef = ref({});
  // 数据校验
  const rules = {
    tName: [
      {
        required: true,
        message: '必填项不能为空!',
      },
    ],
  };

  const formState = reactive({
    tName: '',
    tJici1: '',
    tJici2: '',
    tJici3: '',
    tJici4: '',
    tJici5: '',
    tJici6: '',
    tJici7: '',
    tJici8: '',
    tJici9: '',
    tJici10: '',
    tJici11: '',
    tJici12: '',
    tJici13: '',
    tJici14: '',
    tJici15: '',
    tpid: '',
    tOrganization: '',
    uniqueAccStandard: 0,
  });
  const disabledflg = reactive({
    tJici1: false,
    tJici2: false,
    tJici3: false,
    tJici4: false,
    tJici5: false,
    tJici6: false,
    tJici7: false,
    tJici8: false,
    tJici9: false,
    tJici10: false,
    tJici11: false,
    tJici12: false,
    tJici13: false,
    tJici14: false,
    tJici15: false,
  });

  const [register, { closeModal }] = useModalInner((data) => {
    if(data.data.length>0){
      data.data.forEach(v=>{
        zzlist.value.some((item,i)=>{
          if(item.id==v.torganization){
            zzlist.value.splice(i,1)
          }
        })
      })
    }
    findAllInfo()
  });

  function handleCancel() {
    standardSelected.value=''
    zz.value=''
    formState.uniqueAccStandard=0,
    formState.tName= '',
    codeFirst.value=''
    formState.tJici2= ''
    formState.tJici2= '',
    formState.tJici3= '',
    formState.tJici4= '',
    formState.tJici5= '',
    formState.tJici6= '',
    formState.tJici7= '',
    formState.tJici8= '',
    formState.tJici9= '',
    formState.tJici10= '',
    formState.tJici11= '',
    formState.tJici12= '',
    formState.tJici13= '',
    formState.tJici14= '',
    formState.tJici15= '',
    formState.tpid= '',
    formState.tOrganization= ''

    disabledflg.tJici1= false,
    disabledflg.tJici2= false,
    disabledflg.tJici3= false,
    disabledflg.tJici4= false,
    disabledflg.tJici5= false,
    disabledflg.tJici6= false,
    disabledflg.tJici7= false,
    disabledflg.tJici8= false,
    disabledflg.tJici9= false,
    disabledflg.tJici10= false,
    disabledflg.tJici11= false,
    disabledflg.tJici12= false,
    disabledflg.tJici13= false,
    disabledflg.tJici14= false,
    disabledflg.tJici15= false
  }

  async function handleOk() {
    if(zz.value===''){
      return message.error('请选择组织！');
    }
    if(standardSelected.value===''){
      return message.error('请选择所属集团会计科目制度！');
    }

    if(formState.tName!==''){
      const name= await countByName('组织模板',formState.tName)
      if (name > 0) {
        return message.error('名称已存在！');
      }
    }else{
      return message.error('请填写名称！');
    }

    formRef.value
      .validate()
      .then(() => {
        formState.tOrganization=zz.value
        formState.tJici1=codeFirst.value
        formState.tpid = standardSelected.value
        emit('save', toRaw(formState));
        closeModal();
      })
      .catch((error) => {
        console.log('error', error);
      });
  }

  async function standardCheck(data) {
    let standardUnique=accStandardList.value.filter(v=>v.id===data)[0]
    formState.uniqueAccStandard=standardUnique.uniqueAccStandard

    for (let i = 0; i < standardUnique.tjici.split('-').length; i++) {
      formState['tJici'+(i+1)]=standardUnique.tjici.split('-')[i]
      disabledflg['tJici'+(i+1)]=true
    }
    codeFirst.value = standardUnique.tjici.split('-')[0]
  }

  const findAllInfo = async () => {
    // 获取组织
    const orientation =await getOrganizeList();
    zzlist.value=orientation.items
    // 集团科目会计准则（自定义）
    const list = await findByCustomTemplate('自定义模板');
    accStandardList.value = list.items;
  }
</script>
<style lang="less">
  .vben-basic-title {
    color: rgb(1, 129, 226) !important;
  }

  .ant-modal-body {
    padding: 0px;
    border: 1px solid rgb(1, 129, 226);
    border-left: none;
    border-right: none;
  }

  .input {
    width: 50%;
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
  }

  .ant-input:focus {
    box-shadow: none;
  }

  .ant-select-selector {
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
  }
</style>

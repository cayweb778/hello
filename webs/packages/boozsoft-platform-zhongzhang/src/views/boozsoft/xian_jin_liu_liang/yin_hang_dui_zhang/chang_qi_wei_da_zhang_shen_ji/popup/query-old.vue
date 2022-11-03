<template>
  <BasicModal
    width="500px"
    v-bind="$attrs"
    title="长期未达账项"
    @ok="handleOk()"
    @cancel="handleClose()"
    :loading="loadMark"
    @register="register"
  >
    <div class="nc-open-content" style="margin-top: 10px;margin-bottom: 10px;">
      <div class="open-content-up">
        <AccountPicker theme="two" @reloadTable="dynamicAdReload"/>
        <br/>
        <label>未达账项：</label>
        <a-select v-model:value="formItems.weidaZhang" placeholder="未达账项" style="width: 50%">
          <a-select-option value="1">企业未达账项</a-select-option>
          <a-select-option value="0">银行未达账项</a-select-option>
        </a-select>
        <br/><br/>
        <label>科目：</label>
        <a-select v-model:value="formItems.kemu" placeholder="请选择科目" style="width: 50%;">
          <a-select-option v-for="item in codeKemu" :key="item.ccode" :value="item.ccode">
            {{ item.ccode }}-{{ item.ccodeName }}
          </a-select-option>
        </a-select>
        <br/><br/>
        <label>截至日期：</label>
        <a-date-picker
          v-model:value="formItems.endDate"
          placeholder="截止日期"
          format="YYYY-MM-DD"
          style="width: 50%;"
        />
      </div>
    </div>
  </BasicModal>
</template>

<script setup="props, { content }" lang="ts">
import {onMounted, ref, unref} from 'vue';
import {BasicModal, useModalInner} from '/@/components/Modal';
import {Select as ASelect, Input as AInput, DatePicker as ADatePicker} from 'ant-design-vue';
import {useCompanyOperateStoreWidthOut} from "/@/store/modules/operate-company";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker.vue";
import {getCurrentAccountName} from "/@/api/task-api/tast-bus-api";
import {findCodeKemuByBr} from "/@/api/record/system/bank-statement";
import router from "/@/router";
import {useTabs} from "/@/hooks/web/useTabs";
import {findAllByCode, findByUserIdAndAccIdAndYear} from "/@/api/sys-acc-auth/sys-acc-anth";
import {useUserStore} from "/@/store/modules/user";
import {useMessage} from "/@/hooks/web/useMessage";
import moment from "moment";
const {
  createErrorModal
} = useMessage()
const AInputSearch = AInput.Search;
const ASelectOption = ASelect.Option;

const {closeCurrent} = useTabs(router);

const emit = defineEmits(['register', 'save']);

const dynamicTenantId = ref(getCurrentAccountName(true))
const defaultAdName = ref(useCompanyOperateStoreWidthOut().getSchemaName)
const year = ref()

const formItems: any = ref({});
const [register, {closeModal}] = useModalInner((data) => {
  formItems.value.openOne = data.data.openOne;

  if (data.data.kemu != null && data.data.kemu != '') {
    formItems.value.kemu = data.data.kemu
  }
  formItems.value.weidaZhang = data.data.weidaZhang
});

const codeKemu: any = ref([])

async function reloadKemuPage() {
  codeKemu.value = []
  const res = await useRouteApi(findCodeKemuByBr, {schemaName: dynamicTenantId})()
  const sysAccAuth = await findByUserIdAndAccIdAndYear({userId:useUserStore().getUserInfo['id'],accId:defaultAdName.value,year:year.value})
  if (sysAccAuth.length>0) {
    if (sysAccAuth[0].supervisor=='1' || sysAccAuth[0].ccodeAll=='1') {
      codeKemu.value = res.filter(item => {
        item.value = item.ccode + '-' + item.ccodeName
        return item.bend == '1'
      })
    } else {
      const codeList = await findAllByCode({userId:useUserStore().getUserInfo['id'],accId:defaultAdName.value,year:year.value})
      if (codeList.length>0){
        codeList.forEach(code=>{
          res.forEach(item=>{
            if (item.ccode==code && item.bend == '1'){
              item.value = item.ccode + '-' + item.ccodeName
              codeKemu.value.push(item)
            }
          })
        })
      }
    }
  }
  if (codeKemu.value.length > 0) {
    formItems.value.kemu = codeKemu.value[0].ccode
  } else {
    formItems.value.kemu = ''
    createErrorModal({
      iconType: 'warning',
      title: '警告',
      content: '没有授权的银行科目，请授权后重新查询！'
    })
  }
}

onMounted(async () => {
  formItems.value.endDate = moment(useCompanyOperateStoreWidthOut().getLoginDate, 'YYYY/MM/DD')
  // await reloadKemuPage()
})

async function handleOk() {
  emit('save', unref(formItems));
  closeModal();
  return false;
}

async function handleClose() {
  if (null != formItems.value.openOne && formItems.value.openOne == 1) {
    await closeCurrent()
    await router.push('/')
  }
}

const loadMark = ref(false)
async function dynamicAdReload(obj) {
  loadMark.value = true
  defaultAdName.value = obj.accId
  dynamicTenantId.value = obj.accountMode
  year.value = obj.year

  formItems.value.defaultAdName = obj.accId
  formItems.value.dynamicTenantId = obj.accountMode
  formItems.value.year = obj.year
  formItems.value.thisAdInfo = obj
  await reloadKemuPage()
  loadMark.value = false
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
:deep(.ant-calendar-picker-input.ant-input),
:deep(.ant-select-single:not(.ant-select-customize-input)
      .ant-select-selector
      .ant-select-selection-search-input) {
  border: none;
  border-bottom: solid 1px rgb(191, 191, 191) !important;
  width: 100%;
  font-size: 14px;
  font-weight: bold;
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
    width: 50%;
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
    font-size: 14px;
    font-weight: bold;
  }

  .ant-input:focus {
    box-shadow: none;
  }

  :deep(.ant-select-selector) {
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
    font-size: 14px;
    font-weight: bold;
  }

  label {
    text-align: left;
    width: 90px;
    display: inline-block;
    padding: 5px 10px;
    margin-left: 2em;
    color: #535353;
    font-size: 13px;
    font-weight: bold;
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

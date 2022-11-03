<template>
  <BasicModal
    width="700px"
    v-bind="$attrs"
    title="长期未达账项"
    @ok="handleOk()"
    @cancel="handleClose()"
    :loading="loadMark"
    @register="register"
    :canFullscreen="false"
    :footer="null"
  >
    <template #title>
      <div style="height: 30px;width: 100%;background-color: #5f375c;color: white;line-height: 30px;">
        <AppstoreOutlined  style="margin: 0 2px;font-size: 14px;"/>
        <span style="font-size: 14px">现金银行</span>
      </div>
    </template>
    <div style="display: inline-flex;justify-content: space-between;width: 100%;">
      <div style="width: calc(100% - 150px);height: 100%;">
        <div style="text-align: center;padding: 10px 0 5px;">
          <SearchOutlined style="font-size: 24px;color: #0096c7;margin-top: 2px"/>
          <span style="line-height:30px;font-size: 24px;color: #0096c7;font-weight: bold;">&ensp;长期未达账项</span>
        </div>
        <div class="nc-open-content">
          <div class="open-content-up">
            <div class="border-div">
              <span>业务范围</span>
              <AccountPicker theme="three" @reloadTable="dynamicAdReload" style="text-align: center;"/>
            </div>
            <div class="border-div">
              <span>查询条件</span>
              <div>
                <span class="red_span"></span>
                <label>未达账项：</label>
                <a-select v-model:value="formItems.weidaZhang" placeholder="未达账项" style="width: 300px">
                  <template #suffixIcon><CaretDownOutlined style="color:#666666;"/></template>
                  <a-select-option value="1">企业未达账项</a-select-option>
                  <a-select-option value="0">银行未达账项</a-select-option>
                </a-select>
              </div>
              <div>
                <span class="red_span"></span>
                <label>科目：</label>
                <a-select v-model:value="formItems.kemu" placeholder="请选择科目" style="width: 300px;">
                  <template #suffixIcon><CaretDownOutlined style="color:#666666;"/></template>
                  <a-select-option v-for="item in codeKemu" :key="item.ccode" :value="item.ccode">
                    {{ item.ccode }}-{{ item.ccodeName }}
                  </a-select-option>
                </a-select>
              </div>
              <div>
                <span class="red_span"></span>
                <label>截至日期：</label>
                <a-date-picker
                  v-model:value="formItems.endDate"
                  placeholder="截止日期"
                  format="YYYY-MM-DD"
                  value-format="YYYY-MM-DD"
                  style="width: 300px;"
                />
              </div>
              <div>
                <span class="red_span"></span>
                <label>字号：</label>
                <a-select v-model:value="formItems.fontSize" style="width: 300px;">
                  <template #suffixIcon><CaretDownOutlined style="color:#666666;"/></template>
                  <a-select-option value="MAX"> 大号字体</a-select-option>
                  <a-select-option value="MIN"> 小号字体</a-select-option>
                </a-select>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="right-btns">
        <Button style="width: 100px;" shape="round" @click="handleOk"  type="primary">查询</Button>
        <Button style="width: 100px;margin-top: 10px" shape="round" @click="handleClose">取消</Button>
      </div>
    </div>
  </BasicModal>
</template>

<script setup="props, { content }" lang="ts">
import {onMounted, ref, unref} from 'vue';
import {BasicModal, useModalInner} from '/@/components/Modal';
import {
  DatePicker as ADatePicker,
  Select as ASelect,
  Input as AInput,
  Checkbox as ACheckbox,
  Popover as APopover,
  Switch as ASwitch,
  Radio as ARadio,
  Upload as AUpload,
  Table as ATable,
  Tabs as ATabs,
  message,
  Button
} from 'ant-design-vue';
import {AppstoreOutlined, SearchOutlined,CaretDownOutlined,LinkOutlined} from '@ant-design/icons-vue';
import {useCompanyOperateStoreWidthOut} from "/@/store/modules/operate-company";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker.vue";
import {getCurrentAccountName, hasBlank} from "/@/api/task-api/tast-bus-api";
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
  if (hasBlank(formItems.value.endDate)) {
    formItems.value.endDate = useCompanyOperateStoreWidthOut().getLoginDate
  }
  formItems.value.weidaZhang = data.data.weidaZhang
  formItems.value.fontSize = data.data.showRulesSize
});

const codeKemu: any = ref([])

async function reloadKemuPage() {
  codeKemu.value = []
  const res = await useRouteApi(findCodeKemuByBr, {schemaName: dynamicTenantId})({})
  codeKemu.value = res.filter(item => {
    item.value = item.ccode + '-' + item.ccodeName
    return item.bend == '1'
  })
  /*const sysAccAuth = await findByUserIdAndAccIdAndYear({userId:useUserStore().getUserInfo['id'],accId:defaultAdName.value,year:year.value})
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
  }*/
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
  formItems.value.endDate = useCompanyOperateStoreWidthOut().getLoginDate
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
<style lang="less" scoped>
:deep(.ant-checkbox){
  margin-top: -8px;
}
.nc-open-content {
  background-image: url(/@/assets/images/homes/bg-pattern.png);
  background-repeat: no-repeat;
  background-position: 66% 8%;
  background-size: contain;
  position: relative;
  :deep(.ant-select-selector),:deep(.ant-input),:deep(.ant-picker), :deep(.ant-input-affix-wrapper) {
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
    background: none;
  }
  .border-div {
    position: relative;
    border: 1px #a29f9f solid;
    margin: 20px 10px;
    padding: 2.5%;

    > span {
      display: block;
      text-align: center;
      background-color: white;
      position: absolute;
      left: 50px;
      top: -10px;
      color: #888888;
      font-size: 12px;
      font-weight: bold;
      padding-left: 1em;
      padding-right: 1em;
    }
    :deep(.account-picker){
      >div{
        text-align: left;
      }
    }

    label {
      text-align: left;
      width: 100px;
      display: inline-block;
      color: #535353;
      font-size: 13px;
      font-weight: bold;
    }
    :deep(.ant-input){
      border: none !important;
    }

    :deep(.red_span) {
      color: red;
      font-weight: bold;
      display: inline-block;
      width: 20px;
      text-align: left;
    }

    .ant-select{
      color: #000000;
      font-weight: bold;
    }
  }

}

.right-btns{
  width: 150px;background-color: #f1f1f1;padding: 10% 4%;height: 350px;
  :deep(.ant-btn-primary:hover){
    border: 1px solid #5f375c;
  }
}
:global(.ant-modal-header) {
  padding: 10px 0 !important;
}
:global(.ant-modal-close-x){
  height: 30px !important;
  color: white;
}

:deep(.ant-radio-button-wrapper){
  color: #0096c7;
}
</style>

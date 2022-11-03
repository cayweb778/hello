<template>
  <BasicModal
    destroyOnClose
    width="600px"
    v-bind="$attrs"
    :canFullscreen="false"
    okText="保存"
    title="会计科目"
    @ok="simpleSave()"
    @register="register"
  >
    <template #title>
      <div style="display: flex;color: #0096c7;margin-top: 5px;">
        <span style="line-height:60px;font-size: 28px;">
          <PlusCircleOutlined style="font-size: 34px;font-weight: bold"/>
        </span>
        <span style="line-height:40px;font-size: 28px;margin-top:5px;">
          &nbsp;&nbsp;新增会计科目
        </span>
      </div>
    </template>
    <div class="nc-open-content">
      <div class="open-content-up">
        <div style="margin-top: 50px;">
          <label style="font-size: 18px;margin-left: 0;width:150px;">科目名称：</label>
          <a-input v-model:value.trim="ccodeName" autocomplete="off"
                   style="font-size: 18px;width: 60%;" @keydown.enter.native="$refs.focus2.focus()"/>
          <br><br>
          <label style="font-size: 18px;margin-left: 0;width:150px;">上级科目名称：</label>
          <TreeSelect
            ref="focus2"
            style="width: 60%;margin-left:-6%;"
            :tree-data="parentList"
            tree-default-expand-all
            allow-clear
            show-search
            v-model:value="parentCode"
            :filterTreeNode="filterOption"
            @keydown.enter.native="simpleSave"
          >
            <template #suffixIcon>
              <CaretDownOutlined style="color:#666666;"/>
            </template>
          </TreeSelect>
          <br><br>
          <label style="font-size: 18px;margin-left: 0;width:150px;">外币核算：</label>
          <a-select
            v-model:value="currencyType"
            show-search
            style="width: 60%;"
            allow-clear
          >
            <a-select-option :value="item.foreignName" v-for="(item, i) in currencylist">
              {{ item.foreignName }}
            </a-select-option>
            <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
          </a-select>
        </div>
      </div>
    </div>
    <template #footer>
      <a-button @click="closeModal()">取消</a-button>
      <a-button @click="simpleSave()" :disabled="btn" type="primary">保存</a-button>
    </template>
  </BasicModal>
</template>
<script setup="props, { content }" lang="ts">
import {ref, toRaw, watch} from 'vue';
import {BasicModal, useModal, useModalInner} from '/@/components/Modal';
import {CaretDownOutlined, PlusCircleOutlined} from '@ant-design/icons-vue'
import {
  Input as AInput,
  Radio as ARadio,
  Select as ASelect,
  Statistic as AStatistic,
  Tabs as ATabs,
  TreeSelect
} from 'ant-design-vue';
import {useMessage} from "/@/hooks/web/useMessage";
import {findAllByUniqueCodeAndDatabaseNameAndIsCtrl} from "/@/api/record/system/group-dataSee";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {
  company_findByAuthorizationAndIyearKeMuTree,
  company_findByAuthorizationKeMuTree, company_findByCcodeAndIyearOrderByDbillDate,
  company_findByIyearAndCcode, company_findByLowerMaxCodeNum,
  company_simpleSave,
  company_treeCodeAndFlag1,
  findAllByCurrency
} from "/@/api/codekemu/codekemu";
import {findDataBase} from "/@/api/record/system/account";
import {getThisAdInfoData} from "/@/api/record/system/financial-settings";
import {useUserStore} from "/@/store/modules/user";

const ASelectOption = ASelect.Option;
const AInputSearch = AInput.Search;
const AStatisticCountdown = AStatistic.Countdown;
const ATabPane = ATabs.TabPane;
const ARadioGroup = ARadio.Group
const emit = defineEmits(['register', 'save']);
const {createConfirm, createWarningModal, createMessage} = useMessage();
const [registerCodeAllPopPage, {openModal: openCodeAllPopPage}] = useModal();

// 数据库模式名称
const database = ref('');
const iyear: any = ref('');
const currencyType: any = ref('');
const ccodeName: any = ref('');
const parentCode: any = ref('');
const jici: any = ref('');
// 1 是独立账套  2 集团账套
const independent: any = ref('');
const btn: any = ref(true);
const parentList: any = ref([]);
const currencylist: any = ref([]);
const userStore = useUserStore();

watch(() => parentCode.value, async () => {
  btn.value=parentCode.value==undefined?true:false
})

const [register, {closeModal}] = useModalInner(async (data) => {
  currencyType.value=''
  ccodeName.value=''
  parentCode.value=''

  iyear.value = data.iyear;
  database.value = data.database;
  findByGroupDataSeeSwith()
  // 获取所有国币-读取常用外币表
  await useRouteApi(findAllByCurrency,{schemaName:database})('').then((res) => {
    currencylist.value = res;
  });

  // 重新查询账套编码信息
  await findDataBase(database.value.substring(0,database.value.length-5), data.iyear).then((item) => {
    jici.value = item.jici;
    // 进页面执行
    getThisAdInfoData({ accId: item.accountId }).then((res) => {
      independent.value=res.independent
    });
  });
});

// 科目是否授权
async function findByGroupDataSeeSwith() {
  let map = {
    uniqueCode: database.value,
    databaseName: 'code_kemu',
    ctrl: '1',
  }
  let author = await findAllByUniqueCodeAndDatabaseNameAndIsCtrl(map)
  if (author.length > 0) {
    // 1是2否 按年度控制科目
    let isCtrlYear = author[0].isCtrlYear
    if (isCtrlYear == '1') {
      await useRouteApi(company_findByAuthorizationAndIyearKeMuTree, {schemaName: database})({tenantId: database.value,iyear: iyear.value,userId:userStore.getUserInfo.id})
        .then((res) => {
          parentList.value = res
        });
    } else {
      await useRouteApi(company_findByAuthorizationKeMuTree, {schemaName: database})({
        tenantId: database.value,
        userId:userStore.getUserInfo.id
      })
        .then((res) => {
          parentList.value =  res
        });
    }
    if (parentList.value.length == 0) {
      return createWarningModal({content: '会计科目已被数据控制,请进行数据授权！'});
    }
  } else {
    findByKeMuAndIyear()
  }
}

// 查询科目
async function findByKeMuAndIyear() {
  // 会计科目
  await useRouteApi(company_treeCodeAndFlag1, {schemaName: database})({iyear: iyear.value}).then((res) => {
    parentList.value = res
  });
}
function filterOption(input, option) {
  // 不是数字
  if(isNaN(input)){
    return option.title.toLowerCase().indexOf(input.toLowerCase()) >= 0;
  }else{
    return option.title.toLowerCase().startsWith(input.toLowerCase());
  }
}

async function simpleSave() {
  if(ccodeName.value==''){
    return createWarningModal({content: '科目名称不能为空!'});
  }
  if(parentCode.value=='' || parentCode.value==undefined){
    return createWarningModal({content: '上级科目不能为空!'});
  }
  // 查找上级科目信息
  let findByCode=await useRouteApi(company_findByIyearAndCcode, {schemaName: database})({iyear: iyear.value,ccode:parentCode.value})
  // 集团账套判断
  if(independent.value=='0'&&findByCode.lowerControl!=='1'){
    return createWarningModal({content: '科目已设置不允许新增下级!'});
  }

  const igrade = findByCode.igrade;
  let last_jici = parseInt(igrade) + 1 ;
  const arr = jici.value.replaceAll('-', '').substring(0, last_jici);
  var code_length = 0; // 符合规则的科目长度
  for (let i = parseInt(igrade); i < arr.length; i++) {
    code_length += parseInt(arr[i]);
  }
  const newLastCode = await useRouteApi(company_findByLowerMaxCodeNum,{schemaName:database})({
    superiorCcode: parentCode.value,
    code_length: code_length,
  });

  const findAccvoucher = await useRouteApi(company_findByCcodeAndIyearOrderByDbillDate,{schemaName:database})({
    ccode: parentCode.value,
    iyear: iyear.value,
  });

  let map={
    iyear:iyear.value,
    ccodeName:ccodeName.value,
    parentCode:parentCode.value,
    currencyType:currencyType.value,
    newLastCode:newLastCode,
    newLastIgrade:igrade,
  }
  if(findByCode.bend=='1'&&findAccvoucher>0){
    createConfirm({
      iconType: 'warning',
      title: '提示',
      content: '上级科目已使用,凭证和期初将替换成新科目。确定操作吗？',
      onOk: async() => {
        save2(map)
      },
    })
  }else{
    save2(map)
  }
}
async function save2(map) {
  await useRouteApi(company_simpleSave, {schemaName: database})(map)
  emit('throwData', toRaw(map));
  closeModal();
}
</script>
<style lang="less" scoped>
.red_span {
  color: red;
  font-weight: bold;
  display: inline-block;
  width: 20px;
  text-align: right;
}

.nc-open-content {
  input {
    width: 35%;
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
    font-weight: bold;
    font-size: 14px;
  }

  .abc {
    border-bottom: 2px solid #666666 !important;
    font-size: 18px;
  }

  .ant-input:focus {
    box-shadow: none;
  }

  :deep(.ant-select-selector) {
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
  }

  label {
    padding: 5px 35px;
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
      background-color: #0096c7;
    }
  }
}

:deep(.ant-checkbox) {
  border: 1px solid #2f2a2a
}

.nc-border-div {
  position: relative;
  border: 1px solid #4949496b;
  margin: 2% 0;
  width: 83%;

  .nc-border-div-span {
    min-width: 140px;
    background-color: white;
    position: absolute;
    top: -12px;
    left: 50px;
    display: block;
    text-align: center;
    color: black;
    font-weight: bold;
  }

  .nc-border-div-content {
    padding: 10px;
    min-height: 40px;
  }
}

.divtop {
  margin-top: 10px;
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

.tablePointer {
  pointer-events: none;
}

.a-table-font-size-12 :deep(td),
.a-table-font-size-12 :deep(th) {
  font-size: 14px !important;
  padding: 2px 8px !important;
}

:deep(.ant-tabs-content-holder) {
  margin-top: -9px;
}
</style>

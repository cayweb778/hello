<template>
  <BasicModal
    title="多计量添加"
    v-bind="$attrs"
    width="700px"
    :height="300"
    @ok="handleOk()"
    @register="register"
  >

    <template #title>
      <div style="display: flex;" class="vben-basic-title">
        <span style="line-height:40px;font-size: 28px;">
          <PlusCircleOutlined style="font-size: 34px;font-weight: bold"/>&nbsp;&nbsp;多计量
        </span>
      </div>
      <div style="display: inline-block;position:absolute;right: 40px;top: 10px;z-index: 100000;background:#ffffff">
        <img src="../../../../../assets/images/012.png" style="height:80px;margin-right: 10px;"/>
      </div>
    </template>

    <div
      class="nc-open-content"
      style="height: 100%"
    >
      <div class="open-content-up">
        <div style="text-align: left;padding-left: 8%">
          <a-radio-group v-model:value="formItems.conversionType" name="radioGroup">
            <a-radio  value="1">固定换算率</a-radio>
            <a-radio value="2">浮动换算率</a-radio>
          </a-radio-group>
          <br/>
          <label style="font-size: 18px;margin-left: 0;">计量单位：</label>
          <a-input v-model:value="formItems.unitName" placeholder="请输入计量单位名称" class="abc" style="width: 55%;" />
          <span class="red_span">*</span>
        </div>

<!--        <label style="padding-top: 30px;">换算类型：</label>
        <a-select
          placeholder="请选择换算类型"
          v-model:value="formItems.conversionType"
          style="width: 150px"
          :options="options"
        >
        </a-select>-->
        <br>
        <a-table
          :columns="columns"
          :bordered="false"
          style="padding-top: 10px;"
          size="small"
          :pagination="false"
          class="a-table-font-size-12"
          :data-source="dataSource" >
          <template #bodyCell="{ column, text, record }">

            <template v-if="['unitName'].includes(column.dataIndex)">
              <div>
                <a-input
                  v-if="editableData[record.key]"
                  v-model:value="editableData[record.key][column.dataIndex]"
                  style="margin: -5px 0"
                  placeholder="请输入计量单位名称"
                />
                <template v-else>
                  {{ text }}
                </template>
              </div>
            </template>

            <template v-else-if="column.dataIndex === 'conversionRate'">
              <div> 
                <a-input-number
                  v-if="editableData[record.key]"
                  v-model:value="editableData[record.key][column.dataIndex]"
                  style="margin: -5px 0"
                  :min="0"
                  :max="10000"
                  :step="0.00001"
                  string-mode
                  placeholder="请输入换算率"
                />
                <template v-else>
                  {{ text }}
                </template>
              </div>
            </template>

            <template v-else-if="column.dataIndex === 'isMain'">
              <div>
                <a-checkbox  @change="changeBox(record.key)" v-model:checked="dataSource[record.key][column.dataIndex]" style="text-align: center"></a-checkbox>
              </div>
            </template>

            <template v-else-if="column.dataIndex === 'operation'">
              <div class="editable-row-operations">
                <span v-if="editableData[record.key]">
                  <a @click="saveTable(record.key)">关闭</a>
                </span>
                <span v-else>
                  <a-popover title="操作" trigger="hover" style="width: 100px">
                    <template #content>
                      <p ><a  @click="editTable(record.key)">编辑</a></p>
                      <p ><a  @click="fzTable(record)">加行</a></p>
                      <p ><a  @click="delTable(record.key)">减行</a></p>
                    </template>
                    <a-button>
                      <SettingFilled :style="{ fontSize: '14px' }"/>
                    </a-button>
                  </a-popover>
                </span>
              </div>
            </template>
          </template>
        </a-table>
      </div>
    </div>

    <template #footer>
      <div style="height: 35px">
        <div style="float: right">
          <a-button @click="handleClose()">取消</a-button>
          <a-button @click="handleOk(false)"  type="primary">保存</a-button>
          <a-button  @click="handleOk(true)" type="primary">保存并新增</a-button>
        </div>
      </div>
    </template>
  </BasicModal>
</template>

<script lang="ts" setup="props, { content }">
import {ref, unref, onMounted, reactive,UnwrapRef,SelectProps } from 'vue';
import { cloneDeep } from 'lodash-es';
import {BasicModal, useModalInner} from '/@/components/Modal';
import {
  DatePicker as ADatePicker,
  Select as ASelect,
  Input as AInput,
  InputNumber as AInputNumber,
  Checkbox as ACheckbox,
  Popover as APopover,
  Switch as ASwitch,
  Radio as ARadio,
  Upload as AUpload,
  Table as ATable,
  Tabs as ATabs,
  message,
  Modal as AModal
} from 'ant-design-vue';
import {
  PlusCircleOutlined,EditOutlined,SettingFilled,
  CaretDownOutlined
} from '@ant-design/icons-vue'
import {
  saveManyList,
} from '/@/api/group/unit-mea'
import {getCurrentAccountName} from "/@/api/task-api/tast-bus-api";
import {useUserStore} from "/@/store/modules/user";
import {useCompanyOperateStoreWidthOut} from "/@/store/modules/operate-company";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
const ASelectOption = ASelect.Option;
const AInputSearch = AInput.Search;
const ARadioGroup = ARadio.Group;
const ARadioButton = ARadio.Button;
const ACheckboxGroup = ACheckbox.Group;
const ATabPane = ATabs.TabPane;
const emit = defineEmits(['register', 'save']);

const activeKey: any = ref('');
const formItems: any = ref({
  conversionType:'1'
});

const year = ref(useCompanyOperateStoreWidthOut().getLoginDate.slice(0, 4))

const userStore: any = useUserStore();
// 数据库模式名称
const dynamicTenantId = ref(getCurrentAccountName(true))
const defaultAdName = ref(useCompanyOperateStoreWidthOut().getSchemaName)

const accPrint: any = ref({})

const options = ref<SelectProps['options']>([
  { value: '1', label: '固定换算率' },
  { value: '2', label: '浮动换算率' },
]);

const columns = [
  {
    title: '操作',
    dataIndex: 'operation',
    width: '10%',
    align: 'center',

  },
  {
    title: '序号',
    dataIndex: 'num',
    width: '8%',
    align: 'center',
  },
  {
    title: '计量单位名称',
    dataIndex: 'unitName',
    width: '15%',
    align: 'center',
  },
  {
    title: '主计量',
    dataIndex: 'isMain',
    width: '8%',
  },
  {
    title: '换算率',
    dataIndex: 'conversionRate',
    width: '12%',
  },
  {
    title: '换算说明',
    dataIndex: 'conversionExplain',
    width: '15%',
  },
];
interface DataItem {
  key: number;
  num: number;
  unitName: string;
  isMain: boolean;
  conversionRate: string;
  conversionExplain: string;
}
const data: DataItem[] = [];

const dataSource = ref([]);
const editableData: UnwrapRef<Record<string, DataItem>> = reactive({});

const kmListAll = ref([]);

const edit = (key: string) => {
  editableData[key] = cloneDeep(dataSource.value.filter(item => key === item.key)[0]);
};
const save = (key: string) => {
  Object.assign(dataSource.value.filter(item => key === item.key)[0], editableData[key]);
  delete editableData[key];
};
const cancel = (key: string) => {
  delete editableData[key];
};

const kmList = ref([]);
const km = ref();
const [register, {closeModal, setModalProps}] = useModalInner((d) => {

  dynamicTenantId.value = d.accId
  //
  console.log(dynamicTenantId.value)
  if(dataSource.value.length === 0){
    dataSource.value.push({
      key: '0',
      num: '1',
      unitName: '',
      isMain: false,
      conversionRate: '1',
      conversionExplain: '',
    })
  }
});

async function handleOk(flg) {
  formItems.value.dynamicTenantId = dynamicTenantId.value
  formItems.value.defaultAdName = defaultAdName.value

  //不能有重复科目
  let arr = dataSource.value.map(v=> v.unitName)
  if(new Set(arr).size !== arr.length){
    message.error("存在重复的名称！")
    return false;
  }
  //保存
  console.log(dynamicTenantId.value)
  await saveManyList({
    unitName: formItems.value.unitName,
    conversionType: formItems.value.conversionType,
    list: dataSource.value
  });
  dataSource.value = []
  if(flg){
    formItems.value = {}
  }else{
    //emit('save', unref(formItems));
    closeModal();
    return true;
  }
}

const loading = ref('false')

function handleClose() {
  dataSource.value = []
  closeModal();
}

const fzTable = (record:any) => {
  //浮动最多2条
  if(formItems.value.conversionType === '2' && dataSource.value.length >= 2){
    message.error("浮动换算率最多2条！")
  }else{
    let d = JSON.parse(JSON.stringify(record))
    d.num =  dataSource.value.length + 1
    d.key =  dataSource.value.length
    dataSource.value.push(d)
  }
};
const delTable = (key: string) => {
  dataSource.value.splice(key,1);
  //重新排序
  dataSource.value.forEach((v,i)=>{
    v.key = i
    v.num = i+1
  })
};
const editTable = (key: string) => {
  editableData[key] = cloneDeep(dataSource.value.filter(item => key === item.key)[0]);
};
const saveTable = (key: string) => {
  //验证参数

  if(!editableData[key].unitName){
    message.error("请输入计量单位名称！")
    return
  }
  if(formItems.conversionType === '1'){
    if(!editableData[key].conversionRate){
      message.error("请输入换算率！")
      return
    }
  }

  Object.assign(dataSource.value.filter(item => key === item.key)[0], editableData[key]);
  delete editableData[key];


  //设置主计量
  let n = ''
  dataSource.value.forEach(item=>{
    if(true === item.isMain){
      item.conversionRate = '1'
      n = item.unitName
    }else{
      item.isMain = false
      item.conversionExplain = ''
    }
  })
  if(n != ''){
    //然后根据数据 得出换算说明
    dataSource.value.forEach(item=>{
      if('1' != item.isMain){
        item.conversionExplain = '1'+item.unitName+'='+ item.conversionRate +n
      }
    })
  }
};

const filterOption = (input: string, option: any) => {
  return option.value.toLowerCase().indexOf(input.toLowerCase()) >= 0;
};

const handleChange = (key: string) => {
  editableData[key].name = kmListAll.value.filter(item => editableData[key].ccode === item.ccode)[0].ccodeName;
};

const changeBox = (key: string) => {
  //设置主计量
  let n = ''
  dataSource.value.forEach(item=>{
    if(key === item.key){
      item.conversionRate = '1'
      n = item.unitName
    }else{
      item.isMain = false
    }
    item.conversionExplain = ''
  })
  //然后根据数据 得出换算说明
  dataSource.value.forEach(item=>{
    if(key != item.key){
      let s = '1'
      item.conversionExplain = '1'+item.unitName+'='+ item.conversionRate +n
    }
  })
};
const formatFx = (o: any) => {
  let str = ''
  if(o === '1'){
    str = '固定换算率'
  }else if(o === '2'){
    str = '浮动换算率'
  }
  return str;
};

</script>

<style>
.vben-basic-title {
  color: #0096c7 !important;
  border:none !important;
}

.ant-modal-body {
  padding: 0px;
  border-bottom: 1px solid rgb(1, 129, 226);
  border-left: none;
  border-right: none;
  border-top: none !important;
}
.ant-modal-header{
  border: none !important;
}
</style>
<style lang="less" scoped>
:deep(.vben-basic-title) {
  color: #0096c7 !important;
}
.ant-modal-header{
  border: none !important;
}


:deep(.ant-select-single:not(.ant-select-customize-input)
.ant-select-selector
.ant-select-selection-search-input) {
  border: none !important;
}

.vben-basic-title {
  color: #0096c7 !important;
  border: none !important;
}

.ant-modal-body {
  padding: 0px;
  border-bottom: 1px solid rgb(1, 129, 226);
  border-left: none;
  border-right: none;
  border-top: none !important;
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
    width: 90%;
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
    width: 120px;
    display: inline-block;
    padding-top: 5px;
    padding-left: 0px;
    padding-bottom: 5px;
    color: #535353;
    font-size: 13px;
    margin-left: 1em;
    font-weight: bold;
  }

  .rightLabel {
    padding-left: 6%;
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

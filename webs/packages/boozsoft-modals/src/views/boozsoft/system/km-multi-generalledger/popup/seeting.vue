<template>
  <BasicModal
    title="多栏设置"
    v-bind="$attrs"
    width="800px"
    @ok="handleOk()"
    @register="register"
  >
    <div class="nc-open-content" style="height: 500px">
      <div class="open-content-up">
        <a-table
          :columns="columns"
          :bordered="false"
          style="padding-top: 20px;"
          size="small"
          :pagination="false"
          class="a-table-font-size-12"
          :data-source="dataSource" >
          <template #bodyCell="{ column, text, record }">
            <template v-if="['name'].includes(column.dataIndex)">
              <div>
                <a-input
                  v-if="editableData[record.key]"
                  v-model:value="editableData[record.key][column.dataIndex]"
                  style="margin: -5px 0"
                />
                <template v-else>
                  {{ text }}
                </template>
              </div>
            </template>

            <template v-else-if="column.dataIndex === 'fx'">
              <div>
                <a-select
                  v-if="editableData[record.key]"
                  placeholder="请选择分析方向"
                  v-model:value="editableData[record.key][column.dataIndex]"
                  style="width: 110px"
                  :options="options"
                >
                </a-select>
                <template v-else>
                  {{ formatFx(text) }}
                </template>
              </div>
            </template>

            <template v-else-if="column.dataIndex === 'ccode'">
              <div>
                <a-select
                  v-if="editableData[record.key]"
                  placeholder="请选择科目"
                  v-model:value="editableData[record.key][column.dataIndex]"
                  style="width: 100%;text-align: center"
                  show-search
                  :filter-option="filterOption"
                  @change="handleChange(record.key)"
                >
                  <a-select-option v-for="item in kmListAll" :key="item.ccode" :value="item.ccode">
                    {{item.ccode }}
                  </a-select-option>

                </a-select>
                <template v-else>
                  {{ text }}
                </template>
              </div>
            </template>

            <template v-else-if="column.dataIndex === 'operation'">
              <div class="editable-row-operations">
                <span v-if="editableData[record.num]">
                  <a @click="saveTable(record.num)">关闭</a>
                </span>
                <span v-else>
                  <a-popover title="操作" trigger="hover">
                    <template #content>
                      <p><a @click="editTable(record.num)">编辑</a></p>
                      <p><a @click="fzTable(record)">复制</a></p>
                      <p><a @click="delTable(record.num)">删除</a></p>
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
          <a-button @click="autoTable"  type="primary">自动编制</a-button>
          <a-button @click="handleClose()">取消</a-button>
          <a-button  @click="handleOk()" type="primary">保存</a-button>
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
import {EditOutlined,SettingFilled} from '@ant-design/icons-vue'
import {
  findCodeKmByPeriod,
  saveMultiSet,
  findCodeKmAll,
  findCodeByKm
} from '/@/api/record/generalLedger/data';
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
const formItems: any = ref({});

const year = ref(useCompanyOperateStoreWidthOut().getLoginDate.slice(0, 4))

const userStore: any = useUserStore();
// 数据库模式名称
const dynamicTenantId = ref(getCurrentAccountName(true))
const defaultAdName = ref(useCompanyOperateStoreWidthOut().getSchemaName)

const accPrint: any = ref({})

const options = ref<SelectProps['options']>([
  { value: '1', label: '借' },
  { value: '2', label: '贷' },
  { value: '3', label: '双向' },
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
    title: '分析方向',
    dataIndex: 'fx',
    width: '15%',
    align: 'center',
  },
  {
    title: '科目编码',
    dataIndex: 'ccode',
    width: '30%',
  },
  {
    title: '栏目名称',
    dataIndex: 'name',
    width: '40%',

  },
];
interface DataItem {
  key: number;
  num: number;
  fx: string;
  ccode: string;
  name: string;
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

  kmList.value = d.data
  dynamicTenantId.value = d.accId
  km.value = d.km

  //所有科目
  useRouteApi(findCodeKmAll,{schemaName:dynamicTenantId.value})({})
    .then(list =>{
       kmListAll.value =list
    });

  d.data.forEach((v,i)=>{
    dataSource.value.push({
      key: i+1,
      num: i+1,
      fx: v.fx,
      ccode: v.ccode,
      name: v.ccodeName ? v.ccodeName : v.name,
    });
  })

  if(dataSource.value.length === 0){
    dataSource.value.push({
      key: '1',
      num: '1',
      fx: '1',
      ccode: '',
      name: '',
    })
  }
});

async function handleOk() {
  formItems.value.dynamicTenantId = dynamicTenantId.value
  formItems.value.defaultAdName = defaultAdName.value

  //不能有重复科目
  let arr = dataSource.value.map(v=> v.ccode)
  if(new Set(arr).size !== arr.length){
    message.error("存在重复的分析科目！")
    return false;
  }
  //保存
  await useRouteApi(saveMultiSet,{schemaName:dynamicTenantId.value})({
    km: km.value,
    list: dataSource.value
  });
  dataSource.value = []
  emit('save', unref(formItems));
  closeModal();
  return true;
}

const loading = ref('false')

function handleClose() {
  dataSource.value = []
  closeModal();
}

const fzTable = (record:any) => {
  let d = JSON.parse(JSON.stringify(record))
  d.num =  dataSource.value.length + 1
  d.key =  d.num
  dataSource.value.push(d)
};
const delTable = (key: string) => {
  dataSource.value.splice(key-1,1);
  //重新排序
  dataSource.value.forEach((v,i)=>{
    v.num = i+1
  })
};
const editTable = (key: string) => {
  editableData[key] = cloneDeep(dataSource.value.filter(item => key === item.key)[0]);
};
const saveTable = (key: string) => {
  //验证参数
  if(!editableData[key].fx){
    message.error("请选择分析方向！")
    return
  }
  if(!editableData[key].ccode){
    message.error("请选择科目！")
    return
  }
  if(!editableData[key].name){
    message.error("请输入栏目名称！")
    return
  }

  Object.assign(dataSource.value.filter(item => key === item.key)[0], editableData[key]);
  delete editableData[key];
};

const filterOption = (input: string, option: any) => {
  return option.value.toLowerCase().indexOf(input.toLowerCase()) >= 0;
};

const handleChange = (key: string) => {
  editableData[key].name = kmListAll.value.filter(item => editableData[key].ccode === item.ccode)[0].ccodeName;
};

const autoTable = () => {
  dataSource.value = []
  //查询km下一级科目
  useRouteApi(findCodeByKm,{schemaName:dynamicTenantId.value})({
    km: km.value
  })
  .then(list =>{
    list.forEach((v,i)=>{
      dataSource.value.push({
        key: i+1,
        num: i+1,
        fx: v.bprogerty,
        ccode: v.ccode,
        name: v.ccodeName ? v.ccodeName : v.name,
      });
    })
  });
};

const formatFx = (o: any) => {
  let str = '借'
  if(o === '1'){
    str = '借'
  }else if(o === '2'){
    str = '贷'
  }else{
    str = '双向'
  }
  return str;
};

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
:deep(.ant-calendar-picker-input.ant-input), :deep(.ant-input-number), :deep(.ant-input-affix-wrapper) {
  border: none;
  border-bottom: 1px solid rgb(191, 191, 191) !important;
  width: 100%;
}

.nc-open-content {
  position: relative;

  .open-content-title {
    > div {
      display: inline-block;
    }

    > div:nth-of-type(1) {
      width: 200px;
      background-color: #efeeee;
      color: black;
      font-size: 20px;
      text-align: center;
      padding: 5px 10px
    }
  }

  .open-content-up {
    position: relative;

    .ocup-position {
      position: absolute;
      left: 0;
      width: 180px;
      background-color: #0096c7;
      color: white;
      font-size: 16px;
      text-align: center;
      padding: 5px 10px;
    }

    .ocup-position:nth-of-type(1) {
      top: 0px;
    }

    .ocup-position:nth-of-type(2) {
      top: 190px;
    }

    ul {
      padding: 10px 60px 0;

      li {
        margin: 10px 0;
        text-align: left;

        span {
          font-size: 14px;
          color: #535353;
        }

        > span:nth-of-type(1), .right_span {
          display: inline-block;
          width: 120px;
        }

        .ant-select {
          font-size: 14px;
        }
      }
    }
  }

  .open-content-foot {
    display: block;
    position: fixed;
    margin-top: 43px;
  }

  .ant-tabs-tabpane-active {
    overflow-y: auto;
    height: 400px;
  }

  .ant-select-selection-search-input {
    border-bottom: none !important;
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

  .ant-radio-group {
    .ant-radio-wrapper {
      width: 70px;

      .ant-radio-input {
        border-color: slategrey;
      }
    }

    p:nth-of-type(2) {
      margin-bottom: 0;
    }
  }
}


</style>

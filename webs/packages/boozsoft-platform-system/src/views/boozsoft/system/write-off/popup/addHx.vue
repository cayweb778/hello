<template>
  <BasicModal
    width="900px"
    v-bind="$attrs"
    title="往来核销"
    @ok="handleOk()"
    @register="register"
  >
  <div class="app-container">
    <div style="text-align: center;font-size: 16px;">往来单位简称:{{dwName}}</div>

    <div class="app-container-bottom"/>
      <span>借方: {{jmoney}}</span>
      <a-table :columns="columns"
               size="middle"
                bordered
               :data-source="jlist"
               :pagination="{ pageSize: 50 }"
               :scroll="{ y: 240 }">

        <template v-for="col in ['hxMoney']" #[col]="{ text, record }" :key="col">
          <div>
            <a-input
              v-if="editableData[record.key]"
              v-model:value="editableData[record.key][col]"
            />
            <template v-else>
              {{ text }}
            </template>
          </div>
        </template>
        <template #operation="{ record }">
          <div class="editable-row-operations">
            <span v-if="editableData[record.key]">
              <a @click="save(record.key)">保存</a>
            </span>
                <span v-else>
              <a @click="edit(record.key)">编辑</a>
            </span>
          </div>
        </template>

      </a-table>
     <span>贷方: {{dmoney}}</span>
     <a-table :columns="columns2"
             size="middle"
             :data-source="dlist"
             :pagination="{ pageSize: 50 }"
             :scroll="{ y: 240 }">
       <template v-for="col in ['hxMoney']" #[col]="{ text, record }" :key="col">
         <div>
           <a-input
             v-if="editableData2[record.key]"
             v-model:value="editableData2[record.key][col]"
           />
           <template v-else>
             {{ text }}
           </template>
         </div>
       </template>
       <template #operation="{ record }">
         <div class="editable-row-operations">
            <span v-if="editableData2[record.key]">
              <a @click="save2(record.key)">保存</a>
            </span>
           <span v-else>
              <a @click="edit2(record.key)">编辑</a>
            </span>
         </div>
       </template>
    </a-table>
  </div>
  </BasicModal>
</template>
<script setup lang="ts">
  import { BasicTable, useTable } from '/@/components/Table'
  import {BasicModal, useModalInner} from '/@/components/Modal'
  import { PageWrapper } from '/@/components/Page'

  import {
    DatePicker as ADatePicker, Select as ASelect, Input as AInput, Popover as APopover, Switch as ASwitch,
    Radio as ARadio, Pagination as APagination, Popconfirm as APopconfirm, Table as ATable,
    Checkbox as ACheckbox, message,Drawer as ADrawer
  } from "ant-design-vue"
  const ARangePicker=ADatePicker.RangePicker
  const ASelectOption=ASelect.Option
  const AInputSearch=AInput.Search
  const ARadioGroup=ARadio.Group
  const ARadioButton=ARadio.Button
  import {
    SortDescendingOutlined,
    SortAscendingOutlined,
    CaretDownFilled,
    FormOutlined,
    DeleteOutlined,
    CheckCircleOutlined,
    CloseCircleOutlined,
    SettingFilled,
    SyncOutlined,
    PicLeftOutlined,
    EditFilled,
    PieChartFilled,
    FilterFilled,CheckOutlined,EditOutlined,SearchOutlined,MacCommandOutlined,PrinterOutlined,UsbOutlined,UnlockTwoTone,LockTwoTone
  } from '@ant-design/icons-vue'

  import {
     manualWriteOff
  } from '/@/api/record/system/write-off'
  import { useAccvoucherHxStore } from '/@/store/modules/accvoucher-hexiao'
  import {onMounted, reactive, ref, unref, UnwrapRef} from "vue";
  import {useCompanyOperateStoreWidthOut} from "/@/store/modules/operate-company";
  import {changeDefaultDynamics, initDynamics,assemblyDynamicColumn} from "../data";

  import {cloneDeep} from "lodash-es";
  import {useMessage} from "/@/hooks/web/useMessage";
  import {
    getCurrentAccountName,
  } from "/@/api/task-api/tast-bus-api";
  import {useUserStore, useUserStoreWidthOut} from "/@/store/modules/user";
  import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";

  const { createConfirm,createWarningModal } = useMessage();

  const accvoucherStore = useAccvoucherHxStore()
  // 页面变量
  const columns = [
    {
      title: '序号',
      dataIndex: 'cdfine30',
      align: 'center',
      width: '6%',
    },
    {
      title: '日期',
      dataIndex: 'dbillDate',
      align: 'center',
      width: '12%',
    },
    {
      title: '字号',
      dataIndex: 'inoId',
      align: 'center',
      width: '6%',
    },
    {
      title: '摘要',
      dataIndex: 'cdigest',
      width: '20%',
      ellipsis: true,
    },
    {
      title: '借方金额',
      dataIndex: 'md',
      width: '10%',
      align: 'right',
    },
    {
      title: '未核销金额',
      dataIndex: 'remainMoney',
      width: '10%',
      align: 'right',
    },
    {
      title: '核销金额',
      dataIndex: 'hxMoney',
      width: '10%',
      align: 'right',
      slots: { customRender: 'hxMoney' },
    },
    {
      title: '操作',
      width: '10%',
      align: 'center',
      dataIndex: 'operation',
      slots: { customRender: 'operation' },
    },
  ];
  const columns2 = [
    {
      title: '序号',
      dataIndex: 'cdfine30',
      width: '6%',
      align: 'center',
    },
    {
      title: '日期',
      dataIndex: 'dbillDate',
      width: '12%',
      align: 'center',
    },
    {
      title: '字号',
      dataIndex: 'inoId',
      width: '6%',
      align: 'center',
    },
    {
      title: '摘要',
      dataIndex: 'cdigest',
      width: '20%',
      ellipsis: true,
    },
    {
      title: '贷方金额',
      dataIndex: 'mc',
      width: '10%',
      align: 'right',
    },
    {
      title: '未核销金额',
      align: 'right',
      dataIndex: 'remainMoney',
      width: '10%',
    },
    {
      title: '核销金额',
      dataIndex: 'hxMoney',
      width: '10%',
      align: 'right',
      slots: { customRender: 'hxMoney' },
    },
    {
      title: '操作',
      width: '10%',
      align: 'center',
      dataIndex: 'operation',
      slots: { customRender: 'operation' },
    },
  ];
  interface DataItem {
    cdfine30: string;
    dbillDate: string;
    inoId: string;
    cdigest: string;

    custname: string;
    mc: string;
    md: string;
    remainMoney: string;
  }
  const data: DataItem[] = [];

  const dwName = ref('');
  const jmoney = ref('');
  const dmoney = ref('');

  const jlist = ref(data);
  const dlist = ref(data);
  const list = ref([]);
  const editableData: UnwrapRef<Record<string, DataItem>> = reactive({});

  const edit = (key: string) => {
    editableData[key] = cloneDeep(jlist.value.filter(item => key === item.key)[0]);
  };
  const save = (key: string) => {
    //验证金额是否符合规定
    // 核销金额不能超过自身剩余核销金额
    if(editableData[key].hxMoney > editableData[key].remainMoney){
      message.error("核销金额不能超过剩余核销金额！")
      return
    }
    // 本方向总和不能超过最小方向总和
    let maxMoney
    if(jmoney > dmoney){
      maxMoney = dmoney;
    }else{
      maxMoney = jmoney;
    }
    if(editableData[key].hxMoney > maxMoney){
      message.error("核销金额不能有误请重新输入！")
      return
    }
    Object.assign(jlist.value.filter(item => key === item.key)[0], editableData[key]);
    delete editableData[key];
  };
  const cancel2 = (key: string) => {
    delete editableData[key];
  };


  const editableData2: UnwrapRef<Record<string, DataItem>> = reactive({});

  const edit2 = (key: string) => {
    editableData2[key] = cloneDeep(dlist.value.filter(item => key === item.key)[0]);
  };
  const save2 = (key: string) => {
    //验证金额是否符合规定
    // 核销金额不能超过自身剩余核销金额
    if(editableData2[key].hxMoney > editableData2[key].remainMoney){
      message.error("核销金额不能超过剩余核销金额！")
      return
    }
    // 本方向总和不能超过最小方向总和
    let maxMoney
    if(jmoney > dmoney){
      maxMoney = dmoney;
    }else{
      maxMoney = jmoney;
    }
    if(editableData2[key].hxMoney > maxMoney){
      message.error("核销金额不能有误请重新输入！")
      return
    }
    Object.assign(dlist.value.filter(item => key === item.key)[0], editableData2[key]);
    delete editableData2[key];
  };
  const cancel3 = (key: string) => {
    delete editableData2[key];
  };

  const userStore = useUserStore();
  const defaultPage = ref(true) // 默认为独立账套
  const visible = ref(false);
  // 表格参数
  const loadMark = ref(false)

  const defaultDbName = useCompanyOperateStoreWidthOut().getTenentName
  const manipulateDbName = ref(getCurrentAccountName(true))

  const formItems: any = ref({})
  let changeBeforeModel: any = {}
  const [register, {closeModal, setModalProps}] = useModalInner((d) => {
    // 方式2
    changeBeforeModel = JSON.parse(JSON.stringify(formItems.value))
    dwName.value = d.data1[0].custname;
    let jm  = 0.00
    let dm  = 0.00

    d.data1.forEach(v=>{
      jm = jm + parseFloat(v.md)
    })
    d.data2.forEach(v=>{
      dm = dm + parseFloat(v.mc)
    })
    jmoney.value = money(jm);
    dmoney.value = money(dm);
    //请求后台自动核销
     manuWriteOff({
      createCode: '1',
      tenantId : ref(getCurrentAccountName(true)).value,
      jlist: JSON.stringify(d.data1),
      dlist: JSON.stringify(d.data2)
    })
    setModalProps({minHeight: 400});
  })

  const manuWriteOff = async (data)=>{
    let res = await useRouteApi(manualWriteOff, {schemaName: manipulateDbName.value})(data)
    console.log(res)
    if (res != null) {
      jlist.value = res.jlist
      dlist.value = res.dlist
      list.value = res.list
    } else {
      message.error("自动分配错误")
    }
  }

  function money(data:any){
    let str = ""
    if(data){
      // 千分位保留2位小数
      var source = String(parseFloat(data).toFixed(2)).split("."); //按小数点分成2部分
      source[0] = source[0].replace( new RegExp('(\\d)(?=(\\d{3})+$)' , 'ig'), "$1,");//只将整数部分进行都好分割
      str = source.join("." );//再将小数部分合并进来
    }
    return data == ('0.00' || '0')?'': str;
  }

  function formeatData(val:any) {
    return val === "1"?'已分配': '未分配'
  }
  function moneyhxStatue(val:any) {
    return val === "1"?'已核销': '待核销'
  }

  const cancel = (e: MouseEvent) => {

  }

  // 已授权账套列表
  const accAuthList = ref([])
  // 账套授权信息
  const userAuthMap = ref([])

  const emit = defineEmits(['register', 'save'])
  const handleOk = async(data)=>{
    emit('save', unref({
      list:list.value,
    }))
    closeModal()
    return true
  }

</script>

<style src="../../../../../assets/styles/global-menu-index-block.less" lang="less" scoped></style><style lang='less' scoped ></style>
<style src="../../../../../assets/styles/global-menu-index.less" lang="less" scoped></style><style lang='less' scoped ></style>
<style scoped>/*针对当前页面特有样式*/
.a-table-font-size-16 :deep(td),
.a-table-font-size-16 :deep(th) {
  font-size: 16px !important;
  padding: 5px 8px !important;
}

.a-table-font-size-12 :deep(td),
.a-table-font-size-12 :deep(th) {
  font-size: 14px !important;
  padding: 2px 8px !important;
}

.a-table-font-size-16 :deep(td) .a-table-money-font-size {
  font-size: 14px !important;
  color: black;
}

.a-table-font-size-12 :deep(td) .a-table-money-font-size {
  font-size: 13px !important;
  color: black;
}

:deep(.a-table-font-arial) {
  font-family: Arial !important;
}

:deep(.pz-unit-change) {
  width: 50px;
  border-radius: 4px;
  text-align: center;
  font-weight: bold;
  color: black;
  pointer-events: none;
  border: none;
}

.pz-unit-change :deep(.ant-input) {
  font-weight: bold;
  color: black;
}
.app-container {
  padding:  0px;
  margin: 10px 10px 5px;
}
.account-picker-c {
  width: 40%;
  display: inline-block;
  font-size: 14px
}
</style>

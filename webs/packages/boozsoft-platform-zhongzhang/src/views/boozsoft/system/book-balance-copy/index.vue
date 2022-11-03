<template>
  <div class="app-container">
    <div class="app-container-head">
      <span style="margin-left: 48%"><b>发生额及余额表</b></span>
      <div
        class="ant-btn-group"
        data-v-a1ccd506=""
        style="float: right"
      >
        <button
          type="button"
          class="ant-btn"
          ant-click-animating-without-extra-node="false"
          @click="openAddPage()"
        ><span>查询</span></button>
        <button
          type="button"
          class="ant-btn"
          ant-click-animating-without-extra-node="false"
        ><span>发送</span></button>
        <button
          type="button"
          class="ant-btn"
        ><span>图表</span></button>
        <button
          type="button"
          class="ant-btn"
        ><span>导出</span></button>
        <button
          type="button"
          class="ant-btn"
        ><span>打印</span></button>
      </div>
    </div>
    <div class="app-container-neck" style="margin-top: 8px;">

      <div style="display: inline-block;float: left">
        <span style="font-size: 18px;color: black;font-weight: bold">期间：{{ thisInterval }}</span>
      </div>
      <div style="display: inline-block;float: left;margin-left: 1%"><span
        style="font-size: 18px;color: black;font-weight: bold">币种： 人民币</span></div>
      <div style="float: right; margin-left: 10px">
        <a-button class="ant-btn-me" @click="tableReload">
          <SyncOutlined :style="{ fontSize: '14px' }"/>
        </a-button>
        <a-popover placement="bottom">
          <template #content>
            <a-popconfirm
              ok-text="确定"
              cancel-text="放弃"
              @confirm="confirm"
              @cancel="cancel"
            >
              <template #icon><b>栏目设置</b><br></template>
              <template #title>
                <a-table bordered :data-source="dynamicColumnData" :columns="dynamicColumns"
                         childrenColumnName="children" :pagination="false">
                  <template #checkBox="{ text, record }">
                    <a-checkbox v-model:checked="record.check" :disabled="record.isFixed"/>
                  </template>
                  <template #widthInput="{ text, record }">
                    <div class="editable-cell">
                      <div v-if="editableData[record.key]" class="editable-cell-input-wrapper">
                        <a-input type="number" v-model:value="editableData[record.key].width"
                                 @pressEnter="save(record.key,record.min,record.max)" style="width: 80px"/>
                        <check-outlined class="editable-cell-icon-check"
                                        @click="save(record.key,record.min,record.max)"/>
                      </div>
                      <div v-else class="editable-cell-text-wrapper">
                        {{ text || ' ' }}
                        <edit-outlined class="editable-cell-icon" @click="edit(record.key)"/>
                        <span style="float: right;">{{ record.min + '~' + record.max }}</span>
                      </div>
                    </div>
                  </template>
                  <template #nameInput="{ text, record }">
                    <div class="editable-cell">
                      <div v-if="editableData[record.key]" class="editable-cell-input-wrapper">
                        <a-input type="text" v-model:value="editableData[record.key].nameNew"
                                 @pressEnter="saveName(record.key)" style="width: 100px"/>
                        <check-outlined class="editable-cell-icon-check"
                                        @click="saveName(record.key)"/>
                      </div>
                      <div v-else class="editable-cell-text-wrapper">
                        {{ text || ' ' }}
                        <edit-outlined class="editable-cell-icon" @click="edit(record.key)"/>
                      </div>
                    </div>
                  </template>
                  <template #alignRadio="{ text, record }">
                    <a-radio-group default-value="a" size="small" v-model:value="record.align"
                                   :disabled="record.align==''">
                      <a-radio-button value="left">
                        左
                      </a-radio-button>
                      <a-radio-button value="center">
                        中
                      </a-radio-button>
                      <a-radio-button value="right">
                        右
                      </a-radio-button>
                    </a-radio-group>
                  </template>
                </a-table>
              </template>
              <a-button style="width: 165px;border: none">栏目设置</a-button>
            </a-popconfirm>
            <br/>
            <span @click="showRulesSize = 'MAX'"
                  :style="{backgroundColor: (showRulesSize==='MAX')?'#e3e0e0':'white',cursor: 'pointer'}">&emsp;&emsp;<SortDescendingOutlined
              :style="{ fontSize: '14px' }"/>&emsp;大号字体&emsp;<CheckOutlined v-if="showRulesSize==='MAX'"
                                                                            :style="{ fontSize: '14px' }"/>&emsp;</span><br/>
            <span @click="showRulesSize = 'MIN'"
                  :style="{backgroundColor: (showRulesSize==='MIN')?'#e3e0e0':'white',cursor: 'pointer'}">&emsp;&emsp;<SortAscendingOutlined
              :style="{ fontSize: '14px' }"/>&emsp;小号字体&emsp;<CheckOutlined v-if="showRulesSize==='MIN'"
                                                                            :style="{ fontSize: '14px' }"/>&emsp;</span><br/>
          </template>
          <template #title>
            <b>设置表格字号</b>
          </template>
          <a-button>
            <SettingFilled :style="{ fontSize: '14px' }"/>
          </a-button>
        </a-popover>
        <a-popover placement="bottom">
          <template #content>
            <span @click="showRules = 'J'"
                  :style="{backgroundColor: (showRules==='J')?'#e3e0e0':'white',cursor: 'pointer'}">&emsp;<OrderedListOutlined
              :style="{ fontSize: '14px' }"/>&emsp;&emsp;&emsp;金额式&nbsp;<CheckOutlined v-if="showRules==='J'"
                                                                                       :style="{ fontSize: '14px' }"/>&emsp;</span><br/>
            <span @click="showRules = 'SJ'"
                  :style="{backgroundColor: (showRules==='SJ')?'#e3e0e0':'white',cursor: 'pointer'}">&emsp;<OrderedListOutlined
              :style="{ fontSize: '14px' }"/>&emsp;数量金额式&nbsp;<CheckOutlined v-if="showRules==='SJ'"
                                                                             :style="{ fontSize: '14px' }"/>&emsp;</span><br/>
            <span @click="showRules = 'WJ'"
                  :style="{backgroundColor: (showRules==='WJ')?'#e3e0e0':'white',cursor: 'pointer'}">&emsp;<OrderedListOutlined
              :style="{ fontSize: '14px' }"/>&emsp;外币金额式&nbsp;<CheckOutlined v-if="showRules==='WJ'"
                                                                             :style="{ fontSize: '14px' }"/>&emsp;</span><br/>
            <span @click="showRules = 'SWJ'"
                  :style="{backgroundColor: (showRules==='SWJ')?'#e3e0e0':'white',cursor: 'pointer'}">&emsp;<OrderedListOutlined
              :style="{ fontSize: '14px' }"/>&emsp;数量外币式&nbsp;<CheckOutlined v-if="showRules==='SWJ'"
                                                                             :style="{ fontSize: '14px' }"/>&emsp;</span>
          </template>
          <template #title>
            <b>表格显示样式</b>
          </template>
          <a-button>
            <PicLeftOutlined :style="{ fontSize: '14px' }"/>
          </a-button>
        </a-popover>
        <a-button>
          <PieChartFilled :style="{ fontSize: '14px' }"/>
        </a-button>

      </div>
      <div style="float: right; position: relative">
        <!-- 搜索 -->
        <a-input-search
          placeholder=""
          style="width: 200px; border-radius: 4px"
          @search="onSearch"
        />
      </div>
    </div>
    <Edit
      @save="saveData"
      @register="registerEditPage"
    />
    <a-table
      ref="TableInfo"
      :columns="initPageBasicDataByColumns(showRules,false,reloadMark)"
      :data-source="initPageBasicData()"
      :loading="loadMark"
      bordered
      size="middle"
      :class="showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
      :pagination="{ pageSize: 200,showSizeChanger: true, pageSizeOptions: ['200','500','1000'],showTotal: t => `总共${t}条数据` }"
      :scroll="{ x: 'calc(700px + 50%)', y  : tableHeight }"
    >
      <template #left1="{ record }"><span style="float: left">{{ record.num }}--左</span></template>
      <template #right1="{ record }"><span style="float: right">{{ record.num }}--右</span></template>
      <template #left2="{ record }"><span style="float: left">{{ record.name }}</span></template>
      <template #right2="{ record }"><span style="float: right">{{ record.name }}</span></template>

      <template #left3="{ record }"><span style="float: left">{{ record.unit }}</span></template>
      <template #right3="{ record }"><span style="float: right">{{ record.unit }}</span></template>
      <template #left4="{ record }"><span style="float: left">{{ record.type }}</span></template>
      <template #right4="{ record }"><span style="float: right">{{ record.type }}</span></template>

      <template #left="{ record }"><span style="float: left">{{ record.fjj }}</span></template>
      <template #right="{ record }"><span style="float: right">{{ record.fjj }}</span></template>
      <!--      <template #right-q1="{ record }"><span style="float: right">{{ record.fjj }}</span></template>
            <template #right-q2="{ record }"><span style="float: right">{{ record.fjj }}</span></template>
            <template #right-q3="{ record }"><span style="float: right">{{ record.fjj }}</span></template>
            <template #right-q4="{ record }"><span style="float: right">{{ record.fjj }}</span></template>
            <template #right-b1="{ record }"><span style="float: right">{{ record.fjj }}</span></template>
            <template #right-b2="{ record }"><span style="float: right">{{ record.fjj }}</span></template>
            <template #right-b3="{ record }"><span style="float: right">{{ record.fjj }}</span></template>
            <template #right-b4="{ record }"><span style="float: right">{{ record.fjj }}</span></template>
            <template #right-m1="{ record }"><span style="float: right">{{ record.fjj }}</span></template>
            <template #right-m2="{ record }"><span style="float: right">{{ record.fjj }}</span></template>
            <template #right-m3="{ record }"><span style="float: right">{{ record.fjj }}</span></template>
            <template #right-m4="{ record }"><span style="float: right">{{ record.fjj }}</span></template>-->
      <template #footer="{ record }">
        <a-table
          :show-header="false"
          :data-source="initPageBasicDataTotal()"
          :columns="initPageBasicDataByColumns(showRules,true,reloadMark)"
          :bordered="false"
          size="small"
          :pagination="false"
          :scroll="{ x: 'calc(700px + 50%)' }">
        </a-table>
      </template>
    </a-table>
<!--    <div style="text-align: center">
      <span style="font-size: 22px;font-weight: bold">科目明细账</span>
    </div>
    <span style="font-size: 18px;color: black;font-weight: bold">期间：{{ thisInterval }}&emsp;&emsp;&emsp;&emsp;科目: 1002-银行科目</span>
    <a-table v-if="showMingXi"
             :data-source="initPageBasicDataMX()"
             :columns="initPageBasicDataMXByColumns()"
             :bordered="false"
             size="small"
             :pagination="false"
    >
    </a-table>-->
  </div>

</template>
<script setup="props, {emit}" lang="ts">
import {
  initPageBasicDataByColumns,
  initPageBasicData,
  initPageBasicDataTotal,
  initDynamics,
  changeDefaultDynamics,
  initPageBasicDataMX,
  initPageBasicDataMXByColumns
} from '/@/api/record/system/book-balance'

import Edit from './popup/edit.vue'
import {useModal} from '/@/components/Modal'
import {PageWrapper} from '/@/components/Page'
import {computed, reactive, ref, onMounted, onBeforeUnmount, watch} from 'vue';
import {cloneDeep} from 'lodash-es';
import {
  DatePicker as ADatePicker,
  Select as ASelect,
  Input as AInput,
  Popover as APopover,
  Switch as ASwitch,
  Radio as ARadio,
  Upload as AUpload,
  Table as ATable,
  Checkbox as ACheckbox,
  Popconfirm as APopconfirm
} from "ant-design-vue"

const ARangePicker = ADatePicker.RangePicker
const ACheckboxGroup = ACheckbox.Group
const ASelectOption = ASelect.Option
const AInputSearch = AInput.Search
const ARadioGroup = ARadio.Group
const ARadioButton = ARadio.Button
import {
  SortDescendingOutlined,
  SortAscendingOutlined,
  CheckOutlined,
  OrderedListOutlined,
  FormOutlined,
  DeleteOutlined,
  SettingFilled,
  SyncOutlined,
  PicLeftOutlined,
  PieChartFilled, FilterFilled,
  EditOutlined
} from '@ant-design/icons-vue'

const tableHeight = (document.documentElement.clientHeight-390)+'px'
const [registerEditPage, {openModal: openEditPage}] = useModal()
const val = {
  id: '',
  qrCode: '',
  checkDate: '',
  checkType: '',
  checkNum: '',
  checkName: '',
  payName: '',
  payAccount: '',
  payeeName: '',
  purpose: '',
  amount: '',
  encryCode: '',
  voucherNumber: '',
  openOne: 0
}
const showMingXi = ref(true);
const loadMark = ref(true);
const reloadMark = ref(false);
const showRules = ref('J')
const showRulesSize = ref('MAX')
const thisInterval = ref('2020-01 ~ 2021-12')
const condClick = (data: any, index: any, e: any) => {
  console.log(index)
  if (e.target.cellIndex == 1) {
    openEditPage(true, {
      data: data
    })
  }
}
const openAddPage = () => {
  val.openOne = 0
  openEditPage(true, {
    data: val
  })
}
const TableInfo = ref(null)
onMounted(() => {
  let dom = TableInfo.value.$el.querySelectorAll(".ant-table-body")[0];
  dom.addEventListener(
    "scroll",
    () => {
      TableInfo.value.$el.querySelectorAll(
        ".ant-table-body"
      )[1].scrollLeft = dom.scrollLeft;
    },
    true
  );
});
onBeforeUnmount(() => {
  let dom = TableInfo.value.$el.querySelectorAll(".ant-table-body")[0];
  dom.removeEventListener(
    "scroll",
    () => {
      TableInfo.value.$el.querySelectorAll(
        ".ant-table-body"
      )[1].scrollLeft = dom.scrollLeft;
    },
    true
  );
});

const openEdit = (data: any) => {
  openEditPage(true, {
    data: data
  })
}
onMounted(() => {
  val.openOne = 1
  openEditPage(true, {
    data: val
  })
})
const del = async (data: any) => {
  alert('删除成功！')
}

async function saveData(data: any) {
  // 查询条件
  loadMark.value = false
}

async function onSearch(data: any) {
// 查询条件
}

const dynamicColumns = initDynamics().DEFAULT

const dynamicColumnData = ref(initDynamics().DATA)

const count = computed(() => dynamicColumnData.value.length + 1);

const editableData = reactive({});

const edit = (key: string) => {
  if (key.toString().indexOf('-') != -1) {
    let arr = key.split('-');
    let one = parseInt(arr[0])
    if (arr.length == 2) {
      editableData[key] = cloneDeep(dynamicColumnData.value[one].children.filter(item => key === item.key)[0]);
    } else {
      let two = parseInt(arr[1] - 1)
      editableData[key] = cloneDeep(dynamicColumnData.value[one].children[two].children.filter(item => key === item.key)[0]);
    }
  } else {
    editableData[key] = cloneDeep(dynamicColumnData.value.filter(item => key === item.key)[0]);
  }
}

const save = (key: string, min: number, max: number) => {
  editableData[key].width = editableData[key].width > max ? max : editableData[key].width < min ? min : editableData[key].width
  if (key.toString().indexOf('-') != -1) {
    let arr = key.split('-');
    let one = parseInt(arr[0])
    if (arr.length == 2) {
      Object.assign(dynamicColumnData.value[one].children.filter(item => key === item.key)[0], editableData[key]);
      Object.assign(dynamicColumnData.value[one].children.filter(item => key === item.key)[0], editableData[key]);
    } else {
      let two = parseInt(arr[1] - 1)
      Object.assign(dynamicColumnData.value[one].children[two].children.filter(item => key === item.key)[0], editableData[key]);
    }
  } else {
    Object.assign(dynamicColumnData.value.filter(item => key === item.key)[0], editableData[key]);
  }
  delete editableData[key];
}

const saveName = (key: string) => {
  if (key.toString().indexOf('-') != -1) {
    let arr = key.split('-');
    let one = parseInt(arr[0])
    if (arr.length == 2) {
      Object.assign(dynamicColumnData.value[one].children.filter(item => key === item.key)[0], editableData[key]);
      Object.assign(dynamicColumnData.value[one].children.filter(item => key === item.key)[0], editableData[key]);
    } else {
      let two = parseInt(arr[1] - 1)
      Object.assign(dynamicColumnData.value[one].children[two].children.filter(item => key === item.key)[0], editableData[key]);
    }
  } else {
    Object.assign(dynamicColumnData.value.filter(item => key === item.key)[0], editableData[key]);
  }
  delete editableData[key];
}

const confirm = (e: MouseEvent) => {
  // 调整数据库 列参数
  changeDefaultDynamics(dynamicColumnData.value)
  // 重新获取数据
  tableReload()
};

const cancel = (e: MouseEvent) => {
  // 恢复默认
  dynamicColumnData.value = []
  dynamicColumnData.value = initDynamics().DATA
};

const tableReload = ()=>{
  loadMark.value = true
  reloadMark.value = !reloadMark.value
  setTimeout(()=>{
    loadMark.value = false
  },1000)
}
</script>
<style src="../../../../assets/styles/global-menu-index-block.less" lang="less" scoped></style>
<style src="../../../../assets/styles/global-menu-index.less" lang="less" scoped></style>
<style lang="less" scoped>
.app-container {
  background: white;

  .ant-btn {
    border-color: #aaaaaa;
    font-size: 16px;
  }
  .ant-btn:hover{
    border-color: #2a7dc9;
  }
  .ant-input-affix-wrapper {
    border-color: #aaaaaa;
  }

}

:deep(.ant-table-scroll) {
  overflow: hidden;
}

:deep(.ant-table-header), :deep(ant-table-body) {
  .ant-table-thead th {
    text-align: center;
    border-right: 1px solid #f0f0f0;

    .ant-table-column-title {
      color: black !important;
      font-weight: bold !important;
    }
  }
}
:deep(th){
  background-color: #94bfbf !important;
}
:deep(td) {
  padding: 5px 8px !important;
  border: 1px solid #a7a7a7 !important;
  border-left: none !important;
  border-bottom: none !important;
  color: black !important;
}

:deep(.ant-table-footer) .ant-table-body {
  overflow: hidden !important;
}

:deep(.ant-table-footer) {
  padding: 0;

  tr {
    font-weight: bold !important;
    background-color: #94bfbf !important;
  }
}

.a-table-font-size-16 :deep(td), .a-table-font-size-16 :deep(th) {
  font-size: 16px !important;
}

.a-table-font-size-12 :deep(td), .a-table-font-size-12 :deep(th) {
  font-size: 12px !important;
}

</style>


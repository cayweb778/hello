<template>
  <div class="app-container">
    <div class="app-container-top">
      <div style="width: 100%;margin-top: 5px;height: 100px;">
        <div style="width: 40%;padding-top:20px;">
          <div>
            <AccountPicker theme="one" readonly="" @reloadTable="dynamicAdReload" style="margin-top: -5px;"/>
          </div>
          <div style="float: left;margin-left:6px;">
            <span style="font-size: 14px;">科目：</span>
            <a-select
              show-search
              placeholder="科目选择"
              option-filter-prop="children"
              style="width: 250px;display: inline-block;"
              @change="handleChangeMinKm"
              v-model:value="pageParameter.km"
            >
              <a-select-option v-for="d in kmList" :value="d.ccode">
                {{ d.ccode }}-{{ d.ccodeName }}
              </a-select-option>
              <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
            </a-select>
          </div>
          <div style="margin-top: 5px;float: right;margin-right: 250px;">
            本位币：<span style="font-weight: bold">{{ bwb }} </span>
            &emsp;&emsp;样式：<span style="color: black;font-weight: bold">{{ styleName }}</span>
          </div>
        </div>
        <div style="width: 60%;float: right;margin-top: -77px;">
          <span style="font-size: 24px;color: #0096c7;font-weight: bold;">科目辅助余额表</span>
          <div class="ant-btn-group" style="float: right;margin-top: 5px;">
            <button
              type="button"
              class="ant-btn ant-btn-me"
              ant-click-animating-without-extra-node="false"
              @click="openQueryPage()"
            ><span>查询</span></button>
            <a-popover placement="bottom">
              <template #content>
                <span class="group-btn-span">导出当前</span><br/>
                <span class="group-btn-span">导出全部</span>
              </template>
              <button
                type="button"
                class="ant-btn ant-btn-me"
                ant-click-animating-without-extra-node="false"
              ><span>导出</span></button></a-popover>
            <a-popover placement="bottom">
              <template #content>
                <span class="group-btn-span">打印当前</span><br/>
              </template>
              <button
                type="button"
                class="ant-btn ant-btn-me"
                ant-click-animating-without-extra-node="false"
              ><span>打印</span></button>
            </a-popover>
            <button type="button" class="ant-btn ant-btn-me" @click="closeCurrent(),router.push('/zhongZhang/home/welcome')"><span>退出</span></button>
          </div>
          <span style="display: block;color: black;font-size: 14px;margin-left: 10px;">期间：{{ time.strDate }} - {{ time.endDate }}
          <span style="float: right;margin-top: 5px;">
            <div>
              <!-- 搜索 -->
              <a-select v-model:value="pageParameter.searchConditon.requirement" style="width: 130px;"
                        class="special_select">
                <template v-for="item in searchConditonList">
                  <a-select-option v-if="item.ifShow == true" :value="item.dataIndex">
                    {{ item.title }}
                  </a-select-option>
                </template>
              </a-select>
              <a-input-search
                placeholder=""
                v-model:value="pageParameter.searchConditon.value"
                @search="pageSearch"
                style="width: 150px;border-radius: 4px"
              />&emsp;
              <a-button class="ant-btn" @click="closeFilterV(),pageReload()">
                <SyncOutlined :style="{ fontSize: '14px' }"/>
              </a-button>
              <a-popover class="ant-btn-default" placement="bottom">
                <template #content>
                  <a-popconfirm
                    ok-text="确定"
                    cancel-text="放弃"
                    @confirm="confirm"
                    @cancel="cancel">
                    <template #icon><b>栏目设置</b><br></template>
                    <template #title>
                      <a-table bordered :data-source="dynamicColumnData" :columns="dynamicColumns"
                               childrenColumnName="children" :pagination="false"
                               style="max-height: 650px;overflow-y: auto" :class="'a-table-font-size-12'">
                        <template #checkBox="{ text, record }">
                          <a-checkbox v-model:checked="record.check" :disabled="record.isFixed"/>
                        </template>
                        <template #widthInput="{ text, record }">
                          <div class="editable-cell">
                            <div v-if="editableData[record.key]" class="editable-cell-input-wrapper">
                              <a-input type="number" v-model:value="editableData[record.key].width"
                                       @pressEnter="save(record.key,record.min,record.max)"
                                       style="width: 80px"/>
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
                    <a-button style="width: 128px;margin-bottom: 2px">栏目设置</a-button>
                  </a-popconfirm>
                  <br/>
                  <span class="group-btn-span-special2" @click="pageParameter.showRulesSize = 'MAX'"
                        :style="pageParameter.showRulesSize==='MAX'?{backgroundColor: '#0096c7',color: 'white'}:''">
              <SortDescendingOutlined/>&nbsp;大号字体&ensp;<CheckOutlined
                    v-if="pageParameter.showRulesSize==='MAX'"/></span><br/>
                  <span class="group-btn-span-special2" @click="pageParameter.showRulesSize = 'MIN'"
                        :style="pageParameter.showRulesSize==='MIN'?{backgroundColor: '#0096c7',color: 'white'}:''">
              <SortAscendingOutlined/>&nbsp;小号字体&ensp;<CheckOutlined
                    v-if="pageParameter.showRulesSize==='MIN'"/></span>
                </template>
                <!--            <template #title>
                              <b>设置表格字号</b></template>-->
                <a-button>
                  <SettingFilled :style="{ fontSize: '14px' }"/>
                </a-button>
              </a-popover>

              <a-popover placement="bottom">
                <template #content>
            <span class="group-btn-span-special2" @click="onChangeSwitch('J')"
                  :style="pageParameter.queryMark=='J'?{backgroundColor: '#0096c7',color: 'white'}:''">
              <SortDescendingOutlined/>&nbsp;金额式&emsp;&ensp;<CheckOutlined
              v-if="pageParameter.queryMark=='J'"/></span><br/>
                  <span class="group-btn-span-special2" @click="onChangeSwitch('SJ')"
                        :style="pageParameter.queryMark=='SJ'?{backgroundColor: '#0096c7',color: 'white'}:''">
              <SortAscendingOutlined/>&nbsp;数量金额式<CheckOutlined
                    v-if="pageParameter.queryMark=='SJ'"/></span><br/>
                  <span class="group-btn-span-special2" @click="onChangeSwitch('WJ')"
                        :style="pageParameter.queryMark=='WJ'?{backgroundColor: '#0096c7',color: 'white'}:''">
              <SortDescendingOutlined/>&nbsp;外币金额式<CheckOutlined
                    v-if="pageParameter.queryMark=='WJ'"/></span><br/>
                  <span class="group-btn-span-special2" @click="onChangeSwitch('SWJ')"
                        :style="pageParameter.queryMark=='SWJ'?{backgroundColor: '#0096c7',color: 'white'}:''">
              <SortAscendingOutlined/>&nbsp;数量外币式<CheckOutlined
                    v-if="pageParameter.queryMark=='SWJ'"/></span><br/>
                </template>
                <a-button>
                  <PicLeftOutlined :style="{ fontSize: '14px' }"/>
                </a-button>
              </a-popover>
              <a-button @click="()=>{
                  if (!visible){ visible = true;reloadColumns()}
                  return false
                }">
                <FilterFilled :style="{ fontSize: '14px' }"/>
              </a-button>
            </div>
          </span>
        </span>
        </div>
      </div>
    </div>
    <div class="app-container-bottom">
      <PageWrapper dense content-full-height fixed-height content-class="flex">
        <BasicTable
          ref="tableRef"
          :class="
            pageParameter.showRulesSize == 'MAX' ? 'a-table-font-size-16' : 'a-table-font-size-12'
          "
          :scroll="{ x: totalColumnWidth, y: windowHeight }"
          size="small"
          @register="registerTable"
        >

          <template #dept="{ record }"><span style="float: left">{{ record.deptCode }}-{{ record.deptName }}</span></template>
          <template #psn="{ record }"><span style="float: left">{{ record.psnCode }}-{{ record.psnName }}</span></template>
          <template #cus="{ record }"><span style="float: left">{{ record.cusCode }}-{{ record.cusName }}</span></template>
          <template #sup="{ record }"><span style="float: left">{{ record.supCode }}-{{ record.supName }}</span></template>
          <template #pro="{ record }"><span style="float: left">{{ record.proCode }}-{{ record.proName }}</span></template>
          <template #unitMeasurement="{ record }">{{ record.unitMeasurement }}</template>
          <template #foreignCurrency="{ record }">{{ record.foreignCurrency }}</template>

          <template #qcMd="{ record }"><span style="float: right">{{ moneyformat(record.qcMd) }}</span></template>
          <template #qcMc="{ record }"><span style="float: right">{{ moneyformat(record.qcMc) }}</span></template>
          <template #qcNum="{ record }">{{ numformat(record.qcNum) }}</template>
          <template #qcNfrat="{ record }"><span style="float: right">{{ moneyformat(record.qcNfrat) }}</span></template>

          <template #pzMd="{ record }"><span style="float: right">{{ moneyformat(record.pzMd) }}</span></template>
          <template #pzMc="{ record }"><span style="float: right">{{ moneyformat(record.pzMc) }}</span></template>
          <template #pzNum="{ record }">{{ numformat(record.pzNum) }}</template>
          <template #pzNfrat="{ record }"><span style="float: right">{{ moneyformat(record.pzNfrat) }}</span></template>

          <template #qmMd="{ record }"><span style="float: right">{{ moneyformat(record.qmMd) }}</span></template>
          <template #qmMc="{ record }"><span style="float: right">{{ moneyformat(record.qmMc) }}</span></template>
          <template #qmNum="{ record }">{{ numformat(record.qmNum) }}</template>
          <template #qmNfrat="{ record }"><span style="float: right">{{ moneyformat(record.qmNfrat) }}</span></template>

        </BasicTable>
        <Query @save="loadPage" @register="registerQueryPage" />
      </PageWrapper>
    </div>
  </div>
</template>
<script setup lang="ts">
import { BasicTable, useTable } from '/@/components/Table';
import { useModal } from '/@/components/Modal';
import { PageWrapper } from '/@/components/Page';
import UnitChange from '../department/UnitChange.vue';
import {
  DatePicker as ADatePicker,
  Select as ASelect,
  Input as AInput,
  Popover as APopover,
  Switch as ASwitch,
  Radio as ARadio,
  Upload as AUpload,
  Pagination as APagination,
  Popconfirm as APopconfirm,
  Table as ATable,
  Checkbox as ACheckbox,
  message,
  Drawer as ADrawer,
} from 'ant-design-vue';
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
  FilterFilled,
  CheckOutlined,
  EditOutlined,
  SearchOutlined,CaretDownOutlined
} from '@ant-design/icons-vue';
import { findDbLanMuList, saveLanMuList } from '/@/api/record/system/accvoucher';
//////////////////////////////// START /////////////////////////////////////////
import { initDynamics, assemblyDynamicColumn } from './data';
import { finByParameterAccuracy } from '/@/api/record/km_mingxi/data';
import { findAll } from '/@/api/record/duo_fuzhu/data';
import Query from '/@/views/boozsoft/system/duo_fuzhu/popup/query.vue';
//////////////////////////////// END /////////////////////////////////////////

import { onMounted, reactive, ref } from 'vue';
import moment from 'moment';
import { useCompanyOperateStoreWidthOut } from '/@/store/modules/operate-company';
import { cloneDeep } from 'lodash-es';
import { useMessage } from '/@/hooks/web/useMessage';
import { getThisIndexImg, getCurrentAccountName } from '/@/api/task-api/tast-bus-api';
import { useUserStoreWidthOut } from '/@/store/modules/user';
import router from '/@/router';
import { savelog } from '/@/api/record/log';
import { useRouteApi } from '/@/utils/boozsoft/datasource/datasourceUtil';
import { findByAccId } from '/@/api/record/system/account';
import { useRoute } from 'vue-router';
import { yueGlStore } from '/@/api/record/duo_fuzhu/yue_colunm';
import { findCusAll } from '/@/api/record/generalLedger/data';
import AccountPicker from '/@/boozsoft/components/AccountPicker/AccountPicker.vue';
import { useTabs } from '/@/hooks/web/useTabs';
const ARangePicker = ADatePicker.RangePicker;
const ASelectOption = ASelect.Option;
const AInputSearch = AInput.Search;
const ARadioGroup = ARadio.Group;
const ARadioButton = ARadio.Button;
const { createConfirm, createWarningModal } = useMessage();
// 全局常量
const glStore = yueGlStore();
const { closeCurrent } = useTabs();
// 页面变量
const pageParameter = reactive({
  thisAdInfo: {},
  total: 0,
  database: '',
  page: '',
  size: '',
  queryMark: 'J',
  showRulesSize: 'MIN',
  condition: {},
  searchConditon: {
    typr: '',
    value: '',
  },
  companyCode: '',
  companyName: '',
  reloadMark: false,
  numJd: '2', // 数量精度
  priceJd: '2', // 金额精度
  lvJd: '2', // 汇率精度
  moneyJd: '2', // 金额精度
  km: '',
  // 显示期间
  endDate: '',
  strDate: '',
  //显示未记账
  ishaveRjz: true,
  styleValue: '',
  cclass: '',
  pzType: [],
  fuzhu: '',
  map: '',
});

const styleName = ref<String>('金额式');
// 本位币
const bwb = ref<String>('');
// 会计科目
const kmList: any = ref([]);
// 部门
const deptList: any = ref([]);
//币种名称
const bzName = ref<String>('');
//显示未记账
const ibook = ref<boolean>(true);
const showStyle = ref([
  {
    name: '金额式',
    value: 'J',
  },
  {
    name: '数量金额式',
    value: 'SJ',
  },
  {
    name: '外币金额式',
    value: 'WJ',
  },
  {
    name: '数量外币式',
    value: 'SWJ',
  },
]);
const time = reactive({
  endDate: '',
  strDate: '',
});
const val = {
  openOne: 0,
};
const visible = ref(false);
const searchConditonList = ref([]);
const lanMuData = {
  accId: '',
  menuName: '辅助余额表',
  type: '',
  objects: '',
  username: useUserStoreWidthOut().getUserInfo.username,
};
// 表格参数
const loadMark = ref(false);
const tableSelectedRowKeys = ref([]);
const tableSelectedRowObjs = ref([]);
const windowWidth = document.documentElement.clientWidth - 70;
const windowHeight = document.documentElement.clientHeight - 350;
const totalColumnWidth = ref(0);
const dynamicTenantId = ref(getCurrentAccountName(true));
const accId = ref(getCurrentAccountName(false));

const tableRef = ref(null);
const userName = useUserStoreWidthOut().getUserInfo.username;
const CrudApi = {
  list: findAll,
  columns: JSON.parse(JSON.stringify(glStore.getColumns(pageParameter.queryMark))),
};

const setingColunm = ref([]);

// 组件实例区
const [
  registerTable,
  { reload, setTableData, setColumns, getColumns, getPaginationRef, setPagination },
] = useTable({
  api: useRouteApi(CrudApi.list, { schemaName: dynamicTenantId }),
  columns: CrudApi.columns,
  bordered: true,
  loading: loadMark.value,
  immediate: false,
  canResize: true,
  showIndexColumn: false, //显示序号列
  pagination: {
    pageSize: 200,
    showSizeChanger: true,
    pageSizeOptions: ['200', '500', '1000', '1500'],
    showTotal: (t) => `总共${t}条数据`,
  },
  searchInfo: pageParameter,
  tableSetting: {
    // 是否显示刷新按钮
    redo: true,
    // 是否显示尺寸调整按钮
    size: true,
    // 是否显示字段调整按钮
    setting: true,
    // 是否显示全屏按钮
    fullScreen: true,
  },
});
const route = useRoute();
const routemsg = ref(route.query);
const [registerQueryPage, { openModal: openQueryPageM }] = useModal();
const [registerReviewPage, { openModal: openReviewPageM }] = useModal();

onMounted(async () => {
  if (JSON.stringify(routemsg.value).length === 2) {
    val.openOne = 1;
    openQueryPageM(true, {
      data: val,
    });
    loadMark.value = true;
  } else {
    dynamicTenantId.value = routemsg.value.dynamicTenantId;
    pageParameter.ishaveRjz = routemsg.value.ishaveRjz;
    pageParameter.strDate = routemsg.value.strDate.replaceAll('-', '');
    pageParameter.endDate = routemsg.value.endDate.replaceAll('-', '');
    pageParameter.km = routemsg.value.km;
    pageParameter.bz = routemsg.value.bz;
    pageParameter.minDept = routemsg.value.minDept;
    pageParameter.styleValue = routemsg.value.styleValue;
    pageParameter.companyCode = routemsg.value.companyCode;
    pageParameter.companyName = routemsg.value.companyName;
    // 客户
    deptList.value = await useRouteApi(findCusAll, {
      schemaName: routemsg.value.dynamicTenantId,
    })('');
    // 用于显示
    time.strDate = routemsg.value.strDate.replaceAll('-', '.');
    time.endDate = routemsg.value.endDate.replaceAll('-', '.');
    resetDynamicColumnData();
  }
});
const filterOption = (input: string, option: any) => {
  return option.children[0].children.toLowerCase().indexOf(input.toLowerCase()) >= 0;
};
// +++++++++++++++++++++ 复写公司代码 +++++++++++++++++++++++++
const dynamicAdReload = async (obj) => {
  // 先获取部门组件查看是否存在部门信息
  pageParameter.page = getPaginationRef().current;
  pageParameter.size = getPaginationRef().pageSize;
  if (pageParameter.strDate !== '') {
    let res = await useRouteApi(findAll, { schemaName: obj.accId + '-' + obj.year })(
      pageParameter,
    );
    if (res != null && res.total > 0) {
      loadMark.value = true;
      setTableData([]); // 清空可能残留的数据
      setTableData(res.items);
      // 底部分页信息
      dynamicTenantId.value = obj.accId + '-' + obj.year;
      pageParameter.thisAdInfo = {};
      pageParameter.thisAdInfo = obj;
      pageParameter.total = res.total;
      setPagination({ total: res.total });
      deptList.value = await useRouteApi(findCusAll, { schemaName: obj.accId + '-' + obj.year })(
        '',
      );
    } else {
      createWarningModal({ title: '温馨提示', content: '暂无任何数据！' });
      pageParameter.thisAdInfo = {};
      pageParameter.total = -1;
    }
  }
  loadMark.value = false;
};

const openQueryPage = () => {
  val.openOne = 0;
  openQueryPageM(true, {
    data: val,
  });
};

// 页面函数区
const onSelectChange = (selectedRowKeys, obj) => {
  tableSelectedRowKeys.value = selectedRowKeys;
  tableSelectedRowObjs.value = obj;
};
const pageReload = () => {
  reload({
    searchInfo: pageParameter,
  });
};

const onChangeSwitch = async (v) => {
  // 动态列
  styleName.value = showStyle.value.filter((o) => o.value === v)[0].name;
  pageParameter.queryMark = v;
  resetDynamicColumnData();
};
const pageSearch = async () => {
  pageReload();
};

/*start栏目设置*/
const dynamicColumns = initDynamics().DEFAULT;
const dynamicColumnData = ref([]);
let dynamicColumnDataCopy = [];
const editableData = reactive({});

const confirm = (e: MouseEvent) => {
  // 询问
  createConfirm({
    iconType: 'warning',
    title: '栏目同步',
    content: '是否将刚才设置同步数据库!',
    onOk: async () => {
      // 调整数据库 列参数
      lanMuData.accId = dynamicTenantId.value;
      lanMuData.objects = JSON.stringify(
        filterModifyData(dynamicColumnData.value, dynamicColumnDataCopy),
      );
      if (lanMuData.objects == '[]') {
        createWarningModal({ content: '请先做修改后再进行确认同步数据库！' });
      } else {
        saveLanMuList(lanMuData).then((res) => {
          message.success('数据库同步成功！');
        });
        // 重新赋值
        dynamicColumnDataCopy = JSON.parse(JSON.stringify(dynamicColumnData.value));
      }
    },
  });
  // 重新获取数据
  reloadColumns();
};

function filterModifyData(lanMuList: any, copyList) {
  let a = lanMuList.filter((item) => {
    try {
      copyList.forEach((item2) => {
        if (item.key === item2.key && item.name == item2.name) {
          if (
            item.nameNew != item2.nameNew ||
            item.width != item2.width ||
            item.check != item2.check ||
            item.align != item2.align
          )
            throw new Error('ok');
        }
      });
      return false;
    } catch (e) {
      if (e.message == 'ok') {
        return true;
      } else {
        return false;
      }
    }
  });

  //对子节点处理  过滤有子节点并且变动 添加到a
  lanMuList.forEach((item, index) => {
    if (item.children) {
      let b = item.children.filter((item2) => {
        try {
          copyList[index].children.forEach((item3) => {
            if (item2.key === item3.key && item2.name == item3.name) {
              if (
                item2.nameNew != item3.nameNew ||
                item2.width != item3.width ||
                item2.check != item3.check ||
                item2.align != item3.align
              )
                throw new Error('ok');
            }
          });
          return false;
        } catch (e) {
          if (e.message == 'ok') {
            return true;
          } else {
            return false;
          }
        }
      });
      b.forEach((item) => {
        a.push(item);
      });
    }
  });
  return a;
}

const cancel = (e: MouseEvent) => {
  // 恢复默认
  dynamicColumnData.value = [];
  dynamicColumnData.value = dynamicColumnDataCopy;
};

function resetDynamicColumnData() {
  // 先从数据查询是否已经设置
  lanMuData.accId = dynamicTenantId.value;
  lanMuData.type = pageParameter.queryMark;
  findDbLanMuList(lanMuData).then((res) => {
    // 栏目列
    let dbList = res.items;
    if (dbList.length > 0) {
      let statiList = initDynamics()[pageParameter.queryMark];
      dbList = combineParameters(statiList, dbList);
      dynamicColumnData.value = dbList;
      dynamicColumnDataCopy = JSON.parse(JSON.stringify(dbList));
    } else {
      let statiList = initDynamics()[pageParameter.queryMark];
      dynamicColumnData.value = statiList;
      dynamicColumnDataCopy = JSON.parse(JSON.stringify(statiList));
    }
    // 表格列
    pageReload();
    reloadColumns();
  });
}

function combineParameters(staticList: any, dbList: any) {
  staticList.forEach((item) => {
    dbList.forEach((item2) => {
      if (item.key === item2.key && item.name === item2.name) {
        item.nameNew = item2.nameNew;
        item.width = parseInt(item2.width);
        item.check = item2.check == 'true';
        item.align = item2.align;
      } else {
        //对子节点处理
        if (item.children) {
          item.children.forEach((item3) => {
            if (item3.key === item2.key && item3.name === item2.name) {
              item3.nameNew = item2.nameNew;
              item3.width = parseInt(item2.width);
              item3.check = item2.check == 'true';
              item3.align = item2.align;
            }
          });
        }
      }
    });
  });
  return staticList;
}

const edit = (key: string) => {
  if (key.toString().indexOf('-') != -1) {
    let arr = key.split('-');
    let one = parseInt(arr[0]);
    if (arr.length == 2) {
      editableData[key] = cloneDeep(
        dynamicColumnData.value[one].children.filter((item) => key === item.key)[0],
      );
    } else {
      let two = parseInt(arr[1] - 1);
      editableData[key] = cloneDeep(
        dynamicColumnData.value[one].children[two].children.filter((item) => key === item.key)[0],
      );
    }
  } else {
    editableData[key] = cloneDeep(dynamicColumnData.value.filter((item) => key === item.key)[0]);
  }
};

const save = (key: string, min: number, max: number) => {
  editableData[key].width =
    editableData[key].width > max
      ? max
      : editableData[key].width < min
        ? min
        : editableData[key].width;
  if (key.toString().indexOf('-') != -1) {
    let arr = key.split('-');
    let one = parseInt(arr[0]);
    if (arr.length == 2) {
      Object.assign(
        dynamicColumnData.value[one].children.filter((item) => key === item.key)[0],
        editableData[key],
      );
      Object.assign(
        dynamicColumnData.value[one].children.filter((item) => key === item.key)[0],
        editableData[key],
      );
    } else {
      let two = parseInt(arr[1] - 1);
      Object.assign(
        dynamicColumnData.value[one].children[two].children.filter((item) => key === item.key)[0],
        editableData[key],
      );
    }
  } else {
    Object.assign(
      dynamicColumnData.value.filter((item) => key === item.key)[0],
      editableData[key],
    );
  }
  delete editableData[key];
};

const saveName = (key: string) => {
  if (key.toString().indexOf('-') != -1) {
    let arr = key.split('-');
    let one = parseInt(arr[0]);
    if (arr.length == 2) {
      Object.assign(
        dynamicColumnData.value[one].children.filter((item) => key === item.key)[0],
        editableData[key],
      );
      Object.assign(
        dynamicColumnData.value[one].children.filter((item) => key === item.key)[0],
        editableData[key],
      );
    } else {
      let two = parseInt(arr[1] - 1);
      Object.assign(
        dynamicColumnData.value[one].children[two].children.filter((item) => key === item.key)[0],
        editableData[key],
      );
    }
  } else {
    Object.assign(
      dynamicColumnData.value.filter((item) => key === item.key)[0],
      editableData[key],
    );
  }
  delete editableData[key];
};

const reloadColumns = () => {
  let a = [];
  a = getColumns();
  let newA = JSON.parse(JSON.stringify(glStore.getColumns(pageParameter.queryMark,setingColunm.value)));
  newA = assemblyDynamicColumn(dynamicColumnData.value, newA);
  setColumns(newA);
  initTableWidth(newA);
  //多表头 添加子节点筛选
  let seachList = [];
  newA.forEach((item) => {
    if (item.children) {
      item.children.forEach((item2) => {
        item2.title = item.title + item2.title;
        seachList.push(item2);
      });
    } else {
      seachList.push(item);
    }
  });
  searchConditonList.value = seachList;
};

function initTableWidth(thisCs) {
  let total = 60;
  thisCs.forEach((item) => {
    if (item.ifShow == null || item.ifShow) total += parseInt(item.width);
  });
  if (total > windowWidth) {
    let f = 0;
    if (visible.value) f = 260;
    totalColumnWidth.value = Number(windowWidth) - f;
    tableRef.value.$el.style.setProperty('width', windowWidth + 20 - f + 'px');
  } else {
    if (visible.value && windowWidth - 260 < total) total -= total - (windowWidth - 260);
    totalColumnWidth.value = total;
    tableRef.value.$el.style.setProperty('width', total + 20 + 'px');
  }
}
// 格式化  Tue Jun 08 2021 16:57:19 GMT+0800 (中国标准时间)
function timeformat(dateData) {
  let date = new Date(dateData);
  let y = date.getFullYear();
  let m = date.getMonth() + 1;
  m = m < 10 ? '0' + m : m;
  let d = date.getDate();
  d = d < 10 ? '0' + d : d;
  return y + '-' + m + '-' + d;
}
/*栏目设置end*/
const loadPage = async (data) => {
  console.log(data);
  setingColunm.value=data.map.fuzhu
  reloadColumns()
  accId.value = data.pageParameter.accId;
  dynamicTenantId.value = data.pageParameter.database;
  // 查询是否独立账套  是否独立账套（1）反之集团账套（0）
  const datainfo = await findByAccId(data.pageParameter.accId);
  bwb.value = datainfo.currencyName;
  // 参数规则
  let ParameteRule = await useRouteApi(finByParameterAccuracy, { schemaName: dynamicTenantId })(
    '',
  );
  if (ParameteRule.length > 0) {
    for (let i = 0; i < ParameteRule.length; i++) {
      if (ParameteRule[i].paraName === '汇率') {
        pageParameter.lvJd = ParameteRule[i].decimalPlaces;
      }
      if (ParameteRule[i].paraName === '单价') {
        pageParameter.priceJd = ParameteRule[i].decimalPlaces;
      }
      if (ParameteRule[i].paraName === '数量') {
        pageParameter.numJd = ParameteRule[i].decimalPlaces;
      }
      if (ParameteRule[i].paraName === '金额') {
        pageParameter.moneyJd = ParameteRule[i].decimalPlaces;
      }
    }
  }
  // 回显筛选条件
  loadMark.value = false;
  deptList.value = data.deptList;
  kmList.value = data.kmList;
  time.strDate = data.strDate.replaceAll('-', '.');
  time.endDate = data.endDate.replaceAll('-', '.');

  pageParameter.km = data.km;
  pageParameter.strDate = data.strDate.replaceAll('-', '');
  pageParameter.endDate = data.endDate.replaceAll('-', '');
  pageParameter.showRulesSize = data.fontSize;
  pageParameter.ishaveRjz = data.ishaveRjz;
  pageParameter.styleValue = data.styleValue;
  pageParameter.pzType = data.pzType;
  pageParameter.fuzhu = data.fuzhu.join(',');
  pageParameter.map =JSON.stringify(data.map);

  if (data.openOne == 1) {
    // 第一次初始化 + 条件查询
    resetDynamicColumnData();
  } else {
    pageParameter.searchConditon.value = '';
    pageReload();
  }
};
async function handleChangeMinKm() {
  const test = kmList.value.filter((km) => km.ccode === pageParameter.km);
  if (test.length > 0) {
    if (test.bnum === '1' && test.currency === '1') {
      pageParameter.queryMark = 'SWJ';
    } else if (test.bnum === '1') {
      pageParameter.queryMark = 'SJ';
    } else if (test.currency === '1') {
      pageParameter.queryMark = 'WJ';
    } else {
      pageParameter.queryMark = 'J';
    }
  }
  styleName.value = showStyle.value.filter((o) => o.value === pageParameter.queryMark)[0].name;
  pageReload();
}
// 金额格式化
function moneyformat(data: any) {
  let str = '';
  if (data) {
    // 千分位保留2位小数
    var source = String(data.toFixed(pageParameter.moneyJd)).split('.'); //按小数点分成2部分
    source[0] = source[0].replace(new RegExp('(\\d)(?=(\\d{3})+$)', 'ig'), '$1,'); //只将整数部分进行都好分割
    str = source.join('.'); //再将小数部分合并进来
  }
  return str;
}
// 数量格式化
function numformat(data: any) {
  let str = '';
  if (data) {
    if (0 === data) {
      str = '';
    } else {
      var source = String(data.toFixed(pageParameter.numJd)).split('.'); //按小数点分成2部分
      source[0] = source[0].replace(new RegExp('(\\d)(?=(\\d{3})+$)', 'ig'), '$1,'); //只将整数部分进行都好分割
      str = source.join('.');
    }
  }
  return str;
}
</script>
<style src="../../../../assets/styles/global-menu-index-block.less" lang="less" scoped></style>
<style src="../../../../assets/styles/global-menu-index.less" lang="less" scoped></style>
<style>
.ant-table-wrapper {
  padding: 0 !important;
  background: #f2f2f2 !important;
}
</style>
<style scoped>
.app-container-top{
  height: 90px;
}
:deep(.ant-calendar-picker-input.ant-input), :deep(.ant-select-single:not(.ant-select-customize-input) .ant-select-selector .ant-select-selection-search-input) {
  border: none;
  border-bottom: solid 1px rgb(191, 191, 191) !important;
  width: 100%;
  font-weight: bold;
}

.app-container-bottom {
:deep(.ant-table-thead th) {
  background-color: #d8d8d8 !important;
  font-weight: bold !important;
  border-left: 1px solid #d8d8d8 !important;
  border-bottom: 1px solid #d8d8d8 !important;
  border-top: 1px solid #d8d8d8 !important;
}
}

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
:deep(.table-month-striped) {
  background-color: honeydew;
}
:deep(.table-year-striped) {
  background-color: lightyellow;
}
:deep(.table-odd-striped) {
  background-color: #efedea;
}
</style>

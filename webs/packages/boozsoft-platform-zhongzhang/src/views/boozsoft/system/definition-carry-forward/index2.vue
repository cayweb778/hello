<template>
  <div class="app-container">
    <div class="app-container-top">
      <div class="app-container-head">
        <div class="container-head-title" style="padding-left: 46%; text-align: center">
          <b class="noneSpan">期间损益结转</b>
          <div style="font-size: 14px; text-align: center; margin-top: 20px">
            <span style="color: black; font-size: 14px"> 期间：{{ pageParameter.yearMonth }} </span>
          </div>
        </div>
        <div class="ant-btn-group">
          <button
            type="button"
            class="ant-btn"
            ant-click-animating-without-extra-node="false"
            @click="openQueryFrame(0)"
            ><span>条件查询</span></button
          >
          <button
            type="button"
            class="ant-btn"
            ant-click-animating-without-extra-node="false"
            @click="setAccVocher"
            ><span>生成凭证</span></button
          >
          <button type="button" class="ant-btn"><span>退出</span></button>
        </div>
      </div>
      <div class="app-container-neck">
        <div style="display: inline-flex; font-size: 14px; float: left; line-height: 32px">
          <AccountPicker v-if="showChome" readonly="" theme="one" />
        </div>
        <div style="margin-left: 5px">
          <a-button class="ant-btn-me" @click="pageReload">
            <SyncOutlined :style="{ fontSize: '14px' }" />
          </a-button>
          <a-popover placement="bottom">
            <a-button>
              <SettingFilled :style="{ fontSize: '14px' }" />
            </a-button>
          </a-popover>
          <a-popover placement="bottom">
            <a-button>
              <PicLeftOutlined :style="{ fontSize: '14px' }" />
            </a-button>
          </a-popover>

          <a-button>
            <EditFilled :style="{ fontSize: '14px' }" />
          </a-button>

          <a-button>
            <PieChartFilled :style="{ fontSize: '14px' }" />
          </a-button>
          <a-button>
            <FilterFilled :style="{ fontSize: '14px' }" />
          </a-button>
        </div>
        <div style="float: right; position: relative">
          <!-- 搜索 -->
          <a-select
            v-model:value="pageParameter.searchConditon.requirement"
            style="width: 120px"
            class="special_select"
          >
            <!--   .slice(1)       -->
            <template v-for="item in searchlist">
              <a-select-option :value="item.dataIndex">
                {{ item.title }}
              </a-select-option>
            </template>
          </a-select>
          <a-input-search
            placeholder=""
            v-model:value="pageParameter.searchConditon.value"
            @search="pageSearch"
            style="width: 200px; border-radius: 4px"
          />
        </div>
      </div>
    </div>
    <div class="app-container-bottom">
      <BasicTable
        v-if="tableShow"
        :class="'a-table-font-size-12'"
        @row-click="clearSelectedRowKeys()"
        @register="registerTable"
      >
        <template #chamd="{ record }">
          <span style="float: right" v-if="record.childrenflag == '0'">{{
            moneyformat(record.chamd)
          }}</span>
          <span style="float: right" v-if="record.childrenflag == '1'">{{
            moneyformat(record.chamd)
          }}</span>
        </template>
        <template #chamc="{ record }">
          <span style="float: right" v-if="record.childrenflag == '0'">{{
            moneyformat(record.chamc)
          }}</span>
          <span style="float: right" v-if="record.childrenflag == '1'">{{
            moneyformat(record.chamc)
          }}</span>
        </template>
      </BasicTable>
      <Query @save="loadPage" @register="registerQueryPage" />
      <!--      <PingzhengIndex @register="registerPingZhengModal"></PingzhengIndex>-->
    </div>
  </div>
</template>
<script setup="props, { emit }" lang="ts">
  import { BasicTable, useTable } from '/@/components/Table';
  import { onMounted, ref, reactive } from 'vue';
  import { getCurrentAccountName, getThisIndexImg } from '/@/api/task-api/tast-bus-api';
  import {
    SettingFilled,
    SyncOutlined,
    PicLeftOutlined,
    EditFilled,
    PieChartFilled,
    FilterFilled,
    UnorderedListOutlined,
  } from '@ant-design/icons-vue';

  import { Select as ASelect, Input as AInput, Popover as APopover } from 'ant-design-vue';
  import AccountPicker from '/@/boozsoft/components/AccountPicker/AccountPicker.vue';
  import { useModal } from '/@/components/Modal';
  import Query from '/@/views/boozsoft/system/definition-carry-forward/popup/query.vue';
  import { useRouteApi } from '/@/utils/boozsoft/datasource/datasourceUtil';
  import { findAll, setPingZhengSave, getLastDayOfMonth } from '/@/api/record/system/losses-gain';
  import { useMessage } from '/@/hooks/web/useMessage';

  const { createConfirm, createWarningModal, createMessage } = useMessage();
  const ASelectOption = ASelect.Option;
  const AInputSearch = AInput.Search;
  const showChome = ref(true);
  const tableShow = ref(false);
  const dynamicTenantId = ref(getCurrentAccountName(true));
  const searchlist = [
    {
      title: '凭证摘要',
      dataIndex: 'digest',
      ellipsis: true,
      align: 'left',
    },
    {
      title: '科目编码',
      dataIndex: 'ccode',
      ellipsis: true,
      align: 'left',
    },
    {
      title: '科目名称',
      dataIndex: 'ccodeName',
      ellipsis: true,
      align: 'left',
    },
    {
      title: '方向',
      dataIndex: 'bprogerty',
      ellipsis: true,
      width: 100,
    },
  ];
  const CrudApi: any = {
    list: findAll,
    columns: [
      {
        title: '凭证摘要',
        dataIndex: 'digest',
        ellipsis: true,
        align: 'left',
      },
      {
        title: '损益科目编码',
        dataIndex: 'ccode',
        ellipsis: true,
        align: 'left',
      },
      {
        title: '损益科目名称',
        dataIndex: 'ccodeName',
        ellipsis: true,
        align: 'left',
      },
      {
        title: '辅助项',
        dataIndex: 'fuzhuName',
        ellipsis: true,
        align: 'left',
      },
      {
        title: '方向',
        dataIndex: 'bprogerty',
        ellipsis: true,
        width: 100,
        format: function (value) {
          return value == '0' ? '贷' : '借';
        },
      },
      {
        title: '借方余额',
        dataIndex: 'chamd',
        ellipsis: true,
        align: 'right',
        slots: { customRender: 'chamd' },
      },
      {
        title: '贷方余额',
        dataIndex: 'chamc',
        ellipsis: true,
        align: 'right',
        slots: { customRender: 'chamc' },
        // format: function (value) {
        //   return value == '0' ? "贷" : ''
        // }
      },
      {
        title: '本年利润科目',
        dataIndex: 'profitCcode',
        ellipsis: true,
        align: 'left',
      },
    ],
  };
  const pageParameter = reactive({
    yearMonth: '', // 期间
    searchConditon: {
      value: '',
      requirement: 'ccode',
    },
    pzType: '',
    codeJici: '',
    codeType: '',
    liRunCode: '',
    lrCodeBprogerty: '',
    digest: '',
    bz: '',
    ishaveRjz: '',
    styleList: '',
    codeLevelFirst: '',
  });
  const rowSelection = {
    getCheckboxProps: (record) => ({
      disabled: record.lossCcode === null || record.childrenflag == '1',
      name: record.lossCcode,
    }),
  };
  // 这是示例组件
  const [
    registerTable,
    { reload, getSelectRows, getDataSource, clearSelectedRowKeys, setLoading },
  ] = useTable({
    api: useRouteApi(CrudApi.list, { schemaName: dynamicTenantId }),
    columns: CrudApi.columns,
    // showIndexColumn: false, //显示序号列
    isTreeTable: true,
    indexColumnProps: { width: 100, fixed: 'left' },
    searchInfo: pageParameter,
    rowSelection: { type: 'checkbox', getCheckboxProps: rowSelection.getCheckboxProps },
    pagination: {
      pageSize: 200,
      showSizeChanger: true,
      pageSizeOptions: ['50', '100', '200'],
      showTotal: (t) => `总共${t}条数据`,
    },
  });

  const [registerQueryPage, { openModal: openQueryPageM }] = useModal();
  const openVal = ref({
    openOne: 0,
    total: 0,
  });

  // 金额格式化
  function moneyformat(data: any) {
    let str = '';
    if (data) {
      // 千分位保留2位小数
      var source = String(data.toFixed(2)).split('.'); //按小数点分成2部分
      source[0] = source[0].replace(new RegExp('(\\d)(?=(\\d{3})+$)', 'ig'), '$1,'); //只将整数部分进行都好分割
      str = source.join('.'); //再将小数部分合并进来
    }
    return str;
  }
  onMounted(async () => {
    openQueryFrame(1);
  });
  const openQueryFrame = (i) => {
    openVal.value.openOne = i;
    openQueryPageM(true, {
      data: openVal.value,
      dynamicTenantId: dynamicTenantId.value,
    });
  };
  const loadPage = (condition) => {
    dynamicTenantId.value = condition.constant.tenantId;
    pageParameter.yearMonth = condition.variable.period;
    pageParameter.pzType = condition.variable.voucherType;
    pageParameter.codeJici = condition.variable.carryOverlevel; // 0末级，1一级
    pageParameter.liRunCode = condition.variable.yearProfitCode;
    pageParameter.digest = condition.variable.period.split('-')[1] + '月' + condition.digest;
    pageParameter.bz = '全部';
    pageParameter.ishaveRjz = condition.ishaveRjz;
    pageParameter.styleList = condition.styleList;
    pageParameter.lrCodeBprogerty = condition.lrCodeBprogerty;
    pageParameter.codeLevelFirst = condition.codeLevelFirst.split('-')[0];

    // 加载数据
    pageReload();
  };
  const pageReload = () => {
    tableShow.value = true;
    openVal.value.total = 1;
    reload({
      searchInfo: pageParameter,
    });
    // setTimeout(()=>{
    //   if(getDataSource().length==1){
    //     createConfirmPop('【'+pageParameter.yearMonth+'】期间损益结转没有余额')
    //   }
    // },200)
  };
  const pageSearch = () => {
    reload();
  };
  function createConfirmPop(text) {
    createConfirm({
      iconType: 'warning',
      title: '警告',
      content: text,
      onOk: async () => {},
    });
  }
  //********************************************** 生成凭证 ***************************************************
  import PingzhengIndex from '/@/components/pingzheng-fillin/index.vue';
  import { usePingZhengModal } from '/@/components/pingzheng-fillin/components/Modal/usePingzhengModal';
  import { finByMonthMaxInoId } from '/@/api/record/system/accvoucher';
  import { useUserStoreWidthOut } from '/@/store/modules/user';
  const pingzhengModalContainerRef: any = ref([]);
  const { registerPingZhengModal, openPingzhengModal } = usePingZhengModal(
    pingzhengModalContainerRef,
  );

  function randomString(length) {
    var str = '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ';
    var result = '';
    for (var i = length; i > 0; --i) result += str[Math.floor(Math.random() * str.length)];
    return result;
  }
  // 生成凭证
  const setAccVocher = async () => {
    if (getSelectRows().length === 0) {
      createConfirmPop('至少选择一条损益科目,进行期间损益结转！');
      return false;
    }
    setLoading(true)
    // 指定月最后一天
    let lastDay = await useRouteApi(getLastDayOfMonth, { schemaName: dynamicTenantId })(
      pageParameter.yearMonth,
    );
    // 已经加 1 了
    let maxInoId = await useRouteApi(finByMonthMaxInoId, { schemaName: dynamicTenantId })(
      pageParameter.yearMonth.split('-')[1],
    );
    let accvoucherUnique = randomString(20);
    const apiData: any = [];
    let lr_inid = 0;
    getSelectRows().forEach((v, index) => {
      if (v.yuemx !== null && v.yuemx.length > 0) {
        v.yuemx.forEach((a, index2) => {
          if (a.chamd !== 0 || a.chamc !== 0) {
            lr_inid = pageParameter.codeJici == '2' ? index + 1 : index2 + 1;
            let accvoucher = {
              uniqueCode: accvoucherUnique,
              csign: '记',
              iyear: pageParameter.yearMonth.split('-')[0],
              imonth: pageParameter.yearMonth.split('-')[1],
              iyperiod: pageParameter.yearMonth.replace('-', ''),
              cdigest: pageParameter.digest,
              dbillDate: lastDay,
              inoId: maxInoId,
              inid: pageParameter.codeJici == '2' ? index + 1 : index2 + 1,
              ccode: a.ccode,
              ccodeName: a.ccodeName,
              md: a.chamd,
              mc: a.chamc,
              cbill: useUserStoreWidthOut().getUserInfo.username,
              ifrag: '0',
              iperiod: pageParameter.yearMonth.split('-')[1],
              vouchUnCode: randomString(20),
              fuzhu: '',
            };
            apiData.push(accvoucher);
          }
        });
      }
      if (v.children !== null && v.children.length > 0) {
        v.children.forEach((a, index2) => {
          if (a.chamd !== 0 || a.chamc !== 0) {
            lr_inid = pageParameter.codeJici == '2' ? index + 1 : index2 + 1;
            let accvoucher = {
              uniqueCode: accvoucherUnique,
              csign: '记',
              iyear: pageParameter.yearMonth.split('-')[0],
              imonth: pageParameter.yearMonth.split('-')[1],
              iyperiod: pageParameter.yearMonth.replace('-', ''),
              cdigest: pageParameter.digest,
              dbillDate: lastDay,
              inoId: maxInoId,
              inid: pageParameter.codeJici == '2' ? index + 1 : index2 + 1,
              ccode: a.ccode,
              ccodeName: a.ccodeName,
              md: a.chamd,
              mc: a.chamc,
              cbill: useUserStoreWidthOut().getUserInfo.username,
              ifrag: '0',
              iperiod: pageParameter.yearMonth.split('-')[1],
              vouchUnCode: randomString(20),
              cdeptId: a.cdeptId,
              cpersonId: a.cpersonId,
              ccusId: a.ccusId,
              csupId: a.csupId,
              projectId: a.projectId,
              cdfine1: a.cdfine1,
              cdfine2: a.cdfine2,
              cdfine3: a.cdfine3,
              cdfine4: a.cdfine4,
              cdfine5: a.cdfine5,
              cdfine6: a.cdfine6,
              cdfine7: a.cdfine7,
              cdfine8: a.cdfine8,
              cdfine9: a.cdfine9,
              cdfine10: a.cdfine10,
              cdfine11: a.cdfine11,
              cdfine12: a.cdfine12,
              cdfine13: a.cdfine13,
              cdfine14: a.cdfine14,
              cdfine15: a.cdfine15,
              cdfine16: a.cdfine16,
              cdfine17: a.cdfine17,
              cdfine18: a.cdfine18,
              cdfine19: a.cdfine19,
              cdfine20: a.cdfine20,
              cdfine21: a.cdfine21,
              cdfine22: a.cdfine22,
              cdfine23: a.cdfine23,
              cdfine24: a.cdfine24,
              cdfine25: a.cdfine25,
              cdfine26: a.cdfine26,
              cdfine27: a.cdfine27,
              cdfine28: a.cdfine28,
              cdfine29: a.cdfine29,
              cdfine30: a.cdfine30,
            };
            apiData.push(accvoucher);
          }
        });
      }
      if (v.children2 !== null && v.children2.length > 0) {
        v.children2.forEach((a, index2) => {
          if (a.chamd !== 0 || a.chamc !== 0) {
            lr_inid = pageParameter.codeJici == '2' ? index + 1 : index2 + 1;
            let accvoucher = {
              uniqueCode: accvoucherUnique,
              csign: '记',
              iyear: pageParameter.yearMonth.split('-')[0],
              imonth: pageParameter.yearMonth.split('-')[1],
              iyperiod: pageParameter.yearMonth.replace('-', ''),
              cdigest: pageParameter.digest,
              dbillDate: lastDay,
              inoId: maxInoId,
              inid: pageParameter.codeJici == '2' ? index + 1 : index2 + 1,
              ccode: a.ccode,
              ccodeName: a.ccodeName,
              md: a.chamd,
              mc: a.chamc,
              cbill: useUserStoreWidthOut().getUserInfo.username,
              ifrag: '0',
              iperiod: pageParameter.yearMonth.split('-')[1],
              vouchUnCode: randomString(20),
              cdeptId: a.cdeptId,
              cpersonId: a.cpersonId,
              ccusId: a.ccusId,
              csupId: a.csupId,
              projectId: a.projectId,
              cdfine1: a.cdfine1,
              cdfine2: a.cdfine2,
              cdfine3: a.cdfine3,
              cdfine4: a.cdfine4,
              cdfine5: a.cdfine5,
              cdfine6: a.cdfine6,
              cdfine7: a.cdfine7,
              cdfine8: a.cdfine8,
              cdfine9: a.cdfine9,
              cdfine10: a.cdfine10,
              cdfine11: a.cdfine11,
              cdfine12: a.cdfine12,
              cdfine13: a.cdfine13,
              cdfine14: a.cdfine14,
              cdfine15: a.cdfine15,
              cdfine16: a.cdfine16,
              cdfine17: a.cdfine17,
              cdfine18: a.cdfine18,
              cdfine19: a.cdfine19,
              cdfine20: a.cdfine20,
              cdfine21: a.cdfine21,
              cdfine22: a.cdfine22,
              cdfine23: a.cdfine23,
              cdfine24: a.cdfine24,
              cdfine25: a.cdfine25,
              cdfine26: a.cdfine26,
              cdfine27: a.cdfine27,
              cdfine28: a.cdfine28,
              cdfine29: a.cdfine29,
              cdfine30: a.cdfine30,
            };
            apiData.push(accvoucher);
          }
        });
      }
    });
    const mdSum: any =
      apiData.length == 0 ? '0' : apiData.map((item) => item.md).reduce((n, m) => n + m);
    const mcSum: any =
      apiData.length == 0 ? '0' : apiData.map((item) => item.mc).reduce((n, m) => n + m);
    let lrmd: any = 0;
    let lrmc: any = 0;

    if (pageParameter.lrCodeBprogerty === '0') {
      lrmd = mcSum - mdSum;
    } else {
      lrmc = mdSum - mcSum;
    }
    let accvoucher = {
      uniqueCode: accvoucherUnique,
      csign: '记',
      iyear: pageParameter.yearMonth.split('-')[0],
      imonth: pageParameter.yearMonth.split('-')[1],
      iyperiod: pageParameter.yearMonth.replace('-', ''),
      cdigest: pageParameter.digest,
      dbillDate: lastDay,
      inoId: maxInoId,
      inid: lr_inid + 1,
      ccode: pageParameter.liRunCode.split('-')[0],
      ccodeName: pageParameter.liRunCode.split('-')[1],
      md: parseFloat(lrmd).toFixed(2),
      mc: parseFloat(lrmc).toFixed(2),
      cbill: useUserStoreWidthOut().getUserInfo.username,
      ifrag: '0',
      iperiod: pageParameter.yearMonth.split('-')[1],
      vouchUnCode: randomString(20),
    };
    apiData.push(accvoucher);
    await useRouteApi(setPingZhengSave, { schemaName: dynamicTenantId })(apiData).then((a) => {
      createConfirmPop('已成功生成' + pageParameter.yearMonth.split('-')[1] + '期间损益结转');
      pageReload();
    });
  };
</script>
<style src="../../../../assets/styles/global-menu-index.less" lang="less"></style>
<style lang="less" scoped="scoped">
  .app-container {
    padding: 0px;
    margin: 10px 10px 5px;
  }
  .a-table-font-size-12 :deep(td),
  .a-table-font-size-12 :deep(th) {
    font-size: 14px !important;
    padding: 2px 8px !important;
  }
</style>

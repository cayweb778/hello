function useDate(dbillDate) {
  return {
    getYear() {
      return dbillDate.split('-')[0];
    },
    getMonth() {
      return dbillDate.split('-')[1];
    },
    getDay() {
      return dbillDate.split('-')[2];
    },
  };
}
export function usePingZhengModelDataHelper() {
  const apiReference = {
    options: [
      // id
      ['uniqueCode', 'optionPzId'],
      // 凭证日期getFuZhuHeSuanDefine
      ['dbillDate', 'optionDate'],
      // 凭证号
      [
        'inoId',
        'optionInoId',
        (e) => {
          // viewModel.value.toInoIdText()
          return e;
        },
      ],
      // 凭证类型
      ['csign', 'optionType'],
      // 凭证状态
      ['ifrag', 'optionFlag'],
      // 凭证附单据数
      ['idoc', 'optionDanJuQuantity'],
      // 制单人
      ['cbill', 'optionZhiDanBy'],
      // 出纳人
      ['ccashier', 'optionChuNaBy'],
      // 出纳日期
      ['ccashierDate', 'optionChuNaDate'],
      // 审核人
      ['ccheck', 'optionCheckBy'],
      // 审核日期
      ['ccheckDate', 'optionCheckDate'],
      // 主管
      ['cdirector', 'optionZhuGuan'],
      // 记账人
      ['cbook', 'optionJiZhangBy'],
      // 记账日期
      ['ibookDate', 'optionJiZhangDate'],
      // 凭证编码
      // ['vouchUnCode', 'optionId2']
    ],
    rowData: [
      // 摘要
      ['cdigest', 'zhaiYao'],
      ['vouchUnCode', 'thisInoid'],
      // 会计科目号
      ['ccode', 'ccode'],
      // 会计科目名
      ['ccodeName', 'ccodeName'],
      // 借方金额
      ['md', 'md'],
      // 贷方金额
      ['mc', 'mc'],
      // 人员辅助核算
      ['cdeptId', 'fzDept'],
      // 人员辅助核算
      ['cpersonId', 'fzEmp'],
      ['coordId', 'fz'],
      // 客户辅助核算
      ['ccusId', 'fzCustom'],
      // 供应商辅助核算
      ['csupId', 'fzGys'],
      // 项目辅助核算
      ['projectId', 'fzItem'],
      // 项目大类辅助核算
      ['project_classId', 'fzItemClass'],
      // 外币币种
      ['foreignCurrency', 'currencyType'],
      // 外币汇率
      ['mdF', 'currencyExchangeRate'],
      // 原币贷方金额
      ['nfrat', 'currencyMoney'],
      // 计量单位
      ['unitMeasurement', 'slUnit'],
      // 单价
      ['cunitPrice', 'slPrice'],
      // 借方数量
      ['ndS', 'slJieMoney'],
      // 贷方数量
      ['ncS', 'slDaiMoney'],
      // 结算方式编码
      ['pjCsettle', 'pjCsettle'],
      // 票据号
      ['pjId', 'pjId'],
      // 票据日期
      ['pjDate', 'pjDate'],
      // 结算单位
      ['pjUnitName', 'pjUnitName'],
    ],
  };

  return {
    toOptionsApi(optionsData) {
      const apiData = {};
      function assignApiData(apiData, optionsData) {
        apiReference.options.forEach((it) => (apiData[it[0]] = optionsData[it[1]]));
      }

      assignApiData(apiData, optionsData);
      const { getYear, getMonth, getDay } = useDate(apiData['dbillDate']);
      apiData['iyear'] = getYear();
      apiData['imonth'] = getMonth();
      apiData['iperiod'] = getMonth();
      apiData['inoId'] = parseInt(apiData['inoId']);
      apiData['ifrag'] = 0;

      function is13Iperiod() {
        return getMonth() == '12' && getDay() == '30';
      }

      function is14Iperiod() {
        return getMonth() == '12' && getDay() == '31';
      }
      if (is13Iperiod()) {
        apiData['iperiod'] = '13-测试期间';
      } else if (is14Iperiod()) {
        apiData['iperiod'] = '14-测试期间';
      }
      apiData['iyperiod'] = getYear() + getMonth();
      return apiData;
    },
    toApi(modelData) {
      const apiData = {};
      apiReference.options.forEach((item) => (apiData[item[0]] = modelData[item[1]]));
      apiReference.rowData.forEach((item) => (apiData[item[0]] = modelData[item[1]]));
      return apiData;
    },

    toOptionsModel(apiDataRow) {
      const hello = {};
      apiReference.options.forEach((item) => {
        if (item[2] != null) {
          hello[item[1]] = item[2](apiDataRow[item[0]]);
        } else {
          hello[item[1]] = apiDataRow[item[0]];
        }
      });
      return hello;
    },
    toRowDataModel(apiDataRow) {
      const hello = {};
      apiReference.rowData.forEach((item) => (hello[item[1]] = apiDataRow[item[0]]));
      if(apiDataRow.nfratMd!=null){
        hello.currencyMoney=apiDataRow.nfratMd
      }else{
        hello.currencyMoney=apiDataRow.nfratMc

      }

      return hello;
    },
  };
}

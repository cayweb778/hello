import { nextTick, shallowRef } from 'vue';
import { getBizhongText, getShuliangText } from '/@/components/pingzheng-fillin/hooks/funs/abc';

export function getFuZhuXiangCorresField() {
  // 人员辅助核算
  const arr = [
    ['cdeptId', 'fzDept'],
    // 部门辅助核算
    ['cpersonId', 'fzEmp'],
    // 客户辅助核算
    ['ccusId', 'fzCustom'],
    // 供应商辅助核算
    ['csupId', 'fzGys'],
    // 项目大类辅助核算
    ['projectClassId', 'fzPrjectClass'],
    // 项目辅助核算
    ['projectId', 'fzPrject'],
    ['cdfine1', 'beiyong1'],
    ['cdfine2', 'beiyong2'],
    ['cdfine3', 'beiyong3'],
    ['cdfine4', 'beiyong4'],
    ['cdfine5', 'beiyong5'],
    ['cdfine6', 'beiyong6'],
    ['cdfine7', 'beiyong7'],
    ['cdfine8', 'beiyong8'],
    ['cdfine9', 'beiyong9'],
    ['cdfine10', 'beiyong10'],
    ['cdfine11', 'beiyong11'],
    ['cdfine12', 'beiyong12'],
    ['cdfine13', 'beiyong13'],
    ['cdfine14', 'beiyong14'],
    ['cdfine15', 'beiyong15'],
    ['cdfine16', 'beiyong16'],
    ['cdfine17', 'beiyong17'],
    ['cdfine18', 'beiyong18'],
    ['cdfine19', 'beiyong19'],
    ['cdfine20', 'beiyong20'],
    ['cdfine21', 'beiyong21'],
    ['cdfine22', 'beiyong22'],
    ['cdfine23', 'beiyong23'],
    ['cdfine24', 'beiyong24'],
    ['cdfine25', 'beiyong25'],
    ['cdfine26', 'beiyong26'],
    ['cdfine27', 'beiyong27'],
    ['cdfine28', 'beiyong28'],
    ['cdfine29', 'beiyong29'],
    ['cdfine30', 'beiyong30'],
  ];
  return arr;
}

export function createColumnDefine(key, { columns, resultMergeEvent, defineData }) {
  return {
    key,
    enable: false,
    columns,
    defineData,
    resultMergeEvent,
  };
}

export function shuLiangChange(row, shuLiangValue) {
  return function () {
    let { slNumber, slPrice } = shuLiangValue.value;
    let exchangeRate = row.huiLv.value.exchangeRate;
    row.huiLv.value.currencyMoney = 121212;
    exchangeRate = exchangeRate == null ? 1 : exchangeRate;
    slPrice = slPrice == null ? 0 : slPrice;
    slNumber = slNumber == null ? 0 : slNumber;
    const money = slNumber * slPrice;
    row.huiLv.value.currencyMoney = money;

    // 方向  1.借 0.贷
    const dir = row.kuaiJiKeMu.info.bprogerty;
    const m = money * exchangeRate;
    if (row.huiLv.value.currencyExchangeRate == null) {
      row.huiLv.value.currencyExchangeRate = 1;
    }

    row.huiLv.columnDatas.bizhong.text = getBizhongText(
      row.huiLv.value.currencyExchangeRate,
      parseFloat(m).toFixed(2),
    );
    // if(row.jieMoney.value=="0.00" &&row.daiMoney.value=="0.00"){
    if (row.jieMoney.value != '0.00' || row.daiMoney.value != '0.00') {
      return;
    }
    if (dir == '1') {
      // row.daiMoney.value="0.00"
      row.jieMoney.value = m;
    } else if (dir == '0') {
      // row.jieMoney.value="0.00"
      row.daiMoney.value = m;
    } else {
      throw new Error('格式错误');
    }
    // }
    // else if(row.jieMoney.value!="0.00"){
    //   row.jieMoney.value=money*exchangeRate
    // }else if(row.daiMoney.value!="0.00"){
    //   row.daiMoney.value=money*exchangeRate
    // }
    // else{
    //   throw new Error('格式错误')
    // }
  };
}

export function guid() {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
    const r = (Math.random() * 16) | 0,
      v = c == 'x' ? r : (r & 0x3) | 0x8;
    return v.toString(16);
  });
}

export function createRowDefine({
  requireModels,
  hover,
  showSetting,
  rowData,
  rowIndex,
  thisInid,
}) {
  // :guid().replace('-','')
  const obj = {
    rowIndex,
    thisInid,
    requireModels,
    hover,
    showSettingDelay: 3,
    showSetting,
    checkbox: null,
    target: shallowRef({
      value: null,
    }),

    rowData,
  };
  return obj;
}

export function getRequireFuZhuHeSuan(kuaiJiKeMuInfo) {
  const { bdept, bperson, bcus, bsup, cassItem, bitem } = kuaiJiKeMuInfo;
  const defFuZhuHeSuans = [
    {
      custom: true,
      key: 'beiyong1',
      enable: kuaiJiKeMuInfo.cdfine1 == '1',
      label: '',
    },
    {
      custom: true,
      key: 'beiyong2',
      enable: kuaiJiKeMuInfo.cdfine2 == '1',
      label: '',
    },
    {
      custom: true,
      key: 'beiyong3',
      enable: kuaiJiKeMuInfo.cdfine3 == '1',
      label: '',
    },
    {
      custom: true,
      key: 'beiyong4',
      enable: kuaiJiKeMuInfo.cdfine4 == '1',
      label: '',
    },
    {
      custom: true,
      key: 'beiyong5',
      enable: kuaiJiKeMuInfo.cdfine5 == '1',
      label: '',
    },
    {
      custom: true,
      key: 'beiyong6',
      enable: kuaiJiKeMuInfo.cdfine6 == '1',
      label: '',
    },
    {
      custom: true,
      key: 'beiyong7',
      enable: kuaiJiKeMuInfo.cdfine7 == '1',
      label: '',
    },
    {
      custom: true,
      key: 'beiyong8',
      enable: kuaiJiKeMuInfo.cdfine8 == '1',
      label: '',
    },
    {
      custom: true,
      key: 'beiyong9',
      enable: kuaiJiKeMuInfo.cdfine9 == '1',
      label: '',
    },
    {
      custom: true,
      key: 'beiyong10',
      enable: kuaiJiKeMuInfo.cdfine10 == '1',
      label: '',
    },
    {
      custom: true,
      key: 'beiyong11',
      enable: kuaiJiKeMuInfo.cdfine11 == '1',
      label: '',
    },
    {
      custom: true,
      key: 'beiyong12',
      enable: kuaiJiKeMuInfo.cdfine12 == '1',
      label: '',
    },
    {
      custom: true,
      key: 'beiyong13',
      enable: kuaiJiKeMuInfo.cdfine13 == '1',
      label: '',
    },
    {
      custom: true,
      key: 'beiyong14',
      enable: kuaiJiKeMuInfo.cdfine14 == '1',
      label: '',
    },
    {
      custom: true,
      key: 'beiyong15',
      enable: kuaiJiKeMuInfo.cdfine15 == '1',
      label: '',
    },
    {
      custom: true,
      key: 'beiyong16',
      enable: kuaiJiKeMuInfo.cdfine16 == '1',
      label: '',
    },
    {
      custom: true,
      key: 'beiyong17',
      enable: kuaiJiKeMuInfo.cdfine17 == '1',
      label: '',
    },
    {
      custom: true,
      key: 'beiyong18',
      enable: kuaiJiKeMuInfo.cdfine18 == '1',
      label: '',
    },
    {
      custom: true,
      key: 'beiyong19',
      enable: kuaiJiKeMuInfo.cdfine19 == '1',
      label: '',
    },
    {
      custom: true,
      key: 'beiyong20',
      enable: kuaiJiKeMuInfo.cdfine20 == '1',
      label: '',
    },
    {
      custom: true,
      key: 'beiyong21',
      enable: kuaiJiKeMuInfo.cdfine21 == '1',
      label: '',
    },
    {
      custom: true,
      key: 'beiyong22',
      enable: kuaiJiKeMuInfo.cdfine22 == '1',
      label: '',
    },
    {
      custom: true,
      key: 'beiyong23',
      enable: kuaiJiKeMuInfo.cdfine23 == '1',
      label: '',
    },
    {
      custom: true,
      key: 'beiyong24',
      enable: kuaiJiKeMuInfo.cdfine24 == '1',
      label: '',
    },
    {
      custom: true,
      key: 'beiyong25',
      enable: kuaiJiKeMuInfo.cdfine25 == '1',
      label: '',
    },
    {
      custom: true,
      key: 'beiyong26',
      enable: kuaiJiKeMuInfo.cdfine26 == '1',
      label: '',
    },
    {
      custom: true,
      key: 'beiyong27',
      enable: kuaiJiKeMuInfo.cdfine27 == '1',
      label: '',
    },
    {
      custom: true,
      key: 'beiyong29',
      enable: kuaiJiKeMuInfo.cdfine29 == '1',
      label: '',
    },
    {
      custom: true,
      key: 'beiyong30',
      enable: kuaiJiKeMuInfo.cdfine30 == '1',
      label: '',
    },
    {
      custom: true,
      key: 'beiyong28',
      enable: kuaiJiKeMuInfo.cdfine28 == '1',
      label: '',
    },
  ].filter((item) => item.enable);
  const arr = [];

  if (bdept == '1') {
    arr.push({ key: 'fzDept', label: '部门' });
    // arr.push({key: 'fzDept', label: '部门', value: abc('fzDept', row)});
  }
  if (bperson == '1') {
    arr.push({ key: 'fzEmp', label: '人员' });
    // , value: abc('fzEmp', row)});
  }
  if (bcus == '1') {
    arr.push({ key: 'fzCustom', label: '客户' });
  }
  if (bsup == '1') {
    arr.push({ key: 'fzGys', label: '供应商' });
  }

  if (cassItem != null) {
    arr.push({ key: 'fzItemClass', label: '项目大类' });
  }
  if (bitem == '1') {
    arr.push({ key: 'fzItem', label: '项目' });
  }
  //
  arr.push(...defFuZhuHeSuans);
  return arr;
  // return [...new Set(Object.values({
  //   bperson,
  //   bcus,
  //   bsup,
  //   bdept,
  //   bitem,
  //   cassItem,
  // }))
  // ].length>0
}

export function getBaseRowDefine(row, rowIndex, thisInid) {
  return createRowDefine({
    thisInid,
    requireModels: ['zhaiYao', 'kuaiJiKeMu', 'jieMoney', 'daiMoney'],
    showSetting: false,
    hover: false,
    rowData: row,
    rowIndex,
  });
}

export function getAllRequireModels(value) {
  const requireModelsxxx = [];
  // 数量金额
  if (value.bnum == '1') {
    requireModelsxxx.push('shuliangjine');
  }
  // 外币
  if (value.currency == '1') {
    requireModelsxxx.push('huiLv');
  }
  return requireModelsxxx;
}

export function useRequireModels(kuaiJiKeMuInfo) {
  const fun = {
    getRequireModels() {
      const base = ['zhaiYao', 'kuaiJiKeMu', 'jieMoney', 'daiMoney'];
      base.push(...getAllRequireModels(kuaiJiKeMuInfo));
      return base;
    },
    resetRequireModels(rowDefine) {
      rowDefine.requireModels.splice(0, rowDefine.requireModels.length);
      rowDefine.requireModels.push(...fun.getRequireModels());
    },
  };
  return fun;
}

export function useFuZhuHeSuan(kuaiJiKeMuInfo) {
  const fun = {
    resetFuZhuHeSuan(row, rowIndex) {
      const fuZhuHeSuan = fun.getFuZhuXiang();
      if (Object.keys.length > 0) {
        fuZhuHeSuan.rowIndex = rowIndex;

        function abc(key, row) {
          const target = row.fuZhuHeSuan.value.filter((item) => item.key == key);
          if (target.length != 0) {
            return target[0].value;
          } else {
            return { value: '' };
          }
        }

        fuZhuHeSuan.value = fuZhuHeSuan.value.map((it) => ({ ...it, value: abc(it.key, row) }));
      }
      Object.assign(row.fuZhuHeSuan, fuZhuHeSuan);
    },
    getFuZhuXiang() {
      const fuZhuHeSuan = {};
      // 结算方式
      if (kuaiJiKeMuInfo.bbank == '1') {
        fuZhuHeSuan.enable = true;

        if (fuZhuHeSuan.jieSuanMode == null) {
          fuZhuHeSuan.jieSuanMode = '';
        }
        // requireModels.push('fuZhuHeSuan');
      } else {
        fuZhuHeSuan.jieSuanMode = null;
      }

      // 辅助核算
      const requireFuZhuHeSuan = getRequireFuZhuHeSuan(kuaiJiKeMuInfo);
      if (requireFuZhuHeSuan.length > 0) {
        // requireModels.indexOf('fuZhuHeSuan') == -1 && requireModels.push('fuZhuHeSuan');

        fuZhuHeSuan.value = requireFuZhuHeSuan;
        fuZhuHeSuan.enable = true;
      } else {
        fuZhuHeSuan.value = [];
      }
      return fuZhuHeSuan;
    },
  };
  return fun;
}
export function useRequireModels2(requireModels) {
  return {
    getNextRef() {
      if (this.hasFuZhuHeSuan()) {
        // rowDefines.value[rowIndex].target.fuZhuHeSuan.focus();
        return 'fuZhuHeSuan';
      } else if (this.hasShuliang()) {
        return 'shuliang';
      } else if (this.hasHuiLv()) {
        return 'huiLv';
      } else if (this.hasJieMoney()) {
        return 'jieMoney';
      }
    },
    hasFuZhuHeSuan() {
      return requireModels.indexOf('fuZhuHeSuan') != -1;
    },
    hasShuliang() {
      return requireModels.indexOf('shuliang') != -1;
    },
    hasHuiLv() {
      return requireModels.indexOf('huiLv') != -1;
    },
    hasJieMoney() {
      return requireModels.indexOf('jieMoney') != -1;
    },
    FUZHUHESUAN: 'fuZhuHeSuan',
    SHULIANG: 'shuliang',
    HUILV: 'huiLv',
    JIEMONEY: 'jieMoney',
  };
}
export function useKuaiJiKeMuData(row, kuaiJiKeMuInfo) {
  const fun = {
    ...{
      setBiZhong(row, value) {
        row.huiLv.value.currencyType = value;
        row.huiLv.columnDatas.bizhong.text = getBizhongText('', '');
      },
      setUnit(row, value) {
        row.shuliangjine.value.slUnit = value;
        row.shuliangjine.columnDatas.jiliangdanwei.text = getShuliangText('', '');
      },
      mergeUnitAndBiZhongData() {
        if (kuaiJiKeMuInfo == null) {
          console.log(kuaiJiKeMuInfo);
          return;
        }
        const requireModels = useRequireModels(kuaiJiKeMuInfo).getRequireModels();
        // 数量金额
        if (requireModels.indexOf('shuliangjine') != -1) {
          // 单位
          fun.setUnit(row, kuaiJiKeMuInfo.menterage);
        }

        // 外币
        if (requireModels.indexOf('huiLv') != -1) {
          // 币种
          fun.setBiZhong(row, kuaiJiKeMuInfo.currencyType);
        }
      },
    },
  };
  return fun;
}

export function createKuaiJiKeMuInputEvents(row, target, rowIndex, rowDefines) {
  return {
    up: () => {
      if (rowIndex == 0) {
        return;
      }

      rowDefines.value[rowIndex - 1].target.kuaiJiKeMu.focus();
    },
    left: () => {
      rowDefines.value[rowIndex].target.zhaiYao.focus();
    },
    right: () => {
      const { FUZHUHESUAN, SHULIANG, HUILV, JIEMONEY, getNextRef } = useRequireModels2(
        rowDefines.value[rowIndex].requireModels,
      );
      switch (getNextRef()) {
        case FUZHUHESUAN:
          // rowDefines.value[rowIndex].target.fuZhuHeSuan.focus();
          break;
        case SHULIANG:
          rowDefines.value[rowIndex].target.shuliang.focus();
          break;
        case HUILV:
          rowDefines.value[rowIndex].target.huiLv.focus();
          break;
        case JIEMONEY:
          rowDefines.value[rowIndex].target.jieMoney.focus();
          break;
      }
    },
    down: () => {
      if (rowDefines.value[rowIndex + 1] != null) {
        rowDefines.value[rowIndex + 1].target.kuaiJiKeMu.focus();
      }
    },
    next: () => {
      target.right();
    },
    change: (kuaiJiKeMuInfo, fun) => {
      const { mergeUnitAndBiZhongData } = useKuaiJiKeMuData(row, kuaiJiKeMuInfo);
      const { resetRequireModels } = useRequireModels(kuaiJiKeMuInfo);
      const { resetFuZhuHeSuan } = useFuZhuHeSuan(kuaiJiKeMuInfo);

      // 重新设置辅助核算
      resetFuZhuHeSuan(row, rowIndex);
      // 重新设置需要的列
      resetRequireModels(rowDefines.value[rowIndex]);
      // 合并单位和币种信息
      mergeUnitAndBiZhongData();
      nextTick(() => {
        if (row.fuZhuHeSuan.value.length > 0) {
        } else if (rowDefines.value[rowIndex].requireModels.indexOf('shuliangjine') != -1) {
          rowDefines.value[rowIndex].target.jiLiangDanWei.focus();
        } else if (rowDefines.value[rowIndex].requireModels.indexOf('huiLv') != -1) {
          rowDefines.value[rowIndex].target.huiLv.focus();
        } else {
          if(rowDefines.value[rowIndex].rowData.jieMoney.value!='0.00' ||rowDefines.value[rowIndex].rowData.daiMoney.value!='0.00'){
            rowDefines.value[rowIndex+1].target.zhaiYao.focus();
          }else{
            rowDefines.value[rowIndex].target.jieMoney.focus();
          }

        }
      });
    },
    rowIndex: rowIndex,
  };
}

export function getAllRequireModels2(kuaiJiKeMuInfoArr) {
  const ccc = kuaiJiKeMuInfoArr.map((it) => {
    const aaa = useFuZhuHeSuan(it).getFuZhuXiang();
    return aaa;
  });
  console.log(ccc);
}

window.getAllRequireModels2 = getAllRequireModels2;

export function useGetRef(rowDefines) {
  return {
    getRef(index, key) {
      return rowDefines.value[index].target[key];
    },
  };
}

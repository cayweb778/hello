import {convertCurrency} from "/@/utils/boozsoft/moneyUtil";

export const moneyBase = ['百', '十', '亿', '千', '百', '十', '万', '千', '百', '十', '元', '角', '分'];
export const moneyBaseBig = ['千','百','十','兆','千','百', '十', '亿', '千', '百', '十', '万', '千', '百', '十', '元', '角', '分'];

// 转换金额格式
export function formatMoneyAPI(moneyArr,{isBig}) {
  const _moneyBase=isBig?moneyBaseBig:moneyBase
  moneyArr = String(moneyArr);
  moneyArr = moneyArr == null ? '' : moneyArr;
  if (moneyArr == 0) moneyArr = '';
  if (moneyArr.split('.').length > 1 && moneyArr.split('.')[1] == 0) {
    moneyArr = moneyArr.split('.')[0];
  }
  moneyArr = String(moneyArr).split('.');
  if (moneyArr.length > 1) {
    let empty = [];
    if (parseInt(moneyArr[0]) < 0) {
      moneyArr[0] = moneyArr[0].substring(1, moneyArr[0].length);
    }
    let num = moneyArr[0].split('');

    for (let i = 0; i < Object.getOwnPropertyNames(_moneyBase).length - 2 - num.length - 1; i++) {
      empty.push('');
    }
    const float = moneyArr[1].split('');
    if (float.length == 1) {
      float.push('0');
    }
    if (float[float.length - 1] == '\n') {
      float.pop();
    }
    moneyArr = empty.concat(num.concat(float));
  } else {
    let empty = [];
    if (parseInt(moneyArr[0]) < 0) {
      moneyArr[0] = moneyArr[0].substring(1, moneyArr[0].length);
    }
    let num = moneyArr[0].split('');
    if (num[num.length - 1] == '\n') {
      num.pop();
    }
    for (let i = 0; i < Object.getOwnPropertyNames(_moneyBase).length - 2 - num.length - 1; i++) {
      empty.push('');
    }
    if (!moneyArr[0] == '') {
      num.push('0');
      num.push('0');
    } else {
      num.push('');
      num.push('');
    }
    moneyArr = empty.concat(num);
  }
  return moneyArr;
}


export function toChineseMoney(money) {
  if (money == 0 || money == null || money == '') {
    return "零元整"
  } else {
    return convertCurrency(money)
  }
}

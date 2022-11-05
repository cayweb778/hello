import {reactive} from "vue";
import {
  findAccountByBank,
  findAccountByBankAndQc
} from "/@/api/xian_jin_ri_ji_zhang/xian_jin_ri_ji_zhang";
import {findSettModesByFlag} from "/@/api/record/system/sett-modes";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";

const dynamicColumnAndDataModel: any = reactive({
  DEFAULT: [{
    title: '栏目名称',
    dataIndex: 'name',
    align: 'center',
    width: 150,
  },
    {
      title: '显示',
      dataIndex: 'check',
      align: 'center',
      slots: { customRender: 'checkBox' },
    },
    {
      title: '显示名称',
      dataIndex: 'nameNew',
      width: 150,
      align: 'center',
      slots: { customRender: 'nameInput', },
    },
    {
      title: '宽度(范围)',
      dataIndex: 'width',
      align: 'center',
      width: 140,
      slots: { customRender: 'widthInput', },
    },
    {
      title: '对齐方式',
      dataIndex: 'align',
      align: 'center',
      width: 140,
      slots: { customRender: 'alignRadio' },
    }
  ],
  DATA: [
    {
      key: '0',
      name: '序号',
      nameNew: '序号',
      check: true,
      width: 80,
      max: 80,
      min: 60,
      isFixed: true,
      align: 'center'
    },
    {
      key: '1',
      name: '日期',
      nameNew: '日期',
      check: true,
      width: 120,
      max: 120,
      min: 80,
      isFixed: true,
      align: 'left'
    },
    {
      key: '2',
      name: '凭证字号',
      nameNew: '凭证字号',
      check: true,
      width: 100,
      max: 120,
      min: 80,
      isFixed: true,
      align: 'center'
    },
    {
      key: '3',
      name: '摘要',
      nameNew: '摘要',
      check: true,
      width: 300,
      max: 450,
      min: 100,
      isFixed: false,
      align: 'left'
    },
    {
      key: '4',
      name: '对方科目',
      nameNew: '对方科目',
      check: true,
      width: 200,
      max: 250,
      min: 100,
      isFixed: false,
      align: 'left'
    },
    {
      key: '5',
      name: '借方',
      nameNew: '借方',
      check: true,
      width: 450,
      max: 600,
      min: 100,
      isFixed: false,
      align: '',
      children:[
        {
          key: '5-1',
          name: '金额',
          nameNew: '金额',
          check: true,
          width: 200,
          max: 300,
          min: 100,
          align: 'right',
          isFixed: false,
        },{
          key: '5-2',
          name: '外币',
          nameNew: '外币',
          check: true,
          width: 200,
          max: 300,
          min: 100,
          align: 'right',
          isFixed: false,
        }
      ]
    },
    {
      key: '6',
      name: '贷方',
      nameNew: '贷方',
      check: true,
      width: 450,
      max: 600,
      min: 100,
      isFixed: false,
      align: '',
      children:[
        {
          key: '6-1',
          name: '金额',
          nameNew: '金额',
          check: true,
          width: 200,
          max: 300,
          min: 100,
          align: 'right',
          isFixed: false,
        },{
          key: '6-2',
          name: '外币',
          nameNew: '外币',
          check: true,
          width: 200,
          max: 300,
          min: 100,
          align: 'right',
          isFixed: false,
        }
      ]
    },
    {
      key: '7',
      name: '余额',
      nameNew: '余额',
      check: true,
      width: 450,
      max: 200,
      min: 100,
      isFixed: false,
      align: '',
      children:[
        {
          key: '7-1',
          name: '方向',
          nameNew: '方向',
          check: true,
          width: 100,
          max: 300,
          min: 100,
          align: 'center',
          isFixed: false,
        },{
          key: '7-2',
          name: '金额',
          nameNew: '金额',
          check: true,
          width: 200,
          max: 300,
          min: 100,
          align: 'right',
          isFixed: false,
        },{
          key: '7-3',
          name: '外币',
          nameNew: '外币',
          check: true,
          width: 200,
          max: 300,
          min: 100,
          align: 'right',
          isFixed: false,
        }
      ]
    }
  ]
})

const thisName = (index)=>{
  let value = ''
  if (index.toString().indexOf('-') != -1){
    let arr = index.split('-');
    if (arr.length == 2)  {
      value = dynamicColumnAndDataModel.DATA[parseInt(arr[0])].children[parseInt(arr[1])-1].nameNew
      if ('' == value)value = dynamicColumnAndDataModel.DATA[parseInt(arr[0])].children[parseInt(arr[1])-1].name
    }else{
      value = dynamicColumnAndDataModel.DATA[parseInt(arr[0])].children[parseInt(arr[1])-1].children[parseInt(arr[2])-1].nameNew
      if ('' == value)value = dynamicColumnAndDataModel.DATA[parseInt(arr[0])].children[parseInt(arr[1])-1].children[parseInt(arr[2])-1].name
    }
  }else{
    value = dynamicColumnAndDataModel.DATA[index].nameNew
    if ('' == value)value = dynamicColumnAndDataModel.DATA[index].name
  }
  return value
}
const thisWidth = (index)=>{
  let value = 0
  if (index.toString().indexOf('-') != -1){
    let arr = index.split('-');
    if (arr.length == 2)  {
      value = dynamicColumnAndDataModel.DATA[parseInt(arr[0])].children[parseInt(arr[1])-1].width
    }else{
      value = dynamicColumnAndDataModel.DATA[parseInt(arr[0])].children[parseInt(arr[1])-1].children[parseInt(arr[2])-1].width
    }
  }else{
    value = dynamicColumnAndDataModel.DATA[index].width
  }
  return  parseInt(value)
}

const thisIsShow = (index)=>{
  let value = false
  if (index.toString().indexOf('-') != -1){
    let arr = index.split('-');
    if (arr.length == 2)  {
      value = dynamicColumnAndDataModel.DATA[parseInt(arr[0])].children[parseInt(arr[1])-1].check
    }else{
      value = dynamicColumnAndDataModel.DATA[parseInt(arr[0])].children[parseInt(arr[1])-1].children[parseInt(arr[2])-1].check
    }
  }else{
    value = dynamicColumnAndDataModel.DATA[index].check
  }
  return  value
}

const thisAlign = (index)=>{
  let value = 'center';
  if (index.toString().indexOf('-') != -1){
    let arr = index.split('-');
    if (arr.length == 2)  {
      value = dynamicColumnAndDataModel.DATA[parseInt(arr[0])].children[parseInt(arr[1])-1].align
    }else{
      value = dynamicColumnAndDataModel.DATA[parseInt(arr[0])].children[parseInt(arr[1])-1].children[parseInt(arr[2])-1].align
    }
  }else{
    value = dynamicColumnAndDataModel.DATA[index].align
  }
  return  value
}
const initDirection = (value,index)=>{
  const obj: any = {
    children: value,
    attrs: {},
  };
  obj.attrs.align = thisAlign(index);
  return obj;
}
let ColumnsModel = {}
const initColumnsModel = ()=>{
  ColumnsModel = {
    J:[
      {
        title: thisName(0),
        dataIndex: 'number',
        key: 'number',
        fixed: 'left',
        align: 'center',
        isShow: thisIsShow(0),
        width: thisWidth(0),
      },
      {
        title: thisName(1),
        dataIndex: 'idate',
        key: 'idate',
        fixed: 'left',
        isShow: thisIsShow(1),
        width: thisWidth(1),
        align: thisAlign(1),
        slots: {customRender: 'idate'},
      },
      {
        title: thisName(2),
        dataIndex: 'pzNum',
        key: 'pzNum',
        fixed: 'left',
        isShow: thisIsShow(2),
        width: thisWidth(2),
        align: thisAlign(2),
        slots: {customRender: 'pzNum'},
      },
      {
        title: thisName(3),
        dataIndex: 'zhaiyao',
        key: 'zhaiyao',
        fixed: 'left',
        isShow: thisIsShow(3),
        width: thisWidth(3),
        align: thisAlign(3),
        slots: {customRender: 'zhaiyao'},
      },
      {
        title: thisName(4),
        dataIndex: 'dfkemu',
        key: 'dfkemu',
        isShow: thisIsShow(4),
        width: thisWidth(4),
        align: thisAlign(4),
        slots: {customRender: 'dfkemu'}
      },
      {
        title: thisName(5),
        isShow: thisIsShow(5),
        width: thisWidth(5),
        children: [
          {
            title: thisName('5-1'),
            dataIndex: 'jieMoney',
            key: 'jieMoney',
            isShow: thisIsShow('5-1'),
            width: thisWidth('5-1'),
            align: thisAlign('5-1'),
            slots: {customRender: 'jieMoney'},
          }
        ],
      },
      {
        title: thisName(6),
        isShow: thisIsShow(6),
        width: thisWidth(6),
        children: [
          {
            title: thisName('6-1'),
            dataIndex: 'daiMoney',
            key: 'daiMoney',
            isShow: thisIsShow('6-1'),
            width: thisWidth('6-1'),
            align: thisAlign('6-1'),
            slots: {customRender: 'daiMoney'},
          }
        ],
      },
      {
        title: thisName(7),
        isShow: thisIsShow(7),
        width: thisWidth(7),
        children: [
          {
            title: thisName('7-1'),
            dataIndex: 'fangxiang',
            key: 'fangxiang',
            isShow: thisIsShow('7-1'),
            width: thisWidth('7-1'),
            align: thisAlign('7-1'),
            slots: {customRender: 'fangxiang'}
          },
          {
            title: thisName('7-2'),
            dataIndex: 'yueMoney',
            key: 'yueMoney',
            isShow: thisIsShow('7-2'),
            width: thisWidth('7-2'),
            align: thisAlign('7-2'),
            slots: {customRender: 'yueMoney'}
          }
        ],
      }
    ],
    SJ: [
      {
        title: thisName(0),
        dataIndex: 'number',
        key: 'number',
        fixed: 'left',
        align: 'center',
        isShow: thisIsShow(0),
        width: thisWidth(0),
      },
      {
        title: thisName(1),
        dataIndex: 'idate',
        key: 'idate',
        fixed: 'left',
        isShow: thisIsShow(1),
        width: thisWidth(1),
        align: thisAlign(1),
        slots: {customRender: 'idate'}
      },
      {
        title: thisName(2),
        dataIndex: 'pzNum',
        key: 'pzNum',
        fixed: 'left',
        isShow: thisIsShow(2),
        width: thisWidth(2),
        align: thisAlign(2),
        slots: {customRender: 'pzNum'}
      },
      {
        title: thisName(3),
        dataIndex: 'zhaiyao',
        key: 'zhaiyao',
        fixed: 'left',
        isShow: thisIsShow(3),
        width: thisWidth(3),
        align: thisAlign(3),
        slots: {customRender: 'zhaiyao'}
      },
      {
        title: thisName(4),
        dataIndex: 'dfkemu',
        key: 'dfkemu',
        isShow: thisIsShow(4),
        width: thisWidth(4),
        align: thisAlign(4),
        slots: {customRender: 'dfkemu'}
      },
      {
        title: thisName(5),
        isShow: thisIsShow(5),
        width: thisWidth(5),
        children: [
          {
            title: thisName('5-2'),
            dataIndex: 'jiewb',
            key: 'jiewb',
            isShow: thisIsShow('5-2'),
            width: thisWidth('5-2'),
            align: thisAlign('5-2'),
            slots: {customRender: 'jiewb'}
          }
        ],
      },
      {
        title: thisName(6),
        isShow: thisIsShow(6),
        width: thisWidth(6),
        children: [
          {
            title: thisName('6-2'),
            dataIndex: 'daiwb',
            key: 'daiwb',
            isShow: thisIsShow('6-2'),
            width: thisWidth('6-2'),
            align: thisAlign('6-2'),
            slots: {customRender: 'daiwb'}
          }
        ],
      },
      {
        title: thisName(7),
        isShow: thisIsShow(7),
        width: thisWidth(7),
        children: [
          {
            title: thisName('7-1'),
            dataIndex: 'fangxiang',
            key: 'fangxiang',
            isShow: thisIsShow('7-1'),
            width: thisWidth('7-1'),
            align: thisAlign('7-1'),
            slots: {customRender: 'fangxiang'}
          },
          {
            title: thisName('7-3'),
            dataIndex: 'yuewb',
            key: 'yuewb',
            isShow: thisIsShow('7-3'),
            width: thisWidth('7-3'),
            align: thisAlign('7-3'),
            slots: {customRender: 'yuewb'}
          }
        ],
      }
    ],
    WJ: [
      {
        title: thisName(0),
        dataIndex: 'number',
        key: 'number',
        fixed: 'left',
        align: 'center',
        isShow: thisIsShow(0),
        width: thisWidth(0),
      },
      {
        title: thisName(1),
        dataIndex: 'idate',
        key: 'idate',
        fixed: 'left',
        isShow: thisIsShow(1),
        width: thisWidth(1),
        align: thisAlign(1),
        slots: {customRender: 'idate'}
      },
      {
        title: thisName(2),
        dataIndex: 'pzNum',
        key: 'pzNum',
        fixed: 'left',
        isShow: thisIsShow(2),
        width: thisWidth(2),
        align: thisAlign(2),
        slots: {customRender: 'pzNum'}
      },
      {
        title: thisName(3),
        dataIndex: 'zhaiyao',
        key: 'zhaiyao',
        fixed: 'left',
        isShow: thisIsShow(3),
        width: thisWidth(3),
        align: thisAlign(3),
        slots: {customRender: 'zhaiyao'}
      },
      {
        title: thisName(4),
        dataIndex: 'dfkemu',
        key: 'dfkemu',
        isShow: thisIsShow(4),
        width: thisWidth(4),
        align: thisAlign(4),
        slots: {customRender: 'dfkemu'}
      },
      {
        title: thisName(5),
        isShow: thisIsShow(5),
        width: thisWidth(5),
        children: [
          {
            title: thisName('5-1'),
            dataIndex: 'jieMoney',
            key: 'jieMoney',
            isShow: thisIsShow('5-1'),
            width: thisWidth('5-1'),
            align: thisAlign('5-1'),
            slots: {customRender: 'jieMoney'}
          },
          {
            title: thisName('5-2'),
            dataIndex: 'jiewb',
            key: 'jiewb',
            isShow: thisIsShow('5-2'),
            width: thisWidth('5-2'),
            align: thisAlign('5-2'),
            slots: {customRender: 'jiewb'}
          }
        ],
      },
      {
        title: thisName(6),
        isShow: thisIsShow(6),
        width: thisWidth(6),
        children: [
          {
            title: thisName('6-1'),
            dataIndex: 'daiMoney',
            key: 'daiMoney',
            isShow: thisIsShow('6-1'),
            width: thisWidth('6-1'),
            align: thisAlign('6-1'),
            slots: {customRender: 'daiMoney'}
          },
          {
            title: thisName('6-2'),
            dataIndex: 'daiwb',
            key: 'daiwb',
            isShow: thisIsShow('6-2'),
            width: thisWidth('6-2'),
            align: thisAlign('6-2'),
            slots: {customRender: 'daiwb'}
          }
        ],
      },
      {
        title: thisName(7),
        isShow: thisIsShow(7),
        width: thisWidth(7),
        children: [
          {
            title: thisName('7-1'),
            dataIndex: 'fangxiang',
            key: 'fangxiang',
            isShow: thisIsShow('7-1'),
            width: thisWidth('7-1'),
            align: thisAlign('7-1'),
            slots: {customRender: 'fangxiang'}
          },
          {
            title: thisName('7-2'),
            dataIndex: 'yueMoney',
            key: 'yueMoney',
            isShow: thisIsShow('7-2'),
            width: thisWidth('7-2'),
            align: thisAlign('7-2'),
            slots: {customRender: 'yueMoney'}
          },
          {
            title: thisName('7-3'),
            dataIndex: 'yuewb',
            key: 'yuewb',
            isShow: thisIsShow('7-3'),
            width: thisWidth('7-3'),
            align: thisAlign('7-3'),
            slots: {customRender: 'yuewb'}
          }
        ],
      }
    ],
  }
}

export function initPageBasicDataByColumns(type:string,isTotal:boolean,reloadMark:boolean) {
  // 获取默认参数
  if (isTotal){
    ColumnsModel[type][1].slots = {}
  }
  let fiterList = ColumnsModel[type]
  return fiterList.filter(item=> item.isShow);
}
export function changeDefaultDynamics(list) {
  // 改变默认数据
  dynamicColumnAndDataModel['DEFAULT'] = list
  initColumnsModel()
}

export function initDynamics() {
  return dynamicColumnAndDataModel
}
export function initPageBasicDataMX() {
  return [{key: 0,date:'',number:'',summary: '期初余额',jf:'',df:'',fx:'平',yue:''}]
}

export async function initPageBasicData(condition, dynamicTenantId) {
  const qichu = await getTheLatestOpeningBalance(condition, dynamicTenantId)
  const res = await useRouteApi(findAccountByBank, {schemaName: dynamicTenantId})(condition)
  return await calculateTheDetailsOfTheSpecifiedPeriod(res, qichu, dynamicTenantId)
}

async function getTheLatestOpeningBalance(condition, dynamicTenantId) {
  // 期间为第一时 直接返回
  let thisYearOneQcMonth = '01'
  let iyperiod
  if (condition.periodStart != null || condition.periodStart != null) {
    iyperiod = condition.periodStart.split('-')[0]
  } else {
    iyperiod = condition.dateStart.split('-')[0]
  }
  let list = await useRouteApi(findAccountByBankAndQc, {schemaName: dynamicTenantId})({
    iyperiod: iyperiod + '00',
    ccode: condition.accountNum
  })
  let startYearMonth = condition.periodStart
  if (startYearMonth.substring(5, 7) == thisYearOneQcMonth) {
    return list
  } else {
    // 获取中间的期间
    let startOld = condition.periodStart
    let endOld = condition.periodEnd
    let supplementMonth = (parseInt(startYearMonth.substring(5, 7)) - 1) + ''
    supplementMonth = supplementMonth.length === 1 ? '0' + supplementMonth : supplementMonth
    let data = condition
    let year = startYearMonth.substr(0, 5)
    data.periodStart = year + thisYearOneQcMonth
    data.periodEnd = year + supplementMonth
    const res = await useRouteApi(findAccountByBank, {schemaName: dynamicTenantId})(data)
    condition.periodStart = startOld
    condition.periodEnd = endOld
    let mingXiList: any = await calculateTheDetailsOfTheSpecifiedPeriod(res, list, dynamicTenantId)
    let arr: any = []
    let oReg = new RegExp(',', "g");
    arr.push({
      md: mingXiList[mingXiList.length - 1].jieMoney.replace(oReg, ''),
      mc: mingXiList[mingXiList.length - 1].daiMoney.replace(oReg, '')
    })
    return arr
  }
}

async function calculateTheDetailsOfTheSpecifiedPeriod(res, qichu, dynamicTenantId) {
  let data: any = []
  const groupIperiodList = groupIperiodArr(res)
  const jiesuanList = await useRouteApi(findSettModesByFlag, {schemaName: dynamicTenantId})({})
  let num = 0
  let jieYearSum = 0
  let daiYearSum = 0
  let yue = 0
  let wbJieYearSum = 0
  let wbDaiYearSum = 0
  let wbYue = 0
  qichu.forEach(item => {
    if (item.md != '') {
      yue = yue + parseFloat(item.md)
    }
    if (item.mc != '') {
      yue = yue - parseFloat(item.mc)
    }
    if (item.nfratMd != null && item.nfratMd != '') {
      wbYue = wbYue + parseFloat(item.nfratMd)
    }
    if (item.nfratMc != null && item.nfratMc != '') {
      wbYue = wbYue - parseFloat(item.nfratMc)
    }
  })
  data.push({
    key: num,
    number: num + 1,
    idate: '',
    pzNum: '',
    zhaiyao: '期初余额',
    jiesuan: '',
    piaohao: '',
    jieMoney: '',
    jiewb: '',
    daiMoney: '',
    daiwb: '',
    fangxiang: yue==0?'平':yue>0?'借':'贷',
    yueMoney: yue==0?'':yue>0?toThousandFilter(yue.toFixed(2)):toThousandFilter((0-yue).toFixed(2)),
    yuewb: wbYue==0?'':wbYue>0?toThousandFilter(wbYue.toFixed(2)):toThousandFilter((0-wbYue).toFixed(2)),
    dfkemu: '',
    dfUnit: ''
  })
  num++
  for (let a=0; a<groupIperiodList.length; a++) {
    const groupIperiod: any = groupIperiodList[a]
    const groupDataList = groupDataArr(groupIperiod.items)
    let jiesum = 0
    let daisum = 0
    let wbJiesum = 0
    let wbDaisum = 0
    for (let i = 0; i < groupDataList.length; i++) {
      const group: any = groupDataList[i]
      let jie = 0
      let dai = 0
      let wbJie = 0
      let wbDai = 0
      group.items.forEach((item, index) => {
        if (item.md != '') {
          jie = jie + parseFloat(item.md)
          jiesum = jiesum + parseFloat(item.md)
          jieYearSum = jieYearSum + parseFloat(item.md)
          yue = yue + parseFloat(item.md)
        }
        if (item.mc != '') {
          dai = dai + parseFloat(item.mc)
          daisum = daisum + parseFloat(item.mc)
          daiYearSum = daiYearSum + parseFloat(item.mc)
          yue = yue - parseFloat(item.mc)
        }
        if (item.nfratMd != null && item.nfratMd != '') {
          wbJie = wbJie + parseFloat(item.nfratMd)
          wbJiesum = wbJiesum + parseFloat(item.nfratMd)
          wbJieYearSum = wbJieYearSum + parseFloat(item.nfratMd)
          wbYue = wbYue + parseFloat(item.nfratMd)
        }
        if (item.nfratMc != null && item.nfratMc != '') {
          wbDai = wbDai + parseFloat(item.nfratMc)
          wbDaisum = wbDaisum + parseFloat(item.nfratMc)
          wbDaiYearSum = wbDaiYearSum + parseFloat(item.nfratMc)
          wbYue = wbYue - parseFloat(item.nfratMc)
        }
        let jiesuan = item.pjCsettle
        jiesuanList.items.forEach(js => {
          if (js.settModesCode == item.pjCsettle) {
            jiesuan = js.settModesName
          }
        })
        let inoId = item.inoId
        if (item.inoId!=null && item.inoId!='') {
          if (item.inoId.length == 1) {
            inoId = '000' + item.inoId
          }
          if (item.inoId.length == 2) {
            inoId = '00' + item.inoId
          }
          if (item.inoId.length == 3) {
            inoId = '0' + item.inoId
          }
        }
        data.push({
          key: num,
          number: num + 1,
          idate: item.dbillDate,
          pzNum: item.csign + '-' + inoId,
          zhaiyao: item.cdigest,
          jiesuan: jiesuan,
          piaohao: item.pjId,
          jieMoney: item.md == 0 ? '' : toThousandFilter(item.md),
          jiewb: item.nfratMd == 0 ? '' : toThousandFilter(item.nfratMd),
          daiMoney: item.mc == 0 ? '' : toThousandFilter(item.mc),
          daiwb: item.nfratMc == 0 ? '' : toThousandFilter(item.nfratMc),
          fangxiang: yue == 0 ? '平' : yue > 0 ? '借' : '贷',
          yueMoney: yue == 0 ? '' : yue > 0 ? toThousandFilter(yue.toFixed(2)) : toThousandFilter((0 - yue).toFixed(2)),
          yuewb: wbYue == 0 ? '' : wbYue > 0 ? toThousandFilter(wbYue.toFixed(2)) : toThousandFilter((0 - wbYue).toFixed(2)),
          dfkemu: item.oppositeCode==null?'':item.oppositeCode,
          dfUnit: item.pjUnitName==null?'':item.pjUnitName,
          uniqueCode: item.uniqueCode
        })
        num++
      })
      data.push({
        key: num,
        number: num + 1,
        idate: group.dbillDate,
        pzNum: '',
        zhaiyao: '本日合计',
        jiesuan: '',
        piaohao: '',
        jieMoney: jie == 0 ? '' : toThousandFilter(jie.toFixed(2)),
        jiewb: wbJie == 0 ? '' : toThousandFilter(wbJie.toFixed(2)),
        daiMoney: dai == 0 ? '' : toThousandFilter(dai.toFixed(2)),
        daiwb: wbDai == 0 ? '' : toThousandFilter(wbDai.toFixed(2)),
        fangxiang: yue==0?'平':yue>0?'借':'贷',
        yueMoney: yue==0?'':yue>0?toThousandFilter(yue.toFixed(2)):toThousandFilter((0-yue).toFixed(2)),
        yuewb: wbYue==0?'':wbYue>0?toThousandFilter(wbYue.toFixed(2)):toThousandFilter((0-wbYue).toFixed(2)),
        dfkemu: '',
        dfUnit: ''
      })
      num++
    }
    data.push({
      key: num,
      number: num + 1,
      idate: groupIperiod.iyperiod.substr(0,4)+'.'+groupIperiod.iyperiod.substr(4,6),
      pzNum: '',
      zhaiyao: '本月合计',
      jiesuan: '',
      piaohao: '',
      jieMoney: jiesum == 0 ? '' : toThousandFilter(jiesum.toFixed(2)),
      jiewb: wbJiesum == 0 ? '' : toThousandFilter(wbJiesum.toFixed(2)),
      daiMoney: daisum == 0 ? '' : toThousandFilter(daisum.toFixed(2)),
      daiwb: wbDaisum == 0 ? '' : toThousandFilter(wbDaisum.toFixed(2)),
      fangxiang: yue==0?'平':yue>0?'借':'贷',
      yueMoney: yue==0?'':yue>0?toThousandFilter(yue.toFixed(2)):toThousandFilter((0-yue).toFixed(2)),
      yuewb: wbYue==0?'':wbYue>0?toThousandFilter(wbYue.toFixed(2)):toThousandFilter((0-wbYue).toFixed(2)),
      dfkemu: '',
      dfUnit: ''
    })
    num++
    data.push({
      key: num,
      number: num + 1,
      idate: groupIperiod.iyperiod.substr(0,4)+'.'+groupIperiod.iyperiod.substr(4,6),
      pzNum: '',
      zhaiyao: '本年累计',
      jiesuan: '',
      piaohao: '',
      jieMoney: jieYearSum == 0 ? '' : toThousandFilter(jieYearSum.toFixed(2)),
      jiewb: wbJieYearSum == 0 ? '' : toThousandFilter(wbJieYearSum.toFixed(2)),
      daiMoney: daiYearSum == 0 ? '' : toThousandFilter(daiYearSum.toFixed(2)),
      daiwb: wbDaiYearSum == 0 ? '' : toThousandFilter(wbDaiYearSum.toFixed(2)),
      fangxiang: yue==0?'平':yue>0?'借':'贷',
      yueMoney: yue==0?'':yue>0?toThousandFilter(yue.toFixed(2)):toThousandFilter((0-yue).toFixed(2)),
      yuewb: wbYue==0?'':wbYue>0?toThousandFilter(wbYue.toFixed(2)):toThousandFilter((0-wbYue).toFixed(2)),
      dfkemu: '',
      dfUnit: ''
    })
    num++
  }
  return data
}

function toThousandFilter(num:any) {
  if (num=='' || num==null){
    return ''
  }
  return (+num || 0).toFixed(2).replace(/\d{1,3}(?=(\d{3})+(\.\d*)?$)/g, '$&,')
}

function groupDataArr(beforeData:any) {
  let tempArr: any = [];
  let afterData: any = []
  for (let i = 0; i < beforeData.length; i++) {
    if (tempArr.indexOf(beforeData[i].dbillDate) === -1) {
      afterData.push({
        dbillDate: beforeData[i].dbillDate,
        iyperiod: beforeData[i].iyperiod,
        items: [beforeData[i]]
      });
      tempArr.push(beforeData[i].dbillDate);
    } else {
      for (let j = 0; j < afterData.length; j++) {
        if (afterData[j].dbillDate == beforeData[i].dbillDate) {
          afterData[j].items.push(beforeData[i]);
          break;
        }
      }
    }
  }
  return afterData
}
function groupIperiodArr(beforeData:any) {
  let tempArr: any = [];
  let afterData: any = []
  for (let i = 0; i < beforeData.length; i++) {
    if (tempArr.indexOf(beforeData[i].iyperiod) === -1) {
      afterData.push({
        iyperiod: beforeData[i].iyperiod,
        iperiod: beforeData[i].iperiod,
        iyear: beforeData[i].iyear,
        imonth: beforeData[i].imonth,
        items: [beforeData[i]]
      });
      tempArr.push(beforeData[i].iyperiod);
    } else {
      for (let j = 0; j < afterData.length; j++) {
        if (afterData[j].iyperiod == beforeData[i].iyperiod) {
          afterData[j].items.push(beforeData[i]);
          break;
        }
      }
    }
  }
  return afterData
}

initColumnsModel()

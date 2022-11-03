import {apiDataToModel, apiDataToModelAsync} from '../../transforms/apiDataToModelUtils';
import {findSettModesAll} from "/@/api/record/system/sett-modes";

export function toDemoModel() {
  return {
    settings:{
      titleName:'新增凭证'
    },
    'options': {
      'optionType': '记',
      'optionFlag': '0',
      'optionDanJuQuantity': null,
      'optionZhiDanBy': '侯晓聪',
      'optionChuNaBy': '侯晓聪',
      'optionChuNaDate': null,
      'optionZhuGuan': '',
      'optionCheckBy': '罗锋',
      'optionCheckDate': '2021-01-31',
      'optionJiZhangBy': '',
      'optionJiZhangDate': null
    },
    repository:{
      zhaiYao:{
        data:[],
        findAll(){

        }
      },
      kuaiJiKeMu:{
        data:[],
        findAll(){

        }
      },
      fuZhuHeSuan:{
        data:[],
        findAll(){

        },
        async findJieSuanFangShiAll(){
          return (await findSettModesAll()).items.map(item => ({
            key: item.settModesCode,
            label: item.settModesName,
            target: item
          }))
        }
      }
    },
    'rows': [{
      'zhaiYao': {'value': 'DSQ009-HLS'},
      'kuaiJiKeMu': {'value': {'code': '1001', 'name': '销项税额', 'path': ''}},
      'jieMoney': {'value': '0.00'},
      'daiMoney': {'value': '4590.26'},
      'fuZhuHeSuan': {
        'value': [
          {
            'value': {'key': 1, 'label': '技术部', value: ''},
            'key': 'fzEmp',
            'label': '人员'
          },
          {
            'value': {'key': 16, 'label': '技术部'},
            'key': 'fzGys',
            'label': '供应商'
          }
        ]
      },
      'huiLv': {
        'value': {
          'currencyType': null,
          'currencyExchangeRate': '1.0000',
          'currencyMoney': '0.00'
        }
      },
      'shuliangjine': {'value': {'slUnit': '个', 'slPrice': '15.5', 'slNumber': '15'}}
    }]
  };
}

export function toEmptyModel() {
  const abc = {
    rows: [
      {
        zhaiYao: {
          value: '这是一个摘要'
        },
        kuaiJiKeMu: {
          value: {
            code: 1001,
            name: '库存现金',
            path: '银行存款/库存现金'
          }
        },
        jieMoney: {
          value: '15.00'
        },
        daiMoney: {
          value: '15.00'
        },
        fuZhuHeSuan: {
          html: '',
          value: {
            fzDept: '1',
            fzEmp: '1'
          }
        },
        huiLv: {
          value: {
            type: '$',
            value: 15
          }
        },
        shuliangjine: {
          value: {
            number: 10,
            unit: 15.5,
            sum: 155
          }
        }
      }
    ]
  };
  return abc
}

export function apiDataToShowModel(apiData, params?) {
  if (params == null) {
    params = {};
  }
  if(params.settings==null){
    params.settings = {};
  }
  return apiDataToModel(apiData, params);
}

export async function apiDataToShowModelAsync(apiData, params?) {
  if (params == null) {
    params = {};
  }
  if(params.settings==null){
    params.settings = {};
  }
  return await apiDataToModelAsync(apiData, params);
}

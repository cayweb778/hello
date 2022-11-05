import {reactive} from "vue";

const jsonData = reactive({
  //编码方式
  codeWay:[
    {
      key: '0',
      name: '自动编码',
    },
    {
      key: '1',
      name: '手动编码',
    }
  ],
  //档案
  daList:[
    {
      key: '1',
      value: '财务会计',
      children:[
        {
          key: '1-1',
          value: '凭证',
          tableName: 'xx',
          lenght: 3,
          qz:[
            {
              key: '3-0',
              name: '系统编码',
              code: 'PZ',
              lenght: 2,
            },
            {
              key: '88',
              name: '日期(年月)',
              lenght: 6,
            },
            {
              key: '99',
              name: '自定义',
            }
          ]
        },
      ]
    },
    {
      key: '2',
      value: '固定资产',
      children:[
        {
          key: '2-0',
          value: '资产卡片',
          lenght: 3,
          tableName: 'xx',
          qz:[
            {
              key: '3-0',
              name: '系统编码',
              code: 'AC',
              lenght: 2,
            },
            {
              key: '88',
              name: '日期(年月)',
              lenght: 6,
            },
            {
              key: '99',
              name: '自定义',
            }
          ]
        },
        {
          key: '2-1',
          value: '变动单',
          lenght: 3,
          tableName: ''                                                                                                                                                                                                                              ,
          qz:[
            {
              key: '3-0',
              name: '系统编码',
              code: 'AA',
              lenght: 2,
            },
            {
              key: '88',
              name: '日期(年月)',
              lenght: 6,
            },
            {
              key: '99',
              name: '自定义',
            }
          ]
        },
        {
          key: '2-2',
          value: '拆分单',
          lenght: 3,
          tableName: 'xx',
          qz:[
            {
              key: '3-0',
              name: '系统编码',
              code: 'AS',
              lenght: 2,
            },
            {
              key: '88',
              name: '日期(年月)',
              lenght: 6,
            },
            {
              key: '99',
              name: '自定义',
            }
          ]
        },
        {
          key: '2-3',
          value: '处置单',
          lenght: 3,
          tableName: 'xx',
          qz:[
            {
              key: '3-0',
              name: '系统编码',
              code: 'AR',
              lenght: 2,
            },
            {
              key: '88',
              name: '日期(年月)',
              lenght: 6,
            },
            {
              key: '99',
              name: '自定义',
            }
          ]
        },
        {
          key: '2-4',
          value: '盘点单',
          lenght: 3,
          tableName: 'xx',
          qz:[
            {
              key: '3-0',
              name: '系统编码',
              code: 'AB',
              lenght: 2,
            },
            {
              key: '88',
              name: '日期(年月)',
              lenght: 6,
            },
            {
              key: '99',
              name: '自定义',
            }
          ]
        },
        {
          key: '2-5',
          value: '工作量清单',
          lenght: 3,
          tableName: '_app_group_sys_psn_type',
          qz:[
            {
              key: '3-0',
              name: '系统编码',
              code: 'AQ',
              lenght: 2,
            },
            {
              key: '88',
              name: '日期(年月)',
              lenght: 6,
            },
            {
              key: '99',
              name: '自定义',
            }
          ]
        },
        {
          key: '2-6',
          value: '折旧清单',
          lenght: 3,
          tableName: 'XX',
          qz:[
            {
              key: '3-0',
              name: '系统编码',
              code: 'AD',
              lenght: 2,
            },
            {
              key: '88',
              name: '日期(年月)',
              lenght: 6,
            },
            {
              key: '99',
              name: '自定义',
            }
          ]
        },

      ]
    },
    {
      key: '3',
      value: '采购管理',
      children:[
        {
          key: '3-0',
          value: '采购订单',
          lenght: 3,
          tableName: 'xx',
          qz:[
            {
              key: '3-0',
              name: '系统编码',
              code: 'PQ',
              lenght: 2,
            },
            {
              key: '88',
              name: '日期(年月)',
              lenght: 6,
            },
            {
              key: '99',
              name: '自定义',
            }
          ]
        },
        {
          key: '3-1',
          value: '采购到货单',
          lenght: 3,
          tableName: 'xx',
          qz:[
            {
              key: '3-0',
              name: '系统编码',
              code: 'PS',
              lenght: 2,
            },
            {
              key: '88',
              name: '日期(年月)',
              lenght: 6,
            },
            {
              key: '99',
              name: '自定义',
            }
          ]
        },
        {
          key: '3-2',
          value: '采购发票',
          lenght: 3,
          tableName: 'xx',
          qz:[
            {
              key: '3-0',
              code: 'PB',
              name: '系统编码',
              lenght: 2,
            },
            {
              key: '88',
              name: '日期(年月)',
              lenght: 6,
            },
            {
              key: '99',
              name: '自定义',
            }
          ]
        }
      ]
    },
    {
      key: '4',
      value: '销售管理',
      children:[
        {
          key: '3-5',
          value: '销售订单',
          lenght: 3,
          tableName: 'xx',
          qz:[
            {
              key: '3-0',
              code: 'SO',
              name: '系统编码',
              lenght: 2,
            },
            {
              key: '88',
              name: '日期(年月)',
              lenght: 6,
            },
            {
              key: '99',
              name: '自定义',
            }
          ]
        },
        {
          key: '3-6',
          value: '销货单',
          lenght: 3,
          tableName: 'xx',
          qz:[
            {
              key: '3-0',
              code: 'SA',
              name: '系统编码',
              lenght: 2,
            },
            {
              key: '88',
              name: '日期(年月)',
              lenght: 6,
            },
            {
              key: '99',
              name: '自定义',
            }
          ]
        },
        {
          key: '3-7',
          value: '销售发票',
          lenght: 3,
          tableName: 'xx',
          qz:[
            {
              key: '3-0',
              code: 'SB',
              name: '系统编码',
              lenght: 2,
            },
            {
              key: '88',
              name: '日期(年月)',
              lenght: 6,
            },
            {
              key: '99',
              name: '自定义',
            }
          ]
        },
      ]
    },
    {
      key: '5',
      value: '库存管理',
      children:[
        {
          key: '3-10',
          value: '采购入库单',
          lenght: 3,
          tableName: 'xx',
          qz:[
            {
              key: '3-0',
              code: 'II',
              name: '系统编码',
              lenght: 2,
            },
            {
              key: '88',
              name: '日期(年月)',
              lenght: 6,
            },
            {
              key: '99',
              name: '自定义',
            }
          ]
        },
        {
          key: '3-11',
          value: '销售出库单',
          lenght: 3,
          tableName: 'xx',
          qz:[
            {
              key: '3-0',
              code: 'IO',
              name: '系统编码',
              lenght: 2,
            },
            {
              key: '88',
              name: '日期(年月)',
              lenght: 6,
            },
            {
              key: '99',
              name: '自定义',
            }
          ]
        },
        {
          key: '3-12',
          value: '销售物流单',
          lenght: 3,
          tableName: 'xx',
          qz:[
            {
              key: '3-0',
              code: 'WL',
              name: '系统编码',
              lenght: 2,
            },
            {
              key: '88',
              name: '日期(年月)',
              lenght: 6,
            },
            {
              key: '99',
              name: '自定义',
            }
          ]
        },
        {
          key: '3-13',
          value: '其它入库单',
          lenght: 3,
          tableName: 'xx',
          qz:[
            {
              key: '3-0',
              code: 'IC',
              name: '系统编码',
              lenght: 2,
            },
            {
              key: '88',
              name: '日期(年月)',
              lenght: 6,
            },
            {
              key: '99',
              name: '自定义',
            }
          ]
        },
        {
          key: '3-14',
          value: '盘点单',
          lenght: 3,
          tableName: 'stock_taking',
          qz:[
            {
              key: '3-0',
              code: 'PD',
              name: '系统编码',
              lenght: 2,
            },
            {
              key: '88',
              name: '日期(年月)',
              lenght: 6,
            },
            {
              key: '99',
              name: '自定义',
            }
          ]
        },
        {
          key: '3-15',
          value: '调拨单',
          lenght: 3,
          tableName: 'xx',
          qz:[
            {
              key: '3-0',
              code: 'AL',
              name: '系统编码',
              lenght: 2,
            },
            {
              key: '88',
              name: '日期(年月)',
              lenght: 6,
            },
            {
              key: '99',
              name: '自定义',
            }
          ]
        },
        {
          key: '3-16',
          value: '形态转换单',
          lenght: 3,
          tableName: 'xx',
          qz:[
            {
              key: '3-0',
              code: 'BC',
              name: '系统编码',
              lenght: 2,
            },
            {
              key: '88',
              name: '日期(年月)',
              lenght: 6,
            },
            {
              key: '99',
              name: '自定义',
            }
          ]
        },
        {
          key: '3-17',
          value: '借入借用单',
          lenght: 3,
          tableName: 'xx',
          qz:[
            {
              key: '3-0',
              code: 'JR',
              name: '系统编码',
              lenght: 2,
            },
            {
              key: '88',
              name: '日期(年月)',
              lenght: 6,
            },
            {
              key: '99',
              name: '自定义',
            }
          ]
        },
        {
          key: '3-18',
          value: '借入还回单',
          lenght: 3,
          tableName: 'xx',
          qz:[
            {
              key: '3-0',
              code: 'JZ',
              name: '系统编码',
              lenght: 2,
            },
            {
              key: '88',
              name: '日期(年月)',
              lenght: 6,
            },
            {
              key: '99',
              name: '自定义',
            }
          ]
        },
        {
          key: '3-22',
            value: '借出借用单',
          lenght: 3,
          tableName: 'xx',
          qz:[
          {
            key: '3-0',
            code: 'JC',
            name: '系统编码',
            lenght: 2,
          },
          {
            key: '88',
            name: '日期(年月)',
            lenght: 6,
          },
          {
            key: '99',
            name: '自定义',
          }
        ]
        },
        {
          key: '3-23',
            value: '借出归还单',
          lenght: 3,
          tableName: 'xx',
          qz:[
          {
            key: '3-0',
            code: 'JG',
            name: '系统编码',
            lenght: 2,
          },
          {
            key: '88',
            name: '日期(年月)',
            lenght: 6,
          },
          {
            key: '99',
            name: '自定义',
          }
        ]
        },
        {
          key: '3-19',
          value: '拣货装箱单',
          lenght: 3,
          tableName: 'xx',
          qz:[
            {
              key: '3-0',
              code: 'JH',
              name: '系统编码',
              lenght: 2,
            },
            {
              key: '88',
              name: '日期(年月)',
              lenght: 6,
            },
            {
              key: '99',
              name: '自定义',
            }
          ]
        },
        {
          key: '3-20',
          value: '采购物流单',
          lenght: 3,
          tableName: 'xx',
          qz:[
            {
              key: '3-0',
              code: 'WC',
              name: '系统编码',
              lenght: 2,
            },
            {
              key: '88',
              name: '日期(年月)',
              lenght: 6,
            },
            {
              key: '99',
              name: '自定义',
            }
          ]
        },
        {
          key: '3-21',
          value: '其他出库单',
          lenght: 3,
          tableName: 'xx',
          qz:[
            {
              key: '3-0',
              code: 'QT',
              name: '系统编码',
              lenght: 2,
            },
            {
              key: '88',
              name: '日期(年月)',
              lenght: 6,
            },
            {
              key: '99',
              name: '自定义',
            }
          ]
       },
        {
          key: '3-25',
          value: '入库调整单',
          lenght: 3,
          tableName: 'xx',
          qz:[
            {
              key: '3-0',
              code: 'TR',
              name: '系统编码',
              lenght: 2,
            },
            {
              key: '88',
              name: '日期(年月)',
              lenght: 6,
            },
            {
              key: '99',
              name: '自定义',
            }
          ]
        },
        {
          key: '3-26',
          value: '出库调整单',
          lenght: 3,
          tableName: 'xx',
          qz:[
            {
              key: '3-0',
              code: 'TC',
              name: '系统编码',
              lenght: 2,
            },
            {
              key: '88',
              name: '日期(年月)',
              lenght: 6,
            },
            {
              key: '99',
              name: '自定义',
            }
          ]
        },
      ]
    },
    {
      key: '6',
      value: '应收款',
      children:[
        {
          key: '3-3',
          value: '应付款单',
          lenght: 3,
          tableName: 'xx',
          qz:[
            {
              key: '3-0',
              name: '系统编码',
              code: 'YF',
              lenght: 2,
            },
            {
              key: '88',
              name: '日期(年月)',
              lenght: 6,
            },
            {
              key: '99',
              name: '自定义',
            }
          ]
        },
        {
          key: '3-4',
          value: '付款单',
          lenght: 3,
          tableName: 'xx',
          qz:[
            {
              key: '3-0',
              code: 'FK',
              name: '系统编码',
              lenght: 2,
            },
            {
              key: '88',
              name: '日期(年月)',
              lenght: 6,
            },
            {
              key: '99',
              name: '自定义',
            }
          ]
        },
        {
          key: '3-8',
          value: '应收款单',
          lenght: 3,
          tableName: 'xx',
          qz:[
            {
              key: '3-0',
              code: 'YS',
              name: '系统编码',
              lenght: 2,
            },
            {
              key: '88',
              name: '日期(年月)',
              lenght: 6,
            },
            {
              key: '99',
              name: '自定义',
            }
          ]
        },
        {
          key: '3-9',
          value: '收款单',
          lenght: 3,
          tableName: 'xx',
          qz:[
            {
              key: '3-0',
              code: 'SK',
              name: '系统编码',
              lenght: 2,
            },
            {
              key: '88',
              name: '日期(年月)',
              lenght: 6,
            },
            {
              key: '99',
              name: '自定义',
            }
          ]
        },
      ]
    },
  ],
  //分隔符
  delimiter:[
    {
      key: '1',
      name: '无分隔符',
    },
    {
      key: '2',
      name: '.',
    },
    {
      key: '3',
      name: '-',
    },


  ],

})

export function initDynamics() {
  return jsonData
}

export function getQz(index) {
  //找下级 不处理一级
  let d
  jsonData.daList.forEach(item=>{
    if(item.children.find(value=> index === value.key)){
      d = item.children.find(value=> index === value.key)
      return  d
    }
  })
  return d
}

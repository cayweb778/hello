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
      value: '主数据',
      children:[
        {
          key: '1-1',
          value: '人员',
          lenght: 3,
          tableName: '_app_group_sys_psn',
          qz:[
            {
              key: '1-0',
              name: '人员类别编码',
              lenght: 3,
            },
            {
              key: '88',
              name: '日期(年月)',
              lenght: 6,
            },
            {
              key: '99',
              name: '手动录入',
            }
          ]
        },
        {
          key: '1-2',
          value: '客户',
          lenght: 3,
          tableName: '_app_group_customer',
          qz:[
            {
              key: '2-0',
              name: '客户分类编码',
              lenght: 3,
            },
            {
              key: '88',
              name: '日期(年月)',
              lenght: 6,
            },
            {
              key: '99',
              name: '手动录入',
            }
          ]
        },
        {
          key: '1-3',
          value: '供应商',
          lenght: 4,
          tableName: '_app_group_supplier',
          qz:[
            {
              key: '3-0',
              name: '供应商分类编码',
              lenght: 3,
            },
            {
              key: '88',
              name: '日期(年月)',
              lenght: 6,
            },
            {
              key: '99',
              name: '手动录入',
            }
          ]
        },
        {
          key: '1-4',
          value: '项目',
          lenght: 5,
          tableName: '_app_group_customer',
          qz:[
            {
              key: '4-0',
              name: '项目大类编码',
              lenght: 3,
            },
            {
              key: '4-1',
              name: '项目分类编码',
              lenght: 3,
            },
            {
              key: '88',
              name: '日期(年月)',
              lenght: 6,
            },
            {
              key: '99',
              name: '手动录入',
            }
          ]
        },
        {
          key: '1-5',
          value: '自定义项',
          lenght: 5,
          tableName: '_app_group_customer',
          qz:[
            {
              key: '88',
              name: '日期(年月)',
              lenght: 6,
            },
            {
              key: '99',
              name: '手动录入',
            }
          ]
        }
      ]
    },
    {
      key: '2',
      value: '财务会计',
      children:[
        {
          key: '2-0',
          value: '结算方式',
          lenght: 3,
          tableName: 'xx',
          qz:[
            {
              key: '2-0',
              name: '结算编码',
              lenght: 3,
            },
            {
              key: '88',
              name: '日期(年月)',
              lenght: 6,
            },
            {
              key: '99',
              name: '手动录入',
            }
          ]
        },
      ]
    },
    {
      key: '3',
      value: '固定资产',
      children:[]
    },
    {
      key: '4',
      value: '存货管理',
      children:[
        {
          key: '4-0',
          value: '存货管理',
          lenght: 3,
          tableName: 'xx',
          qz:[
            {
              key: '2-0',
              name: '存货分类编码',
              lenght: 3,
            },
            {
              key: '88',
              name: '日期(年月)',
              lenght: 6,
            },
            {
              key: '99',
              name: '手动录入',
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

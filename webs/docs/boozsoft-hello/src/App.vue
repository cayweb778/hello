<script setup>
import {pinyin} from 'pinyin-pro';
import {ref} from "vue";
import {oneDir} from "./funs";
import {replaceStr} from "./funs/index2";

const arr = [
  {name: '期初', child: [{name: '业务', child: ['应收款期初余额']}]},
  {
    name: '应收', child: [{
      name: '单据', child: [
        '应收单',
        '销售发票',
        '费用单',
      ]
    },
      {
        name: '查询',
        child: [
          '应收单列表',
          '销售发票列表',
          '费用单列表',
          '应收款统计表',
          '销售发票统计表',
          '费用统计表',
          '应收余额表',
        ]
      }]
  },
  {
    name: '收款', child: [
      {
        name: '操作', child: [
          '选择收款',
          '应收收款单',
          '应收退款单',
        ]
      },
      {
        name: '查询',
        child: [
          '收款单列表',
          '退款单列表',
          '收款明细表',
          '收款统计表',
        ]
      }
    ]
  },
  {
    name: '核销', child: [{
      name: '操作', child: [
        '手工核销',
        '取消核销',
      ]
    }, {
      name: '查询',
      child: [
        '应收核销明细表'
      ]
    }]
  },
  {
    name: '冲销', child: [{
      name: '操作',
      child: [
        '预收冲应收',
        '应收冲应收',
        '应收冲收付',
      ]
    }, {
      name: '查询', child: [
        '预收冲应收列表',
        '应收冲应收列表',
        '应收冲收付列表',
      ]
    }]
  },
  {
    name: '凭证', child: [{
      name: '单据', child: [
        '应收款制单',
        '收款单制单',
      ]
    }, {
      name: '查询',
      child: [
        '凭证列表',
        '销售物流单列表',
      ]
    }]
  },
  {
    name: '账表', child: [{
      name: '账表查询', child: [
        '应收余额表',
        '应收总账',
        '应收明细账',
        '收款执行表',
      ]
    }, {
      name: '账表分析', child: [
        '应收账龄分析',
        '资金预测分析',
      ]
    }]
  },
  {
    name: '结账', child: [{
      name: '业务', child: [
        '月末结账',
        '取消结账',
      ]
    }]
  },
  {
    name: '设置', child: [{
      name: '参数', child: [
        '系统选项',
        '业务流程',
      ]
    }, {
      name: '应收档案', child: [
        '收款账户',
        '结算方式',
        '费用档案',
      ]
    }, {
      name: '对应科目', child: [
        '应收科目设置',
        '收款科目设置',
        '核销科目设置',
      ]
    }]
  }
]

const ModelName = 'YingShouZhang'

let mainIndex = 3000000
// arr一级目录转拼音
arr.forEach(it => {
  it.enName = replaceStr(pinyin(it.name, {toneType: 'none'})).replaceAll(' ', '')
})




oneDir(arr, mainIndex)
console.warn('第一阶段', JSON.parse(JSON.stringify(arr)))


const arr2 = arr.flatMap(it => {
  return it.child
}).flatMap(it => {
  return it.child
})

console.log('第二阶段', arr2)
const three = arr2.map(it => {
  return `
    createPlatformMenu({
    id: ${it.id},
    path: '${it.enName}',
    componentName: '${ModelName}XX${it.enName}',
    component: '/platforms/${ModelName}/${it.enName}/layouts/RouteCache',
    parentId: ${it.parentId},
    name: '${it.name}',
  }),
  `
})
console.log('第三阶段', three.join('\n'))

const one = arr.map(it => {
  return `
    createPlatformMenu({
    id: ${it.id},
    component: 'LAYOUT',
    path: '/${ModelName}/${it.enName}',
    parentId: 0,
    name: '${it.name}'
  }),
  `
})
console.log('第四阶段', one.join('\n'))

// 二级目录
function getTwo() {
  const two = []
  arr.forEach(it => {
    it.child.forEach(it2 => {
      two.push(`
    createPlatformMenu({
    id: ${it2.id},
    path: '${it2.enName}',
     component: '',
    parentId: ${it2.parentId},
    name: '${it2.name}'
  }),
  `)
    })
  })
  return two
}

console.log('第五阶段', getTwo().join('\n'))
const text = ref(
    '/** 一级目录 **/\n' +
    one.join('\n')
    + '/** 二级目录 **/\n'
    + getTwo().join('\n')
    + '/**  三级目录 **/\n'
    + three.join('\n')
)



const threeObj = arr2.map(it => {
  return {enName:it.enName,name:it.name}
})
console.log(JSON.stringify(threeObj))
</script>

<template>
  <textarea v-model="text" style="width:500px;height:1000px"></textarea>
</template>


<template>
  <div style="position: fixed;width:100%;height: 100%;left:0;top:0;background:yellow;z-index: 111">
    {{ selectKeys }}
    平台：总账<br>
    公司：希格科技<br>
    角色：财务<br>
    用户：开发组<br>
    {{recordNames}}<br>
    <table>
      <thead>
      <tr>
        <td>
          <Checkbox/>
        </td>
        <td>功能点名称</td>
        <td v-for="it in s1">{{ it.name }}</td>
      </tr>
      </thead>
      <tbody>
      <CheckboxGroup style="display: table-row" v-for="it2 in recordNames" v-model:value="it2.selectKeys">
        <td>
          <Checkbox/>
        </td>
        <td>{{ it2.name }}</td>
        <td v-for="it in s1">
          <Checkbox :value="it.name"/>
        </td>
      </CheckboxGroup>
      </tbody>
    </table>

  </div>
</template>
<script setup>
import {ref} from 'vue'
import {Checkbox} from 'ant-design-vue'

const CheckboxGroup = Checkbox.Group

const selectKeys = ref()

const recordNames = ref([
  {name: '部门档案'},
  {name: '填制凭证'},
  {name: '凭证列表'},
  {name: '凭证列表'},
  {name: '凭证列表'},
  {name: '凭证列表'},
  {name: '凭证列表'},
  {name: '凭证列表'},
  {name: '凭证列表'},
])

const s1 = [
  {name: '查看'},
  {name: '发送邮件'},
  {name: '新增'},
  {name: '修改'},
  {name: '删除'},
  {name: '审核'},
  {name: '弃审'},
  {name: '中止执行'},
  {name: '取消中止'},
  {name: '打印'},
  {name: '打印模块设置'},
  {name: '导出'},
  {name: '导入'},
  {name: '消息'},
  {name: '变更'},
  {name: '发布'},
  {name: '设置'},
  {name: '重新取价'},
  {name: '附件查看'},
  {name: '附件变更'},
  {name: '附件编辑'},
  {name: '现存量查询'},
  {name: '销售订单查询'},
  {name: '执行采购需求分析'},
  {name: '金额'},
  {name: '客户'},
]
</script>
<style>
td {
  /*border: solid 1px black;*/
  text-align: center;
}
</style>

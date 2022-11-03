<template>
<!--  <div class="kuaiJiKeMuPopupContainer" v-show="visible" ref="kuaiJiKeMuPopupContainer"></div>-->
<!--  <Drawer-->
<!--    title="科目管理"-->
<!--    placement="right"-->
<!--    :closable="false"-->
<!--    v-model:visible="visible"-->
<!--    :get-container="kuaiJiKeMuPopupContainer"-->
<!--    width="500"-->
<!--    :wrap-style="{ position: 'absolute' }"-->
<!--    :after-visible-change="afterVisibleChange"-->
<!--  >-->
<!--    <div style="display: flex;flex-direction: row;align-items: center;margin-bottom: 20px">-->
<!--      <div><input placeholder="请输入科目名称" type="text" style="border:solid 1px #E5E5E5"/></div>-->
<!--      <Button>新增</Button>-->
<!--    </div>-->
<!--    <table>-->
<!--      <tbody>-->
<!--      <tr>-->
<!--        <td>key</td>-->
<!--        <td>label</td>-->
<!--        <td>操作</td>-->
<!--      </tr>-->
<!--      <template v-if="showCodeList2">-->
<!--        <tr-->
<!--          v-for="it in codeList.map(item=>({key:item.ccode,label:item.ccodeName,label2:item.id}))">-->
<!--          <td>{{ it.key }}</td>-->
<!--          <td>{{ it.label }}</td>-->
<!--          <td>-->
<!--            <Button>编辑</Button>-->
<!--            <Button>删除</Button>-->
<!--          </td>-->
<!--        </tr>-->

<!--      </template>-->
<!--      </tbody>-->
<!--    </table>-->

<!--  </Drawer>-->
</template>
<script setup>
import {Tooltip,Input, Select, Button, Radio, Drawer} from 'ant-design-vue';
import {
  SearchOutlined, PlusOutlined, SettingOutlined, CloseOutlined
} from '@ant-design/icons-vue'
function afterVisibleChange(){}

</script>

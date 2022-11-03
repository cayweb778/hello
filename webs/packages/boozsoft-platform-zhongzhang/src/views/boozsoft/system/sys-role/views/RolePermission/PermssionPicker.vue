<template>
  <div style="width:100%;border-radius: 4px;position: relative;height:100%;background:#f1f1f1;box-shadow: 0 0 4px #989898;margin-right:10px;padding:10px">
    <div class="platformManagerPageTabsGroup" style="padding-bottom:40px">
      <div class="platformManagerPageModelTitle">功能权限</div>
    </div>
    <!--        <div class="platformManagerPageTabsGroup" style="width:220px">-->
    <!--          <div style="margin-left:10px">NC应用</div>-->
    <!--          <div style="margin-left:10px">集成应用</div>-->
    <!--          <div style="margin-left:10px">外链应用</div>-->
    <!--        </div>-->
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


    <!--        <div class="saveBtn">-->
    <!--          <Button @click="updateOwnPlatform" type="primary">更新权限</Button>-->
    <!--        </div>-->
  </div>
</template>
<script setup>
import {message, Select, Checkbox, Button, Row, Col} from 'ant-design-vue'
import {CheckOutlined, LoadingOutlined} from '@ant-design/icons-vue';
import {inject, nextTick, onMounted, ref} from "vue";
import {savePlatforms} from "../../../../../../api/group/platform";
import {RoleMenuApi} from "../../../../../../api/boozsoft/group/RouteMenuApi";
import {findAll} from "/@/api/record/sys-role/data";
import {usePlatformsStoreWidthOut} from "/@/store/modules/platforms";


const CheckboxGroup = Checkbox.Group

const viewModel = inject('viewModel')
const thisRole = ref(1)
const roleList = ref([])
const platformChecked = ref([])
const platformLoading = ref(false)
const platformList = ref([])

async function updateOwnPlatform() {
  platformLoading.value = true
  nextTick(async () => {
    console.log(roleList.value)
    console.warn(
      '--------- 调试信息 ---------\n',
      '\n\n所需条件:', ['当前角色', '拥有的平台'],
      '\n',
      '\n\n\n\n\n--------- 当前角色 ---------', {
        id: thisRole.value,
        object: roleList.value.filter(it => it.value == thisRole.value)[0]
      },
      '\n\n\n\n\n--------- 拥有的平台 ---------', [platformChecked.value, platformChecked],
      '\n\n\n\n\n--------- 接口数据详情 ---------', null,
      '\n\n\n\n\n--------- 接口数据 ---------', {
        roleId: thisRole.value,
        platformCheckedIds: platformChecked.value.map(it => it)
      },
      '\n\n\n\n\n--------- APi ---------', [savePlatforms]
    )
    await RoleMenuApi.savePlatforms({
      roleId: viewModel.value.currentRole.id,
      menuIds: platformChecked.value
    })
    platformLoading.value = false
    message.success('模块保存成功')
  })

  // savePlatforms(platformChecked)
}



onMounted(async ()=>{
  platformList.value = (await usePlatformsStoreWidthOut().getPlatformListToNames()).map(it => {

    return {
      value: {
        ...it,
        id: parseInt(it.id)
      },
      hover: false,
      checked: false
    }
  })
  const roleList2 = (await findAll())
  roleList.value = roleList2.map(it => {
    return {
      key: it.id,
      label: it.roleName,
      value: it.id
    }
  })
})



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

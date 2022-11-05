<template>
  <div style="width:100%;border-radius: 4px;position: relative;height:100%;background:#f1f1f1;box-shadow: 0 0 4px #989898;margin-right:10px;padding:10px">
    <div class="platformManagerPageTabsGroup" style="padding-bottom:40px">
      <div class="platformManagerPageModelTitle">模块权限</div>
    </div>
    <!--        <div class="platformManagerPageTabsGroup" style="width:220px">-->
    <!--          <div style="margin-left:10px">NC应用</div>-->
    <!--          <div style="margin-left:10px">集成应用</div>-->
    <!--          <div style="margin-left:10px">外链应用</div>-->
    <!--        </div>-->
    <div style="position:absolute;top:10px;right:20px;">
      <div>
        <Button style="border-radius: 5px;padding:0 15px;font-size:10px"
                @click="updateOwnPlatform" type="primary">
          <LoadingOutlined v-if="platformLoading"/>
          <CheckOutlined v-else/>
          保存
        </Button>
      </div>
    </div>

    <div class="model_screen">筛选</div>
    <div class="model-checkAll" @click="checkAllFun($event)"> 选择所有
      <Checkbox v-model:checked="platformCheckAll"
                :indeterminate="platformIndeterminate"></Checkbox>
    </div>
    <CheckboxGroup :value="platformChecked" name="checkboxgroup"
                   class="this-checkboxgroup">
      <Row :gutter="[8,16]">
        <Col v-for="it in platformList" :span="4">
          <div class="box-item"
               :class="{
                      'box-item-checked':viewModel.currentPlatform.id==it.value.id,
                      'box-item-hover':it.hover==true
                    }"
               style="position:relative;"
               @mousemove="it.hover=true"
               @mouseleave="it.hover=false"
               @click="boxItemClick(it)"
          >

            <div class="box-item-checkbox">
              <Checkbox :value="it.value.id"></Checkbox>
            </div>
            <div class="box-item-title">{{ it.value.name.replaceAll(/【.*】/g, '') }}</div>
            <div class="box-item-enter">
              <Button class="box-item-enter-btn"
                      @click.stop="useMenuTree(it)">
                选择
              </Button>
            </div>
          </div>
        </Col>
        <Col :span="4">
          <div class="newBox box-item">
            <span> + </span>
          </div>
        </Col>
      </Row>
      <div class="no-box" v-for="it in 100"/>
    </CheckboxGroup>

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
</script>

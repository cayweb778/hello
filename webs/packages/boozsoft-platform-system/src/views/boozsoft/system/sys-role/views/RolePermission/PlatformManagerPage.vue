<template>
  <div class="platformManagerPage" style="padding:0;height:calc(100%)">
    <!--    <PermissionPage></PermissionPage>-->
    <!--    <div class="platformManagerPageBack" @click="viewModel.showPlatformManagerPage=false">返回</div>-->
    <div class="platformManagerPageContainer" style="display: flex;height:100%">
      <div class="platformManagerPageHeader"
           style="padding:10px;min-width:200px;height:100%;background:#f1f1f1;box-shadow: 0 0 4px #989898;margin-right:10px;border-radius:10px">
        <div class="platformManagerPageTabsGroup" style="padding-bottom:40px">
          <div class="platformManagerPageModelTitle">角色列表</div>
        </div>
        <!--        <Select :options="roleList" -->
        <!--                @change="roleSelectChange"-->
        <!--                v-model:value="thisRole"-->
        <!--                placeholder="选择角色"-->

        <div style="height:500px;margin-left: -21px;">
          <!--                value="开发账号"/>-->
          <BasicTree
            v-model:selectedKeys="treeSelectKeys"
            :treeData="treeData"
            :checkable="false"
            defaultExpandAll
          />
        </div>
      </div>
      <PlatformPicker></PlatformPicker>
<!--      <PermssionPicker></PermssionPicker>-->
      <div
        style="position: relative;width: 400px;background: #f1f1f1;box-shadow: 0 0 4px #989898;border-radius: 4px;height: 100%;overflow: hidden;padding:10px;">
        <div class="platformManagerPageTabsGroup" style="padding-bottom:40px">
          <div class="platformManagerPageModelTitle">菜单权限</div>
        </div>
        <div style="position:absolute;top:10px;right:20px;">
          <div>
            <Button style="border-radius: 5px;padding:0 15px;font-size:10px" @click="saveMenu"
                    type="primary">
              <LoadingOutlined v-if="menuLoading"/>
              <CheckOutlined v-else/>
              保存
            </Button>
          </div>
        </div>
        <BasicTree
          v-model:value="menuTreeSelectKeys"
          :treeData="menuTreeData"
          :checkable="true"
          defaultExpandAll
        />
      </div>
    </div>
  </div>
</template>
<script setup>
import {inject, onMounted, ref, computed, watch, nextTick} from "vue";
import {usePlatformsStoreWidthOut} from "/@/store/modules/platforms";
import {message, Select, Checkbox, Button, Row, Col} from 'ant-design-vue'
import PlatformPicker from './PlatformPicker.vue'
import PermssionPicker from './PermssionPicker.vue'
import {CheckOutlined, LoadingOutlined} from '@ant-design/icons-vue';
import {findAll} from '/@/api/record/sys-role/data';

const CheckboxGroup = Checkbox.Group
import {findOwnPlatforms, savePlatforms} from "../../../../../../api/group/platform";
import {useMessage} from "../../../../../../hooks/web/useMessage";
import {RoleMenuApi} from "/@/api/boozsoft/group/RouteMenuApi";
import {BasicTree} from '/@/components/Tree'
import {getUserMenuTreeByPlatformId2} from "/@/api/sys/user";
import PermissionPage from './PermissionPage.vue'

const thisRole = ref(1)
const viewModel = inject('viewModel')

const roleList = ref([])
const platformList = ref([])


const platformChecked = ref([])
const platformPlantOptions = ref([])
const platformIndeterminate = ref(true)
const platformCheckAll = ref(false)
watch(platformChecked, () => {
  platformIndeterminate.value = !!platformChecked.value.length && platformChecked.value.length < platformPlantOptions.value.length;
  platformCheckAll.value = platformChecked.value.length == platformPlantOptions.value.length;
})

function checkAllFun(e) {
  platformChecked.value = e.target.checked ? platformPlantOptions.value : []
  // if (platformIndeterminate.value) {
  //   platformChecked.value = []
  //   platformIndeterminate.value = false
  //   platformCheckAll.value = false
  // } else {
  //
  //   if (platformCheckAll.value) {
  //     platformChecked.value = []
  //     platformCheckAll.value = false
  //     platformIndeterminate.value = false
  //   } else {
  //     platformChecked.value = platformPlantOptions.value
  //   }
  // }
}

const newRoleList = ref([])
onMounted(async () => {
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
  useMenuTree(platformList.value[0])
  const roleList2 = (await findAll())
  roleList.value = roleList2.map(it => {
    return {
      key: it.id,
      label: it.roleName,
      value: it.id
    }
  })
  newRoleList.value = roleList2
  thisRole.value = roleList.value[0].value
  treeSelectKeys.value.push(roleList2[0].id)
  viewModel.value.currentRole = roleList2[0]
  const ownPlatformIds = await findOwnPlatforms()

  // platformList.value

  platformPlantOptions.value = platformList.value.map(it => parseInt(it.value.id))
  const data2 = await RoleMenuApi.findAll()
  const selectKeys2 = data2
    .filter(it => it.roleId == viewModel.value.currentRole.id)
  const selectKeys = selectKeys2
    .filter(it => platformPlantOptions.value.indexOf(parseInt(it.menuId)) != -1)
    .map(it => parseInt(it.menuId))
  platformChecked.value = selectKeys

  watch(treeSelectKeys, async (e) => {
    viewModel.value.currentRole = newRoleList.value.filter(it => it.id == e[0])[0]
    updateMenuTreeSelectKeys(viewModel.value.currentRole.id)
    const data2 = await RoleMenuApi.findAll()
    const selectKeys = data2
      .filter(it => it.roleId == viewModel.value.currentRole.id)
      .filter(it => platformPlantOptions.value.indexOf(parseInt(it.menuId)) != -1)
      .map(it => parseInt(it.menuId))
    platformChecked.value = selectKeys
  }, {immediate: true})
})


const plainOptions = ['Apple', 'Pear', 'Orange'];


function boxItemClick(it) {
  const indexOf = platformChecked.value.indexOf(it.value.id)
  if (indexOf != -1) {
    delete platformChecked.value[indexOf]
    platformChecked.value = platformChecked.value.filter(it => it != null)
  } else {
    platformChecked.value = [...platformChecked.value, it.value.id]
  }

}

function roleSelectChange(e) {
  const a = roleList.value.filter(it => it.key == e)[0]
  console.log(a)
}

const treeSelectKeys = ref([])

const treeData = computed(() => {
  return roleList.value.map(it => {
    return {
      key: it.key,
      title: it.label,
      value: it.key
    }
  })
})

async function getMenuTree(platformId) {
  const menus = (await getUserMenuTreeByPlatformId2({platformId}))
  const arr = []

  function toMenuModel(menu) {
    const obj = {
      ...menu,
      title: menu.meta.target.name,
      value: menu.meta.target.id,
      key: menu.meta.target.id,
    }
    return obj
  }

  function abc(menus, arr) {
    menus.forEach(it => {
      const obj = toMenuModel(it)
      arr.push(obj)
      if (it.children != null) {
        obj.children = []
        abc(it.children, obj.children)
      }
    })
  }

  abc(menus, arr)

  return arr
}

const menuTreeData = ref([])
const menuTreeSelectKeys = ref([])

function getMenuIds(treeData) {
  const menuIds = []

  function abc(arr) {
    arr.forEach(it => {
      menuIds.push(it.id)
      if (it.children != null) {
        abc(it.children)
      }
    })
  }

  abc(treeData.value)
  return menuIds
}

async function updateMenuTreeSelectKeys(roleId) {
  const menuIds = getMenuIds(menuTreeData)
  menuTreeSelectKeys.value = []
  nextTick(async () => {
      const roleMenuApiAll = await RoleMenuApi.findAll()
      const abc = roleMenuApiAll
        .filter(it => it.roleId == roleId)


      menuTreeSelectKeys.value = abc.filter(it => menuIds.indexOf(it.menuId) != -1)
        .map(it => it.menuId)
    }
  )
  return menuIds
}

function openPermissionSetting() {

}

async function useMenuTree(platform) {
  // currentPlatform.value = platform
  menuTreeData.value = await getMenuTree(platform.value.id)
  updateMenuTreeSelectKeys(viewModel.value.currentRole.id)
  console.log(platform.value.id)
  viewModel.value.currentPlatform = platform.value
}


function saveMenu() {
  menuLoading.value = true
  nextTick(async () => {
    console.warn('接口数据', menuTreeSelectKeys.value)
    await RoleMenuApi.saveMenus({
      roleId: viewModel.value.currentRole.id,
      menuIds: menuTreeSelectKeys.value
    })
    menuLoading.value = false
    message.success('菜单保存成功')
  })

}

const platformLoading = ref(false)
const menuLoading = ref(false)

</script>
<style scoped>
.this-checkboxgroup {
  width: 100%
}

.platformManagerPage {
  width: 100%;
  border-radius: 4px;
  padding: 10px
}

.platformManagerPageHeader {
  height: 50px;
}

.platformManagerPageBack {
  position: absolute;
  right: 10px;
  top: 10px;
  height: 50px;
  font-size: 10px;
}

.platformManagerPageTabsGroup {
  display: flex;
  height: 30px;;
  margin-bottom: 30px;
  border-bottom: 1px solid;
}

.model_screen {
  text-align: right;
  font-size: 10px;
  height: 20px;
}

.saveBtn {
  text-align: center;
  margin-top: 20px;
}

.box-item {
  position: relative;
  height: 100px;
  border-radius: 4px;
  border: solid 1px gray;
  cursor: pointer;
  box-shadow: 0 0 4px #989898;
}

.box-item-checkbox {
  position: absolute;
  right: 5px;
  top: 5px
}

.box-item-title {
  width: 100%;
  bottom: 26px;
  text-align: center;
}

.box-item-enter {
  position: absolute;
  width: 100%;
  bottom: 5px;
  text-align: center;
  font-size: 10px
}

.box-item-enter-btn {
  padding: 0px 20px;
  font-size: 10px;
  height: 20px
}

.newBox {
  height: 100px;
}

.newBox span {
  font-size: 70px;
  position: absolute;
  top: -6px;
  left: calc((100% - 40px) / 2);
}

.no-box {
  width: 0px;
  height: 0px;
}

.model-checkAll {
  margin-bottom: 15px;
}

.platformManagerPageModelTitle {
  font-weight: 900;
  padding-top: 6px
}
</style>
<style scoped>
:deep(.vben-basic-tree) {
  background: inherit
}

.box-item-checked {
  background: #a0effa;
  color: black
}


.box-item-hover {
  background: #e6fdff !important;
  color: black
}
</style>

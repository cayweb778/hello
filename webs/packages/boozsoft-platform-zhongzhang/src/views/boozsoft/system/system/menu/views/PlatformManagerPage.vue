<template>
  <div style="width:100%;border-radius:10px;padding:10px">
    <div style="height:50px;; ">
      <div style="display: flex">
        <div>
          <Select :options="roleList" v-model:value="thisRole" placeholder="选择角色"
                  value="开发账号"></Select>
        </div>
      </div>
    </div>
    <div style="height:50px;; ">
      <div style="display: flex">
        <div>权限</div>
        <div style="margin-left:10px">应用</div>
      </div>
    </div>
    <div style="position:relative;height:50px;; ">
      <div style="position:absolute;right:0;font-size:10px;display: flex">
        <div>筛选</div>
      </div>
    </div>
    <CheckboxGroup v-model:value="ownPlatforms" name="checkboxgroup" style="width:100%">
      <Row :gutter="[8,16]">
        <Col  v-for="it in platformList" :span="4">
            <div style="position:relative;height:100px;border-radius:20px;border:solid 1px black">
              <div style="position:absolute;right:5px;top:5px">
                <Checkbox :value="it"></Checkbox>
              </div>
              <div style="width:100%;bottom:26px;text-align: center;">

                {{ it.value.name.replaceAll(/【.*】/g, '')}}
              </div>
              <div style="position:absolute;width:100%;bottom:5px;text-align: center;font-size:10px">
                <Button style="padding:0px 20px;font-size:10px;height:20px"
                        @click="viewModel.openPlatformManager(it.value)">选择
                </Button>
              </div>
            </div>
        </Col>
        <Col :span="4">
          <div style="height:100px;border-radius:20px;border:solid 1px black">
          <span style="font-size: 70px;position: absolute;top: -4px;left: 32px;"> + </span>
          </div>
        </Col>
      </Row>
<!--      <div v-for="it in platformList" @click="it.checked=!it.checked"-->
<!--           class="platform-check"-->
<!--           >-->

<!--      </div>-->


      <div v-for="it in 100" style=" width: 0px;height: 0px;"></div>
    </CheckboxGroup>
    <div style="text-align:center;">
      <Button @click="updateOwnPlatform" type="primary">更新权限</Button>
    </div>
  </div>
</template>
<script setup>
import {inject, onMounted, ref,computed} from "vue";
import {usePlatformsStoreWidthOut} from "/@/store/modules/platforms";
import {message, Select, Checkbox, Button,Row,Col} from 'ant-design-vue'
import {
  delRole,
  findAll, saveRole
} from '/@/api/record/sys-role/data';

const CheckboxGroup = Checkbox.Group
import {findOwnPlatforms, savePlatforms} from "../../../../../../api/group/platform";
import {useMessage} from "../../../../../../hooks/web/useMessage";


const thisRole = ref(1)
const viewModel = inject('viewModel')

const roleList = ref([])
const platformList = computed(()=>{
  console.log(333)
  return viewModel.value.platformList.map(it => {

    return {
      value: it,
      checked: false
    }
  })
})


const ownPlatforms = ref([])

onMounted(async () => {
  const ownPlatformIds=await findOwnPlatforms()
  // platformList.value
  ownPlatforms.value = platformList.value.filter(it=>ownPlatformIds.indexOf(it.value.id))
  roleList.value = (await findAll()).map(it => {
    return {
      key: it.id,
      label: it.roleName,
      value: it.id
    }
  })
  thisRole.value=roleList.value[0].value
})


const plainOptions = ['Apple', 'Pear', 'Orange'];

function updateOwnPlatform() {
  message.warning('开发2中...');
  console.log(roleList.value)
  console.warn(
    '--------- 调试信息 ---------\n',
    '\n\n所需条件:',['当前角色','拥有的平台'],
    '\n',
    '\n\n\n\n\n--------- 当前角色 ---------',{id:thisRole.value,object:roleList.value.filter(it=>it.value==thisRole.value)[0]},
    '\n\n\n\n\n--------- 拥有的平台 ---------',[ownPlatforms.value,ownPlatforms.value.map(it => it.value.id)],
    '\n\n\n\n\n--------- 接口数据详情 ---------',null,
    '\n\n\n\n\n--------- 接口数据 ---------',{roleId:thisRole.value,ownPlatformsIds:ownPlatforms.value.map(it => it.value.id)},
    '\n\n\n\n\n--------- APi ---------',[savePlatforms]
  )


  // savePlatforms(ownPlatforms)
}

</script>

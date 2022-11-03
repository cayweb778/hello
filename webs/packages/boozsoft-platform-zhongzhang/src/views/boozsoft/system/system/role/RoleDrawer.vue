<template>
  <BasicDrawer
    v-bind="$attrs"
    @register="registerDrawer"
    showFooter
    :title="getTitle"
    width="600px"
    @ok="handleSubmit"
  >
    <BasicForm @register="registerForm">
      <template #menu="{ model, field }">
         <div style="display: flex;margin-left:-90px">
          <div style="width:50%">
            平台分配<br>
            <CheckboxGroup>
            <Tabs type="line" tab-position="left" @change="platformChoose">
              <template :key="platform.id"  v-for="platform in platformListToNames">
                <TabPane  >
                  <template #tab>
                    <Checkbox :value="platform.name" @click="changeMenuTitle(platform.id ,platform.name)" >{{platform.name}}</Checkbox>
                  </template>
                  <div style="width:100%;height:1000px;"></div>
                </TabPane>
              </template>
            </Tabs>
            </CheckboxGroup>
          </div>
         <div style="width:50%">
           <span style="color:green">注:可跨平台菜单，分配到当前菜单</span><br>
           平台：{{menuPlatformName}}<br>
           菜单分配：
           <Select v-model:value="menuPlatformId" @change="platformChoose" style="width:200px">
             <Option :value="platform.id" v-for="platform in platformListToNames">{{platform.name}}</Option>
           </Select>
           <BasicTree
             style="height: 400px"
             v-model:value="abc"
             :treeData="treeData"
             :replaceFields="{ title: 'name', key: 'id' }"
             :checked-keys="abc"
             checkable
             toolbar
           />
         </div>
         </div>
      </template>
    </BasicForm>
  </BasicDrawer>
</template>
<script lang="ts" setup>
import {GetMenuTree, GetMenuTree2, getMenuTreeByPlatformId} from '/@/api/sys/menu';
import {getUserMenuTreeByPlatformId} from '/@/api/sys/user';
import {usePlatformsStoreWidthOut} from '/@/store/modules/platforms';
import {defineComponent, ref, computed, unref, onMounted, getCurrentInstance} from 'vue';
  import { BasicForm, useForm } from '/@/components/Form/index';
  import { formSchema } from './role.data';
import {Checkbox, Select, Tabs} from 'ant-design-vue';
const CheckboxGroup=Checkbox.Group
const TabPane=Tabs.TabPane
  import { BasicDrawer, useDrawerInner } from '/@/components/Drawer';
import {BasicTree} from '/@/components/Tree';

  import { getMenuList } from '/@/api/sys/system';
  const Option=Select.Option
const abc=ref()
const  platformListToNames=ref([]);

(async ()=>{
  platformListToNames.value=await usePlatformsStoreWidthOut().getPlatformListToNames()

})()

const emit=defineEmits(['success', 'register'])
const isUpdate = ref(true);
const treeData = ref([]);

const [registerForm, { resetFields, setFieldsValue, validate }] = useForm({
  labelWidth: 90,
  schemas: formSchema,
  showActionButtonGroup: false,
});

const [registerDrawer, { setDrawerProps, closeDrawer }] = useDrawerInner(async (data) => {
  resetFields();
  setDrawerProps({ confirmLoading: false });
  // 需要在setFieldsValue之前先填充treeData，否则Tree组件可能会报key not exist警告
  if (unref(treeData).length === 0) {
    // treeData.value = (await GetMenuTree2()) as any ;
  }
  isUpdate.value = !!data?.isUpdate;
  // abc.value=["1413809719050637312", "1413809719365210112", "1413809719474262016", "1413809719583313920", "1413809719688171520", "1413809719797223424", "1413809719902081024", "1413809720011132928", "1413809720115990528", "1413809720225042432", "1413809720329900032", "1413809720434757632", "1413809720535420928" ]

  if (unref(isUpdate)) {
    setFieldsValue({
      ...data.record,
      menu:['0','1']
    });
  }
});

const getTitle = computed(() => (!unref(isUpdate) ? '新增角色' : '编辑角色'));
const menuPlatformId=ref()
const menuPlatformName=ref()
async function handleSubmit() {
  try {
    const values = await validate();
    setDrawerProps({ confirmLoading: true });
    // TODO custom api
    console.log(values);
    closeDrawer();
    emit('success');
  } finally {
    setDrawerProps({ confirmLoading: false });
  }
}

async function choosePlatform(){
  treeData.value=await getMenuTreeByPlatformId({platformId:1001})
  // alert(a)
}
async function platformChoose(platformId){
  treeData.value=await getMenuTreeByPlatformId({platformId})

}
function changeMenuTitle(id,name) {
  menuPlatformId.value=id
  menuPlatformName.value=name
}
</script>

<template>
  <div>


    <div
      style="text-align: center;width:900px;margin:0 auto;margin-top:100px;display:flex;justify-content: space-between">
      <div class="asdsad">
        菜单数据<br>
        <Button @click="exportMenus">导出</Button>
        <br>
        <Upload
          v-model:file-list="fileList"
          :remove="handleRemove" :before-upload="beforeUpload"
          @change="importMenuData2"
          name="file"
        >
          <Button>
            <UploadOutlined></UploadOutlined>
            上传数据
          </Button>
        </Upload>
      </div>

      <div class="asdsad">
        角色菜单数据<br>
        <Button @click="exportRoleMenus">导出</Button>
        <br>
        <Upload
          v-model:file-list="fileList"
          :remove="handleRemove" :before-upload="beforeUpload"
          @change="importRoleMenuData"
          name="file"
        >
          <Button>
            <UploadOutlined></UploadOutlined>
            上传数据
          </Button>
        </Upload>
      </div>

      <div class="asdsad">
        角色数据<br>
        <Button disabled="disabled" @click="exportRoles">导出</Button>
        <br>
        <Button disabled="disabled" @click="importRoleData">上传数据</Button>
      </div>
    </div>

  </div>
</template>
<script setup>
import {
  importMenuDataApi,
  importRoleApi,
  importRoleMenuDataApi
} from "../../../../api/data_manager";
import {ref} from 'vue'
import {Upload, Button} from 'ant-design-vue'
import aaa from 'file-saver'
import {UploadOutlined, DownloadOutlined} from '@ant-design/icons-vue'
import {sysMenuQueryAll} from "/@/utils/boozsoft/rbac/menuData";
import {getMockRoleMenus} from "../../../../utils/boozsoft/rbac/roleData";
import {getMenuMockData2} from "../../../../utils/boozsoft/rbac/utils/roleDataUtils";
function fileToText(file) {

  return new Promise((r) => {
    const fr = new FileReader()
    fr.onload = function () {
      if (fr.result) {
        r(fr.result)
      }
    }
    fr.readAsText(file)
  })
}


async function importMenuData2(menuData) {
  const text = await fileToText(menuData.file)
  const arrJSON = JSON.stringify(JSON.parse(text).filter(it=>it!=null).map(item => {
    item.layoutId = item.platformId
    return item
  }))
  await importMenuDataApi({json: arrJSON})
  alert('上传成功，已覆盖菜单数据')
}

async function importRoleMenuData(menuData) {
  const text = await fileToText(menuData.file)
  console.log(JSON.parse(text))
  const arrJSON = JSON.stringify(JSON.parse(text).map(item => {
    item.layoutId = item.platformId
    return item
  }))
  await importRoleMenuDataApi({json: arrJSON})
  alert('上传成功，已覆盖角色菜单数据')
}

function importRoleData() {
  const arrJSON = JSON.stringify([])
  importRoleApi({json: arrJSON})
}


function exportRoles() {
  const [roleMenusModel, roles] = getMenuMockData2()
  const roleMenuData = JSON.stringify(roles)
  var blob = new Blob([roleMenuData], {type: "text/plain;charset=utf-8"});
  aaa.saveAs(blob, "roleData.txt")

}

async function exportRoleMenus() {
  const roleMenuData = JSON.stringify(await getMockRoleMenus())
  var blob = new Blob([roleMenuData], {type: "text/plain;charset=utf-8"});
  aaa.saveAs(blob, "roleMenuData.txt")

}

async function exportMenus() {
  const menuData = JSON.stringify(sysMenuQueryAll().menus)

  var blob = new Blob([menuData], {type: "text/plain;charset=utf-8"});
  aaa.saveAs(blob, "menudata.txt")
  // await syncMenuAndRoleMenuApi({
  //   menuList: sysMenuQueryAll(),
  //   roleMenuList: roleMenus(),
  // });
}

const fileList = ref([])
const headers = ref()

function abc() {
  return true
}

const handleRemove = (file) => {
  const index = fileList.value.indexOf(file);
  const newFileList = fileList.value.slice();
  newFileList.splice(index, 1);
  fileList.value = newFileList;
};

const beforeUpload = (file) => {
  fileList.value = [...fileList.value, file];
  return false;
};
</script>
<style>
.asdsad {
  border-radius: 15px;
  width: 200px;
  height: 100px;
  background: yellow
}
</style>

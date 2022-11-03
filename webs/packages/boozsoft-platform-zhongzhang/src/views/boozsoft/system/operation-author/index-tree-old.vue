<template>
  <div>
    <div class="app-container">
      <div class="app-container-head">
        <div class="container-head-title">
          <UnorderedListOutlined style="margin: 0 10px;"/>
          <b class="noneSpan">操作员权限设置</b>
        </div>
        <div class="ant-btn-group" style="float: right">
          <!--          <button type="button" class="ant-btn ant-btn-me">查询</button>-->
          <button type="button" class="ant-btn ant-btn-me"
                  :style="showOk?{}:{pointerEvents: 'none'}" @click="saveData">保存
          </button>
          <button type="button" class="ant-btn ant-btn-me"
                  :style="showOk?{}:{pointerEvents: 'none'}" @click="emptyCurrentData">清空
          </button>
          <button type="button" class="ant-btn ant-btn-me" @click="openCopy">复制</button>
<!--                    <button type="button" class="ant-btn ant-btn-me" @click="initMeun">初始化菜单</button>-->
          <button type="button" class="ant-btn ant-btn-me" @click="closeCurrent()">退出</button>
        </div>
      </div>
      <div class="app-container-neck">
        <div style="float:left;">
          <AccountPicker theme="one" @reloadTable="dynamicAdReload"/>
        </div>
        <div style="display: inline-block;float: left;margin-left: 2%;">
          <span>操作员：</span>
          <a-select style="width: 120px;" v-model:value="saveDataModel.userUniqueCode"
                    @change="operChange">
            <template v-for="item in operList">
              <a-select-option :value="item.id">
                {{ item.realName }}
              </a-select-option>
            </template>
          </a-select>
        </div>
        <div style="float: right; margin-left: 10px">
          <a-button class="ant-btn-me" @click="pageReload">
            <SyncOutlined :style="{ fontSize: '14px' }"/>
          </a-button>
          <a-popover placement="bottom">
            <template #content>
              <!--
                            <a-popconfirm
                              ok-text="保存"
                              cancel-text="关闭"
                              @confirm="confirm"
                              @cancel="cancel">
                              <template #icon><b>栏目设置</b><br></template>
                              <template #title>
                                <div style="width: 640px">
                                  <a-table bordered :data-source="dynamicColumnData" :columns="dynamicColumns"
                                           childrenColumnName="children" :pagination="false"
                                           style="max-height: 500px;overflow-y: auto" class="lanmu-table">
                                    <template #checkBox="{ text, record }">
                                      <a-checkbox v-model:checked="record.check" :disabled="record.isFixed"  />
                                    </template>
                                    <template #widthInput="{ text, record }">
                                      <div class="editable-cell">
                                        <div v-if="editableData[record.key]" class="editable-cell-input-wrapper">
                                          <a-input type="number" v-model:value="editableData[record.key].width"
                                                   @pressEnter="save(record.key,record.min,record.max)"
                                                   style="width: 80px"  />
                                          <check-outlined class="editable-cell-icon-check"
                                                          @click="save(record.key,record.min,record.max)"  />
                                        </div>
                                        <div v-else class="editable-cell-text-wrapper">
                                          {{ text || ' ' }}
                                          <edit-outlined class="editable-cell-icon" @click="edit(record.key)"  />
                                          <span style="float: right;">{{ record.min + '~' + record.max }}</span>
                                        </div>
                                      </div>
                                    </template>
                                    <template #nameInput="{ text, record }">
                                      <div class="editable-cell">
                                        <div v-if="editableData[record.key]" class="editable-cell-input-wrapper">
                                          <a-input type="text" v-model:value="editableData[record.key].nameNew"
                                                   @pressEnter="saveName(record.key)" style="width: 100px"  />
                                          <check-outlined class="editable-cell-icon-check"
                                                          @click="saveName(record.key)"  />
                                        </div>
                                        <div v-else class="editable-cell-text-wrapper">
                                          {{ text || ' ' }}
                                          <edit-outlined class="editable-cell-icon" @click="edit(record.key)"  />
                                        </div>
                                      </div>
                                    </template>
                                    <template #alignRadio="{ text, record }">
                                      <a-radio-group default-value="a" size="small" v-model:value="record.align"
                                                     :disabled="record.align==''">
                                        <a-radio-button value="left">
                                          左
                                        </a-radio-button>
                                        <a-radio-button value="center">
                                          中
                                        </a-radio-button>
                                        <a-radio-button value="right">
                                          右
                                        </a-radio-button>
                                      </a-radio-group>
                                    </template>
                                  </a-table>
                                </div>
                              </template>
                              <a-button style="width: 120px;margin-bottom: 2px">栏目设置</a-button>
                            </a-popconfirm>
                            <br/>-->
              <span class="group-btn-span-special2" @click="pageParameter.showRulesSize = 'MAX'"
                    :style="pageParameter.showRulesSize==='MAX'?{backgroundColor: '#0096c7',color: 'white'}:''">
                <SortDescendingOutlined/>&nbsp;大号字体&ensp;<CheckOutlined
                v-if="pageParameter.showRulesSize==='MAX'"/></span><br/>
              <span class="group-btn-span-special2" @click="pageParameter.showRulesSize = 'MIN'"
                    :style="pageParameter.showRulesSize==='MIN'?{backgroundColor: '#0096c7',color: 'white'}:''">
                <SortAscendingOutlined/>&nbsp;小号字体&ensp;<CheckOutlined
                v-if="pageParameter.showRulesSize==='MIN'"/></span>
            </template>
            <a-button>
              <SettingFilled :style="{ fontSize: '14px' }"/>
            </a-button>
          </a-popover>
        </div>
        <div class="act-two-d-right">
          <div class="acttd-right-d-search">
            <a-select v-model:value="pageParameter.searchConditon.requirement"
                      class="acttdrd-search-select">
              <a-select-option :value="'functionName'">功能菜单</a-select-option>
            </a-select>

            <a-input-search
              style="width: 180px;"
              v-model:value="pageParameter.searchConditon.value"
              class="acttdrd-search-input"
              @search="onSearch"
            />
          </div>
        </div>
      </div>
    </div>
    <div class="app-container">
      <PageWrapper dense content-full-height fixed-height content-class="flex">
        <LeftTree @select="handleSelect"/>
        <AuthorCopy @register="registerAuthorCopy" @log="copyCallback"/>
        <div style="width: calc(100% - 250px);">
          <BasicTable
            ref="tableRef"
            :row-selection="{ type: 'checkbox',fixed: true, selectedRowKeys: tableSelectedRowKeys, onSelect: onSelectChange,onSelectAll: onSelectChange2 }"
            :rowKey="r=>r.functionId"
            :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
            :scroll="{ x: totalColumnWidth,y: windowHeight }"
            :loading="loadMark"
            @register="registerTable"
          >
            <template #isSee="{record}">
              <div v-if="isShow(record,'isSee')">
                <a-checkbox v-model:checked="record.isSee" @change="rowChange(record,true)"/>
              </div>
            </template>
            <template #isEdit="{record}">
              <div v-if="isShow(record,'isEdit')">
                <a-checkbox v-model:checked="record.isEdit" @change="rowChange(record)"/>
              </div>
            </template>
            <template #isAdd="{record}">
              <div v-if="isShow(record,'isAdd')">
                <a-checkbox v-model:checked="record.isAdd" @change="rowChange(record)"/>
              </div>
            </template>
            <template #isChange="{record}">
              <div v-if="isShow(record,'isChange')">
                <a-checkbox v-model:checked="record.isChange" @change="rowChange(record)"/>
              </div>
            </template>
            <template #isDel="{record}">
              <div v-if="isShow(record,'isDel')">
                <a-checkbox v-model:checked="record.isDel" @change="rowChange(record)"/>
              </div>
            </template>
            <template #isOprat="{record}">
              <div v-if="isShow(record,'isOprat')">
                <a-checkbox v-model:checked="record.isOprat" @change="rowChange(record)"/>
              </div>
            </template>
            <template #isInsert="{record}">
              <div v-if="isShow(record,'isInsert')">
                <a-checkbox v-model:checked="record.isInsert" @change="rowChange(record)"/>
              </div>
            </template>
            <template #isSign="{record}">
              <div v-if="isShow(record,'isSign')">
                <a-checkbox v-model:checked="record.isSign" @change="rowChange(record)"/>
              </div>
            </template>
            <template #isCancel="{record}">
              <div v-if="isShow(record,'isCancel')">
                <a-checkbox v-model:checked="record.isCancel" @change="rowChange(record)"/>
              </div>
            </template>
            <template #isTovoid="{record}">
              <div v-if="isShow(record,'isTovoid')">
                <a-checkbox v-model:checked="record.isTovoid" @change="rowChange(record)"/>
              </div>
            </template>
            <template #isMake="{record}">
              <div v-if="isShow(record,'isMake')">
                <a-checkbox v-model:checked="record.isMake" @change="rowChange(record)"/>
              </div>
            </template>
            <template #isImport="{record}">
              <div v-if="isShow(record,'isImport')">
                <a-checkbox v-model:checked="record.isImport" @change="rowChange(record)"/>
              </div>
            </template>
            <template #isExport="{record}">
              <div v-if="isShow(record,'isExport')">
                <a-checkbox v-model:checked="record.isExport" @change="rowChange(record)"/>
              </div>
            </template>
            <template #isPrint="{record}">
              <div v-if="isShow(record,'isPrint')">
                <a-checkbox v-model:checked="record.isPrint" @change="rowChange(record)"/>
              </div>
            </template>
            <template #isEmail="{record}">
              <div v-if="isShow(record,'isEmail')">
                <a-checkbox v-model:checked="record.isEmail" @change="rowChange(record)"/>
              </div>
            </template>
            <template #isColumn="{record}">
              <div v-if="isShow(record,'isColumn')">
                <a-checkbox v-model:checked="record.isColumn" @change="rowChange(record)"/>
              </div>
            </template>
            <template #isStart="{record}">
              <div v-if="isShow(record,'isStart')">
                <a-checkbox v-model:checked="record.isStart" @change="rowChange(record)"/>
              </div>
            </template>
            <template #isStop="{record}">
              <div v-if="isShow(record,'isStop')">
                <a-checkbox v-model:checked="record.isStop" @change="rowChange(record)"/>
              </div>
            </template>
            <template #isSetting="{record}">
              <div v-if="isShow(record,'isSetting')">
                <a-checkbox v-model:checked="record.isSetting" @change="rowChange(record)"/>
              </div>
            </template>
            <template #isAnnexSee="{record}">
              <div v-if="isShow(record,'isAnnexSee')">
                <a-checkbox v-model:checked="record.isAnnexSee" @change="rowChange(record)"/>
              </div>
            </template>
            <template #isAnnexEdit="{record}">
              <div v-if="isShow(record,'isAnnexEdit')">
                <a-checkbox v-model:checked="record.isAnnexEdit" @change="rowChange(record)"/>
              </div>
            </template>
            <template #isAnnexChange="{record}">
              <div v-if="isShow(record,'isAnnexChange')">
                <a-checkbox v-model:checked="record.isAnnexChange" @change="rowChange(record)"/>
              </div>
            </template>
            <template #isRecycle="{record}">
              <div v-if="isShow(record,'isRecycle')">
                <a-checkbox v-model:checked="record.isRecycle" @change="rowChange(record)"/>
              </div>
            </template>
            <template #isSave="{record}">
              <div v-if="isShow(record,'isSave')">
                <a-checkbox v-model:checked="record.isSave" @change="rowChange(record)"/>
              </div>
            </template>
            <template #isCashierSign="{record}">
              <div v-if="isShow(record,'isCashierSign')">
                <a-checkbox v-model:checked="record.isCashierSign" @change="rowChange(record)"/>
              </div>
            </template>
            <template #isCashierCancel="{record}">
              <div v-if="isShow(record,'isCashierCancel')">
                <a-checkbox v-model:checked="record.isCashierCancel" @change="rowChange(record)"/>
              </div>
            </template>
            <template #isSupervisorSign="{record}">
              <div v-if="isShow(record,'isSupervisorSign')">
                <a-checkbox v-model:checked="record.isSupervisorSign" @change="rowChange(record)"/>
              </div>
            </template>
            <template #isSupervisorCancel="{record}">
              <div v-if="isShow(record,'isSupervisorCancel')">
                <a-checkbox v-model:checked="record.isSupervisorCancel"
                            @change="rowChange(record)"/>
              </div>
            </template>
            <template #isBook="{record}">
              <div v-if="isShow(record,'isBook')">
                <a-checkbox v-model:checked="record.isBook" @change="rowChange(record)"/>
              </div>
            </template>
            <template #isJoin="{record}">
              <div v-if="isShow(record,'isJoin')">
                <a-checkbox v-model:checked="record.isJoin" @change="rowChange(record)"/>
              </div>
            </template>
            <template #isCarryOver="{record}">
              <div v-if="isShow(record,'isCarryOver')">
                <a-checkbox v-model:checked="record.isCarryOver" @change="rowChange(record)"/>
              </div>
            </template>
            <template #isCheck="{record}">
              <div v-if="isShow(record,'isCheck')">
                <a-checkbox v-model:checked="record.isCheck" @change="rowChange(record)"/>
              </div>
            </template>
            <template #isAmount="{record}">
              <div v-if="isShow(record,'isAmount')">
                <a-checkbox v-model:checked="record.isAmount" @change="rowChange(record)"/>
              </div>
            </template>
            <template #isCopy="{record}">
              <div v-if="isShow(record,'isCopy')">
                <a-checkbox v-model:checked="record.isCopy" @change="rowChange(record)"/>
              </div>
            </template>
          </BasicTable>
        </div>
      </PageWrapper>
    </div>
  </div>
</template>

<script setup lang="ts">
import {getCurrentAccountName, hasBlank} from '/@/api/task-api/tast-bus-api';
import {PageWrapper} from '/@/components/Page'
import {BasicTable, useTable, TableAction} from '/@/components/Table';
import {useModal} from '/@/components/Modal';
import {onMounted, provide, reactive, ref, unref} from 'vue';

const {closeCurrent} = useTabs(router);
import {useMessage} from '/@/hooks/web/useMessage';
import {
  SettingFilled,
  SyncOutlined,
  SortAscendingOutlined, CheckOutlined, SortDescendingOutlined, UnorderedListOutlined
} from '@ant-design/icons-vue';
import {
  Col as ACol,
  Popover as APopover,
  Input as AInput,
  Select as ASelect,
  Checkbox as ACheckbox,
  Row as ARow,
  Card as ACard, Popconfirm as APopconfirm,
  List as AList, message, Radio as ARadio, Table as ATable, Tag
} from 'ant-design-vue'
import LeftTree from './popup/LeftTree.vue';

const AInputSearch = AInput.Search
const ASelectOption = ASelect.Option
const AListItem = AList.Item
const ACheckboxGroup = ACheckbox.Group
const ARadioButton = ARadio.Button
const ARadioGroup = ARadio.Group
import {
  findAll,
  saveAuthor,
  findUserAuthor,
  getPlatformColumn,
  savePlatformColumn,
  getPlatformAndMenu, savePlatformAndMenu, emptyAuthor
} from '/@/api/caozuoyuan/caozuoyuan';
import {useTabs} from "/@/hooks/web/useTabs";
import router from "/@/router";
import {usePlatformsStore} from "/@/store/modules/platforms";
import {assemblyDynamicColumn, initDynamics} from "./data";
import {useUserStoreWidthOut} from "/@/store/modules/user";
import {findDbLanMuList, saveLanMuList} from "/@/api/record/system/accvoucher";
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker-DATA.vue";
import {cloneDeep} from "lodash-es";
import {getAllPlatform} from '/@/api/sys/menu';
import {JsonTool} from "/@/api/task-api/tools/universal-tools";
import AuthorCopy from './popup/AuthorCopy.vue';
import {
  getTheColumnOfTheSpecifiedPlatform
} from "/@/views/boozsoft/system/operation-author/column-data";
import {
  getTheMenuOfTheSpecifiedPlatform
} from "/@/views/boozsoft/system/operation-author/menu-data";
import {object} from "vue-types";
import {saveLog} from "/@/api/record/system/group-sys-login-log";

const CrudApi = {
  columns: [
    {
      title: '功能菜单',
      dataIndex: 'functionName',
      ellipsis: true, width: 150, align: 'left'
      // slots: {customRender: 'functionName'},
    },
    {
      title: '查看',
      dataIndex: 'isSee',
      ellipsis: true,
      slots: {customRender: 'isSee'},
    }, {
      title: '编辑',
      dataIndex: 'isEdit',
      ellipsis: true,
      slots: {customRender: 'isEdit'},
    }, {
      title: '新增',
      dataIndex: 'isAdd',
      ellipsis: true,
      slots: {customRender: 'isAdd'},
    }, {
      title: '修改',
      dataIndex: 'isChange',
      ellipsis: true,
      slots: {customRender: 'isChange'},
    }, {
      title: '删除',
      dataIndex: 'isDel',
      ellipsis: true,
      slots: {customRender: 'isDel'},
    }, {
      title: '操作',
      dataIndex: 'isOprat',
      ellipsis: true,
      slots: {customRender: 'isOprat'},
    }, {
      title: '插入',
      dataIndex: 'isInsert',
      ellipsis: true,
      slots: {customRender: 'isInsert'},
    }, {
      title: '审核/签字',
      dataIndex: 'isSign',
      ellipsis: true,
      slots: {customRender: 'isSign'},
    }, {
      title: '弃审/取消',
      dataIndex: 'isCancel',
      ellipsis: true,
      slots: {customRender: 'isCancel'},
    }, {
      title: '作废',
      dataIndex: 'isTovoid',
      ellipsis: true,
      slots: {customRender: 'isTovoid'},
    }, {
      title: '生单',
      dataIndex: 'isMake',
      ellipsis: true,
      slots: {customRender: 'isMake'},
    }, {
      title: '导入',
      dataIndex: 'isImport',
      ellipsis: true,
      slots: {customRender: 'isImport'},
    }, {
      title: '导出',
      dataIndex: 'isExport',
      ellipsis: true,
      slots: {customRender: 'isExport'},
    }, {
      title: '打印',
      dataIndex: 'isPrint',
      ellipsis: true,
      slots: {customRender: 'isPrint'},
    }, {
      title: '发送邮件',
      dataIndex: 'isEmail',
      ellipsis: true,
      slots: {customRender: 'isEmail'},
    }, {
      title: '栏目设置',
      dataIndex: 'isColumn',
      ellipsis: true,
      slots: {customRender: 'isColumn'},
    }, {
      title: '启用/生效',
      dataIndex: 'isStart',
      ellipsis: true,
      slots: {customRender: 'isStart'},
    }, {
      title: '停用/失效',
      dataIndex: 'isStop',
      ellipsis: true,
      slots: {customRender: 'isStop'},
    }
  ],
};
const totalNumber = ref(0)
// 这是示例组件
const [registerTable, {setTableData, getColumns, setColumns, reload, getDataSource}] = useTable({
  columns: CrudApi.columns,
  bordered: true,
  showIndexColumn: true,
  indexColumnProps: {width: 60, fixed: 'left'},
  /*  showTableSetting: true,*/
  /*  tableSetting: {
      redo: true,
      size: false,
      setting: true,
      fullScreen: false
    },*/
  pagination: false/*{
    pageSize: 100,
    showSizeChanger: true,
    pageSizeOptions: ['100', '150'],
    showTotal: (t) => {
      totalNumber.value = t
      return `共${t}条记录`
    }
  },*/
  /*  actionColumn: {
      width: 160,
      title: '操作',
      dataIndex: 'action',
      slots: {customRender: 'action'},
    },*/
});

const operList = ref([]);
const menuList = ref([]);
const userDataList = ref([]);
const tableList = ref([]);

const treeList = ref([])
const lastCheckList = ref([])


const loadMark = ref(false)
const showOk = ref(true)
const saveDataModel = reactive({
  id: null,
  ztUniqueCode: '',
  ztStyle: '',
  ztYear: '',
  userUniqueCode: '',
  supervisor: '0',
  defaultLogin: ''
})
onMounted(async () => {
  await reloadList()
  // resetDynamicColumnData()
});

async function reloadList() {
  // 查询所以操作员 与 菜单
  menuList.value = JsonTool.parseProxy((await getPlatformAndMenu()) || [])
  treeList.value = menuList.value.filter(it => it.platformId == null).map(it => ({
    key: it.id,
    name: it.name
  }))
}


const dynamicAdReload = async (e) => {
  saveDataModel.ztUniqueCode = e.coCode
  saveDataModel.ztYear = e.iyear
  saveDataModel.ztStyle = e.target.ztStyle
  saveDataModel.id = null
  if (operList.value.length == 0) {
    operList.value = ((await findAll()).items)
    if (operList.value.length > 0 && hasBlank(saveDataModel.userUniqueCode)) {
      saveDataModel.userUniqueCode = operList.value[1].id
      operChange(operList.value[1].id)
    }
  }else {
    operChange(saveDataModel.userUniqueCode)
  }
}

const operChange = async (v) => {
  if (!hasBlank(saveDataModel.ztUniqueCode) && !hasBlank(saveDataModel.ztYear) && !hasBlank(saveDataModel.userUniqueCode)) {
    loadMark.value = true
    let res = await findUserAuthor({
      userId: saveDataModel.userUniqueCode,
      coCode: saveDataModel.ztUniqueCode,
      iyear: saveDataModel.ztYear
    })
    if (null != res) {
      saveDataModel.id = res.id
      saveDataModel.supervisor = res.supervisor || '0'
      saveDataModel.defaultLogin = res.defaultLogin || ''
      if (saveDataModel.supervisor != '1' && !hasBlank(res.defaultLogin)) {
        let list = JsonTool.parseObj(res.defaultLogin)
        if (list.length > 0) {
          userDataList.value = list
          lastCheckList.value = [...new Set(list.map(it => it.platformMark))]
        } else {
          userDataList.value = []
          lastCheckList.value = []
        }
      } else if (saveDataModel.supervisor == '1') {
        lastCheckList.value = ['all']
        userDataList.value = []
      }
    } else {
      lastCheckList.value = []
      userDataList.value = []
    }
    loadMark.value = false
  }
}

async function saveData() {
  showOk.value = false
  if (hasBlank(saveDataModel.ztUniqueCode)) {
    createWarningModal({content: '请先选择授权账套！'})
    showOk.value = true
  } else if (hasBlank(saveDataModel.userUniqueCode)) {
    createWarningModal({content: '请选择操作员！'})
    showOk.value = true
  } else {
    if (saveDataModel.supervisor == '1') { // 所以权限
      saveDataModel.defaultLogin = ''
    } else {
      let list = getDataSource().filter(it => modificationExists(it))
      if (null == list || list.length == 0) {
        createWarningModal({content: '请先进行授权操作！'})
        showOk.value = true
        return false
      }
      saveDataModel.defaultLogin = JsonTool.json(list)
    }
    let res = await saveAuthor(saveDataModel)
    if (res != null) {
      message.success('保存成功！')
      await writeLog('分配', saveDataModel, null)
      operChange(saveDataModel.userUniqueCode)
    } else {
      message.error('保存失败！')
    }
    showOk.value = true
  }
}
async function emptyCurrentData() {
  showOk.value = false
  if (hasBlank(saveDataModel.ztUniqueCode)) {
    createWarningModal({content: '请先选择授权账套！'})
    showOk.value = true
  } else if (hasBlank(saveDataModel.userUniqueCode)) {
    createWarningModal({content: '请选择操作员！'})
    showOk.value = true
  } else if (pageParameter.checkList == null || pageParameter.checkList.length == 0){
    createWarningModal({content: '请左侧选择需要进行清空的模块！'})
    showOk.value = true
  } else if (saveDataModel.id == null){ //清理天台即刻
    //operChange(saveDataModel.userUniqueCode)
    showOk.value = true
  }else {
    let data =  JsonTool.parseProxy(saveDataModel)
    data.defaultLogin = pageParameter.checkList[0]
    console.log(data)
    let res = await emptyAuthor(data)
    if (res != null) {
      message.success('清空成功！')
      // operChange(saveDataModel.userUniqueCode)
      await writeLog('删除', saveDataModel, null)
    } else {
      message.error('清空失败！')
    }
    showOk.value = true
  }
}

function onSearch() {
  loadMark.value = true
  if (hasBlank(pageParameter.searchConditon.value.trim())) {
    setTableData(tableList.value)
  } else {
    let v = pageParameter.searchConditon.value.trim()
    let k = pageParameter.searchConditon.requirement.trim()
    let searchAfter = tableList.value.filter(item => item[k].indexOf(v) != -1)
    setTableData(searchAfter)
  }
  loadMark.value = false
}

const searchConditonList = ref([])
const PlatformColumns = ref({})
const [registerAuthorCopy, {openModal: openAuthorCopyPage}] = useModal();
const openCopy = () => {
  openAuthorCopyPage(true, {userList: operList.value});
}
const initMeun = async () => {
  return false;
  let arr = [20017,1007,1099]
  arr.forEach((a) => {
    savePlatformAndMenu({entries: JsonTool.json(getTheMenuOfTheSpecifiedPlatform(a))})
  })
}

const handleSelect = async (e) => {
  let value = e.keys
  loadMark.value = true
  pageParameter.checkList = value
  let tLen = treeList.value.length + 1
  if (e.keys != 'all')
  saveDataModel.supervisor = tLen == e.checkLen && 0 != e.checkLen ? '1' : '0'
  tableSelectedRowKeys.value = []
  setTableData([])
  if (saveDataModel.supervisor == '1') {
    tableList.value = []
  } else {
    if (value.length != 0) {
      // 获取当前
      let theC = getColumns().slice(0, 2)
      let cs = PlatformColumns.value[value[0]]
      if (null == cs) {//根据平台标识查询
        let res = await getPlatformColumn({mark: value[0]})
        if (null == res || res.length == 0) res = await savePlatformColumn({entries: JsonTool.json(getTheColumnOfTheSpecifiedPlatform(value[0]))})
        PlatformColumns.value[value[0]] = res.map(it => ({
          title: it.columnTitle,
          dataIndex: it.columnProperty,
          ellipsis: true,
          width: it.columnTitle.length > 2 ? 100 : 60,
          slots: {customRender: it.columnProperty}
        }))
      }
      theC.push(...PlatformColumns.value[value[0]])
      setColumns(theC)
      initTableWidth(theC)
    }
    let list = menuList.value.filter(it => value.indexOf(it.platformId) != -1)
      // 用于获取系统菜单的所有末吉菜单
      // .filter(it => null != it.component && '' != it.component && it.component != 'LAYOUT' && it.component != 'Layout' && it.name != '工作台' && it.name != '首页' && it.name != '根目录')
      .map(it => createRow(it, userDataList.value, false))
    setTableData(list)
    tableList.value = list
  }
  pageParameter.searchConditon.value = ''
  loadMark.value = false
}

function createRow(it, dbList, v) {
  let dbt = dbList.filter(itt => itt.functionId == it.id)
  if (dbt.length > 0) {
    let nIt = dbt[0]
    return {
      platformMark: it.platformId,
      functionId: it.id,
      functionName: it.name,
      isSee: nIt['isSee'] == 'true',
      isEdit: nIt['isEdit'] == 'true',
      isAdd: nIt['isAdd'] == 'true',
      isChange: nIt['isChange'] == 'true',
      isDel: nIt['isDel'] == 'true',
      isOprat: nIt['isOprat'] == 'true',
      isInsert: nIt['isInsert'] == 'true',
      isSign: nIt['isSign'] == 'true',
      isCancel: nIt['isCancel'] == 'true',
      isTovoid: nIt['isTovoid'] == 'true',
      isMake: nIt['isMake'] == 'true',
      isImport: nIt['isImport'] == 'true',
      isExport: nIt['isExport'] == 'true',
      isPrint: nIt['isPrint'] == 'true',
      isEmail: nIt['isEmail'] == 'true',
      isColumn: nIt['isColumn'] == 'true',
      isStart: nIt['isStart'] == 'true',
      isStop: nIt['isStop'] == 'true',
      isSetting: nIt['isSetting'] == 'true',
      isAnnexSee: nIt['isAnnexSee'] == 'true',
      isAnnexEdit: nIt['isAnnexEdit'] == 'true',
      isAnnexChange: nIt['isAnnexChange'] == 'true',
      isRecycle: nIt['isRecycle'] == 'true',
      isSave: nIt['isSave'] == 'true',
      isCashierSign: nIt['isCashierSign'] == 'true',
      isCashierCancel: nIt['isCashierCancel'] == 'true',
      isSupervisorSign: nIt['isSupervisorSign'] == 'true',
      isSupervisorCancel: nIt['isSupervisorCancel'] == 'true',
      isBook: nIt['isBook'] == 'true',
      isJoin: nIt['isJoin'] == 'true',
      isCarryOver: nIt['isCarryOver'] == 'true',
      isCheck: nIt['isCheck'] == 'true',
      isAmount: nIt['isAmount'] == 'true',
      isCopy: nIt['isCopy'] == 'true'
    }
  } else {
    return {
      platformMark: it.platformId,
      functionId: it.id,
      functionName: it.name,
      isSee: v,
      isEdit: v,
      isAdd: v,
      isChange: v,
      isDel: v,
      isOprat: v,
      isInsert: v,
      isSign: v,
      isCancel: v,
      isTovoid: v,
      isMake: v,
      isImport: v,
      isExport: v,
      isPrint: v,
      isEmail: v,
      isColumn: v,
      isStart: v,
      isStop: v,
      isSetting: v,
      isAnnexSee: v,
      isAnnexEdit: v,
      isAnnexChange: v,
      isRecycle: v,
      isSave: v,
      isCashierSign: v,
      isCashierCancel: v,
      isSupervisorSign: v,
      isSupervisorCancel: v,
      isBook: v,
      isJoin : v,
      isCarryOver : v,
      isCheck : v,
      isAmount : v,
      isCopy : v
    }
  }
}

function checkboxRow(it, v) {
  it.isSee = v,
    it.isEdit = v, it.isAdd = v, it.isChange = v, it.isDel = v, it.isOprat = v, it.isInsert = v,
    it.isSign = v, it.isCancel = v, it.isTovoid = v, it.isMake = v, it.isImport = v, it.isExport = v,
    it.isPrint = v, it.isEmail = v, it.isColumn = v, it.isStart = v, it.isStop = v
    , it.isSetting = v, it.isAnnexSee = v, it.isAnnexEdit = v, it.isAnnexChange = v, it.isRecycle = v,
    it.isSave = v, it.isCashierSign = v, it.isCashierCancel = v, it.isSupervisorSign = v, it.isSupervisorCancel = v,
    it.isBook = v,
    it.isJoin = v,
    it.isCarryOver = v,
    it.isCheck = v,
    it.isAmount = v,
    it.isCopy = v
  return it
}

function mergeRow(it, last) {
  it.isSee = last['isSee'],
    it.isEdit = last['isEdit'], it.isAdd = last['isAdd'], it.isChange = last['isChange'], it.isDel = last['isDel'], it.isOprat = last['isOprat'], it.isInsert = last['isInsert'],
    it.isSign = last['isSign'], it.isCancel = last['isCancel'], it.isTovoid = last['isTovoid'], it.isMake = last['isMake'], it.isImport = last['isImport'], it.isExport = last['isExport'],
    it.isPrint = last['isPrint'], it.isEmail = last['isEmail'], it.isColumn = last['isColumn'], it.isStart = last['isStart'], it.isStop = last['isStop']
    , it.isSetting = last['isSetting'], it.isAnnexSee = last['isAnnexSee'], it.isAnnexEdit = last['isAnnexEdit'], it.isAnnexChange = last['isAnnexChange'], it.isRecycle = last['isRecycle'],
    it.isSave = last['isSave'],
    it.isCashierSign = last['isCashierSign'],
    it.isCashierCancel = last['isCashierCancel'],
    it.isSupervisorSign = last['isSupervisorSign'],
    it.isSupervisorCancel = last['isSupervisorCancel'],
    it.isBook = last['isBook'],
    it.isJoin= last['isJoin'],
    it.isCarryOver= last['isCarryOver'],
    it.isCheck= last['isCheck'],
    it.isAmount= last['isAmount'],
    it.isCopy= last['isCopy']
  return it;
}

function modificationExists(it) {
  return it.isSee || it.isEdit || it.isAdd || it.isChange || it.isDel || it.isOprat || it.isInsert ||
    it.isSign || it.isCancel || it.isTovoid || it.isMake || it.isImport || it.isExport ||
    it.isPrint || it.isEmail || it.isColumn || it.isStart || it.isStop || it.isSetting || it.isAnnexSee || it.isAnnexEdit || it.isAnnexChange || it.isRecycle
    || it.isSave || it.isCashierSign || it.isCashierCancel || it.isSupervisorSign || it.isSupervisorCancel || it.isBook
    it.isBook ||
    it.isJoin ||
    it.isCarryOver||
    it.isCheck||
    it.isAmount||
    it.isCopy
}

function isShow(o, key) {
  let arr = menuList.value.filter(it => it.platformId == o.platformMark && it.id == o.functionId)
  return arr.length > 0 ? (arr[0][key]) == '1' : false
}

const pageReload = () => {
  // setTableData([])
}
const tableSelectedRowKeys = ref([])
const onSelectChange = (record, selected, obj, nativeEvent) => {
  loadMark.value = true
  record = checkboxRow(record, selected)
  tableSelectedRowKeys.value = obj.map(it => it.functionId)
  loadMark.value = false
}
const onSelectChange2 = (selected, selectedRows, changeRows) => {
  loadMark.value = true
  if (selected) {
    tableSelectedRowKeys.value = selectedRows.map(item => item.functionId)
    changeRows.map(it => checkboxRow(it, true))
  } else {
    tableSelectedRowKeys.value = []
    changeRows.map(it => checkboxRow(it, false))
  }
  loadMark.value = false
}
/*start栏目设置*/
const pageParameter = reactive({
  showRulesSize: 'MIN',
  searchConditon: {
    requirement: 'functionName',
    value: ''
  },checkList: []
})
const mark = usePlatformsStore().getCurrentPlatformId
const visible = ref(false);
const windowWidth = (document.documentElement.clientWidth - (70 /*+ 280*/))
const windowHeight = (window.innerHeight - (265))
const totalColumnWidth = ref(0)
const dynamicColumns = initDynamics().DEFAULT
const dynamicColumnData: any = ref([])
let dynamicColumnDataCopy = []
const editableData = reactive({});
const tableRef: any = ref(null)
const lanMuData = {
  'accId': 'postgres',
  'menuName': '操作权限设置',
  'type': '列表',
  objects: '',
  username: useUserStoreWidthOut().getUserInfo.username
}
const {createConfirm, createWarningModal} = useMessage();
const confirm = (e: MouseEvent) => {
  // 询问
  createConfirm({
    iconType: 'warning',
    title: '栏目同步',
    content: '是否将刚才设置同步数据库!',
    onOk: async () => {
      // 调整数据库 列参数
      lanMuData.accId = getCurrentAccountName(false)
      lanMuData.objects = JSON.stringify(filterModifyData(dynamicColumnData.value, dynamicColumnDataCopy))
      if (lanMuData.objects == '[]') {
        createWarningModal({content: '请先做修改后再进行确认同步数据库！'})
      } else {
        saveLanMuList(lanMuData).then(res => {
          message.success("数据库同步成功！")
        })
        // 重新赋值
        dynamicColumnDataCopy = JSON.parse(JSON.stringify(dynamicColumnData.value))
      }
    }
  });
  // 重新获取数据
  reloadColumns()
}
const cancel = (e: MouseEvent) => {
  // 恢复默认
  dynamicColumnData.value = []
  dynamicColumnData.value = dynamicColumnDataCopy
}
const edit = (key: string) => {
  if (key.toString().indexOf('-') != -1) {
    let arr: any = key.split('-');
    let one = parseInt(arr[0])
    if (arr.length == 2) {
      editableData[key] = cloneDeep(dynamicColumnData.value[one].children.filter(item => key === item.key)[0]);
    } else {
      let two = parseInt(arr[1] - 1)
      editableData[key] = cloneDeep(dynamicColumnData.value[one].children[two].children.filter(item => key === item.key)[0]);
    }
  } else {
    editableData[key] = cloneDeep(dynamicColumnData.value.filter(item => key === item.key)[0]);
  }
}
const save = (key: string, min: number, max: number) => {
  editableData[key].width = editableData[key].width > max ? max : editableData[key].width < min ? min : editableData[key].width
  if (key.toString().indexOf('-') != -1) {
    let arr: any = key.split('-');
    let one = parseInt(arr[0])
    if (arr.length == 2) {
      Object.assign(dynamicColumnData.value[one].children.filter(item => key === item.key)[0], editableData[key]);
      Object.assign(dynamicColumnData.value[one].children.filter(item => key === item.key)[0], editableData[key]);
    } else {
      let two = parseInt(arr[1] - 1)
      Object.assign(dynamicColumnData.value[one].children[two].children.filter(item => key === item.key)[0], editableData[key]);
    }
  } else {
    Object.assign(dynamicColumnData.value.filter(item => key === item.key)[0], editableData[key]);
  }
  delete editableData[key];
}
const saveName = (key: string) => {
  if (key.toString().indexOf('-') != -1) {
    let arr: any = key.split('-');
    let one = parseInt(arr[0])
    if (arr.length == 2) {
      Object.assign(dynamicColumnData.value[one].children.filter(item => key === item.key)[0], editableData[key]);
      Object.assign(dynamicColumnData.value[one].children.filter(item => key === item.key)[0], editableData[key]);
    } else {
      let two = parseInt(arr[1] - 1)
      Object.assign(dynamicColumnData.value[one].children[two].children.filter(item => key === item.key)[0], editableData[key]);
    }
  } else {
    Object.assign(dynamicColumnData.value.filter(item => key === item.key)[0], editableData[key]);
  }
  delete editableData[key];
}

function filterModifyData(lanMuList: any, copyList) {
  let a = lanMuList.filter(item => {
    try {
      copyList.forEach(item2 => {
        if (item.key === item2.key && item.name == item2.name) {
          if (item.nameNew != item2.nameNew || item.width != item2.width || item.check != item2.check || item.align != item2.align)
            throw new Error('ok')
        }
      })
      return false
    } catch (e: any) {
      if (e.message == 'ok') {
        return true
      } else {
        return false
      }
    }
  })
  return a;
}

function combineParameters(staticList: any, dbList: any) {
  staticList.forEach(item => {
    dbList.forEach(item2 => {
      if (item.key === item2.key && item.name === item2.name) {
        item.nameNew = item2.nameNew
        item.width = parseInt(item2.width)
        item.check = item2.check == 'true'
        item.align = item2.align
      }
    })
  })
  return staticList
}

const reloadColumns = () => {
  /*let a: any = []
  a = getColumns()
  let last = a.pop()*/
  let newA = JSON.parse(JSON.stringify(CrudApi.columns))
  newA = assemblyDynamicColumn(dynamicColumnData.value, newA)
  // newA.push(last)
  setColumns(newA)
  initTableWidth(newA)
  //searchConditonList.value = newA
}

function resetDynamicColumnData() {
  // 先从数据查询是否已经设置
  lanMuData.accId = getCurrentAccountName(false)
  findDbLanMuList(lanMuData).then(res => {
    // 栏目列
    let dbList = res.items
    if (dbList.length > 0) {
      let statiList = initDynamics()['DATA']
      dbList = combineParameters(statiList, dbList)
      dynamicColumnData.value = dbList
      dynamicColumnDataCopy = JSON.parse(JSON.stringify(dbList))
    } else {
      let statiList = initDynamics()['DATA']
      dynamicColumnData.value = statiList
      dynamicColumnDataCopy = JSON.parse(JSON.stringify(statiList))
    }
    // 表格列
    reloadColumns()
    //pageReload()
  })
}

function initTableWidth(thisCs) {
  let total = 60
  thisCs.forEach(item => {
    if (item.ifShow == null || item.ifShow)
      total += parseInt(item.width)
  })
  /*  // 去除操作咧宽
    total -= 100*/
  if (total > windowWidth) {
    let f = 0
    if (visible.value) f = 260
    totalColumnWidth.value = Number(windowWidth) - f
    tableRef.value.$el.style.setProperty('width', (windowWidth + 70 - f) + 'px')
  } else {
    if (visible.value && (windowWidth - 260) < total) total -= (total - (windowWidth - 260))
    totalColumnWidth.value = total
    tableRef.value.$el.style.setProperty('width', (total + 70) + 'px')
  }
}

/*栏目设置end*/


const rowChange = (e, m) => {
  if (m) {
    if (!e.isSee) {
      let arr = Object.keys(e).slice(4)
      for (let i = 0; i < arr.length; i++) {
        if (e[arr[i]]) e[arr[i]] = false
      }
    }
  } else {
    e.isSee = true
  }
}

async function writeLog(action, a, content) {
  /************** 记录操作日志 ****************/
  if (null == content){
    content = `操作内容【${action}操作员权限】,` + '账套代码【' + a.ztUniqueCode + '】,年度【' + a.ztYear + '】,操作员【' + transformText('userUniqueCode', a.userUniqueCode) + '】'
    if (a.supervisor == '1') {
      content += '被授予的账套主管权限！'
    } else {
      let list = JsonTool.parseObj(a.defaultLogin)
      let name = (treeList.value.filter(it => it.key == list[0].platformMark))[0].name
      content += `被授予功能模块-${name}下【${list.map(it => it.functionName).join(',')}】菜单操作权限！`
    }
  }
  let map = {
    loginTime: new Date(+new Date() + 8 * 3600 * 1000).toJSON().substr(0, 19).replace("T", " "),
    userId: useUserStoreWidthOut().getUserInfo.username,
    userName: useUserStoreWidthOut().getUserInfo.realName,
    optModule: 'group',
    optFunction: '操作员权限',
    optRange: '2',
    uniqueCode: '',
    optAction: action,
    optContent: content,
  }
  await saveLog(map)
}

function transformText(key, value) {
  let text = value
  switch (key) {
    case "userUniqueCode":
      let arr = operList.value.filter(it => it.id == value)
      text = arr.length == 0 ? value : arr[0].realName
      break;
  }
  return text
}
function copyCallback(u1,u2) {
  writeLog("复制",{},`操作内容【复制操作员权限】将操作员【${transformText('userUniqueCode', u1)}】的权限复制给操作员【${transformText('userUniqueCode', u2)}】！`)
}
provide('roleList', treeList)
provide('lastCheckList', lastCheckList)
</script>
<style lang="less" scoped>
@import '/@/assets/styles/global-menu-index.less';
</style>
<style scoped>
.a-table-font-size-16 :deep(td),
.a-table-font-size-16 :deep(th) {
  font-size: 16px !important;
  padding: 5px 8px !important;
}

.a-table-font-size-16 :deep(th), .a-table-font-size-12 :deep(th) {
  text-align: center !important;
  font-weight: bold;
  background-color: #f1f1f1;
}

.a-table-font-size-16 :deep(.ant-checkbox-inner), .a-table-font-size-12 :deep(.ant-checkbox-inner) {
  border: 1px solid #c9c9c9;
}

.tableUStyle {
  color: #0798c8;
  cursor: pointer;
  text-decoration: none;
}

.tableUStyle:hover {
  color: #b4c8e3;
}

.a-table-font-size-12 :deep(td),
.a-table-font-size-12 :deep(th) {
  font-size: 14px !important;
  padding: 2px 8px !important;
}

.app-container:nth-of-type(1) {
  background-color: #f2f2f2;
  padding: 10px 5px;
  margin: 10px 10px 5px;
}

.app-container:nth-of-type(2) {
  padding: 0px;
  margin: 5px 10px;
  background: #b4c8e3 !important;
}
</style>

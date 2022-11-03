<template>
  <div>
    <div class="app-container">
      <div class="app-container-head">
        <AppstoreOutlined style="font-size: 36px;color: #0096c7;" class="container-head-img"/>
        <div class="container-head-title2">
          <b class="noneSpan">组织信息</b>
        </div>
        <span
          style="font-size: 12px;color: black;position: absolute;top: 90px;left: 25px">共{{
            totalNumber
          }}条记录</span>
        <div class="ant-btn-group" data-v-a1ccd506="" style="float: right">
          <button type="button" class="ant-btn ant-btn-me"
                  ant-click-animating-without-extra-node="false" @click="openAddPage()">
            <span>新建</span></button>
          <button v-if="activeKey" type="button" class="ant-btn ant-btn-me" @click="editBefore">
            <span>修改</span>
          </button>
          <button v-if="activeKey" type="button" class="ant-btn ant-btn-me" @click="delBefore">
            <span>删除</span>
          </button>
          <button v-if="activeKey" type="button" class="ant-btn ant-btn-me" @click="openAuthor">
            <span>授权</span>
          </button>
          <!--          <button v-if="activeKey" type="button" class="ant-btn ant-btn-me"><span>导入</span></button>-->
          <button v-if="activeKey" type="button" class="ant-btn ant-btn-me" @click="startExport('table')"><span>导出</span></button>
          <button type="button" class="ant-btn ant-btn-me"
                  @click="closeCurrent(),router.push(`/${mark == 1003?'system':'origin'}/home/welcome`)">
            退出
          </button>
        </div>
      </div>
      <div class="app-container-neck">
        <div style="float: right; margin-left: 10px">
          <a-button style="padding: 0px 12px !important;margin-right: 3px;" @click="reload">
            <SyncOutlined :style="{ fontSize: '14px' }"/>
          </a-button>
          <a-popover v-if="activeKey" class="ant-btn-default" placement="bottom">
            <template #content>
              <a-popconfirm
                ok-text="确定"
                cancel-text="放弃"
                @confirm="confirm"
                @cancel="cancel">
                <template #icon><b>栏目设置</b><br></template>
                <template #title>
                  <div style="width: 640px">
                    <a-table bordered :data-source="dynamicColumnData" :columns="dynamicColumns"
                             childrenColumnName="children" :pagination="false"
                             style="max-height: 500px;overflow-y: auto" class="lanmu-table">
                      <template #checkBox="{ text, record }">
                        <a-checkbox v-model:checked="record.check" :disabled="record.isFixed"/>
                      </template>
                      <template #widthInput="{ text, record }">
                        <div class="editable-cell">
                          <div v-if="editableData[record.key]" class="editable-cell-input-wrapper">
                            <a-input type="number" v-model:value="editableData[record.key].width"
                                     @pressEnter="save(record.key,record.min,record.max)"
                                     style="width: 80px"/>
                            <check-outlined class="editable-cell-icon-check"
                                            @click="save(record.key,record.min,record.max)"/>
                          </div>
                          <div v-else class="editable-cell-text-wrapper">
                            {{ text || ' ' }}
                            <edit-outlined class="editable-cell-icon" @click="edit(record.key)"/>
                            <span style="float: right;">{{ record.min + '~' + record.max }}</span>
                          </div>
                        </div>
                      </template>
                      <template #nameInput="{ text, record }">
                        <div class="editable-cell">
                          <div v-if="editableData[record.key]" class="editable-cell-input-wrapper">
                            <a-input type="text" v-model:value="editableData[record.key].nameNew"
                                     @pressEnter="saveName(record.key)" style="width: 100px"/>
                            <check-outlined class="editable-cell-icon-check"
                                            @click="saveName(record.key)"/>
                          </div>
                          <div v-else class="editable-cell-text-wrapper">
                            {{ text || ' ' }}
                            <edit-outlined class="editable-cell-icon" @click="edit(record.key)"/>
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
              <br/>
              <span class="group-btn-span-special2" @click="pageParameter.showRulesSize = 'MAX'"
                    :style="pageParameter.showRulesSize==='MAX'?{backgroundColor: '#0096c7',color: 'white'}:''">
                <SortDescendingOutlined/>&nbsp;大号字体&ensp;<CheckOutlined
                v-if="pageParameter.showRulesSize==='MAX'"/></span><br/>
              <span class="group-btn-span-special2" @click="pageParameter.showRulesSize = 'MIN'"
                    :style="pageParameter.showRulesSize==='MIN'?{backgroundColor: '#0096c7',color: 'white'}:''">
                <SortAscendingOutlined/>&nbsp;小号字体&ensp;<CheckOutlined
                v-if="pageParameter.showRulesSize==='MIN'"/></span>
            </template>
            <!--            <template #title>
                          <b>设置表格字号</b></template>-->
            <a-button>
              <SettingFilled :style="{ fontSize: '14px' }"/>
            </a-button>
          </a-popover>
          <!--          <a-button @click="activeKey = !activeKey">
                      <PieChartFilled :style="{ fontSize: '14px' }"/>
                    </a-button>-->
          <a-button title="回收站" @click="openRecycleDel">
            <DeleteOutlined :style="{ fontSize: '14px' }"/>
          </a-button>
        </div>
        <div class="act-two-d-right">
          <div class="acttd-right-d-search">
            <!--              <a-select v-model:value="pageParameter.searchConditon.requirement"
                                    class="acttdrd-search-select">
                            <template v-for="item in searchConditonList">
                              <a-select-option v-if="item.ifShow == true" :value="item.dataIndex">
                                {{ item.title }}
                              </a-select-option>
                            </template>
                          </a-select>-->
            <!--                v-model:value="pageParameter.searchConditon.value"-->
            <a-input-search
              style="width: 180px;"
              placeholder="代码或名称"
              class="acttdrd-search-input"
              @search="onSearch"
            />
          </div>
        </div>
      </div>
    </div>
    <div class="app-container">
      <Authorization @register="registerAuthorPage" @log="logCallback"/>
      <Recycle @modify="recycleModify" @register="registerRecyclePage"/>
      <Edit @save="saveData" @register="registerEditPage"/>
      <!--        :style="{height: (windowHeight+60)+'px'}"-->
      <BasicTable
        v-show="activeKey"
        class="w-4/5 xl:w-5/6"
        ref="tableRef"
        :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
        :scroll="{ x: totalColumnWidth,y: windowHeight }"
        :row-selection="{ type: 'radio',fixed: true,selectedRowKeys: tableSelectedRowKeys, onChange: onSelectChange }"
        @register="registerTable">
        <template #orgName="{ record }"><u class="tableUStyle"
                                           @click="openEdit(record,true)">{{ record.orgName }}</u>
        </template>
        <template #uniqueGroupCode="{ record }"> {{
            formatGroupInCharge(record.uniqueGroupCode)
          }}
        </template>
        <template #uniqueAccStandard="{ record }">
          {{ formatAcountStInCharge(record.uniqueAccStandard) }}
        </template>
        <template #industryclassCode="{ record }"> {{
            formatHyInCharge(record.industryclassCode)
          }}
        </template>
        <template #uniqueCodeZone="{ record }"> {{
            formatXzInCharge(record.uniqueCodeZone)
          }}
        </template>
        <template #orgSuperior="{ record }"> {{ formatZzInCharge(record.orgSuperior) }}</template>
        <template #countryId="{ record }"> {{ formatDqInCharge(record.countryId) }}</template>
        <template #action="{ record }">
          <div>
            <a-popover placement="bottom">
              <a-button style="padding: 0px 4px; height: 20px;">
                <CaretDownFilled/>
              </a-button>
              <template #content>
                <p class="p_specifics" @click="openEdit(record,false)">
                  <FormOutlined/>
                  修改
                </p>
                <p class="p_specifics" @click="del(record)">
                  <DeleteOutlined/>
                  删除
                </p>
              </template>
            </a-popover>
          </div>
        </template>
      </BasicTable>
      <div
        v-show="!activeKey"
        :class="cardList"
      >
        <div :class="`${cardList}__content`">
          <!--        <div>ID:{{ item.id }}</div>-->
          <a-list style="height: 700px;overflow-y: scroll;overflow-x: hidden;">
            <a-row :gutter="16">
              <template
                v-for="(item) in cardList"
              >
                <a-col :span="8">
                  <a-list-item style="width: 95%">
                    <a-card
                      :hoverable="true"
                      :class="`${cardList}__card`"
                      style="width: 100%"
                    >
                      <div class="abc" style="width: 100%;float: right;" @click="openEdit(item)">
                        <div style="width: 88px;height: 65px;display: inline-block">
                          <a-popover placement="right">
                            <template #content>
                              <p class="p_specifics" @click="openEdit(item,false)">
                                <FormOutlined/>
                                修改
                              </p>
                              <p class="p_specifics" @click="del(item)">
                                <DeleteOutlined/>
                                删除
                              </p>
                            </template>
                            <EllipsisOutlined style="font-size: 20px"/>
                          </a-popover>
                        </div>
                        <div style="width: calc(100% - 88px);display: inline-block;float: right;">
                          <div
                            style="float: right; border: 1px solid rgb(1, 143, 251);border-radius: 10px;padding: 0 10px; margin-top: -5px;">
                            {{ item.orgCode }}
                          </div>
                          <div style="font-size: 24px;margin-top: 20px;">
                            {{ item.orgName }}
                          </div>
                        </div>
                        <div style="font-size: 14px;">
                          行业性质：{{ formatHyInCharge(item.industryclassCode) }}
                        </div>
                        <div style="font-size: 14px;color: red">
                          下属公司：{{ item.beiyong3 || '无' }}
                        </div>
                      </div>
                    </a-card>
                  </a-list-item>
                </a-col>
              </template>
            </a-row>
          </a-list>
        </div>

      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import {BasicTable, useTable} from '/@/components/Table'
import {useModal} from '/@/components/Modal'
import {
  CaretDownFilled,
  FormOutlined,
  DeleteOutlined,
  SettingFilled,
  SyncOutlined,
  PicLeftOutlined,
  EllipsisOutlined,
  EditFilled,
  PieChartFilled,
  FilterFilled,
  EditOutlined,
  SortAscendingOutlined,
  CheckOutlined,
  SortDescendingOutlined,
  AppstoreOutlined
} from '@ant-design/icons-vue';
import {computed, onMounted, ref, reactive, watch} from 'vue'
import {
  getOrganizeList,
  deleteOrganize, deleteOrganizeTrue,
  saveOrganize,
  getOrganizeAllAndUnit, saveOrgCash, saveOrgPeriod, getOrganizeDelAll, reductionOrganize
} from '/@/api/record/group/im-organize'
import {findAllIndustry, getGroupAll} from "/@/api/record/group/im-group";
import Edit from './popup/edit.vue';
import Recycle from '/@/views/boozsoft/management/im-organize/popup/recycle.vue';

import {getDeptListById} from "/@/api/record/system/dept";
import {psnFindAll} from "/@/api/psn/psn";
import {projectClassFindAll} from "/@/api/record/system/project_class";
import {
  Select as ASelect, Input as AInput, Popover as APopover, Tag as ATag, Popconfirm as APopconfirm,
  Radio as ARadio, Checkbox as ACheckbox, List as AList, Row as ARow, Col as ACol, Card as ACard,
  Table as ATable, message
} from 'ant-design-vue'
import Authorization from './popup/Authorization.vue';

const mark = usePlatformsStore().getCurrentPlatformId
const {closeCurrent} = useTabs(router);
const ARadioButton = ARadio.Button
const ARadioGroup = ARadio.Group
const AInputSearch = AInput.Search
const ASelectOption = ASelect.Option
const AListItem = AList.Item
//选择项目大类
const cateCode: any = ref()
const currentCateCode = computed(() => cateCode.value)
const userR = ref(useUserStoreWidthOut().getUserInfo.id)
watch(currentCateCode, (abc) => {
  // projectStore.projectClassTree()
  reload({
    searchInfo: {
      userId: userR.value
    }
  })
})
//根据基础档案查询基础档案栏目
const cateList: any = ref([])
/*async function reloadCate() {
  const res: any = await cateFindStateFlag()
  cateList.value = res
  if (res.length>0) {
    cateCode.value = res[0].projectCateCode
    // console.log(currentCateCode)
  }
}*/

const CrudApi = {
  list: getOrganizeList,
  columns: [
    {
      title: '组织代码',
      dataIndex: 'orgCode',
      fixed: true,
      ellipsis: true
    },
    {
      title: '组织名称',
      dataIndex: 'orgName',
      ellipsis: true,
      fixed: true,
      slots: {customRender: 'orgName'}
    },
    {
      title: '组织简称',
      dataIndex: 'orgSimpName',
      ellipsis: true,
      slots: {customRender: 'orgSimpName'}
    }, {
      title: '会计准则',
      dataIndex: 'uniqueAccStandard',
      ellipsis: true,
      slots: {customRender: 'uniqueAccStandard'}
    }, {
      title: '科目级次',
      dataIndex: 'ccodeLevel',
      ellipsis: true,
    }, {
      title: '上级组织',
      dataIndex: 'orgSuperior',
      ellipsis: true,
      slots: {customRender: 'orgSuperior'}
    },
    {
      title: '所属集团',
      dataIndex: 'uniqueGroupCode',
      ellipsis: true,
      slots: {customRender: 'uniqueGroupCode'}
    },
    {
      title: '所属行业',
      dataIndex: 'industryclassCode',
      ellipsis: true,
      slots: {customRender: 'industryclassCode'}
    },
    {
      title: '行政区划',
      dataIndex: 'uniqueCodeZone',
      ellipsis: true,
      slots: {customRender: 'uniqueCodeZone'}
    },
    {
      title: '国家(地区)',
      dataIndex: 'countryId',
      ellipsis: true,
      slots: {customRender: 'countryId'}
    },
    {
      title: '启用日期',
      dataIndex: 'createDate',
      ellipsis: true
    },
    {
      title: '负责人',
      dataIndex: 'personInCharge',
      ellipsis: true,
      slots: {customRender: 'personInCharge'}
    },

    {
      title: '联系电话',
      dataIndex: 'telephone',
      ellipsis: true,
    },

    {
      title: '通讯地址',
      dataIndex: 'address',
      ellipsis: true,
    },
    {
      title: '简介',
      dataIndex: 'remarks',
      ellipsis: true,
    },
  ],
  editData: {
    id: '',
    uniqueCode: '',
    orgCode: '',
    orgName: '',
    orgSimpName: '',
    uniqueGroupCode: '',
    orgSuperior: '',
    industryclassCode: '',
    uniqueCodeZone: '',
    countryId: '',
    createDate: '',
    personInCharge: '',
    uniqueAccStandard: '',
    telephone: '',
    address: '',
    ccodeLevel: '',
    remarks: '',
  }
}
//项目分类
const proClassList = ref([])
const groupInfoList = ref([])


async function reloadProClass() {
  const res: any = await projectClassFindAll();
  proClassList.value = res
  // console.log(proClassList.value)
}

function formatProjectClassCode(projectClassCode: any) {
  let str = projectClassCode
  // console.log(projectClassCode)
  proClassList.value.forEach(
    function (proClass: any) {
      if (proClass.uniqueCode == projectClassCode) {
        // console.log(proClass)
        str = proClass.projectClassName
      }
    }
  )
  return str
}

//部门
const deptList = ref([])

async function reloadDept() {
  const res: any = await getDeptListById()
  deptList.value = res.items
  // console.log(deptList.value)
}

function formatDeptCode(deptCode: any) {
  let str = deptCode
  // console.log(deptCode)
  deptList.value.forEach(
    function (dept: any) {
      if (dept.uniqueCode == deptCode) {
        // console.log(dept)
        str = dept.deptName
      }
    }
  )
  return str
}

//人员
const psnList = ref([])

async function reloadPsn() {
  const res: any = await psnFindAll()
  psnList.value = res.items
  // console.log(psnList.value)
}

function formatGroupInCharge(psnInCharge: any) {
  let str = psnInCharge
  groupInfoList.value.forEach(
    function (psn: any) {
      if (psn.uniqueCode == psnInCharge) {
        // console.log(psn)
        str = psn.groupName
      }
    }
  )
  return str
}

function formatAcountStInCharge(psnInCharge: any) {
  let str = psnInCharge
  normList.value.forEach(
    function (psn: any) {
      if (psn.id == psnInCharge) {
        // console.log(psn)    str = psn.accStandardName
        str = psn.tname
      }
    }
  )
  return str
}

function formatPsnInCharge(psnInCharge: any) {
  let str = psnInCharge
  // console.log(psnInCharge)
  psnList.value.forEach(
    function (psn: any) {
      if (psn.uniqueCode == psnInCharge) {
        // console.log(psn)
        str = psn.psnName
      }
    }
  )
  return str
}

function formatDqInCharge(psnInCharge: any) {
  if (psnInCharge == '' || null == psnInCharge) {
    return ""
  } else {
    let str = ""
    countryList.value.forEach((item) => {
      if (item.uniqueCode == psnInCharge) str = item.namech
    })
    return str
  }
}

function formatHyInCharge(psnInCharge: any) {
  if (psnInCharge == '' || null == psnInCharge) {
    return ""
  } else {
    let arr = JSON.parse(psnInCharge);
    let str = ""
    if (arr.length >= 1) {
      industryList.value.forEach((item) => {
        if (item.value == arr[0]) {
          str += item.label
          if (item.children.length > 0) {
            item.children.forEach((item1) => {
              if (item1.value == arr[1]) {
                str = str + ' / ' + item1.label
              }
            })
          }
        }
      })
    }
    return str
  }
}

function formatXzInCharge(psnInCharge: any) {
  if (psnInCharge == '' || null == psnInCharge) {
    return ""
  } else {
    let arr = JSON.parse(psnInCharge);
    let str = ""
    if (arr.length >= 1) {
      zoningList.value.forEach((item) => {
        if (item.value == arr[0]) {
          str += item.label
          if (item.children.length > 0) {
            item.children.forEach((item1) => {
              if (item1.value == arr[1]) {
                str = str + ' / ' + item1.label
                if (item1.children.length > 0) {
                  item1.children.forEach((item2) => {
                    if (item2.value == arr[2]) {
                      str = str + ' / ' + item2.label
                    }
                  })
                }
              }
            })
          }
        }
      })
    }
    return str
  }
}

function formatZzInCharge(psnInCharge: any) {
  if (psnInCharge == '' || null == psnInCharge) {
    return ""
  } else {
    let str = ""
    getDataSource().forEach((item) => {
      if (item.uniqueCode == psnInCharge) str = item.orgSimpName
    })
    return str
  }
}

const countryList = ref([])
const zoningList = ref([])
const industryList = ref([])
const normList = ref([])
const levelList = ref([])
onMounted(async () => {
  //await reloadDept()
  // await reloadPsn()
  // await reloadProClass()
  //await reloadCate()
  resetDynamicColumnData()
  groupInfoList.value = await getGroupAll()
  countryList.value = (await findAllCountry()).items
  zoningList.value = (await findAllProvince())
  industryList.value = (await findAllIndustry())
  let res = (await initBasisTabAccoutData())
  normList.value = res.acountStandardList || []
  levelList.value = res.levelList || []
})
const cardList: any = ref([])
const totalNumber = ref(0)
const activeKey = ref(true)
// 这是示例组件
const [registerTable, {reload, setTableData, getDataSource, setColumns, getColumns}] = useTable({
  api: CrudApi.list,
  columns: CrudApi.columns,
  bordered: true,
  showIndexColumn: false,
  pagination: {
    pageSize: 50,
    showSizeChanger: true,
    pageSizeOptions: ['10', '30', '50', '100'],
    showTotal: (t) => {
      totalNumber.value = t
      return `共${t}条记录`
    }
  },
  actionColumn: {
    width: 100,
    title: '操作',
    dataIndex: 'action',
    slots: {customRender: 'action'}
  },
  searchInfo: {
    userId: mark == 1003 ? null : userR.value
  }
})

const [registerEditPage, {openModal: openEditPage}] = useModal()


const openAddPage = () => {
  let data = CrudApi.editData
  data.isEdit = false
  data.groupInfoList = groupInfoList.value
  if (groupInfoList.value.length === 0) {
    createWarningModal({title: '温馨提示', content: '暂未发现集团信息,请先建立集团信息后在进行组织的新建操作！'})
    return false;
  }
  data.normList = normList.value
  data.levelList = levelList.value
  data.countryList = countryList.value
  data.zoningList = zoningList.value
  data.industryList = industryList.value
  data.numberDec = '2'
  data.unitPriceDec = '2'
  data.rateDec = '5'
  data.accvouchDec = '4'
  data.periodNum = '12'
  data.upList = getDataSource()
  openEditPage(true, {
    data: data
  })
}
const openEdit = (source: any, flag) => {
  let data = JSON.parse(JSON.stringify(source))
  data.isEdit = true
  data.isLook = flag
  data.groupInfoList = groupInfoList.value
  data.normList = normList.value
  data.levelList = levelList.value
  data.countryList = countryList.value
  data.zoningList = zoningList.value
  data.industryList = industryList.value
  data.upList = getDataSource()
  openEditPage(true, {
    data
  })
}
const del = async (data: any) => {
  data.beiyong2 = lanMuData.username
  await deleteOrganize(data).then(async (res) => {
    if (null != res && res.isOk == false) {
      await pointMessage({
        title: '处理结果',
        content: `删除失败,该组织下已经建立了${res.type == 'D02' ? '公司' : '下级组织'},无法进行删除！`,
        delay: true
      })
    } else {
      reload()
      await cardReload()
      await pointMessage({title: '处理结果', content: '删除成功！', delay: true})

      /* // 删除科目模板及科目
       let templateInfo = await findByTOrganization(data.id)
       await accTemplateDel(templateInfo[0].id);
       await codekemuDel(templateInfo[0].id);*/
    }
  }).catch(async () => await pointMessage({title: '处理结果', content: '删除失败！', delay: true}))
}

async function saveData(data: any) {
  if (data.id == '') data.id = null

  await saveOrganize(data).then(async (res) => {
    // -----------------生成组织会计科目模板-------------------
   if (data.id == null){
     // 获取科目模板信息
     let templateInfo = await findAllByTemplateID(res.uniqueAccStandard)
     let iyear = data.createDate.split('-')[0]
     // 新增组织期间
     await saveOrgPeriod({
       startDate: iyear + '-' + res.yearStartDate,
       periodNum: res.periodNum,
       periodMonth: res.createDate.substring(0, 7).replaceAll('-', ''),
       orgId: res.uniqueCode
     })
     const map = {
       tFlg: '1',
       tName: res.orgName,
       uniqueAccStandard: templateInfo.uniqueAccStandard,
       tJici: res.ccodeLevel.replaceAll('-', ','),  // 组织的级次
       ttype: '组织模板',
       tpid: res.uniqueAccStandard,
       tOrganization: res.uniqueCode,
       iyear: iyear
     };
     await accTemplateZZSave(map);
     // -----------------END-------------------
     // -----------------生成组织现金流量项目------------------- templateInfo.uniqueAccStandard
     await saveOrgCash({
       orgId: res.uniqueCode,
       iyear: iyear,
       accStandard: templateInfo.uniqueAccStandard
     });
     // -----------------END-------------------
   }
    // 写日志
    writeLog(data.id == null ? '新增' : '修改', res, null)
    await pointMessage({title: '处理结果', content: '更新成功！', delay: true})
    await reload()
    await cardReload()
  }).catch(async () => await pointMessage({title: '处理结果', content: '更新失败！', delay: true}))
}

async function cardReload() {
  if (!activeKey.value) cardList.value = await getOrganizeAllAndUnit()
}

let searchDataSource = []

function onSearch(v) {
  if (searchDataSource.length == 0) searchDataSource = getDataSource()
  if (hasBlank(v)) {
    setTableData(searchDataSource)
  } else {
    let searchAfter = searchDataSource.filter(item => (item.orgCode.indexOf(v) != -1) ||(item.orgSimpName.indexOf(v) != -1) ||(item.orgName.indexOf(v) != -1) )
    setTableData(searchAfter)
  }
}

/**********************栏目设置**********************/
import {initDynamics, assemblyDynamicColumn} from "./data/data";
import {useMessage} from "/@/hooks/web/useMessage";
import {
  findDbLanMuList,
  saveLanMuList,
} from '/@/api/record/system/accvoucher';
import {
  pointMessage, getThisIndexImg, hasBlank
} from "/@/api/task-api/tast-bus-api";
import {cloneDeep} from "lodash-es";
import {useUserStoreWidthOut} from "/@/store/modules/user";
import {findAllCountry} from "/@/api/record/costomer_data/customer";
import {findAllProvince} from "/@/api/record/system/zone";
import {initBasisTabAccoutData} from "/@/api/record/system/financial-settings";
import {useTabs} from "/@/hooks/web/useTabs";
import router from "/@/router";
import {usePlatformsStore} from "/@/store/modules/platforms";
import {
  accTemplateDel,
  accTemplateZZSave,
  codekemuDel,
  findAllByTemplateID,
  findByTOrganization
} from "/@/api/acctemplate/acctemplate";
import {delCodeByTemplate} from "/@/api/codekemu/codekemu";
import {JsonTool} from "/@/api/task-api/tools/universal-tools";
import {saveLog} from "/@/api/record/system/group-sys-login-log";
import {saveAudit} from "/@/api/caozuoyuan/caozuoyuan";
import {
  aoaToSheetXlsx
} from "/@/views/boozsoft/xian_jin_liu_liang/yin_hang_dui_zhang/yin_hang_dui_zhang_dan/excel/components/importexcel";

const {createConfirm, createWarningModal} = useMessage();
const pageParameter = reactive({
  showRulesSize: 'MIN',
})
const visible = ref(false);
const windowWidth = (window.innerWidth - (70/*+280*/))
const windowHeight = (window.innerHeight - (350))
const totalColumnWidth = ref(0)
const dynamicColumns = initDynamics().DEFAULT
const dynamicColumnData = ref([])
let dynamicColumnDataCopy = []
const editableData = reactive({});
const tableRef = ref(null)
const lanMuData = {
  'accId': 'postgre',
  'menuName': '组织信息',
  'type': '集团',
  objects: '',
  username: useUserStoreWidthOut().getUserInfo.username
}
const confirm = (e: MouseEvent) => {
  // 询问
  createConfirm({
    iconType: 'warning',
    title: '栏目同步',
    content: '是否将刚才设置同步数据库!',
    onOk: async () => {
      // 调整数据库 列参数
      //lanMuData.accId = getCurrentAccountName(false)
      lanMuData.objects = JSON.stringify(filterModifyData(dynamicColumnData.value, dynamicColumnDataCopy))
      if (lanMuData.objects == '[]') {
        createWarningModal({title: '温馨提示', content: '请先做修改后再进行确认同步数据库！'})
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
    let arr = key.split('-');
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
    let arr = key.split('-');
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
    let arr = key.split('-');
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
    } catch (e) {
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
  let a = []
  a = getColumns()
  let last = a.pop()
  let newA = JSON.parse(JSON.stringify(CrudApi.columns))
  newA = assemblyDynamicColumn(dynamicColumnData.value, newA)
  newA.push(last)
  setColumns(newA)
  initTableWidth(newA)
  //searchConditonList.value = newA
}

function resetDynamicColumnData() {
  // 先从数据查询是否已经设置
  //lanMuData.accId = getCurrentAccountName(false)
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
  })
}

function initTableWidth(thisCs) {
  let total = 60
  // debugger
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
    tableRef.value.$el.style.setProperty('width', (windowWidth + 40 - f) + 'px')
  } else {
    if (visible.value && (windowWidth - 260) < total) total -= (total - (windowWidth - 260))
    totalColumnWidth.value = total
    tableRef.value.$el.style.setProperty('width', (total + 40) + 'px')
  }
}

/*栏目设置end*/
const tableSelectedRowKeys = ref([])
const tableSelectedRowObjs = ref([])
const onSelectChange = (selectedRowKeys, objs) => {
  tableSelectedRowKeys.value = selectedRowKeys
  tableSelectedRowObjs.value = objs
};
const editBefore = () => {
  if (tableSelectedRowKeys.value.length == 0) {
    message.info("请选择需要进行修改的行且只能是一行！")
  } else {
    openEdit(tableSelectedRowObjs.value[0], false)
  }
}
const delBefore = async () => {
  if (tableSelectedRowKeys.value.length != 1) {
    message.info("请至少选择一行进行删除！")
  } else {
    for (let i = 0; i < tableSelectedRowObjs.value.length; i++) {
      let entity = tableSelectedRowObjs.value[i]
      let flag = false
      entity.beiyong2 = lanMuData.username
      await deleteOrganize(entity).then(async (res) => {
        if (null != res && res.isOk == false) {
          await pointMessage({
            title: '处理结果',
            content: `删除失败,该组织下已经建立了${res.type == 'D02' ? '公司' : '下级组织'},无法进行删除！`,
            delay: true
          })
          flag = true
        } else {
          // 查找组织所属的会计科目模板
          /* let template= await findByTOrganization(entity.uniqueCode)
           if(template.length>0){
             // 删除会计科目
             await delCodeByTemplate(template[0].id)
           }*/
          writeLog("删除", entity, null)
          reload()
          await pointMessage({title: '处理结果', content: '删除成功！', delay: true})
        }
      }).catch(async () => {
        flag = true
        await pointMessage({title: '处理结果', content: '删除失败！', delay: true})
      })
      if (flag) break
    }
  }
}
watch(activeKey, async (v) => {
  if (!v && getDataSource().length != cardList.value.length) await cardReload()
})
const [registerAuthorPage, {openModal: openAuthorPage}] = useModal()
const openAuthor = () => {
  let data = {}
  openAuthorPage(true, {
    data
  })
}
const [registerRecyclePage, {openModal: openRecyclePage}] = useModal()

const recycleList = ref([])
const openRecycleDel = async () => {
  // 获取回收站数据
  recycleList.value = await getOrganizeDelAll()
  if (recycleList.value.length == 0) {
    await pointMessage({title: '温馨提起', content: '当前回收站为空！', delay: true})
  } else {
    openRecyclePage(true, {
      dynamicTenantId: '',
      dataList: recycleList.value.map(it => ({
        id: it.id,
        uniqueCode: it.uniqueCode,
        code: it.orgCode,
        name: it.orgSimpName,
        delName: it.delName,
        delDate: it.delDate
      }))
    })
  }
}

const recycleModify = async (e) => {
  if (e.type == 'reduce') {
    const data = e.data
    let model = recycleList.value.filter(it=>it.id == e.data.id)[0]
    await reductionOrganize(data)
    e.callback()
    writeLog("还原",model,null)
    reload()
    await cardReload()
  } else if (e.type == 'del') {
    const data = e.data
    let model = recycleList.value.filter(it=>it.id == e.data.id)[0]
    await deleteOrganizeTrue(data).then(async (res) => {
      if (null != res && res.isOk == false) {
        await pointMessage({
          title: '处理结果',
          content: `删除失败,该组织下已经建立了${res.type == 'D02' ? '公司' : '下级组织'},无法进行删除！`,
          delay: true
        })
      } else {
        // 删除科目模板及科目
        // await pointMessage({title: '处理结果', content: '删除成功！', delay: true})
        let templateInfo = await findByTOrganization(data.uniqueCode)
        await accTemplateDel(templateInfo[0].id);
        await codekemuDel(templateInfo[0].id);
        e.callback()
        writeLog("移除",model,null)
      }
    }).catch(async () => await pointMessage({title: '处理结果', content: '删除失败！', delay: true}))
  } else {
      //导出
    startExport('del')
  }
}

function startExport(type) {
  // console.log(arrHeader)
  // 保证data顺序与header一致
  let name = '自定义项设置'
  let dataList = []
  let keys = CrudApi.columns.map(it=>it.dataIndex)
  let titleList = CrudApi.columns.map(it=>it.title)
  if (type=='table'){
    name = '组织信息列表'
    dataList =  getDataSource().map((item) => keys.map(column=>transformText(column,item[column])));
  }else {
    name = '组织信息回收站列表'
    titleList.push(...['删除人','删除时间'])
    keys.push(...['delName','delDate'])
    dataList =  recycleList.value.map((item) => keys.map(column=>transformText(column,item[column])));
  }
  if (dataList.length == 0){
      createWarningModal({title: '温馨提示', content: '暂未发现可进行导出的数据！'})
  }else {
    aoaToSheetXlsx({
      data: dataList,
      header: titleList,
      filename: name+'.xlsx',
    });
  }
}

/************ 日志 *************/
async function writeLog(action, a, content) {
  /************** 记录操作日志 ****************/
  if (action == '修改') {
  let old = getDataSource().filter(it => it.id == a.id)[0]
    let keys = (Object.keys(old)).filter(k => old[k] != a[k] && k != 'key')
    let text = `操作内容【修改组织信息】,`+'组织代码【' + a.orgCode + '】,组织名称【' + a.orgName + '】,'
    for (let i = 0; i < keys.length; i++) {
      const k = keys[i];
      let t = CrudApi.columns.filter(t => t.dataIndex == k)[0]?.title
      text += `${t}【${transformText(k, old[k])},${transformText(k, a[k])}】;`
    }
    content = text.substring(0, text.length - 1)
  }
  let map = {
    loginTime: new Date(+new Date() + 8 * 3600 * 1000).toJSON().substr(0, 19).replace("T", " "),
    userId: useUserStoreWidthOut().getUserInfo.username,
    userName: useUserStoreWidthOut().getUserInfo.realName,
    optModule: 'group',
    optFunction: '组织',
    optRange: '2',
    uniqueCode: '',
    optAction: action,
    optContent: content || `操作内容【${action}组织信息】,` + '组织代码【' + a.orgCode + '】,组织名称【' + a.orgName + '】,组织简称【' + a.orgSimpName + '】',
  }
  await saveLog(map)
}

function transformText(key, value) {
  let text = value
  switch (key) {
    case "uniqueGroupCode":
      text = formatGroupInCharge(value)
      break;
    case "uniqueAccStandard":
      text = formatAcountStInCharge(value)
      break;
    case "industryclassCode":
      text = formatHyInCharge(value)
      break;
    case "uniqueCodeZone":
      text = formatXzInCharge(value)
      break;
    case "orgSuperior":
      text = formatZzInCharge(value)
      break;
    case "countryId":
      text = formatDqInCharge(value)
      break;
  }
  return text
}
const logCallback = (e) => {
    writeLog("分配",{},e)
}
/************ 日志 *************/
</script>
<style lang="less">
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

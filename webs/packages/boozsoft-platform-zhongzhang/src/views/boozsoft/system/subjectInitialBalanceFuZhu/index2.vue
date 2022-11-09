<template>
  <div class="app-container">
    <div class="app-container-top">
      <div class="app-container-top-one">
        <div class="act-one-d-left" style="display: inline-flex;">
          <ProfileOutlined style="color: #0096c7;font-size: 60px;margin-top: 10px;"/>
          &emsp;
          <AccountPicker theme="three" @reloadTable="dynamicAdReload" style="margin-top: 10px;"/>
        </div>
        <div class="act-one-d-title">
          <span class="acto-d-title-span">辅助科目期初余额</span>
          <br>
          <span style="color: rgb(102, 102, 102);font-weight: bold;margin-left: 4px;">年度：</span>
          <a-select v-model:value="iyearselected" style="width: 120px;" @change="/*yearChange*/findAllInitialBalance">
            <a-select-option :value="item.iyear" v-for="(item, i) in iyearlist">{{
                item.iyear
              }}
            </a-select-option>
          </a-select>
        </div>
        <div class="act-one-d-btn-group" style="min-width: 476px;">
          <Button v-if="ibookflg" class="actod-btn" @click="qcjzbtn('0')">恢复记账</Button>
          <Button v-if="!ibookflg" class="actod-btn" @click="editflg(edittext)">{{
              edittext
            }}
          </Button>
          <span v-if="roweditflg">
           <Button class="actod-btn" @click="importPop('fz')">导入</Button>
          <Button class="actod-btn" @click="emptyAll2">清空</Button>
        </span>
          <Button class="actod-btn" @click="ssphbtn">试算</Button>
          <Button class="actod-btn actod-btn-last" ant-click-animating-without-extra-node="false" @click="closeCurrent()" >退出</Button>
          <br>
          <div class="act-two-d-right" style="display: inline-flex">
            <div class="acttd-right-d-search">
              <a-select v-model:value="selectSearchValue" class="acttdrd-search-select" @change="selectSearch2">
                <a-select-option value="ccode">科目编码</a-select-option>
                <a-select-option value="ccodeName">科目名称</a-select-option>
                <a-select-option value="cclass">余额方向</a-select-option>
                <a-select-option value="fuzhu">辅助项</a-select-option>
                <a-select-option value="menterage">计量单位</a-select-option>
                <a-select-option value="currencyType">外币币种</a-select-option>
              </a-select>
              <a-input-search placeholder="" v-model:value="inputsearchtext" style="width: 150px;margin-top: 4px;" class="acttdrd-search-input" @search="selectSearch"/>
            </div>
            <div class="acttd-right-d-btns">
              <Button class="acttdrd-btn" @click="findAllInitialBalance()/*,findByAccStyleAll()*/">
                <SyncOutlined :style="{ fontSize: '14px' }"/>
              </Button>
              <Button class="acttdrd-btn" ant-click-animating-without-extra-node="false"
                      @click="exportexcel">
                <UsbOutlined/>
              </Button>
              <Button class="acttdrd-btn" ant-click-animating-without-extra-node="false">
                <PrinterOutlined/>
              </Button>
              <a-popover placement="bottom" v-model:visible="visible3">
                <template #content>
<!--                  <a-popconfirm-->
<!--                    ok-text="保存"-->
<!--                    cancel-text="关闭"-->
<!--                    @confirm="confirm"-->
<!--                    @cancel="cancel">-->
<!--                    <template #icon><b>栏目设置</b><br></template>-->
<!--                    <template #title>-->
<!--                      <div style="width: 640px">-->
<!--                        <a-table bordered :class="'a-table-font-size-12'"-->
<!--                                 :data-source="dynamicColumnData" :columns="dynamicColumns"-->
<!--                                 childrenColumnName="children" :pagination="false"-->
<!--                                 style="max-height: 500px;overflow-y: auto" class="lanmu-table">-->
<!--                          <template #checkBox="{ text, record }">-->
<!--                            <a-checkbox v-model:checked="record.check" :disabled="record.isFixed"/>-->
<!--                          </template>-->
<!--                          <template #widthInput="{ text, record }">-->
<!--                            <div class="editable-cell">-->
<!--                              <div v-if="editableData[record.key]"-->
<!--                                   class="editable-cell-input-wrapper">-->
<!--                                <a-input type="number" v-model:value="editableData[record.key].width"-->
<!--                                         @pressEnter="save(record.key,record.min,record.max)"-->
<!--                                         style="width: 80px"/>-->
<!--                                <check-outlined class="editable-cell-icon-check"-->
<!--                                                @click="save(record.key,record.min,record.max)"/>-->
<!--                              </div>-->
<!--                              <div v-else class="editable-cell-text-wrapper">-->
<!--                                {{ text || ' ' }}-->
<!--                                <edit-outlined class="editable-cell-icon" @click="edit(record.key)"/>-->
<!--                                <span style="float: right;">{{ record.min + '~' + record.max }}</span>-->
<!--                              </div>-->
<!--                            </div>-->
<!--                          </template>-->
<!--                          <template #nameInput="{ text, record }">-->
<!--                            <div class="editable-cell">-->
<!--                              <div v-if="editableData[record.key]"-->
<!--                                   class="editable-cell-input-wrapper">-->
<!--                                <a-input type="text" v-model:value="editableData[record.key].nameNew"-->
<!--                                         @pressEnter="saveName(record.key)" style="width: 100px"/>-->
<!--                                <check-outlined class="editable-cell-icon-check"-->
<!--                                                @click="saveName(record.key)"/>-->
<!--                              </div>-->
<!--                              <div v-else class="editable-cell-text-wrapper">-->
<!--                                {{ text || ' ' }}-->
<!--                                <edit-outlined class="editable-cell-icon" @click="edit(record.key)"/>-->
<!--                              </div>-->
<!--                            </div>-->
<!--                          </template>-->
<!--                          <template #alignRadio="{ text, record }">-->
<!--                            <a-radio-group default-value="a" size="small" v-model:value="record.align"-->
<!--                                           :disabled="record.align==''">-->
<!--                              <a-radio-button value="left">-->
<!--                                左-->
<!--                              </a-radio-button>-->
<!--                              <a-radio-button value="center">-->
<!--                                中-->
<!--                              </a-radio-button>-->
<!--                              <a-radio-button value="right">-->
<!--                                右-->
<!--                              </a-radio-button>-->
<!--                            </a-radio-group>-->
<!--                          </template>-->
<!--                        </a-table>-->
<!--                      </div>-->
<!--                    </template>-->
<!--                    <Button style="width: 120px;margin-bottom: 2px">栏目设置</Button>-->
<!--                  </a-popconfirm>-->
<!--                  <br/>-->
                  <DynamicColumn :defaultData="initDynamics()['DATA1']" :dynamicData="dynamicColumnData" :lanmuInfo="lanMuData" @reload="reloadColumns"/>
                  <span class="group-btn-span-special2" @click="pageParameter.showRulesSize = 'MAX'"
                        :style="pageParameter.showRulesSize==='MAX'?{backgroundColor: '#0096c7',color: 'white'}:''">
                <SortDescendingOutlined/>&nbsp;大号字体&ensp;<CheckOutlined
                    v-if="pageParameter.showRulesSize==='MAX'"/></span><br/>
                  <span class="group-btn-span-special2" @click="pageParameter.showRulesSize = 'MIN'"
                        :style="pageParameter.showRulesSize==='MIN'?{backgroundColor: '#0096c7',color: 'white'}:''">
                <SortAscendingOutlined/>&nbsp;小号字体&ensp;<CheckOutlined
                    v-if="pageParameter.showRulesSize==='MIN'"/></span>
                </template>
                <Button class="acttdrd-btn">
                  <SettingFilled :style="{ fontSize: '14px' }"/>
                </Button>
              </a-popover>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="bg-white"
         :style="{height: (windowHeight+40)+'px',display: 'inline',width: showCatalog?'90px':'20px',float: 'left',marginTop: '5px',overflow:showCatalog?'auto':'hidden',overflowY:'hidden'}">
        <Tabs v-model:activeKey="activeKey" @change="tabsCheck" tab-position="left" style="font-weight: bold">
          <TabPane
            v-for="(item, i) in styleNamelist"
            :key="item.cclass"
            :tab="`${item.cclass !== '全部' ? i + ' ' + item.cclass : item.cclass}`"
          />
        </Tabs>
    </div>
    <div class="app-container-bottom" :style="{height: (windowHeight+60)+'px',display: 'inline',width: showCatalog?'calc( 100% - 90px )':'calc( 100% - 22px )',float: 'right',marginTop: '5px'}">
      <BasicTable
        ref="tableRef"
        :row-selection="{ type: 'checkbox',fixed: true,selectedRowKeys: tableSelectedRowKeys, /*onChange: onSelectChange */ getCheckboxProps:rowSelection.getCheckboxProps}"
        :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
        :columns="tableColumns"
        :scroll="{ x: totalColumnWidth,y: pageParameter.queryMark=='1'?(windowHeight-60):(windowHeight-40) }"
        :dataSource="tableData"
        :loading="loading"
        size="small"
        @register="registerTable"
        :showIndexColumn="false"
        :indexColumnProps="{width: 120, fixed: 'left', title: '序号'}"
        :pagination="{
          pageSize: 200,
          simple:true
        }"
      >
        <template #md="{ record }">
          <template
            v-if="roweditflg && ((record.rowType=='1' && record.children == null) || ( record.rowType=='2'))">
            <template v-if="record?.mdEdit">
              <a-input-number v-model:value="record.md"
                              :formatter="value =>`${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')"
                              :parser="value =>`${value}`.replace(/\$\s?|(,*)/g,'')"
                              :precision="2"
                              style="width: 90%;margin: -5px 0;height: 25px;line-height: 25px;"
                              @keyup.enter="record.mdEdit = null;record.mc='0';dbSave2(record)"/>
              <CheckOutlined @click="record.mdEdit = null;record.mc='0';dbSave2(record);"/>
            </template>
            <template v-else>
              <span class="a-table-font-arial" :style="(record.md < 0 )?{color: 'red'}:{}"> {{
                  record.md == 0 ? '' : money(record.md)
                }}
              <EditOutlined @click="record.mdEdit = true;record.oldMd=record.md"/></span>
            </template>
          </template>
          <template v-else>
            <span class="a-table-font-arial" :style="(record.md < 0 )?{color: 'red'}:{}">{{
                record.md == 0 ? '' : money(record.md)
              }}</span>
          </template>
        </template>
        <template #mc="{ record }">
          <template
            v-if="roweditflg && ((record.rowType=='1' && record.children == null) || ( record.rowType=='2' ))">
            <template v-if="record?.mcEdit">
              <a-input-number v-model:value="record.mc"
                              :formatter="value =>`${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')"
                              :parser="value =>`${value}`.replace(/\$\s?|(,*)/g,'')"
                              :precision="2" style="width: 90%;margin: -5px 0"
                              @keyup.enter="record.mcEdit = null;record.md='0';dbSave2(record)"/>
              <CheckOutlined @click="record.mcEdit = null;record.md='0';dbSave2(record)"/>
            </template>
            <template v-else>
              <span class="a-table-font-arial" :style="(record.mc < 0 )?{color: 'red'}:{}">{{
                  record.mc == 0 ? '' : money(record.mc)
                }}
              <EditOutlined @click="record.mcEdit = true;record.oldMc=record.mc"/></span>
            </template>
          </template>
          <template v-else>
            <span class="a-table-font-arial" :style="(record.mc < 0 )?{color: 'red'}:{}">{{
                record.mc == 0 ? '' : money(record.mc)
              }}</span>
          </template>
        </template>
        <template #fuzhu="{ record }">
          <template v-if="record.rowType == null"><!---->
            <span>{{
                `${record.ccode} - ${record.ccodeName} ${record.fuzhu != '' ? `（${transformName(record.fuzhu)}）` : ``}`
              }}</span>
            <EditOutlined style="color: #0a84ff;margin-left: 1em;" v-if="roweditflg"
                          @click="setModal1Visible(record)"/>
          </template>
          <template v-if="record.rowType == '1'">
            <span style="margin-left: 1em;">{{ `${record.text}` }} </span>
<!--            <EditOutlined style="color: #0a84ff;margin-left: 1em;"/>-->
          </template>
          <template v-if="record.rowType == '2'">
            <span style="margin-left: 2em;">{{ `${record.text}` }}</span>
<!--            <EditOutlined style="color: #0a84ff;margin-left: 1em;"/>-->
          </template>
        </template>

        <template #summary>
          <TableSummary fixed>
            <TableSummaryRow style="background-color: #cccccc;">
              <TableSummaryCell class="nc-summary" :index="0" :align="'center'" :colspan="2" style="border-right: none;">合计</TableSummaryCell>
              <TableSummaryCell class="nc-summary" :index="1" :align="'right'" style="border-right: none;">{{ summaryTotals.md }}</TableSummaryCell>
              <TableSummaryCell class="nc-summary" :index="1" :align="'right'" style="border-right: none;">{{ summaryTotals.mc }}</TableSummaryCell>
            </TableSummaryRow>
          </TableSummary>
        </template>
      </BasicTable>
      <div class="pagination-text" :style="{top: (windowHeight+15)+'px',left:(totalColumnWidth-600)+'px'}" v-show="showPageNumber">
        {{`共 ${tableDataAll.length} 条记录&emsp;每页 1000 条`}}
      </div>
      <ImprotExcelFz @save="reloadProjects" @register="registerImportPageFz"/>
      <ThisEdit @register="registerEditPage" @save="findAllInitialBalance" :placement="'top'"
                :database="databaseTrue"></ThisEdit>
      <CodeModalPop @throwData="modalData" @register="registerCodePopPage"/>

      <a-modal v-model:visible="modal2Visible" centered @ok="modal2Visible = false">
        <template #title>
          <div style="display: flex;" class="vben-basic-title">
            <img src="/create.svg" style="width:25px;margin-right: 10px;"/>
            <span style="line-height: 25px;font-size: 16px;">科目期初余额录入</span>
          </div>
        </template>
        <template #footer>
          <Button key="back"
                  @click="(modal2Visible = false), (md = ''), (mc = ''), (ndS = ''), (ncS = ''), (nfrat = '')">
            取消
          </Button>
          <Button key="submit" type="primary" @click="submitPop" :disabled="lock0K">确定</Button>
        </template>
        <div class="customize-modal">
          <ul>
            <li>
              <label>本币借方金额:</label>
              <a-input
                v-model:value="md"
                @blur="md = md != '' ? money(md) : '';mc = md != '' ? '' : mc;ncS = '';"
                @click="md = String(md).replace(/,/g, '')"
                autocomplete="off"
                class="special-ulli-input"
              />
            </li>
            <li>
              <label>本币贷方金额:</label>
              <a-input
                v-model:value="mc"
                @blur="mc = mc != '' ? money(mc) : '';md = mc != '' ? '' : md;ndS = '';"
                @click="mc = String(mc).replace(/,/g, '')"
                autocomplete="off"
                class="special-ulli-input"
              />
            </li>
            <template v-if="isLj">
              <Divider style="height: 1px; background-color: #0096c7"/>
              <li>
                <label>累计借方金额:</label>
                <a-input
                  v-model:value="mdLj"
                  @blur="mdLj = mdLj != '' ? money(mdLj) : '';"
                  @click="mdLj = String(mdLj).replace(/,/g, '')"
                  autocomplete="off"
                  class="special-ulli-input"
                />
              </li>
              <li>
                <label>累计贷方金额:</label>
                <a-input v-model:value="mcLj"
                         @blur="mcLj = mcLj != '' ? money(mcLj) : '';"
                         @click="mcLj = String(mcLj).replace(/,/g, '');"
                         autocomplete="off"
                         class="special-ulli-input"
                />
              </li>
            </template>
            <template v-if="nfratflg">
              <Divider style="height: 1px; background-color: #0096c7"/>
              <li>
                <label>外币金额:&emsp;&emsp;</label>
                <a-input
                  v-model:value="nfrat"
                  @blur="nfrat = nfrat != '' ? money(nfrat) : ''"
                  @click="nfrat = String(nfrat).replace(/,/g, '')"
                  autocomplete="off"
                  class="special-ulli-input"/>
              </li>
              <li v-if="isLj">
                <label>外币累计借方:</label>
                <a-input
                  v-model:value="nfratLj"
                  @blur="nfratLj = nfratLj != '' ? money(nfratLj) : ''"
                  @click="nfratLj = String(nfratLj).replace(/,/g, '')"
                  autocomplete="off"
                  class="special-ulli-input"/>
              </li>
              <li v-if="isLj">
                <label>外币累计贷方:</label>
                <a-input
                  v-model:value="nfratLj2"
                  @blur="nfratLj2 = nfratLj2 != '' ? money(nfratLj2) : ''"
                  @click="nfratLj2 = String(nfratLj2).replace(/,/g, '')"
                  autocomplete="off"
                  class="special-ulli-input"
                />
              </li>
            </template>

            <template v-if="numflg">
              <Divider style="height: 1px; background-color: #0096c7"/>
              <li>
                <label>数量:&emsp;&emsp;&emsp;&emsp;</label>
                <a-input
                  v-model:value="ncnum"
                  autocomplete="off"
                  class="special-ulli-input"
                />
              </li>
              <li v-if="isLj">
                <label>累计借方数量:</label>
                <a-input
                  v-model:value="ncnumLj"
                  autocomplete="off"
                  class="special-ulli-input"
                />
              </li>
              <li v-if="isLj">
                <label>累计贷方数量:</label>
                <a-input
                  v-model:value="ncnumLj2"
                  autocomplete="off"
                  class="special-ulli-input"/>
              </li>
            </template>
          </ul>
        </div>
      </a-modal>
      <a-modal
        width="460px"
        title="期初余额试算平衡"
        v-model:visible="modal1Visible"
        centered
        :footer="null"
      >
        <div class="trial-balance-div">
          <div class="tb-div-dashboard">
            <Progress type="dashboard" :percent="textObj.proportion"/>
          </div>
          <div class="tb-div">
            <span>{{ ssphtext }}</span><br/>
            <div class="tb-div-line">
              <Progress stroke-linecap="square" :percent="textObj.left.proportion"
                        :show-info="false"/>
              <span>{{ textObj.left.text }}</span><br/>
              <span>{{ textObj.left.value == '' ? '0元' : textObj.left.value + '元' }}</span>
            </div>
            <div class="tb-div-line">
              <Progress stroke-linecap="square" :percent="textObj.right.proportion"
                        :show-info="false"/>
              <span>{{ textObj.right.text }}</span><br/>
              <span>{{ textObj.right.value == '' ? '0元' : textObj.right.value + '元' }}</span>
            </div>
          </div>
          <div class="tb-div-bottom">
            <Button type="primary" size="small" @click="modal1Visible = false">关闭</Button>
          </div>
        </div>

        <!--        <br/>
                <a-table
                  v-if="cwflag"
                  :pagination="false"
                  :columns="ssphcolumns"
                  :data-source="ssphlist"
                />
                <a-table
                  v-if="ysflag"
                  :pagination="false"
                  :columns="ssphcolumns"
                  :data-source="ssphlist"
                />
                <span style="flot: right" size="5" v-if="ysflag">{{ ystext }}</span>
                <br/>
                <a-table
                  v-if="ysflag"
                  :pagination="false"
                  :columns="ssphcolumns"
                  :data-source="ssphlist2"
                />-->
      </a-modal>
      <a-modal
        title="选择账套"
        style="top: 20px"
        v-model:visible="modal3Visible"
        @ok="modalok"
      >
        <a-select
          v-model:value="selectDataBaseValue"
          show-search
          style="width: 100%"
        >
          <a-select-option v-for="item in databaseAll" :value="item.coCode">
            {{ item.coCode + '-' + item.accName }}
          </a-select-option>
        </a-select>
      </a-modal>
      <a-modal
        width="800px"
        title="科目结转对照表"
        v-model:visible="carryDownModal"
        @ok="carryDownOk"
        :closable="false"
        okText="确认结转"
      >
        <a-table bordered :loading="loading3" :pagination="false" :data-source="dataSourceTable"
                 :columns="columns" :class="'a-table-font-size-12'" :scroll="{y:240}">
          <template #upCcode="{ record }">
            {{ setString(record.upCcode + '-' + record.upCcodeName, 30) }}
          </template>
          <template #upMd="{ record }">
            <span v-if="record.upMd!==0" style="float: right;">{{ money(record.upMd) }}</span>
            <span v-if="record.upMc!==0" style="float: right;">{{ money(record.upMc) }}</span>
          </template>
          <template #thisCcode="{ record }">
            <span v-if="record.thisCcodeName!=''">{{
                setString(record.thisCcode + '-' + record.thisCcodeName, 30)
              }}</span>
            <span v-else>
              <a-select show-search v-model:value="record.thisCcode" :filter-option="filterOption"
                        style="width: 80%">
                <a-select-option :value="item2.ccode +'-'+ item2.ccodeName" :key="item2.ccode"
                                 :data="item2.ccode +'-'+ item2.ccodeName"
                                 v-for="(item2, j) in codelist">
                  {{ item2.ccode + '-' + item2.ccodeName }}
                </a-select-option>
              </a-select>
              &nbsp;
              <a style="font-weight: bold;font-size: 18px;"><LinkOutlined
                @click="openSelectCode(record)"/></a>
            </span>
          </template>
        </a-table>
      </a-modal>
    </div>
  </div>
</template>
<script setup lang="ts">
import {
  ProfileOutlined,
  EditOutlined,
  CheckOutlined,
  LinkOutlined,
  SettingFilled,
  SyncOutlined,
  FilterFilled,
  SortDescendingOutlined,
  SortAscendingOutlined,
  PrinterOutlined,
  UsbOutlined, ShrinkOutlined, ArrowsAltOutlined
} from '@ant-design/icons-vue';
import {BasicTable, useTable} from '/@/components/Table';
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker.vue";
import CodeModalPop from '/@/views/boozsoft/system/acccode2/popup/modalPop.vue';
import {
  DatePicker as ADatePicker,
  Select as ASelect,
  Input as AInput, InputNumber as AInputNumber,
  Popover as APopover,
  Radio as ARadio,
  Pagination as APagination,
  Popconfirm as APopconfirm,
  Table as ATable,
  Checkbox as ACheckbox,
  Statistic as AStatistic, Modal as AModal,
  Drawer as ADrawer, Collapse, message, Divider, Button, Progress,Tabs,Table
} from "ant-design-vue"
import {BasicTree} from '/@/components/Tree';
import {useModal} from '/@/components/Modal';
import {
  findAllSubjectInitialBalance,
  findByFunctionModule,
  saveSubjectInitialBalance,
  delInitalBalance,
  findByIperiodFlag,
  findByAccvoucherIbook,
  findBySubjectInitalBalabceIbook,
  ssph,
  qcjz,
  qcjzsum,
  pzjzibook,
  emptyAll,
  findAllSubjectInitialBalanceFuZhuList,
  saveTaskInfo,
  findAllSubjectInitialBalanceNewFuZhu, modifySubjectInitialBalanceFuZhu,
} from '/@/api/subjectInitialBalance/subjectInitialBalance';
import {onMounted, ref, reactive} from 'vue';
import ImprotExcelFz from '/@/views/boozsoft/system/subjectInitialBalance/popup/improtExcelFz.vue';
import ThisEdit from '../subjectInitialBalanceFuZhu/popup/edit.vue'
import {jsonToSheetXlsx} from '/@/components/Excel';
import {useUserStore, useUserStoreWidthOut} from '/@/store/modules/user';
import {
  countByTaskId, delFunctionModule,
  hasBlank
} from "/@/api/task-api/tast-bus-api";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {findByAccId, findDataBase} from "/@/api/record/system/account";
import {
  company_findAllByIyear,
  company_findByIyearOrderByCcodeTree,
  company_findByUpYearCodeAndThisYearCode,
  company_setNewYearQc,
  groupStandardAndTemplate
} from "/@/api/codekemu/codekemu";
import {acctemplateFindByAccId, findTemplateByAccId} from '/@/api/acctemplate/acctemplate';
import {findPeriod, findYearMinDate} from "/@/api/record/generalLedger/data";
import {onBeforeRouteLeave} from "vue-router";
import {findDbLanMuList, saveLanMuList} from "/@/api/record/system/accvoucher";
import {cloneDeep} from "lodash-es";
import {useMessage} from "/@/hooks/web/useMessage";
import {countAllByAccountIdAndIyear, findPeriodByYaer} from "/@/api/sys_period/data";
import {useTabs} from "/@/hooks/web/useTabs";
import router from "/@/router";
import {findByAccStyle, findByStandardUnique} from "/@/api/accstandard/accstandard";
import {findKeyLabelAll, findKeyLabelOtherAll} from "/@/api/record/system/fuZhuHeSuan";
import {JsonTool} from "/@/api/task-api/tools/universal-tools";
import {toThousandFilter} from "/@/utils/calculation";

const dataSourceTable: any = ref([]);
const columns = [
  {
    title: '上年科目',
    dataIndex: 'upCcode',
    key: 'upCcode',
    slots: {customRender: "upCcode"}
  },
  {
    title: '上年余额',
    dataIndex: 'upMd',
    key: 'upMd', slots: {customRender: "upMd"}, width: 150
  }, {
    title: '本年科目',
    dataIndex: 'thisCcode',
    key: 'thisCcode', slots: {customRender: "thisCcode"}
  },
];
const ARangePicker = ADatePicker.RangePicker
const ASelectOption = ASelect.Option
const AInputSearch = AInput.Search
const ARadioGroup = ARadio.Group
const ARadioButton = ARadio.Button
const CollapsePanel = Collapse.Panel
const AStatisticCountdown = AStatistic.Countdown;
const TabPane = Tabs.TabPane
const TableSummary = Table.Summary
const TableSummaryRow = Table.Summary.Row
const TableSummaryCell = Table.Summary.Cell

const {closeCurrent} = useTabs(router);
// 期初记账/恢复期初记账  显示
const ibookflg = ref(false);
// 计时数值
const deadline = ref(Date.now() + 1000 * 10);
// 借贷方金额
const md = ref('');
const mc = ref('');
// 借贷方数量
const numflg = ref(false);
const ncnum = ref('');
// 外币金额
const nfrat = ref('');
const nfratflg = ref(false);
const visible = ref(false);

const isLj = ref(false)
const showCatalog = ref(true)
// 累计借贷方金额
const mdLj = ref('');
const mcLj = ref('');
// 累计借贷方数量
const numflgLj = ref(false);
const ncnumLj = ref('');
const ncnumLj2 = ref('');
// 累计外币金额
const nfratLj = ref('');
const nfratLj2 = ref('');
const nfratflgLj = ref(false);
const showPageNumber = ref(false);
// 弹框2状态
const carryDownModal = ref(false);
const modal2Visible = ref(false);
const lock0K = ref(false);
const edittext = ref('开始编辑');
// 弹框1状态
const modal1Visible = ref(false);
// 试算平衡list
const ssphtext = ref('');
const ystext = ref('');
const textObj = {
  proportion: 100,
  left: {text: '', value: '0', proportion: 100},
  right: {text: '', value: '0', proportion: 100}
}
const ssphlist = ref([]);
const ssphlist2 = ref([]);
// 试算平衡表头
const ssphcolumns = ref([
  {
    title: '科目类型',
    dataIndex: 'name',
    width: 100
  },
  {
    title: '借方金额',
    dataIndex: 'md',
  },
  {
    title: '贷方金额',
    dataIndex: 'mc',
  },
  {
    title: '方向',
    dataIndex: 'fx',
    width: 50
  },
  {
    title: '余额',
    dataIndex: 'yue',
  },
  {
    title: '科目类型',
    dataIndex: 'name2',
    width: 100
  },
  {
    title: '借方金额',
    dataIndex: 'md2',
  },
  {
    title: '贷方金额',
    dataIndex: 'mc2',
  },
  {
    title: '方向',
    dataIndex: 'fx2',
    width: 50
  },
  {
    title: '余额',
    dataIndex: 'yue2',
  },
]);
// 单元格可编状态
const roweditflg = ref(false);
const loading = ref(false);
const loading3 = ref(false);
// 账套启用期间
const begintime = ref('');
// 账套启用期间
const iyearlist = ref([]);
// 选中年度
const iyearselected = ref('');
// 显示末级科目
const lastCode = ref(true);
const tableDataAll = ref([]);
const tableData = ref([]);
// 任务管理信息
const taskinfo = ref('');
// 期初弹框回调信息
const datainfo = ref('');
// 期初弹框回调信息
const types = ref('');
// 数据库模式名称
const database = ref('');
const databaseTrue = ref('');
const databaselist = ref([]);
const userinfo = ref({
  databaseNum: database.value,
  username: useUserStoreWidthOut().getUserInfo.username,
  realName: useUserStoreWidthOut().getUserInfo.realName,
  name: useUserStoreWidthOut().getUserInfo.name,
  phone: useUserStoreWidthOut().getUserInfo.phone,
});
const userStore = useUserStore();
const codelist = ref([])
// 字体
const pageParameter = reactive({
  cclass: '全部',
  ccode: '',
  bend: '',
  databasenum: databaseTrue.value,
  queryMark: '1',
  showRulesSize: 'MIN',
  filterConditon: {
    amountMinMc: '',
    amountMaxMc: '',
    amountMinMd: '',
    amountMaxMd: ''
  },
  uniqueAccStandard: '',
  templateId: '',
  activeKey: '',
  reloadMark: false,
  companyCode: '',
  companyName: '',
  ifUnit: false,
})
const accountingInfoModel:any = reactive({
  definitions: null,
  details: null,
})
// 数量/外币 栏目是否显示
const menterage = ref(true)
const currencyType = ref(true)
const tableColumns = ref([
  /* {
     title: "操作",
     dataIndex: "caozuo",
     slots: {customRender: "caozuo"}
   },
   {
     title: "科目编码",
     dataIndex: "ccode",
     align: "left"
   },
   {
     title: "科目名称",
     dataIndex: "ccodeName",
     align: "left"
   },
   {
     title: "方向",
     dataIndex: "bprogerty",
     slots: {customRender: "bprogerty"},
   },*/
  {
    title: "辅助科目及明细",
    dataIndex: "fuzhu", slots: {customRender: 'fuzhu'},
  },
  /*{
    title: "计量单位",
    dataIndex: "menterage",
    defaultHidden: menterage,
  },
  {
    title: "外币币种",
    dataIndex: "currencyType",
    defaultHidden: currencyType,
  },
  {
    title: "数量",
    dataIndex: "ncnum",
    defaultHidden: menterage,
    slots: {customRender: "ncnum"},
  },
  {
    title: "外币金额",
    dataIndex: "nfrat",
    slots: {customRender: "nfrat"},
    defaultHidden: currencyType,
  },*/
  {
    title: "本币借方金额",
    dataIndex: "md",
    slots: {customRender: "md"}
  },
  {
    title: "本币贷方金额",
    dataIndex: "mc",
    slots: {customRender: "mc"}
  },
])
const tableColumns2 = ref([
  {
    title: "操作",
    dataIndex: "caozuo",
    defaultHidden: menterage && currencyType,
    slots: {customRender: "caozuo"}
  },
  {
    title: "科目编码",
    dataIndex: "ccode",
    align: "left"
  },
  {
    title: "科目名称",
    dataIndex: "ccodeName",
    align: "left"
  },
  {
    title: '期初借方',
    dataIndex: 'md',
    align: 'right',
    slots: {customRender: 'md'},
  }, {
    title: '期初贷方',
    dataIndex: 'mc',
    align: 'right',
    slots: {customRender: 'mc'},
  },
  {
    title: '累计借方',
    dataIndex: 'ljMd',
    align: 'right',
    slots: {customRender: 'ljMd'},
  },
  {
    title: '累计贷方',
    dataIndex: 'ljMc',
    align: 'right',
    slots: {customRender: 'ljMc'},
  }, {
    title: "方向",
    dataIndex: "bprogerty",
    slots: {customRender: "bprogerty"},
  },
  {
    title: '年初余额',
    dataIndex: 'nYue',
    align: 'right',
    slots: {customRender: 'nYue'},
  }
])
const tableColumns22 = ref([
  {
    title: "录入",
    dataIndex: "caozuo",
    slots: {customRender: "caozuo"},
    fixed: "left",
  },
  {
    title: "科目编码",
    dataIndex: "ccode",
    align: "left",
    fixed: "left",
  },
  {
    title: "科目名称",
    dataIndex: "ccodeName",
    align: "left",
    fixed: "left",
  },
  {
    title: "核算项",
    dataIndex: "fuzhu", align: "left"
  },
  {
    title: '期初余额',
    dataIndex: 'qcyue',
    children: [
      {
        title: '数量',
        dataIndex: 'ncnum',
        align: 'center',
        key: '5-1',
        defaultHidden: menterage,
        slots: {customRender: 'ncnum'},
      },
      {
        title: '外币金额',
        dataIndex: 'nfrat',
        key: '5-2',
        align: 'right',
        defaultHidden: currencyType,
        slots: {customRender: 'nfrat'},
      },
      {
        title: '本币借方',
        dataIndex: 'md',
        key: '5-3',
        align: 'right',
        slots: {customRender: 'md'},
      },
      {
        title: '本币贷方',
        dataIndex: 'mc',
        key: '5-4',
        align: 'right',
        slots: {customRender: 'mc'},
      }
    ],
  },
  {
    title: '累计发生',
    dataIndex: 'ljfs',
    children: [
      {
        title: '数量借方',
        dataIndex: 'ljSlMd',
        align: 'center',
        key: '6-1',
        defaultHidden: menterage,
        slots: {customRender: 'ljSlMd'},
      },
      {
        title: '数量贷方',
        dataIndex: 'ljSlMc',
        align: 'center',
        key: '6-2',
        defaultHidden: menterage,
        slots: {customRender: 'ljSlMc'},
      },
      {
        title: '外币借方',
        dataIndex: 'ljWbMd',
        key: '6-3',
        align: 'right',
        defaultHidden: currencyType,
        slots: {customRender: 'ljWbMd'},
      }, {
        title: '外币贷方',
        dataIndex: 'ljWbMc',
        key: '6-4',
        align: 'right',
        defaultHidden: currencyType,
        slots: {customRender: 'ljWbMc'},
      },
      {
        title: '本币借方',
        dataIndex: 'ljMd',
        key: '6-5',
        align: 'right',
        slots: {customRender: 'ljMd'},
      },
      {
        title: '本币贷方',
        dataIndex: 'ljMc',
        key: '6-6',
        align: 'right',
        slots: {customRender: 'ljMc'},
      }
    ],
  },
  {
    title: '年初余额',
    dataIndex: 'yearYue',
    children: [
      {
        title: '数量',
        dataIndex: 'yearNum',
        key: '7-1',
        align: 'center',
        defaultHidden: menterage,
        slots: {customRender: 'yearNum'},
      },
      {
        title: '外币金额',
        dataIndex: 'yearNfrat',
        key: '7-2',
        align: 'right',
        defaultHidden: currencyType,
        slots: {customRender: 'yearNfrat'},
      },
      {
        title: '本币借方',
        dataIndex: 'yearMd',
        key: '7-3',
        align: 'right',
        slots: {customRender: 'yearMd'},
      },
      {
        title: '本币贷方',
        dataIndex: 'yearMc',
        key: '7-4',
        align: 'right',
        slots: {customRender: 'yearMc'},
      }
    ],
  }
])
// 是否非企业机构标识 1是
const icorp = ref('')
// 公司码
const cocode = ref('')
// 账套详细信息
const databaseinfo = ref('')
// 1是 否独立账套
const independent = ref(false)
const cwflag = ref(false);
const ysflag = ref(false);
const modal3Visible = ref(false);
const selectDataBaseValue = ref('');
const selectSearchValue = ref('ccode');
const bwb = ref('');
const qujian = ref('');
// 所有账套集合
const databaseAll = ref([])
const inputsearchtext = ref('')
const templateSelected = ref('')
const standardSelected = ref('')
// 余额录入弹框
const openInfo = ref({
  ccode: '',
  ccodeName: '',
  fx: '',
  currency: ''
})

const loading2 = ref(true);
const treeData: any = ref([]);
const selectedKeys2: any = ref([]);
const expandedKeys: any = ref([]);
const styleList: any = ref([]);

// 这是示例组件
const [registerImportPageFz, {openModal: openImprotPageFz}] = useModal();

const [registerEditPage, {openModal: openEditPage}] = useModal();
const [registerCodePopPage, {openModal: openCodePopPage}] = useModal();

const tableRef:any = ref(null)
const windowWidth = (document.documentElement.clientWidth - 70)
const windowHeight = (window.innerHeight - 300)
const totalColumnWidth = ref(0)
const [registerTable, {
  reload,
  setColumns,
  getDataSource,
  setTableData,
  getSelectRows
}] = useTable();

function selectSearch2() {
  if (inputsearchtext.value !== '') {
    findAllInitialBalance()
  }
}
const rowSelection = {
  getCheckboxProps: (record) => ({
    disabled: hasBlank(record.ccode)
  }),
};
// 检索条件
async function selectSearch() {
  let all = tableDataAll.value
  if (selectSearchValue.value === 'ccode') {
    tableData.value = all.filter((a) => a.ccode.indexOf(inputsearchtext.value) !== -1);
  }
  if (selectSearchValue.value === 'ccodeName') {
    tableData.value = all.filter((a) => a.ccodeName.indexOf(inputsearchtext.value) !== -1);
  }
  if (selectSearchValue.value === 'cclass') {
    tableData.value = all.filter((a) => a.cclass.indexOf(inputsearchtext.value) !== -1);
  }
  if (selectSearchValue.value === 'bprogerty') {
    tableData.value = all.filter((a) => a.bprogerty.indexOf(inputsearchtext.value) !== -1);
  }
  if (selectSearchValue.value === 'fuzhu') {
    tableData.value = all.filter((a) => a.fuzhu.indexOf(inputsearchtext.value) !== -1);
  }
  if (selectSearchValue.value === 'menterage') {
    tableData.value = all.filter((a) => a.menterage.indexOf(inputsearchtext.value) !== -1);
  }
  if (selectSearchValue.value === 'currencyType') {
    tableData.value = all.filter((a) => a.currencyType.indexOf(inputsearchtext.value) !== -1);
  }

  if (inputsearchtext.value === '') {
    findAllInitialBalance()
  }
}

async function modalok() {
  modal3Visible.value = false
}

async function emptyAllbtn(data) {
  if (data.accvouid !== null) {
    await useRouteApi(emptyAll, {schemaName: databaseTrue.value})(data.accvouid)
    findAllInitialBalance();
  }
}

// 期初记账
async function qcjzbtn(ibook: any) {
  if (ibook === '0') {
    // 查询本年凭证是否记账
    const pzibook = await useRouteApi(pzjzibook, {schemaName: databaseTrue.value})({
      iyear: iyearselected.value,
      databasenum: databaseTrue.value
    })
    if (pzibook > 0) {
      message.error('本年凭证已经记账，不能恢复期初余额记账。请取消凭证记账后再试！');
      return;
    }
  }
  ibookflg.value = !ibookflg.value;
  await useRouteApi(qcjz, {schemaName: databaseTrue.value})({
    iyear: iyearselected.value,
    ibook: ibook,
    databasenum: databaseTrue.value
  })
  findAllInitialBalance();
}

// 倒计时到时间触发
function onFinish() {
  console.log('30分钟到了');
}

// 试算平衡
async function ssphbtn() {
  ssphlist.value = []
  const list1 = await useRouteApi(ssph, {schemaName: databaseTrue.value})({
    iyear: iyearselected.value,
    standardSelected: standardSelected.value,
    databasenum: databaseTrue.value
  })
  let a = list1.filter((li1) => li1.flag === '1');
  let b = list1.filter((li1) => li1.flag === '2');
  let max = a.length < b.length ? b.length : a.length
  for (let i = 0; i < max; i++) {
    ssphlist.value.push(
      {
        name: a[i].name,
        md: a[i].md,
        mc: a[i].mc,
        fx: a[i].fx,
        yue: a[i].yue,
        id: parseInt(a[i].id),
        name2: b[i].name,
        md2: b[i].md,
        mc2: b[i].mc,
        fx2: b[i].fx,
        yue2: b[i].yue,
      }
    )
  }

  let list3 = list1.filter((li1) => li1.flag === '5');
  if (list3[0].cwOrys === '0') {
    let cw = list1.filter((li1) => li1.flag === '3');
    ssphtext.value = '\xa0\xa0' + cw[0].name;
    cwflag.value = true
  } else {
    ssphlist2.value = []
    let aa = list1.filter((li1) => li1.flag === '11');
    let bb = list1.filter((li1) => li1.flag === '22');
    let max = aa.length < bb.length ? bb.blength : aa.length
    for (let i = 0; i < max; i++) {
      ssphlist2.value.push(
        {
          name: aa[i].name,
          md: aa[i].md,
          mc: aa[i].mc,
          fx: aa[i].fx,
          yue: aa[i].yue,
          id: parseInt(aa[i].id),
          name2: bb[i].name,
          md2: bb[i].md,
          mc2: bb[i].mc,
          fx2: bb[i].fx,
          yue2: bb[i].yue,
        }
      )
    }
    let cw = list1.filter((li1) => li1.flag === '3');
    let ys = list1.filter((li1) => li1.flag === '4');
    ssphtext.value = '\xa0\xa0' + cw[0].name;
    ystext.value = '\xa0\xa0' + ys[0].name;
    ysflag.value = true
  }
  textObj.left.text = a.filter(i => i.id == 1).map(i => i.name).join(',')
  textObj.right.text = b.filter(i => i.id == 1).map(i => i.name).join(',')
  if (a.length > 0 || b.length > 0) {
    if (a.length > 0 && a.filter(i => i.name == '合计').length > 0) {
      textObj.left.value = a.filter(i => i.name == '合计')[0].yue
    } else {
      textObj.left.value = '0'
    }
    if (b.length > 0 && b.filter(i => i.name == '合计').length > 0) {
      textObj.right.value = b.filter(i => i.name == '合计')[0].yue
    } else {
      textObj.right.value = '0'
    }
    let l = hasBlank(textObj.left.value) ? 0 : Math.abs(parseFloat(textObj.left.value.replace(',', '')))
    let r = hasBlank(textObj.right.value) ? 0 : Math.abs(parseFloat(textObj.right.value.replace(',', '')))
    if (l == 0 || r == 0) {
      if (l == 0) textObj.left.proportion = 0;
      if (r == 0) textObj.right.proportion = 0;
      if (r == 0 && l == 0) {
        textObj.proportion = 100
      } else {
        textObj.proportion = 50
      }
    } else {
      if (l > r) {
        textObj.left.proportion = 100
        let n = parseInt((r / l) * 100)
        textObj.right.proportion = n
        textObj.proportion = n
      } else if (r > l) {
        textObj.right.proportion = 100
        let n = parseInt((l / r) * 100)
        textObj.left.proportion = n
        textObj.proportion = n
      } else {
        textObj.left.proportion = 100
        textObj.right.proportion = 100
        textObj.proportion = 100
      }
    }
  }
  modal1Visible.value = true;
}

// 导出excel
function exportexcel() {
  var list = []; // 没有单价金额/外币金额
  var list2 = []; // 有单价金额/外币金额
  for (let i = 0; i < tableDataAll.value.length; i++) {
    // 所有末级科目
    list.push({
      ccode: tableDataAll.value[i].ccode,
      ccodeName: tableDataAll.value[i].ccodeName,
      md: tableDataAll.value[i].md == 0 ? '' : tableDataAll.value[i].md,
      mc: tableDataAll.value[i].mc == 0 ? '' : tableDataAll.value[i].mc,
      num: tableDataAll.value[i].md > 0 ? tableDataAll.value[i].ndS == 0 ? '' : tableDataAll.value[i].ndS : tableDataAll.value[i].ncS == 0 ? '' : tableDataAll.value[i].ncS,
      lv: tableDataAll.value[i].mdF,
      wnmoney: tableDataAll.value[i].md > 0 ? tableDataAll.value[i].nfratMd == 0 ? '' : tableDataAll.value[i].nfratMd : tableDataAll.value[i].nfratMc == 0 ? '' : tableDataAll.value[i].nfratMc,
      pjCsettle: tableDataAll.value[i].pjCsettle,
      pjId: tableDataAll.value[i].pjId,
      pjDate: tableDataAll.value[i].pjDate,
      pjUnitName: tableDataAll.value[i].pjUnitName,
    });
  }


  list = list2.length > 0 ? list2 : list;
  jsonToSheetXlsx({
    data: list,
    header: {
      ccode: '科目编码',
      ccodeName: '科目名称',
      md: '借方金额',
      mc: '贷方金额',
      num: '数量',
      lv: '汇率',
      wnmoney: '外币金额',
      pjCsettle: '结算方式编码',
      pjId: '结算票据号',
      pjDate: '票据日期',
      pjUnitName: '对方单位名称',
    },
    filename: '期初余额导入模板.xlsx',
  });
}

// 导入弹框
const importPop = (type) => {
  openImprotPageFz(true, {
    databaseTrue: databaseTrue.value,
    database: database.value,
    iyear: iyearselected.value,
    isLiji: pageParameter.queryMark == '2' ? true : false,
  });
};

async function reloadProjects() {
  findAllInitialBalance();
}

// 清空处理
const emptyAll2 = () => {
  const rowinfo = getSelectRows();
  if (rowinfo.length === 0) {
    message.error('至少选择一条数据!');
    return false;
  }
  createConfirm({
    iconType: 'warning',
    title: '操作警告',
    content: `确定清空吗？清除后不可恢复！`,
    onOk: async () => {
      const delArr = []; // 期初ID_科目编码_借方_贷方_年度
      for (let i = 0; i < rowinfo.length; i++) {
        var ccode = '';
        var accvouid = '';
        for (let j = 0; j < tableData.value.length; j++) {
          if (
            tableData.value[j].ccode.indexOf(rowinfo[i].ccode) > -1 &&
            tableData.value[j].bend === '1'
          ) {
            ccode = tableData.value[j].ccode;
            accvouid = tableData.value[j].accvouid;
          }
        }
        delArr.push(accvouid + '_' + ccode + '_' + rowinfo[i].md + '_' + rowinfo[i].mc);
      }
      await del(uniq(delArr), iyearselected.value);
    },
    onCancel: () => {
    }
  });
};

/**
 * 简单数组去重
 * @param array
 */
function uniq(array) {
  var temp = []; //一个新的临时数组
  for (var i = 0; i < array.length; i++) {
    if (temp.indexOf(array[i]) == -1) {
      temp.push(array[i]);
    }
  }
  return temp;
}

const del = async (delArr: any, iyear: any) => {
  await useRouteApi(delInitalBalance, {schemaName: databaseTrue.value})({str: delArr, iyear: iyear})
  findAllInitialBalance();
};

// 提交弹框
const submitPop = async () => {
  var aa: any = true;
  // 期初是否记账
  lock0K.value = true
  if (numflg.value || nfratflg.value) { // 核算值大于0 借贷输入值必须大于 0
    let cmd = hasBlank(md.value) ? 0 : parseFloat(md.value.replace(',', ''))
    let cmc = hasBlank(mc.value) ? 0 : parseFloat(mc.value.replace(',', ''))
    let clmd = hasBlank(mdLj.value) ? 0 : parseFloat(mdLj.value.replace(',', ''))
    let clmc = hasBlank(mcLj.value) ? 0 : parseFloat(mcLj.value.replace(',', ''))
    let f = false;
    if ((!hasBlank(ncnum.value) && parseInt(ncnum.value) > 0) && (cmd < 0 || cmc < 0)) f = true
    if ((!hasBlank(ncnumLj.value) && parseInt(ncnumLj.value) > 0) && (clmd < 0 || clmc < 0)) f = true
    if (!f && (!hasBlank(nfrat.value) && parseFloat(nfrat.value) > 0) && (cmd < 0 || cmc < 0)) f = true
    if (!f && (!hasBlank(nfratLj.value) && parseFloat(nfratLj.value) > 0) && (clmd < 0 || clmc < 0)) f = true
    if (f) {
      message.error('核算项值为正数时，本金或累计借贷金额不得为小于0！');
      lock0K.value = false;
      return false;
    }
    if (numflg.value &&
      (('' == ncnum.value || parseInt(ncnum.value) == 0)
        || ((clmd > 0 && ('' == ncnumLj.value || parseInt(ncnumLj.value) == 0))
          || (clmc > 0 && ('' == ncnumLj2.value || parseInt(ncnumLj2.value) == 0))))) {
      message.error('当前科目为数量核算科目,数量输入框不能为零或空！');
      lock0K.value = false
      return false;
    }
    if (nfratflg.value &&
      (('' == nfrat.value || parseInt(nfrat.value) == 0)
        || ((clmd > 0 && ('' == nfratLj.value || parseInt(nfratLj.value) == 0))
          || (clmc > 0 && ('' == nfratLj2.value || parseInt(nfratLj2.value) == 0))))) {
      message.error('当前科目为外币核算科目,外币金额输入框不能为零或空！');
      lock0K.value = false
      return false;
    }

    if (numflg.value && nfratflg.value && ((parseInt(ncnum.value) > 0 && parseFloat(nfrat.value) < 0) || (parseInt(ncnum.value) < 0 && parseFloat(nfrat.value) > 0))) {
      message.error("数量核算与外币核算参数值正负方向必须相同！");
      lock0K.value = false
      return false;
    }
    if (nfratLj.value && nfratflg.value && (
      ((parseInt(ncnumLj.value) > 0 && parseFloat(nfratLj.value) < 0) || (parseInt(ncnumLj.value) < 0 && parseFloat(nfratLj.value) > 0)) ||
      ((parseInt(ncnumLj2.value) > 0 && parseFloat(nfratLj2.value) < 0) || (parseInt(ncnumLj2.value) < 0 && parseFloat(nfratLj2.value) > 0))
    )) {
      message.error("累计数量核算与累计外币核算参数值正负方向必须相同！");
      lock0K.value = false
      return false;
    }
  }
  const qcjz = await useRouteApi(findBySubjectInitalBalabceIbook, {schemaName: databaseTrue.value})({
    iyear: iyearselected.value,
    databasenum: databaseTrue.value
  })
  if (qcjz !== 0) {
    aa = false;
    message.error('提示：期初已记账不能进行编辑');
  }
  // 1、判断当年会计期间是否结过账
  const iperio = await useRouteApi(findByIperiodFlag, {schemaName: databaseTrue.value})({
    iyear: iyearselected.value,
    databasenum: databaseTrue.value
  })
  if (iperio === 0) {
    // 下一步判断-2、判断当年凭证是否已记账
    const accvoucheribook = await useRouteApi(findByAccvoucherIbook, {schemaName: databaseTrue.value})({
      iyear: iyearselected.value,
      databasenum: databaseTrue.value
    })
    if (accvoucheribook === 0) {
      // 下一步判断-3、判断任务管理中是否存在记账任务
      const task1 = await useRouteApi(findByFunctionModule, {schemaName: databaseTrue.value})(iyearselected.value)
      // 没有任务；增加一条本人期初余额任务
      if (task1.length === 0) {
        aa = true;
      } else {
        for (let i = 0; i < task1.length; i++) {
          if (task1[i].functionModule === '凭证记账' && task1[i].caozuoUnique != null) {
            if (task1[i].state === '0') {
              aa = false;
              message.error(
                '发现操作员【' +
                task1[i].caozuoUnique +
                '】独占任务异常，请联系财务主管进行独占任务清理!'
              );
              break;
            } else {
              // 判断是不是自己；动态获取
              if (task1[i].caozuoUnique !== useUserStoreWidthOut().getUserInfo.username) {
                aa = false;
                message.error(
                  '提示：任务冲突！操作员【' +
                  task1[i].caozuoUnique +
                  '】正在进行凭证记账操作，不能进行期初余额编辑，请销后再试，或联系财务主管清理该记账任务'
                );
                break;
              }
            }
          }
          if (task1[i].functionModule === '月末结账' && task1[i].caozuoUnique != null) {
            if (task1[i].state === '0') {
              aa = false;
              message.error(
                '发现操作员【' +
                task1[i].caozuoUnique +
                '】独占任务异常，请联系财务主管进行独占任务清理!'
              );
              break;
            } else {
              // 判断是不是自己；动态获取
              if (task1[i].caozuoUnique !== useUserStoreWidthOut().getUserInfo.username) {
                aa = false;
                message.error(
                  '提示：任务冲突！操作员【' +
                  task1[i].caozuoUnique +
                  '】正在进行月末结账处理，不能进行期初余额编辑，请销后再试，或联系财务主管清理该记账任务'
                );
                break;
              }
            }
          }
          if (task1[i].functionModule === '科目编辑' && task1[i].caozuoUnique != null) {
            if (task1[i].state === '0') {
              aa = false;
              message.error(
                '发现操作员【' +
                task1[i].caozuoUnique +
                '】独占任务异常，请联系财务主管进行独占任务清理!'
              );
              break;
            } else {
              // 判断是不是自己；动态获取
              if (task1[i].caozuoUnique !== useUserStoreWidthOut().getUserInfo.username) {
                aa = false;
                message.error(
                  '提示：任务冲突！操作员【' +
                  task1[i].caozuoUnique +
                  '】正在进行会计科目编辑操作，不能进行期初余额编辑，请销后再试，或联系财务主管清理该记账任务'
                );
                break;
              }
            }
          }
          if (task1[i].functionModule === '转账生成' && task1[i].caozuoUnique != null) {
            if (task1[i].state === '0') {
              aa = false;
              message.error(
                '发现操作员【' +
                task1[i].caozuoUnique +
                '】独占任务异常，请联系财务主管进行独占任务清理!'
              );
              break;
            } else {
              // 判断是不是自己；动态获取
              if (task1[i].caozuoUnique !== useUserStoreWidthOut().getUserInfo.username) {
                aa = false;
                message.error(
                  '提示：任务冲突！操作员【' +
                  task1[i].caozuoUnique +
                  '】正在进行转账生成编辑操作，不能进行期初余额编辑，请销后再试，或联系财务主管清理该记账任务'
                );
                break;
              }
            }
          }
          if (task1[i].functionModule === '期初余额' && task1[i].caozuoUnique != null) {
            if (task1[i].state === '0') {
              aa = false;
              message.error(
                '发现操作员【' +
                task1[i].caozuoUnique +
                '】独占任务异常，请联系财务主管进行独占任务清理!'
              );
              break;
            } else {
              // 判断是不是自己；动态获取
              if (task1[i].caozuoUnique !== useUserStoreWidthOut().getUserInfo.username) {
                aa = false;
                message.error(
                  '提示：任务冲突！操作员【' +
                  task1[i].caozuoUnique +
                  '】正在进行期初余额编辑操作，不能进行期初余额编辑，请销后再试，或联系财务主管清理该记账任务'
                );
                break;
              }
            }
          }
        }
      }
    } else {
      aa = false;
      message.error('本年度凭证已记账，不能进行期初余额编辑，请取消凭证记账后再运行本功能');
    }
  } else {
    aa = false;
    message.error('本年已发生结账月份，不能进行期初余额编辑，请取消结账和记账后再运行本功能');
  }
  if (aa) {
    datainfo.value.md = md.value == '' ? '0.00' : String(md.value).replace(/,/g, '');
    datainfo.value.mc = mc.value == '' ? '0.00' : String(mc.value).replace(/,/g, '');
    datainfo.value.ndS = md.value == '' ? '0' : ncnum.value;
    datainfo.value.ncS = mc.value == '' ? '0' : ncnum.value;
    datainfo.value.nfrat = nfrat.value == '' ? '0.00' : String(nfrat.value).replace(/,/g, '');
    datainfo.value.nfratMd = md.value == '' ? '0.00' : datainfo.value.nfrat;
    datainfo.value.nfratMc = mc.value == '' ? '0.00' : datainfo.value.nfrat;
    // 中途启用
    datainfo.value.ljMc = mcLj.value == '' ? '0.00' : String(mcLj.value).replace(/,/g, '');
    datainfo.value.ljMd = mdLj.value == '' ? '0.00' : String(mdLj.value).replace(/,/g, '');
    datainfo.value.ljSlMd = ncnumLj.value == '' ? '0' : String(ncnumLj.value).replace(/,/g, '');
    datainfo.value.ljSlMc = ncnumLj2.value == '' ? '0' : String(ncnumLj2.value).replace(/,/g, '');
    datainfo.value.ljWbMd = nfratLj.value == '' ? '0.00' : String(nfratLj.value).replace(/,/g, '');
    datainfo.value.ljWbMc = nfratLj2.value == '' ? '0.00' : String(nfratLj2.value).replace(/,/g, '');
    if ((numflg.value || nfratflg.value) && ((datainfo.value.md == '0.00' || datainfo.value.md == '') && (datainfo.value.mc == '0.00' || datainfo.value.mc == ''))) {
      createConfirm({
        iconType: 'warning',
        title: '操作警告',
        content: `本币期初借贷余额为空白或0，当前数据为${numflg.value ? '数量' : '外币'}核算时，若保存系统将清空该科目期初余额记录!`,
        onOk: async () => {
          await useRouteApi(saveData, {schemaName: databaseTrue.value})(datainfo.value)
          modal2Visible.value = false;
        },
        onCancel: () => {
          modal2Visible.value = false;
        }
      });
    } else {
      await useRouteApi(saveData, {schemaName: databaseTrue.value})(datainfo.value)
      modal2Visible.value = false;
    }
  }
  lock0K.value = false
};

const setModal1Visible = async (data: any) => {
  let ffuuzz = data.fuzhu;
  isLj.value = pageParameter.queryMark == '2' ? true : false
  if (ffuuzz != '') {
    datainfo.value = data;
    var fuzhus = xiushiFuZhu(data);
    ffuuzz = data.fuzhu;
    const map = await useRouteApi(findAllSubjectInitialBalanceFuZhuList, {schemaName: databaseTrue.value})({
      iyear: iyearselected.value,
      ccode: data.ccode,
      databasenum: databaseTrue.value
    })
    let minDate = await findYearMinDate(database.value, iyearselected.value);
    minDate = findLastDate(minDate);
    openEditPage(true, {
      ffuuzz: ffuuzz, map: map, fuzhus: fuzhus, maxDate: minDate, isLj: isLj.value
    })
  } else {
    message.warning('当前科目不存在辅助核算！')
  }/*else {
    datainfo.value = data;
    modal2Visible.value = true;
    lock0K.value = false
    // 数量核算
    numflg.value = null != data.menterage && data.menterage !== '';
    // 外币金额
    nfratflg.value = null != data.currencyType && data.currencyType !== '';

    md.value = data.md != 0 ? money(data.md) : '';
    mc.value = data.mc != 0 ? money(data.mc) : '';
    ncnum.value = data.ndS != 0 ? data.ndS : data.ncS != 0 ? data.ncS : '';
    nfrat.value = data.md != 0 && data.nfratMd != 0 ? money(data.nfratMd) : data.mc != 0 && data.nfratMc != 0 ? money(data.nfratMc) : '';
    mcLj.value = data.ljMc != 0 ? money(data.ljMc) : ''
    mdLj.value = data.ljMd != 0 ? money(data.ljMd) : ''
    ncnumLj.value = data.ljSlMd != 0 ? data.ljSlMd : ''
    ncnumLj2.value = data.ljSlMc != 0 ? data.ljSlMc : ''
    nfratLj.value = data.ljWbMd != 0 ? money(data.ljWbMd) : ''
    nfratLj2.value = data.ljWbMc != 0 ? money(data.ljWbMc) : ''
    openInfo.value.ccode = data.ccode
    openInfo.value.ccodeName = data.ccodeName
    openInfo.value.fx = data.bprogerty == '1' ? '借' : '贷'
    openInfo.value.currency = data.currencyType
  }*/
};

function findLastDate(date) {
  date = new Date(date);
  date = 0 + (date).getTime() - 1000 * 60 * 60 * 24;
  date = new Date(date);
  date = date.getFullYear() + "-" + (date.getMonth() > 9 ? (date.getMonth() + 1) : "0" + (date.getMonth() + 1)) + "-" + (date.getDate() > 9 ? (date.getDate()) : "0" + (date.getDate()));
  return date;
}

function xiushiFuZhu(data) {
  let fuzhus = [];
  if (data.bperson == '1') {
    fuzhus.push('fzEmp');
  }
  if (data.bdept == '1') {
    fuzhus.push('fzDept');
  }
  if (data.bcus == '1') {
    fuzhus.push('fzCustom');
  }
  if (data.bsup == '1') {
    fuzhus.push('fzGys');
  }
  if (data.bitem == '1') {
    fuzhus.push('fzItemClass');
  }
  if (data.bitem == '1') {
    fuzhus.push('fzItem');
  }
  if (data.cdfine1 == '1') {
    fuzhus.push('1');
  }
  if (data.cdfine2 == '1') {
    fuzhus.push('2');
  }
  if (data.cdfine3 == '1') {
    fuzhus.push('3');
  }
  if (data.cdfine4 == '1') {
    fuzhus.push('4');
  }
  if (data.cdfine5 == '1') {
    fuzhus.push('5');
  }
  if (data.cdfine6 == '1') {
    fuzhus.push('6');
  }
  if (data.cdfine7 == '1') {
    fuzhus.push('7');
  }
  if (data.cdfine8 == '1') {
    fuzhus.push('8');
  }
  if (data.cdfine9 == '1') {
    fuzhus.push('9');
  }
  if (data.cdfine10 == '1') {
    fuzhus.push('10');
  }
  if (data.cdfine11 == '1') {
    fuzhus.push('11');
  }
  if (data.cdfine12 == '1') {
    fuzhus.push('12');
  }
  if (data.cdfine13 == '1') {
    fuzhus.push('13');
  }
  if (data.cdfine14 == '1') {
    fuzhus.push('14');
  }
  if (data.cdfine15 == '1') {
    fuzhus.push('15');
  }
  if (data.cdfine16 == '1') {
    fuzhus.push('16');
  }
  if (data.cdfine17 == '1') {
    fuzhus.push('17');
  }
  if (data.cdfine18 == '1') {
    fuzhus.push('18');
  }
  if (data.cdfine19 == '1') {
    fuzhus.push('19');
  }
  if (data.cdfine20 == '1') {
    fuzhus.push('20');
  }
  if (data.cdfine21 == '1') {
    fuzhus.push('21');
  }
  if (data.cdfine22 == '1') {
    fuzhus.push('22');
  }
  if (data.cdfine23 == '1') {
    fuzhus.push('23');
  }
  if (data.cdfine24 == '1') {
    fuzhus.push('24');
  }
  if (data.cdfine25 == '1') {
    fuzhus.push('25');
  }
  if (data.cdfine26 == '1') {
    fuzhus.push('26');
  }
  if (data.cdfine27 == '1') {
    fuzhus.push('27');
  }
  if (data.cdfine28 == '1') {
    fuzhus.push('28');
  }
  if (data.cdfine29 == '1') {
    fuzhus.push('29');
  }
  if (data.cdfine30 == '1') {
    fuzhus.push('30');
  }
  var ss = '';
  for (let i = 0; i < fuzhus.length; i++) {
    if (i == 0) {
      ss = ss + fuzhus[i];
    } else {
      ss = ss + "," + fuzhus[i];
    }
  }
  return ss;
}

// 编辑期初前查询科目表是否锁定
const editflg = async (data: any) => {
  if (data === '开始编辑') {
    var aa: any = true;
    // 期初是否记账
    const qcjz = await useRouteApi(findBySubjectInitalBalabceIbook, {schemaName: databaseTrue.value})({
      iyear: iyearselected.value,
      databasenum: databaseTrue.value
    })
    if (qcjz !== 0) {
      aa = false;
      message.error('提示：期初已记账不能进行编辑');
    }
    // 1、判断当年会计期间是否结过账
    const iperio = await useRouteApi(findByIperiodFlag, {schemaName: databaseTrue.value})({
      iyear: iyearselected.value,
      databasenum: databaseTrue.value
    })
    if (iperio === 0) {
      // 下一步判断-2、判断当年凭证是否已记账
      const accvoucheribook = await useRouteApi(findByAccvoucherIbook, {schemaName: databaseTrue.value})({
        iyear: iyearselected.value,
        databasenum: databaseTrue.value
      })
      if (accvoucheribook === 0) {
        // 下一步判断-3、判断任务管理中是否存在记账任务
        const task1 = await useRouteApi(findByFunctionModule, {schemaName: databaseTrue.value})({
          iyear: iyearselected.value,
          databasenum: databaseTrue.value
        })

        // 没有任务；增加一条本人期初余额任务
        if (task1.length === 0) {
          aa = true;
        } else {
          for (let i = 0; i < task1.length; i++) {
            if (task1[i].functionModule === '凭证记账' && task1[i].caozuoUnique != null) {
              if (task1[i].state === '0') {
                aa = false;
                message.error(
                  '发现操作员【' +
                  task1[i].caozuoUnique +
                  '】独占任务异常，请联系财务主管进行独占任务清理!'
                );
                break;
              } else {
                // 判断是不是自己；动态获取
                if (task1[i].caozuoUnique !== null && task1[i].caozuoUnique !== useUserStoreWidthOut().getUserInfo.username) {
                  aa = false;
                  message.error(
                    '提示：任务冲突！操作员【' +
                    task1[i].caozuoUnique +
                    '】正在进行凭证记账操作，不能进行期初余额编辑，请销后再试，或联系财务主管清理该记账任务'
                  );
                  break;
                }
              }
            }
            if (task1[i].functionModule === '月末结账' && task1[i].caozuoUnique != null) {
              if (task1[i].state === '0') {
                aa = false;
                message.error(
                  '发现操作员【' +
                  task1[i].caozuoUnique +
                  '】独占任务异常，请联系财务主管进行独占任务清理!'
                );
                break;
              } else {
                // 判断是不是自己；动态获取
                if (task1[i].caozuoUnique !== null && task1[i].caozuoUnique !== useUserStoreWidthOut().getUserInfo.username) {
                  aa = false;
                  message.error(
                    '提示：任务冲突！操作员【' +
                    task1[i].caozuoUnique +
                    '】正在进行月末结账处理，不能进行期初余额编辑，请销后再试，或联系财务主管清理该记账任务'
                  );
                  break;
                }
              }
            }
            if (task1[i].functionModule === '科目编辑' && task1[i].caozuoUnique != null) {
              if (task1[i].state === '0') {
                aa = false;
                message.error(
                  '发现操作员【' +
                  task1[i].caozuoUnique +
                  '】独占任务异常，请联系财务主管进行独占任务清理!'
                );
                break;
              } else {
                // 判断是不是自己；动态获取
                if (task1[i].caozuoUnique !== null && task1[i].caozuoUnique !== useUserStoreWidthOut().getUserInfo.username) {
                  aa = false;
                  message.error(
                    '提示：任务冲突！操作员【' +
                    task1[i].caozuoUnique +
                    '】正在进行会计科目编辑操作，不能进行期初余额编辑，请销后再试，或联系财务主管清理该记账任务'
                  );
                  break;
                }
              }
            }
            if (task1[i].functionModule === '转账生成' && task1[i].caozuoUnique != null) {
              if (task1[i].state === '0') {
                aa = false;
                message.error(
                  '发现操作员【' +
                  task1[i].caozuoUnique +
                  '】独占任务异常，请联系财务主管进行独占任务清理!'
                );
                break;
              } else {
                // 判断是不是自己；动态获取
                if (task1[i].caozuoUnique !== null && task1[i].caozuoUnique !== useUserStoreWidthOut().getUserInfo.username) {
                  aa = false;
                  message.error(
                    '提示：任务冲突！操作员【' +
                    task1[i].caozuoUnique +
                    '】正在进行转账生成编辑操作，不能进行期初余额编辑，请销后再试，或联系财务主管清理该记账任务'
                  );
                  break;
                }
              }
            }
            if (task1[i].functionModule === '期初余额' && task1[i].caozuoUnique != null) {
              taskinfo.value = task1[i];
              if (task1[i].state === '0') {
                aa = false;
                message.error(
                  '发现操作员【' +
                  task1[i].caozuoUnique +
                  '】独占任务异常，请联系财务主管进行独占任务清理!'
                );
                break;
              } else {
                // 判断是不是自己；动态获取
                if (task1[i].caozuoUnique !== null && task1[i].caozuoUnique !== lanMuData.username) {
                  aa = false;
                  message.error(
                    '提示：任务冲突！操作员【' +
                    task1[i].caozuoUnique +
                    '】正在进行期初余额编辑操作，不能进行期初余额编辑，请销后再试，或联系财务主管清理该记账任务'
                  );
                  break;
                }
              }
            }
          }
        }
      } else {
        aa = false;
        message.error('本年度凭证已记账，不能进行期初余额编辑，请取消凭证记账后再运行本功能');
      }
    } else {
      aa = false;
      message.error('本年已发生结账月份，不能进行期初余额编辑，请取消结账和记账后再运行本功能');
    }

    if (aa) {
      // deadline.value = Date.now() + 1000 * 60 * 30; // 30分钟
      edittask()
      edittext.value = data === '开始编辑' ? '退出编辑' : '开始编辑';
      roweditflg.value = true;
    }
  } else {
    deadline.value = '';
    edittext.value = data === '退出编辑' ? '开始编辑' : '退出编辑';
    deltask()
    roweditflg.value = false;
  }
};

/****************************** 任务三剑客 ************************************/
// 离开当前。删除任务
onBeforeRouteLeave(deltask)

// 任务表
async function edittask() {
  // 首次
  if (taskinfo.value === '') {
    let task = await useRouteApi(saveTaskInfo, {schemaName: databaseTrue.value})({
      iyear: iyearselected.value,
      functionModule: '期初余额',
      username: lanMuData.username,
      method: "编辑"
    });
    taskinfo.value = task
    return true;
  } else {
    // 判断任务是否被清除
    let test = await useRouteApi(countByTaskId, {schemaName: databaseTrue.value})({id: taskinfo.value.id});
    if (test === 0) {
      return false;
    }
  }
  return true;
}

// 清除任务
async function deltask() {
  // 屏蔽
  if (taskinfo.value !== '') {
    await useRouteApi(delFunctionModule, {schemaName: databaseTrue.value})({
      id: taskinfo.value.id,
      databasenum: databaseTrue.value
    });
    taskinfo.value = '';
  }
}

/****************************END**************************************/

// 是否末级科目查询
const lastCodechecked = async (val) => {
  lastCode.value = val.target.checked;
  findAllInitialBalance();
};

const yearChange = async (val) => {
};

const saveData = async (data: any) => {
  await useRouteApi(saveSubjectInitialBalance, {schemaName: databaseTrue.value})({
    params: data,
    accId: database.value,
    userName: userinfo.value.username,
    databasenum: databaseTrue.value,
    saveType: pageParameter.queryMark
  })
  findAllInitialBalance();
};

// 获取期初余额
const findAllInitialBalance = async () => {
  // 查询本年是否期初记账
  const qcjzsflg = await useRouteApi(qcjzsum, {schemaName: databaseTrue.value})({
    iyear: iyearselected.value,
    databasenum: databaseTrue.value
  })
  if (qcjzsflg > 0) {
    ibookflg.value = true;
  } else {
    ibookflg.value = false;
  }
  tableData.value = []
  tableDataAll.value = []
  loading.value = true;
  const a = await useRouteApi(findAllSubjectInitialBalanceNewFuZhu, {schemaName: databaseTrue.value})({
    iyear: iyearselected.value,
    lastCode: lastCode.value,
    databasenum: databaseTrue.value,
    ccode: pageParameter.ccode,
    cclass: pageParameter.cclass,
    bend: pageParameter.bend,
  })
  let list = a.tablesData.filter(it => it.fuzhu != '').map(it => assemblyChildren(it, a.accList))
  calculateTotal(list)
  if(list.length<50){
    for (let i =  list.length; i < 50; i++) {
      let temp={
        accvouid: "",
        bcus: "0",
        bdept: "0",
        bend: "0",
        bitem: "0",
        bnum: "0",
        bperson: "0",
        bprogerty: "0",
        bsup: "0",
        cclass: "",
        ccode: "",
        ccodeName: "",
        ccusId: null,
        ccusName: null,
        cdeptId: null,
        cdeptName: null,
        cdfine1: "",
        cdfine1Id: null,
        cdfine1Name: null,
        cdfine2: "",
        cdfine2Id: null,
        cdfine2Name: null,
        cdfine3: "0",
        cdfine3Id: null,
        cdfine3Name: null,
        cdfine4: "0",
        cdfine4Id: null,
        cdfine4Name: null,
        cdfine5: "0",
        cdfine5Id: null,
        cdfine5Name: null,
        cdfine6: "0",
        cdfine6Id: null,
        cdfine6Name: null,
        cdfine7: "0",
        cdfine7Id: null,
        cdfine7Name: null,
        cdfine8: "0",
        cdfine8Id: null,
        cdfine8Name: null,
        cdfine9: "0",
        cdfine9Id: null,
        cdfine9Name: null,
        cdfine10: "0",
        cdfine10Id: null,
        cdfine10Name: null,
        cdfine11: "0",
        cdfine11Id: null,
        cdfine11Name: null,
        cdfine12: "0",
        cdfine12Id: null,
        cdfine12Name: null,
        cdfine13: "0",
        cdfine13Id: null,
        cdfine13Name: null,
        cdfine14: "0",
        cdfine14Id: null,
        cdfine14Name: null,
        cdfine15: "0",
        cdfine15Id: null,
        cdfine15Name: null,
        cdfine16: "0",
        cdfine16Id: null,
        cdfine16Name: null,
        cdfine17: "0",
        cdfine17Id: null,
        cdfine17Name: null,
        cdfine18: "0",
        cdfine18Id: null,
        cdfine18Name: null,
        cdfine19: "0",
        cdfine19Id: null,
        cdfine19Name: null,
        cdfine20: "0",
        cdfine20Id: null,
        cdfine20Name: null,
        cdfine21: "0",
        cdfine21Id: null,
        cdfine21Name: null,
        cdfine22: "0",
        cdfine22Id: null,
        cdfine22Name: null,
        cdfine23: "0",
        cdfine23Id: null,
        cdfine23Name: null,
        cdfine24: "0",
        cdfine24Id: null,
        cdfine24Name: null,
        cdfine25: "0",
        cdfine25Id: null,
        cdfine25Name: null,
        cdfine26: "0",
        cdfine26Id: null,
        cdfine26Name: null,
        cdfine27: "0",
        cdfine27Id: null,
        cdfine27Name: null,
        cdfine28: "0",
        cdfine28Id: null,
        cdfine28Name: null,
        cdfine29: "0",
        cdfine29Id: null,
        cdfine29Name: null,
        cdfine30: "0",
        cdfine30Id: null,
        cdfine30Name: null,
        cdigest: null,
        cpersonId: null,
        cpersonName: null,
        csupId: null,
        csupName: null,
        currency: null,
        currencyType: "",
        dbillDate: null,
        flag: "",
        fuzhu: "",
        igrade: "",
        inoId: null,
        iperiod: "",
        iyear: "",
        iyperiod: "",
        ljMc: 0,
        ljMd: 0,
        ljSlMc: 0,
        ljSlMd: 0,
        ljWbMc: 0,
        ljWbMd: 0,
        mc: 0,
        md: 0,
        menterage: "",
        menterageORcurrencyType: null,
        ncS: 0,
        ncnum: 0,
        ndS: 0,
        nfrat: 0,
        nfratMc: 0,
        nfratMd: 0,
        projectClassId: null,
        projectId: null,
        projectName: null,
        superiorCcode: "1133",
        uniqueCode: null,
        vouchUnCode: null,
        yearMc: 0,
        yearMd: 0,
        yearNfrat: 0,
        yearNum: 0,
        yuemc: 0,
        yuemd: 0,
        children:null,
        rowType:'666',
      }
      list.push(temp)
    }
  }
  tableDataAll.value = list;
  tableData.value = list;
  let tableColumns = a.tableColumns;
  if (tableColumns === 'all') {
    menterage.value = false
    currencyType.value = false
  }
  if (tableColumns === 'menterage') {
    menterage.value = false
  }
  if (tableColumns === 'currencyType') {
    currencyType.value = false
  }
  loading.value = false;
  showPageNumber.value=true
};
const summaryTotals = ref({})
const calculateTotal = (datalist) => {
  let list = JsonTool.parseProxy(datalist)
  if (list.length == 0){
    summaryTotals.value = {}
    return false;
  }
  let md = 0
  let mc = 0
  for (let i = 0; i < list.length; i++) {
    const e = list[i];
    md += parseFloat(e.md || '0')
    mc += parseFloat(e.mc || '0')
  }
  summaryTotals.value={
    md: toThousandFilter(md),
    mc: toThousandFilter(mc),
  }
}
const assemblyChildren = (it, aList) => {
  let tList = aList.filter(it2 => it2.ccode == it.ccode)
  if (tList.length > 0) {
    // 得到辅助核算类型
    let arr = it.fuzhu.split(',')
    let keys = generateKeys(arr)
    let rTwoList = [...new Set(tList.map(it1 => {
      let s = '';
      keys.forEach(k => {
        s += it1[k] + ','
      });
      return s.substring(0, s.length - 1);
    }))]
    it.children = rTwoList.map(s => {
      let o = {}
      o['rowType'] = '1'
      o['text'] = generateText(s, keys)
      let obj = generateChildren(s, keys, tList)
      if (keys.indexOf('cpersonId') != -1 || keys.indexOf('ccusId') != -1 || keys.indexOf('csupId') != -1){
        o['children'] = obj.list
      }else {
        o['the'] = obj.list[0].the
      }
      o['md'] = obj.md
      o['mc'] = obj.mc
      return o;
    })
  }
  return it;
}

const generateChildren = (s, keys, list) => {
  let arr = s.split(',')
  let rlist = list.filter(it => {
    let b = true;
    for (let i = 0; i < arr.length; i++) {
      if (it[keys[i]] != arr[i]) b = false;
    }
    return b;
  })
  let md = 0;
  let mc = 0;
  rlist.forEach(it=>{
    md += parseFloat((it.md || 0))
    mc += parseFloat((it.mc || 0))
  })
  return {
    list: rlist.map(it => ({the: it,'rowType': '2', text: `${it.dbillDate},凭证号${it.inoId}号,${it.cdigest}`,md: it.md,mc: it.mc})),
    md:md,mc:mc
  }
}

const generateText = (s, keys) => {
  let arr = s.split(',')
  let text = ''
  for (let i = 0; i < arr.length; i++) {
    let key = ''
    if (keys[i].startsWith('cdfine')) {
      key = keys[i].replace('cdfine', '')
    } else {
      switch (keys[i]) {
        case 'cdeptId':
          key = 'fzDept'
          break;
        case 'cpersonId':
          key = 'fzEmp'
          break;
        case 'ccusId':
          key = 'fzCustom'
          break;
        case 'csupId':
          key = 'fzGys'
          break;
        case 'projectId':
          key = 'fzItem'
          break;
      }
    }
    console.log(accountingInfoModel)
    console.log(key)
    let list = accountingInfoModel.details?.filter(it=>it.key == key)[0]?.list
    if (null != list && list.length > 0) text += ((list.filter(it => it.key == arr[i])[0]?.label) + ',')
  }
  return text != ''?text.substring(0,text.length-1):''
}

const generateKeys = (arr) => {
  let keys = []
  for (let i = 0; i < arr.length; i++) {
    let n = arr[i]
    if (n.startsWith('自定义')) {
      let mark = n.replace('自定义', '')
      let key = 'cdfine' + mark
      keys.push(key)
    } else {
      let key = ''
      switch (n) {
        case '部门':
          key = 'cdeptId'
          break;
        case '个人':
          key = 'cpersonId'
          break;
        case '客户':
          key = 'ccusId'
          break;
        case '供应商':
          key = 'csupId'
          break;
        case '项目':
          key = 'projectId'
          break;
      }
      keys.push(key)
    }
  }
  return keys
}
const transformName = (fuzhu) => {
  let arr = fuzhu.split(',')
  arr = arr.map(lt=>{
    if (lt.startsWith('自定义')){
      let a =  lt.replace('自定义','')
      lt = accountingInfoModel.definitions.filter(it=>it.cdfine == a)[0].cname
    }
    return lt;
  })
  return arr.join(',')
}
// 金额格式化
function money(val: any) {
  if (val == null) val = '';
  val = val.toString().replace(/\$|\,\-/g, '');
  if (isNaN(val)) {
    val = '0';
  }
  let fs = val.indexOf('-') != -1
  const sign = val === (val = Math.abs(val));
  val = Math.floor(val * 100 + 0.50000000001);
  let cents = val % 100;
  val = Math.floor(val / 100).toString();
  if (cents < 10) {
    cents = '0' + cents;
  }
  for (let i = 0; i < Math.floor((val.length - (1 + i)) / 3); i++) {
    val =
      val.substring(0, val.length - (4 * i + 3)) + ',' + val.substring(val.length - (4 * i + 3));
  }
  return (sign ? '' : '') + (fs ? '-' : '') + val + '.' + cents;
}

const getAdObjInfoByCoCode = (value, type, accList) => {
  let list = accList.filter(item => item[type] == value)
  return list.length > 0 ? list[0] : null
}


const dynamicAdReload = async (obj) => {
  initTable()
  loading.value = true;
  // 获取期间年度
  iyearlist.value=await findPeriod(obj.accId)
  if (taskinfo.value !== '') {
    await deltask() //删除旧任务
    database.value = obj.accId;
    databaseTrue.value = obj.accountMode;
    iyearselected.value = obj.year;
    await edittask() // 添加新的任务
  } else {
    database.value = obj.accId;
    databaseTrue.value = obj.accountMode;
    iyearselected.value = obj.year;
  }
  findByAccStyleAll()
  // 获取对应的账套科目 所属的 会计准则、科目模板
  let codelistall = await useRouteApi(groupStandardAndTemplate, {schemaName: databaseTrue.value})({
    databaseNum: databaseTrue.value,
    iyear: iyearselected.value
  });

  if (codelistall.length > 0) {
    templateSelected.value = codelistall[0].templateId
    standardSelected.value = codelistall[0].uniqueAccStandard
  } else {
    console.log("账套下没有科目")
    // 根据账套信息表重新获取 会计准则、科目模板
    let tplateInfo = await findTemplateByAccId(database.value)
    templateSelected.value = tplateInfo.id
    standardSelected.value = tplateInfo.uniqueAccStandard
  }

  // 从集团获取账套基本信息
  const datainfo = await findByAccId(database.value);
  let period = await findPeriodByYaer(database.value, iyearselected.value)
  if (period == '') {
    loading.value = false
    return message.error('没有本年期间!');
  }
  qujian.value = period // 本位币
  if (null == period || period.substring(4, 6) == '01') {
    pageParameter.queryMark = '1'
  } else {
    pageParameter.queryMark = '2'
  }
  bwb.value = obj.target.currencyName    // 本位币
  independent.value = datainfo.independent > 0 ? true : false;  // 1是独立账套 0是集团账套
  //
  accountingInfoModel.definitions = await useRouteApi(findKeyLabelOtherAll, {schemaName: databaseTrue.value})({
    require: 'all',
    toTarget: "false"
  });
  if (null != accountingInfoModel.definitions)
    accountingInfoModel.details = await useRouteApi(findKeyLabelAll, {schemaName: databaseTrue.value})({
      require: 'fzEmp,fzDept,fzCustom,fzGys,fzItem' + accountingInfoModel.definitions.filter(it => it.cdfine != null).map(it => it.cdfine).join(','),
      toTarget: "false"
    });
  await findAllInitialBalance();
  loading.value = true
  loading.value = false
}
const styleNamelist = ref([])
// 获取科目类型
async function findByAccStyleAll() {
  loading2.value = true
  const codelistall = await acctemplateFindByAccId(database.value)
  const accstandard = await findByStandardUnique(codelistall.uniqueAccStandard)
  const accStyleUnique = await findByAccStyle(accstandard.accStyleUnique)
  styleList.value = []
  for (let i = 0; i < accStyleUnique.length; i++) {
    styleList.value.push(accStyleUnique[i].cclass)
  }
/*  treeData.value = []
  treeData.value.push({title: '全部', key: '0', children: styleList.value.map(it=>({key: it,title: it}))})
  selectedKeys2.value = ['0']
  expandedKeys.value = ['0']*/
  if (styleNamelist.value.length === 0) {
    styleNamelist.value.push({
      cclass: '全部',
      flagYusuan: '',
    });
    for (let i = 0; i < accStyleUnique.length; i++) {
      styleNamelist.value.push({
        cclass: accStyleUnique[i].cclass,
        flagYusuan: accStyleUnique[i].flagYusuan,
        order: accStyleUnique[i].order,
      });
    }
  }
 /* let map = {style: styleList.value.join(','), iyear: iyearselected.value}
  fetch(map)*/
}

async function fetch(map) {
  const deptTree = await useRouteApi(company_findByIyearOrderByCcodeTree, {schemaName: databaseTrue})(map)
  if (deptTree != null) {
    function a(customerTree) {
      customerTree.forEach((item) => {
        if (item.children != null) {
          a(item.children);
        }
        item.title = item.title
        item.key = item.key + '>>' + item.bend
      });
    }

    a(deptTree);
    treeData.value = []
    selectedKeys2.value = ['0']
    expandedKeys.value = ['0']
    treeData.value.push({title: '全部', key: '0', children: deptTree})
  }
  loading2.value = false;
}

//js切割字符串
function setString(str, len) {
  var strlen = 0;
  var s = '';
  for (var i = 0; i < str.length; i++) {
    if (str.charCodeAt(i) > 128) {
      strlen += 2;
    } else {
      strlen += 1.2;
    }
    s += str.charAt(i);
    if (strlen >= len) {
      return s + '...';
    }
  }
  return s;
}

function handleSelect(obj) {
  if (obj.toString() !== '') {
    if (!isNaN(obj.toString().split('>>')[0])) {
      pageParameter.ccode = obj.toString().split('>>')[0]
      pageParameter.bend = obj.toString().split('>>')[1]
      pageParameter.cclass = ''
      findAllInitialBalance()
    } else {
      pageParameter.ccode = ''
      pageParameter.cclass = obj.toString().split('>>')[0]
      pageParameter.bend = obj.toString().split('>>')[1]
      findAllInitialBalance()
    }
  } else {
    selectedKeys2.value = ['0']
    pageParameter.cclass = '全部'
    pageParameter.ccode = ''
    findAllInitialBalance()
  }
}
const activeKey = ref('全部')
const tabsCheck = (v) => {
  pageParameter.cclass = v
  findAllInitialBalance()
}
/*start栏目设置*/
import DynamicColumn from "/@/views/boozsoft/stock/stock_sales_add/component/DynamicColumn.vue";
import {assemblyDynamicColumn, initDynamics} from "./data";
const dynamicColumnData:any = ref({value: []})
const dynamicColumns = initDynamics().DEFAULT
const visible3 = ref(false)
const lanMuData = ref({
  accId: databaseTrue.value,
  menuName: '辅助科目期初余额信息',
  type: '列表',
  objects: '',
  username: useUserStoreWidthOut().getUserInfo.username,
  changeNumber: 0
})

const initTable = ()=>{
  visible3.value = true
  setTimeout(()=>{
      lanMuData.value.changeNumber+=1
      visible3.value = false
    }
    ,300)
}
const reloadColumns = () => {
  let newA = JSON.parse(JSON.stringify(tableColumns.value))
  newA = assemblyDynamicColumn(dynamicColumnData.value.value, newA)
  setColumns(newA)
  initTableWidth(newA)
  findAllInitialBalance()
}
function initTableWidth(thisCs) {
  let total = 60 + 60 // 选择列与序号列
  thisCs.forEach(item => {
    if (item.ifShow == null || item.ifShow)
      total += parseInt(item.width)
  })

  if (total > windowWidth) {
    let f = 0
    if (visible3.value) f = 380
    totalColumnWidth.value = Number(windowWidth) - f
    tableRef.value.$el.style.setProperty('width', (windowWidth + 62 - f) + 'px')
  } else {
    if (visible3.value && (windowWidth - 380) < total) total -= (total - (windowWidth - 380))
    totalColumnWidth.value = total
    tableRef.value.$el.style.setProperty('width', (total + 62) + 'px')
  }
}
/*栏目设置end*/

const dbSave = async (o) => {
  // 暂无变化停止修改行为
  if ((o.oldMc != null && o.mc == o.oldMc) || (o.oldMd != null && o.md == o.oldMd) || (o.oldLjMc != null && o.ljMc == o.oldLjMc) || (o.oldLjMd != null && o.ljMd == o.oldLjMd)) return false;
  if (o.oldMc != null) {
    o.md = 0;
  } else if (o.oldMd != null) {
    o.mc = 0;
  }
  const qcjz = await useRouteApi(findBySubjectInitalBalabceIbook, {schemaName: databaseTrue.value})({
    iyear: iyearselected.value,
    databasenum: databaseTrue.value
  })
  let aa = true;
  if (qcjz !== 0) {
    aa = false;
    message.error('提示：期初已记账不能进行编辑');
  }
  // 1、判断当年会计期间是否结过账
  const iperio = await useRouteApi(findByIperiodFlag, {schemaName: databaseTrue.value})({
    iyear: iyearselected.value,
    databasenum: databaseTrue.value
  })
  if (iperio === 0) {
    // 下一步判断-2、判断当年凭证是否已记账
    const accvoucheribook = await useRouteApi(findByAccvoucherIbook, {schemaName: databaseTrue.value})({
      iyear: iyearselected.value,
      databasenum: databaseTrue.value
    })
    if (accvoucheribook === 0) {
      // 下一步判断-3、判断任务管理中是否存在记账任务
      const task1 = await useRouteApi(findByFunctionModule, {schemaName: databaseTrue.value})(iyearselected.value)
      // 没有任务；增加一条本人期初余额任务
      if (task1.length === 0) {
        aa = true;
      } else {
        for (let i = 0; i < task1.length; i++) {
          if (task1[i].functionModule === '凭证记账' && task1[i].caozuoUnique != null) {
            if (task1[i].state === '0') {
              aa = false;
              message.error(
                '发现操作员【' +
                task1[i].caozuoUnique +
                '】独占任务异常，请联系财务主管进行独占任务清理!'
              );
              break;
            } else {
              // 判断是不是自己；动态获取
              if (task1[i].caozuoUnique !== useUserStoreWidthOut().getUserInfo.username) {
                aa = false;
                message.error(
                  '提示：任务冲突！操作员【' +
                  task1[i].caozuoUnique +
                  '】正在进行凭证记账操作，不能进行期初余额编辑，请销后再试，或联系财务主管清理该记账任务'
                );
                break;
              }
            }
          }
          if (task1[i].functionModule === '月末结账' && task1[i].caozuoUnique != null) {
            if (task1[i].state === '0') {
              aa = false;
              message.error(
                '发现操作员【' +
                task1[i].caozuoUnique +
                '】独占任务异常，请联系财务主管进行独占任务清理!'
              );
              break;
            } else {
              // 判断是不是自己；动态获取
              if (task1[i].caozuoUnique !== useUserStoreWidthOut().getUserInfo.username) {
                aa = false;
                message.error(
                  '提示：任务冲突！操作员【' +
                  task1[i].caozuoUnique +
                  '】正在进行月末结账处理，不能进行期初余额编辑，请销后再试，或联系财务主管清理该记账任务'
                );
                break;
              }
            }
          }
          if (task1[i].functionModule === '科目编辑' && task1[i].caozuoUnique != null) {
            if (task1[i].state === '0') {
              aa = false;
              message.error(
                '发现操作员【' +
                task1[i].caozuoUnique +
                '】独占任务异常，请联系财务主管进行独占任务清理!'
              );
              break;
            } else {
              // 判断是不是自己；动态获取
              if (task1[i].caozuoUnique !== useUserStoreWidthOut().getUserInfo.username) {
                aa = false;
                message.error(
                  '提示：任务冲突！操作员【' +
                  task1[i].caozuoUnique +
                  '】正在进行会计科目编辑操作，不能进行期初余额编辑，请销后再试，或联系财务主管清理该记账任务'
                );
                break;
              }
            }
          }
          if (task1[i].functionModule === '转账生成' && task1[i].caozuoUnique != null) {
            if (task1[i].state === '0') {
              aa = false;
              message.error(
                '发现操作员【' +
                task1[i].caozuoUnique +
                '】独占任务异常，请联系财务主管进行独占任务清理!'
              );
              break;
            } else {
              // 判断是不是自己；动态获取
              if (task1[i].caozuoUnique !== useUserStoreWidthOut().getUserInfo.username) {
                aa = false;
                message.error(
                  '提示：任务冲突！操作员【' +
                  task1[i].caozuoUnique +
                  '】正在进行转账生成编辑操作，不能进行期初余额编辑，请销后再试，或联系财务主管清理该记账任务'
                );
                break;
              }
            }
          }
          if (task1[i].functionModule === '期初余额' && task1[i].caozuoUnique != null) {
            if (task1[i].state === '0') {
              aa = false;
              message.error(
                '发现操作员【' +
                task1[i].caozuoUnique +
                '】独占任务异常，请联系财务主管进行独占任务清理!'
              );
              break;
            } else {
              // 判断是不是自己；动态获取
              if (task1[i].caozuoUnique !== useUserStoreWidthOut().getUserInfo.username) {
                aa = false;
                message.error(
                  '提示：任务冲突！操作员【' +
                  task1[i].caozuoUnique +
                  '】正在进行期初余额编辑操作，不能进行期初余额编辑，请销后再试，或联系财务主管清理该记账任务'
                );
                break;
              }
            }
          }
        }
      }
    } else {
      aa = false;
      message.error('本年度凭证已记账，不能进行期初余额编辑，请取消凭证记账后再运行本功能');
    }
  } else {
    aa = false;
    message.error('本年已发生结账月份，不能进行期初余额编辑，请取消结账和记账后再运行本功能');
  }
  if (aa) {
    let res = await useRouteApi(saveSubjectInitialBalance, {schemaName: databaseTrue.value})({
      params: o,
      accId: database.value,
      userName: userinfo.value.username,
      databasenum: databaseTrue.value,
      saveType: pageParameter.queryMark
    })
    message.success('修改已同步！')
    let resetTable = false;
    let dataSources = getDataSource()
    if (null != o?.superiorCcode && o.superiorCcode != '0') {
      dataSources = changeTableList(o.superiorCcode, dataSources) || []
      resetTable = true;
    }
    if (pageParameter.queryMark == '2') { // 最顶层
      resetTable = true;
      let v = o?.bprogerty == '1' ? parseFloat(o?.md) + parseFloat(o?.ljMc) - parseFloat(o?.mc) - parseFloat(o?.ljMd) : parseFloat(o?.mc) + parseFloat(o?.ljMd) - parseFloat(o?.md) - parseFloat(o?.ljMc)
      if (v > 0) {
        if (o?.bprogerty == '1') {
          o.yearMd = v
          o.yearMc = 0
        } else {
          o.yearMc = v
          o.yearMd = 0
        }
      } else {
        if (o?.bprogerty == '1') {
          o.yearMc = Math.abs(v)
          o.yearMd = 0
        } else {
          o.yearMd = Math.abs(v)
          o.yearMc = 0
        }
        o.bprogerty == '1' ? o.bprogerty = '0' : o.bprogerty = '1'
      }
    }
    if (resetTable || (null == o?.accvouid)) {
      o.accvouid = res.id
      loading.value = true
      let index = dataSources.findIndex(it => it.ccode == o?.ccode)
      dataSources.splice(index, 1, o)
      setTableData(dataSources)
      loading.value = false
    }
  }
}
const dbSave2 = async (o) => {
  if (o.the.md == o.md && o.the.mc == o.mc)return;
  if(parseFloat(o.md) + parseFloat(o.mc) == 0.0){
    message.warning("请填写借贷方金额!");
    return;
  }
  let t = o.the
  t.md = o.md
  t.mc = o.mc
  t.accvouid = o.the.id
  console.log(t)
  await useRouteApi(modifySubjectInitialBalanceFuZhu,{schemaName:databaseTrue.value})({
    params:t,
    accId: iyearselected.value,
    userName: userinfo.value.username,
    databasenum: databaseTrue.value,
  })
  findAllInitialBalance() // 刷新
}
const changeTableList = (upCcode, dataSourceList) => {
  let up = dataSourceList.filter(it => it.ccode == upCcode)[0]           // 当前上级科目
  let childs = dataSourceList.filter(it => it.superiorCcode == upCcode)  // 当前科目下所有子科目
  let mdSum = 0;
  let mcSum = 0;
  let ldSum = 0;
  let lcSum = 0;
  childs.forEach((it) => {
    mdSum += parseFloat(hasBlank(it.md) ? '0' : it.md)
    mcSum += parseFloat(hasBlank(it.mc) ? '0' : it.mc)
    ldSum += parseFloat(hasBlank(it.ljMd) ? '0' : it.ljMd)
    lcSum += parseFloat(hasBlank(it.ljMc) ? '0' : it.ljMc)
  })
  if (mdSum > mcSum) {
    (up.md = mdSum - mcSum)
    up.mc = 0
  } else {
    up.mc = mcSum - mdSum
    up.md = 0
  }
  if (pageParameter.queryMark == '2') {
    up.ljMd = ldSum
    up.ljMc = lcSum
    let v = up?.bprogerty == '1' ? parseFloat(up?.md) + parseFloat(up?.ljMc) - parseFloat(up?.mc) - parseFloat(up?.ljMd) : parseFloat(up?.mc) + parseFloat(up?.ljMd) - parseFloat(up?.md) - parseFloat(up?.ljMc)
    if (v > 0) {
      if (up?.bprogerty == '1') {
        up.yearMd = v
        up.yearMc = 0
      } else {
        up.yearMc = v
        up.yearMd = 0
      }
    } else {
      if (up?.bprogerty == '1') {
        up.yearMc = Math.abs(v)
        up.yearMd = 0
      } else {
        up.yearMd = Math.abs(v)
        up.yearMc = 0
      }
      up.bprogerty == '1' ? up.bprogerty = '0' : up.bprogerty = '1'
    }
  }
  let index = dataSourceList.findIndex(it => it.ccode == upCcode)
  dataSourceList.splice(index, 1, up)
  if (up.superiorCcode == '0') { // 不存在上级科目
    return dataSourceList
  } else { // 还存在上级
    return changeTableList(up.superiorCcode, dataSourceList)
  }
}
/*************** 中途启用 ****************/
const currentShowColumns = (type) => {
  let flag = false
  if (!menterage.value || !currencyType.value) flag = true
  if (type == 'lanmu') {
    return flag ? initDynamics()['DATA' + pageParameter.queryMark + '2'] : initDynamics()['DATA' + pageParameter.queryMark]
  } else {
    return flag ? tableColumns22.value : tableColumns2.value;
  }
}

// 结转上年
async function carryDown() {
  // 上年区间
  let upYearPeriod = await countAllByAccountIdAndIyear(database.value, parseInt(iyearselected.value) - 1)
  if (upYearPeriod == 0) {
    createConfirmPop('没有上年期间,无法结转')
    return false
  }
  // 查询 账套年度模式对照表 获取 租户
  let accountPeriod = await findDataBase(database.value, parseInt(iyearselected.value) - 1)
  if (accountPeriod == '') {
    createConfirmPop('账套年度模式对照表没有上年模式')
    return false
  } else {
    // 上年科目
    let upYearCode = await useRouteApi(company_findAllByIyear, {schemaName: accountPeriod.accountMode})({
      iyear: parseInt(iyearselected.value) - 1,
      databasenum: ''
    })
    if (upYearCode.length == 0) {
      createConfirmPop('没有上年科目,无法结转')
      return false
    }
  }

  carryDownModal.value = true
  // 会计科目
  codelist.value = await useRouteApi(company_findAllByIyear, {schemaName: accountPeriod.accountMode})({
    iyear: iyearselected.value,
    databasenum: ''
  });
  loading3.value = true

  let data = await useRouteApi(company_findByUpYearCodeAndThisYearCode, {schemaName: accountPeriod.accountMode})({
    iyear: iyearselected.value,
    styleStr: treeData.value[0].children.map(t => t.title).join(',')
  })
  dataSourceTable.value = data
  loading3.value = false
}

function createConfirmPop(text) {
  createConfirm({
    iconType: 'warning',
    title: '警告',
    content: text,
    onOk: async () => {
    },
  })
}

const filterOption = (input: string, option: any) => {
  return option.data.toLowerCase().indexOf(input.toLowerCase()) >= 0;
};

const modalRowKey = ref('')

function openSelectCode(data) {
  modalRowKey.value = data.key
  openCodePopPage(true, {
    database: databaseTrue.value,
    accId: database.value,
    iyear: iyearselected.value,
  });
}

function modalData(data) {
  dataSourceTable.value.filter(a => a.key === modalRowKey.value).map(t => t.thisCcode = data.ccode + '-' + data.ccodeName)
}

// 确认结转
async function carryDownOk() {
  let a = dataSourceTable.value.filter(a => a.thisCcode === '').length
  if (a > 0) {
    createConfirmPop('本年科目不能为空!!');
    return false;
  }
  loading3.value = true
  let map = {list: JSON.stringify(dataSourceTable.value), iyear: iyearselected.value}
  await useRouteApi(company_setNewYearQc, {schemaName: databaseTrue})(map)
    .then(data => {
      if (data === 'ok') {
        createConfirm({
          iconType: 'warning',
          title: '警告',
          content: '结转完成',
          onOk: async () => {
            loading3.value = false
            carryDownModal.value = false
            findAllInitialBalance()
            findByAccStyleAll()
          },
        })
      }
    })
}
</script>
<style scoped lang="less">

:deep(.ant-select){
  border: none;
  background-color: #f1f1f1;
  color: black;
  border-bottom: 1px solid #d2cfcf;
}
:deep(.ant-select:not(.ant-select-customize-input) .ant-select-selector){
  background-color: #f2f2f2;
}

a-table-font-size-12 :deep(td),
.a-table-font-size-12 :deep(th) {
  font-size: 14px !important;
  padding: 2px 8px !important;
}
.bg-white {
  border: 1px #cccccc solid;
  background: white;
  margin-top: 10px;
}

:deep(.ant-tree .ant-tree-node-content-wrapper.ant-tree-node-selected),
:deep(.ant-table-tbody > tr.ant-table-row-selected > td) {
  background-color: #1488b1;
  color: white;
}

@import '/@/assets/styles/part-open.less';
@import '/@/assets/styles/global-menu-index1.less';
.customize-modal {
  padding: 5% 10%;
  font-weight: bold;

  label {
    font-size: 13px;
    color: #666666;
  }

  .special-ulli-input {
    font-family: "Arial" !important;
    width: 60%;
    margin-left: 5em;
    text-align: right;
    border-top: hidden;
    border-left: hidden;
    border-right: hidden;
    border-color: #c9c9c9;
    color: black;
  }
}

.trial-balance-div {
  width: 460px;
  height: 300px;

  .tb-div-dashboard {
    width: 100%;
    height: 55%;
    text-align: center;
    display: flex;
    /*实现垂直居中*/
    align-items: center;
    /*实现水平居中*/
    justify-content: center;
  }

  .tb-div {
    text-align: center;
    border-bottom: 1px solid #dcdbdb;

    > span {
      font-size: 20px;
      font-weight: bold;
      color: red;
    }

    .tb-div-line {
      display: inline-block;
      width: 44%;
      height: 35%;
      margin: 0 1%;

      > span {
        font-weight: bold;
      }
    }
  }

  .tb-div-bottom {
    height: 10%;
    text-align: center;
    margin-top: 1%;

    > .ant-btn {
      width: 80%;
    }
  }
}

.pagination-text{
  position: absolute;
  font-size: 13px;
  color: black;
  z-index: 99999999;
}
:deep(.nc-summary){
  font-weight: bold;
  background-color: #cccccc!important;;
  border-right-color: #cccccc!important;
}
:deep(.vben-basic-table .ant-pagination) {
  margin-top: 0px;
  background-color: #cccccc;
  padding-right: 20px;
  padding-top: 5px;
  padding-bottom: 5px;
}
</style>

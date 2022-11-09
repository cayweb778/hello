<template>
  <div>
    <div class="app-container">
      <ProfileOutlined style="color: #0096c7;font-size: 60px;margin-top: 10px;"/>&emsp;
      <div style="width: 33%;margin-top: 9px;">
        <AccountPicker theme="three" readonly="" @reloadTable="dynamicAdReload" style="margin-top: 2px;"/>
        <div style="margin-top: 12px;"/>
        <span style="color:#666666;font-weight: bold;margin-left: 6px;">显示样式：</span><span style="color: black;font-weight: bold">{{ styleName }}</span>
        <span style="color:#666666;font-weight: bold;margin-left: 90px;">显示累计：</span><span><a-checkbox v-model:checked="ljchecked" @change="ljchange"/></span>
      </div>
      <div style="width: 29.5%;text-align: center;margin-top: 9px;">
        <span style="font-size: 24px;font-weight: bold;color:rgb(0 150 199)">发生及余额表</span><br>
        <span style="color:#666666;font-weight: bold;">期间：</span> <span style="color: #0f0f0f;font-weight: bold;">{{ time.strDate }} - {{ time.endDate }}</span>

      </div>
      <div style="width: 33%;text-align: right;margin-left: 8px;" v-if="pageMode=='1'">
        <a-button class="actod-btn"
          ant-click-animating-without-extra-node="false"
          @click="openQueryPage()"
        ><span>查询</span></a-button>
        <a-button
          class="actod-btn"
          ant-click-animating-without-extra-node="false"
          @click="exportExcelBtn()"
        ><span>导出表格</span></a-button>
        <a-button
          class="actod-btn"
          ant-click-animating-without-extra-node="false"
        ><span>导出PDF</span></a-button>
        <a-button
          class="actod-btn"
          ant-click-animating-without-extra-node="false"
          @click="openPrint()"
        ><span>打印</span></a-button>
        <a-button class="actod-btn" style="border-right: 1px solid #cccccc;" @click="closeCurrent(),router.push('/zhongZhang/home/welcome')"><span>退出</span></a-button>
        <p/>
        <a-select v-model:value="pageParameter.searchConditon.requirement" style="width: 150px;text-align: left;font-size: 12px;">
          <template v-for="item in searchConditonList">
            <a-select-option v-if="item.ifShow == true" :value="item.dataIndex">
              {{ item.title }}
            </a-select-option>
          </template>
        </a-select>
        <a-input-search
          placeholder=""
          v-model:value="pageParameter.searchConditon.value"
          @search="pageSearch"
          style="width: 150px;border-radius: 4px;border: 1px #cccccc solid;"
        />&emsp;
        <a-button class="ant-btn" @click="closeFilterV(),pageReload()">
          <SyncOutlined :style="{ fontSize: '14px' }"/>
        </a-button>
        <a-popover class="ant-btn-default" placement="bottom">
          <template #content>
            <a-popconfirm
              ok-text="确定"
              cancel-text="放弃"
              @confirm="confirm"
              @cancel="cancel">
              <template #icon><b>栏目设置</b><br></template>
              <template #title>
                <a-table bordered :data-source="dynamicColumnData" :columns="dynamicColumns"
                         childrenColumnName="children" :pagination="false"
                         style="max-height: 650px;overflow-y: auto" :class="'a-table-font-size-12'">
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
              </template>
              <a-button style="width: 128px;margin-bottom: 2px">栏目设置</a-button>
            </a-popconfirm>
            <br/>
            <span class="group-btn-span-special2" @click="pageParameter.showRulesSize = 'MAX'"
                  :style="pageParameter.showRulesSize==='MAX'?{backgroundColor: '#0096c7',color: 'white'}:''">
              &nbsp;大号字体&ensp;<CheckOutlined
              v-if="pageParameter.showRulesSize==='MAX'"/></span><br/>
            <span class="group-btn-span-special2" @click="pageParameter.showRulesSize = 'MIN'"
                  :style="pageParameter.showRulesSize==='MIN'?{backgroundColor: '#0096c7',color: 'white'}:''">
              &nbsp;小号字体&ensp;<CheckOutlined
              v-if="pageParameter.showRulesSize==='MIN'"/></span>
          </template>
          <a-button>
            <SettingFilled :style="{ fontSize: '14px' }"/>
          </a-button>
        </a-popover>
        <a-popover placement="bottom">
          <template #content>
             <span class="group-btn-span-special2" @click="onChangeSwitch('J')"
              :style="pageParameter.queryMark=='J'?{backgroundColor: '#0096c7',color: 'white',width:'150px'}:''">
              <SortDescendingOutlined/>&nbsp;金额式&emsp;&ensp;<CheckOutlined
              v-if="pageParameter.queryMark=='J'"/></span><br/>
                  <span class="group-btn-span-special2" @click="onChangeSwitch('SJ')"
                        :style="pageParameter.queryMark=='SJ'?{backgroundColor: '#0096c7',color: 'white',width:'150px'}:''">
              <SortAscendingOutlined/>&nbsp;数量金额式<CheckOutlined
                    v-if="pageParameter.queryMark=='SJ'"/></span><br/>
                  <span class="group-btn-span-special2" @click="onChangeSwitch('WJ')"
                        :style="pageParameter.queryMark=='WJ'?{backgroundColor: '#0096c7',color: 'white',width:'150px'}:''">
              <SortDescendingOutlined/>&nbsp;外币金额式<CheckOutlined
                    v-if="pageParameter.queryMark=='WJ'"/></span><br/>
                  <span class="group-btn-span-special2" @click="onChangeSwitch('SWJ')"
                        :style="pageParameter.queryMark=='SWJ'?{backgroundColor: '#0096c7',color: 'white',width:'150px'}:''">
              <SortAscendingOutlined/>&nbsp;数量外币式<CheckOutlined
                v-if="pageParameter.queryMark=='SWJ'"/></span><br/>
            </template>
            <a-button>
              <PicLeftOutlined :style="{ fontSize: '14px' }"/>
            </a-button>
        </a-popover>
      </div>
      <div style="width: 33%;text-align: right;" v-else>
        <a-button
          class="actod-btn"
          ant-click-animating-without-extra-node="false"
          @click="openQueryPage()"
        ><span>查询</span></a-button>
        <a-button class="actod-btn" style="border-right: 1px solid #cccccc;" @click="closeCurrent(),router.push('/zhongZhang/home/welcome')"><span>退出</span></a-button>
      </div>
    </div>
    <div class="app-container">
      <div class="app-container-bottom">
        <div v-if="pageMode=='1'" :style="{height: (windowHeight)+'px'}">
          <BasicTable
            ref="tableRef"
            :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
            :scroll="{ x: totalColumnWidth,y: windowHeight }"
            size="small"
            @register="registerTable"
            @fetch-success="fetchSuccess"
          >
            <template #ccode="{ record }">
              <span v-if="record.ccodeName!=null">{{record.ccode}}</span>
              <span v-if="record.ccodeName==null" style="color: black;font-weight: bold;">
                <span v-if="record.ccode.indexOf('小计')!=-1">
                  {{ record.ccode }}
                </span>
                <span v-else>
                  &nbsp;
                </span>
              </span>
            </template>
            <template #ccodeName="{ record }">{{record.ccodeName}}</template>
            <template #qcMd="{ record }">
              <span v-if="record.qcMd!=0" class="a-table-money-font-size a-table-font-arial">
                <span style="color: black;font-weight: bold;" v-if="record.ccode.indexOf('小计')>-1 || record.ccode.indexOf('合计')!=-1">
                  {{ moneyformat(record.qcMd) }}
                </span>
                <span v-else ><a style="cursor: pointer;"  @click="goRouter(record.ccode,'1')">{{ moneyformat(record.qcMd) }}</a></span>
              </span>
            </template>
            <template #qcMc="{ record }">
              <span v-if="record.ccode!='temp'" class="a-table-money-font-size a-table-font-arial">
                <span style="color: black;font-weight: bold;" v-if="record.ccode.indexOf('小计')!=-1 || record.ccode.indexOf('合计')!=-1">
                  {{ moneyformat(record.qcMc) }}
                </span>
                <span v-else ><a style="cursor: pointer;"  @click="goRouter(record.ccode,'1')">{{ moneyformat(record.qcMc) }}</a></span>
              </span>
            </template>
            <template #qcNum="{ record }">
              <span v-if="record.ccode!='temp'" class="a-table-money-font-size a-table-font-arial">
                <span style="color: black;font-weight: bold;" v-if="record.ccode.indexOf('小计')!=-1 || record.ccode.indexOf('合计')!=-1">
                  {{ moneyformat(record.qcNum) }}
                </span>
                <span v-else ><a style="cursor: pointer;"  @click="goRouter(record.ccode,'1')">{{ formatNum(record.qcNum) }}</a></span>
              </span>
            </template>
            <template #qcNfrat="{ record }">
              <span v-if="record.ccode!='temp'" class="a-table-money-font-size a-table-font-arial">
                <span style="color: black;font-weight: bold;" v-if="record.ccode.indexOf('小计')!=-1 || record.ccode.indexOf('合计')!=-1">
                  {{ moneyformat(record.qcNfrat) }}
                </span>
                <span v-else ><a style="cursor: pointer;"  @click="goRouter(record.ccode,'1')">{{ moneyformat(record.qcNfrat) }}</a></span>
              </span>
            </template>
            <template #pzMd="{ record }">
              <span v-if="record.ccode!='temp'" class="a-table-money-font-size a-table-font-arial">
                <span style="color: black;font-weight: bold;" v-if="record.ccode.indexOf('小计')!=-1 || record.ccode.indexOf('合计')!=-1">
                  {{ moneyformat(record.pzMd) }}
                </span>
                <span v-else ><a style="cursor: pointer;"  @click="goRouter(record.ccode,'1')">{{ moneyformat(record.pzMd) }}</a></span>
              </span>
            </template>
            <template #pzMc="{ record }">
              <span v-if="record.ccode!='temp'" class="a-table-money-font-size a-table-font-arial">
                <span style="color: black;font-weight: bold;" v-if="record.ccode.indexOf('小计')!=-1 || record.ccode.indexOf('合计')!=-1">
                  {{ moneyformat(record.pzMc) }}
                </span>
                <span v-else ><a style="cursor: pointer;"  @click="goRouter(record.ccode,'1')">{{ moneyformat(record.pzMc) }}</a></span>
              </span>
            </template>
            <template #pzNum="{ record }">
              <span v-if="record.ccode!='temp'" class="a-table-money-font-size a-table-font-arial">
                <span style="color: black;font-weight: bold;" v-if="record.ccode.indexOf('小计')!=-1 || record.ccode.indexOf('合计')!=-1">
                  {{ moneyformat(record.pzNum) }}
                </span>
                <span v-else ><a style="cursor: pointer;"  @click="goRouter(record.ccode,'1')">{{ formatNum(record.pzNum) }}</a></span>
              </span>
            </template>
            <template #pzNfrat="{ record }" class="a-table-money-font-size a-table-font-arial">
              <span v-if="record.ccode!='temp'">
                <span style="color: black;font-weight: bold;" v-if="record.ccode.indexOf('小计')!=-1 || record.ccode.indexOf('合计')!=-1">
                  {{ moneyformat(record.pzNfrat) }}
                </span>
                <span v-else ><a style="cursor: pointer;"  @click="goRouter(record.ccode,'1')">{{ moneyformat(record.pzNfrat) }}</a></span>
              </span>
            </template>
            <template #ljMd="{ record }">
              <span v-if="record.ccode!='temp'" class="a-table-money-font-size a-table-font-arial">
                <span style="color: black;font-weight: bold;" v-if="record.ccode.indexOf('小计')!=-1 || record.ccode.indexOf('合计')!=-1">
                  {{ moneyformat(record.ljMd) }}
                </span>
                <span v-else ><a style="cursor: pointer;"  @click="goRouter(record.ccode,'2')">{{ moneyformat(record.ljMd) }}</a></span>
              </span>
            </template>
            <template #ljMc="{ record }">
              <span v-if="record.ccode!='temp'" class="a-table-money-font-size a-table-font-arial">
                 <span style="color: black;font-weight: bold;" v-if="record.ccode.indexOf('小计')!=-1 || record.ccode.indexOf('合计')!=-1">
                  {{ moneyformat(record.ljMc) }}
                </span>
                <span v-else ><a style="cursor: pointer;"  @click="goRouter(record.ccode,'2')">{{ moneyformat(record.ljMc) }}</a></span>
              </span>
            </template>
            <template #ljNum="{ record }">
              <span v-if="record.ccode!='temp'" class="a-table-money-font-size a-table-font-arial">
                <span style="color: black;font-weight: bold;" v-if="record.ccode.indexOf('小计')!=-1 || record.ccode.indexOf('合计')!=-1">
                  {{ moneyformat(record.ljNum) }}
                </span>
                <span v-else ><a style="cursor: pointer;"  @click="goRouter(record.ccode,'2')">{{ formatNum(record.ljNum) }}</a></span>
              </span>
            </template>
            <template #ljNfrat="{ record }">
              <span v-if="record.ccode!='temp'" class="a-table-money-font-size a-table-font-arial">
                 <span style="color: black;font-weight: bold;" v-if="record.ccode.indexOf('小计')!=-1 || record.ccode.indexOf('合计')!=-1">
                  {{ moneyformat(record.ljNfrat) }}
                </span>
                <span v-else ><a style="cursor: pointer;"  @click="goRouter(record.ccode,'2')">{{ moneyformat(record.ljNfrat) }}</a></span>
              </span>
            </template>
            <template #qmNum="{ record }">
              <span v-if="record.ccode!='temp'" class="a-table-money-font-size a-table-font-arial">
                <span style="color: black;font-weight: bold;" v-if="record.ccode.indexOf('小计')!=-1 || record.ccode.indexOf('合计')!=-1">
                  {{ moneyformat(record.qmNum) }}
                </span>
                <span v-else ><a style="cursor: pointer;"  @click="goRouter(record.ccode,'2')">{{ formatNum(record.qmNum) }}</a></span>
              </span>
            </template>
            <template #qmNfrat="{ record }">
              <span v-if="record.ccode!='temp'" class="a-table-money-font-size a-table-font-arial">
                 <span style="color: black;font-weight: bold;" v-if="record.ccode.indexOf('小计')!=-1 || record.ccode.indexOf('合计')!=-1">
                  {{ moneyformat(record.qmNfrat) }}
                </span>
                <span v-else ><a style="cursor: pointer;"  @click="goRouter(record.ccode,'2')">{{ moneyformat(record.qmNfrat) }}</a></span>
              </span>
            </template>
            <template #qmMd="{ record }">
              <span v-if="record.ccode!='temp'" class="a-table-money-font-size a-table-font-arial">
                <span style="color: black;font-weight: bold;" v-if="record.ccode.indexOf('小计')!=-1 || record.ccode.indexOf('合计')!=-1">
                  {{ moneyformat(record.qmMd) }}
                </span>
                <span v-else ><a style="cursor: pointer;"  @click="goRouter(record.ccode,'2')">{{ moneyformat(record.qmMd) }}</a></span>
              </span>
            </template>
            <template #qmMc="{ record }">
              <span v-if="record.ccode!='temp'" class="a-table-money-font-size a-table-font-arial">
                <span style="color: black;font-weight: bold;" v-if="record.ccode.indexOf('小计')!=-1 || record.ccode.indexOf('合计')!=-1">
                  {{ moneyformat(record.qmMc) }}
                </span>
                <span v-else ><a style="cursor: pointer;"  @click="goRouter(record.ccode,'2')">{{ moneyformat(record.qmMc) }}</a></span>
              </span>
            </template>
            <template #summary>
              <TableSummary fixed>
                <TableSummaryRow style="background-color: #cccccc;" v-if="pageParameter.queryMark=='J'">
                  <TableSummaryCell class="nc-summary" :index="0" :align="'left'" :colSpan="3" style="border-right: none;">合计</TableSummaryCell>
                  <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.qcMd}}</TableSummaryCell>
                  <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.qcMc}}</TableSummaryCell>
                  <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.pzMd}}</TableSummaryCell>
                  <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.pzMc}}</TableSummaryCell>
                  <TableSummaryCell v-show="ljchecked" class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.ljMd}}</TableSummaryCell>
                  <TableSummaryCell v-show="ljchecked" class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.ljMc}}</TableSummaryCell>
                  <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.qmMd}}</TableSummaryCell>
                  <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.qmMc}}</TableSummaryCell>
                </TableSummaryRow>

                <TableSummaryRow style="background-color: #cccccc;" v-if="pageParameter.queryMark=='SJ'">
                  <TableSummaryCell class="nc-summary" :index="0" :align="'left'" :colSpan="4" style="border-right: none;">合计</TableSummaryCell>
                  <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.qcNum}}</TableSummaryCell>
                  <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.qcMd}}</TableSummaryCell>
                  <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.qcMc}}</TableSummaryCell>
                  <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.pzNum}}</TableSummaryCell>
                  <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.pzMd}}</TableSummaryCell>
                  <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.pzMc}}</TableSummaryCell>
                  <TableSummaryCell v-show="ljchecked" class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.ljNum}}</TableSummaryCell>
                  <TableSummaryCell v-show="ljchecked" class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.ljMd}}</TableSummaryCell>
                  <TableSummaryCell v-show="ljchecked" class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.ljMc}}</TableSummaryCell>
                  <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.qmNum}}</TableSummaryCell>
                  <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.qmMd}}</TableSummaryCell>
                  <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.qmMc}}</TableSummaryCell>
                </TableSummaryRow>

                <TableSummaryRow style="background-color: #cccccc;" v-if="pageParameter.queryMark=='WJ'">
                  <TableSummaryCell class="nc-summary" :index="0" :align="'left'" :colSpan="4" style="border-right: none;">合计</TableSummaryCell>
                  <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.qcNfrat}}</TableSummaryCell>
                  <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.qcMd}}</TableSummaryCell>
                  <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.qcMc}}</TableSummaryCell>
                  <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.pzNfrat}}</TableSummaryCell>
                  <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.pzMd}}</TableSummaryCell>
                  <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.pzMc}}</TableSummaryCell>
                  <TableSummaryCell v-show="ljchecked" class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.ljNfrat}}</TableSummaryCell>
                  <TableSummaryCell v-show="ljchecked" class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.ljMd}}</TableSummaryCell>
                  <TableSummaryCell v-show="ljchecked" class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.ljMc}}</TableSummaryCell>
                  <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.qmNfrat}}</TableSummaryCell>
                  <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.qmMd}}</TableSummaryCell>
                  <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.qmMc}}</TableSummaryCell>
                </TableSummaryRow>

                <TableSummaryRow style="background-color: #cccccc;" v-if="pageParameter.queryMark=='SWJ'">
                  <TableSummaryCell class="nc-summary" :index="0" :align="'left'" :colSpan="5" style="border-right: none;">合计</TableSummaryCell>
                  <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.qcNum}}</TableSummaryCell>
                  <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.qcNfrat}}</TableSummaryCell>
                  <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.qcMd}}</TableSummaryCell>
                  <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.qcMc}}</TableSummaryCell>

                  <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.pzNum}}</TableSummaryCell>
                  <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.pzNfrat}}</TableSummaryCell>
                  <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.pzMd}}</TableSummaryCell>
                  <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.pzMc}}</TableSummaryCell>

                  <TableSummaryCell v-show="ljchecked" class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.ljNum}}</TableSummaryCell>
                  <TableSummaryCell v-show="ljchecked" class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.ljNfrat}}</TableSummaryCell>
                  <TableSummaryCell v-show="ljchecked" class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.ljMd}}</TableSummaryCell>
                  <TableSummaryCell v-show="ljchecked" class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.ljMc}}</TableSummaryCell>

                  <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.qmNum}}</TableSummaryCell>
                  <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.qmNfrat}}</TableSummaryCell>
                  <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.qmMd}}</TableSummaryCell>
                  <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.qmMc}}</TableSummaryCell>
                </TableSummaryRow>
              </TableSummary>
            </template>
          </BasicTable>
          <div class="pagination-text" :style="{top: windowHeight+85+'px'}" v-show="showPageNumber">
            {{`共 ${pageNumber} 条记录&emsp;每页 1000 条`}}
          </div>
        </div>
        <div :style="{height: (60+windowHeight)+'px',overflowY: 'auto'}" v-else>
          <TablePiece v-if="startRender" :ljchecked="ljchecked" v-model="pageParameter" v-for="accId in queryAccIds"
                      :pageAccName="showCurrentName(accId)" :pageAccId="accId" :exportData="exportData" />
        </div>
        <QueryAll @save="loadPage" @register="registerQueryAllPage" />
        <ImportExcel @save="reload" @register="registerImportExcel" />
        <Print @save="loadPrint" @register="registerPrintPage"/>
        <Loading :loading="compState.loading" :absolute="compState.absolute" :tip="compState.tip" />
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import {BasicTable, useTable} from '/@/components/Table'
import {useModal} from '/@/components/Modal'

import {
  Checkbox as ACheckbox,
  DatePicker as ADatePicker,
  Drawer as ADrawer,
  Input as AInput,
  message,
  Popconfirm as APopconfirm,
  Popover as APopover,
  Radio as ARadio,
  Select as ASelect, Table,
  Table as ATable
} from "ant-design-vue"
import {
  ProfileOutlined,
  CheckOutlined,
  EditOutlined,
  FilterFilled,
  PicLeftOutlined,
  SettingFilled,
  SortAscendingOutlined,
  SortDescendingOutlined,
  SyncOutlined
} from '@ant-design/icons-vue'
import {findDbLanMuList, saveLanMuList} from '/@/api/record/system/accvoucher'
import {yueGlStore} from '/@/api/record/km_yue/generalLedger'

import {onMounted, provide, reactive, ref} from "vue";
import {useCompanyOperateStoreWidthOut} from "/@/store/modules/operate-company";
import {assemblyDynamicColumn, initDynamics} from "./data";
import QueryAll from "/@/views/boozsoft/system/book-balance/popup/queryAll2.vue";
import ImportExcel from "/@/views/boozsoft/system/book-balance/popup/importExcel.vue";
import Print from "/@/views/boozsoft/system/book-balance/popup/print.vue";
import {cloneDeep} from "lodash-es";
import {useMessage} from "/@/hooks/web/useMessage";
import {getCurrentAccountName} from "/@/api/task-api/tast-bus-api";
import {findAll,} from '/@/api/record/km_yue/data';
import {finByParameterAccuracy} from '/@/api/record/km_mingxi/data';
import {useUserStoreWidthOut} from "/@/store/modules/user";
import router from "/@/router";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker.vue";
import {useTabs} from "/@/hooks/web/useTabs";
import {useGo} from '/@/hooks/web/usePage';
import {exportExcel} from "/@/api/record/generalLedger/excelExport";
import {useNcLogger} from "/@/utils/boozsoft/record/recordUtils";
import TablePiece from "/@/views/boozsoft/system/book-balance/popup/TablePiece.vue";
import {useNewPrint} from "/@/utils/boozsoft/print/print";
import {tableStyle} from "/@/store/modules/abc-print";
/******************* 弹框加载中 **************************/
import {Loading} from '/@/components/Loading';
import {JsonTool} from "/@/api/task-api/tools/universal-tools";
import {toThousandFilter} from "/@/utils/calculation";
import AButton from "/@/components/Button/src/BasicButton.vue";


const ARangePicker = ADatePicker.RangePicker
const ASelectOption = ASelect.Option
const AInputSearch = AInput.Search
const ARadioGroup = ARadio.Group
const ARadioButton = ARadio.Button
const TableSummary = Table.Summary
const TableSummaryRow = Table.Summary.Row
const TableSummaryCell = Table.Summary.Cell
const {createConfirm, createWarningModal} = useMessage();
const go = useGo();
const startRender = ref(false);
const recordName = 'SystemLogger'
const {ncLogger} = useNcLogger({recordName})
// 全局常量
const glStore = yueGlStore()
const { closeCurrent } =useTabs();
const styleName = ref<String>("金额式")
// 科目名称标题
const titleName = ref<String>("")
// 本位币
const bwb = ref<String>("")
//币种名称
const bzName = ref<String>("")
//显示未记账
const ibook = ref<boolean>(true)
const ljchecked = ref<boolean>(false)
provide('ljchecked',ljchecked)
const showStyle = ref([
  {
    'name': '金额式',
    'value': 'J'
  }, {
    'name': '数量金额式',
    'value': 'SJ'
  }, {
    'name': '外币金额式',
    'value': 'WJ'
  }, {
    'name': '数量外币式',
    'value': 'SWJ'
  }
])
const time = reactive({
  endDate: "",
  strDate: ""
})
const exportData=ref({
  map:{}
})
const val = {
  openOne: 0
}
const visible = ref(false);
const searchConditonList = ref([])
const lanMuData = {'accId': '', 'menuName': '余额表', 'type': '', objects: '',username:useUserStoreWidthOut().getUserInfo.username}
// 表格参数
const loadMark = ref(false)
const tableSelectedRowKeys = ref([])
const tableSelectedRowObjs = ref([])
const windowWidth = (window.innerWidth - 70)
const windowHeight = (window.innerHeight - 350)
const totalColumnWidth = ref(0)
const tableRef = ref(null)
const userName = useUserStoreWidthOut().getUserInfo.username
const database = ref(getCurrentAccountName(true));
const accId = ref(getCurrentAccountName(false));
// 页面变量
const pageParameter:any = reactive({
  pzType: '',
  database: '',
  dynamicTenantId:'',
  openOne: '',
  companyCode: '',
  companyName: '',
  queryMark: 'J',
  showRulesSize: 'MIN',
  condition: {},
  searchConditon: {
    requirement: 'ccode',
    value: '',
  },
  filterConditon: {
    amountMaxQcMd: '',
    amountMinQcMd: '',
    amountMaxQcMc: '',
    amountMinQcMc: '',

    amountMaxPzMd: '',
    amountMinPzMd: '',
    amountMaxPzMc: '',
    amountMinPzMc: '',

    amountMaxQmMd: '',
    amountMinQmMd: '',
    amountMaxQmMc: '',
    amountMinQmMc: '',
  },
  reloadMark: false,
  wb: "",
  dw: "",
  km: "",
  minKm: "",
  maxKm: "",
  // 显示期间
  endDate: "",
  strDate: "",
  //显示未记账
  ishaveRjz: true,
  // 级次
  minJc: "1",
  maxJc: "1",
  bend: '1',
  jc: '1',
  numJd: '2',  // 数量精度
  priceJd: '2',  // 金额精度
  lvJd: '2',  // 汇率精度
  moneyJd: '2',  // 金额精度
  timflg: 'qj',
  bz: '',
  bzName:'全部',
  styleValue:'',
  cclass:'',
  kmList: [],
  querytype:'gs'
})
// 增加操作日志：1、修改2、删除3、停用、4查看、5导入、6导出、7打印
const logmap = ref({
  accId: database,
  username: useUserStoreWidthOut().getUserInfo.username,
  flag: '4',
  text: '',
});
const CrudApi = {
  list: findAll,
  columns: JSON.parse(JSON.stringify(glStore.getColumns(pageParameter.queryMark)))
}

const [registerQueryAllPage, {openModal: openQueryAllPageM}] = useModal()
const [registerImportExcel, {openModal: openImportExcel}] = useModal()
const [registerPrintPage, {openModal: openPrintPage}] = useModal()

// 组件实例区
const [registerTable, { reload,setTableData,setColumns,getColumns,getPaginationRef,setPagination,getDataSource }] = useTable({
  api: useRouteApi(CrudApi.list, {schemaName: database}),
  columns: CrudApi.columns,
  bordered: true,
  loading: loadMark.value,
  immediate: false,
  canResize: true,
  showIndexColumn: true, //显示序号列
  pagination: {
    pageSize: 1000,
    simple:true
  },
  searchInfo: pageParameter,
})

const showPageNumber=ref(false)
const pageNumber=ref(0)
function fetchSuccess(data) {
  let list=data.items.filter(a=>a.ccode.indexOf('小计')!=-1)
  pageNumber.value=data.items.length
  showPageNumber.value=true
  calculateTotal(list)
  if(data.items.length<50){
    for (let i =  data.items.length; i < 50; i++) {
      data.items.push({ccodeName: null,ccode:'temp',qcMd:null,qcMc:null,qcNum:null,qcNfrat:null,pzMd:null,pzMc:null,pzNum:null,pzNfrat:null,ljMd:null,ljMc:null,ljNum:null,ljNfrat:null,qmMd:null,qmMc:null,qmNum:null,qmNfrat:null})
    }
  }
}

const summaryTotals = ref({})
const calculateTotal = (datalist) => {
  let list = JsonTool.parseProxy(datalist)
  if (list.length == 0){
    summaryTotals.value = {}
    return false;
  }
  let qcMd = 0
  let qcMc = 0
  let qcNum = 0
  let qcNfrat = 0
  let pzMd = 0
  let pzMc = 0
  let pzNum = 0
  let pzNfrat = 0
  let qmMd = 0
  let qmMc = 0
  let qmNum = 0
  let qmNfrat = 0
  let ljMd = 0
  let ljMc = 0
  let ljNum = 0
  let ljNfrat = 0
  for (let i = 0; i < list.length; i++) {
    const e = list[i];
    qcMd += parseFloat(e.qcMd || '0')
    qcMc += parseFloat(e.qcMc || '0')
    qcNum += parseFloat(e.qcNum || '0')
    qcNfrat += parseFloat(e.qcNfrat || '0')
    pzMd += parseFloat(e.pzMd || '0')
    pzMc += parseFloat(e.pzMc || '0')
    pzNum += parseFloat(e.pzNum || '0')
    pzNfrat += parseFloat(e.pzNfrat || '0')
    qmMd += parseFloat(e.qmMd || '0')
    qmMc += parseFloat(e.qmMc || '0')
    qmNum += parseFloat(e.qmNum || '0')
    qmNfrat += parseFloat(e.qmNfrat || '0')
    ljMd += parseFloat(e.ljMd || '0')
    ljMc += parseFloat(e.ljMc || '0')
    ljNum += parseFloat(e.ljNum || '0')
    ljNfrat += parseFloat(e.ljNfrat || '0')
  }
  summaryTotals.value={
    qcMd: toThousandFilter(qcMd),
    qcMc: toThousandFilter(qcMc),
    pzMd: toThousandFilter(pzMd),
    pzMc: toThousandFilter(pzMc),
    qmMd: toThousandFilter(qmMd),
    qmMc: toThousandFilter(qmMc),
    ljMd: toThousandFilter(ljMd),
    ljMc: toThousandFilter(ljMc),
    ljNum: toThousandFilter(ljNum),
    ljNfrat: toThousandFilter(ljNfrat),
    qcNum: toThousandFilter(qcNum),
    qcNfrat: toThousandFilter(qcNfrat),
    pzNum: toThousandFilter(pzNum),
    pzNfrat: toThousandFilter(pzNfrat),
    qmNum: qmNum==0?'':toThousandFilter(qmNum),
    qmNfrat: toThousandFilter(qmNfrat),
  }
}

const loadingRef = ref(false);
const compState = reactive({
  absolute: false,
  loading: false,
  tip: '加载中...',
});
function openCompFullLoading() {
  openLoading(false);
}
function openLoading(absolute: boolean) {
  compState.absolute = absolute;
  compState.loading = true;
}
/*******************END**************************/
const showCurrentName = (v) => {
  console.log(v)
  console.log(accAuthList.value)
  let arr:any = accAuthList.value.filter(item=> item.accId == v)
  if (arr.length > 0) {
    return arr[0].accNameCn
  }
  return ''
}
function companyImportExcelView() {
  const data = JSON.parse(JSON.stringify(getDataSource()))
  const data2=data.splice(0,200)
  let columns:any = getColumns()
  if(!ljchecked.value){
    columns= columns.filter(v=>v.title!=='累计发生')
  }
  //根据columns 格式化导出excel数据
  let name = accNameAll.value
  const title = ['核算单位：'+name+'         期间：'+ time.strDate +' - '+ time.endDate +'         单位：元']
  const multiHeader:any = [[],[]]
  const keys:any = []
  const merges:any = []
  const cellStyle = [
    {
      cell: 'A1',
      font: {
        sz: 16,
        color: { rgb: "000000" },
        bold: true,
      },
      fill: {
        fgColor: { rgb: "ffffff" },
      },
    }
  ]
  const cell = ['A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z']
  let flg = 0;
  columns.forEach((v:any,index)=>{
    //多级表头
    if(v.children){
      multiHeader[0].push(v.title)
      for (let i = 0; i < v.children.length; i++) {
        if(i < v.children.length-1){
          multiHeader[0].push('')
        }
        multiHeader[1].push(v.children[i].title)
        keys.push(v.children[i].dataIndex)
      }
      //根据子节点数量合并单元格 5 56  6 78 7 9 10
      if(flg === 0){
        flg = index
      }
      merges.push('`' + cell[flg] + '3:' + cell[flg + v.children.length-1] +'3`');
      flg = flg + v.children.length
    }else{
      multiHeader[0].push(v.title)
      multiHeader[1].push('')
      keys.push(v.key)
      //合并上下单元格
      merges.push('`' + cell[flg] + '3:' + cell[flg] +'4`');
      flg = flg + 1
    }
  })
  merges.push('`A2:' + cell[keys.length-1] +'2`');
  data2.forEach(v=>{
    v.ccodeName=v.ccodeName===null?'':v.ccodeName
    v.qmMc=v.qmMc===0?'':moneyformat(v.qmMc)
    v.qmMd=v.qmMd===0?'':moneyformat(v.qmMd)
    v.qmNfrat=v.qmNfrat===0?'':moneyformat(v.qmNfrat)
    v.qmNum=v.qmNum===0?'':formatNum(v.qmNum)

    v.qcMc=v.qcMc===0?'':moneyformat(v.qcMc)
    v.qcMd=v.qcMd===0?'':moneyformat(v.qcMd)
    v.qcNfrat=v.qcNfrat===0?'':moneyformat(v.qcNfrat)
    v.qcNum=v.qcNum===0?'':formatNum(v.qcNum)

    v.pzMd=v.pzMd===0?'':moneyformat(v.pzMd)
    v.pzMc=v.pzMc===0?'':moneyformat(v.pzMc)
    v.pzNum=v.pzNum===0?'':formatNum(v.pzNum)
    v.pzNfrat=v.pzNfrat===0?'':moneyformat(v.pzNfrat)

    v.ljMc=v.ljMc===0?'':moneyformat(v.ljMc)
    v.ljMd=v.ljMd===0?'':moneyformat(v.ljMd)
    v.ljNfrat=v.ljNfrat===0?'':moneyformat(v.ljNfrat)
    v.ljNum=v.ljNum===0?'':formatNum(v.ljNum)
    v.unitMeasurement=v.unitMeasurement===null?'':v.unitMeasurement
    v.foreignCurrency=v.foreignCurrency===null?'':v.foreignCurrency
  })

  //样式靠右列
  let rightrow:any=[]
  //样式靠左列
  let leftrow:any=['A','B']
  if(pageParameter.queryMark === 'J'){
    rightrow=['C','D','E','F','G','H']
  }else if(pageParameter.queryMark === 'SJ'){
    rightrow=['E','I','F','H','K','L']
  }else if(pageParameter.queryMark === 'WJ'){
    rightrow=['F','H','K','E','J','G','I','J','L']
  }else{
    rightrow=['G','F','H','K','O','J','N','L','P']
  }
  const sheet =[
    {
      title: '科目余额表',
      tHeader: title,
      multiHeader: multiHeader,
      table: data2,
      keys: keys,
      merges: merges,
      sheetName: 'sheet1',
      cellStyle: cellStyle,
      rightrow:rightrow,
      leftrow:leftrow,
    },
  ]
  exportExcel(sheet, 'xlsx','科目余额表_'+time.strDate+'-'+time.endDate+'-'+name)
}
// 跳转科目明细账：type=1【日期：xxx - xxx】。2【日期：1 - xxx】
async function goRouter(ccode,type) {
  pageParameter.km=ccode
  pageParameter.bzName=pageParameter.bz
  pageParameter.openOne='2'
  // pageParameter.dynamicTenantId=database.value
  pageParameter.strDate=type==='1'?time.strDate.replaceAll(".", ""):time.strDate.split('.')[0]+'01'
  pageParameter.endDate=time.endDate.replaceAll(".", "")
  pageParameter.database=database.value
  router.push({
    path: '/zhongZhang/account-book/ab-kemuzhang/abk-mxtable',
    query: pageParameter,
  });
}
// 累计列是否显示
function ljchange(a){
  ljchecked.value=a.target.checked
  reloadColumns()
}

// 金额格式化
function moneyformat(data: any) {
  if(data==null||data==''||data==0||parseFloat(data)==0){return }
  let str = ""
  if(data){
    // 千分位保留2位小数
    var source = String(data.toFixed(pageParameter.moneyJd)).split("."); //按小数点分成2部分
    source[0] = source[0].replace( new RegExp('(\\d)(?=(\\d{3})+$)' , 'ig'), "$1,");//只将整数部分进行都好分割
    str = source.join("." );//再将小数部分合并进来
  }
  return str;
}
// 数量格式化
function formatNum(data:any){
  let str = ""
  if(data){
    if(0 === data){
      str = ""
    }else {
      var source = String(data.toFixed(pageParameter.numJd)).split("."); //按小数点分成2部分
      source[0] = source[0].replace( new RegExp('(\\d)(?=(\\d{3})+$)' , 'ig'), "$1,");//只将整数部分进行都好分割
      str = source.join("." );
    }
  }
  return str;
}

onMounted(async () => {
  val.openOne = 1
  openQueryAllPageM(true, {
    data: val,
    openType:'yue'
  })
})

const openQueryPage = () => {
  val.openOne = 0
  openQueryAllPageM(true, {
    data: val,
    database:database.value,
    openType:'yue'
  })
}

// 页面函数区
const onSelectChange = (selectedRowKeys, obj) => {
  tableSelectedRowKeys.value = selectedRowKeys;
  tableSelectedRowObjs.value = obj;
};
const pageReload = () => {
  reload({
    searchInfo: pageParameter
  })
}

const onChangeSwitch = async (v) => { // 动态列
  styleName.value = showStyle.value.filter(o => o.value === v)[0].name
  pageParameter.queryMark = v
  resetDynamicColumnData()
  pageParameter.searchConditon.requirement = 'ccode'
  pageParameter.searchConditon.value = ''
}
const pageSearch = async () => {
  // 搜索前校验格式
  if ('' == pageParameter.searchConditon.requirement.trim()) {
    message.warn('请选择检索条件')
    return false
  }
  // 校验完成后搜索
  closeFilterV()
  pageReload()
}

const filterSearch = async () => {
  // 期初
  let qcmdmin = pageParameter.filterConditon.amountMinQcMd.trim()
  let qcmdmax = pageParameter.filterConditon.amountMaxQcMd.trim()
  if (qcmdmax != '' || qcmdmin != '') {
    if (qcmdmin != '' && qcmdmax != '' && (!isRealNum(qcmdmin) || !isRealNum(qcmdmax) || parseFloat(qcmdmin) == 0 || parseFloat(qcmdmax) == 0)) {
      message.warn('请输入数值类型值并且值不能为0！')
      closeFilterV()
      return false
    } else if (parseFloat(qcmdmin) > parseFloat(qcmdmax)) {
      message.warn('金额区间最小值不能大于最大值！')
      closeFilterV()
      return false
    }
  }

  let qcmcmin = pageParameter.filterConditon.amountMinQcMc.trim()
  let qcmcmax = pageParameter.filterConditon.amountMaxQcMc.trim()
  if (qcmcmax != '' || qcmcmin != '') {
    if (qcmcmin != '' && qcmcmax != '' && (!isRealNum(qcmcmin) || !isRealNum(qcmcmax) || parseFloat(qcmcmin) == 0 || parseFloat(qcmcmax) == 0)) {
      message.warn('请输入数值类型值并且值不能为0！')
      closeFilterV()
      return false
    } else if (parseFloat(qcmcmin) > parseFloat(qcmcmax)) {
      message.warn('金额区间最小值不能大于最大值！')
      closeFilterV()
      return false
    }
  }
  // 凭证
  let pzmdmin = pageParameter.filterConditon.amountMinPzMd.trim()
  let pzmdmax = pageParameter.filterConditon.amountMaxPzMd.trim()
  if (pzmdmax != '' || pzmdmin != '') {
    if (pzmdmin != '' && pzmdmax != '' && (!isRealNum(pzmdmin) || !isRealNum(pzmdmax) || parseFloat(pzmdmin) == 0 || parseFloat(pzmdmax) == 0)) {
      message.warn('请输入数值类型值并且值不能为0！')
      closeFilterV()
      return false
    } else if (parseFloat(pzmdmin) > parseFloat(pzmdmax)) {
      message.warn('金额区间最小值不能大于最大值！')
      closeFilterV()
      return false
    }
  }
  let pzmcmin = pageParameter.filterConditon.amountMinPzMc.trim()
  let pzmcmax = pageParameter.filterConditon.amountMaxPzMc.trim()
  if (pzmcmax != '' || pzmcmin != '') {
    if (pzmcmin != '' && pzmcmax != '' && (!isRealNum(pzmcmin) || !isRealNum(pzmcmax) || parseFloat(pzmcmin) == 0 || parseFloat(pzmcmax) == 0)) {
      message.warn('请输入数值类型值并且值不能为0！')
      closeFilterV()
      return false
    } else if (parseFloat(pzmcmin) > parseFloat(pzmcmax)) {
      message.warn('金额区间最小值不能大于最大值！')
      closeFilterV()
      return false
    }
  }
  // 期末
  let qmmdmin = pageParameter.filterConditon.amountMinQmMd.trim()
  let qmmdmax = pageParameter.filterConditon.amountMaxQmMd.trim()
  if (qmmdmax != '' || qmmdmin != '') {
    if (qmmdmin != '' && qmmdmax != '' && (!isRealNum(qmmdmin) || !isRealNum(qmmdmax) || parseFloat(qmmdmin) == 0 || parseFloat(qmmdmax) == 0)) {
      message.warn('请输入数值类型值并且值不能为0！')
      closeFilterV()
      return false
    } else if (parseFloat(qmmdmin) > parseFloat(qmmdmax)) {
      message.warn('金额区间最小值不能大于最大值！')
      closeFilterV()
      return false
    }
  }
  let qmmcmin = pageParameter.filterConditon.amountMinQmMc.trim()
  let qmmcmax = pageParameter.filterConditon.amountMaxQmMc.trim()
  if (qmmcmax != '' || qmmcmin != '') {
    if (qmmcmin != '' && qmmcmax != '' && (!isRealNum(qmmcmin) || !isRealNum(qmmcmax) || parseFloat(qmmcmin) == 0 || parseFloat(qmmcmax) == 0)) {
      message.warn('请输入数值类型值并且值不能为0！')
      closeFilterV()
      return false
    } else if (parseFloat(qmmcmin) > parseFloat(qmmcmax)) {
      message.warn('金额区间最小值不能大于最大值！')
      closeFilterV()
      return false
    }
  }

  // 校验完成后搜索
  pageReload()
}

const closeFilterV = () => {
  pageParameter.filterConditon.amountMaxQcMd = ''
  pageParameter.filterConditon.amountMinQcMd = ''
  pageParameter.filterConditon.amountMaxQcMc = ''
  pageParameter.filterConditon.amountMinQcMc = ''

  pageParameter.filterConditon.amountMaxPzMd = ''
  pageParameter.filterConditon.amountMinPzMd = ''
  pageParameter.filterConditon.amountMaxPzMc = ''
  pageParameter.filterConditon.amountMinPzMc = ''

  pageParameter.filterConditon.amountMaxQmMd = ''
  pageParameter.filterConditon.amountMinQmMd = ''
  pageParameter.filterConditon.amountMaxQmMc = ''
  pageParameter.filterConditon.amountMinQmMc = ''

  visible.value = false
  reloadColumns()
}

function isRealNum(val) {
  // isNaN()函数 把空串 空格 以及NUll 按照0来处理 所以先去除，
  if (val === "" || val == null) {
    return false;
  }
  if (!isNaN(val)) {
    //对于空数组和只有一个数值成员的数组或全是数字组成的字符串，isNaN返回false，例如：'123'、[]、[2]、['123'],isNaN返回false,   //所以如果不需要val包含这些特殊情况，则这个判断改写为if(!isNaN(val) && typeof val === 'number' )
    return true;
  } else {
    return false;
  }
}

/*start栏目设置*/
const dynamicColumns = initDynamics().DEFAULT
const dynamicColumnData:any = ref([])
let dynamicColumnDataCopy = []
const editableData = reactive({});

const confirm = (e: MouseEvent) => {
  // 询问
  createConfirm({
    iconType: 'warning',
    title: '栏目同步',
    content: '是否将刚才设置同步数据库!',
    onOk: async () => {
      // 调整数据库 列参数
      lanMuData.accId = database.value
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
    } catch (e:any) {
      if (e.message == 'ok') {
        return true
      } else {
        return false
      }
    }
  })

  //对子节点处理  过滤有子节点并且变动 添加到a
  lanMuList.forEach((item, index) => {
    if (item.children) {
      let b = item.children.filter(item2 => {
        try {
          copyList[index].children.forEach(item3 => {
            if (item2.key === item3.key && item2.name == item3.name) {
              if (item2.nameNew != item3.nameNew || item2.width != item3.width || item2.check != item3.check || item2.align != item3.align)
                throw new Error('ok')
            }
          })
          return false
        } catch (e:any) {
          if (e.message == 'ok') {
            return true
          } else {
            return false
          }
        }
      })
      b.forEach(item => {
        a.push(item);
      })
    }
  })
  return a;
}

const cancel = (e: MouseEvent) => {
  // 恢复默认
  dynamicColumnData.value = []
  dynamicColumnData.value = dynamicColumnDataCopy
}


async function resetDynamicColumnData() {
  // 先从数据查询是否已经设置
  lanMuData.accId = database.value
  lanMuData.type = pageParameter.queryMark
  findDbLanMuList(lanMuData).then(res => {
    // 栏目列
    let dbList = res.items
    if (dbList.length > 0) {
      let statiList = initDynamics()[pageParameter.queryMark]
      dbList = combineParameters(statiList, dbList)
      dynamicColumnData.value = dbList
      dynamicColumnDataCopy = JSON.parse(JSON.stringify(dbList))
    } else {
      let statiList = initDynamics()[pageParameter.queryMark]
      dynamicColumnData.value = statiList
      dynamicColumnDataCopy = JSON.parse(JSON.stringify(statiList))
    }
    // 表格列
    reloadColumns()
    pageReload()
  })
}

function combineParameters(staticList: any, dbList: any) {
  staticList.forEach(item => {
    dbList.forEach(item2 => {
      if (item.key === item2.key && item.name === item2.name) {
        item.nameNew = item2.nameNew
        item.width = parseInt(item2.width)
        item.check = item2.check == 'true'
        item.align = item2.align
      } else {
        //对子节点处理
        if (item.children) {
          item.children.forEach(item3 => {
            if (item3.key === item2.key && item3.name === item2.name) {
              item3.nameNew = item2.nameNew
              item3.width = parseInt(item2.width)
              item3.check = item2.check == 'true'
              item3.align = item2.align
            }
          })
        }
      }
    })
  })
  return staticList
}

const edit = (key: string) => {
  if (key.toString().indexOf('-') != -1) {
    let arr:any = key.split('-');
    let one = parseInt(arr[0])
    if (arr.length == 2) {
      editableData[key] = cloneDeep(dynamicColumnData.value[one].children.filter(item => key === item.key)[0]);
    } else {
      let two = parseInt(String(arr[1] - 1))
      editableData[key] = cloneDeep(dynamicColumnData.value[one].children[two].children.filter(item => key === item.key)[0]);
    }
  } else {
    editableData[key] = cloneDeep(dynamicColumnData.value.filter(item => key === item.key)[0]);
  }
}

const save = (key: string, min: number, max: number) => {
  editableData[key].width = editableData[key].width > max ? max : editableData[key].width < min ? min : editableData[key].width
  if (key.toString().indexOf('-') != -1) {
    let arr:any = key.split('-');
    let one = parseInt(arr[0])
    if (arr.length == 2) {
      Object.assign(dynamicColumnData.value[one].children.filter(item => key === item.key)[0], editableData[key]);
      Object.assign(dynamicColumnData.value[one].children.filter(item => key === item.key)[0], editableData[key]);
    } else {
      let two = parseInt(String(arr[1] - 1))
      Object.assign(dynamicColumnData.value[one].children[two].children.filter(item => key === item.key)[0], editableData[key]);
    }
  } else {
    Object.assign(dynamicColumnData.value.filter(item => key === item.key)[0], editableData[key]);
  }
  delete editableData[key];
}

const saveName = (key: string) => {
  if (key.toString().indexOf('-') != -1) {
    let arr:any = key.split('-');
    let one = parseInt(arr[0])
    if (arr.length == 2) {
      Object.assign(dynamicColumnData.value[one].children.filter(item => key === item.key)[0], editableData[key]);
      Object.assign(dynamicColumnData.value[one].children.filter(item => key === item.key)[0], editableData[key]);
    } else {
      let two = parseInt(String(arr[1] - 1))
      Object.assign(dynamicColumnData.value[one].children[two].children.filter(item => key === item.key)[0], editableData[key]);
    }
  } else {
    Object.assign(dynamicColumnData.value.filter(item => key === item.key)[0], editableData[key]);
  }
  delete editableData[key];
}

const reloadColumns = () => {
  let a = []
  let newA:any = JSON.parse(JSON.stringify(glStore.getColumns(pageParameter.queryMark,ljchecked.value)))
  newA = assemblyDynamicColumn(dynamicColumnData.value, newA)
  setColumns(newA)
  initTableWidth(newA)
  //多表头 添加子节点筛选
  let seachList:any = []
  newA.forEach(item => {
    if (item.children) {
      item.children.forEach(item2 => {
        item2.title = item.title + item2.title
        seachList.push(item2)
      })
    } else {
      seachList.push(item)
    }
  })
  searchConditonList.value = seachList
}

function initTableWidth(thisCs) {
  let total = 60
  thisCs.forEach(item => {
    if (item.ifShow == null || item.ifShow)
      total += parseInt(item.width)
  })
  if (total > windowWidth) {
    let f = 0
    if (visible.value) f = 260
    totalColumnWidth.value = Number(windowWidth) - f
    // tableRef.value.$el.style.setProperty('width', (windowWidth + 20 - f) + 'px')
  } else {
    if (visible.value && (windowWidth - 260) < total) total -= (total - (windowWidth - 260))
    totalColumnWidth.value = total
    // tableRef.value.$el.style.setProperty('width', (total + 20) + 'px')
  }
}

// 格式化  Tue Jun 08 2021 16:57:19 GMT+0800 (中国标准时间)
function timeformat(dateData) {
  let date = new Date(dateData);
  let y = date.getFullYear();
  let m:any = date.getMonth() + 1;
  m = m < 10 ? '0' + m : m;
  let d:any = date.getDate();
  d = d < 10 ? '0' + d : d;
  return y + '-' + m + '-' + d;
}

/*栏目设置end*/
/*集团查询页面 页面参数*/
const pageMode = ref('1')
const queryAccIds = ref([])
const accAuthList:any = ref([])
const accNameAll = ref('')

const loadPage = async (map) => {
  let data = map.data
  accAuthList.value = map.accAuthList
  pageMode.value = data.queryMode

  // 回显筛选条件
  loadMark.value = false
  // 参数规则
  let ParameteRule= await useRouteApi(finByParameterAccuracy,{schemaName: database})('')
  if(ParameteRule.length>0){
    for (let i = 0; i < ParameteRule.length; i++) {
      if(ParameteRule[i].paraName==='汇率'){
        pageParameter.lvJd=ParameteRule[i].decimalPlaces
      }
      if(ParameteRule[i].paraName==='单价'){
        pageParameter.priceJd=ParameteRule[i].decimalPlaces
      }
      if(ParameteRule[i].paraName==='数量'){
        pageParameter.numJd=ParameteRule[i].decimalPlaces
      }
      if(ParameteRule[i].paraName==='金额'){
        pageParameter.moneyJd=ParameteRule[i].decimalPlaces
      }
    }
  }

  // 集团模式将分割成月份
  pageParameter.strDate = data.strDate.replaceAll("-", "")
  pageParameter.endDate = data.endDate.replaceAll("-", "")
  pageParameter.showRulesSize = data.fontSize
  bzName.value = data.bzName
  pageParameter.ishaveRjz = data.ishaveRjz
  pageParameter.minJc = data.minJc
  pageParameter.maxJc = data.maxJc
  pageParameter.bend = data.jc
  pageParameter.bz = data.bzName
  pageParameter.styleValue = data.styleValue
  pageParameter.cclass = JSON.stringify(data.styleList)
  pageParameter.kmList = data.kmList
  pageParameter.querytype = data.querytype
  pageParameter.pzType = data.pzType
  pageParameter.showRulesSize = 'MIN'
  time.strDate = data.strDate.replaceAll("-", ".")
  time.endDate = data.endDate.replaceAll("-", ".")

  // 外币金额式
  if(data.bzName!=='全部'){ pageParameter.queryMark = 'WJ';styleName.value='外币金额式' }

  if (pageMode.value == '1') {
    closeFilterV()
    pageParameter.searchConditon.value = ''
    resetDynamicColumnData()
  }else {
    queryAccIds.value = data.accIds
    // 字页面
    if (data.openOne == 1) {
      startRender.value = true
      pageParameter.reloadMark = false
    } else {
      startRender.value = false
      pageParameter.reloadMark = true
      startRender.value = true
    }
    resetDynamicColumnData()
  }
}

const dynamicAdReload = async (obj) =>{
  accNameAll.value = obj.companyName   // 账套全称
  loadMark.value = true
  bwb.value = obj.target.currencyName
  // 先获取部门组件查看是否存在部门信息
  pageParameter.page= getPaginationRef().current
  pageParameter.size= getPaginationRef().pageSize
  loadMark.value = false
}

function exportExcelBtn() {
  if(pageMode.value==='1'){
    companyImportExcelView()
  }else{
    jtImportExcelView()
  }
}
function jtImportExcelView() {
  let data:any=exportData.value['map']
  //根据columns 格式化导出excel数据
  const multiHeader:any = [[],[]]
  const keys:any = []
  const merges:any = []
  const cellStyle = [
    {
      cell: 'A1',
      font: {
        sz: 16,
        color: { rgb: "000000" },
        bold: true,
      },
      fill: {
        fgColor: { rgb: "ffffff" },
      }
    }
  ]
  const cell = ['A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z']
  let flg = 0;
  const columns=getColumns()
  columns.forEach((v:any,index)=>{
    //多级表头
    if(v.children){
      multiHeader[0].push(v.title)
      for (let i = 0; i < v.children.length; i++) {
        if(i < v.children.length-1){
          multiHeader[0].push('')
        }
        multiHeader[1].push(v.children[i].title)
        keys.push(v.children[i].dataIndex)
      }
      //根据子节点数量合并单元格 5 56  6 78 7 9 10
      if(flg === 0){
        flg = index
      }
      merges.push('`' + cell[flg] + '3:' + cell[flg + v.children.length-1] +'3`');
      flg = flg + v.children.length
    }else{
      multiHeader[0].push(v.title)
      multiHeader[1].push('')
      keys.push(v.key)
      //合并上下单元格
      merges.push('`' + cell[flg] + '3:' + cell[flg] +'4`');
      flg = flg + 1
    }
  })
  merges.push('`A2:' + cell[keys.length-1] +'2`');

  var sheet:any=[]
  for (var key in data) {
    //样式靠右列
    let rightrow:any=[]
    //样式靠左列
    let leftrow:any=['A','B']
    if(pageParameter.queryMark === 'J'){
      rightrow=['C','D','E','F','G','H']
    }else if(pageParameter.queryMark === 'SJ'){
      rightrow=['E','I','F','H','K','L']
    }else if(pageParameter.queryMark === 'WJ'){
      rightrow=['F','H','K','E','J','G','I','J','L']
    }else{
      rightrow=['G','F','H','K','O','J','N','L','P']
    }
    //筛选条件
    const title = [ "公司名称："+key+'   日期：'+time.strDate+' - '+time.strDate ]
    let tabledata=JSON.parse(JSON.stringify(data[key]))
    tabledata.forEach(v=>{
      v.ccodeName=v.ccodeName===null?'':v.ccodeName
      v.ljMc=v.ljMc===null?'':moneyformat(v.ljMc)
      v.ljMd=v.ljMd===null?'':moneyformat(v.ljMd)
      v.ljNfrat=v.ljNfrat===null?'':moneyformat(v.ljNfrat)
      v.ljNum=v.ljNum===null?'':moneyformat(v.ljNum)
      v.mc=v.mc===null?'':moneyformat(v.mc)
      v.md=v.md===null?'':moneyformat(v.md)
      v.mdF=v.mdF===null?'':moneyformat(v.mdF)
      v.ncS=v.ncS===null?'':moneyformat(v.ncS)
      v.ncnum=v.ncnum===null?'':formatNum(v.ncnum)
      v.ndS=v.ndS===null?'':formatNum(v.ndS)
      v.nfrat_mc=v.nfrat_mc===null?'':moneyformat(v.nfrat_mc)
      v.nfrat_md=v.nfrat_md===null?'':moneyformat(v.nfrat_md)
      v.yue=v.yue===null?'':moneyformat(v.yue)
      v.yue_nfrat=v.yue_nfrat===null?'':moneyformat(v.yue_nfrat)
      v.yue_num=v.yue_num===null?'':formatNum(v.yue_num)
      v.pzMc=v.pzMc===null?'':moneyformat(v.pzMc)
      v.pzMd=v.pzMd===null?'':moneyformat(v.pzMd)
      v.pzNfrat=v.pzNfrat===null?'':moneyformat(v.pzNfrat)
      v.pzNum=v.pzNum===null?'':formatNum(v.pzNum)
      v.qcMc=v.qcMc===null?'':moneyformat(v.qcMc)
      v.qcMd=v.qcMd===null?'':moneyformat(v.qcMd)
      v.qcNfrat=v.qcNfrat===null?'':moneyformat(v.qcNfrat)
      v.qcNum=v.qcNum===null?'':formatNum(v.qcNum)
      v.qmMc=v.qmMc===null?'':moneyformat(v.qmMc)
      v.qmMd=v.qmMd===null?'':moneyformat(v.qmMd)
      v.qmNfrat=v.qmNfrat===null?'':moneyformat(v.qmNfrat)
      v.qmNum=v.qmNum===null?'':formatNum(v.qmNum)
      v.unitMeasurement=v.unitMeasurement===null?'':v.unitMeasurement
      v.foreignCurrency=v.foreignCurrency===null?'':v.foreignCurrency
    })
    sheet.push(
      {
        title: '科目余额表',
        tHeader: title,
        multiHeader: multiHeader,
        table: tabledata,
        keys: keys,
        merges: merges,
        sheetName: key+'-科目余额表',
        cellStyle: cellStyle,
        rightrow:rightrow,
        leftrow:leftrow,
      },
    )
  }
  exportExcel(sheet, 'xlsx','科目余额表_'+time.strDate+'-'+time.endDate)
}

const openPrint = () => {
  openPrintPage(true, {
    data: {
      dynamicTenantId: database.value,
      defaultAdName: useCompanyOperateStoreWidthOut().getSchemaName,
      year: time.strDate.substring(0,4),
    }
  })
}
const loadPrint = (obj) => {
  openCompFullLoading()
  const data = JSON.parse(JSON.stringify(getDataSource()))
  let printList: any = []
  let qcMdDay = 0
  let qcMcDay = 0
  let pzMdDay = 0
  let pzMcDay = 0
  let qmMdDay = 0
  let qmMcDay = 0

  let printUser = ''
  if(obj.printUser){
    printUser = '制表人：' + userName
  }
  let printBz = ''
  if(obj.printBz){
    printBz = '币种：人民币'
  }

  data.forEach((item,index) => {
    let item1 = {}
    item1[0] = item.ccode
    item1[1] = setString(item.ccodeName==null?'':item.ccodeName,17)
    item1[2] = moneyformat(item.qcMd)
    item1[3] = moneyformat(item.qcMc)
    item1[4] = moneyformat(item.pzMd)
    item1[5] = moneyformat(item.pzMc)
    item1[6] = moneyformat(item.qmMd)
    item1[7] = moneyformat(item.qmMc)
    printList.push(item1)

    if (item.ccodeName!=null){
      let qcMd = item.qcMd==''?0:item.qcMd
      let qcMc = item.qcMc==''?0:item.qcMc
      let pzMd = item.pzMd==''?0:item.pzMd
      let pzMc = item.pzMc==''?0:item.pzMc
      let qmMd = item.qmMd==''?0:item.qmMd
      let qmMc = item.qmMc==''?0:item.qmMc
      qcMdDay = add(qcMdDay,qcMd)
      qcMcDay = add(qcMcDay,qcMc)
      pzMdDay = add(pzMdDay,pzMd)
      pzMcDay = add(pzMcDay,pzMc)
      qmMdDay = add(qmMdDay,qmMd)
      qmMcDay = add(qmMcDay,qmMc)
      // if ((index)>0 && (index)%26==0){
      //   let item2 = {}
      //   item2[0] = ''
      //   item2[1] = '承前页'
      //   item2[2] = moneyformat(qcMd)
      //   item2[3] = moneyformat(qcMc)
      //   item2[4] = moneyformat(pzMd)
      //   item2[5] = moneyformat(pzMc)
      //   item2[6] = moneyformat(qmMd)
      //   item2[7] = moneyformat(qmMc)
      //   printList.push(item2)
      // }
    } else {
      qcMdDay = 0
      qcMcDay = 0
      pzMdDay = 0
      pzMcDay = 0
      qmMdDay = 0
      qmMcDay = 0
      // if ((index)>0 && (index)%26==0) {
      //   let item2 = {}
      //   item1[0] = ''
      //   item1[1] = '承前页'
      //   item1[2] = moneyformat(item.qcMd)
      //   item1[3] = moneyformat(item.qcMc)
      //   item1[4] = moneyformat(item.pzMd)
      //   item1[5] = moneyformat(item.pzMc)
      //   item1[6] = moneyformat(item.qmMd)
      //   item1[7] = moneyformat(item.qmMc)
      //   printList.push(item2)
      // }
    }
  })
  for (let i=0; i<printList.length%27; i++){
    let item1 = {}
    item1[0] = ''
    item1[1] = ''
    item1[2] = ''
    item1[3] = ''
    item1[4] = ''
    item1[5] = ''
    item1[6] = ''
    item1[7] = ''
    printList.push(item1)
  }
  let num = Math.ceil(printList.length/27)
  useNewPrint({data: ['l', 'px', 'a4', true]}, (doc) => {
    compState.loading = false;
    loadMark.value = false
    doc.autoTable({
      head: [['', '', '', '科目余额表', '', '', '', ''],
        ['核算单位：' + accNameAll.value, '', '', '期间:' + time.strDate+' - '+time.endDate,'', '', printBz, '单位：元'],
        ['科目编码', '科目名称', '期初借方', '期初贷方', '本期借方', '本期贷方', '余额借方', '余额贷方']],
      body: printList,
      // startY: 60,
      styles: tableStyle(),
      margin: {
        left: 40,
        top: 20,
      },
      addPageContent: (data) => {
        //data.table.finalY 表格最大可容纳y轴坐标，超出时将根据设置决定是否另取一页.在页面需要分页时出现
        let tabHeigth = data.table.height,
          tabMarginTop = data.settings.margin.top,
          tabSize = data.table.finalY - tabMarginTop,//表格最大Y轴-表格顶部距离得到每页表格的最大值
          tabMarginLeft = data.settings.margin.left;
        if (data.table.finalY)//是否分页 有分页时才有该属性finalY
          if (data.pageNumber != Math.ceil(tabHeigth / tabSize)) return;//如果需要每一页都显示页尾则注释该行代码
        //data.cursor.y ,data.cursor.x:表格最后一个单元坐标
        data.doc.setFontSize(9)
        // data.doc.setFont('fuhuiR', 'bold')
        // doc.autoTableText(
        //   '核算单位：' + accNameAll.value,
        //   tabMarginLeft,
        //   data.cursor.y + 3,
        //   0
        // );
        doc.autoTableText(
          printUser,
          data.cursor.x/2-25,
          data.cursor.y + 3,
          0
        );
        doc.autoTableText(
          '第'+data.doc.getCurrentPageInfo().pageNumber+'页/共'+num+'页',
          // '第'+data.doc.getCurrentPageInfo().pageNumber+'页',
          data.cursor.x - 50,
          data.cursor.y + 3,
          0
        );
      },
      didParseCell(data) {
        data.cell.styles.cellPadding = {top: 3, left: 2, right: 2, bottom: 2}
        data.cell.styles.fillColor = [255, 255, 255]
        data.cell.styles.fontSize = 9
        data.cell.styles.lineColor = [150, 150, 150]
        // data.cell.styles.bold = false

        if (data.section == 'head' && data.row.index == 0) {

          data.cell.styles.fontSize = 20
          data.cell.styles.fontStyle = 'bold'
          data.cell.styles.lineColor = [255, 255, 255]
          if (data.column.index == 3) {
            data.cell.colSpan = 2
            data.cell.styles.halign = 'center'
          }
        }
        if (data.section == 'head' && data.row.index == 1) {
          data.cell.styles.fontSize = 10
          data.cell.styles.fontStyle = 'bold'
          data.cell.styles.lineColor = [255, 255, 255]
          if (data.column.index == 0) {
            data.cell.colSpan = 3
            data.cell.styles.halign = 'left'
          } else if (data.column.index == 3) {
            data.cell.colSpan = 2
            data.cell.styles.halign = 'center'
          }else {
            data.cell.styles.halign = 'right'
          }
        }
        if (data.section == 'head' && data.row.index == 2) {
          data.cell.styles.fontSize = 10
          data.cell.styles.cellPadding = {top: 4, left: 2, right: 2, bottom: 3}
          data.cell.styles.fontStyle = 'bold'
          data.cell.styles.fillColor = [230, 230, 230]
          data.cell.styles.halign = 'center'
        }
        if (data.section == 'body'){
          if (data.row.index%2==1) {
            data.cell.styles.fillColor = [240,240,240]
          }
          if (data.row.raw[0].indexOf('小计')>-1 || data.row.raw[0] == '合计') {
            data.cell.styles.fontStyle = 'bold'
          }
        }
      },
      columnStyles: {
        0: {maxHeight: 10,cellWidth: 70, halign: 'left'},
        1: {cellWidth: 70, halign: 'left'},
        2: {cellWidth: 70, halign: 'right'},
        3: {cellWidth: 70, halign: 'right'},
        4: {cellWidth: 70, halign: 'right'},
        5: {cellWidth: 70, halign: 'right'},
        6: {cellWidth: 70, halign: 'right'},
        7: {cellWidth: 70, halign: 'right'},
      }
    })
  })

}
//js切割字符串
function setString(str, len) {
  var strlen = 0;
  var s = "";
  for (var i = 0; i < str.length; i++) {
    if (str.charCodeAt(i) > 128) {
      strlen += 2;
    } else {
      strlen += 1.2;
    }
    s += str.charAt(i);
    if (strlen >= len) {
      return s+"...";
    }
  }
  return s;
}
//加
function add(a, b) {
  let c, d, e;
  try {
    c = a.toString().split(".")[1].length;
  } catch (f) {
    c = 0;
  }
  try {
    d = b.toString().split(".")[1].length;
  } catch (f) {
    d = 0;
  }
  return e = Math.pow(10, Math.max(c, d)), (a * e + b * e) / e;
}
//减
function sub(a, b) {
  let c, d, e;
  try {
    c = a.toString().split(".")[1].length;
  } catch (f) {
    c = 0;
  }
  try {
    d = b.toString().split(".")[1].length;
  } catch (f) {
    d = 0;
  }
  return e = Math.pow(10, Math.max(c, d)), (a * e - b * e) / e;
}
</script>
<style scoped lang="less">
@import '/@/assets/styles/part-open.less';
@import '/@/assets/styles/global-menu-index1.less';
:deep(.ant-select){
  border: none;
  background-color: #f1f1f1;
  color: black;
}

:deep(.ant-select:not(.ant-select-customize-input) .ant-select-selector){
  border: 1px #cccccc solid;
  height: 33px;
}

// ***************  button样式  ***************
.actod-btn {
  color: @Global-Comm-BcOrText-Color;
  font-size: 14px;
  border-color: @Global-Border-Color;
  border-right: none;
}

.actod-btn-last {
  border-right: 1px solid @Global-Border-Color;
}

.actod-btn:hover {
  background-color: @Global-Comm-BcOrText-Color;
  color: white;
}
// ***************  button样式  ***************
:deep(.vben-basic-table .ant-pagination) {
  margin-top: 0px;
  background-color: #cccccc;
  padding-right: 20px;
  padding-top: 5px;
  padding-bottom: 5px;
}
.app-container:nth-of-type(1) {
  background-color: #f2f2f2;
  padding: 10px 10px;
  margin: 10px 10px 5px;
  display: inline-flex;
  width: 99%;
  border-radius: 5px;
}

.app-container:nth-of-type(2) {
  padding: 0px;
  margin: 0px 10px;
  background: #b4c8e3 !important;
  margin-top: -6px;
  position: relative;
  .pagination-text{
    position: absolute;
    bottom: 9px;
    right: 12%;
    font-size: 13px;
    color: black;
    z-index: 99999999;
  }
}
.a-table-font-size-16 :deep(td),
.a-table-font-size-16 :deep(th) {
  font-size: 14% !important;
  padding: 2px 8px !important;
  height: 25px;
  color: black;
}
.a-table-font-size-12 :deep(td),
.a-table-font-size-12 :deep(th) {
  font-size: 13px !important;
  padding: 2px 8px !important;
  height: 25px;
  color: black;
}

.bg-white{
  border: 1px #cccccc solid;
  background:white ;
  margin-top: 10px;
}
:deep(.ant-tree .ant-tree-node-content-wrapper.ant-tree-node-selected),
:deep(.ant-table-tbody > tr.ant-table-row-selected > td){
  background-color: #1488b1;
  color: white;
}
// 合计行
:deep(.nc-summary){
  font-weight: bold;
  border-bottom-color: #9e9e9e;
  background-color: #cccccc!important;;
  border-right-color: #cccccc!important;
}
</style>


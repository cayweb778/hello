<template>
  <div>
    <div class="app-container">
      <ProfileOutlined style="color: #0096c7;font-size: 60px;margin-top: 10px;"/>&emsp;
      <div style="width: 33%;margin-top: 9px;">
        <AccountPicker theme="three" :readonly="!roweditflg" @reloadTable="dynamicAdReload"/>
        <span style="color: rgb(102, 102, 102);font-weight: bold;margin-left: 4px;">
          显示方式：
        </span>
        <a-select v-model:value="queryMark" style="width: 120px;text-align: center;" @change="initTable">
          <a-select-option value="J">金额式 </a-select-option>
          <a-select-option value="SJ">数量式 </a-select-option>
          <a-select-option value="WJ">外币式 </a-select-option>
        </a-select>
        &emsp;
        <label style="font-size: 14px;color: rgb(102, 102, 102);font-weight: bold;margin-left: 20px;">显示末级科目：</label>
        <a-checkbox @change="lastCodechecked" v-model:checked="lastCode"/>
<!--        <label style="font-size: 14px;;margin-left: 1em;">启用期间：</label><span style="font-size: 14px;font-weight: bold;margin-right: 1em;">{{ hasBlank(qujian)?'':qujian.substring(0,4)+'年'+qujian.substring(4)+'月' }}</span>-->
      </div>
      <div style="width: 29.5%;text-align: center;">
        <span style="font-size: 24px;font-weight: bold;color:rgb(0 150 199)">科目期初余额</span>
        <p/>
        <span style="color: rgb(102, 102, 102);font-weight: bold;margin-left: 4px;">
          年度：
        </span>
        <a-select v-model:value="iyearselected" style="width: 120px;text-align: center;" @change="findByAccStyleAll">
          <a-select-option :value="item.iyear" v-for="(item, i) in iyearlist">{{ item.iyear+'年' }} </a-select-option>
        </a-select>
      </div>
      <div style="width: 33%;text-align: right;">
        <Button v-if="ibookflg" class="actod-btn" @click="qcjzbtn('0')">恢复记账</Button>
          <Button v-if="!ibookflg" class="actod-btn" @click="editflg(edittext)">{{ edittext }} </Button>
          <span v-if="roweditflg">
            <Button class="actod-btn" @click="carryDown">结转上年</Button>
            <a-popover placement="bottom">
              <template #content>
                <span class="group-btn-span-special" @click="importPop('')">科目期初导入</span><br/>
                <span class="group-btn-span-special" @click="importPop('fz')">辅助期初导入</span>
              </template>
              <Button class="actod-btn">导入</Button>
            </a-popover>
            <Button class="actod-btn" @click="emptyAll2">清空</Button>
          </span>
          <Button class="actod-btn" @click="toSsphPage">试算</Button>
          <Button class="actod-btn">对账</Button>
          <Button class="actod-btn actod-btn-last" ant-click-animating-without-extra-node="false" @click="closeCurrent(),deltask()" >退出 </Button>
        <p/>
        <a-select v-model:value="selectSearchValue" class="acttdrd-search-select" @change="selectSearch2">
          <a-select-option value="ccode">科目编码</a-select-option>
          <a-select-option value="ccodeName">科目名称</a-select-option>
          <a-select-option value="cclass">余额方向</a-select-option>
          <a-select-option value="fuzhu">辅助项</a-select-option>
          <a-select-option value="menterage">计量单位</a-select-option>
          <a-select-option value="currencyType">外币币种</a-select-option>
        </a-select>
        <a-input-search placeholder="" v-model:value="inputsearchtext" style="width: 150px;" class="acttdrd-search-input" @search="selectSearch"/>
        <Button class="acttdrd-btn" @click="findAllInitialBalance(),findByAccStyleAll()">
          <SyncOutlined :style="{ fontSize: '14px' }"/>
        </Button>
        <Button class="acttdrd-btn" ant-click-animating-without-extra-node="false" @click="exportexcel">
          <UsbOutlined/>
        </Button>
        <Button class="acttdrd-btn" ant-click-animating-without-extra-node="false">
          <PrinterOutlined/>
        </Button>
        <a-popover placement="bottom" v-model:visible="visible">
          <template #content>
            <DynamicColumn :defaultData="(queryMark=='J'?initDynamics()['J']:queryMark=='SJ'?initDynamics()['SJ']:initDynamics()['WJ'])" :dynamicData="dynamicColumnData" :lanmuInfo="lanMuData" @reload="reloadColumns"/>
            <span class="group-btn-span-special2" @click="pageParameter.showRulesSize = 'MAX'" :style="pageParameter.showRulesSize==='MAX'?{backgroundColor: '#0096c7',color: 'white'}:''">
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
    <div class="app-container">
      <div class="bg-white" :style="{height: (windowHeight+50)+'px',display: 'inline',width: showCatalog?'300px':'20px',float: 'left',marginTop: '5px',overflow:showCatalog?'auto':'hidden',overflowY:'hidden'}" >
        <div v-show="showCatalog" style="width: 100%; height: 26px;text-align: center;background-color: rgb(216 216 216);">
          <label style="font-size: 14px;font-weight: bold;">科目目录</label> <ShrinkOutlined title="收起" class="exit-class" @click="showCatalog=!showCatalog"/>
        </div>
        <BasicTree
          v-show="showCatalog"
          defaultExpandAll
          :click-row-to-expand="false"
          :tree-data="treeData"
          :loading="loading2"
          v-model:selectedKeys="selectedKeys2"
          v-model:expandedKeys="expandedKeys"
          @select="handleSelect"/>
        <div v-show="!showCatalog" style="width: 20px;text-align: center; height: 100%;background-color: rgb(216 216 216);padding-top: 10%">
          <ArrowsAltOutlined class="exit-class" style="padding: 3px" title="展开"  @click="showCatalog=!showCatalog"/>
          <label style="font-size: 14px;font-weight: bold;">科目目录</label>
        </div>
      </div>
      <div class="app-container-bottom" :style="{height: (windowHeight+60)+'px',display: 'inline',width: showCatalog?'calc( 100% - 305px )':'calc( 100% - 22px )',float: 'right',marginTop: '5px'}">
        <BasicTable
          ref="tableRef"
          :row-selection="{ type: 'checkbox' ,fixed: true}"
          :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
          :scroll="{ x: totalColumnWidth,y: pageParameter.queryMark=='1'?(windowHeight-35):(windowHeight-40) }"
          :dataSource="tableData"
          :loading="loading"
          @register="registerTable"
        >
          <template #caozuo="{ record }">
            <!--          只有未停用的科目能编辑-->
            <span v-if="record.flag=='1' &&　(record.bnum=='1' || (null != record.currencyType && record.currencyType != '') || record.fuzhu.length > 0)">
                 <Button :disabled="!roweditflg" style="border-color: white;" size="small" v-if="record.bend === '1'" type="link" @click="setModal1Visible(record)">
                    <EditOutlined/>
                  </Button>
              </span>
          </template>
          <template #ncnum="{ record }">
              <span class="a-table-font-arial" :style="(record.ndS < 0 && record.ncS == 0) || record.ncS < 0 && record.ndS == 0?{color: 'red'}:{}">{{
                  record.ncnum != 0 ? record.ncnum : ''
                }}</span>
          </template>
          <template #ljSlMd="{ record }">
              <span class="a-table-font-arial" :style="(record.ljSlMd < 0 )?{color: 'red'}:{}">{{
                  record.ljSlMd != 0 ? record.ljSlMd : ''
                }}</span>
          </template>
          <template #ljSlMc="{ record }">
              <span class="a-table-font-arial" :style="(record.ljSlMc < 0 )?{color: 'red'}:{}">{{
                  record.ljSlMc != 0 ? record.ljSlMc : ''
                }}</span>
          </template>
          <template #yearNum="{ record }">
            <span class="a-table-font-arial">{{ record.yearNum != 0 ? record.yearNum : '' }}</span>
          </template>
          <template #md="{ record }">
            <template
              v-if="roweditflg && record.bend=='1' && record.fuzhu == '' && record?.bnum!='1' && (record.currencyType == null || record.currencyType == '')">
              <template v-if="record?.mdEdit">
                <a-input-number v-model:value="record.md"
                                :formatter="value =>`${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')"
                                :parser="value =>`${value}`.replace(/\$\s?|(,*)/g,'')"
                                :precision="2"
                                style="width: 90%;margin: -5px 0;height: 25px;line-height: 25px;"
                                @keyup.enter="record.mdEdit = null;dbSave(record)"/>
                <CheckOutlined @click="record.mdEdit = null;dbSave(record);"/>
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
            <template v-if="roweditflg && record.bend=='1' && record.fuzhu == '' && record?.bnum!='1' && (record.currencyType == null || record.currencyType == '')">
              <template v-if="record?.mcEdit">
                <a-input-number v-model:value="record.mc"
                                :formatter="value =>`${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')"
                                :parser="value =>`${value}`.replace(/\$\s?|(,*)/g,'')"
                                :precision="2" style="width: 90%;margin: -5px 0"
                                @keyup.enter="record.mcEdit = null;dbSave(record)"/>
                <CheckOutlined @click="record.mcEdit = null;dbSave(record)"/>
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
          <template #nfrat="{ record }">
              <span class="a-table-font-arial" :style="(record.nfratMd < 0 && record.nfratMc == 0) || record.nfratMc < 0 && record.nfratMd == 0?{color: 'red'}:{}">{{
                  record.nfrat != 0 ? money(record.nfrat) : ''
                }}</span>
          </template>
          <template #ljWbMd="{ record }">
              <span v-if="record.ljWbMd != 0" :style="(record.ljWbMd < 0 )?{color: 'red'}:{}"
                    class="a-table-font-arial">{{
                  money(record.ljWbMd)
                }}</span>
          </template>
          <template #ljWbMc="{ record }">
              <span v-if="record.ljWbMc != 0" :style="(record.ljWbMc < 0 )?{color: 'red'}:{}"
                    class="a-table-font-arial">{{
                  money(record.ljWbMc)
                }}</span>
          </template>
          <template #yearNfrat="{ record }">
              <span v-if="null != record.yearNfrat && record.yearNfrat != 0" class="a-table-font-arial">{{
                  money(record.yearNfrat)
                }}</span>
          </template>
          <template #bprogerty="{ record }">
            {{ record.bprogerty === '1' ? '借' : record.bprogerty === '0' ? '贷' : '' }}
          </template>
          <template #ljMd="{ record }">
            <template v-if="roweditflg && record.bend=='1' && record.fuzhu == '' && record?.bnum!='1' && (record.currencyType == null || record.currencyType == '')">
              <template v-if="record?.lmdEdit">
                <a-input-number v-model:value="record.ljMd"
                                :formatter="value =>`${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')"
                                :parser="value =>`${value}`.replace(/\$\s?|(,*)/g,'')"
                                :precision="2"
                                style="width: 90%;margin: -5px 0"
                                @keyup.enter="record.lmdEdit = null;dbSave(record)"/>
                <CheckOutlined @click="record.lmdEdit = null;dbSave(record)"/>
              </template>
              <template v-else>
                  <span class="a-table-font-arial" :style="(record.ljMd < 0 )?{color: 'red'}:{}">{{
                      record.ljMd == 0 ? '' : money(record.ljMd)
                    }}
                  <EditOutlined @click="record.lmdEdit = true;record.oldLjMd=record.ljMd"/></span>
              </template>
            </template>
            <template v-else>
                <span class="a-table-font-arial" :style="(record.ljMd < 0 )?{color: 'red'}:{}">{{
                    record.ljMd == 0 ? '' : money(record.ljMd)
                  }}</span>
            </template>
          </template>
          <template #ljMc="{ record }">
            <template v-if="roweditflg && record.bend=='1' && record.fuzhu == '' && record?.bnum!='1' && (record.currencyType == null || record.currencyType == '')">
              <template v-if="record?.lmcEdit">
                <a-input-number v-model:value="record.ljMc"
                                :formatter="value =>`${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')"
                                :parser="value =>`${value}`.replace(/\$\s?|(,*)/g,'')"
                                :precision="2"
                                style="width: 90%;margin: -5px 0"
                                @keyup.enter="record.lmcEdit = null;dbSave(record)"/>
                <CheckOutlined @click="record.lmcEdit = null;dbSave(record)"/>
              </template>
              <template v-else>
                  <span class="a-table-font-arial" :style="(record.ljMc < 0 )?{color: 'red'}:{}">{{
                      record.ljMc == 0 ? '' : money(record.ljMc)
                    }}
                  <EditOutlined @click="record.lmcEdit = true;record.oldLjMc=record.ljMc"/></span>
              </template>
            </template>
            <template v-else>
                <span class="a-table-font-arial" :style="(record.ljMc < 0 )?{color: 'red'}:{}">{{
                    record.ljMc == 0 ? '' : money(record.ljMc)
                  }}</span>
            </template>
          </template>
          <template #nYue="{ record }">
              <span class="a-table-font-arial">{{
                  (null != record.yearMd && record.yearMd != 0) ? money(record.yearMd) : (null != record.yearMc && record.yearMc != 0) ? money(record.yearMc) : ''
                }}</span>
          </template>
          <template #yearMd="{ record }">
              <span class="a-table-font-arial">{{
                  (null != record.yearMd && record.yearMd != 0) ? money(record.yearMd) : ''
                }}</span>
          </template>
          <template #yearMc="{ record }">
              <span class="a-table-font-arial">{{
                  (null != record.yearMc && record.yearMc != 0) ? money(record.yearMc) : ''
                }}</span>
          </template>

          <template #summary>
            <TableSummary fixed>
              <TableSummaryRow style="background-color: #cccccc;" >
                <TableSummaryCell class="nc-summary" :index="0" :align="'center'">合</TableSummaryCell>
                <TableSummaryCell class="nc-summary" :index="1" :align="'center'">计</TableSummaryCell>
                <TableSummaryCell class="nc-summary" v-for="cell in getCurrSummary()"  :index="cell.ind" :align="cell.align"><span class="a-table-font-arial">{{null == summaryTotals[cell.dataIndex]?'':summaryTotals[cell.dataIndex]}}</span></TableSummaryCell>
<!--                <TableSummaryCell class="nc-summary" :index="0" :colspan="queryMark=='J'?7:9" :align="'center'" style="border-right: none;">合计</TableSummaryCell>-->
<!--                <TableSummaryCell class="nc-summary" :index="1" :align="'right'" style="border-right: none;">{{ summaryTotals.md }}</TableSummaryCell>-->
<!--                <TableSummaryCell class="nc-summary" :index="1" :align="'right'" style="border-right: none;">{{ summaryTotals.mc }}</TableSummaryCell>-->
              </TableSummaryRow>

<!--              <TableSummaryRow style="background-color: #cccccc;" v-if="lanMuData.type=='累计'">-->
<!--                <TableSummaryCell class="nc-summary" :index="0" :colspan="queryMark=='J'?7:9" :align="'center'" style="border-right: none;">{{lanMuData.type}}合计</TableSummaryCell>-->
<!--                <TableSummaryCell class="nc-summary" :index="1" :align="'right'" style="border-right: none;">{{ summaryTotals.ncnum }}</TableSummaryCell>-->
<!--                <TableSummaryCell class="nc-summary" :index="1" :align="'right'" style="border-right: none;">{{ summaryTotals.nfrat }}</TableSummaryCell>-->
<!--                <TableSummaryCell class="nc-summary" :index="1" :align="'right'" style="border-right: none;">{{ summaryTotals.md }}</TableSummaryCell>-->
<!--                <TableSummaryCell class="nc-summary" :index="1" :align="'right'" style="border-right: none;">{{ summaryTotals.mc }}</TableSummaryCell>-->
<!--                <TableSummaryCell class="nc-summary" :index="1" :align="'right'" style="border-right: none;">{{ summaryTotals.ljMd }}</TableSummaryCell>-->
<!--                <TableSummaryCell class="nc-summary" :index="1" :align="'right'" style="border-right: none;">{{ summaryTotals.ljMc }}</TableSummaryCell>-->
<!--                <TableSummaryCell class="nc-summary" :index="1" :align="'right'" style="border-right: none;">{{ summaryTotals.nYue }}</TableSummaryCell>-->
<!--              </TableSummaryRow>-->
            </TableSummary>
          </template>
        </BasicTable>
        <div class="pagination-text" :style="{top: (windowHeight+25)+'px',left:(totalColumnWidth-600)+'px'}" v-show="showPageNumber">
          {{`共 ${tableDataAll.length}条记录&emsp;每页 1000 条`}}
        </div>
      </div>
    </div>
    <ImprotExcel @save="reloadProjects" @register="registerImportPage"/>
    <ImprotExcelFz @save="reloadProjects" @register="registerImportPageFz"/>
    <CodeModalPop @throwData="modalData" @register="registerCodePopPage" />
    <ThisEdit @register="registerEditPage" @save="findAllInitialBalance" :placement="'top'" :database="databaseTrue"></ThisEdit>
    <a-modal v-model:visible="modal2Visible" centered @ok="modal2Visible = false">
      <template #title>
        <div style="display: flex;" class="vben-basic-title">
          <img src="/create.svg" style="width:25px;margin-right: 10px;"/>
          <span style="line-height: 25px;font-size: 16px;">科目期初余额录入</span>
        </div>
      </template>
      <template #footer>
        <Button key="back" @click="(modal2Visible = false), (md = ''), (mc = ''), (ndS = ''), (ncS = ''), (nfrat = '')">
          取消
        </Button>
        <Button key="submit" type="primary" @click="submitPop" :disabled="lock0K">确定</Button>
      </template>
      <div class="customize-modal">
        <ul>
          <li>
            <label>本币借方金额:</label>
            <a-input v-model:value="md" @click="md = String(md).replace(/,/g, '')" autocomplete="off" class="special-ulli-input" />
          </li>
          <li>
            <label>本币贷方金额:</label>
            <a-input v-model:value="mc" @click="mc = String(mc).replace(/,/g, '')" autocomplete="off" class="special-ulli-input" />
          </li>
          <template v-if="isLj">
            <Divider style="height: 1px; background-color: #0096c7"/>
            <li>
              <label>累计借方金额:</label>
              <a-input
                v-model:value="mdLj"
                @click="mdLj = String(mdLj).replace(/,/g, '')"
                autocomplete="off"
                class="special-ulli-input"
              />
            </li>
            <li>
              <label>累计贷方金额:</label>
              <a-input v-model:value="mcLj"
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
                @click="nfrat = String(nfrat).replace(/,/g, '')"
                autocomplete="off"
                class="special-ulli-input"
              />
            </li>
            <li v-if="isLj">
              <label>外币累计借方:</label>
              <a-input
                v-model:value="nfratLj"
                @click="nfratLj = String(nfratLj).replace(/,/g, '')"
                autocomplete="off"
                class="special-ulli-input"/>
            </li>
            <li v-if="isLj">
              <label>外币累计贷方:</label>
              <a-input
                v-model:value="nfratLj2"
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
            <Progress stroke-linecap="square" :percent="textObj.left.proportion" :show-info="false"/>
            <span>{{textObj.left.text}}</span><br/>
            <span>{{textObj.left.value == ''?'0元':textObj.left.value+'元'}}</span>
          </div>
          <div class="tb-div-line">
            <Progress stroke-linecap="square" :percent="textObj.right.proportion" :show-info="false"/>
            <span>{{textObj.right.text}}</span><br/>
            <span>{{textObj.right.value == ''?'0元':textObj.right.value+'元'}}</span>
          </div>
        </div>
        <div class="tb-div-bottom">
          <Button type="primary" size="small" @click="modal1Visible = false">关闭</Button>
        </div>
      </div>
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
      <a-table bordered :loading="loading3" :pagination="false" :data-source="dataSourceTable" :columns="columns" :class="'a-table-font-size-12'" :scroll="{y:240}">
        <template #upCcode="{ record }">
          {{ setString(record.upCcode+'-'+record.upCcodeName,30) }}
        </template>
        <template #upMd="{ record }">
          <span v-if="record.upMd!==0" style="float: right;">{{  money(record.upMd) }}</span>
          <span v-if="record.upMc!==0" style="float: right;">{{ money(record.upMc) }}</span>
        </template>
        <template #thisCcode="{ record }">
          <span v-if="record.thisCcodeName!=''">{{ setString(record.thisCcode+'-'+record.thisCcodeName,30) }}</span>
          <span v-else>
              <a-select show-search v-model:value="record.thisCcode" :filter-option="filterOption" style="width: 80%">
                <a-select-option :value="item2.ccode +'-'+ item2.ccodeName" :key="item2.ccode" :data="item2.ccode +'-'+ item2.ccodeName" v-for="(item2, j) in codelist">
                  {{item2.ccode +'-'+ item2.ccodeName}}
                </a-select-option>
              </a-select>
              &nbsp;
              <a style="font-weight: bold;font-size: 18px;"><LinkOutlined @click="openSelectCode(record)" /></a>
            </span>
        </template>
      </a-table>
    </a-modal>
  </div>
</template>
<script setup="props, {emit}" lang="ts">
import {
  ProfileOutlined,
  ArrowsAltOutlined,
  CheckOutlined,
  EditOutlined,
  FilterFilled,
  LinkOutlined,
  PrinterOutlined,
  SettingFilled,
  ShrinkOutlined,
  SortAscendingOutlined,
  SortDescendingOutlined,
  SyncOutlined,
  UsbOutlined
} from '@ant-design/icons-vue';
import {BasicTable, useTable} from '/@/components/Table';
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker.vue";
import CodeModalPop from '/@/views/boozsoft/system/acccode2/popup/modalPop.vue';
import {
  Button,
  Checkbox as ACheckbox,
  Collapse,
  DatePicker as ADatePicker,
  Divider,
  Drawer as ADrawer,
  Input as AInput,
  InputNumber as AInputNumber,
  message,
  Modal as AModal,
  Popconfirm as APopconfirm,
  Popover as APopover,
  Progress,
  Radio as ARadio,
  Select as ASelect,
  Statistic as AStatistic,
  Table as ATable,
  Table
} from "ant-design-vue"
import {BasicTree} from '/@/components/Tree';
import {useModal} from '/@/components/Modal';
import {
  delInitalBalance,
  emptyAll,
  findAllSubjectInitialBalance,
  findAllSubjectInitialBalanceFuZhuList,
  findByAccvoucherIbook,
  findByFunctionModule,
  findByIperiodFlag,
  findBySubjectInitalBalabceIbook,
  pzjzibook,
  qcjz,
  qcjzsum,
  saveSubjectInitialBalance,
  saveTaskInfo,
  ssph,
} from '/@/api/subjectInitialBalance/subjectInitialBalance';
import {reactive, ref} from 'vue';
import ImprotExcel from './popup/improtExcel.vue';
import ImprotExcelFz from './popup/improtExcelFz.vue';
import ThisEdit from '/@/views/boozsoft/system/subjectInitialBalanceFuZhu/popup/edit.vue'
import {jsonToSheetXlsx} from '/@/components/Excel';
import {useUserStore, useUserStoreWidthOut} from '/@/store/modules/user';
import {
  countByTaskId,
  delFunctionModule,
  getCurrentAccountName,
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
import {JsonTool} from "/@/api/task-api/tools/universal-tools";
import {toThousandFilter} from "/@/utils/calculation";

const {createConfirm, createWarningModal} = useMessage();
const dataSourceTable:any =ref([]);
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
    key: 'upMd',slots: {customRender: "upMd"},width:150
  },{
    title: '本年科目',
    dataIndex: 'thisCcode',
    key: 'thisCcode',slots: {customRender: "thisCcode"}
  },
];
const ARangePicker = ADatePicker.RangePicker
const ASelectOption = ASelect.Option
const AInputSearch = AInput.Search
const ARadioGroup = ARadio.Group
const ARadioButton = ARadio.Button
const CollapsePanel = Collapse.Panel
const AStatisticCountdown = AStatistic.Countdown;
const TableSummary = Table.Summary
const TableSummaryRow = Table.Summary.Row
const TableSummaryCell = Table.Summary.Cell

const {closeCurrent} = useTabs(router);
// 期初记账/恢复期初记账  显示
const ibookflg = ref(false);
// 计时数值
const deadline:any = ref(Date.now() + 1000 * 10);
// 借贷方金额
const md = ref('');
const mc = ref('');
const ndS = ref('');
const ncS = ref('');
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
const ssphlist:any = ref([]);
const ssphlist2:any = ref([]);
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
// 金额式显示
const queryMark = ref('J');
// 显示末级科目
const lastCode = ref(false);
const showPageNumber = ref(false);
const tableDataAll:any = ref([]);
const tableData:any = ref([]);
// 任务管理信息
const taskinfo:any = ref('');
// 期初弹框回调信息
const datainfo:any = ref('');
// 期初弹框回调信息
const types = ref('');
// 数据库模式名称
const database = ref(getCurrentAccountName(false));
const databaseTrue = ref(getCurrentAccountName(true));
const databaseObj:any = ref('');
const databaselist = ref([]);
const userinfo = ref({
  databaseNum: database.value,
  username: useUserStoreWidthOut().getUserInfo.username,
  realName: useUserStoreWidthOut().getUserInfo.realName,
  name: useUserStoreWidthOut().getUserInfo.name,
  phone: useUserStoreWidthOut().getUserInfo.phone,
});
const userStore = useUserStore();
const codelist=ref([])
// 字体
const pageParameter:any = reactive({
  cclass: '全部',
  ccode:'',
  bend:'',
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
// 数量/外币 栏目是否显示
const menterage = ref(true)
const currencyType = ref(true)
const tableColumns = ref(
  {
    "J":[
      {
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
      },
      {
        title: "辅助项",
        dataIndex: "fuzhu",
      },
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
    ],
    "SJ":[
  {
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
  },
  {
    title: "辅助项",
    dataIndex: "fuzhu",
  },
  {
    title: "计量单位",
    dataIndex: "menterage",
  },

  {
    title: "数量",
    dataIndex: "ncnum",
    slots: {customRender: "ncnum"},
  },
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
],
    "WJ":[
  {
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
  },
  {
    title: "辅助项",
    dataIndex: "fuzhu",
  },
  {
    title: "外币币种",
    dataIndex: "currencyType",
  },
  {
    title: "外币金额",
    dataIndex: "nfrat",
    slots: {customRender: "nfrat"},
  },
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
],
  }
  )
const tableColumns2 = ref([
  {
    title: "操作",
    dataIndex: "caozuo",
    width: 60,
    slots: {customRender: "caozuo"}
  },
  {
    title: "科目编码",
    dataIndex: "ccode",
    align: "left",ellipsis: true,
  },
  {
    title: "科目名称",
    dataIndex: "ccodeName",
    align: "left",ellipsis: true,
  }, {
    title: "方向",
    dataIndex: "bprogerty",width: 60,
    slots: {customRender: "bprogerty"},
  },
  {
    title: "辅助项",
    dataIndex: "fuzhu",
  },
  {
    title: "计量单位",
    dataIndex: "menterage",
    defaultHidden: menterage,
  },

  {
    title: "数量",
    dataIndex: "ncnum",
    defaultHidden: menterage,
    slots: {customRender: "ncnum"},
  },
  {
    title: "外币币种",
    dataIndex: "currencyType",
    defaultHidden: currencyType,
  },
  {
    title: "外币金额",
    dataIndex: "nfrat",
    slots: {customRender: "nfrat"},
    defaultHidden: currencyType,
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
  },
  {
    title: '年初余额',
    dataIndex: 'nYue',
    align: 'right',
    slots: {customRender: 'nYue'},
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
const treeData:any = ref([]);
const selectedKeys2:any = ref([]);
const expandedKeys:any = ref([]);
const styleList:any = ref([]);

// 这是示例组件
const [registerImportPage, {openModal: openImprotPage}] = useModal();
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
  getSelectRows,
  getColumns
}] = useTable({
  showIndexColumn: true,
  indexColumnProps:{ fixed:true },
  pagination: {
    pageSize: 1000,
    simple:true
  },
});

// 过滤漏斗
function filterSearch() {
  pageParameter.filterConditon.amountMinMd = pageParameter.filterConditon.amountMinMd === '' ? '0' : pageParameter.filterConditon.amountMinMd
  pageParameter.filterConditon.amountMaxMd = pageParameter.filterConditon.amountMaxMd === '' ? '0' : pageParameter.filterConditon.amountMaxMd
  pageParameter.filterConditon.amountMinMc = pageParameter.filterConditon.amountMinMc === '' ? '0' : pageParameter.filterConditon.amountMinMc
  pageParameter.filterConditon.amountMaxMc = pageParameter.filterConditon.amountMaxMc === '' ? '0' : pageParameter.filterConditon.amountMaxMc

  let all = tableDataAll.value
  tableData.value = all.filter((a) =>
    parseFloat(a.md) >= parseFloat(pageParameter.filterConditon.amountMinMd) && parseFloat(a.md) <= parseFloat(pageParameter.filterConditon.amountMaxMd)
    && parseFloat(a.mc) >= parseFloat(pageParameter.filterConditon.amountMinMc) && parseFloat(a.mc) <= parseFloat(pageParameter.filterConditon.amountMaxMc)
  );
}

function selectSearch2() {
  if (inputsearchtext.value !== '') {
    findAllInitialBalance()
  }
}

// 检索条件
async function selectSearch() {
  let all:any = tableDataAll.value
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

function toSsphPage() {
  router.push({
    path: '/zhongZhang/qichus/qichu-menu/kemu-qichu-ssph-list',
    query: {iyear:iyearselected.value,standardSelected:standardSelected.value,databaseTrue:databaseTrue.value},
  });
  // ssphbtn()
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
    }else {
      textObj.left.value = '0'
    }
    if (b.length > 0 && b.filter(i => i.name == '合计').length > 0) {
      textObj.right.value = b.filter(i => i.name == '合计')[0].yue
    }else {
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
        let n =  parseInt(( r / l)*100)
        textObj.right.proportion = n
        textObj.proportion = n
      } else if (r > l) {
        textObj.right.proportion = 100
        let n =  parseInt(( l / r)*100)
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
  var list:any = []; // 没有单价金额/外币金额
  var list2:any = []; // 有单价金额/外币金额
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
    filename: '科目期初余额_'+databaseObj.value.baseName+'.xlsx',
  });
}

// 导入弹框
const importPop = (type) => {
  if (hasBlank(type)) {
    openImprotPage(true, {
      databaseTrue: databaseTrue.value,
      database: database.value,
      iyear: iyearselected.value,
      isLiji: lanMuData.type == '累计' ? true : false,
    });
  } else {
    openImprotPageFz(true, {
      databaseTrue: databaseTrue.value,
      database: database.value,
      iyear: iyearselected.value,
      isLiji: lanMuData.type == '累计' ? true : false ,
    });
  }
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
  var temp:any = []; //一个新的临时数组
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
    datainfo.value = data;
    modal2Visible.value = true;
    lock0K.value = false
    console.log(data.menterage)
    console.log(data.currencyType)
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
  }
};

function findLastDate(date) {
  date = new Date(date);
  date = 0 + (date).getTime() - 1000 * 60 * 60 * 24;
  date = new Date(date);
  date = date.getFullYear() + "-" + (date.getMonth() > 9 ? (date.getMonth() + 1) : "0" + (date.getMonth() + 1)) + "-" + (date.getDate() > 9 ? (date.getDate()) : "0" + (date.getDate()));
  return date;
}

function xiushiFuZhu(data) {
  let fuzhus:any = [];
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
      return message.error('提示：期初已记账不能进行编辑');
    }
    // 1、判断当年会计期间是否结过账
    const iperio = await useRouteApi(findByIperiodFlag, {schemaName: databaseTrue.value})({
      iyear: iyearselected.value,
      databasenum: databaseTrue.value
    })
    if(iperio>0){
      return message.error('本年已发生结账月份，不能进行期初余额编辑，请取消结账和记账后再运行本功能');
    }
    // 下一步判断-2、判断当年凭证是否已记账
    const accvoucheribook = await useRouteApi(findByAccvoucherIbook, {schemaName: databaseTrue.value})({
      iyear: iyearselected.value,
      databasenum: databaseTrue.value
    })
    if(accvoucheribook>0){
      return message.error('本年度凭证已记账，不能进行期初余额编辑，请取消凭证记账后再运行本功能');
    }

    // 下一步判断-3、判断任务
    const task1 = await useRouteApi(findByFunctionModule, {schemaName: databaseTrue.value})(iyearselected.value)
    // 没有任务；增加一条本人期初余额任务
    if (task1.length === 0) {
      aa = true;
    }
    else {
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
            if (task1[i].caozuoUnique !== null && task1[i].caozuoUnique !== useUserStoreWidthOut().getUserInfo.id) {
              aa = false;
              message.error(
                '提示：任务冲突！操作员【' +
                task1[i].username +
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
            if (task1[i].caozuoUnique !== null && task1[i].caozuoUnique !== useUserStoreWidthOut().getUserInfo.id) {
              aa = false;
              message.error(
                '提示：任务冲突！操作员【' +
                task1[i].username +
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
              task1[i].username +
              '】独占任务异常，请联系财务主管进行独占任务清理!'
            );
            break;
          } else {
            // 判断是不是自己；动态获取
            if (task1[i].caozuoUnique !== null && task1[i].caozuoUnique !== useUserStoreWidthOut().getUserInfo.id) {
              aa = false;
              message.error(
                '提示：任务冲突！操作员【' +
                task1[i].username +
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
              task1[i].username +
              '】独占任务异常，请联系财务主管进行独占任务清理!'
            );
            break;
          } else {
            // 判断是不是自己；动态获取
            if (task1[i].caozuoUnique !== null && task1[i].caozuoUnique !== useUserStoreWidthOut().getUserInfo.id) {
              aa = false;
              message.error(
                '提示：任务冲突！操作员【' +
                task1[i].username +
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
              task1[i].username +
              '】独占任务异常，请联系财务主管进行独占任务清理!'
            );
            break;
          } else {
            // 判断是不是自己；动态获取
            if (task1[i].caozuoUnique !== null && task1[i].caozuoUnique !== String(lanMuData.username)) {
              aa = false;
              message.error(
                '提示：任务冲突！操作员【' +
                task1[i].username +
                '】正在进行期初余额编辑操作123，不能进行期初余额编辑，请销后再试，或联系财务主管清理该记账任务'
              );
              break;
            }
          }
        }
      }
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

const getCurrSummary  = () => {
  return (getColumns().filter(it=>it.title != '序号' && it.ifShow).map((it,ind)=>{it['ind']=ind+2;return it;}))
}
const summaryTotals = ref({})
const calculateTotal = (datalist) => {
  let list = JsonTool.parseProxy(datalist)
  if (list.length == 0){
    summaryTotals.value = {}
    return false;
  }
  let ncnum = 0
  let nfrat = 0
  let md = 0
  let mc = 0
  let ljMd = 0
  let ljMc = 0
  let yearMd = 0
  let yearMc = 0
  let nYue = 0
  for (let i = 0; i < list.length; i++) {
    const e = list[i];
    md += parseFloat(e.md || '0')
    mc += parseFloat(e.mc || '0')
    ncnum += parseFloat(e.ncnum || '0')
    nfrat += parseFloat(e.nfrat || '0')
    ljMd += parseFloat(e.ljMd || '0')
    ljMc += parseFloat(e.ljMc || '0')
    yearMd += parseFloat(e.yearMd || '0')
    yearMc += parseFloat(e.yearMc || '0')
    nYue += parseFloat(e.nYue || '0')
  }
  summaryTotals.value={
    yearMd: toThousandFilter(yearMd),
    yearMc: toThousandFilter(yearMc),
    ljMd: toThousandFilter(ljMd),
    ljMc: toThousandFilter(ljMc),
    ncnum: toThousandFilter(ncnum),
    nfrat: toThousandFilter(nfrat),
    md: toThousandFilter(md),
    mc: toThousandFilter(mc),
    nYue: toThousandFilter(nYue),
  }
}
const initTable = ()=>{
  visible.value = true
  setTimeout(()=>{
      lanMuData.value.changeNumber+=1
      visible.value = false
    }
    ,100)
}
// 获取期初余额
const findAllInitialBalance = async () => {
  loading.value = true
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

  let map={
    iyear: iyearselected.value,
    lastCode: lastCode.value,
    databasenum: databaseTrue.value,
    ccode:pageParameter.ccode,
    cclass:pageParameter.cclass,
    bend:pageParameter.bend,
  }
  const a = await useRouteApi(findAllSubjectInitialBalance, {schemaName: databaseTrue.value})(map)
  if(a.tablesData.length<50){
    for (let i =  a.tablesData.length; i < 50; i++) {
      a.tablesData.push({})
    }
  }
  tableDataAll.value = a.tablesData;
  tableData.value = a.tablesData;
  calculateTotal(a.tablesData)
  showPageNumber.value=true
  loading.value = false;
  visible.value = false
};

// 金额格式化
function money(val: any) {
  if (val==undefined||val == null) {return };
  val = val.toString().replace(/\$|\,\-/g, '');
  if (isNaN(val)) {
    val = '0';
  }
  let fs = val.indexOf('-') != -1
  const sign = val === (val = Math.abs(val));
  val = Math.floor(val * 100 + 0.50000000001);
  let cents:any = val % 100;
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
  deltask()
  edittext.value ='开始编辑'
  visible.value = true
  roweditflg.value=false
  showPageNumber.value=false
  databaseObj.value=obj
  // 获取期间年度
  iyearlist.value=await findPeriod(obj.accId)
  loading.value = true;
  if (taskinfo.value !== ''){
    await deltask() //删除旧任务
    database.value = obj.accId;
    databaseTrue.value = obj.accountMode;
    iyearselected.value = obj.year;
    await  edittask() // 添加新的任务
  }else {
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
  if(period==''){
    loading.value = false
    return  message.error('没有本年期间!');
  }
  qujian.value = period // 本位币
  if (null == period || period.substring(4, 6) == '01') {
    pageParameter.queryMark = '1'
  } else {
    pageParameter.queryMark = '2'
  }
  bwb.value = obj.target.currencyName    // 本位币
  independent.value = datainfo.independent > 0 ? true : false;  // 1是独立账套 0是集团账套
  await findAllInitialBalance();

}

// 获取科目类型
async function findByAccStyleAll() {
  loading2.value = true
  const codelistall= await acctemplateFindByAccId(database.value)
  const accstandard= await findByStandardUnique(codelistall.uniqueAccStandard)
  const accStyleUnique= await findByAccStyle(accstandard.accStyleUnique)
  styleList.value=[]
  for (let i = 0; i < accStyleUnique.length; i++) {
    styleList.value.push(accStyleUnique[i].cclass)
  }
  let map={ style:styleList.value.join(','),iyear:iyearselected.value }
  fetch(map)
}

async function fetch(map) {
  const deptTree = await useRouteApi(company_findByIyearOrderByCcodeTree,{schemaName: databaseTrue})(map)
  if(deptTree!=null){
    function a(customerTree) {
      customerTree.forEach((item) => {
        if (item.children != null&&item.children!='') {
          a(item.children);
        }
        item.title=item.title
        item.key=item.key+'>>'+item.bend
      });
    }
    a(deptTree);
    treeData.value = []
    selectedKeys2.value=['0']
    expandedKeys.value=['0']
    treeData.value.push({title: '全部',key:'0',children: deptTree})
  }
  findAllInitialBalance()
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
  console.log(obj)
  if(obj.toString()!==''){
    if(!isNaN(obj.toString().split('>>')[0])){
      pageParameter.ccode=obj.toString().split('>>')[0]
      pageParameter.bend=obj.toString().split('>>')[1]
      pageParameter.cclass=''
      findAllInitialBalance()
    }else{
      pageParameter.ccode=''
      pageParameter.cclass=obj.toString().split('>>')[0]
      pageParameter.bend=obj.toString().split('>>')[1]
      findAllInitialBalance()
    }
  }else{
    selectedKeys2.value=['0']
    pageParameter.cclass='全部'
    pageParameter.ccode=''
    findAllInitialBalance()
  }
}
/*start栏目设置*/
 //import DynamicColumn from "/@/views/boozsoft/stock/stock_sales_add/component/DynamicColumn.vue";
const DynamicColumn=null

import {assemblyDynamicColumn, initDynamics} from "./data";
const dynamicColumnData:any = ref({value: []})
const dynamicColumns = initDynamics().DEFAULT
const lanMuData = ref({
  accId: databaseTrue.value,
  menuName: '科目期初余额信息',
  type: '列表',
  objects: '',
  username: useUserStoreWidthOut().getUserInfo.username,
  changeNumber: 0
})

const reloadColumns = () => {
  // dynamicColumnData.value.value= initDynamics()[queryMark.value]
  lanMuData.value.type = pageParameter.queryMark == '1' ? '标准' : '累计'
  let newA = JSON.parse(JSON.stringify(tableColumns.value[queryMark.value]))
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
    if (visible.value) f = 380
    totalColumnWidth.value = Number(windowWidth) - f
    tableRef.value.$el.style.setProperty('width', (windowWidth + 62 - f) + 'px')
  } else {
    if (visible.value && (windowWidth - 380) < total) total -= (total - (windowWidth - 380))
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
  if(iperio>0){
    return  message.error('本年已发生结账月份，不能进行期初余额编辑，请取消结账和记账后再运行本功能');
  }

  // 下一步判断-2、判断当年凭证是否已记账
  const accvoucheribook = await useRouteApi(findByAccvoucherIbook, {schemaName: databaseTrue.value})({
    iyear: iyearselected.value,
    databasenum: databaseTrue.value
  })
  if(accvoucheribook>0){
    return message.error('本年度凭证已记账，不能进行期初余额编辑，请取消凭证记账后再运行本功能');
  }

  // 下一步判断-3、判断任务管理中是否存在记账任务
  const task1 = await useRouteApi(findByFunctionModule, {schemaName: databaseTrue.value})(iyearselected.value)
  // 没有任务；增加一条本人期初余额任务
  if (task1.length === 0) {
    aa = true;
  }
  else {
    for (let i = 0; i < task1.length; i++) {
      if (task1[i].functionModule === '凭证记账' && task1[i].caozuoUnique != null) {
        if (task1[i].state === '0') {
          aa = false;
          message.error(
            '发现操作员【' +
            task1[i].username +
            '】独占任务异常，请联系财务主管进行独占任务清理!'
          );
          break;
        } else {
          // 判断是不是自己；动态获取
          if (task1[i].caozuoUnique !== useUserStoreWidthOut().getUserInfo.id) {
            aa = false;
            message.error(
              '提示：任务冲突！操作员【' +
              task1[i].username +
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
            task1[i].username +
            '】独占任务异常，请联系财务主管进行独占任务清理!'
          );
          break;
        } else {
          // 判断是不是自己；动态获取
          if (task1[i].caozuoUnique !== useUserStoreWidthOut().getUserInfo.id) {
            aa = false;
            message.error(
              '提示：任务冲突！操作员【' +
              task1[i].username +
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
            task1[i].username +
            '】独占任务异常，请联系财务主管进行独占任务清理!'
          );
          break;
        } else {
          // 判断是不是自己；动态获取
          if (task1[i].caozuoUnique !== useUserStoreWidthOut().getUserInfo.id) {
            aa = false;
            message.error(
              '提示：任务冲突！操作员【' +
              task1[i].username +
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
            task1[i].username +
            '】独占任务异常，请联系财务主管进行独占任务清理!'
          );
          break;
        } else {
          // 判断是不是自己；动态获取
          if (task1[i].caozuoUnique !== useUserStoreWidthOut().getUserInfo.id) {
            aa = false;
            message.error(
              '提示：任务冲突！操作员【' +
              task1[i].username +
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
            task1[i].username +
            '】独占任务异常，请联系财务主管进行独占任务清理!'
          );
          break;
        } else {
          // 判断是不是自己；动态获取
          if (task1[i].caozuoUnique !== useUserStoreWidthOut().getUserInfo.id) {
            aa = false;
            message.error(
              '提示：任务冲突！操作员【' +
              task1[i].username +
              '】正在进行期初余额编辑操作，不能进行期初余额编辑，请销后再试，或联系财务主管清理该记账任务'
            );
            break;
          }
        }
      }
    }
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
  console.log(o)
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
  return tableColumns2.value
}

// 结转上年
async function carryDown() {
  // 上年区间
  let upYearPeriod=await countAllByAccountIdAndIyear(database.value,parseInt(iyearselected.value)-1)
  if(upYearPeriod==0){
    createConfirmPop('没有上年期间,无法结转')
    return false
  }
  // 查询 账套年度模式对照表 获取 租户
  let accountPeriod=await findDataBase(database.value,parseInt(iyearselected.value)-1)
  if(accountPeriod==''){
    createConfirmPop('账套年度模式对照表没有上年模式')
    return false
  }else{
    // 上年科目
    let upYearCode=await useRouteApi(company_findAllByIyear, {schemaName: accountPeriod.accountMode})({iyear:parseInt(iyearselected.value)-1,databasenum:''})
    if(upYearCode.length==0){
      createConfirmPop('没有上年科目,无法结转')
      return false
    }
  }

  carryDownModal.value=true
  // 会计科目
  codelist.value=await useRouteApi(company_findAllByIyear,{schemaName: accountPeriod.accountMode})({iyear:iyearselected.value,databasenum:''});
  loading3.value=true

  let data=await useRouteApi(company_findByUpYearCodeAndThisYearCode, {schemaName: accountPeriod.accountMode})({iyear:iyearselected.value,styleStr:treeData.value[0].children.map(t=>t.title).join(',')})
  dataSourceTable.value=data
  loading3.value=false
}
function createConfirmPop(text) {
  createConfirm({
    iconType: 'warning',
    title: '警告',
    content: text,
    onOk: async() => {},
  })
}

const filterOption = (input: string, option: any) => {
  return option.data.toLowerCase().indexOf(input.toLowerCase()) >= 0;
};

const modalRowKey=ref('')
function openSelectCode(data) {
  modalRowKey.value=data.key
  openCodePopPage(true, {
    database: databaseTrue.value,
    accId: database.value,
    iyear: iyearselected.value,
  });
}

function modalData(data) {
  dataSourceTable.value.filter(a=>a.key===modalRowKey.value).map(t=>t.thisCcode=data.ccode+'-'+data.ccodeName)
}

// 确认结转
async function carryDownOk() {
  let a=dataSourceTable.value.filter(a=>a.thisCcode==='').length
  if(a>0){
    createConfirmPop('本年科目不能为空!!');
    return false;
  }
  loading3.value=true
  let map={list:JSON.stringify(dataSourceTable.value),iyear:iyearselected.value}
  await useRouteApi(company_setNewYearQc, {schemaName: databaseTrue})(map)
    .then(data=>{
      if(data==='ok'){
        createConfirm({
          iconType: 'warning',
          title: '警告',
          content: '结转完成',
          onOk: async() => {
            loading3.value=false
            carryDownModal.value=false
            findAllInitialBalance()
            findByAccStyleAll()
          },
        })
      }
    })
}
</script>
<style scoped lang="less">
@import '/@/assets/styles/part-open.less';
@import '/@/assets/styles/global-menu-index1.less';
:deep(.ant-select){
  border: none;
  background-color: #f1f1f1;
  color: black;
  border-bottom: 1px solid #d2cfcf;
}
:deep(.ant-select:not(.ant-select-customize-input) .ant-select-selector){
  background-color: #f2f2f2;
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
  padding: 10px 5px;
  margin: 10px 10px 5px;
  display: inline-flex;
  width: 100%;
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
    font-size: 13px;
    color: black;
    z-index: 99999999;
  }
}
.a-table-font-size-12 :deep(td),
.a-table-font-size-12 :deep(th) {
  font-size: 13px !important;
  padding: 2px 8px !important;
  border-color: #aaaaaa !important;
  height: 25px;
  color: black;
}

:deep(.h-full){
  margin-top: 5px;
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
.exit-class{
  float: right;padding: 6px;cursor: pointer;
  font-weight: bold;
}
.exit-class:hover{
  background-color: plum;
}
:deep(.nc-summary){
  font-weight: bold;
  background-color: #cccccc!important;;
  border-right-color: #cccccc!important;
}

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
</style>

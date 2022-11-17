<template>
  <div class="app-container">
    <div class="app-container-top lcr-theme-div">
      <div>
        <div>
          <CopyOutlined style="color: white;font-size: 50px;"/>
        </div>
        <div>  <AccountPicker theme="three" @reloadTable="dynamicAdReload"  :dataFun="accountPickerFuns" :readonly="status != 3?'':'false'"/></div>
      </div>
      <div></div>
      <div>
        <div>
          <Button class="actod-btn" @click="router.push('/kc-xsDepot-list')">查询</Button>
          <Button class="actod-btn" @click="startEdit('add')" v-if="status == 3">新增</Button>
          <Button class="actod-btn" v-if="status == 3 && formItems.bcheck == '1'">变更</Button>
          <Button class="actod-btn" @click="saveData" v-if="status == 1 || status == 2">保存</Button>
          <Button class="actod-btn" @click="giveUp" v-if="status == 1 || status == 2">放弃</Button>
          <Button class="actod-btn" @click="startEdit('edit')"
                  v-if="status == 3 && formItems.bcheck != '1'">修改
          </Button>
          <Button class="actod-btn" @click="tableDel" v-if="status == 1 || status == 2">删行</Button>
          <!--            <Button class="actod-btn" v-if="status == 3 && formItems.bcheck == '1'">生单</Button>-->
          <Button class="actod-btn" @click="startDel" v-if="status == 3 && formItems.bcheck != '1'">
            删除
          </Button>
          <Button class="actod-btn" @click="startReview(true)"
                  v-if="status == 3 && formItems.bcheck != '1'">审核
          </button>
          <Button class="actod-btn" @click="startReview(false)"
                  v-if="status == 3 && formItems.bcheck == '1'">弃审
          </button>

          <Popover placement="bottom">
            <template #content>
              <span class="group-btn-span-special">&nbsp;复制&emsp;&emsp;</span><br/>
              <span class="group-btn-span-special">&nbsp;关闭&emsp;&emsp;</span><br/>
              <span class="group-btn-span-special">&nbsp;打开&emsp;&emsp;</span><br/>
              <span class="group-btn-span-special">&nbsp;导入&emsp;&emsp;</span><br/>
              <span class="group-btn-span-special">&nbsp;导出&emsp;&emsp;</span><br/>
            </template>
            <Button class="actod-btn" v-if="status == 3">更多</Button>
          </Popover>
          <Button v-if="status != 3" class="actod-btn">复制</Button>
          <Button class="actod-btn actod-btn-last" @click="outBefore">退出</Button>

        </div>
        <div :class="status != 3?'status-look':''">
          <div class="acttd-right-d-search">
            <InputSearch
              class="acttdrd-search-input"
              placeholder="存货编码或名称"
              @search="pageSearch"
              v-model:value="pageParameter.searchConditon.value"
              style="border-left:1px solid #c9c9c9;"
            />
          </div>
          <div class="acttd-right-d-btns">
            <Button class="acttdrd-btn" @click="pageReload()">
              <SyncOutlined :style="{ fontSize: '14px' }"/>
            </Button>
            <Popover placement="bottom" v-model:visible="visible">
              <template #content>
                <Button style="width: 120px;margin-bottom: 2px" @click="openSetting">表头栏目设置</Button>
                <br/>
                <DynamicColumn :defaultData="initDynamics()['DATA']" :dynamicData="dynamicColumnModel" :lanmuInfo="lanMuData" @reload="reloadColumns"/>
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
            </Popover>
<!--            <Popover placement="bottom">
              <template #content>
                <span class="group-btn-span-special2" @click="titleValue = 0;contentSwitch('tail')"
                      :style="titleValue===0?{backgroundColor: '#0096c7',color: 'white',width:'170px'}:{width:'170px'}">
                &nbsp;蓝字单据&ensp;<CheckOutlined v-if="titleValue===0"/></span><br/>
                <span class="group-btn-span-special2" @click="titleValue = 1;contentSwitch('tail')"
                      :style="titleValue===1?{backgroundColor: '#0096c7',color: 'white',width:'170px'}:{width:'170px'}">
                &nbsp;红字单据&ensp;<CheckOutlined v-if="titleValue===1"/></span>
              </template>
              <Button class="acttdrd-btn">
                <PicLeftOutlined :style="{ fontSize: '14px' }"/>
              </Button>
            </Popover>-->
          </div>
        </div>
      </div>


    </div>
    <div class="app-container-bottom" :style="{height: (windowHeight+70)+'px'}">
      <div class="acb-head">
        <div class="acbgead-one">
          <div class="acbgead-one-triangle">
            <div></div>
            <div>
              <span
                style="color: white;">{{
                  status == 0 ? '暂存' : status == 1 ? '新增' : status == 2 ? '编辑' : '查看'
                }}</span>
            </div>
          </div>
          <div class="acbgead-one-changes" :class="status == 1 || status == 2 ?'status-look':''">
            <VerticalRightOutlined @click="contentSwitch('first')"/>&nbsp;
            <LeftOutlined @click="contentSwitch('prev')"/>&nbsp;
            <RightOutlined @click="contentSwitch('next')"/>&nbsp;
            <VerticalLeftOutlined @click="contentSwitch('tail')"/>
            <span v-if="status=='3'" class="anticon" style="margin-left: 1em">
              <Tag v-if="formItems.bcheck=='1'" :color="'volcano' ">
                已审核
              </Tag>
            </span>
          </div>
          <span style="font-size: 22px;font-weight: bold;"
                :style="{color: titleValue===0?'#0096c7':'red'}">{{
              titleContents[titleValue]
            }}</span>
          <span style="display: inline-block;width: 280px;"></span>
        </div>
        <div class="acbgead-two" :style="status == 3?{ pointerEvents: 'none'}:{}">
          <DynamicForm :datasource="dynamicFormModel" :formDataFun="formFuns"
                       :accId="dynamicTenantId" @open="openHeadSelectContent"/>
        </div>
      </div>
      <div class="acb-centent">
        <!--  针对过滤框显示添加的内容高度 :class="status == 3?'status-look':''"  -->
        <!--       :rowKey="r=>r.assetsCode"-->
        <BasicTable
          ref="tableRef"
          :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
          :clickToRowSelect="false"
          :loading="loadMark"
          :row-selection="{ type: 'checkbox',selectedRowKeys: tableSelectedRowKeys, onChange: onSelectChange }"
          :scroll="{ x: totalColumnWidth,y: windowHeight-(100 + (formRowNum*39))}"
          size="small"
          @register="registerTable"
        >
          <template #cwhcode="{ record }">
            <template v-if="record.editCwhcode">
              <Select
                v-model:value="record.tempCwhcode"
                :default-active-first-option="false"
                :filter-option="false"
                :not-found-content="null"
                :options="ckListOptions.map(it=>({value: it.id,label: it.ckName}))"
                :show-arrow="false"
                show-search
                style="width: 82%;"
                class="cwhcode"
                @keyup.enter="focusNext(record,'cwhcode')"
              ></Select>
              <SearchOutlined @click="openSelectContent(record,'cwhcode')"/>
            </template>
            <template v-else>
              <div @click="record.tempCwhcode=record.cwhcode,record.editCwhcode = true;"
                   :class="status == 1 || status == 2?'suspended-div':'status-look'">
                <span>{{ record.cwhcodeText }}</span>
              </div>
            </template>
          </template>
          <template #cinvode="{ record }">
            <template v-if="record.editCinvode">
              <Select
                v-model:value="record.tempCinvode"
                show-search
                class="cinvode"
                style="width: 82%;"
                :default-active-first-option="false"
                :show-arrow="false"
                :filter-option="false"
                :not-found-content="null"
                :options="cardListOptions"
                @change="value=>handleChange(value,'one')"
                @search="value=>handleSearch(value,'one')"
                @keyup.enter="chFocus = 'one';focusNext(record,'cinvode')"
              ></Select>
              <SearchOutlined @click="openSelectContent(record,'cinvode')"/>
            </template>
            <template v-else>
              <div :class="status == 1 || status == 2?'suspended-div':'status-look'"
                   @click="record.tempCinvode=record.cinvode,record.editCinvode = true;">
                <span class="a-table-font-arial">{{ record.cinvode }}</span>
              </div>
            </template>
          </template>
          <template #cinvName="{ record }">
            <template v-if="record.editCinvName">
              <Select
                v-model:value="record.tempCinvName"
                show-search
                style="width: 82%;"
                :default-active-first-option="false"
                :show-arrow="false"
                :filter-option="false"
                :not-found-content="null"
                :options="cardListOptions"
                @change="value=>handleChange(value,'two')"
                @search="value=>handleSearch(value,'two')"
                @keyup.enter="chFocus = 'two';focusNext(record,'cinvode')"
              ></Select>
              <SearchOutlined @click="openSelectContent(record,'cinvode')"/>
            </template>
            <template v-else>
              <div :class="status == 1 || status == 2?'suspended-div':'status-look'"
                   @click="record.tempCinvName=record.cinvName,record.editCinvName = true;">
                <span class="a-table-font-arial">{{ record?.cinvName }}</span>
              </div>
            </template>
          </template>
          <template #tiaoxinma="{ record }">
            <template v-if="record.editTiaoxinma">
              <Select
                v-model:value="record.tempTiaoxinma"
                show-search
                style="width: 82%;"
                :default-active-first-option="false"
                :show-arrow="false"
                :filter-option="false"
                :not-found-content="null"
                :options="cardListOptions"
                @change="value=>handleChange(value,'three')"
                @search="value=>handleSearch(value,'three')"
                @keyup.enter="chFocus = 'three';focusNext(record,'cinvode')"
              ></Select>
              <SearchOutlined @click="openSelectContent(record,'cinvode')"/>
            </template>
            <template v-else>
              <div :class="status == 1 || status == 2?'suspended-div':'status-look'"
                   @click="record.tempTiaoxinma=record.tiaoxinma,record.editTiaoxinma = true;">
                <span class="a-table-font-arial">{{ record?.tiaoxinma }}</span>
              </div>
            </template>
          </template>
          <template #unitId="{ record }">
            <template v-if="record?.editUnitId ">
              <Select v-model:value="record.tempUnitId"
                      class="unitId"
                      :options="record.unitList"
                      style="width: 82%;"
                      @keyup.enter="focusNext(record,'unitId')"/>
              <CheckOutlined @click="record.editUnitId = null;record.unitId=record.tempUnitId;tableDataChange(record,'unitId')"/>
            </template>
            <template v-else>
              <div :class="status == 1 || status == 2?'suspended-div':'status-look'" @click="record.tempUnitId=record.unitId,record.oldXsUnitId=record.unitId,record.editUnitId = true;">
                <span>{{cunitFormat(record.unitList,record.unitId)}}</span>
              </div>
            </template>
          </template>
          <template #isum="{ record }">
            <template v-if="record?.editIsum">
              <InputNumber v-model:value="record.tempIsum"
                           class="isum"
                           :formatter="value => `${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')"
                           :parser="value => value.replace(/\$\s?|(,*)/g, '')"
                           style="width: 82%;"
                           @keyup.enter="focusNext(record,'isum')"/>
              <CheckOutlined @click="record.editIsum = null;record.isum=record.tempIsum;tableDataChange(record,'isum')"/>
            </template>
            <template v-else>
              <div :class="status == 1 || status == 2?'suspended-div':'status-look'"
                   @click="record.tempIsum=record.isum,record.beseOld=record.isum,record.editIsum = true;">
                    <span class="a-table-font-arial">{{
                        (record.isum == null ? '' : parseFloat(record.isum).toFixed(2) + '').replace(/\B(?=(\d{3})+(?!\d))/g, ',')
                      }}</span>
              </div>
            </template>
          </template>
          <template #unitIdZhu="{ record }">
            <div>
              <span>{{cunitFormat(record.unitList,record.unitIdZhu)}}</span>
            </div>
          </template>
          <template #cunitidF1="{ record }">
            <div v-if="record.cunitType != '1'">
              <span>{{cunitFormat(record.unitList,record.cunitidF1)}}</span>
            </div>
          </template>
          <template #cunitidF2="{ record }">
            <div v-if="record.cunitType != '1'">
              <span>{{cunitFormat(record.unitList,record.cunitidF2)}}</span>
            </div>
          </template>
          <template #isumZhu="{ record }">
            <template v-if="record?.editSix">
              <InputNumber v-model:value="record.tempSix"
                           class="isumZhu"
                           :formatter="value => `${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')"
                           :parser="value => value.replace(/\$\s?|(,*)/g, '')"
                           style="width: 82%;"
                           @keyup.enter="/*tableDataChange(record);*/focusNext(record,'isumZhu')"/>
              <CheckOutlined
                @click="record.editSix = null;record.isumZhu=record.tempSix;tableDataChange(record,'isumZhu')"/>
            </template>
            <template v-else>

              <!--              <div :class="status == 1 || status == 2?'suspended-div':'status-look'"
                                 @click="record.tempSix=record.isumZhu,record.editSix = true;">-->
              <div>
                    <span class="a-table-font-arial">{{
                        (record.isumZhu == null ? '' : parseFloat(record.isumZhu).toFixed(2) + '').replace(/\B(?=(\d{3})+(?!\d))/g, ',')
                      }}</span>
              </div>
            </template>
          </template>
          <template #subQuantity1="{ record }">
            <!--            <template v-if="record?.editSeven">
                          <InputNumber v-model:value="record.tempSeven" class="subQuantity1"
                                       :formatter="value => `${value}`.replace(/[^\d]/g, '')"
                                       style="width: 82%;"
                                       @keyup.enter="record.editSeven = null;record.subQuantity1=record.tempSeven;"/>
                          <CheckOutlined
                            @click="focusNext(record,'subQuantity1');"/>
                        </template>
                        <template v-else>
                          <div :class="status == 1 || status == 2?'suspended-div':'status-look'"
                               @click="record.tempSeven=record.subQuantity1,record.editSeven = true;">
                                <span class="a-table-font-arial">{{
                                    (record.subQuantity1 == null ? '' : record.subQuantity1 + '').replace(/[^\d]/g, '')
                                  }}
                         </span>
                          </div>
                        </template>-->
            <span class="a-table-font-arial">{{
                (record.subQuantity1 == null || record.subQuantity1 == 0 ? '' : parseFloat(record.subQuantity1).toFixed(2) + '').replace(/\B(?=(\d{3})+(?!\d))/g, ',')
              }}
             </span>
          </template>
          <template #subQuantity2="{ record }">

            <span class="a-table-font-arial">{{
                (record.subQuantity2 == null || record.subQuantity2 == 0 ? '' : parseFloat(record.subQuantity2).toFixed(2) + '').replace(/\B(?=(\d{3})+(?!\d))/g, ',')
              }}
             </span>
          </template>
          <template #explain="{ record }">
            <template v-if="record?.editExplain">
              <Input v-model:value="record.tempExplain"
                     style="width: 82%;" class="explain"
                     @keyup.enter="focusNext(record,'explain')"/>
              <CheckOutlined @click="record.editExplain = null;record.explain=record.tempExplain;"/>
            </template>
            <template v-else>
              <div :class="status == 1 || status == 2?'suspended-div':'status-look'"
                   @click="record.tempExplain=record.explain,record.editExplain = true;">
                <span class="a-table-font-arial">{{ record.explain }}</span>
              </div>
            </template>
          </template>
          <template #bcheck="{ record }">
            {{ formatUniqueOperator(record.bcheckUser)}}
          </template>


          <template #gxId="{ record }">
            <template v-if="record?.editGxId">
              <Input v-model:value="record.tempGxId"
                     style="width: 82%;" class="gxId"
                     @keyup.enter="focusNext(record,'gxId')"/>
              <CheckOutlined @click="record.editGxId = null;record.gxId=record.tempGxId;"/>
            </template>
            <template v-else>
              <div :class="status == 1 || status == 2?'suspended-div':'status-look'"
                   @click="record.tempGxId=record.gxId,record.editGxId = true;">
                <span class="a-table-font-arial">{{ record.gxId }}</span>
              </div>
            </template>
          </template>

          <template #sunhaoLv="{ record }">
            <template v-if="record?.editSunhaoLv">
              <InputNumber v-model:value="record.tempSunhaoLv"
                           class="sunhaoLv"
                           :formatter="value => `${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')"
                           :parser="value => value.replace(/\$\s?|(,*)/g, '')"
                           :min="0" :max="100"
                           style="width: 82%;"
                           @keyup.enter="focusNext(record,'sunhaoLv')"/>
              <CheckOutlined @click="record.editSunhaoLv = null;record.sunhaoLv=record.tempSunhaoLv;tableDataChange(record,'sunhaoLv')"/>
            </template>
            <template v-else>
              <div :class="status == 1 || status == 2?'suspended-div':'status-look'"
                   @click="record.tempSunhaoLv=record.sunhaoLv,record.editSunhaoLv = true;">
                    <span class="a-table-font-arial">{{
                        (record.sunhaoLv == null ? '' : parseFloat(record.sunhaoLv).toFixed(2) + '').replace(/\B(?=(\d{3})+(?!\d))/g, ',')
                      }}</span>
              </div>
            </template>
          </template>

          <template #isGuding="{ record }">
            <template v-if="record?.editIsGuding">
              <Select v-model:value="record.tempIsGuding"
                      class="isGuding"
                      :options="[{value: '1',label: '是'},{value: '0',label: '否'}]"
                      :defaultActiveFirstOption="true"
                      style="width: 82%;"
                      @keyup.enter="focusNext(record,'isGuding')"/>
              <CheckOutlined @click="record.editIsGuding = null;record.isGuding=record.tempIsGuding;tableDataChange(record,'isGuding')"/>
            </template>
            <template v-else>
              <div :class="status == 1 || status == 2?'suspended-div':'status-look'" @click="record.tempIsGuding=record.isGuding,record.editIsGuding = true;">
                <span>{{record.isGuding==='1'?'是':record.isGuding==='0'?'否':''}}</span>
              </div>
            </template>
          </template>

          <template #isKuoZhan="{ record }">
            <template v-if="record?.editIsKuoZhan">
              <Select v-model:value="record.tempIsKuoZhan"
                      class="isKuoZhan"
                      :options="[{value: '1',label: '是'},{value: '0',label: '否'}]"
                      style="width: 82%;"
                      @keyup.enter="focusNext(record,'isKuoZhan')"/>
              <CheckOutlined @click="record.editIsKuoZhan = null;record.isKuoZhan=record.tempIsKuoZhan;tableDataChange(record,'isKuoZhan')"/>
            </template>
            <template v-else>
<!--              <div :class="status == 1 || status == 2?'suspended-div':'status-look'" @click="record.tempIsKuoZhan=record.isKuoZhan,record.editIsKuoZhan = true;">-->
                <div>
              <span>{{record.isKuoZhan==='1'?'是':record.isKuoZhan==='0'?'否':''}}</span>
              </div>
            </template>
          </template>

          <template #bstyle="{ record }">
            <template v-if="record?.editBstyle">
              <Select v-model:value="record.tempBstyle"
                      class="bstyle"
                      :options="gongYingOptions"
                      :defaultActiveFirstOption="true"
                      style="width: 82%;"
                      @keyup.enter="focusNext(record,'bstyle')"/>
              <CheckOutlined @click="record.editBstyle = null;record.bstyle=record.tempBstyle;tableDataChange(record,'bstyle')"/>
            </template>
            <template v-else>
              <div :class="status == 1 || status == 2?'suspended-div':'status-look'" @click="record.tempBstyle=record.bstyle,record.editBstyle = true;">
                <span>{{gongYingOptions.filter(it=>it.value === record.bstyle)[0]?.label }}</span>
              </div>
            </template>
          </template>

          <template #cvencodeCg="{ record }">
            <template v-if="record?.editCvencodeCg">
              <Select v-model:value="record.tempCvencodeCg"
                      class="cvencodeCg"
                      :options="custList"
                      style="width: 82%;"
                      @keyup.enter="focusNext(record,'cvencodeCg')"/>
              <CheckOutlined @click="record.editCvencodeCg = null;record.cvencodeCg=record.tempCvencodeCg;tableDataChange(record,'cvencodeCg')"/>
            </template>
            <template v-else>
              <div :class="status == 1 || status == 2?'suspended-div':'status-look'" @click="record.tempCvencodeCg=record.cvencodeCg,record.editCvencodeCg = true;">
                <span> {{formatUniqueCvencoe(record.cvencodeCg)}}</span>
              </div>
            </template>
          </template>


          <template #cdepcode="{ record }">
            <template v-if="record?.editCdepcode">
              <Select v-model:value="record.tempCdepcode"
                      class="cdepcode"
                      :options="deptList"
                      style="width: 82%;"
                      @keyup.enter="focusNext(record,'cdepcode')"/>
              <CheckOutlined @click="record.editCdepcode = null;record.cdepcode=record.tempCdepcode;tableDataChange(record,'cdepcode')"/>
            </template>
            <template v-else>
              <div :class="status == 1 || status == 2?'suspended-div':'status-look'" @click="record.tempCdepcode=record.cdepcode,record.editCdepcode = true;">
                <span> {{formatUniqueDept(record.cdepcode)}}</span>
              </div>
            </template>
          </template>

          <template #summary>
            <TableSummary fixed>
              <TableSummaryRow style="background-color: #cccccc;font-weight: bold;">
                <TableSummaryCell class="nc-summary" :index="0" :align="'center'">合</TableSummaryCell>
                <TableSummaryCell class="nc-summary" :index="1" :align="'center'">计</TableSummaryCell>
                <TableSummaryCell class="nc-summary" v-for="cell in getCurrSummary()"  :index="cell.ind" :align="cell.align"><span class="a-table-font-arial">{{null == summaryModel[cell.dataIndex]?'':summaryModel[cell.dataIndex].toFixed(2)}}</span></TableSummaryCell>
              </TableSummaryRow>
            </TableSummary>
          </template>
        </BasicTable>
        <Import
          @save="introduceData"
          @register="registerImportPage"
        />
        <Query
          @query="loadPage"
          @register="registerQueryPage"
        />
      </div>
    </div>
    <SupperModalPop @throwData="modalData" @register="registerModalPopPage"/>
    <DeptModalPop @register="registerSelectDeptPage" @save="modalData"/>
    <StockCangKuModalPop @register="registerStockCangKuModalPage" @throwData="modalData"/>
    <SelectPsn @register="registerSelectPsnPage" @save="modalData"/>
    <StockInfiModalPop @register="registerStockInfoModalPage" @throwData="modalData"/>
    <BatchSelector @register="registerBatchSelectorPage" @throwData="modalData"/>
  </div>
</template>

<script setup="props, {emit}" lang="ts">
import {
  Button,
  Input,
  InputNumber,
  message,
  Popover,
  Radio,
  Select,
  Switch,
  Table,
  Tabs,
  Tag
} from "ant-design-vue";
import Query from "./popup/query.vue";
// import BatchSelector from "/@/views/boozsoft/stock/stock_sales_add/component/BatchNumberSelector.vue";
const BatchNumberSelector = null;
import Import from "./popup/import.vue";
import DynamicForm from './component/DynamicForm.vue';
import {
  CheckOutlined,
  LeftOutlined,
  RightOutlined,
  SearchOutlined,
  SettingFilled,
  SortAscendingOutlined,
  SortDescendingOutlined,
  SyncOutlined,
  VerticalLeftOutlined,
  VerticalRightOutlined,CopyOutlined
} from '@ant-design/icons-vue';
import {getCurrentInstance, nextTick, provide, reactive, ref} from "vue";
import {useMessage} from "/@/hooks/web/useMessage";
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker-DATA.vue";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {useTabs} from "/@/hooks/web/useTabs";
import router from "/@/router";
import {JsonTool, NumberTool, ObjTool, uuid} from "/@/api/task-api/tools/universal-tools";
import {BasicTable, useTable} from '/@/components/Table'
import {useModal} from '/@/components/Modal'
import {findFaAssetInfoByCode} from "/@/api/fa/fa-assets-minus";
import DynamicColumn from "/@/views/boozsoft/stock/stock_sales_add/component/DynamicColumn.vue";
import {useUserStoreWidthOut} from "/@/store/modules/user";
import {useRoute} from "vue-router";
/**********************栏目设置**********************/
import {initDynamics} from "./data";
import {assemblyDynamicColumn} from "/@/views/boozsoft/stock/stock_sales_add/component/DynamicColumn";
import {hasBlank} from "/@/api/task-api/tast-bus-api";
import {usePlatformsStore} from "/@/store/modules/platforms";
import {GenerateDynamicColumn} from "./component/DynamicForm";
// import SupperModalPop from "/@/views/boozsoft/global/popup/supplier/modalPop.vue";
// import DeptModalPop from "/@/views/boozsoft/global/popup/dept/select-dept.vue";
//
// import StockCangKuModalPop from "/@/views/boozsoft/stock/stock_cangku/popup/stockCangKuModalPop.vue";
// import SelectPsn from "/@/views/boozsoft/global/popup/dept/select-psn.vue";
const SelectPsn = null;
const DeptModalPop = null;
const StockCangKuModalPop = null;
const SupperModalPop = null;
import StockInfiModalPop from "/@/views/boozsoft/stock/stock_info/popup/stockInfoModalPop.vue";
import {findAll as findAllJiLang, findUnitInfoList} from "/@/api/record/system/unit-mea";
import {findCunHuoAllList} from "/@/api/record/stock/stock-caigou";
import {
  findBillCode,
  findBillLastDate,
} from "/@/api/record/stock/stock-xhd";
import {useCompanyOperateStoreWidthOut} from "/@/store/modules/operate-company";
import { findStockPeriodInfoByYm} from "/@/api/record/group/im-unit";
import {findAll as findAllPrice} from "/@/api/record/stock/stock-price";
import {
  getByStockBalanceTask, stockBalanceTaskEditNewTime, stockBalanceTaskSave,
  stockTaskDelById,
  updateStockCurrentRevision
} from "/@/api/record/stock/stock_balance";
// import {useNcModals} from "/@/views/boozsoft/stock/stock_out_add/otherServerReferences";
const useNcModals = null;
import {changeCust} from "/@/views/boozsoft/stock/stock_sales_add/component/DynamicForm";
import {saveLog} from "/@/api/record/system/group-sys-login-log";
import {
  bomDel,
  bomSave,
  bomVerify,isBomVerify,
  findBillByCondition,
  reviewReceipt
} from "/@/api/record/stock/stock-bom";

const RadioButton = Radio.Button
const RadioGroup = Radio.Group
const InputSearch = Input.Search
const SelectOption = Select.Option
const TabPane = Tabs.TabPane
const TableSummary = Table.Summary
const TableSummaryRow = Table.Summary.Row
const TableSummaryCell = Table.Summary.Cell
const {createErrorModal, createConfirm, createWarningModal} = useMessage()
const {closeCurrent} = useTabs(router);
const [registerQueryPage, {openModal: openQueryPageM}] = useModal()
const [registerImportPage, {openModal: openImportPageM}] = useModal()
const windowHeight = (window.innerHeight - 300)
const dynamicTenantId = ref('')
const dynamicTenant = ref(null)
const dynamicAccId = ref('')
const dynamicYear = ref('')
const titleContents = ['BOM物料清单','BOM物料清单']
const titleValue = ref(0)
const formRowNum = ref(1)
const ymPeriod:any = ref(false)
const pageParameter = reactive({
  showRulesSize: 'MIN',
  searchConditon: {
    requirement: '',
    value: '',
  },
  type: 'XSDD'
})
const gongYingOptions = [{value: '1',label: '领用'},{value: '2',label: '直接供应'},{value: '3',label: '虚拟件'},{value: '4',label: '入库倒冲'},{value: '5',label: '工序倒冲'}]
const formItems: any = ref({})
const summaryModel: any = ref({})
const jiList = ref([])
const manyJiList = ref([])
const ckListOptions = ref([])
const operatorList = ref([])
const custList = ref([])
const deptList = ref([])
const cardListOptions = ref([])
const assetsCardList = ref([])
const stockPriceList = ref([])
const searchList = ref([])
const busDate = useCompanyOperateStoreWidthOut().getLoginDate;
let num = 0
const dynamicAdReload = async (obj) => {
  num+=1
  dynamicTenant.value = obj
  let c = false
  if ( dynamicTenantId.value != obj.accountMode) c=true
  dynamicTenantId.value = obj.accountMode
  dynamicAccId.value = obj.accId
  lanMuData.value.accId = dynamicAccId.value
  dynamicYear.value = busDate.substring(0,4)
  visible.value = true
  setTimeout(()=>
      visible.value = false
    ,100)
    await columnReload()
    await reloadList()
    lanMuData.value.changeNumber+=1
}
const dynamicFormModel = ref([])
const formFuns = ref({
  getFormValue: () => {
  }, setFormValue: () => {
  }, getSelectMap: () => {
  }
})

const accountPickerFuns = ref({
   resetCoCode: (v) => {}
})
const columnReload = async () => {
  dynamicFormModel.value = await GenerateDynamicColumn(dynamicTenantId.value) || []
  formRowNum.value = Math.ceil((dynamicFormModel.value.filter(it=>it.isShow).length/4))-1
}
const routeData:any = useRoute().query;
const pageReload = async () => {
  if(routeData.type!==undefined){
    if (!hasBlank(routeData.co) && dynamicTenant.value?.coCode !=routeData.co){
      accountPickerFuns.value.resetCoCode(routeData.co)
      return false
    }
    if(routeData.type=='add'){
      await startEdit('add')
    }else if(routeData.type=='edit'){
      status.value=2
      await contentSwitch('curr')
    }else{
      await contentSwitch('curr')
    }
  }else{
    await contentSwitch(formItems.value.id == null?'tail':'curr')
  }
}



async function reloadList() {
  jiList.value = await useRouteApi(findAllJiLang, {schemaName: dynamicTenantId})({unitName: ''})
  manyJiList.value = await useRouteApi(findUnitInfoList, {schemaName: dynamicTenantId})({})
  assetsCardList.value = (await useRouteApi(findCunHuoAllList, {schemaName: dynamicTenantId})({date:busDate}))
  nextTick(()=>{
    ckListOptions.value = formFuns.value.getSelectMap()['warehouse']
    operatorList.value = formFuns.value.getSelectMap()['operator']
    custList.value = formFuns.value.getSelectMap()['supplier']
    deptList.value = formFuns.value.getSelectMap()['dept']
  })
}

async function contentSwitch(action) {
  loadMark.value = true
  let res = await useRouteApi(findBillByCondition, {schemaName: dynamicTenantId})({
    action: action,
    curr: routeData?.code != null?routeData.code:( formFuns.value.getFormValue()?.bomSysId || ''),
  })
  if (null != res) {
    formItems.value = JsonTool.parseProxy(res)
    res.entryList = null
    formFuns.value.setFormValue(res)
    if (!hasBlank(formItems.value.entryList)) {
      let list = JsonTool.parseObj(formItems.value.entryList).map(it => resetRow(it))
      let len = list.length
      for (let i = 0; i < (25 - len); i++) {
        list.push({})
      }
      setTableData(list)
    }else {
      let list = []
      for (let i = 0; i < (25); i++) {
        list.push({})
      }
      setTableData(list)
    }
    pageParameter.searchConditon.value = ''
    searchList.value = []
    formItems.value.entryList = null
  } else {
    message.success('暂无数据！')
    formItems.value = {}
    formFuns.value.setFormValue({})
    let list = []
    for (let i = 0; i < (25); i++) {
      list.push({})
    }
    setTableData(list)
  }
  calculateTotal()
  loadMark.value = false
}

// 0 暂存 1 新建 2编辑 3查看
const status = ref(3)

const CrudApi = {
  columns: [
    {
      title: '工序号',
      dataIndex: 'gxId',
      ellipsis: true,
      slots: {customRender: 'gxId'},
    },
    {
      title: '条形码',
      dataIndex: 'tiaoxinma',
      ellipsis: true,
      slots: {customRender: 'tiaoxinma'},
    },
    {
      title: '存货编码',
      dataIndex: 'cinvode',
      ellipsis: true,
      slots: {customRender: 'cinvode'},
    },
    {
      title: '存货名称',
      dataIndex: 'cinvName',
      ellipsis: true,
      slots: {customRender: 'cinvName'},
    },
    {
      title: '规格型号',
      dataIndex: 'guigexh',
      ellipsis: true,
      slots: {customRender: 'guigexh'},
    },{
      title: '计量单位',
      dataIndex: 'unitId',
      ellipsis: true,
      slots: {customRender: 'unitId'},
    },{
      title: '数量',
      dataIndex: 'isum',
      slots: {customRender: 'isum'},
      ellipsis: true,
    },
    {
      title: '主计量',
      dataIndex: 'unitIdZhu',
      ellipsis: true,
      align: 'left',
      slots: {customRender: 'unitIdZhu'},
    },{
      title: '主数量',
      dataIndex: 'isumZhu',
      slots: {customRender: 'isumZhu'},
      ellipsis: true,
    },

    {
      title: '是否固定数量',
      dataIndex: 'isGuding',
      ellipsis: true,
      slots: {customRender: 'isGuding'},
    },
    {
      title: '损耗率',
      dataIndex: 'sunhaoLv',
      ellipsis: true,
      slots: {customRender: 'sunhaoLv'},
    },
    {
      title: '供应类型',
      dataIndex: 'bstyle',
      ellipsis: true,
      slots: {customRender: 'bstyle'},
    },
    {
      title: 'BOM子件扩展',
      dataIndex: 'isKuoZhan',
      ellipsis: true,
      slots: {customRender: 'isKuoZhan'},
    },{
      title: '默认供应商',
      dataIndex: 'cvencodeCg',
      ellipsis: true,
      slots: {customRender: 'cvencodeCg'},
    },{
      title: '默认仓库',
      dataIndex: 'cwhcode',
      ellipsis: true,
      slots: {customRender: 'cwhcode'},
    },{
      title: '默认领料部门',
      dataIndex: 'cdepcode',
      ellipsis: true,
      slots: {customRender: 'cdepcode'},
    },
    {
      title: '备注',
      dataIndex: 'explain',
      ellipsis: true,
      slots: {customRender: 'explain'},
    }
  ]
}
// 这是示例组件
const [registerTable, {
  reload,
  getDataSource,
  setTableData,
  setPagination,
  getPaginationRef,
  getColumns,
  setColumns
}] = useTable({
  columns: CrudApi.columns,
  bordered: true,
  showIndexColumn: true,
  indexColumnProps:{ fixed:true },
  pagination: {
    pageSize: 50,
    hideOnSinglePage: true,
    showSizeChanger: true,
    pageSizeOptions: ['30', '50', '100'],
    showTotal: t => `总共${t}条数据`
  }
})

const tableSelectedRowKeys = ref([])
const tableSelectedRowObjs = ref([])
const onSelectChange = (k, o) => {
  tableSelectedRowKeys.value = k
  tableSelectedRowObjs.value = o
}

const openQpage = async () => {
  if (true) {
    openQueryPageM(true, {schemaName: dynamicTenantId.value})
  } else {
    createErrorModal({
      iconType: 'warning',
      title: '温馨提示',
      content: '请先给该公司创建固定资产账！'
    })
  }
}

const tableAdd = () => {
  if (tableSelectedRowKeys.value.length == 1) {
    let list = getDataSource()
    let selectIndex = list.findIndex(it => it.key === tableSelectedRowKeys.value[0])
    list.splice(selectIndex, 0, {})
    setTableData(list)
    tableSelectedRowKeys.value = []
  } else {
    createErrorModal({
      iconType: 'warning',
      title: '温馨提示',
      content: '请选择插入行次！'
    })
  }
}
const tableDel = () => {
  if (tableSelectedRowKeys.value.length == 1) {
    let list = getDataSource()
    let selectIndex = list.findIndex(it => it.key === tableSelectedRowKeys.value[0])
    list.splice(selectIndex, 1)
    setTableData(list)
    tableSelectedRowKeys.value = []
  } else {
    createErrorModal({
      iconType: 'warning',
      title: '温馨提示',
      content: '请选择删除行次！'
    })
  }
}
async function checkBusDate(date) {
  let dates = date.split('-')
  let list = await findStockPeriodInfoByYm({code:dynamicTenant.value.target.id,date: date})
  formItems.value.ddate = date
  if (list.length == 0){
    createWarningModal({title: "温馨提示",content: '当年度暂未创建，不能新增销售出库单，请先创建该年度'})
    return false;
  }else {
    if (list.filter(it=>it.istock == '1').length == list.length){
      createWarningModal({title: "温馨提示",content: '当年度已经全部结账，不能新增销售出库单，请切换公司代码或年度'})
      return false;
    }else if(list.filter(it=>it.stockMonth == dates[1] && it.istock == '1').length > 0){ // 本年月结账
      // 得到当年最后一张销售出库单日期
      let n =  (await useRouteApi(findBillLastDate, {schemaName: dynamicTenantId})({year: dates[0],type:"XHD"}))
      formItems.value.ddate = n
    }
  }
  return true;
}
/**
 * 检查当前账套业务期间 是否已经结账 或者 是否正在结账
 */
async function checkPeriod(){
  if (ymPeriod.value){
    createWarningModal({title: "温馨提示",content: '当前业务日期期间已经结账，请重新选择单据日期！'})
    return true;
  }
  let taskData= await useRouteApi(getByStockBalanceTask, { schemaName: dynamicTenantId })({iyear:dynamicYear.value,name:'月末结账',method:'结账',recordNum:''})
  if (!hasBlank(taskData)){
    createWarningModal({title: '温馨提示',content: '操作员'+taskData[0].username+'正在对当前账套进行月末结账处理，不能进行对单据业务操作，请销后再试！'})
    return  true;
  }
  return false
}
const startEdit = async (type) => {
  // if (await checkPeriod())return false;
  let maxR = 20
  if (type === 'add') {
    status.value = 1
    if (false/* await checkBusDate(busDate)*/){
      formItems.value.ccode = await generateCode(busDate) // 生成编码
      formItems.value.xsRate = dynamicTenant.value.target.xsRate || 0 // 获取账套汇率
      formFuns.value.setFormValue({
      })
    }
    formFuns.value.setFormValue({chengpingLv: 100,quantity: 1,sunhaoLv: 0})
    let list = []
    for (let i = 0; i < maxR; i++) {
      list.push({})
    }
    setTableData(list)
     tempTaskSave('新增')
  } else {
    // 任务
    let taskData= await useRouteApi(getByStockBalanceTask, { schemaName: dynamicTenantId })({iyear:dynamicYear.value,name:'BOM物料清单',method:'修改,审核,删除',recordNum:dynamicTenantId.value})
    if(taskData==''){
      tempTaskSave('修改')
    }else{
      // 任务不是当前操作员的
      if(taskData[0].caozuoUnique!==useUserStoreWidthOut().getUserInfo.id){
        return createWarningModal({ content: taskData[0].username+'正在修改销货单,不能同时进行操作！' });
      }else{
        await useRouteApi(stockBalanceTaskEditNewTime, { schemaName: dynamicTenantId })(taskData[0].id)
      }
    }
    status.value = 2
    let list = getDataSource()
    let dLen = list.length
    for (let i = 0; i < (maxR - dLen); i++) {
      list.push({})
    }
    setTableData(list)
  }

}
const taskIds = ref([])
async function tempTaskDel() {
  if(!hasBlank(taskIds.value)){
    taskIds.value.filter(id=>useRouteApi(stockTaskDelById, { schemaName: dynamicTenantId })(id)
      .then((a)=>{taskIds.value=taskIds.value.filter(s=>s != id)}))
  }
}

async function tempTaskSave(method) {
  await useRouteApi(stockBalanceTaskSave, { schemaName: dynamicTenantId })({iyear:dynamicYear.value,userName:useUserStoreWidthOut().getUserInfo.id,functionModule:'BOM物料清单',method:method,recordNum:dynamicTenantId.value,caozuoModule:'stock'})
    .then((a)=>{
      taskIds.value.push(a.id)
    })
}
async function generateCode(date) {
  return await useRouteApi(findBillCode, {schemaName: dynamicTenantId})({
    type: pageParameter.type,
    date: date,
    key: '3-5',
    prefix:''
  })
}


const startDel = async () => {
  if (null == formItems.value || hasBlank(formItems.value.id)) {
    createErrorModal({
      iconType: 'warning',
      title: '温馨提示',
      content: '暂无任何单据！'
    })
  } else {
    let taskData= await useRouteApi(getByStockBalanceTask, { schemaName: dynamicTenantId })({iyear:dynamicYear.value,name:'BOM物料清单',method:'修改,审核,删除',recordNum:dynamicTenantId.value})
    if(taskData==''){
      tempTaskSave('删除')
      createConfirm({
        iconType: 'warning',
        title: '物料清单删除',
        content: '您确定要进行物料清单删除吗!',
        onOk: async () => {
          // 删除前校验
            await useRouteApi(bomDel, {schemaName: dynamicTenantId})({id: formItems.value.id})
            message.success('删除成功！')
            tempTaskDel()
            writeLog(`删除`,formItems.value,null)
            formItems.value.id = ''
            await contentSwitch('tail')
        },onCancel: () => {
          tempTaskDel()
          return false
        }
      });
    }
  }
}

const startReview = async (b) => {
  // if (await checkPeriod())return false;
  let a = useUserStoreWidthOut().getUserInfo.id
  if (!hasBlank(a) && !hasBlank(formItems.value.id)) {
    if ((b && !hasBlank(formItems.value.bcheckUser)) || (!b && hasBlank(formItems.value.bcheckUser))){
      createWarningModal({title: '温馨提示',content: '请勿重复操作！'})
    }else {
      let taskData= await useRouteApi(getByStockBalanceTask, { schemaName: dynamicTenantId })({iyear:dynamicYear.value,name:'BOM物料清单',method:'修改,审核,弃审,删除',recordNum:dynamicTenantId.value})
      if(taskData=='' ){
        tempTaskSave(b?'审核':'弃审')
      }else{
        // 任务不是当前操作员的
        if(taskData[0].caozuoUnique!==useUserStoreWidthOut().getUserInfo.id){
          return createWarningModal({ content: '当前单据正在被操作员'+taskData[0].username+'正在进行'+taskData[0].method+'操作，任务互斥，请销后再试！' });
        }else{
          await useRouteApi(stockBalanceTaskEditNewTime, { schemaName: dynamicTenantId })(taskData[0].id)
        }
      }
      let res = await useRouteApi(reviewReceipt, {schemaName: dynamicTenantId})({
        id: formItems.value.id,
        userId: a,
        type: b,
      })
      message.success(`${b?'审核':'弃审'}成功！`)
      tempTaskDel()
      writeLog(`${b?'审核':'弃审'}`,formItems.value,null)
      await pageReload()
    }
  } else {
    if (hasBlank(a)) message.error('获取用户信息异常！')
  }
}

function codeToName(arr) {
  let names = {'XSCKD':'销售出库单'}
  return arr.map(s=>names[s]).filter(s=>s != null && s != '').join(',')
}

const loadMark = ref(false)

//存货校验
async function stockCheck(list,model) {
  let err = ''
  if (err != ''){
    createWarningModal({title: '温馨提示', content: err})
    return  false;
  }
  return true;
}
//保存验证
async function saveCheck(list) {
  let form = formFuns.value.getFormValue();

  if (hasBlank(form.bomSysId)) {
    createErrorModal({
      iconType: 'warning',
      title: '温馨提示',
      content: 'BOM识别码不能为空！'
    })
    return false
  }

  if (hasBlank(form.bomId)) {
    createErrorModal({
      iconType: 'warning',
      title: '温馨提示',
      content: '父件编码不能为空！'
    })
    return false
  }

  if (hasBlank(form.bomVersion)) {
    createErrorModal({
      iconType: 'warning',
      title: '温馨提示',
      content: '版本号不能为空！'
    })
    return false
  }

  if (hasBlank(form.unitId) || hasBlank(form.quantity)) {
     createErrorModal({
      iconType: 'warning',
      title: '温馨提示',
      content: '计量单位与数量不能为空！'
    })
    return false
  }


  if (list.length == 0 ) {
    createWarningModal({
      title: '温馨提示',
      content: '列表完整内容至少存在一条!存货编码为必选项,主数量、单价和金额为必填项!'
    })
    return false
  }

  // 校验识别码唯一性
  let  mark =  await useRouteApi(bomVerify, {schemaName: dynamicTenantId})(form)
  if ("0" != mark){
    createErrorModal({
      iconType: 'warning',
      title: '温馨提示',
      content: mark=='1'?'BOM标识码已存在,不可重复！':'当前父件下BOM版本号已存在,不可重复！'
    })
    return false
  }
  return true
}

const modelText = ref('');
const modelText1 = ref('');
const modelText2 = ref('');
//数据保存
async function saveData() {
  let id = (status.value == 1?null:formItems.value.id)
  formItems.value = formFuns.value.getFormValue()
  formItems.value.id = id // 制单人
  formItems.value.cmaker = useUserStoreWidthOut().getUserInfo.id // 制单
  let list = getDataSource().filter(it =>  !hasBlank(it.cinvode) && !hasBlank(it.unitIdZhu) && !hasBlank(it.isumZhu) )
  if (await saveCheck(list)) {
    let delKyes = Object.keys(list[0]).filter(i => i.startsWith('edit') || i.startsWith('temp') )
    delKyes.push(...['cwhcodeInfo','unitInfo'])
    list.forEach((o, i) => {  //转化
      o = ObjTool.dels(o,delKyes)
    })
    formItems.value.entryList = JsonTool.json(merge(list))
    await useRouteApi(bomSave, {schemaName: dynamicTenantId})(formItems.value)
    message.success('保存成功！')
    tempTaskDel()
    writeLog(formItems.value?.id == null?'新增':'修改',formItems.value,null)
    await pageReload()
    status.value = 3
  }
}

function merge(list) {
  // 将客户+仓库+存货+批号+生失日期相同的数据合并
  // list.map(it=>it.cvencode+'-'+it.cinvode+'-'+)
  return list
}

async function editStockCurrentRevison(map) {
  if(!hasBlank(map.id)){
    await useRouteApi(updateStockCurrentRevision,{schemaName: dynamicTenantId})(map)
  }
}

async function giveUp() {
  formFuns.value.setFormValue({})
  setTableData([])
  if (status.value == 1) {
    await contentSwitch('first')
  } else if (status.value == 2) {
    await contentSwitch('curr')
  }
  tempTaskDel()
  tableSelectedRowKeys.value = []
  status.value = 3
}

const loadPage = (e) => {
  columnReload()
}
async function importEntry() {
  openImportPageM(true, {
    query: {
      code: pageParameter.managementCode,
      expirationDate: formItems.value.endDate,
      condition: {choose: formItems.value.pdMethod, values: formItems.value.pdRange}
    },
    schemaName: dynamicTenantId.value
  })
}

function introduceData(list) {
  if (list.length > 0) {
    let tables = getDataSource()
    let existeds = tables.map(it => it.assetsCode)
    list.forEach(it => {
      if (existeds.indexOf(it.assetsCode) != -1) {
        tables[tables.findIndex(it2 => it2.assetsCode == it.assetsCode)].realityAmount = it.realityAmount
      } else {
        tables.push(it)
      }
    })
    setTableData(tables)
  }
}


function formatUniqueOperator(uniqueCodeUser) {
  let str = uniqueCodeUser
  if (!hasBlank(str))operatorList.value.forEach(
    function (psn: any) {
      if (psn.id == uniqueCodeUser) {
        str = psn.realName
      }
    }
  )
  return str || ''
}

function formatUniqueCvencoe(uniqueCodeUser) {
  let str = uniqueCodeUser
  if (!hasBlank(str))custList.value.forEach(
    function (psn: any) {
      if (psn.value == uniqueCodeUser) {
        str = psn.label
      }
    }
  )
  return str || ''
}
function formatUniqueDept(uniqueCodeUser) {
  let str = uniqueCodeUser
  if (!hasBlank(str))deptList.value.forEach(
    function (psn: any) {
      if (psn.value == uniqueCodeUser) {
        str = psn.label
      }
    }
  )
  return str || ''
}


const disabledDate = (current) => {
  // 获取区间最小区间
  if (!hasBlank(formItems.value.handleDate)) {
    let variable = formItems.value.handleDate.substring(0, 7)
    let min = moment(variable + '-01', 'YYYY-MM-DD')
    let max = moment(variable, 'YYYY-MM-DD').endOf('month')
    return current < min || current > max
  }
};


const rowDataGrab = async (rowObj, type: string) => {
  let arr = assetsCardList.value.filter(it => (type == 'one' ? (it.sysId == rowObj.sysId) : type == 'two' ? (it.assetsCode == rowObj.assetsCode) : (it.assetsName == rowObj.assetsName)))
  if (arr.length > 0 && rowObj['assetsUniqueCode'] != arr[0].assetsUniqueCode) {
    rowObj['assetsUniqueCode'] = arr[0].assetsUniqueCode
    rowObj['sysId'] = arr[0].sysId
    rowObj['assetsCode'] = arr[0].assetsCode
    rowObj['assetsName'] = arr[0].assetsName
    // 初始化其他参数
    let res = await useRouteApi(findFaAssetInfoByCode, {schemaName: dynamicTenantId})({code: arr[0].assetsUniqueCode}) || []
    if (null != res && res.length > 0) {
      rowObj['speciType'] = res[0].speciType
      rowObj['bookAmount'] = res[0].bookAmount
      rowObj['measureUnit'] = res[0].measureUnit
      rowObj['yuanzhi'] = res[0].yuanzhi
      rowObj['jtBy'] = res[0].jtBy
      rowObj['zjLj'] = res[0].zjLj
      rowObj['jingzhi'] = res[0].jingzhi
    }
    cardListOptions.value = []
  }
}

const handleSearch = async (val: string, type: string) => {
  if (!hasBlank(val)) {
    // 排除已存在到存货
   let headNumer = formFuns.value.getFormValue()['bomId']
    let filterList =[headNumer] // getDataSource().filter(it => !hasBlank(it.cinvode)).map(it => it.cinvode)
    await fetch(val, type, filterList, (d: any[]) => (cardListOptions.value = d));
  }
};
const handleChange = async (val: string, type: string) => {
  // await fetch(val, type,(d: any[]) => (cardListOptions.value = d));
  // 赋值
};
let timeout: any;
let currentValue = '';

async function fetch(value: string, type: string, filterList: any, callback: any) {
  if (timeout) {
    clearTimeout(timeout);
    timeout = null;
  }
  currentValue = value;

  async function fake() {
    /*let qData = {
      date: formFuns.value.getFormValue().ddate,
      type: type,
      qValue: value,
    }
    let res = await useRouteApi(findCunHuoListByCondition, {schemaName: dynamicTenantId})(qData) || []*/
    let k = type == 'one' ? 'stockNum' : type == 'three' ? 'stockBarcode' : 'stockName'
    let res = assetsCardList.value.filter(it => null != it[k] && it[k].indexOf(value) != -1)
    let data = []
    res = res.filter(it => filterList.indexOf(it.stockNum) == -1)
    res.forEach((r: any) => {
      data.push({
        value: r[type == 'one' ? 'stockNum' : type == 'three' ? 'stockBarcode' : 'stockName'],
        label: type == 'three' ? r['stockBarcode'] : (r['stockNum'] + ' ' + r['stockName']),
      });
    });
    callback(data);
  }
  timeout = setTimeout(fake, 300);
}

const chFocus = ref('one')
const mark = usePlatformsStore().getCurrentPlatformId
const visible = ref(false);
const windowWidth = (window.innerWidth - (70/*+280*/))

const totalColumnWidth = ref(0)

const tableRef = ref(null)
const dynamicColumnModel = ref({value:[]})
const lanMuData = ref({
  'accId': 'postgres',
  'menuName': '销售订单新增',
  'type': '列表',
  objects: '',
  username: useUserStoreWidthOut().getUserInfo.username,changeNumber:0
})

const reloadColumns = () => {
  let newA = JSON.parse(JSON.stringify(CrudApi.columns))
  newA = assemblyDynamicColumn(dynamicColumnModel.value.value, newA)
  setColumns(newA)
  initTableWidth(newA)
  pageReload()
}

function initTableWidth(thisCs) {
  let total = 60
  thisCs.forEach(item => {
    if (item.ifShow == null || item.ifShow)
      total += parseInt(item.width)
  })
  if (total > windowWidth) {
    let f = 0
    totalColumnWidth.value = Number(windowWidth) - f
    tableRef.value.$el.style.setProperty('width', (windowWidth + 40 - f) + 'px')
  } else {
    totalColumnWidth.value = total
    tableRef.value.$el.style.setProperty('width', (total + 40) + 'px')
  }
}

const openSetting = () => {
  openQueryPageM(true, {schemaName: dynamicTenantId.value})
}
/*栏目设置end*/
const formEtcData = ref({
  makerMan: '',
  auditMan: '',
})
const thisEditObj = ref(null)
const thisEditType = ref('')
const [registerModalPopPage, {openModal: openMoalPopPage}] = useModal();
const [registerSelectDeptPage, {openModal: openSelectDeptPage}] = useModal()
const [registerStockCangKuModalPage, {openModal: openStockCangKuModalPage}] = useModal();
const [registerSelectPsnPage, {openModal: openSelectPsnPage}] = useModal()


const [registerStockInfoModalPage, {openModal: openStockInfoModalPage}] = useModal();
const [registerBatchSelectorPage, {openModal: opneBatchSelectorPage}] = useModal();
const openSelectContent = (o, type) => {
  thisEditObj.value = o
  thisEditType.value = type
  switch (type) {
    case 'cinvode':
      openStockInfoModalPage(true, {
        database: dynamicTenantId.value,
      })
      break;
    case 'cwhcode':
      let data = formFuns.value.getFormValue()
      openStockCangKuModalPage(true, {
        database: dynamicTenantId.value,
        ckIsDuli: null != o ? o['cangkuDuli'] : null,
        entity: null != o ? ckListOptions.value.filter(it => it.id == data.cwhcode)[0] : null
      })
      break;
  }
}
/*const {openCustomModal,openDeptModal,openPsnModal} =  useNcModals()*/
const openHeadSelectContent = (type) => {
  thisEditType.value = type
  if (status.value == 3) return false
/*  switch (type) {
    case 'cvencode':
      openCustomModal(dynamicTenant.value).then(res=>modalData(res))
      break;
    case 'cdepcode':
      openDeptModal(dynamicTenant.value).then(res=>modalData(res))
      break;
    case 'cpersoncode':
    case 'deliveryUser':
      openPsnModal(dynamicTenant.value).then(res=>modalData(res))
      break;
    case 'cwhcode':
      openStockCangKuModalPage(true, {
        database: dynamicTenantId.value,
      })
      break;

  }*/
}
const modalData = (o) => {
  if (thisEditObj.value) {
    if (thisEditType.value == 'cinvode') {
      thisEditObj.value['tempTwo'] = o[0].stockNum
      focusNext(thisEditObj.value,thisEditType.value)
    } else {
      if (thisEditObj.value['cangkuDuli'] == '0') {
        thisEditObj.value['tempOne'] = o[0].cangkuId
        thisEditObj.value['cwhcodeInfo'] = o[0]
        thisEditObj.value['cwhcodeText'] = o[0].cangkuName
      } else {
        thisEditObj.value['tempOne'] = o[0].id
        thisEditObj.value['cwhcodeText'] = o[0].cangkuName
      }
      focusNext(thisEditObj.value,thisEditType.value)
    }
    thisEditObj.value = null
  } else {
    let e = formFuns.value.getFormValue()
    e[thisEditType.value] = thisEditType.value == 'cvencode' || thisEditType.value == 'cwhcode' ? o[0].id : (thisEditType.value == 'cdepcode' || thisEditType.value == 'deliveryUser') ? o.uniqueCode : o.id
    if (thisEditType.value == 'cvencode') e = changeCust(o[0].id,custList.value,e)
    formFuns.value.setFormValue(e)
  }
}
const resetRow = (record) => {
  record['baseQuantityOld'] = record.isumZhu
  let o = assetsCardList.value.filter(it => it.stockNum == record.cinvode )[0]
  record.cunitType = o?.stockMeasurementType ==  '单计量'?'1':'0'
  if (record.cunitType == '0'){ //
    let res =  manyJiList.value.filter(it=>it.id == o?.stockMeasurementUnit)[0]
    if (res != null) {
      record.unitInfo = res
      let list = JsonTool.parseObj(res.detail) || []
      record.unitList = list.map(it=>{it.value=it.id;it.label=it.unitName;it.ggxh='';it.barcode ='';return it;})
      if (list.length > 0) {
        record.unitList[0].ggxh = o?.stockGgxh
        record.unitList[0].barcode = o?.stockBarcode
        if (list.length > 1) {
          record.unitList[1].ggxh = o?.stockUnitGgxh1
          record.unitList[1].barcode = o?.stockUnitBarcode1
          if (list.length > 2){
            record.unitList[2].ggxh = o?.stockUnitGgxh2
            record.unitList[2].barcode = o?.stockUnitBarcode2
          }
        }
      }
    }
  }else {
    let res = jiList.value.filter(it => it.id == o?.stockMeasurementUnit)
    if (null !=  res && res.length >0){
      record.unitInfo = res[0]
      record.unitList = res.map(it=>{it.value=it.id;it.label=it.unitName;it.ggxh=o?.stockGgxh;it.barcode =o?.stockBarcode;return it;})
    }
  }
  let cangkuInfo = ckListOptions.value.filter(it => it.id == record.cwhcode)[0]
  if (null != cangkuInfo) {
    if (cangkuInfo.ckIsDuli == '1') {
      record['cwhcodeText'] = cangkuInfo.ckName
    }
  }
  return record;
}

const tableDataChange =  (r,c) => {
  switch (c) {
    case 'cwhcode':
      let cangkuInfo = ckListOptions.value.filter(it => it.id == r.cwhcode)[0]
      if (null != cangkuInfo) {
        if (cangkuInfo.ckIsDuli == '1') {
          r['cwhcodeText'] = cangkuInfo.ckName
        }
      }
      break;
    case 'cinvode':
      if (chFocus.value == 'two') r.cinvName = r.tempCinvName
      if (chFocus.value == 'three') r.tiaoxinma = r.tempTiaoxinma
      chChange(r)
      break;
    case 'unitId':
    case 'isum':
      let index = r.unitList.findIndex(it=>it.id==r.unitId)
      if (c == 'unitId' && hasBlank(r.isum))  break;
      r.isum = (parseFloat(r.isum)>0?(parseFloat(r.isum)*(titleValue.value==1?-1:1)):parseFloat(r.isum)).toFixed(10)
      if (c == 'unitId' && index!=-1){
        r.guigexh = r.unitList[index]?.ggxh // 规格型号
        r.tiaoxinma = r.unitList[index]?.barcode // 条形码
      }
      if (c == 'unitId' || hasBlank(r.isum))  break;
      slChangeNew(r)
      break;
  }
  return r;
}

const slChangeNew = (r) => {
  if (r.cunitType == '1') { // 单计量
    r.isumZhu = r.isum
    r.subQuantity1 = "0.0000000000"
    r.subQuantity2 = "0.0000000000"
  } else { //多计量
    let n = parseFloat(r.isum)
    let index = r.unitList.findIndex(it => it.id == r.unitId)
    if (index == 1 && r.unitList.length > 1) {
      r.subQuantity1 = n.toFixed(10)
      r.isumZhu = parseFloat((n * parseFloat(r.unitList[1].conversionRate))).toFixed(10)
      if (r.unitList.length > 2) r.subQuantity2 = parseFloat(n * (parseFloat(r.unitList[1].conversionRate)) / (parseFloat(r.unitList[2].conversionRate))).toFixed(10)
    } else if (index == 2 && r.unitList.length > 2) {
      r.subQuantity2 = n.toFixed(10)
      r.isumZhu = parseFloat(((n * parseFloat(r.unitList[2].conversionRate)) / parseFloat(r.unitList[0].conversionRate))).toFixed(10)
      r.subQuantity1 = parseFloat(((n * parseFloat(r.unitList[2].conversionRate)) / parseFloat(r.unitList[1].conversionRate))).toFixed(10)
    } else {
      r.isumZhu = n.toFixed(10)
      r.subQuantity1 = parseFloat((n / parseFloat(r.unitList[1].conversionRate))).toFixed(10)
      if (r.unitList.length > 2) r.subQuantity2 = parseFloat((n / parseFloat(r.unitList[2].conversionRate))).toFixed(10)
    }
  }
}
const slChange = (r,c) => {
  if (r.cunitType == '0' && r.unitInfo != null){ // 存在多计量的清空
    // 运算辅助数量
    let list = JsonTool.parseObj(r.unitInfo.detail) || []
    if (list.length > 0){
      let n = parseFloat(r.isumZhu).toFixed(10)
      let isnum  = (r.unitInfo.unitType == '2')
      let one =  parseFloat((n/parseFloat(list[1].conversionRate))).toFixed(10)
      let two =  null != list[2]?parseFloat((n/parseFloat(list[2].conversionRate))).toFixed(10):0
      if (isnum){ //取整数}
        r.isumZhu = NumberTool.toCeil(n,2)+''
        r.subQuantity1 = NumberTool.toCeil(one,2)+''
        r.subQuantity2 = NumberTool.toCeil(two,2)+''
      }else {
        r.subQuantity1 = one+''
        r.subQuantity2 = two+''
      }
    }else {
      r.subQuantity1 = "0.0000000000"
      r.subQuantity2 = "0.0000000000"
    }
  }else {
    r.subQuantity1 = "0.0000000000"
    r.subQuantity2 = "0.0000000000"
  }
}

const chChange = (record) => {
  let o = assetsCardList.value.filter(it => (chFocus.value == 'one' && (it.stockNum == record.cinvode)) || (chFocus.value == 'two' && it.stockName == record.cinvName)
    || (chFocus.value == 'three' && it.stockBarcode == record.tiaoxinma))[0]
  if (o == null) return false
  record.cinvName = o?.stockName
  // record.cinvode = o?.stockNum
  record.guigexh = o?.stockGgxh // 规格型号
  record.tiaoxinma = o?.stockBarcode // 条形码
  // 存货对应仓库 console.log(o?.stockCangku)
  // 得到单价
  let p = stockPriceList.value.filter(it => it.stockNum == o?.stockNum)[0]
  let k = formFuns.value.getFormValue()?.cvencodeGrade
  if (null != p && null != k && null != p[k] && '0' !=  p[k]) record.defaultPrice = parseFloat(p[k]).toFixed(10)
  // 计量类型
  record.cunitType = o.stockMeasurementType == '单计量' ? '1' : '0'
  record.isBatch = o?.stockPropertyBatch == '1' // 批号必须输入
  if (record.cunitType == '0' && !hasBlank(o?.stockMeasurementUnit) && record?.unitInfo == null) {
    let res = manyJiList.value.filter(it => it.id == o?.stockMeasurementUnit)[0]
    if (res != null) {
      record.unitInfo = res
      let list = JsonTool.parseObj(res.detail) || []
      record.unitList = list.map(it=>{it.value=it.id;it.label=it.unitName;it.ggxh='';it.barcode ='';return it;})
      if (list.length > 0) {
        record.unitId = (o?.stockMarketUnit?o?.stockMarketUnit: list.filter(it=>it.isMain == 'true')[0]?.id)
        record.unitIdZhu = list[0].id
        record.unitList[0].ggxh = o?.stockGgxh
        record.unitList[0].barcode = o?.stockBarcode
        if (list.length > 1) {
          record.cunitidF1 = list[1].id
          record.unitList[1].ggxh = o?.stockUnitGgxh1
          record.unitList[1].barcode = o?.stockUnitBarcode1
          if (list.length > 2){
            record.unitList[2].ggxh = o?.stockUnitGgxh2
            record.unitList[2].barcode = o?.stockUnitBarcode2
            record.cunitidF2 = list[2].id
          }
        }
      }
    }
  }else if (record.cunitType == '1' &&  !hasBlank(o?.stockMeasurementUnit)){
    let res = jiList.value.filter(it => it.id == o?.stockMeasurementUnit)
    if (null !=  res && res.length >0){
      record.unitInfo = res[0]
      record.unitList = res.map(it=>{it.value=it.id;it.label=it.unitName;it.ggxh=o?.stockGgxh;it.barcode =o?.stockBarcode;return it;})
      record.unitId = record.unitList[0].value
      record.unitIdZhu = record.unitList[0].value
    }
  }
  // 检查当前存活是否为 BOM
  verifyBom(record)
}

const verifyBom = async (record) => {
  let  mark = ( await useRouteApi(isBomVerify, {schemaName: dynamicTenantId})({code: record.cinvode}) || '0')
  record.isKuoZhan = mark
}

const outBefore = () => {
  if (status.value != 3){
    createConfirm({
      iconType: 'warning',
      title: '编辑中',
      content: '当前正在处于编辑,退出编辑将丢失,您确定要继续进行吗？',
      onOk: async () => {
        // 调整数据库 列参数
        closeCurrent()
        tempTaskDel()
      },
      onCancel: async () => {

      }
    });
  } else {
    closeCurrent()
  }
}

function indexToUpper(str,index) {
  return str.trim().replace(str[index], str[index].toUpperCase());
}
/**
 * @param e 回车确定事件
 * @param r rowObj
 * @param c className
 * @param t mark
 */
const focusNext =  (r, c) => {
  // 得到当前临时标记
  let t = indexToUpper(c,0)
  Object.keys(r).map(i => {
    if (i.startsWith('edit')) r[i] = null
  })
      r[`${c}`] = r[`temp${t}`];
  // 列表值发生变动 监听调整
  tableDataChange(r, c)
  // 查找下一个
  let list = getDataSource();
  let filters = [ 'guigexh', 'unitIdZhu', 'cinvName','tiaoxinma','isumZhu','isKuoZhan','cunitidF1','cunitidF2','subQuantity1','subQuantity2']
            let cols = getColumns().filter(it => it.title != '序号' && filters.indexOf(it.dataIndex) == -1 /*&& it.ifShow*/)
  let index = list.findIndex(it => it.key == r.key)
  let nextC = cols[0].dataIndex // 获取下一个列位置
  if (index == list.length - 1 && cols[cols.length - 1].dataIndex == c) { // 最后一行最后一列回车追加
    list.push({editOne: true})
    setTableData(list)
  } else {
    let cObj = cols[cols.findIndex(it => it.dataIndex == c) + 1]
      if (cObj != null) {
      nextC = cObj.dataIndex
      // 的到指定位置
      let nextMark = indexToUpper(nextC, 0)
      r[`edit${nextMark}`] = true;
      r[`temp${nextMark}`] = r[`${nextC}`];
    } else { //之后一列时换行
      nextC = cols[0].dataIndex
      let nextMark = indexToUpper(nextC, 0)
      ++index
      list[index][`edit${nextMark}`] = true
      list[index][`temp${nextMark}`] = list[index][`${nextC}`];
      setTableData(list)
    }
  }
  nextTick(() => {
    let doms = nextC == 'explain' ? document.getElementsByClassName(nextC)[0] : document.getElementsByClassName(nextC)[0]?.getElementsByTagName('input')[0]
    if (null != doms) doms.focus()
  })
}



const pageSearch = async (v) => {
  if (searchList.value.length == 0) searchList.value = getDataSource().filter(it => it.key != null && it.ccode != null);
  let newList = hasBlank(v) ? searchList.value : searchList.value.filter(it => it.cinvode.indexOf(v) != -1 || it.cinvName.indexOf(v) != -1)
  let len = newList.length
  for (let i = 0; i < (25 - len); i++) {
    newList.push({})
  }
  setTableData(newList)
}


const cunitFormat = (list,id,k) => {
  if (null == list || hasBlank(id)) return id;
  let it = list.filter(it=>it.id == id)[0]
  return  null == it?id:it.label
}

/*** 合计 ***/
const getCurrSummary  = () => {
  return (getColumns().filter(it=>it.title != '序号' && it.ifShow).map((it,ind)=>{it['ind']=ind+2;return it;}))
}
const calculateTotal = () => {
  let list = JsonTool.parseProxy(getDataSource().filter(it=>it.cvencode!=null && it.isum!=null))
  if (list.length == 0){
    summaryModel.value = {}
    return false;
  }
  let num = 0
  let numz = 0
  let num1 = 0
  let num2 = 0
  let wsum = 0
  let sum = 0
  let se = 0
  for (let i = 0; i < list.length; i++) {
    const e = list[i];
    num+=parseFloat(e['isum'] || 0)
    numz+=parseFloat(e['isumZhu'] || 0)
    num1+=parseFloat(e['subQuantity1'] || 0)
    num2+=parseFloat(e['subQuantity2'] || 0)
    wsum+=parseFloat(e['icost'] || 0)
    sum+=parseFloat(e['isum'] || 0)
    se+=parseFloat(e['itaxprice'] || 0)
  }
  summaryModel.value={
    isum: num,
    isumZhu: numz,
    subQuantity1: num1,
    subQuantity2: num2,
    icost: wsum,isum: sum,itaxprice:se
  }
}
/*** 合计 ***/
/************ 日志 *************/
async function writeLog(action, a, content) {
  /************** 记录操作日志 ****************/
  if (action == '修改') {
    let old = getDataSource().filter(it => it.id == a.id)[0]
    let text = `操作内容【${action}BOM物料清单】,` + 'BOM识别码【' + a.bomSysId + '】,父件编码【' + a.bomId + '】,'
    /* for (let i = 0; i < keys.length; i++) {
       const k = keys[i];
       let t = CrudApi.columns.filter(t => t.dataIndex == k)[0]?.title
       text += `${t}【${transformText(k, old[k])},${transformText(k, a[k])}】;`
     }*/
    content = text.substring(0, text.length - 1)
  }
  let map = {
    loginTime: new Date(+new Date() + 8 * 3600 * 1000).toJSON().substr(0, 19).replace("T", " "),
    userId: useUserStoreWidthOut().getUserInfo.username,
    userName: useUserStoreWidthOut().getUserInfo.realName,
    optModule: 'stock',
    optFunction: 'BOM物料清单',
    optRange: '2',
    uniqueCode: '',
    optAction: action,    accId:dynamicAccId.value,
    optContent: content ||`操作内容【${action}BOM物料清单】,` + 'BOM识别码【' + a.bomSysId + '】,父件编码【' + a.bomId + '】'
  }
  await saveLog(map)
}
/************ 日志 *************/
provide('simpJlList',jiList)
provide('manyJlList',manyJiList)
</script>
<style lang="less" scoped="scoped">
@Global-Border-Color: #c9c9c9; // 全局下划线颜色
@Global-Label-Color: #666666; // 全局#c9c9c9颜色
@Global-Comm-BcOrText-Color: #0096c7; // 全局#c9c9c9颜色
.app-container {
  padding: 10px;
  background-color: #b4c8e3;
  margin: 0;

  .app-container-top {
    background-color: @Global-Comm-BcOrText-Color !important;
    border-radius: 5px 5px 0 0;
    padding: 10px;
    > div {
      width: 100%;
      display: inline-flex;
      justify-content: space-between;
    }

  }

  .app-container-bottom {
    background-color: white;

    .acb-head {
      .acbgead-one {
        text-align: center;
        height: 60px;
        line-height: 60px;

        .acbgead-one-triangle {
          > div:nth-of-type(1) {
            width: 0px;
            height: 0px;
            border-top: 60px solid #5141d7;
            border-right: 70px solid transparent;
            position: absolute;
          }

          > div:nth-of-type(2) {
            width: max-content;
            color: #fff;
            transform: rotate(-45deg) translateY(-2px) translateX(10px);
            position: absolute;
          }
        }

        .acbgead-one-changes {
          > span {
            cursor: pointer;
          }

          > span:hover {
            color: black;
          }
        }

        > div:nth-of-type(2) {
          display: inline-block;
          float: left;
          margin-left: 4%;
          font-weight: bold;
          font-size: 24px;
          color: #666666;
        }
      }

      .acbgead-two {
        margin: 0 5rem;
      }
    }

    .acb-centent {
      margin: 5px 4% 0;

      :deep(.nc-summary) {
        font-size: 14px;
        font-weight: bold;
        width: 100%;

        > div {
          background-color: #d8d8d8;
          padding: 5px;
          width: 15%;
          margin: 0 1px;
          display: inline-flex;
          justify-content: space-between;
        }
        > div:nth-of-type(1) {
          width: 24%;
          display: inline-block;
          text-align: center;
        }
      }
    }
  }

  .status-look {
    pointer-events: none;
    -webkit-user-select: none;
    -moz-user-select: none;
    -ms-user-select: none;
    user-select: none;
  }
}
.lanmu-table{
  :deep(.ant-table-content){
    .ant-table-thead{
      tr{
        th{
          padding: 5px 8px !important;
          text-align: center !important;
        }
      }
    }
    .ant-table-tbody{
      tr{
        td{
          padding:0 5px !important;
          font-size: 12px !important;
          .ant-radio-button-wrapper{
            font-size: 12px;
          }
        }
      }

    }
  }
}
.suspended-div {
  width: 100%;
  height: 22px;

  .anticon-form {
    float: right;
    line-height: 22px;
    display: none;
  }
}
.suspended-div:hover {
  cursor: text;
  background-color: #e4e7e7;
  .anticon-form {
    display: block;
  }
}
</style>
<style lang="less" scoped>
@import '/@/assets/styles/global-menu-index1.less';
.lcr-theme-div{
  display: inline-flex;justify-content: space-between;width: 100%;height: 100px;
  >div:nth-of-type(1){
    width: 40%;
    position: relative;
    display: block;
    >div:nth-of-type(1){width: 64px;display: inline-block;text-align: center;    top: 12px;
      position: inherit
    }
    >div:nth-of-type(2){

      width: calc( 100% - 64px);display: inline-block;
      :deep(.account-picker){
        .ap-title,.thisNameSpan{
          color: white !important;
        }
      }
    }
  }
  >div:nth-of-type(2){
    width: 20%;text-align:center;
    >div:nth-of-type(2){margin-top: 14px;}
  }
  >div:nth-of-type(3){
    width: 40%;text-align: right;
    display: block;
    >div:nth-of-type(1){
      .actod-btn {
        color: @Global-Comm-BcOrText-Color;
        font-size: 14px;
        margin: 0 1px 0 0;
      }

      .actod-btn-last {
        border-right: 1px solid @Global-Border-Color;
      }

      .actod-btn:hover {
        background-color: @Global-Comm-BcOrText-Color;
        color: white;
        font-weight: bold;
        border: 1px solid white;
      }
    }
    >div:nth-of-type(2){
      display: inline-flex;justify-content: space-between;margin-top: 15px;
      .acttd-right-d-search {
        .acttdrd-search-select {
          width: 120px;

          :deep(.ant-select-selector) {
            border-color: @Global-Border-Color;
            border-radius: 2px 0 0 2px;
          }
        }

        .acttdrd-search-input {
          width: 180px;
          border-radius: 0 2px 2px 0;
          border-color: @Global-Border-Color;
          border-left: none;
        }
      }

      .acttd-right-d-btns {
        margin-left: 10px;

        .acttdrd-btn {
          color: @Global-Comm-BcOrText-Color;
          margin-left: 2px;
        }
        .acttdrd-btn:hover{
          border-color: #ffffff;
          color: #ffffff;
          background-color: @Global-Comm-BcOrText-Color;
        }
      }
    }
  }
}
</style>

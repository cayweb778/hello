<template>
  <div class="app-financial-info" :style="{height: (windowHeight-95)+'px'}">
    <div class="a-container-head">
      <div class="a-c-head-title">
        <SettingOutlined style="font-size: 30px;color: #666666;float: left;margin: 0 .5%;"/>
        <h2>参数设置</h2>
        <div style="float: right;margin-top: -40px;" >
          <button type="button" class="ant-btn ant-btn-me" @click="closeCurrent(),router.push('/zhongZhang/home/welcome')"><span>退出</span></button>
        </div>
      </div>
      <div class="a-c-head-btns">
        <div class="ant-btn-group a-c-h-btns-group" style="min-width: 600px">
          <AccountPicker theme="one" @reloadTable="dynamicAdReload" style="width: 100%"/>
        </div>
      </div>
    </div>
    <div class="setting_box"></div>
    <div class="a-container-content">
      <a-tabs type="card" :tabBarGutter="0" v-model:activeKey="activeKey" tabPosition="left">
        <a-tab-pane key="1" :style="activeKey=='1'?{height: (windowHeight-170)+'px',overflowY: 'auto'}:{}">
          <template #tab>
            <span style="line-height: 30px;display: flex;color: white;">
            <AppstoreOutlined style="font-size: 30px;color: #666666;background-color: white;"/>
              基本选项
            </span>
          </template>
          <div class="a-container-content-one">
            <div class="acco-btn">
                <a-button v-if="isEdit" type="primary" @click="handleBus('0')">编辑</a-button>
                <template v-if="!isEdit">
                  <a-button @click="handleBus('1')">恢复系统设置</a-button>
                  <a-button type="primary" @click="handleBus('2')">保存</a-button>
                  <a-button @click="handleBus('3')">放弃</a-button>
                </template>
            </div>
            <div class="acco-sideline">
              <span>基础信息</span>
              <div class="acco-sideline-content-one">
                <div>
                  <span>会计准则：</span>
                  <a-select v-model:value="dataModel.basisMap.accStandard" style="width: 300px" :disabled="true">
                    <a-select-option v-for="item in pageParameter.acountStandardList"
                                     :value="item.id+''">
                      {{ item.tname }}
                    </a-select-option>
                  </a-select>
                </div>
                <div>
                  <span>科目级次：</span>
                  <span
                    style="display: inline-block;color: black;width: 124px;text-align: center">{{
                      levelPrefix || '选择准则'
                    }}-</span>
                  <a-input v-model:value="levelSuffix" placeholder="" style="width: 176px" class="against-input-mark"
                           @keyup="importLimit" @blur="importLimit" :disabled="isEdit"/>
<!--                  <a-select  v-model:value="dataModel.basisMap.ccodeLevel" style="width: 300px" :disabled="isEdit">
                    <a-select-option v-for="item in pageParameter.levelList" :value="item.tjici">
                      {{item.tjici}}
                    </a-select-option>
                  </a-select>-->
                </div>
<!--    abbreviation--currencyZhCnName--currencyName             -->
                <div>
                  <span>&emsp;本位币：</span>
                  <a-select  v-model:value="dataModel.basisMap.currency" style="width: 300px" :disabled="true">
                    <a-select-option v-for="item in currencyTypeList" :key="item.abbreviation" :value="item.abbreviation">{{item.currencyZhCnName}}</a-select-option>
                  </a-select>
                </div>
                <div>
                  <span>纳税性质：</span>
                  <a-select v-model:value="dataModel.basisMap.taxClass" style="width: 300px" :disabled="isEdit">
                    <a-select-option value="1">
                      一般纳税人
                    </a-select-option>
                    <a-select-option value="2">
                      小规模企业纳税人
                    </a-select-option>
                  </a-select>
                </div>
                <div>
                  <span>公司(单位)名称：</span>
                  <a-input v-model:value="dataModel.basisMap.accName" :disabled="true" style="width: 260px"/>
                </div>
                <div>
                  <span>凭证打印名称：</span>
                  <a-input v-model:value="dataModel.basisMap.unitName" :disabled="isEdit" style="width: 260px"/>
                </div>
              </div>
            </div>
            <div class="acco-sideline">
              <span>制单控制</span>
              <div  class="acco-sideline-content-two">
                <a-checkbox-group v-model:value="dataModel.zhiDanList" :disabled="isEdit" style="width: 100%;">
                  <a-row>
                    <a-col :span="5">
                      <a-checkbox value="ichronological">
                        制单序时控制
                      </a-checkbox>
                    </a-col>
                    <a-col :span="5">
                      <a-checkbox value="ideficit">
                        资金及往来赤字控制
                      </a-checkbox>
                    </a-col>
                    <a-col :span="5">
                      <a-checkbox value="icashFlow">
                        现金流量必录
                      </a-checkbox>
                    </a-col>
                    <a-col :span="5">
                      <a-checkbox value="iar">
                        可以使用业务受控科目
                      </a-checkbox>
                    </a-col>
<!--                    <a-col :span="5">
                      <a-checkbox value="isettlement">
                        结算方式必录
                      </a-checkbox>
                    </a-col>
                    <a-col :span="5">
                      <a-checkbox value="ibill">
                        往来单位票据号必录
                      </a-checkbox>
                    </a-col>-->
<!--                    <a-col :span="5">
                      <a-checkbox value="iar">
                        可以使用应收受控科目
                      </a-checkbox>
                    </a-col>
                    <a-col :span="5">
                      <a-checkbox value="iap">
                        可以使用应付受控科目
                      </a-checkbox>
                    </a-col>
                    <a-col :span="5">
                      <a-checkbox value="price">
                        单价允许为空
                      </a-checkbox>
                    </a-col>
                    <a-col :span="5">
                      <a-checkbox value="inum">
                        数量为零允许保存
                      </a-checkbox>
                    </a-col>
                    <a-col :span="5">
                      <a-checkbox value="isave">
                        借贷不平保存为暂存凭证
                      </a-checkbox>
                    </a-col>
                    <a-col :span="5">
                      <a-checkbox value="iexchange">
                        外币汇率为空允许保存
                      </a-checkbox>
                    </a-col>
                    <a-col :span="5">
                      <a-checkbox value="ifreecorp">
                        允许跨公司制单
                      </a-checkbox>
                    </a-col>-->
                  </a-row>
                </a-checkbox-group>
              </div>
            </div>
            <div class="acco-sideline">
              <span>凭证编号</span>
              <div  class="acco-sideline-content-two">
                <a-checkbox-group v-model:value="dataModel.pingZhenNumberList" :disabled="isEdit" style="width: 100%;">
                  <a-row>
                    <a-col :span="5">
                      <a-checkbox value="ibreakCode">
                        凭证自动补断号
                      </a-checkbox>
                    </a-col>

                    <a-col :span="5">
                      <a-checkbox value="iautoCode">
                        手工编码
                      </a-checkbox>
                    </a-col>
                    <a-col :span="5">
                      <a-checkbox value="monthCode">
                        凭证号按月份统一编号排序
                      </a-checkbox>
                    </a-col>
                    <a-col :span="5">
                      <a-checkbox value="iyearCode">
                        凭证号按年度统一编号排序
                      </a-checkbox>
                    </a-col>
                  </a-row>
                </a-checkbox-group>
              </div>
            </div>
            <div class="acco-sideline">
              <span>凭证控制</span>
              <div  class="acco-sideline-content-two">
                <a-checkbox-group v-model:value="dataModel.pingZhenControlList" :disabled="isEdit" style="width: 100%;">
                  <a-row>
                    <a-col :span="5">
                      <a-checkbox value="iverify">
                        制单与审批允许同一人
                      </a-checkbox>
                    </a-col>
                    <a-col :span="5">
                      <a-checkbox value="iverifyCancel">
                        审核与取消审核须为同一人
                      </a-checkbox>
                    </a-col>
                    <a-col :span="5">
                      <a-checkbox value="icashierNo">
                        出纳签字取消须为同一人
                      </a-checkbox>
                    </a-col>
                    <a-col :span="5">
                      <a-checkbox value="icdirectorNo">
                        主管签字取消须为同一人
                      </a-checkbox>
                    </a-col>
                    <a-col :span="5">
                      <a-checkbox value="icashier">
                        出纳凭证必须出纳签字
                      </a-checkbox>
                    </a-col>
                    <a-col :span="5">
                      <a-checkbox value="imanager">
                        凭证必须经由主管签字
                      </a-checkbox>
                    </a-col>
                    <a-col :span="5">
                      <a-checkbox value="ifVerify">
                        凭证必须审核后记账
                      </a-checkbox>
                    </a-col>

                    <a-col :span="5">
                      <a-checkbox value="iotherAccvouch">
                        允许修改业务系统凭证
                      </a-checkbox>
                    </a-col>
<!--                    <a-col :span="5">
                      <a-checkbox value="iotherAccvouchDel">
                        允许删除业务系统凭证
                      </a-checkbox>
                    </a-col>-->
                  </a-row>
                </a-checkbox-group>
              </div>
            </div>
<!--            <div class="acco-sideline">
              <span>系统设置</span>
              <div  class="acco-sideline-content-two">
                <a-checkbox-group v-model:value="dataModel.settingList" :disabled="isEdit" style="width: 100%;">
                  <a-row>
&lt;!&ndash;                    <a-col :span="5">
                      <a-checkbox value="ifreeConfirm">
                          是否允许跨公司审核记账
                      </a-checkbox>
                    </a-col>
                         <a-col :span="5">
                      <a-checkbox value="ibook">
                        是否允许取消记账
                      </a-checkbox>
                    </a-col>
                    &ndash;&gt;

                    <a-col :span="5">
                      <a-checkbox value="ibook">
                        允许删除作废凭证
                      </a-checkbox>
                    </a-col>
                    <a-col :span="5">
                      <a-checkbox value="icheckOut">
                        允许取消结账
                      </a-checkbox>
                    </a-col>
                    <a-col :span="5">
                      <a-checkbox value="icash">
                        按凭证类别控制操作
                      </a-checkbox>
                    </a-col>
                    <a-col :span="5">
                      <a-checkbox value="iProfit">
                        按会计科目范围控制操作
                      </a-checkbox>
                    </a-col>
&lt;!&ndash;                    <a-col :span="5">
                      <a-checkbox value="icash">
                        是否控先出纳签字后审核凭证
                      </a-checkbox>
                    </a-col>
                    <a-col :span="5">
                      <a-checkbox value="iProfit">
                        结账检查损益结转是否完成
                      </a-checkbox>
                    </a-col>
                    <a-col :span="5">
                      <a-checkbox value="icheckOutNext">
                        上个未结账本月允许记账
                      </a-checkbox>
                    </a-col>
                    <a-col :span="5">
                      <a-checkbox value="iexchangeRateControl">
                        是否外币汇率管控
                      </a-checkbox>
                    </a-col>&ndash;&gt;
                    <a-col :span="5">
                      <a-checkbox value="projectClassCtl">
                        项目大类控制科目及项目
                      </a-checkbox>
                    </a-col>
                  </a-row>
                </a-checkbox-group>
              </div>
            </div>
            <div class="acco-sideline">
              <span>日期控制</span>
              <div class="acco-sideline-content-there">
               <ul>
                 <li>
                   新增凭证日期规则：
                   <a-radio-group v-model:value="dataModel.dateMap.addAccDate" :disabled="isEdit">
                     <a-radio value="1">
                       业务日期
                     </a-radio>
                     <a-radio value="2">
                       系统日期
                     </a-radio>
                     <a-radio value="3">
                       手动添加
                     </a-radio>
                   </a-radio-group>
                 </li>
                 <li>
                   出纳签字日期规则：
                   <a-radio-group v-model:value="dataModel.dateMap.cashhierDate" :disabled="isEdit">
                     <a-radio value="1">
                       业务日期
                     </a-radio>
                     <a-radio value="2">
                       系统日期
                     </a-radio>
&lt;!&ndash;                     <a-radio value="3">
                       手动添加
                     </a-radio>&ndash;&gt;
                   </a-radio-group>
                 </li>
                 <li>
                   审核凭证日期规则：
                   <a-radio-group v-model:value="dataModel.dateMap.verifyDate" :disabled="isEdit">
                     <a-radio value="1">
                       业务日期
                     </a-radio>
                     <a-radio value="2">
                       系统日期
                     </a-radio>
&lt;!&ndash;                     <a-radio value="3">
                       手动添加
                     </a-radio>&ndash;&gt;
                   </a-radio-group>
                 </li>
                 <li>
                   主管签字日期规则：
                   <a-radio-group v-model:value="dataModel.dateMap.managerDate" :disabled="isEdit">
                     <a-radio value="1">
                       业务日期
                     </a-radio>
                     <a-radio value="2">
                       系统日期
                     </a-radio>
&lt;!&ndash;                     <a-radio value="3">
                       手动添加
                     </a-radio>&ndash;&gt;
                   </a-radio-group>
                 </li>
                 <li>
                   凭证记账日期规则：
                   <a-radio-group v-model:value="dataModel.dateMap.bookDate" :disabled="isEdit">
                     <a-radio value="1">
                       业务日期
                     </a-radio>
                     <a-radio value="2">
                       系统日期
                     </a-radio>
&lt;!&ndash;                     <a-radio value="3">
                       手动添加
                     </a-radio>&ndash;&gt;
                   </a-radio-group>
                 </li>
                 <li>
                   往来核销日期规则：
                   <a-radio-group v-model:value="dataModel.dateMap.wotDate" :disabled="isEdit">
                     <a-radio value="1">
                       业务日期
                     </a-radio>
                     <a-radio value="2">
                       系统日期
                     </a-radio>
                     &lt;!&ndash;                     <a-radio value="3">
                       手动添加
                     </a-radio>&ndash;&gt;
                   </a-radio-group>
                 </li>
               </ul>
              </div>
            </div>-->
          </div>
        </a-tab-pane>
        <a-tab-pane key="2" :style="activeKey=='2'?{height: (windowHeight-170)+'px',overflowY: 'auto'}:{}" >
          <template #tab>
            <span style="line-height: 30px;display: flex;color: white;">
            <ProfileOutlined style="font-size: 30px;color: #666666;background-color: white;"/>
              会计期间
            </span>
          </template>
          <div class="a-container-content-two" style="margin-top: 5px;">
            <div class="acco-btn">
              <a-button v-if="isEdit2" type="primary" @click="handleBusQj('0')">编辑</a-button>
              <template v-if="!isEdit2">
                <a-button type="primary" @click="handleBusQj('2')">保存</a-button>
                <a-button type="primary" @click="handleBusQj('3')">初始化下一年度</a-button>
                <a-button @click="handleBusQj('4')">放弃</a-button>
              </template>
            </div>
            <div class="acct-up">
              <span>基本信息</span>
              <div>
              <span class="acctd-span">启用期间：</span><a-input :value="QjBasicInfo.startPeriod" style="width: 150px;margin: 5px 50px 10px 5px;"/>
               <span class="acctd-span">期间数量：</span><a-input :value="QjBasicInfo.periodNum" style="width: 125px;margin-right: 50px"/>
                   <span class="acctd-span">季度数量：</span>
                   <a-select v-model:value="QjBasicInfo_quarterlyNumber" style="width: 125px;text-align: center" @change="QjHandleChange">
                     <a-select-option value="1">
                       1
                     </a-select-option>
                     <a-select-option value="2">
                       2
                     </a-select-option>
                     <a-select-option value="3" >
                       3
                     </a-select-option>
                     <a-select-option value="4">
                       4
                     </a-select-option>
                     <a-select-option value="5">
                       5
                     </a-select-option>
                     <a-select-option value="6">
                       6
                     </a-select-option>
                   </a-select>
<!--                   <br/>
                   <span class="acctd-span">年度开始日期：</span><a-input :value="QjBasicInfo.yearStartDate" style="width: 320px;margin: 0px 50px 5px 5px;"/>
                   <span class="acctd-span">年度结束日期：</span><a-input :value="QjBasicInfo.yearEndDate" style="width: 320px"/>-->
              </div>
            </div>
            <div class="acct-down" >
              <span>月份期间</span>
              <div>
<!--                <span>会计年度：</span><a-select v-model:value="fiscalYear" @change="yearChange" style="width: 125px;text-align: center;margin: 0 0 15px" >
                  <a-select-option v-for="value in (QjBasicInfo.yearList || [])" :value="value">{{value}}</a-select-option>
              </a-select>-->
                <a-table size="small" bordered :data-source="QjDataSource" :columns="initQjColumns(1)" :pagination="false" class="a-table-font-size-14">
                  <template #dateEnd="{ text, record }">
                    <div class="editable-cell">
                      <div v-if="editableData[record.key] && editableData[record.key].type == 'YFQJ'" class="editable-cell-input-wrapper">
                        <a-date-picker v-model:value="editableData[record.key].dateEnd" value-format="YYYY-MM-DD" format="YYYY-MM-DD"/>
                        <check-outlined class="editable-cell-icon-check" @click="yueSave(record.key,'YFQJ')" />
                      </div>
                      <div v-else class="editable-cell-text-wrapper">
                        {{ text || ' ' }}
                        <edit-outlined v-if="record.iperiodNum == '01' && !isEdit2" class="editable-cell-icon" @click="edit(record.key,'YFQJ')" />
                      </div>
                    </div>
                  </template>
                </a-table>
              </div>
            </div>
            <div class="acct-down">
              <span>季度期间</span>
              <div>
<!--                <span>会计年度：</span><a-select v-model:value="fiscalYear2"  @change="yearChange" style="width: 125px;text-align: center;margin: 0 0 15px" >
                <a-select-option v-for="value in (QjBasicInfo.yearList || [])" :value="value">{{value}}</a-select-option>
              </a-select>-->
                <a-table size="small" bordered :data-source="QjJdDataSource" :columns="initQjColumns(2)" :pagination="false" class="a-table-font-size-14">
                  <template #dateStart="{ text, record }">
                    <div class="editable-cell">
                      <div v-if="editableData[record.key] && editableData[record.key].type == 'JD'" class="editable-cell-input-wrapper">
                        <a-select v-model:value="editableData[record.key].dateStart" style="width: 85%">
                          <a-select-option v-for="item in QjDataSource" :value="item.dateStart">{{item.dateStart}}</a-select-option>
                        </a-select>
<!--                        <a-input v-model:value="editableData[record.key].dateStart" @pressEnter="save(record.key,'JD')" />-->
                        <check-outlined class="editable-cell-icon-check" @click="save(record.key,'JD')" />
                      </div>
                      <div v-else class="editable-cell-text-wrapper">
                        {{ text || ' ' }}
                        <edit-outlined class="editable-cell-icon" v-if="!isEdit2" @click="edit(record.key,'JD')" />
                      </div>
                    </div>
                  </template>
                  <template #dateEnd="{ text, record }">
                    <div class="editable-cell">
                      <div v-if="editableData[record.key] && editableData[record.key].type == 'JD'" class="editable-cell-input-wrapper">

                        <a-select v-model:value="editableData[record.key].dateEnd"  style="width: 85%">
                          <a-select-option v-for="item in QjDataSource" :value="item.dateEnd">{{item.dateEnd}}</a-select-option>
                        </a-select>
                        <check-outlined class="editable-cell-icon-check" @click="save(record.key,'JD')" />
                      </div>
                      <div v-else class="editable-cell-text-wrapper">
                        {{ text || ' ' }}
                        <edit-outlined class="editable-cell-icon" v-if="!isEdit2" @click="edit(record.key,'JD')" />
                      </div>
                    </div>
                  </template>
                </a-table>
              </div>
            </div>
          </div>
        </a-tab-pane>
<!--        <a-tab-pane key="3" :style="activeKey=='3'?{height: windowHeight+'px',overflowY: 'auto'}:{}" tab="凭证类型">
          <div class="a-container-content-two">
            <div class="acct-up">
              <span>默认类型</span>
              <div>
                <span class="acctd-span">凭证字：</span><a-select v-model:value="PingZhengTypeInfo.voucherTypeCode" style="width: 200px;text-align: center;margin-right: 30px" @change="typeHandleChange">
                <a-select-option v-for="item in PingZhengTypeDataSource" :value="item.voucherTypeCode" :key="item.id">
                  {{item.voucherTypeCode}}
                </a-select-option>
              </a-select>
                <span class="acctd-span">凭证类型：</span>
                <a-input :value="PingZhengTypeInfo.voucherTypeName" style="width: 200px;text-align: center;margin-right: 30px"/>
                <span class="acctd-span">限制类型：</span>
                <a-input :value="PingZhengTypeInfo.limitClass" style="width: 200px;text-align: center"/>
              </div>
            </div>
            <div class="acct-down">
              <span>凭证类型</span>
              <div>
                <a-button class="editable-add-btn" type="primary" size="small" @click="handleTypeAdd" style="margin-left: 15px;">+</a-button>
                <a-table size="small" bordered :data-source="PingZhengTypeDataSource" :columns="PingZhengTypeColumns" :pagination="false">
                 <template #voucherTypeCode="{ text, record }">
                    <div class="editable-cell">
                      <div v-if="editableData[record.key] && editableData[record.key].type == 'TYPE'" class="editable-cell-input-wrapper">
                        <a-input v-model:value="editableData[record.key].voucherTypeCode" @pressEnter="save(record.key,'TYPE')" />
                        <check-outlined class="editable-cell-icon-check" @click="save(record.key,'TYPE')" />
                      </div>
                      <div v-else class="editable-cell-text-wrapper">
                        {{ text || ' ' }}
                        <edit-outlined class="editable-cell-icon" @click="edit(record.key,'TYPE')" />
                      </div>
                    </div>
                  </template>
                  <template #voucherTypeName="{ text, record }">
                    <div class="editable-cell">
                      <div v-if="editableData[record.key] && editableData[record.key].type == 'TYPE'" class="editable-cell-input-wrapper">
                        <a-input v-model:value="editableData[record.key].voucherTypeName" @pressEnter="save(record.key,'TYPE')" />
                        <check-outlined class="editable-cell-icon-check" @click="save(record.key,'TYPE')" />
                      </div>
                      <div v-else class="editable-cell-text-wrapper">
                        {{ text || ' ' }}
                        <edit-outlined class="editable-cell-icon" @click="edit(record.key,'TYPE')" />
                      </div>
                    </div>
                  </template>
                  <template #limitClass="{ text, record }">
                    <div class="editable-cell">
                      <div v-if="editableData[record.key] && editableData[record.key].type == 'TYPE'" class="editable-cell-input-wrapper">
                        <a-select v-model:value="editableData[record.key].limitClass" style="width: 85%">
                          <a-select-option value="无限制">
                            无限制
                          </a-select-option>
                          <a-select-option value="借方必有">
                            借方必有
                          </a-select-option>
                          <a-select-option value="贷方必有">
                            贷方必有
                          </a-select-option>
                          <a-select-option value="借方必无">
                            借方必无
                          </a-select-option>
                          <a-select-option value="贷方必无">
                            贷方必无
                          </a-select-option>
                          <a-select-option value="凭证必有">
                            凭证必有
                          </a-select-option>
                          <a-select-option value="凭证必无">
                            凭证必无
                          </a-select-option>
                        </a-select>
                        <check-outlined class="editable-cell-icon-check" @click="save(record.key,'TYPE')" />
                      </div>
                      <div v-else class="editable-cell-text-wrapper">
                        {{ text || ' ' }}
                        <edit-outlined class="editable-cell-icon" @click="edit(record.key,'TYPE')" />
                      </div>
                    </div>
                  </template>
                  <template #limitKemu="{ text, record }">
                    <div class="editable-cell">
                      <div v-if="editableData[record.key] && editableData[record.key].type == 'TYPE'" class="editable-cell-input-wrapper">
                        <a-select
                          mode="tags"
                          size="default"
                          placeholder=""
                          v-model:value="editableData[record.key].limitKemu"
                          style="width: 80%"
                        >
                          <a-select-option v-for="item in QjDataSource" :key="item.id" :value="item.dateStart">{{item.dateStart}}</a-select-option>
                        </a-select>
                        <check-outlined class="editable-cell-icon-check" @click="save(record.key,'TYPE')" />
                      </div>
                      <div v-else class="editable-cell-text-wrapper">
                        {{ text || ' ' }}
                        <edit-outlined class="editable-cell-icon" @click="edit(record.key,'TYPE')" />
                      </div>
                    </div>
                  </template>
                  <template #operation="{ record }">
                    <a-button class="editable-add-btn" type="primary" size="small" @click="openPage(record,'TYPE')">✎</a-button>&nbsp;
                    <a-popconfirm
                        v-if="PingZhengTypeDataSource.length"
                      title="你确定删除当前凭证类型数据吗?"
                      @confirm="onDelete(record.key,'TYPE')"
                    >
                      <a-button class="editable-add-btn" type="primary" size="small">-</a-button>
                    </a-popconfirm>
                  </template>
                </a-table>
              </div>
            </div>
          </div>
        </a-tab-pane>-->
<!--        <a-tab-pane key="4" :style="activeKey=='4'?{height: windowHeight+'px',overflowY: 'auto'}:{}" tab="结算方式">
          <div class="a-container-content-two">
            <div class="acct-up">
                <span>设置说明</span>
                <div>
                   <p>1、结算方式主要用于现金银行凭证使用，通过结算方式,可记录相关票据,比如用于银行对账两清对比识别</p>
                   <p>2、结算方式档案名称与默认账户可随时修改,结算方式编码一经过使用不可修改</p>
                </div>
            </div>
            <div class="acct-down">
                <span>结算方式</span>
              <div>
                <a-button class="editable-add-btn" type="primary" size="small" @click="handleAdd" style="margin-left: 15px;">+</a-button>
                <a-table size="small" bordered :data-source="JsDataSource" :columns="JsColumns" :pagination="false">
                 <template #settModesCode="{ text, record }">
                    <div class="editable-cell">
                      <div v-if="editableData[record.key]  && editableData[record.key].type == 'JS'" class="editable-cell-input-wrapper">
                        <a-input v-model:value="editableData[record.key].settModesCode" @pressEnter="save(record.key,'JS')" />
                        <check-outlined class="editable-cell-icon-check" @click="save(record.key,'JS')" />
                      </div>
                      <div v-else class="editable-cell-text-wrapper">
                        {{ text || ' ' }}
                        <edit-outlined class="editable-cell-icon" @click="edit(record.key,'JS')" />
                      </div>
                    </div>
                  </template>
                  <template #settModesName="{ text, record }">
                    <div class="editable-cell">
                      <div v-if="editableData[record.key]  && editableData[record.key].type == 'JS'" class="editable-cell-input-wrapper">
                        <a-input v-model:value="editableData[record.key].settModesName" @pressEnter="save(record.key,'JS')" />
                        <check-outlined class="editable-cell-icon-check" @click="save(record.key,'JS')" />
                      </div>
                      <div v-else class="editable-cell-text-wrapper">
                        {{ text || ' ' }}
                        <edit-outlined class="editable-cell-icon" @click="edit(record.key,'JS')" />
                      </div>
                    </div>
                  </template>
                  <template #beiyong1="{ text, record }">
                    <div class="editable-cell">
                      <div v-if="editableData[record.key]  && editableData[record.key].type == 'JS'" class="editable-cell-input-wrapper">
                        <a-select
                          show-search
                          option-filter-prop="children"
                          :filter-option="filterKeMuOption"
                          v-model:value="editableData[record.key].beiyong1"
                          style="width: 80%"
                        >
                          <a-select-option v-for="item in AiDataSource" :key="item.id" :value="item.corpBankCode">{{item.corpBankCode}} {{item.corpBankName}}</a-select-option>
                        </a-select>
                        <check-outlined class="editable-cell-icon-check" @click="save(record.key,'JS')" />
                      </div>
                      <div v-else class="editable-cell-text-wrapper">
                        {{ text || ' ' }}
                        <edit-outlined class="editable-cell-icon" @click="edit(record.key,'JS')" />
                      </div>
                    </div>
                  </template>
                  <template #beiyong2="{ text, record }">
                    <div class="editable-cell">
                      <div v-if="editableData[record.key]  && editableData[record.key].type == 'JS'" class="editable-cell-input-wrapper">
                        <a-input v-model:value="editableData[record.key].beiyong2" @pressEnter="save(record.key,'JS')" />
                        <check-outlined class="editable-cell-icon-check" @click="save(record.key,'JS')" />
                      </div>
                      <div v-else class="editable-cell-text-wrapper">
                        {{ text || ' ' }}
                        <edit-outlined class="editable-cell-icon" @click="edit(record.key,'JS')" />
                      </div>
                    </div>
                  </template>
                  <template #operation="{ record }">
                    <a-button class="editable-add-btn" type="primary" size="small" @click="openJsEdit(record)">✎</a-button>&nbsp;
                    <a-popconfirm
                      v-if="JsDataSource.length"
                      title="你确定删除当前结算方式数据吗?"
                      @confirm="onDelete(record.key,'JS')"
                    >
                      <a-button class="editable-add-btn" type="primary" size="small">-</a-button>
                    </a-popconfirm>
                  </template>
                </a-table>
              </div>
            </div>
          </div>
        </a-tab-pane>
               <a-tab-pane key="5" tab="账户信息">
          <div class="a-container-content-two">
            <div class="acct-up">
              <span>设置说明</span>
              <div>
                <p>1、账户用于银行对账或收付款处理，若启用银行对账功能，必须添加账户信息；</p>
                <p>2、使用第三方支付平台，如支付宝与微信，账号对应支付宝与微信号码。</p>
              </div>
            </div>
            <div class="acct-down">
              <span>账户信息</span>
              <div>
                <a-button class="editable-add-btn" type="primary" size="small" @click="handleAiAdd" style="margin-left: 15px;">+</a-button>
                <a-table size="small" bordered :data-source="AiDataSource" :columns="AiColumns" :pagination="false">
                 <template v-for="col in ['corpBankName', 'beiyong1', 'beiyong2', 'beiyong3']" #[col]="{ text, record }" :key="col">
                    <div class="editable-cell">
                      <div v-if="editableData[record.key]  && editableData[record.key].type == 'AI'" class="editable-cell-input-wrapper">
                        <a-input
                          v-model:value="editableData[record.key][col]"
                          style="margin: -5px 0"
                        />
                        <check-outlined class="editable-cell-icon-check" @click="save(record.key,'AI')" />
                      </div>
                      <div v-else class="editable-cell-text-wrapper">
                        {{ text || ' ' }}
                        <edit-outlined class="editable-cell-icon" @click="edit(record.key,'AI')" />
                      </div>
                    </div>
                  </template>
                  <template #ccode="{ text, record }">
                    <div class="editable-cell">
                      <div v-if="editableData[record.key]  && editableData[record.key].type == 'AI'" class="editable-cell-input-wrapper">
                        <a-select
                          show-search
                          option-filter-prop="children"
                          :filter-option="filterKeMuOption"
                          v-model:value="editableData[record.key].ccode"
                          style="width: 80%"
                        >
                          <a-select-option v-for="item in XjAndLlCodeList" :key="item.id" :value="item.ccode+'&#45;&#45;'+(item.ibank == null?'0':item.ibank)">{{item.ccode}} {{item.ccodeName}}</a-select-option>
                        </a-select>
                        <check-outlined class="editable-cell-icon-check" @click="save(record.key,'AI')" />
                      </div>
                      <div v-else class="editable-cell-text-wrapper">
                        {{ text || ' ' }}
                        <edit-outlined class="editable-cell-icon" @click="edit(record.key,'AI')" />
                      </div>
                    </div>
                  </template>
                  <template #operation="{ record }">
                    <a-button class="editable-add-btn" type="primary" size="small" @click="openPage(record,'AI')">✎</a-button>&nbsp;
                    <a-popconfirm
                      v-if="PingZhengTypeDataSource.length"
                      title="你确定删除当前账户信息数据吗?"
                      @confirm="onDelete(record.key,'AI')"
                    >
                      <a-button class="editable-add-btn" type="primary" size="small">-</a-button>
                    </a-popconfirm>
                  </template>
                </a-table>
              </div>
            </div>
          </div>
        </a-tab-pane>
        <a-tab-pane key="6" :style="activeKey=='6'?{height: windowHeight+'px',overflowY: 'auto'}:{}" tab="常用凭证">
          <div class="a-container-content-two">
            <div class="acct-up">
              <span>设置说明</span>
              <div>
                <p>1、常用凭证为经常使用到的凭证科目样式，用于填制凭证是引用，提高录入效率；</p>
                <p>2、常用凭证所使用的会计科目为初次设置科目，或与当前年度科目不符，参照时若不能带出科目，请修改我正确科目。</p>
              </div>
            </div>
            <div class="acct-down">
              <span>常用凭证</span>
              <div>
                <a-button class="editable-add-btn" type="primary" size="small" @click="handlePzAdd" style="margin-left: 15px;">+</a-button>
                <br/>
                <div style="width: 20%;display: inline-block;float: left;">
                   search
                  <BasicTree
                    title="常用凭证分类"
                    toolbar
                    :tree-data="keMuTreeData"
                    :click-row-to-expand="false"
                    @select="handleTreeSelect"
                  >
                  </BasicTree>
                </div>
                <div style="width: 80%;display: inline-block">
                  <a-table size="small" bordered :data-source="PzDataSource" :columns="PzColumns" :pagination="false">
                    <template v-for="col in ['accountName', 'bankAccount', 'bankCity', 'bankCode']" #[col]="{ text, record }" :key="col">
                      <div class="editable-cell">
                        <div v-if="editableData[record.key]   && editableData[record.key].type == 'PZ'" class="editable-cell-input-wrapper">
                          <a-input
                            v-model:value="editableData[record.key][col]"
                            style="margin: -5px 0"
                          />
                          <check-outlined class="editable-cell-icon-check" @click="save(record.key,'PZ')" />
                        </div>
                        <div v-else class="editable-cell-text-wrapper">
                          {{ text || ' ' }}
                          <edit-outlined class="editable-cell-icon" @click="edit(record.key,'PZ')" />
                        </div>
                      </div>
                    </template>
                    <template #ccode="{ text, record }">
                      <div class="editable-cell">
                        <div v-if="editableData[record.key]    && editableData[record.key].type == 'PZ'" class="editable-cell-input-wrapper">
                          <a-select
                            show-search
                            option-filter-prop="children"
                            :filter-option="filterKeMuOption"
                            v-model:value="editableData[record.key].ccode"
                            style="width: 80%"
                          >
                            <a-select-option v-for="item in QjDataSource" :key="item.id" :value="item.dateStart">{{item.dateStart}}</a-select-option>
                          </a-select>
                          <check-outlined class="editable-cell-icon-check" @click="save(record.key,'PZ')" />
                        </div>
                        <div v-else class="editable-cell-text-wrapper">
                          {{ text || ' ' }}
                          <edit-outlined class="editable-cell-icon" @click="edit(record.key,'PZ')" />
                        </div>
                      </div>
                    </template>
                  </a-table>
                </div>
              </div>
            </div>
          </div>
        </a-tab-pane>
        <a-tab-pane key="8" :style="activeKey=='8'?{height: windowHeight+'px',overflowY: 'auto'}:{}" tab="常用外币">
          <div class="a-container-content-two">
            <div class="acct-up">
              <span>设置说明</span>
              <div>
                <p>1、常用外币用于添加凭证处理中需要处理的各种外币；</p>
                <p>2、外币编码为国际标准编码，不能进行自定义修改。</p>
              </div>
            </div>
            <div class="acct-down">
              <span>外币设置</span>
              <div>
                <a-button class="editable-add-btn" type="primary" size="small" @click="handleCyAdd" style="margin-left: 15px;">+</a-button>
                <a-table size="small" bordered :data-source="CyDataSource" :columns="CyColumns" :pagination="false">
                  <template #foreignName="{ text, record }">
                    <div class="editable-cell">
                      <div v-if="editableData[record.key]  && editableData[record.key].type == 'CY'" class="editable-cell-input-wrapper">
                        <a-select
                          show-search
                          option-filter-prop="children"
                          :filter-option="filterKeMuOption"
                          v-model:value="editableData[record.key].foreignName"
                          style="width: 80%"
                          @change="CyChange($event,record.key)"
                        >
                          <a-select-option v-for="item in currencyTypeList" :key="item.id" :value="item.currencyZhCnName">{{item.currencyZhCnName}}</a-select-option>
                        </a-select>
                        <check-outlined class="editable-cell-icon-check" @click="save(record.key,'CY')" />
                      </div>
                      <div v-else class="editable-cell-text-wrapper">
                        {{ text || ' ' }}
                        <edit-outlined class="editable-cell-icon" @click="edit(record.key,'CY')" />
                      </div>
                    </div>
                  </template>
                  <template #operation="{ record }">
                    <a-popconfirm
                      v-if="JsDataSource.length"
                      title="你确定删除当前常用外币数据吗?"
                      @confirm="onDelete(record.key,'CY')"
                    >
                      <a-button class="editable-add-btn" type="primary" size="small">-</a-button>
                    </a-popconfirm>
                  </template>
                </a-table>
              </div>
            </div>
          </div>
        </a-tab-pane>
        <a-tab-pane key="7" :style="activeKey=='7'?{height: windowHeight+'px',overflowY: 'auto'}:{}" tab="外币汇率">
          <div class="a-container-content-two">
            <div class="acct-up">
              <span>外币参数</span>
              <br/>
                <a-button v-if="ifModify" class="editable-add-btn" type="primary" size="small" @click="handleWbModify" style="margin-right: 15px;float: right">保存</a-button>
              <br/>
              <div>
                <span class="acctd-span">&emsp;&emsp;外币：</span><a-select v-model:value="WbBasicInfo.foreignCode" @change="WbChange" style="width: 200px;text-align: center;margin-right: 30px">
                <a-select-option v-for="item in CyDataSource" :key="item.id" :value="item.foreignCode">
                  {{item.foreignCode}}-{{item.foreignName}}
                </a-select-option>
              </a-select>
                <span class="acctd-span">&emsp;本位币：</span><a-input v-model:value="WbBasicInfo.localCode" style="width: 200px;text-align: center;margin-right: 30px" />
                <span class="acctd-span">计算方式：</span>
                <a-select default-value="" v-model:value="WbBasicInfo.comMethod" style="width: 200px;text-align: center" :disabled="!ifModify">
                  <a-select-option value="*">原币*汇率=本位币</a-select-option>
                  <a-select-option value="/">原币/汇率=本位币</a-select-option>
                </a-select>
              </div>
              <div>
                <span class="acctd-span">汇率小数点精度：</span><a-select v-model:value="WbBasicInfo.decimalNum" default-value="7" :disabled="!ifModify" style="width: 158px;text-align: center;margin-right: 30px">
                <a-select-option value="2">2</a-select-option>
                <a-select-option value="3">3</a-select-option>
                <a-select-option value="4">4</a-select-option>
                <a-select-option value="5">5</a-select-option>
                <a-select-option value="6">6</a-select-option>
                <a-select-option value="7">7</a-select-option>
                <a-select-option value="8">8</a-select-option>
                <a-select-option value="9">9</a-select-option>
              </a-select>
                <span class="acctd-span">最大误差：</span>
                <a-input v-model:value="WbBasicInfo.errorValue" style="width: 200px;text-align: center;margin-right: 30px;pointer-events: auto" v-bind:readonly="!ifModify"  />
                <span class="acctd-span">取值方式：</span>
                <a-select default-value=""  v-model:value="WbBasicInfo.valueMethod" style="width: 200px;text-align: center" :disabled="!ifModify">
                  <a-select-option value="VOUCHER">
                    距离凭证日期最近
                  </a-select-option>
                  <a-select-option value="SYSTEM">
                    距离系统日期最近
                  </a-select-option>
                </a-select>
              </div>
              <div>
                <span class="acctd-span">会计年度：</span><a-select default-value="2021" v-model:value="WbBasicInfo.iyear" style="width: 200px;text-align: center;margin-right: 30px">
                <a-select-option value="2021">2021</a-select-option>
              </a-select>
                <span class="acctd-span">汇率设置：</span><a-select default-value="" v-model:value="WbBasicInfo.rateStyle"  @change="WbChange"  style="width: 200px;text-align: center;margin-right: 30px">
                <a-select-option value="MONTH">月汇率</a-select-option>
                <a-select-option value="DAY">日汇率</a-select-option>
              </a-select>
                <span class="acctd-span">集团控制：</span>
                    <a-input v-if="!ifModify"  default-value="控制" style="width: 200px;text-align: center;margin-right: 30px" />
                    <a-input v-else  default-value="不控制" style="width: 200px;text-align: center;margin-right: 30px" />
               <a-select default-value="" style="width: 200px;text-align: center">
                  <a-select-option value="">
                   不控制
                  </a-select-option>
                </a-select>
              </div>
            </div>
            <div class="acct-down">
              <span>汇率设置</span>
              <div>
                <span style="margin-right: 15px">当前外币: <span style="color: red">{{getWbName(WbBasicInfo.foreignCode)}}</span></span>
                <span v-if=" WbBasicInfo.rateStyle === 'DAY'">月份：</span>
                <a-month-picker
                  v-model:value="WbMonth"
                  v-if="WbBasicInfo.rateStyle === 'DAY'"
                  @change="WbChange"
                  valueFormat="MM"
                  format="MM"
                  placeholder=""
                  style="width: 80px"
                />
                <span v-if="ifModify && WbBasicInfo.rateStyle === 'DAY'">选择新增日期：</span>
                <a-select v-model:value="WbDay" v-if="ifModify && WbBasicInfo.rateStyle === 'DAY'">
                  <a-select-option v-for="value in getMonthDays(WbMonth)" :value="(value) > 9?value+'':'0'+(value)">{{(value) > 9?value+'':'0'+(value)}}</a-select-option>
                </a-select>
                <a-button v-if="ifModify" class="editable-add-btn" type="primary" size="small" @click="handleWbAdd(WbDay)" style="margin-left: 15px;">+</a-button>
                <a-table size="small" bordered :data-source="WbDataSource" :columns="WbColumns" :pagination="false" :scroll="{ y  : tableHeight }">
                  <template #rateBook="{ text, record }">
                    <div class="editable-cell">
                      <div v-if="editableData[record.key]   && editableData[record.key].type == 'WB'" class="editable-cell-input-wrapper">
                        <a-input v-model:value="editableData[record.key].rateBook" @pressEnter="save(record.key,'WB')" />
                        <check-outlined class="editable-cell-icon-check" @click="save(record.key,'WB')" />
                      </div>
                      <div v-else class="editable-cell-text-wrapper">
                        {{ text || ' ' }}
                        <edit-outlined class="editable-cell-icon" v-if="ifModify" @click="edit(record.key,'WB')" />
                      </div>
                    </div>
                  </template>
                  <template #operation="{ record }">
                    <a-popconfirm
                      v-if="ifModify"
                      title="你确定删除当前外币汇率数据吗?"
                      @confirm="onDelete(record.key,'WB')"
                    >
                      <a-button class="editable-add-btn" type="primary" size="small">-</a-button>
                    </a-popconfirm>
                  </template>
                </a-table>
              </div>
            </div>
          </div>
        </a-tab-pane>
        -->
        <a-tab-pane key="9" :style="activeKey=='9'?{height: windowHeight+'px',overflowY: 'auto'}:{}" >
          <template #tab>
            <span style="line-height: 30px;display: flex;color: white;">
            <ProjectOutlined  style="font-size: 30px;color: #666666;background-color: white;"/>
              数据精度
            </span>
          </template>
          <div class="a-container-content-two">
            <div class="acct-up">
              <span>设置说明</span>
              <div>
                <p>1、数据精度用于数量和外币核算科目录入凭证分录控制；</p>
                <p>2、若控制方式设置为严格控制，凭证录入时必须按照设置精度进行数据处理。</p>
              </div>
            </div>
            <div class="acct-down">
              <span>精度设置</span>
              <div>
                <a-button class="editable-add-btn" type="primary" size="small" @click="handleCsAdd" style="margin-left: 15px;">+</a-button>
                <a-table size="small" bordered :data-source="CsDataSource" :columns="CsColumns" :pagination="false" :scroll="{ y  : tableHeight }">
                  <template #paraName="{ text, record }">
                                       <div class="editable-cell">
                    <div v-if="editableData[record.key] && editableData[record.key].type == 'CS'" class="editable-cell-input-wrapper">
                      <a-input v-model:value="editableData[record.key].paraName" @pressEnter="save(record.key,'CS')" />
                      <check-outlined class="editable-cell-icon-check" @click="save(record.key,'CS')" />
                    </div>
                    <div v-else class="editable-cell-text-wrapper">
                      {{ text || ' ' }}
                      <edit-outlined class="editable-cell-icon" @click="edit(record.key,'CS')" />
                    </div>
                  </div>
                  </template>
                  <template #beiyong1="{ text, record }">
                    <div class="editable-cell">
                      <div v-if="editableData[record.key]   && editableData[record.key].type == 'CS'" class="editable-cell-input-wrapper">
                        <a-select v-model:value="editableData[record.key].beiyong1" style="width: 80%">
                          <a-select-option v-for="value in (8+1)" :value="value-1">{{value-1}}</a-select-option>
                        </a-select>
                        <check-outlined class="editable-cell-icon-check" @click="save(record.key,'CS')" />
                      </div>
                      <div v-else class="editable-cell-text-wrapper">
                        {{ text || ' ' }}
                        <edit-outlined v-if="record.groupControl == '0'" class="editable-cell-icon" @click="edit(record.key,'CS')" />
                      </div>
                    </div>
                  </template>
                  <template #decimalPlaces="{ text, record }">
                    <div class="editable-cell">
                      <div v-if="editableData[record.key]   && editableData[record.key].type == 'CS'" class="editable-cell-input-wrapper">
                        <a-select v-model:value="editableData[record.key].decimalPlaces" style="width: 80%">
                          <a-select-option v-for="value in (8+1)" :value="value-1">{{value-1}}</a-select-option>
                        </a-select>
                        <check-outlined class="editable-cell-icon-check" @click="save(record.key,'CS')" />
                      </div>
                      <div v-else class="editable-cell-text-wrapper">
                        {{ text || ' ' }}
                        <edit-outlined v-if="record.groupControl == '0'" class="editable-cell-icon" @click="edit(record.key,'CS')" />
                      </div>
                    </div>
                  </template>
                  <template #controlManner="{ text, record }">
                    <div class="editable-cell">
                      <div v-if="editableData[record.key] && editableData[record.key].type == 'CS' && record.groupControl == '0'" class="editable-cell-input-wrapper">
                        <a-select v-model:value="editableData[record.key].controlManner" style="width: 80%">
                          <a-select-option value="1">严格控制</a-select-option>
                          <a-select-option value="0">不控制</a-select-option>
                          <check-outlined class="editable-cell-icon-check" @click="save(record.key,'CS')" />
                        </a-select>
                      </div>
                      <div v-else class="editable-cell-text-wrapper">
                        {{ (text=='1'?'严格控制':'不控制') || ' ' }}
                        <edit-outlined v-if="record.groupControl == '0'" class="editable-cell-icon" @click="edit(record.key,'CS')" />
                      </div>
                    </div>
                  </template>
<!--                  <template #groupControl="{ text, record }">
                                       <div class="editable-cell">
                    <div v-if="editableData[record.key] && editableData[record.key].type == 'CS'" class="editable-cell-input-wrapper">
                      <a-select v-model:value="editableData[record.key].groupControl" style="width: 80%">
                        <a-select-option value="1">管控</a-select-option>
                        <a-select-option value="0">不管控</a-select-option>
                      </a-select>
                      <check-outlined class="editable-cell-icon-check" @click="save(record.key,'CS')" />
                    </div>
                    <div v-else class="editable-cell-text-wrapper">
                      {{ (text=='1'?'管控':'不管控') || ' ' }}
                      <edit-outlined class="editable-cell-icon" @click="edit(record.key,'CS')" />
                    </div>
                  </div>
                  </template>-->
                  <template #operation="{ record }">
                    <a-popconfirm
                      v-if="CsDataSource.length"
                      title="你确定删除当前数据精度吗?"
                      @confirm="onDelete(record.key,'CS')"
                    >
                      <a-button class="editable-add-btn" type="primary" size="small">-</a-button>
                    </a-popconfirm>
                  </template>
                </a-table>
              </div>
            </div>
          </div>
        </a-tab-pane>
<!--        <a-tab-pane key="10" :style="activeKey=='10'?{height: windowHeight+'px',overflowY: 'auto'}:{}" tab="数据控制">
          <div class="a-container-content-one">
            <div style="margin-top: 10px;width: 500px;float: left;margin-left: 10px;">
              <a-button v-if="isEdit" type="primary" @click="codeAuthSwithBtn('0')">编辑</a-button>
              <template v-if="!isEdit">
                <a-button @click="codeAuthSwithBtn('1')">恢复系统设置</a-button>&nbsp;&nbsp;
                <a-button type="primary" @click="codeAuthSwithBtn('2')">保存</a-button>&nbsp;&nbsp;
                <a-button @click="codeAuthSwithBtn('3')">放弃</a-button>
              </template>
            </div><br>
            <div class="acco-sideline" style="margin-top: 40px;width: 500px;">
              <span>数据权限控制</span>
              <div  class="acco-sideline-content-two">
                <a-checkbox-group style="width: 100%;" v-model:value="codeAllAuth" v-model:disabled="codeAllDisabled">
                  <a-row>
                    <a-col :span="10">
                      <a-checkbox value="code" >
                        全部会计科目
                      </a-checkbox>
                    </a-col>

                    <a-col :span="10">
                      <a-checkbox value="pztype" >
                        全部凭证类别
                      </a-checkbox>
                    </a-col>
                  </a-row>
                </a-checkbox-group>
              </div>
            </div>
          </div>
        </a-tab-pane>-->
      </a-tabs>
    </div>
    <Edit @save="modifyData" @register="registerEditPage" />
  </div>
</template>
<script setup="props, {emit}" lang="ts">
import {
  Select as ASelect,
  Input as AInput,
  Radio as ARadio,
  Tabs as ATabs,
  Checkbox as ACheckbox,
  Row as ARow,
  Col as ACol,
  Button as AButton,
  message,
  Table as ATable,
  DatePicker as ADatePicker,
  Tree as ATree,
  Popconfirm as APopconfirm
} from "ant-design-vue"
import router from "/@/router";
const ATabPane = ATabs.TabPane
const ASelectOption = ASelect.Option
const ACheckboxGroup = ACheckbox.Group
const ARadioGroup = ARadio.Group
const ATreeNode = ATree.TreeNode
const AMonthPicker = ADatePicker.MonthPicker
import {onMounted, reactive, ref, watch} from "vue";
import Edit from './popup/edit.vue';
import {
  SortDescendingOutlined,
  SortAscendingOutlined,
  CheckOutlined,
  OrderedListOutlined,
  FormOutlined,
  DeleteOutlined,
  SettingFilled,
  SyncOutlined,
  PicLeftOutlined,
  PieChartFilled, FilterFilled,
  EditOutlined,
  DownOutlined, SmileOutlined, FrownOutlined, FrownFilled,AppstoreOutlined,ProfileOutlined,ProjectOutlined,SettingOutlined
} from '@ant-design/icons-vue'
import {
  initBasisTabAccoutData,
  initBasisTabData,
  initBasisTabDataDefault,
  modifyBasisTabDBData, modifyPeriodDBDataByAccId, initNextPeriodByCodeAndYaer,
  currentAccountQjInfo,
  currentAccountYaerQjAndJdList,
  saveQjJdQuarterly,
  saveSystemQuarterly,
  currentAccountTypes,
  saveVoucherData,
  delVoucherData,
  delAccountData,
  currentCyDatas,
  findCurrencyTypeList,
  saveCurrencyData,
  deleteCurrencyData,
  deleteWbData,
  deleteCsData,
  saveCsData,
  findCurrentWbhvInfo,
  findAllWbhvEntrys,
  saveWbhvData,
  saveWbhvEntrys,
  findAllXjOrLlList, findAllMJList,
  saveAIData, findCorpBankAll, parameterAccuracyDatas, defaultSettingModel, checkPeriod
} from '/@/api/record/system/financial-settings'
const windowHeight  = (document.documentElement.clientHeight)
const {
  createErrorModal,createWarningModal,createConfirm
} = useMessage()

import {
  compareDate,
  compareTime,
  findByFunctionModule,
  markAnomaly,
  offsetToStr,
  serverTime,
  getCurrentMonthLast,
  getCurrentAccountName, hasBlank
} from "/@/api/task-api/tast-bus-api";
import {useMessage} from "/@/hooks/web/useMessage";
import {computed} from "vue";
import {cloneDeep} from "lodash-es";
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker.vue";
const activeKey= ref('1')
const database = ref('')
const databaseCn = ref('')
const databaseYear = ref('')
const databaseCode = ref('')
const [registerEditPage, { openModal: openEditPage }] = useModal();
const openPage = (data,type) => {
  data.type=type
  openEditPage(true, {
    data: data,
  });
};
// 数据权限控制表 ID
const authSwithId=ref(null)
/************************************基础选项************************************/
const isEdit = ref(true)
const isEdit2 = ref(true)
const pageParameter = reactive({
  acountStandardList: [],
  levelList: [],
  currencyList: []
})
const { closeCurrent } =useTabs();
const dataModel = reactive(defaultSettingModel)

const dataModelCopy = reactive(defaultSettingModel)

onMounted(()=>{
  // pageReload()
})

const pageReload = ()=>{
  // 初始化选项一的默认数据
  initBasisTabAccoutData().then(res=>{
    pageParameter.acountStandardList = res.acountStandardList
    pageParameter.levelList = res.levelList
    //pageParameter.currencyList = res.currencyList
  })
    .catch(error => {
      console.log('error', error)
    })
  findCurrencyTypeList({}).then(res=>{
    currencyTypeList.value = res.items
  })

  initBasisTabData({'accId': databaseCn.value}).then(res=>{
    if (res.basisMap.length != 0) {
      dataModel.basisMap = res.basisMap
      dataModelCopy.basisMap = res.basisMap
      standardChange(dataModel.basisMap.accStandard)
      if (res.zhiDanList.length != 0) {
        dataModel.zhiDanList = res.zhiDanList
        dataModelCopy.zhiDanList = res.zhiDanList
      }
      if (res.pingZhenNumberList.length != 0){
        dataModel.pingZhenNumberList = res.pingZhenNumberList
        dataModelCopy.pingZhenNumberList = res.pingZhenNumberList}
    }
    if (res.pingZhenControlList.length != 0) {
      dataModel.pingZhenControlList = res.pingZhenControlList
      dataModelCopy.pingZhenControlList = res.pingZhenControlList
    }
    if (res.settingList.length != 0) {
      dataModel.settingList = res.settingList
      dataModelCopy.settingList = res.settingList
    }
    if (Object.keys(res.dateMap).length != 0) {
      dataModel.dateMap = res.dateMap
      dataModelCopy.dateMap = res.dateMap
    }
  }).catch(error => {
    console.log('error', error)
  })
}

const handleBus = (bus)=>{
  if(bus == '0'){
    isEdit.value=false
  }else if (bus == '1'){
    let data = initBasisTabDataDefault();
    dataModel.basisMap = data.basisMap
    dataModel.zhiDanList = data.zhiDanList
    dataModel.pingZhenNumberList = data.pingZhenNumberList
    dataModel.pingZhenControlList = data.pingZhenControlList
    dataModel.settingList = data.settingList
    dataModel.dateMap = data.dateMap
  }else if(bus == '2'){
    isEdit.value=true

    dataModel.basisMap.ccodeLevel = splitLevel(levelPrefix.value, levelSuffix.value)
    modifyBasisTabDBData({modifyModel: JSON.stringify(dataModel),accId: databaseCn.value}).then(res=>{
      console.log('保存成功!')
      pageReload()
    }).catch(error => {
      console.log('error', error)
    })
  }else{
    dataModel.basisMap = dataModelCopy.basisMap
    dataModel.zhiDanList = dataModelCopy.zhiDanList
    dataModel.pingZhenNumberList = dataModelCopy.pingZhenNumberList
    dataModel.pingZhenControlList = dataModelCopy.pingZhenControlList
    dataModel.settingList = dataModelCopy.settingList
    dataModel.dateMap = dataModelCopy.dateMap
    isEdit.value=true
  }
}
const handleBusQj = (bus)=>{
  if(bus == '0'){
    isEdit2.value=false
  }else if(bus == '2'){
    isEdit2.value=true
    // 组装数据添加
    if (QjDataSourceCopy[0].dateEnd == QjDataSource.value[0].dateEnd) return false;
    let data = []
    QjDataSource.value.forEach(item=>{
      data.push({period: item.yearMonth,dateStart: item.dateStart.replace(databaseYear.value+"-",""),dateEnd: item.dateEnd.replace(databaseYear.value+"-","")})
    })
    modifyPeriodDBDataByAccId({modifyModel: JSON.stringify(data),accId: databaseCn.value})
      .then(res=>{
      console.log('保存成功!')
      yearChange()
    }).catch(error => {
      console.log('error', error)
    })
  }else if(bus == '3'){
    let data={year: databaseYear.value,accId: databaseCn.value,periodNum: QjBasicInfo.value.periodNum,coCode: databaseCode.value}
    initNextPeriodByCodeAndYaer(data).then(res=>{
     if (res != null){
       message.success('成功初始化下一年度!')
     }else {
       message.warning('下一年度已被初始化!')
     }
    }).catch(error => {
      message.error('参数异常!')
    })
  }else{
    QjDataSource.value = QjDataSourceCopy
    isEdit2.value=true
  }
}

const beforeShow = () => {
    // 进页面是否存在查看选项权限  你无此功能的查看权限，请申请管理员授权后再进行操作。
    // 进选项页面点击编辑是否存在权限   你无此功能的编辑权限，请申请管理员授权后再进行操作。
    // 校验是否 被锁定
    let msg = '';
    // 终止for循环，使用break
    findByFunctionModule(2021).then(res=>{
      let checkMenu = ['月末结账','凭证记账','凭证主管签字','审核凭证','出纳凭证签字','凭证的增加修改整理','转账生成']
      for (let i = 0; i < res.length; i++) {
        if (checkMenu.indexOf(res[i].functionModule) != -1 && res[i].state == '1'){
          // 校验时间
          if (res[i].time != '' && !compareTime(offsetToStr(res[i].time))){ // 超时
            msg = '提示：系统冲突！操作员【' +
              res[i].caozuoUnique +
              '】正在进行'+res[i].functionModule+'操作!不能继续进行财务选项编辑操作，请销后再试，或联系财务主管清理该记账任务!'
            return false;
          }else {
            // 标记异常
            markAnomaly(res[i].id,res[i].iyear);
          }
        }
      }
      if (msg != ''){
        createWarningModal({title: '模板导入检测',content: msg })
        return false
      }else {
        return  true;
      }
    })
  return false;
}
const levelPrefix = ref('')
const levelSuffix = ref('')
const standardChange = async (val) => {
  let obj = pageParameter.acountStandardList.filter(item => item.id == val)
  if (obj.length > 0) {
    levelPrefix.value = obj[0].tjici/*.replace(/-/g,"")*/
    if (dataModel.basisMap.ccodeLevel != '' && isEdit.value) {
      levelSuffix.value = dataModel.basisMap.ccodeLevel.replace(obj[0].tjici, '').replace(/-/g, "").split('').join('-')
    }
  }
}
const importLimit = (e) => {
  e.target.value = e.target.value.replace(/[^123456789]/g, "")
  if (!hasBlank(e.target.value)) e.target.value = e.target.value.split('').join('-')
  levelSuffix.value = e.target.value
}
const splitLevel = (a, b) => {
  let c = a
  if (b != '')
    c = c + '-' + b
  return c
}
/************************************基础选项************************************/
/************************************结算方式************************************/
import {findSettModesAll,saveSettModes,deleteSettModes} from "/@/api/record/system/sett-modes";
const JsColumns = [
  {
    title: '序号',
    key: 'serialNumber',
    dataIndex: 'serialNumber',
    width: '10%'
  },
  {
    title: '编码',
    key: 'settModesCode',
    dataIndex: 'settModesCode',
    width: '15%',
    slots: { customRender: 'settModesCode' },
  },
  {
    title: '结算方式名称',
    key: 'settModesName',
    dataIndex: 'settModesName',
    width: '20%',
    slots: { customRender: 'settModesName' },
  },
  {
    title: '默认账户',
    key: 'beiyong1',
    dataIndex: 'beiyong1',
    width: '15%',
    slots: { customRender: 'beiyong1' },
  },{
    title: '说明',
    key: 'beiyong2',
    dataIndex: 'beiyong2',
    width: '30%',
    slots: { customRender: 'beiyong2' },
  }, {
    title: '删除',
    dataIndex: 'operation',
    width: '10%',
    align: 'center',
    slots: { customRender: 'operation' },
  },
];
const JsDataSource= ref([
  {
    key: '0',
    serialNumber: '1',
    settModesCode: '',
    settModesName: '',
    beiyong1: '',
    beiyong2: '',
  },{
    key: '1',
    serialNumber: '2',
    settModesCode: '',
    settModesName: '',
    beiyong1: '',
    beiyong2: '',
  }
  ]);
const count = computed(() => JsDataSource.value.length + 1);
const editableData = reactive({});

const handleAdd = () => {
 let a = null == JsDataSource.value?0:JsDataSource.value.length
  if (a == 12){
    return
  }
  const newData = {
    key: `${a}`,
    serialNumber: `${a+1}`,
    settModesCode: '',
    settModesName: '',
    beiyong1: '',
    beiyong2: '',
    list: []
  };
  JsDataSource.value.push(newData);
  newData.list = AiDataSource.value
  openPage(newData,'JS')
};
function checkJsData(data: any) {
  try {
    if (data.corpBankName == '' || data.ccode == ''){
      throw new Error('请完善当前结算信息！')
    }
    if (JsDataSource.value.filter(item => item.settModesCode.trim() === data.settModesCode.trim()).length > 1){
      throw new Error('结算编码已存在！')
    }
    if (JsDataSource.value.filter(item => item.settModesName.trim() === data.settModesName.trim()).length > 1){
      throw new Error('结算名称已存在！')
    }
    return  true
  }catch (e) {
    createWarningModal({content: e.message});
    return false
  }
}
const openJsEdit = (data)=>{
  data.list = AiDataSource.value
  openPage(data,'JS')
}
/************************************结算方式************************************/
/************************************会计期间************************************/
const fiscalYear = ref('')
const fiscalYear2 = ref('')

const QjBasicInfo = ref({
  startPeriod: '2021-01',periodNum:'12',periodMonth: '01-01'
})
const QjBasicInfo_quarterlyNumber = ref(4)
const QjDataSource= ref([{
  key: '0',
  serialNumber: '1',
  iperiodNum: '1',
  yearMonth: '202101',
  dateStart: '2021-01-01',
  dateEnd: '2021-01-31',
}]);
let QjDataSourceCopy = []
const QjJdDataSource= ref([
  {
    key: '0',
    serialNumber: '1',
    iperiodNum: '1',
    yearMonth: '202101',
    dateStart: '2021-01-01',
    dateEnd: '2021-01-31',
    iyear: '2021',
    beiyong1: '1',
    uniqueCode: '1',
    enablePeriod: '0',
    accountId: databaseCn.value,
  }
]);
const QjHandleChange = (value)=>{
  let number =  QjJdDataSource.value.length
  if (value > number){ // 加
    let s = (value - number)
    let thisYear = QjBasicInfo.value.startPeriod.substring(0,4)
    for (let index = 0; index < s; index++) {
      let thisNumber  = (number+index+1).toString()
      QjJdDataSource.value.push(
        {
          key: `${number+index}`,
          serialNumber: thisNumber,
          iperiodNum: thisNumber,
          uniqueCode: thisNumber,
          yearMonth: thisYear+(thisNumber.length==2?thisNumber:('0'+thisNumber)),
          dateStart: '',
          dateEnd: '',
          iyear: thisYear,
          beiyong1: '1',
          enablePeriod: '0',
          accountId: databaseCn.value,
        }
      )
    }
  }else { //减
    let s = (number - value)
    for (let index = 0; index < s; index++) {
      QjJdDataSource.value.pop()
    }
  }
}
const initQjColumns = (type)=>{
   let flag = type == '1'?true:false
   return [
    {
      title: '序号',
      key: 'serialNumber',
      dataIndex: 'serialNumber',
      align: 'center',
      width: '10%'
    },
    {
      title: flag?'会计期间':'季度区间',
      key: 'iperiodNum',
      dataIndex: 'iperiodNum',
      align: 'center',
      width: '15%',
    },
    {
      title: flag?'系统期间值':'季度区间值',
      key: 'yearMonth',
      dataIndex: 'yearMonth',
      align: 'center',
      width: '25%',
    },
    {
      title: '开始日期',
      key: 'dateStart',
      dataIndex: 'dateStart',
      align: 'center',
      width: '25%',
      slots: { customRender: 'dateStart' },
    },{
      title: '结束日期',
      key: 'dateEnd',
      dataIndex: 'dateEnd',
      align: 'center',
      width: '25%',
       slots: { customRender: 'dateEnd' },
    },
  ]
}
const yearChange = ()=>{
  let data = {
    maxYear: fiscalYear.value+'--'+fiscalYear2.value
  }
  initDataSource(data,databaseCn)
}
const initDataSource = (data,name)=>{
    //数据
  let thisYear = data.maxYear
  // 获取当前期间 以及 季度
  currentAccountYaerQjAndJdList({accId: name,iyear: thisYear}).then(res=>{
    QjDataSource.value = []
    res.t1.forEach((item,index)=>{
      item.key = `${index}`
      item.serialNumber = `${index+1}`
      item.yearMonth = fiscalYear.value+(item.iperiodNum.length==2?item.iperiodNum:('0'+item.iperiodNum))
      item.dateStart = fiscalYear.value+'-'+item.dateStart
      item.dateEnd = fiscalYear.value+'-'+item.dateEnd
      QjDataSource.value.push(item)
    })
    QjDataSourceCopy = JsonTool.parseProxy(QjDataSource.value)
    QjJdDataSource.value = []
    if (res.t2.length == 0){
      let number = QjBasicInfo_quarterlyNumber.value;
      for (let index = 0; index < number; index++) {
        let thisNumber  = (index+1).toString()
        QjJdDataSource.value.push(
          {
            key: `${index}`,
            serialNumber: thisNumber,
            iperiodNum: thisNumber,
            uniqueCode: thisNumber,
            yearMonth: fiscalYear2.value+(thisNumber.length==2?thisNumber:('0'+thisNumber)),
            dateStart: '',
            dateEnd: '',
            iyear: fiscalYear2.value,
            beiyong1: '1',
            enablePeriod: '0',
            accountId: name,
          }
        )
      }
    }else {
      QjBasicInfo_quarterlyNumber.value = res.t2.length
      res.t2.forEach((item,index)=>{
        item.key = `${index}`
        item.serialNumber = `${index+1}`
        item.yearMonth = fiscalYear2.value+(item.iperiodNum.length==2?item.iperiodNum:('0'+item.iperiodNum))
        item.uniqueCode  = item.iperiodNum
        QjJdDataSource.value.push(item)
      })
    }
  })
}
const checkQjKdData = ()=>{
  let list = QjJdDataSource.value
  try {
    // 先校验是否填充
    list.forEach(item=>{
      if (item.dateStart== '' || item.dateEnd== ''){
        throw new Error('请继续完善季度区间值'+item.yearMonth+'的开启与结束区间!')
      }
    })
    // 后校验日期规则
    for (let index = 0; index < list.length; index++) {
      let item =  list[index]
        if (compareDate(item.dateStart,item.dateEnd)){
          throw new Error('数据不规范！季度区间值'+item.yearMonth+'的开始区间不能大于结束期间,请重新修改!')
        }
        if (0 != index && compareDate(list[index-1].dateEnd,item.dateStart)){
          throw new Error('数据不规范！季度区间值'+item.yearMonth+'的开始区间不能小于上一季度结束期间,请重新修改!')
        }
    }
    return true
  }catch (e) {
    if (e.message.indexOf('数据不规范！') != -1){
      createWarningModal({content: e.message});
    }else{
     message.success(e.message)
    }
    return false
  }
}
/************************************会计期间************************************/
/************************************凭证类型************************************/
const PingZhengTypeInfo=ref({
  voucherTypeCode: '',
  voucherTypeName: '',
  limitClass: '',
})
const PingZhengTypeDataSource= ref([{
  key: '0',
  serialNumber: '1',
  voucherTypeCode: '',
  voucherTypeName: '',
  limitClass: '',
  limitKemu: '',
  beiyong1: '',
}]);
const PingZhengTypeColumns= [
  {
    title: '序号',
    key: 'serialNumber',
    dataIndex: 'serialNumber',
    align: 'center',
    width: '8%'
  },
  {
    title: '凭证字',
    key: 'voucherTypeCode',
    dataIndex: 'voucherTypeCode',
    align: 'center',
    width: '10%',
    slots: { customRender: 'voucherTypeCode' },
  },
  {
    title: '凭证类型',
    key: 'voucherTypeName',
    dataIndex: 'voucherTypeName',
    align: 'center',
    width: '20%',
    slots: { customRender: 'voucherTypeName' },
  },
  {
    title: '限制类型',
    key: 'limitClass',
    dataIndex: 'limitClass',
    align: 'center',
    width: '20%',
    slots: { customRender: 'limitClass' },
  },{
    title: '限制科目',
    key: 'limitKemu',
    dataIndex: 'limitKemu',
    align: 'center',
    width: '34%',
    slots: { customRender: 'limitKemu' },
  }, {
    title: '操作',
    dataIndex: 'operation',
    width: '8%',
    align: 'center',
    slots: { customRender: 'operation' },
  },
]
const handleTypeAdd = ()=>{
  let a = null == PingZhengTypeDataSource.value?0:PingZhengTypeDataSource.value.length
  if (a == 12){
    return
  }
  const newData = {
    key: `${a}`,
    serialNumber: `${a+1}`,
    voucherTypeCode: '',
    voucherTypeName: '',
    limitClass: '',
    limitKemu: '',
    beiyong1: '',
  };
  PingZhengTypeDataSource.value.push(newData)
  //打开弹框
  openPage(newData,'TYPE')
}
const typeHandleChange = (value)=>{
  let data =PingZhengTypeDataSource.value.filter(item=>item.voucherTypeCode==value)[0]
  PingZhengTypeInfo.value.voucherTypeName = data.voucherTypeName
  PingZhengTypeInfo.value.limitClass = data.limitClass
  // 是否设置默认
  let list = typeModifyDefalutList();
  if ((list.filter(i=>i.voucherTypeCode == data.voucherTypeCode).length) == 0){
    createConfirm({
      iconType: 'warning',
      title: '默认',
      content: '是否将该类型设置为默认类型!',
      onOk: async() => {
        list.forEach(item=>{
          item.beiyong1 = ''
          saveVoucherData(item)
        })
        data.beiyong1 = '1'
        await saveVoucherData(data).then(res=>{
          if (res.message === 'ok' || res.length > 0){
            message.success('系统已同步！')
          }else {
            message.error('系统同步失败！')
          }
        })
      }
    });
  }
}

const typeModifyDefalutList=()=>{
  return  PingZhengTypeDataSource.value.filter(item=>item.beiyong1=='1')
}

const checkTypeData = (data)=>{
  try {
      if (data.voucherTypeCode == '' || data.voucherTypeName == ''){
        throw new Error('凭证字与凭证类型为必填项,不可为空！')
      }
      if (PingZhengTypeDataSource.value.filter(item => item.voucherTypeCode === data.voucherTypeCode).length > 1){
        throw new Error('凭证字已存在！')
      }
      if (PingZhengTypeDataSource.value.filter(item => item.voucherTypeName === data.voucherTypeName).length > 1){
        throw new Error('凭证类型已存在！')
      }
      return  true
  }catch (e) {
    createWarningModal({content: e.message});
    return false
  }
}
/************************************凭证类型************************************/
/************************************账户信息************************************/
const XjAndLlCodeList = ref([])
const AiDataSource= ref([{
  key: '0',
  serialNumber: '1',
  corpBankName: '',
  ccode: '',
  beiyong1: '',
  beiyong2: '',
  beiyong3: '',
}]);
const AiColumns= [
  {
    title: '序号',
    key: 'serialNumber',
    dataIndex: 'serialNumber',
    align: 'center',
    width: '6%'
  },
  {
    title: '账户名称',
    key: 'corpBankName',
    dataIndex: 'corpBankName',
    align: 'left',
    width: '12%',
    slots: { customRender: 'corpBankName' },
  },
  {
    title: '对应科目',
    key: 'ccode',
    dataIndex: 'ccode',
    align: 'center',
    width: '20%',
    slots: { customRender: 'ccode' },
  },
  {
    title: '开户机构',
    key: 'beiyong1',
    dataIndex: 'beiyong1',
    align: 'left',
    width: '12%',
    slots: { customRender: 'beiyong1' },
  },{
    title: '开户地',
    key: 'beiyong2',
    dataIndex: 'beiyong2',
    align: 'left',
    width: '20%',
    slots: { customRender: 'beiyong2' },
  },
  {
    title: '账户',
    key: 'beiyong3',
    dataIndex: 'beiyong3',
    align: 'left',
    width: '20%',
    slots: { customRender: 'beiyong3' },
  },
  {
    title: '删除',
    dataIndex: 'operation',
    width: '10%',
    align: 'center',
    slots: { customRender: 'operation' }
  }
]
const handleAiAdd = ()=>{
  let a = null == AiDataSource.value?0:AiDataSource.value.length
  if (a == 12){
    return
  }
  const newData = {
    key: `${a}`,
    serialNumber: `${a+1}`,
    corpBankName: '',
    ccode: '',
    beiyong1: '',
    beiyong2: '',
    beiyong3: '',
    ibank: ''
  };
  AiDataSource.value.push(newData)
  //打开弹框
  openPage(newData,'AI')
}
const filterKeMuOption = (input: string, option: any) => {
  return option.value.toLowerCase().indexOf(input.toLowerCase()) >= 0;
}


function checkAiData(data: any) {
  try {
    if (data.corpBankName == '' || data.ccode == '' /*|| data.beiyong1 == '' ||  data.beiyong3==''*/){
      throw new Error('请完善当前账户信息！')
    }
    if (AiDataSource.value.filter(item => item.corpBankName.trim() === data.corpBankName.trim()).length > 1){
      throw new Error('账户名称已存在！')
    }
    return  true
  }catch (e) {
    createWarningModal({content: e.message});
    return false
  }
}

/************************************账户信息************************************/
/************************************常用凭证************************************/
const PzDataSource= ref([{
  key: '0',
  serialNumber: '1',
  ccode: '001',
  pingZhengDesc: '',
  pingZhengSummary: '',
  pingZhengCharacter: '',
  pingZhengType: '',
},{
  key: '1',
  serialNumber: '2',
  ccode: '002',
  pingZhengDesc: '',
  pingZhengSummary: '',
  pingZhengCharacter: '',
  pingZhengType: '',
},{
  key: '2',
  serialNumber: '2',
  ccode: '003',
  pingZhengDesc: '',
  pingZhengSummary: '',
  pingZhengCharacter: '',
  pingZhengType: '',
}]);

const PzColumns= [
  {
    title: '序号',
    key: 'serialNumber',
    dataIndex: 'serialNumber',
    align: 'center',
    width: '10%'
  },
  {
    title: '编码',
    key: 'ccode',
    dataIndex: 'ccode',
    align: 'center',
    width: '15%',
  },
  {
    title: '凭证说明',
    key: 'pingZhengDesc',
    dataIndex: 'pingZhengDesc',
    align: 'center',
    width: '25%',
    slots: { customRender: 'pingZhengDesc' },
  },
  {
    title: '凭证摘要',
    key: 'pingZhengSummary',
    dataIndex: 'pingZhengSummary',
    align: 'center',
    width: '25%',
    slots: { customRender: 'pingZhengSummary' },
  },
  {
    title: '凭证字',
    key: 'pingZhengCharacter',
    dataIndex: 'pingZhengCharacter',
    align: 'center',
    width: '10%',
    slots: { customRender: 'pingZhengCharacter' },
  },
  {
    title: '类别',
    key: 'pingZhengType',
    dataIndex: 'pingZhengType',
    align: 'center',
    width: '15%',
    slots: { customRender: 'pingZhengType' },
  }
]

import { BasicTree } from '/@/components/Tree'
import { keMuTreeData } from './data'
import {carryoverDeleteApi} from "/@/api/record/system/definition-correspond";
import {pushLog} from "/@/utils/logs/logs";
import moment, { Moment } from 'moment';
import {useModal} from "/@/components/Modal";
import {useCompanyOperateStoreWidthOut} from "/@/store/modules/operate-company";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {DateTool, JsonTool} from "/@/api/task-api/tools/universal-tools";
import {
  delById,
  findByAccIdAndIyear,
  sysDataAuthSwithSave
} from "/@/api/record/system/sys_data_auth_swith";
import {useTabs} from "/@/hooks/web/useTabs";
const handleTreeSelect = (keys: string, e)=> {
  //keys[0]
}
const handlePzAdd = ()=>{
  let a = null == PzDataSource.value?0:PzDataSource.value.length
  if (a == 12){
    return
  }
  const newData = {
    key: `${a}`,
    serialNumber: `${a+1}`,
    ccode: (a+1)<10?('00'+(a+1)):(a+1)<100?('0'+(a+1)):(a+1)+'',
    pingZhengDesc: '',
    pingZhengSummary: '',
    pingZhengCharacter: '',
    pingZhengType: '',
  };
  PzDataSource.value.push(newData)
}
/************************************常用凭证************************************/
/************************************常用外币************************************/
const CyDataSource= ref([{
  key: '0',
  serialNumber: '1',
  foreignCode: 'RMB',
  foreignName: '人民币',
  foreignSimpName: '元',
}]);
const CyColumns= [
  {
    title: '序号',
    key: 'serialNumber',
    dataIndex: 'serialNumber',
    align: 'center',
    width: '10%'
  },
  {
    title: '外币名称',
    key: 'foreignName',
    dataIndex: 'foreignName',
    align: 'center',
    width: '30%',
    slots: { customRender: 'foreignName' },
  },
  {
    title: '外币编码',
    key: 'foreignCode',
    dataIndex: 'foreignCode',
    align: 'center',
    width: '25%',
  },
  {
    title: '换算规则',
    key: 'foreignSimpName',
    dataIndex: 'foreignSimpName',
    align: 'center',
    width: '30%',
  }
  ,
  {
    title: '删除',
    dataIndex: 'operation',
    width: '5%',
    align: 'center',
    slots: { customRender: 'operation' },
  },
]
const handleCyAdd = ()=>{
  let a = null == CyDataSource.value?0:CyDataSource.value.length
  if (a == 12){
    return
  }
  const newData = {
    key: `${a}`,
    serialNumber: `${a+1}`,
    accountId: getCurrentAccountName(),
    foreignCode: '',
    foreignName: '',
    foreignSimpName: '',
  };
  CyDataSource.value.push(newData)
}
const CyChange=(value,index)=>{
  let obj = currencyTypeList.value.filter(item=>item.currencyZhCnName==value)[0]
  editableData[index].foreignCode = obj.abbreviation
  editableData[index].foreignSimpName = obj.fractionalCurrency
}
const checkCurrencyData = (data)=>{
  try {
    if (CyDataSource.value.filter(item => item.foreignName.indexOf(data.foreignName) != -1).length > 1){
      data.foreignName = ''
      data.foreignCode = ''
      data.foreignSimpName = ''
      throw new Error('该常用外币已存在！')
    }
    return  true
  }catch (e) {
    createWarningModal({content: e.message});
    return false
  }
}
/************************************常用外币************************************/
  const currencyTypeList = ref([])
/************************************外币汇率************************************/
const ifModify = ref(true)
const tableHeight = (document.documentElement.clientHeight-540)+'px'
const WbBasicInfo = ref({
  id:'',
  foreignCode:'S.RP(S)',
  localCode:'RMB',
  comMethod:'*',
  valueMethod:'VOUCHER',
  decimalNum:'7',
  errorValue:'0.01',
  rateStyle:'MONTH',
  accountId: getCurrentAccountName(),
  iyear: '2021'
})

const WbMonth = ref('')
const WbDay = ref('')
const WbDataSource= ref([{
  key: '0',
  serialNumber: '1',
  iperiod: '2021-01',
  rateBook: '',
  checkInTime: '',
  uniqueCode: ''
}]);
const WbColumns = [
  {
    title: '序号',
    key: 'serialNumber',
    dataIndex: 'serialNumber',
    align: 'center',
    width: '10%'
  },
  {
    title: '汇率期间',
    key: 'iperiod',
    dataIndex: 'iperiod',
    align: 'center',
    width: '25%',
  },
  {
    title: '记账汇率',
    key: 'rateBook',
    dataIndex: 'rateBook',
    align: 'center',
    width: '25%',
    slots: { customRender: 'rateBook' },
  },
  {
    title: '登记时间',
    key: 'checkInTime',
    dataIndex: 'checkInTime',
    align: 'center',
    width: '30%',
  },{
    title: '操作',
    dataIndex: 'operation',
    width: '10%',
    align: 'center',
    slots: { customRender: 'operation' },
  }
]


let changeBeforeModel:any = {}
const handleWbModify = ()=>{
  // 校验必填
  let msg = ''
  for(let i in WbBasicInfo.value){
    if (i != 'id' && WbBasicInfo.value[i] == ''){
      msg = '请完善基础性信息后再点击保存！'
      break;
    }
  }
  let isChanged:boolean = false
  isChanged = !(WbBasicInfo.value.foreignCode == changeBeforeModel.foreignCode
    && WbBasicInfo.value.comMethod == changeBeforeModel.comMethod
    && WbBasicInfo.value.valueMethod == changeBeforeModel.valueMethod
    && WbBasicInfo.value.decimalNum == changeBeforeModel.decimalNum
    && WbBasicInfo.value.rateStyle == changeBeforeModel.rateStyle
    && WbBasicInfo.value.iyear == changeBeforeModel.iyear
    && WbBasicInfo.value.errorValue == changeBeforeModel.errorValue)
  if(!isChanged){
    msg = '数据无变化！无法进行保存操作！'
  }
  if (msg != ''){
    createWarningModal({content: msg});
   return;
  }
  // 后台同步
  saveWbhvData(WbBasicInfo.value).then(res=>{
    if (null == res){
      message.success('系统已同步！')
      initWbPageData()
    }else{
      message.success('系统同步失败！')
    }
  })
}
const getMonthDays = (month)=>{
  if (month.value == ''){
    return []
  }else {
    return parseInt(getCurrentMonthLast(WbBasicInfo.value.iyear+'-'+WbMonth.value))
  }
}
const handleWbAdd = ()=>{
  let a = null == WbDataSource.value?0:WbDataSource.value.length
  if (WbBasicInfo.value.rateStyle === 'DAY'){
    if (''==WbMonth.value){
      createWarningModal({content: '请选择日汇率所属月份！'});
      return;
    }
    if (''==WbDay.value){
      createWarningModal({content: '请选择日汇率所属具体日期！'});
      return;
    }
    if (WbDataSource.value.filter(item=>item.iperiod == assembleTheCurrentPeriod(a)).length > 0){
      createWarningModal({content: '该日期已存在不能进行重复添加！'});
      return;
    }
    if (a == parseInt(WbDay.value)){
      return
    }
  }else {
    if (a == 12){
      return
    }
  }
  const newData = {
    key: `${a}`,
    serialNumber: `${a+1}`,
    iperiod: assembleTheCurrentPeriod(a),
    rateBook: '',
    checkInTime: '',
    uniqueCode: '',
    currencyCode: ''
  };
  WbDataSource.value.push(newData)
  WbDay.value = ''
}

const assembleTheCurrentPeriod= (count) => {
  if (WbBasicInfo.value.rateStyle === 'DAY'){
    return  WbBasicInfo.value.iyear+'-'+WbMonth.value+'-'+WbDay.value
  }else {
    return  WbBasicInfo.value.iyear+'-'+((count+1)>9?''+(count+1):'0'+(count+1))
  }
}
const loadWbDataSource =async (uniqueCode,condition)=>{
  WbDataSource.value = []
  await useRouteApi(findAllWbhvEntrys,{schemaName:database.value})({uniqueCode: uniqueCode,date: condition,currencyCode: WbBasicInfo.value.foreignCode}).then(res2=>{
    res2.items.forEach((item,index)=>{
      item.key = `${index}`
      item.serialNumber = `${index+1}`
      WbDataSource.value.push(item)
    })
  })
}

const getWbName = (num)=>{
  let obj = CyDataSource.value.filter(item=>item.foreignCode==num)[0]
  if (null == obj){
    return ''
  }
  return obj.foreignCode+' '+ obj.foreignName
}
const WbChange=()=>{
  let condition = WbBasicInfo.value.iyear
  if (WbBasicInfo.value.rateStyle == 'DAY'){
    condition = WbBasicInfo.value.iyear+'-'+WbMonth.value
  }
  loadWbDataSource(WbBasicInfo.value.id,condition)
}
const initWbPageData= async ()=>{
  await useRouteApi(findCurrentWbhvInfo,{schemaName:database.value})({accId:databaseCn.value,iyear: databaseYear.value}).then(res=>{
    changeBeforeModel= JSON.parse(JSON.stringify(res))
    WbBasicInfo.value = res
    let condition =''
    if (res.rateStyle == 'DAY'){
      WbMonth.value = (new Date().getMonth()+1 > 9)?( new Date().getMonth()+1 +''):('0'+(new Date().getMonth()+1))
      condition = res.iyear + '-'+WbMonth.value
    }else {
      condition = res.iyear
    }
    loadWbDataSource(res.id,condition)
  })
}
/************************************外币汇率************************************/
/************************************参数精度************************************/
const CsDataSource =ref(
  [{
    key: '0',
    serialNumber:'1',
    paraNum:'',
    paraName:'',
    beiyong1:'',
    decimalPlaces:'',
    controlManner:'',
    groupControl:'',
    accountId:'',
  }])
const CsColumns =[{
  title: '序号',
  key: 'serialNumber',
  dataIndex: 'serialNumber',
  align: 'center',
  width: '8%'
}, {
    title: '控制参数',
    key: 'paraName',
    dataIndex: 'paraName',
    align: 'left',
    width: '22%',
    slots: { customRender: 'paraName' },
  }, {
  title: '整数位数',
  key: 'beiyong1',
  dataIndex: 'beiyong1',
  align: 'center',
  width: '15%',
  slots: { customRender: 'beiyong1' },
},{
    title: '小数点位数',
    key: 'decimalPlaces',
    dataIndex: 'decimalPlaces',
    align: 'center',
    width: '15%',
    slots: { customRender: 'decimalPlaces' },
  }, {
    title: '控制方式',
    key: 'controlManner',
    dataIndex: 'controlManner',
    align: 'center',
    width: '22%',
    slots: { customRender: 'controlManner' },
  }, /*{
    title: '集团管控',
    key: 'groupControl',
    dataIndex: 'groupControl',
    align: 'center',
    width: '22%',
    slots: { customRender: 'groupControl' },
  }, {
    title: '操作',
    dataIndex: 'operation',
    width: '8%',
    align: 'center',
    slots: { customRender: 'operation' },
  }*/
 ]

const handleCsAdd = ()=>{
  let a = null == CsDataSource.value?0:CsDataSource.value.length
  let dFlag = useCompanyOperateStoreWidthOut().getAccountList[0].independent == '1' //独立账套
  const newData = {
    key: `${a}`,
    serialNumber: `${a+1}`,
    paraNum:'',
    paraName:'',
    decimalPlaces:'2',
    controlManner:ifModify.value?'1':'0',
    groupControl: dFlag?'0':'1',
    accountId: databaseCn.value,
    tenantId: database.value
  };
  CsDataSource.value.push(newData)
}

const checkCsData = (data)=>{
  try {
    if (data.paraName == ''){
      throw new Error('该控制参数不能为空！')
    }
    if (CsDataSource.value.filter(item => item.paraName.indexOf(data.paraName) != -1).length > 1){
      data.paraName = ''
      throw new Error('该控制参数已存在！')
    }
    return true
  }catch (e) {
    createWarningModal({content: e.message});
    return false
  }
}

/************************************参数精度************************************/
// 监听切换
watch( () => activeKey.value, async() => {
  if (activeKey.value !== '1' && !isEdit.value){
    handleBus('3')
  }
  if (activeKey.value === '1') {

  }else if(activeKey.value === '2'){
    currentAccountQjInfo({accId: databaseCn.value}).then(res=>{
      QjBasicInfo.value = res
      fiscalYear.value = res.maxYear
      fiscalYear2.value = res.maxYear
      res.maxYear = fiscalYear.value+'--'+fiscalYear2.value
      initDataSource(res,databaseCn.value);
    })
  }else if(activeKey.value === '3'){
    PingZhengTypeDataSource.value = []
    await useRouteApi(currentAccountTypes,{schemaName:database.value})({ pageSize : 20,pageNumber: 1}).then(res=>{
      res.items.forEach((item,index)=>{
        item.key = `${index}`
        item.serialNumber = `${index+1}`
        PingZhengTypeDataSource.value.push(item)
      })
      let data =PingZhengTypeDataSource.value.filter(item=>item.beiyong1=='1')[0]
      if (null != data){
        PingZhengTypeInfo.value.voucherTypeCode = data.voucherTypeCode
        PingZhengTypeInfo.value.voucherTypeName = data.voucherTypeName
        PingZhengTypeInfo.value.limitClass = data.limitClass
      }
    })
  }else if(activeKey.value === '4'){
    AiDataSource.value = []
    findCorpBankAll().then(res2=>{
      res2.items.forEach((item,index)=>{
        item.key = `${index}`
        item.serialNumber = `${index+1}`
        AiDataSource.value.push(item)
      })
      JsDataSource.value = []
      useRouteApi(findSettModesAll,{schemaName:database.value})({}).then(res=>{
        res.items.forEach((item,index)=>{
          item.key = `${index}`
          item.serialNumber = `${index+1}`
          JsDataSource.value.push(item)
        })
      })
    })
  }else if(activeKey.value === '5'){
    findAllXjOrLlList().then(res=>{
      XjAndLlCodeList.value =  res.filter(item=> item.bend == '1')
        AiDataSource.value = []
      findCorpBankAll().then(res2=>{
        res2.items.forEach((item,index)=>{
          item.key = `${index}`
          item.serialNumber = `${index+1}`
          AiDataSource.value.push(item)
        })
      })
    })
  }else if(activeKey.value === '6'){


  }else if(activeKey.value === '7'){
    //获取指定账套已存在常用外币信息
    CyDataSource.value = []
    await useRouteApi(currentCyDatas,{schemaName:database.value})({accId: databaseCn.value}).then(res1=>{
      res1.items.forEach((item,index)=>{
        item.key = `${index}`
        item.serialNumber = `${index+1}`
        CyDataSource.value.push(item)
      })
    })
    initWbPageData()
    ifModify.value = dataModel.settingList.indexOf('iexchangeRateControl') === -1
  }else if(activeKey.value === '8'){
    // 获取所以外币信息
    findCurrencyTypeList({}).then(res=>{
      currencyTypeList.value = res.items.filter(obj => obj.currencyZhCnName.indexOf('人民币') == -1  /*|| obj.currencyZhCnName != dataModel.basisMap.currency*/)
    })
    //获取指定账套已存在常用外币信息
    CyDataSource.value = []
    // currentCyDatas({accId: getCurrentAccountName()}).then(res1=>{
    //   res1.items.forEach((item,index)=>{
    //     item.key = `${index}`
    //     item.serialNumber = `${index+1}`
    //     CyDataSource.value.push(item)
    //   })
    // })
    await useRouteApi(currentCyDatas,{schemaName:database.value})({accId: databaseCn.value}).then(res1=>{
      res1.items.forEach((item,index)=>{
        item.key = `${index}`
        item.serialNumber = `${index+1}`
        CyDataSource.value.push(item)
      })
    })
  }else if(activeKey.value === '9'){
    //
    CsDataSource.value = []
    let dFlag = useCompanyOperateStoreWidthOut().getAccountList[0].independent == '1'
    await useRouteApi(parameterAccuracyDatas,{schemaName:database.value})({'tenantId':database.value}).then(res1=>{
      res1.forEach((item,index)=>{
        item.key = `${index}`
        item.serialNumber = `${index+1}`
        item.controlManner=dFlag?'0':'1'
        item.groupControl= ifModify.value?'1':'0'
        CsDataSource.value.push(item)
      })
    })
  }
});

const edit = async (key: string,type:string) => {
  if (type=='JS') {
    editableData[key] = cloneDeep(JsDataSource.value.filter(item => key === item.key)[0]);
    editableData[key].type = 'JS'
  }else if(type=='JD'){
    editableData[key] = cloneDeep(QjJdDataSource.value.filter(item => key === item.key)[0]);
    editableData[key].type = 'JD'
  }else if (type=='TYPE'){
    editableData[key] = cloneDeep(PingZhengTypeDataSource.value.filter(item => key === item.key)[0]);
    editableData[key].type = 'TYPE'
  }else if(type=='AI'){
    editableData[key] = cloneDeep(AiDataSource.value.filter(item => key === item.key)[0]);
    editableData[key].type = 'AI'
  }else if(type=='WB'){
    editableData[key] = cloneDeep(WbDataSource.value.filter(item => key === item.key)[0]);
    editableData[key].type = 'WB'
  }else if(type=='PZ'){
    editableData[key] = cloneDeep(PzDataSource.value.filter(item => key === item.key)[0]);
    editableData[key].type = 'PZ'
  }else if(type=='CY'){
    editableData[key] = cloneDeep(CyDataSource.value.filter(item => key === item.key)[0]);
    editableData[key].type = 'CY'
  }else if (type=='CS'){
    editableData[key] = cloneDeep(CsDataSource.value.filter(item => key === item.key)[0]);
    editableData[key].type = 'CS'
  }else if (type == 'YFQJ'){
    // 判断当前年是否已存在凭证
    let data = {
      type: 'edit',
      accId: database.value,
      year: databaseYear.value,
    }
    let size = await checkPeriod(data)
    if (null != size && size == 0) {
      editableData[key] = cloneDeep(QjDataSource.value.filter(item => key === item.key)[0]);
      editableData[key].type = 'YFQJ'
    } else {
      createWarningModal({title: '温馨提示', content: `${data.year}年度会计期间已被使用，不能进行修改！`})
    }
  }
}

const save = async (key: string,type:string) => {
  let data = editableData[key]
  let handle = null
  if (type=='JS'){
    Object.assign(JsDataSource.value.filter(item => key === item.key)[0], editableData[key]);
    handle = await useRouteApi(saveSettModes,{schemaName:database.value})(data)
  }else if(type=='JD'){
    Object.assign(QjJdDataSource.value.filter(item => key === item.key)[0], editableData[key]);
    if (checkQjKdData()){
      // 添加
      handle = saveSystemQuarterly(QjJdDataSource.value)
      // 账套同步
      //await saveQjJdQuarterly(QjJdDataSource.value)
    }
  }else if (type=='TYPE'){
    // 校验必填项
    Object.assign(PingZhengTypeDataSource.value.filter(item => key === item.key)[0], editableData[key]);
    if (checkTypeData(data)){
      handle = await useRouteApi(saveVoucherData,{schemaName:database.value})(data)
    }
  }else if(type=='AI'){
    Object.assign(AiDataSource.value.filter(item => key === item.key)[0], editableData[key]);
    if (checkAiData(data)){
      data.corpBankCode = '00'+data.serialNumber
      data.ibankStatement = '1'
      if (data.ccode.split('--')[1] == '1'){
        data.ibank = '1'
        data.corpBankType = '银行'
        data.icash = '0'
      }else{
        data.ibank = '0'
        data.corpBankType = '现金'
        data.icash = '1'
      }
      data.ccode = data.ccode
      handle = saveAIData(data)
    }
  }else if(type=='PZ'){
    Object.assign(PzDataSource.value.filter(item => key === item.key)[0], editableData[key]);
  }else if(type=='CY'){
    Object.assign(CyDataSource.value.filter(item => key === item.key)[0], editableData[key]);
    if (checkCurrencyData(data)){
      //data.foreignName = data.foreignName.replace('↵','').trim()
      handle =  await useRouteApi(saveCurrencyData,{schemaName:database.value})(data)
    }
  }else if(type=='WB'){
    editableData[key].rateBook = parseFloat(editableData[key].rateBook).toFixed(parseInt(WbBasicInfo.value.decimalNum))
    editableData[key].checkInTime = serverTime()
    editableData[key].uniqueCode = WbBasicInfo.value.id
    editableData[key].currencyCode = WbBasicInfo.value.foreignCode
    Object.assign(WbDataSource.value.filter(item => key === item.key)[0], editableData[key]);
    handle =  await useRouteApi(saveWbhvEntrys,{schemaName:database.value})(data)
    WbChange()
  }else if (type=='CS'){
    Object.assign(CsDataSource.value.filter(item => key === item.key)[0], editableData[key]);
    if (checkCsData(data)){
      //data.foreignName = data.foreignName.replace('↵','').trim()
      handle = useRouteApi(saveCsData,{schemaName:database.value})(data)
    }
  }
  if (null != handle){
    handle.then(res=>{
      if (res.message === 'ok' || res.length > 0 || res.t1 != null){
        message.success('系统已同步！')
      }else {
        message.error('系统同步失败！')
      }
    })
  }
  delete editableData[key];
}
const onDelete = (key: string,type: string) => {
  let handle = null
  if (type=='TYPE'){
    let data = PingZhengTypeDataSource.value.filter(item => item.key === key)[0]
    // db 删除
    if (data.id == null || data.id == ''){ // 空白行删除
      PingZhengTypeDataSource.value = PingZhengTypeDataSource.value.filter(item => item.key !== key);
    }else {
      handle = delVoucherData(data)
    }
  }else if(type=='AI'){
    let data = AiDataSource.value.filter(item => item.key === key)[0]
    // db 删除
    if (data.id == null || data.id == ''){ // 空白行删除
      AiDataSource.value = AiDataSource.value.filter(item => item.key !== key);
    }else {
      handle = delAccountData(data)
    }
  }else if (type=='JS'){
    let data = JsDataSource.value.filter(item => item.key === key)[0]
    // db 删除
    if (data.id == null || data.id == ''){ // 空白行删除

      JsDataSource.value = JsDataSource.value.filter(item => item.key !== key);
    }else {
      handle =  useRouteApi(deleteSettModes,{schemaName:database.value})(data)
      handle = deleteSettModes(data)
    }

  }else if (type=='CY'){
    let data = CyDataSource.value.filter(item => item.key === key)[0]
    if (data.id == null || data.id == ''){ // 空白行删除
      CyDataSource.value = CyDataSource.value.filter(item => item.key !== key);
    }else {
      handle =  useRouteApi(deleteCurrencyData,{schemaName:database.value})(data)
    }
  }else if (type=='WB'){
    let data = WbDataSource.value.filter(item => item.key === key)[0]
    if (data.id == null || data.id == ''){ // 空白行删除
      WbDataSource.value = WbDataSource.value.filter(item => item.key !== key);
    }else {
      handle =   useRouteApi(deleteWbData,{schemaName:database.value})(data)
    }
  }else if (type=='CS'){
    let data = CsDataSource.value.filter(item => item.key === key)[0]
    if (data.id == null || data.id == ''){ // 空白行删除
      CsDataSource.value = CsDataSource.value.filter(item => item.key !== key);
    }else {
      handle = deleteCsData(data)
    }
  }
  if (null != handle){
    handle.then(res=>{
      if (null == res || res.message === 'ok' || res.length > 0 ){
        message.success('系统已同步！')
        if (type=='TYPE'){
           PingZhengTypeDataSource.value = PingZhengTypeDataSource.value.filter(item => item.key !== key);
        }else if (type=='JS'){
          JsDataSource.value = JsDataSource.value.filter(item => item.key !== key);
        }else if(type=='AI'){
          AiDataSource.value = AiDataSource.value.filter(item => item.key !== key);
        }else if(type == 'CY'){
          CyDataSource.value = CyDataSource.value.filter(item => item.key !== key);
        }else if (type=='WB'){
          WbDataSource.value = WbDataSource.value.filter(item => item.key !== key);
        }else if(type=='CS'){
          CsDataSource.value = CsDataSource.value.filter(item => item.key !== key);
        }
      }else {
        message.error('系统同步失败！')
      }
    })
  }
}
const modifyData = (data)=>{
  let handle = null
  if (data.type == 'TYPE'){
    if (checkTypeData(data)){
      handle = saveVoucherData(data)
    }else {
      if (data.id == '' || data.id == null){
        data.voucherTypeCode=''
        data.voucherTypeName=''
        data.limitClass=''
        data.limitKemu=''
      }
    }
  }else if(data.type == 'AI'){
    if (checkAiData(data)){
      data.corpBankCode = '00'+data.serialNumber
      data.ibankStatement = '1'
      if (data.ibank == '1'){
        data.ibank = '1'
        data.corpBankType = '银行'
        data.icash = '0'
      }else{
        data.ibank = '0'
        data.corpBankType = '现金'
        data.icash = '1'
      }
      handle = saveAIData(data)
    }else {
      if (data.id == '' || data.id == null){
        data.corpBankName=''
        data.ccode=''
        data.beiyong1=''
        data.beiyong2=''
        data.beiyong3=''
      }
    }
  }else if(data.type == 'JS'){
    if (checkJsData(data)) {
      handle = saveSettModes(data)
    }else {
      if (data.id == '' || data.id == null){
        data.settModesCode=''
        data.settModesName=''
        data.beiyong1=''
        data.beiyong2=''
      }
    }
  }
  if (null != handle){
    handle.then(res=>{
      if (res.message === 'ok' || res.length > 0 || res.t1 != null || res.id != ''){
        message.success('系统已同步！')
      }else {
        message.error('系统同步失败！')
      }
    })
  }
}
// pushApi("进入财务信息主页")
/************************************* 2021.10.14 补充改造 ******************************************/
const dynamicAdReload = async (obj) => {
  database.value = obj.accountMode
  databaseCn.value = obj.accId
  databaseYear.value = obj.year
  databaseCode.value = obj.coCode
  if (activeKey.value === '1') {
    pageReload()
  }else if(activeKey.value === '2'){
    // 年切换
    yearChange()
  }else if(activeKey.value === '3'){

  }
  findByAuthSwith()
}

const yueSave = (key: string,type:string) => {
  let data = editableData[key]
  let thisDate = new Date(data.dateEnd)
  let thisDay = thisDate.getDate()
  QjDataSource.value[0].dateEnd = data.dateEnd
  QjDataSource.value.forEach(item=>{
    let num = parseInt(item.iperiodNum)
    if(num != 1 && num  <  12){
      let year = thisDate.getFullYear();
      let month = thisDate.getMonth()+1
      let after = null
      let afterDay = DateTool().getLastDay(year,month+1)
      let pervDay =  DateTool().getLastDay(year,month)
      if (thisDay > afterDay){ // 当前日期大于本月最虎一天时 去最后一天
        after =  new Date(year,month,afterDay)
      }else if (thisDay <= afterDay ){
        after =   new Date(year,month,thisDay)
      }
      item.dateStart = DateTool().dateStr(new Date(year,thisDate.getMonth(),thisDay > pervDay?((pervDay)+1):(thisDay+1)))
      item.dateEnd = DateTool().dateStr(after)
      thisDate = after
    }else if (parseInt(item.iperiodNum) == 12){
      let year = thisDate.getFullYear();
      let month = thisDate.getMonth()
      let after = new Date(year,month,thisDay+1)
      if (thisDay<30){
        item.dateStart = DateTool().dateStr(after)
      }else {
        item.dateStart = year+'-12-01'
      }
    }
  })
  delete editableData[key];
}
/************************************* 2021.10.14 补充改造 ******************************************/
const codeAllAuth=ref([])
const codeAllDisabled=ref(true)
async function codeAllAuthCheck() {
  if(codeAllAuth.value.length>0){
    let code='0'
    let pztype='0'
    if(codeAllAuth.value.length==1){
      code=codeAllAuth.value[0]=='code'?'1':'0'
      pztype=codeAllAuth.value[0]=='pztype'?'1':'0'
    }else if(codeAllAuth.value.length==2){
      code=codeAllAuth.value.filter(item =>item==='code').length?'1':'0'
      pztype=codeAllAuth.value.filter(item =>item==='pztype').length?'1':'0'
    }

    let temp={
      id:authSwithId.value,
      recordNum:databaseCn.value,
      iyear:databaseYear.value,
      isCcode:code,
      isVoucherType:pztype
    }
    await useRouteApi(sysDataAuthSwithSave,{schemaName: database})(temp)
      .then(item=>{
        findByAuthSwith()
        console.log('增加返回>>>'+JSON.stringify(item))
      })
  }
}

async function findByAuthSwith() {
  await useRouteApi(findByAccIdAndIyear,{schemaName: database})({accId:databaseCn.value,iyear:databaseYear.value})
    .then(item=>{
      const temp:any=[]
      if(item!=='不存在'){
        authSwithId.value=item.id
        if(item.isCcode=='1'){
          temp.push('code')
        }
        if(item.isVoucherType=='1'){
          temp.push('pztype')
        }
      }
      codeAllAuth.value=temp
    })
}

async function codeAuthSwithBtn(flag) {
  if(flag==='0'){
    isEdit.value=false
    codeAllDisabled.value=false
  }else if(flag==='3'){
    isEdit.value=true
    codeAllDisabled.value=true
    codeAllAuth.value=[]
  }else if(flag==='1'){
    // 删除
    await useRouteApi(delById,{schemaName: database})({id: authSwithId.value})
    .then(()=>{
      isEdit.value=true
      codeAllDisabled.value=true
      findByAuthSwith()
    })
  }else if(flag==='2'){
    isEdit.value=true
    codeAllDisabled.value=true
    codeAllAuthCheck()
  }
}
</script>
<style src="../../../../assets/styles/financial-info-menu.less" lang="less" scoped="scoped"></style>

<template>
  <BasicModal
    width="900px"
    class="spaceLogo"
    v-bind="$attrs"
    title="出纳凭证查询"
    @ok="handleOk()"
    @cancel="handleClose()"
    @visible-change="openOrClose"
    @register="register"
  >
    <div
      class="nc-open-content"
      style="height: 100%"
    >
      <div class="open-content-title">
        <div>
          系统方案
        </div>
        <div>
          <span>
          查询条件
          </span>
        </div>
      </div>
      <div class="open-content-up">
        <div class="ocup-position">
          个人方案
        </div>
        <a-tabs type="card" v-model:activeKey="defaultTabsKey" @change="tabsChange" tabPosition="left" style="height: 430px">
          <a-tab-pane key="1" tab="标准模式">
            <ul>
              <li>
                <a-radio-group style="width: 100%" v-model:value="radiovalue" @change="radiochange">
                  <p>
                    <a-radio :value="1">
<!--                      期&emsp;&emsp;间：-->
                      <span style="color: red">*</span>
                    </a-radio>
                    <a-select
                      v-model:value="strDate"
                      show-search
                      :disabled="dateselflg"
                      EditOutlined
                      style="width: 227px"
                      @focus="focusStrDate"
                      @change="handleChangeStrDate"
                    >
                      <a-select-option
                        v-for="item in strDateList"
                        :key="item.id"
                        :value="item.value"
                      >
                        {{ item.value }}
                      </a-select-option>
                    </a-select>
                    <span>&emsp;~&emsp;</span>
                    <a-select
                      v-model:value="endDate"
                      show-search
                      :disabled="dateselflg"
                      EditOutlined
                      style="width: 227px"
                      @focus="focusEndDate"
                      @change="handleChangeEndDate"
                    >
                      <a-select-option
                        v-for="item in endDateList"
                        :key="item.id"
                        :value="item.value"
                      >
                        {{ item.value }}
                      </a-select-option>
                    </a-select>
                  </p>
                  <p>
<!--                    日&emsp;&emsp;期：-->
                    <a-radio :value="2">
                      <span style="color: red">*</span>
                    </a-radio>
                    <a-range-picker
                      v-model:value="riqi"
                      style="width: 496px"
                      :disabled="timeselflg"
                      @change="timechange"
                    />
                  </p>
                </a-radio-group>
              </li>
              <li><span>凭证编号：</span>
                <a-input v-model:value="modelList['1'].variable.voucherNumberStart" :allowClear="true" placeholder="" style="width: 227px"/>
                <span>&emsp;~&emsp;</span>
                <a-input v-model:value="modelList['1'].variable.voucherNumberEnd" :allowClear="true" placeholder="" style="width: 227px"/>
              </li>
              <li><span>凭证类别：</span>
                <a-select
                  v-model:value="modelList['1'].variable.voucherType"
                  placeholder=""
                  :allowClear="true"
                  style="width: 200px">
                  <a-select-option value="记">记</a-select-option>
                  <a-select-option value="转">转</a-select-option>
                </a-select>
                <span>&emsp;&emsp;&emsp;摘要：</span>
                <a-input v-model:value="modelList['1'].variable.summary" :allowClear="true"  placeholder="摘要" style="width: 200px"/>
              </li>
              <li>
                <span>审核状态：</span>
                <a-select
                  :allowClear="true"
                  v-model:value="modelList['1'].variable.reviewStatus"
                  placeholder="未审核/已审核/待审核"
                  style="width: 200px"
                >
                  <a-select-option value="0">未审核</a-select-option>
                  <a-select-option value="1">已审核</a-select-option>
                  <a-select-option value="2">待审核</a-select-option>
                </a-select>
                <span>&emsp;记账状态：</span>
                <a-select
                  :allowClear="true"
                  placeholder="未记账/已记账/待记账"
                  style="width: 200px"
                  v-model:value="modelList['1'].variable.bookStatus"
                >
                  <a-select-option value="0">未记账</a-select-option>
                  <a-select-option value="1">已记账</a-select-option>
                  <a-select-option value="2">待记账</a-select-option>
                </a-select>
              </li>
              <li>
                <span>科目编码：</span>
                <a-select
                  :allowClear="true"
                  v-model:value="modelList['1'].variable.subjectNumber"
                  placeholder=""
                  style="width: 200px"
                >
                  <a-select-option v-for="item in kmList" :value="item.ccode">{{item.ccode}} {{item.ccodeName}}</a-select-option>
                </a-select>
                <span>&emsp;科目方向：</span>
                <a-select
                  :allowClear="true"
                  placeholder=""
                  style="width: 200px"
                  v-model:value="modelList['1'].variable.direction"
                >
                  <a-select-option value="jf" >借方</a-select-option>
                  <a-select-option value="df" >贷方</a-select-option>
                </a-select>
              </li>
              <li><span>本币金额：</span>
                <a-input v-model:value="modelList['1'].variable.amountMin" :allowClear="true" placeholder="最小金额" style="width: 227px"/>
                <span>&emsp;~&emsp;</span>
                <a-input v-model:value="modelList['1'].variable.amountMax" :allowClear="true" placeholder="最大金额" style="width: 227px"/>
              </li>
              <li v-if="filterGroup.indexOf('A') != -1"><span>&emsp;&emsp;数量：</span>
                <a-input v-model:value="modelList['1'].variable.numberMin" :allowClear="true" placeholder="" style="width: 227px"/>
                <span>&emsp;~&emsp;</span>
                <a-input v-model:value="modelList['1'].variable.numberMax" :allowClear="true" placeholder="" style="width: 227px"/>
              </li>
              <li  v-if="filterGroup.indexOf('B') != -1"><span>&emsp;&emsp;币种：</span>
                <a-select style="width: 150px" v-model:value="modelList['1'].constant.currency" :allowClear="true">
                  <a-select-option  v-for="d in bzList" :key="d.id" :value="d.foreignName">
                    {{ d.foreignName }}
                  </a-select-option>
                </a-select>
                <span>&emsp;外币金额：</span>
                <a-input v-model:value="modelList['1'].variable.wbAmountMin" :allowClear="true" placeholder="最小金额" style="width: 100px"/>
                <span>&emsp;~&emsp;</span>
                <a-input v-model:value="modelList['1'].variable.wbAmountMax" :allowClear="true" placeholder="最大金额" style="width: 100px"/>
              </li>
              <li  v-if="filterGroup.indexOf('C') != -1"><span>&emsp;票据号：</span>
                <a-input v-model:value="modelList['1'].variable.billNumberStart" :allowClear="true" placeholder="" style="width: 227px"/>
                <span>&emsp;~&emsp;</span>
                <a-input v-model:value="modelList['1'].variable.billNumberEnd" :allowClear="true" placeholder="" style="width: 227px"/>
              </li>
              <li>
                <span>&emsp;制单人：</span>
                <a-select
                  :allowClear="true"
                  v-model:value="modelList['1'].variable.preparedMan"
                  placeholder=""
                  style="width: 200px"
                >
                  <a-select-option value="un">未审核</a-select-option>
                  <a-select-option value="ok">已审核</a-select-option>
                  <a-select-option value="to">待审核</a-select-option>
                </a-select>
                <span>&emsp;&emsp;&emsp;出纳：</span>
                <a-select
                  :allowClear="true"
                  placeholder=""
                  style="width: 200px"
                  v-model:value="modelList['1'].variable.cashierMan"
                >
                  <a-select-option value="JF" label="借方"/>
                  <a-select-option value="DF" label="贷方"/>
                </a-select>
              </li>
              <li>
                <span>&emsp;审核人：</span>
                <a-select
                  :allowClear="true"
                  v-model:value="modelList['1'].variable.reviewMan"
                  placeholder=""
                  style="width: 200px"
                >
                  <a-select-option value="un">未审核</a-select-option>
                  <a-select-option value="ok">已审核</a-select-option>
                  <a-select-option value="to">待审核</a-select-option>
                </a-select>
                <span>&emsp;&emsp;记账人：</span>
                <a-select
                  :allowClear="true"
                  placeholder=""
                  style="width: 200px"
                  v-model:value="modelList['1'].variable.bookMan"
                >
                  <a-select-option value="JF" label="借方"/>
                  <a-select-option value="DF" label="贷方"/>
                </a-select>
              </li>
              <li>
                <a-button>✚添加辅助核算条件</a-button>
              </li>
            </ul>
          </a-tab-pane>
          <a-tab-pane v-if="whetherGroup" key="2" tab="集团模式">
<!--            <ul>
              <li>
                <span>核算单位：</span>
                <a-select
                  mode="multiple"
                  style="width: 80%"
                  placeholder="可多选"
                  option-label-prop="label"
                >
                  <a-select-option value="湖北万亚" label="湖北万亚">
                    湖北万亚
                  </a-select-option>
                  <a-select-option value="北京希格" label="北京希格">
                    北京希格
                  </a-select-option>
                </a-select>
              </li>
              <li><span>期&emsp;&emsp;间：</span>
                <a-select default-value="2020-01" style="width: 218px;text-align: center" @change="handleChange">
                  <a-select-option value="2020-01">
                    2020-01
                  </a-select-option>
                  <a-select-option value="2020-02">
                    2020-02
                  </a-select-option>
                  <a-select-option value="2020-03" disabled>
                    2020-03
                  </a-select-option>
                  <a-select-option value="2020-04">
                    2020-04
                  </a-select-option>
                </a-select>
                <span>&emsp;~&emsp;</span>
                <a-select default-value="2020-01" style="width: 218px;text-align: center" @change="handleChange">
                  <a-select-option value="2020-01">
                    2020-01
                  </a-select-option>
                  <a-select-option value="2020-02">
                    2020-02
                  </a-select-option>
                  <a-select-option value="2020-03" disabled>
                    2020-03
                  </a-select-option>
                  <a-select-option value="2020-04">
                    2020-04
                  </a-select-option>
                </a-select>
              </li>
              <li><span>科&emsp;&emsp;目：</span>

                <a-select
                  show-search
                  placeholder="最小科目"
                  style="width: 218px"
                  :default-active-first-option="false"
                  :show-arrow="false"
                  :filter-option="false"
                  :not-found-content="null"
                  @change="handleChange"
                >
                  <a-select-option v-for="d in data" :key="d.value">
                    {{ d.text }}
                  </a-select-option>
                </a-select>
                <span>&emsp;~&emsp;</span>
                <a-select
                  show-search
                  placeholder="最大科目"
                  style="width: 218px"
                  :default-active-first-option="false"
                  :show-arrow="false"
                  :filter-option="true"
                  :not-found-content="null"
                  @change="handleChange"
                >
                  <a-select-option v-for="d in data" :key="d.value">
                    {{ d.text }}
                  </a-select-option>
                </a-select>
              </li>
              <li>
                <a-radio-group @change="onChange" style="width: 100%">
                  <a-radio :value="1">
                    <span>级次</span>
                    <a-select default-value="1" style="width: 150px;text-align: center" @change="handleChange">
                      <a-select-option value="1">
                        1
                      </a-select-option>
                      <a-select-option value="2">
                        2
                      </a-select-option>
                      <a-select-option value="3">
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
                    <span>&emsp;~&emsp;</span>
                    <a-select default-value="1" style="width: 150px;text-align: center" @change="handleChange">
                      <a-select-option value="1">
                        1
                      </a-select-option>
                      <a-select-option value="2">
                        2
                      </a-select-option>
                      <a-select-option value="3">
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
                  </a-radio>
                  <a-radio :value="2" style="margin-left: 50%;">
                    <span>末级科目</span>
                  </a-radio>
                </a-radio-group>
              </li>
              <li><span>科目类型：</span>
                <a-select
                  mode="multiple"
                  style="width: 80%"
                  placeholder="可多选"
                  option-label-prop="label"
                >
                  <a-select-option value="" label="全部">
                    全部
                  </a-select-option>
                  <a-select-option value="1" label="损益">
                    损益
                  </a-select-option>
                </a-select>
              </li>
              <li><span>样&emsp;&emsp;式：</span>
                <a-select default-value="J" style="width: 185px" @change="handleChange">
                  <a-select-option value="J">
                    本币式
                  </a-select-option>
                  <a-select-option value="SJ">
                    外币式
                  </a-select-option>
                  <a-select-option value="WJ">
                    本币外币式
                  </a-select-option>
                </a-select>
                <span>&emsp;字&emsp;&emsp;号：</span>
                <a-select default-value="MAX" style="width: 185px" @change="handleChange">
                  <a-select-option value="MAX">
                    大号字体
                  </a-select-option>
                  <a-select-option value="MIN">
                    小号字体
                  </a-select-option>
                </a-select>
              </li>
              <li><span>币&emsp;&emsp;种：</span>
                <a-select default-value="1" style="width: 200px" @change="handleChange">
                  <a-select-option value="1">
                    人民币
                  </a-select-option>
                  <a-select-option value="2">
                    美元
                  </a-select-option>
                  <a-select-option value="3" disabled>
                    欧元
                  </a-select-option>
                  <a-select-option value="4">
                    港元
                  </a-select-option>
                </a-select>
              </li>
            </ul>
            <a-checkbox-group @change="onChange">
              <a-checkbox value="A" style="width: 200px">
                包含未记账
              </a-checkbox>
              <a-checkbox value="B" style="width: 200px">
                期间无发生显示累计
              </a-checkbox>
            </a-checkbox-group>-->
          </a-tab-pane>
          <a-tab-pane key="3" tab="个人标准模式">
            <ul>
              <li><span style="color: red">*期&emsp;&emsp;间：</span>
                <a-select
                  v-model:value="strDate"
                  EditOutlined
                  style="width: 200px"
                  @focus="focusStrDate"
                  @change="handleChangeStrDate"
                >
                  <a-select-option
                    v-for="item in strDateList"
                    :key="item.id"
                    :value="item.value"
                  >
                    {{ item.value }}
                  </a-select-option>
                </a-select>
                <span>&emsp;~&emsp;</span>
                <a-select
                  v-model:value="endDate"
                  EditOutlined
                  style="width: 200px"
                  @focus="focusEndDate"
                  @change="handleChangeEndDate"
                >
                  <a-select-option
                    v-for="item in endDateList"
                    :key="item.id"
                    :value="item.value"
                  >
                    {{ item.value }}
                  </a-select-option>
                </a-select>
              </li>
              <li><span>账&emsp;&emsp;户：</span>
                <a-select
                  v-model:value="minKm"
                  show-search
                  placeholder="所属账户"
                  EditOutlined
                  style="width: 200px"
                  :filter-option="filterOption"
                  @focus="handleFocusMinKm"
                >
                  <a-select-option v-for="d in minkmList" :value="d.ccode">
                    {{ d.corpBankName }}
                  </a-select-option>
                </a-select>
                <span>&emsp;摘&emsp;&emsp;要：</span>
                <a-input v-model:value="modelList['3'].variable.summary" placeholder="摘要" style="width: 200px"/>
              </li>
              <li>
                <span>方&emsp;&emsp;向：</span>
                <a-select
                  v-model:value="modelList['3'].variable.direction"
                  allow-clear
                  placeholder="方向"
                  style="width: 200px"
                >
                  <a-select-option value="">全部</a-select-option>
                  <a-select-option value="md">借方</a-select-option>
                  <a-select-option value="mc">贷方</a-select-option>
                </a-select>
                <span>&emsp;科目性质：</span>
                <a-select
                  style="width: 200px"
                  v-model:value="modelList['3'].constant.nature"
                >
                  <a-select-option value="" label="全部" />
                  <a-select-option value="CW" label="财务会计"/>
                  <a-select-option value="YS" label="预算会计"/>
                </a-select>
              </li>
              <li><span>金&emsp;&emsp;额：</span>
                <a-input v-model:value="modelList['3'].variable.amountMin" :allowClear="true" placeholder="最小金额" style="width: 200px"/>
                <span>&emsp;~&emsp;</span>
                <a-input v-model:value="modelList['3'].variable.amountMax" :allowClear="true" placeholder="最大金额" style="width: 200px"/>
              </li>
              <li><span>币&emsp;&emsp;种：</span>
                <a-select style="width: 200px" v-model:value="modelList['3'].constant.currency">
                  <a-select-option  v-for="d in bzList" :key="d.id" :value="d.foreignName">
                    {{ d.foreignName }}
                  </a-select-option>
                </a-select>
                <span>&emsp;字&emsp;&emsp;号：</span>
                <a-select v-model:value="modelList['3'].constant.tableFontSize" style="width: 200px" @change="handleChange">
                  <a-select-option value="MAX">
                    大号字体
                  </a-select-option>
                  <a-select-option value="MIN">
                    小号字体
                  </a-select-option>
                </a-select>
              </li>
            </ul>
            <a-checkbox v-model:checked="ishaveRjz" style="width: 200px">
              包含未记账
            </a-checkbox>
            <a-checkbox v-model:checked="isShowQjlh" style="width: 200px">
              期间无发生显示累计
            </a-checkbox>
          </a-tab-pane>
        </a-tabs>
      </div>

    </div>

    <template #footer="{ record }">
      <div style="height: 35px">
        <div style="float: left"> <a-popconfirm
          ok-text="确定"
          cancel-text="放弃"
          @confirm="confirm"
          @cancel="cancel"
        >
          <template #icon><b>过滤设置</b><br></template>
          <template #title>
            <br/>
            <a-checkbox-group v-model:value="filterGroup">
              <a-checkbox value="A" style="width: 100px">
                数量
              </a-checkbox>
              <a-checkbox value="B" style="width: 120px">
                币种、金额
              </a-checkbox>
              <a-checkbox value="C" style="width: 100px">
                票据号
              </a-checkbox>
            </a-checkbox-group>
          </template>
          <a-button  type="primary" >过滤条件</a-button>
        </a-popconfirm></div>
        <div style="float: right">
          <a-button @click="handleClose">放弃</a-button>
          <a-button @click="handleOk" type="primary" >确定</a-button>
        </div>
      </div>
    </template>
  </BasicModal>
</template>

<script setup="props, {content}" lang="ts">
import {ref, unref, reactive,  onMounted} from 'vue'
import {BasicModal, useModalInner} from '/@/components/Modal'
import {PageWrapper} from '/@/components/Page'
import {
  DatePicker as ADatePicker,
  Select as ASelect,
  Input as AInput,
  Checkbox as ACheckbox,
  Popover as APopover,
  Switch as ASwitch,
  Radio as ARadio,
  Upload as AUpload,
  Table as ATable,
  Popconfirm as APopconfirm,
  Tabs as ATabs, message,Button as AButton
} from "ant-design-vue"
import CheckboxGroup from "ant-design-vue/lib/checkbox/Group";
import {useTabs} from '/@/hooks/web/useTabs';
import router from "/@/router";
import {Moment} from 'moment';
import {
  findPeriodByAccontId,
  findBzAll,
  findMaxJc,
  findCodeKmByPeriod, findMaxPingZhengQiJian
} from '/@/api/record/generalLedger/data';
import {findByImgid} from "/@/api/record/eletronicInvoice_data/eletronic_invoice";
import {useCompanyOperateStoreWidthOut} from "/@/store/modules/operate-company";
import {currentCyDatas, findCorpBankAll, getAdInfoDatas,getAllAccAuths} from "/@/api/record/system/financial-settings";
import {useUserStore} from "/@/store/modules/user";
import {getCurrentAccountName} from "/@/api/task-api/tast-bus-api";
//红字（无现金科目，请设置现金科目后再进行查询）
const {closeCurrent} = useTabs(router);
const userStore = useUserStore();
const ARangePicker = ADatePicker.RangePicker
const ASelectOption = ASelect.Option
const AInputSearch = AInput.Search
const ARadioGroup = ARadio.Group
const ARadioButton = ARadio.Button
const ACheckboxGroup = ACheckbox.Group
const ATabPane = ATabs.TabPane
const emit = defineEmits(['save','register']);

const data = []
const formItems: any = ref({})
let changeBeforeModel: any = {}
// 会计区间
const dateList: any = ref([])
// 会计科目
const kmList: any = ref([])
//级次
const jcList: any = ref([])
//币种
const bzList: any = ref([])
// 过滤
const filterGroup: any = ref([])
const endDate = ref<String>("")
const strDate = ref<String>("")
let endDateList: any = ref([])
let strDateList: any = ref([])
let time: any = ref<Moment[]>([]);
// 日期是否可选
let timeselflg: any = ref(true);
// 期间是否可选
let dateselflg: any = ref(false);
let minkmList: any = ref([])
let maxkmList: any = ref([])
const minKm = ref<string>()
const maxKm = ref<string>()

const jc = ref<string>()
const minJc = ref<string>()
const maxJc = ref<string>()

const showStyle = ref<string>('1')
const fontSize = ref<string>('1')
const bz = ref()

const maxPingzhengQj = ref('')

const ishaveRjz = ref<boolean>(true)
const isShowQjlh = ref<boolean>(false)

//查询条件
const seach: any = ref({})

const [register, {closeModal, setModalProps}] = useModalInner((data) => {
  // 方式2
  formItems.value.openOne = data.data.openOne
  changeBeforeModel = JSON.parse(JSON.stringify(formItems.value))
  setModalProps({minHeight: 400});
})

async function handleChangeStrDate() {
  if (strDate.value === undefined) {
    endDate.value = '';
    timeselflg.value = false;
  }
  // 开始日期变动  如果开始日期大于结束于日期 则重置结束日期
  if (endDate.value) {
    if (strDate.value > endDate.value) {
      endDate.value = '';
    }
    // 会计科目
    kmList.value = await findCodeKmByPeriod({
      strDate: strDate.value,
      endDate: endDate.value,
      accId: defaultAdName,
      userId:userStore.getUserInfo.id
    });
    timeselflg.value = true;
  }
}
function timechange() {
  dateselflg.value = time.value.length > 0;
}
async function handleChangeEndDate() {
  if (endDate.value === undefined) {
    strDate.value = '';
    timeselflg.value = false;
  }
  // 开始日期变动  如果结束日期小于开始日期 则重置开始日期
  if (strDate.value) {
    if (strDate.value > endDate.value) {
      strDate.value = '';
    }
    // 会计科目
    kmList.value = await findCodeKmByPeriod({
      strDate: strDate.value,
      endDate: endDate.value,
      accId: defaultAdName,
      userId:userStore.getUserInfo.id
    });
    timeselflg.value = true;
  }
}

async function focusStrDate() {
  if (endDate.value) {
    strDateList.value = dateList.value.filter(o => o.value <= endDate.value)
  } else {
    strDateList.value = dateList.value
  }
}

async function focusEndDate() {
  if (strDate.value) {
    endDateList.value = dateList.value.filter(o => o.value >= strDate.value)
  } else {
    endDateList.value = dateList.value
  }
}

const handleFocusMinKm = () => {
  //获取焦点时 如果最大科目已选择 则过滤大于最大科目的数据
  /*if (maxKm.value) {
    minkmList.value = kmList.value.filter(o => o.ccode <= maxKm.value)
  } else {
    minkmList.value = kmList.value
  }*/
  findCorpBankAll().then(res=>{
    minkmList.value =  res.items.filter(item=>item.icash == '1')
  });
};

const filterOption = (input: string, option: any) => {
  return option.value.toLowerCase().indexOf(input.toLowerCase()) >= 0;
};
// 已授权账套列表
const accAuthList = ref([])
// 账套授权信息
const userAuthMap = ref([])
const defaultAdName = getCurrentAccountName(false)
onMounted(async () => {
  // 获取所有账套信息 并过滤已授权的账套
  let accList = await getAdInfoDatas()
  // 获取当前用户的权限根据用户信息汇率
  userAuthMap.value = await getAllAccAuths({'userId': userStore.getUserInfo.id})
  accAuthList.value = filterAccListByAuth(accList,userAuthMap.value )
  //加载数据
  dateList.value = await findPeriodByAccontId(defaultAdName);
  //级次
  const num = await findMaxJc(defaultAdName);
  for (let i = 1; i < num + 1; i++) {
    jcList.value.push({
      'label': i,
      'value': i,
    });
  }
  //币种
  currentCyDatas({accId: getCurrentAccountName(true)}).then(res=>{
    bzList.value = res.items/*.filter(item=>item.foreignName.indexOf('人民币') == -1)*/
  });


  const qijian = await findMaxPingZhengQiJian();
  // 初始化默认区间
  if (qijian == ''){
    strDate.value = dateList.value[0].value
    endDate.value = dateList.value[dateList.value.length-1].value
  }else {
    maxPingzhengQj.value = qijian.substring(0,4)+'-'+qijian.substring(4,6)
    strDate.value = maxPingzhengQj.value
    endDate.value = maxPingzhengQj.value
  }
  // 是否集团
  let flag = useCompanyOperateStoreWidthOut().getAccountList[0].independent
  whetherGroup.value = (null == flag ? true : '0' == flag ? true : false)
  // 是否预算会计
  if (useCompanyOperateStoreWidthOut().getAccountList[0].ibudgetAccounting ||　false)  modelList[defaultTabsKey.value].constant.nature = 'YS'

  // 现金账户   findCorpBankAll()
  // 金币 currentCyDatas({accId: getThisSchemaName()})
});

function handleChange() {
}

function onChange() {
}
const confirm = ()=>{

}
const cancel = ()=>{

}
/*********Mr·Ye*********/
const defaultTabsKey = ref('1')
const whetherGroup = ref(false)

const defaultModel= {
  constant: {
 /*   tableStyle: 'J',
    tableFontSize: 'MAX',*/
    currency: '人民币元',
/*    nature: '',
    unaccounted: true,
    showCumulative: false*/
  },
  authority: {codes: [],types: []},
  variable: {
    periodStart: '',
    periodEnd: '',
    dateStart: '',
    dateEnd: '',
    voucherNumberStart: '',
    voucherNumberEnd: '',
    voucherType: '',
    summary: '',
    reviewStatus:'',
    bookStatus:'',
    subjectNumber: '',
    direction:'',
    amountMax: '',
    amountMin: '',
    numberMax: '',
    numberMin: '',
    wbAmountMax: '',
    wbAmountMin: '',
    billNumberStart: '',
    billNumberEnd: '',
    preparedMan: '',
    cashierMan: '',
    reviewMan: '',
    bookMan: '',
  }
}
const modelList = reactive({'1': {constant: {
 /*     tableStyle: 'J',
      tableFontSize: 'MAX',*/
      currency: '人民币元',
 /*     nature: '',
      unaccounted: true,
      showCumulative: false*/
    },
    authority: {codes: [],types: []},
    variable: {
      periodStart: '',
      periodEnd: '',
      dateStart: '',
      dateEnd: '',
      voucherNumberStart: '',
      voucherNumberEnd: '',
      voucherType: '',
      summary: '',
      reviewStatus:'',
      bookStatus:'',
      subjectNumber: '',
      direction:'',
      amountMax: '',
      amountMin: '',
      numberMax: '',
      numberMin: '',
      wbAmountMax: '',
      wbAmountMin: '',
      billNumberStart: '',
      billNumberEnd: '',
      preparedMan: '',
      cashierMan: '',
      reviewMan: '',
      bookMan: '',
    }},'2':{},'3':{
    constant: {
      tableStyle: 'J',
      tableFontSize: 'MAX',
      currency: '人民币元',
      nature: '',
      unaccounted: true,
      showCumulative: false
    },
    authority: {codes: [],types: []},
    variable: {
      periodStart: '',
      periodEnd: '',
      dateStart: '',
      dateEnd: '',
      accountNum: '',
      summary: '',
      direction: '',
      amountMax: '',
      amountMin: '',
    }
  },'4':{}})

const openOrClose = (visible:boolean)=>{
  // 初始化选中
  if (visible &&  null != formItems.value.openOne && Object.keys(modelList['3']).length != 0){
    //默认赋值
    defaultTabsKey.value = '3'
  }
}
const tabsChange  = (value)=>{
 /* if (value == '1'){
    modelList[defaultTabsKey.value] =  defaultmodelList[defaultTabsKey.value]
  }else if (value == '2'){
    modelList[defaultTabsKey.value] = modelList[value]
  }else if (value == '3'){
    modelList[defaultTabsKey.value] = modelList[value]
  }else if (value == '4'){
    modelList[defaultTabsKey.value]= modelList[value]
  }*/
}
function filterAccListByAuth(acclist,authList) {
  return acclist.filter(item=>{
    for(let i = 0;i < authList.length;i++){
      if (item.accId ==  authList[i].accId){
        return true
      }
    }
    return  false;
  })
}
function initAuthCondition(adName,authList) {
  // 获取当前选中的年度
  let newList = authList.filter(item=> item.accId == adName)
  let obj = newList[0]
  if (newList.length > 1){
    let startRq =  modelList[defaultTabsKey.value].variable.periodStart
    let dateStart =  modelList[defaultTabsKey.value].variable.dateStart
    let yearValue = ''
    if (startRq == ''){
      yearValue  = dateStart.substring(0,4)
    }else {
      yearValue  = startRq.substring(0,4)
    }
    obj = newList.filter(item => item.iyear == yearValue)[0]
  }
  if (obj.ccodeAll != '1')modelList[defaultTabsKey.value].authority.codes = JSON.parse(obj.codeListJson)
  if (obj.ccodeAll != '1')modelList[defaultTabsKey.value].authority.types = JSON.parse(obj.voucherTypeJson)
}
/*********Mr·Ye*********/
let isChanged: boolean = false

async function handleOk() {
  let startQj = strDate.value || ''
  let endQj = endDate.value || ''
  let startRq = null == riqi.value[0]? '':riqi.value[0].format('YYYY-MM-DD')
  let endRq = null == riqi.value[1]? '':riqi.value[1].format('YYYY-MM-DD')
  if ((startQj == '' &&  endQj == '') && riqi .value.length === 0) {
    message.error('请选择会计区间or日期期间!');
    return
  }
  if ((startQj != '' &&  endQj == '') || (startQj == '' &&  endQj != '')  ) {
    message.error('请完善会计区间');
    return
  }else if ((startQj != '' && endQj != '' && (startQj.substring(0,4) != endQj.substring(0,4)))){
    message.error('暂不支持跨越年度会计区间查询');
    return
  }else if ((startRq != '' && endRq != '' && (startRq.substring(0,4) != endRq.substring(0,4)))){
    message.error('暂不支持跨越年度日期区间查询');
    return
  }

  let pingS = modelList[defaultTabsKey.value].variable.voucherNumberStart
  let pingE = modelList[defaultTabsKey.value].variable.voucherNumberEnd
  if (pingS != '' || pingS !='' ){
    if(pingS == '' || pingE == ''){
      message.error('请完善凭证编号区间条件!');
      return
    }else if(Number(pingE) < Number(pingS)){
      message.error('凭证编号区间条件必须自小到大!');
      return
    }
  }
  let min = modelList[defaultTabsKey.value].variable.amountMin
  let max = modelList[defaultTabsKey.value].variable.amountMax
  if ((min != '' && max =='' ) || max != '' && min =='' ){
    message.error('请完善本币金额区间条件!');
    return
  }else if (parseFloat(min) > parseFloat(max)){
    message.error('本币金额区间条件必须自小到大!');
    return
  }
  let nMin = modelList[defaultTabsKey.value].variable.numberMin
  let nMax = modelList[defaultTabsKey.value].variable.numberMax
  if ((nMin != '' && nMax =='' ) || nMax != '' && nMin =='' ){
    message.error('请完善数量区间条件!');
    return
  }else if (parseFloat(nMin) > parseFloat(nMax)){
    message.error('数量条件必须自小到大!');
    return
  }

  let wbMin = modelList[defaultTabsKey.value].variable.wbAmountMin
  let wbMax = modelList[defaultTabsKey.value].variable.wbAmountMax
  if ((wbMin != '' && wbMax =='' ) || wbMax != '' && wbMin =='' ){
    message.error('请完善外币金额区间条件!');
    return
  }else if (parseFloat(wbMin) > parseFloat(wbMax)){
    message.error('外币金额区间条件必须自小到大!');
    return
  }

  modelList[defaultTabsKey.value].variable.periodStart = startQj
  modelList[defaultTabsKey.value].variable.periodEnd = endQj
  modelList[defaultTabsKey.value].variable.dateStart = startRq
  modelList[defaultTabsKey.value].variable.dateEnd = endRq
  // 存入自定义
  /*if (defaultTabsKey.value == '1' || defaultTabsKey.value == '3'){
    modelList['3'] = modelList[defaultTabsKey.value]
    modelList['3'].variable.summary = ''
    modelList['3'].variable.direction = ''
    modelList['3'].variable.amountMax = ''
    modelList['3'].variable.amountMin = ''
  }*/
  modelList[defaultTabsKey.value].openOne = formItems.value.openOne
  modelList[defaultTabsKey.value].accAuthList = accAuthList.value
  modelList[defaultTabsKey.value].userAuthMap = userAuthMap.value
  initAuthCondition(defaultAdName,userAuthMap.value)
  emit('save', unref(modelList[defaultTabsKey.value]))
  closeModal()
  return true
}
async function handleClose() {
  if (null != formItems.value.openOne && formItems.value.openOne == 1) {
    await closeCurrent()
   // router.push('/home/welcome')
  }
  closeModal()
}
// 查询

let radiovalue = ref(1);
const riqi: any = ref([]);
function radiochange(val) {
    kmList.value = [];
    timeselflg.value = val.target.value === 1;
    dateselflg.value = val.target.value === 2;
    if (val.target.value === 1) {
      riqi.value = '';
      strDate.value = maxPingzhengQj.value;
      endDate.value = maxPingzhengQj.value;
    } else {
      strDate.value = '';
      endDate.value = '';
    }
}
</script>
<style lang="less" scoped>
:deep(.ant-calendar-picker-input.ant-input), :deep(.ant-select-single:not(.ant-select-customize-input) .ant-select-selector),:deep(.ant-input-affix-wrapper) {
  border: none;
  border-bottom: solid 1px rgb(191, 191, 191) !important;
  width: 100%;
}

.nc-open-content {
  position: relative;
  .open-content-foot{
    display: block;
    position: fixed;
    margin-top: 43px;
  }
  .ant-tabs-tabpane-active{
    overflow-y: auto;
    height: 430px;
  }
  input {
    width: 35%;
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
  }

  .ant-select-selection-search-input {
    border-bottom: none !important;
  }

  .ant-input:focus {
    box-shadow: none;
  }

  :deep(.ant-select-selector) {
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
  }

  label {
    text-align: right;
    width: 110px;
    display: inline-block;
    padding: 5px 10px;
  }
  .ant-radio-group{
    .ant-radio-wrapper{
      width: 70px;
    }
  }
}


.open-content-title {
  > div {
    display: inline-block;
  }

  > div:nth-of-type(1) {
    width: 200px;
    background-color: #efeeee;
    color: black;
    font-size: 20px;
    text-align: center;
    padding: 5px 10px
  }

  > div:nth-of-type(2) {
    width: calc(100% - 200px);
    border-bottom: 1px #e6e3e3 solid;
    font-size: 18px;
    padding: 5px;

    > span {
      border-bottom: 4px #0960bd solid;
      padding: 6px 20px;
      color: #0960bd;
    }
  }
}

.open-content-up {
  position: relative;

  .ocup-position {
    position: absolute;
    left: 0;
    bottom: 100px;
    width: 200px;
    background-color: #efeeee;
    color: black;
    font-size: 20px;
    text-align: center;
    padding: 5px 10px;
  }

  ul {
    padding: 20px 40px;

    li {
      margin: 10px 0;

      span {
        font-size: 16px;
        color: #747272;
      }

      .ant-select {
        font-size: 16px;
      }
    }
  }
}

:deep(.ant-tabs-left-bar) {
  margin-right: 0px !important;
}

:deep(.ant-tabs-left) {
  .ant-tabs-tab {
    width: 200px;
    font-weight: bold;
    padding-right: 40px !important;
    margin-bottom: 0px !important;
  }

  .ant-tabs-tab-active {
    background-color: #0960bd !important;
    color: white !important;
  }

  .ant-tabs-tab:nth-of-type(3) {
    margin-top: 250px;
  }
}

</style>

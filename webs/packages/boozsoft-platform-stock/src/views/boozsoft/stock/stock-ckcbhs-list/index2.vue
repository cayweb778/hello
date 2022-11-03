<template>
  <div class="nci-hr">
    <div class="nc-hr-top">
      <div class="nc-hr-top-left">
        <div>
          <Avatar src="" :size="{ xs: 24, sm: 32, md: 40, lg: 64, xl: 80, xxl: 80 }">
            <CarryOutOutlined style="font-size: 30px;color: #146dc1;"/>
          </Avatar>
        </div>
        <div>
          <span style="font-size: 22px;">成本核算</span><br/>
          <span style="font-size: 14px;">核算出入库单据成本,将成本金额计入核算单据,标记成本核算状态</span>
        </div>
      </div>
      <div style="line-height: 75px;">
        <span style="font-size: 22px;">自动成本核算</span>
      </div>
      <div class="nc-hr-top-right">
        <div>
          <Rate v-model:value="rateValue" allow-half/>
        </div>
        <div>
          <span>了解自动成本核算</span>
        </div>
      </div>
    </div>
    <div class="nc-hr-middle">
      <div style="width: 20%;float: left;">
        <AccountPicker theme="three"  readonly=""/>
      </div>
      <div>
      </div>
      <div>
        <Button @click="closeCurrent()">
          退出
        </Button>
      </div>
    </div>
    <div class="nc-hrm-body" :style="{maxHeight: windowHeight+'px'}">
      <div style="text-align: center"><span>成本核算期间：</span><span>{{ pageParameter.riqi }}</span></div>
      <div>
        <Steps :current="stepValue" status="error">
          <Step title="开始成本核算" description=""/>
          <Step :title="stepValue < 2?'未完成':'已完成'" description="销售出库单成本核算"/>
          <Step :title="stepValue < 3?'未完成':'已完成'" description="材料领用出库单成本核算"/>
          <Step :title="stepValue < 4?'未完成':'已完成'" description="其他出库单成本核算"/>
          <Step :title="stepValue < 5?'未完成':'已完成'" description="产成品入库单"/>
          <Step :title="stepValue < 6?'结束成本核算':'已核算'" description="成本核算完成"/>
        </Steps>
      </div>
      <div>
        <Button type="primary" shape="round" size="large" v-if="!indicator"
                 @click="openQuery">重新核算
        </Button>
        <Button type="primary" shape="round" size="large" v-if="!indicator" style="margin-left: 25px;"
                :disabled="stepValue != 6" @click="closeCurrent(),router.push('/zhongZhang/home/welcome')">退出核算
        </Button>

        <Spin :indicator="indicator" :tip="`正在进行${tipText}...`" v-else="indicator"/>
      </div>
      <CancelClose @register="registerCancelPage"/>
      <Query @save="saveQuery" @register="registerQueryPage"/>
    </div>
  </div>
</template>
<script setup lang="ts">
import {CarryOutOutlined} from '@ant-design/icons-vue';
import {Input, Select, Button, Avatar, Steps, Rate, message, Spin} from "ant-design-vue";
import {onMounted, reactive, ref} from "vue";
import CancelClose from "/@/views/boozsoft/global/popup/CancelClose.vue";
import Query from './popup/query.vue'

const Step = Steps.Step
const InputSearch = Input.Search
const SelectOption = Select.Option
const windowHeight = (window.innerHeight - (280))
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker-STOCK.vue";
import {useTabs} from "/@/hooks/web/useTabs";
import router from "/@/router";
import {findNewestCloseMonth, closeBill} from "/@/api/record/system/financial-settings";
import {useUserStore, useUserStoreWidthOut} from "/@/store/modules/user";
import {
  compareTime,
  findByFunctionModule, markAnomaly,
  offsetToStr
} from "/@/api/task-api/tast-bus-api";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {useMessage} from "/@/hooks/web/useMessage";
import {useModal} from "/@/components/Modal";
import {
  breakNumTidy,
  checkAccvoucherBreakSign,
  checkAccvoucherError, checkAccvoucherNumber,execAccvoucherReciewOrBook,
  checkAccvoucherSequenceDate
} from "/@/api/record/system/accvoucher";
import {
  warehousingOne,
  warehousingTwo,
  warehousingThree,
} from "/@/api/record/stock/stock_cost";
import {findOption} from "/@/api/record/stock/stock-option";

const {closeCurrent} = useTabs(router);
const rateValue = ref(4)
const stepValue = ref(1)
const stepStatus = ref(1)
const indicator = ref(false)

const currMonth = ref('')
const currId = ref('')

const dynamicTenantId = ref('')
const dynamicAccId = ref('')
const dynamicYear = ref('')
const tipText = ref('')
const dynamicSettings = ref({})
const requestParameter = ref({

})
const [registerCancelPage, {openModal: openQueryPageM}] = useModal()

async function selectChange(v, y) {
  stepValue.value = 1
  tipText.value = '销售出库单成本核算'
  indicator.value = true
  setTimeout(async () => {//1
    if (await checkOne()) {
      stepValue.value = 2
      indicator.value = false
    } else {
      indicator.value = false
      return false;
    }
    tipText.value = '材料领用出库单成本核算'
    indicator.value = true
    setTimeout(async () => {//2
      if (await checkTwo()) {
        stepValue.value = 3
        indicator.value = false
      } else {
        indicator.value = false
        return false;
      }
      tipText.value = '其他出库单成本核算'
      indicator.value = true
      setTimeout(async () => {//3
        if ( await checkThree()) {
          stepValue.value = 4
          indicator.value = false
        }
        tipText.value = '产成品入库单'
        indicator.value = true
        setTimeout(async () => {//4
          if (await checkFour()) {
            stepValue.value = 5
            indicator.value = false
          } else {
            indicator.value = false
            return false;
          }
          tipText.value = '成本核算中'
          indicator.value = true
          setTimeout(async () => {//5
              if (await checkEven()) {
                stepValue.value = 6
                indicator.value = false
              } else {
                indicator.value = false
                return false;
              }
            }, 2000)
        }, 2000)
      }, 2000)
    }, 2000)
  }, 2000)

}

async function checkOne() {
  console.log(pageParameter)
  let res = await useRouteApi(warehousingOne, {schemaName: dynamicTenantId.value})(pageParameter)
  console.log(res)
  if (null != res) {
    return true;
  }
  return false;
}

const checkTwo = async () => {
  let res = await useRouteApi(warehousingTwo, {schemaName: dynamicTenantId.value})(pageParameter)
  if (null != res) {
    return true;
  }
  return false;
}

const checkThree = async () => {
  let res = await useRouteApi(warehousingThree, {schemaName: dynamicTenantId.value})(pageParameter)
  if (null != res) {
    return true;
  }
  return false;
}
const checkFour = async () => {
  return true;
}
const checkEven = async () => {
  return true;
}

const userStore: any = useUserStore();
const {createWarningModal, createConfirm} = useMessage();
const startBill = async () => {
  if (!await check()) return false
  router.push({
    path: '/zhongZhang/ends/ends-em-bill/bill-list',
    query: {}
  });
}

// 开始结账前检查
async function check() {
  let res = await useRouteApi(findByFunctionModule, {schemaName: dynamicTenantId.value})({iyear: dynamicYear.value})
  let checkMenu = ['月末结账', '填制凭证', '凭证列表']
  let msg = ''
  for (let i = 0; i < res.length; i++) {
    if (checkMenu.indexOf(res[i].functionModule) != -1 && res[i].state == '1') {
      // 校验时间
      if (res[i].time != '' && !compareTime(offsetToStr(res[i].time))) { // 超时
        msg = '提示：系统冲突！操作员【' +
          res[i].caozuoUnique +
          '】正在进行' + res[i].functionModule + '操作!不能继续进行期末结账操作，请销后再试，或联系财务主管清理该任务!'
        return false;
      } else {
        // 标记异常
        await useRouteApi(markAnomaly, {schemaName: dynamicTenantId.value})({
          id: res[i].id,
          iyear: res[i].iyear
        })
      }
    }
  }
  if (msg != '') {
    createWarningModal({title: '开始操作前检测', content: msg})
    return false
  }
  return true;
}

const che = () => {
  openQueryPageM(true, {})
}

const [registerQueryPage, {openModal: openQueryPage}] = useModal()
//文件导入
const val = ref({
  openOne: 0,
  total: 0
})
const openQuery = async () => {
  val.value.openOne = 0
  openQueryPage(true, {
    data: val
  })
}

const pageParameter: any = reactive({
  year: '',
  coCode: '',
  dateStart:'',
  dateEnd:'',
  ch:'',
  chname:''
})
async function saveQuery(data) {
  //查询 数据
  console.log(data)
  pageParameter.riqi = data.riqi
  pageParameter.type = data.type
  pageParameter.rkBcheck = data.rkBcheck
  pageParameter.ckBcheck = data.ckBcheck
  pageParameter.hsFlg = data.hsFlg
  dynamicTenantId.value = data.accId
  selectChange(data.accId, data.iyear)

}
onMounted( ()=>{
  val.value.openOne = 0
  openQueryPage(true, {
    data: val
  })
})
</script>
<style lang="less" scoped>
.nci-hr {
  padding: 10px;

  .nc-hr-top {
    font-weight: bold;
    color: white;
    height: 120px;
    width: 100%;
    background-color: #146dc1;
    display: inline-flex;
    justify-content: space-between;
    padding: 1.2% 5%;

    > div {
      display: inline-flex;
      justify-content: space-between;
    }

    .nc-hr-top-left {
      width: 400px;

      > div:nth-of-type(1) {
        width: 80px;
        height: 80px;
        border-radius: 50%;

        .ant-avatar {
          background-color: white;
        }
      }

      > div:nth-of-type(2) {
        padding: 4%;

        span:nth-of-type(2) {
          font-size: 22px;

        }
      }
    }

    .nc-hr-top-right {
      width: 360px;
      padding: 1% 0;

      > div:nth-of-type(1) {
        > span {
          font-size: 13px;
        }
      }

      > div:nth-of-type(2) {
        width: 180px;
        background-color: white;
        height: 50px;
        line-height: 50px;
        border-radius: 5px;
        text-align: center;
        cursor: pointer;
        color: #146dc1;

        > span {
          font-size: 13px;
        }
      }

      > div:nth-of-type(2):hover {
        background-color: #f1f1f1;
        color: rebeccapurple;
      }
    }
  }

  .nc-hr-middle {
    height: 50px;
    margin: .5% 0;
    line-height: 40px;
    background-color: #f1f1f1;
    padding: 5px;
    display: flex;
    justify-content: space-between;

    > div:nth-of-type(1) {
      width: 15%;
      text-align: center;
    }

    > div:nth-of-type(2) {
      > span {
        color: red;
      }
    }

    .ant-btn {
      border-color: #c9c9c9
    }
  }

  .nc-hrm-body {
    background-color: white;

    > div {
      width: 100%;
      height: 150px;
      padding: 3% 5%;
    }

    > div:nth-of-type(1) {
      font-size: 26px;
      font-weight: bold;

      span:nth-of-type(1) {
        color: #666666;
      }
    }

    > div:nth-of-type(2) {
      font-weight: bold;
    }

    > div:nth-of-type(3) {
      text-align: center;

    }
  }
}
</style>

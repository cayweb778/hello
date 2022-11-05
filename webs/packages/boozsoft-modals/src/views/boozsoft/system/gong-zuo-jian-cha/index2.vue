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
          <span>总账</span><br/>
          <span>期末例行工作点及任务</span>
        </div>
      </div>
      <div style="line-height: 75px;">
        <span style="font-size: 32px;">工作检查</span>
      </div>
      <div class="nc-hr-top-right">
        <div>
          <Rate v-model:value="rateValue" allow-half/>
        </div>
        <div>
          <span>了解期末工作检查步骤</span>
        </div>
      </div>
    </div>
    <div class="nc-hr-middle">
      <div>
        <AccountPicker theme="one" @reloadTable="dynamicAdReload"/>
      </div>
      <div>
      </div>
      <div>
        <!--        <Button @click="che()">
                  取消
                </Button> -->
        <Button @click="closeCurrent()">
          退出
        </Button>
      </div>
    </div>
    <div class="nc-hrm-body" :style="{maxHeight: windowHeight+'px'}">
      <div><span>检查期间：</span><span>{{ currMonth }}</span></div>
      <div>
        <Steps :current="stepValue" status="error">
          <Step title="开始工作检查" description=""/>
          <Step :title="stepValue < 2?'未完成':'已完成'" description="凭证暂存与错误"/>
          <Step :title="stepValue < 3?'未完成':'已完成'" description="凭证制单序时控制"/>
          <Step :title="stepValue < 4?'未完成':'已完成'" description="凭证自动补断号"/>
          <Step :title="stepValue < 5?'未完成':'已完成'" description="凭证是否已审核"/>
          <Step :title="stepValue < 6?'未完成':'已完成'" description="凭证是否已记账"/>
          <Step :title="stepValue < 7?'未完成':'已完成'" description="期间损益是否已结转本年利润"/>
          <Step :title="stepValue < 8?'结束工作检查':'前往结账'" description="立即前往期末结账"/>
        </Steps>
      </div>
      <div>
        <Button type="primary" shape="round" size="large" v-if="!indicator"
                :disabled="stepValue != 8" @click="startBill">立即前往结账
        </Button>
        <Spin :indicator="indicator" :tip="`正在进行${tipText}工作检查中...`" v-else="indicator"/>
      </div>
      <CancelClose @register="registerQueryPage"/>
    </div>
  </div>
</template>
<script setup lang="ts">
import {CarryOutOutlined} from '@ant-design/icons-vue';
import {Input, Select, Button, Avatar, Steps, Rate, message, Spin} from "ant-design-vue";
import {onMounted, ref} from "vue";
import CancelClose from "/@/views/boozsoft/system/qi-mo-jie-zhang/popup/CancelClose.vue";

const Step = Steps.Step
const InputSearch = Input.Search
const SelectOption = Select.Option
const props = defineProps(['modelValue'])
const windowHeight = (window.innerHeight - (280))
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker.vue";
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
const requestParameter = ref({startDate: '', endDate: ''})
const [registerQueryPage, {openModal: openQueryPageM}] = useModal()
const dynamicAdReload = async (obj) => {
  // 获取指定账未结账
  dynamicSettings.value = obj.settings
  dynamicTenantId.value = obj.accountMode
  dynamicAccId.value = obj.accId
  dynamicYear.value = obj.iyear
  selectChange(obj.accId, obj.iyear)
}

async function selectChange(v, y) {
  let dbE = await findNewestCloseMonth({accId: v, year: y})
  if (null != dbE) {
    currMonth.value = `${y}.${dbE.iperiodNum}`
    requestParameter.value.startDate = y + '-' + dbE.dateStart
    requestParameter.value.endDate = y + '-' + dbE.dateEnd
    currId.value = dbE.id
    stepValue.value = 1
    tipText.value = '凭证暂存与错误'
    indicator.value = true
    setTimeout(async () => {//1
      if (await checkOne()) {
        stepValue.value = 2
        indicator.value = false
      } else {
        indicator.value = false
        return false;
      }
      tipText.value = '凭证制单序时控制'
      indicator.value = true
      setTimeout(async () => {//2
        if (dynamicSettings.value.zhiDanList.indexOf('ichronological') != -1 && await checkTwo()) {
          stepValue.value = 3
          indicator.value = false
        } else {
          indicator.value = false
          return false;
        }
        tipText.value = '凭证自动补断号'
        indicator.value = true
        setTimeout(async () => {//3
          if (dynamicSettings.value.pingZhenNumberList.indexOf('ibreakCode') != -1 && await checkThree()) {
            stepValue.value = 4
            indicator.value = false
          } else {
            if (dynamicSettings.value.pingZhenNumberList.indexOf('ibreakCode') != -1) {
              createConfirm({
                iconType: 'warning',
                title: '凭证断号整理',
                content: '是否自动执行凭证断号整理？',
                onOk: async () => {
                  await breakNumTidy({
                    periodStart: requestParameter.value.startDate,
                    periodEnd: requestParameter.value.endDate,
                    type: 'query'
                  })
                  indicator.value = false
                },
                onCancel: async () => {
                  indicator.value = false
                  return false;
                }
              });
            } else {
              indicator.value = false
              return false;
            }
          }
          tipText.value = '凭证是否已审核'
          indicator.value = true
          setTimeout(async () => {//4
            if (dynamicSettings.value.pingZhenControlList.indexOf('ifVerify') != -1 && await checkFour('review')) {
              stepValue.value = 5
              indicator.value = false
            } else {
              indicator.value = false
              return false;
            }
            tipText.value = '凭证是否已记账'
            indicator.value = true
            setTimeout(async () => {//5
              if (await checkFour('book')) {
                stepValue.value = 6
                indicator.value = false
              } else {
                indicator.value = false
                return false;
              }
              tipText.value = '期间损益是否已结转本年利润'
              indicator.value = true
              setTimeout(async () => {//6
                if (false) { // 期间损益
                  stepValue.value = 7
                  stepValue.value = 8
                  indicator.value = false
                } else {
                  if (dynamicSettings.value.pingZhenNumberList.indexOf('ibreakCode') != -1) {
                    createConfirm({
                      iconType: 'warning',
                      title: '损益结转凭证',
                      content: '是否自动生成期间损益结转凭证？',
                      onOk: async () => {
                        stepValue.value = 7
                        stepValue.value = 8
                        indicator.value = false
                      },
                      onCancel: async () => {
                        indicator.value = false
                        return false;
                      }
                    });
                  } else {
                    indicator.value = false
                    return false;
                  }
                }
              }, 2000)
            }, 2000)
          }, 2000)
        }, 2000)
      }, 2000)
    }, 2000)
  }
}

async function checkOne() {
  let res = await useRouteApi(checkAccvoucherError, {schemaName: dynamicTenantId.value})(requestParameter.value)
  if (null != res) {
    createWarningModal({title: '开始操作前检测', content: `凭证号【${res}】为暂存/错误凭证，请修改成正常凭证或删除该凭证！`})
    return false;
  }
  return true;
}

const checkTwo = async () => {
  let res = await useRouteApi(checkAccvoucherSequenceDate, {schemaName: dynamicTenantId.value})(requestParameter.value)
  if (null != res) {
    createWarningModal({title: '开始操作前检测', content: `凭证号【${res}】未按照序时规则处理，请将凭证号按日期顺序进行编号处理！`})
    return false;
  }
  return true;
}

const checkThree = async () => {
  let res = await useRouteApi(checkAccvoucherBreakSign, {schemaName: dynamicTenantId.value})(requestParameter.value)
  if (null != res) {
    createWarningModal({title: '开始操作前检测', content: `凭证号【${res}】号开始出现断号，请通过凭证列表的“整理”功能整理凭证断号！`})
    return false;
  }
  return true;
}

const checkFour = async (type) => {
  let res = await useRouteApi(checkAccvoucherNumber, {schemaName: dynamicTenantId.value})({
    startDate: requestParameter.value.startDate,
    endDate: requestParameter.value.endDate,
    type: type
  })
  if (null != res) {
    let v  = await new Promise((r,e)=>{
      createConfirm({
        iconType: 'warning',
        title: `自动处理凭证`,
        content: `凭证号【${res}】未完成${type == 'review' ? '审核' : '记账'},是否自动进行${type == 'review' ? '审核' : '记账'}操作？`,
        onOk: async () => {
          r(true)
        },
        onCancel: async () => {
          r(false)
        }
      });
    })
    if (v && await check()){
     let res1 = (await useRouteApi(execAccvoucherReciewOrBook, {schemaName: dynamicTenantId.value})({
        startDate: requestParameter.value.startDate,
        endDate: requestParameter.value.endDate,
        type: type,operator: userStore.getUserInfo.username
      }))
      if (res1 > 0) message.success(`成功完成${res}张凭证${type == 'review' ? '审核' : '记账'}操作!`)
      return  null != res1
    }else {
      return  false
    }
  }
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

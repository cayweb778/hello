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
        <span style="font-size: 32px;">期末结账</span>
      </div>
      <div class="nc-hr-top-right">
        <div>
          <Rate v-model:value="rateValue" allow-half/>
        </div>
        <div>
          <span>了解月末工作检查步骤</span>
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
      <div><span>结账期间：</span><span>{{ currMonth }}</span></div>
      <div>
        <Steps :current="stepValue" status="error">
          <Step title="开始工作检查" description=""/>
          <Step :title="stepValue < 2?'未完成':'已完成'" description="工作检查中"/>
          <Step :title="stepValue < 3?'未完成':'已完成'" description="月末结账"/>
        </Steps>
      </div>
      <div>
        <Button type="primary" shape="round" size="large" v-if="!indicator"
                :disabled="stepValue != 2" @click="startBill">
          开始结账
        </Button>
        <Spin :indicator="indicator" :tip="`正在进行${tipText}工作检查中...`" v-else="indicator"/>
      </div>
      <CancelClose
        @register="registerQueryPage"
      />
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
import {useUserStore} from "/@/store/modules/user";
import {compareTime, findByFunctionModule, markAnomaly, offsetToStr} from "/@/api/task-api/tast-bus-api";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {useMessage} from "/@/hooks/web/useMessage";
import {useModal} from "/@/components/Modal";

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

const [registerQueryPage, {openModal: openQueryPageM}] = useModal()

const dynamicAdReload = async (obj) => {
  // 获取指定账未结账
  dynamicTenantId.value = obj.accountMode
  dynamicAccId.value = obj.accId
  dynamicYear.value = obj.iyear
  selectChange(obj.accId, obj.iyear)
}

async function selectChange(v, y) {
  let dbE = await findNewestCloseMonth({accId: v, year: y})
  if (null != dbE) {
    currMonth.value = `${y}.${dbE.iperiodNum}`
    currId.value = dbE.id
    stepValue.value = 1
    tipText.value = '检查中'
    indicator.value = true
    setTimeout(() => {
      if (null != dbE.id) {
        stepValue.value = 2
        indicator.value = false
      } else {
        indicator.value = false
        return false;
      }
      tipText.value = '月末结账'
      indicator.value = true
      setTimeout(() => {
        if (dbE.gl == '1') {
          stepValue.value = 3
          indicator.value = false
        } else {
          indicator.value = false
          return false;
        }
      }, 2000)
    }, 2000)
  }
}

const userStore: any = useUserStore();

const {createWarningModal} = useMessage();

const startBill = async () => {
  if (!await check()) return false
  tipText.value = '月末结账'
  indicator.value = true
  let e = await closeBill({
    id: currId.value,
    accId: dynamicAccId.value,
    operator: userStore.getUserInfo.realName
  })
  setTimeout(() => {
    indicator.value = false
    if (null != e) {
      message.success('已成功完成结账')
      stepValue.value = 4
    }
  }, 2000)
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
        await useRouteApi(markAnomaly, {schemaName: dynamicTenantId.value})({id: res[i].id,iyear:res[i].iyear})
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

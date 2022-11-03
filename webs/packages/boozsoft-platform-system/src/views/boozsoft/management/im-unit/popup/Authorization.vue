<template>
  <BasicModal
    width="680px"
    v-bind="$attrs"
    title="公司（单位）操作授权"
    @ok="handleOk"
    @cancel="handleClose"
    :loading="modelLoadIng"
    :canFullscreen="false"
    @register="register"
  >
    <div class="open_div_one">
      <ul>
        <li>
          <span>选择授权单位：</span>
          <a-select
            :allowClear="true"
            v-model:value="pageParameter.accId"
            :filter-option="filterOption"
            show-search
            placeholder=""
            @change="queryChange(true)"
            style="width: 320px"
          >
            <a-select-option v-for="(item,index) in unitList" :key="index" :value="item.accId" :label="item.coCode+''+item.accNameCn">
              {{item.coCode}}&emsp;{{ item.accNameCn }}
            </a-select-option>
          </a-select>
          <span style="margin-left: 3em;">授权年度:</span>
          <a-select
            v-model:value="pageParameter.year"
            placeholder=""
            @change="queryChange(false)"
            style="width: 100px"
          >
            <a-select-option v-for="(value,index) in yearList" :key="index" :value="value">{{
                value
              }}
            </a-select-option>
          </a-select>
        </li>
        <li>
          <span>单位主管：</span>
          <a-select
            :allowClear="true"
            v-model:value="pageParameter.supervisors"
            show-search
            mode="multiple"
            placeholder=""
            style="width: 80%"
            @change="superChange"
          >
            <a-select-option v-for="(item,index) in userList" :key="index" :value="item.id">
              {{ item.realName }}
            </a-select-option>
          </a-select>
        </li>
      </ul>
    </div>
    <a-transfer
      :titles="['待授权操作员', '已授权操作员']"
      :operations="['授权', '取消']"
      :render="item => item.title"
      :list-style="{width: '250px',height: '260px'}"
      :selected-keys="selectedKeys"
      :showSelectAll="false"
      :target-keys="targetKeys"
      :data-source="filterDatas"
      @change="transferHandleChange"
      @selectChange="transferHandleSelectChange"
    />
    <div class="open_div_two">
      <span>授权说明</span>
      <div>
        <p>1.授权公司(单位)必须是状态为正常的公司(单位);</p>
        <p>2.操作员必须是状态为正常的操作员;</p>
        <p>3.选择主管则具有当前公司的所有操作权限和数据权限;</p>
        <p>4.操作员授权在正常年结时将自动赋予下一年度权限。</p>
      </div>
    </div>
  </BasicModal>
</template>

<script setup="props, {content}" lang="ts">
import {ref, unref, reactive, onMounted, watch, computed} from 'vue'
import {BasicModal, useModalInner} from '/@/components/Modal'
import {
  message, Transfer as ATransfer, Input as AInput, Select as ASelect
} from "ant-design-vue"
import {getCurrentAccountName, hasBlank} from "/@/api/task-api/tast-bus-api";
import {JsonTool} from "/@/api/task-api/tools/universal-tools";
import {
  findAuthors,
  findYearListByAccId,
  getUnitAvailables,
  modifyAuthor
} from "/@/api/record/group/im-unit";
import {findAllByGroupSysUserFlag} from "/@/api/record/accpanel/data";
import {findPeriodYears} from "/@/api/record/generalLedger/data";
import {recrawlResetAccountCache} from "/@/boozsoft/components/AccountPicker/utils/accountUtils";

const emit = defineEmits(['register', 'modify','log'])
const ASelectOption = ASelect.Option
const targetKeys: any = ref([])
const selectedKeys: any = ref([])
let filterDatas = ref([])
let filterDatasCopy = []


const unitList: any = ref([])
const yearList: any = ref(['2020', '2021'])
const userList = ref([])
const modelLoadIng = ref(false)
const defaultParm = getCurrentAccountName(true)

const pageParameter = reactive({
  accId: '',
  year: '',
  supervisors: [],
  authorizes: []
})

onMounted(async () => {

});

let changeBeforeModel: any = {}
let opentargetKeysLength = 0

const [register, {closeModal, setModalProps}] = useModalInner(async (data) => {
  // 判断当前辅助核算类型
  targetKeys.value = []
  filterDatas.value = []
  try {
    modelLoadIng.value = true
    //获取基础参数
    let arr = defaultParm.split('-')
    pageParameter.accId = arr[0] + '-' + arr[1]
    unitList.value = (await getUnitAvailables()).filter(it=>!hasBlank(it?.accStandard) && !hasBlank(it?.startPeriod))
    userList.value = await findAllByGroupSysUserFlag()
    yearList.value = await findPeriodYears(pageParameter.accId)
    pageParameter.year = yearList.value[0]
    if (userList.value.length > 0) userList.value.forEach(item => filterDatas.value.push({
      key: item.id,
      title: item.realName
    }))
    filterDatasCopy = filterDatas.value
    // 获取数据库已存在信息
    await reloadTransfer(arr[0] + '-' + arr[1], arr[2])
  } catch (e) {
    modelLoadIng.value = false
    message.error('请检查查询参数！')
  }
  modelLoadIng.value = false
  setModalProps({minHeight: 425});
})

// 过滤设置
const transferHandleChange = (nextTargetKeys: string[]) => {
  targetKeys.value = nextTargetKeys;
}
const transferHandleSelectChange = (sourceSelectedKeys: string[], targetSelectedKeys: string[]) => {
  selectedKeys.value = [...sourceSelectedKeys, ...targetSelectedKeys];
}

const handleOk = () => {
  if (hasBlank(pageParameter.accId) || hasBlank(pageParameter.year)) {
    message.info('单位与年度必选项！')
    return false
  }
  // 判断有无变化
  let ifChange = false
  ifChange = !(pageParameter.accId == changeBeforeModel.accId
    && pageParameter.year == changeBeforeModel.year
    && JsonTool.json(pageParameter.supervisors) == JsonTool.json(changeBeforeModel.supervisors)
    && JsonTool.json(targetKeys.value) == JsonTool.json(changeBeforeModel.authorizes))
  if (ifChange) {
    pageParameter.authorizes = targetKeys.value
    let res = modifyAuthor(pageParameter)
    if (res != null) {
      message.info('授权成功！')
      changeBeforeModel = JsonTool.parseProxy(pageParameter)
       recrawlResetAccountCache()
    }
    closeModal()
  } else {
    message.info('暂未发现任何修改记录！')
  }
  return false;
}

const handleClose = () => {
  closeModal()
  return true;
}

const reloadTransfer = async (accId, year) => {
  let data = {accId: accId, year: year}
  await findAuthors(data).then(res => {
    if (null != res) {
      pageParameter.supervisors = res.filter(item => item.supervisor == '1').map(item => item.userNum)
      targetKeys.value = res.filter(item => null == item.supervisor || item.supervisor != '1').map(item => item.userNum)
      pageParameter.authorizes = targetKeys.value
      changeBeforeModel = JsonTool.parseProxy(pageParameter)
      superChange(pageParameter.supervisors)
      modelLoadIng.value = false
    } else {
      message.error('请检查查询参数！')
    }
  }).catch(() => message.error('请检查查询参数！'))
}

const queryChange = async (flag) => {
  if (hasBlank(pageParameter.accId)) {
    return false
  } else {
    modelLoadIng.value = true
    if (flag) {
      yearList.value = await findPeriodYears(pageParameter.accId)
      pageParameter.year = yearList.value[0]
    }
    if (hasBlank(pageParameter.year)) {
      message.warning('请先初始化该账户下会计期间！')
      modelLoadIng.value = false
    } else {
      await reloadTransfer(pageParameter.accId, pageParameter.year)
    }
  }
}

const superChange = (vlas) => {
  filterDatas.value = filterDatasCopy.filter(item => vlas.indexOf(item.key) == -1)
  targetKeys.value = targetKeys.value.filter(key => vlas.indexOf(key) == -1)
  selectedKeys.value = selectedKeys.value.filter(key => vlas.indexOf(key) == -1)
}

const filterOption = (input: string, option: any) => {
  return option.label.indexOf(input) >= 0;
};
</script>
<style lang="less" scoped>
.open_div_one {
  height: 100px;
  width: 100%;
  clear: both;
  text-align: center;

  ul {
    li {
      margin: 2% 0;

      :deep(.ant-select-selector) {
        border: none;
        border-bottom: 1px solid #ABAAAAFF;
      }
    }
  }
}

.ant-transfer {
  padding-left: 5.5%;
}

.open_div_two {
  width: 90%;
  margin: 4% 5% 0;
  border: 1px solid #ABAAAAFF;
  position: relative;
  padding: 10px;

  span {
    position: absolute;
    display: inline-block;
    width: 100px;
    text-align: center;
    background-color: white;
    top: -12px;
    left: 20px;
    font-weight: bold;
  }

  div {
    p {
      margin: 3px 5px;
    }
  }
}
</style>

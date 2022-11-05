<template>
  <BasicModal
    width="500px"
    v-bind="$attrs"
    title="''"
    @ok="handleOk"
    @cancel="handleClose"
    :loading="modelLoadIng"
    :canFullscreen="false"
    :showOkBtn="showOk"
    @register="register"
  >
    <template #title>
      <div style="display: flex;" class="vben-basic-title">
        <img src="/@/assets/images/create.svg" style="width:25px;margin-right: 10px;"/>
        <span style="line-height: 25px;font-size: 16px;">管理类别授权</span>
      </div>
    </template>
    <div class="open_div_one">
      <ul>
        <li>
          <RadioGroup name="radioGroup" v-model:value="pageParameter.supervisors"
                      @change="radioChange"
                      style="margin: 0;">
            <Radio style="font-weight: bold" value="0">独立类别</Radio>
            <Radio style="font-weight: bold" value="1">主管（全部类别）</Radio>
          </RadioGroup>
        </li>
        <li>
          <span>操作员：&emsp;&emsp;&emsp;</span>
          <Select
            :allowClear="true"
            v-model:value="pageParameter.userNum"
            show-search
            placeholder=""
            @change="operateChange"
            style="width: 320px"
          >
            <SelectOption v-for="(item,index) in userList" :key="index" :value="item.id">
              {{ item.realName }}
            </SelectOption>
          </Select>
        </li>
        <li>
          <span>管理代码：&emsp;&emsp;</span>
          <Select :allowClear="true"
                  v-model:value="pageParameter.authorizes"
                  :filter-option="filterOption"
                  mode="multiple"
                  :disabled="pageParameter.supervisors == '1'"
                  placeholder=""
                  style="width: 320px">
            <SelectOption v-for="(item,index) in unitList" :key="index" :value="item.split('==')[0]"
                          :label="'FA'+item.split('==')[1]">
              {{ 'FA' + item.split('==')[1] }}
            </SelectOption>
          </Select>
        </li>
      </ul>
    </div>
  </BasicModal>
</template>

<script setup="props, {content}" lang="ts">
import {ref, reactive, onMounted,} from 'vue'
import {BasicModal, useModalInner} from '/@/components/Modal'
import {
  message, Select, Radio
} from "ant-design-vue";

const RadioGroup = Radio.Group;
const SelectOption = Select.Option;
import {hasBlank} from "/@/api/task-api/tast-bus-api";
import {JsonTool} from "/@/api/task-api/tools/universal-tools";
import {
  findAssetsUniqueCodeAndCode, findByAuthAssetsByUniqueCode,
  modifyManageAuthor
} from "/@/api/record/group/im-unit";
import {findAllByGroupSysUserFlag} from "/@/api/record/accpanel/data";
import Template from "/@/views/boozsoft/system/report-template-zcfzb/template.vue";

const emit = defineEmits(['register'])

const unitList: any = ref([])
const userList = ref([])
const modelLoadIng = ref(false)
const showOk = ref(false)

const pageParameter = reactive({
  supervisors: '0',
  userNum: '',
  authorizes: []
})

const [register, {closeModal, setModalProps}] = useModalInner(async (data) => {

  try {
    modelLoadIng.value = true
    //获取基础参数
    userList.value = await findAllByGroupSysUserFlag()
    if (userList.value.length > 0) pageParameter.userNum = userList.value[0].id
    unitList.value = (await findAssetsUniqueCodeAndCode({code: data.schemaName.substring(0, data.schemaName.length - 5)}))
    if (unitList.value.length > 1) {
      showOk.value = true
      // 查询指定已存在权限
      await operateChange(userList.value[0].id)
    } else {
      showOk.value = false
      message.info('当前公司下只存在一个固定资产管理账,无需在进行授权！')
    }
  } catch (e) {
    modelLoadIng.value = false
    message.error('请检查查询参数！')
  }
  modelLoadIng.value = false
  setModalProps({minHeight: 200});
})

let changeBeforeModel: any = {}
const handleOk = () => {
  if (hasBlank(pageParameter.userNum)) {
    message.info('操作员必选项！')
    return false
  }
  if (pageParameter.supervisors == '0' && hasBlank(pageParameter.authorizes)) {
    message.info('管理代码为必填项！')
    return false
  }
  // 判断有无变化
  let ifChange = false
  ifChange = !(
    pageParameter.userNum == changeBeforeModel.userNum
    && pageParameter.supervisors == changeBeforeModel.supervisors
    && JsonTool.json(pageParameter.authorizes) == JsonTool.json(changeBeforeModel.authorizes)
  )
  if (ifChange) {
    let res = modifyManageAuthor(pageParameter)
    if (res != null) {
      message.success('管理授权成功！')
      changeBeforeModel = JsonTool.parseProxy(pageParameter)
    }
  } else {
    message.info('暂未发现任何修改记录！')
  }
  return false;
}

const radioChange = (e) => {
  pageParameter.authorizes = []
}

const operateChange = async (v) => {
  if (showOk.value) {
    let codes = (await findByAuthAssetsByUniqueCode({code: v})).map(it => it.manageUniqueCode)
    if (null != codes && codes.length > 0) {
      if (null == codes[0]) {
        pageParameter.supervisors = '1'
        pageParameter.authorizes = []
      } else {
        pageParameter.supervisors = '0'
        pageParameter.authorizes = codes
      }
    } else {
      pageParameter.supervisors = '0'
      pageParameter.authorizes = []
    }
    changeBeforeModel = JsonTool.parseProxy(pageParameter)
  }
}

const handleClose = () => {
  closeModal()
  return true;
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

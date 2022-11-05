<template>
  <BasicModal
    width="900px"
    v-bind="$attrs"
    title="科目余额表查询"
    @ok="handleOk()"
    @cancel="handleClose()"
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
        <a-tabs type="card" tabPosition="left" style="height: 400px">
          <a-tab-pane key="1" tab="标准模式">
            <ul>
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
                  @search="handleSearch"
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
                  :filter-option="false"
                  :not-found-content="null"
                  @search="handleSearch"
                  @change="handleChange"
                >
                  <a-select-option v-for="d in data" :key="d.value">
                    {{ d.text }}
                  </a-select-option>
                </a-select>
              </li>
              <li>
                <a-radio-group  @change="onChange" style="width: 100%">
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
              <li><span>科目性质：</span>
                <a-select
                  style="width: 80%"
                >
                  <a-select-option value="" label="全部">
                    全部
                  </a-select-option>
                  <a-select-option value="财务会计" label="财务会计">
                    财务会计
                  </a-select-option>
                  <a-select-option value="预算会计" label="预算会计">
                    预算会计
                  </a-select-option>
                </a-select>
              </li>
              <li><span>样&emsp;&emsp;式：</span> <a-select default-value="1" style="width: 185px" @change="handleChange">
                <a-select-option value="1">
                  金额式
                </a-select-option>
                <a-select-option value="2">
                  数量金额式
                </a-select-option>
                <a-select-option value="3" >
                  外币金额式
                </a-select-option>
                <a-select-option value="4">
                  数量外币式
                </a-select-option>
              </a-select>
                <span>&emsp;字&emsp;&emsp;号：</span> <a-select default-value="1" style="width: 185px" @change="handleChange">
                  <a-select-option value="1">
                    大号字体
                  </a-select-option>
                  <a-select-option value="2">
                    小号字体
                  </a-select-option>
                </a-select>
              </li>
              <li><span>币&emsp;&emsp;种：</span> <a-select default-value="1" style="width: 200px" @change="handleChange">
                <a-select-option value="1">
                  人民币
                </a-select-option>
                <a-select-option value="2">
                  美元
                </a-select-option>
                <a-select-option value="3" >
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
            </a-checkbox-group>
          </a-tab-pane>
          <a-tab-pane key="2" tab="集团模式">
            <ul>
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
                  @search="handleSearch"
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
                  :filter-option="false"
                  :not-found-content="null"
                  @search="handleSearch"
                  @change="handleChange"
                >
                  <a-select-option v-for="d in data" :key="d.value">
                    {{ d.text }}
                  </a-select-option>
                </a-select>
              </li>
              <li>
                <a-radio-group  @change="onChange" style="width: 100%">
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
              <li><span>样&emsp;&emsp;式：</span> <a-select default-value="1" style="width: 185px" @change="handleChange">
                <a-select-option value="1">
                  金额式
                </a-select-option>
                <a-select-option value="2">
                  数量金额式
                </a-select-option>
                <a-select-option value="3" >
                  外币金额式
                </a-select-option>
                <a-select-option value="4">
                  数量外币式
                </a-select-option>
              </a-select>
                <span>&emsp;字&emsp;&emsp;号：</span> <a-select default-value="1" style="width: 185px" @change="handleChange">
                  <a-select-option value="1">
                    大号字体
                  </a-select-option>
                  <a-select-option value="2">
                    小号字体
                  </a-select-option>
                </a-select>
              </li>
              <li><span>币&emsp;&emsp;种：</span> <a-select default-value="1" style="width: 200px" @change="handleChange">
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
              </a-select></li>
            </ul>
            <a-checkbox-group @change="onChange">
              <a-checkbox value="A" style="width: 200px">
                包含未记账
              </a-checkbox>
              <a-checkbox value="B" style="width: 200px">
                期间无发生显示累计
              </a-checkbox>
            </a-checkbox-group>
          </a-tab-pane>
          <a-tab-pane key="3" tab="自定义模式">
            <ul>
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
                  @search="handleSearch"
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
                  :filter-option="false"
                  :not-found-content="null"
                  @search="handleSearch"
                  @change="handleChange"
                >
                  <a-select-option v-for="d in data" :key="d.value">
                    {{ d.text }}
                  </a-select-option>
                </a-select>
              </li>
              <li>
                <a-radio-group  @change="onChange" style="width: 100%">
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
              <li><span>样&emsp;&emsp;式：</span> <a-select default-value="1" style="width: 185px" @change="handleChange">
                <a-select-option value="1">
                  金额式
                </a-select-option>
                <a-select-option value="2">
                  数量金额式
                </a-select-option>
                <a-select-option value="3" >
                  外币金额式
                </a-select-option>
                <a-select-option value="4">
                  数量外币式
                </a-select-option>
              </a-select>
                <span>&emsp;字&emsp;&emsp;号：</span> <a-select default-value="1" style="width: 185px" @change="handleChange">
                  <a-select-option value="1">
                    大号字体
                  </a-select-option>
                  <a-select-option value="2">
                    小号字体
                  </a-select-option>
                </a-select>
              </li>
              <li><span>币&emsp;&emsp;种：</span> <a-select default-value="1" style="width: 200px" @change="handleChange">
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
              </a-select></li>
            </ul>
            <a-checkbox-group @change="onChange">
              <a-checkbox value="A" style="width: 200px">
                包含未记账
              </a-checkbox>
              <a-checkbox value="B" style="width: 200px">
                期间无发生显示累计
              </a-checkbox>
            </a-checkbox-group>
          </a-tab-pane>
        </a-tabs>
      </div>
    </div>
  </BasicModal>
</template>
<script setup="props, {content}" lang="ts">
import {ref, unref} from 'vue'
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
  Tabs as ATabs
} from "ant-design-vue"
import CheckboxGroup from "ant-design-vue/lib/checkbox/Group";
import { useTabs } from '/@/hooks/web/useTabs';
import router from "/@/router";
const { closeCurrent } = useTabs(router);
const ARangePicker = ADatePicker.RangePicker
const ASelectOption = ASelect.Option
const AInputSearch = AInput.Search
const ARadioGroup = ARadio.Group
const ARadioButton = ARadio.Button
const ACheckboxGroup = ACheckbox.Group
const ATabPane = ATabs.TabPane
// const {emit} = useContext()
const emit='错误。asmdasdsa'
const data = []
const formItems: any = ref({})

let changeBeforeModel: any = {}

const [register, {closeModal, setModalProps}] = useModalInner((data) => {
  // 方式2
  formItems.value.openOne = data.data.openOne
  changeBeforeModel = JSON.parse(JSON.stringify(formItems.value))
  setModalProps({minHeight: 400});
})
let isChanged: boolean = false

async function handleOk() {
  emit('save', unref(formItems))
  closeModal()
  return true
}
async function handleClose() {
  if (null != formItems.value.openOne && formItems.value.openOne == 1){
    await closeCurrent()
    router.push('/home/welcome')
  }
}

const handleSearch =()=>{

}
const onChange =()=>{

}
const handleChange =()=>{

}
</script>
<style lang="less" scoped>
:deep(.ant-calendar-picker-input.ant-input), :deep(.ant-select-single:not(.ant-select-customize-input) .ant-select-selector) {
  border: none;
  border-bottom: solid 1px rgb(191, 191, 191) !important;
  width: 100%;
}
.nc-open-content {
  input {
    width: 35%;
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
  }
  .ant-select-selection-search-input{
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


}


.open-content-title{
  > div{
    display: inline-block;
  }
  >div:nth-of-type(1){
    width: 200px;
    background-color: #efeeee;
    color: black;
    font-size: 20px;
    text-align: center;
    padding: 5px 10px
  }
  >div:nth-of-type(2){
    width: calc(100% - 200px);
    border-bottom: 1px #e6e3e3 solid;
    font-size: 18px;
    padding: 5px;
    >span{
      border-bottom: 4px #0960bd solid;
      padding: 6px 20px;
      color: #0960bd;
    }
  }
}
.open-content-up{
  position: relative;
  .ocup-position{
    position: absolute;left: 0;bottom: 100px;
    width: 200px;
    background-color: #efeeee;
    color: black;
    font-size: 20px;
    text-align: center;
    padding: 5px 10px;
  }
  ul{
    padding: 20px 40px;
    li{
      margin: 10px 0;
      span{
        font-size: 16px;
        color: #747272;
      }
      .ant-select {
        font-size: 16px;
      }
    }
  }
}

:deep(.ant-tabs-left-bar){
  margin-right: 0px !important;
}

:deep(.ant-tabs-left){
    .ant-tabs-tab{
      width: 200px;
      font-weight: bold;
      padding-right: 40px !important;
      margin-bottom: 0px!important;
    }
    .ant-tabs-tab:nth-of-type(3){
      margin-top: 220px;
    }
    .ant-tabs-tab-active{
      background-color: #0960bd !important;
      color: white !important;
    }
}

</style>

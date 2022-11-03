<template>
  <BasicModal
    width="700px"
    :height="450"
    class="spaceLogo"
    v-bind="$attrs"
    title="入库单查询"
    @ok="handleOk()"
    @cancel="handleClose()"
    :canFullscreen="false"
    :footer="null"
    @visible-change="openOrClose"
    @register="register"
    :loading="modelLoadIng"
    loadingTip="查询参数初始化中"
  >
    <template #title>
      <div style="height: 30px;width: 100%;background-color: #5f375c;color: white;line-height: 30px;">
        <AppstoreOutlined  style="margin: 0 2px"/>
        <span style="font-size: 14px">主数据</span>
      </div>
    </template>

    <div style="display: inline-flex;justify-content: space-between;width: 100%;">
      <div style="width: calc(100% - 130px);height: 100%;">
        <div style="display: flex;margin-top: 10px;text-align: center">
          <SearchOutlined style="font-size: 30px;color: #0096c7;margin-left: 1em;"/>
          <span style="line-height:30px;font-size: 24px;color: #0096c7;font-weight: bold;">&ensp;计量单位</span>
        </div>
        <div class="nc-open-content">
          <div class="open-content-up">
            <div class="border-div">
              <span>计量类别</span>
              <div style="margin-left: 72px;">
                <ul>
                  <li style="margin: 2% 0px;">
                    <a-radio-group v-model:value="formItems.typeCode" name="radioGroup">
                      <a-radio  value="k1">单计量</a-radio>
                      <a-radio  value="k2">多计量</a-radio>
                    </a-radio-group>
                  </li>
                </ul>
              </div>
            </div>
            <div class="border-div">
              <span>计量单位</span>
              <div style="margin-left: 42px;">
                <ul>

                  <li style="margin: 2% 0px;">
                    <label style="margin-left: 0;">计量单位：</label>
                    <a-input v-model:value="formItems.unitName" placeholder="请输入计量单位名称" class="abc" style="width: 25%;" />

                    <label style="margin-left: 25px;">计量类型：</label>
                    <a-select
                      v-model:value="formItems.unitType"
                      show-search
                      option-filter-prop="children"
                      style="width: 25%;text-align: center;"
                    >
                      <a-select-option key="1" value="1">重量</a-select-option>
                      <a-select-option key="2" value="2">数量</a-select-option>
                      <a-select-option key="3" value="3">面积</a-select-option>
                      <a-select-option key="4" value="4">长度</a-select-option>
                      <a-select-option key="5" value="5">体积</a-select-option>
                      <a-select-option key="6" value="6">容积</a-select-option>
                      <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
                    </a-select>
                  </li>

                </ul>
              </div>
            </div>
            <div class="border-div" >
              <span>多计量</span>
              <div style="margin-left: 42px;">
                <ul>

                  <li style="margin: 2% 0px;">
                    <label style="margin-left: 0;">主计量：</label>
                    <a-input v-model:value="formItems.unitName1" onkeyup="this.value=this.value.replace(/[^\u4e00-\u9fa5]/g,'')" placeholder="请输入主计量名称" class="abc" style="width: 25%;" />

                  </li>
                  <li style="margin: 2% 0px;">
                    <label style="margin-left: 0;">计 量 1：</label>
                    <a-input  v-model:value="formItems.unitName2" onkeyup="this.value=this.value.replace(/[^\u4e00-\u9fa5]/g,'')" placeholder="请输入计量1名称" class="abc" style="width: 25%;" />
                    <label style="margin-left: 25px;">换算率：</label>
                    <InputNumber   min="0" :formatter="value => `${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')"
                                  :parser="value => value.replace(/\$\s?|(,*)/g, '')" v-model:value="formItems.conversionRate2" placeholder="请输入换算率" class="abc" style="width: 25%;" />

                  </li>
                  <li style="margin: 2% 0px;">
                    <label style="margin-left: 0;">计 量 2：</label>
                    <a-input v-model:value="formItems.unitName3" placeholder="请输入计量2名称" class="abc" style="width: 25%;" />
                    <label style="margin-left: 25px;">换算率：</label>
                    <InputNumber min="0" :formatter="value => `${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')"
                           :parser="value => value.replace(/\$\s?|(,*)/g, '')"
                           v-model:value="formItems.conversionRate3" placeholder="请输入换算率" class="abc" style="width: 25%;" />
                  </li>
                </ul>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div style="width: 150px;background-color: #f1f1f1;padding: 10% 4%;height: 360px;">
        <Button style="width: 100px;" shape="round" @click="handleOk(false)" v-if="isEdit == false" type="primary">保存</Button>
        <Button style="width: 100px;margin-top: 10px" shape="round" v-if="isEdit == false" @click="handleOk(true)"  type="primary">保存并新增</Button>
        <Button style="width: 100px;" shape="round" @click="handleOk(false)" v-if="isEdit == true" type="primary">修改</Button>
        <Button style="width: 100px;margin-top: 10px" shape="round" @click="handleClose">取消</Button>
      </div>
    </div>

  </BasicModal>
</template>

<script setup="props, {content}" lang="ts">
  import {ref, unref, reactive, onMounted, watch, computed} from 'vue'
  import {BasicModal, useModal, useModalInner} from '/@/components/Modal'
  import {
    DatePicker as ADatePicker,
    Select as ASelect,
    Input as AInput,
    Checkbox as ACheckbox,
    Radio as ARadio,
    InputNumber,
    Popconfirm as APopconfirm,
    Tabs as ATabs, message, Transfer as ATransfer,Button
  } from "ant-design-vue"
  import {
    PlusSquareOutlined,
    FileSearchOutlined,
    SearchOutlined,AppstoreOutlined,
    PicLeftOutlined, PaperClipOutlined
  } from '@ant-design/icons-vue';
  import {useTabs} from '/@/hooks/web/useTabs';
  import router from "/@/router";
  import moment, {Moment} from 'moment';

  import {useUserStore} from "/@/store/modules/user";
  import {
    getCurrentAccountName,
    hasBlank,
    pointMessage
  } from "/@/api/task-api/tast-bus-api";
  import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
  import {useMessage} from "/@/hooks/web/useMessage";
  import {ObjTool, StrTool} from "/@/api/task-api/tools/universal-tools";
  import {
    editManyList,
    savePsn,
    saveManyList,
    findAllSysUnitOfMeaList
  } from "/@/api/record/system/unit-mea";

  //红字（无现金科目，请设置现金科目后再进行查询）
  const {closeCurrent} = useTabs(router);
  const emit = defineEmits(['register', 'save'])

  const ARangePicker = ADatePicker.RangePicker
  const ASelectOption = ASelect.Option
  const AInputSearch = AInput.Search
  const ARadioGroup = ARadio.Group
  const ARadioButton = ARadio.Button
  const ACheckboxGroup = ACheckbox.Group
  const ATabPane = ATabs.TabPane
  const userStore = useUserStore();
  const formItems: any = ref({
    typeCode: 'k1',
    conversionType: '1',
    unitType: '2',
  })
  let changeBeforeModel: any = {}
  const {
    createErrorModal
  } = useMessage()
  const isEdit = ref(false)
  const [register, {closeModal}] = useModalInner(async(d) => {
    dynamicTenantId.value = d.data.accId
    if(d.isEdit === true){
      isEdit.value = true
      if(isEdit.value){
        formItems.value = d.data.data
        formItems.value.typeCode = d.data.typeCode
      }
      if(d.data.typeCode == 'k2'){
        let res = await useRouteApi(findAllSysUnitOfMeaList, {schemaName: dynamicTenantId.value})({
          manyCode: d.data.data.unitCode,
        })
        if(res.length == 2){
          formItems.value.unitName1 = res[0].unitName
          formItems.value.unitName2 = res[1].unitName
          formItems.value.conversionRate2 = res[1].conversionRate

        }else if(res.length == 3){
          formItems.value.unitName1 = res[0].unitName
          formItems.value.unitName2 = res[1].unitName
          formItems.value.conversionRate2 = res[1].conversionRate
          formItems.value.unitName3 = res[2].unitName
          formItems.value.conversionRate3 = res[2].conversionRate
        }
      }
    }else{
      isEdit.value = false
      formItems.value ={
        typeCode: 'k1',
        conversionType: '1',
        unitType: '2',
      }
    }
    formItems.value.conversionType = '1'
    changeBeforeModel = JSON.parse(JSON.stringify(formItems.value))
  })
  const dynamicTenantId = ref(getCurrentAccountName(true))
  async function handleOk(flg) {

    if (formItems.value.unitName==null || formItems.value.unitName==''){
      createErrorModal({
        iconType: 'warning',
        title: '提示',
        content: '计量单位名称不能为空！'
      })
      return false
    }

    //判断单/多
    if(formItems.value.typeCode == 'k1'){
      formItems.value.unitName=formItems.value.unitName.trim()
      const d = await useRouteApi(savePsn, {schemaName: dynamicTenantId.value})(formItems.value)
      if(d === '600'){
        message.error("单位名称存在,请勿重复添加!")
      }
      if(flg){
        formItems.value = {
          typeCode: 'k1',
          conversionType: '1',
          unitType: '2',
        }
        return true
      }else{
        emit('save', unref(formItems))
        closeModal()
        return true
      }
    }else{
      //拼装多计量
      if (formItems.value.unitName1==null || formItems.value.unitName1==''){
        createErrorModal({
          iconType: 'warning',
          title: '提示',
          content: '主计量不能为空！'
        })
        return false
      }
      if (formItems.value.unitName2==null || formItems.value.unitName2==''){
        createErrorModal({
          iconType: 'warning',
          title: '提示',
          content: '计量1不能为空！'
        })
        return false
      }
      if (formItems.value.conversionRate2==null || formItems.value.conversionRate2==''){
        createErrorModal({
          iconType: 'warning',
          title: '提示',
          content: '计量1换算率不能为空！'
        })
        return false
      }
      let arr = []
      arr.push({
        unitName: formItems.value.unitName1.trim(),
        isMain: "true",
        conversionRate: '1',
        conversionExplain: '',
      },{
        unitName: formItems.value.unitName2.trim(),
        isMain: "false",
        conversionRate: formItems.value.conversionRate2,
        conversionExplain: '',
      })

      if(typeof(formItems.value.unitName3) != 'undefined' && typeof(formItems.value.conversionRate3) != 'undefined'){
          arr.push({
          unitName: formItems.value.unitName3.trim(),
          isMain: "false",
          conversionRate: formItems.value.conversionRate3,
          conversionExplain: '',
        })
      }
      if(isEdit.value === true){
        await useRouteApi(editManyList,{schemaName:dynamicTenantId.value})({
          unitName: formItems.value.unitName.trim(),
          unitCode: formItems.value.unitCode,
          conversionType: formItems.value.conversionType,
          unitType: formItems.value.unitType,
          list: arr
        }).then(v=>{
          if(v=='600'){
            message.error("单位名称存在,请勿重复添加!")
            return
          }else{
            emit('save', unref(formItems))
            closeModal()
            return true
          }
        })
      }else{
        await useRouteApi(saveManyList,{schemaName:dynamicTenantId.value})({
          unitName: formItems.value.unitName.trim(),
          unitCode: formItems.value.unitCode,
          conversionType: formItems.value.conversionType,
          unitType: formItems.value.unitType,
          list: arr
        }).then(v=>{
          if(v=='600'){
            message.error("单位名称存在,请勿重复添加!")
            return
          }else{
            if(flg){
              formItems.value = {
                typeCode: 'k2',
                conversionType: '1',
                unitType: '2',
              }
              return true
            }else{
              emit('save', unref(formItems))
              closeModal()
              return true
            }
          }
        })
      }
    }
  }

  async function handleClose() {
    if (null != formItems.value.openOne && formItems.value.openOne == 1) {
      await closeCurrent()
      router.push('/one/home/welcome')
    }
    closeModal()
  }


</script>
<style lang="less" scoped>

  :deep(.ant-checkbox){
    margin-top: -8px;
  }
  .nc-open-content {
    background-image: url(/@/assets/images/homes/bg-pattern.png);
    background-repeat: no-repeat;
    background-position: 66% 8%;
    background-size: contain;
    position: relative;
    :deep(.ant-select-selector), :deep(.ant-input-affix-wrapper) {
      border: none !important;
      border-bottom: 1px solid #bdb9b9 !important;
      background: none;
    }
    .border-div {
      position: relative;
      border: 1px #a29f9f solid;
      margin: 20px 10px;
      padding: 2.5%;

      > span {
        display: block;
        width: 80px;
        text-align: center;
        background-color: white;
        position: absolute;
        left: 50px;
        top: -10px;
        color: #888888;
        font-size: 12px;
        font-weight: bold;
      }
    }
  }
  :global(.ant-modal-header) {
    padding: 10px 0 !important;
  }
  :global(.ant-modal-close-x){
    height: 30px !important;
    color: white;
  }
</style>

<template>
  <BasicModal
    width="800px"
    v-bind="$attrs"
    title="发送消息"
    @ok="handleClose()"
    okText="关闭"
    @cancel="handleClose()"
    @register="register"
  >
    <div
      class="nc-open-content"
      style="height: 100%"
    >
      <div class="open-content-up">
        <ul>
          <li>
            <span><font style="color: red">*</font>标&emsp;&emsp;题：{{sysMessageUser.title}}</span>
          </li>
          <li v-if="formItems.flg === '1'">
            <span><font style="color: red">*</font>发 送 人：{{sysMessageUser.username}}</span>

          </li>
          <li v-if="formItems.flg === '1'">
            <span><font style="color: red">*</font>发送日期：{{formatTime(sysMessageUser.sendTime)}}</span>
          </li>
          <li v-if="formItems.flg != '1'">
            <span><font style="color: red">*</font>接 收 人：{{sysMessageUser.username}}</span>
          </li>
          <li>
            <span ><font style="color: red">*</font>优 先 级：</span>
            <a-radio-group  button-style="solid" v-model:value="sysMessageUser.weights">
              <a-radio-button value="1">重要且紧急</a-radio-button>
              <a-radio-button value="2">重要不紧急</a-radio-button>
              <a-radio-button value="3">紧急不重要</a-radio-button>
              <a-radio-button value="4">一般消息</a-radio-button>
            </a-radio-group>
          </li>
          <li>
            <span style="float: left;" ><font style="color: red">*</font>信&emsp;&emsp;息：</span>
            <a-textarea style="width: 80%;height: 100px" v-model:value="sysMessageUser.content" placeholder="消息内容"/>
          </li>
          <li>
            <span style="float: left;" ><font style="color: red">*</font>附&emsp;&emsp;件：</span>
            <ul style="padding-left: 20px">
              <li v-for="item in fileUpList">
                <a  @click="download(item.id)">{{item.oldName}}</a>
              </li>
            </ul>
          </li>
        </ul>

      </div>
    </div>

  </BasicModal>
</template>

<script setup="props, {content}" lang="ts">
  import {ref, unref,  onMounted} from 'vue'
  import {BasicModal, useModalInner} from '/@/components/Modal'
  import {PageWrapper} from '/@/components/Page'
  import {
    DatePicker as ADatePicker,
    Select as ASelect,
    Input as AInput,
    Checkbox as ACheckbox,
    Popover as APopover,
    Switch as ASwitch,
    Transfer as ATransfer,
    Radio as ARadio,
    Table as ATable,
    Tabs as ATabs, message
  } from "ant-design-vue"
  const ATextarea=AInput.TextArea
  import CheckboxGroup from "ant-design-vue/lib/checkbox/Group";
  import { useTabs } from '/@/hooks/web/useTabs';
  import router from "/@/router";
  import moment, { Moment } from 'moment';
  import {useCompanyOperateStoreWidthOut} from "/@/store/modules/operate-company";
  import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
  import {useUserStore, useUserStoreWidthOut} from "/@/store/modules/user";
  import { getUserList,save, getFileList,downloadFile } from "/@/api/record/sys-task/data";
  import {assignWith} from "lodash-es";
  import {dlownload} from "/@/api/record/eletronicInvoice_data/eletronic_invoice";
  import {downloadByData} from "/@/utils/file/download";
  const userStore = useUserStore();
  const { closeCurrent } = useTabs(router);
  const ARangePicker = ADatePicker.RangePicker
  const ASelectOption = ASelect.Option
  const AInputSearch = AInput.Search
  const ARadioGroup = ARadio.Group
  const ARadioButton = ARadio.Button
  const ACheckboxGroup = ACheckbox.Group
  const ATabPane = ATabs.TabPane

  const emit=defineEmits([])
  const data = []
  const formItems: any = ref({})
  let changeBeforeModel: any = {}
  //查询条件
  const seach : any = ref({})
  const userList = ref([])

  const options: { value: string;label: string; disabled: boolean }[] = [];

  const sysMessageUser = ref({
    weights: '4'
  })

  const [register, {closeModal, setModalProps}] =  useModalInner (async(data) => {
    // 方式2
    formItems.value.openOne = data.data.openOne
    formItems.value.flg = data.data.flg
    sysMessageUser.value = data.data.data
    //文件list
    fileUpList.value = await getFileList(sysMessageUser.value.messageId)
    console.log(fileUpList.value)
    //获取该信息发送的人
    targetKeys.value = data.data.data.user
    changeBeforeModel = JSON.parse(JSON.stringify(formItems.value))
    setModalProps({minHeight: 400});
  })
  let isChanged: boolean = false
  const formatTime = (val)=>{
    return moment(val).format('YYYY-MM-DD HH:mm:ss')
  }
  //获取当前年月日
  const nowDate = ()=>{
    const nowDate = new Date();
    const date = {
      year: nowDate.getFullYear(),
      month: nowDate.getMonth() + 1,
      date: nowDate.getDate(),
    }
    const newmonth = date.month>10?date.month:'0'+date.month
    const day = date.date>10?date.date:'0'+date.date
    return date.year + '-' + newmonth
  }

  const filterOption = (input: string, option: any) => {
    return option.value.toLowerCase().indexOf(input.toLowerCase()) >= 0;
  };

  const whetherGroup = ref(false)
  // 数据库模式名称
  const database = ref(useCompanyOperateStoreWidthOut().getSchemaName);
  onMounted(async () => {
    //查询操作人员表
    mockData.value = await getUserList()

  })

  interface MockData {
    key: string;
    title: string;
    chosen: boolean;
  }

  const mockData = ref<MockData[]>([]);

  const targetKeys = ref<string[]>([]);

  const handleChange = (keys: string[], direction: string, moveKeys: string[]) => {
    targetKeys.value = keys;
  };



  async function handleClose() {
    if (null != formItems.value.openOne && formItems.value.openOne == 1) {
      await closeCurrent();
    }
  }

  //已上传成功的文件信息
  const fileUpList = ref([])
  const download = async(id) => {
    //下载
    const data = await downloadFile(id);
    const fileName = data.headers['content-disposition'].split('filename=')[1];
    console.log(data.data)
    downloadByData(data.data, fileName);
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
    .ant-tabs-tab-active{
      background-color: #0960bd !important;
      color: white !important;
    }
  }
  .nc-open-content label{
    text-align: center;
    width: 110px;
    display: inline-block;
    padding: 3px 10px;
  }
</style>

<template>
  <BasicModal
    width="720px"
    v-bind="$attrs"
    title="期初余额导入"
    @register="register"
  >
    <div style="margin: 0% 5% 0%">
      <div style="position: relative;border:1px solid #9b9b9b;padding: 20px; margin-top:2%;">
        <span style="position: absolute; display: block; width: 80px; top: -12px; left: 50px; background-color: white; text-align: center;">导入说明</span>
        <div style="color: black;">
          <p>1、导入文件格式必须为xls或xlsx，且数据表内容必须放置在第一个页签中；</p>
          <p>2、“简约凭证模板”是用于不包含数量核算、外币核算和自定义项附助核算的期初余额导入；</p>
          <p>3、“标准凭证模板”可以用于所有科目的期初余额导入；</p>
        </div>
      </div>
      <div style="margin: 3% 5%">
        <a-button type="primary" @click="downLoad()">
         <DownloadOutlined />模板下载
        </a-button>
      </div>
      <div style="margin: 3% 5%">
        <h2>数据导入格式</h2>
        <a-radio-group v-model:value="templateType">
          <a-radio style="display: block;height: 30px;" value="2">
            辅助核算项按编码导入，如：部门、个人、客户、供应商、项目等需要填写编码
          </a-radio>
          <a-radio style="display: block;height: 30px;" value="1">
            辅助核算项按名称导入，如：部门、个人、客户、供应商、项目等需要填写名称
          </a-radio>
        </a-radio-group>
        <br/>
        <br/>
        <!--          :action="'/api/accvoucher/importAccvoucher2?templateID='+uploadParm"
                  :headers="headers"
                  @change="handleChange"
                  :before-upload="beforeUpload"-->
        <a-upload
          :file-list="fileList"
          :remove="handleRemove"
          :before-upload="beforeUpload"
        >
          <a-button type="primary">
            请选择导入的文件
          </a-button>
        </a-upload>
      </div>
    </div>
    <template #footer>
      <div style="height: 35px">
        <div style="float: right">
          <a-button @click="handleClose">放弃</a-button>
          <!-- <a-button @click="handleOk(false)">检查</a-button> -->
          <a-button @click="handleOk(true)" type="primary" :loading="uploading"
                    :disabled="fileList.length == 0 || fileList.length> 1">{{uploading != 0?'':'开始导入'}}
          </a-button>
        </div>
      </div>
    </template>
  </BasicModal>
</template>
<script setup="props, {content}" lang="ts">
import {DownloadOutlined} from '@ant-design/icons-vue';
import {defineComponent, onMounted, ref, unref, VNodeTypes} from 'vue'
import {BasicModal, useModalInner} from '/@/components/Modal'
import {
  findAllAccvoucherTemplate,
  findAllAccvoucherFieldsApi,
  findAllAccassFieldsApi
} from '/@/api/record/system/accvoucher_template'
import {
  findByFunctionModule,
  offsetToStr,
  compareTime,
  markAnomaly
} from '/@/api/task-api/tast-bus-api'
import {message} from 'ant-design-vue'
import {jsonToSheetXlsx} from '/@/components/Excel';
import {useUserStoreWidthOut} from "/@/store/modules/user";
import {useCompanyOperateStoreWidthOut} from "/@/store/modules/operate-company";
import {
  voucherBackgroundImport,
  downLoadCheckResultFile,
  delCheckResultFile, findAllAccvoucher, voucherBackgroundImportQiChu
} from '/@/api/record/system/accvoucher';
import {useMessage} from "/@/hooks/web/useMessage";

const {
  createErrorModal, createWarningModal
} = useMessage()
import {
  DatePicker as ADatePicker,
  Select as ASelect,
  Input as AInput,
  Popover as APopover,
  Switch as ASwitch,
  Radio as ARadio,
  Upload as AUpload,
  Checkbox as ACheckbox
} from "ant-design-vue"

const ARangePicker = ADatePicker.RangePicker
const ASelectOption = ASelect.Option
const AInputSearch = AInput.Search
const ARadioGroup = ARadio.Group
const ARadioButton = ARadio.Button
// 选中模板
const templateName = ref()
const templateType = ref('2')
const templateNumber = ref(true) // false 1自定义 true 2系统生成
const isShow = ref(false)
const templateList = ref([])
const headers = ref({})
// 上传文件
const uploading = ref(false)
const fileList = ref([])

const emit = defineEmits(['register'])
const iyear = ref('')
const databasenum = ref('')
const pageSchemaName = ref('')

const [register, {closeModal}] = useModalInner((data) => {
  iyear.value = data.iyear;
  databasenum.value = data.databasenum;
  pageSchemaName.value = data.databasenum;
})

onMounted(async() => {

})

// 唯一码 -- 模板类型 -- 是否为名称 -- 数据下标 -- 是否自定义编码
const uploadParm = ref('')
// 上传回调
const handleChange = async (info: any) => {
  if (info.fileList[0].status === 'done') {
    if (info.fileList[0].response.result[0].code === 404) {
      createErrorModal({title: '模板导入检测结果', content: info.fileList[0].response.result[0].result})
      return false
    } else {
      message.success('导入成功')
      emit('reloadProjects')
      closeModal()
    }
  }
}
import {downloadByData} from "/@/utils/file/download";
import {dlownload} from "/@/api/record/eletronicInvoice_data/eletronic_invoice";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";

const beforeUpload = (file) => {
  // 默认存储最新单文件
  if (fileList.value.length > 0) fileList.value = []
  fileList.value = [...fileList.value, file]
  return false;
}

const handleOk = (flag) => {
  if (fileList.value.length == 0) {
    createWarningModal({title: '凭证导入前检测', content: '请先选择要进行导入的Excel文件！'})
  } else {
    let msg = '';
    findByFunctionModule({iyear: iyear.value}).then(res => {
      let checkMenu = ['会计科目修改', '凭证导入', '月末结账', '人员档案', '客户档案', '部门档案', '项目档案', '部门档案']
      res.forEach(item => {
        if (checkMenu.indexOf(item.functionModule) != -1 && item.state == '1') {
          // 校验时间
          if (item.time != '' && !compareTime(offsetToStr(item.time))) { // 超时
            msg = '提示：任务冲突！操作员【' +
              item.caozuoUnique +
              '】正在进行' + item.functionModule + '处理!不能继续进行凭证导入操作，请销后再试，或联系财务主管清理该记账任务!'
            return false;
          } else {
            // 标记异常
           // markAnomaly(item.id, item.iyear);
          }
        }
      })
      if (msg != '') {
        fileList.value = []
        createWarningModal({title: '凭证导入前检测', content: msg})
        return false
      } else {
        templateName.value = "期初余额";
        alert(templateName.value);
        uploadParm.value = ('0000' + '--' + templateName.value + '--' + templateType.value + '--' + "2222" + '--' + (templateNumber.value?'2':'1'))
        const formData = new FormData();
        fileList.value.forEach((file) => {
          formData.append('file', file as any);
        });
        uploading.value = true;
        let s = pageSchemaName.value.split('-')[0]+'-'+pageSchemaName.value.split('-')[1]
        formData.append('templateInfo', uploadParm.value+'--'+s + '--' + iyear.value)
        formData.append('isCehck', flag)
          useRouteApi(voucherBackgroundImportQiChu,{schemaName: pageSchemaName.value})(formData)
          .then(async (info:any) => {
            fileList.value = [];
            uploading.value = false;
            if (null != info.pass){ // 检查
              if (!info.pass) {
                createErrorModal({
                  title: '模板导入检测结果',
                  content: '后台检查不通过是否下载表格查看错误注释！',
                  cancelButtonProps: {title:'放弃'},
                  onOk: async () =>{
                    const data = await  downLoadCheckResultFile({parm: info.path})
                    const fileName = data.headers['content-disposition'].split('filename=')[1];
                    downloadByData(data.data, fileName);
                  },
                  onCancel: ()=>{
                    delCheckResultFile({parm: info.path})
                  }
                })
                return false
              } else {
                message.success('导入成功')
                emit('reloadProjects')
                closeModal()
              }
            }else { // 导入
              if (info[0].code == 404){
                createWarningModal({title: '凭证导入前检测', content: info[0].result})
              }else {
                message.success('导入成功')
                emit('reloadProjects')
                closeModal()
              }
            }
            return false;
          })
          .catch((err)=>{
            fileList.value = [];
            uploading.value = false;
            message.error('导入失败')
          })
      }
    })
  }
}
const handleClose = () => {
  fileList.value = [];
  closeModal()
}
const handleRemove = (file) => {
  const index = fileList.value.indexOf(file);
  const newFileList = fileList.value.slice();
  newFileList.splice(index, 1);
  fileList.value = newFileList;
};
const downLoad = () => {
  templateName.value = "期初余额";
  if (null == templateName.value) {
    createWarningModal({title: '提示', content: '请先选择要进行下载的凭证模板类型！'})
    return false;
  }
  let id = '0';
  findAllAccassFieldsApi({iyear:iyear.value, databasenum: databasenum.value}).then(res => {
    if (res.length > 0) {
      let name = templateName.value + '导入模板.xlsx'
      jsonToSheetXlsx({data: [], header: res, filename: name})
    }
  })
}
</script>
<style lang="less" scoped>

:deep(.ant-calendar-picker-input.ant-input), :deep(.ant-select-single:not(.ant-select-customize-input) .ant-select-selector .ant-select-selection-search-input) {
  border: none;
  border-bottom: solid 1px rgb(191, 191, 191) !important;
}

.vben-basic-title {
  color: rgb(1, 129, 226) !important;
}

.ant-modal-body {
  padding: 0px;
  border: 1px solid rgb(1, 129, 226);
  border-left: none;
  border-right: none;
}
:deep(.ant-upload-list-text){
  width: 68%;
  float: right;
  display: inline-block;
}
.nc-open-content {
  input {
    width: 35%;
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
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

  .open-content-down {
    margin-top: 5%;
  }
}
</style>

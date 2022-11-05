<template>
  <BasicModal
    width="900px"
    v-bind="$attrs"
    title="发票识别"
    @ok="handleSubmit()"
    @cancel="closeModal()"
    @register="register2"
  >
    <div class="app-container">
      <div class="app-container-content" style="margin-top: 50px; background-color: white">
        <a-row class="workbench p-4" :gutter="12">
          <a-col :md="22" :lg="11" style="text-align: center">
            <a-upload
              list-type="picture-card"
              class="avatar-uploader"
              name="file"
              v-model:file-list="fileOcrList"
              :show-upload-list="false"
              :disabled="fileOcrList.length > 1"
              :before-upload="beforeUpload"
              :custom-request="customrequest"
              @change="handle123"
            >
              <a-badge :count="formItems.fapiaoCheck === '2' ? '已验真' : ''">
                <img
                  v-if="imageUrl"
                  :src="imageUrl"
                  alt="avatar"
                  class="mr-2"
                  style="width: 800px; height: 400px"
                />
                <div v-else>
                  <loading-outlined v-if="loading" />
                  <plus-outlined v-else />
                  <div class="ant-upload-text">发票识别</div>
                </div>
              </a-badge>
            </a-upload>
          </a-col>

        </a-row>
        <a-modal v-model:visible="visible" title="发票验真" @ok="isCheck" style="margin: 200px auto">
          <p>是否进行发票验真</p>
        </a-modal>
      </div>
    </div>

    <template #footer>
      <a-button @click="closeModal()">取消</a-button>
      <a-button @click="handleSubmit()" v-if="status">保存</a-button>
    </template>
  </BasicModal>
</template>
<script setup="props, { emit }" lang="ts">
  import { useRoute, useRouter } from 'vue-router';
  import { BasicTable, useTable } from '/@/components/Table';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { BasicForm, useForm } from '/@/components/Form';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { onMounted, reactive, ref, unref } from 'vue';
  import { ScrollContainer } from '/@/components/Container/index';
  import { downloadByData } from '/@/utils/file/download';
  import {
    findAllInvoiceType,
    findByImgid,
    findAllElectronicInvoice,
    eletronicInvoiceSaveApi,
    delByImgid,
    findAllOcrApi,
    findByOcrSurplusNumber,
    findByCheckFlag,
    isCheckApi,
    dlownload, uploadInvocie, listOCR, pzAssociationFp,
  } from '/@/api/record/eletronicInvoice_data/eletronic_invoice';
  import {
    CaretDownFilled,
    FormOutlined,
    DeleteOutlined,
    SettingFilled,
    SyncOutlined,
    PicLeftOutlined,
    EditFilled,
    PieChartFilled,
    FilterFilled,
  } from '@ant-design/icons-vue';
  import {
    Select as ASelect,
    Input as AInput,
    Popover as APopover,
    Checkbox as ACheckbox,
    Modal as AModal,
    Upload as AUpload,
    Row as ARow,
    Col as ACol,
    message,
    Badge as ABadge,
  } from 'ant-design-vue';
  import { useUserStoreWidthOut } from '/@/store/modules/user';
  import { useCompanyOperateStoreWidthOut } from '/@/store/modules/operate-company';
  import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
  import {getCurrentAccountName} from "/@/api/task-api/tast-bus-api";

  // 上传文件
  const headers = ref({
    authorization: useUserStoreWidthOut().getToken,
    datasource: JSON.stringify({
      databaseName: 'xahls-001-2021',
      schemaName: useCompanyOperateStoreWidthOut().getSchemaName,
    }),
  });
  const emit=defineEmits([])
  const route = useRoute();
  const router = useRouter();
  const { createMessage } = useMessage();
  const formRef = ref({});
  const formItems: any = ref({});
  // 发票类型集合
  const invoiceTypeList: any = ref([]);
  const fileOcrList: any = ref([]);
  const fileImgList: any = ref([]);
  // 文件
  const fileList = ref([]);
  const loading = ref<boolean>(false);
  const imageUrl = ref<string>('');
  const previewVisible = ref<boolean>(false);
  const previewImage = ref<string | undefined>('');
  // 是否允许提交
  const status: any = ref(false);
  // 是否验真弹框
  const visible: any = ref(false);
  // 手动验真按钮
  const manualFlag: any = ref(false);

  interface FileItem {
    uid: string;
    name?: string;
    status?: string;
    response?: string;
    url?: string;
    type?: string;
    size: number;
    originFileObj: any;
    thumbUrl?: string;
  }

  interface FileInfo {
    file: FileItem;
    fileList: FileItem[];
    fileLists: FileItem[];
  }


  const databasesName: any = ref(getCurrentAccountName(true));
  const [register2, {closeModal, setModalProps}] = useModalInner((data) => {
    // 方式2
    setModalProps({minHeight: 400});
    databasesName.value = data.databasesName
  })

  function getBase64(img: Blob, callback: (base64Url: string) => void) {
    const reader = new FileReader();
    reader.addEventListener('load', () => callback(reader.result as string));
    // 压缩
    reader.readAsDataURL(img);
  }

  const handleChange = (info: FileInfo) => {
    if (info.file.status === 'uploading') {
      loading.value = true;
      return;
    }
    if (info.file.status === 'done') {
      // Get this url from response in real world.
      getBase64(info.file.originFileObj, (base64Url: string) => {
        imageUrl.value = base64Url;
        loading.value = false;
      });
    }
    if (info.file.status === 'error') {
      loading.value = false;
      message.error('upload error');
    }
  };

  const beforeUpload = async (file: FileItem) => {
    loading.value = true;
    const isJpgOrPng =
      file.type === 'image/jpeg' || file.type === 'image/png' || file.type === 'application/pdf';
    if (!isJpgOrPng) {
      message.error('You can only upload JPG file!');
      return a();
    }
    const isLt2M = file.size / 1024 / 1024 < 2;
    if (!isLt2M) {
      message.error('Image must smaller than 2MB!');
      return a();
    }
    // 查询集团OCRAPI接口是否配置
    const a = await findAllOcrApi();
    if (a.length === 0) {
      message.error('未配置图像识别接口，请联系管理员!');
      return a();
    }
    // 查询集团OCRAPI剩余次数
    const b = await findByOcrSurplusNumber();
    if (b === 0) {
      message.error('识别次数已经用完；请再API官网在线购买!');
      return b();
    }
    getBase64(file as any, (base64Url: string) => {
      imageUrl.value = base64Url;
    });
    return false;
  };

  const handleCancel = () => {
    previewVisible.value = false;
  };
  const handlePreview = async (file: FileItem) => {
    const inds = fileList.value.findIndex((value) => value.uid === file.uid);
    const imgsList = [];
    fileList.value.forEach((item) => {
      imgsList.push(item.originFileObj.url);
    });
    // createImgPreview({ imageList: imgsList, index: inds });
  };

  const handleClick = (img: string) => {
    // createImgPreview({ imageList: [img] });
  };

  async function look() {
    // createImgPreview({ imageList: [imageUrl.value] });
  }

  async function isYes() {
    manualFlag.value = true;
    visible.value = true;
  }

  async function dlown() {
    //下载
    const data = await dlownload(route.query.id);
    console.log(data.headers['content-disposition'].split('filename=')[1]);
    const fileName = data.headers['content-disposition'].split('filename=')[1];
    downloadByData(data.data, fileName);
  }

  async function handleOk() {
    if (status.value) {
      if (fileImgList.value.length > 0) {
        formItems.value.imgName = fileImgList.value[0].name;
      }
      // emit('save', unref(formItems));
    } else {
      message.error('发票已存在,不能录入!');
      return false;
    }
  }

  const customrequest = (data) => {
    const formData = new FormData()
    formData.append('file', data.file)
    useRouteApi(listOCR,{schemaName:databasesName})(formData)
      .then(async (info:any) => {
        console.log(info)
          if (info.code === 111) {
            status.value = false;
            message.error('发票已存在,不能录入!');
            return false;
          } else if (info.code === 200) {
            status.value = true;
            formItems.value.buyerSupplier = info.result[0].comName; // 购买方名称
            formItems.value.buyerShuihao = info.result[0].taxNum; // 购买方税号
            formItems.value.buyerAddrPhone = info.result[0].comHome; // 购买方地址及电话
            formItems.value.buyerBankAccount = info.result[0].comBank; // 购买方开户行及账号

            formItems.value.sellSupplier = info.result[0].xsComName; // 销售方开户行及账号
            formItems.value.sellShuihao = info.result[0].xsfNum; // 销售方税号
            formItems.value.sellAddrPhone = info.result[0].xsfPhone; // 销售方地址、电话
            formItems.value.sellBankAccount =
              info.result[0].xsfBankName +
              info.result[0].xsfNumber; // 销售方开户账号
            formItems.value.fapiaoCode = info.result[0].invoiceCode; // 发票代码
            formItems.value.fapiaoNumber = info.result[0].invoiceNum; // 发票号码
            formItems.value.fapiaoCheckCode =
              info.result[0].invoiceCheckCode; // 发票校验码
            //formItems.value.machineCode = info.result[0].machineId; // 机器编号
            formItems.value.fapiaoDate = info.result[0].travelDate; // 开票日期
            formItems.value.fapiaoMoney = info.result[0].hjje.replaceAll(
              '¥',
              ''
            ); // 发票金额
            formItems.value.fapiaoTaxAmount =
              info.result[0].hjse.replaceAll('¥', ''); // 发票税额
            formItems.value.fapiaoTotalAmount =
              info.result[0].xxje.replaceAll('¥', ''); // 价税合计
            formItems.value.fapiaoPayee = info.result[0].payee; // 收款人
            formItems.value.fapiaoDrawer = info.result[0].drawer; // 开票人
            formItems.value.fapiaoCheckPsn = info.result[0].recheck; // 复核人
            formItems.value.fapiaoRemarks = info.result[0].comment; // 发票备注

            formItems.value.fapiaoType = info.result[0].fpType; // 发票类型
            //判断进项销项  比较购方 销方

            console.log(info.result[0].tableData)
            formItems.value.tableData = info.result[0].tableData;
             // 发票表体
            // 图片信息
             formItems.value.fapiaoQrCode = info.result[0].ftpFile.id; // 图片id
             formItems.value.fapiaoSaveDir = info.result[0].ftpFile.id; // 附件id

             formItems.value.fpType = '1'; // 发票类别

            const flag = await findByCheckFlag();
            if (flag === '1') {
              manualFlag.value = true;
              visible.value = true;
            }
          }
        loading.value = false;
        message.success('识别成功');
      })
  };

  async function a() {
    return false;
  }
  async function b() {
    return false;
  }

  async function beforeImgUpload(file: any) {
    const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png';
    if (!isJpgOrPng) {
      message.error('文件必须是图片格式的!');
    }
  }
  async function removeImgUpload(data: any) {
    if (data.id !== undefined) {
      delByImgid(data.id, data.ftpUrl, data.name);
    } else {
      fileImgList.value = [];
    }
  }

  async function isCheck() {
    console.log(formItems.value)
    if (!formItems.value.fapiaoCode) {
      message.error('发票代码不能为空!');
      return false;
    }
    if (!formItems.value.fapiaoNumber) {
      message.error('发票号码不能为空!');
      return false;
    }
    if (!formItems.value.fapiaoCheckCode) {
      message.error('发票校验码不能为空!');
      return false;
    }
    if (!formItems.value.fapiaoDate) {
      message.error('开票日期不能为空!');
      return false;
    }
    if (!formItems.value.fapiaoMoney) {
      message.error('发票金额不能为空!');
      return false;
    }
    const a = await isCheckApi(
      formItems.value.fapiaoCode,
      formItems.value.fapiaoNumber,
      formItems.value.fapiaoCheckCode,
      formItems.value.fapiaoDate,
      formItems.value.fapiaoMoney
    );
    if (a === '2') {
      formItems.value.fapiaoCheck = '1';
      message.success('发票已验真!');
    } else if (a === '9') {
      message.error('所查发票不存在!');
      manualFlag.value = true;
    } else if (a !== '2' && a !== '9') {
      message.error('参数长度不正确!');
      manualFlag.value = true;
    }
    visible.value = false;
  }

  async function handleSubmit(values: any) {
    formItems.value.djStatus = '2'
    formItems.value.rzStatus = '2'
    formItems.value.fpStatus = '1'
    console.log(formItems.value)

    useRouteApi(eletronicInvoiceSaveApi,{schemaName:databasesName})(formItems.value)
    emit('save', unref(formItems))
    closeModal()
    return true
  }


</script>
<style src="../../../../../assets/styles/global-menu-index-block.less" lang="less" scoped></style>

<style src="../../../../../assets/styles/global-menu-index.less" lang="less" scoped></style>

<style scoped lang="less">
  :deep(.ant-card-body) {
    padding: 16px;
    border-left: 2px solid rgb(1, 143, 251);
    box-shadow: rgb(72 113 140) -3px 1px 7px -1px;
  }
</style>

<style>
  .p-4 {
    padding: 5px;
  }

  .ant-upload.ant-upload-select-picture-card {
    width: 804px;
    height: 400px;
  }
</style>

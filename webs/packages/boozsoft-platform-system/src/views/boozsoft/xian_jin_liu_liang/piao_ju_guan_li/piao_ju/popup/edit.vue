<template>
  <BasicModal
    width="900px"
    v-bind="$attrs"
    title="电子发票信息"
    @ok="handleOk()"
    @register="register"
  >
    <div class="nc-open-content" style="height: 100%; overflow: hidden">
      <ScrollContainer>
        <div class="open-content-up" style="margin-top: 10px">
          <div style="text-align: center">
            <a-upload
              list-type="picture-card"
              class="avatar-uploader"
              name="file"
              v-model:file-list="fileOcrList"
              :show-upload-list="false"
              :disabled="fileOcrList.length > 1"
              :before-upload="beforeUpload"
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
          </div>

          <!-- 发票长图-待处理
      <a-row class="workbench p-4" :gutter="12">
        <div class="p-4">
          <div class="flex justify-center mt-4">
            <div class="clearfix">
              <a-upload
                action="#"
                 class="avatar-uploader"
                list-type="picture-card"
                v-model:file-list="fileList"
                :before-upload="beforeUploadList"
                @preview="handlePreview"
              >
                <div v-if="fileList.length < 5">
                  <plus-outlined />
                  <div class="ant-upload-text">Upload</div>
                </div>
              </a-upload>
              <a-modal :visible="previewVisible" :footer="null" @cancel="handleCancel">
                <img alt="example" style="width: 128px; height: 128px" :src="previewImage" />
              </a-modal>
            </div>
          </div>
        </div>
      </a-row>-->
          <a-divider>购买方</a-divider>
          <label>单位名称</label>
          <a-input
            v-model:value.trim="formItems.buyerSupplier"
            autocomplete="off"
            placeholder="单位名称"
            @click="downloadfile"
          />
          <label>单位税号</label>
          <a-input
            v-model:value.trim="formItems.buyerShuihao"
            autocomplete="off"
            placeholder="单位税号"
          />
          <label>单位地址电话</label>
          <a-input
            v-model:value.trim="formItems.buyerAddrPhone"
            autocomplete="off"
            placeholder="单位地址电话"
          />
          <label>开户行及账号</label>
          <a-input
            v-model:value.trim="formItems.buyerBankAccount"
            autocomplete="off"
            placeholder="开户行及账号"
          />

          <a-divider>销售方</a-divider>
          <label>单位名称</label>
          <a-input
            v-model:value.trim="formItems.sellSupplier"
            autocomplete="off"
            placeholder="单位名称"
          />
          <label>单位税号</label>
          <a-input
            v-model:value.trim="formItems.sellShuihao"
            autocomplete="off"
            placeholder="单位税号"
          />
          <label>单位地址电话</label>
          <a-input
            v-model:value.trim="formItems.sellAddrPhone"
            autocomplete="off"
            placeholder="单位地址电话"
          />
          <label>开户行及账号</label>
          <a-input
            v-model:value.trim="formItems.sellBankAccount"
            autocomplete="off"
            placeholder="开户行及账号"
          />

          <a-divider style="background-color: #7cb305" />

          <label>发票代码</label>
          <a-input
            v-model:value.trim="formItems.fapiaoCode"
            autocomplete="off"
            placeholder="发票代码"
          />
          <label>发票号码</label>
          <a-input
            v-model:value.trim="formItems.fapiaoNumber"
            autocomplete="off"
            placeholder="发票号码"
          />
          <label>校验码</label>
          <a-input
            v-model:value.trim="formItems.fapiaoCheckCode"
            autocomplete="off"
            placeholder="校验码"
          />
          <label>机器编号</label>
          <a-input
            v-model:value.trim="formItems.machineCode"
            autocomplete="off"
            placeholder="机器编号"
          />

          <label>开票日期</label>
          <a-date-picker
            v-model:value="formItems.fapiaoDate"
            autocomplete="off"
            placeholder="请选择开票日期"
            style="width: 198px"
          />
          <label></label>

          <label>开票内容</label>
          <a-input
            v-model:value.trim="formItems.fapiaoContent"
            autocomplete="off"
            placeholder="开票内容"
          />
          <label>发票金额</label>
          <a-input
            v-model:value.trim="formItems.fapiaoMoney"
            autocomplete="off"
            placeholder="发票金额"
          />
          <label>发票税额</label>
          <a-input
            v-model:value.trim="formItems.fapiaoTaxAmount"
            autocomplete="off"
            placeholder="发票税额"
          />
          <label>价税合计</label>
          <a-input
            v-model:value.trim="formItems.fapiaoTotalAmount"
            autocomplete="off"
            placeholder="价税合计"
          />
          <label>发票数量</label>
          <a-input
            v-model:value.trim="formItems.fapiaoSum"
            autocomplete="off"
            placeholder="发票数量"
          />
          <label>发票类型</label>
          <a-select
            v-model:value="formItems.fapiaoType"
            show-search
            placeholder="发票类型"
            option-filter-prop="children"
            style="width: 35%"
            allow-clear
          >
            <a-select-option
              v-for="invoice in invoiceTypeList"
              :key="invoice.num"
              :value="invoice.num"
            >
              {{ invoice.name }}
            </a-select-option>
          </a-select>

          <label>收款人</label>
          <a-input
            v-model:value.trim="formItems.fapiaoPayee"
            autocomplete="off"
            placeholder="收款人"
          />
          <label>开票人</label>
          <a-input
            v-model:value.trim="formItems.fapiaoDrawer"
            autocomplete="off"
            placeholder="开票人"
          />
          <label>复核人</label>
          <a-input
            v-model:value.trim="formItems.fapiaoCheckPsn"
            autocomplete="off"
            placeholder="复核人"
          />
          <label>发票备注</label>
          <a-input
            v-model:value.trim="formItems.fapiaoRemarks"
            autocomplete="off"
            placeholder="发票备注"
            style="width: 83%"
          />
          <br />
          <br />

          <!--<div>
          <div style="display: inline-flex;width: 50%;">
            <a-upload
              name="file"
              v-model:file-list="fileOcrList"
              :disabled="fileOcrList.length>1"
              :before-upload="beforeOcrUpload"
              :action="'/api/ocr/listOCR'"
              @change="handle123"
            >
              <a-button>
               图像识别
              </a-button>
            </a-upload>
            &nbsp;
            &nbsp;
            &nbsp;

            <a-button v-if="manualFlag" @click="isCheck">
              手动验真
            </a-button>
          </div>
          <div style="float: right;">
            <a-upload
              list-type="picture-card"
              v-model:file-list="fileImgList"
              :before-upload="beforeImgUpload"
              :remove="removeImgUpload"
              headers=""
              :action="'/api/electronicInvoice/uploadInvocie'"
            >
              <div v-if="fileImgList.length <1">
                <plus-outlined />
                <div class="ant-upload-text">图片上传</div>
              </div>
            </a-upload>
          </div>
        </div>-->

          <a-modal
            v-model:visible="visible"
            title="发票验真"
            @ok="isCheck"
            style="margin: 200px auto"
          >
            <p>是否进行发票验真</p>
          </a-modal>
        </div>
      </ScrollContainer>
      <br />
      <br />
      <!--      <a-modal :visible="previewVisible" :footer="null" @cancel="handleCancel">-->
      <!--        <img alt="example" style="width: 100%" :src="previewImage" />-->
      <!--      </a-modal>-->
    </div>
  </BasicModal>
</template>
<script setup="props, { content }" lang="ts">
  import { PlusOutlined, LoadingOutlined, UploadOutlined } from '@ant-design/icons-vue';
  import { ref, unref } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import {
    Select as ASelect,
    Input as AInput,
    Popover as APopover,
    Checkbox as ACheckbox,
    Modal as AModal,
    Upload as AUpload,
    DatePicker as ADatePicker,
    Divider as ADivider,
    message,
  } from 'ant-design-vue';
  import {
    findAllInvoiceType,
    findByImgid,
    delByImgid,
    findAllOcrApi,
    findByOcrSurplusNumber,
    findByCheckFlag,
    isCheckApi,
  } from '/@/api/record/eletronicInvoice_data/eletronic_invoice';
  import { ScrollContainer } from '/@/components/Container/index';
  // import { createImgPreview } from '/@/components/Previews';
  import { useRoute, useRouter } from 'vue-router';
  import { defHttp } from '/@/utils/http/axios';
  const route = useRoute();
  const router = useRouter();
  const emit=defineEmits([]);
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
  const status: any = ref(true);
  // 是否验真弹框
  const visible: any = ref(false);
  // 手动验真按钮
  const manualFlag: any = ref(false);

  const [register, { closeModal }] = useModalInner((data) => {
    // 发票类型集合
    findAllInvoiceType().then((res) => {
      invoiceTypeList.value = res;
    });

    // 修改时赋值
    formItems.value = data.data;

    // 获取ftp图片
    if (formItems.value.fapiaoQrCode !== '' && formItems.value.fapiaoQrCode !== null) {
      findByImgid(formItems.value.fapiaoQrCode).then((res) => {
        imageUrl.value = res.url;
        fileImgList.value.push(res);
      });
    } else {
      imageUrl.value = '';
    }
  });

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

  function getBase64(img: Blob, callback: (base64Url: string) => void) {
    const reader = new FileReader();
    reader.addEventListener('load', () => callback(reader.result as string));
    // 压缩
    reader.readAsDataURL(img);
  }

  const handleChange = (info: FileInfo) => {
    console.log(info.file.originFileObj);

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
    console.log(file.type);
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
      loading.value = false;
    });
    return false;
  };

  const beforeUploadList = (file: FileItem) => {
    const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png';
    if (!isJpgOrPng) {
      message.error('You can only upload JPG file!');
      return false;
    }
    const isLt2M = file.size / 1024 / 1024 < 2;
    if (!isLt2M) {
      message.error('Image must smaller than 2MB!');
    }
    if (isLt2M && isJpgOrPng) {
      getBase64(file as any, (base64Url: string) => {
        file.url = base64Url;
        loading.value = false;
      });
      // fileList.value = [...fileList.value, file]
      // fileLists.value.push(file as any)
      fileList.value.push(file as any);

      console.log(fileList.value);
    }
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

  async function handleOk() {
    if (status.value) {
      if (fileImgList.value.length > 0) {
        formItems.value.imgName = fileImgList.value[0].name;
      }
      emit('save', unref(formItems));
      closeModal();
    } else {
      message.error('发票已存在,不能录入!');
      return false;
    }
  }

  async function beforeOcrUpload() {
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
  }

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

  import { useGo } from '/@/hooks/web/usePage';

  async function downloadfile() {
    router.push('/ocr/download');
    const go = useGo();
    go('/ocr/download');
    return {};
    /* return defHttp.request({
    url: '/ocr/download',
    method: 'GET'
  })*/
  }
  // 识别回调
  async function handle123(info: any) {}

  async function isCheck() {
    if (formItems.value.fapiaoCode === '') {
      message.error('发票代码不能为空!');
      return false;
    }
    if (formItems.value.fapiaoNumber === '') {
      message.error('发票号码不能为空!');
      return false;
    }
    if (formItems.value.fapiaoCheckCode === '') {
      message.error('发票校验码不能为空!');
      return false;
    }
    if (formItems.value.fapiaoDate === '') {
      message.error('开票日期不能为空!');
      return false;
    }
    if (formItems.value.fapiaoMoney === '') {
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
    console.log(a);
    if (a === '2') {
      console.log('gogo');
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
</script>
<style lang="less" scoped>
  :dept(.ant-calendar-picker-input.ant-input),
  :deep(.ant-select-single:not(.ant-select-customize-input)
    .ant-select-selector
    .ant-select-selection-search-input) {
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

  .ant-upload.ant-upload-select-picture-card {
    width: 804px;
    height: 400px;
  }
</style>

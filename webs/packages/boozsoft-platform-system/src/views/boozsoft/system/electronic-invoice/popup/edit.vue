<template>
  <BasicModal
    width="900px"
    v-bind="$attrs"
    title="发票信息"
    @ok="handleOk()"
    @cancel="handleClose()"
    @register="register"
  >
    <div
      class="nc-open-content"
      style="height: 100%"
    >
      <a-divider style="font-size: 18px;">发票主体</a-divider>
      <p>
        <span>
           <label><font style="color: red">*</font>发票类别:</label>
                <a-select
                  v-model:value="formData.fpType"
                  placeholder="发票类别"
                  style="width: 27%;margin-left: 10px;text-align: center;"
                >
              <a-select-option value="1">进项发票</a-select-option>
              <a-select-option value="2">销项专票</a-select-option>
            </a-select>
        </span>
        <span style="margin-left: 32px;">
          <label><font style="color: red">*</font>发票类型:</label>
              <a-select
                v-model:value="formData.fapiaoType"
                placeholder="发票类型"
                style="width: 27%;margin-left: 10px;text-align: center;"
              >
              <a-select-option value="1">普票</a-select-option>
              <a-select-option value="2">专票</a-select-option>
            </a-select>
        </span>
      </p>
      <p>
        <label><font style="color: red">*</font>发票代码:</label>
        <a-input
          autocomplete="off"
          v-model:value="formData.fapiaoCode"
          style="width: 27%;margin-left: 10px;"
          onkeyup="value=value.replace(/[^\d{1,}\.\d{1,}|\d{1,}]/g,'')"
        />

        <span style="margin-left: 32px;">
            <label><font style="color: red">*</font>发票号码:</label>
            <a-input
              v-model:value="formData.fapiaoNumber"
              autocomplete="off"
              style="width: 27%;margin-left: 10px;"
              onkeyup="value=value.replace(/[^\d{1,}\.\d{1,}|\d{1,}]/g,'')"
            />
          </span>
      </p>
      <p>
        <label><font style="color: red">*</font>开票日期:</label>
        <a-input
          autocomplete="off"
          v-model:value="formData.fapiaoDate"
          style="width: 27%;margin-left: 10px;"
        />

        <span style="margin-left: 32px;">
            <label><font style="color: red">*</font>校验码:</label>
            <a-input
              v-model:value="formData.fapiaoCheckCode"
              autocomplete="off"
              style="width: 27%;margin-left: 10px;"
              onkeyup="value=value.replace(/[^\d{1,}\.\d{1,}|\d{1,}]/g,'')"
            />
          </span>
      </p>
      <p>
        <label><font style="color: red">*</font>发票税额:</label>
        <a-input
          autocomplete="off"
          v-model:value="formData.fapiaoTaxAmount"
          style="width: 27%;margin-left: 10px;"
          onkeyup="value=value.replace(/[^\d{1,}\.\d{1,}|\d{1,}]/g,'')"
        />

        <span style="margin-left: 32px;">
            <label><font style="color: red">*</font>发票金额:</label>
            <a-input
              v-model:value="formData.fapiaoMoney"
              autocomplete="off"
              style="width: 27%;margin-left: 10px;"
              onkeyup="value=value.replace(/[^\d{1,}\.\d{1,}|\d{1,}]/g,'')"
            />
          </span>
      </p>
      <p>
        <label><font style="color: red">*</font>价税合计:</label>
        <a-input
          autocomplete="off"
          v-model:value="formData.fapiaoTotalAmount"
          style="width: 27%;margin-left: 10px;"
          onkeyup="value=value.replace(/[^\d{1,}\.\d{1,}|\d{1,}]/g,'')"
        />

        <span style="margin-left: 32px;">
            <label>开 票 人:</label>
            <a-input
              v-model:value="formData.fapiaoDrawer"
              autocomplete="off"
              style="width: 27%;margin-left: 10px;"
            />
          </span>
      </p>
      <p>
        <label>收 款 人:</label>
        <a-input
          autocomplete="off"
          v-model:value="formData.fapiaoPayee"
          style="width: 27%;margin-left: 10px;"
        />

        <span style="margin-left: 32px;">
            <label> 复 核 :</label>
            <a-input
              v-model:value="formData.fapiaoCheckPsn"
              autocomplete="off"
              style="width: 27%;margin-left: 10px;"
            />
          </span>
      </p>
      <p>
        <label> 备 注 :</label>
        <a-input
          autocomplete="off"
          v-model:value="formData.fapiaoRemarks"
          style="width: 80%;margin-left: 10px;"
        />
      </p>

      <a-divider style="font-size: 18px;">购买方</a-divider>
      <p>
        <label><font style="color: red">*</font>购买方名称:</label>
        <a-input
          autocomplete="off"
          v-model:value="formData.buyerSupplier"
          style="width: 27%;margin-left: 10px;"
        />

        <span style="margin-left: 32px;">
            <label><font style="color: red">*</font>纳税识别号:</label>
            <a-input
              autocomplete="off"
              v-model:value="formData.buyerShuihao"
              style="width: 27%;margin-left: 10px;"
            />
          </span>
      </p>
      <p>
        <label>地址丶电话:</label>
        <a-input
          autocomplete="off"
          v-model:value="formData.buyerAddrPhone"
          style="width: 27%;margin-left: 10px;"
        />

        <span style="margin-left: 32px;">
            <label>开户行账号:</label>
            <a-input
              v-model:value="formData.buyerBankAccount"
              autocomplete="off"
              style="width: 27%;margin-left: 10px;"
            />
          </span>
      </p>

      <a-divider style="font-size: 18px;">销售方</a-divider>
      <p>
        <label><font style="color: red">*</font>销售方名称:</label>
        <a-input
          autocomplete="off"
          v-model:value="formData.sellSupplier"
          style="width: 27%;margin-left: 10px;"
        />

        <span style="margin-left: 32px;">
              <label><font style="color: red">*</font>纳税识别号:</label>
              <a-input
                v-model:value="formData.sellShuihao"
                autocomplete="off"
                style="width: 27%;margin-left: 10px;"
              />
            </span>
      </p>
      <p>
        <label>地址丶电话:</label>
        <a-input
          autocomplete="off"
          v-model:value="formData.sellAddrPhone"
          style="width: 27%;margin-left: 10px;"
        />

        <span style="margin-left: 32px;">
              <label>开户行账号:</label>
              <a-input
                autocomplete="off"
                v-model:value="formData.sellBankAccount"
                style="width: 27%;margin-left: 10px;"
              />
            </span>
      </p>
      <a-divider style="font-size: 18px;">发票明细</a-divider>
      <a-button class="editable-add-btn" @click="addMx" style="margin-bottom: 8px">Add</a-button>

      <a-table :columns="columns"
                 :data-source="dataSource"  size="small"
                 :pagination="{ pageSize: 4}">
          <template #number="{record,index }">
          <span slot="key" slot-scope="text,record,index">
           {{key}}
          </span>
          </template>
          <template v-for="col in ['stockName', 'stockModel', 'stockNum', 'unit','taxRate']" #[col]="{ text, record }" :key="col">
            <div>
              <a-input
                v-if="editableData[record.key]"
                v-model:value="editableData[record.key][col]"
                style="width: 100%"
              />
              <template v-else>
                {{ text }}
              </template>
            </div>
          </template>

          <template v-for="col in ['price', 'amount', 'taxes']" #[col]="{ text, record }" :key="col">
            <div>
              <a-input
                v-if="editableData[record.key]"
                type="number"
                v-model:value="editableData[record.key][col]"
                style="width: 100%"
              />
              <template v-else>
                {{ text }}
              </template>
            </div>
          </template>

          <template #action="{ record }">
            <!--<span>
              <a-divider type="vertical" />
              <a>删除</a>
              <a-divider type="vertical" />
              <a class="ant-dropdown-link">
                修改
                <down-outlined />
              </a>
            </span>-->
            <div class="editable-row-operations">
          <span v-if="editableData[record.key]">
            <a @click="save(record.key)">Save</a>

          </span>
              <span v-else>
            <a @click="edit(record.key)">Edit</a>

            <a @click="del(record.key)" style="padding-left: 5px">DEL</a>

          </span>
            </div>
          </template>
        </a-table>

      <a-divider style="font-size: 18px;">发票附件</a-divider>
      <a-button class="editable-add-btn" @click="dlown" style="margin-bottom: 8px">下载</a-button>

      <a-upload
        list-type="picture-card"
        class="avatar-uploader"
        name="file"
        v-model:file-list="fileOcrList"
        :show-upload-list="false"
        :disabled="fileOcrList.length > 3"
        :before-upload="beforeUpload"
        :headers="headers"
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
    </div>

  </BasicModal>
</template>

<script setup="props, {content}" lang="ts">
  import {ref, unref,  onMounted, UnwrapRef,reactive} from 'vue'
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
    Divider as ADivider,
    Tabs as ATabs, message
  } from "ant-design-vue"
  import CheckboxGroup from "ant-design-vue/lib/checkbox/Group";
  import { useTabs } from '/@/hooks/web/useTabs';
  import router from "/@/router";
  import { Moment } from 'moment';
  import {
    dlownload,
    eletronicInvoiceSaveApi,
    findAllOcrApi, findByHeaderUniqueInvoice,
    findByImgid, findByOcrSurplusNumber,
    uploadInvocie
  } from "/@/api/record/eletronicInvoice_data/eletronic_invoice";
  import {useCompanyOperateStoreWidthOut} from "/@/store/modules/operate-company";
  import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
  import {getCurrentAccountName} from "/@/api/task-api/tast-bus-api";
  import {getAllAccAuths} from "/@/api/record/system/financial-settings";
  import {useUserStore} from "/@/store/modules/user";
  import {cloneDeep} from "lodash-es";
  import {uploadFile} from "/@/api/record/sys-task/data";
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
  const formRef = ref();
  const formData = ref({
    tableData:[]
  });
  const gxFlgs = ref('1');
  const columns = [
    {
      dataIndex: 'key',
      key: 'key',
      title:  '序号',
      width: '40px',
      align: 'center',
    },
    {
      dataIndex: 'stockName',
      key: 'stockName',
      title: '商品名称',
      width: '120px',
      align: 'center',
      ellipsis: true,
      slots: { customRender: 'stockName' },
    },
    {
      title: '型号',
      dataIndex: 'stockModel',
      key: 'stockModel',
      width: '50px',
      align: 'center',
      slots: { customRender: 'stockModel' },
    },
    {
      title: '单位',
      dataIndex: 'unit',
      key: 'unit',
      width: '50px',
      align: 'center',
      ellipsis: true,
      slots: { customRender: 'unit' },
    },
    {
      title: '数量',
      key: 'stockNum',
      width: '50px',
      align: 'center',
      dataIndex: 'stockNum',
      slots: { customRender: 'stockNum' },
    },
    {
      title: '单价',
      key: 'price',
      width: '70px',
      align: 'center',
      dataIndex: 'price',
      ellipsis: true,
      slots: { customRender: 'price' },
    },
    {
      title: '金额',
      key: 'amount',
      width: '70px',
      align: 'center',
      dataIndex: 'amount',
      slots: { customRender: 'amount' },
    },
    {
      title: '税率',
      key: 'taxRate',
      width: '60px',
      align: 'center',
      dataIndex: 'taxRate',
      slots: { customRender: 'taxRate' },
    },
    {
      title: '税额',
      key: 'taxes',
      width: '60px',
      align: 'center',
      dataIndex: 'taxes',
      slots: { customRender: 'taxes' },
    },
    {
      title: '操作',
      key: 'action',
      align: 'center',
      width: '100px',
      slots: { customRender: 'action' },
    },
  ];
  const editableData: UnwrapRef<Record<string, DataItem>> = reactive({});
  interface DataItem {
    key: string;
    stockName: string;
    stockModel: string;
    stockNum: string;
    unit: string;
    price: string;
    amount: string;
    taxRate: string;
    taxes: string;
    invoiceHeaderUniqueCode: string;
    id: string;
    tenantId: string;
  }
  const data2: DataItem[] = [];
  const dataSource = ref(data2);
  const edit = (key: string) => {
    editableData[key] = cloneDeep(dataSource.value.filter(item => key === item.key)[0]);
  };
  const save = (key: string) => {
    Object.assign(dataSource.value.filter(item => key === item.key)[0], editableData[key]);
    delete editableData[key];
  };
  const del = (key: string) => {
    dataSource.value.splice(key-1,1);
  };
  const addMx = (key: string) => {
    dataSource.value.push({
      key: dataSource.value.length+1,
      stockName: '某某商品',
      amount: '0.00'
    });
  };

  const [register, {closeModal, setModalProps}] = useModalInner(async(data) => {
    // 方式2
    formData.value = data.data
    //获取明细list
    const mxlist = await findByHeaderUniqueInvoice(data.data.uniqueCode)
    dataSource.value = []
    mxlist.forEach((v,index)=>{
      v.key = index+1
      dataSource.value.push(v)
    })
    //获取附件list
    const fileId = data.data.fapiaoQrCode
    const fapiaoCheck = data.data.fapiaoCheck;
    if (fileId) {
      // 加载对应数据
      // 根据文件id读取图片 提供下载
      const data = await findByImgid(fileId);
      formItems.value.fapiaoCheck = fapiaoCheck
      imageUrl.value = data.url;
    }else{
      imageUrl.value = '';
    }
    setModalProps({minHeight: 400});
  })
  let isChanged: boolean = false

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
  // 数据库模式名称
  const databases = ref(getCurrentAccountName(true))

  async function handleOk() {
    formData.value.tenantId = 'xahls-001-2021'
    formData.value.tableData = dataSource.value
    console.log(formData.value)
    await eletronicInvoiceSaveApi(formData.value)
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
  // 发票类型集合
  const invoiceTypeList: any = ref([]);
  const fileOcrList: any = ref([]);
  const fileImgList: any = ref([]);
  // 文件
  const loading = ref<boolean>(false);
  const imageUrl = ref<string>('');
  const previewVisible = ref<boolean>(false);
  const previewImage = ref<string | undefined>('');

  interface FileItem {
    uid: string;
    name?: string;
    status?: string;
    response?: string;
    url?: string;
  }
  interface FileInfo {
    file: FileItem;
    fileList: FileItem[];
  }
  const fileList = ref<FileItem[]>([
    /*   {
          uid: '1',
          name: 'xxx.png',
          status: 'done',
          response: 'Server Error 500', // custom error message to show
          url: 'http://www.baidu.com/xxx.png',
        },*/
  ]);
  //已上传成功的文件信息
  const fileUpList = ref([])

  const customrequest = (data) => {
    const formData2 = new FormData()
    formData2.append('file', data.file)
    uploadInvocie(formData2)
      .then(async (info:any) => {
         //如果是pdf则返回转换后的base64
        console.log(info)
          imageUrl.value = info.baseUrl
          formData.value.fapiaoQrCode = info.id
          message.success('上传成功');
      })
  };

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

  const filterOption = (input: string, option: any) => {
    return option.value.toLowerCase().indexOf(input.toLowerCase()) >= 0;
  };

  const whetherGroup = ref(false)

  async function dlown() {
    //下载
    if(formData.value.fapiaoQrCode){
      const data = await dlownload(formData.value.fapiaoQrCode);
      console.log(data.headers['content-disposition'].split('filename=')[1]);
      const fileName = data.headers['content-disposition'].split('filename=')[1];
      downloadByData(data.data, fileName);
    }
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
  .ant-upload.ant-upload-select-picture-card {
    width: 380px;
    height: 205px;
  }
</style>

<template>
  <BasicModal
    width="400px"
    v-bind="$attrs"
    @ok="handleOk()"
    @cancel="handleClose()"
    @register="register"
  >
    <template #title>
      <div style="display: flex;" class="vben-basic-title">
        <PlusCircleOutlined v-if="!isEdit" style="font-size: 50px;color: #0096c7;"/>
        <FormOutlined v-else-if="isEdit" style="font-size: 50px;color: #0096c7;"/>
        <span style="line-height:50px;font-size: 30px;color: #0096c7;">&ensp;角色</span>
      </div>
<!--      <div style="display: inline-block;position:absolute;right: 120px;top: 20px;background:#ffffff">
        <img src="/@/assets/images/cz.png" draggable="false" style="height:100px;margin-right: 10px;"/>
      </div>-->
    </template>
    <div class="nc-open-content" style="height: 100%">
      <div class="open-content-up">
        <label>角色名称</label>
        <a-input
          v-model:value="formItems.roleName"
          autocomplete="off"  style="width: 60%"
        />

        <span v-if="showflag">
          <br/>
          <br/>
          <label>封存&emsp;&emsp;</label>
          <a-select
            v-model:value="formItems.roleFlag"
            option-filter-prop="children"
            style="width: 60%"
          >
          <a-select-option value="0">停用</a-select-option>
          <a-select-option value="1">启用</a-select-option>
        </a-select>
        </span>
        <p/>
<!--        <a-divider orientation="left">角色范围</a-divider>
        <a-transfer
          :data-source="mockData"
          :titles="['待选择', '已选择']"
          :target-keys="targetKeys"
          :selected-keys="selectedKeys"
          :render="item => item.title"
          :disabled="disabled"
          @change="transferChange"
          @selectChange="transferSelectChange"
        />-->

<!--        <a-divider orientation="left">模块范围设置</a-divider>-->
<!--        选择平台-->
<!--        <div style="text-align:center;margin-top:20px">
          <a-button>模块分配</a-button>
        </div>-->
      </div>
    </div>
  </BasicModal>
</template>

<script setup="props, { content }" lang="ts">
import {ref, unref, onMounted, reactive, toRaw} from 'vue';
import {BasicModal, useModalInner} from '/@/components/Modal';
import {
  Select as ASelect,
  Transfer as ATransfer,
  DatePicker as ADatePicker,
  Divider as ADivider,
  Input as AInput,
  InputNumber as AInputNumber,
  Checkbox as ACheckbox,
  Popover as APopover,
  Switch as ASwitch,
  Radio as ARadio,
  Upload as AUpload,
  Table as ATable,
  Tabs as ATabs,
  message,
  Button as AButton
} from 'ant-design-vue';
import {findByRoleName} from '/@/api/record/sys-role/data';
import {useUserStore} from "/@/store/modules/user";
import { PlusCircleOutlined,FormOutlined } from '@ant-design/icons-vue'
const ARangePicker = ADatePicker.RangePicker;
const ASelectOption = ASelect.Option;
const AInputSearch = AInput.Search;
const ARadioGroup = ARadio.Group;
const ARadioButton = ARadio.Button;
const ATabPane = ATabs.TabPane;

const emit = defineEmits(['register','save','handleClose']);
const userStore = useUserStore();
let formItemsCopy = null
const formItems: any = ref({
  id: null,
  roleNum: '',
  roleName: '',
  roleRange: '',
  roleAddTime: '',
  roleAddPsn: userStore.getUserInfo.id,
  roleFlag: '1'
});
const mockData = ref([
  {
    key: '1',
    title: '集团',
  },
  {
    key: '2',
    title: '组织',
  },
  {
    key: '3',
    title: '公司',
  }
])
const targetKeys = ref([])
const selectedKeys = ref([])
const showflag = ref(false)
const isEdit = ref(false)

const [register, {closeModal, setModalProps}] = useModalInner((data) => {
  if (data.data !== '') {
    isEdit.value = true
    showflag.value=true
    formItems.value.id = data.data.id
    formItems.value.roleNum = data.data.roleNum
    formItems.value.roleName = data.data.roleName
    formItems.value.roleAddTime = data.data.roleAddTime
    formItems.value.roleAddPsn = data.data.roleAddPsn
    formItems.value.roleFlag = data.data.roleFlag
    targetKeys.value=data.data.roleRange
    formItemsCopy = data.data
  }
});

// 选项在两栏之间转移时的回调函数
function transferChange(a,b) {
  targetKeys.value = a
}
// 选中项发生改变时的回调函数
function transferSelectChange(a,b) {
  selectedKeys.value=[...a,...b]
}

const handleOk = async () => {
  if (formItems.value.roleName === '') {
    return message.error('请填写角色名称');
  } else {
    let a=await findByRoleName(formItems.value.roleName)
    if((a>0 && formItemsCopy == null) || (formItemsCopy != null && formItemsCopy.roleName != formItems.value.roleName && a > 0)){
      return message.error('名称已存在，请重新输入！');
    }
  }
  if (targetKeys.value.length > 0)
  formItems.value.roleRange=targetKeys.value.join(',')
  emit('save', toRaw(formItems.value));
  closeModal();
}
const handleClose = () => {
  emit('handleClose');
  closeModal();
}
</script>
<style lang="less" scoped>
.nc-open-content {
  position: relative;
  margin-top: 40px;
  :deep(.ant-select-selector) {
    border: none !important;
    border-bottom: 1px solid #ffffff !important;
  }

  input {
    width: 30%;
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
  }

  .open-content-title {
    > div {
      display: inline-block;
    }

    > div:nth-of-type(1) {
      width: 200px;
      background-color: #efeeee;
      color: black;
      font-size: 20px;
      text-align: center;
      padding: 5px 10px
    }
  }

  .open-content-up {
    position: relative;

    .ocup-position {
      position: absolute;
      left: 0;
      width: 150px;
      background-color: #0096c7;
      color: white;
      font-size: 16px;
      text-align: center;
      padding: 5px 10px;
    }

    .ocup-position:nth-of-type(1) {
      top: 0px;
    }

    .ocup-position:nth-of-type(2) {
      top: 190px;
    }

    ul {
      padding: 10px 30px;

      li {
        margin: 10px 0;

        span {
          font-size: 14px;
          color: #747272;
        }

        > span:nth-of-type(1), .right_span {
          display: inline-block;
          width: 120px;
        }

        .ant-select {
          font-size: 14px;
        }
      }
    }
  }

  .open-content-foot {
    display: block;
    position: fixed;
    margin-top: 43px;
  }

  .ant-tabs-tabpane-active {
    overflow-y: auto;
    height: 400px;
  }

  .ant-select-selection-search-input {
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

  .ant-radio-group {
    .ant-radio-wrapper {
      width: 70px;

      .ant-radio-input {
        border-color: slategrey;
      }
    }

    p:nth-of-type(2) {
      margin-bottom: 0;
    }
  }
}


:deep(.ant-tabs-left-bar) {
  margin-right: 0px !important;
}

:deep(.ant-tabs-left) {
  .ant-tabs-tab {
    width: 150px;
    font-weight: bold;
    text-align: center;
    margin-bottom: 0px !important;
  }

  .ant-tabs-tab-active {
    background-color: #65cbec !important;
    color: rgba(0, 0, 0, 0.85) !important;
  }

  .ant-tabs-tab:nth-of-type(1) {
    margin-top: 35px;
  }

  .ant-tabs-tab:nth-of-type(3) {
    margin-top: 110px;
  }
}

</style>

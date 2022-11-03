<template>
  <BasicModal width="900px"
              v-bind="$attrs"
              title="集团信息"
              @ok="handleOk(getFieldsValue())"
              @register="register">
    <div class="nc-open-content"
         style="height: 100%">
      <div class="open-content-up">
        <a-input placeholder="请输入名称"
                 style="width: 85%;margin-left: 5.2%;" /><span>必填项</span><br><br>

        <label>公司地址</label>
        <a-input placeholder="街道" />
        <label>电话</label>
        <a-input placeholder="" /><br>

        <label />
        <a-input placeholder="街道2" />
        <label>手机</label>
        <a-input placeholder="" /><br>

        <label />
        <a-select placeholder="国家/地区  "
                  style="width: 35%;">
          <a-select-option value="lucy">中国</a-select-option>
        </a-select>
        <label>Email</label>
        <a-input placeholder="" /><br>

        <label />
        <a-select style="width: 11%;"
                  placeholder="省">
          <a-select-option value="lucy">湖北省</a-select-option>
        </a-select>
        <a-select style="width: 11%;margin: 0 1%"
                  placeholder="市">
          <a-select-option value="lucy">武汉市</a-select-option>
        </a-select>
        <a-select style="width: 11%;"
                  placeholder="区/县">
          <a-select-option value="lucy">汉阳区</a-select-option>
        </a-select>
        <label>网站</label>
        <a-input placeholder="列如：www.baidu.com" /><br>

        <label>税号</label>
        <a-input placeholder="" />
        <label>行业</label>
        <a-select placeholder=""
                  style="width: 35%;">
          <a-select-option value="lucy">Lucy</a-select-option>
        </a-select>
      </div>
      <div class="open-content-down">
        <a-tabs v-model:activeKey="activeKey"
                type="card">
          <a-tab-pane key="1"
                      tab="联系人&地址">
            <div class="down-tab-content">
              <label>法人代表</label>
              <a-input placeholder="" />
              <label>手机</label>
              <a-input placeholder="" /><br>
              <label>电话</label>
              <a-input placeholder="" />
              <label>Email</label>
              <a-input placeholder="" /><br>
            </div>
          </a-tab-pane>
          <a-tab-pane key="2"
                      tab="开户信息">
            <div class="content-down-tab">
              <div class="down-tab-content">
                <label>开户银行</label>
                <a-input placeholder="例如：中国建设银行朝阳路支行" />
                <label>开户地</label>
                <a-input placeholder="例如：武汉市汉阳区" /><br>
                <label>银行行号</label>
                <a-input placeholder="" />
                <label>银行账户</label>
                <a-input placeholder="" /><br>
              </div>
            </div>
          </a-tab-pane>
        </a-tabs>
      </div>
      <div />
    </div>
  </BasicModal>
</template>
<script lang="ts">
import { defineComponent, reactive, ref, toRefs } from 'vue';
import { BasicModal, useModalInner } from '/@/components/Modal';
import { BasicForm, FormSchema, useForm } from '/@/components/Form/index';
import { saveJigou } from '/@/api/record/system/jigou';

const schemas: FormSchema[] = [
  // { label: 'ID', show: false, field: 'id', component: 'Input', colProps: { span: 12 } },
  {
    label: '',
    field: 'deptName',
    component: 'Input',
    colProps: { span: 50 },
    suffix: '',
    componentProps: {
      placeholder: '请输入集团名称',
      onChange: (e: any) => {
        console.log(e);
      },
    },
    renderComponentContent: () => {
      return {
        suffix: () => 'suffix',
      };
    },
  },
  { label: '公司地址', field: 'tenantId', component: 'Input', colProps: { span: 12 } },
  { label: '电话', field: 'parentId', component: 'Input', colProps: { span: 12 } },
  { label: '部门全称', field: 'fullName', component: 'Input', colProps: { span: 12 } },
  { label: '排序', field: 'sort', component: 'Input', colProps: { span: 12 } },
  { label: '类型', field: 'deptType', component: 'Input', colProps: { span: 12 } },
  { label: '备注', field: 'remark', component: 'Input', colProps: { span: 12 } },
];

export default defineComponent({
  components: { BasicModal, BasicForm },

  setup(props, { emit, attrs }) {
    const state = reactive({});
    const modelRef = ref({});
    const [
      registerForm,
      {
        getFieldsValue,
        // setFieldsValue,
        // setProps
      },
    ] = useForm({
      labelWidth: 80,
      schemas,
      showActionButtonGroup: false,
      actionColOptions: {
        span: 24,
      },
    });

    const [register, { closeModal }] = useModalInner((data) => {
      // 方式2
      modelRef.value = {
        id: data.data.id,
        deptName: data.data.deptName,
        tenantId: data.data.tenantId,
        parentId: data.data.parentId,
        fullName: data.data.fullName,
        sort: data.data.sort,
        deptType: data.data.deptType,
        remark: data.data.remark,
      };
    });
    return {
      async edit(data) {
        await saveJigou(data);
        reload(data);
      },
      ...toRefs(state),
      register,
      schemas,
      registerForm,
      model: modelRef,
      getFieldsValue,
      handleOk(data) {
        emit('save', data);
        closeModal();
      },
      handleCancel() {},
      activeKey: ref('2'),
    };
  },
});
</script>
<style lang="less">
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
  .ant-select-selector {
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
    .ant-tabs-tab-active::before {
      position: absolute;
      top: 0px;
      left: 0;
      width: 100%;
      border-top: 2px solid transparent;
      border-radius: 2px 2px 0 0;
      transition: all 0.3s;
      content: '';
      pointer-events: none;
      background-color: rgb(1, 143, 251);
    }
  }
}
</style>

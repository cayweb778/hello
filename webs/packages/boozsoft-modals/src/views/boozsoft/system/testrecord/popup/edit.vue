<template>
  <BasicModal
    v-bind="$attrs"
    title="编辑地区"
    @ok="handleOk(getFieldsValue())"
    @register="register"
  >
    <BasicForm :model="model" @register="registerForm" />
  </BasicModal>
</template>
<script lang="ts">
import { defineComponent, reactive, ref, toRefs, unref, watch } from 'vue'
import { BasicModal, useModalInner } from '/@/components/Modal'
import { BasicForm, FormSchema, useForm } from '/@/components/Form/index'
import { saveAddress } from '/@/api/record/system/address'

const schemas: FormSchema[] = [
  { label: 'ID', show: false, field: 'id', colProps: { span: 12 }, component: 'Input' },
  { label: '唯一标识', show: false, field: 'uniqueCode', colProps: { span: 12 }, component: 'Input' },
  { label: '省', field: 'province', colProps: { span: 12 }, component: 'Input' },
  { label: '市', field: 'city', colProps: { span: 12 }, component: 'Input' },
  { label: '区', field: 'district', colProps: { span: 12 }, component: 'Input' },
  { label: '街道', field: 'station', colProps: { span: 12 }, component: 'Input' },
  { label: '电话区号', field: 'areaCode', colProps: { span: 12 }, component: 'Input' },
  { label: '邮编', field: 'postcode', colProps: { span: 12 }, component: 'Input' }
]
export default defineComponent({
  components: { BasicModal, BasicForm },

  setup(props, { emit, attrs }) {
    const state = reactive({})
    const modelRef = ref(reactive({}))
    const [
      registerForm,
      {
        getFieldsValue
        // setFieldsValue,
        // setProps
      }
    ] = useForm({
      labelWidth: 120,
      schemas,
      showActionButtonGroup: false,
      actionColOptions: {
        span: 24
      }
    })
    let isChanged = false
    let changeBeforeModel = {}
    const [register, { closeModal }] = useModalInner((data) => {
      const state = {
        id: data.data.id,
        uniqueCode: data.data.uniqueCode,
        province: data.data.province,
        city: data.data.city,
        district: data.data.district,
        station: data.data.station,
        areaCode: data.data.areaCode,
        postcode: data.data.postcode
      }
      changeBeforeModel = JSON.parse(JSON.stringify(state))

      // 方式2
      modelRef.value = state
    })
    return {
      async edit(data) {
        console.log(data)

        await saveAddress(data)
        reload(data)
      },
      ...toRefs(state), register, schemas, registerForm, model: modelRef,
      getFieldsValue,
      handleOk(data) {
        console.log(55)
        const modelValue = getFieldsValue()
        console.log(modelValue)
        isChanged = !(modelValue.id == changeBeforeModel.id &&
           modelValue.uniqueCode == changeBeforeModel.uniqueCode &&
           modelValue.province == changeBeforeModel.province &&
           modelValue.city == changeBeforeModel.city &&
           modelValue.district == changeBeforeModel.district &&
           modelValue.station == changeBeforeModel.station &&
           modelValue.areaCode == changeBeforeModel.areaCode &&
           modelValue.postcode == changeBeforeModel.postcode)
        if (isChanged) {
          emit('save', data)
          closeModal()
        }
        closeModal()
        // alert('没有改变过')
      },
      handleCancel() {

      }
    }
  }
})
</script>

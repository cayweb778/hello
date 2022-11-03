<template>
  <div :class="{enablethis:!enable,thisDiv:true}">
    <input ref="inputRef" v-if="enable" @focus="inputRef.select()"
           @keyup.left="registerEvents.left()" @keyup.right="registerEvents.next()"
           @keyup.enter="registerEvents.next()" style="height: 100%;width:100%;text-align: right"
           @blur="textareaChage" v-model="props.modelValue.value.currencyExchangeRate"/>
  </div>
</template>
<script setup>
import {ref, computed} from 'vue';
import {
  useRegiterEvent,
  useRegiterEventNoEmit
} from '/@/components/pingzheng-fillin/utils/regiterEvent';

const props = defineProps(['modelValue', 'rowDefine', 'registerEventFun'])
const rowDefine = computed(() => props.rowDefine)
const enable = computed(() => rowDefine.value.requireModels.indexOf("huiLv") != -1)


const inputRef = ref()
const emit = defineEmits(['register'])


Object.assign(inject('registerEventFun'), useRegiterEventNoEmit({
  data: {
    row: null,
  },
  actions: {
    focus() {
      inputRef.value.focus()
    }
  }
}))
const registerEvents = inject('registerEventFun');


function textareaChage() {
  props.modelValue.value.currencyExchangeRate = parseFloat(props.modelValue.value.currencyExchangeRate).toFixed(2)
  registerEvents.change()
}
</script>

<template>
  <div :class="{ enablethis: !enable, thisDiv: true }" style="border: solid 1px blue">
    <div v-if="enable" style="margin-top: 2px; font-size: 10px">
      <Popover :visible="visiblePopover" placement="bottom" trigger="contextMenu">
        <div
          style="height: 100%; overflow: hidden; text-align: left"
          @click="visiblePopover = !visiblePopover"
        >
          <!--          <div>计量单位: {{ props.modelValue.value.slUnit }}</div>-->
          <div>数量: {{ props.modelValue.value.slNumber }}</div>
          <div>单价: {{ props.modelValue.value.slPrice }}</div>
        </div>

        <template #content>
          <div>
            <!--            <div>计量单位: {{ props.modelValue.value.slUnit }}</div>-->
            <div
              >数量:
              <Input
                ref="shuliangRef"
                @keyup.enter="danJiaRef.focus()"
                @input="hello"
                style="width: 150px"
                :maxlength="6"
                v-model:value="slNumberInput"
            /></div>
            <div
              >单价:
              <Input
                ref="danJiaRef"
                style="width: 150px"
                @keyup.enter="closePopover"
                @input="hello2"
                :maxlength="6"
                v-model:value="slPriceInput"
            /></div>
            <div style="text-align: center" @click="closePopover">
              <Button type="primary" danger>关闭</Button>
            </div>
          </div>
        </template>
      </Popover>
    </div>
    <!--    <Select :value="value">-->
    <!--      <Option :key="item.key" :label="item.label" :value="item.key" v-for="item in list">{{item.label}}</Option>-->
    <!--    </Select>-->
  </div>
</template>
<script setup>
  import { computed, inject, onMounted, provide, ref, watch } from 'vue';
  import { Select, Popover, Input, Button } from 'ant-design-vue';
  import { useRegiterEvent } from '/@/components/pingzheng-fillin/utils/regiterEvent';

  const props = defineProps(['modelValue', 'rowDefine']);

  const list = [
    {
      key: '1',
      label: '个',
    },
    {
      key: '2',
      label: '把',
    },
  ];
  const propsSlNumber = computed(() => props.modelValue.value.slNumber);
  const propsSlPrice = computed(() => props.modelValue.value.slPrice);
  const slNumberInput = ref('');
  const slPriceInput = ref('');

  watch(propsSlNumber, () => (slNumberInput.value = propsSlNumber.value));
  watch(propsSlPrice, () => (slPriceInput.value = propsSlPrice.value));
  // watch(slNumberInput, () => (props.modelValue.value.slNumber = slNumberInput.value));
  // watch(slPriceInput, () => (props.modelValue.value.slPrice = slPriceInput.value));

  const visiblePopover = ref(true);
  const rowDefine = computed(() => props.rowDefine);
  const enable = computed(() => rowDefine.value.requireModels.indexOf('shuliangjine') != -1);
  const shuliangRef = ref();
  const value = '个';
  const Option = Select.Option;
  const danJiaRef = ref();
  const emit = defineEmits(['register']);
  const registerEvents = useRegiterEvent({
    emit,
    data: {
      focus() {},
      requireModels: null,
      rowIndex: null,
      setRequireModels: null,
      setBiZhong: null,
      setUnit: null,
    },
  });

  const pingzhengGridParams = inject('pingzhengGridParams');
  const pingzhengGridState = inject('pingzhengGridState');

  function hello(e) {
    slNumberInput.value = e.target.value.replace(/[^\d]/g, '');
  }

  function hello2(e) {
    slPriceInput.value = e.target.value.replace(/[^\d]/g, '');
    // pingzhengGridParams.modelValue.change();
  }

  function closePopover() {
    console.log(54555);
    if (props.modelValue.value.slPrice == null) {
      props.modelValue.value.slPrice = '';
    }
    props.modelValue.value.slNumber = slNumberInput.value;
    props.modelValue.value.slPrice = slPriceInput.value;

    pingzhengGridParams.modelValue.columnDatas.jiliangdanwei.text = `<div>数量:${props.modelValue.value.slNumber}</div><div>单价:${props.modelValue.value.slPrice}</div>`;
    pingzhengGridParams.modelValue.change();
    pingzhengGridParams.modelValue.next();
    pingzhengGridState.data.blur();
    visiblePopover.value = false;
  }
  onMounted(() => {
    setTimeout(() => shuliangRef.value.focus(), 100);
    shuliangRef.value.focus();
  });
  watch(visiblePopover, (e) => {
    if (e) {
      shuliangRef.value.focus();
    }
  });
</script>
<style scoped>
  .thisDiv {
    height: 100%;
  }
  .enablethis {
    background: #dedede;
  }
</style>

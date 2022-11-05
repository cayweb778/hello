<template>
  <div :class="{ enablethis: !enable, thisDiv: true }" style="border: solid 1px blue">
    <div v-if="enable" style="margin-top: 4px">
      <Popover :visible="visiblePopover" placement="bottom">
        <TextContent
          :huiLv="props.modelValue.value.currencyExchangeRate"
          :money="props.modelValue.value.currencyMoney"
        />

        <template #content>
          <div>
            <!--            <div>计量单位: {{ props.modelValue.value.slUnit }}</div>-->
            <div
              >汇率:
              <Input
                ref="huiLvRef"
                :maxlength="6"
                style="width: 150px"
                @input="hello"
                v-model:value="props.modelValue.value.currencyExchangeRate"
                @keyup.enter="closePopover"
              />
              <button @click="closePopover">确认</button>
            </div>
            <!--            <div-->
            <!--              >金额:-->
            <!--              <Input-->
            <!--                style="width: 150px"-->
            <!--                v-model:value="props.modelValue.value.currencyMoney"-->
            <!--            /></div>-->
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
  import { useRegiterEventNoEmit } from '/@/components/pingzheng-fillin/utils/regiterEvent';

  const props = defineProps(['modelValue', 'rowDefine']);
  import { Select, Popover, Input } from 'ant-design-vue';
  import { computed, inject, onMounted, ref } from 'vue';
  import TextContent from './Bizhong/TextContent.vue';
  const list = [
    {
      key: 'RMB',
      label: '人民币',
    },
    {
      key: 'USD',
      label: '美元',
    },
  ];
  const rowDefine = computed(() => props.rowDefine);
  const enable = computed(() => rowDefine.value.requireModels.indexOf('huiLv') != -1);
  const visiblePopover = ref(true);
  const emit = defineEmits(['register', 'registerEventFun']);

  Object.assign(
    inject('registerEventFun'),
    useRegiterEventNoEmit({
      data: {
        focus() {},
        requireModels: null,
        rowIndex: null,
        setRequireModels: null,
        setBiZhong: null,
        setUnit: null,
      },
    }),
  );

  function hello(e) {
    props.modelValue.value.currencyExchangeRate = e.target.value.replace(/[^\d]/g, '');
  }

  const pingzhengGridParams = inject('pingzhengGridParams');
  const pingzhengGridState = inject('pingzhengGridState');

  function closePopover() {
    if (props.modelValue.value.currencyMoney == null) {
      props.modelValue.value.currencyMoney = '';
    }
    pingzhengGridParams.modelValue.columnDatas.bizhong.text = `<div>汇率:${props.modelValue.value.currencyExchangeRate}</div><div>金额:${props.modelValue.value.currencyMoney}</div>`;
    pingzhengGridParams.modelValue.next();
    pingzhengGridState.data.blur();
    visiblePopover.value = false;
  }

  const registerEvents = inject('registerEventFun');
  const value = props.modelValue.value.currency == null ? 'RMB' : props.modelValue.value.currency;
  const Option = Select.Option;
  const huiLvRef = ref();
  onMounted(() => {
    setTimeout(() => huiLvRef.value.focus(), 100);
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

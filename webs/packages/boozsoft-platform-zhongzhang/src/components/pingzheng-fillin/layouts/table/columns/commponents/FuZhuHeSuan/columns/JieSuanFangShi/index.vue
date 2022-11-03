<template>
  <div style="height: 100%">
    <Drawer
      v-model:visible="visible"
      :closable="false"
      :get-container="hello"
      :style="{ position: 'absolute' }"
      placement="right"
      title="结算方式管理"
    >
      <div> aaaaa </div>
    </Drawer>
    <Popover
      v-model:visible="popoverVisible"
      overlayClassName="pingzhengPopover"
      trigger="contextmenu"
      placement="bottom"
    >
      <Input
        ref="inputRef"
        class="fuZhuHeSuanInputClass"
        v-model:value="showText"
        v-on="inputEvents"
      >
        <template #suffix>
          <SearchOutlined @click="inputEvents.searchPage()" class="searchIco" />
        </template>
      </Input>

      <template #content>
        <div style="width: 180px">
          <PingzhengItemList
            v-model:items="jieSuanFangshiList2"
            @change="selectBlur($event.label)"
          />
        </div>
      </template>
    </Popover>
    <ProfileOutlined style="margin-left:10px;color:gray;font-size:18px"/>
  </div>
</template>
<script setup>
  import { ref, computed, watch, onMounted, inject, h } from 'vue';
  import PingzhengItemList from '/@/components/pingzheng-fillin/components/PingzhengItemList/index.vue';
  import { Input, Popover, Select, Drawer, notification } from 'ant-design-vue';
  import { ProfileOutlined } from '@ant-design/icons-vue';
  import { SearchOutlined, WarningOutlined } from '@ant-design/icons-vue';
  import pinyin from 'js-pinyin';
  import { useJieSuanFangShi, useJieSuanFangShiList } from './funs';

  const Option = Select.Option;
  const emit = defineEmits(['change', 'focus', 'left', 'right']);
  const tableData = inject('tableData');
  const props = defineProps(['modelValue']);

  const showOpenSelect = ref(false);
  const jieSuanFangshiList = ref([]);
  const visible = ref(false);
  const hello = ref();

  function contains(str, str2) {
    return str.indexOf(str2) != -1;
  }

  const showFullData = ref(false);
  const popoverVisible = ref(false);
  const showText = ref('');
  const inputRef = ref();

  const { isEmptyJieSuanFangShi, findJieSuanFangShi } = useJieSuanFangShi(
    props.modelValue.jieSuanMode,
  );

  const { resetJieSuanFangShi } = useJieSuanFangShiList(tableData, jieSuanFangshiList);

  const jieSuanFangshiList2 = computed(() => {
    return jieSuanFangshiList.value.filter((item) => {
      if (showFullData.value) {
        console.log(22);
        return true;
      }

      if (showText.value == '') {
        return true;
      } else if (showText.value == null) {
        return true;
      }
      return (
        contains(item.label, showText.value) ||
        contains(item.key, showText.value) ||
        pinyin.getCamelChars(item.label).indexOf(showText.value.toUpperCase()) != -1
      );
    });
  });

  function selectBlur(e) {
    showText.value = e;
  }

  function keydownSpace(e) {
    var e = window.event || event;
    if (e.preventDefault) {
      e.preventDefault();
    } else {
      window.event.returnValue = false;
    }
    popoverVisible.value = !popoverVisible.value;
  }

  const inputEvents = {
    keydown(e) {
      switch (e.key) {
        case 'ArrowLeft':
          emit('left');
          break;
        case 'ArrowRight':
          emit('right');
          break;
        case ' ':
          keydownSpace(e);
          e.preventDefault();
          break;
      }
    },
    input() {
      console.log(jieSuanFangshiList.value);
      showOpenSelect.value = true;
      showFullData.value = false;

      //
      // if (e.value.length == 0) {
      //   if (showText.value == null) {
      //     showText.value = ''
      //   }
      if (jieSuanFangshiList.value.length == 0) {
        notification.open({
          key: 'noCode',
          message: '结算方式必填!',
          description: '错误:w结算方式列表获取长度为0!',
          icon: h(WarningOutlined, { style: 'color: red' }),
        });
        showText.value = showText.value.slice(0, showText.value.length - 1);
        return;
      }

      if (jieSuanFangshiList2.value.length === 0) {
        notification.open({
          key: 'noCode',
          message: '结算方式警告',
          description: '未检索到数据.',
          icon: h(WarningOutlined, { style: 'color: #e1c73b' }),
        });
        showText.value = showText.value.slice(0, showText.value.length - 1);
      } else {
        popoverVisible.value = true;
      }

      // }
    },
    blur() {
      setTimeout(() => {
        showOpenSelect.value = false;
        popoverVisible.value = false;
      }, 200);
    },
    searchPage() {
      popoverVisible.value = true;
    },
  };

  watch(showOpenSelect, (e) => {
    if (e) {
      showFullData.value = true;
    }
  });
  watch(showText, () => {
    const arr = jieSuanFangshiList.value.filter((item) => item.label == showText.value);
    console.log(857)
    if (arr.length > 0) {
      props.modelValue.jieSuanMode = arr[0].key;
    }
  });

  onMounted(async () => {
    await resetJieSuanFangShi();

    !isEmptyJieSuanFangShi && (showText.value = findJieSuanFangShi().label);
  });

  defineExpose({
    focus() {
      showFullData.value = true;
      inputRef.value.focus();
    },
  });
</script>
<style>
  .kuaiJiKeMuSelect {
    width: auto !important;
    color: black;
    width: 200px;
    border-radius: 10px;
    box-shadow: 0 0 6px black;
  }

  .inputRef {
    height: 100%;
  }
</style>

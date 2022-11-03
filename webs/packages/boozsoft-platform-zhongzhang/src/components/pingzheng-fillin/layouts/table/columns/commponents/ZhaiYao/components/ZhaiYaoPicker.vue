<template>
  <Popover placement="bottom" overlayClassName="pingzhengPopover" :visible="enableSelect">
    <template #content>
      <div ref="selectRef2" style=" cursor: pointer">
        <div class="thisTable" style="width: 182px;">
          <ZhaiYaoItemList
            ref="vScroll"
            v-model:items="filterZhaiYaoList"
            @change="change($event.label)"
          />
        </div>
      </div>
    </template>

    <ZhaiYaoTextarea
      ref="inputTextareaRef"
      class="inputRef kuaiJiKeMuInput"
      v-model:value="inputText"
      @changeValue="inputText=$event"
      @change="helloChange"
      @blur2="helloBlur()"
      :inputEvents="inputEvents"
    >
      <template #suffix>
        <SearchOutlined @click="inputEvents.search()" class="searchIco" />
      </template>
    </ZhaiYaoTextarea>
  </Popover>
</template>
<script setup>
  import { computed, inject, nextTick, ref, watch } from 'vue';
  import { Input, Popover } from 'ant-design-vue';
  import { SearchOutlined } from '@ant-design/icons-vue';
  import ZhaiYaoItemList from '/@/components/pingzheng-fillin/components/ZhaiYaoItemList/index.vue';
  import { usePingZhengResult } from '/@/components/pingzheng-fillin/hooks/okFun';
  import ZhaiYaoTextarea from './ZhaiYaoTextarea.vue';
  import pinyin from 'js-pinyin';
  const TextArea = Input.TextArea;
  const emits = defineEmits(['change','blur', 'updated:modelValue']);
  const usePageRouterApi = inject('usePageRouterApi');
  const pingzhengData = inject('tableData');
  const showSearch = ref(false);
  const props = defineProps(['items', 'open', 'modelValue', 'inputEvents']);

  const textareaRef = ref();
  const inputRef = ref();
  const selectRowIndex = ref(0);
  const enableSelect = ref(false);
  const vScroll = ref();
  const showFullList = ref(false);
  const inputText = ref('');
  const inputTextareaRef = ref();

  watch(inputText, (e) => {
    console.log('ZhaiYaoTextarea Change')
    // inputText.value = inputText.value.replace('\n', '');
    emits('updated:modelValue', inputText.value);
  });
  function getPingzhengListRef() {
    return vScroll.value.$el;
  }
  function change(e) {
    emits('change', e);
    enableSelect.value = false;
    setTimeout(() => (showSearch.value = false), 2000);
    setTimeout(() => {
      registerEvents.value.blur();
    }, 200);
  }

  function helloBlur(){
    // console.log(123213)
    emits('blur',111)
  }

  function helloChange(e) {
    console.log(e);

    change(e.target.value);
  }
  const modelValue = computed(() => props.modelValue);
  watch(modelValue, () => {
    inputText.value = modelValue.value;
  });
  const filterZhaiYaoList = computed(() => {
    console.log(9991111)
    if (inputText.value == null) {
      return props.items;
    }
    if (inputText.value == '' || showFullList.value) {
      return props.items;
    }
    return props.items.filter((item) => {
      return (
        item.label.indexOf(inputText.value) != -1 ||
        pinyin.getCamelChars(item.label).indexOf(inputText.value.toUpperCase()) != -1
      );
    });
  });

  const registerEvents = ref();
  registerEvents.value = inject('registerEventFun');

  function keydownUp() {
    window.aaa = textareaRef.value;
    var e = window.event || event;
    if (e.preventDefault) {
      e.preventDefault();
    } else {
      window.event.returnValue = false;
    }
  }

  function cacelEnableAll() {
    props.items.forEach((item) => (item.enable = false));
  }
  const inputEvents = {
    keyup(e) {
      switch (e.key) {
        case 'ArrowUp':
          if (!enableSelect.value) {
            registerEvents.value.up();
          } else {
            cacelEnableAll();
            if (props.items <= 1) {
              return;
            }
            selectRowIndex.value--;
            if (selectRowIndex.value == -1) {
              selectRowIndex.value = props.items.length - 1;
              console.log(selectRowIndex.value);
              const body = getPingzhengListRef();
              body.scrollTop = body.offsetHeight;
              console.log(body.scrollTop);
            } else {
              getPingzhengListRef().scrollTop = getPingzhengListRef().scrollTop - 30;
            }
            props.items[selectRowIndex.value].enable = true;
            // document.querySelector('.a-table-font-size-16  .ant-table-body')
          }
          break;
        case 'ArrowDown':
          if (!enableSelect.value) {
            registerEvents.value.down();
          } else {
            props.items.forEach((item) => (item.enable = false));
            if (props.items <= 1) {
              props.modelValue.value = e.target.value;
              registerEvents.value.down();
              return;
            }
            if (selectRowIndex.value == props.items.length) {
              selectRowIndex.value = 0;
              getPingzhengListRef().scrollTop = 0;
            }
            props.items[selectRowIndex.value++].enable = true;
            if (selectRowIndex.value > 6) {
              getPingzhengListRef().scrollTop = getPingzhengListRef().scrollTop + 34;
            }
          }
          break;
        case 'ArrowLeft':
          break;
        case 'ArrowRight':
          break;
        case 'Enter':
          console.log(46565)
          enableSelect.value = false;
          nextTick(() => {
            const arr = props.items.filter((item) => item.enable);
            if (arr.length == 0) {
              emits('change', inputText.value);
              // abcdd();
            } else {
              emits('change', arr[0].label);
            }
          });
          break;
      }
    },
    keydown(e) {
      inputText.value = inputText.value.replace('\n', '');
      switch (e.key) {
        case 'ArrowUp':
          keydownUp(e);
          break;
        case 'ArrowDown':
          keydownUp(e);
          break;
        case 'ArrowLeft':
          if (
            (textareaRef.value.selectionEnd == 0 && textareaRef.value.selectionStart == 0) ||
            (textareaRef.value.selectionEnd == textareaRef.value.selectionStart &&
              !enableSelect.value)
          ) {
            registerEvents.value.left();

            var e = window.event || event;
            if (e.preventDefault) {
              e.preventDefault();
            } else {
              window.event.returnValue = false;
            }
          }
          break;
        case 'ArrowRight':
          if (
            (textareaRef.value.selectionEnd == 0 && textareaRef.value.selectionStart == 0) ||
            (textareaRef.value.selectionEnd == textareaRef.value.selectionStart &&
              !enableSelect.value)
          ) {
            var e = window.event || event;
            if (e.preventDefault) {
              e.preventDefault();
            } else {
              window.event.returnValue = false;
            }
            setTimeout(() => {
              registerEvents.value.right();
            }, 200);
          }
          break;
        case 'Tab':
          abcdd(e);
          break;
        case ' ':
          enableSelect.value = true;
          if (enableSelect.value) {
            // enableSelect.value = false;
          } else {
            var e = window.event || event;
            if (e.preventDefault) {
              e.preventDefault();
            } else {
              window.event.returnValue = false;
            }
            showFullList.value = true;
            enableSelect.value = true;
          }
          nextTick(() => {
            // selectRef.value.focus()
          });
          e.preventDefault();
          break;
      }
    },
    input(e) {
      emits('updated:modelValue', e.target.value);
      if (!enableSelect.value) {
        showFullList.value = true;
      } else {
        showFullList.value = false;
      }
      enableSelect.value = true;
    },
    change(e) {
      // setTimeout(() => (showSearch.value = false), 2000);
      // emits('change', e.target.value);
      // emits('change', inputText.value);
      // setTimeout(()=>{
      //   registerEvents.value.blur();
      // },2000)
    },
    focus() {
      usePingZhengResult(pingzhengData).saveTempData(usePageRouterApi);
      setTimeout(() => {
        showSearch.value = true;

        nextTick(() => {
          inputTextareaRef.value.select();
        });
      });
    },
    search() {
      enableSelect.value = true;
    },
  };

  watch(filterZhaiYaoList, () => {
    selectRowIndex.value = 0;
  });

  defineExpose({
    focus() {
      inputTextareaRef.value.focus();
    },
  });
</script>
<style>
.kuaiJiKeMuInput{
  word-break: break-word; font-weight: 900; padding: 0
}
</style>

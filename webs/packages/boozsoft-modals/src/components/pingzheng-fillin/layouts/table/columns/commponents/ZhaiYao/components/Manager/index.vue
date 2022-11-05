<template>
  <div style="height:100%;position:relative">
    <div id="hello222" v-show="visible" ref="hello" style="    position: fixed;
    left: 0px;
    top: 0px;
    width: 100%;
    height: 100%;
    z-index: 100000000;
    background: #0000004d;"></div>
    <Drawer
        title="摘要管理"
        placement="right"
        :closable="false"
        v-model:visible="visible"
        :get-container="hello"
        width="500"
        :style="{ position: 'absolute' }"
        @after-visible-change="afterVisibleChange"
    >
      <div style="display: flex;flex-direction: row;align-items: center;margin-bottom: 20px">
        <div><input placeholder="请输入摘要名称" type="text" style="border:solid 1px #E5E5E5"/></div>
        <Button>新增</Button>
      </div>
      <table>
        <tbody>
        <tr>
          <td>key</td>
          <td>label</td>
          <td>操作</td>
        </tr>
        <tr v-for="it in zhaiYaoList2">
          <td>{{ it.key }}</td>
          <td>{{ it.label }}</td>
          <td>
            <Button>编辑</Button>
            <Button>删除</Button>
          </td>
        </tr>
        </tbody>
      </table>

    </Drawer>

    <!--    <SearchOutlined v-if="showSearch"-->
    <!--                    style="position: absolute;z-index:1000001;top:8px;right:3px;cursor: pointer"-->
    <!--                    @click="keyDownSpace"></SearchOutlined>-->


    <Popover placement="bottom" overlayClassName="pingzhengPopover" :visible="enableSelect">
      <template #content>
        <div
            ref="selectRef2"
            style="padding: 4px 8px; cursor: pointer"

        >
          <div class="thisTable"
               style="width:182px;padding: 4px 8px; cursor: pointer;">
            <PingzhengItemList v-model:items="zhaiYaoList2" @change="selectBlur($event.label)"></PingzhengItemList>

            <!--            <BasicTable-->
            <!--                :showIndexColumn="false"-->
            <!--                :rowClassName="rowClass"-->
            <!--                :columns="[-->
            <!--                {title: '编码', dataIndex: 'key', width: 50,},-->
            <!--                {title: '名称', dataIndex: 'label', width: 100,},-->
            <!--                // {title: '上级', dataIndex: 'label2', width: 50,},-->
            <!--                {title: '操作', width: 50, dataIndex: 'station',slots: {customRender: 'station'}}-->
            <!--             ]"-->
            <!--                :scroll="{ y: 200 }"-->
            <!--                :pagination="false"-->
            <!--                @row-click="selectBlur($event.label)"-->
            <!--                :data-source=" zhaiYaoList2"-->
            <!--                row-key="key"-->
            <!--            >-->

            <!--              <template #station="{ record }">-->
            <!--                <Button style="background: inherit">调整</Button>-->
            <!--              </template>-->
            <!--            </BasicTable>-->
            <!--            <div style="text-align: center">关键字：{{-->
            <!--                props.modelValue.columnDatas['zhaiYao'].text-->
            <!--              }},总数据:{{ zhaiYaoList.length }}条-->
            <!--            </div>-->
            <!--            <Button style="padding:0 10px">增加</Button>-->
            <!--            <Button style="padding:0 10px">栏目</Button>-->
            <!--            <Button style="padding:0 10px">全模糊匹配</Button>-->
            <!--            <Button style="padding:0 10px">全部</Button>-->
            <!--            <Button style="padding:0 10px" @click="visible=true">-->
            <!--              <SettingOutlined/>-->
            <!--              管理-->
            <!--            </Button>-->

          </div>
        </div>
      </template>
      <InputSearch ref="textareaRef"
                   v-model:value="inputText"
                   :disabled="tableData.settings.onlyShow"
                   v-on="inputEvents"
                   style="position:absolute;z-index:1000000;left:0;width:100%;height:100%;"
                   class="inputRef"/>
    </Popover>
    <!--    <Select ref="selectRef"-->
    <!--            dropdownClassName="kuaiJiKeMuSelect"-->
    <!--            v-show="enableSelect"-->
    <!--            v-model:value="value"-->
    <!--            @change="selectBlur"-->
    <!--            :open="openSelect" :dropdownStyle="{zIndex:'2200999999'}"-->
    <!--            style="position:absolute;top:0;width:100%;left:0;z-index:-1" class="abc" auto-size-->
    <!--            :height="'100%'"-->
    <!--            show-search>-->
    <!--      <template #dropdownRender="{ menuNode: menu }">-->
    <!--        &lt;!&ndash;          @mouseleave="openSelect=false"&ndash;&gt;-->

    <!--        &lt;!&ndash;          <v-nodes :vnodes="menu" />&ndash;&gt;-->
    <!--        &lt;!&ndash;         <div @click="visible=true">&ndash;&gt;-->

    <!--        &lt;!&ndash;          <SettingOutlined />&ndash;&gt;-->
    <!--        &lt;!&ndash;          摘要管理&ndash;&gt;-->
    <!--        &lt;!&ndash;         </div>&ndash;&gt;-->
    <!--        &lt;!&ndash;           <br/>&ndash;&gt;-->
    <!--        &lt;!&ndash;          <div @click="openSelect=false">&ndash;&gt;-->
    <!--        &lt;!&ndash;            <CloseOutlined @click="openSelect=false" />&ndash;&gt;-->
    <!--        &lt;!&ndash;            关闭&ndash;&gt;-->
    <!--        &lt;!&ndash;          </div>&ndash;&gt;-->
    <!--      </template>-->
    <!--    </Select>-->

  </div>
</template>
<script setup>
import {
  useRegiterEvent,
  useRegiterEventNoEmit
} from '/@/components/pingzheng-fillin/utils/regiterEvent';
import {ref, computed, onUpdated, onMounted, watch, nextTick, inject} from 'vue';
import {
  SearchOutlined, PlusOutlined, SettingOutlined, CloseOutlined
} from '@ant-design/icons-vue'
import {Button, Input, Select, Drawer, Popover} from 'ant-design-vue';

const InputSearch = Input.Search
import pinyin from 'js-pinyin'
// ;(async ()=>{
//   const a=await import ('js-pinyin');
//   const pinyin=a.default
//   // console.log(pinyin("我喜欢你", {
//   //   segment: true,                // 启用分词
//   //   group: true                   // 启用词组
//   // }))
// })();
import {findSettModesAll} from "/@/api/record/system/sett-modes";
import {useRouteApi, useRouterApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {BasicTable} from '/@/components/Table'
import PingzhengItemList from '/@/components/pingzheng-fillin/components/PingzhengItemList/index.vue'
import {findAllApi} from "/@/api/boozsoft/account/AccvoucherCdigest";
import {VScroll} from '/@/components/VirtualScroll/index';

const tableData = inject('tableData');
const usePageRouterApi = inject('usePageRouterApi');
const datasourcePicker = inject('datasourcePicker');

const tempDatasourceLock = datasourcePicker.value.datasourceLock

async function useApiData() {
  const apiData = await usePageRouterApi(findAllApi)()
  zhaiYaoList.value = apiData.items.map(it => ({
    key: it.ccode,
    value: it.content,
    label: it.content
  }))
}

const props = defineProps(['modelValue', 'registerEventFun'])
const emit = defineEmits(['register'])
const textareaRef = ref()
const zhaiYaoList = ref([])
const enableSelect = ref(false)
const openSelect = ref(false)

const value = ref('')

// api
watch(enableSelect, useApiData)


// watch(datasourcePicker.value.datasourceLock,abc1)


watch(props, () => {
  props.modelValue.columnDatas['zhaiYao'].text = props.modelValue.value
}, {immediate: true})
// const showText=computed(()=>{
//   const a=zhaiYaoList.value.filter(item=>item.key===value.value)
//   if(a.length===0){
//     return props.modelValue.value
//   }else{
//     return a[0].label
//   }
// })
const inputRef = ref()

function divClick() {
  // isShowText.value = false
  // nextTick(() => {
  // })
}

const registerEvents = ref()
onMounted(() => {
  textareaRef.value.focus()

  inputText.value = props.modelValue.columnDatas['zhaiYao'].text
  Object.assign(inject('registerEventFun'), useRegiterEventNoEmit({
    data: {
      setValue(e) {
        props.modelValue.value = e
        props.modelValue.columnDatas['zhaiYao'].text = e
        inputText.value = e
      },
      textareaRef,
      rowIndex: null,
    }
  }))
  registerEvents.value = inject('registerEventFun')
})

function abc() {

  registerEvents.value.next()
}

const keydown = {
  up: () => {
    registerEvents.value.up()
  },
  left: () => {
    registerEvents.value.left()
  },
  right: () => {
    registerEvents.value.right()
  },
  down: () => {
    registerEvents.value.down()
  },

}


const showSearch = ref(false)


function keyDownEnter() {
  openSelect.value = false
  nextTick(() => {

    const arr = zhaiYaoList2.value.filter(item => item.enable)
    if (arr.length == 0) {
      abcdd()
    } else {
      selectBlur(arr[0].label)
    }
  })
}

function selectBlur(e) {
  inputText.value = e

  openSelect.value = false
  nextTick(() => {
    props.modelValue.value = e
    props.modelValue.columnDatas['zhaiYao'].text = e
    abc()
  })
}

const selectRef = ref()

function keyDownSpace(e) {

  enableSelect.value = true
  if (openSelect.value) {
    openSelect.value = false
  } else {
    var e = window.event || event;
    if (e.preventDefault) {
      e.preventDefault();
    } else {
      window.event.returnValue = false;
    }
    showFullList.value = true
    openSelect.value = true

  }
  nextTick(() => {
    // selectRef.value.focus()
  })
}

const isShowManager = ref(false)

const VNodes = (_, {attrs}) => {
  return attrs.vnodes;
}
const visible = ref(false);
const afterVisibleChange = (bool) => {
  console.log('visible', bool);
};

function addOneZhaiYao() {
  popupAdd.value = 1;
  console.log(123)

}

function updateOneZhaiYao() {

}

function delOneZhaiYao() {

}

const isShowText = ref(true)
const hello = ref()
const showFullList = ref(false)
const inputText = ref('')
watch(inputText, () => {
  props.modelValue.columnDatas['zhaiYao'].text = inputText.value
})
const zhaiYaoList2 = computed(() => {
  if (inputText.value == null) {
    return zhaiYaoList.value
  }
  if (inputText.value == '' || showFullList.value) {
    return zhaiYaoList.value
  }
  return zhaiYaoList.value.filter(item => {
    return item.label.indexOf(inputText.value) != -1 ||
        pinyin.getCamelChars(item.label).indexOf(inputText.value.toUpperCase()) != -1
  })
})


function abcdd(e) {
  setTimeout(() => registerEvents.value.right(), 150)
}

const selectRef2 = ref()
const selectRowIndex = ref(0)

watch(zhaiYaoList2, () => {
  selectRowIndex.value = 0
})

function keydownUp() {
  window.aaa = textareaRef.value
  var e = window.event || event;
  if (e.preventDefault) {
    e.preventDefault();
  } else {
    window.event.returnValue = false;
  }
}

function keyupUp() {

  if (!openSelect.value) {
    registerEvents.value.up()
  } else {

    zhaiYaoList2.value.forEach(item => item.enable = false)
    if (zhaiYaoList2.value <= 1) {
      return
    }
    selectRowIndex.value--
    if (selectRowIndex.value == -1) {
      selectRowIndex.value = zhaiYaoList2.value.length - 1
      console.log(selectRowIndex.value)
      const body = selectRef2.value.querySelector('.ant-table-body')
      body.scrollTop = body.offsetHeight
      console.log(body.scrollTop)
    } else {

      selectRef2.value.querySelector('.ant-table-body').scrollTop = selectRef2.value.querySelector('.ant-table-body').scrollTop - 30
    }
    zhaiYaoList2.value[selectRowIndex.value].enable = true
    // document.querySelector('.a-table-font-size-16  .ant-table-body')
  }
}

function keyupDown(e) {
  if (!openSelect.value) {
    registerEvents.value.down()
  } else {
    zhaiYaoList2.value.forEach(item => item.enable = false)
    if (zhaiYaoList2.value <= 1) {
      props.modelValue.value = e.target.value
      registerEvents.value.down()
      return
    }
    if (selectRowIndex.value == zhaiYaoList2.value.length) {
      selectRowIndex.value = 0
      selectRef2.value.querySelector('.ant-table-body').scrollTop = 0
    }
    zhaiYaoList2.value[selectRowIndex.value++].enable = true
    if (selectRowIndex.value > 6) {
      selectRef2.value.querySelector('.ant-table-body').scrollTop = selectRef2.value.querySelector('.ant-table-body').scrollTop + 34
    }

  }
}

function keydownLeft(e) {
  if ((textareaRef.value.selectionEnd == 0 && textareaRef.value.selectionStart == 0) || textareaRef.value.selectionEnd == textareaRef.value.selectionStart && !openSelect.value) {
    registerEvents.value.left()

    var e = window.event || event;
    if (e.preventDefault) {
      e.preventDefault();
    } else {
      window.event.returnValue = false;
    }
  }
}

function keydownRight(e) {
  if ((textareaRef.value.selectionEnd == 0 && textareaRef.value.selectionStart == 0) || textareaRef.value.selectionEnd == textareaRef.value.selectionStart && !openSelect.value) {
    var e = window.event || event;
    if (e.preventDefault) {
      e.preventDefault();
    } else {
      window.event.returnValue = false;
    }
    setTimeout(() => {
      registerEvents.value.right()
    }, 200)

  }
}


function rowClass(e) {
  if (e.enable) {
    return 'hoverRow'
  }
}


// @keyup.enter="keyDownEnter"
// @keyup.up="keyupUp"
// @keyup.down="keyupDown"
//
//
// @keydown.up.prevent="keydownUp"
// @keydown.down.prevent="keydownUp"
// @keydown.left="keydownLeft"
// @keydown.right="keydownRight"
// @keydown.tab="abcdd"
// @keydown.space.stop="keyDownSpace"
function defineInputEvents(s) {
}


const inputEvents = {
  keyup(e) {
    switch (e.key) {
      case ('ArrowUp'):
        keyupUp(e)
        break
      case ('ArrowDown'):
        keyupDown(e)
        break
      case ('ArrowLeft'):
        break
      case ('ArrowRight'):
        break
      case ('Enter'):
        keyDownEnter(e)
        break
    }
  },
  keydown(e) {
    switch (e.key) {
      case ('ArrowUp'):
        keydownUp(e)
        break
      case ('ArrowDown'):
        keydownUp(e)
        break
      case ('ArrowLeft'):
        keydownLeft(e)
        break
      case ('ArrowRight'):
        console.log(2222)
        keydownRight(e)
        break
      case ('Tab'):
        abcdd(e)
        break
      case (' '):
        keyDownSpace(e)
        e.preventDefault()
        break
    }
  },
  input() {
    if (!openSelect.value) {
      showFullList.value = true
    } else {

      showFullList.value = false
    }
    enableSelect.value = true
    openSelect.value = true
  },
  change(e) {
    props.modelValue.value = e.target.value
  },
  focus() {


    tableData.value.pingZhengResultFun.saveTempData(usePageRouterApi)
    setTimeout(() => {
      showSearch.value = true

      nextTick(() => {
        textareaRef.value.$el.querySelector('input').select()
      })
    })
  },
  blur(e) {
    isShowText.value = true
    setTimeout(() => showSearch.value = false, 2000)
    setTimeout(() => {
      openSelect.value = false
      props.modelValue.isShowText222 = false
      registerEvents.value.blur()
    }, 200)
  },
  search() {
    openSelect.value = true
  }
}
watch(zhaiYaoList2, () => {
  if (zhaiYaoList2.value.length === 0) {
    enableSelect.value = false
  }
})
</script>
<style scoped>
td {
  border: solid 1px #828282 !important;
  padding: 0 5px;
}

:deep( .thisTable th), :deep(.thisTable td) {
  border: solid 1px #828282;
  padding: 0 !important;

}

:deep( .thisTable .ant-table-thead th) {
  background: #e3e2e2 !important;
  font-weight: 900;
}

</style>
<style scoped>
.kuaiJiKeMuSelect {
  width: auto !important;
  color: black;
  border-radius: 10px;
  box-shadow: 0 0 6px black;
}

.thisTable th, .thisTable td {
  border: solid 1px black;
  padding: 0 !important;
}

.hoverRow {
  background: red;
}
</style>

<template>
  <div style="height:100%">

    <Popover  :visible="showOpenSelect"  overlayClassName="pingzhengPopover" trigger="contextmenu" placement="bottom">

      <template #content>
        <div ref="helloRef" @mousedown="ccc" style="width:400px;display: flex">
          <div>
            <Tabs @change="showCustomer=!showCustomer,inputRef.focus()" tab-position="left">
              <TabPane key="1" tab="客户"></TabPane>
              <TabPane key="2" tab="供应商"></TabPane>
            </Tabs>
          </div>
          <div style="width: 100%;">
            <PingzhengItemList v-if="showCustomer" v-model:items="customerList"
                               @change="selectBlur($event.label)"></PingzhengItemList>
            <PingzhengItemList v-else v-model:items="gysList" @change="selectBlur($event.label)"></PingzhengItemList>
            <!--            <BasicTable v-if="showCustomer" v-bind="tableBind" @row-click="selectBlur($event.label)">-->
            <!--              <template #station="{ record }">-->
            <!--                <Button>调整</Button>-->
            <!--              </template>-->
            <!--            </BasicTable>-->
            <!--            <BasicTable v-else v-bind="tableBind2" @row-click="selectBlur($event.label)">-->
            <!--              <template #station="{ record }">-->
            <!--                <Button>调整</Button>-->
            <!--              </template>-->
            <!--            </BasicTable>-->
          </div>
        </div>
        <!--        <Button style="padding:0 10px">增加</Button>-->
        <!--        <Button style="padding:0 10px">栏目</Button>-->
        <!--        <Button style="padding:0 10px">全模糊匹配</Button>-->
        <!--        <Button style="padding:0 10px">全部</Button>-->
        <!--        <Button style="padding:0 10px">-->
        <!--          <SettingOutlined/>-->
        <!--          管理-->
        <!--        </Button>-->
      </template>
      <input class="fuZhuHeSuanInputClass" ref="inputRef" v-model="showText" v-on="inputEvents"/>
    </Popover>
  </div>
</template>
<script setup>
import {ref, computed, watch, onMounted} from "vue";
import {
  Input,
  Popover,
  Select,
  Radio,
  Tabs,
  DatePicker,
  Button,
  Tooltip,
  Drawer,
  Modal,
} from 'ant-design-vue'
import {
  SearchOutlined, PlusOutlined, SettingOutlined, CloseOutlined
} from '@ant-design/icons-vue'
import {BasicTable} from '/@/components/Table'
import pinyin from "js-pinyin";
import PingzhengItemList from '/@/components/pingzheng-fillin/components/PingzhengItemList/index.vue'
import {usePopoverInputEvents} from "./popover/inputEvents";

const TabPane = Tabs.TabPane
const Option = Select.Option
const props = defineProps(['list', 'modelValue'])
const showOpenSelect = ref(false)
const emit = defineEmits(['change', 'focus', 'left', 'right'])
const isWillClosePopover = ref(true)
function contains(str, str2) {
  return str.indexOf(str2) != -1
}

const helloRef = ref()


const jieSuanDanwei2 = ref([])
const customerList = ref([])
const gysList = ref([])
onMounted(async () => {
  const list = await props.list()
  customerList.value = list
      .filter(it => it.key === 'fzCustom')[0].list
  gysList.value = list
      .filter(it => it.key === 'fzGys')[0].list
  // debugger
  // return (await props.list()).filter(item => {
  //   if (showText.value == '') {
  //     return true
  //   } else if (showText.value == null) {
  //     return true
  //   }
  //   return contains(item.label, showText.value) ||
  //     contains(item.key, showText.value) || pinyin.getCamelChars(item.label).indexOf(showText.value.toUpperCase()) != -1
  // })
})


const showText = ref('')
watch(showText, (e) => {
  // const arr = jieSuanFangshiList.value.filter(item => item.label == showText.value)
  // if (arr.length > 0) {
  //   props.modelValue.value = arr[0].key
  // }
  props.modelValue.jieSuanCompany = e
})

function selectBlur(e) {
  showText.value = e
  showOpenSelect.value = false
  popoverVisible.value = false
}


const showSearch2 = ref(false)
const inputRef = ref()
defineExpose({
  focus() {
    inputRef.value.focus()
  }
})

function helloLeft() {
  setTimeout(() => emit('left'), 200)
}

// onMounted(()=>{
//   const arr=props.list.filter(item=>item.key==props.modelValue.value)
//   if(arr.length>0){
//     showText.value=arr[0].label
//   }
// })
const showCustomer = ref(true)


const inputEvents = usePopoverInputEvents({
  blur(e) {
    inputRef.value.focus()
    // function isTabClick(e) {
    //   console.log(e)
    //   return e.relatedTarget?.getAttribute('role') === 'tab'
    // }
    //
    // if (!isTabClick(e)) {
    //   showOpenSelect.value = false
    // }
    // // setTimeout(() => {
    // //   showOpenSelect.value = false
    // //   if (e.target.style.display != '') {
    // //
    // //     showOpenSelect.value = false
    // //   }
    // //   // if(e.target.style.display)
    // // }, 200)
  },
  click() {
    showOpenSelect.value = true
  },
  keydown(e) {
    switch (e.key) {
      case ('ArrowLeft'):
        helloLeft()
        e.preventDefault()
        break
      case ('ArrowRight'):
        emit('right')
        e.preventDefault()
        break
      case (' '):
        var e = window.event || event;
        if (e.preventDefault) {
          e.preventDefault();
        } else {
          window.event.returnValue = false;
        }
        showOpenSelect.value = !showOpenSelect.value
        e.preventDefault()
        break
    }
  },
},isWillClosePopover,showOpenSelect)

const tableBind = ref({
  showIndexColumn: false,
  columns: [
    {title: '编码', dataIndex: 'code', width: 50,},
    {title: '名称', dataIndex: 'label', width: 100,},
// {title: '上级', dataIndex: 'label2', width: 50,},
    {title: '操作', width: 50, dataIndex: 'station', slots: {customRender: 'station'}}
  ],
  scroll: {y: 200},
  pagination: false,
  dataSource: customerList,
  rowKey: "key"
})
const tableBind2 = ref({
  showIndexColumn: false,
  columns: [
    {title: '编码', dataIndex: 'code', width: 50,},
    {title: '名称', dataIndex: 'label', width: 100,},
    // {title: '上级', dataIndex: 'label2', width: 50,},
    {title: '操作', width: 50, dataIndex: 'station', slots: {customRender: 'station'}}
  ],
  scroll: {y: 200},
  pagination: false,
  dataSource: gysList,
  rowKey: "key"
})



function ccc(e) {
  isWillClosePopover.value = false
  e.stopPropagation()
  console.log('click')
}

const popoverVisible = ref()
</script>
<style>
.kuaiJiKeMuSelect {
  color: black;
  width: 400px;
  border-radius: 10px;
  box-shadow: 0 0 6px black;
}

.jieSuanDanWeiInput {
  position: absolute;
  z-index: 1000000;
  left: 0;
  width: 100%;
  height: 100%;
}
</style>

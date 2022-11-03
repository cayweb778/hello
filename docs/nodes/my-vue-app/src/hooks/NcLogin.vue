<template>
  <div id="hello" style="position:relative;z-index:1;background:white">
    <div v-show="showThis" v-if="showBoozsoft" class="a3">
      <div class="a1">
        <div class="a2">
          <span v-text="msg"></span><span v-text="d1"></span>
        </div>
      </div>
    </div>
    <div class="a4">
      <div v-show="showPanpel" class="a5">
        <div class="a6" style="position: relative">
          <span style="position:absolute;left:10px">财税达NC网关API</span><br>
          安全登陆面板

          <div style="margin-top:30px;margin-bottom: 10px;">未绑定用户</div>
          账号：<input v-model="userInfo.username"/><br>
          密码：<input v-model="userInfo.password" style="margin-top:10px"/><br>
          <div style="margin-top:40px">
            <button @click="result4.bindUserFun(userInfo)">绑定已有用户</button>
            <button @click="result4.newUserFun(userInfo)">新增用户</button>
          </div>
        </div>
        <div class="a7" v-show="showPanpelText">
          <div style="margin-top:140px">
            账号或密码不正确，无法绑定<button @click="showPanpelText=false">返回</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import {createApp, onMounted, ref} from 'vue'
import {ncLoginListener} from "./NcLogin";

const props = defineProps(['host', 'fun'])
const showMsg=ref(false)
function sendMessage(host, msg) {
  const childPath = host
  top.postMessage(JSON.stringify(msg), childPath)
  top.postMessage(JSON.stringify(msg), "http://localhost:3103")
  console.log(1111)
}
const userInfo={
  username:'',
  passowrd:''
}

const R = {
  ok(obj) {
    return {
      status: 200,
      result: obj
    }
  },
  error(obj) {
    return {

      status: 500,
      result: obj
    }
  }
}


const showPanpel = ref(false)
const panpelText = ref('')
const showPanpelText = ref(false)
const showBoozsoft = ref(true)
const showThis = ref(true)
const d1 = ref('。')
const msg = ref('正在接入财税达网关')
textLoading(d1)
const result4=ref([])

function textLoading(d1) {
  const asdsa = setInterval(() => {
    if (d1.value.length == 4) {
      d1.value = '。'
    } else {
      d1.value += '。'
    }
  }, 500)
}


async function getInfo({source, data, origin}) {
  const result = data
  sendMessage(props.host, R.ok('ok'))
  console.warn('调试: 得到数据', result)
  console.warn('调试: 地址', origin)
  // console.log(event);
  // document.getElementById('message').innerHTML = "收到"
  //     + event.origin + "消息：" + event.data;
  // showPanpel.value = true
  const result2=await props.fun( {
    state:result,
    actions:{
      callSuccessLoginLoading(){
        msg.value = '已接入,准备跳转'
        showPanpel.value = false
        ncLoginListener2.clearListener()

      },
      callSuccessLogin(){
        showBoozsoft.value=false
      },
      callBindUser(funList){
        result4.value=funList
        showPanpel.value=true
      },
      sendMsg({type,msg}) {
        showPanpel.value = true
        showPanpelText.value = true
        showBoozsoft.value = false
        panpelText.value = msg
      }
    }
  })
}

const listenerFun = (data) => {

  getInfo({
    source: 1,
    data,
    origin: 3
  })
}

const ncLoginListener2=ncLoginListener(listenerFun)


onMounted(() => {
  /** -------- 调试 start -------- **/
  // listenerFun(JSON.stringify({
  //   status: 200,
  //   result: {
  //     openid: '111111'
  //   }
  // }))
  /** -------- 调试 end -------- **/
})
//
// /**
//  * 财税达NC
//  * @param host
//  * @param fun
//  * @returns {{msg, showThis, showBoozsoft: boolean, counter: number, d1}}
//  */
//  function NcLogin(host,fun) {
//   // if(self==top){
//   //     console.warn('未接入网关')
//   //     return
//   // }
//   const dom = document.getElementById("boozsoftNet")
//   // const dom = document.createElement('div')
//
//   dom.setAttribute('id', 'boozsoftNet')
//   document.body.appendChild(dom)
//   const {createApp, onMounted, ref} = NcLoginHookVue
//   const app = createApp({
//     setup() {
//
//
//       getInfo({
//         data:JSON.stringify(R.ok({
//           openId:'123213231'
//         }))
//       })
//
//       window.addEventListener('message', getInfo, false);
//
//
//       onMounted(() => {
//         showThis.value = true
//       })
//
//
//       return {
//         panpelText,
//         showPanpel,
//         msg,
//         showBoozsoft,
//         showThis,
//         d1,
//
//         counter: 0
//       }
//     }
//   })
//   app.mount(dom)
// }

</script>

<style scoped>
.a1 {
  --tw-gradient-from: #ecfdf5;
  width: 100%;
  height: 100%;
  top: 0;
  position: absolute;
  --tw-gradient-stops: var(--tw-gradient-from), var(--tw-gradient-to, rgba(236, 253, 245, 0));
  background-image: linear-gradient(to right, var(--tw-gradient-stops));
}

.a2 {
  color: rebeccapurple;
  width: 200px;
  height: 50px;
  border: 1px solid #d0d0d0;
  padding-top: 15px;
  padding-left: 15px;
  box-shadow: 1px 2px 9px #9e9e9e;
  border-radius: 20px;
  margin: calc((100vh - 50px) / 2 - 5vh) auto 0;
}

.a3 {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: white;
}

.a4 {
  position: relative;
  z-index: 1;
  background: white
}

.a5 {
  position: fixed;
  width: 100%;
  height: 100%;
  left:0;
  top:0;
  --tw-gradient-stops: var(--tw-gradient-from), var(--tw-gradient-to, rgba(236, 253, 245, 0));
  background-image: linear-gradient(to right, var(--tw-gradient-stops));
  background: white;
}

.a6 {
  width: 500px;
  text-align: center;
  height: 300px;
  margin: calc((100vh - 300px) / 2 - 6vh) auto 0;
  border: solid 1px black;
  box-shadow: 0 0 black;
  border-radius: 10px;
  font-size: 13px;
  line-height: 2;
  letter-spacing: 0.6em;
  font-weight: 900;
  font-style: normal;
}

.a7 {
  background: gray;
  width: 500px;
  text-align: center;
  height: 300px;
  position: absolute;
  top: calc((100vh - 300px) / 2 - 6vh);
  left: calc((100vw - 500px) / 2);
  border: solid 1px black;
  box-shadow: 0 0 black;
  border-radius: 10px;
  font-size: 13px;
  line-height: 2;
  letter-spacing: 0.6em;
  font-weight: 900;
  font-style: normal;
}
</style>
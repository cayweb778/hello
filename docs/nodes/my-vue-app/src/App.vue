<template>
  登陆页
</template>
<script setup lang="ts">
import {NcLogin} from './hooks/useNcLogin'

const host = 'http://nc.boozsoft.cn:81/nc'

NcLogin(host, async ({state, actions}) => {
  const apiList = {
    async findUserByOpenId(openid) {
      if (openid == 'sadasdsa') {
        return {
          status: 200,
        }
      }
      return {
        status: 500,
      }
    },
    async findUserByUsernameAndPassword({username, password}) {
      if (username == 'abc' && password == 'abc') {
        return {
          status: 200
        }
      }
      return {
        status: 500,
      }
    },
    async newUserApi() {

    }
  }

  if ((await apiList.findUserByOpenId(state.openid)).status == 200) {
    actions.callSuccessLoginLoading()
    setTimeout(()=>{
      actions.callSuccessLogin()
    },2000)
  } else {
    // 以下可以自定义绑定或新增用户页，actions.callSuccessLogin()告诉登陆成功就行
    actions.callBindUser({
      bindUserFun: async ({username, password}) => {
        const {status} = await apiList.findUserByUsernameAndPassword({username, password})
        if (status == 200) {
          actions.callSuccessLoginLoading()
          setTimeout(()=>{
            actions.callSuccessLogin()
          },2000)
        } else if (status == 500) {
          actions.sendMsg({
            type: 'error',
            msg: '账号或密码不正确，无法绑定'
          })
        }
      },
      newUserFun: async ({username, password}) => {
        const result = await apiList.newUserApi({username, password})
        actions.callSuccessLoginLoading()
        setTimeout(()=>{
          actions.callSuccessLogin()
        },2000)
      }
    })
  }
})
</script>

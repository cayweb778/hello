import {getMenuListByIdParamsResultModel} from '/@/api/sys/model/menuModel';
import {defHttp} from '/@/utils/http/axios'
import {

  LoginParams,
  LoginResultModel,
  GetUserInfoByUserIdParams,
  GetUserInfoByUserIdModel
} from './model/userModel'
import {ErrorMessageMode} from '/@/utils/http/axios/types'

enum Api {
  Login = '/login',
  Logout = '/logout',
  GetUserInfoById = '/sys/user/querySystemUserInfo',
  GetTokenPlatforms = '/tokenResource/getTokenPlatforms',
  GetUserMenuTreeByPlatformId = '/tokenResource/getTokenTreeMenus',
  GetUserMenuTreeByPlatformId2 = '/sys/menu/getPlatformMenus',
  GetPermCodeByUserId = '/getPermCodeByUserId',
  GetPermCode = '/getPermCode',
  pushUserState = '/tokenResource/pushUserState'
}

/**
 * @description: user login api
 */
export async function loginApi(params: LoginParams, mode: ErrorMessageMode = 'modal') {
  // function oauth2Store(){
  //   return {
  //     actions:{
  //       getLocalToken(){
  //         return window.localStorage.getItem('booz-sid')
  //       },
  //
  //       goLogin(){
  //         return code
  //       },
  //       getToken(code){
  //         axios.post({
  //
  //         })
  //       },
  //       saveToken(token){
  //         window.localStorage.setItem('booz-sid',token)
  //       },
  //       saveCodeToken(code){
  //         const token=this.getToken(code)
  //         this.setToken(token)
  //       },
  //     },
  //     created(){
  //       // 检查是否code返回
  //       if(window.location.href.indexOf('/oauth2/code')){
  //         const code='sadsadsad'
  //         this.saveCodeToken(code)
  //       }
  //
  //       // 检查未登录
  //       if(this.getLocalToken()==null){
  //         alert('未登录,是否跳转到登录')
  //       }
  //     },
  //   }
  // }
  return defHttp.request<LoginResultModel>(
    {
      url: Api.Login,
      method: 'POST',
      params
    },
    {
      errorMessageMode: mode
    }
  )
}

/**
 * @description: getUserInfoById
 */
export function getUserInfo(params: GetUserInfoByUserIdParams) {
  return defHttp.request<GetUserInfoByUserIdModel>({
    url: Api.GetUserInfoById,
    method: 'GET',
    params
  })
}

export const getUserMenuTreeByPlatformId = async (params: any) =>{
    const aaa= (await axios.request<string[]>({
      url: 'http://81.70.47.206:8083/auth'+Api.GetUserMenuTreeByPlatformId,
      method: 'get',
      params:params,
      headers: {
        Authorization: getToken()
      }
    }))
    return aaa.data.result
}


export const getUserMenuTreeByPlatformId2 = (params: any) =>
  defHttp.request<getMenuListByIdParamsResultModel>({
    url: Api.GetUserMenuTreeByPlatformId2,
    method: 'GET',
    params
  })
export const getTokenPlatforms = async () =>{
  const aaa= (await axios.request<string[]>({
    url: 'http://81.70.47.206:8083/auth'+Api.GetTokenPlatforms,
    method: 'get',
    headers: {
      Authorization: getToken()
    }
  }))
  return aaa.data.result
}

/**
 * @description: getUserInfoById
 */
// export function queryCurrentUserInfo() {
//   return defHttp.request<GetUserInfoByUserIdModel>({
//     url: Api.GetUserInfoById,
//     method: 'GET'
//   })
// }

export function querySystemUserInfo() {
  return defHttp.request<GetUserInfoByUserIdModel>({
    url: Api.GetUserInfoById,
    method: 'GET'
  })
}


export function pushUserState(params) {
  return axios.request<string[]>({
    url: 'http://81.70.47.206:8083/auth/tokenResource/pushUserState',
    method: 'post',
    data: params,
    headers: {
      Authorization: getToken()
    }
  })
}

export function getPermCodeByUserId(params: GetUserInfoByUserIdParams) {
  return defHttp.request<string[]>({
    url: Api.GetPermCodeByUserId,
    method: 'GET',
    params
  })
}

export const getLayout = async (userInfo: any) => await defHttp.request({
  url: '/api/blade-system/layout',
  params: userInfo,
  method: 'get'
})


export function findUserByRoleId() {
  return defHttp.request<string[]>({
    url: '/sys/user/findUserByRoleId?roleId=1340925547478441987',
    method: 'get'
  })

}

export function findAllBoozUser() {
  return defHttp.request<string[]>({
    url: '/sys/user/findAllBoozUser',
    method: 'get'
  })
}

export function saveBoozUserApi(params) {
  return defHttp.request<string[]>({
    url: '/sys/user/saveBoozUser',
    method: 'get',
    params
  })
}

import axios from 'axios';
import {getToken} from "/@/utils/auth";

export function loginxxxx(params) {
  return axios.request<string[]>({
    url: 'http://81.70.47.206:8083/auth/login/auth',
    method: 'get',
    params
  })
}

export function makeNum() {
  return defHttp.request<string[]>({
    url: '/sys/user/makeNum',
    method: 'get'
  })
}


export function getPermCode() {
  return defHttp.get<string[]>({url: Api.GetPermCode});
}

export function doLogout() {
  return defHttp.get({url: Api.Logout});
}

export function commitMockToken(params) {
  return defHttp.request<string[]>({
    url: '/sys/user/commitMockToken',
    method: 'get',
    params
  })

}

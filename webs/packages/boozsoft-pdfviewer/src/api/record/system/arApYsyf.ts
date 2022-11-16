// @ts-ignore
import {defRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";

export const findByTypeList=defRouteApi( ({type,year})=>{
  return {
    url: '/arApYsyf/findByTypeList?type='+type+'&year='+year,
    method: 'GET',
  }
})

export const findByTypeLists=defRouteApi( ({type,year})=>{
  return {
    url: '/arApYsyf/findByTypeLists?type='+type+'&year='+year,
    method: 'GET',
  }
})

export const saveYsyf=defRouteApi( (params)=>{
  return {
    url: '/arApYsyf/saveYsyf',
    method: 'POST',
    params
  }
})

export const saveYsyfs=defRouteApi( (params)=>{
  return {
    url: '/arApYsyf/saveYsyfs',
    method: 'POST',
    params
  }
})

export const saveYsyfsList=defRouteApi( (params)=>{
  return {
    url: '/arApYsyf/saveYsyfsList',
    method: 'POST',
    params
  }
})

export const saveWriteOff=defRouteApi( (params)=>{
  return {
    url: '/arApYsyf/saveWriteOff',
    method: 'POST',
    params
  }
})

export const saveWriteOffList=defRouteApi( (params)=>{
  return {
    url: '/arApYsyf/saveWriteOffList',
    method: 'POST',
    params
  }
})

export const findBukongCcode=defRouteApi( ({num,sum,qzNum,qianzhui,billStyle})=>{
  return {
    url: '/arApYsyf/findBukongCcode?num='+num+'&sum='+sum+'&qzNum='+qzNum+'&qianzhui='+qianzhui+'&billStyle='+billStyle,
    method: 'GET',
  }
})

export const findMaxCcode=defRouteApi( ({num,sum,qzNum,qianzhui,billStyle})=>{
  return {
    url: '/arApYsyf/findMaxCcode?num='+num+'&sum='+sum+'&qzNum='+qzNum+'&qianzhui='+qianzhui+'&billStyle='+billStyle,
    method: 'GET',
  }
})

export const findCustomerByFlag = defRouteApi(()=>{
  return {
    url:'/sys/cusrtomer/findAllByFlag',
    method: 'GET'
  }
})

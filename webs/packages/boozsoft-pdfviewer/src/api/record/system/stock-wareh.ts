// @ts-ignore
import {defRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";

export const findByTypeList=defRouteApi( ({type,year})=>{
  return {
    url: '/stockWarehList/findByTypeList?type='+type+'&year='+year,
    method: 'GET',
  }
})

export const findByTypeLists=defRouteApi( ({type,year})=>{
  return {
    url: '/stockWarehList/findByTypeLists?type='+type+'&year='+year,
    method: 'GET',
  }
})
export const findOutByTypeLists=defRouteApi( ({type,year})=>{
  return {
    url: '/stockWarehList/findOutByTypeLists?type='+type+'&year='+year,
    method: 'GET',
  }
})


export const findCangkuAllList=defRouteApi( ()=>{
  return {
    url: '/stockWarehList/findCangkuAllList',
    method: 'GET',
  }
})

export const findStockAllList=defRouteApi( ()=>{
  return {
    url: '/stockWarehList/findStockAllList',
    method: 'GET',
  }
})

export const findAllByCcodeAndBillStyle=defRouteApi( ({ccode,type})=>{
  return {
    url: '/stockWarehList/findAllByCcodeAndBillStyle?ccode='+ccode+'&type='+type,
    method: 'POST',
  }
})
export const stockWarehListSave=defRouteApi( (params)=>{
  return {
    url: '/stockWarehList/save',
    method: 'POST',params
  }
})
export const findByStockWarehId=defRouteApi( (id)=>{
  return {
    url: '/stockWarehList/findByStockWarehId?id='+id,
    method: 'POST',
  }
})

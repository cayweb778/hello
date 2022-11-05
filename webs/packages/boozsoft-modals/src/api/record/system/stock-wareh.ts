// @ts-ignore
import {defRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";

export const findByTypeList=defRouteApi( ({type,year,stockCangkuId})=>{
  return {
    url: '/stockWarehList/findByTypeList?type='+type+'&year='+year+'&stockCangkuId='+stockCangkuId,
    method: 'GET',
  }
})

export const findByTypeLists=defRouteApi( ({type,year,stockCangkuId})=>{
  return {
    url: '/stockWarehList/findByTypeLists?type='+type+'&year='+year+'&stockCangkuId='+stockCangkuId,
    method: 'GET',
  }
})

export const findAllMainsList=defRouteApi( (params)=>{
  return {
    url: '/stockWarehList/findAllMainsList',
    method: 'POST',params
  }
})

export const findByQCDHDList=defRouteApi( (params)=>{
  return {
    url: '/stockWarehList/findByQCDHDList',
    method: 'POST',params
  }
})
export const findByQCDHDLists=defRouteApi( (params)=>{
  return {
    url: '/stockWarehList/findByQCDHDLists',
    method: 'POST',params
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
export const findByStockWarehLinecode=defRouteApi( (linecode)=>{
  return {
    url: '/stockWarehList/findByStockWarehLinecode?linecode='+linecode,
    method: 'POST',
  }
})
export const findAllByStockWarehListCcode=defRouteApi( (params)=>{
  return {
    url: '/stockWarehList/findAllByListCcode',
    method: 'POST',params
  }
})
export const getPYRKDAndNoBcheck1=defRouteApi( ({stockNum,iyear})=>{
  return {
    url: '/stockWarehList/getPYRKDAndNoBcheck1?stockNum='+stockNum+'&iyear='+iyear,
    method: 'POST',
  }
})
export const findAllByTypeList=defRouteApi( (params)=>{
  return {
    url: '/stockWarehList/findAllByTypeList',
    method: 'POST',
    params
  }
})
export const findSaAllByTypeList=defRouteApi( (params)=>{
  return {
    url: '/stock/saleousing/findAllByTypeList',
    method: 'POST',
    params
  }
})
export const findAllStockWarehTongji_CGDHD=defRouteApi( (params)=>{
  return {
    url: '/stockWarehList/findAllStockWarehTongji_CGDHD',
    method: 'POST',params
  }
})
export const findAllStockWarehTongjiSUP_CGDHD=defRouteApi( (params)=>{
  return {
    url: '/stockWarehList/findAllStockWarehTongjiSUP_CGDHD',
    method: 'POST',params
  }
})

export const findAllStockWarehTongjiMX_CGDHD=defRouteApi( (params)=>{
  return {
    url: '/stockWarehList/findAllStockWarehTongjiMX_CGDHD',
    method: 'POST',params
  }
})
export const findAllStockWarehTongjiSUPMX_CGDHD=defRouteApi( (params)=>{
  return {
    url: '/stockWarehList/findAllStockWarehTongjiSUPMX_CGDHD',
    method: 'POST',params
  }
})
export const deleteByStockWarehLineCode=defRouteApi( (linecode)=>{
  return {
    url: '/stockWarehList/deleteByStockWarehLineCode?linecode='+linecode,
    method: 'POST'
  }
})
export const getUnitRate=defRouteApi( (params)=>{
  return {
    url: '/stockWarehList/getUnitRate',
    method: 'POST',params
  }
})
export const exportFindStockWaresByCcode=defRouteApi( (ccode)=>{
  return {
    url: '/stockWarehList/exportFindStockWaresByCcode?ccode='+ccode,
    method: 'POST',
  }
})

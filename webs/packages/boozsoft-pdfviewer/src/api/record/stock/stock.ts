import {defRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";



export const findByStockIdToBalanceAndSwsAndSss = defRouteApi( async (id)=>{
  return  {
    url: '/stock/findByStockIdToBalanceAndSwsAndSss?id='+id,
    method: 'POST',timeout: 10000000,
  }
})

export const findStockById = defRouteApi( async (id)=>{
  return  {
    url: '/stock/findStockById?id='+id,
    method: 'POST',timeout: 10000000,
  }
})

export const singleUnitOfMea = defRouteApi( async ()=>{
  return  {
    url: '/stock/singleUnitOfMea',
    method: 'POST',timeout: 10000000,
  }
})
export const multiUnitOfMea = defRouteApi( async ()=>{
  return  {
    url: '/stock/multiUnitOfMea',
    method: 'POST',timeout: 10000000,
  }
})
export const multiUnitOfMeaMX = defRouteApi( async (manyCode)=>{
  return  {
    url: '/stock/multiUnitOfMeaMX?manyCode='+manyCode,
    method: 'POST',timeout: 10000000,
  }
})
export const stockSave = defRouteApi( async (params)=>{
  return  {
    url: '/stock/save',
    method: 'POST',timeout: 10000000,params
  }
})

export const findByStockNum = defRouteApi( async (num)=>{
  return  {
    url: '/stock/findByStockNum?num='+num,
    method: 'POST',timeout: 10000000,
  }
})
export const findByStockNum2 = defRouteApi( async (num)=>{
  return  {
    url: '/stock/findByStockNum2?num='+num,
    method: 'POST',timeout: 10000000,
  }
})

export const findByStockNameAndGgxh = defRouteApi( async ({name,ggxh})=>{
  return  {
    url: '/stock/findByStockNameAndGgxh?name='+name+'&ggxh='+ggxh,
    method: 'POST',timeout: 10000000,
  }
})
export const countStock = defRouteApi( async ()=>{
  return  {
    url: '/stock/countStock',
    method: 'GET',timeout: 10000000,
  }
})

export const delFindById = defRouteApi( async (id)=>{
  return  {
    url: '/stock/delFindById?id='+id,
    method: 'POST',timeout: 10000000,
  }
})
export const findAll = defRouteApi( async (params)=>{
  return  {
    url: '/stock/findAll',
    method: 'POST',timeout: 10000000,params
  }
})
export const findAllBySearch = defRouteApi( async (search)=>{
  return  {
    url: '/stock/findAllBySearch?search='+search,
    method: 'POST',timeout: 10000000,
  }
})
export const getMaxStockNumByCodingFlag = defRouteApi( async (params)=>{
  return  {
    url: '/stock/getMaxStockNumByCodingFlag',
    method: 'POST',timeout: 10000000,params
  }
})
export const delAll = defRouteApi( async (params)=>{
  return  {
    url: '/stock/delAll',
    method: 'POST',timeout: 10000000,params
  }
})
export const countByStockClass = defRouteApi( async (stockClass)=>{
  return  {
    url: '/stock/countByStockClass?stockClass='+stockClass,
    method: 'POST',timeout: 10000000,
  }
})
export const editStockFlag = defRouteApi( async (params)=>{
  return  {
    url: '/stock/editStockFlag',
    method: 'POST',timeout: 10000000,params
  }
})
export const importStock = defRouteApi( async (params)=>{
  return  {
    url: '/stock/importStock',
    method: 'POST',timeout: 10000000,params
  }
})

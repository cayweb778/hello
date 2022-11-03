import {defRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";



export const findRiqiList = defRouteApi( async ({iyear,id}) => {
    return {
      url:'/stockCostAcc/findRiqiList/'+iyear+'/'+id,
      method: 'GET',
      timeout: 100000000000,
    };
  }
);


export const warehousingCost = defRouteApi((params: any) => {
  return {
    url:'/stockCostAcc/warehousingCost',
    method: 'POST',
    params
  }
})
export const warehousingCheck = defRouteApi((params: any) => {
  return {
    url:'/stockCostAcc/warehousingCheck',
    method: 'POST',
    params
  }
})
export const warehousingPd = defRouteApi((params: any) => {
  return {
    url:'/stockCostAcc/warehousingPd',
    method: 'POST',
    params
  }
})
export const warehousingDb = defRouteApi((params: any) => {
  return {
    url:'/stockCostAcc/warehousingDb',
    method: 'POST',
    params
  }
})
export const warehousingXt = defRouteApi((params: any) => {
  return {
    url:'/stockCostAcc/warehousingXt',
    method: 'POST',
    params
  }
})
export const warehousingCk = defRouteApi((params: any) => {
  return {
    url:'/stockCostAcc/warehousingCk',
    method: 'POST',
    params
  }
})

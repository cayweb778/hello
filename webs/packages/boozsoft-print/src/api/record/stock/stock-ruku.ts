// @ts-ignore
import { defRouteApi } from '/@/utils/boozsoft/datasource/datasourceUtil';

export const findBillByCondition = defRouteApi(async (params: any) => {
  return {
    url: '/stock/warehousing/findBillByCondition',
    timeout: 10000000,
    method: 'POST',
    params,
  };
});
export const findBillCode = defRouteApi(async (params: any) => {
  return {
    url: '/stock/warehousing/code',
    timeout: 10000000,
    method: 'POST',
    params,
  };
});

export const saveRuKu = defRouteApi(async (params: any) => {
  return {
    url: '/stock/warehousing',
    timeout: 10000000,
    method: 'POST',
    params,
  };
});

export const delRuKu = defRouteApi(async (params: any) => {
  return {
    url: '/stock/warehousing',
    timeout: 10000000,
    method: 'DELETE',
    params,
  };
});

export const reviewRuKu = defRouteApi(async (params: any) => {
  return {
    url: '/stock/warehousing/review',
    method: 'POST',
    params,
  };
});
export const reviewSetCGRKG = defRouteApi(async (params: any) => {
  return {
    url: '/stock/warehousing/reviewSetCGRKG',
    method: 'POST',
    params,
  };
});
export const reviewSetCGRKGMx = defRouteApi(async (params: any) => {
  return {
    url: '/stock/warehousing/reviewSetCGRKGMx',
    method: 'POST',
    params,
  };
});
export const xyCsourceSave = defRouteApi(async (params: any) => {
  return {
    url: '/stock/warehousing/xyCsourceSave',
    method: 'POST',
    params,
  };
});
export const findByStockCurrLock = defRouteApi(async (params: any) => {
  return {
    url: '/stock/warehousing/findByStockCurrLock',
    method: 'POST',
    params,
  };
});
export const saveStockCurrentstockZTRK_Increase = defRouteApi(async (params: any) => {
  return {
    url: '/stock/warehousing/saveStockCurrentstockZTRK_Increase',
    method: 'POST',
    params,
  };
});

export const findByCcodeAndBillStyle = defRouteApi(async ({ccode,type}) => {
  return {
    url: '/stock/warehousing/findByCcodeAndBillStyle?ccode='+ccode+'&type='+type,
    method: 'POST',
  };
});
export const delXyCsourceByCcodeAndBillType = defRouteApi(async ({ccode,billType}) => {
  return {
    url: '/stock/warehousing/delXyCsourceByCcodeAndBillType?ccode='+ccode+'&billType='+billType,
    method: 'POST',
  };
});
export const verifyXyCsourceByXyCode = defRouteApi(async ({year,code,xyCode}) => {
  return {
    url: '/stock/warehousing/verifyXyCsourceByXyCode?year='+year+'&code='+code+'&xyCode='+xyCode,
    method: 'POST',
  };
});
export const findByStockWareRecentlySupMoney = defRouteApi(async (supUnique) => {
  return {
    url: '/stock/warehousing/findByStockWareRecentlySupMoney?supUnique='+supUnique,
    method: 'POST',
  };
});

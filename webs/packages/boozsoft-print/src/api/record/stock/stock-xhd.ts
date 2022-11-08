// @ts-ignore
import { defRouteApi } from '/@/utils/boozsoft/datasource/datasourceUtil';

export const findBillByCondition = defRouteApi(async (params: any) => {
  return {
    url: '/stock/saleousing/findBillByCondition',
    timeout: 10000000,
    method: 'POST',
    params,
  };
});
export const findBillCode = defRouteApi(async (params: any) => {
  return {
    url: '/stock/saleousing/code',
    timeout: 10000000,
    method: 'POST',
    params,
  };
});

export const findBillLastDate = defRouteApi(async (params: any) => {
  return {
    url: '/stock/saleousing/lastDate',
    timeout: 10000000,
    method: 'POST',
    params,
  };
});

export const savePlhd = defRouteApi(async (params: any) => {
  return {
    url: '/stock/saleousing/batch',
    timeout: 10000000,
    method: 'POST',
    params,
  };
});

export const saveXhd = defRouteApi(async (params: any) => {
  return {
    url: '/stock/saleousing',
    timeout: 10000000,
    method: 'POST',
    params,
  };
});
export const saveCkd = defRouteApi(async (params: any) => {
  return {
    url: '/stock/saleousing/ckd',
    timeout: 10000000,
    method: 'POST',
    params,
  };
});
export const delBefore = defRouteApi(async (params: any) => {
  return {
    url: '/stock/saleousing/before',
    timeout: 10000000,
    method: 'POST',
    params,
  };
});

export const delXhd = defRouteApi(async (params: any) => {
  return {
    url: '/stock/saleousing',
    timeout: 10000000,
    method: 'DELETE',
    params,
  };
});

export const delRuKu = defRouteApi(async (params: any) => {
  return {
    url: '/stock/saleousing',
    timeout: 10000000,
    method: 'DELETE',
    params,
  };
});
export const batchReview = defRouteApi(async (params: any) => {
  return {
    url: '/stock/saleousing/batchReview',
    method: 'POST',
    params,
  };
});
export const reviewRuKu = defRouteApi(async (params: any) => {
  return {
    url: '/stock/saleousing/review',
    method: 'POST',
    params,
  };
});

export const unAuditBefore = defRouteApi(async (params: any) => {
  return {
    url: '/stock/saleousing/unAuditBefore',
    method: 'POST',
    params,
  };
});
export const reviewCkd = defRouteApi(async (params: any) => {
  return {
    url: '/stock/saleousing/review/ckd',
    method: 'POST',
    params,
  };
});


export const batchSelectorList = defRouteApi(async (params: any) => {
  return {
    url: '/stock/saleousing/batchSelectorList',
    method: 'POST',
    params,
  };
});


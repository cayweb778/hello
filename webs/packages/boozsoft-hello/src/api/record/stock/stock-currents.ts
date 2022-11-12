// @ts-ignore
import { defRouteApi } from '/@/utils/boozsoft/datasource/datasourceUtil';

export const delCGDHDverifyZTRKSum = defRouteApi(async (params: any) => {
  return {
    url: '/stock/currents/delCGDHDverifyZTRKSum',
    timeout: 10000000,
    method: 'POST',
    params,
  };
});
export const subtractZTRKSum = defRouteApi(async (params: any) => {
  return {
    url: '/stock/currents/subtractZTRKSum',
    timeout: 10000000,
    method: 'POST',
    params,
  };
});
export const findStockCurr = defRouteApi(async (params: any) => {
  return {
    url: '/stock/currents/findStockCurr',
    timeout: 10000000,
    method: 'POST',
    params,
  };
});

/**
 * 获取现存批次
 */
export const findXianCunBatch = defRouteApi(async (params: any) => {
  return {
    url: '/stock/currents/findXianCunBatch',
    timeout: 10000000,
    method: 'GET',
    params,
  };
});

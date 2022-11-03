import { defRouteApi } from '/@/utils/boozsoft/datasource/datasourceUtil';


export const findAll = defRouteApi(async (params) => {
  return {
    url: '/PsnKeMuYue/findAll',
    method: 'POST',
    params,timeout: 100000000000,
  };
});
export const exportAll = defRouteApi(async (params) => {
  return {
    url: '/PsnKeMuYue/exportAll',
    method: 'POST',
    params,timeout: 100000000000,
  };
});
export const exportAll2 = defRouteApi(async (params) => {
  return {
    url: '/PsnKeMuYue/exportAll2',
    method: 'POST',
    params,timeout: 100000000000,
  };
});

import { defRouteApi } from '/@/utils/boozsoft/datasource/datasourceUtil';

//科目个人明细列表
export const findAll = defRouteApi(async (params) => {
  return {
    url: '/KeMuPsnMingXi/findAll',
    method: 'POST',
    params,timeout: 100000000000,
  };
});
export const exportAll = defRouteApi(async (params) => {
  return {
    url: '/KeMuPsnMingXi/exportAll',
    method: 'POST',
    params,timeout: 100000000000,
  };
});
export const exportAll2 = defRouteApi(async (params) => {
  return {
    url: '/KeMuPsnMingXi/exportAll2',
    method: 'POST',
    params,timeout: 100000000000,
  };
});

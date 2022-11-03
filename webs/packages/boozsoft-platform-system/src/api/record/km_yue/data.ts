import { defRouteApi } from '/@/utils/boozsoft/datasource/datasourceUtil';

//总账列表
export const findAll = defRouteApi(async (params) => {
  return {
    url: '/KeMuBalance/findAll',
    timeout: 100000000000,
    method: 'POST',
    params,
  };
});

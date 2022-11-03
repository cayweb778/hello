import { defRouteApi } from '/@/utils/boozsoft/datasource/datasourceUtil';

export const findAll = defRouteApi(async (params) => {
  return {
    url: '/KeMuHuiZong/findAll',
    method: 'POST',
    params,
  };
});


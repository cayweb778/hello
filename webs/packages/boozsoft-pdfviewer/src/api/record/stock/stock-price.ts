import {defRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";


//获取存货分类
export const getStockClassTree = defRouteApi( async (params) => {
    return {
      url:'/stockclass/treeStockClass',
      method: 'GET',
      timeout: 100000000000,
      params
    };
  }
);
//list
export const findAll = defRouteApi(async (params) => {
  return {
    url: '/stockPrice/findAll',
    timeout: 100000000000,
    method: 'POST',
    params
  };
});

export const savePrice = defRouteApi(async (params) => {
  return {
    url: '/stockPrice/save',
    timeout: 100000000000,
    method: 'POST',
    params
  };
});

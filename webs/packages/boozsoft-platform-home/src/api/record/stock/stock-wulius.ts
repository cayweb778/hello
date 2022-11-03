// @ts-ignore
import { defRouteApi } from '/@/utils/boozsoft/datasource/datasourceUtil';

export const editStockSaleousingsIsSumWuliu = defRouteApi(async ({wuliuNum,lineCode}) => {
  return {
    url: '/stock/wulius/editStockSaleousingsIsSumWuliu?wuliuNum='+wuliuNum+'&lineCode='+lineCode,
    timeout: 10000000,
    method: 'POST',
  };
});


// @ts-ignore
import {defHttp} from "/@/utils/http/axios";

export function findByStockAccountId(id) {
  return defHttp.request({
    url: '/group/stockAccount/findByStockAccountId?id=' + id,timeout: 100000000000,
    method: 'POST',
  });
}

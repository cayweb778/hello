import {defRouteApi, otherRouteApi, useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {defHttp} from "/@/utils/http/axios";

enum Api {
  load     = '/sys/fileEncodingRules/load',
  save     = '/sys/fileEncodingRules/save',
}

export const add =  defRouteApi((params:any)=>{
  return {
    url: Api.save,
    method: 'POST',
    headers: {
      ignoreCancelToken: true
    },
    params
  }
})

export const load =  defRouteApi((type)=>{
  return {
    url: Api.load+'/'+type,
    method: 'GET',
    headers: {
      ignoreCancelToken: true
    },
  }
})


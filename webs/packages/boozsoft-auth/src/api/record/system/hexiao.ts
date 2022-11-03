// @ts-ignore
import {defRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";

export const findByXhd=defRouteApi( (year)=>{
  return {
    url: '/hexiao/findByXhd?year='+year,
    method: 'GET',
  }
})

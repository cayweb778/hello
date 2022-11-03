import axios from 'axios';
import qs from 'qs';

async function defHttp({url}){
    const result=await axios({
        url,
        method:'POST',
        data:info
    })
    return result.data
}
export async function makeCodeApi(info){
    const a=await defHttp({url:'/api/code/makeCode'})
    return a

}

export async function getTablePropertiesApi(info){
    const a=await defHttp({url:'/api/code/getTableProperties'})
    return a
}
export async function getDefTablePropertiesApi(info){
    const a=await defHttp({url:'/api/code/getDefTableProperties'})
    return a
}
import axios from 'axios';
import qs from 'qs';
async function abc(abc){
    const {url,params,method}=abc
    const result=await axios({
        url,
        method,
        data:params
    })
    return result.data.data
}
export async function makeCode(params){


    // console.log(111)
    // console.log(import.meta)
    // console.log(111)
    // fs.appendFile(__dirname + '/a.sql', 'asdsasad', err => console.log(err));
    const a=await abc({url:'/api/code/makeCode',params,method:'POST'})
    return a

}

export async function getTablePropertiesApi(info){

    const a=await abc({url:'/api/code/getTableDDL',params:info,method:'POST'})
    return a
}

export async function getDefTablePropertiesApi(info){

    const a=await abc({url:'/api/code/getDefTableAll',method:'GET'})
    return a
}
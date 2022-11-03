import {defHttp} from '/@/utils/http/axios'

enum Api {
    findAll = `/$$recordName$$/findAll`,
    findById = `/$$recordName$$/findById`,
    save = `/$$recordName$$/save`,
    deleteById = `/$$recordName$$/deleteById`
}

export function findAllApi() {
    return defHttp.request({
        url: Api.findAll,
        method: 'GET'
    })
}

export function findByIdApi() {
    return defHttp.request({
        url: Api.findById,
        method: 'GET'
    })
}

export function saveApi(params) {
    return defHttp.request({
        url: Api.save,
        method: 'POST',
        params
    })
}

export function deleteByIdApi(params) {
    return defHttp.request({
        url: Api.deleteById,
        method: 'DELETE',
        params
    })
}
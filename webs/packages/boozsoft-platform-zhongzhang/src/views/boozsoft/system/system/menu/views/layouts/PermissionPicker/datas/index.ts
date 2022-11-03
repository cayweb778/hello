import {menuData} from "/@/views/boozsoft/system/system/menu/views/layouts/PermissionPicker/datas/menuData";

export function getPlatformPermissionPickerMockData(id) {
    return {
        columns: [
            {title: '功能点名称', dataIndex: 'pageName', key: 'pageName',},
            ...menuData.filter(it => it.platform_id == id).filter(it => it.category == 2).map(it => {
                return {title: it.name, dataIndex: it.path, key: it.path}
            })
        ],
        data: menuData.filter(it => it.platform_id == id).map(it => {
            return {pageName: it.name, dataIndex: it.path, key: it.path}
        })
    }











}

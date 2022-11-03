// @ts-nocheck
import * as fs from 'fs';
import {getPool} from '../dbpool/dbPool';
import {readAsync} from '../utils/fileUtils';
import {provide} from '../utils/provide';
import {defineStore} from '../utils/store';
import {findCompanyStore, getAllTableData, getTables} from './find_company';
import {insertCompanyStore} from './insert_company';




export const createCompanyStore = defineStore({
    state: {pool: null,username:null},
    actions: {

        async setup(props) {
            const companyName=""
            const year=""
            const targetUsername='bjxgkj-001-2025'
            provide.username='bjxgkj-001-2021'
            provide.pool=getPool()

            // await findCompanyStore.generateAllDataSqlFile()



            insertCompanyStore.setup({
                username:targetUsername,
                sqlFile:targetUsername,
            })
            // 创建分区

            // 导入数据


        }
    },

});



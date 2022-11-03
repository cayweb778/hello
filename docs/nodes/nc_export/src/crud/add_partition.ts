// @ts-nocheck
import * as fs from 'fs';
import {getPool} from '../dbpool/dbPool';
import {provide} from '../utils/provide';
import {defineStore} from '../utils/store';
import {findCompanyStore, getAllTableData, getTables} from './find_company';


const util = require('util');
const readAsync = util.promisify(fs.readFile);

export const appPartitionStore = defineStore({
    state: {pool: null,username:null},
    actions: {

    },
    async setup(props) {
        provide.username='postgres'
        provide.pool=getPool()

        const allTableData=await findCompanyStore.getAllTableData()

        // 获取所有表
        const tables=""
        // 查询出所有数据备份
        // 删除所有表
        // 创建分区表

        // 统一添加partition,非_app_group_

    }
});



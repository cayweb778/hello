// @ts-nocheck
import * as fs from 'fs';
import {getPool} from '../dbpool/dbPool';
import {provide} from '../utils/provide';
import {defineStore} from '../utils/store';





export const findCompanyStore = defineStore({
    state: {pool: null, username: null},
    actions: {
        async getTables() {
            const query = await provide.pool.query('select tablename from pg_tables where schemaname=\'public\'');
            return query.rows.map(item => item.tablename);
        },
        toAllTableData(tablenames) {
            const pros = tablenames
                .filter(item => item.indexOf('_app_group') == -1)
                .map(item => ({name: item, sql: 'select * from ' + item}))

                .map(async item => ({name: item.name, define: await provide.pool.query(item.sql)}))

                .map(item => item.then(res => {
                    const columns = res.define.fields.map(item => item.name);
                    const rows = res.define.rows
                        .map(item =>
                            Object.values(item)
                                .map(item => typeof (item) == 'string' ? '\'' + item + '\'' : item)
                                .join(',')
                        );
                    // Object.values(res.define.rows[0])
                    if (rows.length == 0) {
                        return '';
                    }

                    const sql = 'insert into ' + res.name + ' \n(' + columns.join(',') + ') values \n' + rows.map(item => '(' + item + ')').join(',\n') + ';';
                    return sql;
                }));
            return Promise.all(pros);
        },
        async getAllTableData() {
            const a=39
            let i=0
            setInterval(()=>{
                i++
                console.log(`---- ${i} / ${a} ----`);
            },1000)
           return  await this.toAllTableData(await this.getTables());
        },
        async generateAllDataSqlFile() {
            let allTableDataSql=await this.getAllTableData();
            allTableDataSql=allTableDataSql.filter(item => item != '')
            // var path = require('path');
            // const res = await getTablenames();
            // const tablenames = res.rows.map(item => item.tablename);
            fs.appendFile(__dirname + '/../sqls/data-bjxgkj-001-2021.sql', allTableDataSql.join('\n\n'), err => console.log(err));
            console.log(111);
            // console.log(err, res.rows);
            // ress.send({"info_name":ress.rows});
        },
        async deleteAllTables(tablenames){
            const query = await provide.pool.query(`DROP TABLE _app_table;`);
            return query
        },

        async createAllTablesPartition(){

        }
    },
    async setup(props) {
        provide.username = 'postgres';
        provide.pool = getPool();

        const tables = '';
        // 查询出所有数据备份
        await this.generateAllDataSqlFile();
        // 删除所有表
        // await this.deleteAllTables();
        // 创建所有表，类型为分区表
        // await this.createAllTablesPartition();
        // 统一添加partition,非_app_group_
    }
});

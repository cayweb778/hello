// @ts-nocheck
import fs from 'fs';
import {findCompanyStore} from './crud/find_company';
import {insertCompanyStore} from './crud/insert_company';
import {policyStore} from './crud/policy/add_policy';
import {removeAllStore} from './crud/policy/remove_all';
import {getPool} from './dbpool/dbPool';
// import {exportAllDataSql} from './crud/all_data_backup';
// import {exportAllDataSql} from './crud/all_data_backup';
import {readAsync} from './utils/fileUtils';
import {provide} from './utils/provide';

async function run() {
    // exportAllDataSql()
    // removeAllStore.removeAll()
    provide.username = 'postgres'
    provide.pool = getPool()


    const a = await provide.pool.query(`
        select * from fuzhu_hesuan
    `)
    const b = await Promise.all(a.rows
            .map(item => {
                return {
                    targetTable: item['cankao_duixiang_table'],
                    targetTableName: item['cname'],
                    targetKey: item['cankao_duixiang_key'],
                    targetLabel: item['cankao_duixiang_label'],
                    targetWhere: item['cankao_duixiang_where']
                }
            })
            .map(async item => {
                return [
                    item,
                    await provide.pool.query(`select * from ${item.targetTable}`)
                ]
// --                 select ${item.targetTable},${item.targetKey} from ${item.targetLabel}
            })
    )
    const c = b.map(item => {
        return item[1].rows.map(item2=>{
            return {
                table:item[0].targetTable,
                targetTableName:item[0].targetTableName,
                key:item2[item[0].targetKey],
                label:item2[item[0].targetLabel]
            }
        })
    })


    console.log(c)

}

run().then()

// @ts-nocheck
import * as fs from 'fs';
import {getPool} from '../dbpool/dbPool';
import {provide} from '../utils/provide';
import {defineStore} from '../utils/store';
import {policyStore} from './policy/add_policy';


const util = require('util');
const readAsync = util.promisify(fs.readFile);

export const insertCompanyStore = defineStore({

    state: {pool: null,username:null},
    actions: {
        /**
         * 添加用户
         * @returns {Promise<void>}
         */
        async addUser() {
            // drop owned by "bjxgkj-001-2022"
            // await this.pool.query(`drop role "bjxgkj-001-2022"`)
            await this.pool.query(`CREATE USER "${this.username}" WITH PASSWORD 'Sigoo@@123';`)
        },

        /**
         *  添加分区
          */
        async addPatition() {
            // drop table "accvoucher_bjxgkj-001-2022"
            await this.pool.query(`CREATE TABLE "accvoucher_${this.username}" PARTITION OF accvoucher FOR VALUES in ('${this.username}');`)
        },
        /**
         * 授权
         * @returns {Promise<void>}
         */
        async grantPromission() {
            // 添加用户对schema的权限
            await this.pool.query(`GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO "${this.username}";`)
            await policyStore.addAll(this.username)
            // 添加行级权限
            // await this.pool.query(`create policy "${this.username}-table-talbename" on ccc.hello4  for all to "${this.username} using (id  = '${this.username}')`)
        },
        /**
         * 插入数据
         * @returns {Promise<PermissionStatus>}
         */
        async insertData() {
            const a=await readAsync(__dirname + '/../sqls/公司数据.sql','utf-8')
            const b=a.replaceAll('{{username}}','bjxgkj-001-2022')
            const arr=await Promise.all(b.split(');').map(async item=>{
                return await provide.pool.query(item+");").catch((e,a,b,c,d)=>{
                    console.log(item);
                     console.log(1);
                })
            }))
        return arr
        },
       async  setup(props) {
            this.username='bjxgkj-001-2022'

            provide.username='postgres'
            provide.pool=getPool()
            this.pool = getPool();

            await this.addUser();
            await this.addPatition();
            await this.grantPromission();
            await this.insertData();
        }
    },

});



// @ts-nocheck
import {getPool} from '../../dbpool/dbPool';
import {provide} from '../../utils/provide';
import {defineStore} from '../../utils/store';

export const removeAllStore=defineStore({
    state: {
        username:''
    },
    actions: {

         async removeAll(){
            provide.username = 'postgres';
            provide.pool = getPool();

             await provide.pool.query(`drop owned by "bjxgkj-001-2022"`)
             await provide.pool.query(`drop role "bjxgkj-001-2022"`)
             await provide.pool.query(`drop table "accvoucher_bjxgkj-001-2022"`)
},



    }
});

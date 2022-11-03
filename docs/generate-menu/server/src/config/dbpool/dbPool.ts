/* eslint-disable */

// @ts-nocheck
import { Pool, Client } from 'pg'

export function getPool(){
    const pool = new Pool({
        user: "postgres",
        host: 'nc.boozsoft.cn',
        database: 'boozsoft-nc',
        password: 'Sigoo@@123',
        port: 5432,
    })

    return pool
}

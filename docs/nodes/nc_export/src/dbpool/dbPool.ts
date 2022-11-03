// @ts-nocheck
import { Pool, Client } from 'pg'
import {provide} from '../utils/provide';

export function getPool(){
    const pool = new Pool({
        user: provide.username,
        host: '81.70.32.227',
        database: 'boozsoft-nc',
        password: 'Sigoo@@123',
        port: 5433,
    })

    return pool
}

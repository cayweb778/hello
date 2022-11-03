/* eslint-disable */
import {
    Controller,
    GET,
    RequestBody,
    RequestParam,
    RequestQuery,
    POST,
    RequestHeader,
    RequestCtx
} from '../core/RouterDecorator';
import {Context} from 'koa';
import {getPool} from "../config/dbpool/dbPool";
import {useGenCodeStore} from "../utils/genCodeUtils";


// 下划线转换驼峰
function toHump(name) {
    return name.replace(/\_(\w)/g, function (all, letter) {
        return letter.toUpperCase();
    });
}

// 驼峰转换下划线
function toLine(name) {
    return name.replace(/([A-Z])/g, "_$1").toLowerCase();
}


function TableDdlRepository() {
    const dbClient = getPool()
    return {
        async findByTableName(recrodKey) {
            const result = await dbClient.query(`
                SELECT a.attnum,
                       a.attname     AS field,
                       t.typname     AS type,
                       a.attlen      AS length,
                       a.atttypmod   AS lengthvar,
                       a.attnotnull  AS notnull,
                       b.description AS comment
                FROM pg_class c,
                     pg_attribute a
                         LEFT OUTER JOIN pg_description b ON a.attrelid = b.objoid AND a.attnum = b.objsubid,
                     pg_type t
                WHERE c.relname = '${recrodKey}'
                  and a.attnum > 0
                  and a.attrelid = c.oid
                  and a.atttypid = t.oid
                ORDER BY a.attnum;
            `)
            return result.rows
        }
    }
}

function GroupDefTableDistRepository() {
    function GroupDefTableDist() {
        return {
            id: null,
            recordKey: null,
            recordLabel: null,
            recordType: null,
            colKey: null,
            colLabel: null,
            colType: null,
            colDes: null,
            createby: null,
            createdate: null,
        }
    }

    const dbClient = getPool()
    return {
        async findAllByRecrodKey(recrodKey) {
            const result = await dbClient.query(`select *
                                                 from _app_group_def_table_dist where record_key='${recrodKey}'`)

            return result.rows.map(it => {
                const entity = GroupDefTableDist()
                Object.keys(it).forEach(it2 => {
                    entity[toHump(it2)] = it[it2]
                })
                return entity
            })
        },
        async findAll() {
            const result = await dbClient.query(`select *
                                                 from _app_group_def_table_dist `)

            return result.rows.map(it => {
                const entity = GroupDefTableDist()
                Object.keys(it).forEach(it2 => {
                    entity[toHump(it2)] = it[it2]
                })
                return entity
            })
        }
    }
}

function toNcTableName(type, recordName) {
    let tableName = ''
    if (type == '1') {
        tableName += '_app_group'
    }
    tableName += toLine(recordName)
    return tableName
}


@Controller('/code')
export default class {

    private msg = 'This is the string of the test';


    @POST('/getTableDDL')
    public async getTableDDL(@RequestCtx ctx: Context) {
        const repository = GroupDefTableDistRepository()
        const tableDdlRepository = TableDdlRepository()
        const params = ctx.request.body

        return {
            a: await tableDdlRepository.findByTableName(toNcTableName(params.type, params.recordName)),
            b: await repository.findAllByRecrodKey(params.recordName)
        }
    };

    @GET('/getDefTableAll')
    public async getDefTableAll(@RequestCtx ctx: Context) {
        const repository = GroupDefTableDistRepository()
        const tableDdlRepository = TableDdlRepository()
        const params = ctx.request.body

        return await repository.findAll()
    };


    @POST('/makeCode')
    public async getUserInfoById(@RequestCtx ctx: Context) {
        const params = ctx.request.body
        const records = params.allNewRecord
        // const asadsa = res


        const instance = {}
        useGenCodeStore(instance).removeTemplates()
        records.forEach(it => {
            useGenCodeStore(instance).setInfo(it)
            useGenCodeStore(instance).saveCodeInfo()
            useGenCodeStore(instance).genAll()
        })
        ctx.body = __dirname;
    }

    @GET('/:id')
    public async greet(
        @RequestParam('id') id: string,
        @RequestQuery('id2') id2: string,
        @RequestQuery('id') b: string,
        @RequestBody s: object,
        @RequestHeader header: object
    ) {
        console.log(id, id2, b, s, this.msg, header);
        return {
            name: 123, code: -1, msg: "213", list: [{xx: 123, name: 123}],
        };
    }

    @POST('/:id')
    public async post(
        @RequestCtx ctx: Context,
        @RequestParam('id') id: string,
        @RequestQuery('id2') id2: string,
        @RequestQuery('id') b: string,
        @RequestBody s: {}
    ) {
        return this.msg;
    }
}
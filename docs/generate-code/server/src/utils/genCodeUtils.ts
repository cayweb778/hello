/* eslint-disable */

// @ts-nocheck

import * as fs from 'fs';
import {getPool} from "../config/dbpool/dbPool";


function firstToUpper1(str) {
    return str.trim().replace(str[0], str[0].toUpperCase());
}

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


// 要删除非空文件夹，需要先把文件夹里的文件删除，再删除空文件夹

function removeDir(path) {
    let data
    try {
        data = fs.readdirSync(path); //data是一个数组，文件夹名和文件名用引号括起来，如["1", "2.txt", "3.html"]
    } catch (e) {
        return
    }

    for (let i = 0; i < data.length; ++i) {
        //用循环判断数组中的元素是文件还是文件夹，是文件就删除，是文件夹就继续查找
        let url = path + "/" + data[i];
        let stat = fs.statSync(url); //用fs.stat获取文件或文件夹的详细信息
        if (stat.isDirectory()) {
            //用isDirectory判断元素是不是文件夹，是的话继续查找
            removeDir(url);
        } else {
            //不是文件夹的话，就是文件，删除文件
            fs.unlinkSync(url);
        }
    }

    // 删除空文件夹
    fs.rmdirSync(path);
}

let instance = {
    store: {}
}

function defineStore(params) {
    return (instance) => {
        if(!instance.enable){

        const s = {
            enable:true,
            ...params.state(),
            ...params.actions
        }
        Object.assign(instance, s)
        }
        return instance
    }
}

const projectPath = __dirname + '/../../..'
export const useGenCodeStore = defineStore({
    state: () => ({
        id: 1,
        name: '',
        info: '',
        tempDir: projectPath + '/dist',
        // tempDir: '/home/cay/Documents/hello/templates'
        // tempDir: __dirname + '/templates'
        pathInfo: {},
        label: ''
    }),
    actions: {
        setName(name) {
            // this.name=name
        },
        setInfo(info) {
            this.info = info
            this.name = info.recordName
            this.label = info.label
            if (info.type == 1) {
                this.platformName = 'group'
            } else if (info.type == 2) {
                this.platformName = 'origin'
            } else if (info.type == 3) {
                this.platformName = 'account'
            }
        },
        saveCodeInfo() {

            const dbClient = getPool()

            function getCurrentData() {
                var myDate = new Date();

                const year = myDate.getFullYear();
                const month = String(myDate.getMonth() + 1).padStart(2, '0');
                const day = String(myDate.getDate()).padStart(2, '0');
                return year + '-' + month + '-' + day
            }

            this.info.tableInfo.forEach(item => {
                const record_key = this.info.recordName
                const record_label = this.info.label
                const record_type = this.info.type
                const col_key = item.field
                const col_label = item.label
                const col_type = item.type
                const col_des = item.des
                const createby = '木子桉易洋'
                const createdate = getCurrentData()
                dbClient.query(`
                    INSERT INTO public._app_group_def_table_dist (record_key, record_label, record_type, col_key,
                                                                  col_label, col_type, "col_des", createby, createdate)
                    VALUES ('${record_key}', '${record_label}', '${record_type}', '${col_key}', '${col_label}',
                            '${col_type}
                        ',
                            '${col_des}', '${createby}', '${createdate}')
                `)
            })

        },
        getName() {

        },
        removeTemplates() {
            removeDir(this.tempDir + '/src')
            removeDir(this.tempDir + '/web')
            // fs.mkdirSync('/tmp/a/apple',{recursive：true});
        },
        genDir() {
            fs.mkdirSync(this.tempDir + '/src/main/java/org/boozsoft/rest/' + this.platformName, {recursive: true});
            fs.mkdirSync(this.tempDir + '/src/main/java/org/boozsoft/service/' + this.platformName, {recursive: true});
            fs.mkdirSync(this.tempDir + '/src/main/java/org/boozsoft/service/' + this.platformName + '/impl/', {recursive: true});
            fs.mkdirSync(this.tempDir + '/src/main/java/org/boozsoft/repo/' + this.platformName, {recursive: true});
            fs.mkdirSync(this.tempDir + '/src/main/java/org/boozsoft/domain/entity/' + this.platformName, {recursive: true});
            fs.mkdirSync(this.tempDir + '/src/main/java/org/boozsoft/domain/vo/' + this.platformName, {recursive: true});
            fs.mkdirSync(this.tempDir + `/web/src/views/boozsoft/${this.platformName}/${this.name}`, {recursive: true});
            fs.mkdirSync(this.tempDir + `/web/src/views/boozsoft/${this.platformName}/${this.name}/popup`, {recursive: true});
            fs.mkdirSync(this.tempDir + `/web/src/views/boozsoft/${this.platformName}/${this.name}/data`, {recursive: true});
            fs.mkdirSync(this.tempDir + `/web/src/views/boozsoft/${this.platformName}/${this.name}/other`, {recursive: true});
            fs.mkdirSync(this.tempDir + `/web/src/views/boozsoft/${this.platformName}/${this.name}/record-layout`, {recursive: true});
            fs.mkdirSync(this.tempDir + `/web/src/api/boozsoft/${this.platformName}`, {recursive: true});
            fs.mkdirSync(this.tempDir + `/web/rbac/menu_data/pages`, {recursive: true});
        },

        genTable() {
            const tableInfo = this.info.tableInfo
            const aaa = tableInfo.map(it => {
                const str = `
                   ${it.field}                   varchar,
                `

                return str
            }).join('\n')

            fs.appendFile(this.tempDir + `/${this.name}.sql`, `create table ${this.name}
                                                        (
                                                            ${aaa}
                                                        );`, err => {
            });
        },

        genEntity() {
            const idStr = `
    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
            `
            const tableInfo = this.info.tableInfo
            const content = idStr + tableInfo.filter(item => item.field != 'id').map(it => {
                const str = `
    @ApiModelProperty(value = "${it.label}", hidden = true)
    private String ${it.field};
                `

                return str
            }).join('\n')

            let tableName = toLine(this.name)
            if (this.info.type == 1) {
                tableName = '_app_group' + tableName
            } else if (this.info.type == 2) {
                tableName = '_app_group' + tableName

            } else if (this.info.type == 3) {
                tableName = tableName.substring(1, tableName.length)
            }
            fs.appendFile(
                this.tempDir + `/src/main/java/org/boozsoft/domain/entity/${this.platformName}/${firstToUpper1(this.platformName)}${this.name}.java`,
                fs.readFileSync(__dirname + '/templates/src/main/java/org/boozsoft/domain/entity/Entity.java.tpl', 'utf8')
                    .replaceAll('$$内容$$', content)
                    .replaceAll('$$表名$$', tableName)
                    .replaceAll('$$RecordName$$', firstToUpper1(this.platformName)+this.name)
                    .replaceAll('$$platformName$$', this.platformName), err => {
                });
        },
        genVo() {
            const idStr = `
    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
            `
            const tableInfo = this.info.tableInfo
            const content = idStr + tableInfo.filter(item => item.field != 'id').map(it => {
                const str = `
    @ApiModelProperty(value = "未设置", hidden = true)
    private String ${it.field};
                `

                return str
            }).join('\n')

            let tableName = toLine(this.name)
            if (this.info.type == 1) {
                tableName = '_app_group' + tableName
            } else if (this.info.type == 2) {
                tableName = '_app_group' + tableName

            } else if (this.info.type == 3) {
                tableName = tableName.substring(1, tableName.length)
            }
            fs.appendFile(
                this.tempDir + `/src/main/java/org/boozsoft/domain/vo/${this.platformName}/${firstToUpper1(this.platformName)}${this.name}Vo.java`,
                fs.readFileSync(__dirname + '/templates/src/main/java/org/boozsoft/domain/entity/Entity.java.tpl', 'utf8')
                    .replaceAll('$$内容$$', content)
                    .replaceAll('$$表名$$', tableName)
                    .replaceAll('$$RecordName$$',  firstToUpper1(this.platformName)+this.name+'Vo')
                    .replaceAll('$$platformName$$', this.platformName), err => {
                });
        },

        genRest() {
            fs.appendFile(
                this.tempDir + `/src/main/java/org/boozsoft/rest/${this.platformName}/${firstToUpper1(this.platformName)}${this.name}Controller.java`,
                fs.readFileSync(__dirname + '/templates/src/main/java/org/boozsoft/rest/Controller.java.tpl', 'utf8')
                    .replaceAll('$$path$$', `/${this.name}`)
                    .replaceAll('$$RecordName$$', firstToUpper1(this.platformName)+this.name)
                    .replaceAll('$$platformName$$', this.platformName)
                , err => {
                }
            );
        },

        genService() {
            fs.appendFile(
                this.tempDir + `/src/main/java/org/boozsoft/service/${this.platformName}/${firstToUpper1(this.platformName)}${this.name}Service.java`,
                fs.readFileSync(__dirname + '/templates/src/main/java/org/boozsoft/service/Service.java.tpl', 'utf8')
                    .replaceAll('$$RecordName$$', firstToUpper1(this.platformName)+this.name)
                    .replaceAll('$$platformName$$', this.platformName)
                , err => {
                });
            fs.appendFile(
                this.tempDir + `/src/main/java/org/boozsoft/service/${this.platformName}/impl/${firstToUpper1(this.platformName)}${this.name}ServiceImpl.java`,
                fs.readFileSync(__dirname + '/templates/src/main/java/org/boozsoft/service/impl/ServiceImpl.java.tpl', 'utf8')
                    .replaceAll('$$RecordName$$', firstToUpper1(this.platformName)+this.name)
                    .replaceAll('$$platformName$$', this.platformName)

                , err => {
                });
        },
        genRepo() {
            fs.appendFile(
                this.tempDir + `/src/main/java/org/boozsoft/repo/${this.platformName}/${firstToUpper1(this.platformName)}${this.name}Repository.java`,
                fs.readFileSync(__dirname + '/templates/src/main/java/org/boozsoft/repo/Repository.java.tpl', 'utf8')

                    .replaceAll('$$RecordName$$', firstToUpper1(this.platformName)+this.name)
                    .replaceAll('$$platformName$$', this.platformName)
                , err => {});
        },
        genPage() {
            this.pathInfo.frontPath = `/boozsoft/${this.platformName}/${this.name}/index`

            function renderTpls(arr) {
                arr.forEach(it => fs.appendFileSync(
                    it.target,
                    it.tplText,
                    err => {
                    }))
            }

            const apiDir = this.tempDir + `/web/src/api/boozsoft/${this.platformName}`
            const baseDir = this.tempDir + `/web/src/views/boozsoft/${this.platformName}/${this.name}`
            renderTpls([
                {
                    key: 'indexTpl',
                    tplText: fs.readFileSync(__dirname + '/templates/web/src/views/boozsoft/group/index.vue.tpl', 'utf8')
                        .replaceAll('$$recordPropties$$',
                            this.info.tableInfo.map(it => {
                                return ` {field:'${it.field}',label:'${it.label}'}`
                            }).join(',\n')

                        )
                        .replaceAll('$$recordName$$', this.name)
                    ,
                    target: `${baseDir}/index.vue`
                },
                {
                    key: 'dataTpl ',
                    tplText: fs.readFileSync(__dirname + '/templates/web/src/views/boozsoft/group/data/data.ts.tpl', 'utf8'),
                    target: `${baseDir}/data/data.ts`
                },
                {
                    key: 'editTpl ',
                    tplText: fs.readFileSync(__dirname + '/templates/web/src/views/boozsoft/group/popup/edit.vue.tpl', 'utf8'),
                    target: `${baseDir}/popup/edit.vue`
                },
                {
                    key: 'printTpl',
                    tplText: fs.readFileSync(__dirname + '/templates/web/src/views/boozsoft/group/popup/print.ts.tpl', 'utf8'),
                    target: `${baseDir}/popup/print.ts`
                },
                {
                    key: 'queryTpl',
                    tplText: fs.readFileSync(__dirname + '/templates/web/src/views/boozsoft/group/popup/query.vue.tpl', 'utf8'),
                    target: `${baseDir}/popup/query.vue`
                },
                {
                    key: 'excelTpl',
                    tplText: fs.readFileSync(__dirname + '/templates/web/src/views/boozsoft/group/popup/excel.ts.tpl', 'utf8'),
                    target: `${baseDir}/popup/excel.vue`
                },
                {
                    key: 'RecordLayout',
                    tplText:
                        fs.readFileSync(__dirname + '/templates/web/src/views/boozsoft/group/record-layout/RecordLayout.vue.tpl', 'utf8')
                            .replaceAll('$$recordName$$',this.name)
                    ,

                    target: `${baseDir}/record-layout/RecordLayout.vue`
                },
                {
                    key: 'crudFun.ts.tpl',
                    tplText: fs.readFileSync(__dirname + '/templates/web/src/views/boozsoft/group/crudFun.ts.tpl', 'utf8'),
                    target: `${baseDir}/crudFun.ts`
                },
                {
                    key: 'crudFun.ts.tpl',
                    tplText: fs.readFileSync(__dirname + '/templates/web/src/views/boozsoft/group/DefaultView.vue.tpl', 'utf8'),
                    target: `${baseDir}/DefaultView.vue`
                },
                {
                    key: 'CardView.vue.tpl',
                    tplText: fs.readFileSync(__dirname + '/templates/web/src/views/boozsoft/group/other/CardView.vue.tpl', 'utf8'),
                    target: `${baseDir}/other/CardView.vue`
                },
                {
                    key: 'apiTpl',
                    tplText: fs.readFileSync(__dirname + `/templates/web/src/api/boozsoft/api.ts.tpl`, 'utf8')
                        .replaceAll('$$recordName$$', this.name),
                    target: `${apiDir}/${this.name}.ts`
                }
            ])
        },
        genFrontRbac() {
            fs.appendFile(this.tempDir + `/web/rbac/menu_data/pages/systemMenus.ts`, `
              createPlatformMenu({
                id: 50602,
                path: '${this.name}',
                component: '${this.pathInfo.frontPath}',
                parentId: 50600,
                name: '${this.label}',
              }),
`, err => {
            });
            // /zhongZhangMenus.ts
        },
        genAll() {
            this.genDir()
            this.genTable()
            this.genEntity()
            this.genVo()
            this.genRest()
            this.genService()
            this.genRepo()
            this.genPage()
            this.genFrontRbac()
        }
    }
})

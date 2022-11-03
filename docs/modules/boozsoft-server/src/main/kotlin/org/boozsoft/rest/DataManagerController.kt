package org.boozsoft.rest


import com.alibaba.fastjson.JSONArray
import org.boozsoft.repo.Menu2Repository
import org.boozsoft.repo.RoleMenuRepository
import org.boozsoft.repo.RoleRepository
import org.boozsoft.repo.entity.Menu
import org.boozsoft.repo.entity.Role
import org.boozsoft.repo.entity.RoleMenu
import org.springbooz.core.tool.result.R
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.util.*

//@RequiredArgsConstructor
// 数据管理
@RestController
@RequestMapping("/dataManager")
class DataManagerController {

    @Autowired
    lateinit var r2dbcEntityTemplate: R2dbcEntityTemplate

    @Autowired
    lateinit var menu2Controller: Menu2Repository

    @Autowired
    lateinit var roleRepository: RoleRepository

    @Autowired
    lateinit var roleMenu2Repository: RoleMenuRepository
    fun abc(aaa: Any?): String {
        if (aaa == null) {
            return "null"
        } else {
            var str = "'" + aaa + "'"
            if (str == "'false'") {
                return "false"
            } else if (str == "'true'") {
                return "true"
            } else {
                return str
            }
        }
    }

    @RequestMapping("/importMenuData")
//  fun getUser(): Flux<GroupUser> {
    fun importMenuData(@RequestBody json: Map<Object, String>): Mono<R<Any>> {
//        val parse = JSONArray.parseObject(json,SystemMenu::class.java)
        val parseArray = JSONArray.parseArray(json.get<Any, String>("json"), Menu::class.java)
        var sql = parseArray.map {
            var aa =
                "insert into _app_group_sys_menu ( id,platform_id,redirect, parent_id ,name, path ,component ,component_name  ,category ,perms ,perms_type ,sort_no ,icon ,description ,create_by ,create_time ,update_by ,update_time ,del_flag ,rule_flag ,status ,always_show ,hidden ,internal_or_external ,is_route ,keep_alive ,is_leaf) values (" +
                        abc(it.id) + "," +
                        abc(it.platformId) + "," +
                        abc(it.redirect) + "," +
                        abc(it.parentId) + "," +
                        abc(it.name) + "," +
                        abc(it.path) + "," +
                        abc(it.component) + "," +
                        abc(if(it.componentName==null)it.name else{it.componentName}) + "," +
                        abc(it.category) + "," +
                        abc(it.perms) + "," +
                        abc(it.permsType) + "," +
                        abc(it.sortNo) + "," +
                        abc(it.icon) + "," +
                        abc(it.description) + "," +
                        abc(it.createBy) + "," +
                        abc(it.createTime) + "," +
                        abc(it.updateBy) + "," +
                        abc(it.updateTime) + "," +
                        abc(it.delFlag) + "," +
                        abc(it.ruleFlag) + "," +
                        abc(it.status) + "," +
                        abc(it.isAlwaysShow) + "," +
                        abc(it.isHidden) + "," +
                        abc(it.isInternalOrExternal) + "," +
                        abc(it.isRoute) + "," +
                        abc(it.isKeepAlive) + "," +
                        abc(it.isLeaf) + ");"

            aa
        }.joinToString(separator = "")
        val deleteAll = menu2Controller.deleteAll()
        val fetch = r2dbcEntityTemplate.databaseClient.sql(sql).fetch().first()
        return deleteAll.then(fetch).thenReturn(R.ok())
    }

    @RequestMapping("/importRoleMenuData")
    fun importRoleMenuData(@RequestBody json: Map<Object, String>): Mono<R<Any>> {
        val parseArray = JSONArray.parseArray(json.get<Any, String>("json"), RoleMenu::class.java)
        var sql = parseArray.map {
            var aa =
                "insert into _app_group_sys_role_menu (id,role_id, menu_id) values (" +
                        abc(it.id) + "," +
                        abc(it.roleId) + "," +
                        abc(it.menuId)+");"
            aa
        }.joinToString(separator = "")
        val deleteAll = roleMenu2Repository.deleteAll()
        val fetch = r2dbcEntityTemplate.databaseClient.sql(sql).fetch().first()
        return deleteAll.then(fetch).thenReturn(R.ok())
    }


    @RequestMapping("/importRoleData")
    fun importRoleData(@RequestBody json: Map<Object, String>): Mono<R<Any>> {
        val parseArray = JSONArray.parseArray(json.get<Any, String>("json"), Role::class.java)
        var sql = parseArray.map {
            var aa =
                "insert into _app_group_sys_role ( id, parent_id, role_name, sort, role_alias, is_deleted) values (" +
                        abc(it.id) + "," +
                        abc(it.parentId) + "," +
                        abc(it.roleName) + "," +
                        abc(it.sort) + "," +
                        abc(it.roleAlias) + "," +
                        abc(it.isDeleted)+ ");"
            aa
        }.joinToString(separator = "")
        val deleteAll = roleRepository.deleteAll()
        val fetch = r2dbcEntityTemplate.databaseClient.sql(sql).fetch().first()
        return deleteAll.then(fetch).thenReturn(R.ok())
    }

}


//package org.boozsoft.rest
//
//
//import com.alibaba.fastjson.JSON
//import org.boozsoft.domain.entity.Customer
//import org.boozsoft.domain.entity.Supplier
//import org.boozsoft.domain.entity.account.ProjectCategory
//import org.boozsoft.domain.entity.account.SysDepartment
//import org.boozsoft.domain.entity.account.SysPsn
//import org.boozsoft.domain.entity.share.project.base.Project
//import org.boozsoft.repo.*
//import org.boozsoft.repo.project.base.ProjectRepositoryBase
//import org.boozsoft.util.UnderscopeAndCalmelUtils
//import org.springbooz.core.tool.result.R
//import org.springbooz.datasource.r2dbc.domain.vo.R2dbcRouterBuilder
//import org.springbooz.datasource.r2dbc.service.R2dbcRouterLoader
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
//import org.springframework.web.bind.annotation.PostMapping
//import org.springframework.web.bind.annotation.RequestBody
//import org.springframework.web.bind.annotation.RequestMapping
//import org.springframework.web.bind.annotation.RestController
//import reactor.core.publisher.Flux
//import reactor.core.publisher.Mono
//import java.util.function.Function
//
//@RestController
//@RequestMapping("/fuZhuHeSuan2")
//class FuZhuHeSuan2Controller3 {
//    @Autowired
//    lateinit var r2dbcEntityTemplate: R2dbcEntityTemplate
//
//    @Autowired
//    lateinit var r2dbcRouterLoader: R2dbcRouterLoader
//
//    @Autowired
//    lateinit var fzDeptR: SysDepartmentRepository
//
//    @Autowired
//    lateinit var fzEmpR: SysPsnRepository
//
//    @Autowired
//    lateinit var fzCustomR: CustomerRepository
//
//    @Autowired
//    lateinit var fzGysR: SupplierRepository
//
//    @Autowired
//    lateinit var fzProjectR: ProjectRepositoryBase
//
//    @Autowired
//    lateinit var fzProjectCategoryR: ProjectCategoryRepository
//
//    @Autowired
//    lateinit var fuzhuHesuanRepository: FuzhuHesuanRepository
//
//
//    // -- kotlin的单项数据流 --
//    // 单向数据流，个人理解，保持粗话，通俗易懂
//    // 这是一个目前nc，较好的并发概念例子
//    // 尽量减少业务内的关联性
//    // 注意高利用rxjava的并发作用
//
//    // 1.Mono,Flux都是 方法(函数)定义的一种手段
//    // 2.也就是Mono内逻辑，或者Flux逻辑，不会直接执行
//
//    // Flux可以理解多线程
//    // Flux内的元素（逻辑），可以理解为多个方法（函数）定义
//
//    // 以下例子，业务范围是36个表 36线路 sql并发查询
//
//    // Flux.mergeWith无序，合并Flux
//
//    // collectList(),36线程(Flux元素)都处理完毕
//    // 将最终结果返回给前端
//    fun toMap(flux: Flux<Customer>,a:Function<String,String>,paramsP:String): Mono<Map<String, Any>> {
//        return flux.map { JSON.parseObject(JSON.toJSONString(it), Map::class.java) }  .collectList().map { it1 ->
//            mapOf(
//                    "key" to "fzGys",
//                    "label" to "供应商",
//                    "list" to it1.map { row ->
//                        if (paramsP== "true")
//                            mapOf(
//                                    "key" to row["uniqueCode"],
//                                    "label" to row["supName"],
//                                    "code" to row["supCode"],
//                                    "target" to row
//                            )
//                        else
//                            mapOf(
//                                    "key" to row["uniqueCode"],
//                                    "label" to row["supName"],
//                                    "code" to row["supCode"],
//                            )
//                    }
//            )
//        }
//    }
//
//
//    @PostMapping("/findKeyLabelAll")
//    fun findKeyLabelAll(@RequestBody params: Map<Any, Any>): Mono<R<Any>> {
//        val require = params["require"] as String
//        val dbClient = r2dbcEntityTemplate.databaseClient
//        // 定义30线程
//        return Mono.fromSupplier { require.split(",").filter { !it.contains("fz") } }
//            .map {
//                val joinToString = it.joinToString(separator = ",")
//                if (joinToString.isNotEmpty()) {
//                    val sql1 = """select * from fuzhu_hesuan where cdfine in (${joinToString})"""
//                    dbClient.sql(sql1).fetch().all()
//                } else Flux.empty()
//            }
//            .flatMapMany { it }
//            .doOnNext {
//                if (it["cankao_duixiang_where"] == null) it["cankao_duixiang_where"] = ""
//                if (it["cankao_duixiang_flag"] == null) it["cankao_duixiang_where"] = ""
//            }
//            .flatMap { def ->
//                if ((def["cankao_duixiang_table"] as String).contains("_app_group")) {
//                    r2dbcRouterLoader
//                        .bind({
//                            dbClient.sql("""select * from ${def["cankao_duixiang_table"]} ${def["cankao_duixiang_where"]} ${def["cankao_duixiang_where"]} ${def["cankao_duixiang_flag"]}""")
//                                .fetch().all()
//                                .collectList()
//                        }, R2dbcRouterBuilder.ofDefault())
//                        .map { arrayOf(def, it) }
//
//                } else {
//                    dbClient.sql("""select * from ${def["cankao_duixiang_table"]} ${def["cankao_duixiang_where"]}""")
//                        .fetch().all()
//                        .collectList()
//                        .map { arrayOf(def, it) }
//                }
//            }
//            .map {
//                val def = it[0] as Map<*, *>
//                val recordList = it[1] as ArrayList<Map<String, Any>>
//                mapOf<Any, Any?>(
//                    "key" to def["cdfine"],
//                    "label" to def["cname"],
//                    "list" to recordList.map { row ->
//                        if (params["toTarget"] == "true")
//                            mapOf(
//                                "key" to row[def["cankao_duixiang_key"]],
//                                "label" to row[def["cankao_duixiang_label"]],
//                                "code" to row[def["cankao_duixiang_code"]],
//                                "target" to UnderscopeAndCalmelUtils.dbRowsToCamel(row)
//                            )
//                        else
//                            mapOf(
//                                "key" to row[def["cankao_duixiang_key"]],
//                                "label" to row[def["cankao_duixiang_label"]],
//                                "code" to row[def["cankao_duixiang_code"]],
//                            )
//                    }
//                )
//            }
//            // 定义6线程
//            .mergeWith(
//                if (!require.contains("fzDept")) Mono.empty()
//                else fzDeptR.findAll().map { JSON.parseObject(JSON.toJSONString(it), Map::class.java) }
//                    .collectList()
//                    .map { it1 ->
//                        mapOf<Any, Any>(
//                            "key" to "fzDept",
//                            "label" to "部门",
//                            "list" to it1.map { row ->
//                                if (params["toTarget"] == "true")
//                                    mapOf(
//                                        "key" to row["uniqueCode"],
//                                        "label" to row["deptName"],
//                                        "code" to row["deptCode"],
//                                        "target" to row
//                                    )
//                                else
//                                    mapOf(
//                                        "key" to row["uniqueCode"],
//                                        "label" to row["deptName"],
//                                        "code" to row["deptCode"],
//                                    )
//                            }
//                        )
//                    }
//            )
//            .mergeWith(
//                if (!require.contains("fzEmp")) Mono.empty()
//                else fzEmpR.findAll().map { JSON.parseObject(JSON.toJSONString(it), Map::class.java) }.collectList()
//                    .map { it1 ->
//                        mapOf(
//                            "key" to "fzEmp",
//                            "label" to "员工",
//                            "list" to it1.map { row ->
//                                if (params["toTarget"] == "true")
//                                    mapOf(
//                                        "key" to row["uniqueCode"],
//                                        "label" to row["psnName"],
//                                        "code" to row["psnCode"],
//                                        "target" to row
//                                    )
//                                else
//                                    mapOf(
//                                        "key" to row["uniqueCode"],
//                                        "label" to row["psnName"],
//                                        "code" to row["psnCode"],
//                                    )
//                            }
//                        )
//                    }
//            )
//            .mergeWith(
//                    if (!require.contains("fzCustom")) Mono.empty()
//            else
//                toMap(fzCustomR.findAll(), Function { b: String? -> "1" }, params["toTarget"] as String)
//            )
//            .mergeWith(
//                if (!require.contains("fzGys")) Mono.empty()
//                else
//                    fzGysR.findAll()
//                        .map { JSON.parseObject(JSON.toJSONString(it), Map::class.java) }.collectList().map { it1 ->
//                            mapOf(
//                                "key" to "fzGys",
//                                "label" to "供应商",
//                                "list" to it1.map { row ->
//                                    if (params["toTarget"] == "true")
//                                        mapOf(
//                                            "key" to row["uniqueCode"],
//                                            "label" to row["supName"],
//                                            "code" to row["supCode"],
//                                            "target" to row
//                                        )
//                                    else
//                                        mapOf(
//                                            "key" to row["uniqueCode"],
//                                            "label" to row["supName"],
//                                            "code" to row["supCode"],
//                                        )
//                                }
//                            )
//                        }
//            )
//            .mergeWith(
//                if (!require.contains("fzItem")) Mono.empty()
//                else fzProjectR.findAll().map { JSON.parseObject(JSON.toJSONString(it), Map::class.java) }.collectList()
//                    .map { it1 ->
//                        mapOf(
//                            "key" to "fzItem",
//                            "label" to "项目",
//                            "list" to it1.map { row ->
//                                if (params["toTarget"] == "true")
//                                    mapOf(
//                                        "key" to row["uniqueCode"],
//                                        "label" to row["projectName"],
//                                        "code" to row["projectCode"],
//                                        "itemClass" to row["projectCateCode"],
//                                        "target" to row
//                                    )
//                                else
//                                    mapOf(
//                                        "key" to row["uniqueCode"],
//                                        "label" to row["projectName"],
//                                        "code" to row["projectCode"],
//                                        "itemClass" to row["projectCateCode"],
//                                    )
//                            }
//                        )
//                    }
//            )
//            .mergeWith(
//                if (!require.contains("fzItemClass")) Mono.empty()
//                else fzProjectCategoryR.findAll().map { JSON.parseObject(JSON.toJSONString(it), Map::class.java) }
//                    .collectList()
//                    .map { it1 ->
//                        mapOf(
//                            "key" to "fzItemClass",
//                            "label" to "项目大类",
//                            "list" to it1.map { row ->
//                                if (params["toTarget"] == "true")
//                                    mapOf(
//                                        "key" to row["projectCateCode"],
//                                        "label" to row["projectCateName"],
//                                        "code" to row["projectCateCode"],
//                                        "target" to row
//                                    )
//                                else
//                                    mapOf(
//                                        "key" to row["projectCateCode"],
//                                        "label" to row["projectCateName"],
//                                        "code" to row["projectCateCode"],
//                                    )
//                            }
//                        )
//                    }
//            )
//            .collectList()
//            .map { R.ok(it) }
//    }
//
//
//    fun addOne(params: Map<String, String>): Mono<R<Any>> {
//        val data = (JSON.parseObject(params["data"], Map::class.java) as Map<String, String>).toMutableMap()
//        // 自定义辅助项的删除
//        val require = params["require"] as String
//        val dbClient = r2dbcEntityTemplate.databaseClient
//        if (!(params["key"] as String).contains("fz")) {
//            return Mono.fromSupplier { require.split(",").filter { !it.contains("fz") } }
//                .map {
//                    val joinToString = it.joinToString(separator = ",")
//                    if (joinToString.isNotEmpty()) {
//                        val sql1 = """select * from fuzhu_hesuan where cdfine in (${joinToString})"""
//                        dbClient.sql(sql1).fetch().all()
//                    } else Flux.empty()
//                }
//                .flatMapMany { it }
//                .doOnNext {
//                    if (it["cankao_duixiang_where"] == null) it["cankao_duixiang_where"] = ""
//                    if (it["cankao_duixiang_flag"] == null) it["cankao_duixiang_where"] = ""
//                }
//                .flatMap { def ->
//                    if ((def["cankao_duixiang_table"] as String).contains("_app_group")) {
//                        r2dbcRouterLoader
//                            .bind({
////                                data["uniqueCode"] = data["key"] as String
////                                data["deptName"] = data["label"] as String
////                                data["deptCode"] = data["key"] as String
//
//                                dbClient.sql("""insert into ${def["cankao_duixiang_table"]} ( ${def["cankao_duixiang_key"]},${def["cankao_duixiang_label"]},${def["cankao_duixiang_code"]}) values ('${data["key"] as String}','${data["label"] as String}','${data["key"] as String}')""")
//                                    .fetch().all()
//                                    .collectList()
//                            }, R2dbcRouterBuilder.ofDefault())
//                            .map { "ok" }
//
//                    } else {
//                        dbClient.sql("""insert into ${def["cankao_duixiang_table"]} ( ${def["cankao_duixiang_key"]},${def["cankao_duixiang_label"]},${def["cankao_duixiang_code"]}) values ('${data["key"] as String}','${data["label"] as String}','${data["key"] as String}')""")
//                            .fetch().all()
//                            .collectList()
//                            .map { "ok" }
//                    }
//
//                }.then(Mono.just(R.ok()))
//        }
//
//
//        if (params["key"] == "fzDept") {
//
//            data["uniqueCode"] = data["key"] as String
//            data["deptName"] = data["label"] as String
//            data["deptCode"] = data["key"] as String
//            return fzDeptR.save(JSON.parseObject(JSON.toJSONString(data), SysDepartment::class.java)).thenReturn(R.ok())
//
//        }
//        if (params["key"] == "fzEmp") {
//            data["uniqueCode"] = data["key"] as String
//            data["psnName"] = data["label"] as String
//            data["psnCode"] = data["code"] as String
//            return fzEmpR.save(JSON.parseObject(JSON.toJSONString(data), SysPsn::class.java)).thenReturn(R.ok())
//
//        }
//        if (params["key"] == "fzCustom") {
//            data["uniqueCode"] = data["key"] as String
//            data["custNam"] = data["label"] as String
//            data["custCode"] = data["code"] as String
//            return fzCustomR.save(JSON.parseObject(JSON.toJSONString(data), Customer::class.java)).thenReturn(R.ok());
//
//        }
//        if (params["key"] == "fzGys") {
//            data["uniqueCode"] = data["key"] as String
//            data["supName"] = data["label"] as String
//            data["supCode"] = data["code"] as String
//            return fzGysR.save(JSON.parseObject(JSON.toJSONString(data), Supplier::class.java)).thenReturn(R.ok());
//
//        }
//        if (params["key"] == "fzProject") {
//            data["uniqueCode"] = data["key"] as String
//            data["projectName"] = data["label"] as String
//            data["projectCode"] = data["code"] as String
//            return fzProjectR.save(JSON.parseObject(JSON.toJSONString(data), Project::class.java)).thenReturn(R.ok());
//
//
//        }
//        if (params["key"] == "fzProjectCategory") {
//            data["projectCateCode"] = data["key"] as String
//            data["projectCateName"] = data["label"] as String
//            data["projectCateCode"] = data["key"] as String
//
//            return fzProjectCategoryR.save(JSON.parseObject(JSON.toJSONString(data), ProjectCategory::class.java))
//                .thenReturn(R.ok());
//
//        }
//        throw RuntimeException("")
//    }
//
//    fun delOne(params: Map<String, String>): Mono<R<Any>> {
//        val data = (JSON.parseObject(params["data"], Map::class.java) as Map<String, String>).toMutableMap()
//        val tableId = params["tableId"] as String
//        val dbClient = r2dbcEntityTemplate.databaseClient
//
//        if (!(params["key"] as String).contains("fz")) {
//            return Mono.fromSupplier { tableId.split(",").filter { !tableId.contains("fz") } }
//                .map {
//                    val joinToString = it.joinToString(separator = ",")
//                    if (joinToString.isNotEmpty()) {
//                        val sql1 = """select * from fuzhu_hesuan where cdfine in (${joinToString})"""
//                        dbClient.sql(sql1).fetch().all()
//                    } else Flux.empty()
//                }
//                .flatMapMany { it }
//                .doOnNext {
//                    if (it["cankao_duixiang_where"] == null) it["cankao_duixiang_where"] = ""
//                    if (it["cankao_duixiang_flag"] == null) it["cankao_duixiang_where"] = ""
//                }
//                .flatMap { def ->
//                    if ((def["cankao_duixiang_table"] as String).contains("_app_group")) {
//                        r2dbcRouterLoader
//                            .bind({
////                                data["uniqueCode"] = data["key"] as String
////                                data["deptName"] = data["label"] as String
////                                data["deptCode"] = data["key"] as String
//
//                                dbClient.sql("""delete * from ${def["cankao_duixiang_table"]} where  ${def["cankao_duixiang_key"]}='${data["key"] as String}';""")
//                                    .fetch().all()
//                                    .collectList()
//                            }, R2dbcRouterBuilder.ofDefault())
//                            .map { "ok" }
//
//                    } else {
//                        dbClient.sql("""delete * from ${def["cankao_duixiang_table"]} where  ${def["cankao_duixiang_key"]}='${data["key"] as String}';""")
//                            .fetch().all()
//                            .collectList()
//                            .map { "ok" }
//                    }
//
//                }.then(Mono.just(R.ok()))
//        }
//
//
//
//        if (tableId.contains("fzDept")) {
//            val data = (JSON.parseObject(params["data"], Map::class.java) as Map<String, String>).toMutableMap()
//            return fzDeptR.deleteAllByUniqueCode(data["key"] as String).thenReturn(R.ok());
//
//        }
//        if (tableId.contains("fzEmp")) {
//            val data = (JSON.parseObject(params["data"], Map::class.java) as Map<String, String>).toMutableMap()
//            return fzEmpR.deleteAllByUniqueCode(data["key"] as String).thenReturn(R.ok());
//        }
//        if (tableId.contains("fzCustom")) {
//            val data = (JSON.parseObject(params["data"], Map::class.java) as Map<String, String>).toMutableMap()
//            return fzCustomR.deleteAllByUniqueCode(data["key"] as String).thenReturn(R.ok());
//
//        }
//        if (tableId.contains("fzGys")) {
//            val data = (JSON.parseObject(params["data"], Map::class.java) as Map<String, String>).toMutableMap()
//            return fzGysR.deleteAllByUniqueCode(data["key"] as String).thenReturn(R.ok());
//
//        }
//        if (tableId.contains("fzProject")) {
//            val data = (JSON.parseObject(params["data"], Map::class.java) as Map<String, String>).toMutableMap()
//            return fzProjectR.deleteAllByUniqueCode(data["key"] as String).thenReturn(R.ok());
//
//
//        }
//        if (tableId.contains("fzProjectCategory")) {
//            val data = (JSON.parseObject(params["data"], Map::class.java) as Map<String, String>).toMutableMap()
//            return fzProjectCategoryR.deleteByProjectCateCode(data["key"] as String).thenReturn(R.ok());
//
//        }
//
//        throw RuntimeException("")
//    }
//
//    @PostMapping("/findKeyLabelOtherAll")
//    fun findKeyLabelOtherAll(): Mono<R<Any>> {
//        return fuzhuHesuanRepository.findAll().collectList().map { R.ok(it) }
//    }
//}
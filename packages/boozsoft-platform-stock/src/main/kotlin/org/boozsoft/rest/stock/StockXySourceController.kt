package org.boozsoft.rest.stock

import cn.hutool.core.util.StrUtil
import com.alibaba.fastjson.JSON
import lombok.extern.slf4j.Slf4j
import org.boozsoft.domain.entity.SysUnitOfMea
import org.boozsoft.domain.entity.SysUnitOfMeaList
import org.boozsoft.domain.entity.SysUnitOfMeaMany
import org.boozsoft.domain.entity.stock.StockXyCsource
import org.boozsoft.domain.vo.stock.StockWarehousings2Vo
import org.boozsoft.domain.vo.stock.StockXySourceVo
import org.boozsoft.repo.SysUnitOfMeaListRepository
import org.boozsoft.repo.SysUnitOfMeaManyRepository
import org.boozsoft.repo.SysUnitOfMeaRepository
import org.boozsoft.repo.stock.StockXyCsourceRepository
import org.boozsoft.service.StockCangkuLevelRecordService
import org.springbooz.core.tool.result.R
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.math.BigDecimal
import java.util.function.Consumer
import java.util.stream.Collectors

@Slf4j
@RestController
@RequestMapping("/stockXySource")
class StockXySourceController {
    @Autowired
    private lateinit var xyCsourceRepository: StockXyCsourceRepository

    @Autowired
    private lateinit var unitOfMeaRepository: SysUnitOfMeaRepository

    @Autowired
    private lateinit var listRepository: SysUnitOfMeaListRepository

    @Autowired
    private lateinit var manyRepository: SysUnitOfMeaManyRepository

    @Autowired
    private lateinit var client: DatabaseClient

    @Autowired
    private lateinit var stockCangkuLevelRecordService: StockCangkuLevelRecordService
    @PostMapping("findAllByCcode")
    fun findAllByCcode(ccode: String?): Mono<R<*>> {
        return xyCsourceRepository!!.findAllByCcode(ccode).collectList()
            .map { a: List<StockXyCsource?>? -> R.ok().setResult(a) }
    }

    @PostMapping("findByXyDataSourrceMap")
    fun findByXyDataSourrceMap(@RequestBody map: Map<*, *>): Mono<R<*>> {
        return unitOfMeaRepository!!.findAll().collectList()
            .flatMap { djl: List<SysUnitOfMea> ->
                listRepository!!.findAll().collectList()
                    .flatMap { duojl: List<SysUnitOfMeaList> ->
                        manyRepository!!.findAll().collectList()
                            .flatMap { many: List<SysUnitOfMeaMany> ->
                                xyCsourceRepository!!.findByXyDataSourrceMap(
                                    map["xytype"].toString(), map["sytype"].toString(), map["syccode"].toString()
                                ).collectList()
                                    .flatMap { list: List<StockXySourceVo> ->
                                        list.forEach(Consumer { a: StockXySourceVo ->
                                            val collect = djl.stream().filter { d: SysUnitOfMea -> d.id == a.cunitid }
                                                .collect(Collectors.toList())
                                            a.cunitName = if (collect.size > 0) collect[0].unitName else ""
                                            // 查找多计量
                                            if (collect.size == 0) {
                                                val collect1 =
                                                    many.stream().filter { d: SysUnitOfMeaMany -> d.id == a.cunitid }
                                                        .collect(Collectors.toList())
                                                if (collect1.size > 0) {
                                                    val aTrue = duojl.stream()
                                                        .filter { d: SysUnitOfMeaList -> d.isMain == "true" && d.manyCode == collect1[0].unitCode }
                                                        .collect(Collectors.toList())
                                                    a.cunitName = if (aTrue.size > 0) aTrue[0].unitName else ""
                                                }
                                            }
                                        })
                                        Mono.just(list)
                                    }
                            }
                    }
            }.map { a: List<StockXySourceVo>? -> R.ok().setResult(a) }
    }

    @PostMapping("findByXyDataSourrceSearch")
    fun findByXyDataSourrceSearch(@RequestBody map: Map<*, *>): Mono<R<*>> {
        var list = JSON.parseArray(map["list"].toString(), StockXySourceVo::class.java)
        val searchMap: Map<String, String>? = map["searchConditon"] as HashMap<String, String>?
        if (StrUtil.isNotBlank(searchMap!!["value"])) {
            if (searchMap["requirement"] == "ccode") {
                list = list.stream().filter { a: StockXySourceVo ->
                    a.xyccode.contains(
                        searchMap["value"]!!
                    )
                }.collect(Collectors.toList())
            }
            if (searchMap["requirement"] == "cwhcode") {
                list = list.stream().filter { a: StockXySourceVo ->
                    a.cwhcodeName.contains(
                        searchMap["value"]!!
                    )
                }.collect(Collectors.toList())
            }
            if (searchMap["requirement"] == "cinvode") {
                list = list.stream().filter { a: StockXySourceVo ->
                    a.stockNum.contains(
                        searchMap["value"]!!
                    )
                }.collect(Collectors.toList())
            }
            if (searchMap["requirement"] == "cinvodeName") {
                list = list.stream().filter { a: StockXySourceVo ->
                    a.stockName.contains(
                        searchMap["value"]!!
                    )
                }.collect(Collectors.toList())
            }
            if (searchMap["requirement"] == "ddate") {
                list = list.stream().filter { a: StockXySourceVo -> a.xyccodeDate == searchMap["value"] }
                    .collect(Collectors.toList())
            }
            if (searchMap["requirement"] == "cmemo") {
                list = list.stream().filter { a: StockXySourceVo ->
                    a.cmemo.contains(
                        searchMap["value"]!!
                    )
                }.collect(Collectors.toList())
            }
        }
        return Mono.just(R.ok().setResult(list))
    }

    /**
     * 下游表明细
     * @param map
     * @return
     */
    @PostMapping("findBySyDataSourrceMap")
    fun findBySyDataSourrceMap(@RequestBody map: Map<*, *>): Mono<R<*>> {
        return unitOfMeaRepository!!.findAll().collectList()
            .flatMap { djl: List<SysUnitOfMea> ->
                listRepository!!.findAll().collectList()
                    .flatMap { duojl: List<SysUnitOfMeaList> ->
                        manyRepository!!.findAll().collectList()
                            .flatMap { many: List<SysUnitOfMeaMany> ->
                                xyCsourceRepository!!.findBySyDataSourrceMap(
                                    map["xytype"].toString(), map["sytype"].toString(), map["xyccode"].toString()
                                ).collectList()
                                    .flatMap { list: List<StockXySourceVo> ->
                                        list.forEach(Consumer { a: StockXySourceVo ->
                                            val collect = djl.stream().filter { d: SysUnitOfMea -> d.id == a.cunitid }
                                                .collect(Collectors.toList())
                                            a.cunitName = if (collect.size > 0) collect[0].unitName else ""
                                            // 查找多计量
                                            if (collect.size == 0) {
                                                val collect1 =
                                                    many.stream().filter { d: SysUnitOfMeaMany -> d.id == a.cunitid }
                                                        .collect(Collectors.toList())
                                                if (collect1.size > 0) {
                                                    val aTrue = duojl.stream()
                                                        .filter { d: SysUnitOfMeaList -> d.isMain == "true" && d.manyCode == collect1[0].unitCode }
                                                        .collect(Collectors.toList())
                                                    a.cunitName = if (aTrue.size > 0) aTrue[0].unitName else ""
                                                }
                                            }
                                        })
                                        Mono.just(list)
                                    }
                            }
                    }
            }.map { a: List<StockXySourceVo>? -> R.ok().setResult(a) }
    }

    /**
     * 获取销货下游
     * @return
     */
    @PostMapping("findByXyOutDataSourrce")
    fun findByXyOutDataSourrce(@RequestBody map: Map<*, *>): Mono<R<*>> {
        val billStyle = map["billStyle"].toString()
        val code = map["code"].toString()
        val type = map["type"].toString()
        return xyCsourceRepository!!.findByXyOutDataSourrce(billStyle, code)
            .filter { it: StockXySourceVo -> if (StrUtil.isBlank(type)) true else it.xyBillStyle == type }
            .collectList().map { a: List<StockXySourceVo?>? -> R.ok().setResult(a) }
    }

    /**
     * 获取销货来源
     * @return
     */
    @PostMapping("findByLyOutDataSourrce")
    fun findByLyOutDataSourrce(@RequestBody map: Map<*, *>): Mono<R<*>> {
        val billStyle = map["billStyle"].toString()
        val code = map["code"].toString()
        val type = map["type"].toString()
        return xyCsourceRepository!!.findByLyOutDataSourrce(billStyle, code)
            .filter { it: StockXySourceVo -> if (StrUtil.isBlank(type)) true else it.xyBillStyle == type }
            .collectList().map { a: List<StockXySourceVo?>? -> R.ok().setResult(a) }
    }

    /**
     * 检验上游子表行唯一码是否存在下游
     * @param map
     * @return
     */
    @PostMapping("verifySyLineCodeExistXyData")
    fun verifySyLineCodeExistXyData(@RequestBody map: Map<*, *>): Mono<R<*>> {
        val billStyle = map["billStyle"].toString()
        val xyIyear = map["xyIyear"].toString()
        val sourcedetailId = map["sourcedetailId"].toString()
        return xyCsourceRepository!!.verifySyLineCodeExistXyData(billStyle, xyIyear, sourcedetailId)
            .map { a: Long? -> R.ok().setResult(a) }
    }

    /**
     * 查询下游表主表数据
     * @param map
     * @return
     */
    @PostMapping("findByXyDataMainSourrceMap")
    fun findByXyDataMainSourrceMap(@RequestBody map: Map<*, *>): Mono<R<*>> {
        return unitOfMeaRepository!!.findAll().collectList()
            .flatMap { djl: List<SysUnitOfMea> ->
                listRepository!!.findAll().collectList()
                    .flatMap { duojl: List<SysUnitOfMeaList> ->
                        manyRepository!!.findAll().collectList()
                            .flatMap { many: List<SysUnitOfMeaMany> ->
                                xyCsourceRepository!!.findByXyDataMainSourrceMap(
                                    map["xytype"].toString(),
                                    map["sytype"].toString(),
                                    if (map["syccode"] == null) "" else map["syccode"].toString()
                                ).collectList()
                                    .flatMap { list: List<StockXySourceVo> ->
                                        list.forEach(Consumer { a: StockXySourceVo ->
                                            val collect = djl.stream().filter { d: SysUnitOfMea -> d.id == a.cunitid }
                                                .collect(Collectors.toList())
                                            a.cunitName = if (collect.size > 0) collect[0].unitName else ""
                                            // 查找多计量
                                            if (collect.size == 0) {
                                                val collect1 =
                                                    many.stream().filter { d: SysUnitOfMeaMany -> d.id == a.cunitid }
                                                        .collect(Collectors.toList())
                                                if (collect1.size > 0) {
                                                    val aTrue = duojl.stream()
                                                        .filter { d: SysUnitOfMeaList -> d.isMain == "true" && d.manyCode == collect1[0].unitCode }
                                                        .collect(Collectors.toList())
                                                    a.cunitName = if (aTrue.size > 0) aTrue[0].unitName else ""
                                                }
                                            }
                                        })
                                        Mono.just(list)
                                    }
                            }
                    }
            }.map { a: List<StockXySourceVo>? -> R.ok().setResult(a) }
    }

    /**
     * 查询上游表主表数据
     * @param map
     * @return
     */
    @PostMapping("findBySyDataMainSourrceMap")
    fun findBySyDataMainSourrceMap(@RequestBody map: Map<*, *>): Mono<R<*>> {
        return unitOfMeaRepository!!.findAll().collectList()
            .flatMap { djl: List<SysUnitOfMea> ->
                listRepository!!.findAll().collectList()
                    .flatMap { duojl: List<SysUnitOfMeaList> ->
                        manyRepository!!.findAll().collectList()
                            .flatMap { many: List<SysUnitOfMeaMany> ->
                                xyCsourceRepository!!.findBySyDataMainSourrceMap(
                                    map["xytype"].toString(),
                                    map["sytype"].toString(),
                                    if (map["xyccode"] == null) "" else map["xyccode"].toString()
                                ).collectList()
                                    .flatMap { list: List<StockXySourceVo> ->
                                        list.forEach(Consumer { a: StockXySourceVo ->
                                            val collect = djl.stream().filter { d: SysUnitOfMea -> d.id == a.cunitid }
                                                .collect(Collectors.toList())
                                            a.cunitName = if (collect.size > 0) collect[0].unitName else ""
                                            // 查找多计量
                                            if (collect.size == 0) {
                                                val collect1 =
                                                    many.stream().filter { d: SysUnitOfMeaMany -> d.id == a.cunitid }
                                                        .collect(Collectors.toList())
                                                if (collect1.size > 0) {
                                                    val aTrue = duojl.stream()
                                                        .filter { d: SysUnitOfMeaList -> d.isMain == "true" && d.manyCode == collect1[0].unitCode }
                                                        .collect(Collectors.toList())
                                                    a.cunitName = if (aTrue.size > 0) aTrue[0].unitName else ""
                                                }
                                            }
                                        })
                                        Mono.just(list)
                                    }
                            }
                    }
            }.map { a: List<StockXySourceVo>? -> R.ok().setResult(a) }
    }

    /**
     * 查询下游单据
     * @param map
     * @return
     */
    @PostMapping("findByIyearAndCcodeAndXyBillStyle")
    fun findByIyearAndCcodeAndXyBillStyle(@RequestBody map: Map<*, *>): Mono<R<*>> {
        val iyear = map["iyear"].toString()
        val ccode = map["ccode"].toString()
        val xyBillStyle = map["xyBillStyle"].toString()
        return xyCsourceRepository!!.findByIyearAndCcodeAndXyBillStyle(iyear, ccode, xyBillStyle)
            .map { `object`: StockXyCsource? -> R.ok(`object`) }
            .defaultIfEmpty(R.ok().setResult(""))
    }

    @PostMapping("findByStockWareSMax")
    fun findByStockWareSMax(@RequestBody map: Map<*, *>): Mono<R<*>> {
        val ccode = map["ccode"].toString()
        val listStr: MutableList<Int> = ArrayList()
        val daohuo: MutableList<StockWarehousings2Vo> = ArrayList()
        val ruku: MutableList<StockWarehousings2Vo> = ArrayList()
        val fapiao: MutableList<StockWarehousings2Vo> = ArrayList()
        val listMap: MutableList<Map<*, *>> = ArrayList()
        return xyCsourceRepository!!.findByStockWareSMax(ccode).collectList()
            .flatMap { list: List<StockWarehousings2Vo> ->

                list.forEach(Consumer { a: StockWarehousings2Vo ->
                    val v = StockWarehousings2Vo("","","","","","","","","","",BigDecimal.ZERO,BigDecimal.ZERO,
                        BigDecimal.ZERO,"","",BigDecimal.ZERO,"","","","",a.isumDaohuo,"")
                    val v2 = StockWarehousings2Vo("","","","","","","","","","",BigDecimal.ZERO,BigDecimal.ZERO,
                        BigDecimal.ZERO,"","",BigDecimal.ZERO,a.isumRuku,"","","","","")
                    val v3 = StockWarehousings2Vo("","","","","","","","","","",BigDecimal.ZERO,BigDecimal.ZERO,
                        BigDecimal.ZERO,"","",BigDecimal.ZERO,"","",a.isumFapiao,"","","")
                    daohuo.add(v)
                    ruku.add(v2)
                    fapiao.add(v3)
                    listStr.add(Integer.valueOf(a.isumDaohuo))
                    listStr.add(Integer.valueOf(a.isumRuku))
                    listStr.add(Integer.valueOf(a.isumFapiao))
                })

                // 最大值
                val max = listStr.stream().reduce { a: Int, b: Int -> Integer.max(a, b) }.get()
                if (max > 0) {
                    val count =
                        daohuo.stream().filter { a: StockWarehousings2Vo -> Integer.valueOf(a.isumDaohuo) === max }
                            .count()
                    val count2 =
                        ruku.stream().filter { a: StockWarehousings2Vo -> Integer.valueOf(a.isumRuku) === max }
                            .count()
                    val count3 =
                        fapiao.stream().filter { a: StockWarehousings2Vo -> Integer.valueOf(a.isumFapiao) === max }
                            .count()
                    if (count > 0) {
                        val map1: MutableMap<Any, Any> = HashMap<Any, Any>()
                        map1["daohuo"] = max
                        listMap.add(map1)
                    } else if (count2 > 0) {
                        val map1: MutableMap<Any, Any> = HashMap<Any, Any>()
                        map1["ruku"] = max
                        listMap.add(map1)
                    } else if (count3 > 0) {
                        val map1: MutableMap<Any, Any> = HashMap<Any, Any>()
                        map1["fapiao"] = max
                        listMap.add(map1)
                    }
                }
                Mono.just(listMap)



            } .map { list: List<Map<*, *>>? -> R.ok(list) }
    }

    @PostMapping("findByXySyCcode")
    fun findByXySyCcode(@RequestBody map: Map<*, *>): Mono<R<*>> {
        val xytype = map["xytype"].toString()
        val sytype = map["sytype"].toString()
        val syccode = map["syccode"].toString()
        return xyCsourceRepository!!.findByXyDataMainSourrceMap(xytype, sytype, syccode).collectList()
            .map { list: List<StockXySourceVo?>? -> R.ok(list) }
    }

    @PostMapping("findAllByBillStyleAndIyearAndCcode")
    fun findAllByBillStyleAndIyearAndCcode(@RequestBody map: Map<*, *>): Mono<R<*>> {
        val billStyle = map["billStyle"].toString()
        val iyear = map["iyear"].toString()
        val syccode = map["syccode"].toString()
        return xyCsourceRepository!!.findAllByBillStyleAndIyearAndCcode(billStyle, iyear, syccode).collectList()
            .map { list: List<StockXyCsource?>? -> R.ok(list) }
    }

    /**
     * @description: 其他入库单独立查询来源单据接口
     * @author: miao
     * @date: 2022/11/2 15:40
     * @param: [map]
     * @return: Mono<R>
    </R> */
    @PostMapping("findQTRKD_OnlyByXyData")
    fun findQTRKD_OnlyByXyData(@RequestBody map: Map<*, *>): Mono<R<*>>? {
        val ccode = map["ccode"].toString()
        val sql =
            """
             select xy.xy_bill_style,xy.xyccode,xy.xyccode_date from stock_xy_csource xy where xy.xy_bill_style='QTRKD' and xy.xyccode='$ccode' union all
             select xy.xy_bill_style,xy.syccode as xyccode,xy.syccode_date as xyccode_date from stock_changes_source xy where xy.xy_bill_style='QTRKD' and xy.syccode='$ccode' union all
             select xy.xy_bill_style,xy.syccode as xyccode,xy.syccode_date as xyccode_date from stock_taking_source xy where xy.xy_bill_style='QTRKD' and xy.syccode='$ccode' union all
             select xy.xy_bill_style,xy.syccode as xyccode,xy.syccode_date as xyccode_date from stock_transfer_source xy where xy.xy_bill_style='QTRKD' and xy.syccode='$ccode'
             """.trimIndent()
        return client!!.sql(sql).fetch().all().collectList()
            .flatMap { list: List<Map<String?, Any?>?>? ->
                val listVo =
                    JSON.parseArray(
                        JSON.toJSONString(list),
                        StockXySourceVo::class.java
                    )
                Mono.just(listVo)
            }.map { list: List<StockXySourceVo>? ->
                R.ok(
                    list
                )
            }
    }
}
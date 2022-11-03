package org.boozsoft.rest


import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import org.boozsoft.domain.entity.Accvoucher
import org.boozsoft.domain.entity.AccvoucherTemp
import org.boozsoft.repo.AccvoucherRepository
import org.boozsoft.repo.AccvoucherTempRepository
import org.boozsoft.repo.SysPeriodRepository
import org.boozsoft.repo.group.GroupSysOrgRepository
import org.boozsoft.service.impl.LoadUserImpl
import org.boozsoft.util.UnderscopeAndCalmelUtils
import org.springbooz.core.tool.result.R
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.lang.RuntimeException
import java.util.*
import kotlin.collections.HashMap

//@RequiredArgsConstructor

@RestController
@RequestMapping("/pingzheng")
class PingZhengController {
    @Autowired
    lateinit var accvoucherRepository: AccvoucherRepository

    @Autowired
    lateinit var sysPeriodRepository: SysPeriodRepository

    @Autowired
    lateinit var sysOrgRepository: GroupSysOrgRepository


    @Autowired
    lateinit var accvoucherTempRepository: AccvoucherTempRepository


    @Autowired
    lateinit var loadUserApi: LoadUserImpl

    @PostMapping("saveTempData")
    fun savePingZhengTempData(@RequestBody accvoucherTemp: AccvoucherTemp): Mono<R<Any>>? {
        var params=JSON.parseObject(JSON.toJSONString(accvoucherTemp),Map::class.java);
        return accvoucherTempRepository
            .deleteAllByUserIdAndTenantIdAndIyear(params["userId"].toString(), params["tenantId"].toString(), params["iyear"].toString())
            .then(accvoucherTempRepository.save(accvoucherTemp).map { R.ok() })

    }


    @PostMapping("findTempData")
    fun findTempData(@RequestBody params: Map<Any, Any>): Mono<R<Any>>? {
        return accvoucherTempRepository.findAllByUserIdAndTenantIdAndIyear(params["userId"].toString(), params["tenantId"].toString(), params["iyear"].toString()).next().map { R.ok(it) }.switchIfEmpty(Mono.just(R.ok()))
    }

    @PostMapping("delTempData")
    fun delTempData(@RequestBody params: Map<Any, Any>): Mono<R<Any>>? {
        return accvoucherTempRepository
            .deleteAllByUserIdAndTenantIdAndIyear(params["userId"].toString(), params["tenantId"].toString(), params["iyear"].toString())
            .then(Mono.just(R.ok()))

    }

    @PostMapping("save")
    fun savePingZheng(@RequestBody params: Map<Any, Any>): Mono<R<Any>>? {
        val parseArray = JSONArray.parseArray(params["json"].toString(), Map::class.java)
        var a = if (parseArray[0]["uniqueCode"] == null) {
            var uuid = UUID.randomUUID()
            Flux.fromIterable(parseArray)
                .map { UnderscopeAndCalmelUtils.dbRowsToCamel(it as Map<String, Any>) }
                .doOnNext {
                    it["uniqueCode"] = uuid.toString()
                    it["vouchUnCode"] = UUID.randomUUID().toString()
                }
                .collectList()
                .map { JSON.parseArray(JSON.toJSONString(it), Accvoucher::class.java) }
                .flatMapMany { accvoucherRepository.saveAll(it) }
        } else accvoucherRepository.deleteByUniqueCode(parseArray[0]["uniqueCode"] as String).thenMany(
            Flux.fromIterable(parseArray)
                .map { UnderscopeAndCalmelUtils.dbRowsToCamel(it as Map<String, Any>) }
                .doOnNext {
                    if(it["vouchUnCode"]==null){
                        it["vouchUnCode"] = UUID.randomUUID().toString()
                    }
                }
                .collectList()
                .map { JSON.parseArray(JSON.toJSONString(it), Accvoucher::class.java) }
                .flatMapMany { accvoucherRepository.saveAll(it) }
        )

        return a.collectList().map { R.ok() }
    }

    @PostMapping("delPingZhengByUniqueCode")
    fun delPingZhengByUniqueCode(@RequestBody params: Map<Any, Any>): Mono<R<Any>>? {
        return accvoucherRepository.deleteByUniqueCode(params["uniqueCode"].toString()).thenReturn(R.ok())
    }

    @PostMapping("saveTempPingZheng")
    fun saveTempPingZhengApi(@RequestBody params: Map<Any, Any>): Mono<R<Any>>? {
        val parseArray = JSONArray.parseArray(params["json"].toString(), Map::class.java)
        var uuid = UUID.randomUUID()
        val a = Flux.fromIterable(parseArray)
            .map { UnderscopeAndCalmelUtils.dbRowsToCamel(it as Map<String, Any>) }
            .doOnNext {
                it["uniqueCode"] = uuid.toString()
                it["vouchUnCode"] = UUID.randomUUID().toString()
                it["ifrag"] = "2"
            }
            .collectList()
            .map { JSON.parseArray(JSON.toJSONString(it), Accvoucher::class.java) }
            .flatMapMany {
                println(1)
                accvoucherRepository.saveAll(it)
            }


        return a.collectList().map { R.ok() }
    }


    @PostMapping("insert")
    fun insertPingZheng(@RequestBody params: Map<Any, Any>): Mono<R<Any>>? {
        val reqPingZheng = JSONArray.parseArray(params["json"].toString(), Map::class.java)
        var uuid = UUID.randomUUID()
        return accvoucherRepository.findAllByIyperiod("202102")
            .map { JSON.parseObject(JSON.toJSONString(it), HashMap::class.java) as HashMap<String, Any?> }
            .filter {
                Integer.parseInt(it["inoId"].toString()) >= Integer.parseInt(reqPingZheng[0]["inoId"].toString())
            }
            .doOnNext { it["inoId"] = (Integer.parseInt(it["inoId"].toString()) + 1) }
            .map { JSON.parseObject(JSON.toJSONString(it), Accvoucher::class.java) }
            .collectList()
            .flatMapMany {
                println(2)
                accvoucherRepository.saveAll(it)
            }
            .then(
                Mono.just(reqPingZheng)
                    .flatMapMany { Flux.fromIterable(it) }
                    .map { it as HashMap<String, Any> }
                    .doOnNext {
                        it["uniqueCode"] = uuid.toString()
                        it["vouchUnCode"] = UUID.randomUUID().toString()
                    }
                    .collectList()
                    .map { JSON.parseArray(JSON.toJSONString(it), Accvoucher::class.java) }
                    .flatMapMany { accvoucherRepository.saveAll(it) }.then()
            )
            .thenReturn(R.ok())
//            .map { it["iyperiod"] }


//        val parseArray = JSONArray.parseArray(params["json"].toString(), Map::class.java)
//        // 原单据
//        val orignDanJu = accvoucherRepository.findAllByIyperiodAndInoIdIn("yearMonth", "inoId");
////        orignDanJu.map { it.danj }
//
//        var a = accvoucherRepository.deleteByUniqueCode(parseArray[0]["uniqueCode"] as String).thenMany(
//            Flux.fromIterable(parseArray)
//                .map { UnderscopeAndCalmelUtils.dbRowsToCamel(it as Map<String, Any>) }
//                .collectList()
//                .map { JSON.parseArray(JSON.toJSONString(it), Accvoucher::class.java) }
//                .flatMapMany { accvoucherRepository.saveAll(it) }
//        )
//
//        return a.collectList().map { R.ok() }
    }

    // 凭证默认日期
    @PostMapping("queryDefaultPingZhengDate")
    fun defaultData(@RequestBody params: Map<Any, Any>): Mono<R<Any>> {

        return accvoucherRepository.queryMaxPingZhengDate().map {
            R.ok(it)
        }.switchIfEmpty(Mono.just(R.ok("")))
    }
    // 凭证默认编号

    @PostMapping("defaultInoid")
    fun defaultCode(@RequestBody params: Map<Any, Any>): Mono<R<Any>>? {
        return accvoucherRepository.queryMaxPingZhengInoId(params["yearMonth"].toString()).map { R.ok(it) }
            .switchIfEmpty(Mono.just(R.ok("")))
    }

    @PostMapping("queryByYearMonthInoIdApi2")
    fun queryByYearMonthInoIdApi2(@RequestBody params: Map<Any, Any>): Mono<R<Any>>? {
        var yearMonth = params["yearMonth"].toString();
        var inoId = params["inoId"].toString();
        return accvoucherRepository.findAllByIyperiodAndInoIdIn(yearMonth, listOf(inoId)).collectList().map { R.ok(it) }
    }

    @PostMapping("pingZhengPage")
    fun pingZhengPage(@RequestBody params: Map<Any, Any>): Mono<R<Any>>? {
//        {
//                page: 1,
//                total: 10,
//                yearMonth: '202001',
//                task: first pre next  last
//        }
//        if(params["first"]==null){
//            return accvoucherRepository.queryMaxPingZhengInoId(params["yearMonth"].toString(),params["inoId"].toString()).map { R.ok(it) }
//        }else if(params["pre"]==null){
//            return accvoucherRepository.queryMaxPingZhengInoId(params["yearMonth"].toString(),params["inoId"].toString()).map { R.ok(it) }
//        }else if(params["next"]==null){
//            return accvoucherRepository.queryMaxPingZhengInoId(params["yearMonth"].toString(),params["inoId"].toString()).map { R.ok(it) }
//        }else if(params["last"]==null){
//            return accvoucherRepository.queryMaxPingZhengInoId(params["yearMonth"].toString(),params  ["inoId"].toString()).map { R.ok(it) }
//        }else if(params["total"]==null){
//            return accvoucherRepository.queryMaxPingZhengInoId(params["yearMonth"].toString(),params["inoId"].toString()).map { R.ok(it) }
//        }
        throw RuntimeException("defaultCode错误")
    }


    @PostMapping("lastItem")
    fun abc(@RequestBody params: Map<Any, Any>): Mono<R<Any>>? {
        return accvoucherRepository.abc(params["yearMonth"].toString()).map { R.ok(it) }
    }

    @PostMapping("previousItem")
    fun aaa(@RequestBody params: Map<Any, Any>): Mono<R<Any>>? {
        return accvoucherRepository.aaa(params["yearMonth"].toString()).map { R.ok(it) }
    }

    @PostMapping("lastPage")
    fun lastPage(@RequestBody params: Map<Any, Any>): Mono<R<Any>> {
        return accvoucherRepository.lastPage(params["yearMonth"].toString()).collectList().map { R.ok(it) }
    }

    @PostMapping("oneMonth")
    fun oneMonth(@RequestBody params: Map<Any, Any>): Mono<R<Any>> {
        return accvoucherRepository.oneMonth(params["yearMonth"].toString()).collectList().map { R.ok(it) }
    }
}


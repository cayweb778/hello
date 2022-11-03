package org.boozsoft.rest.stock;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.boozsoft.domain.entity.stock.*;
import org.boozsoft.repo.stock.*;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/arApYsyf")
public class ArApYsyfController {

    @Autowired
    private ArApYsyfRepository arApYsyfRepository;
    @Autowired
    private ArApYsyfsRepository arApYsyfsRepository;
    @Autowired
    private ArApWriteOffRepository arApWriteOffRepository;
    @Autowired
    private StockSaleousingRepository saleousingRepository;
    @Autowired
    private StockXyCsourceRepository stockXyCsourceRepository;

    @GetMapping("findByTypeList")
    public Mono<R> findByTypeList(String type,String year) {
        return arApYsyfRepository.findByBillStyleAndIyearOrderByCcode(type,year)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findByTypeByDateList")
    public Mono<R> findByTypeByDateList(String type,String ddate1,String ddate2) {
        return arApYsyfRepository.findByBillStyleAndDdateOrderByCcode(type,ddate1, ddate2)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findByTypeLists")
    public Mono<R> findByTypeLists(String type,String year) {
        return arApYsyfsRepository.findByBillStyleAndIyearOrderByCcode(type,year)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping("saveYsyf")
    public Mono<R> saveYsyf(@RequestBody ArApYsyf object){
        return arApYsyfRepository.save(object)
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping("saveYsyfs")
    public Mono<R> saveYsyfs(@RequestBody ArApYsyfs object){
        return arApYsyfsRepository.save(object)
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping("saveYsyfsList")
    public Mono<R> saveYsyfsList(@RequestBody List<ArApYsyfs> object){
        return arApYsyfsRepository.saveAll(object)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping("saveWriteOff")
    public Mono<R> saveWriteOff(@RequestBody ArApWriteOff object){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        object.setDdate(sdf.format(new Date()));
        return arApWriteOffRepository.save(object)
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping("saveWriteOffList")
    public Mono<R> saveWriteOffList(@RequestBody List<ArApWriteOff> object){
        return arApWriteOffRepository.saveAll(object)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findBukongCcode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findBukongCcode(Integer num,Integer sum,Integer qzNum,String qianzhui,String billStyle) {
        return arApYsyfRepository.findBukongCcode(num,sum,qzNum,qianzhui,billStyle)
                .map(item->R.ok().setResult(item));
    }

    @GetMapping("findMaxCcode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findMaxCcode(Integer num,Integer sum,Integer qzNum,String qianzhui,String billStyle) {
        return arApYsyfRepository.findMaxCcode(num,sum,qzNum,qianzhui,billStyle)
                .map(item->R.ok().setResult(item));
    }

    @GetMapping("findArApYsyfByCcode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findArApYsyfByCcode(String ccode){
        return arApYsyfRepository.findByCcode(ccode)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findArApYsyfsByCcode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findArApYsyfsByCcode(String ccode){
        return arApYsyfsRepository.findByCcodeOrderByLineId(ccode)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findArApWriteOffByCcode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findArApWriteOffByCcode(String ccode){
        return arApWriteOffRepository.findByCcodeOrderByHxCcode(ccode)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findArApWriteOffByCcodeAndHxCcode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findArApWriteOffByCcodeAndHxCcode(String ccode,String hxCcode){
        return arApWriteOffRepository.findByCcodeAndHxCcode(ccode,hxCcode)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findArApWriteOffByType")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findArApWriteOffByType(String type,String year){
        return arApWriteOffRepository.findByBillStyleAndIyearOrderByCcode(type,year)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping("saveSaleousing")
    public Mono<R> saveSaleousing(@RequestBody StockSaleousing object){
        return saleousingRepository.save(object)
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping("saveStockXyCsource")
    public Mono<R> saveStockXyCsource(@RequestBody StockXyCsource object){
        return stockXyCsourceRepository.save(object)
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findYskhxmxb")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findYskhxmxb(String year){
        return arApWriteOffRepository.findYskhxmxb(year)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findYskhxmxbYue")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findYskhxmxbYue(String year){
        return arApWriteOffRepository.findYskhxmxbYue(year)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findYfkhxmxb")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findYfkhxmxb(String year){
        return arApWriteOffRepository.findYfkhxmxb(year)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findYfkhxmxbYue")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findYfkhxmxbYue(String year){
        return arApWriteOffRepository.findYfkhxmxbYue(year)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findQuxiaohexiaoList")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findQuxiaohexiaoList(String year){
        return arApWriteOffRepository.findQuxiaohexiaoList(year)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findQuxiaohexiaoYueList")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findQuxiaohexiaoYueList(String year){
        return arApWriteOffRepository.findQuxiaohexiaoYueList(year)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findQuxiaohexiaoYfdList")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findQuxiaohexiaoYfdList(String year){
        return arApWriteOffRepository.findQuxiaohexiaoYfdList(year)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findQuxiaohexiaoYfdYueList")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findQuxiaohexiaoYfdYueList(String year){
        return arApWriteOffRepository.findQuxiaohexiaoYfdYueList(year)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findSaleousingByCcode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findSaleousingByCcode(String ccode){
        return saleousingRepository.findByCcode(ccode)
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findXyCsourceByCcodeAndXyccode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findXyCsourceByCcodeAndXyccode(String ccode,String xyccode){
        return stockXyCsourceRepository.findByCcodeAndXyccode(ccode, xyccode)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @DeleteMapping("deleteXyCsouceById")
    public Mono<R> deleteXyCsouceById(String id){
        return stockXyCsourceRepository.deleteById(id)
                .then(Mono.just(R.ok()));
    }

    @DeleteMapping("deleteArApWriteOffById")
    public Mono<R> deleteArApWriteOffById(String id){
        return arApWriteOffRepository.deleteById(id)
                .then(Mono.just(R.ok()));
    }

    @GetMapping("findWhxskd")
    public Mono<R> findWhxskd(String year, String cvencode){
        return arApYsyfRepository.findWhxskd(year,cvencode)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findWhxskdByEndDate")
    public Mono<R> findWhxskdByEndDate(String billStyle,String year, String endDate){
        return arApYsyfRepository.findWhxskdByEndDate(billStyle,year,endDate)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @DeleteMapping("deleteArApYsyfById")
    public Mono<R> deleteArApYsyfById(String id){
        return arApYsyfRepository.deleteById(id)
                .then(Mono.just(R.ok()));
    }

    @DeleteMapping("deleteArApYsyfsByCcode")
    public Mono<R> deleteArApYsyfsByCcode(String ccode){
        return arApYsyfsRepository.deleteByCcode(ccode)
                .then(Mono.just(R.ok()));
    }

    @DeleteMapping("deleteArApYsyfsById")
    public Mono<R> deleteArApYsyfsById(String id){
        return arApYsyfsRepository.deleteById(id)
                .then(Mono.just(R.ok()));
    }

    @GetMapping("findSkdtjbList")
    public Mono<R> findSkdtjbList(String bcheck,String ddate1,String ddate2){
        if (bcheck.equals("1")){
            return arApYsyfRepository.findYshSkdtjbList(ddate1, ddate2)
                    .collectList()
                    .map(item -> R.ok().setResult(item));
        } else if (bcheck.equals("0")){
            return arApYsyfRepository.findWshSkdtjbList(ddate1, ddate2)
                    .collectList()
                    .map(item -> R.ok().setResult(item));
        } else {
            return arApYsyfRepository.findSkdtjbList(ddate1, ddate2)
                    .collectList()
                    .map(item -> R.ok().setResult(item));
        }
    }

    @GetMapping("findSkdByEndDateList")
    public Mono<R> findSkdByEndDateList(String bcheck,String iyear,String endDate){
        if (bcheck.equals("1")){
            return arApYsyfRepository.findYshSkdByEndDateList(iyear,endDate)
                    .collectList()
                    .map(item -> R.ok().setResult(item));
        } else if (bcheck.equals("0")){
            return arApYsyfRepository.findWshSkdByEndDateList(iyear,endDate)
                    .collectList()
                    .map(item -> R.ok().setResult(item));
        } else {
            return arApYsyfRepository.findSkdByEndDateList(iyear,endDate)
                    .collectList()
                    .map(item -> R.ok().setResult(item));
        }
    }

    @GetMapping("findFkdByEndDateList")
    public Mono<R> findFkdByEndDateList(String bcheck,String iyear,String endDate){
        if (bcheck.equals("1")){
            return arApYsyfRepository.findYshFkdByEndDateList(iyear,endDate)
                    .collectList()
                    .map(item -> R.ok().setResult(item));
        } else if (bcheck.equals("0")){
            return arApYsyfRepository.findWshFkdByEndDateList(iyear,endDate)
                    .collectList()
                    .map(item -> R.ok().setResult(item));
        } else {
            return arApYsyfRepository.findFkdByEndDateList(iyear,endDate)
                    .collectList()
                    .map(item -> R.ok().setResult(item));
        }
    }

    @GetMapping("findSkdmxzList")
    public Mono<R> findSkdmxzList(String bcheck,String ddate1,String ddate2,String cvencode){
        if (bcheck.equals("1")){
            return arApYsyfRepository.findYshSkdmxzList(ddate1,ddate2,cvencode)
                    .collectList()
                    .map(item -> R.ok().setResult(item));
        } else if (bcheck.equals("0")){
            return arApYsyfRepository.findWshSkdmxzList(ddate1,ddate2,cvencode)
                    .collectList()
                    .map(item -> R.ok().setResult(item));
        } else {
            return arApYsyfRepository.findSkdmxzList(ddate1,ddate2,cvencode)
                    .collectList()
                    .map(item -> R.ok().setResult(item));
        }
    }

    @GetMapping("findFkdtjbList")
    public Mono<R> findFkdtjbList(String bcheck,String ddate1,String ddate2){
        if (bcheck.equals("1")){
            return arApYsyfRepository.findYfhFkdtjbList(ddate1,ddate2)
                    .collectList()
                    .map(item -> R.ok().setResult(item));
        } else if (bcheck.equals("0")){
            return arApYsyfRepository.findWfhFkdtjbList(ddate1,ddate2)
                    .collectList()
                    .map(item -> R.ok().setResult(item));
        } else {
            return arApYsyfRepository.findFkdtjbList(ddate1,ddate2)
                    .collectList()
                    .map(item -> R.ok().setResult(item));
        }
    }

    @GetMapping("findFkdmxzList")
    public Mono<R> findFkdmxzList(String bcheck,String ddate1,String ddate2,String cvencode){
        if (bcheck.equals("1")){
            return arApYsyfRepository.findYfhFkdmxzList(ddate1,ddate2,cvencode)
                    .collectList()
                    .map(item -> R.ok().setResult(item));
        } else if (bcheck.equals("0")){
            return arApYsyfRepository.findWfhFkdmxzList(ddate1,ddate2,cvencode)
                    .collectList()
                    .map(item -> R.ok().setResult(item));
        } else {
            return arApYsyfRepository.findFkdmxzList(ddate1,ddate2,cvencode)
                    .collectList()
                    .map(item -> R.ok().setResult(item));
        }
    }

    @GetMapping("findWriteOffByIyearAndBillStyle")
    public Mono<R> findWriteOffByIyearAndBillStyle(String iyear,String billStyle){
        return arApWriteOffRepository.findByIyearAndBillStyleOrderByCcode(iyear,billStyle)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

}

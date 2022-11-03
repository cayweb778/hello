package org.boozsoft.rest.stock;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.boozsoft.domain.entity.stock.StockWarehousing;
import org.boozsoft.domain.entity.stock.StockWarehousings;
import org.boozsoft.repo.stock.StockWarehousingRepository;
import org.boozsoft.repo.stock.StockWarehousingsRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/yfd")
public class YfdController {

    @Autowired
    private StockWarehousingRepository stockWarehousingRepository;
    @Autowired
    private StockWarehousingsRepository stockWarehousingsRepository;

    @PostMapping("saveWarehousing")
    public Mono<R> saveWarehousing(@RequestBody StockWarehousing object){
        return stockWarehousingRepository.save(object)
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping("saveWarehousings")
    public Mono<R> saveWarehousings(@RequestBody StockWarehousings object){
        return stockWarehousingsRepository.save(object)
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping("saveWarehousingsList")
    public Mono<R> saveWarehousingsList(@RequestBody List<StockWarehousings> object){
        return stockWarehousingsRepository.saveAll(object)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findBukongCcode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findBukongCcode(Integer num,Integer sum,Integer qzNum,String qianzhui,String billStyle) {
        return stockWarehousingRepository.findBukongCcode(num,sum,qzNum,qianzhui,billStyle)
                .map(item->R.ok().setResult(item));
    }

    @GetMapping("findMaxCcode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findMaxCcode(Integer num,Integer sum,Integer qzNum,String qianzhui,String billStyle) {
        return stockWarehousingRepository.findMaxCcode(num,sum,qzNum,qianzhui,billStyle)
                .map(item->R.ok().setResult(item));
    }

    @DeleteMapping("deleteWarehousingById")
    public Mono<R> deleteWarehousingById(String id){
        return stockWarehousingRepository.deleteById(id)
                .then(Mono.just(R.ok()));
    }

    @DeleteMapping("deleteWarehousingsById")
    public Mono<R> deleteWarehousingsById(String id){
        return stockWarehousingRepository.deleteById(id)
                .then(Mono.just(R.ok()));
    }

    @DeleteMapping("deleteWarehousingsByCcode")
    public Mono<R> deleteWarehousingsByCcode(String ccode,String billStyle){
        return stockWarehousingsRepository.deleteByCcodeAndBillType(ccode,billStyle)
                .then(Mono.just(R.ok()));
    }

    @GetMapping("findWarehousingByCcode")
    public Mono<R> findWarehousingByCcode(String ccode){
        return stockWarehousingRepository.findByCcode(ccode)
                .map(item->R.ok().setResult(item));
    }

    @GetMapping("findWarehousingsByCcode")
    public Mono<R> findWarehousingsByCcode(String billStyle,String ccode){
        return stockWarehousingsRepository.findAllByBillStyleAndCcode(billStyle,ccode)
                .collectList()
                .map(item->R.ok().setResult(item));
    }

    @GetMapping("findWarehousingList")
    public Mono<R> findWarehousingList(String billStyle,String iyear){
        return stockWarehousingRepository.findAllByBillStyleAndIyearOrderByBcheckAscDdateAscCcodeAsc(billStyle,iyear)
                .collectList()
                .map(item->R.ok().setResult(item));
    }

    @GetMapping("findYfdtjbList")
    public Mono<R> findYfdtjbList(String bcheck,String ddate1,String ddate2,String apSourceFlag){
        if (apSourceFlag.equals("1")){
            if (bcheck.equals("1")) {
                return stockWarehousingRepository.findYshYfdCgfptjbList(ddate1, ddate2)
                        .collectList()
                        .map(item -> R.ok().setResult(item));
            } else if (bcheck.equals("0")) {
                return stockWarehousingRepository.findWshYfdCgfptjbList(ddate1, ddate2)
                        .collectList()
                        .map(item -> R.ok().setResult(item));
            } else {
                return stockWarehousingRepository.findYfdCgfptjbList(ddate1, ddate2)
                        .collectList()
                        .map(item -> R.ok().setResult(item));
            }
        } else {
            if (bcheck.equals("1")) {
                return stockWarehousingRepository.findYshYfdCgdhdtjbList(ddate1, ddate2)
                        .collectList()
                        .map(item -> R.ok().setResult(item));
            } else if (bcheck.equals("0")) {
                return stockWarehousingRepository.findWshYfdCgdhdtjbList(ddate1, ddate2)
                        .collectList()
                        .map(item -> R.ok().setResult(item));
            } else {
                return stockWarehousingRepository.findYfdCgdhdtjbList(ddate1, ddate2)
                        .collectList()
                        .map(item -> R.ok().setResult(item));
            }
        }
    }

    @GetMapping("findYfdmxbList")
    public Mono<R> findYfdmxbList(String bcheck,String ddate1,String ddate2){
        if (bcheck.equals("1")){
            return stockWarehousingRepository.findYshYfdmxzList(ddate1,ddate2)
                    .collectList()
                    .map(item -> R.ok().setResult(item));
        } else if (bcheck.equals("0")){
            return stockWarehousingRepository.findWshYfdmxzList(ddate1,ddate2)
                    .collectList()
                    .map(item -> R.ok().setResult(item));
        } else {
            return stockWarehousingRepository.findYfdmxzList(ddate1,ddate2)
                    .collectList()
                    .map(item -> R.ok().setResult(item));
        }
    }

    @GetMapping("findYfdmxzByDateAndCvencodeList")
    public Mono<R> findYfdmxzByDateAndCvencodeList(String bcheck,String ddate1,String ddate2,String cvencode,String apSourceFlag){
        if (apSourceFlag.equals("1")){
            if (bcheck.equals("1")) {
                return stockWarehousingRepository.findYshYfdCgfpmxzByDateAndCvencodeList(ddate1, ddate2, cvencode)
                        .collectList()
                        .map(item -> R.ok().setResult(item));
            } else if (bcheck.equals("0")) {
                return stockWarehousingRepository.findWshYfdCgfpmxzByDateAndCvencodeList(ddate1, ddate2, cvencode)
                        .collectList()
                        .map(item -> R.ok().setResult(item));
            } else {
                return stockWarehousingRepository.findYfdCgfpmxzByDateAndCvencodeList(ddate1, ddate2, cvencode)
                        .collectList()
                        .map(item -> R.ok().setResult(item));
            }
        } else {
            if (bcheck.equals("1")) {
                return stockWarehousingRepository.findYshYfdCgdhdmxzByDateAndCvencodeList(ddate1, ddate2, cvencode)
                        .collectList()
                        .map(item -> R.ok().setResult(item));
            } else if (bcheck.equals("0")) {
                return stockWarehousingRepository.findWshYfdCgdhdmxzByDateAndCvencodeList(ddate1, ddate2, cvencode)
                        .collectList()
                        .map(item -> R.ok().setResult(item));
            } else {
                return stockWarehousingRepository.findYfdCgdhdmxzByDateAndCvencodeList(ddate1, ddate2, cvencode)
                        .collectList()
                        .map(item -> R.ok().setResult(item));
            }
        }
    }

    @GetMapping("findYshCgdhdByIyear")
    public Mono<R> findYshCgdhdByIyear(String ddate1,String ddate2){
        return stockWarehousingRepository.findYshCgdhdByIyear(ddate1, ddate2)
                .collectList()
                .map(item -> R.ok().setResult(item));
    }

    @GetMapping("findCgfpByIyear")
    public Mono<R> findCgfpByIyear(String ddate1,String ddate2){
        return stockWarehousingRepository.findCgfpByIyear(ddate1, ddate2)
                .collectList()
                .map(item -> R.ok().setResult(item));
    }

    @GetMapping("findYfdByDateList")
    public Mono<R> findYfdByDateList(String bcheck,String iyear,String ddate1,String ddate2,String apSourceFlag){
        if (apSourceFlag.equals("1")){
            if (bcheck.equals("1")){
                return stockWarehousingRepository.findYshYfdCgfpByDateList(iyear,ddate1, ddate2)
                        .collectList()
                        .map(item -> R.ok().setResult(item));
            } else if (bcheck.equals("0")){
                return stockWarehousingRepository.findWshYfdCgfpByDateList(iyear,ddate1, ddate2)
                        .collectList()
                        .map(item -> R.ok().setResult(item));
            } else {
                return stockWarehousingRepository.findYfdCgfpByDateList(iyear,ddate1, ddate2)
                        .collectList()
                        .map(item -> R.ok().setResult(item));
            }
        } else {
            if (bcheck.equals("1")) {
                return stockWarehousingRepository.findYshYfdCgdhdByDateList(iyear, ddate1, ddate2)
                        .collectList()
                        .map(item -> R.ok().setResult(item));
            } else if (bcheck.equals("0")) {
                return stockWarehousingRepository.findWshYfdCgdhdByDateList(iyear, ddate1, ddate2)
                        .collectList()
                        .map(item -> R.ok().setResult(item));
            } else {
                return stockWarehousingRepository.findYfdCgdhdByDateList(iyear, ddate1, ddate2)
                        .collectList()
                        .map(item -> R.ok().setResult(item));
            }
        }
    }

    @GetMapping("findYfdByEndDateList")
    public Mono<R> findYfdByEndDateList(String bcheck,String iyear,String endDate,String apSourceFlag){
        if (apSourceFlag.equals("1")){
            if (bcheck.equals("1")) {
                return stockWarehousingRepository.findYshYfdCgfpByEndDateList(iyear, endDate)
                        .collectList()
                        .map(item -> R.ok().setResult(item));
            } else if (bcheck.equals("0")) {
                return stockWarehousingRepository.findWshYfdCgfpByEndDateList(iyear, endDate)
                        .collectList()
                        .map(item -> R.ok().setResult(item));
            } else {
                return stockWarehousingRepository.findYfdCgfpByEndDateList(iyear, endDate)
                        .collectList()
                        .map(item -> R.ok().setResult(item));
            }
        } else {
            if (bcheck.equals("1")) {
                return stockWarehousingRepository.findYshYfdCgdhdByEndDateList(iyear, endDate)
                        .collectList()
                        .map(item -> R.ok().setResult(item));
            } else if (bcheck.equals("0")) {
                return stockWarehousingRepository.findWshYfdCgdhdByEndDateList(iyear, endDate)
                        .collectList()
                        .map(item -> R.ok().setResult(item));
            } else {
                return stockWarehousingRepository.findYfdCgdhdByEndDateList(iyear, endDate)
                        .collectList()
                        .map(item -> R.ok().setResult(item));
            }
        }
    }

}

package org.boozsoft.rest.stock;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.boozsoft.domain.entity.stock.StockSaleousing;
import org.boozsoft.domain.entity.stock.StockSaleousings;
import org.boozsoft.repo.stock.StockSaleousingRepository;
import org.boozsoft.repo.stock.StockSaleousingsRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/ysd")
public class YsdController {

    @Autowired
    private StockSaleousingRepository saleousingRepository;
    @Autowired
    private StockSaleousingsRepository saleousingsRepository;

    @PostMapping("saveSaleousing")
    public Mono<R> saveSaleousing(@RequestBody StockSaleousing object){
        return saleousingRepository.save(object)
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping("saveSaleousings")
    public Mono<R> saveSaleousings(@RequestBody StockSaleousings object){
        return saleousingsRepository.save(object)
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping("saveSaleousingsList")
    public Mono<R> saveSaleousingsList(@RequestBody List<StockSaleousings> object){
        return saleousingsRepository.saveAll(object)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findBukongCcode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findBukongCcode(Integer num,Integer sum,Integer qzNum,String qianzhui,String billStyle) {
        return saleousingRepository.findBukongCcode(num,sum,qzNum,qianzhui,billStyle)
                .map(item->R.ok().setResult(item));
    }

    @GetMapping("findMaxCcode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findMaxCcode(Integer num,Integer sum,Integer qzNum,String qianzhui,String billStyle) {
        return saleousingRepository.findMaxCcode(num,sum,qzNum,qianzhui,billStyle)
                .map(item->R.ok().setResult(item));
    }

    @DeleteMapping("deleteSaleousingById")
    public Mono<R> deleteSaleousingById(String id){
        return saleousingRepository.deleteById(id)
                .then(Mono.just(R.ok()));
    }

    @DeleteMapping("deleteSaleousingsById")
    public Mono<R> deleteSaleousingsById(String id){
        return saleousingsRepository.deleteById(id)
                .then(Mono.just(R.ok()));
    }

    @DeleteMapping("deleteSaleousingsByCcode")
    public Mono<R> deleteSaleousingsByCcode(String ccode,String billStyle){
        return saleousingsRepository.deleteByCcodeAndBillType(ccode,billStyle)
                .then(Mono.just(R.ok()));
    }

    @GetMapping("findSaleousingByCcode")
    public Mono<R> findSaleousingByCcode(String ccode){
        return saleousingRepository.findByCcode(ccode)
                .map(item->R.ok().setResult(item));
    }

    @GetMapping("findSaleousingsByCcode")
    public Mono<R> findSaleousingsByCcode(String ccode,String billStyle){
        return saleousingsRepository.findAllByBillStyleAndCcodeOrderByDdateAscCcodeAscLineIdAsc(billStyle,ccode)
                .collectList()
                .map(item->R.ok().setResult(item));
    }

    @GetMapping("findSaleousingList")
    public Mono<R> findSaleousingList(String billStyle,String iyear){
        return saleousingRepository.findAllByBillStyleAndIyearOrderByDdateAscCcodeAsc(billStyle,iyear)
                .collectList()
                .map(item->R.ok().setResult(item));
    }

    @GetMapping("findYsdtjbList")
    public Mono<R> findYsdtjbList(String bcheck,String ddate1,String ddate2,String arSourceFlag){
        if (arSourceFlag.equals("1")){
            if (bcheck.equals("1")) {
                return saleousingRepository.findYshYsdXsfptjbList(ddate1, ddate2)
                        .collectList()
                        .map(item -> R.ok().setResult(item));
            } else if (bcheck.equals("0")) {
                return saleousingRepository.findWshYsdXsfptjbList(ddate1, ddate2)
                        .collectList()
                        .map(item -> R.ok().setResult(item));
            } else {
                return saleousingRepository.findYsdXsfptjbList(ddate1, ddate2)
                        .collectList()
                        .map(item -> R.ok().setResult(item));
            }
        } else {
            if (bcheck.equals("1")) {
                return saleousingRepository.findYshYsdXhdtjbList(ddate1, ddate2)
                        .collectList()
                        .map(item -> R.ok().setResult(item));
            } else if (bcheck.equals("0")) {
                return saleousingRepository.findWshYsdXhdtjbList(ddate1, ddate2)
                        .collectList()
                        .map(item -> R.ok().setResult(item));
            } else {
                return saleousingRepository.findYsdXhdtjbList(ddate1, ddate2)
                        .collectList()
                        .map(item -> R.ok().setResult(item));
            }
        }
    }

    @GetMapping("findYsdByDateList")
    public Mono<R> findYsdByDateList(String bcheck,String iyear,String ddate1,String ddate2,String arSourceFlag){
        if (arSourceFlag.equals("1")){
            if (bcheck.equals("1")){
                return saleousingRepository.findYshYsdXsfpByDateList(iyear,ddate1, ddate2)
                        .collectList()
                        .map(item -> R.ok().setResult(item));
            } else if (bcheck.equals("0")){
                return saleousingRepository.findWshYsdXsfpByDateList(iyear,ddate1, ddate2)
                        .collectList()
                        .map(item -> R.ok().setResult(item));
            } else {
                return saleousingRepository.findYsdXsfpByDateList(iyear,ddate1, ddate2)
                        .collectList()
                        .map(item -> R.ok().setResult(item));
            }
        } else {
            if (bcheck.equals("1")) {
                return saleousingRepository.findYshYsdXhdByDateList(iyear, ddate1, ddate2)
                        .collectList()
                        .map(item -> R.ok().setResult(item));
            } else if (bcheck.equals("0")) {
                return saleousingRepository.findWshYsdXhdByDateList(iyear, ddate1, ddate2)
                        .collectList()
                        .map(item -> R.ok().setResult(item));
            } else {
                return saleousingRepository.findYsdXhdByDateList(iyear, ddate1, ddate2)
                        .collectList()
                        .map(item -> R.ok().setResult(item));
            }
        }
    }

    @GetMapping("findYsdByEndDateList")
    public Mono<R> findYsdByEndDateList(String bcheck,String iyear,String endDate,String arSourceFlag){
        if (arSourceFlag.equals("1")){
            if (bcheck.equals("1")) {
                return saleousingRepository.findYshYsdXsfpByEndDateList(iyear, endDate)
                        .collectList()
                        .map(item -> R.ok().setResult(item));
            } else if (bcheck.equals("0")) {
                return saleousingRepository.findWshYsdXsfpByEndDateList(iyear, endDate)
                        .collectList()
                        .map(item -> R.ok().setResult(item));
            } else {
                return saleousingRepository.findYsdXsfpByEndDateList(iyear, endDate)
                        .collectList()
                        .map(item -> R.ok().setResult(item));
            }
        } else {
            if (bcheck.equals("1")) {
                return saleousingRepository.findYshYsdXhdByEndDateList(iyear, endDate)
                        .collectList()
                        .map(item -> R.ok().setResult(item));
            } else if (bcheck.equals("0")) {
                return saleousingRepository.findWshYsdXhdByEndDateList(iyear, endDate)
                        .collectList()
                        .map(item -> R.ok().setResult(item));
            } else {
                return saleousingRepository.findYsdXhdByEndDateList(iyear, endDate)
                        .collectList()
                        .map(item -> R.ok().setResult(item));
            }
        }
    }

    @GetMapping("findYsdmxbList")
    public Mono<R> findYsdmxbList(String bcheck,String ddate1,String ddate2){
        if (bcheck.equals("1")){
            return saleousingRepository.findYshYsdmxzList(ddate1,ddate2)
                    .collectList()
                    .map(item -> R.ok().setResult(item));
        } else if (bcheck.equals("0")){
            return saleousingRepository.findWshYsdmxzList(ddate1,ddate2)
                    .collectList()
                    .map(item -> R.ok().setResult(item));
        } else {
            return saleousingRepository.findYsdmxzList(ddate1,ddate2)
                    .collectList()
                    .map(item -> R.ok().setResult(item));
        }
    }

    @GetMapping("findYsdmxzByDateAndCvencodeList")
    public Mono<R> findYsdmxzByDateAndCvencodeList(String bcheck,String ddate1,String ddate2,String cvencode,String arSourceFlag){
        if (arSourceFlag.equals("1")){
            if (bcheck.equals("1")) {
                return saleousingRepository.findYshYsdXsfpmxzByDateAndCvencodeList(ddate1, ddate2, cvencode)
                        .collectList()
                        .map(item -> R.ok().setResult(item));
            } else if (bcheck.equals("0")) {
                return saleousingRepository.findWshYsdXsfpmxzByDateAndCvencodeList(ddate1, ddate2, cvencode)
                        .collectList()
                        .map(item -> R.ok().setResult(item));
            } else {
                return saleousingRepository.findYsdXsfpmxzByDateAndCvencodeList(ddate1, ddate2, cvencode)
                        .collectList()
                        .map(item -> R.ok().setResult(item));
            }
        } else {
            if (bcheck.equals("1")) {
                return saleousingRepository.findYshYsdXhdmxzByDateAndCvencodeList(ddate1, ddate2, cvencode)
                        .collectList()
                        .map(item -> R.ok().setResult(item));
            } else if (bcheck.equals("0")) {
                return saleousingRepository.findWshYsdXhdmxzByDateAndCvencodeList(ddate1, ddate2, cvencode)
                        .collectList()
                        .map(item -> R.ok().setResult(item));
            } else {
                return saleousingRepository.findYsdXhdmxzByDateAndCvencodeList(ddate1, ddate2, cvencode)
                        .collectList()
                        .map(item -> R.ok().setResult(item));
            }
        }
    }

    @GetMapping("findYshXhdByIyear")
    public Mono<R> findYshXhdByIyear(String ddate1,String ddate2){
        return saleousingRepository.findYshXhdByIyear(ddate1, ddate2)
                .collectList()
                .map(item -> R.ok().setResult(item));
    }

    @GetMapping("findXsfpByIyear")
    public Mono<R> findXsfpByIyear(String ddate1,String ddate2){
        return saleousingRepository.findXsfpByIyear(ddate1, ddate2)
                .collectList()
                .map(item -> R.ok().setResult(item));
    }

}

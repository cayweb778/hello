package org.boozsoft.rest.stock;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.boozsoft.domain.entity.stock.ArApHzhcd;
import org.boozsoft.domain.entity.stock.ArApHzhcds;
import org.boozsoft.repo.stock.ArApHzhcdRepository;
import org.boozsoft.repo.stock.ArApHzhcdsRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/arApHzhcd")
public class ArApHzhcdController {

    @Autowired
    private ArApHzhcdRepository arApHzhcdRepository;
    @Autowired
    private ArApHzhcdsRepository arApHzhcdsRepository;

    @PostMapping("saveArApHzhcd")
    public Mono<R> saveArApHzhcd(@RequestBody ArApHzhcd object){
        return arApHzhcdRepository.save(object)
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping("saveArApHzhcds")
    public Mono<R> saveArApHzhcds(@RequestBody ArApHzhcds object){
        return arApHzhcdsRepository.save(object)
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping("saveArApHzhcdsList")
    public Mono<R> saveArApHzhcdsList(@RequestBody List<ArApHzhcds> object){
        return arApHzhcdsRepository.saveAll(object)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findBukongCcode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findBukongCcode(Integer num,Integer sum,Integer qzNum,String qianzhui,String billStyle) {
        return arApHzhcdRepository.findBukongCcode(num,sum,qzNum,qianzhui)
                .map(item->R.ok().setResult(item));
    }

    @GetMapping("findMaxCcode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findMaxCcode(Integer num,Integer sum,Integer qzNum,String qianzhui,String billStyle) {
        return arApHzhcdRepository.findMaxCcode(num,sum,qzNum,qianzhui)
                .map(item->R.ok().setResult(item));
    }

    @DeleteMapping("deleteArApHzhcdById")
    public Mono<R> deleteArApHzhcdById(String id){
        return arApHzhcdRepository.deleteById(id)
                .then(Mono.just(R.ok()));
    }

    @DeleteMapping("deleteArApHzhcdsById")
    public Mono<R> deleteArApHzhcdsById(String id){
        return arApHzhcdsRepository.deleteById(id)
                .then(Mono.just(R.ok()));
    }

    @DeleteMapping("deleteArApHzhcdsByCcode")
    public Mono<R> deleteArApHzhcdsByCcode(String ccode,String billStyle){
        return arApHzhcdsRepository.deleteByCcodeAndBillType(ccode,billStyle)
                .then(Mono.just(R.ok()));
    }

    @GetMapping("findArApHzhcdList")
    public Mono<R> findArApHzhcdList(String billStyle,String iyear){
        return arApHzhcdRepository.findByBillStyleAndIyearOrderByCcode(billStyle,iyear)
                .collectList()
                .map(item->R.ok().setResult(item));
    }

    @GetMapping("findArApHzhcdsByCcode")
    public Mono<R> findArApHzhcdsByCcode(String ccode,String billStyle){
        return arApHzhcdsRepository.findAllByBillStyleAndCcodeOrderByDdateAscCcodeAscLineIdAsc(billStyle,ccode)
                .collectList()
                .map(item->R.ok().setResult(item));
    }

    @GetMapping("findHzhcdsByCcode")
    public Mono<R> findHzhcdsByCcode(String ccode,String billStyle,String busStyle){
        if (busStyle.equals("YSD")) {
            return arApHzhcdsRepository.findSaleousingByBillStyleAndCcodeOrderByLineId(billStyle, ccode)
                    .collectList()
                    .map(item -> R.ok().setResult(item));
        } else {
            return arApHzhcdsRepository.findYsyfByBillStyleAndCcodeOrderByLineId(billStyle, ccode)
                    .collectList()
                    .map(item -> R.ok().setResult(item));
        }
    }

    @GetMapping("findHzhcdsByIyearAndBillStyle")
    public Mono<R> findHzhcdsByIyearAndBillStyle(String iyear,String billStyle){
        return arApHzhcdsRepository.findByIyearAndBusStyle(iyear,billStyle)
                .collectList()
                .map(item -> R.ok().setResult(item));
    }

}

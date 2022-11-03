package org.boozsoft.rest.stock;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.boozsoft.domain.entity.stock.ArApChongxiao;
import org.boozsoft.domain.entity.stock.ArApChongxiaos;
import org.boozsoft.repo.stock.ArApChongxiaoRepository;
import org.boozsoft.repo.stock.ArApChongxiaosRepository;
import org.boozsoft.repo.stock.ArApWriteOffRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/arApChongxiao")
public class ArApChongxiaoController {

    @Autowired
    private ArApChongxiaoRepository arApChongxiaoRepository;
    @Autowired
    private ArApChongxiaosRepository arApChongxiaosRepository;
    @Autowired
    private ArApWriteOffRepository arApWriteOffRepository;

    @PostMapping("saveArApChongxiao")
    public Mono<R> saveArApChongxiao(@RequestBody ArApChongxiao object){
        return arApChongxiaoRepository.save(object)
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping("saveArApChongxiaos")
    public Mono<R> saveArApChongxiaos(@RequestBody ArApChongxiaos object){
        return arApChongxiaosRepository.save(object)
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping("saveArApChongxiaosList")
    public Mono<R> saveArApChongxiaosList(@RequestBody List<ArApChongxiaos> object){
        return arApChongxiaosRepository.saveAll(object)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findBukongCcode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findBukongCcode(Integer num,Integer sum,Integer qzNum,String qianzhui,String billStyle) {
        return arApChongxiaoRepository.findBukongCcode(num,sum,qzNum,qianzhui)
                .map(item->R.ok().setResult(item));
    }

    @GetMapping("findMaxCcode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findMaxCcode(Integer num,Integer sum,Integer qzNum,String qianzhui,String billStyle) {
        return arApChongxiaoRepository.findMaxCcode(num,sum,qzNum,qianzhui)
                .map(item->R.ok().setResult(item));
    }

    @DeleteMapping("deleteArApChongxiaoById")
    public Mono<R> deleteArApChongxiaoById(String id){
        return arApChongxiaoRepository.deleteById(id)
                .then(Mono.just(R.ok()));
    }

    @DeleteMapping("deleteArApChongxiaosById")
    public Mono<R> deleteArApChongxiaosById(String id){
        return arApChongxiaosRepository.deleteById(id)
                .then(Mono.just(R.ok()));
    }

    @DeleteMapping("deleteArApChongxiaosByCcode")
    public Mono<R> deleteArApChongxiaosByCcode(String ccode,String billStyle){
        return arApChongxiaosRepository.deleteByCcodeAndBillType(ccode,billStyle)
                .then(Mono.just(R.ok()));
    }

    @GetMapping("findArApChongxiaoList")
    public Mono<R> findArApChongxiaoList(String billStyle,String iyear){
        return arApChongxiaoRepository.findByBillStyleAndIyearOrderByCcode(billStyle,iyear)
                .collectList()
                .map(item->R.ok().setResult(item));
    }

    @GetMapping("findArApChongxiaosByCcode")
    public Mono<R> findArApChongxiaosByCcode(String ccode,String billStyle){
        return arApChongxiaosRepository.findAllByBusStyleAndCcodeOrderByDdateAscCcodeAscLineIdAsc(billStyle,ccode)
                .collectList()
                .map(item->R.ok().setResult(item));
    }

    @GetMapping("findChongxiaosByCcode")
    public Mono<R> findChongxiaosByCcode(String ccode,String billStyle){
        if (billStyle.equals("SS")) {
            return arApChongxiaosRepository.findSSByBillStyleAndCcodeOrderByLineId(billStyle, ccode)
                    .collectList()
                    .map(item -> R.ok().setResult(item));
        } if (billStyle.equals("FF")) {
            return arApChongxiaosRepository.findFFByBillStyleAndCcodeOrderByLineId(billStyle, ccode)
                    .collectList()
                    .map(item -> R.ok().setResult(item));
        } if (billStyle.equals("YS")) {
            return arApChongxiaosRepository.findYSByBillStyleAndCcodeOrderByLineId(billStyle, ccode)
                    .collectList()
                    .map(item -> R.ok().setResult(item));
        } if (billStyle.equals("YF")) {
            return arApChongxiaosRepository.findYFByBillStyleAndCcodeOrderByLineId(billStyle, ccode)
                    .collectList()
                    .map(item -> R.ok().setResult(item));
        } else {
            return arApChongxiaosRepository.findYsyfByBillStyleAndCcodeOrderByLineId(billStyle, ccode)
                    .collectList()
                    .map(item -> R.ok().setResult(item));
        }
    }

    @GetMapping("findChongxiaosByIyearAndBillStyle")
    public Mono<R> findChongxiaosByIyearAndBillStyle(String iyear,String billStyle){
        return arApChongxiaosRepository.findByIyearAndBusStyle(iyear,billStyle)
                .collectList()
                .map(item -> R.ok().setResult(item));
    }

    @DeleteMapping("deleteByCxCcode")
    public Mono<R> deleteByCxCcode(String ccode,String billStyle){
        return arApWriteOffRepository.deleteByCxCcodeAndCxStyle(ccode,billStyle)
                .then(Mono.just(R.ok()));
    }

}

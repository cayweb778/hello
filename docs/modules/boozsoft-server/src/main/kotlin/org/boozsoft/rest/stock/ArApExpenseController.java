package org.boozsoft.rest.stock;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.boozsoft.domain.entity.stock.ArApExpense;
import org.boozsoft.domain.entity.stock.ArApExpenses;
import org.boozsoft.repo.stock.ArApExpenseRepository;
import org.boozsoft.repo.stock.ArApExpensesRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/arApExpense")
public class ArApExpenseController {

    @Autowired
    private ArApExpenseRepository arApExpenseRepository;
    @Autowired
    private ArApExpensesRepository arApExpensesRepository;

    @PostMapping("saveArApExpense")
    public Mono<R> saveArApExpense(@RequestBody ArApExpense object){
        return arApExpenseRepository.save(object)
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping("saveArApExpenses")
    public Mono<R> saveArApExpenses(@RequestBody ArApExpenses object){
        return arApExpensesRepository.save(object)
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping("saveArApExpensesList")
    public Mono<R> saveArApExpensesList(@RequestBody List<ArApExpenses> object){
        return arApExpensesRepository.saveAll(object)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findBukongCcode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findBukongCcode(Integer num,Integer sum,Integer qzNum,String qianzhui,String billStyle) {
        return arApExpenseRepository.findBukongCcode(num,sum,qzNum,qianzhui)
                .map(item->R.ok().setResult(item));
    }

    @GetMapping("findMaxCcode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findMaxCcode(Integer num,Integer sum,Integer qzNum,String qianzhui,String billStyle) {
        return arApExpenseRepository.findMaxCcode(num,sum,qzNum,qianzhui)
                .map(item->R.ok().setResult(item));
    }

    @DeleteMapping("deleteArApExpenseById")
    public Mono<R> deleteArApExpenseById(String id){
        return arApExpenseRepository.deleteById(id)
                .then(Mono.just(R.ok()));
    }

    @DeleteMapping("deleteArApExpensesById")
    public Mono<R> deleteArApExpensesById(String id){
        return arApExpensesRepository.deleteById(id)
                .then(Mono.just(R.ok()));
    }

    @DeleteMapping("deleteArApExpensesByCcode")
    public Mono<R> deleteArApExpensesByCcode(String ccode,String billStyle){
        return arApExpensesRepository.deleteByCcodeAndBillType(ccode,billStyle)
                .then(Mono.just(R.ok()));
    }

    @GetMapping("findArApExpenseList")
    public Mono<R> findArApExpenseList(String billStyle,String iyear){
        return arApExpenseRepository.findByBillStyleAndIyearOrderByCcode(billStyle,iyear)
                .collectList()
                .map(item->R.ok().setResult(item));
    }

    @GetMapping("findArApExpensesByCcode")
    public Mono<R> findArApExpensesByCcode(String ccode,String billStyle){
        return arApExpensesRepository.findAllByBillStyleAndCcodeOrderByDdateAscCcodeAscLineIdAsc(billStyle,ccode)
                .collectList()
                .map(item->R.ok().setResult(item));
    }

}

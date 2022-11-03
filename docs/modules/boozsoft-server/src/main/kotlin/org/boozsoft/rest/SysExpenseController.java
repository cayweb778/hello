package org.boozsoft.rest;

import cn.hutool.core.util.IdUtil;
import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.account.SysExpense;
import org.boozsoft.repo.SysExpenseRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/sysExpense")
public class SysExpenseController {

    @Autowired
    SysExpenseRepository sysExpenseRepository;

    @GetMapping("findAll")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAll(){
        return sysExpenseRepository.findAll()
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findByFlag")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByFlag(){
        return sysExpenseRepository.findByFlagOrderById("1")
                .collectList().map(o -> R.ok().setResult(o));
    }

    @PostMapping
    @ApiOperation(value = "新增或修改", notes = "传入code")
    public Mono save(@RequestBody SysExpense object){
        if (object.getCcode()==null || object.getCcode().equals("")){
            object.setCcode(IdUtil.objectId());
        }
        if (object.getFlag()==null || object.getFlag().equals("")) {
            object.setFlag("1");
        }
        return sysExpenseRepository.save(object)
                .map(o -> R.ok().setResult(o));
    }

    @DeleteMapping
    @ApiOperation(value = "根据id删除数据", notes = "传入code")
    public Mono delete(String id){
        return sysExpenseRepository.deleteById(id)
                .then(Mono.just(R.ok()));
    }

    @PostMapping("editFlag")
    @ApiOperation(value = "停用启用操作", notes = "传入code")
    public Mono editFlag(@RequestBody SysExpense object) {
        if (object.getFlag().equals("1")) {
            object.setFlag("0");
        } else {
            object.setFlag("1");
        }
        return sysExpenseRepository.save(object)
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping("excel")
    @ApiOperation(value = "前台解析excel,后台直接保存集合", notes = "传入code")
    @Transactional
    public Mono excel(@RequestBody List<SysExpense> object){
        return Flux.fromIterable(object).map(item -> {
                    if (item.getCcode()==null || item.getCcode().equals("")){
                        item.setCcode(IdUtil.objectId());
                    }
                    if (item.getFlag()==null || item.getFlag().equals("")) {
                        item.setFlag("1");
                    }
                    return item;
                })
                .collectList()
                .flatMapMany(sysExpenseRepository::saveAll)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findByCcode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByCcode(String ccode){
        return sysExpenseRepository.findByCcodeOrderById(ccode)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findByCname")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByCname(String cname){
        return sysExpenseRepository.findByCnameOrderById(cname)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findMaxCcode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findMaxCcode() {
        return sysExpenseRepository.findMaxCcode()
                .map(item->R.ok().setResult(item));
    }

    @GetMapping("findBukongCcode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findBukongCcode() {
        return sysExpenseRepository.findBukongCcode()
                .map(item->R.ok().setResult(item));
    }

}

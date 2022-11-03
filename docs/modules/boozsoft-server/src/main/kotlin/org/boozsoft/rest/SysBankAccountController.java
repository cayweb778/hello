package org.boozsoft.rest;

import cn.hutool.core.util.IdUtil;
import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.account.SysBankAccount;
import org.boozsoft.repo.SysBankAccountRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/sysBankAccount")
public class SysBankAccountController {

    @Autowired
    SysBankAccountRepository sysBankAccountRepository;

    @GetMapping("findAll")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAll(){
        return sysBankAccountRepository.findAll()
                .collectList().map(o -> R.ok().setResult(o));
    }

    @GetMapping("findByStatus")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByStatus(){
        return sysBankAccountRepository.findByStatusOrderById("1")
                .collectList().map(o -> R.ok().setResult(o));
    }

    @PostMapping
    @ApiOperation(value = "新增或修改", notes = "传入code")
    public Mono save(@RequestBody SysBankAccount object){
        if (object.getCcode()==null || object.getCcode().equals("")){
            object.setCcode(IdUtil.objectId());
        }
        if (object.getStatus()==null || object.getStatus().equals("")) {
            object.setStatus("1");
        }
        return sysBankAccountRepository.save(object)
                .map(o -> R.ok().setResult(o));
    }

    @DeleteMapping
    @ApiOperation(value = "根据id删除数据", notes = "传入code")
    public Mono delete(String id){
        return sysBankAccountRepository.deleteById(id)
                .then(Mono.just(R.ok()));
    }

    @PostMapping("editFlag")
    @ApiOperation(value = "停用启用操作", notes = "传入code")
    public Mono editFlag(@RequestBody SysBankAccount object) {
        if (object.getStatus().equals("1")) {
            object.setStatus("0");
        } else {
            object.setStatus("1");
        }
        return sysBankAccountRepository.save(object)
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping("excel")
    @ApiOperation(value = "前台解析excel,后台直接保存集合", notes = "传入code")
    @Transactional
    public Mono excel(@RequestBody List<SysBankAccount> object){
        return Flux.fromIterable(object).map(item -> {
                    if (item.getCcode()==null || item.getCcode().equals("")){
                        item.setCcode(IdUtil.objectId());
                    }
                    if (item.getStatus()==null || item.getStatus().equals("")) {
                        item.setStatus("1");
                    }
                    return item;
                })
                .collectList()
                .flatMapMany(sysBankAccountRepository::saveAll)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findByCname")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByCname(String cname){
        return sysBankAccountRepository.findByCnameOrderById(cname)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

}

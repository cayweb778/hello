package org.boozsoft.rest;

import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.account.BankStatementHeader;
import org.boozsoft.repo.BankStatementHeadRepository;
import org.boozsoft.utils.CollectOfUtils;
import org.springbooz.core.tool.result.R;

import org.springbooz.datasource.r2dbc.annotation.SCHEMA_TYPE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("bankStatementHeader")
public class BankStatementHeaderController {

    @Autowired
    public BankStatementHeadRepository repository;

    @PostMapping
    @ApiOperation(value = "save", notes = "传入code")
    public Mono<R> save(@RequestBody BankStatementHeader item){
        /*String qiyongDate = item.getQiyongDate();
        if (qiyongDate!=null && !qiyongDate.equals("")){
            item.setQiyongDate(qiyongDate.substring(0,10));
        }*/
        if (item.getFlag()==null || item.getFlag().equals("")){
            item.setFlag("0");
        }
        return repository.save(item).map(o -> R.ok().setResult(o));

    }

    @DeleteMapping("del")
    @ApiOperation(value = "delete", notes = "传入code")
    public Mono<R> delete(String id){
        return repository.deleteById(id)
                .thenReturn(R.ok());
    }

    @GetMapping("findAll")
    @ApiOperation(value = "findAll", notes = "传入code")
    public Mono<R> findAll(){
        return repository.findAll()
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findById")
    @ApiOperation(value = "findById", notes = "传入code")
    public Mono<R> findById(String id){
        return repository.findAllById(CollectOfUtils.listof(id))
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findByYearAndKemu")
    @ApiOperation(value = "findByYearAndKemu", notes = "传入code")
    public Mono<R> findByYearAndKemu(String year,String kemuCode){
        return repository.findByIyearAndCcodeOrderById(year, kemuCode)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

}

package org.boozsoft.rest;

import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.account.AccvoucherSettingClass;
import org.boozsoft.repo.AccvoucherSettingClassRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/AccvoucherSettingClass")
public class AccvoucherSettingClassController {

    @Autowired
    AccvoucherSettingClassRepository repository;


    @GetMapping("findAll")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAll(Pageable pageable) {
        return repository.findAllByOrderByClassCode().collectList().map(o -> R.ok().setResult(o));
    }

    @GetMapping("findMaxClassCode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findMaxClassCode() {
        return repository.findMaxClassCode()
                .map(item->R.ok().setResult(item));
    }

    @GetMapping("findBukongClassCode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findBukongClassCode() {
        return repository.findBukongClassCode()
                .map(item->R.ok().setResult(item));
    }

    @GetMapping("findById")
    @ApiOperation(value = "查询Id", notes = "传入Id")
    public Mono<R> findById(@RequestBody Map params) {
        return repository.findById(params.get("id").toString()).map(o -> R.ok().setResult(o));
    }

    @PostMapping("save")
    @ApiOperation(value = "保存（新增或修改）", notes = "传入实体类")
    public Mono<R> save(@RequestBody AccvoucherSettingClass entity) {
        return repository.save(entity).map(o -> R.ok().setResult(o));
    }


    @DeleteMapping("deleteById")
    @ApiOperation(value = "通过ID删除", notes = "传入Id")
    public Mono<R> deleteById(@RequestBody Map params) {
        return repository.deleteById(params.get("id").toString()).thenReturn(R.ok());
    }


}
package org.boozsoft.rest;

import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.account.AccvoucherSetting;
import org.boozsoft.domain.entity.account.AccvoucherSettingEntry;
import org.boozsoft.repo.AccvoucherSettingEntryRepository;
import org.boozsoft.repo.AccvoucherSettingRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/AccvoucherSetting")
public class AccvoucherSettingController {

    @Autowired
    AccvoucherSettingRepository repository;
    @Autowired
    AccvoucherSettingEntryRepository entryRepository;


    @GetMapping("findAll")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAll(Pageable pageable, String classCode) {
        return repository.findAllByOrderByCcode().collectList()
                .map(list -> {
                    if (StrUtil.isNotBlank(classCode) && !classCode.equals("0") && !classCode.equals("undefined")) {
                        return list.stream().filter(item -> classCode.contains(item.getClassCode())).collect(Collectors.toList());
                    }
                    return list;
                })
                .map(R::page);
    }

    @GetMapping("findMaxCcode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findMaxCcode() {
        return repository.findMaxCcode()
                .map(item->R.ok().setResult(item));
    }

    @GetMapping("findBukongCcode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findBukongCcode() {
        return repository.findBukongCcode()
                .map(item->R.ok().setResult(item));
    }

    @GetMapping("findById")
    @ApiOperation(value = "查询Id", notes = "传入Id")
    public Mono<R> findById(@RequestBody Map params) {
        return repository.findById(params.get("id").toString()).map(o -> R.ok().setResult(o));
    }

    @PostMapping("save")
    @ApiOperation(value = "保存（新增或修改）", notes = "传入实体类")
    public Mono<R> save(@RequestBody AccvoucherSetting entity) {
        Mono<AccvoucherSetting> mono1 = repository.save(entity);
        // 分录的逻辑
        Mono<List<AccvoucherSettingEntry>> mono2 = Mono.fromSupplier(
                        () -> Flux.fromIterable(entity.getTable())
                                .map(item -> {
                                    item.setParentId(entity.getId());
                                    return item;
                                }))
                .flatMapMany(item->item)
                .collectList()
                .flatMapMany(entryRepository::saveAll)
                .collectList();
        return mono1
                .flatMap(item->mono2)
                .map(o -> R.ok().setResult(o));
    }


    @DeleteMapping("deleteById")
    @ApiOperation(value = "通过ID删除", notes = "传入Id")
    public Mono<R> deleteById(@RequestBody Map params) {
        return repository.deleteById(params.get("id").toString()).thenReturn(R.ok());
    }

    @PostMapping("saveList")
    @ApiOperation(value = "保存集合（新增或修改）", notes = "传入实体类集合")
    public Mono<R> saveList(@RequestBody List<AccvoucherSetting> entity) {
        return repository.saveAll(entity).collectList().map(o -> R.ok().setResult(o));
    }

    @GetMapping("findAllByParentIdOrderByNum")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAllByParentIdOrderByNum(String id) {
        return entryRepository.findAllByParentIdOrderByNum(id)
                .collectList()
                .map(item->R.ok().setResult(item));
    }

    @DeleteMapping("deleteByParentId")
    @ApiOperation(value = "通过ID删除", notes = "传入Id")
    public Mono<R> deleteByParentId(String id) {
        return entryRepository.deleteByParentId(id).then(Mono.just(R.ok()));
    }

    @DeleteMapping("deleteEntryById")
    @ApiOperation(value = "通过ID删除", notes = "传入Id")
    public Mono<R> deleteEntryById(String id) {
        return entryRepository.deleteById(id).then(Mono.just(R.ok()));
    }



    @PostMapping("findAllByCcode")
    public Mono<R> findAllByCcode(String ccode) {
        return repository.findAllByCcode(ccode).collectList().map(o -> R.ok().setResult(o));
    }
}
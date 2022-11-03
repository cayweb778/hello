package org.boozsoft.rest;

import cn.hutool.core.util.IdUtil;
import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.SysLog;
import org.boozsoft.domain.entity.account.SysPsnType;
import org.boozsoft.domain.entity.account.SysPsnTypeRollback;
import org.boozsoft.repo.SysPsnTypeRepository;
import org.boozsoft.repo.SysPsnTypeRollbackRepository;
import org.boozsoft.service.SysLogService;
import org.springbooz.core.tool.result.R;

import org.springbooz.datasource.r2dbc.annotation.SCHEMA_TYPE;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/sys/psnType")
public class SysPsnTypeController {

    //    @Autowired
//    OAuth2ClientProperties oAuth2ClientProperties;
    @Autowired
    SysPsnTypeRepository sysPsnTypeRepository;

    @GetMapping("findAll")
    @ApiOperation(value = "查询列表", notes = "传入code")

    public Mono<R> findAll(Pageable pageable) {
        return sysPsnTypeRepository.findAllByOrderByPsnTypeCode().collectList().map(R::page);
    }

    @GetMapping("findById")
    @ApiOperation(value = "查询", notes = "传入code")

    public Mono findById(String id) {
        Mono mono = sysPsnTypeRepository.findById(id);
        return mono;
    }

    @PostMapping
    @ApiOperation(value = "新增或修改", notes = "传入code")

    public Mono save(@RequestBody SysPsnType object) {
        if (object.getUniqueCode() == null || object.getUniqueCode().equals("")) {
            object.setUniqueCode(IdUtil.objectId());
        }
        //增加
        if (object.getId() == null || object.getId().equals("")) {
            return sysPsnTypeRepository.save(object)
                    .map(o -> R.ok().setResult(o));
        }
        return sysPsnTypeRepository.save(object)
                .map(o -> R.ok().setResult(o));
    }

    @DeleteMapping
    @ApiOperation(value = "删除", notes = "传入code")

    public Mono delete(@RequestBody SysPsnType object) {
        return sysPsnTypeRepository.deleteById(object.getId())
                .then(Mono.just(R.ok()));
    }

    @PostMapping("excel")
    @ApiOperation(value = "前台解析excel,后台直接保存集合", notes = "传入code")
    @Transactional
    public Mono excel(@RequestBody List<SysPsnType> object) {
        return Flux.fromIterable(object).map(item -> {
                    if (item.getUniqueCode() == null || item.getUniqueCode().equals("")) {
                        item.setUniqueCode(IdUtil.objectId());
                    }
                    return item;
                })
                .collectList()
                .flatMapMany(sysPsnTypeRepository::saveAll)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findByUniqueCode")
    @ApiOperation(value = "根据唯一码查询", notes = "传入code")
    public Mono findByUniqueCode(String uniqueCode) {
        return sysPsnTypeRepository.findByUniqueCode(uniqueCode)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping("acceptSave")
    @ApiOperation(value = "引入", notes = "传入code")
    public Mono acceptSave(@RequestBody SysPsnType object) {
        object.setId(null);
        return sysPsnTypeRepository
                .findByUniqueCode(object.getUniqueCode())
                .collectList()
                .flatMap(item -> item.size() > 0 ? Mono.just(item.get(0)) : sysPsnTypeRepository.save(object))
                .map(R::ok);
    }

}

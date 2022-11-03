package org.boozsoft.rest;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.boozsoft.domain.entity.AppGroupPwdRule;
import org.boozsoft.domain.entity.SysDataAuthSwith;
import org.boozsoft.repo.SysDataAuthSwithRepository;
import org.boozsoft.repo.SysPwdRuleRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/sys/dataAuthSwith")
@Api(value = "dataSee/findDataSeeAuthor数据权限控制", tags = "API系统：数据权限控制")
public class SysDataAuthSwithController {
    @Autowired
    SysDataAuthSwithRepository sysDataAuthSwithRepository;

    @PostMapping("findByAccIdAndIyear")
    public Mono<R> findByAccIdAndIyear(String accId,String iyear) {
        return sysDataAuthSwithRepository.findAllByRecordNumAndIyear(accId,iyear).map(o -> R.ok().setResult(o)).defaultIfEmpty(R.ok().setResult("不存在"));
    }

    @PostMapping("save")
    public Mono<R> savePwdRule(@RequestBody SysDataAuthSwith data) {
        return sysDataAuthSwithRepository.save(data).map(o -> R.ok().setResult(o));
    }

    @PostMapping("delById")
    public Mono<R> delById(String id) {
        return sysDataAuthSwithRepository.deleteById(id).then(Mono.just(R.ok()));
    }
}

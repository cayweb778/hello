package org.boozsoft.rest;

import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.boozsoft.domain.entity.AppGroupPwdRule;
import org.boozsoft.repo.SysPwdRuleRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName : SysPwdRuleController
 * @Description : 密码策略
 * @Author : miao
 * @Date: 2021-04-01 09:25
 */
@Slf4j
@RestController
@RequestMapping("/sys/pwdrule")
@Api(value = "密码策略", tags = "API系统：密码策略")
public class SysPwdRuleController {
    @Autowired
    SysPwdRuleRepository sysPwdRuleRepository;

    @PostMapping("/findByPwdName")
    public Mono<R> findByPwdName(String pwdName) {
        return sysPwdRuleRepository.findByPwdName(pwdName).map(o -> R.ok().setResult(o));
    }
    @PostMapping("/findAll")
    public Mono<R> findAll(@RequestBody Map map) {
        String searchValue=map.size()>0?map.get("searchValue").toString():"";
        return sysPwdRuleRepository.findAll().collectList()
                .flatMap(list->{
                    if(StrUtil.isNotBlank(searchValue)){
                        list=list.stream().filter(v->v.getPwdName().equals(searchValue)).collect(Collectors.toList());
                    }
                  return Mono.just(list);
                })
                .map(o -> R.ok().setResult(o));
    }


    @PostMapping("savePwdRule")
    public Mono<R> savePwdRule(@RequestBody Mono<AppGroupPwdRule> pwd) {
        return pwd.flatMap(sysPwdRuleRepository::save)
                .map(o -> R.ok().setResult(o));
    }

    @DeleteMapping("delPwdRule")
    public Mono<R> delPwdRule(@RequestBody List<AppGroupPwdRule> pwd) {
        return sysPwdRuleRepository.deleteAll(pwd).then(Mono.just(R.ok()));
    }
}

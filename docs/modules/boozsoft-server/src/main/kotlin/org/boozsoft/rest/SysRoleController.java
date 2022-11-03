package org.boozsoft.rest;

import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.boozsoft.domain.entity.AppGroupSysRole;
import org.boozsoft.repo.SysRoleRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/sys/role")
@Api(value = "系统角色表", tags = "API系统：系统角色表")
public class SysRoleController {
    @Autowired
    SysRoleRepository sysRoleRepository;

    @PostMapping("/findByRoleName")
    public Mono<R> findByPwdName(String roleName) {
        return sysRoleRepository.findByRoleName(roleName).map(o -> R.ok().setResult(o));
    }

    @GetMapping("/findAll")
    public Mono<R> findAll() {
        return sysRoleRepository.findAll().collectList()
                .flatMap(list->{
                    list.stream().forEach(v->{
                        if (StrUtil.isNotBlank(v.getRoleRange()))
                        v.setRoleRangeName(v.getRoleRange().replaceAll("1","集团").replaceAll("2","组织").replaceAll("3","公司"));
                    });
                    return Mono.just(list);
                })
                .map(o -> R.ok().setResult(o));
    }


    @PostMapping("saveSysRole")
    public Mono<R> saveSysRole(@RequestBody AppGroupSysRole role) {
        return sysRoleRepository.findByMaxRoleNum()
                .map(max->{
                    int maxpwdnum=Integer.valueOf(max)+1;
                    if(StringUtils.isBlank(role.getId())){
                        role.setRoleNum(String.valueOf(maxpwdnum<10?"0"+maxpwdnum:maxpwdnum));
                        role.setRoleAddTime(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                    }
                    return role;
                })
                .flatMap(sysRoleRepository::save)
                .map(o -> R.ok().setResult(o));
    }

    @DeleteMapping("delRole")
    public Mono<R> delPwdRule(@RequestBody List<AppGroupSysRole> pwd) {
        return sysRoleRepository.deleteAll(pwd).then(Mono.just(R.ok(pwd)));
    }
}

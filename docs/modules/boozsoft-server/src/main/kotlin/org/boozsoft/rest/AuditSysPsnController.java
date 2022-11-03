package org.boozsoft.rest;

import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.account.AuditSysPsn;
import org.boozsoft.domain.entity.group.GroupAuditRole;
import org.boozsoft.repo.AuditSysPsnRepository;
import org.boozsoft.repo.group.GroupAuditRoleRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auditSysPsn")
public class AuditSysPsnController {

    @Autowired
    AuditSysPsnRepository auditSysPsnRepository;

    @GetMapping("findAll")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAll() {
        return auditSysPsnRepository.findAll()
                .collectList()
                .map(R::ok);
    }


    @PostMapping
    @ApiOperation(value = "添加审计", notes = "传入code")
    public Mono<R> save(@RequestBody AuditSysPsn entity) {
        return auditSysPsnRepository.save(entity)
                .map(R::ok);
    }
}

package org.boozsoft.rest.group;

import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.group.GroupAuditOperator;
import org.boozsoft.domain.entity.group.GroupAuditRole;
import org.boozsoft.repo.group.GroupAuditOperatorRepository;
import org.boozsoft.repo.group.GroupAuditRoleRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/audit-role")
public class GroupAuditRoleController {

    @Autowired
    GroupAuditRoleRepository groupAuditRoleRepository;

    @GetMapping("findAll")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAll() {
        return groupAuditRoleRepository.findAll().collectList().map(R::ok);
    }


    @PostMapping
    @ApiOperation(value = "添加审计", notes = "传入code")
    public Mono<R> save(@RequestBody GroupAuditRole entity) {
        entity.setAuditId(entity.getId()).setId(null);
        return groupAuditRoleRepository.save(entity).map(R::ok);
    }
}

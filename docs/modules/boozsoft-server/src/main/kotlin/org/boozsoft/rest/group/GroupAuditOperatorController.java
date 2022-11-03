package org.boozsoft.rest.group;

import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.group.GroupAccPrint;
import org.boozsoft.domain.entity.group.GroupAuditOperator;
import org.boozsoft.repo.group.GroupAccPrintRepository;
import org.boozsoft.repo.group.GroupAuditOperatorRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/audit-operator")
public class GroupAuditOperatorController {

    @Autowired
    GroupAuditOperatorRepository groupAuditOperatorRepository;

    @GetMapping("findAll")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAll() {
        return groupAuditOperatorRepository.findAll()
                .collectList().map(R::ok);
    }


    @PostMapping
    @ApiOperation(value = "添加审计", notes = "传入code")
    public Mono<R> save(@RequestBody GroupAuditOperator entity) {
        entity.setAuditId(entity.getId()).setId(null);
        return groupAuditOperatorRepository.save(entity).map(R::ok);
    }
}

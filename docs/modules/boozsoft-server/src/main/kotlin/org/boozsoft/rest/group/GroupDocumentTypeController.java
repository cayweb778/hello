package org.boozsoft.rest.group;

import io.swagger.annotations.ApiOperation;
import org.boozsoft.repo.group.GroupDocumentTypeRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/groupDocumentType")
public class GroupDocumentTypeController {

    @Autowired
    GroupDocumentTypeRepository repository;

    @GetMapping("findAll")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAll() {
        return repository.findAllByOrderByCcode()
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

}

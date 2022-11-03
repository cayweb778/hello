package org.boozsoft.rest.group;

import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.group.GroupReportAcc;
import org.boozsoft.repo.group.GroupReportAccRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/groupReportAcc")
public class GroupReportAccController {

    @Autowired
    GroupReportAccRepository repository;

    @GetMapping("findAll")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAll() {
        return repository.findAll().collectList().map(R::page);
    }

    @GetMapping("findByAccStandardOrderByNum")
    @ApiOperation(value = "查询", notes = "传入code")
    public Mono findByAccStandardOrderByNum(String accStandard) {
        return repository.findByAccStandardOrderByNum(accStandard)
                .collectList().map(o -> R.ok().setResult(o));
    }

    @PostMapping("save")
    @ApiOperation(value = "新增或修改", notes = "传入code")
    public Mono save(@RequestBody GroupReportAcc object) {
        return repository.save(object)
                .map(o -> R.ok().setResult(o));
    }

    @DeleteMapping("deleteById")
    @ApiOperation(value = "删除", notes = "传入code")
    public Mono deleteById(String id) {
        return repository.deleteById(id)
                .then(Mono.just(R.ok()));
    }

}

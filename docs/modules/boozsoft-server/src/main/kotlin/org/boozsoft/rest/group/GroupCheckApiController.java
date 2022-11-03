package org.boozsoft.rest.group;

import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.SysCheckApi;
import org.boozsoft.domain.entity.SysOcrApi;
import org.boozsoft.repo.SysCheckApiRepository;
import org.boozsoft.repo.SysOcrApiRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/check")
public class GroupCheckApiController {

    @Autowired
    SysCheckApiRepository sysCheckApiRepository;

    @GetMapping("findAll")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAll() {
        return sysCheckApiRepository.findAll()
                .collectList()
                .map(o-> R.ok().setResult(o));
    }

    @PostMapping
    @ApiOperation(value = "新增或修改", notes = "传入code")
    public Mono save(@RequestBody SysCheckApi object) {
        return sysCheckApiRepository.save(object)
                .map(item -> R.ok().setResult(item));
    }

}
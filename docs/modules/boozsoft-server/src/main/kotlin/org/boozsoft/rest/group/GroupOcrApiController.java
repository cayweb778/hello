package org.boozsoft.rest.group;

import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.SysOcrApi;
import org.boozsoft.domain.entity.group.GroupProjectItem;
import org.boozsoft.repo.SysCheckApiRepository;
import org.boozsoft.repo.SysOcrApiRepository;
import org.boozsoft.repo.group.GroupProjectItemRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/ocr")
public class GroupOcrApiController {

    @Autowired
    SysOcrApiRepository sysOcrApiRepository;


    @GetMapping("findAll")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAll() {
        return sysOcrApiRepository.findAll()
                .collectList()
                .map(o-> R.ok().setResult(o));
    }

    @PostMapping
    @ApiOperation(value = "新增或修改", notes = "传入code")
    public Mono save(@RequestBody SysOcrApi object) {
        return sysOcrApiRepository.save(object)
                .map(item -> R.ok().setResult(item));
    }

}
package org.boozsoft.rest.group;

import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.group.GroupFileEncodingRules;
import org.boozsoft.domain.entity.group.GroupSysUnitOfMea;
import org.boozsoft.repo.group.GroupFileEncodingRulesRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/group/fileEncodingRules")
public class GroupFileEncodingRulesController {

    @Autowired
    GroupFileEncodingRulesRepository groupFileEncodingRulesRepository;

    @PostMapping("save")
    @ApiOperation(value = "保存", notes = "传入object")
    public Mono save(@RequestBody GroupFileEncodingRules obj) {
        return groupFileEncodingRulesRepository.save(obj).thenReturn(R.ok());
    }

    @GetMapping("load/{type}")
    @ApiOperation(value = "加载", notes = "type")
    public Mono load(@PathVariable String type) {
        return groupFileEncodingRulesRepository.findByFileType(type)
                .defaultIfEmpty(new GroupFileEncodingRules())
                .map(v-> R.ok().setResult(v));
    }

}

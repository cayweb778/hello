package org.boozsoft.rest.group;

import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.group.GroupFileEncodingRules;
import org.boozsoft.domain.entity.group.GroupReportEncodingRules;
import org.boozsoft.repo.group.GroupFileEncodingRulesRepository;
import org.boozsoft.repo.group.GroupReportEncodingRulesRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/group/reportEncodingRules")
public class GroupReportEncodingRulesController {

    @Autowired
    GroupReportEncodingRulesRepository groupFileEncodingRulesRepository;

    @PostMapping("save")
    @ApiOperation(value = "保存", notes = "传入object")
    public Mono save(@RequestBody GroupReportEncodingRules obj) {
        return groupFileEncodingRulesRepository.save(obj).thenReturn(R.ok());
    }

    @GetMapping("load/{type}")
    @ApiOperation(value = "加载", notes = "type")
    public Mono load(@PathVariable String type) {
        return groupFileEncodingRulesRepository.findByFileType(type)
                .defaultIfEmpty(new GroupReportEncodingRules())
                .map(v-> R.ok().setResult(v));
    }

}

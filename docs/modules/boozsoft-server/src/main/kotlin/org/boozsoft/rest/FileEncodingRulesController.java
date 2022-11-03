package org.boozsoft.rest;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.FileEncodingRules;
import org.boozsoft.domain.entity.ReportEncodingRules;
import org.boozsoft.domain.entity.SysPeriod;
import org.boozsoft.domain.entity.account.Iperiod;
import org.boozsoft.domain.entity.group.GroupFileEncodingRules;
import org.boozsoft.repo.AccCloseRepository;
import org.boozsoft.repo.FileEncodingRulesRepository;
import org.boozsoft.repo.IperiodRepository;
import org.boozsoft.repo.SysPeriodRepository;
import org.boozsoft.repo.group.GroupFileEncodingRulesRepository;
import org.boozsoft.service.SysLogService;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/sys/fileEncodingRules")
public class FileEncodingRulesController {

    @Autowired
    FileEncodingRulesRepository fileEncodingRulesRepository;

    @PostMapping("save")
    @ApiOperation(value = "保存", notes = "传入object")
    public Mono save(@RequestBody FileEncodingRules obj) {
        return fileEncodingRulesRepository.save(obj).thenReturn(R.ok());
    }

    @GetMapping("load/{type}")
    @ApiOperation(value = "加载", notes = "type")
    public Mono load(@PathVariable String type) {
        return fileEncodingRulesRepository.findByFileType(type)
                .defaultIfEmpty(new FileEncodingRules())
                .map(v-> R.ok().setResult(v));
    }


    @GetMapping("init")
    @ApiOperation(value = "初始化", notes = "初始化")
    public Mono init() {

        //系统编码
        String[] arr =  new String[]{"1-1","1-2","1-3","1-4","2-0","4-0"};

        List<FileEncodingRules> list = new ArrayList<>();

        Arrays.stream(arr).forEach(v->{
            FileEncodingRules f =  new FileEncodingRules();
            f.setFileType(v);
            f.setCodeWay("0");
            f.setIsManual("false");
            f.setAutoNum("false");
            f.setSerialNumLength("4");
            f.setSerialNumStr("1");
            f.setShowRules("1");
            f.setDelimiter("1");
            f.setPrefixOneIs("false");
            f.setPrefixTwoIs("false");
            f.setPrefixThreeIs("false");
            f.setPrefixThreeIs("false");
            //f.setShowRules("0001");
            list.add(f);
        });

        return fileEncodingRulesRepository.saveAll(list).collectList()
                .map(v-> R.ok().setResult(v));
    }

}

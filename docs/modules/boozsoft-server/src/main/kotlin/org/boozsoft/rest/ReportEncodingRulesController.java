package org.boozsoft.rest;

import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.FileEncodingRules;
import org.boozsoft.domain.entity.ReportEncodingRules;
import org.boozsoft.repo.FileEncodingRulesRepository;
import org.boozsoft.repo.ReportEncodingRulesRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/sys/reportEncodingRules")
public class ReportEncodingRulesController {

    @Autowired
    ReportEncodingRulesRepository fileEncodingRulesRepository;

    @PostMapping("save")
    @ApiOperation(value = "保存", notes = "传入object")
    public Mono save(@RequestBody ReportEncodingRules obj) {
        return fileEncodingRulesRepository.save(obj).thenReturn(R.ok());
    }

    @GetMapping("load/{type}")
    @ApiOperation(value = "加载", notes = "type")
    public Mono load(@PathVariable String type) {
        return fileEncodingRulesRepository.findByFileType(type)
                .defaultIfEmpty(new ReportEncodingRules())
                .map(v-> R.ok().setResult(v));
    }

    @GetMapping("init")
    @ApiOperation(value = "初始化", notes = "初始化")
    public Mono init() {

        //单据编码
        List<ReportEncodingRules> list = new ArrayList<>();

        String[] arr2 =  new String[]{
                "1-1andPZ",
                "2-0andAC","2-1andAA","2-2andAS","2-3andAR","2-4andAB","2-5andAQ","2-6andAD",
                "3-0andPQ","3-1andPS","3-2andPB",
                "3-4andSO","3-6andSA","3-7andSB",
                "3-10andII","3-11andIO","3-12andWL","3-13andIC","3-14andPD","3-15andAL","3-16andBC","3-17andJR","3-18andJZ", "3-19andJH","3-20andWC","3-21andQT","3-22andJC","3-23andJG",
                "3-3andYF","3-4andFK","3-8andYS","3-9andSK",
        };

        Arrays.stream(arr2).forEach(v->{
            ReportEncodingRules f =  new ReportEncodingRules();
            String  c = v.split("and")[0];
            String  d = v.split("and")[1];
            f.setFileType(c);
            f.setCodeWay("0");
            f.setIsManual("false");
            f.setAutoNum("false");
            f.setSerialNumLength("4");
            f.setSerialNumStr("1");
            f.setShowRules("1");
            f.setDelimiter("3");

            f.setPrefixOne("3-0");//默认编码标识
            f.setPrefixOneLength("2");
            f.setPrefixOneCustomize(d);
            f.setPrefixOneIs("true");

            f.setPrefixTwo("88");
            f.setPrefixTwoLength("6");
            f.setPrefixTwoCustomize("");
            f.setPrefixTwoIs("true");

            f.setPrefixThreeIs("false");

            String s = LocalDate.now().toString().substring(0, 6).replace("-", "");//2022-06-01
            f.setShowRules(d+"-"+s+"-");
            list.add(f);
        });

        return fileEncodingRulesRepository.saveAll(list).collectList()
                .map(v-> R.ok().setResult(v));
    }

}

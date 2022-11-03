package org.boozsoft.rest.fuzhu.duo_fuzhu;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.boozsoft.service.impl.fuzhu.duo_fuzhu.DuoFuzhuServiceImpl;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/duo_fuzhuyue")
@Api(value = "多辅助余额表", tags = "API系统：多辅助余额表")
public class DuoFuzhuYueController {
    @Autowired
    DuoFuzhuServiceImpl duoFuzhuService;


    @PostMapping("findAll")
    @ApiOperation(value = "科目余额表", notes = "")
    public Mono<R> findAll(@RequestBody Map map, Pageable pageable){
        // 当前页
        int page= map.get("page")==null?1:Integer.parseInt(map.get("page").toString());
        // 页多少条
        int size= map.get("size")==null?10000:Integer.parseInt(map.get("size").toString());
        String ishaveRjz=map.get("ishaveRjz").toString();         // 是否包含未记账  0否/1是
        String strDate=map.get("strDate").toString();   // 起始期间
        String endDate=map.get("endDate").toString();   // 结束期间
        String km=map.get("km").toString();             // 科目
        String styleValue=map.get("styleValue").toString();             // 科目分类
        String fuzhu=map.get("fuzhu").toString();
        String jsonStr=map.get("map").toString();

        return duoFuzhuService.findAll(pageable,strDate,endDate,styleValue,map.get("pzType").toString(),km,ishaveRjz,page,size,jsonStr, Arrays.asList(fuzhu.split(",")));
    }
}

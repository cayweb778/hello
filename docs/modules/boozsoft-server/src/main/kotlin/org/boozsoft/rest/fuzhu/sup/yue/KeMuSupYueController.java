package org.boozsoft.rest.fuzhu.sup.yue;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.boozsoft.service.impl.fuzhu.sup.yue.KmYueSupServiceImpl;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/KeMuSupYue")
@Api(value = "客户科目明细账", tags = "API系统：客户科目明细账")
public class KeMuSupYueController {
    @Autowired
    KmYueSupServiceImpl kmYueSupService;

    @PostMapping("findAll")
    public Mono<R> findAll(@RequestBody Map map, Pageable pageable){
        String database=map.get("database").toString();       // 开始客户
        String minDept=map.get("minDept").toString();       // 开始客户
//        String maxDept=map.get("maxDept").toString();       // 结束部门
        String km=map.get("km").toString();       // 部门
        String strDate=map.get("strDate").toString();   // 起始期间
        String endDate=map.get("endDate").toString();   // 结束期间
        String bz=map.get("bz").toString();             // 币种
        String ishaveRjz=map.get("ishaveRjz").toString();         // 是否包含未记账  0否/1是
        String styleValue=map.get("styleValue").toString();         // 科目类型
        Map<String, String> searchMap = ((HashMap<String,  String>) map.get("searchConditon"));
        Map<String, String> filterMap = ((HashMap<String, String>) map.get("filterConditon"));
        return  kmYueSupService.findAll(pageable,minDept,km,strDate,endDate,bz,ishaveRjz,styleValue,searchMap,filterMap,database);
    }

    /**
     * 按页签导出
     * @param map
     * @param pageable
     * @return
     */
    @PostMapping("exportAll")
    public Mono<R> exportAll(@RequestBody Map map, Pageable pageable){
        String strDate=map.get("strDate").toString();   // 起始期间
        String endDate=map.get("endDate").toString();   // 结束期间
        String bz=map.get("bz").toString();             // 币种
        String ishaveRjz=map.get("ishaveRjz").toString();         // 是否包含未记账  0否/1是
        String styleValue=map.get("styleValue").toString();         // 科目类型
        List<String> ccodeList= Arrays.asList(map.get("ccodeList").toString().split(","));
        return  kmYueSupService.exportAll(strDate,endDate,bz,ishaveRjz,styleValue,ccodeList);
    }
    @PostMapping("exportAll2")
    public Mono<R> exportAll2(@RequestBody Map map, Pageable pageable){
        String strDate=map.get("strDate").toString();   // 起始期间
        String endDate=map.get("endDate").toString();   // 结束期间
        String bz=map.get("bz").toString();             // 币种
        String ishaveRjz=map.get("ishaveRjz").toString();         // 是否包含未记账  0否/1是
        String styleValue=map.get("styleValue").toString();         // 科目类型
        List<String> ccodeList= Arrays.asList(map.get("ccodeList").toString().split(","));
        return  kmYueSupService.exportAll2(strDate,endDate,bz,ishaveRjz,styleValue,ccodeList);
    }
}

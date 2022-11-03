package org.boozsoft.rest.fuzhu.pro.mx;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.boozsoft.service.impl.fuzhu.pro.mx.ProKmMxServiceImpl;
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

/**
 * @ClassName : KeMuMingXiController
 * @Description : 科目明细账
 * @Author : miao
 * @Date: 2021-06-23 14:54
 */
@Slf4j
@RestController
@RequestMapping("/ProKeMuMingXi")
@Api(value = "项目科目明细账", tags = "API系统：项目科目明细账")
public class ProKeMuMingXiController {
    @Autowired
    ProKmMxServiceImpl proKmMxService;

    @PostMapping("findAll")
    public Mono<R> findAll(@RequestBody Map map, Pageable pageable){
        String database=map.get("database").toString();
        String minDept=map.get("minDept").toString();       // 开始部门
//        String maxDept=map.get("maxDept").toString();       // 结束部门
        String km=map.get("km").toString();       // 部门
        String strDate=map.get("strDate").toString();   // 起始期间
        String endDate=map.get("endDate").toString();   // 结束期间
        String bz=map.get("bz").toString();             // 币种
        String ishaveRjz=map.get("ishaveRjz").toString();         // 是否包含未记账  0否/1是
        String styleValue=map.get("styleValue").toString();         // 科目类型
        Map<String, String> searchMap = ((HashMap<String,  String>) map.get("searchConditon"));
        Map<String, String> filterMap = ((HashMap<String, String>) map.get("filterConditon"));
        return  proKmMxService.findAll(pageable,minDept,km,strDate,endDate,bz,ishaveRjz,styleValue,searchMap,filterMap,database);
    }

    @PostMapping("exportAll")
    @ApiOperation(value = "科目明细账", notes = "")
    public Mono<R> exportAll(@RequestBody Map map, Pageable pageable){
        String strDate=map.get("strDate").toString();   // 起始期间
        String endDate=map.get("endDate").toString();   // 结束期间
        String bz=map.get("bz").toString();             // 币种
        String ishaveRjz=map.get("ishaveRjz").toString();         // 是否包含未记账  0否/1是
        String styleValue=map.get("styleValue").toString();         // 科目类型
        List<String> proList= Arrays.asList(map.get("cusList").toString().split(","));
        return  proKmMxService.exportAll(strDate,endDate,bz,ishaveRjz,styleValue,proList);
    }
    @PostMapping("exportAll2")
    @ApiOperation(value = "科目明细账", notes = "")
    public Mono<R> exportAll2(@RequestBody Map map, Pageable pageable){
        String strDate=map.get("strDate").toString();   // 起始期间
        String endDate=map.get("endDate").toString();   // 结束期间
        String bz=map.get("bz").toString();             // 币种
        String ishaveRjz=map.get("ishaveRjz").toString();         // 是否包含未记账  0否/1是
        String styleValue=map.get("styleValue").toString();         // 科目类型
        List<String> proList= Arrays.asList(map.get("cusList").toString().split(","));
        return  proKmMxService.exportAll2(strDate,endDate,bz,ishaveRjz,styleValue,proList);
    }
}

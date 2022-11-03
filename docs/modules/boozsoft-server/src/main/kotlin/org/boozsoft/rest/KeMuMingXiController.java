package org.boozsoft.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.boozsoft.repo.ParameterAccuracyRepository;
import org.boozsoft.repo.SysPeriodRepository;
import org.boozsoft.service.impl.KeMuMingXiServiceImpl;
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
@RequestMapping("/KeMuMingXi")
@Api(value = "科目明细账", tags = "API系统：科目明细账")
public class KeMuMingXiController {

    @Autowired
    SysPeriodRepository sysPeriodRepository;
    @Autowired
    KeMuMingXiServiceImpl keMuMingXiService;
    @Autowired
    ParameterAccuracyRepository parameterAccuracyRepository;

    @PostMapping("finByParameterAccuracy")
    public Mono<R>  finByParameterAccuracy(){
        return parameterAccuracyRepository.findAll().collectList().map(e->R.ok().setResult(e));
    }

    @PostMapping("findAll")
    @ApiOperation(value = "科目明细账", notes = "")
    public Mono<R> findAll(@RequestBody Map map){
        String database=map.get("database").toString();
        String ishaveRjz=map.get("ishaveRjz").toString();         // 是否包含未记账  0否/1是
        String bend=map.get("bend").toString();         // 是否末级 1否 2是
        String strDate=map.get("strDate").toString();   // 起始期间
        String endDate=map.get("endDate").toString();   // 结束期间
        String maxJc=map.get("maxJc").toString();       // 起始级次
        String minJc=map.get("minJc").toString();       // 结束级次
        String minKm=map.get("km").toString();       // 起始科目
        String timflg=map.get("timflg").toString();     // 是 期间 qj 还是 日期 rq
        String bz=map.get("bz").toString();             // 币种
        String digestStyle=map.get("digestStyle").toString();             // 摘要显示设置 0 标准辅助核算项编码 1 标准辅助核算项名称 2 扩展辅助核算项编码 3 扩展辅助核算项名称 4 结算方式名称 5 结算日期 6 票据号 7 结算单位
        String pzType=map.get("pzType").toString();             // 凭证类型
        Map<String, String> searchMap = ((HashMap<String,  String>) map.get("searchConditon"));
        Map<String, String> filterMap = ((HashMap<String, String>) map.get("filterConditon"));

        return  keMuMingXiService.findAll(bend,strDate,endDate,maxJc,minJc,minKm,timflg,ishaveRjz,bz,searchMap,filterMap,digestStyle,database,pzType);
    }

    @PostMapping("findAllDayBook")
    @ApiOperation(value = "科目日记账", notes = "")
    public Mono<R> findAllDayBook(@RequestBody Map map, Pageable pageable){
        String database=map.get("database").toString();
        String ishaveRjz=map.get("ishaveRjz").toString();         // 是否包含未记账  0否/1是
        String bend=map.get("bend").toString();         // 是否末级 1否 2是
        String strDate=map.get("strDate").toString();   // 起始期间
        String endDate=map.get("endDate").toString();   // 结束期间
        String maxJc=map.get("maxJc").toString();       // 起始级次
        String minJc=map.get("minJc").toString();       // 结束级次
        String minKm=map.get("km").toString();       // 起始科目
        String timflg=map.get("timflg").toString();     // 是 期间 qj 还是 日期 rq
        String bz=map.get("bz").toString();             // 币种
        String digestStyle=map.get("digestStyle").toString();             // 摘要显示设置 0 标准辅助核算项编码 1 标准辅助核算项名称 2 扩展辅助核算项编码 3 扩展辅助核算项名称 4 结算方式名称 5 结算日期 6 票据号 7 结算单位
        Map<String, String> searchMap = ((HashMap<String,  String>) map.get("searchConditon"));
        Map<String, String> filterMap = ((HashMap<String, String>) map.get("filterConditon"));
        return  keMuMingXiService.findAllDayBook(pageable,bend,strDate,endDate,maxJc,minJc,minKm,timflg,ishaveRjz,bz,searchMap,filterMap,digestStyle,database);
    }


    @PostMapping("findByCodeExportExcel")
    @ApiOperation(value = "所有科目按页签显示", notes = "")
    public Mono<R> findByCodeExportExcel(@RequestBody Map map){
        String code=map.get("code").toString();
        Map<String, Object> pageParameter = ((HashMap<String,  Object>) map.get("pageParameter"));
        String database=pageParameter.get("database").toString();
        String ishaveRjz=pageParameter.get("ishaveRjz").toString();         // 是否包含未记账  0否/1是
        String bend=pageParameter.get("bend").toString();         // 是否末级 1否 2是
        String strDate=pageParameter.get("strDate").toString();   // 起始期间
        String endDate=pageParameter.get("endDate").toString();   // 结束期间
        String maxJc=pageParameter.get("maxJc").toString();       // 起始级次
        String minJc=pageParameter.get("minJc").toString();       // 结束级次
        String minKm=pageParameter.get("km").toString();       // 起始科目
        String timflg=pageParameter.get("timflg").toString();     // 是 期间 qj 还是 日期 rq
        String bz=pageParameter.get("bz").toString();             // 币种
        String digestStyle=pageParameter.get("digestStyle").toString();             // 摘要显示设置 0 标准辅助核算项编码 1 标准辅助核算项名称 2 扩展辅助核算项编码 3 扩展辅助核算项名称 4 结算方式名称 5 结算日期 6 票据号 7 结算单位

        List<String> list= Arrays.asList(code.split(","));
       return keMuMingXiService.findByCodeExportExcel(bend,strDate,endDate,maxJc,minJc,list,timflg,ishaveRjz,bz,null,null,digestStyle,database).map(a -> R.ok().setResult(a));
    }
    @PostMapping("findByCodeExportExcel2")
    @ApiOperation(value = "所有科目显示在同一页签", notes = "")
    public Mono<R> findByCodeExportExcel2(@RequestBody Map map){
        String code=map.get("code").toString();
        Map<String, Object> pageParameter = ((HashMap<String,  Object>) map.get("pageParameter"));
        String ishaveRjz=pageParameter.get("ishaveRjz").toString();         // 是否包含未记账  0否/1是
        String bend=pageParameter.get("bend").toString();         // 是否末级 1否 2是
        String strDate=pageParameter.get("strDate").toString();   // 起始期间
        String endDate=pageParameter.get("endDate").toString();   // 结束期间
        String maxJc=pageParameter.get("maxJc").toString();       // 起始级次
        String minJc=pageParameter.get("minJc").toString();       // 结束级次
        String timflg=pageParameter.get("timflg").toString();     // 是 期间 qj 还是 日期 rq
        String bz=pageParameter.get("bz").toString();             // 币种

        List<String> list= Arrays.asList(code.split(","));
        return keMuMingXiService.findByCodeExportExcel2(bend,strDate,endDate,maxJc,minJc,list,timflg,ishaveRjz,bz).map(a -> R.ok().setResult(a));
    }
}

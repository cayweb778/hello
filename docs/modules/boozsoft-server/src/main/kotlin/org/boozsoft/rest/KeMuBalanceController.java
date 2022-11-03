package org.boozsoft.rest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.boozsoft.domain.entity.accstyle.AccStyle;
import org.boozsoft.domain.entity.codekemu.CodeKemu;
import org.boozsoft.service.KeMuBalanceService;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/KeMuBalance")
@Api(value = "科目余额表", tags = "API系统：科目余额表")
public class KeMuBalanceController {
    @Autowired
    KeMuBalanceService keMuBalanceService;


    @PostMapping("findAll")
    @ApiOperation(value = "科目余额表", notes = "")
    public Mono<R> findAll(@RequestBody Map map, Pageable pageable){
        // 当前页
        int page= map.get("page")==null||map.get("page").equals("")?1:Integer.parseInt(map.get("page").toString());
        // 页多少条
        int size= map.get("size")==null||map.get("size").equals("")?10000:Integer.parseInt(map.get("size").toString());

        String database=map.get("database").toString();
        String ishaveRjz=map.get("ishaveRjz").toString();         // 是否包含未记账  0否/1是
        String bend=map.get("bend").toString();         // 是否末级 1否 2是
        String strDate=map.get("strDate").toString();   // 起始期间
        String endDate=map.get("endDate").toString();   // 结束期间
        String minJc=map.get("minJc").toString();       // 起始级次
        String maxJc=map.get("maxJc").toString();       // 结束级次
        String minKm=map.get("minKm").equals("空")?"":map.get("minKm").toString();       // 起始科目
        String maxKm=map.get("maxKm").equals("空")?"":map.get("maxKm").toString();       // 结束科目
        String timflg=map.get("timflg").toString();     // 是 期间 qj 还是 日期 rq
        String bz=map.get("bz").toString();             // 币种
        String styleValue=map.get("styleValue").toString();             // 科目分类
        String querytype=map.get("querytype").toString();             // 集团还是公司
        String pzType=map.get("pzType")==null||map.get("pzType").equals("")?"记":map.get("pzType").toString(); // 集团模式默认记;根据凭证类别
        Map<String, String> searchMap = ((HashMap<String,  String>) map.get("searchConditon"));
        Map<String, String> filterMap = ((HashMap<String, String>) map.get("filterConditon"));

        String styleListJSON=map.get("cclass").toString();
        List<AccStyle> styleList= JSONArray.parseArray(styleListJSON, AccStyle.class);

        List<CodeKemu> kmList=JSONArray.parseArray(JSON.toJSONString(map.get("kmList")), CodeKemu.class);
        return  keMuBalanceService.findAll(page,size,pageable,bend,strDate,endDate,maxJc,minJc,maxKm,minKm,timflg,ishaveRjz,bz,styleValue,searchMap,filterMap,styleList,database,querytype,pzType,kmList);
    }
}

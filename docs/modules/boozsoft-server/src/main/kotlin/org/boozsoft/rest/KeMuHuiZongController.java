package org.boozsoft.rest;

import com.alibaba.fastjson.JSONArray;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.boozsoft.domain.entity.Accvoucher;
import org.boozsoft.domain.entity.accstyle.AccStyle;
import org.boozsoft.repo.AccvoucherRepository;
import org.boozsoft.service.impl.KeMuHuiZongServiceImpl;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@RestController
@RequestMapping("/KeMuHuiZong")
@Api(value = "科目汇总表", tags = "API系统：科目汇总表")
public class KeMuHuiZongController {
    @Autowired
    KeMuHuiZongServiceImpl keMuHuiZongService;
    @Autowired
    AccvoucherRepository accvoucherRepository;

    @PostMapping("groupByCbill")
    @ApiOperation(value = "汇总凭证表的制单人", notes = "")
    public Mono<R> findAll(String strDate,String endDate){
        // 有可能是日期
        String str=strDate.length()>6?strDate.substring(0,strDate.length()-2):strDate;
        String end=endDate.length()>6?endDate.substring(0,endDate.length()-2):endDate;
        // 根据期间查
       return accvoucherRepository.groupByCbill(str,end).collectList()
               .flatMap(item->{
                   List<Accvoucher> list = item;
                   if(strDate.length()>4){
                       list= item.stream().filter(acv->Integer.valueOf(acv.getIyperiod())<=Integer.valueOf(endDate)&&Integer.valueOf(acv.getIyperiod())>=Integer.valueOf(strDate)).collect(Collectors.toList());
                   }
                   List<Accvoucher> newlist=new ArrayList<>();
                   list.stream().forEach(v->{
                       newlist.add(new Accvoucher().setCbill(v.getCbill()));
                   });
                   return Mono.just(newlist.stream().distinct().collect(Collectors.toList()));
               })
               .map(q->R.ok().setResult(q));
    }

    @PostMapping("findAll")
    @ApiOperation(value = "科目汇总表", notes = "")
    public Mono<R> findAll(@RequestBody Map map, Pageable pageable){
        String database=map.get("database").toString();
        String bend=map.get("bend").toString();                             // 是否末级 1否 2是
        String strDate=map.get("strDate").toString();                       // 起始期间
        String endDate=map.get("endDate").toString();                       // 结束期间
        String minJc=map.get("minJc").toString();                           // 起始级次
        String maxJc=map.get("maxJc").toString();                           // 结束级次
        String minKm=map.get("minKm").equals("空")?"":map.get("minKm").toString();       // 起始科目
        String maxKm=map.get("maxKm").equals("空")?"":map.get("maxKm").toString();       // 结束科目
        String bz=map.get("bz").toString();                                 // 币种
        String styleValue=map.get("styleValue").toString();                 // 科目分类
        String ibook=map.get("ibook").toString();                           // 记账状态
        String cbill=map.get("cbill").toString();                           // 制单人
        String voucherTypes=map.get("voucherTypes").toString();             // 凭证类别
        String minInoId=map.get("minInoId").toString();                     // 开始凭证编号
        String maxInoId=map.get("maxInoId").toString();                     // 结束凭证编号
        String styleListJSON=map.get("styleList").toString();
        List<AccStyle> styleList= JSONArray.parseArray(styleListJSON, AccStyle.class);// sdfsdf

        Map<String, String> searchMap = ((HashMap<String,  String>) map.get("searchConditon"));
        Map<String, String> filterMap = ((HashMap<String, String>) map.get("filterConditon"));

        return keMuHuiZongService.findAll(pageable,bend,strDate,endDate,minJc,maxJc,minKm,maxKm,bz,styleValue,ibook,cbill,voucherTypes,minInoId,maxInoId,searchMap,filterMap,styleList,database);
    }
}

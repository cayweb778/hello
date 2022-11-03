package org.boozsoft.rest.stock;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.boozsoft.domain.entity.stock.StockCustGroup;
import org.boozsoft.domain.entity.stock.StockCustGroups;
import org.boozsoft.repo.stock.StockCustGroupRepository;
import org.boozsoft.repo.stock.StockCustGroupsRepository;
import org.springbooz.core.tool.result.R;
import org.springbooz.core.tool.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/stock/custGroups")
public class StockCustGroupsController {

    @Autowired
    private StockCustGroupsRepository stockCustGroupsRepository;



    @PostMapping("findAllStockCustGroups")
    public Mono<R> findAllStockCustGroups(@RequestBody Map map){
        String ccodeGroupCcode=map.get("ccodeGroupCcode").toString();
        Map<String, String> searchMap = ((HashMap<String,  String>) map.get("searchConditon"));
        return stockCustGroupsRepository.findAllStockCustGroups().collectList()
                .flatMap(list->{
                    if(StrUtil.isNotBlank(ccodeGroupCcode)&&!ccodeGroupCcode.equals("0")){
                        list=list.stream().filter(a->a.getCcodeGroupCcode().equals(ccodeGroupCcode)).collect(Collectors.toList());
                    }
                    return Mono.just(list.stream().filter(item->{
                        // 按条件过滤
                        if (StringUtils.isNotBlank(searchMap.get("requirement")) && StringUtils.isNotBlank(searchMap.get("value"))) {
                            String value = searchMap.get("value");
                            String dbValue = getFieldValueByFieldName(searchMap.get("requirement").trim(), item);
                            if(searchMap.get("requirement").trim().equals("custCode")){
                                if (Objects.nonNull(dbValue) && !dbValue.equals(value)) {
                                    return false;
                                }
                            }else{
                                if (Objects.nonNull(dbValue) && !dbValue.contains(value)) {
                                    return false;
                                }
                            }
                        }
                        return true;
                    }));
                })
                .map(a->R.ok().setResult(a));
    }

    @PostMapping("findAllStockCustGroupsCustCode")
    public Mono<R> findAllStockCustGroupsCustCode(String ccodeGroupCcode){
        return stockCustGroupsRepository.findAllCustCode(ccodeGroupCcode).collectList().map(a->R.ok().setResult(a));
    }

    @PostMapping("saveStockCustGroupsList")
    public Mono<R> saveStockCustGroupsList(@RequestBody List<StockCustGroups> list){
        return stockCustGroupsRepository.saveAll(list).collectList().map(a->R.ok().setResult(a));
    }

    @PostMapping("delStockCustGroupsByCustCode")
    public Mono<R> delStockCustGroupsByCustCode(@RequestBody List<String> list,String ccodeGroupCcode){
        return stockCustGroupsRepository.deleteByCustCode(list,ccodeGroupCcode).then(Mono.just(R.ok()));
    }
    @PostMapping("deleteByCcodeGroupCcode")
    public Mono<R> deleteByCcodeGroupCcode(String ccodeGroupCcode){
        return stockCustGroupsRepository.deleteByCcodeGroupCcode(ccodeGroupCcode).then(Mono.just(R.ok()));
    }


    private String getFieldValueByFieldName(String fieldName, Object object) {
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + firstLetter + fieldName.substring(1);
            Method method = object.getClass().getMethod(getter, new Class[]{});
            Object value = method.invoke(object, new Object[]{});
            return value.toString();
        } catch (Exception e) {
            return null;
        }
    }
}

package org.boozsoft.rest.stock;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.boozsoft.domain.entity.group.GroupStockAccount;
import org.boozsoft.domain.entity.group.GroupSysAccount;
import org.boozsoft.domain.entity.stock.StockOptionXs;
import org.boozsoft.domain.entity.stock.StockWarehousing;
import org.boozsoft.domain.entity.stock.StockWarehousings;
import org.boozsoft.repo.group.GroupStockAccountRepository;
import org.boozsoft.repo.stock.StockWarehousingRepository;
import org.boozsoft.repo.stock.StockWarehousingsRepository;
import org.boozsoft.repo.stock.option.StockOptionXsRepository;
import org.springbooz.core.launch.constant.AppConstant;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/stock/option")
public class StockOptionController {

    @Autowired
    private StockOptionXsRepository xsRepository;

    @Autowired
    private GroupStockAccountRepository groupStockAccountRepository;

    @GetMapping()
    @ApiOperation(value = "获取参数", notes = "传入accId")
    public Mono<R> findData(String code,String type) {
        Mono exec = groupStockAccountRepository.findAllByCoCode(code.trim()).collectList().map(i -> i.size()>0?i.get(0):"");
        return exec.flatMap(entity -> !entity.equals("")? Mono.just(assemblyMapData(entity, type)):Mono.just("")).map(o -> R.ok().setResult(o));
    }

    @PostMapping()
    @Transactional
    @ApiOperation(value = "获取参数", notes = "传入accId")
    public Mono<R> save(@RequestBody Map map) {
        Mono exec = groupStockAccountRepository.findAllByCoCode( map.get("code").toString().trim()).collectList().map(i -> i.size()>0?i.get(0):"");
        return exec.flatMap(entity -> Mono.just(modifyEntityData(entity, map.get("modifyModel").toString(), map.get("type").toString()))).flatMap(entity -> groupStockAccountRepository.save((GroupStockAccount)entity)).map(o -> R.ok());
    }

    @SneakyThrows
    private Map<String, Object> assemblyMapData(Object obj, String type) {
        Map<String, Object> map = new HashMap<>();
        Map<String, String> map1 = new HashMap<>();
        List<String> list1 = new ArrayList<>();
        GroupStockAccount entity = (GroupStockAccount) obj;
        switch (type){
            case "1": //销售
                map1.put("xsNumWs", StringUtils.isBlank(entity.getXsNumWs()) ? "" : entity.getXsNumWs());
                map1.put("xsPriceWs", StringUtils.isBlank(entity.getXsPriceWs()) ? "" : entity.getXsPriceWs());
                map1.put("xsRateWs", StringUtils.isBlank(entity.getXsRateWs()) ? "" : entity.getXsRateWs());
                map1.put("xsRate", StringUtils.isBlank(entity.getXsRate()) ? "" : entity.getXsRate());
                map1.put("xsDhRc", StringUtils.isBlank(entity.getXsDhRc()) ? "" : entity.getXsDhRc());
                map1.put("xsZdRc", StringUtils.isBlank(entity.getXsZdRc()) ? "" : entity.getXsZdRc());
                map1.put("xsLiucheng", StringUtils.isBlank(entity.getXsLiucheng()) ? "1" : entity.getXsLiucheng());
                if (Objects.equals(entity.getXsKzqxCust(), "1")) list1.add("xsKzqxCust");
                if (Objects.equals(entity.getXsKzqxDepot(), "1")) list1.add("xsKzqxDepot");
                if (Objects.equals(entity.getXsKzqxStock(), "1")) list1.add("xsKzqxStock");
                if (Objects.equals(entity.getXsOutCloseOrder(), "1")) list1.add("xsOutCloseOrder");
                if (Objects.equals(entity.getXsShXkd(), "1")) list1.add("xsShXkd");
                if (Objects.equals(entity.getXsThXhd(), "1")) list1.add("xsThXhd");
                if (Objects.equals(entity.getXsByDd(), "1")) list1.add("xsByDd");
                if (Objects.equals(entity.getXsKzxyCust(), "1")) list1.add("xsKzxyCust");
                if (Objects.equals(entity.getXsKzxyDept(), "1")) list1.add("xsKzxyDept");
                if (Objects.equals(entity.getXsKzxyUser(), "1")) list1.add("xsKzxyUser");
                if (Objects.equals(entity.getXsOutFapiao(), "1")) list1.add("xsOutFapiao");
                if (Objects.equals(entity.getXsOutXhd(), "1")) list1.add("xsOutXhd");
                if (Objects.equals(entity.getXsSaveCheck(), "1")) list1.add("xsSaveCheck");
                if (Objects.equals(entity.getXsPriceCxkh(), "1")) list1.add("xsPriceCxkh");
                if (Objects.equals(entity.getXsPriceZjyc(), "1")) list1.add("xsPriceZjyc");
                if (Objects.equals(entity.getXsPriceZdsj(), "1")) list1.add("xsPriceZdsj");
                if (Objects.equals(entity.getXsReviewCheck(), "1")) list1.add("xsReviewCheck");
                if (Objects.equals(entity.getXsRemoveCheck(), "1")) list1.add("xsRemoveCheck");
                if (Objects.equals(entity.getXsOperateCheck(), "1")) list1.add("xsOperateCheck");
                if (Objects.equals(entity.getXsKzqxStockClass(), "1")) list1.add("xsKzqxStockClass");
                if (Objects.equals(entity.getXsKzqxCustClass(), "1")) list1.add("xsKzqxCustClass");
                if (list1.contains("xsPriceZdsj"))
                map1.put("xsPriceZdsjPwd", StringUtils.isBlank(entity.getXsPriceZdsjPwd()) ? "" : entity.getXsPriceZdsjPwd());
                break;
            case "2": //采购
                map1.put("cgNumWs", StringUtils.isBlank(entity.getCgNumWs()) ? "" : entity.getCgNumWs());
                map1.put("cgPriceWs", StringUtils.isBlank(entity.getCgPriceWs()) ? "" : entity.getCgPriceWs());
                map1.put("cgRateWs", StringUtils.isBlank(entity.getCgRateWs()) ? "" : entity.getCgRateWs());
                map1.put("cgRate", StringUtils.isBlank(entity.getCgRate()) ? "" : entity.getCgRate());
                map1.put("cgLiucheng", StringUtils.isBlank(entity.getCgLiucheng()) ? "1" : entity.getCgLiucheng());
                map1.put("cgDhRc", StringUtils.isBlank(entity.getCgDhRc()) ? "" : entity.getCgDhRc());
                map1.put("cgZdRc", StringUtils.isBlank(entity.getCgZdRc()) ? "" : entity.getCgZdRc());
                if (Objects.equals(entity.getCgKzqxSup(), "1")) list1.add("cgKzqxSup");
                if (Objects.equals(entity.getCgKzqxDepot(), "1")) list1.add("cgKzqxDepot");
                if (Objects.equals(entity.getCgKzqxStock(), "1")) list1.add("cgKzqxStock");
                if (Objects.equals(entity.getCgInputCloseOrder(), "1")) list1.add("cgInputCloseOrder");
                if (Objects.equals(entity.getCgShDhd(), "1")) list1.add("cgShDhd");
                if (Objects.equals(entity.getCgThDhd(), "1")) list1.add("cgThDhd");
                if (Objects.equals(entity.getCgOutFapiao(), "1")) list1.add("cgOutFapiao");
                if (Objects.equals(entity.getCgOutDhd(), "1")) list1.add("cgOutDhd");
                if (Objects.equals(entity.getCgSaveCheck(), "1")) list1.add("cgSaveCheck");
                if (Objects.equals(entity.getCgPriceIsRate(), "1")) list1.add("cgPriceIsRate");
                if (Objects.equals(entity.getCgPriceZjyc(), "1")) list1.add("cgPriceZjyc");
                if (Objects.equals(entity.getCgPriceZgjj(), "1")) list1.add("cgPriceZdsj");
                if (Objects.equals(entity.getCgReviewCheck(), "1")) list1.add("cgReviewCheck");
                if (Objects.equals(entity.getCgRemoveCheck(), "1")) list1.add("cgRemoveCheck");

                if (Objects.equals(entity.getCgOperateCheck(), "1")) list1.add("cgOperateCheck");
                if (Objects.equals(entity.getCgKzqxStockClass(), "1")) list1.add("cgKzqxStockClass");
                if (Objects.equals(entity.getCgKzqxSupClass(), "1")) list1.add("cgKzqxSupClass");
                if (list1.contains("cgPriceZgjj"))
                    map1.put("cgPriceZgjjPwd", StringUtils.isBlank(entity.getCgPriceZgjjPwd()) ? "" : entity.getCgPriceZgjjPwd());
                break;
            case "3": //成本
                map1.put("kcNumWs", StringUtils.isBlank(entity.getKcNumWs()) ? "" : entity.getKcNumWs());
                map1.put("kcPriceWs", StringUtils.isBlank(entity.getKcPriceWs()) ? "" : entity.getKcPriceWs());
                map1.put("kcCostAccounting", StringUtils.isBlank(entity.getKcCostAccounting()) ? "" : entity.getKcCostAccounting());
                map1.put("kcEstimated", StringUtils.isBlank(entity.getKcEstimated()) ? "" : entity.getKcEstimated());
                if (Objects.equals(entity.getKcKzqxDepot(), "1")) list1.add("kcKzqxDepot");
                if (Objects.equals(entity.getKcKzqxStock(), "1")) list1.add("kcKzqxStock");
                if (Objects.equals(entity.getKcKzkcHighBottom(), "1")) list1.add("kcKzkcHighBottom");
                if (Objects.equals(entity.getKcKzgzAvailability(), "1")) list1.add("kcKzgzAvailability");
                if (Objects.equals(entity.getKcIsLck(), "1")) list1.add("kcIsLck");
                if (Objects.equals(entity.getKcCgrkSaveCheck(), "1")) list1.add("kcCgrkSaveCheck");
                if (Objects.equals(entity.getKcXsckSaveCheck(), "1")) list1.add("kcXsckSaveCheck");
                if (Objects.equals(entity.getKcCgrkCheck(), "1")) list1.add("kcCgrkCheck");
                if (Objects.equals(entity.getKcXsckCheck(), "1")) list1.add("kcXsckCheck");
                if (Objects.equals(entity.getKcQtcrkCheck(), "1")) list1.add("kcQtcrkCheck");
                if (Objects.equals(entity.getKcJcjrCheck(), "1")) list1.add("kcJcjrCheck");
                break;
        }
        map.put("basisMap", map1);
        map.put("controlList", list1);
        return map;
    }

    @SneakyThrows
    private Object modifyEntityData(Object obj, String json, String type) {
        JSONObject jsonObject = JSONUtil.parseObj(json);
        HashMap<String, String> basisMap = jsonObject.get("basisMap", HashMap.class);
        List<String> controlList = jsonObject.get("controlList", ArrayList.class);
        GroupStockAccount entity = (GroupStockAccount) obj;
        switch (type) {
            case "1":
                entity.setXsNumWs(String.valueOf(basisMap.get("xsNumWs")));
                entity.setXsPriceWs(String.valueOf(basisMap.get("xsPriceWs")));
                entity.setXsRateWs(String.valueOf(basisMap.get("xsRateWs")));
                entity.setXsRate(String.valueOf(basisMap.get("xsRate")));
                entity.setXsDhRc(String.valueOf(basisMap.get("xsDhRc")));
                entity.setXsZdRc(String.valueOf(basisMap.get("xsZdRc")));
                entity.setXsLiucheng(basisMap.get("xsLiucheng"));
                if (controlList.contains("xsPriceZdsj")) {
                    entity.setXsPriceZdsj("1");
                    entity.setXsPriceZdsjPwd(basisMap.get("xsPriceZdsjPwd"));
                } else {
                    entity.setXsPriceZdsj(null);
                    entity.setXsPriceZdsjPwd(null);
                }
                entity.setXsKzqxCust(controlList.contains("xsKzqxCust")?"1":null);
                entity.setXsKzqxDepot(controlList.contains("xsKzqxDepot")?"1":null);
                entity.setXsKzqxStock(controlList.contains("xsKzqxStock")?"1":null);
                entity.setXsOutCloseOrder(controlList.contains("xsOutCloseOrder")?"1":null);
                entity.setXsShXkd(controlList.contains("xsShXkd")?"1":null);
                entity.setXsThXhd(controlList.contains("xsThXhd")?"1":null);
                entity.setXsByDd(controlList.contains("xsByDd")?"1":null);
                entity.setXsKzxyCust(controlList.contains("xsKzxyCust")?"1":null);
                entity.setXsKzxyDept(controlList.contains("xsKzxyDept")?"1":null);
                entity.setXsKzxyUser(controlList.contains("xsKzxyUser")?"1":null);
                entity.setXsOutFapiao(controlList.contains("xsOutFapiao")?"1":null);
                entity.setXsOutXhd(controlList.contains("xsOutXhd")?"1":null);
                entity.setXsSaveCheck(controlList.contains("xsSaveCheck")?"1":null);
                entity.setXsPriceCxkh(controlList.contains("xsPriceCxkh")?"1":null);
                entity.setXsPriceZjyc(controlList.contains("xsPriceZjyc")?"1":null);
                entity.setXsReviewCheck(controlList.contains("xsReviewCheck")?"1":null);
                entity.setXsRemoveCheck(controlList.contains("xsRemoveCheck")?"1":null);

                entity.setXsOperateCheck(controlList.contains("xsOperateCheck")?"1":null);
                entity.setXsKzqxCustClass(controlList.contains("xsKzqxCustClass")?"1":null);
                entity.setXsKzqxStockClass(controlList.contains("xsKzqxStockClass")?"1":null);
                return entity;
            case "2":
                entity.setCgNumWs(String.valueOf(basisMap.get("cgNumWs")));
                entity.setCgPriceWs(String.valueOf(basisMap.get("cgPriceWs")));
                entity.setCgRateWs(String.valueOf(basisMap.get("cgRateWs")));
                entity.setCgRate(String.valueOf(basisMap.get("cgRate")));
                entity.setCgLiucheng(basisMap.get("cgLiucheng"));
                entity.setCgDhRc(String.valueOf(basisMap.get("cgDhRc")));
                entity.setCgZdRc(String.valueOf(basisMap.get("cgZdRc")));
                if (controlList.contains("cgPriceZgjj")) {
                    entity.setCgPriceZgjj("1");
                    entity.setCgPriceZgjjPwd(basisMap.get("cgPriceZgjjPwd"));
                } else {
                    entity.setCgPriceZgjj(null);
                    entity.setCgPriceZgjjPwd(null);
                }
                entity.setCgKzqxSup(controlList.contains("cgKzqxSup")?"1":null);
                entity.setCgKzqxDepot(controlList.contains("cgKzqxDepot")?"1":null);
                entity.setCgKzqxStock(controlList.contains("cgKzqxStock")?"1":null);
                entity.setCgInputCloseOrder(controlList.contains("cgInputCloseOrder")?"1":null);
                entity.setCgShDhd(controlList.contains("cgShDhd")?"1":null);
                entity.setCgThDhd(controlList.contains("cgThDhd")?"1":null);
                entity.setCgOutFapiao(controlList.contains("cgOutFapiao")?"1":null);
                entity.setCgOutDhd(controlList.contains("cgOutDhd")?"1":null);
                entity.setCgSaveCheck(controlList.contains("cgSaveCheck")?"1":null);
                entity.setCgPriceIsRate(controlList.contains("cgPriceIsRate")?"1":null);
                entity.setCgPriceZjyc(controlList.contains("cgPriceZjyc")?"1":null);
                entity.setCgReviewCheck(controlList.contains("cgReviewCheck")?"1":null);
                entity.setCgRemoveCheck(controlList.contains("cgRemoveCheck")?"1":null);

                entity.setCgOperateCheck(controlList.contains("cgOperateCheck")?"1":null);
                entity.setCgKzqxSupClass(controlList.contains("cgKzqxSupClass")?"1":null);
                entity.setCgKzqxStockClass(controlList.contains("cgKzqxStockClass")?"1":null);
                return entity;

            case "3":
                entity.setKcNumWs(String.valueOf(basisMap.get("kcNumWs")));
                entity.setKcPriceWs(String.valueOf(basisMap.get("kcPriceWs")));
                entity.setKcCostAccounting(basisMap.get("kcCostAccounting"));
                entity.setKcEstimated(basisMap.get("kcEstimated"));
                entity.setKcKzqxDepot(controlList.contains("kcKzqxDepot")?"1":null);
                entity.setKcKzqxStock(controlList.contains("kcKzqxStock")?"1":null);
                entity.setKcKzkcHighBottom(controlList.contains("kcKzkcHighBottom")?"1":null);
                entity.setKcKzgzAvailability(controlList.contains("kcKzgzAvailability")?"1":null);
                entity.setKcIsLck(controlList.contains("kcIsLck")?"1":null);
                entity.setKcCgrkSaveCheck(controlList.contains("kcCgrkSaveCheck")?"1":null);
                entity.setKcXsckSaveCheck(controlList.contains("kcXsckSaveCheck")?"1":null);
                entity.setKcCgrkCheck(controlList.contains("kcCgrkCheck")?"1":null);
                entity.setKcXsckCheck(controlList.contains("kcXsckCheck")?"1":null);
                entity.setKcQtcrkCheck(controlList.contains("kcQtcrkCheck")?"1":null);
                entity.setKcJcjrCheck(controlList.contains("kcJcjrCheck")?"1":null);
                return entity;
        }
        return null;
    }
    
}

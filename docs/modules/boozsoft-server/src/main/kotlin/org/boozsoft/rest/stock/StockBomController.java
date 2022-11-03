package org.boozsoft.rest.stock;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.spring.util.ObjectUtils;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.boozsoft.domain.entity.Customer;
import org.boozsoft.domain.entity.ReportEncodingRules;
import org.boozsoft.domain.entity.group.GroupStockPeriod;
import org.boozsoft.domain.entity.stock.*;
import org.boozsoft.domain.entity.stock.biandong.StockSaleousingBiandong;
import org.boozsoft.domain.entity.stock.biandong.StockSaleousingsBiandong;
import org.boozsoft.domain.ro.StockCostAccRo;
import org.boozsoft.domain.vo.stock.StockVo;
import org.boozsoft.repo.CustomerRepository;
import org.boozsoft.repo.ReportEncodingRulesRepository;
import org.boozsoft.repo.group.GroupStockPeriodRepository;
import org.boozsoft.repo.stock.*;
import org.boozsoft.repo.stock.biandong.StockSaleousingBiandongRepository;
import org.boozsoft.repo.stock.biandong.StockSaleousingsBiandongRepository;
import org.jetbrains.annotations.NotNull;
import org.springbooz.core.tool.result.R;
import org.springbooz.core.tool.utils.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuple4;
import reactor.util.function.Tuple6;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/stock/bom")
public class StockBomController {

    @Autowired
    private StockBomRepository bomRepository;

    @Autowired
    private StockBomsRepository bomsRepository;


    @PostMapping("findAll")
    @ApiOperation(value = "查询列表", notes = "传入类型")
    public Mono<R> findAll(@RequestBody Map map, Pageable pageable){
        // 当前页
        int page= map.get("page")==null?1:Integer.parseInt(map.get("page").toString());
        // 页多少条
        int size= map.get("size")==null?10000:Integer.parseInt(map.get("size").toString());
        AtomicReference<Long> totalAR = new AtomicReference(0);
        Map<String, String> searchMap = ((HashMap<String,  String>) map.get("searchConditon"));
        return bomRepository.findAll()
                .collectList().flatMap(item2->{
                    List<StockBom> list=item2.stream().skip((page - 1) * size).limit(size).collect(Collectors.toList());
                    long total=list.size();
                    totalAR.set(total);
                    return Mono.just(list.stream().filter(item->{
                        // 按条件过滤
                        if (StringUtils.isNotBlank(searchMap.get("requirement")) && StringUtils.isNotBlank(searchMap.get("value"))) {
                            String value = searchMap.get("value");
                            String dbValue = ReflectUtil.getFieldValue(item,searchMap.get("requirement").trim()).toString();
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
                .map(a -> R.page(a.collect(Collectors.toList()),pageable,(totalAR.get())));
    }


    @PostMapping("findEntryListById")
    @ApiOperation(value = "查询列表", notes = "传入类型")
    public Mono<R> findEntryListById(@RequestBody Map map){
        return bomsRepository.findAllByBomUniqueIdOrderByGxIdAsc(map.get("uniqueId").toString())
                .collectList()
                .map(R::ok);
    }


    @PostMapping
    @Transactional
    public Mono<R> save(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        Map<String, Object> obj = assemblyCorrespondingRulesData(map);
        StockBom master = (StockBom) obj.get("master");
        List<StockBoms> sub = (List<StockBoms>) obj.get("sub");
        boolean b = master.getId() == null;
        return bomRepository.save(master).flatMap(db -> b ? addXhd(db, sub) : editXhd(db, sub)).map(o -> R.ok());
    }

    private Map<String, Object> assemblyCorrespondingRulesData(Map map) {
        StockBom bom = new StockBom();
        bom.setId(null != map.get("id")? map.get("id").toString() : null)
                .setBomUniqueId(null != map.get("bomUniqueId")? map.get("bomUniqueId").toString() : IdUtil.objectId())
                .setBomSysId(null != map.get("bomSysId")? map.get("bomSysId").toString() : null)
                .setBomId(null != map.get("bomId")? map.get("bomId").toString() : null)
                .setBomName(null != map.get("bomName")? map.get("bomName").toString() : null)
                .setCmaker(null != map.get("cmaker")? map.get("cmaker").toString() : null)
                .setCmakerTime(null != map.get("cmakerTime")? map.get("cmakerTime").toString() : DateUtil.now())
                .setBomVersion(null != map.get("bomVersion")? map.get("bomVersion").toString() : null)
                .setBomVerName(null != map.get("bomVerName")? map.get("bomVerName").toString() : null)
                .setBomVerStartDate(null != map.get("bomVerStartDate")? map.get("bomVerStartDate").toString() : null)
                .setBomVerStopDate(null != map.get("bomVerStopDate")? map.get("bomVerStopDate").toString() : null)
                .setDdate(null != map.get("ddate")? map.get("ddate").toString() : null)
                .setBstyle(null != map.get("bstyle")? map.get("bstyle").toString() : null)
                .setUnitId(null != map.get("unitId")? map.get("unitId").toString() : null)
                .setCunitid(null != map.get("cunitid")? map.get("cunitid").toString() : null)
                .setQuantity(null != map.get("quantity")? map.get("quantity").toString() : null)
                .setBaseQuantity(null != map.get("baseQuantity")? map.get("baseQuantity").toString() : null)
                .setCdepcode(null != map.get("cdepcode")? map.get("cdepcode").toString() : null)
                .setCpersoncode(null != map.get("cpersoncode")? map.get("cpersoncode").toString() : null)
                .setBomExplain(null != map.get("bomExplain")? map.get("bomExplain").toString() : null)
                .setSunhaoLv(null != map.get("sunhaoLv")? map.get("sunhaoLv").toString() : null)
                .setChengpingLv(null != map.get("chengpingLv")? map.get("chengpingLv").toString() : null)
                .setBcloser(null != map.get("bcloser")? map.get("bcloser").toString() : null)
                .setBcloserTime(null != map.get("bcloserTime")? map.get("bcloserTime").toString() : null)
                .setBcloserUser(null != map.get("bcloserUser")? map.get("bcloserUser").toString() : null)
                .setBcloserExplain(null != map.get("bcloserExplain")? map.get("bcloserExplain").toString() : null)
                .setBcheck(null != map.get("bcheck")? map.get("bcheck").toString() : null)
                .setBcheckTime(null != map.get("bcheckTime")? map.get("bcheckTime").toString() : null)
                .setBcheckUser(null != map.get("bcheckUser")? map.get("bcheckUser").toString() : null)
        ;
        if (map.keySet().toString().contains("cfree")){
            for (int i = 1; i <= 12; i++) {
                String key = "cfree"+i;
                if (null != map.get(key)) ReflectUtil.setFieldValue(bom,key,map.get(key).toString());
            }
        }
        String entryList = map.containsKey("entryList") ? map.get("entryList").toString() : ""; // 表体
        List<StockBoms> entrys = JSON.parseArray(entryList, StockBoms.class);
        for (StockBoms entry : entrys) {
            entry.setBomUniqueId(bom.getBomUniqueId());
        }
        Map map2=new HashMap<>();
        map2.put("master",bom);
        map2.put("sub",entrys);
        return map2;
//        return Map.of("master",bom,"sub",entrys);
    }

    private Mono<String> addXhd(StockBom db, List<StockBoms> sub) {
        return bomsRepository.saveAll(sub).collectList().thenReturn(db).flatMap(z -> Mono.just(""));
    }

    private Mono<String> editXhd(StockBom db, List<StockBoms> sub) {
        return bomsRepository.saveAll(sub).collectList().thenReturn(db).flatMap(z -> Mono.just(""));
    }

    @PostMapping("findBillByCondition")
    public Mono<R> findBillByCondition(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        String action = map.get("action").toString();
        String currPdId = map.containsKey("curr") ? map.get("curr").toString() : "";
        return bomRepository.findAllByOrderByCmakerTimeAsc()
                .collectList().cache()
                .flatMap(list -> {
                    if (list.size() == 0) {
                        return Mono.just(R.ok());
                    } else {
                        StockBom master = null;
                        switch (action) {
                            case "curr":
                                master = list.get((list.stream().map(e -> e.getBomSysId()).collect(Collectors.toList())).indexOf(currPdId));
                                break;
                            case "tail":
                                master = list.get(list.size() - 1);
                                break;
                            case "prev":
                                if (StrUtil.isBlank(currPdId)) {
                                    master = list.get(0);
                                } else {
                                    int index = (list.stream().map(e -> e.getBomSysId()).collect(Collectors.toList())).indexOf(currPdId);
                                    index = index == 0 ? 0 : index - 1;
                                    master = list.get(index);
                                }
                                break;
                            case "next":
                                if (StrUtil.isBlank(currPdId)) {
                                    master = list.get(0);
                                } else {
                                    int index = (list.stream().map(e -> e.getBomSysId()).collect(Collectors.toList())).indexOf(currPdId);
                                    index = index >= list.size() - 1 ? list.size() - 1 : index + 1;
                                    master = list.get(index);
                                }
                                break;
                            default:
                                master = list.get(0);
                                break;
                        }
                        StockBom finalMaster = master;
                        return bomsRepository.findAllByBomUniqueIdOrderByGxIdAsc(master.getBomUniqueId()).collectList().map(enlist -> {
                            if (enlist.size() > 0) finalMaster.setEntryList(JSON.toJSONString(enlist));
                            return R.ok(finalMaster);
                        });
                    }
                });
    }


    @PostMapping("review")
    @Transactional
    public Mono<R> review(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        String id = map.get("id").toString();
        String userId = map.get("userId").toString();
        String b = map.get("type").toString();
        return bomRepository.findById(id).flatMap(dbEntry ->{
            dbEntry.setBcheck(b.equals("true")?"1":null);
            dbEntry.setBcheckTime(b.equals("true")?DateUtil.now():null);
            dbEntry.setBcheckUser(b.equals("true")?userId:null);
            return  bomRepository.save(dbEntry).thenReturn("");
        }).map(R::ok);
    }

    @DeleteMapping
    @Transactional
    public Mono<R> remove(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        String id = map.get("id").toString();
        return bomRepository.findById(id)
                .flatMap(dbEntry ->
                        bomsRepository.findAllByBomUniqueIdOrderByGxIdAsc(dbEntry.getBomUniqueId()).collectList()
                                .flatMap(list-> bomRepository.delete(dbEntry).thenReturn("").zipWith( bomsRepository.deleteAll(list).thenReturn(Mono.just(""))).flatMap(tips->Mono.just("")))
                 ).map(R::ok);
    }

    @PostMapping("verify")
    public Mono<R> verify(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        String id = null==map.get("id")?null:map.get("id").toString().trim();
        String bomSysId = null==map.get("bomSysId")?null:map.get("bomSysId").toString().trim();
        String bomId = null==map.get("bomId")?null:map.get("bomId").toString().trim();
        String bomVersion = null==map.get("bomVersion")?null:map.get("bomVersion").toString().trim();
        return bomRepository.findAllByOrderByCmakerTimeAsc().collectList()
        .flatMap(list ->{
            String mark = "0";
            if (list.stream().filter(it->it.getBomSysId().equals(bomSysId) && (null==id?true:!id.equals(it.getId()))).collect(Collectors.toList()).size()>0){
                mark = "1";
            }else if (list.stream().filter(it->it.getBomId().equals(bomId) && it.getBomVersion().equals(bomVersion)  && (null==id?true:!id.equals(it.getId())) ).collect(Collectors.toList()).size()>0){
                mark = "2";
            }
            return Mono.just(mark);
        }).map(R::ok);
    }

    @PostMapping("verifyComp")
    public Mono<R> verifyComp(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        String code = null==map.get("code")?null:map.get("code").toString().trim();
        return bomRepository.findAllByOrderByCmakerTimeAsc().collectList()
                .flatMap(list ->{
                    String mark = "0";
                    if (list.stream().filter(it->it.getBomId().equals(code)).collect(Collectors.toList()).size()>0){
                        mark = "1";
                    }
                    return Mono.just(mark);
                }).map(R::ok);
    }

    @PostMapping("findByBomUniqueId")
    public Mono<R> findByBomUniqueId(@RequestBody Map map) {
        String uniqueId=map.get("uniqueId").toString();
        return bomRepository.findByBomUniqueId(uniqueId).map(R::ok).defaultIfEmpty(R.ok().setResult(""));
    }

}
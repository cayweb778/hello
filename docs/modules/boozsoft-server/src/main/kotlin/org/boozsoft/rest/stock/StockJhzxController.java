package org.boozsoft.rest.stock;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.boozsoft.domain.entity.ReportEncodingRules;
import org.boozsoft.domain.entity.stock.*;
import org.boozsoft.domain.vo.stock.StockJhzxVo;
import org.boozsoft.repo.ReportEncodingRulesRepository;
import org.boozsoft.repo.stock.*;
import org.boozsoft.utils.CollectOfUtils;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Transient;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/stock/jhzx")
public class StockJhzxController {

    @Autowired
    private StockSaleousingsRepository saleousingsRepository;
    @Autowired
    private StockSaleousingRepository saleousingRepository;
    @Autowired
    private StockXyCsourceRepository xyCsourceRepository;
    @Autowired
    private StockJhzxRepository jhzxRepository;
    @Autowired
    private StockJhzxCustRepository jhzxCustRepository;
    @Autowired
    private StockJhzxStockRepository jhzxStockRepository;
    @Autowired
    private StockJhzxCsourceRepository jhzxCsourceRepository;
    @Autowired
    private ReportEncodingRulesRepository encodingRulesRepository;

    @PostMapping
    @Transient
    public Mono<R> save(@RequestBody Map map) {
        Map<String, Object> saves = businessResolve(map);
        StockJhzx stockJhzx = (StockJhzx) saves.get("jxzx");
        List<StockJhzxCust> stockCusts = (List<StockJhzxCust>) saves.get("jxzxCust");
        List<StockJhzxStock> jxzxStock = (List<StockJhzxStock>) saves.get("jxzxStock");
        Mono<StockJhzx> one = jhzxRepository.save(stockJhzx);
        Mono<List<StockJhzxCust>> two = jhzxCustRepository.saveAll(stockCusts).collectList();
        Mono<List<StockJhzxStock>> there = jhzxStockRepository.saveAll(jxzxStock).collectList();
        return Mono.zip(one, two, there).flatMap(tips -> {
            StockJhzx t1 = tips.getT1();
            List<String> codes = tips.getT2().stream().map(it -> it.getOutCcode()).collect(Collectors.toList());
            return saleousingRepository.findByCcodeIn(codes).collectList().flatMap(list -> {
                List<StockXyCsource> xyList = new ArrayList<>();
                for (StockSaleousing i : list) {
                    i.setPickingId(t1.getCcode()).setPickingDate(t1.getDdate()).setJhzxStatus("1");
                    xyList.add(new StockXyCsource().setCcode(i.getCcode()).setCcodeDate(i.getDdate()).setBillStyle(i.getBillStyle()).setIyear(i.getIyear()).setXyccode(t1.getCcode()).setXyccodeDate(t1.getDdate()).setXyBillStyle("JHZXD"));
                }
                return saleousingRepository.saveAll(list).collectList().zipWith(xyCsourceRepository.saveAll(xyList).collectList()).flatMap(tis->Mono.just(""));
            }).flatMap(s -> Mono.just(R.ok()));
        });
    }

    @PostMapping("modify")
    @Transient
    public Mono<R> modify(@RequestBody Map map) {
        String id = map.get("id").toString();
        String ccode = map.get("ccode").toString();
        String cdepcode = map.get("cdepcode").toString();
        String kgUserId = map.get("kgUserId").toString();
        List<StockJhzxStock> entryList = JSON.parseArray(map.get("entryList").toString(), StockJhzxStock.class);
        Mono<StockJhzx> one = jhzxRepository.findById(id);
        Mono<List<StockJhzxStock>> there = jhzxStockRepository.findAllByCcode(ccode).collectList();
        return Mono.zip(one, there).flatMap(tips -> {
            StockJhzx t1 = tips.getT1();
            t1.setCdepcode(StrUtil.isBlank(cdepcode)?null:cdepcode).setKgUserId(StrUtil.isBlank(kgUserId)?null:kgUserId);
            List<StockJhzxStock> jhzxStocks = tips.getT2().stream().map(it -> {
                List<StockJhzxStock> collect = entryList.stream().filter(it2 -> it2.getId().equals(it.getId())).collect(Collectors.toList());
                if (collect.size() > 0) it.setCmemo(collect.get(0).getCmemo());
                return it;
            }).collect(Collectors.toList());
            return jhzxRepository.save(t1).zipWith(jhzxStockRepository.saveAll(jhzxStocks).collectList()).flatMap(tis->Mono.just(R.ok()));
        });
    }

    @PostMapping("code")
    public Mono<R> lastCode(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        String date = map.get("date").toString();
        String type = map.get("type").toString();
        String key = map.get("key").toString();
        String customize = map.containsKey("prefix") ? map.get("prefix").toString() : "";
        return generateCode(key, type, date, customize).map(R::ok);
    }

    /**
     *
     * @param key 代码规则
     * @param type 单据类型
     * @param date 时间
     * @param customize 自定义前缀
     * @return 单据编码
     */
    private Mono<String> generateCode(String key, String type, String date, String customize) {
        return encodingRulesRepository.findByFileType(key).switchIfEmpty(Mono.just(new ReportEncodingRules())).zipWith(jhzxRepository.findFirstByDdateLikeOrderByDdateDescCcodeDesc("%" + date.substring(0, 7) + "%").switchIfEmpty(Mono.just(new StockJhzx()))).flatMap(tips -> {
            ReportEncodingRules obj = tips.getT1();
            StockJhzx it = tips.getT2();
            StringBuilder pre = new StringBuilder("");
            int l = 4;
            if (obj.getId() == null) {
                pre.append(customize);
            } else {
                l = Integer.parseInt(obj.getSerialNumLength());
                String separation = obj.getDelimiter().equals("3") ? "-" : obj.getDelimiter().equals("2") ? "." : "";
                if (obj.getPrefixOneIs().equals("true"))
                    pre.append((StrUtil.isBlank(customize) ? obj.getPrefixOneCustomize() : customize) + separation);
                if (obj.getPrefixTwoIs().equals("true"))
                    pre.append((date.substring(0, 7).replace("-", "")) + separation);
            }
            if (null == it.getCcode() || !NumberUtil.isNumber(it.getCcode().replace(pre.toString(),"") )) {
                return Mono.just(pre.toString() + "0001");
            } else {
                int t = Integer.parseInt(it.getCcode().replace(pre.toString(), "")) + 1;
                return Mono.just(pre.toString() + String.format("%0" + l + "d", t));
            }
        });
    }

    private Map<String, Object> businessResolve(Map map) {
        Map<String, Object> results = new HashMap<>();
        String ccode = map.get("ccode").toString();
        String ddate = map.get("ddate").toString();
        String cdepcode = map.get("cdepcode").toString();
        String kgUserId = map.get("kgUserId").toString();
        String cmaker = map.get("cmaker").toString();
        String cmakerDate = DateUtil.today();
        List<String> cvencodes = (List<String>) map.get("cvencodes");
        List<String> codes = (List<String>) map.get("codes");
        List<String> codeDates = (List<String>) map.get("codeDates");
        List<StockJhzxStock> entryList = JSON.parseArray(map.get("entryList").toString(), StockJhzxStock.class);
        StockJhzx stockJhzx = new StockJhzx().setCcode(ccode).setDdate(ddate).setCdepcode(cdepcode).setKgUserId(kgUserId).setCmaker(cmaker).setCmakerDate(cmakerDate).setIyear(ddate.split("-")[0]);
        List<StockJhzxCust> stockCusts = new ArrayList<>(cvencodes.size());
        for (int i = 0; i < codes.size(); i++)
            stockCusts.add(new StockJhzxCust().setCcode(ccode).setDdate(ddate).setIyear(stockJhzx.getIyear()).setCvencode(cvencodes.get(i)).setOutCcode(codes.get(i)).setOutDate(codeDates.get(i)));
        for (StockJhzxStock jhzxStock : entryList)
            jhzxStock.setCcode(ccode).setDdate(ddate).setIyear(stockJhzx.getIyear());
        results.put("jxzx", stockJhzx);
        results.put("jxzxCust", stockCusts);
        results.put("jxzxStock", entryList);
        return results;
    }

    @PostMapping("findByList")
    public Mono<R> findByList(@RequestBody Map map) {
        String mark = map.get("queryMark").toString();
        Map<String,String> query = (Map<String, String>) map.get("query");
        String start = "";
        String end = "";
        if (StrUtil.isNotBlank(query.get("periodStart"))){
            start = query.get("periodStart").substring(0,4)+"-"+query.get("periodStart").substring(4,6)+"-01";
            end = query.get("periodEnd").substring(0,4)+"-"+query.get("periodEnd").substring(4,6)+"-31";
        }else {
            start = query.get("dateStart");
            end = query.get("dateEnd");
        }
        return (mark.equals("1")?jhzxRepository.findAllByDdateBetweenOrderByDdateAscCcodeAscDetails(start,end):jhzxRepository.findAllByDdateBetweenOrderByDdateAscCcodeAsc(start,end))
                .collectList()
                .map(o -> R.ok().setResult(queryFilter(o,query,mark)));
    }

    private Object queryFilter(List<?> o, Map<String, String> query,String mark) {
        String ccodeMin =!query.containsKey("ccodeMin")?"":query.get("ccodeMin").trim();
        String ccodeMax = !query.containsKey("ccodeMax")?"":query.get("ccodeMax").trim();
        String cvencode = !query.containsKey("cvencode")?"":query.get("cvencode").trim();
        String bcheck = !query.containsKey("bcheck")?"":query.get("bcheck").trim();
        String cinvode = !query.containsKey("cinvode")?"":query.get("cinvode").trim();
        String cinvodeClass = !query.containsKey("cinvodeClass")?"":query.get("cinvodeClass").trim();
        String batchId = !query.containsKey("batchId")?"":query.get("batchId").trim();
        String dvdate = !query.containsKey("dvdate")?"":query.get("dvdate").trim();
        String cwhcode = !query.containsKey("cwhcode")?"":query.get("cwhcode").trim();
        String cpersoncode = !query.containsKey("cpersoncode")?"":query.get("cpersoncode").trim();
        String cdepcode = !query.containsKey("cdepcode")?"":query.get("cdepcode").trim();
        String citemcode = !query.containsKey("citemcode")?"":query.get("citemcode").trim();
        String cwhcodeUser = !query.containsKey("cwhcodeUser")?"":query.get("cwhcodeUser").trim();
        List<?> list = o.stream().filter(it -> {
            if (mark.equals("1")) {
                StockJhzxVo entiry = (StockJhzxVo) it;
                if (StrUtil.isNotBlank(ccodeMin) && StrUtil.isNotBlank(ccodeMax)){}
                if (StrUtil.isNotBlank(bcheck) && !entiry.getBcheck().equals(bcheck))  return false;
                if (StrUtil.isNotBlank(cwhcodeUser) && !entiry.getKgUserId().equals(cwhcodeUser))  return false;
                if (StrUtil.isNotBlank(cdepcode) && !entiry.getCdepcode().equals(cdepcode))  return false;
                if (StrUtil.isNotBlank(cpersoncode) && !entiry.getCvencode().equals(cpersoncode))  return false;
            } else {
                StockJhzx entiry = (StockJhzx) it;
                if (StrUtil.isNotBlank(ccodeMin) && StrUtil.isNotBlank(ccodeMax)){}
                if (StrUtil.isNotBlank(bcheck) && !entiry.getBcheck().equals(bcheck))  return false;
                if (StrUtil.isNotBlank(cwhcodeUser) && !entiry.getKgUserId().equals(cwhcodeUser))  return false;
                if (StrUtil.isNotBlank(cdepcode) && !entiry.getCdepcode().equals(cdepcode))  return false;
            }
            return true;
        }).collect(Collectors.toList());
        return  list;
    }

    @PostMapping("review")
    @Transactional
    public Mono<R> review(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        List<String> codes = (List<String>) map.get("codes");
        String userId = map.get("userId").toString();
        String type = map.get("type").toString();
        return Flux.fromIterable(codes).flatMap(id ->
                jhzxRepository.findByCcode(id).flatMap(dbEntry -> {
                    if (type.equals("true")) {
                        dbEntry.setBcheck("1").setBcheckTime(DateUtil.now()).setBcheckUser(userId);
                    } else {
                        dbEntry.setBcheck(null).setBcheckTime(null).setBcheckUser(null);
                    }
                    return jhzxRepository.save(dbEntry).thenReturn("");
                })
        ).collectList().map(R::ok);
    }

    @PostMapping("dels")
    @Transactional
    public Mono<R> dels(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        List<String> codes = (List<String>) map.get("codes");
        return Flux.fromIterable(codes).flatMap(id ->{
                    Mono<StockJhzx> one = jhzxRepository.findByCcode(id);
                    Mono<List<StockJhzxCust>> two = jhzxCustRepository.findAllByCcode(id).collectList();
                    Mono<List<StockJhzxStock>> there = jhzxStockRepository.findAllByCcode(id).collectList();
                    return Mono.zip(one,two,there).flatMap(tips->{
                        StockJhzx t1 = tips.getT1();
                        List<String> dbCodes = tips.getT2().stream().map(it -> it.getOutCcode()).collect(Collectors.toList());
                        return (null != t1.getBcheck() && t1.getBcheck().equals("1"))?Mono.just(""):saleousingRepository.findByCcodeIn(dbCodes).collectList().flatMap(list -> {
                            for (StockSaleousing i : list) {
                                i.setPickingId(null).setPickingDate(null).setJhzxStatus(null);
                            }
                            Mono<List<StockSaleousing>> four = saleousingRepository.saveAll(list).collectList();
                            Mono<String> five = xyCsourceRepository.findByXyccodeAndXyBillStyle(t1.getCcode(), "JHZXD").collectList().flatMap(xys -> xyCsourceRepository.deleteAll(xys).thenReturn(""));
                            Mono<String> six = jhzxRepository.delete(t1).thenReturn("");
                            Mono<String> seven = jhzxCustRepository.deleteAll(tips.getT2()).thenReturn("");
                            Mono<String> eight = jhzxStockRepository.deleteAll(tips.getT3()).thenReturn("");
                            return Mono.zip(four,five,six,seven,eight).flatMap(tips2->Mono.just(""));
                        });
                    });
                }
        ).collectList().map(R::ok);
    }

    @PostMapping("findByCode")
    public Mono<R> findByCode(@RequestBody Map map) {
        String code = map.get("code").toString();
        Mono<StockJhzx> one = jhzxRepository.findByCcode(code).switchIfEmpty(Mono.just(new StockJhzx()));
        Mono<List<StockJhzxCust>> two = jhzxCustRepository.findAllByCcode(code).collectList();
        Mono<List<StockJhzxStock>> there = jhzxStockRepository.findAllByCcode(code).collectList();
        return Mono.zip(one,two,there).flatMap(tips->Mono.just(R.ok(tips.getT1().getId()==null?null:CollectOfUtils.mapof("one",tips.getT1(),"two",tips.getT2().stream().map(it->it.getCvencode()).collect(Collectors.toList()),"three",tips.getT3()))));
    }

}

package org.boozsoft.rest.stock;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import lombok.extern.slf4j.Slf4j;
import org.boozsoft.domain.entity.ReportEncodingRules;
import org.boozsoft.domain.entity.stock.*;
import org.boozsoft.domain.vo.stock.StockCurrentLackVo;
import org.boozsoft.domain.vo.stock.StockCurrentstockVo;
import org.boozsoft.domain.vo.stock.StockWarehousingVo;
import org.boozsoft.repo.ReportEncodingRulesRepository;
import org.boozsoft.repo.group.GroupStockPeriodRepository;
import org.boozsoft.repo.stock.*;
import org.springbooz.core.launch.constant.AppConstant;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/stock/warehousing")
public class StockWarehousingController {

    @Autowired
    private StockWarehousingRepository warehousingRepository;
    @Autowired
    private StockWarehousingsRepository warehousingsRepository;
    @Autowired
    private StockXyCsourceRepository xyCsourceRepository;
    @Autowired
    StockCurrentstockRepository stockCurrentstockRepository;
    @Autowired
    private ReportEncodingRulesRepository encodingRulesRepository;
    @Autowired
    GroupStockPeriodRepository stockPeriodRepository;
    @Autowired
    DatabaseClient client;

    @Autowired
    StockSaleousingsRepository saleousingsRepository;

    @PostMapping("findAllByCcodeAndBillStyle")
    public Mono<R> findAllByCcodeAndBillStyle(String ccode,String billStyle){
        return warehousingRepository.findAllByCcodeAndBillStyle(ccode,billStyle).collectList().map(R::ok);
    }

    @PostMapping
    @Transactional
    public Mono<R> save(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        Map<String, Object> obj = assemblyCorrespondingRulesData(map);
        StockWarehousing master = (StockWarehousing) obj.get("master");
        List<StockWarehousings> sub = (List<StockWarehousings>) obj.get("sub");
        boolean b = master.getId() == null;
        return warehousingRepository.save(master).flatMap(db -> {
            Mono<StockWarehousing> s = warehousingsRepository.saveAll(sub).collectList().thenReturn(db); //添加
            Mono<StockWarehousing> d = warehousingsRepository.findAllByBillStyleAndCcodeOrderByDdateAscCcodeAscLineIdAsc(db.getBillStyle(), db.getCcode()).collectList().flatMap(list -> warehousingsRepository.deleteAll(list).thenReturn("").flatMap(i -> s)); //修改
            return b ? s : d;
        }).map(o -> R.ok());
    }

    @PostMapping("findBillByCondition")
    public Mono<R> findBillByCondition(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        // 单据编码
        String ccode = map.get("ccode")==null?"":map.get("ccode").toString();
        String type = map.get("type").toString();
        String iyear = map.get("iyear").toString();
        String action = map.get("action").toString();
        String currPdId = map.containsKey("curr") ? map.get("curr").toString() : "";
        String bdocumStyle = map.containsKey("bdocumStyle") ? map.get("bdocumStyle").toString() : "";
        return warehousingRepository.findAllByBillStyleAndIyearOrderByDdateAscCcodeAsc(type, iyear)
        .filter(it->bdocumStyle.equals("")?true:it.getBdocumStyle().equals(bdocumStyle))
        .collectList().cache()
        .flatMap(list -> {
            if (list.size() == 0) {
                return Mono.just(R.ok());
            } else {
                StockWarehousing master = null;
                switch (action) {
                    case "curr":
                        long count = list.stream().filter(a -> a.getCcode().equals(currPdId)).count();
                        if(count>0){
                            master = list.get((list.stream().map(e -> e.getCcode()).collect(Collectors.toList())).indexOf(currPdId));
                            break;
                        }
                    case "tail":
                        List<StockWarehousing> collect = list.stream().filter(a -> a.getCcode().equals(currPdId)).collect(Collectors.toList());

                        if(StrUtil.isNotBlank(ccode)){
                            list=list.stream().filter(a->a.getCcode().equals(ccode)).collect(Collectors.toList());
                            if(list.size()>0){
                                master = list.get(0);
                            }else{
                                master = collect.get(0);
                            }
                        }else{
                            master = list.get(list.size() - 1);
                        }
                        break;
                    case "prev":
                        if (StrUtil.isBlank(currPdId)) {
                            master = list.get(0);
                        } else {
                            int index = (list.stream().map(e -> e.getCcode()).collect(Collectors.toList())).indexOf(currPdId);
                            index = index == 0 ? 0 : index - 1;
                            master = list.get(index);
                        }
                        break;
                    case "next":
                        if (StrUtil.isBlank(currPdId)) {
                            master = list.get(0);
                        } else {
                            int index = (list.stream().map(e -> e.getCcode()).collect(Collectors.toList())).indexOf(currPdId);
                            index = index >= list.size() - 1 ? list.size() - 1 : index + 1;
                            master = list.get(index);
                        }
                        break;
                    default:
                        master = list.get(0);
                        break;
                }
                StockWarehousing finalMaster = master;
                return warehousingsRepository.findAllByBillStyleAndCcodeOrderByDdateAscCcodeAscLineIdAsc(master.getBillStyle(), master.getCcode()).collectList().map(enlist -> {
                    if (enlist.size() > 0) {
                        enlist.stream().forEach(temp->{
                            temp.setOldBaseQuantity(temp.getBaseQuantity()).setOldCnumber(temp.getCnumber()).setOldIsum(temp.getIsum());
                        });
                        finalMaster.setEntryList(JSON.toJSONString(enlist));
                    }
                    return R.ok(finalMaster);
                });
            }
        });
    }

    @PostMapping("code")
    public Mono<R> lastCode(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        String date = map.get("date").toString();
        String type = map.get("type").toString();
        String key = map.get("key").toString();
        return warehousingRepository.findAllTypeAndIyear(type, date.substring(0,4)).collectList().flatMap(list->{
            return encodingRulesRepository.findByFileType(key).flatMap(tips->{
                ReportEncodingRules obj = tips;
//                StockWarehousing it = tips.getT2();
                String customize = map.containsKey("prefix")?map.get("prefix").toString():"";
                StringBuilder pre = new StringBuilder("");
                int l = 4;
                if (obj.getId() == null){
                    pre.append(customize);
                }else {
                    l = Integer.parseInt(obj.getSerialNumLength());
                    String separation = obj.getDelimiter().equals("3")?"-":obj.getDelimiter().equals("2")?".":"";
                    if (obj.getPrefixOneIs().equals("true"))
                        pre.append((StrUtil.isBlank(customize)?obj.getPrefixOneCustomize():customize)+separation);
                    if (obj.getPrefixTwoIs().equals("true"))
                        pre.append((date.substring(0,7).replace("-",""))+separation);
                }
                if(list.size()>0){
                    List<StockWarehousing> collect = list.stream().filter(a -> a.getCcode().contains(pre.toString())).collect(Collectors.toList());
                    if(collect.size()>0){
                        collect.sort(Comparator.comparing(StockWarehousing::getCcode).reversed());
                        int t = Integer.parseInt(collect.get(0).getCcode().replace(pre.toString(),""))+1;
                        return Mono.just(pre.toString()+String.format("%0"+l+"d",t));
                    }
                    return Mono.just(pre.toString()+"0001");
                }else{
                    return Mono.just(pre.toString()+"0001");
                }
            });
        }).map(R::ok).defaultIfEmpty(R.ok().setResult(""));
    }

    /**
     * 期初到货单用到
     * @param map
     * @return
     */
    @PostMapping("code2")
    public Mono<R> code2(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        String iyear = map.get("iyear").toString();
        String type = map.get("type").toString();
        String time = type+"-"+map.get("time").toString().replaceAll("-","")+"-";
        return warehousingRepository.findByCodeMax(type, iyear).collectList().flatMap(codeMax->{
            String newcode="";
            if(codeMax.size()==0){
                newcode=time+"0001";
            }else{
                List<StockWarehousing> collect = codeMax.stream().filter(a -> a.getCcode().contains(time)).collect(Collectors.toList());
                if(collect.size()==0){
                    newcode=time+"0001";
                }else{
                    List<Integer> list2 = new ArrayList<>();
                    for (int i = 0; i < 1000; i++) {
                        list2.add(i + 1);
                    }

                    List<Integer> list=new ArrayList<>();
                    collect.forEach(c->{
                        list.add(Integer.parseInt(c.getCcode().replace(time,"")));
                    });
                    int minCode = list2.stream().filter(item -> !list.contains(item)).mapToInt(Integer::intValue).min().orElse(0);
                    newcode=time+String.format("%04d",minCode);
                }
            }
            return Mono.just(newcode);
        }).map(a->R.ok().setResult(a));
    }

    @PostMapping("review")
    @Transactional
    public Mono<R> review(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        String id = map.get("id").toString();
        String userId = map.get("userId").toString();
        String type = map.get("type").toString();
        List<String> keys = map.containsKey("keys") ? (List<String>) map.get("keys") : new ArrayList<>();
        return warehousingRepository.findById(id).flatMap(dbEntry ->
                warehousingsRepository.findAllByBillStyleAndCcodeOrderByDdateAscCcodeAscLineIdAsc(dbEntry.getBillStyle(), dbEntry.getCcode()).collectList()
                        .flatMap(list -> {
                            if (type.equals("true")) {
                                HashSet<String> dbSet = new HashSet<>(list.stream().filter(it -> StrUtil.isNotBlank(it.getBcheck()) && it.getBcheck().equals("1")).map(it -> it.getLineCode()).collect(Collectors.toList()));
                                dbSet.addAll(keys);
                                if (keys.size() == 0 || dbSet.size() == list.size()) {
                                    dbEntry.setBcheck("1").setBcheckTime(DateUtil.now()).setBcheckUser(userId);
                                    for (StockWarehousings stockWarehousings : list) {
                                        stockWarehousings.setBcheck("1").setBcheckTime(DateUtil.now()).setBcheckUser(userId);
                                    }
                                    return warehousingRepository.save(dbEntry).flatMap(e -> warehousingsRepository.saveAll(list).collectList().thenReturn(e));
                                } else {
                                    for (StockWarehousings stockWarehousings : list) {
                                        if (keys.contains(stockWarehousings.getLineCode()))
                                            stockWarehousings.setBcheck("1").setBcheckTime(DateUtil.now()).setBcheckUser(userId);
                                    }
                                    return warehousingsRepository.saveAll(list).collectList().thenReturn(dbEntry);
                                }
                            } else {
                                dbEntry.setBcheck(null).setBcheckTime(null).setBcheckUser(null);
                                for (StockWarehousings stockWarehousings : list) {
                                    if (keys.size() == 0) {
                                        stockWarehousings.setBcheck(null).setBcheckTime(null).setBcheckUser(null);
                                    } else if (keys.contains(stockWarehousings.getLineCode())) {
                                        stockWarehousings.setBcheck(null).setBcheckTime(null).setBcheckUser(null);
                                    }
                                }
                                return warehousingRepository.save(dbEntry).flatMap(e -> warehousingsRepository.saveAll(list).collectList().thenReturn(e));
                            }
                        })
        ).map(R::ok);
    }

    @DeleteMapping
    @Transactional
    public Mono<R> del(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        return warehousingRepository.findById(map.get("id").toString()).flatMap(db ->
                warehousingsRepository.findAllByBillStyleAndCcodeOrderByDdateAscCcodeAscLineIdAsc(db.getBillStyle(), db.getCcode()).collectList()
                        .flatMap(list -> warehousingsRepository.deleteAll(list).thenReturn("").flatMap(i -> warehousingRepository.delete(db).thenReturn(""))) //修改
        ).map(o -> R.ok());
    }

    @PostMapping("delMx")
    @Transactional
    public Mono<R> delMx(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        return warehousingRepository.findByCcode(map.get("ccode").toString()).flatMap(db ->
                warehousingsRepository.findAllByBillStyleAndCcodeOrderByDdateAscCcodeAscLineIdAsc(db.getBillStyle(), db.getCcode()).collectList()
                        .flatMap(list -> warehousingsRepository.deleteAll(list).thenReturn("").flatMap(i -> warehousingRepository.delete(db).thenReturn(""))) //修改
        ).map(o -> R.ok());
    }

    @PostMapping("reviewSetCGRKG")
    @Transactional
    public Mono<R> reviewSetCGRKG(@RequestBody StockWarehousing pojo) {
        return warehousingRepository.save(pojo).map(a->R.ok().setResult(a));
    }
    @PostMapping("reviewSetCGRKGMx")
    @Transactional
    public Mono<R> reviewSetCGRKGMx(@RequestBody List<StockWarehousings> pojo) {
        pojo.stream().forEach(a->{
            if(a.getIsGive().equals("true") || a.getIsGive().equals("1")){
                a.setIsGive("1");
            }else{
                a.setIsGive("0");
            }
        });
        return warehousingsRepository.saveAll(pojo).collectList().map(a->R.ok().setResult(a));
    }
    @PostMapping("xyCsourceSave")
    @Transactional
    public Mono<R> xyCsourceSave(@RequestBody StockXyCsource pojo) {
        return xyCsourceRepository.save(pojo).map(a->R.ok().setResult(a));
    }

    @PostMapping("findByStockCurrLock")
    public Mono<R> findByStockCurrLock(@RequestBody Map map) {
        String iyear=map.get("iyear").toString();
        List<StockWarehousings> list= JSON.parseArray(map.get("list").toString(), StockWarehousings.class);
        return stockCurrentstockRepository.findAlls().collectList()
        .flatMap(stockCurrentList->{
            List<Map>mapList=new ArrayList<>();
            if(stockCurrentList.size()>0){
                for (int i = 0; i < list.size(); i++) {
                    int finalI = i;

                    String text="存货编码【"+list.get(i).getCinvode();
                    Map map1=new HashMap();
                    // 1 【仓库、存货】
                    List<StockCurrentstockVo> collect = stockCurrentList.stream().filter(sc -> sc.getIyear().equals(iyear)&&sc.getCwhcode().equals(list.get(finalI).getCwhcode()) && sc.getInvCode().equals(list.get(finalI).getCinvode())).collect(Collectors.toList());
                    // 2 【批号】
                    if(StrUtil.isNotBlank(list.get(i).getBatchId())){
                        text+=",批号【"+list.get(i).getBatchId()+"】";
                        collect=collect.stream().filter(a->StrUtil.isNotBlank(a.getBatchId())&&a.getBatchId().equals(list.get(finalI).getBatchId())).collect(Collectors.toList());
                        // 2.1 生产日期、失效日期
                        if(StrUtil.isNotBlank(list.get(i).getDvdate())){
                            text+=",生产日期【"+list.get(i).getDpdate()+"】失效日期【"+list.get(i).getDvdate()+"】";
                            collect=collect.stream().filter(a->a.getDvdate().equals(list.get(finalI).getDvdate()) && a.getDpdate().equals(list.get(finalI).getDpdate())).collect(Collectors.toList());
                        }
                    }

                    if(collect.size()>0){
                        // 已锁住
                        if(collect.get(0).getRevision()==1){
                            map1.put("revision","1");
                            map1.put("stockCurrentsId","");
                            map1.put("lockCreatedUserName",collect.get(0).getLockCreatedUserName());
                            map1.put("text",text);
                            mapList.add(map1);
                            break;
                        }else{
                            map1.put("revision","0");
                            map1.put("stockCurrentsId",collect.get(0).getId());
                            map1.put("lockCreatedUserName","");
                            map1.put("text","");
                            mapList.add(map1);
                        }
                    }else{
                        map1.put("revision","");
                        map1.put("stockCurrentsId","");
                        map1.put("lockCreatedUserName","");
                        map1.put("text","");
                        mapList.add(map1);
                    }
                }
            }
            else{
                Map map1=new HashMap();
                map1.put("revision","");
                map1.put("stockCurrentsId","");
                map1.put("lockCreatedUserName","");
                map1.put("text","");
                mapList.add(map1);
            }
            return Mono.just(mapList);
        })
         .map(a->R.ok().setResult(a));
    }

    @PostMapping("saveStockCurrentstockZTRK_Increase")
    @Transactional
    public Mono<R> saveStockCurrentstockZTRK_Increase(@RequestBody Map map2){
        List<StockWarehousings> list= JSONArray.parseArray(map2.get("list").toString(),StockWarehousings.class);
        // 1 新建 2编辑
        String status= map2.get("status").toString();
        // 采购入库单审核后修改现存量（默认值1是，0或空否，保存即修改）
        String kcCgrkCheck= map2.get("kcCgrkCheck").toString();
        // 采购入库单审核标志
        String rkdCheck= map2.get("rkdCheck")==null?"":map2.get("rkdCheck").toString();

        // 所有现存量表数据
        return stockCurrentstockRepository.findAll().collectList()
        .flatMap(stockCurrList->{
            Map map=new HashMap<>();
            map.put("stockCurrList",stockCurrList);
            return Mono.just(map);
        })
        .flatMap(mapArr->{
            List<StockCurrentstock> stockCurrList= (List<StockCurrentstock>) mapArr.get("stockCurrList");

            List<StockCurrentstock> newlist=new ArrayList<>();
            list.forEach(temp->{
                StockCurrentstock c=new StockCurrentstock();
                List<StockCurrentstock> collect;
                // 查找现存量表是否存在【年度、存货ID、仓库ID】
                // 批号
                if(StrUtil.isNotBlank(temp.getBatchId())){
                    collect = stockCurrList.stream().filter(a ->StrUtil.isNotBlank(a.getBatchId())&&a.getBatchId().equals(temp.getBatchId()) && a.getIyear().equals(temp.getIyear()) && a.getInvCode().equals(temp.getCinvode()) && a.getCwhcode().equals(temp.getCwhcode())).collect(Collectors.toList());
                    if(StrUtil.isNotBlank(temp.getDvdate())){
                        // 批号下 生产日期与失效日期 是否 有相同数据
                        collect=collect.stream().filter(a->StrUtil.isNotBlank(a.getDvdate())&&a.getDvdate().equals(temp.getDvdate())&&a.getDpdate().equals(temp.getDpdate())).collect(Collectors.toList());
                    }
                }else{
                    collect = stockCurrList.stream().filter(a -> a.getIyear().equals(temp.getIyear()) && a.getInvCode().equals(temp.getCinvode()) && a.getCwhcode().equals(temp.getCwhcode())).collect(Collectors.toList());
                }

                // 有就修改，没有就增加
                if(collect.size()==0){
                    c.setIyear(temp.getIyear());
                    c.setInvCode(temp.getCinvode());
                    c.setCwhcode(temp.getCwhcode());
                    c.setCwhcode1(StrUtil.isBlank(temp.getCwhcode1())?"":temp.getCwhcode1());
                    c.setCwhcode2(StrUtil.isBlank(temp.getCwhcode2())?"":temp.getCwhcode2());
                    c.setCwhcode3(StrUtil.isBlank(temp.getCwhcode3())?"":temp.getCwhcode3());
                    c.setCwhcode4(StrUtil.isBlank(temp.getCwhcode4())?"":temp.getCwhcode4());
                    c.setCwhcode5(StrUtil.isBlank(temp.getCwhcode5())?"":temp.getCwhcode5());
                    c.setCwhcode6(StrUtil.isBlank(temp.getCwhcode6())?"":temp.getCwhcode6());
//                    c.setCunitid(temp.getCunitid());
                    c.setCunitidType(StrUtil.isNotBlank(temp.getCunitidF1())?"多计量":"单计量");
                    c.setCunitidF1(temp.getCunitidF1());
                    c.setCunitidF2(temp.getCunitidF2());
                    c.setBatchId(temp.getBatchId());
                    c.setDpdate(temp.getDpdate());
                    c.setDvdate(temp.getDvdate());

                    if(kcCgrkCheck.equals("1")){
//                        c.setZtrkQuantity(temp.getKcKylCg().equals("1")?new BigDecimal("0"):new BigDecimal(temp.getBaseQuantity()));
//                        c.setZtrkQuantity1(temp.getKcKylCg().equals("1")?new BigDecimal("0"):new BigDecimal(StrUtil.isBlank(temp.getSubQuantity1())?"0":temp.getSubQuantity1()));
//                        c.setZtrkQuantity2(temp.getKcKylCg().equals("1")?new BigDecimal("0"):new BigDecimal(StrUtil.isBlank(temp.getSubQuantity2())?"0":temp.getSubQuantity2()));
                    }else{
                        // 采购入库单审核后修改现存量（默认值1是，0或空否，保存即修改）
                        c.setBaseQuantity(new BigDecimal(temp.getBaseQuantity()));
//                        c.setZtrkQuantity(new BigDecimal("0"));
//                        c.setZtrkQuantity1(new BigDecimal("0"));
//                        c.setZtrkQuantity2(new BigDecimal("0"));
                    }
                    newlist.add(c);
                }
                else{
                    BigDecimal baseQuantity=new BigDecimal("0");
                    BigDecimal sub1=new BigDecimal("0");
                    BigDecimal sub2=new BigDecimal("0");

                    BigDecimal ztrk=new BigDecimal("0");
                    BigDecimal ztrk1=new BigDecimal("0");
                    BigDecimal ztrk2=new BigDecimal("0");

                    // 采购入库单审核后修改现存量（默认值1是，0或空否，保存即修改）
                    if(StrUtil.isBlank(kcCgrkCheck)&&kcCgrkCheck.equals("0")){
                        // 等于
//                        if(collect.get(0).getZtrkQuantity().compareTo(new BigDecimal(temp.getBaseQuantity()))==0){
//                            baseQuantity=collect.get(0).getZtrkQuantity();
//                        }else if(new BigDecimal(temp.getBaseQuantity()).compareTo(collect.get(0).getZtrkQuantity())==1){ // 大于
//                            BigDecimal cha=new BigDecimal(temp.getBaseQuantity()).subtract(collect.get(0).getZtrkQuantity());
//                            BigDecimal cha2=new BigDecimal(temp.getSubQuantity2()).subtract(collect.get(0).getZtrkQuantity2());
//                            baseQuantity=collect.get(0).getZtrkQuantity().add(cha);
//                        }else if(new BigDecimal(temp.getBaseQuantity()).compareTo(collect.get(0).getZtrkQuantity())==-1){ // 小于
//                            BigDecimal cha=collect.get(0).getZtrkQuantity().subtract(new BigDecimal(temp.getBaseQuantity()));
//                            baseQuantity=collect.get(0).getZtrkQuantity().subtract(cha);
//                        }
                        // kc_kyl_cg  可用量是否包含采购在途量和销售在途量（默认值1是，0或空否）
                        if(temp.getKcKylCg()==null||temp.getKcKylCg().equals("1")){
                            collect.get(0).setBaseQuantity(new BigDecimal("0"));
//                            collect.get(0).setZtrkQuantity(new BigDecimal("0"));
                        }else{
                            collect.get(0).setBaseQuantity(baseQuantity);
//                            collect.get(0).setZtrkQuantity(new BigDecimal("0"));
                        }
                    }
                    else{
                        BigDecimal baseQuantity1 = StrUtil.isNotBlank(collect.get(0).getBaseQuantity().toString())?new BigDecimal("0"):collect.get(0).getBaseQuantity();
                        BigDecimal baseQuantity2 = new BigDecimal(temp.getBaseQuantity());
                        // 等于
                        if(baseQuantity1.compareTo(baseQuantity2)==0){
                            baseQuantity=collect.get(0).getBaseQuantity();
//                            sub1=collect.get(0).getSubQuantity1();
//                            sub2=collect.get(0).getSubQuantity2();

                            ztrk=baseQuantity;
                            ztrk1=sub1;
                            ztrk2=sub2;
                        }else{
                            baseQuantity=collect.get(0).getBaseQuantity().add(new BigDecimal(temp.getBaseQuantity()));
//                            sub1=collect.get(0).getSubQuantity1().add(new BigDecimal(temp.getSubQuantity1()));
//                            sub2=collect.get(0).getSubQuantity1().add(new BigDecimal(temp.getSubQuantity2()));

//                            ztrk=collect.get(0).getZtrkQuantity().subtract(new BigDecimal(temp.getBaseQuantity()));
//                            ztrk1=collect.get(0).getZtrkQuantity1().subtract(new BigDecimal(temp.getSubQuantity1()));
//                            ztrk2=collect.get(0).getZtrkQuantity2().subtract(new BigDecimal(temp.getSubQuantity2()));
                        }
                        collect.get(0).setBaseQuantity(baseQuantity);
//                        collect.get(0).setSubQuantity1(sub1);
//                        collect.get(0).setSubQuantity2(sub2);
//                        collect.get(0).setZtrkQuantity(ztrk);
//                        collect.get(0).setZtrkQuantity1(ztrk1);
//                        collect.get(0).setZtrkQuantity2(ztrk2);
                    }
                    newlist.add(collect.get(0));
                }
            });
            return Mono.just(newlist);
        })
        .flatMapMany(newlist->stockCurrentstockRepository.saveAll(newlist)).collectList()
        .map(a->R.ok().setResult(a));
    }

    /**
     * 删除上游游单据
     * @param ccode
     * @param billType
     * @return
     */
    @PostMapping("delXyCsourceByCcodeAndBillType")
    @Transactional
    public Mono<R> delXyCsourceByCcodeAndBillType(String ccode,String billType) {
        return warehousingRepository.delXyCsourceByCcodeAndBillType(ccode,billType).then(Mono.just(R.ok()));
    }

    /**
     * 删除下游表
     * @param xyccode
     * @param xybillStyle
     * @return
     */
    @PostMapping("delXyCsourceByxyCcodeAndxyBillTypeAndBillTypeAndCcode")
    @Transactional
    public Mono<R> delXyCsourceByxyCcodeAndxyBillTypeAndBillTypeAndCcode(String xyccode,String xybillStyle,String billStyle,String ccode) {
        return warehousingRepository.delXyCsourceByxyCcodeAndxyBillTypeAndBillTypeAndCcode(xyccode,xybillStyle,billStyle,ccode).then(Mono.just(R.ok()));
    }
    @PostMapping("delXyCsourceByxyCcode")
    @Transactional
    public Mono<R> delXyCsourceByxyCcode(String xyccode) {
        return warehousingRepository.delXyCsourceByxyCcode(xyccode).then(Mono.just(R.ok()));
    }

    @PostMapping("deleteByXyStyleAndBillStyleAndccode")
    @Transactional
    public Mono<R> deleteByXyStyleAndBillStyleAndccode(String xybillStyle,String billStyle,String ccode) {
        return warehousingRepository.deleteByXyStyleAndBillStyleAndccode(xybillStyle,billStyle,ccode).then(Mono.just(R.ok()));
    }

    /**
     * 是否存在下游单据
     * @param year
     * @param code
     * @param xyCode
     * @return
     */
    @PostMapping("verifyXyCsourceByXyCode")
    @Transactional
    public Mono<R> verifyXyCsourceByXyCode(String year,String code,String xyCode){
        return xyCsourceRepository.findByIyearAndCcodeAndXyBillStyle(year,code,xyCode).map(a->R.ok().setResult(a)).defaultIfEmpty(R.ok().setResult(""));
    }

    /**
     * 上游单据是否存在此类型
     * @param year
     * @param billStyle
     * @return
     */
    @PostMapping("verifySyCsourceByXyCode")
    public Mono<R> findAllByBillStyleAndIyear(String year,String billStyle,String ccode){
        return xyCsourceRepository.findAllByBillStyleAndIyearAndCcode(billStyle,year,ccode).collectList().map(a->R.ok().setResult(a));
    }

    /**
     * 查询下游单据表
     * @param map
     * @return
     */
    @PostMapping("findXyCsourceByXyStyleAndXyCodeAndIyear")
    public Mono<R> findXyCsourceByXyStyleAndXyCodeAndIyear(@RequestBody Map map){
        String xyStyle=map.get("xyStyle").toString();
        String xyCode=map.get("xyCode").toString();
        String iyear=map.get("iyear").toString();
        String ccode=map.get("ccode").toString();
        return xyCsourceRepository.findByXyBillStyleAndXyccodeAndIyearAndCcode(xyStyle,xyCode,iyear,ccode).map(a->R.ok().setResult(a)).defaultIfEmpty(R.ok().setResult(""));
    }


    /**
     * 获取最近的供应商价格
     * @param supUnique
     * @return
     */
    @PostMapping("findByStockWareRecentlySupMoney")
    public Mono<R> findByStockWareRecentlySupMoney(String supUnique){
        return warehousingRepository.findByStockWareRecentlySupMoney(supUnique).map(a->R.ok().setResult(a)).defaultIfEmpty(R.ok().setResult(""));
    }

    /**
     * ID查找主表
     * @param ccode
     * @return
     */
    @PostMapping("findStockWareByCcode")
    public Mono<R> findStockWareByCcode(String ccode){
        return warehousingRepository.findByCcodeData(ccode).map(a->R.ok().setResult(a)).defaultIfEmpty(R.ok().setResult(""));
    }

    @PostMapping("findByCcodeAdnBillStyleData")
    public Mono<R> findByCcodeAdnBillStyleData(@RequestBody Map map){
        String ccode=map.get("ccode").toString();
        String billStyle=map.get("billStyle").toString();
        return warehousingRepository.findByCcodeAdnBillStyleData(ccode,billStyle).map(a->R.ok().setResult(a)).defaultIfEmpty(R.ok().setResult(""));
    }

    /**
     * 只增加入库单主表
     * @param obj
     * @return
     */
    @PostMapping("saveStockWarehousing")
    @Transactional
    public Mono<R> saveStockWarehousing(@RequestBody StockWarehousing obj){
        return warehousingRepository.save(obj).map(a->R.ok().setResult(a));
    }

    /**
     * 判断存货期间表是否有结账
     * @return
     */
    @PostMapping("findStockPeriodYmFlag")
    public Mono<R> findStockPeriodYmFlag(String iyear,String flag){
        return stockPeriodRepository.countByStockYearAndIstock(iyear,flag).map(a->R.ok().setResult(a));
    }

    /**
     * 采购入库单弃审 修改现存量
     * @param map2
     * @return
     */
    @PostMapping("saveStockCurrentstockZTRK_Edit_XCL")
    @Transactional
    public Mono<R> saveStockCurrentstockZTRK_Edit_XCL(@RequestBody Map map2){
        List<StockWarehousings> list= JSONArray.parseArray(map2.get("list").toString(),StockWarehousings.class);

        // 所有现存量表数据
        return stockCurrentstockRepository.findAll().collectList()
                .flatMap(stockCurrList->{
                    Map map=new HashMap<>();
                    map.put("stockCurrList",stockCurrList);
                    return Mono.just(map);
                })
                .flatMap(mapArr->{
                    List<StockCurrentstock> stockCurrList= (List<StockCurrentstock>) mapArr.get("stockCurrList");

                    List<StockCurrentstock> newlist=new ArrayList<>();
                    list.forEach(temp->{
                        StockCurrentstock c=new StockCurrentstock();
                        List<StockCurrentstock> collect;
                        // 查找现存量表是否存在【年度、存货ID、仓库ID】
                        // 批号
                        if(StrUtil.isNotBlank(temp.getBatchId())){
                            collect = stockCurrList.stream().filter(a ->StrUtil.isNotBlank(a.getBatchId())&&a.getBatchId().equals(temp.getBatchId()) && a.getIyear().equals(temp.getIyear()) && a.getInvCode().equals(temp.getCinvode()) && a.getCwhcode().equals(temp.getCwhcode())).collect(Collectors.toList());
                            if(StrUtil.isNotBlank(temp.getDvdate())){
                                // 批号下 生产日期与失效日期 是否 有相同数据
                                collect=collect.stream().filter(a->StrUtil.isNotBlank(a.getDvdate())&&a.getDvdate().equals(temp.getDvdate())&&a.getDpdate().equals(temp.getDpdate())).collect(Collectors.toList());
                            }
                        }else{
                            collect = stockCurrList.stream().filter(a -> a.getIyear().equals(temp.getIyear()) && a.getInvCode().equals(temp.getCinvode()) && a.getCwhcode().equals(temp.getCwhcode())).collect(Collectors.toList());
                        }

                        collect.get(0).setBaseQuantity(collect.get(0).getBaseQuantity().subtract(new BigDecimal(temp.getBaseQuantity())));
//                        collect.get(0).setSubQuantity1(collect.get(0).getSubQuantity1().subtract(new BigDecimal(temp.getSubQuantity1())));
//                        collect.get(0).setSubQuantity2(collect.get(0).getSubQuantity2().subtract(new BigDecimal(temp.getSubQuantity2())));

//                        collect.get(0).setZtrkQuantity(collect.get(0).getZtrkQuantity().add(new BigDecimal(temp.getBaseQuantity())));
//                        collect.get(0).setZtrkQuantity1(collect.get(0).getZtrkQuantity1().add(new BigDecimal(temp.getSubQuantity1())));
//                        collect.get(0).setZtrkQuantity2(collect.get(0).getZtrkQuantity2().add(new BigDecimal(temp.getSubQuantity2())));
                        newlist.add(collect.get(0));
                    });
                    return Mono.just(newlist);
                })
                .flatMapMany(newlist->stockCurrentstockRepository.saveAll(newlist)).collectList()
                .map(a->R.ok().setResult(a));
    }


    /**
     * 判断上游是否生成红字
     * @param syCode
     * @param billStyle
     * @return
     */
    @PostMapping("findByCodeSetXyCodeBdocumStyle")
    public Mono<R> findByCodeSetXyCodeBdocumStyle(String syCode,String billStyle){
        return warehousingRepository.findByCodeSetXyCodeBdocumStyle(syCode,billStyle).map(a->R.ok().setResult(a));
    }

    // list修改审核状态
    @PostMapping("editBcheck")
    @Transactional
    public Mono<R> editBcheck(@RequestBody Map map){
        String bcheck=map.get("bcheck").toString();
        String bcheckTime=map.get("bcheckTime").toString();
        String bcheckUser=map.get("bcheckUser").toString();
        List<String> list= (List<String>) map.get("list");
        Mono<Void> a=warehousingRepository.editBcheck(bcheck,bcheckTime,bcheckUser,list);
        Mono<Void> b=warehousingsRepository.editBcheck(bcheck,bcheckTime,bcheckUser,list);
        return Mono.zip(a,b).then(Mono.just(R.ok()));
    }


    @PostMapping("delAllList")
    @Transactional
    public Mono<R> delAllList(@RequestBody List<String> list){
        Mono<Void> a=warehousingRepository.delAllList(list);
        Mono<Void> b=warehousingsRepository.delAllList(list);
        return Mono.zip(a,b).then(Mono.just(R.ok()));
    }

    // 列表查询
    @PostMapping("findAllMainList")
    public Mono<R> findAllMainList(@RequestBody Map map){
        String type=map.get("type").toString();

        String strDate=map.get("strDate").toString();
        String endDate=map.get("endDate").toString();
        // true
        if(map.get("strDate").toString().indexOf("-")>0){
            // 2022-01
            if(map.get("strDate").toString().length()==7){
                strDate=map.get("strDate").toString()+"-01";
                endDate=map.get("endDate").toString()+"-31";
            }
        }
        // 202201
        else{
            strDate=map.get("strDate").toString().substring(0,4)+"-"+map.get("strDate").toString().substring(4)+"-01";
            endDate=map.get("endDate").toString().substring(0,4)+"-"+map.get("endDate").toString().substring(4)+"-31";
        }
        String dataType=map.get("dataType").toString();
        String strNum=map.get("strNum")==null?"":map.get("strNum").toString();
        String endNum=map.get("endNum")==null?"":map.get("endNum").toString();
        String sup=map.get("sup")==null?"":map.get("sup").toString();
        String jssup=map.get("jssup")==null?"":map.get("jssup").toString();
        String bdocumStyle=map.get("bdocumStyle")==null?"":map.get("bdocumStyle").toString();
        String cangku=map.get("cangku")==null?"":map.get("cangku").toString();
        Map<String, String> pageSearch = ((HashMap<String,  String>) map.get("pageSearch"));
        // 按添加日期
        return warehousingRepository.findMainList2(type,strDate,endDate).collectList()
                .flatMap(list->{
                    if(StrUtil.isNotBlank(bdocumStyle)){
                        list=list.stream().filter(a->a.getBdocumStyle().equals(bdocumStyle)).collect(Collectors.toList());
                    }
                    if(StrUtil.isNotBlank(strNum)){
                        list=list.stream().filter(a->a.getCcode().contains(strNum)&&a.getCcode().contains(endNum)).collect(Collectors.toList());
                    }
                    if(StrUtil.isNotBlank(dataType)){
                        list=list.stream().filter(a->a.getBcheck().equals(dataType)).collect(Collectors.toList());
                    }
                    if(StrUtil.isNotBlank(cangku)){
                        list=list.stream().filter(a->a.getCwhcode().equals(cangku)).collect(Collectors.toList());
                    }
                    if(StrUtil.isNotBlank(sup)){
                        list=list.stream().filter(a->a.getCvencode().equals(sup)).collect(Collectors.toList());
                    }
                    if(StrUtil.isNotBlank(jssup)){
                        list=list.stream().filter(a->a.getCvencodeJs().equals(jssup)).collect(Collectors.toList());
                    }
                    if(pageSearch!=null&&StrUtil.isNotBlank(pageSearch.get("selectValue"))){
                        if(pageSearch.get("selectType").equals("ccode")){
                            list=list.stream().filter(a->a.getCcode().contains(pageSearch.get("selectValue"))).collect(Collectors.toList());
                        }else if(pageSearch.get("selectType").equals("supName")){
                            list=list.stream().filter(a->a.getCustName().contains(pageSearch.get("selectValue"))).collect(Collectors.toList());
                        }else if(pageSearch.get("selectType").equals("custCode")){
                            list=list.stream().filter(a->a.getCustCode().contains(pageSearch.get("selectValue"))).collect(Collectors.toList());
                        }else if(pageSearch.get("selectType").equals("custName")){
                            list=list.stream().filter(a->a.getCustName().contains(pageSearch.get("selectValue"))).collect(Collectors.toList());
                        }else if(pageSearch.get("selectType").equals("deptName")){
                            list=list.stream().filter(a->a.getDeptName().contains(pageSearch.get("selectValue"))).collect(Collectors.toList());
                        }else if(pageSearch.get("selectType").equals("personName")){
                            list=list.stream().filter(a->a.getPersonName().contains(pageSearch.get("selectValue"))).collect(Collectors.toList());
                        }else if(pageSearch.get("selectType").equals("cmakerName")){
                            list=list.stream().filter(a->a.getCmakerName().contains(pageSearch.get("selectValue"))).collect(Collectors.toList());
                        }else if(pageSearch.get("selectType").equals("jscustCode")){
                            list=list.stream().filter(a->a.getJscustCode().contains(pageSearch.get("selectValue"))).collect(Collectors.toList());
                        }else if(pageSearch.get("selectType").equals("jscustName")){
                            list=list.stream().filter(a->a.getJscustName().contains(pageSearch.get("selectValue"))).collect(Collectors.toList());
                        }
                    }
                    return Mono.just(list);
                }).map(a->R.ok().setResult(a));
    }
    @PostMapping("findAllQCZGRKD")
    public Mono<R> findAllQCZGRKD(@RequestBody Map map){
        String type=map.get("type").toString();
        String dataType=map.get("dataType").toString();
        String strNum=map.get("strNum").toString();
        String endNum=map.get("endNum").toString();
        String sup=map.get("sup").toString();
        String jssup=map.get("jssup").toString();
        String bdocumStyle=map.get("bdocumStyle")==null?"":map.get("bdocumStyle").toString();
        String cangku=map.get("cangku").toString();

        String strDate=map.get("strDate").toString();
        String endDate=map.get("endDate").toString();
        Map<String, String> pageSearch = ((HashMap<String,  String>) map.get("pageSearch"));
        // true
        if(map.get("strDate").toString().indexOf("-")>0){
            // 2022-01
            if(map.get("strDate").toString().length()==7){
                strDate=map.get("strDate").toString()+"-01";
                endDate=map.get("endDate").toString()+"-31";
            }
        }
        // 202201
        else{
            strDate=map.get("strDate").toString().substring(0,4)+"-"+map.get("strDate").toString().substring(4)+"-01";
            endDate=map.get("endDate").toString().substring(0,4)+"-"+map.get("endDate").toString().substring(4)+"-31";
        }
        // 按添加日期
        return warehousingRepository.findAllQCZGRKD(type,strDate+" 00:00:00",endDate+" 59:59:59").collectList()
                .flatMap(list->{
                    if(StrUtil.isNotBlank(bdocumStyle)){
                        list=list.stream().filter(a->a.getBdocumStyle().equals(bdocumStyle)).collect(Collectors.toList());
                    }
                    if(StrUtil.isNotBlank(strNum)){
                        list=list.stream().filter(a->a.getCcode().contains(strNum)&&a.getCcode().contains(endNum)).collect(Collectors.toList());
                    }
                    if(StrUtil.isNotBlank(dataType)){
                        list=list.stream().filter(a->a.getBcheck().equals(dataType)).collect(Collectors.toList());
                    }
                    if(StrUtil.isNotBlank(cangku)){
                        list=list.stream().filter(a->a.getCwhcode().equals(cangku)).collect(Collectors.toList());
                    }
                    if(StrUtil.isNotBlank(sup)){
                        list=list.stream().filter(a->a.getCvencode().equals(sup)).collect(Collectors.toList());
                    }
                    if(StrUtil.isNotBlank(jssup)){
                        list=list.stream().filter(a->a.getCvencodeJs().equals(jssup)).collect(Collectors.toList());
                    }
                    if(pageSearch!=null&&StrUtil.isNotBlank(pageSearch.get("selectValue"))){
                        if(pageSearch.get("selectType").equals("ccode")){
                            list=list.stream().filter(a->a.getCcode().contains(pageSearch.get("selectValue"))).collect(Collectors.toList());
                        }else if(pageSearch.get("selectType").equals("supName")){
                            list=list.stream().filter(a->a.getCustName().contains(pageSearch.get("selectValue"))).collect(Collectors.toList());
                        }
                    }
                    return Mono.just(list);
                }).map(a->R.ok().setResult(a));
    }

    /**
     * 查询 已审核、未关闭的期初到货单、到货单
     * @param map
     * @return
     */
    @PostMapping("findAllCGDHD_And_QCDHD")
    public Mono<R> findAllCGDHD_And_QCDHD(@RequestBody Map map){
        String iyear=map.get("iyear").toString();
        String cvencode=map.get("cvencode").toString();
        String cwhcode=map.get("cwhcode").toString();
        String startDate=map.get("startDate").toString();
        String endDate=map.get("endDate").toString();
        String titleValue=map.get("titleValue").toString();
        String type=map.get("type").toString();
        Map<String, String> searchMap = ((HashMap<String,  String>) map.get("searchConditon"));

        StringBuffer sb=new StringBuffer();
        if(StrUtil.isNotBlank(cwhcode)){
            sb.append("and sw.cwhcode='"+cwhcode+"' ");
        }

        String sql="select temp.* from ((select (select sum(cast(sws.base_quantity as float))\n" +
                "        from stock_warehousings sws\n" +
                "        where sws.ccode = sw.ccode) -\n" +
                "       (select sum(cast(coalesce(sws.isum_ruku,'0') as float))\n" +
                "        from stock_warehousings sws\n" +
                "        where sws.ccode = sw.ccode) sws_isum_ruku," +
                "(select sum(cast(sws.base_quantity as float)) from stock_warehousings sws where sws.ccode = sw.ccode) -\n" +
                "       (select sum(cast(coalesce(sws.isum_daohuo, '0') as float))\n" +
                "        from stock_warehousings sws\n" +
                "        where sws.ccode = sw.ccode) sws_isum_daohuo,"+
                "(select sum(cast(sws.base_quantity as float)) from stock_warehousings sws where sws.ccode = sw.ccode) -\n" +
                "       (select sum(cast(coalesce(sws.isum_fapiao, '0') as float))\n" +
                "        from stock_warehousings sws\n" +
                "        where sws.ccode = sw.ccode) sws_isum_fapiao," +
                "(select sum(cast(coalesce(sws.isum_tui_huo, '0') as float))\n" +
                "                from stock_warehousings sws\n" +
                "                where sws.ccode = sw.ccode) sws_isum_tuihuo,(select sum(cast(sws.base_quantity as float))\n" +
                "                     from stock_warehousings sws\n" +
                "                     where sws.ccode = sw.ccode) -\n" +
                "                    (select sum(cast(coalesce(sws.isum_tui_huo, '0') as float))\n" +
                "                     from stock_warehousings sws\n" +
                "                     where sws.ccode = sw.ccode) sws_isum_tuihuo,"+
                " dept.dept_name as dname,psn.psn_name as buname,psn2.real_name as cmaker_name,sw.*\n" +
                "from stock_warehousing sw\n" +
                " left join sys_department dept on dept.unique_code=sw.cdepcode\n" +
                " left join sys_psn psn on psn.id=sw.cpersoncode\n" +
                " left join _app_group_sys_user psn2 on psn2.id=sw.cmaker\n" +
                "where sw.bill_style='"+type+"' \n" +
                "  and (sw.bcheck is null or sw.bcheck = '1') and cvencode='"+cvencode+"' and sw.iyear='"+iyear+"' and sw.ddate between '"+startDate+"' and '"+endDate+"' and sw.bdocum_style='"+titleValue+"' "+sb+")" +
                " union all (select temp.* from (select\n" +
                "     (select sum(cast(sws.base_quantity as float))\n" +
                "         from stock_warehousings sws\n" +
                "         where sws.ccode = sw.ccode) -\n" +
                "        (select sum(cast(coalesce(sws.isum_ruku, '0') as float))\n" +
                "         from stock_warehousings sws\n" +
                "         where sws.ccode = sw.ccode) sws_isum_ruku,\n" +
                "        (select sum(cast(sws.base_quantity as float)) from stock_warehousings sws where sws.ccode = sw.ccode) -\n" +
                "        (select sum(cast(coalesce(sws.isum_daohuo, '0') as float))\n" +
                "         from stock_warehousings sws\n" +
                "         where sws.ccode = sw.ccode) sws_isum_daohuo,\n" +
                "        (select sum(cast(sws.base_quantity as float)) from stock_warehousings sws where sws.ccode = sw.ccode) -\n" +
                "        (select sum(cast(coalesce(sws.isum_fapiao, '0') as float))\n" +
                "         from stock_warehousings sws\n" +
                "         where sws.ccode = sw.ccode) sws_isum_fapiao,\n" +
                "        (select sum(cast(coalesce(sws.isum_tui_huo, '0') as float))\n" +
                "         from stock_warehousings sws\n" +
                "         where sws.ccode = sw.ccode) sws_isum_tuihuo,(select sum(cast(sws.base_quantity as float))\n" +
                "                     from stock_warehousings sws\n" +
                "                     where sws.ccode = sw.ccode) -\n" +
                "                    (select sum(cast(coalesce(sws.isum_tui_huo, '0') as float))\n" +
                "                     from stock_warehousings sws\n" +
                "                     where sws.ccode = sw.ccode) sws_isum_tuihuo,\n" +
                "        dept.dept_name as            dname,\n" +
                "        psn.psn_name   as            buname,\n" +
                "        psn2.real_name as            cmaker_name,\n" +
                "        sw.*\n" +
                " from stock_warehousing sw\n" +
                "          left join sys_department dept on dept.unique_code = sw.cdepcode\n" +
                "          left join sys_psn psn on psn.id = sw.cpersoncode\n" +
                "          left join _app_group_sys_user psn2 on psn2.id = sw.cmaker\n" +
                " where sw.bill_style in ('QC')\n" +
                "   and (sw.bcheck is null or sw.bcheck = '1')\n" +
                "   and cvencode = '"+cvencode+"' "+sb+" " +
                " ) as temp )) as temp  order by temp.bill_style desc, temp.ccode asc" ;
        // where cast(temp.sws_isum_ruku as float)>0
        return client.sql(sql).fetch().all().collectList()
        .flatMap(list->{
            List<StockWarehousingVo> listVo = JSON.parseArray(JSON.toJSONString(list), StockWarehousingVo.class);
            // 不是到货单  需排序 期初到货单
            if(!"CGDHD".equals(type)){
                listVo=listVo.stream().filter(a->!a.getBillStyle().equals("QC")).collect(Collectors.toList());
            }
            if(StrUtil.isNotBlank(searchMap.get("value"))){
                if(searchMap.get("requirement").equals("ccode")){
                    listVo=listVo.stream().filter(tx->tx.getCcode().contains(searchMap.get("value"))).collect(Collectors.toList());
                }else if(searchMap.get("requirement").equals("dname")){
                    listVo=listVo.stream().filter(tx->tx.getDname().contains(searchMap.get("value"))).collect(Collectors.toList());
                }else if(searchMap.get("requirement").equals("buname")){
                    listVo=listVo.stream().filter(tx->tx.getBuname().contains(searchMap.get("value"))).collect(Collectors.toList());
                }else if(searchMap.get("requirement").equals("cmakerName")){
                    listVo=listVo.stream().filter(tx->tx.getCmakerName().contains(searchMap.get("value"))).collect(Collectors.toList());
                }
            }
            return Mono.just(listVo);
        })
        .map(R::ok);
    }

    @PostMapping("findAllCGRKD_And_QCZGRKHD")
    public Mono<R> findAllCGRKD_And_QCZGRKHD(@RequestBody Map map){
        String iyear=map.get("iyear").toString();
        String cvencode=map.get("cvencode").toString();
        String cwhcode=map.get("cwhcode").toString();
        String startDate=map.get("startDate").toString();
        String endDate=map.get("endDate").toString();
        String titleValue=map.get("titleValue").toString();
        String type=map.get("type").toString();
        Map<String, String> searchMap = ((HashMap<String,  String>) map.get("searchConditon"));

        StringBuffer sb=new StringBuffer();
        if(StrUtil.isNotBlank(cwhcode)){
            sb.append("and sw.cwhcode='"+cwhcode+"' ");
        }
        if(StrUtil.isNotBlank(searchMap.get("value"))){
            sb.append("and sw.ccode like '%"+cwhcode+"%' ");
        }

        String sql="select temp.* from ((select (select sum(cast(sws.base_quantity as float))\n" +
                "        from stock_warehousings sws\n" +
                "        where sws.ccode = sw.ccode) -\n" +
                "       (select sum(cast(coalesce(sws.isum_ruku,'0') as float))\n" +
                "        from stock_warehousings sws\n" +
                "        where sws.ccode = sw.ccode) sws_isum_ruku," +
                "(select sum(cast(sws.base_quantity as float)) from stock_warehousings sws where sws.ccode = sw.ccode) -\n" +
                "       (select sum(cast(coalesce(sws.isum_daohuo, '0') as float))\n" +
                "        from stock_warehousings sws\n" +
                "        where sws.ccode = sw.ccode) sws_isum_daohuo,"+
                "(select sum(cast(sws.base_quantity as float)) from stock_warehousings sws where sws.ccode = sw.ccode) -\n" +
                "       (select sum(cast(coalesce(sws.isum_fapiao, '0') as float))\n" +
                "        from stock_warehousings sws\n" +
                "        where sws.ccode = sw.ccode) sws_isum_fapiao," +
                "(select sum(cast(coalesce(sws.isum_tui_huo, '0') as float))\n" +
                "                from stock_warehousings sws\n" +
                "                where sws.ccode = sw.ccode) sws_isum_tuihuo,(select sum(cast(sws.base_quantity as float))\n" +
                "                     from stock_warehousings sws\n" +
                "                     where sws.ccode = sw.ccode) -\n" +
                "                    (select sum(cast(coalesce(sws.isum_tui_huo, '0') as float))\n" +
                "                     from stock_warehousings sws\n" +
                "                     where sws.ccode = sw.ccode) sws_isum_tuihuo,"+
                " dept.dept_name as dname,psn.psn_name as buname,psn2.real_name as cmaker_name,sw.*\n" +
                "from stock_warehousing sw\n" +
                " left join sys_department dept on dept.unique_code=sw.cdepcode\n" +
                " left join sys_psn psn on psn.id=sw.cpersoncode\n" +
                " left join _app_group_sys_user psn2 on psn2.id=sw.cmaker\n" +
                "where sw.bill_style='"+type+"' \n" +
                "  and (sw.bcheck is null or sw.bcheck = '1') and cvencode='"+cvencode+"' and sw.iyear='"+iyear+"' and sw.ddate between '"+startDate+"' and '"+endDate+"' and sw.bdocum_style='"+titleValue+"' "+sb+")" +
                " union all (select temp.* from (select\n" +
                "     (select sum(cast(sws.base_quantity as float))\n" +
                "         from stock_warehousings sws\n" +
                "         where sws.ccode = sw.ccode) -\n" +
                "        (select sum(cast(coalesce(sws.isum_ruku, '0') as float))\n" +
                "         from stock_warehousings sws\n" +
                "         where sws.ccode = sw.ccode) sws_isum_ruku,\n" +
                "        (select sum(cast(sws.base_quantity as float)) from stock_warehousings sws where sws.ccode = sw.ccode) -\n" +
                "        (select sum(cast(coalesce(sws.isum_daohuo, '0') as float))\n" +
                "         from stock_warehousings sws\n" +
                "         where sws.ccode = sw.ccode) sws_isum_daohuo,\n" +
                "        (select sum(cast(sws.base_quantity as float)) from stock_warehousings sws where sws.ccode = sw.ccode) -\n" +
                "        (select sum(cast(coalesce(sws.isum_fapiao, '0') as float))\n" +
                "         from stock_warehousings sws\n" +
                "         where sws.ccode = sw.ccode) sws_isum_fapiao,\n" +
                "        (select sum(cast(coalesce(sws.isum_tui_huo, '0') as float))\n" +
                "         from stock_warehousings sws\n" +
                "         where sws.ccode = sw.ccode) sws_isum_tuihuo,(select sum(cast(sws.base_quantity as float))\n" +
                "                     from stock_warehousings sws\n" +
                "                     where sws.ccode = sw.ccode) -\n" +
                "                    (select sum(cast(coalesce(sws.isum_tui_huo, '0') as float))\n" +
                "                     from stock_warehousings sws\n" +
                "                     where sws.ccode = sw.ccode) sws_isum_tuihuo,\n" +
                "        dept.dept_name as            dname,\n" +
                "        psn.psn_name   as            buname,\n" +
                "        psn2.real_name as            cmaker_name,\n" +
                "        sw.*\n" +
                " from stock_warehousing sw\n" +
                "          left join sys_department dept on dept.unique_code = sw.cdepcode\n" +
                "          left join sys_psn psn on psn.id = sw.cpersoncode\n" +
                "          left join _app_group_sys_user psn2 on psn2.id = sw.cmaker\n" +
                " where sw.bill_style in ('QT')\n" +
                "   and (sw.bcheck is null or sw.bcheck = '1')\n" +
                "   and cvencode = '"+cvencode+"' "+sb+" " +
                " ) as temp )) as temp  order by temp.bill_style desc, temp.ccode asc" ;
        // where cast(temp.sws_isum_ruku as float)>0
        return client.sql(sql).fetch().all().collectList()
                .flatMap(list->{
                    List<StockWarehousingVo> listVo = JSON.parseArray(JSON.toJSONString(list), StockWarehousingVo.class);
                    // 不是到货单  需排序 期初到货单
                    if(!"CGDHD".equals(type)){
                        listVo=listVo.stream().filter(a->!a.getBillStyle().equals("QC")).collect(Collectors.toList());
                    }
                    return Mono.just(listVo);
                })
                .map(R::ok);
    }

    // 期间是否结账
    @PostMapping("findByStockPeriodIsClose")
    public Mono<R> findByStockPeriodIsClose(String iyear,String month){
        return warehousingRepository.findByStockPeriodIsClose(iyear,month).map(a->R.ok().setResult(a));
    }

    @PostMapping("findByWarSearch")
    public Mono<R> findByWarSearch(String billStyle,String ccode){
        return warehousingRepository.findBySearch(billStyle,"%"+ccode+"%").map(R::ok).defaultIfEmpty(R.ok().setResult(""));
    }

    @PostMapping("delStockWareZip")
    @Transactional
    public Mono<R> delStockWareZip(@RequestBody Map map){
        if (map.keySet().size() == 0) return Mono.just(R.error());
        String ccode= map.get("ccode").toString();
        Mono<Void> a=warehousingRepository.delAllList(Arrays.asList(ccode.split(","))).then();
        Mono<Void> b=warehousingsRepository.delAllList(Arrays.asList(ccode.split(","))).then();
        return Mono.zip(a,b).then(Mono.just(R.ok()));
    }

    /**
     * @description: 删除回冲单
     * @author: miao
     * @date: 2022/11/11 11:52
     * @param: [map]
     * @return: Mono<R>
     **/
    @PostMapping("delStockWareHCD")
    @Transactional
    public Mono<R> delStockWareHCD(@RequestBody Map map){
        if (map.keySet().size() == 0) return Mono.just(R.error());
        String sourcecode= map.get("sourcecode").toString();
        Mono<Void> a=warehousingRepository.deleteBySourcetype(sourcecode).then();
        Mono<Void> b=warehousingsRepository.delByHCD(sourcecode).then();
        return Mono.zip(a,b).then(Mono.just(R.ok()));
    }

    private Map<String, Object> assemblyCorrespondingRulesData(Map map) {
        String type = map.get("billStyle").toString();
        HashMap<String, Object> resut = new HashMap<>();
        switch (type) {
            case ("CGRKD"):
                StockWarehousing warehousing = new StockWarehousing();
                String sgspperson = map.containsKey("sgspperson") ? map.get("sgspperson").toString() : null; // 质检人
                String bcheckUser = map.containsKey("bcheckUser") ? map.get("bcheckUser").toString() : null; // 审核人
                warehousing.setId(map.containsKey("id") ? map.get("id").toString() : null).setCcode(map.containsKey("ccode") ? map.get("ccode").toString() : null) //单号
                        .setDdate(map.containsKey("ddate") ? map.get("ddate").toString().substring(0, 10) : null) // 日期
                        .setIyear(map.containsKey("ddate") ? map.get("ddate").toString().substring(0, 4) : null).setCmaker(map.containsKey("cmaker") ? map.get("cmaker").toString() : null) // 制单人
                        .setCvencode(map.containsKey("cvencode") ? map.get("cvencode").toString() : null) // 供应商
                        .setCvencodeJs(map.containsKey("cvencodeJs") ? map.get("cvencodeJs").toString() : null) // 结算供应商
                        .setCwhcode(map.containsKey("cwhcode") ? map.get("cwhcode")==null?null:map.get("cwhcode").toString() : null)  // 仓库
                        .setCdepcode(map.containsKey("cdepcode") ? map.get("cdepcode")==null?null:map.get("cdepcode").toString() : null) // 部门
                        .setSourcetype(map.containsKey("sourcetype") ? map.get("sourcetype")==null?null:map.get("sourcetype").toString() : null)
                        .setCpersoncode(map.containsKey("cpersoncode") ? map.get("cpersoncode")==null?null:map.get("cpersoncode").toString() : null) // 业务员
                        .setCwhcodeUser(map.containsKey("cwhcodeUser") ? map.get("cwhcodeUser")==null?null:map.get("cwhcodeUser").toString() : null) // 库管员
                        .setInvoiceStyle(map.containsKey("invoiceStyle") ? map.get("invoiceStyle")==null?null:map.get("invoiceStyle").toString() : null) // 发票类型
                        .setRate(map.containsKey("rate") ? map.get("rate")==null?null:map.get("rate").toString() : null) // 税率
                        .setCmemo(map.containsKey("cmemo") ? map.get("cmemo")==null?null: map.get("cmemo").toString(): null)
                        .setBdocumStyle(map.containsKey("bdocumStyle") ? map.get("bdocumStyle").toString() : null)
                        .setBstyle(map.containsKey("bstyle") ? map.get("bstyle")==null?null: map.get("bstyle").toString(): null)
                        .setJiesuanStatus(map.containsKey("jiesuanStatus") ? map.get("jiesuanStatus")==null?null: map.get("jiesuanStatus").toString(): null)
                        .setCmakerTime(map.get("cmakerTime").toString())
                        .setBillStyle(type);
                if (bcheckUser != null) {
                    warehousing.setBcheckUser(bcheckUser);
                    warehousing.setBcheck("1");
                    warehousing.setBcheckTime(DateUtil.now());
                }else{
                    warehousing.setBcheck("0");
                }
                String entryList = map.containsKey("entryList") ? map.get("entryList").toString() : ""; // 表体
                List<StockWarehousings> entrys = JSON.parseArray(entryList, StockWarehousings.class);
                BigDecimal squantitySum = new BigDecimal(0);
                BigDecimal squantity1Sum = new BigDecimal(0);
                BigDecimal squantity2Sum = new BigDecimal(0);
                BigDecimal icostSum = new BigDecimal(0);
                BigDecimal isumSum = new BigDecimal(0);
                BigDecimal taxAmountSum = new BigDecimal(0);
                for (StockWarehousings entry : entrys) {
                    entry.setId(null).setIyear(warehousing.getIyear())
                            .setBillStyle(warehousing.getBillStyle())
                            .setBdocumStyle(warehousing.getBdocumStyle())
                            .setDdate(warehousing.getDdate())
                            .setCcode(warehousing.getCcode())
                            .setCvencode(warehousing.getCvencode())
                            .setCmaker(warehousing.getCmaker()).setCdepcode(warehousing.getCdepcode())
                            .setBstyle(warehousing.getBstyle()).setCordercode(warehousing.getCcode())
                            .setCmakerTime(warehousing.getCmakerTime())
                            .setBatchId(StrUtil.isNotBlank(entry.getBatchId())?entry.getBatchId().trim():"")
                            .setPriceZangu(entry.getPrice())
                            .setIcostZangu(entry.getIcost());
                    if(StrUtil.isNotBlank(entry.getIsGive())&&(entry.getIsGive().equals("true") || entry.getIsGive().equals("1"))){
                        entry.setIsGive("1");
                    }else{
                        entry.setIsGive("0");
                    }
                    if (null != sgspperson) {
                        entry.setCgspstate("1");
                        entry.setSgspdate(DateUtil.today());
                        entry.setSgspperson(sgspperson);
                    }
                    if (bcheckUser == null) {
                        entry.setBcheck("0");
                    }
                    squantitySum = squantitySum.add(new BigDecimal(entry.getBaseQuantity() == null ? "0" : entry.getBaseQuantity()));
                    squantity1Sum = squantity1Sum.add(new BigDecimal(entry.getSubQuantity1() == null ? "0" : entry.getSubQuantity1()));
                    squantity2Sum = squantity2Sum.add(new BigDecimal(entry.getSubQuantity2() == null ? "0" : entry.getSubQuantity2()));
                    icostSum = icostSum.add(new BigDecimal(entry.getTaxprice() == null ? "0" : entry.getTaxprice()));
                    isumSum = isumSum.add(new BigDecimal(entry.getIsum() == null ? "0" : entry.getIsum()));
                    taxAmountSum = taxAmountSum.add(new BigDecimal(entry.getItaxprice() == null ? "0" : entry.getItaxprice()));
                }
                warehousing.setSquantity(keepDecimals(squantitySum, 10)).setSquantity1(keepDecimals(squantity1Sum, 10)).setSquantity2(keepDecimals(squantity2Sum, 10)).setIcost(keepDecimals(icostSum, 4)).setIsum(keepDecimals(isumSum, 4)).setTaxAmount(keepDecimals(taxAmountSum, 6));
                resut.put("master", warehousing);
                resut.put("sub", entrys);
                break;
            case ("QT"):
                warehousing = new StockWarehousing();
                bcheckUser = map.containsKey("bcheckUser") ? map.get("bcheckUser").toString() : null; // 审核人
                warehousing.setId(map.containsKey("id") ? map.get("id").toString() : null).setCcode(map.containsKey("ccode") ? map.get("ccode").toString() : null) //单号
                        .setDdate(map.containsKey("ddate") ? map.get("ddate").toString().substring(0, 10) : null) // 日期
                        .setIyear(map.containsKey("ddate") ? map.get("ddate").toString().substring(0, 4) : null).setCmaker(map.containsKey("cmaker") ? map.get("cmaker").toString() : null) // 制单人
                        .setCvencode(map.containsKey("cvencode") ? map.get("cvencode").toString() : null) // 供应商
                        .setCvencodeJs(map.containsKey("cvencodeJs") ? map.get("cvencodeJs").toString() : null) // 结算供应商
                        .setCwhcode(map.containsKey("cwhcode") ? map.get("cwhcode")==null?null:map.get("cwhcode").toString() : null)  // 仓库
                        .setSourcetype(map.containsKey("sourcetype") ? map.get("sourcetype")==null?null:map.get("sourcetype").toString() : null)
                        .setCpersoncode(map.containsKey("cpersoncode") ? map.get("cpersoncode")==null?null:map.get("cpersoncode").toString() : null) // 业务员
                        .setCwhcodeUser(map.containsKey("cwhcodeUser") ? map.get("cwhcodeUser")==null?null:map.get("cwhcodeUser").toString() : null) // 库管员
                        .setRate(map.containsKey("rate") ? map.get("rate")==null?null:map.get("rate").toString() : null) // 税率
                        .setCmemo(map.containsKey("cmemo") ? map.get("cmemo")==null?null: map.get("cmemo").toString(): null)
                        .setBdocumStyle(map.containsKey("bdocumStyle") ? map.get("bdocumStyle").toString() : null)
                        .setBstyle(map.containsKey("bstyle") ? map.get("bstyle")==null?null: map.get("bstyle").toString(): null) // 入库类别
                        .setCmakerTime(map.get("cmakerTime").toString())
                        .setBillStyle(type);
                if (bcheckUser != null) {
                    warehousing.setBcheckUser(bcheckUser);
                    warehousing.setBcheck("1");
                    warehousing.setBcheckTime(DateUtil.now());
                }else{
                    warehousing.setBcheck("0");
                }
                entryList = map.containsKey("entryList") ? map.get("entryList").toString() : ""; // 表体
                entrys = JSON.parseArray(entryList, StockWarehousings.class);
                squantitySum = new BigDecimal(0);
                squantity1Sum = new BigDecimal(0);
                squantity2Sum = new BigDecimal(0);
                icostSum = new BigDecimal(0);
                isumSum = new BigDecimal(0);
                taxAmountSum = new BigDecimal(0);
                for (StockWarehousings entry : entrys) {
                    entry.setId(null).setIyear(warehousing.getIyear())
                            .setBillStyle(warehousing.getBillStyle())
                            .setBdocumStyle(warehousing.getBdocumStyle())
                            .setDdate(warehousing.getDdate())
                            .setCcode(warehousing.getCcode())
                            .setCvencode(warehousing.getCvencode())
                            .setCmaker(warehousing.getCmaker()).setCdepcode(warehousing.getCdepcode())
                            .setBstyle(warehousing.getBstyle()).setCordercode(warehousing.getCcode())
                            .setCmakerTime(warehousing.getCmakerTime())
                            .setBatchId(StrUtil.isNotBlank(entry.getBatchId())?entry.getBatchId().trim():"")
                            .setPriceZangu(entry.getPrice())
                            .setIcostZangu(entry.getIcost());
                    if (bcheckUser == null) {
                        entry.setBcheck("0");
                    }
                    squantitySum = squantitySum.add(new BigDecimal(entry.getBaseQuantity() == null ? "0" : entry.getBaseQuantity()));
                    squantity1Sum = squantity1Sum.add(new BigDecimal(entry.getSubQuantity1() == null ? "0" : entry.getSubQuantity1()));
                    squantity2Sum = squantity2Sum.add(new BigDecimal(entry.getSubQuantity2() == null ? "0" : entry.getSubQuantity2()));
                    icostSum = icostSum.add(new BigDecimal(entry.getTaxprice() == null ? "0" : entry.getTaxprice()));
                    isumSum = isumSum.add(new BigDecimal(entry.getIsum() == null ? "0" : entry.getIsum()));
                    taxAmountSum = taxAmountSum.add(new BigDecimal(entry.getItaxprice() == null ? "0" : entry.getItaxprice()));
                }
                warehousing.setSquantity(keepDecimals(squantitySum, 10)).setSquantity1(keepDecimals(squantity1Sum, 10)).setSquantity2(keepDecimals(squantity2Sum, 10)).setIcost(keepDecimals(icostSum, 4)).setIsum(keepDecimals(isumSum, 4)).setTaxAmount(keepDecimals(taxAmountSum, 6));
                resut.put("master", warehousing);
                resut.put("sub", entrys);
                break;
            case ("CGDHD"):
                warehousing = new StockWarehousing();
                sgspperson = map.containsKey("sgspperson") ? map.get("sgspperson").toString() : null; // 质检人
                bcheckUser = map.containsKey("bcheckUser") ? map.get("bcheckUser")==null?null:map.get("bcheckUser").toString() : null; // 审核人
                warehousing.setId(map.containsKey("id") ? map.get("id").toString() : null).setCcode(map.containsKey("ccode") ? map.get("ccode").toString() : null) //单号
                        .setDdate(map.containsKey("ddate") ? map.get("ddate").toString().substring(0, 10) : null) // 日期
                        .setIyear(map.containsKey("ddate") ? map.get("ddate").toString().substring(0, 4) : null).setCmaker(map.containsKey("cmaker") ? map.get("cmaker").toString() : null) // 制单人
                        .setCvencode(map.containsKey("cvencode") ? map.get("cvencode").toString() : null) // 供应商
                        .setCvencodeJs(map.containsKey("cvencodeJs") ? map.get("cvencodeJs").toString() : null) // 结算供应商
                        .setCwhcode(map.containsKey("cwhcode") ? map.get("cwhcode")==null?null:map.get("cwhcode").toString() : null)  // 仓库
                        .setCdepcode(map.containsKey("cdepcode") ? map.get("cdepcode")==null?null:map.get("cdepcode").toString() : null) // 部门
                        .setSourcetype(map.containsKey("sourcetype") ? map.get("sourcetype")==null?null:map.get("sourcetype").toString() : null)
                        .setCpersoncode(map.containsKey("cpersoncode") ? map.get("cpersoncode")==null?null:map.get("cpersoncode").toString() : null) // 业务员
                        .setCwhcodeUser(map.containsKey("cwhcodeUser") ? map.get("cwhcodeUser")==null?null:map.get("cwhcodeUser").toString() : null) // 库管员
                        .setInvoiceStyle(map.containsKey("invoiceStyle") ? map.get("invoiceStyle")==null?null:map.get("invoiceStyle").toString() : null) // 发票类型
                        .setRate(map.containsKey("rate") ? map.get("rate")==null?null:map.get("rate").toString() : null) // 税率
                        .setCmemo(map.containsKey("cmemo") ? map.get("cmemo")==null?null: map.get("cmemo").toString(): null).setBdocumStyle(map.containsKey("bdocumStyle") ? map.get("bdocumStyle").toString() : null).setBillStyle(type)
                        .setBstyle(map.containsKey("bstyle") ? map.get("bstyle")==null?null: map.get("bstyle").toString(): null)
                        .setMethodPay(map.containsKey("methodPay") ? map.get("methodPay")==null?null: map.get("methodPay").toString(): null)
                        .setPayDate(map.containsKey("payDate") ? map.get("payDate")==null?null: map.get("payDate").toString(): null)
                        .setCmakerTime(map.get("cmakerTime").toString())
                        .setTheDeposit(map.containsKey("theDeposit") ? map.get("theDeposit")==null?null: map.get("theDeposit").toString(): null);
                if (bcheckUser != null) {
                    warehousing.setBcheckUser(bcheckUser);
                    warehousing.setBcheck("1");
                    warehousing.setBcheckTime(DateUtil.now());
                }else{
                    warehousing.setBcheck("0");
                }
                entryList = map.containsKey("entryList") ? map.get("entryList").toString() : ""; // 表体
                entrys = JSON.parseArray(entryList, StockWarehousings.class);
                squantitySum = new BigDecimal(0);
                squantity1Sum = new BigDecimal(0);
                squantity2Sum = new BigDecimal(0);
                icostSum = new BigDecimal(0);
                isumSum = new BigDecimal(0);
                taxAmountSum = new BigDecimal(0);
                for (StockWarehousings entry : entrys) {
                    entry.setId(null).setIyear(warehousing.getIyear())
                            .setBdocumStyle(warehousing.getBdocumStyle())
                            .setBillStyle(warehousing.getBillStyle())
                            .setBstyle(warehousing.getBstyle())
                            .setDdate(warehousing.getDdate())
                            .setCcode(warehousing.getCcode())
                            .setCvencode(warehousing.getCvencode())
                            .setCmaker(warehousing.getCmaker())
                            .setCdepcode(warehousing.getCdepcode())
                            .setCmakerTime(warehousing.getCmakerTime())
                            .setBatchId(StrUtil.isNotBlank(entry.getBatchId())?entry.getBatchId().trim():"");

                    if(StrUtil.isNotBlank(entry.getIsGive())&&(entry.getIsGive().equals("true") || entry.getIsGive().equals("1"))){
                        entry.setIsGive("1");
                    }else{
                        entry.setIsGive("0");
                    }

                    if (null != sgspperson) {
                        entry.setCgspstate("1");
                        entry.setSgspdate(DateUtil.today());
                        entry.setSgspperson(sgspperson);
                    }
                    if (bcheckUser == null) {
                        entry.setBcheck("0");
                    }
                    squantitySum = squantitySum.add(new BigDecimal(entry.getBaseQuantity() == null ? "0" : entry.getBaseQuantity()));
                    squantity1Sum = squantity1Sum.add(new BigDecimal(entry.getSubQuantity1() == null ? "0" : entry.getSubQuantity1()));
                    squantity2Sum = squantity2Sum.add(new BigDecimal(entry.getSubQuantity2() == null ? "0" : entry.getSubQuantity2()));
                    icostSum = icostSum.add(new BigDecimal(entry.getIcost() == null ? "0" : entry.getIcost()));
                    isumSum = isumSum.add(new BigDecimal(entry.getIsum() == null ? "0" : entry.getIsum()));;// miao：先等于无税金额
                    taxAmountSum = taxAmountSum.add(new BigDecimal(entry.getItaxprice() == null ? "0" : entry.getItaxprice()));
                }
                warehousing.setSquantity(keepDecimals(squantitySum, 10)).setSquantity1(keepDecimals(squantity1Sum, 10)).setSquantity2(keepDecimals(squantity2Sum, 10)).setIcost(keepDecimals(icostSum, 4)).setIsum(keepDecimals(isumSum, 4)).setTaxAmount(keepDecimals(taxAmountSum, 6));
                entrys.forEach(mx->{
                    if(StrUtil.isBlank(mx.getIsum())||StrUtil.isBlank("0")){
                        mx.setIsum(mx.getIcost());
                    }
                });
                resut.put("master", warehousing);
                resut.put("sub", entrys);
                break;
            case ("CGDD"):
                warehousing = new StockWarehousing();
                sgspperson = map.containsKey("sgspperson") ? map.get("sgspperson").toString() : null; // 质检人
                bcheckUser = map.containsKey("bcheckUser") ? map.get("bcheckUser")==null?null:map.get("bcheckUser").toString() : null; // 审核人
                warehousing.setId(map.containsKey("id") ? map.get("id").toString() : null).setCcode(map.containsKey("ccode") ? map.get("ccode").toString() : null) //单号
                        .setDdate(map.containsKey("ddate") ? map.get("ddate").toString().substring(0, 10) : null) // 日期
                        .setIyear(map.containsKey("ddate") ? map.get("ddate").toString().substring(0, 4) : null).setCmaker(map.containsKey("cmaker") ? map.get("cmaker").toString() : null) // 制单人
                        .setCvencode(map.containsKey("cvencode") ? map.get("cvencode").toString() : null) // 供应商
                        .setCdepcode(map.containsKey("cdepcode") ? map.get("cdepcode")==null?null:map.get("cdepcode").toString() : null) // 部门
                        .setCpersoncode(map.containsKey("cpersoncode") ? map.get("cpersoncode")==null?null:map.get("cpersoncode").toString() : null) // 业务员
                        .setRate(map.containsKey("rate") ? map.get("rate")==null?null:map.get("rate").toString() : null) // 税率
                        .setCvencodeContact(map.containsKey("cvencodeContact") ? map.get("cvencodeContact")==null?null:map.get("cvencodeContact").toString() : null) // 联系人&电话
                        .setCmemo(map.containsKey("cmemo") ? map.get("cmemo")==null?null: map.get("cmemo").toString(): null).setBdocumStyle(map.containsKey("bdocumStyle") ? map.get("bdocumStyle").toString() : null).setBillStyle(type)
                        .setBstyle(map.containsKey("bstyle") ? map.get("bstyle")==null?null: map.get("bstyle").toString(): null)
                        .setBcloser("0")
                        .setSourcetype(map.containsKey("sourcetype") ? map.get("sourcetype")==null?null:map.get("sourcetype").toString() : null)
                        .setInvoiceStyle(map.containsKey("invoiceStyle") ? map.get("invoiceStyle")==null?null:map.get("invoiceStyle").toString() : null) // 发票类型
                        .setMethodPay(map.containsKey("methodPay") ? map.get("methodPay")==null?null: map.get("methodPay").toString(): null)
                        .setPayDate(map.containsKey("payDate") ? map.get("payDate")==null?null: map.get("payDate").toString(): null)
                        .setCmakerTime(map.get("cmakerTime").toString())
                        .setTheDeposit(map.containsKey("theDeposit") ? map.get("theDeposit")==null?null: map.get("theDeposit").toString(): null);
                if (bcheckUser != null) {
                    warehousing.setBcheckUser(bcheckUser);
                    warehousing.setBcheck("1");
                    warehousing.setBcheckTime(DateUtil.now());
                }else{
                    warehousing.setBcheck("0");
                }
                entryList = map.containsKey("entryList") ? map.get("entryList").toString() : ""; // 表体
                entrys = JSON.parseArray(entryList, StockWarehousings.class);
                squantitySum = new BigDecimal(0);
                squantity1Sum = new BigDecimal(0);
                squantity2Sum = new BigDecimal(0);
                icostSum = new BigDecimal(0);
                isumSum = new BigDecimal(0);
                taxAmountSum = new BigDecimal(0);
                for (StockWarehousings entry : entrys) {
                    entry.setId(null)
                            .setIyear(warehousing.getIyear())
                            .setBdocumStyle(warehousing.getBdocumStyle())
                            .setBillStyle(warehousing.getBillStyle())
                            .setBstyle(warehousing.getBstyle())
                            .setDdate(warehousing.getDdate())
                            .setCcode(warehousing.getCcode())
                            .setCvencode(warehousing.getCvencode())
                            .setCmaker(warehousing.getCmaker())
                            .setCdepcode(warehousing.getCdepcode())
                            .setCmakerTime(warehousing.getCmakerTime())
                            .setBatchId(StrUtil.isNotBlank(entry.getBatchId())?entry.getBatchId().trim():"");
                    if(StrUtil.isNotBlank(entry.getIsGive())&&(entry.getIsGive().equals("true") || entry.getIsGive().equals("1"))){
                        entry.setIsGive("1");
                    }else{
                        entry.setIsGive("0");
                    }
                    if (null != sgspperson) {
                        entry.setCgspstate("1");
                        entry.setSgspdate(DateUtil.today());
                        entry.setSgspperson(sgspperson);
                    }
                    if (bcheckUser == null) {
                        entry.setBcheck("0");
                    }
                    squantitySum = squantitySum.add(new BigDecimal(entry.getBaseQuantity() == null ? "0" : entry.getBaseQuantity()));
                    squantity1Sum = squantity1Sum.add(new BigDecimal(entry.getSubQuantity1() == null ? "0" : entry.getSubQuantity1()));
                    squantity2Sum = squantity2Sum.add(new BigDecimal(entry.getSubQuantity2() == null ? "0" : entry.getSubQuantity2()));
                    icostSum = icostSum.add(new BigDecimal(entry.getIcost() == null ? "0" : entry.getIcost()));
                    isumSum = isumSum.add(new BigDecimal(entry.getIsum() == null ? "0" : entry.getIsum()));;// miao：先等于无税金额
                    taxAmountSum = taxAmountSum.add(new BigDecimal(entry.getItaxprice() == null ? "0" : entry.getItaxprice()));
                }
                warehousing.setSquantity(keepDecimals(squantitySum, 10)).setSquantity1(keepDecimals(squantity1Sum, 10)).setSquantity2(keepDecimals(squantity2Sum, 10)).setIcost(keepDecimals(icostSum, 4)).setIsum(keepDecimals(isumSum, 4)).setTaxAmount(keepDecimals(taxAmountSum, 6));
                entrys.forEach(mx->{
                    if(StrUtil.isBlank(mx.getIsum())||StrUtil.isBlank("0")){
                        mx.setIsum(mx.getIcost());
                    }
                });
                resut.put("master", warehousing);
                resut.put("sub", entrys);
                break;
            case ("QTRKD"):
                warehousing = new StockWarehousing();
                sgspperson = map.containsKey("sgspperson") ? map.get("sgspperson").toString() : null; // 质检人
                bcheckUser = map.containsKey("bcheckUser") ? map.get("bcheckUser").toString() : null; // 审核人
                warehousing.setId(map.containsKey("id") ? map.get("id").toString() : null).setCcode(map.containsKey("ccode") ? map.get("ccode").toString() : null) //单号
                        .setDdate(map.containsKey("ddate") ? map.get("ddate").toString().substring(0, 10) : null) // 日期
                        .setIyear(map.containsKey("ddate") ? map.get("ddate").toString().substring(0, 4) : null).setCmaker(map.containsKey("cmaker") ? map.get("cmaker").toString() : null) // 制单人
                        .setCvencode(map.containsKey("cvencode") ? map.get("cvencode").toString() : null) // 供应商
                        .setCwhcode(map.containsKey("cwhcode") ? map.get("cwhcode")==null?null:map.get("cwhcode").toString() : null)  // 仓库
                        .setCdepcode(map.containsKey("cdepcode") ? map.get("cdepcode")==null?null:map.get("cdepcode").toString() : null) // 部门
                        .setCpersoncode(map.containsKey("cpersoncode") ? map.get("cpersoncode")==null?null:map.get("cpersoncode").toString() : null) // 业务员
                        .setCwhcodeUser(map.containsKey("cwhcodeUser") ? map.get("cwhcodeUser")==null?null:map.get("cwhcodeUser").toString() : null) // 库管员
                        .setInvoiceStyle(map.containsKey("invoiceStyle") ? map.get("invoiceStyle")==null?null:map.get("invoiceStyle").toString() : null) // 发票类型
                        .setUnitType(map.containsKey("unitType") ? map.get("unitType")==null?null:map.get("unitType").toString() : null) // 单位类型:1其他,2客户,3供应商,4员工,5项目
                        .setUnitValue(map.containsKey("unitValue") ? map.get("unitValue")==null?null:map.get("unitValue").toString() : null) // 业务单位
                        .setCmemo(map.containsKey("cmemo") ? map.get("cmemo")==null?null: map.get("cmemo").toString(): null).setBdocumStyle(map.containsKey("bdocumStyle") ? map.get("bdocumStyle").toString() : null)
                        .setBillStyle(type).setBstyle(map.containsKey("bstyle") ? map.get("bstyle")==null?null: map.get("bstyle").toString(): null)
                        .setCmakerTime(map.get("cmakerTime").toString())
                        .setSourcetype(map.containsKey("sourcetype") ? map.get("sourcetype")==null?null:map.get("sourcetype").toString() : null);
                if (bcheckUser != null) {
                    warehousing.setBcheckUser(bcheckUser);
                    warehousing.setBcheck("1");
                    warehousing.setBcheckTime(DateUtil.now());
                }else{
                    warehousing.setBcheck("0");
                }
                entryList = map.containsKey("entryList") ? map.get("entryList").toString() : ""; // 表体
                entrys = JSON.parseArray(entryList, StockWarehousings.class);
                squantitySum = new BigDecimal(0);
                squantity1Sum = new BigDecimal(0);
                squantity2Sum = new BigDecimal(0);
                icostSum = new BigDecimal(0);
                isumSum = new BigDecimal(0);
                taxAmountSum = new BigDecimal(0);
                for (StockWarehousings entry : entrys) {
                    entry.setId(null)
                            .setIyear(warehousing.getIyear())
                            .setBdocumStyle(warehousing.getBdocumStyle())
                            .setBillStyle(warehousing.getBillStyle())
                            .setBstyle(warehousing.getBstyle())
                            .setDdate(warehousing.getDdate())
                            .setCcode(warehousing.getCcode())
                            .setCvencode(warehousing.getCvencode())
                            .setCmaker(warehousing.getCmaker())
                            .setCdepcode(warehousing.getCdepcode())
                            .setCordercode(warehousing.getCcode())
                            .setCmakerTime(warehousing.getCmakerTime())
                            .setBatchId(StrUtil.isNotBlank(entry.getBatchId())?entry.getBatchId().trim():"");
                    if(StrUtil.isNotBlank(entry.getIsGive())&&(entry.getIsGive().equals("true") || entry.getIsGive().equals("1"))){
                        entry.setIsGive("1");
                    }else{
                        entry.setIsGive("0");
                    }
                    if (null != sgspperson) {
                        entry.setCgspstate("1");
                        entry.setSgspdate(DateUtil.today());
                        entry.setSgspperson(sgspperson);
                    }
                    if (bcheckUser == null) {
                        entry.setBcheck("0");
                    }
                    squantitySum = squantitySum.add(new BigDecimal(entry.getBaseQuantity() == null ? "0" : entry.getBaseQuantity()));
                    squantity1Sum = squantity1Sum.add(new BigDecimal(entry.getSubQuantity1() == null ? "0" : entry.getSubQuantity1()));
                    squantity2Sum = squantity2Sum.add(new BigDecimal(entry.getSubQuantity2() == null ? "0" : entry.getSubQuantity2()));
                    icostSum = icostSum.add(new BigDecimal(entry.getIcost() == null ? "0" : entry.getIcost()));
                    isumSum = isumSum.add(new BigDecimal(entry.getIsum() == null ? "0" : entry.getIsum()));
                    taxAmountSum = taxAmountSum.add(new BigDecimal(entry.getItaxprice() == null ? "0" : entry.getItaxprice()));
                }
                warehousing.setSquantity(keepDecimals(squantitySum, 10)).setSquantity1(keepDecimals(squantity1Sum, 10)).setSquantity2(keepDecimals(squantity2Sum, 10)).setIcost(keepDecimals(icostSum, 4)).setIsum(keepDecimals(isumSum, 4)).setTaxAmount(keepDecimals(taxAmountSum, 6));
                resut.put("master", warehousing);
                resut.put("sub", entrys);
                break;
            case ("QC"):
                warehousing = new StockWarehousing();
                sgspperson = map.containsKey("sgspperson") ? map.get("sgspperson").toString() : null; // 质检人
                bcheckUser = map.containsKey("bcheckUser") ? map.get("bcheckUser")==null?null:map.get("bcheckUser").toString() : null; // 审核人
                warehousing.setId(map.containsKey("id") ? map.get("id").toString() : null).setCcode(map.containsKey("ccode") ? map.get("ccode").toString() : null) //单号
                        .setDdate(map.containsKey("ddate") ? map.get("ddate").toString().substring(0, 10) : null) // 日期
                        .setIyear(map.get("iyear").toString()).setCmaker(map.containsKey("cmaker") ? map.get("cmaker").toString() : null) // 制单人
                        .setCvencode(map.containsKey("cvencode") ? map.get("cvencode").toString() : null) // 供应商
                        .setCvencodeJs(map.containsKey("cvencodeJs") ? map.get("cvencodeJs").toString() : null) // 结算供应商
                        .setCwhcode(map.containsKey("cwhcode") ? map.get("cwhcode")==null?null:map.get("cwhcode").toString() : null)  // 仓库
                        .setCdepcode(map.containsKey("cdepcode") ? map.get("cdepcode")==null?null:map.get("cdepcode").toString() : null) // 部门
                        .setCpersoncode(map.containsKey("cpersoncode") ? map.get("cpersoncode")==null?null:map.get("cpersoncode").toString() : null) // 业务员
                        .setCwhcodeUser(map.containsKey("cwhcodeUser") ? map.get("cwhcodeUser")==null?null:map.get("cwhcodeUser").toString() : null) // 库管员
                        .setInvoiceStyle(map.containsKey("invoiceStyle") ? map.get("invoiceStyle")==null?null:map.get("invoiceStyle").toString() : null) // 发票类型
                        .setRate(map.containsKey("rate") ? map.get("rate")==null?null:map.get("rate").toString() : null) // 税率
                        .setCmemo(map.containsKey("cmemo") ? map.get("cmemo")==null?null: map.get("cmemo").toString(): null).setBdocumStyle(map.containsKey("bdocumStyle") ? map.get("bdocumStyle").toString() : null)
                        .setBillStyle(type).setBstyle(map.containsKey("bstyle") ? map.get("bstyle")==null?null: map.get("bstyle").toString(): null)
                        .setCmakerTime(map.get("cmakerTime").toString())
                        .setMethodPay(map.containsKey("methodPay") ? map.get("methodPay")==null?null: map.get("methodPay").toString(): null)
                        .setPayDate(map.containsKey("payDate") ? map.get("payDate")==null?null: map.get("payDate").toString(): null)
                        .setSourcetype(map.containsKey("sourcetype") ? map.get("sourcetype")==null?null:map.get("sourcetype").toString() : null);
                if (bcheckUser != null) {
                    warehousing.setBcheckUser(bcheckUser);
                    warehousing.setBcheck("1");
                    warehousing.setBcheckTime(DateUtil.now());
                }else{
                    warehousing.setBcheck("0");
                }
                entryList = map.containsKey("entryList") ? map.get("entryList").toString() : ""; // 表体
                entrys = JSON.parseArray(entryList, StockWarehousings.class);
                squantitySum = new BigDecimal(0);
                squantity1Sum = new BigDecimal(0);
                squantity2Sum = new BigDecimal(0);
                icostSum = new BigDecimal(0);
                isumSum = new BigDecimal(0);
                taxAmountSum = new BigDecimal(0);
                for (StockWarehousings entry : entrys) {
                    entry.setId(null)
                            .setBdocumStyle(warehousing.getBdocumStyle())
                            .setIyear(warehousing.getIyear())
                            .setBillStyle(warehousing.getBillStyle())
                            .setDdate(warehousing.getDdate())
                            .setCcode(warehousing.getCcode())
                            .setCvencode(warehousing.getCvencode())
                            .setCmaker(warehousing.getCmaker())
                            .setCdepcode(warehousing.getCdepcode())
                            .setBstyle(warehousing.getBstyle())
                            .setCmakerTime(warehousing.getCmakerTime())
                            .setBatchId(StrUtil.isNotBlank(entry.getBatchId())?entry.getBatchId().trim():"");
                    if(StrUtil.isNotBlank(entry.getIsGive())&&(entry.getIsGive().equals("true") || entry.getIsGive().equals("1"))){
                        entry.setIsGive("1");
                    }else{
                        entry.setIsGive("0");
                    }
                    if (null != sgspperson) {
                        entry.setCgspstate("1");
                        entry.setSgspdate(DateUtil.today());
                        entry.setSgspperson(sgspperson);
                    }
                    if (bcheckUser == null) {
                        entry.setBcheck("0");
                    }

                    squantitySum = squantitySum.add(new BigDecimal(entry.getBaseQuantity() == null ? "0" : entry.getBaseQuantity()));
                    squantity1Sum = squantity1Sum.add(new BigDecimal(entry.getSubQuantity1() == null ? "0" : entry.getSubQuantity1()));
                    squantity2Sum = squantity2Sum.add(new BigDecimal(entry.getSubQuantity2() == null ? "0" : entry.getSubQuantity2()));
                    icostSum = icostSum.add(new BigDecimal(entry.getIcost() == null ? "0" : entry.getIcost()));
                    isumSum = isumSum.add(new BigDecimal(entry.getIsum() == null ? "0" : entry.getIsum()));;// miao：先等于无税金额
                    taxAmountSum = taxAmountSum.add(new BigDecimal(entry.getItaxprice() == null ? "0" : entry.getItaxprice()));
                }
                warehousing.setSquantity(keepDecimals(squantitySum, 10)).setSquantity1(keepDecimals(squantity1Sum, 10)).setSquantity2(keepDecimals(squantity2Sum, 10)).setIcost(keepDecimals(icostSum, 4)).setIsum(keepDecimals(isumSum, 4)).setTaxAmount(keepDecimals(taxAmountSum, 6));
                entrys.forEach(mx->{
                    if(StrUtil.isBlank(mx.getIsum())||StrUtil.isBlank("0")){
                        mx.setIsum(mx.getIcost());
                    }
                });
                resut.put("master", warehousing);
                resut.put("sub", entrys);
                break;
            case ("CGFP"):
                warehousing = new StockWarehousing();
                bcheckUser = map.containsKey("bcheckUser") ? map.get("bcheckUser")==null?null:map.get("bcheckUser").toString() : null; // 审核人
                warehousing.setId(map.containsKey("id") ? map.get("id").toString() : null).setCcode(map.containsKey("ccode") ? map.get("ccode").toString() : null) //单号
                        .setDdate(map.containsKey("ddate") ? map.get("ddate").toString().substring(0, 10) : null) // 日期
                        .setIyear(map.containsKey("ddate") ? map.get("ddate").toString().substring(0, 4) : null).setCmaker(map.containsKey("cmaker") ? map.get("cmaker").toString() : null) // 制单人
                        .setCvencode(map.containsKey("cvencode") ? map.get("cvencode").toString() : null) // 供应商
                        .setCvencodeJs(map.containsKey("cvencodeJs") ? map.get("cvencodeJs").toString() : null) // 结算供应商
                        .setCwhcode(map.containsKey("cwhcode") ? map.get("cwhcode")==null?null:map.get("cwhcode").toString() : null)  // 仓库
                        .setCdepcode(map.containsKey("cdepcode") ? map.get("cdepcode")==null?null:map.get("cdepcode").toString() : null) // 部门
                        .setCpersoncode(map.containsKey("cpersoncode") ? map.get("cpersoncode")==null?null:map.get("cpersoncode").toString() : null) // 业务员
                        .setRate(map.containsKey("rate") ? map.get("rate")==null?null:map.get("rate").toString() : null) // 税率
                        .setCmemo(map.containsKey("cmemo") ? map.get("cmemo")==null?null: map.get("cmemo").toString(): null)
                        .setBdocumStyle(map.containsKey("bdocumStyle") ? map.get("bdocumStyle").toString() : null)
                        .setBillStyle(type)
                        .setBillCode(map.get("billCode")==null?"":map.get("billCode").toString())
                        .setBillNumber(map.get("billNumber")==null?"":map.get("billNumber").toString())
                        .setBillDate(map.get("billDate")==null?"":map.get("billDate").toString().split(" ")[0])
                        .setInvoiceStyle(map.containsKey("invoiceStyle") ? map.get("invoiceStyle")==null?null:map.get("invoiceStyle").toString() : null) // 发票类型
                        .setCmakerTime(map.get("cmakerTime").toString())
                        .setBstyle(map.containsKey("bstyle") ? map.get("bstyle")==null?null: map.get("bstyle").toString(): null);
                if (bcheckUser != null) {
                    warehousing.setBcheckUser(bcheckUser);
                    warehousing.setBcheck("1");
                    warehousing.setBcheckTime(DateUtil.now());
                }else{
                    warehousing.setBcheck("0");
                }
                entryList = map.containsKey("entryList") ? map.get("entryList").toString() : ""; // 表体
                entrys = JSON.parseArray(entryList, StockWarehousings.class);
                squantitySum = new BigDecimal(0);
                squantity1Sum = new BigDecimal(0);
                squantity2Sum = new BigDecimal(0);
                icostSum = new BigDecimal(0);
                isumSum = new BigDecimal(0);
                taxAmountSum = new BigDecimal(0);
                for (StockWarehousings entry : entrys) {
                    entry.setId(null)
                            .setIyear(warehousing.getIyear())
                            .setBdocumStyle(warehousing.getBdocumStyle())
                            .setBillStyle(warehousing.getBillStyle())
                            .setBstyle(warehousing.getBstyle())
                            .setDdate(warehousing.getDdate())
                            .setCcode(warehousing.getCcode())
                            .setCvencode(warehousing.getCvencode())
                            .setCmaker(warehousing.getCmaker())
                            .setCdepcode(warehousing.getCdepcode())
                            .setCmakerTime(warehousing.getCmakerTime())
                            .setBatchId(StrUtil.isNotBlank(entry.getBatchId())?entry.getBatchId().trim():"");
                    if(StrUtil.isNotBlank(entry.getIsGive())&&(entry.getIsGive().equals("true") || entry.getIsGive().equals("1"))){
                        entry.setIsGive("1");
                    }else{
                        entry.setIsGive("0");
                    }
                    if (bcheckUser == null) {
                        entry.setBcheck("0");
                    }
                    squantitySum = squantitySum.add(new BigDecimal(entry.getBaseQuantity() == null ? "0" : entry.getBaseQuantity()));
                    squantity1Sum = squantity1Sum.add(new BigDecimal(entry.getSubQuantity1() == null ? "0" : entry.getSubQuantity1()));
                    squantity2Sum = squantity2Sum.add(new BigDecimal(entry.getSubQuantity2() == null ? "0" : entry.getSubQuantity2()));
                    icostSum = icostSum.add(new BigDecimal(entry.getIcost() == null ? "0" : entry.getIcost()));
                    isumSum = isumSum.add(new BigDecimal(entry.getIsum() == null ? "0" : entry.getIsum()));;// miao：先等于无税金额
                    taxAmountSum = taxAmountSum.add(new BigDecimal(entry.getItaxprice() == null ? "0" : entry.getItaxprice()));
                }
                warehousing.setSquantity(keepDecimals(squantitySum, 10)).setSquantity1(keepDecimals(squantity1Sum, 10)).setSquantity2(keepDecimals(squantity2Sum, 10)).setIcost(keepDecimals(icostSum, 4)).setIsum(keepDecimals(isumSum, 4)).setTaxAmount(keepDecimals(taxAmountSum, 6));
                entrys.forEach(mx->{
                    if(StrUtil.isBlank(mx.getIsum())||StrUtil.isBlank("0")){
                        mx.setIsum(mx.getIcost());
                    }
                });
                resut.put("master", warehousing);
                resut.put("sub", entrys);
                break;
            case ("QCCGFP"):
                warehousing = new StockWarehousing();
                bcheckUser = map.containsKey("bcheckUser") ? map.get("bcheckUser")==null?null:map.get("bcheckUser").toString() : null; // 审核人
                warehousing.setId(map.containsKey("id") ? map.get("id").toString() : null).setCcode(map.containsKey("ccode") ? map.get("ccode").toString() : null) //单号
                        .setDdate(map.containsKey("ddate") ? map.get("ddate").toString().substring(0, 10) : null) // 日期
                        .setIyear(map.containsKey("ddate") ? map.get("ddate").toString().substring(0, 4) : null).setCmaker(map.containsKey("cmaker") ? map.get("cmaker").toString() : null) // 制单人
                        .setCvencode(map.containsKey("cvencode") ? map.get("cvencode").toString() : null) // 供应商
                        .setCvencodeJs(map.containsKey("cvencodeJs") ? map.get("cvencodeJs").toString() : null) // 结算供应商
                        .setCwhcode(map.containsKey("cwhcode") ? map.get("cwhcode")==null?null:map.get("cwhcode").toString() : null)  // 仓库
                        .setCdepcode(map.containsKey("cdepcode") ? map.get("cdepcode")==null?null:map.get("cdepcode").toString() : null) // 部门
                        .setCpersoncode(map.containsKey("cpersoncode") ? map.get("cpersoncode")==null?null:map.get("cpersoncode").toString() : null) // 业务员
                        .setRate(map.containsKey("rate") ? map.get("rate")==null?null:map.get("rate").toString() : null) // 税率
                        .setCmemo(map.containsKey("cmemo") ? map.get("cmemo")==null?null: map.get("cmemo").toString(): null)
                        .setBdocumStyle(map.containsKey("bdocumStyle") ? map.get("bdocumStyle").toString() : null)
                        .setBillStyle(type)
                        .setBillCode(map.containsKey("billCode") ? map.get("billCode").toString() : null)
                        .setBillNumber(map.containsKey("billNumber") ? map.get("billNumber").toString() : null)
                        .setBillDate(map.containsKey("billDate") ? map.get("billDate").toString().split(" ")[0] : null)
                        .setInvoiceStyle(map.containsKey("invoiceStyle") ? map.get("invoiceStyle")==null?null:map.get("invoiceStyle").toString() : null) // 发票类型
                        .setCmakerTime(map.get("cmakerTime").toString())
                        .setBstyle(map.containsKey("bstyle") ? map.get("bstyle")==null?null: map.get("bstyle").toString(): null);
                if (bcheckUser != null) {
                    warehousing.setBcheckUser(bcheckUser);
                    warehousing.setBcheck("1");
                    warehousing.setBcheckTime(DateUtil.now());
                }else{
                    warehousing.setBcheck("0");
                }
                entryList = map.containsKey("entryList") ? map.get("entryList").toString() : ""; // 表体
                entrys = JSON.parseArray(entryList, StockWarehousings.class);
                squantitySum = new BigDecimal(0);
                squantity1Sum = new BigDecimal(0);
                squantity2Sum = new BigDecimal(0);
                icostSum = new BigDecimal(0);
                isumSum = new BigDecimal(0);
                taxAmountSum = new BigDecimal(0);
                for (StockWarehousings entry : entrys) {
                    entry.setId(null)
                            .setIyear(warehousing.getIyear())
                            .setBdocumStyle(warehousing.getBdocumStyle())
                            .setBillStyle(warehousing.getBillStyle())
                            .setBstyle(warehousing.getBstyle())
                            .setDdate(warehousing.getDdate())
                            .setCcode(warehousing.getCcode())
                            .setCvencode(warehousing.getCvencode())
                            .setCmaker(warehousing.getCmaker())
                            .setCdepcode(warehousing.getCdepcode())
                            .setCmakerTime(warehousing.getCmakerTime())
                            .setBatchId(StrUtil.isNotBlank(entry.getBatchId())?entry.getBatchId().trim():"");
                    if(StrUtil.isNotBlank(entry.getIsGive())&&(entry.getIsGive().equals("true") || entry.getIsGive().equals("1"))){
                        entry.setIsGive("1");
                    }else{
                        entry.setIsGive("0");
                    }
                    if (bcheckUser == null) {
                        entry.setBcheck("0");
                    }
                    squantitySum = squantitySum.add(new BigDecimal(entry.getBaseQuantity() == null ? "0" : entry.getBaseQuantity()));
                    squantity1Sum = squantity1Sum.add(new BigDecimal(entry.getSubQuantity1() == null ? "0" : entry.getSubQuantity1()));
                    squantity2Sum = squantity2Sum.add(new BigDecimal(entry.getSubQuantity2() == null ? "0" : entry.getSubQuantity2()));
                    icostSum = icostSum.add(new BigDecimal(entry.getIcost() == null ? "0" : entry.getIcost()));
                    isumSum = isumSum.add(new BigDecimal(entry.getIsum() == null ? "0" : entry.getIsum()));;// miao：先等于无税金额
                    taxAmountSum = taxAmountSum.add(new BigDecimal(entry.getItaxprice() == null ? "0" : entry.getItaxprice()));
                }
                warehousing.setSquantity(keepDecimals(squantitySum, 10)).setSquantity1(keepDecimals(squantity1Sum, 10)).setSquantity2(keepDecimals(squantity2Sum, 10)).setIcost(keepDecimals(icostSum, 4)).setIsum(keepDecimals(isumSum, 4)).setTaxAmount(keepDecimals(taxAmountSum, 6));
                entrys.forEach(mx->{
                    if(StrUtil.isBlank(mx.getIsum())||StrUtil.isBlank("0")){
                        mx.setIsum(mx.getIcost());
                    }
                });
                resut.put("master", warehousing);
                resut.put("sub", entrys);
                break;
            case ("RKTZD"):
                warehousing = new StockWarehousing();
                sgspperson = map.containsKey("sgspperson") ? map.get("sgspperson").toString() : null; // 质检人
                bcheckUser = map.containsKey("bcheckUser") ? map.get("bcheckUser").toString() : null; // 审核人
                warehousing.setId(map.containsKey("id") ? map.get("id").toString() : null).setCcode(map.containsKey("ccode") ? map.get("ccode").toString() : null) //单号
                        .setDdate(map.containsKey("ddate") ? map.get("ddate").toString().substring(0, 10) : null) // 日期
                        .setIyear(map.containsKey("ddate") ? map.get("ddate").toString().substring(0, 4) : null).setCmaker(map.containsKey("cmaker") ? map.get("cmaker").toString() : null) // 制单人
                        .setCvencode(map.containsKey("cvencode") ? map.get("cvencode").toString() : null) // 供应商
                        .setCvencodeJs(map.containsKey("cvencodeJs") ? map.get("cvencodeJs").toString() : null) // 结算供应商
                        .setCwhcode(map.containsKey("cwhcode") ? map.get("cwhcode")==null?null:map.get("cwhcode").toString() : null)  // 仓库
                        .setCdepcode(map.containsKey("cdepcode") ? map.get("cdepcode")==null?null:map.get("cdepcode").toString() : null) // 部门
                        .setCpersoncode(map.containsKey("cpersoncode") ? map.get("cpersoncode")==null?null:map.get("cpersoncode").toString() : null) // 业务员
                        .setCwhcodeUser(map.containsKey("cwhcodeUser") ? map.get("cwhcodeUser")==null?null:map.get("cwhcodeUser").toString() : null) // 库管员
                        .setInvoiceStyle(map.containsKey("invoiceStyle") ? map.get("invoiceStyle")==null?null:map.get("invoiceStyle").toString() : null) // 发票类型
                        .setRate(map.containsKey("rate") ? map.get("rate")==null?null:map.get("rate").toString() : null) // 税率
                        .setCmemo(map.containsKey("cmemo") ? map.get("cmemo")==null?null: map.get("cmemo").toString(): null)
                        .setBdocumStyle(map.containsKey("bdocumStyle") ? map.get("bdocumStyle").toString() : null)
                        .setBstyle(map.containsKey("bstyle") ? map.get("bstyle")==null?null: map.get("bstyle").toString(): null)
                        .setJiesuanStatus(map.containsKey("jiesuanStatus") ? map.get("jiesuanStatus")==null?null: map.get("jiesuanStatus").toString(): null)
                        .setCmakerTime(LocalDateTime.now().toString())
                        .setBillStyle(type);
                if (bcheckUser != null) {
                    warehousing.setBcheckUser(bcheckUser);
                    warehousing.setBcheck("1");
                    warehousing.setBcheckTime(DateUtil.now());
                }else{
                    warehousing.setBcheck("0");
                }
                entryList = map.containsKey("entryList") ? map.get("entryList").toString() : ""; // 表体
                 entrys = JSON.parseArray(entryList, StockWarehousings.class);
                squantitySum = new BigDecimal(0);
                squantity1Sum = new BigDecimal(0);
                squantity2Sum = new BigDecimal(0);
                icostSum = new BigDecimal(0);
                isumSum = new BigDecimal(0);
                taxAmountSum = new BigDecimal(0);
                for (StockWarehousings entry : entrys) {
                    entry.setId(null).setIyear(warehousing.getIyear())
                            .setBillStyle(warehousing.getBillStyle())
                            .setBdocumStyle(warehousing.getBdocumStyle())
                            .setDdate(warehousing.getDdate())
                            .setCcode(warehousing.getCcode())
                            .setCvencode(warehousing.getCvencode())
                            .setBaseQuantity("0.00")
                            .setIcost(entry.getThicost().toString())
                            .setCvencode(warehousing.getCvencode())
                            .setCmaker(warehousing.getCmaker()).setCdepcode(warehousing.getCdepcode())
                            .setBstyle(warehousing.getBstyle()).setCordercode(warehousing.getCcode())
                            .setBatchId(StrUtil.isNotBlank(entry.getBatchId())?entry.getBatchId().trim():"")
                            .setCmakerTime(warehousing.getCmakerTime());
                    if(StrUtil.isNotBlank(entry.getIsGive())&&(entry.getIsGive().equals("true") || entry.getIsGive().equals("1"))){
                        entry.setIsGive("1");
                    }else{
                        entry.setIsGive("0");
                    }
                    if (null != sgspperson) {
                        entry.setCgspstate("1");
                        entry.setSgspdate(DateUtil.today());
                        entry.setSgspperson(sgspperson);
                    }
                    if (bcheckUser == null) {
                        entry.setBcheck("0");
                    }
                    squantitySum = squantitySum.add(new BigDecimal(entry.getBaseQuantity() == null ? "0" : entry.getBaseQuantity()));
                    squantity1Sum = squantity1Sum.add(new BigDecimal(entry.getSubQuantity1() == null ? "0" : entry.getSubQuantity1()));
                    squantity2Sum = squantity2Sum.add(new BigDecimal(entry.getSubQuantity2() == null ? "0" : entry.getSubQuantity2()));
                    icostSum = icostSum.add(new BigDecimal(entry.getTaxprice() == null ? "0" : entry.getTaxprice()));
                    isumSum = isumSum.add(new BigDecimal(entry.getIsum() == null ? "0" : entry.getIsum()));
                    taxAmountSum = taxAmountSum.add(new BigDecimal(entry.getItaxprice() == null ? "0" : entry.getItaxprice()));
                }
                warehousing.setSquantity(keepDecimals(BigDecimal.ZERO, 10)).setSquantity1(keepDecimals(BigDecimal.ZERO, 10)).setSquantity2(keepDecimals(BigDecimal.ZERO, 10)).setIcost(keepDecimals(BigDecimal.ZERO, 4)).setIsum(keepDecimals(BigDecimal.ZERO, 4)).setTaxAmount(keepDecimals(BigDecimal.ZERO, 6));
                resut.put("master", warehousing);
                resut.put("sub", entrys);
                break;
            case (AppConstant.TEST_CODE):
                break;
            default:
                break;
        }
        return resut;
    }

    private String keepDecimals(BigDecimal b, int len) {
        StringBuilder s = new StringBuilder("");
        for (int i = 0; i < len; i++) s.append("0");
        DecimalFormat decimalFormat = new DecimalFormat("0." + s + "#");
        BigDecimal value = b.setScale(len, BigDecimal.ROUND_HALF_UP);
        return decimalFormat.format(value);
    }

    @PostMapping("delXyHCD")
    public Mono<R> delXyHCD(@RequestBody Map map){
        String ccode=map.get("ccode").toString();
        return xyCsourceRepository.delXyHCD(ccode).then(Mono.just(R.ok()));
    }

    /**
     * @description: 单据、列表操作前判断单据状态
     * @author: miao
     * @date: 2022/11/16 9:16
     * @param: [map]
     * @return: Mono<R>
     **/
    @PostMapping("verifyDataState")
    public Mono<R> verifyDataState(@RequestBody Map map){
        // cg/xh
        String dataType=map.get("dataType").toString();
        // 操作类型【rowEdit（列表点击行跳转）,edit：修改,del：删除,audit：审核】
        String operation=map.get("operation").toString();
        // 单号>>>审核状态
        List<String> list= (List<String>) map.get("list");
        Mono<List<StockWarehousings>> cg = warehousingsRepository.findAll().collectList();
        Mono<List<StockSaleousings>> xh = saleousingsRepository.findAll().collectList();

        return Mono.zip(cg,xh).flatMap(temp->{
            List<StockWarehousings> cglist = temp.getT1();
            List<StockSaleousings> xhlist = temp.getT2();
            String txt="1";
            for (int i = 0; i < list.size(); i++) {
                String ccode=list.get(i).split(">>>")[0];
                String bcheck=list.get(i).split(">>>")[1];
                long count=1;
                if(dataType.equals("cg")){
                    count = cglist.stream().filter(t -> operation.equals("rowEdit")?t.getCcode().equals(ccode):t.getCcode().equals(ccode)&&t.getBcheck().equals(bcheck)).count();
                    if (count==0){txt="";break;}
                }else if(dataType.equals("xh")){
                    count = xhlist.stream().filter(t -> operation.equals("rowEdit")?t.getCcode().equals(ccode):t.getCcode().equals(ccode)&&t.getBcheck().equals(bcheck)).count();
                    if (count==0){txt="";break;}
                }
            }
            return Mono.just(txt);
        }).map(R::ok);
    }
}

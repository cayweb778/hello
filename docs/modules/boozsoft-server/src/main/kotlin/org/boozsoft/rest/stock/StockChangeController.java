package org.boozsoft.rest.stock;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import lombok.extern.slf4j.Slf4j;
import org.boozsoft.domain.entity.ReportEncodingRules;
import org.boozsoft.domain.entity.stock.*;
import org.boozsoft.domain.vo.stock.*;
import org.boozsoft.repo.ReportEncodingRulesRepository;
import org.boozsoft.repo.group.GroupStockPeriodRepository;
import org.boozsoft.repo.stock.*;
import org.springbooz.core.launch.constant.AppConstant;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/stock/change")
public class StockChangeController {

    @Autowired
    private StockChangeRepository warehousingRepository;
    @Autowired
    private StockChangesRepository warehousingsRepository;
    @Autowired
    private StockXyCsourceRepository xyCsourceRepository;
    @Autowired
    StockCurrentstockRepository stockCurrentstockRepository;
    @Autowired
    private ReportEncodingRulesRepository encodingRulesRepository;
    @Autowired
    GroupStockPeriodRepository stockPeriodRepository;
    @Autowired
    private StockCurrentstockRepository currentstockRepository;
    @Autowired
    private StockWarehousingRepository stockWarehousingRepository;
    @Autowired
    private StockWarehousingsRepository stockWarehousingsRepository;
    @Autowired
    private StockSaleousingRepository stockSaleousingRepository;
    @Autowired
    private StockSaleousingsRepository stockSaleousingsRepository;
    @Autowired
    private StockChangesCsourceRepository stockTransferCsourceRepository;
    @Autowired
    private StockRepository stockRepository;
    @Autowired
    private StockBeginBalanceRepository stockBeginBalanceRepository;

    @PostMapping
    @Transactional
    public Mono<R> save(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        Map<String, Object> obj = assemblyCorrespondingRulesData(map);
        StockChange master = (StockChange) obj.get("master");
        List<StockChanges> sub = (List<StockChanges>) obj.get("sub");
        boolean b = master.getId() == null;
        return warehousingRepository.save(master).flatMap(db -> {
            Mono<StockChange> s = warehousingsRepository.saveAll(sub).collectList().thenReturn(db); //添加
            Mono<StockChange> d = warehousingsRepository.findAllByBillStyleAndCcodeOrderByDdateAscCcodeAscLineIdAsc(db.getBillStyle(), db.getCcode()).collectList().flatMap(list -> warehousingsRepository.deleteAll(list).thenReturn("").flatMap(i -> s)); //修改
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
        String bdocumStyle = map.containsKey("bdocumStyle") ? map.get("bdocumStyle").toString() : "0";
        String flgs = map.containsKey("flgs") ? "1" : "0";
        return warehousingRepository.findAllByBillStyleAndIyearOrderByDdateAscCcodeAsc(type, iyear)
                .collectList().cache().flatMap(list -> {
            if (list.size() == 0) {
                return Mono.just(R.ok());
            } else {
                StockChange master = null;
                switch (action) {
                    case "curr":
                        long count = list.stream().filter(a -> a.getCcode().equals(currPdId)).count();
                        if(count>0){
                            master = list.get((list.stream().map(e -> e.getCcode()).collect(Collectors.toList())).indexOf(currPdId));
                            break;
                        }
                    case "tail":
                        if(StrUtil.isNotBlank(ccode)){
                            list=list.stream().filter(a->a.getCcode().equals(ccode)).collect(Collectors.toList());
                            master = list.get(0);
                        }else if(list.size()>0){
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
                        master = list.size()>0?list.get(0):null;
                        break;
                }
                StockChange finalMaster = master;
               return finalMaster==null?Mono.just(R.ok(finalMaster)):warehousingsRepository.findAllByBillStyleAndCcodeAndFlgsOrderByDdateAscCcodeAscLineIdAsc(master.getBillStyle(), master.getCcode(),flgs).collectList().map(enlist -> {
                    if (enlist.size() > 0)
                        enlist.forEach(temp->{
                            String str="";
                            if(StrUtil.isNotBlank(temp.getCangkuDuli())&&temp.getCangkuDuli().equals("0")){
                                if(StrUtil.isNotBlank(temp.getCwhcode1())){
                                    str=temp.getCwhcode1();
                                }
                                if(StrUtil.isNotBlank(temp.getCwhcode2())){
                                    str+="\\"+temp.getCwhcode2();
                                }
                                if(StrUtil.isNotBlank(temp.getCwhcode3())){
                                    str+="\\"+temp.getCwhcode3();
                                }
                                if(StrUtil.isNotBlank(temp.getCwhcode4())){
                                    str+="\\"+temp.getCwhcode4();
                                }
                                if(StrUtil.isNotBlank(temp.getCwhcode5())){
                                    str+="\\"+temp.getCwhcode5();
                                }
                                if(StrUtil.isNotBlank(temp.getCwhcode6())){
                                    str+="\\"+temp.getCwhcode6();
                                }
                            }else{
                                str=temp.getCwhcode1();
                            }
                            temp.setCwhcodeNameJoin(str);
                            temp.setOldBaseQuantity(temp.getBaseQuantity());
                        });
                        finalMaster.setEntryList(JSON.toJSONString(enlist));
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
        String type = map.get("type").toString();
        List<String> keys = map.containsKey("keys") ? (List<String>) map.get("keys") : new ArrayList<>();
        AtomicReference<String> rkCcode = new AtomicReference<>("");
        AtomicReference<String> ckCcode = new AtomicReference<>("");
        AtomicReference<String> dbCcode = new AtomicReference<>("");
        return warehousingRepository.findById(id)
                .flatMap(dbEntry->{
                    //获取单据最新的编码-其他入库
                    String date = dbEntry.getDdate();//map.get("date").toString();
                    String types = "QTRKD";//map.get("type").toString();
                    String key = "3-13";//map.get("key").toString();
                    return stockWarehousingRepository.findAllTypeAndIyear(types, date.substring(0,4))
                            .collectList()
                            .flatMap(list->{
                                return encodingRulesRepository.findByFileType(key)
                                        .flatMap(tips->{
                                            ReportEncodingRules obj = tips;
                                            String customize = "";
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
                                                    rkCcode.set(pre.toString() + String.format("%0" + l + "d", t));
                                                    return Mono.just(dbEntry);//Mono.just(pre.toString()+String.format("%0"+l+"d",t));
                                                }
                                                rkCcode.set(pre.toString() + "0001");
                                                return Mono.just(dbEntry);//Mono.just(pre.toString()+"0001");
                                            }else{
                                                rkCcode.set(pre.toString() + "0001");
                                                return Mono.just(dbEntry);//Mono.just(pre.toString()+"0001");
                                            }
                                        });
                            });
                })
                .flatMap(dbEntry->{
                    //获取单据最新的编码-其他出库
                    String date = dbEntry.getDdate();//map.get("date").toString();
                    String types = "QTCKD";//map.get("type").toString();
                    String key = "3-21";//map.get("key").toString();
                    return stockSaleousingRepository.findAllByBillStyleAndIyearOrderByDdateAscCcodeAsc(types, date.substring(0,4))
                            .collectList()
                            .flatMap(list->{
                                return encodingRulesRepository.findByFileType(key)
                                        .flatMap(tips->{
                                            ReportEncodingRules obj = tips;
                                            String customize = "";
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
                                                List<StockSaleousing> collect = list.stream().filter(a -> a.getCcode().contains(pre.toString())).collect(Collectors.toList());
                                                if(collect.size()>0){
                                                    collect.sort(Comparator.comparing(StockSaleousing::getCcode).reversed());
                                                    int t = Integer.parseInt(collect.get(0).getCcode().replace(pre.toString(),""))+1;
                                                    ckCcode.set(pre.toString() + String.format("%0" + l + "d", t));
                                                    return Mono.just(dbEntry);//Mono.just(pre.toString()+String.format("%0"+l+"d",t));
                                                }
                                                ckCcode.set(pre.toString() + "0001");
                                                return Mono.just(dbEntry);//Mono.just(pre.toString()+"0001");
                                            }else{
                                                ckCcode.set(pre.toString() + "0001");
                                                return Mono.just(dbEntry);//Mono.just(pre.toString()+"0001");
                                            }
                                        });
                            });
                })
                .flatMap(dbEntry ->
                    warehousingsRepository.findAllByBillStyleAndCcodeOrderByDdateAscCcodeAscLineIdAsc(dbEntry.getBillStyle(), dbEntry.getCcode())
                        .collectList()
                        .flatMap(list -> {
                            StockWarehousing sw =  new StockWarehousing();
                            StockSaleousing sa =  new StockSaleousing();

                            BeanUtils.copyProperties(dbEntry,sw);
                            sw.setId(null);
                            sw.setBstyle("形态转换入库");//入库类别（收发方式中的收方向编码
                            sw.setCmakerTime(LocalDateTime.now().toString());
                            sw.setUnitType("etc");
                            sw.setUnitValue("形态转换入库生成");
                            sw.setBillStyle("QTRKD");
                            sw.setSourcetype("XTZHD");//来源单据类型id
                            sw.setCcode(rkCcode.get());
                            sw.setBdocumStyle("0");
                            sw.setBcheck("1");
                            sw.setBcheckTime(LocalDate.now().toString());
                            sw.setBcheckUser(userId);
                            sw.setCwhcode(list.get(0).getCwhcode());
                            if (type.equals("true")) {
                                HashSet<String> dbSet = new HashSet<>(list.stream().filter(it -> StrUtil.isNotBlank(it.getBcheck()) && it.getBcheck().equals("1")).map(it -> it.getLineCode()).collect(Collectors.toList()));
                                dbSet.addAll(keys);
                                if (keys.size() == 0 || dbSet.size() == list.size()) {
                                    dbEntry.setBcheck("1").setBcheckTime(DateUtil.now()).setBcheckUser(userId);
                                    for (StockChanges StockChanges : list) {
                                        StockChanges.setBcheck("1").setBcheckTime(DateUtil.now()).setBcheckUser(userId);
                                    }
                                    return stockWarehousingRepository.save(sw)
                                            .flatMap(obj->{
                                                StockChangesSource sc = new StockChangesSource();
                                                sc.setIyear(dbEntry.getIyear());
                                                sc.setBillStyle("XTZHRKD");
                                                sc.setXyBillStyle("QTRKD");
                                                sc.setCcodeDate(dbEntry.getDdate());
                                                sc.setCcode(dbEntry.getCcode());
                                                sc.setSyccode(sw.getCcode());
                                                sc.setSyccodeDate(dbEntry.getDdate());
                                                return  stockTransferCsourceRepository.save(sc)
                                                        .map(item->{
                                                            return obj;
                                                        });
                                            })
                                            .flatMap(o -> {
                                                BeanUtils.copyProperties(dbEntry,sa);
                                                sa.setId(null);
                                                sa.setBstyle("形态转换出库");//入库类别（收发方式中的收方向编码
                                                sa.setCmakerTime(LocalDateTime.now().toString());
                                                sa.setUnitType("etc");
                                                sa.setUnitValue("形态转换出库生成");
                                                sa.setBillStyle("QTCKD");
                                                sa.setSourcetype("XTZHD");//来源单据类型id
                                                sa.setCcode(ckCcode.get());
                                                sa.setBdocumStyle("0");
                                                sa.setBcheck("1");
                                                sa.setBcheckTime(LocalDate.now().toString());
                                                sa.setBcheckUser(userId);
                                                sa.setBstyle("");//入库类别（收发方式中的收方向编码）
                                                sa.setCwhcode(list.get(0).getCwhcode());
                                                return stockSaleousingRepository.save(sa)
                                                        .flatMap(obj->{
                                                            StockChangesSource sc = new StockChangesSource();
                                                            sc.setIyear(dbEntry.getIyear());
                                                            sc.setBillStyle("XTZHCKD");
                                                            sc.setXyBillStyle("QTCKD");
                                                            sc.setCcodeDate(dbEntry.getDdate());
                                                            sc.setCcode(dbEntry.getCcode());
                                                            sc.setSyccode(sa.getCcode());
                                                            sc.setSyccodeDate(dbEntry.getDdate());
                                                            return  stockTransferCsourceRepository.save(sc)
                                                                    .map(item->{
                                                                        return obj;
                                                                    });
                                                        })
                                                        .map(item->{
                                                            List<StockChanges> collect = list.stream().filter(v -> v.getFlgs().equals("0")).collect(Collectors.toList());
                                                            List<StockSaleousings> salist = new ArrayList<>();
                                                            collect.stream().forEach(v->{
                                                                StockSaleousings sas =  new StockSaleousings();
                                                                BeanUtils.copyProperties(v,sas);
                                                                sas.setCmakerTime(v.getCfree1());
                                                                sas.setCsource("XTZHD");//单据来源
                                                                sas.setBcheck(v.getBcheck());
                                                                sas.setBcheckUser(v.getBcheckUser());
                                                                sas.setSourcetype("XTZHD");//来源单据类型id
                                                                sas.setSourcecode(dbCcode.get());//来源单据编码
                                                                sas.setSourcedetailId(v.getLineCode());
                                                                sas.setSourcedate(v.getDdate());//来源单据日期
                                                                sas.setCangkuDuli("1");
                                                                sas.setLineId("1");
                                                                sas.setIsGive("0");
                                                                sas.setBillStyle("QTCKD");
                                                                sas.setBstyle("形态转换出库");//入库类别（收发方式中的收方向编码）
                                                                sas.setDdate(v.getDdate());
                                                                sas.setCcode(ckCcode.get());
                                                                sas.setCmakerTime(LocalDateTime.now().toString());
                                                                sas.setXsUnitId(v.getCunitid());
                                                                sas.setQuantity(sas.getBaseQuantity());
                                                                sas.setCwhcode1(v.getCwhcode());
                                                                sas.setBcheck("1");
                                                                sas.setBcheckTime(LocalDate.now().toString());
                                                                sas.setBcheckUser(userId);
                                                                sas.setId(null);
                                                                salist.add(sas);
                                                            });
                                                            return salist;
                                                        });
                                            })
                                            .flatMap(e -> {
                                                List<StockChanges> sl = list.stream().filter(v -> v.getFlgs().equals("1")).collect(Collectors.toList());
                                                List<StockWarehousings> swlist = new ArrayList<>();
                                                sl.stream().forEach(v->{
                                                    StockWarehousings stockWarehousings =  new StockWarehousings();
                                                    BeanUtils.copyProperties(v,stockWarehousings);
                                                    stockWarehousings.setId(null);
                                                    stockWarehousings.setCmakerTime(v.getCfree1());
                                                    stockWarehousings.setCsource("XTZHD");//单据来源
                                                    stockWarehousings.setBcheck(v.getBcheck());
                                                    stockWarehousings.setBcheckUser(v.getBcheckUser());
                                                    stockWarehousings.setSourcetype("XTZHD");//来源单据类型id
                                                    stockWarehousings.setSourcecode(dbCcode.get());//来源单据编码
                                                    stockWarehousings.setSourcedetailId(v.getLineCode());
                                                    stockWarehousings.setSourcedate(v.getDdate());//来源单据日期
                                                    stockWarehousings.setCangkuDuli("1");
                                                    stockWarehousings.setLineId("1");
                                                    stockWarehousings.setIsGive("0");
                                                    stockWarehousings.setBillStyle("QTRKD");
                                                    stockWarehousings.setBstyle("形态转换入库");//入库类别（收发方式中的收方向编码）
                                                    stockWarehousings.setDdate(v.getDdate());
                                                    stockWarehousings.setCcode(rkCcode.get());
                                                    stockWarehousings.setCmakerTime(LocalDateTime.now().toString());
                                                    stockWarehousings.setCgUnitId(v.getCunitid());
                                                    stockWarehousings.setCnumber(v.getCnumber());
                                                    stockWarehousings.setCwhcode1(v.getCwhcode());
                                                    stockWarehousings.setBcheck("1");
                                                    stockWarehousings.setBcheckTime(LocalDate.now().toString());
                                                    stockWarehousings.setBcheckUser(userId);
                                                    stockWarehousings.setId(null);
                                                    stockWarehousings.setIcost(new BigDecimal(v.getIcost()).add(new BigDecimal(v.getFyprice())).toString());
                                                    stockWarehousings.setPrice(new BigDecimal(stockWarehousings.getIcost()).divide(new BigDecimal(v.getCnumber()),10,BigDecimal.ROUND_HALF_UP).toString());
                                                    swlist.add(stockWarehousings);
                                                });
                                                return stockSaleousingsRepository.saveAll(e).collectList().thenReturn(swlist);
                                            })
                                            .flatMap(e -> stockWarehousingsRepository.saveAll(e).collectList().thenReturn(dbEntry))
                                            .flatMap(o -> {
                                                return warehousingRepository.save(dbEntry).flatMap(e -> warehousingsRepository.saveAll(list).collectList().thenReturn(e));
                                            });
                                }else{
                                    return null;
                                }
                            } else {
                                dbEntry.setBcheck("0").setBcheckTime(null).setBcheckUser(null);
                                for (StockChanges StockChanges : list) {
                                    if (keys.size() == 0) {
                                        StockChanges.setBcheck("0").setBcheckTime(null).setBcheckUser(null);
                                    } else if (keys.contains(StockChanges.getLineCode())) {
                                        StockChanges.setBcheck("0").setBcheckTime(null).setBcheckUser(null);
                                    }
                                }
                                //删除出入库单
                                AtomicReference<String> rkccode = new AtomicReference<>("");
                                AtomicReference<String> ckccode = new AtomicReference<>("");
                                return warehousingRepository.save(dbEntry)
                                        .flatMap(e -> warehousingsRepository.saveAll(list).collectList())
                                        .flatMap(o->{
                                            //获取下游ccode
                                            return stockTransferCsourceRepository.findAllByCcodeAndIyear(dbEntry.getCcode(),"2022")
                                                    .collectList()
                                                    .map(stcList->{
                                                        Optional<StockChangesSource> first = stcList.stream().filter(v -> "QTRKD".equals(v.getXyBillStyle())).findFirst();
                                                        if(first.isPresent())  rkccode.set(first.get().getSyccode());
                                                        Optional<StockChangesSource> first1 = stcList.stream().filter(v -> "QTCKD".equals(v.getXyBillStyle())).findFirst();
                                                        if(first1.isPresent())  ckccode.set(first1.get().getSyccode());
                                                        return dbEntry.getCcode();
                                                    });
                                        })
                                        .flatMap(c->{
                                            return Objects.nonNull(c) ? stockTransferCsourceRepository.deleteByCcode(c).then(Mono.just(c)):Mono.just(c);
                                        })
                                        .flatMap(c->{
                                            return Objects.nonNull(ckccode.get()) ? stockSaleousingRepository.deleteByCcodeAndBillType(ckccode.get(),"QTCKD").thenReturn(c):Mono.just(c);
                                        })
                                        .flatMap(c->{
                                            return Objects.nonNull(ckccode.get()) ?stockSaleousingsRepository.deleteByCcodeAndBillType(ckccode.get(),"QTCKD").thenReturn(c):Mono.just(c);
                                        })
                                        .flatMap(c->{
                                            return Objects.nonNull(rkccode.get()) ?stockWarehousingRepository.deleteByCcodeAndBillType(rkccode.get(),"QTRKD").thenReturn(c):Mono.just(c);
                                        })
                                        .flatMap(c->{
                                            return  Objects.nonNull(rkccode.get()) ?stockWarehousingsRepository.deleteByCcodeAndBillType(rkccode.get(),"QTRKD").thenReturn(c):Mono.just(c);
                                        })
                                        .thenReturn(dbEntry);
                            }
                        })
        ).map(R::ok);
    }

    @GetMapping("/auditCheck/{ccode}/{rkBcheck}/{ckBcheck}/{flg}/{type}")
    public Mono<R> auditCheck(@PathVariable String ccode,@PathVariable String rkBcheck,@PathVariable String ckBcheck,@PathVariable String flg,@PathVariable String type) {
        // 参数设置：入库保存状态就是现存量 标志 1是查询所有 0 查询已审核
        rkBcheck=ObjectUtil.isEmpty(rkBcheck)?"0":rkBcheck;
        ckBcheck=ObjectUtil.isEmpty(ckBcheck)?"0":ckBcheck;
        //审核弃审前校验现存量
        String finalRkBcheck = rkBcheck;
        String finalCkBcheck = ckBcheck;
        //type 0转换前 1转换后  审核验证转换前后现存量 弃审验证转换后现存量
        return warehousingRepository.findByCcode(ccode)
                .flatMap(st->{
                    //转换前
                    return warehousingsRepository.findAllByCcodeAndFlgs(ccode,type)
                            .collectList()
                            .map(stsList->{
                                st.setStsList(stsList);
                                return st;
                            });
                })
                .flatMap(st->{
                    //入库单 需要验证现存量
                    String year = st.getIyear();
                    List<StockAccSheetVo> skl = new ArrayList<>();
                    List<StockVo> sv = new ArrayList<>();
                    List<StockChanges> stsList1 = st.getStsList();
                    List<String> cinvodeList = stsList1.stream().map(v -> v.getCinvode()).collect(Collectors.toList());
                    return stockRepository.findAllByXcl2(cinvodeList)
                            .collectList()
                            .flatMap(slist->{
                                //期初
                                return stockBeginBalanceRepository.findAllByIyearAndStockList(year,cinvodeList)
                                        .collectList()
                                        .map(wl->{
                                            skl.addAll(wl);
                                            return slist;
                                        });
                            })
                            .flatMap(slist->{
                                //入库(其他 采购 领用)+到货单(采购到货单数量-累计入库数量)
                                return stockWarehousingsRepository.findAllByIyearAndList(year,cinvodeList)
                                        .filter(v-> {
                                            if(finalRkBcheck.equals("0")){
                                                return  "1".equals(v.getBcheck());
                                            }else{
                                                return  true;
                                            }
                                        })
                                        .collectList()
                                        .map(wl->{
                                            if("XCL".equals(flg)){
                                                List<StockAccSheetVo> li = wl.stream().filter(v -> !"CGDHD".equals(v.getBillStyle()))
                                                        .collect(Collectors.toList());
                                                skl.addAll(li);
                                            }else{
                                                wl.stream().filter(v -> "CGDHD".equals(v.getBillStyle()))
                                                        .forEach(v->{
                                                            //采购到货单数量-累计入库数量
                                                            BigDecimal subtract = new BigDecimal(v.getBq()).subtract(new BigDecimal(v.getIsum()));
                                                            v.setBq(subtract.toString());
                                                            v.setTypes("3");
                                                        });
                                                skl.addAll(wl);
                                            }
                                            return slist;
                                        });
                            })
                            .flatMap(slist->{
                                //出库(销售 其他 领用)+销货单(销货单数量-累计销售出库数量)
                                return stockSaleousingsRepository.findAllByIyearAndList(year,cinvodeList)
                                        .filter(v-> {
                                            if(finalCkBcheck.equals("0")){
                                                return  "1".equals(v.getBcheck());
                                            }else{
                                                return  true;
                                            }
                                        })
                                        .collectList()
                                        .map(sl->{
                                            if("XCL".equals(flg)){
                                                List<StockAccSheetVo> li = sl.stream().filter(v -> !"XHD".equals(v.getBillStyle()))
                                                        .collect(Collectors.toList());
                                                skl.addAll(li);
                                            }else{
                                                sl.stream().filter(v -> "XHD".equals(v.getBillStyle()))
                                                        .forEach(v->{
                                                            //采购到货单数量-累计入库数量
                                                            BigDecimal subtract = new BigDecimal(v.getBq()).subtract(new BigDecimal(v.getIsum()));
                                                            v.setBq(subtract.toString());
                                                            v.setTypes("4");
                                                        });
                                                skl.addAll(sl);
                                            }
                                            return slist;
                                        });
                            })
                            .map(list->{
                                //根据现存量去计算现存量
                                // dataList  对月份分组
                                Map<String, List<StockAccSheetVo>> map1 = skl.stream()
                                        .collect(Collectors.groupingBy(v->{
                                            String str = v.getCinvode();
                                            if(Objects.nonNull(v.getBatchid())){
                                                str+="_"+v.getBatchid();
                                            }
                                            return str;
                                        }));
                                //  排序
                                Map<String, List<StockAccSheetVo>> map2 = map1.entrySet().stream()
                                        .sorted(Map.Entry.comparingByKey())
                                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                                (oleValue, newValue) -> oleValue, LinkedHashMap::new));

                                //计算现存量 合并存货数据
                                map2.forEach((k, value) -> {
                                    List<StockChanges> stsList = st.getStsList();
                                    stsList.stream().forEach(obj->{
                                        //筛选仓库 计算现存量
                                        //入库+期初
                                        List<StockAccSheetVo> ckList = value.stream().filter(o ->    "0".equals(o.getTypes()) ||  "1".equals(o.getTypes()))
                                                .filter(o-> o.getCwhcode().equals(obj.getCwhcode()))
                                                .collect(Collectors.toList());
                                        //出库
                                        List<StockAccSheetVo> rkList = value.stream().filter(o ->   "2".equals(o.getTypes()))
                                                .filter(o-> o.getCwhcode().equals(obj.getCwhcode()))
                                                .collect(Collectors.toList());

                                        double sumBq = ckList.stream().mapToDouble(v -> {
                                            if (Objects.isNull(v.getBq())) return 0.00d;
                                            return Double.parseDouble(v.getBq().toString());
                                        }).sum();

                                        double sumBqrk = rkList.stream().mapToDouble(v -> {
                                            if (Objects.isNull(v.getBq())) return 0.00d;
                                            return Double.parseDouble(v.getBq().toString());
                                        }).sum();

                                        BigDecimal subtract = BigDecimal.ZERO;
                                        if("XCL".equals(flg)){
                                            //现存量  期初+入库-出库
                                            subtract =  new BigDecimal(sumBq).subtract(new BigDecimal(sumBqrk));
                                        }else{
                                            //可用量 = 现存量：（期初+入库-出库） +  在途量： （到货单(采购到货单数量-累计入库数量)）- （销货单(销货单数量-累计销售出库数量)）
                                            //到货单
                                            List<StockAccSheetVo> dhList = value.stream().filter(o ->   "3".equals(o.getTypes())).collect(Collectors.toList());
                                            //销货单
                                            List<StockAccSheetVo> xhList = value.stream().filter(o ->   "4".equals(o.getTypes())).collect(Collectors.toList());
                                            double sumBqdh = dhList.stream().mapToDouble(v -> {
                                                if (Objects.isNull(v.getBq())) return 0.00d;
                                                return Double.parseDouble(v.getBq().toString());
                                            }).sum();

                                            double sumBqxh = xhList.stream().mapToDouble(v -> {
                                                if (Objects.isNull(v.getBq())) return 0.00d;
                                                return Double.parseDouble(v.getBq().toString());
                                            }).sum();
                                            subtract =  new BigDecimal(sumBq).subtract(new BigDecimal(sumBqrk)).add(new BigDecimal(sumBqdh)).subtract(new BigDecimal(sumBqxh));
                                        }

                                        //现存量存在 并且不等于0的
                                        if(subtract.compareTo(BigDecimal.ZERO) >= 0){
                                            StockVo stockVo = new StockVo();
                                            stockVo.setStockNum(value.get(0).getCinvode());
                                            stockVo.setXcl(subtract);
                                            //批号
                                            stockVo.setBatchId(value.get(0).getBatchid());
                                            stockVo.setDpdate(value.get(0).getDpdate());
                                            stockVo.setDvdate(value.get(0).getDvdate());
                                            sv.add(stockVo);
                                        }
                                    });
                                });
                                return sv;
                            })
                            .map(list->{
                                AtomicReference<Boolean> b = new AtomicReference<>(false);
                                //审核弃审 判断现存量
                                List<StockChanges> rkList = st.getStsList();
                                //获取显存量的存货数据 然后比对对应的存货数据
                                rkList.stream().forEach(v->{
                                    Optional<StockVo> first = list.stream().filter(o -> o.getStockNum().equals(v.getCinvode()) &&  (Objects.nonNull(v.getBatchId())? o.getBatchId().equals(v.getBatchId()) :true)).findFirst();
                                    //现存量大于出库数量
                                    if(first.isPresent() && first.get().getXcl().compareTo(new BigDecimal(v.getBaseQuantity())) >= 0){
                                        b.set(true);
                                    }
                                });
                                return b.get();
                            });
                })
                .map(a -> R.ok().setResult(a));
    }

    @PostMapping("auditCheckBcheck")
    @Transactional
    public Mono<R> auditCheckBcheck(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        String ccode = map.get("ccode").toString();
        //获取下游ccode
        return stockTransferCsourceRepository.findByXyOutDataSourrce(ccode)
                .collectList()
                .map(stcList->{
                    //获取下游ccode 判断是否审核
                    AtomicReference<Boolean> flsg = new AtomicReference<>(false);
                    stcList.forEach(v->{
                        if("1".equals(v)){
                            flsg.set(true);
                        }
                    });
                    return flsg.get();
                }).map(o->R.ok().setResult(o));

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

    @PostMapping("reviewSetCGRKG")
    public Mono<R> reviewSetCGRKG(@RequestBody StockChange pojo) {
        return warehousingRepository.save(pojo).map(a->R.ok().setResult(a));
    }
    @PostMapping("reviewSetCGRKGMx")
    public Mono<R> reviewSetCGRKGMx(@RequestBody List<StockChanges> pojo) {
        return warehousingsRepository.saveAll(pojo).collectList().map(a->R.ok().setResult(a));
    }
    @PostMapping("xyCsourceSave")
    public Mono<R> xyCsourceSave(@RequestBody StockXyCsource pojo) {
        return xyCsourceRepository.save(pojo).map(a->R.ok().setResult(a));
    }

    @PostMapping("findByStockCurrLock")
    public Mono<R> findByStockCurrLock(@RequestBody Map map) {
        String iyear=map.get("iyear").toString();
        List<StockChanges> list= JSON.parseArray(map.get("list").toString(), StockChanges.class);
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
    public Mono<R> saveStockCurrentstockZTRK_Increase(@RequestBody Map map2){
        List<StockChanges> list= JSONArray.parseArray(map2.get("list").toString(),StockChanges.class);
        // 1 新建 2编辑
        String status= map2.get("status").toString();
        // 采购入库单审核后修改现存量（默认值1是，0或空否，保存即修改）
        String kcCgrkCheck= map2.get("kcCgrkCheck").toString();

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
                    }
                    newlist.add(c);
                }
                else{
                    BigDecimal baseQuantity=new BigDecimal("0");
                    // 采购入库单审核后修改现存量（默认值1是，0或空否，保存即修改）
                    if(StrUtil.isBlank(kcCgrkCheck)&&kcCgrkCheck.equals("0")){
                        // 等于
//                        if(collect.get(0).getZtrkQuantity().compareTo(new BigDecimal(temp.getBaseQuantity()))==0){
//                            baseQuantity=collect.get(0).getZtrkQuantity();
//                        }else if(new BigDecimal(temp.getBaseQuantity()).compareTo(collect.get(0).getZtrkQuantity())==1){ // 大于
//                            BigDecimal cha=new BigDecimal(temp.getBaseQuantity()).subtract(collect.get(0).getZtrkQuantity());
//                            baseQuantity=collect.get(0).getZtrkQuantity().add(cha);
//                        }else if(new BigDecimal(temp.getBaseQuantity()).compareTo(collect.get(0).getZtrkQuantity())==-1){ // 小于
//                            BigDecimal cha=collect.get(0).getZtrkQuantity().subtract(new BigDecimal(temp.getBaseQuantity()));
//                            baseQuantity=collect.get(0).getZtrkQuantity().subtract(cha);
//                        }
                        // kc_kyl_cg  可用量是否包含采购在途量和销售在途量（默认值1是，0或空否）
//                        collect.get(0).setZtrkQuantity(temp.getKcKylCg()==null||temp.getKcKylCg().equals("1")?new BigDecimal("0"):baseQuantity);
                    }else{
                        // 等于
                        if(collect.get(0).getBaseQuantity().compareTo(new BigDecimal(temp.getBaseQuantity()))==0){
                            baseQuantity=collect.get(0).getBaseQuantity();
                        }else if(new BigDecimal(temp.getBaseQuantity()).compareTo(collect.get(0).getBaseQuantity())==1){ // 大于
                            BigDecimal cha=new BigDecimal(temp.getBaseQuantity()).subtract(collect.get(0).getBaseQuantity());
                            baseQuantity=collect.get(0).getBaseQuantity().add(cha);
                        }else if(new BigDecimal(temp.getBaseQuantity()).compareTo(collect.get(0).getBaseQuantity())==-1){ // 小于
                            BigDecimal cha=collect.get(0).getBaseQuantity().subtract(new BigDecimal(temp.getBaseQuantity()));
                            baseQuantity=collect.get(0).getBaseQuantity().subtract(cha);
                        }
//                        collect.get(0).setBaseQuantity(baseQuantity);
//                        collect.get(0).setZtrkQuantity(new BigDecimal("0"));
//                        collect.get(0).setZtrkQuantity(baseQuantity);
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
    public Mono<R> delXyCsourceByxyCcodeAndxyBillTypeAndBillTypeAndCcode(String xyccode,String xybillStyle,String billStyle,String ccode) {
        return warehousingRepository.delXyCsourceByxyCcodeAndxyBillTypeAndBillTypeAndCcode(xyccode,xybillStyle,billStyle,ccode).then(Mono.just(R.ok()));
    }


    /**
     * 是否存在下游单据
     * @param year
     * @param code
     * @param xyCode
     * @return
     */
    @PostMapping("verifyXyCsourceByXyCode")
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
    public Mono<R> findAllByBillStyleAndIyear(String year,String billStyle){
        return xyCsourceRepository.findAllByBillStyleAndIyear(year,billStyle).collectList().map(a->R.ok().setResult(a));
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
        return xyCsourceRepository.findByXyBillStyleAndXyccodeAndIyear(xyStyle,xyCode,iyear).map(a->R.ok().setResult(a)).defaultIfEmpty(R.ok().setResult(""));
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
        return warehousingRepository.findByCcode(ccode).map(a->R.ok().setResult(a)).defaultIfEmpty(R.ok().setResult(""));
    }

    /**
     * 只增加入库单主表
     * @param obj
     * @return
     */
    @PostMapping("saveStockChange")
    public Mono<R> saveStockChange(@RequestBody StockChange obj){
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
    public Mono<R> saveStockCurrentstockZTRK_Edit_XCL(@RequestBody Map map2){
        List<StockChanges> list= JSONArray.parseArray(map2.get("list").toString(),StockChanges.class);

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
//                        collect.get(0).setZtrkQuantity(collect.get(0).getZtrkQuantity().add(new BigDecimal(temp.getBaseQuantity())));
                        newlist.add(collect.get(0));
                    });
                    return Mono.just(newlist);
                })
                .flatMapMany(newlist->stockCurrentstockRepository.saveAll(newlist)).collectList()
                .map(a->R.ok().setResult(a));
    }

    private Map<String, Object> assemblyCorrespondingRulesData(Map map) {
        String type = map.get("billStyle").toString();
        String bstyle = "01"; //map.get("bstyle").toString();
        HashMap<String, Object> resut = new HashMap<>();
        switch (type) {
            case ("XTZHCKD"):
                StockChange warehousing = new StockChange();
                warehousing.setBillStyle("XTZHCKD");
                String sgspperson = map.containsKey("sgspperson") ? map.get("sgspperson").toString() : null; // 质检人
                String bcheckUser = map.containsKey("bcheckUser") ? map.get("bcheckUser").toString() : null; // 审核人
                warehousing.setId(map.containsKey("id") ? Objects.isNull(map.get("id"))?null:map.get("id").toString() : null)
                        .setCcode(map.containsKey("ccode") ? map.get("ccode").toString() : null) //单号
                        .setDdate(map.containsKey("ddate") ? map.get("ddate").toString().substring(0, 10) : null) // 日期
                        .setIyear(map.containsKey("ddate") ? map.get("ddate").toString().substring(0, 4) : null).setCmaker(map.containsKey("cmaker") ? map.get("cmaker").toString() : null) // 制单人
                        //.setCwhcode(map.containsKey("cwhcode") ? map.get("cwhcode")==null?null:map.get("cwhcode").toString() : null)  // 仓库
                        .setCdepcode(map.containsKey("cdepcode") ? map.get("cdepcode")==null?null:map.get("cdepcode").toString() : null) // 部门
                        .setCpersoncode(map.containsKey("cpersoncode") ? map.get("cpersoncode")==null?null:map.get("cpersoncode").toString() : null) // 业务员
                        //.setCwhcodeUser(map.containsKey("cwhcodeUser") ? map.get("cwhcodeUser")==null?null:map.get("cwhcodeUser").toString() : null) // 库管员
                        .setCmemo(map.containsKey("cmemo") ? map.get("cmemo")==null?null: map.get("cmemo").toString(): null) // 备注
                        .setFymoney(map.containsKey("fymoney") ? map.get("fymoney")==null?null: map.get("fymoney").toString(): null)
                        .setCymoney(map.containsKey("cymoney") ? map.get("cymoney").toString() : null);
                if (bcheckUser != null) {
                    warehousing.setBcheckUser(bcheckUser);
                    warehousing.setBcheck("1");
                    warehousing.setBcheckTime(DateUtil.now());
                }
                String entryList = map.containsKey("entryList") ? map.get("entryList").toString() : ""; // 表体
                List<StockChanges> entrys = JSON.parseArray(entryList, StockChanges.class);
                BigDecimal squantitySum = new BigDecimal(0);
                BigDecimal squantity1Sum = new BigDecimal(0);
                BigDecimal squantity2Sum = new BigDecimal(0);
                BigDecimal icostSum = new BigDecimal(0);
                BigDecimal isumSum = new BigDecimal(0);
                BigDecimal taxAmountSum = new BigDecimal(0);
                for (StockChanges entry : entrys) {
                    entry.setId(null).setIyear(warehousing.getIyear())
                           .setCfree1(LocalDateTime.now().toString())
                           .setBillStyle(warehousing.getBillStyle())
                           .setCunitid(entry.getCunitid())
                           .setDdate(warehousing.getDdate())
                           .setCcode(warehousing.getCcode())
                           .setCmaker(warehousing.getCmaker())
                           .setCdepcode(warehousing.getCdepcode());
                    squantitySum = squantitySum.add(new BigDecimal(entry.getBaseQuantity() == null ? "0" : entry.getBaseQuantity()));
                    //taxAmountSum = taxAmountSum.add(new BigDecimal(entry.getItaxprice() == null ? "0" : entry.getItaxprice()));
                }
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


    @PostMapping("batchSelectorList")
    public Mono<R> batchSelectorList(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        String year = map.get("year").toString();
        List<String> codes = (List<String>) map.get("codes");
        return currentstockRepository.findAllByIyearAndInvCodeIn(year,codes).collectList().map(R::ok);
    }

    @PostMapping("before")
    public Mono<R> before(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        String type = map.get("type").toString();
        String code = map.get("code").toString();
        if (type.equals("XTZHCKD")){// 检查出库单存不存在
            return  warehousingsRepository.countAllByBillStyleAndCcode("XTZHCKD",code).map(R::ok);
        }else {
            return  warehousingsRepository.countAllByBillStyleAndCcode("XTZHCKD",code).map(R::ok);
        }
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
        return encodingRulesRepository.findByFileType(key).switchIfEmpty(Mono.just(new ReportEncodingRules())).zipWith(warehousingRepository.findFirstByBillStyleAndDdateLikeOrderByDdateDescCcodeDesc(type, "%" + date.substring(0, 7) + "%").switchIfEmpty(Mono.just(new StockChange()))).flatMap(tips -> {
            ReportEncodingRules obj = tips.getT1();
            StockChange it = tips.getT2();
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
            if (null == it.getCcode()) {
                return Mono.just(pre.toString() + "0001");
            } else {
                int t = Integer.parseInt(it.getCcode().replace(pre.toString(), "")) + 1;
                return Mono.just(pre.toString() + String.format("%0" + l + "d", t));
            }
        });
    }


    @PostMapping("findAllMainList")
    public Mono<R> findAllMainList(@RequestBody Map map){
        String strDate = map.get("strDate").toString();
        String endDate = map.get("endDate").toString();
        return warehousingRepository.findMainList(strDate, endDate).collectList()
                .flatMap(list->{
                    //筛选
                    List<StockChangeVo> collect = list.stream().filter(v -> {
                        if (map.containsKey("ccode") && ObjectUtil.isNotEmpty(map.get("ccode").toString())) {
                            return v.getCcode().contains(map.get("ccode").toString());
                        }

                        String dataType = map.get("dataType").toString();
                        if ("1".equals(dataType)) {
                            // 2全部 1审核 0弃审
                            if (!dataType.equals(v.getBcheck())) {
                                return false;
                            }
                        }

                        if ("0".equals(dataType)) {
                            if ("1".equals(v.getBcheck())) {
                                return false;
                            }
                        }

                        if (map.containsKey("dept") && ObjectUtil.isNotEmpty(map.get("dept").toString())) {
                            return v.getCdepcode().equals(map.get("dept").toString());
                        }
                        if (map.containsKey("user") && ObjectUtil.isNotEmpty(map.get("user").toString())) {
                            return v.getCpersoncode().equals(map.get("user").toString());
                        }
                        return true;
                    }).collect(Collectors.toList());
                    return Mono.just(collect);
                }).map(a->R.ok().setResult(a));
    }

    /**
     * 下游
     * @return
     */
    @PostMapping("findByXyOutDataSourrce")
    public Mono<R> findByXyOutDataSourrce(@RequestBody Map map){
        String code = map.get("code").toString();
        String type = map.get("type").toString();
        // 
        return stockWarehousingRepository.findByCcode(code)
                .map(a->R.ok().setResult(a));
    }


    @PostMapping("findAllByCondition")
    public Mono<R> findAllByCondition(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        // 单据编码
        String strDate = map.containsKey("strDate")?map.get("strDate").toString():"";
        String endDate = map.containsKey("endDate")?map.get("endDate").toString():"";
        String cdepcode = map.containsKey("cdepcode")?map.get("cdepcode").toString():"";
        String cmaker = map.containsKey("cmaker")?map.get("cmaker").toString():"";
        String cangku = map.containsKey("cangku")?map.get("cangku").toString():"";
        String ccode = map.containsKey("ccode")?map.get("ccode").toString():"";
        String type = map.containsKey("dataType")? map.get("dataType").toString():"";
        String iyear = map.containsKey("iyear")? map.get("iyear").toString():"";

        Mono<R> map1 = stockWarehousingRepository.findByPyList(strDate, endDate, iyear, type)
                .collectList()
                .map(list -> {
                    return list.stream().filter(v -> {
                        if (Objects.nonNull(ccode)) {
                            return v.getCcode().contains(ccode);
                        }
                        if (Objects.nonNull(cmaker)) {
                            return v.getCmaker().equals(cmaker);
                        }
                        if (Objects.nonNull(cangku)) {
                            return v.getCwhcode().equals(cangku);
                        }
                        if (Objects.nonNull(cdepcode)) {
                            return v.getCdepcode().equals(cdepcode);
                        }
                        return true;
                    }).collect(Collectors.toList());
                })
                .map(list -> {
                    int l = list.size();
                    for (int i = 0; i < (25-l); i++) {
                        list.add(new StockWarehousingVo());
                    }
                    return list;
                })
                .map(list -> R.ok().setResult(list));

        Mono<R> map2 = stockSaleousingRepository.findByPkList(strDate, endDate, iyear, type)
                .collectList()
                .map(list -> {
                    return list.stream().filter(v -> {
                        if (Objects.nonNull(ccode)) {
                            return v.getCcode().contains(ccode);
                        }
                        if (Objects.nonNull(cmaker)) {
                            return v.getCmaker().equals(cmaker);
                        }
                        if (Objects.nonNull(cangku)) {
                            return v.getCwhcode().equals(cangku);
                        }
                        if (Objects.nonNull(cdepcode)) {
                            return v.getCdepcode().equals(cdepcode);
                        }
                        return true;
                    }).collect(Collectors.toList());
                })
                .map(list -> {
                    int l = list.size();
                    for (int i = 0; i < (25-l); i++) {
                        list.add(new StockWarehousingVo());
                    }
                    return list;
                })
                .map(list -> R.ok().setResult(list));

        return type.equals("DBRKD")? map1:map2;
    }

    @GetMapping("/findByXyCcode/{ccode}/{year}/{type}")
    public Mono<R> findByXyCcode(@PathVariable String ccode,@PathVariable String year,@PathVariable String type){
        return stockTransferCsourceRepository.findAllByCcodeAndIyear(ccode,year)
                .filter(it->StrUtil.isBlank(type)?true:it.getXyBillStyle().equals(type))
                .collectList()
                .map(a->R.ok().setResult(a.get(0).getSyccode()));
    }
}

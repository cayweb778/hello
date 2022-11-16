package org.boozsoft.rest.stock;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;
import org.boozsoft.domain.entity.ReportEncodingRules;
import org.boozsoft.domain.entity.stock.*;
import org.boozsoft.domain.vo.stock.*;
import org.boozsoft.repo.ReportEncodingRulesRepository;
import org.boozsoft.repo.stock.*;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/stock/stockTransfers")
public class StockTransfersController {

    @Autowired
    private StockTransferRepository stockTransferRepository;
    @Autowired
    private StockTransfersRepository stockTransfersRepository;
    @Autowired
    private  StockCurrentstockRepository currentstockRepository;
    @Autowired
    private StockTransferCsourceRepository stockTransferCsourceRepository;
    @Autowired
    private ReportEncodingRulesRepository encodingRulesRepository;
    @Autowired
    private StockSaleousingRepository saleousingRepository;
    @Autowired
    private StockSaleousingsRepository saleousingsRepository;
    @Autowired
    private StockWarehousingRepository warehousingRepository;
    @Autowired
    private StockWarehousingsRepository warehousingsRepository;
    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private StockBeginBalanceRepository stockBeginBalanceRepository;

    @PostMapping("findBillByCondition")
    public Mono<R> findBillByCondition(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        String type = map.get("type").toString();
        String iyear = map.get("iyear").toString();
        String action = map.get("action").toString();
        String currPdId = map.containsKey("curr") ? map.get("curr").toString() : "";
        return stockTransferRepository.findAllByBillStyleAndIyearOrderByDdateAscCcodeAsc(type, iyear).collectList().cache().flatMap(list -> {
            if (list.size() == 0) {
                return Mono.just(R.ok());
            } else {
                StockTransfer master = null;
                switch (action) {
                    case "curr":
                        master = list.get((list.stream().map(e -> e.getCcode()).collect(Collectors.toList())).indexOf(currPdId));
                        break;
                    case "tail":
                        master = list.get(list.size() - 1);
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
                StockTransfer finalMaster = master;
                return stockTransfersRepository.findAllByCcodeOrderByDdateAscCcodeAscLineIdAsc(master.getCcode())
                        .collectList()
                        .map(enlist -> {
                            if (enlist.size() > 0) finalMaster.setEntryList(JSON.toJSONString(enlist));
                            return R.ok(finalMaster);
                        });
            }
        });
    }

    @PostMapping("lastDate")
    public Mono<R> lastDate(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        String year = map.get("year").toString();
        String type = map.get("type").toString();
        return stockTransferRepository.findFirstByBillStyleAndIyearOrderByDdateDesc(type,year).defaultIfEmpty(new StockTransfer()).map(o->R.ok(o.getDdate()));
    }

    @PostMapping
    @Transactional
    public Mono<R> save(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        Map<String, Object> obj = assemblyCorrespondingRulesData(map);
        StockTransfer master = (StockTransfer) obj.get("master");
        List<StockTransfers> sub = (List<StockTransfers>) obj.get("sub");
        boolean b = master.getId() == null;
        return stockTransferRepository.save(master)
                .flatMap(db -> {
                    Mono<StockTransfer> s = stockTransfersRepository.saveAll(sub).collectList().thenReturn(db); //添加
                    Mono<StockTransfer> d = stockTransfersRepository.findAllByCcodeOrderByDdateAscCcodeAscLineIdAsc( db.getCcode()).collectList().flatMap(list -> stockTransfersRepository.deleteAll(list).thenReturn("").flatMap(i -> s)); //修改
                    return b ? s : d;
                }).map(o -> R.ok());
    }

    private Map<String, Object> assemblyCorrespondingRulesData(Map map) {
        String type = map.get("billStyle").toString();
        String bstyle = "01"; //map.get("bstyle").toString();
        HashMap<String, Object> resut = new HashMap<>();
        switch (type) {
            case ("DBD"):
                StockTransfer warehousing = new StockTransfer();
                String sgspperson = map.containsKey("sgspperson") ? map.get("sgspperson").toString() : null; // 质检人
                String bcheckUser = map.containsKey("bcheckUser") ? map.get("bcheckUser").toString() : null; // 审核人
                warehousing.setId(map.containsKey("id") &&  null != map.get("id")? map.get("id").toString() : null);
                warehousing.setCcode(map.containsKey("ccode") ? map.get("ccode").toString() : null); //单号
                warehousing.setDdate(map.containsKey("ddate") ? map.get("ddate").toString().substring(0, 10) : null); // 日期
                warehousing.setIyear(map.containsKey("ddate") ? map.get("ddate").toString().substring(0, 4) : null);
                warehousing.setCmaker(map.containsKey("cmaker") ? map.get("cmaker").toString() : null); // 制单人
                warehousing.setCwhcode(map.containsKey("cwhcode") ? map.get("cwhcode").toString() : null);  // 调出仓库
                warehousing.setCwhcoderk(map.containsKey("cwhcoderk") ? map.get("cwhcoderk").toString() : null);  // 调入仓库
                warehousing.setCdepcode(map.containsKey("cdepcode") ? map.get("cdepcode").toString() : null); // 部门
                warehousing.setCpersoncode(map.containsKey("cpersoncode") ? map.get("cpersoncode").toString() : null); // 业务员
                warehousing.setCmemo(map.containsKey("cmemo") ? ObjectUtil.isEmpty(map.get("cmemo"))?null:map.get("cmemo").toString() : null);
                warehousing.setBillStyle(type);
                warehousing.setCwhcodeUser(map.containsKey("cwhcodeUser") ? map.get("cwhcodeUser").toString() : null);
                if (bcheckUser != null) {
                    warehousing.setBcheckUser(bcheckUser);
                    warehousing.setBcheck("1");
                    warehousing.setBcheckTime(DateUtil.now());
                }
                String entryList = map.containsKey("entryList") ? map.get("entryList").toString() : ""; // 表体
                List<StockTransfers> entrys = JSON.parseArray(entryList, StockTransfers.class);
                BigDecimal squantitySum = new BigDecimal(0);
                BigDecimal squantity1Sum = new BigDecimal(0);
                BigDecimal squantity2Sum = new BigDecimal(0);
                BigDecimal icostSum = new BigDecimal(0);
                for (StockTransfers entry : entrys) {
                    String tempCode = StrUtil.uuid();
                    StockTransfer saleousing = new StockTransfer();
                    entry.setId(null);
                    entry.setCfree1(LocalDateTime.now().toString());
                    entry.setIyear(warehousing.getIyear()) ;
                    entry.setDdate(warehousing.getDdate()) ;
                    entry.setCcode(warehousing.getCcode())  ;
                    entry.setCmaker(warehousing.getCmaker()) ;
                    entry.setCdepcode(warehousing.getCdepcode());
                    BeanUtils.copyProperties(entry,saleousing);
                    BigDecimal b1 = new BigDecimal(entry.getBaseQuantity() == null ? "0" : entry.getBaseQuantity());
                    squantitySum = squantitySum.add(b1);
                    BigDecimal s1 = new BigDecimal(entry.getSubQuantity1() == null ? "0" : entry.getSubQuantity1());
                    squantity1Sum = squantity1Sum.add(s1);
                    BigDecimal s2 = new BigDecimal(entry.getSubQuantity2() == null ? "0" : entry.getSubQuantity2());
                    squantity2Sum = squantity2Sum.add(s2);
                    BigDecimal ic = new BigDecimal(entry.getIcost() == null ? "0" : entry.getIcost());
                    icostSum = icostSum.add(ic);
                }
                resut.put("master", warehousing);
                resut.put("sub", entrys);
                break;
            default:
                break;
        }
        return resut;
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
        return encodingRulesRepository.findByFileType(key).switchIfEmpty(Mono.just(new ReportEncodingRules())).zipWith(stockTransferRepository.findFirstByBillStyleAndDdateLikeOrderByDdateDescCcodeDesc(type, "%" + date.substring(0, 7) + "%").switchIfEmpty(Mono.just(new StockTransfer()))).flatMap(tips -> {
            ReportEncodingRules obj = tips.getT1();
            StockTransfer it = tips.getT2();
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

    @PostMapping("batchSelectorList")
    public Mono<R> batchSelectorList(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        String year = map.get("year").toString();
        List<String> codes = (List<String>) map.get("codes");
        return currentstockRepository.findAllByIyearAndInvCodeIn(year,codes).collectList().map(R::ok);
    }

    @DeleteMapping
    @Transactional
    public Mono<R> del(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        return stockTransferRepository.findById(map.get("id").toString()).flatMap(db ->
                stockTransfersRepository.findAllByCcodeOrderByDdateAscCcodeAscLineIdAsc(db.getCcode()).collectList()
                        .flatMap(list -> stockTransfersRepository.deleteAll(list).thenReturn("").flatMap(i -> stockTransferRepository.delete(db).thenReturn("")).flatMap(s->
                                db.getBillStyle().equals("DBD")?stockTransferCsourceRepository.findAllByCcodeAndIyear(db.getCcode(),db.getDdate().substring(0,4)).collectList().flatMap(xd->stockTransferCsourceRepository.deleteAll(xd).thenReturn("")):Mono.just("")
                        )) //修改
        ).map(o -> R.ok());
    }

    @PostMapping("review")
    @Transactional
    public Mono<R> review(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        String ccode = map.get("ccode").toString();
        String userId = map.get("userId").toString();
        String type = map.get("type").toString();
        List<String> keys = map.containsKey("keys") ? (List<String>) map.get("keys") : new ArrayList<>();
        //入库
        AtomicReference<String> rkId = new AtomicReference<>("");
        //出库
        AtomicReference<String> ckId = new AtomicReference<>("");
        AtomicReference<String> rkCcode = new AtomicReference<>("");
        AtomicReference<String> ckCcode = new AtomicReference<>("");
        AtomicReference<String> dbCcode = new AtomicReference<>("");
        AtomicReference<String> cwhcode = new AtomicReference<>("");
        AtomicReference<String> cwhcoderk = new AtomicReference<>("");
        return stockTransferRepository.findByCcode(ccode)
                .flatMap(dbEntry->{
                    //获取单据最新的编码-其他入库
                    String date = dbEntry.getDdate();//map.get("date").toString();
                    String types = "QTRKD";//map.get("type").toString();
                    String key = "3-13";//map.get("key").toString();
                    return warehousingRepository.findAllTypeAndIyear(types, date.substring(0,4))
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
                    return saleousingRepository.findAllByBillStyleAndIyearOrderByDdateAscCcodeAsc(types, date.substring(0,4))
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
                .map(dbEntry -> {
                    rkId.set(dbEntry.getCwhcode());
                    ckId.set(dbEntry.getCwhcoderk());
                    dbEntry.setBcheck("1");
                    dbEntry.setBcheckTime(LocalDate.now().toString());
                    dbEntry.setBcheckUser(userId);
                    dbCcode.set(dbEntry.getCcode());
                    cwhcoderk.set(dbEntry.getCwhcoderk());
                    cwhcode.set(dbEntry.getCwhcode());
                    return dbEntry;
                })
                .flatMap(stockTransferRepository::save)
                .map(dbEntry -> {
                    // 数据调整后
                    StockSaleousing saleousing = new StockSaleousing();
                    BeanUtils.copyProperties(dbEntry, saleousing);
                    saleousing.setId(null);
                    saleousing.setCmakerTime(LocalDateTime.now().toString());
                    saleousing.setBstyle("调拨出库");//入库类别（收发方式中的收方向编码）
                    saleousing.setUnitType("etc");
                    saleousing.setUnitValue("调拨出库生成");
                    saleousing.setBillStyle("QTCKD");
                    saleousing.setSourcetype("DBD");//来源单据类型id
                    saleousing.setCcode(ckCcode.get());
                    saleousing.setBdocumStyle("0");
                    saleousing.setCwhcode(cwhcode.get());
                    saleousing.setBcheck("1");
                    saleousing.setBcheckTime(LocalDate.now().toString());
                    saleousing.setBcheckUser(userId);
                    return saleousing;
                })
                .flatMap(saleousingRepository::save)
                .flatMap(dbEntry->{
                    StockTransferSource stockTransferSource = new StockTransferSource();
                    stockTransferSource.setIyear(dbEntry.getIyear());
                    stockTransferSource.setBillStyle("DBCKD");
                    stockTransferSource.setXyBillStyle("QTCKD");
                    stockTransferSource.setCcodeDate(dbEntry.getDdate());
                    stockTransferSource.setCcode(dbCcode.get());
                    stockTransferSource.setSyccode(dbEntry.getCcode());
                    stockTransferSource.setSyccodeDate(dbEntry.getDdate());
                    return  stockTransferCsourceRepository.save(stockTransferSource)
                            .map(item->{
                                return dbEntry;
                            });
                })
                .map(dbEntry -> {
                    StockWarehousing warehousing = new StockWarehousing();
                    BeanUtils.copyProperties(dbEntry, warehousing);
                    warehousing.setId(null);
                    warehousing.setCwhcode(rkId.get());
                    warehousing.setCmakerTime(LocalDateTime.now().toString());
                    warehousing.setBstyle("调拨入库");//入库类别（收发方式中的收方向编码）
                    warehousing.setUnitType("etc");
                    warehousing.setUnitValue("调拨入库生成");
                    warehousing.setBillStyle("QTRKD");
                    warehousing.setSourcetype("DBD");//来源单据类型id
                    warehousing.setCcode(rkCcode.get());
                    warehousing.setBdocumStyle("0");
                    warehousing.setCwhcode(cwhcoderk.get());
                    warehousing.setBcheck("1");
                    warehousing.setBcheckTime(LocalDate.now().toString());
                    warehousing.setBcheckUser(userId);
                    return warehousing;
                })
                .flatMap(warehousingRepository::save)
                .flatMap(dbEntry->{
                    StockTransferSource stockTransferSource = new StockTransferSource();
                    stockTransferSource.setIyear(dbEntry.getIyear());
                    stockTransferSource.setBillStyle("DBRKD");
                    stockTransferSource.setXyBillStyle("QTRKD");
                    stockTransferSource.setCcodeDate(dbEntry.getDdate());
                    stockTransferSource.setCcode(dbCcode.get());
                    stockTransferSource.setSyccode(dbEntry.getCcode());
                    stockTransferSource.setSyccodeDate(dbEntry.getDdate());
                    return  stockTransferCsourceRepository.save(stockTransferSource)
                            .map(item->{
                                return dbEntry;
                            });
                })
                .flatMap(item -> {
                    return stockTransfersRepository.findByCcode(dbCcode.get()).collectList();
                })
                .map(list -> {
                    List<StockSaleousings> slist = new ArrayList<>();
                    list.stream().forEach(v->{
                        StockSaleousings sa = new StockSaleousings();
                        BeanUtils.copyProperties(v, sa);

                        sa.setCmakerTime(v.getCfree1());
                        sa.setCsource("DBD");//单据来源

                        sa.setBcheck(v.getBcheck());
                        sa.setBcheckUser(v.getBcheckUser());
                        sa.setSourcetype("DBD");//来源单据类型id
                        sa.setSourcecode(dbCcode.get());//来源单据编码
                        sa.setSourcedetailId(v.getLineCode());
                        sa.setSourcedate(v.getDdate());//来源单据日期

                        sa.setCangkuDuli("1");
                        sa.setLineId("1");
                        sa.setIsGive("0");
                        sa.setBillStyle("QTCKD");
                        sa.setBstyle("调拨出库");//入库类别（收发方式中的收方向编码）
                        sa.setDdate(v.getDdate());
                        sa.setCcode(ckCcode.get());
                        sa.setCmakerTime(LocalDateTime.now().toString());
                        sa.setQuantity(sa.getBaseQuantity());
                        sa.setCwhcode(cwhcode.get());
                        sa.setCwhcode1(cwhcode.get());

                        sa.setQuantity(v.getCnumber());
                        sa.setXsUnitId(v.getCgUnitId());
                        sa.setBcheck("1");
                        sa.setBcheckTime(LocalDate.now().toString());
                        sa.setBcheckUser(userId);
                        sa.setId(null);
                        slist.add(sa);
                    });
                    return slist;
                })
                .flatMap(list -> {
                    return saleousingsRepository.saveAll(list).collectList();
                })
                .map(list -> {
                    List<StockWarehousings> wlist = new ArrayList<>();
                    list.stream().forEach(v->{
                        StockWarehousings sw = new StockWarehousings();
                        BeanUtils.copyProperties(v, sw);
                        sw.setCmakerTime(v.getCfree1());
                        sw.setCsource("DBD");//单据来源

                        sw.setBcheck(v.getBcheck());
                        sw.setBcheckTime(v.getBcheckTime());
                        sw.setBcheckUser(v.getBcheckUser());

                        sw.setSourcetype("DBD");//来源单据类型id
                        sw.setSourcecode(dbCcode.get());//来源单据编码
                        sw.setSourcedetailId(v.getLineCode());
                        sw.setSourcedate(v.getDdate());//来源单据日期

                        sw.setCangkuDuli("1");
                        sw.setLineId("1");
                        sw.setBdocumStyle("0");
                        sw.setIsGive("0");
                        sw.setBillStyle("QTRKD");
                        sw.setBstyle("调拨入库");//入库类别（收发方式中的收方向编码）
                        sw.setDdate(v.getDdate());
                        sw.setCcode(rkCcode.get());
                        sw.setCmakerTime(LocalDateTime.now().toString());
                        sw.setCgUnitId(v.getXsUnitId());
                        sw.setCnumber(v.getQuantity());
                        sw.setCwhcode(cwhcoderk.get());
                        sw.setCwhcode1(cwhcoderk.get());

                        sw.setBcheck("1");
                        sw.setBcheckTime(LocalDate.now().toString());
                        sw.setBcheckUser(userId);
                        sw.setId(null);
                        wlist.add(sw);
                    });
                    return wlist;
                })
                .flatMap(list -> {
                    return warehousingsRepository.saveAll(list).collectList();
                })
                .map(R::ok);
    }

    @PostMapping("reviewBack")
    @Transactional
    public Mono<R> reviewBack(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        String code = map.get("ccode").toString();
        String userId = map.get("userId").toString();
        //入库
        AtomicReference<String> rkId = new AtomicReference<>("");
        //出库
        AtomicReference<String> ckId = new AtomicReference<>("");
        AtomicReference<String> ccode = new AtomicReference<>("");
        AtomicReference<String> rkccode = new AtomicReference<>("");
        AtomicReference<String> ckccode = new AtomicReference<>("");
        return stockTransferRepository.findByCcode(code)
                .map(dbEntry -> {
                    rkId.set(dbEntry.getCwhcode());
                    ckId.set(dbEntry.getCwhcoderk());
                    ccode.set(dbEntry.getCcode());
                    dbEntry.setBcheck("0");
                    dbEntry.setBcheckTime(null);
                    dbEntry.setBcheckUser(null);
                    return dbEntry;
                })
                .flatMap(stockTransferRepository::save)
                .flatMap(item -> {
                    return stockTransfersRepository.findByCcode(item.getCcode()).collectList();
                })
                .map(list -> {
                    list.stream().forEach(v->{
                        v.setBcheck("0");
                        v.setBcheckTime(null);
                        v.setBcheckUser(null);
                    });
                    return list;
                })
                .flatMap(list -> {
                    return stockTransfersRepository.saveAll(list).collectList();
                })
                .flatMap(o->{
                    //获取下游ccode
                    return stockTransferCsourceRepository.findAllByCcodeAndIyear(ccode.get(),"2022")
                            .collectList()
                            .map(list->{
                                Optional<StockTransferSource> first = list.stream().filter(v -> "QTRKD".equals(v.getXyBillStyle())).findFirst();
                                if(first.isPresent())  rkccode.set(first.get().getSyccode());
                                Optional<StockTransferSource> first1 = list.stream().filter(v -> "QTCKD".equals(v.getXyBillStyle())).findFirst();
                                if(first1.isPresent())  ckccode.set(first1.get().getSyccode());
                                return ccode.get();
                            });
                })
                .flatMap(c->{
                    return Objects.nonNull(ccode.get()) ? stockTransferCsourceRepository.deleteByCcode(ccode.get()).then(Mono.just(c)):Mono.just(c);
                })
                .flatMap(c->{
                    return Objects.nonNull(ckccode.get()) ? saleousingRepository.deleteByCcodeAndBillType(ckccode.get(),"QTCKD").thenReturn(c):Mono.just(c);
                })
                .flatMap(c->{
                    return Objects.nonNull(ckccode.get()) ?saleousingsRepository.deleteByCcodeAndBillType(ckccode.get(),"QTCKD").thenReturn(c):Mono.just(c);
                })
                .flatMap(c->{
                    return Objects.nonNull(rkccode.get()) ?warehousingRepository.deleteByCcodeAndBillType(rkccode.get(),"QTRKD").thenReturn(c):Mono.just(c);
                })
                .flatMap(c->{
                    return  Objects.nonNull(rkccode.get()) ?warehousingsRepository.deleteByCcodeAndBillType(rkccode.get(),"QTRKD").thenReturn(c):Mono.just(c);
                })
                .map(R::ok);
    }

    @GetMapping("/auditCheck/{ccode}/{rkBcheck}/{ckBcheck}/{flg}")
    public Mono<R> auditCheck(@PathVariable String ccode,@PathVariable String rkBcheck,@PathVariable String ckBcheck,@PathVariable String flg) {
        // 参数设置：入库保存状态就是现存量 标志 1是查询所有 0 查询已审核
        rkBcheck=ObjectUtil.isEmpty(rkBcheck)?"0":rkBcheck;
        ckBcheck=ObjectUtil.isEmpty(ckBcheck)?"0":ckBcheck;
        //审核弃审前校验现存量
        String finalRkBcheck = rkBcheck;
        String finalCkBcheck = ckBcheck;
        return stockTransferRepository.findByCcode(ccode)
                .flatMap(st->{
                    return stockTransfersRepository.findByCcode(ccode)
                            .collectList()
                            .map(stsList->{
                                st.setStsList(stsList);
                                return st;
                            });
                })
                .flatMap(st->{
                    //入库单 需要验证现存量
                    String ck = st.getCwhcode();
                    String year = st.getIyear();
                    List<StockAccSheetVo> skl = new ArrayList<>();
                    List<StockVo> sv = new ArrayList<>();
                    List<StockTransfers> stsList1 = st.getStsList();
                    List<String> cinvodeList = stsList1.stream().map(v -> v.getCinvode()).collect(Collectors.toList());
                    return stockRepository.findAllByXcl2(cinvodeList)
                            .collectList()
                            .flatMap(slist->{
                                //期初
                                return stockBeginBalanceRepository.findAllByIyearAndCkAndStockList(year,ck,cinvodeList)
                                        .collectList()
                                        .map(wl->{
                                            skl.addAll(wl);
                                            return slist;
                                        });
                            })
                            .flatMap(slist->{
                                //入库
                                return warehousingsRepository.findAllByIyearAndCkAndList(year,ck,cinvodeList)
                                        .filter(v-> {
                                            if(finalRkBcheck.equals("0")){
                                                return  "1".equals(v.getBcheck());
                                            }else{
                                                return  true;
                                            }
                                        })
                                        .collectList()
                                        .map(wl->{
                                            skl.addAll(wl);
                                            return slist;
                                        });
                            })
                            .flatMap(slist->{
                                //出库
                                return saleousingsRepository.findAllByIyearAndCkAndList(year,ck,cinvodeList)
                                        .filter(v-> {
                                            if(finalCkBcheck.equals("0")){
                                                return  "1".equals(v.getBcheck());
                                            }else{
                                                return  true;
                                            }
                                        })
                                        .collectList()
                                        .map(sl->{
                                            skl.addAll(sl);
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
                                                /*if(Objects.nonNull(v.getDpdate())){
                                                    str+="_"+v.getDpdate();
                                                }
                                                if(Objects.nonNull(v.getDvdate())){
                                                    str+="_"+v.getDvdate();
                                                }*/
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

                                    //入库+期初
                                    List<StockAccSheetVo> ckList = value.stream().filter(o ->    !"2".equals(o.getTypes())).collect(Collectors.toList());
                                    //出库
                                    List<StockAccSheetVo> rkList = value.stream().filter(o ->   "2".equals(o.getTypes())).collect(Collectors.toList());

                                    double sumBq = ckList.stream().mapToDouble(v -> {
                                        if (Objects.isNull(v.getBq())) return 0.00d;
                                        return Double.parseDouble(v.getBq().toString());
                                    }).sum();

                                    double sumBqrk = rkList.stream().mapToDouble(v -> {
                                        if (Objects.isNull(v.getBq())) return 0.00d;
                                        return Double.parseDouble(v.getBq().toString());
                                    }).sum();

                                    //现存量  期初+入库-出库
                                    BigDecimal subtract = new BigDecimal(sumBq).subtract(new BigDecimal(sumBqrk));

                                    //匹配存货的信息
                                    Optional<StockVo> first = list.stream().filter(v -> v.getStockNum().equals(value.get(0).getCinvode())).findFirst();
                                    //现存量存在 并且不等于0的
                                    if(first.isPresent() && subtract.compareTo(BigDecimal.ZERO) != 0){
                                        first.get().setXcl(subtract);
                                        //批号
                                        first.get().setBatchId(value.get(0).getBatchid());
                                        first.get().setDpdate(value.get(0).getDpdate());
                                        first.get().setDvdate(value.get(0).getDvdate());
                                        sv.add(first.get());
                                    }
                                });
                                return sv;
                            })
                            .map(list->{
                                AtomicReference<Boolean> b = new AtomicReference<>(false);
                                List<StockTransfers> stsList = st.getStsList();
                                //审核弃审 判断现存量
                                List<StockTransfers> rkList = stsList;
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
    @GetMapping("/auditCheck2/{ccode}/{rkBcheck}/{ckBcheck}/{flg}/{type}")
    public Mono<R> auditCheck2(@PathVariable String ccode,@PathVariable String rkBcheck,@PathVariable String ckBcheck,@PathVariable String flg,@PathVariable String type) {
        // 参数设置：入库保存状态就是现存量 标志 1是查询所有 0 查询已审核
        rkBcheck=ObjectUtil.isEmpty(rkBcheck)?"0":rkBcheck;
        ckBcheck=ObjectUtil.isEmpty(ckBcheck)?"0":ckBcheck;
        //审核弃审前校验现存量
        String finalRkBcheck = rkBcheck;
        String finalCkBcheck = ckBcheck;
        return stockTransferRepository.findByCcode(ccode)
                .flatMap(st->{
                    return stockTransfersRepository.findByCcode(ccode)
                            .collectList()
                            .map(stsList->{
                                st.setStsList(stsList);
                                return st;
                            });
                })
                .flatMap(st->{
                    //入库单 需要验证现存量
                    String ck = st.getCwhcode();
                    if("sh".equals(type)){
                        ck = st.getCwhcode();
                    }else{
                        ck = st.getCwhcoderk();
                    }

                    String year = st.getIyear();
                    List<StockAccSheetVo> skl = new ArrayList<>();
                    List<StockVo> sv = new ArrayList<>();
                    List<StockTransfers> stsList1 = st.getStsList();
                    List<String> cinvodeList = stsList1.stream().map(v -> v.getCinvode()).collect(Collectors.toList());
                    String finalCk = ck;
                    return stockRepository.findAllByXcl2(cinvodeList)
                            .collectList()
                            .flatMap(slist->{
                                //期初
                                return stockBeginBalanceRepository.findAllByIyearAndCkAndStockList(year, finalCk,cinvodeList)
                                        .collectList()
                                        .map(wl->{
                                            skl.addAll(wl);
                                            return slist;
                                        });
                            })
                            .flatMap(slist->{
                                //入库(其他 采购 领用)+到货单(采购到货单数量-累计入库数量)
                                return warehousingsRepository.findAllByIyearAndCkAndList(year,finalCk,cinvodeList)
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
                                return saleousingsRepository.findAllByIyearAndCkAndList(year,finalCk,cinvodeList)
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
                                                /*if(Objects.nonNull(v.getDpdate())){
                                                    str+="_"+v.getDpdate();
                                                }
                                                if(Objects.nonNull(v.getDvdate())){
                                                    str+="_"+v.getDvdate();
                                                }*/
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

                                    //入库+期初
                                    List<StockAccSheetVo> ckList = value.stream().filter(o ->    "0".equals(o.getTypes()) ||  "1".equals(o.getTypes())).collect(Collectors.toList());
                                    //出库
                                    List<StockAccSheetVo> rkList = value.stream().filter(o ->   "2".equals(o.getTypes())).collect(Collectors.toList());

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
                                return sv;
                            })
                            .map(list->{
                                AtomicReference<Boolean> b = new AtomicReference<>(false);
                                List<StockTransfers> stsList = st.getStsList();
                                //审核弃审 判断现存量
                                List<StockTransfers> rkList = stsList;
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

    /**
     * 下游
     * @return
     */
    @PostMapping("findByXyOutDataSourrce")
    public Mono<R> findByXyOutDataSourrce(@RequestBody Map map){
        String code = map.get("code").toString();
        String type = map.get("type").toString();
        return stockTransferCsourceRepository.findByXyOutDataSourrce(code).filter(it->StrUtil.isBlank(type)?true:it.getXyBillStyle().equals(type)).collectList().map(a->R.ok().setResult(a));
    }


    @PostMapping("findAllMainList")
    public Mono<R> findAllMainList(@RequestBody Map map){
        String strDate = map.get("strDate").toString();
        String endDate = map.get("endDate").toString();
        return stockTransferRepository.findMainList(strDate, endDate).collectList()
                .flatMap(list->{
                    //筛选
                    List<StockTransferVo> collect = list.stream().filter(v -> {
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

                        if (map.containsKey("cangrk") && ObjectUtil.isNotEmpty(map.get("cangrk").toString())) {
                            return v.getCwhcoderk().equals(map.get("cangrk").toString());
                        }
                        if (map.containsKey("cangck") && ObjectUtil.isNotEmpty(map.get("cangck").toString())) {
                            return v.getCwhcode().equals(map.get("cangck").toString());
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

        Mono<R> map1 = warehousingRepository.findByPyList(strDate, endDate, iyear, type)
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

        Mono<R> map2 = saleousingRepository.findByPkList(strDate, endDate, iyear, type)
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
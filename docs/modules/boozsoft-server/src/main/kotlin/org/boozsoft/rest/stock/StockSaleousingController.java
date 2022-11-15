package org.boozsoft.rest.stock;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.boozsoft.domain.entity.Customer;
import org.boozsoft.domain.entity.ReportEncodingRules;
import org.boozsoft.domain.entity.group.GroupStockPeriod;
import org.boozsoft.domain.entity.group.GroupSysCorp;
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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/stock/saleousing")
public class StockSaleousingController {

    @Autowired
    private StockSaleousingRepository saleousingRepository;

    @Autowired
    private StockSaleousingsRepository saleousingsRepository;

    @Autowired
    private ReportEncodingRulesRepository encodingRulesRepository;

    @Autowired
    private StockCurrentstockRepository currentstockRepository;

    @Autowired
    private StockXyCsourceRepository stockXyCsourceRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private GroupStockPeriodRepository groupStockPeriodRepository;

    @Autowired
    private StockJhzxStockRepository jhzxStockRepository;

    @Autowired
    private StockQuerenChangeRepository querenChangeRepository;

    @Autowired
    private StockBeginBalanceRepository beginBalanceRepository;

    @Autowired
    private StockWarehousingsRepository warehousingsRepository;
    @Autowired
    private StockWarehousingRepository warehousingRepository;
    @Autowired
    private StockJiesuanRepository jiesuanRepository;
    @Autowired
    private StockWuLiuRepository wuLiuRepository;
    @Autowired
    private StockBorrowRepository borrowRepository;

    @PostMapping
    @Transactional
    public Mono<R> save(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        Map<String, Object> obj = assemblyCorrespondingRulesData(map);
        StockSaleousing master = (StockSaleousing) obj.get("master");
        List<StockSaleousings> sub = (List<StockSaleousings>) obj.get("sub");
        boolean b = master.getId() == null;
        return saleousingRepository.save(master).flatMap(db -> b ? addXhd(db, sub) : editXhd(db, sub)).map(o -> R.ok());
    }

    private Mono<String> addXhd(StockSaleousing db, List<StockSaleousings> sub) {
        List<StockSaleousings> xsckd = sub.stream().filter(it -> StrUtil.isNotBlank(it.getSourcecode()) && StrUtil.isNotBlank(it.getSourcetype()) && (it.getSourcetype().equals("XSCKD") || it.getSourcetype().equals("XHD") || it.getSourcetype().equals("QCXHD"))).collect(Collectors.toList());
        Mono<String> undateMone =xsckd.size() == 0? Mono.just("")/*currentstockRepository.findAllByIyearAndInvCodeIn(db.getIyear(), sub.stream().map(it -> it.getCinvode()).distinct().collect(Collectors.toList())).collectList().flatMap(list -> {
            List<StockCurrentstock> xclList = new ArrayList<>();
            for (StockSaleousings s : sub) {
                if (list.stream().filter(c->s.getCinvode().equals(c.getInvCode()) && (StrUtil.isNotBlank(s.getBatchId()) ? (s.getBatchId().equals(c.getBatchId()) && s.getDvdate().equals(c.getDvdate()) && s.getDpdate().equals(c.getDpdate())) : true)).collect(Collectors.toList()).size() == 0){
                    xclList.add(
                            new StockCurrentstock().setIyear(s.getIyear()).setInvCode(s.getCinvode()).setBatchId(s.getBatchId()).setBatchDate(DateUtil.today()).setDvdate(s.getDvdate()).setDpdate(s.getDpdate()).setZtckQuantityXhd(new BigDecimal(s.getBaseQuantity())).setCunitidType(StrUtil.isNotBlank(s.getCunitidF1()) ? "多计量" : "单计量")
                            .setCwhcode(s.getCwhcode()).setCwhcode1(s.getCwhcode1()).setCwhcode2(s.getCwhcode3()).setCwhcode3(s.getCwhcode4()).setCwhcode4(s.getCwhcode4()).setCwhcode5(s.getCwhcode5()).setCwhcode6(s.getCwhcode6())
                    );
                }else {
                    for (StockCurrentstock c : list) {
                        if (s.getCinvode().equals(c.getInvCode()) && (StrUtil.isNotBlank(s.getBatchId()) ? (s.getBatchId().equals(c.getBatchId()) && s.getDvdate().equals(c.getDvdate()) && s.getDpdate().equals(c.getDpdate())) : true)){
                            BigDecimal s1 = db.getBdocumStyle().equals("0") ? c.getZtckQuantityXhd().add(new BigDecimal(s.getBaseQuantity())) : c.getZtckQuantityXhd().subtract(new BigDecimal(s.getBaseQuantity()).abs());
                            c.setZtckQuantityXhd(s1);
                            break;
                        }
                    }
                }
            }
            Mono<String> saves = xclList.size() == 0 ? Mono.just("") : currentstockRepository.saveAll(xclList).collectList().thenReturn("");
            Mono<String> moves = list.size() == 0 ? Mono.just("") : currentstockRepository.saveAll(list).collectList().thenReturn("");
            return saves.zipWith(moves).flatMap(ts->Mono.just(""));
        })*/:saleousingRepository.findByCcode(xsckd.get(0).getSourcecode()).zipWith(saleousingsRepository.findAllByCcode(xsckd.get(0).getSourcecode()).collectList()).flatMap(tips->{ // 出库单修改销货数量
            StockSaleousing xhEntity = tips.getT1();
            List<StockSaleousings> list = tips.getT2();
            BigDecimal ljNum = new BigDecimal(0);
            for (StockSaleousings saleousings : list) {
                List<StockSaleousings> collect = sub.stream().filter(it -> it.getSourcedetailId().equals(saleousings.getLineCode())).collect(Collectors.toList());
                if (collect.size() > 0){
                    BigDecimal add = null;
                    if (db.getBdocumStyle().equals("0")){
                         add = new BigDecimal((StrUtil.isNotBlank(saleousings.getIsumXiaohuo()) ? saleousings.getIsumXiaohuo() : "0")).add(new BigDecimal(collect.get(0).getBaseQuantity()));
                        saleousings.setIsumXiaohuo(keepDecimals(add,10));
                    }else {
                         add = new BigDecimal((StrUtil.isNotBlank(saleousings.getIsumTuiHuo()) ? saleousings.getIsumTuiHuo() : "0")).add(new BigDecimal(collect.get(0).getBaseQuantity()).abs());
                        saleousings.setIsumTuiHuo(keepDecimals(add,10));
                    }
                    ljNum =  ljNum.add(add);
                }
            }
            boolean b = list.stream().mapToDouble(it -> Double.parseDouble(it.getBaseQuantity())).sum() == ljNum.doubleValue();
            if (db.getBdocumStyle().equals("0")){
                xhEntity.setXiaohuoStatus(b?"1":null);
            }else {
                xhEntity.setTuihuoStatus(b?"1":null);
            }
            Mono<String> xy = stockXyCsourceRepository.save(new StockXyCsource().setIyear(db.getIyear()).setCcode(xhEntity.getCcode()).setCcodeDate(xhEntity.getDdate()).setBillStyle(xhEntity.getBillStyle()).setXyccode(db.getCcode()).setXyccodeDate(db.getDdate()).setXyBillStyle(db.getBillStyle())).flatMap(z -> Mono.just(""));
            if (sub.get(0).getSourcetype().equals("XSCKD"))
                for (StockSaleousings stockSaleousings : sub) stockSaleousings.setIsumChuku(stockSaleousings.getBaseQuantity());
            return saleousingRepository.save(xhEntity).flatMap(e->saleousingsRepository.saveAll(list).collectList().flatMap(s->xy).flatMap(s->Mono.just("")));
        });
        return saleousingsRepository.saveAll(sub).collectList().thenReturn(db).flatMap(z -> undateMone.flatMap(a -> Mono.just("")));
    }

    private Mono<String> editXhd(StockSaleousing db, List<StockSaleousings> sub) {
        return saleousingsRepository.findAllByBillStyleAndCcodeOrderByDdateAscCcodeAscLineIdAsc(db.getBillStyle(), db.getCcode()).collectList().flatMap(dblist -> {
            List<String> strings = dblist.stream().map(it -> it.getCinvode()).distinct().collect(Collectors.toList());
            for (StockSaleousings saleousings : sub) {
                saleousings.setId(null);
                strings.add(saleousings.getCinvode());
            }
            List<StockSaleousings> xsckd = sub.stream().filter(it -> StrUtil.isNotBlank(it.getSourcecode()) && StrUtil.isNotBlank(it.getSourcetype()) && (it.getSourcetype().equals("XSCKD") || it.getSourcetype().equals("XHD")|| it.getSourcetype().equals("QCXHD"))).collect(Collectors.toList());
            Mono<List<StockCurrentstock>> relifeMone =xsckd.size() == 0? Mono.just(new ArrayList<>())/*currentstockRepository.findAllByIyearAndInvCodeIn(db.getIyear(), strings.stream().distinct().collect(Collectors.toList())).collectList().flatMap(list -> {
                for (StockCurrentstock stockCurrentstock : list) {
                    // 先减除后添加
                    List<StockSaleousings> dbSales = dblist.stream().filter(i -> i.getCinvode().equals(stockCurrentstock.getInvCode()) && (StrUtil.isNotBlank(i.getBatchId()) ? (i.getBatchId().equals(stockCurrentstock.getBatchId()) && i.getDvdate().equals(stockCurrentstock.getDvdate()) && i.getDpdate().equals(stockCurrentstock.getDpdate())) : true)).collect(Collectors.toList());
                    if (dbSales.size() > 0) {
                        StockSaleousings saleousings = dbSales.get(0);
                        BigDecimal s1 = db.getBdocumStyle().equals("0") ? stockCurrentstock.getZtckQuantityXhd().subtract(new BigDecimal(saleousings.getBaseQuantity())) : stockCurrentstock.getZtckQuantityXhd().add(new BigDecimal(saleousings.getBaseQuantity()).abs());
                        stockCurrentstock.setZtckQuantityXhd(s1);
                    }
                    List<StockSaleousings> sales = sub.stream().filter(i -> i.getCinvode().equals(stockCurrentstock.getInvCode()) && (StrUtil.isNotBlank(i.getBatchId()) ? (i.getBatchId().equals(stockCurrentstock.getBatchId()) && i.getDvdate().equals(stockCurrentstock.getDvdate()) && i.getDpdate().equals(stockCurrentstock.getDpdate())) : true)).collect(Collectors.toList());
                    if (sales.size() > 0) {
                        StockSaleousings saleousings = sales.get(0);
                        BigDecimal s1 = db.getBdocumStyle().equals("0") ? stockCurrentstock.getZtckQuantityXhd().add(new BigDecimal(saleousings.getBaseQuantity())) : stockCurrentstock.getZtckQuantityXhd().subtract(new BigDecimal(saleousings.getBaseQuantity()).abs());
                        stockCurrentstock.setZtckQuantityXhd(s1);
                    }
                }
                return currentstockRepository.saveAll(list).collectList();
            })*/:saleousingRepository.findByCcode(xsckd.get(0).getSourcecode()).zipWith(saleousingsRepository.findAllByCcode(xsckd.get(0).getSourcecode()).collectList()).flatMap(tips->{ // 出库单修改销货数量
                StockSaleousing xhEntity = tips.getT1();
                List<StockSaleousings> list = tips.getT2();
                BigDecimal ljNum = new BigDecimal(0);
                for (StockSaleousings saleousings : list) {
                    // 先减除后添加
                    List<StockSaleousings> dbSales = dblist.stream().filter(it -> it.getSourcedetailId().equals(saleousings.getLineCode())).collect(Collectors.toList());
                    List<StockSaleousings> collect = sub.stream().filter(it -> it.getSourcedetailId().equals(saleousings.getLineCode())).collect(Collectors.toList());
                    BigDecimal add = null;
                    if (db.getBdocumStyle().equals("0")){
                        add = new BigDecimal((StrUtil.isNotBlank(saleousings.getIsumXiaohuo()) ? saleousings.getIsumXiaohuo() : "0"));
                        if (dbSales.size() > 0) add = add.subtract(new BigDecimal(dbSales.get(0).getBaseQuantity()));
                        if (collect.size() > 0) add = add.add(new BigDecimal(collect.get(0).getBaseQuantity()));
                        saleousings.setIsumXiaohuo(keepDecimals(add,10));
                    }else {
                        add = new BigDecimal((StrUtil.isNotBlank(saleousings.getIsumTuiHuo()) ? saleousings.getIsumTuiHuo() : "0"));
                        if (dbSales.size() > 0) add = add.subtract(new BigDecimal(dbSales.get(0).getBaseQuantity()).abs());
                        if (collect.size() > 0) add = add.add(new BigDecimal(collect.get(0).getBaseQuantity()).abs());
                        saleousings.setIsumTuiHuo(keepDecimals(add,10));
                    }
                    ljNum =  ljNum.add(add);
                }
                boolean b = list.stream().mapToDouble(it -> Double.parseDouble(it.getBaseQuantity())).sum() == ljNum.doubleValue();
                if (db.getBdocumStyle().equals("0")){
                    xhEntity.setXiaohuoStatus(b?"1":null);
                }else {
                    xhEntity.setTuihuoStatus(b?"1":null);
                }
                if (sub.get(0).getSourcetype().equals("XSCKD"))
                    for (StockSaleousings stockSaleousings : sub) stockSaleousings.setIsumChuku(stockSaleousings.getBaseQuantity());
                return saleousingRepository.save(xhEntity).flatMap(e->saleousingsRepository.saveAll(list).collectList().thenReturn(new ArrayList<>()));
            });
            return relifeMone.flatMap(d -> saleousingsRepository.deleteAll(dblist).thenReturn("").flatMap(c -> saleousingsRepository.saveAll(sub).collectList().thenReturn("")));
        });
    }

    @PostMapping("ckd")
    @Transactional
    public Mono<R> saveCkd(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        Map<String, Object> obj = assemblyCorrespondingRulesData(map);
        StockSaleousing master = (StockSaleousing) obj.get("master");
        List<StockSaleousings> sub = (List<StockSaleousings>) obj.get("sub");
        List<String> collect = sub.stream().filter(it -> it.getSourcecode() != null).map(it -> it.getSourcecode()).collect(Collectors.toList());
        boolean b = master.getId() == null;
        return saleousingRepository.save(master).flatMap(db -> {
            Mono<StockSaleousing> one = collect.size() > 0 ? saleousingRepository.findByCcode(collect.get(0)) : Mono.just(new StockSaleousing());
            Mono<List<StockSaleousings>> two = collect.size() > 0 ? saleousingsRepository.findAllByCcode(collect.get(0)).collectList() : Mono.just(new ArrayList<>());
            Mono<List<StockSaleousings>> three = saleousingsRepository.findAllByCcode(db.getCcode()).collectList();
            Mono<List<StockSaleousings>> addM = saleousingsRepository.saveAll(sub).collectList();
            Mono<String> editM = Mono.zip(one, two, three).flatMap(tips -> {
                BigDecimal zck = new BigDecimal(0);
                BigDecimal yck = new BigDecimal(0);
                StockSaleousing t1 = tips.getT1();
                List<StockSaleousings> t2 = tips.getT2();
                List<StockSaleousings> t3 = tips.getT3();
                for (StockSaleousings tw : t2) {//修改销货单累计出库量 与 出库标识
                    List<StockSaleousings> collect1 = t3.stream().filter(it -> tw.getLineCode().equals(it.getSourcedetailId())).collect(Collectors.toList());
                    BigDecimal add = new BigDecimal(StrUtil.isBlank(tw.getIsumChuku()) ? "0" : tw.getIsumChuku()).subtract(new BigDecimal(collect1.size() > 0 ? collect1.get(0).getBaseQuantity() : "0"));
                    List<StockSaleousings> collect2 = sub.stream().filter(it -> it.getSourcedetailId().equals(tw.getLineCode())).collect(Collectors.toList());
                    BigDecimal add1 = add.add(new BigDecimal(collect2.size() > 0 ? collect2.get(0).getBaseQuantity() : ""));
                    tw.setIsumChuku(keepDecimals(add1,10));
                    yck = yck.add(add1);
                    zck = zck.add(new BigDecimal(tw.getBaseQuantity()));
                }
                t1.setChukuStatus(zck.equals(yck) ? "1" : null);
                for (StockSaleousings saleousings : sub) saleousings.setId(null).setBstyle("销售出库");
                Mono<String> xy = stockXyCsourceRepository.save(new StockXyCsource().setIyear(db.getIyear()).setCcode(t1.getCcode()).setCcodeDate(t1.getDdate()).setBillStyle(t1.getBillStyle()).setXyccode(db.getCcode()).setXyccodeDate(db.getDdate()).setXyBillStyle(db.getBillStyle())).flatMap(z -> Mono.just(""));
                return Mono.zip(collect.size() > 0 ? saleousingRepository.save(t1).thenReturn("") : Mono.just(""), collect.size() > 0 ? saleousingsRepository.saveAll(t2).collectList().thenReturn("") : Mono.just(""), saleousingsRepository.deleteAll(t3).thenReturn(""),Mono.just(new ArrayList<>()), saleousingsRepository.saveAll(sub).collectList(),(b && collect.size() > 0?xy:Mono.just(""))).flatMap(tips2 -> Mono.just(""));
            });
            return !b || collect.size() > 0 ? editM : addM;
        }).map(o -> R.ok());
    }

    @PostMapping("qcxhd")
    @Transactional
    public Mono<R> saveQcxhd(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        Map<String, Object> obj = assemblyCorrespondingRulesData(map);
        StockSaleousing master = (StockSaleousing) obj.get("master");
        List<StockSaleousings> sub = (List<StockSaleousings>) obj.get("sub");
        boolean b = master.getId() == null;
        return saleousingRepository.save(master).flatMap(db -> {
            Mono<StockSaleousing> s = saleousingsRepository.saveAll(sub).collectList().flatMap(dbList->currentstockRepository.findAllByIyearAndInvCodeIn(db.getIyear(), sub.stream().map(it -> it.getCinvode()).distinct().collect(Collectors.toList())).collectList().flatMap(list -> {
              /*  for (StockCurrentstock stockCurrentstock : list) {
                    List<StockSaleousings> stockSaleousings = sub.stream().filter(i -> i.getCinvode().equals(stockCurrentstock.getInvCode()) && (StrUtil.isNotBlank(i.getBatchId()) ? (i.getBatchId().equals(stockCurrentstock.getBatchId()) && i.getDvdate().equals(stockCurrentstock.getDvdate()) && i.getDpdate().equals(stockCurrentstock.getDpdate())) : true)).collect(Collectors.toList());
                    if (stockSaleousings.size() > 0) {
                        StockSaleousings saleousings = stockSaleousings.get(0);
                        BigDecimal s1 = db.getBdocumStyle().equals("0") ? stockCurrentstock.getZtckQuantityXhd().add((new BigDecimal(saleousings.getBaseQuantity()).subtract(new BigDecimal(saleousings.getIsumChuku())))) : stockCurrentstock.getZtckQuantityXhd().subtract((new BigDecimal(saleousings.getBaseQuantity()).subtract(new BigDecimal(saleousings.getIsumChuku()))).abs());
                        stockCurrentstock.setZtckQuantityXhd(s1);
                    }
                }*/
                return Mono.just("")/* currentstockRepository.saveAll(list).collectList()*/;
            })).thenReturn(db); //添加
            Mono<StockSaleousing> d = saleousingsRepository.findAllByBillStyleAndCcodeOrderByDdateAscCcodeAscLineIdAsc(db.getBillStyle(), db.getCcode()).collectList().flatMap(list -> {
                Mono<List<StockCurrentstock>> changeMone = currentstockRepository.findAllByIyearAndInvCodeIn(db.getIyear(), list.stream().map(it -> it.getCinvode()).distinct().collect(Collectors.toList())).collectList().flatMap(list2 -> {
                    for (StockCurrentstock stockCurrentstock : list2) {
                        List<StockSaleousings> olds = list.stream().filter(i -> i.getCinvode().equals(stockCurrentstock.getInvCode()) && (StrUtil.isNotBlank(i.getBatchId()) ? (i.getBatchId().equals(stockCurrentstock.getBatchId()) && i.getDvdate().equals(stockCurrentstock.getDvdate()) && i.getDpdate().equals(stockCurrentstock.getDpdate())) : true)).collect(Collectors.toList());
                        if (olds.size() > 0) {
                            StockSaleousings saleousings = olds.get(0);
                            BigDecimal s1 = db.getBdocumStyle().equals("0") ? stockCurrentstock.getZtckQuantityXhd().subtract((new BigDecimal(saleousings.getBaseQuantity()).subtract(new BigDecimal(saleousings.getIsumChuku())))) : stockCurrentstock.getZtckQuantityXhd().add((new BigDecimal(saleousings.getBaseQuantity()).subtract(new BigDecimal(saleousings.getIsumChuku()))).abs());
                            stockCurrentstock.setZtckQuantityXhd(s1);
                        }
                        List<StockSaleousings> stockSaleousings = sub.stream().filter(i -> i.getCinvode().equals(stockCurrentstock.getInvCode()) && (StrUtil.isNotBlank(i.getBatchId()) ? (i.getBatchId().equals(stockCurrentstock.getBatchId()) && i.getDvdate().equals(stockCurrentstock.getDvdate()) && i.getDpdate().equals(stockCurrentstock.getDpdate())) : true)).collect(Collectors.toList());
                        if (stockSaleousings.size() > 0) {
                            StockSaleousings saleousings = stockSaleousings.get(0);
                            BigDecimal s1 = db.getBdocumStyle().equals("0") ? stockCurrentstock.getZtckQuantityXhd().add((new BigDecimal(saleousings.getBaseQuantity()).subtract(new BigDecimal(saleousings.getIsumChuku())))) : stockCurrentstock.getZtckQuantityXhd().subtract((new BigDecimal(saleousings.getBaseQuantity()).subtract(new BigDecimal(saleousings.getIsumChuku()))).abs());
                            stockCurrentstock.setZtckQuantityXhd(s1);
                        }
                    }
                    return currentstockRepository.saveAll(list2).collectList();
                });
                return /*changeMone.zipWith(*/saleousingsRepository.deleteAll(list).thenReturn("").flatMap(i -> saleousingsRepository.saveAll(sub).collectList())/*)*/.flatMap(tips->Mono.just(db));
            }); //修改
            return b ? s : d;
        }).map(o -> R.ok());
    }

    @PostMapping("qtckd")
    @Transactional
    public Mono<R> saveCkdQt(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        Map<String, Object> obj = assemblyCorrespondingRulesData(map);
        StockSaleousing master = (StockSaleousing) obj.get("master");
        List<StockSaleousings> sub = (List<StockSaleousings>) obj.get("sub");
        boolean b = master.getId() == null;
        return saleousingRepository.save(master).flatMap(db -> {
            Mono<StockSaleousing> s = saleousingsRepository.saveAll(sub).collectList().thenReturn(db); //添加
            Mono<StockSaleousing> d = saleousingsRepository.findAllByBillStyleAndCcodeOrderByDdateAscCcodeAscLineIdAsc(db.getBillStyle(), db.getCcode()).collectList().flatMap(list -> saleousingsRepository.deleteAll(list).thenReturn("").flatMap(i -> s)); //修改
            return b ? s : d;
        }).map(o -> R.ok());
    }

    @PostMapping("getBatch")
    public Mono<R> getBatch(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        List<String> codes = JSON.parseArray(map.get("codes").toString(), String.class);
        return saleousingRepository.findByCcodePlIn(codes).collectList().zipWith(saleousingsRepository.findAllByTotalsourceidInOrderByDdateAscCcodeAscLineIdAsc(codes).collectList()).flatMap(tips->
                Mono.just(R.ok(tips))
        );
    }

    @PostMapping("batch")
    @Transactional
    public Mono<R> saveBatch(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        Map<String, Object> obj = assemblyCorrespondingRulesData(map);
        StockSaleousing batch = (StockSaleousing) obj.get("batch");
        List<StockSaleousing> master = (List<StockSaleousing>) obj.get("master");
        List<StockSaleousings> sub = (List<StockSaleousings>) obj.get("sub");
        // 获取指定数量的单号
        Mono<List<String>> codesMono = Mono.just(master.size()).flatMapMany(size -> {
            List<Integer> s = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                s.add(i+1);
            }
            return Flux.fromIterable(s);
        }).flatMap(s->generateCode("3-6","XHD",batch.getDdate(),"",s)).collectList();
        return saleousingRepository.save(batch).zipWith(codesMono).flatMap(tips -> {
            // 新添加
            StockSaleousing db = tips.getT1();
            List<String> codes = tips.getT2();
            for (int i = 0; i < codes.size(); i++) {
                master.get(i).setCcode(codes.get(i));
                sub.get(i).setCcode(codes.get(i));
            }
            return Mono.zip(saleousingRepository.saveAll(master).collectList(),saleousingsRepository.saveAll(sub).collectList())
                    .flatMap(tis-> Mono.just(R.ok()));//添加
        });
    }

    @PostMapping("findBillByCondition")
    public Mono<R> findBillByCondition(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        String type = map.get("type").toString();
        String iyear = map.get("iyear").toString();
        String action = map.get("action").toString();
        String currPdId = map.containsKey("curr") ? map.get("curr").toString() : "";
        String bdocum = map.containsKey("bdocum") ? map.get("bdocum").toString() : "";
        return saleousingRepository.findAllByBillStyleAndIyearOrderByDdateAscCcodeAsc(type, iyear)
                .filter(it->bdocum.equals("")?true:it.getBdocumStyle().equals(bdocum))
                .collectList().cache()
                .flatMap(list -> {
            if (list.size() == 0) {
                return Mono.just(R.ok());
            } else {
                StockSaleousing master = null;
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
                StockSaleousing finalMaster = master;
                return saleousingsRepository.findAllByBillStyleAndCcodeOrderByDdateAscCcodeAscLineIdAsc(master.getBillStyle(), master.getCcode()).collectList().map(enlist -> {
                    if (enlist.size() > 0) finalMaster.setEntryList(JSON.toJSONString(enlist));
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
        return encodingRulesRepository.findByFileType(key).switchIfEmpty(Mono.just(new ReportEncodingRules())).zipWith(saleousingRepository.findFirstByBillStyleAndDdateLikeOrderByDdateDescCcodeDesc(type, "%" + date.substring(0, 7) + "%").switchIfEmpty(Mono.just(new StockSaleousing()))).flatMap(tips -> {
            ReportEncodingRules obj = tips.getT1();
            StockSaleousing it = tips.getT2();
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

    private Mono<List<StockCostAccRo>> getCurrCbPrice(List<StockSaleousings> dblist,String rkBcheck,String ckBcheck){
        //接收仓库唯一码、存货唯一码、批号、单据日期参数用于计算单价
        String ddate = dblist.get(0).getDdate();
        String year = dblist.get(0).getIyear();
        //list   成本单价计算 "0
        List<StockCostAccRo> scList = dblist.stream().map(it -> new StockCostAccRo().setStockCangku(it.getCwhcode1()).setStockNum(it.getCinvode()).setBatchId(it.getBatchId())).collect(Collectors.toList());
        List<String> stocks = scList.stream().map(v -> v.getStockNum()).collect(Collectors.toList());
        List<StockVo> stockVoList = new ArrayList<>();
        List<StockWarehousings> list1 = new ArrayList<>();
        List<StockBeginBalance> list2 = new ArrayList<>();
        List<StockSaleousings> list3 = new ArrayList<>();
        List<StockSaleousings> hzckList =  new ArrayList<>();
        return stockRepository.findAllByStockNums(stocks)
                .collectList()
                .map(list-> stockVoList.addAll(list))
                .flatMap(s->{
                    //入库 采购 其他 三个特殊+入库调整
                    return warehousingsRepository.findAllByCinvodesAndDdate(stocks, year, ddate)
                            .filter(o-> "0".equals(rkBcheck)? "1".equals(o.getBcheck()) : true)
                            .collectList()
                            .map(list-> list1.addAll(list));
                })
                .flatMap(s->{
                    //出库调整单
                    return saleousingsRepository.findAllByCinvodesAndDdate(stocks, year, ddate)
                            .filter(o-> "0".equals(ckBcheck)? "1".equals(o.getBcheck()) : true)
                            .collectList()
                            .map(list-> list3.addAll(list));
                })
                .flatMap(s->{
                    //期初
                    return beginBalanceRepository.findAllByCinvodes(stocks, year)
                            .collectList()
                            .map(list-> list2.addAll(list));
                })
                .flatMap(list->{
                    //-3红字出库( 材料领用 销售出库 其他出)
                    return saleousingsRepository.findAllByBillStyleAndYearAndDdate(year,ddate)
                            .filter(v-> {
                                if(ckBcheck.equals("0")){
                                    return  "1".equals(v.getBcheck());
                                }else{
                                    return  "0".equals(v.getBcheck()) || Objects.isNull(v.getBcheck());
                                }
                            })
                            .collectList()
                            .map(item->{
                                hzckList.addAll(item);
                                return list;
                            });
                })
                .map(v->{
                    //循环计算
                    scList.stream().forEach(item->{
                        Optional<StockVo> first = stockVoList.stream().filter(obj -> obj.getStockNum().equals(item.getStockNum())).findFirst();
                        if(first.isPresent()){
                            //成本核算方式
                            String type = first.get().getStockValuationType();
                            //判断存货的成本核算方式
                            if(type.equals("移动平均")){
                                //入库
                                List<StockWarehousings> rkList = list1.stream().filter(obj -> obj.getCinvode().equals(item.getStockNum()) && obj.getCwhcode().equals(item.getStockCangku())
                                ).collect(Collectors.toList());
                                //汇总主数量 无税金额
                                double num = rkList.stream().mapToDouble(obj -> {
                                    if (Objects.isNull(obj.getBaseQuantity())) return 0.00d;
                                    return Double.parseDouble(obj.getBaseQuantity().toString());
                                }).sum();

                                double moeny = rkList.stream().mapToDouble(obj -> {
                                    if (Objects.isNull(obj.getIcost())) return 0.00d;
                                    return Double.parseDouble(obj.getIcost().toString());
                                }).sum();

                                //期初
                                List<StockBeginBalance> qcList = list2.stream().filter(obj -> obj.getStockId().equals(item.getStockNum()) && obj.getStockCangkuId().equals(item.getStockCangku())).collect(Collectors.toList());
                                //汇总主数量 无税金额
                                double num1 = qcList.stream().mapToDouble(obj -> {
                                    if (Objects.isNull(obj.getBaseQuantity())) return 0.00d;
                                    return Double.parseDouble(obj.getBaseQuantity().toString());
                                }).sum();

                                double moeny1 = qcList.stream().mapToDouble(obj -> {
                                    if (Objects.isNull(obj.getIcost())) return 0.00d;
                                    return Double.parseDouble(obj.getIcost().toString());
                                }).sum();

                                //出库调整单
                                List<StockSaleousings> ckList = list3.stream().filter(obj -> obj.getCwhcode().equals(item.getStockNum()) && obj.getCwhcode().equals(item.getStockCangku())).collect(Collectors.toList());
                                //汇总主数量 无税金额
                                double num3 = ckList.stream().mapToDouble(obj -> {
                                    if (Objects.isNull(obj.getBaseQuantity())) return 0.00d;
                                    return Double.parseDouble(obj.getBaseQuantity().toString());
                                }).sum();

                                double moeny3 = ckList.stream().mapToDouble(obj -> {
                                    if (Objects.isNull(obj.getIcost())) return 0.00d;
                                    return Double.parseDouble(obj.getIcost().toString());
                                }).sum();

                                //获取当前出库存货 单据日期/制单日期之前的红字出库单
                                List<StockSaleousings> hzck = hzckList.stream()
                                        .filter(o -> o.getCinvode().equals(item.getStockNum())).collect(Collectors.toList());
                                //汇总主数量 无税金额
                                double num2 = hzck.stream().mapToDouble(obj -> {
                                    if (Objects.isNull(obj.getBaseQuantity())) return 0.00d;
                                    return Double.parseDouble(obj.getBaseQuantity().toString());
                                }).sum();
                                double moeny2 = hzck.stream().mapToDouble(obj -> {
                                    if (Objects.isNull(obj.getIcost())) return 0.00d;
                                    return Double.parseDouble(obj.getIcost().toString());
                                }).sum();

                                BigDecimal n = BigDecimal.valueOf(num).add(BigDecimal.valueOf(num1)).subtract(BigDecimal.valueOf(num3)).subtract(BigDecimal.valueOf(num2).abs());
                                BigDecimal m = BigDecimal.valueOf(moeny).add(BigDecimal.valueOf(moeny1)).subtract(BigDecimal.valueOf(moeny3)).subtract(BigDecimal.valueOf(moeny2).abs());

                                //计算成本单价
                                //数量0 =单价0
                                if(n.compareTo(BigDecimal.ZERO) == 0){
                                    item.setPrice("0");
                                }
                                //金额0 =单价0
                                if(m.compareTo(BigDecimal.ZERO) == 0){
                                    item.setPrice("0");
                                }
                                //数量负数 金额不是负数  =单价0
                                if(n.compareTo(BigDecimal.ZERO) == -1 && m.compareTo(BigDecimal.ZERO) == 1){
                                    item.setPrice("0");
                                }
                                //金额负数 数量不是负数 =单价0
                                if(m.compareTo(BigDecimal.ZERO) == -1 && n.compareTo(BigDecimal.ZERO) == 1){
                                    item.setPrice("0");
                                }
                                if(n.compareTo(BigDecimal.ZERO) != 0){
                                    item.setPrice(m.divide(n,10,BigDecimal.ROUND_HALF_UP).toString());
                                }
                            }else if(type.equals("相同批号")){
                                //过滤期初和入库
                                List<StockWarehousings> rkList = list1.stream().filter(obj -> {
                                    //没批号不操作
                                    if(!obj.getCinvode().equals(item.getStockNum()) || !obj.getCwhcode().equals(item.getStockCangku())){
                                        return false;
                                    }
                                    if(!obj.getBillStyle().equals("CGRKD")&&!obj.getBillStyle().equals("QTRKD")&&!obj.getBillStyle().equals("RKZTD")){
                                        return false;
                                    }
                                    if(Objects.isNull(item.getBatchId())){
                                        return false;
                                    }
                                    if(Objects.isNull(obj.getBatchId())){
                                        return false;
                                    }
                                    if(!item.getBatchId().equals(obj.getBatchId())){
                                        return false;
                                    }
                                    return true;
                                }).collect(Collectors.toList());
                                //汇总主数量 无税金额
                                double num = rkList.stream().mapToDouble(obj -> {
                                    if (Objects.isNull(obj.getBaseQuantity())) return 0.00d;
                                    return Double.parseDouble(obj.getBaseQuantity().toString());
                                }).sum();

                                double moeny = rkList.stream().mapToDouble(obj -> {
                                    if (Objects.isNull(obj.getIcost())) return 0.00d;
                                    return Double.parseDouble(obj.getIcost().toString());
                                }).sum();

                                List<StockBeginBalance> qcList = list2.stream().filter(obj ->{
                                    //没批号不操作
                                    if(Objects.isNull(item.getBatchId())){
                                        return false;
                                    }
                                    if(Objects.isNull(obj.getBatchNumber())){
                                        return false;
                                    }
                                    if(!item.getBatchId().equals(obj.getBatchNumber())){
                                        return false;
                                    }
                                    if(obj.getStockId().equals(item.getStockNum()) && obj.getStockCangkuId().equals(item.getStockCangku())){
                                        return true;
                                    }
                                    return false;
                                }).collect(Collectors.toList());
                                //汇总主数量 无税金额
                                double num1 = qcList.stream().mapToDouble(obj -> {
                                    if (Objects.isNull(obj.getBaseQuantity())) return 0.00d;
                                    return Double.parseDouble(obj.getBaseQuantity().toString());
                                }).sum();

                                double moeny1 = qcList.stream().mapToDouble(obj -> {
                                    if (Objects.isNull(obj.getIcost())) return 0.00d;
                                    return Double.parseDouble(obj.getIcost().toString());
                                }).sum();

                                //出库调整单
                                List<StockSaleousings> ckList = list3.stream().filter(obj -> {
                                    //没批号不操作
                                    if(Objects.isNull(item.getBatchId())){
                                        return false;
                                    }
                                    if(Objects.isNull(obj.getBatchId())){
                                        return false;
                                    }
                                    if(!item.getBatchId().equals(obj.getBatchId())){
                                        return false;
                                    }
                                    if(obj.getCwhcode().equals(item.getStockNum()) && obj.getCwhcode().equals(item.getStockCangku())){
                                        return true;
                                    }
                                    return false;
                                }).collect(Collectors.toList());
                                //汇总主数量 无税金额
                                double num3 = ckList.stream().mapToDouble(obj -> {
                                    if (Objects.isNull(obj.getBaseQuantity())) return 0.00d;
                                    return Double.parseDouble(obj.getBaseQuantity().toString());
                                }).sum();

                                double moeny3 = ckList.stream().mapToDouble(obj -> {
                                    if (Objects.isNull(obj.getIcost())) return 0.00d;
                                    return Double.parseDouble(obj.getIcost().toString());
                                }).sum();

                                BigDecimal n = BigDecimal.valueOf(num).add(BigDecimal.valueOf(num1)).subtract(BigDecimal.valueOf(num3));
                                BigDecimal m = BigDecimal.valueOf(moeny).add(BigDecimal.valueOf(moeny1)).subtract(BigDecimal.valueOf(moeny3));

                                //计算成本单价
                                //数量0 =单价0
                                if(n.compareTo(BigDecimal.ZERO) == 0){
                                    item.setPrice("0");
                                }
                                //金额0 =单价0
                                if(m.compareTo(BigDecimal.ZERO) == 0){
                                    item.setPrice("0");
                                }
                                //数量负数 金额不是负数  =单价0
                                if(n.compareTo(BigDecimal.ZERO) == -1 && m.compareTo(BigDecimal.ZERO) == 1){
                                    item.setPrice("0");
                                }
                                //金额负数 数量不是负数 =单价0
                                if(m.compareTo(BigDecimal.ZERO) == -1 && n.compareTo(BigDecimal.ZERO) == 1){
                                    item.setPrice("0");
                                }
                                if(n.compareTo(BigDecimal.ZERO) != 0){
                                    item.setPrice(m.divide(n,10,BigDecimal.ROUND_HALF_UP).toString());
                                }
                            }else if(type.equals("手工计价")){
                                //无成本单价反馈，需要手工输入成本价格
                                item.setPrice("0");
                            }else if(type.equals("先进先出")){
                                item.setPrice("0");
                            }
                        }
                    });
                    return scList;
                })
                .flatMap(o-> Mono.just(o));
    }

    /**
     *
     * @param key 代码规则
     * @param type 单据类型
     * @param date 时间
     * @param customize 自定义前缀
     * @param auto 动态自增
     * @return 单据编码
     */
    private Mono<String> generateCode(String key, String type, String date, String customize,Integer auto) {
        return encodingRulesRepository.findByFileType(key).switchIfEmpty(Mono.just(new ReportEncodingRules())).zipWith(saleousingRepository.findFirstByBillStyleAndDdateLikeOrderByDdateDescCcodeDesc(type, "%" + date.substring(0, 7) + "%").switchIfEmpty(Mono.just(new StockSaleousing()))).flatMap(tips -> {
            ReportEncodingRules obj = tips.getT1();
            StockSaleousing it = tips.getT2();
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
                int t = Integer.parseInt(it.getCcode().replace(pre.toString(), "")) + auto;
                return Mono.just(pre.toString() + String.format("%0" + l + "d", t));
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
        String auto = map.get("auto").toString();
        // 参数设置：入库保存状态就是现存量 标志 1是查询所有 0 查询已审核
        String rkBcheck=map.get("rkBcheck")==null?"0":map.get("rkBcheck").toString();
        String ckBcheck=map.get("ckBcheck")==null?"0":map.get("ckBcheck").toString();
        List<String> keys = map.containsKey("keys") ? (List<String>) map.get("keys") : new ArrayList<>();
        return saleousingRepository.findById(id).flatMap(dbEntry ->
                saleousingsRepository.findAllByBillStyleAndCcodeOrderByDdateAscCcodeAscLineIdAsc(dbEntry.getBillStyle(), dbEntry.getCcode()).collectList()
                    .flatMap(list -> {
                            Mono<List<StockCurrentstock>> one = currentstockRepository.findAllByIyearAndInvCodeIn(dbEntry.getIyear(), list.stream().map(it -> it.getCinvode()).distinct().collect(Collectors.toList())).collectList().flatMap(list1 -> {
                                // 修改在途数量
                                for (StockCurrentstock stockCurrentstock : list1) {
                                    List<StockSaleousings> stockSaleousings = list.stream().filter(i -> i.getCinvode().equals(stockCurrentstock.getInvCode()) && (StrUtil.isNotBlank(i.getBatchId())?( i.getBatchId().equals(stockCurrentstock.getBatchId()) && i.getDvdate().equals(stockCurrentstock.getDvdate()) && i.getDpdate().equals(stockCurrentstock.getDpdate())):true)).collect(Collectors.toList());
                                    if (stockSaleousings.size() > 0){ // 减
                                        StockSaleousings saleousings = stockSaleousings.get(0);
                                        BigDecimal xh = null;
                                        BigDecimal ck = null;
                                        if (type.equals("true")){
                                            xh = stockCurrentstock.getZtckQuantityXhd().subtract(new BigDecimal(saleousings.getBaseQuantity()));
                                            ck = stockCurrentstock.getZtckQuantityXsck().add(new BigDecimal(saleousings.getBaseQuantity()));
                                        }/*else {
                                            xh = stockCurrentstock.getZtckQuantityXhd().add(new BigDecimal(saleousings.getBaseQuantity()));
                                            ck = stockCurrentstock.getZtckQuantityXsck().subtract(new BigDecimal(saleousings.getBaseQuantity()));
                                        }*/
                                        if (auto.equals("true") && (null == dbEntry.getSourcetype() || !dbEntry.getSourcetype().equals("XSCKD"))){
                                            stockCurrentstock.setZtckQuantityXhd(xh);
                                            stockCurrentstock.setZtckQuantityXsck(ck);
                                        }
                                    }
                                }
                                return currentstockRepository.saveAll(list1).collectList();
                            });
                            if (type.equals("true")) {
                                HashSet<String> dbSet = new HashSet<>(list.stream().filter(it -> StrUtil.isNotBlank(it.getBcheck()) && it.getBcheck().equals("1")).map(it -> it.getLineCode()).collect(Collectors.toList()));
                                dbSet.addAll(keys);
                                if (keys.size() == 0 || dbSet.size() == list.size()) {
                                    dbEntry.setBcheck("1").setBcheckTime(DateUtil.now()).setBcheckUser(userId);
                                    if(auto.equals("true") && (null == dbEntry.getSourcetype() || !dbEntry.getSourcetype().equals("XSCKD"))) dbEntry.setChukuStatus("1");
                                    for (StockSaleousings stockWarehousings : list) {
                                        stockWarehousings.setBcheck("1").setBcheckTime(DateUtil.now()).setBcheckUser(userId);
                                    }

                                    return /*one.flatMap(e->*/
                                            (auto.equals("true") && (null == dbEntry.getSourcetype() || !dbEntry.getSourcetype().equals("XSCKD"))?(generateCode("3-11","XSCKD",dbEntry.getDdate(),"").zipWith(getCurrCbPrice(list,rkBcheck,ckBcheck))).flatMap(s->portrayXsckd(dbEntry,list,s.getT1(),s.getT2()).flatMap(tips->{
                                                StockSaleousing t1 = tips.getT1();
                                                List<StockSaleousings> t2 = tips.getT2();
                                                StockXyCsource t3 = tips.getT3();
                                                return saleousingRepository.save(t1).zipWith(saleousingsRepository.saveAll(t2).collectList()).zipWith(stockXyCsourceRepository.save(t3)).flatMap(ts->Mono.just(t3));
                                            }))
                                                    :Mono.just("")).flatMap(tips->saleousingRepository.save(dbEntry).flatMap(e1 -> saleousingsRepository.saveAll(list).collectList().thenReturn(tips))
                                    )/*)*/;
                                } else {
                                    for (StockSaleousings stockWarehousings : list) {
                                        if (keys.contains(stockWarehousings.getLineCode()))
                                            stockWarehousings.setBcheck("1").setBcheckTime(DateUtil.now()).setBcheckUser(userId);
                                    }
                                    return saleousingsRepository.saveAll(list).collectList().thenReturn(dbEntry);
                                }
                            } else {
                                dbEntry.setBcheck(null).setBcheckTime(null).setBcheckUser(null);
                                for (StockSaleousings stockWarehousings : list) {
                                    stockWarehousings.setIsumChuku("0.0000000000");
                                    if (keys.size() == 0) {
                                        stockWarehousings.setBcheck(null).setBcheckTime(null).setBcheckUser(null);
                                    } else if (keys.contains(stockWarehousings.getLineCode())) {
                                        stockWarehousings.setBcheck(null).setBcheckTime(null).setBcheckUser(null);
                                    }
                                }
                                return deprecated(dbEntry, list);
                            }
                        })).map(R::ok);
    }

    @PostMapping("batchReview/ckd")
    @Transactional
    public Mono<R> batchReviewCkd(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        List<String> codes = (List<String>) map.get("codes");
        String userId = map.get("userId").toString();
        String type = map.get("type").toString();
        return Flux.fromIterable(codes).flatMap(id -> saleousingRepository.findByCcode(id).flatMap(dbEntry -> saleousingsRepository.findAllByBillStyleAndCcodeOrderByDdateAscCcodeAscLineIdAsc(dbEntry.getBillStyle(), dbEntry.getCcode()).collectList().flatMap(list -> {
            if (type.equals("true")) {
                    dbEntry.setBcheck("1").setBcheckTime(DateUtil.now()).setBcheckUser(userId);
                    for (StockSaleousings stockWarehousings : list) {
                        stockWarehousings.setBcheck("1").setBcheckTime(DateUtil.now()).setBcheckUser(userId);
                    }
                    return saleousingRepository.save(dbEntry).flatMap(e1 -> saleousingsRepository.saveAll(list).collectList().thenReturn(""));

            } else {
                dbEntry.setBcheck(null).setBcheckTime(null).setBcheckUser(null);
                for (StockSaleousings stockWarehousings : list) {
                        stockWarehousings.setBcheck(null).setBcheckTime(null).setBcheckUser(null);

                }
                return saleousingRepository.save(dbEntry).flatMap(o -> saleousingsRepository.saveAll(list).collectList().thenReturn(""));
            }
                }))

        ).collectList().map(R::ok);
    }

    @PostMapping("batchReview")
    @Transactional
    public Mono<R> batchReview(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        List<String> codes = (List<String>) map.get("codes");
        String userId = map.get("userId").toString();
        String type = map.get("type").toString();
        String auto = map.get("auto").toString();
        // 参数设置：入库保存状态就是现存量 标志 1是查询所有 0 查询已审核
        String rkBcheck=map.get("rkBcheck")==null?"0":map.get("rkBcheck").toString();
        String ckBcheck=map.get("ckBcheck")==null?"0":map.get("ckBcheck").toString();
        return Flux.fromIterable(codes).flatMap(id -> saleousingRepository.findByCcode(id).flatMap(dbEntry -> saleousingsRepository.findAllByBillStyleAndCcodeOrderByDdateAscCcodeAscLineIdAsc(dbEntry.getBillStyle(), dbEntry.getCcode()).collectList().flatMap(list -> {
                    if (type.equals("true")) {
                        dbEntry.setBcheck("1").setBcheckTime(DateUtil.now()).setBcheckUser(userId);
                        for (StockSaleousings stockWarehousings : list) {
                            stockWarehousings.setBcheck("1").setBcheckTime(DateUtil.now()).setBcheckUser(userId);
                        }
                        return (auto.equals("true")?(generateCode("3-11","XSCKD",dbEntry.getDdate(),"").zipWith(getCurrCbPrice(list,rkBcheck,ckBcheck))).flatMap(s->portrayXsckd(dbEntry, list,s.getT1(),s.getT2())).flatMap(tips -> {
                            StockSaleousing t1 = tips.getT1();
                            List<StockSaleousings> t2 = tips.getT2();
                            StockXyCsource t3 = tips.getT3();
                            return saleousingRepository.save(t1).zipWith(saleousingsRepository.saveAll(t2).collectList()).zipWith(stockXyCsourceRepository.save(t3)).flatMap(ts -> Mono.just(t3));
                        }):Mono.just("")).flatMap(tips -> saleousingRepository.save(dbEntry).flatMap(e1 -> saleousingsRepository.saveAll(list).collectList().thenReturn(tips)));
                    } else {
                        dbEntry.setBcheck(null).setBcheckTime(null).setBcheckUser(null);
                        for (StockSaleousings stockWarehousings : list) {
                            stockWarehousings.setIsumChuku("0.0000000000");
                            stockWarehousings.setBcheck(null).setBcheckTime(null).setBcheckUser(null);

                        }
                        return deprecated(dbEntry, list);
                    }
                }))

        ).collectList().map(R::ok);
    }

    @PostMapping("unAuditBefore")
    public Mono<R> unAuditBefore(@RequestBody Map map) {
        List<String> codes = (List<String>) map.get("codes");
        String style =  map.get("style").toString();
        String id =  map.get("id").toString();
        HashMap<String, Object> result = new HashMap<>();
        result.put("type","0");
        return saleousingRepository.findByCcodeIn(codes).collectList().flatMap(list->{
            List<String> ymList = list.stream().map(it -> it.getDdate().substring(0, 10)).distinct().collect(Collectors.toList());
            Mono<List<GroupStockPeriod>> proiodsMono = groupStockPeriodRepository.findAllByUniqueCodeAndStockYearInAndStockMonthInOrderByStockYearAscStockMonthAsc(id, ymList.stream().map(s -> s.substring(0, 4)).distinct().collect(Collectors.toList()), ymList.stream().map(s -> s.substring(5, 7)).distinct().collect(Collectors.toList())).collectList();
            return proiodsMono.flatMap(ps->{
                List<GroupStockPeriod> periods = ps.stream().filter(it -> null != it.getIstock() &&  it.getIstock().equals("1")).collect(Collectors.toList());
                if (periods.size() > 0){
                    result.put("type","1");
                    result.put("codes",periods.stream().map(it -> it.getStockYear()+it.getStockMonth()).distinct().collect(Collectors.toList()));
                    return Mono.just(R.ok(result));
                }else {
                    return stockXyCsourceRepository.findAllByBillStyleAndCcodeIn(style,codes).collectList().flatMap(xyList->{
                        if (xyList.size() > 0){
                            result.put("type","2");
                            result.put("codes",xyList.stream().map(it->it.getCcode()).collect(Collectors.toList()));
                            result.put("codeType",new HashSet<>(xyList.stream().map(it->it.getXyBillStyle()).collect(Collectors.toList())));
                            return Mono.just(R.ok(result));
                        }else {
                            List<StockSaleousing> hsList = list.stream().filter(it -> null != it.getHxIsum() && Double.parseDouble(it.getHxIsum()) != 0).collect(Collectors.toList());
                            List<StockSaleousing> hcList = list.stream().filter(it -> null != it.getHzhcNum() && Integer.parseInt(it.getHzhcNum()) > 0).collect(Collectors.toList());
                            List<StockSaleousing> ckList = list.stream().filter(it -> null != it.getChukuStatus() && it.getChukuStatus().equals("1")).collect(Collectors.toList());
                            if (hsList.size() > 0 || hcList.size() > 0 || ckList.size() > 0) {
                                result.put("type", hsList.size() > 0 ? "3" : hcList.size() > 0 ? "4" : "5");
                                result.put("codes", (hsList.size() > 0 ? hsList : hcList.size() > 0 ? hcList : ckList).stream().map(it -> it.getCcode()).collect(Collectors.toList()));
                                return Mono.just(R.ok(result));
                            } else {
                                return saleousingsRepository.findAllByBillStyleAndCcodeIn(style, codes).collectList().flatMap(bodys -> {
                                    List<StockSaleousings> cks = bodys.stream().filter(it -> null != it.getIsumChuku() && Double.parseDouble(it.getIsumChuku()) != 0).collect(Collectors.toList());
                                    List<StockSaleousings> kps = bodys.stream().filter(it -> null != it.getIsumFapiao() && Double.parseDouble(it.getIsumFapiao()) != 0).collect(Collectors.toList());
                                    List<StockSaleousings> ths = bodys.stream().filter(it -> null != it.getIsumTuiHuo() && Double.parseDouble(it.getIsumTuiHuo()) != 0).collect(Collectors.toList());
                                    if (cks.size() > 0 || kps.size() > 0 || ths.size() > 0) {
                                        result.put("type", cks.size() > 0 ? "5" : kps.size() > 0 ? "6" : "7");
                                        result.put("codes", (cks.size() > 0 ? cks : kps.size() > 0 ? kps : ths).stream().map(it -> it.getCcode()).distinct().collect(Collectors.toList()));
                                    }
                                    return Mono.just(R.ok(result));
                                });
                            }
                        }
                    });
                }
            });
        });
    }

    @NotNull
    private Mono<?> deprecated(StockSaleousing dbEntry, List<StockSaleousings> list) {
        Mono<String> two = stockXyCsourceRepository.findByIyearAndCcodeAndCcodeDate(dbEntry.getIyear(), dbEntry.getCcode(), dbEntry.getDdate()).switchIfEmpty(Mono.just(new StockXyCsource())).flatMap(dbXy -> /*saleousingRepository.findByIyearAndDdateAndCcode(dbXy.getIyear(), dbXy.getXyccodeDate(), dbXy.getXyccode()).flatMap(db -> saleousingsRepository.findAllByBillStyleAndCcodeOrderByDdateAscCcodeAscLineIdAsc(db.getBillStyle(), db.getCcode()).collectList().flatMap(list2 -> saleousingsRepository.deleteAll(list2).thenReturn("").flatMap(i -> saleousingRepository.delete(db).thenReturn("")).flatMap(i -> */null == dbXy.getId()?Mono.just(""):stockXyCsourceRepository.delete(dbXy).thenReturn("")/*)))*/ //修改
        );
        return two.flatMap(e -> saleousingRepository.save(dbEntry).flatMap(o -> saleousingsRepository.saveAll(list).collectList().thenReturn(e)));
    }

    @PostMapping("review/ckd")
    @Transactional
    public Mono<R> reviewCkd(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        String id = map.get("id").toString();
        String userId = map.get("userId").toString();
        String type = map.get("type").toString();
        List<String> keys = map.containsKey("keys") ? (List<String>) map.get("keys") : new ArrayList<>();
        // 参数设置：入库保存状态就是现存量 标志 1是查询所有 0 查询已审核
        String rkBcheck=map.get("rkBcheck")==null?"0":map.get("rkBcheck").toString();
        String ckBcheck=map.get("ckBcheck")==null?"0":map.get("ckBcheck").toString();
        return saleousingRepository.findById(id).flatMap(dbEntry ->
                saleousingsRepository.findAllByBillStyleAndCcodeOrderByDdateAscCcodeAscLineIdAsc(dbEntry.getBillStyle(), dbEntry.getCcode()).collectList()
                        .flatMap(list -> {
                            Mono<List<StockCurrentstock>> one = currentstockRepository.findAllByIyearAndInvCodeIn(dbEntry.getIyear(), list.stream().map(it -> it.getCinvode()).distinct().collect(Collectors.toList())).collectList().flatMap(list1 -> {
                                // 修改在途数量
                                for (StockCurrentstock stockCurrentstock : list1) {
                                    List<StockSaleousings> stockSaleousings = list.stream().filter(i -> i.getCinvode().equals(stockCurrentstock.getInvCode()) && (StrUtil.isNotBlank(i.getBatchId())?(i.getBatchId().equals(stockCurrentstock.getBatchId()) && i.getDvdate().equals(stockCurrentstock.getDvdate()) && i.getDpdate().equals(stockCurrentstock.getDpdate())):true)).collect(Collectors.toList());
                                    if (stockSaleousings.size() > 0){
                                        StockSaleousings saleousings = stockSaleousings.get(0);
                                    // 调整在涂量
                                    BigDecimal s1 = type.equals("true")?stockCurrentstock.getZtckQuantityXsck().subtract(new BigDecimal(saleousings.getBaseQuantity())):stockCurrentstock.getZtckQuantityXsck().add(new BigDecimal(saleousings.getBaseQuantity()));
                                    stockCurrentstock.setZtckQuantityXsck(s1);
                                    // 调整现存量
                                    BigDecimal c1 = type.equals("true")?stockCurrentstock.getBaseQuantity().subtract(new BigDecimal(saleousings.getBaseQuantity())):stockCurrentstock.getBaseQuantity().add(new BigDecimal(saleousings.getBaseQuantity()));
                                    stockCurrentstock.setBaseQuantity(c1);
                                 }
                                }
                                return currentstockRepository.saveAll(list1).collectList();
                            });

                            if (type.equals("true")) {
                                    dbEntry.setBcheck("1").setBcheckTime(DateUtil.now()).setBcheckUser(userId);
                               return saleousingRepository.save(dbEntry).zipWith( getCurrCbPrice(list,rkBcheck,ckBcheck)).flatMap(e1 -> {
                                   for (StockSaleousings a : list) {
                                       a.setBcheck("1").setBcheckTime(DateUtil.now()).setBcheckUser(userId);
                                       List<StockCostAccRo> accRos = e1.getT2().stream().filter(p -> p.getStockCangku().equals(a.getCwhcode1()) && p.getStockNum().equals(a.getCinvode()) && (StrUtil.isBlank(a.getBatchId()) ? true : a.getBatchId().equals(p.getBatchId()))).collect(Collectors.toList());
                                       if (accRos.size() > 0 && StrUtil.isNotBlank(accRos.get(0).getPrice())){
                                           BigDecimal b = new BigDecimal(a.getQuantity());
                                           BigDecimal price = calculateTheExchangeRate(a,accRos.get(0).getPrice()).abs();
                                           BigDecimal e = b.multiply(price);
                                           a.setPrice(keepDecimals(price,10));
                                           a.setIcost(keepDecimals(e,4));
                                       }
                                   }
                                   return saleousingsRepository.saveAll(list).collectList().thenReturn("");
                               });
                            } else {
                                dbEntry.setBcheck(null).setBcheckTime(null).setBcheckUser(null);
                                for (StockSaleousings stockWarehousings : list) {
                                    if (keys.size() == 0) {
                                        stockWarehousings.setBcheck(null).setBcheckTime(null).setBcheckUser(null);
                                    } else if (keys.contains(stockWarehousings.getLineCode())) {
                                        stockWarehousings.setBcheck(null).setBcheckTime(null).setBcheckUser(null);
                                    }
                                }
                                return /*one.flatMap(e->*/saleousingRepository.save(dbEntry).flatMap(o -> saleousingsRepository.saveAll(list).collectList().thenReturn(""))/*)*/;
                            }
                        })
        ).map(R::ok);
    }


    @PostMapping("review/xsdd")
    @Transactional
    public Mono<R> reviewXsdd(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        String id = map.get("id").toString();
        String userId = map.get("userId").toString();
        String type = map.get("type").toString();
        List<String> keys = map.containsKey("keys") ? (List<String>) map.get("keys") : new ArrayList<>();
        return saleousingRepository.findById(id).flatMap(dbEntry ->
                saleousingsRepository.findAllByBillStyleAndCcodeOrderByDdateAscCcodeAscLineIdAsc(dbEntry.getBillStyle(), dbEntry.getCcode()).collectList()
                        .flatMap(list -> {
                            if (type.equals("true")) {
                                HashSet<String> dbSet = new HashSet<>(list.stream().filter(it -> StrUtil.isNotBlank(it.getBcheck()) && it.getBcheck().equals("1")).map(it -> it.getLineCode()).collect(Collectors.toList()));
                                dbSet.addAll(keys);
                                if (keys.size() == 0 || dbSet.size() == list.size()) {
                                    dbEntry.setBcheck("1").setBcheckTime(DateUtil.now()).setBcheckUser(userId);
                                    for (StockSaleousings stockWarehousings : list) {
                                        stockWarehousings.setBcheck("1").setBcheckTime(DateUtil.now()).setBcheckUser(userId);
                                    }
                                    return /*one.flatMap(e->*/saleousingRepository.save(dbEntry).flatMap(e1 -> saleousingsRepository.saveAll(list).collectList().thenReturn(""))/*)*/;
                                } else {
                                    for (StockSaleousings stockWarehousings : list) {
                                        if (keys.contains(stockWarehousings.getLineCode()))
                                            stockWarehousings.setBcheck("1").setBcheckTime(DateUtil.now()).setBcheckUser(userId);
                                    }
                                    return saleousingsRepository.saveAll(list).collectList().thenReturn(dbEntry);
                                }
                            } else {
                                dbEntry.setBcheck(null).setBcheckTime(null).setBcheckUser(null);
                                for (StockSaleousings stockWarehousings : list) {
                                    if (keys.size() == 0) {
                                        stockWarehousings.setBcheck(null).setBcheckTime(null).setBcheckUser(null);
                                    } else if (keys.contains(stockWarehousings.getLineCode())) {
                                        stockWarehousings.setBcheck(null).setBcheckTime(null).setBcheckUser(null);
                                    }
                                }
                                return /*one.flatMap(e->*/saleousingRepository.save(dbEntry).flatMap(o -> saleousingsRepository.saveAll(list).collectList().thenReturn(""))/*)*/;
                            }
                        })
        ).map(R::ok);
    }


    private Mono<Tuple3<StockSaleousing, List<StockSaleousings>, StockXyCsource>> portrayXsckd(StockSaleousing dbEntry, List<StockSaleousings> list,String code,List<StockCostAccRo> priceList) {
        StockSaleousing saleousing = new StockSaleousing();
        BeanUtils.copyProperties(dbEntry,saleousing);
        saleousing.setId(null).setCcode(code).setBillStyle("XSCKD").setBcheck(null).setBcheckTime(null).setBcheckUser(null).setCcodePl(null).setSourcetype("XHD").setSourcecode(dbEntry.getCcode()).setSourcedate(dbEntry.getDdate()).setBstyle("销售出库").setChukuStatus(null);
        if (dbEntry.getBdocumStyle().equals("1"))saleousing.setIsum(null).setTaxAmount(null);
        List<StockSaleousings> xsckd = new ArrayList<>();
        for (StockSaleousings s : list) {
            StockSaleousings a = new StockSaleousings();
            BeanUtils.copyProperties(s,a);
            s.setIsumChuku(a.getBaseQuantity()); // 存入累计数量
            a.setId(null).setCcode(code).setBillStyle("XSCKD").setBcheck(null).setBcheckTime(null).setBcheckUser(null).setTotalsourceid(null)
                    .setSourcedetailId(s.getLineCode()).setSourcetype("XHD").setSourcecode(s.getCcode()).setSourcedate(s.getDdate()).setBstyle("销售出库"); // 填写来源信息
            if (dbEntry.getBdocumStyle().equals("1"))a.setIsum(null).setTaxprice(null).setItaxprice(null);
            List<StockCostAccRo> accRos = priceList.stream().filter(p -> p.getStockCangku().equals(a.getCwhcode1()) && p.getStockNum().equals(a.getCinvode()) && (StrUtil.isBlank(a.getBatchId()) ? true : a.getBatchId().equals(p.getBatchId()))).collect(Collectors.toList());
            if (accRos.size() > 0 && StrUtil.isNotBlank(accRos.get(0).getPrice())){
                BigDecimal b = new BigDecimal(a.getQuantity());
                BigDecimal price = calculateTheExchangeRate(s,accRos.get(0).getPrice()).abs();
                BigDecimal e = b.multiply(price);
                a.setPrice(keepDecimals(price,10));
                a.setIcost(keepDecimals(e,4));
            }
            xsckd.add(a);
        }
        StockXyCsource xy = new StockXyCsource().setBillStyle("XHD").setCcode(dbEntry.getCcode()).setCcodeDate(dbEntry.getDdate()).setIyear(dbEntry.getIyear()).setXyBillStyle(saleousing.getBillStyle()).setXyccode(saleousing.getCcode()).setXyccodeDate(saleousing.getDdate());
       return Mono.zip(Mono.just(saleousing),Mono.just(xsckd),Mono.just(xy));
    }

    private BigDecimal calculateTheExchangeRate(StockSaleousings r, String price) {
        BigDecimal c = new BigDecimal(price).setScale(10, RoundingMode.HALF_UP); // 20
        // 主数量1瓶
        if (!r.getXsUnitId().equals(r.getCunitid())){ // 计量1  12组
            c = (c.multiply(new BigDecimal(r.getBaseQuantity()).divide(new BigDecimal(r.getQuantity()))));
        }
        return c.setScale(10, RoundingMode.HALF_UP);
    }

    @PostMapping("before")
    public Mono<R> before(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        String type = map.get("type").toString();
        String code = map.get("code").toString();
        if (type.equals("XHD")){// 检查出库单存不存在
            return  saleousingsRepository.countAllByBillStyleAndSourcecode("XSCKD",code).map(R::ok);
        }else {
            return  saleousingsRepository.countAllByBillStyleAndSourcecode("XSCKD",code).map(R::ok);
        }
    }

    @DeleteMapping
    @Transactional
    public Mono<R> del(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        return saleousingRepository.findById(map.get("id").toString()).flatMap(db -> saleousingsRepository.findAllByBillStyleAndCcodeOrderByDdateAscCcodeAscLineIdAsc(db.getBillStyle(), db.getCcode()).collectList().flatMap(list ->
                     saleousingsRepository.deleteAll(list).thenReturn("").flatMap(i -> saleousingRepository.delete(db).thenReturn("")).flatMap(s -> {
                        // 是否存在来源单据
                        boolean r = list.stream().filter(a -> StrUtil.isNotBlank(a.getSourcecode())).collect(Collectors.toList()).size() > 0;
                        Mono<String> xsckd = r ? stockXyCsourceRepository.findByXyBillStyleAndXyccodeAndIyear(db.getBillStyle(), db.getCcode(), db.getDdate().substring(0, 4)).flatMap(xd -> stockXyCsourceRepository.delete(xd).thenReturn("").flatMap(z -> saleousingRepository.findByCcode(xd.getCcode()).zipWith(saleousingsRepository.findAllByCcode(xd.getCcode()).collectList()).flatMap(tips -> {
                            StockSaleousing t1 = tips.getT1();
                            t1.setChukuStatus(null);
                            List<StockSaleousings> t2 = tips.getT2();
                            for (StockSaleousings saleousings : t2) {
                                List<StockSaleousings> stockSaleousings = list.stream().filter(i -> i.getSourcedetailId().equals(saleousings.getLineCode())).collect(Collectors.toList());
                                if (stockSaleousings.size() > 0) {
                                    BigDecimal lj = new BigDecimal(saleousings.getIsumChuku()).subtract(new BigDecimal(stockSaleousings.get(0).getBaseQuantity()));
                                    saleousings.setIsumChuku(keepDecimals(lj,10));
                                }
                            }
                            return saleousingRepository.save(t1).zipWith(saleousingsRepository.saveAll(t2).collectList()).flatMap(tips2 -> Mono.just(""));
                        }))) : Mono.just("");

                        Mono<String> xhd =  r?
                                ((saleousingRepository.findByCcode(list.get(0).getSourcecode()).zipWith(saleousingsRepository.findAllByCcode(list.get(0).getSourcecode()).collectList()).flatMap(tips -> {
                                    StockSaleousing t1 = tips.getT1();
                                    if (db.getBdocumStyle().equals("0")){
                                        t1.setXiaohuoStatus(null);
                                    }else{
                                        t1.setTuihuoStatus(null);
                                    }
                                    List<StockSaleousings> t2 = tips.getT2();
                                    for (StockSaleousings saleousings : t2) {
                                        List<StockSaleousings> stockSaleousings = list.stream().filter(i -> i.getSourcedetailId().equals(saleousings.getLineCode())).collect(Collectors.toList());
                                        if (stockSaleousings.size() > 0) {
                                            BigDecimal add = null;
                                            if (db.getBdocumStyle().equals("0")){
                                                add = new BigDecimal((StrUtil.isNotBlank(saleousings.getIsumXiaohuo()) ? saleousings.getIsumXiaohuo() : "0")).subtract(new BigDecimal(stockSaleousings.get(0).getBaseQuantity()));
                                                saleousings.setIsumXiaohuo(keepDecimals(add,10));
                                            }else {
                                                add = new BigDecimal((StrUtil.isNotBlank(saleousings.getIsumTuiHuo()) ? saleousings.getIsumTuiHuo() : "0")).subtract(new BigDecimal(stockSaleousings.get(0).getBaseQuantity()).abs());
                                                saleousings.setIsumTuiHuo(keepDecimals(add,10));
                                            }
                                        }
                                    }
                                    return saleousingRepository.save(t1).zipWith(saleousingsRepository.saveAll(t2).collectList()).flatMap(tips2 -> Mono.just(""));
                                })).zipWith(    // 存在下游删除下游
                                        stockXyCsourceRepository.findByXyBillStyleAndXyccodeAndIyear(list.get(0).getBillStyle(),list.get(0).getCcode(),list.get(0).getIyear()).defaultIfEmpty(new StockXyCsource()).flatMap(
                                                dbXy->null==dbXy.getId()?Mono.just(""):  stockXyCsourceRepository.delete(dbXy).thenReturn("")
                                        )
                                )).flatMap(
                                        tips4 -> (null != db.getBiandong() && db.getBiandong().equals("1"))?Mono.zip(saleousingBiandongRepository.findAllByCcodeLikeOrderByBaindongDateDesc("%"+db.getCcode()+"%").collectList(),
                                                        saleousingBiandongsRepository.findAllByCcodeLikeOrderByDdateAscCcodeAscLineIdAsc("%"+db.getCcode()+"%").collectList())
                                                .flatMap(tips3->(tips3.getT1().size() == 0?Mono.just(""):saleousingBiandongRepository.deleteAll(tips3.getT1()).thenReturn("")).zipWith((tips3.getT2().size() == 0?Mono.just(""):saleousingBiandongsRepository.deleteAll(tips3.getT2())).thenReturn("")))
                                                .flatMap(sss->Mono.just("")):Mono.just("")
                                )
                        :Mono.just("");

                         Mono<String> qtckd =  r && list.get(0).getSourcetype().equals("JCJYD")?
                                 ((saleousingRepository.findByCcode(list.get(0).getSourcecode()).zipWith(saleousingsRepository.findAllByCcode(list.get(0).getSourcecode()).collectList()).flatMap(tips -> {
                                     StockSaleousing t1 = tips.getT1();
                                     t1.setChukuStatus(null);
                                     List<StockSaleousings> t2 = tips.getT2();
                                     for (StockSaleousings saleousings : t2) {
                                         List<StockSaleousings> stockSaleousings = list.stream().filter(i -> i.getSourcedetailId().equals(saleousings.getLineCode())).collect(Collectors.toList());
                                         if (stockSaleousings.size() > 0) {
                                             BigDecimal add = new BigDecimal((StrUtil.isNotBlank(saleousings.getIsumChuku()) ? saleousings.getIsumChuku() : "0")).subtract(new BigDecimal(stockSaleousings.get(0).getBaseQuantity()));
                                             saleousings.setIsumChuku(keepDecimals(add,10));
                                         }
                                     }
                                     return saleousingRepository.save(t1).zipWith(saleousingsRepository.saveAll(t2).collectList()).flatMap(tips2 -> Mono.just(""));
                                 })).zipWith(    // 存在下游删除下游
                                         stockXyCsourceRepository.findByXyBillStyleAndXyccodeAndIyear(list.get(0).getBillStyle(),list.get(0).getCcode(),list.get(0).getIyear()).defaultIfEmpty(new StockXyCsource()).flatMap(
                                                 dbXy->null==dbXy.getId()?Mono.just(""):  stockXyCsourceRepository.delete(dbXy).thenReturn("")
                                         )
                                 )).flatMap(
                                         tips4 -> Mono.just("")
                                 )
                                 :Mono.just("");
                        Mono<String> qcxhd = Mono.just("");
                        return db.getBillStyle().equals("XSCKD") ? xsckd : db.getBillStyle().equals("XHD") ? xhd : db.getBillStyle().equals("QCXHD") ? qcxhd :db.getBillStyle().equals("QTCKD") ? qtckd : Mono.just("");
                    })
                ) //修改
        ).map(o -> R.ok());
    }

    @DeleteMapping("qrd")
    @Transactional
    public Mono<R> delQrd(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        return saleousingRepository.findById(map.get("id").toString()).flatMap(db ->
                saleousingsRepository.findAllByBillStyleAndCcodeOrderByDdateAscCcodeAscLineIdAsc(db.getBillStyle(), db.getCcode()).collectList()
                        .flatMap(list -> saleousingsRepository.deleteAll(list).thenReturn("").flatMap(i -> saleousingRepository.delete(db).thenReturn("")).flatMap(s->
                                stockXyCsourceRepository.findByXyBillStyleAndXyccodeAndIyear(db.getBillStyle(),db.getCcode(),db.getDdate().substring(0,4)).flatMap(xd->
                                stockXyCsourceRepository.delete(xd).thenReturn("").zipWith( saleousingRepository.findByCcode(xd.getCcode()).flatMap(dbOut -> {
                                    dbOut.setQuerenStatus(null);
                                    return saleousingRepository.save(dbOut).thenReturn("");
                                })).flatMap(it->Mono.just(it)))
                        )) //修改
        ).map(o -> R.ok());
    }

    @DeleteMapping("batch")
    @Transactional
    public Mono<R> dels(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        List<String> codes = JSON.parseArray(map.get("codes").toString(), String.class);
        String type = map.get("type").toString();
        return saleousingRepository.findByCcodeIn(codes).collectList().zipWith(saleousingsRepository.findAllByBillStyleAndLineCodeInOrderByDdateAscCcodeAscLineIdAsc(type, codes).collectList()).flatMap(tips->
             saleousingRepository.deleteAll(tips.getT1()).thenReturn("").zipWith(saleousingsRepository.deleteAll(tips.getT2()).thenReturn("")).flatMap(res->Mono.just(R.ok()))
        );
    }

    @PostMapping("lastDate")
    public Mono<R> lastDate(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        String year = map.get("year").toString();
        String type = map.get("type").toString();
        return saleousingRepository.findFirstByBillStyleAndIyearOrderByDdateDesc(type,year).defaultIfEmpty(new StockSaleousing()).map(o->R.ok(o.getDdate()));
    }

    @PostMapping("batchSelectorList")
    public Mono<R> batchSelectorList(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        String year = map.get("year").toString();
        List<String> codes = (List<String>) map.get("codes");
        return currentstockRepository.findAllByIyearAndInvCodeInOrderByDvdateDesc(year,codes).collectList().map(R::ok);
    }

    private Map<String, Object> assemblyCorrespondingRulesData(Map map) {
        String type = map.get("billStyle").toString();
        String bstyle = "01"; //map.get("bstyle").toString();
        HashMap<String, Object> resut = new HashMap<>();
        switch (type) {
            case ("PLXHD"):
                StockSaleousing warehousing = new StockSaleousing();
                String sgspperson = map.containsKey("sgspperson") ? map.get("sgspperson").toString() : null; // 质检人
                String bcheckUser = map.containsKey("bcheckUser") ? map.get("bcheckUser").toString() : null; // 审核人
                warehousing.setId(map.containsKey("id") &&  null != map.get("id")? map.get("id").toString() : null).setCcode(map.containsKey("ccode") ? map.get("ccode").toString() : null) //单号
                        .setDdate(map.containsKey("ddate") ? map.get("ddate").toString().substring(0, 10) : null) // 日期
                        .setIyear(map.containsKey("ddate") ? map.get("ddate").toString().substring(0, 4) : null).setCmaker(map.containsKey("cmaker") ? map.get("cmaker").toString() : null) // 制单人
                        .setCvencode(map.containsKey("cvencode") ? map.get("cvencode").toString() : null).setCvencodeJs(map.containsKey("cvencodeJs") ? map.get("cvencodeJs").toString() : null).setCwhcode(map.containsKey("cwhcode") ? map.get("cwhcode").toString() : null)  // 仓库
                        .setCdepcode(map.containsKey("cdepcode") ? map.get("cdepcode").toString() : null) // 部门
                        .setCpersoncode(map.containsKey("cpersoncode") ? map.get("cpersoncode").toString() : null) // 业务员
                        .setCmemo(map.containsKey("cmemo") ? map.get("cmemo").toString() : null).setBdocumStyle(map.containsKey("bdocumStyle") ? map.get("bdocumStyle").toString() : null).setBillStyle(type).setBstyle(bstyle).setDeliveryUser((map.containsKey("deliveryUser") ? map.get("deliveryUser").toString() : null))
                        .setPayDate((map.containsKey("payDate")&&null!=map.get("payDate") ? map.get("payDate").toString() : null))
                        .setMethodPay((map.containsKey("methodPay")&&null!=map.get("methodPay") ? map.get("methodPay").toString() : null))
                        .setTheDeposit((map.containsKey("theDeposit")&&null!=map.get("theDeposit") ? map.get("theDeposit").toString() : null))
                        .setBillCode((map.containsKey("billCode")&&null!=map.get("billCode") ? map.get("billCode").toString() : null))
                        .setBillNumber((map.containsKey("billNumber")&&null!=map.get("billNumber") ? map.get("billNumber").toString() : null))
                        .setBillDate((map.containsKey("billDate")&&null!=map.get("billDate") ? map.get("billDate").toString() : null))
                ; // 备注
                String entryList = map.containsKey("entryList") ? map.get("entryList").toString() : ""; // 表体
                List<StockSaleousings> entrys = JSON.parseArray(entryList, StockSaleousings.class);
                List<StockSaleousing> entrys2 = new ArrayList<>(entrys.size());
                BigDecimal squantitySum = new BigDecimal(0);
                BigDecimal squantity1Sum = new BigDecimal(0);
                BigDecimal squantity2Sum = new BigDecimal(0);
                BigDecimal icostSum = new BigDecimal(0);
                BigDecimal isumSum = new BigDecimal(0);
                BigDecimal taxAmountSum = new BigDecimal(0);
                String markerTime = null;
                for (StockSaleousings entry : entrys) {
                    String tempCode = StrUtil.uuid();
                    StockSaleousing saleousing = new StockSaleousing();
                    entry.setId(null).setIyear(warehousing.getIyear()).setBstyle(warehousing.getBstyle()).setDdate(warehousing.getDdate()).setTotalsourceid(warehousing.getCcode()).setCcode(tempCode).setCmaker(warehousing.getCmaker()).setCdepcode(warehousing.getCdepcode());
                    if (null != sgspperson) {
                        entry.setCgspstate("1");
                        entry.setSgspdate(DateUtil.today());
                        entry.setSgspperson(sgspperson);
                    }
                    BeanUtils.copyProperties(entry,saleousing);
                    saleousing.setCcodePl(entry.getTotalsourceid()).setBdocumStyle(warehousing.getBdocumStyle()).setCpersoncode(warehousing.getCpersoncode());
                    if (null == markerTime)
                        markerTime = StrUtil.isNotBlank(saleousing.getCmakerTime())?saleousing.getCmakerTime():DateUtil.now();
                    entry.setCmakerTime(markerTime);
                    BigDecimal b1 = new BigDecimal(entry.getBaseQuantity() == null ? "0" : entry.getBaseQuantity());
                    squantitySum = squantitySum.add(b1);
                    BigDecimal s1 = new BigDecimal(entry.getSubQuantity1() == null ? "0" : entry.getSubQuantity1());
                    squantity1Sum = squantity1Sum.add(s1);
                    BigDecimal s2 = new BigDecimal(entry.getSubQuantity2() == null ? "0" : entry.getSubQuantity2());
                    squantity2Sum = squantity2Sum.add(s2);
                    BigDecimal ic = new BigDecimal(entry.getIcost() == null ? "0" : entry.getIcost());
                    icostSum = icostSum.add(ic);
                    BigDecimal is = new BigDecimal(entry.getIsum() == null ? "0" : entry.getIsum());
                    isumSum = isumSum.add(is);
                    BigDecimal ta = new BigDecimal(entry.getItaxprice() == null ? "0" : entry.getItaxprice());
                    taxAmountSum = taxAmountSum.add(ta);
                    saleousing.setSquantity(keepDecimals(b1, 10)).setSquantity1(keepDecimals(s1, 10)).setSquantity2(keepDecimals(s2, 10)).setIcost(keepDecimals(ic, 4)).setIsum(keepDecimals(is, 4)).setTaxAmount(keepDecimals(ta, 6));
                    entrys2.add(saleousing);
                }
                warehousing.setCmakerTime(markerTime).setCvencode(entrys.stream().map(it->it.getCvencode()).distinct().collect(Collectors.toList()).toString()).setSquantity(keepDecimals(squantitySum, 10)).setSquantity1(keepDecimals(squantity1Sum, 10)).setSquantity2(keepDecimals(squantity2Sum, 10)).setIcost(keepDecimals(icostSum, 4)).setIsum(keepDecimals(isumSum, 4)).setTaxAmount(keepDecimals(taxAmountSum, 6));
                resut.put("batch", warehousing);
                resut.put("master", entrys2);
                resut.put("sub", entrys);
                break;
            case ("QCXHD"):
            case ("XHD"):
                StockSaleousing warehousing1 = new StockSaleousing();
                String sgspperson1 = map.containsKey("sgspperson") ? map.get("sgspperson").toString() : null; // 质检人
                String bcheckUser1 = map.containsKey("bcheckUser") ? map.get("bcheckUser").toString() : null; // 审核人
                warehousing1.setId(map.containsKey("id") && null != map.get("id")? map.get("id").toString() : null).setCcode(map.containsKey("ccode") ? map.get("ccode").toString() : null) //单号
                        .setDdate(map.containsKey("ddate") ? map.get("ddate").toString().substring(0, 10) : null) // 日期
                        .setIyear(map.containsKey("ddate") ? map.get("ddate").toString().substring(0, 4) : null).setCmaker(map.containsKey("cmaker") ? map.get("cmaker").toString() : null) // 制单人
                        .setCvencode(map.containsKey("cvencode") ? map.get("cvencode").toString() : null) // 供应商
                        .setCvencodeJs(map.containsKey("cvencodeJs") ? map.get("cvencodeJs").toString() : null) // 供应商
                        .setCwhcode(map.containsKey("cwhcode") ? map.get("cwhcode").toString() : null)  // 仓库
                        .setCdepcode(map.containsKey("cdepcode")&& null !=  map.get("cdepcode") ? map.get("cdepcode").toString() : null) // 部门
                        .setCpersoncode(map.containsKey("cpersoncode") && null !=  map.get("cpersoncode")?map.get("cpersoncode").toString() : null) // 业务员
                        .setCmemo(map.containsKey("cmemo") && null !=  map.get("cmemo") ? map.get("cmemo").toString() : null)
                        .setBdocumStyle(map.containsKey("bdocumStyle") ? map.get("bdocumStyle").toString() : null)
                        .setBillStyle(type).setBstyle(bstyle).setInvoiceStyle(map.containsKey("invoiceStyle") && null !=  map.get("invoiceStyle")?map.get("invoiceStyle").toString() : null)
                        .setDeliveryUser((map.containsKey("deliveryUser") ? map.get("deliveryUser").toString() : null))
                        .setPayDate((map.containsKey("payDate")&&null!=map.get("payDate") ? map.get("payDate").toString() : null))
                        .setMethodPay((map.containsKey("methodPay")&&null!=map.get("methodPay") ? map.get("methodPay").toString() : null))
                        .setTheDeposit((map.containsKey("theDeposit")&&null!=map.get("theDeposit") ? map.get("theDeposit").toString() : null))
                        .setBillCode((map.containsKey("billCode")&&null!=map.get("billCode") ? map.get("billCode").toString() : null))
                        .setBillNumber((map.containsKey("billNumber")&&null!=map.get("billNumber") ? map.get("billNumber").toString() : null))
                        .setBillDate((map.containsKey("billDate")&&null!=map.get("billDate") ? map.get("billDate").toString() : null)); // 备注
                if (map.keySet().toString().contains("cfree")){
                    for (int i = 1; i <= 12; i++) {
                        String key = "cfree"+i;
                        if (null != map.get(key)) ReflectUtil.setFieldValue(warehousing1,key,map.get(key).toString());
                    }
                }
                String entryList1 = map.containsKey("entryList") ? map.get("entryList").toString() : ""; // 表体
                List<StockSaleousings> entrys1 = JSON.parseArray(entryList1, StockSaleousings.class);
                BigDecimal squantitySum1 = new BigDecimal(0);
                BigDecimal squantity1Sum1 = new BigDecimal(0);
                BigDecimal squantity2Sum1 = new BigDecimal(0);
                BigDecimal icostSum1 = new BigDecimal(0);
                BigDecimal isumSum1 = new BigDecimal(0);
                BigDecimal taxAmountSum1 = new BigDecimal(0);
                String markerTime1 = null;
                for (StockSaleousings entry : entrys1) {
                    entry.setIyear(warehousing1.getIyear()).setCvencodeJs(warehousing1.getCvencodeJs()).setBillStyle(warehousing1.getBillStyle()).setBstyle(warehousing1.getBstyle()).setDdate(warehousing1.getDdate()).setCcode(warehousing1.getCcode()).setCvencode(warehousing1.getCvencode()).setCmaker(warehousing1.getCmaker()).setCdepcode(warehousing1.getCdepcode()).setBstyle(warehousing1.getBstyle());
                    if (null != sgspperson1) {
                        entry.setCgspstate("1");
                        entry.setSgspdate(DateUtil.today());
                        entry.setSgspperson(sgspperson1);
                    }
                    if (StrUtil.isNotBlank(entry.getSourcecode()) && StrUtil.isBlank(warehousing1.getSourcecode())){
                        warehousing1.setSourcecode(entry.getSourcecode()).setSourcetype(entry.getSourcetype()).setSourcedate(entry.getSourcedate());
                    }
                    if (null == markerTime1)
                        markerTime1 = StrUtil.isNotBlank(entry.getCmakerTime())?entry.getCmakerTime():DateUtil.now();
                    entry.setCmakerTime(markerTime1);
                    squantitySum1 = squantitySum1.add(new BigDecimal(entry.getBaseQuantity() == null ? "0" : entry.getBaseQuantity()));
                    squantity1Sum1 = squantity1Sum1.add(new BigDecimal(entry.getSubQuantity1() == null ? "0" : entry.getSubQuantity1()));
                    squantity2Sum1 = squantity2Sum1.add(new BigDecimal(entry.getSubQuantity2() == null ? "0" : entry.getSubQuantity2()));
                    icostSum1 = icostSum1.add(new BigDecimal(entry.getIcost() == null ? "0" : entry.getIcost()));
                    isumSum1 = isumSum1.add(new BigDecimal(entry.getIsum() == null ? "0" : entry.getIsum()));
                    taxAmountSum1 = taxAmountSum1.add(new BigDecimal(entry.getItaxprice() == null ? "0" : entry.getItaxprice()));
                }
                warehousing1.setCmakerTime(markerTime1).setSquantity(keepDecimals(squantitySum1, 10)).setSquantity1(keepDecimals(squantity1Sum1, 10)).setSquantity2(keepDecimals(squantity2Sum1, 10)).setIcost(keepDecimals(icostSum1, 4)).setIsum(keepDecimals(isumSum1, 4)).setTaxAmount(keepDecimals(taxAmountSum1, 6));
                resut.put("master", warehousing1);
                resut.put("sub", entrys1);
                break;
            case ("QTCKD"):
                StockSaleousing warehousing2 = new StockSaleousing();
                String sgspperson2 = map.containsKey("sgspperson") ? map.get("sgspperson").toString() : null; // 质检人
                String bcheckUser2 = map.containsKey("bcheckUser") ? map.get("bcheckUser").toString() : null; // 审核人
                String unitType = map.containsKey("unitType") ? map.get("unitType").toString() : null; // 审核人
                String unitValue = map.containsKey("unitValue") ? map.get("unitValue").toString() : null; // 审核人
                warehousing2.setId(map.containsKey("id") && null != map.get("id") ? map.get("id").toString() : null).setCcode(map.containsKey("ccode") ? map.get("ccode").toString() : null) //单号
                        .setDdate(map.containsKey("ddate") ? map.get("ddate").toString().substring(0, 10) : null) // 日期
                        .setIyear(map.containsKey("ddate") ? map.get("ddate").toString().substring(0, 4) : null).setCmaker(map.containsKey("cmaker") ? map.get("cmaker").toString() : null) // 制单人
                        .setCvencode(map.containsKey("cvencode") ? map.get("cvencode").toString() : null) // 供应商
                        .setCvencodeJs(map.containsKey("cvencodeJs") ? map.get("cvencodeJs").toString() : null) // 供应商
                        .setCwhcode(map.containsKey("cwhcode") ? map.get("cwhcode").toString() : null)  // 仓库
                        .setCdepcode(map.containsKey("cdepcode") ? map.get("cdepcode").toString() : null) // 部门
                        .setCpersoncode(map.containsKey("cpersoncode") ? map.get("cpersoncode").toString() : null) // 业务员
                        .setUnitType(unitType).setUnitValue(unitValue)
                        .setCmemo(map.containsKey("cmemo") ? map.get("cmemo").toString() : null).setBdocumStyle(map.containsKey("bdocumStyle") ? map.get("bdocumStyle").toString() : null).setBillStyle(type).setBstyle(type.equals("XSCKD")?"201":type.equals("QTCKD")?"202":null).setDeliveryUser((map.containsKey("deliveryUser") ? map.get("deliveryUser").toString() : null))
                        .setPayDate((map.containsKey("payDate")&&null!=map.get("payDate") ? map.get("payDate").toString() : null))
                        .setMethodPay((map.containsKey("methodPay")&&null!=map.get("methodPay") ? map.get("methodPay").toString() : null))
                        .setTheDeposit((map.containsKey("theDeposit")&&null!=map.get("theDeposit") ? map.get("theDeposit").toString() : null))
                        .setBillCode((map.containsKey("billCode")&&null!=map.get("billCode") ? map.get("billCode").toString() : null))
                        .setBillNumber((map.containsKey("billNumber")&&null!=map.get("billNumber") ? map.get("billNumber").toString() : null))
                        .setBillDate((map.containsKey("billDate")&&null!=map.get("billDate") ? map.get("billDate").toString() : null))
                ; // 备注
                if (map.keySet().toString().contains("cfree")){
                    for (int i = 1; i <= 12; i++) {
                        String key = "cfree"+i;
                        if (null != map.get(key)) ReflectUtil.setFieldValue(warehousing2,key,map.get(key).toString());
                    }
                }
                String entryList2 = map.containsKey("entryList") ? map.get("entryList").toString() : ""; // 表体
                List<StockSaleousings> entrys22 = JSON.parseArray(entryList2, StockSaleousings.class);
                BigDecimal squantitySum2 = new BigDecimal(0);
                BigDecimal squantity1Sum2 = new BigDecimal(0);
                BigDecimal squantity2Sum2 = new BigDecimal(0);
                BigDecimal icostSum2 = new BigDecimal(0);
                BigDecimal isumSum2 = new BigDecimal(0);
                BigDecimal taxAmountSum2 = new BigDecimal(0);
                String markerTime2 = null;
                for (StockSaleousings entry : entrys22) {
                    entry.setIyear(warehousing2.getIyear()).setCvencodeJs(warehousing2.getCvencodeJs()).setBillStyle(warehousing2.getBillStyle()).setBstyle(warehousing2.getBstyle()).setDdate(warehousing2.getDdate()).setCcode(warehousing2.getCcode()).setCvencode(warehousing2.getCvencode()).setCmaker(warehousing2.getCmaker()).setCdepcode(warehousing2.getCdepcode()).setBstyle(warehousing2.getBstyle());
                    if (null != sgspperson2) {
                        entry.setCgspstate("1");
                        entry.setSgspdate(DateUtil.today());
                        entry.setSgspperson(sgspperson2);
                    }
                    if (null == markerTime2)
                        markerTime2 = StrUtil.isNotBlank(entry.getCmakerTime())?entry.getCmakerTime():DateUtil.now();
                    entry.setCmakerTime(markerTime2);
                    squantitySum2 = squantitySum2.add(new BigDecimal(entry.getBaseQuantity() == null ? "0" : entry.getBaseQuantity()));
                    squantity1Sum2 = squantity1Sum2.add(new BigDecimal(entry.getSubQuantity1() == null ? "0" : entry.getSubQuantity1()));
                    squantity2Sum2 = squantity2Sum2.add(new BigDecimal(entry.getSubQuantity2() == null ? "0" : entry.getSubQuantity2()));
                    icostSum2 = icostSum2.add(new BigDecimal(entry.getIcost() == null ? "0" : entry.getIcost()));
                    isumSum2 = isumSum2.add(new BigDecimal(entry.getIsum() == null ? "0" : entry.getIsum()));
                    taxAmountSum2 = taxAmountSum2.add(new BigDecimal(entry.getItaxprice() == null ? "0" : entry.getItaxprice()));
                }
                warehousing2.setCmakerTime(markerTime2).setSquantity(keepDecimals(squantitySum2, 10)).setSquantity1(keepDecimals(squantity1Sum2, 10)).setSquantity2(keepDecimals(squantity2Sum2, 10)).setIcost(keepDecimals(icostSum2, 4)).setIsum(keepDecimals(isumSum2, 4)).setTaxAmount(keepDecimals(taxAmountSum2, 6));
                resut.put("master", warehousing2);
                resut.put("sub", entrys22);
                break;
            case ("XSDD"):
                StockSaleousing warehousing3 = new StockSaleousing();
                String sgspperson3 = map.containsKey("sgspperson") ? map.get("sgspperson").toString() : null; // 质检人
                String bcheckUser3 = map.containsKey("bcheckUser") ? map.get("bcheckUser").toString() : null; // 审核人
                warehousing3.setId(map.containsKey("id") && null != map.get("id")? map.get("id").toString() : null).setCcode(map.containsKey("ccode") ? map.get("ccode").toString() : null) //单号
                        .setDdate(map.containsKey("ddate") ? map.get("ddate").toString().substring(0, 10) : null) // 日期
                        .setIyear(map.containsKey("ddate") ? map.get("ddate").toString().substring(0, 4) : null).setCmaker(map.containsKey("cmaker") ? map.get("cmaker").toString() : null) // 制单人
                        .setCvencode(map.containsKey("cvencode") ? map.get("cvencode").toString() : null) // 供应商
                        .setCvencodeJs(map.containsKey("cvencodeJs") ? map.get("cvencodeJs").toString() : null) // 供应商
//                        .setCwhcode(map.containsKey("cwhcode") ? map.get("cwhcode").toString() : null)  // 仓库
                        .setCdepcode(map.containsKey("cdepcode") ? map.get("cdepcode").toString() : null) // 部门
                        .setCpersoncode(map.containsKey("cpersoncode") ? map.get("cpersoncode").toString() : null) // 业务员
                        .setCmemo(map.containsKey("cmemo") ? map.get("cmemo").toString() : null).setBdocumStyle(map.containsKey("bdocumStyle") ? map.get("bdocumStyle").toString() : null).setBillStyle(type).setBstyle(bstyle).setDeliveryUser((map.containsKey("deliveryUser") ? map.get("deliveryUser").toString() : null)); // 备注
                if (map.keySet().toString().contains("cfree")){
                    for (int i = 1; i <= 12; i++) {
                        String key = "cfree"+i;
                        if (null != map.get(key)) ReflectUtil.setFieldValue(warehousing3,key,map.get(key).toString());
                    }
                }
                String entryList3 = map.containsKey("entryList") ? map.get("entryList").toString() : ""; // 表体
                List<StockSaleousings> entrys3 = JSON.parseArray(entryList3, StockSaleousings.class);
                BigDecimal squantitySum3 = new BigDecimal(0);
                BigDecimal squantity1Sum3 = new BigDecimal(0);
                BigDecimal squantity2Sum3 = new BigDecimal(0);
                BigDecimal icostSum3 = new BigDecimal(0);
                BigDecimal isumSum3 = new BigDecimal(0);
                BigDecimal taxAmountSum3 = new BigDecimal(0);
                String markerTime3 = null;
                for (StockSaleousings entry : entrys3) {
                    entry.setIyear(warehousing3.getIyear()).setCvencodeJs(warehousing3.getCvencodeJs()).setBillStyle(warehousing3.getBillStyle()).setBstyle(warehousing3.getBstyle()).setDdate(warehousing3.getDdate()).setCcode(warehousing3.getCcode()).setCvencode(warehousing3.getCvencode()).setCmaker(warehousing3.getCmaker()).setCdepcode(warehousing3.getCdepcode()).setBstyle(warehousing3.getBstyle()).setCwhcode(null);
                    if (null != sgspperson3) {
                        entry.setCgspstate("1");
                        entry.setSgspdate(DateUtil.today());
                        entry.setSgspperson(sgspperson3);
                    }
                    if (null == markerTime3)
                        markerTime3 = StrUtil.isNotBlank(entry.getCmakerTime())?entry.getCmakerTime():DateUtil.now();
                    entry.setCmakerTime(markerTime3);
                    squantitySum3 = squantitySum3.add(new BigDecimal(entry.getBaseQuantity() == null ? "0" : entry.getBaseQuantity()));
                    squantity1Sum3 = squantity1Sum3.add(new BigDecimal(entry.getSubQuantity1() == null ? "0" : entry.getSubQuantity1()));
                    squantity2Sum3 = squantity2Sum3.add(new BigDecimal(entry.getSubQuantity2() == null ? "0" : entry.getSubQuantity2()));
                    icostSum3 = icostSum3.add(new BigDecimal(entry.getIcost() == null ? "0" : entry.getIcost()));
                    isumSum3 = isumSum3.add(new BigDecimal(entry.getIsum() == null ? "0" : entry.getIsum()));
                    taxAmountSum3 = taxAmountSum3.add(new BigDecimal(entry.getItaxprice() == null ? "0" : entry.getItaxprice()));
                }
                warehousing3.setCmakerTime(markerTime3).setSquantity(keepDecimals(squantitySum3, 10)).setSquantity1(keepDecimals(squantity1Sum3, 10)).setSquantity2(keepDecimals(squantity2Sum3, 10)).setIcost(keepDecimals(icostSum3, 4)).setIsum(keepDecimals(isumSum3, 4)).setTaxAmount(keepDecimals(taxAmountSum3, 6));
                resut.put("master", warehousing3);
                resut.put("sub", entrys3);
                break;
            case ("XSCKD"):
            case ("XSQRD"):
                StockSaleousing warehousing4 = new StockSaleousing();
                String sgspperson4 = map.containsKey("sgspperson") && null != map.get("sgspperson")  ? map.get("sgspperson").toString() : null; // 质检人
                String bcheckUser4 = map.containsKey("bcheckUser") && null != map.get("bcheckUser")  ? map.get("bcheckUser").toString() : null; // 审核人
                warehousing4.setId(map.containsKey("id") && null != map.get("id") ? map.get("id").toString() : null).setCcode(map.containsKey("ccode") ? map.get("ccode").toString() : null) //单号
                        .setDdate(map.containsKey("ddate") ? map.get("ddate").toString().substring(0, 10) : null) // 日期
                        .setIyear(map.containsKey("ddate") ? map.get("ddate").toString().substring(0, 4) : null).setCmaker(map.containsKey("cmaker") ? map.get("cmaker").toString() : null) // 制单人
                        .setCvencode(map.containsKey("cvencode") ? map.get("cvencode").toString() : null) // 供应商
                        .setCvencodeJs(map.containsKey("cvencodeJs") ? map.get("cvencodeJs").toString() : null) // 供应商
                        .setCwhcode(map.containsKey("cwhcode") ? map.get("cwhcode").toString() : null)  // 仓库
                        .setCdepcode(map.containsKey("cdepcode") && null != map.get("cdepcode") ? map.get("cdepcode").toString() : null) // 部门
                        .setCpersoncode(map.containsKey("cpersoncode") && null != map.get("cpersoncode")? map.get("cpersoncode").toString() : null) // 业务员
                        .setCmemo(map.containsKey("cmemo") && null != map.get("cmemo") ? map.get("cmemo").toString() : null).setBdocumStyle(map.containsKey("bdocumStyle") ? map.get("bdocumStyle").toString() : null).setBillStyle(type).setBstyle("1").setDeliveryUser((map.containsKey("deliveryUser") && null != map.get("deliveryUser") ? map.get("deliveryUser").toString() : null)).setCwhcodeUser((map.containsKey("cwhcodeUser") && null != map.get("cwhcodeUser") ? map.get("cwhcodeUser").toString() : null)); // 备注
                if (map.keySet().toString().contains("cfree")){
                    for (int i = 1; i <= 12; i++) {
                        String key = "cfree"+i;
                        if (null != map.get(key)) ReflectUtil.setFieldValue(warehousing4,key,map.get(key).toString());
                    }
                }
                String entryList4 = map.containsKey("entryList") ? map.get("entryList").toString() : ""; // 表体
                List<StockSaleousings> entrys4 = JSON.parseArray(entryList4, StockSaleousings.class);
                BigDecimal squantitySum4 = new BigDecimal(0);
                BigDecimal squantity1Sum4 = new BigDecimal(0);
                BigDecimal squantity2Sum4 = new BigDecimal(0);
                String markerTime4 = null;
                for (StockSaleousings entry : entrys4) {
                    entry.setIyear(warehousing4.getIyear()).setCvencodeJs(warehousing4.getCvencodeJs()).setBillStyle(warehousing4.getBillStyle()).setBstyle(warehousing4.getBstyle()).setDdate(warehousing4.getDdate()).setCcode(warehousing4.getCcode()).setCvencode(warehousing4.getCvencode()).setCmaker(warehousing4.getCmaker()).setCdepcode(warehousing4.getCdepcode()).setBstyle(warehousing4.getBstyle());
                    if (null != sgspperson4) {
                        entry.setCgspstate("1");
                        entry.setSgspdate(DateUtil.today());
                        entry.setSgspperson(sgspperson4);
                    }
                    if (StrUtil.isNotBlank(entry.getSourcecode()) && StrUtil.isBlank(warehousing4.getSourcecode())){
                        warehousing4.setSourcecode(entry.getSourcecode()).setSourcetype(entry.getSourcetype()).setSourcedate(entry.getSourcedate());
                    }
                    if (null == markerTime4)
                        markerTime4 = StrUtil.isNotBlank(entry.getCmakerTime())?entry.getCmakerTime():DateUtil.now();
                    entry.setCmakerTime(markerTime4);
                    if (StrUtil.isNotBlank(entry.getSourcecode()) && warehousing4.getBdocumStyle().equals("0") && warehousing4.getId() == null)
                        entry.setIsum(null).setIcost(null).setPrice(null).setItaxrate(null).setItaxprice(null).setTaxprice(null);
                    squantitySum4 = squantitySum4.add(new BigDecimal(entry.getBaseQuantity() == null ? "0" : entry.getBaseQuantity()));
                    squantity1Sum4 = squantity1Sum4.add(new BigDecimal(entry.getSubQuantity1() == null ? "0" : entry.getSubQuantity1()));
                    squantity2Sum4 = squantity2Sum4.add(new BigDecimal(entry.getSubQuantity2() == null ? "0" : entry.getSubQuantity2()));
                }
                warehousing4.setCmakerTime(markerTime4).setSquantity(keepDecimals(squantitySum4, 10)).setSquantity1(keepDecimals(squantity1Sum4, 10)).setSquantity2(keepDecimals(squantity2Sum4, 10));
                resut.put("master", warehousing4);
                resut.put("sub", entrys4);
                break;
            case ("CKTZD"):
                StockSaleousing warehousing5 = new StockSaleousing();
                String sgspperson5 = map.containsKey("sgspperson") ? map.get("sgspperson").toString() : null; // 质检人
                String bcheckUser5 = map.containsKey("bcheckUser") ? map.get("bcheckUser").toString() : null; // 审核人
                warehousing5.setId(map.containsKey("id") && null != map.get("id")? map.get("id").toString() : null).setCcode(map.containsKey("ccode") ? map.get("ccode").toString() : null) //单号
                        .setDdate(map.containsKey("ddate") ? map.get("ddate").toString().substring(0, 10) : null) // 日期
                        .setIyear(map.containsKey("ddate") ? map.get("ddate").toString().substring(0, 4) : null).setCmaker(map.containsKey("cmaker") ? map.get("cmaker").toString() : null) // 制单人
                        .setCvencode(map.containsKey("cvencode") ? map.get("cvencode").toString() : null) // 供应商
                        .setCvencodeJs(map.containsKey("cvencodeJs") ? map.get("cvencodeJs").toString() : null) // 供应商
                        .setCwhcode(map.containsKey("cwhcode") ? map.get("cwhcode").toString() : null)  // 仓库
                        .setCdepcode(map.containsKey("cdepcode")&& null !=  map.get("cdepcode") ? map.get("cdepcode").toString() : null) // 部门
                        .setCpersoncode(map.containsKey("cpersoncode") && null !=  map.get("cpersoncode")?map.get("cpersoncode").toString() : null) // 业务员
                        .setCmemo(map.containsKey("cmemo") && null !=  map.get("cmemo") ? map.get("cmemo").toString() : null)
                        .setBdocumStyle(map.containsKey("bdocumStyle") ? map.get("bdocumStyle").toString() : null)
                        .setCmakerTime(LocalDateTime.now().toString())
                        .setBillStyle(type).setBstyle(bstyle).setInvoiceStyle(map.containsKey("invoiceStyle") && null !=  map.get("invoiceStyle")?map.get("invoiceStyle").toString() : null)
                        .setDeliveryUser((map.containsKey("deliveryUser") ? map.get("deliveryUser").toString() : null)).setPayDate((map.containsKey("payDate")&&null!=map.get("payDate") ? map.get("payDate").toString() : null)); // 备注
                if (map.keySet().toString().contains("cfree")){
                    for (int i = 1; i <= 12; i++) {
                        String key = "cfree"+i;
                        if (null != map.get(key)) ReflectUtil.setFieldValue(warehousing5,key,map.get(key).toString());
                    }
                }
                String entryList5 = map.containsKey("entryList") ? map.get("entryList").toString() : ""; // 表体
                List<StockSaleousings> entrys5 = JSON.parseArray(entryList5, StockSaleousings.class);
                BigDecimal squantitySum5 = new BigDecimal(0);
                BigDecimal squantity1Sum5 = new BigDecimal(0);
                BigDecimal squantity2Sum5 = new BigDecimal(0);
                BigDecimal icostSum5 = new BigDecimal(0);
                BigDecimal isumSum5 = new BigDecimal(0);
                BigDecimal taxAmountSum5 = new BigDecimal(0);
                String markerTime5 = null;
                for (StockSaleousings entry : entrys5) {
                    entry.setIyear(warehousing5.getIyear()).setCvencodeJs(warehousing5.getCvencodeJs()).setBillStyle(warehousing5.getBillStyle()).setBstyle(warehousing5.getBstyle()).setDdate(warehousing5.getDdate()).setCcode(warehousing5.getCcode()).setCvencode(warehousing5.getCvencode()).setCmaker(warehousing5.getCmaker())
                            .setBaseQuantity("0.00").setIcost(entry.getThicost().toString())
                            .setCdepcode(warehousing5.getCdepcode()).setBstyle(warehousing5.getBstyle());
                    if (null != sgspperson5) {
                        entry.setCgspstate("1");
                        entry.setSgspdate(DateUtil.today());
                        entry.setSgspperson(sgspperson5);
                    }
                    if (StrUtil.isNotBlank(entry.getSourcecode()) && StrUtil.isBlank(warehousing5.getSourcecode())){
                        warehousing5.setSourcecode(entry.getSourcecode()).setSourcetype(entry.getSourcetype()).setSourcedate(entry.getSourcedate());
                    }
                    if (null == markerTime5)
                        markerTime5 = StrUtil.isNotBlank(entry.getCmakerTime())?entry.getCmakerTime():DateUtil.now();
                    entry.setCmakerTime(markerTime5);
                    squantitySum5 = squantitySum5.add(new BigDecimal(entry.getBaseQuantity() == null ? "0" : entry.getBaseQuantity()));
                    squantity1Sum5 = squantity1Sum5.add(new BigDecimal(entry.getSubQuantity1() == null ? "0" : entry.getSubQuantity1()));
                    squantity2Sum5 = squantity2Sum5.add(new BigDecimal(entry.getSubQuantity2() == null ? "0" : entry.getSubQuantity2()));
                    icostSum5 = icostSum5.add(new BigDecimal(entry.getIcost() == null ? "0" : entry.getIcost()));
                    isumSum5 = isumSum5.add(new BigDecimal(entry.getIsum() == null ? "0" : entry.getIsum()));
                    taxAmountSum5 = taxAmountSum5.add(new BigDecimal(entry.getItaxprice() == null ? "0" : entry.getItaxprice()));
                }
                warehousing5.setSquantity(keepDecimals(BigDecimal.ZERO, 10)).setSquantity1(keepDecimals(BigDecimal.ZERO, 10)).setSquantity2(keepDecimals(BigDecimal.ZERO, 10)).setIcost(keepDecimals(BigDecimal.ZERO, 4)).setIsum(keepDecimals(BigDecimal.ZERO, 4)).setTaxAmount(keepDecimals(BigDecimal.ZERO, 6));
                resut.put("master", warehousing5);
                resut.put("sub", entrys5);
                break;
            default:
                StockSaleousing warehousingD = new StockSaleousing();
                String sgsppersonD = map.containsKey("sgspperson") ? map.get("sgspperson").toString() : null; // 质检人
                warehousingD.setId(map.containsKey("id") && null != map.get("id")? map.get("id").toString() : null)
                        .setCcode(map.containsKey("ccode") ? map.get("ccode").toString() : null) //单号
                        .setDdate(map.containsKey("ddate") ? map.get("ddate").toString().substring(0, 10) : null) // 日期
                        .setIyear(map.containsKey("ddate") ? map.get("ddate").toString().substring(0, 4) : null)
                        .setCmaker(map.containsKey("cmaker") ? map.get("cmaker").toString() : null) // 制单人
                        .setCvencode(map.containsKey("cvencode") ? map.get("cvencode").toString() : null) // 供应商
                        .setCvencodeJs(map.containsKey("cvencodeJs") ? map.get("cvencodeJs").toString() : null) // 供应商
                        .setCwhcode(map.containsKey("cwhcode") ? map.get("cwhcode").toString() : null)  // 仓库
                        .setCdepcode(map.containsKey("cdepcode")&& null !=  map.get("cdepcode") ? map.get("cdepcode").toString() : null) // 部门
                        .setCpersoncode(map.containsKey("cpersoncode") && null !=  map.get("cpersoncode")?map.get("cpersoncode").toString() : null) // 业务员
                        .setCmemo(map.containsKey("cmemo") && null !=  map.get("cmemo") ? map.get("cmemo").toString() : null)
                        .setBdocumStyle(map.containsKey("bdocumStyle") ? map.get("bdocumStyle").toString() : null)
                        .setBillStyle(type).setBstyle(bstyle).setInvoiceStyle(map.containsKey("invoiceStyle") && null !=  map.get("invoiceStyle")?map.get("invoiceStyle").toString() : null)
                        .setDeliveryUser((map.containsKey("deliveryUser") ? map.get("deliveryUser").toString() : null))
                        .setPayDate((map.containsKey("payDate")&&null!=map.get("payDate") ? map.get("payDate").toString() : null))
                        .setMethodPay((map.containsKey("methodPay")&&null!=map.get("methodPay") ? map.get("methodPay").toString() : null))
                        .setTheDeposit((map.containsKey("theDeposit")&&null!=map.get("theDeposit") ? map.get("theDeposit").toString() : null))
                        .setBillCode((map.containsKey("billCode")&&null!=map.get("billCode") ? map.get("billCode").toString() : null))
                        .setBillNumber((map.containsKey("billNumber")&&null!=map.get("billNumber") ? map.get("billNumber").toString() : null))
                        .setBillDate((map.containsKey("billDate")&&null!=map.get("billDate") ? map.get("billDate").toString() : null))
                ;
                if (map.keySet().toString().contains("cfree")){
                    for (int i = 1; i <= 12; i++) {
                        String key = "cfree"+i;
                        if (null != map.get(key)) ReflectUtil.setFieldValue(warehousingD,key,map.get(key).toString());
                    }
                }
                List<StockSaleousings> entrysD = JSON.parseArray(map.containsKey("entryList") && null != map.get("entryList") ? map.get("entryList").toString() : "", StockSaleousings.class);
                BigDecimal squantitySumD = new BigDecimal(0);
                BigDecimal squantity1SumD = new BigDecimal(0);
                BigDecimal squantity2SumD = new BigDecimal(0);
                BigDecimal icostSumD = new BigDecimal(0);
                BigDecimal isumSumD = new BigDecimal(0);
                BigDecimal taxAmountSumD = new BigDecimal(0);
                String markerTimeD=null;
                for (StockSaleousings entry : entrysD) {
                    if (null != sgsppersonD) {
                        entry.setCgspstate("1");
                        entry.setSgspdate(DateUtil.today());
                        entry.setSgspperson(sgsppersonD);
                    }
                    entry.setIyear(warehousingD.getIyear()).setCvencodeJs(warehousingD.getCvencodeJs()).setBillStyle(warehousingD.getBillStyle()).setBstyle(warehousingD.getBstyle()).setDdate(warehousingD.getDdate()).setCcode(warehousingD.getCcode()).setCvencode(warehousingD.getCvencode()).setCmaker(warehousingD.getCmaker()).setCdepcode(warehousingD.getCdepcode()).setBstyle(warehousingD.getBstyle());
                    if (StrUtil.isNotBlank(entry.getSourcecode()) && StrUtil.isBlank(warehousingD.getSourcecode())){
                        warehousingD.setSourcecode(entry.getSourcecode()).setSourcetype(entry.getSourcetype()).setSourcedate(entry.getSourcedate());
                    }
                    if (null == markerTimeD)
                        markerTimeD = StrUtil.isNotBlank(entry.getCmakerTime())?entry.getCmakerTime():DateUtil.now();
                    entry.setCmakerTime(markerTimeD);
                    squantitySumD = squantitySumD.add(new BigDecimal(entry.getBaseQuantity() == null ? "0" : entry.getBaseQuantity()));
                    squantity1SumD = squantity1SumD.add(new BigDecimal(entry.getSubQuantity1() == null ? "0" : entry.getSubQuantity1()));
                    squantity2SumD = squantity2SumD.add(new BigDecimal(entry.getSubQuantity2() == null ? "0" : entry.getSubQuantity2()));
                    icostSumD = icostSumD.add(new BigDecimal(entry.getIcost() == null ? "0" : entry.getIcost()));
                    isumSumD = isumSumD.add(new BigDecimal(entry.getIsum() == null ? "0" : entry.getIsum()));
                    taxAmountSumD = taxAmountSumD.add(new BigDecimal(entry.getItaxprice() == null ? "0" : entry.getItaxprice()));
                }
                warehousingD.setCmakerTime(markerTimeD).setSquantity(keepDecimals(squantitySumD, 10)).setSquantity1(keepDecimals(squantity1SumD, 10)).setSquantity2(keepDecimals(squantity2SumD, 10)).setIcost(keepDecimals(icostSumD, 4)).setIsum(keepDecimals(isumSumD, 4)).setTaxAmount(keepDecimals(taxAmountSumD, 6));
                resut.put("master", warehousingD);
                resut.put("sub", entrysD);
                break;
        }
        return resut;
    }

    private String keepDecimals(BigDecimal b, int len) {
        StringBuilder s = new StringBuilder("");
        for (int i = 0; i < len; i++) s.append("0");
        DecimalFormat decimalFormat = new DecimalFormat("0." + s + "#");
        BigDecimal value = b.setScale(len, RoundingMode.HALF_UP);
        return decimalFormat.format(value);
    }

    @PostMapping("findOutByTypeList")
    public Mono<R> findOutByTypeList(@RequestBody Map map) {
        String type = map.get("type").toString();
        String mark = map.get("queryMark").toString();
        String doc = map.containsKey("documentType")?map.get("documentType").toString():"";
        Map<String,String> query = (Map<String, String>) map.get("query");
        String start = "";
        String end = "";
        if (StrUtil.isNotBlank(query.get("periodStart"))){
            if (query.get("periodStart").toString().split("-").length>0){
                start = query.get("periodStart")+"-01";
                end = query.get("periodEnd")+"-31";
            }else {
                start = query.get("periodStart").substring(0,4)+"-"+query.get("periodStart").substring(4,6)+"-01";
                end = query.get("periodEnd").substring(0,4)+"-"+query.get("periodEnd").substring(4,6)+"-31";
            }
        }else {
            start = query.get("dateStart");
            end = query.get("dateEnd");
        }
        return saleousingsRepository.findAllByBillStyleAndDdateBetweenOrderByDdateAscCcodeAscLineIdAsc(type,start,end).collectList().zipWith(saleousingRepository.findAllByBillStyleAndDdateBetweenOrderByDdateAscCcodeAsc(type,start,end).collectList())
                .map(o -> R.ok().setResult(queryFilter(o,query,mark,doc)));
    }


    @PostMapping("findTongJi")
    public Mono<R> findTongJi(@RequestBody Map map) {
        String mark = map.get("queryMark").toString();
        String sortMark = map.containsKey("sortMark")?map.get("sortMark").toString():"";
        String type = map.get("type").toString();
        Map<String, String> query = (Map<String, String>) map.get("query");
        String start = "";
        String end = "";
        if (StrUtil.isNotBlank(query.get("periodStart"))) {
            start = query.get("periodStart").substring(0, 4) + "-" + query.get("periodStart").substring(4, 6) + "-01";
            end = query.get("periodEnd").substring(0, 4) + "-" + query.get("periodEnd").substring(4, 6) + "-31";
        } else {
            start = query.get("dateStart");
            end = query.get("dateEnd");
        }
        String xsckCheck = query.get("xsckCheck").toString();
        String bcheck = query.get("bcheck").toString();
        Mono<List<Stock>> one = stockRepository.findAll().collectList();
        // 拿到客户信息
        Mono<List<Customer>> two = customerRepository.findAllByFlag("1").collectList();
        Mono<Tuple2<List<StockSaleousings>, List<StockSaleousing>>> xhd = saleousingsRepository.findAllByBillStyleAndDdateBetweenOrderByDdateAscCcodeAscLineIdAsc(type, start, end)
                .filter(it-> StrUtil.isBlank(bcheck)?true: (bcheck.equals("1")?null != it.getBcheck() &&  it.getBcheck().equals("1"): (null == it.getBcheck() || !it.getBcheck().equals("1"))))
                .filter(it -> Double.parseDouble(null == it.getBaseQuantity() ? "0" : it.getBaseQuantity()) > 0).collectList()
                .flatMap(list -> saleousingRepository.findByCcodeIn(list.stream().map(it -> it.getCcode()).distinct().collect(Collectors.toList())).collectList().flatMap(list2 -> Mono.zip(Mono.just(list), Mono.just(list2))));
        // 拿到存货信息
        // 出库单
        Mono<List<StockSaleousings>> ckd = mark.equals("1")?saleousingsRepository.findAllByBillStyleAndDdateBetweenOrderByDdateAscCcodeAscLineIdAsc("XSCKD",  start, end)
                .filter(it->  xsckCheck.equals("1")?null != it.getBcheck() &&  it.getBcheck().equals("1"):true)
                .collectList():Mono.just(new ArrayList<>());
        // 退货单
        Mono<List<StockSaleousings>> thd = mark.equals("1")?saleousingsRepository.findAllByBillStyleAndDdateBetweenOrderByDdateAscCcodeAscLineIdAsc("XHD",  start, end)
                .filter(it-> StrUtil.isBlank(bcheck)?true:( bcheck.equals("1")?null != it.getBcheck() &&  it.getBcheck().equals("1"): (null == it.getBcheck() || !it.getBcheck().equals("1"))))
                .filter(it->Double.parseDouble(null == it.getBaseQuantity() ? "0" : it.getBaseQuantity()) < 0).collectList():Mono.just(new ArrayList<>());
        // 发票
        Mono<List<StockSaleousings>> xsfp = mark.equals("1")?saleousingsRepository.findAllByBillStyleAndDdateBetweenOrderByDdateAscCcodeAscLineIdAsc("XSFP",  start, end)
                .filter(it-> StrUtil.isBlank(bcheck)?true: (bcheck.equals("1")?null != it.getBcheck() &&  it.getBcheck().equals("1"): (null == it.getBcheck() || !it.getBcheck().equals("1")))).collectList():Mono.just(new ArrayList<>());

        return Mono.zip(one,two, xhd,ckd,thd,xsfp).map(o -> R.ok().setResult(queryFilterTingJi(o, query, mark,sortMark)));
    }

    @PostMapping("findMingXi")
    public Mono<R> findMingXi(@RequestBody Map map) {
        String mark = map.get("queryMark").toString();
        String type = map.get("type").toString();
        Map<String, String> query = (Map<String, String>) map.get("query");
        String start = "";
        String end = "";
        if (StrUtil.isNotBlank(query.get("periodStart"))) {
            start = query.get("periodStart").substring(0, 4) + "-" + query.get("periodStart").substring(4, 6) + "-01";
            end = query.get("periodEnd").substring(0, 4) + "-" + query.get("periodEnd").substring(4, 6) + "-31";
        } else {
            start = query.get("dateStart");
            end = query.get("dateEnd");
        }
        Mono<List<StockSaleousings>> one = saleousingsRepository.findAllByBillStyleAndBcheckAndDdateBetweenOrderByDdateAscCcodeAscLineIdAsc(type, "1", start, end).collectList();
        Mono<List<StockSaleousing>> two = saleousingRepository.findAllByBillStyleAndBcheckAndDdateBetweenOrderByDdateAscCcodeAsc(type, "1", start, end).collectList();
        // 拿到存货信息
        Mono<List<Stock>> three = stockRepository.findAll().collectList();
        // 拿到客户信息
        Mono<List<Customer>> four = customerRepository.findAllByFlag("1").collectList();
        return Mono.zip(one, two, three, four).map(o -> R.ok().setResult(queryFilterMingXi(o, query, mark)));
    }

    private Object queryFilterTingJi(Tuple6<List<Stock>, List<Customer>,Tuple2<List<StockSaleousings>,List<StockSaleousing>>,List<StockSaleousings>,List<StockSaleousings>,List<StockSaleousings>> o, Map<String, String> query, String mark, String sortMark) {
        String ccodeMin = !query.containsKey("ccodeMin") ? "" : query.get("ccodeMin").trim();
        String ccodeMax = !query.containsKey("ccodeMax") ? "" : query.get("ccodeMax").trim();
        String bdocumStyle = !query.containsKey("bdocumStyle") ? "" : query.get("bdocumStyle").trim();
        String cvencode = !query.containsKey("cvencode") ? "" : query.get("cvencode").trim();
        String cvencodeJs = !query.containsKey("cvencodeJs") ? "" : query.get("cvencodeJs").trim();
        String cinvode = !query.containsKey("cinvode") ? "" : query.get("cinvode").trim();
        String cwhcode = !query.containsKey("cwhcode") ? "" : query.get("cwhcode").trim();
        String cpersoncode = !query.containsKey("cpersoncode") ? "" : query.get("cpersoncode").trim();
        String cinvodeClass = !query.containsKey("cinvodeClass") ? "" : query.get("cinvodeClass").trim();
        String cdepcode = !query.containsKey("cdepcode") ? "" : query.get("cdepcode").trim();
        String region = !query.containsKey("region") ? "" : query.get("region").trim();
        String country = !query.containsKey("country") ? "" : query.get("country").trim();
        String wuliuUser = !query.containsKey("wuliuUser") ? "" : query.get("wuliuUser").trim();
        String stockGgxh = !query.containsKey("stockGgxh") ? "" : query.get("stockGgxh").trim();
        String isGive = !query.containsKey("isGive") ? "" : query.get("isGive").trim();
        Tuple2<List<StockSaleousings>, List<StockSaleousing>> e = o.getT3();
        Set<String> chSets = null;
        if (mark.equals("1") && StrUtil.isBlank(cinvode) && (StrUtil.isNotBlank(stockGgxh) || StrUtil.isNotBlank(cinvodeClass))) {
            List<Stock> stocks = o.getT1().stream().filter(it ->
                            (StrUtil.isNotBlank(stockGgxh) && StrUtil.isNotBlank(cinvodeClass))?(it.getStockGgxh().equals(stockGgxh) && it.getStockClass().equals(cinvodeClass)): ( StrUtil.isNotBlank(stockGgxh) ? it.getStockGgxh().equals(stockGgxh):it.getStockClass().equals(cinvodeClass))
                    ).collect(Collectors.toList());
            chSets = new HashSet<>(stocks.stream().map(it -> it.getStockNum()).collect(Collectors.toList()));
        }
        Set<String> finalChSets = chSets; // 过滤 存货
        List<StockSaleousings> filterList = e.getT1().stream().filter(entiry -> {
            if (StrUtil.isNotBlank(ccodeMin) && StrUtil.isNotBlank(ccodeMax)) { // 编码过滤
                String ccode = entiry.getCcode().substring(entiry.getCcode().length() - 4);
                if (!NumberUtil.isNumber(ccode) || Integer.parseInt(ccodeMin) > Integer.parseInt(ccode) && Integer.parseInt(ccodeMax) < Integer.parseInt(ccode))
                    return false;
            }
            StockSaleousing saleousing = e.getT2().stream().filter(it -> it.getCcode().equals(entiry.getCcode())).collect(Collectors.toList()).get(0);
            // 红篮字过滤 暂时启用
            if (StrUtil.isNotBlank(bdocumStyle) && !saleousing.getBdocumStyle().equals(bdocumStyle)) return false; // 蓝字 或 红字
            entiry.setBcheck(saleousing.getBdocumStyle()); // 临时存储类型
            entiry.setBcheckUser(saleousing.getCpersoncode()); // 临时存储业务员
            if (!mark.equals("1") && StrUtil.isNotBlank(isGive) && !entiry.getIsGive().equals(isGive)) return false;
            if (mark.equals("1")){
                if (StrUtil.isNotBlank(cinvode) ){ // 存货不匹费
                    List<Stock> stocks = o.getT1().stream().filter(it -> it.getStockNum().equals(entiry.getCinvode())).collect(Collectors.toList());
                    if (!stocks.get(0).getId().equals(cinvode))return false;
                }
                // 规格型号 或 存货分类不匹配
                if (null != finalChSets &&  !finalChSets.contains(entiry.getCinvode())) return false; // 不包含说明不匹配
                if (StrUtil.isNotBlank(cwhcode) && !entiry.getCwhcode1().equals(cwhcode)) return false; // 仓库
            }else if(mark.equals("2")){
                // 客户 不匹配
                if (StrUtil.isNotBlank(cvencode) && !entiry.getCvencode().equals(cvencode)) return false;
                if ( StrUtil.isNotBlank(cvencodeJs) && !entiry.getCvencodeJs().equals(cvencodeJs)) return false;
            }else if(mark.equals("3")){
                if (StrUtil.isNotBlank(cdepcode) && !saleousing.getCdepcode().equals(cdepcode)) return false;
                if (StrUtil.isNotBlank(cpersoncode) && !saleousing.getCpersoncode().equals(cpersoncode)) return false;
            }else if(mark.equals("4")){
                entiry.setBcheckTime(null);
                List<Customer> collect = o.getT2().stream().filter(it -> it.getId().equals(entiry.getCvencode())).collect(Collectors.toList());
                if (collect.size() == 0 || (StrUtil.isNotBlank(collect.get(0).getArea()) && !collect.get(0).getArea().equals(country))) //地区不能为空
                    return false;
                if (collect.size() == 0 || StrUtil.isBlank(collect.get(0).getZone()) || !collect.get(0).getZone().contains(region)) // 省市不匹配
                    return false;
                if (StrUtil.isNotBlank(wuliuUser) &&  (null == saleousing.getDeliveryUser() || !saleousing.getDeliveryUser().equals(wuliuUser))) return false;
                entiry.setBcheckTime(collect.get(0).getArea()); // 临时存储地区
            }
            return true;
        }).collect(Collectors.toList());

        Set<String> strings = new HashSet<>(filterList.stream().map(i -> mark.equals("1") ? i.getCinvode() : mark.equals("2") ? (StrUtil.isNotBlank(cvencode) ? i.getCvencode() : i.getCvencodeJs()) : mark.equals("3") ? i.getBcheckUser() : i.getBcheckTime()).collect(Collectors.toList()));
        List<StockSaleousings> list = new ArrayList<>();
        for (String str : strings) {
            if (null == str) continue;
            BigDecimal num = new BigDecimal(0);
            BigDecimal cos = new BigDecimal(0);
            BigDecimal se = new BigDecimal(0);
            BigDecimal sum = new BigDecimal(0);
            BigDecimal ck = new BigDecimal(0);
            BigDecimal th = new BigDecimal(0);
            BigDecimal kp = new BigDecimal(0);
            BigDecimal js = new BigDecimal(0);

            List<StockSaleousings> collect = filterList.stream().filter(it -> mark.equals("1") ? it.getCinvode().equals(str) : mark.equals("2") ? (StrUtil.isNotBlank(cvencode) ? it.getCvencode().equals(str) : it.getCvencodeJs().equals(str)) : mark.equals("3") ? it.getBcheckUser().equals(str) : it.getBcheckTime().equals(str)).collect(Collectors.toList());
            StockSaleousings saleousings = collect.get(0);
            Set<String> setCodes = new HashSet<>(collect.stream().map(it -> it.getCcode()).collect(Collectors.toList()));
            for (StockSaleousings so : collect) {
                StockSaleousing saleousing = e.getT2().stream().filter(it -> it.getCcode().equals(so.getCcode())).collect(Collectors.toList()).get(0);
                BigDecimal hxB = new BigDecimal("0");
                if (setCodes.contains(so.getCcode())) { // 一个单据只能取一次
                    hxB = new BigDecimal(StrUtil.isBlank(saleousing.getHxIsum()) ? "0" : so.getHxIsum()).abs();
                    setCodes.remove(so.getCcode());
                }
                if (so.getBcheck().equals("0")) {
                    num = num.add(new BigDecimal(StrUtil.isBlank(so.getBaseQuantity()) ? "0" :so.getBaseQuantity()));
                    sum = sum.add(new BigDecimal(StrUtil.isBlank(so.getIsum()) ? "0" :so.getIsum()));
                    cos = cos.add(new BigDecimal(StrUtil.isBlank(so.getIcost()) ? "0" :so.getIcost()));
                    se = se.add(new BigDecimal(StrUtil.isBlank(so.getItaxprice()) ? "0" :so.getItaxprice()));
                    js = js.add(hxB);
                } else {
                    num = num.subtract(new BigDecimal(StrUtil.isBlank(so.getBaseQuantity()) ? "0" :so.getBaseQuantity()).abs());
                    sum = sum.subtract(new BigDecimal(StrUtil.isBlank(so.getIsum()) ? "0" :so.getIsum()).abs());
                    cos = cos.subtract(new BigDecimal(StrUtil.isBlank(so.getIcost()) ? "0" :so.getIcost()).abs());
                    se = se.subtract(new BigDecimal(StrUtil.isBlank(so.getItaxprice()) ? "0" :so.getItaxprice()).abs());
                    js = js.subtract(hxB);
                }
                if (mark.equals("1")){
                    List<StockSaleousings> cks = o.getT4().stream().filter(it->mark.equals("1") ? it.getCinvode().equals(str) : mark.equals("2") ? (StrUtil.isNotBlank(cvencode) ? it.getCvencode().equals(str) : it.getCvencodeJs().equals(str)) : mark.equals("3") ? it.getBcheckUser().equals(str) : it.getBcheckTime().equals(str)).collect(Collectors.toList());
                    List<StockSaleousings> ths = o.getT5().stream().filter(it->mark.equals("1") ? it.getCinvode().equals(str) : mark.equals("2") ? (StrUtil.isNotBlank(cvencode) ? it.getCvencode().equals(str) : it.getCvencodeJs().equals(str)) : mark.equals("3") ? it.getBcheckUser().equals(str) : it.getBcheckTime().equals(str)).collect(Collectors.toList());
                    List<StockSaleousings> fps = o.getT6().stream().filter(it->mark.equals("1") ? it.getCinvode().equals(str) : mark.equals("2") ? (StrUtil.isNotBlank(cvencode) ? it.getCvencode().equals(str) : it.getCvencodeJs().equals(str)) : mark.equals("3") ? it.getBcheckUser().equals(str) : it.getBcheckTime().equals(str)).collect(Collectors.toList());
                    ck = ck.add(BigDecimal.valueOf(cks.stream().mapToDouble(it->new BigDecimal(null==it.getBaseQuantity()?"0":it.getBaseQuantity()).doubleValue()).sum()));
                    th = th.add(BigDecimal.valueOf(ths.stream().mapToDouble(it->new BigDecimal(null==it.getBaseQuantity()?"0":it.getBaseQuantity()).abs().doubleValue()).sum()));
                    kp = kp.add(BigDecimal.valueOf(fps.stream().mapToDouble(it->new BigDecimal(null==it.getBaseQuantity()?"0":it.getBaseQuantity()).doubleValue()).sum()));
                }
            }
            saleousings.setBaseQuantity(keepDecimals(num, 10)).setIsum(keepDecimals(sum, 4)).setIsumChuku(keepDecimals(ck, 10)).setIsumTuiHuo(keepDecimals(th, 10)).setIsumFapiao(keepDecimals(kp, 10)).setIcost(keepDecimals(cos, 4)).setItaxprice(keepDecimals(se, 4)).setHxIsum(keepDecimals(js, 4));
            list.add(saleousings);
        }
        if (StrUtil.isBlank(sortMark)){
            if (sortMark.equals("1")){
            Collections.sort(list, Comparator.comparing(StockSaleousings::getBaseQuantity));
            }else {
            Collections.sort(list, Comparator.comparing(StockSaleousings::getIsum));
            }
        }
        return list;
    }

    private Object queryFilterMingXi(Tuple4<List<StockSaleousings>, List<StockSaleousing>, List<Stock>, List<Customer>> o, Map<String, String> query, String mark) {
        String ccodeMin = !query.containsKey("ccodeMin") ? "" : query.get("ccodeMin").trim();
        String ccodeMax = !query.containsKey("ccodeMax") ? "" : query.get("ccodeMax").trim();
        String bdocumStyle = !query.containsKey("bdocumStyle") ? "" : query.get("bdocumStyle").trim();
        String cvencode = !query.containsKey("cvencode") ? "" : query.get("cvencode").trim();
        String cvencodeJs = !query.containsKey("cvencodeJs") ? "" : query.get("cvencodeJs").trim();
        String cinvode = !query.containsKey("cinvode") ? "" : query.get("cinvode").trim();
        String cwhcode = !query.containsKey("cwhcode") ? "" : query.get("cwhcode").trim();
        String cpersoncode = !query.containsKey("cpersoncode") ? "" : query.get("cpersoncode").trim();
        String cinvodeClass = !query.containsKey("cinvodeClass") ? "" : query.get("cinvodeClass").trim();
        String cdepcode = !query.containsKey("cdepcode") ? "" : query.get("cdepcode").trim();
        String region = !query.containsKey("region") ? "" : query.get("region").trim();
        String wuliuUser = !query.containsKey("wuliuUser") ? "" : query.get("wuliuUser").trim();
        String stockGgxh = !query.containsKey("stockGgxh") ? "" : query.get("stockGgxh").trim();
        Set<String> chSets = null;
        if (StrUtil.isBlank(cinvode) && (StrUtil.isNotBlank(stockGgxh) || StrUtil.isNotBlank(cinvodeClass))) {
            List<Stock> stocks = StrUtil.isNotBlank(stockGgxh) ? o.getT3().stream().filter(it -> it.getStockGgxh().equals(stockGgxh)).collect(Collectors.toList())
                    : o.getT3().stream().filter(it -> it.getStockClass().equals(cinvodeClass)).collect(Collectors.toList());
            chSets = new HashSet<>(stocks.stream().map(it -> it.getStockNum()).collect(Collectors.toList()));
        }
        Set<String> finalChSets = chSets;
        List<StockSaleousings> filterList = o.getT1().stream().filter(entiry -> {
            if (StrUtil.isNotBlank(ccodeMin) && StrUtil.isNotBlank(ccodeMax)) { // 编码过滤
                String ccode = entiry.getCcode().substring(entiry.getCcode().length() - 4);
                if (Integer.parseInt(ccodeMin) > Integer.parseInt(ccode) && Integer.parseInt(ccodeMax) < Integer.parseInt(ccode))
                    return false;
            }
            StockSaleousing saleousing = o.getT2().stream().filter(it -> it.getCcode().equals(entiry.getCcode())).collect(Collectors.toList()).get(0);
            if (StrUtil.isNotBlank(bdocumStyle) && !saleousing.getBdocumStyle().equals(bdocumStyle)) return false; // 蓝字 或 红字
            entiry.setCfree1(saleousing.getBdocumStyle()); // 零食存储类型
            entiry.setCfree2(saleousing.getCdepcode()); // 零食存储部门
            entiry.setCfree3(saleousing.getCpersoncode()); // 零食存储业务员
            entiry.setCfree4(saleousing.getBworkableUser()); // 零食存储业务员
            if (mark.equals("1") && StrUtil.isNotBlank(cinvode) ){
                List<Stock> stocks = o.getT3().stream().filter(it -> it.getStockNum().equals(entiry.getCinvode())).collect(Collectors.toList());
                if (!stocks.get(0).getId().equals(cinvode))return false;
            }
            if (mark.equals("2") && StrUtil.isNotBlank(cvencode) && !entiry.getCvencode().equals(cvencode))
                return false;
            if (mark.equals("2") && StrUtil.isNotBlank(cvencodeJs) && !entiry.getCvencodeJs().equals(cvencodeJs))
                return false;
            if (StrUtil.isNotBlank(cwhcode) && !entiry.getCwhcode().equals(cwhcode)) return false;

            if (StrUtil.isNotBlank(cdepcode) && (null == saleousing.getCdepcode() || !entiry.getCdepcode().equals(cdepcode))) return false;
            if (StrUtil.isNotBlank(wuliuUser) &&  (null == saleousing.getDeliveryUser() || !saleousing.getDeliveryUser().equals(wuliuUser))) return false;
            if (null != finalChSets &&  !finalChSets.contains(entiry.getCinvode())) return false; // 不包含说明不匹配
            return true;
        }).collect(Collectors.toList());
        Set<String> strings = new HashSet<>(filterList.stream().map(i -> mark.equals("1") ? i.getCinvode() : mark.equals("2") ? (StrUtil.isNotBlank(cvencode) ? i.getCvencode() : i.getCvencodeJs()) : mark.equals("3") ? i.getBcheckUser() : i.getBcheckTime()).collect(Collectors.toList()));
        List<StockSaleousings> list = new ArrayList<>();
        for (String str : strings) {
            if (null == str) continue;
            List<StockSaleousings> collect = filterList.stream().filter(it -> mark.equals("1") ? it.getCinvode().equals(str) : mark.equals("2") ? (StrUtil.isNotBlank(cvencode) ? it.getCvencode().equals(str) : it.getCvencodeJs().equals(str)) : mark.equals("3") ? it.getBcheckUser().equals(str) : it.getBcheckTime().equals(str)).collect(Collectors.toList());
            list.addAll(collect);
        }
        return list;
    }

    /**
     * 查询审核完的、未关闭的、物流单号等于空的销售出库单
     * @param map
     * @return
     */
    @PostMapping("findByDdateAndType")
    public Mono<R> findByDdateAndType(@RequestBody Map map){
        String billStyle=map.get("billStyle").toString();
        String iyear=map.get("iyear").toString();
       return saleousingRepository.findByDdateAndType(billStyle,iyear).collectList()
                .flatMap(list->{
                    list=list.stream().filter(a->StrUtil.isBlank(a.getDeliveryId())&&(StrUtil.isBlank(a.getBcloser())||!a.getBcloser().equals("1"))).collect(Collectors.toList());
                    return Mono.just(list);
                }).map(a->R.ok().setResult(a));
    }

    @PostMapping("findByDdateAndCk")
    public Mono<R> findByDdateAndCk(@RequestBody Map map){
        String strDate=map.get("strDate").toString();
        String endDate=map.get("endDate").toString();
        String billStyle=map.get("billStyle").toString();
        String cwhcode=map.get("cwhcode").toString();
        return (cwhcode.equals("all")?saleousingsRepository.findAllByDdateBetweenAndBillStyleAndBcheckOrderByCcodeAsc(strDate, endDate, billStyle,"1").collectList():saleousingsRepository.findAllByDdateBetweenAndBillStyleAndCwhcodeAndBcheckOrderByCcodeAsc( strDate, endDate, billStyle,cwhcode,"1").collectList())
                .flatMap(list->{
                    list=list.stream().filter(a->StrUtil.isBlank(a.getIsumWuliu())&&(StrUtil.isBlank(a.getBcloser()) ||!a.getBcloser().equals("1") ) && (null == a.getIsumWuliu() || StrUtil.isBlank(a.getIsumWuliu()))).collect(Collectors.toList());
                    return Mono.just(list);
                }).map(a->R.ok().setResult(a));
    }

    @PostMapping("findAllJhzxByDate")
    public Mono<R> findAllJhzxByDate(@RequestBody Map map){
        String strDate=map.get("strDate").toString();
        String endDate=map.get("endDate").toString();
        String billStyle=map.get("billStyle").toString();
        String cwhcode=map.get("cwhcode").toString();
        return (cwhcode.equals("all")?saleousingRepository.findAllByDdateBetweenAndBillStyleAndBcheckOrderByCcodeAsc(strDate, endDate, billStyle,"1").collectList():saleousingRepository.findAllByDdateBetweenAndBillStyleAndCwhcodeAndBcheckOrderByCcodeAsc( strDate, endDate, billStyle,cwhcode,"1").collectList())
                .flatMap(list->{
                    list=list.stream().filter(a->(StrUtil.isBlank(a.getJhzxStatus()) || !a.getJhzxStatus().equals("1")) && (StrUtil.isBlank(a.getBcloser()) ||!a.getBcloser().equals("1") )).collect(Collectors.toList());
                    return Mono.just(list);
                }).map(a->R.ok().setResult(a));
    }

    @PostMapping("findbyStockSaleousingsCodeAndBillStyle")
    public Mono<R> findbyStockSaleousingsCodeAndBillStyle(String code,String billStyle){
        return saleousingsRepository.findAllByBillStyleAndCcodeOrderByDdateAscCcodeAscLineIdAsc(billStyle,code).collectList().map(a->R.ok().setResult(a));
    }

    @PostMapping("findbyStockSaleousingsCode")
    public Mono<R> findbyStockSaleousingsCode(String code){
        return saleousingsRepository.findAllByCcode(code).collectList().map(a->R.ok().setResult(a));
    }

    @PostMapping("findAllByStockSaleousingsTypeAndIyear")
    public Mono<R> findAllByStockSaleousingsTypeAndIyear(@RequestBody Map map){
        String billStyle=map.get("billStyle").toString();
        String iyear=map.get("iyear").toString();
        // 已审核并且主表没有物流信息
        return saleousingsRepository.findAllByBillStyleAndIyear(billStyle,iyear).collectList().map(a->R.ok().setResult(a));
    }

    private Object queryFilter(Tuple2<List<StockSaleousings>,List<StockSaleousing>> o, Map<String, String> query,String mark,String doc) {
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
        List<?> list = (mark.equals("1")?o.getT1():o.getT2()).stream().filter(it -> {
            if (mark.equals("1")) {
                StockSaleousings entiry = (StockSaleousings) it;
                if (StrUtil.isNotBlank(doc) && !(o.getT2().stream().filter(i->i.getCcode().equals(entiry.getCcode())).collect(Collectors.toList()).get(0).getBdocumStyle()).equals(doc)) return false;
                if (StrUtil.isNotBlank(ccodeMin) && StrUtil.isNotBlank(ccodeMax)){

                }
                if (StrUtil.isNotBlank(cvencode) && !entiry.getCvencode().equals(cvencode))  return false;
                if (StrUtil.isNotBlank(bcheck) && !entiry.getBcheck().equals(bcheck))  return false;
                if (StrUtil.isNotBlank(cinvode) && !entiry.getCinvode().equals(cinvode))  return false;
                if (StrUtil.isNotBlank(cinvodeClass) && !entiry.getCinvode().equals(cinvodeClass))  return false;
                if (StrUtil.isNotBlank(cwhcode) && !entiry.getCwhcode().equals(cwhcode))  return false;
//                if (StrUtil.isNotBlank(cpersoncode) && !entiry.getCvencode().equals(cpersoncode))  return false;
                if (StrUtil.isNotBlank(cdepcode) && !entiry.getCdepcode().equals(cdepcode))  return false;
                if (StrUtil.isNotBlank(citemcode) && !entiry.getCitemcode().equals(citemcode))  return false;
                if (StrUtil.isNotBlank(batchId) && !entiry.getBatchId().equals(batchId))  return false;
                if (StrUtil.isNotBlank(dvdate) && !entiry.getDvdate().equals(dvdate))  return false;
            } else {
                StockSaleousing entiry = (StockSaleousing) it;
                if (StrUtil.isNotBlank(doc) && !entiry.getBdocumStyle().equals(doc)) return false;
            }
            return true;
        }).collect(Collectors.toList());
        return  list;
    }

    /**
     *  根据出库单号 去重汇总得到数量
     * @param map
     * @return
     */
    @PostMapping("findMingXiDeduplicationByCodes")
    public Mono<R> findByCcodeNumberSummaryDeduplication(@RequestBody Map map){
        List<String> codes = (List<String>)map.get("codes");
        String type=map.get("type").toString();
        return saleousingsRepository.findAllByBillStyleAndCcodeIn(type,codes).collectList().map(o->R.ok(assemblyDeduplicationData(o)));
    }

    /**
     *  根根据来源单号 查询单据明细
     * @param map
     * @return
     */
    @PostMapping("findMingXiBySoureCodes")
    public Mono<R> findMingXiBySoureCodes(@RequestBody Map map){
        List<String> codes = (List<String>)map.get("codes");
        String type=map.get("type").toString();
        return saleousingsRepository.findAllByBillStyleAndSourcecodeIn(type,codes).collectList().map(R::ok);
    }

    /**
     *  根据出库单号 去重汇总得到数量
     * @param map
     * @return
     */
    @PostMapping("findAllSaleousingByCodes")
    public Mono<R> findAllSaleousingByCodes(@RequestBody Map map){
        List<String> codes = JSON.parseArray(map.get("codes").toString(),String.class);
        return saleousingRepository.findByCcodeIn(codes).collectList().zipWith(saleousingsRepository.findAllByCcodeIn(codes).collectList()).map(o->R.ok(o));
    }

    private Object assemblyDeduplicationData(List<StockSaleousings> o) {
        List<Object> list = new ArrayList<>();
        // 根据 仓库 存货编码 批号 日期 去重汇总
        Set<String> keys = new HashSet<>(o.stream().map(it->{
            StringBuilder s = new StringBuilder(it.getCwhcode());
            s.append("=").append(it.getCinvode()).append("=").append(it.getBatchId()).append("=").append(it.getDpdate()).append("=").append(it.getDvdate());
            return s.toString();
        }).collect(Collectors.toList()));
        for (String key : keys) {
            String[] ss = key.split("=");
            List<StockSaleousings> collect = o.stream().filter(it -> it.getCwhcode().equals(ss[0]) && it.getCinvode().equals(ss[1]) && ((null == it.getBatchId() && ss[2].equals("null")) || it.getBatchId().equals(ss[2])) && ((null == it.getDpdate() && ss[3].equals("null")) || it.getDpdate().equals(ss[3])) && ( (null == it.getDvdate() && ss[4].equals("null")) || it.getDvdate().equals(ss[4]))).collect(Collectors.toList());
            StockSaleousings temp = new StockSaleousings();
            BeanUtils.copyProperties(collect.get(0),temp);
            temp.setCcode(null).setId(null).setCmemo(null);
            BigDecimal zNum = new BigDecimal(0);
            BigDecimal num = new BigDecimal(0);
            BigDecimal c1Num = new BigDecimal(0);
            BigDecimal c2Num = new BigDecimal(0);
            for (StockSaleousings saleousings : collect) {
                num = num.add(new BigDecimal(saleousings.getQuantity()));
                zNum = zNum.add(new BigDecimal(saleousings.getBaseQuantity()));
                c1Num = c1Num.add(new BigDecimal(saleousings.getSubQuantity1()));
                c2Num = c2Num.add(new BigDecimal(saleousings.getSubQuantity2()));
            }
            temp.setQuantity(keepDecimals(num, 10)).setBaseQuantity(keepDecimals(zNum, 10)).setSubQuantity1(keepDecimals(c1Num, 10)).setSubQuantity2(keepDecimals(c2Num, 10));
            list.add(temp);
        }
        return list;
    }

    /**
     * 查询 已审核、未关闭的期初销货单、     * 查询 已审核、未关闭的期初销货单、到货单单
     * @param map
     * @return
     */
    @PostMapping("findAllXHD_And_QCXHD")
    public Mono<R> findAllXHD_And_QCXHD(@RequestBody Map map){
        String iyear=map.get("iyear").toString();
        String cvencode=map.get("cvencode").toString();
        List<String> list= (List<String>) map.get("type");
        Map<String, String> searchMap = ((HashMap<String,  String>) map.get("searchConditon"));
        return saleousingRepository.findAllXHD_And_QCXHD(iyear,cvencode,list).collectList()
                .flatMap(datalist->{
                    if(StrUtil.isNotBlank(searchMap.get("value"))){
                        datalist=datalist.stream().filter(a->a.getCcode().contains(searchMap.get("value"))).collect(Collectors.toList());
                    }
                    return Mono.just(datalist);
                })
                .map(a->R.ok().setResult(a));
    }

    /**
     * 销售业务所有参照 查询过滤
     * cvencode 客户
     * bstype 0蓝字 1红字
     * btype 查询单据类型
     * list 被查询类型列表
     * dates 日期区间
     * @param map
     * @return
     */
    @PostMapping("findAllSalesRefer")
    public Mono<R> findAllSalesRefer(@RequestBody Map map){
        String cvencode=map.get("cvencode").toString();
        String bstype=  null != map.get("bstype")?map.get("bstype").toString():"0";
        String btype= null != map.get("btype")?map.get("btype").toString():"";
        String billType= null != map.get("billType")?map.get("billType").toString():"";
        String qrfp= null != map.get("qrfp")?map.get("qrfp").toString():"";
        List<String> list= (List<String>) map.get("type");
        List<String> dates= (List<String>) map.get("dates");
        return saleousingRepository.findAllSalesRefer(dates.get(0),dates.get(1),cvencode,list).collectList()
                .flatMap(datalist->{
                    if (billType.equals("XSFP")){
                        if (list.contains("XSDD")){
                            datalist=datalist.stream().filter(a->(a.getBdocumStyle().equals(bstype) && (null == a.getSourcetype() || (null != a.getSourcetype() &&  (qrfp.equals("1")?!a.getSourcetype().equals("XSCKD"):!a.getSourcetype().equals("XHD")) ))) && (null == a.getFapiaoStatus() || !a.getFapiaoStatus().equals("1")) ).collect(Collectors.toList());
                        } else {
                            datalist=datalist.stream().filter(a->(a.getBdocumStyle().equals(bstype) && (null == a.getFapiaoStatus() || !a.getFapiaoStatus().equals("1")) )).collect(Collectors.toList());
                        }
                    }else if(billType.equals("XSCKD")){
                        datalist=datalist.stream().filter(a->(a.getBdocumStyle().equals(bstype) && (null == a.getSourcetype() || (null != a.getSourcetype() &&  (list.contains("XSDD")?!a.getSourcetype().equals("CGRKD"):!a.getSourcetype().equals("XSCKD"))  )) && (null == a.getChukuStatus() || !a.getChukuStatus().equals("1")))).collect(Collectors.toList());
                    }else {
                        // 来源单据
                        if (list.contains("XHD")) datalist = datalist.stream().filter(a->a.getBdocumStyle().equals("0") && (null == a.getSourcetype() || (null != a.getSourcetype() && !a.getSourcetype().equals("XSCKD"))) && (btype.equals("XSCKD")?(null == a.getChukuStatus() || !a.getChukuStatus().equals("1")):(null == a.getTuihuoStatus() || !a.getTuihuoStatus().equals("1")))).collect(Collectors.toList());
                        if (list.contains("XSCKD")) datalist = datalist.stream().filter(a->(a.getBdocumStyle().equals(bstype) && (null == a.getSourcetype() || (null != a.getSourcetype() && !a.getSourcetype().equals("XHD")))) && (null == a.getXiaohuoStatus() || !a.getXiaohuoStatus().equals("1")) ).collect(Collectors.toList());
                    }
                    return Mono.just(datalist);
                })
                .map(a->R.ok().setResult(a));
    }

    /**
     * 查询 已审核、未关闭、未确认的出库单
     * @param map
     * @return
     */
    @PostMapping("findAllXSCKD")
    public Mono<R> findAllXSCKD(@RequestBody Map map){
        List<String> list= (List<String>) map.get("type");
        List<String> dates= (List<String>) map.get("dates");
        Map<String, String> searchMap = ((HashMap<String,  String>) map.get("searchConditon"));
        return saleousingRepository.findAllXSCKD(dates.get(0),dates.get(1),list).collectList()
                .flatMap(datalist->{
                    if(StrUtil.isNotBlank(searchMap.get("value"))){
                        datalist=datalist.stream().filter(a->a.getCcode().contains(searchMap.get("value"))).collect(Collectors.toList());
                    }
                    return Mono.just(datalist);
                })
                .map(a->R.ok().setResult(a));
    }

    @PostMapping("findAllByListCcode")
    public Mono<R> findAllByListCcode(@RequestBody List<String> lists){
        return saleousingsRepository.findAllByListCcode(lists).collectList().map(o -> R.ok().setResult(o));
    }

    @PostMapping("findAllByTypeList")
    public Mono<R> findAllByTypeList(@RequestBody Map map) {
        String type=map.get("type").toString();
        String strDate=map.get("strDate").toString();
        String endDate=map.get("endDate").toString();
        String cangku= map.containsKey("cangku") ? map.get("cangku").toString() : "";
        String stock= map.containsKey("cangku") ? map.get("stock").toString() : "";
        String ccode= map.containsKey("ccode") ? map.get("ccode").toString() : "";
        return saleousingRepository.findAllByTypeList(type, strDate, endDate).collectList()
                .flatMap(list->{
                    if(StrUtil.isNotBlank(cangku)){
                        list=list.stream().filter(a->a.getCwhcode().equals(cangku)).collect(Collectors.toList());
                    }
                    if(StrUtil.isNotBlank(ccode)){
                        list=list.stream().filter(a->a.getCcode().equals(ccode)).collect(Collectors.toList());
                    }
                    return Mono.just(list);
                }).map(a->R.ok().setResult(a));
    }

    @PostMapping("findAllChuKuMainList")
    public Mono<R> findAllChuKuMainList(@RequestBody Map map) {
        String type = map.get("type").toString();
        Integer strDate = Integer.valueOf(map.get("strDate").toString().replaceAll("-", ""));
        Integer endDate = Integer.valueOf(map.get("endDate").toString().replaceAll("-", ""));
        String strNum = map.get("strNum").toString();
        String endNum = map.get("endNum").toString();
        String sup = map.get("sup").toString();
        String jssup = map.get("jssup").toString();
        String dataType = map.get("dataType").toString();
        String cangku = map.get("cangku").toString();

        return Mono.just(R.ok().setResult(""));
    }

    @PostMapping("changeBefore")
    public Mono<R> changeBefore(@RequestBody Map map) {
        String code = map.get("code").toString();
        String style = map.get("style").toString();
        String id = map.get("id").toString();
        HashMap<String, Object> result = new HashMap<>();
        result.put("type", "0");
        return saleousingRepository.findByCcode(code).flatMap(db -> {
            String[] dates = db.getDdate().split("-");
            Mono<GroupStockPeriod> proiodMono = groupStockPeriodRepository.findByUniqueCodeAndStockYearAndStockMonth(id, dates[0], dates[1]);
            return proiodMono.flatMap(ps -> {
                if (null != ps.getIstock() && ps.getIstock().equals("1")) {
                    result.put("type", "1");
                    result.put("codes", ps.getStockYear() + ps.getStockMonth());
                    return Mono.just(R.ok(result));
                } else {
                    return stockXyCsourceRepository.findAllByBillStyleAndCcode(style, code).collectList().flatMap(xyList -> {
                        List<StockXyCsource> cwpz = xyList.stream().filter(it -> it.getXyBillStyle().equals("CWPZ")).collect(Collectors.toList());
                        if (cwpz.size() > 0) {
                            result.put("type", "2");
                            result.put("codes", cwpz.stream().map(it -> it.getCcode()).collect(Collectors.toList()));
                            result.put("codeType", new HashSet<>(cwpz.stream().map(it -> it.getXyBillStyle()).collect(Collectors.toList())));
                            return Mono.just(R.ok(result));
                        } else {
                            boolean hs = null != db.getHxIsum() && Double.parseDouble(db.getHxIsum()) != 0;
                            boolean hc = null != db.getHzhcNum() && Integer.parseInt(db.getHzhcNum()) > 0;
                            if (hs || hc) {
                                result.put("type", hs ? "3" : "4");
                                result.put("codes", db.getCcode());
                            }
                            return Mono.just(R.ok(result));
                        }
                    });
                }
            });
        });
    }

    @PostMapping("generateCkd")
    @Transactional
    public Mono<R> generateCkd(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        // 参数设置：入库保存状态就是现存量 标志 1是查询所有 0 查询已审核
        String rkBcheck=map.get("rkBcheck")==null?"0":map.get("rkBcheck").toString();
        String ckBcheck=map.get("ckBcheck")==null?"0":map.get("ckBcheck").toString();
        Map<String, Object> obj = assemblyCorrespondingRulesData(map);
        StockSaleousing master = (StockSaleousing) obj.get("master");
        List<StockSaleousings> sub = (List<StockSaleousings>) obj.get("sub");
        // 数量变化 添加下游表
        List<String> collect = sub.stream().filter(it -> it.getSourcecode() != null).map(it -> it.getSourcecode()).collect(Collectors.toList());
        return saleousingRepository.save(master).flatMap(db -> {
            Mono<StockSaleousing> one = saleousingRepository.findByCcode(collect.get(0));
            Mono<List<StockSaleousings>> two = saleousingsRepository.findAllByCcode( collect.get(0)).collectList();
            Mono<String> xy = stockXyCsourceRepository.save(new StockXyCsource().setIyear(db.getIyear()).setCcode(sub.get(0).getSourcecode()).setCcodeDate(sub.get(0).getSourcedate()).setBillStyle(sub.get(0).getSourcetype()).setXyccode(master.getCcode()).setXyccodeDate(master.getDdate()).setXyBillStyle(master.getBillStyle())).flatMap(z -> Mono.just(""));
            return Mono.zip(one, two,getCurrCbPrice(sub,rkBcheck,ckBcheck)).flatMap(tips -> {
                BigDecimal zck = new BigDecimal(0);
                BigDecimal yck = new BigDecimal(0);
                StockSaleousing t1 = tips.getT1();
                List<StockSaleousings> t2 = tips.getT2();
                for (StockSaleousings tw : t2) {
                    List<StockSaleousings> collect2 = sub.stream().filter(it -> it.getSourcedetailId().equals(tw.getLineCode())).collect(Collectors.toList());
                    BigDecimal add1 = new BigDecimal(StrUtil.isBlank(tw.getIsumChuku()) ? "0" : tw.getIsumChuku()).add(new BigDecimal(collect2.size() > 0 ? collect2.get(0).getBaseQuantity() : "0"));
                    tw.setIsumChuku(keepDecimals(add1,10));
                    yck = yck.add(add1);
                    zck = zck.add(new BigDecimal(tw.getBaseQuantity()));
                }
                t1.setChukuStatus(zck.equals(yck) ? "1" : null);
                for (StockSaleousings saleousings : sub) {
                    saleousings.setId(null);
                    List<StockCostAccRo> accRos = tips.getT3().stream().filter(p -> p.getStockCangku().equals(saleousings.getCwhcode1()) && p.getStockNum().equals(saleousings.getCinvode()) && (StrUtil.isBlank(saleousings.getBatchId()) ? true : saleousings.getBatchId().equals(p.getBatchId()))).collect(Collectors.toList());
                    if (accRos.size() > 0 && StrUtil.isNotBlank(accRos.get(0).getPrice())){
                        BigDecimal b = new BigDecimal(saleousings.getQuantity());
                        BigDecimal price = calculateTheExchangeRate(saleousings,accRos.get(0).getPrice()).abs();
                        BigDecimal e = b.multiply(price);
                        saleousings.setPrice(keepDecimals(price,10));
                        saleousings.setIcost(keepDecimals(e,4));
                    }
                }
                return Mono.zip(saleousingRepository.save(t1), saleousingsRepository.saveAll(t2).collectList(), saleousingsRepository.saveAll(sub).collectList()/*,currentstockRepository.saveAll(t3).collectList()*/, xy).flatMap(tips2 -> Mono.just(R.ok(db)));
            });
        });
    }

    @PostMapping("generateThd")
    @Transactional
    public Mono<R> generateThd(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        Map<String, Object> obj = assemblyCorrespondingRulesData(map);
        StockSaleousing master = (StockSaleousing) obj.get("master");
        List<StockSaleousings> sub = (List<StockSaleousings>) obj.get("sub");
        // 数量变化 添加下游表
        List<String> collect = sub.stream().filter(it -> it.getSourcedetailId() != null).map(it -> it.getSourcedetailId()).collect(Collectors.toList());
        return (collect.size() > 0 ? saleousingsRepository.findAllByBillStyleAndLineCodeInOrderByDdateAscCcodeAscLineIdAsc("XHD", collect).collectList() : Mono.just(new ArrayList<StockSaleousings>())).flatMap(l -> saleousingRepository.save(master).flatMap(db -> {
            for (StockSaleousings stockSaleousings : l) { // 出库明细不存在 代表 已经被移除 赋值为0
                List<StockSaleousings> collect1 = sub.stream().filter(it -> it.getSourcedetailId().equals(stockSaleousings.getLineCode())).collect(Collectors.toList());
                BigDecimal add = new BigDecimal(StrUtil.isBlank(stockSaleousings.getIsumTuiHuo()) ? "0" : stockSaleousings.getIsumTuiHuo()).add(new BigDecimal(collect1.size() > 0 ? collect1.get(0).getBaseQuantity() : "0").abs());
                stockSaleousings.setIsumTuiHuo(keepDecimals(add,10));
            }
            Mono<StockSaleousing> s = saleousingsRepository.saveAll(sub).collectList().zipWith(l.size() > 0 ? saleousingsRepository.saveAll(l).collectList() : Mono.just(new ArrayList<StockSaleousings>())).thenReturn(db); //添加
            return s.flatMap(z -> {
                Mono<String> xy = stockXyCsourceRepository.save(new StockXyCsource().setIyear(db.getIyear()).setCcode(sub.get(0).getSourcecode()).setCcodeDate(sub.get(0).getSourcedate()).setBillStyle(sub.get(0).getSourcetype()).setXyccode(master.getCcode()).setXyccodeDate(master.getDdate()).setXyBillStyle(master.getBillStyle())).flatMap(g -> Mono.just(""));
                Mono<List<StockCurrentstock>> ztl = currentstockRepository.findAllByIyearAndInvCodeIn(db.getIyear(), l.stream().map(it -> it.getCinvode()).distinct().collect(Collectors.toList())).collectList().flatMap(list1 -> {
                    // 修改在途数量
                    for (StockCurrentstock stockCurrentstock : list1) {
                        List<StockSaleousings> stockSaleousings = l.stream().filter(i -> i.getCinvode().equals(stockCurrentstock.getInvCode()) && (StrUtil.isNotBlank(i.getBatchId())?(i.getBatchId().equals(stockCurrentstock.getBatchId()) && i.getDvdate().equals(stockCurrentstock.getDvdate()) && i.getDpdate().equals(stockCurrentstock.getDpdate())):true) ).collect(Collectors.toList());
                        if (stockSaleousings.size() > 0) {
                            StockSaleousings saleousings = stockSaleousings.get(0);
                            BigDecimal s1 = stockCurrentstock.getZtckQuantityXhd().subtract(new BigDecimal(saleousings.getBaseQuantity()));
                            stockCurrentstock.setZtckQuantityXhd(s1);
                        }
                    }
                    return currentstockRepository.saveAll(list1).collectList();
                });
                return xy/*.zipWith(ztl)*/.flatMap(tis -> Mono.just(db));
            });
        }).map(o -> R.ok()));
    }


    @PostMapping("generateXhd")
    @Transactional
    public Mono<R> generateXhd(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        Map<String, Object> obj = assemblyCorrespondingRulesData(map);
        StockSaleousing master = (StockSaleousing) obj.get("master");
        List<StockSaleousings> sub = (List<StockSaleousings>) obj.get("sub");
        // 数量变化 添加下游表
        List<String> collect = sub.stream().filter(it -> it.getSourcedetailId() != null).map(it -> it.getSourcedetailId()).collect(Collectors.toList());
        return (collect.size() > 0 ? saleousingsRepository.findAllByBillStyleAndLineCodeInOrderByDdateAscCcodeAscLineIdAsc("XSCKD", collect).collectList() : Mono.just(new ArrayList<StockSaleousings>())).flatMap(l -> saleousingRepository.save(master).flatMap(db -> {
            for (StockSaleousings stockSaleousings : l) { // 出库明细不存在 代表 已经被移除 赋值为0
                List<StockSaleousings> collect1 = sub.stream().filter(it -> it.getSourcedetailId().equals(stockSaleousings.getLineCode())).collect(Collectors.toList());
                BigDecimal add = new BigDecimal(StrUtil.isBlank(stockSaleousings.getIsumXiaohuo()) ? "0" : stockSaleousings.getIsumXiaohuo()).add(new BigDecimal(collect1.size() > 0 ? collect1.get(0).getBaseQuantity() : "0"));
                stockSaleousings.setIsumXiaohuo(keepDecimals(add,10));
            }
            Mono<StockSaleousing> s = saleousingsRepository.saveAll(sub).collectList().zipWith(l.size() > 0 ? saleousingsRepository.saveAll(l).collectList() : Mono.just(new ArrayList<StockSaleousings>())).thenReturn(db); //添加
            return s.flatMap(z -> {
                Mono<String> xy = stockXyCsourceRepository.save(new StockXyCsource().setIyear(db.getIyear()).setCcode(sub.get(0).getSourcecode()).setCcodeDate(sub.get(0).getSourcedate()).setBillStyle(sub.get(0).getSourcetype()).setXyccode(master.getCcode()).setXyccodeDate(master.getDdate()).setXyBillStyle(master.getBillStyle())).flatMap(g -> Mono.just(""));
                Mono<List<StockCurrentstock>> ztl = currentstockRepository.findAllByIyearAndInvCodeIn(db.getIyear(), l.stream().map(it -> it.getCinvode()).distinct().collect(Collectors.toList())).collectList().flatMap(list1 -> {
                    // 修改在途数量
                    for (StockCurrentstock stockCurrentstock : list1) {
                        List<StockSaleousings> stockSaleousings = l.stream().filter(i -> i.getCinvode().equals(stockCurrentstock.getInvCode()) && (StrUtil.isNotBlank(i.getBatchId())?(i.getBatchId().equals(stockCurrentstock.getBatchId()) && i.getDvdate().equals(stockCurrentstock.getDvdate()) && i.getDpdate().equals(stockCurrentstock.getDpdate())):true) ).collect(Collectors.toList());
                        if (stockSaleousings.size() > 0) {
                            StockSaleousings saleousings = stockSaleousings.get(0);
                            BigDecimal s1 = stockCurrentstock.getZtckQuantityXhd().subtract(new BigDecimal(saleousings.getBaseQuantity()));
                            stockCurrentstock.setZtckQuantityXhd(s1);
                        }
                    }
                    return currentstockRepository.saveAll(list1).collectList();
                });
                return xy/*.zipWith(ztl)*/.flatMap(tis -> Mono.just(db));
            });
        }).map(o -> R.ok()));
    }


    @PostMapping("findEntityAndDetailsByCcode")
    public Mono<R> findEntityAndDetailsByCcode(@RequestBody Map map) {
        if (map.keySet().size() != 2 || null == map.get("code")) return Mono.just(R.error());
        String code = map.get("code").toString();
        String iyear = map.get("iyear").toString();
        return (saleousingRepository.findByIyearAndCcode(iyear,code).switchIfEmpty(Mono.just(new StockSaleousing()))).zipWith(saleousingsRepository.findAllByIyearAndCcode(iyear,code).collectList()).map(tips->R.ok(tips.getT1().getId()==null?null:tips));
    }

    /****************************** 变动 ****************************/
    @Autowired
    private StockSaleousingBiandongRepository saleousingBiandongRepository;
    @Autowired
    private StockSaleousingsBiandongRepository saleousingBiandongsRepository;

    @PostMapping("change")
    @Transactional
    public Mono<R> change(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        String changeId = map.get("changeId").toString();
        StockSaleousing modifyEntry = JSON.parseObject(map.get("entity").toString(), StockSaleousing.class);
        List<StockSaleousings> modifyEntityList = JSON.parseArray(map.get("entityList").toString(), StockSaleousings.class);
        List<String> dels = JSON.parseArray(map.get("dels").toString(), String.class);
        Mono<String> numerM = saleousingBiandongRepository.findFirstByCcodeLikeOrderByBaindongDateDesc("%"+modifyEntry.getCcode() + "-%").flatMap(it -> {
            int i = Integer.parseInt(it.getCcode().replace(modifyEntry.getCcode() + "-", ""))+1;
            return Mono.just(i > 9 ? "" + i : "0" + i);
        }).switchIfEmpty(Mono.just("01"));
        return Mono.zip(saleousingRepository.findByCcode(modifyEntry.getCcode()),saleousingsRepository.findAllByCcode(modifyEntry.getCcode()).collectList(),numerM).flatMap(
                tips->{
                    StockSaleousingBiandong bEntity = new StockSaleousingBiandong();
                    BeanUtils.copyProperties(tips.getT1(),bEntity);
                    bEntity.setId(null).setCcode(bEntity.getCcode()+"-"+tips.getT3()).setBiandong("1").setBiandongUser(changeId).setBaindongDate(DateUtil.now());
                    List<StockSaleousingsBiandong> bList = tips.getT2().stream().map(it -> {
                        StockSaleousingsBiandong saleousings = new StockSaleousingsBiandong();
                        BeanUtils.copyProperties(it, saleousings);
                        saleousings.setId(null).setCcode(bEntity.getCcode()).setBiandong("1").setBiandongUser(changeId).setBaindongDate(DateUtil.now());
                        return saleousings;
                    }).collect(Collectors.toList());
                    BigDecimal squantitySum1 = new BigDecimal(0);
                    BigDecimal squantity1Sum1 = new BigDecimal(0);
                    BigDecimal squantity2Sum1 = new BigDecimal(0);
                    BigDecimal icostSum1 = new BigDecimal(0);
                    BigDecimal isumSum1 = new BigDecimal(0);
                    BigDecimal taxAmountSum1 = new BigDecimal(0);
                    for (StockSaleousings entry : modifyEntityList) {
                        squantitySum1 = squantitySum1.add(new BigDecimal(entry.getBaseQuantity() == null ? "0" : entry.getBaseQuantity()));
                        squantity1Sum1 = squantity1Sum1.add(new BigDecimal(entry.getSubQuantity1() == null ? "0" : entry.getSubQuantity1()));
                        squantity2Sum1 = squantity2Sum1.add(new BigDecimal(entry.getSubQuantity2() == null ? "0" : entry.getSubQuantity2()));
                        icostSum1 = icostSum1.add(new BigDecimal(entry.getIcost() == null ? "0" : entry.getIcost()));
                        isumSum1 = isumSum1.add(new BigDecimal(entry.getIsum() == null ? "0" : entry.getIsum()));
                        taxAmountSum1 = taxAmountSum1.add(new BigDecimal(entry.getItaxprice() == null ? "0" : entry.getItaxprice()));
                        entry.setBiandong("1");
                    }
                    modifyEntry.setBiandong("1").setSquantity(keepDecimals(squantitySum1, 10)).setSquantity1(keepDecimals(squantity1Sum1, 10)).setSquantity2(keepDecimals(squantity2Sum1, 10)).setIcost(keepDecimals(icostSum1, 4)).setIsum(keepDecimals(isumSum1, 4)).setTaxAmount(keepDecimals(taxAmountSum1, 6));

                    // 修改来源 累计数
                    List<StockSaleousings> xsckd = modifyEntityList.stream().filter(it -> StrUtil.isNotBlank(it.getSourcecode()) && StrUtil.isNotBlank(it.getSourcetype()) && (it.getSourcetype().equals("XSCKD") || it.getSourcetype().equals("XSDD"))).collect(Collectors.toList());
                    Mono<String> lyModifyM =xsckd.size() == 0?Mono.just(""): saleousingRepository.findByCcode(xsckd.get(0).getSourcecode()).zipWith(saleousingsRepository.findAllByCcode(xsckd.get(0).getSourcecode()).collectList()).flatMap(tips3 -> { // 出库单修改销货数量
                        StockSaleousing xhEntity = tips3.getT1();
                        List<StockSaleousings> list = tips3.getT2();
                        BigDecimal ljNum = new BigDecimal(0);
                        for (StockSaleousings saleousings : list) {
                            // 先减除后添加
                            List<StockSaleousings> dbSales = tips.getT2().stream().filter(it -> it.getSourcedetailId().equals(saleousings.getLineCode())).collect(Collectors.toList());
                            List<StockSaleousings> collect = modifyEntityList.stream().filter(it -> it.getSourcedetailId().equals(saleousings.getLineCode())).collect(Collectors.toList());
                            BigDecimal add = null;
                            if (tips.getT1().getBdocumStyle().equals("0")) {
                                add = new BigDecimal((StrUtil.isNotBlank(saleousings.getIsumXiaohuo()) ? saleousings.getIsumXiaohuo() : "0"));
                                if (dbSales.size() > 0)
                                    add = add.subtract(new BigDecimal(dbSales.get(0).getBaseQuantity()));
                                if (collect.size() > 0) add = add.add(new BigDecimal(collect.get(0).getBaseQuantity()));
                                saleousings.setIsumXiaohuo(keepDecimals(add,10));
                            } else {
                                add = new BigDecimal((StrUtil.isNotBlank(saleousings.getIsumTuiHuo()) ? saleousings.getIsumTuiHuo() : "0"));
                                if (dbSales.size() > 0)
                                    add = add.subtract(new BigDecimal(dbSales.get(0).getBaseQuantity()).abs());
                                if (collect.size() > 0)
                                    add = add.add(new BigDecimal(collect.get(0).getBaseQuantity()).abs());
                                saleousings.setIsumTuiHuo(keepDecimals(add,10));
                            }
                            ljNum = ljNum.add(add);
                        }
                        boolean b = list.stream().mapToDouble(it -> Double.parseDouble(it.getBaseQuantity())).sum() == ljNum.doubleValue();
                        if (tips.getT1().getBdocumStyle().equals("0")) {
                            xhEntity.setXiaohuoStatus(b ? "1" : null);
                        } else {
                            xhEntity.setTuihuoStatus(b ? "1" : null);
                        }
                        return saleousingRepository.save(xhEntity).flatMap(e -> saleousingsRepository.saveAll(list).collectList().thenReturn(""));
                    });

                    return Mono.zip(saleousingBiandongRepository.save(bEntity),saleousingBiandongsRepository.saveAll(bList).collectList()).flatMap(tips2->{
                        boolean b = modifyEntityList.stream().mapToDouble(it -> new BigDecimal(StrUtil.isNotBlank(it.getBaseQuantity()) ? it.getBaseQuantity() : "0").doubleValue()).sum() == modifyEntityList.stream().mapToDouble(it -> new BigDecimal(StrUtil.isNotBlank(it.getIsumChuku()) ? it.getBaseQuantity() : "0").doubleValue()).sum();
                        modifyEntry.setChukuStatus(b?"1":null);
                        Mono<StockSaleousing> save = saleousingRepository.save(modifyEntry);
                        // 添加之前检查变动数量
                        Mono< List<StockSaleousings>> saveList = modifyEntityList.size() > 0 ? (saleousingsRepository.saveAll(modifyEntityList).collectList()) : Mono.just(new ArrayList<StockSaleousings>());
                        Mono<String> delM = dels.size() > 0 ? saleousingsRepository.deleteByLineCodeIn(dels).thenReturn("") : Mono.just("");
                        return Mono.zip(save,saveList,lyModifyM,delM).flatMap(tips3->Mono.just(R.ok(tips3.getT1())));
                    });
                }
        );
    }

    @PostMapping("findChangeList")
    public Mono<R> findChangeList(@RequestBody Map map) {
        return saleousingBiandongRepository.findAllByCcodeLikeOrderByBaindongDateDesc("%"+map.get("code").toString()+"%").collectList().map(R::ok);
    }

    @PostMapping("findBillByCondition2")
    public Mono<R> findBillByCondition2(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        String iyear = map.get("iyear").toString();
        String action = map.get("action").toString();
        String currPdId = map.containsKey("curr") ? map.get("curr").toString() : "";
        String ccdoe  = currPdId.substring(0,currPdId.lastIndexOf("-"));
        return saleousingBiandongRepository.findAllByIyearAndCcodeLikeOrderByBaindongDateDescCcodeDesc(iyear, "%"+ccdoe+"%")
                .collectList().cache()
                .flatMap(list -> {
                    if (list.size() == 0) {
                        return Mono.just(R.ok());
                    } else {
                        StockSaleousingBiandong master = null;
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
                        StockSaleousingBiandong finalMaster = master;
                        return saleousingBiandongsRepository.findAllByCcodeOrderByDdateAscCcodeAscLineIdAsc( master.getCcode()).collectList().map(enlist -> {
                            if (enlist.size() > 0) finalMaster.setEntryList(JSON.toJSONString(enlist));
                            return R.ok(finalMaster);
                        });
                    }
                });
    }
    /****************************** 变动 ****************************/

    /****************************** 确认单 ****************************/
    @PostMapping("qrckd")
    @Transactional
    public Mono<R> saveQrxhd(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        Map<String, Object> obj = assemblyCorrespondingRulesData(map);
        StockSaleousing master = (StockSaleousing) obj.get("master");
        List<StockSaleousings> sub = (List<StockSaleousings>) obj.get("sub");
        boolean b = master.getId() == null;
        return saleousingRepository.save(master).flatMap(db -> {
            Mono<String> xy = stockXyCsourceRepository.save(new StockXyCsource().setIyear(db.getIyear()).setCcode(sub.get(0).getSourcecode()).setCcodeDate(sub.get(0).getSourcedate()).setBillStyle(sub.get(0).getSourcetype()).setXyccode(master.getCcode()).setXyccodeDate(master.getDdate()).setXyBillStyle(master.getBillStyle())).flatMap(z->Mono.just(""));
            Mono<String> mark = saleousingRepository.findByCcode(sub.get(0).getSourcecode()).flatMap(en -> {en.setQuerenStatus("1");return saleousingRepository.save(en).thenReturn("");});
            Mono<StockSaleousing> s = saleousingsRepository.saveAll(sub).collectList().thenReturn(db); //添加
            Mono<StockSaleousing> d = saleousingsRepository.findAllByBillStyleAndCcodeOrderByDdateAscCcodeAscLineIdAsc(db.getBillStyle(), db.getCcode()).collectList().flatMap(list -> saleousingsRepository.deleteAll(list).thenReturn("").flatMap(i -> s)); //修改
            return b ? s.flatMap(a->xy.zipWith(mark).flatMap(t->Mono.just(""))) : d;
        }).map(o -> R.ok());
    }

    @PostMapping("review/qrd")
    @Transactional
    public Mono<R> reviewQrd(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        String id = map.get("id").toString();
        String userId = map.get("userId").toString();
        String type = map.get("type").toString();
        return saleousingRepository.findById(id).flatMap(dbEntry ->
                saleousingsRepository.findAllByBillStyleAndCcodeOrderByDdateAscCcodeAscLineIdAsc(dbEntry.getBillStyle(), dbEntry.getCcode()).collectList()
                        .flatMap(list -> {
                            if (type.equals("true")) {
                                dbEntry.setBcheck("1").setBcheckTime(DateUtil.now()).setBcheckUser(userId);
                                for (StockSaleousings stockWarehousings : list) {
                                    stockWarehousings.setBcheck("1").setBcheckTime(DateUtil.now()).setBcheckUser(userId);
                                }
//                                return changeCkdNumber(list).flatMap(e->saleousingRepository.save(dbEntry).flatMap(e1 -> saleousingsRepository.saveAll(list).collectList().thenReturn("")));
                            } else {
                                dbEntry.setBcheck(null).setBcheckTime(null).setBcheckUser(null);
                                for (StockSaleousings stockWarehousings : list) {
                                    stockWarehousings.setBcheck(null).setBcheckTime(null).setBcheckUser(null);
                                }
                            }
                            Mono<String> changeDelM = querenChangeRepository.findAllByBillStyleAndIyearAndQuerdId("XIAOSHOU", dbEntry.getIyear(), dbEntry.getCcode()).collectList().flatMap(qcs -> qcs.size() > 0 ? querenChangeRepository.deleteAll(qcs).thenReturn("") : Mono.just(""));
                            return saleousingRepository.save(dbEntry).flatMap(o -> saleousingsRepository.saveAll(list).collectList().flatMap(s->!type.equals("true")?changeDelM:Mono.just("")));
                        })
        ).map(R::ok);
    }

    @PostMapping("review/change")
    @Transactional
    public Mono<R> reviewChange(@RequestBody Map map) {
        if (map.keySet().size() == 0 || map.keySet().size()!=2) return Mono.just(R.error());
        String iyear = map.get("iyear").toString();
        String code = map.get("code").toString();
        return querenChangeRepository.findAllByBillStyleAndIyearAndQuerdId("XIAOSHOU",iyear,code).collectList().map(R::ok);
    }

    @PostMapping("review/specialQrd")
    @Transactional
    public Mono<R> reviewSpecialQrd(@RequestBody Map map) {
        if (map.keySet().size() != 4) return Mono.just(R.error());
        String id = map.get("id").toString();
        String userId = map.get("userId").toString();
        List<String> checks = (List<String>) map.get("checks");
        List<StockSaleousings> changeRow = JSON.parseArray(map.get("changeRow").toString(), StockSaleousings.class);
        return saleousingRepository.findById(id).flatMap(dbEntry ->
                saleousingsRepository.findAllByBillStyleAndCcodeOrderByDdateAscCcodeAscLineIdAsc(dbEntry.getBillStyle(), dbEntry.getCcode()).collectList()
                        .flatMap(list -> {
                            dbEntry.setBcheck("1").setBcheckTime(DateUtil.now()).setBcheckUser(userId);
                            for (StockSaleousings stockWarehousings : list) {
                                stockWarehousings.setBcheck("1").setBcheckTime(DateUtil.now()).setBcheckUser(userId);
                            }
                            return changeCkdNumber(dbEntry,checks,changeRow).flatMap(e->saleousingRepository.save(dbEntry).flatMap(e1 -> saleousingsRepository.saveAll(list).collectList().thenReturn("")));
                        })
        ).map(R::ok);
    }

    private Mono<String> changeCkdNumber(StockSaleousing queEntity,List<String> checks, List<StockSaleousings> changeList) {
        // 销售确认单
        Mono<StockSaleousing> one = saleousingRepository.findByCcode(changeList.get(0).getCcode());
        Mono<List<StockSaleousings>> two = saleousingsRepository.findAllByCcode(changeList.get(0).getCcode()).collectList();
        return Mono.zip(one,two).flatMap(tips->{
            StockSaleousing t1 = tips.getT1();
            List<StockSaleousings> t2 = tips.getT2();
            boolean lyB = t2.stream().filter(it -> it.getSourcecode() != null).collect(Collectors.toList()).size() > 0;
            // 是否发生改变 (没找到的 数量不对等的)
            BigDecimal squantitySum = new BigDecimal(0);
            BigDecimal squantity1Sum = new BigDecimal(0);
            BigDecimal squantity2Sum = new BigDecimal(0);
            BigDecimal icostSum = new BigDecimal(0);
            BigDecimal isumSum = new BigDecimal(0);
            BigDecimal taxAmountSum = new BigDecimal(0);
            List<StockQuerenChange> qrcList = new ArrayList<>(changeList.size());
            for (StockSaleousings ck : t2) {
                for (StockSaleousings qr : changeList) {
                    StockQuerenChange qrChange = new StockQuerenChange().setIyear(ck.getIyear()).setCcode(ck.getCcode()).setDdate(ck.getCcode()).setQuerdId(queEntity.getCcode()).setQuerdDate(queEntity.getCcode()).setBillStyle("XIAOSHOU")
                            .setInvoiceId(ck.getCinvode()).setBatchId(ck.getBatchId()).setDpdate(ck.getDpdate()).setDvdate(ck.getDvdate())
                            .setUnitId(ck.getXsUnitId()).setSquantity(ck.getQuantity());
                    if (qr.getSourcedetailId().equals(ck.getLineCode()) && null != qr.getCfree1()){
                        BigDecimal wsje = new BigDecimal(ck.getPrice()).multiply(new BigDecimal(qr.getCfree1()));
                        BigDecimal hsje = new BigDecimal(ck.getTaxprice()).multiply(new BigDecimal(qr.getCfree1()));
                        BigDecimal se = hsje.subtract(wsje);
                        ck.setQuantity(qr.getCfree1()).setBaseQuantity(qr.getCfree2()).setSubQuantity1(qr.getCfree3()).setSubQuantity2(qr.getCfree4()).setIcost(keepDecimals(wsje,4)).setIsum(keepDecimals(hsje,4)).setItaxprice(keepDecimals(se,4));
                        squantitySum = squantitySum.add(new BigDecimal(qr.getCfree2() == null ? "0" : qr.getCfree2()));
                        squantity1Sum = squantity1Sum.add(new BigDecimal(qr.getCfree3() == null ? "0" : qr.getCfree3()));
                        squantity2Sum = squantity2Sum.add(new BigDecimal(qr.getCfree4() == null ? "0" : qr.getCfree4()));
                        icostSum = icostSum.add(wsje);
                        isumSum = isumSum.add(hsje);
                        taxAmountSum = taxAmountSum.add(se);
                        qrChange.setSquantityQr(qr.getCfree1());
                        qrcList.add(qrChange);
                    }else if (qr.getSourcedetailId().equals(ck.getLineCode()) && null == qr.getCfree1()){
                        ck.setQuantity(null);
                        qrChange.setSquantityQr("0.0000000000");
                        qrcList.add(qrChange);
                    }
                }
            }
            Mono<String> changeM = qrcList.size() > 0 ? querenChangeRepository.saveAll(qrcList).collectList().thenReturn("") : Mono.just("");
            List<StockSaleousings> newt2 = t2.stream().filter(it -> null != it.getQuantity()).map(it->{StockSaleousings e = new StockSaleousings();BeanUtil.copyProperties(it,e,"id");return  e;}).collect(Collectors.toList());
            t1.setSquantity(keepDecimals(squantitySum, 10)).setSquantity1(keepDecimals(squantity1Sum, 10)).setSquantity2(keepDecimals(squantity2Sum, 10)).setIcost(keepDecimals(icostSum, 4)).setIsum(keepDecimals(isumSum, 4)).setTaxAmount(keepDecimals(taxAmountSum, 6));
            // 修改现存量
            Mono<List<StockCurrentstock>> xclM = currentstockRepository.findAllByIyearAndInvCodeIn(t1.getIyear(), changeList.stream().map(it -> it.getCinvode()).distinct().collect(Collectors.toList())).collectList().flatMap(xclList -> {
                for (StockCurrentstock xc : xclList) {
                    List<StockSaleousings> cgs = changeList.stream().filter(i -> i.getCinvode().equals(xc.getInvCode()) && (StrUtil.isNotBlank(i.getBatchId())?(i.getBatchId().equals(xc.getBatchId()) && i.getDvdate().equals(xc.getDvdate()) && i.getDpdate().equals(xc.getDpdate())):true)).collect(Collectors.toList());
                    if (cgs.size() > 0) { // 减去在途出库量
                        StockSaleousings ck = cgs.get(0);
                        BigDecimal ce = new BigDecimal(ck.getBaseQuantity()).subtract(new BigDecimal(ck.getCfree2()==null?"0":ck.getCfree2()));
                        BigDecimal s1 = xc.getBaseQuantity().add(ce);
                        xc.setBaseQuantity(s1);
                    }
                }
                return currentstockRepository.saveAll(xclList).collectList();
            });
            // 存在上游修改累计
            Mono<String> lyljM = lyB ? saleousingRepository.findByCcode(t2.get(0).getSourcecode()).zipWith(saleousingsRepository.findAllByCcode(t2.get(0).getSourcecode()).collectList()).flatMap(lyTips -> {
                StockSaleousing t11 = lyTips.getT1();
                List<StockSaleousings> t21 = lyTips.getT2();
                t11.setChukuStatus(null);
                for (StockSaleousings xh : t21) {
                    List<StockSaleousings> cgs = changeList.stream().filter(i -> i.getSourcedetailId().equals(xh.getLineCode())).collect(Collectors.toList());
                    if (cgs.size() > 0){
                        BigDecimal ce = new BigDecimal(cgs.get(0).getBaseQuantity()).subtract(new BigDecimal(cgs.get(0).getCfree2()==null?"0":cgs.get(0).getCfree2()));
                        BigDecimal xhlj = new BigDecimal(null == xh.getIsumChuku() ? "0" : xh.getIsumChuku()).subtract(ce);
                        xh.setIsumChuku(keepDecimals(xhlj, 10));
                    }
                }
                return saleousingRepository.save(t11).zipWith(saleousingsRepository.saveAll(t21).collectList()).flatMap(a->Mono.just(""));
            }) : Mono.just("");
            // 调整下游
            AtomicReference<List<String>> typesAR = new AtomicReference<>(new ArrayList<>());
            List<StockCurrentstock> xclList = new ArrayList<>();
            Mono<String> xychangeM = stockXyCsourceRepository.findAllByBillStyleAndCcode(t1.getBillStyle(), t1.getCcode()).collectList().flatMap(xyList -> {
                List<StockXyCsource> jhzxd = xyList.stream().filter(it -> it.getXyBillStyle().equals("JHZXD")).collect(Collectors.toList());
                List<StockXyCsource> wlshd = xyList.stream().filter(it -> it.getXyBillStyle().equals("WLSHD")).collect(Collectors.toList());
                List<StockXyCsource> xhd = xyList.stream().filter(it -> it.getXyBillStyle().equals("XHD")).collect(Collectors.toList());
                Mono<String> jhzxM = (checks.contains("JHZXD") && jhzxd.size() > 0) ? jhzxStockRepository.findAllByCcode(jhzxd.get(0).getXyccode()).collectList().flatMap(jhzxList -> {
                    if (jhzxList.size() == 0) return Mono.just("");
                    typesAR.get().add("JHZXD");
                    for (StockJhzxStock jhzxStock : jhzxList) {
                        List<StockSaleousings> cgs = changeList.stream().filter(i -> i.getCinvode().equals(jhzxStock.getCinvode()) &&  (StrUtil.isNotBlank(i.getBatchId())?( i.getBatchId().equals(jhzxStock.getBatchId()) && i.getDvdate().equals(jhzxStock.getDvdate()) && i.getDpdate().equals(jhzxStock.getDpdate())):true)).collect(Collectors.toList());
                        if (cgs.size() > 0) {
                            BigDecimal ce = new BigDecimal(cgs.get(0).getQuantity()).subtract(new BigDecimal(cgs.get(0).getCfree1() == null ? "0" : cgs.get(0).getCfree1()));
                            BigDecimal ce2 = new BigDecimal(cgs.get(0).getBaseQuantity()).subtract(new BigDecimal(cgs.get(0).getCfree2() == null ? "0" : cgs.get(0).getCfree2()));
                            BigDecimal q = new BigDecimal(null == jhzxStock.getQuantity() ? "0" : jhzxStock.getQuantity()).subtract(ce);
                            BigDecimal qb = new BigDecimal(null == jhzxStock.getBaseQuantity() ? "0" : jhzxStock.getBaseQuantity()).subtract(ce2);
                            jhzxStock.setQuantity(keepDecimals(q, 10));
                            jhzxStock.setBaseQuantity(keepDecimals(qb, 10));
                            if (cgs.get(0).getCfree1() == null && q.doubleValue() == 0)jhzxStock.setQuantity(null);
                        }
                    }
                    List<StockJhzxStock> newJh = jhzxList.stream().filter(it -> null != it.getQuantity()).map(it->{StockJhzxStock e = new StockJhzxStock();BeanUtil.copyProperties(it,e,"id");return  e;}).collect(Collectors.toList());
                    return jhzxStockRepository.deleteAll(jhzxList).thenReturn("1").flatMap(s -> jhzxStockRepository.saveAll(newJh).collectList().thenReturn("1"));
                }) : Mono.just("");
                Mono<String> wlshdM = (checks.contains("WLSHD") && wlshd.size() > 0) ? Mono.just("") : Mono.just("");
               /* Mono<String> xhdM = (checks.contains("XHD") && xhd.size() > 0) ? Mono.zip(saleousingRepository.findByCcode(xhd.get(0).getCcode()),
                        saleousingsRepository.findAllByCcode(xhd.get(0).getCcode()).collectList(),
                        (saleousingBiandongRepository.findFirstByCcodeLikeOrderByBaindongDateDesc(xhd.get(0).getCcode() + "-").flatMap(it -> {
                            int i = Integer.parseInt(it.getCcode().replace(xhd.get(0).getCcode() + "-", "")) + 1;
                            return Mono.just(i > 9 ? "" + i : "0" + i);
                        }).switchIfEmpty(Mono.just("01")))
                ).flatMap(xhTips -> {
                    // 变动
                    StockSaleousing modifyEntry = xhTips.getT1();
                    List<StockSaleousings> modifyEntityList = xhTips.getT2();
                    StockSaleousingBiandong bEntity = new StockSaleousingBiandong();
                    BeanUtils.copyProperties(modifyEntry, bEntity);
                    bEntity.setId(null).setCcode(bEntity.getCcode() + "-" + xhTips.getT3()).setBiandong("1").setBiandongUser(changeId).setBaindongDate(DateUtil.now());
                    List<StockSaleousingsBiandong> bList = modifyEntityList.stream().map(it -> {
                        StockSaleousingsBiandong saleousings = new StockSaleousingsBiandong();
                        BeanUtils.copyProperties(it, saleousings);
                        saleousings.setId(null).setCcode(bEntity.getCcode()).setBiandong("1").setBiandongUser(changeId).setBaindongDate(DateUtil.now());
                        return saleousings;
                    }).collect(Collectors.toList());
                    BigDecimal squantitySum1 = new BigDecimal(0);
                    BigDecimal squantity1Sum1 = new BigDecimal(0);
                    BigDecimal squantity2Sum1 = new BigDecimal(0);
                    BigDecimal icostSum1 = new BigDecimal(0);
                    BigDecimal isumSum1 = new BigDecimal(0);
                    BigDecimal taxAmountSum1 = new BigDecimal(0);
                    List<String> dels = new ArrayList<>();
                    for (StockSaleousings entry : modifyEntityList) {
                        List<StockSaleousings> cgs = changeList.stream().filter(it -> it.getSourcedetailId() == entry.getSourcedetailId()).collect(Collectors.toList());
                        BigDecimal ce2 = new BigDecimal(cgs.get(0).getBaseQuantity()).subtract(new BigDecimal(cgs.get(0).getCfree2() == null ? "0" : cgs.get(0).getCfree2()));
                        if (cgs.size() > 0) {
                            BigDecimal ce = new BigDecimal(cgs.get(0).getQuantity()).subtract(new BigDecimal(cgs.get(0).getCfree1() == null ? "0" : cgs.get(0).getCfree1()));
                            BigDecimal ce3 = new BigDecimal(cgs.get(0).getSubQuantity1()).subtract(new BigDecimal(cgs.get(0).getCfree3() == null ? "0" : cgs.get(0).getCfree3()));
                            BigDecimal ce4 = new BigDecimal(cgs.get(0).getSubQuantity2()).subtract(new BigDecimal(cgs.get(0).getCfree4() == null ? "0" : cgs.get(0).getCfree4()));
                            BigDecimal q = new BigDecimal(entry.getQuantity()).subtract(ce);
                            BigDecimal qb = new BigDecimal(entry.getBaseQuantity()).subtract(ce2);
                            BigDecimal qb1 = new BigDecimal(entry.getSubQuantity1()).subtract(ce3);
                            BigDecimal qb2 = new BigDecimal(entry.getSubQuantity2()).subtract(ce4);
                            BigDecimal wsje = new BigDecimal(entry.getPrice()).multiply(q);
                            BigDecimal hsje = new BigDecimal(entry.getTaxprice()).multiply(q);
                            BigDecimal se = hsje.subtract(wsje);

                            entry.setQuantity(q.setScale(10).toString()).setBaseQuantity(qb.setScale(10).toString()).setSubQuantity1(qb1.setScale(10).toString()).setSubQuantity2(qb2.setScale(10).toString())
                                    .setIcost(wsje.setScale(4,RoundingMode.HALF_UP).toString()).setIsum(hsje.setScale(4,RoundingMode.HALF_UP).toString()).setItaxprice(se.setScale(6,RoundingMode.HALF_UP).toString());
                            squantitySum1 = squantitySum1.add(ce2);
                            squantity1Sum1 = squantity1Sum1.add(ce3);
                            squantity2Sum1 = squantity2Sum1.add(ce4);
                            icostSum1 = icostSum1.add(wsje);
                            isumSum1 = isumSum1.add(hsje);
                            taxAmountSum1 = taxAmountSum1.add(se);
                            entry.setBiandong("1");
                        }
                        // 存在问题
                        if (modifyEntityList.size() != changeList.size() && cgs.size() == 0)
                            dels.add(entry.getLineCode());
                        for (StockCurrentstock stockCurrentstock : xclList) {
                            if (entry.getCinvode().equals(stockCurrentstock.getInvCode()) && (StrUtil.isNotBlank(entry.getBatchId()) ? (entry.getBatchId().equals(stockCurrentstock.getBatchId()) && entry.getDvdate().equals(stockCurrentstock.getDvdate()) && entry.getDpdate().equals(stockCurrentstock.getDpdate())) : true)) {
                                BigDecimal l1 = stockCurrentstock.getBaseQuantity().add(ce2);
                                stockCurrentstock.setBaseQuantity(l1);
                        }
                    }
                    modifyEntry.setBiandong("1").setSquantity(keepDecimals(squantitySum1, 10)).setSquantity1(keepDecimals(squantity1Sum1, 10)).setSquantity2(keepDecimals(squantity2Sum1, 10)).setIcost(keepDecimals(icostSum1, 4)).setIsum(keepDecimals(isumSum1, 4)).setTaxAmount(keepDecimals(taxAmountSum1, 6));
                    return Mono.zip(saleousingBiandongRepository.save(bEntity), saleousingBiandongsRepository.saveAll(bList).collectList()).flatMap(tips2 -> {
                        boolean b = modifyEntityList.stream().mapToDouble(it -> new BigDecimal(StrUtil.isNotBlank(it.getBaseQuantity()) ? it.getBaseQuantity() : "0").doubleValue()).sum() == modifyEntityList.stream().mapToDouble(it -> new BigDecimal(StrUtil.isNotBlank(it.getIsumChuku()) ? it.getBaseQuantity() : "0").doubleValue()).sum();
                        modifyEntry.setChukuStatus(b ? "1" : null);
                        Mono<StockSaleousing> save = saleousingRepository.save(modifyEntry);
                        // 添加之前检查变动数量
                        Mono<List<StockSaleousings>> saveList = modifyEntityList.size() > 0 ? (saleousingsRepository.saveAll(modifyEntityList).collectList()) : Mono.just(new ArrayList<StockSaleousings>());
                        Mono<String> delM = dels.size() > 0 ? saleousingsRepository.deleteByLineCodeIn(dels).thenReturn("") : Mono.just("");
                        return Mono.zip(save, saveList, delM, currentstockRepository.saveAll(xclList).collectList().thenReturn("")).flatMap(tips3 -> Mono.just(""));
                    });
                }) : Mono.just("");*/
                return Mono.zip(jhzxM,wlshdM).flatMap(z -> Mono.just(""));
            });
            return  Mono.zip(saleousingRepository.save(t1),(saleousingsRepository.deleteAll(t2).thenReturn("").flatMap(s->saleousingsRepository.saveAll(newt2).collectList()).thenReturn("")),/*xclM,*/lyljM,xychangeM,changeM).flatMap(v->Mono.just(""));
        });
    }
    /****************************** 确认单 ****************************/

    /**
     * ID查找主表
     * @param ccode
     * @return
     */
    @PostMapping("findStockWareByCcode")
    public Mono<R> findStockWareByCcode(String ccode){
        return saleousingRepository.findByCcode(ccode).map(a->R.ok().setResult(a)).defaultIfEmpty(R.ok().setResult(""));
    }

    @PostMapping("reviewSetCGRKG")
    @Transactional
    public Mono<R> reviewSetCGRKG(@RequestBody StockSaleousing pojo) {
        return saleousingRepository.save(pojo).map(a->R.ok().setResult(a));
    }

    @PostMapping("reviewSetCGRKGMx")
    @Transactional
    public Mono<R> reviewSetCGRKGMx(@RequestBody List<StockSaleousings> pojo) {
        return saleousingsRepository.saveAll(pojo).collectList().map(a->R.ok().setResult(a));
    }


    @PostMapping("xsfp")
    @Transactional
    public Mono<R> saveFp(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        Map<String, Object> obj = assemblyCorrespondingRulesData(map);
        StockSaleousing master = (StockSaleousing) obj.get("master");
        List<StockSaleousings> sub = (List<StockSaleousings>) obj.get("sub");
        boolean b = master.getId() == null;
        return saleousingRepository.save(master).flatMap(db -> b ? addFp(db, sub) : editFp(db, sub)).map(o -> R.ok());
    }
    private Mono<String> addFp(StockSaleousing db, List<StockSaleousings> sub) {
        List<StockSaleousings> xsckd = sub.stream().filter(it -> StrUtil.isNotBlank(it.getSourcecode()) && StrUtil.isNotBlank(it.getSourcetype()) && (it.getSourcetype().equals("XSCKD") || it.getSourcetype().equals("XHD") || it.getSourcetype().equals("QCXHD")|| it.getSourcetype().equals("XSDD"))).collect(Collectors.toList());
        Mono<String> undateMone =xsckd.size() == 0? Mono.just(""):saleousingRepository.findByCcode(xsckd.get(0).getSourcecode()).zipWith(saleousingsRepository.findAllByCcode(xsckd.get(0).getSourcecode()).collectList()).flatMap(tips->{ // 出库单修改销货数量
            StockSaleousing xhEntity = tips.getT1();
            List<StockSaleousings> list = tips.getT2();
            BigDecimal ljNum = new BigDecimal(0);
            for (StockSaleousings saleousings : list) {
                BigDecimal add = new BigDecimal((StrUtil.isNotBlank(saleousings.getIsumFapiao()) ? saleousings.getIsumFapiao() : "0"));
                List<StockSaleousings> collect = sub.stream().filter(it -> it.getSourcedetailId().equals(saleousings.getLineCode())).collect(Collectors.toList());
                if (collect.size() > 0){
                    add = add.add(new BigDecimal(collect.get(0).getBaseQuantity()));
                }else  if (new BigDecimal(saleousings.getPrice()).doubleValue() == 0 ){// 单价为0 数量计入
                    add = new BigDecimal(saleousings.getBaseQuantity()).abs();
                }
                saleousings.setIsumFapiao(keepDecimals(add,10));
                ljNum =  ljNum.add(add);
            }
            boolean b = list.stream().mapToDouble(it -> Double.parseDouble(it.getBaseQuantity())).sum() == ljNum.doubleValue();
            xhEntity.setFapiaoStatus(b?"1":null);
            return saleousingRepository.save(xhEntity).flatMap(e->saleousingsRepository.saveAll(list).collectList().thenReturn(""));
        });
        return saleousingsRepository.saveAll(sub).collectList().thenReturn(db).flatMap(z -> undateMone.flatMap(a -> Mono.just("")));
    }

    private Mono<String> editFp(StockSaleousing db, List<StockSaleousings> sub) {
        return saleousingsRepository.findAllByBillStyleAndCcodeOrderByDdateAscCcodeAscLineIdAsc(db.getBillStyle(), db.getCcode()).collectList().flatMap(dblist -> {
            List<String> strings = dblist.stream().map(it -> it.getCinvode()).distinct().collect(Collectors.toList());
            for (StockSaleousings saleousings : sub) {
                saleousings.setId(null);
                strings.add(saleousings.getCinvode());
            }
            List<StockSaleousings> xsckd = sub.stream().filter(it -> StrUtil.isNotBlank(it.getSourcecode()) && StrUtil.isNotBlank(it.getSourcetype()) && (it.getSourcetype().equals("XSCKD") || it.getSourcetype().equals("XHD")|| it.getSourcetype().equals("QCXHD"))).collect(Collectors.toList());
            Mono<List<StockCurrentstock>> relifeMone =xsckd.size() == 0? Mono.just(new ArrayList<>()):saleousingRepository.findByCcode(xsckd.get(0).getSourcecode()).zipWith(saleousingsRepository.findAllByCcode(xsckd.get(0).getSourcecode()).collectList()).flatMap(tips->{ // 出库单修改销货数量
                StockSaleousing xhEntity = tips.getT1();
                List<StockSaleousings> list = tips.getT2();
                BigDecimal ljNum = new BigDecimal(0);
                for (StockSaleousings saleousings : list) {
                    // 先减除后添加
                    List<StockSaleousings> dbSales = dblist.stream().filter(it -> it.getSourcedetailId().equals(saleousings.getLineCode())).collect(Collectors.toList());
                    List<StockSaleousings> collect = sub.stream().filter(it -> it.getSourcedetailId().equals(saleousings.getLineCode())).collect(Collectors.toList());
                    BigDecimal add = new BigDecimal((StrUtil.isNotBlank(saleousings.getIsumFapiao()) ? saleousings.getIsumFapiao() : "0"));
                    if (db.getBdocumStyle().equals("0")){
                        if (dbSales.size() > 0) add = add.subtract(new BigDecimal(dbSales.get(0).getBaseQuantity()));
                        if (collect.size() > 0) add = add.add(new BigDecimal(collect.get(0).getBaseQuantity()));
                    }else {
                        if (dbSales.size() > 0) add = add.subtract(new BigDecimal(dbSales.get(0).getBaseQuantity()).abs());
                        if (collect.size() > 0) add = add.add(new BigDecimal(collect.get(0).getBaseQuantity()).abs());
                    }
                    ljNum =  ljNum.add(add);
                    saleousings.setIsumFapiao(keepDecimals(add,10));
                }
                boolean b = list.stream().mapToDouble(it -> Double.parseDouble(it.getBaseQuantity())).sum() == ljNum.doubleValue();
                if (db.getBdocumStyle().equals("0")){
                    xhEntity.setFapiaoStatus(b?"1":null);
                }else {
                    xhEntity.setFapiaoStatus(b?"1":null);
                }
                return saleousingRepository.save(xhEntity).flatMap(e->saleousingsRepository.saveAll(list).collectList().thenReturn(new ArrayList<>()));
            });
            return relifeMone.flatMap(d -> saleousingsRepository.deleteAll(dblist).thenReturn("").flatMap(c -> saleousingsRepository.saveAll(sub).collectList().thenReturn("")));
        });
    }


    @PostMapping("findReceiptList")
    public Mono<R> findReceiptList(@RequestBody Map map){
        if (map.keySet().size() != 5)return Mono.just(R.ok(new ArrayList<>()));
        String year = map.get("year").toString();
        String type = map.get("type").toString();
        String qValue = map.get("qValue").toString();
        String model = map.get("model").toString();
        String style = map.get("style").toString();
        Mono<List<String>> listMono = null;
        if (model.equals("XS")){
            listMono = saleousingRepository.findAllByBillStyleAndIyearAndBdocumStyleAndCcodeLikeOrderByDdateAscCcodeAsc(type, year, style, "%" + qValue + "%").collectList().map(list->list.stream().map(it->it.getCcode()).distinct().collect(Collectors.toList()));
        }else if (model.equals("CG")){
            listMono = warehousingRepository.findAllByBillStyleAndIyearAndBdocumStyleAndCcodeLikeOrderByDdateAscCcodeAsc(type, year, style, "%" + qValue + "%").collectList().map(list->list.stream().map(it->it.getCcode()).distinct().collect(Collectors.toList()));
        }else if(model.equals("JS")){
            listMono = jiesuanRepository.findAllByIyearAndBdocumStyleAndCcodeLikeOrderByDdateAscCcodeAsc(year, style, "%" + qValue + "%").collectList().map(list->list.stream().map(it->it.getCcode()).distinct().collect(Collectors.toList()));
        }else if(model.equals("WL")){
            listMono = wuLiuRepository.findAllByIyearAndCcodeLikeOrderByDdateAscCcodeAsc(year, "%" + qValue + "%").collectList().map(list->list.stream().map(it->it.getCcode()).distinct().collect(Collectors.toList()));
        }else if(model.equals("JR")){
            listMono = borrowRepository.findAllByBorrowStyleAndIyearAndCcodeLikeOrderByDdateAscCcodeAsc(type,year, "%" + qValue + "%").collectList().map(list->list.stream().map(it->it.getCcode()).distinct().collect(Collectors.toList()));
        }else {
            listMono = Mono.just(new ArrayList<>());
        }
        return listMono.map(a->R.ok().setResult(a));
    }


    @PostMapping("copyReceipt")
    @Transactional
    public Mono<R> copyReceipt(@RequestBody Map map){
        if (map.keySet().size() == 0)return Mono.just(R.ok(new ArrayList<>()));
        String code = map.get("code").toString();
        String ddte = map.get("date").toString();
        String newCode = map.get("newCode").toString();
        return Mono.zip(saleousingRepository.findByCcode(code),saleousingsRepository.findAllByCcode(code).collectList())
                .flatMap(
                tips-> {
                    StockSaleousing t1 = tips.getT1();
                    List<StockSaleousings> t2 = tips.getT2();
                    t1.setId(null);
                    t1.setCcode(newCode);
                    t1.setCcodePl(null);
                    t1.setDdate(ddte);
                    for (StockSaleousings saleousings : t2) {
                        saleousings.setId(null);
                        saleousings.setCcode(newCode);
                        saleousings.setDdate(ddte);
                        saleousings.setSourcecode(null);
                        saleousings.setSourcetype(null);
                        saleousings.setSourcedate(null);
                        saleousings.setSourcedetailId(null);
                    }
                    return saleousingRepository.save(t1).thenReturn("").zipWith(saleousingsRepository.saveAll(t2).collectList()).flatMap(tips2->Mono.just(R.ok()));
                }
        );
    }

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
        String cangku=map.get("cangku").toString();
        Map<String, String> pageSearch = ((HashMap<String,  String>) map.get("pageSearch"));
        // 按添加日期
        return saleousingRepository.findMainList2(type,strDate,endDate).collectList()
                .flatMap(list->{

                    if(pageSearch!=null&&StrUtil.isNotBlank(pageSearch.get("selectValue"))){
                        if(pageSearch.get("selectType").equals("ccode")){
                            list=list.stream().filter(a->a.getCcode().contains(pageSearch.get("selectValue"))).collect(Collectors.toList());
                        }else if(pageSearch.get("selectType").equals("supName")){
                            list=list.stream().filter(a->a.getCustName().contains(pageSearch.get("selectValue"))).collect(Collectors.toList());
                        }else if(pageSearch.get("selectType").equals("custName")){
                            list=list.stream().filter(a->a.getCustName().contains(pageSearch.get("selectValue"))).collect(Collectors.toList());
                        }else if(pageSearch.get("selectType").equals("deptName")){
                            list=list.stream().filter(a->a.getDeptName().contains(pageSearch.get("selectValue"))).collect(Collectors.toList());
                        }else if(pageSearch.get("selectType").equals("cmakerName")){
                            list=list.stream().filter(a->a.getCmakerName().contains(pageSearch.get("selectValue"))).collect(Collectors.toList());
                        }
                    }
                    return Mono.just(list);
                }).map(a->R.ok().setResult(a));
    }

}
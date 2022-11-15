package org.boozsoft.rest.stock;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.boozsoft.domain.entity.stock.StockBeginBalance;
import org.boozsoft.domain.entity.stock.StockSaleousings;
import org.boozsoft.domain.entity.stock.StockWarehousings;
import org.boozsoft.domain.ro.StockCostAccRo;
import org.boozsoft.domain.vo.stock.StockAccSheetVo;
import org.boozsoft.domain.vo.stock.StockCurrentLackVo;
import org.boozsoft.domain.vo.stock.StockVo;
import org.boozsoft.repo.stock.*;
import org.boozsoft.service.stock.StockXCLService;
import org.boozsoft.util.BigDecimalUtils;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/stock/currents")
public class StockCurrentsController {

    @Autowired
    StockCurrentstockRepository currentstockRepository;
    @Autowired
    DatabaseClient client;
    @Autowired
    StockXCLService stockXCLService;
    @Autowired
    private StockRepository stockRepository;
    @Autowired
    private StockBeginBalanceRepository beginBalanceRepository;

    //***************************************** 现存量查询：仓库、存货、失效日期 ***************************************

    /**
     * 按行校验存货现存量
     * @param map
     * @return
     */
    @PostMapping("verifyStockRowXCL")
    public Mono<R> verifyStockRowXCL(@RequestBody Map map) {
        String queryType=map.get("queryType").toString();
        // 存货
        String cinvode=map.get("cinvode").toString();
        // 仓库
        String cwhcode=map.get("cwhcode")==null?"":map.get("cwhcode").toString();
        // 批次
        String batchId=map.get("batchId")==null?"":map.get("batchId").toString();
        // 生产日期
        String dpdate=map.get("dpdate")==null?"":map.get("dpdate").toString();
        // 失效日期
        String dvdate=map.get("dvdate")==null?"":map.get("dvdate").toString();
        // 参数设置：入库保存状态就是现存量 标志 1是查询所有 0 查询已审核
        String rkBcheck=map.get("rkBcheck")==null?"0":map.get("rkBcheck").toString();
        String ckBcheck=map.get("ckBcheck")==null?"0":map.get("ckBcheck").toString();
        String iyear=map.get("iyear").toString();

        String bcheck1="";String bcheck2="";String ztrkSql="";
        // 入库：0 查已审核的
        if(StrUtil.isBlank(rkBcheck)||rkBcheck.equals("0")){ bcheck1=" and s.bcheck='1' "; ztrkSql=" and sws.bcheck='1' ";}
        // 出库  0 查已审核的
        if(StrUtil.isBlank(ckBcheck)||ckBcheck.equals("0")){ bcheck2=" and s.bcheck='1' "; }
        String finalBcheck = bcheck1;
        String finalBcheck1 = bcheck2;
        String finalZtrkSql = ztrkSql;

        String qcsql="select coalesce(cast(s.base_quantity as float), '0') base_quantity,s.iyear,s.stock_id as cinvode,s.batch_number as batch_id," +
                "s.dpdate,s.dvdate,s.stock_cangku_id as cwhcode from stock_begin_balance s where s.bcheck = '1' and s.iyear='"+iyear+"' ";
        return client.sql(qcsql).fetch().all().collectList()
        .flatMap(qclist->{
            List<StockCurrentLackVo> newqclist= JSON.parseArray(JSON.toJSONString(qclist), StockCurrentLackVo.class);

            // 采购入库、其他入库、盘盈入库单、调拨入库、形态转换入库
            String rksql="select coalesce(cast(s.base_quantity as float),'0')-coalesce(cast(s.isum_ruku as float), '0') base_quantity,s.iyear,s.cwhcode,s.batch_id,s.dpdate,s.dvdate,s.cinvode from stock_warehousings s where " +
                    " s.bill_style in ('CGRKD','QTRKD','PYRKD','DBRKD','XTZHRKD') and s.iyear='"+iyear+"' "+ finalBcheck +" ";
            return client.sql(rksql).fetch().all().collectList()
            .flatMap(rklist->{
                List<StockCurrentLackVo> newrklist=JSON.parseArray(JSON.toJSONString(rklist), StockCurrentLackVo.class);

                // 销售出库、其他出库、材料领用、盘亏出库、调拨出库、形态转换出库
                String cksql="select coalesce(cast(s.base_quantity as float),'0')-coalesce(cast(s.isum_chuku as float), '0') base_quantity,s.iyear,s.cwhcode,s.batch_id,s.dpdate,s.dvdate,s.cinvode from stock_saleousings s " +
                        " where s.bill_style in ('XSCKD','QTCKD','CLLYCKD','PKCKD','DBCKD','XTZHCKD') and s.iyear='"+iyear+"' "+ finalBcheck1 +" ";
                return client.sql(cksql).fetch().all().collectList()
                .flatMap(cklist->{
                    List<StockCurrentLackVo> newcklist=JSON.parseArray(JSON.toJSONString(cklist), StockCurrentLackVo.class);

                    BigDecimal qcNumber= BigDecimal.valueOf(0);
                    BigDecimal rkNumber= BigDecimal.valueOf(0);
                    BigDecimal ckNumber= BigDecimal.valueOf(0);

                    if(newqclist.size()>0){
                        AtomicReference<List<StockCurrentLackVo>> collect1 = new AtomicReference<>(newqclist.stream().filter(a->a.getCinvode().equals(cinvode)).collect(Collectors.toList()));
                        if(StrUtil.isNotBlank(cwhcode)){
                            collect1.set(collect1.get().stream().filter(qc -> qc.getCwhcode().equals(cwhcode)).collect(Collectors.toList()));
                        }
                        if(StrUtil.isNotBlank(batchId)){
                            collect1.set(collect1.get().stream().filter(qc -> qc.getBatchId().equals(batchId)).collect(Collectors.toList()));
                        }
                        if(StrUtil.isNotBlank(dvdate)){
                            collect1.set(collect1.get().stream().filter(qc -> qc.getDvdate().equals(dvdate)).collect(Collectors.toList()));
                        }
                        qcNumber=collect1.get().stream().map(StockCurrentLackVo::getBaseQuantity).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                    }
                    if(newrklist.size()>0){
                        AtomicReference<List<StockCurrentLackVo>> collect1 = new AtomicReference<>(newrklist.stream().filter(a->a.getCinvode().equals(cinvode)).collect(Collectors.toList()));
                        if(StrUtil.isNotBlank(cwhcode)){
                            collect1.set(collect1.get().stream().filter(qc -> qc.getCwhcode().equals(cwhcode)).collect(Collectors.toList()));
                        }
                        if(StrUtil.isNotBlank(batchId)){
                            collect1.set(collect1.get().stream().filter(qc -> qc.getBatchId().equals(batchId)).collect(Collectors.toList()));
                        }
                        if(StrUtil.isNotBlank(dvdate)){
                            collect1.set(collect1.get().stream().filter(qc -> qc.getDvdate().equals(dvdate)).collect(Collectors.toList()));
                        }
                        rkNumber=collect1.get().stream().map(StockCurrentLackVo::getBaseQuantity).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                    }
                    if(newcklist.size()>0){
                        AtomicReference<List<StockCurrentLackVo>> collect1 = new AtomicReference<>(newcklist.stream().filter(a->a.getCinvode().equals(cinvode)).collect(Collectors.toList()));
                        if(StrUtil.isNotBlank(cwhcode)){
                            collect1.set(collect1.get().stream().filter(qc -> qc.getCwhcode().equals(cwhcode)).collect(Collectors.toList()));
                        }
                        if(StrUtil.isNotBlank(batchId)){
                            collect1.set(collect1.get().stream().filter(qc -> qc.getBatchId().equals(batchId)).collect(Collectors.toList()));
                        }
                        if(StrUtil.isNotBlank(dvdate)){
                            collect1.set(collect1.get().stream().filter(qc -> qc.getDvdate().equals(dvdate)).collect(Collectors.toList()));
                        }
                        ckNumber=collect1.get().stream().map(StockCurrentLackVo::getBaseQuantity).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                    }

                    BigDecimal xcl=qcNumber.add(rkNumber).subtract(ckNumber);

                    // 在途到货
                    Mono<List<StockCurrentLackVo>> dhMidWay = stockXCLService.findByDaoHuoMidWay(cinvode, iyear,"")
                            .flatMapMany(dhlist->Flux.fromIterable(dhlist)).collectList();

                    // 在途入库
                    Mono<List<StockCurrentLackVo>> rukuMidWay = stockXCLService.findByRukuMidWay(cinvode, iyear,"",finalZtrkSql)
                            .flatMapMany(dhlist->Flux.fromIterable(dhlist)).collectList();

                    // 在途销货
                    Mono<List<StockCurrentLackVo>> xhMidWay = stockXCLService.findByXiaoHuoMidWay(cinvode, iyear,"")
                            .flatMapMany(dhlist->Flux.fromIterable(dhlist)).collectList();

                    // 在途出库
                    Mono<List<StockCurrentLackVo>> ckMidWay = stockXCLService.findByChukuMidWay(cinvode, iyear,"")
                            .flatMapMany(dhlist->Flux.fromIterable(dhlist)).collectList();

                    return Mono.zip(dhMidWay,rukuMidWay,xhMidWay,ckMidWay)
                    .map(a->{
                        List<StockCurrentLackVo> dhlist = a.getT1();
                        List<StockCurrentLackVo> rklist2 = a.getT2();
                        List<StockCurrentLackVo> xhlist = a.getT3();
                        List<StockCurrentLackVo> cklist2 = a.getT4();

                        if(StrUtil.isNotBlank(cinvode)){
                            dhlist=dhlist.stream().filter(d->d.getCinvode().equals(cinvode)).collect(Collectors.toList());
                            rklist2=rklist2.stream().filter(d->d.getCinvode().equals(cinvode)).collect(Collectors.toList());
                            xhlist=xhlist.stream().filter(d->d.getCinvode().equals(cinvode)).collect(Collectors.toList());
                            cklist2=cklist2.stream().filter(d->d.getCinvode().equals(cinvode)).collect(Collectors.toList());
                        }
                        if(StrUtil.isNotBlank(cwhcode)){
                            dhlist=dhlist.stream().filter(d->d.getCwhcode().equals(cwhcode)).collect(Collectors.toList());
                            rklist2=rklist2.stream().filter(d->d.getCwhcode().equals(cwhcode)).collect(Collectors.toList());
                            xhlist=xhlist.stream().filter(d->d.getCwhcode().equals(cwhcode)).collect(Collectors.toList());
                            cklist2=cklist2.stream().filter(d->d.getCwhcode().equals(cwhcode)).collect(Collectors.toList());
                        }
                        // 批次现存量查询所需
                        if(StrUtil.isNotBlank(batchId)){
                            dhlist=dhlist.stream().filter(d->d.getBatchId().equals(batchId)).collect(Collectors.toList());
                            rklist2=rklist2.stream().filter(d->d.getBatchId().equals(batchId)).collect(Collectors.toList());
                            xhlist=xhlist.stream().filter(d->d.getBatchId().equals(batchId)).collect(Collectors.toList());
                            cklist2=cklist2.stream().filter(d->d.getBatchId().equals(batchId)).collect(Collectors.toList());
                        }
                        if(StrUtil.isNotBlank(dvdate)){
                            dhlist=dhlist.stream().filter(d->d.getDvdate().equals(dvdate)).collect(Collectors.toList());
                            rklist2=rklist2.stream().filter(d->d.getDvdate().equals(dvdate)).collect(Collectors.toList());
                            xhlist=xhlist.stream().filter(d->d.getDvdate().equals(dvdate)).collect(Collectors.toList());
                            cklist2=cklist2.stream().filter(d->d.getDvdate().equals(dvdate)).collect(Collectors.toList());
                        }

                        BigDecimal dh = dhlist.stream().map(StockCurrentLackVo::getMidWayDh).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                        BigDecimal rk = rklist2.stream().map(StockCurrentLackVo::getMidWayRk).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                        BigDecimal xh = xhlist.stream().map(StockCurrentLackVo::getMidWayXh).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                        BigDecimal ck = cklist2.stream().map(StockCurrentLackVo::getMidWayCk).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);

                        rk=rkBcheck.equals("1")?BigDecimal.ZERO:rk;
                        ck=ckBcheck.equals("1")?BigDecimal.ZERO:ck;
                        BigDecimal keyong=xcl.add(dh.add(rk)).subtract(xh.add(ck));
                        return queryType.equals("xcl")?xcl:keyong;
                    });
                });
            });
        }).map(R::ok);
    }

    /**
     * 按集合校验-存货list现存量或可用量
     * @param map
     * @return
     */
    @PostMapping("verifyStockXCLList")
    public Mono<R> verifyStockXCLList(@RequestBody Map map) {
        // 参数设置：入库保存状态就是现存量 标志 1是查询所有 0 查询已审核
        String rkBcheck=map.get("rkBcheck")==null?"0":map.get("rkBcheck").toString();
        String ckBcheck=map.get("ckBcheck")==null?"0":map.get("ckBcheck").toString();
        // 1红字  0蓝字
        String bdocumStyle=map.get("bdocumStyle").toString();
        String iyear=map.get("iyear").toString();
        // xcl\keyong
        String queryType=map.get("queryType").toString();
        List<StockCurrentLackVo> stockWarehousingsList= JSON.parseArray(map.get("list").toString(),StockCurrentLackVo.class);
        return stockXCLService.verifyStockXCLList("",queryType,rkBcheck,ckBcheck,bdocumStyle,iyear,stockWarehousingsList);
    }

    /**
     * 批次选择弹框 查询指定仓库下存货批号列表-现存量
     * cwhcode：必传
     * cinvode：必传
     * @param map
     * @return
     */
    @PostMapping("batchSelectorXCLList")
    public Mono<R> batchSelectorXCLList(@RequestBody Map map) {
        // xcl \ keyong
        String queryType=map.get("queryType").toString();
        String cwhcode=map.get("cwhcode").toString();
        String cinvode=map.get("cinvode").toString();
        // 参数设置：入库保存状态就是现存量 标志 1是查询所有 0 查询已审核
        String rkBcheck=map.get("rkBcheck")==null?"0":map.get("rkBcheck").toString();
        String ckBcheck=map.get("ckBcheck")==null?"0":map.get("ckBcheck").toString();
        String bdocumStyle=map.get("bdocumStyle").toString();
        String iyear=map.get("iyear").toString();
        String unitId=map.get("unitId").toString();

        // 做过单据的存货
        String sql1="select distinct temp.* from (select sbb.stock_id as stock_num, sbb.stock_cangku_id as cwhcode, sbb.batch_number as batch_id, sbb.dpdate, sbb.dvdate\n" +
                "               from stock_begin_balance sbb\n" +
                "               where sbb.stock_id = '"+cinvode+"'\n" +
                "                 and sbb.stock_cangku_id = '"+cwhcode+"' union all\n" +
                "               select distinct sws.cinvode, sws.cwhcode, sws.batch_id, sws.dpdate, sws.dvdate\n" +
                "               from stock_warehousings sws\n" +
                "               where sws.cinvode = '"+cinvode+"'\n" +
                "                 and sws.cwhcode = '"+cwhcode+"' union all\n" +
                "               select distinct sss.cinvode, sss.cwhcode, sss.batch_id, sss.dpdate, sss.dvdate\n" +
                "               from stock_saleousings sss\n" +
                "               where sss.cinvode = '"+cinvode+"'\n" +
                "                 and sss.cwhcode = '"+cwhcode+"') as temp order by temp.batch_id \n";
        return client.sql(sql1).fetch().all().collectList()
        .flatMapMany(skList-> Flux.fromIterable(JSON.parseArray(JSON.toJSONString(skList), StockCurrentLackVo.class)))
        .doOnNext(p -> p.setBaseQuantity(BigDecimal.ZERO))
        .index((i,it)->{
            List<StockCurrentLackVo> templist=new ArrayList<>();
            templist.add(it);
            return stockXCLService.verifyStockXCLList("",queryType,rkBcheck, ckBcheck, bdocumStyle, iyear, templist)
            .flatMap(tt->{
                List<StockCurrentLackVo> monolist= (List<StockCurrentLackVo>) tt.getResult();
                BigDecimal xcl = monolist.stream().map(StockCurrentLackVo::getXcl).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                BigDecimal keyong = monolist.stream().map(StockCurrentLackVo::getKeyong).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                it.setXcl(xcl);
                it.setKeyong(keyong);
                it.setUnitId(unitId);
                return Mono.just(it);
            });
        })
        .flatMap(it->it)
        .collectList()
        .map(tt->tt.stream().sorted(Comparator.comparing(StockCurrentLackVo::getDvdate)).collect(Collectors.toList()))
        .map(R::ok);
    }
    //****************************************** 重写 ****************************************

    @Autowired
    private StockBeginBalanceRepository stockBeginBalanceRepository;

    @Autowired
    private StockWarehousingsRepository warehousingsRepository;

    @Autowired
    private StockSaleousingsRepository saleousingsRepository;

    /**
     * 查询指定仓库下存货可用量
     * year 年度
     * mode 查询方式 xcl 现存量 ztl 在途量 all 可用量、
     * checks 是否保存时调整现存量标记 kcCgrkCheck 入库 kcXsckCheck 出库
     * parameters 仓库存货信息 ['仓库1ID=存货编码=批号=生效日期=失效日期=唯一标记']
     * @param map
     * @return [{parameter:long}]
     */
    @PostMapping("findAvailability")
    public Mono<R> findAvailability(@RequestBody Map map) {
        if (map.keySet().size() != 4) return Mono.just(R.error());
        String year = map.get("year").toString();
        String mode = map.get("mode").toString();
        List<String> checks = JSON.parseArray(map.get("checks").toString(),String.class);
        List<String> parameters = JSON.parseArray(map.get("parameters").toString(),String.class);
        return Flux.fromIterable(parameters).flatMap(parameter->{
            String[] pram = parameter.split("=");
            if (pram[2].equals("undefined") || pram[2].equals("null"))pram[2] = null;
            Boolean cgB = checks.contains("cg");
            Boolean xsB = checks.contains("xs");
            Mono<Long> xclM = Mono.just(pram).flatMap(pram2-> {
                Mono<Long> qcye = (pram2[2] == null ? stockBeginBalanceRepository.findAllByIyearAndBcheckAndStockId(year, "1", pram2[1]) : stockBeginBalanceRepository.findAllByIyearAndBcheckAndStockIdAndBatchNumberAndDpdateAndDvdateOrderByDvdateDesc(year, "1", pram2[1], pram2[2], pram2[3], pram2[4])).filter(it -> pram2[0] == null ? true : (null != it.getCwhcode1() && pram2[0].equals(it.getCwhcode1()))).collectList()
                        .flatMap(list -> Mono.just(list.size() == 0 ? 0L : list.stream().mapToLong(e -> e.getBaseQuantity().longValue()).sum()));
                Mono<Long> rk = (pram2[2] == null ? warehousingsRepository.findAllByIyearAndCinvodeAndBillStyleIn(year,  pram2[1], Arrays.asList("CGRKD", "QTRKD")) : warehousingsRepository.findAllByIyearAndCinvodeAndBatchIdAndDpdateAndDvdateAndBillStyleInOrderByDvdateDesc(year,  pram2[1], pram2[2], pram2[3], pram2[4], Arrays.asList("CGRKD", "QTRKD"))).filter(it -> (cgB?true: StrUtil.equals(it.getBcheck(),"1")) && (pram2[0] == null ? true : null != it.getCwhcode1()) && pram2[0].equals(it.getCwhcode1())).collectList()
                        .flatMap(list -> Mono.just(list.size() == 0 ? 0L : list.stream().mapToLong(e -> new BigDecimal(StrUtil.isNotBlank(e.getBaseQuantity()) ? e.getBaseQuantity() : "0").longValue()).sum()));
                Mono<Long> etcrk = (pram2[2] == null ? warehousingsRepository.findAllByIyearAndCinvodeAndBillStyleIn(year,  pram2[1], Arrays.asList("PYRKD","DBRKD","XTZHRKD")) : warehousingsRepository.findAllByIyearAndCinvodeAndBatchIdAndDpdateAndDvdateAndBillStyleInOrderByDvdateDesc(year,  pram2[1], pram2[2], pram2[3], pram2[4], Arrays.asList("PYRKD","DBRKD","XTZHRKD"))).filter(it -> (xsB?true:StrUtil.equals(it.getBcheck(),"1")) && (pram2[0] == null ? true : null != it.getCwhcode1()) && pram2[0].equals(it.getCwhcode1())).collectList()
                        .flatMap(list -> Mono.just(list.size() == 0 ? 0L : list.stream().mapToLong(e -> new BigDecimal(StrUtil.isNotBlank(e.getBaseQuantity()) ? e.getBaseQuantity() : "0").longValue()).sum()));
                Mono<Long> ck = (pram2[2] == null ? saleousingsRepository.findAllByIyearAndCinvodeAndBillStyleIn(year,  pram2[1], Arrays.asList("XSCKD", "QTCKD", "CLLYCKD")) : saleousingsRepository.findAllByIyearAndCinvodeAndBatchIdAndDpdateAndDvdateAndBillStyleInOrderByDvdateDesc(year, pram2[1], pram2[2], pram2[3], pram2[4], Arrays.asList("XSCKD", "QTCKD", "CLLYCKD"))).filter(it -> (cgB?true:StrUtil.equals(it.getBcheck(),"1")) && (pram2[0] == null ? true : null != it.getCwhcode1()) && pram2[0].equals(it.getCwhcode1())).collectList()
                        .flatMap(list -> Mono.just(list.size() == 0 ? 0L : list.stream().mapToLong(e -> new BigDecimal(StrUtil.isNotBlank(e.getBaseQuantity()) ? e.getBaseQuantity() : "0").longValue()).sum()));
                Mono<Long> etcck = (pram2[2] == null ? saleousingsRepository.findAllByIyearAndCinvodeAndBillStyleIn(year,  pram2[1], Arrays.asList("PKCKD","DBCKD","XTZHCKD")) : saleousingsRepository.findAllByIyearAndCinvodeAndBatchIdAndDpdateAndDvdateAndBillStyleInOrderByDvdateDesc(year, pram2[1], pram2[2], pram2[3], pram2[4], Arrays.asList("PKCKD","DBCKD","XTZHCKD"))).filter(it -> (xsB?true:StrUtil.equals(it.getBcheck(),"1")) && (pram2[0] == null ? true : null != it.getCwhcode1()) && pram2[0].equals(it.getCwhcode1())).collectList()
                        .flatMap(list -> Mono.just(list.size() == 0 ? 0L : list.stream().mapToLong(e -> new BigDecimal(StrUtil.isNotBlank(e.getBaseQuantity()) ? e.getBaseQuantity() : "0").longValue()).sum()));
                return Mono.zip(qcye, rk, Mono.just(0), ck, Mono.just(0)).flatMap(tips -> Mono.just(tips.getT1() + tips.getT2() + tips.getT3() - tips.getT4() - tips.getT5()));
            });
            Mono<Long> ztlM = Mono.just(pram).flatMap(pram2->{
                Mono<Long> dh = (pram2[2] == null ? warehousingsRepository.findAllByIyearAndCinvodeAndBillStyle(year,  pram2[1], "CGDHD") : warehousingsRepository.findAllByIyearAndCinvodeAndBatchIdAndDpdateAndDvdateAndBillStyleOrderByDvdateDesc(year,  pram2[1], pram2[2], pram2[3], pram2[4],"CGDHD")).filter(it -> (null == it.getSourcetype() || !it.getSourcetype().equals("CGRKD")) && (pram2[0] == null ? true : null != it.getCwhcode1()) && pram2[0].equals(it.getCwhcode1())).collectList()
                        .flatMap(list -> Mono.just(list.size() == 0 ? 0L : list.stream().mapToLong(e -> (new BigDecimal(StrUtil.isNotBlank(e.getBaseQuantity()) ? e.getBaseQuantity() : "0").longValue()-new BigDecimal(StrUtil.isNotBlank(e.getIsumRuku()) ? e.getIsumRuku() : "0").longValue())).sum()));
                Mono<Long> rk = cgB?Mono.just(0L):(pram2[2] == null ? warehousingsRepository.findAllByIyearAndCinvodeAndBillStyleIn(year,  pram2[1],  Arrays.asList("CGRKD", "QTRKD")) : warehousingsRepository.findAllByIyearAndCinvodeAndBatchIdAndDpdateAndDvdateAndBillStyleInOrderByDvdateDesc(year,  pram2[1], pram2[2], pram2[3], pram2[4], Arrays.asList("CGRKD", "QTRKD"))).filter(it -> (null == it.getBcheck() || !it.getBcheck().equals("1")) && (pram2[0] == null ? true : null != it.getCwhcode1()) && pram2[0].equals(it.getCwhcode1())).collectList()
                        .flatMap(list -> Mono.just(list.size() == 0 ? 0L : list.stream().mapToLong(e -> (new BigDecimal(StrUtil.isNotBlank(e.getBaseQuantity()) ? e.getBaseQuantity() : "0").longValue())).sum()));
                Mono<Long> xh = (pram2[2] == null ? saleousingsRepository.findAllByIyearAndCinvodeAndBillStyle(year,  pram2[1], "XHD") : saleousingsRepository.findAllByIyearAndCinvodeAndBatchIdAndDpdateAndDvdateAndBillStyleOrderByDvdateDesc(year,  pram2[1], pram2[2], pram2[3], pram2[4],"XHD")).filter(it -> (null == it.getSourcetype() || !it.getSourcetype().equals("XSCKD")) && (pram2[0] == null ? true : null != it.getCwhcode1()) && pram2[0].equals(it.getCwhcode1())).collectList()
                        .flatMap(list -> Mono.just(list.size() == 0 ? 0L : list.stream().mapToLong(e -> (new BigDecimal(StrUtil.isNotBlank(e.getBaseQuantity()) ? e.getBaseQuantity() : "0").longValue()-new BigDecimal(StrUtil.isNotBlank(e.getIsumChuku()) ? e.getIsumChuku() : "0").longValue())).sum()));
                Mono<Long> ck =  xsB?Mono.just(0L):(pram2[2] == null ? saleousingsRepository.findAllByIyearAndCinvodeAndBillStyleIn(year,  pram2[1],  Arrays.asList("XSCKD", "QTCKD", "CLLYCKD")) : saleousingsRepository.findAllByIyearAndCinvodeAndBatchIdAndDpdateAndDvdateAndBillStyleInOrderByDvdateDesc(year,  pram2[1], pram2[2], pram2[3], pram2[4], Arrays.asList("XSCKD", "QTCKD", "CLLYCKD"))).filter(it -> (null == it.getBcheck() || !it.getBcheck().equals("1")) && (pram2[0] == null ? true : null != it.getCwhcode1()) && pram2[0].equals(it.getCwhcode1())).collectList()
                        .flatMap(list -> Mono.just(list.size() == 0 ? 0L : list.stream().mapToLong(e -> (new BigDecimal(StrUtil.isNotBlank(e.getBaseQuantity()) ? e.getBaseQuantity() : "0").longValue())).sum()));
                return Mono.zip(dh,rk,xh,ck).flatMap(tips->Mono.just(tips.getT1() + tips.getT2() - tips.getT3() - tips.getT4()));
            });
            return (mode.equals("xcl") ? xclM : mode.equals("ztl") ? ztlM : xclM.zipWith(ztlM).flatMap(tips -> Mono.just(tips.getT1() + tips.getT2()))).flatMap(v -> Mono.just(MapUtil.of(parameter, v)));
        }).collectList().map(R::ok);
    }

    /**
     * 查询指定仓库存货下批号列表
     * year 年度
     * mode 查询方式 xcl 现存量 ztl 在途量 all 可用量、
     * checks 是否保存时调整现存量标记 kcCgrkCheck 入库 kcXsckCheck 出库
     * parameters 仓库存货信息 ['仓库1ID=存货编码=唯一标记']
     * @param map
     * @return [{parameter:long}]
     */
    @PostMapping("batchSelectorList")
    public Mono<R> batchSelectorList(@RequestBody Map map) {
        if (map.keySet().size() != 4) return Mono.just(R.error());
        String year = map.get("year").toString();
        String mode = map.get("mode").toString();
        List<String> checks = JSON.parseArray(map.get("checks").toString(),String.class);
        List<String> parameters = JSON.parseArray(map.get("parameters").toString(),String.class);
        return Flux.fromIterable(parameters).flatMap(parameter->{
            String[] pram = parameter.split("=");
            Boolean cgB = checks.contains("cg");
            Boolean xsB = checks.contains("xs");
//                    stockBeginBalanceRepository.findAllByIyearAndBcheckAndStockId(year, "1", pram[1]).filter(it -> pram[0] == null ? true : (null != it.getCwhcode1() && pram[0].equals(it.getCwhcode1())))
            return stockBeginBalanceRepository.findAllByIyearAndCwhcodeAndStockId(year, pram[0],pram[1])
            .flatMap(entry->{
                Mono<Long> xclM = Mono.just(pram).flatMap(pram2-> {
                    Mono<Long> qcye = stockBeginBalanceRepository.findAllByIyearAndBcheckAndStockIdAndBatchNumberAndDpdateAndDvdateOrderByDvdateDesc(year, "1", entry.getStockId(),entry.getBatchNumber(),entry.getDpdate(),entry.getDvdate()).filter(it -> pram2[0] == null ? true : (null != it.getCwhcode1() && pram2[0].equals(it.getCwhcode1()))).collectList()
                            .flatMap(list -> Mono.just(list.size() == 0 ? 0L : list.stream().mapToLong(e -> e.getBaseQuantity().longValue()).sum()));
                    Mono<Long> rk = (warehousingsRepository.findAllByIyearAndCinvodeAndBatchIdAndDpdateAndDvdateAndBillStyleInOrderByDvdateDesc(year,  entry.getStockId(),entry.getBatchNumber(),entry.getDpdate(),entry.getDvdate(), Arrays.asList("CGRKD", "QTRKD"))).filter(it -> (cgB?true:StrUtil.equals(it.getBcheck(),"1")) && (pram2[0] == null ? true : null != it.getCwhcode1()) && pram2[0].equals(it.getCwhcode1())).collectList()
                            .flatMap(list -> Mono.just(list.size() == 0 ? 0L : list.stream().mapToLong(e -> new BigDecimal(StrUtil.isNotBlank(e.getBaseQuantity()) ? e.getBaseQuantity() : "0").longValue()).sum()));
                    Mono<Long> etcrk =  (warehousingsRepository.findAllByIyearAndCinvodeAndBatchIdAndDpdateAndDvdateAndBillStyleInOrderByDvdateDesc(year,  entry.getStockId(),entry.getBatchNumber(),entry.getDpdate(),entry.getDvdate(), Arrays.asList("PYRKD","DBRKD","XTZHRKD"))).filter(it -> (xsB?true:StrUtil.equals(it.getBcheck(),"1")) && (pram2[0] == null ? true : null != it.getCwhcode1()) && pram2[0].equals(it.getCwhcode1())).collectList()
                            .flatMap(list -> Mono.just(list.size() == 0 ? 0L : list.stream().mapToLong(e -> new BigDecimal(StrUtil.isNotBlank(e.getBaseQuantity()) ? e.getBaseQuantity() : "0").longValue()).sum()));
                    Mono<Long> ck =  (saleousingsRepository.findAllByIyearAndCinvodeAndBatchIdAndDpdateAndDvdateAndBillStyleInOrderByDvdateDesc(year, entry.getStockId(),entry.getBatchNumber(),entry.getDpdate(),entry.getDvdate(), Arrays.asList("XSCKD", "QTCKD", "CLLYCKD"))).filter(it -> (cgB?true:StrUtil.equals(it.getBcheck(),"1")) && (pram2[0] == null ? true : null != it.getCwhcode1()) && pram2[0].equals(it.getCwhcode1())).collectList()
                            .flatMap(list -> Mono.just(list.size() == 0 ? 0L : list.stream().mapToLong(e -> new BigDecimal(StrUtil.isNotBlank(e.getBaseQuantity()) ? e.getBaseQuantity() : "0").longValue()).sum()));
                    Mono<Long> etcck = (saleousingsRepository.findAllByIyearAndCinvodeAndBatchIdAndDpdateAndDvdateAndBillStyleInOrderByDvdateDesc(year, entry.getStockId(),entry.getBatchNumber(),entry.getDpdate(),entry.getDvdate(), Arrays.asList("PKCKD","DBCKD","XTZHCKD"))).filter(it -> (xsB?true:StrUtil.equals(it.getBcheck(),"1")) && (pram2[0] == null ? true : null != it.getCwhcode1()) && pram2[0].equals(it.getCwhcode1())).collectList()
                            .flatMap(list -> Mono.just(list.size() == 0 ? 0L : list.stream().mapToLong(e -> new BigDecimal(StrUtil.isNotBlank(e.getBaseQuantity()) ? e.getBaseQuantity() : "0").longValue()).sum()));
                    return Mono.zip(qcye, rk, Mono.just(0), ck, Mono.just(0)).flatMap(tips -> Mono.just(tips.getT1() + tips.getT2() + tips.getT3() - tips.getT4() - tips.getT5()));
                });
                Mono<Long> ztlM = Mono.just(pram).flatMap(pram2->{
                    Mono<Long> dh = (warehousingsRepository.findAllByIyearAndCinvodeAndBatchIdAndDpdateAndDvdateAndBillStyleOrderByDvdateDesc(year,  entry.getStockId(),entry.getBatchNumber(),entry.getDpdate(),entry.getDvdate(),"CGDHD")).filter(it -> (null == it.getSourcetype() || !it.getSourcetype().equals("CGRKD")) && (pram2[0] == null ? true : null != it.getCwhcode1()) && pram2[0].equals(it.getCwhcode1())).collectList()
                            .flatMap(list -> Mono.just(list.size() == 0 ? 0L : list.stream().mapToLong(e -> (new BigDecimal(StrUtil.isNotBlank(e.getBaseQuantity()) ? e.getBaseQuantity() : "0").longValue()-new BigDecimal(StrUtil.isNotBlank(e.getIsumRuku()) ? e.getIsumRuku() : "0").longValue())).sum()));
                    Mono<Long> rk = cgB?Mono.just(0L):( warehousingsRepository.findAllByIyearAndCinvodeAndBatchIdAndDpdateAndDvdateAndBillStyleInOrderByDvdateDesc(year,  entry.getStockId(),entry.getBatchNumber(),entry.getDpdate(),entry.getDvdate(), Arrays.asList("CGRKD", "QTRKD"))).filter(it -> (null == it.getBcheck() || !it.getBcheck().equals("1")) && (pram2[0] == null ? true : null != it.getCwhcode1()) && pram2[0].equals(it.getCwhcode1())).collectList()
                            .flatMap(list -> Mono.just(list.size() == 0 ? 0L : list.stream().mapToLong(e -> (new BigDecimal(StrUtil.isNotBlank(e.getBaseQuantity()) ? e.getBaseQuantity() : "0").longValue())).sum()));
                    Mono<Long> xh = ( saleousingsRepository.findAllByIyearAndCinvodeAndBatchIdAndDpdateAndDvdateAndBillStyleOrderByDvdateDesc(year,  entry.getStockId(),entry.getBatchNumber(),entry.getDpdate(),entry.getDvdate(),"XHD")).filter(it -> (null == it.getSourcetype() || !it.getSourcetype().equals("XSCKD")) && (pram2[0] == null ? true : null != it.getCwhcode1()) && pram2[0].equals(it.getCwhcode1())).collectList()
                            .flatMap(list -> Mono.just(list.size() == 0 ? 0L : list.stream().mapToLong(e -> (new BigDecimal(StrUtil.isNotBlank(e.getBaseQuantity()) ? e.getBaseQuantity() : "0").longValue()-new BigDecimal(StrUtil.isNotBlank(e.getIsumChuku()) ? e.getIsumChuku() : "0").longValue())).sum()));
                    Mono<Long> ck =  xsB?Mono.just(0L):(saleousingsRepository.findAllByIyearAndCinvodeAndBatchIdAndDpdateAndDvdateAndBillStyleInOrderByDvdateDesc(year,  entry.getStockId(),entry.getBatchNumber(),entry.getDpdate(),entry.getDvdate(), Arrays.asList("XSCKD", "QTCKD", "CLLYCKD"))).filter(it -> (null == it.getBcheck() || !it.getBcheck().equals("1")) && (pram2[0] == null ? true : null != it.getCwhcode1()) && pram2[0].equals(it.getCwhcode1())).collectList()
                            .flatMap(list -> Mono.just(list.size() == 0 ? 0L : list.stream().mapToLong(e -> (new BigDecimal(StrUtil.isNotBlank(e.getBaseQuantity()) ? e.getBaseQuantity() : "0").longValue())).sum()));
                    return Mono.zip(dh,rk,xh,ck).flatMap(tips->Mono.just(tips.getT1() + tips.getT2() - tips.getT3() - tips.getT4()));
                });
                return (mode.equals("xcl") ? xclM : mode.equals("ztl") ? ztlM : xclM.zipWith(ztlM).flatMap(tips -> Mono.just(tips.getT1() + tips.getT2()))).flatMap(v -> {
                    entry.setBaseQuantity(BigDecimal.valueOf(v));
                    return Mono.just(entry);
                });
            });
        }).collectList().map(R::ok);
    }

    /**
     * 查询指定仓库下可用存货列表
     * year 年度
     * mode 查询方式 xcl 现存量 ztl 在途量 all 可用量、
     * checks 是否保存时调整现存量标记 kcCgrkCheck 入库 kcXsckCheck 出库
     * parameters 仓库存货信息 ['仓库1ID']
     * @param map
     * @return [{parameter:long}]
     */
    @PostMapping("getStockList")
    public Mono<R> getStockList(@RequestBody Map map) {
        if (map.keySet().size() != 4) return Mono.just(R.error());
        String year = map.get("year").toString();
        String mode = map.get("mode").toString();
        List<String> checks = JSON.parseArray(map.get("checks").toString(),String.class);
        List<String> parameters = JSON.parseArray(map.get("parameters").toString(),String.class);
        return Flux.fromIterable(parameters).flatMap(parameter->{
            String[] pram = parameter.split("=");
            Boolean cgB = checks.contains("cg");
            Boolean xsB = checks.contains("xs");
            return stockBeginBalanceRepository.findAllByIyearAndCwhcode(year,  pram[0]).flatMap(entry->{
                Mono<Long> xclM = Mono.just(pram).flatMap(pram2-> {
                    Mono<Long> qcye = stockBeginBalanceRepository.findAllByIyearAndBcheckAndStockIdAndBatchNumberAndDpdateAndDvdateOrderByDvdateDesc(year, "1", entry.getStockId(),entry.getBatchNumber(),entry.getDpdate(),entry.getDvdate()).filter(it -> pram2[0] == null ? true : (null != it.getCwhcode1() && pram2[0].equals(it.getCwhcode1()))).collectList()
                            .flatMap(list -> Mono.just(list.size() == 0 ? 0L : list.stream().mapToLong(e -> e.getBaseQuantity().longValue()).sum()));
                    Mono<Long> rk = (warehousingsRepository.findAllByIyearAndCinvodeAndBatchIdAndDpdateAndDvdateAndBillStyleInOrderByDvdateDesc(year,  entry.getStockId(),entry.getBatchNumber(),entry.getDpdate(),entry.getDvdate(), Arrays.asList("CGRKD", "QTRKD"))).filter(it -> (cgB?true:StrUtil.equals(it.getBcheck(),"1")) && (pram2[0] == null ? true : null != it.getCwhcode1()) && pram2[0].equals(it.getCwhcode1())).collectList()
                            .flatMap(list -> Mono.just(list.size() == 0 ? 0L : list.stream().mapToLong(e -> new BigDecimal(StrUtil.isNotBlank(e.getBaseQuantity()) ? e.getBaseQuantity() : "0").longValue()).sum()));
                    Mono<Long> etcrk =  (warehousingsRepository.findAllByIyearAndCinvodeAndBatchIdAndDpdateAndDvdateAndBillStyleInOrderByDvdateDesc(year,  entry.getStockId(),entry.getBatchNumber(),entry.getDpdate(),entry.getDvdate(), Arrays.asList("PYRKD","DBRKD","XTZHRKD"))).filter(it -> (xsB?true:StrUtil.equals(it.getBcheck(),"1")) && (pram2[0] == null ? true : null != it.getCwhcode1()) && pram2[0].equals(it.getCwhcode1())).collectList()
                            .flatMap(list -> Mono.just(list.size() == 0 ? 0L : list.stream().mapToLong(e -> new BigDecimal(StrUtil.isNotBlank(e.getBaseQuantity()) ? e.getBaseQuantity() : "0").longValue()).sum()));
                    Mono<Long> ck =  (saleousingsRepository.findAllByIyearAndCinvodeAndBatchIdAndDpdateAndDvdateAndBillStyleInOrderByDvdateDesc(year, entry.getStockId(),entry.getBatchNumber(),entry.getDpdate(),entry.getDvdate(), Arrays.asList("XSCKD", "QTCKD", "CLLYCKD"))).filter(it -> (cgB?true:StrUtil.equals(it.getBcheck(),"1")) && (pram2[0] == null ? true : null != it.getCwhcode1()) && pram2[0].equals(it.getCwhcode1())).collectList()
                            .flatMap(list -> Mono.just(list.size() == 0 ? 0L : list.stream().mapToLong(e -> new BigDecimal(StrUtil.isNotBlank(e.getBaseQuantity()) ? e.getBaseQuantity() : "0").longValue()).sum()));
                    Mono<Long> etcck = (saleousingsRepository.findAllByIyearAndCinvodeAndBatchIdAndDpdateAndDvdateAndBillStyleInOrderByDvdateDesc(year, entry.getStockId(),entry.getBatchNumber(),entry.getDpdate(),entry.getDvdate(), Arrays.asList("PKCKD","DBCKD","XTZHCKD"))).filter(it -> (xsB?true:StrUtil.equals(it.getBcheck(),"1")) && (pram2[0] == null ? true : null != it.getCwhcode1()) && pram2[0].equals(it.getCwhcode1())).collectList()
                            .flatMap(list -> Mono.just(list.size() == 0 ? 0L : list.stream().mapToLong(e -> new BigDecimal(StrUtil.isNotBlank(e.getBaseQuantity()) ? e.getBaseQuantity() : "0").longValue()).sum()));
                    return Mono.zip(qcye, rk, Mono.just(0), ck, Mono.just(0)).flatMap(tips -> Mono.just(tips.getT1() + tips.getT2() + tips.getT3() - tips.getT4() - tips.getT5()));
                });
                Mono<Long> ztlM = Mono.just(pram).flatMap(pram2->{
                    Mono<Long> dh = (warehousingsRepository.findAllByIyearAndCinvodeAndBatchIdAndDpdateAndDvdateAndBillStyleOrderByDvdateDesc(year,  entry.getStockId(),entry.getBatchNumber(),entry.getDpdate(),entry.getDvdate(),"CGDHD")).filter(it -> (null == it.getSourcetype() || !it.getSourcetype().equals("CGRKD")) && (pram2[0] == null ? true : null != it.getCwhcode1()) && pram2[0].equals(it.getCwhcode1())).collectList()
                            .flatMap(list -> Mono.just(list.size() == 0 ? 0L : list.stream().mapToLong(e -> (new BigDecimal(StrUtil.isNotBlank(e.getBaseQuantity()) ? e.getBaseQuantity() : "0").longValue()-new BigDecimal(StrUtil.isNotBlank(e.getIsumRuku()) ? e.getIsumRuku() : "0").longValue())).sum()));
                    Mono<Long> rk = cgB?Mono.just(0L):( warehousingsRepository.findAllByIyearAndCinvodeAndBatchIdAndDpdateAndDvdateAndBillStyleInOrderByDvdateDesc(year,  entry.getStockId(),entry.getBatchNumber(),entry.getDpdate(),entry.getDvdate(), Arrays.asList("CGRKD", "QTRKD"))).filter(it -> (null == it.getBcheck() || !it.getBcheck().equals("1")) && (pram2[0] == null ? true : null != it.getCwhcode1()) && pram2[0].equals(it.getCwhcode1())).collectList()
                            .flatMap(list -> Mono.just(list.size() == 0 ? 0L : list.stream().mapToLong(e -> (new BigDecimal(StrUtil.isNotBlank(e.getBaseQuantity()) ? e.getBaseQuantity() : "0").longValue())).sum()));
                    Mono<Long> xh = ( saleousingsRepository.findAllByIyearAndCinvodeAndBatchIdAndDpdateAndDvdateAndBillStyleOrderByDvdateDesc(year,  entry.getStockId(),entry.getBatchNumber(),entry.getDpdate(),entry.getDvdate(),"XHD")).filter(it -> (null == it.getSourcetype() || !it.getSourcetype().equals("XSCKD")) && (pram2[0] == null ? true : null != it.getCwhcode1()) && pram2[0].equals(it.getCwhcode1())).collectList()
                            .flatMap(list -> Mono.just(list.size() == 0 ? 0L : list.stream().mapToLong(e -> (new BigDecimal(StrUtil.isNotBlank(e.getBaseQuantity()) ? e.getBaseQuantity() : "0").longValue()-new BigDecimal(StrUtil.isNotBlank(e.getIsumChuku()) ? e.getIsumChuku() : "0").longValue())).sum()));
                    Mono<Long> ck =  xsB?Mono.just(0L):(saleousingsRepository.findAllByIyearAndCinvodeAndBatchIdAndDpdateAndDvdateAndBillStyleInOrderByDvdateDesc(year,  entry.getStockId(),entry.getBatchNumber(),entry.getDpdate(),entry.getDvdate(), Arrays.asList("XSCKD", "QTCKD", "CLLYCKD"))).filter(it -> (null == it.getBcheck() || !it.getBcheck().equals("1")) && (pram2[0] == null ? true : null != it.getCwhcode1()) && pram2[0].equals(it.getCwhcode1())).collectList()
                            .flatMap(list -> Mono.just(list.size() == 0 ? 0L : list.stream().mapToLong(e -> (new BigDecimal(StrUtil.isNotBlank(e.getBaseQuantity()) ? e.getBaseQuantity() : "0").longValue())).sum()));
                    return Mono.zip(dh,rk,xh,ck).flatMap(tips->Mono.just(tips.getT1() + tips.getT2() - tips.getT3() - tips.getT4()));
                });
                return (mode.equals("xcl") ? xclM : mode.equals("ztl") ? ztlM : xclM.zipWith(ztlM).flatMap(tips -> Mono.just(tips.getT1() + tips.getT2()))).flatMap(v -> {
                    entry.setBaseQuantity(BigDecimal.valueOf(v));
                    return Mono.just(entry);
                });
            });
        }).collectList().map(R::ok);
    }

    /**
     * 查询仓库下有现存量的存货
     * cwhcode 存货仓库
     * iyear 年度
     * rkBcheck 入库保存状态就是现存量 标志 1是查询所有 0 查询已审核
     * ckBcheck 出库保存状态就是现存量 标志 1是查询所有 0 查询已审核
     * @param map
     * @return
     */
    @PostMapping("getStockXclList")
    public Mono<R> getStockXclList(@RequestBody Map map) {
        // 仓库
        String ck=map.get("cwhcode")==null?"":map.get("cwhcode").toString();
        String year=map.get("iyear").toString();
        // 参数设置：入库保存状态就是现存量 标志 1是查询所有 0 查询已审核
        String rkBcheck=map.get("rkBcheck")==null?"0":map.get("rkBcheck").toString();
        String ckBcheck=map.get("rkBcheck")==null?"0":map.get("ckBcheck").toString();
        List<StockAccSheetVo> skl = new ArrayList<>();
        List<StockVo> sv = new ArrayList<>();
        return stockRepository.findAllByXcl()
                .collectList()
                .flatMap(slist->{
                    //期初
                    return  stockBeginBalanceRepository.findAllByIyearAndCk(ck,year)
                            .collectList()
                            .map(wl->{
                                skl.addAll(wl);
                                return slist;
                            });
                })
                .flatMap(slist->{
                    //入库
                    return warehousingsRepository.findAllByIyearAndCk(ck,year)
                            .filter(v-> {
                                if(rkBcheck.equals("0")){
                                    return  "1".equals(v.getBcheck());
                                }else{
                                    return  "0".equals(v.getBcheck()) || Objects.isNull(v.getBcheck());
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
                    return saleousingsRepository.findAllByIyearAndCk(ck,year)
                            .filter(v-> {
                                if(ckBcheck.equals("0")){
                                    return  "1".equals(v.getBcheck());
                                }else{
                                    return  "0".equals(v.getBcheck()) || Objects.isNull(v.getBcheck());
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
                                    str+=v.getBatchid();
                                    if(Objects.nonNull(v.getDpdate())){
                                        str+=v.getDpdate();
                                    }
                                    if(Objects.nonNull(v.getDvdate())){
                                        str+=v.getDvdate();
                                    }
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
                .flatMap(slist->{
                    //接收仓库唯一码、存货唯一码、批号、单据日期参数用于计算单价
                    String ddate = "2022-08-25";
                    List<String> stocks = sv.stream().map(v -> v.getStockNum()).collect(Collectors.toList());
                    List<StockVo> stockVoList = new ArrayList<>();
                    List<StockWarehousings> list1 = new ArrayList<>();
                    List<StockBeginBalance> list2 = new ArrayList<>();
                    List<StockSaleousings> list3 = new ArrayList<>();
                    return stocks.size() > 0 ? stockRepository.findAllByStockNums(stocks)
                            .collectList()
                            .map(list-> stockVoList.addAll(list))
                            .flatMap(s->{
                                //入库 采购其他 三个特殊+入库调整
                                return warehousingsRepository.findAllByCinvodesAndDdate(stocks, year, ddate)
                                        .collectList()
                                        .map(list-> list1.addAll(list));
                            })
                            .flatMap(s->{
                                //出库调整单
                                return saleousingsRepository.findAllByCinvodesAndDdate(stocks, year, ddate)
                                        .collectList()
                                        .map(list-> list3.addAll(list));
                            })
                            .flatMap(s->{
                                //期初
                                return beginBalanceRepository.findAllByCinvodes(stocks, year)
                                        .collectList()
                                        .map(list-> list2.addAll(list));
                            })
                            .map(v->{
                                //循环计算
                                sv.stream().forEach(item->{
                                    Optional<StockVo> first = stockVoList.stream().filter(obj -> obj.getStockNum().equals(item.getStockNum())).findFirst();
                                    if(first.isPresent()){
                                        //成本核算方式
                                        String type = first.get().getStockValuationType();
                                        //判断存货的成本核算方式
                                        if(type.equals("移动平均")){
                                            //入库
                                            List<StockWarehousings> rkList = list1.stream().filter(obj -> obj.getCwhcode().equals(item.getStockNum()) && obj.getCwhcode().equals(item.getStockCangku())
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

                                            BigDecimal n = BigDecimal.valueOf(num).add(BigDecimal.valueOf(num1)).subtract(BigDecimal.valueOf(num3));
                                            BigDecimal m = BigDecimal.valueOf(moeny).add(BigDecimal.valueOf(moeny1)).subtract(BigDecimal.valueOf(moeny3));

                                            //计算成本单价
                                            //数量0 =单价0
                                            if(n.compareTo(BigDecimal.ZERO) == 0){
                                                item.setPrice("0");
                                            }else
                                            //金额0 =单价0
                                            if(m.compareTo(BigDecimal.ZERO) == 0){
                                                item.setPrice("0");
                                            }else
                                            //数量负数 金额不是负数  =单价0
                                            if(n.compareTo(BigDecimal.ZERO) == -1 && m.compareTo(BigDecimal.ZERO) == 1){
                                                item.setPrice("0");
                                            }else
                                            //金额负数 数量不是负数 =单价0
                                            if(m.compareTo(BigDecimal.ZERO) == -1 && n.compareTo(BigDecimal.ZERO) == 1){
                                                item.setPrice("0");
                                            }else{
                                                item.setPrice(m.divide(n,10,BigDecimal.ROUND_DOWN).toString());
                                            }
                                        }else if(type.equals("相同批号")){
                                            //过滤期初和入库
                                            List<StockWarehousings> rkList = list1.stream().filter(obj -> {
                                                //没批号不操作
                                                if(!obj.getCwhcode().equals(item.getStockNum()) || !obj.getCwhcode().equals(item.getStockCangku())){
                                                    return false;
                                                }
                                                if(!obj.getBillStyle().equals("CGRKD")){
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
                                            }else
                                            //金额0 =单价0
                                            if(m.compareTo(BigDecimal.ZERO) == 0){
                                                item.setPrice("0");
                                            }else
                                            //数量负数 金额不是负数  =单价0
                                            if(n.compareTo(BigDecimal.ZERO) == -1 && m.compareTo(BigDecimal.ZERO) == 1){
                                                item.setPrice("0");
                                            }else
                                            //金额负数 数量不是负数 =单价0
                                            if(m.compareTo(BigDecimal.ZERO) == -1 && n.compareTo(BigDecimal.ZERO) == 1){
                                                item.setPrice("0");
                                            }else{
                                                item.setPrice(m.divide(n,10,BigDecimal.ROUND_DOWN).toString());
                                            }
                                        }else if(type.equals("手工计价")){
                                            //无成本单价反馈，需要手工输入成本价格
                                            item.setPrice("0");
                                        }else if(type.equals("先进先出")){
                                            item.setPrice("0");
                                        }
                                    }
                                });
                                return sv;
                            }): Mono.just(sv);
                })
                .map(v->R.ok().setResult(v));
    }
}

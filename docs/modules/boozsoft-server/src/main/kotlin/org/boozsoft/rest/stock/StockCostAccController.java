package org.boozsoft.rest.stock;

import com.alibaba.fastjson.JSON;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.boozsoft.domain.entity.Task;
import org.boozsoft.domain.entity.group.GroupStockAccount;
import org.boozsoft.domain.entity.group.GroupStockPeriod;
import org.boozsoft.domain.entity.stock.*;
import org.boozsoft.domain.ro.StockCostAccRo;
import org.boozsoft.domain.vo.stock.StockKctzVo;
import org.boozsoft.domain.vo.stock.StockVo;
import org.boozsoft.repo.group.GroupStockAccountRepository;
import org.boozsoft.repo.stock.*;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;


@Slf4j
@RestController
@RequestMapping("/stockCostAcc")
public class StockCostAccController {

    @Autowired
    private StockCostAccRepository stockCostAccRepository;
    @Autowired
    private StockWarehousingRepository warehousingRepository;
    @Autowired
    private StockSaleousingRepository saleousingRepository;
    @Autowired
    private StockSaleousingsRepository saleousingsRepository;
    @Autowired
    private StockWarehousingsRepository warehousingsRepository;

    @Autowired
    private StockRepository stockRepository;
    @Autowired
    private StockBeginBalanceRepository beginBalanceRepository;

    @Autowired
    private GroupStockAccountRepository groupStockAccountRepository;

    //可核算日期
    @GetMapping("/findRiqiList/{iyear}/{id}")
    public Mono<R> findRiqiList(@PathVariable String iyear,@PathVariable String id){
        return stockCostAccRepository.findAllByIyearAndMonth(id, iyear)
                .collectList()
                .map(list->{
                    List<GroupStockPeriod> spList = new ArrayList<>();
                    //过滤 已结账 已制单数据
                    List<GroupStockPeriod> collect = list.stream()
                            .filter(v -> (Objects.isNull(v.getIstock()) || !"1".equals(v.getIstock())) && (Objects.isNull(v.getBillMake()) || !"1".equals(v.getBillMake())))
                            .collect(Collectors.toList());
                    //启用日期
                    /*GroupStockPeriod stockPeriod = list.stream().filter(v -> "1".equals(v.getStockFlag())).findFirst().get();
                    if(collect.size() == 12){
                        spList.add(stockPeriod);
                    }else{
                        //从最新结账制单的下一个月开始
                        GroupStockPeriod sp =  list.stream()
                                .filter(v -> v.getStockMonth().compareTo(stockPeriod.getStockMonth()) >= 0)
                                .filter(v -> (Objects.isNull(v.getIstock()) || !"1".equals(v.getIstock())) && (Objects.isNull(v.getBillMake()) || !"1".equals(v.getBillMake())))
                                .findFirst()
                                .get();
                        spList.add(sp);
                    }*/
                    return collect;
                })
                .map(a->R.ok().setResult(a));
    }

    @PostMapping("warehousingCost")
    public Mono<R> warehousingCost(@RequestBody Map map){
        String riqi = map.get("riqi").toString();
        String flg = map.containsKey("flg") ?map.get("flg").toString():"1";//是否启用日期
        //入库单成本核算
        List<StockCostAcc> scList =  new ArrayList<>();
        //采购 其他  审核  日期
        //根据配置  仓库   存货
        return groupStockAccountRepository.findAllByCoCode("102")
                    .collectList()
                    .map(i -> i.size()>0?i.get(0):"")
                    .flatMap(entity -> !entity.equals("")? Mono.just(assemblyMapData(entity, "3")):Mono.just(""))
                    .flatMap(str->{
                        if("1".equals(str)){
                            //按仓库核算
                            //查询所有入库单 采购入库 其他入库  筛选出存在仓库
                            List<String> tlist = new ArrayList<>();
                            tlist.add("CGRKD");
                            tlist.add("QTRKD");
                            return warehousingsRepository.findAllByBillStyleAndDdate(riqi+"%", tlist)
                                    .collectList()
                                    .map(list->{
                                        //筛选仓库
                                        //筛选 采购入库 其他入库


                                        return null;
                                    })
                                    .map(v->R.ok().setResult(v));

                            //根据仓库去查询期初

                            //查询出库   材料领用出库 销售出库 其他出库 出库单负数 算入库 加入
                            //查询出库  材料领用出库 销售出库 其他出库 正数 减去

                        }else if("2".equals(str)){
                            //按存货核算
                            return null;
                        }else if("3".equals(str)){
                            //批次list
                            List<String> pcList = new ArrayList<>();
                            Map pmap = new HashMap();

                            //期初
                            List<StockBeginBalance> qclist = new ArrayList<>();
                            List<StockCostAcc> qclist2 = new ArrayList<>();

                            //入库
                            List<StockWarehousings> swlist = new ArrayList<>();
                            //出库单
                            List<StockSaleousings> sslist = new ArrayList<>();

                            //第一步 检验到货单 期初是否有
                            // 有
                            //按批号核算 到货单 计算批号单价 *到货单查询不到 查询期初获取批次  都查不到弹窗 查询出库批号 让他手动填写单价
                            return warehousingsRepository.findAllDdate(riqi+"%")
                                    .collectList()
                                    .map(list->{
                                        // 批次分组
                                        Map<String, List<StockWarehousings>> pcmap = list.stream()
                                                .collect(Collectors.groupingBy(StockWarehousings::getBatchId));
                                        //计算批次 单价
                                        pcmap.forEach((k, value) -> {
                                            //主数量
                                            BigDecimal numSum = value.stream().map(x -> {
                                                return Objects.isNull(x.getBaseQuantity()) || x.getBaseQuantity().length() <= 0 ? "0.00" : x.getBaseQuantity();
                                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                                            //无税金额
                                            BigDecimal isotSum = value.stream().map(x -> {
                                                return Objects.isNull(x.getIcost()) || x.getIcost().length() <= 0 ? "0.00" : x.getIcost();
                                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                                            //单价
                                            BigDecimal price = isotSum.divide(numSum,10,BigDecimal.ROUND_HALF_DOWN);
                                            pmap.put(k, price);
                                            pcList.add(k);
                                        });
                                        return pcList;
                                    })
                                    .flatMap(item->{
                                        //期初余额
                                        if("1".equals(flg)){
                                            return beginBalanceRepository.findAll()
                                                    .collectList()
                                                    .map(list -> {
                                                        //过滤批次
                                                        List<StockBeginBalance> sb = list.stream().filter(v -> pcList.contains(v.getBatchNumber())).collect(Collectors.toList());
                                                        qclist.addAll(sb);
                                                        return qclist;
                                                    });
                                        }else{
                                            //读取上一个月的结存
                                            return stockCostAccRepository.findAllByIyearAndMonthAnd(riqi.split("-")[0], riqi.split("-")[1])
                                                    .collectList()
                                                    .map(list -> {
                                                        //过滤批次
                                                        List<StockCostAcc> sb = list.stream().filter(v -> pcList.contains(v.getBatchId())).collect(Collectors.toList());
                                                        qclist2.addAll(sb);
                                                        return qclist2;
                                                    });
                                        }
                                    })
                                    .flatMap(item->{
                                        //本期采购 其他入库
                                        List<String> tlist = new ArrayList<>();
                                        tlist.add("CGRKD");
                                        tlist.add("QTRKD");
                                        return warehousingsRepository.findAllByBillStyleAndDdate(riqi+"%", tlist)
                                                .collectList()
                                                .map(list -> {
                                                    //过滤批次
                                                    List<StockWarehousings> sw = list.stream().filter(v -> pcList.contains(v.getBatchId())).collect(Collectors.toList());
                                                    swlist.addAll(sw);
                                                    return swlist;
                                                });
                                    })
                                    .flatMap(item->{
                                        //查询出库
                                        List<String> tlist = new ArrayList<>();
                                        tlist.add("XSCKD");
                                        tlist.add("QTCKD");
                                        tlist.add("CLLYD");
                                        return saleousingsRepository.findAllByBillStyleAndDdate(riqi+"%", tlist)
                                                .collectList()
                                                .map(list -> {
                                                    sslist.addAll(list);
                                                    return sslist;
                                                });
                                    })
                                    .flatMap(item->{
                                        //删除改年度月份的记录
                                        return stockCostAccRepository.deleteAll()
                                                .thenReturn(pcList);
                                    })
                                    .map(list->{
                                        list.forEach(pc->{
                                            StockCostAcc stockCostAcc = new StockCostAcc();
                                            stockCostAcc.setIyear(riqi.split("-")[0]);
                                            stockCostAcc.setImonth(riqi.split("-")[1]);
                                            stockCostAcc.setBatchId(pc);
                                            //期初
                                            if("1".equals(flg)){
                                                double sumQc = qclist.stream().filter(i-> pc.equals(i.getBatchNumber())).mapToDouble(v -> {
                                                    if (Objects.isNull(v.getBaseQuantity())) return 0.00d;
                                                    return Double.parseDouble(v.getBaseQuantity().toString());
                                                }).sum();
                                                double sumQc1 = qclist.stream().filter(i-> pc.equals(i.getBatchNumber())).mapToDouble(v -> {
                                                    if (Objects.isNull(v.getSubQuantity1())) return 0.00d;
                                                    return Double.parseDouble(v.getSubQuantity1().toString());
                                                }).sum();
                                                double sumQc2 = qclist.stream().filter(i-> pc.equals(i.getBatchNumber())).mapToDouble(v -> {
                                                    if (Objects.isNull(v.getSubQuantity2())) return 0.00d;
                                                    return Double.parseDouble(v.getSubQuantity2().toString());
                                                }).sum();
                                                double icostQc = qclist.stream().filter(i-> pc.equals(i.getBatchNumber())).mapToDouble(v -> {
                                                    if (Objects.isNull(v.getIcost())) return 0.00d;
                                                    return Double.parseDouble(v.getIcost().toString());
                                                }).sum();
                                                stockCostAcc.setBaseQuantityQc(sumQc + "");
                                                stockCostAcc.setBaseQuantity1Qc(sumQc1 + "");
                                                stockCostAcc.setBaseQuantity2Qc(sumQc2 + "");
                                                stockCostAcc.setIcostQc(icostQc + "");
                                            }else{
                                                double sumQc = qclist2.stream().filter(i-> pc.equals(i.getBatchId())).mapToDouble(v -> {
                                                    if (Objects.isNull(v.getBaseQuantityJc())) return 0.00d;
                                                    return Double.parseDouble(v.getBaseQuantityJc().toString());
                                                }).sum();
                                                double sumQc1 = qclist2.stream().filter(i-> pc.equals(i.getBatchId())).mapToDouble(v -> {
                                                    if (Objects.isNull(v.getBaseQuantity1Jc())) return 0.00d;
                                                    return Double.parseDouble(v.getBaseQuantity1Jc().toString());
                                                }).sum();
                                                double sumQc2 = qclist2.stream().filter(i-> pc.equals(i.getBatchId())).mapToDouble(v -> {
                                                    if (Objects.isNull(v.getBaseQuantity2Jc())) return 0.00d;
                                                    return Double.parseDouble(v.getBaseQuantity2Jc().toString());
                                                }).sum();
                                                double icostQc = qclist2.stream().filter(i-> pc.equals(i.getBatchId())).mapToDouble(v -> {
                                                    if (Objects.isNull(v.getIcostJc())) return 0.00d;
                                                    return Double.parseDouble(v.getIcostJc().toString());
                                                }).sum();
                                                stockCostAcc.setBaseQuantityQc(sumQc + "");
                                                stockCostAcc.setBaseQuantity1Qc(sumQc1 + "");
                                                stockCostAcc.setBaseQuantity2Qc(sumQc2 + "");
                                                stockCostAcc.setIcostQc(icostQc + "");
                                            }

                                            //本期入库
                                            double sumBq = swlist.stream().filter(i-> pc.equals(i.getBatchId())).mapToDouble(v -> {
                                                if (Objects.isNull(v.getBaseQuantity())) return 0.00d;
                                                return Double.parseDouble(v.getBaseQuantity().toString());
                                            }).sum();
                                            double sumBq1 = swlist.stream().filter(i-> pc.equals(i.getBatchId())).mapToDouble(v -> {
                                                if (Objects.isNull(v.getSubQuantity1())) return 0.00d;
                                                return Double.parseDouble(v.getSubQuantity1().toString());
                                            }).sum();
                                            double sumBq2 = swlist.stream().filter(i-> pc.equals(i.getBatchId())).mapToDouble(v -> {
                                                if (Objects.isNull(v.getSubQuantity2())) return 0.00d;
                                                return Double.parseDouble(v.getSubQuantity2().toString());
                                            }).sum();
                                            double icostBq = swlist.stream().filter(i-> pc.equals(i.getBatchId())).mapToDouble(v -> {
                                                if (Objects.isNull(v.getIcost())) return 0.00d;
                                                return Double.parseDouble(v.getIcost().toString());
                                            }).sum();
                                            stockCostAcc.setBaseQuantityBq(sumBq + "");
                                            stockCostAcc.setBaseQuantity1Bq(sumBq1 + "");
                                            stockCostAcc.setBaseQuantity2Bq(sumBq2 + "");
                                            stockCostAcc.setIcostBq(icostBq + "");

                                            //本期材料领用出库 销售出库 其他出库负数
                                            double sumCkf = sslist.stream().filter(v->pcList.contains(v.getBatchId()) && new BigDecimal(v.getIcost()).compareTo(BigDecimal.ZERO) < 0).mapToDouble(v -> {
                                                if (Objects.isNull(v.getBaseQuantity())) return 0.00d;
                                                return Double.parseDouble(v.getBaseQuantity().toString());
                                            }).sum();
                                            double sumCkf1 = sslist.stream().filter(v->pcList.contains(v.getBatchId()) && new BigDecimal(v.getIcost()).compareTo(BigDecimal.ZERO) < 0).mapToDouble(v -> {
                                                if (Objects.isNull(v.getSubQuantity1())) return 0.00d;
                                                return Double.parseDouble(v.getSubQuantity1().toString());
                                            }).sum();
                                            double sumCkf2 = sslist.stream().filter(v->pcList.contains(v.getBatchId()) && new BigDecimal(v.getIcost()).compareTo(BigDecimal.ZERO) < 0).mapToDouble(v -> {
                                                if (Objects.isNull(v.getSubQuantity2())) return 0.00d;
                                                return Double.parseDouble(v.getSubQuantity2().toString());
                                            }).sum();
                                            double icostCkf = sslist.stream().filter(v->pcList.contains(v.getBatchId()) && new BigDecimal(v.getIcost()).compareTo(BigDecimal.ZERO) < 0).mapToDouble(v -> {
                                                if (Objects.isNull(v.getIcost())) return 0.00d;
                                                return Double.parseDouble(v.getIcost().toString());
                                            }).sum();
                                            BigDecimal bq = new BigDecimal(stockCostAcc.getBaseQuantityBq()).add(new BigDecimal(sumCkf));
                                            BigDecimal bq1 = new BigDecimal(stockCostAcc.getBaseQuantity1Bq()).add(new BigDecimal(sumCkf1));
                                            BigDecimal bq2 = new BigDecimal(stockCostAcc.getBaseQuantity2Bq()).add(new BigDecimal(sumCkf2));
                                            BigDecimal icost = new BigDecimal(stockCostAcc.getIcostBq()).add(new BigDecimal(icostCkf));
                                            stockCostAcc.setBaseQuantityBq(bq.toString());
                                            stockCostAcc.setBaseQuantity1Bq(bq1.toString());
                                            stockCostAcc.setBaseQuantity2Bq(bq2.toString());
                                            stockCostAcc.setIcostBq(icost.toString());

                                            //本月金额 = 本期 + 期初
                                            BigDecimal bqTotal = new BigDecimal(stockCostAcc.getBaseQuantityBq()).add(new BigDecimal(stockCostAcc.getBaseQuantityQc()));
                                            BigDecimal bqTotal1 = new BigDecimal(stockCostAcc.getBaseQuantity1Bq()).add(new BigDecimal(stockCostAcc.getBaseQuantity1Qc()));
                                            BigDecimal bqTotal2 = new BigDecimal(stockCostAcc.getBaseQuantity2Bq()).add(new BigDecimal(stockCostAcc.getBaseQuantity2Qc()));
                                            BigDecimal icostTotal = new BigDecimal(stockCostAcc.getIcostBq()).add(new BigDecimal(stockCostAcc.getIcostQc()));
                                            stockCostAcc.setBaseQuantityTotal(bqTotal.toString());
                                            stockCostAcc.setBaseQuantity1Total(bqTotal1.toString());
                                            stockCostAcc.setBaseQuantity2Total(bqTotal2.toString());
                                            stockCostAcc.setIcostTotal(icostTotal.toString());
                                            stockCostAcc.setPrice(icostTotal.divide(bqTotal,10,BigDecimal.ROUND_HALF_UP).toString());

                                            //查询出库  材料领用出库 销售出库 其他出库 正数
                                            List<StockSaleousings> clist = sslist.stream().filter(v -> pcList.contains(v.getBatchId()) && new BigDecimal(v.getIcost()).compareTo(BigDecimal.ZERO) >= 0).collect(Collectors.toList());
                                            double sumCk = clist.stream().mapToDouble(v -> {
                                                if (Objects.isNull(v.getBaseQuantity())) return 0.00d;
                                                return Double.parseDouble(v.getBaseQuantity().toString());
                                            }).sum();
                                            double sumCk1 = clist.stream().mapToDouble(v -> {
                                                if (Objects.isNull(v.getSubQuantity1())) return 0.00d;
                                                return Double.parseDouble(v.getSubQuantity1().toString());
                                            }).sum();
                                            double sumCk2 = clist.stream().mapToDouble(v -> {
                                                if (Objects.isNull(v.getSubQuantity2())) return 0.00d;
                                                return Double.parseDouble(v.getSubQuantity2().toString());
                                            }).sum();

                                            double icostCk = clist.stream().mapToDouble(v -> {
                                                if (Objects.isNull(v.getIcost())) return 0.00d;
                                                return Double.parseDouble(v.getIcost().toString());
                                            }).sum();

                                            stockCostAcc.setBaseQuantityCk(sumCk+"");
                                            stockCostAcc.setBaseQuantity1Ck(sumCk1+"");
                                            stockCostAcc.setBaseQuantity2Ck(sumCk2+"");

                                            stockCostAcc.setIcostCk(pmap.get(pc).toString());

                                            //结存 本月 - 出库
                                            BigDecimal bqJc = new BigDecimal(stockCostAcc.getBaseQuantityTotal()).subtract(new BigDecimal(stockCostAcc.getBaseQuantityCk()));
                                            BigDecimal bqJc1 = new BigDecimal(stockCostAcc.getBaseQuantity1Total()).subtract(new BigDecimal(stockCostAcc.getBaseQuantity1Ck()));
                                            BigDecimal bqJc2 = new BigDecimal(stockCostAcc.getBaseQuantity2Total()).subtract(new BigDecimal(stockCostAcc.getBaseQuantity2Ck()));
                                            BigDecimal icostJc = new BigDecimal(stockCostAcc.getIcostBq()).subtract(new BigDecimal(stockCostAcc.getIcostQc()));
                                            stockCostAcc.setBaseQuantityJc(bqJc+"");
                                            stockCostAcc.setBaseQuantity1Jc(bqJc1+"");
                                            stockCostAcc.setBaseQuantity2Jc(bqJc2+"");
                                            stockCostAcc.setIcostJc(icostJc+"");

                                            stockCostAcc.setPriceJc(icostJc.divide(bqJc,10,BigDecimal.ROUND_HALF_UP).toString());

                                            scList.add(stockCostAcc);

                                        });
                                        return scList;
                                    })
                                    .flatMap(item->{
                                        return stockCostAccRepository.saveAll(item).collectList();
                                    })
                                    .map(item->{
                                        //更新到出库单价>>> 出库list 过滤对应批号 跟新对应批号的单价
                                        List<StockSaleousings> clist = sslist.stream().filter(v -> pcList.contains(v.getBatchId())).map(v->{
                                            v.setPrice(pmap.get(v.getBatchId()).toString());
                                            return v;
                                        }).collect(Collectors.toList());
                                        return clist;
                                    })
                                    .flatMap(item->{
                                        return saleousingsRepository.saveAll(item).collectList();
                                    })
                                    .flatMap(item->{
                                        //更新到入库单主表成本核算标志
                                        List<String> slist = swlist.stream().filter(v -> pcList.contains(v.getBatchId())).map(v->v.getCcode()).distinct().collect(Collectors.toList());
                                        return slist.size() > 0 ? warehousingRepository.updateCostStatusByCcodes(slist).thenReturn(slist) : Mono.just(slist);
                                    })
                                    .flatMap(item->{
                                        //更新到出库单主表成本核算标志
                                        List<String> clist = sslist.stream().filter(v -> pcList.contains(v.getBatchId())).map(v->v.getCcode()).distinct().collect(Collectors.toList());
                                        return clist.size() > 0 ? saleousingRepository.updateCostStatusByCcodes(clist).thenReturn(clist): Mono.just(clist);
                                    })
                                    .map(v->R.ok().setResult(v));
                        }else{
                            return null;
                        }
                    })
                    .map(o -> R.ok().setResult(o));

    }

    @PostMapping("warehousingPd")
    public Mono<R> warehousingPd(@RequestBody Map map){
        String riqi = map.get("riqi").toString();
        String flg = map.containsKey("flg") ?map.get("flg").toString():"1";//是否启用日期

        //入库单成本核算
        List<StockCostAcc> scList =  new ArrayList<>();
        //采购 其他  审核 日期
        //根据配置  仓库   存货
        return groupStockAccountRepository.findAllByCoCode("102")
                .collectList()
                .map(i -> i.size()>0?i.get(0):"")
                .flatMap(entity -> !entity.equals("")? Mono.just(assemblyMapData(entity, "3")):Mono.just(""))
                .map(str->{
                    if("1".equals(str)){
                        //按仓库核算
                        //查询所有入库单 采购入库 其他入库  筛选出存在仓库
                        //本期盘点入库
                        List<String> tlist = new ArrayList<>();
                        tlist.add("CGRKD");
                        tlist.add("QTRKD");
                        return warehousingsRepository.findAllByBillStyleAndDdate(riqi+"%", tlist)
                                .collectList()
                                .map(list->{
                                    //筛选仓库
                                    //筛选 采购入库 其他入库


                                    return null;
                                })
                                .map(v->R.ok().setResult(v));

                        //根据仓库去查询期初


                    }else if("2".equals(str)){
                        //按存货核算
                        return null;
                    }else if("3".equals(str)){
                        //批次list
                        List<String> pcList = new ArrayList<>();
                        Map pmap = new HashMap();
                        //存货成本核算
                        List<StockCostAcc> sca = new ArrayList<>();
                        //入库
                        List<StockWarehousings> swlist = new ArrayList<>();
                        //出库单
                        List<StockSaleousings> sslist = new ArrayList<>();

                        //第一步 检验到货单 期初是否有
                        // 有
                        //按批号核算 到货单 计算批号单价 *到货单查询不到 查询期初获取批次  都查不到弹窗 查询出库批号 让他手动填写单价
                        return stockCostAccRepository.findAllByIyearAndMonthAnd(riqi.split("-")[0], riqi.split("-")[1])
                                .collectList()
                                .map(list->{
                                    // 批次分组
                                    Map<String, List<StockCostAcc>> pcmap = list.stream()
                                            .collect(Collectors.groupingBy(StockCostAcc::getBatchId));
                                    //计算批次
                                    pcmap.forEach((k, value) -> {
                                        pcList.add(k);
                                    });
                                    sca.addAll(list);
                                    return pcList;
                                })
                                .flatMap(item->{
                                    //本期盘点入库
                                    List<String> tlist = new ArrayList<>();
                                    tlist.add("PYRKD");
                                    return warehousingsRepository.findAllByBillStyleAndDdate(riqi+"%", tlist)
                                            .collectList()
                                            .map(list -> {
                                                //过滤批次
                                                List<StockWarehousings> sw = list.stream().filter(v -> pcList.contains(v.getBatchId())).collect(Collectors.toList());
                                                swlist.addAll(sw);
                                                return swlist;
                                            });
                                })
                                .flatMap(item->{
                                    //查询出库
                                    List<String> tlist = new ArrayList<>();
                                    tlist.add("PKCKD");
                                    return saleousingsRepository.findAllByBillStyleAndDdate(riqi+"%", tlist)
                                            .collectList()
                                            .map(list -> {
                                                sslist.addAll(list);
                                                return sslist;
                                            });
                                })
                                .map(list->{
                                    sca.forEach(stockCostAcc->{

                                        //本期入库
                                        double sumBq = swlist.stream().filter(i-> stockCostAcc.equals(i.getBatchId())).mapToDouble(v -> {
                                            if (Objects.isNull(v.getBaseQuantity())) return 0.00d;
                                            return Double.parseDouble(v.getBaseQuantity().toString());
                                        }).sum();
                                        double sumBq1 = swlist.stream().filter(i-> stockCostAcc.equals(i.getBatchId())).mapToDouble(v -> {
                                            if (Objects.isNull(v.getSubQuantity1())) return 0.00d;
                                            return Double.parseDouble(v.getSubQuantity1().toString());
                                        }).sum();
                                        double sumBq2 = swlist.stream().filter(i-> stockCostAcc.equals(i.getBatchId())).mapToDouble(v -> {
                                            if (Objects.isNull(v.getSubQuantity2())) return 0.00d;
                                            return Double.parseDouble(v.getSubQuantity2().toString());
                                        }).sum();
                                        double icostBq = swlist.stream().filter(i-> stockCostAcc.equals(i.getBatchId())).mapToDouble(v -> {
                                            if (Objects.isNull(v.getIcost())) return 0.00d;
                                            return Double.parseDouble(v.getIcost().toString());
                                        }).sum();
                                        BigDecimal bq = new BigDecimal(stockCostAcc.getBaseQuantityBq()).add(new BigDecimal(sumBq));
                                        BigDecimal bq1 = new BigDecimal(stockCostAcc.getBaseQuantity1Bq()).add(new BigDecimal(sumBq1));
                                        BigDecimal bq2 = new BigDecimal(stockCostAcc.getBaseQuantity2Bq()).add(new BigDecimal(sumBq2));
                                        BigDecimal icost = new BigDecimal(stockCostAcc.getIcostBq()).add(new BigDecimal(icostBq));

                                        stockCostAcc.setBaseQuantityBq(bq.toString());
                                        stockCostAcc.setBaseQuantity1Bq(bq1.toString());
                                        stockCostAcc.setBaseQuantity2Bq(bq2.toString());
                                        stockCostAcc.setIcostBq(icost.toString());

                                        //本月金额
                                        BigDecimal bqTotal = new BigDecimal(stockCostAcc.getBaseQuantityTotal()).add(bq);
                                        BigDecimal bqTotal1 = new BigDecimal(stockCostAcc.getBaseQuantityTotal()).add(bq1);
                                        BigDecimal bqTotal2 = new BigDecimal(stockCostAcc.getBaseQuantityTotal()).add(bq2);
                                        BigDecimal icostTotal = new BigDecimal(stockCostAcc.getIcostTotal()).add(icost);
                                        stockCostAcc.setBaseQuantityTotal(bqTotal.toString());
                                        stockCostAcc.setBaseQuantity1Total(bqTotal1.toString());
                                        stockCostAcc.setBaseQuantity2Total(bqTotal2.toString());
                                        stockCostAcc.setIcostTotal(icostTotal.toString());
                                        stockCostAcc.setPrice(icostTotal.divide(bqTotal,10,BigDecimal.ROUND_HALF_UP).toString());


                                        //查询出库 出库
                                        List<StockSaleousings> clist = sslist.stream().filter(v -> pcList.contains(v.getBatchId()) && new BigDecimal(v.getIcost()).compareTo(BigDecimal.ZERO) >= 0).collect(Collectors.toList());
                                        double sumCk = clist.stream().mapToDouble(v -> {
                                            if (Objects.isNull(v.getBaseQuantity())) return 0.00d;
                                            return Double.parseDouble(v.getBaseQuantity().toString());
                                        }).sum();
                                        double sumCk1 = clist.stream().mapToDouble(v -> {
                                            if (Objects.isNull(v.getSubQuantity1())) return 0.00d;
                                            return Double.parseDouble(v.getSubQuantity1().toString());
                                        }).sum();
                                        double sumCk2 = clist.stream().mapToDouble(v -> {
                                            if (Objects.isNull(v.getSubQuantity2())) return 0.00d;
                                            return Double.parseDouble(v.getSubQuantity2().toString());
                                        }).sum();

                                        double icostCk = clist.stream().mapToDouble(v -> {
                                            if (Objects.isNull(v.getIcost())) return 0.00d;
                                            return Double.parseDouble(v.getIcost().toString());
                                        }).sum();

                                        BigDecimal bqc = new BigDecimal(stockCostAcc.getBaseQuantityCk()).add(new BigDecimal(sumCk));
                                        BigDecimal bq1c = new BigDecimal(stockCostAcc.getBaseQuantity1Ck()).add(new BigDecimal(sumCk1));
                                        BigDecimal bq2c = new BigDecimal(stockCostAcc.getBaseQuantity2Ck()).add(new BigDecimal(sumCk2));
                                        BigDecimal icostc = new BigDecimal(stockCostAcc.getIcostBq()).add(new BigDecimal(icostCk));

                                        stockCostAcc.setBaseQuantityCk(bqc.toString());
                                        stockCostAcc.setBaseQuantity1Ck(bq1c.toString());
                                        stockCostAcc.setBaseQuantity2Ck(bq2c.toString());
                                        stockCostAcc.setIcostCk(icostc.toString());


                                        //结存 本月 - 出库
                                        BigDecimal bqJc = new BigDecimal(stockCostAcc.getBaseQuantityTotal()).subtract(new BigDecimal(stockCostAcc.getBaseQuantityCk()));
                                        BigDecimal bqJc1 = new BigDecimal(stockCostAcc.getBaseQuantity1Total()).subtract(new BigDecimal(stockCostAcc.getBaseQuantity1Ck()));
                                        BigDecimal bqJc2 = new BigDecimal(stockCostAcc.getBaseQuantity2Total()).subtract(new BigDecimal(stockCostAcc.getBaseQuantity2Ck()));
                                        BigDecimal icostJc = new BigDecimal(stockCostAcc.getIcostBq()).subtract(new BigDecimal(stockCostAcc.getIcostQc()));
                                        stockCostAcc.setBaseQuantityJc(bqJc+"");
                                        stockCostAcc.setBaseQuantity1Jc(bqJc1+"");
                                        stockCostAcc.setBaseQuantity2Jc(bqJc2+"");
                                        stockCostAcc.setIcostJc(icostJc.divide(bqJc,10,BigDecimal.ROUND_HALF_UP).toString());

                                        scList.add(stockCostAcc);

                                    });
                                    return scList;
                                })
                                .flatMap(item->{
                                    return stockCostAccRepository.saveAll(item).collectList();
                                })
                                .map(item->{
                                    //更新到出库单价>>> 出库list 过滤对应批号 跟新对应批号的单价
                                    List<StockSaleousings> clist = sslist.stream().filter(v -> pcList.contains(v.getBatchId())).map(v->{
                                        v.setPrice(pmap.get(v.getBatchId()).toString());
                                        return v;
                                    }).collect(Collectors.toList());
                                    return clist;
                                })
                                .flatMap(item->{
                                    return saleousingsRepository.saveAll(item).collectList();
                                })
                                .flatMap(item->{
                                    //更新到入库单主表成本核算标志
                                    List<String> slist = swlist.stream().filter(v -> pcList.contains(v.getBatchId())).map(v->v.getCcode()).distinct().collect(Collectors.toList());
                                    return saleousingRepository.updateCostStatusByCcodes(slist).thenReturn(scList);
                                })
                                .flatMap(item->{
                                    //更新到出库单主表成本核算标志
                                    List<String> clist = sslist.stream().filter(v -> pcList.contains(v.getBatchId())).map(v->v.getCcode()).distinct().collect(Collectors.toList());
                                    return warehousingRepository.updateCostStatusByCcodes(clist).thenReturn(scList);
                                })
                                .map(v->R.ok().setResult(v));
                    }else{
                        return null;
                    }
                })
                .map(o -> R.ok().setResult(o));

    }

    @PostMapping("warehousingDb")
    public Mono<R> warehousingDb(@RequestBody Map map){
        String riqi = map.get("riqi").toString();
        String flg = map.containsKey("flg") ?map.get("flg").toString():"1";//是否启用日期

        //入库单成本核算
        List<StockCostAcc> scList =  new ArrayList<>();
        //采购 其他  审核 日期
        //根据配置  仓库   存货
        return groupStockAccountRepository.findAllByCoCode("102")
                .collectList()
                .map(i -> i.size()>0?i.get(0):"")
                .flatMap(entity -> !entity.equals("")? Mono.just(assemblyMapData(entity, "3")):Mono.just(""))
                .map(str->{
                    if("1".equals(str)){
                        //按仓库核算
                        //查询所有入库单 采购入库 其他入库  筛选出存在仓库
                        //本期盘点入库
                        List<String> tlist = new ArrayList<>();
                        tlist.add("CGRKD");
                        tlist.add("QTRKD");
                        return warehousingsRepository.findAllByBillStyleAndDdate(riqi+"%", tlist)
                                .collectList()
                                .map(list->{
                                    //筛选仓库
                                    //筛选 采购入库 其他入库


                                    return null;
                                })
                                .map(v->R.ok().setResult(v));

                        //根据仓库去查询期初


                    }else if("2".equals(str)){
                        //按存货核算
                        return null;
                    }else if("3".equals(str)){
                        //批次list
                        List<String> pcList = new ArrayList<>();
                        Map pmap = new HashMap();
                        //存货成本核算
                        List<StockCostAcc> sca = new ArrayList<>();
                        //入库
                        List<StockWarehousings> swlist = new ArrayList<>();
                        //出库单
                        List<StockSaleousings> sslist = new ArrayList<>();

                        //第一步 检验到货单 期初是否有
                        // 有
                        //按批号核算 到货单 计算批号单价 *到货单查询不到 查询期初获取批次  都查不到弹窗 查询出库批号 让他手动填写单价
                        return stockCostAccRepository.findAllByIyearAndMonthAnd(riqi.split("-")[0], riqi.split("-")[1])
                                .collectList()
                                .map(list->{
                                    // 批次分组
                                    Map<String, List<StockCostAcc>> pcmap = list.stream()
                                            .collect(Collectors.groupingBy(StockCostAcc::getBatchId));
                                    //计算批次
                                    pcmap.forEach((k, value) -> {
                                        pcList.add(k);
                                    });
                                    sca.addAll(list);
                                    return pcList;
                                })
                                .flatMap(item->{
                                    //本期盘点入库
                                    List<String> tlist = new ArrayList<>();
                                    tlist.add("DBRKD");
                                    return warehousingsRepository.findAllByBillStyleAndDdate(riqi+"%", tlist)
                                            .collectList()
                                            .map(list -> {
                                                //过滤批次
                                                List<StockWarehousings> sw = list.stream().filter(v -> pcList.contains(v.getBatchId())).collect(Collectors.toList());
                                                swlist.addAll(sw);
                                                return swlist;
                                            });
                                })
                                .flatMap(item->{
                                    //查询出库
                                    List<String> tlist = new ArrayList<>();
                                    tlist.add("DBCKD");
                                    return saleousingsRepository.findAllByBillStyleAndDdate(riqi+"%", tlist)
                                            .collectList()
                                            .map(list -> {
                                                sslist.addAll(list);
                                                return sslist;
                                            });
                                })
                                .map(list->{
                                    sca.forEach(stockCostAcc->{

                                        //本期入库
                                        double sumBq = swlist.stream().filter(i-> stockCostAcc.equals(i.getBatchId())).mapToDouble(v -> {
                                            if (Objects.isNull(v.getBaseQuantity())) return 0.00d;
                                            return Double.parseDouble(v.getBaseQuantity().toString());
                                        }).sum();
                                        double sumBq1 = swlist.stream().filter(i-> stockCostAcc.equals(i.getBatchId())).mapToDouble(v -> {
                                            if (Objects.isNull(v.getSubQuantity1())) return 0.00d;
                                            return Double.parseDouble(v.getSubQuantity1().toString());
                                        }).sum();
                                        double sumBq2 = swlist.stream().filter(i-> stockCostAcc.equals(i.getBatchId())).mapToDouble(v -> {
                                            if (Objects.isNull(v.getSubQuantity2())) return 0.00d;
                                            return Double.parseDouble(v.getSubQuantity2().toString());
                                        }).sum();
                                        double icostBq = swlist.stream().filter(i-> stockCostAcc.equals(i.getBatchId())).mapToDouble(v -> {
                                            if (Objects.isNull(v.getIcost())) return 0.00d;
                                            return Double.parseDouble(v.getIcost().toString());
                                        }).sum();
                                        BigDecimal bq = new BigDecimal(stockCostAcc.getBaseQuantityBq()).add(new BigDecimal(sumBq));
                                        BigDecimal bq1 = new BigDecimal(stockCostAcc.getBaseQuantity1Bq()).add(new BigDecimal(sumBq1));
                                        BigDecimal bq2 = new BigDecimal(stockCostAcc.getBaseQuantity2Bq()).add(new BigDecimal(sumBq2));
                                        BigDecimal icost = new BigDecimal(stockCostAcc.getIcostBq()).add(new BigDecimal(icostBq));

                                        stockCostAcc.setBaseQuantityBq(bq.toString());
                                        stockCostAcc.setBaseQuantity1Bq(bq1.toString());
                                        stockCostAcc.setBaseQuantity2Bq(bq2.toString());
                                        stockCostAcc.setIcostBq(icost.toString());

                                        //本月金额
                                        BigDecimal bqTotal = new BigDecimal(stockCostAcc.getBaseQuantityTotal()).add(bq);
                                        BigDecimal bqTotal1 = new BigDecimal(stockCostAcc.getBaseQuantityTotal()).add(bq1);
                                        BigDecimal bqTotal2 = new BigDecimal(stockCostAcc.getBaseQuantityTotal()).add(bq2);
                                        BigDecimal icostTotal = new BigDecimal(stockCostAcc.getIcostTotal()).add(icost);
                                        stockCostAcc.setBaseQuantityTotal(bqTotal.toString());
                                        stockCostAcc.setBaseQuantity1Total(bqTotal1.toString());
                                        stockCostAcc.setBaseQuantity2Total(bqTotal2.toString());
                                        stockCostAcc.setIcostTotal(icostTotal.toString());
                                        stockCostAcc.setPrice(icostTotal.divide(bqTotal,10,BigDecimal.ROUND_HALF_UP).toString());


                                        //查询出库 出库
                                        List<StockSaleousings> clist = sslist.stream().filter(v -> pcList.contains(v.getBatchId()) && new BigDecimal(v.getIcost()).compareTo(BigDecimal.ZERO) >= 0).collect(Collectors.toList());
                                        double sumCk = clist.stream().mapToDouble(v -> {
                                            if (Objects.isNull(v.getBaseQuantity())) return 0.00d;
                                            return Double.parseDouble(v.getBaseQuantity().toString());
                                        }).sum();
                                        double sumCk1 = clist.stream().mapToDouble(v -> {
                                            if (Objects.isNull(v.getSubQuantity1())) return 0.00d;
                                            return Double.parseDouble(v.getSubQuantity1().toString());
                                        }).sum();
                                        double sumCk2 = clist.stream().mapToDouble(v -> {
                                            if (Objects.isNull(v.getSubQuantity2())) return 0.00d;
                                            return Double.parseDouble(v.getSubQuantity2().toString());
                                        }).sum();

                                        double icostCk = clist.stream().mapToDouble(v -> {
                                            if (Objects.isNull(v.getIcost())) return 0.00d;
                                            return Double.parseDouble(v.getIcost().toString());
                                        }).sum();

                                        BigDecimal bqc = new BigDecimal(stockCostAcc.getBaseQuantityCk()).add(new BigDecimal(sumCk));
                                        BigDecimal bq1c = new BigDecimal(stockCostAcc.getBaseQuantity1Ck()).add(new BigDecimal(sumCk1));
                                        BigDecimal bq2c = new BigDecimal(stockCostAcc.getBaseQuantity2Ck()).add(new BigDecimal(sumCk2));
                                        BigDecimal icostc = new BigDecimal(stockCostAcc.getIcostBq()).add(new BigDecimal(icostCk));

                                        stockCostAcc.setBaseQuantityCk(bqc.toString());
                                        stockCostAcc.setBaseQuantity1Ck(bq1c.toString());
                                        stockCostAcc.setBaseQuantity2Ck(bq2c.toString());
                                        stockCostAcc.setIcostCk(icostc.toString());


                                        //结存 本月 - 出库
                                        BigDecimal bqJc = new BigDecimal(stockCostAcc.getBaseQuantityTotal()).subtract(new BigDecimal(stockCostAcc.getBaseQuantityCk()));
                                        BigDecimal bqJc1 = new BigDecimal(stockCostAcc.getBaseQuantity1Total()).subtract(new BigDecimal(stockCostAcc.getBaseQuantity1Ck()));
                                        BigDecimal bqJc2 = new BigDecimal(stockCostAcc.getBaseQuantity2Total()).subtract(new BigDecimal(stockCostAcc.getBaseQuantity2Ck()));
                                        BigDecimal icostJc = new BigDecimal(stockCostAcc.getIcostBq()).subtract(new BigDecimal(stockCostAcc.getIcostQc()));
                                        stockCostAcc.setBaseQuantityJc(bqJc+"");
                                        stockCostAcc.setBaseQuantity1Jc(bqJc1+"");
                                        stockCostAcc.setBaseQuantity2Jc(bqJc2+"");
                                        stockCostAcc.setIcostJc(icostJc.divide(bqJc,10,BigDecimal.ROUND_HALF_UP).toString());

                                        scList.add(stockCostAcc);

                                    });
                                    return scList;
                                })
                                .flatMap(item->{
                                    return stockCostAccRepository.saveAll(item).collectList();
                                })
                                .map(item->{
                                    //更新到出库单价>>> 出库list 过滤对应批号 跟新对应批号的单价
                                    List<StockSaleousings> clist = sslist.stream().filter(v -> pcList.contains(v.getBatchId())).map(v->{
                                        v.setPrice(pmap.get(v.getBatchId()).toString());
                                        return v;
                                    }).collect(Collectors.toList());
                                    return clist;
                                })
                                .flatMap(item->{
                                    return saleousingsRepository.saveAll(item).collectList();
                                })
                                .flatMap(item->{
                                    //更新到入库单主表成本核算标志
                                    List<String> slist = swlist.stream().filter(v -> pcList.contains(v.getBatchId())).map(v->v.getCcode()).distinct().collect(Collectors.toList());
                                    return saleousingRepository.updateCostStatusByCcodes(slist).thenReturn(scList);
                                })
                                .flatMap(item->{
                                    //更新到出库单主表成本核算标志
                                    List<String> clist = sslist.stream().filter(v -> pcList.contains(v.getBatchId())).map(v->v.getCcode()).distinct().collect(Collectors.toList());
                                    return warehousingRepository.updateCostStatusByCcodes(clist).thenReturn(scList);
                                })
                                .map(v->R.ok().setResult(v));
                    }else{
                        return null;
                    }
                })
                .map(o -> R.ok().setResult(o));

    }

    @PostMapping("warehousingXt")
    public Mono<R> warehousingXt(@RequestBody Map map){
        String riqi = map.get("riqi").toString();
        String flg = map.containsKey("flg") ?map.get("flg").toString():"1";//是否启用日期

        //入库单成本核算
        List<StockCostAcc> scList =  new ArrayList<>();
        //采购 其他  审核 日期
        //根据配置  仓库   存货
        return groupStockAccountRepository.findAllByCoCode("102")
                .collectList()
                .map(i -> i.size()>0?i.get(0):"")
                .flatMap(entity -> !entity.equals("")? Mono.just(assemblyMapData(entity, "3")):Mono.just(""))
                .map(str->{
                    if("1".equals(str)){
                        //按仓库核算
                        //查询所有入库单 采购入库 其他入库  筛选出存在仓库
                        //本期盘点入库
                        List<String> tlist = new ArrayList<>();
                        tlist.add("CGRKD");
                        tlist.add("QTRKD");
                        return warehousingsRepository.findAllByBillStyleAndDdate(riqi+"%", tlist)
                                .collectList()
                                .map(list->{
                                    //筛选仓库
                                    //筛选 采购入库 其他入库


                                    return null;
                                })
                                .map(v->R.ok().setResult(v));

                        //根据仓库去查询期初


                    }else if("2".equals(str)){
                        //按存货核算
                        return null;
                    }else if("3".equals(str)){
                        //批次list
                        List<String> pcList = new ArrayList<>();
                        Map pmap = new HashMap();
                        //存货成本核算
                        List<StockCostAcc> sca = new ArrayList<>();
                        //入库
                        List<StockWarehousings> swlist = new ArrayList<>();
                        //出库单
                        List<StockSaleousings> sslist = new ArrayList<>();

                        //第一步 检验到货单 期初是否有
                        // 有
                        //按批号核算 到货单 计算批号单价 *到货单查询不到 查询期初获取批次  都查不到弹窗 查询出库批号 让他手动填写单价
                        return stockCostAccRepository.findAllByIyearAndMonthAnd(riqi.split("-")[0], riqi.split("-")[1])
                                .collectList()
                                .map(list->{
                                    // 批次分组
                                    Map<String, List<StockCostAcc>> pcmap = list.stream()
                                            .collect(Collectors.groupingBy(StockCostAcc::getBatchId));
                                    //计算批次
                                    pcmap.forEach((k, value) -> {
                                        pcList.add(k);
                                    });
                                    sca.addAll(list);
                                    return pcList;
                                })
                                .flatMap(item->{
                                    //本期盘点入库
                                    List<String> tlist = new ArrayList<>();
                                    tlist.add("XTZHRKD");
                                    return warehousingsRepository.findAllByBillStyleAndDdate(riqi+"%", tlist)
                                            .collectList()
                                            .map(list -> {
                                                //过滤批次
                                                List<StockWarehousings> sw = list.stream().filter(v -> pcList.contains(v.getBatchId())).collect(Collectors.toList());
                                                swlist.addAll(sw);
                                                return swlist;
                                            });
                                })
                                .flatMap(item->{
                                    //查询出库
                                    List<String> tlist = new ArrayList<>();
                                    tlist.add("XTZHCKD");
                                    return saleousingsRepository.findAllByBillStyleAndDdate(riqi+"%", tlist)
                                            .collectList()
                                            .map(list -> {
                                                sslist.addAll(list);
                                                return sslist;
                                            });
                                })
                                .map(list->{
                                    sca.forEach(stockCostAcc->{

                                        //本期入库
                                        double sumBq = swlist.stream().filter(i-> stockCostAcc.equals(i.getBatchId())).mapToDouble(v -> {
                                            if (Objects.isNull(v.getBaseQuantity())) return 0.00d;
                                            return Double.parseDouble(v.getBaseQuantity().toString());
                                        }).sum();
                                        double sumBq1 = swlist.stream().filter(i-> stockCostAcc.equals(i.getBatchId())).mapToDouble(v -> {
                                            if (Objects.isNull(v.getSubQuantity1())) return 0.00d;
                                            return Double.parseDouble(v.getSubQuantity1().toString());
                                        }).sum();
                                        double sumBq2 = swlist.stream().filter(i-> stockCostAcc.equals(i.getBatchId())).mapToDouble(v -> {
                                            if (Objects.isNull(v.getSubQuantity2())) return 0.00d;
                                            return Double.parseDouble(v.getSubQuantity2().toString());
                                        }).sum();
                                        double icostBq = swlist.stream().filter(i-> stockCostAcc.equals(i.getBatchId())).mapToDouble(v -> {
                                            if (Objects.isNull(v.getIcost())) return 0.00d;
                                            return Double.parseDouble(v.getIcost().toString());
                                        }).sum();
                                        BigDecimal bq = new BigDecimal(stockCostAcc.getBaseQuantityBq()).add(new BigDecimal(sumBq));
                                        BigDecimal bq1 = new BigDecimal(stockCostAcc.getBaseQuantity1Bq()).add(new BigDecimal(sumBq1));
                                        BigDecimal bq2 = new BigDecimal(stockCostAcc.getBaseQuantity2Bq()).add(new BigDecimal(sumBq2));
                                        BigDecimal icost = new BigDecimal(stockCostAcc.getIcostBq()).add(new BigDecimal(icostBq));

                                        stockCostAcc.setBaseQuantityBq(bq.toString());
                                        stockCostAcc.setBaseQuantity1Bq(bq1.toString());
                                        stockCostAcc.setBaseQuantity2Bq(bq2.toString());
                                        stockCostAcc.setIcostBq(icost.toString());

                                        //本月金额
                                        BigDecimal bqTotal = new BigDecimal(stockCostAcc.getBaseQuantityTotal()).add(bq);
                                        BigDecimal bqTotal1 = new BigDecimal(stockCostAcc.getBaseQuantityTotal()).add(bq1);
                                        BigDecimal bqTotal2 = new BigDecimal(stockCostAcc.getBaseQuantityTotal()).add(bq2);
                                        BigDecimal icostTotal = new BigDecimal(stockCostAcc.getIcostTotal()).add(icost);
                                        stockCostAcc.setBaseQuantityTotal(bqTotal.toString());
                                        stockCostAcc.setBaseQuantity1Total(bqTotal1.toString());
                                        stockCostAcc.setBaseQuantity2Total(bqTotal2.toString());
                                        stockCostAcc.setIcostTotal(icostTotal.toString());
                                        stockCostAcc.setPrice(icostTotal.divide(bqTotal,10,BigDecimal.ROUND_HALF_UP).toString());


                                        //查询出库 出库
                                        List<StockSaleousings> clist = sslist.stream().filter(v -> pcList.contains(v.getBatchId()) && new BigDecimal(v.getIcost()).compareTo(BigDecimal.ZERO) >= 0).collect(Collectors.toList());
                                        double sumCk = clist.stream().mapToDouble(v -> {
                                            if (Objects.isNull(v.getBaseQuantity())) return 0.00d;
                                            return Double.parseDouble(v.getBaseQuantity().toString());
                                        }).sum();
                                        double sumCk1 = clist.stream().mapToDouble(v -> {
                                            if (Objects.isNull(v.getSubQuantity1())) return 0.00d;
                                            return Double.parseDouble(v.getSubQuantity1().toString());
                                        }).sum();
                                        double sumCk2 = clist.stream().mapToDouble(v -> {
                                            if (Objects.isNull(v.getSubQuantity2())) return 0.00d;
                                            return Double.parseDouble(v.getSubQuantity2().toString());
                                        }).sum();

                                        double icostCk = clist.stream().mapToDouble(v -> {
                                            if (Objects.isNull(v.getIcost())) return 0.00d;
                                            return Double.parseDouble(v.getIcost().toString());
                                        }).sum();

                                        BigDecimal bqc = new BigDecimal(stockCostAcc.getBaseQuantityCk()).add(new BigDecimal(sumCk));
                                        BigDecimal bq1c = new BigDecimal(stockCostAcc.getBaseQuantity1Ck()).add(new BigDecimal(sumCk1));
                                        BigDecimal bq2c = new BigDecimal(stockCostAcc.getBaseQuantity2Ck()).add(new BigDecimal(sumCk2));
                                        BigDecimal icostc = new BigDecimal(stockCostAcc.getIcostBq()).add(new BigDecimal(icostCk));

                                        stockCostAcc.setBaseQuantityCk(bqc.toString());
                                        stockCostAcc.setBaseQuantity1Ck(bq1c.toString());
                                        stockCostAcc.setBaseQuantity2Ck(bq2c.toString());
                                        stockCostAcc.setIcostCk(icostc.toString());


                                        //结存 本月 - 出库
                                        BigDecimal bqJc = new BigDecimal(stockCostAcc.getBaseQuantityTotal()).subtract(new BigDecimal(stockCostAcc.getBaseQuantityCk()));
                                        BigDecimal bqJc1 = new BigDecimal(stockCostAcc.getBaseQuantity1Total()).subtract(new BigDecimal(stockCostAcc.getBaseQuantity1Ck()));
                                        BigDecimal bqJc2 = new BigDecimal(stockCostAcc.getBaseQuantity2Total()).subtract(new BigDecimal(stockCostAcc.getBaseQuantity2Ck()));
                                        BigDecimal icostJc = new BigDecimal(stockCostAcc.getIcostBq()).subtract(new BigDecimal(stockCostAcc.getIcostQc()));
                                        stockCostAcc.setBaseQuantityJc(bqJc+"");
                                        stockCostAcc.setBaseQuantity1Jc(bqJc1+"");
                                        stockCostAcc.setBaseQuantity2Jc(bqJc2+"");
                                        stockCostAcc.setIcostJc(icostJc.divide(bqJc,10,BigDecimal.ROUND_HALF_UP).toString());

                                        scList.add(stockCostAcc);

                                    });
                                    return scList;
                                })
                                .flatMap(item->{
                                    return stockCostAccRepository.saveAll(item).collectList();
                                })
                                .map(item->{
                                    //更新到出库单价>>> 出库list 过滤对应批号 跟新对应批号的单价
                                    List<StockSaleousings> clist = sslist.stream().filter(v -> pcList.contains(v.getBatchId())).map(v->{
                                        v.setPrice(pmap.get(v.getBatchId()).toString());
                                        return v;
                                    }).collect(Collectors.toList());
                                    return clist;
                                })
                                .flatMap(item->{
                                    return saleousingsRepository.saveAll(item).collectList();
                                })
                                .flatMap(item->{
                                    //更新到入库单主表成本核算标志
                                    List<String> slist = swlist.stream().filter(v -> pcList.contains(v.getBatchId())).map(v->v.getCcode()).distinct().collect(Collectors.toList());
                                    return saleousingRepository.updateCostStatusByCcodes(slist).thenReturn(scList);
                                })
                                .flatMap(item->{
                                    //更新到出库单主表成本核算标志
                                    List<String> clist = sslist.stream().filter(v -> pcList.contains(v.getBatchId())).map(v->v.getCcode()).distinct().collect(Collectors.toList());
                                    return warehousingRepository.updateCostStatusByCcodes(clist).thenReturn(scList);
                                })
                                .map(v->R.ok().setResult(v));
                    }else{
                        return null;
                    }
                })
                .map(o -> R.ok().setResult(o));

    }


    @PostMapping("warehousingCk")
    public Mono<R> warehousingCk(@RequestBody Map map){
        String riqi = map.get("riqi").toString();
        String flg = map.containsKey("flg") ?map.get("flg").toString():"1";//是否启用日期

        //入库单成本核算
        List<StockCostAcc> scList =  new ArrayList<>();
        //采购 其他  审核 日期
        //根据配置  仓库   存货
        return groupStockAccountRepository.findAllByCoCode("102")
                .collectList()
                .map(i -> i.size()>0?i.get(0):"")
                .flatMap(entity -> !entity.equals("")? Mono.just(assemblyMapData(entity, "3")):Mono.just(""))
                .map(str->{
                    if("1".equals(str)){
                        //按仓库核算
                        //查询所有入库单 采购入库 其他入库  筛选出存在仓库
                        //本期盘点入库
                        List<String> tlist = new ArrayList<>();
                        tlist.add("CGRKD");
                        tlist.add("QTRKD");
                        return warehousingsRepository.findAllByBillStyleAndDdate(riqi+"%", tlist)
                                .collectList()
                                .map(list->{
                                    //筛选仓库
                                    //筛选 采购入库 其他入库


                                    return null;
                                })
                                .map(v->R.ok().setResult(v));

                        //根据仓库去查询期初

                    }else if("2".equals(str)){
                        //按存货核算
                        return null;
                    }else if("3".equals(str)){
                        //批次list
                        List<String> pcList = new ArrayList<>();
                        //存货成本核算
                        List<StockCostAcc> sca = new ArrayList<>();
                        return stockCostAccRepository.findAllByIyearAndMonthAnd(riqi.split("-")[0], riqi.split("-")[1])
                                .collectList()
                                .map(list->{
                                    // 批次分组
                                    Map<String, List<StockCostAcc>> pcmap = list.stream()
                                            .collect(Collectors.groupingBy(StockCostAcc::getBatchId));
                                    //计算批次
                                    pcmap.forEach((k, value) -> {
                                        pcList.add(k);
                                    });
                                    sca.addAll(list);
                                    return pcList;
                                })
                                .map(list->{
                                    sca.forEach(stockCostAcc->{
                                        //结存 本月 - 出库
                                        BigDecimal bqJc = new BigDecimal(stockCostAcc.getBaseQuantityTotal()).subtract(new BigDecimal(stockCostAcc.getBaseQuantityCk()));
                                        BigDecimal bqJc1 = new BigDecimal(stockCostAcc.getBaseQuantity1Total()).subtract(new BigDecimal(stockCostAcc.getBaseQuantity1Ck()));
                                        BigDecimal bqJc2 = new BigDecimal(stockCostAcc.getBaseQuantity2Total()).subtract(new BigDecimal(stockCostAcc.getBaseQuantity2Ck()));
                                        BigDecimal icostJc = new BigDecimal(stockCostAcc.getIcostBq()).subtract(new BigDecimal(stockCostAcc.getIcostQc()));
                                        stockCostAcc.setBaseQuantityJc(bqJc+"");
                                        stockCostAcc.setBaseQuantity1Jc(bqJc1+"");
                                        stockCostAcc.setBaseQuantity2Jc(bqJc2+"");
                                        stockCostAcc.setIcostJc(icostJc.divide(bqJc,10,BigDecimal.ROUND_HALF_UP).toString());
                                        scList.add(stockCostAcc);
                                    });
                                    return scList;
                                })
                                .flatMap(item->{
                                    return stockCostAccRepository.saveAll(item).collectList();
                                })
                                .map(v->R.ok().setResult(v));
                    }else{
                        return null;
                    }
                })
                .map(o -> R.ok().setResult(o));

    }



    @SneakyThrows
    private String assemblyMapData(Object obj, String type) {
        GroupStockAccount entity = (GroupStockAccount) obj;
        String kcCostAccounting = "";
        switch (type){
            case "3": //成本
                kcCostAccounting = StringUtils.isBlank(entity.getKcCostAccounting()) ? "" : entity.getKcCostAccounting();
                break;
        }
        return kcCostAccounting;
    }




    @PostMapping(value = "/getCkPrice")
    @ApiOperation(value = "出库成本单价生成", notes = "出库成本单价生成")
    public Mono<R> getCkPrice(@RequestBody Map maps){
        //接收仓库唯一码、存货唯一码、批号、单据日期参数用于计算单价
        String stockNum = maps.get("stockNum").toString();
        String stockCangku = maps.get("stockCangku").toString();
        String batchId = maps.containsKey("batchId")?maps.get("batchId").toString():"";
        String ddate = maps.containsKey("ddate")?maps.get("ddate").toString():"";
        String year = maps.get("year").toString();
        // 参数设置：入库保存状态就是现存量 标志 1是查询所有 0 查询已审核
        String rkBcheck=maps.get("rkBcheck")==null?"0":maps.get("rkBcheck").toString();
        String ckBcheck=maps.get("ckBcheck")==null?"0":maps.get("ckBcheck").toString();
        return stockRepository.findByStockNum(stockNum)
                .flatMap(obj->{
                    //获取存货的信息
                    String type = obj.getStockValuationType();
                    //判断存货的成本核算方式
                    if(type.equals("移动平均")){
                        AtomicReference<BigDecimal> n = new AtomicReference<>(BigDecimal.ZERO);
                        AtomicReference<BigDecimal> m = new AtomicReference<>(BigDecimal.ZERO);
                        //入库
                        Mono<AtomicReference<BigDecimal>> map1 = warehousingsRepository.findAllByCwhcodeAndCinvodeAndDdate(stockCangku, stockNum, year, ddate)
                                .filter(o-> "0".equals(rkBcheck)? "1".equals(o.getBcheck()) : true)
                                .collectList()
                                .map(list->{
                                    //汇总主数量 无税金额
                                    double num = list.stream().mapToDouble(v -> {
                                        if (Objects.isNull(v.getBaseQuantity())) return 0.00d;
                                        return Double.parseDouble(v.getBaseQuantity().toString());
                                    }).sum();

                                    double moeny = list.stream().mapToDouble(v -> {
                                        if (Objects.isNull(v.getIcost())) return 0.00d;
                                        return Double.parseDouble(v.getIcost().toString());
                                    }).sum();
                                    n.set(n.get().add(BigDecimal.valueOf(num)));
                                    m.set(m.get().add(BigDecimal.valueOf(moeny)));
                                    return n;
                                });
                        //出库调整单
                        Mono<AtomicReference<BigDecimal>> map3 = saleousingsRepository.findAllByCwhcodeAndCinvodeAndDdate(stockCangku, stockNum, year, ddate)
                                .filter(o-> "0".equals(ckBcheck)? "1".equals(o.getBcheck()) : true)
                                .collectList()
                                .map(list->{
                                    //汇总主数量 无税金额
                                    double num = list.stream().mapToDouble(v -> {
                                        if (Objects.isNull(v.getBaseQuantity())) return 0.00d;
                                        return Double.parseDouble(v.getBaseQuantity().toString());
                                    }).sum();

                                    double moeny = list.stream().mapToDouble(v -> {
                                        if (Objects.isNull(v.getIcost())) return 0.00d;
                                        return Double.parseDouble(v.getIcost().toString());
                                    }).sum();
                                    n.set(n.get().subtract(BigDecimal.valueOf(num)));
                                    m.set(m.get().subtract(BigDecimal.valueOf(moeny)));
                                    return n;
                                });
                        //期初
                        Mono<AtomicReference<BigDecimal>> map2 = beginBalanceRepository.findAllByCwhcodeAndCinvode(stockCangku, stockNum, year)
                                .collectList()
                                .map(list -> {
                                    //汇总主数量 无税金额
                                    double num = list.stream().mapToDouble(v -> {
                                        if (Objects.isNull(v.getBaseQuantity())) return 0.00d;
                                        return Double.parseDouble(v.getBaseQuantity().toString());
                                    }).sum();

                                    double moeny = list.stream().mapToDouble(v -> {
                                        if (Objects.isNull(v.getIcost())) return 0.00d;
                                        return Double.parseDouble(v.getIcost().toString());
                                    }).sum();
                                    n.set(n.get().add(BigDecimal.valueOf(num)));
                                    m.set(m.get().add(BigDecimal.valueOf(moeny)));
                                    return m;
                                });

                        //红字出库( 材料领用 销售出库 其他出)
                        Mono<AtomicReference<BigDecimal>> map4 = saleousingsRepository.findAllByBillStyleAndYearAndDdate(year,ddate)
                                .filter(v-> {
                                    if(ckBcheck.equals("0")){
                                        return  "1".equals(v.getBcheck());
                                    }else{
                                        return  "0".equals(v.getBcheck()) || Objects.isNull(v.getBcheck());
                                    }
                                })
                                .collectList()
                                .map(list -> {
                                    //汇总主数量 无税金额
                                    double num = list.stream().mapToDouble(v -> {
                                        if (Objects.isNull(v.getBaseQuantity())) return 0.00d;
                                        return Double.parseDouble(v.getBaseQuantity().toString());
                                    }).sum();

                                    double moeny = list.stream().mapToDouble(v -> {
                                        if (Objects.isNull(v.getIcost())) return 0.00d;
                                        return Double.parseDouble(v.getIcost().toString());
                                    }).sum();
                                    n.set(n.get().add(BigDecimal.valueOf(num)));
                                    m.set(m.get().add(BigDecimal.valueOf(moeny)));
                                    return m;
                                });

                        //计算成本单价
                        return map1.zipWith(map2).zipWith(map3).zipWith(map4).map(v->{
                            //数量0 =单价0
                            if(n.get().compareTo(BigDecimal.ZERO) == 0){
                                return BigDecimal.ZERO;
                            }
                            //金额0 =单价0
                            if(m.get().compareTo(BigDecimal.ZERO) == 0){
                                return BigDecimal.ZERO;
                            }
                            //数量负数 金额不是负数  =单价0
                            if(n.get().compareTo(BigDecimal.ZERO) == -1 && m.get().compareTo(BigDecimal.ZERO) == 1){
                                return BigDecimal.ZERO;
                            }
                            //金额负数 数量不是负数 =单价0
                            if(m.get().compareTo(BigDecimal.ZERO) == -1 && n.get().compareTo(BigDecimal.ZERO) == 1){
                                return BigDecimal.ZERO;
                            }
                            return m.get().divide(n.get(),10,BigDecimal.ROUND_HALF_UP);
                        });

                    }else if(type.equals("相同批号")){
                        AtomicReference<BigDecimal> n = new AtomicReference<>(BigDecimal.ZERO);
                        AtomicReference<BigDecimal> m = new AtomicReference<>(BigDecimal.ZERO);
                        if(Objects.isNull(batchId)){
                            return  Mono.just(BigDecimal.ZERO);
                        }
                        //入库
                        Mono<AtomicReference<BigDecimal>> map1 = warehousingsRepository.findAllByCwhcodeAndCinvodeAndDdateAndBatchId(stockCangku, stockNum, year, ddate, batchId)
                                .filter(list -> {
                                    //过滤采购入库单数量小于1
                                    /*if("CGRKD".equals(list.getBillStyle()) ){
                                        if(new BigDecimal(list.getBaseQuantity()).compareTo(BigDecimal.ZERO) > 1){
                                            return true;
                                        }
                                    }*/

                                    if("0".equals(ckBcheck)){
                                        return "1".equals(list.getBcheck());
                                    }else{
                                        return true;
                                    }
                                })
                                .collectList()
                                .map(list -> {
                                    //汇总主数量 无税金额
                                    double num = list.stream().mapToDouble(v -> {
                                        if (Objects.isNull(v.getBaseQuantity())) return 0.00d;
                                        return Double.parseDouble(v.getBaseQuantity().toString());
                                    }).sum();

                                    double moeny = list.stream().mapToDouble(v -> {
                                        if (Objects.isNull(v.getIcost())) return 0.00d;
                                        return Double.parseDouble(v.getIcost().toString());
                                    }).sum();
                                    n.set(n.get().add(BigDecimal.valueOf(num)));
                                    m.set(m.get().add(BigDecimal.valueOf(moeny)));
                                    return n;
                                });

                        //出库调整单
                        Mono<AtomicReference<BigDecimal>> map3 = saleousingsRepository.findAllByCwhcodeAndCinvodeAndDdateAndBatchId(stockCangku, stockNum, year, ddate, batchId)
                                .collectList()
                                .map(list->{
                                    //汇总主数量 无税金额
                                    double num = list.stream().mapToDouble(v -> {
                                        if (Objects.isNull(v.getBaseQuantity())) return 0.00d;
                                        return Double.parseDouble(v.getBaseQuantity().toString());
                                    }).sum();

                                    double moeny = list.stream().mapToDouble(v -> {
                                        if (Objects.isNull(v.getIcost())) return 0.00d;
                                        return Double.parseDouble(v.getIcost().toString());
                                    }).sum();
                                    n.set(n.get().subtract(BigDecimal.valueOf(num)));
                                    m.set(m.get().subtract(BigDecimal.valueOf(moeny)));
                                    return n;
                                });

                        //期初
                        Mono<AtomicReference<BigDecimal>> map2 = beginBalanceRepository.findAllByCwhcodeAndCinvodeAndDdateAndBatchId(stockCangku, stockNum, year, batchId)
                                .collectList()
                                .map(list -> {
                                    //汇总主数量 无税金额
                                    double num = list.stream().mapToDouble(v -> {
                                        if (Objects.isNull(v.getBaseQuantity())) return 0.00d;
                                        return Double.parseDouble(v.getBaseQuantity().toString());
                                    }).sum();

                                    double moeny = list.stream().mapToDouble(v -> {
                                        if (Objects.isNull(v.getIcost())) return 0.00d;
                                        return Double.parseDouble(v.getIcost().toString());
                                    }).sum();
                                    n.set(n.get().add(BigDecimal.valueOf(num)));
                                    m.set(m.get().add(BigDecimal.valueOf(moeny)));
                                    return m;
                                });
                        //计算成本单价
                        return map1.zipWith(map2).zipWith(map3).map(v->{
                            //数量0 =单价0
                            if(n.get().compareTo(BigDecimal.ZERO) == 0){
                                return BigDecimal.ZERO;
                            }
                            //金额0 =单价0
                            if(m.get().compareTo(BigDecimal.ZERO) == 0){
                                return BigDecimal.ZERO;
                            }
                            //数量负数 金额不是负数  =单价0
                            if(n.get().compareTo(BigDecimal.ZERO) == -1 && m.get().compareTo(BigDecimal.ZERO) == 1){
                                return BigDecimal.ZERO;
                            }
                            //金额负数 数量不是负数 =单价0
                            if(m.get().compareTo(BigDecimal.ZERO) == -1 && n.get().compareTo(BigDecimal.ZERO) == 1){
                                return BigDecimal.ZERO;
                            }
                            return m.get().divide(n.get(),10,BigDecimal.ROUND_HALF_UP);
                        });
                    }else if(type.equals("手工计价")){
                        //无成本单价反馈，需要手工输入成本价格 
                    }else if(type.equals("先进先出")){

                    }
                    return  Mono.just(BigDecimal.ZERO);
                })
                .map(o-> R.ok().setResult(o));
    }

    @PostMapping(value = "/getCkPriceList")
    @ApiOperation(value = "批量获取出库成本单价", notes = "批量获取出库成本单价")
    public Mono<R> getCkPriceList(@RequestBody Map maps){
        //接收仓库唯一码、存货唯一码、批号、单据日期参数用于计算单价
        String ddate = maps.get("ddate").toString();
        String year = maps.get("year").toString();
        // 参数设置：入库保存状态就是现存量 标志 1是查询所有 0 查询已审核
        String rkBcheck=maps.get("rkBcheck")==null?"0":maps.get("rkBcheck").toString();
        String ckBcheck=maps.get("ckBcheck")==null?"0":maps.get("ckBcheck").toString();
        //list   成本单价计算 + 现存量查询
        List<StockCostAccRo> scList = JSON.parseArray(maps.get("stockCostAccRo").toString(), StockCostAccRo.class);
        List<String> stocks = scList.stream().map(v -> v.getStockNum()).collect(Collectors.toList());
        List<StockVo> stockVoList = new ArrayList<>();
        List<StockWarehousings> list1 = new ArrayList<>();
        List<StockBeginBalance> list2 = new ArrayList<>();
        List<StockSaleousings> hzckList =  new ArrayList<>();
        List<StockSaleousings> list3 = new ArrayList<>();
        return stockRepository.findAllByStockNums(stocks)
                .collectList()
                .map(list-> stockVoList.addAll(list))
                .flatMap(s->{
                    //入库 采购 其他
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
                .map(o-> R.ok().setResult(o));
    }


    @PostMapping("warehousingOne")
    public Mono<R> warehousingOne(@RequestBody Map map){
        String riqi = map.get("riqi").toString()+"-31";
        // 参数设置：入库保存状态就是现存量 标志 1是查询所有 0 查询已审核
        String rkBcheck=map.get("rkBcheck")==null?"0":map.get("rkBcheck").toString();
        String ckBcheck=map.get("ckBcheck")==null?"0":map.get("ckBcheck").toString();
        //成本核算模式
        String hs=map.get("hsFlg")==null?"2":map.get("hsFlg").toString();//2存货3仓库存货
        String flg = map.containsKey("flg") ?map.get("flg").toString():"1";//是否启用日期
        //入库单成本核算
        List<StockSaleousings> ckList =  new ArrayList<>();
        List<StockBeginBalance> qcList =  new ArrayList<>();
        List<StockSaleousings> hzckList =  new ArrayList<>();
        List<StockWarehousings> rkList =  new ArrayList<>();
        List<StockSaleousings> cktzList =  new ArrayList<>();
        List<StockSaleousings> salist =  new ArrayList<>();
        //查询所有销售出库单然后根据销售出库单查询存货获取核算方式 ('XSCKD', 'QTCKD','CLLYD')
        return saleousingsRepository.findAllByBillStyleAndDdateDist(riqi,"XSCKD")
                .filter(v-> {
                    if(ckBcheck.equals("0")){
                        return  "1".equals(v.getBcheck());
                    }else{
                        return  "0".equals(v.getBcheck()) || Objects.isNull(v.getBcheck());
                    }
                })
                .collectList()
                .flatMap(list->{
                    ckList.addAll(list);
                    List<String> cinvodes = ckList.stream().map(v -> v.getCinvode()).collect(Collectors.toList());
                    //存货的期初
                    return cinvodes.size() > 0 ? beginBalanceRepository.findAllByCinvodes(cinvodes,"2022")
                            .collectList()
                            .map(item->{
                                qcList.addAll(item);
                                return list;
                            }): Mono.just(list);
                })
                .flatMap(list->{
                    //采购 + 其他 +3个入库 +入库调整
                    return warehousingsRepository.findAllByYearAndDate("2022",riqi)
                            .filter(v-> {
                                if(rkBcheck.equals("0")){
                                    return  "1".equals(v.getBcheck());
                                }else{
                                    return  "0".equals(v.getBcheck()) || Objects.isNull(v.getBcheck());
                                }
                            })
                            .collectList()
                            .map(item->{
                                rkList.addAll(item);
                                return list;
                            });
                })
                .flatMap(list->{
                    //-3红字出库( 材料领用 销售出库 其他出)
                    return saleousingsRepository.findAllByBillStyleAndYearAndDdate("2022",riqi)
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
                .flatMap(list->{
                    //出库调整单
                    return saleousingsRepository.findAllByBillStyleAndDdateDist(riqi,"CKTZD")
                            .filter(v-> {
                                if(ckBcheck.equals("0")){
                                    return  "1".equals(v.getBcheck());
                                }else{
                                    return  "0".equals(v.getBcheck()) || Objects.isNull(v.getBcheck());
                                }
                            })
                            .collectList()
                            .map(item->{
                                cktzList.addAll(item);
                                return list;
                            });
                })
                .map(list->{
                    //根据核算方式去计算
                    list.forEach(v->{
                        if("xx".equals(v.getBuname())){

                        }else if("相同批号".equals(v.getBuname())){
                            //期初 + 采购 + 其他 +入库调整 - 出库调整单  = 相同批号单价
                            //获取当前存货的期初
                            List<StockBeginBalance> qc = qcList.stream()
                                    .filter(o -> "2".equals(hs)?true:o.getStockCangkuId().equals(v.getCwhcode()))
                                    .filter(o -> o.getStockId().equals(v.getCinvode()) && o.getBatchNumber().equals(v.getBatchId())).collect(Collectors.toList());
                            //汇总主数量 无税金额
                            double num = qc.stream().mapToDouble(obj -> {
                                if (Objects.isNull(obj.getBaseQuantity())) return 0.00d;
                                return Double.parseDouble(obj.getBaseQuantity().toString());
                            }).sum();

                            double moeny = qc.stream().mapToDouble(obj -> {
                                if (Objects.isNull(obj.getIcost())) return 0.00d;
                                return Double.parseDouble(obj.getIcost().toString());
                            }).sum();

                            //获取当前出库存货 单据日期/制单日期之前的入库单
                            List<StockWarehousings> rk = rkList.stream()
                                    .filter(o -> "CGRKD".equals(o.getBillStyle()) || "QTRKD".equals(o.getBillStyle())  || "RKTZD".equals(o.getBillStyle()))
                                    .filter(o -> o.getBatchId().equals(v.getBatchId()))
                                    .filter(o -> "2".equals(hs)?true:o.getCwhcode().equals(v.getCwhcode()))
                                    .filter(o -> o.getCinvode().equals(v.getCinvode()) && v.getDdate().compareTo(o.getDdate()) >= 0 ).collect(Collectors.toList());//&& v.getCmakerTime().compareTo(o.getCmakerTime()) >= 0
                            //汇总主数量 无税金额
                            double num1 = rk.stream().mapToDouble(obj -> {
                                if (Objects.isNull(obj.getBaseQuantity())) return 0.00d;
                                return Double.parseDouble(obj.getBaseQuantity().toString());
                            }).sum();

                            double moeny1 = rk.stream().mapToDouble(obj -> {
                                if (Objects.isNull(obj.getIcost())) return 0.00d;
                                return Double.parseDouble(obj.getIcost().toString());
                            }).sum();

                            //获取当前出库存货 单据日期/制单日期之前的出库调整单
                            List<StockSaleousings> cktz = cktzList.stream()
                                    .filter(o -> o.getBatchId().equals(v.getBatchId()))
                                    .filter(o -> "2".equals(hs)?true:o.getCwhcode().equals(v.getCwhcode()))
                                    .filter(o -> o.getCinvode().equals(v.getCinvode()) && v.getDdate().compareTo(o.getDdate()) >= 0 ).collect(Collectors.toList());
                            //汇总主数量 无税金额
                            double num2 = cktz.stream().mapToDouble(obj -> {
                                if (Objects.isNull(obj.getBaseQuantity())) return 0.00d;
                                return Double.parseDouble(obj.getBaseQuantity().toString());
                            }).sum();

                            double moeny2 = cktz.stream().mapToDouble(obj -> {
                                if (Objects.isNull(obj.getIcost())) return 0.00d;
                                return Double.parseDouble(obj.getIcost().toString());
                            }).sum();

                            //汇总数量 金额 计算单价
                            BigDecimal sumNum = BigDecimal.valueOf(num).add(BigDecimal.valueOf(num1)).add(BigDecimal.valueOf(num2).abs());
                            BigDecimal sumMoney = BigDecimal.valueOf(moeny).add(BigDecimal.valueOf(moeny1)).add(BigDecimal.valueOf(moeny2).abs());
                            BigDecimal price = sumMoney.divide(sumNum, 10, BigDecimal.ROUND_HALF_UP);
                            //数量0 =单价0 金额0 =单价0
                            if(sumNum.compareTo(BigDecimal.ZERO) == 0){
                                v.setPrice("0");
                            }else if(sumMoney.compareTo(BigDecimal.ZERO) == 0){
                                v.setPrice("0");
                            }else{
                                v.setPrice(price.toString());
                            }
                            v.setIcost(new BigDecimal(v.getBaseQuantity()).multiply(price).setScale(10, BigDecimal.ROUND_HALF_UP).toString());

                        }else{//"移动平均"
                            //期初 + 采购 + 其他 +3个入库 +入库调整 -出库调整单  -3红字出库( 材料领用 销售出库 其他出) = 移动平均单价
                            //获取当前存货的期初
                            List<StockBeginBalance> qc = qcList.stream()
                                    .filter(o -> "2".equals(hs)?true:o.getStockCangkuId().equals(v.getCwhcode()))
                                    .filter(o -> o.getStockId().equals(v.getCinvode())).collect(Collectors.toList());
                            //汇总主数量 无税金额
                            double num = qc.stream().mapToDouble(obj -> {
                                if (Objects.isNull(obj.getBaseQuantity())) return 0.00d;
                                return Double.parseDouble(obj.getBaseQuantity().toString());
                            }).sum();

                            double moeny = qc.stream().mapToDouble(obj -> {
                                if (Objects.isNull(obj.getIcost())) return 0.00d;
                                return Double.parseDouble(obj.getIcost().toString());
                            }).sum();
                            //获取当前出库存货 单据日期/制单日期之前的入库单
                            List<StockWarehousings> rk = rkList.stream()
                                    .filter(o -> "2".equals(hs)?true:o.getCwhcode().equals(v.getCwhcode()))
                                    .filter(o -> o.getCinvode().equals(v.getCinvode()) && v.getDdate().compareTo(o.getDdate()) >= 0 ).collect(Collectors.toList());
                            //汇总主数量 无税金额
                            double num1 = rk.stream().mapToDouble(obj -> {
                                if (Objects.isNull(obj.getBaseQuantity())) return 0.00d;
                                return Double.parseDouble(obj.getBaseQuantity().toString());
                            }).sum();

                            double moeny1 = rk.stream().mapToDouble(obj -> {
                                if (Objects.isNull(obj.getIcost())) return 0.00d;
                                return Double.parseDouble(obj.getIcost().toString());
                            }).sum();
                            //获取当前出库存货 单据日期/制单日期之前的红字出库单
                            List<StockSaleousings> hzck = hzckList.stream()
                                    .filter(o -> "2".equals(hs)?true:o.getCwhcode().equals(v.getCwhcode()))
                                    .filter(o -> o.getCinvode().equals(v.getCinvode()) && v.getDdate().compareTo(o.getDdate()) >= 0 ).collect(Collectors.toList());
                            //汇总主数量 无税金额
                            double num2 = hzck.stream().mapToDouble(obj -> {
                                if (Objects.isNull(obj.getBaseQuantity())) return 0.00d;
                                return Double.parseDouble(obj.getBaseQuantity().toString());
                            }).sum();
                            double moeny2 = hzck.stream().mapToDouble(obj -> {
                                if (Objects.isNull(obj.getIcost())) return 0.00d;
                                return Double.parseDouble(obj.getIcost().toString());
                            }).sum();

                            //获取当前出库存货 单据日期/制单日期之前的出库调整单
                            List<StockSaleousings> cktz = cktzList.stream()
                                    .filter(o -> "2".equals(hs)?true:o.getCwhcode().equals(v.getCwhcode()))
                                    .filter(o -> o.getCinvode().equals(v.getCinvode()) && v.getDdate().compareTo(o.getDdate()) >= 0 ).collect(Collectors.toList());
                            //汇总主数量 无税金额
                            double num3 = cktz.stream().mapToDouble(obj -> {
                                if (Objects.isNull(obj.getBaseQuantity())) return 0.00d;
                                return Double.parseDouble(obj.getBaseQuantity().toString());
                            }).sum();
                            double moeny3 = cktz.stream().mapToDouble(obj -> {
                                if (Objects.isNull(obj.getIcost())) return 0.00d;
                                return Double.parseDouble(obj.getIcost().toString());
                            }).sum();

                            //汇总数量 金额 计算单价
                            BigDecimal sumNum = BigDecimal.valueOf(num).add(BigDecimal.valueOf(num1)).add(BigDecimal.valueOf(num2).abs()).add(BigDecimal.valueOf(num3));
                            BigDecimal sumMoney = BigDecimal.valueOf(moeny).add(BigDecimal.valueOf(moeny1)).add(BigDecimal.valueOf(moeny2).abs()).add(BigDecimal.valueOf(moeny3));
                            BigDecimal price = sumMoney.divide(sumNum, 10, BigDecimal.ROUND_HALF_UP);
                            //数量0 =单价0 金额0 =单价0
                            if(sumNum.compareTo(BigDecimal.ZERO) == 0){
                                v.setPrice("0");
                            }else if(sumMoney.compareTo(BigDecimal.ZERO) == 0){
                                v.setPrice("0");
                            }else{
                                v.setPrice(price.toString());
                            }
                            v.setIcost(new BigDecimal(v.getBaseQuantity()).multiply(price).setScale(10, BigDecimal.ROUND_HALF_UP).toString());
                        }
                        StockSaleousings sa = new StockSaleousings();
                        BeanUtils.copyProperties(v,sa);
                        salist.add(sa);
                    });
                    return salist;
                })
                .flatMap(list-> {
                    return saleousingsRepository.saveAll(list).collectList();
                })
                .map(o->R.ok().setResult(ckList));
    }

    @PostMapping("warehousingTwo")
    public Mono<R> warehousingTwo(@RequestBody Map map){
        String riqi = map.get("riqi").toString();
        // 参数设置：入库保存状态就是现存量 标志 1是查询所有 0 查询已审核
        String rkBcheck=map.get("rkBcheck")==null?"0":map.get("rkBcheck").toString();
        String ckBcheck=map.get("ckBcheck")==null?"0":map.get("ckBcheck").toString();
        //成本核算模式
        String hs=map.get("hsFlg")==null?"2":map.get("hsFlg").toString();//2存货3仓库存货
        String flg = map.containsKey("flg") ?map.get("flg").toString():"1";//是否启用日期
        //入库单成本核算
        List<StockCostAcc> scList =  new ArrayList<>();
        List<StockSaleousings> ckList =  new ArrayList<>();
        List<StockBeginBalance> qcList =  new ArrayList<>();
        List<StockSaleousings> hzckList =  new ArrayList<>();
        List<StockWarehousings> rkList =  new ArrayList<>();
        List<StockSaleousings> cktzList =  new ArrayList<>();
        List<StockSaleousings> salist =  new ArrayList<>();
        //查询所有销售出库单然后根据销售出库单查询存货获取核算方式
        return saleousingsRepository.findAllByBillStyleAndDdateDist(riqi,"CLLYD")
                .filter(v-> {
                    if(ckBcheck.equals("0")){
                        return  "1".equals(v.getBcheck());
                    }else{
                        return  "0".equals(v.getBcheck()) || Objects.isNull(v.getBcheck());
                    }
                })
                .collectList()
                .flatMap(list->{
                    ckList.addAll(list);
                    List<String> cinvodes = ckList.stream().map(v -> v.getCinvode()).collect(Collectors.toList());
                    //存货的期初
                    return cinvodes.size() > 0 ? beginBalanceRepository.findAllByCinvodes(cinvodes,"2022")
                            .collectList()
                            .map(item->{
                                qcList.addAll(item);
                                return list;
                            }): Mono.just(list);
                })
                .flatMap(list->{
                    //采购 + 其他 +3个入库 +入库调整
                    return warehousingsRepository.findAllByYearAndDate("2022",riqi)
                            .filter(v-> {
                                if(rkBcheck.equals("0")){
                                    return  "1".equals(v.getBcheck());
                                }else{
                                    return  "0".equals(v.getBcheck()) || Objects.isNull(v.getBcheck());
                                }
                            })
                            .collectList()
                            .map(item->{
                                rkList.addAll(item);
                                return list;
                            });
                })
                .flatMap(list->{
                    //-3红字出库( 材料领用 销售出库 其他出)
                    return saleousingsRepository.findAllByBillStyleAndYearAndDdate("2022",riqi)
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
                .flatMap(list->{
                    //出库调整单
                    return saleousingsRepository.findAllByBillStyleAndDdateDist(riqi,"CKTZD")
                            .filter(v-> {
                                if(ckBcheck.equals("0")){
                                    return  "1".equals(v.getBcheck());
                                }else{
                                    return  "0".equals(v.getBcheck()) || Objects.isNull(v.getBcheck());
                                }
                            })
                            .collectList()
                            .map(item->{
                                cktzList.addAll(item);
                                return list;
                            });
                })
                .map(list->{
                    //根据核算方式去计算
                    list.forEach(v->{
                        if("xx".equals(v.getBuname())){

                        }else if("相同批号".equals(v.getBuname())){
                            //期初 + 采购 + 其他 +入库调整 - 出库调整单  = 相同批号单价
                            //获取当前存货的期初
                            List<StockBeginBalance> qc = qcList.stream()
                                    .filter(o -> "2".equals(hs)?true:o.getStockCangkuId().equals(v.getCwhcode()))
                                    .filter(o -> o.getStockId().equals(v.getCinvode()) && o.getBatchNumber().equals(v.getBatchId())).collect(Collectors.toList());
                            //汇总主数量 无税金额
                            double num = qc.stream().mapToDouble(obj -> {
                                if (Objects.isNull(obj.getBaseQuantity())) return 0.00d;
                                return Double.parseDouble(obj.getBaseQuantity().toString());
                            }).sum();

                            double moeny = qc.stream().mapToDouble(obj -> {
                                if (Objects.isNull(obj.getIcost())) return 0.00d;
                                return Double.parseDouble(obj.getIcost().toString());
                            }).sum();

                            //获取当前出库存货 单据日期/制单日期之前的入库单
                            List<StockWarehousings> rk = rkList.stream()
                                    .filter(o -> "CGRKD".equals(o.getBillStyle()) || "QTRKD".equals(o.getBillStyle())  || "RKTZD".equals(o.getBillStyle()))
                                    .filter(o -> o.getBatchId().equals(v.getBatchId()))
                                    .filter(o -> "2".equals(hs)?true:o.getCwhcode().equals(v.getCwhcode()))
                                    .filter(o -> o.getCinvode().equals(v.getCinvode()) && v.getDdate().compareTo(o.getDdate()) >= 0 ).collect(Collectors.toList());//&& v.getCmakerTime().compareTo(o.getCmakerTime()) >= 0
                            //汇总主数量 无税金额
                            double num1 = rk.stream().mapToDouble(obj -> {
                                if (Objects.isNull(obj.getBaseQuantity())) return 0.00d;
                                return Double.parseDouble(obj.getBaseQuantity().toString());
                            }).sum();

                            double moeny1 = rk.stream().mapToDouble(obj -> {
                                if (Objects.isNull(obj.getIcost())) return 0.00d;
                                return Double.parseDouble(obj.getIcost().toString());
                            }).sum();

                            //获取当前出库存货 单据日期/制单日期之前的出库调整单
                            List<StockSaleousings> cktz = cktzList.stream()
                                    .filter(o -> o.getBatchId().equals(v.getBatchId()))
                                    .filter(o -> "2".equals(hs)?true:o.getCwhcode().equals(v.getCwhcode()))
                                    .filter(o -> o.getCinvode().equals(v.getCinvode()) && v.getDdate().compareTo(o.getDdate()) >= 0 ).collect(Collectors.toList());
                            //汇总主数量 无税金额
                            double num2 = cktz.stream().mapToDouble(obj -> {
                                if (Objects.isNull(obj.getBaseQuantity())) return 0.00d;
                                return Double.parseDouble(obj.getBaseQuantity().toString());
                            }).sum();

                            double moeny2 = cktz.stream().mapToDouble(obj -> {
                                if (Objects.isNull(obj.getIcost())) return 0.00d;
                                return Double.parseDouble(obj.getIcost().toString());
                            }).sum();

                            //汇总数量 金额 计算单价
                            BigDecimal sumNum = BigDecimal.valueOf(num).add(BigDecimal.valueOf(num1)).add(BigDecimal.valueOf(num2).abs());
                            BigDecimal sumMoney = BigDecimal.valueOf(moeny).add(BigDecimal.valueOf(moeny1)).add(BigDecimal.valueOf(moeny2).abs());
                            BigDecimal price = sumMoney.divide(sumNum, 10, BigDecimal.ROUND_HALF_UP);
                            //数量0 =单价0 金额0 =单价0
                            if(sumNum.compareTo(BigDecimal.ZERO) == 0){
                                v.setPrice("0");
                            }else if(sumMoney.compareTo(BigDecimal.ZERO) == 0){
                                v.setPrice("0");
                            }else{
                                v.setPrice(price.toString());
                            }
                            v.setIcost(new BigDecimal(v.getBaseQuantity()).multiply(price).setScale(10, BigDecimal.ROUND_HALF_UP).toString());

                        }else{//"移动平均"
                            //期初 + 采购 + 其他 +3个入库 +入库调整 -出库调整单  -3红字出库( 材料领用 销售出库 其他出) = 移动平均单价
                            //获取当前存货的期初
                            List<StockBeginBalance> qc = qcList.stream()
                                    .filter(o -> "2".equals(hs)?true:o.getStockCangkuId().equals(v.getCwhcode()))
                                    .filter(o -> o.getStockId().equals(v.getCinvode())).collect(Collectors.toList());
                            //汇总主数量 无税金额
                            double num = qc.stream().mapToDouble(obj -> {
                                if (Objects.isNull(obj.getBaseQuantity())) return 0.00d;
                                return Double.parseDouble(obj.getBaseQuantity().toString());
                            }).sum();

                            double moeny = qc.stream().mapToDouble(obj -> {
                                if (Objects.isNull(obj.getIcost())) return 0.00d;
                                return Double.parseDouble(obj.getIcost().toString());
                            }).sum();
                            //获取当前出库存货 单据日期/制单日期之前的入库单
                            List<StockWarehousings> rk = rkList.stream()
                                    .filter(o -> "2".equals(hs)?true:o.getCwhcode().equals(v.getCwhcode()))
                                    .filter(o -> o.getCinvode().equals(v.getCinvode()) && v.getDdate().compareTo(o.getDdate()) >= 0 ).collect(Collectors.toList());
                            //汇总主数量 无税金额
                            double num1 = rk.stream().mapToDouble(obj -> {
                                if (Objects.isNull(obj.getBaseQuantity())) return 0.00d;
                                return Double.parseDouble(obj.getBaseQuantity().toString());
                            }).sum();

                            double moeny1 = rk.stream().mapToDouble(obj -> {
                                if (Objects.isNull(obj.getIcost())) return 0.00d;
                                return Double.parseDouble(obj.getIcost().toString());
                            }).sum();
                            //获取当前出库存货 单据日期/制单日期之前的红字出库单
                            List<StockSaleousings> hzck = hzckList.stream()
                                    .filter(o -> "2".equals(hs)?true:o.getCwhcode().equals(v.getCwhcode()))
                                    .filter(o -> o.getCinvode().equals(v.getCinvode()) && v.getDdate().compareTo(o.getDdate()) >= 0 ).collect(Collectors.toList());
                            //汇总主数量 无税金额
                            double num2 = hzck.stream().mapToDouble(obj -> {
                                if (Objects.isNull(obj.getBaseQuantity())) return 0.00d;
                                return Double.parseDouble(obj.getBaseQuantity().toString());
                            }).sum();
                            double moeny2 = hzck.stream().mapToDouble(obj -> {
                                if (Objects.isNull(obj.getIcost())) return 0.00d;
                                return Double.parseDouble(obj.getIcost().toString());
                            }).sum();

                            //获取当前出库存货 单据日期/制单日期之前的出库调整单
                            List<StockSaleousings> cktz = cktzList.stream()
                                    .filter(o -> "2".equals(hs)?true:o.getCwhcode().equals(v.getCwhcode()))
                                    .filter(o -> o.getCinvode().equals(v.getCinvode()) && v.getDdate().compareTo(o.getDdate()) >= 0 ).collect(Collectors.toList());
                            //汇总主数量 无税金额
                            double num3 = cktz.stream().mapToDouble(obj -> {
                                if (Objects.isNull(obj.getBaseQuantity())) return 0.00d;
                                return Double.parseDouble(obj.getBaseQuantity().toString());
                            }).sum();
                            double moeny3 = cktz.stream().mapToDouble(obj -> {
                                if (Objects.isNull(obj.getIcost())) return 0.00d;
                                return Double.parseDouble(obj.getIcost().toString());
                            }).sum();

                            //汇总数量 金额 计算单价
                            BigDecimal sumNum = BigDecimal.valueOf(num).add(BigDecimal.valueOf(num1)).add(BigDecimal.valueOf(num2).abs()).add(BigDecimal.valueOf(num3));
                            BigDecimal sumMoney = BigDecimal.valueOf(moeny).add(BigDecimal.valueOf(moeny1)).add(BigDecimal.valueOf(moeny2).abs()).add(BigDecimal.valueOf(moeny3));
                            BigDecimal price = sumMoney.divide(sumNum, 10, BigDecimal.ROUND_HALF_UP);
                            //数量0 =单价0 金额0 =单价0
                            if(sumNum.compareTo(BigDecimal.ZERO) == 0){
                                v.setPrice("0");
                            }else if(sumMoney.compareTo(BigDecimal.ZERO) == 0){
                                v.setPrice("0");
                            }else{
                                v.setPrice(price.toString());
                            }
                            v.setIcost(new BigDecimal(v.getBaseQuantity()).multiply(price).setScale(10, BigDecimal.ROUND_HALF_UP).toString());
                        }
                        StockSaleousings sa = new StockSaleousings();
                        BeanUtils.copyProperties(v,sa);
                        salist.add(sa);
                    });
                    return salist;
                })
                .flatMap(list-> {
                    return saleousingsRepository.saveAll(list).collectList();
                })
                .map(o->R.ok().setResult(ckList));
    }

    @PostMapping("warehousingThree")
    public Mono<R> warehousingThree(@RequestBody Map map){
        String riqi = map.get("riqi").toString();
        // 参数设置：入库保存状态就是现存量 标志 1是查询所有 0 查询已审核
        String rkBcheck=map.get("rkBcheck")==null?"0":map.get("rkBcheck").toString();
        String ckBcheck=map.get("ckBcheck")==null?"0":map.get("ckBcheck").toString();
        //成本核算模式
        String hs=map.get("hsFlg")==null?"2":map.get("hsFlg").toString();//2存货3仓库存货
        String flg = map.containsKey("flg") ?map.get("flg").toString():"1";//是否启用日期
        //入库单成本核算
        List<StockCostAcc> scList =  new ArrayList<>();
        List<StockSaleousings> ckList =  new ArrayList<>();
        List<StockBeginBalance> qcList =  new ArrayList<>();
        List<StockSaleousings> hzckList =  new ArrayList<>();
        List<StockWarehousings> rkList =  new ArrayList<>();
        List<StockSaleousings> cktzList =  new ArrayList<>();
        List<StockSaleousings> salist =  new ArrayList<>();
        //查询所有销售出库单然后根据销售出库单查询存货获取核算方式
        return saleousingsRepository.findAllByBillStyleAndDdateDist(riqi,"QTCKD")
                .filter(v-> {
                    if(ckBcheck.equals("0")){
                        return  "1".equals(v.getBcheck());
                    }else{
                        return  "0".equals(v.getBcheck()) || Objects.isNull(v.getBcheck());
                    }
                })
                .collectList()
                .flatMap(list->{
                    ckList.addAll(list);
                    List<String> cinvodes = ckList.stream().map(v -> v.getCinvode()).collect(Collectors.toList());
                    //存货的期初
                    return cinvodes.size() > 0 ? beginBalanceRepository.findAllByCinvodes(cinvodes,"2022")
                            .collectList()
                            .map(item->{
                                qcList.addAll(item);
                                return list;
                            }): Mono.just(list);
                })
                .flatMap(list->{
                    //采购 + 其他 +3个入库 +入库调整
                    return warehousingsRepository.findAllByYearAndDate("2022",riqi)
                            .filter(v-> {
                                if(rkBcheck.equals("0")){
                                    return  "1".equals(v.getBcheck());
                                }else{
                                    return  "0".equals(v.getBcheck()) || Objects.isNull(v.getBcheck());
                                }
                            })
                            .collectList()
                            .map(item->{
                                rkList.addAll(item);
                                return list;
                            });
                })
                .flatMap(list->{
                    //-3红字出库( 材料领用 销售出库 其他出)
                    return saleousingsRepository.findAllByBillStyleAndYearAndDdate("2022",riqi)
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
                .flatMap(list->{
                    //出库调整单
                    return saleousingsRepository.findAllByBillStyleAndDdateDist(riqi,"CKTZD")
                            .filter(v-> {
                                if(ckBcheck.equals("0")){
                                    return  "1".equals(v.getBcheck());
                                }else{
                                    return  "0".equals(v.getBcheck()) || Objects.isNull(v.getBcheck());
                                }
                            })
                            .collectList()
                            .map(item->{
                                cktzList.addAll(item);
                                return list;
                            });
                })
                .map(list->{
                    //根据核算方式去计算
                    list.forEach(v->{
                        if("xx".equals(v.getBuname())){

                        }else if("相同批号".equals(v.getBuname())){
                            //期初 + 采购 + 其他 +入库调整 - 出库调整单  = 相同批号单价
                            //获取当前存货的期初
                            List<StockBeginBalance> qc = qcList.stream()
                                    .filter(o -> "2".equals(hs)?true:o.getStockCangkuId().equals(v.getCwhcode()))
                                    .filter(o -> o.getStockId().equals(v.getCinvode()) && o.getBatchNumber().equals(v.getBatchId())).collect(Collectors.toList());
                            //汇总主数量 无税金额
                            double num = qc.stream().mapToDouble(obj -> {
                                if (Objects.isNull(obj.getBaseQuantity())) return 0.00d;
                                return Double.parseDouble(obj.getBaseQuantity().toString());
                            }).sum();

                            double moeny = qc.stream().mapToDouble(obj -> {
                                if (Objects.isNull(obj.getIcost())) return 0.00d;
                                return Double.parseDouble(obj.getIcost().toString());
                            }).sum();

                            //获取当前出库存货 单据日期/制单日期之前的入库单
                            List<StockWarehousings> rk = rkList.stream()
                                    .filter(o -> "CGRKD".equals(o.getBillStyle()) || "QTRKD".equals(o.getBillStyle())  || "RKTZD".equals(o.getBillStyle()))
                                    .filter(o -> o.getBatchId().equals(v.getBatchId()))
                                    .filter(o -> "2".equals(hs)?true:o.getCwhcode().equals(v.getCwhcode()))
                                    .filter(o -> o.getCinvode().equals(v.getCinvode()) && v.getDdate().compareTo(o.getDdate()) >= 0 ).collect(Collectors.toList());//&& v.getCmakerTime().compareTo(o.getCmakerTime()) >= 0
                            //汇总主数量 无税金额
                            double num1 = rk.stream().mapToDouble(obj -> {
                                if (Objects.isNull(obj.getBaseQuantity())) return 0.00d;
                                return Double.parseDouble(obj.getBaseQuantity().toString());
                            }).sum();

                            double moeny1 = rk.stream().mapToDouble(obj -> {
                                if (Objects.isNull(obj.getIcost())) return 0.00d;
                                return Double.parseDouble(obj.getIcost().toString());
                            }).sum();

                            //获取当前出库存货 单据日期/制单日期之前的出库调整单
                            List<StockSaleousings> cktz = cktzList.stream()
                                    .filter(o -> o.getBatchId().equals(v.getBatchId()))
                                    .filter(o -> "2".equals(hs)?true:o.getCwhcode().equals(v.getCwhcode()))
                                    .filter(o -> o.getCinvode().equals(v.getCinvode()) && v.getDdate().compareTo(o.getDdate()) >= 0 ).collect(Collectors.toList());
                            //汇总主数量 无税金额
                            double num2 = cktz.stream().mapToDouble(obj -> {
                                if (Objects.isNull(obj.getBaseQuantity())) return 0.00d;
                                return Double.parseDouble(obj.getBaseQuantity().toString());
                            }).sum();

                            double moeny2 = cktz.stream().mapToDouble(obj -> {
                                if (Objects.isNull(obj.getIcost())) return 0.00d;
                                return Double.parseDouble(obj.getIcost().toString());
                            }).sum();

                            //汇总数量 金额 计算单价
                            BigDecimal sumNum = BigDecimal.valueOf(num).add(BigDecimal.valueOf(num1)).add(BigDecimal.valueOf(num2).abs());
                            BigDecimal sumMoney = BigDecimal.valueOf(moeny).add(BigDecimal.valueOf(moeny1)).add(BigDecimal.valueOf(moeny2).abs());
                            BigDecimal price = sumMoney.divide(sumNum, 10, BigDecimal.ROUND_HALF_UP);
                            //数量0 =单价0 金额0 =单价0
                            if(sumNum.compareTo(BigDecimal.ZERO) == 0){
                                v.setPrice("0");
                            }else if(sumMoney.compareTo(BigDecimal.ZERO) == 0){
                                v.setPrice("0");
                            }else{
                                v.setPrice(price.toString());
                            }
                            v.setIcost(new BigDecimal(v.getBaseQuantity()).multiply(price).setScale(10, BigDecimal.ROUND_HALF_UP).toString());

                        }else{//"移动平均"
                            //期初 + 采购 + 其他 +3个入库 +入库调整 -出库调整单  -3红字出库( 材料领用 销售出库 其他出) = 移动平均单价
                            //获取当前存货的期初
                            List<StockBeginBalance> qc = qcList.stream()
                                    .filter(o -> "2".equals(hs)?true:o.getStockCangkuId().equals(v.getCwhcode()))
                                    .filter(o -> o.getStockId().equals(v.getCinvode())).collect(Collectors.toList());
                            //汇总主数量 无税金额
                            double num = qc.stream().mapToDouble(obj -> {
                                if (Objects.isNull(obj.getBaseQuantity())) return 0.00d;
                                return Double.parseDouble(obj.getBaseQuantity().toString());
                            }).sum();

                            double moeny = qc.stream().mapToDouble(obj -> {
                                if (Objects.isNull(obj.getIcost())) return 0.00d;
                                return Double.parseDouble(obj.getIcost().toString());
                            }).sum();
                            //获取当前出库存货 单据日期/制单日期之前的入库单
                            List<StockWarehousings> rk = rkList.stream()
                                    .filter(o -> "2".equals(hs)?true:o.getCwhcode().equals(v.getCwhcode()))
                                    .filter(o -> o.getCinvode().equals(v.getCinvode()) && v.getDdate().compareTo(o.getDdate()) >= 0 ).collect(Collectors.toList());
                            //汇总主数量 无税金额
                            double num1 = rk.stream().mapToDouble(obj -> {
                                if (Objects.isNull(obj.getBaseQuantity())) return 0.00d;
                                return Double.parseDouble(obj.getBaseQuantity().toString());
                            }).sum();

                            double moeny1 = rk.stream().mapToDouble(obj -> {
                                if (Objects.isNull(obj.getIcost())) return 0.00d;
                                return Double.parseDouble(obj.getIcost().toString());
                            }).sum();
                            //获取当前出库存货 单据日期/制单日期之前的红字出库单
                            List<StockSaleousings> hzck = hzckList.stream()
                                    .filter(o -> "2".equals(hs)?true:o.getCwhcode().equals(v.getCwhcode()))
                                    .filter(o -> o.getCinvode().equals(v.getCinvode()) && v.getDdate().compareTo(o.getDdate()) >= 0 ).collect(Collectors.toList());
                            //汇总主数量 无税金额
                            double num2 = hzck.stream().mapToDouble(obj -> {
                                if (Objects.isNull(obj.getBaseQuantity())) return 0.00d;
                                return Double.parseDouble(obj.getBaseQuantity().toString());
                            }).sum();
                            double moeny2 = hzck.stream().mapToDouble(obj -> {
                                if (Objects.isNull(obj.getIcost())) return 0.00d;
                                return Double.parseDouble(obj.getIcost().toString());
                            }).sum();

                            //获取当前出库存货 单据日期/制单日期之前的出库调整单
                            List<StockSaleousings> cktz = cktzList.stream()
                                    .filter(o -> "2".equals(hs)?true:o.getCwhcode().equals(v.getCwhcode()))
                                    .filter(o -> o.getCinvode().equals(v.getCinvode()) && v.getDdate().compareTo(o.getDdate()) >= 0 ).collect(Collectors.toList());
                            //汇总主数量 无税金额
                            double num3 = cktz.stream().mapToDouble(obj -> {
                                if (Objects.isNull(obj.getBaseQuantity())) return 0.00d;
                                return Double.parseDouble(obj.getBaseQuantity().toString());
                            }).sum();
                            double moeny3 = cktz.stream().mapToDouble(obj -> {
                                if (Objects.isNull(obj.getIcost())) return 0.00d;
                                return Double.parseDouble(obj.getIcost().toString());
                            }).sum();

                            //汇总数量 金额 计算单价
                            BigDecimal sumNum = BigDecimal.valueOf(num).add(BigDecimal.valueOf(num1)).add(BigDecimal.valueOf(num2).abs()).add(BigDecimal.valueOf(num3));
                            BigDecimal sumMoney = BigDecimal.valueOf(moeny).add(BigDecimal.valueOf(moeny1)).add(BigDecimal.valueOf(moeny2).abs()).add(BigDecimal.valueOf(moeny3));
                            BigDecimal price = sumMoney.divide(sumNum, 10, BigDecimal.ROUND_HALF_UP);
                            //数量0 =单价0 金额0 =单价0
                            if(sumNum.compareTo(BigDecimal.ZERO) == 0){
                                v.setPrice("0");
                            }else if(sumMoney.compareTo(BigDecimal.ZERO) == 0){
                                v.setPrice("0");
                            }else{
                                v.setPrice(price.toString());
                            }
                            v.setIcost(new BigDecimal(v.getBaseQuantity()).multiply(price).setScale(10, BigDecimal.ROUND_HALF_UP).toString());
                        }
                        StockSaleousings sa = new StockSaleousings();
                        BeanUtils.copyProperties(v,sa);
                        salist.add(sa);
                    });
                    return salist;
                })
                .flatMap(list-> {
                    return saleousingsRepository.saveAll(list).collectList();
                })
                .map(o->R.ok().setResult(ckList));
    }
}

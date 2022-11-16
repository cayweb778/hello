package org.boozsoft.rest.stock;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.boozsoft.domain.entity.ReportEncodingRules;
import org.boozsoft.domain.entity.SysUnitOfMea;
import org.boozsoft.domain.entity.SysUnitOfMeaList;
import org.boozsoft.domain.entity.stock.*;
import org.boozsoft.domain.ro.StockCostAccRo;
import org.boozsoft.domain.ro.StockTakingImprotRo;
import org.boozsoft.domain.vo.stock.*;
import org.boozsoft.repo.ReportEncodingRulesRepository;
import org.boozsoft.repo.SysUnitOfMeaListRepository;
import org.boozsoft.repo.SysUnitOfMeaRepository;
import org.boozsoft.repo.stock.*;
import org.springbooz.core.tool.result.R;
import org.springbooz.core.tool.utils.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Transient;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/stockTaking")
public class StockTakingController {

    @Autowired
    private StockTakingRepository stockTakingRepository;
    @Autowired
    private StockTakingsRepository stockTakingsRepository;
    @Autowired
    private StockRepository stockRepository;
    @Autowired
    private ReportEncodingRulesRepository reportEncodingRulesRepository;
    @Autowired
    private StockClassRepository stockClassRepository;
    @Autowired
    private StockTakingCunRepository stockTakingCunRepository;
    @Autowired
    private StockTakingCunbatchRepository stockTakingCunbatchRepository;
    @Autowired
    private StockTakingClassRepository stockTakingClassRepository;
    @Autowired
    private  StockCurrentstockRepository stockCurrentstockRepository;
    @Autowired
    private StockWarehousingRepository stockWarehousingRepository;
    @Autowired
    private StockWarehousingsRepository stockWarehousingsRepository;
    @Autowired
    private StockSaleousingRepository stockSaleousingRepository;
    @Autowired
    private StockSaleousingsRepository stockSaleousingsRepository;
    @Autowired
    private StockTakingSourceRepository stockTakingSourceRepository;
    @Autowired
    private SysUnitOfMeaRepository sysUnitOfMeaRepository;
    @Autowired
    private SysUnitOfMeaListRepository sysUnitOfMeaListRepository;
    @Autowired
    private StockBeginBalanceRepository stockBeginBalanceRepository;

    @PostMapping("findAll")
    public Mono<R> findAll(@RequestBody Map map) {
        String stockClass=map.get("stockClass").toString();
        String ccode = map.get("ccode").toString();
        // 0.全仓库、1.存货分类、2.存货、3.存货+批号
        String type = map.get("type").toString();
        // 先查询 有 过滤  无 按照规则过滤 
        return stockTakingsRepository.findAllByPrice(ccode).collectList()
                .flatMap(list->{
                    //查询对应类型的关联表 例 存货分类
                    if(type.equals("1")){
                        return stockTakingRepository.findAllTakingClass(ccode).collectList()
                                .map(classList->{
                                    map.put("classList",classList);
                                    return list;
                                });
                    }else if(type.equals("2")){
                        return stockTakingRepository.findAllTakingCun(ccode).collectList()
                               .map(chList->{
                                   map.put("chList",chList);
                                   return list;
                               });
                    }else if(type.equals("3")){
                        return stockTakingRepository.findAllTakingCunbatch(ccode).collectList()
                                .map(cbList->{
                                    map.put("cbList",cbList);
                                    return list;
                                });
                    }
                    return  Mono.just(list);
                })
                .flatMap(list->{
                    //查询单计量单位
                    return sysUnitOfMeaRepository.findAll()
                            .collectList()
                            .map(umlist->{
                                map.put("umlist",umlist);
                                return list;
                            });
                })
                .flatMap(list->{
                    //查询多计量单位
                    return sysUnitOfMeaListRepository.findAlls()
                            .collectList()
                            .map(umslist->{
                                map.put("umslist",umslist);
                                return list;
                            });
                })
                .map(list->{
                    List<String> classList = map.containsKey("classList")?  (List<String>)map.get("classList") : new ArrayList<>();
                    List<String> chList = map.containsKey("chList")?  (List<String>)map.get("chList") : new ArrayList<>();
                    List<String> cbList = map.containsKey("cbList")?  (List<String>)map.get("cbList") : new ArrayList<>();
                    return list.stream().filter(v -> {
                        //范围过滤
                        if (type.equals("1")) {
                            return classList.stream().filter(s -> s.equals(v.getStockClass())).findFirst().isPresent();
                        } else if (type.equals("2")) {
                            return chList.stream().filter(s -> s.equals(v.getStockNum())).findFirst().isPresent();
                        } else if (type.equals("3")) {
                            return cbList.stream().filter(s -> s.equals(v.getStockNum()+v.getBatchId())).findFirst().isPresent();
                        }
                        return true;
                    }).filter(v -> {
                        //分类过滤
                        if (StringUtils.isNotBlank(stockClass) && !stockClass.equals("0")) {
                            return v.getStockClass().equals(stockClass);
                        }
                        return true;
                    }).collect(Collectors.toList());
                })
                .map(list->{
                    list.forEach(v->{
                        if(Objects.isNull(v.getPdid())){
                            v.setQuantityPd(v.getBaseQuantity());
//                            v.setSubPandian1(v.getSubQuantity1());
//                            v.setSubPandian2(v.getSubQuantity2());
                        }
                        //匹配计量单位
                        if("单计量".equals(v.getUmtype())){
                            List<SysUnitOfMea> umlist =  (List<SysUnitOfMea>) map.get("umlist");
                            Optional<SysUnitOfMea> first = umlist.stream().filter(o -> o.getId().equals(v.getUtcode())).findFirst();
                            v.setCunitname(first.get().getUnitName());
                            v.setCunitidType("单计量");
                        }else{
                            List<SysUnitOfMeaListVo> umslist =  (List<SysUnitOfMeaListVo>) map.get("umslist");
                            List<SysUnitOfMeaListVo> collect = umslist.stream().filter(o -> o.getUmid().equals(v.getUtcode()))
                                    .sorted(Comparator.comparing(SysUnitOfMeaListVo::getId))
                                    .collect(Collectors.toList());
                            v.setCunitidType("多计量");
                            if(collect.size() > 0){
                                v.setCunitnames(collect.get(0).getUnitName());
                            }
                            if(collect.size() > 1){
                                v.setCunitidF1(collect.get(1).getId());
                                v.setFnames1(collect.get(1).getUnitName());
                                v.setRate1(collect.get(1).getConversionRate());
                            }
                            if(collect.size() > 2){
                                v.setCunitidF2(collect.get(2).getId());
                                v.setFnames2(collect.get(2).getUnitName());
                                v.setRate2(collect.get(2).getConversionRate());
                            }
                        }
                    });
                    return list;
                })
                .map(a -> R.ok().setResult(a));
    }

    @GetMapping("/del/{id}")
    public Mono<R> del(@PathVariable String id) {
        return stockTakingsRepository.deleteById(id).then(Mono.just(R.ok()));
    }

    @GetMapping("/delCust/{id}")
    public Mono<R> delCust(@PathVariable String id) {
        return stockTakingsRepository.deleteById(id)
                .zipWith(stockTakingRepository.deleteBycustId(id))
                .then(Mono.just(R.ok()));
    }

    @PostMapping("addCusts")
    public Mono<R> addCusts(@RequestBody List<StockTakings> list) {
        return stockTakingsRepository.saveAll(list).collectList().map(a -> R.ok().setResult(a));
    }


    @PostMapping("findAllTaking")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAllTaking(@RequestBody Map map) {
        String strDate = map.get("strDate").toString();
        String endDate = map.get("endDate").toString();
        return stockTakingRepository.findAllByOrderById(strDate,endDate).collectList()
                .map(a -> R.ok().setResult(a));
    }


    @PostMapping
    @ApiOperation(value = "新增", notes = "传入code")
    public Mono save(@RequestBody StockTaking object) {
        String time = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        AtomicReference<String> ccode = new AtomicReference<>("");
        // 参数设置：入库保存状态就是现存量 标志 1是查询所有 0 查询已审核
        String finalRkBcheck=ObjectUtil.isEmpty(object.getRkBcheck())?"0":object.getRkBcheck();
        String finalCkBcheck=ObjectUtil.isEmpty(object.getCkBcheck())?"0":object.getCkBcheck();
        //审核弃审前校验现存量
        return reportEncodingRulesRepository.findByFileType("3-14")
                .flatMap(obj->{
                    return  stockTakingRepository.findMaxCode()
                            .defaultIfEmpty(obj.getShowRules()+"001")
                            .map(maxCode->{
                                String code = "";
                                if(StringUtils.isNotBlank(maxCode)){
                                    Integer num = Integer.parseInt(maxCode.replace(obj.getShowRules(),""))+1;
                                    code = String.format("%0"+obj.getSerialNumLength()+"d",num);
                                }else{
                                    code = String.format("%0"+obj.getSerialNumLength()+"d","1");
                                }
                                object.setStartDate(object.getStartDate().substring(0,10));
                                object.setStopDate(object.getStopDate().substring(0,10));
                                if (object.getId() == null || object.getId().equals("")) {
                                    object.setDdate(time);
                                    object.setLockStatus("0");
                                    object.setBcheck("0");
                                    object.setCcode(obj.getShowRules()+code);
                                }
                                return object;
                            });
                })
                .flatMap(stockTakingRepository::save)
                .flatMap(obj->{
                    List<StockTakings> stlist = new ArrayList();
                    ccode.set(object.getCcode());
                    //根据范围查询现存量 添加到盘点单
                    String bstyle = object.getBstyle();
                    String cwhcode = object.getCwhcode();
                    String year = object.getIyear();
                    if(bstyle.equals("1")){//
                        String[] arr = object.getClassList();
                        return stockCurrentstockRepository.findAllByCwhcodeAndIyearAndStockClass(cwhcode,year,arr)
                                .collectList()
                                .map(list->{
                                    list.forEach(item->{
                                        StockTakings st = new StockTakings();
                                        st.setCfree1(LocalDateTime.now().toString());
                                        st.setIyear(item.getIyear());
                                        st.setCurrentId(item.getId());//现存量id
                                        st.setCinvode(item.getInvCode());
                                        st.setCcode(obj.getCcode());
                                        st.setLineCode(UUID.randomUUID().toString());
                                        st.setCwhcode(item.getCwhcode());
                                        st.setCwhcode1(item.getCwhcode1());
                                        st.setCwhcode2(item.getCwhcode2());
                                        st.setCwhcode3(item.getCwhcode3());
                                        st.setCwhcode4(item.getCwhcode4());
                                        st.setCwhcode5(item.getCwhcode5());
                                        st.setCwhcode6(item.getCwhcode6());
                                        st.setDpdate(item.getDpdate());
                                        st.setDvdate(item.getDpdate());
                                        st.setBatchId(item.getBatchId());
                                        st.setBatchDate(item.getBatchDate());
                                        st.setBaseQuantity(item.getBaseQuantity());
                                        //st.setSubQuantity1(item.getSubQuantity1());
                                        //st.setSubQuantity2(item.getSubQuantity2());
                                        st.setQuantityPd(item.getBaseQuantity());
                                        //st.setSubPandian1(item.getSubQuantity1());
                                        //st.setSubPandian2(item.getSubQuantity2());
                                        st.setQuantityUk(BigDecimal.ZERO);
                                        st.setQuantityUk1(BigDecimal.ZERO);
                                        st.setQuantityUk2(BigDecimal.ZERO);
                                        st.setCunitidType(item.getCunitidType());
                                        //st.setCunitid(item.getCunitid());
                                        st.setCunitidF1(item.getCunitidF1());
                                        st.setCunitidF2(item.getCunitidF2());
                                        stlist.add(st);
                                    });
                                    return stlist;
                                });
                    }else if(bstyle.equals("2")){
                        String[] arr = object.getCunList();
                        return stockCurrentstockRepository.findAllByCwhcodeAndIyearAndStockNum(cwhcode,year,arr)
                                .collectList()
                                .map(list->{
                                    list.forEach(item->{
                                        StockTakings st = new StockTakings();
                                        st.setIyear(item.getIyear());
                                        st.setCurrentId(item.getId());//现存量id
                                        st.setCinvode(item.getInvCode());
                                        st.setCcode(obj.getCcode());
                                        st.setLineCode(UUID.randomUUID().toString());
                                        st.setCwhcode(item.getCwhcode());
                                        st.setCwhcode1(item.getCwhcode1());
                                        st.setCwhcode2(item.getCwhcode2());
                                        st.setCwhcode3(item.getCwhcode3());
                                        st.setCwhcode4(item.getCwhcode4());
                                        st.setCwhcode5(item.getCwhcode5());
                                        st.setCwhcode6(item.getCwhcode6());
                                        st.setDpdate(item.getDpdate());
                                        st.setDvdate(item.getDpdate());
                                        st.setBatchId(item.getBatchId());
                                        st.setBatchDate(item.getBatchDate());
                                        st.setBaseQuantity(item.getBaseQuantity());
//                                        st.setSubQuantity1(item.getSubQuantity1());
//                                        st.setSubQuantity2(item.getSubQuantity2());
                                        st.setQuantityPd(item.getBaseQuantity());
//                                        st.setSubPandian1(item.getSubQuantity1());
//                                        st.setSubPandian2(item.getSubQuantity2());
                                        st.setQuantityUk(BigDecimal.ZERO);
                                        st.setQuantityUk1(BigDecimal.ZERO);
                                        st.setQuantityUk2(BigDecimal.ZERO);
                                        st.setCunitidType(item.getCunitidType());
//                                        st.setCunitid(item.getCunitid());
                                        st.setCunitidF1(item.getCunitidF1());
                                        st.setCunitidF2(item.getCunitidF2());
                                        stlist.add(st);
                                    });
                                    return stlist;
                                });

                    }else if(bstyle.equals("3")){

                        return Mono.just(stlist);
                    }else{
                        //查询实时现存量
                        List<StockAccSheetVo> skl = new ArrayList<>();
                        String ck= object.getCwhcode();
                        return stockRepository.findAll()
                                .collectList()
                                .flatMap(slist->{
                                    //期初
                                    return  stockBeginBalanceRepository.findAllByIyearAndCk(year,ck)
                                            .collectList()
                                            .map(wl->{
                                                skl.addAll(wl);
                                                return slist;
                                            });
                                })
                                .flatMap(slist->{
                                    //入库
                                    return stockWarehousingsRepository.findAllByIyearAndCk(year,ck)
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
                                    return stockSaleousingsRepository.findAllByIyearAndCk(year,ck)
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
                                            .collect(Collectors.groupingBy(StockAccSheetVo::getCinvode));
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

                                        double sumBq1 = ckList.stream().mapToDouble(v -> {
                                            if (Objects.isNull(v.getBq1())) return 0.00d;
                                            return Double.parseDouble(v.getBq1().toString());
                                        }).sum();

                                        double sumBqrk1 = rkList.stream().mapToDouble(v -> {
                                            if (Objects.isNull(v.getBq1())) return 0.00d;
                                            return Double.parseDouble(v.getBq1().toString());
                                        }).sum();
                                        double sumBq2 = ckList.stream().mapToDouble(v -> {
                                            if (Objects.isNull(v.getBq2())) return 0.00d;
                                            return Double.parseDouble(v.getBq2().toString());
                                        }).sum();

                                        double sumBqrk2 = rkList.stream().mapToDouble(v -> {
                                            if (Objects.isNull(v.getBq2())) return 0.00d;
                                            return Double.parseDouble(v.getBq2().toString());
                                        }).sum();
                                        double icost1 = ckList.stream().mapToDouble(v -> {
                                            if (Objects.isNull(v.getIcost())) return 0.00d;
                                            return Double.parseDouble(v.getIcost().toString());
                                        }).sum();
                                        double icost2 = rkList.stream().mapToDouble(v -> {
                                            if (Objects.isNull(v.getIcost())) return 0.00d;
                                            return Double.parseDouble(v.getIcost().toString());
                                        }).sum();
                                        //现存量  期初+入库-出库
                                        BigDecimal subtract = new BigDecimal(sumBq).subtract(new BigDecimal(sumBqrk));
                                        BigDecimal subtract1 = new BigDecimal(sumBq1).subtract(new BigDecimal(sumBqrk1));
                                        BigDecimal subtract2 = new BigDecimal(sumBq2).subtract(new BigDecimal(sumBqrk2));
                                        BigDecimal icost = new BigDecimal(icost1).subtract(new BigDecimal(icost2));

                                        //匹配存货的信息
                                        Optional<Stock> first = list.stream().filter(v -> v.getStockNum().equals(k)).findFirst();

                                        StockAccSheetVo item = value.get(0);
                                        //现存量存在 并且不等于0的
                                        if(first.isPresent() && subtract.compareTo(BigDecimal.ZERO) != 0){
                                            first.get().setXcl(subtract);
                                            //查询数据调整
                                            StockTakings st = new StockTakings();
                                            BeanUtils.copyProperties(item,st);
                                            st.setId(null);
                                            st.setIyear(item.getIyear());
                                            st.setCinvode(item.getCinvode());
                                            st.setCcode(obj.getCcode());
                                            st.setLineCode(UUID.randomUUID().toString());
                                            st.setBatchId(item.getBatchid());
                                            st.setCunitidType(first.get().getStockMeasurementType());
                                            st.setBaseQuantity(subtract);
                                            st.setSubQuantity1(subtract1);
                                            st.setSubPandian2(subtract2);
                                            st.setQuantityPd(subtract);
                                            st.setSubPandian1(subtract1);
                                            st.setSubPandian2(subtract2);
                                            st.setQuantityUk(BigDecimal.ZERO);
                                            st.setQuantityUk1(BigDecimal.ZERO);
                                            st.setQuantityUk2(BigDecimal.ZERO);
                                            st.setIcost(icost.toString());
                                            st.setCunitidF1(first.get().getStockUnitId1());
                                            st.setCunitidF2(first.get().getStockUnitId2());
                                            stlist.add(st);
                                        }
                                    });
                                    return stlist;
                                });
                    }
                })
                .flatMap(stlist->{
                    return stockTakingsRepository.saveAll(stlist).collectList().then(Mono.just(object.getBstyle()));
                })
                .flatMap(bstyle->{
                        String[] obj = object.getClassList();
                        if(bstyle.equals("1")){//
                            List<StockTakingClass> tc = new ArrayList<>();
                            Arrays.stream(obj).forEach(v->{
                                StockTakingClass sc =  new StockTakingClass();
                                sc.setStockClass(v);
                                sc.setPid(object.getCcode());
                                tc.add(sc);
                            });
                            return stockTakingClassRepository.deleteByPid(object.getCcode()).then(Mono.just(tc))
                                    .flatMap(list->{
                                        return stockTakingClassRepository.saveAll(list).collectList().then(Mono.just(R.ok()));
                                    });
                        }else if(bstyle.equals("2")){
                            List<StockTakingCun> tc = new ArrayList<>();
                            Arrays.stream(obj).forEach(v->{
                                StockTakingCun sc =  new StockTakingCun();
                                sc.setStockNum(v);
                                sc.setPid(object.getCcode());
                                tc.add(sc);
                            });
                            return stockTakingCunRepository.deleteByPid(object.getCcode()).then(Mono.just(tc))
                                    .flatMap(list->{
                                        return stockTakingCunRepository.saveAll(list).collectList().then(Mono.just(R.ok()));
                                    });
                        }else if(bstyle.equals("3")){
                            List<StockTakingCunbatch> tc = new ArrayList<>();
                            Arrays.stream(obj).forEach(v->{
                                StockTakingCunbatch sc =  new StockTakingCunbatch();
                                sc.setStockBatch(v);
                                sc.setPid(object.getCcode());
                                tc.add(sc);
                            });
                            return stockTakingCunbatchRepository.deleteByPid(object.getCcode()).then(Mono.just(tc))
                                    .flatMap(list->{
                                        return stockTakingCunbatchRepository.saveAll(list).collectList().then(Mono.just(R.ok()));
                                    });
                        }
                    return Mono.just(R.ok());
                }).map(o -> R.ok().setResult(ccode.get()));
    }

    @PostMapping("edit")
    @ApiOperation(value = "修改", notes = "传入code")
    public Mono edit(@RequestBody StockTaking object) {
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        AtomicReference<String> ccode = new AtomicReference<>("");
        return reportEncodingRulesRepository.findByFileType("3-14")
                .flatMap(obj->{
                    return  stockTakingRepository.findMaxCode()
                            .defaultIfEmpty(obj.getShowRules()+"001")
                            .map(maxCode->{
                                String code = "";
                                if(StringUtils.isNotBlank(maxCode)){
                                    Integer num = Integer.parseInt(maxCode.replace(obj.getShowRules(),""))+1;
                                    code = String.format("%0"+obj.getSerialNumLength()+"d",num);
                                }else{
                                    code = String.format("%0"+obj.getSerialNumLength()+"d","1");
                                }
                                object.setStartDate(object.getStartDate().substring(0,10));
                                object.setStopDate(object.getStopDate().substring(0,10));
                                if (object.getId() == null || object.getId().equals("")) {
                                    object.setDdate(time);
                                    object.setLockStatus("0");
                                    object.setBcheck("0");
                                    object.setCcode(obj.getShowRules()+code);
                                }
                                return object;
                            });
                })
                .flatMap(stockTakingRepository::save)
                .map(o -> R.ok().setResult(ccode.get()));
    }

    @DeleteMapping
    @ApiOperation(value = "删除", notes = "传入code")
    public Mono delete(@RequestBody StockTaking object) {
        return stockTakingRepository.deleteById(object.getId())
                .then(Mono.just(object))
                .flatMap(obj-> stockTakingsRepository.deleteByCcode(obj.getCcode()))
                .then(Mono.just(R.ok()));
    }

    @PostMapping("editFlag")
    @ApiOperation(value = "停用启用操作", notes = "传入code")
    public Mono editFlag(@RequestBody StockTaking object) {
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        return stockTakingRepository.save(object)
                .map(o -> R.ok().setResult(o));
    }



    @PostMapping("/saveMx")
    @Transient
    @ApiOperation(value = "新增或修改", notes = "传入code")
    public Mono saveMx(@RequestBody StockTakingsVo object) {
        return  StringUtils.isNotBlank(object.getId())
                ? Mono.just(object)
                   .map(item->{
                        StockTakings st = new StockTakings();
                        BeanUtils.copyProperties(item, st);
                        st.setCinvode(item.getCinvode());
                        st.setCcode(item.getCcode());
                        st.setCurrentId(item.getId());
                        st.setDpdate(item.getDpdate());
                        st.setDvdate(item.getDpdate());
                        st.setBatchId(item.getBatchId());
                        st.setCurrentId(item.getId());
                        st.setIyear(item.getIyear());
                        st.setQuantityUk(item.getQuantityPd().subtract(item.getBaseQuantity()));
                        st.setIcost(new BigDecimal(item.getPrice()).multiply(item.getQuantityUk().abs()).toString());
                        //st.setQuantityUk1(Objects.isNull(item.getSubPandian1())? BigDecimal.ZERO.subtract(item.getSubQuantity1()):item.getSubPandian1().subtract(item.getSubQuantity1()));
                        //st.setQuantityUk2(Objects.isNull(item.getSubPandian2())? BigDecimal.ZERO.subtract(item.getSubQuantity2()) :item.getSubPandian2().subtract(item.getSubQuantity2()));
                        if (StringUtils.isBlank(item.getPdid())) {
                            st.setId(null);
                            st.setLineCode(UUID.randomUUID().toString());
                        }
                        return st;
                    })
                    .flatMap(item->{
                        return stockTakingsRepository.findByCurrentId(object.getPdid(), item.getCcode())
                                .map(o->{
                                    o.setQuantityPd(item.getQuantityPd());
                                    o.setSubPandian1(item.getSubPandian1());
                                    o.setSubPandian2(item.getSubPandian2());
                                    o.setQuantityUk(item.getQuantityPd().subtract(item.getBaseQuantity()));
                                    o.setQuantityUk1(Objects.isNull(item.getSubPandian1())? BigDecimal.ZERO.subtract(Objects.isNull(item.getSubPandian1())? BigDecimal.ZERO:item.getSubPandian1()):item.getSubPandian1().subtract(Objects.isNull(item.getSubQuantity1())? BigDecimal.ZERO:item.getSubQuantity1()));
                                    o.setQuantityUk2(Objects.isNull(item.getSubPandian2())? BigDecimal.ZERO.subtract(Objects.isNull(item.getSubQuantity2())? BigDecimal.ZERO:item.getSubQuantity2()) :item.getSubPandian2().subtract(Objects.isNull(item.getSubQuantity2())? BigDecimal.ZERO:item.getSubQuantity2()));

                                    o.setPrice(item.getPrice());
                                    o.setIcost(new BigDecimal(item.getPrice()).multiply(item.getQuantityUk().abs()).toString());
                                    return  o;
                                })
                                .defaultIfEmpty(item)
                                .flatMap(stockTakingsRepository::save)
                                .map(o -> R.ok().setResult(o));})
                                .map(o -> R.ok())
                                : Mono.just(object.getStockNum())
                                    .flatMap(stockNum-> {
                                        return stockRepository.findByStockNum(stockNum)
                                                .map(obj->{
                                        return obj;
                                    });
                    })
                    .map(item->{
                        StockTakings st = new StockTakings();
                        st.setId(null);
                        st.setIyear(object.getIyear());
                        st.setCinvode(item.getStockNum());
                        st.setCcode(object.getCcode());
                        st.setLineCode(UUID.randomUUID().toString());

                        st.setCwhcode(item.getStockCangku());
                        st.setCwhcode1(object.getCwhcode1());
                        st.setCwhcode2(object.getCwhcode2());
                        st.setCwhcode3(object.getCwhcode3());
                        st.setCwhcode4(object.getCwhcode4());
                        st.setCwhcode5(object.getCwhcode5());
                        st.setCwhcode6(object.getCwhcode6());

                        st.setDpdate(object.getDpdate());
                        st.setDvdate(object.getDvdate());
                        st.setBatchId(object.getBatchId());

                        st.setBaseQuantity(BigDecimal.ZERO);
                        st.setSubQuantity1(BigDecimal.ZERO);
                        st.setSubQuantity2(BigDecimal.ZERO);

                        st.setQuantityPd(Objects.isNull(object.getQuantityPd())? BigDecimal.ZERO :object.getQuantityPd());
                        st.setSubPandian1(Objects.isNull(object.getSubPandian1())? BigDecimal.ZERO :object.getSubPandian1());
                        st.setSubPandian2(Objects.isNull(object.getSubPandian2())? BigDecimal.ZERO :object.getSubPandian2());

                        st.setQuantityUk(Objects.isNull(object.getQuantityPd())? BigDecimal.ZERO :object.getQuantityPd());
                        st.setQuantityUk1(Objects.isNull(object.getSubPandian1())? BigDecimal.ZERO :object.getSubPandian1());
                        st.setQuantityUk2(Objects.isNull(object.getSubPandian2())? BigDecimal.ZERO :object.getSubPandian2());

                        st.setCunitidType(item.getStockMeasurementType());
//                        st.setCunitid(item.getCunitid());
                        st.setCunitidF1(item.getStockUnitId1());
                        st.setCunitidF2(item.getStockUnitId2());

                        st.setPrice(object.getPrice());
                        st.setIcost(new BigDecimal(object.getPrice()).multiply(object.getQuantityUk().abs()).toString());
                        return st;
                    })
                    .flatMap(stockTakingsRepository::save)
                    .map(o -> R.ok().setResult(o));
    }



    @GetMapping("findAllStockClass")
    public Mono<R> findAllStockClass() {
        return stockClassRepository.findAll()
                .collectList()
                .map(list -> R.ok().setResult(list));
    }

    @GetMapping("findAllStock")
    public Mono<R> findAllStock() {
        return stockRepository.findAll()
                .collectList()
                .map(list -> R.ok().setResult(list));
    }

    @PostMapping("checkAdd")
    public Mono<R> checkAdd(@RequestBody StockTaking object) {
        // 0.全仓库、1.存货分类、2.存货、3.存货+批号
        //仓库范围查询 仓库  范围  分类/存货list
        // 例子1
        //   1. 该仓库全仓库
        //   2  该仓库分类
        //   3  该仓库存货
        //   4. 该仓库存货+批次
        // 根据当前范围判断
        // 1全范围 判断 1 2 3 4 有则不能添加
        // 2分类 1 2 3 4 有
        return Mono.just(object)
                .flatMap(obj->{
                    return stockTakingRepository.findAllByCwhcode(obj.getCwhcode())
                            .collectList()
                            .map(list->{
                                obj.setStList(list);
                                return obj;
                            });
                 })
                .flatMap(obj->{
                    //该仓库下未审核的仓库分类
                    return stockTakingClassRepository.findAllByPidAndClass(object.getCwhcode())
                            .collectList()
                            .map(list->{
                                obj.setStcvList(list);
                                return obj;
                            });
                })
                .flatMap(obj->{
                    //该仓库下未审核的存货
                    return stockTakingCunRepository.findAllByPidAndStockNum(object.getCwhcode())
                            .collectList()
                            .map(list->{
                                obj.setStcList(list);
                                return obj;
                            });
                })
                .map(obj->{
                    //根据范围依此判断
                    List<StockTaking>  list1 = obj.getStList();
                    List<StockTakingClassVo>  list2 = obj.getStcvList();
                    List<StockTakingCunVo>  list3 = obj.getStcList();
                    List<StockTakingCunbatchVo>  list4 = obj.getStcpList();
                    // 0.全仓库、1.存货分类、2.存货、3.存货+批号
                    Boolean flgs = true;
                    if("0".equals(object.getBstyle())){
                        if(list1.size()>0) flgs = false;
                        if(list2.size()>0) flgs = false;
                        if(list3.size()>0) flgs = false;
                        if(list4.size()>0) flgs = false;
                    }else if("1".equals(object.getBstyle())){
                        String[] classArr =  obj.getClassList();
                        if(list1.size()>0) flgs = false;
                        if(list2.size()>0) {
                            //对比已经添加和要添加是否重合
                            Optional<StockTakingClassVo> first = list2.stream().filter(v -> Arrays.asList(classArr).contains(v.getClass())).findFirst();
                            if(first.isPresent()){
                                flgs = false;
                            }
                        };
                        if(list3.size()>0) {
                            Optional<StockTakingCunVo> first = list3.stream().filter(v -> Arrays.asList(classArr).contains(v.getStockClass())).findFirst();
                            if(first.isPresent()){
                                flgs = false;
                            }
                        };
                        if(list4.size()>0) flgs = false;
                    }else if("2".equals(object.getBstyle())){
                        String[] classArr =  obj.getCunList();
                        if(list1.size()>0) flgs = false;
                        if(list2.size()>0) {
                            //存货 存货编码-存货分类    分类比较
                            Optional<StockTakingClassVo> first = list2.stream().filter(v -> {
                                Optional<String> first1 = Arrays.asList(classArr).stream().filter(o -> o.endsWith(v.getStockClass())).findFirst();
                                if(first1.isPresent()){
                                    return false;
                                }
                                return true;
                            }).findFirst();
                            if(first.isPresent()){
                                flgs = false;
                            }
                        }
                        if(list3.size()>0) {
                            Optional<StockTakingCunVo> first = list3.stream().filter(v -> Arrays.asList(classArr).contains(v.getStockNum())).findFirst();
                            if(first.isPresent()){
                                flgs = false;
                            }
                        };
                        if(list4.size()>0) flgs = false;
                    }else if("3".equals(object.getBstyle())){

                    }

                    return flgs;
                })
                .map(a -> R.ok().setResult(a));


    }

    //盘点
    @GetMapping("autoPd")
    public Mono<R> autoPd(String ccode) {
        return stockTakingsRepository.updateByCcode(ccode).then(Mono.just(R.ok()));
    }

    //
    @GetMapping("clearPd")
    public Mono<R> clearPd(String ccode) {
        return stockTakingsRepository.updateByCcodeZero(ccode).then(Mono.just(R.ok()));
    }

    @Autowired
    private ReportEncodingRulesRepository encodingRulesRepository;

    @PostMapping("code")
    public Mono<R> lastCode(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        String date = map.get("date").toString();
        String type = map.get("type").toString();
        String key = map.get("key").toString();
        return stockWarehousingRepository.findAllTypeAndIyear(type, date.substring(0,4)).collectList().flatMap(list->{
            return encodingRulesRepository.findByFileType(key).flatMap(tips->{
                ReportEncodingRules obj = tips;
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


    @GetMapping("audit")
    public Mono<R> audit(String ccode, String userName) {
        String year = "2022";
        // 根据盘点单编码查询  盘点明细 判断盈亏添加盈亏表和下游 修改现存量信息
        AtomicReference<String> newccode = new AtomicReference<>("");
        return stockTakingRepository.findAllByCcode(ccode)
                .flatMap(st->{
                    return stockTakingsRepository.findByCcode(ccode)
                            .collectList()
                            .map(stsList->{
                                st.setStsList(stsList);
                                return st;
                            });
                })
                .flatMap(st->{
                    //获取单据最新的编码-其他入库
                    String date = st.getDdate();//map.get("date").toString();
                    String type = "QTRKD";//map.get("type").toString();
                    String key = "3-13";//map.get("key").toString();
                    return stockWarehousingRepository.findAllTypeAndIyear(type, date.substring(0,4))
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
                                                    newccode.set(pre.toString() + String.format("%0" + l + "d", t));
                                                    return Mono.just(st);//Mono.just(pre.toString()+String.format("%0"+l+"d",t));
                                                }
                                                newccode.set(pre.toString() + "0001");
                                                return Mono.just(st);//Mono.just(pre.toString()+"0001");
                                            }else{
                                                newccode.set(pre.toString() + "0001");
                                                return Mono.just(st);//Mono.just(pre.toString()+"0001");
                                            }
                                });
                       });
                })
                .flatMap(st->{
                    //获取单据最新的编码-其他出库
                    String date = st.getDdate();//map.get("date").toString();
                    String type = "QTCKD";//map.get("type").toString();
                    String key = "3-21";//map.get("key").toString();
                    return stockSaleousingRepository.findAllByBillStyleAndIyearOrderByDdateAscCcodeAsc(type, date.substring(0,4))
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
                                                    newccode.set(pre.toString() + String.format("%0" + l + "d", t));
                                                    return Mono.just(st);//Mono.just(pre.toString()+String.format("%0"+l+"d",t));
                                                }
                                                newccode.set(pre.toString() + "0001");
                                                return Mono.just(st);//Mono.just(pre.toString()+"0001");
                                            }else{
                                                newccode.set(pre.toString() + "0001");
                                                return Mono.just(st);//Mono.just(pre.toString()+"0001");
                                            }
                                        });
                            });
                })
                .map(st -> {
                    st.setBcheck("1");
                    st.setBcheckUser(userName);
                    st.setBcheckTime(LocalDate.now().toString());
                    return st;
                })
                .map(st -> {
                    List<StockTakings> stsList = st.getStsList();
                    //盘盈
                    List<StockTakings> pyList = stsList.stream().filter(v -> v.getQuantityUk().compareTo(BigDecimal.ZERO) == 1).collect(Collectors.toList());
                    if(pyList.size()>0){
                        List<StockWarehousings> pysList = new ArrayList<>();
                        StockWarehousing py =  new StockWarehousing();
                        py.setIyear(st.getIyear());
                        py.setCmaker(st.getCmaker());
                        py.setDdate(st.getDdate());
                        py.setCwhcode(st.getCwhcode());
                        py.setCdepcode(st.getCdepcode());
                        py.setCpersoncode(st.getPdUserid());
                        py.setBcheck(st.getBcheck());
                        py.setBcheckTime(st.getBcheckTime());
                        py.setBcheckUser(st.getBcheckUser());
                        py.setCmakerTime(LocalDateTime.now().toString());

                        py.setBstyle("盘盈入库");//入库类别（收发方式中的收方向编码）
                        py.setUnitType("etc");
                        py.setUnitValue("盘点单盘盈生成");
                        py.setBillStyle("QTRKD");
                        py.setSourcetype("PDD");//来源单据类型id
                        py.setCcode(newccode.get());
                        py.setBdocumStyle("0");

                        final BigDecimal[] squantity = {new BigDecimal("0")};
                        final BigDecimal[] squantity1 = {new BigDecimal("0")};
                        final BigDecimal[] squantity2 = {new BigDecimal("0")};
                        pyList.forEach(v->{
                            StockWarehousings sw =  new StockWarehousings();
                            squantity[0] = squantity[0].add(v.getQuantityPd());
                            squantity1[0] = squantity1[0].add(v.getSubPandian1());
                            squantity2[0] = squantity2[0].add(v.getSubPandian2());
                            sw.setCmakerTime(v.getCfree1());
                            sw.setLineCode(v.getLineCode());
                            sw.setCmaker(st.getCmaker());
                            sw.setIyear(st.getIyear());
                            sw.setCunitid(v.getCunitid());//主计量

                            sw.setCwhcode(v.getCwhcode());
                            sw.setCinvode(v.getCinvode());
                            sw.setBatchId(v.getBatchId());
                            sw.setCunitid(v.getCunitid());
                            sw.setCunitidF1(v.getCunitidF1());
                            sw.setCunitidF2(v.getCunitidF2());
                            sw.setUnitId(v.getCunitid());
                            sw.setBaseQuantity(keepDecimals(v.getQuantityUk(), 10));
                            sw.setSubQuantity1(keepDecimals(v.getQuantityUk1(), 10));
                            sw.setSubQuantity2(keepDecimals(v.getQuantityUk2(), 10));

                            sw.setCwhcode1(v.getCwhcode1());
                            sw.setCwhcode2(v.getCwhcode2());
                            sw.setCwhcode3(v.getCwhcode3());
                            sw.setCwhcode4(v.getCwhcode4());
                            sw.setCwhcode5(v.getCwhcode5());
                            sw.setCwhcode6(v.getCwhcode6());
                            sw.setDpdate(v.getDpdate());
                            sw.setDvdate(v.getDvdate());
                            sw.setCsource("PDD");//单据来源
                            sw.setBcheck(st.getBcheck());
                            sw.setBcheckTime(st.getBcheckTime());
                            sw.setBcheckUser(st.getBcheckUser());
                            sw.setSourcetype("PDD");//来源单据类型id
                            sw.setSourcecode(v.getCcode());//来源单据编码
                            sw.setSourcedetailId(v.getLineCode());
                            sw.setSourcedate(st.getDdate());//来源单据日期
                            sw.setQuantityPd(v.getQuantityPd());
                            sw.setSubPandian1(v.getSubPandian1());
                            sw.setSubPandian2(v.getSubPandian2());
                            sw.setQuantityUk(v.getQuantityUk());
                            sw.setQuantityUk1(v.getQuantityUk1());
                            sw.setQuantityUk2(v.getQuantityUk2());

                            //批次 取批次采购价    当前的采购价 最近的采购价
                            sw.setPrice(Objects.nonNull(v.getPrice())?v.getPrice():"0.00");
                            sw.setIcost(Objects.nonNull(v.getPrice())?keepDecimals(new BigDecimal(v.getPrice()).multiply(new BigDecimal(sw.getBaseQuantity())),10):"0.00");

                            sw.setCangkuDuli("1");
                            sw.setLineId("1");
                            sw.setBdocumStyle("0");
                            sw.setIsGive("0");
                            sw.setBillStyle("QTRKD");
                            sw.setBstyle("盘盈入库");//入库类别（收发方式中的收方向编码）
                            sw.setDdate(st.getDdate());
                            sw.setCcode(newccode.get());
                            sw.setCmakerTime(LocalDateTime.now().toString());
                            sw.setCgUnitId(v.getCunitid());
                            sw.setCnumber(sw.getBaseQuantity());
                            pysList.add(sw);
                        });
                        py.setSquantity(squantity[0].toString());//合计
                        py.setSquantity1(keepDecimals(squantity1[0], 10));
                        py.setSquantity2(keepDecimals(squantity2[0], 10));
                        st.setPy(py);
                        st.setPyList(pysList);
                    }

                    //盘亏
                    List<StockTakings> pkList = stsList.stream().filter(v -> v.getQuantityUk().compareTo(BigDecimal.ZERO) == -1).collect(Collectors.toList());
                    if(pkList.size()>0){
                        List<StockSaleousings> pksList = new ArrayList<>();
                        StockSaleousing pk =  new StockSaleousing();
                        pk.setCcode(st.getCcode());
                        pk.setIyear(st.getIyear());
                        pk.setCmaker(st.getCmaker());
                        pk.setDdate(st.getDdate());
                        pk.setIyear(st.getIyear());
                        pk.setBstyle("盘亏出库");//入库类别（收发方式中的收方向编码）
                        pk.setCwhcode(st.getCwhcode());
                        pk.setCdepcode(st.getCdepcode());
                        pk.setBcheck(st.getBcheck());
                        pk.setBcheckTime(st.getBcheckTime());
                        pk.setBcheckUser(st.getBcheckUser());
                        pk.setCpersoncode(st.getPdUserid());
                        pk.setCmakerTime(LocalDateTime.now().toString());

                        pk.setBstyle("盘亏出库");//入库类别（收发方式中的收方向编码）
                        pk.setUnitType("etc");
                        pk.setUnitValue("盘点单盘亏生成");
                        pk.setBillStyle("QTCKD");
                        pk.setSourcetype("PDD");//来源单据类型id
                        pk.setCcode(newccode.get());
                        pk.setBdocumStyle("0");

                        final BigDecimal[] squantity = {new BigDecimal("0")};
                        final BigDecimal[] squantity1 = {new BigDecimal("0")};
                        final BigDecimal[] squantity2 = {new BigDecimal("0")};
                        pkList.forEach(v->{
                            StockSaleousings sw =  new StockSaleousings();
                            squantity[0] = squantity[0].add(v.getQuantityPd());
                            squantity1[0] = squantity1[0].add(v.getSubPandian1());
                            squantity2[0] = squantity2[0].add(v.getSubPandian2());
                            sw.setCmakerTime(v.getCfree1());
                            sw.setLineCode(v.getLineCode());
                            sw.setCmaker(st.getCmaker());
                            sw.setIyear(st.getIyear());
                            sw.setCunitid(v.getCunitid());//主计量

                            sw.setCwhcode(v.getCwhcode());
                            sw.setCinvode(v.getCinvode());
                            sw.setBatchId(v.getBatchId());
                            sw.setCunitid(v.getCunitid());
                            sw.setCunitidF1(v.getCunitidF1());
                            sw.setCunitidF2(v.getCunitidF2());
                            sw.setUnitId(v.getCunitid());
                            sw.setBaseQuantity(keepDecimals(v.getQuantityUk(), 10));
                            sw.setSubQuantity1(keepDecimals(v.getQuantityUk1(), 10));
                            sw.setSubQuantity2(keepDecimals(v.getQuantityUk2(), 10));

                            sw.setCwhcode1(v.getCwhcode1());
                            sw.setCwhcode2(v.getCwhcode2());
                            sw.setCwhcode3(v.getCwhcode3());
                            sw.setCwhcode4(v.getCwhcode4());
                            sw.setCwhcode5(v.getCwhcode5());
                            sw.setCwhcode6(v.getCwhcode6());
                            sw.setDpdate(v.getDpdate());
                            sw.setDvdate(v.getDvdate());
                            sw.setCsource("PDD");//单据来源
                            sw.setBcheck(st.getBcheck());
                            sw.setBcheckTime(st.getBcheckTime());
                            sw.setBcheckUser(st.getBcheckUser());
                            sw.setSourcetype("PDD");//来源单据类型id
                            sw.setSourcecode(v.getCcode());//来源单据编码
                            sw.setSourcedetailId(v.getLineCode());
                            sw.setSourcedate(st.getDdate());//来源单据日期
                            sw.setQuantityPd(v.getQuantityPd());
                            sw.setSubPandian1(v.getSubPandian1());
                            sw.setSubPandian2(v.getSubPandian2());
                            sw.setQuantityUk(v.getQuantityUk());
                            sw.setQuantityUk1(v.getQuantityUk1());
                            sw.setQuantityUk2(v.getQuantityUk2());

                            //批次 取批次采购价    当前的采购价 最近的采购价
                            sw.setPrice(Objects.nonNull(v.getPrice())?v.getPrice():"0.00");
                            sw.setIcost(Objects.nonNull(v.getPrice())?keepDecimals(new BigDecimal(v.getPrice()).multiply(new BigDecimal(sw.getBaseQuantity())),10):"0.00");

                            sw.setCangkuDuli("1");
                            sw.setLineId("1");
                            //sw.setBdocumStyle("0");
                            sw.setIsGive("0");
                            sw.setBillStyle("QTCKD");
                            sw.setBstyle("盘亏出库");//入库类别（收发方式中的收方向编码）
                            sw.setDdate(st.getDdate());
                            sw.setCcode(newccode.get());
                            sw.setCmakerTime(LocalDateTime.now().toString());
                            sw.setXsUnitId(v.getCunitid());
                            sw.setQuantity(sw.getBaseQuantity());
                            pksList.add(sw);
                        });
                        pk.setSquantity(squantity[0].toString());//合计
                        pk.setSquantity1(keepDecimals(squantity1[0], 10));
                        pk.setSquantity2(keepDecimals(squantity2[0], 10));
                        st.setPk(pk);
                        st.setPkList(pksList);
                    }
                    return st;
                })
                .map(st -> {
                    //下游
                    List<StockTakingSource> sList = new ArrayList<>();
                    if(Objects.nonNull(st.getPyList()) && st.getPyList().size()>0){
                        StockTakingSource sts = new StockTakingSource();
                        sts.setXyBillStyle("QTRKD");
                        sts.setBillStyle("PDD");
                        sts.setIyear(st.getIyear());
                        sts.setCcode(st.getCcode());
                        sts.setCcodeDate(st.getDdate());
                        sts.setSyccode(st.getPy().getCcode());
                        sts.setSyccodeDate(st.getPy().getDdate());
                        sList.add(sts);
                    }
                    if(Objects.nonNull(st.getPkList()) && st.getPkList().size()>0){
                        StockTakingSource sts = new StockTakingSource();
                        sts.setXyBillStyle("QTCKD");
                        sts.setBillStyle("PDD");
                        sts.setIyear(st.getIyear());
                        sts.setCcode(st.getCcode());
                        sts.setCcodeDate(st.getDdate());
                        sts.setSyccode(st.getPk().getCcode());
                        sts.setSyccodeDate(st.getPk().getDdate());
                        sList.add(sts);
                    }
                    st.setSList(sList);
                    return st;
                })
                .flatMap(st->{
                    if(Objects.isNull(st.getPy())){
                        return Mono.just(st);
                    }
                    return stockWarehousingRepository.save(st.getPy()).then(Mono.just(st));
                })
                .flatMap(st->{
                    if(Objects.isNull(st.getPyList())){
                        return Mono.just(st);
                    }
                    return stockWarehousingsRepository.saveAll(st.getPyList()).collectList().then(Mono.just(st));
                })
                .flatMap(st->{
                    if(Objects.isNull(st.getPk())){
                        return Mono.just(st);
                    }
                    return stockSaleousingRepository.save(st.getPk()).then(Mono.just(st));
                })
                .flatMap(st->{
                    if(Objects.isNull(st.getPkList())){
                        return Mono.just(st);
                    }
                    return stockSaleousingsRepository.saveAll(st.getPkList()).collectList().then(Mono.just(st));
                })
                .flatMap(st->{
                    //添加下游
                    if(Objects.isNull(st.getSList())){
                        return Mono.just(st);
                    }
                    return stockTakingSourceRepository.saveAll(st.getSList()).collectList().then(Mono.just(st));
                })
                .flatMap(stockTakingRepository::save)
                .map(a -> R.ok().setResult(a));
    }
    private String keepDecimals(BigDecimal b, int len) {
        StringBuilder s = new StringBuilder("");
        for (int i = 0; i < len; i++) s.append("0");
        DecimalFormat decimalFormat = new DecimalFormat("0." + s + "#");
        BigDecimal value = b.abs().setScale(len, BigDecimal.ROUND_HALF_UP);
        return decimalFormat.format(value);
    }

    @GetMapping("auditBack")
    public Mono<R> auditBack(String ccode) {
        //弃审
        //根据盘点code查明细 然后关联现存量 跟新现存量表 删除 盘盈 盘亏 下游
        AtomicReference<String> rkccode = new AtomicReference<>("");
        AtomicReference<String> ckccode = new AtomicReference<>("");
        return stockTakingRepository.findAllByCcode(ccode)
                .flatMap(st->{
                    return stockTakingsRepository.findByCcode(ccode)
                            .collectList()
                            .map(stsList->{
                                st.setStsList(stsList);
                                return st;
                            });
                })
                .flatMap(st->{
                    st.setBcheck("0");
                    st.setBcheckUser(null);
                    st.setBcheckTime(null);
                    return stockTakingRepository.save(st).then(Mono.just(st));
                })
                .flatMap(o->{
                    //获取下游ccode
                    return stockTakingSourceRepository.findALLByCcode(ccode)
                            .collectList()
                            .map(list->{
                                Optional<StockTakingSource> first = list.stream().filter(v -> "QTRKD".equals(v.getXyBillStyle())).findFirst();
                                if(first.isPresent())  rkccode.set(first.get().getSyccode());
                                Optional<StockTakingSource> first1 = list.stream().filter(v -> "QTCKD".equals(v.getXyBillStyle())).findFirst();
                                if(first1.isPresent())  ckccode.set(first1.get().getSyccode());
                                return ccode;
                            });
                })
                .flatMap(o->{
                    return Objects.nonNull(rkccode.get()) ? stockWarehousingRepository.deleteByCcodeAndBillType(rkccode.get(), "QTRKD").then(Mono.just(ccode)):Mono.just(ccode);
                })
                .flatMap(o->{
                    return Objects.nonNull(rkccode.get()) ? stockWarehousingsRepository.deleteByCcodeAndSourcetype(rkccode.get(), "QTRKD").then(Mono.just(ccode)):Mono.just(ccode);
                })
                .flatMap(o->{
                    return Objects.nonNull(ckccode.get()) ? stockSaleousingRepository.deleteByCcodeAndBillType(ckccode.get(), "QTCKD").then(Mono.just(ccode)):Mono.just(ccode);
                })
                .flatMap(o->{
                    return Objects.nonNull(ckccode.get())  ? stockSaleousingsRepository.deleteByCcodeAndSourcetype(ckccode.get(), "QTCKD").then(Mono.just(ccode)):Mono.just(ccode);
                })
                .flatMap(o->{
                    return stockTakingSourceRepository.deleteByCcode(ccode).then(Mono.just(ccode));
                })
                .map(a -> R.ok().setResult(a));

    }

    @GetMapping("/auditCheck/{ccode}/{rkBcheck}/{ckBcheck}/{type}")
    public Mono<R> auditCheck(@PathVariable String ccode,@PathVariable String rkBcheck,@PathVariable String ckBcheck,@PathVariable String type) {
        // 参数设置：入库保存状态就是现存量 标志 1是查询所有 0 查询已审核
        rkBcheck=ObjectUtil.isEmpty(rkBcheck)?"0":rkBcheck;
        ckBcheck=ObjectUtil.isEmpty(ckBcheck)?"0":ckBcheck;
        //弃审前校验现存量
        String finalRkBcheck = rkBcheck;
        String finalCkBcheck = ckBcheck;
        return stockTakingRepository.findAllByCcode(ccode)
                .flatMap(st->{
                    return stockTakingsRepository.findByCcode(ccode)
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
                    List<StockTakings> stsList1 = st.getStsList();
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
                                return stockWarehousingsRepository.findAllByIyearAndCkAndList(year,ck,cinvodeList)
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
                                return stockSaleousingsRepository.findAllByIyearAndCkAndList(year,ck,cinvodeList)
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
                                                str+=v.getBatchid();
                                                /*if(Objects.nonNull(v.getDpdate())){
                                                    str+=v.getDpdate();
                                                }
                                                if(Objects.nonNull(v.getDvdate())){
                                                    str+=v.getDvdate();
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

                                    //现存量存在 并且不等于0的
                                    if(subtract.compareTo(BigDecimal.ZERO) != 0){
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
                                List<StockTakings> stsList = st.getStsList();
                                if("sh".equals(type)){
                                    //审核 其他出库 下游审核未生成
                                    List<StockTakings> rkList = stsList.stream()
                                            .filter(v -> v.getQuantityUk().compareTo(BigDecimal.ZERO) != 0)
                                            .filter(v -> v.getBaseQuantity().compareTo(v.getQuantityPd()) > 0)
                                            .collect(Collectors.toList());
                                    //获取显存量的存货数据 然后比对对应的存货数据
                                    rkList.stream().forEach(v->{
                                        Optional<StockVo> first = list.stream().filter(o -> o.getStockNum().equals(v.getCinvode()) &&  (Objects.nonNull(v.getBatchId())? o.getBatchId().equals(v.getBatchId()) :true)).findFirst();
                                        //现存量大于出库数量
                                        if(first.isPresent() && first.get().getXcl().compareTo(v.getQuantityUk()) >= 0){
                                            b.set(true);
                                        }
                                    });
                                }else{
                                    //弃审 其他入库 下游审核未生成
                                    List<StockTakings> rkList = stsList.stream()
                                            .filter(v -> v.getQuantityUk().compareTo(BigDecimal.ZERO) != 0)
                                            .filter(v -> v.getBaseQuantity().compareTo(v.getQuantityPd()) < 0)
                                            .collect(Collectors.toList());
                                    //获取显存量的存货数据 然后比对对应的存货数据
                                    rkList.stream().forEach(v->{
                                        Optional<StockVo> first = list.stream().filter(o -> o.getStockNum().equals(v.getCinvode()) &&  Objects.nonNull(v.getBatchId())? o.getBatchId().equals(v.getBatchId()) :true).findFirst();
                                        //现存量大于出库数量
                                        if(first.isPresent() && first.get().getXcl().compareTo(v.getQuantityUk()) >= 0){
                                            b.set(true);
                                        }
                                    });
                                    //不存在其他入库则继续审核
                                    if(rkList.size() == 0){
                                        b.set(true);
                                    }
                                }
                                return b.get();
                            });
                })
                .map(a -> R.ok().setResult(a));

    }

    @PostMapping("findBillByCondition")
    public Mono<R> findBillByCondition(@RequestBody Map map) {
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

        return type.equals("PYRKD")? map1:map2;
    }

    @PostMapping("findBillByConditions")
    public Mono<R> findBillByConditions(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        // 单据编码  
        String ccode = map.containsKey("ccode")?map.get("ccode").toString():"";
        String type = map.get("type").toString();
        String iyear = map.get("iyear").toString();
        return stockWarehousingsRepository.findAllByBillStyleAndCcodeOrderByDdateAscCcodeAscLineIdAsc(type, ccode)
                .collectList()
                .map(list -> R.ok().setResult(list));
    }



    @PostMapping("findYkBillByConditions")
    public Mono<R> findYkBillByConditions(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        // 单据编码
        String ccode = map.containsKey("ccode")?map.get("ccode").toString():"";
        String type = map.get("type").toString();
        String iyear = map.get("iyear").toString();
        return stockSaleousingsRepository.findAllByBillStyleAndCcode(type, ccode)
                .collectList()
                .map(list -> R.ok().setResult(list));
    }


    @PostMapping("delLine")
    public Mono<R> delLine(@RequestBody List ids) {
        return stockTakingsRepository.deleteByIds(ids)
                .then(Mono.just(R.ok()));
    }

    @PostMapping("getFwList")
    public Mono<R> getFwList(@RequestBody Map map) {
        return Mono.just(map)
                .flatMap(m->{
                    String ccode = m.get("ccode").toString();
                    String type = m.get("type").toString();
                    if(type.equals("1")){
                        return stockTakingRepository.findAllTakingClass(ccode).collectList()
                                .map(classList->{
                                    return classList;
                                });
                    }else if(type.equals("2")){
                        return stockTakingRepository.findAllTakingCun(ccode).collectList()
                                .map(chList->{
                                    return chList;
                                });
                    }else{
                        return stockTakingRepository.findAllTakingCunbatch(ccode).collectList()
                                .map(cbList->{
                                    return cbList;
                                });
                    }
                })
                .map(list -> R.ok().setResult(list));

    }


    /**
     * 查询盘点单子表
     * @param map
     * @return
     */
    @PostMapping("findByTakingsAndCwhcodeAndCinvode")
    public Mono<R> findByTakingsAndCwhcodeAndCinvode(@RequestBody Map map){
        String iyear=map.get("iyear").toString();
        String cwhcode=map.get("cwhcode").toString();
        String cinvode=map.get("cinvode").toString();
        String batchId=map.get("batchId").toString();
        String dpdate=map.get("dpdate").toString();
        String dvdate=map.get("dvdate").toString();
        return stockTakingsRepository.findByCwhcodeAndCinvodeAndIyearAndBatchIdAndDpdateAndDvdate(cwhcode,cinvode,iyear,batchId,dpdate,dvdate).collectList().map(a->R.ok().setResult(a));
    }

    /**
     *  未审核盘点单 明细存货编码
     *   销货单和销售出库单新增时，要判断存货明细，不能和未审核盘点单的存货相同，否则提示，XXXX存货正在盘点中，不能进行单据处理，请销后再试。
     * @param map
     * @return
     */
    @PostMapping("findByTakingChCodes")
    public Mono<R> findByTakingChCodes(@RequestBody Map map){
       return stockTakingRepository.findAllByBcheck("1").collectList().flatMap(list->
           list.size() == 0?Mono.just(R.ok(new ArrayList<>())):stockTakingsRepository.findAllByCcodeIn(list.stream().map(it -> it.getCcode()).collect(Collectors.toList())).collectList().flatMap(entrys->Mono.just(R.ok(new HashSet<>(entrys.stream().map(it->it.getCinvode()).collect(Collectors.toList()))))));
    }


    /**
     *  导入excel
     * @param map
     * @return
     */
    @PostMapping("improtExcel")
    public Mono<R> improtExcel(@RequestBody Map map) {
        //1.条形码 数量 批号 开始 结束  反算主数量
        //2.存货编码 计量单位(明细) 数量  批号 开始 结束   反算主数量
        //根据存货编码查询存货信息
        //查询存货编码list
        //List<StockTakingImprotRo> list = (List<StockTakingImprotRo>) map.get("list");
        List<StockTakingImprotRo> list= JSONArray.parseArray(map.get("list").toString(),StockTakingImprotRo.class);
        String pdCode = map.get("pdCode").toString();
        String year = map.get("year").toString();
        //需要对导入数据进行处理 1.重复 2.不存在
        //导入过程中 存货编码和计量单位不对应  1单剂量不对应 2.多剂量不对应
        List<String> stockList = list.stream().map(v -> v.getStockNum()).collect(Collectors.toList());
        return stockRepository.findAllByStockNums(stockList).collectList()
                .flatMap(sList->{
                    //查询单计量单位
                    return sysUnitOfMeaRepository.findAll()
                            .collectList()
                            .map(umlist->{
                                map.put("umlist",umlist);
                                return sList;
                            });
                })
                .flatMap(sList->{
                    //查询多计量单位
                    return sysUnitOfMeaListRepository.findAlls()
                            .collectList()
                            .map(umslist->{
                                map.put("umslist",umslist);
                                return sList;
                            });

                })
                .flatMap(sList->{
                    //查询盘点明细
                    return stockTakingsRepository.findByCcodes(pdCode)
                            .collectList()
                            .map(sclist->{
                                map.put("sclist",sclist);
                                return sList;
                            });
                })
                .map(sList->{
                    //盘点明细存在
                    List<StockTakingsVo> pdlist =  (List<StockTakingsVo>) map.get("sclist");
                    //根据存货设置筛选  存货 批次 日期
                    List<StockTakingsVo> xcl = pdlist.stream().filter(v -> {
                        Optional<StockTakingImprotRo> first = list.stream().filter(o -> o.getStockNum().equals(v.getCinvode())
                                && (Objects.nonNull(v.getBatchId())&&Objects.nonNull(o.getBatchId()) ? v.getBatchId().equals(o.getBatchId()) : true)
                                && (Objects.nonNull(v.getDpdate())&&Objects.nonNull(o.getDpdate()) ? v.getDpdate().equals(o.getDpdate()) : true)
                                && (Objects.nonNull(v.getDvdate())&&Objects.nonNull(o.getDvdate()) ? v.getDvdate().equals(o.getDvdate()) : true)).findFirst();
                        if(first.isPresent()){
                            return true;
                        }
                        return false;
                    }).collect(Collectors.toList());
                    List<StockTakings> stlist =  new ArrayList<>();
                    xcl.forEach(v->{
                        //该存货对应数据
                        Optional<StockTakingImprotRo> first = list.stream()
                                .filter(o -> o.getStockNum().equals(v.getCinvode())
                                        && (Objects.nonNull(v.getBatchId())&&Objects.nonNull(o.getBatchId()) ? v.getBatchId().equals(o.getBatchId()) : true)
                                        && (Objects.nonNull(v.getDpdate())&&Objects.nonNull(o.getDpdate()) ? v.getDpdate().equals(o.getDpdate()) : true)
                                        && (Objects.nonNull(v.getDvdate())&&Objects.nonNull(o.getDvdate()) ? v.getDvdate().equals(o.getDvdate()) : true))
                                .findFirst();
                        StockTakingImprotRo importRo = first.get();

                        //对应计量单位
                        if("单计量".equals(v.getUimtype())){
                            v.setQuantityPd(new BigDecimal(importRo.getStockNum()));
                        }else{
                            //多剂量 先筛选对应的计量组 然后匹配对应明细 计算其他盘点数据
                            List<SysUnitOfMeaListVo> umslist =  (List<SysUnitOfMeaListVo>) map.get("umslist");
                            List<SysUnitOfMeaListVo> somlist = umslist.stream().filter(o -> o.getUmid().equals(v.getUimid()))
                                    .sorted(Comparator.comparing(SysUnitOfMeaListVo::getId))
                                    .collect(Collectors.toList());
                            //判断计量单位是
                            Optional<SysUnitOfMeaListVo> main = somlist.stream().filter(o -> "true".equals(o.getIsMain())).findFirst();
                            SysUnitOfMeaListVo sysUnitOfMeaList = main.get();
                            if(sysUnitOfMeaList.getUnitName().equals(importRo.getUnitName())){
                                //主计量
                                v.setQuantityPd(new BigDecimal(importRo.getNum()));
                                v.setQuantityUk(v.getQuantityPd());
                                //计算其他值
                                if(somlist.size() > 1){
                                    //计量1
                                    BigDecimal b1 = new BigDecimal(importRo.getNum());
                                    BigDecimal b2 = new BigDecimal(somlist.get(1).getConversionRate());
                                    v.setSubPandian1(b1.divide(b2,10,BigDecimal.ROUND_UP));
                                    v.setQuantityUk1(v.getSubPandian1());
                                }
                                if(somlist.size() > 2){
                                    //计量2
                                    BigDecimal b1 = new BigDecimal(importRo.getNum());
                                    BigDecimal b2 = new BigDecimal(somlist.get(2).getConversionRate());
                                    v.setSubPandian2(b1.divide(b2,10,BigDecimal.ROUND_UP));
                                    v.setQuantityUk2(v.getSubPandian2());
                                }
                            }else{
                                //非主计量
                                if(somlist.size() == 2){
                                    //主计量
                                    BigDecimal b1 = new BigDecimal(importRo.getNum());
                                    BigDecimal b2 = new BigDecimal(somlist.get(1).getConversionRate());
                                    v.setQuantityPd(b1.multiply(b2));
                                    v.setQuantityUk(v.getQuantityPd());

                                    //计量1
                                    v.setSubPandian1(new BigDecimal(importRo.getNum()));
                                    v.setQuantityUk1(v.getSubPandian1());
                                }

                                if(somlist.size() > 2){
                                    if(somlist.get(1).getUnitName().equals(importRo.getUnitName())){
                                        //主计量
                                        BigDecimal b1 = new BigDecimal(importRo.getNum());
                                        BigDecimal b2 = new BigDecimal(somlist.get(1).getConversionRate());
                                        v.setQuantityPd(b1.multiply(b2));
                                        v.setQuantityUk(v.getQuantityPd());

                                        //计量1
                                        v.setSubPandian1(new BigDecimal(importRo.getNum()));
                                        v.setQuantityUk1(v.getSubPandian1());

                                        //计量2
                                        BigDecimal b3 = new BigDecimal(somlist.get(2).getConversionRate());
                                        v.setSubPandian2(v.getQuantityPd().divide(b3,10,BigDecimal.ROUND_UP));
                                        v.setQuantityUk2(v.getSubPandian2());
                                    }

                                    if(somlist.get(2).getUnitName().equals(importRo.getUnitName())){
                                        //主计量
                                        BigDecimal b1 = new BigDecimal(importRo.getNum());
                                        BigDecimal b2 = new BigDecimal(somlist.get(2).getConversionRate());
                                        v.setQuantityPd(b1.multiply(b2));
                                        v.setQuantityUk(v.getQuantityPd());

                                        //计量1
                                        BigDecimal b3 = new BigDecimal(somlist.get(2).getConversionRate());
                                        v.setSubPandian2(v.getQuantityPd().divide(b3,10,BigDecimal.ROUND_UP));
                                        v.setQuantityUk2(v.getSubPandian2());

                                        //计量2
                                        v.setSubPandian2(new BigDecimal(importRo.getNum()));
                                        v.setQuantityUk2(v.getSubPandian2());
                                    }

                                }
                            }
                        }

                        StockTakings stockTakings = new StockTakings();
                        BeanUtils.copyProperties(v,stockTakings);
                        stlist.add(stockTakings);
                    });

                    //盘点明细不存在
                    //去除盘点
                    List<StockTakingImprotRo> collect = list.stream().filter(f -> {
                        Optional<StockTakingsVo> first = pdlist.stream().filter(o -> o.getCinvode().equals(f.getStockNum())
                                && (Objects.nonNull(f.getBatchId())&&Objects.nonNull(o.getBatchId()) ? f.getBatchId().equals(o.getBatchId()) : true)
                                && (Objects.nonNull(f.getDpdate())&&Objects.nonNull(o.getDpdate()) ? f.getDpdate().equals(o.getDpdate()) : true)
                                && (Objects.nonNull(f.getDvdate())&&Objects.nonNull(o.getDvdate()) ? f.getDvdate().equals(o.getDvdate()) : true)).findFirst();
                        if (first.isPresent()) {
                            return false;
                        }
                        return true;
                    }).collect(Collectors.toList());
                    //去除不存在的存货
                    List<StockVo> stockNoList = sList.stream()
                            .filter(v -> {
                                Optional<StockTakingImprotRo> first = collect.stream().filter(f -> v.getStockNum().equals(f.getStockNum())).findFirst();
                                if (first.isPresent()) {
                                    return true;
                                }
                                return false;
                            }).collect(Collectors.toList());
                    List<StockTakings> stList = new ArrayList<>();
                    stockNoList.forEach(v->{
                        //该存货对应数据
                        Optional<StockTakingImprotRo> first = list.stream().filter(o -> stockList.contains(v.getStockNum())).findFirst();
                        StockTakingImprotRo importRo = first.get();

                        StockTakings st =  new StockTakings();
                        BeanUtils.copyProperties(v, st);
                        st.setCunitidType(v.getStockMeasurementType());
                        st.setLineCode(UUID.randomUUID().toString());
                        st.setCcode(pdCode);
                        st.setBatchId(importRo.getBatchId());
                        st.setDpdate(importRo.getDpdate());
                        st.setDvdate(importRo.getDvdate());
                        st.setId(null);
                        st.setIyear(year);
                        st.setCinvode(v.getStockNum());
                        st.setCwhcode(v.getStockCangku());

                        st.setBaseQuantity(BigDecimal.ZERO);
                        st.setSubQuantity1(BigDecimal.ZERO);
                        st.setSubQuantity2(BigDecimal.ZERO);
                        if("单计量".equals(v.getStockMeasurementType())){
                            List<SysUnitOfMea> umlist =  (List<SysUnitOfMea>) map.get("umlist");
                            Optional<SysUnitOfMea> um = umlist.stream().filter(o -> o.getUnitName().equals(v.getStockUnitName())).findFirst();
                            st.setQuantityPd(new BigDecimal(importRo.getNum()));
                            st.setCunitid(um.get().getId());
                        }else{
                            //多剂量 先筛选对应的计量组 然后匹配对应明细 计算其他盘点数据
                            List<SysUnitOfMeaListVo> umslist =  (List<SysUnitOfMeaListVo>) map.get("umslist");
                            List<SysUnitOfMeaListVo> somlist = umslist.stream().filter(o -> Objects.nonNull(o.getUmid()) ?o.getUmid().equals(v.getStockMeasurementUnit()):false)
                                    .sorted(Comparator.comparing(SysUnitOfMeaListVo::getId))
                                    .collect(Collectors.toList());
                            //判断计量单位是
                            Optional<SysUnitOfMeaListVo> main = somlist.stream().filter(o -> "true".equals(o.getIsMain())).findFirst();
                            SysUnitOfMeaListVo sysUnitOfMeaList = main.get();
                            st.setCunitid(sysUnitOfMeaList.getId());

                            if(sysUnitOfMeaList.getUnitName().equals(importRo.getUnitName())){
                                //主计量
                                st.setQuantityPd(new BigDecimal(importRo.getNum()));
                                st.setQuantityUk(st.getQuantityPd());
                                //计算其他值
                                if(somlist.size() > 1){
                                    //计量1
                                    BigDecimal b1 = new BigDecimal(importRo.getNum());
                                    BigDecimal b2 = new BigDecimal(somlist.get(1).getConversionRate());
                                    st.setSubPandian1(b1.divide(b2,10,BigDecimal.ROUND_UP));
                                    st.setQuantityUk1(st.getSubPandian1());
                                    st.setCunitidF1(somlist.get(1).getId());
                                }
                                if(somlist.size() > 2){
                                    //计量2
                                    BigDecimal b1 = new BigDecimal(importRo.getNum());
                                    BigDecimal b2 = new BigDecimal(somlist.get(2).getConversionRate());
                                    st.setSubPandian2(b1.divide(b2,10,BigDecimal.ROUND_UP));
                                    st.setQuantityUk2(st.getSubPandian2());
                                    st.setCunitidF2(somlist.get(2).getId());

                                }
                            }else{
                                //非主计量
                                if(somlist.size() == 2){
                                    //主计量
                                    BigDecimal b1 = new BigDecimal(importRo.getNum());
                                    BigDecimal b2 = new BigDecimal(somlist.get(1).getConversionRate());
                                    st.setQuantityPd(b1.multiply(b2));
                                    st.setQuantityUk(st.getQuantityPd());

                                    //计量1
                                    st.setSubPandian1(new BigDecimal(importRo.getNum()));
                                    st.setQuantityUk1(st.getSubPandian1());
                                    st.setCunitidF1(somlist.get(1).getId());

                                }

                                if(somlist.size() > 2){
                                    if(somlist.get(1).getUnitName().equals(importRo.getUnitName())){
                                        //主计量
                                        BigDecimal b1 = new BigDecimal(importRo.getNum());
                                        BigDecimal b2 = new BigDecimal(somlist.get(1).getConversionRate());
                                        st.setQuantityPd(b1.multiply(b2));
                                        st.setQuantityUk(st.getQuantityPd());

                                        //计量1
                                        st.setSubPandian1(new BigDecimal(importRo.getNum()));
                                        st.setQuantityUk1(st.getSubPandian1());
                                        st.setCunitidF1(somlist.get(1).getId());

                                        //计量2
                                        BigDecimal b3 = new BigDecimal(somlist.get(2).getConversionRate());
                                        st.setSubPandian2(st.getQuantityPd().divide(b3,10));
                                        st.setQuantityUk2(st.getSubPandian2());
                                        st.setCunitidF2(somlist.get(2).getId());

                                    }

                                    if(somlist.get(2).getUnitName().equals(importRo.getUnitName())){
                                        //主计量
                                        BigDecimal b1 = new BigDecimal(importRo.getNum());
                                        BigDecimal b2 = new BigDecimal(somlist.get(2).getConversionRate());
                                        st.setQuantityPd(b1.multiply(b2));
                                        st.setQuantityUk(st.getQuantityPd());

                                        //计量1
                                        BigDecimal b3 = new BigDecimal(somlist.get(2).getConversionRate());
                                        st.setSubPandian2(st.getQuantityPd().divide(b3,10));
                                        st.setQuantityUk2(st.getSubPandian2());
                                        st.setCunitidF1(somlist.get(1).getId());

                                        //计量2
                                        st.setSubPandian2(new BigDecimal(importRo.getNum()));
                                        st.setQuantityUk2(st.getSubPandian2());
                                        st.setCunitidF2(somlist.get(2).getId());
                                    }

                                }
                            }
                        }

                        stList.add(st);
                    });
                    map.put("stockList",stList);
                    map.put("stlist",stlist);
                    return xcl;
                })
                .flatMap(pddlist->{
                    List<StockTakings> stList = (List<StockTakings>) map.get("stlist");
                    return stockTakingsRepository.saveAll(stList).collectList().thenReturn(stList);
                })
                .flatMap(pddlist->{
                    List<StockTakings> stList = (List<StockTakings>) map.get("stockList");
                    return stockTakingsRepository.saveAll(stList).collectList().thenReturn(pddlist);
                })
                .map(o->R.ok().setResult(o));
    }

    @PostMapping("improtExcel2")
    public Mono<R> improtExcel2(@RequestBody List<StockTakingImprotRo> list) {
        //1.条形码 数量 批号 开始 结束  反算主数量
        //根据存货编码查询存货信息
        //查询存货编码list
        String pdCode = "YS-202204-003";
        Map map =  new HashMap();
        //需要对导入数据进行处理 1.重复 2.不存在
        //导入过程中 存货编码和计量单位不对应  1单剂量不对应 2.多剂量不对应
        List<String> stockList = list.stream().map(v -> v.getStockBarcode()).collect(Collectors.toList());
        return stockRepository.findAllByStockBarcode(stockList).collectList()
                .flatMap(sList->{
                    //查询单计量单位
                    return sysUnitOfMeaRepository.findAll()
                            .collectList()
                            .map(umlist->{
                                map.put("umlist",umlist);
                                return sList;
                            });
                })
                .flatMap(sList->{
                    //查询多计量单位
                    return sysUnitOfMeaListRepository.findAlls()
                            .collectList()
                            .map(umslist->{
                                map.put("umslist",umslist);
                                return sList;
                            });

                })
                .flatMap(sList->{
                    //查询盘点明细
                    return stockTakingsRepository.findByCcodes(pdCode)
                            .collectList()
                            .map(sclist->{
                                map.put("sclist",sclist);
                                return sList;
                            });
                })
                .map(sList->{
                    //盘点明细存在
                    List<StockTakingsVo> pdlist =  (List<StockTakingsVo>) map.get("sclist");
                    //根据存货设置筛选  存货 批次 日期
                    List<StockTakingsVo> xcl = pdlist.stream().filter(v -> {
                        Optional<StockTakingImprotRo> first = list.stream().filter(o ->
                                ( o.getStockBarcode().equals(v.getSb())
                                || o.getStockBarcode().equals(v.getSb1())
                                || o.getStockBarcode().equals(v.getSb2()))

                                && (Objects.nonNull(v.getBatchId())&&Objects.nonNull(o.getBatchId()) ? v.getBatchId().equals(o.getBatchId()) : true)
                                && (Objects.nonNull(v.getDpdate())&&Objects.nonNull(o.getDpdate()) ? v.getDpdate().equals(o.getDpdate()) : true)
                                && (Objects.nonNull(v.getDvdate())&&Objects.nonNull(o.getDvdate()) ? v.getDvdate().equals(o.getDvdate()) : true)).findFirst();
                        if(first.isPresent()){
                            return true;
                        }
                        return false;
                    }).collect(Collectors.toList());
                    List<StockTakings> stlist =  new ArrayList<>();
                    xcl.forEach(v->{
                        //该存货对应数据
                        Optional<StockTakingImprotRo> first = list.stream()
                                .filter(o ->
                                        ( o.getStockBarcode().equals(v.getSb())
                                        || o.getStockBarcode().equals(v.getSb1())
                                        || o.getStockBarcode().equals(v.getSb2()))
                                        && (Objects.nonNull(v.getBatchId())&&Objects.nonNull(o.getBatchId()) ? v.getBatchId().equals(o.getBatchId()) : true)
                                        && (Objects.nonNull(v.getDpdate())&&Objects.nonNull(o.getDpdate()) ? v.getDpdate().equals(o.getDpdate()) : true)
                                        && (Objects.nonNull(v.getDvdate())&&Objects.nonNull(o.getDvdate()) ? v.getDvdate().equals(o.getDvdate()) : true))
                                .findFirst();
                        StockTakingImprotRo importRo = first.get();

                        //对应计量单位
                        if("单计量".equals(v.getUimtype())){
                            v.setQuantityPd(new BigDecimal(importRo.getStockNum()));
                        }else{
                            //多剂量 先筛选对应的计量组 然后匹配对应明细 计算其他盘点数据
                            List<SysUnitOfMeaListVo> umslist =  (List<SysUnitOfMeaListVo>) map.get("umslist");
                            List<SysUnitOfMeaListVo> somlist = umslist.stream().filter(o -> o.getUmid().equals(v.getUimid()))
                                    .sorted(Comparator.comparing(SysUnitOfMeaListVo::getId))
                                    .collect(Collectors.toList());
                            if(importRo.getStockBarcode().equals(v.getStockBarcode())){
                                //主计量
                                v.setQuantityPd(new BigDecimal(importRo.getNum()));
                                v.setQuantityUk(v.getQuantityPd());
                                //计算其他值
                                if(somlist.size() > 1){
                                    //计量1
                                    BigDecimal b1 = new BigDecimal(importRo.getNum());
                                    BigDecimal b2 = new BigDecimal(somlist.get(1).getConversionRate());
                                    v.setSubPandian1(b1.divide(b2,10,BigDecimal.ROUND_UP));
                                    v.setQuantityUk1(v.getSubPandian1());
                                }
                                if(somlist.size() > 2){
                                    //计量2
                                    BigDecimal b1 = new BigDecimal(importRo.getNum());
                                    BigDecimal b2 = new BigDecimal(somlist.get(2).getConversionRate());
                                    v.setSubPandian2(b1.divide(b2,10,BigDecimal.ROUND_UP));
                                    v.setQuantityUk2(v.getSubPandian2());
                                }
                            }else if(importRo.getStockBarcode().equals(v.getSb1())){
                                if(somlist.size() > 1){
                                    //主计量
                                    BigDecimal b1 = new BigDecimal(importRo.getNum());
                                    BigDecimal b2 = new BigDecimal(somlist.get(1).getConversionRate());
                                    v.setQuantityPd(b1.multiply(b2));
                                    v.setQuantityUk(v.getQuantityPd());

                                    //计量1
                                    v.setSubPandian1(new BigDecimal(importRo.getNum()));
                                    v.setQuantityUk1(v.getSubPandian1());

                                }

                                if(somlist.size() > 2){
                                    //计量2
                                    BigDecimal b1 = new BigDecimal(importRo.getNum());
                                    BigDecimal b2 = new BigDecimal(somlist.get(2).getConversionRate());
                                    v.setSubPandian2(b1.divide(b2,10,BigDecimal.ROUND_UP));
                                    v.setQuantityUk2(v.getSubPandian2());
                                }
                            }else if(importRo.getStockBarcode().equals(v.getSb2())){
                                if(somlist.size() > 1){
                                    //主计量
                                    BigDecimal b1 = new BigDecimal(importRo.getNum());
                                    BigDecimal b2 = new BigDecimal(somlist.get(2).getConversionRate());
                                    v.setQuantityPd(b1.multiply(b2));
                                    v.setQuantityUk(v.getQuantityPd());

                                    //计量1
                                    BigDecimal b3 = new BigDecimal(somlist.get(1).getConversionRate());
                                    v.setSubPandian1(b1.divide(b3,10,BigDecimal.ROUND_UP));
                                    v.setQuantityUk1(v.getSubPandian1());

                                }

                                if(somlist.size() > 2){
                                    //计量2
                                    v.setSubPandian2(new BigDecimal(importRo.getNum()));
                                    v.setQuantityUk2(v.getSubPandian2());
                                }
                            }
                        }

                        StockTakings stockTakings = new StockTakings();
                        BeanUtils.copyProperties(v,stockTakings);
                        stlist.add(stockTakings);
                    });

                    //盘点明细不存在
                    //去除盘点
                    List<StockTakingImprotRo> collect = list.stream().filter(f -> {
                        Optional<StockTakingsVo> first = pdlist.stream().filter(o ->
                                ( o.getSb().equals(f.getStockBarcode())
                                || o.getSb1().equals(f.getStockBarcode())
                                || o.getSb2().equals(f.getStockBarcode()))
                                && (Objects.nonNull(f.getBatchId())&&Objects.nonNull(o.getBatchId()) ? f.getBatchId().equals(o.getBatchId()) : true)
                                && (Objects.nonNull(f.getDpdate())&&Objects.nonNull(o.getDpdate()) ? f.getDpdate().equals(o.getDpdate()) : true)
                                && (Objects.nonNull(f.getDvdate())&&Objects.nonNull(o.getDvdate()) ? f.getDvdate().equals(o.getDvdate()) : true)).findFirst();
                        if (first.isPresent()) {
                            return false;
                        }
                        return true;
                    }).collect(Collectors.toList());
                    //去除不存在的存货
                    List<StockVo> stockNoList = sList.stream()
                            .filter(v -> {
                                Optional<StockTakingImprotRo> first = collect.stream().filter(f ->
                                         ( f.getStockBarcode().equals(v.getStockBarcode())
                                        || f.getStockBarcode().equals(v.getStockUnitBarcode1())
                                        || f.getStockBarcode().equals(v.getStockUnitBarcode2()))

                                ).findFirst();
                                if (first.isPresent()) {
                                    return true;
                                }
                                return false;
                            }).collect(Collectors.toList());
                    List<StockTakings> stList = new ArrayList<>();
                    stockNoList.forEach(v->{
                        //该存货对应数据
                        Optional<StockTakingImprotRo> first = list.stream().filter(o ->
                                ( o.getStockBarcode().equals(v.getStockBarcode())
                                || o.getStockBarcode().equals(v.getStockUnitBarcode1())
                                || o.getStockBarcode().equals(v.getStockUnitBarcode2()))
                                ).findFirst();
                        StockTakingImprotRo importRo = first.get();

                        StockTakings st =  new StockTakings();
                        BeanUtils.copyProperties(v, st);
                        st.setCunitidType(v.getStockMeasurementType());
                        st.setLineCode(UUID.randomUUID().toString());
                        st.setCcode(pdCode);
                        st.setBatchId(importRo.getBatchId());
                        st.setDpdate(importRo.getDpdate());
                        st.setDvdate(importRo.getDvdate());
                        st.setId(null);
                        st.setIyear("2022");
                        st.setCinvode(v.getStockNum());
                        st.setCwhcode(v.getStockCangku());

                        st.setBaseQuantity(BigDecimal.ZERO);
                        st.setSubQuantity1(BigDecimal.ZERO);
                        st.setSubQuantity2(BigDecimal.ZERO);
                        if("单计量".equals(v.getStockMeasurementType())){
                            List<SysUnitOfMea> umlist =  (List<SysUnitOfMea>) map.get("umlist");
                            Optional<SysUnitOfMea> um = umlist.stream().filter(o -> o.getUnitName().equals(v.getStockUnitName())).findFirst();
                            st.setQuantityPd(new BigDecimal(importRo.getNum()));
                            st.setCunitid(um.get().getId());
                        }else{
                            //多剂量 先筛选对应的计量组 然后匹配对应明细 计算其他盘点数据
                            List<SysUnitOfMeaListVo> umslist =  (List<SysUnitOfMeaListVo>) map.get("umslist");
                            List<SysUnitOfMeaListVo> somlist = umslist.stream().filter(o -> Objects.nonNull(o.getUmid()) ?o.getUmid().equals(v.getStockMeasurementUnit()):false)
                                    .sorted(Comparator.comparing(SysUnitOfMeaListVo::getId))
                                    .collect(Collectors.toList());
                            //判断计量单位是
                            Optional<SysUnitOfMeaListVo> main = somlist.stream().filter(o -> "true".equals(o.getIsMain())).findFirst();
                            SysUnitOfMeaList sysUnitOfMeaList = main.get();
                            st.setCunitid(sysUnitOfMeaList.getId());

                            if(importRo.getStockBarcode().equals(v.getStockBarcode())){
                                //主计量
                                st.setQuantityPd(new BigDecimal(importRo.getNum()));
                                st.setQuantityUk(st.getQuantityPd());
                                //计算其他值
                                if(somlist.size() > 1){
                                    //计量1
                                    BigDecimal b1 = new BigDecimal(importRo.getNum());
                                    BigDecimal b2 = new BigDecimal(somlist.get(1).getConversionRate());
                                    st.setSubPandian1(b1.divide(b2,10,BigDecimal.ROUND_UP));
                                    st.setQuantityUk1(st.getSubPandian1());
                                    st.setCunitidF1(somlist.get(1).getId());
                                }
                                if(somlist.size() > 2){
                                    //计量2
                                    BigDecimal b1 = new BigDecimal(importRo.getNum());
                                    BigDecimal b2 = new BigDecimal(somlist.get(2).getConversionRate());
                                    st.setSubPandian2(b1.divide(b2,10,BigDecimal.ROUND_UP));
                                    st.setQuantityUk2(st.getSubPandian2());
                                    st.setCunitidF2(somlist.get(2).getId());

                                }
                            }else if(importRo.getStockBarcode().equals(v.getStockUnitBarcode1())){
                                if(somlist.size() > 1){
                                    //主计量
                                    BigDecimal b1 = new BigDecimal(importRo.getNum());
                                    BigDecimal b2 = new BigDecimal(somlist.get(1).getConversionRate());
                                    st.setQuantityPd(b1.multiply(b2));
                                    st.setQuantityUk(st.getQuantityPd());

                                    //计量1
                                    st.setSubPandian1(new BigDecimal(importRo.getNum()));
                                    st.setQuantityUk1(st.getSubPandian1());
                                    st.setCunitidF1(somlist.get(1).getId());

                                }

                                if(somlist.size() > 2){
                                    //计量2
                                    BigDecimal b1 = new BigDecimal(importRo.getNum());
                                    BigDecimal b2 = new BigDecimal(somlist.get(2).getConversionRate());
                                    st.setSubPandian2(b1.divide(b2,10,BigDecimal.ROUND_UP));
                                    st.setQuantityUk2(st.getSubPandian2());
                                    st.setCunitidF2(somlist.get(2).getId());
                                }
                            }else if(importRo.getStockBarcode().equals(v.getStockUnitBarcode2())){
                                if(somlist.size() > 1){
                                    //主计量
                                    BigDecimal b1 = new BigDecimal(importRo.getNum());
                                    BigDecimal b2 = new BigDecimal(somlist.get(2).getConversionRate());
                                    st.setQuantityPd(b1.multiply(b2));
                                    st.setQuantityUk(st.getQuantityPd());

                                    //计量1
                                    BigDecimal b3 = new BigDecimal(somlist.get(1).getConversionRate());
                                    st.setSubPandian1(b1.divide(b3,10,BigDecimal.ROUND_UP));
                                    st.setQuantityUk1(st.getSubPandian1());
                                    st.setCunitidF1(somlist.get(1).getId());

                                }

                                if(somlist.size() > 2){
                                    //计量2
                                    st.setSubPandian2(new BigDecimal(importRo.getNum()));
                                    st.setQuantityUk2(st.getSubPandian2());
                                    st.setCunitidF2(somlist.get(2).getId());
                                }
                            }

                        }
                        stList.add(st);
                    });
                    map.put("stockList",stList);
                    map.put("stlist",stlist);
                    return xcl;
                })
                .flatMap(pddlist->{
                    List<StockTakings> stList = (List<StockTakings>) map.get("stlist");
                    return stockTakingsRepository.saveAll(stList).collectList().thenReturn(stList);
                })
                .flatMap(pddlist->{
                    List<StockTakings> stList = (List<StockTakings>) map.get("stockList");
                    return stockTakingsRepository.saveAll(stList).collectList().thenReturn(pddlist);
                })
                .map(o->R.ok().setResult(o));
    }

    @GetMapping("getUnitList")
    public Mono<R> getUnitList(){
        return sysUnitOfMeaRepository.findAll().collectList()
                .map(item->R.ok().setResult(item));
    }

    @GetMapping("/getUnitsList/{mcode}")
    public Mono<R> getUnitsList(@PathVariable String mcode){
        return sysUnitOfMeaListRepository.findAlls().collectList()
                .map(item->R.ok().setResult(item));
    }

    @GetMapping("/findByXyCcode/{ccode}/{year}/{type}")
    public Mono<R> findByXyCcode(@PathVariable String ccode,@PathVariable String year,@PathVariable String type){
        return stockTakingSourceRepository.findAllByCcodeAndIyear(ccode,year)
                .filter(it->StrUtil.isBlank(type)?true:it.getXyBillStyle().equals(type))
                .collectList()
                .map(v-> ObjectUtil.isEmpty(v) ? "0" : v.get(0).getSyccode())
                .map(a->R.ok().setResult(a));
    }
}
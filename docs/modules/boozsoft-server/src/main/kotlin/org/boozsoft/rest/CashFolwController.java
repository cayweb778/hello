package org.boozsoft.rest;

import com.alibaba.fastjson.JSONArray;
import org.boozsoft.domain.entity.CashFlow;
import org.boozsoft.domain.entity.KmCashFlow;
import org.boozsoft.domain.ro.GeneralLedgerRo;
import org.boozsoft.domain.vo.CashFlowVo;
import org.boozsoft.domain.vo.ProjectCashCodeVo;
import org.boozsoft.repo.*;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * 现金流量
 * @author lz
 */
@RestController
@RequestMapping("/cashFolw")
public class CashFolwController {

    @Autowired
    CashFlowRepository cashFlowRepository;
    @Autowired
    KmCashFlowRepository kmCashFlowRepository;
    @Autowired
    AccvoucherRepository accvoucherRepository;
    @Autowired
    ProjectCashRepository projectCashRepository;
    @Autowired
    ProjectCashCodeRepository projectCashCodeRepository;
    //加载现金流量分配信息
    @PostMapping("/getDistributeInfo")
    public Mono<R> getDistributeInfo(@RequestBody HashMap maps) {
        String accId = maps.get("accId").toString();
        String uniqueCode = maps.get("uniqueCode").toString();
        //是否分配
        String cashProject = maps.get("cashProject").toString();
        //key科目 value序号
        Map<String,String> num = (Map<String,String>)maps.get("num");
        List<CashFlowVo>  cf = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        //查询数据
        return cashFlowRepository.findByUniqueCode(uniqueCode, accId).collectList()
                .map(item-> {
                    AtomicReference<Integer> index = new AtomicReference<>(0);
                    //对应序号
                    item.forEach(v -> {
                        if (num.containsKey(v.getCcode())) {
                            v.setDnum(num.get(v.getCcode()));
                        }
                        v.setFx(v.getFx().equals("1")?"流入":"流出");
                        v.setNum(index.getAndSet(index.get() + 1).toString());
                    });
                    map.put("cf", item);
                    return map;
                })
                .flatMap(item -> {
                    return kmCashFlowRepository.findAllByTenantId(accId).collectList();
                })
                .flatMap(item -> {
                    return accvoucherRepository.findByUniqueCodeAndTenantId(uniqueCode, accId).collectList()
                            .map(obj -> {

                                List<GeneralLedgerRo> acc = obj;//凭证信息
                                List<KmCashFlow> list = item;//指定的现金流量科目

                                List<GeneralLedgerRo> xjList = acc.stream().filter(v -> list.stream().filter(o -> o.getCcode().equals(v.getCcode())).findFirst().isPresent() ?  true : false).collect(Collectors.toList());
                                List<GeneralLedgerRo> dfList = acc.stream().filter(v -> !list.stream().filter(o -> o.getCcode().equals(v.getCcode())).findFirst().isPresent() ? true : false).collect(Collectors.toList());

                                //计算现金流量总金额
                                BigDecimal md = xjList.stream()
                                        .map(x -> Objects.isNull(x.getMd()) || x.getMd().length() <= 0 || x.getMd().equals("0.00") ? "0.00" : x.getMd())
                                        .map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                                BigDecimal mc = xjList.stream()
                                        .map(x -> Objects.isNull(x.getMd()) || x.getMd().length() <= 0 || x.getMd().equals("0.00") ? "0.00" : x.getMd())
                                        .map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                                BigDecimal jz = new BigDecimal("0.00");
                                if (md.compareTo(mc) == 1) {
                                    //vo.setFx("借").setScale(2, RoundingMode.HALF_UP);;
                                    jz = md.subtract(mc);
                                } else if (md.compareTo(mc) == -1) {
                                    //vo.setFx("贷");
                                    jz = mc.subtract(md).setScale(2, RoundingMode.HALF_UP);
                                }

                                //查询不到数据 默认添加流量科目的条数
                                List<CashFlowVo> cfList = (List<CashFlowVo>)map.get("cf");
                                AtomicReference<Integer> i = new AtomicReference<>(0);
                                if(cfList.size() == 0){
                                    xjList.forEach(ro->{
                                        CashFlowVo cashFlowVo = new CashFlowVo();
                                        cashFlowVo.setCcode(ro.getCcode());
                                        cashFlowVo.setCodename(ro.getCcodeName());
                                        cashFlowVo.setCdigest(ro.getCdigest());
                                        cashFlowVo.setUniqueCode(ro.getUniqueCode());
                                        if (num.containsKey(ro.getCcode())) {
                                            cashFlowVo.setDnum(num.get(ro.getCcode()));
                                        }
                                        cashFlowVo.setNum(i.getAndSet(i.get() + 1).toString());
                                        cf.add(cashFlowVo);
                                    });
                                    map.put("cf", cf);
                                }
                                //只有现金流量时 对方科目添加
                                if(dfList.size() == 0){
                                    dfList = xjList.stream().distinct().collect(Collectors.toList());
                                }

                                //格式化对方科目数据
                                dfList.stream().forEach(v -> {
                                    v.setValue(v.getCcode());
                                    v.setLabel(v.getCcodeName());
                                });

                                map.put("dkmList", dfList);
                                map.put("ts", xjList.size());
                                map.put("jlr", jz.toString());
                                return map;
                            });

                })
                .map(o -> R.ok().setResult(o));
    }

    //现金流量分配
    @PostMapping("/getFpInfo")
    public Mono<R> getFpInfo(@RequestBody HashMap maps) {
        String accId = maps.get("accId").toString();
        String uniqueCode = maps.get("uniqueCode").toString();
        //key科目 value序号
        Map<String,String> num = (Map<String,String>)maps.get("num");
        List<CashFlowVo>  cf = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        //自动分配
        return kmCashFlowRepository.findAllByTenantId(accId).collectList()
                //现金流量项目对应科目
                .flatMap(item -> {
                    return projectCashRepository.findByTenantId(accId).collectList()
                            .map(obj -> {
                                map.put("pcList", obj);
                                return item;
                            });

                })
                .flatMap(item -> {
                    //现金流量科目list 和 现金流量和项目对照list 查询出要分配的凭证
                    return accvoucherRepository.findByUniqueCodeAndTenantId(uniqueCode, accId).collectList()
                            .map(obj -> {
                                //对应序号
                                obj.forEach(v -> {
                                    if (num.containsKey(v.getCcode())) {
                                        v.setDnum(num.get(v.getCcode()));
                                    }
                                });

                                List<GeneralLedgerRo> acc = obj;//凭证信息
                                List<KmCashFlow> list = item;//指定的现金流量科目

                                List<GeneralLedgerRo> xjList = acc.stream().filter(v -> list.stream().filter(o -> o.getCcode().equals(v.getCcode())).findFirst().isPresent() ?  true: false).collect(Collectors.toList());
                                xjList.forEach(v->v.setIsXj(true));
                                List<GeneralLedgerRo> dfList = acc.stream().filter(v -> !list.stream().filter(o -> o.getCcode().equals(v.getCcode())).findFirst().isPresent() ? true: false).collect(Collectors.toList());

                                List<GeneralLedgerRo> xjListGroup = new ArrayList<>();//合并后现金流量科目
                                //合并相同科目 对科目分组
                                xjList.parallelStream().collect(Collectors.groupingBy(o-> o.getCcode(), Collectors.toList())).forEach((id,v)->{
                                    v.stream().reduce((a,b)-> {
                                        //合并规则 如果是同方向则是+ 不同方向则是- 然后复制给最大的方向
                                        BigDecimal am = new BigDecimal((Objects.nonNull(a.getMd()) && new BigDecimal(a.getMd()).abs().compareTo(new BigDecimal("0.00")) > 0 ? a.getMd():a.getMc()));
                                        BigDecimal bm = new BigDecimal((Objects.nonNull(b.getMd()) && new BigDecimal(b.getMd()).abs().compareTo(new BigDecimal("0.00")) > 0 ? b.getMd():b.getMc()));
                                        //方向
                                        Boolean aFx = Objects.nonNull(a.getMd()) && new BigDecimal(a.getMd()).abs().compareTo(new BigDecimal("0.00")) > 0 ? true:false;
                                        Boolean bFx = Objects.nonNull(a.getMc()) && new BigDecimal(a.getMc()).abs().compareTo(new BigDecimal("0.00")) > 0 ? true:false;
                                        if(am.compareTo(bm) > 0){
                                            a.setMd((aFx.equals(bFx) ? am.add(bm) : am.subtract(bm)).setScale(2, RoundingMode.HALF_UP).toString());
                                        }else if(am.compareTo(bm) < 0){
                                            a.setMc((aFx.equals(bFx) ? am.add(bm) : am.subtract(bm)).setScale(2, RoundingMode.HALF_UP).toString());
                                        }else{
                                            a.setMd((aFx.equals(bFx) ? am.add(bm) : am.subtract(bm)).setScale(2, RoundingMode.HALF_UP).toString());
                                        }
                                        return a;
                                    }).ifPresent(xjListGroup::add);
                                });
                                //现金流量拆分借贷方
                                List<GeneralLedgerRo> jkmList = xjListGroup.stream().filter(v -> (Objects.nonNull(v.getMd()) && (new BigDecimal(v.getMd()).abs().compareTo(new BigDecimal("0.00")) > 0))).collect(Collectors.toList());
                                List<GeneralLedgerRo> dkmList = xjListGroup.stream().filter(v -> (Objects.nonNull(v.getMc()) && (new BigDecimal(v.getMc()).abs().compareTo(new BigDecimal("0.00")) > 0))).collect(Collectors.toList());
                                //判断
                                List<GeneralLedgerRo> fpList = new ArrayList<>();
                                Boolean flgs = false;
                                if(jkmList.size() > 0 && dkmList.size() > 0){//借贷2个方向
                                    flgs = true;
                                    //分出借贷哪边最大
                                    BigDecimal md = jkmList.stream()
                                            .map(x -> Objects.isNull(x.getMd()) || x.getMd().length() <= 0 ? "0.00" : x.getMd())
                                            .map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                                    BigDecimal mc = dkmList.stream()
                                            .map(x -> Objects.isNull(x.getMc()) || x.getMc().length() <= 0 ? "0.00" : x.getMc())
                                            .map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                                    if(md.compareTo(mc) > 0){
                                        fpList.addAll(jkmList);
                                        dfList.addAll(dkmList);

                                        fpList.addAll(dkmList);
                                        dfList.addAll(fpList);
                                    }else if(md.compareTo(mc) < 0){
                                        fpList.addAll(dkmList);
                                        dfList.addAll(jkmList);

                                        fpList.addAll(jkmList);
                                        dfList.addAll(dkmList);
                                    }else{
                                        fpList.addAll(jkmList);
                                        dfList.addAll(dkmList);

                                        fpList.addAll(dkmList);
                                        dfList.addAll(jkmList);
                                    }
                                }else if(jkmList.size() > 0){//借方向
                                    fpList.addAll(jkmList);
                                }else if(dkmList.size() > 0){//贷方向
                                    fpList.addAll(dkmList);
                                }else{//借 贷方 全部抵消
                                     //               j           d
                                     // 例  1001     100         100
                                    //      1002     100         100
                                    List<GeneralLedgerRo> collect = xjListGroup.stream().filter(v -> (Objects.nonNull(v.getMd()) && (new BigDecimal(v.getMd()).abs().compareTo(new BigDecimal("0.00")) == 0))).collect(Collectors.toList());
                                    fpList.addAll(collect);
                                    flgs = true;
                                }
                                //循环递归分配
                                List<ProjectCashCodeVo> pcList = (List<ProjectCashCodeVo>) map.get("pcList");
                                List<GeneralLedgerRo> dfLists = new ArrayList<>();
                                dfLists.addAll(dfList);
                                fpList.forEach(v -> {
                                    fenpei(v, dfList, cf, pcList);
                                });
                                AtomicReference<Integer> i = new AtomicReference<>(0);
                                cf.forEach(v->{
                                    v.setNum(i.getAndSet(i.get() + 1).toString());
                                });
                                map.put("cf", cf);

                                //计算净值
                                BigDecimal md = xjList.stream()
                                        .map(x -> Objects.isNull(x.getMd()) || x.getMd().length() <= 0 || x.getMd().equals("0.00")? "0.00" : x.getMd())
                                        .map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                                BigDecimal mc = xjList.stream()
                                        .map(x -> Objects.isNull(x.getMc()) || x.getMc().length() <= 0 || x.getMc().equals("0.00")? "0.00" : x.getMc())
                                        .map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                                BigDecimal jz = new BigDecimal("0.00");
                                if (md.compareTo(mc) == 1) {
                                    jz = md.subtract(mc);
                                } else if (md.compareTo(mc) == -1) {
                                    jz = mc.subtract(md).setScale(2, RoundingMode.HALF_UP);
                                }
                                map.put("jlr", jz.toString());

                                //格式化对方科目
                                if(flgs){
                                    xjListGroup.stream().forEach(v -> {
                                        v.setValue(v.getCcode());
                                        v.setLabel(v.getCcodeName());
                                    });
                                    map.put("dkmList", xjListGroup);
                                }else{
                                    dfLists.stream().forEach(v -> {
                                        v.setValue(v.getCcode());
                                        v.setLabel(v.getCcodeName());
                                    });
                                    map.put("dkmList", dfLists);
                                }
                                //现金流量数量（不重复）
                                map.put("ts", xjListGroup.size());
                                return map;
                            });

                })
                .map(o -> R.ok().setResult(o));
    }

    //现金流量分配直接保存
    @PostMapping("/saveXjll")
    public Mono<R> saveXjll(@RequestBody HashMap maps) {
        String accId = maps.get("accId").toString();
        String uniqueCode = maps.get("uniqueCode").toString();
        //key科目 value序号
        Map<String,String> num = (Map<String,String>)maps.get("num");
        List<CashFlowVo>  cf = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        //自动分配
        return kmCashFlowRepository.findAllByTenantId(accId).collectList()
                //现金流量项目对应科目
                .flatMap(item -> {
                    return projectCashRepository.findByTenantId(accId).collectList()
                            .map(obj -> {
                                map.put("pcList", obj);
                                return item;
                            });

                })
                .flatMap(item -> {
                    return accvoucherRepository.findByUniqueCodeAndTenantId(uniqueCode, accId).collectList()
                            .map(obj -> {
                                //对应序号
                                obj.forEach(v -> {
                                    if (num.containsKey(v.getCcode())) {
                                        v.setDnum(num.get(v.getCcode()));
                                    }
                                    v.setTenantId(accId);
                                });

                                List<GeneralLedgerRo> acc = obj;//凭证信息
                                List<KmCashFlow> list = item;//指定的现金流量科目

                                List<GeneralLedgerRo> xjList = acc.stream().filter(v -> list.stream().filter(o -> o.getCcode().equals(v.getCcode())).findFirst().isPresent() ? true :false ).collect(Collectors.toList());
                                xjList.forEach(v-> v.setIsXj(true));
                                List<GeneralLedgerRo> dfList = acc.stream().filter(v -> !list.stream().filter(o -> o.getCcode().equals(v.getCcode())).findFirst().isPresent() ? true :false).collect(Collectors.toList());

                                List<GeneralLedgerRo> xjListGroup = new ArrayList<>();//合并后现金流量科目
                                //合并相同科目 对科目分组
                                xjList.parallelStream().collect(Collectors.groupingBy(o-> o.getCcode(), Collectors.toList())).forEach((id,v)->{
                                    v.stream().reduce((a,b)-> {
                                        //合并规则 如果是同方向则是+ 不同方向则是- 然后复制给最大的方向
                                        BigDecimal am = new BigDecimal((Objects.nonNull(a.getMd()) && new BigDecimal(a.getMd()).abs().compareTo(new BigDecimal("0.00")) > 0 ? a.getMd():a.getMc()));
                                        BigDecimal bm = new BigDecimal((Objects.nonNull(b.getMd()) && new BigDecimal(b.getMd()).abs().compareTo(new BigDecimal("0.00")) > 0 ? b.getMd():b.getMc()));
                                        //方向
                                        Boolean aFx = Objects.nonNull(a.getMd()) && new BigDecimal(a.getMd()).abs().compareTo(new BigDecimal("0.00")) > 0 ? true:false;
                                        Boolean bFx = Objects.nonNull(a.getMd()) && new BigDecimal(a.getMd()).abs().compareTo(new BigDecimal("0.00")) > 0 ? true:false;
                                        if(am.compareTo(bm) > 0){
                                            a.setMd((aFx.equals(bFx) ? am.add(bm) : am.subtract(bm)).setScale(2, RoundingMode.HALF_UP).toString());
                                        }else if(am.compareTo(bm) < 0){
                                            a.setMc((aFx.equals(bFx) ? am.add(bm) : am.subtract(bm)).setScale(2, RoundingMode.HALF_UP).toString());
                                        }else{
                                            a.setMd((aFx.equals(bFx) ? am.add(bm) : am.subtract(bm)).setScale(2, RoundingMode.HALF_UP).toString());
                                        }
                                        return a;
                                    }).ifPresent(xjListGroup::add);
                                });
                                //现金流量拆分借贷方
                                List<GeneralLedgerRo> jkmList = xjListGroup.stream().filter(v -> (Objects.nonNull(v.getMd()) && (new BigDecimal(v.getMd()).abs().compareTo(new BigDecimal("0.00")) > 0))).collect(Collectors.toList());
                                List<GeneralLedgerRo> dkmList = xjListGroup.stream().filter(v -> (Objects.nonNull(v.getMc()) && (new BigDecimal(v.getMc()).abs().compareTo(new BigDecimal("0.00")) > 0))).collect(Collectors.toList());
                                //判断
                                List<GeneralLedgerRo> fpList = new ArrayList<>();
                                Boolean flgs = false;
                                if(jkmList.size() > 0 && dkmList.size() > 0){//借贷2个方向
                                    flgs = true;
                                    //分出借贷哪边最大
                                    BigDecimal md = jkmList.stream()
                                            .map(x -> Objects.isNull(x.getMd()) || x.getMd().length() <= 0 ? "0.00" : x.getMd())
                                            .map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                                    BigDecimal mc = dkmList.stream()
                                            .map(x -> Objects.isNull(x.getMc()) || x.getMc().length() <= 0 ? "0.00" : x.getMc())
                                            .map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                                    if(md.compareTo(mc) > 0){
                                        fpList.addAll(jkmList);
                                        dfList.addAll(dkmList);

                                        fpList.addAll(dkmList);
                                        dfList.addAll(fpList);
                                    }else if(md.compareTo(mc) < 0){
                                        fpList.addAll(dkmList);
                                        dfList.addAll(jkmList);

                                        fpList.addAll(jkmList);
                                        dfList.addAll(dkmList);
                                    }else{
                                        fpList.addAll(jkmList);
                                        dfList.addAll(dkmList);

                                        fpList.addAll(dkmList);
                                        dfList.addAll(jkmList);
                                    }
                                }else if(jkmList.size() > 0){//借方向
                                    fpList.addAll(jkmList);
                                }else if(dkmList.size() > 0){//贷方向
                                    fpList.addAll(dkmList);
                                }
                                //循环递归分配
                                List<ProjectCashCodeVo> pcList = (List<ProjectCashCodeVo>) map.get("pcList");
                                fpList.forEach(v -> {
                                    fenpei(v, dfList, cf, pcList);
                                });
                                return cf;
                            });

                })
                .flatMap(item->{
                    List<CashFlow> list =  new ArrayList<>();
                    copyList(item,list, CashFlow.class);
                    //先删除之前数据 在添加  更新凭证状态
                    return cashFlowRepository.deleteByUniqueCode(list.get(0).getUniqueCode())
                            .then(cashFlowRepository.saveAll(list).collectList())
                            .then(accvoucherRepository.updateCashProjectByUniqueCode(list.get(0).getUniqueCode()));
                })
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("/findProjectCash/{accId}")
    public Mono<R> findProjectCash(@PathVariable String accId) {
        return  projectCashRepository.findByFlagAndTenantIdOrderByProjectCode("1", accId).collectList().map(o-> R.ok().setResult(o));
    }

    @PostMapping("/saveFp")
    @Transactional
    public Mono<R> saveFp(@RequestBody HashMap map) {
        String parameteraccuracyJSON=map.get("list").toString();
        List<CashFlow> list=JSONArray.parseArray(parameteraccuracyJSON, CashFlow.class);
        //先删除之前数据 在添加  更新凭证状态
        return cashFlowRepository.deleteByUniqueCode(list.get(0).getUniqueCode())
                .then(cashFlowRepository.saveAll(list).collectList())
                .then(accvoucherRepository.updateCashProjectByUniqueCode(list.get(0).getUniqueCode()))
                .thenReturn(R.ok());
    }

    private void fenpei(GeneralLedgerRo ro, final List<GeneralLedgerRo> list1,final List<CashFlowVo> cf, final List<ProjectCashCodeVo> pcList) {
        BigDecimal x = new BigDecimal((Objects.nonNull(ro.getMd()) && new BigDecimal(ro.getMd()).abs().compareTo(new BigDecimal("0.00")) > 0 ? ro.getMd():ro.getMc()));
        //判断流入流出方向  流入： 借 -贷  流出： -借 贷
        String fx = "借";
        if((Objects.nonNull(ro.getMd()) && (new BigDecimal(ro.getMd()).abs().compareTo(new BigDecimal("0.00")) > 0))){
            fx = "借";
        }else if((Objects.nonNull(ro.getMd()) && (new BigDecimal(ro.getMd()).abs().compareTo(new BigDecimal("0.00")) < 0))){
            fx = "贷";
        }else if((Objects.nonNull(ro.getMc()) && (new BigDecimal(ro.getMc()).abs().compareTo(new BigDecimal("0.00")) > 0))){
            fx = "贷";
        }else if((Objects.nonNull(ro.getMc()) && (new BigDecimal(ro.getMc()).abs().compareTo(new BigDecimal("0.00")) < 0))){
            fx = "借";
        }

        for (int i = 0; i < list1.size(); i++) {
            BigDecimal d = new BigDecimal(Objects.nonNull(list1.get(i).getMd()) && new BigDecimal(list1.get(i).getMd()).abs().compareTo(new BigDecimal("0.00")) > 0  ? list1.get(i).getMd() : list1.get(i).getMc());

            CashFlowVo cashFlowVo = new CashFlowVo();
            cashFlowVo.setDnum(ro.getDnum());
            cashFlowVo.setCcode(ro.getCcode());
            cashFlowVo.setCodename(ro.getCcodeName());
            cashFlowVo.setCdigest(ro.getCdigest());
            cashFlowVo.setDccode(list1.get(i).getCcode());
            cashFlowVo.setDcodename(list1.get(i).getCcodeName());
            cashFlowVo.setIyear(list1.get(i).getIyear());
            cashFlowVo.setIperiod(list1.get(i).getIperiod());
            cashFlowVo.setTenantId(list1.get(i).getTenantId());
            cashFlowVo.setUniqueCode(ro.getUniqueCode());
            //自动关联项目信息 特例:如果都是现金流量 则流量项目是不影响现金流量科目
            ProjectCashCodeVo pv = new ProjectCashCodeVo();
            if("借".equals(fx)){
                if(Objects.nonNull(list1.get(i).getIsXj()) && list1.get(i).getIsXj() == true){
                    List<ProjectCashCodeVo> collect =  pcList.stream().filter(o -> o.getProjectType().equals("06") && o.getFangxiang().equals("1")).collect(Collectors.toList());
                    if(collect.size()>0){
                        pv = collect.get(0);
                    }
                }else{
                    List<ProjectCashCodeVo> collect =  pcList.stream().filter(o-> Objects.nonNull(o.getJcode()) && o.getJcode().equals(ro.getCcode())).collect(Collectors.toList());
                    if(collect.size()>0){
                        pv = collect.get(0);
                    }
                }
            }else{
                if(Objects.nonNull(list1.get(i).getIsXj()) && list1.get(i).getIsXj() == true){
                    List<ProjectCashCodeVo> collect = pcList.stream().filter(o ->  o.getProjectType().equals("06") && o.getFangxiang().equals("0")).collect(Collectors.toList());
                    if(collect.size()>0){
                        pv = collect.get(0);
                    }
                }else{
                    List<ProjectCashCodeVo> collect = pcList.stream().filter(o -> Objects.nonNull(o.getDcode()) && o.getDcode().equals(ro.getCcode())).collect(Collectors.toList());
                    if(collect.size()>0){
                        pv = collect.get(0);
                    }
                }
            }
            cashFlowVo.setProjectCode(pv.getProjectCode());
            cashFlowVo.setProjectname(pv.getProjectName());
            cashFlowVo.setFx(Objects.isNull(pv.getFangxiang()) ? "": pv.getFangxiang().equals("1")?"流入":"流出");
            cashFlowVo.setProjectnum(pv.getProjectCode());

            if(x.abs().compareTo(d.abs()) == 1){//大于 减去值 继续分配
                //list1 -1
                cashFlowVo.setMoney(d.toString());
                cf.add(cashFlowVo);
                fenpei(ro, list1.subList(1,list1.size()), cf, pcList);
                return;
            }else if(x.abs().compareTo(d.abs()) == -1){//小于 减去值 继续分配
                //改变list1的值  = Integer.parseInt(v.toString())-list
                //add
                if("1".equals(list1.get(i).getBprogerty())){
                    list1.get(i).setMd(d.subtract(x).toString());
                }else{
                    list1.get(i).setMc(d.subtract(x).toString());
                }
                cashFlowVo.setMoney(x.toString());
                cf.add(cashFlowVo);
                return;
            }else{//相同 过滤第一个
                //add
                if("1".equals(list1.get(i).getBprogerty())){
                    list1.get(i).setMd(d.subtract(x).toString());
                }else{
                    list1.get(i).setMc(d.subtract(x).toString());
                }
                cashFlowVo.setMoney(d.toString());
                cf.add(cashFlowVo);
                list1.remove(list1.get(i));
                return;
            }
        }
        //只有现金流量科目时
        if(list1.size() == 0){
            CashFlowVo cashFlowVo = new CashFlowVo();
            cashFlowVo.setDnum(ro.getDnum());
            cashFlowVo.setCcode(ro.getCcode());
            cashFlowVo.setCodename(ro.getCcodeName());
            cashFlowVo.setCdigest(ro.getCdigest());
            cashFlowVo.setDccode(ro.getCcode());
            cashFlowVo.setDcodename(ro.getCcodeName());
            cashFlowVo.setIyear(ro.getIyear());
            cashFlowVo.setIperiod(ro.getIperiod());
            cashFlowVo.setTenantId(ro.getTenantId());
            cashFlowVo.setUniqueCode(ro.getUniqueCode());
            List<ProjectCashCodeVo> collect =  pcList.stream().filter(o -> o.getProjectType().equals("06") && o.getFangxiang().equals("1")).collect(Collectors.toList());
            ProjectCashCodeVo pv = new ProjectCashCodeVo();
            if(collect.size()>0){
                pv = collect.get(0);
                cashFlowVo.setProjectCode(pv.getProjectCode());
                cashFlowVo.setProjectname(pv.getProjectName());
                cashFlowVo.setFx(Objects.isNull(pv.getFangxiang()) ? "": pv.getFangxiang().equals("1")?"流入":"流出");
                cashFlowVo.setProjectnum(pv.getProjectCode());
            }
            cashFlowVo.setMoney(ro.getMd());
            cf.add(cashFlowVo);
        }

    }

    public void copyList(Object obj, List<CashFlow> list2, Class<CashFlow> classObj) {
        if ((!Objects.isNull(obj)) && (!Objects.isNull(list2))) {
            List list1 = (List) obj;
            list1.forEach(item -> {
                try {
                    CashFlow data = classObj.newInstance();
                    BeanUtils.copyProperties(item, data);
                    list2.add(data);
                } catch (InstantiationException e) {
                } catch (IllegalAccessException e) {
                }
            });
        }
    }
}

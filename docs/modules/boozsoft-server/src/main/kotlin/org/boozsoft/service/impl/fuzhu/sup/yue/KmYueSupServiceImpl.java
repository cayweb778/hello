package org.boozsoft.service.impl.fuzhu.sup.yue;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.boozsoft.domain.entity.Supplier;
import org.boozsoft.domain.entity.codekemu.CodeKemu;
import org.boozsoft.domain.vo.*;
import org.boozsoft.repo.AccvoucherRepository;
import org.boozsoft.repo.CustomerRepository;
import org.boozsoft.repo.SupplierRepository;
import org.boozsoft.repo.accstyle.AccStyleRepository;
import org.boozsoft.repo.codekemu.CodeKemuRepository;
import org.boozsoft.service.impl.SubjectInitialFuZhuBalanceServiceImpl;
import org.boozsoft.util.BigDecimalUtils;
import org.boozsoft.utils.CollectOfUtils;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;


@Slf4j
@Service
public class KmYueSupServiceImpl {

    @Autowired
    SubjectInitialFuZhuBalanceServiceImpl fuZhuBalanceService;
    @Autowired
    AccvoucherRepository accvoucherRepository;
    @Autowired
    CodeKemuRepository codeKemuRepository;
    @Autowired
    AccStyleRepository accStyleRepository;
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    SupplierRepository supplierRepository;


    public Mono<R> findAll(Pageable pageable,String minDept, String km, String strDate, String endDate, String bz, String ishaveRjz, String styleValue, Map<String, String> searchMap, Map<String, String> filterMap,String database) {
        String year = strDate.substring(0, 4);
        String month = strDate.substring(4, 6);
        AtomicReference<Long> totalAR = new AtomicReference(0);
        // 获取 已发生凭证的供应商辅助核算
        return supplierRepository.findAll().collectList()
        .flatMap(pzsuplist->{
            List<Supplier> newpzsuplist=pzsuplist;
            if (StringUtils.isNotBlank(minDept)) {
                List<String> strings = Arrays.asList(minDept.split(","));
                newpzsuplist = pzsuplist.stream().filter(vo -> strings.indexOf(vo.getUniqueCode())>-1 ).collect(Collectors.toList());
            }
            List<Supplier> finalNewpzsuplist = newpzsuplist;
            // 获取 已发生供应商辅助核算的凭证
            return accvoucherRepository.findAllByAccVoucherSup(year,km,year+"01",endDate).collectList()
                    .flatMap(accvoucherlist->{
                        List<DeptCodeAccvoucherVo> newaccvoucherlist = accvoucherlist;
                        // 根据币种查询凭证
                        if (!"全部".equals(bz)) {
                            newaccvoucherlist = accvoucherlist.stream().filter(a -> a.getForeignCurrency().equals(bz)).collect(Collectors.toList());
                        }
                        // ****** 是否包含未记账【查询条件】 *****不包含；只获取 已记账的凭证
                        if (ishaveRjz.equals("0")) {
                            newaccvoucherlist = accvoucherlist.stream().filter(vo -> StringUtils.isNotBlank(vo.getIbook()) && vo.getIbook().equals("1")).collect(Collectors.toList());
                        }

                        List<DeptCodeAccvoucherVo> finalNewaccvoucherlist = newaccvoucherlist;
                        return fuZhuBalanceService.findAllSubjectInitialFuZhuBalance(year, "false", year + "21", database)
                            .flatMap(qclist -> {
                                Map qcmap = (Map) qclist.getResult();
                                // 获取科目详细信息
                                return codeKemuRepository.findByIyearOrderByCcode(year).collectList()
                                    .flatMap(code->{
                                        List<CodeKemu> newdeptCodeList=code.stream().filter(d->d.getCcode().equals(km)).collect(Collectors.toList());
                                        return Mono.just(
                                                CollectOfUtils.mapof("qclist", qcmap.get("tablesData"), "pzlist", finalNewaccvoucherlist, "pzsuplist", finalNewpzsuplist,"code",newdeptCodeList.get(0))
                                        );
                                    });
                        });
                    });
        })
        .flatMap(map->{
            ArrayList<KeMuBalanceVo> mapList = new ArrayList<>();
            CodeKemu code = (CodeKemu) map.get("code");
            List<Supplier> pzsuplist = (List<Supplier>) map.get("pzsuplist");
            List<DeptCodeAccvoucherVo> pzlist = (List<DeptCodeAccvoucherVo>) map.get("pzlist");
            List<SubjectInitialBalanceVo> qclist = (List<SubjectInitialBalanceVo>) map.get("qclist");

            pzsuplist.stream().forEach(sup->{
                KeMuBalanceVo vo = new KeMuBalanceVo();
                String bprogerty = code.getBprogerty();  // 方向
                // ************* 获取此科目下的客户期初 *************
                List<SubjectInitialBalanceVo> findByCodeQcList = qclist.stream().filter(qc -> StrUtil.isNotBlank(qc.getCsupId()) && qc.getCcode().equals(km)&&qc.getCsupId().equals(sup.getUniqueCode())).collect(Collectors.toList());
                BigDecimal qcMd = findByCodeQcList.size() > 0 ? findByCodeQcList.get(0).getMd() : new BigDecimal(0);
                BigDecimal qcMc = findByCodeQcList.size() > 0 ? findByCodeQcList.get(0).getMc() : new BigDecimal(0);
                // 数量
                BigDecimal qcNdS = findByCodeQcList.size() > 0 ? findByCodeQcList.get(0).getNdS() : new BigDecimal(0);
                BigDecimal qcNcS = findByCodeQcList.size() > 0 ? findByCodeQcList.get(0).getNcS() : new BigDecimal(0);
                BigDecimal qcNum = new BigDecimal(0);
                // 外币金额
                BigDecimal qcNfratMd = findByCodeQcList.size() > 0 ? findByCodeQcList.get(0).getNfratMd() : new BigDecimal(0);
                BigDecimal qcNfratMc = findByCodeQcList.size() > 0 ? findByCodeQcList.get(0).getNfratMc() : new BigDecimal(0);
                BigDecimal qcNfrat = new BigDecimal(0);

                // 期间 不是从一月开始的 计算出上月的期末余额
                if (Integer.valueOf(strDate.substring(4, 6)) > 1) {
                    String lastmonth = Integer.valueOf(month) - 1 < 10 ? "0" + (Integer.valueOf(month) - 1) : month;
                    List<DeptCodeAccvoucherVo> lastpz = pzlist.stream().filter(last -> Integer.valueOf(last.getImonth()) <= Integer.valueOf(lastmonth) && last.getCcode().equals(code.getCcode())&&last.getCsupId().equals(sup.getUniqueCode())).collect(Collectors.toList());
                    BigDecimal lastMd = lastpz.size() > 0 ? lastpz.stream().map(DeptCodeAccvoucherVo::getMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum) : new BigDecimal(0);
                    BigDecimal lastMc = lastpz.size() > 0 ? lastpz.stream().map(DeptCodeAccvoucherVo::getMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum) : new BigDecimal(0);
                    BigDecimal lastNdS = lastpz.size() > 0 ? lastpz.stream().map(DeptCodeAccvoucherVo::getNdS).reduce(BigDecimal.ZERO, BigDecimalUtils::sum) : new BigDecimal(0);
                    BigDecimal lastNcS = lastpz.size() > 0 ? lastpz.stream().map(DeptCodeAccvoucherVo::getNcS).reduce(BigDecimal.ZERO, BigDecimalUtils::sum) : new BigDecimal(0);
                    BigDecimal lastNfratMd = lastpz.size() > 0 ? lastpz.stream().map(DeptCodeAccvoucherVo::getNfratMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum) : new BigDecimal(0);
                    BigDecimal lastNfratMc = lastpz.size() > 0 ? lastpz.stream().map(DeptCodeAccvoucherVo::getNfratMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum) : new BigDecimal(0);
                    // 根据方向进行计算 借方【期初借+凭证借-期初贷+凭证贷】 贷方【期初贷+凭证贷-期初借+凭证借】
                    if(bprogerty.equals("1")){  // 借
                        qcNum=qcNdS.add(lastNdS).subtract(qcNcS.add(lastNcS));
                        qcNfrat=qcNfratMd.add(lastNfratMd).subtract(qcNfratMc.add(lastNfratMc));
                        BigDecimal yue=qcMd.add(lastMd).subtract(qcMc.add(lastMc));
                        if(yue.compareTo(BigDecimal.ZERO) >= 0){
                            qcMc=new BigDecimal(0);
                            qcMd=yue;
                        }else{
                            qcMd=new BigDecimal(0);
                            qcMc=yue.multiply(new BigDecimal(-1));
                        }
                    }
                    else{                       // 贷
                        qcNum=qcNcS.add(lastNcS).subtract(qcNdS.add(lastNdS));
                        qcNfrat=qcNfratMc.add(lastNfratMc).subtract(qcNfratMd.add(lastNfratMd));
                        BigDecimal yue=qcMc.add(lastMc).subtract(qcMd.add(lastMd));
                        if(yue.compareTo(BigDecimal.ZERO) >= 0){
                            qcMd=new BigDecimal(0);
                            qcMc=yue;
                        }else{
                            qcMc=new BigDecimal(0);
                            qcMd=yue.multiply(new BigDecimal(-1));
                        }
                    }
                }

                // 检索科目获取凭证
                List<DeptCodeAccvoucherVo> newpzlist = pzlist.stream().filter(pz -> Integer.valueOf(pz.getIyperiod()) >= Integer.valueOf(strDate) && Integer.valueOf(pz.getIyperiod()) <= Integer.valueOf(endDate)&& pz.getCcode().equals(code.getCcode())&&pz.getCsupId().equals(sup.getUniqueCode())).collect(Collectors.toList());
                BigDecimal pzMd =newpzlist.size()>0?newpzlist.stream().map(DeptCodeAccvoucherVo::getMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                BigDecimal pzMc =newpzlist.size()>0?newpzlist.stream().map(DeptCodeAccvoucherVo::getMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                BigDecimal pzNfratMd =newpzlist.size()>0?newpzlist.stream().map(DeptCodeAccvoucherVo::getNfratMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                BigDecimal pzNfratMc =newpzlist.size()>0?newpzlist.stream().map(DeptCodeAccvoucherVo::getNfratMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                BigDecimal pzNdS=newpzlist.size()>0?newpzlist.stream().map(DeptCodeAccvoucherVo::getNdS).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                BigDecimal pzNcS=newpzlist.size()>0?newpzlist.stream().map(DeptCodeAccvoucherVo::getNcS).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                BigDecimal pzNum=new BigDecimal(0);
                BigDecimal pzNfrat=new BigDecimal(0);

                // 获取累计发生 【月份：<= endDate】
                List<DeptCodeAccvoucherVo> pzljlist = pzlist.stream().filter(pz -> Integer.valueOf(pz.getIyperiod()) >= Integer.valueOf(strDate) && Integer.valueOf(pz.getIyperiod()) <= Integer.valueOf(endDate)&& pz.getCcode().equals(code.getCcode())&&pz.getCsupId().equals(sup.getUniqueCode())).collect(Collectors.toList());
                BigDecimal pzLjMd =pzljlist.size()>0?pzljlist.stream().map(DeptCodeAccvoucherVo::getMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                BigDecimal pzLjMc =pzljlist.size()>0?pzljlist.stream().map(DeptCodeAccvoucherVo::getMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                BigDecimal pzLjNfratMd =pzljlist.size()>0?pzljlist.stream().map(DeptCodeAccvoucherVo::getNfratMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                BigDecimal pzLjNfratMc =pzljlist.size()>0?pzljlist.stream().map(DeptCodeAccvoucherVo::getNfratMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                BigDecimal pzLjNdS =pzljlist.size()>0?pzljlist.stream().map(DeptCodeAccvoucherVo::getNdS).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                BigDecimal pzLjNcS =pzljlist.size()>0?pzljlist.stream().map(DeptCodeAccvoucherVo::getNcS).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                BigDecimal pzLjNum=new BigDecimal(0);
                BigDecimal pzLjNfrat=new BigDecimal(0);

                BigDecimal qmMd=new BigDecimal(0);
                BigDecimal qmMc=new BigDecimal(0);
                BigDecimal qmNum=new BigDecimal(0);
                BigDecimal qmNfrat=new BigDecimal(0);

                // 根据方向进行计算 借方【期初借+凭证借-期初贷+凭证贷】 贷方【期初贷+凭证贷-期初借+凭证借】
                if(bprogerty.equals("1")){  // 借
                    pzNum=pzNdS.subtract(pzNcS);
                    pzLjNum=pzLjNdS.subtract(pzLjNcS);
                    pzNfrat=pzNfratMd.subtract(pzNfratMc);
                    pzLjNfrat=pzLjNfratMd.subtract(pzLjNfratMc);
                    qmNum=qcNum.add(pzNdS.subtract(pzNcS));
                    qmNfrat=qcNfrat.add(pzNfratMd.subtract(pzNfratMc));

                    BigDecimal yue=qcMd.add(pzMd).subtract(qcMc.add(pzMc));
                    if(yue.compareTo(BigDecimal.ZERO) >= 0){
                        qmMc=new BigDecimal(0);
                        qmMd=yue;
                    }else{
                        qmMd=new BigDecimal(0);
                        qmMc=yue.multiply(new BigDecimal(-1));
                    }
                }
                else{
                    pzNum=pzNcS.subtract(pzNdS);
                    qmNum=qcNum.add(pzNcS.subtract(pzNdS));
                    pzLjNum=pzLjNcS.subtract(pzLjNdS);
                    pzNfrat=pzNfratMc.subtract(pzNfratMd);
                    pzLjNfrat=pzLjNfratMc.subtract(pzLjNfratMd);
                    qmNfrat=qcNfrat.add(pzNfratMc.subtract(pzNfratMd));
                    BigDecimal yue=qcMc.add(pzMc).subtract(qcMd.add(pzMd));
                    if(yue.compareTo(BigDecimal.ZERO) >= 0){
                        qmMc=yue;
                        qmMd=new BigDecimal(0);
                    }else{
                        qmMc=new BigDecimal(0);
                        qmMd=yue.multiply(new BigDecimal(-1));
                    }
                }
                if(qcMd.compareTo(BigDecimal.ZERO) == 0&&qcMc.compareTo(BigDecimal.ZERO) == 0 &&pzMd.compareTo(BigDecimal.ZERO) == 0&&pzMc.compareTo(BigDecimal.ZERO) == 0
                        &&pzLjMd.compareTo(BigDecimal.ZERO) == 0&&pzLjMc.compareTo(BigDecimal.ZERO) == 0
                ){

                }else{
                    if(qcMd.compareTo(BigDecimal.ZERO) < 0){
                        qcMc=qcMd.multiply(new BigDecimal(-1));
                        qcMd=new BigDecimal(0);
                    }
                    if(qcMc.compareTo(BigDecimal.ZERO) < 0){
                        qcMd=qcMc.multiply(new BigDecimal(-1));
                        qcMc=new BigDecimal(0);
                    }
                    vo.setUniqueCode(sup.getUniqueCode()).setCcode(sup.getCustCode()).setCcodeName(sup.getCustName())
                            .setCclass(code.getCclass()).setFindByBend(code.getBend()).setUnitMeasurement(code.getMenterage())
                            .setForeignCurrency(code.getCurrencyType())
                            .setQcMd(qcMd).setQcMc(qcMc).setQcNum(qcNum).setQcNfrat(qcNfrat)
                            .setPzMd(pzMd).setPzMc(pzMc).setPzNum(pzNum).setPzNfrat(pzNfrat)
                            .setLjMd(pzLjMd).setLjMc(pzLjMc).setLjNum(pzLjNum).setLjNfrat(pzLjNfrat)
                            .setQmMd(qmMd).setQmMc(qmMc).setQmNum(qmNum).setQmNfrat(qmNfrat);
                    mapList.add(vo);
                }
            });
            totalAR.set((long) mapList.size());
            return Mono.just(mapList.stream().filter(item->{
                // 按条件过滤
                if (StringUtils.isNotBlank(searchMap.get("requirement")) && org.springbooz.core.tool.utils.StringUtils.isNotBlank(searchMap.get("value"))) {
                    String value = searchMap.get("value");
                    String dbValue = getFieldValueByFieldName(searchMap.get("requirement").trim(), item);
                    if (Objects.nonNull(dbValue) && !dbValue.contains(value)) {
                        return false;
                    }
                }
                // 过滤漏斗-----------------期初借方
                if (StringUtils.isNotBlank(filterMap.get("amountMinQcMd")) && org.springbooz.core.tool.utils.StringUtils.isNotBlank(filterMap.get("amountMaxQcMd"))) {
                    BigDecimal min = new BigDecimal(filterMap.get("amountMinQcMd"));
                    BigDecimal max = new BigDecimal(filterMap.get("amountMaxQcMd"));
                    BigDecimal s_md = new BigDecimal(item.getQcMd().toString().replaceAll(",",""));
                    if (s_md.compareTo(min) < 0 || s_md.compareTo(max) > 0) {
                        return false;
                    }
                }
                // 过滤漏斗-----------------期初贷方
                if (StringUtils.isNotBlank(filterMap.get("amountMinQcMc")) && org.springbooz.core.tool.utils.StringUtils.isNotBlank(filterMap.get("amountMaxQcMc"))) {
                    BigDecimal min = new BigDecimal(filterMap.get("amountMinQcMc"));
                    BigDecimal max = new BigDecimal(filterMap.get("amountMaxQcMc"));
                    BigDecimal s_md = new BigDecimal(item.getQcMc().toString().replaceAll(",",""));
                    if (s_md.compareTo(min) < 0 || s_md.compareTo(max) > 0) {
                        return false;
                    }
                }

                // 过滤漏斗-----------------凭证借方
                if (StringUtils.isNotBlank(filterMap.get("amountMinPzMd")) && org.springbooz.core.tool.utils.StringUtils.isNotBlank(filterMap.get("amountMaxPzMd"))) {
                    BigDecimal min = new BigDecimal(filterMap.get("amountMinPzMd"));
                    BigDecimal max = new BigDecimal(filterMap.get("amountMaxPzMd"));
                    BigDecimal s_md = new BigDecimal(item.getPzMd().toString().replaceAll(",",""));
                    if (s_md.compareTo(min) < 0 || s_md.compareTo(max) > 0) {
                        return false;
                    }
                }
                // 过滤漏斗-----------------凭证dai方
                if (StringUtils.isNotBlank(filterMap.get("amountMinPzMc")) && org.springbooz.core.tool.utils.StringUtils.isNotBlank(filterMap.get("amountMaxPzMc"))) {
                    BigDecimal min = new BigDecimal(filterMap.get("amountMinPzMc"));
                    BigDecimal max = new BigDecimal(filterMap.get("amountMaxPzMc"));
                    BigDecimal s_md = new BigDecimal(item.getPzMc().toString().replaceAll(",",""));
                    if (s_md.compareTo(min) < 0 || s_md.compareTo(max) > 0) {
                        return false;
                    }
                }
                // 过滤漏斗-----------------期末借方
                if (StringUtils.isNotBlank(filterMap.get("amountMinQmMd")) && org.springbooz.core.tool.utils.StringUtils.isNotBlank(filterMap.get("amountMaxQmMd"))) {
                    BigDecimal min = new BigDecimal(filterMap.get("amountMinQmMd"));
                    BigDecimal max = new BigDecimal(filterMap.get("amountMaxQmMd"));
                    BigDecimal s_md = new BigDecimal(item.getQmMd().toString().replaceAll(",",""));
                    if (s_md.compareTo(min) < 0 || s_md.compareTo(max) > 0) {
                        return false;
                    }
                }
                // 过滤漏斗-----------------期末dai方
                if (StringUtils.isNotBlank(filterMap.get("amountMinQmMc")) && org.springbooz.core.tool.utils.StringUtils.isNotBlank(filterMap.get("amountMaxQmMc"))) {
                    BigDecimal min = new BigDecimal(filterMap.get("amountMinQmMc"));
                    BigDecimal max = new BigDecimal(filterMap.get("amountMaxQmMc"));
                    BigDecimal s_md = new BigDecimal(item.getQmMc().toString().replaceAll(",",""));
                    if (s_md.compareTo(min) < 0 || s_md.compareTo(max) > 0) {
                        return false;
                    }
                }
                return true;
            }));
        })
        .map(a -> R.page(a.collect(Collectors.toList()),pageable,(totalAR.get())));
    }

    private String getFieldValueByFieldName(String fieldName, Object object) {
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + firstLetter + fieldName.substring(1);
            Method method = object.getClass().getMethod(getter, new Class[]{});
            Object value = method.invoke(object, new Object[]{});
            return value.toString();
        } catch (Exception e) {
            return null;
        }
    }

    public Mono<R> exportAll(String strDate,String endDate,String bz,String ishaveRjz,String styleValue,List<String> ccodeList) {
        String year = strDate.substring(0, 4);
        String month = strDate.substring(4, 6);
        // 封装list
        List<Map<String,Object>> excellist = new ArrayList<>();
        // 获取 已发生凭证的供应商辅助核算
        return supplierRepository.findAllOrderBySupCode2().collectList()
            .flatMap(pzsuplist->{
                List<CustomerVo> finalNewpzsuplist = pzsuplist;
                // 获取 已发生供应商辅助核算的凭证
                return accvoucherRepository.findAllByIyearFuzhuAccvoucher(year,year+"01",endDate).collectList()
                    .flatMap(accvoucherlist->{
                            List<DeptCodeAccvoucherVo> newaccvoucherlist = accvoucherlist;
                            // 根据币种查询凭证
                            if (!"全部".equals(bz)) {
                                newaccvoucherlist = accvoucherlist.stream().filter(a -> a.getForeignCurrency().equals(bz)).collect(Collectors.toList());
                            }
                            // ****** 是否包含未记账【查询条件】 *****不包含；只获取 已记账的凭证
                            if (ishaveRjz.equals("0")) {
                                newaccvoucherlist = accvoucherlist.stream().filter(vo -> StringUtils.isNotBlank(vo.getIbook()) && vo.getIbook().equals("1")).collect(Collectors.toList());
                            }

                            List<DeptCodeAccvoucherVo> finalNewaccvoucherlist = newaccvoucherlist;
                            return fuZhuBalanceService.findAllSubjectInitialFuZhuBalance(year, "false", year + "21", "")
                                    .flatMap(qclist -> {
                                        Map qcmap = (Map) qclist.getResult();
                                        // 获取科目详细信息
                                        return codeKemuRepository.findByIyearOrderByCcode(year).collectList()
                                                .flatMap(codelist->{
                                                    return Mono.just(
                                                            CollectOfUtils.mapof("qclist", qcmap.get("tablesData"), "pzlist", finalNewaccvoucherlist, "pzsuplist", finalNewpzsuplist,"codelist",codelist)
                                                    );
                                                });
                                    });
                        });
            })
            .flatMap(map->{
                List<CodeKemu> codelist = (List<CodeKemu>) map.get("codelist");
                List<CustomerVo> pzsuplist = (List<CustomerVo>) map.get("pzsuplist");
                List<DeptCodeAccvoucherVo> pzlist = (List<DeptCodeAccvoucherVo>) map.get("pzlist");
                List<SubjectInitialBalanceVo> qclist = (List<SubjectInitialBalanceVo>) map.get("qclist");

                ccodeList.stream().forEach(ck->{
                    ArrayList<KeMuBalanceVo> mapList = new ArrayList<>();

                    List<CodeKemu> collect = codelist.stream().filter(v -> v.getCcode().equals(ck)).collect(Collectors.toList());
                    CodeKemu code=collect.get(0);

                    pzsuplist.stream().filter(v->v.getCcode().equals(ck)).forEach(sup->{
                        KeMuBalanceVo vo = new KeMuBalanceVo();
                        String bprogerty = code.getBprogerty();  // 方向
                        // ************* 获取此科目下的客户期初 *************
                        List<SubjectInitialBalanceVo> findByCodeQcList = qclist.stream().filter(qc -> StrUtil.isNotBlank(qc.getCsupId()) && qc.getCcode().equals(ck)&&qc.getCsupId().equals(sup.getUniqueCode())).collect(Collectors.toList());
                        BigDecimal qcMd = findByCodeQcList.size() > 0 ? findByCodeQcList.get(0).getMd() : new BigDecimal(0);
                        BigDecimal qcMc = findByCodeQcList.size() > 0 ? findByCodeQcList.get(0).getMc() : new BigDecimal(0);
                        // 数量
                        BigDecimal qcNdS = findByCodeQcList.size() > 0 ? findByCodeQcList.get(0).getNdS() : new BigDecimal(0);
                        BigDecimal qcNcS = findByCodeQcList.size() > 0 ? findByCodeQcList.get(0).getNcS() : new BigDecimal(0);
                        BigDecimal qcNum = new BigDecimal(0);
                        // 外币金额
                        BigDecimal qcNfratMd = findByCodeQcList.size() > 0 ? findByCodeQcList.get(0).getNfratMd() : new BigDecimal(0);
                        BigDecimal qcNfratMc = findByCodeQcList.size() > 0 ? findByCodeQcList.get(0).getNfratMc() : new BigDecimal(0);
                        BigDecimal qcNfrat = new BigDecimal(0);

                        // 期间 不是从一月开始的 计算出上月的期末余额
                        if (Integer.valueOf(strDate.substring(4, 6)) > 1) {
                            String lastmonth = Integer.valueOf(month) - 1 < 10 ? "0" + (Integer.valueOf(month) - 1) : month;
                            List<DeptCodeAccvoucherVo> lastpz = pzlist.stream().filter(last -> Integer.valueOf(last.getImonth()) <= Integer.valueOf(lastmonth) && last.getCcode().equals(code.getCcode())&&last.getCsupId().equals(sup.getUniqueCode())).collect(Collectors.toList());
                            BigDecimal lastMd = lastpz.size() > 0 ? lastpz.stream().map(DeptCodeAccvoucherVo::getMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum) : new BigDecimal(0);
                            BigDecimal lastMc = lastpz.size() > 0 ? lastpz.stream().map(DeptCodeAccvoucherVo::getMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum) : new BigDecimal(0);
                            BigDecimal lastNdS = lastpz.size() > 0 ? lastpz.stream().map(DeptCodeAccvoucherVo::getNdS).reduce(BigDecimal.ZERO, BigDecimalUtils::sum) : new BigDecimal(0);
                            BigDecimal lastNcS = lastpz.size() > 0 ? lastpz.stream().map(DeptCodeAccvoucherVo::getNcS).reduce(BigDecimal.ZERO, BigDecimalUtils::sum) : new BigDecimal(0);
                            BigDecimal lastNfratMd = lastpz.size() > 0 ? lastpz.stream().map(DeptCodeAccvoucherVo::getNfratMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum) : new BigDecimal(0);
                            BigDecimal lastNfratMc = lastpz.size() > 0 ? lastpz.stream().map(DeptCodeAccvoucherVo::getNfratMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum) : new BigDecimal(0);
                            // 根据方向进行计算 借方【期初借+凭证借-期初贷+凭证贷】 贷方【期初贷+凭证贷-期初借+凭证借】
                            if(bprogerty.equals("1")){  // 借
                                qcNum=qcNdS.add(lastNdS).subtract(qcNcS.add(lastNcS));
                                qcNfrat=qcNfratMd.add(lastNfratMd).subtract(qcNfratMc.add(lastNfratMc));
                                BigDecimal yue=qcMd.add(lastMd).subtract(qcMc.add(lastMc));
                                if(yue.compareTo(BigDecimal.ZERO) >= 0){
                                    qcMc=new BigDecimal(0);
                                    qcMd=yue;
                                }else{
                                    qcMd=new BigDecimal(0);
                                    qcMc=yue.multiply(new BigDecimal(-1));
                                }
                            }
                            else{                       // 贷
                                qcNum=qcNcS.add(lastNcS).subtract(qcNdS.add(lastNdS));
                                qcNfrat=qcNfratMc.add(lastNfratMc).subtract(qcNfratMd.add(lastNfratMd));
                                BigDecimal yue=qcMc.add(lastMc).subtract(qcMd.add(lastMd));
                                if(yue.compareTo(BigDecimal.ZERO) >= 0){
                                    qcMd=new BigDecimal(0);
                                    qcMc=yue;
                                }else{
                                    qcMc=new BigDecimal(0);
                                    qcMd=yue.multiply(new BigDecimal(-1));
                                }
                            }
                        }

                        // 检索科目获取凭证
                        List<DeptCodeAccvoucherVo> newpzlist = pzlist.stream().filter(pz ->StrUtil.isNotBlank(pz.getCsupId()) &&Integer.valueOf(pz.getIyperiod()) >= Integer.valueOf(strDate) && Integer.valueOf(pz.getIyperiod()) <= Integer.valueOf(endDate)&& pz.getCcode().equals(code.getCcode())&&pz.getCsupId().equals(sup.getUniqueCode())).collect(Collectors.toList());
                        BigDecimal pzMd =newpzlist.size()>0?newpzlist.stream().map(DeptCodeAccvoucherVo::getMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                        BigDecimal pzMc =newpzlist.size()>0?newpzlist.stream().map(DeptCodeAccvoucherVo::getMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                        BigDecimal pzNfratMd =newpzlist.size()>0?newpzlist.stream().map(DeptCodeAccvoucherVo::getNfratMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                        BigDecimal pzNfratMc =newpzlist.size()>0?newpzlist.stream().map(DeptCodeAccvoucherVo::getNfratMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                        BigDecimal pzNdS=newpzlist.size()>0?newpzlist.stream().map(DeptCodeAccvoucherVo::getNdS).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                        BigDecimal pzNcS=newpzlist.size()>0?newpzlist.stream().map(DeptCodeAccvoucherVo::getNcS).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                        BigDecimal pzNum=new BigDecimal(0);
                        BigDecimal pzNfrat=new BigDecimal(0);

                        // 获取累计发生 【月份：<= endDate】
                        List<DeptCodeAccvoucherVo> pzljlist = pzlist.stream().filter(pz ->StrUtil.isNotBlank(pz.getCsupId()) && Integer.valueOf(pz.getIyperiod()) >= Integer.valueOf(strDate) && Integer.valueOf(pz.getIyperiod()) <= Integer.valueOf(endDate)&& pz.getCcode().equals(code.getCcode())&&pz.getCsupId().equals(sup.getUniqueCode())).collect(Collectors.toList());
                        BigDecimal pzLjMd =pzljlist.size()>0?pzljlist.stream().map(DeptCodeAccvoucherVo::getMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                        BigDecimal pzLjMc =pzljlist.size()>0?pzljlist.stream().map(DeptCodeAccvoucherVo::getMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                        BigDecimal pzLjNfratMd =pzljlist.size()>0?pzljlist.stream().map(DeptCodeAccvoucherVo::getNfratMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                        BigDecimal pzLjNfratMc =pzljlist.size()>0?pzljlist.stream().map(DeptCodeAccvoucherVo::getNfratMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                        BigDecimal pzLjNdS =pzljlist.size()>0?pzljlist.stream().map(DeptCodeAccvoucherVo::getNdS).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                        BigDecimal pzLjNcS =pzljlist.size()>0?pzljlist.stream().map(DeptCodeAccvoucherVo::getNcS).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                        BigDecimal pzLjNum=new BigDecimal(0);
                        BigDecimal pzLjNfrat=new BigDecimal(0);

                        BigDecimal qmMd=new BigDecimal(0);
                        BigDecimal qmMc=new BigDecimal(0);
                        BigDecimal qmNum=new BigDecimal(0);
                        BigDecimal qmNfrat=new BigDecimal(0);

                        // 根据方向进行计算 借方【期初借+凭证借-期初贷+凭证贷】 贷方【期初贷+凭证贷-期初借+凭证借】
                        if(bprogerty.equals("1")){  // 借
                            pzNum=pzNdS.subtract(pzNcS);
                            pzLjNum=pzLjNdS.subtract(pzLjNcS);
                            pzNfrat=pzNfratMd.subtract(pzNfratMc);
                            pzLjNfrat=pzLjNfratMd.subtract(pzLjNfratMc);
                            qmNum=qcNum.add(pzNdS.subtract(pzNcS));
                            qmNfrat=qcNfrat.add(pzNfratMd.subtract(pzNfratMc));

                            BigDecimal yue=qcMd.add(pzMd).subtract(qcMc.add(pzMc));
                            if(yue.compareTo(BigDecimal.ZERO) >= 0){
                                qmMc=new BigDecimal(0);
                                qmMd=yue;
                            }else{
                                qmMd=new BigDecimal(0);
                                qmMc=yue.multiply(new BigDecimal(-1));
                            }
                        }
                        else{
                            pzNum=pzNcS.subtract(pzNdS);
                            qmNum=qcNum.add(pzNcS.subtract(pzNdS));
                            pzLjNum=pzLjNcS.subtract(pzLjNdS);
                            pzNfrat=pzNfratMc.subtract(pzNfratMd);
                            pzLjNfrat=pzLjNfratMc.subtract(pzLjNfratMd);
                            qmNfrat=qcNfrat.add(pzNfratMc.subtract(pzNfratMd));
                            BigDecimal yue=qcMc.add(pzMc).subtract(qcMd.add(pzMd));
                            if(yue.compareTo(BigDecimal.ZERO) >= 0){
                                qmMc=yue;
                                qmMd=new BigDecimal(0);
                            }else{
                                qmMc=new BigDecimal(0);
                                qmMd=yue.multiply(new BigDecimal(-1));
                            }
                        }
                        if(qcMd.compareTo(BigDecimal.ZERO) == 0&&qcMc.compareTo(BigDecimal.ZERO) == 0 &&pzMd.compareTo(BigDecimal.ZERO) == 0&&pzMc.compareTo(BigDecimal.ZERO) == 0
                                &&pzLjMd.compareTo(BigDecimal.ZERO) == 0&&pzLjMc.compareTo(BigDecimal.ZERO) == 0
                        ){

                        }else{
                            if(qcMd.compareTo(BigDecimal.ZERO) < 0){
                                qcMc=qcMd.multiply(new BigDecimal(-1));
                                qcMd=new BigDecimal(0);
                            }
                            if(qcMc.compareTo(BigDecimal.ZERO) < 0){
                                qcMd=qcMc.multiply(new BigDecimal(-1));
                                qcMc=new BigDecimal(0);
                            }
                            vo.setTemp1(code.getCcode()).setTemp2(code.getCcodeName())
                                .setUniqueCode(sup.getUniqueCode()).setCcode(sup.getCustCode()).setCcodeName(sup.getCustName())
                                .setCclass(code.getCclass()).setFindByBend(code.getBend()).setUnitMeasurement(code.getMenterage())
                                .setForeignCurrency(code.getCurrencyType())
                                .setQcMd(qcMd).setQcMc(qcMc).setQcNum(qcNum).setQcNfrat(qcNfrat)
                                .setPzMd(pzMd).setPzMc(pzMc).setPzNum(pzNum).setPzNfrat(pzNfrat)
                                .setLjMd(pzLjMd).setLjMc(pzLjMc).setLjNum(pzLjNum).setLjNfrat(pzLjNfrat)
                                .setQmMd(qmMd).setQmMc(qmMc).setQmNum(qmNum).setQmNfrat(qmNfrat);
                            mapList.add(vo);
                        }
                    });
                    BigDecimal totalQcMdTotal=mapList.stream().filter(f->f.getTemp1().equals(ck)).map(KeMuBalanceVo::getQcMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                    BigDecimal totalQcMcTotal=mapList.stream().filter(f->f.getTemp1().equals(ck)).map(KeMuBalanceVo::getQcMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                    BigDecimal totalQcNumTotal=mapList.stream().filter(f->f.getTemp1().equals(ck)).map(KeMuBalanceVo::getQcNum).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                    BigDecimal totalQcNfratTotal=mapList.stream().filter(f->f.getTemp1().equals(ck)).map(KeMuBalanceVo::getQcNfrat).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);

                    BigDecimal totalPzMdTotal= mapList.stream().filter(f->f.getTemp1().equals(ck)).map(KeMuBalanceVo::getPzMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                    BigDecimal totalPzMcTotal= mapList.stream().filter(f->f.getTemp1().equals(ck)).map(KeMuBalanceVo::getPzMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                    BigDecimal totalPzNumTotal= mapList.stream().filter(f->f.getTemp1().equals(ck)).map(KeMuBalanceVo::getPzNum).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                    BigDecimal totalPzNfratTotal= mapList.stream().filter(f->f.getTemp1().equals(ck)).map(KeMuBalanceVo::getPzNfrat).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);

                    BigDecimal totalLjMdTotal=mapList.stream().filter(f->f.getTemp1().equals(ck)).map(KeMuBalanceVo::getLjMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                    BigDecimal totalLjMcTotal= mapList.stream().filter(f->f.getTemp1().equals(ck)).map(KeMuBalanceVo::getLjMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                    BigDecimal totalLjNumTotal= mapList.stream().filter(f->f.getTemp1().equals(ck)).map(KeMuBalanceVo::getLjNum).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);;
                    BigDecimal totalLjNfratTotal= mapList.stream().filter(f->f.getTemp1().equals(ck)).map(KeMuBalanceVo::getLjNfrat).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);;

                    BigDecimal totalQmMdTotal=  mapList.stream().filter(f->f.getTemp1().equals(ck)).map(KeMuBalanceVo::getQmMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                    BigDecimal totalQmMcTotal= mapList.stream().filter(f->f.getTemp1().equals(ck)).map(KeMuBalanceVo::getQmMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                    BigDecimal totalQmNumTotal=  mapList.stream().filter(f->f.getTemp1().equals(ck)).map(KeMuBalanceVo::getQmNum).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                    BigDecimal totalQmNfratTotal= mapList.stream().filter(f->f.getTemp1().equals(ck)).map(KeMuBalanceVo::getQmNfrat).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);

                    BigDecimal total_qcyuemd =new BigDecimal(0);
                    BigDecimal total_qcyuemc =new BigDecimal(0);

                    BigDecimal total_qmyuemd =new BigDecimal(0);
                    BigDecimal total_qmyuemc =new BigDecimal(0);
                    // 计算规则：
                    if(code.getBprogerty().equals("1")){
                        BigDecimal qcyue=totalQcMdTotal.subtract(totalQcMcTotal);
                        BigDecimal qmyue=totalQmMdTotal.subtract(totalQmMcTotal);
                        if(qcyue.compareTo(BigDecimal.ZERO) < 0){ total_qcyuemc=qcyue.multiply(new BigDecimal(-1)); }else{ total_qcyuemd=qcyue; }

                        if(qmyue.compareTo(BigDecimal.ZERO) < 0){total_qmyuemc=qmyue.multiply(new BigDecimal(-1));}else{total_qmyuemd=qmyue;}
                    }else{
                        BigDecimal yue=totalQcMcTotal.subtract(totalQcMdTotal);
                        BigDecimal qmyue=totalQmMdTotal.subtract(totalQmMcTotal);

                        if(yue.compareTo(BigDecimal.ZERO) < 0){total_qcyuemd=yue.multiply(new BigDecimal(-1));}else{total_qcyuemc=yue;}
                        if(qmyue.compareTo(BigDecimal.ZERO) < 0){total_qmyuemc=qmyue.multiply(new BigDecimal(-1));}else{total_qmyuemd=qmyue;}
                    }
                    KeMuBalanceVo vo=new KeMuBalanceVo();
                    vo.setCcode("合计").setFindByBend("0")
                            .setQcMd(total_qcyuemd).setQcMc(total_qcyuemc).setQcNum(totalQcNumTotal).setQcNfrat(totalQcNfratTotal)
                            .setPzMd(totalPzMdTotal).setPzMc(totalPzMcTotal).setPzNum(totalPzNumTotal).setPzNfrat(totalPzNfratTotal)
                            .setLjMd(totalLjMdTotal).setLjMc(totalLjMcTotal).setLjNum(totalLjNumTotal).setLjNfrat(totalLjNfratTotal)
                            .setQmMd(total_qmyuemd).setQmMc(total_qmyuemc).setQmNum(totalQmNumTotal).setQmNfrat(totalQmNfratTotal);
                    mapList.add(vo);

                    Map<String,Object> excelvo=new HashMap<>();
                    excelvo.put("ccode",code.getCcode());
                    excelvo.put("ccodeName",code.getCcodeName());
                    excelvo.put("mxlist", mapList);
                    excellist.add(excelvo);
                });
                return Mono.just(excellist);
            })
            .map(a -> R.ok().setResult(a));

    }
    public Mono<R> exportAll2(String strDate,String endDate,String bz,String ishaveRjz,String styleValue,List<String> ccodeList) {
        String year = strDate.substring(0, 4);
        String month = strDate.substring(4, 6);

        // 获取 已发生凭证的供应商辅助核算
        return supplierRepository.findAllOrderBySupCode2().collectList()
            .flatMap(pzsuplist->{
                List<CustomerVo> finalNewpzsuplist = pzsuplist;
                // 获取 已发生供应商辅助核算的凭证
                return accvoucherRepository.findAllByIyearFuzhuAccvoucher(year,year+"01",endDate).collectList()
                        .flatMap(accvoucherlist->{
                            List<DeptCodeAccvoucherVo> newaccvoucherlist = accvoucherlist;
                            // 根据币种查询凭证
                            if (!"全部".equals(bz)) {
                                newaccvoucherlist = accvoucherlist.stream().filter(a -> a.getForeignCurrency().equals(bz)).collect(Collectors.toList());
                            }
                            // ****** 是否包含未记账【查询条件】 *****不包含；只获取 已记账的凭证
                            if (ishaveRjz.equals("0")) {
                                newaccvoucherlist = accvoucherlist.stream().filter(vo -> StringUtils.isNotBlank(vo.getIbook()) && vo.getIbook().equals("1")).collect(Collectors.toList());
                            }

                            List<DeptCodeAccvoucherVo> finalNewaccvoucherlist = newaccvoucherlist;
                            return fuZhuBalanceService.findAllSubjectInitialFuZhuBalance(year, "false", year + "21", "")
                                    .flatMap(qclist -> {
                                        Map qcmap = (Map) qclist.getResult();
                                        // 获取科目详细信息
                                        return codeKemuRepository.findByIyearOrderByCcode(year).collectList()
                                                .flatMap(codelist->{
                                                    return Mono.just(
                                                            CollectOfUtils.mapof("qclist", qcmap.get("tablesData"), "pzlist", finalNewaccvoucherlist, "pzsuplist", finalNewpzsuplist,"codelist",codelist)
                                                    );
                                                });
                                    });
                        });
            })
            .flatMap(map->{
                List<CodeKemu> codelist = (List<CodeKemu>) map.get("codelist");
                List<CustomerVo> pzsuplist = (List<CustomerVo>) map.get("pzsuplist");
                List<DeptCodeAccvoucherVo> pzlist = (List<DeptCodeAccvoucherVo>) map.get("pzlist");
                List<SubjectInitialBalanceVo> qclist = (List<SubjectInitialBalanceVo>) map.get("qclist");
                // 封装list
                ArrayList<KeMuBalanceVo> mapList = new ArrayList<>();
                ccodeList.stream().forEach(ck->{
                    List<CodeKemu> collect = codelist.stream().filter(v -> v.getCcode().equals(ck)).collect(Collectors.toList());
                    CodeKemu code=collect.get(0);

                    pzsuplist.stream().filter(v->v.getCcode().equals(ck)).forEach(sup->{
                        KeMuBalanceVo vo = new KeMuBalanceVo();
                        String bprogerty = code.getBprogerty();  // 方向
                        // ************* 获取此科目下的客户期初 *************
                        List<SubjectInitialBalanceVo> findByCodeQcList = qclist.stream().filter(qc -> StrUtil.isNotBlank(qc.getCsupId()) && qc.getCcode().equals(ck)&&qc.getCsupId().equals(sup.getUniqueCode())).collect(Collectors.toList());
                        BigDecimal qcMd = findByCodeQcList.size() > 0 ? findByCodeQcList.get(0).getMd() : new BigDecimal(0);
                        BigDecimal qcMc = findByCodeQcList.size() > 0 ? findByCodeQcList.get(0).getMc() : new BigDecimal(0);
                        // 数量
                        BigDecimal qcNdS = findByCodeQcList.size() > 0 ? findByCodeQcList.get(0).getNdS() : new BigDecimal(0);
                        BigDecimal qcNcS = findByCodeQcList.size() > 0 ? findByCodeQcList.get(0).getNcS() : new BigDecimal(0);
                        BigDecimal qcNum = new BigDecimal(0);
                        // 外币金额
                        BigDecimal qcNfratMd = findByCodeQcList.size() > 0 ? findByCodeQcList.get(0).getNfratMd() : new BigDecimal(0);
                        BigDecimal qcNfratMc = findByCodeQcList.size() > 0 ? findByCodeQcList.get(0).getNfratMc() : new BigDecimal(0);
                        BigDecimal qcNfrat = new BigDecimal(0);

                        // 期间 不是从一月开始的 计算出上月的期末余额
                        if (Integer.valueOf(strDate.substring(4, 6)) > 1) {
                            String lastmonth = Integer.valueOf(month) - 1 < 10 ? "0" + (Integer.valueOf(month) - 1) : month;
                            List<DeptCodeAccvoucherVo> lastpz = pzlist.stream().filter(last -> Integer.valueOf(last.getImonth()) <= Integer.valueOf(lastmonth) && last.getCcode().equals(code.getCcode())&&last.getCsupId().equals(sup.getUniqueCode())).collect(Collectors.toList());
                            BigDecimal lastMd = lastpz.size() > 0 ? lastpz.stream().map(DeptCodeAccvoucherVo::getMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum) : new BigDecimal(0);
                            BigDecimal lastMc = lastpz.size() > 0 ? lastpz.stream().map(DeptCodeAccvoucherVo::getMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum) : new BigDecimal(0);
                            BigDecimal lastNdS = lastpz.size() > 0 ? lastpz.stream().map(DeptCodeAccvoucherVo::getNdS).reduce(BigDecimal.ZERO, BigDecimalUtils::sum) : new BigDecimal(0);
                            BigDecimal lastNcS = lastpz.size() > 0 ? lastpz.stream().map(DeptCodeAccvoucherVo::getNcS).reduce(BigDecimal.ZERO, BigDecimalUtils::sum) : new BigDecimal(0);
                            BigDecimal lastNfratMd = lastpz.size() > 0 ? lastpz.stream().map(DeptCodeAccvoucherVo::getNfratMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum) : new BigDecimal(0);
                            BigDecimal lastNfratMc = lastpz.size() > 0 ? lastpz.stream().map(DeptCodeAccvoucherVo::getNfratMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum) : new BigDecimal(0);
                            // 根据方向进行计算 借方【期初借+凭证借-期初贷+凭证贷】 贷方【期初贷+凭证贷-期初借+凭证借】
                            if(bprogerty.equals("1")){  // 借
                                qcNum=qcNdS.add(lastNdS).subtract(qcNcS.add(lastNcS));
                                qcNfrat=qcNfratMd.add(lastNfratMd).subtract(qcNfratMc.add(lastNfratMc));
                                BigDecimal yue=qcMd.add(lastMd).subtract(qcMc.add(lastMc));
                                if(yue.compareTo(BigDecimal.ZERO) >= 0){
                                    qcMc=new BigDecimal(0);
                                    qcMd=yue;
                                }else{
                                    qcMd=new BigDecimal(0);
                                    qcMc=yue.multiply(new BigDecimal(-1));
                                }
                            }
                            else{                       // 贷
                                qcNum=qcNcS.add(lastNcS).subtract(qcNdS.add(lastNdS));
                                qcNfrat=qcNfratMc.add(lastNfratMc).subtract(qcNfratMd.add(lastNfratMd));
                                BigDecimal yue=qcMc.add(lastMc).subtract(qcMd.add(lastMd));
                                if(yue.compareTo(BigDecimal.ZERO) >= 0){
                                    qcMd=new BigDecimal(0);
                                    qcMc=yue;
                                }else{
                                    qcMc=new BigDecimal(0);
                                    qcMd=yue.multiply(new BigDecimal(-1));
                                }
                            }
                        }

                        // 检索科目获取凭证
                        List<DeptCodeAccvoucherVo> newpzlist = pzlist.stream().filter(pz ->StrUtil.isNotBlank(pz.getCsupId()) &&Integer.valueOf(pz.getIyperiod()) >= Integer.valueOf(strDate) && Integer.valueOf(pz.getIyperiod()) <= Integer.valueOf(endDate)&& pz.getCcode().equals(code.getCcode())&&pz.getCsupId().equals(sup.getUniqueCode())).collect(Collectors.toList());
                        BigDecimal pzMd =newpzlist.size()>0?newpzlist.stream().map(DeptCodeAccvoucherVo::getMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                        BigDecimal pzMc =newpzlist.size()>0?newpzlist.stream().map(DeptCodeAccvoucherVo::getMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                        BigDecimal pzNfratMd =newpzlist.size()>0?newpzlist.stream().map(DeptCodeAccvoucherVo::getNfratMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                        BigDecimal pzNfratMc =newpzlist.size()>0?newpzlist.stream().map(DeptCodeAccvoucherVo::getNfratMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                        BigDecimal pzNdS=newpzlist.size()>0?newpzlist.stream().map(DeptCodeAccvoucherVo::getNdS).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                        BigDecimal pzNcS=newpzlist.size()>0?newpzlist.stream().map(DeptCodeAccvoucherVo::getNcS).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                        BigDecimal pzNum=new BigDecimal(0);
                        BigDecimal pzNfrat=new BigDecimal(0);

                        // 获取累计发生 【月份：<= endDate】
                        List<DeptCodeAccvoucherVo> pzljlist = pzlist.stream().filter(pz ->StrUtil.isNotBlank(pz.getCsupId()) && Integer.valueOf(pz.getIyperiod()) >= Integer.valueOf(strDate) && Integer.valueOf(pz.getIyperiod()) <= Integer.valueOf(endDate)&& pz.getCcode().equals(code.getCcode())&&pz.getCsupId().equals(sup.getUniqueCode())).collect(Collectors.toList());
                        BigDecimal pzLjMd =pzljlist.size()>0?pzljlist.stream().map(DeptCodeAccvoucherVo::getMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                        BigDecimal pzLjMc =pzljlist.size()>0?pzljlist.stream().map(DeptCodeAccvoucherVo::getMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                        BigDecimal pzLjNfratMd =pzljlist.size()>0?pzljlist.stream().map(DeptCodeAccvoucherVo::getNfratMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                        BigDecimal pzLjNfratMc =pzljlist.size()>0?pzljlist.stream().map(DeptCodeAccvoucherVo::getNfratMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                        BigDecimal pzLjNdS =pzljlist.size()>0?pzljlist.stream().map(DeptCodeAccvoucherVo::getNdS).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                        BigDecimal pzLjNcS =pzljlist.size()>0?pzljlist.stream().map(DeptCodeAccvoucherVo::getNcS).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                        BigDecimal pzLjNum=new BigDecimal(0);
                        BigDecimal pzLjNfrat=new BigDecimal(0);

                        BigDecimal qmMd=new BigDecimal(0);
                        BigDecimal qmMc=new BigDecimal(0);
                        BigDecimal qmNum=new BigDecimal(0);
                        BigDecimal qmNfrat=new BigDecimal(0);

                        // 根据方向进行计算 借方【期初借+凭证借-期初贷+凭证贷】 贷方【期初贷+凭证贷-期初借+凭证借】
                        if(bprogerty.equals("1")){  // 借
                            pzNum=pzNdS.subtract(pzNcS);
                            pzLjNum=pzLjNdS.subtract(pzLjNcS);
                            pzNfrat=pzNfratMd.subtract(pzNfratMc);
                            pzLjNfrat=pzLjNfratMd.subtract(pzLjNfratMc);
                            qmNum=qcNum.add(pzNdS.subtract(pzNcS));
                            qmNfrat=qcNfrat.add(pzNfratMd.subtract(pzNfratMc));

                            BigDecimal yue=qcMd.add(pzMd).subtract(qcMc.add(pzMc));
                            if(yue.compareTo(BigDecimal.ZERO) >= 0){
                                qmMc=new BigDecimal(0);
                                qmMd=yue;
                            }else{
                                qmMd=new BigDecimal(0);
                                qmMc=yue.multiply(new BigDecimal(-1));
                            }
                        }
                        else{
                            pzNum=pzNcS.subtract(pzNdS);
                            qmNum=qcNum.add(pzNcS.subtract(pzNdS));
                            pzLjNum=pzLjNcS.subtract(pzLjNdS);
                            pzNfrat=pzNfratMc.subtract(pzNfratMd);
                            pzLjNfrat=pzLjNfratMc.subtract(pzLjNfratMd);
                            qmNfrat=qcNfrat.add(pzNfratMc.subtract(pzNfratMd));
                            BigDecimal yue=qcMc.add(pzMc).subtract(qcMd.add(pzMd));
                            if(yue.compareTo(BigDecimal.ZERO) >= 0){
                                qmMc=yue;
                                qmMd=new BigDecimal(0);
                            }else{
                                qmMc=new BigDecimal(0);
                                qmMd=yue.multiply(new BigDecimal(-1));
                            }
                        }
                        if(qcMd.compareTo(BigDecimal.ZERO) == 0&&qcMc.compareTo(BigDecimal.ZERO) == 0 &&pzMd.compareTo(BigDecimal.ZERO) == 0&&pzMc.compareTo(BigDecimal.ZERO) == 0
                                &&pzLjMd.compareTo(BigDecimal.ZERO) == 0&&pzLjMc.compareTo(BigDecimal.ZERO) == 0
                        ){

                        }else{
                            if(qcMd.compareTo(BigDecimal.ZERO) < 0){
                                qcMc=qcMd.multiply(new BigDecimal(-1));
                                qcMd=new BigDecimal(0);
                            }
                            if(qcMc.compareTo(BigDecimal.ZERO) < 0){
                                qcMd=qcMc.multiply(new BigDecimal(-1));
                                qcMc=new BigDecimal(0);
                            }
                            vo.setTemp1(code.getCcode()).setTemp2(code.getCcodeName())
                                    .setUniqueCode(sup.getUniqueCode()).setCcode(sup.getCustCode()).setCcodeName(sup.getCustName())
                                    .setCclass(code.getCclass()).setFindByBend(code.getBend()).setUnitMeasurement(code.getMenterage())
                                    .setForeignCurrency(code.getCurrencyType())
                                    .setQcMd(qcMd).setQcMc(qcMc).setQcNum(qcNum).setQcNfrat(qcNfrat)
                                    .setPzMd(pzMd).setPzMc(pzMc).setPzNum(pzNum).setPzNfrat(pzNfrat)
                                    .setLjMd(pzLjMd).setLjMc(pzLjMc).setLjNum(pzLjNum).setLjNfrat(pzLjNfrat)
                                    .setQmMd(qmMd).setQmMc(qmMc).setQmNum(qmNum).setQmNfrat(qmNfrat);
                            mapList.add(vo);
                        }
                    });

                    if(mapList.size()>0){
                        BigDecimal totalQcMdTotal=mapList.stream().filter(f->f.getTemp1().equals(ck)).map(KeMuBalanceVo::getQcMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                        BigDecimal totalQcMcTotal=mapList.stream().filter(f->f.getTemp1().equals(ck)).map(KeMuBalanceVo::getQcMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                        BigDecimal totalQcNumTotal=mapList.stream().filter(f->f.getTemp1().equals(ck)).map(KeMuBalanceVo::getQcNum).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                        BigDecimal totalQcNfratTotal=mapList.stream().filter(f->f.getTemp1().equals(ck)).map(KeMuBalanceVo::getQcNfrat).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);

                        BigDecimal totalPzMdTotal= mapList.stream().filter(f->f.getTemp1().equals(ck)).map(KeMuBalanceVo::getPzMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                        BigDecimal totalPzMcTotal= mapList.stream().filter(f->f.getTemp1().equals(ck)).map(KeMuBalanceVo::getPzMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                        BigDecimal totalPzNumTotal= mapList.stream().filter(f->f.getTemp1().equals(ck)).map(KeMuBalanceVo::getPzNum).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                        BigDecimal totalPzNfratTotal= mapList.stream().filter(f->f.getTemp1().equals(ck)).map(KeMuBalanceVo::getPzNfrat).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);

                        BigDecimal totalLjMdTotal=mapList.stream().filter(f->f.getTemp1().equals(ck)).map(KeMuBalanceVo::getLjMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                        BigDecimal totalLjMcTotal= mapList.stream().filter(f->f.getTemp1().equals(ck)).map(KeMuBalanceVo::getLjMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                        BigDecimal totalLjNumTotal= mapList.stream().filter(f->f.getTemp1().equals(ck)).map(KeMuBalanceVo::getLjNum).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);;
                        BigDecimal totalLjNfratTotal= mapList.stream().filter(f->f.getTemp1().equals(ck)).map(KeMuBalanceVo::getLjNfrat).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);;

                        BigDecimal totalQmMdTotal=  mapList.stream().filter(f->f.getTemp1().equals(ck)).map(KeMuBalanceVo::getQmMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                        BigDecimal totalQmMcTotal= mapList.stream().filter(f->f.getTemp1().equals(ck)).map(KeMuBalanceVo::getQmMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                        BigDecimal totalQmNumTotal=  mapList.stream().filter(f->f.getTemp1().equals(ck)).map(KeMuBalanceVo::getQmNum).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                        BigDecimal totalQmNfratTotal= mapList.stream().filter(f->f.getTemp1().equals(ck)).map(KeMuBalanceVo::getQmNfrat).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);

                        BigDecimal total_qcyuemd =new BigDecimal(0);
                        BigDecimal total_qcyuemc =new BigDecimal(0);

                        BigDecimal total_qmyuemd =new BigDecimal(0);
                        BigDecimal total_qmyuemc =new BigDecimal(0);
                        // 计算规则：
                        if(code.getBprogerty().equals("1")){
                            BigDecimal qcyue=totalQcMdTotal.subtract(totalQcMcTotal);
                            BigDecimal qmyue=totalQmMdTotal.subtract(totalQmMcTotal);
                            if(qcyue.compareTo(BigDecimal.ZERO) < 0){ total_qcyuemc=qcyue.multiply(new BigDecimal(-1)); }else{ total_qcyuemd=qcyue; }

                            if(qmyue.compareTo(BigDecimal.ZERO) < 0){total_qmyuemc=qmyue.multiply(new BigDecimal(-1));}else{total_qmyuemd=qmyue;}
                        }else{
                            BigDecimal yue=totalQcMcTotal.subtract(totalQcMdTotal);
                            BigDecimal qmyue=totalQmMdTotal.subtract(totalQmMcTotal);

                            if(yue.compareTo(BigDecimal.ZERO) < 0){total_qcyuemd=yue.multiply(new BigDecimal(-1));}else{total_qcyuemc=yue;}
                            if(qmyue.compareTo(BigDecimal.ZERO) < 0){total_qmyuemc=qmyue.multiply(new BigDecimal(-1));}else{total_qmyuemd=qmyue;}
                        }
                        KeMuBalanceVo vo=new KeMuBalanceVo();
                        vo.setTemp1(code.getCcode()).setTemp2(code.getCcodeName()).setCcode("合计").setFindByBend("0")
                            .setQcMd(total_qcyuemd).setQcMc(total_qcyuemc).setQcNum(totalQcNumTotal).setQcNfrat(totalQcNfratTotal)
                            .setPzMd(totalPzMdTotal).setPzMc(totalPzMcTotal).setPzNum(totalPzNumTotal).setPzNfrat(totalPzNfratTotal)
                            .setLjMd(totalLjMdTotal).setLjMc(totalLjMcTotal).setLjNum(totalLjNumTotal).setLjNfrat(totalLjNfratTotal)
                            .setQmMd(total_qmyuemd).setQmMc(total_qmyuemc).setQmNum(totalQmNumTotal).setQmNfrat(totalQmNfratTotal);
                        mapList.add(vo);
                    }
                });
                return Mono.just(mapList);
            })
            .map(a -> R.ok().setResult(a));

    }
}

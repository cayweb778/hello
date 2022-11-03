package org.boozsoft.service.impl.fuzhu.cus.yue;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.boozsoft.domain.entity.Customer;
import org.boozsoft.domain.entity.codekemu.CodeKemu;
import org.boozsoft.domain.vo.CustomerVo;
import org.boozsoft.domain.vo.DeptCodeAccvoucherVo;
import org.boozsoft.domain.vo.KeMuBalanceVo;
import org.boozsoft.domain.vo.SubjectInitialBalanceVo;
import org.boozsoft.repo.AccvoucherRepository;
import org.boozsoft.repo.CustomerRepository;
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
public class CusKmYueServiceImpl {

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


    public Mono<R> findAll(Pageable pageable, String minDept, String km, String strDate, String endDate, String bz, String ishaveRjz,
                           String styleValue, Map<String, String> searchMap, Map<String, String> filterMap,String database) {
        String year = strDate.substring(0, 4);
        String month = strDate.substring(4, 6);
        AtomicReference<Long> totalAR = new AtomicReference(0);
        // 1、获取客户辅助核算的科目
        return codeKemuRepository.findByBcusAndIyearOrderByCcodeAsc("1", year).collectList()
                .flatMap(deptCodeList -> {
                    List<CodeKemu> newDeptCodeList = deptCodeList;
                    //  2、过滤包含此科目的末级科目
                    if (StringUtils.isNotBlank(km)) {
                        newDeptCodeList = deptCodeList.stream().filter(ck -> ck.getCcode().startsWith(km) && ck.getBend().equals("1")).collect(Collectors.toList());
                    }
                    // 3-1、根据科目类型查找   财务会计【1-5】 预算会计【6-8】 先写死
                    if ("财务会计".equals(styleValue)) {
                        newDeptCodeList = newDeptCodeList.stream().filter(ck -> Integer.valueOf(ck.getCcode().substring(0, 1)) <= 5).collect(Collectors.toList());
                    }
                    // 3-2、根据科目类型查找   财务会计【1-5】 预算会计【6-8】 先写死
                    else if ("预算会计".equals(styleValue)) {
                        newDeptCodeList = newDeptCodeList.stream().filter(ck -> Integer.valueOf(ck.getCcode().substring(0, 1)) >= 6 && Integer.valueOf(ck.getCcode().substring(0, 1)) <= 8).collect(Collectors.toList());
                    }
                    // 3-3、根据科目类型查找
                    else if (!"全部".equals(styleValue)) {
                        newDeptCodeList = newDeptCodeList.stream().filter(ck -> ck.getCclass().equals(styleValue)).collect(Collectors.toList());
                    }

                    List<CodeKemu> finalNewDeptCodeList = newDeptCodeList;

                    // 查询客户辅助核算凭证
                    return accvoucherRepository.findAllByIyearAndCcusId(year, minDept, year+"01", endDate).collectList()
                            .flatMap(accvoucherlist -> {
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
                                            return Mono.just(
                                                    CollectOfUtils.mapof("qclist", qcmap.get("tablesData"), "deptAccVoucherList", finalNewaccvoucherlist, "codeList", finalNewDeptCodeList)
                                            );
                                        });
                            });
                })
                // 获取部门科目凭证
                .flatMap(map -> {
                    List<KeMuBalanceVo> mapList = new ArrayList<>();
                    List<CodeKemu> codeList = (List<CodeKemu>) map.get("codeList");  // 都是末级科目
                    List<SubjectInitialBalanceVo> qclist = (List<SubjectInitialBalanceVo>) map.get("qclist");
                    List<DeptCodeAccvoucherVo> pzlist = (List<DeptCodeAccvoucherVo>) map.get("deptAccVoucherList");

                    codeList.stream().forEach(ck -> {
                        KeMuBalanceVo vo = new KeMuBalanceVo();
                        String ccode = ck.getCcode();
                        String ccodeName = ck.getCcodeName();
                        String cclass = ck.getCclass();
                        String bprogerty = ck.getBprogerty();  // 方向
                        String findByBend = ck.getBend();      // 末级
                        String unitMeasurement = ck.getMenterage();
                        String foreignCurrency = ck.getCurrencyType();

                        // ************* 获取科目期初 *************
                        List<SubjectInitialBalanceVo> findByCodeQcList = qclist.stream().filter(qc -> StrUtil.isNotBlank(qc.getCcusId()) && qc.getCcode().equals(ck.getCcode())&& qc.getCcusId().equals(minDept)).collect(Collectors.toList());
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
                            List<DeptCodeAccvoucherVo> lastpz = pzlist.stream().filter(last -> last.getCcode().startsWith(ck.getCcode()) && Integer.valueOf(last.getImonth()) <= Integer.valueOf(lastmonth)).collect(Collectors.toList());
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
                        List<DeptCodeAccvoucherVo> newpzlist = pzlist.stream().filter(pz -> pz.getCcode().startsWith(ck.getCcode()) && Integer.valueOf(pz.getIyperiod()) >= Integer.valueOf(strDate) && Integer.valueOf(pz.getIyperiod()) <= Integer.valueOf(endDate)).collect(Collectors.toList());
                        BigDecimal pzMd =newpzlist.size()>0?newpzlist.stream().map(DeptCodeAccvoucherVo::getMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                        BigDecimal pzMc =newpzlist.size()>0?newpzlist.stream().map(DeptCodeAccvoucherVo::getMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                        BigDecimal pzNfratMd =newpzlist.size()>0?newpzlist.stream().map(DeptCodeAccvoucherVo::getNfratMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                        BigDecimal pzNfratMc =newpzlist.size()>0?newpzlist.stream().map(DeptCodeAccvoucherVo::getNfratMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                        BigDecimal pzNdS=newpzlist.size()>0?newpzlist.stream().map(DeptCodeAccvoucherVo::getNdS).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                        BigDecimal pzNcS=newpzlist.size()>0?newpzlist.stream().map(DeptCodeAccvoucherVo::getNcS).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                        BigDecimal pzNum=new BigDecimal(0);
                        BigDecimal pzNfrat=new BigDecimal(0);

                        // 获取累计发生 【月份：<= endDate】
                        List<DeptCodeAccvoucherVo> pzljlist = pzlist.stream().filter(pz -> pz.getCcode().startsWith(ck.getCcode()) && Integer.valueOf(pz.getIyperiod()) <= Integer.valueOf(endDate)).collect(Collectors.toList());
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
                            pzNum=pzNcS.subtract(qcNdS);
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
                            vo.setCcode(ccode).setCcodeName(ccodeName).setCclass(cclass).setFindByBend(findByBend).setUnitMeasurement(unitMeasurement).setForeignCurrency(foreignCurrency)
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

    public Mono<R> exportAll(String strDate, String endDate, String bz, String ishaveRjz, String styleValue,List<String>ccodeList) {
        String year = strDate.substring(0, 4);
        String month = strDate.substring(4, 6);
        // 1、获取客户辅助核算的科目
        return codeKemuRepository.findByBcusAndIyearOrderByCcodeAsc("1", year).collectList()
            .flatMap(deptCodeList -> {
                List<CodeKemu> newDeptCodeList = deptCodeList;
                // 3-1、根据科目类型查找   财务会计【1-5】 预算会计【6-8】 先写死
                if ("财务会计".equals(styleValue)) {
                    newDeptCodeList = newDeptCodeList.stream().filter(ck -> Integer.valueOf(ck.getCcode().substring(0, 1)) <= 5).collect(Collectors.toList());
                }
                // 3-2、根据科目类型查找   财务会计【1-5】 预算会计【6-8】 先写死
                else if ("预算会计".equals(styleValue)) {
                    newDeptCodeList = newDeptCodeList.stream().filter(ck -> Integer.valueOf(ck.getCcode().substring(0, 1)) >= 6 && Integer.valueOf(ck.getCcode().substring(0, 1)) <= 8).collect(Collectors.toList());
                }
                // 3-3、根据科目类型查找
                else if (!"全部".equals(styleValue)) {
                    newDeptCodeList = newDeptCodeList.stream().filter(ck -> ck.getCclass().equals(styleValue)).collect(Collectors.toList());
                }
                List<CodeKemu> finalNewDeptCodeList = newDeptCodeList;

                // 查询客户辅助核算凭证
                return accvoucherRepository.findAllByIyearFuzhuAccvoucher(year,year+"01", endDate).collectList()
                    .flatMap(accvoucherlist -> {
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
                                return customerRepository.findAllOrderByCustCode().collectList()
                                    .flatMap(cuslist->{
                                        return Mono.just(
                                                CollectOfUtils.mapof("qclist", qcmap.get("tablesData"), "deptAccVoucherList", finalNewaccvoucherlist, "codeList", finalNewDeptCodeList,"cuslist",cuslist)
                                        );
                                    });
                            });
                    });
            })
            .flatMap(map->{
                List<CodeKemu> codeList = (List<CodeKemu>) map.get("codeList");  // 都是末级科目
                List<SubjectInitialBalanceVo> qclist = (List<SubjectInitialBalanceVo>) map.get("qclist");
                List<DeptCodeAccvoucherVo> pzlist = (List<DeptCodeAccvoucherVo>) map.get("deptAccVoucherList");
                List<Customer> cuslist = (List<Customer>) map.get("cuslist");
                // 封装list
                List<Map<String,Object>> excellist = new ArrayList<>();
                cuslist.stream().forEach(cus->{
                    List<KeMuBalanceVo> mapList = new ArrayList<>();
                    codeList.stream().forEach(ck -> {
                        KeMuBalanceVo vo = new KeMuBalanceVo();
                        String ccode = ck.getCcode();
                        String ccodeName = ck.getCcodeName();
                        String cclass = ck.getCclass();
                        String bprogerty = ck.getBprogerty();  // 方向
                        String findByBend = ck.getBend();      // 末级
                        String unitMeasurement = ck.getMenterage();
                        String foreignCurrency = ck.getCurrencyType();

                        // ************* 获取科目期初 *************
                        List<SubjectInitialBalanceVo> findByCodeQcList = qclist.stream().filter(qc -> StrUtil.isNotBlank(qc.getCcusId()) && qc.getCcode().equals(ck.getCcode())&& qc.getCcusId().equals(cus.getUniqueCode())).collect(Collectors.toList());
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
                            List<DeptCodeAccvoucherVo> lastpz = pzlist.stream().filter(last ->last.getCcusId().equals(cus.getUniqueCode()) && last.getCcode().startsWith(ck.getCcode()) && Integer.valueOf(last.getImonth()) <= Integer.valueOf(lastmonth)).collect(Collectors.toList());
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
                        List<DeptCodeAccvoucherVo> newpzlist = pzlist.stream().filter(pz ->StrUtil.isNotBlank(pz.getCcusId()) && pz.getCcusId().equals(cus.getUniqueCode()) && pz.getCcode().startsWith(ck.getCcode()) && Integer.valueOf(pz.getIyperiod()) >= Integer.valueOf(strDate) && Integer.valueOf(pz.getIyperiod()) <= Integer.valueOf(endDate)).collect(Collectors.toList());
                        BigDecimal pzMd =newpzlist.size()>0?newpzlist.stream().map(DeptCodeAccvoucherVo::getMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                        BigDecimal pzMc =newpzlist.size()>0?newpzlist.stream().map(DeptCodeAccvoucherVo::getMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                        BigDecimal pzNfratMd =newpzlist.size()>0?newpzlist.stream().map(DeptCodeAccvoucherVo::getNfratMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                        BigDecimal pzNfratMc =newpzlist.size()>0?newpzlist.stream().map(DeptCodeAccvoucherVo::getNfratMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                        BigDecimal pzNdS=newpzlist.size()>0?newpzlist.stream().map(DeptCodeAccvoucherVo::getNdS).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                        BigDecimal pzNcS=newpzlist.size()>0?newpzlist.stream().map(DeptCodeAccvoucherVo::getNcS).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                        BigDecimal pzNum=new BigDecimal(0);
                        BigDecimal pzNfrat=new BigDecimal(0);

                        // 获取累计发生 【月份：<= endDate】
                        List<DeptCodeAccvoucherVo> pzljlist = pzlist.stream().filter(pz ->StrUtil.isNotBlank(pz.getCcusId()) &&pz.getCcusId().equals(cus.getUniqueCode()) && pz.getCcode().startsWith(ck.getCcode()) && Integer.valueOf(pz.getIyperiod()) <= Integer.valueOf(endDate)).collect(Collectors.toList());
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
                            pzNum=pzNcS.subtract(qcNdS);
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
                            vo.setCcode(ccode).setCcodeName(ccodeName).setCclass(cclass).setFindByBend(findByBend).setUnitMeasurement(unitMeasurement).setForeignCurrency(foreignCurrency)
                                    .setQcMd(qcMd).setQcMc(qcMc).setQcNum(qcNum).setQcNfrat(qcNfrat)
                                    .setPzMd(pzMd).setPzMc(pzMc).setPzNum(pzNum).setPzNfrat(pzNfrat)
                                    .setLjMd(pzLjMd).setLjMc(pzLjMc).setLjNum(pzLjNum).setLjNfrat(pzLjNfrat)
                                    .setQmMd(qmMd).setQmMc(qmMc).setQmNum(qmNum).setQmNfrat(qmNfrat);
                            mapList.add(vo);
                        }
                    });

                    BigDecimal totalQcMdTotal=mapList.stream().map(KeMuBalanceVo::getQcMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                    BigDecimal totalQcMcTotal=mapList.stream().map(KeMuBalanceVo::getQcMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                    BigDecimal totalQcNumTotal=mapList.stream().map(KeMuBalanceVo::getQcNum).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                    BigDecimal totalQcNfratTotal=mapList.stream().map(KeMuBalanceVo::getQcNfrat).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);

                    BigDecimal totalPzMdTotal= mapList.stream().map(KeMuBalanceVo::getPzMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                    BigDecimal totalPzMcTotal= mapList.stream().map(KeMuBalanceVo::getPzMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                    BigDecimal totalPzNumTotal= mapList.stream().map(KeMuBalanceVo::getPzNum).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                    BigDecimal totalPzNfratTotal= mapList.stream().map(KeMuBalanceVo::getPzNfrat).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);

                    BigDecimal totalLjMdTotal=mapList.stream().map(KeMuBalanceVo::getLjMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                    BigDecimal totalLjMcTotal= mapList.stream().map(KeMuBalanceVo::getLjMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                    BigDecimal totalLjNumTotal= mapList.stream().map(KeMuBalanceVo::getLjNum).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);;
                    BigDecimal totalLjNfratTotal= mapList.stream().map(KeMuBalanceVo::getLjNfrat).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);;

                    BigDecimal totalQmMdTotal=  mapList.stream().map(KeMuBalanceVo::getQmMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                    BigDecimal totalQmMcTotal= mapList.stream().map(KeMuBalanceVo::getQmMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                    BigDecimal totalQmNumTotal=  mapList.stream().map(KeMuBalanceVo::getQmNum).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                    BigDecimal totalQmNfratTotal= mapList.stream().map(KeMuBalanceVo::getQmNfrat).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);

                    BigDecimal total_qmyuemd =new BigDecimal(0);
                    BigDecimal total_qmyuemc =new BigDecimal(0);

                    BigDecimal total_qcyuemd =new BigDecimal(0);
                    BigDecimal total_qcyuemc =new BigDecimal(0);

                    // 借小于贷
                    if(totalQmMdTotal.compareTo(totalQmMcTotal) == -1){
                        BigDecimal yue =totalQmMcTotal.subtract(totalQmMdTotal);
                        if(yue.compareTo(BigDecimal.ZERO)<0){
                            total_qmyuemd=yue.multiply(new BigDecimal(-1));
                        }else{
                            total_qmyuemc=yue;
                        }
                    }
                    if(totalQcMdTotal.compareTo(totalQcMcTotal) == -1){
                        BigDecimal yue =totalQcMcTotal.subtract(totalQcMdTotal);
                        if(yue.compareTo(BigDecimal.ZERO)<0){
                            total_qcyuemd=yue.multiply(new BigDecimal(-1));
                        }else{
                            total_qcyuemc=yue;
                        }
                    }

                    // 借大于贷
                    if(totalQmMdTotal.compareTo(totalQmMcTotal) == 1){
                        BigDecimal yue =totalQmMdTotal.subtract(totalQmMcTotal);
                        if(yue.compareTo(BigDecimal.ZERO)<0){
                            total_qmyuemc=yue.multiply(new BigDecimal(-1));
                        }else{
                            total_qmyuemd=yue;
                        }
                    }
                    if(totalQcMdTotal.compareTo(totalQcMcTotal) == 1){
                        BigDecimal yue =totalQcMdTotal.subtract(totalQcMcTotal);
                        if(yue.compareTo(BigDecimal.ZERO)<0){
                            total_qcyuemc=yue.multiply(new BigDecimal(-1));
                        }else{
                            total_qcyuemd=yue;
                        }
                    }

                    KeMuBalanceVo vo=new KeMuBalanceVo();
                    vo.setCcode("合计").setFindByBend("0")
                            .setQcMd(total_qcyuemd).setQcMc(total_qcyuemc).setQcNum(totalQcNumTotal).setQcNfrat(totalQcNfratTotal)
                            .setPzMd(totalPzMdTotal).setPzMc(totalPzMcTotal).setPzNum(totalPzNumTotal).setPzNfrat(totalPzNfratTotal)
                            .setLjMd(totalLjMdTotal).setLjMc(totalLjMcTotal).setLjNum(totalLjNumTotal).setLjNfrat(totalLjNfratTotal)
                            .setQmMd(total_qmyuemd).setQmMc(total_qmyuemc).setQmNum(totalQmNumTotal).setQmNfrat(totalQmNfratTotal);
                    mapList.add(vo);

                    Map<String,Object> excelvo=new HashMap<>();
                    excelvo.put("ccode",cus.getCustCode());
                    excelvo.put("ccodeName",cus.getCustName());
                    excelvo.put("mxlist", mapList);
                    excellist.add(excelvo);
                });
                return Mono.just(excellist);
            })
            .map(a -> R.ok().setResult(a));
    }
    public Mono<R> exportAll2(String strDate, String endDate, String bz, String ishaveRjz, String styleValue,List<String>ccodeList) {
        String year = strDate.substring(0, 4);
        String month = strDate.substring(4, 6);
        // 1、获取客户辅助核算的科目
        return codeKemuRepository.findByBcusAndIyearOrderByCcodeAsc("1", year).collectList()
                .flatMap(deptCodeList -> {
                    List<CodeKemu> newDeptCodeList = deptCodeList;
                    // 3-1、根据科目类型查找   财务会计【1-5】 预算会计【6-8】 先写死
                    if ("财务会计".equals(styleValue)) {
                        newDeptCodeList = newDeptCodeList.stream().filter(ck -> Integer.valueOf(ck.getCcode().substring(0, 1)) <= 5).collect(Collectors.toList());
                    }
                    // 3-2、根据科目类型查找   财务会计【1-5】 预算会计【6-8】 先写死
                    else if ("预算会计".equals(styleValue)) {
                        newDeptCodeList = newDeptCodeList.stream().filter(ck -> Integer.valueOf(ck.getCcode().substring(0, 1)) >= 6 && Integer.valueOf(ck.getCcode().substring(0, 1)) <= 8).collect(Collectors.toList());
                    }
                    // 3-3、根据科目类型查找
                    else if (!"全部".equals(styleValue)) {
                        newDeptCodeList = newDeptCodeList.stream().filter(ck -> ck.getCclass().equals(styleValue)).collect(Collectors.toList());
                    }
                    List<CodeKemu> finalNewDeptCodeList = newDeptCodeList;

                    // 查询客户辅助核算凭证
                    return accvoucherRepository.findAllByIyearFuzhuAccvoucher(year,year+"01", endDate).collectList()
                            .flatMap(accvoucherlist -> {
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
                                            return customerRepository.findAllOrderByCustCode().collectList()
                                                    .flatMap(cuslist->{
                                                        return Mono.just(
                                                                CollectOfUtils.mapof("qclist", qcmap.get("tablesData"), "deptAccVoucherList", finalNewaccvoucherlist, "codeList", finalNewDeptCodeList,"cuslist",cuslist)
                                                        );
                                                    });
                                        });
                            });
                })
                .flatMap(map->{
                    List<CodeKemu> codeList = (List<CodeKemu>) map.get("codeList");  // 都是末级科目
                    List<SubjectInitialBalanceVo> qclist = (List<SubjectInitialBalanceVo>) map.get("qclist");
                    List<DeptCodeAccvoucherVo> pzlist = (List<DeptCodeAccvoucherVo>) map.get("deptAccVoucherList");
                    List<Customer> cuslist = (List<Customer>) map.get("cuslist");
                    // 封装list
                    List<KeMuBalanceVo> mapList = new ArrayList<>();
                    cuslist.stream().forEach(cus->{
                        codeList.stream().forEach(ck -> {
                            KeMuBalanceVo vo = new KeMuBalanceVo();
                            String ccode = ck.getCcode();
                            String ccodeName = ck.getCcodeName();
                            String cclass = ck.getCclass();
                            String bprogerty = ck.getBprogerty();  // 方向
                            String findByBend = ck.getBend();      // 末级
                            String unitMeasurement = ck.getMenterage();
                            String foreignCurrency = ck.getCurrencyType();

                            // ************* 获取科目期初 *************
                            List<SubjectInitialBalanceVo> findByCodeQcList = qclist.stream().filter(qc -> StrUtil.isNotBlank(qc.getCcusId()) && qc.getCcode().equals(ck.getCcode())&& qc.getCcusId().equals(cus.getUniqueCode())).collect(Collectors.toList());
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
                                List<DeptCodeAccvoucherVo> lastpz = pzlist.stream().filter(last ->last.getCcusId().equals(cus.getUniqueCode()) && last.getCcode().startsWith(ck.getCcode()) && Integer.valueOf(last.getImonth()) <= Integer.valueOf(lastmonth)).collect(Collectors.toList());
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
                            List<DeptCodeAccvoucherVo> newpzlist = pzlist.stream().filter(pz ->StrUtil.isNotBlank(pz.getCcusId()) && pz.getCcusId().equals(cus.getUniqueCode()) && pz.getCcode().startsWith(ck.getCcode()) && Integer.valueOf(pz.getIyperiod()) >= Integer.valueOf(strDate) && Integer.valueOf(pz.getIyperiod()) <= Integer.valueOf(endDate)).collect(Collectors.toList());
                            BigDecimal pzMd =newpzlist.size()>0?newpzlist.stream().map(DeptCodeAccvoucherVo::getMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                            BigDecimal pzMc =newpzlist.size()>0?newpzlist.stream().map(DeptCodeAccvoucherVo::getMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                            BigDecimal pzNfratMd =newpzlist.size()>0?newpzlist.stream().map(DeptCodeAccvoucherVo::getNfratMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                            BigDecimal pzNfratMc =newpzlist.size()>0?newpzlist.stream().map(DeptCodeAccvoucherVo::getNfratMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                            BigDecimal pzNdS=newpzlist.size()>0?newpzlist.stream().map(DeptCodeAccvoucherVo::getNdS).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                            BigDecimal pzNcS=newpzlist.size()>0?newpzlist.stream().map(DeptCodeAccvoucherVo::getNcS).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                            BigDecimal pzNum=new BigDecimal(0);
                            BigDecimal pzNfrat=new BigDecimal(0);

                            // 获取累计发生 【月份：<= endDate】
                            List<DeptCodeAccvoucherVo> pzljlist = pzlist.stream().filter(pz ->StrUtil.isNotBlank(pz.getCcusId()) &&pz.getCcusId().equals(cus.getUniqueCode()) && pz.getCcode().startsWith(ck.getCcode()) && Integer.valueOf(pz.getIyperiod()) <= Integer.valueOf(endDate)).collect(Collectors.toList());
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
                                pzNum=pzNcS.subtract(qcNdS);
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
                                vo.setCcode(ccode).setCcodeName(ccodeName).setCclass(cclass).setTemp1(cus.getCustCode()).setTemp2(cus.getCustName())
                                        .setFindByBend(findByBend).setUnitMeasurement(unitMeasurement).setForeignCurrency(foreignCurrency)
                                        .setQcMd(qcMd).setQcMc(qcMc).setQcNum(qcNum).setQcNfrat(qcNfrat)
                                        .setPzMd(pzMd).setPzMc(pzMc).setPzNum(pzNum).setPzNfrat(pzNfrat)
                                        .setLjMd(pzLjMd).setLjMc(pzLjMc).setLjNum(pzLjNum).setLjNfrat(pzLjNfrat)
                                        .setQmMd(qmMd).setQmMc(qmMc).setQmNum(qmNum).setQmNfrat(qmNfrat);
                                mapList.add(vo);
                            }
                        });
                        BigDecimal totalQcMdTotal=mapList.stream().filter(f->f.getTemp1().equals(cus.getCustCode())).map(KeMuBalanceVo::getQcMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                        BigDecimal totalQcMcTotal=mapList.stream().filter(f->f.getTemp1().equals(cus.getCustCode())).map(KeMuBalanceVo::getQcMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);

                        BigDecimal totalQcNumTotal=mapList.stream().filter(f->f.getTemp1().equals(cus.getCustCode())).map(KeMuBalanceVo::getQcNum).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                        BigDecimal totalQcNfratTotal=mapList.stream().filter(f->f.getTemp1().equals(cus.getCustCode())).map(KeMuBalanceVo::getQcNfrat).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);

                        BigDecimal totalPzMdTotal= mapList.stream().filter(f->f.getTemp1().equals(cus.getCustCode())).map(KeMuBalanceVo::getPzMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                        BigDecimal totalPzMcTotal= mapList.stream().filter(f->f.getTemp1().equals(cus.getCustCode())).map(KeMuBalanceVo::getPzMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                        BigDecimal totalPzNumTotal= mapList.stream().filter(f->f.getTemp1().equals(cus.getCustCode())).map(KeMuBalanceVo::getPzNum).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                        BigDecimal totalPzNfratTotal= mapList.stream().filter(f->f.getTemp1().equals(cus.getCustCode())).map(KeMuBalanceVo::getPzNfrat).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);

                        BigDecimal totalLjMdTotal=mapList.stream().filter(f->f.getTemp1().equals(cus.getCustCode())).map(KeMuBalanceVo::getLjMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                        BigDecimal totalLjMcTotal= mapList.stream().filter(f->f.getTemp1().equals(cus.getCustCode())).map(KeMuBalanceVo::getLjMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                        BigDecimal totalLjNumTotal= mapList.stream().filter(f->f.getTemp1().equals(cus.getCustCode())).map(KeMuBalanceVo::getLjNum).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);;
                        BigDecimal totalLjNfratTotal= mapList.stream().filter(f->f.getTemp1().equals(cus.getCustCode())).map(KeMuBalanceVo::getLjNfrat).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);;

                        BigDecimal totalQmNumTotal=  mapList.stream().filter(f->f.getTemp1().equals(cus.getCustCode())).map(KeMuBalanceVo::getQmNum).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                        BigDecimal totalQmNfratTotal= mapList.stream().filter(f->f.getTemp1().equals(cus.getCustCode())).map(KeMuBalanceVo::getQmNfrat).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);

                        BigDecimal totalQmMdTotal=  mapList.stream().filter(f->f.getTemp1().equals(cus.getCustCode())).map(KeMuBalanceVo::getQmMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                        BigDecimal totalQmMcTotal= mapList.stream().filter(f->f.getTemp1().equals(cus.getCustCode())).map(KeMuBalanceVo::getQmMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);

                        BigDecimal total_qmyuemd =new BigDecimal(0);
                        BigDecimal total_qmyuemc =new BigDecimal(0);

                        BigDecimal total_qcyuemd =new BigDecimal(0);
                        BigDecimal total_qcyuemc =new BigDecimal(0);

                        // 计算规则：比较 借贷方金额大小，得出余额 大于0 借方，反之
                        // 借小于贷
                        if(totalQmMdTotal.compareTo(totalQmMcTotal) == -1){
                            BigDecimal yue =totalQmMcTotal.subtract(totalQmMdTotal);
                            if(yue.compareTo(BigDecimal.ZERO)<0){
                                total_qmyuemd=yue.multiply(new BigDecimal(-1));
                            }else{
                                total_qmyuemc=yue;
                            }
                        }
                        if(totalQcMdTotal.compareTo(totalQcMcTotal) == -1){
                            BigDecimal yue =totalQcMcTotal.subtract(totalQcMdTotal);
                            if(yue.compareTo(BigDecimal.ZERO)<0){
                                total_qcyuemd=yue.multiply(new BigDecimal(-1));
                            }else{
                                total_qcyuemc=yue;
                            }
                        }

                        // 借大于贷
                        if(totalQmMdTotal.compareTo(totalQmMcTotal) == 1){
                            BigDecimal yue =totalQmMdTotal.subtract(totalQmMcTotal);
                            if(yue.compareTo(BigDecimal.ZERO)<0){
                                total_qmyuemc=yue.multiply(new BigDecimal(-1));
                            }else{
                                total_qmyuemd=yue;
                            }
                        }
                        if(totalQcMdTotal.compareTo(totalQcMcTotal) == 1){
                            BigDecimal yue =totalQcMdTotal.subtract(totalQcMcTotal);
                            if(yue.compareTo(BigDecimal.ZERO)<0){
                                total_qcyuemc=yue.multiply(new BigDecimal(-1));
                            }else{
                                total_qcyuemd=yue;
                            }
                        }

                        KeMuBalanceVo vo=new KeMuBalanceVo();
                        if(
                            total_qcyuemd.compareTo(BigDecimal.ZERO)==0 &&
                            total_qcyuemc.compareTo(BigDecimal.ZERO)==0 &&
                            totalPzMdTotal.compareTo(BigDecimal.ZERO)==0 &&
                            totalPzMcTotal.compareTo(BigDecimal.ZERO)==0 &&
                            total_qmyuemd.compareTo(BigDecimal.ZERO)==0 &&
                            total_qmyuemc.compareTo(BigDecimal.ZERO)==0
                        ){}
                        else{
                            vo.setCcode("合计").setFindByBend("0").setTemp1("").setTemp2("")
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

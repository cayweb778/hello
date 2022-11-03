package org.boozsoft.service.impl.fuzhu.pro.mx;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.boozsoft.domain.entity.codekemu.CodeKemu;
import org.boozsoft.domain.entity.share.project.base.Project;
import org.boozsoft.domain.vo.DeptCodeAccvoucherVo;
import org.boozsoft.domain.vo.KeMuMingXiBigDecimalVo;
import org.boozsoft.domain.vo.ProjectVo;
import org.boozsoft.domain.vo.SubjectInitialBalanceVo;
import org.boozsoft.repo.AccvoucherRepository;
import org.boozsoft.repo.SysPeriodRepository;
import org.boozsoft.repo.accstyle.AccStyleRepository;
import org.boozsoft.repo.codekemu.CodeKemuRepository;
import org.boozsoft.repo.project.base.ProjectRepositoryBase;
import org.boozsoft.service.impl.SubjectInitialFuZhuBalanceServiceImpl;
import org.boozsoft.util.BigDecimalUtils;
import org.boozsoft.util.addLeftZero;
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

/**
 * @ClassName : KeMuMingXiServiceImpl
 * @Description : 科目明细账
 * @Author : miao
 * @Date: 2021-06-23 14:57
 */
@Slf4j
@Service
public class ProKmMxServiceImpl {

    @Autowired
    SubjectInitialFuZhuBalanceServiceImpl fuZhuBalanceService;
    @Autowired
    AccvoucherRepository accvoucherRepository;
    @Autowired
    CodeKemuRepository codeKemuRepository;
    @Autowired
    AccStyleRepository accStyleRepository;
    @Autowired
    ProjectRepositoryBase projectRepositoryBase;

    /**
     * 查询科目明细账
     **/
    public Mono<R> findAll(Pageable pageable,String minDept, String km, String strDate, String endDate, String bz, String ishaveRjz,
                           String styleValue, Map<String, String> searchMap, Map<String, String> filterMap,String database) {
        String year = strDate.substring(0, 4);
        String month = strDate.substring(4, 6);
        AtomicReference<Long> totalAR = new AtomicReference(0);
        // 1、获取项目辅助核算的科目
        return codeKemuRepository.findByBitemAndIyearOrderByCcodeAsc("1",year).collectList()
                .flatMap(deptCodeList->{
                    List<CodeKemu> newDeptCodeList=deptCodeList;
                    //  2、过滤包含此科目的末级科目
                    if(StringUtils.isNotBlank(km)){
                        newDeptCodeList=deptCodeList.stream().filter(ck->ck.getCcode().startsWith(km)&&ck.getBend().equals("1")).collect(Collectors.toList());
                    }
                    // 3-1、根据科目类型查找   财务会计【1-5】 预算会计【6-8】 先写死
                    if("财务会计".equals(styleValue)){
                        newDeptCodeList=newDeptCodeList.stream().filter(ck->Integer.valueOf(ck.getCcode().substring(0,1))<=5).collect(Collectors.toList());
                    }
                    // 3-2、根据科目类型查找   财务会计【1-5】 预算会计【6-8】 先写死
                   else if("预算会计".equals(styleValue)){
                        newDeptCodeList=newDeptCodeList.stream().filter(ck->Integer.valueOf(ck.getCcode().substring(0,1))>=6&&Integer.valueOf(ck.getCcode().substring(0,1))<=8).collect(Collectors.toList());
                    }
                    // 3-3、根据科目类型查找
                  else if(!"全部".equals(styleValue)){
                        newDeptCodeList=newDeptCodeList.stream().filter(ck->ck.getCclass().equals(styleValue)).collect(Collectors.toList());
                    }

                    List<CodeKemu> finalNewDeptCodeList = newDeptCodeList;
                    // 获取项目的凭证
                    return accvoucherRepository.findAllByIyearAndItemId(year,minDept,year+"01",endDate).collectList()
                      .flatMap(accvoucherlist->{
                          List<DeptCodeAccvoucherVo> newaccvoucherlist=accvoucherlist;
                          // 根据币种查询凭证
                          if(!"全部".equals(bz)){
                              newaccvoucherlist=accvoucherlist.stream().filter(a->a.getForeignCurrency().equals(bz)).collect(Collectors.toList());
                          }
                          // ****** 是否包含未记账【查询条件】 *****不包含；只获取 已记账的凭证
                          if (ishaveRjz.equals("0")) {
                              newaccvoucherlist = accvoucherlist.stream().filter(vo -> StringUtils.isNotBlank(vo.getIbook()) && vo.getIbook().equals("1")).collect(Collectors.toList());
                          }
                          List<DeptCodeAccvoucherVo> finalNewaccvoucherlist = newaccvoucherlist;
                          return fuZhuBalanceService.findAllSubjectInitialFuZhuBalance(year, "false",year+"21",database)
                                  .flatMap(qclist->{
                                      Map qcmap = (Map) qclist.getResult();
                                      return Mono.just(
                                              CollectOfUtils.mapof("qclist",qcmap.get("tablesData"),"deptAccVoucherList", finalNewaccvoucherlist,"codeList", finalNewDeptCodeList)
                                      );
                                  });
                      });
                })
                // 获取部门科目凭证
                .flatMap(map->{
                    List<KeMuMingXiBigDecimalVo> mapList = new ArrayList<>();
                    List<CodeKemu> codeList= (List<CodeKemu>) map.get("codeList");  // 都是末级科目
                    List<SubjectInitialBalanceVo> qclist = (List<SubjectInitialBalanceVo>) map.get("qclist");
                    List<DeptCodeAccvoucherVo> pzlist= (List<DeptCodeAccvoucherVo>) map.get("deptAccVoucherList");

                    int number = 0; // 序号
                    AtomicReference<BigDecimal> lastYue= new AtomicReference<>(new BigDecimal(0));
                    AtomicReference<BigDecimal> lastYue_num= new AtomicReference<>(new BigDecimal(0));
                    AtomicReference<BigDecimal> lastYue_nfrat= new AtomicReference<>(new BigDecimal(0));
                    AtomicReference<BigDecimal> QcCode= new AtomicReference<>(new BigDecimal(0));
                    codeList.stream().forEach(ck->{
                        BigDecimal codeTotalMd=new BigDecimal(0);
                        BigDecimal codeTotalMc=new BigDecimal(0);
                        BigDecimal codeTotalNds=new BigDecimal(0);
                        BigDecimal codeTotalNcs=new BigDecimal(0);
                        BigDecimal codeTotalNfratMd=new BigDecimal(0);
                        BigDecimal codeTotalNfratMc=new BigDecimal(0);

                        // 科目的计量单位
                        List<CodeKemu> unitMeasurementlist = codeList.stream().filter(c -> c.getCcode().equals(ck.getCcode())).collect(Collectors.toList());
                        String unitMeasurement=unitMeasurementlist.size()>0?unitMeasurementlist.get(0).getMenterage():"";
                        // 科目的币种名称
                        List<CodeKemu> foreignCurrencylist = codeList.stream().filter(c -> c.getCcode().equals(ck.getCcode())).collect(Collectors.toList());
                        String foreignCurrency =foreignCurrencylist.size()>0?foreignCurrencylist.get(0).getCurrencyType():"";

                        // ************* 获取科目期初 *************
                        List<SubjectInitialBalanceVo> findByCodeQcList = qclist.stream().filter(qc ->StrUtil.isNotBlank(qc.getProjectId()) && qc.getCcode().equals(ck.getCcode())&& qc.getProjectId().equals(minDept)).collect(Collectors.toList());
                        BigDecimal qcmd=findByCodeQcList.size()>0?findByCodeQcList.get(0).getMd():new BigDecimal(0);
                        BigDecimal qcmc=findByCodeQcList.size()>0?findByCodeQcList.get(0).getMc():new BigDecimal(0);
                        BigDecimal qcndS=findByCodeQcList.size()>0?findByCodeQcList.get(0).getNdS():new BigDecimal(0);
                        BigDecimal qcncS=findByCodeQcList.size()>0?findByCodeQcList.get(0).getNcS():new BigDecimal(0);
                        BigDecimal qcnfratMd=findByCodeQcList.size()>0?findByCodeQcList.get(0).getNfratMd():new BigDecimal(0);
                        BigDecimal qcnfratMc=findByCodeQcList.size()>0?findByCodeQcList.get(0).getNfratMc():new BigDecimal(0);

                        BigDecimal yue=ck.getBprogerty().equals("1")?qcmd.subtract(qcmc):qcmc.subtract(qcmd);
                        BigDecimal yue_num=ck.getBprogerty().equals("1")?qcndS.subtract(qcncS):qcncS.subtract(qcndS);
                        BigDecimal yue_nfrat=ck.getBprogerty().equals("1")?qcnfratMd.subtract(qcnfratMc):qcnfratMc.subtract(qcnfratMd);

                        BigDecimal lastmd=new BigDecimal(0);
                        BigDecimal lastmc=new BigDecimal(0);
                        BigDecimal lastndS=new BigDecimal(0);
                        BigDecimal lastncS=new BigDecimal(0);
                        BigDecimal lastnfratMd=new BigDecimal(0);
                        BigDecimal lastnfratMc=new BigDecimal(0);

                        // 期间或日期 不是从一月开始的 计算出上月的期末余额
                        if (Integer.valueOf(strDate.substring(4, 6)) > 1) {
                            String lastmonth = Integer.valueOf(month) - 1 < 10 ? "0" + (Integer.valueOf(month) - 1) : month;
                            // 获取上个月凭证发生数
                            List<DeptCodeAccvoucherVo> lastMonthAccVoucher = pzlist.stream().filter(acv -> Integer.valueOf(acv.getImonth()) <= Integer.valueOf(lastmonth) && acv.getCcode().equals(ck.getCcode())).collect(Collectors.toList());
                            lastmd=lastMonthAccVoucher.size()>0?lastMonthAccVoucher.stream().map(DeptCodeAccvoucherVo::getMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                            lastmc=lastMonthAccVoucher.size()>0?lastMonthAccVoucher.stream().map(DeptCodeAccvoucherVo::getMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                            lastndS=lastMonthAccVoucher.size()>0?lastMonthAccVoucher.stream().map(DeptCodeAccvoucherVo::getNdS).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                            lastncS=lastMonthAccVoucher.size()>0?lastMonthAccVoucher.stream().map(DeptCodeAccvoucherVo::getNcS).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                            lastnfratMd=lastMonthAccVoucher.size()>0?lastMonthAccVoucher.stream().map(DeptCodeAccvoucherVo::getNfratMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                            lastnfratMc=lastMonthAccVoucher.size()>0?lastMonthAccVoucher.stream().map(DeptCodeAccvoucherVo::getNfratMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                        }
                        String qc_bprogerty="";
                        // 余额
                        if (ck.getBprogerty().equals("1")) {
                            yue = qcmd.add(lastmd).subtract(qcmc.add(lastmc));
                            yue_num = qcndS.add(lastndS).subtract(qcncS.add(lastncS));
                            yue_nfrat = qcnfratMd.add(lastnfratMd).subtract(qcnfratMc.add(lastnfratMc));
                            if(yue.compareTo(BigDecimal.ZERO) == 0){
                                qc_bprogerty="平";
                            }else if(yue.compareTo(BigDecimal.ZERO)>0){
                                qc_bprogerty="借";
                            }else{
                                qc_bprogerty="贷";
                            }
                        } else {
                            yue = qcmc.add(lastmc).subtract(qcmd.add(lastmd));
                            yue_num = qcncS.add(lastncS).subtract(qcndS.add(lastndS));
                            yue_nfrat = qcnfratMc.add(lastnfratMc).subtract(qcnfratMd.add(lastnfratMd));
                            if(yue.compareTo(BigDecimal.ZERO) == 0){
                                qc_bprogerty="平";
                            }else if(yue.compareTo(BigDecimal.ZERO)>0){
                                qc_bprogerty="贷";
                            }else{
                                qc_bprogerty="借";
                            }
                        }

                        QcCode.set(yue);
                        if(yue.compareTo(BigDecimal.ZERO)!=0){
                            mapList.add(
                                    new KeMuMingXiBigDecimalVo()
                                            .setNumber(number)
                                            .setInoIdASC(0)
                                            .setCcode(ck.getCcode())
                                            .setCcodeName(ck.getCcodeName())
                                            .setCdigest("期初余额")
                                            .setBprogerty(qc_bprogerty)
                                            .setYue(yue.compareTo(BigDecimal.ZERO) < 0?yue.multiply(new BigDecimal(-1)):yue)
                                            .setNcnum(yue_num.compareTo(BigDecimal.ZERO) < 0?yue_num.multiply(new BigDecimal(-1)):yue_num)
                                            .setYue_num(yue_num.compareTo(BigDecimal.ZERO) < 0?yue_num.multiply(new BigDecimal(-1)):yue_num)
                                            .setYue_nfrat(yue_nfrat.compareTo(BigDecimal.ZERO) < 0?yue_nfrat.multiply(new BigDecimal(-1)):yue_nfrat)
                            );
                        }

                        // ************* 获取科目凭证 *************
                        List<DeptCodeAccvoucherVo> findByCodePzList = pzlist.stream().filter(pz -> pz.getCcode().equals(ck.getCcode())).collect(Collectors.toList());
                        for (int i = 0; i < findByCodePzList.size(); i++) {
                            BigDecimal pzmd=findByCodePzList.size()>0?findByCodePzList.get(i).getMd():new BigDecimal(0);
                            BigDecimal pzmc=findByCodePzList.size()>0?findByCodePzList.get(i).getMc():new BigDecimal(0);
                            BigDecimal pzndS=findByCodePzList.size()>0?findByCodePzList.get(i).getNdS():new BigDecimal(0);
                            BigDecimal pzncS=findByCodePzList.size()>0?findByCodePzList.get(i).getNcS():new BigDecimal(0);
                            BigDecimal pznfratMd=findByCodePzList.size()>0?findByCodePzList.get(i).getNfratMd():new BigDecimal(0);
                            BigDecimal pznfratMc=findByCodePzList.size()>0?findByCodePzList.get(i).getNfratMc():new BigDecimal(0);

                            // 合计
                            codeTotalMd=codeTotalMd.add(pzmd);
                            codeTotalMc=codeTotalMc.add(pzmc);
                            codeTotalNds=codeTotalNds.add(pzndS);
                            codeTotalNcs=codeTotalNcs.add(pzncS);
                            codeTotalNfratMd=codeTotalNfratMd.add(pznfratMd);
                            codeTotalNfratMc=codeTotalNfratMc.add(pznfratMc);

                            String bprogerty="";
                            if(ck.getBprogerty().equals("1")){
                                yue=yue.add(pzmd).subtract(pzmc);
                                yue_num=yue_num.add(pzndS).subtract(pzncS);
                                yue_nfrat.add(yue_nfrat).subtract(pznfratMc);

                                if(yue.compareTo(BigDecimal.ZERO) == 0){
                                    bprogerty="平";
                                }else if(yue.compareTo(BigDecimal.ZERO)>0){
                                    bprogerty="借";
                                }else{
                                    bprogerty="贷";
                                }
                            }else{
                                yue=yue.add(pzmc).subtract(pzmd);
                                yue_num=yue_num.add(pzncS).subtract(pzndS);
                                yue_nfrat=yue_nfrat.add(pznfratMc).subtract(pznfratMd);

                                if(yue.compareTo(BigDecimal.ZERO) == 0){
                                    bprogerty="平";
                                }else if(yue.compareTo(BigDecimal.ZERO)>0){
                                    bprogerty="贷";
                                }else{
                                    bprogerty="借";
                                }
                            }

                            BigDecimal tempYue=yue.compareTo(BigDecimal.ZERO) < 0 ? yue.multiply(new BigDecimal(-1)) : yue;
                            BigDecimal tempYueNfract=yue_nfrat.compareTo(BigDecimal.ZERO) > 0 ? yue_nfrat : yue_nfrat.multiply(new BigDecimal(-1));

                            if(pzmd.compareTo(BigDecimal.ZERO)!=0 || pzmc.compareTo(BigDecimal.ZERO)!=0) {
                                KeMuMingXiBigDecimalVo mx=new KeMuMingXiBigDecimalVo();
                                mx.setNumber(i+1)
                                        .setCcode(findByCodePzList.get(i).getCcode())
                                        .setCcodeName(findByCodePzList.get(i).getCcodeName())
                                        .setDbillDate(findByCodePzList.get(i).getDbillDate())
                                        .setInoIdASC(Integer.valueOf(findByCodePzList.get(i).getInoId()))
                                        .setInoId(findByCodePzList.get(i).getCsign() + "-" + addLeftZero.addZeroForNum(findByCodePzList.get(i).getInoId(), 4))
                                        .setCdigest(findByCodePzList.get(i).getCdigest())
                                        .setMd(pzmd)
                                        .setMc(pzmc)
                                        .setBprogerty(bprogerty)
                                        .setNdS(pzndS)
                                        .setNcS(pzncS)
                                        .setCunitPrice(new BigDecimal(findByCodePzList.get(i).getCunitPrice()))
                                        .setUnitMeasurement(unitMeasurement)
                                        .setMdF(new BigDecimal(findByCodePzList.get(i).getMdF()))
                                        .setNfrat_md(pznfratMd)
                                        .setNfrat_mc(pznfratMc)
                                        .setForeignCurrency(foreignCurrency)
                                        .setYue(tempYue)
                                        .setYue_num(yue_num)
                                        .setYue_nfrat(tempYueNfract);

                                mapList.add(mx);
                                lastYue.set(tempYue);
                                lastYue_num.set(yue_num);
                                lastYue_nfrat.set(tempYueNfract);
                            }
                        }

                        if(codeTotalMd.compareTo(BigDecimal.ZERO)!=0 || codeTotalMc.compareTo(BigDecimal.ZERO)!=0) {
                            mapList.add(new KeMuMingXiBigDecimalVo()
                                .setNumber(number+1)
                                .setInoIdASC(0)
                                .setCdigest("当前合计")
                                .setCcode(ck.getCcode())
                                .setCcodeName(ck.getCcodeName())
                                .setMd(codeTotalMd)
                                .setMc(codeTotalMc)
                                .setNdS(codeTotalNds)
                                .setNcS(codeTotalNcs)
                                .setBprogerty(lastYue.get().compareTo(BigDecimal.ZERO) == 0 ? "平" : lastYue.get().compareTo(BigDecimal.ZERO) > 0 ? "借" : "贷")
                                .setYue(lastYue.get().compareTo(BigDecimal.ZERO) > 0 ? lastYue.get() : yue_nfrat.multiply(new BigDecimal(-1)))
                                .setYue_num(lastYue_num.get())
                                .setYue_nfrat(lastYue_nfrat.get())
                                .setNfrat_md(codeTotalNfratMd)
                                .setNfrat_mc(codeTotalNfratMc)
                            );
                        }
                        if(yue.compareTo(BigDecimal.ZERO)!=0 && codeTotalMd.compareTo(BigDecimal.ZERO)==0){
                            mapList.add(
                                    new KeMuMingXiBigDecimalVo()
                                            .setNumber(number+1)
                                            .setInoIdASC(0)
                                            .setCcode(ck.getCcode())
                                            .setCcodeName(ck.getCcodeName())
                                            .setCdigest("当前合计")
                                            .setBprogerty(qc_bprogerty)
                                            .setYue(yue.compareTo(BigDecimal.ZERO) < 0?yue.multiply(new BigDecimal(-1)):yue)
                                            .setTempyue(yue)
                                            .setNcnum(yue_num.compareTo(BigDecimal.ZERO) < 0?yue_num.multiply(new BigDecimal(-1)):yue_num)
                                            .setYue_num(yue_num.compareTo(BigDecimal.ZERO) < 0?yue_num.multiply(new BigDecimal(-1)):yue_num)
                                            .setYue_nfrat(yue_nfrat.compareTo(BigDecimal.ZERO) < 0?yue_nfrat.multiply(new BigDecimal(-1)):yue_nfrat)
                            );
                        }
                    });

                    // 总计
                    BigDecimal totalMd=mapList.stream().filter(f->f.getCdigest().equals("当前合计")).map(KeMuMingXiBigDecimalVo::getMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                    BigDecimal totalMc=mapList.stream().filter(f->f.getCdigest().equals("当前合计")).map(KeMuMingXiBigDecimalVo::getMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                    BigDecimal totalNds=mapList.stream().filter(f->f.getCdigest().equals("当前合计")).map(KeMuMingXiBigDecimalVo::getNdS).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                    BigDecimal totalNcs=mapList.stream().filter(f->f.getCdigest().equals("当前合计")).map(KeMuMingXiBigDecimalVo::getNcS).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                    BigDecimal totalNfratMd=mapList.stream().filter(f->f.getCdigest().equals("当前合计")).map(KeMuMingXiBigDecimalVo::getNfrat_md).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                    BigDecimal totalNfratMc=mapList.stream().filter(f->f.getCdigest().equals("当前合计")).map(KeMuMingXiBigDecimalVo::getNfrat_mc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);

                    BigDecimal totalYue=mapList.stream().filter(f->f.getCdigest().equals("当前合计")).map(KeMuMingXiBigDecimalVo::getTempyue).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                    String bprogerty=totalYue.compareTo(BigDecimal.ZERO) == 0 ? "平" : totalYue.compareTo(BigDecimal.ZERO) > 0 ? "借" : "贷";
                    if(totalMd.compareTo(BigDecimal.ZERO)!=0 || totalMc.compareTo(BigDecimal.ZERO)!=0) {
                        mapList.add(new KeMuMingXiBigDecimalVo()
                                .setNumber(number)
                                .setInoIdASC(0)
                                .setCdigest("总计")
                                .setBprogerty(bprogerty)
                                .setMd(totalMd)
                                .setMc(totalMc)
                                .setNdS(totalNds)
                                .setNcS(totalNcs)
                                .setYue(totalYue.compareTo(BigDecimal.ZERO) > 0 ? totalYue : totalYue.multiply(new BigDecimal(-1)))
                                .setYue_num(QcCode.get().add(totalNds).subtract(totalNcs))
                                .setYue_nfrat(QcCode.get().add(totalNfratMd).subtract(totalNfratMc))
                                .setNfrat_md(totalNfratMd)
                                .setNfrat_mc(totalNfratMc)
                        );
                    }
                    totalAR.set((long) mapList.size());
                    return Mono.just(
                            mapList.stream().filter(item->{
                                // 按条件过滤
                                if (org.springbooz.core.tool.utils.StringUtils.isNotBlank(searchMap.get("requirement")) && org.springbooz.core.tool.utils.StringUtils.isNotBlank(searchMap.get("value"))) {
                                    String value = searchMap.get("value");
                                    if (searchMap.get("requirement").trim().equals("bprogerty")) {
                                        if (!item.getBprogerty().contains(value) && !item.getBprogerty().contains(value)) {
                                            return false;
                                        }
                                    } else {
                                        String dbValue = getFieldValueByFieldName(searchMap.get("requirement").trim(), item);
                                        if (Objects.nonNull(dbValue) && !dbValue.contains(value)) {
                                            return false;
                                        }
                                    }
                                }

                                // 过滤漏斗-借方
                                if (org.springbooz.core.tool.utils.StringUtils.isNotBlank(filterMap.get("amountMinJf")) && org.springbooz.core.tool.utils.StringUtils.isNotBlank(filterMap.get("amountMaxJf"))) {
                                    BigDecimal min = new BigDecimal(filterMap.get("amountMinJf"));
                                    BigDecimal max = new BigDecimal(filterMap.get("amountMaxJf"));
                                    BigDecimal s_md = new BigDecimal(item.getMd().toString().replaceAll(",",""));
                                    if (s_md.compareTo(min) < 0 || s_md.compareTo(max) > 0) {
                                        return false;
                                    }
                                }
                                // 过滤漏斗-贷方
                                if (org.springbooz.core.tool.utils.StringUtils.isNotBlank(filterMap.get("amountMinDf")) && org.springbooz.core.tool.utils.StringUtils.isNotBlank(filterMap.get("amountMaxDf"))) {
                                    BigDecimal min = new BigDecimal(filterMap.get("amountMinDf"));
                                    BigDecimal max = new BigDecimal(filterMap.get("amountMaxDf"));
                                    BigDecimal s_mc = new BigDecimal(item.getMc().toString().replaceAll(",",""));
                                    if (s_mc.compareTo(min) < 0 || s_mc.compareTo(max) > 0) {
                                        return false;
                                    }
                                }
                                // 过滤漏斗-余额
                                if (StrUtil.isNotBlank(filterMap.get("amountMinYe")) && StrUtil.isNotBlank(filterMap.get("amountMaxYe"))) {
                                    BigDecimal min = new BigDecimal(filterMap.get("amountMinYe"));
                                    BigDecimal max = new BigDecimal(filterMap.get("amountMaxYe"));
                                    BigDecimal s_yue = new BigDecimal(item.getYue().toString().replaceAll(",",""));
                                    //余额范围
                                    if (s_yue.compareTo(min) < 0 || s_yue.compareTo(max) > 0) {
                                        return false;
                                    }
                                }
                                return true;
                            })
                    );
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
    public Mono<R> exportAll(String strDate, String endDate, String bz, String ishaveRjz,String styleValue,List<String> proList) {
        String year = strDate.substring(0, 4);
        String month = strDate.substring(4, 6);
        // 封装list
        List<Map<String,Object>> excellist = new ArrayList<>();
        // 1、获取项目辅助核算的科目
        return codeKemuRepository.findByBitemAndIyearOrderByCcodeAsc("1",year).collectList()
                .flatMap(deptCodeList->{
                    List<CodeKemu> newDeptCodeList=deptCodeList;
                    // 3-1、根据科目类型查找   财务会计【1-5】 预算会计【6-8】 先写死
                    if("财务会计".equals(styleValue)){
                        newDeptCodeList=newDeptCodeList.stream().filter(ck->Integer.valueOf(ck.getCcode().substring(0,1))<=5).collect(Collectors.toList());
                    }
                    // 3-2、根据科目类型查找   财务会计【1-5】 预算会计【6-8】 先写死
                    else if("预算会计".equals(styleValue)){
                        newDeptCodeList=newDeptCodeList.stream().filter(ck->Integer.valueOf(ck.getCcode().substring(0,1))>=6&&Integer.valueOf(ck.getCcode().substring(0,1))<=8).collect(Collectors.toList());
                    }
                    // 3-3、根据科目类型查找
                    else if(!"全部".equals(styleValue)){
                        newDeptCodeList=newDeptCodeList.stream().filter(ck->ck.getCclass().equals(styleValue)).collect(Collectors.toList());
                    }

                    List<CodeKemu> finalNewDeptCodeList = newDeptCodeList;
                    // 获取项目的凭证
                    return accvoucherRepository.findAllByIyearFuzhuAccvoucher(year,year+"01",endDate).collectList()
                        .flatMap(accvoucherlist->{
                            List<DeptCodeAccvoucherVo> newaccvoucherlist=accvoucherlist;
                            // 根据币种查询凭证
                            if(!"全部".equals(bz)){
                                newaccvoucherlist=accvoucherlist.stream().filter(a->a.getForeignCurrency().equals(bz)).collect(Collectors.toList());
                            }
                            // ****** 是否包含未记账【查询条件】 *****不包含；只获取 已记账的凭证
                            if (ishaveRjz.equals("0")) {
                                newaccvoucherlist = accvoucherlist.stream().filter(vo -> StringUtils.isNotBlank(vo.getIbook()) && vo.getIbook().equals("1")).collect(Collectors.toList());
                            }
                            List<DeptCodeAccvoucherVo> finalNewaccvoucherlist = newaccvoucherlist;
                            return fuZhuBalanceService.findAllSubjectInitialFuZhuBalance(year, "false",year+"21","")
                                .flatMap(qclist->{
                                    Map qcmap = (Map) qclist.getResult();
                                    return projectRepositoryBase.findAllOrderByProCode().collectList()
                                        .flatMap(prolist->{
                                            return projectRepositoryBase.findAllOrderByProCode2().collectList()
                                                .flatMap(pzProCodeList->{
                                                    return Mono.just(
                                                            CollectOfUtils.mapof("qclist",qcmap.get("tablesData"),"deptAccVoucherList",
                                                                    finalNewaccvoucherlist,"codeList", finalNewDeptCodeList,
                                                                    "prolist",prolist,"pzProCodeList",pzProCodeList)
                                                    );
                                                });
                                        });
                                });
                        });
                })
                // 获取部门科目凭证
                .flatMap(map->{
                    List<CodeKemu> codeList= (List<CodeKemu>) map.get("codeList");  // 都是末级科目
                    List<SubjectInitialBalanceVo> qclist = (List<SubjectInitialBalanceVo>) map.get("qclist");
                    List<DeptCodeAccvoucherVo> pzlist= (List<DeptCodeAccvoucherVo>) map.get("deptAccVoucherList");
                    List<Project> prolist= (List<Project>) map.get("prolist");
                    List<ProjectVo> pzProCodeList= (List<ProjectVo>) map.get("pzProCodeList");

                    proList.stream().forEach(pro->{
                        Project project = prolist.stream().filter(v -> v.getUniqueCode().equals(pro)).collect(Collectors.toList()).get(0);
                        List<String> collect = pzProCodeList.stream().filter(pzpro -> pzpro.getUniqueCode().equals(pro)).map(code -> code.getCcode()).collect(Collectors.toList());
                        List<KeMuMingXiBigDecimalVo> mapList = new ArrayList<>();

                        collect.stream().forEach(code->{
                            AtomicReference<BigDecimal> lastYue= new AtomicReference<>(new BigDecimal(0));
                            AtomicReference<BigDecimal> lastYue_num= new AtomicReference<>(new BigDecimal(0));
                            AtomicReference<BigDecimal> lastYue_nfrat= new AtomicReference<>(new BigDecimal(0));
                            AtomicReference<BigDecimal> QcCode= new AtomicReference<>(new BigDecimal(0));

                            BigDecimal codeTotalMd=new BigDecimal(0);
                            BigDecimal codeTotalMc=new BigDecimal(0);
                            BigDecimal codeTotalNds=new BigDecimal(0);
                            BigDecimal codeTotalNcs=new BigDecimal(0);
                            BigDecimal codeTotalNfratMd=new BigDecimal(0);
                            BigDecimal codeTotalNfratMc=new BigDecimal(0);

                            CodeKemu ck = codeList.stream().filter(f -> f.getCcode().equals(code)).collect(Collectors.toList()).get(0);

                            // 科目的计量单位
                            List<CodeKemu> unitMeasurementlist = codeList.stream().filter(c -> c.getCcode().equals(ck.getCcode())).collect(Collectors.toList());
                            String unitMeasurement=unitMeasurementlist.size()>0?unitMeasurementlist.get(0).getMenterage():"";
                            // 科目的币种名称
                            List<CodeKemu> foreignCurrencylist = codeList.stream().filter(c -> c.getCcode().equals(ck.getCcode())).collect(Collectors.toList());
                            String foreignCurrency =foreignCurrencylist.size()>0?foreignCurrencylist.get(0).getCurrencyType():"";

                            // ************* 获取科目期初 *************
                            List<SubjectInitialBalanceVo> findByCodeQcList = qclist.stream().filter(qc ->StrUtil.isNotBlank(qc.getProjectId()) && qc.getCcode().equals(ck.getCcode())&& qc.getProjectId().equals(pro)).collect(Collectors.toList());
                            BigDecimal qcmd=findByCodeQcList.size()>0?findByCodeQcList.get(0).getMd():new BigDecimal(0);
                            BigDecimal qcmc=findByCodeQcList.size()>0?findByCodeQcList.get(0).getMc():new BigDecimal(0);
                            BigDecimal qcndS=findByCodeQcList.size()>0?findByCodeQcList.get(0).getNdS():new BigDecimal(0);
                            BigDecimal qcncS=findByCodeQcList.size()>0?findByCodeQcList.get(0).getNcS():new BigDecimal(0);
                            BigDecimal qcnfratMd=findByCodeQcList.size()>0?findByCodeQcList.get(0).getNfratMd():new BigDecimal(0);
                            BigDecimal qcnfratMc=findByCodeQcList.size()>0?findByCodeQcList.get(0).getNfratMc():new BigDecimal(0);

                            BigDecimal yue=ck.getBprogerty().equals("1")?qcmd.subtract(qcmc):qcmc.subtract(qcmd);
                            BigDecimal yue_num=ck.getBprogerty().equals("1")?qcndS.subtract(qcncS):qcncS.subtract(qcndS);
                            BigDecimal yue_nfrat=ck.getBprogerty().equals("1")?qcnfratMd.subtract(qcnfratMc):qcnfratMc.subtract(qcnfratMd);

                            BigDecimal lastmd=new BigDecimal(0);
                            BigDecimal lastmc=new BigDecimal(0);
                            BigDecimal lastndS=new BigDecimal(0);
                            BigDecimal lastncS=new BigDecimal(0);
                            BigDecimal lastnfratMd=new BigDecimal(0);
                            BigDecimal lastnfratMc=new BigDecimal(0);

                            // 期间或日期 不是从一月开始的 计算出上月的期末余额
                            if (Integer.valueOf(strDate.substring(4, 6)) > 1) {
                                String lastmonth = Integer.valueOf(month) - 1 < 10 ? "0" + (Integer.valueOf(month) - 1) : month;
                                // 获取上个月凭证发生数
                                List<DeptCodeAccvoucherVo> lastMonthAccVoucher = pzlist.stream().filter(acv ->StrUtil.isNotBlank(acv.getProjectId()) && acv.getProjectId().equals(pro)&& Integer.valueOf(acv.getImonth()) <= Integer.valueOf(lastmonth) && acv.getCcode().equals(ck.getCcode())).collect(Collectors.toList());
                                lastmd=lastMonthAccVoucher.size()>0?lastMonthAccVoucher.stream().map(DeptCodeAccvoucherVo::getMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                                lastmc=lastMonthAccVoucher.size()>0?lastMonthAccVoucher.stream().map(DeptCodeAccvoucherVo::getMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                                lastndS=lastMonthAccVoucher.size()>0?lastMonthAccVoucher.stream().map(DeptCodeAccvoucherVo::getNdS).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                                lastncS=lastMonthAccVoucher.size()>0?lastMonthAccVoucher.stream().map(DeptCodeAccvoucherVo::getNcS).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                                lastnfratMd=lastMonthAccVoucher.size()>0?lastMonthAccVoucher.stream().map(DeptCodeAccvoucherVo::getNfratMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                                lastnfratMc=lastMonthAccVoucher.size()>0?lastMonthAccVoucher.stream().map(DeptCodeAccvoucherVo::getNfratMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                            }
                            String qc_bprogerty="";
                            // 余额
                            if (ck.getBprogerty().equals("1")) {
                                yue = qcmd.add(lastmd).subtract(qcmc.add(lastmc));
                                yue_num = qcndS.add(lastndS).subtract(qcncS.add(lastncS));
                                yue_nfrat = qcnfratMd.add(lastnfratMd).subtract(qcnfratMc.add(lastnfratMc));
                                if(yue.compareTo(BigDecimal.ZERO) == 0){
                                    qc_bprogerty="平";
                                }else if(yue.compareTo(BigDecimal.ZERO)>0){
                                    qc_bprogerty="借";
                                }else{
                                    qc_bprogerty="贷";
                                }
                            } else {
                                yue = qcmc.add(lastmc).subtract(qcmd.add(lastmd));
                                yue_num = qcncS.add(lastncS).subtract(qcndS.add(lastndS));
                                yue_nfrat = qcnfratMc.add(lastnfratMc).subtract(qcnfratMd.add(lastnfratMd));
                                if(yue.compareTo(BigDecimal.ZERO) == 0){
                                    qc_bprogerty="平";
                                }else if(yue.compareTo(BigDecimal.ZERO)>0){
                                    qc_bprogerty="贷";
                                }else{
                                    qc_bprogerty="借";
                                }
                            }

                            QcCode.set(yue);
                            if(yue.compareTo(BigDecimal.ZERO)!=0){
                                mapList.add(
                                        new KeMuMingXiBigDecimalVo()
                                                .setInoIdASC(0)
                                                .setCcode(ck.getCcode())
                                                .setCcodeName(ck.getCcodeName())
                                                .setCdigest("期初余额")
                                                .setBprogerty(qc_bprogerty)
                                                .setYue(yue.compareTo(BigDecimal.ZERO) < 0?yue.multiply(new BigDecimal(-1)):yue)
                                                .setNcnum(yue_num.compareTo(BigDecimal.ZERO) < 0?yue_num.multiply(new BigDecimal(-1)):yue_num)
                                                .setYue_num(yue_num.compareTo(BigDecimal.ZERO) < 0?yue_num.multiply(new BigDecimal(-1)):yue_num)
                                                .setYue_nfrat(yue_nfrat.compareTo(BigDecimal.ZERO) < 0?yue_nfrat.multiply(new BigDecimal(-1)):yue_nfrat)
                                                .setTemp1(project.getUniqueCode())
                                                .setTemp2(project.getProjectName())
                                );
                            }

                            // ************* 获取科目凭证 *************
                            List<DeptCodeAccvoucherVo> findByCodePzList = pzlist.stream().filter(pz ->StrUtil.isNotBlank(pz.getProjectId()) && pz.getProjectId().equals(pro)&& pz.getCcode().equals(ck.getCcode())&& Integer.valueOf(pz.getIyperiod())>=Integer.valueOf(strDate) && Integer.valueOf(pz.getIyperiod())<=Integer.valueOf(endDate)).collect(Collectors.toList());
                            for (int i = 0; i < findByCodePzList.size(); i++) {
                                BigDecimal pzmd=findByCodePzList.size()>0?findByCodePzList.get(i).getMd():new BigDecimal(0);
                                BigDecimal pzmc=findByCodePzList.size()>0?findByCodePzList.get(i).getMc():new BigDecimal(0);
                                BigDecimal pzndS=findByCodePzList.size()>0?findByCodePzList.get(i).getNdS():new BigDecimal(0);
                                BigDecimal pzncS=findByCodePzList.size()>0?findByCodePzList.get(i).getNcS():new BigDecimal(0);
                                BigDecimal pznfratMd=findByCodePzList.size()>0?findByCodePzList.get(i).getNfratMd():new BigDecimal(0);
                                BigDecimal pznfratMc=findByCodePzList.size()>0?findByCodePzList.get(i).getNfratMc():new BigDecimal(0);

                                // 合计
                                codeTotalMd=codeTotalMd.add(pzmd);
                                codeTotalMc=codeTotalMc.add(pzmc);
                                codeTotalNds=codeTotalNds.add(pzndS);
                                codeTotalNcs=codeTotalNcs.add(pzncS);
                                codeTotalNfratMd=codeTotalNfratMd.add(pznfratMd);
                                codeTotalNfratMc=codeTotalNfratMc.add(pznfratMc);

                                String bprogerty="";
                                if(ck.getBprogerty().equals("1")){
                                    yue=yue.add(pzmd).subtract(pzmc);
                                    yue_num=yue_num.add(pzndS).subtract(pzncS);
                                    yue_nfrat.add(yue_nfrat).subtract(pznfratMc);

                                    if(yue.compareTo(BigDecimal.ZERO) == 0){
                                        bprogerty="平";
                                    }else if(yue.compareTo(BigDecimal.ZERO)>0){
                                        bprogerty="借";
                                    }else{
                                        bprogerty="贷";
                                    }
                                }else{
                                    yue=yue.add(pzmc).subtract(pzmd);
                                    yue_num=yue_num.add(pzncS).subtract(pzndS);
                                    yue_nfrat=yue_nfrat.add(pznfratMc).subtract(pznfratMd);

                                    if(yue.compareTo(BigDecimal.ZERO) == 0){
                                        bprogerty="平";
                                    }else if(yue.compareTo(BigDecimal.ZERO)>0){
                                        bprogerty="贷";
                                    }else{
                                        bprogerty="借";
                                    }
                                }

                                BigDecimal tempYue=yue.compareTo(BigDecimal.ZERO) < 0 ? yue.multiply(new BigDecimal(-1)) : yue;
                                BigDecimal tempYueNfract=yue_nfrat.compareTo(BigDecimal.ZERO) > 0 ? yue_nfrat : yue_nfrat.multiply(new BigDecimal(-1));

                                if(pzmd.compareTo(BigDecimal.ZERO)!=0 || pzmc.compareTo(BigDecimal.ZERO)!=0) {
                                    KeMuMingXiBigDecimalVo mx=new KeMuMingXiBigDecimalVo();
                                    mx.setNumber(i+1)
                                            .setCcode(findByCodePzList.get(i).getCcode())
                                            .setCcodeName(findByCodePzList.get(i).getCcodeName())
                                            .setDbillDate(findByCodePzList.get(i).getDbillDate())
                                            .setInoIdASC(Integer.valueOf(findByCodePzList.get(i).getInoId()))
                                            .setInoId(findByCodePzList.get(i).getCsign() + "-" + addLeftZero.addZeroForNum(findByCodePzList.get(i).getInoId(), 4))
                                            .setCdigest(findByCodePzList.get(i).getCdigest())
                                            .setMd(pzmd)
                                            .setMc(pzmc)
                                            .setBprogerty(bprogerty)
                                            .setNdS(pzndS)
                                            .setNcS(pzncS)
                                            .setCunitPrice(new BigDecimal(findByCodePzList.get(i).getCunitPrice()))
                                            .setUnitMeasurement(unitMeasurement)
                                            .setMdF(new BigDecimal(findByCodePzList.get(i).getMdF()))
                                            .setNfrat_md(pznfratMd)
                                            .setNfrat_mc(pznfratMc)
                                            .setForeignCurrency(foreignCurrency)
                                            .setYue(tempYue)
                                            .setYue_num(yue_num)
                                            .setYue_nfrat(tempYueNfract)
                                            .setTemp1(project.getUniqueCode())
                                            .setTemp2(project.getProjectName());

                                    mapList.add(mx);
                                    lastYue.set(tempYue);
                                    lastYue_num.set(yue_num);
                                    lastYue_nfrat.set(tempYueNfract);
                                }
                            }

                            if(codeTotalMd.compareTo(BigDecimal.ZERO)!=0 || codeTotalMc.compareTo(BigDecimal.ZERO)!=0) {
                                mapList.add(new KeMuMingXiBigDecimalVo()
                                        .setInoIdASC(0)
                                        .setCdigest("当前合计")
                                        .setCcode(ck.getCcode())
                                        .setCcodeName(ck.getCcodeName())
                                        .setMd(codeTotalMd)
                                        .setMc(codeTotalMc)
                                        .setNdS(codeTotalNds)
                                        .setNcS(codeTotalNcs)
                                        .setBprogerty(lastYue.get().compareTo(BigDecimal.ZERO) == 0 ? "平" : lastYue.get().compareTo(BigDecimal.ZERO) > 0 ? "借" : "贷")
                                        .setYue(lastYue.get().compareTo(BigDecimal.ZERO) > 0 ? lastYue.get() : yue_nfrat.multiply(new BigDecimal(-1)))
                                        .setYue_num(lastYue_num.get())
                                        .setYue_nfrat(lastYue_nfrat.get())
                                        .setNfrat_md(codeTotalNfratMd)
                                        .setNfrat_mc(codeTotalNfratMc)
                                        .setTemp1(project.getUniqueCode())
                                        .setTemp2(project.getProjectName())
                                );
                            }
                            if(yue.compareTo(BigDecimal.ZERO)!=0 && codeTotalMd.compareTo(BigDecimal.ZERO)==0){
                                mapList.add(
                                        new KeMuMingXiBigDecimalVo()
                                                .setInoIdASC(0)
                                                .setCcode(ck.getCcode())
                                                .setCcodeName(ck.getCcodeName())
                                                .setCdigest("当前合计")
                                                .setBprogerty(qc_bprogerty)
                                                .setYue(yue.compareTo(BigDecimal.ZERO) < 0?yue.multiply(new BigDecimal(-1)):yue)
                                                .setTempyue(yue)
                                                .setNcnum(yue_num.compareTo(BigDecimal.ZERO) < 0?yue_num.multiply(new BigDecimal(-1)):yue_num)
                                                .setYue_num(yue_num.compareTo(BigDecimal.ZERO) < 0?yue_num.multiply(new BigDecimal(-1)):yue_num)
                                                .setYue_nfrat(yue_nfrat.compareTo(BigDecimal.ZERO) < 0?yue_nfrat.multiply(new BigDecimal(-1)):yue_nfrat)
                                                .setTemp1(project.getUniqueCode())
                                                .setTemp2(project.getProjectName())
                                );
                            }
                        });

                        // 总计
                        BigDecimal totalMd=mapList.stream().filter(f->f.getTemp1().equals(pro)&&f.getCdigest().equals("当前合计")).map(KeMuMingXiBigDecimalVo::getMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                        BigDecimal totalMc=mapList.stream().filter(f->f.getTemp1().equals(pro)&&f.getCdigest().equals("当前合计")).map(KeMuMingXiBigDecimalVo::getMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                        BigDecimal totalNds=mapList.stream().filter(f->f.getTemp1().equals(pro)&&f.getCdigest().equals("当前合计")).map(KeMuMingXiBigDecimalVo::getNdS).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                        BigDecimal totalNcs=mapList.stream().filter(f->f.getTemp1().equals(pro)&&f.getCdigest().equals("当前合计")).map(KeMuMingXiBigDecimalVo::getNcS).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                        BigDecimal totalNfratMd=mapList.stream().filter(f->f.getTemp1().equals(pro)&&f.getCdigest().equals("当前合计")).map(KeMuMingXiBigDecimalVo::getNfrat_md).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                        BigDecimal totalNfratMc=mapList.stream().filter(f->f.getTemp1().equals(pro)&&f.getCdigest().equals("当前合计")).map(KeMuMingXiBigDecimalVo::getNfrat_mc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);

                        BigDecimal totalYue=mapList.stream().filter(f->f.getTemp1().equals(pro)&&f.getCdigest().equals("当前合计")).map(KeMuMingXiBigDecimalVo::getTempyue).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                        BigDecimal yuenum=mapList.stream().filter(f->f.getTemp1().equals(pro)&&f.getCdigest().equals("当前合计")).map(KeMuMingXiBigDecimalVo::getYue_num).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                        BigDecimal yuenfrat=mapList.stream().filter(f->f.getTemp1().equals(pro)&&f.getCdigest().equals("当前合计")).map(KeMuMingXiBigDecimalVo::getYue_nfrat).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                        String bprogerty=totalYue.compareTo(BigDecimal.ZERO) == 0 ? "平" : totalYue.compareTo(BigDecimal.ZERO) > 0 ? "借" : "贷";
                        if(totalMd.compareTo(BigDecimal.ZERO)!=0 || totalMc.compareTo(BigDecimal.ZERO)!=0) {
                            mapList.add(new KeMuMingXiBigDecimalVo()
                                    .setInoIdASC(0)
                                    .setCdigest("总计")
                                    .setBprogerty(bprogerty)
                                    .setMd(totalMd)
                                    .setMc(totalMc)
                                    .setNdS(totalNds)
                                    .setNcS(totalNcs)
                                    .setYue(totalYue.compareTo(BigDecimal.ZERO) > 0 ? totalYue : totalYue.multiply(new BigDecimal(-1)))
                                    .setYue_num(yuenum)
                                    .setYue_nfrat(yuenfrat)
                                    .setNfrat_md(totalNfratMd)
                                    .setNfrat_mc(totalNfratMc)
                                    .setTemp1(project.getUniqueCode())
                                    .setTemp2(project.getProjectName())
                            );
                        }
                        Map<String,Object> excelvo=new HashMap<>();
                        excelvo.put("ccode",project.getProjectCode());
                        excelvo.put("ccodeName",project.getProjectName());
                        excelvo.put("mxlist", mapList);
                        excellist.add(excelvo);
                    });
                    return Mono.just(excellist);
                })
                .map(a -> R.ok().setResult(a));
    }
    public Mono<R> exportAll2(String strDate, String endDate, String bz, String ishaveRjz,String styleValue,List<String> proList) {
        String year = strDate.substring(0, 4);
        String month = strDate.substring(4, 6);
        // 封装list
        List<KeMuMingXiBigDecimalVo> mapList = new ArrayList<>();
        // 1、获取项目辅助核算的科目
        return codeKemuRepository.findByBitemAndIyearOrderByCcodeAsc("1",year).collectList()
                .flatMap(deptCodeList->{
                    List<CodeKemu> newDeptCodeList=deptCodeList;
                    // 3-1、根据科目类型查找   财务会计【1-5】 预算会计【6-8】 先写死
                    if("财务会计".equals(styleValue)){
                        newDeptCodeList=newDeptCodeList.stream().filter(ck->Integer.valueOf(ck.getCcode().substring(0,1))<=5).collect(Collectors.toList());
                    }
                    // 3-2、根据科目类型查找   财务会计【1-5】 预算会计【6-8】 先写死
                    else if("预算会计".equals(styleValue)){
                        newDeptCodeList=newDeptCodeList.stream().filter(ck->Integer.valueOf(ck.getCcode().substring(0,1))>=6&&Integer.valueOf(ck.getCcode().substring(0,1))<=8).collect(Collectors.toList());
                    }
                    // 3-3、根据科目类型查找
                    else if(!"全部".equals(styleValue)){
                        newDeptCodeList=newDeptCodeList.stream().filter(ck->ck.getCclass().equals(styleValue)).collect(Collectors.toList());
                    }

                    List<CodeKemu> finalNewDeptCodeList = newDeptCodeList;
                    // 获取项目的凭证
                    return accvoucherRepository.findAllByIyearFuzhuAccvoucher(year,year+"01",endDate).collectList()
                            .flatMap(accvoucherlist->{
                                List<DeptCodeAccvoucherVo> newaccvoucherlist=accvoucherlist;
                                // 根据币种查询凭证
                                if(!"全部".equals(bz)){
                                    newaccvoucherlist=accvoucherlist.stream().filter(a->a.getForeignCurrency().equals(bz)).collect(Collectors.toList());
                                }
                                // ****** 是否包含未记账【查询条件】 *****不包含；只获取 已记账的凭证
                                if (ishaveRjz.equals("0")) {
                                    newaccvoucherlist = accvoucherlist.stream().filter(vo -> StringUtils.isNotBlank(vo.getIbook()) && vo.getIbook().equals("1")).collect(Collectors.toList());
                                }
                                List<DeptCodeAccvoucherVo> finalNewaccvoucherlist = newaccvoucherlist;
                                return fuZhuBalanceService.findAllSubjectInitialFuZhuBalance(year, "false",year+"21","")
                                        .flatMap(qclist->{
                                            Map qcmap = (Map) qclist.getResult();
                                            return projectRepositoryBase.findAllOrderByProCode().collectList()
                                                    .flatMap(prolist->{
                                                        return projectRepositoryBase.findAllOrderByProCode2().collectList()
                                                                .flatMap(pzProCodeList->{
                                                                    return Mono.just(
                                                                            CollectOfUtils.mapof("qclist",qcmap.get("tablesData"),"deptAccVoucherList",
                                                                                    finalNewaccvoucherlist,"codeList", finalNewDeptCodeList,
                                                                                    "prolist",prolist,"pzProCodeList",pzProCodeList)
                                                                    );
                                                                });
                                                    });
                                        });
                            });
                })
                // 获取部门科目凭证
                .flatMap(map->{
                    List<CodeKemu> codeList= (List<CodeKemu>) map.get("codeList");  // 都是末级科目
                    List<SubjectInitialBalanceVo> qclist = (List<SubjectInitialBalanceVo>) map.get("qclist");
                    List<DeptCodeAccvoucherVo> pzlist= (List<DeptCodeAccvoucherVo>) map.get("deptAccVoucherList");
                    List<Project> prolist= (List<Project>) map.get("prolist");
                    List<ProjectVo> pzProCodeList= (List<ProjectVo>) map.get("pzProCodeList");

                    proList.stream().forEach(pro->{
                        Project project = prolist.stream().filter(v -> v.getUniqueCode().equals(pro)).collect(Collectors.toList()).get(0);
                        List<String> collect = pzProCodeList.stream().filter(pzpro -> pzpro.getUniqueCode().equals(pro)).map(code -> code.getCcode()).collect(Collectors.toList());

                        collect.stream().forEach(code->{
                            AtomicReference<BigDecimal> lastYue= new AtomicReference<>(new BigDecimal(0));
                            AtomicReference<BigDecimal> lastYue_num= new AtomicReference<>(new BigDecimal(0));
                            AtomicReference<BigDecimal> lastYue_nfrat= new AtomicReference<>(new BigDecimal(0));
                            AtomicReference<BigDecimal> QcCode= new AtomicReference<>(new BigDecimal(0));

                            BigDecimal codeTotalMd=new BigDecimal(0);
                            BigDecimal codeTotalMc=new BigDecimal(0);
                            BigDecimal codeTotalNds=new BigDecimal(0);
                            BigDecimal codeTotalNcs=new BigDecimal(0);
                            BigDecimal codeTotalNfratMd=new BigDecimal(0);
                            BigDecimal codeTotalNfratMc=new BigDecimal(0);

                            CodeKemu ck = codeList.stream().filter(f -> f.getCcode().equals(code)).collect(Collectors.toList()).get(0);

                            // 科目的计量单位
                            List<CodeKemu> unitMeasurementlist = codeList.stream().filter(c -> c.getCcode().equals(ck.getCcode())).collect(Collectors.toList());
                            String unitMeasurement=unitMeasurementlist.size()>0?unitMeasurementlist.get(0).getMenterage():"";
                            // 科目的币种名称
                            List<CodeKemu> foreignCurrencylist = codeList.stream().filter(c -> c.getCcode().equals(ck.getCcode())).collect(Collectors.toList());
                            String foreignCurrency =foreignCurrencylist.size()>0?foreignCurrencylist.get(0).getCurrencyType():"";

                            // ************* 获取科目期初 *************
                            List<SubjectInitialBalanceVo> findByCodeQcList = qclist.stream().filter(qc ->StrUtil.isNotBlank(qc.getProjectId()) && qc.getCcode().equals(ck.getCcode())&& qc.getProjectId().equals(pro)).collect(Collectors.toList());
                            BigDecimal qcmd=findByCodeQcList.size()>0?findByCodeQcList.get(0).getMd():new BigDecimal(0);
                            BigDecimal qcmc=findByCodeQcList.size()>0?findByCodeQcList.get(0).getMc():new BigDecimal(0);
                            BigDecimal qcndS=findByCodeQcList.size()>0?findByCodeQcList.get(0).getNdS():new BigDecimal(0);
                            BigDecimal qcncS=findByCodeQcList.size()>0?findByCodeQcList.get(0).getNcS():new BigDecimal(0);
                            BigDecimal qcnfratMd=findByCodeQcList.size()>0?findByCodeQcList.get(0).getNfratMd():new BigDecimal(0);
                            BigDecimal qcnfratMc=findByCodeQcList.size()>0?findByCodeQcList.get(0).getNfratMc():new BigDecimal(0);

                            BigDecimal yue=ck.getBprogerty().equals("1")?qcmd.subtract(qcmc):qcmc.subtract(qcmd);
                            BigDecimal yue_num=ck.getBprogerty().equals("1")?qcndS.subtract(qcncS):qcncS.subtract(qcndS);
                            BigDecimal yue_nfrat=ck.getBprogerty().equals("1")?qcnfratMd.subtract(qcnfratMc):qcnfratMc.subtract(qcnfratMd);

                            BigDecimal lastmd=new BigDecimal(0);
                            BigDecimal lastmc=new BigDecimal(0);
                            BigDecimal lastndS=new BigDecimal(0);
                            BigDecimal lastncS=new BigDecimal(0);
                            BigDecimal lastnfratMd=new BigDecimal(0);
                            BigDecimal lastnfratMc=new BigDecimal(0);

                            // 期间或日期 不是从一月开始的 计算出上月的期末余额
                            if (Integer.valueOf(strDate.substring(4, 6)) > 1) {
                                String lastmonth = Integer.valueOf(month) - 1 < 10 ? "0" + (Integer.valueOf(month) - 1) : month;
                                // 获取上个月凭证发生数
                                List<DeptCodeAccvoucherVo> lastMonthAccVoucher = pzlist.stream().filter(acv ->StrUtil.isNotBlank(acv.getProjectId()) && acv.getProjectId().equals(pro)&& Integer.valueOf(acv.getImonth()) <= Integer.valueOf(lastmonth) && acv.getCcode().equals(ck.getCcode())).collect(Collectors.toList());
                                lastmd=lastMonthAccVoucher.size()>0?lastMonthAccVoucher.stream().map(DeptCodeAccvoucherVo::getMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                                lastmc=lastMonthAccVoucher.size()>0?lastMonthAccVoucher.stream().map(DeptCodeAccvoucherVo::getMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                                lastndS=lastMonthAccVoucher.size()>0?lastMonthAccVoucher.stream().map(DeptCodeAccvoucherVo::getNdS).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                                lastncS=lastMonthAccVoucher.size()>0?lastMonthAccVoucher.stream().map(DeptCodeAccvoucherVo::getNcS).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                                lastnfratMd=lastMonthAccVoucher.size()>0?lastMonthAccVoucher.stream().map(DeptCodeAccvoucherVo::getNfratMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                                lastnfratMc=lastMonthAccVoucher.size()>0?lastMonthAccVoucher.stream().map(DeptCodeAccvoucherVo::getNfratMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                            }
                            String qc_bprogerty="";
                            // 余额
                            if (ck.getBprogerty().equals("1")) {
                                yue = qcmd.add(lastmd).subtract(qcmc.add(lastmc));
                                yue_num = qcndS.add(lastndS).subtract(qcncS.add(lastncS));
                                yue_nfrat = qcnfratMd.add(lastnfratMd).subtract(qcnfratMc.add(lastnfratMc));
                                if(yue.compareTo(BigDecimal.ZERO) == 0){
                                    qc_bprogerty="平";
                                }else if(yue.compareTo(BigDecimal.ZERO)>0){
                                    qc_bprogerty="借";
                                }else{
                                    qc_bprogerty="贷";
                                }
                            } else {
                                yue = qcmc.add(lastmc).subtract(qcmd.add(lastmd));
                                yue_num = qcncS.add(lastncS).subtract(qcndS.add(lastndS));
                                yue_nfrat = qcnfratMc.add(lastnfratMc).subtract(qcnfratMd.add(lastnfratMd));
                                if(yue.compareTo(BigDecimal.ZERO) == 0){
                                    qc_bprogerty="平";
                                }else if(yue.compareTo(BigDecimal.ZERO)>0){
                                    qc_bprogerty="贷";
                                }else{
                                    qc_bprogerty="借";
                                }
                            }

                            QcCode.set(yue);
                            if(yue.compareTo(BigDecimal.ZERO)!=0){
                                mapList.add(
                                        new KeMuMingXiBigDecimalVo()
                                                .setInoIdASC(0)
                                                .setCcode(ck.getCcode())
                                                .setCcodeName(ck.getCcodeName())
                                                .setCdigest("期初余额")
                                                .setBprogerty(qc_bprogerty)
                                                .setYue(yue.compareTo(BigDecimal.ZERO) < 0?yue.multiply(new BigDecimal(-1)):yue)
                                                .setNcnum(yue_num.compareTo(BigDecimal.ZERO) < 0?yue_num.multiply(new BigDecimal(-1)):yue_num)
                                                .setYue_num(yue_num.compareTo(BigDecimal.ZERO) < 0?yue_num.multiply(new BigDecimal(-1)):yue_num)
                                                .setYue_nfrat(yue_nfrat.compareTo(BigDecimal.ZERO) < 0?yue_nfrat.multiply(new BigDecimal(-1)):yue_nfrat)
                                                .setTemp1(project.getUniqueCode())
                                                .setTemp2(project.getProjectName())
                                                .setTemp3(project.getProjectCode())
                                );
                            }

                            // ************* 获取科目凭证 *************
                            List<DeptCodeAccvoucherVo> findByCodePzList = pzlist.stream().filter(pz ->StrUtil.isNotBlank(pz.getProjectId()) && pz.getProjectId().equals(pro)&& pz.getCcode().equals(ck.getCcode())&& Integer.valueOf(pz.getIyperiod())>=Integer.valueOf(strDate) && Integer.valueOf(pz.getIyperiod())<=Integer.valueOf(endDate)).collect(Collectors.toList());
                            for (int i = 0; i < findByCodePzList.size(); i++) {
                                BigDecimal pzmd=findByCodePzList.size()>0?findByCodePzList.get(i).getMd():new BigDecimal(0);
                                BigDecimal pzmc=findByCodePzList.size()>0?findByCodePzList.get(i).getMc():new BigDecimal(0);
                                BigDecimal pzndS=findByCodePzList.size()>0?findByCodePzList.get(i).getNdS():new BigDecimal(0);
                                BigDecimal pzncS=findByCodePzList.size()>0?findByCodePzList.get(i).getNcS():new BigDecimal(0);
                                BigDecimal pznfratMd=findByCodePzList.size()>0?findByCodePzList.get(i).getNfratMd():new BigDecimal(0);
                                BigDecimal pznfratMc=findByCodePzList.size()>0?findByCodePzList.get(i).getNfratMc():new BigDecimal(0);

                                // 合计
                                codeTotalMd=codeTotalMd.add(pzmd);
                                codeTotalMc=codeTotalMc.add(pzmc);
                                codeTotalNds=codeTotalNds.add(pzndS);
                                codeTotalNcs=codeTotalNcs.add(pzncS);
                                codeTotalNfratMd=codeTotalNfratMd.add(pznfratMd);
                                codeTotalNfratMc=codeTotalNfratMc.add(pznfratMc);

                                String bprogerty="";
                                if(ck.getBprogerty().equals("1")){
                                    yue=yue.add(pzmd).subtract(pzmc);
                                    yue_num=yue_num.add(pzndS).subtract(pzncS);
                                    yue_nfrat.add(yue_nfrat).subtract(pznfratMc);

                                    if(yue.compareTo(BigDecimal.ZERO) == 0){
                                        bprogerty="平";
                                    }else if(yue.compareTo(BigDecimal.ZERO)>0){
                                        bprogerty="借";
                                    }else{
                                        bprogerty="贷";
                                    }
                                }else{
                                    yue=yue.add(pzmc).subtract(pzmd);
                                    yue_num=yue_num.add(pzncS).subtract(pzndS);
                                    yue_nfrat=yue_nfrat.add(pznfratMc).subtract(pznfratMd);

                                    if(yue.compareTo(BigDecimal.ZERO) == 0){
                                        bprogerty="平";
                                    }else if(yue.compareTo(BigDecimal.ZERO)>0){
                                        bprogerty="贷";
                                    }else{
                                        bprogerty="借";
                                    }
                                }

                                BigDecimal tempYue=yue.compareTo(BigDecimal.ZERO) < 0 ? yue.multiply(new BigDecimal(-1)) : yue;
                                BigDecimal tempYueNfract=yue_nfrat.compareTo(BigDecimal.ZERO) > 0 ? yue_nfrat : yue_nfrat.multiply(new BigDecimal(-1));

                                if(pzmd.compareTo(BigDecimal.ZERO)!=0 || pzmc.compareTo(BigDecimal.ZERO)!=0) {
                                    KeMuMingXiBigDecimalVo mx=new KeMuMingXiBigDecimalVo();
                                    mx.setNumber(i+1)
                                            .setCcode(findByCodePzList.get(i).getCcode())
                                            .setCcodeName(findByCodePzList.get(i).getCcodeName())
                                            .setDbillDate(findByCodePzList.get(i).getDbillDate())
                                            .setInoIdASC(Integer.valueOf(findByCodePzList.get(i).getInoId()))
                                            .setInoId(findByCodePzList.get(i).getCsign() + "-" + addLeftZero.addZeroForNum(findByCodePzList.get(i).getInoId(), 4))
                                            .setCdigest(findByCodePzList.get(i).getCdigest())
                                            .setMd(pzmd)
                                            .setMc(pzmc)
                                            .setBprogerty(bprogerty)
                                            .setNdS(pzndS)
                                            .setNcS(pzncS)
                                            .setCunitPrice(new BigDecimal(findByCodePzList.get(i).getCunitPrice()))
                                            .setUnitMeasurement(unitMeasurement)
                                            .setMdF(new BigDecimal(findByCodePzList.get(i).getMdF()))
                                            .setNfrat_md(pznfratMd)
                                            .setNfrat_mc(pznfratMc)
                                            .setForeignCurrency(foreignCurrency)
                                            .setYue(tempYue)
                                            .setYue_num(yue_num)
                                            .setYue_nfrat(tempYueNfract)
                                            .setTemp1(project.getUniqueCode())
                                            .setTemp2(project.getProjectName())
                                            .setTemp3(project.getProjectCode());

                                    mapList.add(mx);
                                    lastYue.set(tempYue);
                                    lastYue_num.set(yue_num);
                                    lastYue_nfrat.set(tempYueNfract);
                                }
                            }

                            if(codeTotalMd.compareTo(BigDecimal.ZERO)!=0 || codeTotalMc.compareTo(BigDecimal.ZERO)!=0) {
                                mapList.add(new KeMuMingXiBigDecimalVo()
                                        .setInoIdASC(0)
                                        .setCdigest("当前合计")
                                        .setCcode(ck.getCcode())
                                        .setCcodeName(ck.getCcodeName())
                                        .setMd(codeTotalMd)
                                        .setMc(codeTotalMc)
                                        .setNdS(codeTotalNds)
                                        .setNcS(codeTotalNcs)
                                        .setBprogerty(lastYue.get().compareTo(BigDecimal.ZERO) == 0 ? "平" : lastYue.get().compareTo(BigDecimal.ZERO) > 0 ? "借" : "贷")
                                        .setYue(lastYue.get().compareTo(BigDecimal.ZERO) > 0 ? lastYue.get() : yue_nfrat.multiply(new BigDecimal(-1)))
                                        .setYue_num(lastYue_num.get())
                                        .setYue_nfrat(lastYue_nfrat.get())
                                        .setNfrat_md(codeTotalNfratMd)
                                        .setNfrat_mc(codeTotalNfratMc)
                                        .setTemp1(project.getUniqueCode())
                                        .setTemp2(project.getProjectName())
                                        .setTemp3(project.getProjectCode())
                                );
                            }
                            if(yue.compareTo(BigDecimal.ZERO)!=0 && codeTotalMd.compareTo(BigDecimal.ZERO)==0){
                                mapList.add(
                                        new KeMuMingXiBigDecimalVo()
                                                .setInoIdASC(0)
                                                .setCcode(ck.getCcode())
                                                .setCcodeName(ck.getCcodeName())
                                                .setCdigest("当前合计")
                                                .setBprogerty(qc_bprogerty)
                                                .setYue(yue.compareTo(BigDecimal.ZERO) < 0?yue.multiply(new BigDecimal(-1)):yue)
                                                .setTempyue(yue)
                                                .setNcnum(yue_num.compareTo(BigDecimal.ZERO) < 0?yue_num.multiply(new BigDecimal(-1)):yue_num)
                                                .setYue_num(yue_num.compareTo(BigDecimal.ZERO) < 0?yue_num.multiply(new BigDecimal(-1)):yue_num)
                                                .setYue_nfrat(yue_nfrat.compareTo(BigDecimal.ZERO) < 0?yue_nfrat.multiply(new BigDecimal(-1)):yue_nfrat)
                                                .setTemp1(project.getUniqueCode())
                                                .setTemp2(project.getProjectName())
                                                .setTemp3(project.getProjectCode())
                                );
                            }
                        });

                        // 总计
                        BigDecimal totalMd=mapList.stream().filter(f->f.getTemp1().equals(pro)&&f.getCdigest().equals("当前合计")).map(KeMuMingXiBigDecimalVo::getMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                        BigDecimal totalMc=mapList.stream().filter(f->f.getTemp1().equals(pro)&&f.getCdigest().equals("当前合计")).map(KeMuMingXiBigDecimalVo::getMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                        BigDecimal totalNds=mapList.stream().filter(f->f.getTemp1().equals(pro)&&f.getCdigest().equals("当前合计")).map(KeMuMingXiBigDecimalVo::getNdS).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                        BigDecimal totalNcs=mapList.stream().filter(f->f.getTemp1().equals(pro)&&f.getCdigest().equals("当前合计")).map(KeMuMingXiBigDecimalVo::getNcS).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                        BigDecimal totalNfratMd=mapList.stream().filter(f->f.getTemp1().equals(pro)&&f.getCdigest().equals("当前合计")).map(KeMuMingXiBigDecimalVo::getNfrat_md).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                        BigDecimal totalNfratMc=mapList.stream().filter(f->f.getTemp1().equals(pro)&&f.getCdigest().equals("当前合计")).map(KeMuMingXiBigDecimalVo::getNfrat_mc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);

                        BigDecimal totalYue=mapList.stream().filter(f->f.getTemp1().equals(pro)&&f.getCdigest().equals("当前合计")).map(KeMuMingXiBigDecimalVo::getTempyue).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                        BigDecimal yuenum=mapList.stream().filter(f->f.getTemp1().equals(pro)&&f.getCdigest().equals("当前合计")).map(KeMuMingXiBigDecimalVo::getYue_num).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                        BigDecimal yuenfrat=mapList.stream().filter(f->f.getTemp1().equals(pro)&&f.getCdigest().equals("当前合计")).map(KeMuMingXiBigDecimalVo::getYue_nfrat).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                        String bprogerty=totalYue.compareTo(BigDecimal.ZERO) == 0 ? "平" : totalYue.compareTo(BigDecimal.ZERO) > 0 ? "借" : "贷";
                        if(totalMd.compareTo(BigDecimal.ZERO)!=0 || totalMc.compareTo(BigDecimal.ZERO)!=0) {
                            mapList.add(new KeMuMingXiBigDecimalVo()
                                    .setInoIdASC(0)
                                    .setCdigest("总计")
                                    .setBprogerty(bprogerty)
                                    .setMd(totalMd)
                                    .setMc(totalMc)
                                    .setNdS(totalNds)
                                    .setNcS(totalNcs)
                                    .setYue(totalYue.compareTo(BigDecimal.ZERO) > 0 ? totalYue : totalYue.multiply(new BigDecimal(-1)))
                                    .setYue_num(yuenum)
                                    .setYue_nfrat(yuenfrat)
                                    .setNfrat_md(totalNfratMd)
                                    .setNfrat_mc(totalNfratMc)
                                    .setTemp1(project.getUniqueCode())
                                    .setTemp2(project.getProjectName())
                                    .setTemp3(project.getProjectCode())
                            );
                        }
                    });
                    return Mono.just(mapList);
                })
                .map(a -> R.ok().setResult(a));
    }
}

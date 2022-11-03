package org.boozsoft.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.boozsoft.domain.entity.Customer;
import org.boozsoft.domain.entity.Supplier;
import org.boozsoft.domain.entity.account.FuzhuHesuan;
import org.boozsoft.domain.entity.account.SettModes;
import org.boozsoft.domain.entity.account.SysDepartment;
import org.boozsoft.domain.entity.account.SysPsn;
import org.boozsoft.domain.entity.codekemu.CodeKemu;
import org.boozsoft.domain.entity.share.project.base.Project;
import org.boozsoft.domain.vo.*;
import org.boozsoft.repo.*;
import org.boozsoft.repo.codekemu.CodeKemuRepository;
import org.boozsoft.repo.project.base.ProjectRepositoryBase;
import org.boozsoft.service.SubjectInitalBalanceService;
import org.boozsoft.util.BigDecimalUtils;
import org.boozsoft.util.addLeftZero;
import org.jetbrains.annotations.NotNull;
import org.springbooz.core.tool.result.R;
import org.springbooz.core.tool.utils.StringUtils;
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
public class KeMuMingXiServiceImpl {

    @Autowired
    SubjectInitalBalanceService subjectInitalBalanceService;
    @Autowired
    AccvoucherRepository accvoucherRepository;
    @Autowired
    SysLogRepository logRepository;
    @Autowired
    CodeKemuRepository codeKemuRepository;
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    SupplierRepository supplierRepository;
    @Autowired
    SysDepartmentRepository departmentRepository;
    @Autowired
    SysPsnRepository psnRepository;
    @Autowired
    ProjectRepositoryBase projectRepositoryBase;
    @Autowired
    FuzhuHesuanRepository fuzhuHesuanRepository;
    @Autowired
    SettModesRepository settModesRepository;


    /**
     * 查询科目明细账
     **/
    public Mono<R> findAll(@NotNull String bend, String strDate, String endDate, String maxJc, String minJc, String minKm, String timflg, String ishaveRjz, String bz,
                           Map<String, String> searchMap, Map<String, String> filterMap, String digestStyle,String database,String pzType) {
        // digestStyle 0 标准辅助核算项编码 1 标准辅助核算项名称 2 扩展辅助核算项编码 3 扩展辅助核算项名称 4 结算方式名称 5 结算日期 6 票据号 7 结算单位
        String newstrDate = strDate.length() > 6 ? strDate.substring(0, strDate.length() - 2) : strDate;
        String newendDate = endDate.length() > 6 ? endDate.substring(0, endDate.length() - 2) : endDate;
        String year = strDate.substring(0, 4);
        String month = strDate.substring(4, 6);
        String newbend = bend.equals("1") ? "false" : "true";

        return subjectInitalBalanceService.findAllSubjectInitialBalance(strDate.substring(0, 4), newbend,strDate.substring(0, 4)+"00",database,"","全部","类型")
            // 期初\凭证 list
            .flatMap(qclist -> {
                Map qcmap = (Map) qclist.getResult();
                Map map = new HashMap();
                map.put("qclist", qcmap.get("tablesData"));
                map.put("pzlist", "");       // 条件后的list
                map.put("codelist", "");       // 科目list
                map.put("pzlistAll", "");    // 全部凭证
                map.put("iyperiodlist", ""); // 科目中所有的凭证区间

                // 凭证list；前台已自动获取末级科目，方法中不在写入末级判断
                return accvoucherRepository.findByiyperiodpz(minKm + "%", strDate.substring(0, 4))
                        .collectList()
                        .flatMap(pzlist -> {
                            pzlist.stream().forEach(pz -> {
                                pz.setNdS(pz.getNdS() == null ? new BigDecimal("0") : pz.getNdS()).setNcS(pz.getNcS() == null ? new BigDecimal("0") : pz.getNcS())
                                        .setNfratMd(pz.getNfratMd() == null ? new BigDecimal("0") : pz.getNfratMd())
                                        .setNfratMc(pz.getNfratMc() == null ? new BigDecimal("0") : pz.getNfratMc())
                                        .setCunitPrice(org.apache.commons.lang3.StringUtils.isBlank(pz.getCunitPrice()) ? "0" : pz.getCunitPrice())
                                        .setMdF(org.apache.commons.lang3.StringUtils.isBlank(pz.getMdF()) ? "0" : pz.getMdF())
                                        .setForeignCurrency(StringUtils.isBlank(pz.getForeignCurrency()) ? "" : pz.getForeignCurrency());
                            });
                            List<AccvoucherVo> newlist = pzlist;
                            // ***** 日期or期间【查询条件】 *****
                            if ("qj".equals(timflg)) {
                                newlist = newlist.stream().filter(vo -> Integer.valueOf(vo.getImonth()) >= Integer.valueOf(newstrDate.substring(4, 6)) && Integer.valueOf(vo.getImonth()) <= Integer.valueOf(newendDate.substring(4, 6))).collect(Collectors.toList());
                            } else { // 日期
                                newlist = newlist.stream().filter(vo -> vo.getDbillDate() != null && Integer.valueOf(vo.getDbillDate().replaceAll("-", "")) >= Integer.valueOf(strDate) && Integer.valueOf(vo.getDbillDate().replaceAll("-", "")) <= Integer.valueOf(endDate)).collect(Collectors.toList());
                            }

                            // ****** 是否包含未记账【查询条件】 *****不包含；只获取 已记账的凭证
                            if (ishaveRjz.equals("0")) {
                                newlist = newlist.stream().filter(vo -> StringUtils.isNotBlank(vo.getIbook()) && vo.getIbook().equals("1")).collect(Collectors.toList());
                            }
                            // 按照凭证类型查找
                            if(StringUtils.isNotBlank(pzType)){
                                List<String> str = Arrays.asList(pzType.split(","));
                                newlist=newlist.stream().filter(p->str.indexOf(p.getCsign())!=-1).collect(Collectors.toList());
                            }
                            map.put("pzlist", newlist);
                            map.put("pzlistAll", pzlist);
                            return accvoucherRepository.findByCcodeGroupByIyperiod(year, minKm + "%", newstrDate, newendDate).collectList()
                                    .flatMap(iyp -> {
                                        map.put("iyperiodlist", iyp);

                                        //按年度所有科目list
                                        return codeKemuRepository.findAllByIyear(year).collectList()
                                                .flatMap(cl -> {
                                                    map.put("codelist", cl);
                                                    // 客户信息 list
                                                    return customerRepository.findAll().collectList()
                                                            .flatMap(cuslist->{
                                                                map.put("cuslist", cuslist);
                                                                // 供应商信息 list
                                                                return supplierRepository.findAll().collectList()
                                                                        .flatMap(suplist->{
                                                                            map.put("suplist", suplist);
                                                                            // 部门
                                                                            return departmentRepository.findAll().collectList()
                                                                                    .flatMap(deptlist->{
                                                                                        map.put("deptlist", deptlist);
                                                                                        // 人员
                                                                                        return psnRepository.findAll().collectList()
                                                                                                .flatMap(psnlist->{
                                                                                                    map.put("psnlist", psnlist);
                                                                                                    // 项目
                                                                                                    return projectRepositoryBase.findAll().collectList()
                                                                                                            .flatMap(prolist->{
                                                                                                                map.put("prolist", prolist);
                                                                                                                return fuzhuHesuanRepository.findByFlagOrderByCcode("1").collectList()
                                                                                                                        .flatMap(fzhslist -> {
                                                                                                                            map.put("fzhslist", fzhslist);
                                                                                                                            return settModesRepository.findByFlagOrderBySettModesCode("1").collectList()
                                                                                                                                    .flatMap(settmodeslist -> {
                                                                                                                                        map.put("settmodeslist", settmodeslist);
                                                                                                                                        return Mono.just(map);
                                                                                                                                    });
                                                                                                                        });
                                                                                                            });
                                                                                                });
                                                                                    });
                                                                        });
                                                            });
                                                });
                                    });
                        });
            })
            .flatMap(map -> {
                // 封装list
                List<KeMuMingXiVo> mapList = new ArrayList<>();

                List<SubjectInitialBalanceVo> qclist = (List<SubjectInitialBalanceVo>) map.get("qclist");
                List<AccvoucherVo> pzlist = (List<AccvoucherVo>) map.get("pzlist");
                List<AccvoucherVo> pzlistAll = (List<AccvoucherVo>) map.get("pzlistAll");
                List<IyperiodVo> iyperiodlist = (List<IyperiodVo>) map.get("iyperiodlist");
                List<CodeKemu> codelist = (List<CodeKemu>) map.get("codelist");
                // 去重后的凭证币种
                List<pzbzVo> pzbzlist = new ArrayList<>();
                //************************** 五个标准辅助项*****************************
                List<Customer> cuslist = (List<Customer>) map.get("cuslist");
                List<Supplier> suplist = (List<Supplier>) map.get("suplist");
                List<SysDepartment> deptlist = (List<SysDepartment>) map.get("deptlist");
                List<SysPsn> psnlist = (List<SysPsn>) map.get("psnlist");
                List<Project> prolist = (List<Project>) map.get("prolist");
                //************************** 扩展辅助项********************************
                List<FuzhuHesuan> fzhslist = (List<FuzhuHesuan>) map.get("fzhslist");
                //************************** 结算方式********************************
                List<SettModes> settmodeslist = (List<SettModes>) map.get("settmodeslist");

                // 获取maxKm期初
                List<SubjectInitialBalanceVo> qc = qclist.stream().filter(acv -> acv.getCcode().equals(minKm)).collect(Collectors.toList());
                BigDecimal md = qc.size() > 0 ? qc.get(0).getMd() : new BigDecimal("0");
                BigDecimal mc = qc.size() > 0 ? qc.get(0).getMc() : new BigDecimal("0");
                BigDecimal nds = qc.size() > 0 ? qc.get(0).getNdS() : new BigDecimal("0");
                BigDecimal ncs = qc.size() > 0 ? qc.get(0).getNcS() : new BigDecimal("0");

                // 外币金额
                BigDecimal nfrat_md = qc.size() > 0 ? qc.get(0).getNfratMd() : new BigDecimal("0");
                BigDecimal nfrat_mc = qc.size() > 0 ? qc.get(0).getNfratMc() : new BigDecimal("0");

                // 累计期初
                BigDecimal ljmd = qc.size() > 0 ? qc.get(0).getLjMd() : new BigDecimal("0");
                BigDecimal ljmc = qc.size() > 0 ? qc.get(0).getLjMc() : new BigDecimal("0");

                // 凭证借贷方金额
                BigDecimal pz_md = new BigDecimal(0);
                BigDecimal pz_mc = new BigDecimal(0);
                BigDecimal pz_nds = new BigDecimal(0);
                BigDecimal pz_ncs = new BigDecimal(0);
                BigDecimal pz_nfrat_md = new BigDecimal(0);
                BigDecimal pz_nfrat_mc = new BigDecimal(0);
                BigDecimal yearmd = new BigDecimal(0).add(ljmd);
                BigDecimal yearmc = new BigDecimal(0).add(ljmc);
                // ************ 数量核算 年度 **********
                BigDecimal nds_year = new BigDecimal(0).add(nds);
                BigDecimal ncs_year = new BigDecimal(0).add(ncs);
                // ************ 外币核算 年度 **********
                BigDecimal nfrat_md_year = new BigDecimal(0).add(nfrat_md);
                BigDecimal nfrat_mc_year = new BigDecimal(0).add(nfrat_mc);

                // 期间或日期 不是从一月开始的 计算出上月的期末余额
                if (Integer.valueOf(strDate.substring(4, 6)) > 1) {
                    String lastmonth = Integer.valueOf(month) - 1 < 10 ? "0" + (Integer.valueOf(month) - 1) : month;
                    // 获取上个月凭证发生数
                    List<AccvoucherVo> lastMonthAccVoucher = pzlistAll.stream().filter(acv -> Integer.valueOf(acv.getImonth()) <= Integer.valueOf(lastmonth) ).collect(Collectors.toList());
                    pz_md = lastMonthAccVoucher.size() == 0 ? new BigDecimal("0") : lastMonthAccVoucher.stream().map(AccvoucherVo::getMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                    pz_mc = lastMonthAccVoucher.size() == 0 ? new BigDecimal("0") : lastMonthAccVoucher.stream().map(AccvoucherVo::getMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                    pz_nds = lastMonthAccVoucher.size() == 0 ? new BigDecimal("0") : lastMonthAccVoucher.stream().map(AccvoucherVo::getNdS).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                    pz_ncs = lastMonthAccVoucher.size() == 0 ? new BigDecimal("0") : lastMonthAccVoucher.stream().map(AccvoucherVo::getNcS).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);

                    pz_nfrat_md = lastMonthAccVoucher.size() == 0 ? new BigDecimal("0") : lastMonthAccVoucher.stream().map(AccvoucherVo::getNfratMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                    pz_nfrat_mc = lastMonthAccVoucher.size() == 0 ? new BigDecimal("0") : lastMonthAccVoucher.stream().map(AccvoucherVo::getNfratMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);

                    yearmd = yearmd.add(pz_md);
                    yearmc = yearmc.add(pz_mc);
                    // ************ 数量核算 年度 **********
                    nds_year = nds_year.add(pz_nds);
                    ncs_year = ncs_year.add(pz_ncs);
                    // ************ 外币核算 年度 **********
                    nfrat_md_year = nfrat_md_year.add(pz_nfrat_md);
                    nfrat_mc_year = nfrat_mc_year.add(pz_nfrat_mc);
                }
                BigDecimal jie = md.add(pz_md);
                BigDecimal dai = mc.add(pz_mc);
                BigDecimal yue = jie.subtract(dai);  // 借+借-贷+贷

                // ******** 数量核算 *******
                BigDecimal nd_jie = nds.add(pz_nds);
                BigDecimal nc_dai = ncs.add(pz_ncs);
                BigDecimal yue_num = nd_jie.subtract(nc_dai);  // 借+借-贷+贷
                // ******** 外币核算 *******
                BigDecimal nfrat_jie = nfrat_md.add(pz_nfrat_md);
                BigDecimal nfrat_dai = nfrat_mc.add(pz_nfrat_mc);
                BigDecimal yue_nfrat = nfrat_jie.subtract(nfrat_dai);  // 借+借-贷+贷

                int yuetmp = yue.compareTo(new BigDecimal(0));
                int number = 1; // 序号


                // 按币种查询
                List<CodeKemu> collect2 = codelist.stream().filter(c -> c.getCcode().indexOf(minKm) != -1 && c.getBend().equals("1")).collect(Collectors.toList()).stream().distinct().collect(Collectors.toList());
                collect2.stream().forEach(f -> {
                    if ("人民币".equals(bz) || "本位币".equals(bz)) {
                        if (f.getBend().equals("1") && StringUtils.isBlank(f.getCurrencyType())) {
                            pzbzlist.add(new pzbzVo().setCcode(f.getCcode()).setBz(f.getCurrencyType()).setOrder(0));
                        }
                    } else if ("全部".equals(bz)) {
                        if (f.getBend().equals("1")) {
                            if (StringUtils.isNotBlank(f.getCurrencyType())) {
                                pzbzlist.add(new pzbzVo().setCcode(f.getCcode()).setBz(f.getCurrencyType()).setOrder(1));
                            } else {
                                pzbzlist.add(new pzbzVo().setCcode(f.getCcode()).setBz(f.getCurrencyType()).setOrder(0));
                            }
                        }
                    } else {
                        if (f.getCurrencyType().equals(bz)) {
                            pzbzlist.add(new pzbzVo().setCcode(f.getCcode()).setBz(f.getCurrencyType()).setOrder(1));
                        }
                    }
                });

                // 判断科目是否 外币核算
                List<CodeKemu> curlist = codelist.stream().filter(ck -> ck.getCurrency().equals("1") && ck.getCcode().indexOf(minKm) != -1 && ck.getBend().equals("1")).collect(Collectors.toList());
                if (curlist.size() > 0) {
                    pzbzlist.sort(Comparator.comparing(pzbzVo::getOrder).reversed());
                    mapList.add(new KeMuMingXiVo()
                            .setNumber(number)
                            .setInoIdASC(0)
                            .setCdigest("期初余额")
                            .setBprogerty(yuetmp == 0 ? "平" : yuetmp > 0 ? "借" : "贷")
                            .setYue(yue.compareTo(BigDecimal.ZERO)<0?yue.multiply(new BigDecimal(-1)):yue)
                            .setNcnum(yue_num.compareTo(BigDecimal.ZERO)<0?yue_num.multiply(new BigDecimal(-1)):yue_num)
                            .setYue_num(yue_num.compareTo(BigDecimal.ZERO)<0?yue_num.multiply(new BigDecimal(-1)):yue_num)
                            .setYue_nfrat(yue_nfrat.compareTo(BigDecimal.ZERO)<0?yue_nfrat.multiply(new BigDecimal(-1)):yue_nfrat)
                    );
                    for (int q = 0; q < pzbzlist.size(); q++) {
                        yue=new BigDecimal(0);
                        yue_num=new BigDecimal(0);
                        yue_nfrat=new BigDecimal(0);

                        yearmd=new BigDecimal(0);
                        yearmc=new BigDecimal(0);
                        // 数量核算
                        nds_year=new BigDecimal(0);
                        ncs_year=new BigDecimal(0);
                        // 外币核算
                        nfrat_md_year=new BigDecimal(0);
                        nfrat_mc_year=new BigDecimal(0);

                        // 科目在凭证中所有区间
                        for (int i = 0; i < iyperiodlist.size(); i++) {
                            BigDecimal monthmd = new BigDecimal(0);
                            BigDecimal monthmc = new BigDecimal(0);
                            // ******** 数量核算 *******
                            BigDecimal nd_month = new BigDecimal(0);
                            BigDecimal nc_month = new BigDecimal(0);
                            // ******** 外币核算 *******
                            BigDecimal nfrat_md_month = new BigDecimal(0);
                            BigDecimal nfrat_mc_month = new BigDecimal(0);

                            int finalI = i;
                            int finalQ = q;
                            // 凭证
                            List<AccvoucherVo> collect = pzlist.stream().filter(acv -> acv.getIyperiod().equals(iyperiodlist.get(finalI).getIyperiod()) && acv.getCcode().equals(pzbzlist.get(finalQ).getCcode())).collect(Collectors.toList());
                            BigDecimal yue2 = yue; // 余额
                            BigDecimal yue_num2 = yue_num;  // 余额-数量
                            BigDecimal yue_nfrat2 = yue_nfrat;  // 余额-外币
                            int a = collect.size();
                            for (int j = 0; j < collect.size(); j++) {
                                int finalJ = j;
                                // 科目的计量单位
                                String unitMeasurement = codelist.stream().filter(c -> c.getCcode().equals(collect.get(finalJ).getCcode())).collect(Collectors.toList()).get(0).getMenterage();
                                // 科目的币种名称
                                String foreignCurrency = codelist.stream().filter(c -> c.getCcode().equals(collect.get(finalJ).getCcode())).collect(Collectors.toList()).get(0).getCurrencyType();

                                number++;
                                --a;
                                BigDecimal md2 = collect.get(j).getMd();
                                BigDecimal mc2 = collect.get(j).getMc();
                                // 数量核算
                                BigDecimal nds2 = collect.get(j).getNdS() == null ? new BigDecimal("0") : collect.get(j).getNdS();
                                BigDecimal ncs2 = collect.get(j).getNcS() == null ? new BigDecimal("0") : collect.get(j).getNcS();
                                // 外币核算
                                BigDecimal nfrat_md2 = collect.get(j).getNfratMd() == null ? new BigDecimal("0") : collect.get(j).getNfratMd();
                                BigDecimal nfrat_mc2 = collect.get(j).getNfratMc() == null ? new BigDecimal("0") : collect.get(j).getNfratMc();
                                monthmd = monthmd.add(md2);
                                monthmc = monthmc.add(mc2);
                                // ******** 数量核算 *******
                                nd_month = nd_month.add(nds2);
                                nc_month = nc_month.add(ncs2);
                                // ******** 外币核算 *******
                                nfrat_md_month = nfrat_md_month.add(nfrat_md2);
                                nfrat_mc_month = nfrat_mc_month.add(nfrat_mc2);
                                if (yuetmp >= 0) {
                                    yue2 = yue2.add(md2).subtract(mc2);
                                    yue_num2 = yue_num2.add(nds2).subtract(ncs2);
                                    yue_nfrat2 = yue_nfrat2.add(nfrat_mc2).subtract(nfrat_md2);
                                } else {
                                    yue2 = (yue2.multiply(new BigDecimal(-1)).add(mc2)).multiply(new BigDecimal(-1)).add(md2);
                                    yue_num2 = (yue_num2.multiply(new BigDecimal(-1)).add(ncs2)).multiply(new BigDecimal(-1)).add(nds2);
                                    yue_nfrat2 = (yue_nfrat2.multiply(new BigDecimal(-1)).add(nfrat_mc2)).multiply(new BigDecimal(-1)).add(nfrat_md2);
                                }
                                // 计算后在判断是否等于0
                                int yuefx2 = yue2.compareTo(new BigDecimal(0));
                                KeMuMingXiVo mx=new KeMuMingXiVo();
                                mx.setNumber(number)
                                        .setDbillDate(collect.get(j).getDbillDate())
                                        .setInoIdASC(Integer.valueOf(collect.get(j).getInoId()))
                                        .setInoId(collect.get(j).getCsign()+"-"+addLeftZero.addZeroForNum(String.valueOf(collect.get(j).getInoId()),4))
                                        .setCdigest(collect.get(j).getCdigest())
                                        .setMd(md2)
                                        .setMc(mc2)
                                        .setBprogerty(yuefx2==0?"平":yuefx2>0?"借":"贷")
                                        .setNdS(collect.get(j).getNdS())
                                        .setNcS(collect.get(j).getNcS())
                                        .setCunitPrice(new BigDecimal(collect.get(j).getCunitPrice()))
                                        .setUnitMeasurement(unitMeasurement)
                                        .setMdF(new BigDecimal(collect.get(j).getMdF()))
                                        .setNfrat_md(collect.get(j).getNfratMd())
                                        .setNfrat_mc(collect.get(j).getNfratMc())
                                        .setForeignCurrency(foreignCurrency)
                                        .setYue(yue2.compareTo(BigDecimal.ZERO)<0?yue2.multiply(new BigDecimal(-1)):yue2)
                                        .setYue_num(yue_num2)
                                        .setYue_nfrat(yue_nfrat2);

                                // digestStyle 0 标准辅助核算项编码 1 标准辅助核算项名称 2 扩展辅助核算项编码 3 扩展辅助核算项名称 4 结算方式名称 5 结算日期 6 票据号 7 结算单位
                                // 5个辅助项
                                if(StringUtils.isNotBlank(digestStyle)){
                                    String bzfzNum="";
                                    String bzfzName="";
                                    String kzfzNum="";
                                    String kzfzName="";
                                    String jsfsname="";
                                    String jsdate="";
                                    String pjnum="";
                                    String jsdw="";
                                    for (int k = 0; k < digestStyle.split(",").length; k++) {
                                        int finalJ1 = j;
                                        // 5个辅助项
                                        // 标准辅助核算项编码
                                        if(digestStyle.split(",")[k].equals("0")){
                                            // 部门
                                            if(StringUtils.isNotBlank(collect.get(j).getCdeptId())){
                                                List<SysDepartment> collect1 = deptlist.stream().filter(dept -> dept.getUniqueCode().equals(collect.get(finalJ1).getCdeptId())).collect(Collectors.toList());
                                                if(collect1.size()>0){
                                                    bzfzNum+=collect1.get(0).getDeptCode()+",";
                                                }
                                            }
                                            // 人员
                                            if(StringUtils.isNotBlank(collect.get(j).getCpersonId())){
                                                List<SysPsn> collect1 = psnlist.stream().filter(psn -> psn.getUniqueCode().equals(collect.get(finalJ1).getCpersonId())).collect(Collectors.toList());
                                                if(collect1.size()>0){
                                                    bzfzNum+=collect1.get(0).getPsnCode()+",";
                                                }
                                            }
                                            // 客户
                                            if(StringUtils.isNotBlank(collect.get(j).getCcusId())){
                                                List<Customer> collect1 = cuslist.stream().filter(cus -> cus.getUniqueCode().equals(collect.get(finalJ1).getCcusId())).collect(Collectors.toList());
                                                if(collect1.size()>0){
                                                    bzfzNum+=collect1.get(0).getCustCode()+",";
                                                }
                                            }
                                            // 供应商
                                            if(StringUtils.isNotBlank(collect.get(j).getCsupId())){
                                                List<Supplier> collect1 = suplist.stream().filter(sup -> sup.getUniqueCode().equals(collect.get(finalJ1).getCsupId())).collect(Collectors.toList());
                                                if(collect1.size()>0){
                                                    bzfzNum+=collect1.get(0).getCustCode()+",";
                                                }
                                            }
                                            // 项目
                                            if(StringUtils.isNotBlank(collect.get(j).getProjectId())){
                                                List<Project> collect1 = prolist.stream().filter(pro -> pro.getUniqueCode().equals(collect.get(finalJ1).getProjectId())).collect(Collectors.toList());
                                                if(collect1.size()>0){
                                                    bzfzNum+=collect1.get(0).getProjectCode()+",";
                                                }
                                            }
                                        }
                                        // 标准辅助核算项名称
                                        if(digestStyle.split(",")[k].equals("1")){
                                            // 部门
                                            if(StringUtils.isNotBlank(collect.get(j).getCdeptId())){
                                                List<SysDepartment> collect1 = deptlist.stream().filter(dept -> dept.getUniqueCode().equals(collect.get(finalJ1).getCdeptId())).collect(Collectors.toList());
                                                if(collect1.size()>0){
                                                    bzfzName+=collect1.get(0).getDeptName()+",";
                                                }
                                            }
                                            // 人员
                                            if(StringUtils.isNotBlank(collect.get(j).getCpersonId())){
                                                List<SysPsn> collect1 = psnlist.stream().filter(psn -> psn.getUniqueCode().equals(collect.get(finalJ1).getCpersonId())).collect(Collectors.toList());
                                                if(collect1.size()>0){
                                                    bzfzName+=collect1.get(0).getPsnName()+",";
                                                }
                                            }
                                            // 客户
                                            if(StringUtils.isNotBlank(collect.get(j).getCcusId())){
                                                List<Customer> collect1 = cuslist.stream().filter(cus -> cus.getUniqueCode().equals(collect.get(finalJ1).getCcusId())).collect(Collectors.toList());
                                                if(collect1.size()>0){
                                                    bzfzName+=collect1.get(0).getCustName()+",";
                                                }
                                            }
                                            // 供应商
                                            if(StringUtils.isNotBlank(collect.get(j).getCsupId())){
                                                List<Supplier> collect1 = suplist.stream().filter(sup -> sup.getUniqueCode().equals(collect.get(finalJ1).getCsupId())).collect(Collectors.toList());
                                                if(collect1.size()>0){
                                                    bzfzName+=collect1.get(0).getCustName()+",";
                                                }
                                            }
                                            // 项目
                                            if(StringUtils.isNotBlank(collect.get(j).getProjectId())){
                                                List<Project> collect1 = prolist.stream().filter(pro -> pro.getUniqueCode().equals(collect.get(finalJ1).getProjectId())).collect(Collectors.toList());
                                                if(collect1.size()>0){
                                                    bzfzName+=collect1.get(0).getProjectName()+",";
                                                }
                                            }
                                        }
                                        // 扩展辅助核算项编码
                                        if(digestStyle.split(",")[k].equals("2")){
                                            if(StringUtils.isNotBlank(collect.get(j).getCdfine1())){
                                                String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine1());
                                                kzfzNum+=ccode.equals("")?"":ccode+",";
                                            }
                                            if(StringUtils.isNotBlank(collect.get(j).getCdfine2())){
                                                String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine2());
                                                kzfzNum+=ccode.equals("")?"":ccode+",";
                                            }
                                            if(StringUtils.isNotBlank(collect.get(j).getCdfine3())){
                                                String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine3());
                                                kzfzNum+=ccode.equals("")?"":ccode+",";
                                            }
                                            if(StringUtils.isNotBlank(collect.get(j).getCdfine4())){
                                                String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine4());
                                                kzfzNum+=ccode.equals("")?"":ccode+",";
                                            }
                                            if(StringUtils.isNotBlank(collect.get(j).getCdfine5())){
                                                String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine5());
                                                kzfzNum+=ccode.equals("")?"":ccode+",";
                                            }
                                            if(StringUtils.isNotBlank(collect.get(j).getCdfine6())){
                                                String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine6());
                                                kzfzNum+=ccode.equals("")?"":ccode+",";
                                            }
                                            if(StringUtils.isNotBlank(collect.get(j).getCdfine7())){
                                                String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine7());
                                                kzfzNum+=ccode.equals("")?"":ccode+",";
                                            }
                                            if(StringUtils.isNotBlank(collect.get(j).getCdfine8())){
                                                String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine8());
                                                kzfzNum+=ccode.equals("")?"":ccode+",";
                                            }
                                            if(StringUtils.isNotBlank(collect.get(j).getCdfine9())){
                                                String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine9());
                                                kzfzNum+=ccode.equals("")?"":ccode+",";
                                            }
                                            if(StringUtils.isNotBlank(collect.get(j).getCdfine10())){
                                                String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine10());
                                                kzfzNum+=ccode.equals("")?"":ccode+",";
                                            }
                                            if(StringUtils.isNotBlank(collect.get(j).getCdfine11())){
                                                String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine11());
                                                kzfzNum+=ccode.equals("")?"":ccode+",";
                                            }
                                            if(StringUtils.isNotBlank(collect.get(j).getCdfine12())){
                                                String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine12());
                                                kzfzNum+=ccode.equals("")?"":ccode+",";
                                            }
                                            if(StringUtils.isNotBlank(collect.get(j).getCdfine13())){
                                                String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine13());
                                                kzfzNum+=ccode.equals("")?"":ccode+",";
                                            }
                                            if(StringUtils.isNotBlank(collect.get(j).getCdfine14())){
                                                String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine14());
                                                kzfzNum+=ccode.equals("")?"":ccode+",";
                                            }
                                            if(StringUtils.isNotBlank(collect.get(j).getCdfine15())){
                                                String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine15());
                                                kzfzNum+=ccode.equals("")?"":ccode+",";
                                            }
                                            if(StringUtils.isNotBlank(collect.get(j).getCdfine16())){
                                                String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine16());
                                                kzfzNum+=ccode.equals("")?"":ccode+",";
                                            }
                                            if(StringUtils.isNotBlank(collect.get(j).getCdfine17())){
                                                String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine17());
                                                kzfzNum+=ccode.equals("")?"":ccode+",";
                                            }
                                            if(StringUtils.isNotBlank(collect.get(j).getCdfine18())){
                                                String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine18());
                                                kzfzNum+=ccode.equals("")?"":ccode+",";
                                            }
                                            if(StringUtils.isNotBlank(collect.get(j).getCdfine19())){
                                                String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine19());
                                                kzfzNum+=ccode.equals("")?"":ccode+",";
                                            }
                                            if(StringUtils.isNotBlank(collect.get(j).getCdfine20())){
                                                String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine20());
                                                kzfzNum+=ccode.equals("")?"":ccode+",";
                                            }
                                            if(StringUtils.isNotBlank(collect.get(j).getCdfine21())){
                                                String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine21());
                                                kzfzNum+=ccode.equals("")?"":ccode+",";
                                            }
                                            if(StringUtils.isNotBlank(collect.get(j).getCdfine22())){
                                                String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine22());
                                                kzfzNum+=ccode.equals("")?"":ccode+",";
                                            }
                                            if(StringUtils.isNotBlank(collect.get(j).getCdfine23())){
                                                String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine23());
                                                kzfzNum+=ccode.equals("")?"":ccode+",";
                                            }
                                            if(StringUtils.isNotBlank(collect.get(j).getCdfine24())){
                                                String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine24());
                                                kzfzNum+=ccode.equals("")?"":ccode+",";
                                            }
                                            if(StringUtils.isNotBlank(collect.get(j).getCdfine25())){
                                                String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine25());
                                                kzfzNum+=ccode.equals("")?"":ccode+",";
                                            }
                                            if(StringUtils.isNotBlank(collect.get(j).getCdfine26())){
                                                String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine26());
                                                kzfzNum+=ccode.equals("")?"":ccode+",";
                                            }
                                            if(StringUtils.isNotBlank(collect.get(j).getCdfine27())){
                                                String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine27());
                                                kzfzNum+=ccode.equals("")?"":ccode+",";
                                            }
                                            if(StringUtils.isNotBlank(collect.get(j).getCdfine28())){
                                                String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine28());
                                                kzfzNum+=ccode.equals("")?"":ccode+",";
                                            }
                                            if(StringUtils.isNotBlank(collect.get(j).getCdfine29())){
                                                String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine29());
                                                kzfzNum+=ccode.equals("")?"":ccode+",";
                                            }
                                            if(StringUtils.isNotBlank(collect.get(j).getCdfine30())){
                                                String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine30());
                                                kzfzNum+=ccode.equals("")?"":ccode+",";
                                            }

                                        }
                                        // 扩展辅助核算项名称
                                        if(digestStyle.split(",")[k].equals("3")){
                                            if(StringUtils.isNotBlank(collect.get(j).getCdfine1())){
                                                String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine1());
                                                kzfzName+=ccode.equals("")?"":ccode+",";
                                            }
                                            if(StringUtils.isNotBlank(collect.get(j).getCdfine2())){
                                                String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine2());
                                                kzfzName+=ccode.equals("")?"":ccode+",";
                                            }
                                            if(StringUtils.isNotBlank(collect.get(j).getCdfine3())){
                                                String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine3());
                                                kzfzName+=ccode.equals("")?"":ccode+",";
                                            }
                                            if(StringUtils.isNotBlank(collect.get(j).getCdfine4())){
                                                String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine4());
                                                kzfzName+=ccode.equals("")?"":ccode+",";
                                            }
                                            if(StringUtils.isNotBlank(collect.get(j).getCdfine5())){
                                                String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine5());
                                                kzfzName+=ccode.equals("")?"":ccode+",";
                                            }
                                            if(StringUtils.isNotBlank(collect.get(j).getCdfine6())){
                                                String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine6());
                                                kzfzName+=ccode.equals("")?"":ccode+",";
                                            }
                                            if(StringUtils.isNotBlank(collect.get(j).getCdfine7())){
                                                String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine7());
                                                kzfzName+=ccode.equals("")?"":ccode+",";
                                            }
                                            if(StringUtils.isNotBlank(collect.get(j).getCdfine8())){
                                                String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine8());
                                                kzfzName+=ccode.equals("")?"":ccode+",";
                                            }
                                            if(StringUtils.isNotBlank(collect.get(j).getCdfine9())){
                                                String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine9());
                                                kzfzName+=ccode.equals("")?"":ccode+",";
                                            }
                                            if(StringUtils.isNotBlank(collect.get(j).getCdfine10())){
                                                String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine10());
                                                kzfzName+=ccode.equals("")?"":ccode+",";
                                            }
                                            if(StringUtils.isNotBlank(collect.get(j).getCdfine11())){
                                                String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine11());
                                                kzfzName+=ccode.equals("")?"":ccode+",";
                                            }
                                            if(StringUtils.isNotBlank(collect.get(j).getCdfine12())){
                                                String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine12());
                                                kzfzName+=ccode.equals("")?"":ccode+",";
                                            }
                                            if(StringUtils.isNotBlank(collect.get(j).getCdfine13())){
                                                String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine13());
                                                kzfzName+=ccode.equals("")?"":ccode+",";
                                            }
                                            if(StringUtils.isNotBlank(collect.get(j).getCdfine14())){
                                                String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine14());
                                                kzfzName+=ccode.equals("")?"":ccode+",";
                                            }
                                            if(StringUtils.isNotBlank(collect.get(j).getCdfine15())){
                                                String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine15());
                                                kzfzName+=ccode.equals("")?"":ccode+",";
                                            }
                                            if(StringUtils.isNotBlank(collect.get(j).getCdfine16())){
                                                String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine16());
                                                kzfzName+=ccode.equals("")?"":ccode+",";
                                            }
                                            if(StringUtils.isNotBlank(collect.get(j).getCdfine17())){
                                                String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine17());
                                                kzfzName+=ccode.equals("")?"":ccode+",";
                                            }
                                            if(StringUtils.isNotBlank(collect.get(j).getCdfine18())){
                                                String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine18());
                                                kzfzName+=ccode.equals("")?"":ccode+",";
                                            }
                                            if(StringUtils.isNotBlank(collect.get(j).getCdfine19())){
                                                String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine19());
                                                kzfzName+=ccode.equals("")?"":ccode+",";
                                            }
                                            if(StringUtils.isNotBlank(collect.get(j).getCdfine20())){
                                                String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine20());
                                                kzfzName+=ccode.equals("")?"":ccode+",";
                                            }
                                            if(StringUtils.isNotBlank(collect.get(j).getCdfine21())){
                                                String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine21());
                                                kzfzName+=ccode.equals("")?"":ccode+",";
                                            }
                                            if(StringUtils.isNotBlank(collect.get(j).getCdfine22())){
                                                String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine22());
                                                kzfzName+=ccode.equals("")?"":ccode+",";
                                            }
                                            if(StringUtils.isNotBlank(collect.get(j).getCdfine23())){
                                                String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine23());
                                                kzfzName+=ccode.equals("")?"":ccode+",";
                                            }
                                            if(StringUtils.isNotBlank(collect.get(j).getCdfine24())){
                                                String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine24());
                                                kzfzName+=ccode.equals("")?"":ccode+",";
                                            }
                                            if(StringUtils.isNotBlank(collect.get(j).getCdfine25())){
                                                String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine25());
                                                kzfzName+=ccode.equals("")?"":ccode+",";
                                            }
                                            if(StringUtils.isNotBlank(collect.get(j).getCdfine26())){
                                                String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine26());
                                                kzfzName+=ccode.equals("")?"":ccode+",";
                                            }
                                            if(StringUtils.isNotBlank(collect.get(j).getCdfine27())){
                                                String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine27());
                                                kzfzName+=ccode.equals("")?"":ccode+",";
                                            }
                                            if(StringUtils.isNotBlank(collect.get(j).getCdfine28())){
                                                String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine28());
                                                kzfzName+=ccode.equals("")?"":ccode+",";
                                            }
                                            if(StringUtils.isNotBlank(collect.get(j).getCdfine29())){
                                                String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine29());
                                                kzfzName+=ccode.equals("")?"":ccode+",";
                                            }
                                            if(StringUtils.isNotBlank(collect.get(j).getCdfine30())){
                                                String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine30());
                                                kzfzName+=ccode.equals("")?"":ccode+",";
                                            }

                                        }
                                        // 结算方式名称
                                        if(digestStyle.split(",")[k].equals("4")){
                                            if(StringUtils.isNotBlank(collect.get(j).getPjCsettle())){
                                                List<SettModes> collect1 = settmodeslist.stream().filter(s -> s.getSettModesCode().equals(collect.get(finalJ1).getPjCsettle())).collect(Collectors.toList());
                                                if(collect1.size()>0){
                                                    jsfsname+=collect1.get(0).getSettModesName()+",";
                                                }
                                            }
                                        }
                                        // 结算日期
                                        if(digestStyle.split(",")[k].equals("5")){
                                            if(StringUtils.isNotBlank(collect.get(j).getPjDate())){
                                                jsdate+=collect.get(j).getPjDate()+",";
                                            }
                                        }
                                        // 票据号
                                        if(digestStyle.split(",")[k].equals("6")){
                                            if(StringUtils.isNotBlank(collect.get(j).getPjId())){
                                                pjnum+=collect.get(j).getPjId()+",";
                                            }
                                        }
                                        // 结算单位
                                        if(digestStyle.split(",")[k].equals("7")){
                                            if(StringUtils.isNotBlank(collect.get(j).getPjId())){
                                                pjnum+=collect.get(j).getPjId()+",";
                                            }
                                        }
                                    }
                                    String cdgiest=mx.getCdigest();
                                    if(StringUtils.isNotBlank(bzfzNum)){
                                        cdgiest+="||"+bzfzNum.substring(0,bzfzNum.length()-1);
                                    }
                                    if(StringUtils.isNotBlank(bzfzName)){
                                        cdgiest+="||"+bzfzName.substring(0,bzfzName.length()-1);
                                    }
                                    if(StringUtils.isNotBlank(kzfzNum)){
                                        cdgiest+="||"+kzfzNum.substring(0,kzfzNum.length()-1);
                                    }
                                    if(StringUtils.isNotBlank(kzfzName)){
                                        cdgiest+="||"+kzfzName.substring(0,kzfzName.length()-1);
                                    }
                                    if(StringUtils.isNotBlank(jsfsname)){
                                        cdgiest+="||"+jsfsname.substring(0,jsfsname.length()-1);
                                    }
                                    if(StringUtils.isNotBlank(jsdate)){
                                        cdgiest+="||"+jsdate.substring(0,jsdate.length()-1);
                                    }
                                    if(StringUtils.isNotBlank(pjnum)){
                                        cdgiest+="||"+pjnum.substring(0,pjnum.length()-1);
                                    }
                                    if(StringUtils.isNotBlank(jsdw)){
                                        cdgiest+="||"+jsdw.substring(0,jsdw.length()-1);
                                    }
                                    mx.setCdigest(cdgiest);
                                }
                                mapList.add(mx);
                                if(a==0){
                                    mapList.add(new KeMuMingXiVo()
                                            .setNumber(number)
                                            .setInoIdASC(99998)
                                            .setDbillDate(collect.get(j).getIyperiod())
                                            .setCdigest("本月合计")
                                            .setMd(monthmd)
                                            .setMc(monthmc)
                                            .setYue(yue2.compareTo(BigDecimal.ZERO)<0?yue2.multiply(new BigDecimal(-1)):yue2)
                                            .setBprogerty(yuefx2==0?"平":yuefx2>0?"借":"贷")
                                            .setNdS(nd_month)
                                            .setNcS(nc_month)
                                            .setYue_num(yue_num2)
                                            .setNfrat_md(nfrat_md_month)
                                            .setNfrat_mc(nfrat_mc_month)
                                            .setYue_nfrat(yue_nfrat2)
                                    );

                                    yearmd=yearmd.add(monthmd);
                                    yearmc=yearmc.add(monthmc);
                                    // 数量核算
                                    nds_year=nds_year.add(nd_month);
                                    ncs_year=ncs_year.add(nc_month);
                                    // 外币核算
                                    nfrat_md_year=nfrat_md_year.add(nfrat_md_month);
                                    nfrat_mc_year=nfrat_mc_year.add(nfrat_mc_month);

                                    mapList.add(new KeMuMingXiVo()
                                            .setNumber(number)
                                            .setInoIdASC(99999)
                                            .setDbillDate(collect.get(j).getIyperiod())
                                            .setCdigest("本年累计")
                                            .setMd(yearmd)
                                            .setMc(yearmc)
                                            .setBprogerty(yuefx2==0?"平":yuefx2>0?"借":"贷")
                                            .setYue(yue2.compareTo(BigDecimal.ZERO)<0?yue2.multiply(new BigDecimal(-1)):yue2)
                                            .setNdS(nds_year)
                                            .setNcS(ncs_year)
                                            .setYue_num(yue_num2)
                                            .setNfrat_md(nfrat_md_year)
                                            .setNfrat_mc(nfrat_mc_year)
                                            .setYue_nfrat(yue_nfrat2)
                                    );
                                }
                            }
                            yue=yue2;
                            yue_num=yue_num2;
                            yue_nfrat=yue_nfrat2;
                        }
                    }
                }
                else {
                    mapList.add(new KeMuMingXiVo()
                            .setNumber(number)
                            .setInoIdASC(0)
                            .setCdigest("期初余额")
                            .setBprogerty(yuetmp == 0 ? "平" : yuetmp > 0 ? "借" : "贷")
                            .setYue(yue.compareTo(BigDecimal.ZERO)<0?yue.multiply(new BigDecimal(-1)):yue)
                            .setNcnum(yue_num.compareTo(BigDecimal.ZERO)<0?yue_num.multiply(new BigDecimal(-1)):yue_num)
                            .setYue_num(yue_num.compareTo(BigDecimal.ZERO)<0?yue_num.multiply(new BigDecimal(-1)):yue_num)
                            .setYue_nfrat(yue_nfrat.compareTo(BigDecimal.ZERO)<0?yue_nfrat.multiply(new BigDecimal(-1)):yue_nfrat)
                    );
                    // 科目在凭证中所有区间
                    for (int i = 0; i < iyperiodlist.size(); i++) {
                        int finalI = i;
                        List<AccvoucherVo> collect = pzlist.stream().filter(acv -> acv.getIyperiod().equals(iyperiodlist.get(finalI).getIyperiod())).collect(Collectors.toList());
                        BigDecimal yue2 = yue; // 余额
                        BigDecimal yue_num2 = yue_num;  // 余额-数量
                        BigDecimal yue_nfrat2 = yue_nfrat;  // 余额-数量

                        BigDecimal monthmd = new BigDecimal(0);
                        BigDecimal monthmc = new BigDecimal(0);
                        // ******** 数量核算 *******
                        BigDecimal nd_month = new BigDecimal(0);
                        BigDecimal nc_month = new BigDecimal(0);
                        // ******** 外币核算 *******
                        BigDecimal nfrat_md_month = new BigDecimal(0);
                        BigDecimal nfrat_mc_month = new BigDecimal(0);
                        int a = collect.size();
                        for (int j = 0; j < collect.size(); j++) {
                            int finalJ = j;
                            // 科目的计量单位
                            List<CodeKemu> unitMeasurementlist = codelist.stream().filter(c -> c.getCcode().equals(collect.get(finalJ).getCcode())).collect(Collectors.toList());
                            String unitMeasurement=unitMeasurementlist.size()>0?unitMeasurementlist.get(0).getMenterage():"";

                            // 科目的币种名称
                            List<CodeKemu> foreignCurrencylist = codelist.stream().filter(c -> c.getCcode().equals(collect.get(finalJ).getCcode())).collect(Collectors.toList());
                            String foreignCurrency =foreignCurrencylist.size()>0?foreignCurrencylist.get(0).getCurrencyType():"";

                            number++;
                            --a;
                            BigDecimal md2 = collect.get(j).getMd()== null ? new BigDecimal("0") :collect.get(j).getMd();
                            BigDecimal mc2 = collect.get(j).getMc()== null ? new BigDecimal("0") :collect.get(j).getMc();
                            // 数量核算
                            BigDecimal nds2 = collect.get(j).getNdS() == null ? new BigDecimal("0") : collect.get(j).getNdS();
                            BigDecimal ncs2 = collect.get(j).getNcS() == null ? new BigDecimal("0") : collect.get(j).getNcS();
                            // 外币核算
                            BigDecimal nfrat_md2 = collect.get(j).getNdS() == null ? new BigDecimal("0") : collect.get(j).getNfratMd();
                            BigDecimal nfrat_mc2 =collect.get(j).getNdS() == null ? new BigDecimal("0") : collect.get(j).getNfratMc();

                            monthmd = monthmd.add(md2);
                            monthmc = monthmc.add(mc2);
                            // ******** 数量核算 *******
                            nd_month = nd_month.add(nds2);
                            nc_month = nc_month.add(ncs2);
                            // ******** 外币核算 *******
                            nfrat_md_month = nfrat_md_month.add(nfrat_md2);
                            nfrat_mc_month = nfrat_mc_month.add(nfrat_mc2);

                            if (yuetmp >= 0) {
                                yue2 = yue2.add(md2).subtract(mc2);
                                yue_num2 = yue_num2.add(nds2).subtract(ncs2);
                                yue_nfrat2 = yue_nfrat2.add(nfrat_md2).subtract(nfrat_mc2);
                            } else {
                                yue2 = (yue2.multiply(new BigDecimal(-1)).add(mc2)).multiply(new BigDecimal(-1)).add(md2);
                                yue_num2 = (yue_num2.multiply(new BigDecimal(-1)).add(ncs2)).multiply(new BigDecimal(-1)).add(nds2);
                                yue_nfrat2 = (yue_nfrat2.multiply(new BigDecimal(-1)).add(nfrat_mc2)).multiply(new BigDecimal(-1)).add(nfrat_md2);
                            }

                            // 计算后在判断是否等于0
                            int yuefx2 = yue2.compareTo(new BigDecimal(0));
                            KeMuMingXiVo mx=new KeMuMingXiVo();
                            mx.setNumber(number)
                                    .setDbillDate(collect.get(j).getDbillDate())
                                    .setInoIdASC(collect.get(j).getInoId()!=null?Integer.valueOf(collect.get(j).getInoId()):0)
                                    .setInoId(collect.get(j).getCsign()+"-"+addLeftZero.addZeroForNum(String.valueOf(collect.get(j).getInoId()),4))
                                    .setCdigest(collect.get(j).getCdigest())
                                    .setMd(md2)
                                    .setMc(mc2)
                                    .setBprogerty(yuefx2 == 0 ? "平" : yuefx2 > 0 ? "借" : "贷")
                                    .setNdS(collect.get(j).getNdS())
                                    .setNcS(collect.get(j).getNcS())
                                    .setCunitPrice(new BigDecimal(collect.get(j).getCunitPrice()))
                                    .setUnitMeasurement(unitMeasurement)
                                    .setMdF(new BigDecimal(collect.get(j).getMdF()))
                                    .setNfrat_md(collect.get(j).getNfratMd())
                                    .setNfrat_mc(collect.get(j).getNfratMc())
                                    .setForeignCurrency(foreignCurrency)
                                    .setYue(yue2.compareTo(BigDecimal.ZERO) < 0 ? yue2.multiply(new BigDecimal(-1)) : yue2)
                                    .setYue_num(yue_num2)
                                    .setYue_nfrat(yue_nfrat2.compareTo(BigDecimal.ZERO) > 0 ? yue_nfrat2 : yue_nfrat2.multiply(new BigDecimal(-1)));

                            // digestStyle 0 标准辅助核算项编码 1 标准辅助核算项名称 2 扩展辅助核算项编码 3 扩展辅助核算项名称 4 结算方式名称 5 结算日期 6 票据号 7 结算单位
                            // 5个辅助项
                            if(StringUtils.isNotBlank(digestStyle)){
                                String bzfzNum="";
                                String bzfzName="";
                                String kzfzNum="";
                                String kzfzName="";
                                String jsfsname="";
                                String jsdate="";
                                String pjnum="";
                                String jsdw="";
                                for (int k = 0; k < digestStyle.split(",").length; k++) {
                                    int finalJ1 = j;
                                    // 5个辅助项
                                    // 标准辅助核算项编码
                                    if(digestStyle.split(",")[k].equals("0")){
                                        // 部门
                                        if(StringUtils.isNotBlank(collect.get(j).getCdeptId())){
                                            List<SysDepartment> collect1 = deptlist.stream().filter(dept -> dept.getUniqueCode().equals(collect.get(finalJ1).getCdeptId())).collect(Collectors.toList());
                                            if(collect1.size()>0){
                                                bzfzNum+=collect1.get(0).getDeptCode()+",";
                                            }
                                        }
                                        // 人员
                                        if(StringUtils.isNotBlank(collect.get(j).getCpersonId())){
                                            List<SysPsn> collect1 = psnlist.stream().filter(psn -> psn.getUniqueCode().equals(collect.get(finalJ1).getCpersonId())).collect(Collectors.toList());
                                            if(collect1.size()>0){
                                                bzfzNum+=collect1.get(0).getPsnCode()+",";
                                            }
                                        }
                                        // 客户
                                        if(StringUtils.isNotBlank(collect.get(j).getCcusId())){
                                            List<Customer> collect1 = cuslist.stream().filter(cus -> cus.getUniqueCode().equals(collect.get(finalJ1).getCcusId())).collect(Collectors.toList());
                                            if(collect1.size()>0){
                                                bzfzNum+=collect1.get(0).getCustCode()+",";
                                            }
                                        }
                                        // 供应商
                                        if(StringUtils.isNotBlank(collect.get(j).getCsupId())){
                                            List<Supplier> collect1 = suplist.stream().filter(sup -> sup.getUniqueCode().equals(collect.get(finalJ1).getCsupId())).collect(Collectors.toList());
                                            if(collect1.size()>0){
                                                bzfzNum+=collect1.get(0).getCustCode()+",";
                                            }
                                        }
                                        // 项目
                                        if(StringUtils.isNotBlank(collect.get(j).getProjectId())){
                                            List<Project> collect1 = prolist.stream().filter(pro -> pro.getUniqueCode().equals(collect.get(finalJ1).getProjectId())).collect(Collectors.toList());
                                            if(collect1.size()>0){
                                                bzfzNum+=collect1.get(0).getProjectCode()+",";
                                            }
                                        }
                                    }
                                    // 标准辅助核算项名称
                                    if(digestStyle.split(",")[k].equals("1")){
                                        // 部门
                                        if(StringUtils.isNotBlank(collect.get(j).getCdeptId())){
                                            List<SysDepartment> collect1 = deptlist.stream().filter(dept -> dept.getUniqueCode().equals(collect.get(finalJ1).getCdeptId())).collect(Collectors.toList());
                                            if(collect1.size()>0){
                                                bzfzName+=collect1.get(0).getDeptName()+",";
                                            }
                                        }
                                        // 人员
                                        if(StringUtils.isNotBlank(collect.get(j).getCpersonId())){
                                            List<SysPsn> collect1 = psnlist.stream().filter(psn -> psn.getUniqueCode().equals(collect.get(finalJ1).getCpersonId())).collect(Collectors.toList());
                                            if(collect1.size()>0){
                                                bzfzName+=collect1.get(0).getPsnName()+",";
                                            }
                                        }
                                        // 客户
                                        if(StringUtils.isNotBlank(collect.get(j).getCcusId())){
                                            List<Customer> collect1 = cuslist.stream().filter(cus -> cus.getUniqueCode().equals(collect.get(finalJ1).getCcusId())).collect(Collectors.toList());
                                            if(collect1.size()>0){
                                                bzfzName+=collect1.get(0).getCustName()+",";
                                            }
                                        }
                                        // 供应商
                                        if(StringUtils.isNotBlank(collect.get(j).getCsupId())){
                                            List<Supplier> collect1 = suplist.stream().filter(sup -> sup.getUniqueCode().equals(collect.get(finalJ1).getCsupId())).collect(Collectors.toList());
                                            if(collect1.size()>0){
                                                bzfzName+=collect1.get(0).getCustName()+",";
                                            }
                                        }
                                        // 项目
                                        if(StringUtils.isNotBlank(collect.get(j).getProjectId())){
                                            List<Project> collect1 = prolist.stream().filter(pro -> pro.getUniqueCode().equals(collect.get(finalJ1).getProjectId())).collect(Collectors.toList());
                                            if(collect1.size()>0){
                                                bzfzName+=collect1.get(0).getProjectName()+",";
                                            }
                                        }
                                    }
                                    // 扩展辅助核算项编码
                                    if(digestStyle.split(",")[k].equals("2")){
                                        if(StringUtils.isNotBlank(collect.get(j).getCdfine1())){
                                            String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine1());
                                            kzfzNum+=ccode.equals("")?"":ccode+",";
                                        }
                                        if(StringUtils.isNotBlank(collect.get(j).getCdfine2())){
                                            String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine2());
                                            kzfzNum+=ccode.equals("")?"":ccode+",";
                                        }
                                        if(StringUtils.isNotBlank(collect.get(j).getCdfine3())){
                                            String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine3());
                                            kzfzNum+=ccode.equals("")?"":ccode+",";
                                        }
                                        if(StringUtils.isNotBlank(collect.get(j).getCdfine4())){
                                            String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine4());
                                            kzfzNum+=ccode.equals("")?"":ccode+",";
                                        }
                                        if(StringUtils.isNotBlank(collect.get(j).getCdfine5())){
                                            String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine5());
                                            kzfzNum+=ccode.equals("")?"":ccode+",";
                                        }
                                        if(StringUtils.isNotBlank(collect.get(j).getCdfine6())){
                                            String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine6());
                                            kzfzNum+=ccode.equals("")?"":ccode+",";
                                        }
                                        if(StringUtils.isNotBlank(collect.get(j).getCdfine7())){
                                            String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine7());
                                            kzfzNum+=ccode.equals("")?"":ccode+",";
                                        }
                                        if(StringUtils.isNotBlank(collect.get(j).getCdfine8())){
                                            String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine8());
                                            kzfzNum+=ccode.equals("")?"":ccode+",";
                                        }
                                        if(StringUtils.isNotBlank(collect.get(j).getCdfine9())){
                                            String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine9());
                                            kzfzNum+=ccode.equals("")?"":ccode+",";
                                        }
                                        if(StringUtils.isNotBlank(collect.get(j).getCdfine10())){
                                            String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine10());
                                            kzfzNum+=ccode.equals("")?"":ccode+",";
                                        }
                                        if(StringUtils.isNotBlank(collect.get(j).getCdfine11())){
                                            String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine11());
                                            kzfzNum+=ccode.equals("")?"":ccode+",";
                                        }
                                        if(StringUtils.isNotBlank(collect.get(j).getCdfine12())){
                                            String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine12());
                                            kzfzNum+=ccode.equals("")?"":ccode+",";
                                        }
                                        if(StringUtils.isNotBlank(collect.get(j).getCdfine13())){
                                            String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine13());
                                            kzfzNum+=ccode.equals("")?"":ccode+",";
                                        }
                                        if(StringUtils.isNotBlank(collect.get(j).getCdfine14())){
                                            String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine14());
                                            kzfzNum+=ccode.equals("")?"":ccode+",";
                                        }
                                        if(StringUtils.isNotBlank(collect.get(j).getCdfine15())){
                                            String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine15());
                                            kzfzNum+=ccode.equals("")?"":ccode+",";
                                        }
                                        if(StringUtils.isNotBlank(collect.get(j).getCdfine16())){
                                            String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine16());
                                            kzfzNum+=ccode.equals("")?"":ccode+",";
                                        }
                                        if(StringUtils.isNotBlank(collect.get(j).getCdfine17())){
                                            String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine17());
                                            kzfzNum+=ccode.equals("")?"":ccode+",";
                                        }
                                        if(StringUtils.isNotBlank(collect.get(j).getCdfine18())){
                                            String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine18());
                                            kzfzNum+=ccode.equals("")?"":ccode+",";
                                        }
                                        if(StringUtils.isNotBlank(collect.get(j).getCdfine19())){
                                            String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine19());
                                            kzfzNum+=ccode.equals("")?"":ccode+",";
                                        }
                                        if(StringUtils.isNotBlank(collect.get(j).getCdfine20())){
                                            String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine20());
                                            kzfzNum+=ccode.equals("")?"":ccode+",";
                                        }
                                        if(StringUtils.isNotBlank(collect.get(j).getCdfine21())){
                                            String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine21());
                                            kzfzNum+=ccode.equals("")?"":ccode+",";
                                        }
                                        if(StringUtils.isNotBlank(collect.get(j).getCdfine22())){
                                            String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine22());
                                            kzfzNum+=ccode.equals("")?"":ccode+",";
                                        }
                                        if(StringUtils.isNotBlank(collect.get(j).getCdfine23())){
                                            String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine23());
                                            kzfzNum+=ccode.equals("")?"":ccode+",";
                                        }
                                        if(StringUtils.isNotBlank(collect.get(j).getCdfine24())){
                                            String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine24());
                                            kzfzNum+=ccode.equals("")?"":ccode+",";
                                        }
                                        if(StringUtils.isNotBlank(collect.get(j).getCdfine25())){
                                            String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine25());
                                            kzfzNum+=ccode.equals("")?"":ccode+",";
                                        }
                                        if(StringUtils.isNotBlank(collect.get(j).getCdfine26())){
                                            String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine26());
                                            kzfzNum+=ccode.equals("")?"":ccode+",";
                                        }
                                        if(StringUtils.isNotBlank(collect.get(j).getCdfine27())){
                                            String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine27());
                                            kzfzNum+=ccode.equals("")?"":ccode+",";
                                        }
                                        if(StringUtils.isNotBlank(collect.get(j).getCdfine28())){
                                            String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine28());
                                            kzfzNum+=ccode.equals("")?"":ccode+",";
                                        }
                                        if(StringUtils.isNotBlank(collect.get(j).getCdfine29())){
                                            String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine29());
                                            kzfzNum+=ccode.equals("")?"":ccode+",";
                                        }
                                        if(StringUtils.isNotBlank(collect.get(j).getCdfine30())){
                                            String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine30());
                                            kzfzNum+=ccode.equals("")?"":ccode+",";
                                        }

                                    }
                                    // 扩展辅助核算项名称
                                    if(digestStyle.split(",")[k].equals("3")){
                                        if(StringUtils.isNotBlank(collect.get(j).getCdfine1())){
                                            String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine1());
                                            kzfzName+=ccode.equals("")?"":ccode+",";
                                        }
                                        if(StringUtils.isNotBlank(collect.get(j).getCdfine2())){
                                            String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine2());
                                            kzfzName+=ccode.equals("")?"":ccode+",";
                                        }
                                        if(StringUtils.isNotBlank(collect.get(j).getCdfine3())){
                                            String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine3());
                                            kzfzName+=ccode.equals("")?"":ccode+",";
                                        }
                                        if(StringUtils.isNotBlank(collect.get(j).getCdfine4())){
                                            String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine4());
                                            kzfzName+=ccode.equals("")?"":ccode+",";
                                        }
                                        if(StringUtils.isNotBlank(collect.get(j).getCdfine5())){
                                            String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine5());
                                            kzfzName+=ccode.equals("")?"":ccode+",";
                                        }
                                        if(StringUtils.isNotBlank(collect.get(j).getCdfine6())){
                                            String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine6());
                                            kzfzName+=ccode.equals("")?"":ccode+",";
                                        }
                                        if(StringUtils.isNotBlank(collect.get(j).getCdfine7())){
                                            String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine7());
                                            kzfzName+=ccode.equals("")?"":ccode+",";
                                        }
                                        if(StringUtils.isNotBlank(collect.get(j).getCdfine8())){
                                            String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine8());
                                            kzfzName+=ccode.equals("")?"":ccode+",";
                                        }
                                        if(StringUtils.isNotBlank(collect.get(j).getCdfine9())){
                                            String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine9());
                                            kzfzName+=ccode.equals("")?"":ccode+",";
                                        }
                                        if(StringUtils.isNotBlank(collect.get(j).getCdfine10())){
                                            String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine10());
                                            kzfzName+=ccode.equals("")?"":ccode+",";
                                        }
                                        if(StringUtils.isNotBlank(collect.get(j).getCdfine11())){
                                            String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine11());
                                            kzfzName+=ccode.equals("")?"":ccode+",";
                                        }
                                        if(StringUtils.isNotBlank(collect.get(j).getCdfine12())){
                                            String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine12());
                                            kzfzName+=ccode.equals("")?"":ccode+",";
                                        }
                                        if(StringUtils.isNotBlank(collect.get(j).getCdfine13())){
                                            String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine13());
                                            kzfzName+=ccode.equals("")?"":ccode+",";
                                        }
                                        if(StringUtils.isNotBlank(collect.get(j).getCdfine14())){
                                            String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine14());
                                            kzfzName+=ccode.equals("")?"":ccode+",";
                                        }
                                        if(StringUtils.isNotBlank(collect.get(j).getCdfine15())){
                                            String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine15());
                                            kzfzName+=ccode.equals("")?"":ccode+",";
                                        }
                                        if(StringUtils.isNotBlank(collect.get(j).getCdfine16())){
                                            String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine16());
                                            kzfzName+=ccode.equals("")?"":ccode+",";
                                        }
                                        if(StringUtils.isNotBlank(collect.get(j).getCdfine17())){
                                            String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine17());
                                            kzfzName+=ccode.equals("")?"":ccode+",";
                                        }
                                        if(StringUtils.isNotBlank(collect.get(j).getCdfine18())){
                                            String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine18());
                                            kzfzName+=ccode.equals("")?"":ccode+",";
                                        }
                                        if(StringUtils.isNotBlank(collect.get(j).getCdfine19())){
                                            String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine19());
                                            kzfzName+=ccode.equals("")?"":ccode+",";
                                        }
                                        if(StringUtils.isNotBlank(collect.get(j).getCdfine20())){
                                            String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine20());
                                            kzfzName+=ccode.equals("")?"":ccode+",";
                                        }
                                        if(StringUtils.isNotBlank(collect.get(j).getCdfine21())){
                                            String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine21());
                                            kzfzName+=ccode.equals("")?"":ccode+",";
                                        }
                                        if(StringUtils.isNotBlank(collect.get(j).getCdfine22())){
                                            String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine22());
                                            kzfzName+=ccode.equals("")?"":ccode+",";
                                        }
                                        if(StringUtils.isNotBlank(collect.get(j).getCdfine23())){
                                            String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine23());
                                            kzfzName+=ccode.equals("")?"":ccode+",";
                                        }
                                        if(StringUtils.isNotBlank(collect.get(j).getCdfine24())){
                                            String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine24());
                                            kzfzName+=ccode.equals("")?"":ccode+",";
                                        }
                                        if(StringUtils.isNotBlank(collect.get(j).getCdfine25())){
                                            String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine25());
                                            kzfzName+=ccode.equals("")?"":ccode+",";
                                        }
                                        if(StringUtils.isNotBlank(collect.get(j).getCdfine26())){
                                            String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine26());
                                            kzfzName+=ccode.equals("")?"":ccode+",";
                                        }
                                        if(StringUtils.isNotBlank(collect.get(j).getCdfine27())){
                                            String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine27());
                                            kzfzName+=ccode.equals("")?"":ccode+",";
                                        }
                                        if(StringUtils.isNotBlank(collect.get(j).getCdfine28())){
                                            String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine28());
                                            kzfzName+=ccode.equals("")?"":ccode+",";
                                        }
                                        if(StringUtils.isNotBlank(collect.get(j).getCdfine29())){
                                            String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine29());
                                            kzfzName+=ccode.equals("")?"":ccode+",";
                                        }
                                        if(StringUtils.isNotBlank(collect.get(j).getCdfine30())){
                                            String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine30());
                                            kzfzName+=ccode.equals("")?"":ccode+",";
                                        }

                                    }
                                    // 结算方式名称
                                    if(digestStyle.split(",")[k].equals("4")){
                                        if(StringUtils.isNotBlank(collect.get(j).getPjCsettle())){
                                            List<SettModes> collect1 = settmodeslist.stream().filter(s -> s.getSettModesCode().equals(collect.get(finalJ1).getPjCsettle())).collect(Collectors.toList());
                                            if(collect1.size()>0){
                                                jsfsname+=collect1.get(0).getSettModesName()+",";
                                            }
                                        }
                                    }
                                    // 结算日期
                                    if(digestStyle.split(",")[k].equals("5")){
                                        if(StringUtils.isNotBlank(collect.get(j).getPjDate())){
                                            jsdate+=collect.get(j).getPjDate()+",";
                                        }
                                    }
                                    // 票据号
                                    if(digestStyle.split(",")[k].equals("6")){
                                        if(StringUtils.isNotBlank(collect.get(j).getPjId())){
                                            pjnum+=collect.get(j).getPjId()+",";
                                        }
                                    }
                                    // 结算单位
                                    if(digestStyle.split(",")[k].equals("7")){
                                        if(StringUtils.isNotBlank(collect.get(j).getPjId())){
                                            pjnum+=collect.get(j).getPjId()+",";
                                        }
                                    }
                                }
                                String cdgiest=mx.getCdigest();
                                if(StringUtils.isNotBlank(bzfzNum)){
                                    cdgiest+="||"+bzfzNum.substring(0,bzfzNum.length()-1);
                                }
                                if(StringUtils.isNotBlank(bzfzName)){
                                    cdgiest+="||"+bzfzName.substring(0,bzfzName.length()-1);
                                }
                                if(StringUtils.isNotBlank(kzfzNum)){
                                    cdgiest+="||"+kzfzNum.substring(0,kzfzNum.length()-1);
                                }
                                if(StringUtils.isNotBlank(kzfzName)){
                                    cdgiest+="||"+kzfzName.substring(0,kzfzName.length()-1);
                                }
                                if(StringUtils.isNotBlank(jsfsname)){
                                    cdgiest+="||"+jsfsname.substring(0,jsfsname.length()-1);
                                }
                                if(StringUtils.isNotBlank(jsdate)){
                                    cdgiest+="||"+jsdate.substring(0,jsdate.length()-1);
                                }
                                if(StringUtils.isNotBlank(pjnum)){
                                    cdgiest+="||"+pjnum.substring(0,pjnum.length()-1);
                                }
                                if(StringUtils.isNotBlank(jsdw)){
                                    cdgiest+="||"+jsdw.substring(0,jsdw.length()-1);
                                }
                                mx.setCdigest(cdgiest);
                            }
                            mapList.add(mx);
                            if (a == 0) {
                                mapList.add(new KeMuMingXiVo()
                                        .setNumber(number)
                                        .setInoIdASC(99998)
                                        .setDbillDate(collect.get(j).getIyperiod())
                                        .setCdigest("本月合计")
                                        .setMd(monthmd)
                                        .setMc(monthmc)
                                        .setYue( yue2.compareTo(BigDecimal.ZERO) < 0 ? yue2.multiply(new BigDecimal(-1)): yue2)
                                        .setBprogerty(yuefx2 == 0 ? "平" : yuefx2 > 0 ? "借" : "贷")
                                        .setNdS(nd_month)
                                        .setNcS(nc_month)
                                        .setYue_num(yue_num2)
                                        .setNfrat_md(nfrat_md_month)
                                        .setNfrat_mc(nfrat_mc_month)
                                        .setYue_nfrat( yue_nfrat2.compareTo(BigDecimal.ZERO) > 0 ?yue_nfrat2 : yue_nfrat2.multiply(new BigDecimal(-1)))
                                );

                                yearmd = yearmd.add(monthmd);
                                yearmc = yearmc.add(monthmc);
                                // 数量核算
                                nds_year = nds_year.add(nd_month);
                                ncs_year = ncs_year.add(nc_month);
                                // 外币核算
                                nfrat_md_year = nfrat_md_year.add(nfrat_md_month);
                                nfrat_mc_year = nfrat_mc_year.add(nfrat_mc_month);

                                mapList.add(new KeMuMingXiVo()
                                        .setNumber(number)
                                        .setInoIdASC(99998)
                                        .setDbillDate(collect.get(j).getIyperiod())
                                        .setCdigest("本年累计")
                                        .setMd(yearmd)
                                        .setMc(yearmc)
                                        .setBprogerty(yuefx2 == 0 ? "平" : yuefx2 > 0 ? "借" : "贷")
                                        .setYue( yue2.compareTo(BigDecimal.ZERO) < 0 ? yue2.multiply(new BigDecimal(-1)) : yue2)
                                        .setNdS(nds_year)
                                        .setNcS(ncs_year)
                                        .setYue_num(yue_num2)
                                        .setNfrat_md(nfrat_md_year)
                                        .setNfrat_mc(nfrat_mc_year)
                                        .setYue_nfrat( yue_nfrat2.compareTo(BigDecimal.ZERO) > 0 ? yue_nfrat2 : yue_nfrat2.multiply(new BigDecimal(-1)))
                                );
                            }
                        }
                        yue = yue2;
                        yue_num = yue_num2;
                        yue_nfrat = yue_nfrat2;
                    }
                }
                // 按条件过滤
                return Mono.just(
                        mapList.stream().filter(item->{
                            if(searchMap!=null){
                                // 按条件过滤
                                if (StringUtils.isNotBlank(searchMap.get("requirement")) && StringUtils.isNotBlank(searchMap.get("value"))) {
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
                            }

                            if(filterMap!=null){
                                // 过滤漏斗-借方
                                if (StringUtils.isNotBlank(filterMap.get("amountMinJf")) && StringUtils.isNotBlank(filterMap.get("amountMaxJf"))) {
                                    BigDecimal min = new BigDecimal(filterMap.get("amountMinJf"));
                                    BigDecimal max = new BigDecimal(filterMap.get("amountMaxJf"));
                                    BigDecimal s_md = new BigDecimal(item.getMd().toString().replaceAll(",",""));
                                    if (s_md.compareTo(min) < 0 || s_md.compareTo(max) > 0) {
                                        return false;
                                    }
                                }
                                // 过滤漏斗-贷方
                                if (StringUtils.isNotBlank(filterMap.get("amountMinDf")) && StringUtils.isNotBlank(filterMap.get("amountMaxDf"))) {
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
                            }
                            return true;
                        })
                );
            })
            .map(o -> R.ok().setResult(o));
    }
    
    public Mono<R> findAllDayBook(Pageable pageable,@NotNull String bend, String strDate, String endDate, String maxJc, String minJc, String minKm, String timflg, String ishaveRjz, String bz, Map<String, String> searchMap, Map<String, String> filterMap, String digestStyle,String database) {
        // digestStyle 0 标准辅助核算项编码 1 标准辅助核算项名称 2 扩展辅助核算项编码 3 扩展辅助核算项名称 4 结算方式名称 5 结算日期 6 票据号 7 结算单位
        String newstrDate = strDate.length() > 6 ? strDate.substring(0, strDate.length() - 2) : strDate;
        String newendDate = endDate.length() > 6 ? endDate.substring(0, endDate.length() - 2) : endDate;
        String year = strDate.substring(0, 4);
        String month = strDate.substring(4, 6);
        String newbend = bend.equals("1") ? "false" : "true";
        AtomicReference<Long> totalAR = new AtomicReference(0);
        return subjectInitalBalanceService.findAllSubjectInitialBalance(strDate.substring(0, 4), newbend,strDate.substring(0, 4)+"00",database,"","全部","类型")
            // 期初\凭证 list
            .flatMap(qclist -> {
                Map qcmap = (Map) qclist.getResult();
                Map map = new HashMap();
                map.put("qclist", qcmap.get("tablesData"));
                map.put("pzlist", "");       // 条件后的list
                map.put("codelist", "");       // 科目list
                map.put("pzlistAll", "");    // 全部凭证
                map.put("iyperiodlist", ""); // 科目中所有的凭证区间

                // 凭证list；前台已自动获取末级科目，方法中不在写入末级判断
                return accvoucherRepository.findByiyperiodpz(minKm + "%", strDate.substring(0, 4))
                        .collectList()
                        .flatMap(pzlist -> {
                            pzlist.stream().forEach(pz -> {
                                pz.setNdS(pz.getNdS() == null ? new BigDecimal("0") : pz.getNdS()).setNcS(pz.getNcS() == null ? new BigDecimal("0") : pz.getNcS())
                                        .setNfratMd(pz.getNfratMd() == null ? new BigDecimal("0") : pz.getNfratMd())
                                        .setNfratMc(pz.getNfratMc() == null ? new BigDecimal("0") : pz.getNfratMc())
                                        .setCunitPrice(org.apache.commons.lang3.StringUtils.isBlank(pz.getCunitPrice()) ? "0" : pz.getCunitPrice())
                                        .setMdF(org.apache.commons.lang3.StringUtils.isBlank(pz.getMdF()) ? "0" : pz.getMdF())
                                        .setForeignCurrency(StringUtils.isBlank(pz.getForeignCurrency()) ? "" : pz.getForeignCurrency());
                            });
                            List<AccvoucherVo> newlist = pzlist;
                            // ***** 日期or期间【查询条件】 *****
                            if ("qj".equals(timflg)) {
                                newlist = newlist.stream().filter(vo -> Integer.valueOf(vo.getImonth()) >= Integer.valueOf(newstrDate.substring(4, 6)) && Integer.valueOf(vo.getImonth()) <= Integer.valueOf(newendDate.substring(4, 6))).collect(Collectors.toList());
                            } else { // 日期
                                newlist = newlist.stream().filter(vo -> vo.getDbillDate() != null && Integer.valueOf(vo.getDbillDate().replaceAll("-", "")) >= Integer.valueOf(strDate) && Integer.valueOf(vo.getDbillDate().replaceAll("-", "")) <= Integer.valueOf(endDate)).collect(Collectors.toList());
                            }

                            // ****** 是否包含未记账【查询条件】 *****不包含；只获取 已记账的凭证
                            if (ishaveRjz.equals("0")) {
                                newlist = pzlist.stream().filter(vo -> StringUtils.isNotBlank(vo.getIbook()) && vo.getIbook().equals("1")).collect(Collectors.toList());
                            }
                            map.put("pzlist", newlist);
                            map.put("pzlistAll", pzlist);
                            return accvoucherRepository.findByCcodeGroupByIyperiod(year, minKm + "%", newstrDate, newendDate).collectList()
                                    .flatMap(iyp -> {
                                        map.put("iyperiodlist", iyp);

                                        //按年度所有科目list
                                        return codeKemuRepository.findAllByIyear(year).collectList()
                                                .flatMap(cl -> {
                                                    map.put("codelist", cl);
                                                    // 客户信息 list
                                                    return customerRepository.findAll().collectList()
                                                            .flatMap(cuslist->{
                                                                map.put("cuslist", cuslist);
                                                                // 供应商信息 list
                                                                return supplierRepository.findAll().collectList()
                                                                        .flatMap(suplist->{
                                                                            map.put("suplist", suplist);
                                                                            // 部门
                                                                            return departmentRepository.findAll().collectList()
                                                                                    .flatMap(deptlist->{
                                                                                        map.put("deptlist", deptlist);
                                                                                        // 人员
                                                                                        return psnRepository.findAll().collectList()
                                                                                                .flatMap(psnlist->{
                                                                                                    map.put("psnlist", psnlist);
                                                                                                    // 项目
                                                                                                    return projectRepositoryBase.findAll().collectList()
                                                                                                            .flatMap(prolist->{
                                                                                                                map.put("prolist", prolist);
                                                                                                                return fuzhuHesuanRepository.findByFlagOrderByCcode("1").collectList()
                                                                                                                        .flatMap(fzhslist -> {
                                                                                                                            map.put("fzhslist", fzhslist);
                                                                                                                            return settModesRepository.findByFlagOrderBySettModesCode("1").collectList()
                                                                                                                                    .flatMap(settmodeslist -> {
                                                                                                                                        map.put("settmodeslist", settmodeslist);
                                                                                                                                        return accvoucherRepository.findByCcodeGroupByDbillDate(minKm + "%", strDate, endDate).collectList()
                                                                                                                                                .flatMap(dbillDatelist -> {
                                                                                                                                                    map.put("dbillDatelist", dbillDatelist);
                                                                                                                                                    return Mono.just(map);
                                                                                                                                                });
                                                                                                                                    });
                                                                                                                        });
                                                                                                            });
                                                                                                });
                                                                                    });
                                                                        });
                                                            });
                                                });
                                    });
                        });
            })
            .flatMap(map -> {
                // 封装list
                List<KeMuMingXiVo> mapList = new ArrayList<>();

                List<DnillDateVo> dbillDatelist = (List<DnillDateVo>) map.get("dbillDatelist");
                List<SubjectInitialBalanceVo> qclist = (List<SubjectInitialBalanceVo>) map.get("qclist");
                List<AccvoucherVo> pzlist = (List<AccvoucherVo>) map.get("pzlist");
                List<AccvoucherVo> pzlistAll = (List<AccvoucherVo>) map.get("pzlistAll");
                List<IyperiodVo> iyperiodlist = (List<IyperiodVo>) map.get("iyperiodlist");
                List<CodeKemu> codelist = (List<CodeKemu>) map.get("codelist");
                // 去重后的凭证币种
                List<pzbzVo> pzbzlist = new ArrayList<>();
                //************************** 五个标准辅助项*****************************
                List<Customer> cuslist = (List<Customer>) map.get("cuslist");
                List<Supplier> suplist = (List<Supplier>) map.get("suplist");
                List<SysDepartment> deptlist = (List<SysDepartment>) map.get("deptlist");
                List<SysPsn> psnlist = (List<SysPsn>) map.get("psnlist");
                List<Project> prolist = (List<Project>) map.get("prolist");
                //************************** 扩展辅助项********************************
                List<FuzhuHesuan> fzhslist = (List<FuzhuHesuan>) map.get("fzhslist");
                //************************** 结算方式********************************
                List<SettModes> settmodeslist = (List<SettModes>) map.get("settmodeslist");

                // 获取maxKm期初
                List<SubjectInitialBalanceVo> qc = qclist.stream().filter(acv -> acv.getCcode().equals(minKm)).collect(Collectors.toList());
                BigDecimal md = qc.size() > 0 ? qc.get(0).getMd() : new BigDecimal("0");
                BigDecimal mc = qc.size() > 0 ? qc.get(0).getMc() : new BigDecimal("0");
                BigDecimal nds = qc.size() > 0 ? qc.get(0).getNdS() == null ? new BigDecimal("0") : qc.get(0).getNdS() : new BigDecimal("0");
                BigDecimal ncs = qc.size() > 0 ? qc.get(0).getNcS() == null ? new BigDecimal("0") : qc.get(0).getNcS() : new BigDecimal("0");

                // 外币金额
                BigDecimal nfrat_md = qc.size() == 0 ? new BigDecimal("0") : qc.get(0).getNfratMd();
                BigDecimal nfrat_mc = qc.size() == 0 ? new BigDecimal("0") : qc.get(0).getNfratMc();

                // 累计期初
                BigDecimal ljmd = qc.size() > 0 ? qc.get(0).getLjMd() == null ? new BigDecimal("0") : qc.get(0).getLjMd() : new BigDecimal("0");
                BigDecimal ljmc = qc.size() > 0 ? qc.get(0).getLjMc() == null ? new BigDecimal("0") : qc.get(0).getLjMc() : new BigDecimal("0");

                // 凭证借贷方金额
                BigDecimal pz_md = new BigDecimal(0);
                BigDecimal pz_mc = new BigDecimal(0);
                BigDecimal pz_nds = new BigDecimal(0);
                BigDecimal pz_ncs = new BigDecimal(0);
                BigDecimal pz_nfrat_md = new BigDecimal(0);
                BigDecimal pz_nfrat_mc = new BigDecimal(0);


                // 期间或日期 不是从一月开始的 计算出上月的期末余额
                if (Integer.valueOf(strDate.substring(4, 6)) > 1) {
                    String lastmonth = Integer.valueOf(month) - 1 < 10 ? "0" + (Integer.valueOf(month) - 1) : month;
                    // 获取上个月凭证发生数
                    List<AccvoucherVo> lastMonthAccVoucher = pzlistAll.stream().filter(acv -> Integer.valueOf(acv.getImonth()) <= Integer.valueOf(lastmonth) ).collect(Collectors.toList());
                    pz_md = lastMonthAccVoucher.size() == 0 ? new BigDecimal("0") : lastMonthAccVoucher.stream().map(AccvoucherVo::getMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                    pz_mc = lastMonthAccVoucher.size() == 0 ? new BigDecimal("0") : lastMonthAccVoucher.stream().map(AccvoucherVo::getMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                    pz_nds = lastMonthAccVoucher.size() == 0 ? new BigDecimal("0") : lastMonthAccVoucher.stream().map(AccvoucherVo::getNdS).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                    pz_ncs = lastMonthAccVoucher.size() == 0 ? new BigDecimal("0") : lastMonthAccVoucher.stream().map(AccvoucherVo::getNcS).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);

                    pz_nfrat_md = lastMonthAccVoucher.size() == 0 ? new BigDecimal("0") : lastMonthAccVoucher.stream().map(AccvoucherVo::getNfratMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                    pz_nfrat_mc = lastMonthAccVoucher.size() == 0 ? new BigDecimal("0") : lastMonthAccVoucher.stream().map(AccvoucherVo::getNfratMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                }
                BigDecimal jie = md.add(pz_md);
                BigDecimal dai = mc.add(pz_mc);
                BigDecimal yue = jie.subtract(dai);  // 借+借-贷+贷

                // ******** 数量核算 *******
                BigDecimal nd_jie = nds.add(pz_nds);
                BigDecimal nc_dai = ncs.add(pz_ncs);
                BigDecimal yue_num = nd_jie.subtract(nc_dai);  // 借+借-贷+贷
                // ******** 外币核算 *******
                BigDecimal nfrat_jie = nfrat_md.add(pz_nfrat_md);
                BigDecimal nfrat_dai = nfrat_mc.add(pz_nfrat_mc);
                BigDecimal yue_nfrat = nfrat_jie.subtract(nfrat_dai);  // 借+借-贷+贷
                // ******** 年度 *******
                BigDecimal yearmd = new BigDecimal(0);
                BigDecimal yearmc = new BigDecimal(0);
                BigDecimal nds_year = new BigDecimal(0);
                BigDecimal ncs_year = new BigDecimal(0);
                BigDecimal nfrat_md_year = new BigDecimal(0);
                BigDecimal nfrat_mc_year = new BigDecimal(0);

                int yuetmp = yue.compareTo(new BigDecimal(0));
                int number = 1; // 序号
                // 按币种查询
                List<CodeKemu> collect2 = codelist.stream().filter(c -> c.getCcode().indexOf(minKm) != -1 && c.getBend().equals("1")).collect(Collectors.toList()).stream().distinct().collect(Collectors.toList());
                collect2.stream().forEach(f -> {
                    if ("人民币".equals(bz) || "本位币".equals(bz)) {
                        if (f.getBend().equals("1") && StringUtils.isBlank(f.getCurrencyType())) {
                            pzbzlist.add(new pzbzVo().setCcode(f.getCcode()).setBz(f.getCurrencyType()).setOrder(0));
                        }
                    } else if ("全部".equals(bz)) {
                        if (f.getBend().equals("1")) {
                            if (StringUtils.isNotBlank(f.getCurrencyType())) {
                                pzbzlist.add(new pzbzVo().setCcode(f.getCcode()).setBz(f.getCurrencyType()).setOrder(1));
                            } else {
                                pzbzlist.add(new pzbzVo().setCcode(f.getCcode()).setBz(f.getCurrencyType()).setOrder(0));
                            }
                        }
                    } else {
                        if (f.getCurrencyType().equals(bz)) {
                            pzbzlist.add(new pzbzVo().setCcode(f.getCcode()).setBz(f.getCurrencyType()).setOrder(1));
                        }
                    }
                });

                // 判断科目是否 外币核算
                List<CodeKemu> curlist = codelist.stream().filter(ck -> ck.getCurrency().equals("1") && ck.getCcode().indexOf(minKm) != -1 && ck.getBend().equals("1")).collect(Collectors.toList());
                if (curlist.size() > 0) {
                    pzbzlist.sort(Comparator.comparing(pzbzVo::getOrder).reversed());
                    mapList.add(new KeMuMingXiVo()
                            .setNumber(number)
                            .setInoIdASC(0)
                            .setCdigest("期初余额")
                            .setBprogerty(yuetmp == 0 ? "平" : yuetmp > 0 ? "借" : "贷")
                            .setYue(yue)
                            .setNcnum(yue_num)
                            .setYue_num(yue_num)
                            .setYue_nfrat(yue_nfrat)
                    );
                    for (int q = 0; q < pzbzlist.size(); q++) {
                        yue=new BigDecimal(0);
                        yue_num=new BigDecimal(0);
                        yue_nfrat=new BigDecimal(0);

                        // 科目在凭证中所有区间
                        for (int i = 0; i < iyperiodlist.size(); i++) {
                            BigDecimal monthmd = new BigDecimal(0);
                            BigDecimal monthmc = new BigDecimal(0);
                            // ******** 数量核算 *******
                            BigDecimal nd_month = new BigDecimal(0);
                            BigDecimal nc_month = new BigDecimal(0);
                            // ******** 外币核算 *******
                            BigDecimal nfrat_md_month = new BigDecimal(0);
                            BigDecimal nfrat_mc_month = new BigDecimal(0);

                            BigDecimal yue2 = yue; // 余额
                            BigDecimal yue_num2 = yue_num;  // 余额-数量
                            BigDecimal yue_nfrat2 = yue_nfrat;  // 余额-外币

                            int finalI = i;
                            int finalQ = q;
                            for (int y = 0; y < dbillDatelist.size(); y++) {
                                // 凭证
                                int finalY = y;
                                List<AccvoucherVo> collect = pzlist.stream().filter(acv -> acv.getDbillDate().equals(dbillDatelist.get(finalY).getDbillDate())&&acv.getIyperiod().equals(iyperiodlist.get(finalI).getIyperiod()) && acv.getCcode().equals(pzbzlist.get(finalQ).getCcode())).collect(Collectors.toList());
                                BigDecimal day_md=collect.size()>0?collect.stream().map(AccvoucherVo::getMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                                BigDecimal day_mc=collect.size()>0?collect.stream().map(AccvoucherVo::getMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                                BigDecimal day_yue=new BigDecimal(0);
                                BigDecimal day_nds=collect.size()>0?collect.stream().map(AccvoucherVo::getNdS).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                                BigDecimal day_ncs=collect.size()>0?collect.stream().map(AccvoucherVo::getNcS).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                                BigDecimal day_num=new BigDecimal(0);
                                BigDecimal day_nfrat_md=collect.size()>0?collect.stream().map(AccvoucherVo::getNfratMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                                BigDecimal day_nfrat_mc=collect.size()>0?collect.stream().map(AccvoucherVo::getNfratMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                                BigDecimal day_nfrat=new BigDecimal(0);

                                if(collect.size()>0){
                                    for (int j = 0; j < collect.size(); j++) {
                                        int finalJ = j;
                                        // 科目的计量单位
                                        String unitMeasurement = codelist.stream().filter(c -> c.getCcode().equals(collect.get(finalJ).getCcode())).collect(Collectors.toList()).get(0).getMenterage();
                                        // 科目的币种名称
                                        String foreignCurrency = codelist.stream().filter(c -> c.getCcode().equals(collect.get(finalJ).getCcode())).collect(Collectors.toList()).get(0).getCurrencyType();

                                        number++;
                                        BigDecimal md2 = collect.get(j).getMd();
                                        BigDecimal mc2 = collect.get(j).getMc();
                                        // 数量核算
                                        BigDecimal nds2 = collect.get(j).getNdS() == null ? new BigDecimal("0") : collect.get(j).getNdS();
                                        BigDecimal ncs2 = collect.get(j).getNcS() == null ? new BigDecimal("0") : collect.get(j).getNcS();
                                        // 外币核算
                                        BigDecimal nfrat_md2 = collect.get(j).getNfratMd() == null ? new BigDecimal("0") : collect.get(j).getNfratMd();
                                        BigDecimal nfrat_mc2 = collect.get(j).getNfratMc() == null ? new BigDecimal("0") : collect.get(j).getNfratMc();
                                        monthmd = monthmd.add(md2);
                                        monthmc = monthmc.add(mc2);
                                        // ******** 数量核算 *******
                                        nd_month = nd_month.add(nds2);
                                        nc_month = nc_month.add(ncs2);
                                        // ******** 外币核算 *******
                                        nfrat_md_month = nfrat_md_month.add(nfrat_md2);
                                        nfrat_mc_month = nfrat_mc_month.add(nfrat_mc2);
                                        if (yuetmp >= 0) {
                                            yue2 = yue2.add(md2).subtract(mc2);
                                            yue_num2 = yue_num2.add(nds2).subtract(ncs2);
                                            yue_nfrat2 = yue_nfrat2.add(nfrat_mc2).subtract(nfrat_md2);
                                        } else {
                                            yue2 = (yue2.multiply(new BigDecimal(-1)).add(mc2)).multiply(new BigDecimal(-1)).add(md2);
                                            yue_num2 = (yue_num2.multiply(new BigDecimal(-1)).add(ncs2)).multiply(new BigDecimal(-1)).add(nds2);
                                            yue_nfrat2 = (yue_nfrat2.multiply(new BigDecimal(-1)).add(nfrat_mc2)).multiply(new BigDecimal(-1)).add(nfrat_md2);
                                        }
                                        // 计算后在判断是否等于0
                                        int yuefx2 = yue2.compareTo(new BigDecimal(0));

                                        String pjCsettle="";
                                        if(org.apache.commons.lang3.StringUtils.isNotBlank(collect.get(j).getPjCsettle())){
                                            // 结算方式
                                            List<SettModes> settmodes = settmodeslist.stream().filter(s -> s.getSettModesCode().equals(collect.get(finalJ).getPjCsettle())).collect(Collectors.toList());
                                            pjCsettle=settmodes.size()>0?settmodes.get(0).getSettModesName():"";
                                        }

                                        KeMuMingXiVo mx=new KeMuMingXiVo();
                                        mx.setNumber(number)
                                                .setDbillDate(collect.get(j).getDbillDate())
                                                .setPjCsettle(pjCsettle)
                                                .setPjId(collect.get(j).getPjId())
                                                .setInoIdASC(Integer.valueOf(collect.get(j).getInoId()))
                                                .setInoId(collect.get(j).getCsign()+"-"+addLeftZero.addZeroForNum(String.valueOf(collect.get(j).getInoId()),4))
                                                .setCdigest(collect.get(j).getCdigest())
                                                .setMd(md2)
                                                .setMc(mc2)
                                                .setBprogerty(yuefx2==0?"平":yuefx2>0?"借":"贷")
                                                .setNdS(collect.get(j).getNdS())
                                                .setNcS(collect.get(j).getNcS())
                                                .setCunitPrice(new BigDecimal(collect.get(j).getCunitPrice()))
                                                .setUnitMeasurement(unitMeasurement)
                                                .setMdF(new BigDecimal(collect.get(j).getMdF()))
                                                .setNfrat_md(collect.get(j).getNfratMd())
                                                .setNfrat_mc(collect.get(j).getNfratMc())
                                                .setForeignCurrency(foreignCurrency)
                                                .setYue(yue2.compareTo(BigDecimal.ZERO)<0?yue2.multiply(new BigDecimal(-1)):yue2)
                                                .setYue_num(yue_num2)
                                                .setYue_nfrat(yue_nfrat2);

                                        // digestStyle 0 标准辅助核算项编码 1 标准辅助核算项名称 2 扩展辅助核算项编码 3 扩展辅助核算项名称 4 结算方式名称 5 结算日期 6 票据号 7 结算单位
                                        // 5个辅助项
                                        if(StringUtils.isNotBlank(digestStyle)){
                                            String bzfzNum="";
                                            String bzfzName="";
                                            String kzfzNum="";
                                            String kzfzName="";
                                            String jsfsname="";
                                            String jsdate="";
                                            String pjnum="";
                                            String jsdw="";
                                            for (int k = 0; k < digestStyle.split(",").length; k++) {
                                                int finalJ1 = j;
                                                // 5个辅助项
                                                // 标准辅助核算项编码
                                                if(digestStyle.split(",")[k].equals("0")){
                                                    // 部门
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdeptId())){
                                                        List<SysDepartment> collect1 = deptlist.stream().filter(dept -> dept.getUniqueCode().equals(collect.get(finalJ1).getCdeptId())).collect(Collectors.toList());
                                                        if(collect1.size()>0){
                                                            bzfzNum+=collect1.get(0).getDeptCode()+",";
                                                        }
                                                    }
                                                    // 人员
                                                    if(StringUtils.isNotBlank(collect.get(j).getCpersonId())){
                                                        List<SysPsn> collect1 = psnlist.stream().filter(psn -> psn.getUniqueCode().equals(collect.get(finalJ1).getCpersonId())).collect(Collectors.toList());
                                                        if(collect1.size()>0){
                                                            bzfzNum+=collect1.get(0).getPsnCode()+",";
                                                        }
                                                    }
                                                    // 客户
                                                    if(StringUtils.isNotBlank(collect.get(j).getCcusId())){
                                                        List<Customer> collect1 = cuslist.stream().filter(cus -> cus.getUniqueCode().equals(collect.get(finalJ1).getCcusId())).collect(Collectors.toList());
                                                        if(collect1.size()>0){
                                                            bzfzNum+=collect1.get(0).getCustCode()+",";
                                                        }
                                                    }
                                                    // 供应商
                                                    if(StringUtils.isNotBlank(collect.get(j).getCsupId())){
                                                        List<Supplier> collect1 = suplist.stream().filter(sup -> sup.getUniqueCode().equals(collect.get(finalJ1).getCsupId())).collect(Collectors.toList());
                                                        if(collect1.size()>0){
                                                            bzfzNum+=collect1.get(0).getCustCode()+",";
                                                        }
                                                    }
                                                    // 项目
                                                    if(StringUtils.isNotBlank(collect.get(j).getProjectId())){
                                                        List<Project> collect1 = prolist.stream().filter(pro -> pro.getUniqueCode().equals(collect.get(finalJ1).getProjectId())).collect(Collectors.toList());
                                                        if(collect1.size()>0){
                                                            bzfzNum+=collect1.get(0).getProjectCode()+",";
                                                        }
                                                    }
                                                }
                                                // 标准辅助核算项名称
                                                if(digestStyle.split(",")[k].equals("1")){
                                                    // 部门
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdeptId())){
                                                        List<SysDepartment> collect1 = deptlist.stream().filter(dept -> dept.getUniqueCode().equals(collect.get(finalJ1).getCdeptId())).collect(Collectors.toList());
                                                        if(collect1.size()>0){
                                                            bzfzName+=collect1.get(0).getDeptName()+",";
                                                        }
                                                    }
                                                    // 人员
                                                    if(StringUtils.isNotBlank(collect.get(j).getCpersonId())){
                                                        List<SysPsn> collect1 = psnlist.stream().filter(psn -> psn.getUniqueCode().equals(collect.get(finalJ1).getCpersonId())).collect(Collectors.toList());
                                                        if(collect1.size()>0){
                                                            bzfzName+=collect1.get(0).getPsnName()+",";
                                                        }
                                                    }
                                                    // 客户
                                                    if(StringUtils.isNotBlank(collect.get(j).getCcusId())){
                                                        List<Customer> collect1 = cuslist.stream().filter(cus -> cus.getUniqueCode().equals(collect.get(finalJ1).getCcusId())).collect(Collectors.toList());
                                                        if(collect1.size()>0){
                                                            bzfzName+=collect1.get(0).getCustName()+",";
                                                        }
                                                    }
                                                    // 供应商
                                                    if(StringUtils.isNotBlank(collect.get(j).getCsupId())){
                                                        List<Supplier> collect1 = suplist.stream().filter(sup -> sup.getUniqueCode().equals(collect.get(finalJ1).getCsupId())).collect(Collectors.toList());
                                                        if(collect1.size()>0){
                                                            bzfzName+=collect1.get(0).getCustName()+",";
                                                        }
                                                    }
                                                    // 项目
                                                    if(StringUtils.isNotBlank(collect.get(j).getProjectId())){
                                                        List<Project> collect1 = prolist.stream().filter(pro -> pro.getUniqueCode().equals(collect.get(finalJ1).getProjectId())).collect(Collectors.toList());
                                                        if(collect1.size()>0){
                                                            bzfzName+=collect1.get(0).getProjectName()+",";
                                                        }
                                                    }
                                                }
                                                // 扩展辅助核算项编码
                                                if(digestStyle.split(",")[k].equals("2")){
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine1())){
                                                        String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine1());
                                                        kzfzNum+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine2())){
                                                        String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine2());
                                                        kzfzNum+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine3())){
                                                        String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine3());
                                                        kzfzNum+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine4())){
                                                        String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine4());
                                                        kzfzNum+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine5())){
                                                        String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine5());
                                                        kzfzNum+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine6())){
                                                        String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine6());
                                                        kzfzNum+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine7())){
                                                        String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine7());
                                                        kzfzNum+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine8())){
                                                        String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine8());
                                                        kzfzNum+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine9())){
                                                        String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine9());
                                                        kzfzNum+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine10())){
                                                        String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine10());
                                                        kzfzNum+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine11())){
                                                        String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine11());
                                                        kzfzNum+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine12())){
                                                        String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine12());
                                                        kzfzNum+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine13())){
                                                        String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine13());
                                                        kzfzNum+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine14())){
                                                        String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine14());
                                                        kzfzNum+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine15())){
                                                        String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine15());
                                                        kzfzNum+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine16())){
                                                        String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine16());
                                                        kzfzNum+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine17())){
                                                        String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine17());
                                                        kzfzNum+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine18())){
                                                        String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine18());
                                                        kzfzNum+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine19())){
                                                        String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine19());
                                                        kzfzNum+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine20())){
                                                        String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine20());
                                                        kzfzNum+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine21())){
                                                        String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine21());
                                                        kzfzNum+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine22())){
                                                        String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine22());
                                                        kzfzNum+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine23())){
                                                        String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine23());
                                                        kzfzNum+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine24())){
                                                        String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine24());
                                                        kzfzNum+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine25())){
                                                        String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine25());
                                                        kzfzNum+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine26())){
                                                        String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine26());
                                                        kzfzNum+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine27())){
                                                        String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine27());
                                                        kzfzNum+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine28())){
                                                        String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine28());
                                                        kzfzNum+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine29())){
                                                        String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine29());
                                                        kzfzNum+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine30())){
                                                        String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine30());
                                                        kzfzNum+=ccode.equals("")?"":ccode+",";
                                                    }

                                                }
                                                // 扩展辅助核算项名称
                                                if(digestStyle.split(",")[k].equals("3")){
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine1())){
                                                        String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine1());
                                                        kzfzName+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine2())){
                                                        String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine2());
                                                        kzfzName+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine3())){
                                                        String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine3());
                                                        kzfzName+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine4())){
                                                        String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine4());
                                                        kzfzName+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine5())){
                                                        String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine5());
                                                        kzfzName+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine6())){
                                                        String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine6());
                                                        kzfzName+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine7())){
                                                        String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine7());
                                                        kzfzName+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine8())){
                                                        String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine8());
                                                        kzfzName+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine9())){
                                                        String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine9());
                                                        kzfzName+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine10())){
                                                        String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine10());
                                                        kzfzName+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine11())){
                                                        String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine11());
                                                        kzfzName+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine12())){
                                                        String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine12());
                                                        kzfzName+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine13())){
                                                        String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine13());
                                                        kzfzName+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine14())){
                                                        String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine14());
                                                        kzfzName+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine15())){
                                                        String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine15());
                                                        kzfzName+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine16())){
                                                        String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine16());
                                                        kzfzName+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine17())){
                                                        String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine17());
                                                        kzfzName+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine18())){
                                                        String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine18());
                                                        kzfzName+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine19())){
                                                        String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine19());
                                                        kzfzName+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine20())){
                                                        String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine20());
                                                        kzfzName+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine21())){
                                                        String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine21());
                                                        kzfzName+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine22())){
                                                        String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine22());
                                                        kzfzName+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine23())){
                                                        String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine23());
                                                        kzfzName+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine24())){
                                                        String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine24());
                                                        kzfzName+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine25())){
                                                        String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine25());
                                                        kzfzName+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine26())){
                                                        String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine26());
                                                        kzfzName+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine27())){
                                                        String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine27());
                                                        kzfzName+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine28())){
                                                        String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine28());
                                                        kzfzName+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine29())){
                                                        String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine29());
                                                        kzfzName+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine30())){
                                                        String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine30());
                                                        kzfzName+=ccode.equals("")?"":ccode+",";
                                                    }

                                                }
                                                // 结算方式名称
                                                if(digestStyle.split(",")[k].equals("4")){
                                                    if(StringUtils.isNotBlank(collect.get(j).getPjCsettle())){
                                                        List<SettModes> collect1 = settmodeslist.stream().filter(s -> s.getSettModesCode().equals(collect.get(finalJ1).getPjCsettle())).collect(Collectors.toList());
                                                        if(collect1.size()>0){
                                                            jsfsname+=collect1.get(0).getSettModesName()+",";
                                                        }
                                                    }
                                                }
                                                // 结算日期
                                                if(digestStyle.split(",")[k].equals("5")){
                                                    if(StringUtils.isNotBlank(collect.get(j).getPjDate())){
                                                        jsdate+=collect.get(j).getPjDate()+",";
                                                    }
                                                }
                                                // 票据号
                                                if(digestStyle.split(",")[k].equals("6")){
                                                    if(StringUtils.isNotBlank(collect.get(j).getPjId())){
                                                        pjnum+=collect.get(j).getPjId()+",";
                                                    }
                                                }
                                                // 结算单位
                                                if(digestStyle.split(",")[k].equals("7")){
                                                    if(StringUtils.isNotBlank(collect.get(j).getPjId())){
                                                        pjnum+=collect.get(j).getPjId()+",";
                                                    }
                                                }
                                            }
                                            String cdgiest=mx.getCdigest();
                                            if(StringUtils.isNotBlank(bzfzNum)){
                                                cdgiest+="||"+bzfzNum.substring(0,bzfzNum.length()-1);
                                            }
                                            if(StringUtils.isNotBlank(bzfzName)){
                                                cdgiest+="||"+bzfzName.substring(0,bzfzName.length()-1);
                                            }
                                            if(StringUtils.isNotBlank(kzfzNum)){
                                                cdgiest+="||"+kzfzNum.substring(0,kzfzNum.length()-1);
                                            }
                                            if(StringUtils.isNotBlank(kzfzName)){
                                                cdgiest+="||"+kzfzName.substring(0,kzfzName.length()-1);
                                            }
                                            if(StringUtils.isNotBlank(jsfsname)){
                                                cdgiest+="||"+jsfsname.substring(0,jsfsname.length()-1);
                                            }
                                            if(StringUtils.isNotBlank(jsdate)){
                                                cdgiest+="||"+jsdate.substring(0,jsdate.length()-1);
                                            }
                                            if(StringUtils.isNotBlank(pjnum)){
                                                cdgiest+="||"+pjnum.substring(0,pjnum.length()-1);
                                            }
                                            if(StringUtils.isNotBlank(jsdw)){
                                                cdgiest+="||"+jsdw.substring(0,jsdw.length()-1);
                                            }
                                            mx.setCdigest(cdgiest);
                                        }
                                        mapList.add(mx);
                                    }

                                    mapList.add(new KeMuMingXiVo()
                                            .setNumber(number)
                                            .setDbillDate(dbillDatelist.get(y).getDbillDate())
                                            .setCdigest("本日合计")
                                            .setMd(day_md)
                                            .setMc(day_mc)
                                            .setYue(yue2.compareTo(BigDecimal.ZERO) < 0 ? yue2.multiply(new BigDecimal(-1)) : yue2)
                                            .setBprogerty(yue2.compareTo(BigDecimal.ZERO)==0?"平":yue2.compareTo(BigDecimal.ZERO)>0?"借":"贷")
                                            .setNdS(day_nds)
                                            .setNcS(day_ncs)
                                            .setYue_num(day_num)
                                            .setNfrat_md(day_nfrat_md)
                                            .setNfrat_mc(day_nfrat_mc)
                                            .setYue_nfrat(day_nfrat)
                                    );
                                    yue=yue2;
                                    yue_num=yue_num2;
                                    yue_nfrat=yue_nfrat2;
                                }
                            }
                            yearmd=yearmd.add(monthmd);
                            yearmc=yearmc.add(monthmc);
                            nds_year=yearmc.add(nd_month);
                            ncs_year=yearmc.add(nc_month);
                            nfrat_md_year=yearmc.add(nfrat_md_month);
                            nfrat_mc_year=yearmc.add(nfrat_mc_month);

                            mapList.add(new KeMuMingXiVo()
                                    .setNumber(number)
                                    .setDbillDate(iyperiodlist.get(i).getIyperiod())
                                    .setCdigest("本月合计")
                                    .setMd(monthmd)
                                    .setMc(monthmc)
                                    .setYue(yue2.compareTo(BigDecimal.ZERO) < 0 ? yue2.multiply(new BigDecimal(-1)) : yue2)
                                    .setBprogerty(yue2.compareTo(BigDecimal.ZERO) == 0 ? "平" : yue2.compareTo(BigDecimal.ZERO) > 0 ? "借" : "贷")
                                    .setNdS(nd_month)
                                    .setNcS(nc_month)
                                    .setYue_num(yue_num2)
                                    .setNfrat_md(nfrat_md_month)
                                    .setNfrat_mc(nfrat_mc_month)
                                    .setYue_nfrat(yue_nfrat2)
                            );
                            mapList.add(new KeMuMingXiVo()
                                    .setNumber(number)
                                    .setDbillDate(iyperiodlist.get(i).getIyperiod())
                                    .setCdigest("本年累计")
                                    .setMd(yearmd)
                                    .setMc(yearmc)
                                    .setBprogerty(yue2.compareTo(BigDecimal.ZERO) == 0 ? "平" : yue2.compareTo(BigDecimal.ZERO) > 0 ? "借" : "贷")
                                    .setYue(yue2.compareTo(BigDecimal.ZERO) < 0 ? yue2.multiply(new BigDecimal(-1)) : yue2)
                                    .setNdS(nds_year)
                                    .setNcS(ncs_year)
                                    .setYue_num(yue_num2)
                                    .setNfrat_md(nfrat_md_year)
                                    .setNfrat_mc(nfrat_mc_year)
                                    .setYue_nfrat(yue_nfrat2)
                            );
                        }
                    }
                }
                else {
                    pzlist.sort(Comparator.comparing(AccvoucherVo::getInoId));
                    mapList.add(new KeMuMingXiVo()
                            .setNumber(number)
                            .setInoIdASC(0)
                            .setCdigest("期初余额")
                            .setBprogerty(yuetmp == 0 ? "平" : yuetmp > 0 ? "借" : "贷")
                            .setYue(yue)
                            .setNcnum(yue_num)
                            .setYue_num(yue_num)
                            .setYue_nfrat(yue_nfrat)
                    );
                    // 科目在凭证中所有区间
                    for (int i = 0; i < iyperiodlist.size(); i++) {
                        BigDecimal monthmd = new BigDecimal(0);
                        BigDecimal monthmc = new BigDecimal(0);
                        // ******** 数量核算 *******
                        BigDecimal nd_month = new BigDecimal(0);
                        BigDecimal nc_month = new BigDecimal(0);
                        // ******** 外币核算 *******
                        BigDecimal nfrat_md_month = new BigDecimal(0);
                        BigDecimal nfrat_mc_month = new BigDecimal(0);

                        BigDecimal yue2 = yue; // 余额
                        BigDecimal yue_num2 = yue_num;  // 余额-数量
                        BigDecimal yue_nfrat2 = yue_nfrat;  // 余额-外币

                        int finalI = i;
                        for (int y = 0; y < dbillDatelist.size(); y++) {
                            // 凭证
                            int finalY = y;
                            List<AccvoucherVo> collect = pzlist.stream().filter(acv -> acv.getDbillDate().equals(dbillDatelist.get(finalY).getDbillDate())&&acv.getIyperiod().equals(iyperiodlist.get(finalI).getIyperiod())).collect(Collectors.toList());
                            BigDecimal day_md=collect.size()>0?collect.stream().map(AccvoucherVo::getMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                            BigDecimal day_mc=collect.size()>0?collect.stream().map(AccvoucherVo::getMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                            BigDecimal day_nds=collect.size()>0?collect.stream().map(AccvoucherVo::getNdS).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                            BigDecimal day_ncs=collect.size()>0?collect.stream().map(AccvoucherVo::getNcS).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                            BigDecimal day_num=new BigDecimal(0);
                            BigDecimal day_nfrat_md=collect.size()>0?collect.stream().map(AccvoucherVo::getNfratMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                            BigDecimal day_nfrat_mc=collect.size()>0?collect.stream().map(AccvoucherVo::getNfratMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                            BigDecimal day_nfrat=new BigDecimal(0);

                            if(collect.size()>0){
                                for (int j = 0; j < collect.size(); j++) {
                                    int finalJ = j;
                                    // 科目的计量单位
                                    String unitMeasurement = codelist.stream().filter(c -> c.getCcode().equals(collect.get(finalJ).getCcode())).collect(Collectors.toList()).get(0).getMenterage();
                                    // 科目的币种名称
                                    String foreignCurrency = codelist.stream().filter(c -> c.getCcode().equals(collect.get(finalJ).getCcode())).collect(Collectors.toList()).get(0).getCurrencyType();

                                    number++;
                                    BigDecimal md2 = collect.get(j).getMd();
                                    BigDecimal mc2 = collect.get(j).getMc();
                                    // 数量核算
                                    BigDecimal nds2 = collect.get(j).getNdS() == null ? new BigDecimal("0") : collect.get(j).getNdS();
                                    BigDecimal ncs2 = collect.get(j).getNcS() == null ? new BigDecimal("0") : collect.get(j).getNcS();
                                    // 外币核算
                                    BigDecimal nfrat_md2 = collect.get(j).getNfratMd() == null ? new BigDecimal("0") : collect.get(j).getNfratMd();
                                    BigDecimal nfrat_mc2 = collect.get(j).getNfratMc() == null ? new BigDecimal("0") : collect.get(j).getNfratMc();
                                    monthmd = monthmd.add(md2);
                                    monthmc = monthmc.add(mc2);
                                    // ******** 数量核算 *******
                                    nd_month = nd_month.add(nds2);
                                    nc_month = nc_month.add(ncs2);
                                    // ******** 外币核算 *******
                                    nfrat_md_month = nfrat_md_month.add(nfrat_md2);
                                    nfrat_mc_month = nfrat_mc_month.add(nfrat_mc2);
                                    if (yuetmp >= 0) {
                                        yue2 = yue2.add(md2).subtract(mc2);
                                        yue_num2 = yue_num2.add(nds2).subtract(ncs2);
                                        yue_nfrat2 = yue_nfrat2.add(nfrat_mc2).subtract(nfrat_md2);
                                    } else {
                                        yue2 = (yue2.multiply(new BigDecimal(-1)).add(mc2)).multiply(new BigDecimal(-1)).add(md2);
                                        yue_num2 = (yue_num2.multiply(new BigDecimal(-1)).add(ncs2)).multiply(new BigDecimal(-1)).add(nds2);
                                        yue_nfrat2 = (yue_nfrat2.multiply(new BigDecimal(-1)).add(nfrat_mc2)).multiply(new BigDecimal(-1)).add(nfrat_md2);
                                    }

                                    String pjCsettle="";
                                    if(org.apache.commons.lang3.StringUtils.isNotBlank(collect.get(j).getPjCsettle())){
                                        // 结算方式
                                        List<SettModes> settmodes = settmodeslist.stream().filter(s -> s.getSettModesCode().equals(collect.get(finalJ).getPjCsettle())).collect(Collectors.toList());
                                        pjCsettle=settmodes.size()>0?settmodes.get(0).getSettModesName():"";
                                    }

                                    // 计算后在判断是否等于0
                                    int yuefx2 = yue2.compareTo(new BigDecimal(0));
                                    KeMuMingXiVo mx=new KeMuMingXiVo();
                                    mx.setNumber(number)
                                            .setDbillDate(collect.get(j).getDbillDate())
                                            .setPjCsettle(pjCsettle)
                                            .setPjId(collect.get(j).getPjId())
                                            .setInoIdASC(Integer.valueOf(collect.get(j).getInoId()))
                                            .setInoId(collect.get(j).getCsign()+"-"+ addLeftZero.addZeroForNum(String.valueOf(collect.get(j).getInoId()),4))
                                            .setCdigest(collect.get(j).getCdigest())
                                            .setMd(md2)
                                            .setMc(mc2)
                                            .setBprogerty(yuefx2==0?"平":yuefx2>0?"借":"贷")
                                            .setNdS(collect.get(j).getNdS())
                                            .setNcS(collect.get(j).getNcS())
                                            .setCunitPrice(new BigDecimal(collect.get(j).getCunitPrice()))
                                            .setUnitMeasurement(unitMeasurement)
                                            .setMdF(new BigDecimal(collect.get(j).getMdF()))
                                            .setNfrat_md(collect.get(j).getNfratMd())
                                            .setNfrat_mc(collect.get(j).getNfratMc())
                                            .setForeignCurrency(foreignCurrency)
                                            .setYue(yue2.compareTo(BigDecimal.ZERO)<0?yue2.multiply(new BigDecimal(-1)):yue2)
                                            .setYue_num(yue_num2)
                                            .setYue_nfrat(yue_nfrat2);

                                    // digestStyle 0 标准辅助核算项编码 1 标准辅助核算项名称 2 扩展辅助核算项编码 3 扩展辅助核算项名称 4 结算方式名称 5 结算日期 6 票据号 7 结算单位
                                    // 5个辅助项
                                    if(StringUtils.isNotBlank(digestStyle)){
                                        String bzfzNum="";
                                        String bzfzName="";
                                        String kzfzNum="";
                                        String kzfzName="";
                                        String jsfsname="";
                                        String jsdate="";
                                        String pjnum="";
                                        String jsdw="";
                                        for (int k = 0; k < digestStyle.split(",").length; k++) {
                                            int finalJ1 = j;
                                            // 5个辅助项
                                            // 标准辅助核算项编码
                                            if(digestStyle.split(",")[k].equals("0")){
                                                // 部门
                                                if(StringUtils.isNotBlank(collect.get(j).getCdeptId())){
                                                    List<SysDepartment> collect1 = deptlist.stream().filter(dept -> dept.getUniqueCode().equals(collect.get(finalJ1).getCdeptId())).collect(Collectors.toList());
                                                    if(collect1.size()>0){
                                                        bzfzNum+=collect1.get(0).getDeptCode()+",";
                                                    }
                                                }
                                                // 人员
                                                if(StringUtils.isNotBlank(collect.get(j).getCpersonId())){
                                                    List<SysPsn> collect1 = psnlist.stream().filter(psn -> psn.getUniqueCode().equals(collect.get(finalJ1).getCpersonId())).collect(Collectors.toList());
                                                    if(collect1.size()>0){
                                                        bzfzNum+=collect1.get(0).getPsnCode()+",";
                                                    }
                                                }
                                                // 客户
                                                if(StringUtils.isNotBlank(collect.get(j).getCcusId())){
                                                    List<Customer> collect1 = cuslist.stream().filter(cus -> cus.getUniqueCode().equals(collect.get(finalJ1).getCcusId())).collect(Collectors.toList());
                                                    if(collect1.size()>0){
                                                        bzfzNum+=collect1.get(0).getCustCode()+",";
                                                    }
                                                }
                                                // 供应商
                                                if(StringUtils.isNotBlank(collect.get(j).getCsupId())){
                                                    List<Supplier> collect1 = suplist.stream().filter(sup -> sup.getUniqueCode().equals(collect.get(finalJ1).getCsupId())).collect(Collectors.toList());
                                                    if(collect1.size()>0){
                                                        bzfzNum+=collect1.get(0).getCustCode()+",";
                                                    }
                                                }
                                                // 项目
                                                if(StringUtils.isNotBlank(collect.get(j).getProjectId())){
                                                    List<Project> collect1 = prolist.stream().filter(pro -> pro.getUniqueCode().equals(collect.get(finalJ1).getProjectId())).collect(Collectors.toList());
                                                    if(collect1.size()>0){
                                                        bzfzNum+=collect1.get(0).getProjectCode()+",";
                                                    }
                                                }
                                            }
                                            // 标准辅助核算项名称
                                            if(digestStyle.split(",")[k].equals("1")){
                                                // 部门
                                                if(StringUtils.isNotBlank(collect.get(j).getCdeptId())){
                                                    List<SysDepartment> collect1 = deptlist.stream().filter(dept -> dept.getUniqueCode().equals(collect.get(finalJ1).getCdeptId())).collect(Collectors.toList());
                                                    if(collect1.size()>0){
                                                        bzfzName+=collect1.get(0).getDeptName()+",";
                                                    }
                                                }
                                                // 人员
                                                if(StringUtils.isNotBlank(collect.get(j).getCpersonId())){
                                                    List<SysPsn> collect1 = psnlist.stream().filter(psn -> psn.getUniqueCode().equals(collect.get(finalJ1).getCpersonId())).collect(Collectors.toList());
                                                    if(collect1.size()>0){
                                                        bzfzName+=collect1.get(0).getPsnName()+",";
                                                    }
                                                }
                                                // 客户
                                                if(StringUtils.isNotBlank(collect.get(j).getCcusId())){
                                                    List<Customer> collect1 = cuslist.stream().filter(cus -> cus.getUniqueCode().equals(collect.get(finalJ1).getCcusId())).collect(Collectors.toList());
                                                    if(collect1.size()>0){
                                                        bzfzName+=collect1.get(0).getCustName()+",";
                                                    }
                                                }
                                                // 供应商
                                                if(StringUtils.isNotBlank(collect.get(j).getCsupId())){
                                                    List<Supplier> collect1 = suplist.stream().filter(sup -> sup.getUniqueCode().equals(collect.get(finalJ1).getCsupId())).collect(Collectors.toList());
                                                    if(collect1.size()>0){
                                                        bzfzName+=collect1.get(0).getCustName()+",";
                                                    }
                                                }
                                                // 项目
                                                if(StringUtils.isNotBlank(collect.get(j).getProjectId())){
                                                    List<Project> collect1 = prolist.stream().filter(pro -> pro.getUniqueCode().equals(collect.get(finalJ1).getProjectId())).collect(Collectors.toList());
                                                    if(collect1.size()>0){
                                                        bzfzName+=collect1.get(0).getProjectName()+",";
                                                    }
                                                }
                                            }
                                            // 扩展辅助核算项编码
                                            if(digestStyle.split(",")[k].equals("2")){
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine1())){
                                                    String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine1());
                                                    kzfzNum+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine2())){
                                                    String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine2());
                                                    kzfzNum+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine3())){
                                                    String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine3());
                                                    kzfzNum+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine4())){
                                                    String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine4());
                                                    kzfzNum+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine5())){
                                                    String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine5());
                                                    kzfzNum+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine6())){
                                                    String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine6());
                                                    kzfzNum+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine7())){
                                                    String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine7());
                                                    kzfzNum+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine8())){
                                                    String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine8());
                                                    kzfzNum+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine9())){
                                                    String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine9());
                                                    kzfzNum+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine10())){
                                                    String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine10());
                                                    kzfzNum+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine11())){
                                                    String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine11());
                                                    kzfzNum+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine12())){
                                                    String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine12());
                                                    kzfzNum+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine13())){
                                                    String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine13());
                                                    kzfzNum+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine14())){
                                                    String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine14());
                                                    kzfzNum+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine15())){
                                                    String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine15());
                                                    kzfzNum+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine16())){
                                                    String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine16());
                                                    kzfzNum+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine17())){
                                                    String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine17());
                                                    kzfzNum+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine18())){
                                                    String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine18());
                                                    kzfzNum+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine19())){
                                                    String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine19());
                                                    kzfzNum+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine20())){
                                                    String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine20());
                                                    kzfzNum+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine21())){
                                                    String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine21());
                                                    kzfzNum+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine22())){
                                                    String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine22());
                                                    kzfzNum+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine23())){
                                                    String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine23());
                                                    kzfzNum+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine24())){
                                                    String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine24());
                                                    kzfzNum+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine25())){
                                                    String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine25());
                                                    kzfzNum+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine26())){
                                                    String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine26());
                                                    kzfzNum+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine27())){
                                                    String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine27());
                                                    kzfzNum+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine28())){
                                                    String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine28());
                                                    kzfzNum+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine29())){
                                                    String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine29());
                                                    kzfzNum+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine30())){
                                                    String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine30());
                                                    kzfzNum+=ccode.equals("")?"":ccode+",";
                                                }

                                            }
                                            // 扩展辅助核算项名称
                                            if(digestStyle.split(",")[k].equals("3")){
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine1())){
                                                    String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine1());
                                                    kzfzName+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine2())){
                                                    String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine2());
                                                    kzfzName+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine3())){
                                                    String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine3());
                                                    kzfzName+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine4())){
                                                    String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine4());
                                                    kzfzName+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine5())){
                                                    String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine5());
                                                    kzfzName+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine6())){
                                                    String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine6());
                                                    kzfzName+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine7())){
                                                    String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine7());
                                                    kzfzName+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine8())){
                                                    String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine8());
                                                    kzfzName+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine9())){
                                                    String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine9());
                                                    kzfzName+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine10())){
                                                    String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine10());
                                                    kzfzName+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine11())){
                                                    String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine11());
                                                    kzfzName+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine12())){
                                                    String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine12());
                                                    kzfzName+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine13())){
                                                    String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine13());
                                                    kzfzName+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine14())){
                                                    String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine14());
                                                    kzfzName+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine15())){
                                                    String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine15());
                                                    kzfzName+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine16())){
                                                    String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine16());
                                                    kzfzName+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine17())){
                                                    String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine17());
                                                    kzfzName+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine18())){
                                                    String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine18());
                                                    kzfzName+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine19())){
                                                    String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine19());
                                                    kzfzName+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine20())){
                                                    String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine20());
                                                    kzfzName+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine21())){
                                                    String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine21());
                                                    kzfzName+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine22())){
                                                    String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine22());
                                                    kzfzName+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine23())){
                                                    String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine23());
                                                    kzfzName+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine24())){
                                                    String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine24());
                                                    kzfzName+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine25())){
                                                    String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine25());
                                                    kzfzName+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine26())){
                                                    String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine26());
                                                    kzfzName+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine27())){
                                                    String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine27());
                                                    kzfzName+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine28())){
                                                    String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine28());
                                                    kzfzName+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine29())){
                                                    String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine29());
                                                    kzfzName+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine30())){
                                                    String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine30());
                                                    kzfzName+=ccode.equals("")?"":ccode+",";
                                                }

                                            }
                                            // 结算方式名称
                                            if(digestStyle.split(",")[k].equals("4")){
                                                if(StringUtils.isNotBlank(collect.get(j).getPjCsettle())){
                                                    List<SettModes> collect1 = settmodeslist.stream().filter(s -> s.getSettModesCode().equals(collect.get(finalJ1).getPjCsettle())).collect(Collectors.toList());
                                                    if(collect1.size()>0){
                                                        jsfsname+=collect1.get(0).getSettModesName()+",";
                                                    }
                                                }
                                            }
                                            // 结算日期
                                            if(digestStyle.split(",")[k].equals("5")){
                                                if(StringUtils.isNotBlank(collect.get(j).getPjDate())){
                                                    jsdate+=collect.get(j).getPjDate()+",";
                                                }
                                            }
                                            // 票据号
                                            if(digestStyle.split(",")[k].equals("6")){
                                                if(StringUtils.isNotBlank(collect.get(j).getPjId())){
                                                    pjnum+=collect.get(j).getPjId()+",";
                                                }
                                            }
                                            // 结算单位
                                            if(digestStyle.split(",")[k].equals("7")){
                                                if(StringUtils.isNotBlank(collect.get(j).getPjId())){
                                                    pjnum+=collect.get(j).getPjId()+",";
                                                }
                                            }
                                        }
                                        String cdgiest=mx.getCdigest();
                                        if(StringUtils.isNotBlank(bzfzNum)){
                                            cdgiest+="||"+bzfzNum.substring(0,bzfzNum.length()-1);
                                        }
                                        if(StringUtils.isNotBlank(bzfzName)){
                                            cdgiest+="||"+bzfzName.substring(0,bzfzName.length()-1);
                                        }
                                        if(StringUtils.isNotBlank(kzfzNum)){
                                            cdgiest+="||"+kzfzNum.substring(0,kzfzNum.length()-1);
                                        }
                                        if(StringUtils.isNotBlank(kzfzName)){
                                            cdgiest+="||"+kzfzName.substring(0,kzfzName.length()-1);
                                        }
                                        if(StringUtils.isNotBlank(jsfsname)){
                                            cdgiest+="||"+jsfsname.substring(0,jsfsname.length()-1);
                                        }
                                        if(StringUtils.isNotBlank(jsdate)){
                                            cdgiest+="||"+jsdate.substring(0,jsdate.length()-1);
                                        }
                                        if(StringUtils.isNotBlank(pjnum)){
                                            cdgiest+="||"+pjnum.substring(0,pjnum.length()-1);
                                        }
                                        if(StringUtils.isNotBlank(jsdw)){
                                            cdgiest+="||"+jsdw.substring(0,jsdw.length()-1);
                                        }
                                        mx.setCdigest(cdgiest);
                                    }
                                    mapList.add(mx);
                                }

                                mapList.add(new KeMuMingXiVo()
                                        .setNumber(number)
                                        .setDbillDate(dbillDatelist.get(y).getDbillDate())
                                        .setCdigest("本日合计")
                                        .setMd(day_md)
                                        .setMc(day_mc)
                                        .setYue(yue2.compareTo(BigDecimal.ZERO) < 0 ? yue2.multiply(new BigDecimal(-1)) : yue2)
                                        .setBprogerty(yue2.compareTo(BigDecimal.ZERO)==0?"平":yue2.compareTo(BigDecimal.ZERO)>0?"借":"贷")
                                        .setNdS(day_nds)
                                        .setNcS(day_ncs)
                                        .setYue_num(day_num)
                                        .setNfrat_md(day_nfrat_md)
                                        .setNfrat_mc(day_nfrat_mc)
                                        .setYue_nfrat(day_nfrat)
                                );
                                yue=yue2;
                                yue_num=yue_num2;
                                yue_nfrat=yue_nfrat2;
                            }
                        }
                        yearmd=yearmd.add(monthmd);
                        yearmc=yearmc.add(monthmc);
                        nds_year=yearmc.add(nd_month);
                        ncs_year=yearmc.add(nc_month);
                        nfrat_md_year=yearmc.add(nfrat_md_month);
                        nfrat_mc_year=yearmc.add(nfrat_mc_month);

                        mapList.add(new KeMuMingXiVo()
                                .setNumber(number)
                                .setDbillDate(iyperiodlist.get(i).getIyperiod())
                                .setCdigest("本月合计")
                                .setMd(monthmd)
                                .setMc(monthmc)
                                .setYue(yue2.compareTo(BigDecimal.ZERO) < 0 ? yue2.multiply(new BigDecimal(-1)) : yue2)
                                .setBprogerty(yue2.compareTo(BigDecimal.ZERO) == 0 ? "平" : yue2.compareTo(BigDecimal.ZERO) > 0 ? "借" : "贷")
                                .setNdS(nd_month)
                                .setNcS(nc_month)
                                .setYue_num(yue_num2)
                                .setNfrat_md(nfrat_md_month)
                                .setNfrat_mc(nfrat_mc_month)
                                .setYue_nfrat(yue_nfrat2)
                        );
                        mapList.add(new KeMuMingXiVo()
                                .setNumber(number)
                                .setDbillDate(iyperiodlist.get(i).getIyperiod())
                                .setCdigest("本年累计")
                                .setMd(yearmd)
                                .setMc(yearmc)
                                .setBprogerty(yue2.compareTo(BigDecimal.ZERO) == 0 ? "平" : yue2.compareTo(BigDecimal.ZERO) > 0 ? "借" : "贷")
                                .setYue(yue2.compareTo(BigDecimal.ZERO) < 0 ? yue2.multiply(new BigDecimal(-1)) : yue2)
                                .setNdS(nds_year)
                                .setNcS(ncs_year)
                                .setYue_num(yue_num2)
                                .setNfrat_md(nfrat_md_year)
                                .setNfrat_mc(nfrat_mc_year)
                                .setYue_nfrat(yue_nfrat2)
                        );
                    }
                }

                totalAR.set((long) mapList.size());
                // 按条件过滤
                return Mono.just(
                        mapList.stream().filter(item->{
                            // 按条件过滤
                            if (StringUtils.isNotBlank(searchMap.get("requirement")) && StringUtils.isNotBlank(searchMap.get("value"))) {
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
                            if (StringUtils.isNotBlank(filterMap.get("amountMinJf")) && StringUtils.isNotBlank(filterMap.get("amountMaxJf"))) {
                                BigDecimal min = new BigDecimal(filterMap.get("amountMinJf"));
                                BigDecimal max = new BigDecimal(filterMap.get("amountMaxJf"));
                                BigDecimal s_md = new BigDecimal(item.getMd().toString().replaceAll(",",""));
                                if (s_md.compareTo(min) < 0 || s_md.compareTo(max) > 0) {
                                    return false;
                                }
                            }
                            // 过滤漏斗-贷方
                            if (StringUtils.isNotBlank(filterMap.get("amountMinDf")) && StringUtils.isNotBlank(filterMap.get("amountMaxDf"))) {
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
    public String findByFuZhuHeSuanCcode(List<FuzhuHesuan> fzhslist,String cdfine){
        String bzfzNum="";
        List<FuzhuHesuan> collect = fzhslist.stream().filter(f -> f.getCdfine().equals(Integer.valueOf(cdfine))).collect(Collectors.toList());
        if(collect.size()>0){
            bzfzNum=collect.get(0).getCcode();
        }
        return bzfzNum;
    }
    public String findByFuZhuHeSuanCcodeName(List<FuzhuHesuan> fzhslist,String cdfine){
        String bzfzNum="";
        List<FuzhuHesuan> collect = fzhslist.stream().filter(f -> f.getCdfine().equals(Integer.valueOf(cdfine))).collect(Collectors.toList());
        if(collect.size()>0){
            bzfzNum=collect.get(0).getCname();
        }
        return bzfzNum;
    }

    public Mono<R> findByCodeExportExcel( String bend, String strDate, String endDate, String maxJc, String minJc, List<String> kmlist, String timflg, String ishaveRjz,
                                          String bz, Map<String, String> searchMap, Map<String, String> filterMap, String digestStyle,String database) {
        // digestStyle 0 标准辅助核算项编码 1 标准辅助核算项名称 2 扩展辅助核算项编码 3 扩展辅助核算项名称 4 结算方式名称 5 结算日期 6 票据号 7 结算单位
        String newstrDate = strDate.length() > 6 ? strDate.substring(0, strDate.length() - 2) : strDate;
        String newendDate = endDate.length() > 6 ? endDate.substring(0, endDate.length() - 2) : endDate;
        String year = strDate.substring(0, 4);
        String month = strDate.substring(4, 6);
        String newbend = bend.equals("1") ? "false" : "true";
        // 封装list
        List<Map<String,Object>> excellist = new ArrayList<>();
        return subjectInitalBalanceService.findAllSubjectInitialBalance(strDate.substring(0, 4), newbend,strDate.substring(0, 4)+"00",database,"","全部","类型")
                // 期初\凭证 list
                .flatMap(qclist -> {
                    Map qcmap = (Map) qclist.getResult();
                    Map map = new HashMap();
                    map.put("qclist", qcmap.get("tablesData"));
                    map.put("pzlist", "");       // 条件后的list
                    map.put("codelist", "");       // 科目list
                    map.put("pzlistAll", "");    // 全部凭证
                    map.put("iyperiodlist", ""); // 科目中所有的凭证区间

                    // 凭证list；前台已自动获取末级科目，方法中不在写入末级判断
                    return accvoucherRepository.findByiyperiodpz2(strDate.substring(0, 4))
                            .collectList()
                            .flatMap(pzlist -> {
                                pzlist.stream().forEach(pz -> {
                                    pz.setNdS(pz.getNdS() == null ? new BigDecimal("0") : pz.getNdS()).setNcS(pz.getNcS() == null ? new BigDecimal("0") : pz.getNcS())
                                            .setNfratMd(pz.getNfratMd() == null ? new BigDecimal("0") : pz.getNfratMd())
                                            .setNfratMc(pz.getNfratMc() == null ? new BigDecimal("0") : pz.getNfratMc())
                                            .setCunitPrice(org.apache.commons.lang3.StringUtils.isBlank(pz.getCunitPrice()) ? "0" : pz.getCunitPrice())
                                            .setMdF(org.apache.commons.lang3.StringUtils.isBlank(pz.getMdF()) ? "0" : pz.getMdF())
                                            .setForeignCurrency(StringUtils.isBlank(pz.getForeignCurrency()) ? "" : pz.getForeignCurrency());
                                });
                                List<AccvoucherVo> newlist = pzlist;
                                // ***** 日期or期间【查询条件】 *****
                                if ("qj".equals(timflg)) {
                                    newlist = newlist.stream().filter(vo -> Integer.valueOf(vo.getImonth()) >= Integer.valueOf(newstrDate.substring(4, 6)) && Integer.valueOf(vo.getImonth()) <= Integer.valueOf(newendDate.substring(4, 6))).collect(Collectors.toList());
                                } else { // 日期
                                    newlist = newlist.stream().filter(vo -> vo.getDbillDate() != null && Integer.valueOf(vo.getDbillDate().replaceAll("-", "")) >= Integer.valueOf(strDate) && Integer.valueOf(vo.getDbillDate().replaceAll("-", "")) <= Integer.valueOf(endDate)).collect(Collectors.toList());
                                }

                                // ****** 是否包含未记账【查询条件】 *****不包含；只获取 已记账的凭证
                                if (ishaveRjz.equals("0")) {
                                    newlist = pzlist.stream().filter(vo -> StringUtils.isNotBlank(vo.getIbook()) && vo.getIbook().equals("1")).collect(Collectors.toList());
                                }
                                map.put("pzlist", newlist);
                                map.put("pzlistAll", pzlist);
                                return accvoucherRepository.findByCcodeGroupByIyperiod2(year, newstrDate, newendDate).collectList()
                                        .flatMap(iyp -> {
                                            map.put("iyperiodlist", iyp);

                                            //按年度所有科目list
                                            return codeKemuRepository.findAllByIyear(year).collectList()
                                                    .flatMap(cl -> {
                                                        map.put("codelist", cl);
                                                        // 客户信息 list
                                                        return customerRepository.findAll().collectList()
                                                                .flatMap(cuslist->{
                                                                    map.put("cuslist", cuslist);
                                                                    // 供应商信息 list
                                                                    return supplierRepository.findAll().collectList()
                                                                            .flatMap(suplist->{
                                                                                map.put("suplist", suplist);
                                                                                // 部门
                                                                                return departmentRepository.findAll().collectList()
                                                                                        .flatMap(deptlist->{
                                                                                            map.put("deptlist", deptlist);
                                                                                            // 人员
                                                                                            return psnRepository.findAll().collectList()
                                                                                                    .flatMap(psnlist->{
                                                                                                        map.put("psnlist", psnlist);
                                                                                                        // 项目
                                                                                                        return projectRepositoryBase.findAll().collectList()
                                                                                                                .flatMap(prolist->{
                                                                                                                    map.put("prolist", prolist);
                                                                                                                    return fuzhuHesuanRepository.findByFlagOrderByCcode("1").collectList()
                                                                                                                            .flatMap(fzhslist -> {
                                                                                                                                map.put("fzhslist", fzhslist);
                                                                                                                                return settModesRepository.findByFlagOrderBySettModesCode("1").collectList()
                                                                                                                                        .flatMap(settmodeslist -> {
                                                                                                                                            map.put("settmodeslist", settmodeslist);
                                                                                                                                            return Mono.just(map);
                                                                                                                                        });
                                                                                                                            });
                                                                                                                });
                                                                                                    });
                                                                                        });
                                                                            });
                                                                });
                                                    });
                                        });
                            });
                })
                .flatMap(map -> {
                    List<SubjectInitialBalanceVo> qclist = (List<SubjectInitialBalanceVo>) map.get("qclist");
                    List<AccvoucherVo> pzlist = (List<AccvoucherVo>) map.get("pzlist");
                    List<AccvoucherVo> pzlistAll = (List<AccvoucherVo>) map.get("pzlistAll");
                    List<IyperiodVo> iyperiodlist = (List<IyperiodVo>) map.get("iyperiodlist");
                    List<CodeKemu> codelist = (List<CodeKemu>) map.get("codelist");
                    // 去重后的凭证币种
                    List<pzbzVo> pzbzlist = new ArrayList<>();
                    //************************** 五个标准辅助项*****************************
                    List<Customer> cuslist = (List<Customer>) map.get("cuslist");
                    List<Supplier> suplist = (List<Supplier>) map.get("suplist");
                    List<SysDepartment> deptlist = (List<SysDepartment>) map.get("deptlist");
                    List<SysPsn> psnlist = (List<SysPsn>) map.get("psnlist");
                    List<Project> prolist = (List<Project>) map.get("prolist");
                    //************************** 扩展辅助项********************************
                    List<FuzhuHesuan> fzhslist = (List<FuzhuHesuan>) map.get("fzhslist");
                    //************************** 结算方式********************************
                    List<SettModes> settmodeslist = (List<SettModes>) map.get("settmodeslist");


                    kmlist.stream().forEach(kmitem->{
                        Map<String,Object> excelvo=new HashMap<>();

                        // 封装明细list
                        List<KeMuMingXiVo> mapList = new ArrayList<>();
                        // 获取科目名称
                        List<CodeKemu> findByCodeName = codelist.stream().filter(c -> c.getCcode().equals(kmitem)).collect(Collectors.toList());
                        String ccodename=findByCodeName.size()>0?findByCodeName.get(0).getCcodeName():"";

                        List<AccvoucherVo> newpzlist = pzlist.stream().filter(pz -> pz.getCcode().startsWith(kmitem)).collect(Collectors.toList());
                        List<AccvoucherVo> newpzlistAll = pzlistAll.stream().filter(pzall -> pzall.getCcode().startsWith(kmitem)).collect(Collectors.toList());
                        List<IyperiodVo> newiyperiodlist = iyperiodlist.stream().filter(per -> per.getCcode().startsWith(kmitem)).collect(Collectors.toList());

                        // 获取maxKm期初
                        List<SubjectInitialBalanceVo> qc = qclist.stream().filter(acv -> acv.getCcode().equals(kmitem)).collect(Collectors.toList());
                        BigDecimal md = qc.size() > 0 ? qc.get(0).getMd() : new BigDecimal("0");
                        BigDecimal mc = qc.size() > 0 ? qc.get(0).getMc() : new BigDecimal("0");
                        BigDecimal nds = qc.size() > 0 ? qc.get(0).getNdS() : new BigDecimal("0");
                        BigDecimal ncs = qc.size() > 0 ? qc.get(0).getNcS() : new BigDecimal("0");

                        // 外币金额
                        BigDecimal nfrat_md = qc.size() > 0 ? qc.get(0).getNfratMd() : new BigDecimal("0");
                        BigDecimal nfrat_mc = qc.size() > 0 ? qc.get(0).getNfratMc() : new BigDecimal("0");

                        // 累计期初
                        BigDecimal ljmd = qc.size() > 0 ? qc.get(0).getLjMd() : new BigDecimal("0");
                        BigDecimal ljmc = qc.size() > 0 ? qc.get(0).getLjMc() : new BigDecimal("0");

                        // 凭证借贷方金额
                        BigDecimal pz_md = new BigDecimal(0);
                        BigDecimal pz_mc = new BigDecimal(0);
                        BigDecimal pz_nds = new BigDecimal(0);
                        BigDecimal pz_ncs = new BigDecimal(0);
                        BigDecimal pz_nfrat_md = new BigDecimal(0);
                        BigDecimal pz_nfrat_mc = new BigDecimal(0);
                        BigDecimal yearmd = new BigDecimal(0).add(ljmd);
                        BigDecimal yearmc = new BigDecimal(0).add(ljmc);
                        // ************ 数量核算 年度 **********
                        BigDecimal nds_year = new BigDecimal(0).add(nds);
                        BigDecimal ncs_year = new BigDecimal(0).add(ncs);
                        // ************ 外币核算 年度 **********
                        BigDecimal nfrat_md_year = new BigDecimal(0).add(nfrat_md);
                        BigDecimal nfrat_mc_year = new BigDecimal(0).add(nfrat_mc);

                        // 期间或日期 不是从一月开始的 计算出上月的期末余额
                        if (Integer.valueOf(strDate.substring(4, 6)) > 1) {
                            String lastmonth = Integer.valueOf(month) - 1 < 10 ? "0" + (Integer.valueOf(month) - 1) : month;
                            // 获取上个月凭证发生数
                            List<AccvoucherVo> lastMonthAccVoucher = newpzlistAll.stream().filter(acv -> Integer.valueOf(acv.getImonth()) <= Integer.valueOf(lastmonth) ).collect(Collectors.toList());
                            pz_md = lastMonthAccVoucher.size() == 0 ? new BigDecimal("0") : lastMonthAccVoucher.stream().map(AccvoucherVo::getMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                            pz_mc = lastMonthAccVoucher.size() == 0 ? new BigDecimal("0") : lastMonthAccVoucher.stream().map(AccvoucherVo::getMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                            pz_nds = lastMonthAccVoucher.size() == 0 ? new BigDecimal("0") : lastMonthAccVoucher.stream().map(AccvoucherVo::getNdS).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                            pz_ncs = lastMonthAccVoucher.size() == 0 ? new BigDecimal("0") : lastMonthAccVoucher.stream().map(AccvoucherVo::getNcS).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);

                            pz_nfrat_md = lastMonthAccVoucher.size() == 0 ? new BigDecimal("0") : lastMonthAccVoucher.stream().map(AccvoucherVo::getNfratMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                            pz_nfrat_mc = lastMonthAccVoucher.size() == 0 ? new BigDecimal("0") : lastMonthAccVoucher.stream().map(AccvoucherVo::getNfratMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);

                            yearmd = yearmd.add(pz_md);
                            yearmc = yearmc.add(pz_mc);
                            // ************ 数量核算 年度 **********
                            nds_year = nds_year.add(pz_nds);
                            ncs_year = ncs_year.add(pz_ncs);
                            // ************ 外币核算 年度 **********
                            nfrat_md_year = nfrat_md_year.add(pz_nfrat_md);
                            nfrat_mc_year = nfrat_mc_year.add(pz_nfrat_mc);
                        }
                        BigDecimal jie = md.add(pz_md);
                        BigDecimal dai = mc.add(pz_mc);
                        BigDecimal yue = jie.subtract(dai);  // 借+借-贷+贷

                        // ******** 数量核算 *******
                        BigDecimal nd_jie = nds.add(pz_nds);
                        BigDecimal nc_dai = ncs.add(pz_ncs);
                        BigDecimal yue_num = nd_jie.subtract(nc_dai);  // 借+借-贷+贷
                        // ******** 外币核算 *******
                        BigDecimal nfrat_jie = nfrat_md.add(pz_nfrat_md);
                        BigDecimal nfrat_dai = nfrat_mc.add(pz_nfrat_mc);
                        BigDecimal yue_nfrat = nfrat_jie.subtract(nfrat_dai);  // 借+借-贷+贷

                        int yuetmp = yue.compareTo(new BigDecimal(0));
                        int number = 1; // 序号

                        // 按币种查询
                        List<CodeKemu> collect2 = codelist.stream().filter(c -> c.getCcode().indexOf(kmitem) != -1 && c.getBend().equals("1")).collect(Collectors.toList()).stream().distinct().collect(Collectors.toList());
                        collect2.stream().forEach(f -> {
                            if ("人民币".equals(bz) || "本位币".equals(bz)) {
                                if (f.getBend().equals("1") && StringUtils.isBlank(f.getCurrencyType())) {
                                    pzbzlist.add(new pzbzVo().setCcode(f.getCcode()).setBz(f.getCurrencyType()).setOrder(0));
                                }
                            } else if ("全部".equals(bz)) {
                                if (f.getBend().equals("1")) {
                                    if (StringUtils.isNotBlank(f.getCurrencyType())) {
                                        pzbzlist.add(new pzbzVo().setCcode(f.getCcode()).setBz(f.getCurrencyType()).setOrder(1));
                                    } else {
                                        pzbzlist.add(new pzbzVo().setCcode(f.getCcode()).setBz(f.getCurrencyType()).setOrder(0));
                                    }
                                }
                            } else {
                                if (f.getCurrencyType().equals(bz)) {
                                    pzbzlist.add(new pzbzVo().setCcode(f.getCcode()).setBz(f.getCurrencyType()).setOrder(1));
                                }
                            }
                        });

                        // 判断科目是否 外币核算
                        List<CodeKemu> curlist = codelist.stream().filter(ck -> ck.getCurrency().equals("1") && ck.getCcode().indexOf(kmitem) != -1 && ck.getBend().equals("1")).collect(Collectors.toList());
                        if (curlist.size() > 0) {
                            pzbzlist.sort(Comparator.comparing(pzbzVo::getOrder).reversed());
                            mapList.add(new KeMuMingXiVo()
                                    .setCcode(kmitem)
                                    .setCcodeName(ccodename)
                                    .setNumber(number)
                                    .setInoIdASC(0)
                                    .setCdigest("期初余额")
                                    .setBprogerty(yuetmp == 0 ? "平" : yuetmp > 0 ? "借" : "贷")
                                    .setYue(yue)
                                    .setNcnum(yue_num)
                                    .setYue_num(yue_num)
                                    .setYue_nfrat(yue_nfrat)
                            );
                            for (int q = 0; q < pzbzlist.size(); q++) {
                                yue=new BigDecimal(0);
                                yue_num=new BigDecimal(0);
                                yue_nfrat=new BigDecimal(0);

                                yearmd=new BigDecimal(0);
                                yearmc=new BigDecimal(0);
                                // 数量核算
                                nds_year=new BigDecimal(0);
                                ncs_year=new BigDecimal(0);
                                // 外币核算
                                nfrat_md_year=new BigDecimal(0);
                                nfrat_mc_year=new BigDecimal(0);

                                // 科目在凭证中所有区间
                                for (int i = 0; i < newiyperiodlist.size(); i++) {
                                    BigDecimal monthmd = new BigDecimal(0);
                                    BigDecimal monthmc = new BigDecimal(0);
                                    // ******** 数量核算 *******
                                    BigDecimal nd_month = new BigDecimal(0);
                                    BigDecimal nc_month = new BigDecimal(0);
                                    // ******** 外币核算 *******
                                    BigDecimal nfrat_md_month = new BigDecimal(0);
                                    BigDecimal nfrat_mc_month = new BigDecimal(0);

                                    int finalI = i;
                                    int finalQ = q;
                                    // 凭证
                                    List<AccvoucherVo> collect = newpzlist.stream().filter(acv -> acv.getIyperiod().equals(newiyperiodlist.get(finalI).getIyperiod()) && acv.getCcode().equals(pzbzlist.get(finalQ).getCcode())).collect(Collectors.toList());
                                    BigDecimal yue2 = yue; // 余额
                                    BigDecimal yue_num2 = yue_num;  // 余额-数量
                                    BigDecimal yue_nfrat2 = yue_nfrat;  // 余额-外币
                                    int a = collect.size();
                                    for (int j = 0; j < collect.size(); j++) {
                                        int finalJ = j;
                                        // 科目的计量单位
                                        String unitMeasurement = codelist.stream().filter(c -> c.getCcode().equals(collect.get(finalJ).getCcode())).collect(Collectors.toList()).get(0).getMenterage();
                                        // 科目的币种名称
                                        String foreignCurrency = codelist.stream().filter(c -> c.getCcode().equals(collect.get(finalJ).getCcode())).collect(Collectors.toList()).get(0).getCurrencyType();

                                        number++;
                                        --a;
                                        BigDecimal md2 = collect.get(j).getMd();
                                        BigDecimal mc2 = collect.get(j).getMc();
                                        // 数量核算
                                        BigDecimal nds2 = collect.get(j).getNdS() == null ? new BigDecimal("0") : collect.get(j).getNdS();
                                        BigDecimal ncs2 = collect.get(j).getNcS() == null ? new BigDecimal("0") : collect.get(j).getNcS();
                                        // 外币核算
                                        BigDecimal nfrat_md2 = collect.get(j).getNfratMd() == null ? new BigDecimal("0") : collect.get(j).getNfratMd();
                                        BigDecimal nfrat_mc2 = collect.get(j).getNfratMc() == null ? new BigDecimal("0") : collect.get(j).getNfratMc();
                                        monthmd = monthmd.add(md2);
                                        monthmc = monthmc.add(mc2);
                                        // ******** 数量核算 *******
                                        nd_month = nd_month.add(nds2);
                                        nc_month = nc_month.add(ncs2);
                                        // ******** 外币核算 *******
                                        nfrat_md_month = nfrat_md_month.add(nfrat_md2);
                                        nfrat_mc_month = nfrat_mc_month.add(nfrat_mc2);
                                        if (yuetmp >= 0) {
                                            yue2 = yue2.add(md2).subtract(mc2);
                                            yue_num2 = yue_num2.add(nds2).subtract(ncs2);
                                            yue_nfrat2 = yue_nfrat2.add(nfrat_mc2).subtract(nfrat_md2);
                                        } else {
                                            yue2 = (yue2.multiply(new BigDecimal(-1)).add(mc2)).multiply(new BigDecimal(-1)).add(md2);
                                            yue_num2 = (yue_num2.multiply(new BigDecimal(-1)).add(ncs2)).multiply(new BigDecimal(-1)).add(nds2);
                                            yue_nfrat2 = (yue_nfrat2.multiply(new BigDecimal(-1)).add(nfrat_mc2)).multiply(new BigDecimal(-1)).add(nfrat_md2);
                                        }
                                        // 计算后在判断是否等于0
                                        int yuefx2 = yue2.compareTo(new BigDecimal(0));
                                        KeMuMingXiVo mx=new KeMuMingXiVo();
                                        mx.setNumber(number)
                                                .setCcode(kmitem)
                                                .setCcodeName(ccodename)
                                                .setDbillDate(collect.get(j).getDbillDate())
                                                .setInoIdASC(Integer.valueOf(collect.get(j).getInoId()))
                                                .setInoId(collect.get(j).getCsign()+"-"+addLeftZero.addZeroForNum(String.valueOf(collect.get(j).getInoId()),4))
                                                .setCdigest(collect.get(j).getCdigest())
                                                .setMd(md2)
                                                .setMc(mc2)
                                                .setBprogerty(yuefx2==0?"平":yuefx2>0?"借":"贷")
                                                .setNdS(collect.get(j).getNdS())
                                                .setNcS(collect.get(j).getNcS())
                                                .setCunitPrice(new BigDecimal(collect.get(j).getCunitPrice()))
                                                .setUnitMeasurement(unitMeasurement)
                                                .setMdF(new BigDecimal(collect.get(j).getMdF()))
                                                .setNfrat_md(collect.get(j).getNfratMd())
                                                .setNfrat_mc(collect.get(j).getNfratMc())
                                                .setForeignCurrency(foreignCurrency)
                                                .setYue(yue2.compareTo(BigDecimal.ZERO)<0?yue2.multiply(new BigDecimal(-1)):yue2)
                                                .setYue_num(yue_num2)
                                                .setYue_nfrat(yue_nfrat2);

                                        // digestStyle 0 标准辅助核算项编码 1 标准辅助核算项名称 2 扩展辅助核算项编码 3 扩展辅助核算项名称 4 结算方式名称 5 结算日期 6 票据号 7 结算单位
                                        // 5个辅助项
                                        if(StringUtils.isNotBlank(digestStyle)){
                                            String bzfzNum="";
                                            String bzfzName="";
                                            String kzfzNum="";
                                            String kzfzName="";
                                            String jsfsname="";
                                            String jsdate="";
                                            String pjnum="";
                                            String jsdw="";
                                            for (int k = 0; k < digestStyle.split(",").length; k++) {
                                                int finalJ1 = j;
                                                // 5个辅助项
                                                // 标准辅助核算项编码
                                                if(digestStyle.split(",")[k].equals("0")){
                                                    // 部门
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdeptId())){
                                                        List<SysDepartment> collect1 = deptlist.stream().filter(dept -> dept.getUniqueCode().equals(collect.get(finalJ1).getCdeptId())).collect(Collectors.toList());
                                                        if(collect1.size()>0){
                                                            bzfzNum+=collect1.get(0).getDeptCode()+",";
                                                        }
                                                    }
                                                    // 人员
                                                    if(StringUtils.isNotBlank(collect.get(j).getCpersonId())){
                                                        List<SysPsn> collect1 = psnlist.stream().filter(psn -> psn.getUniqueCode().equals(collect.get(finalJ1).getCpersonId())).collect(Collectors.toList());
                                                        if(collect1.size()>0){
                                                            bzfzNum+=collect1.get(0).getPsnCode()+",";
                                                        }
                                                    }
                                                    // 客户
                                                    if(StringUtils.isNotBlank(collect.get(j).getCcusId())){
                                                        List<Customer> collect1 = cuslist.stream().filter(cus -> cus.getUniqueCode().equals(collect.get(finalJ1).getCcusId())).collect(Collectors.toList());
                                                        if(collect1.size()>0){
                                                            bzfzNum+=collect1.get(0).getCustCode()+",";
                                                        }
                                                    }
                                                    // 供应商
                                                    if(StringUtils.isNotBlank(collect.get(j).getCsupId())){
                                                        List<Supplier> collect1 = suplist.stream().filter(sup -> sup.getUniqueCode().equals(collect.get(finalJ1).getCsupId())).collect(Collectors.toList());
                                                        if(collect1.size()>0){
                                                            bzfzNum+=collect1.get(0).getCustCode()+",";
                                                        }
                                                    }
                                                    // 项目
                                                    if(StringUtils.isNotBlank(collect.get(j).getProjectId())){
                                                        List<Project> collect1 = prolist.stream().filter(pro -> pro.getUniqueCode().equals(collect.get(finalJ1).getProjectId())).collect(Collectors.toList());
                                                        if(collect1.size()>0){
                                                            bzfzNum+=collect1.get(0).getProjectCode()+",";
                                                        }
                                                    }
                                                }
                                                // 标准辅助核算项名称
                                                if(digestStyle.split(",")[k].equals("1")){
                                                    // 部门
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdeptId())){
                                                        List<SysDepartment> collect1 = deptlist.stream().filter(dept -> dept.getUniqueCode().equals(collect.get(finalJ1).getCdeptId())).collect(Collectors.toList());
                                                        if(collect1.size()>0){
                                                            bzfzName+=collect1.get(0).getDeptName()+",";
                                                        }
                                                    }
                                                    // 人员
                                                    if(StringUtils.isNotBlank(collect.get(j).getCpersonId())){
                                                        List<SysPsn> collect1 = psnlist.stream().filter(psn -> psn.getUniqueCode().equals(collect.get(finalJ1).getCpersonId())).collect(Collectors.toList());
                                                        if(collect1.size()>0){
                                                            bzfzName+=collect1.get(0).getPsnName()+",";
                                                        }
                                                    }
                                                    // 客户
                                                    if(StringUtils.isNotBlank(collect.get(j).getCcusId())){
                                                        List<Customer> collect1 = cuslist.stream().filter(cus -> cus.getUniqueCode().equals(collect.get(finalJ1).getCcusId())).collect(Collectors.toList());
                                                        if(collect1.size()>0){
                                                            bzfzName+=collect1.get(0).getCustName()+",";
                                                        }
                                                    }
                                                    // 供应商
                                                    if(StringUtils.isNotBlank(collect.get(j).getCsupId())){
                                                        List<Supplier> collect1 = suplist.stream().filter(sup -> sup.getUniqueCode().equals(collect.get(finalJ1).getCsupId())).collect(Collectors.toList());
                                                        if(collect1.size()>0){
                                                            bzfzName+=collect1.get(0).getCustName()+",";
                                                        }
                                                    }
                                                    // 项目
                                                    if(StringUtils.isNotBlank(collect.get(j).getProjectId())){
                                                        List<Project> collect1 = prolist.stream().filter(pro -> pro.getUniqueCode().equals(collect.get(finalJ1).getProjectId())).collect(Collectors.toList());
                                                        if(collect1.size()>0){
                                                            bzfzName+=collect1.get(0).getProjectName()+",";
                                                        }
                                                    }
                                                }
                                                // 扩展辅助核算项编码
                                                if(digestStyle.split(",")[k].equals("2")){
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine1())){
                                                        String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine1());
                                                        kzfzNum+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine2())){
                                                        String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine2());
                                                        kzfzNum+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine3())){
                                                        String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine3());
                                                        kzfzNum+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine4())){
                                                        String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine4());
                                                        kzfzNum+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine5())){
                                                        String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine5());
                                                        kzfzNum+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine6())){
                                                        String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine6());
                                                        kzfzNum+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine7())){
                                                        String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine7());
                                                        kzfzNum+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine8())){
                                                        String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine8());
                                                        kzfzNum+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine9())){
                                                        String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine9());
                                                        kzfzNum+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine10())){
                                                        String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine10());
                                                        kzfzNum+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine11())){
                                                        String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine11());
                                                        kzfzNum+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine12())){
                                                        String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine12());
                                                        kzfzNum+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine13())){
                                                        String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine13());
                                                        kzfzNum+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine14())){
                                                        String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine14());
                                                        kzfzNum+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine15())){
                                                        String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine15());
                                                        kzfzNum+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine16())){
                                                        String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine16());
                                                        kzfzNum+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine17())){
                                                        String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine17());
                                                        kzfzNum+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine18())){
                                                        String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine18());
                                                        kzfzNum+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine19())){
                                                        String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine19());
                                                        kzfzNum+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine20())){
                                                        String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine20());
                                                        kzfzNum+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine21())){
                                                        String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine21());
                                                        kzfzNum+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine22())){
                                                        String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine22());
                                                        kzfzNum+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine23())){
                                                        String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine23());
                                                        kzfzNum+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine24())){
                                                        String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine24());
                                                        kzfzNum+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine25())){
                                                        String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine25());
                                                        kzfzNum+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine26())){
                                                        String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine26());
                                                        kzfzNum+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine27())){
                                                        String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine27());
                                                        kzfzNum+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine28())){
                                                        String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine28());
                                                        kzfzNum+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine29())){
                                                        String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine29());
                                                        kzfzNum+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine30())){
                                                        String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine30());
                                                        kzfzNum+=ccode.equals("")?"":ccode+",";
                                                    }

                                                }
                                                // 扩展辅助核算项名称
                                                if(digestStyle.split(",")[k].equals("3")){
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine1())){
                                                        String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine1());
                                                        kzfzName+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine2())){
                                                        String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine2());
                                                        kzfzName+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine3())){
                                                        String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine3());
                                                        kzfzName+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine4())){
                                                        String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine4());
                                                        kzfzName+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine5())){
                                                        String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine5());
                                                        kzfzName+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine6())){
                                                        String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine6());
                                                        kzfzName+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine7())){
                                                        String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine7());
                                                        kzfzName+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine8())){
                                                        String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine8());
                                                        kzfzName+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine9())){
                                                        String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine9());
                                                        kzfzName+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine10())){
                                                        String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine10());
                                                        kzfzName+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine11())){
                                                        String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine11());
                                                        kzfzName+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine12())){
                                                        String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine12());
                                                        kzfzName+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine13())){
                                                        String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine13());
                                                        kzfzName+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine14())){
                                                        String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine14());
                                                        kzfzName+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine15())){
                                                        String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine15());
                                                        kzfzName+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine16())){
                                                        String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine16());
                                                        kzfzName+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine17())){
                                                        String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine17());
                                                        kzfzName+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine18())){
                                                        String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine18());
                                                        kzfzName+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine19())){
                                                        String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine19());
                                                        kzfzName+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine20())){
                                                        String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine20());
                                                        kzfzName+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine21())){
                                                        String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine21());
                                                        kzfzName+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine22())){
                                                        String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine22());
                                                        kzfzName+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine23())){
                                                        String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine23());
                                                        kzfzName+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine24())){
                                                        String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine24());
                                                        kzfzName+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine25())){
                                                        String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine25());
                                                        kzfzName+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine26())){
                                                        String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine26());
                                                        kzfzName+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine27())){
                                                        String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine27());
                                                        kzfzName+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine28())){
                                                        String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine28());
                                                        kzfzName+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine29())){
                                                        String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine29());
                                                        kzfzName+=ccode.equals("")?"":ccode+",";
                                                    }
                                                    if(StringUtils.isNotBlank(collect.get(j).getCdfine30())){
                                                        String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine30());
                                                        kzfzName+=ccode.equals("")?"":ccode+",";
                                                    }

                                                }
                                                // 结算方式名称
                                                if(digestStyle.split(",")[k].equals("4")){
                                                    if(StringUtils.isNotBlank(collect.get(j).getPjCsettle())){
                                                        List<SettModes> collect1 = settmodeslist.stream().filter(s -> s.getSettModesCode().equals(collect.get(finalJ1).getPjCsettle())).collect(Collectors.toList());
                                                        if(collect1.size()>0){
                                                            jsfsname+=collect1.get(0).getSettModesName()+",";
                                                        }
                                                    }
                                                }
                                                // 结算日期
                                                if(digestStyle.split(",")[k].equals("5")){
                                                    if(StringUtils.isNotBlank(collect.get(j).getPjDate())){
                                                        jsdate+=collect.get(j).getPjDate()+",";
                                                    }
                                                }
                                                // 票据号
                                                if(digestStyle.split(",")[k].equals("6")){
                                                    if(StringUtils.isNotBlank(collect.get(j).getPjId())){
                                                        pjnum+=collect.get(j).getPjId()+",";
                                                    }
                                                }
                                                // 结算单位
                                                if(digestStyle.split(",")[k].equals("7")){
                                                    if(StringUtils.isNotBlank(collect.get(j).getPjId())){
                                                        pjnum+=collect.get(j).getPjId()+",";
                                                    }
                                                }
                                            }
                                            String cdgiest=mx.getCdigest();
                                            if(StringUtils.isNotBlank(bzfzNum)){
                                                cdgiest+="||"+bzfzNum.substring(0,bzfzNum.length()-1);
                                            }
                                            if(StringUtils.isNotBlank(bzfzName)){
                                                cdgiest+="||"+bzfzName.substring(0,bzfzName.length()-1);
                                            }
                                            if(StringUtils.isNotBlank(kzfzNum)){
                                                cdgiest+="||"+kzfzNum.substring(0,kzfzNum.length()-1);
                                            }
                                            if(StringUtils.isNotBlank(kzfzName)){
                                                cdgiest+="||"+kzfzName.substring(0,kzfzName.length()-1);
                                            }
                                            if(StringUtils.isNotBlank(jsfsname)){
                                                cdgiest+="||"+jsfsname.substring(0,jsfsname.length()-1);
                                            }
                                            if(StringUtils.isNotBlank(jsdate)){
                                                cdgiest+="||"+jsdate.substring(0,jsdate.length()-1);
                                            }
                                            if(StringUtils.isNotBlank(pjnum)){
                                                cdgiest+="||"+pjnum.substring(0,pjnum.length()-1);
                                            }
                                            if(StringUtils.isNotBlank(jsdw)){
                                                cdgiest+="||"+jsdw.substring(0,jsdw.length()-1);
                                            }
                                            mx.setCdigest(cdgiest);
                                        }
                                        mapList.add(mx);
                                        if(a==0){
                                            mapList.add(new KeMuMingXiVo()
                                                    .setNumber(number)
                                                    .setInoIdASC(99998)
                                                    .setDbillDate(collect.get(j).getIyperiod())
                                                    .setCdigest("本月合计")
                                                    .setMd(monthmd)
                                                    .setMc(monthmc)
                                                    .setYue(yue2.compareTo(BigDecimal.ZERO)<0?yue2.multiply(new BigDecimal(-1)):yue2)
                                                    .setBprogerty(yuefx2==0?"平":yuefx2>0?"借":"贷")
                                                    .setNdS(nd_month)
                                                    .setNcS(nc_month)
                                                    .setYue_num(yue_num2)
                                                    .setNfrat_md(nfrat_md_month)
                                                    .setNfrat_mc(nfrat_mc_month)
                                                    .setYue_nfrat(yue_nfrat2)
                                            );

                                            yearmd=yearmd.add(monthmd);
                                            yearmc=yearmc.add(monthmc);
                                            // 数量核算
                                            nds_year=nds_year.add(nd_month);
                                            ncs_year=ncs_year.add(nc_month);
                                            // 外币核算
                                            nfrat_md_year=nfrat_md_year.add(nfrat_md_month);
                                            nfrat_mc_year=nfrat_mc_year.add(nfrat_mc_month);

                                            mapList.add(new KeMuMingXiVo()
                                                    .setNumber(number)
                                                    .setInoIdASC(99999)
                                                    .setDbillDate(collect.get(j).getIyperiod())
                                                    .setCdigest("本年累计")
                                                    .setMd(yearmd)
                                                    .setMc(yearmc)
                                                    .setBprogerty(yuefx2==0?"平":yuefx2>0?"借":"贷")
                                                    .setYue(yue2.compareTo(BigDecimal.ZERO)<0?yue2.multiply(new BigDecimal(-1)):yue2)
                                                    .setNdS(nds_year)
                                                    .setNcS(ncs_year)
                                                    .setYue_num(yue_num2)
                                                    .setNfrat_md(nfrat_md_year)
                                                    .setNfrat_mc(nfrat_mc_year)
                                                    .setYue_nfrat(yue_nfrat2)
                                            );
                                        }
                                    }
                                    yue=yue2;
                                    yue_num=yue_num2;
                                    yue_nfrat=yue_nfrat2;
                                }
                            }
                        }
                        else {
                            mapList.add(new KeMuMingXiVo()
                                    .setCcode(kmitem)
                                    .setCcodeName(ccodename)
                                    .setNumber(number)
                                    .setInoIdASC(0)
                                    .setCdigest("期初余额")
                                    .setBprogerty(yuetmp == 0 ? "平" : yuetmp > 0 ? "借" : "贷")
                                    .setYue(yue)
                                    .setNcnum(yue_num)
                                    .setYue_num(yue_num)
                                    .setYue_nfrat(yue_nfrat)
                            );
                            // 科目在凭证中所有区间
                            for (int i = 0; i < newiyperiodlist.size(); i++) {
                                int finalI = i;
                                List<AccvoucherVo> collect = newpzlist.stream().filter(acv -> acv.getIyperiod().equals(newiyperiodlist.get(finalI).getIyperiod())).collect(Collectors.toList());
                                BigDecimal yue2 = yue; // 余额
                                BigDecimal yue_num2 = yue_num;  // 余额-数量
                                BigDecimal yue_nfrat2 = yue_nfrat;  // 余额-数量

                                BigDecimal monthmd = new BigDecimal(0);
                                BigDecimal monthmc = new BigDecimal(0);
                                // ******** 数量核算 *******
                                BigDecimal nd_month = new BigDecimal(0);
                                BigDecimal nc_month = new BigDecimal(0);
                                // ******** 外币核算 *******
                                BigDecimal nfrat_md_month = new BigDecimal(0);
                                BigDecimal nfrat_mc_month = new BigDecimal(0);
                                int a = collect.size();
                                for (int j = 0; j < collect.size(); j++) {
                                    int finalJ = j;
                                    // 科目的计量单位
                                    List<CodeKemu> unitMeasurementlist = codelist.stream().filter(c -> c.getCcode().equals(collect.get(finalJ).getCcode())).collect(Collectors.toList());
                                    String unitMeasurement=unitMeasurementlist.size()>0?unitMeasurementlist.get(0).getMenterage():"";

                                    // 科目的币种名称
                                    List<CodeKemu> foreignCurrencylist = codelist.stream().filter(c -> c.getCcode().equals(collect.get(finalJ).getCcode())).collect(Collectors.toList());
                                    String foreignCurrency =foreignCurrencylist.size()>0?foreignCurrencylist.get(0).getCurrencyType():"";

                                    number++;
                                    --a;
                                    BigDecimal md2 = collect.get(j).getMd()== null ? new BigDecimal("0") :collect.get(j).getMd();
                                    BigDecimal mc2 = collect.get(j).getMc()== null ? new BigDecimal("0") :collect.get(j).getMc();
                                    // 数量核算
                                    BigDecimal nds2 = collect.get(j).getNdS() == null ? new BigDecimal("0") : collect.get(j).getNdS();
                                    BigDecimal ncs2 = collect.get(j).getNcS() == null ? new BigDecimal("0") : collect.get(j).getNcS();
                                    // 外币核算
                                    BigDecimal nfrat_md2 = collect.get(j).getNdS() == null ? new BigDecimal("0") : collect.get(j).getNfratMd();
                                    BigDecimal nfrat_mc2 =collect.get(j).getNdS() == null ? new BigDecimal("0") : collect.get(j).getNfratMc();

                                    monthmd = monthmd.add(md2);
                                    monthmc = monthmc.add(mc2);
                                    // ******** 数量核算 *******
                                    nd_month = nd_month.add(nds2);
                                    nc_month = nc_month.add(ncs2);
                                    // ******** 外币核算 *******
                                    nfrat_md_month = nfrat_md_month.add(nfrat_md2);
                                    nfrat_mc_month = nfrat_mc_month.add(nfrat_mc2);

                                    if (yuetmp >= 0) {
                                        yue2 = yue2.add(md2).subtract(mc2);
                                        yue_num2 = yue_num2.add(nds2).subtract(ncs2);
                                        yue_nfrat2 = yue_nfrat2.add(nfrat_md2).subtract(nfrat_mc2);
                                    } else {
                                        yue2 = (yue2.multiply(new BigDecimal(-1)).add(mc2)).multiply(new BigDecimal(-1)).add(md2);
                                        yue_num2 = (yue_num2.multiply(new BigDecimal(-1)).add(ncs2)).multiply(new BigDecimal(-1)).add(nds2);
                                        yue_nfrat2 = (yue_nfrat2.multiply(new BigDecimal(-1)).add(nfrat_mc2)).multiply(new BigDecimal(-1)).add(nfrat_md2);
                                    }

                                    // 计算后在判断是否等于0
                                    int yuefx2 = yue2.compareTo(new BigDecimal(0));
                                    KeMuMingXiVo mx=new KeMuMingXiVo();
                                    mx.setNumber(number)
                                            .setCcode(kmitem)
                                            .setCcodeName(ccodename)
                                            .setDbillDate(collect.get(j).getDbillDate())
                                            .setInoIdASC(collect.get(j).getInoId()!=null?Integer.valueOf(collect.get(j).getInoId()):0)
                                            .setInoId(collect.get(j).getCsign()+"-"+addLeftZero.addZeroForNum(String.valueOf(collect.get(j).getInoId()),4))
                                            .setCdigest(collect.get(j).getCdigest())
                                            .setMd(md2)
                                            .setMc(mc2)
                                            .setBprogerty(yuefx2 == 0 ? "平" : yuefx2 > 0 ? "借" : "贷")
                                            .setNdS(collect.get(j).getNdS())
                                            .setNcS(collect.get(j).getNcS())
                                            .setCunitPrice(new BigDecimal(collect.get(j).getCunitPrice()))
                                            .setUnitMeasurement(unitMeasurement)
                                            .setMdF(new BigDecimal(collect.get(j).getMdF()))
                                            .setNfrat_md(collect.get(j).getNfratMd())
                                            .setNfrat_mc(collect.get(j).getNfratMc())
                                            .setForeignCurrency(foreignCurrency)
                                            .setYue(yue2.compareTo(BigDecimal.ZERO) < 0 ? yue2.multiply(new BigDecimal(-1)) : yue2)
                                            .setYue_num(yue_num2)
                                            .setYue_nfrat(yue_nfrat2.compareTo(BigDecimal.ZERO) > 0 ? yue_nfrat2 : yue_nfrat2.multiply(new BigDecimal(-1)));

                                    // digestStyle 0 标准辅助核算项编码 1 标准辅助核算项名称 2 扩展辅助核算项编码 3 扩展辅助核算项名称 4 结算方式名称 5 结算日期 6 票据号 7 结算单位
                                    // 5个辅助项
                                    if(StringUtils.isNotBlank(digestStyle)){
                                        String bzfzNum="";
                                        String bzfzName="";
                                        String kzfzNum="";
                                        String kzfzName="";
                                        String jsfsname="";
                                        String jsdate="";
                                        String pjnum="";
                                        String jsdw="";
                                        for (int k = 0; k < digestStyle.split(",").length; k++) {
                                            int finalJ1 = j;
                                            // 5个辅助项
                                            // 标准辅助核算项编码
                                            if(digestStyle.split(",")[k].equals("0")){
                                                // 部门
                                                if(StringUtils.isNotBlank(collect.get(j).getCdeptId())){
                                                    List<SysDepartment> collect1 = deptlist.stream().filter(dept -> dept.getUniqueCode().equals(collect.get(finalJ1).getCdeptId())).collect(Collectors.toList());
                                                    if(collect1.size()>0){
                                                        bzfzNum+=collect1.get(0).getDeptCode()+",";
                                                    }
                                                }
                                                // 人员
                                                if(StringUtils.isNotBlank(collect.get(j).getCpersonId())){
                                                    List<SysPsn> collect1 = psnlist.stream().filter(psn -> psn.getUniqueCode().equals(collect.get(finalJ1).getCpersonId())).collect(Collectors.toList());
                                                    if(collect1.size()>0){
                                                        bzfzNum+=collect1.get(0).getPsnCode()+",";
                                                    }
                                                }
                                                // 客户
                                                if(StringUtils.isNotBlank(collect.get(j).getCcusId())){
                                                    List<Customer> collect1 = cuslist.stream().filter(cus -> cus.getUniqueCode().equals(collect.get(finalJ1).getCcusId())).collect(Collectors.toList());
                                                    if(collect1.size()>0){
                                                        bzfzNum+=collect1.get(0).getCustCode()+",";
                                                    }
                                                }
                                                // 供应商
                                                if(StringUtils.isNotBlank(collect.get(j).getCsupId())){
                                                    List<Supplier> collect1 = suplist.stream().filter(sup -> sup.getUniqueCode().equals(collect.get(finalJ1).getCsupId())).collect(Collectors.toList());
                                                    if(collect1.size()>0){
                                                        bzfzNum+=collect1.get(0).getCustCode()+",";
                                                    }
                                                }
                                                // 项目
                                                if(StringUtils.isNotBlank(collect.get(j).getProjectId())){
                                                    List<Project> collect1 = prolist.stream().filter(pro -> pro.getUniqueCode().equals(collect.get(finalJ1).getProjectId())).collect(Collectors.toList());
                                                    if(collect1.size()>0){
                                                        bzfzNum+=collect1.get(0).getProjectCode()+",";
                                                    }
                                                }
                                            }
                                            // 标准辅助核算项名称
                                            if(digestStyle.split(",")[k].equals("1")){
                                                // 部门
                                                if(StringUtils.isNotBlank(collect.get(j).getCdeptId())){
                                                    List<SysDepartment> collect1 = deptlist.stream().filter(dept -> dept.getUniqueCode().equals(collect.get(finalJ1).getCdeptId())).collect(Collectors.toList());
                                                    if(collect1.size()>0){
                                                        bzfzName+=collect1.get(0).getDeptName()+",";
                                                    }
                                                }
                                                // 人员
                                                if(StringUtils.isNotBlank(collect.get(j).getCpersonId())){
                                                    List<SysPsn> collect1 = psnlist.stream().filter(psn -> psn.getUniqueCode().equals(collect.get(finalJ1).getCpersonId())).collect(Collectors.toList());
                                                    if(collect1.size()>0){
                                                        bzfzName+=collect1.get(0).getPsnName()+",";
                                                    }
                                                }
                                                // 客户
                                                if(StringUtils.isNotBlank(collect.get(j).getCcusId())){
                                                    List<Customer> collect1 = cuslist.stream().filter(cus -> cus.getUniqueCode().equals(collect.get(finalJ1).getCcusId())).collect(Collectors.toList());
                                                    if(collect1.size()>0){
                                                        bzfzName+=collect1.get(0).getCustName()+",";
                                                    }
                                                }
                                                // 供应商
                                                if(StringUtils.isNotBlank(collect.get(j).getCsupId())){
                                                    List<Supplier> collect1 = suplist.stream().filter(sup -> sup.getUniqueCode().equals(collect.get(finalJ1).getCsupId())).collect(Collectors.toList());
                                                    if(collect1.size()>0){
                                                        bzfzName+=collect1.get(0).getCustName()+",";
                                                    }
                                                }
                                                // 项目
                                                if(StringUtils.isNotBlank(collect.get(j).getProjectId())){
                                                    List<Project> collect1 = prolist.stream().filter(pro -> pro.getUniqueCode().equals(collect.get(finalJ1).getProjectId())).collect(Collectors.toList());
                                                    if(collect1.size()>0){
                                                        bzfzName+=collect1.get(0).getProjectName()+",";
                                                    }
                                                }
                                            }
                                            // 扩展辅助核算项编码
                                            if(digestStyle.split(",")[k].equals("2")){
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine1())){
                                                    String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine1());
                                                    kzfzNum+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine2())){
                                                    String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine2());
                                                    kzfzNum+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine3())){
                                                    String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine3());
                                                    kzfzNum+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine4())){
                                                    String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine4());
                                                    kzfzNum+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine5())){
                                                    String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine5());
                                                    kzfzNum+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine6())){
                                                    String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine6());
                                                    kzfzNum+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine7())){
                                                    String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine7());
                                                    kzfzNum+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine8())){
                                                    String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine8());
                                                    kzfzNum+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine9())){
                                                    String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine9());
                                                    kzfzNum+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine10())){
                                                    String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine10());
                                                    kzfzNum+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine11())){
                                                    String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine11());
                                                    kzfzNum+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine12())){
                                                    String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine12());
                                                    kzfzNum+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine13())){
                                                    String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine13());
                                                    kzfzNum+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine14())){
                                                    String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine14());
                                                    kzfzNum+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine15())){
                                                    String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine15());
                                                    kzfzNum+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine16())){
                                                    String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine16());
                                                    kzfzNum+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine17())){
                                                    String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine17());
                                                    kzfzNum+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine18())){
                                                    String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine18());
                                                    kzfzNum+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine19())){
                                                    String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine19());
                                                    kzfzNum+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine20())){
                                                    String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine20());
                                                    kzfzNum+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine21())){
                                                    String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine21());
                                                    kzfzNum+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine22())){
                                                    String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine22());
                                                    kzfzNum+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine23())){
                                                    String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine23());
                                                    kzfzNum+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine24())){
                                                    String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine24());
                                                    kzfzNum+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine25())){
                                                    String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine25());
                                                    kzfzNum+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine26())){
                                                    String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine26());
                                                    kzfzNum+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine27())){
                                                    String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine27());
                                                    kzfzNum+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine28())){
                                                    String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine28());
                                                    kzfzNum+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine29())){
                                                    String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine29());
                                                    kzfzNum+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine30())){
                                                    String ccode=findByFuZhuHeSuanCcode(fzhslist,collect.get(j).getCdfine30());
                                                    kzfzNum+=ccode.equals("")?"":ccode+",";
                                                }

                                            }
                                            // 扩展辅助核算项名称
                                            if(digestStyle.split(",")[k].equals("3")){
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine1())){
                                                    String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine1());
                                                    kzfzName+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine2())){
                                                    String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine2());
                                                    kzfzName+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine3())){
                                                    String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine3());
                                                    kzfzName+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine4())){
                                                    String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine4());
                                                    kzfzName+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine5())){
                                                    String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine5());
                                                    kzfzName+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine6())){
                                                    String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine6());
                                                    kzfzName+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine7())){
                                                    String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine7());
                                                    kzfzName+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine8())){
                                                    String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine8());
                                                    kzfzName+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine9())){
                                                    String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine9());
                                                    kzfzName+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine10())){
                                                    String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine10());
                                                    kzfzName+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine11())){
                                                    String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine11());
                                                    kzfzName+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine12())){
                                                    String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine12());
                                                    kzfzName+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine13())){
                                                    String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine13());
                                                    kzfzName+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine14())){
                                                    String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine14());
                                                    kzfzName+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine15())){
                                                    String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine15());
                                                    kzfzName+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine16())){
                                                    String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine16());
                                                    kzfzName+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine17())){
                                                    String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine17());
                                                    kzfzName+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine18())){
                                                    String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine18());
                                                    kzfzName+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine19())){
                                                    String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine19());
                                                    kzfzName+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine20())){
                                                    String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine20());
                                                    kzfzName+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine21())){
                                                    String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine21());
                                                    kzfzName+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine22())){
                                                    String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine22());
                                                    kzfzName+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine23())){
                                                    String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine23());
                                                    kzfzName+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine24())){
                                                    String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine24());
                                                    kzfzName+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine25())){
                                                    String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine25());
                                                    kzfzName+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine26())){
                                                    String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine26());
                                                    kzfzName+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine27())){
                                                    String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine27());
                                                    kzfzName+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine28())){
                                                    String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine28());
                                                    kzfzName+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine29())){
                                                    String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine29());
                                                    kzfzName+=ccode.equals("")?"":ccode+",";
                                                }
                                                if(StringUtils.isNotBlank(collect.get(j).getCdfine30())){
                                                    String ccode=findByFuZhuHeSuanCcodeName(fzhslist,collect.get(j).getCdfine30());
                                                    kzfzName+=ccode.equals("")?"":ccode+",";
                                                }

                                            }
                                            // 结算方式名称
                                            if(digestStyle.split(",")[k].equals("4")){
                                                if(StringUtils.isNotBlank(collect.get(j).getPjCsettle())){
                                                    List<SettModes> collect1 = settmodeslist.stream().filter(s -> s.getSettModesCode().equals(collect.get(finalJ1).getPjCsettle())).collect(Collectors.toList());
                                                    if(collect1.size()>0){
                                                        jsfsname+=collect1.get(0).getSettModesName()+",";
                                                    }
                                                }
                                            }
                                            // 结算日期
                                            if(digestStyle.split(",")[k].equals("5")){
                                                if(StringUtils.isNotBlank(collect.get(j).getPjDate())){
                                                    jsdate+=collect.get(j).getPjDate()+",";
                                                }
                                            }
                                            // 票据号
                                            if(digestStyle.split(",")[k].equals("6")){
                                                if(StringUtils.isNotBlank(collect.get(j).getPjId())){
                                                    pjnum+=collect.get(j).getPjId()+",";
                                                }
                                            }
                                            // 结算单位
                                            if(digestStyle.split(",")[k].equals("7")){
                                                if(StringUtils.isNotBlank(collect.get(j).getPjId())){
                                                    pjnum+=collect.get(j).getPjId()+",";
                                                }
                                            }
                                        }
                                        String cdgiest=mx.getCdigest();
                                        if(StringUtils.isNotBlank(bzfzNum)){
                                            cdgiest+="||"+bzfzNum.substring(0,bzfzNum.length()-1);
                                        }
                                        if(StringUtils.isNotBlank(bzfzName)){
                                            cdgiest+="||"+bzfzName.substring(0,bzfzName.length()-1);
                                        }
                                        if(StringUtils.isNotBlank(kzfzNum)){
                                            cdgiest+="||"+kzfzNum.substring(0,kzfzNum.length()-1);
                                        }
                                        if(StringUtils.isNotBlank(kzfzName)){
                                            cdgiest+="||"+kzfzName.substring(0,kzfzName.length()-1);
                                        }
                                        if(StringUtils.isNotBlank(jsfsname)){
                                            cdgiest+="||"+jsfsname.substring(0,jsfsname.length()-1);
                                        }
                                        if(StringUtils.isNotBlank(jsdate)){
                                            cdgiest+="||"+jsdate.substring(0,jsdate.length()-1);
                                        }
                                        if(StringUtils.isNotBlank(pjnum)){
                                            cdgiest+="||"+pjnum.substring(0,pjnum.length()-1);
                                        }
                                        if(StringUtils.isNotBlank(jsdw)){
                                            cdgiest+="||"+jsdw.substring(0,jsdw.length()-1);
                                        }
                                        mx.setCdigest(cdgiest);
                                    }
                                    mapList.add(mx);
                                    if (a == 0) {
                                        mapList.add(new KeMuMingXiVo()
                                                .setNumber(number)
                                                .setInoIdASC(99998)
                                                .setDbillDate(collect.get(j).getIyperiod())
                                                .setCdigest("本月合计")
                                                .setMd(monthmd)
                                                .setMc(monthmc)
                                                .setYue( yue2.compareTo(BigDecimal.ZERO) < 0 ? yue2.multiply(new BigDecimal(-1)): yue2)
                                                .setBprogerty(yuefx2 == 0 ? "平" : yuefx2 > 0 ? "借" : "贷")
                                                .setNdS(nd_month)
                                                .setNcS(nc_month)
                                                .setYue_num(yue_num2)
                                                .setNfrat_md(nfrat_md_month)
                                                .setNfrat_mc(nfrat_mc_month)
                                                .setYue_nfrat( yue_nfrat2.compareTo(BigDecimal.ZERO) > 0 ?yue_nfrat2 : yue_nfrat2.multiply(new BigDecimal(-1)))
                                        );

                                        yearmd = yearmd.add(monthmd);
                                        yearmc = yearmc.add(monthmc);
                                        // 数量核算
                                        nds_year = nds_year.add(nd_month);
                                        ncs_year = ncs_year.add(nc_month);
                                        // 外币核算
                                        nfrat_md_year = nfrat_md_year.add(nfrat_md_month);
                                        nfrat_mc_year = nfrat_mc_year.add(nfrat_mc_month);

                                        mapList.add(new KeMuMingXiVo()
                                                .setNumber(number)
                                                .setInoIdASC(99998)
                                                .setDbillDate(collect.get(j).getIyperiod())
                                                .setCdigest("本年累计")
                                                .setMd(yearmd)
                                                .setMc(yearmc)
                                                .setBprogerty(yuefx2 == 0 ? "平" : yuefx2 > 0 ? "借" : "贷")
                                                .setYue( yue2.compareTo(BigDecimal.ZERO) < 0 ? yue2.multiply(new BigDecimal(-1)) : yue2)
                                                .setNdS(nds_year)
                                                .setNcS(ncs_year)
                                                .setYue_num(yue_num2)
                                                .setNfrat_md(nfrat_md_year)
                                                .setNfrat_mc(nfrat_mc_year)
                                                .setYue_nfrat( yue_nfrat2.compareTo(BigDecimal.ZERO) > 0 ? yue_nfrat2 : yue_nfrat2.multiply(new BigDecimal(-1)))
                                        );
                                    }
                                }
                                yue = yue2;
                                yue_num = yue_num2;
                                yue_nfrat = yue_nfrat2;
                            }
                        }

                        excelvo.put("ccode",kmitem);
                        excelvo.put("ccodeName",ccodename);
                        excelvo.put("mxlist", mapList);
                        excellist.add(excelvo);
                    });
                    // 按条件过滤
                    return Mono.just(excellist);
                })
                .map(o -> R.ok().setList(o));
    }

    public Mono<R> findByCodeExportExcel2( String bend, String strDate, String endDate, String maxJc, String minJc, List<String> kmlist, String timflg, String ishaveRjz,String bz) {
        String newstrDate = strDate.length() > 6 ? strDate.substring(0, strDate.length() - 2) : strDate;
        String newendDate = endDate.length() > 6 ? endDate.substring(0, endDate.length() - 2) : endDate;
        String year = strDate.substring(0, 4);
        String month = strDate.substring(4, 6);
        String newbend = bend.equals("1") ? "false" : "true";

        // 封装明细list
        List<KeMuMingXiVo> mapList = new ArrayList<>();
        return subjectInitalBalanceService.findAllSubjectInitialBalance(strDate.substring(0, 4), newbend,strDate.substring(0, 4)+"00","","","全部","类型")
                // 期初\凭证 list
                .flatMap(qclist -> {
                    Map qcmap = (Map) qclist.getResult();
                    Map map = new HashMap();
                    map.put("qclist", qcmap.get("tablesData"));
                    map.put("pzlist", "");       // 条件后的list
                    map.put("codelist", "");       // 科目list
                    map.put("pzlistAll", "");    // 全部凭证
                    map.put("iyperiodlist", ""); // 科目中所有的凭证区间

                    // 凭证list；前台已自动获取末级科目，方法中不在写入末级判断
                    return accvoucherRepository.findByiyperiodpz2(strDate.substring(0, 4))
                        .collectList()
                        .flatMap(pzlist -> {
                            pzlist.stream().forEach(pz -> {
                                pz.setNdS(pz.getNdS() == null ? new BigDecimal("0") : pz.getNdS()).setNcS(pz.getNcS() == null ? new BigDecimal("0") : pz.getNcS())
                                        .setNfratMd(pz.getNfratMd() == null ? new BigDecimal("0") : pz.getNfratMd())
                                        .setNfratMc(pz.getNfratMc() == null ? new BigDecimal("0") : pz.getNfratMc())
                                        .setCunitPrice(org.apache.commons.lang3.StringUtils.isBlank(pz.getCunitPrice()) ? "0" : pz.getCunitPrice())
                                        .setMdF(org.apache.commons.lang3.StringUtils.isBlank(pz.getMdF()) ? "0" : pz.getMdF())
                                        .setForeignCurrency(StringUtils.isBlank(pz.getForeignCurrency()) ? "" : pz.getForeignCurrency());
                            });

                            List<AccvoucherVo> newlist=pzlist;
                            if(!bz.equals("全部")){
                                newlist=pzlist.stream().filter(v->v.getCsign().equals(bz)).collect(Collectors.toList());
                            }
                            // ***** 日期or期间【查询条件】 *****
                            if ("qj".equals(timflg)) {
                                newlist = newlist.stream().filter(vo -> Integer.valueOf(vo.getImonth()) >= Integer.valueOf(newstrDate.substring(4, 6)) && Integer.valueOf(vo.getImonth()) <= Integer.valueOf(newendDate.substring(4, 6))).collect(Collectors.toList());
                            } else { // 日期
                                newlist = newlist.stream().filter(vo -> vo.getDbillDate() != null && Integer.valueOf(vo.getDbillDate().replaceAll("-", "")) >= Integer.valueOf(strDate) && Integer.valueOf(vo.getDbillDate().replaceAll("-", "")) <= Integer.valueOf(endDate)).collect(Collectors.toList());
                            }

                            // ****** 是否包含未记账【查询条件】 *****不包含；只获取 已记账的凭证
                            if (ishaveRjz.equals("0")) {
                                newlist = pzlist.stream().filter(vo -> StringUtils.isNotBlank(vo.getIbook()) && vo.getIbook().equals("1")).collect(Collectors.toList());
                            }
                            map.put("pzlist", newlist);
                            map.put("pzlistAll", pzlist);
                            return accvoucherRepository.findByCcodeGroupByIyperiod2(year, newstrDate, newendDate).collectList()
                                .flatMap(iyp -> {
                                    map.put("iyperiodlist", iyp);

                                    //按年度所有科目list
                                    return codeKemuRepository.findAllByIyear(year).collectList()
                                        .flatMap(cl -> {
                                            map.put("codelist", cl);
                                            return Mono.just(map);
                                        });
                                });
                        });
                })
                .flatMap(map -> {
                    List<SubjectInitialBalanceVo> qclist = (List<SubjectInitialBalanceVo>) map.get("qclist");
                    List<AccvoucherVo> pzlist = (List<AccvoucherVo>) map.get("pzlist");
                    List<AccvoucherVo> pzlistAll = (List<AccvoucherVo>) map.get("pzlistAll");
                    List<IyperiodVo> iyperiodlist = (List<IyperiodVo>) map.get("iyperiodlist");
                    List<CodeKemu> codelist = (List<CodeKemu>) map.get("codelist");

                    kmlist.stream().forEach(kmitem->{
                        // 获取科目名称
                        List<CodeKemu> findByCodeName = codelist.stream().filter(c -> c.getCcode().equals(kmitem)).collect(Collectors.toList());
                        String ccodename=findByCodeName.size()>0?findByCodeName.get(0).getCcodeName():"";
                        String ccodeBend=findByCodeName.size()>0?findByCodeName.get(0).getBend():"1";
                        String bprogerty=findByCodeName.size()>0?findByCodeName.get(0).getBprogerty():"1";

                        List<AccvoucherVo> newpzlist = pzlist.stream().filter(pz -> pz.getCcode().startsWith(kmitem)).collect(Collectors.toList());
                        List<AccvoucherVo> newpzlistAll = pzlistAll.stream().filter(pzall -> pzall.getCcode().startsWith(kmitem)).collect(Collectors.toList());
                        List<IyperiodVo> newiyperiodlist = iyperiodlist.stream().filter(per -> per.getCcode().startsWith(kmitem)).collect(Collectors.toList());

                        // 获取maxKm期初
                        List<SubjectInitialBalanceVo> qc = qclist.stream().filter(acv -> acv.getCcode().equals(kmitem)).collect(Collectors.toList());
                        BigDecimal md = qc.size() > 0 ? qc.get(0).getMd() : new BigDecimal("0");
                        BigDecimal mc = qc.size() > 0 ? qc.get(0).getMc() : new BigDecimal("0");
                        BigDecimal nds = qc.size() > 0 ? qc.get(0).getNdS() : new BigDecimal("0");
                        BigDecimal ncs = qc.size() > 0 ? qc.get(0).getNcS() : new BigDecimal("0");

                        // 外币金额
                        BigDecimal nfrat_md = qc.size() > 0 ? qc.get(0).getNfratMd() : new BigDecimal("0");
                        BigDecimal nfrat_mc = qc.size() > 0 ? qc.get(0).getNfratMc() : new BigDecimal("0");

                        // 累计期初
                        BigDecimal ljmd = qc.size() > 0 ? qc.get(0).getLjMd() : new BigDecimal("0");
                        BigDecimal ljmc = qc.size() > 0 ? qc.get(0).getLjMc() : new BigDecimal("0");

                        // 凭证借贷方金额
                        BigDecimal pz_md = new BigDecimal(0);
                        BigDecimal pz_mc = new BigDecimal(0);
                        BigDecimal pz_nds = new BigDecimal(0);
                        BigDecimal pz_ncs = new BigDecimal(0);
                        BigDecimal pz_nfrat_md = new BigDecimal(0);
                        BigDecimal pz_nfrat_mc = new BigDecimal(0);
                        BigDecimal yearmd = new BigDecimal(0).add(ljmd);
                        BigDecimal yearmc = new BigDecimal(0).add(ljmc);
                        // ************ 数量核算 年度 **********
                        BigDecimal nds_year = new BigDecimal(0).add(nds);
                        BigDecimal ncs_year = new BigDecimal(0).add(ncs);
                        // ************ 外币核算 年度 **********
                        BigDecimal nfrat_md_year = new BigDecimal(0).add(nfrat_md);
                        BigDecimal nfrat_mc_year = new BigDecimal(0).add(nfrat_mc);

                        // 期间或日期 不是从一月开始的 计算出上月的期末余额
                        if (Integer.valueOf(strDate.substring(4, 6)) > 1) {
                            String lastmonth = Integer.valueOf(month) - 1 < 10 ? "0" + (Integer.valueOf(month) - 1) : month;
                            // 获取上个月凭证发生数
                            List<AccvoucherVo> lastMonthAccVoucher = newpzlistAll.stream().filter(acv -> Integer.valueOf(acv.getImonth()) <= Integer.valueOf(lastmonth) ).collect(Collectors.toList());
                            pz_md = lastMonthAccVoucher.size() == 0 ? new BigDecimal("0") : lastMonthAccVoucher.stream().map(AccvoucherVo::getMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                            pz_mc = lastMonthAccVoucher.size() == 0 ? new BigDecimal("0") : lastMonthAccVoucher.stream().map(AccvoucherVo::getMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                            pz_nds = lastMonthAccVoucher.size() == 0 ? new BigDecimal("0") : lastMonthAccVoucher.stream().map(AccvoucherVo::getNdS).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                            pz_ncs = lastMonthAccVoucher.size() == 0 ? new BigDecimal("0") : lastMonthAccVoucher.stream().map(AccvoucherVo::getNcS).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);

                            pz_nfrat_md = lastMonthAccVoucher.size() == 0 ? new BigDecimal("0") : lastMonthAccVoucher.stream().map(AccvoucherVo::getNfratMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                            pz_nfrat_mc = lastMonthAccVoucher.size() == 0 ? new BigDecimal("0") : lastMonthAccVoucher.stream().map(AccvoucherVo::getNfratMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);

                            yearmd = yearmd.add(pz_md);
                            yearmc = yearmc.add(pz_mc);
                            // ************ 数量核算 年度 **********
                            nds_year = nds_year.add(pz_nds);
                            ncs_year = ncs_year.add(pz_ncs);
                            // ************ 外币核算 年度 **********
                            nfrat_md_year = nfrat_md_year.add(pz_nfrat_md);
                            nfrat_mc_year = nfrat_mc_year.add(pz_nfrat_mc);
                        }
                        BigDecimal jie = md.add(pz_md);
                        BigDecimal dai = mc.add(pz_mc);
                        BigDecimal yue = jie.subtract(dai);  // 借+借-贷+贷

                        // ******** 数量核算 *******
                        BigDecimal nd_jie = nds.add(pz_nds);
                        BigDecimal nc_dai = ncs.add(pz_ncs);
                        BigDecimal yue_num = nd_jie.subtract(nc_dai);  // 借+借-贷+贷
                        // ******** 外币核算 *******
                        BigDecimal nfrat_jie = nfrat_md.add(pz_nfrat_md);
                        BigDecimal nfrat_dai = nfrat_mc.add(pz_nfrat_mc);
                        BigDecimal yue_nfrat = nfrat_jie.subtract(nfrat_dai);  // 借+借-贷+贷

                        int yuetmp = yue.compareTo(new BigDecimal(0));

                        mapList.add(new KeMuMingXiVo()
                                .setCcode(kmitem)
                                .setCcodeName(ccodename)
                                .setInoIdASC(0)
                                .setCdigest("期初余额")
                                .setMd(new BigDecimal(0))
                                .setMc(new BigDecimal(0))
                                .setBprogerty(yuetmp == 0 ? "平" : yuetmp > 0 ? "借" : "贷")
                                .setYue(yue.compareTo(BigDecimal.ZERO) < 0 ? yue.multiply(new BigDecimal(-1)) : yue)
                                .setNcnum(yue_num)
                                .setYue_num(yue_num)
                                .setYue_nfrat(yue_nfrat)
                                .setNumber(yuetmp == 0 ? 0 : 1)
                        );

                        // 不是末级
                        if(ccodeBend.equals("0")){
                            List<AccvoucherVo> collect1 = newpzlist.stream().filter(v -> v.getCcode().startsWith(kmitem)).collect(Collectors.toList());
                            BigDecimal superMd = collect1.stream().map(AccvoucherVo::getMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                            BigDecimal superMc = collect1.stream().map(AccvoucherVo::getMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);

                            BigDecimal superYue=new BigDecimal(0);
                            if(bprogerty.equals("1")){
                                superYue = yue.add(superMd).subtract(superMc);
                            }else{
                                superYue = (yue.multiply(new BigDecimal(-1)).add(superMc)).multiply(new BigDecimal(-1)).add(superMd);
                            }

                            mapList.add(new KeMuMingXiVo()
                                    .setCcode(kmitem)
                                    .setCcodeName(ccodename)
                                    .setInoIdASC(0)
                                    .setCdigest(strDate.substring(0, 4)+"."+strDate.substring(4, 6)+"-"+endDate.substring(0, 4)+"."+endDate.substring(4, 6))
                                    .setBprogerty(superYue.compareTo(BigDecimal.ZERO) == 0 ? "平" : superYue.compareTo(BigDecimal.ZERO) > 0 ? "借" : "贷")
                                    .setMd(superMd)
                                    .setMc(superMc)
                                    .setYue(superYue.compareTo(BigDecimal.ZERO) < 0 ? superYue.multiply(new BigDecimal(-1)) : superYue)
                                    .setNcnum(new BigDecimal(0))
                                    .setYue_num(new BigDecimal(0))
                                    .setYue_nfrat(new BigDecimal(0))
                                    .setNumber(superYue.compareTo(BigDecimal.ZERO) == 0 ? 0 : 1)
                            );
                        }
                        else{
                            // 科目在凭证中所有区间
                            for (int i = 0; i < newiyperiodlist.size(); i++) {
                                int finalI = i;
                                List<AccvoucherVo> collect = newpzlist.stream().filter(acv -> acv.getIyperiod().equals(newiyperiodlist.get(finalI).getIyperiod())).collect(Collectors.toList());

                                BigDecimal yue2 = yue; // 余额
                                BigDecimal yue_num2 = yue_num;  // 余额-数量
                                BigDecimal yue_nfrat2 = yue_nfrat;  // 余额-数量

                                BigDecimal monthmd = new BigDecimal(0);
                                BigDecimal monthmc = new BigDecimal(0);
                                // ******** 数量核算 *******
                                BigDecimal nd_month = new BigDecimal(0);
                                BigDecimal nc_month = new BigDecimal(0);
                                // ******** 外币核算 *******
                                BigDecimal nfrat_md_month = new BigDecimal(0);
                                BigDecimal nfrat_mc_month = new BigDecimal(0);

                                int a = collect.size();
                                for (int j = 0; j < collect.size(); j++) {
                                        int finalJ = j;
                                        // 科目的计量单位
                                        List<CodeKemu> unitMeasurementlist = codelist.stream().filter(c -> c.getCcode().equals(collect.get(finalJ).getCcode())).collect(Collectors.toList());
                                        String unitMeasurement=unitMeasurementlist.size()>0?unitMeasurementlist.get(0).getMenterage():"";

                                        // 科目的币种名称
                                        List<CodeKemu> foreignCurrencylist = codelist.stream().filter(c -> c.getCcode().equals(collect.get(finalJ).getCcode())).collect(Collectors.toList());
                                        String foreignCurrency =foreignCurrencylist.size()>0?foreignCurrencylist.get(0).getCurrencyType():"";

                                        --a;
                                        BigDecimal md2 = collect.get(j).getMd()== null ? new BigDecimal("0") :collect.get(j).getMd();
                                        BigDecimal mc2 = collect.get(j).getMc()== null ? new BigDecimal("0") :collect.get(j).getMc();
                                        // 数量核算
                                        BigDecimal nds2 = collect.get(j).getNdS() == null ? new BigDecimal("0") : collect.get(j).getNdS();
                                        BigDecimal ncs2 = collect.get(j).getNcS() == null ? new BigDecimal("0") : collect.get(j).getNcS();
                                        // 外币核算
                                        BigDecimal nfrat_md2 = collect.get(j).getNdS() == null ? new BigDecimal("0") : collect.get(j).getNfratMd();
                                        BigDecimal nfrat_mc2 =collect.get(j).getNdS() == null ? new BigDecimal("0") : collect.get(j).getNfratMc();

                                        monthmd = monthmd.add(md2);
                                        monthmc = monthmc.add(mc2);
                                        // ******** 数量核算 *******
                                        nd_month = nd_month.add(nds2);
                                        nc_month = nc_month.add(ncs2);
                                        // ******** 外币核算 *******
                                        nfrat_md_month = nfrat_md_month.add(nfrat_md2);
                                        nfrat_mc_month = nfrat_mc_month.add(nfrat_mc2);

                                        if (yuetmp >= 0) {
                                            yue2 = yue2.add(md2).subtract(mc2);
                                            yue_num2 = yue_num2.add(nds2).subtract(ncs2);
                                            yue_nfrat2 = yue_nfrat2.add(nfrat_md2).subtract(nfrat_mc2);
                                        } else {
                                            yue2 = (yue2.multiply(new BigDecimal(-1)).add(mc2)).multiply(new BigDecimal(-1)).add(md2);
                                            yue_num2 = (yue_num2.multiply(new BigDecimal(-1)).add(ncs2)).multiply(new BigDecimal(-1)).add(nds2);
                                            yue_nfrat2 = (yue_nfrat2.multiply(new BigDecimal(-1)).add(nfrat_mc2)).multiply(new BigDecimal(-1)).add(nfrat_md2);
                                        }

                                        // 计算后在判断是否等于0
                                        int yuefx2 = yue2.compareTo(new BigDecimal(0));
                                        KeMuMingXiVo mx=new KeMuMingXiVo();
                                        mx
                                                .setNumber(1)
                                                .setCcode(kmitem)
                                                .setCcodeName(ccodename)
                                                .setDbillDate(collect.get(j).getDbillDate())
                                                .setInoIdASC(collect.get(j).getInoId()!=null?Integer.valueOf(collect.get(j).getInoId()):0)
                                                .setInoId(collect.get(j).getCsign()+"-"+addLeftZero.addZeroForNum(String.valueOf(collect.get(j).getInoId()),4))
                                                .setCdigest(collect.get(j).getCdigest())
                                                .setMd(md2)
                                                .setMc(mc2)
                                                .setBprogerty(yuefx2 == 0 ? "平" : yuefx2 > 0 ? "借" : "贷")
                                                .setNdS(collect.get(j).getNdS())
                                                .setNcS(collect.get(j).getNcS())
                                                .setCunitPrice(new BigDecimal(collect.get(j).getCunitPrice()))
                                                .setUnitMeasurement(unitMeasurement)
                                                .setMdF(new BigDecimal(collect.get(j).getMdF()))
                                                .setNfrat_md(collect.get(j).getNfratMd())
                                                .setNfrat_mc(collect.get(j).getNfratMc())
                                                .setForeignCurrency(foreignCurrency)
                                                .setYue(yue2.compareTo(BigDecimal.ZERO) < 0 ? yue2.multiply(new BigDecimal(-1)) : yue2)
                                                .setYue_num(yue_num2)
                                                .setYue_nfrat(yue_nfrat2.compareTo(BigDecimal.ZERO) > 0 ? yue_nfrat2 : yue_nfrat2.multiply(new BigDecimal(-1)));
                                        mapList.add(mx);

                                        if (a == 0) {
                                            mapList.add(new KeMuMingXiVo()
                                                    .setInoIdASC(99998)
                                                    .setDbillDate(collect.get(j).getIyperiod())
                                                    .setCdigest("本月合计")
                                                    .setMd(monthmd)
                                                    .setMc(monthmc)
                                                    .setYue( yue2.compareTo(BigDecimal.ZERO) < 0 ? yue2.multiply(new BigDecimal(-1)): yue2)
                                                    .setBprogerty(yuefx2 == 0 ? "平" : yuefx2 > 0 ? "借" : "贷")
                                                    .setNdS(nd_month)
                                                    .setNcS(nc_month)
                                                    .setYue_num(yue_num2)
                                                    .setNfrat_md(nfrat_md_month)
                                                    .setNfrat_mc(nfrat_mc_month)
                                                    .setYue_nfrat( yue_nfrat2.compareTo(BigDecimal.ZERO) > 0 ?yue_nfrat2 : yue_nfrat2.multiply(new BigDecimal(-1)))
                                            );

                                            yearmd = yearmd.add(monthmd);
                                            yearmc = yearmc.add(monthmc);
                                            // 数量核算
                                            nds_year = nds_year.add(nd_month);
                                            ncs_year = ncs_year.add(nc_month);
                                            // 外币核算
                                            nfrat_md_year = nfrat_md_year.add(nfrat_md_month);
                                            nfrat_mc_year = nfrat_mc_year.add(nfrat_mc_month);

                                            mapList.add(new KeMuMingXiVo()
                                                    .setInoIdASC(99998)
                                                    .setDbillDate(collect.get(j).getIyperiod())
                                                    .setCdigest("本年累计")
                                                    .setMd(yearmd)
                                                    .setMc(yearmc)
                                                    .setBprogerty(yuefx2 == 0 ? "平" : yuefx2 > 0 ? "借" : "贷")
                                                    .setYue( yue2.compareTo(BigDecimal.ZERO) < 0 ? yue2.multiply(new BigDecimal(-1)) : yue2)
                                                    .setNdS(nds_year)
                                                    .setNcS(ncs_year)
                                                    .setYue_num(yue_num2)
                                                    .setNfrat_md(nfrat_md_year)
                                                    .setNfrat_mc(nfrat_mc_year)
                                                    .setYue_nfrat( yue_nfrat2.compareTo(BigDecimal.ZERO) > 0 ? yue_nfrat2 : yue_nfrat2.multiply(new BigDecimal(-1)))
                                            );
                                        }
                                    }

                                yue = yue2;
                                yue_num = yue_num2;
                                yue_nfrat = yue_nfrat2;
                            }
                            // 期初=0 并且 没有做凭证 不显示
                            if(newiyperiodlist.size()==0){
                                List<KeMuMingXiVo> temp = mapList.stream().filter(a1 -> a1.getCdigest().equals("期初余额")).collect(Collectors.toList());
                                temp.forEach(t->{
                                    if(t.getCcode().equals(kmitem)){
                                        mapList.remove(t);
                                    }
                                });
                            }
                        }
                    });

                    // 删除 期初余额=0 并且 没有发生凭证的
                    List<KeMuMingXiVo> collect = mapList.stream().filter(v -> v.getNumber() != null && v.getNumber() == 0).collect(Collectors.toList());
                    collect.forEach(v->{
                        mapList.remove(v);
                    });
                    // 按条件过滤
                    return Mono.just(mapList);
                })
                .map(o -> R.ok().setList(o));
    }
}

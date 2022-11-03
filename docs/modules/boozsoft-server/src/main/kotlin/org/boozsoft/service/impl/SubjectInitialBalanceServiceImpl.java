package org.boozsoft.service.impl;

import cn.hutool.core.util.StrUtil;
import org.boozsoft.domain.entity.Accvoucher;
import org.boozsoft.domain.entity.account.FuzhuHesuan;
import org.boozsoft.domain.entity.codekemu.CodeKemu;
import org.boozsoft.domain.vo.SubjectInitialBalanceVo;
import org.boozsoft.repo.*;
import org.boozsoft.repo.codekemu.CodeKemuRepository;
import org.boozsoft.repo.project.base.ProjectRepositoryBase;
import org.boozsoft.service.SubjectInitalBalanceService;
import org.boozsoft.util.BigDecimalUtils;
import org.boozsoft.utils.CollectOfUtils;
import org.springbooz.core.tool.result.R;
import org.springbooz.core.tool.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 科目模板
 */
@Service
public class SubjectInitialBalanceServiceImpl implements SubjectInitalBalanceService {
    @Autowired
    AccvoucherRepository accvoucherRepository;//凭证

    @Autowired
    FuzhuHesuanRepository fuzhuHesuanRepository;//辅助核算

    @Autowired
    SysPsnRepository syspsnRepository;//个人

    @Autowired
    SysDepartmentRepository sysdepartmentRepository;//部门

    @Autowired
    CustomerRepository customerRepository;//客户

    @Autowired
    SupplierRepository supplierRepository;//供应商

    @Autowired
    ProjectClassRepository projectclassRepository;//项目大类

    @Autowired
    ProjectRepositoryBase projectRepositoryBase;//项目目录

    @Autowired
    DefinedRecordRepository definedrecordRepository;//自定义档案

    @Autowired
    CodeKemuRepository codekemuRepository;//科目

    public Map<String, Object> test(List<SubjectInitialBalanceVo> list,String ccode,String cclass,String bend) {
        if(StrUtil.isNotBlank(cclass)&&!cclass.equals("全部")){
            list=list.stream().filter(f->f.getCclass().equals(cclass)).collect(Collectors.toList());
        }

        if(StrUtil.isNotBlank(bend)&&!bend.equals("类型")){
            // 末级
            if(bend.equals("1")){
                list=list.stream().filter(f->f.getCcode().equals(ccode)).collect(Collectors.toList());
            }else{
                list=list.stream().filter(f->f.getCcode().startsWith(ccode)).collect(Collectors.toList());
            }
        }

        // 1 汇总末级科目得上级科目
        List<SubjectInitialBalanceVo> superCodeList = new ArrayList<>();
        list.stream().forEach(item -> {
            if (!item.getSuperiorCcode().equals("0")) {
                SubjectInitialBalanceVo vo = new SubjectInitialBalanceVo();
                superCodeList.add(vo.setSuperiorCcode(item.getSuperiorCcode()));
            }
            String fuzhu = "";
            if (StringUtils.isNotBlank(item.getBdept()) && item.getBdept().equals("1")) {
                fuzhu += "部门,";
            }
            if (StringUtils.isNotBlank(item.getBperson()) && item.getBperson().equals("1")) {
                fuzhu += "个人,";
            }
            if (StringUtils.isNotBlank(item.getBcus()) && item.getBcus().equals("1")) {
                fuzhu += "客户,";
            }
            if (StringUtils.isNotBlank(item.getBsup()) && item.getBsup().equals("1")) {
                fuzhu += "供应商,";
            }
            if (StringUtils.isNotBlank(item.getBitem()) && item.getBitem().equals("1")) {
                fuzhu += "项目,";
            }

            if (StringUtils.isNotBlank(item.getCdfine1()) && item.getCdfine1().equals("1")) {
                fuzhu += "自定义1,";
            }
            if (StringUtils.isNotBlank(item.getCdfine2()) && item.getCdfine2().equals("1")) {
                fuzhu += "自定义2,";
            }
            if (StringUtils.isNotBlank(item.getCdfine3()) && item.getCdfine3().equals("1")) {
                fuzhu += "自定义3,";
            }
            if (StringUtils.isNotBlank(item.getCdfine4()) && item.getCdfine4().equals("1")) {
                fuzhu += "自定义4,";
            }
            if (StringUtils.isNotBlank(item.getCdfine5()) && item.getCdfine5().equals("1")) {
                fuzhu += "自定义5,";
            }
            if (StringUtils.isNotBlank(item.getCdfine6()) && item.getCdfine6().equals("1")) {
                fuzhu += "自定义6,";
            }
            if (StringUtils.isNotBlank(item.getCdfine7()) && item.getCdfine7().equals("1")) {
                fuzhu += "自定义7,";
            }
            if (StringUtils.isNotBlank(item.getCdfine8()) && item.getCdfine8().equals("1")) {
                fuzhu += "自定义8,";
            }
            if (StringUtils.isNotBlank(item.getCdfine9()) && item.getCdfine9().equals("1")) {
                fuzhu += "自定义9,";
            }
            if (StringUtils.isNotBlank(item.getCdfine10()) && item.getCdfine10().equals("1")) {
                fuzhu += "自定义10,";
            }
            if (StringUtils.isNotBlank(item.getCdfine11()) && item.getCdfine11().equals("1")) {
                fuzhu += "自定义11,";
            }
            if (StringUtils.isNotBlank(item.getCdfine12()) && item.getCdfine12().equals("1")) {
                fuzhu += "自定义12,";
            }
            if (StringUtils.isNotBlank(item.getCdfine13()) && item.getCdfine13().equals("1")) {
                fuzhu += "自定义13,";
            }
            if (StringUtils.isNotBlank(item.getCdfine14()) && item.getCdfine14().equals("1")) {
                fuzhu += "自定义14,";
            }
            if (StringUtils.isNotBlank(item.getCdfine15()) && item.getCdfine15().equals("1")) {
                fuzhu += "自定义15,";
            }
            if (StringUtils.isNotBlank(item.getCdfine16()) && item.getCdfine16().equals("1")) {
                fuzhu += "自定义16,";
            }
            if (StringUtils.isNotBlank(item.getCdfine17()) && item.getCdfine17().equals("1")) {
                fuzhu += "自定义17,";
            }
            if (StringUtils.isNotBlank(item.getCdfine18()) && item.getCdfine18().equals("1")) {
                fuzhu += "自定义18,";
            }
            if (StringUtils.isNotBlank(item.getCdfine19()) && item.getCdfine19().equals("1")) {
                fuzhu += "自定义19,";
            }
            if (StringUtils.isNotBlank(item.getCdfine20()) && item.getCdfine20().equals("1")) {
                fuzhu += "自定义20,";
            }
            if (StringUtils.isNotBlank(item.getCdfine21()) && item.getCdfine21().equals("1")) {
                fuzhu += "自定义21,";
            }
            if (StringUtils.isNotBlank(item.getCdfine22()) && item.getCdfine22().equals("1")) {
                fuzhu += "自定义22,";
            }
            if (StringUtils.isNotBlank(item.getCdfine23()) && item.getCdfine23().equals("1")) {
                fuzhu += "自定义23,";
            }
            if (StringUtils.isNotBlank(item.getCdfine24()) && item.getCdfine24().equals("1")) {
                fuzhu += "自定义24,";
            }
            if (StringUtils.isNotBlank(item.getCdfine25()) && item.getCdfine25().equals("1")) {
                fuzhu += "自定义25,";
            }
            if (StringUtils.isNotBlank(item.getCdfine26()) && item.getCdfine26().equals("1")) {
                fuzhu += "自定义26,";
            }
            if (StringUtils.isNotBlank(item.getCdfine27()) && item.getCdfine27().equals("1")) {
                fuzhu += "自定义27,";
            }
            if (StringUtils.isNotBlank(item.getCdfine28()) && item.getCdfine28().equals("1")) {
                fuzhu += "自定义28,";
            }
            if (StringUtils.isNotBlank(item.getCdfine29()) && item.getCdfine29().equals("1")) {
                fuzhu += "自定义29,";
            }
            if (StringUtils.isNotBlank(item.getCdfine30()) && item.getCdfine30().equals("1")) {
                fuzhu += "自定义30,";
            }

            item.setFuzhu(StringUtils.isNotBlank(fuzhu) ? fuzhu.substring(0, fuzhu.length() - 1) : "");
        });

        // 2 上级科目去重
        List<SubjectInitialBalanceVo> collect = superCodeList.stream().distinct().collect(Collectors.toList());
        collect.sort(Comparator.comparing(SubjectInitialBalanceVo::getSuperiorCcode).reversed());

        // 数据
        Map map = test2(collect, list);

        // 设置表头
        List<Map> columns = new ArrayList<>();
        String flag = "";
        if (Boolean.valueOf(map.get("menterageflg").toString()) && Boolean.valueOf(map.get("currencyTypeflg").toString())) {
            flag = "all";
        } else {
            if (Boolean.valueOf(map.get("menterageflg").toString())) {
                flag = "menterage";
            }
            if (Boolean.valueOf(map.get("currencyTypeflg").toString())) {
                flag = "currencyType";
            }
        }
        return CollectOfUtils.mapof("tableColumns", flag
                , "tablesData", map.get("data"));
    }

    public Map test2(List<SubjectInitialBalanceVo> collectlist, List<SubjectInitialBalanceVo> list) {
        // 判断是否显示 单位/外币  列；
        boolean menterageflg = collectlist.stream().filter(a->StrUtil.isNotBlank(a.getMenterage())).collect(Collectors.toList()).size()>0?true:false;
        boolean currencyTypeflg = collectlist.stream().filter(a->StrUtil.isNotBlank(a.getCurrency())).collect(Collectors.toList()).size()>0?true:false;
        // 通过循环存在 所有上级科目
        for (int i = 0; i < collectlist.size(); i++) {
            int finalI = i;
            List<SubjectInitialBalanceVo> list2 = list.stream().filter(vo -> vo.getSuperiorCcode().equals(collectlist.get(finalI).getSuperiorCcode())).collect(Collectors.toList());
            // 中间科目
            BigDecimal mc = list2.stream().map(SubjectInitialBalanceVo::getMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
            BigDecimal md = list2.stream().map(SubjectInitialBalanceVo::getMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
            BigDecimal slmc = list2.stream().map(SubjectInitialBalanceVo::getNcS).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
            BigDecimal slmd = list2.stream().map(SubjectInitialBalanceVo::getNdS).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
            BigDecimal wbmc = list2.stream().map(SubjectInitialBalanceVo::getNfratMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
            BigDecimal wbmd = list2.stream().map(SubjectInitialBalanceVo::getNfratMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
            BigDecimal lmc = list2.stream().map(SubjectInitialBalanceVo::getLjMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
            BigDecimal lmd = list2.stream().map(SubjectInitialBalanceVo::getLjMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
            BigDecimal lslmc = list2.stream().map(SubjectInitialBalanceVo::getLjSlMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
            BigDecimal lslmd = list2.stream().map(SubjectInitialBalanceVo::getLjSlMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
            BigDecimal lwbmc = list2.stream().map(SubjectInitialBalanceVo::getLjWbMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
            BigDecimal lwbmd = list2.stream().map(SubjectInitialBalanceVo::getLjWbMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
            for (int j = 0; j < list.size(); j++) {
                // 当前是否 存在多级科目
                boolean wReplace = false;
                if (list.get(j).getCcode().contains(collectlist.get(i).getSuperiorCcode()))wReplace = true;
                // 判断是否为中间节点
                if (!list.get(j).getSuperiorCcode().equals("0") && collectlist.get(i).getSuperiorCcode().equals(list.get(j).getCcode())) { // 存在父节点
                    // 余额统计运算
                    if (md.compareTo(mc) > 0) {
                        list.get(j).setMc(BigDecimal.ZERO).setMd(md.subtract(mc));
                    } else if (md.compareTo(mc) < 0) {
                        list.get(j).setMc(mc.subtract(md)).setMd(BigDecimal.ZERO);
                    } else {
                        list.get(j).setMc(mc).setMd(md);
                    }
                    list.get(j).setNdS(slmd).setNcS(slmc).setNfratMc(wbmc).setNfratMd(wbmd);
                    // 累计 与 年初
                    BigDecimal v = null;
                    BigDecimal wb = null;
                    BigDecimal sl = null;
                    if (list.get(j).getBprogerty().equals("1")) { // 借方科目年初数取数方式：借方年初数=期初借+累计贷-期初贷-累计借，正数显示方向为借，金额取绝对值，金额为负，显示方向为贷，金额 取绝对值
                        v = md.add(lmc).subtract(mc).subtract(lmd);
                        wb = (wbmd.subtract(wbmc).abs()).add(lwbmc).subtract(lwbmd);
                        sl = slmd.add(lslmc).subtract(slmc).subtract(lslmd);
                    } else { // 贷方科目年数取值方式：贷方年初数=期初贷+累计借-期初借-累计贷，正数方向为贷。负数方向为借
                        v = mc.add(lmd).subtract(md).subtract(lmc);
                        wb = (wbmc.subtract(wbmd).abs()).add(lwbmd).subtract(lwbmc);
                        sl = slmc.add(lslmd).subtract(slmd).subtract(lslmc);
                    }
                    if (v.compareTo(BigDecimal.ZERO) > 0) {
                        if (list.get(j).getBprogerty().equals("1")) {
                            list.get(j).setYearMd(v);
                            list.get(j).setYearMc(BigDecimal.ZERO);
                        } else {
                            list.get(j).setYearMc(v);
                            list.get(j).setYearMd(BigDecimal.ZERO);
                        }
                    } else if (v.compareTo(BigDecimal.ZERO) < 0) {
                        if (list.get(j).getBprogerty().equals("1")) {
                            list.get(j).setYearMc(v.abs());
                            list.get(j).setYearMd(BigDecimal.ZERO);
                        } else {
                            list.get(j).setYearMd(v.abs());
                            list.get(j).setYearMc(BigDecimal.ZERO);
                        }
                        list.get(j).setBprogerty(list.get(j).getBprogerty().equals("1") ? "0" : "1");
                    }
                    list.get(j).setLjMd(lmd).setLjMc(lmc).setLjSlMc(lslmc).setLjSlMd(lslmd).setLjWbMc(lwbmc).setLjWbMd(lwbmd)
                            .setYearNfrat(wb.abs()).setYearNum(sl.abs());
                } else {
                    // 一级科目与末级科目
                    int finalJ = j;
                    List<SubjectInitialBalanceVo> list3 = list.stream().filter(vo -> vo.getSuperiorCcode().equals(list.get(finalJ).getCcode())).collect(Collectors.toList());
                    BigDecimal mc2 = list3.size() == 0 ? list.get(finalJ).getMc() : list3.stream().map(SubjectInitialBalanceVo::getMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                    BigDecimal md2 = list3.size() == 0 ? list.get(finalJ).getMd() : list3.stream().map(SubjectInitialBalanceVo::getMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                    BigDecimal nds2 = list3.size() == 0 ? list.get(finalJ).getNdS() : list3.stream().map(SubjectInitialBalanceVo::getNdS).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                    BigDecimal ncs2 = list3.size() == 0 ? list.get(finalJ).getNcS() : list3.stream().map(SubjectInitialBalanceVo::getNcS).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                    BigDecimal wbds2 = list3.size() == 0 ? list.get(finalJ).getNfratMd() : list3.stream().map(SubjectInitialBalanceVo::getNfratMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                    BigDecimal wbcs2 = list3.size() == 0 ? list.get(finalJ).getNfratMc() : list3.stream().map(SubjectInitialBalanceVo::getNfratMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                    //累计
                    BigDecimal lMd = list3.size() == 0 ? list.get(finalJ).getLjMd() : list3.stream().map(SubjectInitialBalanceVo::getLjMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                    BigDecimal lMc = list3.size() == 0 ? list.get(finalJ).getLjMc() : list3.stream().map(SubjectInitialBalanceVo::getLjMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                    BigDecimal lnds = list3.size() == 0 ? list.get(finalJ).getLjSlMd() : list3.stream().map(SubjectInitialBalanceVo::getLjSlMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                    BigDecimal lncs = list3.size() == 0 ? list.get(finalJ).getLjSlMc() : list3.stream().map(SubjectInitialBalanceVo::getLjSlMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                    BigDecimal lwbds = list3.size() == 0 ? list.get(finalJ).getLjWbMd() : list3.stream().map(SubjectInitialBalanceVo::getLjWbMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                    BigDecimal lwbcs = list3.size() == 0 ? list.get(finalJ).getLjWbMc() : list3.stream().map(SubjectInitialBalanceVo::getLjWbMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                    list.get(j).setNdS(nds2).setNcS(ncs2).setNfratMc(wbcs2).setNfratMd(wbds2);
                    if (wReplace){ // 存在下级科目
                        if (md2.doubleValue() != 0 && mc2.doubleValue() != 0){
                            if (md2.doubleValue() > mc2.doubleValue()){
                                BigDecimal yue = md2.subtract(mc2);
                                list .get(j).setYuemd(yue).setYuemc(BigDecimal.ZERO).setMd(yue).setMc(BigDecimal.ZERO);
                            }else{
                                BigDecimal yue = mc2.subtract(md2);
                                list .get(j).setYuemd(BigDecimal.ZERO).setYuemc(yue).setMd(BigDecimal.ZERO).setMc(yue);
                            }
                        }
                        if (wbds2.doubleValue() != 0 && wbcs2.doubleValue() != 0){
                            list.get(j).setNfrat(wbds2.doubleValue() > wbcs2.doubleValue() ? wbds2.subtract(wbcs2) : wbcs2.subtract(wbds2)).setNcnum(nds2.doubleValue() > ncs2.doubleValue() ? nds2.subtract(ncs2) : ncs2.subtract(nds2));
                        }else {
                            list.get(j).setNcnum(nds2.doubleValue() != 0  && ncs2.doubleValue() == 0 ? nds2:ncs2)
                                    .setNfrat((wbds2.doubleValue() != 0 && wbcs2.doubleValue() == 0)?wbds2:wbcs2);
                        }
                    }else { //无需计算
                        list.get(j).setYuemd(md2).setYuemc(mc2).setMd(md2).setMc(mc2)
                                .setNcnum(nds2.doubleValue() != 0  && ncs2.doubleValue() == 0 ? nds2:ncs2)
                                .setNfrat((wbds2.doubleValue() != 0 && wbcs2.doubleValue() == 0)?wbds2:wbcs2);
                    }
                    list.get(j).setLjMd(lMd).setLjMc(lMc).setLjSlMd(lnds).setLjSlMc(lncs).setLjWbMd(lwbds).setLjWbMc(lwbcs);
                    BigDecimal v = null;
                    BigDecimal wb = null;
                    BigDecimal sl = null;
                    if (list.get(j).getBprogerty().equals("1")) { // 借方科目年初数取数方式：借方年初数=期初借+累计贷-期初贷-累计借，正数显示方向为借，金额取绝对值，金额为负，显示方向为贷，金额 取绝对值
                        v = md2.add(lMc).subtract(mc2).subtract(lMd);
                        wb = (wbds2.subtract(wbcs2).abs()).add(lwbcs).subtract(lwbds);
                        sl = nds2.add(lncs).subtract(ncs2).subtract(lnds);
                    } else { // 贷方科目年数取值方式：贷方年初数=期初贷+累计借-期初借-累计贷，正数方向为贷。负数方向为借
                        v = mc2.add(lMd).subtract(md2).subtract(lMc);
                        wb = (wbcs2.subtract(wbds2).abs()).add(lwbds).subtract(lwbcs);
                        sl = ncs2.add(lnds).subtract(nds2).subtract(lncs);
                    }
                    if (v.compareTo(BigDecimal.ZERO) > 0) {
                        if (list.get(j).getBprogerty().equals("1")) {
                            list.get(j).setYearMd(v);
                            list.get(j).setYearMc(BigDecimal.ZERO);
                        } else {
                            list.get(j).setYearMc(v);
                            list.get(j).setYearMd(BigDecimal.ZERO);
                        }
                    } else if (v.compareTo(BigDecimal.ZERO) < 0) {
                        if (list.get(j).getBprogerty().equals("1")) {
                            list.get(j).setYearMc(v.abs());
                            list.get(j).setYearMd(BigDecimal.ZERO);
                        } else {
                            list.get(j).setYearMd(v.abs());
                            list.get(j).setYearMc(BigDecimal.ZERO);
                        }
                        list.get(j).setBprogerty(list.get(j).getBprogerty().equals("1") ? "0" : "1");
                    }
                    // 无下级科目走一遍 存在下级科目覆盖
                    if (wReplace || i == 0) list.get(j).setYearNfrat(wb.abs());
                    if (wReplace || i == 0) list.get(j).setYearNum(sl.abs());
                }
            }
        }
        list.sort(Comparator.comparing(SubjectInitialBalanceVo::getCcode));
        return CollectOfUtils.mapof("menterageflg", menterageflg, "currencyTypeflg", currencyTypeflg, "data", list);
    }

    public Map testFuzhu(List<SubjectInitialBalanceVo> list, String cclass) {
        if(StrUtil.isNotBlank(cclass)&&!cclass.equals("全部")){
            list=list.stream().filter(f->f.getCclass().equals(cclass)).collect(Collectors.toList());
        }


        // 1 汇总末级科目得上级科目
        List<SubjectInitialBalanceVo> superCodeList = new ArrayList<>();
        list.stream().forEach(item -> {
            if (!item.getSuperiorCcode().equals("0")) {
                SubjectInitialBalanceVo vo = new SubjectInitialBalanceVo();
                superCodeList.add(vo.setSuperiorCcode(item.getSuperiorCcode()));
            }
            String fuzhu = "";
            if (StringUtils.isNotBlank(item.getBdept()) && item.getBdept().equals("1")) {
                fuzhu += "部门,";
            }
            if (StringUtils.isNotBlank(item.getBperson()) && item.getBperson().equals("1")) {
                fuzhu += "个人,";
            }
            if (StringUtils.isNotBlank(item.getBcus()) && item.getBcus().equals("1")) {
                fuzhu += "客户,";
            }
            if (StringUtils.isNotBlank(item.getBsup()) && item.getBsup().equals("1")) {
                fuzhu += "供应商,";
            }
            if (StringUtils.isNotBlank(item.getBitem()) && item.getBitem().equals("1")) {
                fuzhu += "项目,";
            }

            if (StringUtils.isNotBlank(item.getCdfine1()) && item.getCdfine1().equals("1")) {
                fuzhu += "自定义1,";
            }
            if (StringUtils.isNotBlank(item.getCdfine2()) && item.getCdfine2().equals("1")) {
                fuzhu += "自定义2,";
            }
            if (StringUtils.isNotBlank(item.getCdfine3()) && item.getCdfine3().equals("1")) {
                fuzhu += "自定义3,";
            }
            if (StringUtils.isNotBlank(item.getCdfine4()) && item.getCdfine4().equals("1")) {
                fuzhu += "自定义4,";
            }
            if (StringUtils.isNotBlank(item.getCdfine5()) && item.getCdfine5().equals("1")) {
                fuzhu += "自定义5,";
            }
            if (StringUtils.isNotBlank(item.getCdfine6()) && item.getCdfine6().equals("1")) {
                fuzhu += "自定义6,";
            }
            if (StringUtils.isNotBlank(item.getCdfine7()) && item.getCdfine7().equals("1")) {
                fuzhu += "自定义7,";
            }
            if (StringUtils.isNotBlank(item.getCdfine8()) && item.getCdfine8().equals("1")) {
                fuzhu += "自定义8,";
            }
            if (StringUtils.isNotBlank(item.getCdfine9()) && item.getCdfine9().equals("1")) {
                fuzhu += "自定义9,";
            }
            if (StringUtils.isNotBlank(item.getCdfine10()) && item.getCdfine10().equals("1")) {
                fuzhu += "自定义10,";
            }
            if (StringUtils.isNotBlank(item.getCdfine11()) && item.getCdfine11().equals("1")) {
                fuzhu += "自定义11,";
            }
            if (StringUtils.isNotBlank(item.getCdfine12()) && item.getCdfine12().equals("1")) {
                fuzhu += "自定义12,";
            }
            if (StringUtils.isNotBlank(item.getCdfine13()) && item.getCdfine13().equals("1")) {
                fuzhu += "自定义13,";
            }
            if (StringUtils.isNotBlank(item.getCdfine14()) && item.getCdfine14().equals("1")) {
                fuzhu += "自定义14,";
            }
            if (StringUtils.isNotBlank(item.getCdfine15()) && item.getCdfine15().equals("1")) {
                fuzhu += "自定义15,";
            }
            if (StringUtils.isNotBlank(item.getCdfine16()) && item.getCdfine16().equals("1")) {
                fuzhu += "自定义16,";
            }
            if (StringUtils.isNotBlank(item.getCdfine17()) && item.getCdfine17().equals("1")) {
                fuzhu += "自定义17,";
            }
            if (StringUtils.isNotBlank(item.getCdfine18()) && item.getCdfine18().equals("1")) {
                fuzhu += "自定义18,";
            }
            if (StringUtils.isNotBlank(item.getCdfine19()) && item.getCdfine19().equals("1")) {
                fuzhu += "自定义19,";
            }
            if (StringUtils.isNotBlank(item.getCdfine20()) && item.getCdfine20().equals("1")) {
                fuzhu += "自定义20,";
            }
            if (StringUtils.isNotBlank(item.getCdfine21()) && item.getCdfine21().equals("1")) {
                fuzhu += "自定义21,";
            }
            if (StringUtils.isNotBlank(item.getCdfine22()) && item.getCdfine22().equals("1")) {
                fuzhu += "自定义22,";
            }
            if (StringUtils.isNotBlank(item.getCdfine23()) && item.getCdfine23().equals("1")) {
                fuzhu += "自定义23,";
            }
            if (StringUtils.isNotBlank(item.getCdfine24()) && item.getCdfine24().equals("1")) {
                fuzhu += "自定义24,";
            }
            if (StringUtils.isNotBlank(item.getCdfine25()) && item.getCdfine25().equals("1")) {
                fuzhu += "自定义25,";
            }
            if (StringUtils.isNotBlank(item.getCdfine26()) && item.getCdfine26().equals("1")) {
                fuzhu += "自定义26,";
            }
            if (StringUtils.isNotBlank(item.getCdfine27()) && item.getCdfine27().equals("1")) {
                fuzhu += "自定义27,";
            }
            if (StringUtils.isNotBlank(item.getCdfine28()) && item.getCdfine28().equals("1")) {
                fuzhu += "自定义28,";
            }
            if (StringUtils.isNotBlank(item.getCdfine29()) && item.getCdfine29().equals("1")) {
                fuzhu += "自定义29,";
            }
            if (StringUtils.isNotBlank(item.getCdfine30()) && item.getCdfine30().equals("1")) {
                fuzhu += "自定义30,";
            }

            item.setFuzhu(StringUtils.isNotBlank(fuzhu) ? fuzhu.substring(0, fuzhu.length() - 1) : "");
        });
        // 只要辅助核算
        list = list.stream().filter(it->StrUtil.isNotBlank(it.getFuzhu())).collect(Collectors.toList());
        List<String> codeList = list.stream().map(it -> it.getCcode()).distinct().collect(Collectors.toList());
        // 2 上级科目去重
        List<SubjectInitialBalanceVo> collect = superCodeList.stream().distinct().collect(Collectors.toList());
        collect.sort(Comparator.comparing(SubjectInitialBalanceVo::getSuperiorCcode).reversed());

        // 数据
        Map map = test2(collect, list);

        // 设置表头
        List<Map> columns = new ArrayList<>();
        String flag = "";
        if (Boolean.valueOf(map.get("menterageflg").toString()) && Boolean.valueOf(map.get("currencyTypeflg").toString())) {
            flag = "all";
        } else {
            if (Boolean.valueOf(map.get("menterageflg").toString())) {
                flag = "menterage";
            }
            if (Boolean.valueOf(map.get("currencyTypeflg").toString())) {
                flag = "currencyType";
            }
        }
        return   CollectOfUtils.mapof("tableColumns", "", "tablesData", map.get("data"),"accList",codeList);
    }

    /**
     * @param iyear    年度
     * @param lastCode 是否查看末级科目
     * @return
     */
    @Override
    public Mono<R> findAllSubjectInitialBalance(String iyear, String lastCode, String iyperiod, String databasenum,String ccode,String cclass,String bend) {
        Mono<List<SubjectInitialBalanceVo>> nolast = accvoucherRepository.findAllSubjectInitialBalance(iyear, iyperiod).collectList();
        Mono<List<SubjectInitialBalanceVo>> lastcode = accvoucherRepository.findAllSubjectInitialBalanceLastCode(iyear, iyperiod, "1").collectList();
        return lastCode.equals("false") ? nolast.map(list -> test(list,ccode,cclass,bend)).map(o -> R.ok().setResult(o)) : lastcode.map(list -> test(list,ccode,cclass,bend)).map(o -> R.ok().setResult(o));
    }

    /**
     * @param iyear    年度
     * @param lastCode 是否查看末级科目
     * @return
     */
    @Override
    public Mono<R> findAllSubjectInitialBalanceNewFuZHu(String iyear, String lastCode, String iyperiod, String databasenum,String cclass) {
        Mono<List<SubjectInitialBalanceVo>> lastcode = accvoucherRepository.findAllSubjectInitialBalanceLastCode(iyear, iyperiod, "1").collectList();
        return lastcode.map(list -> testFuzhu(list,cclass)).flatMap(map -> {
            List<String> codes = (List<String>)map.get("accList");
            return accvoucherRepository.findAllByIyperiodAndCcodeInOrderByIdAscCcodeAsc(iyear + "21", codes).collectList().flatMap(
                    accvouchers -> {
                        map.put("accList",accvouchers);
                        return Mono.just(R.ok().setResult(map));
                    }
            );

        });
    }

    /**
     * @param iyear        年度
     * @param iyperiod     期间
     * @param databasenum1 数据库名称
     * @param databasenum2 数据库名称
     * @return
     */
    @Override
    public Mono<R> findAllSubjectInitialBalanceFuZhu(String iyear, String iyperiod, String databasenum1, String databasenum2) {
        testViewCodeKemu();
        return accvoucherRepository.findAllSubjectInitialBalanceLastCodeFuZhu(iyear, iyperiod, databasenum1, databasenum2)
                .collectList()
                .map(list -> test(list,"","全部","类型"))
                .map(o -> R.ok().setResult(o));
    }

    @Override
    public Mono<R> findAllSubjectInitialBalanceFuZhuList(String iyear, String ccode, String databasenum) {
        Map<String, Object> map = new HashMap<>();
        Mono<List<Accvoucher>> accFlux1 = accvoucherRepository.findAllByIyperiodAndCcodeOrderById(iyear + "21", ccode).collectList().doOnNext(item -> map.put("accList", item));
        Mono<CodeKemu> codeFlux2 = codekemuRepository.findByCcodeAndIyear(ccode, iyear).doOnNext(item -> map.put("kemuCode", item));
        Mono<List<FuzhuHesuan>> codeFlux3 = fuzhuHesuanRepository.findByFlagOrderByCcode("1").collectList().doOnNext(item -> map.put("fzhsList", item));
        return accFlux1.then(codeFlux2).then(codeFlux3).map(item -> R.ok(map));
    }

    @Override
    public Mono<R> findAllSubjectInitialBalanceFuZhuListMingXi(String iyear, String ccode, String databasenum) {
        Map<String, Object> map = new HashMap<>();
        Mono<List<Accvoucher>> accFlux1 = accvoucherRepository.findAllByIyperiodAndCcodeOrderById(iyear + "21", ccode).collectList().doOnNext(item -> map.put("accList", item));
        Mono<CodeKemu> codeFlux2 = codekemuRepository.findByCcodeAndIyear(ccode, iyear).doOnNext(item -> map.put("kemuCode", item));
        Mono<List<FuzhuHesuan>> codeFlux3 = fuzhuHesuanRepository.findByFlagOrderByCcode("1").collectList().doOnNext(item -> map.put("fzhsList", item));
        return accFlux1.then(codeFlux2).then(codeFlux3).map(item -> R.ok(map));
    }

    @Override
    public void testViewCodeKemu() {
    }
}

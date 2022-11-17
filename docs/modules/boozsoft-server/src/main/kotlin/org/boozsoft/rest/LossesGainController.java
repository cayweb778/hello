package org.boozsoft.rest;


import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.boozsoft.domain.entity.Accvoucher;
import org.boozsoft.domain.entity.CustomerClass;
import org.boozsoft.domain.entity.SupplierClass;
import org.boozsoft.domain.entity.account.DefinedRecord;
import org.boozsoft.domain.entity.account.FuzhuHesuan;
import org.boozsoft.domain.entity.account.ProjectClass;
import org.boozsoft.domain.entity.account.SysPsnType;
import org.boozsoft.domain.entity.accstyle.AccStyle;
import org.boozsoft.domain.entity.carryovers.AccCarryOverEntry;
import org.boozsoft.domain.entity.codekemu.CodeKemu;
import org.boozsoft.domain.vo.*;
import org.boozsoft.domain.vo.stock.StockCurrentLackVo;
import org.boozsoft.repo.*;
import org.boozsoft.repo.accstyle.AccStyleRepository;
import org.boozsoft.repo.codekemu.CodeKemuRepository;
import org.boozsoft.service.KeMuBalanceService;
import org.boozsoft.service.SubjectInitalBalanceService;
import org.boozsoft.util.BigDecimalUtils;
import org.boozsoft.util.NewDateUtil;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/company/loss")
@Api(value = "损益结转", tags = "损益结转")
public class LossesGainController {
    @Autowired
    CodeKemuRepository codeKemuRepository;
    @Autowired
    AccvoucherRepository accvoucherRepository;
    @Autowired
    SubjectInitalBalanceService subjectInitalBalanceService;
    @Autowired
    KeMuBalanceService keMuBalanceService;
    @Autowired
    AccStyleRepository accStyleRepository;
    @Autowired
    DefinedRecordRepository definedRecordRepository;
    @Autowired
    FuzhuHesuanRepository fuzhuHesuanRepository;
    @Autowired
    CustomerClassRepository customerClassRepository;
    @Autowired
    SupplierClassRepository supplierClassRepository;
    @Autowired
    SysPsnTypeRepository sysPsnTypeRepository;
    @Autowired
    ProjectClassRepository projectClassRepository;
    @Autowired
    DatabaseClient client;


    // 如果测试，把损益科目凭证作废 就能看到数据
    @PostMapping("findAll")
    public Mono<R> findAll(@RequestBody Map map, Pageable pageable) {
        // 结转期间
        String yearMonth = map.get("yearMonth").toString();
        // 凭证类型
        String pzType = map.get("pzType").toString();
        // 结转科目级次
        String codeJici = map.get("codeJici").toString();
        // 本年利润科目
        String liRunCode = map.get("liRunCode").toString().split("-")[0];
        // 摘要
        String digest = map.get("digest").toString();
        // 一级科目长度
        String codeLevelFirst = map.get("codeLevelFirst").toString();
        Map<String, String> searchMap = ((HashMap<String,  String>) map.get("searchConditon"));

        return definedRecordRepository.findAll().collectList()
                .flatMap(zdyfzhslist -> {
                    Map<String, Object> maplist = new HashMap<>();
                    maplist.put("zdyfzhslist", zdyfzhslist);
                    // 损益科目编码首位
                    return codeKemuRepository.company_findBySunYiCodeFirst()
                            .flatMap(sunYiCodeFirst -> {
                                return getJieZhuanData(yearMonth.split("-")[0], yearMonth.replaceAll("-", ""), sunYiCodeFirst).collectList()
                                        .flatMap(listMap -> {
                                            List<QjsyjzVo> listobj = JSONArray.parseArray(JSONArray.toJSONString(listMap), QjsyjzVo.class);
                                            maplist.put("listobj", listobj);
                                            return codeKemuRepository.companyfindAll(yearMonth.split("-")[0], pageable).collectList()
                                                    .flatMap(ccodelist -> {
                                                        maplist.put("ccodelist", ccodelist);
                                                        return fuzhuHesuanRepository.findAll().collectList()
                                                                .flatMap(fzhslist -> {
                                                                    maplist.put("fzhslist", fzhslist);
                                                                    return customerClassRepository.findAll().collectList()
                                                                            .flatMap(cusClassList -> {
                                                                                maplist.put("cusClassList", cusClassList);
                                                                                return supplierClassRepository.findAll().collectList()
                                                                                        .flatMap(supClassList -> {
                                                                                            maplist.put("supClassList", supClassList);
                                                                                            return sysPsnTypeRepository.findAll().collectList()
                                                                                                    .flatMap(psnTypeList -> {
                                                                                                        maplist.put("psnTypeList", psnTypeList);
                                                                                                        return projectClassRepository.findAll().collectList()
                                                                                                                .flatMap(proClassList -> {
                                                                                                                    maplist.put("proClassList", proClassList);
                                                                                                                    return Mono.just(maplist);
                                                                                                                });
                                                                                                    });
                                                                                        });
                                                                            });
                                                                });
                                                    });
                                        });
                            });
                })
                .flatMap(maparr -> {
                    List<QjsyjzVo> listobj = (List<QjsyjzVo>) maparr.get("listobj");
                    List<QjsyjzVo> listobj2 = (List<QjsyjzVo>) maparr.get("listobj");
                    List<CodeKemu> ccodelist = (List<CodeKemu>) maparr.get("ccodelist");
                    List<FuzhuHesuan> fzhslist = (List<FuzhuHesuan>) maparr.get("fzhslist");

                    List<CustomerClass> cusClassList = (List<CustomerClass>) maparr.get("cusClassList");
                    List<SupplierClass> supClassList = (List<SupplierClass>) maparr.get("supClassList");
                    List<SysPsnType> psnTypeList = (List<SysPsnType>) maparr.get("psnTypeList");
                    List<ProjectClass> proClassList = (List<ProjectClass>) maparr.get("proClassList");

                    List<DefinedRecord> zdyfzhslist = (List<DefinedRecord>) maparr.get("zdyfzhslist");
                    List<QjsyjzVo> newlist = new ArrayList<>();

                    List<CodeKemu> findByCcodeName = ccodelist.stream().filter(item -> item.getCcode().equals(liRunCode)).collect(Collectors.toList());
                    String profitBprogerty = findByCcodeName.size() > 0 ? findByCcodeName.get(0).getBprogerty() : "";

                    // 去重上级科目
                    List<String> superCodeDistinct = listobj.stream().map(QjsyjzVo::getParentcode).filter(a -> !a.equals("-")).collect(Collectors.toList()).stream().distinct().collect(Collectors.toList());
                    // 1、查询状态是末级：封装带Children 页面显示
                    if (codeJici.equals("2")) {
                        // 存在上级科目
                        listobj.stream().filter(a -> a.getParentcode().equals("-")).forEach(obj -> {
                            // 查找本科目信息
                            List<QjsyjzVo> c1 = listobj.stream().filter(a -> a.getParentcode().equals("-") && a.getCcode().equals(obj.getCcode())).collect(Collectors.toList());

                            QjsyjzVo vo = new QjsyjzVo();
                            vo.setYuemx(c1);
                            vo.setChildrenflag("0");
                            vo.setChamd(obj.getChamd());
                            vo.setChamc(obj.getChamc());
                            vo.setCcode(obj.getCcode());
                            vo.setCcodeName(obj.getCcodeName());
                            vo.setBprogerty(obj.getBprogerty());
                            vo.setDigest(digest);
                            vo.setProfitCcode(map.get("liRunCode").toString());
                            vo.setProfitBprogerty(profitBprogerty);

                            // 是否存在下级
                            if (superCodeDistinct.size() > 0) {
                                long a = superCodeDistinct.stream().filter(s -> obj.getCcode().equals(s)).count();
                                if (a > 0) {
                                    vo.setYuemx(new ArrayList<>());
                                    List<QjsyjzVo> temp = listobj2.stream().filter(test -> superCodeDistinct.contains(test.getCcode()) && !test.getParentcode().equals("-")).collect(Collectors.toList());
                                    List<QjsyjzVo> newlist2 = new ArrayList<>();
                                    temp.forEach(vv -> {
                                        String fuzhuname = "";
                                        if (StrUtil.isNotBlank(vv.getDeptName())) {
                                            fuzhuname += vv.getDeptName() + ",";
                                        }
                                        if (StrUtil.isNotBlank(vv.getCustName())) {
                                            fuzhuname += vv.getCustName() + ",";
                                        }
                                        if (StrUtil.isNotBlank(vv.getSupName())) {
                                            fuzhuname += vv.getSupName() + ",";
                                        }
                                        if (StrUtil.isNotBlank(vv.getPsnName())) {
                                            fuzhuname += vv.getPsnName() + ",";
                                        }
                                        if (StrUtil.isNotBlank(vv.getProjectName())) {
                                            fuzhuname += vv.getProjectName() + ",";
                                        }
                                        if (StrUtil.isNotBlank(vv.getCdfine1())) {
                                            List<DefinedRecord> f1 = zdyfzhslist.stream().filter(hs -> hs.getId().equals(vv.getCdfine1())).collect(Collectors.toList());
                                            if (f1.size() > 0) {
                                                List<FuzhuHesuan> collect = fzhslist.stream().filter(fz -> fz.getCfield().equals("cdfine1")).collect(Collectors.toList());
                                                if (collect.size() > 0) {
                                                    // 判断是否 自定义项档案表名
                                                    if (collect.get(0).getCankaoDuixiangTable().equals("defined_record")) {
                                                        fuzhuname += collect.get(0).getCname() + "[" + f1.get(0).getRecordName() + "],";
                                                    }
                                                    // 是否固定的分类档案表名
                                                    else if (collect.get(0).getCankaoDuixiangTable().equals("customer_class")) {
                                                        List<CustomerClass> collect1 = cusClassList.stream().filter(cus -> cus.getUniqueCustclass().equals(vv.getCdfine1())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getCusCclassName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("supplier_class")) {
                                                        List<SupplierClass> collect1 = supClassList.stream().filter(sup -> sup.getUniqueCustclass().equals(vv.getCdfine1())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getCusCclassName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("sys_psn_type")) {
                                                        List<SysPsnType> collect1 = psnTypeList.stream().filter(psn -> psn.getUniqueCode().equals(vv.getCdfine1())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getPsnTypeName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("project_class")) {
                                                        List<ProjectClass> collect1 = proClassList.stream().filter(pro -> pro.getUniqueCode().equals(vv.getCdfine1())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getProjectClassName() + "],";
                                                        }
                                                    } else {
                                                        fuzhuname += f1.get(0).getRecordName() + ",";
                                                    }
                                                } else {
                                                    fuzhuname += f1.get(0).getRecordName() + ",";
                                                }
                                            }
                                        }
                                        if (StrUtil.isNotBlank(vv.getCdfine2())) {
                                            List<DefinedRecord> f1 = zdyfzhslist.stream().filter(hs -> hs.getId().equals(vv.getCdfine2())).collect(Collectors.toList());
                                            if (f1.size() > 0) {
                                                List<FuzhuHesuan> collect = fzhslist.stream().filter(fz -> fz.getCfield().equals("cdfine2")).collect(Collectors.toList());
                                                if (collect.size() > 0) {
                                                    // 判断是否 自定义项档案表名
                                                    if (collect.get(0).getCankaoDuixiangTable().equals("defined_record")) {
                                                        fuzhuname += collect.get(0).getCname() + "[" + f1.get(0).getRecordName() + "],";
                                                    }
                                                    // 是否固定的分类档案表名
                                                    else if (collect.get(0).getCankaoDuixiangTable().equals("customer_class")) {
                                                        List<CustomerClass> collect1 = cusClassList.stream().filter(cus -> cus.getUniqueCustclass().equals(vv.getCdfine2())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getCusCclassName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("supplier_class")) {
                                                        List<SupplierClass> collect1 = supClassList.stream().filter(sup -> sup.getUniqueCustclass().equals(vv.getCdfine2())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getCusCclassName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("sys_psn_type")) {
                                                        List<SysPsnType> collect1 = psnTypeList.stream().filter(psn -> psn.getUniqueCode().equals(vv.getCdfine2())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getPsnTypeName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("project_class")) {
                                                        List<ProjectClass> collect1 = proClassList.stream().filter(pro -> pro.getUniqueCode().equals(vv.getCdfine2())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getProjectClassName() + "],";
                                                        }
                                                    } else {
                                                        fuzhuname += f1.get(0).getRecordName() + ",";
                                                    }
                                                } else {
                                                    fuzhuname += f1.get(0).getRecordName() + ",";
                                                }
                                            }
                                        }
                                        if (StrUtil.isNotBlank(vv.getCdfine3())) {
                                            List<DefinedRecord> f1 = zdyfzhslist.stream().filter(hs -> hs.getId().equals(vv.getCdfine3())).collect(Collectors.toList());
                                            if (f1.size() > 0) {
                                                // 辅助核算定义档案
                                                List<FuzhuHesuan> collect = fzhslist.stream().filter(fz -> fz.getCfield().equals("cdfine3")).collect(Collectors.toList());
                                                if (collect.size() > 0) {
                                                    // 判断是否 自定义项档案表名
                                                    if (collect.get(0).getCankaoDuixiangTable().equals("defined_record")) {
                                                        fuzhuname += collect.get(0).getCname() + "[" + f1.get(0).getRecordName() + "],";
                                                    }
                                                    // 是否固定的分类档案表名
                                                    else if (collect.get(0).getCankaoDuixiangTable().equals("customer_class")) {
                                                        List<CustomerClass> collect1 = cusClassList.stream().filter(cus -> cus.getUniqueCustclass().equals(vv.getCdfine3())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getCusCclassName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("supplier_class")) {
                                                        List<SupplierClass> collect1 = supClassList.stream().filter(sup -> sup.getUniqueCustclass().equals(vv.getCdfine3())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getCusCclassName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("sys_psn_type")) {
                                                        List<SysPsnType> collect1 = psnTypeList.stream().filter(psn -> psn.getUniqueCode().equals(vv.getCdfine3())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getPsnTypeName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("project_class")) {
                                                        List<ProjectClass> collect1 = proClassList.stream().filter(pro -> pro.getUniqueCode().equals(vv.getCdfine3())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getProjectClassName() + "],";
                                                        }
                                                    } else {
                                                        fuzhuname += f1.get(0).getRecordName() + ",";
                                                    }
                                                } else {
                                                    fuzhuname += f1.get(0).getRecordName() + ",";
                                                }
                                            }
                                        }
                                        if (StrUtil.isNotBlank(vv.getCdfine4())) {
                                            List<DefinedRecord> f1 = zdyfzhslist.stream().filter(hs -> hs.getId().equals(vv.getCdfine4())).collect(Collectors.toList());
                                            if (f1.size() > 0) {
                                                List<FuzhuHesuan> collect = fzhslist.stream().filter(fz -> fz.getCfield().equals("cdfine4")).collect(Collectors.toList());
                                                if (collect.size() > 0) {
                                                    // 判断是否 自定义项档案表名
                                                    if (collect.get(0).getCankaoDuixiangTable().equals("defined_record")) {
                                                        fuzhuname += collect.get(0).getCname() + "[" + f1.get(0).getRecordName() + "],";
                                                    }
                                                    // 是否固定的分类档案表名
                                                    else if (collect.get(0).getCankaoDuixiangTable().equals("customer_class")) {
                                                        List<CustomerClass> collect1 = cusClassList.stream().filter(cus -> cus.getUniqueCustclass().equals(vv.getCdfine4())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getCusCclassName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("supplier_class")) {
                                                        List<SupplierClass> collect1 = supClassList.stream().filter(sup -> sup.getUniqueCustclass().equals(vv.getCdfine4())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getCusCclassName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("sys_psn_type")) {
                                                        List<SysPsnType> collect1 = psnTypeList.stream().filter(psn -> psn.getUniqueCode().equals(vv.getCdfine4())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getPsnTypeName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("project_class")) {
                                                        List<ProjectClass> collect1 = proClassList.stream().filter(pro -> pro.getUniqueCode().equals(vv.getCdfine4())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getProjectClassName() + "],";
                                                        }
                                                    } else {
                                                        fuzhuname += f1.get(0).getRecordName() + ",";
                                                    }
                                                } else {
                                                    fuzhuname += f1.get(0).getRecordName() + ",";
                                                }
                                            }
                                        }
                                        if (StrUtil.isNotBlank(vv.getCdfine5())) {
                                            List<DefinedRecord> f1 = zdyfzhslist.stream().filter(hs -> hs.getId().equals(vv.getCdfine5())).collect(Collectors.toList());
                                            if (f1.size() > 0) {
                                                List<FuzhuHesuan> collect = fzhslist.stream().filter(fz -> fz.getCfield().equals("cdfine5")).collect(Collectors.toList());
                                                if (collect.size() > 0) {
                                                    // 判断是否 自定义项档案表名
                                                    if (collect.get(0).getCankaoDuixiangTable().equals("defined_record")) {
                                                        fuzhuname += collect.get(0).getCname() + "[" + f1.get(0).getRecordName() + "],";
                                                    }
                                                    // 是否固定的分类档案表名
                                                    else if (collect.get(0).getCankaoDuixiangTable().equals("customer_class")) {
                                                        List<CustomerClass> collect1 = cusClassList.stream().filter(cus -> cus.getUniqueCustclass().equals(vv.getCdfine5())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getCusCclassName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("supplier_class")) {
                                                        List<SupplierClass> collect1 = supClassList.stream().filter(sup -> sup.getUniqueCustclass().equals(vv.getCdfine5())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getCusCclassName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("sys_psn_type")) {
                                                        List<SysPsnType> collect1 = psnTypeList.stream().filter(psn -> psn.getUniqueCode().equals(vv.getCdfine5())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getPsnTypeName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("project_class")) {
                                                        List<ProjectClass> collect1 = proClassList.stream().filter(pro -> pro.getUniqueCode().equals(vv.getCdfine5())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getProjectClassName() + "],";
                                                        }
                                                    } else {
                                                        fuzhuname += f1.get(0).getRecordName() + ",";
                                                    }
                                                } else {
                                                    fuzhuname += f1.get(0).getRecordName() + ",";
                                                }
                                            }
                                        }
                                        if (StrUtil.isNotBlank(vv.getCdfine6())) {
                                            List<DefinedRecord> f1 = zdyfzhslist.stream().filter(hs -> hs.getId().equals(vv.getCdfine6())).collect(Collectors.toList());
                                            if (f1.size() > 0) {
                                                List<FuzhuHesuan> collect = fzhslist.stream().filter(fz -> fz.getCfield().equals("cdfine6")).collect(Collectors.toList());
                                                if (collect.size() > 0) {
                                                    // 判断是否 自定义项档案表名
                                                    if (collect.get(0).getCankaoDuixiangTable().equals("defined_record")) {
                                                        fuzhuname += collect.get(0).getCname() + "[" + f1.get(0).getRecordName() + "],";
                                                    }
                                                    // 是否固定的分类档案表名
                                                    else if (collect.get(0).getCankaoDuixiangTable().equals("customer_class")) {
                                                        List<CustomerClass> collect1 = cusClassList.stream().filter(cus -> cus.getUniqueCustclass().equals(vv.getCdfine6())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getCusCclassName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("supplier_class")) {
                                                        List<SupplierClass> collect1 = supClassList.stream().filter(sup -> sup.getUniqueCustclass().equals(vv.getCdfine6())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getCusCclassName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("sys_psn_type")) {
                                                        List<SysPsnType> collect1 = psnTypeList.stream().filter(psn -> psn.getUniqueCode().equals(vv.getCdfine6())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getPsnTypeName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("project_class")) {
                                                        List<ProjectClass> collect1 = proClassList.stream().filter(pro -> pro.getUniqueCode().equals(vv.getCdfine6())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getProjectClassName() + "],";
                                                        }
                                                    } else {
                                                        fuzhuname += f1.get(0).getRecordName() + ",";
                                                    }
                                                } else {
                                                    fuzhuname += f1.get(0).getRecordName() + ",";
                                                }
                                            }
                                        }
                                        if (StrUtil.isNotBlank(vv.getCdfine7())) {
                                            List<DefinedRecord> f1 = zdyfzhslist.stream().filter(hs -> hs.getId().equals(vv.getCdfine7())).collect(Collectors.toList());
                                            if (f1.size() > 0) {
                                                List<FuzhuHesuan> collect = fzhslist.stream().filter(fz -> fz.getCfield().equals("cdfine7")).collect(Collectors.toList());
                                                if (collect.size() > 0) {
                                                    // 判断是否 自定义项档案表名
                                                    if (collect.get(0).getCankaoDuixiangTable().equals("defined_record")) {
                                                        fuzhuname += collect.get(0).getCname() + "[" + f1.get(0).getRecordName() + "],";
                                                    }
                                                    // 是否固定的分类档案表名
                                                    else if (collect.get(0).getCankaoDuixiangTable().equals("customer_class")) {
                                                        List<CustomerClass> collect1 = cusClassList.stream().filter(cus -> cus.getUniqueCustclass().equals(vv.getCdfine7())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getCusCclassName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("supplier_class")) {
                                                        List<SupplierClass> collect1 = supClassList.stream().filter(sup -> sup.getUniqueCustclass().equals(vv.getCdfine7())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getCusCclassName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("sys_psn_type")) {
                                                        List<SysPsnType> collect1 = psnTypeList.stream().filter(psn -> psn.getUniqueCode().equals(vv.getCdfine7())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getPsnTypeName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("project_class")) {
                                                        List<ProjectClass> collect1 = proClassList.stream().filter(pro -> pro.getUniqueCode().equals(vv.getCdfine7())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getProjectClassName() + "],";
                                                        }
                                                    } else {
                                                        fuzhuname += f1.get(0).getRecordName() + ",";
                                                    }
                                                } else {
                                                    fuzhuname += f1.get(0).getRecordName() + ",";
                                                }
                                            }
                                        }
                                        if (StrUtil.isNotBlank(vv.getCdfine8())) {
                                            List<DefinedRecord> f1 = zdyfzhslist.stream().filter(hs -> hs.getId().equals(vv.getCdfine8())).collect(Collectors.toList());
                                            if (f1.size() > 0) {
                                                List<FuzhuHesuan> collect = fzhslist.stream().filter(fz -> fz.getCfield().equals("cdfine8")).collect(Collectors.toList());
                                                if (collect.size() > 0) {
                                                    // 判断是否 自定义项档案表名
                                                    if (collect.get(0).getCankaoDuixiangTable().equals("defined_record")) {
                                                        fuzhuname += collect.get(0).getCname() + "[" + f1.get(0).getRecordName() + "],";
                                                    }
                                                    // 是否固定的分类档案表名
                                                    else if (collect.get(0).getCankaoDuixiangTable().equals("customer_class")) {
                                                        List<CustomerClass> collect1 = cusClassList.stream().filter(cus -> cus.getUniqueCustclass().equals(vv.getCdfine8())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getCusCclassName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("supplier_class")) {
                                                        List<SupplierClass> collect1 = supClassList.stream().filter(sup -> sup.getUniqueCustclass().equals(vv.getCdfine8())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getCusCclassName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("sys_psn_type")) {
                                                        List<SysPsnType> collect1 = psnTypeList.stream().filter(psn -> psn.getUniqueCode().equals(vv.getCdfine8())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getPsnTypeName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("project_class")) {
                                                        List<ProjectClass> collect1 = proClassList.stream().filter(pro -> pro.getUniqueCode().equals(vv.getCdfine8())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getProjectClassName() + "],";
                                                        }
                                                    } else {
                                                        fuzhuname += f1.get(0).getRecordName() + ",";
                                                    }
                                                } else {
                                                    fuzhuname += f1.get(0).getRecordName() + ",";
                                                }
                                            }
                                        }
                                        if (StrUtil.isNotBlank(vv.getCdfine9())) {
                                            List<DefinedRecord> f1 = zdyfzhslist.stream().filter(hs -> hs.getId().equals(vv.getCdfine9())).collect(Collectors.toList());
                                            if (f1.size() > 0) {
                                                List<FuzhuHesuan> collect = fzhslist.stream().filter(fz -> fz.getCfield().equals("cdfine9")).collect(Collectors.toList());
                                                if (collect.size() > 0) {
                                                    // 判断是否 自定义项档案表名
                                                    if (collect.get(0).getCankaoDuixiangTable().equals("defined_record")) {
                                                        fuzhuname += collect.get(0).getCname() + "[" + f1.get(0).getRecordName() + "],";
                                                    }
                                                    // 是否固定的分类档案表名
                                                    else if (collect.get(0).getCankaoDuixiangTable().equals("customer_class")) {
                                                        List<CustomerClass> collect1 = cusClassList.stream().filter(cus -> cus.getUniqueCustclass().equals(vv.getCdfine9())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getCusCclassName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("supplier_class")) {
                                                        List<SupplierClass> collect1 = supClassList.stream().filter(sup -> sup.getUniqueCustclass().equals(vv.getCdfine9())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getCusCclassName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("sys_psn_type")) {
                                                        List<SysPsnType> collect1 = psnTypeList.stream().filter(psn -> psn.getUniqueCode().equals(vv.getCdfine9())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getPsnTypeName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("project_class")) {
                                                        List<ProjectClass> collect1 = proClassList.stream().filter(pro -> pro.getUniqueCode().equals(vv.getCdfine9())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getProjectClassName() + "],";
                                                        }
                                                    } else {
                                                        fuzhuname += f1.get(0).getRecordName() + ",";
                                                    }
                                                } else {
                                                    fuzhuname += f1.get(0).getRecordName() + ",";
                                                }
                                            }
                                        }
                                        if (StrUtil.isNotBlank(vv.getCdfine10())) {
                                            List<DefinedRecord> f1 = zdyfzhslist.stream().filter(hs -> hs.getId().equals(vv.getCdfine10())).collect(Collectors.toList());
                                            if (f1.size() > 0) {
                                                List<FuzhuHesuan> collect = fzhslist.stream().filter(fz -> fz.getCfield().equals("cdfine10")).collect(Collectors.toList());
                                                if (collect.size() > 0) {
                                                    // 判断是否 自定义项档案表名
                                                    if (collect.get(0).getCankaoDuixiangTable().equals("defined_record")) {
                                                        fuzhuname += collect.get(0).getCname() + "[" + f1.get(0).getRecordName() + "],";
                                                    }
                                                    // 是否固定的分类档案表名
                                                    else if (collect.get(0).getCankaoDuixiangTable().equals("customer_class")) {
                                                        List<CustomerClass> collect1 = cusClassList.stream().filter(cus -> cus.getUniqueCustclass().equals(vv.getCdfine10())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getCusCclassName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("supplier_class")) {
                                                        List<SupplierClass> collect1 = supClassList.stream().filter(sup -> sup.getUniqueCustclass().equals(vv.getCdfine10())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getCusCclassName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("sys_psn_type")) {
                                                        List<SysPsnType> collect1 = psnTypeList.stream().filter(psn -> psn.getUniqueCode().equals(vv.getCdfine10())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getPsnTypeName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("project_class")) {
                                                        List<ProjectClass> collect1 = proClassList.stream().filter(pro -> pro.getUniqueCode().equals(vv.getCdfine10())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getProjectClassName() + "],";
                                                        }
                                                    } else {
                                                        fuzhuname += f1.get(0).getRecordName() + ",";
                                                    }
                                                } else {
                                                    fuzhuname += f1.get(0).getRecordName() + ",";
                                                }
                                            }
                                        }
                                        if (StrUtil.isNotBlank(vv.getCdfine11())) {
                                            List<DefinedRecord> f1 = zdyfzhslist.stream().filter(hs -> hs.getId().equals(vv.getCdfine11())).collect(Collectors.toList());
                                            if (f1.size() > 0) {
                                                List<FuzhuHesuan> collect = fzhslist.stream().filter(fz -> fz.getCfield().equals("cdfine11")).collect(Collectors.toList());
                                                if (collect.size() > 0) {
                                                    // 判断是否 自定义项档案表名
                                                    if (collect.get(0).getCankaoDuixiangTable().equals("defined_record")) {
                                                        fuzhuname += collect.get(0).getCname() + "[" + f1.get(0).getRecordName() + "],";
                                                    }
                                                    // 是否固定的分类档案表名
                                                    else if (collect.get(0).getCankaoDuixiangTable().equals("customer_class")) {
                                                        List<CustomerClass> collect1 = cusClassList.stream().filter(cus -> cus.getUniqueCustclass().equals(vv.getCdfine11())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getCusCclassName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("supplier_class")) {
                                                        List<SupplierClass> collect1 = supClassList.stream().filter(sup -> sup.getUniqueCustclass().equals(vv.getCdfine11())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getCusCclassName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("sys_psn_type")) {
                                                        List<SysPsnType> collect1 = psnTypeList.stream().filter(psn -> psn.getUniqueCode().equals(vv.getCdfine11())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getPsnTypeName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("project_class")) {
                                                        List<ProjectClass> collect1 = proClassList.stream().filter(pro -> pro.getUniqueCode().equals(vv.getCdfine11())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getProjectClassName() + "],";
                                                        }
                                                    } else {
                                                        fuzhuname += f1.get(0).getRecordName() + ",";
                                                    }
                                                } else {
                                                    fuzhuname += f1.get(0).getRecordName() + ",";
                                                }
                                            }
                                        }
                                        if (StrUtil.isNotBlank(vv.getCdfine12())) {
                                            List<DefinedRecord> f1 = zdyfzhslist.stream().filter(hs -> hs.getId().equals(vv.getCdfine12())).collect(Collectors.toList());
                                            if (f1.size() > 0) {
                                                List<FuzhuHesuan> collect = fzhslist.stream().filter(fz -> fz.getCfield().equals("cdfine12")).collect(Collectors.toList());
                                                if (collect.size() > 0) {
                                                    // 判断是否 自定义项档案表名
                                                    if (collect.get(0).getCankaoDuixiangTable().equals("defined_record")) {
                                                        fuzhuname += collect.get(0).getCname() + "[" + f1.get(0).getRecordName() + "],";
                                                    }
                                                    // 是否固定的分类档案表名
                                                    else if (collect.get(0).getCankaoDuixiangTable().equals("customer_class")) {
                                                        List<CustomerClass> collect1 = cusClassList.stream().filter(cus -> cus.getUniqueCustclass().equals(vv.getCdfine12())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getCusCclassName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("supplier_class")) {
                                                        List<SupplierClass> collect1 = supClassList.stream().filter(sup -> sup.getUniqueCustclass().equals(vv.getCdfine12())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getCusCclassName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("sys_psn_type")) {
                                                        List<SysPsnType> collect1 = psnTypeList.stream().filter(psn -> psn.getUniqueCode().equals(vv.getCdfine12())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getPsnTypeName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("project_class")) {
                                                        List<ProjectClass> collect1 = proClassList.stream().filter(pro -> pro.getUniqueCode().equals(vv.getCdfine12())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getProjectClassName() + "],";
                                                        }
                                                    } else {
                                                        fuzhuname += f1.get(0).getRecordName() + ",";
                                                    }
                                                } else {
                                                    fuzhuname += f1.get(0).getRecordName() + ",";
                                                }
                                            }
                                        }
                                        if (StrUtil.isNotBlank(vv.getCdfine13())) {
                                            List<DefinedRecord> f1 = zdyfzhslist.stream().filter(hs -> hs.getId().equals(vv.getCdfine13())).collect(Collectors.toList());
                                            if (f1.size() > 0) {
                                                List<FuzhuHesuan> collect = fzhslist.stream().filter(fz -> fz.getCfield().equals("cdfine13")).collect(Collectors.toList());
                                                if (collect.size() > 0) {
                                                    // 判断是否 自定义项档案表名
                                                    if (collect.get(0).getCankaoDuixiangTable().equals("defined_record")) {
                                                        fuzhuname += collect.get(0).getCname() + "[" + f1.get(0).getRecordName() + "],";
                                                    }
                                                    // 是否固定的分类档案表名
                                                    else if (collect.get(0).getCankaoDuixiangTable().equals("customer_class")) {
                                                        List<CustomerClass> collect1 = cusClassList.stream().filter(cus -> cus.getUniqueCustclass().equals(vv.getCdfine13())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getCusCclassName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("supplier_class")) {
                                                        List<SupplierClass> collect1 = supClassList.stream().filter(sup -> sup.getUniqueCustclass().equals(vv.getCdfine13())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getCusCclassName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("sys_psn_type")) {
                                                        List<SysPsnType> collect1 = psnTypeList.stream().filter(psn -> psn.getUniqueCode().equals(vv.getCdfine13())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getPsnTypeName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("project_class")) {
                                                        List<ProjectClass> collect1 = proClassList.stream().filter(pro -> pro.getUniqueCode().equals(vv.getCdfine13())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getProjectClassName() + "],";
                                                        }
                                                    } else {
                                                        fuzhuname += f1.get(0).getRecordName() + ",";
                                                    }
                                                } else {
                                                    fuzhuname += f1.get(0).getRecordName() + ",";
                                                }
                                            }
                                        }
                                        if (StrUtil.isNotBlank(vv.getCdfine14())) {
                                            List<DefinedRecord> f1 = zdyfzhslist.stream().filter(hs -> hs.getId().equals(vv.getCdfine14())).collect(Collectors.toList());
                                            if (f1.size() > 0) {
                                                List<FuzhuHesuan> collect = fzhslist.stream().filter(fz -> fz.getCfield().equals("cdfine14")).collect(Collectors.toList());
                                                if (collect.size() > 0) {
                                                    // 判断是否 自定义项档案表名
                                                    if (collect.get(0).getCankaoDuixiangTable().equals("defined_record")) {
                                                        fuzhuname += collect.get(0).getCname() + "[" + f1.get(0).getRecordName() + "],";
                                                    }
                                                    // 是否固定的分类档案表名
                                                    else if (collect.get(0).getCankaoDuixiangTable().equals("customer_class")) {
                                                        List<CustomerClass> collect1 = cusClassList.stream().filter(cus -> cus.getUniqueCustclass().equals(vv.getCdfine14())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getCusCclassName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("supplier_class")) {
                                                        List<SupplierClass> collect1 = supClassList.stream().filter(sup -> sup.getUniqueCustclass().equals(vv.getCdfine14())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getCusCclassName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("sys_psn_type")) {
                                                        List<SysPsnType> collect1 = psnTypeList.stream().filter(psn -> psn.getUniqueCode().equals(vv.getCdfine14())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getPsnTypeName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("project_class")) {
                                                        List<ProjectClass> collect1 = proClassList.stream().filter(pro -> pro.getUniqueCode().equals(vv.getCdfine14())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getProjectClassName() + "],";
                                                        }
                                                    } else {
                                                        fuzhuname += f1.get(0).getRecordName() + ",";
                                                    }
                                                } else {
                                                    fuzhuname += f1.get(0).getRecordName() + ",";
                                                }
                                            }
                                        }
                                        if (StrUtil.isNotBlank(vv.getCdfine15())) {
                                            List<DefinedRecord> f1 = zdyfzhslist.stream().filter(hs -> hs.getId().equals(vv.getCdfine15())).collect(Collectors.toList());
                                            if (f1.size() > 0) {
                                                List<FuzhuHesuan> collect = fzhslist.stream().filter(fz -> fz.getCfield().equals("cdfine15")).collect(Collectors.toList());
                                                if (collect.size() > 0) {
                                                    // 判断是否 自定义项档案表名
                                                    if (collect.get(0).getCankaoDuixiangTable().equals("defined_record")) {
                                                        fuzhuname += collect.get(0).getCname() + "[" + f1.get(0).getRecordName() + "],";
                                                    }
                                                    // 是否固定的分类档案表名
                                                    else if (collect.get(0).getCankaoDuixiangTable().equals("customer_class")) {
                                                        List<CustomerClass> collect1 = cusClassList.stream().filter(cus -> cus.getUniqueCustclass().equals(vv.getCdfine15())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getCusCclassName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("supplier_class")) {
                                                        List<SupplierClass> collect1 = supClassList.stream().filter(sup -> sup.getUniqueCustclass().equals(vv.getCdfine15())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getCusCclassName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("sys_psn_type")) {
                                                        List<SysPsnType> collect1 = psnTypeList.stream().filter(psn -> psn.getUniqueCode().equals(vv.getCdfine15())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getPsnTypeName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("project_class")) {
                                                        List<ProjectClass> collect1 = proClassList.stream().filter(pro -> pro.getUniqueCode().equals(vv.getCdfine15())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getProjectClassName() + "],";
                                                        }
                                                    } else {
                                                        fuzhuname += f1.get(0).getRecordName() + ",";
                                                    }
                                                } else {
                                                    fuzhuname += f1.get(0).getRecordName() + ",";
                                                }
                                            }
                                        }
                                        if (StrUtil.isNotBlank(vv.getCdfine16())) {
                                            List<DefinedRecord> f1 = zdyfzhslist.stream().filter(hs -> hs.getId().equals(vv.getCdfine16())).collect(Collectors.toList());
                                            if (f1.size() > 0) {
                                                List<FuzhuHesuan> collect = fzhslist.stream().filter(fz -> fz.getCfield().equals("cdfine16")).collect(Collectors.toList());
                                                if (collect.size() > 0) {
                                                    // 判断是否 自定义项档案表名
                                                    if (collect.get(0).getCankaoDuixiangTable().equals("defined_record")) {
                                                        fuzhuname += collect.get(0).getCname() + "[" + f1.get(0).getRecordName() + "],";
                                                    }
                                                    // 是否固定的分类档案表名
                                                    else if (collect.get(0).getCankaoDuixiangTable().equals("customer_class")) {
                                                        List<CustomerClass> collect1 = cusClassList.stream().filter(cus -> cus.getUniqueCustclass().equals(vv.getCdfine16())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getCusCclassName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("supplier_class")) {
                                                        List<SupplierClass> collect1 = supClassList.stream().filter(sup -> sup.getUniqueCustclass().equals(vv.getCdfine16())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getCusCclassName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("sys_psn_type")) {
                                                        List<SysPsnType> collect1 = psnTypeList.stream().filter(psn -> psn.getUniqueCode().equals(vv.getCdfine16())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getPsnTypeName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("project_class")) {
                                                        List<ProjectClass> collect1 = proClassList.stream().filter(pro -> pro.getUniqueCode().equals(vv.getCdfine16())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getProjectClassName() + "],";
                                                        }
                                                    } else {
                                                        fuzhuname += f1.get(0).getRecordName() + ",";
                                                    }
                                                } else {
                                                    fuzhuname += f1.get(0).getRecordName() + ",";
                                                }
                                            }
                                        }
                                        if (StrUtil.isNotBlank(vv.getCdfine17())) {
                                            List<DefinedRecord> f1 = zdyfzhslist.stream().filter(hs -> hs.getId().equals(vv.getCdfine17())).collect(Collectors.toList());
                                            if (f1.size() > 0) {
                                                List<FuzhuHesuan> collect = fzhslist.stream().filter(fz -> fz.getCfield().equals("cdfine17")).collect(Collectors.toList());
                                                if (collect.size() > 0) {
                                                    // 判断是否 自定义项档案表名
                                                    if (collect.get(0).getCankaoDuixiangTable().equals("defined_record")) {
                                                        fuzhuname += collect.get(0).getCname() + "[" + f1.get(0).getRecordName() + "],";
                                                    }
                                                    // 是否固定的分类档案表名
                                                    else if (collect.get(0).getCankaoDuixiangTable().equals("customer_class")) {
                                                        List<CustomerClass> collect1 = cusClassList.stream().filter(cus -> cus.getUniqueCustclass().equals(vv.getCdfine17())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getCusCclassName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("supplier_class")) {
                                                        List<SupplierClass> collect1 = supClassList.stream().filter(sup -> sup.getUniqueCustclass().equals(vv.getCdfine17())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getCusCclassName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("sys_psn_type")) {
                                                        List<SysPsnType> collect1 = psnTypeList.stream().filter(psn -> psn.getUniqueCode().equals(vv.getCdfine17())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getPsnTypeName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("project_class")) {
                                                        List<ProjectClass> collect1 = proClassList.stream().filter(pro -> pro.getUniqueCode().equals(vv.getCdfine17())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getProjectClassName() + "],";
                                                        }
                                                    } else {
                                                        fuzhuname += f1.get(0).getRecordName() + ",";
                                                    }
                                                } else {
                                                    fuzhuname += f1.get(0).getRecordName() + ",";
                                                }
                                            }
                                        }
                                        if (StrUtil.isNotBlank(vv.getCdfine18())) {
                                            List<DefinedRecord> f1 = zdyfzhslist.stream().filter(hs -> hs.getId().equals(vv.getCdfine18())).collect(Collectors.toList());
                                            if (f1.size() > 0) {
                                                List<FuzhuHesuan> collect = fzhslist.stream().filter(fz -> fz.getCfield().equals("cdfine18")).collect(Collectors.toList());
                                                if (collect.size() > 0) {
                                                    // 判断是否 自定义项档案表名
                                                    if (collect.get(0).getCankaoDuixiangTable().equals("defined_record")) {
                                                        fuzhuname += collect.get(0).getCname() + "[" + f1.get(0).getRecordName() + "],";
                                                    }
                                                    // 是否固定的分类档案表名
                                                    else if (collect.get(0).getCankaoDuixiangTable().equals("customer_class")) {
                                                        List<CustomerClass> collect1 = cusClassList.stream().filter(cus -> cus.getUniqueCustclass().equals(vv.getCdfine18())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getCusCclassName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("supplier_class")) {
                                                        List<SupplierClass> collect1 = supClassList.stream().filter(sup -> sup.getUniqueCustclass().equals(vv.getCdfine18())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getCusCclassName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("sys_psn_type")) {
                                                        List<SysPsnType> collect1 = psnTypeList.stream().filter(psn -> psn.getUniqueCode().equals(vv.getCdfine18())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getPsnTypeName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("project_class")) {
                                                        List<ProjectClass> collect1 = proClassList.stream().filter(pro -> pro.getUniqueCode().equals(vv.getCdfine18())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getProjectClassName() + "],";
                                                        }
                                                    } else {
                                                        fuzhuname += f1.get(0).getRecordName() + ",";
                                                    }
                                                } else {
                                                    fuzhuname += f1.get(0).getRecordName() + ",";
                                                }
                                            }
                                        }
                                        if (StrUtil.isNotBlank(vv.getCdfine19())) {
                                            List<DefinedRecord> f1 = zdyfzhslist.stream().filter(hs -> hs.getId().equals(vv.getCdfine19())).collect(Collectors.toList());
                                            if (f1.size() > 0) {
                                                List<FuzhuHesuan> collect = fzhslist.stream().filter(fz -> fz.getCfield().equals("cdfine19")).collect(Collectors.toList());
                                                if (collect.size() > 0) {
                                                    // 判断是否 自定义项档案表名
                                                    if (collect.get(0).getCankaoDuixiangTable().equals("defined_record")) {
                                                        fuzhuname += collect.get(0).getCname() + "[" + f1.get(0).getRecordName() + "],";
                                                    }
                                                    // 是否固定的分类档案表名
                                                    else if (collect.get(0).getCankaoDuixiangTable().equals("customer_class")) {
                                                        List<CustomerClass> collect1 = cusClassList.stream().filter(cus -> cus.getUniqueCustclass().equals(vv.getCdfine19())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getCusCclassName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("supplier_class")) {
                                                        List<SupplierClass> collect1 = supClassList.stream().filter(sup -> sup.getUniqueCustclass().equals(vv.getCdfine19())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getCusCclassName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("sys_psn_type")) {
                                                        List<SysPsnType> collect1 = psnTypeList.stream().filter(psn -> psn.getUniqueCode().equals(vv.getCdfine19())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getPsnTypeName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("project_class")) {
                                                        List<ProjectClass> collect1 = proClassList.stream().filter(pro -> pro.getUniqueCode().equals(vv.getCdfine19())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getProjectClassName() + "],";
                                                        }
                                                    } else {
                                                        fuzhuname += f1.get(0).getRecordName() + ",";
                                                    }
                                                } else {
                                                    fuzhuname += f1.get(0).getRecordName() + ",";
                                                }
                                            }
                                        }
                                        if (StrUtil.isNotBlank(vv.getCdfine20())) {
                                            List<DefinedRecord> f1 = zdyfzhslist.stream().filter(hs -> hs.getId().equals(vv.getCdfine20())).collect(Collectors.toList());
                                            if (f1.size() > 0) {
                                                List<FuzhuHesuan> collect = fzhslist.stream().filter(fz -> fz.getCfield().equals("cdfine20")).collect(Collectors.toList());
                                                if (collect.size() > 0) {
                                                    // 判断是否 自定义项档案表名
                                                    if (collect.get(0).getCankaoDuixiangTable().equals("defined_record")) {
                                                        fuzhuname += collect.get(0).getCname() + "[" + f1.get(0).getRecordName() + "],";
                                                    }
                                                    // 是否固定的分类档案表名
                                                    else if (collect.get(0).getCankaoDuixiangTable().equals("customer_class")) {
                                                        List<CustomerClass> collect1 = cusClassList.stream().filter(cus -> cus.getUniqueCustclass().equals(vv.getCdfine20())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getCusCclassName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("supplier_class")) {
                                                        List<SupplierClass> collect1 = supClassList.stream().filter(sup -> sup.getUniqueCustclass().equals(vv.getCdfine20())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getCusCclassName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("sys_psn_type")) {
                                                        List<SysPsnType> collect1 = psnTypeList.stream().filter(psn -> psn.getUniqueCode().equals(vv.getCdfine20())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getPsnTypeName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("project_class")) {
                                                        List<ProjectClass> collect1 = proClassList.stream().filter(pro -> pro.getUniqueCode().equals(vv.getCdfine20())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getProjectClassName() + "],";
                                                        }
                                                    } else {
                                                        fuzhuname += f1.get(0).getRecordName() + ",";
                                                    }
                                                } else {
                                                    fuzhuname += f1.get(0).getRecordName() + ",";
                                                }
                                            }
                                        }
                                        if (StrUtil.isNotBlank(vv.getCdfine21())) {
                                            List<DefinedRecord> f1 = zdyfzhslist.stream().filter(hs -> hs.getId().equals(vv.getCdfine21())).collect(Collectors.toList());
                                            if (f1.size() > 0) {
                                                List<FuzhuHesuan> collect = fzhslist.stream().filter(fz -> fz.getCfield().equals("cdfine21")).collect(Collectors.toList());
                                                if (collect.size() > 0) {
                                                    // 判断是否 自定义项档案表名
                                                    if (collect.get(0).getCankaoDuixiangTable().equals("defined_record")) {
                                                        fuzhuname += collect.get(0).getCname() + "[" + f1.get(0).getRecordName() + "],";
                                                    }
                                                    // 是否固定的分类档案表名
                                                    else if (collect.get(0).getCankaoDuixiangTable().equals("customer_class")) {
                                                        List<CustomerClass> collect1 = cusClassList.stream().filter(cus -> cus.getUniqueCustclass().equals(vv.getCdfine21())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getCusCclassName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("supplier_class")) {
                                                        List<SupplierClass> collect1 = supClassList.stream().filter(sup -> sup.getUniqueCustclass().equals(vv.getCdfine21())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getCusCclassName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("sys_psn_type")) {
                                                        List<SysPsnType> collect1 = psnTypeList.stream().filter(psn -> psn.getUniqueCode().equals(vv.getCdfine21())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getPsnTypeName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("project_class")) {
                                                        List<ProjectClass> collect1 = proClassList.stream().filter(pro -> pro.getUniqueCode().equals(vv.getCdfine21())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getProjectClassName() + "],";
                                                        }
                                                    } else {
                                                        fuzhuname += f1.get(0).getRecordName() + ",";
                                                    }
                                                } else {
                                                    fuzhuname += f1.get(0).getRecordName() + ",";
                                                }
                                            }
                                        }
                                        if (StrUtil.isNotBlank(vv.getCdfine22())) {
                                            List<DefinedRecord> f1 = zdyfzhslist.stream().filter(hs -> hs.getId().equals(vv.getCdfine22())).collect(Collectors.toList());
                                            if (f1.size() > 0) {
                                                List<FuzhuHesuan> collect = fzhslist.stream().filter(fz -> fz.getCfield().equals("cdfine22")).collect(Collectors.toList());
                                                if (collect.size() > 0) {
                                                    // 判断是否 自定义项档案表名
                                                    if (collect.get(0).getCankaoDuixiangTable().equals("defined_record")) {
                                                        fuzhuname += collect.get(0).getCname() + "[" + f1.get(0).getRecordName() + "],";
                                                    }
                                                    // 是否固定的分类档案表名
                                                    else if (collect.get(0).getCankaoDuixiangTable().equals("customer_class")) {
                                                        List<CustomerClass> collect1 = cusClassList.stream().filter(cus -> cus.getUniqueCustclass().equals(vv.getCdfine22())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getCusCclassName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("supplier_class")) {
                                                        List<SupplierClass> collect1 = supClassList.stream().filter(sup -> sup.getUniqueCustclass().equals(vv.getCdfine22())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getCusCclassName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("sys_psn_type")) {
                                                        List<SysPsnType> collect1 = psnTypeList.stream().filter(psn -> psn.getUniqueCode().equals(vv.getCdfine22())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getPsnTypeName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("project_class")) {
                                                        List<ProjectClass> collect1 = proClassList.stream().filter(pro -> pro.getUniqueCode().equals(vv.getCdfine22())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getProjectClassName() + "],";
                                                        }
                                                    } else {
                                                        fuzhuname += f1.get(0).getRecordName() + ",";
                                                    }
                                                } else {
                                                    fuzhuname += f1.get(0).getRecordName() + ",";
                                                }
                                            }
                                        }
                                        if (StrUtil.isNotBlank(vv.getCdfine23())) {
                                            List<DefinedRecord> f1 = zdyfzhslist.stream().filter(hs -> hs.getId().equals(vv.getCdfine23())).collect(Collectors.toList());
                                            if (f1.size() > 0) {
                                                List<FuzhuHesuan> collect = fzhslist.stream().filter(fz -> fz.getCfield().equals("cdfine23")).collect(Collectors.toList());
                                                if (collect.size() > 0) {
                                                    // 判断是否 自定义项档案表名
                                                    if (collect.get(0).getCankaoDuixiangTable().equals("defined_record")) {
                                                        fuzhuname += collect.get(0).getCname() + "[" + f1.get(0).getRecordName() + "],";
                                                    }
                                                    // 是否固定的分类档案表名
                                                    else if (collect.get(0).getCankaoDuixiangTable().equals("customer_class")) {
                                                        List<CustomerClass> collect1 = cusClassList.stream().filter(cus -> cus.getUniqueCustclass().equals(vv.getCdfine23())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getCusCclassName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("supplier_class")) {
                                                        List<SupplierClass> collect1 = supClassList.stream().filter(sup -> sup.getUniqueCustclass().equals(vv.getCdfine23())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getCusCclassName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("sys_psn_type")) {
                                                        List<SysPsnType> collect1 = psnTypeList.stream().filter(psn -> psn.getUniqueCode().equals(vv.getCdfine23())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getPsnTypeName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("project_class")) {
                                                        List<ProjectClass> collect1 = proClassList.stream().filter(pro -> pro.getUniqueCode().equals(vv.getCdfine23())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getProjectClassName() + "],";
                                                        }
                                                    } else {
                                                        fuzhuname += f1.get(0).getRecordName() + ",";
                                                    }
                                                } else {
                                                    fuzhuname += f1.get(0).getRecordName() + ",";
                                                }
                                            }
                                        }
                                        if (StrUtil.isNotBlank(vv.getCdfine24())) {
                                            List<DefinedRecord> f1 = zdyfzhslist.stream().filter(hs -> hs.getId().equals(vv.getCdfine24())).collect(Collectors.toList());
                                            if (f1.size() > 0) {
                                                List<FuzhuHesuan> collect = fzhslist.stream().filter(fz -> fz.getCfield().equals("cdfine24")).collect(Collectors.toList());
                                                if (collect.size() > 0) {
                                                    // 判断是否 自定义项档案表名
                                                    if (collect.get(0).getCankaoDuixiangTable().equals("defined_record")) {
                                                        fuzhuname += collect.get(0).getCname() + "[" + f1.get(0).getRecordName() + "],";
                                                    }
                                                    // 是否固定的分类档案表名
                                                    else if (collect.get(0).getCankaoDuixiangTable().equals("customer_class")) {
                                                        List<CustomerClass> collect1 = cusClassList.stream().filter(cus -> cus.getUniqueCustclass().equals(vv.getCdfine24())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getCusCclassName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("supplier_class")) {
                                                        List<SupplierClass> collect1 = supClassList.stream().filter(sup -> sup.getUniqueCustclass().equals(vv.getCdfine24())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getCusCclassName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("sys_psn_type")) {
                                                        List<SysPsnType> collect1 = psnTypeList.stream().filter(psn -> psn.getUniqueCode().equals(vv.getCdfine24())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getPsnTypeName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("project_class")) {
                                                        List<ProjectClass> collect1 = proClassList.stream().filter(pro -> pro.getUniqueCode().equals(vv.getCdfine24())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getProjectClassName() + "],";
                                                        }
                                                    } else {
                                                        fuzhuname += f1.get(0).getRecordName() + ",";
                                                    }
                                                } else {
                                                    fuzhuname += f1.get(0).getRecordName() + ",";
                                                }
                                            }
                                        }
                                        if (StrUtil.isNotBlank(vv.getCdfine25())) {
                                            List<DefinedRecord> f1 = zdyfzhslist.stream().filter(hs -> hs.getId().equals(vv.getCdfine25())).collect(Collectors.toList());
                                            if (f1.size() > 0) {
                                                List<FuzhuHesuan> collect = fzhslist.stream().filter(fz -> fz.getCfield().equals("cdfine25")).collect(Collectors.toList());
                                                if (collect.size() > 0) {
                                                    // 判断是否 自定义项档案表名
                                                    if (collect.get(0).getCankaoDuixiangTable().equals("defined_record")) {
                                                        fuzhuname += collect.get(0).getCname() + "[" + f1.get(0).getRecordName() + "],";
                                                    }
                                                    // 是否固定的分类档案表名
                                                    else if (collect.get(0).getCankaoDuixiangTable().equals("customer_class")) {
                                                        List<CustomerClass> collect1 = cusClassList.stream().filter(cus -> cus.getUniqueCustclass().equals(vv.getCdfine25())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getCusCclassName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("supplier_class")) {
                                                        List<SupplierClass> collect1 = supClassList.stream().filter(sup -> sup.getUniqueCustclass().equals(vv.getCdfine25())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getCusCclassName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("sys_psn_type")) {
                                                        List<SysPsnType> collect1 = psnTypeList.stream().filter(psn -> psn.getUniqueCode().equals(vv.getCdfine25())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getPsnTypeName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("project_class")) {
                                                        List<ProjectClass> collect1 = proClassList.stream().filter(pro -> pro.getUniqueCode().equals(vv.getCdfine25())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getProjectClassName() + "],";
                                                        }
                                                    } else {
                                                        fuzhuname += f1.get(0).getRecordName() + ",";
                                                    }
                                                } else {
                                                    fuzhuname += f1.get(0).getRecordName() + ",";
                                                }
                                            }
                                        }
                                        if (StrUtil.isNotBlank(vv.getCdfine26())) {
                                            List<DefinedRecord> f1 = zdyfzhslist.stream().filter(hs -> hs.getId().equals(vv.getCdfine26())).collect(Collectors.toList());
                                            if (f1.size() > 0) {
                                                List<FuzhuHesuan> collect = fzhslist.stream().filter(fz -> fz.getCfield().equals("cdfine26")).collect(Collectors.toList());
                                                if (collect.size() > 0) {
                                                    // 判断是否 自定义项档案表名
                                                    if (collect.get(0).getCankaoDuixiangTable().equals("defined_record")) {
                                                        fuzhuname += collect.get(0).getCname() + "[" + f1.get(0).getRecordName() + "],";
                                                    }
                                                    // 是否固定的分类档案表名
                                                    else if (collect.get(0).getCankaoDuixiangTable().equals("customer_class")) {
                                                        List<CustomerClass> collect1 = cusClassList.stream().filter(cus -> cus.getUniqueCustclass().equals(vv.getCdfine26())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getCusCclassName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("supplier_class")) {
                                                        List<SupplierClass> collect1 = supClassList.stream().filter(sup -> sup.getUniqueCustclass().equals(vv.getCdfine26())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getCusCclassName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("sys_psn_type")) {
                                                        List<SysPsnType> collect1 = psnTypeList.stream().filter(psn -> psn.getUniqueCode().equals(vv.getCdfine26())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getPsnTypeName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("project_class")) {
                                                        List<ProjectClass> collect1 = proClassList.stream().filter(pro -> pro.getUniqueCode().equals(vv.getCdfine26())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getProjectClassName() + "],";
                                                        }
                                                    } else {
                                                        fuzhuname += f1.get(0).getRecordName() + ",";
                                                    }
                                                } else {
                                                    fuzhuname += f1.get(0).getRecordName() + ",";
                                                }
                                            }
                                        }
                                        if (StrUtil.isNotBlank(vv.getCdfine27())) {
                                            List<DefinedRecord> f1 = zdyfzhslist.stream().filter(hs -> hs.getId().equals(vv.getCdfine27())).collect(Collectors.toList());
                                            if (f1.size() > 0) {
                                                List<FuzhuHesuan> collect = fzhslist.stream().filter(fz -> fz.getCfield().equals("cdfine27")).collect(Collectors.toList());
                                                if (collect.size() > 0) {
                                                    // 判断是否 自定义项档案表名
                                                    if (collect.get(0).getCankaoDuixiangTable().equals("defined_record")) {
                                                        fuzhuname += collect.get(0).getCname() + "[" + f1.get(0).getRecordName() + "],";
                                                    }
                                                    // 是否固定的分类档案表名
                                                    else if (collect.get(0).getCankaoDuixiangTable().equals("customer_class")) {
                                                        List<CustomerClass> collect1 = cusClassList.stream().filter(cus -> cus.getUniqueCustclass().equals(vv.getCdfine27())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getCusCclassName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("supplier_class")) {
                                                        List<SupplierClass> collect1 = supClassList.stream().filter(sup -> sup.getUniqueCustclass().equals(vv.getCdfine27())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getCusCclassName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("sys_psn_type")) {
                                                        List<SysPsnType> collect1 = psnTypeList.stream().filter(psn -> psn.getUniqueCode().equals(vv.getCdfine27())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getPsnTypeName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("project_class")) {
                                                        List<ProjectClass> collect1 = proClassList.stream().filter(pro -> pro.getUniqueCode().equals(vv.getCdfine27())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getProjectClassName() + "],";
                                                        }
                                                    } else {
                                                        fuzhuname += f1.get(0).getRecordName() + ",";
                                                    }
                                                } else {
                                                    fuzhuname += f1.get(0).getRecordName() + ",";
                                                }
                                            }
                                        }
                                        if (StrUtil.isNotBlank(vv.getCdfine28())) {
                                            List<DefinedRecord> f1 = zdyfzhslist.stream().filter(hs -> hs.getId().equals(vv.getCdfine28())).collect(Collectors.toList());
                                            if (f1.size() > 0) {
                                                List<FuzhuHesuan> collect = fzhslist.stream().filter(fz -> fz.getCfield().equals("cdfine28")).collect(Collectors.toList());
                                                if (collect.size() > 0) {
                                                    // 判断是否 自定义项档案表名
                                                    if (collect.get(0).getCankaoDuixiangTable().equals("defined_record")) {
                                                        fuzhuname += collect.get(0).getCname() + "[" + f1.get(0).getRecordName() + "],";
                                                    }
                                                    // 是否固定的分类档案表名
                                                    else if (collect.get(0).getCankaoDuixiangTable().equals("customer_class")) {
                                                        List<CustomerClass> collect1 = cusClassList.stream().filter(cus -> cus.getUniqueCustclass().equals(vv.getCdfine28())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getCusCclassName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("supplier_class")) {
                                                        List<SupplierClass> collect1 = supClassList.stream().filter(sup -> sup.getUniqueCustclass().equals(vv.getCdfine28())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getCusCclassName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("sys_psn_type")) {
                                                        List<SysPsnType> collect1 = psnTypeList.stream().filter(psn -> psn.getUniqueCode().equals(vv.getCdfine28())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getPsnTypeName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("project_class")) {
                                                        List<ProjectClass> collect1 = proClassList.stream().filter(pro -> pro.getUniqueCode().equals(vv.getCdfine28())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getProjectClassName() + "],";
                                                        }
                                                    } else {
                                                        fuzhuname += f1.get(0).getRecordName() + ",";
                                                    }
                                                } else {
                                                    fuzhuname += f1.get(0).getRecordName() + ",";
                                                }
                                            }
                                        }
                                        if (StrUtil.isNotBlank(vv.getCdfine29())) {
                                            List<DefinedRecord> f1 = zdyfzhslist.stream().filter(hs -> hs.getId().equals(vv.getCdfine29())).collect(Collectors.toList());
                                            if (f1.size() > 0) {
                                                List<FuzhuHesuan> collect = fzhslist.stream().filter(fz -> fz.getCfield().equals("cdfine29")).collect(Collectors.toList());
                                                if (collect.size() > 0) {
                                                    // 判断是否 自定义项档案表名
                                                    if (collect.get(0).getCankaoDuixiangTable().equals("defined_record")) {
                                                        fuzhuname += collect.get(0).getCname() + "[" + f1.get(0).getRecordName() + "],";
                                                    }
                                                    // 是否固定的分类档案表名
                                                    else if (collect.get(0).getCankaoDuixiangTable().equals("customer_class")) {
                                                        List<CustomerClass> collect1 = cusClassList.stream().filter(cus -> cus.getUniqueCustclass().equals(vv.getCdfine29())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getCusCclassName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("supplier_class")) {
                                                        List<SupplierClass> collect1 = supClassList.stream().filter(sup -> sup.getUniqueCustclass().equals(vv.getCdfine29())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getCusCclassName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("sys_psn_type")) {
                                                        List<SysPsnType> collect1 = psnTypeList.stream().filter(psn -> psn.getUniqueCode().equals(vv.getCdfine29())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getPsnTypeName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("project_class")) {
                                                        List<ProjectClass> collect1 = proClassList.stream().filter(pro -> pro.getUniqueCode().equals(vv.getCdfine29())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getProjectClassName() + "],";
                                                        }
                                                    } else {
                                                        fuzhuname += f1.get(0).getRecordName() + ",";
                                                    }
                                                } else {
                                                    fuzhuname += f1.get(0).getRecordName() + ",";
                                                }
                                            }
                                        }
                                        if (StrUtil.isNotBlank(vv.getCdfine30())) {
                                            List<DefinedRecord> f1 = zdyfzhslist.stream().filter(hs -> hs.getId().equals(vv.getCdfine30())).collect(Collectors.toList());
                                            if (f1.size() > 0) {
                                                List<FuzhuHesuan> collect = fzhslist.stream().filter(fz -> fz.getCfield().equals("cdfine30")).collect(Collectors.toList());
                                                if (collect.size() > 0) {
                                                    // 判断是否 自定义项档案表名
                                                    if (collect.get(0).getCankaoDuixiangTable().equals("defined_record")) {
                                                        fuzhuname += collect.get(0).getCname() + "[" + f1.get(0).getRecordName() + "],";
                                                    }
                                                    // 是否固定的分类档案表名
                                                    else if (collect.get(0).getCankaoDuixiangTable().equals("customer_class")) {
                                                        List<CustomerClass> collect1 = cusClassList.stream().filter(cus -> cus.getUniqueCustclass().equals(vv.getCdfine30())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getCusCclassName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("supplier_class")) {
                                                        List<SupplierClass> collect1 = supClassList.stream().filter(sup -> sup.getUniqueCustclass().equals(vv.getCdfine30())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getCusCclassName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("sys_psn_type")) {
                                                        List<SysPsnType> collect1 = psnTypeList.stream().filter(psn -> psn.getUniqueCode().equals(vv.getCdfine30())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getPsnTypeName() + "],";
                                                        }
                                                    } else if (collect.get(0).getCankaoDuixiangTable().equals("project_class")) {
                                                        List<ProjectClass> collect1 = proClassList.stream().filter(pro -> pro.getUniqueCode().equals(vv.getCdfine30())).collect(Collectors.toList());
                                                        if (collect1.size() > 0) {
                                                            fuzhuname += collect.get(0).getCname() + "[" + collect1.get(0).getProjectClassName() + "],";
                                                        }
                                                    } else {
                                                        fuzhuname += f1.get(0).getRecordName() + ",";
                                                    }
                                                } else {
                                                    fuzhuname += f1.get(0).getRecordName() + ",";
                                                }
                                            }
                                        }

                                        QjsyjzVo vo2 = new QjsyjzVo();
                                        vo2.setYuemx(new ArrayList<>());
                                        vo2.setChildrenflag("0");
                                        vo2.setChamd(vv.getChamd());
                                        vo2.setChamc(vv.getChamc());
                                        vo2.setCcode(vv.getCcode());
                                        vo2.setCcodeName(vv.getCcodeName());
                                        vo2.setBprogerty(vv.getBprogerty());
                                        vo2.setDigest(digest);
                                        vo2.setProfitCcode(map.get("liRunCode").toString());
                                        vo2.setProfitBprogerty(profitBprogerty);
                                        vo2.setChildrenflag("1");
                                        vo2.setFuzhuName(!fuzhuname.equals("") ? fuzhuname.substring(0, fuzhuname.length() - 1) : "");
                                        vo2.setCdeptId(vv.getCdeptId());
                                        vo2.setCpersonId(vv.getCpersonId());
                                        vo2.setCcusId(vv.getCcusId());
                                        vo2.setCsupId(vv.getCsupId());
                                        vo2.setProjectId(vv.getProjectId());
                                        vo2.setCdfine1(vv.getCdfine1());
                                        vo2.setCdfine2(vv.getCdfine2());
                                        vo2.setCdfine3(vv.getCdfine3());
                                        vo2.setCdfine4(vv.getCdfine4());
                                        vo2.setCdfine5(vv.getCdfine5());
                                        vo2.setCdfine6(vv.getCdfine6());
                                        vo2.setCdfine7(vv.getCdfine7());
                                        vo2.setCdfine8(vv.getCdfine8());
                                        vo2.setCdfine9(vv.getCdfine9());
                                        vo2.setCdfine10(vv.getCdfine10());
                                        vo2.setCdfine11(vv.getCdfine11());
                                        vo2.setCdfine12(vv.getCdfine12());
                                        vo2.setCdfine13(vv.getCdfine13());
                                        vo2.setCdfine14(vv.getCdfine14());
                                        vo2.setCdfine15(vv.getCdfine15());
                                        vo2.setCdfine16(vv.getCdfine16());
                                        vo2.setCdfine17(vv.getCdfine17());
                                        vo2.setCdfine18(vv.getCdfine18());
                                        vo2.setCdfine19(vv.getCdfine19());
                                        vo2.setCdfine20(vv.getCdfine20());
                                        vo2.setCdfine21(vv.getCdfine21());
                                        vo2.setCdfine22(vv.getCdfine22());
                                        vo2.setCdfine23(vv.getCdfine23());
                                        vo2.setCdfine24(vv.getCdfine24());
                                        vo2.setCdfine25(vv.getCdfine25());
                                        vo2.setCdfine26(vv.getCdfine26());
                                        vo2.setCdfine27(vv.getCdfine27());
                                        vo2.setCdfine28(vv.getCdfine28());
                                        vo2.setCdfine29(vv.getCdfine29());
                                        vo2.setCdfine30(vv.getCdfine30());
                                        newlist2.add(vo2);
                                    });

                                    newlist2.sort(Comparator.comparing(QjsyjzVo::getFuzhuName));
                                    vo.setChildren(newlist2);
                                }
                            }
                            newlist.add(vo);
                        });
                    }
                    // 2、查询状态是1级 汇总所有下级
                    else {
                        // 计算出一级科目
                        List<String> codeFirstStr=new ArrayList<>();
                        listobj2.stream().map(QjsyjzVo::getCcode).forEach(tx->{
                            codeFirstStr.add(tx.substring(0,Integer.valueOf(codeLevelFirst)));
                        });

                        codeFirstStr.stream().distinct().forEach(tx->{
                            // 查询一级科目名称
                            List<CodeKemu> ccodename = ccodelist.stream().filter(co -> co.getCcode().equals(tx)).collect(Collectors.toList());
                            List<QjsyjzVo> collect = listobj.stream().filter(v -> v.getCcode().startsWith(tx) && v.getParentcode().equals("-")).collect(Collectors.toList());
                            BigDecimal chamd =collect.stream().map(QjsyjzVo::getChamd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                            BigDecimal chamc =collect.stream().map(QjsyjzVo::getChamc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);

                            QjsyjzVo vo=new QjsyjzVo();
                            vo.setCcode(tx);
                            vo.setCcodeName(ccodename.size()>0?ccodename.get(0).getCcodeName():"");
                            vo.setBprogerty(ccodename.get(0).getBprogerty());
                            vo.setChamd(chamd);
                            vo.setChamc(chamc);
                            vo.setChildrenflag("0");
                            vo.setDigest(digest);
                            vo.setProfitCcode(map.get("liRunCode").toString());
                            vo.setProfitBprogerty(profitBprogerty);

                            List<QjsyjzVo> collect3 = listobj.stream().filter(v -> v.getCcode().startsWith(tx)).collect(Collectors.toList());
                            if(collect3.size()>0){
                                List<QjsyjzVo> collect1 = collect3.stream().filter(txt -> superCodeDistinct.contains(txt.getCcode()) && txt.getParentcode().equals("-")).collect(Collectors.toList());
                                for (int i = 0; i < collect1.size(); i++) {
                                    collect3.remove(collect1.get(i));
                                }
                                vo.setChildren2(collect3);
                            }
                            newlist.add(vo);
                        });
                    }

                    BigDecimal totalMd = newlist.stream().map(QjsyjzVo::getChamd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                    BigDecimal totalMc = newlist.stream().map(QjsyjzVo::getChamc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);

                    QjsyjzVo v2 = new QjsyjzVo();
                    v2.setDigest("合计");
                    v2.setChamd(totalMd);
                    v2.setChamc(totalMc);
                    v2.setChildrenflag("1");
                    newlist.add(v2);

                    return Mono.just(newlist.stream().filter(v->{
                       if(searchMap!=null){
                           // 按条件过滤
                           if (StringUtils.isNotBlank(searchMap.get("requirement")) && org.springbooz.core.tool.utils.StringUtils.isNotBlank(searchMap.get("value"))) {
                               String value = searchMap.get("value");
                               String dbValue = getFieldValueByFieldName(searchMap.get("requirement").trim(), v);
                               if (Objects.nonNull(dbValue) && !dbValue.contains(value)) {
                                   return false;
                               }
                           }
                       }
                       return true;
                   }));
                })
                .map(a -> R.ok().setResult(a));
    }

    // 获取结转数据
    private Flux<Map<String, Object>> getJieZhuanData(String iyear, String iyperiod, String sunYiCodeFirst) {
        String sql = "select vv.*,dep.dept_name,cus.cust_name,sup.sup_name,pro.project_name,psn.psn_name from(\n" +
                "   select '-' as parentcode,tempT.ccode,tempT.ccode_name,tempT.bprogerty,tempT.fzNums,null as cdept_id,null as cperson_id,null as ccus_id,null as csup_id,null as project_id,null as cdfine1,null as cdfine2,null as cdfine3,null as cdfine4,null as cdfine5,null as cdfine6,null as cdfine7,null as cdfine8,null as cdfine9,null as cdfine10,null as cdfine11,null as cdfine12,null as cdfine13,null as cdfine14,null as cdfine15,null as cdfine16,null as cdfine17,null as cdfine18,null as cdfine19,null as cdfine20,null as cdfine21,null as cdfine22,null as cdfine23,null as cdfine24,null as cdfine25,null as cdfine26,null as cdfine27,null as cdfine28,null as cdfine29,null as cdfine30,sum(tempT.md) as md,sum(tempT.mc) as mc,sum(tempT.cha) as cha,sum(tempT.chamd) as chamd,sum(tempT.chamc) as chamc,'0' as ordernum\n" +
                "   from (\n" +
                "            select (case when (cast(coalesce(c.bdept,'0') as int)+cast(coalesce(c.bperson,'0') as int)+cast(coalesce(c.bitem,'0') as int)+cast(coalesce(c.bcus,'0') as int)+cast(coalesce(c.bsup,'0') as int)+cast(coalesce(c.cdfine1,'0') as int)+cast(coalesce(c.cdfine2,'0') as int)+cast(coalesce(c.cdfine3,'0') as int)+cast(coalesce(c.cdfine4,'0') as int)+cast(coalesce(c.cdfine5,'0') as int)+cast(coalesce(c.cdfine6,'0') as int)+cast(coalesce(c.cdfine7,'0') as int)+cast(coalesce(c.cdfine8,'0') as int)+cast(coalesce(c.cdfine9,'0') as int)+cast(coalesce(c.cdfine10,'0') as int)+cast(coalesce(c.cdfine11,'0') as int)+cast(coalesce(c.cdfine12,'0') as int)+cast(coalesce(c.cdfine13,'0') as int)+cast(coalesce(c.cdfine14,'0') as int)+cast(coalesce(c.cdfine15,'0') as int)+cast(coalesce(c.cdfine16,'0') as int)+cast(coalesce(c.cdfine17,'0') as int)+cast(coalesce(c.cdfine18,'0') as int)+cast(coalesce(c.cdfine19,'0') as int)+cast(coalesce(c.cdfine20,'0') as int)+cast(coalesce(c.cdfine21,'0') as int)+cast(coalesce(c.cdfine22,'0') as int)+cast(coalesce(c.cdfine23,'0') as int)+cast(coalesce(c.cdfine24,'0') as int)+cast(coalesce(c.cdfine25,'0') as int)+cast(coalesce(c.cdfine26,'0') as int)+cast(coalesce(c.cdfine27,'0') as int)+cast(coalesce(c.cdfine28,'0') as int)+cast(coalesce(c.cdfine29,'0') as int)+cast(coalesce(c.cdfine30,'0') as int))=0 then '-' else c.ccode end) as parentcode,c.ccode,c.ccode_name,c.bprogerty,(cast(coalesce(c.bdept,'0') as int)+cast(coalesce(c.bperson,'0') as int)+cast(coalesce(c.bitem,'0') as int)+cast(coalesce(c.bcus,'0') as int)+cast(coalesce(c.bsup,'0') as int)+cast(coalesce(c.cdfine1,'0') as int)+cast(coalesce(c.cdfine2,'0') as int)+cast(coalesce(c.cdfine3,'0') as int)+cast(coalesce(c.cdfine4,'0') as int)+cast(coalesce(c.cdfine5,'0') as int)+cast(coalesce(c.cdfine6,'0') as int)+cast(coalesce(c.cdfine7,'0') as int)+cast(coalesce(c.cdfine8,'0') as int)+cast(coalesce(c.cdfine9,'0') as int)+cast(coalesce(c.cdfine10,'0') as int)+cast(coalesce(c.cdfine11,'0') as int)+cast(coalesce(c.cdfine12,'0') as int)+cast(coalesce(c.cdfine13,'0') as int)+cast(coalesce(c.cdfine14,'0') as int)+cast(coalesce(c.cdfine15,'0') as int)+cast(coalesce(c.cdfine16,'0') as int)+cast(coalesce(c.cdfine17,'0') as int)+cast(coalesce(c.cdfine18,'0') as int)+cast(coalesce(c.cdfine19,'0') as int)+cast(coalesce(c.cdfine20,'0') as int)+cast(coalesce(c.cdfine21,'0') as int)+cast(coalesce(c.cdfine22,'0') as int)+cast(coalesce(c.cdfine23,'0') as int)+cast(coalesce(c.cdfine24,'0') as int)+cast(coalesce(c.cdfine25,'0') as int)+cast(coalesce(c.cdfine26,'0') as int)+cast(coalesce(c.cdfine27,'0') as int)+cast(coalesce(c.cdfine28,'0') as int)+cast(coalesce(c.cdfine29,'0') as int)+cast(coalesce(c.cdfine30,'0') as int)) as fzNums,vou.cdept_id,vou.cperson_id,vou.ccus_id,vou.csup_id,vou.project_id,vou.cdfine1,vou.cdfine2,vou.cdfine3,vou.cdfine4,vou.cdfine5,vou.cdfine6,vou.cdfine7,vou.cdfine8,vou.cdfine9,vou.cdfine10,vou.cdfine11,vou.cdfine12,vou.cdfine13,vou.cdfine14,vou.cdfine15,vou.cdfine16,vou.cdfine17,vou.cdfine18,vou.cdfine19,vou.cdfine20,vou.cdfine21,vou.cdfine22,vou.cdfine23,vou.cdfine24,vou.cdfine25,vou.cdfine26,vou.cdfine27,vou.cdfine28,vou.cdfine29,vou.cdfine30,\n" +
                "                   sum(cast(coalesce(vou.md,'0') as decimal(18,4))) as md,\n" +
                "                   sum(cast(coalesce(vou.mc,'0') as decimal(18,4))) as mc,\n" +
                "                   (case when c.bprogerty='1' then (sum(cast(coalesce(vou.md,'0') as decimal(18,4)))-sum(cast(coalesce(vou.mc,'0') as decimal(18,4)))) else (sum(cast(coalesce(vou.mc,'0') as decimal(18,4)))-sum(cast(coalesce(vou.md,'0') as decimal(18,4)))) end) as cha,\n" +
                "                   (case when c.bprogerty='0' then (sum(cast(coalesce(vou.mc,'0') as decimal(18,4)))-sum(cast(coalesce(vou.md,'0') as decimal(18,4)))) else 0.0000 end) as chamd,\n" +
                "                   (case when c.bprogerty='1' then (sum(cast(coalesce(vou.md,'0') as decimal(18,4)))-sum(cast(coalesce(vou.mc,'0') as decimal(18,4)))) else 0.0000 end) as chamc,\n" +
                "                   '1' as ordernum\n" +
                "            from code_kemu c\n" +
                "                     left join (select * from accvoucher v where v.iyear='" + iyear + "' and v.iyperiod>='" + iyear + "01' and v.iyperiod<='" + iyperiod + "' and v.ifrag='0' union\n" +
                "                                select * from accvoucher vb where vb.iyear='" + iyear + "' and ((vb.iyperiod='" + iyear + "00' and vb.ccode in (select ccode from code_kemu where iyear='" + iyear + "' and (cast(coalesce(bdept,'0') as int)+cast(coalesce(bperson,'0') as int)+cast(coalesce(bitem,'0') as int)+cast(coalesce(bcus,'0') as int)+cast(coalesce(bsup,'0') as int)+cast(coalesce(cdfine1,'0') as int)+cast(coalesce(cdfine2,'0') as int)+cast(coalesce(cdfine3,'0') as int)+cast(coalesce(cdfine4,'0') as int)+cast(coalesce(cdfine5,'0') as int)+cast(coalesce(cdfine6,'0') as int)+cast(coalesce(cdfine7,'0') as int)+cast(coalesce(cdfine8,'0') as int)+cast(coalesce(cdfine9,'0') as int)+cast(coalesce(cdfine10,'0') as int)+cast(coalesce(cdfine11,'0') as int)+cast(coalesce(cdfine12,'0') as int)+cast(coalesce(cdfine13,'0') as int)+cast(coalesce(cdfine14,'0') as int)+cast(coalesce(cdfine15,'0') as int)+cast(coalesce(cdfine16,'0') as int)+cast(coalesce(cdfine17,'0') as int)+cast(coalesce(cdfine18,'0') as int)+cast(coalesce(cdfine19,'0') as int)+cast(coalesce(cdfine20,'0') as int)+cast(coalesce(cdfine21,'0') as int)+cast(coalesce(cdfine22,'0') as int)+cast(coalesce(cdfine23,'0') as int)+cast(coalesce(cdfine24,'0') as int)+cast(coalesce(cdfine25,'0') as int)+cast(coalesce(cdfine26,'0') as int)+cast(coalesce(cdfine27,'0') as int)+cast(coalesce(cdfine28,'0') as int)+cast(coalesce(cdfine29,'0') as int)+cast(coalesce(cdfine30,'0') as int))=0)) or vb.iyperiod='" + iyear + "21') and vb.ifrag='0'\n" +
                "            ) vou on vou.ccode=c.ccode\n" +
                "            where c.iyear='" + iyear + "' and c.flag='1' and c.is_del='0' and c.bend='1' and c.ccode like '" + sunYiCodeFirst + "%'\n" +
                "            group by c.ccode,c.ccode_name,c.bprogerty,cast(coalesce(c.bdept,'0') as int)+cast(coalesce(c.bperson,'0') as int)+cast(coalesce(c.bitem,'0') as int)+cast(coalesce(c.bcus,'0') as int)+cast(coalesce(c.bsup,'0') as int)+cast(coalesce(c.cdfine1,'0') as int)+cast(coalesce(c.cdfine2,'0') as int)+cast(coalesce(c.cdfine3,'0') as int)+cast(coalesce(c.cdfine4,'0') as int)+cast(coalesce(c.cdfine5,'0') as int)+cast(coalesce(c.cdfine6,'0') as int)+cast(coalesce(c.cdfine7,'0') as int)+cast(coalesce(c.cdfine8,'0') as int)+cast(coalesce(c.cdfine9,'0') as int)+cast(coalesce(c.cdfine10,'0') as int)+cast(coalesce(c.cdfine11,'0') as int)+cast(coalesce(c.cdfine12,'0') as int)+cast(coalesce(c.cdfine13,'0') as int)+cast(coalesce(c.cdfine14,'0') as int)+cast(coalesce(c.cdfine15,'0') as int)+cast(coalesce(c.cdfine16,'0') as int)+cast(coalesce(c.cdfine17,'0') as int)+cast(coalesce(c.cdfine18,'0') as int)+cast(coalesce(c.cdfine19,'0') as int)+cast(coalesce(c.cdfine20,'0') as int)+cast(coalesce(c.cdfine21,'0') as int)+cast(coalesce(c.cdfine22,'0') as int)+cast(coalesce(c.cdfine23,'0') as int)+cast(coalesce(c.cdfine24,'0') as int)+cast(coalesce(c.cdfine25,'0') as int)+cast(coalesce(c.cdfine26,'0') as int)+cast(coalesce(c.cdfine27,'0') as int)+cast(coalesce(c.cdfine28,'0') as int)+cast(coalesce(c.cdfine29,'0') as int)+cast(coalesce(c.cdfine30,'0') as int),vou.cdept_id,vou.cperson_id,vou.ccus_id,vou.csup_id,vou.project_id,vou.cdfine1,vou.cdfine2,vou.cdfine3,vou.cdfine4,vou.cdfine5,vou.cdfine6,vou.cdfine7,vou.cdfine8,vou.cdfine9,vou.cdfine10,vou.cdfine11,vou.cdfine12,vou.cdfine13,vou.cdfine14,vou.cdfine15,vou.cdfine16,vou.cdfine17,vou.cdfine18,vou.cdfine19,vou.cdfine20,vou.cdfine21,vou.cdfine22,vou.cdfine23,vou.cdfine24,vou.cdfine25,vou.cdfine26,vou.cdfine27,vou.cdfine28,vou.cdfine29,vou.cdfine30\n" +
                "            having sum(cast(coalesce(vou.md,'0') as decimal(18,4)))-sum(cast(coalesce(vou.mc,'0') as decimal(18,4)))!=0.0\n" +
                "        ) tempT\n" +
                "   where tempT.fzNums!='0'\n" +
                "   group by tempT.parentcode,tempT.ccode,tempT.ccode_name,tempT.bprogerty,tempT.fzNums,tempT.fzNums\n" +
                "   union\n" +
                "   select (case when (cast(coalesce(c.bdept,'0') as int)+cast(coalesce(c.bperson,'0') as int)+cast(coalesce(c.bitem,'0') as int)+cast(coalesce(c.bcus,'0') as int)+cast(coalesce(c.bsup,'0') as int)+cast(coalesce(c.cdfine1,'0') as int)+cast(coalesce(c.cdfine2,'0') as int)+cast(coalesce(c.cdfine3,'0') as int)+cast(coalesce(c.cdfine4,'0') as int)+cast(coalesce(c.cdfine5,'0') as int)+cast(coalesce(c.cdfine6,'0') as int)+cast(coalesce(c.cdfine7,'0') as int)+cast(coalesce(c.cdfine8,'0') as int)+cast(coalesce(c.cdfine9,'0') as int)+cast(coalesce(c.cdfine10,'0') as int)+cast(coalesce(c.cdfine11,'0') as int)+cast(coalesce(c.cdfine12,'0') as int)+cast(coalesce(c.cdfine13,'0') as int)+cast(coalesce(c.cdfine14,'0') as int)+cast(coalesce(c.cdfine15,'0') as int)+cast(coalesce(c.cdfine16,'0') as int)+cast(coalesce(c.cdfine17,'0') as int)+cast(coalesce(c.cdfine18,'0') as int)+cast(coalesce(c.cdfine19,'0') as int)+cast(coalesce(c.cdfine20,'0') as int)+cast(coalesce(c.cdfine21,'0') as int)+cast(coalesce(c.cdfine22,'0') as int)+cast(coalesce(c.cdfine23,'0') as int)+cast(coalesce(c.cdfine24,'0') as int)+cast(coalesce(c.cdfine25,'0') as int)+cast(coalesce(c.cdfine26,'0') as int)+cast(coalesce(c.cdfine27,'0') as int)+cast(coalesce(c.cdfine28,'0') as int)+cast(coalesce(c.cdfine29,'0') as int)+cast(coalesce(c.cdfine30,'0') as int))=0 then '-' else c.ccode end) as parentcode,c.ccode,c.ccode_name,c.bprogerty,(cast(coalesce(c.bdept,'0') as int)+cast(coalesce(c.bperson,'0') as int)+cast(coalesce(c.bitem,'0') as int)+cast(coalesce(c.bcus,'0') as int)+cast(coalesce(c.bsup,'0') as int)+cast(coalesce(c.cdfine1,'0') as int)+cast(coalesce(c.cdfine2,'0') as int)+cast(coalesce(c.cdfine3,'0') as int)+cast(coalesce(c.cdfine4,'0') as int)+cast(coalesce(c.cdfine5,'0') as int)+cast(coalesce(c.cdfine6,'0') as int)+cast(coalesce(c.cdfine7,'0') as int)+cast(coalesce(c.cdfine8,'0') as int)+cast(coalesce(c.cdfine9,'0') as int)+cast(coalesce(c.cdfine10,'0') as int)+cast(coalesce(c.cdfine11,'0') as int)+cast(coalesce(c.cdfine12,'0') as int)+cast(coalesce(c.cdfine13,'0') as int)+cast(coalesce(c.cdfine14,'0') as int)+cast(coalesce(c.cdfine15,'0') as int)+cast(coalesce(c.cdfine16,'0') as int)+cast(coalesce(c.cdfine17,'0') as int)+cast(coalesce(c.cdfine18,'0') as int)+cast(coalesce(c.cdfine19,'0') as int)+cast(coalesce(c.cdfine20,'0') as int)+cast(coalesce(c.cdfine21,'0') as int)+cast(coalesce(c.cdfine22,'0') as int)+cast(coalesce(c.cdfine23,'0') as int)+cast(coalesce(c.cdfine24,'0') as int)+cast(coalesce(c.cdfine25,'0') as int)+cast(coalesce(c.cdfine26,'0') as int)+cast(coalesce(c.cdfine27,'0') as int)+cast(coalesce(c.cdfine28,'0') as int)+cast(coalesce(c.cdfine29,'0') as int)+cast(coalesce(c.cdfine30,'0') as int)) as fzNums,vou.cdept_id,vou.cperson_id,vou.ccus_id,vou.csup_id,vou.project_id,vou.cdfine1,vou.cdfine2,vou.cdfine3,vou.cdfine4,vou.cdfine5,vou.cdfine6,vou.cdfine7,vou.cdfine8,vou.cdfine9,vou.cdfine10,vou.cdfine11,vou.cdfine12,vou.cdfine13,vou.cdfine14,vou.cdfine15,vou.cdfine16,vou.cdfine17,vou.cdfine18,vou.cdfine19,vou.cdfine20,vou.cdfine21,vou.cdfine22,vou.cdfine23,vou.cdfine24,vou.cdfine25,vou.cdfine26,vou.cdfine27,vou.cdfine28,vou.cdfine29,vou.cdfine30,\n" +
                "          sum(cast(coalesce(vou.md,'0') as decimal(18,4))) as md,\n" +
                "          sum(cast(coalesce(vou.mc,'0') as decimal(18,4))) as mc,\n" +
                "          (case when c.bprogerty='1' then (sum(cast(coalesce(vou.md,'0') as decimal(18,4)))-sum(cast(coalesce(vou.mc,'0') as decimal(18,4)))) else (sum(cast(coalesce(vou.mc,'0') as decimal(18,4)))-sum(cast(coalesce(vou.md,'0') as decimal(18,4)))) end) as cha,\n" +
                "          (case when c.bprogerty='0' then (sum(cast(coalesce(vou.mc,'0') as decimal(18,4)))-sum(cast(coalesce(vou.md,'0') as decimal(18,4)))) else 0.0000 end) as chamd,\n" +
                "          (case when c.bprogerty='1' then (sum(cast(coalesce(vou.md,'0') as decimal(18,4)))-sum(cast(coalesce(vou.mc,'0') as decimal(18,4)))) else 0.0000 end) as chamc,\n" +
                "          '1' as ordernum\n" +
                "   from code_kemu c\n" +
                "            left join (select * from accvoucher v where v.iyear='" + iyear + "' and v.iyperiod>='" + iyear + "01' and v.iyperiod<='" + iyperiod + "' and v.ifrag='0' union\n" +
                "                       select * from accvoucher vb where vb.iyear='" + iyear + "' and ((vb.iyperiod='" + iyear + "00' and vb.ccode in (select ccode from code_kemu where iyear='" + iyear + "' and (cast(coalesce(bdept,'0') as int)+cast(coalesce(bperson,'0') as int)+cast(coalesce(bitem,'0') as int)+cast(coalesce(bcus,'0') as int)+cast(coalesce(bsup,'0') as int)+cast(coalesce(cdfine1,'0') as int)+cast(coalesce(cdfine2,'0') as int)+cast(coalesce(cdfine3,'0') as int)+cast(coalesce(cdfine4,'0') as int)+cast(coalesce(cdfine5,'0') as int)+cast(coalesce(cdfine6,'0') as int)+cast(coalesce(cdfine7,'0') as int)+cast(coalesce(cdfine8,'0') as int)+cast(coalesce(cdfine9,'0') as int)+cast(coalesce(cdfine10,'0') as int)+cast(coalesce(cdfine11,'0') as int)+cast(coalesce(cdfine12,'0') as int)+cast(coalesce(cdfine13,'0') as int)+cast(coalesce(cdfine14,'0') as int)+cast(coalesce(cdfine15,'0') as int)+cast(coalesce(cdfine16,'0') as int)+cast(coalesce(cdfine17,'0') as int)+cast(coalesce(cdfine18,'0') as int)+cast(coalesce(cdfine19,'0') as int)+cast(coalesce(cdfine20,'0') as int)+cast(coalesce(cdfine21,'0') as int)+cast(coalesce(cdfine22,'0') as int)+cast(coalesce(cdfine23,'0') as int)+cast(coalesce(cdfine24,'0') as int)+cast(coalesce(cdfine25,'0') as int)+cast(coalesce(cdfine26,'0') as int)+cast(coalesce(cdfine27,'0') as int)+cast(coalesce(cdfine28,'0') as int)+cast(coalesce(cdfine29,'0') as int)+cast(coalesce(cdfine30,'0') as int))=0)) or vb.iyperiod='" + iyear + "21') and vb.ifrag='0'\n" +
                "   ) vou on vou.ccode=c.ccode\n" +
                "   where c.iyear='" + iyear + "' and c.flag='1' and c.is_del='0' and c.bend='1' and c.ccode like '" + sunYiCodeFirst + "%'\n" +
                "   group by c.ccode,c.ccode_name,c.bprogerty,cast(coalesce(c.bdept,'0') as int)+cast(coalesce(c.bperson,'0') as int)+cast(coalesce(c.bitem,'0') as int)+cast(coalesce(c.bcus,'0') as int)+cast(coalesce(c.bsup,'0') as int)+cast(coalesce(c.cdfine1,'0') as int)+cast(coalesce(c.cdfine2,'0') as int)+cast(coalesce(c.cdfine3,'0') as int)+cast(coalesce(c.cdfine4,'0') as int)+cast(coalesce(c.cdfine5,'0') as int)+cast(coalesce(c.cdfine6,'0') as int)+cast(coalesce(c.cdfine7,'0') as int)+cast(coalesce(c.cdfine8,'0') as int)+cast(coalesce(c.cdfine9,'0') as int)+cast(coalesce(c.cdfine10,'0') as int)+cast(coalesce(c.cdfine11,'0') as int)+cast(coalesce(c.cdfine12,'0') as int)+cast(coalesce(c.cdfine13,'0') as int)+cast(coalesce(c.cdfine14,'0') as int)+cast(coalesce(c.cdfine15,'0') as int)+cast(coalesce(c.cdfine16,'0') as int)+cast(coalesce(c.cdfine17,'0') as int)+cast(coalesce(c.cdfine18,'0') as int)+cast(coalesce(c.cdfine19,'0') as int)+cast(coalesce(c.cdfine20,'0') as int)+cast(coalesce(c.cdfine21,'0') as int)+cast(coalesce(c.cdfine22,'0') as int)+cast(coalesce(c.cdfine23,'0') as int)+cast(coalesce(c.cdfine24,'0') as int)+cast(coalesce(c.cdfine25,'0') as int)+cast(coalesce(c.cdfine26,'0') as int)+cast(coalesce(c.cdfine27,'0') as int)+cast(coalesce(c.cdfine28,'0') as int)+cast(coalesce(c.cdfine29,'0') as int)+cast(coalesce(c.cdfine30,'0') as int),vou.cdept_id,vou.cperson_id,vou.ccus_id,vou.csup_id,vou.project_id,vou.cdfine1,vou.cdfine2,vou.cdfine3,vou.cdfine4,vou.cdfine5,vou.cdfine6,vou.cdfine7,vou.cdfine8,vou.cdfine9,vou.cdfine10,vou.cdfine11,vou.cdfine12,vou.cdfine13,vou.cdfine14,vou.cdfine15,vou.cdfine16,vou.cdfine17,vou.cdfine18,vou.cdfine19,vou.cdfine20,vou.cdfine21,vou.cdfine22,vou.cdfine23,vou.cdfine24,vou.cdfine25,vou.cdfine26,vou.cdfine27,vou.cdfine28,vou.cdfine29,vou.cdfine30\n" +
                "   having sum(cast(coalesce(vou.md,'0') as decimal(18,4)))-sum(cast(coalesce(vou.mc,'0') as decimal(18,4)))!=0.0\n" +
                ") vv\n" +
                "   left join sys_department dep on dep.flag='1' and dep.unique_code=vv.cdept_id\n" +
                "   left join customer cus on cus.flag='1' and cus.is_del='0' and cus.unique_code=vv.ccus_id\n" +
                "   left join supplier sup on sup.flag='1' and sup.is_del='0' and sup.unique_code=vv.csup_id\n" +
                "   left join project pro on pro.jiesuan='0' and pro.is_del='0' and pro.unique_code=vv.project_id\n" +
                "   left join sys_psn psn on psn.flag='1' and psn.is_del='0' and psn.unique_code=vv.cperson_id\n" +
                "order by vv.ccode,vv.ordernum;";
        return client.sql(sql).fetch().all();
    }
    private String getFieldValueByFieldName(String fieldName, Object object) {
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + firstLetter + fieldName.substring(1);
            Method method = object.getClass().getMethod(getter);
            Object value = method.invoke(object);
            return value.toString();
        } catch (Exception e) {
            return null;
        }
    }

    @PostMapping("findByCcode")
    public Mono<R> findByCcode(@RequestBody Map map, Pageable pageable) {
        String dynamicTenantId = map.get("dynamicTenantId").toString();
        String iyear = map.get("period").toString().split("-")[0];
        String imonth = map.get("period").toString().split("-")[1];
        String ishaveRjz = map.get("ishaveRjz").toString();
        String voucherType = map.get("voucherType").toString();
        List<String> style = Arrays.asList(map.get("styleList").toString().split("-"));
        List<AccStyle> styleList = new ArrayList<>();
        for (int i = 0; i < style.size(); i++) {
            AccStyle sty = new AccStyle();
            sty.setCclass(style.get(i));
            styleList.add(sty);
        }

        List<AccCarryOverEntry> carryList = JSON.parseArray(map.get("carryOverEntries").toString(), AccCarryOverEntry.class);
        // 转出科目
        List<AccCarryOverEntry> zhuan_chu = carryList.stream().filter(v -> v.getTransferMethod().equals("2")).collect(Collectors.toList());
        // 转入科目
        List<AccCarryOverEntry> zhuan_ru = carryList.stream().filter(v -> v.getTransferMethod().equals("1")).collect(Collectors.toList());
        List<AccCarryOverEntryVo> newlist = new ArrayList<AccCarryOverEntryVo>();

        Map<String, Object> maplist = new HashMap<>();
        // 科目
        return codeKemuRepository.companyfindAll(iyear, pageable).collectList()
                .flatMap(codelist -> {
                    maplist.put("codelist", codelist);
                    // 余额
                    return keMuBalanceService.findAll(null, null, pageable, "1", iyear + "01"
                                    , iyear + imonth, "9", "1", "", "", "qj",
                                    ishaveRjz, "全部", "全部", null, null, styleList, dynamicTenantId, "gs", voucherType,new ArrayList<>())
                            .flatMap(yueitem -> {
                                List<KeMuBalanceVo> yuelist = new ArrayList<>();
                                String yuejson = JSON.toJSONString(yueitem.getResult());
                                JSONObject json = JSONObject.parseObject(yuejson);
                                //获取item，得到json数组
                                JSONArray array = json.getJSONArray("items");
                                for (int i = 0; i < array.size(); i++) {
                                    JSONObject jo = array.getJSONObject(i);
                                    if (jo.getString("ccode").indexOf("小计") < 0 && jo.getString("ccode").indexOf("合计") < 0) {
                                        yuelist.add(
                                                new KeMuBalanceVo()
                                                        .setCcode(jo.getString("ccode"))
                                                        .setCcodeName(jo.getString("ccodeName"))
                                                        .setQcMd(jo.getBigDecimal("qcMd"))
                                                        .setQcMc(jo.getBigDecimal("qcMc"))
                                                        .setQmMd(jo.getBigDecimal("qmMd"))
                                                        .setQmMc(jo.getBigDecimal("qmMc"))
                                                        .setQmNum(jo.getBigDecimal("qmNum"))
                                                        .setQmNfrat(jo.getBigDecimal("qmNfrat"))
                                                        .setFindByBend(jo.getString("findByBend"))
                                                        .setUnitMeasurement(jo.getString("unitMeasurement"))
                                                        .setForeignCurrency(jo.getString("foreignCurrency"))
                                        );
                                    }
                                }
                                maplist.put("yuelist", yuelist);
                                // 辅助余额
                                return accvoucherRepository.findAllSubjectInitialFuZhuBalance(iyear, iyear + "21").collectList()
                                        .flatMap(fuzhuyuelist -> {
                                            maplist.put("fuzhuyuelist", fuzhuyuelist);
                                            return accvoucherRepository.findByThisMonthCodeMdMc(iyear + imonth).collectList()
                                                    .flatMap(thisMonth -> {
                                                        maplist.put("thisMonth", thisMonth);
                                                        return accvoucherRepository.findByBeforeMonthCodeMdMc(iyear + "01", iyear + imonth).collectList()
                                                                .flatMap(beforeMonth -> {
                                                                    maplist.put("beforeMonth", beforeMonth);
                                                                    return Mono.just(maplist);
                                                                });
                                                    });
                                        });
                            });
                })
                .flatMap(mapitem -> {
                    List<KeMuBalanceVo> yuelist = (List<KeMuBalanceVo>) mapitem.get("yuelist");
                    List<CodeKemu> codelist = (List<CodeKemu>) mapitem.get("codelist");
                    List<SubjectInitialBalanceVo> fuzhuyuelist = (List<SubjectInitialBalanceVo>) mapitem.get("fuzhuyuelist");
                    List<AccvoucherVo> thisMonth = (List<AccvoucherVo>) mapitem.get("thisMonth");
                    List<AccvoucherVo> beforeMonth = (List<AccvoucherVo>) mapitem.get("beforeMonth");
                    zhuan_chu.forEach(zc -> {
                        // 余额
                        List<KeMuBalanceVo> collect = yuelist.stream().filter(v -> v.getCcode().equals(zc.getAccountSubjectNum())).collect(Collectors.toList());
                        BigDecimal md = collect.size() > 0 ? collect.get(0).getQmMd() : new BigDecimal(0);
                        BigDecimal mc = collect.size() > 0 ? collect.get(0).getQmMc() : new BigDecimal(0);

                        // 当月发生
                        List<AccvoucherVo> collect2 = thisMonth.stream().filter(t -> t.getCcode().equals(zc.getAccountSubjectNum())).collect(Collectors.toList());
                        BigDecimal pz_md = collect2.size() > 0 ? collect2.stream().map(AccvoucherVo::getMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum) : new BigDecimal(0);
                        BigDecimal pz_mc = collect2.size() > 0 ? collect2.stream().map(AccvoucherVo::getMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum) : new BigDecimal(0);
                        // 之前月发生
                        List<AccvoucherVo> collect3 = beforeMonth.stream().filter(t -> t.getCcode().equals(zc.getAccountSubjectNum())).collect(Collectors.toList());
                        BigDecimal before_pz_md = collect3.size() > 0 ? collect3.stream().map(AccvoucherVo::getMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum) : new BigDecimal(0);
                        BigDecimal before_pz_mc = collect3.size() > 0 ? collect3.stream().map(AccvoucherVo::getMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum) : new BigDecimal(0);

                        List<CodeKemu> collect1 = codelist.stream().filter(c -> c.getCcode().equals(zc.getAccountSubjectNum())).collect(Collectors.toList());
                        AccCarryOverEntryVo vo = new AccCarryOverEntryVo();
                        vo.setId(zc.getId())
                                .setOnlyNum(zc.getOnlyNum())
                                .setOwnership(zc.getOwnership())
                                .setSummary(zc.getSummary())
                                .setAccountSubjectNum(zc.getAccountSubjectNum())
                                .setAccountSubjectName(collect1.size() > 0 ? collect1.get(0).getCcodeName() : zc.getAccountSubjectName())
                                .setAuxiliaryAccountingNum(zc.getAuxiliaryAccountingNum())
                                .setAuxiliaryAccountingName(zc.getAuxiliaryAccountingName())
                                .setTransferMethod(zc.getTransferMethod())
                                .setDirection(zc.getDirection())
                                .setAmountFormula(zc.getAmountFormula())
                                .setPercentage(zc.getPercentage())
                                .setEntryFormulas(zc.getEntryFormulas());
                        if (zc.getDirection().equals("0")) {          // 余额
                            vo.setMd(md).setMc(mc);
                        } else if (zc.getDirection().equals("1")) {  // 借方余额
                            vo.setMd(md).setMc(new BigDecimal(0));
                        } else if (zc.getDirection().equals("2")) {  // 贷方余额
                            vo.setMd(new BigDecimal(0)).setMc(mc);
                        } else if (zc.getDirection().equals("3")) {  // 借方本期
                            vo.setMd(pz_md).setMc(new BigDecimal(0));
                        } else if (zc.getDirection().equals("4")) {  // 贷方本期
                            vo.setMd(new BigDecimal(0)).setMc(pz_mc);
                        } else if (zc.getDirection().equals("5")) {  // 借方累计
                            vo.setMd(before_pz_md).setMc(new BigDecimal(0));
                        } else if (zc.getDirection().equals("6")) {  // 贷方累计
                            vo.setMd(new BigDecimal(0)).setMc(before_pz_mc);
                        }

                        // 查找下级科目余额明细：前缀包含本科目的末级科目，
                        List<KeMuBalanceVo> yuemx = yuelist.stream().filter(y -> y.getCcode().startsWith(zc.getAccountSubjectNum()) && y.getFindByBend().equals("1")).collect(Collectors.toList());
                        List<SubjectInitialBalanceVo> fuzhulist = fuzhuyuelist.stream().filter(y -> y.getCcode().startsWith(zc.getAccountSubjectNum()) && y.getBend().equals("1")).collect(Collectors.toList());
                        vo.setYuemx(yuemx);
                        vo.setFuzhumx(fuzhulist);
                        newlist.add(vo);
                    });
                    zhuan_ru.forEach(zc -> {
                        List<CodeKemu> collect1 = codelist.stream().filter(c -> c.getCcode().equals(zc.getAccountSubjectNum())).collect(Collectors.toList());
                        AccCarryOverEntryVo vo = new AccCarryOverEntryVo();
                        vo.setId(zc.getId())
                                .setOnlyNum(zc.getOnlyNum())
                                .setOwnership(zc.getOwnership())
                                .setSummary(zc.getSummary())
                                .setAccountSubjectNum(zc.getAccountSubjectNum())
                                .setAccountSubjectName(collect1.size() > 0 ? collect1.get(0).getCcodeName() : zc.getAccountSubjectName())
                                .setAuxiliaryAccountingNum(zc.getAuxiliaryAccountingNum())
                                .setAuxiliaryAccountingName(zc.getAuxiliaryAccountingName())
                                .setTransferMethod(zc.getTransferMethod())
                                .setDirection(zc.getDirection())
                                .setAmountFormula(zc.getAmountFormula())
                                .setPercentage(zc.getPercentage())
                                .setEntryFormulas(zc.getEntryFormulas())
                                .setMd(new BigDecimal(0))
                                .setMc(new BigDecimal(0));
                        newlist.add(vo);
                    });
                    return Mono.just(newlist);
                })
                .map(o -> R.ok().setResult(o));
    }

    /**
     * 生成凭证
     *
     * @return
     */
    @PostMapping("setPingZhengSave")
    public Mono<R> setPingZhengSave(@RequestBody List<Accvoucher> list) {
        return accvoucherRepository.saveAll(list).collectList().map(a -> R.ok().setResult(a));
    }


    @PostMapping("getLastDayOfMonth")
    public Mono<R> getLastDayOfMonth(String iyearAndMonth) {
        return Mono.just(R.ok().setResult(NewDateUtil.getLastDayOfMonth(iyearAndMonth)));
    }


    /**
     * @description: 新的------期间损益结转
     * @author: miao
     * @date: 2022/11/17 14:28
     * @param:
     * @return: null
     **/
    @PostMapping("newQCSYJZ")
    public Mono<R> newQCSYJZ(@RequestBody Map map){
        String ddate=map.get("ddate").toString();
        String kmLevel=map.get("kmLevel").toString();
        String dataType=map.get("dataType").toString();
        String lirunKm=map.get("lirunKm").toString();
        String jz=map.get("jz").toString();
        String sunYiCodeFirst=map.get("sunYiCodeFirst").toString();
        List<CodeKemu> sykmAll= (List<CodeKemu>) map.get("sykmAll");

        // 获取结转数据
        Mono<List<QjsyjzVo>> listMono = getJieZhuanData(ddate.split("-")[0], ddate.replaceAll("-", ""), sunYiCodeFirst).collectList()
                .flatMapMany(skList -> Flux.fromIterable(JSON.parseArray(JSON.toJSONString(skList), QjsyjzVo.class))).collectList();
        Mono<List<FuzhuHesuan>> fzhslist = fuzhuHesuanRepository.findAll().collectList();
        Mono<List<CustomerClass>> cusClassList = customerClassRepository.findAll().collectList();
        Mono<List<SupplierClass>> supClassList = supplierClassRepository.findAll().collectList();
        Mono<List<SysPsnType>> psnTypeList = sysPsnTypeRepository.findAll().collectList();
        Mono<List<ProjectClass>> proClassList = projectClassRepository.findAll().collectList();

        return Mono.zip(listMono,fzhslist,cusClassList,supClassList,psnTypeList,proClassList).flatMap(t->{
            List<QjsyjzVo> a = t.getT1();

            return Mono.just("");
        }).map(R::ok);
    }
}

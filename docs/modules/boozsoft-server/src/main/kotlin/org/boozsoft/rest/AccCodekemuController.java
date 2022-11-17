package org.boozsoft.rest;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.boozsoft.domain.entity.*;
import org.boozsoft.domain.entity.account.FuzhuHesuan;
import org.boozsoft.domain.entity.accstyle.AccStyle;
import org.boozsoft.domain.entity.codekemu.CodeKemu;
import org.boozsoft.domain.entity.group.GroupCodeKemu;
import org.boozsoft.domain.entity.group.GroupCodeKemuCountry;
import org.boozsoft.domain.entity.group.GroupCodeKemuOrg;
import org.boozsoft.domain.vo.AAtemp;
import org.boozsoft.domain.vo.AccvoucherVo;
import org.boozsoft.domain.vo.AccvoucherVo3;
import org.boozsoft.domain.vo.CarryDownCodeKemuVo;
import org.boozsoft.repo.*;
import org.boozsoft.repo.accstyle.AccStyleRepository;
import org.boozsoft.repo.acctemplate.AccTemplateRepository;
import org.boozsoft.repo.codekemu.CodeKemuRepository;
import org.boozsoft.repo.codekemu.GroupCodeKemuRepository;
import org.boozsoft.repo.group.GroupCodeImportTemplateRepository;
import org.boozsoft.repo.group.GroupCodeKemuCountryRepository;
import org.boozsoft.repo.group.GroupCodeKemuOrgRepository;
import org.boozsoft.repo.standard.AccStandardRepository;
import org.boozsoft.service.AccvoucherService;
import org.boozsoft.service.KeMuBalanceService;
import org.boozsoft.service.SysCurrencyService;
import org.boozsoft.service.SysUnitMeaService;
import org.boozsoft.util.BigDecimalUtils;
import org.boozsoft.util.JsonListUtil;
import org.boozsoft.util.RandomNum;
import org.boozsoft.util.XlsUtils3;
import org.boozsoft.utils.CollectOfUtils;
import org.springbooz.core.tool.result.R;
import org.springbooz.core.tool.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @ClassName : AccTemplateController
 * @Description : 科目
 * @Author : miao
 * @Date: 2021-05-12 10:40
 */

@Slf4j
@RestController
@RequestMapping("/sys/acckemu")
@Api(value = "科目", tags = "API系统：科目")
public class AccCodekemuController {
    @Autowired
    GroupCodeKemuCountryRepository groupCodeKemuCountryRepository;
    @Autowired
    CodeKemuRepository codeKemuRepository;
    @Autowired
    GroupCodeKemuRepository groupCodeKemuRepository;
    @Autowired
    GroupCodeKemuOrgRepository groupCodeKemuOrgRepository;
    @Autowired
    SysUnitOfMeaRepository sysUnitOfMeaRepository;
    @Autowired
    UsedForeignCurrencyRepository usedForeignCurrencyRepository;
    @Autowired
    SysLogRepository logRepository;
    @Autowired
    AccTemplateRepository accTemplateRepository;
    @Autowired
    AccStyleRepository accStyleRepository;
    @Autowired
    SysUnitMeaService sysUnitMeaService;
    @Autowired
    SysCurrencyService sysCurrencyService;
    @Autowired
    AccvoucherRepository accvoucherRepository;
    @Autowired
    TaskRepository taskRepository;
    @Autowired
    AccStandardRepository standardRepository;
    @Autowired
    FuzhuHesuanRepository fuzhuHesuanRepository;
    @Autowired
    ProjectCashCodeRepository projectCashCodeRepository;
    @Autowired
    KmCashFlowRepository kmCashFlowRepository;
    @Autowired
    CurrencyRepository currencyRepository;
    @Autowired
    AccvoucherService accvoucherService;
    @Autowired
    GroupCodeImportTemplateRepository groupCodeImportTemplateRepository;
    @Autowired
    DatabaseClient client;
    @Autowired
    KeMuBalanceService keMuBalanceService;
    @Autowired
    AuditCodeKemuRepository auditCodeKemuRepository;
    @Autowired
    AccvoucherSettingRepository accvoucherSettingRepository;

    @PostMapping("copyCode")
    public Mono<R> copyCode(String uniqueAccStandard, String templateId, String newtemplateid) {
        return groupCodeKemuRepository.findAllByUniqueAccStandardAndTemplateIdOrderByCcodeAsc(uniqueAccStandard, templateId).collectList()
                .flatMapMany(t -> {
                    List<GroupCodeKemu> list = new ArrayList<>();
                    t.stream().forEach(c -> {
                        c.setId(null);
                        c.setTemplateId(newtemplateid);
                        list.add(c);
                    });
                    return groupCodeKemuRepository.saveAll(list);
                })
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    /**
     * 组织复制科目
     *
     * @param uniqueAccStandard
     * @param templateId
     * @param lastcode          目标科目
     * @param nolastcode        来源科目
     * @return
     */
    @PostMapping("GroupCopyCode")
    public Mono GroupCopyCode(String uniqueAccStandard, String templateId, String lastcode, String nolastcode) {
        // 获取目标科目信息；方便修改末级标识
        return groupCodeKemuRepository.findByUniqueAccStandardAndTemplateIdAndCcode(uniqueAccStandard, templateId, lastcode)
                .flatMap(code -> {
                    // 查询来源科目所有下级科目
                    return groupCodeKemuRepository.findByUniqueAccStandardAndTemplateIdLikeCode(uniqueAccStandard, templateId, nolastcode + "_%")
                            .collectList()
                            .map(list -> {
                                list.stream().forEach(v -> {
                                    String num = v.getCcode().substring(nolastcode.length());
                                    v.setId(null).setCcode(lastcode + num).setUniqueCode(lastcode).setSuperiorCcode(lastcode);
                                });

                                return CollectOfUtils.mapof("code", code, "list", list);
                            });
                })
                .flatMapMany(map -> {
                    List<GroupCodeKemu> codelist = (List<GroupCodeKemu>) map.get("list");
                    GroupCodeKemu code = (GroupCodeKemu) map.get("code");
                    code.setBend("0");
                    return groupCodeKemuRepository.save(code).zipWith(groupCodeKemuRepository.saveAll(codelist).collectList());
                })
                .collectList()
                .map(a -> R.ok().setResult(a));
    }


    @PostMapping("findByCodeNum")
    public Mono<R> findByCodeNum(String uniqueAccStandard, String templateId, String ccode) {
        return groupCodeKemuRepository.countByUniqueAccStandardAndTemplateIdAndCcode(uniqueAccStandard, templateId, ccode).map(o -> R.ok().setResult(o));
    }

    @PostMapping("findByOrgCodeNum")
    public Mono<R> findByOrgCodeNum(String orgUnique, String iyear, String ccode) {
        return groupCodeKemuOrgRepository.findAllByCcodeAndOrgUniqueAndIyear(ccode, orgUnique, iyear).map(o -> R.ok().setResult(o)).defaultIfEmpty(R.ok().setResult(""));
    }

    @PostMapping("findByOrgCodeName")
    public Mono<R> findByOrgCodeName(String orgUnique, String iyear, String ccodName) {
        return groupCodeKemuOrgRepository.findAllByCcodeNameAndOrgUniqueAndIyear(ccodName, orgUnique, iyear).map(o -> R.ok().setResult(o)).defaultIfEmpty(R.ok().setResult(""));
    }

    @PostMapping("findByCodeName")
    public Mono<R> findByCodeName(String uniqueAccStandard, String templateId, String ccodename) {
        return groupCodeKemuRepository.countByUniqueAccStandardAndTemplateIdAndCcodeName(uniqueAccStandard, templateId, ccodename).map(o -> R.ok().setResult(o));
    }

    /**
     * 下级科目名称是否重复
     *
     * @param uniqueAccStandard
     * @param templateId
     * @param ccodename
     * @param superCcod
     * @return
     */
    @PostMapping("findByCodeName2")
    public Mono<R> findByCodeName2(String uniqueAccStandard, String templateId, String ccodename, String superCcod) {
        return groupCodeKemuRepository.countByUniqueAccStandardAndTemplateIdAndCcodeNameAndSuperCode(uniqueAccStandard, templateId, ccodename, superCcod + '%').map(o -> R.ok().setResult(o));
    }

    /**
     * 计算单位
     *
     * @return
     */
    @GetMapping("findAllByUnitMea")
    public Mono<R> findAllByUnitMea() {
        return sysUnitOfMeaRepository.findAll().collectList().map(o -> R.ok().setResult(o));
    }

    /**
     * 货币
     *
     * @return
     */
    @GetMapping("findAllByCurrency")
    public Mono<R> findAllByCurrency() {
        return usedForeignCurrencyRepository.findAll().collectList().map(o -> R.ok().setResult(o));
    }

    /**
     * 集团币种信息表
     *
     * @return
     */
    @GetMapping("findAllByGorupCurrency")
    public Mono<R> findAllByGorupCurrency() {
        return currencyRepository.findAllByOrderByNumAsc().collectList().map(o -> R.ok().setResult(o));
    }

    /**
     * 集团科目表增加
     *
     * @param km
     * @return
     */
    @Transactional
    @PostMapping("saveCodekemu")
    public Mono saveCodekemu(@RequestBody GroupCodeKemu km, String username) {
        Calendar date = Calendar.getInstance();
        String year = String.valueOf(date.get(Calendar.YEAR));
        km
                .setUniqueCode(RandomNum.uuid())
                .setCbookType("金额式")
                .setSuperiorCcode(StringUtils.isBlank(km.getSuperiorCcode()) ? "0" : km.getSuperiorCcode())
                .setBnum(StringUtils.isNotBlank(km.getBnum()) ? km.getBnum() : "0")
                .setCurrency(StringUtils.isNotBlank(km.getCurrency()) ? km.getCurrency() : "0")
                .setBcash(StringUtils.isNotBlank(km.getBcash()) ? km.getBcash() : "0")
                .setBbank(StringUtils.isNotBlank(km.getBbank()) ? km.getBbank() : "0")
                .setBcashEquivalence(StringUtils.isNotBlank(km.getBcashEquivalence()) ? km.getBcashEquivalence() : "0")
                .setBdaybook(StringUtils.isNotBlank(km.getBdaybook()) ? km.getBdaybook() : "0")
                .setFlag("1")
                .setIyear(year).setBend("1")
                .setBnum(km.getBnum())
                .setBstock("0")
                .setCassItem("0")
                .setLowerControl(km.getLowerControl())
                .setFuzhuControl(km.getFuzhuControl())
                .setControlled(km.getControlled())
                .setPxjz(km.getPxjz())
                .setXjll(km.getXjll())
                .setCyfx(km.getCyfx())
                .setCdfine1("0")
                .setCdfine2("0")
                .setCdfine3("0")
                .setCdfine4("0")
                .setCdfine5("0")
                .setCdfine6("0")
                .setCdfine7("0")
                .setCdfine8("0")
                .setCdfine9("0")
                .setCdfine10("0")
                .setCdfine11("0")
                .setCdfine12("0")
                .setCdfine13("0")
                .setCdfine14("0")
                .setCdfine15("0")
                .setCdfine16("0")
                .setCdfine17("0")
                .setCdfine18("0")
                .setCdfine19("0")
                .setCdfine20("0")
                .setCdfine21("0")
                .setCdfine22("0")
                .setCdfine23("0")
                .setCdfine24("0")
                .setCdfine25("0")
                .setCdfine26("0")
                .setCdfine27("0")
                .setCdfine28("0")
                .setCdfine29("0")
                .setCdfine30("0");

        if (StringUtils.isNotBlank(km.getFuzhu())) {
            for (int i = 0; i < km.getFuzhu().split(",").length; i++) {
                if (km.getFuzhu().split(",")[i].equals("999")) {                    // 个人
                    km.setBperson("1");
                } else if (km.getFuzhu().split(",")[i].equals("998")) {             // 部门
                    km.setBdept("1");
                } else if (km.getFuzhu().split(",")[i].equals("997")) {            // '项目'
                    km.setBitem("1");
                } else if (km.getFuzhu().split(",")[i].equals("996")) {             // '客户'
                    km.setBcus("1");
                } else if (km.getFuzhu().split(",")[i].equals("995")) {             // '供应商'
                    km.setBsup("1");
                } else if (km.getFuzhu().split(",")[i].equals("994")) {             // '支出功能分类'
                    km.setYsZcgnfl("1");
                } else if (km.getFuzhu().split(",")[i].equals("993")) {             // '部门支出经济分类'
                    km.setYsBmzcjjfl("1");
                } else if (km.getFuzhu().split(",")[i].equals("992")) {             // '政府支出经济分类'
                    km.setYsZfzcjjfl("1");
                } else if (km.getFuzhu().split(",")[i].equals("991")) {             // '预算来源'
                    km.setYsYsly("1");
                }
            }
        } else {
            km.setBperson("0").setBdept("0").setBitem("0").setBcus("0").setBsup("0");
        }

        Mono yiji = Mono.just(km).flatMap(kms -> {
            return groupCodeKemuRepository.save(km).map(o -> R.ok());
        });
        Mono yiji2 = Mono.just(km).flatMap(kms -> {
            return groupCodeKemuRepository.findByUniqueAccStandardAndTemplateIdAndCcode(kms.getUniqueAccStandard(), kms.getTemplateId(), kms.getSuperiorCcode())
                    // 获取上级科目信息
                    .flatMap(code -> {
                        code.setBend("0");
                        // 修改上级科目末级标识
                        return groupCodeKemuRepository.save(code)
                                .flatMap(km2 -> {
                                    return groupCodeKemuRepository.save(km).map(o -> R.ok());
                                });
                    });
        });

        return km.getIgrade().equals("1") ? yiji : yiji2;
    }

    /**
     * 组织科目表增加
     *
     * @param km
     * @return
     */
    @PostMapping("saveOrgCodekemu")
    public Mono saveOrgCodekemu(@RequestBody GroupCodeKemuOrg km) {
        km
                .setUniqueCode(RandomNum.uuid())
                .setCbookType("金额式")
                .setSuperiorCcode(StringUtils.isBlank(km.getSuperiorCcode()) ? "0" : km.getSuperiorCcode())
                .setBnum(StringUtils.isNotBlank(km.getBnum()) ? km.getBnum() : "0")
                .setCurrency(StringUtils.isNotBlank(km.getCurrency()) ? km.getCurrency() : "0")
                .setBcash(StringUtils.isNotBlank(km.getBcash()) ? km.getBcash() : "0")
                .setBbank(StringUtils.isNotBlank(km.getBbank()) ? km.getBbank() : "0")
                .setBcashEquivalence(StringUtils.isNotBlank(km.getBcashEquivalence()) ? km.getBcashEquivalence() : "0")
                .setBdaybook(StringUtils.isNotBlank(km.getBdaybook()) ? km.getBdaybook() : "0")
                .setFlag("1")
                .setBend("1")
                .setBnum(km.getBnum())
                .setBstock("0")
                .setCassItem("0")
                .setLowerControl(km.getLowerControl())
                .setFuzhuControl(km.getFuzhuControl())
                .setControlled(km.getControlled())
                .setPxjz(km.getPxjz())
                .setXjll(km.getXjll())
                .setCyfx(km.getCyfx())
                .setCdfine1("0")
                .setCdfine2("0")
                .setCdfine3("0")
                .setCdfine4("0")
                .setCdfine5("0")
                .setCdfine6("0")
                .setCdfine7("0")
                .setCdfine8("0")
                .setCdfine9("0")
                .setCdfine10("0")
                .setCdfine11("0")
                .setCdfine12("0")
                .setCdfine13("0")
                .setCdfine14("0")
                .setCdfine15("0")
                .setCdfine16("0")
                .setCdfine17("0")
                .setCdfine18("0")
                .setCdfine19("0")
                .setCdfine20("0")
                .setCdfine21("0")
                .setCdfine22("0")
                .setCdfine23("0")
                .setCdfine24("0")
                .setCdfine25("0")
                .setCdfine26("0")
                .setCdfine27("0")
                .setCdfine28("0")
                .setCdfine29("0")
                .setCdfine30("0");

        if (StringUtils.isNotBlank(km.getFuzhu())) {
            for (int i = 0; i < km.getFuzhu().split(",").length; i++) {
                if (km.getFuzhu().split(",")[i].equals("999")) {                    // 个人
                    km.setBperson("1");
                } else if (km.getFuzhu().split(",")[i].equals("998")) {             // 部门
                    km.setBdept("1");
                } else if (km.getFuzhu().split(",")[i].equals("997")) {            // '项目'
                    km.setBitem("1");
                } else if (km.getFuzhu().split(",")[i].equals("996")) {             // '客户'
                    km.setBcus("1");
                } else if (km.getFuzhu().split(",")[i].equals("995")) {             // '供应商'
                    km.setBsup("1");
                } else if (km.getFuzhu().split(",")[i].equals("994")) {             // '支出功能分类'
                    km.setYsZcgnfl("1");
                } else if (km.getFuzhu().split(",")[i].equals("993")) {             // '部门支出经济分类'
                    km.setYsBmzcjjfl("1");
                } else if (km.getFuzhu().split(",")[i].equals("992")) {             // '政府支出经济分类'
                    km.setYsZfzcjjfl("1");
                } else if (km.getFuzhu().split(",")[i].equals("991")) {             // '预算来源'
                    km.setYsYsly("1");
                }
            }
        } else {
            km.setBperson("0").setBdept("0").setBitem("0").setBcus("0").setBsup("0");
        }
        Mono<R> yiji = Mono.just(km).flatMap(kms -> {
            return groupCodeKemuOrgRepository.save(km).map(o -> R.ok().setResult(o));
        });
        Mono<R> yiji2 = Mono.just(km).flatMap(kms -> {
            return groupCodeKemuOrgRepository.findAllByCcodeAndOrgUniqueAndIyear(kms.getSuperiorCcode(), kms.getOrgUnique(), kms.getIyear())
                    // 获取上级科目信息
                    .flatMap(code -> {
                        code.setBend("0");
                        // 修改上级科目末级标识
                        return groupCodeKemuOrgRepository.save(code)
                                .flatMap(km2 -> {
                                    return groupCodeKemuOrgRepository.save(km).map(o -> R.ok().setResult(o));
                                });
                    });
        });
        return km.getIgrade().equals("1") ? yiji : yiji2;
    }

    @Transactional
    @PostMapping("delCodekemu")
    public Mono delCodekemu(String id, String uniqueAccStandard, String templateId) {
        return groupCodeKemuRepository.findAllByUniqueAccStandardAndTemplateIdOrderByCcodeAsc(uniqueAccStandard, templateId)
                .collectList().map(list -> {
                    List<GroupCodeKemu> suporCodeList = new ArrayList<>();
                    List<GroupCodeKemu> newlist = new ArrayList<>();
                    for (int i = 0; i < id.split(",").length; i++) {
                        int finalI = i;
                        GroupCodeKemu codeKemu = list.stream().filter(ck -> ck.getId().equals(id.split(",")[finalI])).collect(Collectors.toList()).get(0);
                        newlist.add(codeKemu);
                        // 获取上级科目
                        if (!codeKemu.getSuperiorCcode().equals("0")) {
                            // 修改上级科目末级状态
                            List<GroupCodeKemu> codeKemu1 = list.stream().filter(ck -> ck.getCcode().equals(codeKemu.getSuperiorCcode())).collect(Collectors.toList());
                            if (codeKemu1.size() > 0) {
                                codeKemu1.get(0).setBend("1");
                                suporCodeList.add(codeKemu1.get(0));
                            }
                        }
                    }
                    return CollectOfUtils.mapof("codelist", newlist, "suporlist", suporCodeList.stream().distinct().collect(Collectors.toList()));
                })
                .flatMap(map -> {
                    List<GroupCodeKemu> suporCodeList = map.get("suporlist");
                    List<GroupCodeKemu> newlist = map.get("codelist");

                    return groupCodeKemuRepository.saveAll(suporCodeList).collectList()
                            .map(aa -> newlist)
                            .flatMap(groupCodeKemuRepository::deleteAll);
                }).then(Mono.just(R.ok()));
    }

    // 查询组织科目
    @PostMapping("findOrgCodeByIyearAndOrgUnique")
    public Mono<R> findOrgCodeByIyearAndOrgUnique(String iyear, String orgUnique) {
        return groupCodeKemuOrgRepository.findAllByIyearAndorgUniqueOrderByCcodeAsc(iyear, orgUnique).collectList().map(a->R.ok().setResult(a));
    }

    @Transactional
    @PostMapping("delOrgCodekemu")
    public Mono delOrgCodekemu(String id, String iyear, String orgUnique) {
        return groupCodeKemuOrgRepository.findAllByIyearAndorgUniqueOrderByCcodeAsc(iyear, orgUnique)
                .collectList().map(list -> {
                    List<GroupCodeKemuOrg> suporCodeList = new ArrayList<>();
                    List<GroupCodeKemuOrg> newlist = new ArrayList<>();
                    for (int i = 0; i < id.split(",").length; i++) {
                        int finalI = i;
                        GroupCodeKemuOrg codeKemu = list.stream().filter(ck -> ck.getId().equals(id.split(",")[finalI])).collect(Collectors.toList()).get(0);
                        newlist.add(codeKemu);
                        // 获取上级科目
                        if (!codeKemu.getSuperiorCcode().equals("0")) {
                            // 修改上级科目末级状态
                            List<GroupCodeKemuOrg> codeKemu1 = list.stream().filter(ck -> ck.getCcode().equals(codeKemu.getSuperiorCcode())).collect(Collectors.toList());
                            if (codeKemu1.size() > 0) {
                                codeKemu1.get(0).setBend("1");
                                suporCodeList.add(codeKemu1.get(0));
                            }
                        }
                    }
                    return CollectOfUtils.mapof("codelist", newlist, "suporlist", suporCodeList.stream().distinct().collect(Collectors.toList()));
                })
                .flatMap(map -> {
                    List<GroupCodeKemuOrg> suporCodeList = map.get("suporlist");
                    List<GroupCodeKemuOrg> newlist = map.get("codelist");

                    return groupCodeKemuOrgRepository.saveAll(suporCodeList).collectList()
                            .map(aa -> newlist)
                            .flatMap(groupCodeKemuOrgRepository::deleteAll);
                }).then(Mono.just(R.ok()));
    }

    /**
     * 删除父级及子级科目
     *
     * @param id                ：科目编码
     * @param uniqueAccStandard
     * @param templateId
     * @return
     */
    @Transactional
    @PostMapping("delCodekemuLikeSuperCode")
    public Mono delCodekemuLikeSuperCode(String id, String uniqueAccStandard, String templateId) {
        return groupCodeKemuRepository.findAllByUniqueAccStandardAndTemplateIdOrderByCcodeAsc(uniqueAccStandard, templateId)
                .collectList().map(list -> {
                    List<GroupCodeKemu> newlist = new ArrayList<>();
                    for (int i = 0; i < id.split(",").length; i++) {
                        int finalI = i;
                        List<GroupCodeKemu> codeKemu = list.stream().filter(ck -> ck.getCcode().startsWith(id.split(",")[finalI])).collect(Collectors.toList());
                        codeKemu.stream().forEach(item -> {
                            newlist.add(item);
                        });
                    }
                    return newlist;
                })
                .flatMap(groupCodeKemuRepository::deleteAll).then(Mono.just(R.ok()));
    }

    @Transactional
    @PostMapping("delOrgCodekemuLikeSuperCode")
    public Mono delOrgCodekemuLikeSuperCode(String id, String uniqueAccStandard, String templateId) {
        return groupCodeKemuOrgRepository.findAllByUniqueAccStandardAndTemplateIdOrderByCcodeAsc(uniqueAccStandard, templateId)
                .collectList().map(list -> {
                    List<GroupCodeKemuOrg> newlist = new ArrayList<>();
                    for (int i = 0; i < id.split(",").length; i++) {
                        int finalI = i;
                        List<GroupCodeKemuOrg> codeKemu = list.stream().filter(ck -> ck.getCcode().startsWith(id.split(",")[finalI])).collect(Collectors.toList());
                        codeKemu.stream().forEach(item -> {
                            newlist.add(item);
                        });
                    }
                    return newlist;
                })
                .flatMap(groupCodeKemuOrgRepository::deleteAll).then(Mono.just(R.ok()));
    }

    /**
     * 查询单个科目信息
     *
     * @param uniqueAccStandard 会计准则
     * @param templateId        科目模板
     * @param ccode             科目编码
     * @return
     */
    @PostMapping("findByUniqueAccStandardAndTemplateIdAndCcode")
    public Mono<R> findByUniqueAccStandardAndTemplateIdAndCcode(String uniqueAccStandard, String templateId, String ccode) {
        return groupCodeKemuRepository.findByUniqueAccStandardAndTemplateIdAndCcode(uniqueAccStandard, templateId, ccode).map(o -> R.ok().setResult(o));
    }


    @Transactional
    @PostMapping("/importCoke")
    public Mono<R> importCoke(@RequestPart("file") FilePart filePartParm, @RequestPart("templateInfo") String templateInfo) throws Exception {
        // 获取表头个数
        AtomicInteger column = new AtomicInteger();
        String[] parameter = templateInfo.split(">>>");
        String uniqueAccStandard = parameter[0];
        String templateId = parameter[1];
        String jici = parameter[2];
        String accStyleUnique = parameter[3];
        Path tempFilePath = Files.createTempFile("", new String(filePartParm.filename().getBytes("ISO-8859-1"), "UTF-8"));
        return Mono.just(filePartParm)
                // 上传到临时目录
                .flatMap(filePart -> {
                    try {
                        return DataBufferUtils
                                .write(filePart.content(), AsynchronousFileChannel.open(tempFilePath, StandardOpenOption.WRITE), 0)
                                .doOnComplete(() -> log.info("上传成功"))
                                .collectList()
                                .map(item -> tempFilePath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return Mono.just("");
                })
                // 遍历导入excel内容
                .flatMap(objArr -> {
                    List<Object[]> list = null;
                    XlsUtils3 excelReader = new XlsUtils3(tempFilePath.toString());
                    String[] str = new String[]{
                            "科目编码",             // 0
                            "科目名称",             // 1
                            "方向",               //2
                            "科目类型",             //3
                            "现金科目(是/空)",                //4
                            "银行科目(是/空)",                //5
                            "现金等价物(是/空)",               //6
                            "数量核算(是/空)",                //7
                            "计量单位(名称/空)",               //8
                            "外币核算(是/空)",                //9
                            "外币名称(名称/空)",               //10
                            "个人往来(是/空)",                //11
                            "客户往来(是/空)",                //12
                            "供应商往来(是/空)",               //13
                            "部门核算(是/空)",                //14
                            "项目核算(是/空)",                //15
                            ""};
                    list = excelReader.getExcelObj(tempFilePath.toString(), str, 0);
                    Arrays.stream(list.get(0)).forEach(v -> {
                        if (v != null) {
                            column.addAndGet(1);
                        }
                    });

                    assert tempFilePath != null;
                    try {
                        Files.delete(tempFilePath);
                    } catch (IOException e) {
                    }
                    return Mono.just(list);
                })
                .flatMap(item -> {
                    Map mapArr = new HashMap();
                    mapArr.put("excellist", item);   // excel中内容
                    mapArr.put("code", "200");
                    mapArr.put("error", "");
                    return groupCodeKemuRepository.findAllByUniqueAccStandardAndTemplateIdOrderByCcodeAsc(uniqueAccStandard, templateId)
                            .collectList()
                            .flatMap(list -> {
                                mapArr.put("kmlist", list);
                                return accStyleRepository.findAllStyleByUnique(accStyleUnique).collectList().flatMap(accStyle -> {
                                    mapArr.put("kmstyle", accStyle);
                                    return usedForeignCurrencyRepository.findAll().collectList().flatMap(c -> {
                                        mapArr.put("currencyTypelist", c);// 币种list
                                        return Mono.just(mapArr);
                                    });
                                });
                            });
                })
                // 判断科目级次是否合法
                .flatMap(mapArr -> {
                    // 科目list
                    List<GroupCodeKemu> kmlist = (List<GroupCodeKemu>) mapArr.get("kmlist");
                    // 科目类型list
                    List<AccStyle> kmstyle = (List<AccStyle>) mapArr.get("kmstyle");
                    // 文件list
                    List<Object[]> excellist = (List<Object[]>) mapArr.get("excellist");
                    // 币种list
                    List<UsedForeignCurrency> sysCurrencyList = (List<UsedForeignCurrency>) mapArr.get("currencyTypelist");
                    List<GroupCodeKemu> codelist = new ArrayList<>();

                    if (!excellist.get(0)[0].equals("科目编码") && !excellist.get(0)[0].equals("科目名称") && !excellist.get(0)[0].equals("方向") && !excellist.get(0)[0].equals("科目类型")) {
                        mapArr.put("error", "导入模板表头不正确，请使用正确的模板进行导入");
                        mapArr.put("code", "401");
                        return Mono.just(mapArr);
                    }

                    // 判断是否重复
                    for (int i = 1; i < excellist.size(); i++) {
                        // 级次封装
                        List<Map> jicilist = new ArrayList<>();
                        int test = 0;
                        for (int j = 0; j < jici.split("-").length; j++) {
                            test += Integer.valueOf(jici.split("-")[j]);
                            jicilist.add(CollectOfUtils.mapof("jicilength", test, "jicinum", jici.split("-")[j], "level", j + 1));
                        }


                        int a = column.get();
                        Object[] obj = excellist.get(i);
                        int finalI = i;
                        List<String> errorText = new ArrayList<>();
                        // 科目编码是否重复
                        if (StringUtils.isNotBlank(excellist.get(finalI)[0].toString().trim())) {
                            List<GroupCodeKemu> collect = kmlist.stream().filter(v -> v.getCcode().equals(excellist.get(finalI)[0].toString())).collect(Collectors.toList());
                            if (collect.size() > 0) {
                                errorText.add("科目编码与当前已存在档案重复");
                                obj[a] = errorText.toString();
                                if (codelist.size() > 0) {
                                    codelist.remove(0);
                                }
                            }

                            List<Map> findByjici = jicilist.stream().filter(ji -> ji.get("jicilength").toString().equals(String.valueOf(excellist.get(finalI)[0].toString().length()))).collect(Collectors.toList());
                            if (findByjici.size() == 0) {
                                errorText.add("科目编码不符合系统科目级次设置规则");
                                obj[a] = errorText.toString();
                            }
                        } else {
                            errorText.add("科目编码不能为空");
                            obj[a] = errorText.toString();
                            if (codelist.size() > 0) {
                                codelist.remove(0);
                            }
                        }
                        // 科目名称是否重复
                        if (StringUtils.isNotBlank(excellist.get(finalI)[1].toString().trim())) {
                            List<GroupCodeKemu> collect = kmlist.stream().filter(v -> v.getCcodeName().equals(excellist.get(finalI)[1].toString())).collect(Collectors.toList());
                            if (collect.size() > 0) {
                                errorText.add("科目名称与当前已存在档案重复");
                                obj[a] = errorText.toString();
                                if (codelist.size() > 0) {
                                    codelist.remove(0);
                                }
                            }
                        } else {
                            errorText.add("科目名称不能为空");
                            obj[a] = errorText.toString();
                            if (codelist.size() > 0) {
                                codelist.remove(0);
                            }
                        }
                        // 科目方向
                        if (StringUtils.isBlank(excellist.get(finalI)[2].toString().trim())) {
                            errorText.add("科目方向不能为空");
                            obj[a] = errorText.toString();
                            if (codelist.size() > 0) {
                                codelist.remove(0);
                            }
                        } else {
                            if (!excellist.get(finalI)[2].toString().contains("借") && !excellist.get(finalI)[2].toString().contains("贷")) {
                                errorText.add("未知科目方向");
                                obj[a] = errorText.toString();
                                if (codelist.size() > 0) {
                                    codelist.remove(0);
                                }
                            }
                        }
                        // 科目类型
                        if (StringUtils.isBlank(excellist.get(finalI)[3].toString().trim())) {
                            errorText.add("科目类型不能为空");
                            obj[a] = errorText.toString();
                            if (codelist.size() > 0) {
                                codelist.remove(0);
                            }
                        } else {
                            // 检查科目首数字是否在此范围内
                            List<AccStyle> findByStyle = kmstyle.stream().filter(s -> s.getCclass().equals(excellist.get(finalI)[3].toString().trim())).collect(Collectors.toList());
                            if (findByStyle.size() == 0) {
                                errorText.add("科目类型不存在");
                                obj[a] = errorText.toString();
                                if (codelist.size() > 0) {
                                    codelist.remove(0);
                                }
                            }
                        }
                        //  检查科目的客户与供应商辅助核算是否同时选 是 11 12
                        if (excellist.get(i)[12] != null || excellist.get(i)[13] != null) {
                            if (
                                    (excellist.get(i)[12].equals("是") || excellist.get(i)[12].equals("1")) && (excellist.get(i)[13].equals("是") || excellist.get(i)[13].equals("1"))
                            ) {
                                errorText.add("科目的客户和供应商辅助核算项选择冲突,同一科目只能在客户辅助或供应商辅助核算中二选一");
                                obj[a] = errorText.toString();
                                if (codelist.size() > 0) {
                                    codelist.remove(0);
                                }
                            }
                        }

                        // 判断 数量核算选择是
                        if (excellist.get(i)[7] != null) {
                            if (excellist.get(i)[7].equals("是") || excellist.get(i)[6].equals("7")) {
                                if (excellist.get(i)[8] == null) {
                                    errorText.add("数量核算,计量单位不能为空");
                                    obj[a] = errorText.toString();
                                    if (codelist.size() > 0) {
                                        codelist.remove(0);
                                    }
                                }
                            }
                        }
                        // 判断 外币核算
                        if (excellist.get(i)[9] != null) {
                            if (excellist.get(i)[9].equals("是") || excellist.get(i)[9].equals("1")) {
                                // 判断 币种是否为空 或者 系统中不存在
                                int finalI1 = i;
                                List<UsedForeignCurrency> syscurr = sysCurrencyList.stream().filter(c -> c.getForeignName().equals(excellist.get(finalI1)[10].toString()) || c.getForeignCode().equals(excellist.get(finalI1)[10].toString())).collect(Collectors.toList());
                                if (StringUtils.isBlank(excellist.get(i)[10].toString()) || syscurr.size() == 0) {
                                    errorText.add("系统外币档案中不存在,请填写正确的外币名称");
                                    obj[a] = errorText.toString();
                                    if (codelist.size() > 0) {
                                        codelist.remove(0);
                                    }
                                }
                            }
                        }
                        if (errorText.size() == 0) {
                            // 科目编码
                            String ccode = excellist.get(i)[0].toString().trim();
                            String ccodeName = excellist.get(i)[1].toString().trim();
                            String bprogerty = excellist.get(i)[2].toString().trim().contains("借") ? "1" : "0";
                            String cclass = excellist.get(i)[3].toString().trim();
                            // 现金账
                            String bcash = excellist.get(i)[4] == null ? "0" : excellist.get(i)[4].toString().trim().equals("是") ? "1" : excellist.get(i)[4].toString().trim().equals("1") ? "1" : "0";
                            // 银行账
                            String bbank = excellist.get(i)[5] == null ? "0" : excellist.get(i)[5].toString().trim().equals("是") ? "1" : excellist.get(i)[5].toString().trim().equals("1") ? "1" : "0";
                            // 现金等价物
                            String bcashEquivalence = excellist.get(i)[6] == null ? "0" : excellist.get(i)[6].toString().trim().equals("是") ? "1" : excellist.get(i)[6].toString().trim().equals("1") ? "1" : "0";
                            // 数量核算
                            String bnum = excellist.get(i)[7] == null ? "0" : excellist.get(i)[7].toString().trim().equals("是") ? "1" : excellist.get(i)[7].toString().trim().equals("1") ? "1" : "0";
                            // 计量单位
                            String menterage = excellist.get(i)[8] == null ? "" : excellist.get(i)[8].toString().trim();
                            // 外币核算
                            String currency = excellist.get(i)[9] == null ? "0" : excellist.get(i)[9].toString().trim().equals("是") ? "1" : excellist.get(i)[9].toString().trim().equals("1") ? "1" : "0";
                            // 外币名称
                            String currencyType = excellist.get(i)[10] == null ? "" : excellist.get(i)[10].toString().trim();
                            // 个人往来
                            String bperson = excellist.get(i)[11] == null ? "0" : excellist.get(i)[11].toString().trim().equals("是") ? "1" : excellist.get(i)[11].toString().trim().equals("1") ? "1" : "0";
                            // 客户往来
                            String bcus = excellist.get(i)[12] == null ? "0" : excellist.get(i)[12].toString().trim().equals("是") ? "1" : excellist.get(i)[12].toString().trim().equals("1") ? "1" : "0";
                            // 供应商往来
                            String bsup = excellist.get(i)[13] == null ? "0" : excellist.get(i)[13].toString().trim().equals("是") ? "1" : excellist.get(i)[13].toString().trim().equals("1") ? "1" : "0";
                            // 部门核算
                            String bdept = excellist.get(i)[14] == null ? "0" : excellist.get(i)[14].toString().trim().equals("是") ? "1" : excellist.get(i)[14].toString().trim().equals("1") ? "1" : "0";
                            // 项目核算
                            String bitem = excellist.get(i)[15] == null ? "0" : excellist.get(i)[15].toString().trim().equals("是") ? "1" : excellist.get(i)[15].toString().trim().equals("1") ? "1" : "0";

                            // 判断科目编码是否符合级次
                            int jicinum = 0;
                            String level = "";
                            List<Map> findByjici = jicilist.stream().filter(ji -> ji.get("jicilength").toString().equals(String.valueOf(ccode.length()))).collect(Collectors.toList());
                            if (findByjici.size() == 0) {
                                jicinum = Integer.valueOf(findByjici.get(0).get("jicinum").toString());
                                level = findByjici.get(0).get("level").toString();
                            }
                            GroupCodeKemu ck = new GroupCodeKemu();
                            ck
                                    .setCclass(cclass)
                                    .setCcode(ccode)
                                    .setCcodeName(ccodeName)
                                    .setBprogerty(bprogerty)
                                    .setBcash(bcash)
                                    .setBbank(bbank)
                                    .setBcashEquivalence(bcashEquivalence)
                                    .setBnum(bnum)
                                    .setMenterage(menterage)
                                    .setCurrency(currency)
                                    .setCurrencyType(currencyType)
                                    .setBperson(bperson)
                                    .setBcus(bcus)
                                    .setBsup(bsup)
                                    .setBdept(bdept)
                                    .setBitem(bitem)
                                    .setUniqueCode(ck.getCcode())
                                    .setUniqueAccStandard(uniqueAccStandard)
                                    .setTemplateId(templateId)
                                    .setCbookType("金额式")
                                    .setFlag("1")
                                    .setBend("1")
                                    .setCassItem("0")
                                    .setBstock("0")
                                    .setFuzhuControl("0")
                                    .setLowerControl("0")
                                    .setIgrade(level)
                                    .setSuperiorCcode("0")
                                    .setBdaybook("0")
                                    .setControlled("0")
                                    .setYusuan("0")
                                    .setPxjz("0")
                                    .setXjll("0")
                                    .setCyfx("0")
                                    .setCdfine1("0")
                                    .setCdfine2("0")
                                    .setCdfine3("0")
                                    .setCdfine4("0")
                                    .setCdfine5("0")
                                    .setCdfine6("0")
                                    .setCdfine7("0")
                                    .setCdfine8("0")
                                    .setCdfine9("0")
                                    .setCdfine10("0")
                                    .setCdfine11("0")
                                    .setCdfine12("0")
                                    .setCdfine13("0")
                                    .setCdfine14("0")
                                    .setCdfine15("0")
                                    .setCdfine16("0")
                                    .setCdfine17("0")
                                    .setCdfine18("0")
                                    .setCdfine19("0")
                                    .setCdfine20("0")
                                    .setCdfine21("0")
                                    .setCdfine22("0")
                                    .setCdfine23("0")
                                    .setCdfine24("0")
                                    .setCdfine25("0")
                                    .setCdfine26("0")
                                    .setCdfine27("0")
                                    .setCdfine28("0")
                                    .setCdfine29("0")
                                    .setCdfine30("0");

                            // 根据科目编码首位查找科目类型
                            List<AccStyle> accStyleStream = kmstyle.stream().filter(s -> s.getCclass().equals(cclass)).collect(Collectors.toList());
                            if (accStyleStream.size() > 0)
                                ck.setYusuan(StrUtil.isNotBlank(accStyleStream.get(0).getFlagYusuan()) ? "1" : "0");

                            codelist.add(ck);
                            // 算出上级科目
                            String suprtcode = ccode.substring(0, ccode.length() - jicinum);
                            if (StringUtils.isNotBlank(suprtcode)) {
                                // 修改上级科目末级状态
                                codelist.stream().forEach(km -> {
                                    if (km.getCcode().equals(excellist.get(finalI)[0].toString())) {
                                        km.setSuperiorCcode(suprtcode);
                                    }
                                    if (km.getCcode().equals(suprtcode)) {
                                        km.setBend("0");
                                    }
                                });

                                // 判断 上级科目是否存在表中
                                List<GroupCodeKemu> findBySuperCode = kmlist.stream().filter(km -> km.getCcode().equals(suprtcode)).collect(Collectors.toList());
                                if (findBySuperCode.size() > 0) {
                                    // 获取同级别的科目编码
                                    List<String> equalsLevelCode = new ArrayList<>();
                                    for (int j = 1; j < excellist.size(); j++) {
                                        if (excellist.get(j)[0].toString().length() == findBySuperCode.get(0).getCcode().length()) {
                                            equalsLevelCode.add(excellist.get(j)[0].toString());
                                        }
                                    }

                                    // 去重后的同级别科目编码
                                    List<String> distinctNumlist = equalsLevelCode.stream().distinct().collect(Collectors.toList());
                                    if (equalsLevelCode.size() != distinctNumlist.size()) {
                                        errorText.add("科目的科目编码在系统中重复");
                                        obj[a] = errorText.toString();
                                        if (codelist.size() > 0) {
                                            codelist.remove(0);
                                        }
                                    }
                                }
                                // 判断 上级科目是否存在模板中
                                List<Object[]> findBySuperCodeEcxel = excellist.stream().filter(km -> km[0].equals(suprtcode)).collect(Collectors.toList());
                                if (findBySuperCodeEcxel.size() > 0) {
                                    // 获取下级科目编码
                                    List<String> equalsLevelCode = new ArrayList<>();
                                    // 获取下级科目名称;用于判断文件中下级科目名称是否重复
                                    List<String> equalsLevelCodeName = new ArrayList<>();
                                    for (int j = 1; j < excellist.size(); j++) {
                                        if (excellist.get(j)[0].toString().indexOf(suprtcode) != -1 && !excellist.get(j)[0].toString().equals(suprtcode)) {
                                            equalsLevelCode.add(excellist.get(j)[0].toString());
                                        }
                                    }

                                    // 把期初、凭证的上级科目修改成下级第一个科目
                                    if (equalsLevelCode.size() > 0) {
                                        // 修改科目末级状态
                                        kmlist.stream().forEach(km -> {
                                            if (km.getCcode().equals(suprtcode)) {
                                                if (!km.getSuperiorCcode().equals("0")) {
                                                    km.setBend("0");
                                                }
                                            }
                                        });
                                    }
                                }
                                if (findBySuperCode.size() == 0 && findBySuperCodeEcxel.size() == 0) {
                                    errorText.add("上级科目编码在系统中不存在");
                                    obj[a] = errorText.toString();
                                    if (codelist.size() > 0) {
                                        if (codelist.size() > 0) {
                                            codelist.remove(0);
                                        }
                                    }
                                }
                            }
                        } else {
                            mapArr.put("code", "200");
                            mapArr.put("error", "error");
                        }
                    }
                    mapArr.put("list", codelist);
                    mapArr.put("kmlist", kmlist);
                    mapArr.put("column", column.get());
                    return Mono.just(mapArr);
                })
                .flatMapMany(mapArr -> {
                    // 科目list
                    List<GroupCodeKemu> kmlist = (List<GroupCodeKemu>) mapArr.get("kmlist");
                    // 往数据库导入科目
                    List<GroupCodeKemu> codelist = (List<GroupCodeKemu>) mapArr.get("list");
                    return mapArr.get("code").equals("401") ? Mono.just(mapArr) : groupCodeKemuRepository.saveAll(codelist).collectList().zipWith(groupCodeKemuRepository.saveAll(kmlist).collectList()).map(t -> mapArr);
                })
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @Transactional
    @PostMapping("/importOrgCoke")
    public Mono<R> importOrgCoke(@RequestPart("file") FilePart filePartParm, @RequestPart("templateInfo") String templateInfo, @RequestPart("iyear") String iyear) throws Exception {
        // 获取表头个数
        AtomicInteger column = new AtomicInteger();
        String[] parameter = templateInfo.split(">>>");
        String uniqueAccStandard = parameter[0];
        String templateId = parameter[1];
        String jici = parameter[2];
        String accStyleUnique = parameter[3];
        Path tempFilePath = Files.createTempFile("", new String(filePartParm.filename().getBytes("ISO-8859-1"), "UTF-8"));
        return Mono.just(filePartParm)
                // 上传到临时目录
                .flatMap(filePart -> {
                    try {
                        return DataBufferUtils
                                .write(filePart.content(), AsynchronousFileChannel.open(tempFilePath, StandardOpenOption.WRITE), 0)
                                .doOnComplete(() -> log.info("上传成功"))
                                .collectList()
                                .map(item -> tempFilePath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return Mono.just("");
                })
                // 遍历导入excel内容
                .flatMap(objArr -> {
                    List<Object[]> list = null;
                    XlsUtils3 excelReader = new XlsUtils3(tempFilePath.toString());
                    String[] str = new String[]{
                            "科目编码",             // 0
                            "科目名称",             // 1
                            "方向",               //2
                            "科目类型",             //3
                            "现金科目(是/空)",                //4
                            "银行科目(是/空)",                //5
                            "现金等价物(是/空)",               //6
                            "数量核算(是/空)",                //7
                            "计量单位(名称/空)",               //8
                            "外币核算(是/空)",                //9
                            "外币名称(名称/空)",               //10
                            "个人往来(是/空)",                //11
                            "客户往来(是/空)",                //12
                            "供应商往来(是/空)",               //13
                            "部门核算(是/空)",                //14
                            "项目核算(是/空)",                //15
                            ""};
                    list = excelReader.getExcelObj(tempFilePath.toString(), str, 0);
                    Arrays.stream(list.get(0)).forEach(v -> {
                        if (v != null) {
                            column.addAndGet(1);
                        }
                    });

                    assert tempFilePath != null;
                    try {
                        Files.delete(tempFilePath);
                    } catch (IOException e) {
                    }
                    return Mono.just(list);
                })
                .flatMap(item -> {
                    Map mapArr = new HashMap();
                    mapArr.put("excellist", item);   // excel中内容
                    mapArr.put("code", "200");
                    mapArr.put("error", "");
                    return groupCodeKemuOrgRepository.findAllByUniqueAccStandardAndTemplateIdOrderByCcodeAsc(uniqueAccStandard, templateId)
                            .collectList()
                            .flatMap(list -> {
                                list = list.stream().filter(v -> StrUtil.isNotBlank(v.getIyear()) && v.getIyear().equals(iyear)).collect(Collectors.toList());
                                mapArr.put("kmlist", list);
                                return accStyleRepository.findAllStyleByUnique(accStyleUnique).collectList().flatMap(accStyle -> {
                                    mapArr.put("kmstyle", accStyle);
                                    return usedForeignCurrencyRepository.findAll().collectList().flatMap(c -> {
                                        mapArr.put("currencyTypelist", c);// 币种list
                                        return Mono.just(mapArr);
                                    });
                                });
                            });
                })
                // 判断科目级次是否合法
                .flatMap(mapArr -> {
                    // 科目list
                    List<GroupCodeKemuOrg> kmlist = (List<GroupCodeKemuOrg>) mapArr.get("kmlist");
                    // 科目类型list
                    List<AccStyle> kmstyle = (List<AccStyle>) mapArr.get("kmstyle");
                    // 文件list
                    List<Object[]> excellist = (List<Object[]>) mapArr.get("excellist");
                    // 币种list
                    List<UsedForeignCurrency> sysCurrencyList = (List<UsedForeignCurrency>) mapArr.get("currencyTypelist");
                    List<GroupCodeKemuOrg> codelist = new ArrayList<>();

                    if (!excellist.get(0)[0].equals("科目编码") && !excellist.get(0)[0].equals("科目名称") && !excellist.get(0)[0].equals("方向") && !excellist.get(0)[0].equals("科目类型")) {
                        mapArr.put("error", "导入模板表头不正确，请使用正确的模板进行导入");
                        mapArr.put("code", "401");
                        return Mono.just(mapArr);
                    }

                    // 判断是否重复
                    for (int i = 1; i < excellist.size(); i++) {

                        // 级次封装
                        List<Map> jicilist = new ArrayList<>();
                        int test = 0;
                        for (int j = 0; j < jici.split("-").length; j++) {
                            test += Integer.valueOf(jici.split("-")[j]);
                            jicilist.add(CollectOfUtils.mapof("jicilength", test, "jicinum", jici.split("-")[j], "level", j + 1));
                        }

                        int a = column.get();
                        Object[] obj = excellist.get(i);
                        int finalI = i;
                        List<String> errorText = new ArrayList<>();
                        // 科目编码是否重复
                        if (StringUtils.isNotBlank(excellist.get(finalI)[0].toString().trim())) {
                            List<GroupCodeKemuOrg> collect = kmlist.stream().filter(v -> v.getCcode().equals(excellist.get(finalI)[0].toString())).collect(Collectors.toList());
                            if (collect.size() > 0) {
                                errorText.add("科目编码与当前已存在档案重复");
                                obj[a] = errorText.toString();
                                if (codelist.size() > 0) {
                                    codelist.remove(0);
                                }
                            }

                            List<Map> findByjici = jicilist.stream().filter(ji -> ji.get("jicilength").toString().equals(String.valueOf(excellist.get(finalI)[0].toString().length()))).collect(Collectors.toList());
                            if (findByjici.size() == 0) {
                                errorText.add("科目编码不符合系统科目级次设置规则");
                                obj[a] = errorText.toString();
                            }
                        } else {
                            errorText.add("科目编码不能为空");
                            obj[a] = errorText.toString();
                            if (codelist.size() > 0) {
                                codelist.remove(0);
                            }
                        }
                        // 科目名称是否重复
                        if (StringUtils.isNotBlank(excellist.get(finalI)[1].toString().trim())) {
                            List<GroupCodeKemuOrg> collect = kmlist.stream().filter(v -> v.getCcodeName().equals(excellist.get(finalI)[1].toString())).collect(Collectors.toList());
                            if (collect.size() > 0) {
                                errorText.add("科目名称与当前已存在档案重复");
                                obj[a] = errorText.toString();
                                if (codelist.size() > 0) {
                                    codelist.remove(0);
                                }
                            }
                        } else {
                            errorText.add("科目名称不能为空");
                            obj[a] = errorText.toString();
                            if (codelist.size() > 0) {
                                codelist.remove(0);
                            }
                        }
                        // 科目方向
                        if (StringUtils.isBlank(excellist.get(finalI)[2].toString().trim())) {
                            errorText.add("科目方向不能为空");
                            obj[a] = errorText.toString();
                            if (codelist.size() > 0) {
                                codelist.remove(0);
                            }
                        } else {
                            if (!excellist.get(finalI)[2].toString().contains("借") && !excellist.get(finalI)[2].toString().contains("贷")) {
                                errorText.add("未知科目方向");
                                obj[a] = errorText.toString();
                                if (codelist.size() > 0) {
                                    codelist.remove(0);
                                }
                            }
                        }
                        // 科目类型
                        if (StringUtils.isBlank(excellist.get(finalI)[3].toString().trim())) {
                            errorText.add("科目类型不能为空");
                            obj[a] = errorText.toString();
                            if (codelist.size() > 0) {
                                codelist.remove(0);
                            }
                        } else {
                            // 检查科目首数字是否在此范围内
                            List<AccStyle> findByStyle = kmstyle.stream().filter(s -> s.getCclass().equals(excellist.get(finalI)[3].toString().trim())).collect(Collectors.toList());
                            if (findByStyle.size() == 0) {
                                errorText.add("科目类型不存在");
                                obj[a] = errorText.toString();
                                if (codelist.size() > 0) {
                                    codelist.remove(0);
                                }
                            }
                        }
                        //  检查科目的客户与供应商辅助核算是否同时选 是 11 12
                        if (excellist.get(i)[12] != null || excellist.get(i)[13] != null) {
                            if (
                                    (excellist.get(i)[12].equals("是") || excellist.get(i)[12].equals("1")) && (excellist.get(i)[13].equals("是") || excellist.get(i)[13].equals("1"))
                            ) {
                                errorText.add("科目的客户和供应商辅助核算项选择冲突,同一科目只能在客户辅助或供应商辅助核算中二选一");
                                obj[a] = errorText.toString();
                                if (codelist.size() > 0) {
                                    codelist.remove(0);
                                }
                            }
                        }

                        // 判断 数量核算选择是
                        if (excellist.get(i)[7] != null) {
                            if (excellist.get(i)[7].equals("是") || excellist.get(i)[6].equals("7")) {
                                if (excellist.get(i)[8] == null) {
                                    errorText.add("数量核算,计量单位不能为空");
                                    obj[a] = errorText.toString();
                                    if (codelist.size() > 0) {
                                        codelist.remove(0);
                                    }
                                }
                            }
                        }
                        // 判断 外币核算
                        if (excellist.get(i)[9] != null) {
                            if (excellist.get(i)[9].equals("是") || excellist.get(i)[9].equals("1")) {
                                // 判断 币种是否为空 或者 系统中不存在
                                int finalI1 = i;
                                List<UsedForeignCurrency> syscurr = sysCurrencyList.stream().filter(c -> c.getForeignName().equals(excellist.get(finalI1)[10].toString()) || c.getForeignCode().equals(excellist.get(finalI1)[10].toString())).collect(Collectors.toList());
                                if (StringUtils.isBlank(excellist.get(i)[10].toString()) || syscurr.size() == 0) {
                                    errorText.add("系统外币档案中不存在,请填写正确的外币名称");
                                    obj[a] = errorText.toString();
                                    if (codelist.size() > 0) {
                                        codelist.remove(0);
                                    }
                                }
                            }
                        }
                        if (errorText.size() == 0) {
                            // 科目编码
                            String ccode = excellist.get(i)[0].toString().trim();
                            String ccodeName = excellist.get(i)[1].toString().trim();
                            String bprogerty = excellist.get(i)[2].toString().trim().contains("借") ? "1" : "0";
                            String cclass = excellist.get(i)[3].toString().trim();
                            // 现金账
                            String bcash = excellist.get(i)[4] == null ? "0" : excellist.get(i)[4].toString().trim().equals("是") ? "1" : excellist.get(i)[4].toString().trim().equals("1") ? "1" : "0";
                            // 银行账
                            String bbank = excellist.get(i)[5] == null ? "0" : excellist.get(i)[5].toString().trim().equals("是") ? "1" : excellist.get(i)[5].toString().trim().equals("1") ? "1" : "0";
                            // 现金等价物
                            String bcashEquivalence = excellist.get(i)[6] == null ? "0" : excellist.get(i)[6].toString().trim().equals("是") ? "1" : excellist.get(i)[6].toString().trim().equals("1") ? "1" : "0";
                            // 数量核算
                            String bnum = excellist.get(i)[7] == null ? "0" : excellist.get(i)[7].toString().trim().equals("是") ? "1" : excellist.get(i)[7].toString().trim().equals("1") ? "1" : "0";
                            // 计量单位
                            String menterage = excellist.get(i)[8] == null ? "" : excellist.get(i)[8].toString().trim();
                            // 外币核算
                            String currency = excellist.get(i)[9] == null ? "0" : excellist.get(i)[9].toString().trim().equals("是") ? "1" : excellist.get(i)[9].toString().trim().equals("1") ? "1" : "0";
                            // 外币名称
                            String currencyType = excellist.get(i)[10] == null ? "" : excellist.get(i)[10].toString().trim();
                            // 个人往来
                            String bperson = excellist.get(i)[11] == null ? "0" : excellist.get(i)[11].toString().trim().equals("是") ? "1" : excellist.get(i)[11].toString().trim().equals("1") ? "1" : "0";
                            // 客户往来
                            String bcus = excellist.get(i)[12] == null ? "0" : excellist.get(i)[12].toString().trim().equals("是") ? "1" : excellist.get(i)[12].toString().trim().equals("1") ? "1" : "0";
                            // 供应商往来
                            String bsup = excellist.get(i)[13] == null ? "0" : excellist.get(i)[13].toString().trim().equals("是") ? "1" : excellist.get(i)[13].toString().trim().equals("1") ? "1" : "0";
                            // 部门核算
                            String bdept = excellist.get(i)[14] == null ? "0" : excellist.get(i)[14].toString().trim().equals("是") ? "1" : excellist.get(i)[14].toString().trim().equals("1") ? "1" : "0";
                            // 项目核算
                            String bitem = excellist.get(i)[15] == null ? "0" : excellist.get(i)[15].toString().trim().equals("是") ? "1" : excellist.get(i)[15].toString().trim().equals("1") ? "1" : "0";

                            // 判断科目编码是否符合级次
                            int jicinum = 0;
                            String level = "";
                            List<Map> findByjici = jicilist.stream().filter(ji -> ji.get("jicilength").toString().equals(String.valueOf(ccode.length()))).collect(Collectors.toList());
                            if (findByjici.size() > 0) {
                                jicinum = Integer.valueOf(findByjici.get(0).get("jicinum").toString());
                                level = findByjici.get(0).get("level").toString();
                            }
                            GroupCodeKemuOrg ck = new GroupCodeKemuOrg();
                            ck
                                    .setCclass(cclass)
                                    .setIyear(iyear)
                                    .setCcode(ccode)
                                    .setCcodeName(ccodeName)
                                    .setBprogerty(bprogerty)
                                    .setBcash(bcash)
                                    .setBbank(bbank)
                                    .setBcashEquivalence(bcashEquivalence)
                                    .setBnum(bnum)
                                    .setMenterage(menterage)
                                    .setCurrency(currency)
                                    .setCurrencyType(currencyType)
                                    .setBperson(bperson)
                                    .setBcus(bcus)
                                    .setBsup(bsup)
                                    .setBdept(bdept)
                                    .setBitem(bitem)
                                    .setUniqueCode(ck.getCcode())
                                    .setUniqueAccStandard(uniqueAccStandard)
                                    .setTemplateId(templateId)
                                    .setCbookType("金额式")
                                    .setFlag("1")
                                    .setBend("1")
                                    .setCassItem("0")
                                    .setBstock("0")
                                    .setFuzhuControl("0")
                                    .setLowerControl("0")
                                    .setIgrade(level)
                                    .setSuperiorCcode("0")
                                    .setBdaybook("0")
                                    .setControlled("0")
                                    .setYusuan("0")
                                    .setPxjz("0")
                                    .setXjll("0")
                                    .setCyfx("0")
                                    .setCdfine1("0")
                                    .setCdfine2("0")
                                    .setCdfine3("0")
                                    .setCdfine4("0")
                                    .setCdfine5("0")
                                    .setCdfine6("0")
                                    .setCdfine7("0")
                                    .setCdfine8("0")
                                    .setCdfine9("0")
                                    .setCdfine10("0")
                                    .setCdfine11("0")
                                    .setCdfine12("0")
                                    .setCdfine13("0")
                                    .setCdfine14("0")
                                    .setCdfine15("0")
                                    .setCdfine16("0")
                                    .setCdfine17("0")
                                    .setCdfine18("0")
                                    .setCdfine19("0")
                                    .setCdfine20("0")
                                    .setCdfine21("0")
                                    .setCdfine22("0")
                                    .setCdfine23("0")
                                    .setCdfine24("0")
                                    .setCdfine25("0")
                                    .setCdfine26("0")
                                    .setCdfine27("0")
                                    .setCdfine28("0")
                                    .setCdfine29("0")
                                    .setCdfine30("0");

                            // 根据科目编码首位查找科目类型
                            List<AccStyle> accStyleStream = kmstyle.stream().filter(s -> s.getCclass().equals(cclass)).collect(Collectors.toList());
                            if (accStyleStream.size() > 0)
                                ck.setYusuan(StrUtil.isNotBlank(accStyleStream.get(0).getFlagYusuan()) ? "1" : "0");

                            codelist.add(ck);
                            // 算出上级科目
                            String suprtcode = ccode.substring(0, ccode.length() - jicinum);
                            if (StringUtils.isNotBlank(suprtcode)) {
                                // 修改上级科目末级状态
                                codelist.stream().forEach(km -> {
                                    if (km.getCcode().equals(excellist.get(finalI)[0].toString())) {
                                        km.setSuperiorCcode(suprtcode);
                                    }
                                    if (km.getCcode().equals(suprtcode)) {
                                        km.setBend("0");
                                    }
                                });

                                // 判断 上级科目是否存在表中
                                List<GroupCodeKemuOrg> findBySuperCode = kmlist.stream().filter(km -> km.getCcode().equals(suprtcode)).collect(Collectors.toList());
                                if (findBySuperCode.size() > 0) {
                                    // 获取同级别的科目编码
                                    List<String> equalsLevelCode = new ArrayList<>();
                                    for (int j = 1; j < excellist.size(); j++) {
                                        if (excellist.get(j)[0].toString().length() == findBySuperCode.get(0).getCcode().length()) {
                                            equalsLevelCode.add(excellist.get(j)[0].toString());
                                        }
                                    }

                                    // 去重后的同级别科目编码
                                    List<String> distinctNumlist = equalsLevelCode.stream().distinct().collect(Collectors.toList());
                                    if (equalsLevelCode.size() != distinctNumlist.size()) {
                                        errorText.add("科目的科目编码在系统中重复");
                                        obj[a] = errorText.toString();
                                        if (codelist.size() > 0) {
                                            codelist.remove(0);
                                        }
                                    }
                                }
                                // 判断 上级科目是否存在模板中
                                List<Object[]> findBySuperCodeEcxel = excellist.stream().filter(km -> km[0].equals(suprtcode)).collect(Collectors.toList());
                                if (findBySuperCodeEcxel.size() > 0) {
                                    // 获取下级科目编码
                                    List<String> equalsLevelCode = new ArrayList<>();
                                    // 获取下级科目名称;用于判断文件中下级科目名称是否重复
                                    List<String> equalsLevelCodeName = new ArrayList<>();
                                    for (int j = 1; j < excellist.size(); j++) {
                                        if (excellist.get(j)[0].toString().indexOf(suprtcode) != -1 && !excellist.get(j)[0].toString().equals(suprtcode)) {
                                            equalsLevelCode.add(excellist.get(j)[0].toString());
                                        }
                                    }

                                    // 把期初、凭证的上级科目修改成下级第一个科目
                                    if (equalsLevelCode.size() > 0) {
                                        // 修改科目末级状态
                                        kmlist.stream().forEach(km -> {
                                            if (km.getCcode().equals(suprtcode)) {
                                                if (!km.getSuperiorCcode().equals("0")) {
                                                    km.setBend("0");
                                                }
                                            }
                                        });
                                    }
                                }
                                if (findBySuperCode.size() == 0 && findBySuperCodeEcxel.size() == 0) {
                                    errorText.add("上级科目编码在系统中不存在");
                                    obj[a] = errorText.toString();
                                    if (codelist.size() > 0) {
                                        if (codelist.size() > 0) {
                                            codelist.remove(0);
                                        }
                                    }
                                }
                            }
                        } else {
                            mapArr.put("code", "200");
                            mapArr.put("error", "error");
                        }
                    }
                    mapArr.put("list", codelist);
                    mapArr.put("kmlist", kmlist);
                    mapArr.put("column", column.get());
                    return Mono.just(mapArr);
                })
                .flatMapMany(mapArr -> {
                    // 科目list
                    List<GroupCodeKemuOrg> kmlist = (List<GroupCodeKemuOrg>) mapArr.get("kmlist");
                    // 往数据库导入科目
                    List<GroupCodeKemuOrg> codelist = (List<GroupCodeKemuOrg>) mapArr.get("list");
                    return mapArr.get("code").equals("401") ? Mono.just(mapArr) : groupCodeKemuOrgRepository.saveAll(codelist).collectList().zipWith(groupCodeKemuOrgRepository.saveAll(kmlist).collectList()).map(t -> mapArr);
                })
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping("/sysCountryImportCoke")
    public Mono<R> sysCountryImportCoke(@RequestPart("file") FilePart filePartParm, @RequestPart("templateInfo") String templateInfo) throws Exception {
        // 获取表头个数
        AtomicInteger column = new AtomicInteger();
        String[] parameter = templateInfo.split(">>>");
        String uniqueAccStandard = parameter[0];
        String templateId = parameter[1];
        String jici = parameter[2];
        String accStyleUnique = parameter[3];
        Path tempFilePath = Files.createTempFile("", new String(filePartParm.filename().getBytes("ISO-8859-1"), "UTF-8"));
        return Mono.just(filePartParm)
                // 上传到临时目录
                .flatMap(filePart -> {
                    try {
                        return DataBufferUtils
                                .write(filePart.content(), AsynchronousFileChannel.open(tempFilePath, StandardOpenOption.WRITE), 0)
                                .doOnComplete(() -> log.info("上传成功"))
                                .collectList()
                                .map(item -> tempFilePath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return Mono.just("");
                })
                // 遍历导入excel内容
                .flatMap(objArr -> {
                    List<Object[]> list = null;
                    XlsUtils3 excelReader = new XlsUtils3(tempFilePath.toString());
                    String[] str = new String[]{"科目编码", "科目名称", "方向", "现金科目(是/空)", "银行科目(是/空)", ""};
                    list = excelReader.getExcelObj(tempFilePath.toString(), str, 0);
                    Arrays.stream(list.get(0)).forEach(v -> {
                        if (v != null) {
                            column.addAndGet(1);
                        }
                    });

                    assert tempFilePath != null;
                    try {
                        Files.delete(tempFilePath);
                    } catch (IOException e) {
                    }
                    return Mono.just(list);
                })
                .flatMap(item -> {
                    Map mapArr = new HashMap();
                    mapArr.put("excellist", item);   // excel中内容
                    mapArr.put("code", "200");
                    mapArr.put("error", "");
                    return groupCodeKemuCountryRepository.findAllByUniqueAccStandardAndTemplateIdOrderByCcodeAsc(uniqueAccStandard, templateId)
                            .collectList()
                            .flatMap(list -> {
                                mapArr.put("kmlist", list);
                                return accStyleRepository.findAllStyleByUnique(accStyleUnique).collectList().flatMap(accStyle -> {
                                    mapArr.put("kmstyle", accStyle);
                                    return Mono.just(mapArr);
                                });
                            });
                })
                // 判断科目级次是否合法
                .flatMap(mapArr -> {
                    // 科目list
                    List<GroupCodeKemuCountry> kmlist = (List<GroupCodeKemuCountry>) mapArr.get("kmlist");
                    // 科目类型list
                    List<AccStyle> kmstyle = (List<AccStyle>) mapArr.get("kmstyle");
                    // 文件list
                    List<Object[]> excellist = (List<Object[]>) mapArr.get("excellist");
                    List<GroupCodeKemuCountry> codelist = new ArrayList<>();

                    // 判断是否重复
                    for (int i = 1; i < excellist.size(); i++) {

                        // 级次封装
                        List<Map> jicilist = new ArrayList<>();
                        int test = 0;
                        for (int j = 0; j < jici.split("-").length; j++) {
                            test += Integer.valueOf(jici.split("-")[j]);
                            jicilist.add(CollectOfUtils.mapof("jicilength", test, "jicinum", jici.split("-")[j], "level", j + 1));
                        }

                        int a = column.get();
                        Object[] obj = excellist.get(i);
                        int finalI = i;
                        List<String> errorText = new ArrayList<>();
                        // 科目编码是否重复
                        if (StringUtils.isNotBlank(excellist.get(finalI)[0].toString().trim())) {
                            List<GroupCodeKemuCountry> collect = kmlist.stream().filter(v -> v.getCcode().equals(excellist.get(finalI)[0].toString())).collect(Collectors.toList());
                            if (collect.size() > 0) {
                                errorText.add("科目编码与当前已存在档案重复");
                                obj[a] = errorText.toString();
                                if (codelist.size() > 0) {
                                    codelist.remove(0);
                                }
                            }

                            List<Map> findByjici = jicilist.stream().filter(ji -> ji.get("jicilength").toString().equals(String.valueOf(excellist.get(finalI)[0].toString().length()))).collect(Collectors.toList());
                            if (findByjici.size() == 0) {
                                errorText.add("科目编码不符合系统科目级次设置规则");
                                obj[a] = errorText.toString();
                            }
                        } else {
                            errorText.add("科目编码不能为空");
                            obj[a] = errorText.toString();
                            if (codelist.size() > 0) {
                                codelist.remove(0);
                            }
                        }
                        // 科目名称是否重复
                        if (StringUtils.isNotBlank(excellist.get(finalI)[1].toString().trim())) {
                            List<GroupCodeKemuCountry> collect = kmlist.stream().filter(v -> v.getCcodeName().equals(excellist.get(finalI)[1].toString())).collect(Collectors.toList());
                            if (collect.size() > 0) {
                                errorText.add("科目名称与当前已存在档案重复");
                                obj[a] = errorText.toString();
                                if (codelist.size() > 0) {
                                    codelist.remove(0);
                                }
                            }
                        } else {
                            errorText.add("科目名称不能为空");
                            obj[a] = errorText.toString();
                            if (codelist.size() > 0) {
                                codelist.remove(0);
                            }
                        }
                        // 科目方向
                        if (StringUtils.isBlank(excellist.get(finalI)[2].toString().trim())) {
                            errorText.add("科目方向不能为空");
                            obj[a] = errorText.toString();
                            if (codelist.size() > 0) {
                                codelist.remove(0);
                            }
                        } else {
                            if (!excellist.get(finalI)[2].toString().contains("借") && !excellist.get(finalI)[2].toString().contains("贷")) {
                                errorText.add("未知科目方向");
                                obj[a] = errorText.toString();
                                if (codelist.size() > 0) {
                                    codelist.remove(0);
                                }
                            }
                        }

                        if (errorText.size() == 0) {
                            // 科目编码
                            String ccode = excellist.get(i)[0].toString().trim();
                            String ccodeName = excellist.get(i)[1].toString().trim();
                            String bprogerty = excellist.get(i)[2].toString().trim().contains("借") ? "1" : "0";
                            // 现金账
                            String bcash = excellist.get(i)[3] == null ? "0" : excellist.get(i)[3].toString().trim().equals("是") ? "1" : excellist.get(i)[3].toString().trim().equals("1") ? "1" : "0";
                            // 银行账
                            String bbank = excellist.get(i)[4] == null ? "0" : excellist.get(i)[4].toString().trim().equals("是") ? "1" : excellist.get(i)[4].toString().trim().equals("1") ? "1" : "0";
                            // 现金等价物
                            String bcashEquivalence = "0";
                            // 数量核算
                            String bnum = "0";
                            // 计量单位
                            String menterage = "";
                            // 外币核算
                            String currency = "0";
                            // 外币名称
                            String currencyType = "";
                            // 个人往来
                            String bperson = "0";
                            // 客户往来
                            String bcus = "0";
                            // 供应商往来
                            String bsup = "0";
                            // 部门核算
                            String bdept = "0";
                            // 项目核算
                            String bitem = "0";

                            String firstWith = ccode.substring(0, 1);
                            // 检查科目首数字是否在此范围内
                            List<AccStyle> findByStyle = kmstyle.stream().filter(s -> s.getOrder().equals(firstWith)).collect(Collectors.toList());
                            if (findByStyle.size() == 0) {
                                errorText.add("科目类型不存在");
                                obj[a] = errorText.toString();
                                if (codelist.size() > 0) {
                                    codelist.remove(0);
                                }
                            }

                            // 判断科目编码是否符合级次
                            int jicinum = 0;
                            String level = "";
                            List<Map> findByjici = jicilist.stream().filter(ji -> ji.get("jicilength").toString().equals(String.valueOf(ccode.length()))).collect(Collectors.toList());
                            if (findByjici.size() > 0) {
                                jicinum = Integer.valueOf(findByjici.get(0).get("jicinum").toString());
                                level = findByjici.get(0).get("level").toString();
                            }
                            GroupCodeKemuCountry ck = new GroupCodeKemuCountry();
                            ck
                                    .setCcode(ccode)
                                    .setCcodeName(ccodeName)
                                    .setBprogerty(bprogerty)
                                    .setBcash(bcash)
                                    .setBbank(bbank)
                                    .setBcashEquivalence(bcashEquivalence)
                                    .setBnum(bnum)
                                    .setMenterage(menterage)
                                    .setCurrency(currency)
                                    .setCurrencyType(currencyType)
                                    .setBperson(bperson)
                                    .setBcus(bcus)
                                    .setBsup(bsup)
                                    .setBdept(bdept)
                                    .setBitem(bitem)
                                    .setUniqueCode(ck.getCcode())
                                    .setUniqueAccStandard(uniqueAccStandard)
                                    .setTemplateId(templateId)
                                    .setCbookType("金额式")
                                    .setFlag("1")
                                    .setBend("1")
                                    .setFuzhuControl("0")
                                    .setLowerControl("0")
                                    .setIgrade(level)
                                    .setSuperiorCcode("0")
                                    .setBdaybook("0")
                                    .setControlled("0")
                                    .setYusuan("0")
                                    .setPxjz("0")
                                    .setXjll("0")
                                    .setCyfx("0")
                                    .setCdfine1("0")
                                    .setCdfine2("0")
                                    .setCdfine3("0")
                                    .setCdfine4("0")
                                    .setCdfine5("0")
                                    .setCdfine6("0")
                                    .setCdfine7("0")
                                    .setCdfine8("0")
                                    .setCdfine9("0")
                                    .setCdfine10("0")
                                    .setCdfine11("0")
                                    .setCdfine12("0")
                                    .setCdfine13("0")
                                    .setCdfine14("0")
                                    .setCdfine15("0")
                                    .setCdfine16("0")
                                    .setCdfine17("0")
                                    .setCdfine18("0")
                                    .setCdfine19("0")
                                    .setCdfine20("0")
                                    .setCdfine21("0")
                                    .setCdfine22("0")
                                    .setCdfine23("0")
                                    .setCdfine24("0")
                                    .setCdfine25("0")
                                    .setCdfine26("0")
                                    .setCdfine27("0")
                                    .setCdfine28("0")
                                    .setCdfine29("0")
                                    .setCdfine30("0");

                            // 根据科目编码首位查找科目类型
                            List<AccStyle> accStyleStream = kmstyle.stream().filter(s -> s.getOrder().equals(ccode.substring(0, 1))).collect(Collectors.toList());
                            if (accStyleStream.size() > 0) ck.setCclass(accStyleStream.get(0).getCclass());
                            ck.setYusuan(StrUtil.isNotBlank(accStyleStream.get(0).getFlagYusuan()) ? "1" : "0");
                            codelist.add(ck);
                            // 算出上级科目
                            String suprtcode = ccode.substring(0, ccode.length() - jicinum);
                            if (StringUtils.isNotBlank(suprtcode)) {
                                // 修改上级科目末级状态
                                codelist.stream().forEach(km -> {
                                    if (km.getCcode().equals(excellist.get(finalI)[0].toString())) {
                                        km.setSuperiorCcode(suprtcode);
                                    }
                                    if (km.getCcode().equals(suprtcode)) {
                                        km.setBend("0");
                                    }
                                });

                                // 判断 上级科目是否存在表中
                                List<GroupCodeKemuCountry> findBySuperCode = kmlist.stream().filter(km -> km.getCcode().equals(suprtcode)).collect(Collectors.toList());
                                if (findBySuperCode.size() > 0) {
                                    // 获取同级别的科目编码
                                    List<String> equalsLevelCode = new ArrayList<>();
                                    for (int j = 1; j < excellist.size(); j++) {
                                        if (excellist.get(j)[0].toString().length() == findBySuperCode.get(0).getCcode().length()) {
                                            equalsLevelCode.add(excellist.get(j)[0].toString());
                                        }
                                    }

                                    // 去重后的同级别科目编码
                                    List<String> distinctNumlist = equalsLevelCode.stream().distinct().collect(Collectors.toList());
                                    if (equalsLevelCode.size() != distinctNumlist.size()) {
                                        errorText.add("科目的科目编码在系统中重复");
                                        obj[a] = errorText.toString();
                                        if (codelist.size() > 0) {
                                            codelist.remove(0);
                                        }
                                    }
                                }
                                // 判断 上级科目是否存在模板中
                                List<Object[]> findBySuperCodeEcxel = excellist.stream().filter(km -> km[0].equals(suprtcode)).collect(Collectors.toList());
                                if (findBySuperCodeEcxel.size() > 0) {
                                    // 获取下级科目编码
                                    List<String> equalsLevelCode = new ArrayList<>();
                                    // 获取下级科目名称;用于判断文件中下级科目名称是否重复
                                    List<String> equalsLevelCodeName = new ArrayList<>();
                                    for (int j = 1; j < excellist.size(); j++) {
                                        if (excellist.get(j)[0].toString().indexOf(suprtcode) != -1 && !excellist.get(j)[0].toString().equals(suprtcode)) {
                                            equalsLevelCode.add(excellist.get(j)[0].toString());
                                        }
                                    }

                                    // 把期初、凭证的上级科目修改成下级第一个科目
                                    if (equalsLevelCode.size() > 0) {
                                        // 修改科目末级状态
                                        kmlist.stream().forEach(km -> {
                                            if (km.getCcode().equals(suprtcode)) {
                                                if (!km.getSuperiorCcode().equals("0")) {
                                                    km.setBend("0");
                                                }
                                            }
                                        });
                                    }
                                }
                                if (findBySuperCode.size() == 0 && findBySuperCodeEcxel.size() == 0) {
                                    errorText.add("上级科目编码在系统中不存在");
                                    obj[a] = errorText.toString();
                                    if (codelist.size() > 0) {
                                        if (codelist.size() > 0) {
                                            codelist.remove(0);
                                        }
                                    }
                                }
                            }
                        } else {
                            mapArr.put("code", "200");
                            mapArr.put("error", "error");
                        }
                    }
                    mapArr.put("list", codelist);
                    mapArr.put("kmlist", kmlist);
                    mapArr.put("column", column.get());
                    return Mono.just(mapArr);
                })
                .flatMapMany(mapArr -> {
                    // 科目list
                    List<GroupCodeKemuCountry> kmlist = (List<GroupCodeKemuCountry>) mapArr.get("kmlist");
                    // 往数据库导入科目
                    List<GroupCodeKemuCountry> codelist = (List<GroupCodeKemuCountry>) mapArr.get("list");
                    return mapArr.get("code").equals("401") ? Mono.just(mapArr) : groupCodeKemuCountryRepository.saveAll(codelist).collectList().zipWith(groupCodeKemuCountryRepository.saveAll(kmlist).collectList()).map(t -> mapArr);
                })
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    /**
     * @return reactor.core.publisher.Mono<org.springbooz.core.tool.result.R>
     * @Author myh
     * @Description 集团会计科目-添加任务表
     * @Date 14:01 2021/7/14
     * @Param [id, iyear, username, text, condition]
     **/
    @PostMapping("saveGroupCodeTask")
    public Mono<R> saveGroupCodeTask(String id, String username, String accstyle, String templateid) {
        Calendar cal = Calendar.getInstance();
        String year = String.valueOf(cal.get(Calendar.YEAR));
        Task task = new Task();
        if (!id.equals("null")) {
            task.setId(id);
        }
        Map map = new HashMap();
        if (StringUtils.isNotBlank(accstyle)) {
            map.put("accstandard", accstyle);
            map.put("templateid", templateid);
        }
        task.setCaozuoUnique(username).setFunctionModule("集团会计科目").setRecordNum(new JSONObject(map).toString()).setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).setState("1").setIyear(year);
        return taskRepository.save(task).map(o -> R.ok().setResult(o));
    }

    @PostMapping("findByIyearAndBendCode")
    public Mono findByIyearAndBendCode(String uniqueAccStandard, String templateId, String bend) {
        return groupCodeKemuRepository.findByIyearAndBendCode(uniqueAccStandard, templateId, bend).collectList().map(a -> R.ok().setResult(a));
    }

    @PostMapping("findByCcode")
    public Mono<R> findByCcode(String ccode, String templateID) {
        return groupCodeKemuRepository.findByCcodeAndTemplateId(ccode, templateID)
                .flatMap(item -> {
                    // 5个辅助项
                    String fuzhu = "";
                    if (StringUtils.isNotBlank(item.getBperson()) && item.getBperson().equals("1")) {
                        fuzhu += "个人,";
                    }
                    if (StringUtils.isNotBlank(item.getBdept()) && item.getBdept().equals("1")) {
                        fuzhu += "部门,";
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
                    if (StringUtils.isNotBlank(item.getYsYsly()) && item.getYsYsly().equals("1")) {
                        fuzhu += "预算来源,";
                    }
                    if (StringUtils.isNotBlank(item.getYsZcgnfl()) && item.getYsZcgnfl().equals("1")) {
                        fuzhu += "支出功能分类,";
                    }
                    if (StringUtils.isNotBlank(item.getYsZfzcjjfl()) && item.getYsZfzcjjfl().equals("1")) {
                        fuzhu += "政府支出经济分类,";
                    }
                    if (StringUtils.isNotBlank(item.getYsBmzcjjfl()) && item.getYsBmzcjjfl().equals("1")) {
                        fuzhu += "部门支出经济分类,";
                    }
                    if (!"".equals(fuzhu)) {
                        item.setFuzhu(fuzhu.substring(0, fuzhu.length() - 1));
                    }

                    String finalFuzhu = StrUtil.isNotBlank(fuzhu) ? fuzhu.substring(0, fuzhu.length() - 1) : "";
                    item.setFuzhu(finalFuzhu);
                    return Mono.just(item);
                })
                .map(o -> R.ok().setResult(o)).defaultIfEmpty(R.ok().setResult("不存在"));
    }

    @PostMapping("findByOrgCcode")
    public Mono<R> findByOrgCcode(String ccode, String orgUnique, String iyear) {
        return groupCodeKemuOrgRepository.findAllByCcodeAndOrgUniqueAndIyear(ccode, orgUnique, iyear)
                .flatMap(item -> {
                    // 5个辅助项
                    String fuzhu = "";
                    if (StringUtils.isNotBlank(item.getBperson()) && item.getBperson().equals("1")) {
                        fuzhu += "个人,";
                    }
                    if (StringUtils.isNotBlank(item.getBdept()) && item.getBdept().equals("1")) {
                        fuzhu += "部门,";
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
                    if (StringUtils.isNotBlank(item.getYsYsly()) && item.getYsYsly().equals("1")) {
                        fuzhu += "预算来源,";
                    }
                    if (StringUtils.isNotBlank(item.getYsZcgnfl()) && item.getYsZcgnfl().equals("1")) {
                        fuzhu += "支出功能分类,";
                    }
                    if (StringUtils.isNotBlank(item.getYsZfzcjjfl()) && item.getYsZfzcjjfl().equals("1")) {
                        fuzhu += "政府支出经济分类,";
                    }
                    if (StringUtils.isNotBlank(item.getYsBmzcjjfl()) && item.getYsBmzcjjfl().equals("1")) {
                        fuzhu += "部门支出经济分类,";
                    }
                    if (!"".equals(fuzhu)) {
                        item.setFuzhu(fuzhu.substring(0, fuzhu.length() - 1));
                    }

                    String finalFuzhu = StrUtil.isNotBlank(fuzhu) ? fuzhu.substring(0, fuzhu.length() - 1) : "";
                    item.setFuzhu(finalFuzhu);
                    return Mono.just(item);
                })
                .map(o -> R.ok().setResult(o)).defaultIfEmpty(R.ok().setResult("不存在"));
    }

    @PostMapping("findBySuperCodeName")
    public Mono<R> findBySuperCodeName(String uniqueAccStandard, String templateId, String ccode, String superCcod, String iyear) {
        return groupCodeKemuRepository.countByNameSuperCode(uniqueAccStandard, templateId, ccode, superCcod + "%", iyear).map(o -> R.ok().setResult(o));
    }

    @PostMapping("findByLowerMaxCodeNum")
    public Mono<R> findByLowerMaxCodeNum(String templateId, String superiorCcode, String code_length) {
        // 获取所有下级
        return groupCodeKemuRepository.countBySuperiorCcodeAndTemplateId(superiorCcode, templateId)
                .flatMap(size -> {
                    return size > 0 ? groupCodeKemuRepository.findBySuperiorCcodeStrAgg(superiorCcode, templateId)
                            .flatMap(superiorCcodeAgg -> {
                                // 获取所有下级
                                List<Integer> list = new ArrayList<>();
                                for (int i = 0; i < superiorCcodeAgg.split(",").length; i++) {
                                    // 不包含字母的科目编码
                                    if (!RandomNum.judgeContainsStr(superiorCcodeAgg.split(",")[i])) {
                                        list.add(Integer.valueOf(superiorCcodeAgg.split(",")[i].substring(superiorCcode.length())));
                                    }
                                }
                                List<Integer> list2 = new ArrayList<>();
                                for (int i = 0; i < 100; i++) {
                                    list2.add(i + 1);
                                }
                                // 查找科目编码 中间 不连续，得出最小值;等于0 说明到头了【最大99】
                                int minCode = list2.stream().filter(item -> !list.contains(item)).mapToInt(Integer::intValue).min().orElse(0);
                                String ccode = minCode == 0 ? "" : superiorCcode + String.format("%0" + code_length + "d", minCode);
                                return Mono.just(ccode);
                            }) : Mono.just(superiorCcode + String.format("%0" + code_length + "d", 1));
                }).map(o -> R.ok().setResult(o));
    }

    @PostMapping("findByOrgLowerMaxCodeNum")
    public Mono<R> findByOrgLowerMaxCodeNum(String orgUnique, String superiorCcode, String code_length, String iyear) {
        // 获取所有下级
        return groupCodeKemuOrgRepository.countBySuperiorCcodeAndOrgUniqueAndIyear(superiorCcode, orgUnique, iyear)
                .flatMap(size -> {
                    return size > 0 ? groupCodeKemuOrgRepository.findBySuperiorCcodeStrAgg(superiorCcode, orgUnique, iyear)
                            .flatMap(superiorCcodeAgg -> {
                                // 获取所有下级
                                List<Integer> list = new ArrayList<>();
                                for (int i = 0; i < superiorCcodeAgg.split(",").length; i++) {
                                    // 不包含字母的科目编码
                                    if (!RandomNum.judgeContainsStr(superiorCcodeAgg.split(",")[i])) {
                                        list.add(Integer.valueOf(superiorCcodeAgg.split(",")[i].substring(superiorCcode.length())));
                                    }
                                }
                                List<Integer> list2 = new ArrayList<>();
                                for (int i = 0; i < 100; i++) {
                                    list2.add(i + 1);
                                }
                                // 查找科目编码 中间 不连续，得出最小值;等于0 说明到头了【最大99】
                                int minCode = list2.stream().filter(item -> !list.contains(item)).mapToInt(Integer::intValue).min().orElse(0);
                                String ccode = minCode == 0 ? "" : superiorCcode + String.format("%0" + code_length + "d", minCode);
                                return Mono.just(ccode);
                            }) : Mono.just(superiorCcode + String.format("%0" + code_length + "d", 1));
                }).map(o -> R.ok().setResult(o));
    }

    // 修改组织科目状态
    @PostMapping("editOrgCodeKemuFlag")
    public Mono<R> editOrgCodeKemuFlag(@RequestBody Map map) {
        String flag = map.get("flag").toString();
        String iyear = map.get("iyear").toString();
        String orgUnique = map.get("orgUnique").toString();
        List<String> ccode = Arrays.asList(map.get("ccode").toString().split(","));
        return groupCodeKemuOrgRepository.editOrgCodeKemuFlag(flag, ccode, iyear, orgUnique).then(Mono.just(R.ok()));
    }

    // 查询科目在组织的账套
    @PostMapping("findByOrgAccountCodeKemu")
    public Mono<R> findByOrgAccountCodeKemu(String orgUnique, String iyear, String ccode) {
        return groupCodeKemuOrgRepository.findByOrgAccountCodeKemu(orgUnique, iyear, ccode).collectList().map(a -> R.ok().setResult(a));
    }

    // 查询科目在组织的账套中是否有下级、期初或凭证
    @PostMapping("findByOrgAccountCodeKemuSuperCodeAccvoucher")
    public Mono<R> findByOrgAccountCodeKemuSuperCodeAccvoucher(String orgUnique, String iyear, String ccode) {
        return groupCodeKemuOrgRepository.findByOrgAccountCodeKemuSuperCodeAccvoucher(orgUnique, iyear, ccode).collectList().map(a -> R.ok().setResult(a));
    }

    @PostMapping("findByAccountCodeKemuTask")
    public Mono<R> findByAccountCodeKemuTask(String orgUnique, String functionModule) {
        return groupCodeKemuOrgRepository.findByAccountCodeKemuTask(orgUnique, functionModule).collectList().map(a -> R.ok().setResult(a));
    }

    // 获取组织会计科目  xxxx 至 xxxx
    @PostMapping("findByOrgKemuCodeToCode")
    public Mono<R> findByOrgKemuCodeToCode(String orgUnique, String iyear, String parentCode, String ccode) {
        return groupCodeKemuOrgRepository.findByOrgKemuCodeLike(orgUnique, iyear, parentCode + "%").collectList()
                .flatMap(orgKemuLike -> {
                    List<GroupCodeKemuOrg> list = new ArrayList<>();
                    // 上级科目去重
                    List<String> superCode = orgKemuLike.stream().filter(a -> !a.getSuperiorCcode().equals("0")).map(GroupCodeKemuOrg::getSuperiorCcode).distinct().collect(Collectors.toList());

                    superCode.forEach(s -> {
                        List<GroupCodeKemuOrg> collect = orgKemuLike.stream().filter(o -> o.getCcode().equals(s)).collect(Collectors.toList());
                        if (collect.size() > 0) {
                            list.add(collect.get(0));
                        }
                    });
                    List<GroupCodeKemuOrg> collect = orgKemuLike.stream().filter(o -> o.getCcode().equals(ccode)).collect(Collectors.toList());
                    if (collect.size() > 0) {
                        list.add(collect.get(0));
                    }
                    return Mono.just(list);
                })
                .map(a -> R.ok().setResult(a));
    }


    /******************************************** 获取账套数据 ***********************************************************/
    /**
     * 查询单个科目信息
     *
     * @param ccode 科目编码
     * @return
     */

    @PostMapping("company/company_findByCcode")
    public Mono<R> company_findByCcode(String ccode, String iyear) {
        return codeKemuRepository.findByCcodeAndIyear(ccode, iyear)
                .flatMap(item -> {
                    // 5个辅助项
                    String fuzhu = "";
                    if (StringUtils.isNotBlank(item.getBperson()) && item.getBperson().equals("1")) {
                        fuzhu += "个人,";
                    }
                    if (StringUtils.isNotBlank(item.getBdept()) && item.getBdept().equals("1")) {
                        fuzhu += "部门,";
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
                    if (StringUtils.isNotBlank(item.getYsYsly()) && item.getYsYsly().equals("1")) {
                        fuzhu += "预算来源,";
                    }
                    if (StringUtils.isNotBlank(item.getYsZcgnfl()) && item.getYsZcgnfl().equals("1")) {
                        fuzhu += "支出功能分类,";
                    }
                    if (StringUtils.isNotBlank(item.getYsZfzcjjfl()) && item.getYsZfzcjjfl().equals("1")) {
                        fuzhu += "政府支出经济分类,";
                    }
                    if (StringUtils.isNotBlank(item.getYsBmzcjjfl()) && item.getYsBmzcjjfl().equals("1")) {
                        fuzhu += "部门支出经济分类,";
                    }
                    if (!"".equals(fuzhu)) {
                        item.setFuzhu(fuzhu.substring(0, fuzhu.length() - 1));
                    }
                    String finalFuzhu = fuzhu;
                    return fuzhuHesuanRepository.countAllBy().flatMap(size -> {
                        return size == 0 ? Mono.just(item) : fuzhuHesuanRepository.findAll().collectList()
                                .flatMap(fuzhuhesuan -> {
                                    Map byZdyFuZhu = findByZdyFuZhu(fuzhuhesuan, item);
                                    item.setFuzhu(byZdyFuZhu.get("result").toString()).setFuzhuhesuan(byZdyFuZhu.get("resultStr").toString());
                                    return Mono.just(item);
                                });
                    });
                })
                .map(o -> R.ok().setResult(o)).defaultIfEmpty(R.ok().setResult("不存在"));
    }

    /**
     * 获取下级科目最新科目
     *
     * @param superiorCcode 上级科目编码
     * @return
     */

    @PostMapping("company/company_findByLowerMaxCodeNum")
    public Mono<R> company_findByLowerMaxCodeNum(String superiorCcode, String code_length) {
        // 获取所有下级
        return codeKemuRepository.countBySuperiorCcode(superiorCcode)
                .flatMap(size -> {
                    return size > 0 ? codeKemuRepository.findBySuperiorCcodeStrAgg(superiorCcode)
                            .flatMap(superiorCcodeAgg -> {
                                // 获取所有下级
                                List<Integer> list = new ArrayList<>();
                                for (int i = 0; i < superiorCcodeAgg.split(",").length; i++) {
                                    // 不包含字母的科目编码
                                    if (!RandomNum.judgeContainsStr(superiorCcodeAgg.split(",")[i])) {
                                        list.add(Integer.valueOf(superiorCcodeAgg.split(",")[i].substring(superiorCcode.length())));
                                    }
                                }
                                List<Integer> list2 = new ArrayList<>();
                                for (int i = 0; i < 100; i++) {
                                    list2.add(i + 1);
                                }
                                // 查找科目编码 中间 不连续，得出最小值;等于0 说明到头了【最大99】
                                int minCode = list2.stream().filter(item -> !list.contains(item)).mapToInt(Integer::intValue).min().orElse(0);
                                String ccode = minCode == 0 ? "" : superiorCcode + String.format("%0" + code_length + "d", minCode);
                                return Mono.just(ccode);
                            }) : Mono.just(superiorCcode + String.format("%0" + code_length + "d", 1));
                }).map(o -> R.ok().setResult(o));
    }


    @PostMapping("company/company_findByCodeNum")
    public Mono<R> company_findByCodeNum(@RequestBody Map map) {
        String ccode=map.get("ccode").toString();
        String iyear=map.get("iyear").toString();
        return codeKemuRepository.countByCcode(ccode, iyear).map(o -> R.ok().setResult(o));
    }


    @PostMapping("company/company_findByCodeName")
    public Mono<R> company_findByCodeName(@RequestBody Map map) {
        String ccodename=map.get("ccodename").toString();
        String iyear=map.get("iyear").toString();
        return codeKemuRepository.countByCcodeName(ccodename, iyear).map(o -> R.ok().setResult(o));
    }

    @PostMapping("company/company_findBySuperCodeName")
    public Mono<R> company_findBySuperCodeName(@RequestBody Map map) {
        String ccodename=map.get("ccodename").toString();
        String superCcod=map.get("superCcod").toString();
        String iyear=map.get("iyear").toString();
        return codeKemuRepository.countByNameSuperCode(iyear, ccodename,superCcod + "%").map(o -> R.ok().setResult(o));
    }

    /**
     * 账套增加科目
     *
     * @param km
     * @return
     */
    @Transactional
    @PostMapping("/company/company_saveCodekemu")
    public Mono company_saveCodekemu(@RequestBody CodeKemu km, String iyear) {
        Calendar date = Calendar.getInstance();
        if (StringUtils.isBlank(km.getUniqueCode())) {
            km.setUniqueCode(RandomNum.uuid());
        }
        km
                .setCbookType("金额式")
                .setSuperiorCcode(StringUtils.isBlank(km.getSuperiorCcode()) ? "0" : km.getSuperiorCcode())
                .setFlag(StringUtils.isNotBlank(km.getFlag()) ? "1" : "0")
                .setBnum(StringUtils.isNotBlank(km.getBnum()) ? km.getBnum() : "0")
                .setCurrency(StringUtils.isNotBlank(km.getCurrency()) ? km.getCurrency() : "0")
                .setBcash(StringUtils.isNotBlank(km.getBcash()) ? km.getBcash() : "0")
                .setBbank(StringUtils.isNotBlank(km.getBbank()) ? km.getBbank() : "0")
                .setBcashEquivalence(StringUtils.isNotBlank(km.getBcashEquivalence()) ? km.getBcashEquivalence() : "0")
                .setBdaybook(StringUtils.isNotBlank(km.getBdaybook()) ? km.getBdaybook() : "0")
                .setPxjz(StringUtils.isNotBlank(km.getPxjz()) ? km.getPxjz() : "0")
                .setXjll(StringUtils.isNotBlank(km.getXjll()) ? km.getXjll() : "0")
                .setCyfx(StringUtils.isNotBlank(km.getCyfx()) ? km.getCyfx() : "0")
                .setControlled(StringUtils.isNotBlank(km.getControlled()) ? km.getControlled() : "0")
                .setYusuan(km.getYusuan())
                .setTenantId(km.getTenantId())
                .setLowerControl(StrUtil.isBlank(km.getLowerControl())?"0":km.getLowerControl())
                .setFuzhuControl(StrUtil.isBlank(km.getFuzhuControl())?"0":km.getFuzhuControl())
                .setCcode(km.getCcode().trim().toUpperCase())
                .setCcodeName(km.getCcodeName().trim().toUpperCase())
                .setIsDel("0")
                .setYsBmzcjjfl("0")
                .setYsYsly("0")
                .setYsZcgnfl("0")
                .setYsZfzcjjfl("0")
                .setBperson("0")
                .setBcus("0")
                .setBsup("0")
                .setBdept("0")
                .setBitem("0")
                .setCdfine1("0")
                .setCdfine2("0")
                .setCdfine3("0")
                .setCdfine4("0")
                .setCdfine5("0")
                .setCdfine6("0")
                .setCdfine7("0")
                .setCdfine8("0")
                .setCdfine9("0")
                .setCdfine10("0")
                .setCdfine11("0")
                .setCdfine12("0")
                .setCdfine13("0")
                .setCdfine14("0")
                .setCdfine15("0")
                .setCdfine16("0")
                .setCdfine17("0")
                .setCdfine18("0")
                .setCdfine19("0")
                .setCdfine20("0")
                .setCdfine21("0")
                .setCdfine22("0")
                .setCdfine23("0")
                .setCdfine24("0")
                .setCdfine25("0")
                .setCdfine26("0")
                .setCdfine27("0")
                .setCdfine28("0")
                .setCdfine29("0")
                .setCdfine30("0");

        if (StringUtils.isNotBlank(km.getFuzhuhesuan())) {
            for (int i = 0; i < km.getFuzhuhesuan().split(",").length; i++) {
                if (km.getFuzhuhesuan().split(",")[i].equals("cperson_id")) {                    // 个人
                    km.setBperson("1");
                } else if (km.getFuzhuhesuan().split(",")[i].equals("cdept_id")) {             // 部门
                    km.setBdept("1");
                } else if (km.getFuzhuhesuan().split(",")[i].equals("project_id")) {            // '项目'
                    km.setBitem("1");
                } else if (km.getFuzhuhesuan().split(",")[i].equals("ccus_id")) {             // '客户'
                    km.setBcus("1");
                } else if (km.getFuzhuhesuan().split(",")[i].equals("csup_id")) {             // '供应商'
                    km.setBsup("1");
                } else if (km.getFuzhuhesuan().split(",")[i].equals("ys_zcgnfl")) {             // '支出功能分类'
                    km.setYsZcgnfl("1");
                } else if (km.getFuzhuhesuan().split(",")[i].equals("ys_bmzcjjfl")) {             // '部门支出经济分类'
                    km.setYsBmzcjjfl("1");
                } else if (km.getFuzhuhesuan().split(",")[i].equals("ys_zfzcjjfl")) {             // '政府支出经济分类'
                    km.setYsZfzcjjfl("1");
                } else if (km.getFuzhuhesuan().split(",")[i].equals("ys_ysly")) {             // '预算来源'
                    km.setYsYsly("1");
                } else if (km.getFuzhuhesuan().split(",")[i].equals("cdfine1")) {
                    km.setCdfine1("1");
                } else if (km.getFuzhuhesuan().split(",")[i].equals("cdfine2")) {
                    km.setCdfine2("1");
                } else if (km.getFuzhuhesuan().split(",")[i].equals("cdfine3")) {
                    km.setCdfine3("1");
                } else if (km.getFuzhuhesuan().split(",")[i].equals("cdfine4")) {
                    km.setCdfine4("1");
                } else if (km.getFuzhuhesuan().split(",")[i].equals("cdfine5")) {
                    km.setCdfine5("1");
                } else if (km.getFuzhuhesuan().split(",")[i].equals("cdfine6")) {
                    km.setCdfine6("1");
                } else if (km.getFuzhuhesuan().split(",")[i].equals("cdfine7")) {
                    km.setCdfine7("1");
                } else if (km.getFuzhuhesuan().split(",")[i].equals("cdfine8")) {
                    km.setCdfine8("1");
                } else if (km.getFuzhuhesuan().split(",")[i].equals("cdfine9")) {
                    km.setCdfine9("1");
                } else if (km.getFuzhuhesuan().split(",")[i].equals("cdfine10")) {
                    km.setCdfine10("1");
                } else if (km.getFuzhuhesuan().split(",")[i].equals("cdfine11")) {
                    km.setCdfine11("1");
                } else if (km.getFuzhuhesuan().split(",")[i].equals("cdfine12")) {
                    km.setCdfine12("1");
                } else if (km.getFuzhuhesuan().split(",")[i].equals("cdfine13")) {
                    km.setCdfine13("1");
                } else if (km.getFuzhuhesuan().split(",")[i].equals("cdfine14")) {
                    km.setCdfine14("1");
                } else if (km.getFuzhuhesuan().split(",")[i].equals("cdfine15")) {
                    km.setCdfine15("1");
                } else if (km.getFuzhuhesuan().split(",")[i].equals("cdfine16")) {
                    km.setCdfine16("1");
                } else if (km.getFuzhuhesuan().split(",")[i].equals("cdfine17")) {
                    km.setCdfine17("1");
                } else if (km.getFuzhuhesuan().split(",")[i].equals("cdfine18")) {
                    km.setCdfine18("1");
                } else if (km.getFuzhuhesuan().split(",")[i].equals("cdfine19")) {
                    km.setCdfine19("1");
                } else if (km.getFuzhuhesuan().split(",")[i].equals("cdfine20")) {
                    km.setCdfine20("1");
                } else if (km.getFuzhuhesuan().split(",")[i].equals("cdfine21")) {
                    km.setCdfine21("1");
                } else if (km.getFuzhuhesuan().split(",")[i].equals("cdfine22")) {
                    km.setCdfine22("1");
                } else if (km.getFuzhuhesuan().split(",")[i].equals("cdfine23")) {
                    km.setCdfine23("1");
                } else if (km.getFuzhuhesuan().split(",")[i].equals("cdfine24")) {
                    km.setCdfine24("1");
                } else if (km.getFuzhuhesuan().split(",")[i].equals("cdfine25")) {
                    km.setCdfine25("1");
                } else if (km.getFuzhuhesuan().split(",")[i].equals("cdfine26")) {
                    km.setCdfine26("1");
                } else if (km.getFuzhuhesuan().split(",")[i].equals("cdfine27")) {
                    km.setCdfine27("1");
                } else if (km.getFuzhuhesuan().split(",")[i].equals("cdfine28")) {
                    km.setCdfine28("1");
                } else if (km.getFuzhuhesuan().split(",")[i].equals("cdfine29")) {
                    km.setCdfine29("1");
                } else if (km.getFuzhuhesuan().split(",")[i].equals("cdfine30")) {
                    km.setCdfine30("1");
                }
            }
        }
        else {
            km.setBperson("0")
                    .setBdept("0").setBitem("0").setBcus("0").setBsup("0").setYsZcgnfl("0").setYsBmzcjjfl("0").setYsZfzcjjfl("0").setYsYsly("0")
                    .setCdfine1("0")
                    .setCdfine2("0")
                    .setCdfine3("0")
                    .setCdfine4("0")
                    .setCdfine5("0")
                    .setCdfine6("0")
                    .setCdfine7("0")
                    .setCdfine8("0")
                    .setCdfine9("0")
                    .setCdfine10("0")
                    .setCdfine11("0")
                    .setCdfine12("0")
                    .setCdfine13("0")
                    .setCdfine14("0")
                    .setCdfine15("0")
                    .setCdfine16("0")
                    .setCdfine17("0")
                    .setCdfine18("0")
                    .setCdfine19("0")
                    .setCdfine20("0")
                    .setCdfine21("0")
                    .setCdfine22("0")
                    .setCdfine23("0")
                    .setCdfine24("0")
                    .setCdfine25("0")
                    .setCdfine26("0")
                    .setCdfine27("0")
                    .setCdfine28("0")
                    .setCdfine29("0")
                    .setCdfine30("0");
        }

        Mono yiji = Mono.just(km).flatMap(kms -> {
            //                                      期初、凭证替换成最新修改的本科目编码
            return codeKemuRepository.save(km).then(codeKemuRepository.updataAccvoucherCode(km.getCcode(), km.getCcodeName(), km.getOldCcode()).then(accvoucherService.editOrDel_AccVoucherOrQc(km.getCcode(), km.getListmap(), km).then(Mono.just(R.ok()))));
        });
        Mono yiji2 = Mono.just(km).flatMap(kms -> {
            return codeKemuRepository.findByCcode(kms.getSuperiorCcode(), iyear)
                    // 获取上级科目信息
                    .flatMap(code -> {
                        code.setBend("0");
                        // 修改上级科目末级标识
                        return codeKemuRepository.save(code)
                                .flatMap(km2 -> {
                                    // 上级科目是末级科目，期初、凭证中的本科目编码修改成第一个添加的下级科目
                                    Mono a = codeKemuRepository.save(km).then(codeKemuRepository.updataAccvoucherCode(km.getCcode(), km.getCcodeName(), km.getSuperiorCcode()).then(Mono.just(R.ok())));
                                    // 期初、凭证替换成最新修改的本科目编码
                                    Mono b = codeKemuRepository.save(km).then(codeKemuRepository.updataAccvoucherCode(km.getCcode(), km.getCcodeName(), km.getOldCcode()).then(Mono.just(R.ok())));
                                    return km.getSuperCodeBend().equals("1") ? a : b;
                                });
                    });
        });
        return km.getIgrade().equals("1") ? yiji : yiji2;
    }

    /**
     * 账套科目替换成组织科目
     *
     * @param km
     * @return
     */
    @Transactional
    @PostMapping("company/company_AccountKeMuReplaceOrgKeMu")
    public Mono<R> company_AccountKeMuReplaceOrgKeMu(@RequestBody CodeKemu km) {
        if (StringUtils.isBlank(km.getUniqueCode())) {
            km.setUniqueCode(RandomNum.uuid());
        }
        km
                .setIsDel("0")
                .setCcode(km.getCcode().toUpperCase())
                .setCcodeName(km.getCcodeName().toUpperCase())
                .setCbookType("金额式")
                .setSuperiorCcode(StringUtils.isBlank(km.getSuperiorCcode()) ? "0" : km.getSuperiorCcode())
                .setFlag(StringUtils.isNotBlank(km.getFlag()) ? "1" : "0")
                .setBnum(StringUtils.isNotBlank(km.getBnum()) ? km.getBnum() : "0")
                .setCurrency(StringUtils.isNotBlank(km.getCurrency()) ? km.getCurrency() : "0")
                .setBcash(StringUtils.isNotBlank(km.getBcash()) ? km.getBcash() : "0")
                .setBbank(StringUtils.isNotBlank(km.getBbank()) ? km.getBbank() : "0")
                .setBcashEquivalence(StringUtils.isNotBlank(km.getBcashEquivalence()) ? km.getBcashEquivalence() : "0")
                .setBdaybook(StringUtils.isNotBlank(km.getBdaybook()) ? km.getBdaybook() : "0")
                .setYsBmzcjjfl(StringUtils.isNotBlank(km.getYsBmzcjjfl()) ? km.getYsBmzcjjfl() : "0")
                .setYsYsly(StringUtils.isNotBlank(km.getYsYsly()) ? km.getYsYsly() : "0")
                .setYsZcgnfl(StringUtils.isNotBlank(km.getYsZcgnfl()) ? km.getYsZcgnfl() : "0")
                .setYsZfzcjjfl(StringUtils.isNotBlank(km.getYsZfzcjjfl()) ? km.getYsZfzcjjfl() : "0")
                .setBperson(StringUtils.isNotBlank(km.getBperson()) ? km.getBperson() : "0")
                .setBcus(StringUtils.isNotBlank(km.getBcus()) ? km.getBcus() : "0")
                .setBsup(StringUtils.isNotBlank(km.getBsup()) ? km.getBsup() : "0")
                .setBdept(StringUtils.isNotBlank(km.getBdept()) ? km.getBdept() : "0")
                .setBitem(StringUtils.isNotBlank(km.getBitem()) ? km.getBitem() : "0")
                .setPxjz(StringUtils.isNotBlank(km.getPxjz()) ? km.getPxjz() : "0")
                .setXjll(StringUtils.isNotBlank(km.getXjll()) ? km.getXjll() : "0")
                .setCyfx(StringUtils.isNotBlank(km.getCyfx()) ? km.getCyfx() : "0")
                .setControlled(StringUtils.isNotBlank(km.getControlled()) ? km.getControlled() : "0")
                .setYusuan(km.getYusuan())
                .setTenantId(km.getTenantId())
                .setLowerControl(km.getLowerControl())
                .setFuzhuControl(km.getFuzhuControl())
                .setCdfine1("0")
                .setCdfine2("0")
                .setCdfine3("0")
                .setCdfine4("0")
                .setCdfine5("0")
                .setCdfine6("0")
                .setCdfine7("0")
                .setCdfine8("0")
                .setCdfine9("0")
                .setCdfine10("0")
                .setCdfine11("0")
                .setCdfine12("0")
                .setCdfine13("0")
                .setCdfine14("0")
                .setCdfine15("0")
                .setCdfine16("0")
                .setCdfine17("0")
                .setCdfine18("0")
                .setCdfine19("0")
                .setCdfine20("0")
                .setCdfine21("0")
                .setCdfine22("0")
                .setCdfine23("0")
                .setCdfine24("0")
                .setCdfine25("0")
                .setCdfine26("0")
                .setCdfine27("0")
                .setCdfine28("0")
                .setCdfine29("0")
                .setCdfine30("0");

        if (StringUtils.isNotBlank(km.getFuzhuhesuan())) {
            for (int i = 0; i < km.getFuzhuhesuan().split(",").length; i++) {
                if (km.getFuzhuhesuan().split(",")[i].equals("cperson_id")) {                    // 个人
                    km.setBperson("1");
                } else if (km.getFuzhuhesuan().split(",")[i].equals("cdept_id")) {             // 部门
                    km.setBdept("1");
                } else if (km.getFuzhuhesuan().split(",")[i].equals("project_id")) {            // '项目'
                    km.setBitem("1");
                } else if (km.getFuzhuhesuan().split(",")[i].equals("ccus_id")) {             // '客户'
                    km.setBcus("1");
                } else if (km.getFuzhuhesuan().split(",")[i].equals("csup_id")) {             // '供应商'
                    km.setBsup("1");
                } else if (km.getFuzhuhesuan().split(",")[i].equals("ys_zcgnfl")) {             // '支出功能分类'
                    km.setYsZcgnfl("1");
                } else if (km.getFuzhuhesuan().split(",")[i].equals("ys_bmzcjjfl")) {             // '部门支出经济分类'
                    km.setYsBmzcjjfl("1");
                } else if (km.getFuzhuhesuan().split(",")[i].equals("ys_zfzcjjfl")) {             // '政府支出经济分类'
                    km.setYsZfzcjjfl("1");
                } else if (km.getFuzhuhesuan().split(",")[i].equals("ys_ysly")) {             // '预算来源'
                    km.setYsYsly("1");
                } else if (km.getFuzhuhesuan().split(",")[i].equals("cdfine1")) {
                    km.setCdfine1("1");
                } else if (km.getFuzhuhesuan().split(",")[i].equals("cdfine2")) {
                    km.setCdfine2("1");
                } else if (km.getFuzhuhesuan().split(",")[i].equals("cdfine3")) {
                    km.setCdfine3("1");
                } else if (km.getFuzhuhesuan().split(",")[i].equals("cdfine4")) {
                    km.setCdfine4("1");
                } else if (km.getFuzhuhesuan().split(",")[i].equals("cdfine5")) {
                    km.setCdfine5("1");
                } else if (km.getFuzhuhesuan().split(",")[i].equals("cdfine6")) {
                    km.setCdfine6("1");
                } else if (km.getFuzhuhesuan().split(",")[i].equals("cdfine7")) {
                    km.setCdfine7("1");
                } else if (km.getFuzhuhesuan().split(",")[i].equals("cdfine8")) {
                    km.setCdfine8("1");
                } else if (km.getFuzhuhesuan().split(",")[i].equals("cdfine9")) {
                    km.setCdfine9("1");
                } else if (km.getFuzhuhesuan().split(",")[i].equals("cdfine10")) {
                    km.setCdfine10("1");
                } else if (km.getFuzhuhesuan().split(",")[i].equals("cdfine11")) {
                    km.setCdfine11("1");
                } else if (km.getFuzhuhesuan().split(",")[i].equals("cdfine12")) {
                    km.setCdfine12("1");
                } else if (km.getFuzhuhesuan().split(",")[i].equals("cdfine13")) {
                    km.setCdfine13("1");
                } else if (km.getFuzhuhesuan().split(",")[i].equals("cdfine14")) {
                    km.setCdfine14("1");
                } else if (km.getFuzhuhesuan().split(",")[i].equals("cdfine15")) {
                    km.setCdfine15("1");
                } else if (km.getFuzhuhesuan().split(",")[i].equals("cdfine16")) {
                    km.setCdfine16("1");
                } else if (km.getFuzhuhesuan().split(",")[i].equals("cdfine17")) {
                    km.setCdfine17("1");
                } else if (km.getFuzhuhesuan().split(",")[i].equals("cdfine18")) {
                    km.setCdfine18("1");
                } else if (km.getFuzhuhesuan().split(",")[i].equals("cdfine19")) {
                    km.setCdfine19("1");
                } else if (km.getFuzhuhesuan().split(",")[i].equals("cdfine20")) {
                    km.setCdfine20("1");
                } else if (km.getFuzhuhesuan().split(",")[i].equals("cdfine21")) {
                    km.setCdfine21("1");
                } else if (km.getFuzhuhesuan().split(",")[i].equals("cdfine22")) {
                    km.setCdfine22("1");
                } else if (km.getFuzhuhesuan().split(",")[i].equals("cdfine23")) {
                    km.setCdfine23("1");
                } else if (km.getFuzhuhesuan().split(",")[i].equals("cdfine24")) {
                    km.setCdfine24("1");
                } else if (km.getFuzhuhesuan().split(",")[i].equals("cdfine25")) {
                    km.setCdfine25("1");
                } else if (km.getFuzhuhesuan().split(",")[i].equals("cdfine26")) {
                    km.setCdfine26("1");
                } else if (km.getFuzhuhesuan().split(",")[i].equals("cdfine27")) {
                    km.setCdfine27("1");
                } else if (km.getFuzhuhesuan().split(",")[i].equals("cdfine28")) {
                    km.setCdfine28("1");
                } else if (km.getFuzhuhesuan().split(",")[i].equals("cdfine29")) {
                    km.setCdfine29("1");
                } else if (km.getFuzhuhesuan().split(",")[i].equals("cdfine30")) {
                    km.setCdfine30("1");
                }
            }
        }
        else {
            km.setBperson("0")
                    .setBdept("0").setBitem("0").setBcus("0").setBsup("0").setYsZcgnfl("0").setYsBmzcjjfl("0").setYsZfzcjjfl("0").setYsYsly("0")
                    .setCdfine1("0")
                    .setCdfine2("0")
                    .setCdfine3("0")
                    .setCdfine4("0")
                    .setCdfine5("0")
                    .setCdfine6("0")
                    .setCdfine7("0")
                    .setCdfine8("0")
                    .setCdfine9("0")
                    .setCdfine10("0")
                    .setCdfine11("0")
                    .setCdfine12("0")
                    .setCdfine13("0")
                    .setCdfine14("0")
                    .setCdfine15("0")
                    .setCdfine16("0")
                    .setCdfine17("0")
                    .setCdfine18("0")
                    .setCdfine19("0")
                    .setCdfine20("0")
                    .setCdfine21("0")
                    .setCdfine22("0")
                    .setCdfine23("0")
                    .setCdfine24("0")
                    .setCdfine25("0")
                    .setCdfine26("0")
                    .setCdfine27("0")
                    .setCdfine28("0")
                    .setCdfine29("0")
                    .setCdfine30("0");
        }
        return codeKemuRepository.save(km).map(a -> R.ok().setResult(a));
    }

    /**
     * 账套删除科目
     *
     * @param id 就是科目编码
     * @return
     */
    @Transactional
    @PostMapping("company/company_delCodekemu")
    public Mono<R> company_delCodekemu(String id, String iyear, String userName, Boolean independent) {
        return codeKemuRepository.findAllByIyear(iyear).collectList()
                .flatMap(codelist123 -> {
                    List<CodeKemu> codelist = codelist123;
                    // 可以删除的科目
                    List<CodeKemu> codelist456 = new ArrayList<>();
                    // 修改上级为末级的科目
                    List<CodeKemu> codelist789 = new ArrayList<>();

                    for (int i = 0; i < id.split(",").length; i++) {
                        String code = id.split(",")[i].trim();
                        CodeKemu ck = new CodeKemu();
                        List<CodeKemu> collect = codelist.stream().filter(v -> v.getCcode().equals(code)).collect(Collectors.toList());
                        if (collect.size() > 0) {
                            ck = collect.get(0);
                        } else {
                            // 没有找到对应的科目,做个假科目跑完代码，防止报错
                            ck.setId("0");
                        }
                        // 有没有下级
                        List<CodeKemu> list1 = codelist.stream().filter(v -> v.getSuperiorCcode().equals(code)).collect(Collectors.toList());
                        if (list1.size() > 0) {
                            continue;
                        } else {
                            codelist456.add(ck);
                            codelist.remove(ck);
                        }
                    }
                    codelist.forEach(v -> {
                        List<CodeKemu> list = codelist.stream().filter(vv -> vv.getSuperiorCcode().equals(v.getCcode())).collect(Collectors.toList());
                        if (v.getBend().equals("0") && list.size() == 0) {
                            v.setBend("1");
                            codelist789.add(v);
                        }
                    });
                    return Mono.just(CollectOfUtils.mapof("codelist", codelist456, "suporlist", codelist789));
                })
                .flatMap(map -> {
                    List<CodeKemu> suporCodeList = map.get("suporlist");
                    List<CodeKemu> newlist = map.get("codelist");

                    List<AuditCodeKemu> auditCodeKemuList = new ArrayList<>();
                    if (newlist.size() > 0) {
                        for (int i = 0; i < newlist.size(); i++) {
                            CodeKemu v = newlist.get(i);
                            AuditCodeKemu a = new AuditCodeKemu();
                            a.setUniqueCode(v.getUniqueCode()).setUniqueAccStandard(v.getUniqueAccStandard())
                                    .setCclass(v.getCclass()).setCcode(v.getCcode()).setCcodeName(v.getCcodeName())
                                    .setIgrade(v.getIgrade()).setBprogerty(v.getBprogerty()).setCbookType(v.getCbookType())
                                    .setBperson(v.getBperson()).setBcus(v.getBcus()).setBsup(v.getBsup()).setBdept(v.getBdept())
                                    .setBitem(v.getBitem()).setCassItem(v.getCassItem()).setBnum(v.getBnum()).setMenterage(v.getMenterage())
                                    .setBstock(v.getBstock()).setBcash(v.getBcash()).setBbank(v.getBbank()).setBend(v.getBend())
                                    .setBdaybook(v.getBdaybook()).setFlag(v.getFlag()).setIyear(v.getIyear()).setTemplateId(v.getTemplateId())
                                    .setCurrency(v.getCurrency()).setCurrencyType(v.getCurrencyType()).setLowerControl(v.getLowerControl())
                                    .setFuzhuControl(v.getFuzhuControl()).setSuperiorCcode(v.getSuperiorCcode()).setBcashEquivalence(v.getBcashEquivalence())
                                    .setControlled(v.getControlled()).setPxjz(v.getPxjz()).setXjll(v.getXjll()).setCyfx(v.getCyfx())
                                    .setCdfine1(v.getCdfine1())
                                    .setCdfine2(v.getCdfine2())
                                    .setCdfine3(v.getCdfine3())
                                    .setCdfine4(v.getCdfine4())
                                    .setCdfine5(v.getCdfine5())
                                    .setCdfine6(v.getCdfine6())
                                    .setCdfine7(v.getCdfine7())
                                    .setCdfine8(v.getCdfine8())
                                    .setCdfine9(v.getCdfine9())
                                    .setCdfine10(v.getCdfine10())
                                    .setCdfine11(v.getCdfine11())
                                    .setCdfine12(v.getCdfine12())
                                    .setCdfine13(v.getCdfine13())
                                    .setCdfine14(v.getCdfine14())
                                    .setCdfine15(v.getCdfine15())
                                    .setCdfine16(v.getCdfine16())
                                    .setCdfine17(v.getCdfine17())
                                    .setCdfine18(v.getCdfine18())
                                    .setCdfine19(v.getCdfine19())
                                    .setCdfine20(v.getCdfine20())
                                    .setCdfine21(v.getCdfine21())
                                    .setCdfine22(v.getCdfine22())
                                    .setCdfine23(v.getCdfine23())
                                    .setCdfine24(v.getCdfine24())
                                    .setCdfine25(v.getCdfine25())
                                    .setCdfine26(v.getCdfine26())
                                    .setCdfine27(v.getCdfine27())
                                    .setCdfine28(v.getCdfine28())
                                    .setCdfine29(v.getCdfine29())
                                    .setCdfine30(v.getCdfine30())
                                    .setYusuan(v.getYusuan())
                                    .setYsYsly(v.getYsYsly())
                                    .setYsZcgnfl(v.getYsZcgnfl())
                                    .setYsZfzcjjfl(v.getYsZfzcjjfl())
                                    .setYsBmzcjjfl(v.getYsBmzcjjfl())
                                    .setProjectClassId(v.getProjectClassId())
                                    .setIsDel(v.getIsDel()).setDelName(v.getDelName()).setDelDate(v.getDelDate())
                                    .setOptMethod("1").setOptUsername(userName).setOptTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                            auditCodeKemuList.add(a);
                        }
                    }
                    return suporCodeList.size() == 0 && newlist.size() == 0 ? Mono.just("") :
                            // 不是独立账套，需要添加审计记录
                            !independent ? Mono.zip(codeKemuRepository.saveAll(suporCodeList).collectList(), auditCodeKemuRepository.saveAll(auditCodeKemuList).collectList(), codeKemuRepository.deleteAll(newlist)) :
                                    codeKemuRepository.saveAll(suporCodeList).collectList().map(aa -> newlist).flatMap(codeKemuRepository::deleteAll);
                }).then(Mono.just(R.ok().setResult("ok")));
    }


    /**
     * @return reactor.core.publisher.Mono
     * @Description 按照末级状态获取所有科目
     * @Author myh
     * @Date 9:24 2021/7/17
     * @Param [iyear]
     **/
    @PostMapping("company/company_findByIyearAndBendCode")
    public Mono company_findByIyearAndBendCode(String iyear, String bend, String databasenum) {
        return codeKemuRepository.findByIyearAndBendCode(iyear, bend, databasenum).collectList().map(a -> R.ok().setResult(a));
    }

    /**
     * @return reactor.core.publisher.Mono
     * @Description 按照末级状态获取所有科目
     * @Author myh
     * @Date 9:24 2021/7/17
     * @Param [iyear]
     **/
    @PostMapping("company/findKuaiJiKeMu")
    public Mono findKuaiJiKeMu(String iyear, String bend) {
        return codeKemuRepository.findByIyearAndBendOrderByCcode(iyear, bend).collectList().map(a -> R.ok().setResult(a));
    }


    /**
     * @return reactor.core.publisher.Mono
     * @Description 复制科目
     * @Author myh
     * @Date 9:47 2021/7/17
     * @Param [iyear=年度, lastcode=目标科目, nolastcode=来源科目]
     **/
    @PostMapping("company/company_copyCode")
    public Mono company_copyCode(String iyear, String lastcode, String nolastcode) {
        // 获取目标科目信息；方便修改末级标识
        return codeKemuRepository.findByCcodeAndIyear(lastcode, iyear)
                .flatMap(code -> {
                    // 查询来源科目所有下级科目
                    return codeKemuRepository.findByIyearLikeCode(iyear, nolastcode + "_%")
                            .collectList()
                            .map(list -> {
                                list.stream().forEach(v -> {
                                    String num = v.getCcode().substring(nolastcode.length());
                                    v.setId(null).setCcode(lastcode + num).setUniqueCode(lastcode).setSuperiorCcode(lastcode);
                                });

                                return CollectOfUtils.mapof("code", code, "list", list);
                            });
                })
                .flatMapMany(map -> {
                    List<CodeKemu> codelist = (List<CodeKemu>) map.get("list");
                    CodeKemu code = (CodeKemu) map.get("code");
                    code.setBend("0");
                    return codeKemuRepository.save(code).zipWith(codeKemuRepository.saveAll(codelist).collectList());
                })
                .collectList()
                .map(a -> R.ok().setResult(a));
    }

    /* ************************************** 账套科目导入 *****************************************************/
    @Transactional
    @PostMapping("/company/company_importCoke")
    public Mono<R> company_importCoke(@RequestPart("file") FilePart filePartParm, String uniqueAccStandard,
                                      String templateId, String iyear, String jici, String databasenum, String codeTemplateId) throws Exception {
        // 获取表头个数
        AtomicInteger column = new AtomicInteger();
        Path tempFilePath = Files.createTempFile("", new String(filePartParm.filename().getBytes("ISO-8859-1"), "UTF-8"));
        return Mono.just(filePartParm)
                // 上传到临时目录
                .flatMap(filePart -> {
                    try {
                        return DataBufferUtils
                                .write(filePart.content(), AsynchronousFileChannel.open(tempFilePath, StandardOpenOption.WRITE), 0)
                                .doOnComplete(() -> log.info("上传成功"))
                                .collectList()
                                .map(item -> tempFilePath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return Mono.just("");
                })
                .flatMap(temp -> {
                    return groupCodeImportTemplateRepository.findById(codeTemplateId)
                            .flatMap(item -> {
                                return Mono.just(item.getTjson());
                            });
                })
                .flatMap(codetemplatejson -> {
                    String[] str = new String[13];
                    JSONArray objects = JSON.parseArray(codetemplatejson);
                    for (int i = 0; i < objects.size(); i++) {
                        JSONObject object = (JSONObject) objects.get(i);
                        str[i] = object.getString("value");
                    }
                    str[12] = "";

                    XlsUtils3 excelReader = new XlsUtils3(tempFilePath.toString());
                    List<Object[]> list = excelReader.getExcelObj(tempFilePath.toString(), str, 0);

                    column.set(str.length - 1);
                    assert tempFilePath != null;
                    try {
                        Files.delete(tempFilePath);
                    } catch (IOException e) {
                    }

                    Map map = new HashMap();
                    map.put("str", str);
                    map.put("list", list);
                    return Mono.just(map);
                })
                .flatMap(item -> {

                    Map mapArr = new HashMap();
                    mapArr.put("excellist", item.get("list"));   // excel中内容
                    mapArr.put("code", "200");
                    mapArr.put("error", "");
                    mapArr.put("colunmStr", item.get("str"));
                    return codeKemuRepository.findAllByIyear(iyear).collectList()
                            .flatMap(list -> {
                                mapArr.put("kmlist", list);// 科目list
                                return standardRepository.findByStandardUnique(uniqueAccStandard).flatMap(accstandard -> {
                                    return accStyleRepository.findByUnique(accstandard.getAccStyleUnique()).collectList().flatMap(accStyle -> {
                                        mapArr.put("kmstyle", accStyle);// 科目类型
                                        return usedForeignCurrencyRepository.findAll().collectList().flatMap(c -> {
                                            mapArr.put("currencyTypelist", c);// 币种list
                                            return accvoucherRepository.findAllByIyperiod(iyear + "00").collectList().flatMap(qc -> {
                                                mapArr.put("qichulist", qc);// 期初余额list
                                                return accvoucherRepository.findAllAccVoucher(iyear + "00", iyear + "20", iyear + "21", iyear).collectList().flatMap(v -> {
                                                    mapArr.put("accvoucherlist", v);// 凭证list
                                                    return sysUnitOfMeaRepository.findAll().collectList()
                                                            .flatMap(jlUnitList -> {
                                                                mapArr.put("jlUnitList", jlUnitList);// 计量单位list
                                                                return fuzhuHesuanRepository.findAll().collectList()
                                                                        .flatMap(fuzhuhesuanlist -> {
                                                                            mapArr.put("fuzhuhesuanlist", fuzhuhesuanlist);// 辅助项list
                                                                            return Mono.just(mapArr);
                                                                        });
                                                            });
                                                });
                                            });
                                        });
                                    });
                                });
                            });
                })
                .flatMap(mapArr -> {
                    // 科目list
                    List<CodeKemu> kmlist = (List<CodeKemu>) mapArr.get("kmlist");
                    // 科目类型list
                    List<AccStyle> kmstyle = (List<AccStyle>) mapArr.get("kmstyle");
                    // 文件list
                    List<Object[]> excellist = (List<Object[]>) mapArr.get("excellist");
                    // 币种list
                    List<UsedForeignCurrency> sysCurrencyList = (List<UsedForeignCurrency>) mapArr.get("currencyTypelist");
                    // 计量单位list
                    List<SysUnitOfMea> jlUnitList = (List<SysUnitOfMea>) mapArr.get("jlUnitList");
                    // 辅助项list
                    List<FuzhuHesuan> fuzhuhesuanlist = (List<FuzhuHesuan>) mapArr.get("fuzhuhesuanlist");
                    List<FuzhuHesuan> fuzhulist = fuzhuhesuanlist.stream().filter(v -> StrUtil.isNotBlank(v.getCtype()) && v.getCtype().equals("1")).collect(Collectors.toList());
                    List<FuzhuHesuan> zdylist = fuzhuhesuanlist.stream().filter(v -> StrUtil.isNotBlank(v.getCtype()) && v.getCtype().equals("2")).collect(Collectors.toList());
                    // 期初余额list
                    List<Accvoucher> qichulist = (List<Accvoucher>) mapArr.get("qichulist");
                    // 凭证list
                    List<Accvoucher> accvoucherlist = (List<Accvoucher>) mapArr.get("accvoucherlist");
                    List<Accvoucher> accvoucherlist2 = new ArrayList<>();
                    List<CodeKemu> kmlist2 = new ArrayList<>();
                    List<CodeKemu> codelist = new ArrayList<>();
                    for (int i = 1; i < excellist.size(); i++) {
                        //######################### 辅助项 #####################
                        String dept = "0";
                        String psn = "0";
                        String cus = "0";
                        String sup = "0";
                        String pro = "0";
                        String ysYsly = "0";
                        String ysZcgnfl = "0";
                        String ysZfzcjjfl = "0";
                        String ysBmzcjjfl = "0";
                        //######################### 自定义辅助项 #####################
                        String cdfine1 = "0";
                        String cdfine2 = "0";
                        String cdfine3 = "0";
                        String cdfine4 = "0";
                        String cdfine5 = "0";
                        String cdfine6 = "0";
                        String cdfine7 = "0";
                        String cdfine8 = "0";
                        String cdfine9 = "0";
                        String cdfine10 = "0";
                        String cdfine11 = "0";
                        String cdfine12 = "0";
                        String cdfine13 = "0";
                        String cdfine14 = "0";
                        String cdfine15 = "0";
                        String cdfine16 = "0";
                        String cdfine17 = "0";
                        String cdfine18 = "0";
                        String cdfine19 = "0";
                        String cdfine20 = "0";
                        String cdfine21 = "0";
                        String cdfine22 = "0";
                        String cdfine23 = "0";
                        String cdfine24 = "0";
                        String cdfine25 = "0";
                        String cdfine26 = "0";
                        String cdfine27 = "0";
                        String cdfine28 = "0";
                        String cdfine29 = "0";
                        String cdfine30 = "0";


                        // 级次封装
                        List<Map> jicilist = new ArrayList<>();
                        int test = 0;
                        for (int j = 0; j < jici.split("-").length; j++) {
                            test += Integer.valueOf(jici.split("-")[j]);
                            jicilist.add(CollectOfUtils.mapof("jicilength", test, "jicinum", jici.split("-")[j], "level", j + 1));
                        }

                        int a = column.get();
                        Object[] obj = excellist.get(i);
                        int finalI = i;
                        List<String> errorText = new ArrayList<>();
                        // 科目编码是否重复
                        if (StringUtils.isNotBlank(excellist.get(finalI)[1].toString().trim())) {
                            List<CodeKemu> collect = kmlist.stream().filter(v -> v.getCcode().equals(excellist.get(finalI)[1].toString())).collect(Collectors.toList());
                            if (collect.size() > 0) {
                                errorText.add("科目编码与当前已存在档案重复");
                                obj[a] = errorText.toString();
                                if (codelist.size() > 0) {
                                    if (codelist.size() > 0) {
                                        codelist.remove(0);
                                    }
                                }
                            }
                            List<Map> findByjici = jicilist.stream().filter(ji -> ji.get("jicilength").toString().equals(String.valueOf(excellist.get(finalI)[1].toString().length()))).collect(Collectors.toList());
                            if (findByjici.size() == 0) {
                                errorText.add("科目编码不符合系统科目级次设置规则");
                                obj[a] = errorText.toString();
                            }
                        } else {
                            errorText.add("科目编码不能为空");
                            obj[a] = errorText.toString();
                            if (codelist.size() > 0) {
                                codelist.remove(0);
                            }
                        }
                        // 科目名称是否重复
                        if (StringUtils.isNotBlank(excellist.get(finalI)[2].toString().trim())) {
                            List<CodeKemu> collect = kmlist.stream().filter(v -> v.getCcodeName().equals(excellist.get(finalI)[2].toString())).collect(Collectors.toList());
                            if (collect.size() > 0) {
                                errorText.add("科目名称与当前已存在档案重复");
                                obj[a] = errorText.toString();
                                if (codelist.size() > 0) {
                                    codelist.remove(0);
                                }
                            }
                        } else {
                            errorText.add("科目名称不能为空");
                            obj[a] = errorText.toString();
                            if (codelist.size() > 0) {
                                codelist.remove(0);
                            }
                        }
                        // 科目方向
                        if (StringUtils.isBlank(excellist.get(finalI)[3].toString().trim())) {
                            errorText.add("科目方向不能为空");
                            obj[a] = errorText.toString();
                            if (codelist.size() > 0) {
                                codelist.remove(0);
                            }
                        } else {
                            if (!excellist.get(finalI)[3].toString().contains("借") && !excellist.get(finalI)[3].toString().contains("贷")) {
                                errorText.add("未知科目方向");
                                obj[a] = errorText.toString();
                                if (codelist.size() > 0) {
                                    codelist.remove(0);
                                }
                            }
                        }
                        // 科目类型
                        if (StringUtils.isBlank(excellist.get(finalI)[0].toString().trim())) {
                            errorText.add("科目类型不能为空");
                            obj[a] = errorText.toString();
                            if (codelist.size() > 0) {
                                codelist.remove(0);
                            }
                        } else {
                            // 检查科目首数字是否在此范围内
                            List<AccStyle> findByStyle = kmstyle.stream().filter(s -> s.getCclass().equals(excellist.get(finalI)[0].toString().trim())).collect(Collectors.toList());
                            if (findByStyle.size() == 0) {
                                errorText.add("科目类型不存在");
                                obj[a] = errorText.toString();
                                if (codelist.size() > 0) {
                                    codelist.remove(0);
                                }
                            }
                        }
                        // 外币名称
                        if (StringUtils.isNotBlank(excellist.get(finalI)[7].toString().trim())) {
                            List<UsedForeignCurrency> collect = sysCurrencyList.stream().filter(wb -> wb.getForeignName().equals(excellist.get(finalI)[7].toString().trim())).collect(Collectors.toList());
                            if (collect.size() == 0) {
                                errorText.add("系统外币档案中不存在,请填写正确的外币名称");
                                obj[a] = errorText.toString();
                                if (codelist.size() > 0) {
                                    codelist.remove(0);
                                }
                            }
                        }
                        // 计量单位
                        if (StringUtils.isNotBlank(excellist.get(finalI)[8].toString().trim())) {
                            List<SysUnitOfMea> collect = jlUnitList.stream().filter(jl -> jl.getUnitName().equals(excellist.get(finalI)[8].toString().trim())).collect(Collectors.toList());
                            if (collect.size() == 0) {
                                errorText.add("系统计量单位档案中不存在,请填写正确的计量单位");
                                obj[a] = errorText.toString();
                                if (codelist.size() > 0) {
                                    codelist.remove(0);
                                }
                            }
                        }
                        // 辅助项list
                        if (StringUtils.isNotBlank(excellist.get(finalI)[9].toString().trim())) {
                            List<String> collect = fuzhulist.stream().map(item -> item.getCname()).collect(Collectors.toList());
                            for (String s : collect) {
                                // 文件中有 逗号分割
                                String temp = excellist.get(finalI)[9].toString().replaceAll("，", ",");
                                if (temp.contains(",")) {
                                    for (int j = 0; j < temp.split(",").length; j++) {
                                        if (temp.split(",")[j].trim().equals("部门")) {
                                            dept = "1";
                                        }
                                        if (temp.split(",")[j].trim().equals("个人")) {
                                            psn = "1";
                                        }
                                        if (temp.split(",")[j].trim().equals("客户")) {
                                            cus = "1";
                                        }
                                        if (temp.split(",")[j].trim().equals("供应商")) {
                                            sup = "1";
                                        }
                                        if (temp.split(",")[j].trim().equals("项目")) {
                                            pro = "1";
                                        }
                                        if (temp.split(",")[j].trim().equals("支出功能分类")) {
                                            ysZcgnfl = "1";
                                        }
                                        if (temp.split(",")[j].trim().equals("部门支出功能分类")) {
                                            ysBmzcjjfl = "1";
                                        }
                                        if (temp.split(",")[j].trim().equals("政府支出经济分类")) {
                                            ysZfzcjjfl = "1";
                                        }
                                        if (temp.split(",")[j].trim().equals("预算来源")) {
                                            ysYsly = "1";
                                        }
                                    }
                                } else {
                                    if (s.equals("部门")) {
                                        if (excellist.get(finalI)[9].toString().trim().contains("部门") && !s.contains("部门支出")) {
                                            dept = "1";
                                        }
                                    }
                                    if (s.equals("个人")) {
                                        if (excellist.get(finalI)[9].toString().trim().contains("个人")) {
                                            psn = "1";
                                        }
                                    }
                                    if (s.equals("客户")) {
                                        if (excellist.get(finalI)[9].toString().trim().contains("客户")) {
                                            cus = "1";
                                        }
                                    }
                                    if (s.equals("供应商")) {
                                        if (excellist.get(finalI)[9].toString().trim().contains("供应商")) {
                                            sup = "1";
                                        }
                                    }
                                    if (s.equals("项目")) {
                                        if (excellist.get(finalI)[9].toString().trim().contains("项目")) {
                                            pro = "1";
                                        }
                                    }
                                    if (s.equals("支出功能分类")) {
                                        if (excellist.get(finalI)[9].toString().trim().contains("支出功能分类")) {
                                            ysZcgnfl = "1";
                                        }
                                    }
                                    if (s.equals("部门支出功能分类")) {
                                        if (excellist.get(finalI)[9].toString().trim().contains("部门支出功能分类")) {
                                            ysBmzcjjfl = "1";
                                        }
                                    }
                                    if (s.equals("政府支出经济分类")) {
                                        if (excellist.get(finalI)[9].toString().trim().contains("政府支出经济分类")) {
                                            ysZfzcjjfl = "1";
                                        }
                                    }
                                    if (s.equals("预算来源")) {
                                        if (excellist.get(finalI)[9].toString().trim().contains("预算来源")) {
                                            ysYsly = "1";
                                        }
                                    }
                                }
                            }
                        }
                        // 自定义辅助项list
                        if (StringUtils.isNotBlank(excellist.get(finalI)[9].toString().trim())) {
                            // 判断是否包含 '自定义项' 字
                            if (excellist.get(finalI)[9].toString().trim().contains("自定义项")) {
                                // 把自定义项替换成 ,
                                String str = excellist.get(finalI)[9].toString().trim().replaceAll("自定义项", ",").substring(1);

                                for (int j = 0; j < str.split(",").length; j++) {
                                    int aa = Integer.valueOf(str.split(",")[j]);
                                    List<FuzhuHesuan> fuzhuHesuanStream = zdylist.stream().filter(v -> aa == v.getCdfine()).collect(Collectors.toList());
                                    if (fuzhuHesuanStream.size() > 0) {
                                        if (fuzhuHesuanStream.get(0).getCdfine() == 1) {
                                            cdfine1 = "1";
                                        }
                                        if (fuzhuHesuanStream.get(0).getCdfine() == 2) {
                                            cdfine2 = "1";
                                        }
                                        if (fuzhuHesuanStream.get(0).getCdfine() == 3) {
                                            cdfine3 = "1";
                                        }
                                        if (fuzhuHesuanStream.get(0).getCdfine() == 4) {
                                            cdfine4 = "1";
                                        }
                                        if (fuzhuHesuanStream.get(0).getCdfine() == 5) {
                                            cdfine5 = "1";
                                        }
                                        if (fuzhuHesuanStream.get(0).getCdfine() == 6) {
                                            cdfine6 = "1";
                                        }
                                        if (fuzhuHesuanStream.get(0).getCdfine() == 7) {
                                            cdfine7 = "1";
                                        }
                                        if (fuzhuHesuanStream.get(0).getCdfine() == 8) {
                                            cdfine8 = "1";
                                        }
                                        if (fuzhuHesuanStream.get(0).getCdfine() == 9) {
                                            cdfine9 = "1";
                                        }
                                        if (fuzhuHesuanStream.get(0).getCdfine() == 10) {
                                            cdfine10 = "1";
                                        }
                                        if (fuzhuHesuanStream.get(0).getCdfine() == 11) {
                                            cdfine11 = "1";
                                        }
                                        if (fuzhuHesuanStream.get(0).getCdfine() == 12) {
                                            cdfine12 = "1";
                                        }
                                        if (fuzhuHesuanStream.get(0).getCdfine() == 13) {
                                            cdfine13 = "1";
                                        }
                                        if (fuzhuHesuanStream.get(0).getCdfine() == 14) {
                                            cdfine14 = "1";
                                        }
                                        if (fuzhuHesuanStream.get(0).getCdfine() == 15) {
                                            cdfine15 = "1";
                                        }
                                        if (fuzhuHesuanStream.get(0).getCdfine() == 16) {
                                            cdfine16 = "1";
                                        }
                                        if (fuzhuHesuanStream.get(0).getCdfine() == 17) {
                                            cdfine17 = "1";
                                        }
                                        if (fuzhuHesuanStream.get(0).getCdfine() == 18) {
                                            cdfine18 = "1";
                                        }
                                        if (fuzhuHesuanStream.get(0).getCdfine() == 19) {
                                            cdfine19 = "1";
                                        }
                                        if (fuzhuHesuanStream.get(0).getCdfine() == 20) {
                                            cdfine20 = "1";
                                        }
                                        if (fuzhuHesuanStream.get(0).getCdfine() == 21) {
                                            cdfine21 = "1";
                                        }
                                        if (fuzhuHesuanStream.get(0).getCdfine() == 22) {
                                            cdfine22 = "1";
                                        }
                                        if (fuzhuHesuanStream.get(0).getCdfine() == 23) {
                                            cdfine23 = "1";
                                        }
                                        if (fuzhuHesuanStream.get(0).getCdfine() == 24) {
                                            cdfine24 = "1";
                                        }
                                        if (fuzhuHesuanStream.get(0).getCdfine() == 25) {
                                            cdfine25 = "1";
                                        }
                                        if (fuzhuHesuanStream.get(0).getCdfine() == 26) {
                                            cdfine26 = "1";
                                        }
                                        if (fuzhuHesuanStream.get(0).getCdfine() == 27) {
                                            cdfine27 = "1";
                                        }
                                        if (fuzhuHesuanStream.get(0).getCdfine() == 28) {
                                            cdfine28 = "1";
                                        }
                                        if (fuzhuHesuanStream.get(0).getCdfine() == 29) {
                                            cdfine29 = "1";
                                        }
                                        if (fuzhuHesuanStream.get(0).getCdfine() == 30) {
                                            cdfine30 = "1";
                                        }
                                    }
                                }
                            } else {
                                List<String> namestr = zdylist.stream().map(item -> item.getCname()).collect(Collectors.toList());
                                List<String> list = namestr.stream().filter(v -> excellist.get(finalI)[9].toString().trim().contains(v)).collect(Collectors.toList());
                                if (list.size() > 0) {
                                    for (int j = 0; j < list.size(); j++) {
                                        int finalJ = j;
                                        List<FuzhuHesuan> fuzhuHesuanStream = zdylist.stream().filter(v -> list.get(finalJ).equals(v.getCname())).collect(Collectors.toList());
                                        if (fuzhuHesuanStream.size() > 0) {
                                            if (fuzhuHesuanStream.get(0).getCdfine() == 1) {
                                                cdfine1 = "1";
                                            }
                                            if (fuzhuHesuanStream.get(0).getCdfine() == 2) {
                                                cdfine2 = "1";
                                            }
                                            if (fuzhuHesuanStream.get(0).getCdfine() == 3) {
                                                cdfine3 = "1";
                                            }
                                            if (fuzhuHesuanStream.get(0).getCdfine() == 4) {
                                                cdfine4 = "1";
                                            }
                                            if (fuzhuHesuanStream.get(0).getCdfine() == 5) {
                                                cdfine5 = "1";
                                            }
                                            if (fuzhuHesuanStream.get(0).getCdfine() == 6) {
                                                cdfine6 = "1";
                                            }
                                            if (fuzhuHesuanStream.get(0).getCdfine() == 7) {
                                                cdfine7 = "1";
                                            }
                                            if (fuzhuHesuanStream.get(0).getCdfine() == 8) {
                                                cdfine8 = "1";
                                            }
                                            if (fuzhuHesuanStream.get(0).getCdfine() == 9) {
                                                cdfine9 = "1";
                                            }
                                            if (fuzhuHesuanStream.get(0).getCdfine() == 10) {
                                                cdfine10 = "1";
                                            }
                                            if (fuzhuHesuanStream.get(0).getCdfine() == 11) {
                                                cdfine11 = "1";
                                            }
                                            if (fuzhuHesuanStream.get(0).getCdfine() == 12) {
                                                cdfine12 = "1";
                                            }
                                            if (fuzhuHesuanStream.get(0).getCdfine() == 13) {
                                                cdfine13 = "1";
                                            }
                                            if (fuzhuHesuanStream.get(0).getCdfine() == 14) {
                                                cdfine14 = "1";
                                            }
                                            if (fuzhuHesuanStream.get(0).getCdfine() == 15) {
                                                cdfine15 = "1";
                                            }
                                            if (fuzhuHesuanStream.get(0).getCdfine() == 16) {
                                                cdfine16 = "1";
                                            }
                                            if (fuzhuHesuanStream.get(0).getCdfine() == 17) {
                                                cdfine17 = "1";
                                            }
                                            if (fuzhuHesuanStream.get(0).getCdfine() == 18) {
                                                cdfine18 = "1";
                                            }
                                            if (fuzhuHesuanStream.get(0).getCdfine() == 19) {
                                                cdfine19 = "1";
                                            }
                                            if (fuzhuHesuanStream.get(0).getCdfine() == 20) {
                                                cdfine20 = "1";
                                            }
                                            if (fuzhuHesuanStream.get(0).getCdfine() == 21) {
                                                cdfine21 = "1";
                                            }
                                            if (fuzhuHesuanStream.get(0).getCdfine() == 22) {
                                                cdfine22 = "1";
                                            }
                                            if (fuzhuHesuanStream.get(0).getCdfine() == 23) {
                                                cdfine23 = "1";
                                            }
                                            if (fuzhuHesuanStream.get(0).getCdfine() == 24) {
                                                cdfine24 = "1";
                                            }
                                            if (fuzhuHesuanStream.get(0).getCdfine() == 25) {
                                                cdfine25 = "1";
                                            }
                                            if (fuzhuHesuanStream.get(0).getCdfine() == 26) {
                                                cdfine26 = "1";
                                            }
                                            if (fuzhuHesuanStream.get(0).getCdfine() == 27) {
                                                cdfine27 = "1";
                                            }
                                            if (fuzhuHesuanStream.get(0).getCdfine() == 28) {
                                                cdfine28 = "1";
                                            }
                                            if (fuzhuHesuanStream.get(0).getCdfine() == 29) {
                                                cdfine29 = "1";
                                            }
                                            if (fuzhuHesuanStream.get(0).getCdfine() == 30) {
                                                cdfine30 = "1";
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        if (errorText.size() == 0) {
                            // 科目编码
                            String cclass = excellist.get(i)[0].toString().trim();
                            String ccode = excellist.get(i)[1].toString().trim();
                            String ccodeName = excellist.get(i)[2].toString().trim();
                            String bprogerty = excellist.get(i)[3].toString().trim().contains("借") ? "1" : "0";
                            // 现金等价物
                            String bcashEquivalence = "0";
                            // 现金账
                            String bcash = excellist.get(i)[4] == null ? "0" : StringUtils.isBlank(excellist.get(i)[4].toString()) ? "0" : excellist.get(i)[4].toString().trim().equals("是") || excellist.get(i)[4].toString().trim().equals("1") || excellist.get(i)[4].toString().trim().equals("Y") ? "1" : "0";
                            // 银行账
                            String bbank = excellist.get(i)[5] == null ? "0" : StringUtils.isBlank(excellist.get(i)[5].toString()) ? "0" : excellist.get(i)[5].toString().trim().equals("是") || excellist.get(i)[5].toString().trim().equals("1") || excellist.get(i)[5].toString().trim().equals("Y") ? "1" : "0";
                            // 日记账
                            String bdaybook = excellist.get(i)[6] == null ? "0" : StringUtils.isBlank(excellist.get(i)[6].toString()) ? "0" : excellist.get(i)[6].toString().trim().equals("是") || excellist.get(i)[6].toString().trim().equals("1") || excellist.get(i)[6].toString().trim().equals("Y") ? "1" : "0";
                            // 外币名称
                            String currencyType = excellist.get(i)[7] == null ? "0" : StringUtils.isBlank(excellist.get(i)[7].toString()) ? "" : sysCurrencyList.stream().filter(v -> v.getForeignName().equals(excellist.get(finalI)[7].toString().trim())).collect(Collectors.toList()).get(0).getForeignCode();
                            // 外币核算
                            String currency = excellist.get(i)[7] == null ? "0" : StringUtils.isBlank(excellist.get(i)[7].toString()) ? "0" : "1";
                            // 计量单位
                            String menterage = excellist.get(i)[8] == null ? "0" : excellist.get(i)[8].toString().trim();
                            // 数量核算
                            String bnum = excellist.get(i)[8] == null ? "0" : StringUtils.isBlank(excellist.get(i)[8].toString()) ? "0" : "1";
                            // 业务受控科目
                            String controlled = excellist.get(i)[10] == null ? "0" : StringUtils.isBlank(excellist.get(i)[10].toString()) ? "0" : excellist.get(i)[10].toString().trim().equals("是") || excellist.get(i)[10].toString().trim().equals("1") || excellist.get(i)[10].toString().trim().equals("Y") ? "1" : "0";
                            // 封存
                            String flag = excellist.get(i)[11] == null ? "1" : StringUtils.isBlank(excellist.get(i)[11].toString()) ? "1" : excellist.get(i)[11].toString().trim().equals("是") || excellist.get(i)[11].toString().trim().equals("1") || excellist.get(i)[11].toString().trim().equals("Y") ? "0" : "1";

                            // 判断科目编码是否符合级次
                            int jicinum = 0;
                            String level = "";
                            List<Map> findByjici = jicilist.stream().filter(ji -> ji.get("jicilength").toString().equals(String.valueOf(ccode.length()))).collect(Collectors.toList());
                            if (findByjici.size() > 0) {
                                jicinum = Integer.valueOf(findByjici.get(0).get("jicinum").toString());
                                level = findByjici.get(0).get("level").toString();
                            }

                            CodeKemu ck = new CodeKemu();
                            ck
                                    .setCcode(ccode)
                                    .setCcodeName(ccodeName)
                                    .setBprogerty(bprogerty)
                                    .setCclass(cclass)
                                    .setBcash(bcash)
                                    .setBbank(bbank)
                                    .setBdaybook(bdaybook)
                                    .setBcashEquivalence(bcashEquivalence)
                                    .setBnum(bnum)
                                    .setMenterage(menterage)
                                    .setCurrency(currency)
                                    .setCurrencyType(currencyType)
                                    .setCbookType("金额式")
                                    .setBperson(psn)
                                    .setBcus(cus)
                                    .setBsup(sup)
                                    .setBdept(dept)
                                    .setBitem(pro)
                                    .setUniqueCode(ck.getCcode())
                                    .setUniqueAccStandard(uniqueAccStandard)
                                    .setTemplateId(templateId)
                                    .setTenantId(databasenum)
                                    .setControlled(controlled)
                                    .setFlag(flag)
                                    .setIyear(iyear)
                                    .setBend("1")
                                    .setIgrade(level)
                                    .setSuperiorCcode("0")
                                    .setYusuan("0")
                                    .setLowerControl("0")
                                    .setFuzhuControl("0")
                                    .setPxjz("0")
                                    .setXjll("0")
                                    .setCyfx("0")
                                    .setYsBmzcjjfl(ysBmzcjjfl)
                                    .setYsYsly(ysYsly)
                                    .setYsZcgnfl(ysZcgnfl)
                                    .setYsZfzcjjfl(ysZfzcjjfl)
                                    .setCdfine1(cdfine1)
                                    .setCdfine2(cdfine2)
                                    .setCdfine3(cdfine3)
                                    .setCdfine4(cdfine4)
                                    .setCdfine5(cdfine5)
                                    .setCdfine6(cdfine6)
                                    .setCdfine7(cdfine7)
                                    .setCdfine8(cdfine8)
                                    .setCdfine9(cdfine9)
                                    .setCdfine10(cdfine10)
                                    .setCdfine11(cdfine11)
                                    .setCdfine12(cdfine12)
                                    .setCdfine13(cdfine13)
                                    .setCdfine14(cdfine14)
                                    .setCdfine15(cdfine15)
                                    .setCdfine16(cdfine16)
                                    .setCdfine17(cdfine17)
                                    .setCdfine18(cdfine18)
                                    .setCdfine19(cdfine19)
                                    .setCdfine20(cdfine20)
                                    .setCdfine21(cdfine21)
                                    .setCdfine22(cdfine22)
                                    .setCdfine23(cdfine23)
                                    .setCdfine24(cdfine24)
                                    .setCdfine25(cdfine25)
                                    .setCdfine26(cdfine26)
                                    .setCdfine27(cdfine27)
                                    .setCdfine28(cdfine28)
                                    .setCdfine29(cdfine29)
                                    .setCdfine30(cdfine30);

                            // 根据科目编码首位查找科目类型
                            List<AccStyle> accStyleStream = kmstyle.stream().filter(s -> s.getCclass().equals(cclass)).collect(Collectors.toList());
                            if (accStyleStream.size() > 0)
                                ck.setYusuan(StrUtil.isNotBlank(accStyleStream.get(0).getFlagYusuan()) ? "1" : "0");
                            codelist.add(ck);
                            // 算出上级科目
                            String suprtcode = ccode.substring(0, ccode.length() - jicinum);
                            if (StringUtils.isNotBlank(suprtcode) && suprtcode.length() > 2) {
                                // 修改上级科目末级状态
                                codelist.stream().forEach(km -> {
                                    if (km.getCcode().equals(excellist.get(finalI)[1].toString())) {
                                        km.setSuperiorCcode(suprtcode);
                                    }
                                    if (km.getCcode().equals(suprtcode)) {
                                        km.setBend("0");
                                    }
                                });

                                // 判断 上级科目是否存在表中
                                List<CodeKemu> findBySuperCode = kmlist.stream().filter(km -> km.getCcode().equals(suprtcode)).collect(Collectors.toList());
                                if (findBySuperCode.size() > 0) {
                                    // 获取同级别的科目编码
                                    List<String> equalsLevelCode = new ArrayList<>();
                                    for (int j = 1; j < excellist.size(); j++) {
                                        if (excellist.get(j)[1].toString().length() == findBySuperCode.get(0).getCcode().length()) {
                                            equalsLevelCode.add(excellist.get(j)[0].toString());
                                        }
                                    }
                                    // 把期初、凭证的上级科目修改成下级第一个科目
                                    if (equalsLevelCode.size() > 0) {
                                        // 将期初余额改为第一个下级科目编码
                                        qichulist.stream().forEach(qc -> {
                                            if (qc.getCcode().equals(suprtcode)) {
                                                qc.setCcode(equalsLevelCode.get(0));
                                            }
                                        });
                                        // 将凭证改为第一个下级科目编码
                                        accvoucherlist2 = accvoucherlist.stream().filter(ac -> ac.getCcode().equals(suprtcode)).collect(Collectors.toList());
                                        accvoucherlist2.stream().forEach(v -> {
                                            if (v.getCcode().equals(suprtcode)) {
                                                v.setCcode(equalsLevelCode.get(0));
                                            }
                                        });
                                        // 修改科目末级状态
                                        kmlist2 = kmlist.stream().filter(km -> km.getCcode().equals(suprtcode)).collect(Collectors.toList());
                                        kmlist2.stream().forEach(km -> {
                                            if (km.getCcode().equals(suprtcode)) {
                                                if (!km.getSuperiorCcode().equals("0")) {
                                                    km.setBend("0");
                                                }
                                            }
                                        });
                                    }

                                    // 去重后的同级别科目编码
                                    List<String> distinctNumlist = equalsLevelCode.stream().distinct().collect(Collectors.toList());
                                    if (equalsLevelCode.size() != distinctNumlist.size()) {
                                        errorText.add("科目的科目编码在系统中重复");
                                        obj[a] = errorText.toString();
                                        if (codelist.size() > 0) {
                                            codelist.remove(0);
                                        }
                                    }
                                }

                                // 判断 上级科目是否存在模板中
                                List<Object[]> findBySuperCodeEcxel = excellist.stream().filter(km -> km[1].equals(suprtcode)).collect(Collectors.toList());
                                if (findBySuperCodeEcxel.size() > 0) {
                                    // 获取下级科目编码
                                    List<String> equalsLevelCode = new ArrayList<>();
                                    // 获取下级科目名称;用于判断文件中下级科目名称是否重复
                                    List<String> equalsLevelCodeName = new ArrayList<>();
                                    for (int j = 1; j < excellist.size(); j++) {
                                        if (excellist.get(j)[1].toString().indexOf(suprtcode) != -1 && !excellist.get(j)[1].toString().equals(suprtcode)) {
                                            equalsLevelCode.add(excellist.get(j)[1].toString());
                                        }
                                    }

                                    // 把期初、凭证的上级科目修改成下级第一个科目
                                    if (equalsLevelCode.size() > 0) {
                                        // 将期初余额改为第一个下级科目编码
                                        qichulist.stream().forEach(qc -> {
                                            if (qc.getCcode() != null) {
                                                if (qc.getCcode().equals(suprtcode)) {
                                                    qc.setCcode(equalsLevelCode.get(0));
                                                }
                                            }
                                        });
                                        // 将凭证改为第一个下级科目编码
                                        accvoucherlist.stream().forEach(v -> {
                                            if (v.getCcode() != null) {
                                                if (v.getCcode().equals(suprtcode)) {
                                                    v.setCcode(equalsLevelCode.get(0));
                                                }
                                            }
                                        });
                                        // 修改科目末级状态
                                        kmlist.stream().forEach(km -> {
                                            if (km.getCcode().equals(suprtcode)) {
                                                if (!km.getSuperiorCcode().equals("0")) {
                                                    km.setBend("0");
                                                }
                                            }
                                        });
                                    }
                                }
                                if (findBySuperCode.size() == 0 && findBySuperCodeEcxel.size() == 0) {
                                    errorText.add("上级科目编码在系统中不存在");
                                    obj[a] = errorText.toString();
                                    if (codelist.size() > 0) {
                                        codelist.remove(0);
                                    }
                                }
                            }
                        } else {
                            mapArr.put("code", "200");
                            mapArr.put("error", "error");
                        }
                    }
                    /***********************************重复数据***************************************/
                    // 进行去重
                    List<String> listStr = new ArrayList<>();
                    for (int i = 1; i < excellist.size(); i++) {
                        listStr.add(excellist.get(i)[1].toString().trim());    // 编码
                    }
                    // 去重后list
                    long doublesize = listStr.stream().distinct().count();
                    if (doublesize < listStr.size()) {
                        mapArr.put("error", "文件中科目编码有重复数据，请仔细检查再进行导入");
                        mapArr.put("code", "401");
                        return Mono.just(mapArr);
                    }
                    /**************************************END*************************************/
                    mapArr.put("list", codelist);
                    mapArr.put("kmlist", kmlist2);
                    mapArr.put("column", column.get());
                    mapArr.put("qichulist", qichulist);
                    mapArr.put("accvoucherlist", accvoucherlist2);
                    return Mono.just(mapArr);
                })
                .flatMapMany(mapArr -> {
                    List<CodeKemu> kmlist = (List<CodeKemu>) mapArr.get("kmlist");
                    List<CodeKemu> codelist = (List<CodeKemu>) mapArr.get("list");
                    List<Accvoucher> qichulist = (List<Accvoucher>) mapArr.get("qichulist");
                    List<Accvoucher> accvoucherlist = (List<Accvoucher>) mapArr.get("accvoucherlist");
                    return mapArr.get("code").equals("401") ? Mono.just(mapArr) : codelist.size() == 0 ? Mono.just(mapArr) : codeKemuRepository.saveAll(codelist).collectList()
                            .zipWith(codeKemuRepository.saveAll(kmlist).collectList())
                            .zipWith(accvoucherRepository.saveAll(accvoucherlist).collectList())
                            .zipWith(accvoucherRepository.saveAll(qichulist).collectList()).map(a -> mapArr);

                }).collectList()
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping("company/company_findAllByIyear")
    public Mono company_findAllByIyear(String iyear, String databasenum) {
        return codeKemuRepository.findAllByIyear(iyear).collectList().map(a -> R.ok().setResult(a));
    }

    /***************************Mr.Ye***************************/
    /**
     * 获取所有现金或者流量科目
     *
     * @return
     */

    @GetMapping("company/findAllXjOrLlList")
    public Mono<R> findAllXjOrLlList() {
        return codeKemuRepository.findAllByXjOrLi().collectList().map(o -> R.ok().setResult(o));
    }


    @GetMapping("company/findAllMJList")
    public Mono<R> findAllMJList() {
        return codeKemuRepository.findAllByFlagAndBendOrderByCcodeAsc("1", "1").collectList().map(o -> R.ok().setResult(o));
    }

    /***************************Mr.Ye***************************/

    /**
     * 获取对应的账套科目 所属的 会计准则、科目模板
     *
     * @return reactor.core.publisher.Mono<org.springbooz.core.tool.result.R>
     * @Author myh
     * @Date 11:59 2021/8/5
     * @Param [uniqueAccStandard, templateId, ccode]
     **/
    @PostMapping("company/groupStandardAndTemplate")
    public Mono<R> groupStandardAndTemplate(String databaseNum, String iyear) {
        return codeKemuRepository.groupStandardAndTemplate(databaseNum, iyear).collectList().map(o -> R.ok().setResult(o)).defaultIfEmpty(R.ok().setResult(""));
    }

    @PostMapping("company/companyfindAll")
    public Mono<R> companyfindAll(@RequestBody Map map, Pageable pageable) {
        AtomicReference<Long> totalAR = new AtomicReference(0);
        String cclass = map.get("cclass").toString();
        String databaseNum = map.get("databasenum").toString();
        String iyear = map.get("iyear").toString();
        // 当前页
        int page = map.get("page") == null ? 1 : Integer.parseInt(map.get("page").toString());
        // 页多少条
        int size = map.get("size") == null ? 10000 : Integer.parseInt(map.get("size").toString());
        Map<String, String> searchMap = ((HashMap<String, String>) map.get("searchConditon"));
        return codeKemuRepository.companyfindAll(iyear, pageable).collectList()
                .flatMap(list -> {
                    return fuzhuHesuanRepository.findByTenantId(databaseNum).collectList()
                            .flatMap(fuzhuhesuan -> {
                                long total = list.size();
                                List<CodeKemu> cklist = list;
                                if (!"全部".equals(cclass)) {
                                    cklist = list.stream().filter(ck -> ck.getCclass().equals(cclass)).collect(Collectors.toList());
                                    total = cklist.size();
                                }
                                cklist = cklist.stream().skip((page - 1) * size).limit(size).collect(Collectors.toList());

                                for (int i = 0; i < cklist.size(); i++) {
                                    // 5个辅助项
                                    String fuzhu = "";
                                    String fuzhuhesuanStr = "";
                                    if (StringUtils.isNotBlank(cklist.get(i).getBperson()) && cklist.get(i).getBperson().equals("1")) {
                                        fuzhu += "个人,";
                                        fuzhuhesuanStr += "cperson_id,";
                                    }
                                    if (StringUtils.isNotBlank(cklist.get(i).getBdept()) && cklist.get(i).getBdept().equals("1")) {
                                        fuzhu += "部门,";
                                        fuzhuhesuanStr += "cdept_id,";
                                    }
                                    if (StringUtils.isNotBlank(cklist.get(i).getBcus()) && cklist.get(i).getBcus().equals("1")) {
                                        fuzhu += "客户,";
                                        fuzhuhesuanStr += "ccus_id,";
                                    }
                                    if (StringUtils.isNotBlank(cklist.get(i).getBsup()) && cklist.get(i).getBsup().equals("1")) {
                                        fuzhu += "供应商,";
                                        fuzhuhesuanStr += "csup_id,";
                                    }
                                    if (StringUtils.isNotBlank(cklist.get(i).getBitem()) && cklist.get(i).getBitem().equals("1")) {
                                        fuzhu += "项目,";
                                        fuzhuhesuanStr += "project_id,";
                                    }
                                    if (StringUtils.isNotBlank(cklist.get(i).getYsYsly()) && cklist.get(i).getYsYsly().equals("1")) {
                                        fuzhu += "预算来源,";
                                        fuzhuhesuanStr += "ys_ysly,";
                                    }

                                    if (StringUtils.isNotBlank(cklist.get(i).getYsZcgnfl()) && cklist.get(i).getYsZcgnfl().equals("1")) {
                                        fuzhu += "支出功能分类,";
                                        fuzhuhesuanStr += "ys_zfgnfl,";
                                    }
                                    if (StringUtils.isNotBlank(cklist.get(i).getYsZfzcjjfl()) && cklist.get(i).getYsZfzcjjfl().equals("1")) {
                                        fuzhu += "政府支出经济分类,";
                                        fuzhuhesuanStr += "ys_zfzcjjfl,";
                                    }
                                    if (StringUtils.isNotBlank(cklist.get(i).getYsBmzcjjfl()) && cklist.get(i).getYsBmzcjjfl().equals("1")) {
                                        fuzhu += "部门支出经济分类,";
                                        fuzhuhesuanStr += "ys_bmzcjjfl,";
                                    }
                                    if (!"".equals(fuzhu)) {
                                        fuzhu = fuzhu.substring(0, fuzhu.length() - 1);
                                        cklist.get(i).setFuzhu(fuzhu.substring(0, fuzhu.length() - 1));
                                    }

                                    Map byZdyFuZhu = findByZdyFuZhu(fuzhuhesuan, cklist.get(i));
                                    String a = "";
                                    if (StrUtil.isNotBlank(fuzhu)) {
                                        a = fuzhu;
                                        if (StrUtil.isNotBlank(byZdyFuZhu.get("result").toString())) {
                                            a += "," + byZdyFuZhu.get("result").toString();
                                        }
                                    }
                                    String b = "";
                                    if (StrUtil.isNotBlank(fuzhuhesuanStr)) {
                                        b = fuzhuhesuanStr;
                                        if (StrUtil.isNotBlank(byZdyFuZhu.get("resultStr").toString())) {
                                            b += "," + byZdyFuZhu.get("resultStr").toString();
                                        }
                                    }
                                    cklist.get(i).setFuzhu(a).setFuzhuhesuan(b).setIndex(i);

                                }
                                if (StringUtils.isNotBlank(searchMap.get("requirement")) && StringUtils.isNotBlank(searchMap.get("value"))) {
                                    switch (searchMap.get("requirement")) {
                                        case "ccode":
                                            cklist = list.stream().filter(ck -> ck.getCcode().startsWith(searchMap.get("value"))).collect(Collectors.toList());
                                            break;
                                        case "ccodeName":
                                            cklist = list.stream().filter(ck -> ck.getCcodeName().contains(searchMap.get("value"))).collect(Collectors.toList());
                                            break;
                                        case "cclass":
                                            cklist = list.stream().filter(ck -> ck.getCclass().equals(searchMap.get("value"))).collect(Collectors.toList());
                                            break;
                                        case "bprogerty":
                                            String bprogerty = searchMap.get("value").contains("借") ? "1" : searchMap.get("value").contains("贷") ? "0" : "";
                                            if (!bprogerty.equals("")) {
                                                cklist = list.stream().filter(ck -> ck.getBprogerty().equals(bprogerty)).collect(Collectors.toList());
                                            }
                                            break;
                                        case "fuzhu":
                                            cklist = list.stream().filter(ck -> StrUtil.isNotBlank(ck.getFuzhu()) && ck.getFuzhu().contains(searchMap.get("value"))).collect(Collectors.toList());
                                            break;
                                        case "menterage":
                                            cklist = list.stream().filter(ck -> StrUtil.isNotBlank(ck.getMenterage()) && ck.getMenterage().equals(searchMap.get("value"))).collect(Collectors.toList());
                                            break;
                                        case "currencyType":
                                            cklist = list.stream().filter(ck -> StrUtil.isNotBlank(ck.getCurrencyType()) && ck.getCurrencyType().equals(searchMap.get("value"))).collect(Collectors.toList());
                                            break;
                                    }
                                    total = cklist.size();
                                }
                                totalAR.set(total);
                                return Mono.just(cklist.stream());
                            });
                })
                .map(a -> R.page(a.collect(Collectors.toList()), pageable, (totalAR.get())));
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

    public Map findByZdyFuZhu(List<FuzhuHesuan> list, CodeKemu ck) {
        String result = "";
        String resultStr = "";
        if (StringUtils.isNotBlank(ck.getCdfine1()) && ck.getCdfine1().equals("1")) {
            List<FuzhuHesuan> collect = list.stream().filter(zdy -> zdy.getCdfine() != null && zdy.getCdfine() == 1).collect(Collectors.toList());
            if (collect.size() > 0) {
                result += collect.get(0).getCname() + ",";
                resultStr += "cdfine1,";
            }
        }
        if (StringUtils.isNotBlank(ck.getCdfine2()) && ck.getCdfine2().equals("1")) {
            List<FuzhuHesuan> collect = list.stream().filter(zdy -> zdy.getCdfine() != null && zdy.getCdfine() == 2).collect(Collectors.toList());
            if (collect.size() > 0) {
                result += collect.get(0).getCname() + ",";
                resultStr += "cdfine2,";
            }
        }
        if (StringUtils.isNotBlank(ck.getCdfine3()) && ck.getCdfine3().equals("1")) {
            List<FuzhuHesuan> collect = list.stream().filter(zdy -> zdy.getCdfine() != null && zdy.getCdfine() == 3).collect(Collectors.toList());
            if (collect.size() > 0) {
                result += collect.get(0).getCname() + ",";
                resultStr += "cdfine3,";
            }
        }
        if (StringUtils.isNotBlank(ck.getCdfine4()) && ck.getCdfine4().equals("1")) {
            List<FuzhuHesuan> collect = list.stream().filter(zdy -> zdy.getCdfine() != null && zdy.getCdfine() == 4).collect(Collectors.toList());
            if (collect.size() > 0) {
                result += collect.get(0).getCname() + ",";
                resultStr += "cdfine4,";
            }
        }
        if (StringUtils.isNotBlank(ck.getCdfine5()) && ck.getCdfine5().equals("1")) {
            List<FuzhuHesuan> collect = list.stream().filter(zdy -> zdy.getCdfine() != null && zdy.getCdfine() == 5).collect(Collectors.toList());
            if (collect.size() > 0) {
                result += collect.get(0).getCname() + ",";
                resultStr += "cdfine5,";
            }
        }
        if (StringUtils.isNotBlank(ck.getCdfine6()) && ck.getCdfine6().equals("1")) {
            List<FuzhuHesuan> collect = list.stream().filter(zdy -> zdy.getCdfine() != null && zdy.getCdfine() == 6).collect(Collectors.toList());
            if (collect.size() > 0) {
                result += collect.get(0).getCname() + ",";
                resultStr += "cdfine6,";
            }
        }
        if (StringUtils.isNotBlank(ck.getCdfine7()) && ck.getCdfine7().equals("1")) {
            List<FuzhuHesuan> collect = list.stream().filter(zdy -> zdy.getCdfine() != null && zdy.getCdfine() == 7).collect(Collectors.toList());
            if (collect.size() > 0) {
                result += collect.get(0).getCname() + ",";
                resultStr += "cdfine7,";
            }
        }
        if (StringUtils.isNotBlank(ck.getCdfine8()) && ck.getCdfine8().equals("1")) {
            List<FuzhuHesuan> collect = list.stream().filter(zdy -> zdy.getCdfine() != null && zdy.getCdfine() == 8).collect(Collectors.toList());
            if (collect.size() > 0) {
                result += collect.get(0).getCname() + ",";
                resultStr += "cdfine8,";
            }
        }
        if (StringUtils.isNotBlank(ck.getCdfine9()) && ck.getCdfine9().equals("1")) {
            List<FuzhuHesuan> collect = list.stream().filter(zdy -> zdy.getCdfine() != null && zdy.getCdfine() == 9).collect(Collectors.toList());
            if (collect.size() > 0) {
                result += collect.get(0).getCname() + ",";
                resultStr += "cdfine9,";
            }
        }
        if (StringUtils.isNotBlank(ck.getCdfine10()) && ck.getCdfine10().equals("1")) {
            List<FuzhuHesuan> collect = list.stream().filter(zdy -> zdy.getCdfine() != null && zdy.getCdfine() == 10).collect(Collectors.toList());
            if (collect.size() > 0) {
                result += collect.get(0).getCname() + ",";
                resultStr += "cdfine10,";
            }
        }
        if (StringUtils.isNotBlank(ck.getCdfine11()) && ck.getCdfine11().equals("1")) {
            List<FuzhuHesuan> collect = list.stream().filter(zdy -> zdy.getCdfine() != null && zdy.getCdfine() == 11).collect(Collectors.toList());
            if (collect.size() > 0) {
                result += collect.get(0).getCname() + ",";
                resultStr += "cdfine11,";
            }
        }
        if (StringUtils.isNotBlank(ck.getCdfine12()) && ck.getCdfine12().equals("1")) {
            List<FuzhuHesuan> collect = list.stream().filter(zdy -> zdy.getCdfine() != null && zdy.getCdfine() == 12).collect(Collectors.toList());
            if (collect.size() > 0) {
                result += collect.get(0).getCname() + ",";
                resultStr += "cdfine12,";
            }
        }
        if (StringUtils.isNotBlank(ck.getCdfine13()) && ck.getCdfine13().equals("1")) {
            List<FuzhuHesuan> collect = list.stream().filter(zdy -> zdy.getCdfine() != null && zdy.getCdfine() == 13).collect(Collectors.toList());
            if (collect.size() > 0) {
                result += collect.get(0).getCname() + ",";
                resultStr += "cdfine13,";
            }
        }
        if (StringUtils.isNotBlank(ck.getCdfine14()) && ck.getCdfine14().equals("1")) {
            List<FuzhuHesuan> collect = list.stream().filter(zdy -> zdy.getCdfine() != null && zdy.getCdfine() == 14).collect(Collectors.toList());
            if (collect.size() > 0) {
                result += collect.get(0).getCname() + ",";
                resultStr += "cdfine14,";
            }
        }
        if (StringUtils.isNotBlank(ck.getCdfine15()) && ck.getCdfine15().equals("1")) {
            List<FuzhuHesuan> collect = list.stream().filter(zdy -> zdy.getCdfine() != null && zdy.getCdfine() == 15).collect(Collectors.toList());
            if (collect.size() > 0) {
                result += collect.get(0).getCname() + ",";
                resultStr += "cdfine15,";
            }
        }
        if (StringUtils.isNotBlank(ck.getCdfine16()) && ck.getCdfine16().equals("1")) {
            List<FuzhuHesuan> collect = list.stream().filter(zdy -> zdy.getCdfine() != null && zdy.getCdfine() == 16).collect(Collectors.toList());
            if (collect.size() > 0) {
                result += collect.get(0).getCname() + ",";
                resultStr += "cdfine16,";
            }
        }
        if (StringUtils.isNotBlank(ck.getCdfine17()) && ck.getCdfine17().equals("1")) {
            List<FuzhuHesuan> collect = list.stream().filter(zdy -> zdy.getCdfine() != null && zdy.getCdfine() == 17).collect(Collectors.toList());
            if (collect.size() > 0) {
                result += collect.get(0).getCname() + ",";
                resultStr += "cdfine17,";
            }
        }
        if (StringUtils.isNotBlank(ck.getCdfine18()) && ck.getCdfine18().equals("1")) {
            List<FuzhuHesuan> collect = list.stream().filter(zdy -> zdy.getCdfine() != null && zdy.getCdfine() == 18).collect(Collectors.toList());
            if (collect.size() > 0) {
                result += collect.get(0).getCname() + ",";
                resultStr += "cdfine18,";
            }
        }
        if (StringUtils.isNotBlank(ck.getCdfine19()) && ck.getCdfine19().equals("1")) {
            List<FuzhuHesuan> collect = list.stream().filter(zdy -> zdy.getCdfine() != null && zdy.getCdfine() == 19).collect(Collectors.toList());
            if (collect.size() > 0) {
                result += collect.get(0).getCname() + ",";
                resultStr += "cdfine19,";
            }
        }
        if (StringUtils.isNotBlank(ck.getCdfine20()) && ck.getCdfine20().equals("1")) {
            List<FuzhuHesuan> collect = list.stream().filter(zdy -> zdy.getCdfine() != null && zdy.getCdfine() == 20).collect(Collectors.toList());
            if (collect.size() > 0) {
                result += collect.get(0).getCname() + ",";
                resultStr += "cdfine20,";
            }
        }
        if (StringUtils.isNotBlank(ck.getCdfine21()) && ck.getCdfine21().equals("1")) {
            List<FuzhuHesuan> collect = list.stream().filter(zdy -> zdy.getCdfine() != null && zdy.getCdfine() == 21).collect(Collectors.toList());
            if (collect.size() > 0) {
                result += collect.get(0).getCname() + ",";
                resultStr += "cdfine21,";
            }
        }
        if (StringUtils.isNotBlank(ck.getCdfine22()) && ck.getCdfine22().equals("1")) {
            List<FuzhuHesuan> collect = list.stream().filter(zdy -> zdy.getCdfine() != null && zdy.getCdfine() == 22).collect(Collectors.toList());
            if (collect.size() > 0) {
                result += collect.get(0).getCname() + ",";
                resultStr += "cdfine22,";
            }
        }
        if (StringUtils.isNotBlank(ck.getCdfine23()) && ck.getCdfine23().equals("1")) {
            List<FuzhuHesuan> collect = list.stream().filter(zdy -> zdy.getCdfine() != null && zdy.getCdfine() == 23).collect(Collectors.toList());
            if (collect.size() > 0) {
                result += collect.get(0).getCname() + ",";
                resultStr += "cdfine23,";
            }
        }
        if (StringUtils.isNotBlank(ck.getCdfine24()) && ck.getCdfine24().equals("1")) {
            List<FuzhuHesuan> collect = list.stream().filter(zdy -> zdy.getCdfine() != null && zdy.getCdfine() == 24).collect(Collectors.toList());
            if (collect.size() > 0) {
                result += collect.get(0).getCname() + ",";
                resultStr += "cdfine24,";
            }
        }
        if (StringUtils.isNotBlank(ck.getCdfine25()) && ck.getCdfine25().equals("1")) {
            List<FuzhuHesuan> collect = list.stream().filter(zdy -> zdy.getCdfine() != null && zdy.getCdfine() == 25).collect(Collectors.toList());
            if (collect.size() > 0) {
                result += collect.get(0).getCname() + ",";
                resultStr += "cdfine25,";
            }
        }
        if (StringUtils.isNotBlank(ck.getCdfine26()) && ck.getCdfine26().equals("1")) {
            List<FuzhuHesuan> collect = list.stream().filter(zdy -> zdy.getCdfine() != null && zdy.getCdfine() == 26).collect(Collectors.toList());
            if (collect.size() > 0) {
                result += collect.get(0).getCname() + ",";
                resultStr += "cdfine26,";
            }
        }
        if (StringUtils.isNotBlank(ck.getCdfine27()) && ck.getCdfine27().equals("1")) {
            List<FuzhuHesuan> collect = list.stream().filter(zdy -> zdy.getCdfine() != null && zdy.getCdfine() == 27).collect(Collectors.toList());
            if (collect.size() > 0) {
                result += collect.get(0).getCname() + ",";
                resultStr += "cdfine27,";
            }
        }
        if (StringUtils.isNotBlank(ck.getCdfine28()) && ck.getCdfine28().equals("1")) {
            List<FuzhuHesuan> collect = list.stream().filter(zdy -> zdy.getCdfine() != null && zdy.getCdfine() == 28).collect(Collectors.toList());
            if (collect.size() > 0) {
                result += collect.get(0).getCname() + ",";
                resultStr += "cdfine28,";
            }
        }
        if (StringUtils.isNotBlank(ck.getCdfine29()) && ck.getCdfine29().equals("1")) {
            List<FuzhuHesuan> collect = list.stream().filter(zdy -> zdy.getCdfine() != null && zdy.getCdfine() == 29).collect(Collectors.toList());
            if (collect.size() > 0) {
                result += collect.get(0).getCname() + ",";
                resultStr += "cdfine29,";
            }
        }
        if (StringUtils.isNotBlank(ck.getCdfine30()) && ck.getCdfine30().equals("1")) {
            List<FuzhuHesuan> collect = list.stream().filter(zdy -> zdy.getCdfine() != null && zdy.getCdfine() == 30).collect(Collectors.toList());
            if (collect.size() > 0) {
                result += collect.get(0).getCname() + ",";
                resultStr += "cdfine30,";
            }
        }

        Map map = new HashMap<>();
        map.put("result", result.equals("") ? "" : result.substring(0, result.length() - 1));
        map.put("resultStr", resultStr.equals("") ? "" : resultStr.substring(0, resultStr.length() - 1));
        return map;
    }

    /**
     * 科目树形结构
     *
     * @return
     */
    @PostMapping("/company/company_treeCode")
    public Mono<R> company_treeCode(String iyear) {
        return codeKemuRepository.company_treeCode(iyear)
                .collectList()
                .map(list -> R.ok().setResult(getChild("0", list)));
    }


    @PostMapping("/company/company_treeCodeAndFlag1")
    public Mono<R> company_treeCodeAndFlag1(String iyear) {
        return codeKemuRepository.company_treeCodeAndFlag1(iyear)
                .collectList()
                .map(list -> R.ok().setResult(getChild("0", list)));
    }


    @PostMapping("/company/findByIyearOrderByCcode")
    public Mono<R> findByIyearOrderByCcode(String iyear) {
        return codeKemuRepository.findByIyearOrderByCcode(iyear)
                .collectList()
                .map(list -> R.ok().setResult(list));
    }


    public List<Map<String, Object>> getChild(String pid, List<CodeKemu> allList) {
        List<Map<String, Object>> childList = new ArrayList<>();//用于保存子节点的list;
        for (CodeKemu ms : allList) {
            if (ms.getSuperiorCcode().equals(pid)) {//判断传入的父id是否等于自身的，如果是，就说明自己是子节点
                Map<String, Object> map = new HashMap<>();
                map.put("title", ms.getCcode() + "-" + ms.getCcodeName());
                map.put("value", ms.getCcode()); // 编码_级次
                map.put("key", ms.getCcode());
                map.put("children", new Object[]{});
                childList.add(map); //加入子节点
            }
        }
        for (Map<String, Object> map : childList) {//遍历子节点，继续递归判断每个子节点是否还含有子节点
            List<Map<String, Object>> tList = getChild(String.valueOf(map.get("key")), allList);
            map.put("children", tList);
        }
        return childList;
    }

    /**
     * 计算科目数量
     *
     * @return reactor.core.publisher.Mono
     **/
    @PostMapping("company/company_countfindAll")
    public Mono countfindAll(String iyear, String databasenum) {
        return codeKemuRepository.company_countfindAll(databasenum, iyear).map(a -> R.ok().setResult(a));
    }

    @PostMapping("company/company_saveAll")
    public Mono company_saveAll(@RequestBody List<CodeKemu> list) {
        return codeKemuRepository.saveAll(list).collectList().map(a -> R.ok().setResult(a));
    }

    /**
     * 科目删除前判断现金流量对方科目、指定科目、凭证表 是有存在对应的科目
     *
     * @return
     **/
    @Transactional
    @PostMapping("company/company_delFindByCodekemu")
    public Mono<R> company_delFindByCodekemu(String ccode, String iyear, String databasenum) {
        Map map = new HashMap();
        map.put("code", "200");
        map.put("msg", "");
        // 凭证表
        return accvoucherRepository.countByCcode(ccode, iyear)
        .flatMap(accvoucher -> {
            if (accvoucher > 0) {
                map.put("code", "400");
                map.put("msg", "accvoucher");
            }
            return accvoucherRepository.countByQCCcode(ccode, iyear + "00")
            .flatMap(qc -> {
                if (qc > 0) {
                    map.put("code", "400");
                    map.put("msg", "qc");
                }
                return accvoucherSettingRepository.findAllByCcode(ccode).collectList()
                .flatMap(set -> {
                    if (set.size() > 0) {
                        map.put("code", "400");
                        map.put("msg", "accvoucherSetting");
                    }
                    return Mono.just(map);
                });
            });

        }).map(a -> R.ok().setResult(a));
    }

    @PostMapping("company/company_findByTemplateIdCode")
    public Mono<R> findByTemplateIdCode(String templateid) {
        return codeKemuRepository.findAllByTemplateIdOrderByCcode(templateid).collectList().map(o -> R.ok().setResult(o));
    }

    @PostMapping("group/findGroupByTemplateIdCode")
    public Mono<R> findGroupByTemplateIdCode(String templateid) {
        return groupCodeKemuRepository.findAllByTemplateIdOrderByCcode(templateid).collectList().map(o -> R.ok().setResult(o));
    }

    @PostMapping("company/company_findByPzCcodeStr")
    public Mono<R> company_findByPzCcodeStr(String iyear) {
        return accvoucherRepository.findByCcodeStr(iyear).collectList().map(o -> R.ok().setResult(o));
    }

    @PostMapping("company/company_countByLikeCcode")
    public Mono<R> company_countByLikeCcode(String ccode) {
        return accvoucherRepository.countByLikeCcode(ccode + "%").map(o -> R.ok().setResult(o));
    }

    /**
     * 现金流量科目
     *
     * @param iyear
     * @return
     */
    @PostMapping("company/company_findByxjllJieCodeStr")
    public Mono<R> company_findByxjllJieCodeStr(String iyear) {
        return projectCashCodeRepository.findByJieCodeStr(iyear).collectList().map(o -> R.ok().setResult(o));
    }

    @PostMapping("company/company_findByxjllDaiCodeStr")
    public Mono<R> company_findByxjllDaiCodeStr(String iyear) {
        return projectCashCodeRepository.findByDaiCodeStr(iyear).collectList().map(o -> R.ok().setResult(o));
    }

    /**
     * 现金科目
     *
     * @param iyear
     * @return
     */
    @PostMapping("company/company_findByXjCcodeStr")
    public Mono<R> company_findByXjCcodeStr(String iyear) {
        return kmCashFlowRepository.findAllByCodeStr(iyear).collectList().map(o -> R.ok().setResult(o));
    }

    /**
     * 已设置期初科目
     *
     * @param iyear
     * @return
     */
    @PostMapping("company/company_findByQcCcodeStr")
    public Mono<R> company_findByQcCcodeStr(String iyear) {
        return accvoucherRepository.findByQcCcodeStr(iyear + "00").collectList().map(o -> R.ok().setResult(o));
    }

    /**
     * 没有发生数、期初 清空科目
     *
     * @return
     */
    @PostMapping("company/company_delAll")
    public Mono company_delAll(@RequestBody Map map) {
        // 是否独立账套
        Boolean independent = (Boolean) map.get("independent");
        String userName = map.get("userName").toString();
        String iyear = map.get("iyear").toString();
        List<CodeKemu> list = JSON.parseArray(map.get("list").toString(), CodeKemu.class);
        list.sort(Comparator.comparing(CodeKemu::getCcode).reversed());
        // 不可删除
        List<String> noBend0Codelist = new ArrayList<>();
        // 可删除
        List<CodeKemu> Bend1Codelist = new ArrayList<>();

        return codeKemuRepository.findByIyearOrderByCcode(iyear).collectList()
                .flatMap(iyearCodeList -> {
                    list.stream().distinct().forEach(a -> {
                        Bend1Codelist.add(a);
                        iyearCodeList.removeIf(li -> li.getCcode().equals(a.getCcode()));

                        // 判断科目是否含有下级
                        List<AAtemp> childDel = getChildDel(a.getCcode(), iyearCodeList);
                        if (childDel.size() > 0) {
                            noBend0Codelist.add(a.getCcode());
                        }
                    });
                    // 过滤出上级科目,过滤集合中是否还有下级,没有修改末级标识
                    List<String> editflag = new ArrayList<>();
                    List<String> collect = list.stream().filter(a -> !a.getSuperiorCcode().equals("0")).map(CodeKemu::getSuperiorCcode).distinct().collect(Collectors.toList());
                    collect.forEach(f -> {
                        List<AAtemp> childDel = getChildDel(f, iyearCodeList);
                        if (childDel.size() == 0) {
                            editflag.add(f);
                        }
                    });
                    // 不能删除的科目去重
                    List<String> listStr = noBend0Codelist.stream().distinct().collect(Collectors.toList());

                    // 修改上级末级标志
                    if (editflag.size() == 0) {
                        editflag.add("0");
                    }
                    Mono editFlg = codeKemuRepository.updateBendByIds("1", iyear, editflag).then();
                    // 删除科目
                    // 去除包含不能删除的科目编码
                    List<CodeKemu> str = Bend1Codelist.stream().filter(a -> listStr.indexOf(a.getCcode()) == -1).collect(Collectors.toList());

                    // 集团账套-审计会计科目记录
                    List<AuditCodeKemu> auditCodeKemuList = new ArrayList<>();
                    for (int i = 0; i < str.size(); i++) {
                        CodeKemu v = str.get(i);
                        AuditCodeKemu a = new AuditCodeKemu();
                        a.setUniqueCode(v.getUniqueCode()).setUniqueAccStandard(v.getUniqueAccStandard())
                                .setCclass(v.getCclass()).setCcode(v.getCcode()).setCcodeName(v.getCcodeName())
                                .setIgrade(v.getIgrade()).setBprogerty(v.getBprogerty()).setCbookType(v.getCbookType())
                                .setBperson(v.getBperson()).setBcus(v.getBcus()).setBsup(v.getBsup()).setBdept(v.getBdept())
                                .setBitem(v.getBitem()).setCassItem(v.getCassItem()).setBnum(v.getBnum()).setMenterage(v.getMenterage())
                                .setBstock(v.getBstock()).setBcash(v.getBcash()).setBbank(v.getBbank()).setBend(v.getBend())
                                .setBdaybook(v.getBdaybook()).setFlag(v.getFlag()).setIyear(v.getIyear()).setTemplateId(v.getTemplateId())
                                .setCurrency(v.getCurrency()).setCurrencyType(v.getCurrencyType()).setLowerControl(v.getLowerControl())
                                .setFuzhuControl(v.getFuzhuControl()).setSuperiorCcode(v.getSuperiorCcode()).setBcashEquivalence(v.getBcashEquivalence())
                                .setControlled(v.getControlled()).setPxjz(v.getPxjz()).setXjll(v.getXjll()).setCyfx(v.getCyfx())
                                .setCdfine1(v.getCdfine1())
                                .setCdfine2(v.getCdfine2())
                                .setCdfine3(v.getCdfine3())
                                .setCdfine4(v.getCdfine4())
                                .setCdfine5(v.getCdfine5())
                                .setCdfine6(v.getCdfine6())
                                .setCdfine7(v.getCdfine7())
                                .setCdfine8(v.getCdfine8())
                                .setCdfine9(v.getCdfine9())
                                .setCdfine10(v.getCdfine10())
                                .setCdfine11(v.getCdfine11())
                                .setCdfine12(v.getCdfine12())
                                .setCdfine13(v.getCdfine13())
                                .setCdfine14(v.getCdfine14())
                                .setCdfine15(v.getCdfine15())
                                .setCdfine16(v.getCdfine16())
                                .setCdfine17(v.getCdfine17())
                                .setCdfine18(v.getCdfine18())
                                .setCdfine19(v.getCdfine19())
                                .setCdfine20(v.getCdfine20())
                                .setCdfine21(v.getCdfine21())
                                .setCdfine22(v.getCdfine22())
                                .setCdfine23(v.getCdfine23())
                                .setCdfine24(v.getCdfine24())
                                .setCdfine25(v.getCdfine25())
                                .setCdfine26(v.getCdfine26())
                                .setCdfine27(v.getCdfine27())
                                .setCdfine28(v.getCdfine28())
                                .setCdfine29(v.getCdfine29())
                                .setCdfine30(v.getCdfine30())
                                .setYusuan(v.getYusuan())
                                .setYsYsly(v.getYsYsly())
                                .setYsZcgnfl(v.getYsZcgnfl())
                                .setYsZfzcjjfl(v.getYsZfzcjjfl())
                                .setYsBmzcjjfl(v.getYsBmzcjjfl())
                                .setProjectClassId(v.getProjectClassId())
                                .setIsDel(v.getIsDel()).setDelName(v.getDelName()).setDelDate(v.getDelDate())
                                .setOptMethod("1").setOptUsername(userName).setOptTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                        auditCodeKemuList.add(a);
                    }
                    Mono delData = !independent ? Mono.zip(auditCodeKemuRepository.saveAll(auditCodeKemuList).collectList(), codeKemuRepository.deleteAll(str)) : codeKemuRepository.deleteAll(str).then();

                    return listStr.size() > 0 ? delData.then(Mono.just(listStr)) : Mono.zip(editFlg, delData).then(Mono.just(R.ok()));
                }).map(o -> R.ok().setResult(o));
    }

    /**
     * 假删除
     *
     * @param map
     * @return
     */
    @PostMapping("company/company_fakeDelAll")
    public Mono company_fakeDelAll(@RequestBody Map map) {
        // 是否独立账套
        Boolean independent = (Boolean) map.get("independent");
        String userName = map.get("userName").toString();
        String iyear = map.get("iyear").toString();
        List<CodeKemu> list = JSON.parseArray(map.get("list").toString(), CodeKemu.class);
        list.sort(Comparator.comparing(CodeKemu::getCcode).reversed());
        // 不可删除
        List<String> noBend0Codelist = new ArrayList<>();
        // 可删除
        List<CodeKemu> Bend1Codelist = new ArrayList<>();

        return codeKemuRepository.findByIyearOrderByCcode(iyear).collectList()
                .flatMap(iyearCodeList -> {
                    list.stream().distinct().forEach(a -> {
                        Bend1Codelist.add(a);
                        iyearCodeList.removeIf(li -> li.getCcode().equals(a.getCcode()));

                        // 判断科目是否含有下级
                        List<AAtemp> childDel = getChildDel(a.getCcode(), iyearCodeList);
                        if (childDel.size() > 0) {
                            noBend0Codelist.add(a.getCcode());
                        }
                    });
                    // 过滤出上级科目,过滤集合中是否还有下级,没有修改末级标识
                    List<String> editflag = new ArrayList<>();
                    List<String> collect = list.stream().filter(a -> !a.getSuperiorCcode().equals("0")).map(CodeKemu::getSuperiorCcode).distinct().collect(Collectors.toList());
                    collect.forEach(f -> {
                        List<AAtemp> childDel = getChildDel(f, iyearCodeList);
                        if (childDel.size() == 0) {
                            editflag.add(f);
                        }
                    });
                    // 不能删除的科目去重
                    List<String> listStr = noBend0Codelist.stream().distinct().collect(Collectors.toList());

                    // 修改上级末级标志
                    if (editflag.size() == 0) {
                        editflag.add("0");
                    }
                    Mono editFlg = codeKemuRepository.updateBendByIds("1", iyear, editflag).then();
                    // 删除科目
                    // 去除包含不能删除的科目编码
                    List<CodeKemu> str = Bend1Codelist.stream().filter(a -> listStr.indexOf(a.getCcode()) == -1).collect(Collectors.toList());

                    // 集团账套-审计会计科目记录
                    List<AuditCodeKemu> auditCodeKemuList = new ArrayList<>();
                    for (int i = 0; i < str.size(); i++) {
                        CodeKemu v = str.get(i);
                        AuditCodeKemu a = new AuditCodeKemu();
                        a.setUniqueCode(v.getUniqueCode()).setUniqueAccStandard(v.getUniqueAccStandard())
                                .setCclass(v.getCclass()).setCcode(v.getCcode()).setCcodeName(v.getCcodeName())
                                .setIgrade(v.getIgrade()).setBprogerty(v.getBprogerty()).setCbookType(v.getCbookType())
                                .setBperson(v.getBperson()).setBcus(v.getBcus()).setBsup(v.getBsup()).setBdept(v.getBdept())
                                .setBitem(v.getBitem()).setCassItem(v.getCassItem()).setBnum(v.getBnum()).setMenterage(v.getMenterage())
                                .setBstock(v.getBstock()).setBcash(v.getBcash()).setBbank(v.getBbank()).setBend(v.getBend())
                                .setBdaybook(v.getBdaybook()).setFlag(v.getFlag()).setIyear(v.getIyear()).setTemplateId(v.getTemplateId())
                                .setCurrency(v.getCurrency()).setCurrencyType(v.getCurrencyType()).setLowerControl(v.getLowerControl())
                                .setFuzhuControl(v.getFuzhuControl()).setSuperiorCcode(v.getSuperiorCcode()).setBcashEquivalence(v.getBcashEquivalence())
                                .setControlled(v.getControlled()).setPxjz(v.getPxjz()).setXjll(v.getXjll()).setCyfx(v.getCyfx())
                                .setCdfine1(v.getCdfine1())
                                .setCdfine2(v.getCdfine2())
                                .setCdfine3(v.getCdfine3())
                                .setCdfine4(v.getCdfine4())
                                .setCdfine5(v.getCdfine5())
                                .setCdfine6(v.getCdfine6())
                                .setCdfine7(v.getCdfine7())
                                .setCdfine8(v.getCdfine8())
                                .setCdfine9(v.getCdfine9())
                                .setCdfine10(v.getCdfine10())
                                .setCdfine11(v.getCdfine11())
                                .setCdfine12(v.getCdfine12())
                                .setCdfine13(v.getCdfine13())
                                .setCdfine14(v.getCdfine14())
                                .setCdfine15(v.getCdfine15())
                                .setCdfine16(v.getCdfine16())
                                .setCdfine17(v.getCdfine17())
                                .setCdfine18(v.getCdfine18())
                                .setCdfine19(v.getCdfine19())
                                .setCdfine20(v.getCdfine20())
                                .setCdfine21(v.getCdfine21())
                                .setCdfine22(v.getCdfine22())
                                .setCdfine23(v.getCdfine23())
                                .setCdfine24(v.getCdfine24())
                                .setCdfine25(v.getCdfine25())
                                .setCdfine26(v.getCdfine26())
                                .setCdfine27(v.getCdfine27())
                                .setCdfine28(v.getCdfine28())
                                .setCdfine29(v.getCdfine29())
                                .setCdfine30(v.getCdfine30())
                                .setYusuan(v.getYusuan())
                                .setYsYsly(v.getYsYsly())
                                .setYsZcgnfl(v.getYsZcgnfl())
                                .setYsZfzcjjfl(v.getYsZfzcjjfl())
                                .setYsBmzcjjfl(v.getYsBmzcjjfl())
                                .setProjectClassId(v.getProjectClassId())
                                .setIsDel(v.getIsDel()).setDelName(v.getDelName()).setDelDate(v.getDelDate())
                                .setOptMethod("1").setOptUsername(userName).setOptTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                        auditCodeKemuList.add(a);
                    }

                    List<String> collect1 = str.stream().map(CodeKemu::getCcode).collect(Collectors.toList());
                    if (auditCodeKemuList.size() == 0) {
                        AuditCodeKemu a = new AuditCodeKemu();
                        a.setId("0");
                        auditCodeKemuList.add(a);
                    }
                    if (collect1.size() == 0) {
                        collect1.add("1");
                    }
                    // 上边list==0情况，未处理
                    Mono delData = !independent ? Mono.zip(auditCodeKemuRepository.saveAll(auditCodeKemuList).collectList(), codeKemuRepository.updateIsDelByCodeAndIyear("1", userName, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), iyear, collect1)) : codeKemuRepository.updateIsDelByCodeAndIyear("1", userName, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), iyear, collect1).then();
                    return listStr.size() > 0 ? delData.then(Mono.just(listStr)) : Mono.zip(editFlg, delData).then(Mono.just(R.ok()));
                }).map(o -> R.ok().setResult(o));
    }

    public List<AAtemp> getChildDel2(String pid, List<CodeKemu> allList) {
        List<AAtemp> childList = new ArrayList<>();//用于保存子节点的list;
        List<AAtemp> childList2 = new ArrayList<>();//用于保存子节点的list;
        for (CodeKemu ms : allList) {
            if (ms.getSuperiorCcode().equals(pid) && ms.getBend().equals("0")) {//判断传入的父id是否等于自身的，如果是，就说明自己是子节点
                AAtemp a = new AAtemp();
                a.setTitle(ms.getCcode() + "-" + ms.getCcodeName()).setValue(ms.getCcode() + "-" + ms.getCcodeName()).setKey(ms.getCcode()).setBend(ms.getBend()).setChildren(new ArrayList<>()).setParentId(ms.getSuperiorCcode());
                childList.add(a); //加入子节点
            }
        }
        for (AAtemp map : childList) {//遍历子节点，继续递归判断每个子节点是否还含有子节点
            if (map.getBend().equals("0")) {
                List<AAtemp> tList = getChildDel2(map.getKey(), allList);
                map.setChildren(tList);
                tList.forEach(v -> {
                    AAtemp a = new AAtemp();
                    a.setTitle(v.getTitle()).setValue(v.getValue()).setKey(v.getKey()).setBend(v.getBend()).setChildren(new ArrayList<>()).setParentId(v.getParentId());
                    childList2.add(a); //加入子节点
                });
            }
        }

        childList.addAll(childList2);
        return childList;
    }

    public List<AAtemp> getChildDel(String pid, List<CodeKemu> allList) {
        List<AAtemp> childList = new ArrayList<>();//用于保存子节点的list;
        for (CodeKemu ms : allList) {
            if (ms.getSuperiorCcode().equals(pid)) {//判断传入的父id是否等于自身的，如果是，就说明自己是子节点
                AAtemp a = new AAtemp();
                a.setTitle(ms.getCcodeName()).setValue(ms.getCcode()).setKey(ms.getCcode()).setBend(ms.getBend()).setChildren(new ArrayList<>()).setParentId(ms.getSuperiorCcode());
                childList.add(a); //加入子节点
            }
        }
        for (AAtemp map : childList) {//遍历子节点，继续递归判断每个子节点是否还含有子节点
            List<AAtemp> tList = getChildDel(map.getKey(), allList);
            map.setChildren(tList);
        }
        return childList;
    }


    @GetMapping("company/company_findAllCodeOrderByAsc")
    public Mono<R> company_findAllCodeOrderByAsc() {
        return codeKemuRepository.findAllCodeOrderByAsc().collectList().map(a -> R.ok().setResult(a));
    }

    @GetMapping("company/company_findCodeByYear")
    public Mono<R> company_findCodeByYear(String iyear){
        return codeKemuRepository.findByIyearOrderByCcode(iyear).collectList().map(a -> R.ok().setResult(a));
    }

    /**
     * 修改平行记账 || 差异分析 科目
     *
     * @return
     */
    @PostMapping("company/company_editCodePxjzOrCyfx")
    public Mono company_editCodePxjzOrCyfx(@RequestBody Map map) {
        List<String> list = Arrays.asList(map.get("data").toString().split(","));
        String type = map.get("type").toString();
        String flag = map.get("flag").toString();
        Mono pxjz = codeKemuRepository.editCodePxjz(flag, list).then(Mono.just(R.ok()));
        Mono cyfx = codeKemuRepository.editCodeCyfx(flag, list).then(Mono.just(R.ok()));
        Mono proItem = codeKemuRepository.editCodeProItem(flag, list).then(Mono.just(R.ok()));
        return type.equals("pxjz") ? pxjz : type.equals("cyfx") ? cyfx : proItem;
    }

    /**
     * 查询平行记账 || 差异分析 科目
     *
     * @param map
     * @return
     */
    @PostMapping("company/company_findAllCodePxjzOrCyfxOrderByAsc")
    public Mono company_findAllCodePxjzOrCyfxOrderByAsc(@RequestBody Map map, Pageable pageable) {
        // 当前页
        int page = map.get("page") == null ? 1 : Integer.parseInt(map.get("page").toString());
        // 页多少条
        int size = map.get("size") == null ? 10000 : Integer.parseInt(map.get("size").toString());
        AtomicReference<Long> totalAR = new AtomicReference(0);
        String flag = map.get("flag").toString();// 0待选\1已选
        String type = map.get("type").toString();
        String proItem = map.get("proItem").toString();
        Map<String, String> searchMap = ((HashMap<String, String>) map.get("searchConditon"));
        return codeKemuRepository.findAllCodeOrderByAsc().collectList()
                .flatMap(list -> {
                    List<CodeKemu> newlist = new ArrayList<>();
                    if ("pxjz".equals(type)) {
                        if ("0".equals(flag)) {
                            newlist = list.stream().filter(v -> StrUtil.isBlank(v.getPxjz()) || v.getPxjz().equals("0")).collect(Collectors.toList());
                        } else {
                            newlist = list.stream().filter(v -> StrUtil.isNotBlank(v.getPxjz()) && v.getPxjz().equals("1")).collect(Collectors.toList());
                        }
                    } else if ("cyfx".equals(type)) {
                        if ("0".equals(flag)) {
                            newlist = list.stream().filter(v -> StrUtil.isBlank(v.getCyfx()) || v.getCyfx().equals("0")).collect(Collectors.toList());
                        } else {
                            newlist = list.stream().filter(v -> StrUtil.isNotBlank(v.getCyfx()) && v.getCyfx().equals("1")).collect(Collectors.toList());
                        }
                    } else {
                        if ("0".equals(flag)) {
                            newlist = list.stream().filter(v -> StrUtil.isBlank(v.getProjectClassId())).collect(Collectors.toList());
                        } else {
                            newlist = list.stream().filter(v -> StrUtil.isNotBlank(v.getProjectClassId()) && v.getProjectClassId().equals(proItem)).collect(Collectors.toList());
                        }
                    }
                    long total = newlist.size();
                    newlist = newlist.stream().skip((page - 1) * size).limit(size).collect(Collectors.toList());
                    newlist.forEach(v -> {
                        // 5个辅助项
                        String fuzhu = "";
                        if (StringUtils.isNotBlank(v.getBperson()) && v.getBperson().equals("1")) {
                            fuzhu += "个人,";
                        }
                        if (StringUtils.isNotBlank(v.getBdept()) && v.getBdept().equals("1")) {
                            fuzhu += "部门,";
                        }
                        if (StringUtils.isNotBlank(v.getBcus()) && v.getBcus().equals("1")) {
                            fuzhu += "客户,";
                        }
                        if (StringUtils.isNotBlank(v.getBsup()) && v.getBsup().equals("1")) {
                            fuzhu += "供应商,";
                        }
                        if (StringUtils.isNotBlank(v.getBitem()) && v.getBitem().equals("1")) {
                            fuzhu += "项目,";
                        }
                        if (StringUtils.isNotBlank(v.getYsYsly()) && v.getYsYsly().equals("1")) {
                            fuzhu += "预算来源,";
                        }
                        if (StringUtils.isNotBlank(v.getYsZcgnfl()) && v.getYsZcgnfl().equals("1")) {
                            fuzhu += "支出功能分类,";
                        }
                        if (StringUtils.isNotBlank(v.getYsZfzcjjfl()) && v.getYsZfzcjjfl().equals("1")) {
                            fuzhu += "政府支出经济分类,";
                        }
                        if (StringUtils.isNotBlank(v.getYsBmzcjjfl()) && v.getYsBmzcjjfl().equals("1")) {
                            fuzhu += "部门支出经济分类,";
                        }
                        if (!"".equals(fuzhu)) {
                            v.setFuzhu(fuzhu.substring(0, fuzhu.length() - 1));
                        }
                    });


                    totalAR.set(total);
                    return Mono.just(newlist.stream().filter(item -> {
                        // 按条件过滤
                        if (StringUtils.isNotBlank(searchMap.get("requirement")) && StringUtils.isNotBlank(searchMap.get("value"))) {
                            String value = searchMap.get("value");
                            String dbValue = getFieldValueByFieldName(searchMap.get("requirement").trim(), item);
                            if (Objects.nonNull(dbValue) && !dbValue.contains(value)) {
                                return false;
                            }
                        }
                        return true;
                    }));
                })
                .map(a -> R.page(a.collect(Collectors.toList()), pageable, (totalAR.get())));
    }

    /**
     * 直接修改科目状态
     *
     * @return
     */
    @Transactional
    @PostMapping("company/company_editflag")
    public Mono company_editflag(@RequestBody Map map) {
        String flag = map.get("flag").toString();
        String iyear = map.get("iyear").toString();
        List<String> id = Arrays.asList(map.get("id").toString().split(","));
        return codeKemuRepository.company_editflag(flag, id, iyear).then(Mono.just(R.ok()));
    }

    /**
     * 计算一级科目编码长度
     *
     * @return
     */
    @GetMapping("company/company_findByFirstCcodeLength")
    public Mono company_findByFirstCcodeLength() {
        return codeKemuRepository.findByFirstCcodeLength().map(o -> R.ok().setResult(o));
    }

    @PostMapping("/company/company_proItemImport")
    public Mono<R> company_importCoke(@RequestPart("file") FilePart filePartParm, String proItem) throws Exception {
        // 获取表头个数
        AtomicInteger column = new AtomicInteger();
        Path tempFilePath = Files.createTempFile("", new String(filePartParm.filename().getBytes("ISO-8859-1"), "UTF-8"));
        return Mono.just(filePartParm)
                // 上传到临时目录
                .flatMap(filePart -> {
                    try {
                        return DataBufferUtils
                                .write(filePart.content(), AsynchronousFileChannel.open(tempFilePath, StandardOpenOption.WRITE), 0)
                                .doOnComplete(() -> log.info("上传成功"))
                                .collectList()
                                .map(item -> tempFilePath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return Mono.just("");
                })
                .flatMap(test -> {
                    XlsUtils3 excelReader = new XlsUtils3(tempFilePath.toString());
                    List<Object[]> list = excelReader.getExcelObj(tempFilePath.toString(), new String[]{"科目编码", "科目名称", ""}, 0);

                    column.set(3);
                    assert tempFilePath != null;
                    try {
                        Files.delete(tempFilePath);
                    } catch (IOException e) {
                    }
                    return Mono.just(list);
                })
                .flatMap(item -> {
                    Map mapArr = new HashMap();
                    mapArr.put("excellist", item);   // excel中内容
                    mapArr.put("code", "200");
                    mapArr.put("error", "");
                    return codeKemuRepository.findAllCodeOrderByAsc().collectList()
                            .flatMap(list -> {
                                mapArr.put("kmlist", list);// 科目list
                                return Mono.just(mapArr);
                            });
                })
                .flatMap(mapArr -> {
                    // 科目list
                    List<CodeKemu> kmlist = (List<CodeKemu>) mapArr.get("kmlist");
                    // 文件list
                    List<Object[]> excellist = (List<Object[]>) mapArr.get("excellist");

                    List<String> liststr = new ArrayList<>();
                    for (int i = 1; i < excellist.size(); i++) {
                        int finalI = i;
                        List<CodeKemu> collect = kmlist.stream().filter(v -> v.getCcode().equals(excellist.get(finalI)[0].toString().trim())).collect(Collectors.toList());
                        if (collect.size() > 0) {
                            liststr.add(collect.get(0).getCcode());
                        }
                    }

                    /**************************************END*************************************/
                    mapArr.put("list", liststr);
                    return Mono.just(mapArr);
                })
                .flatMapMany(mapArr -> {
                    List<String> codelist = (List<String>) mapArr.get("list");
                    return codeKemuRepository.editCodeProItem(proItem, codelist);
                }).collectList()
                .map(o -> R.ok().setResult(o));
    }

    /**
     * 获取本年利润科目及下级科目（如果有）
     *
     * @return
     */
    @PostMapping("/company/company_findByBenNianCode")
    public Mono company_findByBenNianCode() {
        return codeKemuRepository.company_findByBenNianCode().collectList().map(o -> R.ok().setResult(o));
    }

    @PostMapping("/company/company_findBySuperCode")
    public Mono company_findBySuperCode(String superCode) {
        return codeKemuRepository.company_findBySuperCode(superCode).map(o -> R.ok().setResult(o));
    }

    @Transactional
    @PostMapping("/delCodeByTemplate")
    public Mono delByTemplate(String templateId) {
        return groupCodeKemuRepository.delByTemplate(templateId).map(o -> R.ok().setResult(o));
    }

    @PostMapping("/company/company_findByLastCode")
    public Mono<R> company_findByLastCode(@RequestBody Map map, Pageable pageable) {
        String ccode = map.get("ccode").toString();
        String value = map.get("value").toString();
        String cclass = map.get("cclass").toString();
        String iyear = map.get("iyear").toString();
        // 当前页
        int page = map.get("page") == null ? 1 : Integer.parseInt(map.get("page").toString());
        // 页多少条
        int size = map.get("size") == null ? 10000 : Integer.parseInt(map.get("size").toString());
        AtomicReference<Long> totalAR = new AtomicReference(0);
        return codeKemuRepository.company_findByLastCode("1", iyear).collectList()
                .flatMap(list -> {
                    return fuzhuHesuanRepository.findAll().collectList()
                            .flatMap(fuzhulist -> {
                                List<CodeKemu> list123 = list.stream().skip((page - 1) * size).limit(size).collect(Collectors.toList());
                                if (StrUtil.isNotBlank(cclass)) {
                                    list123 = list.stream().filter(v -> v.getCclass().contains(cclass)).collect(Collectors.toList());
                                } else if (StrUtil.isNotBlank(ccode) && ccode.equals("ccode")) {
                                    list123 = list.stream().filter(v -> v.getCcode().startsWith(value)).collect(Collectors.toList());
                                } else if (StrUtil.isNotBlank(ccode) && ccode.equals("ccodeName")) {
                                    list123 = list.stream().filter(v -> v.getCcodeName().contains(value)).collect(Collectors.toList());
                                }
                                for (CodeKemu ms : list123) {
                                    // 5个辅助项
                                    String fuzhu = "";
                                    if (StringUtils.isNotBlank(ms.getBperson()) && ms.getBperson().equals("1")) {
                                        fuzhu += "个人,";
                                    }
                                    if (StringUtils.isNotBlank(ms.getBdept()) && ms.getBdept().equals("1")) {
                                        fuzhu += "部门,";
                                    }
                                    if (StringUtils.isNotBlank(ms.getBcus()) && ms.getBcus().equals("1")) {
                                        fuzhu += "客户,";
                                    }
                                    if (StringUtils.isNotBlank(ms.getBsup()) && ms.getBsup().equals("1")) {
                                        fuzhu += "供应商,";
                                    }
                                    if (StringUtils.isNotBlank(ms.getBitem()) && ms.getBitem().equals("1")) {
                                        fuzhu += "项目,";
                                    }
                                    if (StringUtils.isNotBlank(ms.getYsYsly()) && ms.getYsYsly().equals("1")) {
                                        fuzhu += "预算来源,";
                                    }
                                    if (StringUtils.isNotBlank(ms.getYsZcgnfl()) && ms.getYsZcgnfl().equals("1")) {
                                        fuzhu += "支出功能分类,";
                                    }
                                    if (StringUtils.isNotBlank(ms.getYsZfzcjjfl()) && ms.getYsZfzcjjfl().equals("1")) {
                                        fuzhu += "政府支出经济分类,";
                                    }
                                    if (StringUtils.isNotBlank(ms.getYsBmzcjjfl()) && ms.getYsBmzcjjfl().equals("1")) {
                                        fuzhu += "部门支出经济分类,";
                                    }
                                    if (!"".equals(fuzhu)) {
                                        ms.setFuzhu(fuzhu.substring(0, fuzhu.length() - 1));
                                    }
                                    Map byZdyFuZhu = findByZdyFuZhu(fuzhulist, ms);
                                    String a = "";
                                    if (StrUtil.isNotBlank(fuzhu)) {
                                        a = fuzhu;
                                        if (StrUtil.isNotBlank(byZdyFuZhu.get("result").toString())) {
                                            a += "," + byZdyFuZhu.get("result").toString();
                                        }
                                        a = a.substring(0, a.length() - 1);
                                    }
                                    ms.setFuzhu(a);
                                }
                                long total = list123.size();
                                totalAR.set(total);
                                return Mono.just(list123);
                            });
                }).map(a -> R.page(a, pageable, (totalAR.get())));
    }

    @PostMapping("/company/company_findByNoLastCode")
    public Mono<R> company_findByNoLastCode(@RequestBody Map map, Pageable pageable) {
        String ccode = map.get("ccode").toString();
        String value = map.get("value").toString();
        String cclass = map.get("cclass").toString();
        String iyear = map.get("iyear").toString();
        // 当前页
        int page = map.get("page") == null ? 1 : Integer.parseInt(map.get("page").toString());
        // 页多少条
        int size = map.get("size") == null ? 10000 : Integer.parseInt(map.get("size").toString());
        AtomicReference<Long> totalAR = new AtomicReference(0);
        return codeKemuRepository.company_findByLastCode("0", iyear).collectList()
                .flatMap(list -> {
                    return fuzhuHesuanRepository.findAll().collectList()
                            .flatMap(fuzhulist -> {
                                List<CodeKemu> list123 = list.stream().skip((page - 1) * size).limit(size).collect(Collectors.toList());
                                if (StrUtil.isNotBlank(cclass)) {
                                    list123 = list.stream().filter(v -> v.getCclass().contains(cclass)).collect(Collectors.toList());
                                } else if (StrUtil.isNotBlank(ccode) && ccode.equals("ccode")) {
                                    list123 = list.stream().filter(v -> v.getCcode().startsWith(value)).collect(Collectors.toList());
                                } else if (StrUtil.isNotBlank(ccode) && ccode.equals("ccodeName")) {
                                    list123 = list.stream().filter(v -> v.getCcodeName().contains(value)).collect(Collectors.toList());
                                }
                                for (CodeKemu ms : list123) {
                                    // 5个辅助项
                                    String fuzhu = "";
                                    if (StringUtils.isNotBlank(ms.getBperson()) && ms.getBperson().equals("1")) {
                                        fuzhu += "个人,";
                                    }
                                    if (StringUtils.isNotBlank(ms.getBdept()) && ms.getBdept().equals("1")) {
                                        fuzhu += "部门,";
                                    }
                                    if (StringUtils.isNotBlank(ms.getBcus()) && ms.getBcus().equals("1")) {
                                        fuzhu += "客户,";
                                    }
                                    if (StringUtils.isNotBlank(ms.getBsup()) && ms.getBsup().equals("1")) {
                                        fuzhu += "供应商,";
                                    }
                                    if (StringUtils.isNotBlank(ms.getBitem()) && ms.getBitem().equals("1")) {
                                        fuzhu += "项目,";
                                    }
                                    if (StringUtils.isNotBlank(ms.getYsYsly()) && ms.getYsYsly().equals("1")) {
                                        fuzhu += "预算来源,";
                                    }
                                    if (StringUtils.isNotBlank(ms.getYsZcgnfl()) && ms.getYsZcgnfl().equals("1")) {
                                        fuzhu += "支出功能分类,";
                                    }
                                    if (StringUtils.isNotBlank(ms.getYsZfzcjjfl()) && ms.getYsZfzcjjfl().equals("1")) {
                                        fuzhu += "政府支出经济分类,";
                                    }
                                    if (StringUtils.isNotBlank(ms.getYsBmzcjjfl()) && ms.getYsBmzcjjfl().equals("1")) {
                                        fuzhu += "部门支出经济分类,";
                                    }
                                    if (!"".equals(fuzhu)) {
                                        ms.setFuzhu(fuzhu.substring(0, fuzhu.length() - 1));
                                    }
                                    Map byZdyFuZhu = findByZdyFuZhu(fuzhulist, ms);
                                    String a = "";
                                    if (StrUtil.isNotBlank(fuzhu)) {
                                        a = fuzhu;
                                        if (StrUtil.isNotBlank(byZdyFuZhu.get("result").toString())) {
                                            a += "," + byZdyFuZhu.get("result").toString();
                                        }
                                        a = a.substring(0, a.length() - 1);
                                    }
                                    ms.setFuzhu(a);
                                }
                                long total = list123.size();
                                totalAR.set(total);
                                return Mono.just(list123);
                            });
                }).map(a -> R.page(a, pageable, (totalAR.get())));
    }

    @PostMapping("/company/company_findByAllCode")
    public Mono<R> company_findByAllCode(@RequestBody Map map, Pageable pageable) {
        String ccode = map.get("ccode").toString();
        String value = map.get("value").toString();
        String cclass = map.get("cclass").toString();
        String iyear = map.get("iyear").toString();
        // 当前页
        int page = map.get("page") == null ? 1 : Integer.parseInt(map.get("page").toString());
        // 页多少条
        int size = map.get("size") == null ? 10000 : Integer.parseInt(map.get("size").toString());
        AtomicReference<Long> totalAR = new AtomicReference(0);
        return codeKemuRepository.findByIyearOrderByCcode(iyear).collectList()
                .flatMap(list -> {
                    return fuzhuHesuanRepository.findAll().collectList()
                            .flatMap(fuzhulist -> {
                                List<CodeKemu> list123 = list.stream().skip((page - 1) * size).limit(size).collect(Collectors.toList());
                                if (StrUtil.isNotBlank(cclass)) {
                                    list123 = list.stream().filter(v -> v.getCclass().contains(cclass)).collect(Collectors.toList());
                                } else if (StrUtil.isNotBlank(ccode) && ccode.equals("ccode")) {
                                    list123 = list.stream().filter(v -> v.getCcode().startsWith(value)).collect(Collectors.toList());
                                } else if (StrUtil.isNotBlank(ccode) && ccode.equals("ccodeName")) {
                                    list123 = list.stream().filter(v -> v.getCcodeName().contains(value)).collect(Collectors.toList());
                                }
                                for (CodeKemu ms : list123) {
                                    // 5个辅助项
                                    String fuzhu = "";
                                    if (StringUtils.isNotBlank(ms.getBperson()) && ms.getBperson().equals("1")) {
                                        fuzhu += "个人,";
                                    }
                                    if (StringUtils.isNotBlank(ms.getBdept()) && ms.getBdept().equals("1")) {
                                        fuzhu += "部门,";
                                    }
                                    if (StringUtils.isNotBlank(ms.getBcus()) && ms.getBcus().equals("1")) {
                                        fuzhu += "客户,";
                                    }
                                    if (StringUtils.isNotBlank(ms.getBsup()) && ms.getBsup().equals("1")) {
                                        fuzhu += "供应商,";
                                    }
                                    if (StringUtils.isNotBlank(ms.getBitem()) && ms.getBitem().equals("1")) {
                                        fuzhu += "项目,";
                                    }
                                    if (StringUtils.isNotBlank(ms.getYsYsly()) && ms.getYsYsly().equals("1")) {
                                        fuzhu += "预算来源,";
                                    }
                                    if (StringUtils.isNotBlank(ms.getYsZcgnfl()) && ms.getYsZcgnfl().equals("1")) {
                                        fuzhu += "支出功能分类,";
                                    }
                                    if (StringUtils.isNotBlank(ms.getYsZfzcjjfl()) && ms.getYsZfzcjjfl().equals("1")) {
                                        fuzhu += "政府支出经济分类,";
                                    }
                                    if (StringUtils.isNotBlank(ms.getYsBmzcjjfl()) && ms.getYsBmzcjjfl().equals("1")) {
                                        fuzhu += "部门支出经济分类,";
                                    }
                                    if (!"".equals(fuzhu)) {
                                        ms.setFuzhu(fuzhu.substring(0, fuzhu.length() - 1));
                                    }
                                    Map byZdyFuZhu = findByZdyFuZhu(fuzhulist, ms);
                                    String a = "";
                                    if (StrUtil.isNotBlank(fuzhu)) {
                                        a = fuzhu;
                                        if (StrUtil.isNotBlank(byZdyFuZhu.get("result").toString())) {
                                            a += "," + byZdyFuZhu.get("result").toString();
                                        }
                                        a = a.substring(0, a.length() - 1);
                                    }
                                    ms.setFuzhu(a);
                                }
                                long total = list123.size();
                                totalAR.set(total);
                                return Mono.just(list123);
                            });
                }).map(a -> R.page(a, pageable, (totalAR.get())));
    }

    @PostMapping("/company/company_findByYearsAllCode")
    public Mono<R> company_findByYearsAllCode(@RequestBody Map map, Pageable pageable) {
        String ccode = map.get("ccode").toString();
        String value = map.get("value").toString();
        String cclass = map.get("cclass").toString();
        List<String> iyearList = JSON.parseArray(map.get("iyearList").toString(), String.class);
        // 当前页
        int page = map.get("page") == null ? 1 : Integer.parseInt(map.get("page").toString());
        // 页多少条
        int size = map.get("size") == null ? 10000 : Integer.parseInt(map.get("size").toString());
        AtomicReference<Long> totalAR = new AtomicReference(0);
        return codeKemuRepository.findByIyearInOrderByCcode(iyearList).collectList()
                .flatMap(list -> {
                    return fuzhuHesuanRepository.findAll().collectList()
                            .flatMap(fuzhulist -> {
                                List<CodeKemu> list123 = list.stream().skip((page - 1) * size).limit(size).collect(Collectors.toList());
                                if (StrUtil.isNotBlank(cclass)) {
                                    list123 = list.stream().filter(v -> v.getCclass().contains(cclass)).collect(Collectors.toList());
                                } else if (StrUtil.isNotBlank(ccode) && ccode.equals("ccode")) {
                                    list123 = list.stream().filter(v -> v.getCcode().startsWith(value)).collect(Collectors.toList());
                                } else if (StrUtil.isNotBlank(ccode) && ccode.equals("ccodeName")) {
                                    list123 = list.stream().filter(v -> v.getCcodeName().contains(value)).collect(Collectors.toList());
                                }
                                for (CodeKemu ms : list123) {
                                    // 5个辅助项
                                    String fuzhu = "";
                                    if (StringUtils.isNotBlank(ms.getBperson()) && ms.getBperson().equals("1")) {
                                        fuzhu += "个人,";
                                    }
                                    if (StringUtils.isNotBlank(ms.getBdept()) && ms.getBdept().equals("1")) {
                                        fuzhu += "部门,";
                                    }
                                    if (StringUtils.isNotBlank(ms.getBcus()) && ms.getBcus().equals("1")) {
                                        fuzhu += "客户,";
                                    }
                                    if (StringUtils.isNotBlank(ms.getBsup()) && ms.getBsup().equals("1")) {
                                        fuzhu += "供应商,";
                                    }
                                    if (StringUtils.isNotBlank(ms.getBitem()) && ms.getBitem().equals("1")) {
                                        fuzhu += "项目,";
                                    }
                                    if (StringUtils.isNotBlank(ms.getYsYsly()) && ms.getYsYsly().equals("1")) {
                                        fuzhu += "预算来源,";
                                    }
                                    if (StringUtils.isNotBlank(ms.getYsZcgnfl()) && ms.getYsZcgnfl().equals("1")) {
                                        fuzhu += "支出功能分类,";
                                    }
                                    if (StringUtils.isNotBlank(ms.getYsZfzcjjfl()) && ms.getYsZfzcjjfl().equals("1")) {
                                        fuzhu += "政府支出经济分类,";
                                    }
                                    if (StringUtils.isNotBlank(ms.getYsBmzcjjfl()) && ms.getYsBmzcjjfl().equals("1")) {
                                        fuzhu += "部门支出经济分类,";
                                    }
                                    if (!"".equals(fuzhu)) {
                                        ms.setFuzhu(fuzhu.substring(0, fuzhu.length() - 1));
                                    }
                                    Map byZdyFuZhu = findByZdyFuZhu(fuzhulist, ms);
                                    String a = "";
                                    if (StrUtil.isNotBlank(fuzhu)) {
                                        a = fuzhu;
                                        if (StrUtil.isNotBlank(byZdyFuZhu.get("result").toString())) {
                                            a += "," + byZdyFuZhu.get("result").toString();
                                        }
                                        a = a.substring(0, a.length() - 1);
                                    }
                                    ms.setFuzhu(a);
                                }
                                long total = list123.size();
                                totalAR.set(total);
                                return Mono.just(list123);
                            });
                }).map(a -> R.page(a, pageable, (totalAR.get())));
    }

    @PostMapping("/company/company_findByIyearOrderByCcodeTree")
    public Mono<R> company_findByIyearOrderByCcodeTree(@RequestBody Map map) {
        String style = map.get("style").toString();
        String iyear = map.get("iyear").toString();
        List<String> list = Arrays.asList(style.split(","));
        return codeKemuRepository.findByIyearOrderByCcode(iyear).collectList()
                .flatMap(codelist -> {
                    List<Map> listmap = new ArrayList<>();
                    list.forEach(sty -> {
                        Map map1 = new HashMap();
                        List<CodeKemu> collect = codelist.stream().filter(v -> v.getCclass().equals(sty)).collect(Collectors.toList());
                        map1.put("title", sty);
                        map1.put("value", sty);
                        map1.put("key", sty);
                        map1.put("bend", "类型");
                        map1.put("children", collect.size() > 0 ? getChild123("0", collect) : "");
                        listmap.add(map1);
                    });
                    return Mono.just(listmap);
                }).map(a -> R.ok().setResult(a));
    }

    @PostMapping("/company/company_findByNotLastCodeTree")
    public Mono<R> company_findByNotLastCodeTree(@RequestBody Map map) {
        String style = map.get("style").toString();
        String iyear = map.get("iyear").toString();
        List<String> list = Arrays.asList(style.split(","));
        return codeKemuRepository.company_findByLastCode("0", iyear).collectList()
                .flatMap(codelist -> {
                    List<Map> listmap = new ArrayList<>();
                    list.forEach(sty -> {
                        Map map1 = new HashMap();
                        List<CodeKemu> collect = codelist.stream().filter(v -> v.getCclass().equals(sty)).collect(Collectors.toList());
                        map1.put("title", sty);
                        map1.put("value", sty);
                        map1.put("key", sty);
                        map1.put("children", collect.size() > 0 ? getChild123("0", collect) : "");
                        listmap.add(map1);
                    });
                    return Mono.just(listmap);
                }).map(a -> R.ok().setResult(a));
    }

    @PostMapping("/company/company_findByAllCodeTree")
    public Mono<R> company_findByAllCodeTree(@RequestBody Map map) {
        String style = map.get("style").toString();
        String iyear = map.get("iyear").toString();
        List<String> list = Arrays.asList(style.split(","));
        return codeKemuRepository.findByIyearOrderByCcode(iyear).collectList()
                .flatMap(codelist -> {
                    List<Map> listmap = new ArrayList<>();
                    list.forEach(sty -> {
                        Map map1 = new HashMap();
                        List<CodeKemu> collect = codelist.stream().filter(v -> v.getCclass().equals(sty)).collect(Collectors.toList());
                        map1.put("title", sty);
                        map1.put("value", sty);
                        map1.put("key", sty);
                        map1.put("children", collect.size() > 0 ? getChild123("0", collect) : "");
                        listmap.add(map1);
                    });
                    return Mono.just(listmap);
                }).map(a -> R.ok().setResult(a));
    }

    @PostMapping("/company/company_findByAllCodeTree2")
    public Mono<R> company_findByAllCodeTree2(@RequestBody Map map) {
        String style = map.get("style").toString();
        List<String> iyearList = JSON.parseArray(map.get("iyearList").toString(), String.class);
        List<String> list = Arrays.asList(style.split(","));
        return codeKemuRepository.findByIyearInOrderByCcode(iyearList).collectList()
                .flatMap(codelist -> {
                    List<Map> listmap = new ArrayList<>();
                    list.forEach(sty -> {
                        Map map1 = new HashMap();
                        List<CodeKemu> collect = codelist.stream().filter(v -> v.getCclass().equals(sty)).collect(Collectors.toList());
                        map1.put("title", sty);
                        map1.put("value", sty);
                        map1.put("key", sty);
                        map1.put("children", collect.size() > 0 ? getChild123("0", collect) : "");
                        listmap.add(map1);
                    });
                    return Mono.just(listmap);
                }).map(a -> R.ok().setResult(a));
    }

    @PostMapping("/company/company_findByIyearAndCcode")
    public Mono<R> company_findByIyearAndCcode(String iyear, String ccode) {
        return codeKemuRepository.company_findByIyearAndCcode(iyear, ccode).map(o -> R.ok().setResult(o)).defaultIfEmpty(R.ok().setResult(""));
    }


    @PostMapping("/company/company_findByUpYearCodeAndThisYearCode")
    public Mono<R> company_findByUpYearCodeAndThisYearCode(String iyear, String styleStr) {
        List<String> style = Arrays.asList(styleStr.split(","));
        List<AccStyle> styleList = new ArrayList<>();
        for (int i = 0; i < style.size(); i++) {
            AccStyle sty = new AccStyle();
            sty.setCclass(style.get(i));
            styleList.add(sty);
        }

        String upyear = String.valueOf(Integer.valueOf(iyear) - 1);
        String thisyear = iyear;
        return codeKemuRepository.findAll().collectList()
                .flatMap(codelist -> {
                    List<CarryDownCodeKemuVo> carryDownList = new ArrayList<>();
                    List<CodeKemu> upyearCodeList = codelist.stream().filter(up -> StrUtil.isNotBlank(up.getIyear()) && up.getIyear().equals(upyear) && up.getBend().equals("1")).collect(Collectors.toList());
                    List<CodeKemu> thisyearCodeList = codelist.stream().filter(th -> StrUtil.isNotBlank(th.getIyear()) && th.getIyear().equals(thisyear) && th.getBend().equals("1")).collect(Collectors.toList());

                    upyearCodeList.stream().forEach(up -> {
                        CarryDownCodeKemuVo v = new CarryDownCodeKemuVo();
                        // 对比本年与上年科目是否一直
                        List<CodeKemu> collect = thisyearCodeList.stream().filter(th -> StrUtil.isNotBlank(th.getCcode()) && th.getCcode().equals(up.getCcode())).collect(Collectors.toList());
                        v.setUpCcodeBprogerty(up.getBprogerty()).setThisCcode(collect.size() == 0 ? "" : collect.get(0).getCcode()).setThisCcodeName(collect.size() == 0 ? "" : collect.get(0).getCcodeName())
                                .setUpCcode(up.getCcode()).setUpCcodeName(up.getCcodeName()).setUpMd(new BigDecimal(0)).setUpMc(new BigDecimal(0));
                        carryDownList.add(v);
                    });
                    // 获取期初
                    return accvoucherRepository.findAllByCodeQc(upyear).collectList()
                            .flatMap(qcCodeList -> {
                                // 获取1-11月凭证
                                return accvoucherRepository.findByBeforeMonthCodeMdMc(upyear + "01", upyear + "11").collectList()
                                        .flatMap(pz1And11MonthList -> {
                                            // 获取12月凭证
                                            return accvoucherRepository.findByBeforeMonthCodeMdMc(upyear + "12", upyear + "12").collectList()
                                                    .flatMap(pz12MonthList -> {
                                                        // 辅助末级科目期初
                                                        return accvoucherRepository.findAllByFuzhuCodeQc(upyear).collectList()
                                                                .flatMap(qcFuZhuBend1CodeMdMcList -> {
                                                                    // 获取带辅助项的末级科目
                                                                    return codeKemuRepository.findAllByBendFuZhuCode(upyear).collectList()
                                                                            .flatMap(fuzhuBend1CodeList -> {
                                                                                Map map = new HashMap();
                                                                                map.put("qcCodeList", qcCodeList);
                                                                                map.put("carryDownList", carryDownList);
                                                                                map.put("pz1And11MonthList", pz1And11MonthList);
                                                                                map.put("pz12MonthList", pz12MonthList);
                                                                                map.put("upyearCodeList", upyearCodeList);
                                                                                map.put("qcFuZhuBend1CodeMdMcList", qcFuZhuBend1CodeMdMcList);
                                                                                map.put("fuzhuBend1CodeList", fuzhuBend1CodeList);
                                                                                return Mono.just(map);
                                                                            });
                                                                });
                                                    });
                                        });
                            });
                })
                .flatMap(map -> {
                    // 上年all科目
                    List<CodeKemu> codelist = (List<CodeKemu>) map.get("upyearCodeList");
                    // 科目期出余额
                    List<AccvoucherVo> qcCodeList = (List<AccvoucherVo>) map.get("qcCodeList");
                    // 对比科目
                    List<CarryDownCodeKemuVo> carryDownList = (List<CarryDownCodeKemuVo>) map.get("carryDownList");
                    // 1-11月凭证
                    List<AccvoucherVo> pz1And11MonthList = (List<AccvoucherVo>) map.get("pz1And11MonthList");
                    // 12月凭证
                    List<AccvoucherVo> pz12MonthList = (List<AccvoucherVo>) map.get("pz12MonthList");
                    // 辅助末级科目期初
                    List<AccvoucherVo> qcFuZhuBend1CodeMdMcList = (List<AccvoucherVo>) map.get("qcFuZhuBend1CodeMdMcList");
                    // 获取带辅助项的末级科目
                    List<CodeKemu> fuzhuBend1CodeList = (List<CodeKemu>) map.get("fuzhuBend1CodeList");

                    carryDownList.forEach(cd -> {
                        cd.setKey(RandomNum.uuid());
                        List<AccvoucherVo> qclist = qcCodeList.stream().filter(qc -> qc.getCcode().equals(cd.getUpCcode())).collect(Collectors.toList());
                        // 期初
                        BigDecimal qcMd = qclist.size() > 0 ? qclist.get(0).getMd() : new BigDecimal(0);
                        BigDecimal qcMc = qclist.size() > 0 ? qclist.get(0).getMc() : new BigDecimal(0);
                        // 1-11月
                        List<AccvoucherVo> pzlist = pz1And11MonthList.stream().filter(pz -> pz.getCcode().equals(cd.getUpCcode())).collect(Collectors.toList());
                        BigDecimal pzMd = pzlist.size() > 0 ? pzlist.stream().map(AccvoucherVo::getMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum) : new BigDecimal(0);
                        BigDecimal pzMc = pzlist.size() > 0 ? pzlist.stream().map(AccvoucherVo::getMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum) : new BigDecimal(0);
                        // 12月
                        List<AccvoucherVo> pzlist2 = pz12MonthList.stream().filter(pz -> pz.getCcode().equals(cd.getUpCcode())).collect(Collectors.toList());
                        BigDecimal pzMd2 = pzlist2.size() > 0 ? pzlist2.stream().map(AccvoucherVo::getMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum) : new BigDecimal(0);
                        BigDecimal pzMc2 = pzlist2.size() > 0 ? pzlist2.stream().map(AccvoucherVo::getMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum) : new BigDecimal(0);

                        if (cd.getUpCcodeBprogerty().equals("1")) {
                            BigDecimal yue = qcMd.add(pzMd).subtract(qcMc.add(pzMc));
                            BigDecimal yue2 = yue.add(pzMd2).subtract(pzMc2);
                            if (yue2.compareTo(BigDecimal.ZERO) > 0) {
                                cd.setUpMd(yue2);
                            } else {
                                cd.setUpMc(yue2.multiply(new BigDecimal(-1)));
                            }
                        } else {
                            BigDecimal yue = qcMc.add(pzMc).subtract(qcMd.add(pzMd));
                            BigDecimal yue2 = yue.add(pzMc2).subtract(pzMd2);
                            if (yue2.compareTo(BigDecimal.ZERO) > 0) {
                                cd.setUpMc(yue2);
                            } else {
                                cd.setUpMd(yue2.multiply(new BigDecimal(-1)));
                            }
                        }

                        cd.setFuzhuQclist(new ArrayList<>());
                        // 科目是否带辅助
                        List<AccvoucherVo> collect = qcFuZhuBend1CodeMdMcList.stream().filter(txt -> txt.getCcode().equals(cd.getUpCcode())).collect(Collectors.toList());
                        if (collect.size() > 0) {
                            // 辅助明细
                            List<AccvoucherVo3> mxlist = new ArrayList<>();
                            qcFuZhuBend1CodeMdMcList.forEach(f -> {
                                AccvoucherVo3 mx = new AccvoucherVo3();
                                mx.setCcode(f.getCcode()).setCcodeName(f.getCcodeName());
                                // 1-11月
                                List<AccvoucherVo> list1 = new ArrayList<>();
                                // 12月
                                List<AccvoucherVo> list2 = new ArrayList<>();
                                if (StrUtil.isNotBlank(f.getCdeptId())) {
                                    mx.setCdeptId(f.getCdeptId());
                                    list1 = pz1And11MonthList.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdeptId()) && a.getCdeptId().equals(f.getCdeptId())).collect(Collectors.toList());
                                }
                                if (StrUtil.isNotBlank(f.getCpersonId())) {
                                    mx.setCpersonId(f.getCpersonId());
                                    if (list1 == null) {
                                        list1 = pz1And11MonthList.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCpersonId()) && a.getCpersonId().equals(f.getCpersonId())).collect(Collectors.toList());
                                    } else {
                                        list1 = list1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCpersonId()) && a.getCpersonId().equals(f.getCpersonId())).collect(Collectors.toList());
                                    }
                                    if (list2 == null) {
                                        list2 = pz12MonthList.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCpersonId()) && a.getCpersonId().equals(f.getCpersonId())).collect(Collectors.toList());
                                    } else {
                                        list2 = list2.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCpersonId()) && a.getCpersonId().equals(f.getCpersonId())).collect(Collectors.toList());
                                    }
                                }
                                if (StrUtil.isNotBlank(f.getCcusId())) {
                                    mx.setCcusId(f.getCcusId());
                                    if (list1 == null) {
                                        list1 = pz1And11MonthList.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCcusId()) && a.getCcusId().equals(f.getCcusId())).collect(Collectors.toList());
                                    } else {
                                        list1 = list1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCcusId()) && a.getCcusId().equals(f.getCcusId())).collect(Collectors.toList());
                                    }
                                    if (list2 == null) {
                                        list2 = pz12MonthList.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCcusId()) && a.getCcusId().equals(f.getCcusId())).collect(Collectors.toList());
                                    } else {
                                        list2 = list2.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCcusId()) && a.getCcusId().equals(f.getCcusId())).collect(Collectors.toList());
                                    }
                                }
                                if (StrUtil.isNotBlank(f.getCsupId())) {
                                    mx.setCsupId(f.getCsupId());
                                    if (list1 == null) {
                                        list1 = pz1And11MonthList.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCsupId()) && a.getCsupId().equals(f.getCsupId())).collect(Collectors.toList());
                                    } else {
                                        list1 = list1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCsupId()) && a.getCsupId().equals(f.getCsupId())).collect(Collectors.toList());
                                    }
                                    if (list2 == null) {
                                        list2 = pz12MonthList.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCsupId()) && a.getCsupId().equals(f.getCsupId())).collect(Collectors.toList());
                                    } else {
                                        list2 = list2.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCsupId()) && a.getCsupId().equals(f.getCsupId())).collect(Collectors.toList());
                                    }
                                }
                                if (StrUtil.isNotBlank(f.getProjectId())) {
                                    mx.setProjectId(f.getProjectId());
                                    if (list1 == null) {
                                        list1 = pz1And11MonthList.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getProjectId()) && a.getProjectId().equals(f.getProjectId())).collect(Collectors.toList());
                                    } else {
                                        list1 = list1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getProjectId()) && a.getProjectId().equals(f.getProjectId())).collect(Collectors.toList());
                                    }
                                    if (list2 == null) {
                                        list2 = pz12MonthList.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getProjectId()) && a.getProjectId().equals(f.getProjectId())).collect(Collectors.toList());
                                    } else {
                                        list2 = list2.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getProjectId()) && a.getProjectId().equals(f.getProjectId())).collect(Collectors.toList());
                                    }
                                }
                                if (StrUtil.isNotBlank(f.getCdfine1())) {
                                    mx.setCdfine1(f.getCdfine1());
                                    if (list1 == null) {
                                        list1 = pz1And11MonthList.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine1()) && a.getCdfine1().equals(f.getCdfine1())).collect(Collectors.toList());
                                    } else {
                                        list1 = list1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine1()) && a.getCdfine1().equals(f.getCdfine1())).collect(Collectors.toList());
                                    }
                                    if (list2 == null) {
                                        list2 = pz12MonthList.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine1()) && a.getCdfine1().equals(f.getCdfine1())).collect(Collectors.toList());
                                    } else {
                                        list2 = list2.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine1()) && a.getCdfine1().equals(f.getCdfine1())).collect(Collectors.toList());
                                    }
                                }
                                if (StrUtil.isNotBlank(f.getCdfine2())) {
                                    mx.setCdfine2(f.getCdfine2());
                                    if (list1 == null) {
                                        list1 = pz1And11MonthList.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine2()) && a.getCdfine2().equals(f.getCdfine2())).collect(Collectors.toList());
                                    } else {
                                        list1 = list1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine2()) && a.getCdfine2().equals(f.getCdfine2())).collect(Collectors.toList());
                                    }
                                    if (list2 == null) {
                                        list2 = pz12MonthList.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine2()) && a.getCdfine2().equals(f.getCdfine2())).collect(Collectors.toList());
                                    } else {
                                        list2 = list2.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine2()) && a.getCdfine2().equals(f.getCdfine2())).collect(Collectors.toList());
                                    }
                                }
                                if (StrUtil.isNotBlank(f.getCdfine3())) {
                                    mx.setCdfine3(f.getCdfine3());
                                    if (list1 == null) {
                                        list1 = pz1And11MonthList.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine3()) && a.getCdfine3().equals(f.getCdfine3())).collect(Collectors.toList());
                                    } else {
                                        list1 = list1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine3()) && a.getCdfine3().equals(f.getCdfine3())).collect(Collectors.toList());
                                    }
                                    if (list2 == null) {
                                        list2 = pz12MonthList.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine3()) && a.getCdfine3().equals(f.getCdfine3())).collect(Collectors.toList());
                                    } else {
                                        list2 = list2.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine3()) && a.getCdfine3().equals(f.getCdfine3())).collect(Collectors.toList());
                                    }
                                }
                                if (StrUtil.isNotBlank(f.getCdfine4())) {
                                    mx.setCdfine4(f.getCdfine4());
                                    if (list1 == null) {
                                        list1 = pz1And11MonthList.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine4()) && a.getCdfine4().equals(f.getCdfine4())).collect(Collectors.toList());
                                    } else {
                                        list1 = list1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine4()) && a.getCdfine4().equals(f.getCdfine4())).collect(Collectors.toList());
                                    }
                                    if (list2 == null) {
                                        list2 = pz12MonthList.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine4()) && a.getCdfine4().equals(f.getCdfine4())).collect(Collectors.toList());
                                    } else {
                                        list2 = list2.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine4()) && a.getCdfine4().equals(f.getCdfine4())).collect(Collectors.toList());
                                    }
                                }
                                if (StrUtil.isNotBlank(f.getCdfine5())) {
                                    mx.setCdfine5(f.getCdfine5());
                                    if (list1 == null) {
                                        list1 = pz1And11MonthList.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine5()) && a.getCdfine5().equals(f.getCdfine5())).collect(Collectors.toList());
                                    } else {
                                        list1 = list1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine5()) && a.getCdfine5().equals(f.getCdfine5())).collect(Collectors.toList());
                                    }
                                    if (list2 == null) {
                                        list2 = pz12MonthList.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine5()) && a.getCdfine5().equals(f.getCdfine5())).collect(Collectors.toList());
                                    } else {
                                        list2 = list2.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine5()) && a.getCdfine5().equals(f.getCdfine5())).collect(Collectors.toList());
                                    }
                                }
                                if (StrUtil.isNotBlank(f.getCdfine6())) {
                                    mx.setCdfine6(f.getCdfine6());
                                    if (list1 == null) {
                                        list1 = pz1And11MonthList.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine6()) && a.getCdfine6().equals(f.getCdfine6())).collect(Collectors.toList());
                                    } else {
                                        list1 = list1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine6()) && a.getCdfine6().equals(f.getCdfine6())).collect(Collectors.toList());
                                    }
                                    if (list2 == null) {
                                        list2 = pz12MonthList.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine6()) && a.getCdfine6().equals(f.getCdfine6())).collect(Collectors.toList());
                                    } else {
                                        list2 = list2.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine6()) && a.getCdfine6().equals(f.getCdfine6())).collect(Collectors.toList());
                                    }
                                }
                                if (StrUtil.isNotBlank(f.getCdfine7())) {
                                    mx.setCdfine7(f.getCdfine7());
                                    if (list1 == null) {
                                        list1 = pz1And11MonthList.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine7()) && a.getCdfine7().equals(f.getCdfine7())).collect(Collectors.toList());
                                    } else {
                                        list1 = list1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine7()) && a.getCdfine7().equals(f.getCdfine7())).collect(Collectors.toList());
                                    }
                                    if (list2 == null) {
                                        list2 = pz12MonthList.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine7()) && a.getCdfine7().equals(f.getCdfine7())).collect(Collectors.toList());
                                    } else {
                                        list2 = list2.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine7()) && a.getCdfine7().equals(f.getCdfine7())).collect(Collectors.toList());
                                    }
                                }
                                if (StrUtil.isNotBlank(f.getCdfine8())) {
                                    mx.setCdfine8(f.getCdfine8());
                                    if (list1 == null) {
                                        list1 = pz1And11MonthList.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine8()) && a.getCdfine8().equals(f.getCdfine8())).collect(Collectors.toList());
                                    } else {
                                        list1 = list1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine8()) && a.getCdfine8().equals(f.getCdfine8())).collect(Collectors.toList());
                                    }
                                    if (list2 == null) {
                                        list2 = pz12MonthList.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine8()) && a.getCdfine8().equals(f.getCdfine8())).collect(Collectors.toList());
                                    } else {
                                        list2 = list2.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine8()) && a.getCdfine8().equals(f.getCdfine8())).collect(Collectors.toList());
                                    }
                                }
                                if (StrUtil.isNotBlank(f.getCdfine9())) {
                                    mx.setCdfine9(f.getCdfine9());
                                    if (list1 == null) {
                                        list1 = pz1And11MonthList.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine9()) && a.getCdfine9().equals(f.getCdfine9())).collect(Collectors.toList());
                                    } else {
                                        list1 = list1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine9()) && a.getCdfine9().equals(f.getCdfine9())).collect(Collectors.toList());
                                    }
                                    if (list2 == null) {
                                        list2 = pz12MonthList.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine9()) && a.getCdfine9().equals(f.getCdfine9())).collect(Collectors.toList());
                                    } else {
                                        list2 = list2.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine9()) && a.getCdfine9().equals(f.getCdfine9())).collect(Collectors.toList());
                                    }
                                }
                                if (StrUtil.isNotBlank(f.getCdfine10())) {
                                    mx.setCdfine10(f.getCdfine10());
                                    if (list1 == null) {
                                        list1 = pz1And11MonthList.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine10()) && a.getCdfine10().equals(f.getCdfine10())).collect(Collectors.toList());
                                    } else {
                                        list1 = list1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine10()) && a.getCdfine10().equals(f.getCdfine10())).collect(Collectors.toList());
                                    }
                                    if (list2 == null) {
                                        list2 = pz12MonthList.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine10()) && a.getCdfine10().equals(f.getCdfine10())).collect(Collectors.toList());
                                    } else {
                                        list2 = list2.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine10()) && a.getCdfine10().equals(f.getCdfine10())).collect(Collectors.toList());
                                    }
                                }
                                if (StrUtil.isNotBlank(f.getCdfine11())) {
                                    mx.setCdfine11(f.getCdfine11());
                                    if (list1 == null) {
                                        list1 = pz1And11MonthList.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine11()) && a.getCdfine11().equals(f.getCdfine11())).collect(Collectors.toList());
                                    } else {
                                        list1 = list1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine11()) && a.getCdfine11().equals(f.getCdfine11())).collect(Collectors.toList());
                                    }
                                    if (list2 == null) {
                                        list2 = pz12MonthList.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine11()) && a.getCdfine11().equals(f.getCdfine11())).collect(Collectors.toList());
                                    } else {
                                        list2 = list2.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine11()) && a.getCdfine11().equals(f.getCdfine11())).collect(Collectors.toList());
                                    }
                                }
                                if (StrUtil.isNotBlank(f.getCdfine12())) {
                                    mx.setCdfine12(f.getCdfine12());
                                    if (list1 == null) {
                                        list1 = pz1And11MonthList.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine12()) && a.getCdfine12().equals(f.getCdfine12())).collect(Collectors.toList());
                                    } else {
                                        list1 = list1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine12()) && a.getCdfine12().equals(f.getCdfine12())).collect(Collectors.toList());
                                    }
                                    if (list2 == null) {
                                        list2 = pz12MonthList.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine12()) && a.getCdfine12().equals(f.getCdfine12())).collect(Collectors.toList());
                                    } else {
                                        list2 = list2.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine12()) && a.getCdfine12().equals(f.getCdfine12())).collect(Collectors.toList());
                                    }
                                }
                                if (StrUtil.isNotBlank(f.getCdfine13())) {
                                    mx.setCdfine13(f.getCdfine13());
                                    if (list1 == null) {
                                        list1 = pz1And11MonthList.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine13()) && a.getCdfine13().equals(f.getCdfine13())).collect(Collectors.toList());
                                    } else {
                                        list1 = list1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine13()) && a.getCdfine13().equals(f.getCdfine13())).collect(Collectors.toList());
                                    }
                                    if (list2 == null) {
                                        list2 = pz12MonthList.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine13()) && a.getCdfine13().equals(f.getCdfine13())).collect(Collectors.toList());
                                    } else {
                                        list2 = list2.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine13()) && a.getCdfine13().equals(f.getCdfine13())).collect(Collectors.toList());
                                    }
                                }
                                if (StrUtil.isNotBlank(f.getCdfine14())) {
                                    mx.setCdfine14(f.getCdfine14());
                                    if (list1 == null) {
                                        list1 = pz1And11MonthList.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine14()) && a.getCdfine14().equals(f.getCdfine14())).collect(Collectors.toList());
                                    } else {
                                        list1 = list1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine14()) && a.getCdfine14().equals(f.getCdfine14())).collect(Collectors.toList());
                                    }
                                    if (list2 == null) {
                                        list2 = pz12MonthList.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine14()) && a.getCdfine14().equals(f.getCdfine14())).collect(Collectors.toList());
                                    } else {
                                        list2 = list2.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine14()) && a.getCdfine14().equals(f.getCdfine14())).collect(Collectors.toList());
                                    }
                                }
                                if (StrUtil.isNotBlank(f.getCdfine15())) {
                                    mx.setCdfine15(f.getCdfine15());
                                    if (list1 == null) {
                                        list1 = pz1And11MonthList.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine15()) && a.getCdfine15().equals(f.getCdfine15())).collect(Collectors.toList());
                                    } else {
                                        list1 = list1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine15()) && a.getCdfine15().equals(f.getCdfine15())).collect(Collectors.toList());
                                    }
                                    if (list2 == null) {
                                        list2 = pz12MonthList.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine15()) && a.getCdfine15().equals(f.getCdfine15())).collect(Collectors.toList());
                                    } else {
                                        list2 = list2.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine15()) && a.getCdfine15().equals(f.getCdfine15())).collect(Collectors.toList());
                                    }
                                }
                                if (StrUtil.isNotBlank(f.getCdfine16())) {
                                    mx.setCdfine16(f.getCdfine16());
                                    if (list1 == null) {
                                        list1 = pz1And11MonthList.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine16()) && a.getCdfine16().equals(f.getCdfine16())).collect(Collectors.toList());
                                    } else {
                                        list1 = list1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine16()) && a.getCdfine16().equals(f.getCdfine16())).collect(Collectors.toList());
                                    }
                                    if (list2 == null) {
                                        list2 = pz12MonthList.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine16()) && a.getCdfine16().equals(f.getCdfine16())).collect(Collectors.toList());
                                    } else {
                                        list2 = list2.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine16()) && a.getCdfine16().equals(f.getCdfine16())).collect(Collectors.toList());
                                    }
                                }
                                if (StrUtil.isNotBlank(f.getCdfine17())) {
                                    mx.setCdfine17(f.getCdfine17());
                                    if (list1 == null) {
                                        list1 = pz1And11MonthList.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine17()) && a.getCdfine17().equals(f.getCdfine17())).collect(Collectors.toList());
                                    } else {
                                        list1 = list1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine17()) && a.getCdfine17().equals(f.getCdfine17())).collect(Collectors.toList());
                                    }
                                    if (list2 == null) {
                                        list2 = pz12MonthList.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine17()) && a.getCdfine17().equals(f.getCdfine17())).collect(Collectors.toList());
                                    } else {
                                        list2 = list2.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine17()) && a.getCdfine17().equals(f.getCdfine17())).collect(Collectors.toList());
                                    }
                                }
                                if (StrUtil.isNotBlank(f.getCdfine18())) {
                                    mx.setCdfine18(f.getCdfine18());
                                    if (list1 == null) {
                                        list1 = pz1And11MonthList.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine18()) && a.getCdfine18().equals(f.getCdfine18())).collect(Collectors.toList());
                                    } else {
                                        list1 = list1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine18()) && a.getCdfine18().equals(f.getCdfine18())).collect(Collectors.toList());
                                    }
                                    if (list2 == null) {
                                        list2 = pz12MonthList.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine18()) && a.getCdfine18().equals(f.getCdfine18())).collect(Collectors.toList());
                                    } else {
                                        list2 = list2.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine18()) && a.getCdfine18().equals(f.getCdfine18())).collect(Collectors.toList());
                                    }
                                }
                                if (StrUtil.isNotBlank(f.getCdfine19())) {
                                    mx.setCdfine19(f.getCdfine19());
                                    if (list1 == null) {
                                        list1 = pz1And11MonthList.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine19()) && a.getCdfine19().equals(f.getCdfine19())).collect(Collectors.toList());
                                    } else {
                                        list1 = list1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine19()) && a.getCdfine19().equals(f.getCdfine19())).collect(Collectors.toList());
                                    }
                                    if (list2 == null) {
                                        list2 = pz12MonthList.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine19()) && a.getCdfine19().equals(f.getCdfine19())).collect(Collectors.toList());
                                    } else {
                                        list2 = list2.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine19()) && a.getCdfine19().equals(f.getCdfine19())).collect(Collectors.toList());
                                    }
                                }
                                if (StrUtil.isNotBlank(f.getCdfine20())) {
                                    mx.setCdfine20(f.getCdfine20());
                                    if (list1 == null) {
                                        list1 = pz1And11MonthList.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine20()) && a.getCdfine20().equals(f.getCdfine20())).collect(Collectors.toList());
                                    } else {
                                        list1 = list1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine20()) && a.getCdfine20().equals(f.getCdfine20())).collect(Collectors.toList());
                                    }
                                    if (list2 == null) {
                                        list2 = pz12MonthList.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine20()) && a.getCdfine20().equals(f.getCdfine20())).collect(Collectors.toList());
                                    } else {
                                        list2 = list2.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine20()) && a.getCdfine20().equals(f.getCdfine20())).collect(Collectors.toList());
                                    }
                                }
                                if (StrUtil.isNotBlank(f.getCdfine21())) {
                                    mx.setCdfine21(f.getCdfine21());
                                    if (list1 == null) {
                                        list1 = pz1And11MonthList.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine21()) && a.getCdfine21().equals(f.getCdfine21())).collect(Collectors.toList());
                                    } else {
                                        list1 = list1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine21()) && a.getCdfine21().equals(f.getCdfine21())).collect(Collectors.toList());
                                    }
                                    if (list2 == null) {
                                        list2 = pz12MonthList.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine21()) && a.getCdfine21().equals(f.getCdfine21())).collect(Collectors.toList());
                                    } else {
                                        list2 = list2.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine21()) && a.getCdfine21().equals(f.getCdfine21())).collect(Collectors.toList());
                                    }
                                }
                                if (StrUtil.isNotBlank(f.getCdfine22())) {
                                    mx.setCdfine22(f.getCdfine22());
                                    if (list1 == null) {
                                        list1 = pz1And11MonthList.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine22()) && a.getCdfine22().equals(f.getCdfine22())).collect(Collectors.toList());
                                    } else {
                                        list1 = list1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine22()) && a.getCdfine22().equals(f.getCdfine22())).collect(Collectors.toList());
                                    }
                                    if (list2 == null) {
                                        list2 = pz12MonthList.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine22()) && a.getCdfine22().equals(f.getCdfine22())).collect(Collectors.toList());
                                    } else {
                                        list2 = list2.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine22()) && a.getCdfine22().equals(f.getCdfine22())).collect(Collectors.toList());
                                    }
                                }
                                if (StrUtil.isNotBlank(f.getCdfine23())) {
                                    mx.setCdfine23(f.getCdfine23());
                                    if (list1 == null) {
                                        list1 = pz1And11MonthList.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine23()) && a.getCdfine23().equals(f.getCdfine23())).collect(Collectors.toList());
                                    } else {
                                        list1 = list1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine23()) && a.getCdfine23().equals(f.getCdfine23())).collect(Collectors.toList());
                                    }
                                    if (list2 == null) {
                                        list2 = pz12MonthList.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine23()) && a.getCdfine23().equals(f.getCdfine23())).collect(Collectors.toList());
                                    } else {
                                        list2 = list2.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine23()) && a.getCdfine23().equals(f.getCdfine23())).collect(Collectors.toList());
                                    }
                                }
                                if (StrUtil.isNotBlank(f.getCdfine24())) {
                                    mx.setCdfine24(f.getCdfine24());
                                    if (list1 == null) {
                                        list1 = pz1And11MonthList.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine24()) && a.getCdfine24().equals(f.getCdfine24())).collect(Collectors.toList());
                                    } else {
                                        list1 = list1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine24()) && a.getCdfine24().equals(f.getCdfine24())).collect(Collectors.toList());
                                    }
                                    if (list2 == null) {
                                        list2 = pz12MonthList.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine24()) && a.getCdfine24().equals(f.getCdfine24())).collect(Collectors.toList());
                                    } else {
                                        list2 = list2.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine24()) && a.getCdfine24().equals(f.getCdfine24())).collect(Collectors.toList());
                                    }
                                }
                                if (StrUtil.isNotBlank(f.getCdfine25())) {
                                    mx.setCdfine25(f.getCdfine25());
                                    if (list1 == null) {
                                        list1 = pz1And11MonthList.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine25()) && a.getCdfine25().equals(f.getCdfine25())).collect(Collectors.toList());
                                    } else {
                                        list1 = list1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine25()) && a.getCdfine25().equals(f.getCdfine25())).collect(Collectors.toList());
                                    }
                                    if (list2 == null) {
                                        list2 = pz12MonthList.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine25()) && a.getCdfine25().equals(f.getCdfine25())).collect(Collectors.toList());
                                    } else {
                                        list2 = list2.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine25()) && a.getCdfine25().equals(f.getCdfine25())).collect(Collectors.toList());
                                    }
                                }
                                if (StrUtil.isNotBlank(f.getCdfine26())) {
                                    mx.setCdfine26(f.getCdfine26());
                                    if (list1 == null) {
                                        list1 = pz1And11MonthList.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine26()) && a.getCdfine26().equals(f.getCdfine26())).collect(Collectors.toList());
                                    } else {
                                        list1 = list1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine26()) && a.getCdfine26().equals(f.getCdfine26())).collect(Collectors.toList());
                                    }
                                    if (list2 == null) {
                                        list2 = pz12MonthList.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine26()) && a.getCdfine26().equals(f.getCdfine26())).collect(Collectors.toList());
                                    } else {
                                        list2 = list2.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine26()) && a.getCdfine26().equals(f.getCdfine26())).collect(Collectors.toList());
                                    }
                                }
                                if (StrUtil.isNotBlank(f.getCdfine27())) {
                                    mx.setCdfine27(f.getCdfine27());
                                    if (list1 == null) {
                                        list1 = pz1And11MonthList.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine27()) && a.getCdfine27().equals(f.getCdfine27())).collect(Collectors.toList());
                                    } else {
                                        list1 = list1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine27()) && a.getCdfine27().equals(f.getCdfine27())).collect(Collectors.toList());
                                    }
                                    if (list2 == null) {
                                        list2 = pz12MonthList.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine27()) && a.getCdfine27().equals(f.getCdfine27())).collect(Collectors.toList());
                                    } else {
                                        list2 = list2.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine27()) && a.getCdfine27().equals(f.getCdfine27())).collect(Collectors.toList());
                                    }
                                }
                                if (StrUtil.isNotBlank(f.getCdfine28())) {
                                    mx.setCdfine28(f.getCdfine28());
                                    if (list1 == null) {
                                        list1 = pz1And11MonthList.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine28()) && a.getCdfine28().equals(f.getCdfine28())).collect(Collectors.toList());
                                    } else {
                                        list1 = list1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine28()) && a.getCdfine28().equals(f.getCdfine28())).collect(Collectors.toList());
                                    }
                                    if (list2 == null) {
                                        list2 = pz12MonthList.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine28()) && a.getCdfine28().equals(f.getCdfine28())).collect(Collectors.toList());
                                    } else {
                                        list2 = list2.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine28()) && a.getCdfine28().equals(f.getCdfine28())).collect(Collectors.toList());
                                    }
                                }
                                if (StrUtil.isNotBlank(f.getCdfine29())) {
                                    mx.setCdfine29(f.getCdfine29());
                                    if (list1 == null) {
                                        list1 = pz1And11MonthList.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine29()) && a.getCdfine29().equals(f.getCdfine29())).collect(Collectors.toList());
                                    } else {
                                        list1 = list1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine29()) && a.getCdfine29().equals(f.getCdfine29())).collect(Collectors.toList());
                                    }
                                    if (list2 == null) {
                                        list2 = pz12MonthList.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine29()) && a.getCdfine29().equals(f.getCdfine29())).collect(Collectors.toList());
                                    } else {
                                        list2 = list2.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine29()) && a.getCdfine29().equals(f.getCdfine29())).collect(Collectors.toList());
                                    }
                                }
                                if (StrUtil.isNotBlank(f.getCdfine30())) {
                                    mx.setCdfine30(f.getCdfine30());
                                    if (list1 == null) {
                                        list1 = pz1And11MonthList.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine30()) && a.getCdfine30().equals(f.getCdfine30())).collect(Collectors.toList());
                                    } else {
                                        list1 = list1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine30()) && a.getCdfine30().equals(f.getCdfine30())).collect(Collectors.toList());
                                    }
                                    if (list2 == null) {
                                        list2 = pz12MonthList.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine30()) && a.getCdfine30().equals(f.getCdfine30())).collect(Collectors.toList());
                                    } else {
                                        list2 = list2.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine30()) && a.getCdfine30().equals(f.getCdfine30())).collect(Collectors.toList());
                                    }
                                }

                                BigDecimal f_qcMd = f.getMd();
                                BigDecimal f_qcMc = f.getMc();

                                // 辅助科目凭证 1-11
                                BigDecimal f_pzMd = list1.size() > 0 ? list1.stream().map(AccvoucherVo::getMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum) : new BigDecimal(0);
                                BigDecimal f_pzMc = list1.size() > 0 ? list1.stream().map(AccvoucherVo::getMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum) : new BigDecimal(0);
                                // 辅助科目凭证 12
                                BigDecimal f_pzMd2 = list2.size() > 0 ? list2.stream().map(AccvoucherVo::getMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum) : new BigDecimal(0);
                                BigDecimal f_pzMc2 = list2.size() > 0 ? list2.stream().map(AccvoucherVo::getMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum) : new BigDecimal(0);

                                if (cd.getUpCcodeBprogerty().equals("1")) {
                                    BigDecimal yue = f_qcMd.add(f_pzMd).subtract(f_qcMc.add(f_pzMc));
                                    BigDecimal yue2 = yue.add(f_pzMd2).subtract(f_pzMc2);
                                    if (yue2.compareTo(BigDecimal.ZERO) > 0) {
                                        mx.setMd(yue2).setMc(new BigDecimal(0));
                                    } else {
                                        mx.setMc(yue2.multiply(new BigDecimal(-1))).setMd(new BigDecimal(0));
                                    }
                                } else {
                                    BigDecimal yue = f_qcMc.add(f_pzMc).subtract(f_qcMd.add(f_pzMd));
                                    BigDecimal yue2 = yue.add(f_pzMc2).subtract(f_pzMd2);
                                    if (yue2.compareTo(BigDecimal.ZERO) > 0) {
                                        mx.setMc(yue2).setMd(new BigDecimal(0));
                                    } else {
                                        mx.setMd(yue2.multiply(new BigDecimal(-1))).setMc(new BigDecimal(0));
                                    }
                                }
                                mxlist.add(mx);
                            });
                            cd.setFuzhuQclist(mxlist);

                            // 汇总辅助明细借贷方
                            BigDecimal totalMd = mxlist.stream().map(AccvoucherVo3::getMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                            BigDecimal totalMc = mxlist.stream().map(AccvoucherVo3::getMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                            if (cd.getUpCcodeBprogerty().equals("1")) {
                                BigDecimal yue = totalMd.subtract(totalMc);
                                if (yue.compareTo(BigDecimal.ZERO) > 0) {
                                    cd.setUpMd(yue).setUpMc(new BigDecimal(0));
                                } else {
                                    cd.setUpMd(new BigDecimal(0)).setUpMc(yue.multiply(new BigDecimal(-1)));
                                }
                            } else {
                                BigDecimal yue = totalMc.subtract(totalMd);
                                if (yue.compareTo(BigDecimal.ZERO) > 0) {
                                    cd.setUpMc(yue).setUpMd(new BigDecimal(0));
                                } else {
                                    cd.setUpMc(new BigDecimal(0)).setUpMd(yue.multiply(new BigDecimal(-1)));
                                }
                            }
                        }
                    });
                    return Mono.just(carryDownList);
                }).map(a -> R.ok().setResult(a));
    }

    /**
     * 开始结转下年期初
     *
     * @return
     */
    @PostMapping("/company/company_setNewYearQc")
    public Mono<R> company_setNewYearQc(@RequestBody Map map) {
        String iyear = map.get("iyear").toString();
        String upiyear = String.valueOf(Integer.valueOf(iyear) - 1);
        // json转list<T>
        List<CarryDownCodeKemuVo> list = JsonListUtil.jsonToList(map.get("list").toString(), CarryDownCodeKemuVo.class);

        // 00-21  先测试上年
        return accvoucherRepository.findAllByCode0021(iyear).collectList()
                .flatMap(codeqc0021List -> {
                    List<Accvoucher> add00 = new ArrayList<>();
                    List<Accvoucher> edit00 = new ArrayList<>();

                    List<Accvoucher> add21 = new ArrayList<>();
                    List<Accvoucher> edit21 = new ArrayList<>();
                    list.forEach(t -> {
                        String ccode = t.getThisCcode().split("-").length > 0 ? t.getThisCcode().split("-")[0] : t.getThisCcode();
                        // 本年是否已设置期初
                        List<Accvoucher> collect = codeqc0021List.stream().filter(f -> f.getCcode().equals(ccode) && f.getImonth().equals("00")).collect(Collectors.toList());
                        if (collect.size() > 0) {
                            collect.get(0)
                                    .setMd(t.getUpMd().toString())
                                    .setMc(t.getUpMc().toString());
                            edit00.add(collect.get(0));
                        } else {
                            Accvoucher a = new Accvoucher();
                            a.setIyear(iyear).setImonth("00").setIyperiod(iyear + "00").setCcode(t.getThisCcode()).setCcodeName(t.getThisCcodeName())
                                    .setMd(t.getUpMd().toString()).setMc(t.getUpMc().toString()).setNdS("0").setNcS("0").setIbook("0").setLjMd("0").setLjMc("0").setNfratMd("0").setNfratMc("0")
                                    .setLjWbMd("0").setLjWbMc("0").setLjSlMd("0").setLjSlMc("0");
                            add00.add(a);
                        }

                        // 本年是否已设置辅助期初
                        if (t.getFuzhuQclist().size() > 0) {
                            // 本年是否已设置辅助期初
                            List<Accvoucher> collect1 = codeqc0021List.stream().filter(f -> f.getCcode().equals(ccode) && f.getImonth().equals("21")).collect(Collectors.toList());
                            if (collect.size() > 0) {
                                collect1.forEach(f -> {
                                    List<Accvoucher> list1 = new ArrayList<>();
                                    if (StrUtil.isNotBlank(f.getCdeptId())) {
                                        list1 = collect1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdeptId()) && a.getCdeptId().equals(f.getCdeptId())).collect(Collectors.toList());
                                    }
                                    if (StrUtil.isNotBlank(f.getCpersonId())) {
                                        if (list1 == null) {
                                            list1 = collect1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCpersonId()) && a.getCpersonId().equals(f.getCpersonId())).collect(Collectors.toList());
                                        } else {
                                            list1 = list1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCpersonId()) && a.getCpersonId().equals(f.getCpersonId())).collect(Collectors.toList());
                                        }
                                    }
                                    if (StrUtil.isNotBlank(f.getCcusId())) {
                                        if (list1 == null) {
                                            list1 = collect1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCcusId()) && a.getCcusId().equals(f.getCcusId())).collect(Collectors.toList());
                                        } else {
                                            list1 = list1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCcusId()) && a.getCcusId().equals(f.getCcusId())).collect(Collectors.toList());
                                        }
                                    }
                                    if (StrUtil.isNotBlank(f.getCsupId())) {
                                        if (list1 == null) {
                                            list1 = collect1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCsupId()) && a.getCsupId().equals(f.getCsupId())).collect(Collectors.toList());
                                        } else {
                                            list1 = list1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCsupId()) && a.getCsupId().equals(f.getCsupId())).collect(Collectors.toList());
                                        }
                                    }
                                    if (StrUtil.isNotBlank(f.getProjectId())) {
                                        if (list1 == null) {
                                            list1 = collect1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getProjectId()) && a.getProjectId().equals(f.getProjectId())).collect(Collectors.toList());
                                        } else {
                                            list1 = list1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getProjectId()) && a.getProjectId().equals(f.getProjectId())).collect(Collectors.toList());
                                        }
                                    }
                                    if (StrUtil.isNotBlank(f.getCdfine1())) {
                                        if (list1 == null) {
                                            list1 = collect1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine1()) && a.getCdfine1().equals(f.getCdfine1())).collect(Collectors.toList());
                                        } else {
                                            list1 = list1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine1()) && a.getCdfine1().equals(f.getCdfine1())).collect(Collectors.toList());
                                        }
                                    }
                                    if (StrUtil.isNotBlank(f.getCdfine2())) {
                                        if (list1 == null) {
                                            list1 = collect1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine2()) && a.getCdfine2().equals(f.getCdfine2())).collect(Collectors.toList());
                                        } else {
                                            list1 = list1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine2()) && a.getCdfine2().equals(f.getCdfine2())).collect(Collectors.toList());
                                        }
                                    }
                                    if (StrUtil.isNotBlank(f.getCdfine3())) {
                                        if (list1 == null) {
                                            list1 = collect1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine3()) && a.getCdfine3().equals(f.getCdfine3())).collect(Collectors.toList());
                                        } else {
                                            list1 = list1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine3()) && a.getCdfine3().equals(f.getCdfine3())).collect(Collectors.toList());
                                        }
                                    }
                                    if (StrUtil.isNotBlank(f.getCdfine4())) {
                                        if (list1 == null) {
                                            list1 = collect1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine4()) && a.getCdfine4().equals(f.getCdfine4())).collect(Collectors.toList());
                                        } else {
                                            list1 = list1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine4()) && a.getCdfine4().equals(f.getCdfine4())).collect(Collectors.toList());
                                        }
                                    }
                                    if (StrUtil.isNotBlank(f.getCdfine5())) {
                                        if (list1 == null) {
                                            list1 = collect1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine5()) && a.getCdfine5().equals(f.getCdfine5())).collect(Collectors.toList());
                                        } else {
                                            list1 = list1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine5()) && a.getCdfine5().equals(f.getCdfine5())).collect(Collectors.toList());
                                        }
                                    }
                                    if (StrUtil.isNotBlank(f.getCdfine6())) {
                                        if (list1 == null) {
                                            list1 = collect1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine6()) && a.getCdfine6().equals(f.getCdfine6())).collect(Collectors.toList());
                                        } else {
                                            list1 = list1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine6()) && a.getCdfine6().equals(f.getCdfine6())).collect(Collectors.toList());
                                        }
                                    }
                                    if (StrUtil.isNotBlank(f.getCdfine7())) {
                                        if (list1 == null) {
                                            list1 = collect1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine7()) && a.getCdfine7().equals(f.getCdfine7())).collect(Collectors.toList());
                                        } else {
                                            list1 = list1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine7()) && a.getCdfine7().equals(f.getCdfine7())).collect(Collectors.toList());
                                        }
                                    }
                                    if (StrUtil.isNotBlank(f.getCdfine8())) {
                                        if (list1 == null) {
                                            list1 = collect1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine8()) && a.getCdfine8().equals(f.getCdfine8())).collect(Collectors.toList());
                                        } else {
                                            list1 = list1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine8()) && a.getCdfine8().equals(f.getCdfine8())).collect(Collectors.toList());
                                        }
                                    }
                                    if (StrUtil.isNotBlank(f.getCdfine9())) {
                                        if (list1 == null) {
                                            list1 = collect1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine9()) && a.getCdfine9().equals(f.getCdfine9())).collect(Collectors.toList());
                                        } else {
                                            list1 = list1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine9()) && a.getCdfine9().equals(f.getCdfine9())).collect(Collectors.toList());
                                        }
                                    }
                                    if (StrUtil.isNotBlank(f.getCdfine10())) {
                                        if (list1 == null) {
                                            list1 = collect1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine10()) && a.getCdfine10().equals(f.getCdfine10())).collect(Collectors.toList());
                                        } else {
                                            list1 = list1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine10()) && a.getCdfine10().equals(f.getCdfine10())).collect(Collectors.toList());
                                        }
                                    }
                                    if (StrUtil.isNotBlank(f.getCdfine11())) {
                                        if (list1 == null) {
                                            list1 = collect1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine11()) && a.getCdfine11().equals(f.getCdfine11())).collect(Collectors.toList());
                                        } else {
                                            list1 = list1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine11()) && a.getCdfine11().equals(f.getCdfine11())).collect(Collectors.toList());
                                        }
                                    }
                                    if (StrUtil.isNotBlank(f.getCdfine12())) {
                                        if (list1 == null) {
                                            list1 = collect1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine12()) && a.getCdfine12().equals(f.getCdfine12())).collect(Collectors.toList());
                                        } else {
                                            list1 = list1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine12()) && a.getCdfine12().equals(f.getCdfine12())).collect(Collectors.toList());
                                        }
                                    }
                                    if (StrUtil.isNotBlank(f.getCdfine13())) {
                                        if (list1 == null) {
                                            list1 = collect1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine13()) && a.getCdfine13().equals(f.getCdfine13())).collect(Collectors.toList());
                                        } else {
                                            list1 = list1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine13()) && a.getCdfine13().equals(f.getCdfine13())).collect(Collectors.toList());
                                        }
                                    }
                                    if (StrUtil.isNotBlank(f.getCdfine14())) {
                                        if (list1 == null) {
                                            list1 = collect1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine14()) && a.getCdfine14().equals(f.getCdfine14())).collect(Collectors.toList());
                                        } else {
                                            list1 = list1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine14()) && a.getCdfine14().equals(f.getCdfine14())).collect(Collectors.toList());
                                        }
                                    }
                                    if (StrUtil.isNotBlank(f.getCdfine15())) {
                                        if (list1 == null) {
                                            list1 = collect1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine15()) && a.getCdfine15().equals(f.getCdfine15())).collect(Collectors.toList());
                                        } else {
                                            list1 = list1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine15()) && a.getCdfine15().equals(f.getCdfine15())).collect(Collectors.toList());
                                        }
                                    }
                                    if (StrUtil.isNotBlank(f.getCdfine16())) {
                                        if (list1 == null) {
                                            list1 = collect1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine16()) && a.getCdfine16().equals(f.getCdfine16())).collect(Collectors.toList());
                                        } else {
                                            list1 = list1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine16()) && a.getCdfine16().equals(f.getCdfine16())).collect(Collectors.toList());
                                        }
                                    }
                                    if (StrUtil.isNotBlank(f.getCdfine17())) {
                                        if (list1 == null) {
                                            list1 = collect1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine17()) && a.getCdfine17().equals(f.getCdfine17())).collect(Collectors.toList());
                                        } else {
                                            list1 = list1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine17()) && a.getCdfine17().equals(f.getCdfine17())).collect(Collectors.toList());
                                        }
                                    }
                                    if (StrUtil.isNotBlank(f.getCdfine18())) {
                                        if (list1 == null) {
                                            list1 = collect1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine18()) && a.getCdfine18().equals(f.getCdfine18())).collect(Collectors.toList());
                                        } else {
                                            list1 = list1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine18()) && a.getCdfine18().equals(f.getCdfine18())).collect(Collectors.toList());
                                        }
                                    }
                                    if (StrUtil.isNotBlank(f.getCdfine19())) {
                                        if (list1 == null) {
                                            list1 = collect1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine19()) && a.getCdfine19().equals(f.getCdfine19())).collect(Collectors.toList());
                                        } else {
                                            list1 = list1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine19()) && a.getCdfine19().equals(f.getCdfine19())).collect(Collectors.toList());
                                        }
                                    }
                                    if (StrUtil.isNotBlank(f.getCdfine20())) {
                                        if (list1 == null) {
                                            list1 = collect1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine20()) && a.getCdfine20().equals(f.getCdfine20())).collect(Collectors.toList());
                                        } else {
                                            list1 = list1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine20()) && a.getCdfine20().equals(f.getCdfine20())).collect(Collectors.toList());
                                        }
                                    }
                                    if (StrUtil.isNotBlank(f.getCdfine21())) {
                                        if (list1 == null) {
                                            list1 = collect1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine21()) && a.getCdfine21().equals(f.getCdfine21())).collect(Collectors.toList());
                                        } else {
                                            list1 = list1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine21()) && a.getCdfine21().equals(f.getCdfine21())).collect(Collectors.toList());
                                        }
                                    }
                                    if (StrUtil.isNotBlank(f.getCdfine22())) {
                                        if (list1 == null) {
                                            list1 = collect1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine22()) && a.getCdfine22().equals(f.getCdfine22())).collect(Collectors.toList());
                                        } else {
                                            list1 = list1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine22()) && a.getCdfine22().equals(f.getCdfine22())).collect(Collectors.toList());
                                        }
                                    }
                                    if (StrUtil.isNotBlank(f.getCdfine23())) {
                                        if (list1 == null) {
                                            list1 = collect1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine23()) && a.getCdfine23().equals(f.getCdfine23())).collect(Collectors.toList());
                                        } else {
                                            list1 = list1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine23()) && a.getCdfine23().equals(f.getCdfine23())).collect(Collectors.toList());
                                        }
                                    }
                                    if (StrUtil.isNotBlank(f.getCdfine24())) {
                                        if (list1 == null) {
                                            list1 = collect1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine24()) && a.getCdfine24().equals(f.getCdfine24())).collect(Collectors.toList());
                                        } else {
                                            list1 = list1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine24()) && a.getCdfine24().equals(f.getCdfine24())).collect(Collectors.toList());
                                        }
                                    }
                                    if (StrUtil.isNotBlank(f.getCdfine25())) {
                                        if (list1 == null) {
                                            list1 = collect1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine25()) && a.getCdfine25().equals(f.getCdfine25())).collect(Collectors.toList());
                                        } else {
                                            list1 = list1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine25()) && a.getCdfine25().equals(f.getCdfine25())).collect(Collectors.toList());
                                        }
                                    }
                                    if (StrUtil.isNotBlank(f.getCdfine26())) {
                                        if (list1 == null) {
                                            list1 = collect1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine26()) && a.getCdfine26().equals(f.getCdfine26())).collect(Collectors.toList());
                                        } else {
                                            list1 = list1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine26()) && a.getCdfine26().equals(f.getCdfine26())).collect(Collectors.toList());
                                        }
                                    }
                                    if (StrUtil.isNotBlank(f.getCdfine27())) {
                                        if (list1 == null) {
                                            list1 = collect1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine27()) && a.getCdfine27().equals(f.getCdfine27())).collect(Collectors.toList());
                                        } else {
                                            list1 = list1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine27()) && a.getCdfine27().equals(f.getCdfine27())).collect(Collectors.toList());
                                        }
                                    }
                                    if (StrUtil.isNotBlank(f.getCdfine28())) {
                                        if (list1 == null) {
                                            list1 = collect1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine28()) && a.getCdfine28().equals(f.getCdfine28())).collect(Collectors.toList());
                                        } else {
                                            list1 = list1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine28()) && a.getCdfine28().equals(f.getCdfine28())).collect(Collectors.toList());
                                        }
                                    }
                                    if (StrUtil.isNotBlank(f.getCdfine29())) {
                                        if (list1 == null) {
                                            list1 = collect1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine29()) && a.getCdfine29().equals(f.getCdfine29())).collect(Collectors.toList());
                                        } else {
                                            list1 = list1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine29()) && a.getCdfine29().equals(f.getCdfine29())).collect(Collectors.toList());
                                        }
                                    }
                                    if (StrUtil.isNotBlank(f.getCdfine30())) {
                                        if (list1 == null) {
                                            list1 = collect1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine30()) && a.getCdfine30().equals(f.getCdfine30())).collect(Collectors.toList());
                                        } else {
                                            list1 = list1.stream().filter(a -> a.getCcode().equals(f.getCcode()) && StrUtil.isNotBlank(a.getCdfine30()) && a.getCdfine30().equals(f.getCdfine30())).collect(Collectors.toList());
                                        }
                                    }

                                    // 找到已设置辅助期初记录
                                    if (list1.size() > 0) {
                                        List<AccvoucherVo3> fuzhuQclist = t.getFuzhuQclist();
                                        List<Accvoucher> finalList = list1;
                                        list1.stream().forEach(li1 -> {
                                            List<AccvoucherVo3> fuzhuQclist1 = new ArrayList<>();
                                            if (StrUtil.isNotBlank(li1.getCdeptId())) {
                                                fuzhuQclist1 = fuzhuQclist.stream().filter(a -> a.getCcode().equals(li1.getCcode()) && StrUtil.isNotBlank(a.getCdeptId()) && a.getCdeptId().equals(li1.getCdeptId())).collect(Collectors.toList());
                                            }
                                            if (StrUtil.isNotBlank(li1.getCpersonId())) {
                                                if (fuzhuQclist1 == null) {
                                                    fuzhuQclist1 = fuzhuQclist.stream().filter(a -> a.getCcode().equals(li1.getCcode()) && StrUtil.isNotBlank(a.getCpersonId()) && a.getCpersonId().equals(li1.getCpersonId())).collect(Collectors.toList());
                                                } else {
                                                    fuzhuQclist1 = fuzhuQclist1.stream().filter(a -> a.getCcode().equals(li1.getCcode()) && StrUtil.isNotBlank(a.getCpersonId()) && a.getCpersonId().equals(li1.getCpersonId())).collect(Collectors.toList());
                                                }
                                            }
                                            if (StrUtil.isNotBlank(li1.getCcusId())) {
                                                if (fuzhuQclist1 == null) {
                                                    fuzhuQclist1 = fuzhuQclist.stream().filter(a -> a.getCcode().equals(li1.getCcode()) && StrUtil.isNotBlank(a.getCcusId()) && a.getCcusId().equals(li1.getCcusId())).collect(Collectors.toList());
                                                } else {
                                                    fuzhuQclist1 = fuzhuQclist1.stream().filter(a -> a.getCcode().equals(li1.getCcode()) && StrUtil.isNotBlank(a.getCcusId()) && a.getCcusId().equals(li1.getCcusId())).collect(Collectors.toList());
                                                }
                                            }
                                            if (StrUtil.isNotBlank(li1.getCsupId())) {
                                                if (fuzhuQclist1 == null) {
                                                    fuzhuQclist1 = fuzhuQclist.stream().filter(a -> a.getCcode().equals(li1.getCcode()) && StrUtil.isNotBlank(a.getCsupId()) && a.getCsupId().equals(li1.getCsupId())).collect(Collectors.toList());
                                                } else {
                                                    fuzhuQclist1 = fuzhuQclist1.stream().filter(a -> a.getCcode().equals(li1.getCcode()) && StrUtil.isNotBlank(a.getCsupId()) && a.getCsupId().equals(li1.getCsupId())).collect(Collectors.toList());
                                                }
                                            }
                                            if (StrUtil.isNotBlank(li1.getProjectId())) {
                                                if (fuzhuQclist1 == null) {
                                                    fuzhuQclist1 = fuzhuQclist.stream().filter(a -> a.getCcode().equals(li1.getCcode()) && StrUtil.isNotBlank(a.getProjectId()) && a.getProjectId().equals(li1.getProjectId())).collect(Collectors.toList());
                                                } else {
                                                    fuzhuQclist1 = fuzhuQclist1.stream().filter(a -> a.getCcode().equals(li1.getCcode()) && StrUtil.isNotBlank(a.getProjectId()) && a.getProjectId().equals(li1.getProjectId())).collect(Collectors.toList());
                                                }
                                            }
                                            if (StrUtil.isNotBlank(li1.getCdfine1())) {
                                                if (fuzhuQclist1 == null) {
                                                    fuzhuQclist1 = fuzhuQclist.stream().filter(a -> a.getCcode().equals(li1.getCcode()) && StrUtil.isNotBlank(a.getCdfine1()) && a.getCdfine1().equals(li1.getCdfine1())).collect(Collectors.toList());
                                                } else {
                                                    fuzhuQclist1 = fuzhuQclist1.stream().filter(a -> a.getCcode().equals(li1.getCcode()) && StrUtil.isNotBlank(a.getCdfine1()) && a.getCdfine1().equals(li1.getCdfine1())).collect(Collectors.toList());
                                                }
                                            }
                                            if (StrUtil.isNotBlank(li1.getCdfine2())) {
                                                if (fuzhuQclist1 == null) {
                                                    fuzhuQclist1 = fuzhuQclist.stream().filter(a -> a.getCcode().equals(li1.getCcode()) && StrUtil.isNotBlank(a.getCdfine2()) && a.getCdfine2().equals(li1.getCdfine2())).collect(Collectors.toList());
                                                } else {
                                                    fuzhuQclist1 = fuzhuQclist1.stream().filter(a -> a.getCcode().equals(li1.getCcode()) && StrUtil.isNotBlank(a.getCdfine2()) && a.getCdfine2().equals(li1.getCdfine2())).collect(Collectors.toList());
                                                }
                                            }
                                            if (StrUtil.isNotBlank(li1.getCdfine3())) {
                                                if (fuzhuQclist1 == null) {
                                                    fuzhuQclist1 = fuzhuQclist.stream().filter(a -> a.getCcode().equals(li1.getCcode()) && StrUtil.isNotBlank(a.getCdfine3()) && a.getCdfine3().equals(li1.getCdfine3())).collect(Collectors.toList());
                                                } else {
                                                    fuzhuQclist1 = fuzhuQclist1.stream().filter(a -> a.getCcode().equals(li1.getCcode()) && StrUtil.isNotBlank(a.getCdfine3()) && a.getCdfine3().equals(li1.getCdfine3())).collect(Collectors.toList());
                                                }
                                            }
                                            if (StrUtil.isNotBlank(li1.getCdfine4())) {
                                                if (fuzhuQclist1 == null) {
                                                    fuzhuQclist1 = fuzhuQclist.stream().filter(a -> a.getCcode().equals(li1.getCcode()) && StrUtil.isNotBlank(a.getCdfine4()) && a.getCdfine4().equals(li1.getCdfine4())).collect(Collectors.toList());
                                                } else {
                                                    fuzhuQclist1 = fuzhuQclist1.stream().filter(a -> a.getCcode().equals(li1.getCcode()) && StrUtil.isNotBlank(a.getCdfine4()) && a.getCdfine4().equals(li1.getCdfine4())).collect(Collectors.toList());
                                                }
                                            }
                                            if (StrUtil.isNotBlank(li1.getCdfine5())) {
                                                if (fuzhuQclist1 == null) {
                                                    fuzhuQclist1 = fuzhuQclist.stream().filter(a -> a.getCcode().equals(li1.getCcode()) && StrUtil.isNotBlank(a.getCdfine5()) && a.getCdfine5().equals(li1.getCdfine5())).collect(Collectors.toList());
                                                } else {
                                                    fuzhuQclist1 = fuzhuQclist1.stream().filter(a -> a.getCcode().equals(li1.getCcode()) && StrUtil.isNotBlank(a.getCdfine5()) && a.getCdfine5().equals(li1.getCdfine5())).collect(Collectors.toList());
                                                }
                                            }
                                            if (StrUtil.isNotBlank(li1.getCdfine6())) {
                                                if (fuzhuQclist1 == null) {
                                                    fuzhuQclist1 = fuzhuQclist.stream().filter(a -> a.getCcode().equals(li1.getCcode()) && StrUtil.isNotBlank(a.getCdfine6()) && a.getCdfine6().equals(li1.getCdfine6())).collect(Collectors.toList());
                                                } else {
                                                    fuzhuQclist1 = fuzhuQclist1.stream().filter(a -> a.getCcode().equals(li1.getCcode()) && StrUtil.isNotBlank(a.getCdfine6()) && a.getCdfine6().equals(li1.getCdfine6())).collect(Collectors.toList());
                                                }
                                            }
                                            if (StrUtil.isNotBlank(li1.getCdfine7())) {
                                                if (fuzhuQclist1 == null) {
                                                    fuzhuQclist1 = fuzhuQclist.stream().filter(a -> a.getCcode().equals(li1.getCcode()) && StrUtil.isNotBlank(a.getCdfine7()) && a.getCdfine7().equals(li1.getCdfine7())).collect(Collectors.toList());
                                                } else {
                                                    fuzhuQclist1 = fuzhuQclist1.stream().filter(a -> a.getCcode().equals(li1.getCcode()) && StrUtil.isNotBlank(a.getCdfine7()) && a.getCdfine7().equals(li1.getCdfine7())).collect(Collectors.toList());
                                                }
                                            }
                                            if (StrUtil.isNotBlank(li1.getCdfine8())) {
                                                if (fuzhuQclist1 == null) {
                                                    fuzhuQclist1 = fuzhuQclist.stream().filter(a -> a.getCcode().equals(li1.getCcode()) && StrUtil.isNotBlank(a.getCdfine8()) && a.getCdfine8().equals(li1.getCdfine8())).collect(Collectors.toList());
                                                } else {
                                                    fuzhuQclist1 = fuzhuQclist1.stream().filter(a -> a.getCcode().equals(li1.getCcode()) && StrUtil.isNotBlank(a.getCdfine8()) && a.getCdfine8().equals(li1.getCdfine8())).collect(Collectors.toList());
                                                }
                                            }
                                            if (StrUtil.isNotBlank(li1.getCdfine9())) {
                                                if (fuzhuQclist1 == null) {
                                                    fuzhuQclist1 = fuzhuQclist.stream().filter(a -> a.getCcode().equals(li1.getCcode()) && StrUtil.isNotBlank(a.getCdfine9()) && a.getCdfine9().equals(li1.getCdfine9())).collect(Collectors.toList());
                                                } else {
                                                    fuzhuQclist1 = fuzhuQclist1.stream().filter(a -> a.getCcode().equals(li1.getCcode()) && StrUtil.isNotBlank(a.getCdfine9()) && a.getCdfine9().equals(li1.getCdfine9())).collect(Collectors.toList());
                                                }
                                            }
                                            if (StrUtil.isNotBlank(li1.getCdfine10())) {
                                                if (fuzhuQclist1 == null) {
                                                    fuzhuQclist1 = fuzhuQclist.stream().filter(a -> a.getCcode().equals(li1.getCcode()) && StrUtil.isNotBlank(a.getCdfine10()) && a.getCdfine10().equals(li1.getCdfine10())).collect(Collectors.toList());
                                                } else {
                                                    fuzhuQclist1 = fuzhuQclist1.stream().filter(a -> a.getCcode().equals(li1.getCcode()) && StrUtil.isNotBlank(a.getCdfine10()) && a.getCdfine10().equals(li1.getCdfine10())).collect(Collectors.toList());
                                                }
                                            }
                                            if (StrUtil.isNotBlank(li1.getCdfine11())) {
                                                if (fuzhuQclist1 == null) {
                                                    fuzhuQclist1 = fuzhuQclist.stream().filter(a -> a.getCcode().equals(li1.getCcode()) && StrUtil.isNotBlank(a.getCdfine11()) && a.getCdfine11().equals(li1.getCdfine11())).collect(Collectors.toList());
                                                } else {
                                                    fuzhuQclist1 = fuzhuQclist1.stream().filter(a -> a.getCcode().equals(li1.getCcode()) && StrUtil.isNotBlank(a.getCdfine11()) && a.getCdfine11().equals(li1.getCdfine11())).collect(Collectors.toList());
                                                }
                                            }
                                            if (StrUtil.isNotBlank(li1.getCdfine12())) {
                                                if (fuzhuQclist1 == null) {
                                                    fuzhuQclist1 = fuzhuQclist.stream().filter(a -> a.getCcode().equals(li1.getCcode()) && StrUtil.isNotBlank(a.getCdfine12()) && a.getCdfine12().equals(li1.getCdfine12())).collect(Collectors.toList());
                                                } else {
                                                    fuzhuQclist1 = fuzhuQclist1.stream().filter(a -> a.getCcode().equals(li1.getCcode()) && StrUtil.isNotBlank(a.getCdfine12()) && a.getCdfine12().equals(li1.getCdfine12())).collect(Collectors.toList());
                                                }
                                            }
                                            if (StrUtil.isNotBlank(li1.getCdfine13())) {
                                                if (fuzhuQclist1 == null) {
                                                    fuzhuQclist1 = fuzhuQclist.stream().filter(a -> a.getCcode().equals(li1.getCcode()) && StrUtil.isNotBlank(a.getCdfine13()) && a.getCdfine13().equals(li1.getCdfine13())).collect(Collectors.toList());
                                                } else {
                                                    fuzhuQclist1 = fuzhuQclist1.stream().filter(a -> a.getCcode().equals(li1.getCcode()) && StrUtil.isNotBlank(a.getCdfine13()) && a.getCdfine13().equals(li1.getCdfine13())).collect(Collectors.toList());
                                                }
                                            }
                                            if (StrUtil.isNotBlank(li1.getCdfine14())) {
                                                if (fuzhuQclist1 == null) {
                                                    fuzhuQclist1 = fuzhuQclist.stream().filter(a -> a.getCcode().equals(li1.getCcode()) && StrUtil.isNotBlank(a.getCdfine14()) && a.getCdfine14().equals(li1.getCdfine14())).collect(Collectors.toList());
                                                } else {
                                                    fuzhuQclist1 = fuzhuQclist1.stream().filter(a -> a.getCcode().equals(li1.getCcode()) && StrUtil.isNotBlank(a.getCdfine14()) && a.getCdfine14().equals(li1.getCdfine14())).collect(Collectors.toList());
                                                }
                                            }
                                            if (StrUtil.isNotBlank(li1.getCdfine15())) {
                                                if (fuzhuQclist1 == null) {
                                                    fuzhuQclist1 = fuzhuQclist.stream().filter(a -> a.getCcode().equals(li1.getCcode()) && StrUtil.isNotBlank(a.getCdfine15()) && a.getCdfine15().equals(li1.getCdfine15())).collect(Collectors.toList());
                                                } else {
                                                    fuzhuQclist1 = fuzhuQclist1.stream().filter(a -> a.getCcode().equals(li1.getCcode()) && StrUtil.isNotBlank(a.getCdfine15()) && a.getCdfine15().equals(li1.getCdfine15())).collect(Collectors.toList());
                                                }
                                            }
                                            if (StrUtil.isNotBlank(li1.getCdfine16())) {
                                                if (fuzhuQclist1 == null) {
                                                    fuzhuQclist1 = fuzhuQclist.stream().filter(a -> a.getCcode().equals(li1.getCcode()) && StrUtil.isNotBlank(a.getCdfine16()) && a.getCdfine16().equals(li1.getCdfine16())).collect(Collectors.toList());
                                                } else {
                                                    fuzhuQclist1 = fuzhuQclist1.stream().filter(a -> a.getCcode().equals(li1.getCcode()) && StrUtil.isNotBlank(a.getCdfine16()) && a.getCdfine16().equals(li1.getCdfine16())).collect(Collectors.toList());
                                                }
                                            }
                                            if (StrUtil.isNotBlank(li1.getCdfine17())) {
                                                if (fuzhuQclist1 == null) {
                                                    fuzhuQclist1 = fuzhuQclist.stream().filter(a -> a.getCcode().equals(li1.getCcode()) && StrUtil.isNotBlank(a.getCdfine17()) && a.getCdfine17().equals(li1.getCdfine17())).collect(Collectors.toList());
                                                } else {
                                                    fuzhuQclist1 = fuzhuQclist1.stream().filter(a -> a.getCcode().equals(li1.getCcode()) && StrUtil.isNotBlank(a.getCdfine17()) && a.getCdfine17().equals(li1.getCdfine17())).collect(Collectors.toList());
                                                }
                                            }
                                            if (StrUtil.isNotBlank(li1.getCdfine18())) {
                                                if (fuzhuQclist1 == null) {
                                                    fuzhuQclist1 = fuzhuQclist.stream().filter(a -> a.getCcode().equals(li1.getCcode()) && StrUtil.isNotBlank(a.getCdfine18()) && a.getCdfine18().equals(li1.getCdfine18())).collect(Collectors.toList());
                                                } else {
                                                    fuzhuQclist1 = fuzhuQclist1.stream().filter(a -> a.getCcode().equals(li1.getCcode()) && StrUtil.isNotBlank(a.getCdfine18()) && a.getCdfine18().equals(li1.getCdfine18())).collect(Collectors.toList());
                                                }
                                            }
                                            if (StrUtil.isNotBlank(li1.getCdfine19())) {
                                                if (fuzhuQclist1 == null) {
                                                    fuzhuQclist1 = fuzhuQclist.stream().filter(a -> a.getCcode().equals(li1.getCcode()) && StrUtil.isNotBlank(a.getCdfine19()) && a.getCdfine19().equals(li1.getCdfine19())).collect(Collectors.toList());
                                                } else {
                                                    fuzhuQclist1 = fuzhuQclist1.stream().filter(a -> a.getCcode().equals(li1.getCcode()) && StrUtil.isNotBlank(a.getCdfine19()) && a.getCdfine19().equals(li1.getCdfine19())).collect(Collectors.toList());
                                                }
                                            }
                                            if (StrUtil.isNotBlank(li1.getCdfine20())) {
                                                if (fuzhuQclist1 == null) {
                                                    fuzhuQclist1 = fuzhuQclist.stream().filter(a -> a.getCcode().equals(li1.getCcode()) && StrUtil.isNotBlank(a.getCdfine20()) && a.getCdfine20().equals(li1.getCdfine20())).collect(Collectors.toList());
                                                } else {
                                                    fuzhuQclist1 = fuzhuQclist1.stream().filter(a -> a.getCcode().equals(li1.getCcode()) && StrUtil.isNotBlank(a.getCdfine20()) && a.getCdfine20().equals(li1.getCdfine20())).collect(Collectors.toList());
                                                }
                                            }
                                            if (StrUtil.isNotBlank(li1.getCdfine21())) {
                                                if (fuzhuQclist1 == null) {
                                                    fuzhuQclist1 = fuzhuQclist.stream().filter(a -> a.getCcode().equals(li1.getCcode()) && StrUtil.isNotBlank(a.getCdfine21()) && a.getCdfine21().equals(li1.getCdfine21())).collect(Collectors.toList());
                                                } else {
                                                    fuzhuQclist1 = fuzhuQclist1.stream().filter(a -> a.getCcode().equals(li1.getCcode()) && StrUtil.isNotBlank(a.getCdfine21()) && a.getCdfine21().equals(li1.getCdfine21())).collect(Collectors.toList());
                                                }
                                            }
                                            if (StrUtil.isNotBlank(li1.getCdfine22())) {
                                                if (fuzhuQclist1 == null) {
                                                    fuzhuQclist1 = fuzhuQclist.stream().filter(a -> a.getCcode().equals(li1.getCcode()) && StrUtil.isNotBlank(a.getCdfine22()) && a.getCdfine22().equals(li1.getCdfine22())).collect(Collectors.toList());
                                                } else {
                                                    fuzhuQclist1 = fuzhuQclist1.stream().filter(a -> a.getCcode().equals(li1.getCcode()) && StrUtil.isNotBlank(a.getCdfine22()) && a.getCdfine22().equals(li1.getCdfine22())).collect(Collectors.toList());
                                                }
                                            }
                                            if (StrUtil.isNotBlank(li1.getCdfine23())) {
                                                if (fuzhuQclist1 == null) {
                                                    fuzhuQclist1 = fuzhuQclist.stream().filter(a -> a.getCcode().equals(li1.getCcode()) && StrUtil.isNotBlank(a.getCdfine23()) && a.getCdfine23().equals(li1.getCdfine23())).collect(Collectors.toList());
                                                } else {
                                                    fuzhuQclist1 = fuzhuQclist1.stream().filter(a -> a.getCcode().equals(li1.getCcode()) && StrUtil.isNotBlank(a.getCdfine23()) && a.getCdfine23().equals(li1.getCdfine23())).collect(Collectors.toList());
                                                }
                                            }
                                            if (StrUtil.isNotBlank(li1.getCdfine24())) {
                                                if (fuzhuQclist1 == null) {
                                                    fuzhuQclist1 = fuzhuQclist.stream().filter(a -> a.getCcode().equals(li1.getCcode()) && StrUtil.isNotBlank(a.getCdfine24()) && a.getCdfine24().equals(li1.getCdfine24())).collect(Collectors.toList());
                                                } else {
                                                    fuzhuQclist1 = fuzhuQclist1.stream().filter(a -> a.getCcode().equals(li1.getCcode()) && StrUtil.isNotBlank(a.getCdfine24()) && a.getCdfine24().equals(li1.getCdfine24())).collect(Collectors.toList());
                                                }
                                            }
                                            if (StrUtil.isNotBlank(li1.getCdfine25())) {
                                                if (fuzhuQclist1 == null) {
                                                    fuzhuQclist1 = fuzhuQclist.stream().filter(a -> a.getCcode().equals(li1.getCcode()) && StrUtil.isNotBlank(a.getCdfine25()) && a.getCdfine25().equals(li1.getCdfine25())).collect(Collectors.toList());
                                                } else {
                                                    fuzhuQclist1 = fuzhuQclist1.stream().filter(a -> a.getCcode().equals(li1.getCcode()) && StrUtil.isNotBlank(a.getCdfine25()) && a.getCdfine25().equals(li1.getCdfine25())).collect(Collectors.toList());
                                                }
                                            }
                                            if (StrUtil.isNotBlank(li1.getCdfine26())) {
                                                if (fuzhuQclist1 == null) {
                                                    fuzhuQclist1 = fuzhuQclist.stream().filter(a -> a.getCcode().equals(li1.getCcode()) && StrUtil.isNotBlank(a.getCdfine26()) && a.getCdfine26().equals(li1.getCdfine26())).collect(Collectors.toList());
                                                } else {
                                                    fuzhuQclist1 = fuzhuQclist1.stream().filter(a -> a.getCcode().equals(li1.getCcode()) && StrUtil.isNotBlank(a.getCdfine26()) && a.getCdfine26().equals(li1.getCdfine26())).collect(Collectors.toList());
                                                }
                                            }
                                            if (StrUtil.isNotBlank(li1.getCdfine27())) {
                                                if (fuzhuQclist1 == null) {
                                                    fuzhuQclist1 = fuzhuQclist.stream().filter(a -> a.getCcode().equals(li1.getCcode()) && StrUtil.isNotBlank(a.getCdfine27()) && a.getCdfine27().equals(li1.getCdfine27())).collect(Collectors.toList());
                                                } else {
                                                    fuzhuQclist1 = fuzhuQclist1.stream().filter(a -> a.getCcode().equals(li1.getCcode()) && StrUtil.isNotBlank(a.getCdfine27()) && a.getCdfine27().equals(li1.getCdfine27())).collect(Collectors.toList());
                                                }
                                            }
                                            if (StrUtil.isNotBlank(li1.getCdfine28())) {
                                                if (fuzhuQclist1 == null) {
                                                    fuzhuQclist1 = fuzhuQclist.stream().filter(a -> a.getCcode().equals(li1.getCcode()) && StrUtil.isNotBlank(a.getCdfine28()) && a.getCdfine28().equals(li1.getCdfine28())).collect(Collectors.toList());
                                                } else {
                                                    fuzhuQclist1 = fuzhuQclist1.stream().filter(a -> a.getCcode().equals(li1.getCcode()) && StrUtil.isNotBlank(a.getCdfine28()) && a.getCdfine28().equals(li1.getCdfine28())).collect(Collectors.toList());
                                                }
                                            }
                                            if (StrUtil.isNotBlank(li1.getCdfine29())) {
                                                if (fuzhuQclist1 == null) {
                                                    fuzhuQclist1 = fuzhuQclist.stream().filter(a -> a.getCcode().equals(li1.getCcode()) && StrUtil.isNotBlank(a.getCdfine29()) && a.getCdfine29().equals(li1.getCdfine29())).collect(Collectors.toList());
                                                } else {
                                                    fuzhuQclist1 = fuzhuQclist1.stream().filter(a -> a.getCcode().equals(li1.getCcode()) && StrUtil.isNotBlank(a.getCdfine29()) && a.getCdfine29().equals(li1.getCdfine29())).collect(Collectors.toList());
                                                }
                                            }
                                            if (StrUtil.isNotBlank(li1.getCdfine30())) {
                                                if (fuzhuQclist1 == null) {
                                                    fuzhuQclist1 = fuzhuQclist.stream().filter(a -> a.getCcode().equals(li1.getCcode()) && StrUtil.isNotBlank(a.getCdfine30()) && a.getCdfine30().equals(li1.getCdfine30())).collect(Collectors.toList());
                                                } else {
                                                    fuzhuQclist1 = fuzhuQclist1.stream().filter(a -> a.getCcode().equals(li1.getCcode()) && StrUtil.isNotBlank(a.getCdfine30()) && a.getCdfine30().equals(li1.getCdfine30())).collect(Collectors.toList());
                                                }
                                            }

                                            if (fuzhuQclist1.size() > 0) {
                                                // 把页面中的值 赋值 到表中对应的辅助项期初余额
                                                li1.setMd(fuzhuQclist1.get(0).getMd().toString()).setMc(fuzhuQclist1.get(0).getMc().toString());
//                                            log.info("表中的="+finalList);
//                                            log.info("页面传的="+fuzhuQclist1);
                                            }
                                            edit21.add(li1);
//                                        log.info("修改后="+li1);
                                        });
                                    }
                                });
                            } else {
                                t.getFuzhuQclist().forEach(f -> {
                                    Accvoucher a = new Accvoucher();
                                    a.setIyear(iyear).setImonth("21").setIyperiod(iyear + "21").setCcode(f.getCcode()).setCcodeName(f.getCcodeName())
                                            .setMd(f.getMd().toString()).setMc(f.getMc().toString()).setNdS("0").setNcS("0").setIbook("0").setLjMd("0").setLjMc("0").setNfratMd("0").setNfratMc("0")
                                            .setLjWbMd("0").setLjWbMc("0").setLjSlMd("0").setLjSlMc("0")
                                            .setCdeptId(f.getCdeptId()).setCpersonId(f.getCpersonId()).setCcusId(f.getCcusId()).setCsupId(f.getCsupId()).setProjectId(f.getProjectId())
                                            .setCdfine1(f.getCdfine1())
                                            .setCdfine2(f.getCdfine2())
                                            .setCdfine3(f.getCdfine3())
                                            .setCdfine4(f.getCdfine4())
                                            .setCdfine5(f.getCdfine5())
                                            .setCdfine6(f.getCdfine6())
                                            .setCdfine7(f.getCdfine7())
                                            .setCdfine8(f.getCdfine8())
                                            .setCdfine9(f.getCdfine9())
                                            .setCdfine10(f.getCdfine10())
                                            .setCdfine11(f.getCdfine11())
                                            .setCdfine12(f.getCdfine12())
                                            .setCdfine13(f.getCdfine13())
                                            .setCdfine14(f.getCdfine14())
                                            .setCdfine15(f.getCdfine15())
                                            .setCdfine16(f.getCdfine16())
                                            .setCdfine17(f.getCdfine17())
                                            .setCdfine18(f.getCdfine18())
                                            .setCdfine19(f.getCdfine19())
                                            .setCdfine20(f.getCdfine20())
                                            .setCdfine21(f.getCdfine21())
                                            .setCdfine22(f.getCdfine22())
                                            .setCdfine23(f.getCdfine23())
                                            .setCdfine24(f.getCdfine24())
                                            .setCdfine25(f.getCdfine25())
                                            .setCdfine26(f.getCdfine26())
                                            .setCdfine27(f.getCdfine27())
                                            .setCdfine28(f.getCdfine28())
                                            .setCdfine29(f.getCdfine29())
                                            .setCdfine30(f.getCdfine30());
                                    add21.add(a);
                                });
                            }
                        }
                    });

                    Mono<List<Accvoucher>> add00Mono = accvoucherRepository.saveAll(add00).collectList();
                    Mono<List<Accvoucher>> edit00Mono = accvoucherRepository.saveAll(edit00).collectList();
                    Mono<List<Accvoucher>> add21Mono = accvoucherRepository.saveAll(add21).collectList();
                    Mono<List<Accvoucher>> edit21Mono = accvoucherRepository.saveAll(edit21).collectList();
                    return Mono.zip(add00Mono, edit00Mono, add21Mono, edit21Mono).then(Mono.just("ok"));
                }).map(a -> R.ok().setResult(a));
    }


    @PostMapping("/company/company_findByCodeAccVoucher")
    public Mono<R> company_findByCodeAccVoucher(String iyear) {
        return accvoucherRepository.findByIyear2(iyear).collectList().map(a -> R.ok().setResult(a));
    }

    @PostMapping("/company/company_executeSQL")
    public Mono<R> company_executeSQL(String sql) {
        return client.sql(sql).fetch().all().collectList().map(a -> R.ok().setResult(a));
    }

    @PostMapping("/company/company_executeAccvoucher")
    public Mono<R> company_executeAccvoucher(@RequestBody List<Accvoucher> list) {
        return accvoucherRepository.saveAll(list).collectList().map(a -> R.ok().setResult(a));
    }

    @PostMapping("/company/company_auditCodeKemuSave")
    public Mono<R> company_auditCodeKemuSave(@RequestBody AuditCodeKemu audit) {
        if (StrUtil.isNotBlank(audit.getId())) {
            audit.setId(null);
        }
        return auditCodeKemuRepository.save(audit).map(a -> R.ok().setResult(a));
    }

    // 账套引入组织科目
    @PostMapping("/company/company_bringOrgKemu")
    public Mono<R> company_bringOrgKemu(@RequestBody AuditCodeKemu audit) {
        if (StrUtil.isNotBlank(audit.getId())) {
            audit.setId(null);
        }
        return auditCodeKemuRepository.save(audit).map(a -> R.ok().setResult(a));
    }

    // 简单科目增加
    @PostMapping("/company/company_simpleSave")
    public Mono<R> company_simpleSave(@RequestBody Map map) {
        String ccodeName = map.get("ccodeName").toString();
        String parentCode = map.get("parentCode").toString();
        String currencyType = map.get("currencyType").toString();
        String iyear = map.get("iyear").toString();
        String newLastCode = map.get("newLastCode").toString();
        String newLastIgrade = map.get("newLastIgrade").toString();
        // 上级科目信息
        return codeKemuRepository.company_findByIyearAndCcode(iyear, parentCode)
                .flatMap(data -> {
                    String oldBend=data.getBend();
                    data.setBend("0");
                    // 修改上级科目末级标识
                    return codeKemuRepository.save(data)
                    .flatMap(data2->{
                        data2.setId(null)
                                .setBend("1")
                                .setUniqueCode(newLastCode)
                                .setCcode(newLastCode)
                                .setCcodeName(ccodeName)
                                .setIgrade(newLastIgrade)
                                .setSuperiorCcode(parentCode);

                        if(StrUtil.isNotBlank(currencyType)){
                            data2.setCurrency("1").setCurrencyType(currencyType);
                        }
                        // 添加新下级科目
                        return codeKemuRepository.save(data2)
                                .flatMap(newData->{
                                    // 上级科目凭证\期初
                                   return accvoucherRepository.findByCcodeAndIyearOrderByDbillDate(parentCode,iyear).collectList()
                                        .flatMap(accvoucherList->{
                                            accvoucherList.forEach(accv->{
                                                accv.setCcode(newLastCode);
                                            });
                                            return accvoucherRepository.saveAll(accvoucherList).collectList().map(a->Mono.just(""));
                                        });
                                });
                    });
                })
                .map(a -> R.ok().setResult(a));
    }

    @PostMapping("/company/findByCcodeAndIyearOrderByDbillDate")
    public Mono<R> findByCcodeAndIyearOrderByDbillDate(@RequestBody Map map) {
        String ccode=map.get("ccode").toString();
        String iyear=map.get("iyear").toString();
        return accvoucherRepository.findByCcodeAndIyearOrderByDbillDate(ccode,iyear).collectList()
                .flatMap(list->{
                    return Mono.just(list.size());
                })
                .map(a -> R.ok().setResult(a));
    }


    /**************************************************** 查询授权科目 ***************************************************************************/
    @PostMapping("/company/company_findByAuthorizationKeMu")
    public Mono<R> company_findByAuthorizationKeMu(@RequestBody Map map) {
        String tenantId = map.get("tenantId").toString();
        String userId = map.get("userId").toString();
        return codeKemuRepository.findByAuthorizationKeMu(tenantId,userId).collectList().map(a -> R.ok().setResult(a));
    }

    @PostMapping("/company/company_findByAuthorizationAndIyearKeMu")
    public Mono<R> findByAuthorizationAndIyearKeMu(@RequestBody Map map) {
        String tenantId = map.get("tenantId").toString();
        String iyear = map.get("iyear").toString();
        String userId = map.get("userId").toString();
        return codeKemuRepository.findByAuthorizationAndIyearKeMu(tenantId, iyear,userId).collectList().map(a -> R.ok().setResult(a));
    }

    @PostMapping("/company/company_findByAuthorizationKeMuTree")
    public Mono<R> company_findByAuthorizationKeMuTree(@RequestBody Map map) {
        String tenantId = map.get("tenantId").toString();
        String userId = map.get("userId").toString();
        return codeKemuRepository.findByAuthorizationKeMu(tenantId,userId).collectList().map(a -> R.ok().setResult(getChild("0", a)));
    }

    @PostMapping("/company/company_findByAuthorizationAndIyearKeMuTree")
    public Mono<R> company_findByAuthorizationAndIyearKeMuTree(@RequestBody Map map) {
        String tenantId = map.get("tenantId").toString();
        String iyear = map.get("iyear").toString();
        String userId = map.get("userId").toString();
        return codeKemuRepository.findByAuthorizationAndIyearKeMu(tenantId, iyear,userId).collectList().map(a -> R.ok().setResult(getChild("0", a)));
    }

    /**************************************************** 查询授权科目 ***************************************************************************/

    public List<Map<String, Object>> getChild123(String pid, List<CodeKemu> allList) {
        List<Map<String, Object>> childList = new ArrayList<>();//用于保存子节点的list;
        for (CodeKemu ms : allList) {
            if (ms.getSuperiorCcode().equals(pid)) {//判断传入的父id是否等于自身的，如果是，就说明自己是子节点
                Map<String, Object> map = new HashMap<>();
                map.put("title", ms.getCcode() + "-" + ms.getCcodeName());
                map.put("value", ms.getUniqueCode());
                map.put("key", ms.getCcode());
                map.put("bend", ms.getBend());
                map.put("children", new Object[]{});
                childList.add(map); //加入子节点
            }
        }
        for (Map<String, Object> map : childList) {//遍历子节点，继续递归判断每个子节点是否还含有子节点
            List<Map<String, Object>> tList = getChild(String.valueOf(map.get("key")), allList);
            map.put("children", tList);
        }
        return childList;
    }


    @PostMapping("/company/findByLastCodeHierarchyNames")
    public Mono<R> findByLastCodeHierarchyNames(@RequestBody Map map) {
        String iyear = map.get("iyear").toString();
        AtomicReference<Long> totalAR = new AtomicReference(0);
        return codeKemuRepository.company_findByLastCode("0", iyear).collectList().zipWith(codeKemuRepository.company_findByLastCode("1", iyear).collectList())
                .flatMap(tips -> {
                    List<CodeKemu> allList = tips.getT1();
                    List<CodeKemu> list = tips.getT2();
                    //
                    return fuzhuHesuanRepository.findAll().collectList()
                            .flatMap(fuzhulist -> {
                                for (CodeKemu ms : list) {
                                    // 5个辅助项
                                    String fuzhu = "";
                                    if (StringUtils.isNotBlank(ms.getBperson()) && ms.getBperson().equals("1")) {
                                        fuzhu += "个人,";
                                    }
                                    if (StringUtils.isNotBlank(ms.getBdept()) && ms.getBdept().equals("1")) {
                                        fuzhu += "部门,";
                                    }
                                    if (StringUtils.isNotBlank(ms.getBcus()) && ms.getBcus().equals("1")) {
                                        fuzhu += "客户,";
                                    }
                                    if (StringUtils.isNotBlank(ms.getBsup()) && ms.getBsup().equals("1")) {
                                        fuzhu += "供应商,";
                                    }
                                    if (StringUtils.isNotBlank(ms.getBitem()) && ms.getBitem().equals("1")) {
                                        fuzhu += "项目,";
                                    }
                                    if (StringUtils.isNotBlank(ms.getYsYsly()) && ms.getYsYsly().equals("1")) {
                                        fuzhu += "预算来源,";
                                    }
                                    if (StringUtils.isNotBlank(ms.getYsZcgnfl()) && ms.getYsZcgnfl().equals("1")) {
                                        fuzhu += "支出功能分类,";
                                    }
                                    if (StringUtils.isNotBlank(ms.getYsZfzcjjfl()) && ms.getYsZfzcjjfl().equals("1")) {
                                        fuzhu += "政府支出经济分类,";
                                    }
                                    if (StringUtils.isNotBlank(ms.getYsBmzcjjfl()) && ms.getYsBmzcjjfl().equals("1")) {
                                        fuzhu += "部门支出经济分类,";
                                    }
                                    if (!"".equals(fuzhu)) {
                                        ms.setFuzhu(fuzhu.substring(0, fuzhu.length() - 1));
                                    }
                                    Map byZdyFuZhu = findByZdyFuZhu(fuzhulist, ms);
                                    String a = "";
                                    if (StrUtil.isNotBlank(fuzhu)) {
                                        a = fuzhu;
                                        if (StrUtil.isNotBlank(byZdyFuZhu.get("result").toString())) {
                                            a += "," + byZdyFuZhu.get("result").toString();
                                        }
                                        a = a.substring(0, a.length() - 1);
                                    }
                                    ms.setFuzhu(a);
                                    // 获取上级名称
                                    ms.setCcodeName(getCurrCodeSupperName(ms,allList));
                                }
                                return Mono.just(list);
                            });
                })
                .map(a -> R.ok(a));
    }

    private String getCurrCodeSupperName(CodeKemu ms, List<CodeKemu> allList) {
        String name = ms.getCcodeName();
        String condition = ms.getSuperiorCcode();
           while (StrUtil.isNotBlank(condition)){
               String finalCondition = condition;
               List<CodeKemu> collect = allList.stream().filter(it -> it.getCcode().equals(finalCondition)).collect(Collectors.toList());
               if (collect.size() > 0) {
                   name = collect.get(0).getCcodeName()+"/"+name;
                   condition = collect.get(0).getSuperiorCcode();
               }else {
                   condition = null;
               }
           }
        return name;
    }

    @PostMapping("/company/findByIyearCcod")
    public Mono<R> findByIyearCcod(@RequestBody Map map) {
        String iyear=map.get("iyear").toString();
        return codeKemuRepository.findAllByIyear(iyear).collectList().flatMap(list->{
            list.forEach(t->{
                t.setValue(t.getCcode());
                t.setTitle(t.getCcode()+"-"+t.getCcodeName());
                t.setLabel(t.getCcode()+"-"+t.getCcodeName());
            });
           return Mono.just(list);
        }).map(R::ok);
    }
}

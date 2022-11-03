package org.boozsoft.rest;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.boozsoft.domain.entity.SysCurrency;
import org.boozsoft.domain.entity.acctemplate.AccTemplate;
import org.boozsoft.domain.entity.group.GroupCodeKemu;
import org.boozsoft.domain.entity.group.GroupCodeKemuOrg;
import org.boozsoft.domain.entity.group.GroupSysVoucherType;
import org.boozsoft.domain.vo.CustomTemplateVo;
import org.boozsoft.repo.CurrencyRepository;
import org.boozsoft.repo.acctemplate.AccTemplateRepository;
import org.boozsoft.repo.codekemu.GroupCodeKemuRepository;
import org.boozsoft.repo.group.GroupCodeKemuOrgRepository;
import org.boozsoft.repo.group.GroupSysVoucherTypeRepository;
import org.boozsoft.repo.standard.AccStandardRepository;
import org.boozsoft.service.AccTemplateService;
import org.springbooz.core.tool.result.R;
import org.springbooz.core.tool.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName : AccTemplateController
 * @Description : 科目模板
 * @Author : miao
 * @Date: 2021-05-12 10:40
 */

@Slf4j
@RestController
@RequestMapping("/sys/acctemplate")
@Api(value = "科目模板", tags = "API系统：科目模板")
public class AccTemplateController {
    @Autowired
    AccTemplateRepository templateRepository;
    @Autowired
    GroupCodeKemuRepository codeKemuRepository;
    @Autowired
    GroupCodeKemuOrgRepository groupCodeKemuOrgRepository;
    @Autowired
    AccTemplateService service;

    @Autowired
    CurrencyRepository currencyRepository;

    @Autowired
    AccStandardRepository standardRepository;


    @PostMapping("/findByCustomTemplate")
    public Mono<R> findByCustomTemplate(String type) {
        return templateRepository.findByCustomTemplate(type).collectList().map(R::page);
    }

    @GetMapping("/findAllAssTemplate")
    public Mono<R> findAllAssTemplate(String str) {
        // 会计准则唯一码_科目类型唯一码BOOZ科目级次（第一级）
        String uniqueAccStandard = "1";
        if (str != null) {
            str = str.split("BOOZ")[0];
            uniqueAccStandard = str.split("_")[0];
        }
        return templateRepository.findAllAssTemplate(uniqueAccStandard, String.valueOf(uniqueAccStandard), "自定义模板").collectList().map(R::page);
    }

    @PostMapping("/findAllByUniqueAccStndard")
    public Mono<R> findAllByUniqueAccStndard(String uniqueAccStandard, String accStyleUnique, String type) {
        return templateRepository.findAllAssTemplate(uniqueAccStandard, accStyleUnique, type).collectList().map(R::page);
    }

    @PostMapping("/countByUniqueAccStandardAndTName")
    public Mono<R> countByUniqueAccStandardAndTName(String uniqueAccStandard, String tName, String ttype) {
        return templateRepository.countByUniqueAccStandardAndTName(uniqueAccStandard, tName, ttype).map(o -> R.ok().setResult(o));
    }

    /**
     * 组织科目增加
     *
     * @param map
     * @return
     */
    @PostMapping("/accTemplateZZSave")
    public Mono<R> accTemplateZZSave(@RequestBody Map map) {
        String jici = "";
        for (int i = 0; i < map.get("tJici").toString().split(",").length; i++) {
            if (map.get("tJici").toString().split(",")[i].equals("")) {
                break;
            } else {
                jici += map.get("tJici").toString().split(",")[i] + "-";
            }
        }

        AccTemplate template = new AccTemplate();
        template
                .setTName(map.get("tName").toString())
                .setTFlg(map.get("tFlg").toString())
                .setTJici(jici.substring(0, jici.length() - 1))
                .setUniqueAccStandard(map.get("uniqueAccStandard").toString())
                .setTNum(1)
                .setTPid(map.get("tpid").toString())
                .setTOrganization(map.get("tOrganization").toString())
                .setTType(map.get("ttype").toString());


        // 预置系统科目
        Mono<Map> flg_true = templateRepository.save(template)
                .flatMap(o -> {
                    return service.findByZZCodeKeMuAndsave(o.getId(), template.getUniqueAccStandard(), template.getTPid(), template.getTJici(),map.get("iyear").toString(),map.get("tOrganization").toString());
                });
        return flg_true.map(o -> R.ok().setResult(o));
    }

    /**
     * 修改模板名称
     *
     * @return
     */
    @PostMapping("/editTemplateName")
    public Mono<R> editTemplateName(String id, String name, String tjici) {
        String jici = "";
        for (int i = 0; i < tjici.split(",").length; i++) {
            if (tjici.split(",")[i].equals("")) {
                break;
            } else {
                jici += tjici.split(",")[i] + "-";
            }
        }
        return templateRepository.editTemplateName(id, name, jici.substring(0, jici.length() - 1)).then(Mono.just(R.ok()));
    }

    // 集团模板
    @PostMapping("/accTemplateSave")
    public Mono<R> accTemplateSave(@RequestBody Map map) {
        AccTemplate template = new AccTemplate();
        template
                .setTName(map.get("tName").toString().trim())
                .setTFlg(map.get("tFlg").toString())
                .setTJici(map.get("tJici").toString())
                .setUniqueAccStandard(map.get("uniqueAccStandard").toString())
                .setTNum(1)
                .setTType(map.get("tType").toString());
        // 不预置系统科目
        Mono flg_false = templateRepository.save(template);
        // 预置系统科目：集团从国家科目表中获取
        Mono<Map> flg_true = templateRepository.save(template)
                .flatMap(o -> {
                    return service.save(o.getId(), template.getUniqueAccStandard(), template.getUniqueAccStandard(), template.getTJici());
                });
        return template.getTFlg().equals("1") ? flg_true.map(o -> R.ok().setResult(o)) : flg_false.map(o -> R.ok().setResult(o));
    }

    @PostMapping("/accTemplateDel")
    public Mono accTemplateDel(String id) {
        return templateRepository.deleteById(id).then(Mono.just(R.ok()));
    }

    @PostMapping("/codekemuDel")
    public Mono codekemuDel(String id) {
        return codeKemuRepository.deleteAllByTemplateId(id).then(Mono.just(R.ok()));
    }

    @PostMapping("/findAllByTemplateID")
    public Mono<R> findAllByTemplateID(String id) {
        return templateRepository.findById(id).map(o -> R.ok().setResult(o));
    }

    /**
     * 查找科目
     *
     * @param uniqueAccStandard 会计准则唯一码
     * @param templateId        科目模板ID
     * @param cclass            科目类型
     * @return
     */
    @PostMapping("/findAllByUniqueAccStandardAndTemplateIdAndCclass")
    public Mono<R> findAllByUniqueAccStandardAndTemplateIdAndCclass(String uniqueAccStandard, String templateId, String cclass) {
//        String uniqueAccStandard=map.get("uniqueAccStandard").toString();
//        String templateId=map.get("templateId").toString();
//        String cclass=map.get("cclass").toString();

        if (cclass.equals("全部")) {
            return codeKemuRepository.findAllByUniqueAccStandardAndTemplateIdOrderByCcodeAsc(uniqueAccStandard, templateId)
                    .collectList()
                    .flatMap(list -> {
                        for (int i = 0; i < list.size(); i++) {
                            String fuzhu = "";
                            if (StringUtils.isNotBlank(list.get(i).getBdept()) && !list.get(i).getBdept().equals("0")) {
                                fuzhu += "部门,";
                            }
                            if (StringUtils.isNotBlank(list.get(i).getBperson()) && !list.get(i).getBperson().equals("0")) {
                                fuzhu += "个人,";
                            }
                            if (StringUtils.isNotBlank(list.get(i).getBcus()) && !list.get(i).getBcus().equals("0")) {
                                fuzhu += "客户,";
                            }
                            if (StringUtils.isNotBlank(list.get(i).getBsup()) && !list.get(i).getBsup().equals("0")) {
                                fuzhu += "供应商,";
                            }
                            if (StringUtils.isNotBlank(list.get(i).getBitem()) && !list.get(i).getBitem().equals("0")) {
                                fuzhu += "项目,";
                            }
                            if (StringUtils.isNotBlank(list.get(i).getYsYsly()) && !list.get(i).getYsYsly().equals("0")) {
                                fuzhu += "预算来源,";
                            }
                            if (StringUtils.isNotBlank(list.get(i).getYsZcgnfl()) && !list.get(i).getYsZcgnfl().equals("0")) {
                                fuzhu += "支出功能分类,";
                            }
                            if (StringUtils.isNotBlank(list.get(i).getYsZfzcjjfl()) && !list.get(i).getYsZfzcjjfl().equals("0")) {
                                fuzhu += "政府支出经济分类,";
                            }
                            if (StringUtils.isNotBlank(list.get(i).getYsBmzcjjfl()) && !list.get(i).getYsBmzcjjfl().equals("0")) {
                                fuzhu += "部门支出经济分类,";
                            }
                            list.get(i).setFuzhu(StringUtils.isBlank(fuzhu) ? "" : fuzhu.substring(0, fuzhu.length() - 1));
                        }
                        return Mono.just(list);
                    }).map(o -> R.ok().setResult(o));
        } else {
            // cclass : 例：1.资产
            return codeKemuRepository.findAllByUniqueAccStandardAndTemplateIdAndCclassOrderByCcodeAsc(uniqueAccStandard, templateId, cclass.substring(2)).collectList().flatMap(list -> {
                for (int i = 0; i < list.size(); i++) {
                    String fuzhu = "";
                    if (StringUtils.isNotBlank(list.get(i).getBdept()) && !list.get(i).getBdept().equals("0")) {
                        fuzhu += "部门,";
                    }
                    if (StringUtils.isNotBlank(list.get(i).getBperson()) && !list.get(i).getBperson().equals("0")) {
                        fuzhu += "个人,";
                    }
                    if (StringUtils.isNotBlank(list.get(i).getBcus()) && !list.get(i).getBcus().equals("0")) {
                        fuzhu += "客户,";
                    }
                    if (StringUtils.isNotBlank(list.get(i).getBsup()) && !list.get(i).getBsup().equals("0")) {
                        fuzhu += "供应商,";
                    }
                    if (StringUtils.isNotBlank(list.get(i).getBitem()) && !list.get(i).getBitem().equals("0")) {
                        fuzhu += "项目,";
                    }
                    if (StringUtils.isNotBlank(list.get(i).getYsYsly()) && !list.get(i).getYsYsly().equals("0")) {
                        fuzhu += "预算来源,";
                    }
                    if (StringUtils.isNotBlank(list.get(i).getYsZcgnfl()) && !list.get(i).getYsZcgnfl().equals("0")) {
                        fuzhu += "支出功能分类,";
                    }
                    if (StringUtils.isNotBlank(list.get(i).getYsZfzcjjfl()) && !list.get(i).getYsZfzcjjfl().equals("0")) {
                        fuzhu += "政府支出经济分类,";
                    }
                    if (StringUtils.isNotBlank(list.get(i).getYsBmzcjjfl()) && !list.get(i).getYsBmzcjjfl().equals("0")) {
                        fuzhu += "部门支出经济分类,";
                    }
                    list.get(i).setFuzhu(StringUtils.isBlank(fuzhu) ? "" : fuzhu.substring(0, fuzhu.length() - 1));
                }
                return Mono.just(list);
            }).map(o -> R.ok().setResult(o));
        }
    }

    /**
     * 科目树形结构
     *
     * @return
     */
    @PostMapping("treeCode")
    public Mono<R> treeCode(String uniqueAccStandard, String templateId) {
        return codeKemuRepository.findAllByUniqueAccStandardAndTemplateIdOrderByCcodeAsc(uniqueAccStandard, templateId)
                .collectList()
                .map(list -> R.ok().setResult(getChild("0", list)));
    }

    @PostMapping("treeOrgCode")
    public Mono<R> treeOrgCode(String originId,String iyear) {
        return groupCodeKemuOrgRepository.findAllByIyearAndOrgUniqueOrderByCcode(iyear,originId)
                .collectList()
                .map(list -> R.ok().setResult(getOrgCodeChild("0", list)));
    }

    public List<Map<String, Object>> getChild(String pid, List<GroupCodeKemu> allList) {
        List<Map<String, Object>> childList = new ArrayList<>();//用于保存子节点的list;
        for (GroupCodeKemu ms : allList) {
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
    public List<Map<String, Object>> getOrgCodeChild(String pid, List<GroupCodeKemuOrg> allList) {
        List<Map<String, Object>> childList = new ArrayList<>();//用于保存子节点的list;
        for (GroupCodeKemuOrg ms : allList) {
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
            List<Map<String, Object>> tList = getOrgCodeChild(String.valueOf(map.get("key")), allList);
            map.put("children", tList);
        }
        return childList;
    }
    /**
     * 获取科目模板
     *
     * @return reactor.core.publisher.Mono<org.springbooz.core.tool.result.R>
     **/
    @PostMapping("finByTemplate")
    public Mono<R> finByTemplate(String templateId) {
        return templateRepository.findById(templateId).map(o -> R.ok().setResult(o));
    }

    /**
     * 组织下的模板：一个组织只能有一个模板
     *
     * @param tOrganization
     * @return
     */
    @PostMapping("findByTOrganization")
    public Mono<R> findByTOrganization(String tOrganization) {
        return templateRepository.findByTOrganization(tOrganization).collectList().map(o -> R.ok().setResult(o));
    }



    /* *************************************** 获取账套数据 *************************************************** */

    /**
     * 查找科目
     *
     * @return
     */

    @PostMapping("/company/company_findAllByUniqueAccStandardAndTemplateIdAndCclass")
    public Mono<R> company_findAllByUniqueAccStandardAndTemplateIdAndCclass(@RequestBody Map map) {
        String uniqueAccStandard = map.get("uniqueAccStandard").toString();
        String templateId = map.get("templateId").toString();
        String cclass = map.get("activeKey").toString();
        Map<String, String> searchMap = ((HashMap<String, String>) map.get("searchConditon"));
        String requirement = searchMap.get("requirement");
        String searchvalue = searchMap.get("value");

        if (cclass.equals("全部")) {
            return codeKemuRepository.findAllByUniqueAccStandardAndTemplateIdOrderByCcodeAsc(uniqueAccStandard, templateId).collectList()
                    .map(list -> {
                        for (int i = 0; i < list.size(); i++) {
                            String fuzhu = "";
                            if (StringUtils.isNotBlank(list.get(i).getBdept()) && !list.get(i).getBdept().equals("0")) {
                                fuzhu += "部门,";
                            }
                            if (StringUtils.isNotBlank(list.get(i).getBperson()) && !list.get(i).getBperson().equals("0")) {
                                fuzhu += "个人,";
                            }
                            if (StringUtils.isNotBlank(list.get(i).getBcus()) && !list.get(i).getBcus().equals("0")) {
                                fuzhu += "客户,";
                            }
                            if (StringUtils.isNotBlank(list.get(i).getBsup()) && !list.get(i).getBsup().equals("0")) {
                                fuzhu += "供应商,";
                            }
                            if (StringUtils.isNotBlank(list.get(i).getBitem()) && !list.get(i).getBitem().equals("0")) {
                                fuzhu += "项目,";
                            }
                            list.get(i).setFuzhu(StringUtils.isBlank(fuzhu) ? "" : fuzhu.substring(0, fuzhu.length() - 1));
                        }
                        return list;
                    })
                    .flatMap(item -> {
                        List<GroupCodeKemu> collect = item;
                        if (StringUtils.isNotBlank(searchvalue)) {
                            if ("bprogerty".equals(requirement)) {
                                String bprogerty = searchvalue.contains("借") ? "1" : "0";
                                collect = item.stream().filter(ck -> ck.getBprogerty().equals(bprogerty)).collect(Collectors.toList());
                            } else if ("ccode".equals(requirement)) {
                                collect = item.stream().filter(ck -> ck.getCcode().equals(searchvalue)).collect(Collectors.toList());
                            } else if ("ccodeName".equals(requirement)) {
                                collect = item.stream().filter(ck -> ck.getCcodeName().equals(searchvalue)).collect(Collectors.toList());
                            }
                        }
                        return Mono.just(collect);
                    })
                    .map(m -> R.ok().setResult(m));
        } else {
            return codeKemuRepository.findAllByUniqueAccStandardAndTemplateIdAndCclassOrderByCcodeAsc(uniqueAccStandard, templateId, cclass).collectList()
                    .map(list -> {
                        for (int i = 0; i < list.size(); i++) {
                            String fuzhu = "";
                            if (StringUtils.isNotBlank(list.get(i).getBdept()) && !list.get(i).getBdept().equals("0")) {
                                fuzhu += "部门,";
                            }
                            if (StringUtils.isNotBlank(list.get(i).getBperson()) && !list.get(i).getBperson().equals("0")) {
                                fuzhu += "个人,";
                            }
                            if (StringUtils.isNotBlank(list.get(i).getBcus()) && !list.get(i).getBcus().equals("0")) {
                                fuzhu += "客户,";
                            }
                            if (StringUtils.isNotBlank(list.get(i).getBsup()) && !list.get(i).getBsup().equals("0")) {
                                fuzhu += "供应商,";
                            }
                            if (StringUtils.isNotBlank(list.get(i).getBitem()) && !list.get(i).getBitem().equals("0")) {
                                fuzhu += "项目,";
                            }
                            list.get(i).setFuzhu(StringUtils.isBlank(fuzhu) ? "" : fuzhu.substring(0, fuzhu.length() - 1));
                        }
                        return list;
                    })
                    .flatMap(item -> {
                        List<GroupCodeKemu> collect = item;
                        if (StringUtils.isNotBlank(searchvalue)) {
                            if ("bprogerty".equals(requirement)) {
                                String bprogerty = searchvalue.contains("借") ? "1" : "0";
                                collect = item.stream().filter(ck -> ck.getBprogerty().equals(bprogerty)).collect(Collectors.toList());
                            } else if ("ccode".equals(requirement)) {
                                collect = item.stream().filter(ck -> ck.getCcode().equals(searchvalue)).collect(Collectors.toList());
                            } else if ("ccodeName".equals(requirement)) {
                                collect = item.stream().filter(ck -> ck.getCcodeName().equals(searchvalue)).collect(Collectors.toList());
                            }
                        }
                        return Mono.just(collect);
                    })
                    .map(m -> R.ok().setResult(m));
        }
    }


    @PostMapping("/company/company_findAllByTemplateID")
    public Mono<R> company_findAllByTemplateID(String id) {
        return templateRepository.findById(id).map(o -> R.ok().setResult(o));
    }


    /********************* Mr.Ye *********************/
    @Autowired
    GroupSysVoucherTypeRepository groupSysVoucherTypeRepository;

    @GetMapping("/findBasisInfo")
    public Mono<R> findBasisInfo() {
        return Mono.just("")
                .flatMap(resultMap -> {
                    Mono<List<CustomTemplateVo>> one = templateRepository.findByCustomTemplate("自定义模板").collectList();
                    Mono<List<AccTemplate>> two = templateRepository.findAll().collectList();
                    //Mono<List<SysCurrency>> there = currencyRepository.findAllByOrderByNumAsc().collectList();
                    Mono<List<GroupSysVoucherType>> four = groupSysVoucherTypeRepository.findAll().collectList();
                    return Mono.zip(one, two, /*there,*/ four);
                }).map(obj -> {
                            Map<String, Object> map = new HashMap<>();
                            map.put("acountStandardList", obj.getT1());
                            map.put("levelList", obj.getT2());
                           // map.put("currencyList", obj.getT3());
                            map.put("voucherList", obj.getT3());
                            return map;
                        }
                ).map(map -> R.ok().setResult(map));
    }

    /********************* Mr.Ye *********************/

    @PostMapping("/findByUniqueAccStandard")
    public Mono<R> findByUniqueAccStandard(String uniqueAccStandard) {
        return templateRepository.findByUniqueAccStandard(uniqueAccStandard).collectList().map(R::page);
    }

    /**
     * 根据类型查询名称是否重复
     *
     * @param ttype
     * @param tName
     * @return
     */
    @PostMapping("/countByName")
    public Mono<R> countByName(String ttype, String tName) {
        return templateRepository.countByName(ttype, tName).map(map -> R.ok().setResult(map)).defaultIfEmpty(R.ok().setResult(""));
    }

    @PostMapping("/findTemplateByAccId")
    public Mono<R> findTemplateByAccId(String accId){
        return templateRepository.findTemplateByAccId(accId).map(a->R.ok().setResult(a)).defaultIfEmpty(R.ok().setResult(""));
    }

    @PostMapping("/findByAccId")
    public Mono<R> findByAccId(String accId){
        return templateRepository.findByAccId(accId).map(a->R.ok().setResult(a)).defaultIfEmpty(R.ok().setResult(""));
    }
    @PostMapping("/editTemplateNameById")
    public Mono<R> editTemplateNameById(String id,String name){
        return templateRepository.editTemplateNameById(id,name).then(Mono.just(R.ok()));
    }
}

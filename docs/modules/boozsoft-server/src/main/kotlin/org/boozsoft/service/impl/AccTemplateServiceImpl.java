package org.boozsoft.service.impl;

import cn.hutool.core.util.StrUtil;
import org.boozsoft.domain.entity.codekemu.CodeKemu;
import org.boozsoft.domain.entity.group.GroupCodeKemu;
import org.boozsoft.domain.entity.group.GroupCodeKemuCountry;
import org.boozsoft.domain.entity.group.GroupCodeKemuOrg;
import org.boozsoft.repo.codekemu.GroupCodeKemuRepository;
import org.boozsoft.repo.group.GroupCodeKemuCountryRepository;
import org.boozsoft.repo.group.GroupCodeKemuOrgRepository;
import org.boozsoft.service.AccTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 科目模板
 */
@Service
public class AccTemplateServiceImpl implements AccTemplateService {
    @Autowired
    GroupCodeKemuRepository codeKemuRepository;
    @Autowired
    GroupCodeKemuCountryRepository groupCodeKemuCountryRepository;
    @Autowired
    GroupCodeKemuOrgRepository groupCodeKemuOrgRepository;

    // 获取此会计准则模板科目
    public Flux<GroupCodeKemu> findAllByUniqueAccStandardAndTemplateId(String uniqueAccStandard, String templateId) {
        return codeKemuRepository.findAllByUniqueAccStandardAndTemplateIdOrderByCcodeAsc(uniqueAccStandard, templateId);
    }

    public List<Map<String, Object>> getChild(String pid, List<CodeKemu> allList){
        List<Map<String, Object>> childList = new ArrayList<>();//用于保存子节点的list;
        for (CodeKemu ms : allList) {
            if (ms.getSuperiorCcode().equals(pid)) {//判断传入的父id是否等于自身的，如果是，就说明自己是子节点
                Map<String, Object> map = new HashMap<>();
                map.put("title", ms.getCcode()+"-"+ms.getCcodeName());
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

    public int getkmLevel(int level){
        String a="4-2-2-2-2-2-2-2-2-2-2-2-2-2-2";
        String arr = a.replaceAll("-", "").substring(0, level);
        int jici=0;
        for (int i = 0; i < arr.length(); i++) {
            jici = jici + Integer.valueOf(arr.charAt(i));
        }
        return jici;
    }
    public List<GroupCodeKemu> test(String id, String uniqueAccStandard,String jici,List<GroupCodeKemuCountry> item){
        List<GroupCodeKemu> list = new ArrayList<>();
        int a = 0;  // 科目编码长度
        int ai = 0; // 科目级次
        a = 0;  // 科目编码长度
        ai = 0; // 科目级次
        String test="";//  旧编码_新编码
        for (int i = 0; i < jici.split("-").length; i++) {
            ai = i + 1;
            a += Integer.valueOf(jici.split("-")[i]);
            for (int j = 0; j < item.size(); j++) {
                // 循环级次
                if (ai == Integer.valueOf(item.get(j).getIgrade())) {
                        if(item.get(j).getCcode().length() > 4) {
                            String superCode = item.get(j).getSuperiorCcode();
                            String codeEnd = item.get(j).getCcode().substring(superCode.length());

                            String newCode = "";
                            if (Integer.valueOf(jici.split("-")[i]) != codeEnd.length()) {
                                // 补0后总位数
                                newCode = superCode + String.format("%0" + Integer.valueOf(jici.split("-")[i]) + "d", Integer.valueOf(codeEnd));
                                test += item.get(j).getCcode() + "_" + newCode + ",";
                            }

                            GroupCodeKemu codekm = new GroupCodeKemu();
                            codekm
                                    .setUniqueAccStandard(uniqueAccStandard)
                                    .setTemplateId(id)
                                    .setCclass(item.get(j).getCclass())
                                    .setCcode(newCode.equals("") ? item.get(j).getCcode() : newCode)
                                    .setCcodeName(item.get(j).getCcodeName())
                                    .setIgrade(item.get(j).getIgrade())
                                    .setBprogerty(item.get(j).getBprogerty())
                                    .setCbookType(item.get(j).getCbookType())
                                    .setUniqueCode(newCode)
                                    .setBend(item.get(j).getBend())
                                    .setFlag(item.get(j).getFlag())
                                    .setLowerControl("0")
                                    .setFuzhuControl("0")
                                    .setBperson("0")
                                    .setBcus("0")
                                    .setBsup("0")
                                    .setBdept("0")
                                    .setBitem("0")
                                    .setCassItem("0")
                                    .setBnum("0")
                                    .setBstock("0")
                                    .setBcash("0")
                                    .setBbank("0")
                                    .setBdaybook("0")
                                    .setCurrency("0")
                                    .setBcashEquivalence("0")
                                    .setControlled("0")
                                    .setPxjz("0")
                                    .setXjll("0")
                                    .setCyfx("0")
                                    .setYusuan("0")
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
                                    .setCdfine30("0")
                                    .setSuperiorCcode(item.get(j).getSuperiorCcode());
                            list.add(codekm);
                        } else {
                            GroupCodeKemu codekm = new GroupCodeKemu();
                            codekm
                                    .setUniqueAccStandard(uniqueAccStandard)
                                    .setTemplateId(id)
                                    .setCclass(item.get(j).getCclass())
                                    .setCcode(item.get(j).getCcode())
                                    .setCcodeName(item.get(j).getCcodeName())
                                    .setIgrade(item.get(j).getIgrade())
                                    .setBprogerty(item.get(j).getBprogerty())
                                    .setCbookType(item.get(j).getCbookType())
                                    .setUniqueCode(item.get(j).getUniqueCode())
                                    .setBend(item.get(j).getBend())
                                    .setFlag(item.get(j).getFlag())
                                    .setLowerControl("0")
                                    .setFuzhuControl("0")
                                    .setBperson("0")
                                    .setBcus("0")
                                    .setBsup("0")
                                    .setBdept("0")
                                    .setBitem("0")
                                    .setCassItem("0")
                                    .setBnum("0")
                                    .setBstock("0")
                                    .setBcash("0")
                                    .setBbank("0")
                                    .setBdaybook("0")
                                    .setCurrency("0")
                                    .setBcashEquivalence("0")
                                    .setControlled("0")
                                    .setPxjz("0")
                                    .setXjll("0")
                                    .setCyfx("0")
                                    .setYusuan("0")
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
                                    .setCdfine30("0")
                                    .setSuperiorCcode(item.get(j).getSuperiorCcode());
                            list.add(codekm);
                        }
                }
            }
        }

        if(!test.equals("")){
            for (int i = 0; i < test.split(",").length; i++) {
                int finalI = i;
                String finalTest = test;
                // 用旧编码查下级
                List<GroupCodeKemu> list1 = list.stream().filter(km -> km.getSuperiorCcode().equals(finalTest.split(",")[finalI].split("_")[0])).collect(Collectors.toList());

                if(list1.size()>0){
                    for (int j = 0; j < list1.size(); j++) {
                        String codeEndWith=list1.get(j).getCcode().substring(test.split(",")[i].split("_")[0].length(),list1.get(j).getCcode().length());
                        list1.get(j).setSuperiorCcode(test.split(",")[i].split("_")[1]).setCcode(test.split(",")[i].split("_")[1]+codeEndWith).setUniqueCode(test.split(",")[i].split("_")[1]+codeEndWith);
                    }
                }
            }
        }
        return list;
    }

    public List<GroupCodeKemuOrg> testOrg(String id, String uniqueAccStandard,String jici,List<GroupCodeKemu> item,String iyear,String orgUnique){
        List<GroupCodeKemuOrg> list = new ArrayList<>();
        int a = 0;  // 科目编码长度
        int ai = 0; // 科目级次
        a = 0;  // 科目编码长度
        ai = 0; // 科目级次
        String test="";//  旧编码_新编码
        for (int i = 0; i < jici.split("-").length; i++) {
            ai = i + 1;
            a += Integer.valueOf(jici.split("-")[i]);
            for (int j = 0; j < item.size(); j++) {
                // 循环级次
                if (ai == Integer.valueOf(item.get(j).getIgrade())) {
                    if(item.get(j).getCcode().length() > 4) {
                        String superCode = item.get(j).getSuperiorCcode();
                        String codeEnd = item.get(j).getCcode().substring(superCode.length());

                        String newCode = "";
                        if (Integer.valueOf(jici.split("-")[i]) != codeEnd.length()) {
                            // 补0后总位数
                            newCode = superCode + String.format("%0" + Integer.valueOf(jici.split("-")[i]) + "d", Integer.valueOf(codeEnd));
                            test += item.get(j).getCcode() + "_" + newCode + ",";
                        }

                        GroupCodeKemuOrg codekm = new GroupCodeKemuOrg();
                        codekm
                                .setIyear(iyear)
                                .setUniqueAccStandard(uniqueAccStandard)
                                .setTemplateId(id)
                                .setOrgUnique(orgUnique)
                                .setCclass(item.get(j).getCclass())
                                .setCcode(newCode.equals("") ? item.get(j).getCcode() : newCode)
                                .setCcodeName(item.get(j).getCcodeName())
                                .setIgrade(item.get(j).getIgrade())
                                .setBprogerty(item.get(j).getBprogerty())
                                .setCbookType(item.get(j).getCbookType())
                                .setUniqueCode(newCode)
                                .setBend(item.get(j).getBend())
                                .setFlag(item.get(j).getFlag())
                                .setLowerControl("0")
                                .setFuzhuControl("0")
                                .setBperson("0")
                                .setBcus("0")
                                .setBsup("0")
                                .setBdept("0")
                                .setBitem("0")
                                .setCassItem("0")
                                .setBnum("0")
                                .setBstock("0")
                                .setBcash("0")
                                .setBbank("0")
                                .setBdaybook("0")
                                .setCurrency("0")
                                .setBcashEquivalence("0")
                                .setControlled("0")
                                .setPxjz("0")
                                .setXjll("0")
                                .setCyfx("0")
                                .setYusuan("0")
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
                                .setCdfine30("0")
                                .setSuperiorCcode(item.get(j).getSuperiorCcode());
                        list.add(codekm);
                    } else {
                        GroupCodeKemuOrg codekm = new GroupCodeKemuOrg();
                        codekm
                                .setIyear(iyear)
                                .setUniqueAccStandard(uniqueAccStandard)
                                .setTemplateId(id)
                                .setOrgUnique(orgUnique)
                                .setCclass(item.get(j).getCclass())
                                .setCcode(item.get(j).getCcode())
                                .setCcodeName(item.get(j).getCcodeName())
                                .setIgrade(item.get(j).getIgrade())
                                .setBprogerty(item.get(j).getBprogerty())
                                .setCbookType(item.get(j).getCbookType())
                                .setUniqueCode(item.get(j).getUniqueCode())
                                .setBend(item.get(j).getBend())
                                .setFlag(item.get(j).getFlag())
                                .setLowerControl("0")
                                .setFuzhuControl("0")
                                .setBperson("0")
                                .setBcus("0")
                                .setBsup("0")
                                .setBdept("0")
                                .setBitem("0")
                                .setCassItem("0")
                                .setBnum("0")
                                .setBstock("0")
                                .setBcash("0")
                                .setBbank("0")
                                .setBdaybook("0")
                                .setCurrency("0")
                                .setBcashEquivalence("0")
                                .setControlled("0")
                                .setPxjz("0")
                                .setXjll("0")
                                .setCyfx("0")
                                .setYusuan("0")
                                .setYsBmzcjjfl("0")
                                .setYsYsly("0")
                                .setYsZcgnfl("0")
                                .setYsZfzcjjfl("0")
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
                                .setCdfine30("0")
                                .setSuperiorCcode(item.get(j).getSuperiorCcode());
                        list.add(codekm);
                    }
                }
            }
        }

        if(!test.equals("")){
            for (int i = 0; i < test.split(",").length; i++) {
                int finalI = i;
                String finalTest = test;
                // 用旧编码查下级
                List<GroupCodeKemuOrg> list1 = list.stream().filter(km -> km.getSuperiorCcode().equals(finalTest.split(",")[finalI].split("_")[0])).collect(Collectors.toList());

                if(list1.size()>0){
                    for (int j = 0; j < list1.size(); j++) {
                        String codeEndWith=list1.get(j).getCcode().substring(test.split(",")[i].split("_")[0].length(),list1.get(j).getCcode().length());
                        list1.get(j).setSuperiorCcode(test.split(",")[i].split("_")[1]).setCcode(test.split(",")[i].split("_")[1]+codeEndWith).setUniqueCode(test.split(",")[i].split("_")[1]+codeEndWith);
                    }
                }
            }
        }
        return list;
    }

    // 集团从国家科目表中获取科目
    public Mono<Map> save(String id, String uniqueAccStandard, String templateId, String jici) {
        Map map = new HashMap<>();
        List<GroupCodeKemu> list = new ArrayList<>();
        return groupCodeKemuCountryRepository.findAllByUniqueAccStandardAndTemplateIdOrderByCcodeAsc(uniqueAccStandard, templateId)
                .collectList()
                .map(item -> {
                    map.put("code", "200");
                    map.put("msg", "");
                    map.put("templateId", String.valueOf(id));

                    int a = 0;  // 科目编码长度
                    int ai = 0; // 科目级次
                    for (int i = 0; i < jici.split("-").length; i++) {
                        ai = i + 1;
                        a += Integer.valueOf(jici.split("-")[i]);
                        boolean error = false; // for循环结束
                        for (int j = 0; j < item.size(); j++) {
                            // 循环级次
                            if (ai == Integer.valueOf(item.get(j).getIgrade())) {
                                if (a < Integer.valueOf(item.get(j).getCcode().length())) {
                                    map.put("code", "400");
                                    map.put("msg", "您设置的科目级次小于系统模板科目级次，无法继续生成数据");
                                    error = true;
                                    break;
                                }
                            }
                        }
                        if (error) break;
                    }
                    return map.get("code").equals("200")?test(id, uniqueAccStandard, jici, item):list;
                })
                .flatMapMany(codeKemuRepository::saveAll)
                .collectList().map(o -> map);
    }

    // 组织科目表中获取科目
    public Mono<Map> findByZZCodeKeMuAndsave(String id, String uniqueAccStandard, String templateId, String jici,String iyear,String orgUnique) {
        Map map = new HashMap<>();
        List<GroupCodeKemuOrg> list = new ArrayList<>();
        return codeKemuRepository.findAllByUniqueAccStandardAndTemplateIdOrderByCcodeAsc(uniqueAccStandard, templateId)
                .collectList()
                .map(item -> {
                    map.put("code", "200");
                    map.put("msg", "");
                    map.put("templateId", String.valueOf(id));

//                    item= item.stream().filter(v -> StrUtil.isNotBlank(v.getIyear())&& v.getIyear().equals(iyear)).collect(Collectors.toList());
                    int a = 0;  // 科目编码长度
                    int ai = 0; // 科目级次
                    for (int i = 0; i < jici.split("-").length; i++) {
                        ai = i + 1;
                        a += Integer.valueOf(jici.split("-")[i]);
                        boolean error = false; // for循环结束
                        for (int j = 0; j < item.size(); j++) {
                            // 循环级次
                            if (ai == Integer.valueOf(item.get(j).getIgrade())) {
                                if (a < Integer.valueOf(item.get(j).getCcode().length())) {
                                    map.put("code", "400");
                                    map.put("msg", "您设置的科目级次小于系统模板科目级次，无法继续生成数据");
                                    error = true;
                                    break;
                                }
                            }
                        }
                        if (error) break;
                    }
                    return map.get("code").equals("200")?testOrg(id, uniqueAccStandard, jici, item,iyear,orgUnique):list;
                })
                .flatMapMany(groupCodeKemuOrgRepository::saveAll)
                .collectList().map(o -> map);
    }
}

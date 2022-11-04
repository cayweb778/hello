package org.boozsoft.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import org.boozsoft.domain.entity.codekemu.CodeKemu;
import org.boozsoft.domain.entity.group.GroupSysAccount;
import org.boozsoft.repo.*;
import org.boozsoft.util.RandomNum;
import org.springbooz.core.tool.utils.StringUtils;
import org.boozsoft.domain.entity.Accvoucher;
import org.boozsoft.domain.entity.SysAccvoucherTemplate;
import org.boozsoft.domain.entity.SysPeriod;
import org.boozsoft.domain.entity.SysVoucherImportEntry;
import org.boozsoft.repo.codekemu.CodeKemuRepository;
import org.boozsoft.service.AccvoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoExtensionsKt;
import reactor.util.function.Tuple2;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author LZ
 * @version 1.0
 * @title 操作人员
 * @company 财税达软件科技
 * @date 2021/4/1 10:47 上午
 */
@Service
public class AccvoucherServiceImpl implements AccvoucherService {
    @Autowired
    AccvoucherRepository accvoucherRepository;
    @Autowired
    SysVoucherImportEntryRepository voucherImportEntryRepository;

    @Autowired
    CodeKemuRepository codeKemuRepository;

    @Autowired
    IperiodRepository iperiodRepository;

    @Autowired
    SysPeriodRepository sysPeriodRepository;

    @Autowired
    GroupSysAccountRepository sysAccountRepository;

    @Autowired
    AccCloseRepository closeRepository;

    public Mono<List<Accvoucher>> queryAllYaerAndMonthMaxInoid() {
        return accvoucherRepository.queryAllYaerAndMonthMaxInoid().collectList()
                .map(a -> a);
    }
    public Mono<String> queryMaxInoid() {
        return accvoucherRepository.queryMaxInoid()
                .flatMap(item -> {
                    String coutnoId = item.getInoId();
                    if (StringUtils.isBlank(coutnoId)) {
                        coutnoId = "0000";
                    } else {
                        coutnoId = item.getInoId();
                        if ((Integer.valueOf(coutnoId) + 1) < 10) {
                            coutnoId = "000" + (Integer.valueOf(coutnoId) + 1);
                        } else if ((Integer.valueOf(coutnoId) + 1) >= 10 && (Integer.valueOf(coutnoId) + 1) < 100) {
                            coutnoId = "00" + (Integer.valueOf(coutnoId) + 1);
                        } else if ((Integer.valueOf(coutnoId) + 1) >= 100 && (Integer.valueOf(coutnoId) + 1) < 1000) {
                            coutnoId = "0" + (Integer.valueOf(coutnoId) + 1);
                        }
                    }
                    return Mono.just(coutnoId);
                })
                .map(a -> a);
    }

    /**
     * 根据属性名称 通过反射 获取对象的属性值
     *
     * @param fieldName 属性名称
     * @param object    所属对象
     * @return
     */
    private String getFieldValueByFieldName(String fieldName, Object object) {
        try {
            if (fieldName.contains("_")) {
                fieldName = lineToHump(fieldName);
            }
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + firstLetter + fieldName.substring(1);
            Method method = object.getClass().getMethod(getter, new Class[]{});
            Object value = method.invoke(object, new Object[]{});
            return value.toString();
        } catch (Exception e) {
            return null;
        }
    }

    private String setFieldValueByFieldName(String fieldName,String fieldValue, Object object) {
        try {
            if (fieldName.contains("_")) {
                fieldName = lineToHump(fieldName);
            }
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String setter = "set" + firstLetter + fieldName.substring(1);
            Method method = object.getClass().getMethod(setter, new Class[]{});
            Object value = method.invoke(object, fieldValue);
            return value.toString();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 下划线转驼峰
     */
    private String lineToHump(String str) {
        Pattern linePattern = Pattern.compile("_(\\w)");
        str = str.toLowerCase();
        Matcher matcher = linePattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 获取指定模板的表名
     *
     * @param temp
     * @param type
     * @return
     */
    public Mono<String[]> getThisTemplateTitles(SysAccvoucherTemplate temp, String type) {
        String defaultJSON = "[{colPropertyName:formDate,colName:制单日期},{colPropertyName:csign,colName:凭证类型},{colPropertyName:inoId,colName:凭证号},{colPropertyName:cdigest,colName:凭证摘要},{colPropertyName:ccode,colName:科目编码},{colPropertyName:ccodeName,colName:科目名称},{colPropertyName:md,colName:借方金额},{colPropertyName:mc,colName:贷方金额},{colPropertyName:cbill,colName:制单人},{colPropertyName:ccheck,colName:审核人},{colPropertyName:cbook,colName:记账人},{colPropertyName:cashier,colName:出纳},{colPropertyName:checkDate,colName:审核日期},{colPropertyName:year,colName:年度},{colPropertyName:settlementMethod,colName:结算方式名称}]";
        String standardJSON = "[{colPropertyName:unitMeasurement,colName:计算单位},{colPropertyName:ndS,colName:借方数量},{colPropertyName:ncS,colName:贷方数量},{colPropertyName:foreignCurrency,colName:外币币种},{colPropertyName:foreignAmount,colName:外币金额},{colPropertyName:unitPrice,colName:单价},{colPropertyName:cdeptId,colName:部门编码},{colPropertyName:cpersonId,colName:个人编码},{colPropertyName:ccusId,colName:客户编码},{colPropertyName:csupId,colName:供应商编码},{colPropertyName:projectClassId,colName:项目大类编码},{colPropertyName:projectId,colName:项目编码}]";
        List<Map> defaultList = (List<Map>) JSON.parseObject(defaultJSON, HashMap.class);
        List<Map> standardList = (List<Map>) JSON.parseObject(standardJSON, HashMap.class);
        ArrayList<String> list = new ArrayList<>();
        defaultList.forEach(map -> {
            String titleName = "";
            String colPropertyValue = getFieldValueByFieldName(map.get("colPropertyName").toString(), temp);
            if (StringUtils.isNotBlank(colPropertyValue)) { // 存在替换
                titleName = colPropertyValue;
            } else {//默认
                titleName = map.get("colName").toString();
            }
            list.add(titleName);
        });

        if (temp.getTemplateType().equals("1")) { //标准
            standardList.forEach(map -> {
                String titleName = "";
                String colPropertyValue = getFieldValueByFieldName(map.get("colPropertyName").toString(), temp);
                if (StringUtils.isNotBlank(colPropertyValue)) { // 存在替换
                    titleName = colPropertyValue;
                } else {//默认
                    titleName = map.get("colName").toString();
                    if (type.equals("1") && map.get("colName").toString().lastIndexOf("编码") != -1) { // 名称
                        titleName = titleName.replace("编码", "名称");
                    }
                }
                list.add(titleName);
            });
        }
        return Mono.just(list.toArray(new String[list.size()]));
    }

    public Mono<Tuple2<List<String>, List<String>>> getTheHeaderOfTheCurrentlyImportedFile(String unCode, String type, String type2) {
        AtomicReference<List<String>> fields = new AtomicReference();
        AtomicReference<List<String>> fields2 = new AtomicReference();
        Flux<SysVoucherImportEntry> dbList = voucherImportEntryRepository.findAllByUniqueCodeOrderById(unCode);
        Mono<List<String>> client = Mono.just(type2) // 用户自定义字段
                .flatMap(
                        typeV ->
                                dbList.map(entry -> {
                                    String titleName = org.apache.commons.lang3.StringUtils.isNotBlank(entry.getCustomerFieldName()) ? entry.getCustomerFieldName() : entry.getSystemFieldName();
                                    checkValue(fields, titleName,typeV.equals("1"));
                                    return entry;
                                }).then(Mono.just("").map(a -> fields.get()))
                );
        Mono<List<String>> system = Mono.just(type2) // 默认系统字段
                .flatMap(
                        typeV ->
                                dbList.map(entry ->{
                                             if (null == fields2.get()) fields2.set(new ArrayList<String>());
                                             fields2.get().add(entry.getSystemFieldName());
                                             return entry;
                                        }
                                ).then(Mono.just("").map(a -> fields2.get()))
                );

        return Mono.zip(client, system);
    }

    /**
     * 校验用户是否自定义字段
     * @param fields
     * @param titleName
     */
    public static void checkValue(AtomicReference<List<String>> fields, String titleName,boolean isName ) {
        if (isName && titleName.endsWith("编码") && (!titleName.equals("项目大类编码")) && (titleName.startsWith("部门") || titleName.startsWith("个人") || titleName.startsWith("职员") || titleName.startsWith("客户")
                || titleName.startsWith("部门") || titleName.startsWith("项目") || titleName.startsWith("供应商")))
            titleName = titleName.replaceFirst("编码", "名称");
        if (null == fields.get()) fields.set(new ArrayList<>());
        fields.get().add(titleName);
    }

    /**
     * 获取指定模板的属性名称
     *
     * @return
     */
    public String[] getThisTemplatePropertys() {
        String defaultJSON = "[{colPropertyName:formDate,colName:制单日期},{colPropertyName:csign,colName:凭证类型},{colPropertyName:inoId,colName:凭证号},{colPropertyName:cdigest,colName:凭证摘要},{colPropertyName:ccode,colName:科目编码},{colPropertyName:ccodeName,colName:科目名称},{colPropertyName:md,colName:借方金额},{colPropertyName:mc,colName:贷方金额},{colPropertyName:cbill,colName:制单人},{colPropertyName:ccheck,colName:审核人},{colPropertyName:cbook,colName:记账人},{colPropertyName:cashier,colName:出纳},{colPropertyName:checkDate,colName:审核日期},{colPropertyName:year,colName:年度},{colPropertyName:settlementMethod,colName:结算方式名称}]";
        String standardJSON = "[{colPropertyName:unitMeasurement,colName:计算单位},{colPropertyName:ndS,colName:借方数量},{colPropertyName:ncS,colName:贷方数量},{colPropertyName:foreignCurrency,colName:外币币种},{colPropertyName:foreignAmount,colName:外币金额},{colPropertyName:unitPrice,colName:单价},{colPropertyName:cdeptId,colName:部门编码},{colPropertyName:cpersonId,colName:个人编码},{colPropertyName:ccusId,colName:客户编码},{colPropertyName:csupId,colName:供应商编码},{colPropertyName:projectClassId,colName:项目大类编码},{colPropertyName:projectId,colName:项目编码}]";
        List<Map> defaultList = (List<Map>) JSON.parseObject(defaultJSON, HashMap.class);
        List<Map> standardList = (List<Map>) JSON.parseObject(standardJSON, HashMap.class);
        ArrayList<String> list = new ArrayList<>();
        defaultList.forEach(map -> {
            list.add(map.get("colPropertyName").toString());
        });
        standardList.forEach(map -> {
            list.add(map.get("colPropertyName").toString());
        });
        return list.toArray(new String[list.size()]);
    }

    // 年月-凭证号
    @Override
    public Mono<String[]> checkPingZhengDbRepeatNumber(List<Map<Object, Object>> list) {
        return Mono.just("").map(item->{
            Map<String, Set<String>> map = new HashMap<>();
            for (Map<Object, Object> objMap : list) {
                Collection<Object> values = objMap.values();// time
                Set<String> valueList  = new HashSet<>();
                for (Object object : values) {
                    String key = object.toString().split(">>")[0].substring(0, 7);
                    if (null != map.get(key)){
                        valueList = map.get(key);
                    }
                    valueList.add(object.toString().split(">>")[1].toString());
                    map.put(key,valueList);
                }
            }
            return map;
        }).flatMap(map ->
              Flux.fromIterable(map.keySet()).flatMap(key->{
               Set<String> numbers = map.get(key);
               return Mono.just(new ArrayList<>(numbers))
                        .flatMap(listV -> accvoucherRepository.findAllByIyperiodAndInoIdIn(key.replaceAll("-", ""), listV)
                                .collectList().map(entirys -> {
                                    String[] strings = new String[entirys.size()];
                                    for (int i = 0; i < entirys.size(); i++) {
                                        strings[i] = key+":"+entirys.get(i).getInoId();
                                    }
                                    return strings;
                                }));
            }).collectList()
              .map(arrs -> {
                  List<String> objects = new ArrayList<>();
                  for (String[] strings : arrs) {
                      CollUtil.addAll(objects,strings);
                  }
                  return objects.toArray(new String[0]);
              }));
    }

    /**
     * 凭证实体类属性注入
     * @param accvoucher 实体类
     * @param objects 被注入的数据
     * @param keys 属性名称集合
     * @param indexs 数据下标
     * @return
     */
    @Override
    public Accvoucher modifyPingZhengEntityPropertyByAuxiliaryItem(Accvoucher accvoucher, Object[] objects, List<Integer> keys, List<Integer> indexs) {
        for (int i = 0; i < keys.size(); i++) {
            String name = "cdfine"+keys.get(i);
            String value = objects[indexs.get(i)].toString();
            try {
                Field field = accvoucher.getClass().getDeclaredField(name);
                field.setAccessible(true);
                field.set(accvoucher,value);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return accvoucher;
    }


    @Override
    public Mono<String[]> checkPingZhengYearIsClose(List<Map<Object, Object>> list) {
        return Mono.just("").flatMap(item -> {
            Set<String> yearList = new HashSet<>();
            Set<String> monthList = new HashSet<>();
            for (Map<Object, Object> objMap : list) {
                Collection<Object> values = objMap.values();// time
                for (Object object : values) {
                    //valueList.add(object.toString().split(">>")[0].substring(0, 7)+"-01");
                    yearList.add(object.toString().split(">>")[0].substring(0, 4));
                    monthList.add(object.toString().split(">>")[0].substring(5, 7));
                }
            }
            return Mono.just(new ArrayList<>())
                    .flatMap(
                            //list1 -> iperiodRepository.findAllByFlagAndDateStartIn("1", list1).collectList()
                            list1 ->  closeRepository.findAllByGlCloseAndCondition(yearList,monthList).collectList()
                    ).flatMap(list1->
                            Mono.just(list1.stream().map(accClose -> accClose.getIyear()+'-'+accClose.getImonth()).
                                    collect(Collectors.toList()).toArray(new String[0]))
                    );
                        /*.map(list1 -> {
                        list1.toArray()
                        return list1.toArray(new String[0]);
                    });*/
        });
    }

    @Override
    
    public Mono<Map<String, Object>> getAllKuaiJiQiJianInfoByAccId(String accId) {
        return sysPeriodRepository.findAllByAccountIdAndBeiyong1NullOrderByIyearAscDateStartAsc(accId).collectList().map(list->assemblyMap(list));
    }

    @Override
    public Mono<GroupSysAccount> queryAccountByAccId(String accId) {
        return sysAccountRepository.findFirstByAccIdOrderByAccId(accId);
    }

    private Map<String,Object> assemblyMap(List<SysPeriod> list) {
        Map<String, Object> map = new HashMap<>();
        for (SysPeriod period : list) {
            if (map.containsKey(period.getIyear())){
                ((HashMap<String, String>)map.get(period.getIyear())).put(period.getDateStart().substring(0,2),period.getIperiodNum());
            }else {
                HashMap<String, String> childMap = new HashMap<>();
                childMap.put(period.getDateStart().substring(0,2),period.getIperiodNum());
                map.put(period.getIyear(),childMap);
            }
        }
        map.put("list",list);
        return map;
    }

    public Mono editOrDel_AccVoucherOrQc(String ccode, List<Map<String, Object>> listmap, CodeKemu km) {
        return accvoucherRepository.findAll().collectList()
            .flatMap(list->{
                List<Accvoucher> findByCcode = list.stream().filter(v ->StringUtils.isNotBlank(v.getCcode()) && v.getCcode().equals(ccode)).collect(Collectors.toList());
                if(listmap==null){
                    return Mono.just("");
                }
                String deptchecked =listmap.get(0).get("deptchecked").toString();
                String cuschecked =listmap.get(1).get("cuschecked").toString();
                String supchecked =listmap.get(2).get("supchecked").toString();
                String psnchecked =listmap.get(3).get("psnchecked").toString();
                String itemchecked =listmap.get(4).get("itemchecked").toString();

                String deptvalue =listmap.get(0).get("deptvalue").toString();
                String cusvalue =listmap.get(1).get("cusvalue").toString();
                String supvalue =listmap.get(2).get("supvalue").toString();
                String psnvalue =listmap.get(3).get("psnvalue").toString();
                String itemvalue =StringUtils.isNotBlank(listmap.get(4).get("itemvalue").toString())?listmap.get(4).get("itemvalue").toString().split("_")[0]:"";
                String itemclassvalue =StringUtils.isNotBlank(listmap.get(4).get("itemvalue").toString())?listmap.get(4).get("itemvalue").toString().split("_")[1]:"";

                // 增加一条辅助期初
                List<Accvoucher> fulist=new ArrayList<>();
                // 有辅助期初，替换辅助项
                List<Accvoucher> findByCcodeFzQc = list.stream().filter(v ->StringUtils.isNotBlank(v.getCcode()) && v.getCcode().equals(ccode) && v.getImonth().equals("21")).collect(Collectors.toList());
                if(findByCcodeFzQc.size()>0){
                    findByCcodeFzQc.forEach(v->{
                        v.setCdeptId(deptchecked.equals("true")?deptvalue:v.getCdeptId())
                        .setCcusId(cuschecked.equals("true")?cusvalue:v.getCcusId())
                        .setCsupId(supchecked.equals("true")?supvalue:v.getCsupId())
                        .setCpersonId(psnchecked.equals("true")?psnvalue:v.getCpersonId())
                        .setProjectId(itemchecked.equals("true")?itemvalue:v.getProjectId())
                        .setProjectClassId(itemchecked.equals("true")?itemclassvalue:v.getProjectClassId());
                    });
                }
                else{
                    // 没有辅助期初，增加一条
                    List<Accvoucher> findByCcodeQc = list.stream().filter(v ->StringUtils.isNotBlank(v.getCcode()) && v.getCcode().equals(ccode) && v.getImonth().equals("00")).collect(Collectors.toList());
                    if(findByCcodeQc.size()>0){
                        Accvoucher v=findByCcodeQc.get(0);
                        int year=Integer.valueOf(v.getIyear())-1;

                                v.setId(null)
                                .setUniqueCode(RandomNum.uuid())
                                .setImonth("21")
                                .setIyperiod(v.getIyperiod().substring(0,v.getIyperiod().length()-2)+"21")
                                .setDbillDate((year)+"-12-31")
                                .setCdeptId(deptchecked.equals("true")?deptvalue:v.getCdeptId())
                                .setCcusId(cuschecked.equals("true")?cusvalue:v.getCcusId())
                                .setCsupId(supchecked.equals("true")?supvalue:v.getCsupId())
                                .setCpersonId(psnchecked.equals("true")?psnvalue:v.getCpersonId())
                                .setProjectId(itemchecked.equals("true")?itemvalue:v.getProjectId())
                                .setProjectClassId(itemchecked.equals("true")?itemclassvalue:v.getProjectClassId());
                        fulist.add(v);
                    }
                }

                // 获取凭证
                List<Accvoucher> findByAccVoucherCcode = findByCcode.stream().filter(v -> !v.getImonth().equals("00") && !v.getImonth().equals("20") && !v.getImonth().equals("21")).collect(Collectors.toList());
                // 修改凭证辅助项
                findByAccVoucherCcode.forEach(v->{
                    v.setCdeptId(deptchecked.equals("true")?deptvalue:v.getCdeptId())
                            .setCcusId(cuschecked.equals("true")?cusvalue:v.getCcusId())
                            .setCsupId(supchecked.equals("true")?supvalue:v.getCsupId())
                            .setCpersonId(psnchecked.equals("true")?psnvalue:v.getCpersonId())
                            .setProjectId(itemchecked.equals("true")?itemvalue:v.getProjectId())
                            .setProjectClassId(itemchecked.equals("true")?itemclassvalue:v.getProjectClassId())
                            .setCdfine1(km.getCdfine1())
                            .setCdfine2(km.getCdfine2())
                            .setCdfine3(km.getCdfine3())
                            .setCdfine4(km.getCdfine4())
                            .setCdfine5(km.getCdfine5())
                            .setCdfine6(km.getCdfine6())
                            .setCdfine7(km.getCdfine7())
                            .setCdfine8(km.getCdfine8())
                            .setCdfine9(km.getCdfine9())
                            .setCdfine10(km.getCdfine10())
                            .setCdfine11(km.getCdfine11())
                            .setCdfine12(km.getCdfine12())
                            .setCdfine13(km.getCdfine13())
                            .setCdfine14(km.getCdfine14())
                            .setCdfine15(km.getCdfine15())
                            .setCdfine16(km.getCdfine16())
                            .setCdfine17(km.getCdfine17())
                            .setCdfine18(km.getCdfine18())
                            .setCdfine19(km.getCdfine19())
                            .setCdfine20(km.getCdfine20())
                            .setCdfine21(km.getCdfine21())
                            .setCdfine22(km.getCdfine22())
                            .setCdfine23(km.getCdfine23())
                            .setCdfine24(km.getCdfine24())
                            .setCdfine25(km.getCdfine25())
                            .setCdfine26(km.getCdfine26())
                            .setCdfine27(km.getCdfine27())
                            .setCdfine28(km.getCdfine28())
                            .setCdfine29(km.getCdfine29())
                            .setCdfine30(km.getCdfine3());
                });
                return accvoucherRepository.saveAll(findByAccVoucherCcode).then(accvoucherRepository.saveAll(fulist).then(accvoucherRepository.saveAll(findByCcodeFzQc).then(Mono.just(""))));
            }).then();
    }
}

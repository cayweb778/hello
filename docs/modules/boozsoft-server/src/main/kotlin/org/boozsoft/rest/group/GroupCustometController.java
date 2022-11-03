package org.boozsoft.rest.group;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.boozsoft.domain.entity.SysCountry;
import org.boozsoft.domain.entity.SysZone;
import org.boozsoft.domain.entity.group.GroupCustomer;
import org.boozsoft.domain.entity.group.GroupCustomerClass;
import org.boozsoft.domain.entity.group.GroupFileEncodingRules;
import org.boozsoft.domain.entity.group.GroupSupplier;
import org.boozsoft.domain.entity.origin.OrgCustomer;
import org.boozsoft.domain.entity.origin.OrgCustomerClass;
import org.boozsoft.domain.entity.origin.OrgSupplier;
import org.boozsoft.domain.vo.CustomerVo;
import org.boozsoft.domain.vo.GroupCustomerVo;
import org.boozsoft.repo.*;
import org.boozsoft.repo.group.GroupAllotRecordRepository;
import org.boozsoft.repo.group.GroupCustomerAccountRepository;
import org.boozsoft.repo.group.GroupFileEncodingRulesRepository;
import org.boozsoft.repo.origin.OriginCustomerClassRepository;
import org.boozsoft.repo.origin.OriginCustomerRepository;
import org.boozsoft.repo.origin.OriginSupplierRepository;
import org.boozsoft.service.GroupCustomerService;
import org.boozsoft.service.SysZoneService;
import org.boozsoft.util.XlsUtils3;
import org.springbooz.core.tool.result.R;
import org.springbooz.core.tool.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.lang.reflect.Method;
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
 * @ClassName : CustometController
 * @Description : 客户信息
 * @Author : miao
 * @Date: 2021-04-01 09:25
 */
@Slf4j
@RestController
@RequestMapping("/sys/cusrtomer_group")
@Api(value = "集团客户信息", tags = "API系统：集团客户信息")
public class GroupCustometController {
    @Autowired
    GroupCustomerRepository customerRepository;
    @Autowired
    CountryRepository countryRepository;
    @Autowired
    GroupCustomerService service;
    @Autowired
    SysZoneService zoneService;
    @Autowired
    GroupCustomerClassRepository customerClassRepository;
    @Autowired
    SupplierRepository supplierRepository;
    @Autowired
    GroupSupplierRepository groupSupplierRepository;
    @Autowired
    SysZoneRepository sysZoneRepository;
    @Autowired
    GroupAllotRecordRepository groupAllotRecordRepository;
    @Autowired
    GroupFileEncodingRulesRepository groupFileEncodingRulesRepository;
    @Autowired
    GroupCustomerAccountRepository groupCustomerAccountRepository;
    @Autowired
    OriginCustomerClassRepository originCustomerClassRepository;
    @Autowired
    SysTradeNatureRepository sysTradeNatureRepository;
    @Autowired
    GroupDistRepository groupDistRepository;
    @Autowired
    OriginSupplierRepository originSupplierRepository;
    @Autowired
    OriginCustomerRepository originCustomerRepository;
    @Autowired
    DatabaseClient client;




    /**
     * 查询  集团档案分配管控权限表
     *
     * @return
     */
    @PostMapping("findByDatabaseUniqueCode")
    public Mono<R> findByDatabaseUniqueCode() {
        return groupDistRepository.findByDatabaseUniqueCodeAndTableName("abc001", "customer").map(o -> R.ok().setResult(o));
    }

    @PostMapping("verifyCustomerName")
    public Mono<R> verifyCustomerName(String name) {
        return customerRepository.countByCustName(name).map(o -> R.ok().setResult(o));
    }

    @PostMapping("verifyCustomerNum")
    public Mono<R> verifyCustomerNum(String num) {
        return customerRepository.countByCustCode(num).map(o -> R.ok().setResult(o));
    }

    @PostMapping("verifyByCustAbbname")
    public Mono<R> verifyByCustAbbname(String custAbbname) {
        return customerRepository.countByCustAbbname(custAbbname).map(o -> R.ok().setResult(o)).defaultIfEmpty(R.ok().setResult(""));
    }
    @PostMapping("findByCustAbbnameAndCustName")
    public Mono<R> findByCustAbbnameAndCustName(String custAbbname,String custName) {
        return customerRepository.findByCustAbbnameAndCustName(custAbbname,custName).map(o -> R.ok().setResult(o)).defaultIfEmpty(R.ok().setResult(""));
    }

    @PostMapping("verifyByCustSregcode")
    public Mono<R> verifyByCustSregcode(String custSregcode) {
        return customerRepository.countByCustSregcode(custSregcode).map(o -> R.ok().setResult(o));
    }

    @PostMapping("verifyCustAccount")
    public Mono<R> verifyCustAccount(String bankNum) {
        return customerRepository.countByCustAccount(bankNum).map(o -> R.ok().setResult(o));
    }

    @PostMapping("findByCustAbbname")
    public Mono<R> findByCustAbbname(String custAbbname) {
        return customerRepository.findByCustAbbname(custAbbname).map(o -> R.ok().setResult(o));
    }
    @PostMapping("findByCustName")
    public Mono<R> findByCustName(String custName) {
        return customerRepository.findByCustName(custName).map(o -> R.ok().setResult(o)).defaultIfEmpty(R.ok().setResult(""));
    }


    /**
     * 获取国家档案
     *
     * @return
     */
    @GetMapping("findAllCountry")
    public Mono<R> findAllCountry() {
        return countryRepository.findAllOrderByNum().collectList().map(R::page);
    }

    /**
     * 获取行业性质信息
     *
     * @return
     */
    @GetMapping("findAllSysTradeNature")
    public Mono<R> findAllSysTradeNature() {
        return sysTradeNatureRepository.findAll().collectList().map(R::page);
    }

    @PostMapping("findAll")
    public Mono<R> findAll(@RequestBody Map map, Pageable pageable) {
        Map<String, String> searchMap = ((HashMap<String,  String>) map.get("searchConditon"));
        String flag=map.get("flag").toString();
        String uniqueCustclass=map.get("uniqueCustclass").toString();
        // 当前页
        int page= Integer.parseInt(map.get("page").toString());
        // 页多少条
        int size= Integer.parseInt(map.get("size").toString());
        AtomicReference<Long> totalAR = new AtomicReference(0);
        return service.findAll().collectList()
                .flatMap(item->{
                    long total=item.size();
                    List<CustomerVo> list=item.stream().skip((page - 1) * size).limit(size).collect(Collectors.toList());
                    if(StringUtils.isNotBlank(flag)){
                        list=item.stream().filter(v->v.getFlag().equals(flag)).collect(Collectors.toList());
                        total=list.size();
                    }
                    if(!"0".equals(uniqueCustclass)){
                        list=item.stream().filter(v->StringUtils.isNotBlank(v.getUniqueCustclass())&&v.getUniqueCustclass().equals(uniqueCustclass)).collect(Collectors.toList());
                        total=list.size();
                    }
                    list.forEach(a->{
                        // 账套新增申请
                        if(StrUtil.isNotBlank(a.getCtype())&&a.getCtype().equals("2")){
                            a.setOrgName(a.getAccName());
                        }
                    });
                    totalAR.set(total);
                    return Mono.just(
                            list.stream().filter(v->{
                                if (org.springbooz.core.tool.utils.StringUtils.isNotBlank(searchMap.get("requirement")) && org.springbooz.core.tool.utils.StringUtils.isNotBlank(searchMap.get("value"))) {
                                    String value = searchMap.get("value");
                                    String dbValue = getFieldValueByFieldName(searchMap.get("requirement").trim(), v);
                                    if (Objects.nonNull(dbValue) && !dbValue.contains(value)) {
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
    /**
     * 按状态获取数据
     *
     * @param flag
     * @return
     */
    @GetMapping("findAllByFlag")
    
    public Mono<R> findAllByFlag(String flag) {
        return customerRepository.findAllBySuccessStateAndFlag(flag,flag).collectList().map(R::page);
    }


    /**
     * 1、集团一份
     * @param exchange
     * @param customerMono
     * @return
     */
    @PostMapping("/save")
    public Mono<R> save(ServerWebExchange exchange, @RequestBody GroupCustomer customerMono) {
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        customerMono.setId(StringUtils.isNotBlank(customerMono.getId())?customerMono.getId():null).setCustDevdate(time);
        if(StrUtil.isBlank(customerMono.getUniqueCode())){
            customerMono.setUniqueCode(IdUtil.objectId());
        }
        return service.sys_save(customerMono).map(o -> R.ok().setResult(o));
    }

    /**
     * 直接更新账套数据，并 回滚记录增加、添加日志记录
     *
     * @param c
     * @return
     */
    @PostMapping("/editDatabaseApi")
    
    public Mono<R> editDatabaseApi(@RequestBody GroupCustomer c) {
        return Mono.just(c)
                .flatMap(customerRepository::save)
                .map(o -> R.ok().setResult(o));
    }

    /**
     * 导入客户信息
     *
     * @param filePartParm
     * @return
     * @throws Exception
     */
    @PostMapping("/importCus")
    public Mono<R> importCus(@RequestPart("file") FilePart filePartParm) throws Exception {
        // 获取表头个数
        AtomicInteger column= new AtomicInteger();
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
                String[] str = new String[]{"客户编码", "客户全称", "客户简称", "税号", "分类名称","母公司","对应客户","国家", "区划(省-市-区)", "详细地址", "电话", "手机", "网站", "EMail", "行业性质", "开户银行", "开户地", "银行账户", "银行行号",""};
                list = excelReader.getExcelObj(tempFilePath.toString(), str, 0);
                Arrays.stream(list.get(0)).forEach(v->{
                    if(v!=null){
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
            // 获取客户list
            .flatMap(item -> {
                Map mapArr = new HashMap();
                mapArr.put("excellist", item);   // excel中内容
                mapArr.put("error", "");
                mapArr.put("code", "200");
                return customerRepository.findAll()
                    .collectList()
                    .flatMap(list -> {
                        mapArr.put("list", list);   // 客户信息list
                        return customerClassRepository.findAll().collectList().flatMap(cla -> {
                            mapArr.put("classlist", cla);   // 客户分类list
                            return countryRepository.findAll().collectList()
                                .flatMap(country->{
                                    mapArr.put("country", country);// 国家list
                                    return groupSupplierRepository.findAll().collectList()
                                        .flatMap(groupSupList->{
                                            mapArr.put("groupSupList", groupSupList);// 集团供应商list
                                            return client.sql("SELECT * from sys_zone").fetch().all().collectList().flatMap(zonelist->{
                                                mapArr.put("zonelist", zonelist); // 省市区
                                                return Mono.just(mapArr);
                                            });
                                        });
                                });
                        });
                    });
            })
            // 判断 表头是否合法
            .flatMap(mapArr -> {
                // 科目list
                List<GroupCustomer> list = (List<GroupCustomer>) mapArr.get("list");
                List<GroupCustomerClass> classlist = (List<GroupCustomerClass>) mapArr.get("classlist");
                // 国家list
                List<SysCountry> countrylist = (List<SysCountry>) mapArr.get("country");
                // 集团供应商list
                List<GroupSupplier> groupSupList = (List<GroupSupplier>) mapArr.get("groupSupList");
                // 文件list
                List<Object[]> excellist = (List<Object[]>) mapArr.get("excellist");
                List<SysZone> zonelist = (List<SysZone>) mapArr.get("zonelist");
                List<GroupCustomer> cuslist = new ArrayList<>();

                // 如果小于5 说明表头不对
                if (!excellist.get(0)[0].equals("客户编码") && !excellist.get(0)[0].equals("客户全称")) {
                    mapArr.put("error", "导入模板表头不正确，请使用正确的模板进行导入");
                    mapArr.put("code", "401");
                    return Mono.just(mapArr);
                }
                // 判断是否重复
                for (int i = 1; i < excellist.size(); i++) {
                    int a=column.get();
                    Object[] obj=excellist.get(i);
                    int finalI = i;
                    List<String>errorText=new ArrayList<>();
                    // 客户编码是否重复
                    if(StringUtils.isNotBlank(excellist.get(finalI)[0].toString())){
                        List<GroupCustomer> collect = list.stream().filter(customer -> customer.getCustCode().equals(excellist.get(finalI)[0].toString())).collect(Collectors.toList());
                        if (collect.size() > 0) {
                            errorText.add("客户编码与当前已存在档案重复");
                            obj[a]=errorText.toString();
                        }
                    }else{
                        errorText.add("客户编码不能为空");
                        obj[a]=errorText.toString();
                    }
                    // 客户全称
                    if(StringUtils.isNotBlank(excellist.get(finalI)[1].toString())){
                        List<GroupCustomer> collect2 = list.stream().filter(customer -> customer.getCustName().equals(excellist.get(finalI)[1].toString())).collect(Collectors.toList());
                        if (collect2.size() > 0) {
                            errorText.add("客户全称与当前已存在档案重复");
                            obj[a]=errorText.toString();
                        }
                    }else{
                        errorText.add("客户全称不能为空");
                        obj[a]=errorText.toString();
                    }
                    // 客户简称
                    if(StringUtils.isNotBlank(excellist.get(finalI)[2].toString())) {
                        List<GroupCustomer> collect3 = list.stream().filter(customer -> customer.getCustAbbname().equals(excellist.get(finalI)[2].toString())).collect(Collectors.toList());
                        if (collect3.size() > 0) {
                            errorText.add("客户简称与当前已存在档案重复");
                            obj[a]=errorText.toString();
                        }
                    }
                    // 税号
                    if(StringUtils.isNotBlank(excellist.get(finalI)[3].toString())) {
                        List<GroupCustomer> collect4 = list.stream().filter(customer -> customer.getCustSregcode().equals(excellist.get(finalI)[3].toString())).collect(Collectors.toList());
                        if (collect4.size() > 0) {
                            errorText.add("客户税号与当前已存在档案重复");
                            obj[a]=errorText.toString();
                        }
                    }
                    if(column.get()>5){
                        // 银行账户
                        if(StringUtils.isNotBlank(excellist.get(finalI)[17].toString())) {
                            List<GroupCustomer> collect4 = list.stream().filter(customer -> customer.getCustAccount().equals(excellist.get(finalI)[17].toString())).collect(Collectors.toList());
                            if (collect4.size() > 0) {
                                errorText.add("银行账户与当前已存在档案重复");
                                obj[a]=errorText.toString();
                            }
                        }
                    }
                    if(errorText.size()==0){
                        String uniqueCustclass=classlist.get(0).getUniqueCustclass();
                        if (StringUtils.isNotBlank(excellist.get(i)[4].toString())) {
                            int finalI2 = i;
                            List<GroupCustomerClass> collect1 = classlist.stream().filter(cla -> cla.getCusCclassName().equals(excellist.get(finalI2)[4].toString().trim())).collect(Collectors.toList());
                            if (collect1.size() > 0) {
                                uniqueCustclass=collect1.get(0).getUniqueCustclass();
                            }
                        }
                        // 母公司
                        String uniqueCodeCcus="";
                        if(excellist.get(finalI)[5]!=null) {
                            List<GroupCustomer> collect = list.stream().filter(customer -> StringUtils.isNotBlank(customer.getUniqueCodeCcus()) && customer.getUniqueCodeCcus().equals(excellist.get(finalI)[5].toString().trim())).collect(Collectors.toList());
                            if(collect.size()>0){
                                uniqueCodeCcus=collect.get(0).getUniqueCode();
                            }
                        }
                        // 对应供应商
                        String uniqueCodeSupplier="";
                        if(excellist.get(finalI)[6]!=null) {
                            if(groupSupList.size()>0){
                                List<GroupSupplier> collect = groupSupList.stream().filter(v -> StringUtils.isNotBlank(v.getCustName()) && v.getCustName().equals(excellist.get(finalI)[6].toString().trim()) || v.getCustAbbname().equals(excellist.get(finalI)[6].toString().trim())).collect(Collectors.toList());
                                if(collect.size()>0){
                                    uniqueCodeSupplier=collect.get(0).getUniqueCode();
                                }
                            }
                        }
                        // 国家
                        String country="";
                        if(excellist.get(finalI)[7]!=null) {
                            if(countrylist.size()>0){
                                List<SysCountry> collect = countrylist.stream().filter(v -> StringUtils.isNotBlank(v.getNamech())&&v.getNamech().equals(excellist.get(finalI)[7].toString().trim())).collect(Collectors.toList());
                                if(collect.size()>0){
                                    country=collect.get(0).getNamech();
                                }
                            }
                        }

                        String province=excellist.get(i)[8]==null?"":excellist.get(i)[8].toString().split("-")[0].trim();
                        String city=excellist.get(i)[8]==null?"":excellist.get(i)[8].toString().split("-").length>1?excellist.get(i)[8].toString().split("-")[1].trim():"";
                        String area=excellist.get(i)[8]==null?"":excellist.get(i)[8].toString().split("-").length>2?excellist.get(i)[8].toString().split("-")[2].trim():"";
                        List<String> zoneStr=new ArrayList<>();
                        if(StringUtils.isNotBlank(excellist.get(finalI)[8].toString().trim())){
                            List<SysZone> collect = zonelist.stream().filter(zone -> zone.getZoneName().equals(excellist.get(finalI)[8].toString().trim())).collect(Collectors.toList());
                            if(collect.size()>0){
                                province=collect.get(0).getZoneName();
                                zoneStr.add(collect.get(0).getId());
                            }
                        }
                        if(StringUtils.isNotBlank(excellist.get(finalI)[9].toString().trim())){
                            List<SysZone> collect = zonelist.stream().filter(zone -> zone.getZoneName().equals(excellist.get(finalI)[9].toString().trim())).collect(Collectors.toList());
                            if(collect.size()>0){
                                city=collect.get(0).getZoneName();
                                zoneStr.add(collect.get(0).getId());
                            }
                        }
                        if(StringUtils.isNotBlank(excellist.get(finalI)[10].toString().trim())){
                            List<SysZone> collect = zonelist.stream().filter(zone -> zone.getZoneName().equals(excellist.get(finalI)[10].toString().trim())).collect(Collectors.toList());
                            if(collect.size()>0){
                                area=collect.get(0).getZoneName();
                                zoneStr.add(collect.get(0).getId());
                            }
                        }

                        GroupCustomer cus = new GroupCustomer();
                        cus.setUniqueCode(IdUtil.objectId())
                                .setCustCode(excellist.get(i)[0].toString().trim())
                                .setCustName(excellist.get(i)[1].toString().trim())
                                .setCustAbbname(StringUtils.isNotBlank(excellist.get(i)[2].toString()) ? excellist.get(i)[2].toString().trim() : excellist.get(i)[1].toString().trim())
                                .setCustSregcode(excellist.get(i)[3].toString().trim())
                                .setUniqueCodeCcus(uniqueCodeCcus)
                                .setUniqueCodeSupplier(uniqueCodeSupplier)
                                .setUniqueCustclass(uniqueCustclass)
                                .setCountryName(country)
                                .setProvince(province)
                                .setCity(city)
                                .setArea(area)
                                .setZone(zoneStr.toString())
                                .setAddress2(excellist.get(i)[11]==null?"":excellist.get(i)[11].toString().trim())
                                .setTelephone(excellist.get(i)[12]==null?"":excellist.get(i)[12].toString().trim())
                                .setCellPhoneNum(excellist.get(i)[13]==null?"":excellist.get(i)[13].toString().trim())
                                .setWebsite(excellist.get(i)[14]==null?"":excellist.get(i)[14].toString().trim())
                                .setEmail(excellist.get(i)[15]==null?"":excellist.get(i)[15].toString().trim())
                                .setIndustryclassName(excellist.get(i)[16]==null?"":excellist.get(i)[16].toString().trim())
                                .setCustBank(excellist.get(i)[17]==null?"":excellist.get(i)[17].toString().trim())
                                .setBankArea(excellist.get(i)[18]==null?"":excellist.get(i)[18].toString().trim())
                                .setCustAccount(excellist.get(i)[19]==null?"":excellist.get(i)[19].toString().trim())
                                .setBankCode(excellist.get(i)[20]==null?"":excellist.get(i)[20].toString().trim())
                                .setManageType("1")
                                .setSuccessState("1")
                                .setFlag("1")
                                .setCustDevdate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                        cuslist.add(cus);

                    }else{ mapArr.put("code", "200");mapArr.put("error", "error");}
                }
                // 进行去重
                List<String> listStr=new ArrayList<>();
                for (int i = 1; i < excellist.size(); i++) {
                    listStr.add(excellist.get(i)[0].toString());    // 编码
//                        listStr.add(excellist.get(i)[1].toString());    // 全称
                    if(StringUtils.isNotBlank(excellist.get(i)[2].toString().trim())){
                        if(!excellist.get(i)[1].toString().trim().equals(excellist.get(i)[2].toString().trim())){
                            listStr.add(excellist.get(i)[2].toString());    // 简称
                        }
                    }
                    if(StringUtils.isNotBlank(excellist.get(i)[3].toString())){
                        listStr.add(excellist.get(i)[3].toString());    // 税号
                    }
                }
                // 去重后list
                long doublesize=listStr.stream().distinct().count();
                if(doublesize<listStr.size()){
                    mapArr.put("error", "文件中有重复数据，请仔细检查再进行导入");
                    mapArr.put("code", "401");
                    return Mono.just(mapArr);
                }

                mapArr.put("list", cuslist);
                mapArr.put("column", column.get());
                return Mono.just(mapArr);
            })
            .flatMapMany(mapArr -> {
                List<GroupCustomer> list = (List<GroupCustomer>) mapArr.get("list");
                return mapArr.get("code").equals("401") ? Mono.just(mapArr) :list.size()==0?Mono.just(mapArr): customerRepository.saveAll(list).map(a->mapArr);
            })
            .collectList()
            .map(o -> R.ok().setResult(o));
    }

    /**
     * 组织新增申请导入
     * @param filePartParm
     * @return
     * @throws Exception
     * 权限orgPermiss：0手动添加，1自动分配
     * 所属供应商  暂时无法匹配【档案未完善】
     */
    @PostMapping("/OrgApplyImportCus")
    public Mono<R> OrgApplyImportCus(@RequestPart("file") FilePart filePartParm,@RequestPart("orgUnique") String orgUnique,
                                     @RequestPart("cusCodeType") String cusCodeType,@RequestPart("userName") String userName
            ,@RequestPart("orgPermiss") String orgPermiss) throws Exception {
        // 获取表头个数
        AtomicInteger column= new AtomicInteger();
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
            String[] str = new String[]{"客户编码", "客户全称", "客户简称", "税号", "分类名称","母公司","对应客户","国家", "区划(省-市-区)", "详细地址", "电话", "手机", "网站", "EMail", "行业性质", "开户银行", "开户地", "银行账户", "银行行号",""};
            list = excelReader.getExcelObj(tempFilePath.toString(), str, 0);
            Arrays.stream(list.get(0)).forEach(v->{
                if(v!=null){
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
        // 获取需要校验的list
        .flatMap(item -> {
            Map mapArr = new HashMap();
            mapArr.put("excellist", item);   // excel中内容
            mapArr.put("error", "");
            mapArr.put("code", "200");

            // 判断组织申请客户是否在分配表中
            return groupCustomerAccountRepository.findAllByCusAssignLeftJoinGroupCusData(orgUnique,"1").collectList()
            .flatMap(groupCustomerAccountList -> {
                // 集团客户信息
                return customerRepository.findAll().collectList()
                .flatMap(groupCusList->{
                    // 组织客户分类
                    return originCustomerClassRepository.findAllByOrgUnique(orgUnique).collectList()
                    .flatMap(orgCusClassList->{
                        // 国家表
                        return countryRepository.findAll().collectList()
                        .flatMap(countryList->{
                            // 客户编码规则
                            return groupFileEncodingRulesRepository.findByFileType("1-2")
                            .flatMap(obj->{
                                // 获取集团客户
                                return customerRepository.getCusCode().collectList()
                                .flatMap(cuscode->{
                                    // 组织供应商
                                    return originSupplierRepository.findAll().collectList()
                                    .flatMap(orgSupAll->{
                                        // 组织客户
                                        return originCustomerRepository.findAll().collectList()
                                        .flatMap(orgCusAll->{
                                            // 流水号
                                            String maxCode= obj.getSerialNumStr();
                                            // 判断表中前缀开头的编码 是否与编码规则一致
                                            List<String> collect = cuscode.stream().filter(a -> a.startsWith(obj.getShowRules())).collect(Collectors.toList());

                                            List<Integer> listInt=new ArrayList<>();
                                            if(collect.size()>0){
                                                collect.forEach(a->{
                                                    listInt.add(Integer.valueOf(a.split(obj.getShowRules())[1]));
                                                });
                                                Integer maxlistInt = listInt.stream().mapToInt(a->a).max().getAsInt();
                                                maxCode=String.valueOf(maxlistInt+1);
                                            }
                                            // 起始值
                                            mapArr.put("maxCode", maxCode);
                                            // 显示效果
                                            mapArr.put("showRules", obj.getShowRules());

                                            mapArr.put("serialNumLength", obj.getSerialNumLength());

                                            mapArr.put("groupCustomerAccountList", groupCustomerAccountList);
                                            mapArr.put("groupCusList", groupCusList);
                                            mapArr.put("orgCusClassList", orgCusClassList);
                                            mapArr.put("countryList", countryList);
                                            mapArr.put("orgSupList", orgSupAll);
                                            mapArr.put("orgCusAll", orgCusAll);
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
        // 判断 表头是否合法
        .flatMap(mapArr -> {
            /************************************ 自动编码 ***********************************/
                Integer maxCode=Integer.valueOf(mapArr.get("maxCode").toString());   // 起始值
                String serialNumLength=mapArr.get("serialNumLength").toString();    // 流水号长度
                String showRules=mapArr.get("showRules").toString();                // 显示效果
            /************************************ 自动编码 END***********************************/

            // 判断组织申请客户是否在分配表中
            List<GroupCustomerVo> groupCustomerAccountList= (List<GroupCustomerVo>) mapArr.get("groupCustomerAccountList");
            // 集团客户信息
            List<GroupCustomer> groupCusList= (List<GroupCustomer>) mapArr.get("groupCusList");
            // 组织客户分类
            List<OrgCustomerClass> orgCusClassList= (List<OrgCustomerClass>) mapArr.get("orgCusClassList");
            // 国家表
            List<SysCountry> countryList= (List<SysCountry>) mapArr.get("countryList");
            // 组织供应商
            List<OrgSupplier> orgSupList= (List<OrgSupplier>) mapArr.get("orgSupList");
            // 组织客户
            List<OrgCustomer> orgCusAll= (List<OrgCustomer>) mapArr.get("orgCusAll");
            // 文件list
            List<Object[]> excellist = (List<Object[]>) mapArr.get("excellist");
            List<GroupCustomer> cuslist = new ArrayList<>();
            List<GroupCustomer> cuslist2 = new ArrayList<>();

            // 如果小于5 说明表头不对
            if (!excellist.get(0)[0].equals("客户编码") && !excellist.get(0)[0].equals("客户全称")) {
                mapArr.put("error", "导入模板表头不正确，请使用正确的模板进行导入");
                mapArr.put("code", "401");
                return Mono.just(mapArr);
            }

            // 判断是否重复
            for (int i = 1; i < excellist.size(); i++) {
                int a=column.get();
                Object[] obj=excellist.get(i);
                int finalI = i;
                List<String>errorText=new ArrayList<>();

                // 客户编码是否重复
                // 手动编码 需校验
                if(cusCodeType.equals("1")){
                    if(StringUtils.isNotBlank(excellist.get(finalI)[0].toString())){
                        // 1、是否在分配表中
                        List<GroupCustomerVo> collect = groupCustomerAccountList.stream().filter(f -> f.getCustCode().equals(excellist.get(finalI)[0].toString())).collect(Collectors.toList());
                        if(collect.size()>0){
                            // 1-1 是否引入 0未引入|1已引入
                            if(collect.get(0).getAssignFlag().equals("1")){
                                errorText.add("【客户编码】客户信息已引入,不允许重复！");
                                obj[a]=errorText.toString();
                            }else{
                                errorText.add("【客户编码】客户信息已存在,未引入！");
                                obj[a]=errorText.toString();
                            }
                        }
                        else{
                            // 2、是否在集团客户表
                            List<GroupCustomer> collect1 = groupCusList.stream().filter(f -> f.getCustCode().equals(excellist.get(finalI)[0].toString())).collect(Collectors.toList());
                            if(collect1.size()>0){
                                errorText.add("客户编码在集团档案中已存在,不能重复！");
                                obj[a]=errorText.toString();
                            }
                        }
                    }else{
                        errorText.add("客户编码不能为空");
                        obj[a]=errorText.toString();
                    }
                }
                // 权限 手动添加需校验；自动添加 略过
                if(orgPermiss.equals("0")){
                    // 客户全称
                    if(StringUtils.isBlank(excellist.get(finalI)[1].toString())){
                        errorText.add("客户全称不能为空");
                        obj[a]=errorText.toString();
                    }
                    // 客户简称+全称是否重复;
                    if(StringUtils.isNotBlank(excellist.get(finalI)[2].toString())){
                        // 1、是否在分配表中
                        List<GroupCustomerVo> collect = groupCustomerAccountList.stream().filter(f -> f.getCustAbbname().equals(excellist.get(finalI)[2].toString())&& f.getCustName().equals(excellist.get(finalI)[1].toString())).collect(Collectors.toList());
                        if(collect.size()>0){
                            // 1-1 是否引入 0未引入|1已引入
                            if(collect.get(0).getAssignFlag().equals("1")){
                                errorText.add("客户信息已引入,不允许重复！");
                                obj[a]=errorText.toString();
                            }else{
                                errorText.add("客户信息已分配,未引入！");
                                obj[a]=errorText.toString();
                            }
                        }
                    }
                    // 客户税号
                    if(StringUtils.isNotBlank(excellist.get(finalI)[3].toString())){
                        // 1、是否在分配表中
                        List<GroupCustomerVo> collect = groupCustomerAccountList.stream().filter(f -> f.getCustSregcode().equals(excellist.get(finalI)[3].toString())).collect(Collectors.toList());
                        if(collect.size()>0){
                            // 1-1 是否引入 0未引入|1已引入
                            if(collect.get(0).getAssignFlag().equals("1")){
                                errorText.add("【税号】客户信息已引入,不允许重复！");
                                obj[a]=errorText.toString();
                            }else{
                                errorText.add("【税号】客户信息已存在,未引入！");
                                obj[a]=errorText.toString();
                            }
                        }
                        else{
                            // 2、是否在集团客户表
                            List<GroupCustomer> collect1 = groupCusList.stream().filter(f -> f.getCustSregcode().equals(excellist.get(finalI)[3].toString())).collect(Collectors.toList());
                            if(collect1.size()>0){
                                errorText.add("税号在集团档案中已存在,不能重复！");
                                obj[a]=errorText.toString();
                            }
                        }
                    }
                    if(column.get()>5){
                        // 银行账号
                        if(StringUtils.isNotBlank(excellist.get(finalI)[17].toString())) {
                            // 1、是否在分配表中
                            List<GroupCustomerVo> collect = groupCustomerAccountList.stream().filter(f -> f.getCustAccount().equals(excellist.get(finalI)[17].toString())).collect(Collectors.toList());
                            if(collect.size()>0){
                                // 1-1 是否引入 0未引入|1已引入
                                if(collect.get(0).getAssignFlag().equals("1")){
                                    errorText.add("【银行账号】客户信息已引入,不允许重复！");
                                    obj[a]=errorText.toString();
                                }else{
                                    errorText.add("【银行账号】客户信息已存在,未引入！");
                                    obj[a]=errorText.toString();
                                }
                            }
                        }
                    }
                }
                if (StringUtils.isNotBlank(excellist.get(i)[4].toString())) {
                    int finalI2 = i;
                    List<OrgCustomerClass> collect1 = orgCusClassList.stream().filter(cla -> cla.getCusBend().equals("1")&& cla.getCusCclassName().equals(excellist.get(finalI2)[4].toString().trim())).collect(Collectors.toList());
                    if (collect1.size() == 0) {
                        errorText.add("分类名称不是末级");
                        obj[a]=errorText.toString();
                    }
                }
                if(errorText.size()==0){
                    // 客户分类
                    String uniqueCustclass="";
                    if (StringUtils.isNotBlank(excellist.get(i)[4].toString())) {
                        int finalI2 = i;
                        List<OrgCustomerClass> collect1 = orgCusClassList.stream().filter(cla ->cla.getCusBend().equals("1")&& cla.getCusCclassName().equals(excellist.get(finalI2)[4].toString().trim())).collect(Collectors.toList());
                        if (collect1.size() > 0) {
                            uniqueCustclass=collect1.get(0).getUniqueCustclass();
                        }
                    }
                    // 母公司
                    String uniqueCodeCcus="";
                    if(excellist.get(finalI)[5]!=null) {
                        List<OrgCustomer> collect = orgCusAll.stream().filter(customer -> StringUtils.isNotBlank(customer.getUniqueCodeCcus()) && customer.getUniqueCodeCcus().equals(excellist.get(finalI)[5].toString().trim())).collect(Collectors.toList());
                        if(collect.size()>0){
                            uniqueCodeCcus=collect.get(0).getUniqueCode();
                        }
                    }
                    // 对应供应商
                    String uniqueCodeSupplier="";
                    if(excellist.get(finalI)[6]!=null) {
                        List<OrgSupplier> collect = orgSupList.stream().filter(customer -> StringUtils.isNotBlank(customer.getUniqueCodeCcus()) && customer.getUniqueCodeCcus().equals(excellist.get(finalI)[6].toString().trim())).collect(Collectors.toList());
                        if(collect.size()>0){
                            uniqueCodeSupplier=collect.get(0).getUniqueCode();
                        }
                    }
                    // 国家
                    String country="";
                    if(excellist.get(finalI)[7]!=null) {
                        if(countryList.size()>0){
                            List<SysCountry> collect = countryList.stream().filter(v -> StringUtils.isNotBlank(v.getNamech())&&v.getNamech().equals(excellist.get(finalI)[7].toString().trim())).collect(Collectors.toList());
                            if(collect.size()>0){
                                country=collect.get(0).getNamech();
                            }
                        }
                    }
                    // 省-市-区
                    String province=excellist.get(i)[8]==null?"":excellist.get(i)[8].toString().split("-")[0].trim();
                    String city=excellist.get(i)[8]==null?"":excellist.get(i)[8].toString().split("-").length>1?excellist.get(i)[8].toString().split("-")[1].trim():"";
                    String area=excellist.get(i)[8]==null?"":excellist.get(i)[8].toString().split("-").length>2?excellist.get(i)[8].toString().split("-")[2].trim():"";

                    // 2自动编码 OR 1手动编码
                    String cusCode=cusCodeType.equals("1")?excellist.get(i)[0].toString().trim():showRules+String.format("%0"+serialNumLength+"d",(maxCode+i)-1);
                    GroupCustomer cus = new GroupCustomer();
                    cus.setUniqueCode(IdUtil.objectId())
                            .setCustCode(cusCode)
                            .setCustName(excellist.get(i)[1].toString().trim())
                            .setCustAbbname(StringUtils.isNotBlank(excellist.get(i)[2].toString()) ? excellist.get(i)[2].toString().trim() : excellist.get(i)[1].toString().trim())
                            .setCustSregcode(excellist.get(i)[3].toString().trim())
                            .setUniqueCodeCcus(uniqueCodeCcus)
                            .setUniqueCodeSupplier(uniqueCodeSupplier)
                            .setUniqueCustclass(uniqueCustclass)
                            .setCountryName(country)
                            .setProvince(province)
                            .setCity(city)
                            .setArea(area)
                            .setAddress2(excellist.get(i)[9]==null?"":excellist.get(i)[9].toString().trim())
                            .setTelephone(excellist.get(i)[10]==null?"":excellist.get(i)[10].toString().trim())
                            .setCellPhoneNum(excellist.get(i)[11]==null?"":excellist.get(i)[11].toString().trim())
                            .setWebsite(excellist.get(i)[12]==null?"":excellist.get(i)[12].toString().trim())
                            .setEmail(excellist.get(i)[13]==null?"":excellist.get(i)[13].toString().trim())
                            .setIndustryclassName(excellist.get(i)[14]==null?"":excellist.get(i)[14].toString().trim())
                            .setCustBank(excellist.get(i)[15]==null?"":excellist.get(i)[15].toString().trim())
                            .setBankArea(excellist.get(i)[16]==null?"":excellist.get(i)[16].toString().trim())
                            .setCustAccount(excellist.get(i)[17]==null?"":excellist.get(i)[17].toString().trim())
                            .setBankCode(excellist.get(i)[18]==null?"":excellist.get(i)[18].toString().trim())
                            .setManageType("1")
                            .setFlag("1")
                            .setCustDevdate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()))
                            .setOriginUnique(orgUnique).setOrgUnique(orgUnique).setCtype("1").setApplyDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()))
                            .setApplyUser(userName).setIsDel("0");

                    cus.setSuccessState(orgPermiss.equals("0")?"0":"1");

                    // 2、是否在集团客户表
                    List<GroupCustomer> collect1 = groupCusList.stream().filter(f ->f.getSuccessState().equals("1") && f.getCustAbbname().equals(excellist.get(finalI)[2].toString()) && f.getCustName().equals(excellist.get(finalI)[1].toString())).collect(Collectors.toList());
                    // 自动审批权限打开。
                    if(orgPermiss.equals("1")){
                        if(collect1.size()==0){
                            cus.setApproveUser(userName).setApproveDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                            cuslist.add(cus);
                        }
                        else{
                            GroupCustomer c = collect1.get(0);
                            // 集团状态是假删除，修改成未删除
                            if(c.getIsDel().equals("1")){
                                c.setIsDel("0");
                                cuslist2.add(c);
                            }
                            GroupCustomer cus2 = new GroupCustomer();
                            cus2.setUniqueCode(IdUtil.objectId())
                            .setCustCode(c.getCustCode())
                            .setCustName(c.getCustName())
                            .setCustAbbname(c.getCustAbbname())
                            .setCustSregcode(c.getCustSregcode())
                            .setUniqueCodeCcus(c.getUniqueCodeCcus())
                            .setUniqueCodeSupplier(c.getUniqueCodeSupplier())
                            .setUniqueCustclass(c.getUniqueCustclass())
                            .setCountryName(c.getCountryName())
                            .setProvince(c.getProvince())
                            .setCity(c.getCity())
                            .setArea(c.getArea())
                            .setAddress2(c.getAddress2())
                            .setTelephone(c.getTelephone())
                            .setCellPhoneNum(c.getCellPhoneNum())
                            .setWebsite(c.getWebsite())
                            .setEmail(c.getEmail())
                            .setIndustryclassName(c.getIndustryclassName())
                            .setCustBank(c.getCustBank())
                            .setBankArea(c.getBankArea())
                            .setCustAccount(c.getCustAccount())
                            .setBankCode(c.getBankCode())
                            .setManageType("1")
                            .setFlag("1")
                            .setCustDevdate(c.getCustDevdate())
                            .setOriginUnique(orgUnique).setOrgUnique(orgUnique).setCtype("1").setApplyDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()))
                            .setApplyUser(userName).setIsDel("0")
                            .setApproveUser(userName).setApproveDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

                            cus2.setSuccessState(orgPermiss.equals("0")?"0":"1");
                            cuslist.add(cus2);
                        }
                    }
                    else{
                        cuslist.add(cus);
                    }
                }else{ mapArr.put("code", "200");mapArr.put("error", "error");}
            }
            // 进行去重
            List<String> listStr=new ArrayList<>();
            for (int i = 1; i < excellist.size(); i++) {
                listStr.add(excellist.get(i)[0].toString());    // 编码
                if(StringUtils.isNotBlank(excellist.get(i)[2].toString().trim())){
                    if(!excellist.get(i)[1].toString().trim().equals(excellist.get(i)[2].toString().trim())){
                        listStr.add(excellist.get(i)[2].toString());    // 简称
                    }
                }
                if(StringUtils.isNotBlank(excellist.get(i)[3].toString())){
                    listStr.add(excellist.get(i)[3].toString());    // 税号
                }
            }
            // 去重后list
            long doublesize=listStr.stream().distinct().count();
            if(doublesize<listStr.size()){
                mapArr.put("error", "文件中有重复数据，请仔细检查再进行导入");
                mapArr.put("code", "401");
                return Mono.just(mapArr);
            }

            mapArr.put("list", cuslist);
            mapArr.put("cuslist2", cuslist2);
            mapArr.put("column", column.get());
            return Mono.just(mapArr);
        })
        .flatMapMany(mapArr -> {
            List<GroupCustomer> list = (List<GroupCustomer>) mapArr.get("list");
            List<GroupCustomer> cuslist2 = (List<GroupCustomer>) mapArr.get("cuslist2");

            // 如果集团有相同的数据，用集团的
            if(cuslist2.size()>0){
                for (int i = 0; i < list.size(); i++) {
                    int finalI = i;
                    List<GroupCustomer> collect = cuslist2.stream().filter(f -> f.getCustName().equals(list.get(finalI).getCustName()) && f.getCustAbbname().equals(list.get(finalI).getCustAbbname())).collect(Collectors.toList());
                    if(collect.size()>0){
                        list.remove(i);
                        list.add(collect.get(0));
                    }
                    // 自动审批开启
                    if(orgPermiss.equals("1")){
                        list.get(i).setApproveUser(userName).setApproveDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                    }
                }
            }
            return mapArr.get("code").equals("401") ? Mono.just(mapArr) :list.size()==0?Mono.just(mapArr): customerRepository.saveAll(list).map(a->mapArr);
        })
        .collectList()
        .map(o -> R.ok().setResult(o));
    }

    /**
     * 假删除
     * @param id
     * @return
     */
    @PostMapping("/delCus")
    public Mono<R>delCus(String id,String delName){
        return customerRepository.editByidIsDel(id,"1",delName,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).then(Mono.just(R.ok()));
    }

    /**
     * 假删除
     * @param id
     * @return
     */
    /**
     * 恢复删除
     * @param id
     * @return
     */
    @PostMapping("/recoverCus")
    public Mono<R>delCus(String id){
        return customerRepository.editByidIsDel(id,"0","","").then(Mono.just(R.ok()));
    }

    /**
     * 批量删除
     * @param id
     * @return
     */
    @PostMapping("/delCusArr")
    public Mono<R>delCusArr(String id){
        List<GroupCustomer> list = new ArrayList<>();
        for (int i = 0; i < id.split(",").length; i++) {
            list.add(new GroupCustomer().setId(id.split(",")[i]));
        }
        return customerRepository.deleteAll(list).then(Mono.just(R.ok()));
    }

    @PostMapping("/findAllByUniqueCustclass")
    public Mono<R>findAllByUniqueCustclass(@RequestBody Map map){
        List<String> list=Arrays.asList(map.get("uniqueCustclass").toString().split(","));
        return customerRepository.findAllByUniqueCustclass(list).collectList().map(a->R.ok().setResult(a));
    }

    @PostMapping("/findAllByGroupAndAllot")
    public Mono<R> findAllByGroupAndAllot(String tenantId){
        return customerRepository.findAllByGroupAndAllot(tenantId).collectList().map(a->R.ok().setResult(a));
    }

    @PostMapping("/countAllotByCustUnique")
    public Mono<R> countAllotByCustUnique(String cusUnique){
        return customerRepository.countAllotByCustUnique(cusUnique).map(a->R.ok().setResult(a));
    }

    @PostMapping("/findAllByAllot")
    public Mono<R> findAllByAllot(){
        return groupAllotRecordRepository.findAll().collectList().map(a->R.ok().setResult(a));
    }


    @PostMapping("/getCodingRule")
    public Mono<R> getCodingRule(String ctype){
        return groupFileEncodingRulesRepository.findByFileType(ctype).map(a->R.ok().setResult(a)).defaultIfEmpty(R.ok().setResult(""));
    }

    @PostMapping("/getMaxCusCodeByCodingFlag")
    public Mono<R> getMaxCusCodeByCodingFlag(@RequestBody GroupFileEncodingRules obj){
        return customerRepository.getCusCode().collectList()
                .flatMap(cuscode->{
                    String newcode="";
                    // 流水号
                    String lsh= String.format("%0"+obj.getSerialNumLength()+"d",Integer.valueOf(obj.getSerialNumStr()));
                    // 判断表中前缀开头的编码 是否与编码规则一致
                    List<String> collect = cuscode.stream().filter(a -> a.startsWith(obj.getShowRules())).collect(Collectors.toList());

                    List<Integer> listInt=new ArrayList<>();
                    if(collect.size()>0){
                        collect.forEach(a->{
                            listInt.add(Integer.valueOf(a.split(obj.getShowRules())[1]));
                        });
                        Integer maxlistInt = listInt.stream().mapToInt(a->a).max().getAsInt();
                        newcode=obj.getShowRules()+String.format("%0"+obj.getSerialNumLength()+"d",maxlistInt+1);
                    }else{
                        newcode= obj.getShowRules()+lsh;
                    }
                    return Mono.just(newcode);
                })
                .map(a->R.ok().setResult(a));
    }

    @PostMapping("/findGroupCusByOrg")
    public Mono<R> findGroupCusByOrg(@RequestBody Map map){
        String orgUnique=map.get("orgUnique").toString();
        Map<String, String> searchMap = ((HashMap<String,  String>) map.get("searchConditon"));
        return customerRepository.findGroupCusByOrg(orgUnique).collectList()
            .flatMap(list->{
                return Mono.just(
                        list.stream().filter(v->{
                            if (org.springbooz.core.tool.utils.StringUtils.isNotBlank(searchMap.get("requirement")) && org.springbooz.core.tool.utils.StringUtils.isNotBlank(searchMap.get("value"))) {
                                String value = searchMap.get("value");
                                String dbValue = getFieldValueByFieldName(searchMap.get("requirement").trim(), v);
                                if (Objects.nonNull(dbValue) && !dbValue.contains(value)) {
                                    return false;
                                }
                            }
                            return true;
                        })
                );
            })
            .map(a->R.ok().setResult(a));
    }

    @PostMapping("/findGroupCusByTenantId")
    public Mono<R> findGroupCusByTenantId(@RequestBody Map map){
        String tenantId=map.get("tenantId").toString();
        Map<String, String> searchMap = ((HashMap<String,  String>) map.get("searchConditon"));
        return customerRepository.findGroupCusByTenantId(tenantId).collectList()
                .flatMap(list->{

                    return Mono.just(
                            list.stream().filter(v->{
                                if (org.springbooz.core.tool.utils.StringUtils.isNotBlank(searchMap.get("requirement")) && org.springbooz.core.tool.utils.StringUtils.isNotBlank(searchMap.get("value"))) {
                                    String value = searchMap.get("value");
                                    String dbValue = getFieldValueByFieldName(searchMap.get("requirement").trim(), v);
                                    if (Objects.nonNull(dbValue) && !dbValue.contains(value)) {
                                        return false;
                                    }
                                }
                                return true;
                            })
                    );
                })
                .map(a->R.ok().setResult(a));
    }

    @PostMapping("/editGroupCusAll")
    public Mono<R> editGroupCusAll(@RequestBody List<GroupCustomer> list){
        return customerRepository.saveAll(list).collectList().map(a->R.ok().setResult(a));
    }

    @PostMapping("/findAllByGroupCusNameAndCustAbbnameLike")
    public Mono<R> findAllByGroupCusNameAndCustAbbnameLike(String name,String abbName){
        return customerRepository.findAllByGroupCusNameAndCustAbbnameLike("%"+name+"%","%"+abbName+"%").collectList().map(a->R.ok().setResult(a));
    }


    @PostMapping("/editByIdSuccessState")
    public Mono<R> editByIdSuccessState(String id,String approveUser){
        return customerRepository.editByIdSuccessState("1",id,approveUser,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).then(Mono.just(R.ok()));
    }

    @PostMapping("/delByIdCustNameAndCustAbbnameAndSuccessState")
    public Mono<R> delByIdCustNameAndCustAbbnameAndSuccessState(String custName,String custAbbName,String successState){
        return customerRepository.delByIdCustNameAndCustAbbnameAndSuccessState(custName,custAbbName,successState).then(Mono.just(R.ok()));
    }
}

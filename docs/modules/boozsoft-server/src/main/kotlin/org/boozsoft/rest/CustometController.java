package org.boozsoft.rest;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.boozsoft.domain.entity.*;
import org.boozsoft.domain.entity.group.GroupCustomer;
import org.boozsoft.domain.entity.origin.OrgCustomerClass;
import org.boozsoft.domain.vo.CustomerMaxNumVo;
import org.boozsoft.domain.vo.CustomerVo;
import org.boozsoft.domain.vo.GroupCustomerVo;
import org.boozsoft.repo.*;
import org.boozsoft.repo.group.GroupCustomerAccountRepository;
import org.boozsoft.repo.group.GroupFileEncodingRulesRepository;
import org.boozsoft.service.CustomerService;
import org.boozsoft.service.GroupCustomerService;
import org.boozsoft.service.SysZoneService;
import org.boozsoft.util.NewStringUtil;
import org.boozsoft.util.XlsUtils3;
import org.springbooz.core.tool.result.R;
import org.springbooz.core.tool.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.transaction.annotation.Transactional;
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
@RequestMapping("/cusrtomer")
@Api(value = "客户信息", tags = "API系统：客户信息")
public class CustometController {
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    GroupCustomerRepository groupCustomerRepository;
    @Autowired
    CountryRepository countryRepository;
    @Autowired
    SysTradeNatureRepository sysTradeNatureRepository;
    @Autowired
    CustomerService service;
    @Autowired
    GroupCustomerService groupCustomerService;
    @Autowired
    GroupDistRepository groupDistRepository;
    @Autowired
    SysDistReocrdRepository sysDistReocrdRepository;
    @Autowired
    SysZoneService zoneService;
    @Autowired
    CustomerClassRepository customerClassRepository;
    @Autowired
    SupplierRepository supplierRepository;
    @Autowired
    SysZoneRepository sysZoneRepository;
    @Autowired
    GroupCustomerAccountRepository groupCustomerAccountRepository;
    @Autowired
    GroupFileEncodingRulesRepository groupFileEncodingRulesRepository;
    @Autowired
    AuditCustomerRepository auditCustomerRepository;
    @Autowired
    DatabaseClient client;




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
     * 查询  集团档案分配管控权限表
     *
     * @return
     */
    @PostMapping("findByDatabaseUniqueCode")
    public Mono<R> findByDatabaseUniqueCode() {
        return groupDistRepository.findByDatabaseUniqueCodeAndTableName("abc001", "customer").map(o -> R.ok().setResult(o));
    }


    @PostMapping("verifyCustAccount")
    public Mono<R> verifyCustAccount(String bankNum) {
        return customerRepository.countByCustAccount(bankNum).map(o -> R.ok().setResult(o));
    }

    @PostMapping("verifyCustomerName")
    public Mono<R> verifyCustomerName(String name) {
        return customerRepository.countByCustName(name).map(o -> R.ok().setResult(o));
    }

    @PostMapping("verifyCustomerNum")
    public Mono<R> verifyCustomerNum(String num) {
        return customerRepository.countByCustCode(num).map(o -> R.ok().setResult(o));
    }

    @PostMapping("verifyCustomerId")
    public Mono<R> verifyCustomerId(String id) {
        return customerRepository.countById(id).map(o -> R.ok().setResult(o));
    }

    @PostMapping("verifyByCustAbbname")
    public Mono<R> verifyByCustAbbname(String custAbbname) {
        return customerRepository.countByCustAbbname(custAbbname).map(o -> R.ok().setResult(o));
    }

    @PostMapping("verifyByCustSregcode")
    public Mono<R> verifyByCustSregcode(String custSregcode) {
        return customerRepository.countByCustSregcode(custSregcode).map(o -> R.ok().setResult(o));
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


    @GetMapping("findById")
    public Mono<R> findById(String id) {
        return customerRepository.findById(id).map(R::ok);
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
        // 当前页
        int page= map.get("page")==null?1:Integer.parseInt(map.get("page").toString());
        // 页多少条
        int size= map.get("size")==null?10000:Integer.parseInt(map.get("size").toString());

        AtomicReference<Long> totalAR = new AtomicReference(0);
        String uniqueCustclass=map.get("uniqueCustclass").toString();
        String flag=map.get("flag").toString();
        Map<String, String> searchMap = ((HashMap<String,  String>) map.get("searchConditon"));
        return service.findAllCustomerByDatabase(uniqueCustclass).collectList()
            .flatMap(cus->{
                long total=cus.size();
                List<CustomerVo> list=cus.stream().skip((page - 1) * size).limit(size).collect(Collectors.toList());
                if(StringUtils.isNotBlank(flag)){
                    list=cus.stream().filter(v->v.getFlag().equals(flag)).collect(Collectors.toList());
                    total=list.size();
                }
                totalAR.set(total);
                return Mono.just(list.stream().filter(item->{
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
                .map(a -> R.page(a.collect(Collectors.toList()),pageable,(totalAR.get())));
    }

    @PostMapping("findAllIsDel")
    public Mono<R> findAllIsDel(@RequestBody Map map, Pageable pageable) {
        // 当前页
        int page= map.get("page")==null?1:Integer.parseInt(map.get("page").toString());
        // 页多少条
        int size= map.get("size")==null?10000:Integer.parseInt(map.get("size").toString());
        AtomicReference<Long> totalAR = new AtomicReference(0);
        Map<String, String> searchMap = ((HashMap<String,  String>) map.get("searchConditon"));
        return customerRepository.findAllIsDel().collectList()
                .flatMap(cuslist->{
                    long total=cuslist.size();
                    List<Customer> cus=cuslist.stream().skip((page - 1) * size).limit(size).collect(Collectors.toList());
                    totalAR.set(total);
                    return Mono.just(cus.stream().filter(item->{
                        // 按条件过滤
                        if (StringUtils.isNotBlank(searchMap.get("requirement")) && StringUtils.isNotBlank(searchMap.get("value"))) {
                            String value = searchMap.get("value");
                            String dbValue = getFieldValueByFieldName(searchMap.get("requirement").trim(), item);
                            if(searchMap.get("requirement").trim().equals("custCode")){
                                if (Objects.nonNull(dbValue) && !dbValue.equals(value)) {
                                    return false;
                                }
                            }else{
                                if (Objects.nonNull(dbValue) && !dbValue.contains(value)) {
                                    return false;
                                }
                            }
                        }
                        return true;
                    }));
                })
                .map(a -> R.page(a.collect(Collectors.toList()),pageable,(totalAR.get())));
    }

    /**
     * 按状态获取数据
     *
     * @param flag
     * @return
     */
    @GetMapping("findAllByFlag")
    public Mono<R> findAllByFlag(String flag) {
        return customerRepository.findAllByFlag(flag).collectList().map(R::page);
    }


    /**
     * 1、集团一份
     * 2、账套一份
     * 3、添加日志记录
     * 4、添加档案分配记录
     * 5、通知集团操作员
     *
     * @param customerMono
     * @return
     */
    @PostMapping("/save")
    public Mono<R> save(@RequestBody Customer customerMono) {
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        customerMono.setId(StringUtils.isNotBlank(customerMono.getId())?customerMono.getId():null);
        if(StringUtils.isBlank(customerMono.getId())){
            customerMono.setCustDevdate(time).setUniqueCode(IdUtil.objectId());
        }else{
            customerMono.setBeiyong2(time);
        }

        customerMono.setFlag("1");
        return Mono.zip(
                service.sys_save(customerMono),
                groupCustomerService.database_save(customerMono)
        ).map(o -> R.ok().setResult(o));
    }

    @PostMapping("/saveCus")
    public Mono<R> saveCus(@RequestBody Customer customerMono) {
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        customerMono.setId(StringUtils.isNotBlank(customerMono.getId())?customerMono.getId():null);
        if(StringUtils.isBlank(customerMono.getId())){
            customerMono.setCustDevdate(time);
        }
        customerMono.setFlag("1");
        return customerRepository.save(customerMono).map(o -> R.ok().setResult(o));
    }

    /**
     * 分配：添加
     * @param customerMono
     * @return
     */
    @PostMapping("/allotSave")
    public Mono<R> allotSave(@RequestBody Customer customerMono) {
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        if(StringUtils.isBlank(customerMono.getId())){
            customerMono.setCustDevdate(time);
        }else{
            customerMono.setBeiyong2(time);
        }
        return customerRepository.save(customerMono).map(o -> R.ok().setResult(o));
    }

    @PostMapping("/duLiSave")
    public Mono<R> duLiSave(@RequestBody Customer customerMono) {
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        customerMono.setId(StringUtils.isNotBlank(customerMono.getId())?customerMono.getId():null);
        if(StringUtils.isBlank(customerMono.getId())){
            customerMono.setCustDevdate(time);
            if(StringUtils.isBlank(customerMono.getUniqueCode())){
                customerMono.setUniqueCode(IdUtil.objectId());
            }
        }else{
            customerMono.setBeiyong2(time);
        }
        return customerRepository.save(customerMono).map(o -> R.ok().setResult(o));
    }

    /**
     * 修改状态：关键字开启 自动分配（集团/已经下发过的账套 全更新）
     *
     * @param exchange
     * @param customerMono 档案唯一码
     * @return
     */
    @PostMapping("/updateKeyWordIsAutoSave")
    public Mono<R> updateKeyWordIsAutoSave(ServerWebExchange exchange, @RequestBody Customer customerMono) {
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        // 根据 档案唯一码 查询 档案分配记录 一并修改
        return sysDistReocrdRepository.findAllByRecordUniqueCode(customerMono.getUniqueCode())
                .collectList()
                .map(item -> {
                    for (int i = 0; i < item.size(); i++) {
                        customerMono.setId(item.get(i).getId());
                        service.updateIsAutoSaveApi(item.get(i).getAccId(), customerMono.getUniqueCode(), customerMono).then();
                    }
                    return Mono.just(customerMono);
                })
                .flatMap(item -> item)
                .map(ctx -> {
                    return customerRepository.save(ctx)
                            .flatMap(ctc -> {
                                return Mono.just("【test在" + time + "】修改了一条客户信息,客户全称：【" + customerMono.getCustName() + "】");
                            });

                })
                .flatMap(log -> log)
                .flatMap(service::syslog_save)
                .map(o -> R.ok().setResult(o));
    }

    /**
     * 直接更新账套数据，并 回滚记录增加、添加日志记录
     * @param c
     * @return
     */
    @PostMapping("/editDatabaseApi")
    public Mono<R> editDatabaseApi(@RequestBody Customer c,String username) {
        return customerRepository.findById(c.getId())
                .map(item -> c)
                .flatMap(customerRepository::save).map(o -> R.ok().setResult(o));
    }

    // 导出时查询所有信息
    @GetMapping("exportExcelFindAllJSON")
    public Mono<R> exportExcelFindAllJSON() {
        Map map = new HashMap();
        map.put("all", ""); // 母公司
        map.put("classlist", ""); // 客户分类
        map.put("gys", ""); // 供应商
        map.put("gsdz", "");// 公司地址
        return customerRepository.findAllByOrderByCustCodeAsc().collectList()
                .flatMap(cus -> {
                    map.put("all", cus);
                    return supplierRepository.findAllByApplyDatabaseUniqueCodeAndApplyUserAndSuccessState().collectList()
                            .flatMap(sup -> {
                                map.put("gys", sup);
                                return zoneService.findAll().flatMap(zone -> {
                                            map.put("gsdz", zone.getResult());
                                            return customerClassRepository.findAll().collectList().map(cla -> {
                                                map.put("classlist", cla);
                                                return map;
                                            });
                                        });
                            });
                })
                .flatMap(m -> {
                    List<Customer> list = (List<Customer>) m.get("all");
                    List<Supplier> suplist = (List<Supplier>) m.get("gys");
                    List<SysZone> zonelist = (List<SysZone>) m.get("gsdz");
                    List<CustomerClass> classlist = (List<CustomerClass>) m.get("classlist");

                    List<Map> listmap = new ArrayList<>();
                    for (int i = 0; i < list.size(); i++) {
                        Map ma = new HashMap();
                        ma.put("custCode", list.get(i).getCustCode());
                        ma.put("custName", list.get(i).getCustName());
                        ma.put("custAbbname", list.get(i).getCustAbbname());
                        ma.put("custSregcode", list.get(i).getCustSregcode());
                        ma.put("manageType", list.get(i).getManageType().equals("1") ? "外部客户" : "内部客户");
                        ma.put("uniqueCodeCcus", "");
                        ma.put("uniqueCodeSupplier", "");
                        ma.put("province", "");
                        ma.put("uniqueCustclass", "");
                        ma.put("address2", list.get(i).getAddress2());
                        ma.put("contacts", list.get(i).getContacts());
                        ma.put("telephone", list.get(i).getTelephone());
                        ma.put("cellPhoneNum", list.get(i).getCellPhoneNum());
                        ma.put("website", list.get(i).getWebsite());
                        ma.put("email", list.get(i).getEmail());
                        ma.put("industryclassName", list.get(i).getIndustryclassName());
                        ma.put("custBank", list.get(i).getCustBank());
                        ma.put("bankArea", list.get(i).getBankArea());
                        ma.put("custAccount", list.get(i).getCustAccount());
                        ma.put("bankCode", list.get(i).getBankCode());

                        // 查询 所在母公司
                        int finalI = i;
                        List<Customer> mu = list.stream().filter(cus -> StringUtils.isNotBlank(cus.getUniqueCodeCcus()) && cus.getUniqueCodeCcus().equals(list.get(finalI).getUniqueCode())).collect(Collectors.toList());
                        if (mu.size() > 0) {
                            ma.put("uniqueCodeCcus", mu.get(0).getCustName());
                        }
                        // 查询 所在供应商
                        if (StringUtils.isNotBlank(list.get(finalI).getUniqueCodeSupplier())) {
                            List<Supplier> sup = suplist.stream().filter(su -> su.getUniqueCode().equals(list.get(finalI).getUniqueCodeSupplier())).collect(Collectors.toList());
                            if (sup.size() > 0) {
                                ma.put("uniqueCodeSupplier", sup.get(0).getCustName());
                            }
                        }
                        // 分割 省市区
                        if (StringUtils.isNotBlank(list.get(i).getProvince())) {
                            String a = "";
                            for (int j = 0; j < list.get(i).getProvince().split(",").length; j++) {
                                int finalJ = j;
                                int finalI1 = i;
                                List<SysZone> collect = zonelist.stream().filter(z -> z.getId().equals(list.get(finalI1).getProvince().split(",")[finalJ])).collect(Collectors.toList());
                                if (collect.size() > 0) {
                                    a += collect.get(0).getZoneName() + "/";
                                }
                            }
                            ma.put("province", StringUtils.isNotBlank(a) ? a.substring(0, a.length() - 1) : a);
                        }
                        // 分类名称
                        List<CustomerClass> clalist = classlist.stream().filter(cla -> cla.getUniqueCustclass().equals(list.get(finalI).getUniqueCustclass())).collect(Collectors.toList());
                        if(clalist.size()>0){
                            ma.put("uniqueCustclass", clalist.get(0).getCusCclassName());
                        }
                        listmap.add(ma);
                    }
                    map.put("all", listmap);
                    return Mono.just(map);
                })
                .map(o -> R.ok().setResult(map.get("all")));
    }

    /**
     * 导入客户信息
     *
     * @param filePartParm
     * @return
     * @throws Exception
     */
    @Transactional
    @PostMapping("/importCus")
    public Mono<R> importCus(@RequestPart("file") FilePart filePartParm, String userid) throws Exception {
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
                    String[] str = new String[]{"客户编码", "客户全称", "客户简称", "税号", "分类名称","母公司","对应供应商", "省","市","区", "详细地址", "电话", "手机", "网站", "EMail", "行业性质", "开户银行", "开户地", "银行账户", "银行行号","联系人","价格级次","税率",""};
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
                    mapArr.put("code", "200");
                    mapArr.put("error", "");
                    return customerRepository.findAll()
                            .collectList()
                            .flatMap(list -> {
                                mapArr.put("list", list);   // 客户信息list
                                return customerClassRepository.findAll().collectList().flatMap(cla -> {
                                    mapArr.put("classlist", cla); // 客户分类list
                                    return supplierRepository.findAllByFlag("1").collectList().flatMap(suplist->{
                                        mapArr.put("suplist", suplist); // 供应商list
                                        return client.sql("SELECT * from sys_zone").fetch().all().collectList().flatMap(zonelist->{
                                            mapArr.put("zonelist", zonelist); // 省市区
                                            return Mono.just(mapArr);
                                        });
                                    });
                                });
                            });
                })
                // 判断 表头是否合法
                .flatMap(mapArr -> {
                    // 科目list
                    List<Customer> list = (List<Customer>) mapArr.get("list");
                    // 文件list
                    List<Object[]> excellist = (List<Object[]>) mapArr.get("excellist");
                    List<CustomerClass> classlist = (List<CustomerClass>) mapArr.get("classlist");

                    List<Supplier> suplist = (List<Supplier>) mapArr.get("suplist");
                    List<SysZone> zonelist = (List<SysZone>) mapArr.get("zonelist");
                    List<Customer> cuslist = new ArrayList<>();

                    // 如果小于5 说明表头不对
                    if (column.get()<5 || column.get()>23) {
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
                        // 客户编码是否重复S
                        if(StringUtils.isNotBlank(excellist.get(finalI)[0].toString().trim())) {
                            List<Customer> collect = list.stream().filter(customer -> customer.getCustCode().equals(excellist.get(finalI)[0].toString().trim())).collect(Collectors.toList());
                            if (collect.size() > 0) {
                                errorText.add("客户编码与当前已存在档案重复");
                                obj[a]=errorText.toString();
                            }
                        }else{
                            errorText.add("客户编码不能为空");
                            obj[a]=errorText.toString();
                        }
                        // 客户全称
                        if(StringUtils.isNotBlank(excellist.get(finalI)[1].toString().trim())) {
                            List<Customer> collect2 = list.stream().filter(customer -> customer.getCustName().equals(excellist.get(finalI)[1].toString().trim())).collect(Collectors.toList());
                            if (collect2.size() > 0) {
                                errorText.add("客户全称与当前已存在档案重复");
                                obj[a]=errorText.toString();
                            }
                        }else{
                            errorText.add("客户全称不能为空");
                            obj[a]=errorText.toString();
                        }
                        // 客户简称
                        if(excellist.get(finalI)[2]!=null&&StringUtils.isNotBlank(excellist.get(finalI)[2].toString().trim())) {
                            List<Customer> collect3 = list.stream().filter(customer -> customer.getCustAbbname().equals(excellist.get(finalI)[2].toString().trim())).collect(Collectors.toList());
                            if (collect3.size() > 0) {
                                errorText.add("客户简称与当前已存在档案重复");
                                obj[a]=errorText.toString();
                            }
                        }
                        // 税号
                        if(excellist.get(finalI)[3]!=null&&StringUtils.isNotBlank(excellist.get(finalI)[3].toString().trim())) {
                            List<Customer> collect4 = list.stream().filter(customer -> customer.getCustSregcode().equals(excellist.get(finalI)[3].toString().trim())).collect(Collectors.toList());
                            if (collect4.size() > 0) {
                                errorText.add("客户税号与当前已存在档案重复");
                                obj[a]=errorText.toString();
                            }
                        }

                        // 母公司
                        if(excellist.get(finalI)[5]!=null&&StringUtils.isNotBlank(excellist.get(finalI)[5].toString().trim())) {
                            List<Customer> collect4 = list.stream().filter(customer -> customer.getCustName().equals(excellist.get(finalI)[5].toString().trim())).collect(Collectors.toList());
                            if (collect4.size() == 0) {
                                errorText.add("母公司在档案中不存在");
                                obj[a]=errorText.toString();
                            }
                        }
                        // 对应供应商
                        if(excellist.get(finalI)[6]!=null&&StringUtils.isNotBlank(excellist.get(finalI)[6].toString().trim())) {
                            List<Supplier> collect4 = suplist.stream().filter(customer -> customer.getCustName().equals(excellist.get(finalI)[6].toString().trim())).collect(Collectors.toList());
                            if (collect4.size() == 0) {
                                errorText.add("对应供应商在档案中不存在");
                                obj[a]=errorText.toString();
                            }
                        }
                        if(column.get()>5){
                            // 银行账户
                            if(excellist.get(finalI)[14]!=null&&StringUtils.isNotBlank(excellist.get(finalI)[14].toString().trim())) {
                                List<Customer> collect4 = list.stream().filter(customer -> customer.getCustAccount().equals(excellist.get(finalI)[14].toString().trim())).collect(Collectors.toList());
                                if (collect4.size() > 0) {
                                    errorText.add("银行账户与当前已存在档案重复");
                                    obj[a]=errorText.toString();
                                }
                            }
                        }

                        if(errorText.size()==0){
                            Customer cus = new Customer();
                            int finalI2 = i;
                            if(excellist.get(finalI)[4]!=null&&StringUtils.isNotBlank(excellist.get(finalI2)[4].toString().trim())){
                                List<CustomerClass> collect1 = classlist.stream().filter(cla -> cla.getCusCclassName().equals(excellist.get(finalI2)[4].toString().trim())).collect(Collectors.toList());
                                if (collect1.size() > 0) { cus.setUniqueCustclass(collect1.get(0).getUniqueCustclass()); }else{
                                    List<CustomerClass> collect2 = classlist.stream().filter(cla -> cla.getCusClass().equals("9999")).collect(Collectors.toList());
                                    if(collect2.size()>0){cus.setUniqueCustclass(collect2.get(0).getUniqueCustclass());}
                                }
                            }else{
                                cus.setUniqueCustclass(classlist.get(0).getUniqueCustclass());
                            }

                            String province="";
                            String city="";
                            String area="";
                            List<String> zoneStr=new ArrayList<>();
                            if(excellist.get(finalI)[7]!=null&&StringUtils.isNotBlank(excellist.get(finalI)[7].toString().trim())){
                                List<SysZone> collect = zonelist.stream().filter(zone -> zone.getZoneName().equals(excellist.get(finalI)[7].toString().trim())).collect(Collectors.toList());
                                if(collect.size()>0){
                                    province=collect.get(0).getZoneName();
                                    zoneStr.add(collect.get(0).getId());
                                }
                            }
                            if(excellist.get(finalI)[8]!=null&&StringUtils.isNotBlank(excellist.get(finalI)[8].toString().trim())){
                                List<SysZone> collect = zonelist.stream().filter(zone -> zone.getZoneName().equals(excellist.get(finalI)[8].toString().trim())).collect(Collectors.toList());
                                if(collect.size()>0){
                                    city=collect.get(0).getZoneName();
                                    zoneStr.add(collect.get(0).getId());
                                }
                            }
                            if(excellist.get(finalI)[9]!=null&&StringUtils.isNotBlank(excellist.get(finalI)[9].toString().trim())){
                                List<SysZone> collect = zonelist.stream().filter(zone -> zone.getZoneName().equals(excellist.get(finalI)[9].toString().trim())).collect(Collectors.toList());
                                if(collect.size()>0){
                                    area=collect.get(0).getZoneName();
                                    zoneStr.add(collect.get(0).getId());
                                }
                            }

                            cus.setUniqueCode(IdUtil.objectId())
                                    .setCustCode(excellist.get(i)[0].toString().trim())
                                    .setCustName(excellist.get(i)[1].toString().trim())
                                    .setCustAbbname(StringUtils.isNotBlank(excellist.get(i)[2].toString()) ? excellist.get(i)[2].toString().trim() : excellist.get(i)[1].toString().trim())
                                    .setCustSregcode(excellist.get(i)[3].toString().trim())
                                    .setProvince(province)
                                    .setCity(city)
                                    .setArea(area)
                                    .setPayType("QT")
                                    .setZone(zoneStr.size()==3?zoneStr.toString():"")
                                    .setAddress2(excellist.get(i)[10]==null?"":excellist.get(i)[10].toString().trim())
                                    .setTelephone(excellist.get(i)[11]==null?"":excellist.get(i)[11].toString().trim())
                                    .setCellPhoneNum(excellist.get(i)[12]==null?"":excellist.get(i)[12].toString().trim())
                                    .setWebsite(excellist.get(i)[13]==null?"":excellist.get(i)[13].toString().trim())
                                    .setEmail(excellist.get(i)[14]==null?"":excellist.get(i)[14].toString().trim())
                                    .setIndustryclassName(excellist.get(i)[15]==null?"":excellist.get(i)[15].toString().trim())
                                    .setCustBank(excellist.get(i)[16]==null?"":excellist.get(i)[16].toString().trim())
                                    .setBankArea(excellist.get(i)[17]==null?"":excellist.get(i)[17].toString().trim())
                                    .setCustAccount(excellist.get(i)[18]==null?"":excellist.get(i)[18].toString().trim())
                                    .setBankCode(excellist.get(i)[19]==null?"":excellist.get(i)[19].toString().trim())
                                    .setContacts(excellist.get(i)[20]==null?"":excellist.get(i)[20].toString().trim())
                                    .setTaxRate(excellist.get(i)[22]==null?"":excellist.get(i)[22].toString().trim())
                                    .setManageType("1")
                                    .setFlag("1")
                                    .setBeiyong1(userid)
                                    .setCustDevdate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

                            String priceLevel="协议价";
                            if(excellist.get(i)[21]!=null && StrUtil.isNotBlank(excellist.get(i)[21].toString().trim())){
                                switch (excellist.get(i)[21].toString().trim()){
                                    case "协议价":
                                        priceLevel="协议价";
                                        break;
                                    case "零售价":
                                        priceLevel="零售价";
                                        break;
                                    case "一级客户价":
                                        priceLevel="一级客户价";
                                        break;
                                    case "二级客户价":
                                        priceLevel="二级客户价";
                                        break;
                                    case "三级客户价":
                                        priceLevel="三级客户价";
                                        break;
                                    case "四级客户价":
                                        priceLevel="四级客户价";
                                        break;
                                    case "五级客户价":
                                        priceLevel="五级客户价";
                                        break;
                                    case "六级客户价":
                                        priceLevel="六级客户价";
                                        break;
                                    case "七级客户价":
                                        priceLevel="七级客户价";
                                        break;
                                    case "八级客户价":
                                        priceLevel="一级客户价";
                                        break;
                                    default:
                                        priceLevel="协议价";
                                }
                                cus.setPriceLevel(priceLevel);
                            }
                            cuslist.add(cus);
                        }
                        else{ mapArr.put("code", "200");mapArr.put("error", "error");}
                    }

                    /***********************************重复数据***************************************/
                    // 进行去重
                    List<String> listStr=new ArrayList<>();
                    for (int i = 1; i < excellist.size(); i++) {
                        if(StrUtil.isNotBlank(excellist.get(i)[0].toString().trim())){
                            listStr.add(excellist.get(i)[0].toString().trim());
                        }
                        if(StrUtil.isNotBlank(excellist.get(i)[2].toString().trim())){
                            listStr.add(excellist.get(i)[2].toString().trim());
                        }
                        if(StringUtils.isNotBlank(excellist.get(i)[3].toString().trim())){
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
                    /**************************************END*************************************/

                    mapArr.put("list", cuslist);
                    mapArr.put("column", column.get());
                    return Mono.just(mapArr);
                })
                .flatMapMany(mapArr -> {
                    List<Customer> list = (List<Customer>) mapArr.get("list");
                    return mapArr.get("code").equals("401") ? Mono.just(mapArr) :list.size()==0?Mono.just(mapArr): customerRepository.saveAll(list).collectList().map(a->mapArr);
                })
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    /**
     * 修改客户状态
     * @param id
     * @param flag
     * @return
     */
    @PostMapping("/editCusFlag")
    public Mono<R>editCusFlag(String id,String flag){
        return customerRepository.editCusFlag(id,flag).then(Mono.just(R.ok()));
    }

    /**
     * 删除客户前判断是否已做凭证
     * @param cusUnique
     * @return
     */
    @PostMapping("/countAccVoucherByCusUnique")
    public Mono<R>deiCusByAccVoucher(String cusUnique){
        return customerRepository.countAccVoucherByCusUnique(cusUnique).map(o -> R.ok().setResult(o));
    }

    /**
     *
     * @param cusUnique
     * @return
     */
    @PostMapping("/countAccVoucherByCusUnique20")
    public Mono<R>deiCusByAccVoucher20(String cusUnique){
        return customerRepository.countAccVoucherByCusUnique20(cusUnique).map(o -> R.ok().setResult(o));
    }

    /**
     * 是否已做辅助核算
     * @param cusUnique
     * @return
     */
    @PostMapping("/countAccVoucherByCusUnique21")
    public Mono<R>deiCusByAccVoucher21(String cusUnique){
        return customerRepository.countAccVoucherByCusUnique21(cusUnique).map(o -> R.ok().setResult(o));
    }

    /**
     * 删除单个客户
     * @param id
     * @return
     */
    @PostMapping("/delCus")
    public Mono<R>deiCus(String id){
        return customerRepository.deleteById(id).then(Mono.just(R.ok()));
    }

    /**
     * 批量删除客户
     * @param id
     * @return
     */
    @PostMapping("/delCusArr")
    public Mono<R>delCusArr(String id){
        List<Customer> list = new ArrayList<>();
        for (int i = 0; i < id.split(",").length; i++) {
            list.add(new Customer().setId(id.split(",")[i]));
        }
        return customerRepository.deleteAll(list).then(Mono.just(R.ok()));
    }

    @PostMapping("/editIsDel")
    public Mono<R>editIsDel(@RequestBody Map map){
        String delName=map.get("delName").toString();
        List<String> list=Arrays.asList(map.get("id").toString().split(","));
        return customerRepository.editisDel("1",delName,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()),list).then(Mono.just(R.ok()));
    }

    @PostMapping("/recoverIsDel")
    public Mono<R>recoverIsDel(@RequestBody Map map){
        List<String> list=Arrays.asList(map.get("id").toString().split(","));
        return customerRepository.editisDel("0","","",list).then(Mono.just(R.ok()));
    }

    @PostMapping("/findAllByUniqueCustclass")
    public Mono<R>findAllByUniqueCustclass(String uniqueCustclass){
        return customerRepository.findAllByUniqueCustclass(uniqueCustclass).collectList().map(a->R.ok().setResult(a));
    }

    /**
     * 集团分配数据记录
     * @param accId
     * @return
     */
    @PostMapping("/findAllByAllotRecordCus")
    public Mono<R> findAllByAllotRecordCus(String accId){
        return customerRepository.findAllByAllotRecordCus(accId).collectList().map(a->R.ok().setResult(a));
    }

    @GetMapping("/findAllOrderByCustCode")
    public Mono<R> findAllOrderByCustCode(){
        return customerRepository.findAllOrderByCustCode().collectList().map(a->R.ok().setResult(a));
    }

    @GetMapping("/custCountAll")
    public Mono<R> CustCountAll(){
        return customerRepository.countAll().map(a->R.ok().setResult(a));
    }

    @GetMapping("/getCustAll")
    public Mono<R> getCustAll(){
        return customerRepository.findAll().collectList().map(a->R.ok().setResult(a));
    }

    /**
     * 账套新增申请导入
     * @param filePartParm
     * @return
     * @throws Exception
     * 权限orgPermiss：0手动添加，1自动分配
     * 所属供应商  暂时无法匹配【档案未完善】
     */
    @PostMapping("/applyImportCus")
    public Mono<R> applyImportCus(@RequestPart("file") FilePart filePartParm,@RequestPart("orgUnique") String orgUnique,
                                     @RequestPart("cusCodeType") String cusCodeType,@RequestPart("userName") String userName
            ,@RequestPart("orgPermiss") String orgPermiss,@RequestPart("tenantId") String tenantId) throws Exception {
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
            String[] str = new String[]{"客户编码", "客户全称", "客户简称", "税号", "分类名称","母公司","对应客户","国家", "省","市","区", "详细地址", "电话", "手机", "网站", "EMail", "行业性质", "开户银行", "开户地", "银行账户", "银行行号",""};
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
            // 判断账套-申请客户是否在分配表中
            return groupCustomerAccountRepository.findAllByCusAssignLeftJoinGroupCusDataAndTenantId(orgUnique,"1",tenantId).collectList()
            .flatMap(groupCustomerAccountList -> {
                // 集团客户信息
                return groupCustomerRepository.findAll().collectList()
                .flatMap(groupCusList->{
                    // 账套客户分类
                    return customerClassRepository.findAll().collectList()
                    .flatMap(cusClassList->{
                        // 国家表
                        return countryRepository.findAll().collectList()
                        .flatMap(countryList->{
                            // 客户编码规则
                            return groupFileEncodingRulesRepository.findByFileType("1-2")
                            .flatMap(obj->{
                                // 获取集团客户
                                return groupCustomerRepository.getCusCode().collectList()
                                .flatMap(cuscode->{
                                    // 账套客户
                                    return customerRepository.findAll().collectList()
                                    .flatMap(cusList->{
                                        // 账套供应商
                                        return supplierRepository.findAll().collectList()
                                        .flatMap(supList->{
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
                                            mapArr.put("cusClassList", cusClassList);
                                            mapArr.put("countryList", countryList);
                                            mapArr.put("cusList", cusList);
                                            mapArr.put("supList", supList);
                                            return client.sql("SELECT * from sys_zone").fetch().all().collectList().flatMap(zonelist->{
                                                mapArr.put("zonelist", zonelist); // 省市区
                                                return Mono.just(mapArr);
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
            // 账套客户分类
            List<CustomerClass> cusClassList= (List<CustomerClass>) mapArr.get("cusClassList");
            // 国家表
            List<SysCountry> countryList= (List<SysCountry>) mapArr.get("countryList");
            // 账套供应商
            List<Supplier> supAlllist= (List<Supplier>) mapArr.get("supList");
            // 账套客户
            List<Customer> cusAlllist= (List<Customer>) mapArr.get("cusList");
            // 文件list
            List<Object[]> excellist = (List<Object[]>) mapArr.get("excellist");
            List<GroupCustomer> cuslist = new ArrayList<>();
            List<GroupCustomer> cuslist2 = new ArrayList<>();
            List<SysZone> zonelist = (List<SysZone>) mapArr.get("zonelist");

            // 如果小于5 说明表头不对
            if (!excellist.get(0)[0].equals("客户编码") && !excellist.get(0)[0].equals("客户全称")) {
                mapArr.put("error", "导入模板表头不正确，请使用正确的模板进行导入");
                mapArr.put("code", "401");
                return Mono.just(mapArr);
            }
            // 判断是否重复
            for (int i = 1; i < excellist.size(); i++) {
                int a = column.get();
                Object[] obj = excellist.get(i);
                int finalI = i;
                List<String> errorText = new ArrayList<>();

                // 客户编码是否重复
                // 1手动编码 需校验。2自动
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
                    List<CustomerClass> collect1 = cusClassList.stream().filter(cla -> cla.getCusBend().equals("1")&& cla.getCusCclassName().equals(excellist.get(finalI2)[4].toString().trim())).collect(Collectors.toList());
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
                        List<CustomerClass> collect1 = cusClassList.stream().filter(cla -> cla.getCusBend().equals("1")&& cla.getCusCclassName().equals(excellist.get(finalI2)[4].toString().trim())).collect(Collectors.toList());
                        if (collect1.size() > 0) {
                            uniqueCustclass=collect1.get(0).getUniqueCustclass();
                        }
                    }
                    // 母公司
                    String uniqueCodeCcus="";
                    if(excellist.get(finalI)[5]!=null) {
                        List<Customer> collect = cusAlllist.stream().filter(customer -> StringUtils.isNotBlank(customer.getUniqueCodeCcus()) && customer.getUniqueCodeCcus().equals(excellist.get(finalI)[5].toString().trim())).collect(Collectors.toList());
                        if(collect.size()>0){
                            uniqueCodeCcus=collect.get(0).getUniqueCode();
                        }
                    }
                    // 对应供应商
                    String uniqueCodeSupplier="";
                    if(excellist.get(finalI)[6]!=null) {
                        List<Supplier> collect = supAlllist.stream().filter(customer -> StringUtils.isNotBlank(customer.getUniqueCodeCcus()) && customer.getUniqueCodeCcus().equals(excellist.get(finalI)[6].toString().trim())).collect(Collectors.toList());
                        if(collect.size()>0){
                            uniqueCodeCcus=collect.get(0).getUniqueCode();
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
                    String province="";
                    String city="";
                    String area="";
                    List<String> zoneStr=new ArrayList<>();
                    if(excellist.get(finalI)[7]!=null&&StringUtils.isNotBlank(excellist.get(finalI)[7].toString().trim())){
                        List<SysZone> collect = zonelist.stream().filter(zone -> zone.getZoneName().equals(excellist.get(finalI)[7].toString().trim())).collect(Collectors.toList());
                        if(collect.size()>0){
                            province=collect.get(0).getZoneName();
                            zoneStr.add(collect.get(0).getId());
                        }
                    }
                    if(excellist.get(finalI)[8]!=null&&StringUtils.isNotBlank(excellist.get(finalI)[8].toString().trim())){
                        List<SysZone> collect = zonelist.stream().filter(zone -> zone.getZoneName().equals(excellist.get(finalI)[8].toString().trim())).collect(Collectors.toList());
                        if(collect.size()>0){
                            city=collect.get(0).getZoneName();
                            zoneStr.add(collect.get(0).getId());
                        }
                    }
                    if(excellist.get(finalI)[9]!=null&&StringUtils.isNotBlank(excellist.get(finalI)[9].toString().trim())){
                        List<SysZone> collect = zonelist.stream().filter(zone -> zone.getZoneName().equals(excellist.get(finalI)[9].toString().trim())).collect(Collectors.toList());
                        if(collect.size()>0){
                            area=collect.get(0).getZoneName();
                            zoneStr.add(collect.get(0).getId());
                        }
                    }
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
                    .setZone(zoneStr.toString())
                    .setAddress2(excellist.get(i)[10]==null?"":excellist.get(i)[10].toString().trim())
                    .setTelephone(excellist.get(i)[11]==null?"":excellist.get(i)[11].toString().trim())
                    .setCellPhoneNum(excellist.get(i)[12]==null?"":excellist.get(i)[12].toString().trim())
                    .setWebsite(excellist.get(i)[13]==null?"":excellist.get(i)[13].toString().trim())
                    .setEmail(excellist.get(i)[14]==null?"":excellist.get(i)[14].toString().trim())
                    .setIndustryclassName(excellist.get(i)[15]==null?"":excellist.get(i)[15].toString().trim())
                    .setCustBank(excellist.get(i)[16]==null?"":excellist.get(i)[16].toString().trim())
                    .setBankArea(excellist.get(i)[17]==null?"":excellist.get(i)[17].toString().trim())
                    .setCustAccount(excellist.get(i)[18]==null?"":excellist.get(i)[18].toString().trim())
                    .setBankCode(excellist.get(i)[19]==null?"":excellist.get(i)[19].toString().trim())
                    .setManageType("1")
                    .setFlag("1")
                    .setCustDevdate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()))
                    .setOriginUnique(orgUnique).setOrgUnique(orgUnique).setTenantId(tenantId)
                    .setCtype("2").setApplyDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()))
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
                            .setOriginUnique(orgUnique).setOrgUnique(orgUnique).setTenantId(tenantId)
                            .setCtype("2").setApplyDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()))
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
            return mapArr.get("code").equals("401") ? Mono.just(mapArr) :list.size()==0?Mono.just(mapArr): groupCustomerRepository.saveAll(list).collectList().map(a->mapArr);
        })
        .collectList()
        .map(o -> R.ok().setResult(o));
    }

    @PostMapping("getCusDataAuthorData")
    public Mono<R> getCusDataAuthorData(@RequestBody Map map){
        String tableName=map.get("tableName").toString();
        String userId=map.get("userId").toString();
        String accId=map.get("accId").toString();
        String classVal=map.get("classVal").toString();
        Map<String, String> searchMap = ((HashMap<String,  String>) map.get("searchConditon"));
        return customerRepository.findByCusAuthData(tableName,accId,userId).collectList()
                .flatMap(list->{
                    if(StrUtil.isNotBlank(classVal)&&!classVal.equals("0")){
                        list=list.stream().filter(a->a.getUniqueCustclass().equals(classVal)).collect(Collectors.toList());
                    }
                    return Mono.just(list.stream().filter(item->{
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
                .map(a->R.ok().setResult(a)).defaultIfEmpty(R.ok().setResult(""));
    }

    @PostMapping("findByCustMaxNum")
    public Mono<R> findByCustMaxNum(@RequestBody FileEncodingRules obj){
        return customerRepository.findAll().collectList()
        .flatMap(stockNumList->{
            String newcode="";
            if(obj.getId()!=null){
                String showRules=StrUtil.isBlank(obj.getShowRules())?"":obj.getShowRules();
                // 判断表中前缀开头的编码 是否与编码规则一致
                List<String> collect = stockNumList.stream().filter(a ->StrUtil.isNotBlank(obj.getShowRules()) &&a.getCustCode().startsWith(obj.getShowRules())).map(Customer::getCustCode).collect(Collectors.toList());
                List<Integer> listInt=new ArrayList<>();
                if(collect.size()>0){
                    collect.forEach(a->{
                        // 不包含特殊符号【是否编码规则设置】
                        if(!NewStringUtil.isSpecialChar(a)){
                            listInt.add(Integer.valueOf(a.substring(obj.getShowRules().length())));
                        }else{
                            listInt.add(Integer.valueOf(a.split(obj.getShowRules())[1]));
                        }
                    });
                    List<Integer> list2 = new ArrayList<>();
                    for (int i = 0; i < 10000; i++) {
                        list2.add(i + 1);
                    }
                    // 查找科目编码 中间 不连续，得出最小值;等于0 说明到头了【最大99】
                    int minCode = list2.stream().filter(item -> !listInt.contains(item)).mapToInt(Integer::intValue).min().orElse(0);
                    newcode=showRules+String.format("%0"+obj.getSerialNumLength()+"d",minCode);
                }else{
                    List<CustomerMaxNumVo> listMaxNumVo=new ArrayList<>();
                    stockNumList.forEach(a->{
                        if(StrUtil.isNotBlank(a.getCustCode())&&NewStringUtil.isNumeric4(a.getCustCode())){
                            CustomerMaxNumVo maxNumVo=new CustomerMaxNumVo();
                            listMaxNumVo.add(maxNumVo.setCustCode(Integer.valueOf(a.getCustCode())));
                        }
                    });
                    if(stockNumList.size()==0 || listMaxNumVo.size()==0){
                        newcode="0001";
                    }else{
                        CustomerMaxNumVo maxNumVo = listMaxNumVo.stream().max(Comparator.comparing(CustomerMaxNumVo::getCustCode)).get();
                        newcode="0"+String.format("%03d",maxNumVo.getCustCode()+1);
                    }
                }
            }else{
                List<CustomerMaxNumVo> listMaxNumVo=new ArrayList<>();
                stockNumList.forEach(a->{
                    if(StrUtil.isNotBlank(a.getCustCode())&&NewStringUtil.isNumeric4(a.getCustCode())){
                        CustomerMaxNumVo maxNumVo=new CustomerMaxNumVo();
                        listMaxNumVo.add(maxNumVo.setCustCode(Integer.valueOf(a.getCustCode())));
                    }
                });
                if(stockNumList.size()==0 || listMaxNumVo.size()==0){
                    newcode="0001";
                }else{
                    CustomerMaxNumVo maxNumVo = listMaxNumVo.stream().max(Comparator.comparing(CustomerMaxNumVo::getCustCode)).get();
                    newcode="0"+String.format("%03d",maxNumVo.getCustCode()+1);
                }
            }
            return Mono.just(newcode);
        }) .map(a->R.ok().setResult(a));
    }

    @PostMapping("editUniqueCustclass")
    public Mono<R> editUniqueCustclass(String uniqueCustclass,String oldUniqueCustclass ){
        return customerRepository.editUniqueCustclass(uniqueCustclass,oldUniqueCustclass).then(Mono.just(R.ok()));
    }

    @PostMapping("batchEditCust")
    public Mono<R> batchEditCust(@RequestBody Map map){
        String uniqueCustclass=map.get("uniqueCustclass").toString();
        String zoneVal=map.get("zoneVal").toString();
        String custDept=map.get("custDept").toString();
        String custPsn=map.get("custPsn").toString();
        String priceLevel=map.get("priceLevel").toString();
        String taxRate=map.get("taxRate").toString();
        List<String>idlist=Arrays.asList(map.get("idlist").toString().split(","));

        StringBuffer sb=new StringBuffer("set");
        if(StrUtil.isNotBlank(uniqueCustclass)){
            sb.append(" unique_custclass='"+uniqueCustclass+"' ,");
        }else if(StrUtil.isNotBlank(zoneVal)){
            sb.append(" zone='"+zoneVal+"' ,");
        }else if(StrUtil.isNotBlank(custDept)){
            sb.append(" cust_dept='"+custDept+"' ,");
        }else if(StrUtil.isNotBlank(custPsn)){
            sb.append(" cust_psn='"+custPsn+"' ,");
        }else if(StrUtil.isNotBlank(priceLevel)){
            sb.append(" price_level='"+priceLevel+"' ,");
        }else if(StrUtil.isNotBlank(taxRate)){
            sb.append(" tax_rate='"+taxRate+"' ,");
        }
        String str=sb.substring(0,sb.length()-1);
        String idStr="";
        for (int i = 0; i <idlist.size(); i++) {
            idStr+="'"+idlist.get(i)+"',";
        }
        idStr=idStr.substring(0,idStr.length()-1);
        String sql="update customer "+str+" where id in ("+idStr+")";
        return client.sql(sql).fetch().rowsUpdated().then(Mono.just(R.ok()));
    }

    /**
     * 删除前判断是否做过 单据
     * @return
     */
    @PostMapping("delFindByStock")
    public Mono<R> delFindByStock(String custCode){
        return customerRepository.delFindByStock(custCode).map(R::ok);
    }

/**************************************************** 审计日志记录 ***************************************************************************/
    @PostMapping("/auditCustomerSave")
    public Mono<R> auditCustomerSave(@RequestBody AuditCustomer cus){
        // 保存修改前的
        if(StrUtil.isNotBlank(cus.getId())){
            cus.setId(null);
        }
        return auditCustomerRepository.save(cus).map(a->R.ok().setResult(a));
    }
}

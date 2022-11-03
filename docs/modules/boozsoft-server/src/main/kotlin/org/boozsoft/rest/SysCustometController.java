package org.boozsoft.rest;

import cn.hutool.core.util.IdUtil;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.boozsoft.domain.entity.*;
import org.boozsoft.repo.*;
import org.boozsoft.service.CustomerService;
import org.boozsoft.service.SysZoneService;
import org.boozsoft.util.XlsUtils3;
import org.springbooz.core.tool.utils.StringUtils;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName : CustometController
 * @Description : 客户信息(系统)
 * @Author : miao
 * @Date: 2021-04-01 09:25
 */
@Slf4j
@RestController
@RequestMapping("/sys/cusrtomer")
@Api(value = "客户信息(系统)", tags = "API系统：客户信息(系统)")
public class SysCustometController {
    @Autowired
    DatabaseClient client;
    @Autowired
    R2dbcEntityTemplate entityTemplate;
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    CountryRepository countryRepository;
    @Autowired
    SysTradeNatureRepository sysTradeNatureRepository;
    @Autowired
    CustomerService service;
    @Autowired
    CustomerRollBackRepository customerRollBackRepository;
    @Autowired
    SupplierRepository supplierRepository;
    @Autowired
    SysZoneRepository sysZoneRepository;
    @Autowired
    SysLogRepository logRepository;
    @Autowired
    SysZoneService zoneService;


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
        return countryRepository.findAll().collectList().map(R::page);
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

    @GetMapping("findAll")
    public Mono<R> findAll() {
        return customerRepository.findAllByOrderByCustCodeAsc().collectList().map(R::page);
    }

    // 导出时查询所有信息
    @GetMapping("exportExcelFindAllJSON")
    public Mono<R> exportExcelFindAllJSON() {
        Map map = new HashMap();
        map.put("all", ""); // 母公司
        map.put("gys", ""); // 供应商
        map.put("gsdz", "");// 公司地址
        return customerRepository.findAllByOrderByCustCodeAsc().collectList()
                .flatMap(cus -> {
                    map.put("all", cus);
                    return supplierRepository.findAll().collectList()
                            .flatMap(sup -> {
                                map.put("gys", sup);
                                return sysZoneRepository.findAll().collectList()
                                        .flatMap(zone -> {
                                            map.put("gsdz", zone);
                                            return Mono.just(map);
                                        });
                            });
                })
                .flatMap(m -> {
                    List<Customer> list = (List<Customer>) m.get("all");
                    List<Supplier> suplist = (List<Supplier>) m.get("gys");
                    List<SysZone> zonelist = (List<SysZone>) m.get("gsdz");

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

                        if (StringUtils.isNotBlank(list.get(finalI).getUniqueCodeSupplier())) {
                            // 查询 所在供应商
                            List<Supplier> sup = suplist.stream().filter(su -> su.getUniqueCode().equals(list.get(finalI).getUniqueCodeSupplier())).collect(Collectors.toList());
                            if (sup.size() > 0) {
                                ma.put("uniqueCodeSupplier", sup.get(0).getCustName());
                            }
                        }
                        if (StringUtils.isNotBlank(list.get(i).getProvince())) {
                            String a = "";
                            // 分割 省市区
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
                        listmap.add(ma);
                    }
                    map.put("all", listmap);
                    return Mono.just(map);
                })
                .map(o -> R.ok().setResult(map.get("all")));
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

    @PostMapping("/save")
    public Mono save(@RequestBody Customer customerMono, String accId, String userName) {
        Calendar date = Calendar.getInstance();
        String year = String.valueOf(date.get(Calendar.YEAR));
        String type = StringUtils.isBlank(customerMono.getId()) ? "10" : "1";
        String type2 = StringUtils.isBlank(customerMono.getId()) ? "新增" : "修改";

        // 系统操作员增加不同判断 集团档案分配管控权限表
        Mono add = Mono.just(customerMono)
                .map(item -> {
                    item.setFlag("1")
                            .setId(null)
                            .setUniqueCode(IdUtil.objectId())
                            .setCustDevdate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                    return item;
                })
                .flatMap(customerRepository::save)
                .map(o -> R.ok().setResult(o));

        Mono edit = Mono.just(customerMono).flatMap(customerRepository::save).map(o -> R.ok().setResult(o));
        return StringUtils.isBlank(customerMono.getId()) ? add : edit;
    }

    /**
     * 导入客户信息
     *
     * @param filePartParm
     * @return
     * @throws Exception
     */
    @PostMapping("/importCus")
    public Mono<R> importCus(@RequestPart("file") FilePart filePartParm, String username) throws Exception {
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

                    String[] str=new String[]{"客户编码", "客户名称", "客户简称", "税号","区划(省-市-区)", "详细地址", "电话", "手机", "网站", "EMail", "行业性质", "开户银行", "开户地", "银行账户", "银行行号"};
                    if(tempFilePath.getFileName().toString().indexOf("简约")!=-1){
                        str=new String[]{"客户编码", "客户名称", "税号"};
                    }
                    list = excelReader.getExcelObj(tempFilePath.toString(),str, 0);
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
                    mapArr.put("list", "");        // 客户信息list
                    mapArr.put("zonelist", "");    // 区划list
                    mapArr.put("error", "");
                    mapArr.put("code", "200");
                    return customerRepository.findAll()
                            .collectList()
                            .flatMap(list -> {
                                mapArr.put("list", list);
                                return zoneService.findAll().map(zone->{
                                    mapArr.put("zonelist", zone.getResult());
                                    return mapArr;
                                });
                            });
                })
                // 判断 表头是否合法
                .flatMap(mapArr -> {
                    // 科目list
                    List<Customer> list = (List<Customer>) mapArr.get("list");
                    // 文件list
                    List<Object[]> excellist = (List<Object[]>) mapArr.get("excellist");
                    List<SysZone> zonelist = (List<SysZone>) mapArr.get("zonelist");
                    // 判断表头是否符合
                    boolean columnsflg = true;
                    String[] a = new String[]{"客户编码", "客户名称", "客户简称", "税号","区划(省-市-区)", "详细地址", "电话", "手机", "网站", "EMail", "行业性质", "开户银行", "开户地", "银行账户", "银行行号"};
                    if(tempFilePath.getFileName().toString().indexOf("简约")!=-1){
                        a=new String[]{"客户编码", "客户名称", "税号"};
                    }
                    try {
                        for (int i = 0; i < a.length; i++) {
                            if (!excellist.get(0)[i].equals(a[i])) {
                                columnsflg = false;
                            }
                        }
                    } catch (Exception e) {
                        // 报错只有一种可能，就是表头不对
                        columnsflg = false;
                    }
                    if (!columnsflg) {
                        mapArr.put("error", "导入模板表头不正确");
                        mapArr.put("code", "401");
                    }

                    List<Customer> cuslist = new ArrayList<>();
                    // 判断是否重复
                    for (int i = 1; i < excellist.size(); i++) {
                        int finalI = i;
                        // 客户编码是否重复
                        List<Customer> collect = list.stream().filter(customer -> customer.getCustCode().equals(excellist.get(finalI)[0].toString())).collect(Collectors.toList());
                        if (collect.size() > 0) {
                            mapArr.put("code", "401");
                            mapArr.put("error", "【" + excellist.get(i)[0] + "】客户编码与当前已存在档案重复，请修改后重新导入");
                            break;
                        }
                        // 客户全称
                        List<Customer> collect2 = list.stream().filter(customer -> customer.getCustName().equals(excellist.get(finalI)[1].toString())).collect(Collectors.toList());
                        if (collect2.size() > 0) {
                            mapArr.put("code", "401");
                            mapArr.put("error", "【" + excellist.get(i)[0] + "】客户全称与当前已存在档案重复，请修改后重新导入");
                            break;
                        }
                        // 客户简称
                        List<Customer> collect3 = list.stream().filter(customer -> customer.getCustAbbname().equals(excellist.get(finalI)[2].toString())).collect(Collectors.toList());
                        if (collect3.size() > 0) {
                            mapArr.put("code", "401");
                            mapArr.put("error", "【" + excellist.get(i)[0] + "】客户简称与当前已存在档案重复，请修改后重新导入");
                            break;
                        }
                        // 客户
                        List<Customer> collect4 = list.stream().filter(customer -> customer.getCustSregcode().equals(excellist.get(finalI)[3].toString())).collect(Collectors.toList());
                        if (collect4.size() > 0) {
                            mapArr.put("code", "401");
                            mapArr.put("error", "【" + excellist.get(i)[0] + "】客户税号与当前已存在档案重复，请修改后重新导入");
                            break;
                        }

                        Customer cus = new Customer();
                        if(tempFilePath.getFileName().toString().indexOf("简约")!=-1){
                            cus.setUniqueCode(IdUtil.objectId())
                                    .setCustCode(excellist.get(i)[0].toString().trim())
                                    .setCustName(excellist.get(i)[1].toString().trim())
                                    .setCustAbbname(excellist.get(i)[1].toString().trim())
                                    .setCustSregcode(excellist.get(i)[2].toString().trim())
                                    .setManageType("1")
                                    .setFlag("1")
                                    .setCustDevdate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                        }
                        else{
                            String zonstr="";
                            if(StringUtils.isNotBlank(excellist.get(i)[4].toString())){
                                for (int j = 0; j < excellist.get(i)[4].toString().split("-").length; j++) {
                                    int finalI1 = i;
                                    int finalJ = j;
                                    List<SysZone> collect1=null;
                                    if(j==0 || j==2){
                                        collect1 = zonelist.stream().filter(zon -> zon.getZoneName().equals(excellist.get(finalI1)[4].toString().split("-")[finalJ])).collect(Collectors.toList());
                                    }else if(j==1){
                                        collect1 = zonelist.stream().filter(zon -> zon.getZoneName().equals(excellist.get(finalI1)[4].toString().split("-")[finalJ]) && StringUtils.isBlank(zon.getProcode())).collect(Collectors.toList());
                                    }
                                    if(collect1.size()>0){
                                        zonstr+=collect1.get(0).getId()+",";
                                    }
                                }
                            }

                            cus.setUniqueCode(IdUtil.objectId())
                                    .setCustCode(excellist.get(i)[0].toString().trim())
                                    .setCustName(excellist.get(i)[1].toString().trim())
                                    .setCustAbbname(excellist.get(i)[2] == null ? excellist.get(i)[1].toString().trim() : excellist.get(i)[2].toString().trim())
                                    .setCustSregcode(excellist.get(i)[3].toString().trim())
                                    .setProvince(StringUtils.isNotBlank(zonstr)?zonstr.substring(0,zonstr.length()-1): excellist.get(i)[4].toString().trim())
                                    .setAddress2(excellist.get(i)[5].toString().trim())
                                    .setTelephone(excellist.get(i)[6].toString().trim())
                                    .setCellPhoneNum(excellist.get(i)[7].toString().trim())
                                    .setWebsite(excellist.get(i)[8].toString().trim())
                                    .setEmail(excellist.get(i)[9].toString().trim())
                                    .setIndustryclassName(excellist.get(i)[10].toString().trim())
                                    .setCustBank(excellist.get(i)[11].toString().trim())
                                    .setBankArea(excellist.get(i)[12].toString().trim())
                                    .setCustAccount(excellist.get(i)[13].toString().trim())
                                    .setBankCode(excellist.get(i)[14].toString().trim())
                                    .setManageType("1")
                                    .setFlag("1")
                                    .setCustDevdate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                        }
                        cuslist.add(cus);
                    }
                    mapArr.put("list", cuslist);
                    return Mono.just(mapArr);
                })
                .flatMapMany(mapArr -> {
                    List<Customer> list = (List<Customer>) mapArr.get("list");
                    return mapArr.get("code").equals("401") ? Mono.just(mapArr) : customerRepository.saveAll(list);
                })
                .collectList()
                .map(o -> R.ok().setResult(o));
    }
}

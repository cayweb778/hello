package org.boozsoft.rest;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.nacos.client.naming.utils.CollectionUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.boozsoft.domain.entity.*;
import org.boozsoft.domain.entity.account.ProjectCategory;
import org.boozsoft.domain.entity.account.ProjectItem;
import org.boozsoft.domain.entity.account.SysDepartment;
import org.boozsoft.domain.entity.account.SysPsn;
import org.boozsoft.domain.entity.codekemu.CodeKemu;
import org.boozsoft.domain.entity.share.project.base.Project;
import org.boozsoft.domain.entity.stock.StockSaleousing;
import org.boozsoft.domain.vo.SubjectInitialBalanceVo;
import org.boozsoft.domain.vo.VoucherBusCheckVo;
import org.boozsoft.repo.*;
import org.boozsoft.repo.accountInfo.AccountInfoRepository;
import org.boozsoft.repo.codekemu.CodeKemuRepository;
import org.boozsoft.repo.project.base.ProjectRepositoryBase;
import org.boozsoft.service.AccvoucherService;
import org.boozsoft.service.AccvoucherTemplateService;
import org.boozsoft.service.ProjectService;
import org.boozsoft.util.ReflectionUtil;
import org.boozsoft.util.XlsUtils3;
import org.boozsoft.utils.CollectOfUtils;
import org.springbooz.core.tool.result.R;
import org.springbooz.core.tool.utils.StringUtils;
import org.springbooz.datasource.r2dbc.service.R2dbcRouterLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ZeroCopyHttpOutputMessage;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple7;
import reactor.util.function.Tuple8;
import reactor.util.function.Tuples;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @ClassName :
 * @Description : 会计凭证
 * @Author : miao
 * @Date: 2021-04-01 09:25
 */
@Slf4j
@RestController
@RequestMapping("/accvoucher")
@Api(value = "凭证导入模板设置", tags = "API系统：凭证导入模板设置")

public class AccvoucherController {

    @Autowired
    AccvoucherTemplateService accvoucherTemplateService;
    @Autowired
    SysDepartmentRepository departmentRepository;
    @Autowired
    SysPsnRepository psnRepository;
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    SupplierRepository supplierRepository;
    @Autowired
    ProjectCategoryRepository projectCategoryRepository;
    @Autowired
    ProjectItemRepository projectItemRepository;
    @Autowired
    ProjectRepositoryBase projectRepository;
    @Autowired
    AccvoucherRepository accvoucherRepository;
    @Autowired
    AccvoucherDeleteRepository accvoucherDeleteRepository;
    @Autowired
    AccvoucherService service;
    @Autowired
    CodeKemuRepository codeKemuRepository;
    @Autowired
    ProjectService projectService;

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    ProjectCashRepository projectCashRepository;

    @Autowired
    AccountInfoRepository accountInfoRepository;

    @Autowired
    KmCashFlowRepository kmCashFlowRepository;

    @Autowired
    FuzhuHesuanRepository fuzhuHesuanRepository;

    public static void main(String[] args) {
        Map map = new HashMap<>();
        map.put(1, null);
        List<Map> list = new ArrayList<>();
        list.add(map);
        System.out.println(JSON.toJSONString(list, SerializerFeature.WriteMapNullValue));
    }

    @Transactional
    @PostMapping("/importAccvoucher2") // 检测
    public Mono<R> listOCR(@RequestPart("file") FilePart filePartParm, @RequestPart("templateInfo") String templateInfo) throws Exception {
        /**
         * 导入前检查思路 ：
         */
        // 唯一码 -- 模板类型 -- 是否为名称 -- 数据下标 -- 是否自定义编码
        String[] parameter = templateInfo.split("--");
        AtomicReference<String[]> titlesAR = new AtomicReference(); // 选择的导入模板列
        AtomicReference<String> thisDbNameAR = new AtomicReference(); // 当前账套名称
        AtomicReference<Boolean> checkPassed = new AtomicReference(true); // 检查是否通过
        Path tempFilePath = Files.createTempFile("", new String(filePartParm.filename().getBytes("ISO-8859-1"), "UTF-8"));
        return Mono.just(filePartParm).flatMap(files -> taskRepository.save(new Task().setCaozuoUnique("test001").setFunctionModule("凭证导入").setTime(DateUtil.now()).setState("1").setIyear(DateUtil.thisYear() + "").setMethod(DateUtil.thisMonth() + "").setMethod("导入")).map(entity1 -> files)).flatMap(filePart -> {
                    try {
                        return DataBufferUtils.write(filePart.content(), AsynchronousFileChannel.open(tempFilePath, StandardOpenOption.WRITE), 0).doOnComplete(() -> log.info("上传成功")).collectList().map(item -> tempFilePath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return Mono.just("");
                })// 上传到临时目录
                // 获取凭证导入动态表头
                .flatMap(item -> service.getTheHeaderOfTheCurrentlyImportedFile(parameter[0], parameter[1], parameter[2])).flatMap(item -> Mono.just(item.getT2()).doOnNext(tits -> {
                                    if (null == titlesAR.get()) titlesAR.set(new String[tits.size() - 1]);
                                    titlesAR.set(tits.toArray(new String[tits.size() - 1]));
                                })    // 系统存储
                                .map(tits -> item.getT1().toArray(new String[tits.size() - 1]))              // 客户业务
                )
                // 遍历导入excel内容
                .flatMap(titles -> { //根据用户自定义字段读
                    List<Object[]> list = null;
                    try {
                        list = new XlsUtils3(tempFilePath.toString()).getExcelObj(tempFilePath.toString(), titles, 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        assert tempFilePath != null;
                       /* try {
                            // Files.delete(tempFilePath);
                        } catch (IOException e) {
                            System.err.println("读取导入文件内容时发生错误: ---------下---------");
                            e.printStackTrace();
                        }*/
                    }
                    return Mono.just(list);
                })
                // 进行条件判断
                .flatMap(list -> {
                    Map mapArr = new HashMap();
                    mapArr.put("excellist", list);   // excel中内容
                    mapArr.put("error", "");
                    mapArr.put("code", "200");
                    if (null == list || list.size() == 0) {  // 数据不能为空
                        mapArr.put("error", "数据异常！表格不能为空或不是系统模板文件！");
                        mapArr.put("code", "404");
                        return Mono.just(mapArr);
                    }
                    // 遍历进行条件判断
                    String[] systemTitleNames = titlesAR.get();
                    Set<String> projects = new HashSet<>();
                    int voucherNumberIndex = -1;
                    int dateIndex = -1;
                    int mdIndex = -1;
                    int mcIndex = -1;
                    String thisImportYearStr = "";
                    for (int i = 0; i < list.size(); i++) {
                        Object[] rows = list.get(i);
                        if (i == 0) { // 凭证号列的下标
                            for (int j = 0; j < rows.length; j++) {
                                if (systemTitleNames[j].equals("凭证号")) voucherNumberIndex = j;
                                if (systemTitleNames[j].equals("制单日期")) dateIndex = j;
                                if (systemTitleNames[j].equals("借方金额")) mdIndex = j;
                                if (systemTitleNames[j].equals("贷方金额")) mcIndex = j;
                            }
                        }
                        StringBuilder rowCheckMsg = new StringBuilder("");
                        int defalutSize = rows.length;
                        for (int j = 0; j < defalutSize; j++) {
                            if (systemTitleNames[j].equals("制单日期") && rows[dateIndex] == "") {
                                rowCheckMsg.append("凭证号【" + rows[dateIndex] + " " + rows[voucherNumberIndex] + "】:制单日期不能为空!");
                            } else if (systemTitleNames[j].equals("制单日期") && StrUtil.isNotBlank(rows[dateIndex].toString())) {
                                try {
                                    DateUtil.parseDate(rows[dateIndex].toString());
                                    if (i == 0) {
                                        thisImportYearStr = rows[dateIndex].toString().substring(0, 4);
                                    }
                                } catch (Exception e) {
                                    rowCheckMsg.append("凭证号【" + rows[dateIndex] + " " + rows[voucherNumberIndex] + "】:制单日期无法识别!");
                                }
                            }
                            // 凭证编码 自定义
                            if (systemTitleNames[j].equals("凭证号") && (null == rows[voucherNumberIndex] || rows[voucherNumberIndex] == "") /*&& parms[4].equals("1")*/) {
                                rowCheckMsg.append("、制单日期不能为空!");
                            }
                            // 凭证摘要
                            if (systemTitleNames[j].equals("凭证摘要") && rows[j] == "") {
                                rowCheckMsg.append("、凭证摘要不能为空！");
                            }
                         /*   if (systemTitleNames[j].equals("凭证类型")) {//默认为记
                                if (rows[j] == "" || (!rows[j].equals("记") || !rows[j].equals("转"))) {
                                    rows[j] = "记";
                                }
                            }*/
                            // 制单人
                            if (systemTitleNames[j].equals("制单人") && rows[j] == "") {
                                rowCheckMsg.append("、制单人不能为空！");
                            }

                            if (systemTitleNames[j].equals("借方金额")) {
                                BigDecimal mdB = new BigDecimal("0");
                                BigDecimal mcB = new BigDecimal("0");
                                if (systemTitleNames[j].equals("借方金额") && rows[j] != "") {
                                    mdB = mdB.add(new BigDecimal(rows[j].toString()));
                                }
                                if (systemTitleNames[mcIndex].equals("贷方金额") && rows[j + 1] != "") {
                                    mcB = mcB.add(new BigDecimal(rows[mcIndex].toString()));
                                }
                                // 借贷方判断
                                if (mdB.doubleValue() == 0 && mcB.doubleValue() == 0 || (mdB.doubleValue() != 0 && mcB.doubleValue() != 0)) {
                                    rowCheckMsg.append("、借贷方金额不能都为空或都存在金额！");
                                }
                            }
                            ListUtil.setOrAppend(list, i, ArrayUtil.setOrAppend(rows, defalutSize, rowCheckMsg.toString()));
                            if (checkPassed.get() && StrUtil.isNotBlank(rowCheckMsg.toString())) checkPassed.set(false);
                            if (systemTitleNames[j].equals("项目大类编码") && null != rows[j] && NumberUtil.isNumber(rows[j].toString()) && (Integer.parseInt(rows[j].toString()) < 11)) {
                                projects.add(rows[j].toString());
                            }
                        }
                    }
                    String finalThisImportYearStr = thisImportYearStr;
                    return Mono.just("").map(s -> {
                        //所有科目与末级有效科目
                        Mono<HashSet<String[]>> all = codeKemuRepository.findAllByYear(finalThisImportYearStr).collectList().map(list1 -> new HashSet<>(getHashSetByKeMu(list1)));
                        Mono<HashSet<CodeKemu>> lastStage = codeKemuRepository.findAllByYearAndBend(finalThisImportYearStr).collectList().map(list1 -> new HashSet<>(list1));
                        // 辅助核算五项
                        Mono<List<SysPsn>> geSets = psnRepository.findAllPsnCodeOrPsnNameByFlag().collectList();
                        Mono<List<SysDepartment>> bmSets = departmentRepository.findAllDeptCodeOrDeptNameByFlag().collectList();
                        Mono<List<Customer>> khSets = customerRepository.findAllCustCodeOrCustNameByFlag().collectList();
                        Mono<List<Supplier>> gysSets = supplierRepository.findAllCustCodeOrCustNameByFlag().collectList();
                        Mono<List<Project>> proSets = projectRepository.findAllProjectCodeOrProjectNameByAll().collectList();
                                /*Mono<Map<String, Set<String>>> proMap = null;
                                if (projects.size() > 0) {
                                    proMap = Mono.just(new HashMap<String, Set<String>>()).map(maps ->
                                            Flux.fromIterable(projects)
                                                    .flatMap(proNum -> projectService.findByProjectCodeAndValue(proNum, parameter[2])
                                                            .doOnNext(sets -> maps.put(proNum, sets))
                                                    ).collectList().map(list1 -> maps)
                                    ).flatMap(a -> a);
                                } else {
                                    proMap = Mono.just(new HashMap<>());
                                }*/
                        // 现金流量是否必录
                        Mono<Map<String, Object>> xjMap = accountInfoRepository.findAll().collectList().flatMap(acclist -> {
                            if (acclist.size() > 0 && null != acclist.get(0)) {
                                thisDbNameAR.set(acclist.get(0).getAccCode());
                                // 先不考虑现金流量
                                //return service.queryAccountByAccId(acclist.get(0).getAccCode()).map(entity -> (null != entity.getIcashFlow() && entity.getIcashFlow().equals("1")) ? true : false);
                                return Mono.just(false);
                            }
                            return Mono.just(false);
                        }).flatMap(isTrue -> {
                            HashMap<String, Object> maps = new HashMap<>();
                            maps.put("XJCheck", isTrue);
                            maps.put("XJList", new HashSet<String>());
                            if (isTrue) {
                                return projectCashRepository.findByProjectAllOrderByCode().collectList().map(list1 -> {
                                    maps.put("XJList", new HashSet<>(list1));
                                    return maps;
                                });
                            }
                            return Mono.just(maps);
                        });
                        return Mono.zip(all, lastStage, geSets, bmSets, khSets, gysSets, proSets, xjMap);
                    }).flatMap(zips -> zips.map(many -> {
                        mapArr.put("codeSets", many);
                        return mapArr;
                    }));
                })
                // 对制单日期、凭证号进行去重汇总，作为唯一检索条件
                .flatMap(map -> {
                    List<Object[]> list = (List<Object[]>) map.get("excellist");
                    List<Map<String, Object>> ListMap = new ArrayList<>();
                    // 遍历进行条件判断
                    String[] systemTitleNames = titlesAR.get();
                    int dateIndex = 0;
                    int numberIndex = 0;
                    int csignIndex = 0;
                    int subjectNumIndex = 0;
                    int subjectNameIndex = 0;
                    int ndSIndex = 0; //借方数量
                    int ncSIndex = 0; //贷方数量
                    int unitPriceIndex = 0; //单价
                    int mdFIndex = 0; //借方汇率
                    int mcFIndex = 0; //借方汇率
                    int cdeptIdIndex = 0; //部门编码
                    int cpersonIdIndex = 0; //个人编码
                    int ccusIdIndex = 0; //客户编码
                    int csupIdIndex = 0; //供应商编码
                    int projectClassIdIndex = 0; //项目大类编码
                    int projectIdIndex = 0; //项目编码
                    int cashProjectIndex = 0; //现金流量项目编码
                    int nfratMdIndex = -1; //'原币借方金额'
                    int nfratMcIndex = -1; //'原币贷方金额'
                    // 辅助核算识别字段 名称 或 编码 boo
                    Boolean fx = parameter[2].equals("1");
                    // 所以科目 与 末级科目
                    Tuple8<HashSet<String[]>, HashSet<CodeKemu>, List<SysPsn>, List<SysDepartment>, List<Customer>, List<Supplier>, List<Project>, Map<String, Object>> beingSets = (Tuple8<HashSet<String[]>, HashSet<CodeKemu>, List<SysPsn>, List<SysDepartment>, List<Customer>, List<Supplier>, List<Project>, Map<String, Object>>) map.get("codeSets");
                    for (int i = 0; i < list.size(); i++) {
                        Object[] row = list.get(i);
                        // 获取日期与凭证号码的下表
                        if (i == 0) {
                            for (int j = 0; j < row.length - 1; j++) {
                                if (systemTitleNames[j].equals("制单日期")) dateIndex = j;
                                if (systemTitleNames[j].equals("凭证类型")) csignIndex = j;
                                if (systemTitleNames[j].equals("凭证号")) numberIndex = j;
                                if (systemTitleNames[j].equals("科目编码")) subjectNumIndex = j;
                                if (systemTitleNames[j].equals("科目名称")) subjectNameIndex = j;
                                if (systemTitleNames[j].equals("借方数量")) ndSIndex = j;
                                if (systemTitleNames[j].equals("贷方数量")) ncSIndex = j;
                                if (systemTitleNames[j].equals("单价")) unitPriceIndex = j;
                                if (systemTitleNames[j].equals("原币借方金额")) nfratMdIndex = j;
                                if (systemTitleNames[j].equals("原币贷方金额")) nfratMcIndex = j;

                                if (systemTitleNames[j].equals("借方汇率")) mdFIndex = j;
//                        if (systemTitleNames[j].equals("贷方汇率")) mcFIndex = j;

                                if (systemTitleNames[j].equals("部门编码")) cdeptIdIndex = j;
                                if (systemTitleNames[j].equals("个人编码")) cpersonIdIndex = j;
                                if (systemTitleNames[j].equals("客户编码")) ccusIdIndex = j;
                                if (systemTitleNames[j].equals("供应商编码")) csupIdIndex = j;
                                if (systemTitleNames[j].equals("项目大类编码")) projectClassIdIndex = j;
                                if (systemTitleNames[j].equals("项目编码")) projectIdIndex = j;

                                if (systemTitleNames[j].equals("现金流量项目编码")) cashProjectIndex = j;
                            }
                        }
                        String codeErrorStr = "凭证日期为【" + row[dateIndex] + " " + row[numberIndex] + "】号凭证对应年度会计科目编码:";
                        Map<String, Object> mm = new HashMap<>();
                        String tDate = DateUtil.formatDate(DateUtil.parseDate(row[dateIndex].toString()));
                        mm.put("time", tDate + ">>" + row[numberIndex]);
                        ListMap.add(mm);
                        // 校验科目编码数据库是否存在row[subjectNumIndex].toString()
                        Object codeValue = row[subjectNumIndex];
                        Map<String, String> mapArr = new HashMap<>();
                        StringBuilder rowCheckMsg = new StringBuilder(row[row.length - 1].toString());
                        if (null == codeValue || org.apache.commons.lang3.StringUtils.isBlank(codeValue.toString())) {
                            rowCheckMsg.append("、科目编码不能为空白！");
                        } else if (!checkImportKemuExist(beingSets.getT1(), codeValue, "2")) {
                            rowCheckMsg.append("、科目编码系统不存在！");
                        } else if (checkImportKemuExist(beingSets.getT1(), codeValue, "1")) {
                            rowCheckMsg.append("、科目编码系统已封存，请将软件中将该会计科目状态状态修改为正常！");
                        } else {
                            HashSet<CodeKemu> kemuList = beingSets.getT2();
                            CodeKemu thisKemu = getThisKemu(codeValue, kemuList);
                            if (null == thisKemu) {
                                rowCheckMsg.append("," + codeErrorStr + codeValue + "不是末级科目，请检查模板文件中的科目是否正确，请修改完后重新导入！");
                            } else {
                                // 现金流量校验
                                if ((boolean) beingSets.getT8().get("XJCheck") && (StringUtils.isNotBlank(thisKemu.getBcash()) || StringUtils.isNotBlank(thisKemu.getBbank()))) {
                                    String cash = setColValue(row, cashProjectIndex);
                                    Set<String> xjSets = (HashSet<String>) beingSets.getT8().get("XJList");
                                    if (StringUtils.isBlank(cash)) {
                                        rowCheckMsg.append(",现金流量项目编码为必填项！");
                                    } else if (StringUtils.isNotBlank(cash) && !xjSets.contains(cash)) {
                                        rowCheckMsg.append(",对应现金流量项目编码不存在，请修改模板文件后重新导入！");
                                    }
                                }
                                // 存在辅助核算
                                if (thisKemu.getBdept().equals("1")) {
                                    String dValue = (null == row[cdeptIdIndex] ? "" : row[cdeptIdIndex].toString());
                                    List<SysDepartment> deptCollect = beingSets.getT4().stream().filter(item -> (fx ? item.getDeptName().equals(dValue) : item.getDeptCode().equals(dValue))).collect(Collectors.toList());
                                    if (StringUtils.isBlank(dValue)) {
                                        rowCheckMsg.append(",部门" + (fx ? "名称" : "编码") + "列不能为空白！");
                                    } else if (deptCollect.size() == 0) {
                                        rowCheckMsg.append(",部门" + (fx ? "名称" : "编码") + "列值" + dValue + "系统不存在或已停用，请检查模板文件是否正确，或者在软件的基础档案中新增该辅助项！");
                                    } else {
                                        row[cdeptIdIndex] = deptCollect.get(0).getUniqueCode();
                                    }
                                }

                                if (thisKemu.getBperson().equals("1")) {
                                    String grValue = (null == row[cpersonIdIndex] ? "" : row[cpersonIdIndex].toString());
                                    List<SysPsn> psnCollect = beingSets.getT3().stream().filter(item -> (fx ? item.getPsnName().equals(grValue) : item.getPsnCode().equals(grValue))).collect(Collectors.toList());
                                    if (StringUtils.isBlank(grValue)) {
                                        rowCheckMsg.append(",个人" + (fx ? "名称" : "编码") + "列不能为空白！");
                                    } else if (psnCollect.size() == 0) {
                                        rowCheckMsg.append(",个人" + (fx ? "名称" : "编码") + "列值" + grValue + "系统不存在或已停用，请检查模板文件是否正确，或者在软件的基础档案中新增该辅助项！");
                                    } else {
                                        row[cpersonIdIndex] = psnCollect.get(0).getUniqueCode();
                                    }
                                }

                                if (thisKemu.getBcus().equals("1")) {
                                    String cValue = (null == row[ccusIdIndex] ? "" : row[ccusIdIndex].toString());
                                    List<Customer> customerCollect = beingSets.getT5().stream().filter(item -> (fx ? (item.getCustName().equals(cValue) || item.getCustAbbname().equals(cValue)) : item.getCustCode().equals(cValue))).collect(Collectors.toList());
                                    if (StringUtils.isBlank(cValue)) {
                                        rowCheckMsg.append(",客户" + (fx ? "名称" : "编码") + "列不能为空白！");
                                    } else if (customerCollect.size() == 0) {
                                        rowCheckMsg.append(",客户" + (fx ? "名称" : "编码") + "列值" + cValue + "系统不存在或已停用，请检查模板文件是否正确，或者在软件的基础档案中新增该辅助项！");
                                    } else {
                                        row[ccusIdIndex] = customerCollect.get(0).getUniqueCode();
                                    }
                                }

                                if (thisKemu.getBsup().equals("1")) {
                                    String gyValue = (null == row[csupIdIndex] ? "" : row[csupIdIndex].toString());
                                    List<Supplier> supCollect = beingSets.getT6().stream().filter(item -> (fx ? (item.getCustName().equals(gyValue) || item.getCustAbbname().equals(gyValue)) : item.getCustCode().equals(gyValue))).collect(Collectors.toList());
                                    if (StringUtils.isBlank(gyValue)) {
                                        rowCheckMsg.append(",供应商" + (fx ? "名称" : "编码") + "列不能为空白！");
                                    } else if (supCollect.size() == 0) {
                                        rowCheckMsg.append(",供应商" + (fx ? "名称" : "编码") + "列值" + gyValue + "系统不存在或已停用，请检查模板文件是否正确，或者在软件的基础档案中新增该辅助项！");
                                    } else {
                                        row[csupIdIndex] = supCollect.get(0).getUniqueCode();
                                    }
                                }

                                if (thisKemu.getBitem().equals("1")) {
                                    String pValue = (null == row[projectIdIndex] ? "" : row[projectIdIndex].toString());
                                    List<Project> proCollect = beingSets.getT7().stream().filter(item -> (fx ? (item.getProjectName().equals(pValue)) : item.getProjectCode().equals(pValue))).collect(Collectors.toList());
                                    if (StringUtils.isBlank(pValue)) {
                                        rowCheckMsg.append(codeErrorStr + codeValue + "为项目辅助核算科目！项目" + (fx ? "名称" : "编码") + "列不能为空白！");
                                    } else if (proCollect.size() == 0) {
                                        rowCheckMsg.append(codeErrorStr + codeValue + "为项目辅助核算科目！项目" + (fx ? "名称" : "编码") + "列值" + pValue + "系统不存在或已停用，请检查模板文件是否正确，或者在软件的基础档案中新增该辅助项！");
                                    } else {
                                        row[projectIdIndex] = proCollect.get(0).getUniqueCode();
                                    }
                                }

                                row[subjectNameIndex] = thisKemu.getCcodeName(); // 数据库对应科目名称赋值给科目
                                if (!parameter[1].equals("1")) { // 非标准跳过校验
                                    continue;
                                }
                                if (thisKemu.getBnum().equals("1")) {//存在计量单位 单据 与 数量必填
                                    String price = setColValue(row, unitPriceIndex);
                                    String jNum = setColValue(row, ndSIndex);
                                    String dNum = setColValue(row, ncSIndex);
                                    if (StringUtils.isBlank(price) || (StringUtils.isBlank(jNum) && StringUtils.isBlank(dNum)) || (StringUtils.isNotBlank(jNum) && StringUtils.isNotBlank(dNum))) {
                                        rowCheckMsg.append(",单价为必填项,借方数量与贷方数量二选一填入整数值！");
                                    } else if (StringUtils.isNotBlank(price) && (!NumberUtil.isNumber(price) || Double.parseDouble(price) < 0)) {
                                        rowCheckMsg.append(",单价必须为正数值！");
                                    } else if ((StringUtils.isNotBlank(jNum) && !NumberUtil.isNumber(jNum)) || (StringUtils.isNotBlank(dNum) && !NumberUtil.isNumber(dNum))) {
                                        rowCheckMsg.append(",借方数量或贷方数量为整数值！");
                                    }
                                }
                                if (thisKemu.getCurrency().equals("1")) {//存在外币核算外币 金额 与 汇率 为必填项
                                    String amountMd = setColValue(row, nfratMdIndex).replaceAll(",", "");
                                    String amountMc = setColValue(row, nfratMcIndex).replaceAll(",", "");
                                    String jNum = setColValue(row, mdFIndex);
                                    //String dNum = row[mcFIndex].toString();
                                    if ((StringUtils.isBlank(amountMd) && StringUtils.isBlank(amountMc)) || (StringUtils.isBlank(jNum) /*&& StringUtils.isBlank(dNum)) || (StringUtils.isNotBlank(jNum) && StringUtils.isNotBlank(dNum)*/)) {
                                        rowCheckMsg.append("," + "原币金额与外币汇率为必填项！");
                                    } else if ((StringUtils.isNotBlank(amountMd) && (!NumberUtil.isNumber(amountMd))) || (StringUtils.isNotBlank(amountMc) && (!NumberUtil.isNumber(amountMc))/* || Double.parseDouble(amount) < 0*/)) {
                                        rowCheckMsg.append("," + "原币金额必须为正整数值！");
                                    } else if ((StringUtils.isNotBlank(jNum) && (!NumberUtil.isNumber(jNum)/* || Double.parseDouble(jNum) < 0*/)) /*|| (StringUtils.isNotBlank(dNum) && (!NumberUtil.isNumber(dNum) || Double.parseDouble(dNum) < 0))*/) {
                                        rowCheckMsg.append("," + "汇率必须为正整数值！");
                                    }
                                }
                            }
                        }
                        ListUtil.setOrAppend(list, i, ArrayUtil.setOrAppend(row, row.length - 1, rowCheckMsg.toString()));
                        if (checkPassed.get() && StrUtil.isNotBlank(rowCheckMsg.toString())) checkPassed.set(false);
                    }
                    //
                    if (!checkPassed.get()) { // 未通过
                        String lastFilePath = WriteCheckInfoToExcel(tempFilePath.toString(), list, csignIndex);
                        return taskRepository.deleteByIyearAndFunctionModule(DateUtil.thisYear() + "", "凭证导入").thenReturn(list).map(o -> R.ok().setResult(CollectOfUtils.mapof("pass", checkPassed.get(), "path", lastFilePath)));
                    } else {// 已通过 开始导入
                        try {
                            Files.delete(tempFilePath);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return taskRepository.deleteByIyearAndFunctionModule(DateUtil.thisYear() + "", "凭证导入").thenReturn(list).map(o -> R.ok().setResult(CollectOfUtils.mapof("pass", checkPassed.get(), "path", "")));
                    }
                });

          /*//汇总之后年月list
          List<Map<Object, Object>> new_ListMap = new ArrayList<Map<Object, Object>>();
          Set arr = new HashSet();
          for (int i = 0; i < ListMap.size(); i++) {
            Map m = ListMap.get(i);
            if (arr.contains(m.get("time"))) {
              continue;
            }
            //添加key的值进set
            arr.add(m.get("time"));
            //储存进的集合
            new_ListMap.add(m);
          }
          map.put("timelist", new_ListMap);
          map.put("kemuList", beingSets.getT2());
          // 获取最大凭证编码 -》 校验凭证 -》 校验科目编码
          return service.queryAllYaerAndMonthMaxInoid()
              .flatMap(maxList -> {
                map.put("maxInoId", maxList);
                // 校验月份是否已经结账
                if (new_ListMap.size() > 0) { // 当为自定义编码时 校验数据库指定月是否存在
                  return service.checkPingZhengYearIsClose(new_ListMap).flatMap(item -> {
                    if (item.length > 0) {
                      map.put("error", "以下年月：【" + item[0] + "】 已完成结账操作,不能进行凭证导入！");
                      map.put("code", "404");
                    } else if (item.length == 0 && parameter[4].equals("1")) {
                      return service.checkPingZhengDbRepeatNumber(new_ListMap).flatMap(item1 -> {
                        if (item1.length > 0) {
                          map.put("error", "以下月份及凭证编码：【" + JSON.toJSONString(item1) + "】已存在！请改正或在导入面板选中编码生成！");
                          map.put("code", "404");
                        }
                        return Mono.just(map);
                      });
                    }
                    return Mono.just(map);
                  });
                }
                return Mono.just(map);
              });
        }).flatMap(
            map1 -> service.getAllKuaiJiQiJianInfoByAccId(thisDbNameAR.get()).doOnNext(map3 -> map1.put("kuaiJiQiJians", map3)).thenReturn(map1)
        )
        // 获取map数据,并向数据库增加
        .flatMapMany(map -> {
          String error = (String) map.get("error");
          String code = (String) map.get("code");
          if (StringUtils.isNotBlank(error)) {
            return Mono.just(R.ok().setResult(error).setCode(Long.valueOf(code)));
          }
          List<Map<Object, Object>> timelist = (List<Map<Object, Object>>) map.get("timelist");
          List<Object[]> excellist = (List<Object[]>) map.get("excellist");
          List<Accvoucher> coutnoIdList = (List<Accvoucher>) map.get("maxInoId");
          Map<String, Object> kjQjMap = (HashMap<String, Object>) map.get("kuaiJiQiJians");
          //List<SysPeriod> priedList = (List<SysPeriod>)kjQjMap.get("list");// 获取db所以时间信息
          HashSet<CodeKemu> kemuList = (HashSet<CodeKemu>) map.get("kemuList");
          List<Accvoucher> saveList = new ArrayList();
          String[] systemTitleNames = titlesAR.get();
          int dateIndex = -1;
          int numberIndex = -1;
          int idocIndex = -1; //附单数
          int cdigestIndex = -1; //凭证摘要
          int ccodeIndex = -1; //科目编码
          int ccodeNameIndex = -1; //科目名称
          int mdIndex = -1; //借方金额
          int mcIndex = -1; // 贷方金额
          int cashProjectIndex = -1; //现金流量项目编码
          int cdeptIdIndex = -1; //部门编码
          int cpersonIdIndex = -1; //个人编码
          int ccusIdIndex = -1; //客户编码
          int csupIdIndex = -1; //供应商编码
          int projectClassIdIndex = -1; //项目大类编码
          int projectIdIndex = -1; //项目编码
          int cbillIndex = -1; //制单人
          int ccheckIndex = -1; // 审核人
          int cdirectorIndex = -1; // 审核人
          int ccashierIndex = -1; //记账人
          int iflagIndex = -1;//  凭证状态
          int cbookIndex = -1; //出纳
          int csignIndex = -1; //凭证类型
          int settlementMethodIndex = -1; //结算方式编码
          int unitMeasurementIndex = -1; //计算单位
          int ndSIndex = -1; //借方数量
          int ncSIndex = -1; //贷方数量
          int unitPriceIndex = -1; //单价
          int foreignCurrencyIndex = -1; //外币币种
          int nfratMdIndex = -1; //'原币借方金额'
          int nfratMcIndex = -1; //'原币贷方金额'
          int mdFIndex = -1; //借方汇率
          int mcFIndex = -1; //借方汇率
          int pjIdIndex = -1; //票据号
          int pjDateIndex = -1; //票据日期
          int pjUnitNameIndex = -1; //票据日期
          int cdfine1Index = -1; //辅助项1
          String thisImportDate = DateUtil.today();
          int sameDateNumber = 0;
          String sameDateStr = "";
          for (int i = 0; i < timelist.size(); i++) {
            // 唯一码
            String uniqueCode = IdUtil.objectId();
            int inidNumber = 0;
            String thisCode = "";
            String coutnoId = "";
            if (!parameter[4].equals("1")) { //系统时
              // 获取动态字段下标
              String importDate = timelist.get(i).get("time").toString().split(">>")[0].substring(0, 7);
              if (!sameDateStr.equals(importDate)) {
                sameDateNumber = 0;
                sameDateStr = importDate;
              } else {
                sameDateNumber++;
              }
              coutnoId = getThisDateMaxVoucherIdon(coutnoIdList, timelist.get(i).get("time").toString());
              if ((Integer.valueOf(coutnoId) + sameDateNumber + 1) < 10) {
                coutnoId = "000" + (Integer.valueOf(coutnoId) + sameDateNumber + 1);
              } else if ((Integer.valueOf(coutnoId) + sameDateNumber + 1) >= 10 && (Integer.valueOf(coutnoId) + sameDateNumber + 1) < 100) {
                coutnoId = "00" + (Integer.valueOf(coutnoId) + sameDateNumber + 1);
              } else if ((Integer.valueOf(coutnoId) + sameDateNumber + 1) >= 100 && (Integer.valueOf(coutnoId) + sameDateNumber + 1) < 1000) {
                coutnoId = "0" + (Integer.valueOf(coutnoId) + sameDateNumber + 1);
              }
            }
            // 往数据库增加
            for (int j = 0; j < excellist.size(); j++) {
              Object[] row = excellist.get(j);
              // 获取日期与凭证号码的下表
              if (j == 0 && i == 0) { // 第一次拿到index
                for (int k = 0; k < row.length; k++) {
                  if (systemTitleNames[k].equals("制单日期")) dateIndex = k;
                  if (systemTitleNames[k].equals("凭证号")) numberIndex = k;
                  if (systemTitleNames[k].equals("附单据数")) idocIndex = k;
                  if (systemTitleNames[k].equals("凭证摘要")) cdigestIndex = k;
                  if (systemTitleNames[k].equals("科目编码")) ccodeIndex = k;
                  if (systemTitleNames[k].equals("科目名称")) ccodeNameIndex = k;
                  if (systemTitleNames[k].equals("借方金额")) mdIndex = k;
                  if (systemTitleNames[k].equals("贷方金额")) mcIndex = k;
                  if (systemTitleNames[k].equals("现金流量项目编码")) cashProjectIndex = k;

                  if (systemTitleNames[k].equals("部门编码")) cdeptIdIndex = k;
                  if (systemTitleNames[k].equals("个人编码")) cpersonIdIndex = k;
                  if (systemTitleNames[k].equals("客户编码")) ccusIdIndex = k;
                  if (systemTitleNames[k].equals("供应商编码")) csupIdIndex = k;
                  if (systemTitleNames[k].equals("项目大类编码")) projectClassIdIndex = k;
                  if (systemTitleNames[k].equals("项目编码")) projectIdIndex = k;

                  if (systemTitleNames[k].equals("制单人")) cbillIndex = k;
                  if (systemTitleNames[k].equals("审核人")) ccheckIndex = k;
                  if (systemTitleNames[k].equals("记账人")) cbookIndex = k;
                  if (systemTitleNames[k].equals("凭证状态")) iflagIndex = k;
                  if (systemTitleNames[k].equals("主管签字人")) cdirectorIndex = k;
                  if (systemTitleNames[k].equals("凭证类型")) csignIndex = k;
                  if (systemTitleNames[k].equals("出纳签字人")) ccashierIndex = k;

                  if (systemTitleNames[k].equals("结算方式编码")) settlementMethodIndex = k;
                  if (systemTitleNames[k].equals("计量单位")) unitMeasurementIndex = k;
                  if (systemTitleNames[k].equals("借方数量")) ndSIndex = k;
                  if (systemTitleNames[k].equals("贷方数量")) ncSIndex = k;
                  if (systemTitleNames[k].equals("外币币种")) foreignCurrencyIndex = k;
                  if (systemTitleNames[k].equals("原币借方金额")) nfratMdIndex = k;
                  if (systemTitleNames[k].equals("原币贷方金额")) nfratMcIndex = k;
                  if (systemTitleNames[k].equals("借方汇率")) mdFIndex = k;
                  if (systemTitleNames[k].equals("贷方汇率")) mcFIndex = k;
                  if (systemTitleNames[k].equals("单价")) unitPriceIndex = k;
                  if (systemTitleNames[k].equals("票据号")) pjIdIndex = k;
                  if (systemTitleNames[k].equals("票据日期")) pjDateIndex = k;
                  if (systemTitleNames[k].equals("结算单位名称")) pjUnitNameIndex = k;
                  if (systemTitleNames[k].equals("辅助项1")) cdfine1Index = k;
                }
              }
              // 逐步顺序添加  自定义凭证号时  或者 系统凭证号时  解决凭证号不存在
              String importDate = timelist.get(i).get("time").toString().split(">>")[0];
              if (importDate.equals(row[dateIndex]) &&
                  (*//*!parms[4].equals("1") || *//*timelist.get(i).get("time").toString().split(">>")[1].equals(row[numberIndex]))
              ) {
                String pingZhengNumber = (parameter[4].equals("1")) ? excellist.get(j)[numberIndex].toString().trim() : coutnoId.trim();
                //if (!parms[4].equals("1") && !pingZhengNumber.equals("")) break;
                String time = timelist.get(i).get("time").toString().replaceAll("-", "");
                // 当凭证编码为自定义时
                if (!thisCode.equals(pingZhengNumber)) {
                  inidNumber = 0;
                  thisCode = pingZhengNumber;
                } else {
                  inidNumber += 1;
                }
                String tYear = time.substring(0, 4);
                String tMonth = time.substring(4, 6);
                String tQj = ((HashMap<String, String>) kjQjMap.get(tYear)).get(tMonth);//数据库对应的期间 与年月
                Accvoucher accvoucher = new Accvoucher() //简版
                    .setUniqueCode(uniqueCode)
                    .setVouchUnCode(IdUtil.objectId())
                    .setIfrag(setVoucherStauts(setColValue(row, iflagIndex)))
                    .setCsign(setColValue(row, csignIndex))
                    .setIyear(StrUtil.trim(tYear))
                    .setImonth(StrUtil.trim(tMonth))
                    .setIyperiod(tYear + tQj) // 年月期间
                    .setIperiod(tQj) //期间
                    .setCbill(setColValue(row, cbillIndex))
                    .setDbillDate(StrUtil.trim(importDate))
                    .setInoId(StrUtil.trim(pingZhengNumber))
                    .setInid(String.valueOf(inidNumber) + 1)
                    .setIdoc(setVoucherIdoc(setColValue(row, idocIndex)))
                    .setCdigest(setColValue(row, cdigestIndex))
                    .setCcode(setColValue(row, ccodeIndex))
                    .setCcodeName(setColValue(row, ccodeNameIndex))
                    .setMd(NumberUtil.roundStr(checkNumberIsBlank(setColValue(row, mdIndex)), 2))
                    .setMc(NumberUtil.roundStr(checkNumberIsBlank(setColValue(row, mcIndex)), 2))
                    .setCsign(setColValue(row, csignIndex))
                    .setCdirector(setColValue(row, cdirectorIndex))
                    .setCashProject(setColValue(row, cashProjectIndex))
                    .setPjCsettle(setColValue(row, settlementMethodIndex))
                    .setPjId(setColValue(row, pjIdIndex))
                    .setPjDate(setColValue(row, pjDateIndex))
                    .setPjUnitName(setColValue(row, pjUnitNameIndex));
                // 条件添加
                if (StringUtils.isNotBlank(setColValue(row, ccheckIndex)))
                  accvoucher.setCcheck(setColValue(row, ccheckIndex)).setCcheckDate(thisImportDate);
                if (StringUtils.isNotBlank(setColValue(row, ccashierIndex)))
                  accvoucher.setCcashier(setColValue(row, ccashierIndex)).setCcashierDate(thisImportDate);
                if (StringUtils.isNotBlank(setColValue(row, cbookIndex)))
                  accvoucher.setCbook(setColValue(row, cbookIndex)).setIbook("1").setIbookDate(thisImportDate);
                CodeKemu thisKemu = getThisKemu(setColValue(row, ccodeIndex), kemuList);
                if (thisKemu.getBdept().equals("1"))
                  accvoucher.setCdeptId(setColValue(row, cdeptIdIndex));
                if (thisKemu.getBperson().equals("1"))
                  accvoucher.setCpersonId(setColValue(row, cpersonIdIndex));
                if (thisKemu.getBcus().equals("1"))
                  accvoucher.setCcusId(setColValue(row, ccusIdIndex));
                if (thisKemu.getBsup().equals("1"))
                  accvoucher.setCsupId(setColValue(row, csupIdIndex));
                if (thisKemu.getBitem().equals("1"))
                  accvoucher.setProjectClassId(setColValue(row, projectClassIdIndex))
                      .setProjectId(setColValue(row, projectIdIndex));
                if (parameter[1].equals("1")) {               //标准
                  accvoucher
                      .setNdS(setColValue(row, ndSIndex).replaceAll(",", ""))
                      .setNcS(setColValue(row, ncSIndex).replaceAll(",", ""))
                      .setNfratMd(NumberUtil.roundStr(checkNumberIsBlank(setColValue(row, nfratMdIndex).replaceAll(",", "")), 2))
                      .setNfratMc(NumberUtil.roundStr(checkNumberIsBlank(setColValue(row, nfratMcIndex).replaceAll(",", "")), 2))
                      .setCunitPrice(setColValue(row, unitPriceIndex))
                      .setMdF(setColValue(row, mdFIndex))
                  //.setMcF(setColValue(row, mcFIndex))
                  ;
                  if (false) {// 校验指定科目是否存在 币种 与 计量单位 作废
                    String ccode = accvoucher.getCcode();
                    accvoucher.setUnitMeasurement(setColValue(row, unitMeasurementIndex))
                        .setForeignCurrency(setColValue(row, foreignCurrencyIndex));
                  }
                }
                // 校验是否存在辅助项 获取存在的数量反射放值
                // if (systemTitleNames[k].equals("辅助项1"))cdfine1Index = k;
                if (systemTitleNames.toString().contains("辅助项")) { // 存在辅助项
                  List<Integer> keys = new ArrayList<>();
                  List<Integer> indexs = new ArrayList<>();
                  for (int k = 0; k < systemTitleNames.length; k++) {
                    String titleName = systemTitleNames[k];
                    String numStr = titleName.replace("辅助项", "");
                    if (titleName.startsWith("辅助项") && NumberUtil.isNumber(numStr)) {
                      keys.add(Integer.parseInt(numStr));
                      indexs.add(k);
                    }
                  }
                  if (keys.size() > 30) {
                    return Mono.just(R.ok().setResult("导入列限制：用户自定义辅助项列不得大于30个！！！").setCode(404L));
                  } else if (keys.size() > 0) {
                    accvoucher = service.modifyPingZhengEntityPropertyByAuxiliaryItem(accvoucher, excellist.get(j), keys, indexs);
                  }
                }
                saveList.add(accvoucher);
              }
            }
          }
          return Flux.just(new Accvoucher());
          //return accvoucherRepository.saveAll(saveList);

        });
        /*.collectList().flatMap(list ->
            taskRepository.deleteByIyearAndFunctionModule(DateUtil.thisYear() + "", "凭证导入").thenReturn(list)
        )
        .map(o -> R.ok().setResult(o));*/
    }


    @PostMapping("/downCheckFile")
    public Mono<Void> downCheckFile(@RequestBody Map map, ServerHttpResponse response) throws Exception {
        String fileName = map.get("parm").toString();
        return Mono.fromCallable(() -> {
            File file = new File(fileName);
            return file;
        }).flatMap(file -> downloadFile(response, file, "检查结果：" + fileName.split("-")[fileName.split("-").length - 1]));
    }


    @PostMapping("/delCheckFile")
    public Mono<R> delCheckFile(@RequestBody Map map) throws Exception {
        return Mono.just(map.get("parm").toString()).map(fileName -> {
            File file = new File(fileName);
            try {
                file.delete();
                return R.ok();
            } catch (Exception e) {
                return R.error();
            }
        });
    }

    private Mono<Void> downloadFile(ServerHttpResponse response, File file, String fileName) {
        ZeroCopyHttpOutputMessage zeroCopyHttpOutputMessage = (ZeroCopyHttpOutputMessage) response;
        try {
            response.getHeaders().set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=".concat(URLEncoder.encode(fileName, StandardCharsets.UTF_8.displayName())));
//      response.getHeaders().setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return zeroCopyHttpOutputMessage.writeWith(file, 0, file.length());
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedOperationException();
        } finally {
            // 最后删除
            file.delete();
        }
    }


    /**
     * 修改导入的临时Excel
     */
    private String WriteCheckInfoToExcel(String tempPath, List<Object[]> list, int index) {
        return new XlsUtils3(tempPath).changeExcelByList(tempPath, list, index);
    }

    @Autowired
    R2dbcRouterLoader r2dbcRouterLoader;

    @Transactional
    @PostMapping("/importAccvoucher") // 导入
    public Mono<R> listOCR2(@RequestPart("file") FilePart filePartParm, @RequestPart("templateInfo") String templateID) throws Exception {
        // 唯一码 -- 模板类型 -- 是否为名称 -- 数据下标 -- 是否自定义编码--账套accId--是否项目大类控制科目及项目
        String[] parms = templateID.split("--");
        AtomicReference<String[]> titlesAR = new AtomicReference(); // 选择的导入模板列
        AtomicReference<String> thisDbName = new AtomicReference(parms[5]); // 当前账套名称
        Path tempFilePath = Files.createTempFile("", new String(filePartParm.filename().getBytes("ISO-8859-1"), "UTF-8"));
        return Mono.just(filePartParm).flatMap(files -> taskRepository.save(new Task().setCaozuoUnique("test001").setFunctionModule("凭证导入").setTime(DateUtil.now()).setState("1").setIyear(DateUtil.thisYear() + "").setMethod(DateUtil.thisMonth() + "").setMethod("导入")).map(entity1 -> files)).flatMap(filePart -> {
                    try {
                        return DataBufferUtils.write(filePart.content(), AsynchronousFileChannel.open(tempFilePath, StandardOpenOption.WRITE), 0).doOnComplete(() -> log.info("上传成功")).collectList().map(item -> tempFilePath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return Mono.just("");
                })// 上传到临时目录
                // 获取凭证导入动态表头
                .flatMap(item -> service.getTheHeaderOfTheCurrentlyImportedFile(parms[0], parms[1], parms[2])).flatMap(item -> Mono.just(item.getT2()).doOnNext(tits -> {
                                    if (null == titlesAR.get()) titlesAR.set(new String[tits.size() - 1]);
                                    titlesAR.set(tits.toArray(new String[tits.size() - 1]));
                                })    // 系统存储
                                .map(tits -> item.getT1().toArray(new String[tits.size() - 1]))              // 客户业务
                )
                // 遍历导入excel内容
                .flatMap(titles -> { //根据用户自定义字段读
                    List<Object[]> list = null;
                    try {
                        list = new XlsUtils3(tempFilePath.toString()).getExcelObj(tempFilePath.toString(), titles, 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        assert tempFilePath != null;
                        try {
                            Files.delete(tempFilePath);
                        } catch (IOException e) {
                            System.err.println("读取导入文件内容时发生错误: ---------下---------");
                            e.printStackTrace();
                        }
                    }
                    return Mono.just(list);
                })
                // 进行条件判断
                .flatMap(list -> {
                    Map mapArr = new HashMap();
                    mapArr.put("excellist", list);   // excel中内容
                    mapArr.put("error", "");
                    mapArr.put("code", "200");
                    if (null == list || list.size() < 2) {  // 数据不能为空
                        mapArr.put("error", "数据异常！造成原因：表格为空、凭证少于两行或非系统模板文件！");
                        mapArr.put("code", "404");
                        return Mono.just(mapArr);
                    }
                    // 遍历进行条件判断
                    String[] systemTitleNames = titlesAR.get();
                    Set<String> projects = new HashSet<>();
                    int voucherNumberIndex = -1;
                    int dateIndex = -1;
                    int mdIndex = -1;
                    int mcIndex = -1;
                    String thisImportYearStr = "";
                    int pzNumber = 0;
                    String pzStr = "";
                    // 借贷统计
                    double mdSum = 0;
                    double mcSum = 0;
                    int ilen = list.size() - 1;
                    for (int i = 0; i < list.size(); i++) {
                        Object[] rows = list.get(i);
                        if (i == 0) { // 凭证号列的下标
                            for (int j = 0; j < rows.length; j++) {
                                if (systemTitleNames[j].equals("凭证号")) voucherNumberIndex = j;
                                if (systemTitleNames[j].equals("制单日期")) dateIndex = j;
                                if (systemTitleNames[j].equals("借方金额")) mdIndex = j;
                                if (systemTitleNames[j].equals("贷方金额")) mcIndex = j;
                            }
                        }
                        for (int j = 0; j < rows.length; j++) {
                            if (systemTitleNames[j].equals("制单日期") && rows[j] == "") {
                                mapArr.put("error", "凭证号【" + rows[dateIndex] + " " + rows[voucherNumberIndex] + "】:制单日期不能为空");
                                mapArr.put("code", "404");
                                return Mono.just(mapArr);
                            } else if (systemTitleNames[j].equals("制单日期") && i == 0) {
                                thisImportYearStr = rows[dateIndex].toString().substring(0, 4);
                            }

                            // 凭证编码 自定义
                            if (systemTitleNames[j].equals("凭证号") && (null == rows[voucherNumberIndex] || rows[voucherNumberIndex] == "") /*&& parms[4].equals("1")*/) {
                                mapArr.put("error", "制单日期【" + rows[dateIndex] + "】:凭证号不能为空");
                                mapArr.put("code", "404");
                                return Mono.just(mapArr);
                            } else if (systemTitleNames[j].equals("凭证号")) {
                                String pzValue = setColValue(rows, voucherNumberIndex);
                                BigDecimal mdB = new BigDecimal("0");
                                BigDecimal mcB = new BigDecimal("0");
                                if (rows[mdIndex] != "") {
                                    mdB = mdB.add(new BigDecimal(rows[mdIndex].toString()));
                                }
                                if (rows[mcIndex] != "") {
                                    mcB = mcB.add(new BigDecimal(rows[mcIndex].toString()));
                                }
                                // 借贷方判断
                                if (mdB.doubleValue() == 0 && mcB.doubleValue() == 0) {
                                    mapArr.put("error", "制单日期【" + rows[dateIndex] + "】:凭证号【" + pzValue + "】:借贷方不能都为空或0");
                                    mapArr.put("code", "404");
                                    return Mono.just(mapArr);
                                }
                                if (StrUtil.equals(pzStr, pzValue)) {
                                    pzNumber++;
                                    mdSum += mdB.doubleValue();
                                    mcSum += mcB.doubleValue();
                                } else {
                                    if (StrUtil.isNotBlank(pzStr) && pzNumber < 2) {
                                        mapArr.put("error", "凭证号【" + pzStr + "】分录数量不得少于两行！");
                                        mapArr.put("code", "404");
                                        return Mono.just(mapArr);
                                    }
                                    if (!NumberUtil.equals(new BigDecimal(mdSum).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(mcSum).setScale(2, BigDecimal.ROUND_HALF_UP))) {
                                        mapArr.put("error", "凭证号【" + pzStr + "】这张凭证金额借贷不平！");
                                        mapArr.put("code", "404");
                                        return Mono.just(mapArr);
                                    }
                                    pzNumber = 1;
                                    pzStr = pzValue;
                                    mdSum = mdB.doubleValue();
                                    mcSum = mcB.doubleValue();
                                    if (i == ilen && pzNumber == 1) {//处理最后一行为单条情况
                                        mapArr.put("error", "凭证号【" + pzStr + "】分录数量不得少于两行！");
                                        mapArr.put("code", "404");
                                        return Mono.just(mapArr);
                                    }
                                }
                            }
                            // 凭证摘要
                            if (systemTitleNames[j].equals("凭证摘要") && rows[j] == "") {
                                mapArr.put("error", "凭证号【" + rows[dateIndex] + " " + rows[voucherNumberIndex] + "】:凭证摘要不能为空");
                                mapArr.put("code", "404");
                                return Mono.just(mapArr);
                            }
                            if (systemTitleNames[j].equals("科目编码") && rows[j] == "") {
                                mapArr.put("error", "凭证号【" + rows[dateIndex] + " " + rows[voucherNumberIndex] + "】:科目编码不能为空");
                                mapArr.put("code", "404");
                                return Mono.just(mapArr);
                            }
                            if (systemTitleNames[j].equals("制单人") && rows[j] == "") {
                                mapArr.put("error", "凭证号【" + rows[dateIndex] + " " + rows[voucherNumberIndex] + "】:制单人不能为空");
                                mapArr.put("code", "404");
                                return Mono.just(mapArr);
                            }
                            if (systemTitleNames[j].equals("凭证类型")) {//默认为记
                                if (rows[j] == "" || (!rows[j].equals("记") || !rows[j].equals("转"))) {
                                    rows[j] = "记";
                                }
                            }
                            // 检查数量、单价、汇率、原币金额、借方金额、贷方金额数据格式是否正确

                            if (systemTitleNames[j].equals("项目大类编码") && null != rows[j] && NumberUtil.isNumber(rows[j].toString()) && (Integer.parseInt(rows[j].toString()) < 11)) {
                                projects.add(rows[j].toString());
                            }
                        }
                    }
                    String finalThisImportYearStr = thisImportYearStr;
                    return Mono.just("").map(s -> {
                        //所有科目与末级有效科目
                        Mono<HashSet<String[]>> all = codeKemuRepository.findAllByYear(finalThisImportYearStr).collectList().map(list1 -> new HashSet<>(getHashSetByKeMu(list1)));
                        Mono<HashSet<CodeKemu>> lastStage = codeKemuRepository.findAllByYearAndBend(finalThisImportYearStr).collectList().map(list1 -> new HashSet<>(list1));
                        // 辅助核算五项
                        Mono<List<SysPsn>> geSets = psnRepository.findAllPsnCodeOrPsnNameByFlag().collectList();
                        Mono<List<SysDepartment>> bmSets = departmentRepository.findAllDeptCodeOrDeptNameByFlag().collectList();
                        Mono<List<Customer>> khSets = customerRepository.findAllCustCodeOrCustNameByFlag().collectList();
                        Mono<List<Supplier>> gysSets = supplierRepository.findAllCustCodeOrCustNameByFlag().collectList();
                        Mono<List<Project>> proSets = projectRepository.findAllProjectCodeOrProjectNameByAll().collectList(); // 所有项目
                        Mono<List<ProjectItem>> proItemSets = projectItemRepository.findAllItemCodeOrItemNameByAll().collectList(); // 所有项目

                                /*
                                Mono<Map<String, Set<String>>> proMap = null;
                                if (projects.size() > 0) {
                                    proMap = Mono.just(new HashMap<String, Set<String>>()).map(maps ->
                                            Flux.fromIterable(projects)
                                                    .flatMap(proNum -> projectService.findByProjectCodeAndValue(proNum, parms[2])
                                                            .doOnNext(sets -> maps.put(proNum, sets))
                                                    ).collectList().map(list1 -> maps)
                                    ).flatMap(a -> a);
                                } else {
                                    proMap = Mono.just(new HashMap<>());
                                }*/
                        // 现金流量是否必录
                        Mono<Map<String, Object>> xjMap = accountInfoRepository.findAll().collectList().flatMap(acclist -> {
                            if (acclist.size() > 0 && null != acclist.get(0)) {
                                //thisDbName.set(acclist.get(0).getAccCode());
                                // 先不考虑现金流量
                                //return service.queryAccountByAccId(acclist.get(0).getAccCode()).map(entity -> (null != entity.getIcashFlow() && entity.getIcashFlow().equals("1")) ? true : false);
                                return Mono.just(false);
                            }
                            return Mono.just(false);
                        }).flatMap(isTrue -> {
                            HashMap<String, Object> maps = new HashMap<>();
                            maps.put("XJCheck", isTrue);
                            maps.put("XJList", new HashSet<String>());
                            if (isTrue) {
                                return projectCashRepository.findByProjectAllOrderByCode().collectList().map(list1 -> {
                                    maps.put("XJList", new HashSet<>(list1));
                                    return maps;
                                });
                            }
                            return Mono.just(maps);
                        });
                        return Tuples.of(Mono.zip(all, lastStage, geSets, bmSets, khSets, gysSets, xjMap), Mono.zip(proItemSets, proSets));
                    }).flatMap(zips -> zips.getT1().flatMap(many -> {
                        mapArr.put("codeSets", many);
                        return zips.getT2().flatMap(many2 -> {
                            mapArr.put("proSets", many2);
                            return Mono.just(mapArr);
                        });
                    }));
                })
                // 对制单日期、凭证号进行去重汇总，作为唯一检索条件
                .flatMap(map -> {
                    String error = (String) map.get("error");
                    if (StringUtils.isNotBlank(error)) {
                        Map<String, String> mapArr = new HashMap<>();
                        mapArr.put("error", error);
                        mapArr.put("code", "404");
                        return Mono.just(mapArr);
                    }
                    List<Object[]> list = (List<Object[]>) map.get("excellist");
                    List<Map<String, Object>> ListMap = new ArrayList<>();
                    // 遍历进行条件判断
                    String[] systemTitleNames = titlesAR.get();
                    int dateIndex = 0;
                    int numberIndex = 0;
                    int subjectNumIndex = 0;
                    int subjectNameIndex = 0;
                    int ndSIndex = 0; //借方数量
                    int ncSIndex = 0; //贷方数量
                    int unitPriceIndex = 0; //单价
                    int mdFIndex = 0; //借方汇率
                    int mcFIndex = 0; //借方汇率
                    int cdeptIdIndex = 0; //部门编码
                    int cpersonIdIndex = 0; //个人编码
                    int ccusIdIndex = 0; //客户编码
                    int csupIdIndex = 0; //供应商编码
                    int projectClassIdIndex = 0; //项目大类编码
                    int projectIdIndex = 0; //项目编码
                    int cashProjectIndex = 0; //现金流量项目编码
                    int nfratMdIndex = -1; //'原币借方金额'
                    int nfratMcIndex = -1; //'原币贷方金额'
                    // 辅助核算识别字段 名称 或 编码 boo
                    Boolean fx = parms[2].equals("1");
                    // 所以科目 与 末级科目
                    Tuple7<HashSet<String[]>, HashSet<CodeKemu>, List<SysPsn>, List<SysDepartment>, List<Customer>, List<Supplier>, Map<String, Object>> beingSets = (Tuple7<HashSet<String[]>, HashSet<CodeKemu>, List<SysPsn>, List<SysDepartment>, List<Customer>, List<Supplier>, Map<String, Object>>) map.get("codeSets");
                    Tuple2<List<ProjectItem>, List<Project>> prosSets = (Tuple2<List<ProjectItem>, List<Project>>) map.get("proSets");
                    for (int i = 0; i < list.size(); i++) {
                        Object[] row = list.get(i);
                        // 获取日期与凭证号码的下表
                        if (i == 0) {
                            for (int j = 0; j < row.length; j++) {
                                if (systemTitleNames[j].equals("制单日期")) dateIndex = j;
                                if (systemTitleNames[j].equals("凭证号")) numberIndex = j;
                                if (systemTitleNames[j].equals("科目编码")) subjectNumIndex = j;
                                if (systemTitleNames[j].equals("科目名称")) subjectNameIndex = j;
                                if (systemTitleNames[j].equals("借方数量")) ndSIndex = j;
                                if (systemTitleNames[j].equals("贷方数量")) ncSIndex = j;
                                if (systemTitleNames[j].equals("单价")) unitPriceIndex = j;
                                if (systemTitleNames[j].equals("原币借方金额")) nfratMdIndex = j;
                                if (systemTitleNames[j].equals("原币贷方金额")) nfratMcIndex = j;

                                if (systemTitleNames[j].equals("借方汇率")) mdFIndex = j;
//                        if (systemTitleNames[j].equals("贷方汇率")) mcFIndex = j;

                                if (systemTitleNames[j].equals("部门编码")) cdeptIdIndex = j;
                                if (systemTitleNames[j].equals("个人编码")) cpersonIdIndex = j;
                                if (systemTitleNames[j].equals("客户编码")) ccusIdIndex = j;
                                if (systemTitleNames[j].equals("供应商编码")) csupIdIndex = j;
                                if (systemTitleNames[j].equals("项目大类编码")) projectClassIdIndex = j;
                                if (systemTitleNames[j].equals("项目编码")) projectIdIndex = j;

                                if (systemTitleNames[j].equals("现金流量项目编码")) cashProjectIndex = j;
                            }
                        }
                        String codeErrorStr = "凭证日期为【" + row[dateIndex] + " " + row[numberIndex] + "】号凭证对应年度会计科目编码:";
                        Map<String, Object> mm = new HashMap<>();
                        String tDate = DateUtil.formatDate(DateUtil.parseDate(row[dateIndex].toString()));
                        mm.put("time", tDate + ">>" + row[numberIndex]);
                        ListMap.add(mm);
                        // 校验科目编码数据库是否存在row[subjectNumIndex].toString()
                        Object codeValue = row[subjectNumIndex];
                        Map<String, String> mapArr = new HashMap<>();
                        if (null == codeValue || org.apache.commons.lang3.StringUtils.isBlank(codeValue.toString())) {
                            mapArr.put("error", "凭证日期为【" + row[dateIndex] + "】:科目编码不能为空白！");
                            mapArr.put("code", "404");
                            return Mono.just(mapArr);
                        } else if (!checkImportKemuExist(beingSets.getT1(), codeValue, "2")) {
                            mapArr.put("error", codeErrorStr + codeValue + "不存在，请修改模板文件中的会计科目或在软件中新增该科目！");
                            mapArr.put("code", "404");
                            return Mono.just(mapArr);
                        } else if (checkImportKemuExist(beingSets.getT1(), codeValue, "1")) {
                            mapArr.put("error", codeErrorStr + codeValue + "已被封存，请将软件中将该会计科目状态状态修改为正常！");
                            mapArr.put("code", "404");
                            return Mono.just(mapArr);
                        } else {
                            HashSet<CodeKemu> kemuList = beingSets.getT2();
                            CodeKemu thisKemu = getThisKemu(codeValue, kemuList);
                            if (null == thisKemu) {
                                mapArr.put("error", codeErrorStr + codeValue + "不是末级科目，请检查模板文件中的科目是否正确，请修改完后重新导入！");
                                mapArr.put("code", "404");
                                return Mono.just(mapArr);
                            } else {
                                // 现金流量校验
                                if ((boolean) beingSets.getT7().get("XJCheck") && (StringUtils.isNotBlank(thisKemu.getBcash()) || StringUtils.isNotBlank(thisKemu.getBbank()))) {
                                    String cash = setColValue(row, cashProjectIndex);
                                    Set<String> xjSets = (HashSet<String>) beingSets.getT7().get("XJList");
                                    if (StringUtils.isBlank(cash)) {
                                        mapArr.put("error", codeErrorStr + codeValue + "为现金流量项目核算科目！现金流量项目编码为必填项！");
                                        mapArr.put("code", "404");
                                        return Mono.just(mapArr);
                                    } else if (StringUtils.isNotBlank(cash) && !xjSets.contains(cash)) {
                                        mapArr.put("error", codeErrorStr + codeValue + "为现金流量项目核算科目！对应现金流量项目编码不存在，请修改模板文件后重新导入！");
                                        mapArr.put("code", "404");
                                        return Mono.just(mapArr);
                                    }
                                }
                                // 存在辅助核算
                                if (thisKemu.getBdept().equals("1")) {
                                    String dValue = (null == row[cdeptIdIndex] ? "" : row[cdeptIdIndex].toString());
                                    List<SysDepartment> deptCollect = beingSets.getT4().stream().filter(item -> (fx ? item.getDeptName().equals(dValue) : item.getDeptCode().equals(dValue))).collect(Collectors.toList());
                                    if (StringUtils.isBlank(dValue)) {
                                        mapArr.put("error", codeErrorStr + codeValue + "为部门辅助核算科目！部门" + (fx ? "名称" : "编码") + "列不能为空白！");
                                        mapArr.put("code", "404");
                                        return Mono.just(mapArr);
                                    } else if (deptCollect.size() == 0) {
                                        mapArr.put("error", codeErrorStr + codeValue + "为部门辅助核算科目！部门" + (fx ? "名称" : "编码") + "列值" + dValue + "系统不存在或已停用，请检查模板文件是否正确，或者在软件的基础档案中新增该辅助项！");
                                        mapArr.put("code", "404");
                                        return Mono.just(mapArr);
                                    } else {
                                        row[cdeptIdIndex] = deptCollect.get(0).getUniqueCode();
                                    }
                                }

                                if (thisKemu.getBperson().equals("1")) {
                                    String grValue = (null == row[cpersonIdIndex] ? "" : row[cpersonIdIndex].toString());
                                    List<SysPsn> psnCollect = beingSets.getT3().stream().filter(item -> (fx ? item.getPsnName().equals(grValue) : item.getPsnCode().equals(grValue))).collect(Collectors.toList());
                                    if (StringUtils.isBlank(grValue)) {
                                        mapArr.put("error", codeErrorStr + codeValue + "为个人辅助核算科目！个人" + (fx ? "名称" : "编码") + "列不能为空白！");
                                        mapArr.put("code", "404");
                                        return Mono.just(mapArr);
                                    } else if (psnCollect.size() == 0) {
                                        mapArr.put("error", codeErrorStr + codeValue + "为个人辅助核算科目！个人" + (fx ? "名称" : "编码") + "列值" + grValue + "系统不存在或已停用，请检查模板文件是否正确，或者在软件的基础档案中新增该辅助项！");
                                        mapArr.put("code", "404");
                                        return Mono.just(mapArr);
                                    } else {
                                        row[cpersonIdIndex] = psnCollect.get(0).getUniqueCode();
                                    }
                                }

                                if (thisKemu.getBcus().equals("1")) {
                                    String cValue = (null == row[ccusIdIndex] ? "" : row[ccusIdIndex].toString());
                                    List<Customer> customerCollect = beingSets.getT5().stream().filter(item -> (fx ? (item.getCustName().equals(cValue) || item.getCustAbbname().equals(cValue)) : item.getCustCode().equals(cValue))).collect(Collectors.toList());
                                    if (StringUtils.isBlank(cValue)) {
                                        mapArr.put("error", codeErrorStr + codeValue + "为客户辅助核算科目！客户" + (fx ? "名称" : "编码") + "列不能为空白！");
                                        mapArr.put("code", "404");
                                        return Mono.just(mapArr);
                                    } else if (customerCollect.size() == 0) {
                                        mapArr.put("error", codeErrorStr + codeValue + "为客户辅助核算科目！客户" + (fx ? "名称" : "编码") + "列值" + cValue + "系统不存在或已停用，请检查模板文件是否正确，或者在软件的基础档案中新增该辅助项！");
                                        mapArr.put("code", "404");
                                        return Mono.just(mapArr);
                                    } else {
                                        row[ccusIdIndex] = customerCollect.get(0).getUniqueCode();
                                    }
                                }

                                if (thisKemu.getBsup().equals("1")) {
                                    String gyValue = (null == row[csupIdIndex] ? "" : row[csupIdIndex].toString());
                                    List<Supplier> supCollect = beingSets.getT6().stream().filter(item -> (fx ? (item.getCustName().equals(gyValue) || item.getCustAbbname().equals(gyValue)) : item.getCustCode().equals(gyValue))).collect(Collectors.toList());
                                    if (StringUtils.isBlank(gyValue)) {
                                        mapArr.put("error", codeErrorStr + codeValue + "为供应商辅助核算科目！供应商" + (fx ? "名称" : "编码") + "列不能为空白！");
                                        mapArr.put("code", "404");
                                        return Mono.just(mapArr);
                                    } else if (supCollect.size() == 0) {
                                        mapArr.put("error", codeErrorStr + codeValue + "为供应商辅助核算科目！供应商" + (fx ? "名称" : "编码") + "列值" + gyValue + "系统不存在或已停用，请检查模板文件是否正确，或者在软件的基础档案中新增该辅助项！");
                                        mapArr.put("code", "404");
                                        return Mono.just(mapArr);
                                    } else {
                                        row[csupIdIndex] = supCollect.get(0).getUniqueCode();
                                    }
                                }

                                if (thisKemu.getBitem().equals("1")) {
                                    String pValue = (null == row[projectIdIndex] ? "" : row[projectIdIndex].toString());
                                    if (parms[6].equals("1")) { // 校验项目大类
                                        String projectClassId = thisKemu.getProjectClassId();
                                        if (StrUtil.isBlank(projectClassId)) {
                                            mapArr.put("error", codeErrorStr + codeValue + "为项目辅助核算科目！项目所属科目项目大类ID数据库空白,请检查该科目编码！");
                                            mapArr.put("code", "404");
                                            return Mono.just(mapArr);
                                        } else if (StringUtils.isNotBlank(projectClassId) && prosSets.getT1().stream().filter(it -> it.getId().equals(projectClassId)).collect(Collectors.toList()).size() == 0) {
                                            mapArr.put("error", codeErrorStr + codeValue + "为项目辅助核算科目！项目" + (fx ? "名称" : "编码") + "" + pValue + "在对应的科目项目大类中不存在，请检查模板文件是否正确，或者在软件的基础档案中新增该辅助项！所属科目项目大类编码【" + projectClassId + "】");
                                            mapArr.put("code", "404");
                                            return Mono.just(mapArr);
                                        }
                                    }
                                    List<Project> proCollect = prosSets.getT2().stream().filter(item -> (fx ? (item.getProjectName().equals(pValue)) : item.getProjectCode().equals(pValue))).collect(Collectors.toList());
                                    if (StringUtils.isBlank(pValue)) {
                                        mapArr.put("error", codeErrorStr + codeValue + "为项目辅助核算科目！项目" + (fx ? "名称" : "编码") + "列不能为空白！");
                                        mapArr.put("code", "404");
                                        return Mono.just(mapArr);
                                    } else if (proCollect.size() == 0) {
                                        mapArr.put("error", codeErrorStr + codeValue + "为项目辅助核算科目！项目" + (fx ? "名称" : "编码") + "列值" + pValue + "系统不存在或已停用，请检查模板文件是否正确，或者在软件的基础档案中新增该辅助项！");
                                        mapArr.put("code", "404");
                                        return Mono.just(mapArr);
                                    } else {
                                        row[projectIdIndex] = proCollect.get(0).getUniqueCode();
                                    }
                                }
                                row[subjectNameIndex] = thisKemu.getCcodeName(); // 数据库对应科目名称赋值给科目
                                if (!parms[1].equals("1")) { // 非标准跳过校验
                                    continue;
                                }
                                if (thisKemu.getBnum().equals("1")) {//存在计量单位 单据 与 数量必填
                                    String price = setColValue(row, unitPriceIndex);
                                    String jNum = setColValue(row, ndSIndex);
                                    String dNum = setColValue(row, ncSIndex);
                                    if (StringUtils.isBlank(price) || (StringUtils.isBlank(jNum) && StringUtils.isBlank(dNum)) || (StringUtils.isNotBlank(jNum) && StringUtils.isNotBlank(dNum))) {
                                        mapArr.put("error", codeErrorStr + codeValue + "为计量单位科目！单价为必填项,借方数量与贷方数量二选一填入整数值！");
                                        mapArr.put("code", "404");
                                        return Mono.just(mapArr);
                                    } else if (StringUtils.isNotBlank(price) && (!NumberUtil.isNumber(price) || Double.parseDouble(price) < 0)) {
                                        mapArr.put("error", codeErrorStr + codeValue + "为计量单位科目！单价必须为正数值！");
                                        mapArr.put("code", "404");
                                        return Mono.just(mapArr);
                                    } else if ((StringUtils.isNotBlank(jNum) && !NumberUtil.isNumber(jNum)) || (StringUtils.isNotBlank(dNum) && !NumberUtil.isNumber(dNum))) {
                                        mapArr.put("error", codeErrorStr + codeValue + "为计量单位科目！借方数量或贷方数量为整数值！");
                                        mapArr.put("code", "404");
                                        return Mono.just(mapArr);
                                    }
                                }
                                if (thisKemu.getCurrency().equals("1")) {//存在外币核算外币 金额 与 汇率 为必填项
                                    String amountMd = setColValue(row, nfratMdIndex).replaceAll(",", "");
                                    String amountMc = setColValue(row, nfratMcIndex).replaceAll(",", "");
                                    String jNum = setColValue(row, mdFIndex);
                                    //String dNum = row[mcFIndex].toString();
                                    if ((StringUtils.isBlank(amountMd) && StringUtils.isBlank(amountMc)) || (StringUtils.isBlank(jNum) /*&& StringUtils.isBlank(dNum)) || (StringUtils.isNotBlank(jNum) && StringUtils.isNotBlank(dNum)*/)) {
                                        mapArr.put("error", codeErrorStr + codeValue + "为外币核算科目！原币金额与外币汇率为必填项！");
                                        mapArr.put("code", "404");
                                        return Mono.just(mapArr);
                                    } else if ((StringUtils.isNotBlank(amountMd) && (!NumberUtil.isNumber(amountMd))) || (StringUtils.isNotBlank(amountMc) && (!NumberUtil.isNumber(amountMc))/* || Double.parseDouble(amount) < 0*/)) {
                                        mapArr.put("error", codeErrorStr + codeValue + "为外币核算科目！原币金额为正整数值！");
                                        mapArr.put("code", "404");
                                        return Mono.just(mapArr);
                                    } else if ((StringUtils.isNotBlank(jNum) && (!NumberUtil.isNumber(jNum)/* || Double.parseDouble(jNum) < 0*/)) /*|| (StringUtils.isNotBlank(dNum) && (!NumberUtil.isNumber(dNum) || Double.parseDouble(dNum) < 0))*/) {
                                        mapArr.put("error", codeErrorStr + codeValue + "为外币核算科目！汇率必须为正整数值！");
                                        mapArr.put("code", "404");
                                        return Mono.just(mapArr);
                                    }
                                }
                            }
                        }

                    }
                    //汇总之后年月list
                    List<Map<Object, Object>> new_ListMap = new ArrayList<Map<Object, Object>>();
                    Set arr = new HashSet();
                    for (int i = 0; i < ListMap.size(); i++) {
                        Map m = ListMap.get(i);
                        if (arr.contains(m.get("time"))) {
                            continue;
                        }
                        //添加key的值进set
                        arr.add(m.get("time"));
                        //储存进的集合
                        new_ListMap.add(m);
                    }
                    map.put("timelist", new_ListMap);
                    map.put("kemuList", beingSets.getT2());
                    // 获取最大凭证编码 -》 校验凭证 -》 校验科目编码
                    return service.queryAllYaerAndMonthMaxInoid().flatMap(maxList -> {
                        map.put("maxInoId", maxList);
                        // 校验月份是否已经结账
                        if (new_ListMap.size() > 0) { // 当为自定义编码时 校验数据库指定月是否存在
                            return service.checkPingZhengYearIsClose(new_ListMap).flatMap(item -> {
                                if (item.length > 0) {
                                    map.put("error", "以下年月：【" + item[0] + "】 已完成结账操作,不能进行凭证导入！");
                                    map.put("code", "404");
                                } else if (item.length == 0 && parms[4].equals("1")) {
                                    return service.checkPingZhengDbRepeatNumber(new_ListMap).flatMap(item1 -> {
                                        if (item1.length > 0) {
                                            map.put("error", "以下月份及凭证编码：【" + JSON.toJSONString(item1) + "】已存在！请改正或在导入面板选中编码生成！");
                                            map.put("code", "404");
                                        }
                                        return Mono.just(map);
                                    });
                                }
                                return Mono.just(map);
                            });
                        }
                        return Mono.just(map);
                    });
                }).flatMap(map1 -> StrUtil.isNotBlank(map1.get("error").toString()) ? Mono.just(map1) : service.getAllKuaiJiQiJianInfoByAccId(thisDbName.get()).doOnNext(map3 -> map1.put("kuaiJiQiJians", map3)).thenReturn(map1))
                // 获取map数据,并向数据库增加
                .flatMapMany(map -> {
                    String error = (String) map.get("error");
                    String code = (String) map.get("code");
                    if (StringUtils.isNotBlank(error)) {
                        return Mono.just(R.ok().setResult(error).setCode(Long.valueOf(code)));
                    }
                    List<Map<Object, Object>> timelist = (List<Map<Object, Object>>) map.get("timelist");
                    List<Object[]> excellist = (List<Object[]>) map.get("excellist");
                    List<Accvoucher> coutnoIdList = (List<Accvoucher>) map.get("maxInoId");
                    Map<String, Object> kjQjMap = (HashMap<String, Object>) map.get("kuaiJiQiJians");
                    //List<SysPeriod> priedList = (List<SysPeriod>)kjQjMap.get("list");// 获取db所以时间信息
                    HashSet<CodeKemu> kemuList = (HashSet<CodeKemu>) map.get("kemuList");
                    List<Accvoucher> saveList = new ArrayList();
                    String[] systemTitleNames = titlesAR.get();
                    int dateIndex = -1;
                    int numberIndex = -1;
                    int idocIndex = -1; //附单数
                    int cdigestIndex = -1; //凭证摘要
                    int ccodeIndex = -1; //科目编码
                    int ccodeNameIndex = -1; //科目名称
                    int mdIndex = -1; //借方金额
                    int mcIndex = -1; // 贷方金额
                    int cashProjectIndex = -1; //现金流量项目编码
                    int cdeptIdIndex = -1; //部门编码
                    int cpersonIdIndex = -1; //个人编码
                    int ccusIdIndex = -1; //客户编码
                    int csupIdIndex = -1; //供应商编码
                    int projectClassIdIndex = -1; //项目大类编码
                    int projectIdIndex = -1; //项目编码
                    int cbillIndex = -1; //制单人
                    int ccheckIndex = -1; // 审核人
                    int cdirectorIndex = -1; // 审核人
                    int ccashierIndex = -1; //记账人
                    int iflagIndex = -1;//  凭证状态
                    int cbookIndex = -1; //出纳
                    int csignIndex = -1; //凭证类型
                    int settlementMethodIndex = -1; //结算方式编码
                    int unitMeasurementIndex = -1; //计算单位
                    int ndSIndex = -1; //借方数量
                    int ncSIndex = -1; //贷方数量
                    int unitPriceIndex = -1; //单价
                    int foreignCurrencyIndex = -1; //外币币种
                    int nfratMdIndex = -1; //'原币借方金额'
                    int nfratMcIndex = -1; //'原币贷方金额'
                    int mdFIndex = -1; //借方汇率
                    int mcFIndex = -1; //借方汇率
                    int pjIdIndex = -1; //票据号
                    int pjDateIndex = -1; //票据日期
                    int pjUnitNameIndex = -1; //票据日期
                    int cdfine1Index = -1; //辅助项1
                    String thisImportDate = DateUtil.today();
                    int sameDateNumber = 0;
                    String sameDateStr = "";
                    for (int i = 0; i < timelist.size(); i++) {
                        // 唯一码
                        String uniqueCode = IdUtil.objectId();
                        int inidNumber = 0;
                        String thisCode = "";
                        String coutnoId = "";
                        if (!parms[4].equals("1")) { //系统时
                            // 获取动态字段下标
                            String importDate = timelist.get(i).get("time").toString().split(">>")[0].substring(0, 7);
                            if (!sameDateStr.equals(importDate)) {
                                sameDateNumber = 0;
                                sameDateStr = importDate;
                            } else {
                                sameDateNumber++;
                            }
                            coutnoId = getThisDateMaxVoucherIdon(coutnoIdList, timelist.get(i).get("time").toString());
             /* if ((Integer.valueOf(coutnoId) + sameDateNumber + 1) < 10) {
                coutnoId = "000" + (Integer.valueOf(coutnoId) + sameDateNumber + 1);
              } else if ((Integer.valueOf(coutnoId) + sameDateNumber + 1) >= 10 && (Integer.valueOf(coutnoId) + sameDateNumber + 1) < 100) {
                coutnoId = "00" + (Integer.valueOf(coutnoId) + sameDateNumber + 1);
              } else if ((Integer.valueOf(coutnoId) + sameDateNumber + 1) >= 100 && (Integer.valueOf(coutnoId) + sameDateNumber + 1) < 1000) {
                coutnoId = "0" + (Integer.valueOf(coutnoId) + sameDateNumber + 1);
              }*/
                            coutnoId = "" + (Integer.valueOf(coutnoId) + sameDateNumber + 1);
                        }
                        // 往数据库增加
                        for (int j = 0; j < excellist.size(); j++) {
                            Object[] row = excellist.get(j);
                            // 获取日期与凭证号码的下表
                            if (j == 0 && i == 0) { // 第一次拿到index
                                for (int k = 0; k < row.length; k++) {
                                    if (systemTitleNames[k].equals("制单日期")) dateIndex = k;
                                    if (systemTitleNames[k].equals("凭证号")) numberIndex = k;
                                    if (systemTitleNames[k].equals("附单据数")) idocIndex = k;
                                    if (systemTitleNames[k].equals("凭证摘要")) cdigestIndex = k;
                                    if (systemTitleNames[k].equals("科目编码")) ccodeIndex = k;
                                    if (systemTitleNames[k].equals("科目名称")) ccodeNameIndex = k;
                                    if (systemTitleNames[k].equals("借方金额")) mdIndex = k;
                                    if (systemTitleNames[k].equals("贷方金额")) mcIndex = k;
                                    if (systemTitleNames[k].equals("现金流量项目编码")) cashProjectIndex = k;

                                    if (systemTitleNames[k].equals("部门编码")) cdeptIdIndex = k;
                                    if (systemTitleNames[k].equals("个人编码")) cpersonIdIndex = k;
                                    if (systemTitleNames[k].equals("客户编码")) ccusIdIndex = k;
                                    if (systemTitleNames[k].equals("供应商编码")) csupIdIndex = k;
                                    if (systemTitleNames[k].equals("项目大类编码")) projectClassIdIndex = k;
                                    if (systemTitleNames[k].equals("项目编码")) projectIdIndex = k;

                                    if (systemTitleNames[k].equals("制单人")) cbillIndex = k;
                                    if (systemTitleNames[k].equals("审核人")) ccheckIndex = k;
                                    if (systemTitleNames[k].equals("记账人")) cbookIndex = k;
                                    if (systemTitleNames[k].equals("凭证状态")) iflagIndex = k;
                                    if (systemTitleNames[k].equals("主管签字人")) cdirectorIndex = k;
                                    if (systemTitleNames[k].equals("凭证类型")) csignIndex = k;
                                    if (systemTitleNames[k].equals("出纳签字人")) ccashierIndex = k;

                                    if (systemTitleNames[k].equals("结算方式编码")) settlementMethodIndex = k;
                                    if (systemTitleNames[k].equals("计量单位")) unitMeasurementIndex = k;
                                    if (systemTitleNames[k].equals("借方数量")) ndSIndex = k;
                                    if (systemTitleNames[k].equals("贷方数量")) ncSIndex = k;
                                    if (systemTitleNames[k].equals("外币币种")) foreignCurrencyIndex = k;
                                    if (systemTitleNames[k].equals("原币借方金额")) nfratMdIndex = k;
                                    if (systemTitleNames[k].equals("原币贷方金额")) nfratMcIndex = k;
                                    if (systemTitleNames[k].equals("借方汇率")) mdFIndex = k;
                                    if (systemTitleNames[k].equals("贷方汇率")) mcFIndex = k;
                                    if (systemTitleNames[k].equals("单价")) unitPriceIndex = k;
                                    if (systemTitleNames[k].equals("票据号")) pjIdIndex = k;
                                    if (systemTitleNames[k].equals("票据日期")) pjDateIndex = k;
                                    if (systemTitleNames[k].equals("结算单位名称")) pjUnitNameIndex = k;
                                    if (systemTitleNames[k].equals("辅助项1")) cdfine1Index = k;
                                }
                            }
                            // 逐步顺序添加  自定义凭证号时  或者 系统凭证号时  解决凭证号不存在
                            String importDate = timelist.get(i).get("time").toString().split(">>")[0];
                            String tDate = DateUtil.formatDate(DateUtil.parseDate(row[dateIndex].toString()));
                            if (importDate.equals(tDate) && (/*!parms[4].equals("1") || */timelist.get(i).get("time").toString().split(">>")[1].equals(row[numberIndex]))) {
                                String pingZhengNumber = (parms[4].equals("1")) ? getIntegerValue(StrUtil.trim(excellist.get(j)[numberIndex].toString())) : coutnoId.trim();
                                //if (!parms[4].equals("1") && !pingZhengNumber.equals("")) break;
                                String time = timelist.get(i).get("time").toString().replaceAll("-", "");
                                // 当凭证编码为自定义时
                                if (!thisCode.equals(pingZhengNumber)) {
                                    inidNumber = 0;
                                    thisCode = pingZhengNumber;
                                } else {
                                    inidNumber += 1;
                                }
                                String tYear = time.substring(0, 4);
                                String tMonth = time.substring(4, 6);
                                String tQj = ((HashMap<String, String>) kjQjMap.get(tYear)).get(tMonth);//数据库对应的期间 与年月
                                Accvoucher accvoucher = new Accvoucher() //简版
                                        .setUniqueCode(uniqueCode).setVouchUnCode(IdUtil.objectId()).setIfrag(setVoucherStauts(setColValue(row, iflagIndex))).setCsign(setColValue(row, csignIndex)).setIyear(StrUtil.trim(tYear)).setImonth(StrUtil.trim(tMonth)).setIyperiod(tYear + tQj) // 年月期间
                                        .setIperiod(tQj) //期间
                                        .setCbill(setColValue(row, cbillIndex)).setDbillDate(StrUtil.trim(importDate)).setInoId(StrUtil.trim(pingZhengNumber)).setInid("" + (inidNumber + 1)).setIdoc(setVoucherIdoc(setColValue(row, idocIndex))).setCdigest(setColValue(row, cdigestIndex)).setCcode(setColValue(row, ccodeIndex)).setCcodeName(setColValue(row, ccodeNameIndex)).setMd(NumberUtil.roundStr(checkNumberIsBlank(setColValue(row, mdIndex)), 2)).setMc(NumberUtil.roundStr(checkNumberIsBlank(setColValue(row, mcIndex)), 2)).setCsign(setColValue(row, csignIndex)).setCdirector(setColValue(row, cdirectorIndex)).setCashProject(setColValue(row, cashProjectIndex)).setPjCsettle(setColValue(row, settlementMethodIndex)).setPjId(setColValue(row, pjIdIndex)).setPjDate(setColValue(row, pjDateIndex)).setPjUnitName(setColValue(row, pjUnitNameIndex));
                                // 条件添加
                                if (StringUtils.isNotBlank(setColValue(row, ccheckIndex)))
                                    accvoucher.setCcheck(setColValue(row, ccheckIndex)).setCcheckDate(thisImportDate);
                                if (StringUtils.isNotBlank(setColValue(row, ccashierIndex)))
                                    accvoucher.setCcashier(setColValue(row, ccashierIndex)).setCcashierDate(thisImportDate);
                                if (StringUtils.isNotBlank(setColValue(row, cbookIndex)))
                                    accvoucher.setCbook(setColValue(row, cbookIndex)).setIbook("1").setIbookDate(thisImportDate);
                                CodeKemu thisKemu = getThisKemu(setColValue(row, ccodeIndex), kemuList);
                                if (thisKemu.getBdept().equals("1"))
                                    accvoucher.setCdeptId(setColValue(row, cdeptIdIndex));
                                if (thisKemu.getBperson().equals("1"))
                                    accvoucher.setCpersonId(setColValue(row, cpersonIdIndex));
                                if (thisKemu.getBcus().equals("1")) accvoucher.setCcusId(setColValue(row, ccusIdIndex));
                                if (thisKemu.getBsup().equals("1")) accvoucher.setCsupId(setColValue(row, csupIdIndex));
                                if (thisKemu.getBitem().equals("1"))
                                    accvoucher.setProjectClassId(setColValue(row, projectClassIdIndex)).setProjectId(setColValue(row, projectIdIndex));
                                if (parms[1].equals("1")) {               //标准
                                    accvoucher.setNdS(setColValue(row, ndSIndex).replaceAll(",", "")).setNcS(setColValue(row, ncSIndex).replaceAll(",", "")).setNfratMd(NumberUtil.roundStr(checkNumberIsBlank(setColValue(row, nfratMdIndex).replaceAll(",", "")), 2)).setNfratMc(NumberUtil.roundStr(checkNumberIsBlank(setColValue(row, nfratMcIndex).replaceAll(",", "")), 2)).setCunitPrice(setColValue(row, unitPriceIndex)).setMdF(setColValue(row, mdFIndex))
                                    //.setMcF(setColValue(row, mcFIndex))
                                    ;
                                    if (false) {// 校验指定科目是否存在 币种 与 计量单位 作废
                                        String ccode = accvoucher.getCcode();
                                        accvoucher.setUnitMeasurement(setColValue(row, unitMeasurementIndex)).setForeignCurrency(setColValue(row, foreignCurrencyIndex));
                                    }
                                }
                                // 校验是否存在辅助项 获取存在的数量反射放值
                                // if (systemTitleNames[k].equals("辅助项1"))cdfine1Index = k;
                                if (systemTitleNames.toString().contains("辅助项")) { // 存在辅助项
                                    List<Integer> keys = new ArrayList<>();
                                    List<Integer> indexs = new ArrayList<>();
                                    for (int k = 0; k < systemTitleNames.length; k++) {
                                        String titleName = systemTitleNames[k];
                                        String numStr = titleName.replace("辅助项", "");
                                        if (titleName.startsWith("辅助项") && NumberUtil.isNumber(numStr)) {
                                            keys.add(Integer.parseInt(numStr));
                                            indexs.add(k);
                                        }
                                    }
                                    if (keys.size() > 30) {
                                        return Mono.just(R.ok().setResult("导入列限制：用户自定义辅助项列不得大于30个！！！").setCode(404L));
                                    } else if (keys.size() > 0) {
                                        accvoucher = service.modifyPingZhengEntityPropertyByAuxiliaryItem(accvoucher, excellist.get(j), keys, indexs);
                                    }
                                }
                                saveList.add(accvoucher);
                            }
                        }
                    }
                    //return Flux.just(new Accvoucher());
                    return accvoucherRepository.saveAll(saveList);
                }).collectList().flatMap(list -> taskRepository.deleteByIyearAndFunctionModule(DateUtil.thisYear() + "", "凭证导入").thenReturn(list)).map(o -> R.ok().setResult(o));
    }

    @Transactional
    @PostMapping("/importAccvoucherQiChu") // 期初余额导入
    public Mono<R> importAccvoucherQiChu(@RequestPart("file") FilePart filePartParm, @RequestPart("templateInfo") String templateID) throws Exception {
        // 唯一码 -- 模板类型 -- 是否为名称 -- 数据下标 -- 是否自定义编码--账套accId--year
        System.out.println(templateID);
        String[] parms = templateID.split("--");
        AtomicReference<String[]> titlesAR = new AtomicReference(); // 选择的导入模板列
        AtomicReference<String> thisDbName = new AtomicReference(parms[5]); // 当前账套名称
        Path tempFilePath = Files.createTempFile("", new String(filePartParm.filename().getBytes("ISO-8859-1"), "UTF-8"));
        return Mono.just(filePartParm).flatMap(files -> taskRepository.save(new Task().setCaozuoUnique("test001").setFunctionModule("凭证导入").setTime(DateUtil.now()).setState("1").setIyear(DateUtil.thisYear() + "").setMethod(DateUtil.thisMonth() + "").setMethod("导入")).map(entity1 -> files)).flatMap(filePart -> {
                    try {
                        return DataBufferUtils.write(filePart.content(), AsynchronousFileChannel.open(tempFilePath, StandardOpenOption.WRITE), 0).doOnComplete(() -> log.info("上传成功")).collectList().map(item -> tempFilePath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return Mono.just("");
                }).flatMap(t -> {
                    return fuzhuHesuanRepository.findAll().collectList();
                })
                // 遍历导入excel内容
                .flatMap(fzs -> { //根据用户自定义字段读
                    List<Object[]> list = null;
                    String[] titles = new String[49];
                    titles[0] = "科目名称";
                    titles[1] = "科目编码";
                    titles[2] = "个人编码";
                    titles[3] = "个人姓名";
                    titles[4] = "部门编码";
                    titles[5] = "部门编码";
                    titles[6] = "客户编码";
                    titles[7] = "客户名称";
                    titles[8] = "供应商编码";
                    titles[9] = "供应商名称";
                    titles[10] = "项目大类编码";
                    titles[11] = "项目大类名称";
                    titles[12] = "项目编码";
                    titles[13] = "项目名称";
                    titles[14] = "数量";
                    titles[15] = "原币金额";
                    titles[16] = "汇率";
                    titles[17] = "借方金额";
                    titles[18] = "贷方金额";
                    titles[19] = "自定义1";
                    titles[20] = "自定义2";
                    titles[21] = "自定义3";
                    titles[22] = "自定义4";
                    titles[23] = "自定义5";
                    titles[24] = "自定义6";
                    titles[25] = "自定义7";
                    titles[26] = "自定义8";
                    titles[27] = "自定义9";
                    titles[28] = "自定义10";
                    titles[29] = "自定义11";
                    titles[30] = "自定义12";
                    titles[31] = "自定义13";
                    titles[32] = "自定义14";
                    titles[33] = "自定义15";
                    titles[34] = "自定义16";
                    titles[35] = "自定义17";
                    titles[36] = "自定义18";
                    titles[37] = "自定义19";
                    titles[38] = "自定义20";
                    titles[39] = "自定义21";
                    titles[40] = "自定义22";
                    titles[41] = "自定义23";
                    titles[42] = "自定义24";
                    titles[43] = "自定义25";
                    titles[44] = "自定义26";
                    titles[45] = "自定义27";
                    titles[46] = "自定义28";
                    titles[47] = "自定义29";
                    titles[48] = "自定义30";
                    fzs.forEach(fz -> {
                        titles[19 - 1 + fz.getCdfine()] = fz.getCname();
                    });
                    if (null == titlesAR.get()) titlesAR.set(new String[titles.length - 1]);
                    titlesAR.set(titles.clone());
                    try {
                        list = new XlsUtils3(tempFilePath.toString()).getExcelObj(tempFilePath.toString(), titles, 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        assert tempFilePath != null;
                        try {
                            Files.delete(tempFilePath);
                        } catch (IOException e) {
                            System.err.println("读取导入文件内容时发生错误: ---------下---------");
                            e.printStackTrace();
                        }
                    }
                    return Mono.just(list);
                })
                // 进行条件判断
                .flatMap(list -> {
                    Map mapArr = new HashMap();
                    mapArr.put("excellist", list);   // excel中内容
                    mapArr.put("error", "");
                    mapArr.put("code", "200");

                    String finalThisImportYearStr = templateID.split("--")[6];
                    return Mono.just("").map(s -> {
                        //所有科目与末级有效科目
                        Mono<HashSet<String[]>> all = codeKemuRepository.findAllByYear(finalThisImportYearStr).collectList().map(list1 -> new HashSet<>(getHashSetByKeMu(list1)));
                        Mono<HashSet<CodeKemu>> lastStage = codeKemuRepository.findAllByYearAndBendQiChu(finalThisImportYearStr).collectList().map(list1 -> new HashSet<>(list1));
                        // 辅助核算五项
                        Mono<List<SysPsn>> geSets = psnRepository.findAllPsnCodeOrPsnNameByFlag().collectList();
                        Mono<List<SysDepartment>> bmSets = departmentRepository.findAllDeptCodeOrDeptNameByFlag().collectList();
                        Mono<List<Customer>> khSets = customerRepository.findAllCustCodeOrCustNameByFlag().collectList();
                        Mono<List<Supplier>> gysSets = supplierRepository.findAllCustCodeOrCustNameByFlag().collectList();
                        Mono<List<ProjectCategory>> proClassSets = projectCategoryRepository.findProjectCateCodeOrProjectCateNameByFlag().collectList();
                        Mono<List<Project>> proSets = projectRepository.findAllProCodeOrProNameByFlag().collectList();
                        Mono<List<ProjectItem>> proItSets = projectItemRepository.findAllItemCodeOrItemNameByAll().collectList();

                        return Mono.zip(all, lastStage, geSets, bmSets, khSets, gysSets, proClassSets, proSets);
                    }).flatMap(zips -> {
                        return zips.map(many -> {
                            mapArr.put("codeSets", many);
                            return mapArr;
                        });
                    });
                })
                // 对制单日期、凭证号进行去重汇总，作为唯一检索条件
                .flatMap(map -> {
                    String error = (String) map.get("error");
                    Map<String, Object> mapArr = new HashMap<>();
                    List<SubjectInitialBalanceVo> accvoList = new ArrayList<>();
                    if (StringUtils.isNotBlank(error)) {
                        mapArr = new HashMap<>();
                        mapArr.put("error", error);
                        mapArr.put("code", "404");
                        return Mono.just(mapArr);
                    }
                    List<Object[]> list = (List<Object[]>) map.get("excellist");
                    // 所以科目 与 末级科目
                    Tuple8<HashSet<String[]>, HashSet<CodeKemu>, List<SysPsn>, List<SysDepartment>, List<Customer>, List<Supplier>, List<ProjectCategory>, List<Project>> beingSets = (Tuple8<HashSet<String[]>, HashSet<CodeKemu>, List<SysPsn>, List<SysDepartment>, List<Customer>, List<Supplier>, List<ProjectCategory>, List<Project>>) map.get("codeSets");

                    boolean fx = "1".equals(parms[2]);

                    for (int i = 0; i < list.size(); i++) {
                        Object[] row = (Object[]) list.get(i);
                        String codeValue = row[0].toString();
                        HashSet<CodeKemu> kemuList = beingSets.getT2();
                        CodeKemu codeKemu = getThisKemu(codeValue, kemuList);
                        SubjectInitialBalanceVo accvo = getThisSubjectInitialBalanceVo(codeKemu);
                        mapArr = new HashMap<>();
                        String codeErrorStr = "excel中【第 " + (i + 1) + " 行】科目编码【" + codeValue + "】";
                        if (!checkImportKemuExist(beingSets.getT1(), codeValue, "2")) {
                            mapArr.put("error", codeErrorStr + "不存在，请修改模板文件中的会计科目或在软件中新增该科目！");
                            mapArr.put("code", "404");
                            return Mono.just(mapArr);
                        }
                        if (null == getThisKemu(codeValue, beingSets.getT2())) {
                            mapArr.put("error", codeErrorStr + "不是末级科目，请检查模板文件中的科目是否正确，请修改完后重新导入！");
                            mapArr.put("code", "404");
                            return Mono.just(mapArr);
                        }

                        //判断借贷方是否合理并装填mdmc
                        //titles[17] = "借方金额";titles[18] = "贷方金额";
                        Object mdTemp = row[17];
                        Object mcTemp = row[18];
                        if (mdTemp == null) {
                            mapArr.put("error", codeErrorStr + "没有识别到【借方金额】");
                            mapArr.put("code", "404");
                            return Mono.just(mapArr);
                        }
                        if (mcTemp == null) {
                            mapArr.put("error", codeErrorStr + "没有识别到【贷方金额】");
                            mapArr.put("code", "404");
                            return Mono.just(mapArr);
                        }
                        BigDecimal mdBig = BigDecimal.valueOf(0.0);
                        BigDecimal mcBig = BigDecimal.valueOf(0.0);
                        if (!"".equals(mdTemp)) {
                            try {
                                mdBig = (BigDecimal) mdTemp;
                            } catch (Exception e) {
                                mapArr.put("error", codeErrorStr + "借方金额转换成数字失败,请检查");
                                mapArr.put("code", "404");
                                return Mono.just(mapArr);
                            }
                        }
                        if (!"".equals(mcTemp)) {
                            try {
                                mcBig = (BigDecimal) mcTemp;
                            } catch (Exception e) {
                                mapArr.put("error", codeErrorStr + "贷方金额转换成数字失败,请检查");
                                mapArr.put("code", "404");
                                return Mono.just(mapArr);
                            }
                        }

                        mdBig = fmtJiShui(mdBig, 2);
                        mcBig = fmtJiShui(mcBig, 2);

                        if (mdBig != BigDecimal.valueOf(0.0) && mcBig != BigDecimal.valueOf(0.0)) {
                            mapArr.put("error", codeErrorStr + "检测到借方贷方都存在数值,请删除其中一个数值");
                            mapArr.put("code", "404");
                            return Mono.just(mapArr);
                        }
                        if (mdBig == BigDecimal.valueOf(0.0) && mcBig == BigDecimal.valueOf(0.0)) {
                            mapArr.put("error", codeErrorStr + "检测到借方贷方都为零,请填写其中一个数值");
                            mapArr.put("code", "404");
                            return Mono.just(mapArr);
                        }

                        accvo.setMd(mdBig);
                        accvo.setMc(mcBig);

                        //存在个人辅助核算
                        if (accvo.getBperson().equals("1")) {
                            Object grValue = fx ? row[3] : row[2];
                            List<SysPsn> psnCollect = beingSets.getT3().stream().filter(item -> (fx ? item.getPsnName().equals(grValue) : item.getPsnCode().equals(grValue))).collect(Collectors.toList());
                            if (grValue == null || "".equals(grValue)) {
                                mapArr.put("error", codeErrorStr + codeValue + "为个人辅助核算科目！个人" + (fx ? "名称" : "编码") + "列不能为空白！");
                                mapArr.put("code", "404");
                                return Mono.just(mapArr);
                            } else if (psnCollect.size() == 0) {
                                mapArr.put("error", codeErrorStr + codeValue + "为个人辅助核算科目！个人" + (fx ? "名称" : "编码") + "列值" + grValue + "系统不存在，请检查模板文件是否正确，或者在软件的基础档案中新增该辅助项！");
                                mapArr.put("code", "404");
                                return Mono.just(mapArr);
                            } else {
                                accvo.setCpersonId(psnCollect.get(0).getUniqueCode());
                            }
                        }

                        // 存在部门辅助核算
                        if (accvo.getBdept().equals("1")) {
                            Object dValue = fx ? row[5] : row[4];
                            List<SysDepartment> deptCollect = beingSets.getT4().stream().filter(item -> (fx ? item.getDeptName().equals(dValue) : item.getDeptCode().equals(dValue))).collect(Collectors.toList());
                            if (dValue == null || "".equals(dValue)) {
                                mapArr.put("error", codeErrorStr + "为部门辅助核算科目！部门" + (fx ? "名称" : "编码") + "列不能为空白！");
                                mapArr.put("code", "404");
                                return Mono.just(mapArr);
                            } else if (deptCollect.size() == 0) {
                                mapArr.put("error", codeErrorStr + codeValue + "为部门辅助核算科目！部门" + (fx ? "名称" : "编码") + "列值" + dValue.toString() + "系统不存在，请检查模板文件是否正确，或者在软件的基础档案中新增该辅助项！");
                                mapArr.put("code", "404");
                                return Mono.just(mapArr);
                            } else {
                                accvo.setCdeptId(deptCollect.get(0).getUniqueCode());
                            }
                        }

                        // 存在客户辅助核算
                        if (accvo.getBcus().equals("1")) {
                            Object cValue = fx ? row[7] : row[6];
                            List<Customer> customerCollect = beingSets.getT5().stream().filter(item -> (fx ? (item.getCustName().equals(cValue) || item.getCustAbbname().equals(cValue)) : item.getCustCode().equals(cValue))).collect(Collectors.toList());
                            if (cValue == null || "".equals(cValue)) {
                                mapArr.put("error", codeErrorStr + codeValue + "为客户辅助核算科目！客户" + (fx ? "名称" : "编码") + "列不能为空白！");
                                mapArr.put("code", "404");
                                return Mono.just(mapArr);
                            } else if (customerCollect.size() == 0) {
                                mapArr.put("error", codeErrorStr + codeValue + "为客户辅助核算科目！客户" + (fx ? "名称" : "编码") + "列值" + cValue + "系统不存在，请检查模板文件是否正确，或者在软件的基础档案中新增该辅助项！");
                                mapArr.put("code", "404");
                                return Mono.just(mapArr);
                            } else {
                                accvo.setCcusId(customerCollect.get(0).getUniqueCode());
                            }
                        }

                        // 存在供应商辅助核算
                        if (accvo.getBsup().equals("1")) {
                            Object gyValue = fx ? row[9] : row[8];
                            List<Supplier> supCollect = beingSets.getT6().stream().filter(item -> (fx ? (item.getCustName().equals(gyValue) || item.getCustAbbname().equals(gyValue)) : item.getCustCode().equals(gyValue))).collect(Collectors.toList());
                            if (gyValue == null || "".equals(gyValue)) {
                                mapArr.put("error", codeErrorStr + codeValue + "为供应商辅助核算科目！供应商" + (fx ? "名称" : "编码") + "列不能为空白！");
                                mapArr.put("code", "404");
                                return Mono.just(mapArr);
                            } else if (supCollect.size() == 0) {
                                mapArr.put("error", codeErrorStr + codeValue + "为供应商辅助核算科目！供应商" + (fx ? "名称" : "编码") + "列值" + gyValue + "系统不存在，请检查模板文件是否正确，或者在软件的基础档案中新增该辅助项！");
                                mapArr.put("code", "404");
                                return Mono.just(mapArr);
                            } else {
                                accvo.setCsupId(supCollect.get(0).getUniqueCode());
                            }
                        }

                        // 存在项目辅助核算
                        if (accvo.getBitem().equals("1")) {
                            Object pdValue = fx ? row[11] : row[10];
                            Object pValue = fx ? row[13] : row[12];
                            String proClassNum = "";
                            List<ProjectCategory> proClassCollect = beingSets.getT7().stream().filter(item -> (fx ? item.getProjectCateName().equals(pdValue) : item.getProjectCateCode().equals(pdValue))).collect(Collectors.toList());
                            List<Project> proCollect = beingSets.getT8().stream().filter(item -> (fx ? (item.getProjectName().equals(pValue)) : item.getProjectCode().equals(pValue))).collect(Collectors.toList());
                            if (pValue == null || "".equals(pValue)) {
                                mapArr.put("error", codeErrorStr + codeValue + "为项目辅助核算科目！项目" + (fx ? "名称" : "编码") + "列不能为空白！");
                                mapArr.put("code", "404");
                                return Mono.just(mapArr);
                            } else if (pdValue == null || "".equals(pdValue)) {
                                mapArr.put("error", codeErrorStr + codeValue + "为项目辅助核算科目！项目大类" + (fx ? "名称" : "编码") + "列不能为空白！");
                                mapArr.put("code", "404");
                                return Mono.just(mapArr);
                            } else if (proCollect.size() == 0) {
                                mapArr.put("error", codeErrorStr + codeValue + "为项目辅助核算科目！项目" + (fx ? "名称" : "编码") + "列值" + pValue + "系统不存在，请检查模板文件是否正确，或者在软件的基础档案中新增该辅助项！");
                                mapArr.put("code", "404");
                                return Mono.just(mapArr);
                            } else if (proClassCollect.size() == 0) {
                                mapArr.put("error", codeErrorStr + codeValue + "为项目辅助核算科目！项目大类" + (fx ? "名称" : "编码") + "列值" + pdValue + "系统不存在，请检查模板文件是否正确，或者在软件的基础档案中新增该辅助项！");
                                mapArr.put("code", "404");
                                return Mono.just(mapArr);
                            } else {
                                ProjectCategory pc = proClassCollect.get(0);
                                Project pro = proCollect.get(0);
                                if (pc.getProjectCateCode().equals(pro.getProjectCateCode())) {
                                    accvo.setProjectId(pro.getUniqueCode());
                                    accvo.setProjectClassId(pc.getProjectCateCode());
                                }
                            }
                        }

                        // titles[14] = "数量";
                        // 存在数量辅助核算[识别数量是否与金额方向一致、正负数相同]
                        if (accvo.getBnum().equals("1")) {
                            Object numTemp = row[14];
                            if (numTemp == null) {
                                mapArr.put("error", codeErrorStr + "为数量辅助核算科目！但是没有识别到【数量】请检查");
                                mapArr.put("code", "404");
                                return Mono.just(mapArr);
                            }
                            BigDecimal numBig = BigDecimal.valueOf(0.0);
                            if (!"".equals(numTemp)) {
                                try {
                                    numBig = (BigDecimal) numTemp;
                                } catch (Exception e) {
                                    mapArr.put("error", codeErrorStr + "为数量辅助核算科目！但是数量转换成数字失败，请检查");
                                    mapArr.put("code", "404");
                                    return Mono.just(mapArr);
                                }
                            }

                            numBig = fmtJiShui(numBig, 2);

                            if (numBig.compareTo(BigDecimal.valueOf(0)) == 0) {
                                mapArr.put("error", codeErrorStr + "为数量辅助核算科目！但是数量不能为零，请检查");
                                mapArr.put("code", "404");
                                return Mono.just(mapArr);
                            }
                            BigDecimal comPare0 = BigDecimal.valueOf(0.0);
                            BigDecimal md = accvo.getMd();
                            BigDecimal mc = accvo.getMc();

                            BigDecimal nds = BigDecimal.valueOf(0);
                            BigDecimal ncs = BigDecimal.valueOf(0);

                            if (md.compareTo(comPare0) != 0) {
                                nds = numBig;
                            }
                            if (mc.compareTo(comPare0) != 0) {
                                ncs = numBig;
                            }

                            if (md.compareTo(comPare0) != 0) {
                                if ((md.compareTo(comPare0) == 1 && nds.compareTo(comPare0) == -1) || (md.compareTo(comPare0) == -1 && nds.compareTo(comPare0) == 1)) {
                                    mapArr.put("error", codeErrorStr + "为数量辅助核算科目！但是数量与金额要同时为正数或者同时为负数，请检查");
                                    mapArr.put("code", "404");
                                    return Mono.just(mapArr);
                                }
                            }

                            if (mc.compareTo(comPare0) != 0) {
                                if ((mc.compareTo(comPare0) == 1 && ncs.compareTo(comPare0) == -1) || (mc.compareTo(comPare0) == -1 && ncs.compareTo(comPare0) == 1)) {
                                    mapArr.put("error", codeErrorStr + "为数量辅助核算科目！但是数量与金额要同时为正数或者同时为负数，请检查");
                                    mapArr.put("code", "404");
                                    return Mono.just(mapArr);
                                }
                            }
                            accvo.setNdS(nds);
                            accvo.setNcS(ncs);
                        }

                        //titles[15] = "原币金额";titles[16] = "汇率";
                        // 存在外币辅助核算[识别外币金额是否与金额方向一致、正负数相同]
                        if (accvo.getCurrency().equals("1")) {//存在外币核算外币 金额 与 汇率 为必填项
                            Object moneyTemp = row[15];
                            Object hlTemp = row[16];
                            if (moneyTemp == null) {
                                mapArr.put("error", codeErrorStr + "为数量辅助核算科目！但是没有识别到【外币金额】请检查");
                                mapArr.put("code", "404");
                                return Mono.just(mapArr);
                            }
                            if (hlTemp == null) {
                                mapArr.put("error", codeErrorStr + "为数量辅助核算科目！但是没有识别到【汇率】请检查");
                                mapArr.put("code", "404");
                                return Mono.just(mapArr);
                            }
                            BigDecimal moneyBig = BigDecimal.valueOf(0.0);
                            BigDecimal hlBig = BigDecimal.valueOf(0.0);
                            if (!"".equals(moneyTemp)) {
                                try {
                                    moneyBig = (BigDecimal) moneyTemp;
                                } catch (Exception e) {
                                    mapArr.put("error", codeErrorStr + "为外币辅助核算科目！但是外币金额转换成数字失败，请检查");
                                    mapArr.put("code", "404");
                                    return Mono.just(mapArr);
                                }
                            }

                            if (!"".equals(hlTemp)) {
                                try {
                                    hlBig = (BigDecimal) hlTemp;
                                } catch (Exception e) {
                                    mapArr.put("error", codeErrorStr + "为外币辅助核算科目！但是汇率转换成数字失败，请检查");
                                    mapArr.put("code", "404");
                                    return Mono.just(mapArr);
                                }
                            }

                            moneyBig = fmtJiShui(moneyBig, 6);
                            hlBig = fmtJiShui(hlBig, 6);

                            if (moneyBig.compareTo(BigDecimal.valueOf(0)) == 0) {
                                mapArr.put("error", codeErrorStr + "为外币辅助核算科目！但是外币金额不能为零，请检查");
                                mapArr.put("code", "404");
                                return Mono.just(mapArr);
                            }
                            BigDecimal comPare0 = BigDecimal.valueOf(0.0);
                            BigDecimal md = accvo.getMd();
                            BigDecimal mc = accvo.getMc();

                            BigDecimal nfratMd = BigDecimal.valueOf(0);
                            BigDecimal nfratMc = BigDecimal.valueOf(0);

                            if (md.compareTo(comPare0) != 0) {
                                nfratMd = moneyBig;
                            }
                            if (mc.compareTo(comPare0) != 0) {
                                nfratMc = moneyBig;
                            }

                            if (md.compareTo(comPare0) != 0) {
                                if ((md.compareTo(comPare0) == 1 && nfratMd.compareTo(comPare0) == -1) || (md.compareTo(comPare0) == -1 && nfratMc.compareTo(comPare0) == 1)) {
                                    mapArr.put("error", codeErrorStr + "为外币辅助核算科目！但是外币金额与借方金额要同时为正数或者同时为负数，请检查");
                                    mapArr.put("code", "404");
                                    return Mono.just(mapArr);
                                }
                            }

                            if (mc.compareTo(comPare0) != 0) {
                                if ((mc.compareTo(comPare0) == 1 && nfratMd.compareTo(comPare0) == -1) || (mc.compareTo(comPare0) == -1 && nfratMc.compareTo(comPare0) == 1)) {
                                    mapArr.put("error", codeErrorStr + "为外币辅助核算科目！但是外币金额与贷方金额要同时为正数或者同时为负数，请检查");
                                    mapArr.put("code", "404");
                                    return Mono.just(mapArr);
                                }
                            }
                            accvo.setNfratMd(nfratMd);
                            accvo.setNfratMc(nfratMc);
                            accvo.setNfrat(hlBig);
                        }

                        accvoList.add(accvo);
                    }
                    mapArr.put("accvoList", accvoList);
                    return Mono.just(mapArr);
                }).flatMapMany(map -> {
                    String error = (String) map.get("error");
                    String code = (String) map.get("code");
                    List<SubjectInitialBalanceVo> accvoList = (List<SubjectInitialBalanceVo>) map.get("accvoList");
                    if (StringUtils.isNotBlank(error)) {
                        return Mono.just(R.ok().setResult(error).setCode(Long.valueOf(code)));
                    }
                    // 往数据库增加
                    List<Accvoucher> saveList = new ArrayList<>();
                    for (int i = 0; i < accvoList.size(); i++) {
                        SubjectInitialBalanceVo vo = accvoList.get(i);
                        Accvoucher acc = new Accvoucher();
                        acc.setIyear(vo.getIyear());
                        acc.setImonth(vo.getIperiod());
                        acc.setIyperiod(vo.getIyperiod());
                        acc.setCcode(vo.getCcode());
                        acc.setCcodeName(vo.getCcodeName());
                        acc.setMd(vo.getMd().toString());
                        acc.setMc(vo.getMc().toString());
                        if ("1".equals(vo.getBnum())) {
                            acc.setNdS(vo.getNdS().toString());
                            acc.setNcS(vo.getNcS().toString());
                            acc.setCunitPrice(vo.getMd().add(vo.getMc()).divide(vo.getNdS().add(vo.getNcS())).toString());
                        }
                        if ("1".equals(vo.getCurrency())) {
                            acc.setNfratMd(vo.getNfratMd().toString());
                            acc.setNfratMc(vo.getNfratMc().toString());
                            acc.setMdF(vo.getNfrat().toString());
                        }
                        if ("1".equals(vo.getBperson())) {
                            acc.setCpersonId(vo.getCpersonId());
                        }
                        if ("1".equals(vo.getBdept())) {
                            acc.setCdeptId(vo.getCdeptId());
                        }
                        if ("1".equals(vo.getBcus())) {
                            acc.setCcusId(vo.getCcusId());
                        }
                        if ("1".equals(vo.getBsup())) {
                            acc.setCsupId(vo.getCsupId());
                        }
                        if ("1".equals(vo.getBitem())) {
                            acc.setProjectClassId(vo.getProjectClassId());
                            acc.setProjectId(vo.getProjectId());
                        }

                        if ("1".equals(vo.getCdfine1())) {
                            acc.setCdfine1(vo.getCdfine1Id());
                        }
                        if ("1".equals(vo.getCdfine2())) {
                            acc.setCdfine2(vo.getCdfine2Id());
                        }
                        if ("1".equals(vo.getCdfine3())) {
                            acc.setCdfine3(vo.getCdfine3Id());
                        }
                        if ("1".equals(vo.getCdfine4())) {
                            acc.setCdfine4(vo.getCdfine4Id());
                        }
                        if ("1".equals(vo.getCdfine5())) {
                            acc.setCdfine5(vo.getCdfine5Id());
                        }
                        if ("1".equals(vo.getCdfine6())) {
                            acc.setCdfine6(vo.getCdfine6Id());
                        }
                        if ("1".equals(vo.getCdfine7())) {
                            acc.setCdfine7(vo.getCdfine7Id());
                        }
                        if ("1".equals(vo.getCdfine8())) {
                            acc.setCdfine8(vo.getCdfine8Id());
                        }
                        if ("1".equals(vo.getCdfine9())) {
                            acc.setCdfine9(vo.getCdfine9Id());
                        }

                        if ("1".equals(vo.getCdfine10())) {
                            acc.setCdfine10(vo.getCdfine10Id());
                        }
                        if ("1".equals(vo.getCdfine11())) {
                            acc.setCdfine11(vo.getCdfine11Id());
                        }
                        if ("1".equals(vo.getCdfine12())) {
                            acc.setCdfine12(vo.getCdfine12Id());
                        }
                        if ("1".equals(vo.getCdfine13())) {
                            acc.setCdfine13(vo.getCdfine13Id());
                        }
                        if ("1".equals(vo.getCdfine14())) {
                            acc.setCdfine14(vo.getCdfine14Id());
                        }
                        if ("1".equals(vo.getCdfine15())) {
                            acc.setCdfine15(vo.getCdfine15Id());
                        }
                        if ("1".equals(vo.getCdfine16())) {
                            acc.setCdfine16(vo.getCdfine16Id());
                        }
                        if ("1".equals(vo.getCdfine17())) {
                            acc.setCdfine17(vo.getCdfine17Id());
                        }
                        if ("1".equals(vo.getCdfine18())) {
                            acc.setCdfine18(vo.getCdfine18Id());
                        }
                        if ("1".equals(vo.getCdfine19())) {
                            acc.setCdfine19(vo.getCdfine19Id());
                        }

                        if ("1".equals(vo.getCdfine20())) {
                            acc.setCdfine20(vo.getCdfine20Id());
                        }
                        if ("1".equals(vo.getCdfine21())) {
                            acc.setCdfine21(vo.getCdfine21Id());
                        }
                        if ("1".equals(vo.getCdfine22())) {
                            acc.setCdfine22(vo.getCdfine22Id());
                        }
                        if ("1".equals(vo.getCdfine23())) {
                            acc.setCdfine23(vo.getCdfine23Id());
                        }
                        if ("1".equals(vo.getCdfine24())) {
                            acc.setCdfine24(vo.getCdfine24Id());
                        }
                        if ("1".equals(vo.getCdfine25())) {
                            acc.setCdfine25(vo.getCdfine25Id());
                        }
                        if ("1".equals(vo.getCdfine26())) {
                            acc.setCdfine26(vo.getCdfine26Id());
                        }
                        if ("1".equals(vo.getCdfine27())) {
                            acc.setCdfine27(vo.getCdfine27Id());
                        }
                        if ("1".equals(vo.getCdfine28())) {
                            acc.setCdfine28(vo.getCdfine28Id());
                        }
                        if ("1".equals(vo.getCdfine29())) {
                            acc.setCdfine29(vo.getCdfine29Id());
                        }
                        if ("1".equals(vo.getCdfine30())) {
                            acc.setCdfine30(vo.getCdfine30Id());
                        }

                        saveList.add(acc);
                    }
                    return Flux.just(new Accvoucher());
                    //return accvoucherRepository.saveAll(saveList);
                }).collectList().flatMap(list -> taskRepository.deleteByIyearAndFunctionModule(DateUtil.thisYear() + "", "辅助期初导入").thenReturn(list)).map(o -> R.ok().setResult(o));
    }

    private BigDecimal fmtJiShui(BigDecimal num, Integer xiaoshu) {
        return num.setScale(xiaoshu, BigDecimal.ROUND_HALF_UP);
    }

    private SubjectInitialBalanceVo getThisSubjectInitialBalanceVo(CodeKemu ck) {
        SubjectInitialBalanceVo vo = new SubjectInitialBalanceVo();

        vo.setIyear(ck.getIyear());

        vo.setCcode(ck.getCcode());
        vo.setCcodeName(ck.getCcodeName());

        Integer fuzhus = 0;

        vo.setBnum("0");
        if ("1".equals(ck.getBnum())) vo.setBnum("1");
        vo.setCurrency("0");
        if ("1".equals(ck.getCurrency())) vo.setCurrency("1");

        vo.setBperson("0");
        if ("1".equals(ck.getBperson())) {
            vo.setBperson("1");
            fuzhus = fuzhus + 1;
        }
        vo.setBdept("0");
        if ("1".equals(ck.getBdept())) {
            vo.setBdept("1");
            fuzhus = fuzhus + 1;
        }
        vo.setBcus("0");
        if ("1".equals(ck.getBcus())) {
            vo.setBcus("1");
            fuzhus = fuzhus + 1;
        }
        vo.setBsup("0");
        if ("1".equals(ck.getBsup())) {
            vo.setBsup("1");
            fuzhus = fuzhus + 1;
        }
        vo.setBitem("0");
        if ("1".equals(ck.getBitem())) {
            vo.setBitem("1");
            fuzhus = fuzhus + 1;
        }

        vo.setCdfine1("0");
        if ("1".equals(ck.getCdfine1())) {
            vo.setCdfine1("1");
            fuzhus = fuzhus + 1;
        }
        vo.setCdfine2("0");
        if ("1".equals(ck.getCdfine2())) {
            vo.setCdfine2("1");
            fuzhus = fuzhus + 1;
        }
        vo.setCdfine3("0");
        if ("1".equals(ck.getCdfine3())) {
            vo.setCdfine3("1");
            fuzhus = fuzhus + 1;
        }
        vo.setCdfine4("0");
        if ("1".equals(ck.getCdfine4())) {
            vo.setCdfine4("1");
            fuzhus = fuzhus + 1;
        }
        vo.setCdfine5("0");
        if ("1".equals(ck.getCdfine5())) {
            vo.setCdfine5("1");
            fuzhus = fuzhus + 1;
        }
        vo.setCdfine6("0");
        if ("1".equals(ck.getCdfine6())) {
            vo.setCdfine6("1");
            fuzhus = fuzhus + 1;
        }
        vo.setCdfine7("0");
        if ("1".equals(ck.getCdfine7())) {
            vo.setCdfine7("1");
            fuzhus = fuzhus + 1;
        }
        vo.setCdfine8("0");
        if ("1".equals(ck.getCdfine8())) {
            vo.setCdfine8("1");
            fuzhus = fuzhus + 1;
        }
        vo.setCdfine9("0");
        if ("1".equals(ck.getCdfine9())) {
            vo.setCdfine9("1");
            fuzhus = fuzhus + 1;
        }
        vo.setCdfine10("0");
        if ("1".equals(ck.getCdfine10())) {
            vo.setCdfine10("1");
            fuzhus = fuzhus + 1;
        }
        vo.setCdfine11("0");
        if ("1".equals(ck.getCdfine11())) {
            vo.setCdfine11("1");
            fuzhus = fuzhus + 1;
        }
        vo.setCdfine12("0");
        if ("1".equals(ck.getCdfine12())) {
            vo.setCdfine12("1");
            fuzhus = fuzhus + 1;
        }
        vo.setCdfine13("0");
        if ("1".equals(ck.getCdfine13())) {
            vo.setCdfine13("1");
            fuzhus = fuzhus + 1;
        }
        vo.setCdfine14("0");
        if ("1".equals(ck.getCdfine14())) {
            vo.setCdfine14("1");
            fuzhus = fuzhus + 1;
        }
        vo.setCdfine15("0");
        if ("1".equals(ck.getCdfine15())) {
            vo.setCdfine15("1");
            fuzhus = fuzhus + 1;
        }
        vo.setCdfine16("0");
        if ("1".equals(ck.getCdfine16())) {
            vo.setCdfine16("1");
            fuzhus = fuzhus + 1;
        }
        vo.setCdfine17("0");
        if ("1".equals(ck.getCdfine17())) {
            vo.setCdfine17("1");
            fuzhus = fuzhus + 1;
        }
        vo.setCdfine18("0");
        if ("1".equals(ck.getCdfine18())) {
            vo.setCdfine18("1");
            fuzhus = fuzhus + 1;
        }
        vo.setCdfine19("0");
        if ("1".equals(ck.getCdfine19())) {
            vo.setCdfine19("1");
            fuzhus = fuzhus + 1;
        }

        vo.setCdfine20("0");
        if ("1".equals(ck.getCdfine20())) {
            vo.setCdfine20("1");
            fuzhus = fuzhus + 1;
        }
        vo.setCdfine21("0");
        if ("1".equals(ck.getCdfine21())) {
            vo.setCdfine21("1");
            fuzhus = fuzhus + 1;
        }
        vo.setCdfine22("0");
        if ("1".equals(ck.getCdfine22())) {
            vo.setCdfine22("1");
            fuzhus = fuzhus + 1;
        }
        vo.setCdfine23("0");
        if ("1".equals(ck.getCdfine23())) {
            vo.setCdfine23("1");
            fuzhus = fuzhus + 1;
        }
        vo.setCdfine24("0");
        if ("1".equals(ck.getCdfine24())) {
            vo.setCdfine24("1");
            fuzhus = fuzhus + 1;
        }
        vo.setCdfine25("0");
        if ("1".equals(ck.getCdfine25())) {
            vo.setCdfine25("1");
            fuzhus = fuzhus + 1;
        }
        vo.setCdfine26("0");
        if ("1".equals(ck.getCdfine26())) {
            vo.setCdfine26("1");
            fuzhus = fuzhus + 1;
        }
        vo.setCdfine27("0");
        if ("1".equals(ck.getCdfine27())) {
            vo.setCdfine27("1");
            fuzhus = fuzhus + 1;
        }
        vo.setCdfine28("0");
        if ("1".equals(ck.getCdfine28())) {
            vo.setCdfine28("1");
            fuzhus = fuzhus + 1;
        }
        vo.setCdfine29("0");
        if ("1".equals(ck.getCdfine29())) {
            vo.setCdfine29("1");
            fuzhus = fuzhus + 1;
        }
        vo.setCdfine30("0");
        if ("1".equals(ck.getCdfine30())) {
            vo.setCdfine30("1");
            fuzhus = fuzhus + 1;
        }

        if (fuzhus == 0) {
            vo.setIyperiod(ck.getIyear() + "21");
            vo.setIperiod("21");
        } else {
            vo.setIyperiod(ck.getIyear() + "00");
            vo.setIperiod("00");
        }
        vo.setFuzhu(String.valueOf(fuzhus));
        return vo;
    }

    private String getIntegerValue(String trim) {
        trim = StrUtil.trim(trim);
        if (trim.toCharArray()[0] == -96) {
            trim = getIntegerValue(trim.substring(1));
        }
        return Integer.parseInt(trim) + "";
    }

    private String getThisDateMaxVoucherIdon(List<Accvoucher> coutnoIdList, String time) {
        String[] split = time.split("-");
        for (Accvoucher accvoucher : coutnoIdList) {
            if (accvoucher.getIyear().equals(split[0]) && accvoucher.getImonth().equals(split[1])) {
                return accvoucher.getInoId();
            }
        }
        return "0000";
    }

    private String setVoucherIdoc(String setColValue) {
        String resutl = "";
        if (StrUtil.isNotBlank(setColValue) && NumberUtil.isNumber(setColValue) && Integer.parseInt(setColValue) > 0) {
            resutl = setColValue;
        }
        return resutl;
    }

    private String setVoucherStauts(String setColValue) {
        String resutl = "0";
        if (StrUtil.isNotBlank(setColValue)) {
            switch (setColValue) {
                case ("作废"):
                    resutl = "1";
                    break;

                case ("暂存"):
                    resutl = "2";
                    break;

                case ("错误"):
                    resutl = "3";
                    break;
            }
        }
        return resutl;
    }

    private CodeKemu getThisKemu(Object codeValue, HashSet<CodeKemu> kemuList) {
        CodeKemu thisKemu = null;
        for (CodeKemu codeKemu : kemuList) {
            if (codeKemu.getCcode().equals(codeValue)) {
                thisKemu = codeKemu;
                break;
            }
        }
        return thisKemu;
    }

    private String checkNumberIsBlank(String number) {
        if (StringUtils.isBlank(number)) number = "0";
        return number;
    }

    private boolean checkImportKemuExist(HashSet<String[]> t1, Object codeValue, String s) {
        boolean flag = false;
        if (s.equals("2")) {
            for (String[] strings : t1) { //系统存在
                if ((strings[0].equals(codeValue) && strings[1].equals("0")) || ((strings[0].equals(codeValue) && strings[1].equals("1")))) {
                    flag = true;
                    break;//存在
                }
            }
        } else {
            for (String[] strings : t1) { //是否停用
                if ((strings[0].equals(codeValue) && strings[1].equals("0"))) {
                    flag = true;
                    break;//存在
                }
            }
        }
        return flag;
    }

    private String setColValue(Object[] o, int index) {
        try {
            return null == o[index] ? "" : StrUtil.trim((o[index].toString()));
        } catch (Exception e) {
            return "";
        }
    }

    private List<String[]> getHashSetByKeMu(List<CodeKemu> list1) {
        List<String[]> list = new ArrayList<>(list1.size());
        for (CodeKemu codeKemu : list1) {
            list.add(new String[]{codeKemu.getCcode(), codeKemu.getFlag()});
        }
        return list;
    }

    @PostMapping("/findAllAccvoucher")
    public Mono<R> findAllAccvoucher(@RequestBody Map map) {
        //条件查询
        String year = "";
        String[] intervals = null;
        if (map.keySet().size() == 2) {
            return Mono.just("").map(R::ok);
        } else {
            year = map.get("year").toString();
            intervals = JSON.parseArray(map.get("interval").toString()).toArray(new String[0]);
        }
        return accvoucherRepository.findAllPingZhengMingXing(year, intervals[0].trim(), intervals[1].trim()).collectList().map(R::page);
    }

    @GetMapping("/findMaxQj")
    @ApiOperation(value = "查询账套最大期间", notes = "查询账套最大期间")
    public Mono<R> findMaxQj() {
        return accvoucherRepository.findFirstByMaxIyperiodValue().map(R::ok).defaultIfEmpty(R.ok().setResult(""));
    }

    @GetMapping("/findMaxQjMonth/{iyear}")
    @ApiOperation(value = "查询账套最大期间", notes = "查询账套最大期间")
    public Mono<R> findMaxQj(@PathVariable String iyear) {
        return accvoucherRepository.findFirstByIyearAndImonthBetweenOrderByIyperiodDesc(iyear, "01", "13").map(o -> R.ok(o.getIyperiod())).switchIfEmpty(Mono.just(R.ok().setResult("")));
    }

    @Transactional
    @PostMapping("/findAllPingZhengList")
    public Mono<R> findAllPingZhengList(@RequestBody Map map) {
        //条件查询
        if (map.keySet().size() == 2) {
            return Mono.just(R.ok().setResult(CollectOfUtils.mapof("total", 0, "items", new ArrayList<>())));
        }
        String queryMark = map.get("queryMark").toString();
        int page = Integer.parseInt(map.get("page").toString());
        int pageSize = Integer.parseInt(map.get("size").toString());
        Map<String, String> variableMap = ((HashMap<String, HashMap<String, String>>) map.get("condition")).get("variable");
        String intervalStart = variableMap.get("periodStart").replaceAll("-", "");
        String intervalEnd = variableMap.get("periodEnd").replaceAll("-", "");
        String dateStart = variableMap.get("dateStart");
        String dateEnd = variableMap.get("dateEnd");
        Mono<R> rMono = null;
        AtomicReference<Integer> totalAR = new AtomicReference();
        if (StrUtil.isNotBlank(intervalStart) && StrUtil.isNotBlank(intervalEnd)) {
            if (queryMark.equals("1")) {
                rMono = accvoucherRepository.findAllVoucherDetailByIyperiod(intervalStart, intervalEnd).collectList().cache().map(list -> queryFilter(list, map)).map(list -> splitList(countFilter(list, 8), page, pageSize, totalAR)).map(list -> R.ok().setResult(CollectOfUtils.mapof("total", totalAR.get(), "items", list)));
            } else { // 汇总
                rMono = assemblyVoucherPoolOrDetails(intervalStart, intervalEnd,"QJ").map(list -> splitList(queryFilter(list, map), page, pageSize, totalAR)).map(list -> R.ok().setResult(CollectOfUtils.mapof("total", totalAR.get(), "items", list)));
            }
        } else if (StrUtil.isNotBlank(dateStart) && StrUtil.isNotBlank(dateEnd)) {
            if (queryMark.equals("1")) {
                rMono = accvoucherRepository.findAllVoucherDetailByDate(dateStart.trim(), dateEnd.trim()).collectList().cache().map(list -> queryFilter(list, map)).map(list -> splitList(countFilter(list, 8), page, pageSize, totalAR)).map(list -> R.ok().setResult(CollectOfUtils.mapof("total", totalAR.get(), "items", list)));
            } else { // 汇总
                rMono = assemblyVoucherPoolOrDetails(dateStart.trim(), dateEnd.trim(),"DATE").map(list -> splitList(queryFilter(list, map), page, pageSize, totalAR)).map(list -> R.ok().setResult(CollectOfUtils.mapof("total", totalAR.get(), "items", list)));
            }
        }
        return rMono;
    }

    private Mono<List<Accvoucher>> assemblyVoucherPoolOrDetails(String intervalStart, String intervalEnd,String mark) {
        Mono<List<Accvoucher>> listMono =  (mark.equals("QJ")?accvoucherRepository.findAllVoucherPoolByIyperiod(intervalStart, intervalEnd):accvoucherRepository.findAllVoucherPoolFastByDate(intervalStart, intervalEnd)).collectList().cache();
        Mono<List<Accvoucher>> listMono1 = (mark.equals("QJ")?accvoucherRepository.findAllVoucherPoolMdByIyperiod(intervalStart, intervalEnd):accvoucherRepository.findAllVoucherPoolMdByDate(intervalStart, intervalEnd)).collectList().cache();
        Mono<List<Accvoucher>> listMono2 = (mark.equals("QJ")?accvoucherRepository.findAllVoucherPoolCdigestByIyperiod(intervalStart, intervalEnd):accvoucherRepository.findAllVoucherPoolCdigestByDate(intervalStart, intervalEnd)).collectList().cache();
        return  Mono.zip(listMono, listMono1, listMono2).flatMap(dbt -> {
           List<Accvoucher> t1 = dbt.getT1();
           List<Accvoucher> t2 = dbt.getT2();
           List<Accvoucher> t3 = dbt.getT3();
           for (Accvoucher accvoucher : t1) {
               for (Accvoucher accvoucher2 : t2) {
                   if (accvoucher.getUniqueCode().equals(accvoucher2.getUniqueCode())) {
                       accvoucher.setMd(accvoucher2.getMd());
                       break;
                   }
               }
               for (Accvoucher accvoucher3 : t3) {
                   if (accvoucher.getUniqueCode().equals(accvoucher3.getUniqueCode())) {
                       accvoucher.setCdigest(accvoucher3.getCdigest());
                       break;
                   }
               }
           }
           return Mono.just(t1);
       });
    }

    @Transactional
    @PostMapping("/findPrintDataByCondition")
    public Mono<R> findPrintDataByCondition(@RequestBody Map map) {
        //条件查询
        if (map.keySet().size() != 2) {
            return Mono.just(R.ok().setResult(new ArrayList<>()));
        }
        Map<String, String> constantMap = (HashMap<String, String>) map.get("constant");
        Map<String, Object> variableMap = (HashMap<String, Object>) map.get("variable");
        String queryType = constantMap.get("queryType").toString();
        Mono<R> rMono = null;
        AtomicReference<Integer> totalAR = new AtomicReference();
        if (queryType.equals("1")) {
            if (variableMap.get("printingMethod").toString().equals("1")) {
                List<String> selectKeys = JSON.parseArray(variableMap.get("selectKeys").toString(), String.class);
                rMono = accvoucherRepository.findAllByUniqueCodes(selectKeys).collectList().cache().map(list -> R.ok().setResult(list));
            } else {
                rMono = accvoucherRepository.findAllVoucherDetailByIyperiod("intervalStart", "intervalEnd").collectList().cache().map(list -> R.ok().setResult(list));
            }
        } else {
            String periodStart = variableMap.get("periodStart").toString().replace("-", "");
            String periodEnd = variableMap.get("periodEnd").toString().replace("-", "");
            rMono = accvoucherRepository.findAllVoucherDetailByIyperiod(periodStart, periodEnd).collectList().cache().map(list -> printFilter(list, variableMap)).map(list -> R.ok().setResult(list));
        }
        return rMono;
    }


    @Transactional
    @PostMapping("/findAllCashierPingZhengList")
    public Mono<R> findAllCashierPingZhengList(@RequestBody Map map) {
        //条件查询
        String queryMark = map.get("queryMark").toString();
        int page = Integer.parseInt(map.get("page").toString());
        int pageSize = Integer.parseInt(map.get("size").toString());
        Map<String, String> variableMap = ((HashMap<String, HashMap<String, String>>) map.get("condition")).get("variable");
        String intervalStart = variableMap.get("periodStart").replaceAll("-", "");
        String intervalEnd = variableMap.get("periodEnd").replaceAll("-", "");
        ;
        String dateStart = variableMap.get("dateStart");
        String dateEnd = variableMap.get("dateEnd");
        String filterMark = map.get("filterMark").toString();
        Mono<R> rMono = null;
        AtomicReference<Integer> totalAR = new AtomicReference();
        if (StrUtil.isNotBlank(intervalStart) && StrUtil.isNotBlank(intervalEnd)) {
            String thisYear = intervalStart.substring(0, 4);
            if (queryMark.equals("1")) {
                rMono = accvoucherRepository.findAllVoucherDetailByIyperiod(intervalStart, intervalEnd).collectList().flatMap(list -> codeKemuRepository.findAllByYearAndCashierConditions(thisYear).collectList().cache().map(codeList -> filterUnCashierConditionCode(list, codeList, filterMark))).map(list -> queryFilter(list, map)).map(list -> splitList(countFilter(list, 8), page, pageSize, totalAR)).map(list -> R.ok().setResult(CollectOfUtils.mapof("total", totalAR.get(), "items", list)));
            } else { // 汇总
                rMono =  assemblyVoucherPoolOrDetails(intervalStart, intervalEnd,"QJ").flatMap(list -> codeKemuRepository.findAllByYearAndCashierConditions(thisYear).collectList().map(codeList -> filterUnCashierConditionCode(list, codeList, filterMark))).map(list -> splitList(queryFilter(list, map), page, pageSize, totalAR)).map(list -> R.ok().setResult(CollectOfUtils.mapof("total", totalAR.get(), "items", list)));
            }
        } else if (StrUtil.isNotBlank(dateStart) && StrUtil.isNotBlank(dateEnd)) {
            String thisYear = dateStart.substring(0, 4);
            if (queryMark.equals("1")) {
                rMono = accvoucherRepository.findAllVoucherDetailByDate(dateStart.trim(), dateEnd.trim()).collectList().cache().flatMap(list -> codeKemuRepository.findAllByYearAndCashierConditions(thisYear).collectList().map(codeList -> filterUnCashierConditionCode(list, codeList, filterMark))).map(list -> queryFilter(list, map)).map(list -> splitList(countFilter(list, 8), page, pageSize, totalAR)).map(list -> R.ok().setResult(CollectOfUtils.mapof("total", totalAR.get(), "items", list)));
            } else { // 汇总
                rMono = assemblyVoucherPoolOrDetails(dateStart.trim(), dateEnd.trim(),"DATE").flatMap(list -> codeKemuRepository.findAllByYearAndCashierConditions(thisYear).collectList().map(codeList -> filterUnCashierConditionCode(list, codeList, filterMark))).map(list -> splitList(queryFilter(list, map), page, pageSize, totalAR)).map(list -> R.ok().setResult(CollectOfUtils.mapof("total", totalAR.get(), "items", list)));
            }
        }
        return rMono;
    }

    @PostMapping("/findAllAccvoucherXianJin")
    public Mono<R> findAllAccvoucherXianJin(@RequestBody Map map) {
        //条件查询
        if (map.keySet().size() == 2) {
            return Mono.just(R.ok().setResult(CollectOfUtils.mapof("total", 0, "items", new ArrayList<>())));
        }
        String queryMark = map.get("queryMark").toString();
        int page = Integer.parseInt(map.get("page").toString());
        int pageSize = Integer.parseInt(map.get("size").toString());
        Map<String, String> variableMap = ((HashMap<String, HashMap<String, String>>) map.get("condition")).get("variable");
        String intervalStart = variableMap.get("periodStart").replaceAll("-", "");
        String intervalEnd = variableMap.get("periodEnd").replaceAll("-", "");
        String accId = map.get("accId").toString();
        map.put("ifrag", "0");
        Mono<R> rMono = null;
        AtomicReference<Integer> totalAR = new AtomicReference();
        AtomicReference<Integer> index = new AtomicReference(1);
        if (StrUtil.isNotBlank(intervalStart) && StrUtil.isNotBlank(intervalEnd)) {
            rMono = accvoucherRepository.findAllVoucherDetailByIyperiodAndTenantId(intervalStart, intervalEnd, accId + "-" + intervalStart.substring(0, 4)).collectList()
                    .map(list -> splitList(countFilter(list, 8), page, pageSize, totalAR)).map(list -> {
                        //序号
                        list.forEach(v -> v.setCdfine30(index.getAndSet(index.get() + 1).toString()));
                        return list;
                    }).map(list -> R.ok().setResult(CollectOfUtils.mapof("total", totalAR.get(), "items", list)));
        }
        return rMono;
    }

    @GetMapping("dateTreeXianJin")
    public Mono<R> dateTreeXianJin(String yearMonth) {
        return accvoucherRepository.findAllVoucherTreeByDbillDateLike(yearMonth + "%").collectList().flatMap(alist -> {
            return kmCashFlowRepository.findAll().collectList().map(pList -> {
                List<String> p = pList.stream().map(v -> v.getCcode()).collect(Collectors.toList());
                List<Accvoucher> collect = alist.stream().filter(v -> p.contains((v.getCcode()))).collect(Collectors.toList());
                return collect;
            });
        }).map(list -> {
            Map<String, Map<String, Set<String>>> maps = new HashMap<>();
            for (Accvoucher acc : list) {
                if (maps.containsKey(acc.getCsign())) {
                    Map<String, Set<String>> map = maps.get(acc.getCsign());
                    Set<String> elem = map.containsKey(acc.getDbillDate()) ? map.get(acc.getDbillDate()) : new HashSet<>();
                    elem.add(acc.getInoId());
                    map.put(acc.getDbillDate(), elem);
                    maps.put(acc.getCsign(), map);
                } else {
                    Set<String> elem = new HashSet<>();
                    elem.add(acc.getInoId());
                    maps.put(acc.getCsign(), MapUtil.of(acc.getDbillDate(), elem));
                }
            }
            return maps;
        }).map(o -> R.ok().setResult(o));
    }

    @PostMapping("replacePingZheng")
    @Transactional
    public Mono<R> replacePingZheng(@RequestBody Map map) {
        try {
            if (map == null || map.keySet().size() == 0) return Mono.just(R.error());
            String type = map.get("type").toString();
            String before = map.get("before").toString();
            String after = map.get("after").toString();
            String operator = map.get("operator").toString();
            List<String> keys = (List<String>) map.get("selectedRowKeys");
            Mono<List<Accvoucher>> queryList = accvoucherRepository.findAllByUniqueCodeAndCondition(keys).collectList();
            return queryList.flatMap(list -> {
                Task task = new Task().setCaozuoUnique(operator).setFunctionModule("凭证内容替换").setTime(DateUtil.now()).setState("1").setIyear(DateUtil.thisYear() + "").setMethod("替换");
                return taskRepository.save(task).map(o -> list);
            }).flatMap(list -> {
                List<Accvoucher> modifyList = new ArrayList<>();
                for (Accvoucher accvoucher : list) {
                    Object value = null;
                    if (type.equals("cdfine")) { // 当为辅助核算时
                        String cdfineValue = map.get("cdfineValue").toString();
                        value = ReflectionUtil.getValue(accvoucher, cdfineValue);
                    } else {//
                        value = ReflectionUtil.getValue(accvoucher, type);
                    }
                    if (null != value) {
                        if (type.equals("ccode") && value.toString().equals(before.split("-")[0])) { //替换为会计科目时
                            String[] split = after.split("-");
                            accvoucher.setCcode(split[0]);
                            accvoucher.setCcodeName(split[1]);
                            modifyList.add(accvoucher);
                        } else if (type.equals("cdigest") && value.toString().contains(before)) { //摘要模糊匹配
                            String newValue = value.toString().replaceAll(before, after);
                            ReflectionUtil.setValue(accvoucher, type, newValue);
                            modifyList.add(accvoucher);
                        } else if (value.toString().equals(before)) { // 默认
                            ReflectionUtil.setValue(accvoucher, type, after);
                            modifyList.add(accvoucher);
                        }
                    }
                }
                return Mono.just(modifyList);
            }).flatMap(list -> accvoucherRepository.saveAll(list).collectList().map(dbList -> {
                int num = 0;
                if (dbList.size() > 0)
                    num = (new HashSet<>(dbList.stream().map(itm -> itm.getUniqueCode()).collect(Collectors.toList()))).size();
                return R.ok(num);
            }));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Mono.just(R.error()).flatMap(o -> taskRepository.deleteByIyearAndFunctionModule(DateUtil.thisYear() + "", "凭证内容替换").thenReturn(o));
    }

    @PostMapping("reviewPingZheng")
    @Transactional
    public Mono<R> reviewPingZheng(@RequestBody Map map) {
        try {
            String scopeCondition = map.get("scopeCondition").toString();
            String taskName = scopeCondition.equals("1") ? "审核" : "弃审";
            String reviewMan = map.get("operatorUserName").toString();
            VoucherBusCheckVo reusltVo = new VoucherBusCheckVo();
            List<String> keys = (List<String>) map.get("selectedRowKeys");
            List<String> interval = ListUtil.toList(map.get("thisInterval").toString().split(" ~ "));
            Map<String, Boolean> financialParameters = (Map<String, Boolean>) map.get("financialParameters");
            Boolean selectCashier = financialParameters.get("icashier"); // 出纳必须出纳签字
            Boolean selectMaker = financialParameters.get("iverify"); // 制单与审批同一人
            Boolean selectMakerNo = financialParameters.get("iverifyCancel"); // 取消需为同一人
            reusltVo.setSelectNumber(keys.size());
            Mono<List<Accvoucher>> queryList = accvoucherRepository.findAllByUniqueCodeAndCondition(keys).collectList();
            return queryList.flatMap(list -> {
                Task task = new Task().setCaozuoUnique(reviewMan).setFunctionModule(taskName + "凭证").setTime(DateUtil.now()).setState("1").setIyear(DateUtil.thisYear() + "").setMethod(taskName);
                return taskRepository.save(task).map(o -> list);
            }).flatMap(list -> {
                // 获取出纳科目集合
                Mono<List<CodeKemu>> cashiersMono = codeKemuRepository.findAllByYearAndCashierConditions(interval.get(0).substring(0, 4)).collectList();
                // 获取全部末级科目
                Mono<List<CodeKemu>> lastsMono = codeKemuRepository.findAllByBendAndIyearOrderByCcode("1", interval.get(0).substring(0, 4)).collectList();
                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("voucherList", list);
                if (list.size() == 0 || !selectCashier) return lastsMono.flatMap(lastList -> {
                    resultMap.put("lastCodeList", lastList);
                    return Mono.just(resultMap);
                });
                return Mono.zip(cashiersMono, lastsMono).flatMap(codeList -> {
                    resultMap.put("cashierCodeList", codeList.getT1());
                    resultMap.put("lastCodeList", codeList.getT2());
                    return Mono.just(resultMap);
                });
            }).flatMap(resultMap -> {
                Map<String, Object> a = resultMap;
                List<Accvoucher> voucherList = (List<Accvoucher>) resultMap.get("voucherList");
                // 获取指定年份所有为现金、银行与等物的科目
                List<CodeKemu> cashierCodeList = (List<CodeKemu>) resultMap.get("cashierCodeList");
                List<CodeKemu> lastCodeList = (List<CodeKemu>) resultMap.get("lastCodeList");
                List<Map<String, String>> errorList = new ArrayList<>();
                List<Accvoucher> passList = new ArrayList<>();
                String reviewDate = DateUtil.today();
                String thisCnInoid = "";
                for (Accvoucher accvoucher : voucherList) {
                    List<CodeKemu> kemus = lastCodeList.stream().filter(item -> item.getCcode().equals(accvoucher.getCcode())).collect(Collectors.toList());
                    if (selectCashier && cashierCodeList.size() > 0) {
                        if (StrUtil.isBlank(thisCnInoid) || !thisCnInoid.equals(accvoucher.getInoId())) {
                            thisCnInoid = checkVoucherIsCashierVoucher(voucherList, cashierCodeList, accvoucher);
                        }
                    }
                    if (scopeCondition.equals("1") && selectCashier && StrUtil.isNotBlank(thisCnInoid) && StrUtil.isBlank(accvoucher.getCcashier())) { // 出纳凭证 且未签字
                        errorList.add(CollectOfUtils.mapof("dbillDate", accvoucher.getDbillDate(), "inoId", accvoucher.getInoId(), "errorType", "6"));
                    } else if (scopeCondition.equals("1") && !selectMaker && accvoucher.getCbill().equals(reviewMan)) { // 制单人与审核人不允许同一人
                        errorList.add(CollectOfUtils.mapof("dbillDate", accvoucher.getDbillDate(), "inoId", accvoucher.getInoId(), "errorType", "12"));
                    } else if (scopeCondition.equals("0") && selectMakerNo && null != accvoucher.getCcheck() && !accvoucher.getCcheck().equals(reviewMan)) { // 需为同一人
                        errorList.add(CollectOfUtils.mapof("dbillDate", accvoucher.getDbillDate(), "inoId", accvoucher.getInoId(), "errorType", "1"));
                    }/* else if (scopeCondition.equals("1") && accvoucher.getIfrag().equals("1")) {
                                errorList.add(CollectOfUtils.mapof("dbillDate", accvoucher.getDbillDate(), "inoId", accvoucher.getInoId(), "errorType", "2"));
                            }*/ else if (scopeCondition.equals("1") && DateUtil.compare(DateUtil.parse(accvoucher.getDbillDate()), DateUtil.parse(reviewDate)) > 0) {
                        errorList.add(CollectOfUtils.mapof("dbillDate", accvoucher.getDbillDate(), "inoId", accvoucher.getInoId(), "errorType", "3"));
                    } else if (scopeCondition.equals("1") && accvoucher.getIfrag().equals("3")) {
                        errorList.add(CollectOfUtils.mapof("dbillDate", accvoucher.getDbillDate(), "inoId", accvoucher.getInoId(), "errorType", "4"));
                    } else if (scopeCondition.equals("1") && accvoucher.getIfrag().equals("2")) {
                        errorList.add(CollectOfUtils.mapof("dbillDate", accvoucher.getDbillDate(), "inoId", accvoucher.getInoId(), "errorType", "5"));
                    } else if (scopeCondition.equals("1") && kemus.size() == 0) {
                        errorList.add(CollectOfUtils.mapof("dbillDate", accvoucher.getDbillDate(), "inoId", accvoucher.getInoId(), "errorType", "8"));
                    } else if (scopeCondition.equals("1") && ((kemus.get(0).getBdept().equals("1") && StrUtil.isBlank(accvoucher.getCdeptId())) || (kemus.get(0).getBperson().equals("1") && StrUtil.isBlank(accvoucher.getCpersonId())) || (kemus.get(0).getBcus().equals("1") && StrUtil.isBlank(accvoucher.getCcusId())) || (kemus.get(0).getBsup().equals("1") && StrUtil.isBlank(accvoucher.getCsupId()) || (kemus.get(0).getBitem().equals("1") && StrUtil.isBlank(accvoucher.getCpersonId()))))) {
                        errorList.add(CollectOfUtils.mapof("dbillDate", accvoucher.getDbillDate(), "inoId", accvoucher.getInoId(), "errorType", "9"));
                    } else if ((scopeCondition.equals("1") && StrUtil.isNotBlank(accvoucher.getCcheck())) || (scopeCondition.equals("0") && StrUtil.isBlank(accvoucher.getCcheck()))) {
                        errorList.add(CollectOfUtils.mapof("dbillDate", accvoucher.getDbillDate(), "inoId", accvoucher.getInoId(), "errorType", scopeCondition.equals("1") ? "10" : "11"));
                    } else { // 校验
                        passList.add(accvoucher);
                    }
                }
                // 判断借贷平衡
                Set<String> keysets = new HashSet<>();
                for (String thisInfo : new HashSet<>(passList.stream().map(item -> item.getDbillDate().substring(0, 7) + "==" + item.getInoId()).collect(Collectors.toList()))) {
                    double mdSum = 0;
                    double mcSum = 0;
                    Accvoucher thisVoucher = null;
                    String yearMonth = thisInfo.split("==")[0];
                    String thisInoid = thisInfo.split("==")[1];
                    for (Accvoucher accvoucher : passList) {
                        if (ObjectUtil.equal(accvoucher.getDbillDate().substring(0, 7), yearMonth) && ObjectUtil.equal(accvoucher.getInoId(), thisInoid)) {
                            mdSum += StrUtil.isBlank(accvoucher.getMd()) ? 0 : new BigDecimal(accvoucher.getMd()).doubleValue();
                            mcSum += StrUtil.isBlank(accvoucher.getMc()) ? 0 : new BigDecimal(accvoucher.getMc()).doubleValue();
                            if (null == thisVoucher) thisVoucher = accvoucher;
                        }
                    }
                    if (null != thisVoucher && thisInoid.equals(thisVoucher.getInoId())) {
                        if (!NumberUtil.equals(new BigDecimal(mdSum).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(mcSum).setScale(2, BigDecimal.ROUND_HALF_UP))) {
                            errorList.add(CollectOfUtils.mapof("dbillDate", thisVoucher.getDbillDate(), "inoId", thisInoid, "errorType", "7"));
                        } else {
                            keysets.add(thisVoucher.getUniqueCode());
                        }
                    }
                }
                if (errorList.size() > 0) errorList = removeDuplicateEntries(errorList);
                reusltVo.setSuccessNumber(keysets.size());
                reusltVo.setErrorNumber(errorList.size() > 0 ? new HashSet<>(errorList.stream().map(item -> item.get("dbillDate") + "==" + item.get("inoId")).collect(Collectors.toList())).size() : 0);
                reusltVo.setErrorList(errorList);
                if (keysets.size() == 0)
                    return (scopeCondition.equals("1") ? interval.get(0).length() > 7 ? accvoucherRepository.countByCcheckByDate(interval.get(0), interval.get(1)) : accvoucherRepository.countByCcheckByPeriod(interval.get(0).replace("-", ""), interval.get(1).replace("-", "")) : Mono.just(0)).flatMap(num -> {
                        reusltVo.setSuccessNumber(0);
                        reusltVo.setPassNumber(num);
                        return taskRepository.deleteByIyearAndFunctionModule(DateUtil.thisYear() + "", taskName + "凭证").thenReturn(R.ok(reusltVo));
                    });
                return (scopeCondition.equals("1") ? accvoucherRepository.reviewVoucherByYaerAndUniqueCodes(passList.get(0).getIyear(), keysets, reviewMan, reviewDate) : accvoucherRepository.closeReviewVoucherByYaerAndUniqueCodes(passList.get(0).getIyear(), keysets)).collectList().flatMap(ressults -> {
                    reusltVo.setSuccessNumber(keysets.size());
                    return scopeCondition.equals("1") ? interval.get(0).length() > 7 ? accvoucherRepository.countByCcheckByDate(interval.get(0), interval.get(1)) : accvoucherRepository.countByCcheckByPeriod(interval.get(0).replace("-", ""), interval.get(1).replace("-", "")) : Mono.just(0);
                }).flatMap(total -> {
                            reusltVo.setPassNumber(total);
                            return taskRepository.deleteByIyearAndFunctionModule(DateUtil.thisYear() + "", taskName + "凭证").thenReturn(R.ok(reusltVo));
                        }

                );
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Mono.just(R.error()).flatMap(o -> taskRepository.deleteByIyearAndFunctionModule(DateUtil.thisYear() + "", "审核凭证").thenReturn(o));
    }

    private List<Map<String, String>> removeDuplicateEntries(List<Map<String, String>> list) {
        List<Map<String, String>> newList = new ArrayList<>();
        String thisInodValue = "";
        for (Map<String, String> map : list) {
            if (!StrUtil.equals(thisInodValue, map.get("inoId"))) {
                thisInodValue = map.get("inoId");
                newList.add(map);
            }
        }
        return newList;
    }

    @PostMapping("reviewPingZhengOld")
    @Transactional
    public Mono<R> reviewPingZhengOld(@RequestBody Map map) {
        try {
            String scopeCondition = map.get("scopeCondition").toString();
            Boolean selectMaker = Boolean.valueOf(map.get("selectMaker").toString());
            Boolean selectCashier = Boolean.valueOf(map.get("selectCashier").toString());
            Map<String, String> batchCondition = (HashMap<String, String>) map.get("batchCondition");
            Mono<List<Accvoucher>> queryList = null;
            String reviewMan = map.get("operatorUserName").toString();
            if (scopeCondition.equals("1")) {
                List<String> keys = (List<String>) map.get("selectedRowKeys");
                queryList = accvoucherRepository.findAllByUniqueCodeAndCondition(keys).collectList();
            } else {
                if (StrUtil.isNotBlank(batchCondition.get("voucherPeriod"))) {
                    queryList = accvoucherRepository.findAllByPeriodAndCondition(batchCondition.get("voucherPeriod").replaceAll("-", "")).collectList();
                } else {
                    queryList = accvoucherRepository.findAllByIntervalAndCondition(batchCondition.get("voucherDateStart"), batchCondition.get("voucherDateEnd")).collectList();
                }
            }
            return queryList.flatMap(list -> {
                Task task = new Task().setCaozuoUnique(reviewMan).setFunctionModule("审核凭证").setTime(DateUtil.now()).setState("1").setIyear(DateUtil.thisYear() + "").setMethod("审核");
                return taskRepository.save(task).map(o -> list);
            }).map(list -> voucherReviewFilter(list, batchCondition, selectMaker, reviewMan)).flatMap(list -> {
                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("voucherList", list);
                if (list.size() == 0 || !selectCashier) return Mono.just(resultMap);
                return codeKemuRepository.findAllByYearAndCashierConditions(list.get(0).getIyear()).collectList().map(codeList -> {
                    resultMap.put("codeList", codeList);
                    return resultMap;
                });
            }).flatMap(resultMap -> {
                Map<String, Object> a = resultMap;
                String ERROR_INFO = "";
                List<Accvoucher> voucherList = (List<Accvoucher>) resultMap.get("voucherList");
                if (voucherList.size() == 0) {
                    return taskRepository.deleteByIyearAndFunctionModule(DateUtil.thisYear() + "", "审核凭证").thenReturn(R.ok("共完成0张审核凭证！"));
                }
                // 获取指定年份所有为现金、银行与等物的科目
                if (selectCashier) {
                    List<CodeKemu> codeList = (List<CodeKemu>) resultMap.get("codeList");
                    if (codeList.size() > 0) {
                        for (Accvoucher accvoucher : voucherList) {
                            for (CodeKemu codeKemu : codeList) {
                                if (accvoucher.getCcode().equals(codeKemu.getCcode())) {
                                    ERROR_INFO = "出纳凭证请先进行出纳签字！选择的待审核制单日期：【" + accvoucher.getDbillDate() + "】凭证号:【" + accvoucher.getInoId() + "】为出纳凭证，" + "必须先进行出纳签字操作后才能进行审核凭证。";
                                    break;
                                }
                            }
                        }
                    }
                }
                if (selectCashier && StrUtil.isNotBlank(ERROR_INFO))
                    return taskRepository.deleteByIyearAndFunctionModule(DateUtil.thisYear() + "", "审核凭证").thenReturn(R.ok(ERROR_INFO));
                // 开始审核
                Set<String> keys = new HashSet<>();
                String reviewDate = DateUtil.today();
                for (Accvoucher accvoucher : voucherList) {
                    keys.add(accvoucher.getUniqueCode());
                }
                       /*return Mono.just(new ArrayList<>()).flatMap(ressults->
                               taskRepository.deleteByIyearAndFunctionModule(DateUtil.thisYear() + "", "审核凭证")
                                       .thenReturn(R.ok("共完成"+ressults.size()+"张审核凭证！")));*/
                return accvoucherRepository.reviewVoucherByYaerAndUniqueCodes(voucherList.get(0).getIyear(), keys, reviewMan, reviewDate).collectList().flatMap(ressults -> taskRepository.deleteByIyearAndFunctionModule(DateUtil.thisYear() + "", "审核凭证").thenReturn(R.ok("共完成" + keys.size() + "张凭证审核！")));
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Mono.just(R.error()).flatMap(o -> taskRepository.deleteByIyearAndFunctionModule(DateUtil.thisYear() + "", "审核凭证").thenReturn(o));
    }

    @PostMapping("closeReviewPingZheng")
    @Transactional
    public Mono<R> closeReviewPingZheng(@RequestBody Map map) {
        try {
            String scopeCondition = map.get("scopeCondition").toString();
            Boolean selectMaker = Boolean.valueOf(map.get("selectMaker").toString());
            Boolean selectCashier = Boolean.valueOf(map.get("selectCashier").toString());
            Map<String, String> batchCondition = (HashMap<String, String>) map.get("batchCondition");
            Mono<List<Accvoucher>> queryList = null;
            String reviewMan = map.get("operatorUserName").toString();
            if (scopeCondition.equals("1")) {
                List<String> keys = (List<String>) map.get("selectedRowKeys");
                queryList = accvoucherRepository.findAllByUniqueCodeAndConditionClose(keys).collectList();
            } else {
                if (StrUtil.isNotBlank(batchCondition.get("voucherPeriod"))) {
                    queryList = accvoucherRepository.findAllByPeriodAndConditionClose(batchCondition.get("voucherPeriod").replaceAll("-", "")).collectList();
                } else {
                    queryList = accvoucherRepository.findAllByIntervalAndConditionClose(batchCondition.get("voucherDateStart"), batchCondition.get("voucherDateEnd")).collectList();
                }
            }
            return queryList.flatMap(list -> {
                Task task = new Task().setCaozuoUnique(reviewMan).setFunctionModule("凭证弃审").setTime(DateUtil.now()).setState("1").setIyear(DateUtil.thisYear() + "").setMethod("弃审");
                return taskRepository.save(task).map(o -> list);
            }).map(list -> voucherReviewFilter(list, batchCondition, selectMaker, reviewMan)).flatMap(list -> {
                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("voucherList", list);
                return Mono.just(resultMap);
                       /* if (list.size() == 0 || !selectCashier)return Mono.just(resultMap);
                        return codeKemuRepository.findAllByYearAndCashierConditions(list.get(0).getIyear()).collectList()
                                .map(codeList->{
                                    resultMap.put("codeList",codeList);
                                    return resultMap;
                                });*/
            }).flatMap(resultMap -> {
                Map<String, Object> a = resultMap;
                String ERROR_INFO = "";
                List<Accvoucher> voucherList = (List<Accvoucher>) resultMap.get("voucherList");
                if (voucherList.size() == 0) {
                    return taskRepository.deleteByIyearAndFunctionModule(DateUtil.thisYear() + "", "凭证弃审").thenReturn(R.ok("共完成0张凭证弃审！"));
                }
                       /* // 获取指定年份所有为现金、银行与等物的科目
                        if (selectCashier){
                            List<CodeKemu>  codeList = (List<CodeKemu>) resultMap.get("codeList");
                            if (codeList.size() > 0){
                                for (Accvoucher accvoucher : voucherList) {
                                    for (CodeKemu codeKemu : codeList) {
                                        if (accvoucher.getCcode().equals(codeKemu.getCcode())){
                                            ERROR_INFO = "出纳凭证请先进行出纳签字！选择的待审核制单日期：【"+accvoucher.getDbillDate()+"】凭证号:【"+accvoucher.getInoId()+"】为出纳凭证，" +
                                                    "必须先进行出纳签字操作后才能进行审核凭证。";
                                            break;
                                        }
                                    }
                                }
                            }
                        }*/
                /*if (selectCashier && StrUtil.isNotBlank(ERROR_INFO))return taskRepository.deleteByIyearAndFunctionModule(DateUtil.thisYear() + "", "凭证弃审").thenReturn(R.ok(ERROR_INFO));*/
                // 开始审核
                Set<String> keys = new HashSet<>();
                for (Accvoucher accvoucher : voucherList) {
                    keys.add(accvoucher.getUniqueCode());
                }
                       /*return Mono.just(new ArrayList<>()).flatMap(ressults->
                               taskRepository.deleteByIyearAndFunctionModule(DateUtil.thisYear() + "", "凭证弃审")
                                       .thenReturn(R.ok("共完成"+ressults.size()+"张凭证弃审！")));*/
                return accvoucherRepository.closeReviewVoucherByYaerAndUniqueCodes(voucherList.get(0).getIyear(), keys).collectList().flatMap(ressults -> taskRepository.deleteByIyearAndFunctionModule(DateUtil.thisYear() + "", "凭证弃审").thenReturn(R.ok("共完成" + keys.size() + "张凭证弃审！")));
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Mono.just(R.error()).flatMap(o -> taskRepository.deleteByIyearAndFunctionModule(DateUtil.thisYear() + "", "凭证弃审").thenReturn(o));
    }


    @PostMapping("symbolPingZheng")
    @Transactional
    public Mono<R> symbolPingZheng(@RequestBody Map map) {
        try {
            String scopeCondition = map.get("scopeCondition").toString();
            String taskName = scopeCondition.equals("1") ? "签字" : "弃签";
            String reviewMan = map.get("operatorUserName").toString();
            VoucherBusCheckVo reusltVo = new VoucherBusCheckVo();
            List<String> keys = (List<String>) map.get("selectedRowKeys");
            List<String> interval = ListUtil.toList(map.get("thisInterval").toString().split(" ~ "));
            reusltVo.setSelectNumber(keys.size());
            Map<String, Boolean> financialParameters = (Map<String, Boolean>) map.get("financialParameters");
            Boolean selectCashier = financialParameters.get("icashier"); // 出纳必须出纳签字
            Boolean selectMakerNo = financialParameters.get("icdirectorNo"); // 取消需为同一人
            Mono<List<Accvoucher>> queryList = accvoucherRepository.findAllByUniqueCodeAndConditionSign(keys).collectList();
            return queryList.flatMap(list -> {
                Task task = new Task().setCaozuoUnique(reviewMan).setFunctionModule("主管" + taskName).setTime(DateUtil.now()).setState("1").setIyear(DateUtil.thisYear() + "").setMethod(taskName);
                return taskRepository.save(task).map(o -> list);
            }).flatMap(list -> {
                // 获取出纳科目集合
                Mono<List<CodeKemu>> cashiersMono = codeKemuRepository.findAllByYearAndCashierConditions(interval.get(0).substring(0, 4)).collectList();
                // 获取全部末级科目
                Mono<List<CodeKemu>> lastsMono = codeKemuRepository.findAllByBendAndIyearOrderByCcode("1", interval.get(0).substring(0, 4)).collectList();
                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("voucherList", list);
                if (list.size() == 0 || !selectCashier) return lastsMono.flatMap(lastList -> {
                    resultMap.put("lastCodeList", lastList);
                    return Mono.just(resultMap);
                });
                return Mono.zip(cashiersMono, lastsMono).flatMap(codeList -> {
                    resultMap.put("cashierCodeList", codeList.getT1());
                    resultMap.put("lastCodeList", codeList.getT2());
                    return Mono.just(resultMap);
                });
            }).flatMap(resultMap -> {
                Map<String, Object> a = resultMap;
                List<Accvoucher> voucherList = (List<Accvoucher>) resultMap.get("voucherList");
                // 获取指定年份所有为现金、银行与等物的科目
                List<CodeKemu> cashierCodeList = (List<CodeKemu>) resultMap.get("cashierCodeList");
                List<CodeKemu> lastCodeList = (List<CodeKemu>) resultMap.get("lastCodeList");
                List<Map<String, String>> errorList = new ArrayList<>();
                List<Accvoucher> passList = new ArrayList<>();
                String reviewDate = DateUtil.today();
                String thisCnInoid = "";
                for (Accvoucher accvoucher : voucherList) {
                    List<CodeKemu> kemus = lastCodeList.stream().filter(item -> item.getCcode().equals(accvoucher.getCcode())).collect(Collectors.toList());
                    if (selectCashier && cashierCodeList.size() > 0) {
                        if (StrUtil.isBlank(thisCnInoid) || !thisCnInoid.equals(accvoucher.getInoId())) {
                            thisCnInoid = checkVoucherIsCashierVoucher(voucherList, cashierCodeList, accvoucher);
                        }
                    }
                    if (scopeCondition.equals("1") && selectCashier && StrUtil.isNotBlank(thisCnInoid) && StrUtil.isBlank(accvoucher.getCcashier())) { // 出纳凭证 且未签字
                        errorList.add(CollectOfUtils.mapof("dbillDate", accvoucher.getDbillDate(), "inoId", accvoucher.getInoId(), "errorType", "6"));
                    } else if (scopeCondition.equals("0") && selectMakerNo && null != accvoucher.getCdirector() && !accvoucher.getCdirector().equals(reviewMan)) { //需要为同一人
                        errorList.add(CollectOfUtils.mapof("dbillDate", accvoucher.getDbillDate(), "inoId", accvoucher.getInoId(), "errorType", "1"));
                    } /*else if (scopeCondition.equals("1") && accvoucher.getIfrag().equals("1")) {
                                errorList.add(CollectOfUtils.mapof("dbillDate", accvoucher.getDbillDate(), "inoId", accvoucher.getInoId(), "errorType", "2"));
                            }*/ else if (scopeCondition.equals("1") && DateUtil.compare(DateUtil.parse(accvoucher.getDbillDate()), DateUtil.parse(reviewDate)) > 0) {
                        errorList.add(CollectOfUtils.mapof("dbillDate", accvoucher.getDbillDate(), "inoId", accvoucher.getInoId(), "errorType", "3"));
                    } else if (scopeCondition.equals("1") && accvoucher.getIfrag().equals("3")) {
                        errorList.add(CollectOfUtils.mapof("dbillDate", accvoucher.getDbillDate(), "inoId", accvoucher.getInoId(), "errorType", "4"));
                    } else if (scopeCondition.equals("1") && accvoucher.getIfrag().equals("2")) {
                        errorList.add(CollectOfUtils.mapof("dbillDate", accvoucher.getDbillDate(), "inoId", accvoucher.getInoId(), "errorType", "5"));
                    } else if (scopeCondition.equals("1") && kemus.size() == 0) {
                        errorList.add(CollectOfUtils.mapof("dbillDate", accvoucher.getDbillDate(), "inoId", accvoucher.getInoId(), "errorType", "8"));
                    } else if (scopeCondition.equals("1") && ((kemus.get(0).getBdept().equals("1") && StrUtil.isBlank(accvoucher.getCdeptId())) || (kemus.get(0).getBperson().equals("1") && StrUtil.isBlank(accvoucher.getCpersonId())) || (kemus.get(0).getBcus().equals("1") && StrUtil.isBlank(accvoucher.getCcusId())) || (kemus.get(0).getBsup().equals("1") && StrUtil.isBlank(accvoucher.getCsupId()) || (kemus.get(0).getBitem().equals("1") && StrUtil.isBlank(accvoucher.getCpersonId()))))) {
                        errorList.add(CollectOfUtils.mapof("dbillDate", accvoucher.getDbillDate(), "inoId", accvoucher.getInoId(), "errorType", "9"));
                    } else if ((scopeCondition.equals("1") && StrUtil.isNotBlank(accvoucher.getCdirector())) || (scopeCondition.equals("0") && StrUtil.isBlank(accvoucher.getCdirector()))) {
                        errorList.add(CollectOfUtils.mapof("dbillDate", accvoucher.getDbillDate(), "inoId", accvoucher.getInoId(), "errorType", scopeCondition.equals("1") ? "10" : "11"));
                    } else { // 校验
                        passList.add(accvoucher);
                    }
                }
                // 判断借贷平衡
                Set<String> keysets = new HashSet<>();
                for (String thisInfo : new HashSet<>(passList.stream().map(item -> item.getDbillDate().substring(0, 7) + "==" + item.getInoId()).collect(Collectors.toList()))) {
                    double mdSum = 0;
                    double mcSum = 0;
                    Accvoucher thisVoucher = null;
                    String yearMonth = thisInfo.split("==")[0];
                    String thisInoid = thisInfo.split("==")[1];
                    for (Accvoucher accvoucher : passList) {
                        if (ObjectUtil.equal(accvoucher.getDbillDate().substring(0, 7), yearMonth) && ObjectUtil.equal(accvoucher.getInoId(), thisInoid)) {
                            mdSum += StrUtil.isBlank(accvoucher.getMd()) ? 0 : new BigDecimal(accvoucher.getMd()).doubleValue();
                            mcSum += StrUtil.isBlank(accvoucher.getMc()) ? 0 : new BigDecimal(accvoucher.getMc()).doubleValue();
                            if (null == thisVoucher) thisVoucher = accvoucher;
                        }
                    }
                    if (null != thisVoucher && thisInoid.equals(thisVoucher.getInoId())) {
                        if (!NumberUtil.equals(new BigDecimal(mdSum).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(mcSum).setScale(2, BigDecimal.ROUND_HALF_UP))) {
                            errorList.add(CollectOfUtils.mapof("dbillDate", thisVoucher.getDbillDate(), "inoId", thisInoid, "errorType", "7"));
                        } else {
                            keysets.add(thisVoucher.getUniqueCode());
                        }
                    }
                }
                if (errorList.size() > 0) errorList = removeDuplicateEntries(errorList);
                reusltVo.setSuccessNumber(keysets.size());
                reusltVo.setErrorNumber(errorList.size() > 0 ? new HashSet<>(errorList.stream().map(item -> item.get("dbillDate") + "==" + item.get("inoId")).collect(Collectors.toList())).size() : 0);
                reusltVo.setErrorList(errorList);
                if (keysets.size() == 0)
                    return (scopeCondition.equals("1") ? interval.get(0).length() > 7 ? accvoucherRepository.countByCdirectorByDate(interval.get(0), interval.get(1)) : accvoucherRepository.countByCdirectorByPeriod(interval.get(0).replace("-", ""), interval.get(1).replace("-", "")) : Mono.just(0)).flatMap(num -> {
                        reusltVo.setSuccessNumber(0);
                        reusltVo.setPassNumber(num);
                        return taskRepository.deleteByIyearAndFunctionModule(DateUtil.thisYear() + "", "主管" + taskName).thenReturn(R.ok(reusltVo));
                    });
                return (scopeCondition.equals("1") ? accvoucherRepository.symbolVoucherByYaerAndUniqueCodes(passList.get(0).getIyear(), keysets, reviewMan) : accvoucherRepository.closeSymbolVoucherByYaerAndUniqueCodes(passList.get(0).getIyear(), keysets)).collectList().flatMap(ressults -> {
                    reusltVo.setSuccessNumber(keysets.size());
                    return scopeCondition.equals("1") ? interval.get(0).length() > 7 ? accvoucherRepository.countByCdirectorByDate(interval.get(0), interval.get(1)) : accvoucherRepository.countByCdirectorByPeriod(interval.get(0).replace("-", ""), interval.get(1).replace("-", "")) : Mono.just(0);
                }).flatMap(total -> {
                            reusltVo.setPassNumber(total);
                            return taskRepository.deleteByIyearAndFunctionModule(DateUtil.thisYear() + "", "主管" + taskName).thenReturn(R.ok(reusltVo));
                        }

                );
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Mono.just(R.error()).flatMap(o -> taskRepository.deleteByIyearAndFunctionModule(DateUtil.thisYear() + "", "审核凭证").thenReturn(o));
    }

    @PostMapping("symbolPingZhengOld")
    @Transactional
    public Mono<R> symbolPingZhengOld(@RequestBody Map map) {
        try {
            String scopeCondition = map.get("scopeCondition").toString();
            Boolean selectMaker = Boolean.valueOf(map.get("selectMaker").toString());
            Boolean selectCashier = Boolean.valueOf(map.get("selectCashier").toString());
            Map<String, String> batchCondition = (HashMap<String, String>) map.get("batchCondition");
            Mono<List<Accvoucher>> queryList = null;
            String reviewMan = map.get("operatorUserName").toString();
            ;
            if (scopeCondition.equals("1")) {
                List<String> keys = (List<String>) map.get("selectedRowKeys");
                queryList = accvoucherRepository.findAllByUniqueCodeAndConditionSign(keys).collectList();
            } else {
                if (StrUtil.isNotBlank(batchCondition.get("voucherPeriod"))) {
                    queryList = accvoucherRepository.findAllByPeriodAndConditionSign(batchCondition.get("voucherPeriod").replaceAll("-", "")).collectList();
                } else {
                    queryList = accvoucherRepository.findAllByIntervalAndConditionSign(batchCondition.get("voucherDateStart"), batchCondition.get("voucherDateEnd")).collectList();
                }
            }
            return queryList.flatMap(list -> {
                Task task = new Task().setCaozuoUnique(reviewMan).setFunctionModule("主管签字").setTime(DateUtil.now()).setState("1").setIyear(DateUtil.thisYear() + "").setMethod("签字");
                return taskRepository.save(task).map(o -> list);
            }).map(list -> voucherSignFilter(list, batchCondition, selectMaker, reviewMan)).flatMap(list -> {
                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("voucherList", list);
                return Mono.just(resultMap);
                       /* if (list.size() == 0 || !selectCashier)return Mono.just(resultMap);
                        return codeKemuRepository.findAllByYearAndCashierConditions(list.get(0).getIyear()).collectList()
                                .map(codeList->{
                                    resultMap.put("codeList",codeList);
                                    return resultMap;
                                });*/
            }).flatMap(resultMap -> {
                Map<String, Object> a = resultMap;
                String ERROR_INFO = "";
                List<Accvoucher> voucherList = (List<Accvoucher>) resultMap.get("voucherList");
                if (voucherList.size() == 0) {
                    return taskRepository.deleteByIyearAndFunctionModule(DateUtil.thisYear() + "", "主管签字").thenReturn(R.ok("共完成0张凭证签字！"));
                }
                // 获取指定年份所有为现金、银行与等物的科目
                     /*   if (selectCashier){
                            List<CodeKemu>  codeList = (List<CodeKemu>) resultMap.get("codeList");
                            if (codeList.size() > 0){
                                for (Accvoucher accvoucher : voucherList) {
                                    for (CodeKemu codeKemu : codeList) {
                                        if (accvoucher.getCcode().equals(codeKemu.getCcode())){
                                            ERROR_INFO = "出纳凭证请先进行出纳签字！选择的待审核制单日期：【"+accvoucher.getDbillDate()+"】凭证号:【"+accvoucher.getInoId()+"】为出纳凭证，" +
                                                    "必须先进行出纳签字操作后才能进行审核凭证。";
                                            break;
                                        }
                                    }
                                }
                            }
                        }*/
                for (Accvoucher accvoucher : voucherList) {
                    if (StrUtil.isBlank(accvoucher.getCcheck())) {
                        ERROR_INFO = "主管签字请先进行审核凭证！选择的待签字制单日期：【" + accvoucher.getDbillDate() + "】凭证号:【" + accvoucher.getInoId() + "】未进行审核凭证，" + "必须先进行审核凭证操作后才能进行主管签字。";
                        break;
                    }
                }
                if (/*selectCashier &&*/ StrUtil.isNotBlank(ERROR_INFO))
                    return taskRepository.deleteByIyearAndFunctionModule(DateUtil.thisYear() + "", "主管签字").thenReturn(R.ok(ERROR_INFO));
                // 开始审核
                Set<String> keys = new HashSet<>();
                //String reviewDate = DateUtil.today();
                for (Accvoucher accvoucher : voucherList) {
                    keys.add(accvoucher.getUniqueCode());
                }
                       /*return Mono.just(new ArrayList<>()).flatMap(ressults->
                               taskRepository.deleteByIyearAndFunctionModule(DateUtil.thisYear() + "", "主管签字")
                                       .thenReturn(R.ok("共完成"+keys.size()+"张凭证签字！")));*/
                return accvoucherRepository.symbolVoucherByYaerAndUniqueCodes(voucherList.get(0).getIyear(), keys, reviewMan).collectList().flatMap(ressults -> taskRepository.deleteByIyearAndFunctionModule(DateUtil.thisYear() + "", "主管签字").thenReturn(R.ok("共完成" + keys.size() + "张凭证签字！")));
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Mono.just(R.error()).flatMap(o -> taskRepository.deleteByIyearAndFunctionModule(DateUtil.thisYear() + "", "主管签字").thenReturn(o));
    }

    @PostMapping("closesymbolPingZheng")
    @Transactional
    public Mono<R> closesymbolPingZheng(@RequestBody Map map) {
        try {
            String scopeCondition = map.get("scopeCondition").toString();
            Boolean selectMaker = Boolean.valueOf(map.get("selectMaker").toString());
            Boolean selectCashier = Boolean.valueOf(map.get("selectCashier").toString());
            Map<String, String> batchCondition = (HashMap<String, String>) map.get("batchCondition");
            Mono<List<Accvoucher>> queryList = null;
            String reviewMan = map.get("operatorUserName").toString();
            ;
            if (scopeCondition.equals("1")) {
                List<String> keys = (List<String>) map.get("selectedRowKeys");
                queryList = accvoucherRepository.findAllByUniqueCodeAndConditionSignClose(keys).collectList();
            } else {
                if (StrUtil.isNotBlank(batchCondition.get("voucherPeriod"))) {
                    queryList = accvoucherRepository.findAllByPeriodAndConditionSignClose(batchCondition.get("voucherPeriod").replaceAll("-", "")).collectList();
                } else {
                    queryList = accvoucherRepository.findAllByIntervalAndConditionSignClose(batchCondition.get("voucherDateStart"), batchCondition.get("voucherDateEnd")).collectList();
                }
            }
            return queryList.flatMap(list -> {
                Task task = new Task().setCaozuoUnique(reviewMan).setFunctionModule("主管取签").setTime(DateUtil.now()).setState("1").setIyear(DateUtil.thisYear() + "").setMethod("取签");
                return taskRepository.save(task).map(o -> list);
            }).map(list -> voucherSignFilter(list, batchCondition, selectMaker, reviewMan)).flatMap(list -> {
                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("voucherList", list);
                return Mono.just(resultMap);
                       /* if (list.size() == 0 || !selectCashier)return Mono.just(resultMap);
                        return codeKemuRepository.findAllByYearAndCashierConditions(list.get(0).getIyear()).collectList()
                                .map(codeList->{
                                    resultMap.put("codeList",codeList);
                                    return resultMap;
                                });*/
            }).flatMap(resultMap -> {
                Map<String, Object> a = resultMap;
                String ERROR_INFO = "";
                List<Accvoucher> voucherList = (List<Accvoucher>) resultMap.get("voucherList");
                if (voucherList.size() == 0) {
                    return taskRepository.deleteByIyearAndFunctionModule(DateUtil.thisYear() + "", "主管取签").thenReturn(R.ok("共完成0张凭证取消签字！"));
                }
                // 获取指定年份所有为现金、银行与等物的科目
                     /*   if (selectCashier){
                            List<CodeKemu>  codeList = (List<CodeKemu>) resultMap.get("codeList");
                            if (codeList.size() > 0){
                                for (Accvoucher accvoucher : voucherList) {
                                    for (CodeKemu codeKemu : codeList) {
                                        if (accvoucher.getCcode().equals(codeKemu.getCcode())){
                                            ERROR_INFO = "出纳凭证请先进行出纳签字！选择的待审核制单日期：【"+accvoucher.getDbillDate()+"】凭证号:【"+accvoucher.getInoId()+"】为出纳凭证，" +
                                                    "必须先进行出纳签字操作后才能进行审核凭证。";
                                            break;
                                        }
                                    }
                                }
                            }
                        }*/
                       /* for (Accvoucher accvoucher : voucherList) {
                            if (StrUtil.isBlank(accvoucher.getCcheck())){
                                ERROR_INFO = "主管签字请先进行审核凭证！选择的待签字制单日期：【"+accvoucher.getDbillDate()+"】凭证号:【"+accvoucher.getInoId()+"】未进行审核凭证，" +
                                        "必须先进行审核凭证操作后才能进行主管取消签字。";
                                break;
                            }
                        }
                        if (/*selectCashier && StrUtil.isNotBlank(ERROR_INFO))return taskRepository.deleteByIyearAndFunctionModule(DateUtil.thisYear() + "", "主管签字").thenReturn(R.ok(ERROR_INFO));*/
                // 开始审核
                Set<String> keys = new HashSet<>();
                //String reviewDate = DateUtil.today();
                for (Accvoucher accvoucher : voucherList) {
                    keys.add(accvoucher.getUniqueCode());
                }
                       /*return Mono.just(new ArrayList<>()).flatMap(ressults->
                               taskRepository.deleteByIyearAndFunctionModule(DateUtil.thisYear() + "", "主管签字")
                                       .thenReturn(R.ok("共完成"+keys.size()+"张凭证签字！")));*/
                return accvoucherRepository.closeSymbolVoucherByYaerAndUniqueCodes(voucherList.get(0).getIyear(), keys).collectList().flatMap(ressults -> taskRepository.deleteByIyearAndFunctionModule(DateUtil.thisYear() + "", "主管取签").thenReturn(R.ok("共完成" + keys.size() + "张凭证取消签字！")));
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Mono.just(R.error()).flatMap(o -> taskRepository.deleteByIyearAndFunctionModule(DateUtil.thisYear() + "", "主管取签").thenReturn(o));
    }


    @PostMapping("bookPingZheng")
    @Transactional
    public Mono<R> bookPingZheng(@RequestBody Map map) {
        try {
            String scopeCondition = map.get("scopeCondition").toString();
            String taskName = scopeCondition.equals("1") ? "记账" : "取记";

            String reviewMan = map.get("operatorUserName").toString();
            VoucherBusCheckVo reusltVo = new VoucherBusCheckVo();
            List<String> keys = (List<String>) map.get("selectedRowKeys");
            List<String> interval = ListUtil.toList(map.get("thisInterval").toString().split(" ~ "));
            reusltVo.setSelectNumber(keys.size());
            Map<String, Boolean> financialParameters = (Map<String, Boolean>) map.get("financialParameters");
            Boolean selectCashier = financialParameters.get("icashier"); // 出纳必须出纳签字
            Boolean selectImanager = financialParameters.get("imanager"); // 凭证必须主管签字
            Boolean selectifVerify = financialParameters.get("ifVerify"); // 凭证必须审核
            Mono<List<Accvoucher>> queryList = accvoucherRepository.findAllByUniqueCodeAndConditionBook(keys).collectList();
            return queryList.flatMap(list -> {
                Task task = new Task().setCaozuoUnique(reviewMan).setFunctionModule("凭证" + taskName).setTime(DateUtil.now()).setState("1").setIyear(DateUtil.thisYear() + "").setMethod(taskName);
                return taskRepository.save(task).map(o -> list);
            }).flatMap(list -> {
                // 获取出纳科目集合
                Mono<List<CodeKemu>> cashiersMono = codeKemuRepository.findAllByYearAndCashierConditions(interval.get(0).substring(0, 4)).collectList();
                // 获取全部末级科目
                Mono<List<CodeKemu>> lastsMono = codeKemuRepository.findAllByBendAndIyearOrderByCcode("1", interval.get(0).substring(0, 4)).collectList();
                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("voucherList", list);
                if (list.size() == 0 || !selectCashier) return lastsMono.flatMap(lastList -> {
                    resultMap.put("lastCodeList", lastList);
                    return Mono.just(resultMap);
                });
                return Mono.zip(cashiersMono, lastsMono).flatMap(codeList -> {
                    resultMap.put("cashierCodeList", codeList.getT1());
                    resultMap.put("lastCodeList", codeList.getT2());
                    return Mono.just(resultMap);
                });
            }).flatMap(resultMap -> {
                Map<String, Object> a = resultMap;
                List<Accvoucher> voucherList = (List<Accvoucher>) resultMap.get("voucherList");
                // 获取指定年份所有为现金、银行与等物的科目
                List<CodeKemu> cashierCodeList = (List<CodeKemu>) resultMap.get("cashierCodeList");
                List<CodeKemu> lastCodeList = (List<CodeKemu>) resultMap.get("lastCodeList");
                List<Map<String, String>> errorList = new ArrayList<>();
                List<Accvoucher> passList = new ArrayList<>();
                String reviewDate = DateUtil.today();
                String thisCnInoid = "";
                for (Accvoucher accvoucher : voucherList) {
                    List<CodeKemu> kemus = lastCodeList.stream().filter(item -> item.getCcode().equals(accvoucher.getCcode())).collect(Collectors.toList());
                    if (selectCashier && cashierCodeList.size() > 0) {
                        if (StrUtil.isBlank(thisCnInoid) || !thisCnInoid.equals(accvoucher.getInoId())) {
                            thisCnInoid = checkVoucherIsCashierVoucher(voucherList, cashierCodeList, accvoucher);
                        }
                    }
                    if (scopeCondition.equals("1") && selectCashier && StrUtil.isNotBlank(thisCnInoid) && StrUtil.isBlank(accvoucher.getCcashier())) { // 出纳凭证 且未签字
                        errorList.add(CollectOfUtils.mapof("dbillDate", accvoucher.getDbillDate(), "inoId", accvoucher.getInoId(), "errorType", "6"));
                    } /*else if (scopeCondition.equals("1") && !selectMaker && accvoucher.getCbill().equals(reviewMan)) { // 制单人与审核人不能同人
                                errorList.add(CollectOfUtils.mapof("dbillDate", accvoucher.getDbillDate(), "inoId", accvoucher.getInoId(), "errorType", "1"));
                            } else if (scopeCondition.equals("1") && accvoucher.getIfrag().equals("1")) {
                                errorList.add(CollectOfUtils.mapof("dbillDate", accvoucher.getDbillDate(), "inoId", accvoucher.getInoId(), "errorType", "2"));
                            }*/ else if (scopeCondition.equals("1") && DateUtil.compare(DateUtil.parse(accvoucher.getDbillDate()), DateUtil.parse(reviewDate)) > 0) {
                        errorList.add(CollectOfUtils.mapof("dbillDate", accvoucher.getDbillDate(), "inoId", accvoucher.getInoId(), "errorType", "3"));
                    } else if (scopeCondition.equals("1") && accvoucher.getIfrag().equals("3")) {
                        errorList.add(CollectOfUtils.mapof("dbillDate", accvoucher.getDbillDate(), "inoId", accvoucher.getInoId(), "errorType", "4"));
                    } else if (scopeCondition.equals("1") && accvoucher.getIfrag().equals("2")) {
                        errorList.add(CollectOfUtils.mapof("dbillDate", accvoucher.getDbillDate(), "inoId", accvoucher.getInoId(), "errorType", "5"));
                    } else if (scopeCondition.equals("1") && kemus.size() == 0) {
                        errorList.add(CollectOfUtils.mapof("dbillDate", accvoucher.getDbillDate(), "inoId", accvoucher.getInoId(), "errorType", "8"));
                    } else if (scopeCondition.equals("1") && ((kemus.get(0).getBdept().equals("1") && StrUtil.isBlank(accvoucher.getCdeptId())) || (kemus.get(0).getBperson().equals("1") && StrUtil.isBlank(accvoucher.getCpersonId())) || (kemus.get(0).getBcus().equals("1") && StrUtil.isBlank(accvoucher.getCcusId())) || (kemus.get(0).getBsup().equals("1") && StrUtil.isBlank(accvoucher.getCsupId()) || (kemus.get(0).getBitem().equals("1") && StrUtil.isBlank(accvoucher.getCpersonId()))))) {
                        errorList.add(CollectOfUtils.mapof("dbillDate", accvoucher.getDbillDate(), "inoId", accvoucher.getInoId(), "errorType", "9"));
                    } else if ((scopeCondition.equals("1") && StrUtil.isNotBlank(accvoucher.getIbook())) || (scopeCondition.equals("0") && StrUtil.isBlank(accvoucher.getIbook()))) {
                        errorList.add(CollectOfUtils.mapof("dbillDate", accvoucher.getDbillDate(), "inoId", accvoucher.getInoId(), "errorType", scopeCondition.equals("1") ? "10" : "11"));
                    } else if (scopeCondition.equals("1") && selectifVerify && StrUtil.isBlank(accvoucher.getCcheck())) {
                        errorList.add(CollectOfUtils.mapof("dbillDate", accvoucher.getDbillDate(), "inoId", accvoucher.getInoId(), "errorType", "12"));
                    } else if (scopeCondition.equals("1") && selectImanager && StrUtil.isBlank(accvoucher.getCdirector())) {
                        errorList.add(CollectOfUtils.mapof("dbillDate", accvoucher.getDbillDate(), "inoId", accvoucher.getInoId(), "errorType", "13"));
                    } else { // 校验
                        passList.add(accvoucher);
                    }
                }
                // 判断借贷平衡
                Set<String> keysets = new HashSet<>();
                for (String thisInfo : new HashSet<>(passList.stream().map(item -> item.getDbillDate().substring(0, 7) + "==" + item.getInoId()).collect(Collectors.toList()))) {
                    double mdSum = 0;
                    double mcSum = 0;
                    Accvoucher thisVoucher = null;
                    String yearMonth = thisInfo.split("==")[0];
                    String thisInoid = thisInfo.split("==")[1];
                    for (Accvoucher accvoucher : passList) {
                        if (ObjectUtil.equal(accvoucher.getDbillDate().substring(0, 7), yearMonth) && ObjectUtil.equal(accvoucher.getInoId(), thisInoid)) {
                            mdSum += StrUtil.isBlank(accvoucher.getMd()) ? 0 : new BigDecimal(accvoucher.getMd()).doubleValue();
                            mcSum += StrUtil.isBlank(accvoucher.getMc()) ? 0 : new BigDecimal(accvoucher.getMc()).doubleValue();
                            if (null == thisVoucher) thisVoucher = accvoucher;
                        }
                    }
                    if (null != thisVoucher && thisInoid.equals(thisVoucher.getInoId())) {
                        if (!NumberUtil.equals(new BigDecimal(mdSum).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(mcSum).setScale(2, BigDecimal.ROUND_HALF_UP))) {
                            errorList.add(CollectOfUtils.mapof("dbillDate", thisVoucher.getDbillDate(), "inoId", thisInoid, "errorType", "7"));
                        } else {
                            keysets.add(thisVoucher.getUniqueCode());
                        }
                    }
                }
                if (errorList.size() > 0) errorList = removeDuplicateEntries(errorList);
                reusltVo.setSuccessNumber(keysets.size());
                reusltVo.setErrorNumber(errorList.size() > 0 ? new HashSet<>(errorList.stream().map(item -> item.get("dbillDate") + "==" + item.get("inoId")).collect(Collectors.toList())).size() : 0);
                reusltVo.setErrorList(errorList);
                if (keysets.size() == 0)
                    return (scopeCondition.equals("1") ? interval.get(0).length() > 7 ? accvoucherRepository.countByIbookByDate(interval.get(0), interval.get(1)) : accvoucherRepository.countByIbookByPeriod(interval.get(0).replace("-", ""), interval.get(1).replace("-", "")) : Mono.just(0)).flatMap(num -> {
                        reusltVo.setSuccessNumber(0);
                        reusltVo.setPassNumber(num);
                        return taskRepository.deleteByIyearAndFunctionModule(DateUtil.thisYear() + "", "凭证" + taskName).thenReturn(R.ok(reusltVo));
                    });
                return (scopeCondition.equals("1") ? accvoucherRepository.bookVoucherByYaerAndUniqueCodes(passList.get(0).getIyear(), keysets, reviewMan, reviewDate) : accvoucherRepository.closeBookVoucherByYaerAndUniqueCodes(passList.get(0).getIyear(), keysets)).collectList().flatMap(ressults -> {
                    reusltVo.setSuccessNumber(keysets.size());
                    return scopeCondition.equals("1") ? interval.get(0).length() > 7 ? accvoucherRepository.countByIbookByDate(interval.get(0), interval.get(1)) : accvoucherRepository.countByIbookByPeriod(interval.get(0).replace("-", ""), interval.get(1).replace("-", "")) : Mono.just(0);
                }).flatMap(total -> {
                            reusltVo.setPassNumber(total);
                            return taskRepository.deleteByIyearAndFunctionModule(DateUtil.thisYear() + "", "凭证" + taskName).thenReturn(R.ok(reusltVo));
                        }

                );
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Mono.just(R.error()).flatMap(o -> taskRepository.deleteByIyearAndFunctionModule(DateUtil.thisYear() + "", "审核凭证").thenReturn(R.ok(new VoucherBusCheckVo())));
    }

    @PostMapping("bookPingZhengOld")
    @Transactional
    public Mono<R> bookPingZhengOld(@RequestBody Map map) {
        try {
            String scopeCondition = map.get("scopeCondition").toString();
            Boolean selectMaker = Boolean.valueOf(map.get("selectMaker").toString());
            Boolean selectImanager = Boolean.valueOf(map.get("selectImanager").toString());
            Boolean selectCashier = Boolean.valueOf(map.get("selectCashier").toString());
            Boolean selectifVerify = Boolean.valueOf(map.get("selectifVerify").toString());
            Map<String, String> batchCondition = (HashMap<String, String>) map.get("batchCondition");
            Mono<List<Accvoucher>> queryList = null;
            String reviewMan = map.get("operatorUserName").toString();
            ;
            if (scopeCondition.equals("1")) {
                List<String> keys = (List<String>) map.get("selectedRowKeys");
                queryList = accvoucherRepository.findAllByUniqueCodeAndConditionBook(keys).collectList();
            } else {
                if (StrUtil.isNotBlank(batchCondition.get("voucherPeriod"))) {
                    queryList = accvoucherRepository.findAllByPeriodAndConditionBook(batchCondition.get("voucherPeriod").replaceAll("-", "")).collectList();
                } else {
                    queryList = accvoucherRepository.findAllByIntervalAndConditionBook(batchCondition.get("voucherDateStart"), batchCondition.get("voucherDateEnd")).collectList();
                }
            }
            return queryList.flatMap(list -> {
                Task task = new Task().setCaozuoUnique(reviewMan).setFunctionModule("凭证记账").setTime(DateUtil.now()).setState("1").setIyear(DateUtil.thisYear() + "").setMethod("记账");
                return taskRepository.save(task).map(o -> list);
            }).map(list -> voucherSignFilter(list, batchCondition, selectMaker, reviewMan)).flatMap(list -> {
                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("voucherList", list);
                if (list.size() == 0 || !selectCashier) return Mono.just(resultMap);
                return codeKemuRepository.findAllByYearAndCashierConditions(list.get(0).getIyear()).collectList().map(codeList -> {
                    resultMap.put("codeList", codeList);
                    return resultMap;
                });
            }).flatMap(resultMap -> {
                Map<String, Object> a = resultMap;
                String ERROR_INFO = "";
                List<Accvoucher> voucherList = (List<Accvoucher>) resultMap.get("voucherList");
                if (voucherList.size() == 0) {
                    return taskRepository.deleteByIyearAndFunctionModule(DateUtil.thisYear() + "", "凭证记账").thenReturn(R.ok("共完成0张凭证记账！"));
                }
                // 获取指定年份所有为现金、银行与等物的科目
                if (selectCashier) {
                    List<CodeKemu> codeList = (List<CodeKemu>) resultMap.get("codeList");
                    if (codeList.size() > 0) {
                        for (Accvoucher accvoucher : voucherList) {
                            for (CodeKemu codeKemu : codeList) {
                                if (accvoucher.getCcode().equals(codeKemu.getCcode())) {
                                    ERROR_INFO = "出纳凭证请先进行出纳签字！选择的待审核制单日期：【" + accvoucher.getDbillDate() + "】凭证号:【" + accvoucher.getInoId() + "】为出纳凭证，" + "必须先进行出纳签字操作后才能进行记账操作。";
                                    break;
                                }
                            }
                        }
                    }
                }
                if (StrUtil.isBlank(ERROR_INFO) && selectifVerify) {
                    for (Accvoucher accvoucher : voucherList) {
                        if (StrUtil.isBlank(accvoucher.getCcheck())) {
                            ERROR_INFO = "您选择的待记账凭证号制单日期：【" + accvoucher.getDbillDate() + "】凭证号:【" + accvoucher.getInoId() + "】没有进行审核，不能进行凭证记账！请先对该凭证进行审核，然后再进行凭证记账操作。";
                            break;
                        }
                    }
                }
                if (StrUtil.isBlank(ERROR_INFO) && selectImanager) {
                    for (Accvoucher accvoucher : voucherList) {
                        if (StrUtil.isBlank(accvoucher.getCdirector())) {
                            ERROR_INFO = "您选择的待记账凭证号制单日期：【" + accvoucher.getDbillDate() + "】凭证号:【" + accvoucher.getInoId() + "】没有进行主管签字，不能进行凭证记账！请先对该凭证进行审核，然后再进行凭证记账操作。";
                            break;
                        }
                    }
                }
                if (StrUtil.isNotBlank(ERROR_INFO))
                    return taskRepository.deleteByIyearAndFunctionModule(DateUtil.thisYear() + "", "凭证记账").thenReturn(R.ok(ERROR_INFO));
                // 开始审核
                Set<String> keys = new HashSet<>();
                String reviewDate = DateUtil.today();
                for (Accvoucher accvoucher : voucherList) {
                    keys.add(accvoucher.getUniqueCode());
                }
                       /*return Mono.just(new ArrayList<>()).flatMap(ressults->
                               taskRepository.deleteByIyearAndFunctionModule(DateUtil.thisYear() + "", "主管签字")
                                       .thenReturn(R.ok("共完成"+keys.size()+"张凭证签字！")));*/
                return accvoucherRepository.bookVoucherByYaerAndUniqueCodes(voucherList.get(0).getIyear(), keys, reviewMan, reviewDate).collectList().flatMap(ressults -> taskRepository.deleteByIyearAndFunctionModule(DateUtil.thisYear() + "", "凭证记账").thenReturn(R.ok("共完成" + keys.size() + "张凭证记账！")));
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Mono.just(R.error()).flatMap(o -> taskRepository.deleteByIyearAndFunctionModule(DateUtil.thisYear() + "", "主管签字").thenReturn(o));
    }

    @PostMapping("closeBookPingZheng")
    @Transactional
    public Mono<R> closeBookPingZheng(@RequestBody Map map) {
        try {
            String scopeCondition = map.get("scopeCondition").toString();
            Boolean selectMaker = Boolean.valueOf(map.get("selectMaker").toString());
            Boolean selectImanager = Boolean.valueOf(map.get("selectImanager").toString());
            Boolean selectCashier = Boolean.valueOf(map.get("selectCashier").toString());
            Boolean selectifVerify = Boolean.valueOf(map.get("selectifVerify").toString());
            Map<String, String> batchCondition = (HashMap<String, String>) map.get("batchCondition");
            Mono<List<Accvoucher>> queryList = null;
            String reviewMan = map.get("operatorUserName").toString();
            ;
            if (scopeCondition.equals("1")) {
                List<String> keys = (List<String>) map.get("selectedRowKeys");
                queryList = accvoucherRepository.findAllByUniqueCodeAndConditionBookClose(keys).collectList();
            } else {
                if (StrUtil.isNotBlank(batchCondition.get("voucherPeriod"))) {
                    queryList = accvoucherRepository.findAllByPeriodAndConditionBookClose(batchCondition.get("voucherPeriod").replaceAll("-", "")).collectList();
                } else {
                    queryList = accvoucherRepository.findAllByIntervalAndConditionBookClose(batchCondition.get("voucherDateStart"), batchCondition.get("voucherDateEnd")).collectList();
                }
            }
            return queryList.flatMap(list -> {
                Task task = new Task().setCaozuoUnique(reviewMan).setFunctionModule("取消记账").setTime(DateUtil.now()).setState("1").setIyear(DateUtil.thisYear() + "").setMethod("记账");
                return taskRepository.save(task).map(o -> list);
            }).map(list -> voucherSignFilter(list, batchCondition, selectMaker, reviewMan)).flatMap(list -> {
                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("voucherList", list);
                return Mono.just(resultMap);
                       /* if (list.size() == 0 || !selectCashier)return Mono.just(resultMap);
                        return codeKemuRepository.findAllByYearAndCashierConditions(list.get(0).getIyear()).collectList()
                                .map(codeList->{
                                    resultMap.put("codeList",codeList);
                                    return resultMap;
                                });*/
            }).flatMap(resultMap -> {
                Map<String, Object> a = resultMap;
                String ERROR_INFO = "";
                List<Accvoucher> voucherList = (List<Accvoucher>) resultMap.get("voucherList");
                if (voucherList.size() == 0) {
                    return taskRepository.deleteByIyearAndFunctionModule(DateUtil.thisYear() + "", "取消记账").thenReturn(R.ok("共完成0张凭证取消记账！"));
                }
                // 获取指定年份所有为现金、银行与等物的科目
                    /*    if (selectCashier){
                            List<CodeKemu>  codeList = (List<CodeKemu>) resultMap.get("codeList");
                            if (codeList.size() > 0){
                                for (Accvoucher accvoucher : voucherList) {
                                    for (CodeKemu codeKemu : codeList) {
                                        if (accvoucher.getCcode().equals(codeKemu.getCcode())){
                                            ERROR_INFO = "出纳凭证请先进行出纳签字！选择的待审核制单日期：【"+accvoucher.getDbillDate()+"】凭证号:【"+accvoucher.getInoId()+"】为出纳凭证，" +
                                                    "必须先进行出纳签字操作后才能进行记账操作。";
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        if (StrUtil.isBlank(ERROR_INFO) &&  selectifVerify){
                            for (Accvoucher accvoucher : voucherList) {
                                if (StrUtil.isBlank(accvoucher.getCcheck())){
                                    ERROR_INFO = "您选择的待记账凭证号制单日期：【"+accvoucher.getDbillDate()+"】凭证号:【"+accvoucher.getInoId()+"】没有进行审核，不能进行凭证记账！请先对该凭证进行审核，然后再进行凭证记账操作。";
                                    break;
                                }
                            }
                        }
                        if (StrUtil.isBlank(ERROR_INFO) &&  selectImanager){
                            for (Accvoucher accvoucher : voucherList) {
                                if (StrUtil.isBlank(accvoucher.getCdirector())){
                                    ERROR_INFO = "您选择的待记账凭证号制单日期：【"+accvoucher.getDbillDate()+"】凭证号:【"+accvoucher.getInoId()+"】没有进行主管签字，不能进行凭证记账！请先对该凭证进行审核，然后再进行凭证记账操作。";
                                    break;
                                }
                            }
                        }
                        if (StrUtil.isNotBlank(ERROR_INFO))return taskRepository.deleteByIyearAndFunctionModule(DateUtil.thisYear() + "", "凭证记账").thenReturn(R.ok(ERROR_INFO));*/
                // 开始审核
                Set<String> keys = new HashSet<>();
                //String reviewDate = DateUtil.today();
                for (Accvoucher accvoucher : voucherList) {
                    keys.add(accvoucher.getUniqueCode());
                }
                       /*return Mono.just(new ArrayList<>()).flatMap(ressults->
                               taskRepository.deleteByIyearAndFunctionModule(DateUtil.thisYear() + "", "主管签字")
                                       .thenReturn(R.ok("共完成"+keys.size()+"张凭证签字！")));*/
                return accvoucherRepository.closeBookVoucherByYaerAndUniqueCodes(voucherList.get(0).getIyear(), keys).collectList().flatMap(ressults -> taskRepository.deleteByIyearAndFunctionModule(DateUtil.thisYear() + "", "取消记账").thenReturn(R.ok("共完成" + keys.size() + "张凭证取消记账！")));
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Mono.just(R.error()).flatMap(o -> taskRepository.deleteByIyearAndFunctionModule(DateUtil.thisYear() + "", "取消记账").thenReturn(o));
    }

    @PostMapping("changeStatusPingZheng")
    @Transactional
    public Mono<R> changeStatusPingZheng(@RequestBody Map map) {
        String scopeCondition = map.get("setValue").toString();
        String taskName = (scopeCondition.equals("1") ? "作废" : "标错");
        try {
            String reviewMan = map.get("operatorUserName").toString();
            List<String> keysList = (List<String>) map.get("selectedRowKeys");
            Mono<List<Accvoucher>> queryList = accvoucherRepository.findAllByUniqueCodes(keysList).collectList();

            return queryList.flatMap(list -> {
                Task task = new Task().setCaozuoUnique(reviewMan).setFunctionModule(taskName + "凭证").setTime(DateUtil.now()).setState("1").setIyear(DateUtil.thisYear() + "").setMethod(taskName);
                return taskRepository.save(task).map(o -> list);
            }).flatMap(list -> {
                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("voucherList", list);
                return Mono.just(resultMap);
            }).flatMap(resultMap -> {
                String ERROR_INFO = "";
                List<Accvoucher> voucherList = (List<Accvoucher>) resultMap.get("voucherList");
                if (voucherList.size() == 0) {
                    return taskRepository.deleteByIyearAndFunctionModule(DateUtil.thisYear() + "", "凭证记账").thenReturn(R.ok("共完成0张凭证记账" + taskName + "！"));
                }
                for (Accvoucher accvoucher : voucherList) {
                    if (StrUtil.isNotBlank(accvoucher.getCcheck())) {
                        ERROR_INFO = "不能" + taskName + "凭证!您选择的凭证号制单日期：【" + accvoucher.getDbillDate() + "】凭证号:【" + accvoucher.getInoId() + "】已经进行审核，请先对该凭证进行弃审，然后再进行" + taskName + "凭证操作。";
                        break;
                    }
                    if (StrUtil.isNotBlank(accvoucher.getCcashier())) {
                        ERROR_INFO = "不能" + taskName + "凭证!您选择的凭证号制单日期：【" + accvoucher.getDbillDate() + "】凭证号:【" + accvoucher.getInoId() + "】已经出纳签字，请先对该凭证进行取消出纳签字，然后再进行" + taskName + "凭证操作。";
                        break;
                    }
                    if (StrUtil.isNotBlank(accvoucher.getCdirector())) {
                        ERROR_INFO = "不能" + taskName + "凭证!您选择的凭证号制单日期：【" + accvoucher.getDbillDate() + "】凭证号:【" + accvoucher.getInoId() + "】已经进行主管签字，请先对该凭证进行弃审，然后再进行" + taskName + "凭证操作。";
                        break;
                    }
                    if (StrUtil.isNotBlank(accvoucher.getCbook())) {
                        ERROR_INFO = "不能" + taskName + "凭证!您选择的凭证号制单日期：【" + accvoucher.getDbillDate() + "】凭证号:【" + accvoucher.getInoId() + "】已经进行记账，请先对该凭证进行弃审，然后再进行" + taskName + "凭证操作。";
                        break;
                    }
                    if (Objects.equals(accvoucher.getIfrag(), "1") || Objects.equals(accvoucher.getIfrag(), "3")) {
                        ERROR_INFO = "不能" + taskName + "凭证!您选择的凭证号制单日期：【" + accvoucher.getDbillDate() + "】凭证号:【" + accvoucher.getInoId() + "】为" + (accvoucher.getIfrag().equals("1") ? "作废" : "标错") + "凭证，请先对该凭证进行取消" + (accvoucher.getIfrag().equals("1") ? "作废" : "标错") + "操作，然后再进行" + taskName + "凭证操作。";
                        break;
                    }
                }

                if (StrUtil.isNotBlank(ERROR_INFO))
                    return taskRepository.deleteByIyearAndFunctionModule(DateUtil.thisYear() + "", taskName + "凭证").thenReturn(R.ok(ERROR_INFO));
                // 开始审核
                Set<String> keys = new HashSet<>();
                String reviewDate = DateUtil.now();
                for (Accvoucher accvoucher : voucherList) {
                    keys.add(accvoucher.getUniqueCode());
                }
                Flux<Accvoucher> result = null;
                if (scopeCondition.equals("1")) {
                    result = accvoucherRepository.changeVoucherStatusByYaerAndUniqueCodes(voucherList.get(0).getIyear(), keys, reviewMan, reviewDate, scopeCondition);
                } else {
                    result = accvoucherRepository.changeVoucherStatusByYaerAndUniqueCodes2(voucherList.get(0).getIyear(), keys, reviewMan, reviewDate, scopeCondition);
                }
                return result.collectList().flatMap(ressults -> taskRepository.deleteByIyearAndFunctionModule(DateUtil.thisYear() + "", taskName + "凭证").thenReturn(R.ok("共完成" + keys.size() + "张凭证" + taskName + "！")));
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Mono.just(R.error()).flatMap(o -> taskRepository.deleteByIyearAndFunctionModule(DateUtil.thisYear() + "", taskName + "凭证").thenReturn(o));
    }

    @PostMapping("resetStatusPingZheng")
    @Transactional
    public Mono<R> resetStatusPingZheng(@RequestBody Map map) {
        String scopeCondition = map.get("setValue").toString();
        String taskName = (scopeCondition.equals("1") ? "取消作废" : "取消标错");
        try {
            String reviewMan = map.get("operatorUserName").toString();
            List<String> keysList = (List<String>) map.get("selectedRowKeys");
            Mono<List<Accvoucher>> queryList = accvoucherRepository.findAllByUniqueCodesClose(keysList, scopeCondition).collectList();
            return queryList.flatMap(list -> {
                Task task = new Task().setCaozuoUnique(reviewMan).setFunctionModule(taskName + "凭证").setTime(DateUtil.now()).setState("1").setIyear(DateUtil.thisYear() + "").setMethod(taskName);
                return taskRepository.save(task).map(o -> list);
            }).flatMap(list -> {
                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("voucherList", list);
                return Mono.just(resultMap);
            }).flatMap(resultMap -> {
                List<Accvoucher> voucherList = (List<Accvoucher>) resultMap.get("voucherList");
                if (voucherList.size() == 0) {
                    return taskRepository.deleteByIyearAndFunctionModule(DateUtil.thisYear() + "", "凭证记账").thenReturn(R.ok("共完成0张凭证" + taskName + "！"));
                }
                String ERROR_INFO = "";
                for (Accvoucher accvoucher : voucherList) {
                    if (StrUtil.isNotBlank(accvoucher.getCcashier())) {
                        ERROR_INFO = "不能" + taskName + "凭证!您选择的凭证号制单日期：【" + accvoucher.getDbillDate() + "】凭证号:【" + accvoucher.getInoId() + "】已经出纳签字，请先对该凭证进行取消出纳签字，然后再进行" + taskName + "凭证操作。";
                        break;
                    }
                    if (StrUtil.isNotBlank(accvoucher.getCcheck())) {
                        ERROR_INFO = "不能" + taskName + "凭证!您选择的凭证号制单日期：【" + accvoucher.getDbillDate() + "】凭证号:【" + accvoucher.getInoId() + "】已经进行审核，请先对该凭证进行弃审，然后再进行" + taskName + "凭证操作。";
                        break;
                    }
                    if (StrUtil.isNotBlank(accvoucher.getCdirector())) {
                        ERROR_INFO = "不能" + taskName + "凭证!您选择的凭证号制单日期：【" + accvoucher.getDbillDate() + "】凭证号:【" + accvoucher.getInoId() + "】已经进行主管签字，请先对该凭证进行弃审，然后再进行" + taskName + "凭证操作。";
                        break;
                    }
                    if (StrUtil.isNotBlank(accvoucher.getCbook())) {
                        ERROR_INFO = "不能" + taskName + "凭证!您选择的凭证号制单日期：【" + accvoucher.getDbillDate() + "】凭证号:【" + accvoucher.getInoId() + "】已经进行记账，请先对该凭证进行弃审，然后再进行" + taskName + "凭证操作。";
                        break;
                    }
                    if ((scopeCondition.equals("1") && Objects.equals(accvoucher.getIfrag(), "3")) || (scopeCondition.equals("3") && Objects.equals(accvoucher.getIfrag(), "1"))) {
                        ERROR_INFO = "不能" + taskName + "凭证!您选择的凭证号制单日期：【" + accvoucher.getDbillDate() + "】凭证号:【" + accvoucher.getInoId() + "】为" + (accvoucher.getIfrag().equals("1") ? "作废" : "标错") + "凭证，请先对该凭证进行取消" + (accvoucher.getIfrag().equals("1") ? "作废" : "标错") + "操作，然后再进行" + taskName + "凭证操作。";
                        break;
                    }
                }
                if (StrUtil.isNotBlank(ERROR_INFO))
                    return taskRepository.deleteByIyearAndFunctionModule(DateUtil.thisYear() + "", taskName + "凭证").thenReturn(R.ok(ERROR_INFO));
                // 开始审核
                Set<String> keys = new HashSet<>();
                for (Accvoucher accvoucher : voucherList) {
                    keys.add(accvoucher.getUniqueCode());
                }
                return accvoucherRepository.resetVoucherStatusByYearAndUniqueCodes(voucherList.get(0).getIyear(), keys, scopeCondition).collectList().flatMap(ressults -> taskRepository.deleteByIyearAndFunctionModule(DateUtil.thisYear() + "", taskName + "凭证").thenReturn(R.ok("共完成" + keys.size() + "张凭证" + taskName + "！")));
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Mono.just(R.error()).flatMap(o -> taskRepository.deleteByIyearAndFunctionModule(DateUtil.thisYear() + "", taskName + "凭证").thenReturn(o));
    }


    @PostMapping("cashierPingZheng")
    @Transactional
    public Mono<R> cashierPingZheng(@RequestBody Map map) {
        try {
            String scopeCondition = map.get("scopeCondition").toString();
            String taskName = scopeCondition.equals("1") ? "出纳签字" : "出纳取签";
            String reviewMan = map.get("operatorUserName").toString();
            VoucherBusCheckVo reusltVo = new VoucherBusCheckVo();
            List<String> keys = (List<String>) map.get("selectedRowKeys");
            List<String> interval = ListUtil.toList(map.get("thisInterval").toString().split(" ~ "));
            Map<String, Boolean> financialParameters = (Map<String, Boolean>) map.get("financialParameters");
            Boolean selectMakerNo = financialParameters.get("icashierNo"); // 取消需为同一人
            reusltVo.setSelectNumber(keys.size());
            Mono<List<Accvoucher>> queryList = accvoucherRepository.findAllByUniqueCodes(keys).collectList();
            return queryList.flatMap(list -> {
                Task task = new Task().setCaozuoUnique(reviewMan).setFunctionModule("凭证" + taskName).setTime(DateUtil.now()).setState("1").setIyear(DateUtil.thisYear() + "").setMethod(taskName);
                return taskRepository.save(task).map(o -> list);
            }).flatMap(list -> {
                // 获取出纳科目集合
                Mono<List<CodeKemu>> cashiersMono = codeKemuRepository.findAllByYearAndCashierConditions(interval.get(0).substring(0, 4)).collectList();
                // 获取全部末级科目
                Mono<List<CodeKemu>> lastsMono = codeKemuRepository.findAllByBendAndIyearOrderByCcode("1", interval.get(0).substring(0, 4)).collectList();
                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("voucherList", list);
                if (list.size() == 0) return lastsMono.flatMap(lastList -> {
                    resultMap.put("lastCodeList", lastList);
                    return Mono.just(resultMap);
                });
                return Mono.zip(cashiersMono, lastsMono).flatMap(codeList -> {
                    resultMap.put("cashierCodeList", codeList.getT1());
                    resultMap.put("lastCodeList", codeList.getT2());
                    return Mono.just(resultMap);
                });
            }).flatMap(resultMap -> {
                List<Accvoucher> voucherList = (List<Accvoucher>) resultMap.get("voucherList");
                // 获取指定年份所有为现金、银行与等物的科目
                List<CodeKemu> cashierCodeList = (List<CodeKemu>) resultMap.get("cashierCodeList");
                List<CodeKemu> lastCodeList = (List<CodeKemu>) resultMap.get("lastCodeList");
                List<Map<String, String>> errorList = new ArrayList<>();
                List<Accvoucher> passList = new ArrayList<>();
                String reviewDate = DateUtil.today();
                String thisCnInoid = "";
                for (Accvoucher accvoucher : voucherList) {
                    List<CodeKemu> kemus = lastCodeList.stream().filter(item -> item.getCcode().equals(accvoucher.getCcode())).collect(Collectors.toList());
                    if (cashierCodeList.size() > 0) {
                        if (StrUtil.isBlank(thisCnInoid) || !thisCnInoid.equals(accvoucher.getInoId())) {
                            thisCnInoid = checkVoucherIsCashierVoucher(voucherList, cashierCodeList, accvoucher);
                        }
                    }
                    if (StrUtil.isBlank(thisCnInoid)) { // 不是出纳凭证
                        errorList.add(CollectOfUtils.mapof("dbillDate", accvoucher.getDbillDate(), "inoId", accvoucher.getInoId(), "errorType", "6"));
                    } else if (scopeCondition.equals("0") && selectMakerNo && null != accvoucher.getCcashier() && !accvoucher.getCcashier().equals(reviewMan)) { // 取消需为同一人
                        errorList.add(CollectOfUtils.mapof("dbillDate", accvoucher.getDbillDate(), "inoId", accvoucher.getInoId(), "errorType", "1"));
                    } /*else if (scopeCondition.equals("1") && accvoucher.getIfrag().equals("1")) {
                                errorList.add(CollectOfUtils.mapof("dbillDate", accvoucher.getDbillDate(), "inoId", accvoucher.getInoId(), "errorType", "2"));
                            }*/ else if (scopeCondition.equals("1") && DateUtil.compare(DateUtil.parse(accvoucher.getDbillDate()), DateUtil.parse(reviewDate)) > 0) {
                        errorList.add(CollectOfUtils.mapof("dbillDate", accvoucher.getDbillDate(), "inoId", accvoucher.getInoId(), "errorType", "3"));
                    } else if (scopeCondition.equals("1") && accvoucher.getIfrag().equals("3")) {
                        errorList.add(CollectOfUtils.mapof("dbillDate", accvoucher.getDbillDate(), "inoId", accvoucher.getInoId(), "errorType", "4"));
                    } else if (scopeCondition.equals("1") && accvoucher.getIfrag().equals("2")) {
                        errorList.add(CollectOfUtils.mapof("dbillDate", accvoucher.getDbillDate(), "inoId", accvoucher.getInoId(), "errorType", "5"));
                    } else if (scopeCondition.equals("1") && kemus.size() == 0) {
                        errorList.add(CollectOfUtils.mapof("dbillDate", accvoucher.getDbillDate(), "inoId", accvoucher.getInoId(), "errorType", "8"));
                    } else if (scopeCondition.equals("1") && ((kemus.get(0).getBdept().equals("1") && StrUtil.isBlank(accvoucher.getCdeptId())) || (kemus.get(0).getBperson().equals("1") && StrUtil.isBlank(accvoucher.getCpersonId())) || (kemus.get(0).getBcus().equals("1") && StrUtil.isBlank(accvoucher.getCcusId())) || (kemus.get(0).getBsup().equals("1") && StrUtil.isBlank(accvoucher.getCsupId()) || (kemus.get(0).getBitem().equals("1") && StrUtil.isBlank(accvoucher.getCpersonId()))))) {
                        errorList.add(CollectOfUtils.mapof("dbillDate", accvoucher.getDbillDate(), "inoId", accvoucher.getInoId(), "errorType", "9"));
                    } else if ((scopeCondition.equals("1") && StrUtil.isNotBlank(accvoucher.getCcashier())) || (scopeCondition.equals("0") && StrUtil.isBlank(accvoucher.getCcashier()))) {
                        errorList.add(CollectOfUtils.mapof("dbillDate", accvoucher.getDbillDate(), "inoId", accvoucher.getInoId(), "errorType", scopeCondition.equals("1") ? "10" : "11"));
                    } else { // 校验
                        passList.add(accvoucher);
                    }
                    if (StrUtil.isNotBlank(thisCnInoid)) thisCnInoid = accvoucher.getInoId();
                }
                // 判断借贷平衡
                Set<String> keysets = new HashSet<>();
                for (String thisInfo : new HashSet<>(passList.stream().map(item -> item.getDbillDate().substring(0, 7) + "==" + item.getInoId()).collect(Collectors.toList()))) {
                    double mdSum = 0;
                    double mcSum = 0;
                    Accvoucher thisVoucher = null;
                    String yearMonth = thisInfo.split("==")[0];
                    String thisInoid = thisInfo.split("==")[1];
                    for (Accvoucher accvoucher : passList) {
                        if (ObjectUtil.equal(accvoucher.getDbillDate().substring(0, 7), yearMonth) && ObjectUtil.equal(accvoucher.getInoId(), thisInoid)) {
                            mdSum += StrUtil.isBlank(accvoucher.getMd()) ? 0 : new BigDecimal(accvoucher.getMd()).doubleValue();
                            mcSum += StrUtil.isBlank(accvoucher.getMc()) ? 0 : new BigDecimal(accvoucher.getMc()).doubleValue();
                            if (null == thisVoucher) thisVoucher = accvoucher;
                        }
                    }
                    if (null != thisVoucher && thisInoid.equals(thisVoucher.getInoId())) {
                        if (!NumberUtil.equals(new BigDecimal(mdSum).setScale(2, BigDecimal.ROUND_HALF_UP), new BigDecimal(mcSum).setScale(2, BigDecimal.ROUND_HALF_UP))) {
                            errorList.add(CollectOfUtils.mapof("dbillDate", thisVoucher.getDbillDate(), "inoId", thisInoid, "errorType", "7"));
                        } else {
                            keysets.add(thisVoucher.getUniqueCode());
                        }
                    }
                }
                if (errorList.size() > 0) errorList = removeDuplicateEntries(errorList);
                reusltVo.setSuccessNumber(keysets.size());
                reusltVo.setErrorNumber(errorList.size() > 0 ? new HashSet<>(errorList.stream().map(item -> item.get("dbillDate") + "==" + item.get("inoId")).collect(Collectors.toList())).size() : 0);
                reusltVo.setErrorList(errorList);
                if (keysets.size() == 0)
                    return (scopeCondition.equals("1") ? interval.get(0).length() > 7 ? accvoucherRepository.countByCcashierByDate(interval.get(0), interval.get(1)) : accvoucherRepository.countByCcashierByPeriod(interval.get(0).replace("-", ""), interval.get(1).replace("-", "")) : Mono.just(0)).flatMap(num -> {
                        reusltVo.setSuccessNumber(0);
                        reusltVo.setPassNumber(num);
                        return taskRepository.deleteByIyearAndFunctionModule(DateUtil.thisYear() + "", "凭证" + taskName).thenReturn(R.ok(reusltVo));
                    });
                return (scopeCondition.equals("1") ? accvoucherRepository.cashierVoucherByYaerAndUniqueCodes(passList.get(0).getIyear(), keysets, reviewMan, reviewDate) : accvoucherRepository.closeCashierVoucherByYaerAndUniqueCodes(passList.get(0).getIyear(), keysets)).collectList().flatMap(ressults -> {
                    reusltVo.setSuccessNumber(keysets.size());
                    return scopeCondition.equals("1") ? interval.get(0).length() > 7 ? accvoucherRepository.countByCcashierByDate(interval.get(0), interval.get(1)) : accvoucherRepository.countByCcashierByPeriod(interval.get(0).replace("-", ""), interval.get(1).replace("-", "")) : Mono.just(0);
                }).flatMap(total -> {
                            reusltVo.setPassNumber(total);
                            return taskRepository.deleteByIyearAndFunctionModule(DateUtil.thisYear() + "", "凭证" + taskName).thenReturn(R.ok(reusltVo));
                        }

                );
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Mono.just(R.error()).flatMap(o -> taskRepository.deleteByIyearAndFunctionModule(DateUtil.thisYear() + "", "审核凭证").thenReturn(o));
    }

    /**
     * 查询指定凭证是否为出纳凭证
     */
    private String checkVoucherIsCashierVoucher(List<Accvoucher> voucherList, List<CodeKemu> cashierCodeList, Accvoucher thisVoucher) {
        String resutl = "";
        Set<String> voucherCodeList = new HashSet<>(voucherList.stream().filter(item -> item.getDbillDate().substring(0, 7).equals(thisVoucher.getDbillDate().substring(0, 7)) && item.getInoId().equals(thisVoucher.getInoId())).map(item -> item.getCcode()).collect(Collectors.toList()));
        for (CodeKemu codeKemu : cashierCodeList) {
            if (voucherCodeList.contains(codeKemu.getCcode())) {
                resutl = thisVoucher.getInoId();
                break;
            }
        }
        return resutl;
    }


    @PostMapping("cashierPingZhengOld")
    @Transactional
    public Mono<R> cashierPingZhengOld(@RequestBody Map map) {
        try {
            String scopeCondition = map.get("scopeCondition").toString();
            Boolean selectMaker = Boolean.valueOf(map.get("selectMaker").toString());
            Map<String, String> batchCondition = (HashMap<String, String>) map.get("batchCondition");
            Mono<List<Accvoucher>> queryList = null;
            String reviewMan = map.get("operatorUserName").toString();
            ;
            if (scopeCondition.equals("1")) {
                List<String> keys = (List<String>) map.get("selectedRowKeys");
                queryList = accvoucherRepository.findAllByUniqueCodes(keys).collectList();
            } else {
                if (StrUtil.isNotBlank(batchCondition.get("voucherPeriod"))) {
                    queryList = accvoucherRepository.findAllByPeriodAndConditionCashier(batchCondition.get("voucherPeriod").replaceAll("-", "")).collectList();
                } else {
                    queryList = accvoucherRepository.findAllByIntervalAndConditionCashier(batchCondition.get("voucherDateStart"), batchCondition.get("voucherDateEnd")).collectList();
                }
            }
            return queryList.flatMap(list -> {
                Task task = new Task().setCaozuoUnique(reviewMan).setFunctionModule("出纳签字").setTime(DateUtil.now()).setState("1").setIyear(DateUtil.thisYear() + "").setMethod("签字");
                return taskRepository.save(task).map(o -> list);
            }).map(list -> voucherSignFilter(list, batchCondition, selectMaker, reviewMan)).flatMap(list -> {
                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("voucherList", list);
                if (list.size() == 0) return Mono.just(resultMap);
                return codeKemuRepository.findAllByYearAndCashierConditions(list.get(0).getIyear()).collectList().map(codeList -> {
                    resultMap.put("codeList", codeList);
                    return resultMap;
                });
            }).flatMap(resultMap -> {
                Map<String, Object> a = resultMap;
                String ERROR_INFO = "";
                List<Accvoucher> voucherList = (List<Accvoucher>) resultMap.get("voucherList");
                List<CodeKemu> codeList = (List<CodeKemu>) resultMap.get("codeList");
                if (voucherList.size() == 0) {
                    return taskRepository.deleteByIyearAndFunctionModule(DateUtil.thisYear() + "", "出纳签字").thenReturn(R.ok("共完成0张凭证出纳签字！"));
                }
                for (Accvoucher accvoucher : voucherList) {
                    if (accvoucher.getIbook().equals("1")) {
                        ERROR_INFO = "您选择的待出纳签字的凭证号制单日期：【" + accvoucher.getDbillDate() + "】凭证号:【" + accvoucher.getInoId() + "】已经进行凭证记账，不能进行出纳签字！请先对该凭证进行取消凭证记账，然后再进行凭证出纳签字操作。";
                        break;
                    }
                    if (StrUtil.isNotBlank(accvoucher.getCdirector())) {
                        ERROR_INFO = "您选择的待出纳签字的凭证号制单日期：【" + accvoucher.getDbillDate() + "】凭证号:【" + accvoucher.getInoId() + "】已经进行主管签字，不能进行出纳签字！请先对该凭证进行取消主管签字，然后再进行凭证出纳签字操作。";
                        break;
                    }
                }
                if (StrUtil.isNotBlank(ERROR_INFO))
                    return taskRepository.deleteByIyearAndFunctionModule(DateUtil.thisYear() + "", "出纳签字").thenReturn(R.ok(ERROR_INFO));
                // 开始审核
                Set<String> keys = new HashSet<>();
                String reviewDate = DateUtil.today();
                for (Accvoucher accvoucher : voucherList) {
                    for (CodeKemu codeKemu : codeList) {
                        if (accvoucher.getCcode().equals(codeKemu.getCcode())) {
                            keys.add(accvoucher.getUniqueCode());
                            break;
                        }
                    }
                }
                return accvoucherRepository.cashierVoucherByYaerAndUniqueCodes(voucherList.get(0).getIyear(), keys, reviewMan, reviewDate).collectList().flatMap(ressults -> taskRepository.deleteByIyearAndFunctionModule(DateUtil.thisYear() + "", "出纳签字").thenReturn(R.ok("共完成" + keys.size() + "张凭证出纳签字！")));
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Mono.just(R.error()).flatMap(o -> taskRepository.deleteByIyearAndFunctionModule(DateUtil.thisYear() + "", "主管签字").thenReturn(o));
    }

    @PostMapping("closeCashierPingZheng")
    @Transactional
    public Mono<R> closeCashierPingZheng(@RequestBody Map map) {
        try {
            String scopeCondition = map.get("scopeCondition").toString();
            Boolean selectMaker = Boolean.valueOf(map.get("selectMaker").toString());
            Map<String, String> batchCondition = (HashMap<String, String>) map.get("batchCondition");
            Mono<List<Accvoucher>> queryList = null;
            String reviewMan = map.get("operatorUserName").toString();
            ;
            if (scopeCondition.equals("1")) {
                List<String> keys = (List<String>) map.get("selectedRowKeys");
                queryList = accvoucherRepository.findAllByUniqueCodes(keys).collectList();
            } else {
                if (StrUtil.isNotBlank(batchCondition.get("voucherPeriod"))) {
                    queryList = accvoucherRepository.findAllByPeriodAndConditionCashierClose(batchCondition.get("voucherPeriod").replaceAll("-", "")).collectList();
                } else {
                    queryList = accvoucherRepository.findAllByIntervalAndConditionCashierClose(batchCondition.get("voucherDateStart"), batchCondition.get("voucherDateEnd")).collectList();
                }
            }
            return queryList.flatMap(list -> {
                Task task = new Task().setCaozuoUnique(reviewMan).setFunctionModule("取消出纳签字").setTime(DateUtil.now()).setState("1").setIyear(DateUtil.thisYear() + "").setMethod("签字");
                return taskRepository.save(task).map(o -> list);
            }).map(list -> voucherSignFilter(list, batchCondition, selectMaker, reviewMan)).flatMap(list -> {
                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("voucherList", list);
                return Mono.just(resultMap);
                       /* if (list.size() == 0 || !selectCashier)return Mono.just(resultMap);
                        return codeKemuRepository.findAllByYearAndCashierConditions(list.get(0).getIyear()).collectList()
                                .map(codeList->{
                                    resultMap.put("codeList",codeList);
                                    return resultMap;
                                });*/
            }).flatMap(resultMap -> {
                Map<String, Object> a = resultMap;
                String ERROR_INFO = "";
                List<Accvoucher> voucherList = (List<Accvoucher>) resultMap.get("voucherList");
                if (voucherList.size() == 0) {
                    return taskRepository.deleteByIyearAndFunctionModule(DateUtil.thisYear() + "", "取消出纳签字").thenReturn(R.ok("共完成0张凭证取消出纳签字！"));
                }
                // 获取指定年份所有为现金、银行与等物的科目
                // 开始审核
                Set<String> keys = new HashSet<>();
                //String reviewDate = DateUtil.today();
                for (Accvoucher accvoucher : voucherList) {
                    keys.add(accvoucher.getUniqueCode());
                }
                       /*return Mono.just(new ArrayList<>()).flatMap(ressults->
                               taskRepository.deleteByIyearAndFunctionModule(DateUtil.thisYear() + "", "主管签字")
                                       .thenReturn(R.ok("共完成"+keys.size()+"张凭证签字！")));*/
                return accvoucherRepository.closeCashierVoucherByYaerAndUniqueCodes(voucherList.get(0).getIyear(), keys).collectList().flatMap(ressults -> taskRepository.deleteByIyearAndFunctionModule(DateUtil.thisYear() + "", "取消出纳签字").thenReturn(R.ok("共完成" + keys.size() + "张凭证取消出纳签字！")));
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Mono.just(R.error()).flatMap(o -> taskRepository.deleteByIyearAndFunctionModule(DateUtil.thisYear() + "", "取消记账").thenReturn(o));
    }

    @PostMapping("changeSettlementInfo")
    @Transactional
    public Mono<R> changeSettlementInfo(@RequestBody Accvoucher accvoucher) {
        return accvoucherRepository.findAllByVoucherByVouchUnCode(accvoucher.getIyear(), accvoucher.getUniqueCode(), accvoucher.getVouchUnCode()).collectList().flatMap(list -> {
            for (Accvoucher dbEntity : list) {
                dbEntity.setPjCsettle(accvoucher.getPjCsettle());
                dbEntity.setPjId(accvoucher.getPjId());
                dbEntity.setPjDate(accvoucher.getPjDate());
                dbEntity.setPjUnitName(accvoucher.getPjUnitName());
            }
            return accvoucherRepository.saveAll(list).collectList().map(R::ok);
        });
    }

    @PostMapping("changeXjllInfo")
    @Transactional
    public Mono<R> changeXjllInfo(@RequestBody Accvoucher accvoucher) {
        return accvoucherRepository.findAllByVoucherByVouchUnCodeXj(accvoucher.getIyear(), accvoucher.getUniqueCode(), accvoucher.getVouchUnCode()).collectList().flatMap(list -> {
            for (Accvoucher dbEntity : list) {
                dbEntity.setCashProject(accvoucher.getCashProject());
            }
            return accvoucherRepository.saveAll(list).collectList().map(R::ok);
        });
    }

    private List<Accvoucher> voucherReviewFilter(List<Accvoucher> list, Map<String, String> condition, Boolean selectMaker, String operationName) {
        String startV = condition.get("voucherNumberStart").trim();
        String endV = condition.get("voucherNumberEnd").trim();
        String maker = condition.get("maker").trim();
        String cashier = condition.get("cashier").trim();
        String checkName = condition.get("checkName").trim();
        return list.stream().filter(item -> {
            if (!selectMaker && item.getCbill().equals(operationName)) return false; // 过滤制单为本人的
            if (StrUtil.isNotBlank(startV) && StrUtil.isNotBlank(endV)) {             // 过滤凭证编码区间
                if (item.getInoId().compareTo(startV) < 0 || item.getInoId().compareTo(endV) > 0) return false;
            }
            if ((StrUtil.isNotBlank(maker) && !item.getCcheck().equals(maker))) return false;
            if ((StrUtil.isNotBlank(cashier) && !item.getCcashier().equals(cashier))) return false;
            if ((StrUtil.isNotBlank(checkName) && !item.getCcheck().equals(checkName))) return false;
            return true;
        }).collect(Collectors.toList());
    }

    private List<Accvoucher> voucherSignFilter(List<Accvoucher> list, Map<String, String> condition, Boolean selectMaker, String operationName) {
        String startV = condition.get("voucherNumberStart").trim();
        String endV = condition.get("voucherNumberEnd").trim();
        String maker = condition.get("maker").trim();
        String cashier = condition.get("cashier").trim();
        String checkName = condition.get("checkName").trim();
        return list.stream().filter(item -> {
            if (StrUtil.isNotBlank(startV) && StrUtil.isNotBlank(endV)) {             // 过滤凭证编码区间
                if (item.getInoId().compareTo(startV) < 0 || item.getInoId().compareTo(endV) > 0) return false;
            }
            if ((StrUtil.isNotBlank(maker) && !item.getCcheck().equals(maker))) return false;
            if ((StrUtil.isNotBlank(cashier) && !item.getCcashier().equals(cashier))) return false;
            if ((StrUtil.isNotBlank(checkName) && !item.getCcheck().equals(checkName))) return false;
            return true;
        }).collect(Collectors.toList());
    }

    private List<Accvoucher> printFilter(List<Accvoucher> list, Map variableMap) {
        if (list.size() > 0) {
            String startV = variableMap.containsKey("voucherNumberStart") ? variableMap.get("voucherNumberStart").toString() : "";
            String endV = variableMap.containsKey("voucherNumberEnd") ? variableMap.get("voucherNumberEnd").toString() : "";
            String type = variableMap.containsKey("voucherType") ? variableMap.get("voucherType").toString() : "";
            String reviewStatus = variableMap.containsKey("reviewStatus") ? variableMap.get("reviewStatus").toString() : "";
            list = list.stream().filter(item -> {
                if (StrUtil.isNotBlank(startV) && StrUtil.isNotBlank(endV)) {
                    if ((Integer.valueOf(item.getInoId())).compareTo(Integer.valueOf(startV)) < 0 || (Integer.valueOf(item.getInoId()).compareTo((Integer.valueOf(endV))) > 0))
                        return false;
                }
                if (StrUtil.isNotBlank(type) && !type.equals(item.getCsign())) return false;
                if (StrUtil.isNotBlank(reviewStatus) && (StrUtil.isNotBlank(item.getCcheck()) ? !reviewStatus.equals(item.getCcheck()) : (reviewStatus.equals("1") || reviewStatus.equals("2"))))
                    return false;
                return true;
            }).collect(Collectors.toList());
        }
        return list;
    }

    private List<Accvoucher> queryFilter(List<Accvoucher> list, Map map) {
        if (list.size() > 0) {
            String queryMark = map.get("queryMark").toString();
            String queryType = map.get("biZhong").toString();
            Map<String, Object> variableMap = ((HashMap<String, HashMap<String, Object>>) map.get("condition")).get("variable");
            HashMap<String, Object> authorityMap = ((HashMap<String, HashMap<String, Object>>) map.get("condition")).get("authority");
            Map<String, String> searchMap = ((HashMap<String, String>) map.get("searchConditon"));
            Map<String, String> filterMap = ((HashMap<String, String>) map.get("filterConditon"));
            Map<String, Object> treeCondition = ((Map<String, Object>) map.get("treeConditon"));
            String startV = variableMap.containsKey("voucherNumberStart") ? variableMap.get("voucherNumberStart").toString() : "";
            String endV = variableMap.containsKey("voucherNumberEnd") ? variableMap.get("voucherNumberEnd").toString() : "";
            String type = variableMap.containsKey("voucherType") ? variableMap.get("voucherType").toString() : "";
            String summary = variableMap.containsKey("summary") ? variableMap.get("summary").toString() : "";
            String subjectNumber = variableMap.containsKey("subjectNumber") ? variableMap.get("subjectNumber").toString() : "";
            String amountMin = variableMap.containsKey("amountMin") ? variableMap.get("amountMin").toString() : "";
            String amountMax = variableMap.containsKey("amountMax") ? variableMap.get("amountMax").toString() : "";
            String direction = variableMap.containsKey("direction") ? variableMap.get("direction").toString() : "";

            String preparedMan = variableMap.containsKey("preparedMan") ? variableMap.get("preparedMan").toString() : "";
            String checkMan = variableMap.containsKey("checkMan") ? variableMap.get("checkMan").toString() : "";
            String cashierMan = variableMap.containsKey("cashierMan") ? variableMap.get("cashierMan").toString() : "";
            String bookMan = variableMap.containsKey("bookMan") ? variableMap.get("bookMan").toString() : "";
            String reviewMan = variableMap.containsKey("reviewMan") ? variableMap.get("reviewMan").toString() : "";

            String reviewStatus = variableMap.containsKey("reviewStatus") ? variableMap.get("reviewStatus").toString() : "";
            String bookStatus = variableMap.containsKey("bookStatus") ? variableMap.get("bookStatus").toString() : "";
            String ifrag = variableMap.containsKey("ifrag") ? variableMap.get("ifrag").toString() : "";
            Set<String> codes = new HashSet<>((List<String>) authorityMap.get("codes"));
            Set<String> types = new HashSet<>((List<String>) authorityMap.get("types"));

            // 辅助核算 内容
            Map<String, List<String>> assistsMap = variableMap.containsKey("assists") ? (Map<String, List<String>>) variableMap.get("assists") : null;
            // 外币 外币金额
            list = list.stream().filter(item -> {
                if (StrUtil.isNotBlank(startV) && StrUtil.isNotBlank(endV)) {
                    if ((Integer.valueOf(item.getInoId())).compareTo(Integer.valueOf(startV)) < 0 || (Integer.valueOf(item.getInoId()).compareTo((Integer.valueOf(endV))) > 0))
                        return false;
                }
                if (StrUtil.isNotBlank(type) && !type.equals(item.getCsign())) return false;
                if (StrUtil.isNotBlank(summary) && !item.getCdigest().contains(summary)) return false;
                if (StrUtil.isNotBlank(bookStatus) && (StrUtil.isNotBlank(item.getIbook()) ? !bookStatus.equals(item.getIbook()) : (bookStatus.equals("1") || bookStatus.equals("2"))))
                    return false;
                if (StrUtil.isNotBlank(reviewStatus) && (StrUtil.isNotBlank(item.getCcheck()) ? !reviewStatus.equals(item.getCcheck()) : (reviewStatus.equals("1") || reviewStatus.equals("2"))))
                    return false;
                if (StrUtil.isNotBlank(ifrag) && !ifrag.equals(item.getIfrag())) return false;
                if (StrUtil.isNotBlank(subjectNumber) && !subjectNumber.equals(item.getCcode())) return false;

                if (StrUtil.isNotBlank(preparedMan) && !preparedMan.equals(item.getCbill())) return false;
                if (StrUtil.isNotBlank(checkMan) && !checkMan.equals(item.getCcheck())) return false;
                if (StrUtil.isNotBlank(cashierMan) && !cashierMan.equals(item.getCcashier())) return false;
                if (StrUtil.isNotBlank(bookMan) && !bookMan.equals(item.getCbook())) return false;
                if (StrUtil.isNotBlank(reviewMan) && !reviewMan.equals(item.getCdirector())) return false;
                if (StrUtil.isNotBlank(amountMin) && StrUtil.isNotBlank(amountMax)) {
                    BigDecimal md = StrUtil.isBlank(item.getMd()) ? new BigDecimal(0) : new BigDecimal(item.getMd());
                    BigDecimal mc = StrUtil.isBlank(item.getMc()) ? new BigDecimal(0) : new BigDecimal(item.getMc());
                    if (StrUtil.isNotBlank(direction) && direction.equals("jf")) {
                        if (md.compareTo(new BigDecimal(amountMin)) < 0 || md.compareTo(new BigDecimal(amountMax)) > 0)
                            return false;
                    } else if (StrUtil.isNotBlank(direction) && direction.equals("df")) {
                        if (mc.compareTo(new BigDecimal(amountMin)) < 0 || mc.compareTo(new BigDecimal(amountMax)) > 0)
                            return false;
                    } else if (StrUtil.isBlank(direction) && (md != new BigDecimal(0) || mc != new BigDecimal(0))) {
                        if (md != new BigDecimal(0)) {
                            if (md.compareTo(new BigDecimal(amountMin)) < 0 || md.compareTo(new BigDecimal(amountMax)) > 0)
                                return false;
                        } else if (mc != new BigDecimal(0)) {
                            if (mc.compareTo(new BigDecimal(amountMin)) < 0 || mc.compareTo(new BigDecimal(amountMax)) > 0)
                                return false;
                        }
                    }
                }
                if (queryMark.equals("1") && codes.size() > 0 && !(new HashSet<>(codes).contains(item.getCcode())))
                    return false;
                if (queryMark.equals("1") && types.size() > 0 && !(new HashSet<>(types).contains(item.getCsign())))
                    return false;

                // 辅助核算内容过滤
                if (null != assistsMap && assistsMap.keySet().size() > 0) {
                    for (String key : assistsMap.keySet()) {
                        Set<String> vals = new HashSet<>(assistsMap.get(key));
                        if (vals.size() > 0) {
                            String dbValue = getFieldValueByFieldName(key, item);
                            if (StrUtil.isBlank(dbValue) || !vals.contains(dbValue)) return false; // 空值 或 不包含辅助项
                        }
                    }
                }

                // 查询后过滤
                if (StrUtil.isNotBlank(filterMap.get("amountMin")) && StrUtil.isNotBlank(filterMap.get("amountMax"))) {
                    BigDecimal min = new BigDecimal(filterMap.get("amountMin"));
                    BigDecimal max = new BigDecimal(filterMap.get("amountMax"));
                    BigDecimal md = StrUtil.isBlank(item.getMd()) ? new BigDecimal(0) : new BigDecimal(item.getMd());
                    BigDecimal mc = StrUtil.isBlank(item.getMc()) ? new BigDecimal(0) : new BigDecimal(item.getMc());
                    if (md != new BigDecimal(0)) {
                        if (md.compareTo(min) < 0 || md.compareTo(max) > 0) return false;
                    } else if (mc != new BigDecimal(0)) {
                        if (mc.compareTo(min) < 0 || mc.compareTo(max) > 0) return false;
                    }
                }
                if (StrUtil.isNotBlank(filterMap.get("pzNumberMin")) && StrUtil.isNotBlank(filterMap.get("pzNumberMax"))) {
                    String pzNumberMin = Integer.valueOf(filterMap.get("pzNumberMin")) + "";
                    String pzNumberMax = Integer.valueOf(filterMap.get("pzNumberMax")) + "";
                    if (item.getInoId().compareTo(pzNumberMin) < 0 || item.getInoId().compareTo(pzNumberMax) > 0)
                        return false;
                }
                if (StrUtil.isNotBlank(filterMap.get("cashProject")) && StrUtil.isNotBlank(filterMap.get("cashProject"))) {
                    return filterMap.get("cashProject").equals(item.getCashProject());
                }

                // 搜索过滤
                if (StrUtil.isNotBlank(searchMap.get("requirement")) && StrUtil.isNotBlank(searchMap.get("value"))) {
                    String value = searchMap.get("value");
                    if (searchMap.get("requirement").trim().equals("inoId")) {
                        if (!item.getCsign().contains(value) && !item.getInoId().contains(Integer.parseInt(value) + ""))
                            return false;
                    } else {
                        String dbValue = getFieldValueByFieldName(searchMap.get("requirement").trim(), item);
                        if (null != dbValue && !dbValue.contains(value)) return false;
                    }
                }
                // 树形菜单过滤
                if (null != treeCondition && treeCondition.size() > 0) {
                    List<String> fList = (List<String>) treeCondition.get("list");
                    if (treeCondition.get("type").equals("month") && fList.size() > 0) {
                        if (!fList.contains(item.getDbillDate().substring(0, 7))) return false;
                    } else if (treeCondition.get("type").equals("day")) {
                        String monthValue = treeCondition.containsKey("monthVal") ? treeCondition.get("monthVal").toString() : "";
                        if (StrUtil.isNotBlank(monthValue) && !monthValue.equals(item.getDbillDate().substring(5, 7)))
                            return false;
                        if (fList.size() > 0 && !fList.contains(item.getDbillDate() + "-" + item.getInoId()))
                            return false;
                    }
                }
                // 当为 审核时
                if (StrUtil.isNotBlank(queryType)) { // 过滤
                    // 补充必须是出纳凭证
                    if (queryType.equals("cashier") && (/*StrUtil.isNotBlank(item.getCcheck()) || StrUtil.isNotBlank(item.getCdirector()) ||*/  StrUtil.equals(item.getIbook(), "1") || StrUtil.isNotBlank(item.getCbook())))
                        return false;
                    if (queryType.equals("review") && (/*StrUtil.isNotBlank(item.getCdirector()) ||*/ StrUtil.equals(item.getIbook(), "1") || StrUtil.isNotBlank(item.getCbook())))
                        return false;// 已主管与未记账 签字
                    if (queryType.equals("sign") && (/*StrUtil.isBlank(item.getCcheck()) ||*/ StrUtil.equals(item.getIbook(), "1") || StrUtil.isNotBlank(item.getCbook())))
                        return false;// 已审核与未记账
                    if (queryType.equals("book") && (StrUtil.isBlank(item.getCcheck()))) return false;// 已审核
                }
                return true;
            }).collect(Collectors.toList());
        }
        return list;
    }

    private List<Accvoucher> countFilter(List<Accvoucher> list, int maxNumber) {
        return list;
    }

    /**
     * 相同凭证限制条数
     *
     * @param list
     * @param maxNumber
     * @return
     */
    private List<Accvoucher> countFilter2(List<Accvoucher> list, int maxNumber) {
        List<Accvoucher> filterList = new ArrayList<>();
        int count = 0;
        String thisOPZNumber = "";
        for (Accvoucher accvoucher : list) {
            if (!accvoucher.getInoId().equals(thisOPZNumber)) {
                thisOPZNumber = accvoucher.getInoId();
                count = 0;
            } else {
                ++count;
            }
            if (count < (maxNumber)) filterList.add(accvoucher);
        }
        for (Accvoucher accvoucher : filterList) {
            accvoucher.setInoId(Integer.parseInt(accvoucher.getInoId()) + "");
        }
        return filterList;
    }

    public static List<Accvoucher> splitList(List<Accvoucher> list, int pageNo, int pageSize, AtomicReference<Integer> titlesAR) {
        if (CollectionUtils.isEmpty(list)) {
            titlesAR.set(0);
            return new ArrayList<>();
        }
        // 后端排序
        //List<Accvoucher> sortList = ListUtil.sort(list, (o1, o2) -> Integer.valueOf(o1.getInoId()).compareTo(Integer.valueOf(o2.getInoId())));
        int totalCount = list.size();
        titlesAR.set(totalCount);
        pageNo = pageNo - 1;
        int fromIndex = pageNo * pageSize;
        // 分页不能大于总数
        if (fromIndex >= totalCount) {
            return null;
        }
        int toIndex = ((pageNo + 1) * pageSize);
        if (toIndex > totalCount) {
            toIndex = totalCount;
        }
        return list.subList(fromIndex, toIndex);
    }

    /**
     * 整理凭证断号
     *
     * @return
     */
    @PostMapping("/breakNumTidy")
    public Mono<R> breakNumTidy(@RequestBody Map map) {
        String intervalStart = map.get("periodStart").toString().replaceAll("-", "");
        String intervalEnd = map.get("periodEnd").toString().replaceAll("-", "");
        Flux<Accvoucher> aMono = accvoucherRepository.findAllVoucherDetailByIyperiod(intervalStart.trim(), intervalEnd.trim());
        // 凭证单据Map
        Map<String, List<Accvoucher>> uniqueNumMap = new LinkedHashMap<>();
        return aMono.collectList().map(list -> {
            // 初始化凭证单据Map
            list.forEach(item -> uniqueNumMap.put(item.getDbillDate().substring(0, 7) + "==" + item.getInoId(), new ArrayList<>()));
            // 根据唯一码合并为凭证单据
            list.stream().forEach(item -> uniqueNumMap.get(item.getDbillDate().substring(0, 7) + "==" + item.getInoId()).add(item));
            // 重新排序凭证号(inoid)
            AtomicInteger inoidIndex = new AtomicInteger(0);
            AtomicReference<String> thisYearMonth = new AtomicReference<>(list.get(0).getDbillDate().substring(0, 7));
            uniqueNumMap.forEach((k, accvouchers) -> {
                if (thisYearMonth.get().equals(accvouchers.get(0).getDbillDate().substring(0, 7))) {
                    inoidIndex.set(inoidIndex.get() + 1);
                } else {
                    inoidIndex.set(1);
                }
                for (int i = 0; i < accvouchers.size(); i++) {
                    String inoid = inoidIndex.get() + "";
                    accvouchers.get(i).setInoId(inoid);
                }
                thisYearMonth.set(accvouchers.get(0).getDbillDate().substring(0, 7));
            });
            // 更新到数据库
            return uniqueNumMap;
        }).flatMapMany(map1 -> {
            List<Accvoucher> saveList = new ArrayList();
            map1.forEach((k, accvouchers) -> {
                saveList.addAll(accvouchers);
            });
            return accvoucherRepository.saveAll(saveList);
        }).collectList().map(o -> R.ok().setResult(o));
    }

    @PostMapping("/operateInvalid")
    public Mono<R> operateInvalid(@RequestBody Map map) {
        String intervalStart = map.get("periodStart").toString().replaceAll("-", "");
        String intervalEnd = map.get("periodEnd").toString().replaceAll("-", "");
        String type = map.get("type").toString().replaceAll("-", "");
        Flux<Accvoucher> aMono = accvoucherRepository.findAllVoucherInvalidByIyperiod(intervalStart.trim(), intervalEnd.trim());
        return aMono.collectList().flatMap(list -> type.equals("query") ? Mono.just(list.size()) : accvoucherRepository.deleteAll(list).thenReturn(1)).map(o -> R.ok().setResult(o));
    }

    // 生成凭证号
    private String generatePingZhengNum(int no) {
        return String.format("%04d", no);
    }

    private List<Accvoucher> filterUnCashierConditionCode(List<Accvoucher> acclist, List<CodeKemu> filterCodeList, String filterMark) {
        if (filterMark.equals("xj")) {
            filterCodeList = filterCodeList.stream().filter(item -> {
                if (item.getBcash().equals("1") || item.getBbank().equals("1")) return true;
                return false;
            }).collect(Collectors.toList());
        } else if (!filterMark.equals("all")) {
            String finalFilterMark = filterMark;
            filterCodeList = filterCodeList.stream().filter(item -> {
                if (finalFilterMark.equals("bcash") && item.getBcash().equals("1")) return true;
                if (finalFilterMark.equals("bbank") && item.getBbank().equals("1")) return true;
                if (finalFilterMark.equals("equivalence") && item.getBcashEquivalence().equals("1")) return true;
                return false;
            }).collect(Collectors.toList());
        }
        List<Accvoucher> results = new ArrayList<>();
        for (Accvoucher accvoucher : acclist) {
            for (CodeKemu codeKemu : filterCodeList) {
                if (accvoucher.getCcode().equals(codeKemu.getCcode())) results.add(accvoucher);
            }
        }
        return results;
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
     * 序时账-拷贝 凭证列表接口
     *
     * @param map
     * @return
     */
    @PostMapping("/findAllXuShiZhangList")
    public Mono<R> findAllXuShiZhangList(@RequestBody Map map) {
        //条件查询
        if (map.keySet().size() == 2) {
            return Mono.just(R.ok().setResult(CollectOfUtils.mapof("total", 0, "items", new ArrayList<>())));
        }
        String queryMark = map.get("queryMark").toString();
        int page = Integer.parseInt(map.get("page").toString());
        int pageSize = Integer.parseInt(map.get("size").toString());
        Map<String, String> variableMap = ((HashMap<String, HashMap<String, String>>) map.get("condition")).get("variable");
        String intervalStart = variableMap.get("periodStart").replaceAll("-", "");
        String intervalEnd = variableMap.get("periodEnd").replaceAll("-", "");
        ;
        String dateStart = variableMap.get("dateStart");
        String dateEnd = variableMap.get("dateEnd");
        Mono<R> rMono = null;
        AtomicReference<Integer> totalAR = new AtomicReference();
        if (StrUtil.isNotBlank(intervalStart) && StrUtil.isNotBlank(intervalEnd)) {
            rMono = accvoucherRepository.findAllVoucherDetailByIyperiod(intervalStart, intervalEnd).collectList().map(list -> queryFilter(list, map)).map(list -> splitList(countFilter(list, 8), page, pageSize, totalAR)).map(list -> R.ok().setResult(CollectOfUtils.mapof("total", totalAR.get(), "items", list)));
        } else if (StrUtil.isNotBlank(dateStart) && StrUtil.isNotBlank(dateEnd)) {
            rMono = accvoucherRepository.findAllVoucherDetailByDate(dateStart.trim(), dateEnd.trim()).collectList().map(list -> queryFilter(list, map)).map(list -> splitList(countFilter(list, 8), page, pageSize, totalAR)).map(list -> R.ok().setResult(CollectOfUtils.mapof("total", totalAR.get(), "items", list)));
        }
        return rMono;
    }

    @GetMapping("/findByUniqueCodeOrderByInid")
    public Mono<R> findByUniqueCodeOrderByInid(String uniqueCode) {
        return accvoucherRepository.findByUniqueCodeOrderByInid(uniqueCode).collectList().map(o -> R.ok().setResult(o));
    }

    @GetMapping("newDate")
    public Mono<R> findByUniqueCodeOrderByInid() {
        return accvoucherRepository.findFirstByImonthBetweenOrderByDbillDateDesc("01", "13").defaultIfEmpty(new Accvoucher()).map(o -> R.ok().setResult(o.getDbillDate()));
    }

    @PostMapping("/delSpecifyOld")
    public Mono<R> delSpecifyOld(@RequestBody Map map) {
        List<String> keys = (List<String>) map.get("selectedRowKeys");
        return accvoucherRepository.findAllByUniqueCodes(keys).filter(item -> item.getIfrag().equals("2") || item.getIfrag().equals("3")).collectList().flatMap(list -> accvoucherRepository.deleteAll(list).thenReturn(1)).map(o -> R.ok().setResult(o));
    }

    @PostMapping("/delSpecify")
    @Transactional
    public Mono<R> delSpecify(@RequestBody Map map) {
        List<String> keys = (List<String>) map.get("selectedRowKeys");
        String man = map.get("man").toString();
        return accvoucherRepository.findAllByUniqueCodes(keys).collectList().flatMap(list -> {
            List<Accvoucher> s = list.stream().filter(it -> StrUtil.isNotBlank(it.getCcashier()) || StrUtil.isNotBlank(it.getCcheck()) || StrUtil.isNotBlank(it.getCdirector()) || (StrUtil.isNotBlank(it.getIbook()) && it.getIbook().equals("1"))).filter(it -> !it.getIfrag().equals("2") && !it.getIfrag().equals("3")).collect(Collectors.toList());
            if (s.size() > 0) {
                return Mono.just(R.ok().setResult(CollectOfUtils.mapof("error", true, "list", s)));
            } else {
                return accvoucherRepository.deleteAll(list).thenReturn(1).flatMap(v -> {
                    List<AccvoucherDelete> saveDList = list.stream().map(e -> {
                        AccvoucherDelete d = new AccvoucherDelete();
                        BeanUtil.copyProperties(e, d, "id");
                        d.setDelName(man).setDelTime(DateUtil.now());
                        return d;
                    }).collect(Collectors.toList());
                    return accvoucherDeleteRepository.saveAll(saveDList).collectList();
                }).map(o -> R.ok().setResult(CollectOfUtils.mapof("error", false, "list", o)));
            }
        });
//        return accvoucherRepository.findAllByUniqueCodes(keys).filter(item -> item.getIfrag().equals("2") || item.getIfrag().equals("3")).collectList().flatMap(list -> accvoucherRepository.deleteAll(list).thenReturn(1)).map(o -> R.ok().setResult(o));
    }

    @GetMapping("dateTree")
    public Mono<R> dateTree(String yearMonth) {
        return accvoucherRepository.findAllVoucherTreeByDbillDateLike(yearMonth + "%").collectList().map(list -> {
            Map<String, Map<String, Set<String>>> maps = new HashMap<>();
            for (Accvoucher acc : list) {
                if (maps.containsKey(acc.getCsign())) {
                    Map<String, Set<String>> map = maps.get(acc.getCsign());
                    Set<String> elem = map.containsKey(acc.getDbillDate()) ? map.get(acc.getDbillDate()) : new HashSet<>();
                    elem.add(acc.getInoId());
                    map.put(acc.getDbillDate(), elem);
                    maps.put(acc.getCsign(), map);
                } else {
                    Set<String> elem = new HashSet<>();
                    elem.add(acc.getInoId());
                    maps.put(acc.getCsign(), MapUtil.of(acc.getDbillDate(), elem));
                }
            }
            return maps;
        }).map(o -> R.ok().setResult(o));
    }

    /******************** 工作检查业务 ********************/
    @PostMapping("/checkError")
    public Mono<R> checkError(@RequestBody Map map) {
        if (map.keySet().size() < 2) return Mono.just(R.error());
        return accvoucherRepository.findAllByErrorPingZhengList(map.get("startDate").toString(), map.get("endDate").toString()).collectList().map(list -> {
            if (list.size() > 0) {
                return R.ok(list.stream().map(it -> it.getInoId()).collect(Collectors.toList()));
            } else {
                return R.ok();
            }
        });
    }

    @PostMapping("/checkSequenceDate")
    public Mono<R> checkSequenceDate(@RequestBody Map map) {
        if (map.keySet().size() < 2) return Mono.just(R.error());
        return accvoucherRepository.findAllByAllDateAndNumber(map.get("startDate").toString(), map.get("endDate").toString()).collectList().map(list -> {
            if (list.size() > 0) {
                int errorIndex = -1;
                for (int i = 0; i < list.size(); i++) {
                    Accvoucher up = i == 0 ? null : list.get(i - 1);
                    Accvoucher down = list.get(i);
                    if (null == up) continue;
                    if ((DateUtil.compare(DateUtil.parse(up.getDbillDate()), DateUtil.parse(down.getDbillDate())) > 0) || (Integer.parseInt(up.getInoId()) > Integer.parseInt(down.getInoId()))) {
                        errorIndex = i;
                        break;
                    }
                }
                if (errorIndex > -1) {
                    return R.ok(list.get(errorIndex).getDbillDate() + "  " + list.get(errorIndex).getInoId());
                } else {
                    return R.ok();
                }
            } else {
                return R.ok();
            }
        });
    }

    @PostMapping("/checkBreakSign")
    public Mono<R> checkBreakSign(@RequestBody Map map) {
        if (map.keySet().size() < 2) return Mono.just(R.error());
        return accvoucherRepository.findAllByAllDateAndNumber(map.get("startDate").toString(), map.get("endDate").toString()).collectList().map(list -> {
            if (list.size() > 0) {
                int errorIndex = -1;
                for (int i = 1; i <= list.size(); i++) {
                    if (i != Integer.parseInt(list.get(i - 1).getInoId())) {
                        errorIndex = i;
                        break;
                    }
                }
                if (errorIndex > -1) {
                    return R.ok(list.get(errorIndex - 1).getDbillDate() + "  " + list.get(errorIndex - 1).getInoId());
                } else {
                    return R.ok();
                }
            } else {
                return R.ok();
            }
        });
    }


    @PostMapping("/checkNumber")
    public Mono<R> checkNumber(@RequestBody Map map) {
        if (map.keySet().size() < 2) return Mono.just(R.error());
        String start = map.get("type").toString();
        return (start.equals("review") ? accvoucherRepository.findAllByUnReviewPingZhengList(map.get("startDate").toString(), map.get("endDate").toString()).collectList() : accvoucherRepository.findAllByUnIbookPingZhengList(map.get("startDate").toString(), map.get("endDate").toString()).collectList()).map(list -> {
            if (list.size() > 0) {
                return R.ok(new HashSet<>(list.stream().map(it -> it.getInoId()).collect(Collectors.toList())));
            } else {
                return R.ok();
            }
        });
    }

    /**
     * 执行审核或者记账操作
     *
     * @param map
     * @return
     */
    @PostMapping("/execVoucher")
    @Transactional
    public Mono<R> execVoucher(@RequestBody Map map) {
        if (map.keySet().size() < 2) return Mono.just(R.error());
        String start = map.get("type").toString();
        String operator = map.get("operator").toString();
        return (start.equals("review") ? accvoucherRepository.findAllByUnReviewPingZhengList(map.get("startDate").toString(), map.get("endDate").toString()).collectList() : accvoucherRepository.findAllByUnIbookPingZhengList(map.get("startDate").toString(), map.get("endDate").toString()).collectList()).flatMap(list -> {
            if (list.size() > 0) {
                for (Accvoucher accvoucher : list) {
                    if (start.equals("review")) {
                        accvoucher.setCcheck(operator);
                        accvoucher.setCcheckDate(DateUtil.today());
                    } else {
                        accvoucher.setIbook("1");
                        accvoucher.setCbook(operator);
                        accvoucher.setIbookDate(DateUtil.today());
                    }
                }
                return accvoucherRepository.saveAll(list).collectList().flatMap(l -> Mono.just(R.ok(l.size())));
            } else {
                return Mono.just(R.ok());
            }
        });
    }

    /******************** 工作检查业务********************/


    @PostMapping("/finByMonthMaxInoId")
    public Mono<R> finByMonthMaxInoId(String imonth) {
        return accvoucherRepository.finByMonthMaxInoId(imonth).map(a -> R.ok().setResult(a));
    }

    @PostMapping("/findAllByIfrag")
    public Mono<R> findAllByIfrag(@RequestBody Map map) {
        return accvoucherRepository.findAllByIfrag().collectList().map(R::page);
    }
    @PostMapping("/findAllMxByKeys")
    public Mono<R> findAllMxByKeys(@RequestBody Map map) {
        return accvoucherRepository.findAllByUniqueCodes((List<String>) map.get("selectedRowKeys")).collectList().map(R::ok);
    }
    @PostMapping("/setRevision")
    public Mono<R> setRevision(@RequestBody Map<String,String> map) {
        Flux<Accvoucher> accvoucherFlux = accvoucherRepository.findAllByUniqueCode(map.get("uniqueCode")).map(it -> {
            it.setRevision(Integer.parseInt(map.get("revision")));
            return it;
        });
        return accvoucherRepository.saveAll(accvoucherFlux).then().map(R::ok);
    }

    @PostMapping("/findAllVoucherSummary")
    public Mono<R> findAllVoucherSummary(@RequestBody Map map) {
        return accvoucherRepository.findAllByCdigest(map.get("iyear").toString()).collectList().map(o->R.ok(o.stream().map(it->it.getCdigest()).distinct().collect(Collectors.toList())));
    }

    @PostMapping("/save")
    @Transactional
    public Mono<R> voucherSave(@RequestBody Map<String,String> map) {
        if (null == map.get("str") ) return Mono.just(R.error());
        List<Accvoucher> list = JSON.parseArray(map.get("str"), Accvoucher.class);
        String code = IdUtil.objectId();
        return null == list.get(0).getId()?accvoucherRepository.saveAll(list.stream().map(it->{it.setUniqueCode(code);return it;}).collect(Collectors.toList())).collectList().thenReturn(R.ok()):Mono.just(R.ok());
    }

    @PostMapping("findBillByCondition")
    public Mono<R> findBillByCondition(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        String type = map.get("type").toString();
        String iyear = map.get("iyear").toString();
        String action = map.get("action").toString();
        String currPdId = map.containsKey("curr") ? map.get("curr").toString() : "";
        return accvoucherRepository.findAllByIyearOrderByIyperiodAscInoIdAsc(iyear)
                .filter(it->true)
                .collectList().cache()
                .flatMap(list -> {
                    if (list.size() == 0) {
                        return Mono.just(R.ok());
                    } else {
                        Accvoucher master = null;
                        switch (action) {
                            case "curr":
                                master = list.get((list.stream().map(e -> e.getUniqueCode()).distinct().collect(Collectors.toList())).indexOf(Integer.parseInt(currPdId)));
                                break;
                            case "tail":
                                master = list.get(list.size() - 1);
                                break;
                            case "prev":
                            case "next":
                                if (StrUtil.isBlank(currPdId)) {
                                    master = action.equals("prev")?list.get(0):list.get(list.size() - 1);
                                } else {
                                    List<Accvoucher> collect = list.stream().filter(it -> it.getUniqueCode().equals(currPdId)).collect(Collectors.toList());
                                    List<String> inods = new ArrayList<>();
                                    for (Accvoucher accvoucher : list) {
                                        if (inods.contains(accvoucher.getInoId()+"=="+accvoucher.getUniqueCode())) continue;
                                        inods.add(accvoucher.getInoId()+"=="+accvoucher.getUniqueCode());
                                    }
                                    int index = collect.size() ==0 ?0:inods.indexOf(collect.get(0).getInoId()+"=="+collect.get(0).getUniqueCode());
                                    index = action.equals("prev")?(index == 0 ? 0 : index - 1):(index >= list.size() - 1 ? list.size() - 1 : index + 1);
                                    int finalIndex = index;
                                    master = list.stream().filter(it->(it.getInoId()+"=="+it.getUniqueCode()).equals(inods.get(finalIndex))).collect(Collectors.toList()).get(0);
                                }
                                break;
                            default:
                                master = list.get(0);
                                break;
                        }
                        Accvoucher finalMaster = master;
                        return Mono.just(R.ok(list.stream().filter(it->it.getUniqueCode().equals(finalMaster.getUniqueCode())).collect(Collectors.toList())));
                    }
                });
    }


    @PostMapping("findLastPingZhengInoid")
    public Mono<R> findLastPingZhengInoid(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        String[] dates= map.get("date").toString().split("-");
        String  csign= map.get("csign").toString();
        String  broken= map.get("broken").toString();
        String  sort= map.get("sort").toString();
        return  accvoucherRepository.findAllByCsignAndIyperiodLike(csign,("%"+(sort.equals("1")?dates[0]:dates[0]+dates[1])+"%")).collectList().flatMap(sourList->{
            if (sourList.size() == 0)return Mono.just(R.ok(1));
            String id = "";
            List<Integer> list = sourList.stream().map(it -> Integer.parseInt(it.getInoId())).distinct().collect(Collectors.toList());
            int max = list.get(list.size()-1);
            if (broken.equals("1")){
                for (int i = 1; i <= max; i++) {
                    if (!list.contains(i)){
                        id = i+"";
                        break;
                    }
                }
            }else {
                id = (max+1)+"";
            }
            return Mono.just(R.ok(id));
        });
    }

    @PostMapping("checkLastZhengInoid")
    public Mono<R> checkLastZhengInoid(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        String[] dates= map.get("date").toString().split("-");
        String  csign= map.get("csign").toString();
        String  code= map.get("code").toString();
        String  sort= map.get("sort").toString();
        return  accvoucherRepository.findAllByCsignAndIyperiodLike(csign,("%"+(sort.equals("1")?dates[0]:dates[0]+dates[1])+"%")).collectList().flatMap(sourList->{
            if (sourList.size() == 0)return Mono.just(R.ok(1));
            List<Integer> list = sourList.stream().map(it -> Integer.parseInt(it.getInoId())).distinct().collect(Collectors.toList());
            return Mono.just(R.ok(list.contains(code)?"1":"0"));
        });
    }

    @PostMapping("findPingZhengQjLastDate")
    public Mono<R> findPingZhengQjLastDate(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        String[] dates= map.get("date").toString().split("-");
        return  accvoucherRepository.findAllByIyperiodOrderByDesc(dates[0]+dates[1]).defaultIfEmpty("").flatMap(str->{
            if (StrUtil.hasBlank(str))return Mono.just(R.ok( map.get("date").toString()));
            return  Mono.just(R.ok(str));
        });
    }
}

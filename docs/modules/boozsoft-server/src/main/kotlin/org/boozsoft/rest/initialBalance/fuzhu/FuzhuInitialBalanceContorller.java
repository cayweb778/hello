package org.boozsoft.rest.initialBalance.fuzhu;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.boozsoft.domain.entity.Accvoucher;
import org.boozsoft.domain.entity.AccvoucherRollback;
import org.boozsoft.domain.entity.SysLog;
import org.boozsoft.domain.entity.Task;
import org.boozsoft.domain.entity.codekemu.CodeKemu;
import org.boozsoft.domain.vo.SubjectInitialBalanceVo;
import org.boozsoft.repo.*;
import org.boozsoft.repo.accountInfo.AccountInfoRepository;
import org.boozsoft.repo.accstyle.AccStyleRepository;
import org.boozsoft.repo.codekemu.CodeKemuRepository;
//import org.boozsoft.repo.project.Project01Repository;
import org.boozsoft.repo.standard.AccStandardRepository;
import org.boozsoft.service.SubjectInitalBalanceService;
import org.boozsoft.util.BigDecimalUtils;
import org.boozsoft.utils.CollectOfUtils;
import org.springbooz.core.tool.result.R;

import org.springbooz.datasource.r2dbc.annotation.SCHEMA_TYPE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName : FuzhuInitialBalanceContorller
 * @Description : 辅助期初余额表
 * @Author : miao
 * @Date: 2021-05-24 17:06
 */
@Slf4j
@RestController
@RequestMapping("/sys/fuzhuInitialBalance")
@Api(value = "辅助期初余额表", tags = "API系统：辅助期初余额表")
public class FuzhuInitialBalanceContorller {
    @Autowired
    IperiodRepository iperiodRepository;
    @Autowired
    SysLogRepository logRepository;
    @Autowired
    CodeKemuRepository codeKemuRepository;
    @Autowired
    SysPsnRepository psnRepository;
    @Autowired
    SysDepartmentRepository departmentRepository;
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    SupplierRepository supplierRepository;
//    @Autowired
//    Project01Repository project01Repository;
    @Autowired
    AccvoucherRepository accvoucherRepository;


    /**
     * 获取 项目信息表
     *
     * @return
     */
//    @GetMapping("findByItem")
//
//    public Mono<R> findByItem() {
//        return project01Repository.findAllBySuccessStateOrderById("1").collectList().map(o -> R.ok().setResult(o));
//    }

    /**
     * 获取 供应商信息表
     *
     * @return
     */
    @GetMapping("findBySup")
    
    public Mono<R> findBySup() {
        return supplierRepository.findAllByFlag("1").collectList().map(o -> R.ok().setResult(o));
    }

    /**
     * 获取 客户信息表
     *
     * @return
     */
    @GetMapping("findByCus")
    
    public Mono<R> findByCus() {
        return customerRepository.findAllByFlag("1").collectList().map(o -> R.ok().setResult(o));
    }

    /**
     * 获取 部门表
     *
     * @return
     */
    @GetMapping("findByDept")
    
    public Mono<R> findByDept() {
        return departmentRepository.findByFlagAndIsDelOrderByDeptCode("1","0").collectList().map(o -> R.ok().setResult(o));
    }

    /**
     * 获取 人员表
     *
     * @return
     */
    @GetMapping("findByPerson")
    
    public Mono<R> findByPerson() {
        return psnRepository.findAllByFlag("1").collectList().map(o -> R.ok().setResult(o));
    }

    /**
     * 获取辅助项科目
     *
     * @param iyear
     * @return
     */
    @PostMapping("findByFuzhuCode")
    
    public Mono<R> findByFuzhuCode(String iyear) {
        return codeKemuRepository.findByFuzhuCode(iyear).collectList().map(o -> R.ok().setResult(o));
    }

    public Map setColumnsAndData(CodeKemu km) {
        // 设置表头
        List<Map> columns = new ArrayList<>();
        if (km.getBperson() != null && km.getBperson().equals("1")) {
            columns.add(CollectOfUtils.mapof(
                    "title", "个人",
                    "dataIndex", "bperson"
            ));
        }
        if (km.getBdept() != null && km.getBdept().equals("1")) {
            columns.add(CollectOfUtils.mapof(
                    "title", "部门",
                    "dataIndex", "bdept"
            ));
        }
        if (km.getBcus() != null && km.getBcus().equals("1")) {
            columns.add(CollectOfUtils.mapof(
                    "title", "客户",
                    "dataIndex", "bcus"
            ));
        }
        if (km.getBsup() != null && km.getBsup().equals("1")) {
            columns.add(CollectOfUtils.mapof(
                    "title", "供应商",
                    "dataIndex", "bsup"
            ));
        }
        if (km.getBitem() != null && km.getBitem().equals("1")) {
            columns.add(CollectOfUtils.mapof(
                    "title", "项目",
                    "dataIndex", "bitem"
            ));
        }
        columns.add(CollectOfUtils.mapof(
                "title", "借方金额",
                "dataIndex", "md"
        ));
        columns.add(CollectOfUtils.mapof(
                "title", "贷方金额",
                "dataIndex", "mc"
        ));

        Map map = new HashMap();
        map.put("tableColumns", columns);
        map.put("tablesData", "");
        map.put("codeInfo", km);
        return map;
    }

    @PostMapping("findByFuzhuInitalBalance")
    
    public Mono<R> findByFuzhuInitalBalance(String ccode, String iyear) {
        return codeKemuRepository.findByCcodeAndIyear(ccode, iyear)
                .map(item -> setColumnsAndData(item)).map(o -> R.ok().setResult(o));
    }


    @PostMapping("saveFuzhuInitalBalance")
    
    public Mono<R> saveFuzhuInitalBalance(Long id, String iyear, String ccode, String person, String dept, String cus, String sup, String item, String md, String mc) {
        String type = id == null ? "10" : "1";
        String type2 = id == null ? "新增" : "修改";

        Accvoucher accvoucher=  new Accvoucher();
        accvoucher.setCcode(ccode)
                .setIyear(iyear)
                .setIyperiod(iyear + "21")
                .setMd(Double.valueOf(md) == 0 ? "0" : md)
                .setMc(Double.valueOf(mc) == 0 ? "0" : mc)
                .setCdeptId(dept)
                .setCpersonId(person)
                .setCcusId(cus)
                .setCsupId(sup)
                .setProjectId(item);

        return accvoucherRepository.save(accvoucher)
                .flatMap(itemlog -> {
            return logRepository.save(new SysLog().setUniqueCode("test0001").setUserName("test001").setAccId("bjxgkj001").setIyear(iyear).setOperationCont("【test0001在" + new SimpleDateFormat(" yyyy-MM-dd'T'HH:mm:ss").format(new Date()) + "】" + type2 + "一个辅助期初余额。科目编码【" + accvoucher.getCcode() + "】,借方金额【" + accvoucher.getMd() + "】,贷方金额【" + accvoucher.getMc() + "】").setOperationDate(new SimpleDateFormat(" yyyy-MM-dd'T'HH:mm:ss").format(new Date())).setLogMethod(type))
                    .map(o -> R.ok());
        })
                .map(o -> R.ok().setResult(o));
    }
}

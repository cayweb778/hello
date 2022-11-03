package org.boozsoft.rest.initialBalance.subject;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.util.Asserts;
import org.boozsoft.domain.entity.Accvoucher;
import org.boozsoft.domain.entity.AccvoucherRollback;
import org.boozsoft.domain.entity.SysLog;
import org.boozsoft.domain.entity.Task;
import org.boozsoft.domain.entity.accstandard.AccStandard;
import org.boozsoft.domain.ro.SubjectBalanceRo;
import org.boozsoft.domain.vo.SsphVo;
import org.boozsoft.domain.vo.SubjectInitialBalanceVo;
import org.boozsoft.repo.*;
import org.boozsoft.repo.accountInfo.AccountInfoRepository;
import org.boozsoft.repo.accstyle.AccStyleRepository;
import org.boozsoft.repo.codekemu.CodeKemuRepository;
import org.boozsoft.repo.standard.AccStandardRepository;
import org.boozsoft.service.SubjectInitalBalanceService;
import org.boozsoft.util.BigDecimalUtils;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @ClassName : SubjectInitialBalanceContorller
 * @Description : 科目期初余额表
 * @Author : miao
 * @Date: 2021-05-24 17:06
 */
@Slf4j
@RestController
@RequestMapping("/sys/subjectinitialBalance")
@Api(value = "科目期初余额表", tags = "API系统：科目期初余额表")
public class SubjectInitialBalanceContorller {
    @Autowired
    IperiodRepository iperiodRepository;
    @Autowired
    AccountInfoRepository accountInfoRepository;
    @Autowired
    AccvoucherRepository accvoucherRepository;
    @Autowired
    SubjectInitalBalanceService service;
    @Autowired
    TaskRepository taskRepository;
    @Autowired
    SysLogRepository logRepository;
    @Autowired
    AccvoucherRollBackRepository accvoucherRollBackRepository;
    @Autowired
    CodeKemuRepository codeKemuRepository;
    @Autowired
    AccStyleRepository accStyleRepository;
    @Autowired
    SysPeriodRepository sysPeriodRepository;
    @Autowired
    AccStandardRepository accStandardRepository;


    /**
     * 查询期初是否记账
     *
     * @param iyear
     * @return
     */
    @PostMapping("findBySubjectInitalBalabceIbook")
    public Mono<R> findBySubjectInitalBalabceIbook(String iyear, String databasenum) {
        return accvoucherRepository.countByIperiodAndIbook(iyear + "00", "1", databasenum).map(o -> R.ok().setResult(o));
    }

    /**
     * 查询当年凭证是否结过账
     *
     * @param iyear
     * @return
     */
    @PostMapping("findByAccvoucherIbook")

    public Mono<R> findByAccvoucherIbook(String iyear, String databasenum) {
        return accvoucherRepository.countByIyearAndIbook(iyear, "1", databasenum).map(o -> R.ok().setResult(o));
    }

    /**
     * 查询当年是否结过账
     *
     * @param iyear
     * @return
     */
    @PostMapping("findByIperiodFlag")
    public Mono<R> findByIperiodFlag(String iyear, String databasenum) {
        return iperiodRepository.countByIyearAndFlag(iyear, "1", databasenum).map(o -> R.ok().setResult(o));
    }

    /**
     * 获取 任务管理
     *
     * @return
     */
    @PostMapping("findByFunctionModule")

    public Mono<R> findByFunctionModule(String iyear) {
        return taskRepository.findAllByIyear(iyear).collectList().map(o -> R.ok().setResult(o));
    }

    /**
     * 清除 任务管理
     *
     * @return
     */
    @PostMapping("delFunctionModule")

    public Mono delFunctionModule(String id, String databasenum) {
        return taskRepository.deleteById(id).then(Mono.just(R.ok()));
    }

    /**
     * 添加 任务管理
     *
     * @return
     */
    @PostMapping("saveTaskInfo")
    public Mono<R> saveTaskInfo(String id,String iyear, String functionModule, String username, String databasenum, String method) {
        Task task = new Task();
        task.setCaozuoUnique(username).setFunctionModule(functionModule).setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).setState("1").setIyear(iyear).setTenantId(null).setMethod(method);
        return taskRepository.save(task).map(o -> R.ok().setResult(o));
    }

    /**
     * 修改任务时间
     *
     * @return
     */
    @PostMapping("editTaskTime")
    public Mono<R> editTaskTime(String id) {
        String time=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        return taskRepository.editTimeByid(time,id).then(Mono.just(R.ok()));
    }

    /**
     * 标记异常 任务管理
     *
     * @return
     */
    @PostMapping("markAnomaly")
    public Mono<R> markAnomaly(String id, String iyear) {
        return taskRepository.findAllByIyearAndId(iyear, Long.parseLong(id)).flatMap(entity -> {
            entity.setState("0");
            return taskRepository.save(entity).map(o -> R.ok().setResult(o));
        });
    }

    @PostMapping("closeAnomaly")
    public Mono<R> closeAnomaly(String id, String iyear) {
        return taskRepository.findAllByIyearAndId(iyear, Long.parseLong(id)).flatMap(entity ->
                taskRepository.delete(entity).map(o -> R.ok().setResult(o))
        );
    }

    /**
     * 根据账套编码获取年度
     *
     * @return
     */
    @PostMapping("findAllIperiodIyear")
    public Mono<R> findAllIperiodIyear(String database) {
        return sysPeriodRepository.findIyearAndEnablePeriod(database).collectList().map(o -> R.ok().setResult(o));
    }

    /**
     * 获取账套基本信息
     *
     * @return
     */
    @GetMapping("findByAccountInfo")

    public Mono<R> findByAccountInfo() {
        return accountInfoRepository.findAll().collectList().map(o -> R.ok().setResult(o));
    }

    /**
     * 获取科目期初
     *
     * @param iyear
     * @return
     */
    @PostMapping("findAllSubjectInitialBalance")
    public Mono<R> findAllSubjectInitialBalance(String iyear, String lastCode, String databasenum,String ccode ,String cclass,String bend) {
        iyear = iyear.equals("") ? new SimpleDateFormat("yyyy").format(new Date()) : iyear;
        String iyperiod = iyear + "00";
        return service.findAllSubjectInitialBalance(iyear, lastCode, iyperiod, databasenum,ccode,cclass,bend);
    }

    @PostMapping("/findAllSubjectInitialBalance2")
    public Mono<R> findAllSubjectInitialBalanceNewFuZhu(@RequestBody Map map) {
        String iyear = map.get("iyear").toString();;
        String lastCode = map.get("lastCode").toString();
        String databasenum = map.get("databasenum").toString();
        String cclass = map.get("cclass").toString();
        iyear = iyear.equals("") ? new SimpleDateFormat("yyyy").format(new Date()) : iyear;
        String iyperiod = iyear + "00";
        return service.findAllSubjectInitialBalanceNewFuZHu(iyear, lastCode, iyperiod, databasenum,cclass);
    }

    public static final String VIEW_CODE_KEMU = "" +
            "select *,(cast(c.bperson as int)+cast(c.bdept as int)+cast(c.bcus as int)+cast(c.bsup as int)+cast(c.bitem as int)+cast(c.cdfine1 as int)+" +
            "cast(c.cdfine2 as int)+cast(c.cdfine3 as int)+cast(c.cdfine4 as int)+cast(c.cdfine5 as int)+cast(c.cdfine6 as int)+cast(c.cdfine7 as int)+" +
            "cast(c.cdfine8 as int)+cast(c.cdfine9 as int)+cast(c.cdfine10 as int)+cast(c.cdfine11 as int)+cast(c.cdfine12 as int)+cast(c.cdfine13 as int)+" +
            "cast(c.cdfine14 as int)+cast(c.cdfine15 as int)+cast(c.cdfine16 as int)+cast(c.cdfine17 as int)+cast(c.cdfine18 as int)+cast(c.cdfine19 as int)+" +
            "cast(c.cdfine20 as int)+cast(c.cdfine21 as int)+cast(c.cdfine22 as int)+cast(c.cdfine23 as int)+cast(c.cdfine24 as int)+cast(c.cdfine25 as int)+" +
            "cast(c.cdfine26 as int)+cast(c.cdfine27 as int)+cast(c.cdfine28 as int)+cast(c.cdfine29 as int)+cast(c.cdfine30 as int)) as fzCount " +
            " from (select c.id,c.tenant_id,c.iyear,c.ccode,c.ccode_name,c.igrade,c.bprogerty,c.cass_item," +
            "case when c.bperson='1' then c.bperson else '0' end  as bperson," +
            "case when c.bdept='1' then c.bdept else '0' end as bdept," +
            "case when c.bcus='1' then c.bcus else '0' end as bcus," +
            "case when c.bsup='1' then c.bsup else '0' end as bsup," +
            "case when c.bitem='1' then c.bitem else '0' end as bitem," +
            "case when c.bnum='1' then c.bnum else '0' end as bnum," +
            "c.menterage," +
            "case when c.currency='1' then c.currency else '0' end as currency," +
            "c.currency_type,c.bcash,c.bbank,c.bend,c.bdaybook,c.flag,c.template_id,c.lower_control,c.fuzhu_control," +
            "c.superior_ccode,c.pxjz,c.xjll,c.cyfx," +
            "case when c.cdfine1='1' then c.cdfine1 else '0' end as cdfine1," +
            "case when c.cdfine2='1' then c.cdfine2 else '0' end as cdfine2," +
            "case when c.cdfine3='1' then c.cdfine3 else '0' end as cdfine3," +
            "case when c.cdfine4='1' then c.cdfine4 else '0' end as cdfine4," +
            "case when c.cdfine5='1' then c.cdfine5 else '0' end as cdfine5," +
            "case when c.cdfine6='1' then c.cdfine6 else '0' end as cdfine6," +
            "case when c.cdfine7='1' then c.cdfine7 else '0' end as cdfine7," +
            "case when c.cdfine8='1' then c.cdfine8 else '0' end as cdfine8," +
            "case when c.cdfine9='1' then c.cdfine9 else '0' end as cdfine9," +
            "case when c.cdfine10='1' then c.cdfine10 else '0' end as cdfine10," +
            "case when c.cdfine11='1' then c.cdfine11 else '0' end as cdfine11," +
            "case when c.cdfine12='1' then c.cdfine12 else '0' end as cdfine12," +
            "case when c.cdfine13='1' then c.cdfine13 else '0' end as cdfine13," +
            "case when c.cdfine14='1' then c.cdfine14 else '0' end as cdfine14," +
            "case when c.cdfine15='1' then c.cdfine15 else '0' end as cdfine15," +
            "case when c.cdfine16='1' then c.cdfine16 else '0' end as cdfine16," +
            "case when c.cdfine17='1' then c.cdfine17 else '0' end as cdfine17," +
            "case when c.cdfine18='1' then c.cdfine18 else '0' end as cdfine18," +
            "case when c.cdfine19='1' then c.cdfine19 else '0' end as cdfine19," +
            "case when c.cdfine20='1' then c.cdfine20 else '0' end as cdfine20," +
            "case when c.cdfine21='1' then c.cdfine21 else '0' end as cdfine21," +
            "case when c.cdfine22='1' then c.cdfine22 else '0' end as cdfine22," +
            "case when c.cdfine23='1' then c.cdfine23 else '0' end as cdfine23," +
            "case when c.cdfine24='1' then c.cdfine24 else '0' end as cdfine24," +
            "case when c.cdfine25='1' then c.cdfine25 else '0' end as cdfine25," +
            "case when c.cdfine26='1' then c.cdfine26 else '0' end as cdfine26," +
            "case when c.cdfine27='1' then c.cdfine27 else '0' end as cdfine27," +
            "case when c.cdfine28='1' then c.cdfine28 else '0' end as cdfine28," +
            "case when c.cdfine29='1' then c.cdfine29 else '0' end as cdfine29," +
            "case when c.cdfine30='1' then c.cdfine30 else '0' end as cdfine30 from code_kemu c) c ";

    /**
     * 获取科目辅助
     *
     * @param iyear
     * @return
     */
    @PostMapping("findAllSubjectInitialBalanceFuZhu")
    public Mono<R> findAllSubjectInitialBalanceFuZhu(String iyear, String lastCode, String databasenum) {
        /*
        // 第一种@
        r2dbcEntityTemplate.getDatabaseClient()
                .sql("")
                .fetch()
                .all().collectList()
                .map(item->{
                    return item;
                });
        // 第二@种@
        // r2dbcEntityTemplate.
        */
        iyear = iyear.equals("") ? new SimpleDateFormat("yyyy").format(new Date()) : iyear;
        String iyperiod = iyear + "00";
        lastCode = "1";
        return service.findAllSubjectInitialBalanceFuZhu(iyear, iyperiod, databasenum, databasenum);
    }

    @PostMapping("findAllSubjectInitialBalanceFuZhuList")
    public Mono<R> findAllSubjectInitialBalanceFuZhuList(String iyear, String ccode, String databasenum) {
        return service.findAllSubjectInitialBalanceFuZhuList(iyear, ccode, databasenum);
    }

    @PostMapping("findAllSubjectInitialBalanceFuZhuListMingXi")
    public Mono<R> findAllSubjectInitialBalanceFuZhuListMingXi(String iyear, String ccode, String databasenum) {
        return service.findAllSubjectInitialBalanceFuZhuListMingXi(iyear, ccode, databasenum);
    }

    @Autowired
    R2dbcEntityTemplate r2dbcEntityTemplate;

    @PostMapping("saveSubjectInitialBalance")
    public Mono<R> saveSubjectInitialBalance(@RequestBody SubjectInitialBalanceVo vo, String accId, String userName, String databasenum) {
        Calendar date = Calendar.getInstance();
//        String year = String.valueOf(date.get(Calendar.YEAR));
        String year = vo.getIyear();
        String type = vo.getAccvouid() == null ? "10" : "1";
        String type2 = vo.getAccvouid() == null ? "新增" : "修改";

        Accvoucher accvoucher = new Accvoucher();
        // 清空操作
        boolean closeWhen = false;
        if ((null == vo.getMd() || vo.getMd().doubleValue() == 0) && (null == vo.getMc() || vo.getMc().doubleValue() == 0))
            closeWhen = true;
        // 单价
        BigDecimal dj = null;
        if (!closeWhen && ((null != vo.getMd() && null != vo.getNdS() && vo.getNdS().doubleValue() != 0) || (null != vo.getMc() && null != vo.getNcS() && vo.getNcS().doubleValue() != 0))) {
            dj = null != vo.getMd() && vo.getMd().doubleValue() != 0 ? vo.getMd().divide(vo.getNdS(), 5, BigDecimal.ROUND_HALF_UP).abs() : vo.getMc().divide(vo.getNcS(), 5, BigDecimal.ROUND_HALF_UP).abs();
            if ((null != vo.getNdS() && vo.getNdS().doubleValue() < 0) || (null != vo.getNcS() && vo.getNcS().doubleValue() < 0)) {
                if (null != vo.getMd() && vo.getMd().doubleValue() > 0) {
                    vo.setMd(vo.getMd().negate());
                } else if (null != vo.getMc() && vo.getMc().doubleValue() > 0) {
                    vo.setMc(vo.getMc().negate());
                }
            }
        }
        // 汇率
        BigDecimal hl = null;
        if (!closeWhen && ((null != vo.getMd() || null != vo.getMc()) && null != vo.getNfrat() && vo.getNfrat().doubleValue() != 0)) {
            hl = null != vo.getMd() && vo.getMd().intValue() != 0 ? vo.getMd().divide(vo.getNfrat(), 5, BigDecimal.ROUND_HALF_UP).abs() : vo.getMc().divide(vo.getNfrat(), 5, BigDecimal.ROUND_HALF_UP).abs();
            if (null != vo.getNfrat() && vo.getNfrat().doubleValue() < 0) {
                if (null != vo.getMd() && vo.getMd().doubleValue() > 0) {
                    vo.setMd(vo.getMd().negate());
                } else if (null != vo.getMc() && vo.getMc().doubleValue() > 0) {
                    vo.setMc(vo.getMc().negate());
                }
            }
        }

        accvoucher.setIyperiod(year + "00")
                .setIyear(year)
                .setImonth("00")
                .setId(vo.getAccvouid())
                .setIperiod("00")
                .setCcode(vo.getCcode())
                .setMdF("0")
                .setIbook("0")
                .setMd(vo.getMd() == null ? "0" : String.valueOf(vo.getMd()))
                .setMc(vo.getMc() == null ? "0" : String.valueOf(vo.getMc()))
                .setNdS(vo.getNdS() == null ? "0" : vo.getNdS().toString())
                .setNcS(vo.getNcS() == null ? "0" : vo.getNcS().toString())
                .setNfratMd(vo.getMd().intValue() != 0 ? vo.getNfrat() == null ? "0" : vo.getNfrat().toString():"0")
                .setNfratMc(vo.getMc().intValue() != 0 ? vo.getNfrat() == null ? "0" : vo.getNfrat().toString():"0")
                .setCunitPrice(dj == null ? "0" : dj.toString()) // 单价
                .setMdF(hl == null ? "0" : hl.toString()) // 汇率
                .setLjMd(vo.getLjMd() == null ? "0" : String.valueOf(vo.getLjMd())).setLjMc(vo.getLjMc() == null ? "0" : String.valueOf(vo.getLjMc()))
                .setLjWbMd(vo.getLjMd().intValue() != 0 ?String.valueOf(vo.getLjWbMd()):"0")
                .setLjWbMc(vo.getLjMc().intValue() != 0 ?String.valueOf(vo.getLjWbMc()):"0")
                .setLjSlMd(vo.getLjMd().intValue() != 0 ?String.valueOf(vo.getLjSlMd()):"0")
                .setLjSlMc(vo.getLjMc().intValue() != 0 ?String.valueOf(vo.getLjSlMc()):"0")
                .setTenantId(databasenum);

        Mono<R> add = Mono.just(accvoucher)
                .flatMap(accvoucherRepository::save)
                .flatMap(itemlog ->
                     logRepository.save(new SysLog().setUniqueCode(accId).setUserName(userName).setAccId(accId).setIyear(year).setOperationCont("【" + userName + "在" + new SimpleDateFormat(" yyyy-MM-dd'T'HH:mm:ss").format(new Date()) + "】" + type2 + "一个期初余额。科目编码【" + accvoucher.getCcode() + "】,借方金额【" + accvoucher.getMd() + "】,贷方金额【" + accvoucher.getMc() + "】").setOperationDate(new SimpleDateFormat(" yyyy-MM-dd'T'HH:mm:ss").format(new Date())).setLogMethod(type)).map(o -> itemlog)
                )
                .map(o -> R.ok().setResult(o));

        Mono<R> edit = accvoucherRepository.findById(accvoucher.getId() == null ? "0" : accvoucher.getId())
                .flatMap(item -> {
                    // 回滚表
                    AccvoucherRollback rollback = new AccvoucherRollback();
                    rollback.setIyperiod(item.getIyperiod())
                            .setIyear(item.getIyear())
                            .setIperiod("00")
                            .setImonth("00")
                            .setCcode(item.getCcode())
                            .setMd(item.getMd())
                            .setMc(item.getMc())
                            .setBiandongMethod("1")
                            .setBiandongName(userName)
                            .setBiandongUniqueCode(userName)
                            .setBiandongDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()))
                            .setBiandongId(item.getId())
                            .setNdS(item.getNdS())
                            .setNcS(item.getNcS());
                    return Mono.just(rollback);
                })
                // 添加回滚表
                .flatMap(accvoucherRollBackRepository::save)
                .flatMap(item -> Mono.just(accvoucher))
                // 修改数据
                .flatMap(accvoucherRepository::save)
                // 添加日志
                .flatMap(itemlog ->
                     logRepository.save(new SysLog().setUniqueCode(accId).setUserName(userName).setAccId(accId).setIyear(year).setOperationCont("【" + userName + "在" + new SimpleDateFormat(" yyyy-MM-dd'T'HH:mm:ss").format(new Date()) + "】" + type2 + "一个期初余额。科目编码【" + accvoucher.getCcode() + "】,借方金额【" + accvoucher.getMd() + "】,贷方金额【" + accvoucher.getMc() + "】").setOperationDate(new SimpleDateFormat(" yyyy-MM-dd'T'HH:mm:ss").format(new Date())).setLogMethod(type))
                            .map(o -> itemlog)
                )
                .map(o -> R.ok().setResult(o));

        Mono<R> del = null != accvoucher.getId() ? accvoucherRepository.findById(accvoucher.getId()).flatMap(accvoucherRepository::delete).thenReturn(R.ok()) : Mono.just(R.ok());
        return vo.getAccvouid() == null ? add : closeWhen ? del : edit;
    }

    @PostMapping("saveSubjectInitialBalanceFuZhu")
    public Mono<R> saveSubjectInitialBalanceFuZhu(@RequestBody SubjectInitialBalanceVo vo, String accId, String userName, String databasenum) {
        String type22 = "10";
        String type222 = "新增";

        Accvoucher accvoucher = new Accvoucher();
        if (vo.getAccvouid() != null) {
            accvoucher.setId(vo.getAccvouid());
            type22 = "1";
            type222 = "修改";
        } else {
            vo.setUniqueCode(IdUtil.objectId());//IdUtil.objectId()
            vo.setVouchUnCode(IdUtil.objectId());//IdUtil.objectId()
        }

        String type = "" + type22;
        String type2 = "" + type222;
        BigDecimal dj = null;
        if (((null != vo.getMd() && null != vo.getNdS() && vo.getNdS().doubleValue() != 0) || (null != vo.getMc() && null != vo.getNcS() && vo.getNcS().doubleValue() != 0))) {
            dj = null != vo.getMd() ? vo.getMd().divide(vo.getNdS(), 2, BigDecimal.ROUND_HALF_UP).abs() : vo.getMc().divide(vo.getNcS(), 2, BigDecimal.ROUND_HALF_UP).abs();
        }
        BigDecimal hl = null;
        // 汇率
        if (((null != vo.getMd() || null != vo.getMc()) && null != vo.getNfratMd() && vo.getNfratMd().doubleValue() != 0)) {
            hl = null != vo.getMd() ? vo.getMd().divide(vo.getNfratMd(), 5, BigDecimal.ROUND_HALF_UP).abs() : vo.getMc().divide(vo.getNfratMd(), 5, BigDecimal.ROUND_HALF_UP).abs();
        }
        accvoucher.setIyperiod(vo.getIyear() + "21")
                .setIyear(vo.getIyear())
                .setImonth("21")
                .setIperiod("21")
                .setCcode(vo.getCcode())
                .setCcodeName(vo.getCcodeName())

                .setUniqueCode(vo.getUniqueCode())
                .setVouchUnCode(vo.getVouchUnCode())
                .setDbillDate(vo.getDbillDate())
                .setInoId(vo.getInoId())
                .setCdigest(vo.getCdigest())
                .setInid("1")
                .setIbook("0")
                .setMd(vo.getMd() == null ? "0" : String.valueOf(vo.getMd()))
                .setMc(vo.getMc() == null ? "0" : String.valueOf(vo.getMc()))
                .setNdS(vo.getNdS() == null ? "0" : vo.getNdS().toString())
                .setNcS(vo.getNcS() == null ? "0" : vo.getNcS().toString())
                .setCunitPrice(vo.getNcnum() == null ? "0" : vo.getNcnum().toString())
                .setNfratMd(vo.getNfratMd() == null ? "0" : vo.getNfratMd().toString())
                .setNfratMc(vo.getNfratMc() == null ? "0" : vo.getNfratMc().toString())
                .setMdF(vo.getNfrat() == null ? "0" : vo.getNfrat().toString())
                .setMcF("0")

                .setLjWbMd(vo.getLjWbMd() == null ? "0" : String.valueOf(vo.getLjWbMd()))
                .setLjWbMc(vo.getLjWbMc() == null ? "0" : String.valueOf(vo.getLjWbMc()))
                .setLjSlMd(vo.getLjSlMd() == null ? "0" : String.valueOf(vo.getLjSlMd()))
                .setLjSlMc(vo.getLjSlMc() == null ? "0" : String.valueOf(vo.getLjSlMc()))
                .setLjMd(vo.getLjMd() == null ? "0" : String.valueOf(vo.getLjMd()))
                .setLjMc(vo.getLjMc() == null ? "0" : String.valueOf(vo.getLjMc()))
                .setTenantId(databasenum);

        if ((null != vo.getNdS() && vo.getNdS().doubleValue() < 0) || (null != vo.getNcS() && vo.getNcS().doubleValue() < 0) || (null != vo.getNfrat() && vo.getNfrat().doubleValue() < 0)) {
            if (null != vo.getMd() && vo.getMd().doubleValue() > 0) {
                accvoucher.setMd(String.valueOf(vo.getMd().negate()));
            } else if (null != vo.getMc() && vo.getMc().doubleValue() > 0) {
                accvoucher.setMc(String.valueOf(vo.getMc().negate()));
            }
        }

        if ("1".equals(vo.getBperson())) {
            accvoucher.setCpersonId(vo.getCpersonId());
        }
        if ("1".equals(vo.getBdept())) {
            accvoucher.setCdeptId(vo.getCdeptId());
        }
        if ("1".equals(vo.getBcus())) {
            accvoucher.setCcusId(vo.getCcusId());
        }
        if ("1".equals(vo.getBsup())) {
            accvoucher.setCsupId(vo.getCsupId());
        }
        if ("1".equals(vo.getBitem())) {
            accvoucher.setProjectId(vo.getProjectId());
        }
        if ("1".equals(vo.getBitem())) {
            accvoucher.setProjectClassId(vo.getProjectClassId());
        }
        //数量核算
        if ("1".equals(vo.getBnum())) {
            accvoucher.setNdS(vo.getNdS() == null ? "0" : vo.getNdS().toString());
            accvoucher.setNcS(vo.getNcS() == null ? "0" : vo.getNcS().toString());
            accvoucher.setCunitPrice(dj == null ? "0" : dj.toString()); // 单价
        }
        //是否外币核算
        if ("1".equals(vo.getCurrency())) {
            accvoucher.setNfratMd(vo.getNfratMd() == null ? "0" : vo.getNfratMd().toString());
            accvoucher.setNfratMc(vo.getNfratMc() == null ? "0" : vo.getNfratMc().toString());
            accvoucher.setMdF(hl == null ? "0" : hl.toString()); // 汇率
        }
        //自定义核算
        if ("1".equals(vo.getCdfine1())) {
            accvoucher.setCdfine1(vo.getCdfine1Id());
        }
        if ("1".equals(vo.getCdfine2())) {
            accvoucher.setCdfine1(vo.getCdfine2Id());
        }
        if ("1".equals(vo.getCdfine3())) {
            accvoucher.setCdfine1(vo.getCdfine3Id());
        }
        if ("1".equals(vo.getCdfine4())) {
            accvoucher.setCdfine1(vo.getCdfine4Id());
        }
        if ("1".equals(vo.getCdfine5())) {
            accvoucher.setCdfine1(vo.getCdfine5Id());
        }
        if ("1".equals(vo.getCdfine6())) {
            accvoucher.setCdfine1(vo.getCdfine6Id());
        }
        if ("1".equals(vo.getCdfine7())) {
            accvoucher.setCdfine1(vo.getCdfine7Id());
        }
        if ("1".equals(vo.getCdfine8())) {
            accvoucher.setCdfine1(vo.getCdfine8Id());
        }
        if ("1".equals(vo.getCdfine9())) {
            accvoucher.setCdfine1(vo.getCdfine9Id());
        }

        if ("1".equals(vo.getCdfine10())) {
            accvoucher.setCdfine10(vo.getCdfine10Id());
        }
        if ("1".equals(vo.getCdfine11())) {
            accvoucher.setCdfine11(vo.getCdfine11Id());
        }
        if ("1".equals(vo.getCdfine12())) {
            accvoucher.setCdfine12(vo.getCdfine12Id());
        }
        if ("1".equals(vo.getCdfine13())) {
            accvoucher.setCdfine13(vo.getCdfine13Id());
        }
        if ("1".equals(vo.getCdfine14())) {
            accvoucher.setCdfine14(vo.getCdfine14Id());
        }
        if ("1".equals(vo.getCdfine15())) {
            accvoucher.setCdfine15(vo.getCdfine15Id());
        }
        if ("1".equals(vo.getCdfine16())) {
            accvoucher.setCdfine16(vo.getCdfine16Id());
        }
        if ("1".equals(vo.getCdfine17())) {
            accvoucher.setCdfine17(vo.getCdfine17Id());
        }
        if ("1".equals(vo.getCdfine18())) {
            accvoucher.setCdfine18(vo.getCdfine18Id());
        }
        if ("1".equals(vo.getCdfine19())) {
            accvoucher.setCdfine19(vo.getCdfine19Id());
        }

        if ("1".equals(vo.getCdfine20())) {
            accvoucher.setCdfine20(vo.getCdfine20Id());
        }
        if ("1".equals(vo.getCdfine21())) {
            accvoucher.setCdfine21(vo.getCdfine21Id());
        }
        if ("1".equals(vo.getCdfine22())) {
            accvoucher.setCdfine22(vo.getCdfine22Id());
        }
        if ("1".equals(vo.getCdfine23())) {
            accvoucher.setCdfine23(vo.getCdfine23Id());
        }
        if ("1".equals(vo.getCdfine24())) {
            accvoucher.setCdfine24(vo.getCdfine24Id());
        }
        if ("1".equals(vo.getCdfine25())) {
            accvoucher.setCdfine25(vo.getCdfine25Id());
        }
        if ("1".equals(vo.getCdfine26())) {
            accvoucher.setCdfine26(vo.getCdfine26Id());
        }
        if ("1".equals(vo.getCdfine27())) {
            accvoucher.setCdfine27(vo.getCdfine27Id());
        }
        if ("1".equals(vo.getCdfine28())) {
            accvoucher.setCdfine28(vo.getCdfine28Id());
        }
        if ("1".equals(vo.getCdfine29())) {
            accvoucher.setCdfine29(vo.getCdfine29Id());
        }
        if ("1".equals(vo.getCdfine30())) {
            accvoucher.setCdfine30(vo.getCdfine30Id());
        }
        AtomicReference<Double> md = new AtomicReference<>(0.0);
        AtomicReference<Double> mc = new AtomicReference<>(0.0);
        AtomicReference<Double> nds = new AtomicReference<>(0.0);
        AtomicReference<Double> ncs = new AtomicReference<>(0.0);
        AtomicReference<Double> nfratMd = new AtomicReference<>(0.0);
        AtomicReference<Double> nfratMc = new AtomicReference<>(0.0);
        AtomicReference<Double> cha = new AtomicReference<>(0.0);
        AtomicReference<String> id = new AtomicReference<>("");

        AtomicReference<Double> ljmd = new AtomicReference<>(0.0);
        AtomicReference<Double> ljmc = new AtomicReference<>(0.0);
        AtomicReference<Double> ljslmd = new AtomicReference<>(0.0);
        AtomicReference<Double> ljslmc = new AtomicReference<>(0.0);
        AtomicReference<Double> ljwbmd = new AtomicReference<>(0.0);
        AtomicReference<Double> ljwbmc = new AtomicReference<>(0.0);


        return accvoucherRepository.save(accvoucher)
                .flatMap(item -> {
                    id.set(item.getId());
                    return accvoucherRepository.findAllByIyperiodAndCcodeOrderById(vo.getIyear() + "21", vo.getCcode()).collectList()
                            .map(item2 -> {
                                md.set(item2.stream().mapToDouble(i -> Double.parseDouble(i.getMd())).sum());
                                mc.set(item2.stream().mapToDouble(i -> Double.parseDouble(i.getMc())).sum());
                                ljmd.set(item2.stream().mapToDouble(i -> Double.parseDouble(i.getLjMd())).sum());
                                ljmc.set(item2.stream().mapToDouble(i -> Double.parseDouble(i.getLjMc())).sum());

                                //数量核算
                                if ("1".equals(vo.getBnum())) {
                                    nds.set(item2.stream().mapToDouble(i -> Double.parseDouble(i.getNdS())).sum());
                                    ncs.set(item2.stream().mapToDouble(i -> Double.parseDouble(i.getNcS())).sum());
                                    ljslmd.set(item2.stream().mapToDouble(i -> Double.parseDouble(i.getLjSlMd())).sum());
                                    ljslmc.set(item2.stream().mapToDouble(i -> Double.parseDouble(i.getLjSlMc())).sum());
                                    nds.set(fmtJiShui(nds.get()));
                                    ncs.set(fmtJiShui(ncs.get()));
                                    ljslmd.set(fmtJiShui(ljslmd.get()));
                                    ljslmc.set(fmtJiShui(ljslmc.get()));
                                }
                                //是否外币核算
                                if ("1".equals(vo.getCurrency())) {
                                    nfratMd.set(item2.stream().mapToDouble(i -> Double.parseDouble(i.getNfratMd())).sum());
                                    nfratMc.set(item2.stream().mapToDouble(i -> Double.parseDouble(i.getNfratMc())).sum());

                                    ljwbmd.set(item2.stream().mapToDouble(i -> Double.parseDouble(i.getLjWbMd())).sum());
                                    ljwbmc.set(item2.stream().mapToDouble(i -> Double.parseDouble(i.getLjWbMc())).sum());

                                    nfratMd.set(fmtJiShui(nfratMd.get()));
                                    nfratMc.set(fmtJiShui(nfratMc.get()));
                                    ljwbmd.set(fmtJiShui(ljwbmd.get()));
                                    ljwbmc.set(fmtJiShui(ljwbmc.get()));
                                    if (nfratMd.get() != 0.0 && nfratMc.get() != 0.0) {
                                        if (nfratMd.get() - nfratMc.get() > 0) {
                                            nfratMd.set(nfratMd.get() - nfratMc.get());
                                            nfratMc.set(0.0);
                                        } else if (nfratMd.get() - nfratMc.get() < 0) {
                                            nfratMc.set(nfratMc.get() - nfratMd.get());
                                            nfratMd.set(0.0);
                                        }
                                    }
                                }

                                md.set(fmtJiShui(md.get()));
                                mc.set(fmtJiShui(mc.get()));
                                if (md.get() != 0.0 && mc.get() != 0.0) {
                                    if (md.get() - mc.get() > 0) {
                                        md.set(md.get() - mc.get());
                                        mc.set(0.0);
                                    } else if (md.get() - mc.get() < 0) {
                                        mc.set(mc.get() - md.get());
                                        md.set(0.0);
                                    }
                                }
                                cha.set(md.get() - mc.get());

                                if (mc.get() == 0.0) {
                                    nds.set(nds.get() - ncs.get());
                                    ncs.set(0.0);
                                    nfratMd.set(nfratMd.get() - nfratMc.get());
                                    nfratMc.set(0.0);
                                } else if (md.get() == 0.0) {
                                    ncs.set(ncs.get() - nds.get());
                                    nds.set(0.0);
                                    nfratMc.set(nfratMc.get() - nfratMd.get());
                                    nfratMd.set(0.0);
                                }
                                return item2;
                            })
                            .flatMap(item2 -> {
                                return accvoucherRepository.findAllByIyperiodAndCcodeOrderById(vo.getIyear() + "00", vo.getCcode()).collectList()
                                        .flatMap(v -> {
                                            if (v.size() == 0) {
                                                if (cha.get() != 0.0) {
                                                    //baocunff
                                                    Accvoucher acc = new Accvoucher();
                                                    acc.setIyperiod(vo.getIyear() + "00")
                                                            .setIyear(vo.getIyear())
                                                            .setImonth("00")
                                                            .setIperiod("00")
                                                            .setCcode(vo.getCcode())
                                                            .setMdF("0")
                                                            .setIbook("0")
                                                            .setLjWbMd("0")
                                                            .setLjWbMc("0")
                                                            .setLjSlMd("0")
                                                            .setLjSlMc("0")
                                                            .setMd(String.valueOf(md.get()))
                                                            .setMc(String.valueOf(mc.get()))
                                                            .setLjMd(String.valueOf(ljmd.get()))
                                                            .setLjMc(String.valueOf(ljmc.get()))
                                                            .setNdS("0")
                                                            .setNcS("0")
                                                            .setNfratMd("0")
                                                            .setNfratMc("0")
                                                            .setTenantId(databasenum);

                                                    if ("1".equals(vo.getBnum())) {
                                                        acc.setNdS(String.valueOf(nds.get()));
                                                        acc.setNcS(String.valueOf(ncs.get()));

                                                        acc.setLjSlMd(String.valueOf(ljslmd.get()));
                                                        acc.setLjSlMc(String.valueOf(ljslmc.get()));
                                                        acc.setCunitPrice("0");
                                                    }
                                                    //是否外币核算
                                                    if ("1".equals(vo.getCurrency())) {
                                                        acc.setNfratMd(String.valueOf(nfratMd.get()));
                                                        acc.setNfratMc(String.valueOf(nfratMc.get()));
                                                        acc.setMdF("0");
                                                        acc.setLjWbMd(String.valueOf(ljwbmd.get()));
                                                        acc.setLjWbMc(String.valueOf(ljwbmc.get()));
                                                    }

                                                    return accvoucherRepository.save(acc);
                                                }
                                            }
                                            if (v.size() != 0) {
                                                if (cha.get() != 0.0) {
                                                    //baocunff
                                                    Accvoucher acc = v.stream().findFirst().get();
                                                    acc.setMd(String.valueOf(md.get()));
                                                    acc.setMc(String.valueOf(mc.get()));
                                                    acc.setLjMd(String.valueOf(ljmd.get()));
                                                    acc.setLjMc(String.valueOf(ljmc.get()));
                                                    if ("1".equals(vo.getBnum())) {
                                                        acc.setNdS(String.valueOf(nds.get()));
                                                        acc.setNcS(String.valueOf(ncs.get()));
                                                        acc.setCunitPrice("0");
                                                        acc.setLjSlMd(String.valueOf(ljslmd.get()));
                                                        acc.setLjSlMc(String.valueOf(ljslmc.get()));
                                                    }
                                                    //是否外币核算
                                                    if ("1".equals(vo.getCurrency())) {
                                                        acc.setNfratMd(String.valueOf(nfratMd.get()));
                                                        acc.setNfratMc(String.valueOf(nfratMc.get()));
                                                        acc.setMdF("0");
                                                        acc.setLjWbMd(String.valueOf(ljwbmd.get()));
                                                        acc.setLjWbMc(String.valueOf(ljwbmc.get()));
                                                    }
                                                    return accvoucherRepository.save(acc);
                                                }
                                                if (cha.get() == 0.0) {
                                                    //baocunff
                                                    Accvoucher acc = v.stream().findFirst().get();
                                                    return accvoucherRepository.delete(acc);
                                                }
                                            }
                                            return Mono.just(id.get());
                                        });
                            })
                            .map(v -> {
                                return id.get();
                            });
                })
                .map(o -> R.ok().setResult(o));
    }
    @PostMapping("modifySubjectInitialBalanceFuZhu")
    public Mono<R> modifySubjectInitialBalanceFuZhu(@RequestBody SubjectInitialBalanceVo vo, String accId, String userName, String databasenum) {
        String type22 = "10";
        String type222 = "新增";
        Accvoucher accvoucher = new Accvoucher();
        BeanUtils.copyProperties(vo,accvoucher);
        if (vo.getAccvouid() != null) {
            accvoucher.setId(vo.getAccvouid());
            type22 = "1";
            type222 = "修改";
        } else {
            vo.setUniqueCode(IdUtil.objectId());//IdUtil.objectId()
            vo.setVouchUnCode(IdUtil.objectId());//IdUtil.objectId()
        }

        String type = "" + type22;
        String type2 = "" + type222;
        BigDecimal dj = null;
        if (((null != vo.getMd() && null != vo.getNdS() && vo.getNdS().doubleValue() != 0) || (null != vo.getMc() && null != vo.getNcS() && vo.getNcS().doubleValue() != 0))) {
            dj = null != vo.getMd() ? vo.getMd().divide(vo.getNdS(), 2, BigDecimal.ROUND_HALF_UP).abs() : vo.getMc().divide(vo.getNcS(), 2, BigDecimal.ROUND_HALF_UP).abs();
        }
        BigDecimal hl = null;
        // 汇率
        if (((null != vo.getMd() || null != vo.getMc()) && null != vo.getNfratMd() && vo.getNfratMd().doubleValue() != 0)) {
            hl = null != vo.getMd() ? vo.getMd().divide(vo.getNfratMd(), 5, BigDecimal.ROUND_HALF_UP).abs() : vo.getMc().divide(vo.getNfratMd(), 5, BigDecimal.ROUND_HALF_UP).abs();
        }
        accvoucher.setIyperiod(vo.getIyear() + "21")
                .setIyear(vo.getIyear())
                .setImonth("21")
                .setIperiod("21")
                .setCcode(vo.getCcode())
                .setCcodeName(vo.getCcodeName())

                .setUniqueCode(vo.getUniqueCode())
                .setVouchUnCode(vo.getVouchUnCode())
                .setDbillDate(vo.getDbillDate())
                .setInoId(vo.getInoId())
                .setCdigest(vo.getCdigest())
                .setInid("1")
                .setIbook("0")
                .setMd(vo.getMd() == null ? "0" : String.valueOf(vo.getMd()))
                .setMc(vo.getMc() == null ? "0" : String.valueOf(vo.getMc()))
                .setNdS(vo.getNdS() == null ? "0" : vo.getNdS().toString())
                .setNcS(vo.getNcS() == null ? "0" : vo.getNcS().toString())
                .setCunitPrice(vo.getNcnum() == null ? "0" : vo.getNcnum().toString())
                .setNfratMd(vo.getNfratMd() == null ? "0" : vo.getNfratMd().toString())
                .setNfratMc(vo.getNfratMc() == null ? "0" : vo.getNfratMc().toString())
                .setMdF(vo.getNfrat() == null ? "0" : vo.getNfrat().toString())
                .setMcF("0")

                .setLjWbMd(vo.getLjWbMd() == null ? "0" : String.valueOf(vo.getLjWbMd()))
                .setLjWbMc(vo.getLjWbMc() == null ? "0" : String.valueOf(vo.getLjWbMc()))
                .setLjSlMd(vo.getLjSlMd() == null ? "0" : String.valueOf(vo.getLjSlMd()))
                .setLjSlMc(vo.getLjSlMc() == null ? "0" : String.valueOf(vo.getLjSlMc()))
                .setLjMd(vo.getLjMd() == null ? "0" : String.valueOf(vo.getLjMd()))
                .setLjMc(vo.getLjMc() == null ? "0" : String.valueOf(vo.getLjMc()))
                .setTenantId(databasenum);

        if ((null != vo.getNdS() && vo.getNdS().doubleValue() < 0) || (null != vo.getNcS() && vo.getNcS().doubleValue() < 0) || (null != vo.getNfrat() && vo.getNfrat().doubleValue() < 0)) {
            if (null != vo.getMd() && vo.getMd().doubleValue() > 0) {
                accvoucher.setMd(String.valueOf(vo.getMd().negate()));
            } else if (null != vo.getMc() && vo.getMc().doubleValue() > 0) {
                accvoucher.setMc(String.valueOf(vo.getMc().negate()));
            }
        }

        if ("1".equals(vo.getBperson())) {
            accvoucher.setCpersonId(vo.getCpersonId());
        }
        if ("1".equals(vo.getBdept())) {
            accvoucher.setCdeptId(vo.getCdeptId());
        }
        if ("1".equals(vo.getBcus())) {
            accvoucher.setCcusId(vo.getCcusId());
        }
        if ("1".equals(vo.getBsup())) {
            accvoucher.setCsupId(vo.getCsupId());
        }
        if ("1".equals(vo.getBitem())) {
            accvoucher.setProjectId(vo.getProjectId());
        }
        if ("1".equals(vo.getBitem())) {
            accvoucher.setProjectClassId(vo.getProjectClassId());
        }
        //数量核算
        if ("1".equals(vo.getBnum())) {
            accvoucher.setNdS(vo.getNdS() == null ? "0" : vo.getNdS().toString());
            accvoucher.setNcS(vo.getNcS() == null ? "0" : vo.getNcS().toString());
            accvoucher.setCunitPrice(dj == null ? "0" : dj.toString()); // 单价
        }
        //是否外币核算
        if ("1".equals(vo.getCurrency())) {
            accvoucher.setNfratMd(vo.getNfratMd() == null ? "0" : vo.getNfratMd().toString());
            accvoucher.setNfratMc(vo.getNfratMc() == null ? "0" : vo.getNfratMc().toString());
            accvoucher.setMdF(hl == null ? "0" : hl.toString()); // 汇率
        }
        //自定义核算
        if ("1".equals(vo.getCdfine1())) {
            accvoucher.setCdfine1(vo.getCdfine1Id());
        }
        if ("1".equals(vo.getCdfine2())) {
            accvoucher.setCdfine1(vo.getCdfine2Id());
        }
        if ("1".equals(vo.getCdfine3())) {
            accvoucher.setCdfine1(vo.getCdfine3Id());
        }
        if ("1".equals(vo.getCdfine4())) {
            accvoucher.setCdfine1(vo.getCdfine4Id());
        }
        if ("1".equals(vo.getCdfine5())) {
            accvoucher.setCdfine1(vo.getCdfine5Id());
        }
        if ("1".equals(vo.getCdfine6())) {
            accvoucher.setCdfine1(vo.getCdfine6Id());
        }
        if ("1".equals(vo.getCdfine7())) {
            accvoucher.setCdfine1(vo.getCdfine7Id());
        }
        if ("1".equals(vo.getCdfine8())) {
            accvoucher.setCdfine1(vo.getCdfine8Id());
        }
        if ("1".equals(vo.getCdfine9())) {
            accvoucher.setCdfine1(vo.getCdfine9Id());
        }

        if ("1".equals(vo.getCdfine10())) {
            accvoucher.setCdfine10(vo.getCdfine10Id());
        }
        if ("1".equals(vo.getCdfine11())) {
            accvoucher.setCdfine11(vo.getCdfine11Id());
        }
        if ("1".equals(vo.getCdfine12())) {
            accvoucher.setCdfine12(vo.getCdfine12Id());
        }
        if ("1".equals(vo.getCdfine13())) {
            accvoucher.setCdfine13(vo.getCdfine13Id());
        }
        if ("1".equals(vo.getCdfine14())) {
            accvoucher.setCdfine14(vo.getCdfine14Id());
        }
        if ("1".equals(vo.getCdfine15())) {
            accvoucher.setCdfine15(vo.getCdfine15Id());
        }
        if ("1".equals(vo.getCdfine16())) {
            accvoucher.setCdfine16(vo.getCdfine16Id());
        }
        if ("1".equals(vo.getCdfine17())) {
            accvoucher.setCdfine17(vo.getCdfine17Id());
        }
        if ("1".equals(vo.getCdfine18())) {
            accvoucher.setCdfine18(vo.getCdfine18Id());
        }
        if ("1".equals(vo.getCdfine19())) {
            accvoucher.setCdfine19(vo.getCdfine19Id());
        }

        if ("1".equals(vo.getCdfine20())) {
            accvoucher.setCdfine20(vo.getCdfine20Id());
        }
        if ("1".equals(vo.getCdfine21())) {
            accvoucher.setCdfine21(vo.getCdfine21Id());
        }
        if ("1".equals(vo.getCdfine22())) {
            accvoucher.setCdfine22(vo.getCdfine22Id());
        }
        if ("1".equals(vo.getCdfine23())) {
            accvoucher.setCdfine23(vo.getCdfine23Id());
        }
        if ("1".equals(vo.getCdfine24())) {
            accvoucher.setCdfine24(vo.getCdfine24Id());
        }
        if ("1".equals(vo.getCdfine25())) {
            accvoucher.setCdfine25(vo.getCdfine25Id());
        }
        if ("1".equals(vo.getCdfine26())) {
            accvoucher.setCdfine26(vo.getCdfine26Id());
        }
        if ("1".equals(vo.getCdfine27())) {
            accvoucher.setCdfine27(vo.getCdfine27Id());
        }
        if ("1".equals(vo.getCdfine28())) {
            accvoucher.setCdfine28(vo.getCdfine28Id());
        }
        if ("1".equals(vo.getCdfine29())) {
            accvoucher.setCdfine29(vo.getCdfine29Id());
        }
        if ("1".equals(vo.getCdfine30())) {
            accvoucher.setCdfine30(vo.getCdfine30Id());
        }
        AtomicReference<Double> md = new AtomicReference<>(0.0);
        AtomicReference<Double> mc = new AtomicReference<>(0.0);
        AtomicReference<Double> nds = new AtomicReference<>(0.0);
        AtomicReference<Double> ncs = new AtomicReference<>(0.0);
        AtomicReference<Double> nfratMd = new AtomicReference<>(0.0);
        AtomicReference<Double> nfratMc = new AtomicReference<>(0.0);
        AtomicReference<Double> cha = new AtomicReference<>(0.0);
        AtomicReference<String> id = new AtomicReference<>("");

        AtomicReference<Double> ljmd = new AtomicReference<>(0.0);
        AtomicReference<Double> ljmc = new AtomicReference<>(0.0);
        AtomicReference<Double> ljslmd = new AtomicReference<>(0.0);
        AtomicReference<Double> ljslmc = new AtomicReference<>(0.0);
        AtomicReference<Double> ljwbmd = new AtomicReference<>(0.0);
        AtomicReference<Double> ljwbmc = new AtomicReference<>(0.0);


        return accvoucherRepository.save(accvoucher)
                .flatMap(item -> {
                    id.set(item.getId());
                    return accvoucherRepository.findAllByIyperiodAndCcodeOrderById(vo.getIyear() + "21", vo.getCcode()).collectList()
                            .map(item2 -> {
                                md.set(item2.stream().mapToDouble(i -> Double.parseDouble(i.getMd())).sum());
                                mc.set(item2.stream().mapToDouble(i -> Double.parseDouble(i.getMc())).sum());
                                ljmd.set(item2.stream().mapToDouble(i -> Double.parseDouble(i.getLjMd())).sum());
                                ljmc.set(item2.stream().mapToDouble(i -> Double.parseDouble(i.getLjMc())).sum());

                                //数量核算
                                if ("1".equals(vo.getBnum())) {
                                    nds.set(item2.stream().mapToDouble(i -> Double.parseDouble(i.getNdS())).sum());
                                    ncs.set(item2.stream().mapToDouble(i -> Double.parseDouble(i.getNcS())).sum());
                                    ljslmd.set(item2.stream().mapToDouble(i -> Double.parseDouble(i.getLjSlMd())).sum());
                                    ljslmc.set(item2.stream().mapToDouble(i -> Double.parseDouble(i.getLjSlMc())).sum());
                                    nds.set(fmtJiShui(nds.get()));
                                    ncs.set(fmtJiShui(ncs.get()));
                                    ljslmd.set(fmtJiShui(ljslmd.get()));
                                    ljslmc.set(fmtJiShui(ljslmc.get()));
                                }
                                //是否外币核算
                                if ("1".equals(vo.getCurrency())) {
                                    nfratMd.set(item2.stream().mapToDouble(i -> Double.parseDouble(i.getNfratMd())).sum());
                                    nfratMc.set(item2.stream().mapToDouble(i -> Double.parseDouble(i.getNfratMc())).sum());

                                    ljwbmd.set(item2.stream().mapToDouble(i -> Double.parseDouble(i.getLjWbMd())).sum());
                                    ljwbmc.set(item2.stream().mapToDouble(i -> Double.parseDouble(i.getLjWbMc())).sum());

                                    nfratMd.set(fmtJiShui(nfratMd.get()));
                                    nfratMc.set(fmtJiShui(nfratMc.get()));
                                    ljwbmd.set(fmtJiShui(ljwbmd.get()));
                                    ljwbmc.set(fmtJiShui(ljwbmc.get()));
                                    if (nfratMd.get() != 0.0 && nfratMc.get() != 0.0) {
                                        if (nfratMd.get() - nfratMc.get() > 0) {
                                            nfratMd.set(nfratMd.get() - nfratMc.get());
                                            nfratMc.set(0.0);
                                        } else if (nfratMd.get() - nfratMc.get() < 0) {
                                            nfratMc.set(nfratMc.get() - nfratMd.get());
                                            nfratMd.set(0.0);
                                        }
                                    }
                                }

                                md.set(fmtJiShui(md.get()));
                                mc.set(fmtJiShui(mc.get()));
                                if (md.get() != 0.0 && mc.get() != 0.0) {
                                    if (md.get() - mc.get() > 0) {
                                        md.set(md.get() - mc.get());
                                        mc.set(0.0);
                                    } else if (md.get() - mc.get() < 0) {
                                        mc.set(mc.get() - md.get());
                                        md.set(0.0);
                                    }
                                }
                                cha.set(md.get() - mc.get());

                                if (mc.get() == 0.0) {
                                    nds.set(nds.get() - ncs.get());
                                    ncs.set(0.0);
                                    nfratMd.set(nfratMd.get() - nfratMc.get());
                                    nfratMc.set(0.0);
                                } else if (md.get() == 0.0) {
                                    ncs.set(ncs.get() - nds.get());
                                    nds.set(0.0);
                                    nfratMc.set(nfratMc.get() - nfratMd.get());
                                    nfratMd.set(0.0);
                                }
                                return item2;
                            })
                            .flatMap(item2 -> {
                                return accvoucherRepository.findAllByIyperiodAndCcodeOrderById(vo.getIyear() + "00", vo.getCcode()).collectList()
                                        .flatMap(v -> {
                                            if (v.size() == 0) {
                                                if (cha.get() != 0.0) {
                                                    //baocunff
                                                    Accvoucher acc = new Accvoucher();
                                                    acc.setIyperiod(vo.getIyear() + "00")
                                                            .setIyear(vo.getIyear())
                                                            .setImonth("00")
                                                            .setIperiod("00")
                                                            .setCcode(vo.getCcode())
                                                            .setMdF("0")
                                                            .setIbook("0")
                                                            .setLjWbMd("0")
                                                            .setLjWbMc("0")
                                                            .setLjSlMd("0")
                                                            .setLjSlMc("0")
                                                            .setMd(String.valueOf(md.get()))
                                                            .setMc(String.valueOf(mc.get()))
                                                            .setLjMd(String.valueOf(ljmd.get()))
                                                            .setLjMc(String.valueOf(ljmc.get()))
                                                            .setNdS("0")
                                                            .setNcS("0")
                                                            .setNfratMd("0")
                                                            .setNfratMc("0")
                                                            .setTenantId(databasenum);

                                                    if ("1".equals(vo.getBnum())) {
                                                        acc.setNdS(String.valueOf(nds.get()));
                                                        acc.setNcS(String.valueOf(ncs.get()));

                                                        acc.setLjSlMd(String.valueOf(ljslmd.get()));
                                                        acc.setLjSlMc(String.valueOf(ljslmc.get()));
                                                        acc.setCunitPrice("0");
                                                    }
                                                    //是否外币核算
                                                    if ("1".equals(vo.getCurrency())) {
                                                        acc.setNfratMd(String.valueOf(nfratMd.get()));
                                                        acc.setNfratMc(String.valueOf(nfratMc.get()));
                                                        acc.setMdF("0");
                                                        acc.setLjWbMd(String.valueOf(ljwbmd.get()));
                                                        acc.setLjWbMc(String.valueOf(ljwbmc.get()));
                                                    }

                                                    return accvoucherRepository.save(acc);
                                                }
                                            }
                                            if (v.size() != 0) {
                                                if (cha.get() != 0.0) {
                                                    //baocunff
                                                    Accvoucher acc = v.stream().findFirst().get();
                                                    acc.setMd(String.valueOf(md.get()));
                                                    acc.setMc(String.valueOf(mc.get()));
                                                    acc.setLjMd(String.valueOf(ljmd.get()));
                                                    acc.setLjMc(String.valueOf(ljmc.get()));
                                                    if ("1".equals(vo.getBnum())) {
                                                        acc.setNdS(String.valueOf(nds.get()));
                                                        acc.setNcS(String.valueOf(ncs.get()));
                                                        acc.setCunitPrice("0");
                                                        acc.setLjSlMd(String.valueOf(ljslmd.get()));
                                                        acc.setLjSlMc(String.valueOf(ljslmc.get()));
                                                    }
                                                    //是否外币核算
                                                    if ("1".equals(vo.getCurrency())) {
                                                        acc.setNfratMd(String.valueOf(nfratMd.get()));
                                                        acc.setNfratMc(String.valueOf(nfratMc.get()));
                                                        acc.setMdF("0");
                                                        acc.setLjWbMd(String.valueOf(ljwbmd.get()));
                                                        acc.setLjWbMc(String.valueOf(ljwbmc.get()));
                                                    }
                                                    return accvoucherRepository.save(acc);
                                                }
                                                if (cha.get() == 0.0) {
                                                    //baocunff
                                                    Accvoucher acc = v.stream().findFirst().get();
                                                    return accvoucherRepository.delete(acc);
                                                }
                                            }
                                            return Mono.just(id.get());
                                        });
                            })
                            .map(v -> {
                                return id.get();
                            });
                })
                .map(o -> R.ok().setResult(o));
    }

    private Double fmtJiShui(Double md) {
        return md;
    }

    @PostMapping("/delInitalBalance")
    public Mono<R> delInitalBalance(String str, String iyear) {
        List<AccvoucherRollback> rollBacklist = new ArrayList<>();
        List<Accvoucher> list = new ArrayList<>();
        for (int i = 0; i < str.split(",").length; i++) {
            if (StringUtils.isNotBlank(str.split(",")[i].split("_")[0])) {
                Accvoucher accvoucher = new Accvoucher();
                accvoucher.setIyperiod(iyear + "00")
                        .setIyear(iyear)
                        .setIperiod("00")
                        .setId(str.split(",")[i].split("_")[0])
                        .setCcode(str.split(",")[i].split("_")[1])
                        .setMd(str.split(",")[i].split("_")[2] == null ? "" : str.split(",")[i].split("_")[2])
                        .setMc(str.split(",")[i].split("_")[3] == null ? "" : str.split(",")[i].split("_")[3]);
                list.add(accvoucher);

                // 回滚表
                AccvoucherRollback rollback = new AccvoucherRollback();
                rollback.setIyperiod(iyear + "00")
                        .setIyear(iyear)
                        .setIperiod("00")
                        .setCcode(str.split(",")[i].split("_")[1])
                        .setMd(str.split(",")[i].split("_")[2] == null ? "" : str.split(",")[i].split("_")[2])
                        .setMc(str.split(",")[i].split("_")[3] == null ? "" : str.split(",")[i].split("_")[3])
                        .setBiandongMethod("2")
                        .setBiandongName("哈哈哈")
                        .setBiandongUniqueCode("test001")
                        .setBiandongDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()))
                        .setBiandongId(str.split(",")[i].split("_")[0]);
                /*屏蔽
                list.add(accvoucher);
                */
                rollBacklist.add(rollback);
            }
        }

        //Mono aa = accvoucherRepository.emptyAllByIyperiodAndTenantId(iyear+"21");

        /*下面替换
        return Mono.just(rollBacklist)
                .flatMapMany(accvoucherRollBackRepository::saveAll).collectList().flatMap(item -> Mono.just(list)).flatMap(accvoucherRepository::deleteAll).then(Mono.just(R.ok()));
        */
        Mono<R> rMono = accvoucherRepository.findAllByIyperiodAndCcodeOrderById(iyear + "21", list.get(0).getCcode()).collectList()
                .flatMap(list2 -> list.size() > 0 ? accvoucherRepository.deleteAll(list2) : Mono.just("")).thenReturn(R.ok());
        return Mono.just(list)
                .flatMapMany(accvoucherRepository::deleteAll).then(rMono);
    }

    @PostMapping("/delInitalBalanceFuZhu")
    public Mono<R> delInitalBalanceFuZhu(String str, String iyear, String databasenum, String ccode) {

        Accvoucher accvoucher = new Accvoucher();

        accvoucher.setId(str);
        AtomicReference<Double> md = new AtomicReference<>(0.0);
        AtomicReference<Double> mc = new AtomicReference<>(0.0);
        AtomicReference<Double> cha = new AtomicReference<>(0.0);
        AtomicReference<String> id = new AtomicReference<>("");

        AtomicReference<Double> nds = new AtomicReference<>(0.0);
        AtomicReference<Double> ncs = new AtomicReference<>(0.0);
        AtomicReference<Double> nfratMd = new AtomicReference<>(0.0);
        AtomicReference<Double> nfratMc = new AtomicReference<>(0.0);

        Mono<Void> delete = accvoucherRepository.delete(accvoucher);

        Mono<Object> M1 = accvoucherRepository.findAllByIyperiodAndCcodeOrderById(iyear + "21", ccode).collectList()
                .map(item2 -> {
                    md.set(item2.stream().mapToDouble(i -> Double.parseDouble(i.getMd())).sum());
                    mc.set(item2.stream().mapToDouble(i -> Double.parseDouble(i.getMc())).sum());
                    md.set(fmtJiShui(md.get()));
                    mc.set(fmtJiShui(mc.get()));
                    if (md.get() != 0.0 && mc.get() != 0.0) {
                        if (md.get() - mc.get() > 0) {
                            md.set(md.get() - mc.get());
                            mc.set(0.0);
                        } else if (md.get() - mc.get() < 0) {
                            mc.set(mc.get() - md.get());
                            md.set(0.0);
                        }
                    }
                    cha.set(md.get() - mc.get());

                    //数量核算
                    nds.set(item2.stream().mapToDouble(i -> Double.parseDouble(i.getNdS())).sum());
                    ncs.set(item2.stream().mapToDouble(i -> Double.parseDouble(i.getNcS())).sum());
                    nds.set(fmtJiShui(nds.get()));
                    ncs.set(fmtJiShui(ncs.get()));

                    //是否外币核算
                    nfratMd.set(item2.stream().mapToDouble(i -> Double.parseDouble(i.getNfratMd())).sum());
                    nfratMc.set(item2.stream().mapToDouble(i -> Double.parseDouble(i.getNfratMc())).sum());
                    nfratMd.set(fmtJiShui(nfratMd.get()));
                    nfratMc.set(fmtJiShui(nfratMc.get()));

                    if (nfratMd.get() != 0.0 && nfratMc.get() != 0.0) {
                        if (nfratMd.get() - nfratMc.get() > 0) {
                            nfratMd.set(nfratMd.get() - nfratMc.get());
                            nfratMc.set(0.0);
                        } else if (nfratMd.get() - nfratMc.get() < 0) {
                            nfratMc.set(nfratMc.get() - nfratMd.get());
                            nfratMd.set(0.0);
                        }
                    }

                    if (md.get() != 0.0 && mc.get() != 0.0) {
                        if (md.get() - mc.get() > 0) {
                            md.set(md.get() - mc.get());
                            mc.set(0.0);
                        } else if (md.get() - mc.get() < 0) {
                            mc.set(mc.get() - md.get());
                            md.set(0.0);
                        }
                    }
                    cha.set(md.get() - mc.get());

                    if (mc.get() == 0.0) {
                        nds.set(nds.get() - ncs.get());
                        ncs.set(0.0);
                        nfratMd.set(nfratMd.get() - nfratMc.get());
                        nfratMc.set(0.0);
                    } else if (md.get() == 0.0) {
                        ncs.set(ncs.get() - nds.get());
                        nds.set(0.0);
                        nfratMc.set(nfratMc.get() - nfratMd.get());
                        nfratMd.set(0.0);
                    }

                    return item2;
                })
                .flatMap(item2 -> {
                    return accvoucherRepository.findAllByIyperiodAndCcodeOrderById(iyear + "00", ccode).collectList()
                            .flatMap(v -> {
                                if (v.size() == 0) {
                                    if (cha.get() != 0.0) {
                                        //baocunff
                                        Accvoucher acc = new Accvoucher();
                                        acc.setIyperiod(iyear + "00")
                                                .setIyear(iyear)
                                                .setImonth("00")
                                                .setIperiod("00")
                                                .setCcode(ccode)
                                                .setMdF("0")
                                                .setIbook("0")
                                                .setLjWbMd("0")
                                                .setLjWbMc("0")
                                                .setLjSlMd("0")
                                                .setLjSlMc("0")
                                                .setMd(String.valueOf(md.get()))
                                                .setMc(String.valueOf(mc.get()))
                                                .setNdS("0")
                                                .setNcS("0")
                                                .setNfratMd("0")
                                                .setNfratMc("0")
                                                .setTenantId(databasenum);

                                        //数量核算
                                        acc.setNdS(String.valueOf(nds.get()));
                                        acc.setNcS(String.valueOf(ncs.get()));
                                        acc.setCunitPrice("0");

                                        //外币核算
                                        acc.setNfratMd(String.valueOf(nfratMd.get()));
                                        acc.setNfratMc(String.valueOf(nfratMc.get()));
                                        acc.setMdF("0");

                                        return accvoucherRepository.save(acc);
                                    }
                                }
                                if (v.size() != 0) {
                                    if (cha.get() != 0.0) {
                                        //baocunff
                                        Accvoucher acc = v.stream().findFirst().get();
                                        acc.setMd(String.valueOf(md.get()));
                                        acc.setMc(String.valueOf(mc.get()));

                                        //数量核算
                                        acc.setNdS(String.valueOf(nds.get()));
                                        acc.setNcS(String.valueOf(ncs.get()));
                                        acc.setCunitPrice("0");

                                        //外币核算
                                        acc.setNfratMd(String.valueOf(nfratMd.get()));
                                        acc.setNfratMc(String.valueOf(nfratMc.get()));
                                        acc.setMdF("0");

                                        return accvoucherRepository.save(acc);
                                    }
                                    if (cha.get() == 0.0) {
                                        //baocunff
                                        Accvoucher acc = v.stream().findFirst().get();
                                        return accvoucherRepository.delete(acc);
                                    }
                                }
                                return Mono.just(id.get());
                            });
                })
                .map(v -> {
                    return id.get();
                });
        return delete.then(M1).map(v -> R.ok().setResult(v));
    }


    /**
     * 按年度获取所有末级科目
     *
     * @param bend
     * @param iyear
     * @return
     */
    @PostMapping("/findAllByBendAndIyearOrderByCcode")

    public Mono<R> findAllByBendAndIyearOrderByCcode(String bend, String iyear) {
        return codeKemuRepository.findAllByBendAndIyearOrderByCcode(bend, iyear).collectList().map(item -> R.ok().setResult(item));
    }


    /**
     * 试算平衡-弃用
     */
    @PostMapping("/ssph")
    public Mono<R> ssph(@RequestBody Map map) {
        String iyear=map.get("iyear").toString();
        String standardSelected=map.get("standardSelected").toString();
        String databasenum=map.get("databasenum")==null?"":map.get("databasenum").toString();

        DecimalFormat decimalFormat = new DecimalFormat("###,###.00");
        return accStandardRepository.findByStandardUnique(standardSelected)
                .flatMap(standardList -> {
                    AccStandard accStandard = standardList;
                    // 判断是否预算会计科目，获取对应的计算公式
                    String flag = accStandard.getFlagYusuan();
                    String caiwuStr = accStandard.getCaiwu();
                    String yusuanStr = accStandard.getYusuan();

                    Mono<List<SubjectBalanceRo>> caiwulist = accvoucherRepository.findByAccStyleCcodeMDMC(iyear + "00", iyear).collectList()
                            .map(list -> {
                                String str = caiwuStr.split("=")[0].replace("+", ",");
                                String end = caiwuStr.split("=")[1].replace("+", ",");
                                List<SubjectBalanceRo> mapList = new ArrayList<>();
                                BigDecimal total = new BigDecimal(0);
                                BigDecimal total2 = new BigDecimal(0);
                                int index = 0;
                                if (str.split(",").length < end.split(",").length) {
                                    index = end.split(",").length;
                                } else if (end.split(",").length < str.split(",").length) {
                                    index = str.split(",").length;
                                }
                                for (int i = 0; i < str.split(",").length; i++) {
                                    int finalI = i;
                                    List<SubjectInitialBalanceVo> collect = list.stream().filter(vo -> vo.getCclass().equals(str.split(",")[finalI])).collect(Collectors.toList());
                                    BigDecimal mc = collect.size() == 0 ? new BigDecimal(0) : collect.stream().map(SubjectInitialBalanceVo::getMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                                    BigDecimal md = collect.size() == 0 ? new BigDecimal(0) : collect.stream().map(SubjectInitialBalanceVo::getMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);

                                    BigDecimal yuetmp = md.subtract(mc);
                                    BigDecimal yue1 = new BigDecimal(0);
                                    yue1 = yue1.add(yuetmp);
                                    total = total.add(yue1);
                                    mapList.add(
                                            new SubjectBalanceRo().setId(1).setName(str.split(",")[i]).setMd(md.compareTo(BigDecimal.ZERO) == 0 ? "" : decimalFormat.format(md)).setMc(mc.compareTo(BigDecimal.ZERO) == 0 ? "" : decimalFormat.format(mc))
                                                    .setFlag("1").setFx(yuetmp.compareTo(BigDecimal.ZERO) == 0 ? "平" : "借").setYue(yue1.compareTo(BigDecimal.ZERO) == 0 ? "" : decimalFormat.format(yue1))
                                    );
                                }
                                if (str.split(",").length < index) {
                                    mapList.add(new SubjectBalanceRo().setId(19).setFlag("1"));
                                }

                                for (int i = 0; i < end.split(",").length; i++) {
                                    int finalI = i;
                                    List<SubjectInitialBalanceVo> collect = list.stream().filter(vo -> vo.getCclass().equals(end.split(",")[finalI])).collect(Collectors.toList());
                                    BigDecimal mc = collect.size() == 0 ? new BigDecimal(0) : collect.stream().map(SubjectInitialBalanceVo::getMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                                    BigDecimal md = collect.size() == 0 ? new BigDecimal(0) : collect.stream().map(SubjectInitialBalanceVo::getMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                                    BigDecimal ljmd = collect.size() == 0 ? new BigDecimal(0) : collect.stream().map(SubjectInitialBalanceVo::getLjMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                                    BigDecimal ljmc = collect.size() == 0 ? new BigDecimal(0) : collect.stream().map(SubjectInitialBalanceVo::getLjMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                                    BigDecimal yuetmp = mc.subtract(md);
                                    BigDecimal yue2 = new BigDecimal(0);
                                    yue2 = yue2.add(yuetmp);
                                    total2 = total2.add(yue2);
                                    mapList.add(
                                            new SubjectBalanceRo().setId(1).setName(end.split(",")[i]).setMd(md.compareTo(BigDecimal.ZERO) == 0 ? "" : decimalFormat.format(md)).setMc(mc.compareTo(BigDecimal.ZERO) == 0 ? "" : decimalFormat.format(mc))
                                                    .setFlag("2").setFx(yuetmp.compareTo(BigDecimal.ZERO) == 0 ? "平" : "贷").setYue(yue2.compareTo(BigDecimal.ZERO) == 0 ? "" : decimalFormat.format(yue2))
                                    );
                                }
                                if (end.split(",").length < index) {
                                    mapList.add(new SubjectBalanceRo().setId(19).setFlag("2"));
                                }
                                mapList.add(
                                        new SubjectBalanceRo().setId(20).setName("合计").setYue(total.compareTo(BigDecimal.ZERO) == 0 ? "" : decimalFormat.format(total)).setFlag("1").setFx(total.compareTo(BigDecimal.ZERO) == 0 ? "平" : "借")
                                );
                                mapList.add(
                                        new SubjectBalanceRo().setId(20).setName("合计").setYue(total2.compareTo(BigDecimal.ZERO) == 0 ? "" : decimalFormat.format(total2)).setFlag("2").setFx(total2.compareTo(BigDecimal.ZERO) == 0 ? "平" : "贷")
                                );

                                mapList.add(new SubjectBalanceRo().setId(21).setName(total.abs().compareTo(total2.abs()) == 0 ? "试算已平衡" : "试算不平衡").setFlag("3"));
                                mapList.sort(Comparator.comparing(SubjectBalanceRo::getId));
                                mapList.add(new SubjectBalanceRo().setCwOrys("0").setFlag("5"));
                                return mapList;
                            });

                    Mono<List<SubjectBalanceRo>> yusuanlist = accvoucherRepository.findByAccStyleCcodeMDMC(iyear + "00", iyear).collectList()
                            .map(list -> {
                                List<SubjectBalanceRo> mapList = new ArrayList<>();
                                String cwStr = caiwuStr.split("=")[0].replace("+", ",");
                                String cwEnd = caiwuStr.split("=")[1].replace("+", ",");
                                int index = 0;
                                if (cwStr.split(",").length < cwEnd.split(",").length) {
                                    index = cwEnd.split(",").length;
                                } else if (cwEnd.split(",").length < cwStr.split(",").length) {
                                    index = cwStr.split(",").length;
                                }
                                BigDecimal cwtotal = new BigDecimal(0);
                                BigDecimal cwtotal2 = new BigDecimal(0);
                                for (int i = 0; i < cwStr.split(",").length; i++) {
                                    int finalI = i;
                                    List<SubjectInitialBalanceVo> collect = list.stream().filter(vo -> vo.getCclass().equals(cwStr.split(",")[finalI])).collect(Collectors.toList());
                                    BigDecimal mc = collect.size() == 0 ? new BigDecimal(0) : collect.stream().map(SubjectInitialBalanceVo::getMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                                    BigDecimal md = collect.size() == 0 ? new BigDecimal(0) : collect.stream().map(SubjectInitialBalanceVo::getMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                                    BigDecimal ljmd = collect.size() == 0 ? list.size() == 0 ? new BigDecimal(0) : list.get(finalI).getLjMd() : collect.stream().map(SubjectInitialBalanceVo::getLjMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                                    BigDecimal ljmc = collect.size() == 0 ? list.size() == 0 ? new BigDecimal(0) : list.get(finalI).getLjMc() : collect.stream().map(SubjectInitialBalanceVo::getLjMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                                    int yuetmp = md.add(mc).compareTo(new BigDecimal(0));
                                    BigDecimal yue1 = new BigDecimal(0);
                                    yue1 = yue1.add(md).add(mc);
                                    cwtotal = cwtotal.add(yue1);

                                    mapList.add(
                                            new SubjectBalanceRo().setId(1).setName(cwStr.split(",")[i]).setMd(md.compareTo(BigDecimal.ZERO) == 0 ? "" : decimalFormat.format(md)).setMc(mc.compareTo(BigDecimal.ZERO) == 0 ? "" : decimalFormat.format(mc))
                                                    .setFlag("1").setFx(yuetmp == 0 ? "平" : "借").setYue(yue1.compareTo(BigDecimal.ZERO) == 0 ? "" : decimalFormat.format(yue1))
                                    );

                                }
                                if (cwStr.split(",").length < index) {
                                    mapList.add(new SubjectBalanceRo().setId(2).setFlag("1"));
                                }
                                mapList.add(
                                        new SubjectBalanceRo().setId(3).setName("合计").setYue(cwtotal.compareTo(BigDecimal.ZERO) == 0 ? "" : decimalFormat.format(cwtotal)).setFlag("1").setFx(cwtotal.compareTo(BigDecimal.ZERO) == 0 ? "平" : "借")
                                );


                                for (int i = 0; i < cwEnd.split(",").length; i++) {
                                    int finalI = i;
                                    List<SubjectInitialBalanceVo> collect = list.stream().filter(vo -> vo.getCclass().equals(cwEnd.split(",")[finalI])).collect(Collectors.toList());
                                    BigDecimal mc = collect.size() == 0 ? new BigDecimal(0) : collect.stream().map(SubjectInitialBalanceVo::getMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                                    BigDecimal md = collect.size() == 0 ? new BigDecimal(0) : collect.stream().map(SubjectInitialBalanceVo::getMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                                    BigDecimal ljmd = collect.size() == 0 ? list.size() == 0 ? new BigDecimal(0) : list.get(finalI).getLjMd() : collect.stream().map(SubjectInitialBalanceVo::getLjMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                                    BigDecimal ljmc = collect.size() == 0 ? list.size() == 0 ? new BigDecimal(0) : list.get(finalI).getLjMc() : collect.stream().map(SubjectInitialBalanceVo::getLjMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                                    int yuetmp = md.add(mc).compareTo(new BigDecimal(0));
                                    BigDecimal yue2 = new BigDecimal(0);
                                    yue2 = yue2.add(md).add(mc);
                                    cwtotal2 = cwtotal2.add(yue2);
                                    mapList.add(
                                            new SubjectBalanceRo().setId(4).setName(cwEnd.split(",")[i]).setMd(md.compareTo(BigDecimal.ZERO) == 0 ? "" : decimalFormat.format(md)).setMc(mc.compareTo(BigDecimal.ZERO) == 0 ? "" : decimalFormat.format(mc))
                                                    .setFlag("2").setFx(yuetmp == 0 ? "平" : "贷").setYue(yue2.compareTo(BigDecimal.ZERO) == 0 ? "" : decimalFormat.format(yue2))
                                    );
                                }
                                if (cwEnd.split(",").length < index) {
                                    mapList.add(new SubjectBalanceRo().setId(5).setFlag("2"));
                                }

                                mapList.add(
                                        new SubjectBalanceRo().setId(6).setName("合计").setYue(cwtotal2.compareTo(BigDecimal.ZERO) == 0 ? "" : decimalFormat.format(cwtotal2)).setFlag("2").setFx(cwtotal2.compareTo(BigDecimal.ZERO) == 0 ? "平" : "贷")
                                );
                                mapList.add(new SubjectBalanceRo().setId(7).setName(cwtotal.compareTo(cwtotal2) == 0 ? "财务类：试算已平衡" : "财务类：试算不平衡").setFlag("3"));
                                mapList.sort(Comparator.comparing(SubjectBalanceRo::getId));

                                //***************************************** 预算科目 ***********************************************
                                String ysStr = yusuanStr.split("=")[0].replace("+", ",");
                                String ysEnd = yusuanStr.split("=")[1].replace("+", ",");
                                BigDecimal ystotal = new BigDecimal(0);
                                BigDecimal ystotal2 = new BigDecimal(0);
                                int index2 = 0;
                                if (ysStr.split(",").length < ysEnd.split(",").length) {
                                    index2 = ysEnd.split(",").length;
                                } else if (ysEnd.split(",").length < ysStr.split(",").length) {
                                    index2 = ysStr.split(",").length;
                                }

                                for (int i = 0; i < ysStr.split(",").length; i++) {
                                    int finalI = i;
                                    List<SubjectInitialBalanceVo> collect = list.stream().filter(vo -> vo.getCclass().equals(ysStr.split(",")[finalI])).collect(Collectors.toList());
                                    BigDecimal mc = collect.size() == 0 ? new BigDecimal(0) : collect.stream().map(SubjectInitialBalanceVo::getMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                                    BigDecimal md = collect.size() == 0 ? new BigDecimal(0) : collect.stream().map(SubjectInitialBalanceVo::getMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                                    BigDecimal ljmd = collect.size() == 0 ? list.size() == 0 ? new BigDecimal(0) : list.get(finalI).getLjMd() : collect.stream().map(SubjectInitialBalanceVo::getLjMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                                    BigDecimal ljmc = collect.size() == 0 ? list.size() == 0 ? new BigDecimal(0) : list.get(finalI).getLjMc() : collect.stream().map(SubjectInitialBalanceVo::getLjMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                                    int yuetmp = md.add(mc).compareTo(new BigDecimal(0));
                                    BigDecimal yue1 = new BigDecimal(0);
                                    yue1 = yue1.add(md).add(mc);
                                    cwtotal = cwtotal.add(yue1);
                                    mapList.add(
                                            new SubjectBalanceRo().setId(8).setName(ysStr.split(",")[i]).setMd(ljmd.compareTo(BigDecimal.ZERO) == 0 ? "" : decimalFormat.format(ljmd)).setMc(ljmc.compareTo(BigDecimal.ZERO) == 0 ? "" : decimalFormat.format(ljmc))
                                                    .setFlag("11").setFx(yuetmp == 0 ? "平" : "借").setYue(yue1.compareTo(BigDecimal.ZERO) == 0 ? "" : decimalFormat.format(yue1))
                                    );
                                }
                                if (ysStr.split(",").length < index2) {
                                    mapList.add(new SubjectBalanceRo().setId(9).setFlag("11"));
                                }
                                mapList.add(
                                        new SubjectBalanceRo().setId(10).setName("合计").setYue(ystotal.compareTo(BigDecimal.ZERO) == 0 ? "" : decimalFormat.format(ystotal)).setFlag("11").setFx(ystotal.compareTo(BigDecimal.ZERO) == 0 ? "平" : "借")
                                );
                                for (int i = 0; i < ysEnd.split(",").length; i++) {
                                    int finalI = i;
                                    List<SubjectInitialBalanceVo> collect = list.stream().filter(vo -> vo.getCclass().equals(ysEnd.split(",")[finalI])).collect(Collectors.toList());
                                    BigDecimal mc = collect.size() == 0 ? new BigDecimal(0) : collect.stream().map(SubjectInitialBalanceVo::getMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                                    BigDecimal md = collect.size() == 0 ? new BigDecimal(0) : collect.stream().map(SubjectInitialBalanceVo::getMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                                    BigDecimal ljmd = collect.size() == 0 ? list.size() == 0 ? new BigDecimal(0) : list.get(finalI).getLjMd() : collect.stream().map(SubjectInitialBalanceVo::getLjMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                                    BigDecimal ljmc = collect.size() == 0 ? list.size() == 0 ? new BigDecimal(0) : list.get(finalI).getLjMc() : collect.stream().map(SubjectInitialBalanceVo::getLjMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                                    int yuetmp = md.add(mc).compareTo(new BigDecimal(0));
                                    BigDecimal yue2 = new BigDecimal(0);
                                    yue2 = yue2.add(md).add(mc);
                                    cwtotal2 = cwtotal2.add(yue2);
                                    mapList.add(
                                            new SubjectBalanceRo().setId(11).setName(ysEnd.split(",")[i]).setMd(ljmd.compareTo(BigDecimal.ZERO) == 0 ? "" : decimalFormat.format(ljmd)).setMc(ljmc.compareTo(BigDecimal.ZERO) == 0 ? "" : decimalFormat.format(ljmc))
                                                    .setFlag("22").setFx(yuetmp == 0 ? "平" : "贷").setYue(yue2.compareTo(BigDecimal.ZERO) == 0 ? "" : decimalFormat.format(yue2))
                                    );
                                }
                                if (ysEnd.split(",").length < index2) {
                                    mapList.add(new SubjectBalanceRo().setId(12).setFlag("22"));
                                }

                                mapList.add(
                                        new SubjectBalanceRo().setId(13).setName("合计").setYue(ystotal2.compareTo(BigDecimal.ZERO) == 0 ? "" : decimalFormat.format(ystotal2)).setFlag("22").setFx(ystotal2.compareTo(BigDecimal.ZERO) == 0 ? "平" : "贷")
                                );
                                mapList.add(new SubjectBalanceRo().setId(14).setName(ystotal.compareTo(ystotal2) == 0 ? "预算类：试算已平衡" : "预算类：试算不平衡").setFlag("4"));
                                mapList.sort(Comparator.comparing(SubjectBalanceRo::getId));
                                mapList.add(new SubjectBalanceRo().setCwOrys("1").setFlag("5"));
                                return mapList;
                            });
                    return flag.equals("0") ? caiwulist : yusuanlist;
                })
                .map(item -> R.ok().setResult(item));
    }

    /**
     * 新 试算平衡
     * @param map
     * @return
     */
    @PostMapping("/newSsph")
    public Mono<R> newSsph(@RequestBody Map map) {
        String iyear=map.get("iyear").toString();
        String standardSelected=map.get("standardSelected").toString();
        Map<String, String> searchMap = ((HashMap<String,  String>) map.get("searchData"));

        return accStandardRepository.findByStandardUnique(standardSelected)
        .flatMap(standardList->{
            AccStandard accStandard = standardList;
            // 判断是否预算会计科目，获取对应的计算公式
            String caiwuStr = accStandard.getCaiwu();

            Mono<Map> caiwulist = accvoucherRepository.findBySsph(iyear + "00", iyear).collectList()
            .map(list -> {
                String str = caiwuStr.split("=")[0].replace("+", ",");
                String end = caiwuStr.split("=")[1].replace("+", ",");

                List<SsphVo> resource1 = list.stream().filter(c -> Arrays.asList(str.split(",")).indexOf(c.getCclass())!=-1).collect(Collectors.toList());
                BigDecimal resource1Mc = resource1.size() == 0 ? new BigDecimal(0) : resource1.stream().map(SsphVo::getMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                BigDecimal resource1Md = resource1.size() == 0 ? new BigDecimal(0) : resource1.stream().map(SsphVo::getMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                BigDecimal resource1Total=resource1Md.add(resource1Mc);

                List<SsphVo> resource2 = list.stream().filter(c -> Arrays.asList(end.split(",")).indexOf(c.getCclass())!=-1).collect(Collectors.toList());
                BigDecimal resource2Mc = resource2.size() == 0 ? new BigDecimal(0) : resource2.stream().map(SsphVo::getMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                BigDecimal resource2Md = resource2.size() == 0 ? new BigDecimal(0) : resource2.stream().map(SsphVo::getMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                BigDecimal resource2Total=resource2Md.add(resource2Mc);
                String msg=resource1Total.compareTo(resource2Total)==0?"1":"0";

                if(StrUtil.isNotBlank(searchMap.get("searchValue"))){
                    if(searchMap.get("searchType").equals("ccode")){
                        list=list.stream().filter(a->a.getCcode().startsWith(searchMap.get("searchValue"))).collect(Collectors.toList());
                    }else if(searchMap.get("searchType").equals("ccodeName")){
                        list=list.stream().filter(a->a.getCcodeName().contains(searchMap.get("searchValue"))).collect(Collectors.toList());
                    }
                }
                Map map1=new HashMap();
                map1.put("gongshi",caiwuStr);
                map1.put("msg",msg);
                map1.put("list",list);
                return map1;
            });
            return caiwulist;
        }).map(item -> R.ok().setResult(item));
    }

    /**
     * @return reactor.core.publisher.Mono<org.springbooz.core.tool.result.R>
     * @Author myh
     * @Description 期初记账\恢复期初记账
     * @Date 13:38 2021/6/24
     * @Param [iyear]
     **/
    @PostMapping("/qcjz")
    public Mono<R> qcjz(String iyear, String ibook, String databasenum) {
        return accvoucherRepository.qcjz(iyear + "00", ibook).then(Mono.just(R.ok()));
    }

    /**
     * 查询本年是否期初记账
     **/
    @PostMapping("/qcjzsum")
    public Mono<R> qcjzsum(String iyear, String databasenum) {
        return accvoucherRepository.qcjzsum(iyear + "00", databasenum).map(item -> R.ok().setResult(item));
    }

    /**
     * @return reactor.core.publisher.Mono<org.springbooz.core.tool.result.R>
     * @Author myh
     * @Description 查询本年凭证是否记账
     * @Date 13:52 2021/6/24
     * @Param [iyear]
     **/
    @PostMapping("/pzjzibook")
    public Mono<R> pzjzibook(String iyear, String databasenum) {
        return accvoucherRepository.pzjzibook(iyear, databasenum).map(item -> R.ok().setResult(item));
    }


    /**
     * @return reactor.core.publisher.Mono<org.springbooz.core.tool.result.R>
     * @Author myh
     * @Description 清空一条数据
     * @Date 18:05 2021/6/24
     * @Param [iyear]
     **/
    @PostMapping("/emptyAll")
    public Mono<R> emptyAll(String id) {
        return accvoucherRepository.emptyAll(id).then(Mono.just(R.ok()));
    }

    @PostMapping("/findByTaskId")
    public Mono<R> findByTaskId(String id) {
        return taskRepository.findById(id).map(a -> R.ok().setResult(a));
    }

    @PostMapping("/countByTaskId")
    public Mono<R> countByTaskId(String id) {
        return taskRepository.countById(id).map(a -> R.ok().setResult(a));
    }
}

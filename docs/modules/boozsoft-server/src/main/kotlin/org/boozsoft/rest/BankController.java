package org.boozsoft.rest;

import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.SysLog;
import org.boozsoft.domain.entity.SysNotice;
import org.boozsoft.domain.entity.group.Bank;
import org.boozsoft.domain.entity.group.BankRollback;
import org.boozsoft.repo.BankRepository;
import org.boozsoft.repo.BankRollbackRepository;
import org.boozsoft.repo.GroupDistRepository;
import org.boozsoft.repo.SysNoticeRepository;
import org.boozsoft.service.BankService;
import org.boozsoft.service.CustomerService;
import org.boozsoft.service.SysLogService;
import org.springbooz.core.tool.result.R;

import org.springbooz.datasource.r2dbc.annotation.SCHEMA_TYPE;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping("/bank")
public class BankController {

    @Autowired
    BankRepository bankRepository;
    @Autowired
    BankRollbackRepository bankRollbackRepository;

    @Autowired
    SysLogService sysLogService;
    @Autowired
    GroupDistRepository groupDistRepository;
    @Autowired
    SysNoticeRepository sysNoticeRepository;
    @Autowired
    CustomerService customerService;

    @Autowired
    BankService bankService;

    /**
     * 查询  集团档案分配管控权限表
     * @return
     */
    @GetMapping("findByDatabaseUniqueCode")
    
    public Mono<R> findByDatabaseUniqueCode() {
        return groupDistRepository.findByDatabaseUniqueCodeAndTableName("abc001","bank")
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findAll")
    @ApiOperation(value = "查询列表", notes = "传入code")
    
    public Mono<R> findAll(Pageable pageable){
        return bankRepository.findAllByOrderById(pageable).collectList().map(R::page);
    }

    @GetMapping("accFindAll")
    @ApiOperation(value = "查询列表", notes = "传入code")
    
    public Mono<R> accFindAll(Pageable pageable){
        return bankRepository.findAllByOrderById(pageable).collectList().map(R::page);
    }

    @GetMapping("findByCode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    
    public Mono<R> findByCode(String bankCode){
        return bankRepository.findByBankCodeOrderById(bankCode)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping
    @ApiOperation(value = "新增或修改", notes = "传入code")
    
    public Mono save(@RequestBody Bank object){
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String year = new SimpleDateFormat("yyyy").format(new Date());
        //系统操作日志
        SysLog sysLog = new SysLog();
        sysLog.setOperationDate(time);
        sysLog.setUniqueCode("a001");
        sysLog.setUserName("test");
        sysLog.setAccId("bjxgkj-001");
        sysLog.setIyear(year);

        object.setApplyUser("test");
        object.setApplyDate(time);
        object.setSuccessState("1");

        //增加
        if (object.getId()==null || object.getId().equals("")) {
            sysLog.setOperationCont("【" + sysLog.getUserName() + "在" + time + "】增加了一条银行档案,银行编码：【" + object.getBankCode() + "】,银行名称：【" + object.getBankName() + "】");
            return bankRepository.save(object)
                    .zipWith(sysLogService.save_log(sysLog))
                    .map(o-> R.ok().setResult(o));
        }
        //修改
        sysLog.setOperationCont("【" + sysLog.getUserName() + "在" + time + "】修改了一条银行档案,银行编码：【" + object.getBankCode() + "】,银行名称：【" + object.getBankName() + "】");
        //回滚表添加记录
        Mono<BankRollback> mono1 = bankRepository.findById(object.getId())
                .map(item -> {
                    BankRollback rollback = new BankRollback();
                    BeanUtils.copyProperties(item, rollback);
                    rollback.setId(null);
                    rollback.setBiandongId(item.getId());
                    rollback.setBiandongDate(time);
                    rollback.setBiandongMethod("1");
                    rollback.setBiandongUniqueCode("a001");
                    rollback.setBiandongName("test");
                    return rollback;
                }).flatMap(bankRollbackRepository::save);
        return mono1
                .flatMap(item -> bankRepository.save(object))
                .flatMap(item -> sysLogService.save_log(sysLog))
                .map(o-> R.ok().setResult(o));
    }

    @PostMapping("accSave")
    @ApiOperation(value = "新增或修改", notes = "传入code")
    public Mono accSave(@RequestBody Bank object) {
        //新增
        if (object.getId()==null || object.getId().equals("")){
            return add(object);
        }
        return edit(object);
    }

    /**
     * 新增方法
     * @param object
     * @return
     */
    public Mono add(@RequestBody Bank object) {
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String year = new SimpleDateFormat("yyyy").format(new Date());
        object.setApplyDatabaseUniqueCode("abc001");//申请账套唯一码
        object.setApplyUser("test");//申请人
        object.setApplyDate(time);
        object.setBiandongMethod("1");
        //系统操作日志
        SysLog sysLog = new SysLog();
        sysLog.setOperationDate(time);
        sysLog.setUniqueCode("a001");
        sysLog.setUserName("test");
        sysLog.setAccId("bjxgkj-001");
        sysLog.setIyear(year);
        sysLog.setOperationCont("【" + sysLog.getUserName() + "在" + time + "】增加了一条银行档案,银行编码：【" + object.getBankCode() + "】,银行名称：【" + object.getBankName() + "】");
        //通知集团操作员
        SysNotice sysNotice = new SysNotice();
        sysNotice.setCorpUniqueCode("abc001");
        sysNotice.setUserUniqueCode("user001");
        sysNotice.setNoticeType("增加");
        sysNotice.setNoticeTime(time);
        sysNotice.setNoticeContent(sysLog.getOperationCont());
        sysNotice.setNoticeName("银行档案");
        sysNotice.setNoticeCode("bank");
        sysNotice.setReceiveRole("001");
        sysNotice.setReceiveUser("test");
        sysNotice.setNoticeState("0");
        //管控
        if (object.getIsControl().equals("1")){
            //是否自动分配
            //自动分配系统和账套同时添加
            if (object.getIsAuto().equals("1")){
                object.setSuccessState("1");
                //系统保存项目
                return bankService.sys_save(object)
                        //账套保存项目
                        .flatMap(item -> bankService.acc_save(object))
                        //增加集团档案分配表
                        .flatMap(item -> customerService.sysDistRecord_save("abc001", object.getBankCode(), "bank"))
                        //增加系统日志表
                        .flatMap(item -> sysLogService.save_log(sysLog))
                        //增加系统通知信息表
                        .flatMap(item -> sysNoticeRepository.save(sysNotice))
                        .map(item -> R.ok().setResult(item));
            }
            //不自动分配系统添加需要走审批
            object.setSuccessState("0");
            sysLog.setAccId("booznc-group");
            //系统增加项目
            return bankService.sys_save(object)
                    //增加系统日志表
                    .flatMap(item -> sysLogService.save_log(sysLog))
                    //增加系统通知信息表
                    .flatMap(item -> sysNoticeRepository.save(sysNotice))
                    .map(item -> R.ok().setResult(item));
        }
        //不管控
        object.setSuccessState("1");
        //账套增加项目
        return bankService.acc_save(object)
                //增加系统日志表
                .flatMap(item -> sysLogService.save_log(sysLog))
                .map(item -> R.ok().setResult(item));
    }

    /**
     * 修改方法
     * @param object
     * @return
     */
    
    public Mono edit(@RequestBody Bank object) {
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String year = new SimpleDateFormat("yyyy").format(new Date());
        //系统操作日志
        SysLog sysLog = new SysLog();
        sysLog.setOperationDate(time);
        sysLog.setUniqueCode("a001");
        sysLog.setUserName("test");
        sysLog.setAccId("bjxgkj-001");
        sysLog.setIyear(year);
        sysLog.setOperationCont("【" + sysLog.getUserName() + "在" + time + "】修改了一条银行档案,银行编码：【" + object.getBankCode() + "】,银行名称：【" + object.getBankName() + "】");

        //修改
        sysLog.setOperationCont("【" + sysLog.getUserName() + "在" + time + "】修改了一条银行档案,银行编码：【" + object.getBankCode() + "】,银行名称：【" + object.getBankName() + "】");
        //回滚表添加记录
        Mono<BankRollback> mono1 = bankRepository.findById(object.getId())
                .map(item -> {
                    BankRollback rollback = new BankRollback();
                    BeanUtils.copyProperties(item, rollback);
                    rollback.setId(null);
                    rollback.setBiandongId(item.getId());
                    rollback.setBiandongDate(time);
                    rollback.setBiandongMethod("1");
                    rollback.setBiandongUniqueCode("a001");
                    rollback.setBiandongName("test");
                    return rollback;
                }).flatMap(bankRollbackRepository::save);
        return mono1
                .flatMap(item -> bankRepository.save(object))
                .flatMap(item -> sysLogService.save_log(sysLog))
                .map(o-> R.ok().setResult(o));
    }

    @DeleteMapping
    @ApiOperation(value = "删除", notes = "传入code")
    
    public Mono delete(@RequestBody Bank object){
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String year = new SimpleDateFormat("yyyy").format(new Date());
        //系统操作日志
        SysLog sysLog = new SysLog();
        sysLog.setOperationDate(time);
        sysLog.setUniqueCode("a001");
        sysLog.setUserName("test");
        sysLog.setAccId("bjxgkj-001");
        sysLog.setIyear(year);
        sysLog.setOperationCont("【" + sysLog.getUserName() + "在" + time + "】删除了一条银行档案,银行编码：【" + object.getBankCode() + "】,银行名称：【" + object.getBankName() + "】");
        //回滚表添加记录
        BankRollback rollback = new BankRollback();
        BeanUtils.copyProperties(object, rollback);
        rollback.setId(null);
        rollback.setBiandongId(object.getId());
        rollback.setBiandongDate(time);
        rollback.setBiandongMethod("2");
        rollback.setBiandongUniqueCode("a001");
        rollback.setBiandongName("test");
        Mono mono1 = bankRollbackRepository.save(rollback);
        return mono1
                .defaultIfEmpty(object)
                .flatMap(item -> bankRepository.deleteById(object.getId()))
                .defaultIfEmpty(object)
                .flatMap(item -> sysLogService.save_log(sysLog))
                .then(Mono.just(R.ok()));
    }

    @DeleteMapping("accDelete")
    @ApiOperation(value = "删除", notes = "传入code")
    
    public Mono accDelete(@RequestBody Bank object){
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String year = new SimpleDateFormat("yyyy").format(new Date());
        //系统操作日志
        SysLog sysLog = new SysLog();
        sysLog.setOperationDate(time);
        sysLog.setUniqueCode("a001");
        sysLog.setUserName("test");
        sysLog.setAccId("bjxgkj-001");
        sysLog.setIyear(year);
        sysLog.setOperationCont("【" + sysLog.getUserName() + "在" + time + "】删除了一条银行档案,银行编码：【" + object.getBankCode() + "】,银行名称：【" + object.getBankName() + "】");
        //回滚表添加记录
        BankRollback rollback = new BankRollback();
        BeanUtils.copyProperties(object, rollback);
        rollback.setId(null);
        rollback.setBiandongId(object.getId());
        rollback.setBiandongDate(time);
        rollback.setBiandongMethod("2");
        rollback.setBiandongUniqueCode("a001");
        rollback.setBiandongName("test");
        Mono mono1 = bankRollbackRepository.save(rollback);
        return mono1
                .defaultIfEmpty(object)
                .flatMap(item -> bankRepository.deleteById(object.getId()))
                .defaultIfEmpty(object)
                .flatMap(item -> sysLogService.save_log(sysLog))
                .then(Mono.just(R.ok()));
    }

    @PostMapping("approve")
    @ApiOperation(value = "审批操作", notes = "传入code")
    public Mono approve(@RequestBody Bank object) {
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String year = new SimpleDateFormat("yyyy").format(new Date());
        object.setApproveDate(time);
        object.setApproveUser("admin");
        //系统操作日志
        SysLog sysLog = new SysLog();
        sysLog.setOperationDate(time);
        sysLog.setUniqueCode("admin");
        sysLog.setUserName("admin");
        sysLog.setAccId("bjxgkj-001");
        sysLog.setIyear(year);
        sysLog.setOperationCont("【" + sysLog.getUserName() + "在" + time + "】审批了一条银行档案,银行编码：【" + object.getBankCode() + "】,银行名称：【" + object.getBankName() + "】");
        //通知集团操作员
        SysNotice sysNotice = new SysNotice();
        sysNotice.setCorpUniqueCode("abc001");
        sysNotice.setUserUniqueCode("admin");
        sysNotice.setNoticeType("审批");
        sysNotice.setNoticeTime(time);
        sysNotice.setNoticeContent(sysLog.getOperationCont()+",审批意见："+object.getApproveIdea());
        sysNotice.setNoticeName("银行档案");
        sysNotice.setNoticeCode("bank");
        sysNotice.setReceiveRole("001");
        sysNotice.setReceiveUser("test");
        sysNotice.setNoticeState("0");
        //审批不通过
        if (object.getSuccessState().equals("0")){
            return sysLogService.save_log(sysLog)//增加系统通知信息表
                    .flatMap(item -> sysNoticeRepository.save(sysNotice))
                    .map(item -> R.ok().setResult(item));
        }
        //新增审批
        return bankService.sys_save(object)
                .flatMap(item -> bankService.acc_save(object))
                //增加系统日志表
                .flatMap(item -> sysLogService.save_log(sysLog))
                //增加系统通知信息表
                .flatMap(item -> sysNoticeRepository.save(sysNotice))
                .map(item -> R.ok().setResult(item));
    }

}

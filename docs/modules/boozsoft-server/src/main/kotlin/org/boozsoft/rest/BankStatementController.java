package org.boozsoft.rest;

import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.Accvoucher;
import org.boozsoft.domain.entity.SysLog;
import org.boozsoft.domain.entity.account.BankStatement;
import org.boozsoft.domain.entity.account.BankStatementRollback;
import org.boozsoft.repo.AccvoucherRepository;
import org.boozsoft.repo.BankStatementRepository;
import org.boozsoft.repo.BankStatementRollbackRepository;
import org.boozsoft.repo.SysAccountPeriodRepository;
import org.boozsoft.repo.codekemu.CodeKemuRepository;
import org.boozsoft.service.SysLogService;
import org.springbooz.core.tool.result.R;

import org.springbooz.datasource.r2dbc.annotation.SCHEMA_TYPE;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/bankStatement")
public class BankStatementController {

    @Autowired
    BankStatementRepository bankStatementRepository;
//    @Autowired
//    BankStatementRollbackRepository bankStatementRollbackRepository;

    //    @Autowired
//    SysLogService sysLogService;
    @Autowired
    CodeKemuRepository codeKemuRepository;
    @Autowired
    SysAccountPeriodRepository sysAccountPeriodRepository;
    @Autowired
    AccvoucherRepository accvoucherRepository;

    @GetMapping("findAll")
    @ApiOperation(value = "查询列表", notes = "传入code")
    
    public Mono<R> findAll(Pageable pageable){
        return bankStatementRepository.findAll().collectList().map(R::page);
    }

    @GetMapping("findByKemuCode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    
    public Mono<R> findByKemuCode(String kemuCode){
        return bankStatementRepository.findByKemuCodeOrderByStatementDate(kemuCode)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findCodeKemuByBr")
    @ApiOperation(value = "查询列表", notes = "传入code")
    
    public Mono<R> findCodeKemuByBr(){
        return codeKemuRepository.findByBbankOrderByCcodeAsc("1")
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findYearByAccount")
    @ApiOperation(value = "查询列表", notes = "传入code")
    
    public Mono<R> findYearByAccount(String accountId){
        return sysAccountPeriodRepository.findByAccountIdOrderByAccountYearDesc(accountId)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findByKemuAndDate")
    @ApiOperation(value = "查询列表", notes = "传入code")
    
    public Mono<R> findByKemuAndDate(String kemuCode,String year, String flag,Pageable pageable){
//        if (kemuCode!=null && !kemuCode.equals("")) {
            if (year != null && !year.equals("")) {
//                date1 = new SimpleDateFormat("yyyy-MM").format(new Date(Long.parseLong(date1)));
//                date2 = new SimpleDateFormat("yyyy-MM").format(new Date(Long.parseLong(date2)));
                if (flag != null && !flag.equals("")){
                    return bankStatementRepository.findByKemuCodeAndStatementDateAndFlagOrderByStatementDate(kemuCode, year, flag)
                            .collectList()
                            .map(R::page);
                }
                return bankStatementRepository.findByKemuCodeAndStatementDateOrderByStatementDate(kemuCode, year)
                        .collectList()
                        .map(R::page);
            }
        return bankStatementRepository.findByKemuCodeOrderByStatementDate(kemuCode)
                .collectList()
                .map(R::page);
//        }
//        return bankStatementRepository.findAll()
//                .collectList()
//                .map(R::page);
    }

    @GetMapping("findByKemuAndQichu")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByKemuAndQichu(String kemuCode,String qichu){
        return bankStatementRepository.findByKemuCodeAndDateOrderByStatementDate(kemuCode,qichu)
                .collectList()
                .map(R::page);
    }

    @PostMapping
    @ApiOperation(value = "新增或修改", notes = "传入code")
    
    public Mono save(@RequestBody BankStatement object){
        if (object.getStatementDate()!=null && !object.getStatementDate().equals("")){
            object.setStatementDate(object.getStatementDate().substring(0,10));
        }
        //增加
        if (object.getId()==null || object.getId().equals("")) {
            return bankStatementRepository.save(object)
                    .map(o-> R.ok().setResult(o));
        }
        return bankStatementRepository.save(object)
                .map(o -> R.ok().setResult(o));
    }

    @DeleteMapping("del")
    @ApiOperation(value = "删除", notes = "传入code")
    
    public Mono delete(@RequestBody BankStatement object){
        return bankStatementRepository.deleteById(object.getId())
                .then(Mono.just(R.ok()));
    }

    @PostMapping("excel")
    @ApiOperation(value = "前台解析excel,后台直接保存集合", notes = "传入code")
    @Transactional
    public Mono excel(@RequestBody List<BankStatement> object) {
        return Flux.fromIterable(object).map(item -> {
                    if (item.getStatementDate() != null && !item.getStatementDate().equals("")) {
                        item.setStatementDate(item.getStatementDate().substring(0, 10));
                    }
                    return item;
                })
                .flatMap(item -> bankStatementRepository.saveAll(object))
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @DeleteMapping("delList")
    @ApiOperation(value = "批量删除", notes = "传入code")
    
    public Mono deleteList(@RequestBody List<BankStatement> object){
        return bankStatementRepository.deleteAll(object)
                .then(Mono.just(R.ok()));
    }

    @GetMapping("findAccvoucherByIperiod")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAccvoucherByIperiod(String ccode, String iyear, String iperiod){
        return accvoucherRepository.findByCodeAndYearAndPeriod(ccode, iyear, iperiod)
                .collectList()
                .map(R::page);
    }

    @GetMapping("findAccvoucherByIperiodList")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAccvoucherByIperiodList(String ccode, String iyear, String iperiod1, String iperiod2){
        return accvoucherRepository.findByCodeAndYearAndPeriodList(ccode, iyear, iperiod1, iperiod2)
                .collectList()
                .map(R::page);
    }

    @PostMapping("excelAccvoucher")
    @ApiOperation(value = "前台解析excel,后台直接保存集合", notes = "传入code")
    @Transactional
    public Mono excelAccvoucher(@RequestBody List<Accvoucher> object){
        return accvoucherRepository.saveAll(object)
                .collectList()
                .map(o-> R.ok().setResult(o));
    }

    @PostMapping("saveAccvoucher")
    @ApiOperation(value = "保存", notes = "传入code")
    public Mono saveAccvoucher(@RequestBody Accvoucher object){
        if (object.getDbillDate()!=null && !object.getDbillDate().equals("")){
            object.setDbillDate(object.getDbillDate().substring(0,10));
        }
        if (object.getPjDate()!=null && !object.getPjDate().equals("")){
            object.setPjDate(object.getPjDate().substring(0,10));
        }
        return accvoucherRepository.save(object)
                .map(o -> R.ok().setResult(o));
    }

    @DeleteMapping("deleteAccountList")
    @ApiOperation(value = "批量删除", notes = "传入code")
    public Mono deleteAccountList(@RequestBody List<Accvoucher> object) {
        return accvoucherRepository.deleteAll(object)
                .then(Mono.just(R.ok()));
    }

    @GetMapping("findAccountByKemuAndDate")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAccountByKemuAndDate(String kemuCode, String year, String flag) {
        return accvoucherRepository.findByCcodeAndIyearOrderByDbillDate(kemuCode, year)
                .collectList()
                .map(list -> {
                    if (flag.equals("0")) {
                        return list.stream().filter(item -> item.getStatementNum() == null || item.getStatementNum().equals(""))
                                .collect(Collectors.toList());
                    }
                    if (flag.equals("1")) {
                        return list.stream().filter(item -> item.getStatementNum() != null && !item.getStatementNum().equals(""))
                                .collect(Collectors.toList());
                    }
                    return list;
                })
                .map(R::page);
    }

    @PostMapping("saveDuizhang")
    @ApiOperation(value = "保存", notes = "传入code")
    public Mono saveDuizhang(@RequestBody List<BankStatement> bankStatement, @RequestBody List<Accvoucher> accvoucher) {
        return Flux.fromIterable(bankStatement).map(item -> {
                    item.setFlag("1");
                    return item;
                })
                .flatMap(item -> bankStatementRepository.saveAll(bankStatement))
                .flatMap(item -> accvoucherRepository.saveAll(accvoucher))
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

}

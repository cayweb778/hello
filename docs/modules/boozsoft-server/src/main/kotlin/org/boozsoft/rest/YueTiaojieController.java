package org.boozsoft.rest;

import io.swagger.annotations.ApiOperation;
import org.boozsoft.repo.AccvoucherRepository;
import org.boozsoft.repo.BankStatementRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/yueTiaojie")
public class YueTiaojieController {

    @Autowired
    AccvoucherRepository accvoucherRepository;
    @Autowired
    BankStatementRepository bankStatementRepository;

    /**
     * 根据截止日期查询银行科目余额表
     * @param endDate   截止日期
     * @return
     */
    @GetMapping("findAccountByYuetiaojie")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAccountByYuetiaojie(String endDate){
        String year = endDate.substring(0, 4);
        endDate = endDate.substring(0,10);
        return accvoucherRepository.findAccountByYuetiaojie(year+"-01-01",endDate,year+"00")
                .collectList()
                .map(o->R.ok().setResult(o));
    }

    /**
     * 根据截止日期查询银行科目余额表
     * @param endDate   截止日期
     * @return
     */
    @GetMapping("findBankStatementByYuetiaojie")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findBankStatementByYuetiaojie(String ccode,String endDate){
        String year = endDate.substring(0, 4);
        endDate = endDate.substring(0,10);
        return bankStatementRepository.findByKemuCodeAndDateOrderById(ccode,year+"-01-01",endDate,year+"00")
                .collectList()
                .map(o->R.ok().setResult(o));
    }

}

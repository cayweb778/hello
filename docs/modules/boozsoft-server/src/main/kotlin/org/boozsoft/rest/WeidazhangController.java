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

import java.util.stream.Collectors;

@RestController
@RequestMapping("/weidazhang")
public class WeidazhangController {

    @Autowired
    BankStatementRepository bankStatementRepository;
    @Autowired
    AccvoucherRepository accvoucherRepository;

    @GetMapping("findByKemuAndDate")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByKemuAndDate(String kemuCode, String year, String flag, String endDate){
        return bankStatementRepository.findByKemuAndDate(kemuCode, year, flag, endDate)
                .collectList()
                .map(R::page);
    }

    @GetMapping("findAccountByKemuAndDate")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAccountByKemuAndDate(String kemuCode, String year, String flag, String endDate){
        return accvoucherRepository.findByKemuAndDate(kemuCode, year, endDate)
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

    @GetMapping("findBankStatementByWeidazhang")
    @ApiOperation(value = "根据科目年度查询银行对账单未达账项", notes = "传入code")
    public Mono<R> findBankStatementByWeidazhang(String kemuCode, String year, String flag){
        return bankStatementRepository.findByKemuCodeAndStatementDateAndFlagOrderByStatementDate(kemuCode, year, flag)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findByKemuNotQichuAndDate")
    @ApiOperation(value = "根据科目年度查询银行对账单未达账项", notes = "传入code")
    public Mono<R> findByKemuNotQichuAndDate(String kemuCode, String year, String flag, String endDate){
        return bankStatementRepository.findQichuByKemuAndDate(kemuCode, year, flag, endDate)
                .collectList()
                .map(list -> {
                    return list.stream().filter(item -> item.getQichu()==null || item.getQichu().equals(""));
                })
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findAccountByWeidazhang")
    @ApiOperation(value = "根据科目年度查询凭证对账单未达账项", notes = "传入code")
    public Mono<R> findAccountByWeidazhang(String kemuCode, String year, String flag){
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
                .map(o -> R.ok().setResult(o));
    }

}

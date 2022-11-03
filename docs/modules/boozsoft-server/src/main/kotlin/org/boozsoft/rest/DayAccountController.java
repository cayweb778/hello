package org.boozsoft.rest;

import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.ApiOperation;
import org.boozsoft.repo.AccvoucherRepository;
import org.boozsoft.repo.BankStatementRepository;
import org.boozsoft.repo.codekemu.CodeKemuRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dayAccount")
public class DayAccountController {

    @Autowired
    AccvoucherRepository accvoucherRepository;
    @Autowired
    CodeKemuRepository codeKemuRepository;

    /**
     * 根据条件查询现金日记账期初
     *
     * @param iyperiod 期初年度区间
     * @return
     */
    @GetMapping("findByDayAccountAndQc")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByDayAccountAndQc(String iyperiod, String ccode) {
        return accvoucherRepository.findByDayAccountAndQc(iyperiod)
                .collectList()
                .map(list -> {
                    if (StrUtil.isNotBlank(ccode)) {
                        return list.stream().filter(item -> item.getCcode().contains(ccode)).collect(Collectors.toList());
                    }
                    return list;
                })
                .map(o -> R.ok().setResult(o));
    }

    /**
     * 根据条件查询现金日记账
     * @return
     */
    @PostMapping("findByDayAccount")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByDayAccount(@RequestBody Map map) {
        //开始期间
        String periodStart = map.get("periodStart").toString().replace("-", "");
        //结束期间
        String periodEnd = map.get("periodEnd").toString().replace("-", "");
        //科目编码
        String accountNum = map.get("accountNum").toString();
        //摘要
        String summary = map.get("summary").toString();
        //包含未记账
        String unaccounted = map.get("unaccounted").toString();
        if (StrUtil.isNotBlank(periodStart)) {
            return accvoucherRepository.findByDayAccount(periodStart, periodEnd)
                    .collectList()
                    .map(list -> {
                        if (StrUtil.isNotBlank(accountNum)) {
                            return list.stream().filter(item -> item.getCcode().contains(accountNum)).collect(Collectors.toList());
                        }
                        if (StrUtil.isNotBlank(summary)) {
                            return list.stream().filter(item -> item.getCcode().contains(summary)).collect(Collectors.toList());
                        }
                        if (unaccounted.equals("0") || unaccounted.equals("false")) {
                            return list.stream().filter(item -> item.getIbook().contains("1")).collect(Collectors.toList());
                        }
                        return list;
                    })
                    .map(o -> R.ok().setResult(o));
        }
        //开始日期
        String dateStart = map.get("dateStart").toString();
        //结束日期
        String dateEnd = map.get("dateEnd").toString();
        return accvoucherRepository.findByDayAccountAndDate(dateStart, dateEnd)
                .collectList()
                .map(list -> {
                    if (StrUtil.isNotBlank(accountNum)) {
                        return list.stream().filter(item -> item.getCcode().contains(accountNum)).collect(Collectors.toList());
                    }
                    if (StrUtil.isNotBlank(summary)) {
                        return list.stream().filter(item -> item.getCcode().contains(summary)).collect(Collectors.toList());
                    }
                    if (unaccounted.equals("0") || unaccounted.equals("false")) {
                        return list.stream().filter(item -> item.getIbook().contains("1")).collect(Collectors.toList());
                    }
                    return list;
                })
                .map(o -> R.ok().setResult(o));
    }

    /**
     * 根据条件银行日记账期初
     *
     * @param iyperiod 期初年度区间
     * @return
     */
    @GetMapping("findByBankAccountAndQc")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByBankAccountAndQc(String iyperiod, String ccode) {
        return accvoucherRepository.findByBankAccountAndQc(iyperiod)
                .collectList()
                .map(list -> {
                    if (StrUtil.isNotBlank(ccode)) {
                        return list.stream().filter(item -> item.getCcode().contains(ccode)).collect(Collectors.toList());
                    }
                    return list;
                })
                .map(o -> R.ok().setResult(o));
    }

    /**
     * 根据条件查询银行日记账
     * @return
     */
    @PostMapping("findByBankAccount")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByBankAccount(@RequestBody Map map) {
        //开始期间
        String periodStart = map.get("periodStart").toString().replace("-", "");
        //结束期间
        String periodEnd = map.get("periodEnd").toString().replace("-", "");
        //科目编码
        String accountNum = map.get("accountNum").toString();
        //摘要
        String summary = map.get("summary").toString();
        //包含未记账
        String unaccounted = map.get("unaccounted").toString();
        if (StrUtil.isNotBlank(periodStart)) {
            return accvoucherRepository.findByBankAccount(periodStart, periodEnd)
                    .collectList()
                    .map(list -> {
                        if (StrUtil.isNotBlank(accountNum)) {
                            return list.stream().filter(item -> item.getCcode().contains(accountNum)).collect(Collectors.toList());
                        }
                        if (StrUtil.isNotBlank(summary)) {
                            return list.stream().filter(item -> item.getCcode().contains(summary)).collect(Collectors.toList());
                        }
                        if (unaccounted.equals("0") || unaccounted.equals("false")) {
                            return list.stream().filter(item -> item.getIbook().contains("1")).collect(Collectors.toList());
                        }
                        return list;
                    })
                    .map(o -> R.ok().setResult(o));
        }
        //开始日期
        String dateStart = map.get("dateStart").toString();
        //结束日期
        String dateEnd = map.get("dateEnd").toString();
        return accvoucherRepository.findByBankAccountAndDate(dateStart, dateEnd)
                .collectList()
                .map(list -> {
                    if (StrUtil.isNotBlank(accountNum)) {
                        return list.stream().filter(item -> item.getCcode().contains(accountNum)).collect(Collectors.toList());
                    }
                    if (StrUtil.isNotBlank(summary)) {
                        return list.stream().filter(item -> item.getCcode().contains(summary)).collect(Collectors.toList());
                    }
                    if (unaccounted.equals("0") || unaccounted.equals("false")) {
                        return list.stream().filter(item -> item.getIbook().contains("1")).collect(Collectors.toList());
                    }
                    return list;
                })
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findCodeByDayBook")
    public Mono<R> findCodeByDayBook(){
        return codeKemuRepository.findByDayBook()
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    /**
     * 根据条件银行日记账期初
     *
     * @param iyperiod 期初年度区间
     * @return
     */
    @GetMapping("findByDayBookAccountAndQc")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByDayBookAccountAndQc(String iyperiod, String ccode) {
        return accvoucherRepository.findByDayBookAccountAndQc(iyperiod,ccode)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    /**
     * 根据条件查询银行日记账
     * @return
     */
    @PostMapping("findByDayBookAccount")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByDayBookAccount(@RequestBody Map map) {
        //开始期间
        String periodStart = map.get("periodStart").toString().replace("-", "");
        //结束期间
        String periodEnd = map.get("periodEnd").toString().replace("-", "");
        //科目编码
        String accountNum = map.get("accountNum").toString();
        //摘要
        String summary = map.get("summary").toString();
        //包含未记账
        String unaccounted = map.get("unaccounted").toString();
        if (StrUtil.isNotBlank(periodStart)) {
            return accvoucherRepository.findByDayBookAccount(periodStart, periodEnd,accountNum)
                    .collectList()
                    .map(list -> {
                        if (StrUtil.isNotBlank(accountNum)) {
                            return list.stream().filter(item -> item.getCcode().contains(accountNum)).collect(Collectors.toList());
                        }
                        if (StrUtil.isNotBlank(summary)) {
                            return list.stream().filter(item -> item.getCcode().contains(summary)).collect(Collectors.toList());
                        }
                        if (unaccounted.equals("0") || unaccounted.equals("false")) {
                            return list.stream().filter(item -> item.getIbook().contains("1")).collect(Collectors.toList());
                        }
                        return list;
                    })
                    .map(o -> R.ok().setResult(o));
        }
        //开始日期
        String dateStart = map.get("dateStart").toString();
        //结束日期
        String dateEnd = map.get("dateEnd").toString();
        return accvoucherRepository.findByDayBookAccountAndDate(dateStart, dateEnd,accountNum)
                .collectList()
                .map(list -> {
                    if (StrUtil.isNotBlank(accountNum)) {
                        return list.stream().filter(item -> item.getCcode().contains(accountNum)).collect(Collectors.toList());
                    }
                    if (StrUtil.isNotBlank(summary)) {
                        return list.stream().filter(item -> item.getCcode().contains(summary)).collect(Collectors.toList());
                    }
                    if (unaccounted.equals("0") || unaccounted.equals("false")) {
                        return list.stream().filter(item -> item.getIbook().contains("1")).collect(Collectors.toList());
                    }
                    return list;
                })
                .map(o -> R.ok().setResult(o));
    }

}

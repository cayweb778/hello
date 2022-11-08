package org.boozsoft.rest;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.SysPeriod;
import org.boozsoft.domain.entity.account.Iperiod;
import org.boozsoft.domain.entity.group.GroupSysAccAuth;
import org.boozsoft.domain.entity.group.GroupSysAccount;
import org.boozsoft.domain.entity.group.GroupUserOperatAuthZt;
import org.boozsoft.domain.entity.group.SysAccountPeriod;
import org.boozsoft.repo.*;
import org.boozsoft.service.DatabaseInitService;
import org.boozsoft.service.SysLogService;
import org.boozsoft.service.impl.DatabaseInitServiceImpl;
import org.springbooz.core.tool.result.R;

import org.springbooz.datasource.r2dbc.annotation.SCHEMA_TYPE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/iperiod")
public class IperiodController {

    @Autowired
    IperiodRepository iperiodRepository;

    @Autowired
    SysPeriodRepository sysPeriodRepository;

    @Autowired
    AccvoucherRepository accvoucherRepository;

    @Autowired
    SysAccountPeriodRepository accountPeriodRepository;

    @Autowired
    SysAccAuthRepository sysAccAuthRepository;

    @Autowired
    SysLogService sysLogService;

    @GetMapping("findByIyear")
    @ApiOperation(value = "查询列表", notes = "传入code")

    public Mono<R> findByIyear(String iyear) {
        return iperiodRepository.findByIyearAndBeiyong1NullOrderById(iyear).collectList().map(o -> R.ok().setResult(o));
    }

    /********************* Mr. Ye *******************/


    @GetMapping("findAllByIyear")
    @ApiOperation(value = "查询指定年份区间与季度", notes = "传入code")
    public Mono<R> findAllByIyear(String accId, String iyear) {
        Mono<List<SysPeriod>> qjMono = sysPeriodRepository.findAllByAccountIdAndIyearAndBeiyong1NullOrderByIyearAscDateStartAsc(accId, iyear.split("--")[0]).collectList().cache();
        Mono<List<SysPeriod>> jdMono = sysPeriodRepository.findAllByAccountIdAndIyearAndBeiyong1OrderByIyearAscIperiodNumAsc(accId, iyear.split("--")[1], "1").collectList().cache();
        return Mono.zip(qjMono, jdMono).map(o -> R.ok().setResult(o));
    }

    @GetMapping("findQjByIyear")
    @ApiOperation(value = "查询指定年份区间与季度", notes = "传入code")
    public Mono<R> findQjByIyear(String accId, String iyear) {
        return sysPeriodRepository.findAllByAccountIdAndIyearAndBeiyong1NullOrderByIyearAscDateStartAsc(accId, iyear).collectList().cache().map(o -> R.ok().setResult(o));
    }

    @PostMapping("saveSystemQuarterly")
    @ApiOperation(value = "添加账套区间", notes = "传入code")
    public Mono<R> saveSystemQuarterly(@RequestBody List<SysPeriod> iperiodFlux) {
        return Mono.just(iperiodFlux.get(0)).flatMap(entity -> sysPeriodRepository.findAllByAccountIdAndIyearAndBeiyong1OrderByIyearAscIperiodNumAsc(entity.getAccountId(), entity.getIyear(), "1").collectList().flatMap(sysPeriodRepository::deleteAll).thenReturn(iperiodFlux)).doOnNext(list -> {
            for (SysPeriod iperiod : list) {
                iperiod.setId(null);
            }
        }).flatMap(list -> sysPeriodRepository.saveAll(list).collectList().map(o -> R.ok().setResult(o)));
    }

    @PostMapping("saveQuarterly")
    @ApiOperation(value = "添加账套区间", notes = "传入code")

    public Mono<R> saveQuarterly(@RequestBody List<Iperiod> iperiodFlux) {
        return Mono.just(iperiodFlux.get(0).getIyear()).flatMap(thisYaer -> iperiodRepository.findAllByIyearAndBeiyong1OrderById(thisYaer).collectList().flatMap(iperiodRepository::deleteAll).thenReturn(iperiodFlux)).doOnNext(list -> {
            for (Iperiod iperiod : list) {
                iperiod.setId(null);
            }
        }).flatMap(list -> iperiodRepository.saveAll(list).collectList().map(o -> R.ok().setResult(o)));
    }

    @PostMapping("modifyPeriod")
    @ApiOperation(value = "修改账套区间", notes = "传入code")
    public Mono<R> modifyPeriod(@RequestBody Map map) {
        String accId = map.get("accId").toString();
        List<SysPeriod> modifyModel = JSON.parseArray(map.get("modifyModel").toString(), SysPeriod.class);
        return sysPeriodRepository.findAllByAccountId(accId).collectList().map(list -> {
            List<SysPeriod> saveList = new ArrayList<>();
            for (SysPeriod iperiod : modifyModel) {
                for (SysPeriod sysPeriod : list) {
                    if (sysPeriod.getPeriod().equals(iperiod.getPeriod())) {
                        sysPeriod.setDateStart(iperiod.getDateStart());
                        sysPeriod.setDateEnd(iperiod.getDateEnd());
                        saveList.add(sysPeriod);
                        break;
                    }
                }
            }
            return saveList;
        }).flatMap(list -> sysPeriodRepository.saveAll(list).collectList().map(o -> R.ok().setResult(o)));
    }

    @PostMapping("modifyPeriod2")
    @ApiOperation(value = "修改账套区间", notes = "传入code")
    public Mono<R> modifyPeriod2(@RequestBody Map map) {
        String accId = map.get("accId").toString();
        String periodNum = map.get("periodNum").toString();
        String startPeriod = map.get("startPeriod").toString();
        String year = startPeriod.substring(0, 4);
        List<SysPeriod> modifyModel = JSON.parseArray(map.get("modifyModel").toString(), SysPeriod.class);
        return sysPeriodRepository.findAllByAccountIdAndIyear(accId, year).collectList().flatMap(list -> {
            if (Integer.parseInt(periodNum) != list.size()) {
                return sysPeriodRepository.deleteAll(list).thenReturn(modifyModel).map(list2 -> {
                    for (SysPeriod sysPeriod : list2) {
                        sysPeriod.setAccountId(accId);
                        sysPeriod.setIyear(year);
                        if (startPeriod.equals(sysPeriod.getPeriod())) {
                            sysPeriod.setEnablePeriod("1");
                        } else {
                            sysPeriod.setEnablePeriod("0");
                        }
                    }
                    return list2;
                });
            } else {
                List<SysPeriod> saveList = new ArrayList<>();
                for (SysPeriod iperiod : modifyModel) {
                    for (SysPeriod sysPeriod : list) {
                        if (sysPeriod.getPeriod().equals(iperiod.getPeriod())) {
                            sysPeriod.setDateStart(iperiod.getDateStart());
                            sysPeriod.setDateEnd(iperiod.getDateEnd());
                            if (startPeriod.equals(sysPeriod.getPeriod())) {
                                sysPeriod.setEnablePeriod("1");
                            } else {
                                sysPeriod.setEnablePeriod("0");
                            }
                            saveList.add(sysPeriod);
                            break;
                        }
                    }
                }
                return Mono.just(saveList);
            }
        }).flatMap(list -> sysPeriodRepository.saveAll(list).collectList().map(o -> R.ok().setResult(o)));
    }

    @PostMapping("initNextPeriod")
    @ApiOperation(value = "初始化下一年度并授权", notes = "传入code")
    public Mono<R> initNextPeriod(@RequestBody Map map) {
        if (map.size() != 4) return Mono.just(R.error());
        String accId = map.get("accId").toString();
        String year = map.get("year").toString();
        String coCode = map.get("coCode").toString();
        String periodNum = map.get("periodNum").toString();
        String nextYear = (Integer.parseInt(year) + 1) + "";
        Mono<String> apMono = accountPeriodRepository.findAllByAccountCoCodeOrderByAccountYearDesc(coCode).collectList().flatMap(dList -> {
            if (dList.size() > 0 && dList.stream().filter(it -> it.getAccountYear().equals(nextYear)).collect(Collectors.toList()).size() == 0) { // 不存在
                SysAccountPeriod period = dList.get(0);
                period.setId(null);
                period.setAccountYear(nextYear);
                return accountPeriodRepository.save(period).thenReturn("");
            }
            return Mono.just("");
        });
        return sysPeriodRepository.findAllByAccountIdAndIyear(accId, nextYear).collectList().flatMap(list -> (list.size() > 0) ? Mono.just(R.ok()) : sysPeriodRepository.saveAll(dataCollectionDuringGeneration(periodNum, nextYear, accId)).collectList().flatMap(list2 -> apMono.thenReturn(list2)).map(o -> R.ok().setResult(o)));
    }

    @GetMapping("findPeriodInfo")
    @ApiOperation(value = "查询指定年份区间与季度", notes = "传入code")
    public Mono<R> findPeriodInfo(String accId) {
        return sysPeriodRepository.findAllByAccountId(accId).collectList().map(list -> {
            Map<String, Object> map = new HashMap<>();
            if (list.size() > 0) {
                List<SysPeriod> periods = list.stream().filter(item -> item.getEnablePeriod().equals("1")).collect(Collectors.toList());
                SysPeriod period = periods.get(periods.size() - 1);
                map.put("startPeriod", period.getPeriod());
                map.put("periodNum", list.get(list.size() - 1).getIperiodNum());
                String iyear = list.get(list.size() - 1).getIyear();
                map.put("maxYear", iyear);
                List<SysPeriod> sysPeriods = list.stream().filter(item -> item.getIyear().equals(iyear)).collect(Collectors.toList());
                map.put("yearStartDate", sysPeriods.get(0).getIyear() + "-" + sysPeriods.get(0).getDateStart());
                map.put("yearEndDate", sysPeriods.get(sysPeriods.size() - 1).getIyear() + "-" + sysPeriods.get(sysPeriods.size() - 1).getDateEnd());
                map.put("yearList", assemblyYearList(list));
            }
            return map;
        }).map(o -> R.ok().setResult(o));
    }

    @GetMapping("findPeriodInfo2")
    @ApiOperation(value = "查询指定年份区间与季度", notes = "传入code")
    public Mono<R> findPeriodInfo2(String accId, String year) {
        return sysPeriodRepository.findAllByAccountIdAndIyear(accId, year).collectList().map(list -> {
            Map<String, Object> map = new HashMap<>();
            if (list.size() > 0) {
                List<SysPeriod> periods = list.stream().filter(item -> item.getEnablePeriod().equals("1")).collect(Collectors.toList());
                SysPeriod period = periods.get(periods.size() - 1);
                map.put("startPeriod", period.getPeriod());
                map.put("periodNum", list.get(list.size() - 1).getIperiodNum());
                String iyear = list.get(list.size() - 1).getIyear();
                map.put("maxYear", iyear);
                List<SysPeriod> sysPeriods = list.stream().filter(item -> item.getIyear().equals(iyear)).collect(Collectors.toList());
                map.put("yearStartDate", sysPeriods.get(0).getIyear() + "-" + sysPeriods.get(0).getDateStart());
                map.put("yearEndDate", sysPeriods.get(sysPeriods.size() - 1).getIyear() + "-" + sysPeriods.get(sysPeriods.size() - 1).getDateEnd());
                map.put("yearList", assemblyYearList(list));
            }
            return map;
        }).map(o -> R.ok().setResult(o));
    }


    @PostMapping("checkPeriod")
    @ApiOperation(value = "查询指定年份区间与季度", notes = "传入code")
    public Mono<R> checkPeriod(@RequestBody Map map) {
        String accId = map.get("accId").toString();
        String type = map.get("type").toString();
        String year = map.get("year").toString();
        Mono<Long> one = sysPeriodRepository.countAllByAccountIdAndIyear(accId, year);
        Mono<Long> two = accvoucherRepository.countAllByIyearAndTenantId(year, accId);
        return (type.equals("save") ? one : two).map(o -> R.ok().setResult(o));
    }

    @PostMapping("savePeriod")
    @Transactional
    @ApiOperation(value = "查询指定年份区间与季度", notes = "传入code")
    public Mono<R> savePeriod(@RequestBody Map map) {
        String accId = map.get("accId").toString();
        String mode = map.get("mode").toString();
        String ccodeLevel = map.get("ccodeLevel").toString();
        String year = map.get("year").toString();
        String periodNum = map.get("periodNum").toString();
        String startPeriod = map.get("startPeriod").toString();
        String superStr = map.get("super").toString();
        GroupSysAccount sysAccount = new GroupSysAccount();
        sysAccount.setAccId(accId);
        sysAccount.setYearStartDate(year + "-01-01");
        sysAccount.setStartPeriod(startPeriod);
        sysAccount.setPeriodNum(periodNum);
        return new DatabaseInitServiceImpl().dataCollectionDuringGeneration(sysAccount).collectList().flatMap(list -> sysPeriodRepository.saveAll(list).collectList()).flatMap(list -> {
            if (list.size() > 0) {
                Mono<SysAccountPeriod> two = accountPeriodRepository.save(new SysAccountPeriod(accId, year, mode, ccodeLevel, ccodeLevel.replaceAll("-", "").length()));
                Mono<GroupSysAccAuth> three = sysAccAuthRepository.save(new GroupSysAccAuth().setAccId(accId).setUserNum(superStr).setIyear(year).setAccvocherType("1").setCcodeAll("1"));
                return Mono.zip(two, three).flatMap(t -> Mono.just(R.ok().setResult(list.size())));
            }
            return Mono.just(R.ok().setResult(list.size()));
        });
    }

    private List<String> assemblyYearList(List<SysPeriod> list) {
        HashSet<String> set = new HashSet<>();
        for (SysPeriod sysPseriod : list) {
            set.add(sysPseriod.getIyear());
        }
        return new ArrayList<>(set);
    }

    @GetMapping("findPeriodByYaer")
    @ApiOperation(value = "查询启用期间", notes = "传入code")
    public Mono<R> findPeriodByYaer(String accId, String year) {
        return sysPeriodRepository.findFirstByAccountIdAndIyearAndEnablePeriod(accId, year, "1").map(o -> R.ok().setResult(o.getPeriod())).defaultIfEmpty(R.ok().setResult(""));
    }


    @PostMapping("close")
    @ApiOperation(value = "指定年月结账与取消结账", notes = "传入code")
    public Mono<R> close(@RequestBody Map map) {
        if (map.keySet().size() < 2) return Mono.just(R.error());
        String accId = map.get("accId").toString();
        String id = map.get("id").toString();
        return sysPeriodRepository.findFirstByAccountIdAndId(accId, id).flatMap(period -> {
            if (StrUtil.isBlank(period.getGl())) {
                String operator = map.get("operator").toString();
                period.setGl("1").setGlUser(operator).setGlTime(DateUtil.now());
            } else {
                period.setGl(null).setGlUser(null).setGlTime(null);
            }
            return sysPeriodRepository.save(period).map(o -> R.ok(o));
        });
    }

    @PostMapping("closeMonth")
    @ApiOperation(value = "获取指定年度对象结转月", notes = "传入code")
    public Mono<R> closeMonth(@RequestBody Map map) {
        if (map.keySet().size() < 2) return Mono.just(R.error());
        String accId = map.get("accId").toString();
        String year = map.get("year").toString();
        return sysPeriodRepository.findFirstByAccountIdAndIyearAndGlNotOrAccountIdAndIyearAndGlIsNullOrderByIperiodNumAsc(accId, year, "1", accId, year).map(o -> R.ok(o));
    }

    @PostMapping("closeList")
    @ApiOperation(value = "获取指定年度对象结转月", notes = "传入code")
    public Mono<R> closeList(@RequestBody Map map) {
        if (map.keySet().size() < 2) return Mono.just(R.error());
        String accId = map.get("accId").toString();
        String year = map.get("year").toString();
        return sysPeriodRepository.findAllByAccountIdAndIyearOrderByDateStartAsc(accId, year).collectList().map(o -> R.ok(o));
    }

    public List<SysPeriod> dataCollectionDuringGeneration(String periodNum, String year, String accId) {
        String startDate = year + "-01-01";
        String periodMonth = "";
        int number = Integer.parseInt(periodNum);
        List<SysPeriod> list = new ArrayList<>();
        int index = 0;
        for (int i = 1; i <= number; i++) {
            String num = i > 9 ? "" + i : "0" + i;
            SysPeriod period = new SysPeriod();
            period.setAccountId(accId);
            period.setIyear(year);
            period.setPeriod(year + num);
            period.setIperiodNum(num);
            period.setDateStart(DateUtil.parseDate(startDate).toString("MM-dd"));
            if (number != 12 && Integer.parseInt(num) >= 12) {//12~16  13 -12 =1
                int offset = 12 - number + index;
                if (Integer.parseInt(num) == 12) {
                    period.setDateEnd(DateUtil.offsetDay(DateUtil.endOfMonth(DateUtil.parseDate(startDate)), offset).toString("MM-dd"));
                } else {
                    period.setDateStart(DateUtil.offsetDay(DateUtil.endOfMonth(DateUtil.parseDate(startDate)), offset).toString("MM-dd"));
                    period.setDateEnd(DateUtil.offsetDay(DateUtil.endOfMonth(DateUtil.parseDate(startDate)), offset).toString("MM-dd"));
                }
                index++;
            } else {
                period.setDateEnd(DateUtil.endOfMonth(DateUtil.parseDate(startDate)).toString("MM-dd"));
                startDate = DateUtil.offsetMonth(DateUtil.parseDate(startDate), 1).toString("yyyy-MM-dd");
                year = startDate.substring(0, 4);
            }
            if (period.getPeriod().equals(periodMonth)) {
                period.setEnablePeriod("1");
            } else {
                period.setEnablePeriod("0");
            }
            list.add(period);

        }
        return list;
    }
    /********************* Mr. Ye *******************/

    @PostMapping("countAllByAccountIdAndIyear")
    public Mono<R> countAllByAccountIdAndIyear(String accId,String iyear) {
        return sysPeriodRepository.countAllByAccountIdAndIyear(accId, iyear).map(o -> R.ok(o));
    }
}

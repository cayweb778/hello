package org.boozsoft.rest.origin;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.SysPeriod;
import org.boozsoft.domain.entity.group.GroupSysAccount;
import org.boozsoft.domain.entity.group.SysAccountPeriod;
import org.boozsoft.domain.entity.origin.OriginProjectCash;
import org.boozsoft.domain.entity.origin.OriginSysPeriod;
import org.boozsoft.domain.entity.share.ProjectCash;
import org.boozsoft.repo.*;
import org.boozsoft.repo.codekemu.CodeKemuRepository;
import org.boozsoft.repo.group.GroupCodeKemuOrgRepository;
import org.boozsoft.repo.group.GroupFaAccountRepository;
import org.boozsoft.repo.group.GroupStockAccountRepository;
import org.boozsoft.repo.organize.OriginSysPeriodRepository;
import org.boozsoft.repo.origin.OriginProjectCashRepository;
import org.springbooz.core.tool.result.R;
import org.springbooz.datasource.r2dbc.domain.vo.R2dbcRouterBuilder;
import org.springbooz.datasource.r2dbc.service.R2dbcRouterLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/org-period")
public class OriginSysPeriodController {

    @Autowired
    OriginSysPeriodRepository originSysPeriodRepository;

    @Autowired
    GroupSysAccountRepository groupSysAccountRepository;

    @Autowired
    SysAccountPeriodRepository sysAccountPeriodRepository;

    @Autowired
    CodeKemuRepository codeKemuRepository;

    @Autowired
    GroupCodeKemuOrgRepository groupCodeKemuOrgRepository;

    @Autowired
    SysPeriodRepository sysPeriodRepository;

    @Autowired
    ProjectCashRepository projectCashRepository;

    @Autowired
    OriginProjectCashRepository originProjectCashRepository;

    @Autowired
    GroupProjectCashRepository groupProjectCashRepository;

    @Autowired
    GroupStockAccountRepository groupStockAccountRepository;
    @Autowired
    GroupFaAccountRepository groupFaAccountRepository;

    @Autowired
    R2dbcRouterLoader r2dbcRouterLoader;

    @Autowired
    AccvoucherRepository accvoucherRepository;

    @GetMapping("groupByOrgPeriodList")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> getAll(String orgId) {
        return originSysPeriodRepository.findAllByOrgIdAndBeiyong1IsNullOrderByIyearAscPeriodAsc(orgId).collectList().map(o -> R.ok().setResult(o));
    }

    @PostMapping("groupByOrgPeriodIyear")
    public Mono<R> groupByOrgPeriodIyear(String orgId) {
        return originSysPeriodRepository.groupByOrgPeriodIyear(orgId).collectList().map(o -> R.ok().setResult(o));
    }

    @PostMapping("saveNextYear")
    public Mono<R> saveNextYear(@RequestBody Map map) {
        if (map.keySet().size() != 3) return Mono.just(R.error());
        String unique = map.get("unique").toString();
        String iyear = map.get("iyear").toString();
        String type = map.get("type").toString();
        String nextYear = (Integer.parseInt(iyear) + 1) + "";
        if (type.equals("1")) {

        } else {

        }
        // 添加期间
        Mono<List<GroupSysAccount>> save = originSysPeriodRepository.findAllByOrgIdAndIyearAndBeiyong1IsNullOrderByIyearAscPeriodAsc(unique, iyear).map(it -> {
            it.setId(null).setIyear(nextYear).setPeriod(nextYear + it.getIperiodNum());
            return it;
        }).collectList().flatMap(list -> originSysPeriodRepository.saveAll(list).collectList()).flatMap(d -> groupCodeKemuOrgRepository.findAllByIyearAndorgUniqueOrderByCcodeAsc(iyear, unique).map(it -> {
            it.setId(null).setIyear(nextYear);
            return it;
        }).collectList().flatMap(dl -> groupCodeKemuOrgRepository.saveAll(dl).collectList()).thenReturn(1).zipWith(originProjectCashRepository.findAllByOriginIdAndIyear(unique, iyear).map(it -> {
            it.setId(null);
            it.setIyear(nextYear);
            return it;
        }).collectList().flatMap(dl -> originProjectCashRepository.saveAll(dl).collectList()).thenReturn(1).flatMap(tips -> Mono.just(d))).flatMap(list1 -> groupSysAccountRepository.findAllByAccGroup(unique).flatMap(en -> sysAccountPeriodRepository.findAllByAccountCoCodeOrderByAccountYearDesc(en.getCoCode()).collectList().flatMap(aList -> {
            List<SysAccountPeriod> yearL = aList.stream().filter(it -> it.getAccountYear().equals(iyear)).collect(Collectors.toList());
            String accountMode = yearL.get(0).getAccountMode();
            int nextSzie = aList.stream().filter(it -> it.getAccountYear().equals(nextYear)).collect(Collectors.toList()).size();
            // 是否存在下一年度
            Mono<Integer> one = sysPeriodRepository.findAllByAccountIdAndIyear(en.getAccId(), iyear).collectList().zipWith(sysPeriodRepository.findAllByAccountIdAndIyear(en.getAccId(), nextYear).collectList()).flatMap(tip -> {
                if (tip.getT2().size() == 0) return sysPeriodRepository.saveAll(tip.getT1().stream().map(it -> {
                    it.setId(null).setIyear(nextYear).setPeriod(nextYear + it.getIperiodNum());
                    return it;
                }).collect(Collectors.toList())).collectList().thenReturn(1);
                return Mono.just(0);
            });
            Mono<Integer> two = codeKemuRepository.findAllByIyearAndTenandId(iyear, accountMode).map(it -> {
                it.setId(null).setIyear(nextYear);
                return it;
            }).collectList().flatMap(dl -> codeKemuRepository.saveAll(dl).collectList()).thenReturn(1);
            Mono<Integer> three = originProjectCashRepository.findAllByOrderByProjectCode(unique, iyear).map(it -> {
                it.setId(null);
                ProjectCash cash = new ProjectCash();
                BeanUtil.copyProperties(it, cash);
                return cash;
            }).collectList().flatMap(dl -> projectCashRepository.saveAll(dl).collectList()).thenReturn(1);
            // 判断对账表是否存在
            Mono<Integer> four = nextSzie == 0 ? Mono.just(yearL.get(0)).map(it -> {
                it.setId(null);
                it.setAccountYear(nextYear);
                return it;
            }).flatMap(e -> sysAccountPeriodRepository.save(e).thenReturn(1)) : Mono.just(0);
            return r2dbcRouterLoader.bind(() -> Mono.zip(one, two, three, four), R2dbcRouterBuilder.of("boozsoft-nc", "public", accountMode)).thenReturn(en);
        })).collectList()));
        return save.map(o -> R.ok());
    }

    @PostMapping("saveAccNextYear")
    public Mono<R> saveAccNextYear(@RequestBody Map map) {
        if (map.keySet().size() != 3) return Mono.just(R.error());
        String unique = map.get("unique").toString();
        String iyear = map.get("iyear").toString();
        String type = map.get("type").toString();
        String nextYear = (Integer.parseInt(iyear) + 1) + "";
        // 添加期间
        Mono<Integer> save = sysAccountPeriodRepository.findAllByAccountCoCodeOrderByAccountYearDesc(unique).collectList().flatMap(aList -> {
            List<SysAccountPeriod> yearL = aList.stream().filter(it -> it.getAccountYear().equals(iyear)).collect(Collectors.toList());
            String accountMode = yearL.get(0).getAccountMode();
            int nextSzie = aList.stream().filter(it -> it.getAccountYear().equals(nextYear)).collect(Collectors.toList()).size();
            // 是否存在下一年度
            Mono<Integer> one = sysPeriodRepository.findAllByAccountIdAndIyear(yearL.get(0).getAccountId(), iyear).collectList().zipWith(sysPeriodRepository.findAllByAccountIdAndIyear(yearL.get(0).getAccountId(), nextYear).collectList()).flatMap(tip -> {
                if (tip.getT2().size() == 0) return sysPeriodRepository.saveAll(tip.getT1().stream().map(it -> {
                    it.setId(null).setIyear(nextYear).setPeriod(nextYear + it.getIperiodNum());
                    return it;
                }).collect(Collectors.toList())).collectList().thenReturn(1);
                return Mono.just(0);
            });
            Mono<Integer> two = codeKemuRepository.findAllByIyearAndTenandId(iyear, accountMode).map(it -> {
                it.setId(null).setIyear(nextYear);
                return it;
            }).collectList().flatMap(dl -> codeKemuRepository.saveAll(dl).collectList()).thenReturn(1);
            Mono<Integer> three = originProjectCashRepository.findAllByOrderByProjectCode(unique, iyear).map(it -> {
                it.setId(null);
                ProjectCash cash = new ProjectCash();
                BeanUtil.copyProperties(it, cash);
                return cash;
            }).collectList().flatMap(dl -> projectCashRepository.saveAll(dl).collectList()).thenReturn(1);
            // 判断对账表是否存在
            Mono<Integer> four = nextSzie == 0 ? Mono.just(yearL.get(0)).map(it -> {
                it.setId(null);
                it.setAccountYear(nextYear);
                return it;
            }).flatMap(e -> sysAccountPeriodRepository.save(e).thenReturn(1)) : Mono.just(0);
            return r2dbcRouterLoader.bind(() -> Mono.zip(one, two, three, four), R2dbcRouterBuilder.of("boozsoft-nc", "public", accountMode)).thenReturn(1);
        });
        return save.map(o -> R.ok());
    }

    @DeleteMapping("delAccYear")
    public Mono<R> delAccYear(@RequestBody Map map) {
        if (map.keySet().size() != 3) return Mono.just(R.error());
        String unique = map.get("unique").toString();
        String iyear = map.get("iyear").toString();
        String type = map.get("type").toString();
        // 删除下属账套所以
        AtomicReference<Boolean> check = new AtomicReference(true); // 检查是否通过
        return sysAccountPeriodRepository.findAllByAccountCoCodeOrderByAccountYearDesc(unique).collectList().flatMap(aList -> {
            SysAccountPeriod accountPeriod = aList.stream().filter(it -> it.getAccountYear().equals(iyear)).collect(Collectors.toList()).get(0);
            String accountMode = accountPeriod.getAccountMode();
            return accvoucherRepository.countAllByIyearAndTenantId(iyear, accountMode).flatMap(s -> {
                if (s > 0 || !check.get()) {
                    check.set(false);
                    return Mono.just(new SysAccountPeriod());
                }
                return Mono.just(accountPeriod);
            });
        }).flatMap(accountPeriod -> {
            if (!check.get()) return Mono.just(R.ok(-1)); // 已使用
            String accountMode = accountPeriod.getAccountMode();
            Mono<Integer> one = sysPeriodRepository.deleteAllByAccountIdAndIyear(accountPeriod.getAccountId(), iyear).thenReturn(1);
            Mono<Integer> two = codeKemuRepository.deleteAllByTenantId(accountMode, iyear).thenReturn(1);
            Mono<Integer> three = projectCashRepository.deleteAllByTenantIdAndYear(accountMode, iyear).thenReturn(1);
            Mono<Boolean> existMono = groupStockAccountRepository.findAllByCoCode(accountPeriod.getAccountCoCode()).collectList().zipWith(groupFaAccountRepository.findAllByCoCode(accountPeriod.getAccountCoCode()).collectList()).map(tip -> tip.getT1().size() > 0 || tip.getT2().size() > 0);
            Mono<Integer> four = existMono.flatMap(b->b?Mono.just(1):sysAccountPeriodRepository.deleteAllByAccountCoCodeAndAccountYear(accountPeriod.getAccountCoCode(), iyear).thenReturn(1));
            return r2dbcRouterLoader.bind(() -> Mono.zip(one, two, three, four), R2dbcRouterBuilder.of("boozsoft-nc", "public", accountMode)).thenReturn(R.ok(1));
        });
    }

    @DeleteMapping("delYear")
    public Mono<R> delYear(@RequestBody Map map) {
        if (map.keySet().size() != 3) return Mono.just(R.error());
        String unique = map.get("unique").toString();
        String iyear = map.get("iyear").toString();
        String type = map.get("type").toString();
        // 删除下属账套所以
        AtomicReference<Boolean> check = new AtomicReference(true); // 检查是否通过
        return groupSysAccountRepository.findAllByAccGroup(unique).flatMap(en -> sysAccountPeriodRepository.findAllByAccountCoCodeOrderByAccountYearDesc(en.getCoCode()).collectList().flatMap(aList -> {
            SysAccountPeriod accountPeriod = aList.stream().filter(it -> it.getAccountYear().equals(iyear)).collect(Collectors.toList()).get(0);
            String accountMode = accountPeriod.getAccountMode();
            return accvoucherRepository.countAllByIyearAndTenantId(iyear, accountMode).flatMap(s -> {
                if (s > 0 || !check.get()) {
                    check.set(false);
                    return Mono.just(new SysAccountPeriod());
                }
                return Mono.just(accountPeriod);
            });
        })).collectList().flatMap(list -> {
            if (!check.get()) return Mono.just(R.ok(-1)); // 已使用
            return Flux.fromIterable(list).flatMap(accountPeriod -> {
                String accountMode = accountPeriod.getAccountMode();
                Mono<Integer> one = sysPeriodRepository.deleteAllByAccountIdAndIyear(accountPeriod.getAccountId(), iyear).thenReturn(1);
                Mono<Integer> two = codeKemuRepository.deleteAllByTenantId(accountMode, iyear).thenReturn(1);
                Mono<Integer> three = projectCashRepository.deleteAllByTenantIdAndYear(accountMode, iyear).thenReturn(1);
                Mono<Boolean> existMono = groupStockAccountRepository.findAllByCoCode(accountPeriod.getAccountCoCode()).collectList().zipWith(groupFaAccountRepository.findAllByCoCode(accountPeriod.getAccountCoCode()).collectList()).map(tip -> tip.getT1().size() > 0 || tip.getT2().size() > 0);
                Mono<Integer> four = existMono.flatMap(b->b?Mono.just(1):sysAccountPeriodRepository.deleteAllByAccountCoCodeAndAccountYear(accountPeriod.getAccountCoCode(), iyear).thenReturn(1));
                return r2dbcRouterLoader.bind(() -> Mono.zip(one, two, three, four), R2dbcRouterBuilder.of("boozsoft-nc", "public", accountMode)).thenReturn(1);
            }).collectList().flatMap(l -> {
                Mono<Integer> one = originSysPeriodRepository.deleteAllByOrgIdAndIyear(unique,iyear).thenReturn(1);
                Mono<Integer> two = groupCodeKemuOrgRepository.deleteAllByOrgUniqueAndIyear(unique,iyear).thenReturn(1);
                Mono<Integer> three = originProjectCashRepository.deleteAllByOriginIdAndIyear(unique,iyear).thenReturn(1);
                return Mono.zip(one, two, three).flatMap(tips -> Mono.just(R.ok()));
            });
        });
    }


    @PostMapping("saveOrgCash")
    @Transactional
    public Mono<R> saveOrgCash(@RequestBody Map map) {
        if (map.keySet().size() != 3) return Mono.just(R.error());
        String orgId = map.get("orgId").toString();
        String iyear = map.get("iyear").toString();
        String accStandard = map.get("accStandard").toString();
        return groupProjectCashRepository.findAllByAccStandard(accStandard).map(it -> {
            it.setId(null);
            OriginProjectCash cash = new OriginProjectCash();
            BeanUtil.copyProperties(it, cash);
            cash.setIyear(iyear);
            cash.setOriginId(orgId);
            return cash;
        }).collectList().flatMap(list -> originProjectCashRepository.saveAll(list).collectList()).map(o -> R.ok(1));
    }

    @PostMapping("saveOrgPeriod")
    @Transactional
    public Mono<R> saveOrgPeriod(@RequestBody Map map) {
        if (map.keySet().size() != 4) return Mono.just(R.error());
        String orgId = map.get("orgId").toString();
        String startDate = map.get("startDate").toString();
        String periodNum = map.get("periodNum").toString();
        String periodMonth = map.get("periodMonth").toString();
        return originSysPeriodRepository.saveAll(dataCollectionDuringGeneration(startDate, periodNum, periodMonth, orgId)).collectList().map(o -> R.ok(1));
    }

    public List<OriginSysPeriod> dataCollectionDuringGeneration(String startDate, String periodNum, String periodMonth, String orgId) {
        String year = startDate.substring(0, 4);
        String qYear = startDate.substring(0, 4);
        int number = Integer.parseInt(periodNum);
        List<OriginSysPeriod> list = new ArrayList<>();
        int index = 0;
        for (int i = 1; i <= number; i++) {
            String num = i > 9 ? "" + i : "0" + i;
            OriginSysPeriod period = new OriginSysPeriod();
            period.setIyear(year);
            period.setPeriod(qYear + num);
            period.setIperiodNum(num);
            period.setOrgId(orgId);
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
}

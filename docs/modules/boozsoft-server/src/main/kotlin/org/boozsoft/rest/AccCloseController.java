package org.boozsoft.rest;

import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.SysPeriod;
import org.boozsoft.domain.entity.account.Iperiod;
import org.boozsoft.repo.AccCloseRepository;
import org.boozsoft.repo.IperiodRepository;
import org.boozsoft.repo.SysPeriodRepository;
import org.boozsoft.service.SysLogService;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/accClose")
public class AccCloseController {

    @Autowired
    IperiodRepository iperiodRepository;

    @Autowired
    SysPeriodRepository sysPeriodRepository;

    @Autowired
    SysLogService sysLogService;

    @Autowired
    AccCloseRepository accCloseRepository;

    @GetMapping("findByIyear")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByIyear(String iyear){
        return accCloseRepository.findAllByIyearOrderByImonthAsc(iyear)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }
    /********************* Mr. Ye *******************/


    @GetMapping("findAllByIyear")
    @ApiOperation(value = "查询指定年份区间与季度", notes = "传入code")
    public Mono<R> findAllByIyear(String accId,String iyear){
        Mono<List<SysPeriod>> qjMono = sysPeriodRepository.findAllByAccountIdAndIyearAndBeiyong1NullOrderByIyearAscDateStartAsc(accId,iyear.split("--")[0]).collectList().cache();
        Mono<List<SysPeriod>> jdMono = sysPeriodRepository.findAllByAccountIdAndIyearAndBeiyong1OrderByIyearAscIperiodNumAsc(accId,iyear.split("--")[1],"1").collectList().cache();
        return Mono.zip(qjMono,jdMono).map(o -> R.ok().setResult(o));
    }

    @PostMapping("saveSystemQuarterly")
    @ApiOperation(value = "添加账套区间", notes = "传入code")
    public Mono<R> saveSystemQuarterly(@RequestBody List<SysPeriod>  iperiodFlux) {
        return Mono.just(iperiodFlux.get(0)).flatMap(entity->
            sysPeriodRepository.findAllByAccountIdAndIyearAndBeiyong1OrderByIyearAscIperiodNumAsc(entity.getAccountId(),entity.getIyear(),"1").collectList()
                    .flatMap(sysPeriodRepository::deleteAll).thenReturn(iperiodFlux)
        ).doOnNext(list -> {
            for (SysPeriod iperiod : list) {
                iperiod.setId(null);
            }
        }).flatMap(list->sysPeriodRepository.saveAll(list).collectList().map(o -> R.ok().setResult(o)));
    }

    @PostMapping("saveQuarterly")
    @ApiOperation(value = "添加账套区间", notes = "传入code")

    public Mono<R> saveQuarterly(@RequestBody List<Iperiod>  iperiodFlux) {
         return Mono.just(iperiodFlux.get(0).getIyear())
                 .flatMap(thisYaer->
                     iperiodRepository.findAllByIyearAndBeiyong1OrderById(thisYaer)
                     .collectList()
                     .flatMap(iperiodRepository::deleteAll).thenReturn(iperiodFlux)
                 )
                 .doOnNext(list -> {
                     for (Iperiod iperiod : list) {
                         iperiod.setId(null);
                     }
                 })
                 .flatMap(list->iperiodRepository.saveAll(list).collectList().map(o -> R.ok().setResult(o)));
    }

    @GetMapping("findPeriodInfo")
    @ApiOperation(value = "查询指定年份区间与季度", notes = "传入code")
    public Mono<R> findPeriodInfo(String accId){
        return sysPeriodRepository.findAllByAccountId(accId).collectList().map(list->{
            Map<String, Object> map = new HashMap<>();
            if (list.size() > 0){
                SysPeriod period = list.stream().filter(item -> item.getEnablePeriod().equals("1")).collect(Collectors.toList()).get(0);
                map.put("accStartDate",period.getIyear()+"-"+period.getDateStart());
                map.put("periodNum",list.get(list.size()-1).getIperiodNum());
                String iyear = list.get(list.size() - 1).getIyear();
                map.put("maxYear",iyear);
                List<SysPeriod> sysPeriods = list.stream().filter(item -> item.getIyear().equals(iyear)).collect(Collectors.toList());
                map.put("yearStartDate",sysPeriods.get(0).getIyear()+"-"+sysPeriods.get(0).getDateStart());
                map.put("yearEndDate",sysPeriods.get(sysPeriods.size()-1).getIyear()+"-"+sysPeriods.get(sysPeriods.size()-1).getDateEnd());
                map.put("yearList",assemblyYearList(list));
            }
            return map;
        }).map(o -> R.ok().setResult(o));
    }

    private List<String> assemblyYearList(List<SysPeriod> list) {
        HashSet<String> set = new HashSet<>();
        for (SysPeriod sysPseriod : list) {
            set.add(sysPseriod.getIyear());
        }
        return new ArrayList<>(set);
    }

    /********************* Mr. Ye *******************/
}

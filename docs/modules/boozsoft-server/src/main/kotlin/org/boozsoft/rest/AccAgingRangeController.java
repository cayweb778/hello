package org.boozsoft.rest;

import cn.hutool.core.collection.ListUtil;
import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.SysPeriod;
import org.boozsoft.domain.entity.account.AccAgingRange;
import org.boozsoft.domain.entity.account.Iperiod;
import org.boozsoft.domain.entity.group.GroupSysOrg;
import org.boozsoft.repo.AccAgingRangeRepository;
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
@RequestMapping("/AccAgingRange")
public class AccAgingRangeController {

    /********************* Mr. Ye *******************/
    @Autowired
    private AccAgingRangeRepository agingRangeRepository;

    @GetMapping
    @ApiOperation(value = "查询列表", notes = "传入类型")
    public Mono<R> findAllByModel(String model){
        return agingRangeRepository.findAllByAgingModelOrderByTotalDayNumberAsc(model)
                .collectList()
                .map(list -> ListUtil.sort(list, (o1, o2) -> Integer.valueOf(o1.getTotalDayNumber()).compareTo(Integer.valueOf(o2.getTotalDayNumber()))))
                .map(o -> R.ok().setResult(o));
    }


    @PostMapping
    @ApiOperation(value = "添加区间", notes = "传入code")
    public Mono<R> save(@RequestBody AccAgingRange entity) {
         return agingRangeRepository.save(entity).map(o -> R.ok().setResult(o));
    }
    /********************* Mr. Ye *******************/

    @GetMapping("findAllByAccIdModel")
    @ApiOperation(value = "查询列表", notes = "传入类型")
    public Mono<R> findAllByAccIdModel(String accId,String model){
        return agingRangeRepository.findAllByAccIdAndAgingModelOrderByTotalDayNumberAsc(accId, model)
                .collectList()
                .map(list -> ListUtil.sort(list, (o1, o2) -> Integer.valueOf(o1.getTotalDayNumber()).compareTo(Integer.valueOf(o2.getTotalDayNumber()))))
                .map(o -> R.ok().setResult(o));
    }

}

package org.boozsoft.rest.stock;

import cn.hutool.core.collection.ListUtil;
import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.stock.ArApAgingRange;
import org.boozsoft.repo.stock.ArApAgingRangeRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/ArApAgingRange")
public class ArApAgingRangeController {

    @Autowired
    private ArApAgingRangeRepository agingRangeRepository;

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
    public Mono<R> save(@RequestBody ArApAgingRange entity) {
         return agingRangeRepository.save(entity).map(o -> R.ok().setResult(o));
    }

    @GetMapping("findAllByAccIdModel")
    @ApiOperation(value = "查询列表", notes = "传入类型")
    public Mono<R> findAllByAccIdModel(String accId,String model){
        return agingRangeRepository.findAllByAccIdAndAgingModelOrderByTotalDayNumberAsc(accId, model)
                .collectList()
                .map(list -> ListUtil.sort(list, (o1, o2) -> Integer.valueOf(o1.getTotalDayNumber()).compareTo(Integer.valueOf(o2.getTotalDayNumber()))))
                .map(o -> R.ok().setResult(o));
    }

}

package org.boozsoft.rest.stock;

import cn.hutool.core.date.DateUtil;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.boozsoft.repo.group.GroupStockPeriodRepository;
import org.boozsoft.repo.stock.ArApYsyfRepository;
import org.boozsoft.repo.stock.ArBeginBalanceRepository;
import org.boozsoft.repo.stock.StockSaleousingRepository;
import org.boozsoft.repo.stock.StockWarehousingRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/arApPeriod")
public class ArApPeriodController {

    @Autowired
    private ArBeginBalanceRepository arBeginBalanceRepository;
    @Autowired
    private ArApYsyfRepository arApYsyfRepository;
    @Autowired
    private StockSaleousingRepository saleousingRepository;
    @Autowired
    private StockWarehousingRepository warehousingRepository;
    @Autowired
    private GroupStockPeriodRepository periodRepository;

    @GetMapping("findPeriodByYearList")
    public Mono<R> findPeriodByYearList(String year){
        return periodRepository.findByStockYearOrderByStockMonth(year)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findBeginBalanceByYearList")
    public Mono<R> findBeginBalanceByYearList(String billStyle,String year){
//        return arBeginBalanceRepository.findAllByBillStyleAndIyearOrderByCcode("ar",year)
        return arBeginBalanceRepository.findAllByBillStyleAndIyearOrderByCcode(billStyle,year)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findSaleousingByDateList")
    public Mono<R> findSaleousingByDateList(String billStyle,String date){
//        return saleousingRepository.findAllByBillStyleAndDdateLikeOrderByBcheckAscDdateAscCcodeAsc("YSD","%"+date+"%")
        return saleousingRepository.findAllByBillStyleAndDdateLikeOrderByBcheckAscDdateAscCcodeAsc(billStyle,"%"+date+"%")
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findWarehousingByDateList")
    public Mono<R> findWarehousingByDateList(String billStyle,String date){
        return warehousingRepository.findAllByBillStyleAndDdateLikeOrderByBcheckAscDdateAscCcodeAsc(billStyle,"%"+date+"%")
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findArApYsyfByDateList")
    public Mono<R> findArApYsyfByDateList(String billStyle,String date){
//        return arApYsyfRepository.findAllByBillStyleAndDdateLikeOrderByCcode("SKD","%"+date+"%")
        return arApYsyfRepository.findAllByBillStyleAndDdateLikeOrderByCcode(billStyle,"%"+date+"%")
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping("savePeriod")
    @ApiOperation(value = "获取最新结账或未结账期间", notes = "传入code")
    public Mono<R> savePeriod(@RequestBody Map map) {
        if (map.keySet().size() != 3) return Mono.just("请求参数异常").map(m -> R.error());
        String id = map.get("id").toString();
        String value = map.get("value").toString();
        String user = map.get("user").toString();
        return periodRepository.findById(id).flatMap(dbEntry -> {
            if (value.equals("1")){
                dbEntry.setIar(value);
                dbEntry.setIarTime(DateUtil.now());
                dbEntry.setIarUser(user);
            }else {
                dbEntry.setIar(null);
                dbEntry.setIarTime(null);
                dbEntry.setIarUser(null);
            }
            return periodRepository.save(dbEntry).map(R::ok);
        });
    }

    @PostMapping("saveApPeriod")
    @ApiOperation(value = "获取最新结账或未结账期间", notes = "传入code")
    public Mono<R> saveApPeriod(@RequestBody Map map) {
        if (map.keySet().size() != 3) return Mono.just("请求参数异常").map(m -> R.error());
        String id = map.get("id").toString();
        String value = map.get("value").toString();
        String user = map.get("user").toString();
        return periodRepository.findById(id).flatMap(dbEntry -> {
            if (value.equals("1")){
                dbEntry.setIap(value);
                dbEntry.setIapTime(DateUtil.now());
                dbEntry.setIapUser(user);
            }else {
                dbEntry.setIap(null);
                dbEntry.setIapTime(null);
                dbEntry.setIapUser(null);
            }
            return periodRepository.save(dbEntry).map(R::ok);
        });
    }

}

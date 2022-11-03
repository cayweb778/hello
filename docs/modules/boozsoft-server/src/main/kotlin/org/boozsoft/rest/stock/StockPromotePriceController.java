package org.boozsoft.rest.stock;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.boozsoft.domain.entity.stock.StockPromoteActivity;
import org.boozsoft.domain.entity.stock.StockPromotePrice;
import org.boozsoft.domain.entity.stock.StockPromotePriceUser;
import org.boozsoft.repo.stock.StockPromoteActivityRepository;
import org.boozsoft.repo.stock.StockPromotePriceRepository;
import org.boozsoft.repo.stock.StockPromotePriceUserRepository;
import org.boozsoft.repo.stock.StockRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/stockPromotePrice")
public class StockPromotePriceController {

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private StockPromotePriceRepository stockCustPriceRepository;

    @Autowired
    private StockPromotePriceUserRepository stockCustPriceUserRepository;

    @Autowired
    private StockPromoteActivityRepository stockPromoteActivityRepository;

    
    
    // 查询存货档案 
    @PostMapping("findAll")
    public Mono<R> findAll(@RequestBody Map map){
        String custId = map.get("custId").toString();
        String activityId = map.get("activityId").toString();
        return stockCustPriceRepository.findAllByPrice(custId,activityId).collectList()
                .map(a->R.ok().setResult(a));
    }


    @PostMapping("save")
    public Mono<R> save(@RequestBody StockPromotePrice object) {
        object.setOperatorTime(LocalDate.now().toString());
        return stockCustPriceRepository.save(object).map(a->R.ok().setResult(a));
    }

    @PostMapping("improtExcel")
    public Mono<R> improtExcel(@RequestBody List<StockPromotePrice> list) {
        list.forEach(v->{
            v.setOperatorTime(LocalDate.now().toString());
        });
        return stockCustPriceRepository.findAll()
                .collectList()
                .map(slist->{
                    //已存在数据
                    List<String> collect = list.stream().map(o -> o.getStockNum()+"-"+o.getCustId()).collect(Collectors.toList());
                    List<StockPromotePrice> collect1 = slist.stream().filter(v -> collect.contains(v.getStockNum()+"-"+v.getCustId())).collect(Collectors.toList());
                    return collect1;
                })
                .flatMap(dlist-> {
                    return  stockCustPriceRepository.deleteAll(dlist).thenReturn(dlist);
                })
                .flatMap(v->{
                    return stockCustPriceRepository.saveAll(list)
                            .collectList();
                })
                .map(a->R.ok().setResult(a));
    }


    private String getFieldValueByFieldName(String fieldName, Object object) {
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + firstLetter + fieldName.substring(1);
            Method method = object.getClass().getMethod(getter, new Class[]{});
            Object value = method.invoke(object, new Object[]{});
            return value.toString();
        } catch (Exception e) {
            return null;
        }
    }

    @GetMapping("/getAllCustTree/{activityId}")
    public Mono<R> getAllCustTree(@PathVariable String activityId) {
        return stockCustPriceUserRepository.findCustAll(activityId)
                .collectList()
                .map(list -> R.ok().setResult(list));
    }

    @GetMapping("/del/{id}")
    public Mono<R> del(@PathVariable String id) {
        return stockCustPriceRepository.deleteById(id).then(Mono.just(R.ok()));
    }

    @GetMapping("/delCust/{id}")
    public Mono<R> delCust(@PathVariable String id) {
        return stockCustPriceUserRepository.deleteById(id)
                .zipWith(stockCustPriceRepository.deleteBycustId(id))
                .then(Mono.just(R.ok()));
    }

    @PostMapping("addCusts")
    public Mono<R> addCusts(@RequestBody List<StockPromotePriceUser> list) {
        return stockCustPriceUserRepository.saveAll(list).collectList().map(a->R.ok().setResult(a));
    }




    //促销活动 
    @GetMapping("findAllActivity")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAllActivity(Pageable pageable){
        return stockPromoteActivityRepository.findAllByOrderById(pageable).collectList()
                .map(list->{
                    list.forEach(v->{
                        LocalDate startDate = LocalDate.parse(v.getStartDate().substring(0,10));
                        LocalDate stopDate = LocalDate.parse(v.getStopDate().substring(0,10));
                        v.setStartDate(startDate.toString());
                        v.setStopDate(stopDate.toString());
                        if(LocalDate.now().isAfter(stopDate)){//结束
                            v.setStatus("2");
                        }else if(LocalDate.now().isBefore(startDate)){//未开始
                            v.setStatus("0");
                        }else{
                            v.setStatus("1");
                        }
                    });
                    return list;
                })
                .map(R::page);
    }

    @GetMapping("findByFlag")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByFlag(){
        return stockPromoteActivityRepository.findByFlagOrderById("1")
                .collectList()
                .map(R::page);
    }

    @GetMapping("findByCode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByCode(String settModesCode){
        return stockPromoteActivityRepository.findByActivityCodeOrderById(settModesCode)
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findByBsName")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByBsName(String settModesCode){
        return stockPromoteActivityRepository.findByActivityNameOrderById(settModesCode)
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping
    @ApiOperation(value = "新增或修改", notes = "传入code")
    public Mono save(@RequestBody StockPromoteActivity object){
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        if (object.getFlag()==null || object.getFlag().equals("")) {
            object.setFlag("1");
        }
        //增加
        if (object.getId()==null || object.getId().equals("")) {
            object.setOperatorTime(time);
        }
        return stockPromoteActivityRepository.save(object)
                .map(o-> R.ok().setResult(o));
    }

    @DeleteMapping
    @ApiOperation(value = "删除", notes = "传入code")
    public Mono delete(@RequestBody StockPromoteActivity object){
        return stockPromoteActivityRepository.deleteById(object.getId())
                .then(Mono.just(R.ok()));
    }

    @PostMapping("editFlag")
    @ApiOperation(value = "停用启用操作", notes = "传入code")
    public Mono editFlag(@RequestBody StockPromoteActivity object) {
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        if (object.getFlag().equals("1")){
            object.setStopDate(time);
            object.setFlag("0");
        } else {
            object.setFlag("1");
        }
        return stockPromoteActivityRepository.save(object)
                .map(o -> R.ok().setResult(o));
    }


    // 查询存货档案
    @PostMapping("findPromotionPrice")
    public Mono<R> findPromotionPrice(@RequestBody Map map) {
        String date = map.get("date").toString();
        String cust = map.get("cust").toString();
        String stock = map.get("stock").toString();
        return stockPromoteActivityRepository.findAllByFlagAndStartDateLessThanEqualAndStopDateGreaterThanEqualOrderByStopDateAsc("1", date, date).collectList().flatMap(list -> {
            List<String> ids = list.stream().map(it -> it.getId()).distinct().collect(Collectors.toList());
            Mono<List<String>> custM = stockCustPriceUserRepository.findAllByActivityIdInAndCustId(ids, cust).map(it -> it.getId()).distinct().collectList();
            return list.size() == 0 ? Mono.just(R.ok(0)) : custM.flatMap(custs -> {
                if (list.stream().filter(it -> it.getActivityScope().equals("1")).collect(Collectors.toList()).size() > 0)
                    custs.add("0");
                return stockCustPriceRepository.findAllByActivityIdInAndCustIdInAndStockNum(ids, custs, stock).collectList().flatMap(pList -> {
                    Double p = Double.valueOf("0");
                    if (pList.size() > 0) { // 获取快过期的价格
                        List<String> as = pList.stream().map(it -> it.getActivityId()).distinct().collect(Collectors.toList());
                        List<StockPromoteActivity> activities = list.stream().filter(it -> as.contains(it.getId())).collect(Collectors.toList());
                        for (StockPromoteActivity activity : activities) {
                            List<StockPromotePrice> collect = pList.stream().filter(it -> it.getActivityId().equals(activity.getId())/* && (it.getCustId().equals(activity.getActivityScope().equals("1") ? "0" : cust))*/).collect(Collectors.toList());
                            if (collect.size() > 0) {
                                p = Double.parseDouble(collect.get(0).getMemberprice());
                                break;
                            }
                        }
                    }
                    return Mono.just(R.ok(p));
                });
            });
        });
    }
}

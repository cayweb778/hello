package org.boozsoft.rest.stock;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.boozsoft.domain.entity.stock.StockClass;
import org.boozsoft.domain.entity.stock.StockCustPrice;
import org.boozsoft.domain.entity.stock.StockCustPriceUser;
import org.boozsoft.domain.entity.stock.StockPrice;
import org.boozsoft.domain.vo.stock.StockCustPriceUserVo;
import org.boozsoft.repo.stock.StockCustPriceRepository;
import org.boozsoft.repo.stock.StockCustPriceUserRepository;
import org.boozsoft.repo.stock.StockPriceRepository;
import org.boozsoft.repo.stock.StockRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/stockCustPrice")
public class StockCustPriceController {

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private StockCustPriceRepository stockCustPriceRepository;

    @Autowired
    private StockCustPriceUserRepository stockCustPriceUserRepository;

    // 查询存货档案 
    @PostMapping("findAll")
    public Mono<R> findAll(@RequestBody Map map){
        String custId = map.get("custId").toString();
        Map<String, String> searchMap = ((HashMap<String,  String>) map.get("searchConditon"));
        return stockCustPriceRepository.findAllByPrice(custId).collectList()
                .map(a->R.ok().setResult(a));
    }


    @PostMapping("save")
    public Mono<R> save(@RequestBody StockCustPrice object) {
        object.setOperatorTime(LocalDate.now().toString());
        return stockCustPriceRepository.save(object).map(a->R.ok().setResult(a));
    }


    @PostMapping("improtExcel")
    public Mono<R> improtExcel(@RequestBody List<StockCustPrice> list) {
        list.forEach(v->{
            v.setOperatorTime(LocalDate.now().toString());
        });
        return stockCustPriceRepository.findAll()
                .collectList()
                .map(slist->{
                    //已存在数据
                    List<String> collect = list.stream().map(o -> o.getStockNum()+"-"+o.getCustId()).collect(Collectors.toList());
                    List<StockCustPrice> collect1 = slist.stream().filter(v -> collect.contains(v.getStockNum()+"-"+v.getCustId())).collect(Collectors.toList());
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

    @GetMapping("getAllCustTree")
    public Mono<R> getAllCustTree() {
        return stockCustPriceUserRepository.findCustAll()
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
    public Mono<R> addCusts(@RequestBody List<StockCustPriceUser> list) {
        return stockCustPriceUserRepository.saveAll(list).collectList().map(a->R.ok().setResult(a));
    }


    @PostMapping("findCustAndStockPrice")
    public Mono<R> findCustAndStockPrice(@RequestBody Map map) {
        if (map.keySet().size() != 2)return Mono.just(R.ok(0));
        String cust = map.get("cust").toString();
        String stock = map.get("stock").toString();
        Mono<List<String>> custM = stockCustPriceUserRepository.findAllByCustId(cust).map(it -> it.getId()).distinct().collectList();
        return  custM.flatMap(custs->custs.size()==0? Mono.just(R.ok(0)):
                        stockCustPriceRepository.findAllByCustIdAndStockNumOrderByOperatorTimeDesc(custs.get(0),stock).filter(it-> !StrUtil.equals(it.getStatus(),"1")).collectList()
                                .map(a->R.ok(a.size() > 0?Double.parseDouble(a.get(0).getMemberprice()):0))
                );
    }
}

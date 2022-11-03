package org.boozsoft.rest.stock;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.boozsoft.domain.entity.stock.StockPrice;
import org.boozsoft.domain.vo.stock.StockPriceVo;
import org.boozsoft.repo.stock.StockPriceRepository;
import org.boozsoft.repo.stock.StockRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/stockPrice")
public class StockPriceController {

    @Autowired
    private StockPriceRepository stockPriceRepository;

    @Autowired
    private StockRepository stockRepository;

    // 查询存货档案
    @PostMapping("findAll")
    public Mono<R> findAll(@RequestBody Map map){
        String stockClass=map.get("stockClass").toString();
        Map<String, String> searchMap = ((HashMap<String,  String>) map.get("searchConditon"));
        return stockRepository.findAllByPrice().collectList()
                .flatMap(list->{
                    if(StrUtil.isNotBlank(stockClass) && !stockClass.equals("0")){
                        list=list.stream().filter(f->f.getStockClass().equals(stockClass)).collect(Collectors.toList());
                    }
                    return Mono.just(list);
                })
                .map(a->R.ok().setResult(a));
    }

    @PostMapping("save")
    public Mono<R> save(@RequestBody StockPrice object) {
        object.setOperatorTime(LocalDate.now().toString());
        return stockPriceRepository.save(object).map(a->R.ok().setResult(a));
    }

    @PostMapping("improtExcel")
    public Mono<R> improtExcel(@RequestBody List<StockPrice> list) {
        list.forEach(v->{
            v.setOperatorTime(LocalDate.now().toString());
        });
        List<StockPrice> collect2 =  new ArrayList<>();
        return stockPriceRepository.findAll()
                .collectList()
                .map(slist->{
                    //已存在数据 
                    List<String> collect = list.stream().map(o -> o.getStockNum()).collect(Collectors.toList());
                    List<StockPrice> collect1 = slist.stream().filter(v -> collect.contains(v.getStockNum())).collect(Collectors.toList());

                    collect1.forEach(v->{
                        Optional<StockPrice> first = list.stream().filter(o -> collect.contains(v.getStockNum())).findFirst();
                        if(first.isPresent()){
                            StockPrice s = first.get();
                            if(Objects.nonNull(s.getRegularPrice())) v.setRegularPrice(s.getRegularPrice());
                            if(Objects.nonNull(s.getMemberprice())) v.setMemberprice(s.getMemberprice());
                            if(Objects.nonNull(s.getMaxPrice())) v.setMaxPrice(s.getMaxPrice());
                            if(Objects.nonNull(s.getMinPrice())) v.setMinPrice(s.getMinPrice());
                            if(Objects.nonNull(s.getInvscost1())) v.setInvscost1(s.getInvscost1());
                            if(Objects.nonNull(s.getInvscost2())) v.setInvscost2(s.getInvscost2());
                            if(Objects.nonNull(s.getInvscost3())) v.setInvscost3(s.getInvscost3());
                            if(Objects.nonNull(s.getInvscost4())) v.setInvscost4(s.getInvscost4());
                            if(Objects.nonNull(s.getInvscost5())) v.setInvscost5(s.getInvscost5());
                            if(Objects.nonNull(s.getInvscost6())) v.setInvscost6(s.getInvscost6());
                            if(Objects.nonNull(s.getInvscost7())) v.setInvscost7(s.getInvscost7());
                            if(Objects.nonNull(s.getInvscost8())) v.setInvscost8(s.getInvscost8());
                        }
                    });

                    //不存在数据
                    List<String> collect3 = slist.stream().map(o -> o.getStockNum()).collect(Collectors.toList());
                    List<StockPrice> noList = list.stream().filter(v -> !collect3.contains(v.getStockNum())).collect(Collectors.toList());
                    collect2.addAll(noList);
                    return collect1;
                })
                .flatMap(l-> {
                   return  stockPriceRepository.saveAll(l)
                           .collectList();
                })
                .flatMap(v->{
                    return stockPriceRepository.saveAll(collect2)
                            .collectList();
                })
                .map(a->R.ok().setResult(a));
    }

    @PostMapping("clearList")
    public Mono<R> clearList(@RequestBody List<String> list) {
        return stockPriceRepository.deleteByIds(list)
                .thenReturn(list)
                .map(a -> R.ok().setResult(a));
    }

    @PostMapping("saveMx")
    public Mono<R> saveMx(@RequestBody StockPrice object) {
        return Objects.isNull(object.getId())
                ? stockPriceRepository.save(object).map(a -> R.ok().setResult(a))
                : stockPriceRepository.findById(object.getId())
                    .map(v -> {
                        object.setOperatorTime(v.getOperatorTime());
                        object.setOperatorName(v.getOperatorName());
                        object.setStatus(v.getStatus());
                        object.setStockNum(v.getStockNum());
                        object.setTenantId(v.getTenantId());
                        BeanUtils.copyProperties(object, v);
                        return v;
                    })
                    .flatMap(stockPriceRepository::save)
                    .map(a -> R.ok().setResult(a));

    }

    @GetMapping("/getPriceDataByStockNum/{stockNum}")
    public Mono<R> getPriceDataByStockNum(@PathVariable String stockNum) {
        return stockPriceRepository.findByStockNum(stockNum)
                .defaultIfEmpty(new StockPrice())
                .map(a->R.ok().setResult(a));
    }
    @PostMapping("savePrice")
    public Mono<R> savePrice(@RequestBody StockPrice object) {
        object.setOperatorTime(LocalDate.now().toString());
        object.setId(null);
        return stockPriceRepository.deleteByStockNum(object.getStockNum())
                .thenReturn(object)
                .flatMap(stockPriceRepository::save)
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
}

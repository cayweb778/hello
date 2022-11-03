package org.boozsoft.rest.stock;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.boozsoft.domain.entity.stock.*;
import org.boozsoft.repo.stock.*;
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
@RequestMapping("/stockSupPrice")
public class StockSupPriceController {

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private StockSupPriceRepository stockCustPriceRepository;

    @Autowired
    private StockSupPriceUserRepository stockCustPriceUserRepository;

    // 查询存货档案 
    @PostMapping("findAll")
    public Mono<R> findAll(@RequestBody Map map){
        String stockClass=map.get("stockClass").toString();
        return stockCustPriceRepository.findAllByPrice().collectList()
                .flatMap(list->{
                    if(StrUtil.isNotBlank(stockClass) && !stockClass.equals("0")){
                        list=list.stream().filter(f->f.getStockClass().equals(stockClass)).collect(Collectors.toList());
                    }
                    return Mono.just(list);
                })
                .map(a->R.ok().setResult(a));
    }


    @PostMapping("save")
    public Mono<R> save(@RequestBody StockSupPrice object) {
        object.setOperatorTime(LocalDate.now().toString());
        return stockCustPriceRepository.save(object).map(a->R.ok().setResult(a));
    }

    @PostMapping("improtExcel")
    public Mono<R> improtExcel(@RequestBody List<StockSupPrice> list) {
        list.forEach(v->{
            v.setOperatorTime(LocalDate.now().toString());
        });
        List<StockSupPrice> collect2 =  new ArrayList<>();
        return stockCustPriceRepository.findAll()
                .collectList()
                .map(slist->{
                    //已存在数据
                    List<String> collect = list.stream().map(o -> o.getStockNum()).collect(Collectors.toList());
                    List<StockSupPrice> collect1 = slist.stream().filter(v -> collect.contains(v.getStockNum())).collect(Collectors.toList());

                    collect1.forEach(v->{
                        Optional<StockSupPrice> first = list.stream().filter(o -> collect.contains(v.getStockNum())).findFirst();
                        if(first.isPresent()){
                            StockSupPrice s = first.get();
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
                    List<StockSupPrice> noList = list.stream().filter(v -> !collect3.contains(v.getStockNum())).collect(Collectors.toList());
                    collect2.addAll(noList);
                    return collect1;
                })
                .flatMap(l-> {
                    return  stockCustPriceRepository.saveAll(l)
                            .collectList();
                })
                .flatMap(v->{
                    return stockCustPriceRepository.saveAll(collect2)
                            .collectList();
                })
                .map(a->R.ok().setResult(a));
    }

    @PostMapping("clearList")
    public Mono<R> clearList(@RequestBody List<String> list) {
        return stockCustPriceRepository.deleteByIds(list)
                .thenReturn(list)
                .map(a -> R.ok().setResult(a));
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
    public Mono<R> addCusts(@RequestBody List<StockSupPriceUser> list) {
        return stockCustPriceUserRepository.saveAll(list).collectList().map(a->R.ok().setResult(a));
    }

    @PostMapping("findBySupIdAndStockNum")
    public Mono<R> findBySupIdAndStockNum(String supId,String stockNum) {
        return stockCustPriceRepository.findBySupIdAndStockNum(supId,stockNum).map(a->R.ok().setResult(a)).defaultIfEmpty(R.ok().setResult(""));
    }

    @PostMapping("saveMx")
    public Mono<R> saveMx(@RequestBody StockSupPrice object) {
        return Objects.isNull(object.getId())
                ? stockCustPriceRepository.save(object).map(a -> R.ok().setResult(a))
                : stockCustPriceRepository.findById(object.getId())
                    .map(v -> {
                        object.setOperatorTime(v.getOperatorTime());
                        object.setOperatorName(v.getOperatorName());
                        object.setStatus(v.getStatus());
                        object.setStockNum(v.getStockNum());
                        object.setTenantId(v.getTenantId());
                        BeanUtils.copyProperties(object, v);
                        return v;
                    })
                    .flatMap(stockCustPriceRepository::save)
                    .map(a -> R.ok().setResult(a));
    }

}

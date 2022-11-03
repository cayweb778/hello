package org.boozsoft.rest.stock;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.boozsoft.domain.entity.CustomerClass;
import org.boozsoft.domain.entity.stock.ArBeginBalance;
import org.boozsoft.domain.entity.stock.StockCustGroup;
import org.boozsoft.repo.stock.StockCustGroupRepository;
import org.boozsoft.repo.stock.StockCustGroupsRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/stock/custGroup")
public class StockCustGroupController {

    @Autowired
    private StockCustGroupRepository stockCustGroupRepository;

    @Autowired
    private StockCustGroupsRepository stockCustGroupsRepository;
    @PostMapping("countByStockCustGroupName")
    public Mono<R> countByStockCustGroupName(String name){
        return stockCustGroupRepository.countByCustGroupName(name).map(a->R.ok().setResult(a));
    }

    @PostMapping("getStockCustGroupMaxCcode")
    public Mono<R> getStockCustGroupMaxCcode(){
        return stockCustGroupRepository.getStockCustGroupMaxCcode().collectList()
        .flatMap(maxCcode->{
            String max="";
            if(maxCcode.size()==0){
                max=String.format("%04d",1);
            }else{
                List<Integer> list2 = new ArrayList<>();
                for (int i = 0; i < 1000; i++) {
                    list2.add(i + 1);
                }
                List<Integer> list=new ArrayList<>();
                maxCcode.forEach(c->{
                    list.add(Integer.parseInt(c));
                });
                int minCode = list2.stream().filter(item -> !list.contains(item)).mapToInt(Integer::intValue).min().orElse(0);
                max=String.format("%04d",minCode);
            }
            return Mono.just(max);
        }).map(a->R.ok().setResult(a));
    }

    @PostMapping("saveStockCustGroupMaxCcode")
    public Mono<R> saveStockCustGroupMaxCcode(@RequestBody StockCustGroup obj){
        return stockCustGroupRepository.save(obj).map(a->R.ok().setResult(a));
    }

    @PostMapping("findAllStockCust")
    public Mono<R> findAllStockCust(){
        return stockCustGroupRepository.findAll1().collectList().map(a->R.ok().setResult(a));
    }

    @PostMapping("delByStockCustCcode")
    public Mono<R> findAllStockCust(String ccode){
        return stockCustGroupRepository.deleteByCcode(ccode).then(Mono.just(R.ok()));
    }

    @PostMapping("findAllCustGroupInfo")
    public Mono<R> findAllCustGroupInfo(){
        return stockCustGroupRepository.findAll1()
                .flatMap(it->
                        stockCustGroupsRepository.findAllCustCode(it.getCcode()).collectList().flatMap(list->{it.setCodeList(JSON.toJSONString(list));return Mono.just(it);})
                ).collectList().map(a->R.ok().setResult(a));
    }
}

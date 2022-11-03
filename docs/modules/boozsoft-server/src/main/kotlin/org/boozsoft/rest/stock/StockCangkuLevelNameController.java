package org.boozsoft.rest.stock;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.boozsoft.domain.entity.stock.StockCangkuLevelName;
import org.boozsoft.repo.stock.StockCangkuLevelNameRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/cangku_level_name")
public class StockCangkuLevelNameController {

    @Autowired
    StockCangkuLevelNameRepository stockCangkuLevelNameRepository;


    @PostMapping("findAll")
    @ApiOperation(value = "查询列表", notes = "传入类型")
    public Mono<R> findAll(){
        return stockCangkuLevelNameRepository.findAll().collectList().map(a->R.ok().setResult(a));
    }

    @PostMapping("countByLevelName")
    public Mono<R> countByLevelName(String name){
        return stockCangkuLevelNameRepository.countByLevelName(name).map(a->R.ok().setResult(a));
    }

    @PostMapping("delById")
    public Mono<R> delById(@RequestBody Map map){
        String id=map.get("id").toString();
        List<String> list= Arrays.asList(id.split(","));
        return stockCangkuLevelNameRepository.delById(list).then(Mono.just(R.ok()));
    }

    @PostMapping("save")
    public Mono<R> save(String name){
        StockCangkuLevelName n=new StockCangkuLevelName();
        n.setLevelName(name).setLevelType("2");
        return stockCangkuLevelNameRepository.save(n).map(a->R.ok().setResult(a));
    }
}

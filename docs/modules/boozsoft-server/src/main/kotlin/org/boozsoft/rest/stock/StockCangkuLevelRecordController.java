package org.boozsoft.rest.stock;

import lombok.extern.slf4j.Slf4j;
import org.boozsoft.domain.entity.stock.StockCangkuLevelRecord;
import org.boozsoft.repo.stock.StockCangkuLevelRecordRepository;
import org.boozsoft.service.StockCangkuLevelRecordService;
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
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/cangku_level_record")
public class StockCangkuLevelRecordController {

    @Autowired
    StockCangkuLevelRecordRepository stockCangkuLevelRecordRepository;
    @Autowired
    StockCangkuLevelRecordService stockCangkuLevelRecordService;

    @PostMapping("findAllByRecordBend")
    public Mono<R> findAllByRecordBend(String cangkuId,String parentId){
        return stockCangkuLevelRecordRepository.findAllByRecordBend(cangkuId,parentId,"1").collectList().map(a->R.ok().setResult(a));
    }

    @PostMapping("findAllByCangkuIdAndCangkuLevelIdAndCangkuLevelOrder")
    public Mono<R> findAllByCangkuIdAndCangkuLevelIdAndCangkuLevelOrder(String cangkuId,String cangkuLevelId,String levelOrder){
        return stockCangkuLevelRecordRepository.findAllByCangkuIdAndCangkuLevelIdAndCangkuLevelOrderOrderBySysNum(cangkuId,cangkuLevelId,levelOrder).collectList().map(a->R.ok().setResult(a));
    }

    @PostMapping("findAllByCangkuIdAndCangkuLevelOrderAndParentId")
    public Mono<R> findAllByCangkuIdAndCangkuLevelOrderAndParentId(String cangkuId,String levelOrder,String parentId){
        return stockCangkuLevelRecordRepository.findAllByCangkuIdAndCangkuLevelOrderAndParentId(cangkuId,levelOrder,parentId).collectList().map(a->R.ok().setResult(a));
    }

    @PostMapping("findAllByParentIdOrderBySysNum")
    public Mono<R> findAllByParentId(String parentId){
        return stockCangkuLevelRecordRepository.findAllByParentIdOrderBySysNum(parentId).collectList().map(a->R.ok().setResult(a));
    }

    @PostMapping("findByStockRecordId")
    public Mono<R> findByStockRecordId(String id){
        return stockCangkuLevelRecordRepository.findById(id).map(a->R.ok().setResult(a)).defaultIfEmpty(R.ok().setResult(""));
    }

    @PostMapping("findAllByCangkuIdAndCangkuLevelOrder")
    public Mono<R> findAllByCangkuIdAndCangkuLevelOrder(String cangkuId,String levelOrder){
        return stockCangkuLevelRecordRepository.findAllByCangkuIdAndCangkuLevelOrder(cangkuId,levelOrder).collectList().map(a->R.ok().setResult(a));
    }

    @PostMapping("saveRecord")
    public Mono<R> saveRecord(@RequestBody List<StockCangkuLevelRecord> list){
        List<String> collect = list.stream().map(StockCangkuLevelRecord::getParentId).distinct().collect(Collectors.toList());
       return stockCangkuLevelRecordRepository.saveAll(list).collectList()
           .flatMap(item->{
              return stockCangkuLevelRecordRepository.editByBend("0",collect)
              .then(Mono.just(item));
           }).map(a->R.ok().setResult(a));
    }

    @PostMapping("editRecordByflag")
    public Mono<R> editRecordByflag(@RequestBody Map map){
        String flag=map.get("flag").toString();
        List<String>id=Arrays.asList(map.get("id").toString().split(","));
        return stockCangkuLevelRecordRepository.editByflag(flag,id).then(Mono.just(R.ok()));
    }
    @PostMapping("editBydefaultflag")
    public Mono<R> editBydefaultflag(String flag,String id){
        return stockCangkuLevelRecordRepository.editBydefaultflag(flag,id).then(Mono.just(R.ok()));
    }
    @PostMapping("editByExpalin")
    public Mono<R> editByExpalin(String explain,String id){
        return stockCangkuLevelRecordRepository.editByExpalin(explain,id).then(Mono.just(R.ok()));
    }

    // 获取上下级别名称
    @PostMapping("findCangkuJoinName")
    public Mono<R> findCangkuJoinName(@RequestBody Map map){
        String id=map.get("id")==null?"":map.get("id").toString();
        return stockCangkuLevelRecordService.findCangkuJoinName(id).defaultIfEmpty(R.ok().setResult(""));
    }
}

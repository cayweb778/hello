package org.boozsoft.rest.stock.biandong;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.boozsoft.domain.entity.stock.biandong.StockWarehousingBiandong;
import org.boozsoft.domain.entity.stock.biandong.StockWarehousingsBiandong;
import org.boozsoft.repo.stock.biandong.StockWarehousingBiandongRepository;
import org.boozsoft.repo.stock.biandong.StockWarehousingsBiandongRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/stock/warehousing/biandong")
public class StockWarehousingBianDongController {

    @Autowired
    StockWarehousingBiandongRepository stockWarehousingBiandongRepository;
    @Autowired
    StockWarehousingsBiandongRepository stockWarehousingsBiandongRepository;

    @GetMapping("getBianDongNewCcode")
    public Mono<R> getBianDongNewCcode(String ccode) {
        return stockWarehousingBiandongRepository.findAllByBianDongCcodeLike(ccode+"%").collectList().flatMap(list->{
            String index= String.valueOf(list.size());
            int len=index.length()+1;
            return Mono.just(String.format("%0"+len+"d", list.size()+1));
        }).map(R::ok);
    }

    @PostMapping("saveStockWarehousingPOJO")
    public Mono<R> saveStockWarehousingPOJO(@RequestBody StockWarehousingBiandong pojo) {
        return stockWarehousingBiandongRepository.save(pojo).map(a->R.ok().setResult(a));
    }
    @PostMapping("saveStockWarehousingsListPOJO")
    public Mono<R> saveStockWarehousingsListPOJO(@RequestBody List<StockWarehousingsBiandong> pojo) {
        return stockWarehousingsBiandongRepository.saveAll(pojo).collectList().map(a->R.ok().setResult(a));
    }

    @PostMapping("delStockJoinPojo")
    public Mono<R> delStockJoinPojo(String ccode) {
        Mono a=stockWarehousingBiandongRepository.delStockJoinPojo(ccode+"%");
        Mono b=stockWarehousingsBiandongRepository.delStockJoinPojo(ccode+"%");
        return Mono.zip(a,b).thenReturn(R.ok());
    }

    @PostMapping("findAllByBianDongCcodeLike")
    public Mono<R> findAllByBianDongCcodeLike(String ccode) {
        return stockWarehousingBiandongRepository.findAllByBianDongCcodeLike(ccode+"%").collectList().map(R::ok);
    }

    @PostMapping("findBianDongBillByCondition")
    public Mono<R> findBillByCondition(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        // 单据编码
        String ccode = map.get("ccode")==null?"":map.get("ccode").toString();
        String type = map.get("type").toString();
        String iyear = map.get("iyear").toString();
        String action = map.get("action").toString();
        String currPdId = map.containsKey("curr") ? map.get("curr").toString() : "";
        String bdocumStyle = map.containsKey("bdocumStyle") ? map.get("bdocumStyle").toString() : "";
        return stockWarehousingBiandongRepository.findAllByBillStyleAndIyearOrderByDdateAscCcodeAsc(type, iyear)
                .filter(it->bdocumStyle.equals("")?true:it.getBdocumStyle().equals(bdocumStyle))
                .collectList().cache()
                .flatMap(list -> {
                    if (list.size() == 0) {
                        return Mono.just(R.ok());
                    } else {
                        StockWarehousingBiandong master = null;
                        switch (action) {
                            case "curr":
                                long count = list.stream().filter(a -> a.getCcode().equals(currPdId)).count();
                                if(count>0){
                                    master = list.get((list.stream().map(e -> e.getCcode()).collect(Collectors.toList())).indexOf(currPdId));
                                    break;
                                }
                            case "tail":
                                if(StrUtil.isNotBlank(ccode)){
                                    list=list.stream().filter(a->a.getCcode().equals(ccode)).collect(Collectors.toList());
                                    master = list.get(0);
                                }else{
                                    master = list.get(list.size() - 1);
                                }
                                break;
                            case "prev":
                                if (StrUtil.isBlank(currPdId)) {
                                    master = list.get(0);
                                } else {
                                    int index = (list.stream().map(e -> e.getCcode()).collect(Collectors.toList())).indexOf(currPdId);
                                    index = index == 0 ? 0 : index - 1;
                                    master = list.get(index);
                                }
                                break;
                            case "next":
                                if (StrUtil.isBlank(currPdId)) {
                                    master = list.get(0);
                                } else {
                                    int index = (list.stream().map(e -> e.getCcode()).collect(Collectors.toList())).indexOf(currPdId);
                                    index = index >= list.size() - 1 ? list.size() - 1 : index + 1;
                                    master = list.get(index);
                                }
                                break;
                            default:
                                master = list.get(0);
                                break;
                        }
                        StockWarehousingBiandong finalMaster = master;
                        return stockWarehousingsBiandongRepository.findAllByBillStyleAndCcodeOrderByDdateAscCcodeAscLineIdAsc(master.getBillStyle(), master.getCcode()).collectList().map(enlist -> {
                            if (enlist.size() > 0) {
                                finalMaster.setEntryList(JSON.toJSONString(enlist));
                            }
                            return R.ok(finalMaster);
                        });
                    }
                });
    }

    @PostMapping("findStockWareBianDongByCcode")
    public Mono<R> findStockWareByCcode(String ccode){
        return stockWarehousingBiandongRepository.findByCcode(ccode).map(a->R.ok().setResult(a)).defaultIfEmpty(R.ok().setResult(""));
    }
}

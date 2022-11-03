package org.boozsoft.rest.stock;

import cn.hutool.core.map.MapUtil;
import lombok.extern.slf4j.Slf4j;
import org.boozsoft.domain.entity.stock.StockWuliu;
import org.boozsoft.repo.stock.*;
import org.boozsoft.utils.CollectOfUtils;
import org.reactivestreams.Publisher;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/stockClose")
public class StockPeriodCloseController {

    @Autowired
    private StockWarehousingRepository warehousingRepository;

    @Autowired
    private StockSaleousingRepository saleousingRepository;

    @Autowired
    private StockTakingRepository takingRepository;

    @Autowired
    private StockJhzxRepository jhzxRepository;

    @Autowired
    private StockWuLiuRepository wuLiuRepository;

    @Autowired
    private ArBeginBalanceRepository arBeginBalanceRepository;
    @Autowired
    private ArApYsyfRepository arApYsyfRepository;

    @PostMapping("check")
    public Mono<R> check(@RequestBody Map map) {
        String date = map.get("date").toString();
        String types = map.get("types").toString();
        return Flux.fromIterable(Arrays.asList(types.split(","))).distinct().flatMap(s -> {
            Mono<Map<String, Object>> m = null;
            String type ="";
            switch (s) {
                case ("cg"):
                case ("qtr"):
                case ("db"):
                case ("xt"):
                     type = s.equals("cg")?"CGRKD":s.equals("qtr")?"QTRKD":s.equals("db")?"DBRKD":s.equals("xt")?"XTZHRKD":"";
                    m = warehousingRepository.findAllByBillStyleAndDdateLikeOrderByBcheckAscDdateAscCcodeAsc(type,"%"+date+"%").collectList().map(list->
                           {
                               int size = list.stream().filter(it -> it.getBcheck().equals("1")).collect(Collectors.toList()).size();
                               return MapUtil.of(s,CollectOfUtils.mapof("finish",(list.size() == size)+"","number",((list.size() == size)?size:list.size()-size)+""));
                           }
                   );
                    break;
                case ("pd"):
                    m =  takingRepository.findAllByDdateLikeOrderByBcheckAscDdateAscCcodeAsc("%"+date+"%").collectList().map(list->
                            {
                                int size = list.stream().filter(it -> it.getBcheck().equals("1")).collect(Collectors.toList()).size();
                                return MapUtil.of(s,CollectOfUtils.mapof("finish",(list.size() == size)+"","number",((list.size() == size)?size:list.size()-size)+""));
                            }
                    );
                    break;
                case ("jh"):
                    m =  jhzxRepository.findAllByCmakerDateLikeOrderByBcheckAscCmakerDateAscCcodeAsc("%"+date+"%").collectList().map(list->
                            {
                                int size = list.stream().filter(it -> it.getBcheck().equals("1")).collect(Collectors.toList()).size();
                                return MapUtil.of(s,CollectOfUtils.mapof("finish",(list.size() == size)+"","number",((list.size() == size)?size:list.size()-size)+""));
                            }
                    );
                    break;
                case ("wl"):
                    m =  wuLiuRepository.findAllByDdateLikeOrderByBcheckAscDdateAscCcodeAsc("%"+date+"%").collectList().map(list->
                            {
                                int size = list.stream().filter(it -> it.getBcheck().equals("1")).collect(Collectors.toList()).size();
                                return MapUtil.of(s,CollectOfUtils.mapof("finish",(list.size() == size)+"","number",((list.size() == size)?size:list.size()-size)+""));

                            }
                    );
                    break;
                case ("ck"):
                case ("cl"):
                case ("qtc"):
                    type = s.equals("ck")?"XSCKD":s.equals("qtc")?"QTCKD":"";
                    m = saleousingRepository.findAllByBillStyleAndDdateLikeOrderByBcheckAscDdateAscCcodeAsc(type,"%"+date+"%").collectList().map(list->
                            {
                                int size = list.stream().filter(it -> it.getBcheck().equals("1")).collect(Collectors.toList()).size();
                                return MapUtil.of(s,CollectOfUtils.mapof("finish",(list.size() == size)+"","number",size+""));
                            }
                    );
                    break;
                default:
                    m = Mono.just(MapUtil.of(s, ""));
                    break;
            }
            return m;
        }).collectList().map(list -> {
            Map<String, Object> r = new HashMap<>();
            for (Map<String, Object> s : list) {
                String k = s.keySet().toArray()[0].toString();
                r.put(k, s.get(k));
            }
            return R.ok(r);
        });
    }

    @PostMapping("checkAr")
    public Mono<R> checkAr(@RequestBody Map map) {
        String date = map.get("date").toString();
        String iyear = map.get("iyear").toString();
        String types = map.get("types").toString();
        return Flux.fromIterable(Arrays.asList(types.split(","))).distinct().flatMap(s -> {
            Mono<Map<String, Object>> m = null;
            String type ="";
            switch (s) {
                case ("cg"):
                case ("qtr"):
                case ("db"):
                case ("xt"):
//                    type = s.equals("cg")?"CGRKD":s.equals("qtr")?"QTRKD":s.equals("db")?"DBRKD":s.equals("xt")?"XTZHRKD":"";
                    m = arBeginBalanceRepository.findAllByBillStyleAndIyearOrderByCcode("ar",iyear).collectList().map(list->
                            {
                                int size = list.stream().filter(it -> it.getBcheck().equals("1")).collect(Collectors.toList()).size();
                                return MapUtil.of(s,CollectOfUtils.mapof("finish",(list.size() == size)+"","number",((list.size() == size)?size:list.size()-size)+""));
                            }
                    );
                    break;
                case ("pd"):
                    m =  saleousingRepository.findAllByBillStyleAndDdateLikeOrderByBcheckAscDdateAscCcodeAsc("YSD","%"+date+"%").collectList().map(list->
                            {
                                int size = list.stream().filter(it -> it.getBcheck().equals("1")).collect(Collectors.toList()).size();
                                return MapUtil.of(s,CollectOfUtils.mapof("finish",(list.size() == size)+"","number",((list.size() == size)?size:list.size()-size)+""));
                            }
                    );
                    break;
                case ("jh"):
                    m =  arApYsyfRepository.findAllByBillStyleAndDdateLikeOrderByCcode("SKD","%"+date+"%").collectList().map(list->
                            {
                                int size = list.stream().filter(it -> it.getBcheck().equals("1")).collect(Collectors.toList()).size();
                                return MapUtil.of(s,CollectOfUtils.mapof("finish",(list.size() == size)+"","number",((list.size() == size)?size:list.size()-size)+""));
                            }
                    );
                    break;
                case ("wl"):
                    m =  wuLiuRepository.findAllByDdateLikeOrderByBcheckAscDdateAscCcodeAsc("%"+date+"%").collectList().map(list->
                            {
                                int size = list.stream().filter(it -> it.getBcheck().equals("1")).collect(Collectors.toList()).size();
                                return MapUtil.of(s,CollectOfUtils.mapof("finish",(list.size() == size)+"","number",((list.size() == size)?size:list.size()-size)+""));
                            }
                    );
                    break;
                case ("ck"):
                case ("cl"):
                case ("qtc"):
                    type = s.equals("ck")?"XSCKD":s.equals("qtc")?"QTCKD":"";
                    m = saleousingRepository.findAllByBillStyleAndDdateLikeOrderByBcheckAscDdateAscCcodeAsc(type,"%"+date+"%").collectList().map(list->
                            {
                                int size = list.stream().filter(it -> it.getBcheck().equals("1")).collect(Collectors.toList()).size();
                                return MapUtil.of(s,CollectOfUtils.mapof("finish",(list.size() == size)+"","number",size+""));
                            }
                    );
                    break;
                default:
                    m = Mono.just(MapUtil.of(s, ""));
                    break;
            }
            return m;
        }).collectList().map(list -> {
            Map<String, Object> r = new HashMap<>();
            for (Map<String, Object> s : list) {
                String k = s.keySet().toArray()[0].toString();
                r.put(k, s.get(k));
            }
            return R.ok(r);
        });
    }

    @GetMapping("findPeriodList")
    public Mono<R> findPeriodList(){
        return null;
    }


}

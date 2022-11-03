package org.boozsoft.rest.stock;

import lombok.extern.slf4j.Slf4j;
import org.boozsoft.domain.entity.stock.StockWuliuCsource;
import org.boozsoft.repo.stock.StockWuLiuCsourceRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/stock/wuliuCsource")
public class StockWuLiuCsourceController {

    @Autowired
    private StockWuLiuCsourceRepository csourceRepository;


    @PostMapping("wuliuCsourceSave")
    @Transactional
    public Mono<R> wuliuCsourceSave(@RequestBody StockWuliuCsource obj) {
        return csourceRepository.save(obj).map(a->R.ok().setResult(a));
    }

    @PostMapping("wuliuCsourceDel")
    @Transactional
    public Mono<R> wuliuCsourceDel(@RequestBody Map map) {
        String ccode=map.get("ccode").toString();
        return csourceRepository.wuliuCsourceDel(ccode).then(Mono.just(R.ok()));
    }
}

package org.boozsoft.rest.stock;

import lombok.extern.slf4j.Slf4j;
import org.boozsoft.repo.stock.StockWuLiusRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Slf4j
@RestController
@RequestMapping("/stock/wulius")
public class StockWuLiusController {

    @Autowired
    private StockWuLiusRepository wuLiusRepository;


    // 修改统一出库单子表-累计物流数量
    @PostMapping("editStockSaleousingsIsSumWuliu")
    @Transactional
    public Mono<R> editStockSaleousingsIsSumWuliu(String wuliuNum,String ccode) {
        return wuLiusRepository.editStockSaleousingsIsSumWuliu(new BigDecimal(wuliuNum),ccode).then(Mono.just(R.ok()));
    }
}

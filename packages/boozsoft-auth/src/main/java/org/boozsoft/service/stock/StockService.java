package org.boozsoft.service.stock;

import org.boozsoft.repo.stock.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


@Service
public class StockService {
    @Autowired
    StockRepository stockRepository;

    public Mono<Long> verifyStockIsData(String stockNum){
        return stockRepository.verifyStockIsData(stockNum);
    }
}

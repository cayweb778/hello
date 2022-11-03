package org.boozsoft.repo.stock;

import org.boozsoft.domain.entity.stock.StockWuliuSonghuo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface StockWuLiuSongHuoRepository extends ReactiveCrudRepository<StockWuliuSonghuo, String> {
    Flux<StockWuliuSonghuo> findAllByWuliuCode(String ccode);
    Mono<Void> deleteByWuliuCode(String ccode);

}




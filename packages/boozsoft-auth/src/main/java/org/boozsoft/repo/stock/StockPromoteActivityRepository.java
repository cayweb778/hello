package org.boozsoft.repo.stock;

import org.boozsoft.domain.entity.FaAssetGroup;
import org.boozsoft.domain.entity.stock.StockPromoteActivity;
import org.boozsoft.domain.entity.stock.StockPromotePrice;
import org.boozsoft.domain.vo.stock.StockCustPriceVo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface StockPromoteActivityRepository extends ReactiveCrudRepository<StockPromoteActivity, String> {

    Flux<StockPromoteActivity> findAllByOrderById(Pageable pageable);
    Mono<StockPromoteActivity> findByActivityCodeOrderById(String code);
    Mono<StockPromoteActivity> findByActivityNameOrderById(String bsName);
    Flux<StockPromoteActivity> findByFlagOrderById(String flag);
    Flux<StockPromoteActivity> findAllByFlagAndStartDateLessThanEqualAndStopDateGreaterThanEqualOrderByStopDateAsc(String flag,String date,String date2);

}




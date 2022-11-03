package org.boozsoft.repo.stock;

import cn.hutool.core.util.ArrayUtil;
import org.boozsoft.domain.entity.stock.StockSaleousings;
import org.boozsoft.domain.entity.stock.StockTaking;
import org.boozsoft.domain.entity.stock.StockTransfers;
import org.boozsoft.domain.vo.stock.StockTakingVo;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface StockTransfersRepository extends ReactiveCrudRepository<StockTransfers, String> {

    Flux<StockTransfers> findAllByCcodeOrderByDdateAscCcodeAscLineIdAsc(String ccode);

    Flux<StockTransfers> findByCcode(String ccode);
    @Query("delete from stock_transfers where ccode=:ccode")
    Mono<Void> deleteByCcode(String ccode);

}




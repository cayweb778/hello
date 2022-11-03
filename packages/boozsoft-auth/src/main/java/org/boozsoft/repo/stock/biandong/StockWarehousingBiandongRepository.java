package org.boozsoft.repo.stock.biandong;

import org.boozsoft.domain.entity.stock.biandong.StockWarehousingBiandong;
import org.boozsoft.domain.vo.stock.biandong.StockWarehousingBiandongVo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface StockWarehousingBiandongRepository extends ReactiveCrudRepository<StockWarehousingBiandong, String> {
    @Query("delete from stock_warehousing_biandong where ccode like :ccode ")
    Mono<Void> delStockJoinPojo(String ccode);
    @Query("select * from stock_warehousing_biandong where ccode like :ccode order by ccode")
    Flux<StockWarehousingBiandongVo> findAllByBianDongCcodeLike(String ccode);
    Flux<StockWarehousingBiandong> findAllByBillStyleAndIyearOrderByDdateAscCcodeAsc(String type, String iyear);
    Mono<StockWarehousingBiandong> findByCcode(String ccode);
}




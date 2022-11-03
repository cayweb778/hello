package org.boozsoft.repo.stock;

import org.boozsoft.domain.entity.stock.StockBorrows;
import org.boozsoft.domain.vo.stock.StockWarehousingsVo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface StockBorrowsRepository extends ReactiveCrudRepository<StockBorrows, String> {
    Flux<StockBorrows> findAllByCcode(String code);
    Flux<StockBorrows> findAllByBorrowStyleAndCcodeOrderByCcodeAscLineIdAsc(String type, String code);
    Mono<Void> deleteByCcode(String ccode);

    @Query("update stock_borrows set bcheck=:bcheck,bcheck_time=:bcheckTime,bcheck_user=:bcheckUser where ccode in (:ccode)")
    Mono<Void> updeteStockBorrowBcheckByCcodelist(String bcheck, String bcheckTime, String bcheckUser, List<String> ccode);

    @Query("select  (case\n" +
            "             when s.stock_measurement_type = '单计量' then '1'\n" +
            "             else (select sm.conversion_rate\n" +
            "                   from sys_unit_of_mea_list sm\n" +
            "                   where sm.id = sws.cg_unit_id) end) as conversion_rate\n" +
            "from stock_borrows sws\n" +
            "         left join stock s on s.stock_num = sws.cinvode\n" +
            "where sws.cg_unit_id =:cgUnitId \n" +
            "  and sws.ccode =:ccode ")
    Mono<String> getUnitRate(String cgUnitId, String ccode);

    Mono<StockBorrows> findByLineCode(String lineCode);
}

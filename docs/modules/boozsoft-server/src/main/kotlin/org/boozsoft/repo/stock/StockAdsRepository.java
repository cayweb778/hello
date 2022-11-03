package org.boozsoft.repo.stock;

import org.boozsoft.domain.entity.stock.StockAds;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface StockAdsRepository extends ReactiveCrudRepository<StockAds, String> {
    Flux<StockAds> findAllByAdStyleAndCcodeOrderByDdateAscCcodeAscLineIdAsc(String type, String code);
    Mono<Void> deleteByCcode(String ccode);

    @Query("select  (case\n" +
            "             when s.stock_measurement_type = '单计量' then '1'\n" +
            "             else (select sm.conversion_rate\n" +
            "                   from sys_unit_of_mea_list sm\n" +
            "                   where sm.id = sws.cg_unit_id) end) as conversion_rate\n" +
            "from stock_ads sws\n" +
            "         left join stock s on s.stock_num = sws.cinvode\n" +
            "where sws.cg_unit_id =:cgUnitId \n" +
            "  and sws.ccode =:ccode ")
    Mono<String> getUnitRate(String cgUnitId, String ccode);
}




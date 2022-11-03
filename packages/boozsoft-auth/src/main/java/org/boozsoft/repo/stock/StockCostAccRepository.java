package org.boozsoft.repo.stock;

import io.lettuce.core.dynamic.annotation.Param;
import org.boozsoft.domain.entity.group.GroupStockPeriod;
import org.boozsoft.domain.entity.stock.ArApYsyf;
import org.boozsoft.domain.entity.stock.StockCostAcc;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface StockCostAccRepository extends ReactiveCrudRepository<StockCostAcc, String> {

    @Query("select * from _app_group_stock_period where unique_code = :id and stock_year = :iyear order by stock_month asc")
    Flux<GroupStockPeriod> findAllByIyearAndMonth(String id, String iyear);
    @Query("select * from _app_group_stock_period where iyear = :iyear and imonth = :month order by stock_month asc")
    Flux<StockCostAcc> findAllByIyearAndMonthAnd(String iyear, String month);

}

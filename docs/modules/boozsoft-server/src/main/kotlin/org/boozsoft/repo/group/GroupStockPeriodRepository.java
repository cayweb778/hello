package org.boozsoft.repo.group;

import org.boozsoft.domain.entity.Task;
import org.boozsoft.domain.entity.group.GroupStockPeriod;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface GroupStockPeriodRepository extends ReactiveCrudRepository<GroupStockPeriod, String> {
    Mono<Long> countAllBy();
    Mono<Long> countByStockYearAndIstock(String year,String flag);

    Flux<GroupStockPeriod> findAllByUniqueCode(String code);
    Flux<GroupStockPeriod> findAllByUniqueCodeAndStockYearOrderByStockMonth(String code,String year);
    Mono<GroupStockPeriod> findByUniqueCodeAndStockYearAndStockMonth(String code,String year,String month);

    @Query("select stock_year from _app_group_stock_period where unique_code=:uniqueCode group by stock_year order by stock_year desc")
    Flux<GroupStockPeriod> findYearByUniqueCode(String uniqueCode);

    Flux<GroupStockPeriod> findAllByUniqueCodeAndStockYearInAndStockMonthInOrderByStockYearAscStockMonthAsc(String code, List<String> years, List<String> months);
    @Query("select * from _app_group_stock_period where unique_code=:uniqueCode group by stock_year order by stock_year desc")
    Mono<GroupStockPeriod> findQyDate(String uniqueCode, String s1);


    @Query("select stock_year from _app_group_stock_period where unique_code=:uniqueCode group by stock_year order by stock_year asc")
    Flux<String> findIyearByUniqueCode(String uniqueCode);

    Flux<GroupStockPeriod> findByStockYearOrderByStockMonth(String iyear);

}

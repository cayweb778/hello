package org.boozsoft.repo;

import org.boozsoft.domain.entity.account.Iperiod;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface IperiodRepository extends ReactiveCrudRepository<Iperiod, String> {

    Flux<Iperiod> findByIyearAndBeiyong1NullOrderById(String iyear);

    @Query("SELECT iyear from iperiod  GROUP BY iyear ORDER BY iyear desc ")
    Flux<String> groupByIperiodIyear();

    /********************* Mr. Ye *******************/
    @Query("SELECT date_start from iperiod  WHERE 1=1 and date_start in (:list) and flag =:flag and beiyong1 <> '1' ORDER BY date_start asc")
    Flux<String> findAllByFlagAndDateStartIn(String flag, List<String> list);



    @Query("SELECT * from iperiod  WHERE 1=1 and beiyong1 = '1' and iyear =:year ORDER BY date_start asc")
    Flux<Iperiod> findAllByIyearAndBeiyong1OrderById(String year);
    /********************* Mr. Ye *******************/
    @Query("select count(id) from iperiod where iyear=:iyear and flag=:flag and tenant_id=:databasenum ")
    Mono<Long> countByIyearAndFlag(String iyear,String flag,String databasenum);
}

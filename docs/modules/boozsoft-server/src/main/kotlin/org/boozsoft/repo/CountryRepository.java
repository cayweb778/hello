package org.boozsoft.repo;

import org.boozsoft.domain.entity.SysCountry;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface CountryRepository extends ReactiveCrudRepository<SysCountry, String> {

    @Query("select * from _app_group_sys_country order by num")
    Flux<SysCountry> findAllOrderByNum();
}


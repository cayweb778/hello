package org.boozsoft.repo;

import org.boozsoft.domain.entity.account.AccAgingRange;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.Set;

public interface AccAgingRangeRepository extends ReactiveCrudRepository<AccAgingRange, String> {
    Flux<AccAgingRange> findAllByAgingModelOrderByTotalDayNumberAsc(String model);
    Flux<AccAgingRange> findAllByAccIdAndAgingModelOrderByTotalDayNumberAsc(String accId,String model);
}

package org.boozsoft.repo.stock;

import org.boozsoft.domain.entity.stock.ArApAgingRange;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface ArApAgingRangeRepository extends ReactiveCrudRepository<ArApAgingRange, String> {
    Flux<ArApAgingRange> findAllByAgingModelOrderByTotalDayNumberAsc(String model);
    Flux<ArApAgingRange> findAllByAccIdAndAgingModelOrderByTotalDayNumberAsc(String accId,String model);
}

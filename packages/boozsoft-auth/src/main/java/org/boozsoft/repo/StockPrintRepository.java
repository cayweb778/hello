package org.boozsoft.repo;

import org.boozsoft.domain.entity.StockPrint;
import org.boozsoft.domain.entity.StockPrintHead;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface StockPrintRepository extends ReactiveCrudRepository<StockPrint, String> {
    public Flux<StockPrint> findAllByCcode(String ccode);
}

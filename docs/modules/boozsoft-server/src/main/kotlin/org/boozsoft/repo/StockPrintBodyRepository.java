package org.boozsoft.repo;

import org.boozsoft.domain.entity.StockPrint;
import org.boozsoft.domain.entity.StockPrintBody;
import org.boozsoft.domain.entity.account.AccAgingRange;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.Set;

public interface StockPrintBodyRepository extends ReactiveCrudRepository<StockPrintBody, String> {
    public Flux<StockPrintBody> findAllByCcode(String ccode);
}

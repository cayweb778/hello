package org.boozsoft.repo;

import org.boozsoft.domain.entity.AccClose;
import org.boozsoft.domain.entity.StockJrjcs;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.Set;

public interface StockJrjcsRepository extends ReactiveCrudRepository<StockJrjcs, String> {

}

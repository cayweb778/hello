package org.boozsoft.repo.carryover;

import org.boozsoft.domain.entity.carryovers.AccCarryOverEntry;
import org.boozsoft.domain.entity.carryovers.AccCarryOverEntryFormula;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccCarryOverEntryFormulaRepository extends ReactiveCrudRepository<AccCarryOverEntryFormula, String> {

    @Query("select max(ino_id) ino_id from accvoucher")
    Mono<AccCarryOverEntry> queryMaxInoid();

    Flux<AccCarryOverEntryFormula> findAllByOwnership(String ownership);
}


package org.boozsoft.repo.carryover;

import org.boozsoft.domain.entity.carryovers.AccCarryOverEntryFormulaRollback;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccCarryOverEntryFormulaRollbackRepository extends ReactiveCrudRepository<AccCarryOverEntryFormulaRollback, String> {

    @Query("select max(ino_id) ino_id from accvoucher")
    Mono<AccCarryOverEntryFormulaRollback> queryMaxInoid();

    @Query("SELECT DISTINCT\n" +
            "\tiyear,\n" +
            "\tcsign,\n" +
            "\tdbill_date,\n" +
            "\tino_id,\n" +
            "\t( SELECT cdigest FROM accvoucher acc WHERE acc.ino_id = ac.ino_id LIMIT 1 ),\n" +
            "\t( SELECT SUM ( CAST ( md AS FLOAT ) ) FROM accvoucher acc WHERE acc.ino_id = ac.ino_id ) AS md,\n" +
            "\tac.cbill,\n" +
            "\tcashier,\n" +
            "\tccheck,\n" +
            "\tcbook\n" +
            "FROM\n" +
            "\taccvoucher ac \n" +
            "ORDER BY\n" +
            "\tino_id ASC")
    Flux<AccCarryOverEntryFormulaRollback> findAllAccvoucher();
}


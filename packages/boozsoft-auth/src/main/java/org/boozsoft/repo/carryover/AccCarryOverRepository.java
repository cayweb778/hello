package org.boozsoft.repo.carryover;

import org.boozsoft.domain.entity.carryovers.AccCarryOver;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccCarryOverRepository extends ReactiveCrudRepository<AccCarryOver, String> {

    @Query("select max(ino_id) ino_id from accvoucher")
    Mono<AccCarryOver> queryMaxInoid();

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
    Flux<AccCarryOver> findAllAccvoucher();

    Flux<AccCarryOver> findAllByCarryOverModuleAndKmtypeOrderByOrderNumAsc(String mark, String msg);

    Flux<AccCarryOver> findAllByCarryOverModuleOrderByOrderNumAsc(String mark);

    Mono<AccCarryOver> findFirstByCarryOverModuleAndTemplateNumAndKmtypeOrCarryOverModuleAndTemplateNameAndKmtype(String overModule, String templateNum, String kmtype, String overModule1, String templateName, String kmtype1);

    Mono<AccCarryOver> findFirstByCarryOverModuleAndTemplateNumOrCarryOverModuleAndTemplateName(String overModule, String templateNum, String overModule1, String templateName);

    Mono<AccCarryOver> findFirstByCarryOverModuleOrderByOrderNumDesc(String model);

    Mono<AccCarryOver> getById(String id);
}


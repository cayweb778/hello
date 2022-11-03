package org.boozsoft.repo;

import org.boozsoft.domain.entity.FaCardColumn;
import org.boozsoft.domain.entity.FaProperty;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface FaCardColumnRepository extends ReactiveCrudRepository<FaCardColumn, String> {
    Flux<FaCardColumn> findAllByOrderById();

    Flux<FaCardColumn> findByFlagOrderById(String flag);

    @Query("SELECT * from fa_property WHERE 1=1 and flag = '1' order by peoperty_id Asc")
    Flux<FaCardColumn> findAllDeptCodeOrDeptNameByFlag();

    @Query("SELECT * from fa_property WHERE id = :id or parent_id = :id order by peoperty_id asc")
    Flux<FaCardColumn> findByIdOrderByDeptCode(String id);



}

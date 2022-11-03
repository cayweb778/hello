package org.boozsoft.repo;

import org.boozsoft.domain.entity.FaAddCutMode;
import org.boozsoft.domain.entity.FaProperty;
import org.boozsoft.domain.entity.FaZhejiuMethod;
import org.boozsoft.domain.entity.group.GroupFaProperty;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FaPropertyRepository extends ReactiveCrudRepository<FaProperty, String> {
    Flux<FaProperty> findAllByOrderByPeopertyId();

    Flux<FaProperty> findByFlagOrderByPeopertyId(String flag);

    @Query("SELECT * from fa_property WHERE 1=1 and flag = '1' order by peoperty_id Asc")
    Flux<FaProperty> findAllDeptCodeOrDeptNameByFlag();

    @Query("SELECT * from fa_property WHERE id = :id or parent_id = :id order by peoperty_id asc")
    Flux<FaProperty> findByIdOrderByDeptCode(String id);



}

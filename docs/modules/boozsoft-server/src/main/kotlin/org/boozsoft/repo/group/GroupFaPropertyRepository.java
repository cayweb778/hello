package org.boozsoft.repo.group;

import org.boozsoft.domain.entity.FaProperty;
import org.boozsoft.domain.entity.group.GroupFaClass;
import org.boozsoft.domain.entity.group.GroupFaProperty;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface GroupFaPropertyRepository extends ReactiveCrudRepository<GroupFaProperty, String> {
    Flux<GroupFaProperty> findAllByOrderByPeopertyId();

    @Query("SELECT * from _app_group_fa_property WHERE 1=1 and flag = '1' order by peoperty_id Asc")
    Flux<GroupFaProperty> findAllDeptCodeOrDeptNameByFlag();
}

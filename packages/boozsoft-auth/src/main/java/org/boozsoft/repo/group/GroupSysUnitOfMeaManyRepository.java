package org.boozsoft.repo.group;

import org.boozsoft.domain.entity.SysUnitOfMeaMany;
import org.boozsoft.domain.entity.group.GroupSysUnitOfMeaMany;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.List;

public interface GroupSysUnitOfMeaManyRepository extends ReactiveCrudRepository<GroupSysUnitOfMeaMany, String> {

    @Query("delete from _app_group_sys_unit_of_mea_many where id in (:ids)")
    Mono<Void> deleteByIds(List ids);

}


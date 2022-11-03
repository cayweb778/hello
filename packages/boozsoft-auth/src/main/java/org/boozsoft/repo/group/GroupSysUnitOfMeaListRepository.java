package org.boozsoft.repo.group;

import org.boozsoft.domain.entity.SysUnitOfMeaList;
import org.boozsoft.domain.entity.group.GroupSysUnitOfMeaList;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface GroupSysUnitOfMeaListRepository extends ReactiveCrudRepository<GroupSysUnitOfMeaList, String> {

    Flux<GroupSysUnitOfMeaList> findAllByManyCode(String manyCode);

    @Query("select max(unit_code) from _app_group_sys_unit_of_mea_list ")
    Mono<String> findMaxCode();

    @Query("delete from _app_group_sys_unit_of_mea_list where id in (:ids)")
    Mono<Void> deleteByIds(List ids);

    @Query("select * from _app_group_sys_unit_of_mea_list where many_code in (:codes) ")
    Flux<GroupSysUnitOfMeaList> findAllByManyCodes(List<String> codes);

}


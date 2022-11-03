package org.boozsoft.repo.group;

import org.boozsoft.domain.entity.group.GroupDataSeeSwitch;
import org.boozsoft.domain.entity.group.GroupPermission;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GroupDataSeeSwitchRepository extends ReactiveCrudRepository<GroupDataSeeSwitch, String> {

    Flux<GroupDataSeeSwitch> findAllByUniqueCodeOrderByIdAscUniqueCodeAsc(String uniqueCode);
    Flux<GroupDataSeeSwitch> findAllByUniqueCodeAndDatabaseNameAndIsCtrl(String uniqueCode,String name,String ctrl);
    Mono<GroupDataSeeSwitch> findAllByUniqueCodeAndDatabaseNameAndIsCtrlAndCtrlRange(String uniqueCode,String dataBaseName,String ctrl,String ctrlRange);
}

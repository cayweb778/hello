package org.boozsoft.repo;

import org.boozsoft.domain.entity.group.GroupFaAccAuth;
import org.boozsoft.domain.entity.group.GroupSysAccAuth;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GroupFaAccAuthRepository extends ReactiveCrudRepository<GroupFaAccAuth, String> {

    Flux<GroupFaAccAuth> findAllByUserNumOrderByManageUniqueCodeAsc(String userId);

    Mono<Void> deleteAllByManageUniqueCode(String accId);

}

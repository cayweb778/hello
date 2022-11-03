package org.boozsoft.repo;

import org.boozsoft.domain.entity.group.GroupUserOperatAuth;
import org.boozsoft.domain.entity.group.GroupUserOperatAuthZt;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.List;

public interface GroupUserOperatAuthRepository extends ReactiveCrudRepository<GroupUserOperatAuth, String> {
    Flux<GroupUserOperatAuth> findAllByUsrAuthIdOrderByPlatformMarkAscFunctionIdAsc(String id);
    Flux<GroupUserOperatAuth> findAllByUsrAuthIdAndPlatformMarkOrderByPlatformMarkAscFunctionIdAsc(String id,String mark);
    Flux<GroupUserOperatAuth> findAllByUsrAuthIdInOrderByPlatformMarkAscFunctionIdAsc(List<String> ids);
}

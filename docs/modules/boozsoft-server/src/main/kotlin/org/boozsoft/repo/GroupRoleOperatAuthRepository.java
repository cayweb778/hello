package org.boozsoft.repo;

import org.boozsoft.domain.entity.group.GroupRoleOperatAuth;
import org.boozsoft.domain.entity.group.GroupRoleOperatAuth;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.List;

public interface GroupRoleOperatAuthRepository extends ReactiveCrudRepository<GroupRoleOperatAuth, String> {
    Flux<GroupRoleOperatAuth> findAllByUsrAuthIdOrderByPlatformMarkAscFunctionIdAsc(String id);
    Flux<GroupRoleOperatAuth> findAllByUsrAuthIdAndPlatformMarkOrderByPlatformMarkAscFunctionIdAsc(String id,String mark);
    Flux<GroupRoleOperatAuth> findAllByUsrAuthIdInOrderByPlatformMarkAscFunctionIdAsc(List<String> ids);
}

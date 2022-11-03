package org.boozsoft.repo.group;

import org.boozsoft.domain.entity.group.GroupPermission;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface GroupPermissionRepository extends ReactiveCrudRepository<GroupPermission, String> {

    Flux<GroupPermission> findByUserIdAndOriginIdAndCtypeAndBaseEnName(String userId,String originId,String ctype,String baseEnName);

    Flux<GroupPermission> findByUserIdAndTenantIdAndCtypeAndBaseEnName(String userId,String tenantId,String ctype,String baseEnName);


    Flux<GroupPermission> findByUserIdAndTenantIdOrderById(String userId,String tenantId);
    Flux<GroupPermission> findByUserIdAndOriginIdOrderById(String userId,String originId);

    Flux<GroupPermission> findByUserIdOrderById(String userId);
}

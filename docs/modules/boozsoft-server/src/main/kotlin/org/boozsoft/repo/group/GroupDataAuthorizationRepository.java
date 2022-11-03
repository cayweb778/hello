package org.boozsoft.repo.group;

import org.boozsoft.domain.entity.group.GroupDataAuthorization;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface GroupDataAuthorizationRepository extends ReactiveCrudRepository<GroupDataAuthorization, String> {

    Flux<GroupDataAuthorization> findAllByOperatorIdAndTenantryIdOrderByTenantryIdAsc(String user,String zt);

    Flux<GroupDataAuthorization> findAllByOperatorIdAndTenantryIdAndArchivesIdOrderByTenantryIdAsc(String user,String zt,String name);

    Flux<GroupDataAuthorization> findAllByTenantryIdOrderByTenantryIdAscIyearAsc(String zt);

    Mono<Void> deleteAllByOperatorIdAndTenantryIdAndIyearAndDataIdIn(String user,String zt,String year,List<String> iterable);
}

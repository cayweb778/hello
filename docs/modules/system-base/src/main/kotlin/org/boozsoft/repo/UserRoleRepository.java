package org.boozsoft.repo;


import org.boozsoft.repo.entity.UserRole;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface UserRoleRepository extends ReactiveCrudRepository<UserRole,String> {

    Flux<UserRole> findByUserId(String userId);
    Mono<UserRole> findByUserIdAndRoleId(String id, String roleId);
    Flux<UserRole> findByRoleId(String roleId);
}

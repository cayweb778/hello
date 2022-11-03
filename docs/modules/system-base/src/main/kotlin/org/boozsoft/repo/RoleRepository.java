package org.boozsoft.repo;


import org.boozsoft.repo.entity.Role;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface RoleRepository extends ReactiveCrudRepository<Role,String> {

    Flux<Role> findAllBy();
}

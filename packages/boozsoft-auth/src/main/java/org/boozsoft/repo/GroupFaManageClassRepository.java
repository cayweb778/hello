package org.boozsoft.repo;


import org.boozsoft.domain.entity.group.GroupFaManageClass;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GroupFaManageClassRepository extends ReactiveCrudRepository<GroupFaManageClass, String> {

    Flux<GroupFaManageClass> findAllBySuperiorIdOrderByManageCodeAsc(String id);
}

package org.boozsoft.repo.group;

import org.boozsoft.domain.entity.group.GroupSysPsnType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface GroupSysPsnTypeRepository extends ReactiveCrudRepository<GroupSysPsnType, String> {

    Flux<GroupSysPsnType> findAllByOrderByPsnTypeCode();
}

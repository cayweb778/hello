package org.boozsoft.repo.group;

import org.boozsoft.domain.entity.group.GroupDocumentType;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface GroupDocumentTypeRepository extends ReactiveCrudRepository<GroupDocumentType, String> {

    Flux<GroupDocumentType> findAllByOrderByCcode();

}

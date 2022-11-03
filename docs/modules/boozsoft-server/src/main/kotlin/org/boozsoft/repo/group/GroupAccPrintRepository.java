package org.boozsoft.repo.group;

import org.boozsoft.domain.entity.group.GroupAccPrint;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface GroupAccPrintRepository extends ReactiveCrudRepository<GroupAccPrint, String> {

    Flux<GroupAccPrint> findByAccIdAndMenuName(String accId, String menuName);

}

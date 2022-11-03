package org.boozsoft.repo.group;

import org.boozsoft.domain.entity.group.GroupReportAcc;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface GroupReportAccRepository extends ReactiveCrudRepository<GroupReportAcc, String> {

    Flux<GroupReportAcc> findByAccStandardOrderByNum(String accStandard);

}

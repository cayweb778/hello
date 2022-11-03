package org.boozsoft.repo;

import org.boozsoft.domain.entity.SysDataAuthSwith;
import org.boozsoft.domain.entity.SysTaskFile;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface SysDataAuthSwithRepository extends ReactiveCrudRepository<SysDataAuthSwith, String> {
    Mono<SysDataAuthSwith> findAllByRecordNumAndIyear(String accId,String iyear);
}

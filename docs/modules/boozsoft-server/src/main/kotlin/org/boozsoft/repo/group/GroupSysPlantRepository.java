package org.boozsoft.repo.group;

import org.boozsoft.domain.entity.group.GroupSysPlant;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GroupSysPlantRepository extends ReactiveCrudRepository<GroupSysPlant, String> {
    Mono<Long> countAllBy();

    Flux<GroupSysPlant> findFirstByPlantCode(String orgCode);
    Flux<GroupSysPlant> findFirstByPlantNamej(String orgCode);

    Flux<GroupSysPlant> findAllByCorpUniqueCode(String uniqueCode);

    Mono<Long> countAllByCorpUniqueCode(String uniqueCode);
    Flux<GroupSysPlant> findByCorpUniqueCode(String corpUniqueCode);
}

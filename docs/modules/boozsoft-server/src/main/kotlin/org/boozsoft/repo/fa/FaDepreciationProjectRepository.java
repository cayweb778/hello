package org.boozsoft.repo.fa;

import org.boozsoft.domain.entity.fa.FaDepreciationProject;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FaDepreciationProjectRepository extends ReactiveCrudRepository<FaDepreciationProject, String> {

    Mono<FaDepreciationProject> deleteByManageCodeAndIyearAndImonth(String manageCode, String iyear, String imonth);
    Mono<FaDepreciationProject> deleteByCardUnique(String cardUnique);

    Flux<FaDepreciationProject> findByManageCodeAndIyearAndImonth(String manageCode, String iyear, String imonth);

}

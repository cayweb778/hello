package org.boozsoft.repo.fa;

import org.boozsoft.domain.entity.fa.FaDepreciationDept;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FaDepreciationDeptRepository extends ReactiveCrudRepository<FaDepreciationDept, String> {

    Mono<FaDepreciationDept> deleteByManageCodeAndIyearAndImonth(String manageCode, String iyear, String imonth);
    Mono<FaDepreciationDept> deleteByCardUnique(String cardUnique);

    Flux<FaDepreciationDept> findByManageCodeAndIyearAndImonth(String manageCode, String iyear, String imonth);

}

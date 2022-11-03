package org.boozsoft.repo.fa;

import org.boozsoft.domain.entity.fa.FaPandianMaster;
import org.boozsoft.domain.entity.fa.FaPandianSub;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface FaPandianSubRepository extends ReactiveCrudRepository<FaPandianSub, String> {

    Flux<FaPandianSub> findAllByIyearAndImonthOrderByIdAsc(String year,String month);

    Flux<FaPandianSub> findAllBySuperiorIdOrderByIdAsc(String  code);
}

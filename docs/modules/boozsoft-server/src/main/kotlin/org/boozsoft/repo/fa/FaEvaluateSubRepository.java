package org.boozsoft.repo.fa;

import org.boozsoft.domain.entity.fa.FaEvaluateMaster;
import org.boozsoft.domain.entity.fa.FaEvaluateSub;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FaEvaluateSubRepository extends ReactiveCrudRepository<FaEvaluateSub, String> {
    Flux<FaEvaluateSub> findAllBySuperiorId(String id);

    Flux<FaEvaluateSub> findAllBySuperiorIdOrderByIyearDescImonthDesc(String id);

    Flux<FaEvaluateSub> findAllByIyearAndImonthOrderByIyearDescImonthDesc(String year,String month);
}

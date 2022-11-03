package org.boozsoft.repo.fa;

import org.boozsoft.domain.entity.fa.FaChangeHead;
import org.boozsoft.domain.entity.fa.FaEvaluateMaster;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FaEvaluateMasterRepository extends ReactiveCrudRepository<FaEvaluateMaster, String> {
    Mono<FaEvaluateMaster> findFirstByPgDateLikeOrderByPgIdDesc(String s);

    Flux<FaEvaluateMaster> findAllByManageCodeOrderByPgDateAscPgIdAsc(String code);

    Flux<FaEvaluateMaster> findAllByManageCodeAndPgDateLikeOrderByPgDateAscPgIdAsc(String code, String s);
}

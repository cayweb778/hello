package org.boozsoft.repo.fa;

import org.boozsoft.domain.entity.fa.FaAssetsMinusMaster;
import org.boozsoft.domain.entity.fa.FaChange;
import org.boozsoft.domain.entity.fa.FaChangeHead;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FaChangeHeadRepository extends ReactiveCrudRepository<FaChangeHead, String> {

    Mono<FaChangeHead> findFirstByChangeDateLikeOrderByChangeCodeDesc(String s);

    Flux<FaChangeHead> findAllByManageCodeAndIyearAndImonthOrderByChangeDateAsc(String code,String year,String month);

    Flux<FaChangeHead> findAllByManageCodeOrderByChangeDateAscChangeCodeAsc(String code);

    Flux<FaChangeHead> findAllByManageCodeAndChangeDateLikeOrderByChangeDateAscChangeCodeAsc(String code, String s);
}

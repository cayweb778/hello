package org.boozsoft.repo.fa;

import org.boozsoft.domain.entity.fa.FaChange;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FaChangeRepository extends ReactiveCrudRepository<FaChange, String> {

    Mono<FaChange> findByCardUniqueOrderByCdate(String cardUnique);
    Mono<FaChange> findFirstByCardUniqueOrderByCdateDesc(String cardUnique);
    Mono<FaChange> findFirstByCardUniqueAndCdateLessThanOrderByCdateDesc(String cardUnique,String date);
    Mono<FaChange> findFirstByCardUniqueAndCdateLessThanEqualOrderByCdateDesc(String cardUnique,String date);
    Flux<FaChange> findAllBySuperiorIdLikeOrderByCdateDesc(String cardUnique);
    Flux<FaChange> findAllBySuperiorIdNotLikeOrderByCdateDesc(String cardUnique);
    Mono<FaChange> deleteByCardUnique(String cardUnique);
    Mono<FaChange> deleteByCardUniqueAndIyearAndImonth(String cardUnique,String iyear,String imonth);


}

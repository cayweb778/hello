package org.boozsoft.repo.origin;

import org.boozsoft.domain.entity.origin.OriginFuzhuHesuan;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OriginFuzhuHesuanRepository extends ReactiveCrudRepository<OriginFuzhuHesuan, String> {

    Flux<OriginFuzhuHesuan> findAllByOriginIdOrderByCdfine(Pageable pageable,String originId);
    Flux<OriginFuzhuHesuan> findAllByOriginIdOrderByCdfine(String originId);

    Flux<OriginFuzhuHesuan> findByFlagAndOriginIdOrderByCdfine(String flag,String originId);

    Mono<Long> countAllByOriginId(String originId);

    Flux<OriginFuzhuHesuan> findByCcodeAndOriginIdOrderByCdfine(String ccode,String originId);

    Flux<OriginFuzhuHesuan> findByCnameAndOriginIdOrderByCdfine(String cname,String originId);

    Flux<OriginFuzhuHesuan> findByCankaoDuixiangAndOriginIdOrderByCdfine(String cankaoDuixiang,String originId);

}

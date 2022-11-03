package org.boozsoft.repo.group;

import org.boozsoft.domain.entity.group.GroupFuzhuHesuan;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GroupFuzhuHesuanRepository extends ReactiveCrudRepository<GroupFuzhuHesuan, String> {

    Flux<GroupFuzhuHesuan> findAllByOrderByCdfine(Pageable pageable);
    Flux<GroupFuzhuHesuan> findAllByOrderByCdfine();

    Flux<GroupFuzhuHesuan> findByFlagOrderByCdfine(String flag);

    Mono<Long> countAllBy();

    Flux<GroupFuzhuHesuan> findByCcodeOrderByCdfine(String ccode);

    Flux<GroupFuzhuHesuan> findByCnameOrderByCdfine(String cname);

    Flux<GroupFuzhuHesuan> findByCankaoDuixiangOrderByCdfine(String cankaoDuixiang);

}

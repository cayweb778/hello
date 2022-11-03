package org.boozsoft.repo.group;

import org.boozsoft.domain.entity.group.GroupStockAccount;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface GroupStockAccountRepository extends ReactiveCrudRepository<GroupStockAccount, String> {
    Mono<Long> countAllBy();
    Mono<Long> countAllByUniqueCode(String code);
    Flux<GroupStockAccount> findAllByStockAccName(String trim);
    Flux<GroupStockAccount> findAllByCoCode(String trim);
    Flux<GroupStockAccount> findAllByFlagOrderByCoCodeAsc(String trim);
    Flux<GroupStockAccount> findAllByFlagAndCoCodeInOrderByCoCodeAsc(String trim, List<String> ids);

    Flux<GroupStockAccount> findAllByUniqueCodeIn(List<String> ids);
    Mono<GroupStockAccount> findByStockAccId(String accId);

}

package org.boozsoft.repo.group;


import org.boozsoft.domain.entity.group.GroupUsedForeignCurrency;
import org.boozsoft.domain.entity.group.SysLogger;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface GroupUsedForeignCurrencyRepository extends ReactiveCrudRepository<GroupUsedForeignCurrency, String> {
    Flux<GroupUsedForeignCurrency> findAllByForeignCode(String code);
    Flux<GroupUsedForeignCurrency> findAllByForeignName(String name);
}


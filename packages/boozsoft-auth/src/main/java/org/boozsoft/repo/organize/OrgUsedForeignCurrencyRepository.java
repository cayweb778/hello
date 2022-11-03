package org.boozsoft.repo.organize;


import org.boozsoft.domain.entity.group.GroupUsedForeignCurrency;
import org.boozsoft.domain.entity.organize.OrgUsedForeignCurrency;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface OrgUsedForeignCurrencyRepository extends ReactiveCrudRepository<OrgUsedForeignCurrency, String> {
    Flux<OrgUsedForeignCurrency> findAllByForeignCodeAndOrgUniqueCode(String code,String orgUniqueCode);
    Flux<OrgUsedForeignCurrency> findAllByForeignNameAndOrgUniqueCode(String name,String orgUniqueCode);
    Flux<OrgUsedForeignCurrency> findAllByOrgUniqueCode(String orgUniqueCode);
}


package org.boozsoft.service.group;

import org.boozsoft.domain.entity.UsedForeignCurrency;
import org.boozsoft.domain.entity.group.GroupUsedForeignCurrency;
import org.boozsoft.domain.entity.organize.OrgUsedForeignCurrency;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

import org.springframework.data.domain.Pageable;

public interface GroupUsedForeignCurrencyService {
    Flux<GroupUsedForeignCurrency> findAll(Pageable pageable);

    Mono<GroupUsedForeignCurrency> findById(String id);

    Mono<GroupUsedForeignCurrency> save(GroupUsedForeignCurrency entity);

    Mono<Void> deleteById(String id);

    Flux<GroupUsedForeignCurrency> duplicateCheckQuery(String value, String type);

    Flux<UsedForeignCurrency> findAllAcc(Pageable pageable);

    Mono<UsedForeignCurrency> findByIdAcc(String id);

    Mono<UsedForeignCurrency> saveAcc(UsedForeignCurrency entity);

    Mono<Void> deleteByIdAcc(String id);

    Flux<UsedForeignCurrency> duplicateCheckQueryAcc(String value, String type);

    Flux<OrgUsedForeignCurrency> findAvailablesOrg(String uniqueCode);

    Flux<OrgUsedForeignCurrency> findAllOrg(Pageable pageable);

    Mono<OrgUsedForeignCurrency> findByIdOrg(String id);

    Mono<OrgUsedForeignCurrency>  saveOrg(OrgUsedForeignCurrency entity);

    Mono<Void> deleteByIdOrg(String id);

    Flux<OrgUsedForeignCurrency> duplicateCheckQueryOrg(String value, String type, String uniqueCode);

    Flux<UsedForeignCurrency> findAvailablesAcc();
}
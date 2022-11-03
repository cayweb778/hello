package org.boozsoft.service;


import org.boozsoft.domain.entity.group.GroupFaAccount;
import org.boozsoft.domain.entity.group.GroupStockAccount;
import org.boozsoft.domain.entity.group.GroupSysAccount;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface DatabaseInitService {
    Mono<Integer> insertAccountsData(GroupSysAccount sysAccount, List<String> supers);
    Mono<Integer> removeAccountsData(GroupSysAccount sysAccount);
    Mono<Integer> copyAccountsData(GroupSysAccount dbEntity, GroupSysAccount dbIt, List<String> supers);

    Mono<Integer> resetKemuCode(GroupSysAccount entity);

    Mono<Integer> insertFaAccountsData(GroupFaAccount dbEntity);
    Mono<Integer> removeFaAccountsData(GroupFaAccount sysAccount);

    Mono<String> insertStockAccountsData(GroupStockAccount dbEntity);
    Mono<Integer> removeStockAccountsData(GroupStockAccount dbEntity);
}

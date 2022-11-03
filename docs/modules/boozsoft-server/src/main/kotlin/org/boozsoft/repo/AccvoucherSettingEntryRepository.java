package org.boozsoft.repo;

import org.boozsoft.domain.entity.account.AccvoucherSettingEntry;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface AccvoucherSettingEntryRepository extends ReactiveCrudRepository<AccvoucherSettingEntry, String> {

    Flux<AccvoucherSettingEntry> findAllByParentIdOrderByNum(String parentId);
    Flux<AccvoucherSettingEntry> deleteByParentId(String parentId);

}

package org.boozsoft.repo.group;

import org.boozsoft.domain.entity.group.GroupCodeKemu;
import org.boozsoft.domain.entity.group.GroupCodeKemuCountry;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GroupCodeKemuCountryRepository extends ReactiveCrudRepository<GroupCodeKemuCountry, String> {
    Flux<GroupCodeKemuCountry> findAllByUniqueAccStandardAndTemplateIdOrderByCcodeAsc(String uniqueAccStandard, String templateId);
    Flux<GroupCodeKemuCountry> findAllByUniqueAccStandardAndTemplateIdAndCclassOrderByCcodeAsc(String uniqueAccStandard, String templateId, String cclass);

    @Query("delete from __app_group_code_kemu_country WHERE unique_acc_standard=:uniqueAccStandard and template_id=:templateId")
    Mono<Void> delByStandardAndTemplate(String uniqueAccStandard, String templateId);

    Flux<GroupCodeKemuCountry> findAllByUniqueAccStandardOrderByCcode(String accStandard);
}


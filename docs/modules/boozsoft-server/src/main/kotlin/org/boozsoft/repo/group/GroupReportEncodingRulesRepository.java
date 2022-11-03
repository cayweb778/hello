package org.boozsoft.repo.group;


import org.boozsoft.domain.entity.group.GroupFileEncodingRules;
import org.boozsoft.domain.entity.group.GroupReportEncodingRules;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface GroupReportEncodingRulesRepository extends ReactiveCrudRepository<GroupReportEncodingRules, String> {

    Mono<GroupReportEncodingRules> findByFileType(String type);

}

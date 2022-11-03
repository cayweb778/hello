package org.boozsoft.repo.group;


import org.boozsoft.domain.entity.group.GroupFileEncodingRules;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GroupFileEncodingRulesRepository extends ReactiveCrudRepository<GroupFileEncodingRules, String> {

    Mono<GroupFileEncodingRules> findByFileType(String type);

}

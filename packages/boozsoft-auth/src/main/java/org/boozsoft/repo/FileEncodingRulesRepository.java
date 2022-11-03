package org.boozsoft.repo;


import org.boozsoft.domain.entity.FileEncodingRules;
import org.boozsoft.domain.entity.group.GroupFileEncodingRules;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface FileEncodingRulesRepository extends ReactiveCrudRepository<FileEncodingRules, String> {

    Mono<FileEncodingRules> findByFileType(String type);

}

package org.boozsoft.repo;


import org.boozsoft.domain.entity.FileEncodingRules;
import org.boozsoft.domain.entity.ReportEncodingRules;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface ReportEncodingRulesRepository extends ReactiveCrudRepository<ReportEncodingRules, String> {

    @Query("select * from report_encoding_rules where file_type =:type")
    Mono<ReportEncodingRules> findByFileType(String type);

}

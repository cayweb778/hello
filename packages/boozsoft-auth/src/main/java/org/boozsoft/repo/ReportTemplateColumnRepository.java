package org.boozsoft.repo;

import org.boozsoft.domain.entity.share.ReportTemplateColumn;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ReportTemplateColumnRepository extends ReactiveCrudRepository<ReportTemplateColumn, String> {

    Flux<ReportTemplateColumn> findByTemplateIdOrderByXuhao(String templateId);
    Flux<ReportTemplateColumn> findByTemplateIdAndColumnTypeOrderByXuhao(String templateId, String columnType);
    Mono<ReportTemplateColumn> deleteByTemplateId(String templateId);

}

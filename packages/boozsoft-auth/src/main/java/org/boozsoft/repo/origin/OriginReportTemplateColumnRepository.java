package org.boozsoft.repo.origin;

import org.boozsoft.domain.entity.origin.OriginReportTemplateColumn;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OriginReportTemplateColumnRepository extends ReactiveCrudRepository<OriginReportTemplateColumn, String> {

    Flux<OriginReportTemplateColumn> findByTemplateIdOrderByXuhao(String templateId);
    Flux<OriginReportTemplateColumn> findByTemplateIdAndColumnTypeOrderByXuhao(String templateId, String columnType);
    Mono<OriginReportTemplateColumn> deleteByTemplateId(String templateId);

}

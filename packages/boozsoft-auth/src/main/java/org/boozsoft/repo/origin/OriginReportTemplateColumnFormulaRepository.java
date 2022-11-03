package org.boozsoft.repo.origin;

import org.boozsoft.domain.entity.origin.OriginReportTemplateColumnFormula;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OriginReportTemplateColumnFormulaRepository extends ReactiveCrudRepository<OriginReportTemplateColumnFormula, String> {

    Flux<OriginReportTemplateColumnFormula> findByColumnIdOrderById(String columnId);
    Flux<OriginReportTemplateColumnFormula> findByTemplateIdOrderById(String templateId);
    Mono<OriginReportTemplateColumnFormula> deleteByColumnId(String columnId);
    Mono<OriginReportTemplateColumnFormula> deleteByTemplateId(String templateId);

}

package org.boozsoft.repo.group;

import org.boozsoft.domain.entity.group.GroupReportTemplateColumnFormula;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GroupReportTemplateColumnFormulaRepository extends ReactiveCrudRepository<GroupReportTemplateColumnFormula, String> {

    Flux<GroupReportTemplateColumnFormula> findByColumnIdOrderById(String columnId);
    Flux<GroupReportTemplateColumnFormula> findByTemplateIdOrderById(String templateId);
    Mono<GroupReportTemplateColumnFormula> deleteByColumnId(String columnId);
    Mono<GroupReportTemplateColumnFormula> deleteByTemplateId(String templateId);

}

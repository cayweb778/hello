package org.boozsoft.repo.group;

import org.boozsoft.domain.entity.group.GroupReportTemplateColumn;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GroupReportTemplateColumnRepository extends ReactiveCrudRepository<GroupReportTemplateColumn, String> {

    Flux<GroupReportTemplateColumn> findByTemplateIdOrderByXuhao(String templateId);
    Flux<GroupReportTemplateColumn> findByTemplateIdAndColumnTypeOrderByXuhao(String templateId, String columnType);
    Mono<GroupReportTemplateColumn> deleteByTemplateId(String templateId);

}

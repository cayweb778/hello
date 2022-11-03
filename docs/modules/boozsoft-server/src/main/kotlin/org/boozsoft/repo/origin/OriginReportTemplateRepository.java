package org.boozsoft.repo.origin;

import org.boozsoft.domain.entity.origin.OriginReportTemplate;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface OriginReportTemplateRepository extends ReactiveCrudRepository<OriginReportTemplate, String> {

    Flux<OriginReportTemplate> findByReportNameAndOriginIdOrderByNum(String reportName,String originId);

    Flux<OriginReportTemplate> findByReportNameAndFlagAndOriginIdOrderByNum(String reportName, String flag,String originId);

    Flux<OriginReportTemplate> findByTemplateNameAndOriginIdOrderByNum(String templateName,String originId);

    Flux<OriginReportTemplate> findByReportNameAndTitleNameAndOriginIdOrderByNum(String reportName, String titleName,String originId);

    Flux<OriginReportTemplate> findByNumAndOriginIdOrderByNum(Integer num,String originId);

    Flux<OriginReportTemplate> findByAccStandardAndReportNameAndOriginIdOrderByNum(String accStandard, String reportName,String originId);

}

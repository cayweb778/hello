package org.boozsoft.repo;

import org.boozsoft.domain.entity.share.ReportTemplate;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface ReportTemplateRepository extends ReactiveCrudRepository<ReportTemplate, String> {

    Flux<ReportTemplate> findByReportNameOrderByNum(String reportName);
    Flux<ReportTemplate> findByReportNameAndFlagOrderByNum(String reportName, String flag);
    Flux<ReportTemplate> findByTemplateNameOrderByNum(String templateName);
    Flux<ReportTemplate> findByReportNameAndTitleNameOrderByNum(String reportName, String titleName);
    Flux<ReportTemplate> findByNumOrderByNum(Integer num);

    Flux<ReportTemplate> findBySysFlagOrderByNum(String sysFlag);

}

package org.boozsoft.repo.group;

import org.boozsoft.domain.entity.group.GroupReportTemplate;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface GroupReportTemplateRepository extends ReactiveCrudRepository<GroupReportTemplate, String> {

    Flux<GroupReportTemplate> findByReportNameOrderByNum(String reportName);

    Flux<GroupReportTemplate> findByReportNameAndFlagOrderByNum(String reportName, String flag);

    Flux<GroupReportTemplate> findByTemplateNameOrderByNum(String templateName);

    Flux<GroupReportTemplate> findByReportNameAndTitleNameOrderByNum(String reportName, String titleName);

    Flux<GroupReportTemplate> findByNumOrderByNum(Integer num);

    Flux<GroupReportTemplate> findByAccStandardAndReportNameOrderByNum(String accStandard, String reportName);

}

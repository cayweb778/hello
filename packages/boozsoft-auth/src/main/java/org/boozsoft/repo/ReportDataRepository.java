package org.boozsoft.repo;

import org.boozsoft.domain.entity.share.ReportData;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface ReportDataRepository extends ReactiveCrudRepository<ReportData, String> {

    Flux<ReportData> findByReportNameAndIyearOrderByDataCode(String reportName,String iyear);
    Flux<ReportData> findByTemplateIdOrderById(String templateId);
    Flux<ReportData> findByTemplateIdAndDataTypeAndIyearOrderById(String templateId, String dataType,String iyear);
    Flux<ReportData> findByTemplateIdAndDataTypeAndIyearAndJiduOrderById(String templateId, String dataType,String iyear,String jidu);
    Flux<ReportData> findByTemplateIdAndDataTypeAndIyearAndIperiodOrderById(String templateId, String dataType,String iyear,String iperiod);

}

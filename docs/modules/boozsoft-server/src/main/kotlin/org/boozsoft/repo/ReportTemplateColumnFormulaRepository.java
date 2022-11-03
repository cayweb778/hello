package org.boozsoft.repo;

import io.lettuce.core.dynamic.annotation.Param;
import org.boozsoft.domain.entity.share.ReportTemplateColumnFormula;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ReportTemplateColumnFormulaRepository extends ReactiveCrudRepository<ReportTemplateColumnFormula, String> {

    Flux<ReportTemplateColumnFormula> findByColumnIdOrderById(String columnId);

    Flux<ReportTemplateColumnFormula> findByTemplateIdOrderById(String templateId);

    Mono<ReportTemplateColumnFormula> deleteByColumnId(String columnId);

    Mono<ReportTemplateColumnFormula> deleteByTemplateId(String templateId);

    @Query("select a.template_id,a.column_id,a.ccode,a.fuhao,a.fangxiang, " +
            "sum(cast(COALESCE(md,'0') as decimal)) as beiyong1,sum(cast(COALESCE(mc,'0') as decimal)) as beiyong2 " +
            "from report_template_column_formula a " +
            "left join accvoucher as b on b.ccode like a.ccode and b.iyperiod between :iyperiod1 and :iyperiod2 and (ifrag='0' or ifrag is null) " +
            "where a.template_id = :templateId " +
            "group by a.template_id,a.column_id,a.ccode,a.fuhao,a.fangxiang")
    Flux<ReportTemplateColumnFormula> findByAccvoucherOrderAndTemplateId(@Param("templateId") String templateId, @Param("iyperiod1") String iyperiod1, @Param("iyperiod2") String iyperiod2);

    @Query("select a.template_id,a.column_id,a.ccode,a.fuhao,a.fangxiang, " +
            "sum(cast(COALESCE(md,'0') as decimal)) as beiyong1,sum(cast(COALESCE(mc,'0') as decimal)) as beiyong2 " +
            "from report_template_column_formula a " +
            "left join accvoucher as b on b.ccode like a.ccode and b.iyperiod between :iyperiod1 and :iyperiod2 and (ifrag='0' or ifrag is null) and ibook='1' " +
            "where a.template_id = :templateId " +
            "group by a.template_id,a.column_id,a.ccode,a.fuhao,a.fangxiang")
    Flux<ReportTemplateColumnFormula> findByAccvoucherOrderAndTemplateIdAndIbook(@Param("templateId") String templateId, @Param("iyperiod1") String iyperiod1, @Param("iyperiod2") String iyperiod2);

    @Query("select a.template_id,a.column_id,a.ccode,a.fuhao,a.fangxiang, " +
            "sum(cast(COALESCE(money,'0') as decimal)) as beiyong1 " +
            "from report_template_column_formula a " +
            "left join cash_flow as b on a.ccode=b.project_code and iyear = :iyear and b.iperiod between :iyperiod1 and :iyperiod2 " +
            "where a.template_id = :templateId " +
            "group by a.template_id,a.column_id,a.ccode,a.fuhao,a.fangxiang")
    Flux<ReportTemplateColumnFormula> findByCashOrderAndTemplateId(@Param("templateId") String templateId, @Param("iyear") String iyear, @Param("iyperiod1") String iyperiod1, @Param("iyperiod2") String iyperiod2);

}

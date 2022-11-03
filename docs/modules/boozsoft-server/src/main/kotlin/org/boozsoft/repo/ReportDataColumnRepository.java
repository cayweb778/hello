package org.boozsoft.repo;

import org.boozsoft.domain.entity.share.ReportDataColumn;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ReportDataColumnRepository extends ReactiveCrudRepository<ReportDataColumn, String> {

    Flux<ReportDataColumn> findByDataIdOrderByXuhao(String dataId);
    Flux<ReportDataColumn> findByDataIdAndColumnTypeOrderByXuhao(String dataId, String columnType);
    Mono<ReportDataColumn> deleteByDataId(String dataId);

}

package org.boozsoft.repo;

import org.boozsoft.domain.entity.account.BankTemplateColumn;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BankTemplateColumnRepository extends ReactiveCrudRepository<BankTemplateColumn, String> {

    Flux<BankTemplateColumn> findByTemplateIdOrderById(String templateId);

    Mono<BankTemplateColumn> deleteByTemplateId(String templateId);

}

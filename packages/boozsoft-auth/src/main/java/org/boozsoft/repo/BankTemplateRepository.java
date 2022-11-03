package org.boozsoft.repo;

import org.boozsoft.domain.entity.account.BankTemplate;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface BankTemplateRepository extends ReactiveCrudRepository<BankTemplate, String> {

    Flux<BankTemplate> findAllByOrderById(Pageable pageable);

    Flux<BankTemplate> findByTemplateNameOrderById(String templateName);

    Flux<BankTemplate> findByFlagOrderById(String flag);

}

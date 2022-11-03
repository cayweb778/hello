package org.boozsoft.repo;

import org.boozsoft.domain.entity.SysVoucherImport;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SysVoucherImportRepository extends ReactiveCrudRepository<SysVoucherImport, String> {
    Mono<SysVoucherImport> findById(Long id);

    @Override
    <S extends SysVoucherImport> Mono<S> save(S s);

    Mono<SysVoucherImport> findFirstByOrderByTemplateNumberDesc();
    Flux<SysVoucherImport> findAllByOrderByTemplateNumberAsc();

}


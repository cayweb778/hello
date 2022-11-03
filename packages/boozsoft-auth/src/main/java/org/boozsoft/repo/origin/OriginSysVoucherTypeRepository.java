package org.boozsoft.repo.origin;

import org.boozsoft.domain.entity.origin.OriginSysVoucherType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OriginSysVoucherTypeRepository extends ReactiveCrudRepository<OriginSysVoucherType, String> {

    Flux<OriginSysVoucherType> findAllByOrderById(Pageable pageable);
    Flux<OriginSysVoucherType> findAllByOrderById();
    Mono<OriginSysVoucherType> findByVoucherNumOrderById(String voucherNum);
    Mono<OriginSysVoucherType> findByVoucherTypeCodeOrderById(String voucherTypeCode);
    Mono<OriginSysVoucherType> findByVoucherTypeNameOrderById(String voucherTypeName);

}

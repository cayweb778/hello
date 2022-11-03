package org.boozsoft.repo.group;

import org.boozsoft.domain.entity.group.GroupSysVoucherType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GroupSysVoucherTypeRepository extends ReactiveCrudRepository<GroupSysVoucherType, String> {

    Flux<GroupSysVoucherType> findAllByOrderById(Pageable pageable);
    Flux<GroupSysVoucherType> findAllByOrderById();
    Mono<GroupSysVoucherType> findByVoucherNumOrderById(String voucherNum);
    Mono<GroupSysVoucherType> findByVoucherTypeCodeOrderById(String voucherTypeCode);
    Mono<GroupSysVoucherType> findByVoucherTypeNameOrderById(String voucherTypeName);

}

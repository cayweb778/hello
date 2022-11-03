package org.boozsoft.repo;

import org.boozsoft.domain.entity.account.VoucherType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface VoucherTypeRepository extends ReactiveCrudRepository<VoucherType, String> {

    Flux<VoucherType> findAllByOrderById(Pageable pageable);
    Flux<VoucherType> findAllByOrderById();
    Flux<VoucherType> findByVoucherNumOrderById(String voucherNum);
    Flux<VoucherType> findByVoucherTypeCodeOrderById(String voucherTypeCode);
    Flux<VoucherType> findByVoucherTypeNameOrderById(String voucherTypeName);

    @Modifying
    @Query("delete from sys_voucher_type where tenant_id =:id ")
    Mono<Void> deleteAllByTenantId(String id);
}

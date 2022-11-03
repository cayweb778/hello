package org.boozsoft.repo;

import org.boozsoft.domain.entity.SysVoucherImportEntry;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SysVoucherImportEntryRepository extends ReactiveCrudRepository<SysVoucherImportEntry, String> {
    Mono<SysVoucherImportEntry> findById(int id);

    @Query("SELECT * FROM _app_group_sys_voucher_import_entry WHERE unique_code= :id")
    Flux<SysVoucherImportEntry> findAllByUniqueCodeOrderById(String id);

}


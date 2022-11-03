package org.boozsoft.repo;

import org.boozsoft.domain.entity.AuditSupplier;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface AuditSupplierRepository extends ReactiveCrudRepository<AuditSupplier, String> {

}

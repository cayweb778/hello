package org.boozsoft.repo;

import org.boozsoft.domain.entity.AuditCustomer;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface AuditCustomerRepository extends ReactiveCrudRepository<AuditCustomer, String> {

}

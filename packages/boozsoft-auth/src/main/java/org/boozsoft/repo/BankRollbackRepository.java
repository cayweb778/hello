package org.boozsoft.repo;

import org.boozsoft.domain.entity.group.BankRollback;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface BankRollbackRepository extends ReactiveCrudRepository<BankRollback, String> {
}

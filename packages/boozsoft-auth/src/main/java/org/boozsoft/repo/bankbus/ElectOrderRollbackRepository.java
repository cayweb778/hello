package org.boozsoft.repo.bankbus;

import org.boozsoft.domain.entity.account.bankbus.ElectOrderRollback;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ElectOrderRollbackRepository extends ReactiveCrudRepository<ElectOrderRollback, String> {
}


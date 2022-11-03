package org.boozsoft.repo;

import org.boozsoft.domain.entity.account.BankStatementRollback;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface BankStatementRollbackRepository extends ReactiveCrudRepository<BankStatementRollback, String> {
}

package org.boozsoft.repo.bankbus;

import org.boozsoft.domain.entity.account.bankbus.BankCheckRollback;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface BankCheckRollbackRepository extends ReactiveCrudRepository<BankCheckRollback, String> {
}

package org.boozsoft.repo.bankbus;

import org.boozsoft.domain.entity.account.bankbus.BankBillRollback;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface BankBillRollbackRepository extends ReactiveCrudRepository<BankBillRollback, String> {
}


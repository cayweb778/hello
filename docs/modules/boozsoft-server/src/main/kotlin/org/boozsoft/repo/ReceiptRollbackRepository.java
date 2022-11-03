package org.boozsoft.repo;

import org.boozsoft.domain.entity.account.ReceiptRollback;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ReceiptRollbackRepository extends ReactiveCrudRepository<ReceiptRollback,String> {
}

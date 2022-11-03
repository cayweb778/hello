package org.boozsoft.repo;

import org.boozsoft.domain.entity.account.ReceiptHeaderRollback;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ReceiptHeaderRollbackRepository extends ReactiveCrudRepository<ReceiptHeaderRollback,String> {
}

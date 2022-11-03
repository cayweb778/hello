package org.boozsoft.repo;

import org.boozsoft.domain.entity.account.ApprovalDocRollback;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ApprovalDocRollbackRepository extends ReactiveCrudRepository<ApprovalDocRollback, String> {
}

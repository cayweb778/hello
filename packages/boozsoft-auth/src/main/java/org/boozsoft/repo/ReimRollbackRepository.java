package org.boozsoft.repo;

import org.boozsoft.domain.entity.account.ReimRollback;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ReimRollbackRepository extends ReactiveCrudRepository<ReimRollback, String> {
}

package org.boozsoft.repo;

import org.boozsoft.domain.entity.account.ContractRollback;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ContractRollbackRepository extends ReactiveCrudRepository<ContractRollback, String> {

}

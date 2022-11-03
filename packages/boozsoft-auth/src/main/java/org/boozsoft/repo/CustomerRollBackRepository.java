package org.boozsoft.repo;

import org.boozsoft.domain.entity.CustomerRollback;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface CustomerRollBackRepository extends ReactiveCrudRepository<CustomerRollback, String> {

}

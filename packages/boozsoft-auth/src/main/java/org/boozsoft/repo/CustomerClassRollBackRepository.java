package org.boozsoft.repo;

import org.boozsoft.domain.entity.CustomerClassRollback;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface CustomerClassRollBackRepository extends ReactiveCrudRepository<CustomerClassRollback, String> {

}


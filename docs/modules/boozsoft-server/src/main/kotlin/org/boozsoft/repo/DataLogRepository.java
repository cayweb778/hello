package org.boozsoft.repo;

import org.boozsoft.domain.entity.DataLog;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface DataLogRepository extends ReactiveCrudRepository<DataLog, String> {

}

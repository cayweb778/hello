package org.boozsoft.repo;

import org.boozsoft.domain.entity.SysZone;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface SysZoneRepository extends ReactiveCrudRepository<SysZone, String> {
}

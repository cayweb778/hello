package org.boozsoft.repo;

import org.boozsoft.domain.entity.SysMessageFile;
import org.boozsoft.domain.entity.SysTaskFile;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface SysTaskFileRepository extends ReactiveCrudRepository<SysTaskFile, String> {


}

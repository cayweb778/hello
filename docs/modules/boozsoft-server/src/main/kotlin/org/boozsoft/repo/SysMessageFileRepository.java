package org.boozsoft.repo;

import org.boozsoft.domain.entity.SysMessage;
import org.boozsoft.domain.entity.SysMessageFile;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface SysMessageFileRepository extends ReactiveCrudRepository<SysMessageFile, String> {


}

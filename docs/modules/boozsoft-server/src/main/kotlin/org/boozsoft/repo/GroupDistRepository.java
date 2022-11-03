package org.boozsoft.repo;

import org.boozsoft.domain.entity.group.GroupDist;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface GroupDistRepository extends ReactiveCrudRepository<GroupDist, String> {
    Mono<GroupDist> findByDatabaseUniqueCodeAndTableName(String databaseUniqueCode,String tableName);
}


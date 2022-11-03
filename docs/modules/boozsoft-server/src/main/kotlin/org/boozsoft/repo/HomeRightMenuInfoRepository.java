package org.boozsoft.repo;

import org.boozsoft.domain.entity.HomeRightMenuInfo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface HomeRightMenuInfoRepository extends ReactiveCrudRepository<HomeRightMenuInfo, String> {
    Flux<HomeRightMenuInfo> findAllByPlatformIdOrderByMenuIdAsc(String platId);
}

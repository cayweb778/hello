package org.boozsoft.repo;

import org.boozsoft.domain.entity.SysUnitOfMea;
import org.boozsoft.domain.entity.SysUnitOfMeaMany;
import org.boozsoft.domain.vo.SysUnitOfMeaVo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface SysUnitOfMeaManyRepository extends ReactiveCrudRepository<SysUnitOfMeaMany, String> {

    @Query("delete from sys_unit_of_mea_many where id in (:ids)")
    Mono<Void> deleteByIds(List ids);

}


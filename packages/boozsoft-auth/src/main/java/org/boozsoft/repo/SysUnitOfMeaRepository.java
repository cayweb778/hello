package org.boozsoft.repo;

import org.boozsoft.domain.entity.SysUnitOfMea;
import org.boozsoft.domain.vo.GroupSysUnitOfMeaVo;
import org.boozsoft.domain.vo.SysUnitOfMeaVo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface SysUnitOfMeaRepository extends ReactiveCrudRepository<SysUnitOfMea, String> {


    @Query("select mea.* from  sys_unit_of_mea mea order by mea.id")
    Flux<SysUnitOfMeaVo> findAllOrderById();

    @Query("delete from sys_unit_of_mea where id in (:ids)")
    Mono<Void> deleteByIds(List ids);

    @Query("update sys_unit_of_mea set type_code = :typeCode where id in (:ids)")
    Mono<Void> updateTypeCode(String typeCode, List ids);

    @Query("select unit_name from sys_unit_of_mea")
    Flux<String> findAllByUnitName();

    @Query("select * from sys_unit_of_mea where flag = '1'")
    Flux<SysUnitOfMea> findAllByFlag();

}


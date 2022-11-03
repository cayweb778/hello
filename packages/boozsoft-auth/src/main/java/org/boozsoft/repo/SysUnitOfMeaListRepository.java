package org.boozsoft.repo;

import org.boozsoft.domain.entity.SysUnitOfMeaList;
import org.boozsoft.domain.vo.stock.SysUnitOfMeaListVo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface SysUnitOfMeaListRepository extends ReactiveCrudRepository<SysUnitOfMeaList, String> {

    @Query("select * from sys_unit_of_mea_list where many_code=:manyCode order by is_main desc")
    Flux<SysUnitOfMeaList> findAllByManyCode(String manyCode);
    Flux<SysUnitOfMeaList> findAllByManyCodeOrderByUnitCodeAsc(String manyCode);

    @Query("select max(unit_code) from sys_unit_of_mea_list ")
    Mono<String> findMaxCode();

    @Query("delete from sys_unit_of_mea_list where id in (:ids)")
    Mono<Void> deleteByIds(List ids);

    @Query("delete from sys_unit_of_mea_list where many_Code = :manyCode")
    Mono<Void> deleteByManyCode(String  manyCode);

    @Query("select ul.* from sys_unit_of_mea_list ul left join  sys_unit_of_mea_many um on ul.many_code = um.unit_code where um.id = :id")
    Flux<SysUnitOfMeaList> findAllByManyid(String id);

    @Query("select ul.id, ul.unit_name, ul.conversion_rate,ul.is_main,um.id as umid from sys_unit_of_mea_list ul " +
            " left join  sys_unit_of_mea_many um on ul.many_code = um.unit_code order by ul.is_main desc")
    Flux<SysUnitOfMeaListVo> findAlls();
}


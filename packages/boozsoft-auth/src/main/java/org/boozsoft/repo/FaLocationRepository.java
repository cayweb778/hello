package org.boozsoft.repo;

import org.boozsoft.domain.entity.FaLocation;
import org.boozsoft.domain.entity.SysZfClass;
import org.boozsoft.domain.vo.SysDeptClassVo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FaLocationRepository extends ReactiveCrudRepository<FaLocation, String> {


    @Query("select dc.*, dc.ec_name as pname from fa_location dc " +
            " left join fa_location zc on zc.id = dc.parent_id order by dc.ec_code Asc")
    Flux<SysDeptClassVo> findAllByOrderByEcCode();

    Flux<FaLocation> findByFlagOrderByEcCode(String flag);

    Flux<FaLocation> findByFlagAndBendOrderByEcCode(String flag, String isEnd);

    /********************* Mr. Ye *******************/
    @Query("SELECT ec_code,ec_name,unique_code from fa_location WHERE 1=1 and flag = '1' order by ec_code Asc")
    Flux<FaLocation> findAllDeptCodeOrDeptNameByFlag();
    /********************* Mr. Ye *******************/

    @Query("SELECT * from fa_location WHERE id = :id or parent_id = :id order by ec_code asc")
    Flux<FaLocation> findByIdOrderByDeptCode(String id);

    @Query("SELECT max(ec_code) from fa_location WHERE parent_id = :pid ")
    Mono<String> findMaxCodeByPid(String pid);
}

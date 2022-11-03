package org.boozsoft.repo.group;

import org.boozsoft.domain.entity.FaLocation;
import org.boozsoft.domain.entity.group.GroupFaLocation;
import org.boozsoft.domain.vo.SysDeptClassVo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GroupFaLocationRepository extends ReactiveCrudRepository<GroupFaLocation, String> {


    @Query("select dc.*, dc.ec_name as pname from _app_group_fa_location dc " +
            " left join _app_group_fa_location zc on zc.id = dc.parent_id order by dc.ec_code Asc")
    Flux<SysDeptClassVo> findAllByOrderByEcCode();

    Flux<GroupFaLocation> findByFlagOrderByEcCode(String flag);

    Flux<GroupFaLocation> findByFlagAndBendOrderByEcCode(String flag, String isEnd);

    /********************* Mr. Ye *******************/
    @Query("SELECT ec_code,ec_name,unique_code from _app_group_fa_location WHERE 1=1 and flag = '1' order by ec_code Asc")
    Flux<GroupFaLocation> findAllDeptCodeOrDeptNameByFlag();
    /********************* Mr. Ye *******************/

    @Query("SELECT * from _app_group_fa_location WHERE id = :id or parent_id = :id order by ec_code asc")
    Flux<GroupFaLocation> findByIdOrderByDeptCode(String id);

    @Query("SELECT max(ec_code) from _app_group_fa_location WHERE parent_id = :pid ")
    Mono<String> findMaxCodeByPid(String pid);
}

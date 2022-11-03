package org.boozsoft.repo.group;

import org.boozsoft.domain.entity.FaUsageStatus;
import org.boozsoft.domain.entity.group.GroupFaUsageStatus;
import org.boozsoft.domain.vo.SysDeptClassVo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GroupFaUsageStatusRepository extends ReactiveCrudRepository<GroupFaUsageStatus, String> {


    @Query("select dc.*, dc.ec_name as pname from _app_group_fa_usage_status dc " +
            " left join _app_group_fa_usage_status zc on zc.id = dc.parent_id order by dc.ec_code Asc")
    Flux<SysDeptClassVo> findAllByOrderByEcCode();

    Flux<GroupFaUsageStatus> findByFlagOrderByEcCode(String flag);

    Flux<GroupFaUsageStatus> findByFlagAndBendOrderByEcCode(String flag, String isEnd);

    /********************* Mr. Ye *******************/
    @Query("SELECT ec_code,ec_name,unique_code from _app_group_fa_usage_status WHERE 1=1 and flag = '1' order by ec_code Asc")
    Flux<GroupFaUsageStatus> findAllDeptCodeOrDeptNameByFlag();
    /********************* Mr. Ye *******************/

    @Query("SELECT * from _app_group_fa_usage_status WHERE id = :id or parent_id = :id order by ec_code asc")
    Flux<GroupFaUsageStatus> findByIdOrderByDeptCode(String id);

    @Query("SELECT max(ec_code) from _app_group_fa_usage_status WHERE parent_id = :pid ")
    Mono<String> findMaxCodeByPid(String pid);
}

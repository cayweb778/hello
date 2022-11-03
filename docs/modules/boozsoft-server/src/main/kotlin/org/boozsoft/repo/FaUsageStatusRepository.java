package org.boozsoft.repo;

import org.boozsoft.domain.entity.FaLocation;
import org.boozsoft.domain.entity.FaUsageStatus;
import org.boozsoft.domain.vo.SysDeptClassVo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FaUsageStatusRepository extends ReactiveCrudRepository<FaUsageStatus, String> {


    @Query("select dc.*, dc.ec_name as pname from fa_usage_status dc " +
            " left join fa_usage_status zc on zc.id = dc.parent_id order by dc.ec_code Asc")
    Flux<SysDeptClassVo> findAllByOrderByEcCode();

    Flux<FaUsageStatus> findByFlagOrderByEcCode(String flag);

    Flux<FaUsageStatus> findByFlagAndBendOrderByEcCode(String flag, String isEnd);

    /********************* Mr. Ye *******************/
    @Query("SELECT ec_code,ec_name,unique_code from fa_usage_status WHERE 1=1 and flag = '1' order by ec_code Asc")
    Flux<FaUsageStatus> findAllDeptCodeOrDeptNameByFlag();
    /********************* Mr. Ye *******************/

    @Query("SELECT * from fa_usage_status WHERE id = :id or parent_id = :id order by ec_code asc")
    Flux<FaUsageStatus> findByIdOrderByDeptCode(String id);

    @Query("SELECT max(ec_code) from fa_usage_status WHERE parent_id = :pid ")
    Mono<String> findMaxCodeByPid(String pid);
}

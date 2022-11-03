package org.boozsoft.repo;

import org.boozsoft.domain.entity.FaAddCutMode;
import org.boozsoft.domain.entity.group.GroupFaAddCutMode;
import org.boozsoft.domain.vo.SysDeptClassVo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FaAddCutModeRepository extends ReactiveCrudRepository<FaAddCutMode, String> {
    @Query("select dc.*, dc.ec_name as pname from fa_add_cut_mode dc " +
            " left join fa_add_cut_mode zc on zc.id = dc.parent_id order by dc.ec_code Asc")
    Flux<SysDeptClassVo> findAllByOrderByEcCode();

    Flux<FaAddCutMode> findByFlagOrderByEcCode(String flag);

    Flux<FaAddCutMode> findByFlagAndBendOrderByEcCode(String flag,String isEnd);

    /********************* Mr. Ye *******************/
    @Query("SELECT ec_code,ec_name,unique_code from fa_add_cut_mode WHERE 1=1 and flag = '1' order by ec_code Asc")
    Flux<FaAddCutMode> findAllDeptCodeOrDeptNameByFlag();

    Flux<FaAddCutMode> findAllByFlagAndBendAndIsAccrualOrderByEcCodeAsc(String flag,String bend,String isAcc);
    /********************* Mr. Ye *******************/

    @Query("SELECT * from fa_add_cut_mode WHERE id = :id or parent_id = :id order by ec_code asc")
    Flux<FaAddCutMode> findByIdOrderByDeptCode(String id);


    @Query("SELECT max(ec_code) from fa_add_cut_mode WHERE parent_id = :pid ")
    Mono<String> findMaxCodeByPid(String pid);
}

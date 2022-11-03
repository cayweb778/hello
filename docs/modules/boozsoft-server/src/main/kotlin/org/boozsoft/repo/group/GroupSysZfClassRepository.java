package org.boozsoft.repo.group;

import org.boozsoft.domain.entity.ExpenditureClass;
import org.boozsoft.domain.entity.group.GroupSysZfClass;
import org.boozsoft.domain.vo.SysDeptClassVo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface GroupSysZfClassRepository extends ReactiveCrudRepository<GroupSysZfClass, String> {

    @Query("select dc.*, dc.ec_name as pname from _app_group_sys_zf_class dc " +
            " left join _app_group_sys_zf_class zc on zc.id = dc.parent_id where dc.is_del = '0' order by dc.ec_code Asc")
    Flux<SysDeptClassVo> findAllByOrderByEcCode();

    Flux<GroupSysZfClass> findByFlagOrderByEcCode(String flag);

    Flux<GroupSysZfClass> findByFlagAndBendOrderByEcCode(String flag,String isEnd);

    @Query("SELECT ec_code,ec_name,unique_code from _app_group_sys_zf_class WHERE 1=1 and is_del = '0' and flag = '1' order by ec_code Asc")
    Flux<GroupSysZfClass> findAllDeptCodeOrDeptNameByFlag();

    @Query("SELECT * from _app_group_sys_zf_class WHERE is_del = '0' and (id = :id or parent_id = :id)  order by ec_code asc")
    Flux<GroupSysZfClass> findByIdOrderByDeptCode(String id);


    @Query("SELECT max(ec_code) from _app_group_sys_zf_class WHERE parent_id = :pid ")
    Mono<String> findMaxCodeByPid(String pid);

    @Query("select * from _app_group_sys_zf_class where is_del = '1' order by create_date")
    Flux<ExpenditureClass> findAllByIsDel();

    @Query("delete  from _app_group_sys_zf_class  where id in (:ids)")
    Mono<Void> deleteAllByIds(List ids);

    @Query("update _app_group_sys_zf_class set is_del = '0' where id in (:ids)")
    Mono<Void>  updateIsDel(List ids);
}

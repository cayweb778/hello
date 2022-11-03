package org.boozsoft.repo;

import org.boozsoft.domain.entity.ExpenditureClass;
import org.boozsoft.domain.entity.SysZfClass;
import org.boozsoft.domain.entity.group.GroupSysZfClass;
import org.boozsoft.domain.vo.SysDeptClassVo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface SysZfClassRepository extends ReactiveCrudRepository<SysZfClass, String> {


    @Query("select dc.*, dc.ec_name as pname from sys_zf_class dc " +
            " left join sys_zf_class zc on zc.id = dc.parent_id where dc.is_del = '0' order by dc.ec_code Asc")
    Flux<SysDeptClassVo> findAllByOrderByEcCode();

    @Query("SELECT * from sys_zf_class WHERE is_del = '0' and flag = :flag order by ec_code asc")
    Flux<SysZfClass> findByFlagOrderByEcCode(String flag);

    @Query("SELECT * from sys_zf_class WHERE is_del = '0' and flag = :flag and bend = :isEnd order by ec_code asc")
    Flux<SysZfClass> findByFlagAndBendOrderByEcCode(String flag, String isEnd);

    @Query("SELECT ec_code,ec_name,unique_code from sys_zf_class WHERE 1=1 and flag = '1' and is_del = '0' order by ec_code Asc")
    Flux<SysZfClass> findAllDeptCodeOrDeptNameByFlag();

    @Query("SELECT * from sys_zf_class WHERE is_del = '0' and (id = :id or parent_id = :id) order by ec_code asc")
    Flux<SysZfClass> findByIdOrderByDeptCode(String id);

    @Query("SELECT max(ec_code) from sys_zf_class WHERE parent_id = :pid and is_del = '0'")
    Mono<String> findMaxCodeByPid(String pid);

    @Query("select * from sys_zf_class where is_del = '1' order by create_date")
    Flux<ExpenditureClass> findAllByIsDel();

    @Query("delete  from sys_zf_class  where id in (:ids)")
    Mono<Void> deleteAllByIds(List ids);

    @Query("update sys_zf_class set is_del = '0' where id in (:ids)")
    Mono<Void>  updateIsDel(List ids);
}

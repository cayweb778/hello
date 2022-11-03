package org.boozsoft.repo;

import org.boozsoft.domain.entity.ExpenditureClass;
import org.boozsoft.domain.entity.SysDeptClass;
import org.boozsoft.domain.vo.SysDeptClassVo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface SysDeptClassRepository extends ReactiveCrudRepository<SysDeptClass, String> {

    @Query("select dc.*,zc.ec_name as zfname,pdc.ec_name as pname from sys_dept_class dc " +
            " left join sys_zf_class zc on zc.id = dc.zf_code " +
            " left join sys_dept_class pdc on pdc.id = dc.parent_id where dc.is_del = '0' order by dc.ec_code Asc")
    Flux<SysDeptClassVo> findAllByOrderByEcCode();

    @Query("SELECT * from sys_dept_class WHERE is_del = '0' and flag = :flag order by ec_code asc")
    Flux<SysDeptClass> findByFlagOrderByEcCode(String flag);

    @Query("SELECT ec_code,ec_name,unique_code from sys_dept_class WHERE 1=1 and flag = '1' and is_del = '0' order by ec_code Asc")
    Flux<SysDeptClass> findAllDeptCodeOrDeptNameByFlag();

    @Query("SELECT * from sys_dept_class WHERE is_del = '0' and (id = :id or parent_id = :id)  order by ec_code asc")
    Flux<SysDeptClass> findByIdOrderByDeptCode(String id);

    @Query("SELECT max(ec_code) from sys_dept_class WHERE parent_id = :pid and is_del = '0'")
    Mono<String> findMaxCodeByPid(String pid);

    @Query("select * from sys_dept_class where is_del = '1' order by create_date")
    Flux<ExpenditureClass> findAllByIsDel();

    @Query("delete  from sys_dept_class  where id in (:ids)")
    Mono<Void> deleteAllByIds(List ids);

    @Query("update sys_dept_class set is_del = '0' where id in (:ids)")
    Mono<Void>  updateIsDel(List ids);
}

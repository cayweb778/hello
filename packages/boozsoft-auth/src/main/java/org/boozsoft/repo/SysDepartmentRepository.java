package org.boozsoft.repo;

import org.boozsoft.domain.entity.account.SysDepartment;
import org.boozsoft.domain.vo.SysDepartmentVo2;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SysDepartmentRepository extends ReactiveCrudRepository<SysDepartment, String> {

    /*Flux<SysDepartment> findAllByOrderByDeptCode();
    Mono<Long> countByDeptCode(String deptcode);
    Flux<SysDepartment> findByFlagOrderByDeptCode(String flag);
    Flux<SysDepartment> findByFlagAndTenantIdOrderByDeptCode(String flag, String accId);

    @Query("select * from sys_department order by dept_code ")
    Flux<SysDepartment> findAllOrderByDeotCode();

    @Query("select dept.unique_code, dept.dept_code, dept.dept_name, acv.ccode\n" +
            "from accvoucher acv\n" +
            "         left join sys_department dept on dept.unique_code = acv.cdept_id\n" +
            "where dept.flag = '1'\n" +
            "group by dept.unique_code, dept.dept_code, dept.dept_name, acv.ccode\n" +
            "order by dept.dept_code")
    Flux<SysDepartmentVo2> findAllOrderByDeotCode2();

    *//********************* Mr. Ye *******************//*
    @Query("SELECT dept_code,dept_name,unique_code from sys_department WHERE 1=1 and flag = '1' order by dept_code Asc")
    Flux<SysDepartment> findAllDeptCodeOrDeptNameByFlag();
    *//********************* Mr. Ye *******************//*
    Mono<Void> deleteAllByUniqueCode(String key);

    @Query("SELECT * from sys_department WHERE id = :id or parent_id = :id order by dept_code asc")
    Flux<SysDepartment> findByIdOrderByDeptCode(String id);


    @Modifying
    @Query("delete from sys_department where tenant_id =:id ")
    Mono<Void> deleteAllByTenantId(String id);*/

    Flux<SysDepartment> findAllByIsDelOrderByDeptCode(String isDel);
    Mono<Long> countByDeptCode(String deptcode);
    Flux<SysDepartment> findByFlagAndIsDelOrderByDeptCode(String flag,String isDel);
    Flux<SysDepartment> findByFlagAndTenantIdAndIsDelOrderByDeptCode(String flag, String accId,String isDel);

    @Query("select * from sys_department where is_del='0' order by dept_code ")
    Flux<SysDepartment> findAllOrderByDeotCode();

    @Query("select dept.unique_code, dept.dept_code, dept.dept_name, acv.ccode\n" +
            "from accvoucher acv\n" +
            "         left join sys_department dept on dept.unique_code = acv.cdept_id\n" +
            "where dept.flag = '1' and is_del='0' \n" +
            "group by dept.unique_code, dept.dept_code, dept.dept_name, acv.ccode\n" +
            "order by dept.dept_code")
    Flux<SysDepartmentVo2> findAllOrderByDeotCode2();

    /********************* Mr. Ye *******************/
    @Query("SELECT dept_code,dept_name,unique_code from sys_department WHERE 1=1 and flag = '1' and is_del='0' order by dept_code Asc")
    Flux<SysDepartment> findAllDeptCodeOrDeptNameByFlag();
    /********************* Mr. Ye *******************/
    Mono<Void> deleteAllByUniqueCode(String key);

    @Query("SELECT * from sys_department WHERE id = :id or parent_id = :id and is_del='0' order by dept_code asc")
    Flux<SysDepartment> findByIdOrderByDeptCode(String id);


    @Modifying
    @Query("delete from sys_department where tenant_id =:id ")
    Mono<Void> deleteAllByTenantId(String id);


}

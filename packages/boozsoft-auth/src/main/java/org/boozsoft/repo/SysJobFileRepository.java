package org.boozsoft.repo;

import org.boozsoft.domain.entity.SysJobFile;
import org.boozsoft.domain.entity.SysZfClass;
import org.boozsoft.domain.entity.group.GroupSysZfClass;
import org.boozsoft.domain.vo.SysDeptClassVo;
import org.boozsoft.domain.vo.SysJofFileClassVo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SysJobFileRepository extends ReactiveCrudRepository<SysJobFile, String> {


    @Query("select dc.*, jt.ec_name as pname from sys_job_file dc " +
            " left join sys_job_type jt on jt.id = dc.type_id order by dc.ec_code Asc")
    Flux<SysJofFileClassVo> findAllByOrderByEcCode();



    Flux<SysJobFile> findByFlagOrderByEcCode(String flag);

    Flux<SysJobFile> findByFlagAndBendOrderByEcCode(String flag, String isEnd);

    /********************* Mr. Ye *******************/
    @Query("SELECT ec_code,ec_name,unique_code from sys_job_file WHERE 1=1 and flag = '1' order by ec_code Asc")
    Flux<SysJobFile> findAllDeptCodeOrDeptNameByFlag();
    /********************* Mr. Ye *******************/

    @Query("SELECT * from sys_job_file WHERE id = :id or parent_id = :id order by ec_code asc")
    Flux<SysJobFile> findByIdOrderByDeptCode(String id);


}

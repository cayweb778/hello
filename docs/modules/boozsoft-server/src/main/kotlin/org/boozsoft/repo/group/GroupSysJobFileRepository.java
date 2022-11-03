package org.boozsoft.repo.group;

import org.boozsoft.domain.entity.SysJobFile;
import org.boozsoft.domain.entity.group.GroupSysJobFile;
import org.boozsoft.domain.vo.SysJofFileClassVo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface GroupSysJobFileRepository extends ReactiveCrudRepository<GroupSysJobFile, String> {


    @Query("select dc.*, jt.ec_name as pname from _app_group_sys_job_file dc " +
            " left join _app_group_sys_job_type jt on jt.id = dc.type_id order by dc.ec_code Asc")
    Flux<SysJofFileClassVo> findAllByOrderByEcCode();



    Flux<GroupSysJobFile> findByFlagOrderByEcCode(String flag);

    Flux<GroupSysJobFile> findByFlagAndBendOrderByEcCode(String flag, String isEnd);

    /********************* Mr. Ye *******************/
    @Query("SELECT ec_code,ec_name,unique_code from _app_group_sys_job_file WHERE 1=1 and flag = '1' order by ec_code Asc")
    Flux<GroupSysJobFile> findAllDeptCodeOrDeptNameByFlag();
    /********************* Mr. Ye *******************/

    @Query("SELECT * from _app_group_sys_job_file WHERE id = :id or parent_id = :id order by ec_code asc")
    Flux<GroupSysJobFile> findByIdOrderByDeptCode(String id);


}

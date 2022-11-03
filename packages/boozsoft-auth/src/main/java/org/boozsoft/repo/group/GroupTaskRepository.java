package org.boozsoft.repo.group;

import org.boozsoft.domain.entity.group.GroupTask;
import org.boozsoft.domain.vo.group.GroupTaskVo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface GroupTaskRepository extends ReactiveCrudRepository<GroupTask, String> {

    @Query("select t.*,s.username from _app_group_task t left join _app_group_sys_user s on s.id=t.caozuo_unique where function_module=:functionModule and iyear=:iyear and record_num=:recordNum ")
    Mono<GroupTaskVo> findByFunctionModuleAndIyearAndRecordNum(String functionModule, String iyear, String recordNum);

    @Query("delete from _app_group_task where function_module=:functionModule and iyear=:iyear and record_num=:recordNum and caozuo_unique=:userId ")
    Mono<Void> groupTaskDelByUserIdAndOrgUniqueAndFunctionModule(String functionModule, String iyear, String recordNum,String userId);
}

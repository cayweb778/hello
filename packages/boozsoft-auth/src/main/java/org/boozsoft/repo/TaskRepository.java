package org.boozsoft.repo;

import org.boozsoft.domain.entity.Task;
import org.boozsoft.domain.vo.TaskVo;
import org.boozsoft.domain.vo.group.GroupParameterAccuracy;
import org.springbooz.core.tool.result.R;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface TaskRepository extends ReactiveCrudRepository<Task, String> {
    @Query("select task.*,sysu.username from task left join _app_group_sys_user sysu on sysu.id=task.caozuo_unique where task.iyear =:iyear")
    Flux<TaskVo> findAllByIyear(@Param("iyear") String iyear);

    Mono<Task> findAllByIyearAndId(String iyear, long parseLong);
    Mono<Task> findAllByIyearAndFunctionModule(String iyear, String name);

    @Query("select *,sysu.username from task left join _app_group_sys_user sysu on sysu.id=caozuo_unique where function_module =:name and iyear=:iyear and method in (:method)")
    Flux<TaskVo> findAllByFunctionModule2(@Param("name")String name,@Param("iyear")String iyear,@Param("method") List<String> method);

    @Query("select *,sysu.username from task left join _app_group_sys_user sysu on sysu.id=caozuo_unique where function_module =:name and iyear=:iyear and method in (:method) and record_num=:recordNum ")
    Flux<TaskVo> findAllByFunctionModule3(@Param("name")String name,@Param("iyear")String iyear,@Param("method")List<String> method,@Param("recordNum")String recordNum);


    @Query("select *,sysu.username from task left join _app_group_sys_user sysu on sysu.id=caozuo_unique where iyear=:iyear and  function_module in (:name) and method in (:method)")
    Flux<TaskVo> findAllByFunctionModule4(@Param("name") List<String> name, @Param("iyear") String iyear, @Param("method") List<String> method);

    @Query("select *,sysu.username from task left join _app_group_sys_user sysu on sysu.id=caozuo_unique where iyear=:iyear and function_module in (:name)  and method in (:method) and record_num in (:recordNum) ")
    Flux<TaskVo> findAllByFunctionModule5(@Param("name") List<String>  name,@Param("iyear") String iyear,@Param("method") List<String> method,@Param("recordNum") List<String>  recordNum);

    @Query("select task.*,sysu.username from task left join _app_group_sys_user sysu on sysu.id=caozuo_unique where function_module =:name")
    Mono<TaskVo> findAllByFunctionModule(@Param("name")String name);

    @Query("select count(id) from task where function_module =:name ")
    Mono<Long> countByFunctionModule(@Param("name")String name);

    @Query("delete from task where state =:state ")
    Mono<Void> deleteByState(@Param("state")String state);

    @Query("delete from task where iyear=:iyear and function_module =:name ")
    Mono<Task> deleteByIyearAndFunctionModule(@Param("iyear")String iyear, @Param("name")String name);

    @Query("delete from task where id=:id")
    Mono<Void> deleteById(@Param("id")String id);

    @Query("delete from task where caozuo_unique=:caozuoUnique and function_module =:name")
    Mono<Void> deleteByFunctionModuleAndCaozuoUnique(@Param("caozuoUnique")String caozuoUnique, @Param("name")String name);

    @Query("delete from task where caozuo_unique=:caozuoUnique and function_module =:name and iyear=:iyear and method=:method")
    Mono<Void> deleteByFunctionModuleAndCaozuoUnique2(@Param("caozuoUnique")String caozuoUnique, @Param("name")String name,@Param("iyear")String iyear,@Param("method")String method);

    @Query("delete from task where caozuo_unique=:caozuoUnique and function_module =:name and iyear=:iyear and method=:method and record_num=:recordNum ")
    Mono<Void> deleteByFunctionModuleAndCaozuoUnique3(@Param("caozuoUnique")String caozuoUnique, @Param("name")String name,@Param("iyear")String iyear,@Param("method")String method,@Param("recordNum")String recordNum);

    @Query("update task set time=:time where id=:id ")
    Mono<Void> editTimeByid(@Param("time")String time,@Param("id")String id);

    Mono<Void> deleteByMethodAndRecordNum(String method,String ccode);

    Mono<Task> findById(String id);
    Mono<Long> countById(String id);

    @Query("select task.*,\n" +
            "       case when (select co_code from _app_group_sys_account where left(tenant_id, length(tenant_id) - 5) = acc_id) isnull then (select co_code from _app_group_stock_account where left(tenant_id, length(tenant_id) - 5) = stock_acc_id) else (select co_code from _app_group_sys_account where left(tenant_id, length(tenant_id) - 5) = acc_id) end as company_code,\n" +
            "       case when (select acc_name from _app_group_sys_account where left(tenant_id, length(tenant_id) - 5) = acc_id) isnull then (select stock_acc_name from _app_group_stock_account where left(tenant_id, length(tenant_id) - 5) = stock_acc_id) else (select acc_name from _app_group_sys_account where left(tenant_id, length(tenant_id) - 5) = acc_id) end as company_name,\n" +
            "       case when (select acc_group from _app_group_sys_account where left(tenant_id, length(tenant_id) - 5) = acc_id) isnull then '' else (select acc_group from _app_group_sys_account where left(tenant_id, length(tenant_id) - 5) = acc_id) end as acc_group," +
            "       (select u.real_name from _app_group_sys_user u where u.id=task.caozuo_unique) as caozuo_name " +
            "from task\n ")
    Flux<TaskVo> findAllBy();

    @Query(" delete from task where id in ( :list ) ")
    Mono<Void> delAcclotList(@Param("list")List<String> list);
    @Query(" update task set state='0' where id in ( :list ) ")
    Mono<Void> editAcclotStateList(@Param("list")List<String> list);

    @Query("select * from _app_group_parameter_accuracy where para_name ='操作锁定超时' ")
    Mono<GroupParameterAccuracy> groupAccuracyOverTime();

    @Query("update _app_group_parameter_accuracy set decimal_places=:decimalPlaces where id =:id ")
    Mono<Void> editGroupAccuracyOverTime(@Param("id")String id,@Param("decimalPlaces")String decimalPlaces);
    @Query("insert into _app_group_parameter_accuracy (decimal_places,para_name,id) values (:decimalPlaces,'操作锁定超时',:id)")
    Mono<GroupParameterAccuracy> saveGroupAccuracyOverTime(@Param("decimalPlaces")String decimalPlaces,@Param("id")String id);


    @Query("select * from task where caozuo_module =:module limit 1")
    Mono<Task> findTaskByCaozuoModule(  @Param("module")String module);

    Flux<Task> findByIyearAndCaozuoModule(String iyear,String caozuoModule);

}
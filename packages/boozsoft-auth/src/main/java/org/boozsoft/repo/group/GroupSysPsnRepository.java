package org.boozsoft.repo.group;

import io.lettuce.core.dynamic.annotation.Param;
import org.boozsoft.domain.entity.group.GroupSysPsn;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GroupSysPsnRepository extends ReactiveCrudRepository<GroupSysPsn, String> {

    Flux<GroupSysPsn> findAllByIsDelOrderByPsnCode(Pageable pageable,String isDel);
    @Query("select * from _app_group_sys_psn where is_del=:isDel order by success_state,psn_code")
    Flux<GroupSysPsn> findAllByIsDelOrderByPsnCode(String isDel);
    Mono<Long> countAllByIsDel(String isDel);
    Mono<Long> countByPsnCode(String code);
    Flux<GroupSysPsn> findByFlagAndIsDelOrderByPsnCode(String flag,String isDel);
    Flux<GroupSysPsn> findByFlagAndIsDelOrderByPsnCode(Pageable pageable,String flag,String isDel);
    Mono<Long> countByFlagAndIsDel(String flag,String isDel);
    @Query("select * from _app_group_sys_psn where flag=:flag and is_del=:isDel order by psn_code")
    Flux<GroupSysPsn> findAllByFlag(String flag,String isDel);
    Mono<GroupSysPsn> deleteAllByUniqueCode(String key);

    Flux<GroupSysPsn> findAllByUniquePsnTypeAndIsDelOrderByPsnCode(Pageable pageable,String uniquePsnType, String isDel);
    Flux<GroupSysPsn> findAllByUniquePsnTypeAndIsDelOrderByPsnCode(String uniquePsnType,String isDel);
    Mono<Long> countAllByUniquePsnTypeAndIsDel(String uniquePsnType,String isDel);

    Flux<GroupSysPsn> findAllByUniquePsnTypeAndFlagAndIsDelOrderByPsnCode(Pageable pageable,String uniquePsnType,String flag, String isDel);
    Flux<GroupSysPsn> findAllByUniquePsnTypeAndFlagAndIsDelOrderByPsnCode(String uniquePsnType,String flag, String isDel);
    Mono<Long> countAllByUniquePsnTypeAndFlagAndIsDel(String uniquePsnType,String flag, String isDel);

    Mono<GroupSysPsn> findByUniqueCode(String uniqueCode);

    //根据组织查询所有未生效的人员
    @Query("select * from _app_group_sys_psn where ctype=:ctype and origin_id=:originId and success_state!='1' and is_del='0'")
    Flux<GroupSysPsn> findByCtypeAndOriginId(String ctype,String originId);

    //根据租户查询所有未生效的人员
    @Query("select * from _app_group_sys_psn where ctype=:ctype and tenant_id=:tenantId and success_state!='1' and is_del='0'")
    Flux<GroupSysPsn> findByCtypeAndTenantId(String ctype,String tenantId);

    Flux<GroupSysPsn> findByPsnNameAndCellPhoneNumAndSuccessState(String psnName,String cellPhoneNum,String successState);
    Flux<GroupSysPsn> findByPsnNameAndCellPhoneNum(String psnName,String cellPhoneNum);
    Flux<GroupSysPsn> findByPsnNameAndSuccessState(String psnName,String successState);
    Flux<GroupSysPsn> findByPsnName(String psnName);

    //查询所有未生效的人员
    @Query("select * from _app_group_sys_psn where success_state!='1' and is_del='0'")
    Flux<GroupSysPsn> findByNotSuccessState();

    /**
     * 获取缺少的最小数字
     */
    @Query("select MIN(cast(right(psn_code,:num) as int)+1) as psn_code from _app_group_sys_psn t1 " +
            "where length(psn_code)=:sum and left(psn_code,:qzNum)=:qianzhui and not exists(select * from _app_group_sys_psn t2 " +
            "where length(psn_code)=:sum and left(psn_code,:qzNum)=:qianzhui and cast(right(t2.psn_code,:num) as int) = cast(right(t1.psn_code,:num) as int) + 1) ")
    Mono<GroupSysPsn> findBukongPsnCode(@Param("num") Integer num,@Param("sum") Integer sum, @Param("qzNum") Integer qzNum, @Param("qianzhui") String qianzhui);

    /**
     * 获取最大的编码
     */
    @Query("select Max(right(psn_code,:num)) as psn_code from _app_group_sys_psn where length(psn_code)=:sum and left(psn_code,:qzNum)=:qianzhui ")
    Mono<GroupSysPsn> findMaxPsnCode(@Param("num") Integer num,@Param("sum") Integer sum, @Param("qzNum") Integer qzNum, @Param("qianzhui") String qianzhui);

}

package org.boozsoft.repo;

import io.lettuce.core.dynamic.annotation.Param;
import org.boozsoft.domain.entity.Supplier;
import org.boozsoft.domain.entity.account.SysPsn;
import org.boozsoft.domain.entity.group.GroupSysPsn;
import org.boozsoft.domain.vo.CustomerVo;
import org.boozsoft.domain.vo.SysPsnVo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface SysPsnRepository extends ReactiveCrudRepository<SysPsn, String> {

    /*Flux<SysPsn> findAllByOrderByPsnCode(Pageable pageable);
    Mono<Long> countAllBy();

    Flux<SysPsn> findAllByOrderByPsnCode();

    Mono<Long> countByPsnCode(String code);

    Flux<SysPsn> findByFlagOrderByPsnCode(String flag);
    Flux<SysPsn> findByFlagOrderByPsnCode(String flag,Pageable pageable);
    Mono<Long> countByFlag(String flag);

    @Query("select * from sys_psn where flag=:flag order by psn_code")
    Flux<SysPsn> findAllByFlag(String flag);

    @Query("select * from sys_psn order by psn_code ")
    Flux<SysPsn> findAllOrderByPsnCode();

    *//********************* Mr. Ye *******************//*
    @Query("SELECT DISTINCT psn_code,psn_name,unique_code from sys_psn WHERE flag = '1' order by psn_code Asc")
    Flux<SysPsn> findAllPsnCodeOrPsnNameByFlag();

    Flux<SysPsn> findAllByUniqueCodeDeptInOrderByPsnCode(List<String> deptCodes, Pageable pageable);
    Flux<SysPsn> findAllByUniqueCodeDeptInOrderByPsnCode(List<String> deptCodes);
    Mono<Long> countAllByUniqueCodeDeptIn(List<String> deptCodes);

    Flux<SysPsn> findAllByUniqueCodeDeptInAndFlagOrderByPsnCode(List<String> deptCodes,String flag, Pageable pageable);
    Flux<SysPsn> findAllByUniqueCodeDeptInAndFlagOrderByPsnCode(List<String> deptCodes,String flag);
    Mono<Long> countAllByUniqueCodeDeptInAndFlag(List<String> deptCodes,String flag);

    *//********************* Mr. Ye *******************//*
    Mono<Void> deleteAllByUniqueCode(String key);

    Flux<SysPsn> findAllByUniquePsnTypeOrderByPsnCode(String uniquePsnType, Pageable pageable);
    Flux<SysPsn> findAllByUniquePsnTypeOrderByPsnCode(String uniquePsnType);
    Mono<Long> countAllByUniquePsnType(String uniquePsnType);

    Flux<SysPsn> findAllByUniquePsnTypeAndFlagOrderByPsnCode(String uniquePsnType,String flag, Pageable pageable);
    Flux<SysPsn> findAllByUniquePsnTypeAndFlagOrderByPsnCode(String uniquePsnType,String flag);
    Mono<Long> countAllByUniquePsnTypeAndFlag(String uniquePsnType,String flag);


    @Modifying
    @Query("delete from sys_psn where tenant_id =:id ")
    Mono<Void> deleteAllByTenantId(String id);

    @Query("select psn.unique_code, psn.psn_code, psn.psn_name, acv.ccode\n" +
            "from accvoucher acv\n" +
            "         left join sys_psn psn on psn.unique_code = acv.cperson_id\n" +
            "where psn.flag = '1'\n" +
            "group by psn.unique_code, psn.psn_code, psn.psn_name, acv.ccode\n" +
            "order by psn.psn_code")
    Flux<SysPsnVo> findAllOrderByPsnCode2();*/

    Flux<SysPsn> findAllByIsDelOrderByPsnCode(String isDel,Pageable pageable);
    Mono<Long> countAllByIsDel(String isDel);

    Flux<SysPsn> findAllByIsDelOrderByPsnCode(String isDel);

    Mono<Long> countByPsnCodeAndIsDel(String code,String isDel);

    Flux<SysPsn> findByFlagAndIsDelOrderByPsnCode(String flag,String isDel);
    Flux<SysPsn> findByFlagAndIsDelOrderByPsnCode(String flag,String isDel,Pageable pageable);
    Mono<Long> countByFlagAndIsDel(String flag, String isDel);

    @Query("select * from sys_psn where flag=:flag and is_del='0' order by psn_code")
    Flux<SysPsn> findAllByFlag(String flag);

    @Query("select * from sys_psn where is_del='0' order by psn_code ")
    Flux<SysPsn> findAllOrderByPsnCode();

    /********************* Mr. Ye *******************/
    @Query("SELECT DISTINCT psn_code,psn_name,unique_code from sys_psn WHERE flag = '1' and is_del='0' order by psn_code Asc")
    Flux<SysPsn> findAllPsnCodeOrPsnNameByFlag();

    Flux<SysPsn> findAllByUniqueCodeDeptInAndIsDelOrderByPsnCode(List<String> deptCodes,String isDel, Pageable pageable);
    Flux<SysPsn> findAllByUniqueCodeDeptInAndIsDelOrderByPsnCode(List<String> deptCodes,String isDel);
    Mono<Long> countAllByUniqueCodeDeptInAndIsDel(List<String> deptCodes,String isDel);

    Flux<SysPsn> findAllByUniqueCodeDeptInAndFlagAndIsDelOrderByPsnCode(List<String> deptCodes,String flag,String isDel, Pageable pageable);
    Flux<SysPsn> findAllByUniqueCodeDeptInAndFlagAndIsDelOrderByPsnCode(List<String> deptCodes,String flag,String isDel);
    Mono<Long> countAllByUniqueCodeDeptInAndFlagAndIsDel(List<String> deptCodes,String flag,String isDel);

    /********************* Mr. Ye *******************/
    Mono<Void> deleteAllByUniqueCode(String key);

    Flux<SysPsn> findAllByUniquePsnTypeAndIsDelOrderByPsnCode(String uniquePsnType,String isDel, Pageable pageable);
    Flux<SysPsn> findAllByUniquePsnTypeAndIsDelOrderByPsnCode(String uniquePsnType,String isDel);
    Mono<Long> countAllByUniquePsnTypeAndIsDel(String uniquePsnType,String isDel);

    Flux<SysPsn> findAllByUniquePsnTypeAndFlagAndIsDelOrderByPsnCode(String uniquePsnType,String flag,String isDel, Pageable pageable);
    Flux<SysPsn> findAllByUniquePsnTypeAndFlagAndIsDelOrderByPsnCode(String uniquePsnType,String flag,String isDel);
    Mono<Long> countAllByUniquePsnTypeAndFlagAndIsDel(String uniquePsnType,String flag,String isDel);


    @Modifying
    @Query("delete from sys_psn where tenant_id =:id ")
    Mono<Void> deleteAllByTenantId(String id);

    @Query("select psn.unique_code, psn.psn_code, psn.psn_name, acv.ccode\n" +
            "from accvoucher acv\n" +
            "         left join sys_psn psn on psn.unique_code = acv.cperson_id\n" +
            "where psn.flag = '1' and psn.is_del='0'\n" +
            "group by psn.unique_code, psn.psn_code, psn.psn_name, acv.ccode\n" +
            "order by psn.psn_code")
    Flux<SysPsnVo> findAllOrderByPsnCode2();

    /**
     * 获取缺少的最小数字
     */
    @Query("select MIN(cast(right(psn_code,:num) as int)+1) as psn_code from sys_psn t1 " +
            "where length(psn_code)=:sum and left(psn_code,:qzNum)=:qianzhui and not exists(select * from sys_psn t2 " +
            "where length(psn_code)=:sum and left(psn_code,:qzNum)=:qianzhui and cast(right(t2.psn_code,:num) as int) = cast(right(t1.psn_code,:num) as int) + 1) ")
    Mono<GroupSysPsn> findBukongPsnCode(@Param("num") Integer num, @Param("sum") Integer sum, @Param("qzNum") Integer qzNum, @Param("qianzhui") String qianzhui);

    /**
     * 获取最大的编码
     */
    @Query("select Max(right(psn_code,:num)) as psn_code from sys_psn where length(psn_code)=:sum and left(psn_code,:qzNum)=:qianzhui ")
    Mono<GroupSysPsn> findMaxPsnCode(@Param("num") Integer num,@Param("sum") Integer sum, @Param("qzNum") Integer qzNum, @Param("qianzhui") String qianzhui);

}

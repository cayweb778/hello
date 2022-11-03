package org.boozsoft.repo.standard;

import org.boozsoft.domain.entity.accstandard.AccStandard;
import org.boozsoft.domain.vo.AccStandardVo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccStandardRepository extends ReactiveCrudRepository<AccStandard, String> {
    @Query(value = "SELECT ast.*,\n" +
            "       (SELECT string_agg(acstyle.cclass, ',')\n" +
            "        FROM _app_group_acc_style as acstyle\n" +
            "        WHERE acstyle.unique = ast.acc_style_unique) AS style_name\n" +
            "        ,\n" +
            "       (SELECT string_agg(acstyle.order, ',')\n" +
            "        FROM _app_group_acc_style as acstyle\n" +
            "        WHERE acstyle.unique = ast.acc_style_unique) AS style_id\n" +
            "FROM _app_group_acc_standard as ast\n" +
            "ORDER BY ast.num")
    // style_name、style_id 未能排序，如果需要排序，参照（国家地区）会计科目
    Flux<AccStandardVo> findAllAssStandard();



    @Query("select acst.*\n" +
            "from _app_group_acc_standard acst\n" +
            "         left outer join _app_group_acc_template actemp on acst.unique_acc_standard = actemp.unique_acc_standard\n" +
            "where actemp.t_type = '自定义模板'\n" +
            "order by acst.num ")
    Flux<AccStandard> findByTypeAcctandardList();

    @Query("select * from _app_group_acc_standard where unique_acc_standard=:unique ")
    Mono<AccStandard> findByStandardUnique(String unique);

    Mono<Long> countByAccStandardName(String name);

    @Query("select max(cast(unique_acc_standard as Integer))+1 from _app_group_acc_standard")
    Mono<Long> getAccStandardMaxUnique();

    @Query("delete from _app_group_acc_standard where unique_acc_standard =:uniqueAccStandard ")
    Mono<Void> findByUniqueAccStandardDelStandard(String uniqueAccStandard);
}


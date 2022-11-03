package org.boozsoft.repo;

import org.boozsoft.domain.entity.account.CodeCompare;
import org.boozsoft.domain.vo.CodeCompareVo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CodeCompareRepository extends ReactiveCrudRepository<CodeCompare, String> {

    @Query("select a.id,a.iyear,a.source_code,b.ccode_name as source_name,a.target_code,c.ccode_name as target_name,a.tenant_id " +
            "from code_compare a left join code_kemu b on a.source_code=b.ccode " +
            "left join code_kemu c on a.target_code=c.ccode order by a.target_code,a.source_code ")
    Flux<CodeCompareVo> findAllByOrderBySourceCode();
    @Query("select a.id,a.iyear,a.source_code,b.ccode_name as source_name,a.target_code,c.ccode_name as target_name,a.tenant_id,a.same_source " +
            "from code_compare a left join code_kemu b on a.source_code=b.ccode " +
            "left join code_kemu c on a.target_code=c.ccode where a.iyear=:iyear order by a.target_code,a.source_code ")
    Flux<CodeCompareVo> findByIyearOrderBySourceCode(String iyear);

    @Query("delete from code_compare where same_source in (:sameSource) ")
    Mono<Void> delFindBySameSource(List<String> sameSource);
}

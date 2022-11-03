package org.boozsoft.repo;


import org.boozsoft.domain.entity.account.AccountAccvoucherCdigestClass;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountAccvoucherCdigestClassRepository extends ReactiveCrudRepository<AccountAccvoucherCdigestClass, String> {

    Flux<AccountAccvoucherCdigestClass> findAllByOrderByClassCode();
    /**
     * 获取最大的编码
     */
    @Query("select Max(class_code) as class_code from accvoucher_cdigest_class ")
    Mono<AccountAccvoucherCdigestClass> findMaxClassCode();

    /**
     * 获取缺少的最小数字
     */
    @Query("select MAX(cast(class_code as int))+1 as class_code from accvoucher_cdigest_class " +
            "where cast(class_code as int) <(select MIN(cast(class_code as int)) as minbreak " +
            "from(select class_code,ROW_NUMBER() over(order by cast(class_code as int)) as sort " +
            "from (select distinct cast(class_code as int) from accvoucher_cdigest_class) temp1) temp2 " +
            "where cast(class_code as int) <> sort) ")
    Mono<AccountAccvoucherCdigestClass> findBukongClassCode();

}


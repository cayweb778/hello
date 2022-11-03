package org.boozsoft.repo;

import org.boozsoft.domain.entity.account.AccvoucherSetting;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccvoucherSettingRepository extends ReactiveCrudRepository<AccvoucherSetting, String> {

    Flux<AccvoucherSetting> findAllByOrderByCcode();

    /**
     * 获取最大的编码
     */
    @Query("select Max(ccode) as ccode from accvoucher_setting ")
    Mono<AccvoucherSetting> findMaxCcode();

    /**
     * 获取缺少的最小数字
     */
    @Query("select MAX(cast(ccode as int))+1 as ccode from accvoucher_setting " +
            "where cast(ccode as int) <(select MIN(cast(ccode as int)) as minbreak " +
            "from(select ccode,ROW_NUMBER() over(order by cast(ccode as int)) as sort " +
            "from (select distinct cast(ccode as int) from accvoucher_setting) temp1) temp2 " +
            "where cast(ccode as int) <> sort) ")
    Mono<AccvoucherSetting> findBukongCcode();


    Flux<AccvoucherSetting> findAllByCcode(String ccode);
}

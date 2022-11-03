package org.boozsoft.repo.fa;

import org.boozsoft.domain.entity.fa.FaDepreciation;
import org.boozsoft.domain.vo.fa.FaDepreciationVo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FaDepreciationRepository extends ReactiveCrudRepository<FaDepreciation, String> {

    @Query("select a.unique_code,a.manage_code,a.card_unique,a.iyear,a.imonth, " +
            "sum(cast(COALESCE(a.zj_by,'0') as decimal)) as zj_by,sum(cast(COALESCE(a.zj_lj,'0') as decimal)) as zj_lj, " +
            "sum(cast(COALESCE(a.zj_bn,'0') as decimal)) as zj_bn,sum(cast(COALESCE(a.gzl_by,'0') as decimal)) as gzl_by, " +
            "sum(cast(COALESCE(a.gzl_lj,'0') as decimal)) as gzl_lj, " +
            "b.card_code,b.fa_name,b.speci_type,c.yuanzhi,c.yuezhejiulv,c.yuezhejiue " +
            "from fa_depreciation as a left join fa_card_master as b on a.card_unique=b.card_unique " +
            "left join fa_change as c on a.card_unique = c.card_unique " +
            "and c.iyear = (select Max(iyear) from fa_change d where a.card_unique = d.card_unique) " +
            "and c.imonth = (select Max(imonth) from fa_change d where a.card_unique = d.card_unique) " +
            "where a.manage_code=:code and a.iyear = :iyear and a.imonth = :imonth " +
            "and (c.jianshao='0' or c.jianshao is null) and (c.chaifen='0' or c.chaifen is null) " +
            "group by a.unique_code,a.manage_code,a.card_unique,a.iyear,a.imonth, " +
            "b.card_code,b.fa_name,b.speci_type,c.yuanzhi,c.yuezhejiulv,c.yuezhejiue order by b.card_code ")
    Flux<FaDepreciationVo> findByIyearAndImonth(String code, String iyear, String imonth);

    Mono<FaDepreciation> deleteByManageCodeAndIyearAndImonth(String manageCode, String iyear, String imonth);
    Mono<FaDepreciation> deleteByCardUnique(String cardUnique);

    Flux<FaDepreciation> findByManageCodeAndIyearAndImonth(String manageCode, String iyear, String imonth);

    @Query(
            "select distinct a.card_unique," +
                    "sum(cast(COALESCE(a.zj_by,'0') as decimal)) as zj_by,sum(cast(COALESCE(a.zj_lj,'0') as decimal)) as zj_lj " +
                    "from fa_depreciation a " +
                    "where 1=1 and a.card_unique =:code group by a.card_unique "
    )
    Mono<FaDepreciation> findFirstByManageCodeAndIyearAndImonth( String code);

}

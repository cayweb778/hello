package org.boozsoft.repo.fa;

import org.boozsoft.domain.entity.fa.FaChaifenHead;
import org.boozsoft.domain.vo.fa.FaChaifenVo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FaChaifenHeadRepository extends ReactiveCrudRepository<FaChaifenHead, String> {

    Flux<FaChaifenHead> findAllByManageCodeOrderByChaifenCode(String manageCode);
    @Query("select Max(chaifen_code) as chaifen_code from fa_chaifen_head where manage_code=:manageCode and iyear=:iyear and imonth=:imonth ")
    Mono<FaChaifenHead> findMaxChaifenCode(String manageCode,String iyear, String imonth);

    @Query("select a.unique_code,a.manage_code,a.card_unique,a.chaifen_code,a.chaifen_time,a.chaifen_reason,a.iyear,a.imonth, " +
            "b.card_code,b.fa_name,b.speci_type,c.shuliang,d.unit_name,c.yuanzhi,c.ljzhejiu,e.zj_by,f.zj_bn,f.zj_lj " +
            "from fa_chaifen_head a " +
            "left join fa_card_master b on a.card_unique = b.card_unique " +
            "left join fa_change c on a.card_unique = c.card_unique and a.iyear=c.iyear and a.imonth=c.imonth " +
            "left join sys_unit_of_mea d on b.unit_id=d.id " +
            "left join fa_depreciation e on a.card_unique = e.card_unique and a.iyear=e.iyear and a.imonth=e.imonth " +
            "left join fa_depreciation f on a.card_unique = f.card_unique and a.iyear<=f.iyear and a.imonth<=f.imonth " +
            "where a.manage_code=:manageCode " +
            "order by a.chaifen_code ")
    Flux<FaChaifenVo> findAllOrderByChaifenCode(String manageCode);

    Mono<FaChaifenVo> deleteByCardUnique(String cardUnique);

    Mono<FaChaifenHead> findByCardUnique(String cardUnique);

}

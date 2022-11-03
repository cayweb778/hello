package org.boozsoft.repo.stock;

import org.boozsoft.domain.entity.stock.StockShouFaType;
import org.boozsoft.domain.vo.SysDeptClassVo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface StockShouFaTypeRepository extends ReactiveCrudRepository<StockShouFaType, String> {

    @Query("select dc.*, dc.ec_name as pname from stock_shou_fa_type dc " +
            " left join stock_shou_fa_type zc on zc.id = dc.parent_id where dc.ec_name not in ('收','发') order by dc.ec_code Asc")
    Flux<SysDeptClassVo> findAllByOrderByEcCode();
    @Query("select dc.*, dc.ec_name as pname from stock_shou_fa_type dc " +
            " left join stock_shou_fa_type zc on zc.id = dc.parent_id where dc.ec_name in ('收','发') order by dc.ec_code Asc")
    Flux<SysDeptClassVo> findAllByOrderByEcCode2();

    Flux<StockShouFaType> findByFlagOrderByEcCode(String flag);
    Flux<StockShouFaType> findByFlagAndBendOrderByEcCode(String flag,String isEnd);

    /********************* Mr. Ye *******************/
    @Query("SELECT ec_code,ec_name,unique_code from stock_shou_fa_type WHERE 1=1 and flag = '1' order by ec_code Asc")
    Flux<StockShouFaType> findAllDeptCodeOrDeptNameByFlag();
    Flux<StockShouFaType> findAllByFlagAndBendAndIsAccrualOrderByEcCodeAsc(String flag, String bend, String isAcc);
    /********************* Mr. Ye *******************/

    @Query("SELECT * from stock_shou_fa_type WHERE id = :id or parent_id = :id order by ec_code asc")
    Flux<StockShouFaType> findByIdOrderByDeptCode(String id);


    @Query("SELECT cast(right(ec_code,2) as INT) from stock_shou_fa_type WHERE parent_id =:pid order by ec_code")
    Flux<Integer> findMaxCodeByPid(String pid);
    @Query("SELECT COALESCE(max(ec_code),'0') from stock_shou_fa_type WHERE parent_id =:pid ")
    Mono<String> findMaxCodeByPid2(String pid);

    @Query("SELECT count(id) from stock_shou_fa_type WHERE ec_name in ('收','发') ")
    Mono<Long> findByShouAndFa();

    @Query("SELECT * from stock_shou_fa_type WHERE ec_name = :ecName ")
    Mono<StockShouFaType> findByLikeEcName(String ecName);

    @Query("SELECT * from stock_shou_fa_type WHERE is_accrual = :isAccrual and ec_code!='1' order by ec_code ")
    Flux<StockShouFaType> findAllByIsAccrual(String isAccrual);
}

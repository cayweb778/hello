package org.boozsoft.repo.stock;

import org.boozsoft.domain.entity.stock.*;
import org.boozsoft.domain.vo.stock.StockPriceVo;
import org.boozsoft.domain.vo.stock.StockTakingVo;
import org.boozsoft.domain.vo.stock.StockVo;
import org.springbooz.core.tool.result.R;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface StockTakingRepository extends ReactiveCrudRepository<StockTaking, String> {

    @Query("select st.*, sp1.psn_name as puname, sp2.psn_name as kuname, sp3.psn_name as buname, sd.dept_name as dname, sc.ck_name as cname " +
            " from stock_taking st left join sys_psn sp1 on st.pd_userid = sp1.unique_code " +
            " left join sys_psn sp2 on st.kg_userid = sp2.unique_code " +
            " left join sys_psn sp3 on st.bcheck_user = sp3.unique_code " +
            " left join sys_department sd on st.cdepcode = sd.unique_code " +
            " left join stock_cangku sc on st.cwhcode = sc.id " +
            " where st.start_date between  :strDate and :endDate order by st.ddate desc, st.ccode desc")
    Flux<StockTakingVo> findAllByOrderById(String strDate,String endDate);

    @Query("select max(ccode) from stock_taking ")
    Mono<String> findMaxCode();

    @Modifying
    @Query("delete from stock_taking where ccode = :ccode")
    Mono<Void> deleteBycustId(String ccode);

    //查询存货分类
    @Query("select stock_class from stock_taking_class where pid=:pid ")
    Flux<String> findAllTakingClass(String pid);
    //查询存货
    @Query("select stock_num from stock_taking_cun where pid=:pid")
    Flux<String> findAllTakingCun(String pid);
    //查询存货+批次
    @Query("select stock_batch from stock_taking_cunbatch where pid=:pid")
    Flux<String> findAllTakingCunbatch(String pid);

    @Query("select * from stock_taking where bcheck != :bcheck ")
    Flux<StockTaking> findAllByBcheck(String bcheck);

    Mono<StockTaking> findAllByCcode(String ccode);

    @Query("select * from stock_taking where cwhcode = :cwhcode and bcheck != '1'")
    Flux<StockTaking> findAllByCwhcode(String cwhcode);


    Flux<StockTaking> findAllByDdateLikeOrderByBcheckAscDdateAscCcodeAsc( String like);
}




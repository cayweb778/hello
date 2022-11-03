package org.boozsoft.repo.stock;

import org.boozsoft.domain.entity.stock.StockTransferSource;
import org.boozsoft.domain.entity.stock.StockXyCsource;
import org.boozsoft.domain.vo.stock.StockTransferXySourceVo;
import org.boozsoft.domain.vo.stock.StockXySourceVo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Stream;

public interface StockTransferCsourceRepository extends ReactiveCrudRepository<StockTransferSource, String> {

    Flux<StockTransferSource> findAllByCcodeAndIyear(String xyCode,String iyear);

    @Query("delete from stock_transfer_source where ccode = :ccode")
    Mono<Void> deleteByCcode(String ccode);


    @Query("select "+
            " xy.xy_bill_style," +
            " xy.syccode," +
            " xy.ccode_date," +
            " sd.dept_name," +
            " sa.cmemo," +
            " sp.psn_name," +
            " sc.ck_name" +
            " from  stock_transfer_source xy" +
            " left JOIN stock_saleousing sa ON sa.ccode = xy.syccode" +
            " left JOIN sys_department sd on sd.unique_code = sa.cdepcode" +
            " left JOIN sys_psn sp on sp.id = sa.cpersoncode" +
            " left JOIN stock_cangku sc on sc.id = sa.cwhcode " +
            "where xy.ccode =:code order by sa.ccode ")
    Flux<StockTransferXySourceVo> findByXyOutDataSourrce(String code);


}




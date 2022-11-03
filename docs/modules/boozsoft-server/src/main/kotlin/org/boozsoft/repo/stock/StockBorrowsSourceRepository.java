package org.boozsoft.repo.stock;

import org.boozsoft.domain.entity.stock.StockBorrowsSource;
import org.boozsoft.domain.vo.stock.StockBorrowSourceVo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface StockBorrowsSourceRepository extends ReactiveCrudRepository<StockBorrowsSource, String> {

    Mono<Void> deleteByCcodeAndXyccode(String ccode,String xyccode);

    @Query("select xy.*,sb.ddate,dept.dept_name,psn.psn_name,sb.cmemo\n" +
            "from stock_borrows_source xy\n" +
            "left join stock_borrow sb on xy.ccode = sb.ccode\n" +
            "left join sys_department dept on dept.unique_code=sb.cdepcode\n" +
            "left join sys_psn psn on psn.id=sb.cpersoncode\n" +
            "where xy.ccode =:ccode \n" +
            "  and xy.xy_bill_style =:xytype ")
    Flux<StockBorrowSourceVo> findByCcodeAndXyBillStyle(String ccode, String xytype);
}

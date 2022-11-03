package org.boozsoft.domain.vo.stock;

import lombok.Data;
import org.boozsoft.domain.entity.stock.StockBorrowsSource;

/**
 * @Description  
 * @Author  myh
 * @Date 2022-10-14 
 */

@Data
public class StockBorrowSourceVo extends StockBorrowsSource {
    private String deptName;
    private String psnName;
    private String ddate;
    private String cmemo;
}

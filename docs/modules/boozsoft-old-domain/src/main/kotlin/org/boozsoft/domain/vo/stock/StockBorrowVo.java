package org.boozsoft.domain.vo.stock;

import lombok.Data;
import org.boozsoft.domain.entity.stock.StockBorrow;

/**
 * @Description  
 * @Author  myh
 * @Date 2022-10-14 
 */

@Data
public class StockBorrowVo extends StockBorrow {
    private String deptName;
    private String psnName;
    private String cmakerName;
    private String rukuSum;
    private String ljhhSum;
    private String ljzhSum;
    private String baseQuantity;
}

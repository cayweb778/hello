package org.boozsoft.domain.vo.stock;

import lombok.Data;
import org.boozsoft.domain.entity.stock.StockAds;

/**
 * @Description  
 * @Author  myh
 * @Date 2022-10-14 
 */

@Data
public class StockAdsVo extends StockAds {
    private String deptName;
    private String cmakerName;
}

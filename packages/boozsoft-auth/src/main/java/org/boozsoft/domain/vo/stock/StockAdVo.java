package org.boozsoft.domain.vo.stock;

import lombok.Data;
import org.boozsoft.domain.entity.stock.StockAd;

/**
 * @Description  
 * @Author  myh
 * @Date 2022-10-14 
 */

@Data
public class StockAdVo {
    private String bcheck;
    private String ddate;
    private String ccode;
    private String adStyle;
    private String bomName;
    private String bomVer;
    private String bomVerName;
    private String unitName;
    private String cnumber;
    private String bomLevel;
    private String deptName;
    private String feiyongJe;
    private String cmemo;
    private String cmakerName;
    private String bcheckName;
}

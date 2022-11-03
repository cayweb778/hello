package org.boozsoft.domain.vo.stock;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description  
 * @Author  myh
 * @Date 2022-08-22 
 */

@Data
public class StockJiesuanVo {
	private String ccode;
	private String ddate;
	private String quantityRuku;
	private String quantityDaohuo;
	private String priceZg;
	private String icostZg;
	private String priceJs;
	private String icostJs;
	private String custName;
	private String custCode;
}

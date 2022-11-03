package org.boozsoft.domain.vo.stock;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value="存货期初余额不足",description="存货期初余额不足")
public class StockBalanceLackVo implements Serializable {
	private String stockNum;
	private String stockName;
	private String stockGgxh;
	private String unitName;
	private BigDecimal baseQuantity;
	private BigDecimal lackBaseQuantity;
	private String batchId;
}

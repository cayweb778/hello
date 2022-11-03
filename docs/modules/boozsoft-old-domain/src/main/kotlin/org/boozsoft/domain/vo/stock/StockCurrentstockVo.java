package org.boozsoft.domain.vo.stock;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.boozsoft.domain.entity.stock.StockCurrentstock;

@Data
@ApiModel(value="现存量表",description="现存量表")
public class StockCurrentstockVo extends StockCurrentstock {

	private String accName;
	private String coCode;
	private String lockCreatedUserName;
	private String stockNum;
	private String stockName;

}

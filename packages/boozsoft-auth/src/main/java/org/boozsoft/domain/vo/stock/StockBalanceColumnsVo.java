package org.boozsoft.domain.vo.stock;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class StockBalanceColumnsVo {

	private Long batchNumber;
	private Long dvdate;
	private Long subQuantity1;
	private Long subQuantity2;
}

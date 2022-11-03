package org.boozsoft.domain.vo.stock;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.boozsoft.domain.entity.stock.StockWarehousings;

import java.io.Serializable;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class StockWarehousings3Vo implements Serializable {

	private String stockName;
	private String stockBarcode;
	private String stockGgxh;
	private String unitName;
	private String mainUnitName;
	private String cwhcode;
	private String cwhcodeName;
	private String cinvode;
	private String batchId;
	private String cnumber;
	private String itaxrate;
	private String taxprice;
	private String price;
	private String icost;
	private String isum;
	private String dpdate;
	private String dvdate;
	private String isGive;
	private String cmemo;
	private String baseQuantity;
	private String arrivalDate;
}

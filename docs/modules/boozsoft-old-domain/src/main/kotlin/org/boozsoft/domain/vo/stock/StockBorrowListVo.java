package org.boozsoft.domain.vo.stock;

import lombok.Data;


@Data
public class StockBorrowListVo {
	private String bcheck;
	private String ddate;
	private String ccode;
	private String unitType;
	private String unitValue;
	private String deptName;
	private String psnName;
	private String stockNum;
	private String stockName;
	private String stockBarcode;
	private String stockGgxh;
	private String unitName;
	private String cnumber;
	private String stockUnitName;
	private String baseQuantity;
	private String batchId;
	private String yjdate;
	private String ljhhSum;
	private String noLjhhNum;
	private String ljzhSum;
	private String cmaker;
	private String bcheckUserName;
	// 采购数量
	private String cgNumber;
	// 赠送数量
	private String zsNumber;
	private String rukuSum;
}

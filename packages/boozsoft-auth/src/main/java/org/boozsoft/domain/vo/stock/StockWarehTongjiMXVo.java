package org.boozsoft.domain.vo.stock;

import io.swagger.annotations.ApiModelProperty;
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
public class StockWarehTongjiMXVo implements Serializable {
	@ApiModelProperty(value = "审核状态")
	private String bcheck;
	@ApiModelProperty(value = "单据日期")
	private String ddate;
	private String ccode;
	private String psnName;
	private String deptName;
	private String stockNum;
	private String stockName;
	private String stockUnitName;
	private String stockBarcode;
	private String stockGgxh;
	private String unitName;
	private BigDecimal baseQuantity;
	private BigDecimal icost;
	private BigDecimal itaxprice;
	private BigDecimal isum;
	private String isGive;
	private String cnumber;
	private String cwhcode;
	private String batchId;
	private String dpdate;
	private String dvdate;
	private BigDecimal price;
	private BigDecimal taxprice;
	private String cmemo;
	private String itaxrate;
	private String cvencode;
	private String cvencodeName;
	private String cvencodeJs;
	private String cvencodeJsname;
	private String arrivalDate;
	private String unitValueName;
	private String unitTransName;
	private String bstyle;
	private String invoiceStyle;
	private String billCode;
	private String billNumber;
	private String billDate;
}

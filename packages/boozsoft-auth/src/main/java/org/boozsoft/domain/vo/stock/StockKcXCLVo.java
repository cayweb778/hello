package org.boozsoft.domain.vo.stock;

import io.swagger.annotations.ApiModel;
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
public class StockKcXCLVo implements Serializable {

	private String stockMeasurementType;
	private String stockMeasurementUnit;
	private String stockUnitName;
	private String stockUnitName1;
	private String stockUnitName2;
	private String stockId;
	private String stockNum;
	private String stockName;
	private String stockGgxh;
	private String stockClass;
	private String iyear;
	private String invCode;
	private String cwhcode;
	private String cwhcode1;
	private String cwhcodeName;

	private String batchId;
	private String dpdate;
	private String dvdate;

	private BigDecimal keyong;
	private BigDecimal keyong1;
	private BigDecimal keyong2;

	private BigDecimal baseQuantity;
	private BigDecimal subQuantity1;
	private BigDecimal subQuantity2;
	// 换算率
	private BigDecimal cunitidF1;
	private BigDecimal cunitidF2;

	// 在途入库-采购到货主数量
	private BigDecimal ztrkQuantityCgdh;
	private BigDecimal cgdh1;
	private BigDecimal cgdh2;
	// 在途入库-采购入库单主数量
	private BigDecimal ztrkQuantityCgrk;
	private BigDecimal cgrk1;
	private BigDecimal cgrk2;
	// 在途入库-其他入库单主数量
	private BigDecimal ztrkQuantityQtrk;
	private BigDecimal qtrk1;
	private BigDecimal qtrk2;
	// 在途入库-产成品入库单主数量
	private BigDecimal ztrkQuantityCcprk;
	private BigDecimal ccprk1;
	private BigDecimal ccprk2;
	// 在途入库-借入单主数量
	private BigDecimal ztrkQuantityInt;
	private BigDecimal int1;
	private BigDecimal int2;


	// 在途入库-销货单主数量
	private BigDecimal ztckQuantityXhd;
	private BigDecimal xhd1;
	private BigDecimal xhd2;
	// 在途入库-销售出库单主数量
	private BigDecimal ztrkQuantityXsck;
	private BigDecimal xsck1;
	private BigDecimal xsck2;
	// 在途入库-材料出库单主数量
	private BigDecimal ztrkQuantityClly;
	private BigDecimal clly1;
	private BigDecimal clly2;
	// 在途入库-其他出库单主数量
	private BigDecimal ztrkQuantityQtck;
	private BigDecimal qtck1;
	private BigDecimal qtck2;
	// 在途入库-借出单主数量
	private BigDecimal ztrkQuantityOut;
	private BigDecimal out1;
	private BigDecimal out2;
}

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
public class StockWarehousings2Vo implements Serializable {
	@ApiModelProperty(value = "供应商名称")
	private String custName;
	private String custNum;
	@ApiModelProperty(value = "存货名称")
	private String stockName;
	private String cinvode;
	@ApiModelProperty(value = "规格型号")
	private String stockGgxh;
	private String stockClass;
	private String stockBarcode;
	@ApiModelProperty(value = "计量单位名称")
	private String unitName;
	private String mainUnitName;
	private String cnumber;

	@ApiModelProperty(value = "数量")
	private BigDecimal baseQuantity;
	private BigDecimal subQuantity1;
	private BigDecimal subQuantity2;
	@ApiModelProperty(value = "无税金额")
	private String icost;
	@ApiModelProperty(value = "税额")
	private String itaxprice;
	@ApiModelProperty(value = "价税合计")
	private BigDecimal isum;
	@ApiModelProperty(value = "累计入库")
	private String isumRuku;
	@ApiModelProperty(value = "累计退货")
	private String isumTuihuo;
	@ApiModelProperty(value = "累计发票")
	private String isumFapiao;
	@ApiModelProperty(value = "累计结款")
	private String isumJieKuan;
	@ApiModelProperty(value = "累计到货")
	private String isumDaohuo;
	// 计量单位换算率
	private String rate;
}

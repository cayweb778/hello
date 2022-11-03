package org.boozsoft.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class KeMuBalanceVo {
	private String igrade;			// 科目级次
	private String uniqueCode;
	@ApiModelProperty(value = "科目编码")
	private String ccode;
	@ApiModelProperty(value = "科目名称")
	private String ccodeName;
	private String temp1;
	private String temp2;

	@ApiModelProperty(value = "末级标识")
	private String findByBend;
	private String cclass;
	@ApiModelProperty(value = "辅助项")
	private String fuzhu;
	@ApiModelProperty(value = "计量单位")
	private String unitMeasurement;
	@ApiModelProperty(value = "外币名称")
	private String foreignCurrency;
	/********************* 期初 ************************/
		@ApiModelProperty(value = "借方")
		private BigDecimal qcMd;
		@ApiModelProperty(value = "贷方")
		private BigDecimal qcMc;
		@ApiModelProperty(value = "期初数量")
		private BigDecimal qcNum;
		@ApiModelProperty(value = "期初原币金额")
		private BigDecimal qcNfrat;

	/********************* 凭证 ************************/
		@ApiModelProperty(value = "借方")
		private BigDecimal pzMd;
		@ApiModelProperty(value = "贷方")
		private BigDecimal pzMc;
		@ApiModelProperty(value = "凭证数量")
		private BigDecimal pzNum;
		@ApiModelProperty(value = "凭证原币金额")
		private BigDecimal pzNfrat;
	/********************* 累计 ************************/
		@ApiModelProperty(value = "借方")
		private BigDecimal ljMd;
		@ApiModelProperty(value = "贷方")
		private BigDecimal ljMc;
		@ApiModelProperty(value = "凭证数量")
		private BigDecimal ljNum;
		@ApiModelProperty(value = "凭证原币金额")
		private BigDecimal ljNfrat;
	/********************* 期末余额 ************************/
		@ApiModelProperty(value = "借方")
		private BigDecimal qmMd;
		@ApiModelProperty(value = "贷方")
		private BigDecimal qmMc;
		@ApiModelProperty(value = "凭证数量")
		private BigDecimal qmNum;
		@ApiModelProperty(value = "凭证原币金额")
		private BigDecimal qmNfrat;
}

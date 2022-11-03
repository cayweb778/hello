package org.boozsoft.domain.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value="会计凭证表",description="会计凭证表")
public class AccvoucherVo2 {


	@ApiModelProperty(value = "借方金额", position = 13)
	private BigDecimal md;

	@ApiModelProperty(value = "贷方金额", position = 14)
	private BigDecimal mc;

	@ApiModelProperty(value = "借方外币汇率", position = 15)
	private String mdF;

	@ApiModelProperty(value = "贷方外币汇率", position = 16)
	private String mcF;

	@ApiModelProperty(value = "借方数量", position = 17)
	private BigDecimal ndS;

	@ApiModelProperty(value = "贷方数量", position = 18)
	private BigDecimal ncS;

	@ApiModelProperty(value = "外币借方金额", position = 19)
	private BigDecimal nfratMd;
	@ApiModelProperty(value = "外币贷方金额", position = 19)
	private BigDecimal nfratMc;

	@ApiModelProperty(value = "部门唯一码", position = 32)
	private String cdeptId;

	@ApiModelProperty(value = "个人唯一码", position = 33)
	private String cpersonId;

	@ApiModelProperty(value = "客户唯一码", position = 34)
	private String ccusId;

	@ApiModelProperty(value = "供应商唯一码", position = 35)
	private String csupId;

	@ApiModelProperty(value = "项目大类唯一码", position = 36)
	private String projectClassId;

	@ApiModelProperty(value = "项目唯一码", position = 37)
	private String projectId;

	@ApiModelProperty(value = "计量单位", position = 43)
	private String unitMeasurement;
	@ApiModelProperty(value = "外币币种", position = 43)
	private String foreignCurrency;

	private String deptName;
	private String psnName;
	private String custName;
	private String supName;
	private String proName;

	private String deptCode;
	private String psnCode;
	private String custCode;
	private String supCode;
	private String proCode;
}

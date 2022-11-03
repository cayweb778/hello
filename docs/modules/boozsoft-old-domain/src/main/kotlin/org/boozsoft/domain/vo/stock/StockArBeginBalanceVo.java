package org.boozsoft.domain.vo.stock;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Description  
 * @Author  myh
 * @Date 2022-04-14 
 */

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value="应收款期初余额",description="应收款期初余额")
public class StockArBeginBalanceVo implements Serializable {


	private String id;
	private String tenantId;
	private String iyear;

	@ApiModelProperty(value = "行次唯一码")
	private Long lineId;

	@ApiModelProperty(value = "应收类型（XSFP销售发票、YSK应收款、YUSK预收款、YSPJ应收票据）")
	private String arStyle;

	@ApiModelProperty(value = "收款方向（1预收、0应收）")
	private String arDir;

	@ApiModelProperty(value = "单据日期(日期必须小于当前年度启用月)")
	private String ddate;

	@ApiModelProperty(value = "客户唯一码")
	private String custId;
	private String custName;

	@ApiModelProperty(value = "项目大类唯一码")
	private String citemClass;

	@ApiModelProperty(value = "项目唯一码")
	private String citemId;
	private String citemName;

	@ApiModelProperty(value = "业务员唯一码")
	private String cmakerId;

	@ApiModelProperty(value = "币种国际编码")
	private String abbreviation;

	@ApiModelProperty(value = "原币预收账款")
	private BigDecimal ysIsumYuanbi;

	@ApiModelProperty(value = "本币预收账款")
	private BigDecimal ysIsumBenbi;

	@ApiModelProperty(value = "原币应收账款")
	private BigDecimal arIsumYuanbi;

	@ApiModelProperty(value = "本币应收账款")
	private BigDecimal arIsumBenbi;

	@ApiModelProperty(value = "原币期初余额")
	private BigDecimal isumYuanbi;

	@ApiModelProperty(value = "本币期初余额")
	private BigDecimal isumBenbi;

	@ApiModelProperty(value = "审核状态（1审核，0或其他未审）")
	private String bcheck;

	@ApiModelProperty(value = "审核时间")
	private String bcheckTime;

	@ApiModelProperty(value = "审核人id")
	private String bcheckUser;
	private String bcheckUserName;

	@ApiModelProperty(value = "备注")
	private String cmemo;
}

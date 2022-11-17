package org.boozsoft.domain.entity;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.experimental.Accessors;
import lombok.NoArgsConstructor;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Table ("_app_group_sys_period" )
@ApiModel(value="系统会计期间表",description="系统会计期间表")
public class SysPeriod {

	@Id
	@ApiModelProperty(value = "主键", position = 0)
	@CreatedBy
	@JsonSerialize(using = ToStringSerializer.class)
	private String id;

	@ApiModelProperty(value = "会计个数（>=12）", position = 1)
	private String iperiodNum;

	@ApiModelProperty(value = "年度", position = 2)
	private String iyear;

	@ApiModelProperty(value = "起始月日", position = 3)
	private String dateStart;

	@ApiModelProperty(value = "结束月日", position = 4)
	private String dateEnd;

	@ApiModelProperty(value = "账套id", position = 5)
	private String accountId;

	@ApiModelProperty(value = "总账启用期间（1-12中任意一个）", position = 6)
	private String enablePeriod;

	@ApiModelProperty(value = "区分会计区间与季度 1为季度 其他为期间", hidden = true)
	private String beiyong1;

	@ApiModelProperty(value = "固定资产启用区间", hidden = true)
	private String fixedAssets;

	@ApiModelProperty(value = "应收款启用区间", hidden = true)
	private String receivables;

	@ApiModelProperty(value = "应付款启用区间", hidden = true)
	private String payable;

	@ApiModelProperty(value = "销售启用区间", hidden = true)
	private String sales;

	@ApiModelProperty(value = "采购启用区间", hidden = true)
	private String purchase;

	@ApiModelProperty(value = "", hidden = true)
	private String period;

	@ApiModelProperty(value = "", hidden = true)
	private String beiyong3;

	@ApiModelProperty(value = "总账结转")
	private String gl;

	@ApiModelProperty(value = "结账操作员")
	private String glUser;

	@ApiModelProperty(value = "结账时间")
	private String glTime;

	@Transient
	private String value;
	@Transient
	private String label;
	@Transient
	private String title;
}

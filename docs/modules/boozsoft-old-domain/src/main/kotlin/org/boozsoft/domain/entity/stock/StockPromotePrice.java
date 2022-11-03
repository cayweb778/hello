package org.boozsoft.domain.entity.stock;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table ( "stock_promote_price" )
@ApiModel(value="客户促销价格表",description="客户促销价格表")
public class StockPromotePrice {

	@Id
	@ApiModelProperty(value = "ID", hidden = true)
	@CreatedBy
	@JsonSerialize(using = ToStringSerializer.class)
	private String id;
	@Transient
	@ApiModelProperty(value = "公司唯一码",hidden = true)
	private String tenantId;
	@ApiModelProperty(value = "客户唯一码",hidden = true)
	private String custId;
	@ApiModelProperty(value = "存货编码",hidden = true)
	private String stockNum;
	@ApiModelProperty(value = "普通客户价（10小数位）",hidden = true)
	private String memberprice;
	@ApiModelProperty(value = "状态(1停用，0若其他正常）",hidden = true)
	private String status;
	@ApiModelProperty(value = "最新进价",hidden = true)
	private String newPrice;
	@ApiModelProperty(value = "录入人姓名",hidden = true)
	private String operatorName;
	@ApiModelProperty(value = "录入时间",hidden = true)
	private String operatorTime;
	@ApiModelProperty(value = "活动id",hidden = true)
	private String activityId;
	@ApiModelProperty(value = "",hidden = true)
	private String cfree1;
	@ApiModelProperty(value = "",hidden = true)
	private String cfree2;
	@ApiModelProperty(value = "",hidden = true)
	private String cfree3;
	@ApiModelProperty(value = "",hidden = true)
	private String cfree4;
	@ApiModelProperty(value = "",hidden = true)
	private String cfree5;


}

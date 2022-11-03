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
@Table ( "stock_promote_activity" )
@ApiModel(value="客户促销活动表",description="客户促销活动表")
public class StockPromoteActivity {

	@Id
	@ApiModelProperty(value = "ID", hidden = true)
	@CreatedBy
	@JsonSerialize(using = ToStringSerializer.class)
	private String id;
	@Transient
	@ApiModelProperty(value = "公司唯一码",hidden = true)
	private String tenantId;
	@ApiModelProperty(value = "活动编码",hidden = true)
	private String activityCode;
	@ApiModelProperty(value = "活动名称",hidden = true)
	private String activityName;
	@ApiModelProperty(value = "活动内容",hidden = true)
	private String activityContent;
	@ApiModelProperty(value = "活动范围",hidden = true)
	private String activityScope;
	@ApiModelProperty(value = "价格生效日期",hidden = true)
	private String startDate;
	@ApiModelProperty(value = "价格失效日期",hidden = true)
	private String stopDate;
	@ApiModelProperty(value = "录入人姓名",hidden = true)
	private String operatorName;
	@ApiModelProperty(value = "录入时间",hidden = true)
	private String operatorTime;
	@ApiModelProperty(value = "状态1启用",hidden = true)
	private String flag;

	@Transient
	private String status;
}

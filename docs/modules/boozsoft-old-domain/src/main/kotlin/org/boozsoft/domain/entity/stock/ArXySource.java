package org.boozsoft.domain.entity.stock;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table ( "ar_xy_source" )
@Accessors(chain = true)
@ApiModel(value="下游主表",description="下游主表")
public class ArXySource {

	@Id
	@ApiModelProperty(value = "ID", hidden = true)
	@CreatedBy
	@JsonSerialize(using = ToStringSerializer.class)
	private String id;
	@Transient
	@ApiModelProperty(value = "公司唯一码",hidden = true)
	private String tenantId;
	@ApiModelProperty(value = "年度标志",hidden = true)
	private String iyear;
	@ApiModelProperty(value = "上游类型",hidden = true)
	private String billStyle;
	@ApiModelProperty(value = "上游单据号",hidden = true)
	private String ccode;
	@ApiModelProperty(value = "上游单据日期",hidden = true)
	private String ccodeDate;
	@ApiModelProperty(value = "下游类型",hidden = true)
	private String xyBillStyle;
	@ApiModelProperty(value = "下游单据号",hidden = true)
	private String xyccode;
	@ApiModelProperty(value = "下游单据日期",hidden = true)
	private String xyccodeDate;

}

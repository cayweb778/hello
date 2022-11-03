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
@Table ( "stock_ccode" )
@Accessors(chain = true)
@ApiModel(value="凭证生单会计科目设置主表",description="凭证生单会计科目设置主表")
public class StockCcode {

	@Id
	@ApiModelProperty(value = "ID", hidden = true)
	@CreatedBy
	@JsonSerialize(using = ToStringSerializer.class)
	private String id;
	@Transient
	@ApiModelProperty(value = "公司唯一码",hidden = true)
	private String tenantId;
	@ApiModelProperty(value = "类别唯一码", hidden = true)
	private String uniqueId;
	@ApiModelProperty(value = "类别名称", hidden = true)
	private String stName;
	@ApiModelProperty(value = "会计科目编码（末级会计科目）", hidden = true)
	private String ccode;
	@ApiModelProperty(value = "科目名称", hidden = true)
	private String ccodeName;
	@ApiModelProperty(value = "添加人唯一码（操作员）", hidden = true)
	private String cpersoncode;
	@ApiModelProperty(value = "添加时间", hidden = true)
	private String cmakerTime;
	@ApiModelProperty(value = "", hidden = true)
	private String cfree1;
	@ApiModelProperty(value = "", hidden = true)
	private String cfree2;
	@ApiModelProperty(value = "", hidden = true)
	private String cfree3;
}

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
@Table ( "stock_ccodes" )
@Accessors(chain = true)
@ApiModel(value="凭证生单会计科目设置子表",description="凭证生单会计科目设置子表")
public class StockCcodes {

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
	@ApiModelProperty(value = "存货分类编码", hidden = true)
	private String stName;
	@ApiModelProperty(value = "仓库唯一码", hidden = true)
	private String ckName;
	@ApiModelProperty(value = "会计科目编码（末级会计科目）", hidden = true)
	private String ccode;
	@ApiModelProperty(value = "科目名称", hidden = true)
	private String ccodeName;
	@ApiModelProperty(value = "", hidden = true)
	private String field1;
	@ApiModelProperty(value = "", hidden = true)
	private String field2;
	@ApiModelProperty(value = "", hidden = true)
	private String field3;
	@ApiModelProperty(value = "", hidden = true)
	private String field4;
	@ApiModelProperty(value = "", hidden = true)
	private String field5;
	@ApiModelProperty(value = "", hidden = true)
	private String field6;
	@ApiModelProperty(value = "", hidden = true)
	private String field7;
	@ApiModelProperty(value = "", hidden = true)
	private String field8;
	@ApiModelProperty(value = "", hidden = true)
	private String field9;
	@ApiModelProperty(value = "", hidden = true)
	private String field10;
	@ApiModelProperty(value = "", hidden = true)
	private String cfree1;
	@ApiModelProperty(value = "", hidden = true)
	private String cfree2;
	@ApiModelProperty(value = "", hidden = true)
	private String cfree3;
	@ApiModelProperty(value = "", hidden = true)
	private String cfree4;
	@ApiModelProperty(value = "", hidden = true)
	private String cfree5;
	@ApiModelProperty(value = "", hidden = true)
	private String cfree6;
	@ApiModelProperty(value = "", hidden = true)
	private String cfree7;
	@ApiModelProperty(value = "", hidden = true)
	private String cfree8;
	@ApiModelProperty(value = "", hidden = true)
	private String cfree9;
	@ApiModelProperty(value = "", hidden = true)
	private String cfree10;
}

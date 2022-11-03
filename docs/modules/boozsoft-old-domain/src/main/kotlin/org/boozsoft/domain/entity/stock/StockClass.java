package org.boozsoft.domain.entity.stock;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table ("stock_class" )
@ApiModel(value="存货分类",description="存货分类")
@AllArgsConstructor
@NoArgsConstructor
public class StockClass {

	@Id
	@CreatedBy
	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty(value = "主键", position = 0)
	private String id;

	@ApiModelProperty(value = "唯一编码", position = 1)
	private String uniqueStockclass;

	@ApiModelProperty(value = "客户分类编码", position = 2)
	private String stockClass;

	@ApiModelProperty(value = "客户分类级次，与会计科目级次类似", position = 3)
	private String stockClassGrade;

	@ApiModelProperty(value = "上级客户分类唯一编码", hidden = true)
	private String parentId;

	@ApiModelProperty(value = "客户分类名称", position = 4)
	private String stockCclassName;

	@ApiModelProperty(value = "是否末级分类（1是，0否）", position = 5)
	private String stockBend;

	@ApiModelProperty(value = "是否停用(1.启用;0停用)", position = 6)
	private String flag;

	private String beiyong1;

	private String beiyong2;

	private String beiyong3;

	private String beiyong4;

	private String beiyong5;

	@ApiModelProperty(value = "客户分类级次编码（默认3位）", position = 12)
	private String stockGradeCode;

	@Transient
	private String superClassName;


	@Transient
	private String label;

	@Transient
	private String value;
}

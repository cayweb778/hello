package org.boozsoft.domain.entity.group;


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
@Table ("_app_group_supplier_class" )
@ApiModel(value="集团供应商分类",description="集团供应商分类")
public class GroupSupplierClass {

	@Id
	@CreatedBy
	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty(value = "主键", position = 0)
	private String id;

	@ApiModelProperty(value = "唯一编码", position = 1)
	private String uniqueCustclass;

	@ApiModelProperty(value = "供应商分类编码", position = 2)
	private String cusClass;

	@ApiModelProperty(value = "供应商分类级次，与会计科目级次类似", position = 3)
	private String cusClassGrade;

	@ApiModelProperty(value = "上级供应商分类唯一编码", hidden = true)
	private String parentId;

	@ApiModelProperty(value = "供应商分类名称", position = 4)
	private String cusCclassName;

	@ApiModelProperty(value = "是否末级分类（1是，0否）", position = 5)
	private String cusBend;

	@ApiModelProperty(value = "是否停用(1.启用;0停用)", position = 6)
	private String flag;

	private String beiyong1;
	private String beiyong2;
	private String beiyong3;
	private String beiyong4;
	private String beiyong5;

	@ApiModelProperty(value = "供应商分类级次编码（默认3位）", position = 12)
	private String cusGradeCode;
	@Transient
	private String superClassName;
}

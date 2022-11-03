package org.boozsoft.domain.entity;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Table ("supplier_class_rollback" )
@ApiModel(value="供应商分类回滚表",description="supplier_class_rollback: table")
public class SupplierClassRollback {

	@Id
	@ApiModelProperty(value = "主键", position = 0)
	private String id;

	@ApiModelProperty(value = "唯一编码", position = 1)
	private String uniqueSupclass;

	@ApiModelProperty(value = "供应商分类编码", position = 2)
	private String supClass;

	@ApiModelProperty(value = "供应商分类级次，与会计科目级次类似", position = 3)
	private String supClassGrade;

	@ApiModelProperty(value = "供应商分类名称", position = 4)
	private String supClassName;

	@ApiModelProperty(value = "是否末级分类（1是，0否）", position = 5)
	private String supBend;

	@ApiModelProperty(value = "是否停用(1.启用;0停用)", position = 6)
	private String flag;

	private String beiyong1;

	private String beiyong2;

	private String beiyong3;

	private String beiyong4;

	private String beiyong5;

	@ApiModelProperty(value = "变动日期", position = 12)
	private String biandongDate;

	@ApiModelProperty(value = "变动方式(1.修改;2.删除;3.停用)", position = 13)
	private String biandongMethod;

	@ApiModelProperty(value = "操作员姓名", position = 14)
	private String biandongName;

	@ApiModelProperty(value = "操作员唯一编码", position = 15)
	private String biandongUniqueCode;

	@ApiModelProperty(value = "供应商分类级次编码（默认3位）", position = 16)
	private String supGradeCode;

	@ApiModelProperty(value = "上级ID", position = 17)
	private String parentId;


}

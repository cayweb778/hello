package org.boozsoft.domain.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 供应商 - 客户  用同一个
 */
@Data
@ApiModel(value="集团客户分类",description="集团客户分类")
public class GroupCustomerClassVo {


	private String id;

	@ApiModelProperty(value = "唯一编码", position = 1)
	private String uniqueCustclass;

	@ApiModelProperty(value = "客户分类编码", position = 2)
	private String cusClass;

	@ApiModelProperty(value = "客户分类级次，与会计科目级次类似", position = 3)
	private String cusClassGrade;

	@ApiModelProperty(value = "上级客户分类唯一编码", hidden = true)
	private String parentId;

	@ApiModelProperty(value = "客户分类名称", position = 4)
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

	@ApiModelProperty(value = "客户分类级次编码（默认3位）", position = 12)
	private String cusGradeCode;
	private String superClassName;
}

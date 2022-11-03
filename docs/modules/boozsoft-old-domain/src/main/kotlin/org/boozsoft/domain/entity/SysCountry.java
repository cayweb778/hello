package org.boozsoft.domain.entity;


import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table ("_app_group_sys_country" )
@ApiModel(value="国家表",description="国家表")
public class SysCountry {

	@ApiModelProperty(value = "主键id", position = 0)
	@Id
	private String id;

	@ApiModelProperty(value = "三位字母组成的国家代码", position = 1)
	private String uniqueCode;

	@ApiModelProperty(value = "三位数字组成的国家编码", position = 2)
	private String countryId;
	@ApiModelProperty(value = "国家的英文名称", position = 3)
	private String nameen;

	@ApiModelProperty(value = "国家的中文简称", position = 4)
	private String namech;
	@ApiModelProperty(value = "国家的中文全称", position = 4)
	private String namechFull;

	@ApiModelProperty(value = "排序号", position = 5)
	private Integer num;

	private Integer beiyong1;

	private Integer beiyong2;

	private Integer beiyong3;

	private Integer beiyong4;

	private Integer beiyong5;


}

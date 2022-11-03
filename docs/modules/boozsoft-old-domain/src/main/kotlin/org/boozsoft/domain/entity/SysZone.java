package org.boozsoft.domain.entity;


import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.experimental.Accessors;
import lombok.NoArgsConstructor;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Table ("sys_zone" )
@ApiModel(value="sys_zone: table",description="sys_zone: table")
public class SysZone {

	@Id
	@ApiModelProperty(value = "主键ID", position = 0)
	private String id;

	@ApiModelProperty(value = "省市区名称", position = 1)
	private String zoneName;

	@ApiModelProperty(value = "省市编码", position = 2)
	private Integer pid;

	@ApiModelProperty(value = "拼音", position = 3)
	private String romanLetters;

	@ApiModelProperty(value = "省区域代码", position = 4)
	private String procode;


}

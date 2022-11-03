package org.boozsoft.domain.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Table ("_app_group_sys_dist_record" )
@ApiModel(value="档案分配表",description="档案分配表")
public class SysDistRecord {

	@ApiModelProperty(value = "主键id", position = 0)
	@Id
	private String id;

	@ApiModelProperty(value = "账套唯一编码", position = 1)
	private String databaseUniqueCode;

	@ApiModelProperty(value = "档案唯一码", position = 2)
	private String recordUniqueCode;

	@ApiModelProperty(value = "档案类型 客户：customer项目：project,人员：personal(数据库表名)", position = 3)
	private String recordType;


}

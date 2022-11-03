package org.boozsoft.domain.entity.group;


import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table ("_app_group_group_dist" )
@ApiModel(value="集团档案分配管控权限表",description="集团档案分配管控权限表")
public class GroupDist {

	@ApiModelProperty(value = "主键id", position = 0)
	@Id
	private String id;

	@ApiModelProperty(value = "账套唯一编码", position = 1)
	private String databaseUniqueCode;

	@ApiModelProperty(value = "账套名称", position = 2)
	private String databaseName;

	@ApiModelProperty(value = "基础档案数据表名称", position = 3)
	private String tableName;

	@ApiModelProperty(value = "基础档案名称", position = 4)
	private String archivesName;

	@ApiModelProperty(value = "是否管控:1是0否", position = 5)
	private String isControl;

	@ApiModelProperty(value = "是否允许修改关键字:1是0否", position = 6)
	private String isKeyword;

	@ApiModelProperty(value = "是否允许修改其他信息:1是0否", position = 7)
	private String isOther;

	@ApiModelProperty(value = "新增是否自动分配:1是0否", position = 8)
	private String isAuto;

	private String beiyong1;

	private String beiyong2;

	private String beiyong3;

	private String beiyong4;

	private String beiyong5;


}

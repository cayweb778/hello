package org.boozsoft.domain.entity.group;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table ("_app_group_fa_zhejiu_method" )
@ApiModel(value="折旧方法",description="折旧方法")
public class GroupFaZhejiuMethod {

	@ApiModelProperty(value = "主键id")
	@Id
	@CreatedBy
	@JsonSerialize(using = ToStringSerializer.class)
	private String id;

	@ApiModelProperty(value = "唯一标识", hidden = true)
	private String uniqueCode;

	@ApiModelProperty(value = "折旧方法", hidden = true)
	private String zjName;

	@ApiModelProperty(value = "月折旧率公式", hidden = true)
	private String lFormula;

	@ApiModelProperty(value = "月折旧额公式", hidden = true)
	private String eFormula;

	@ApiModelProperty(value = "员工唯一编码", hidden = true)
	private String uniqueCodeUser;

	@ApiModelProperty(value = "创建日期", hidden = true)
	private String createDate;

	@ApiModelProperty(value = "是否停用(1.启用;0停用)", hidden = true)
	private String flag;

	@ApiModelProperty(value = "上级id", hidden = true)
	private String parentId;

	@ApiModelProperty(value = "类型(1.系统;自定义)", hidden = true)
	private String zjType;


}

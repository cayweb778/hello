package org.boozsoft.domain.entity.group;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Table ("_app_group_sys_job_type" )
@ApiModel(value="职务类别表",description="职务类别表")
public class GroupSysJobType {

	@ApiModelProperty(value = "id", position = 0)
	@Id	@CreatedBy	@JsonSerialize(using = ToStringSerializer.class)
	private String id;

	@ApiModelProperty(value = "唯一标识", hidden = true)
	private String uniqueCode;

	@ApiModelProperty(value = "编码（不允许重复）", hidden = true)
	private String ecCode;

	@ApiModelProperty(value = "上级ID", hidden = true)
	@JsonSerialize(using = ToStringSerializer.class)
	private String parentId;

	@ApiModelProperty(value = "是否末级", hidden = true)
	private String bend;

	@ApiModelProperty(value = "名称", hidden = true)
	private String ecName;

	@ApiModelProperty(value = "员工唯一编码", hidden = true)
	private String uniqueCodeUser;

	@ApiModelProperty(value = "创建日期", hidden = true)
	private String createDate;

	@ApiModelProperty(value = "是否停用(1.启用;0停用)", hidden = true)
	private String flag;



}

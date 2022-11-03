package org.boozsoft.domain.entity;


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
@Table ("sys_job_file" )
@ApiModel(value="职务档案表",description="职务档案表")
public class SysJobFile {

	@ApiModelProperty(value = "id", position = 0)
	@Id	@CreatedBy	@JsonSerialize(using = ToStringSerializer.class)
	private String id;

	@ApiModelProperty(value = "编码（不允许重复）", hidden = true)
	private String ecCode;

	@ApiModelProperty(value = "名称", hidden = true)
	private String ecName;

	@ApiModelProperty(value = "是否末级", hidden = true)
	private String bend;

	@ApiModelProperty(value = "类别ID", hidden = true)
	private String typeId;

	@ApiModelProperty(value = "员工唯一编码", hidden = true)
	private String uniqueCodeUser;

	@ApiModelProperty(value = "创建日期", hidden = true)
	private String createDate;

	@ApiModelProperty(value = "是否停用(1.启用;0停用)", hidden = true)
	private String flag;

	@ApiModelProperty(value = "设立日期", hidden = true)
	private String estDate;

	@ApiModelProperty(value = "管理等级", hidden = true)
	private String mangeLevel;

	@ApiModelProperty(value = "工作概要", hidden = true)
	private String workInfo;

	@ApiModelProperty(value = "级别1-24级", hidden = true)
	private String psnLevel;

}

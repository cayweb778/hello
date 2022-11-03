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
@Table ("_app_group_sys_zf_class" )
@ApiModel(value="政府支出经济分类",description="政府支出经济分类")
public class GroupSysZfClass {

	//编码级次：3-2-2-2-2
	@ApiModelProperty(value = "id", position = 0)
	@Id	@CreatedBy	@JsonSerialize(using = ToStringSerializer.class)
	private String id;

	@ApiModelProperty(value = "唯一标识", hidden = true)
	private String uniqueCode;

	@ApiModelProperty(value = "编码（不允许重复）", hidden = true)
	private String ecCode;

	@ApiModelProperty(value = "名称", hidden = true)
	private String ecName;

	@ApiModelProperty(value = "级次", hidden = true)
	private String ecLevel;

	@ApiModelProperty(value = "是否末级", hidden = true)
	private String bend;

	@ApiModelProperty(value = "上级ID", hidden = true)
	@JsonSerialize(using = ToStringSerializer.class)
	private String parentId;

	@ApiModelProperty(value = "员工唯一编码", hidden = true)
	private String uniqueCodeUser;

	@ApiModelProperty(value = "创建日期", hidden = true)
	private String createDate;

	@ApiModelProperty(value = "是否停用(1.启用;0停用)", hidden = true)
	private String flag;

	@ApiModelProperty(value = "是否删除", hidden = true)
	private String isDel;

	@ApiModelProperty(value = "删除人姓名", hidden = true)
	private String delName;

	@ApiModelProperty(value = "删除时间", hidden = true)
	private String delDate;


}

package org.boozsoft.domain.entity.acctemplate;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.experimental.Accessors;
import lombok.NoArgsConstructor;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Table ("_app_group_acc_template" )
@ApiModel(value="科目模板表",description="acc_template: table")
public class AccTemplate {

	@ApiModelProperty(value = "主键id", position = 0)
	@Id
	@CreatedBy
	@JsonSerialize(using = ToStringSerializer.class)
	private String id;

	@ApiModelProperty(value = "名称", position = 2)
	private String tName;

	@ApiModelProperty(value = "科目级次", position = 3)
	private String tJici;

	@ApiModelProperty(value = "类型（系统-独立账套读取、自定义-集团账套读取）", position = 4)
	private String tType;

	@ApiModelProperty(value = "是否预置数据:1是", position = 4)
	private String tFlg;

	@ApiModelProperty(value = "排序", position = 4)
	private Integer tNum;

	@ApiModelProperty(value = "科目准则表唯一码", position = 5)
	private String uniqueAccStandard;
	@ApiModelProperty(value = "组织模式使用：已使用的集团会计科目模板（自定义）", position = 5)
	private String tPid;
	@ApiModelProperty(value = "所属哪个组织", position = 5)
	private String tOrganization;

	@Transient
	private String styleName;
	@Transient
	private String parentName;
}

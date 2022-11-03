package org.boozsoft.domain.entity;


import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.annotation.CreatedBy;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.experimental.Accessors;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Table ("_app_group_sys_role" )
@ApiModel(value="角色表",description="角色表")
public class AppGroupSysRole {

	@Id	@CreatedBy	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty(value = "主键")
	private String id;

	@ApiModelProperty(value = "租户ID")
	private String tenantId;

	@ApiModelProperty(value = "父主键")
	private String parentId;

	@ApiModelProperty(value = "角色名")
	private String roleName;

	@ApiModelProperty(value = "排序")
	private Integer sort;

	@ApiModelProperty(value = "角色别名")
	private String roleAlias;

	@ApiModelProperty(value = "是否已删除")
	private Integer isDeleted;

	@ApiModelProperty(value = "编码：自动生成3位-N")
	private String roleNum;

	@ApiModelProperty(value = "操作范围：1集团、2组织、3公司")
	private String roleRange;

	@ApiModelProperty(value = "建立日期")
	private String roleAddTime;

	@ApiModelProperty(value = "建立人")
	private String roleAddPsn;

	@ApiModelProperty(value = "状态（0否，1启用）")
	private String roleFlag;
	@Transient
	private String roleRangeName;

}

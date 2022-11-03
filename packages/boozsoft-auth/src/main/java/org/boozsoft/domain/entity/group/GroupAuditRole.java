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
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Table ("_app_group_audit_role" )
@ApiModel(value="角色审计表",description="角色审计表")
public class GroupAuditRole {

	@Id	@CreatedBy	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty(value = "主键")
	private String id;

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
	@ApiModelProperty(value = "审计时间", hidden = true)
	private String optTime;
	@ApiModelProperty(value = "修改0 还是 删除1", hidden = true)
	private String optMethod;
	@ApiModelProperty(value = "审计人", hidden = true)
	private String optUsername;
	@ApiModelProperty(value = "审计所属数据", hidden = true)
	private String auditId;
}

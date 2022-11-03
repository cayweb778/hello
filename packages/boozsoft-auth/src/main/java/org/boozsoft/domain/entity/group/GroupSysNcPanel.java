package org.boozsoft.domain.entity.group;


import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.annotation.Id;
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
@Table ("_app_group_sys_nc_panel" )
@ApiModel(value="NC首页面板设置表",description="NC首页面板设置表")
public class GroupSysNcPanel {

	@Id
	@CreatedBy
	@JsonSerialize(using = ToStringSerializer.class)
	private String id;

	@ApiModelProperty(value = "访问模块路径", position = 6)
	private String appPath;

	@ApiModelProperty(value = "显示名称", position = 1)
	private String panelName;

	@ApiModelProperty(value = "申请账套唯一码", position = 2)
	private String accId;

	@ApiModelProperty(value = "所属品牌", position = 3)
	private String panelBrand;

	@ApiModelProperty(value = "版本", position = 4)
	private String panelVersion;

	@ApiModelProperty(value = "描述", position = 5)
	private String panelDescrip;

	@ApiModelProperty(value = "序号", position = 6)
	private Long panelOrder;

	@ApiModelProperty(value = "状态(1.启用;0停用)", position = 7)
	private String panelFlag;


}

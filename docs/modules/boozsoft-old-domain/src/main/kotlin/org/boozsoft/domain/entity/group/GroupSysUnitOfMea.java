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
@Table ("_app_group_sys_unit_of_mea" )
@ApiModel(value="计量单位",description="_app_group_sys_unit_of_mea: table")
public class GroupSysUnitOfMea {

	@ApiModelProperty(value = "主键id", position = 0)
	@Id
	@CreatedBy
	@JsonSerialize(using = ToStringSerializer.class)
	private String id;

	@ApiModelProperty(value = "计量单位编码（不允许重复）", position = 1)
	private String unitCode;

	@ApiModelProperty(value = "计量单位名称（不允许重复", position = 2)
	private String unitName;

	@ApiModelProperty(value = "创建日期", position = 3)
	private String createDate;

	@ApiModelProperty(value = "停用人", position = 4)
	private String stopCode;

	@ApiModelProperty(value = "停用日期", position = 4)
	private String stopDate;

	@ApiModelProperty(value = "是否停用(1.启用;0停用)", position = 5)
	private String flag;

	@ApiModelProperty(value = "类型 1重量 2数量 3面积 4长度 5体积 6容积", position = 5)
	private String unitType;
}

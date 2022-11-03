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
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;

@Data
@Table ("sys_unit_of_mea_list" )
@ApiModel(value="多计量单位关联",description="sys_unit_of_mea_list: table")
public class SysUnitOfMeaList implements Serializable {

	@ApiModelProperty(value = "主键id", position = 0)
	@Id
	@CreatedBy
	@JsonSerialize(using = ToStringSerializer.class)
	private String id;

	@ApiModelProperty(value = "计量单位编码", position = 1)
	private String unitCode;

	@ApiModelProperty(value = "计量单位名称（不允许重复", position = 2)
	private String unitName;

	@ApiModelProperty(value = "计量编码", position = 1)
	private String manyCode;

	@ApiModelProperty(value = "主计量", position = 2)
	private String isMain;

	@ApiModelProperty(value = "换算率", position = 4)
	private String conversionRate;

	@ApiModelProperty(value = "换算说明", position = 5)
	private String conversionExplain;



}

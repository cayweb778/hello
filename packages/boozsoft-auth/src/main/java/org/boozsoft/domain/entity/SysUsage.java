package org.boozsoft.domain.entity;


import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.experimental.Accessors;
import lombok.NoArgsConstructor;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Table ("sys_usage" )
@ApiModel(value="sys_usage: table",description="sys_usage: table")
public class SysUsage {

	@ApiModelProperty(value = "主键id", position = 0)
	@Id
	private String id;

	@ApiModelProperty(value = "费率", position = 1)
	private String rate;

	@ApiModelProperty(value = "剩余次数", position = 2)
	private String remainingTimes;

	@ApiModelProperty(value = "服务名称", position = 3)
	private String serverName;

	@ApiModelProperty(value = "预警人", position = 4)
	private String warningManId;

	@ApiModelProperty(value = "是否启用(验真服务：1是、0否)", position = 4)
	private String flag;

}

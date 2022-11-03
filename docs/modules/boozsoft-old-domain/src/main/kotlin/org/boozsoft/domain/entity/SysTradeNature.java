package org.boozsoft.domain.entity;


import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table ("_app_group_sys_trade_nature" )
@ApiModel(value="行业性质表",description="行业性质表")
public class SysTradeNature {

	@ApiModelProperty(value = "主键id", position = 0)
	@Id
	private String id;

	private String name;


}

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
@Table ("_app_group_sys_check_api" )
@ApiModel(value="发票验真接口表",description="发票验真接口表")
public class SysCheckApi {

	@ApiModelProperty(value = "主键id", position = 0)
	@Id
	private String id;

	@ApiModelProperty(value = "key", position = 1)
	private String key;

	@ApiModelProperty(value = "secret", position = 2)
	private String secret;

	@ApiModelProperty(value = "url", position = 3)
	private String url;

	@ApiModelProperty(value = "接口服务商", position = 4)
	private String groupName;


}

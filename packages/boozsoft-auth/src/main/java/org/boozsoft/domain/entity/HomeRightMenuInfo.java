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

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Table ("_app_group_home_right" )
@ApiModel(value="主页检索信息表",description="主页检索信息表")
public class HomeRightMenuInfo {

	@Id
	@ApiModelProperty(value = "主键", position = 0)
	@CreatedBy
	@JsonSerialize(using = ToStringSerializer.class)
	private String id;

	@ApiModelProperty(value = "平台ID")
	private String platformId;

	@ApiModelProperty(value = "菜单ID")
	private String menuId;

	@ApiModelProperty(value = "菜单名称")
	private String menuName;

	@ApiModelProperty(value = "路由地址")
	private String routerPath;

	@ApiModelProperty(value = "查询用户类型 1 租户 2 默认")
	private  String queryUserType;

	@ApiModelProperty(value = "数据来源表")
	private String tableName;

	@ApiModelProperty(value = "code对应DB列名")
	private String codeCol;

	@ApiModelProperty(value = "name对应DB列名")
	private String nameCol;

	private String beiyong1;
	private String beiyong2;
	private String beiyong3;
}

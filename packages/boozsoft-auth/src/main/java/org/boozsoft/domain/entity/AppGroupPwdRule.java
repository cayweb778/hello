package org.boozsoft.domain.entity;


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
@Table ("_app_group_pwd_rule" )
@ApiModel(value="_app_group_pwd_rule: table",description="_app_group_pwd_rule: table")
public class AppGroupPwdRule {

	@Id	@CreatedBy	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty(value = "主键 (唯一吗)")
	private String id;

	@ApiModelProperty(value = "密码策略名称")
	private String pwdName;

	@ApiModelProperty(value = "密码长度")
	private Long pwdLength;

	@ApiModelProperty(value = "强制大写（0否，1是）")
	private String pwdUpperCase;

	@ApiModelProperty(value = "强制特殊符号（0否，1是）")
	private String pwdTe;

	@ApiModelProperty(value = "建立日期")
	private String pwdAddTime;

	@ApiModelProperty(value = "有效天数")
	private Long pwdValidity;

	@ApiModelProperty(value = "失效日期")
	private String pwdExpireTime;

	@ApiModelProperty(value = "强制修改（0否，1是）")
	private String pwdUpdate;

	@ApiModelProperty(value = "到期锁定（0否，1是）")
	private String pwdLock;

	@ApiModelProperty(value = "类型（1系统内置、2自定义）")
	private String pwdType;

	@ApiModelProperty(value = "错误锁定次数")
	private Long pwdErrorNumber;
	@ApiModelProperty(value = "错误恢复时间")
	private String pwdErrorTime;


}

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
@Table ("_app_group_sys_acc_auth" )
@ApiModel(value="账套权限表",description="账套权限表")
public class GroupSysAccAuth {

	@Id
	@CreatedBy
	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty(value = "主键", position = 0)
	private String id;

	@ApiModelProperty(value = "操作员编码", position = 1)
	private String userNum;

	@ApiModelProperty(value = "账套编码", position = 2)
	private String accId;

	@ApiModelProperty(value = "年度", position = 3)
	private String iyear;

	@ApiModelProperty(value = "科目范围是否全部1是", position = 4)
	private String ccodeAll;

	@ApiModelProperty(value = "凭证类别是否全部1是", position = 5)
	private String accvocherType;

	@ApiModelProperty(value = "账套主管 1 ", position = 5)
	private String supervisor;

	@ApiModelProperty(value = "默认登录账套 1 ", position = 5)
	private String defaultLogin;

}

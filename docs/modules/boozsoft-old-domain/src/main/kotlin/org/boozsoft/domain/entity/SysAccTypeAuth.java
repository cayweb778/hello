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
import org.springframework.data.relational.core.mapping.Table;

import java.util.Objects;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Table ("sys_acc_auth_type" )
@ApiModel(value="凭证类型权限表",description="凭证类型权限表")
public class SysAccTypeAuth {

	@ApiModelProperty(value = "涓婚敭id", position = 0)
	@Id	@CreatedBy	@JsonSerialize(using = ToStringSerializer.class)
	private String id;

	@ApiModelProperty(value = "账套编码")
	private String accId;

	@ApiModelProperty(value = "凭证类型")
	private String accvocherType;

	@ApiModelProperty(value = "年度")
	private String iyear;

	@ApiModelProperty(value = "操作员编码")
	private String userNum;

	private String tenantId;

	@ApiModelProperty(value = "权限类型1查看2修改3删除")
	private String authType;


	@Override
	public boolean equals(Object o) {
		if(Objects.isNull(o)) {
			return false;
		}
		if(accvocherType.equals(o)) {
			return true;
		}
		return false;
	}
}

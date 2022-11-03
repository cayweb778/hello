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
@Table ("sys_acc_auth_code" )
@ApiModel(value="科目数据权限表",description="科目数据权限表")
public class SysAccCodeAuth {

	@Id
	@CreatedBy
	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty(value = "主键", position = 0)
	private String id;

	@ApiModelProperty(value = "账套编码")
	private String accId;

	@ApiModelProperty(value = "档案编码")
	private String codeNum;

	@ApiModelProperty(value = "年度")
	private String iyear;

	@ApiModelProperty(value = "操作员编码")
	private String userNum;

	private String tenantId;

	@ApiModelProperty(value = "权限类型1查看2修改3删除")
	private String authType;

	@ApiModelProperty(value = "是否允许删除：1是")
	private String isDel;

	@ApiModelProperty(value = "是否允许修改：1是")
	private String isUp;

	@ApiModelProperty(value = "允许增加下级科目：1是")
	private String iadd;

	@ApiModelProperty(value = "允许修改科目属性：是")
	private String iup;

	@ApiModelProperty(value = "授权人")
	private String authName;

	@ApiModelProperty(value = "授权时间")
	private java.util.Date authTime;

	@Override
	public boolean equals(Object o) {
		if(Objects.isNull(o)) {
			return false;
		}
		if(codeNum.equals(o)) {
			return true;
		}
		return false;
	}
}

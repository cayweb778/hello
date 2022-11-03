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

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Table ("_app_group_sys_acc_auth" )
@ApiModel(value="账套权限表",description="账套权限表")
public class SysAccAuth {

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

	@ApiModelProperty(value = "客户分类是否全部 1 ", position = 5)
	private String cusClass;

	@ApiModelProperty(value = "客户档案是否全部 1 ", position = 5)
	private String cus;

	@ApiModelProperty(value = "供应商分类是否全部 1 ", position = 5)
	private String supClass;

	@ApiModelProperty(value = "供应商档案是否全部 1 ", position = 5)
	private String sup;

	@ApiModelProperty(value = "项目分类是否全部 1 ", position = 5)
	private String projectClass;

	@ApiModelProperty(value = "项目是否全部 1 ", position = 5)
	private String project;

	@ApiModelProperty(value = "项目大类是否全部 1 ", position = 5)
	private String projectType;

	@ApiModelProperty(value = "部门是否全部 1 ", position = 5)
	private String dept;

	@ApiModelProperty(value = "人员分类是否全部：1是 ", position = 5)
	private String psnClass;

	@ApiModelProperty(value = "人员是否全部：1是 ", position = 5)
	private String psnCode;

	@ApiModelProperty(value = "自定义档案是否全部1是 1 ", position = 5)
	private String defineId;

	@ApiModelProperty(value = "科目范围全部权限:1是", position = 5)
	private String ccodeAllAuth;

	@ApiModelProperty(value = "凭证类别全部权限:1是 ", position = 5)
	private String accvocherTypeAuth;

	@ApiModelProperty(value = "客户分类全部权限:1是 ", position = 5)
	private String cusClassAuth;

	@ApiModelProperty(value = "客户档案全部权限:1是", position = 5)
	private String cusAuth;

	@ApiModelProperty(value = "供应商分类全部权限:1是 ", position = 5)
	private String supClassAuth;

	@ApiModelProperty(value = "供应商全部权限:1是 ", position = 5)
	private String supAuth;

	@ApiModelProperty(value = "项目分类全部权限:1是 ", position = 5)
	private String projectClassAuth;

	@ApiModelProperty(value = "项目全部权限:1是", position = 5)
	private String projectAuth;

	@ApiModelProperty(value = " 项目大类全部权限:1是", position = 5)
	private String projectTypeAuth;

	@ApiModelProperty(value = "部门全部权限:1是 ", position = 5)
	private String detpAuth;

	@ApiModelProperty(value = " 人员分类全部权限：1是 ", position = 5)
	private String psnClassAuth;

	@ApiModelProperty(value = "人员全部权限：1是", position = 5)
	private String psnCodeAuth;

	@ApiModelProperty(value = "自定义档案全部权限1是", position = 5)
	private String defineIdAuth;


}

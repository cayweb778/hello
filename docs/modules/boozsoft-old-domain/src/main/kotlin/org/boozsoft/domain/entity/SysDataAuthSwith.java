package org.boozsoft.domain.entity;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table ("sys_data_auth_switch" )
@ApiModel(value="数据权限控制表",description="数据权限控制表")
public class SysDataAuthSwith {

	@ApiModelProperty(value = "主键id")
	@Id
	@CreatedBy
	@JsonSerialize(using = ToStringSerializer.class)
	private String id;

	@ApiModelProperty(value = "公司租户唯一编码")
	private String recordNum;

	@ApiModelProperty(value = "年度" )
	private String iyear;

	@ApiModelProperty(value = "授权人" )
	private String userId;

	@ApiModelProperty(value = "授权时间")
	private String createTime;

	@ApiModelProperty(value = "会计科目（1：控制，其他不控制）")
	private String isCcode;

	@ApiModelProperty(value = "凭证类别（1：控制，其他不控制）")
	private String isVoucherType;

	@ApiModelProperty(value = "人员分类（1：控制，其他不控制）")
	private String isPsnClass;
	
	@ApiModelProperty(value = "人员档案（1：控制，其他不控制）")
	private String isPsn;

	@ApiModelProperty(value = "客户分类（1：控制，其他不控制）")
	private String isCusClass;

	@ApiModelProperty(value = "客户档案（1：控制，其他不控制）")
	private String isCus;

	@ApiModelProperty(value = "部门档案（1：控制，其他不控制）")
	private String isDept;

	@ApiModelProperty(value = "供应商分类（1：控制，其他不控制）")
	private String isSupClass;

	@ApiModelProperty(value = "供应商档案（1：控制，其他不控制）")
	private String isSup;

	@ApiModelProperty(value = "项目大类（1：控制，其他不控制）")
	private String isProjectType;

	@ApiModelProperty(value = "项目分类（1：控制，其他不控制）")
	private String isProjectClass;

	@ApiModelProperty(value = "项目目录（1：控制，其他不控制）")
	private String isProject;

	@ApiModelProperty(value = "自定义档案（1：控制，其他不控制）")
	private String isDefine;

	@ApiModelProperty(value = "凭证制单人控制（1：控制，其他不控制）")
	private String isCcashier;

	@ApiModelProperty(value = "固定资产类别控制（1：控制，其他不控制）")
	private String isFaClass;

	@ApiModelProperty(value = "固定资产卡片控制（1：控制 ，其他不控制）")
	private String isFaCard;

	@ApiModelProperty(value = "")
	private String tenantId;

}

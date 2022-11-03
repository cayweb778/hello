package org.boozsoft.domain.entity.account;

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
@Table ( "sys_bank_account" )
@ApiModel(value="账户档案表",description="账户档案表")
public class SysBankAccount {

	@Id
	@ApiModelProperty(value = "ID", hidden = true)
	@CreatedBy
	@JsonSerialize(using = ToStringSerializer.class)
	private String id;
	@Transient
	@ApiModelProperty(value = "公司唯一码",hidden = true)
	private String tenantId;
	@ApiModelProperty(value = "账户唯一码",hidden = true)
	private String ccode;
	@ApiModelProperty(value = "账户名称",hidden = true)
	private String cname;
	@ApiModelProperty(value = "账户类型",hidden = true)
	private String accStyle;
	@ApiModelProperty(value = "开户银行",hidden = true)
	private String accBank;
	@ApiModelProperty(value = "银行账号",hidden = true)
	private String accNumber;
	@ApiModelProperty(value = "状态",hidden = true)
	private String status;
	@ApiModelProperty(value = "备注",hidden = true)
	private String memo;
	@ApiModelProperty(value = "",hidden = true)
	private String cfree1;
	@ApiModelProperty(value = "",hidden = true)
	private String cfree2;
	@ApiModelProperty(value = "",hidden = true)
	private String cfree3;
	@ApiModelProperty(value = "",hidden = true)
	private String cfree4;
	@ApiModelProperty(value = "",hidden = true)
	private String cfree5;

}

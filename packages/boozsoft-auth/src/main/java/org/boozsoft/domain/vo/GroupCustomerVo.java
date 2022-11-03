package org.boozsoft.domain.vo;


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


/**
 * 供应商 - 客户  用一个Vo
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value="系统客户信息表",description="系统客户信息表")
public class GroupCustomerVo {

	
	private String id;

	@ApiModelProperty(value = "1是假删除", position = 0)
	private String isDel;
	private String delName;
	private String delDate;

	@ApiModelProperty(value = "唯一编码", position = 1)
	private String uniqueCode;

	@ApiModelProperty(value = "客户编码（不允许重复）", position = 2)
	private String custCode;

	@ApiModelProperty(value = "客户全称（不允许重复）", position = 3)
	private String custName;

	@ApiModelProperty(value = "客户简称（不允许重复）", position = 4)
	private String custAbbname;

	@ApiModelProperty(value = "客户分类唯一码", position = 5)
	private String uniqueCustclass;
	private String uniqueCustclassName;

	@ApiModelProperty(value = "税号", position = 6)
	private String custSregcode;

	@ApiModelProperty(value = "开户银行", position = 7)
	private String custBank;

	@ApiModelProperty(value = "银行账号", position = 8)
	private String custAccount;

	@ApiModelProperty(value = "创建日期", position = 9)
	private String custDevdate;

	@ApiModelProperty(value = "母公司", position = 10)
	private String uniqueCodeCcus;

	@ApiModelProperty(value = "对应供应商唯一码", position = 11)
	private String uniqueCodeSupplier;

	@ApiModelProperty(value = "行业性质名称", position = 12)
	private String industryclassName;

	@ApiModelProperty(value = "联系人", position = 13)
	private String contacts;

	@ApiModelProperty(value = "电话", position = 14)
	private String telephone;

	@ApiModelProperty(value = "地址一", position = 15)
	private String address1;

	@ApiModelProperty(value = "地址二", position = 16)
	private String address2;

	@ApiModelProperty(value = "手机", position = 17)
	private String cellPhoneNum;

	@ApiModelProperty(value = "Email", position = 18)
	private String email;

	@ApiModelProperty(value = "所属国家名称", position = 19)
	private String countryName;

	@ApiModelProperty(value = "所属网址", position = 20)
	private String website;

	@ApiModelProperty(value = "备注", position = 21)
	private String remarks;

	@ApiModelProperty(value = "是否停用(1.启用;0停用)", position = 22)
	private String flag;
	@ApiModelProperty(value = "邮政编码", position = 22)
	private String beiyong1;	// 邮政编码

	private String beiyong2;

	private String beiyong3;

	private String beiyong4;

	private String beiyong5;

	private String beiyong6;

	private String beiyong7;

	private String beiyong8;

	private String beiyong9;

	private String beiyong10;

	@ApiModelProperty(value = "0内部客户/1外部客户", position = 33)
	private String manageType;

	@ApiModelProperty(value = "银行开户地", position = 34)
	private String bankArea;

	@ApiModelProperty(value = "银行行号", position = 35)
	private String bankCode;

	@ApiModelProperty(value = "公司地址", position = 36)
	private String province;

	@ApiModelProperty(value = "市", position = 37)
	private String city;

	@ApiModelProperty(value = "区", position = 38)
	private String area;
	private String zone;

	@ApiModelProperty(value = "生效状态（1是；0否；-1驳回）", position = 39)
	private String successState;

	@ApiModelProperty(value = "申请人", position = 41)
	private String applyUser;

	@ApiModelProperty(value = "申请时间", position = 42)
	private String applyDate;

	@ApiModelProperty(value = "审批人", position = 43)
	private String approveUser;

	@ApiModelProperty(value = "审批时间", position = 44)
	private String approveDate;

	@ApiModelProperty(value = "组织ID", position = 44)
	private String originUnique;
	@ApiModelProperty(value = "租户ID", position = 44)
	private String tenantId;
	@ApiModelProperty(value = "驳回原因", position = 44)
	private String reason;
	@ApiModelProperty(value = "申请类型(1,组织;2,账套)", position = 44)
	private String ctype;

	private String assignFlag; // 集团客户分配表引入状态,0未引入|1已引入
	private String isControl; // 是否管控:1是0否
	
	private String isKeyword; // 是否允许修改关键字:1是0否
	
	private String isOther; // 是否允许修改其他信息:1是0否
	
	private String isAuto; // 新增是否自动分配:1是0否
	
	private String orgName;
	
	private String orgUnique;
}

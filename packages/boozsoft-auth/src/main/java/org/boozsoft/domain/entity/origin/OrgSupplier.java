package org.boozsoft.domain.entity.origin;

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

import java.io.Serializable;

/**
 * @Description  
 * @Author  myh
 * @Date 2022-02-23 
 */

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Table ( "_app_origin_supplier" )
@ApiModel(value="组织供应商信息表",description="组织供应商信息表")
public class OrgSupplier implements Serializable {


	@ApiModelProperty(value = "主键")
	@Id
	@CreatedBy
	@JsonSerialize(using = ToStringSerializer.class)
	private String id;

	@ApiModelProperty(value = "唯一编码")
	private String uniqueCode;

	@ApiModelProperty(value = "供应商编码（不允许重复）")
	private String custCode;

	@ApiModelProperty(value = "供应商全称（不允许重复）")
	private String custName;

	@ApiModelProperty(value = "供应商简称（不允许重复）")
	private String custAbbname;

	@ApiModelProperty(value = "供应商分类唯一码")
	private String uniqueCustclass;

	@ApiModelProperty(value = "税号")
	private String custSregcode;

	@ApiModelProperty(value = "开户银行")
	private String custBank;

	@ApiModelProperty(value = "银行账号")
	private String custAccount;

	@ApiModelProperty(value = "创建日期")
	private String custDevdate;

	@ApiModelProperty(value = "总公司供应商唯一码")
	private String uniqueCodeCcus;

	@ApiModelProperty(value = "对应供应商唯一码")
	private String uniqueCodeSupplier;

	@ApiModelProperty(value = "行业性质名称")
	private String industryclassName;

	@ApiModelProperty(value = "联系人")
	private String contacts;

	@ApiModelProperty(value = "电话")
	private String telephone;

	@ApiModelProperty(value = "地址一")
	private String address1;

	@ApiModelProperty(value = "地址二")
	private String address2;

	@ApiModelProperty(value = "手机")
	private String cellPhoneNum;

	@ApiModelProperty(value = "Email")
	private String email;

	@ApiModelProperty(value = "所属国家名称")
	private String countryName;

	@ApiModelProperty(value = "所属网址")
	private String website;

	@ApiModelProperty(value = "备注")
	private String remarks;

	@ApiModelProperty(value = "是否停用(1.启用;0停用)")
	private String flag;

	@ApiModelProperty(value = "邮政编码")
	private String beiyong1;

	private String beiyong2;

	private String beiyong3;

	private String beiyong4;

	private String beiyong5;

	private String beiyong6;

	private String beiyong7;

	private String beiyong8;

	private String beiyong9;

	private String beiyong10;

	@ApiModelProperty(value = "1内部供应商/2外部供应商")
	private String manageType;

	@ApiModelProperty(value = "银行开户地")
	private String bankArea;

	@ApiModelProperty(value = "银行行号")
	private String bankCode;

	@ApiModelProperty(value = "省")
	private String province;

	@ApiModelProperty(value = "市")
	private String city;

	@ApiModelProperty(value = "区")
	private String area;

	@ApiModelProperty(value = "所属组织ID")
	private String orgUnique;

	@Transient
	@ApiModelProperty(value = "所属组织ID")
	private String uniqueCustclassName;
}

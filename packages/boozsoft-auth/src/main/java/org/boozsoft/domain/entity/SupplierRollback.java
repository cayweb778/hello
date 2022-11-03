package org.boozsoft.domain.entity;


import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.experimental.Accessors;
import lombok.NoArgsConstructor;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Table ("supplier_rollback" )
@ApiModel(value="supplier_rollback: table",description="supplier_rollback: table")
public class SupplierRollback {

	@Id
	@ApiModelProperty(value = "主键", position = 0)
	private String id;

	@ApiModelProperty(value = "唯一编码", position = 1)
	private String uniqueCode;

	@ApiModelProperty(value = "客户编码（不允许重复）", position = 2)
	private String supCode;

	@ApiModelProperty(value = "客户全称（不允许重复）", position = 3)
	private String supName;

	@ApiModelProperty(value = "客户简称（不允许重复）", position = 4)
	private String supAbbname;

	@ApiModelProperty(value = "供应商分类唯一码", position = 5)
	private String uniqueSupclass;

	@ApiModelProperty(value = "税号", position = 6)
	private String supSregcode;

	@ApiModelProperty(value = "开户银行", position = 7)
	private String supBank;

	@ApiModelProperty(value = "银行账号", position = 8)
	private String supAccount;

	@ApiModelProperty(value = "创建日期", position = 9)
	private String supDevdate;

	@ApiModelProperty(value = "总公司供应商唯一码", position = 10)
	private String uniqueCodeCcus;

	@ApiModelProperty(value = "对应客户唯一码", position = 11)
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

	@ApiModelProperty(value = "1内部客户/2外部客户", position = 33)
	private String manageType;

	@ApiModelProperty(value = "银行开户地", position = 34)
	private String bankArea;

	@ApiModelProperty(value = "银行行号", position = 35)
	private String bankCode;

	@ApiModelProperty(value = "省", position = 36)
	private String province;

	@ApiModelProperty(value = "市", position = 37)
	private String city;

	@ApiModelProperty(value = "区", position = 38)
	private String area;

	@ApiModelProperty(value = "生效状态1是、0", position = 39)
	private String successState;

	@ApiModelProperty(value = "申请账套唯一码", position = 40)
	private String applyDatabaseUniqueCode;

	@ApiModelProperty(value = "申请人", position = 41)
	private String applyUser;

	@ApiModelProperty(value = "申请时间", position = 42)
	private String applyDate;

	@ApiModelProperty(value = "审批人", position = 43)
	private String approveUser;

	@ApiModelProperty(value = "审批时间", position = 44)
	private String approveDate;

	@ApiModelProperty(value = "变动日期", position = 45)
	private String biandongDate;

	@ApiModelProperty(value = "变动方式(1.修改;2.删除;3.停用)", position = 46)
	private String biandongMethod;

	@ApiModelProperty(value = "操作员姓名", position = 47)
	private String biandongName;

	@ApiModelProperty(value = "操作员唯一编码", position = 48)
	private String biandongUniqueCode;


}

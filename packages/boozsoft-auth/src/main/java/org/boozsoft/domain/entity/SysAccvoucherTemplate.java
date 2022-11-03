package org.boozsoft.domain.entity;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.experimental.Accessors;
import lombok.NoArgsConstructor;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Table ("sys_accvoucher_template" )
@ApiModel(value="凭证导入模板表",description="凭证导入模板表")
public class SysAccvoucherTemplate {

	@Id
	@ApiModelProperty(value = "ID", hidden = true)
	@CreatedBy
	@JsonSerialize(using = ToStringSerializer.class)
	private String id;

	@ApiModelProperty(value = "模板名称", position = 1)
	private String templateName;

	@ApiModelProperty(value = "凭证日期", position = 1)
	private String dbillDate;

	@ApiModelProperty(value = "凭证编号", position = 2)
	private String inoId;

	@ApiModelProperty(value = "凭证行号", position = 2)
	private String rowId;

	@ApiModelProperty(value = "附单据数", position = 3)
	private String idoc;

	@ApiModelProperty(value = "凭证摘要", position = 4)
	private String cdigest;

	@ApiModelProperty(value = "科目编码", position = 5)
	private String ccode;

	@ApiModelProperty(value = "科目名称", position = 6)
	private String ccodeName;

	@ApiModelProperty(value = "借方金额", position = 7)
	private String md;

	@ApiModelProperty(value = "贷方金额", position = 8)
	private String mc;

	@ApiModelProperty(value = "部门编码", position = 9)
	private String cdeptId;

	@ApiModelProperty(value = "个人编码", position = 10)
	private String cpersonId;

	@ApiModelProperty(value = "客户编码", position = 11)
	private String ccusId;

	@ApiModelProperty(value = "供应商编码", position = 12)
	private String csupId;

	@ApiModelProperty(value = "项目大类编码", position = 13)
	private String projectClassId;

	@ApiModelProperty(value = "项目编码", position = 14)
	private String projectId;

	@ApiModelProperty(value = "制单人", position = 15)
	private String cbill;

	@ApiModelProperty(value = "审核人", position = 16)
	private String ccheck;

	@ApiModelProperty(value = "记账人", position = 17)
	private String cbook;

	@ApiModelProperty(value = "出纳", position = 18)
	private String cashier;

	@ApiModelProperty(value = "凭证类型", position = 19)
	private String csign;

	@ApiModelProperty(value = "计量单位", position = 20)
	private String unitMeasurement;

	@ApiModelProperty(value = "借方数量", position = 21)
	private String ndS;

	@ApiModelProperty(value = "贷方数量", position = 22)
	private String ncS;

	@ApiModelProperty(value = "外币币种", position = 23)
	private String foreignCurrency;

	@ApiModelProperty(value = "外币金额", position = 24)
	private String foreignAmount;

	@ApiModelProperty(value = "模板类型: 0 简约, 1标准", position = 25)
	private String templateType;

	@ApiModelProperty(value = "制单日期", position = 26)
	private String formDate;

	@ApiModelProperty(value = "审核日期", position = 27)
	private String checkDate;

	@ApiModelProperty(value = "单价", position = 28)
	private String unitPrice;

	@ApiModelProperty(value = "年度", position = 29)
	private String year;

	@ApiModelProperty(value = "结算方式编码", position = 30)
	private String settlementMethod;

}

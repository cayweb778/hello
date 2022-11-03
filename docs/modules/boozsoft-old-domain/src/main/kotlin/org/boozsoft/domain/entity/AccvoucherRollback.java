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
@Table ("accvoucher_rollback" )
@ApiModel(value="会计凭证回滚表: table",description="会计凭证回滚表: table")
public class AccvoucherRollback {

	@ApiModelProperty(value = "主键id", position = 0)
	@Id
	private String id;

	@ApiModelProperty(value = "唯一编码", position = 1)
	private String uniqueCode;

	@ApiModelProperty(value = "凭证类型（记、转）", position = 2)
	private String csign;

	@ApiModelProperty(value = "会计年度", position = 3)
	private String iyear;

	@ApiModelProperty(value = "会计月份", position = 4)
	private String imonth;

	@ApiModelProperty(value = "会计期间年月   (会计年度+会计月份)", position = 5)
	private String iyperiod;

	@ApiModelProperty(value = "凭证日期", position = 6)
	private String dbillDate;

	@ApiModelProperty(value = "凭证号", position = 7)
	private String inoId;

	@ApiModelProperty(value = "分录号", position = 8)
	private String inid;

	@ApiModelProperty(value = "附单据数", position = 9)
	private String idoc;

	@ApiModelProperty(value = "凭证摘要", position = 10)
	private String cdigest;

	@ApiModelProperty(value = "科目编码", position = 11)
	private String ccode;

	@ApiModelProperty(value = "科目名称", position = 12)
	private String ccodeName;

	@ApiModelProperty(value = "借方金额", position = 13)
	private String md;

	@ApiModelProperty(value = "贷方金额", position = 14)
	private String mc;

	@ApiModelProperty(value = "借方外币汇率", position = 15)
	private String mdF;

	@ApiModelProperty(value = "贷方外币汇率", position = 16)
	private String mcF;

	@ApiModelProperty(value = "借方数量", position = 17)
	private String ndS;

	@ApiModelProperty(value = "贷方数量", position = 18)
	private String ncS;

	@ApiModelProperty(value = "外币金额", position = 19)
	private String nfrat;

	@ApiModelProperty(value = "制单人姓名", position = 20)
	private String cbill;

	@ApiModelProperty(value = "出纳签字人姓名", position = 21)
	private String ccashier;

	@ApiModelProperty(value = "出纳签字日期", position = 22)
	private String ccashierDate;

	@ApiModelProperty(value = "审核人姓名", position = 23)
	private String ccheck;

	@ApiModelProperty(value = "审核日期", position = 24)
	private String ccheckDate;

	@ApiModelProperty(value = "主管签字人姓名", position = 25)
	private String cdirector;

	@ApiModelProperty(value = "记账人姓名", position = 26)
	private String cbook;

	@ApiModelProperty(value = "是否记账", position = 27)
	private String ibook;

	@ApiModelProperty(value = "记账日期", position = 28)
	private String ibookDate;

	@ApiModelProperty(value = "结算方式编码", position = 29)
	private String pjCsettle;

	@ApiModelProperty(value = "结算票据号", position = 30)
	private String pjId;

	@ApiModelProperty(value = "票据日期", position = 31)
	private String pjDate;

	@ApiModelProperty(value = "部门唯一码", position = 32)
	private String cdeptId;

	@ApiModelProperty(value = "个人唯一码", position = 33)
	private String cpersonId;

	@ApiModelProperty(value = "客户唯一码", position = 34)
	private String ccusId;

	@ApiModelProperty(value = "供应商唯一码", position = 35)
	private String csupId;

	@ApiModelProperty(value = "项目大类唯一码", position = 36)
	private String projectClassId;

	@ApiModelProperty(value = "项目唯一码", position = 37)
	private String projectId;

	@ApiModelProperty(value = "凭证状态(2暂存,1作废，0正常）", position = 38)
	private String ifrag;

	@ApiModelProperty(value = "现金流量项目编码", position = 39)
	private String cashProject;

	@ApiModelProperty(value = "是否协同凭证", position = 40)
	private String icoord;

	@ApiModelProperty(value = "协同账套名称", position = 41)
	private String coordAccname;

	@ApiModelProperty(value = "协同凭证日期", position = 42)
	private String coordDate;

	@ApiModelProperty(value = "协助凭证唯一码", position = 43)
	private String coordId;

	@ApiModelProperty(value = "辅助项1（辅助核算项编码）", position = 44)
	private String cdfine1;

	private String cdfine2;

	private String cdfine3;

	private String cdfine4;

	private String cdfine5;

	private String cdfine6;

	private String cdfine7;

	private String cdfine8;

	private String cdfine9;

	private String cdfine10;

	private String cdfine11;

	private String cdfine12;

	private String cdfine13;

	private String cdfine14;

	private String cdfine15;

	private String cdfine16;

	private String cdfine17;

	private String cdfine18;

	private String cdfine19;

	private String cdfine20;

	private String cdfine21;

	private String cdfine22;

	private String cdfine23;

	private String cdfine24;

	private String cdfine25;

	private String cdfine26;

	private String cdfine27;

	private String cdfine28;

	private String cdfine29;

	private String cdfine30;

	@ApiModelProperty(value = "会计期间编码（1~12）", position = 74)
	private String iperiod;

	@ApiModelProperty(value = "单价", position = 75)
	private String cunitPrice;

	@ApiModelProperty(value = "计量单位", position = 76)
	private String unitMeasurement;

	@ApiModelProperty(value = "外币币种", position = 77)
	private String foreignCurrency;

	@ApiModelProperty(value = "变动日期", position = 78)
	private String biandongDate;

	@ApiModelProperty(value = "变动方式(1.修改;2.删除;3.停用)", position = 79)
	private String biandongMethod;

	@ApiModelProperty(value = "操作员姓名", position = 80)
	private String biandongName;

	@ApiModelProperty(value = "操作员唯一编码", position = 81)
	private String biandongUniqueCode;

	@ApiModelProperty(value = "变动前id", position = 82)
	private String biandongId;


}

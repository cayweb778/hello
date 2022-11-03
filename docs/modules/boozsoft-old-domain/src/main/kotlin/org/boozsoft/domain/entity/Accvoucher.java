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
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Table ("accvoucher" )
@ApiModel(value="会计凭证表",description="会计凭证表")
public class Accvoucher{

	@ApiModelProperty(value = "主键id", position = 0)
	@Id
	@CreatedBy
	@JsonSerialize(using = ToStringSerializer.class)
	private String id;

	@ApiModelProperty(value = "唯一编码", position = 1)
	private String uniqueCode;

	@ApiModelProperty(value = "分录唯一码", position = 1)
	private String vouchUnCode;

	@ApiModelProperty(value = "凭证类型（记、转）", position = 2)
	private String csign;

	@ApiModelProperty(value = "会计年度", position = 3)
	private String iyear;

	@ApiModelProperty(value = "会计月份", position = 4)
	private String imonth;

	@ApiModelProperty(value = "会计期间年月   (会计年度+会计月份)", position = 5)
	private String iyperiod;

	@ApiModelProperty(value = "会计期间编码（1~12）", position = 5)
	private String iperiod;

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

	@ApiModelProperty(value = "贷方外币汇率（停用）", position = 16)
	private String mcF;

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

	@ApiModelProperty(value = "是否记账 1已记账 ", position = 27)
	private String ibook;

	@ApiModelProperty(value = "记账日期", position = 28)
	private String ibookDate;

	@ApiModelProperty(value = "结算方式编码", position = 29)
	private String pjCsettle;

	@ApiModelProperty(value = "结算票据号", position = 30)
	private String pjId;

	@ApiModelProperty(value = "结算票据日期", position = 31)
	private String pjDate;

	@ApiModelProperty(value = "结算单位全称", position = 31)
	private String pjUnitName;

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

	@ApiModelProperty(value = "凭证状态(3标错,2暂存,1作废，0正常）", position = 38)
	private String ifrag;

	@ApiModelProperty(value = "1已分配0未分配", position = 39)
	private String cashProject;

	@ApiModelProperty(value = "是否协同凭证", position = 40)
	private String icoord;

	@ApiModelProperty(value = "协同账套名称", position = 41)
	private String coordAccname;

	@ApiModelProperty(value = "协同凭证日期", position = 42)
	private String coordDate;

	@ApiModelProperty(value = "协助凭证唯一码", position = 43)
	private String coordId;

	/*导入补充字段*/
	@ApiModelProperty(value = "计量单位", position = 43)
	private String unitMeasurement;
	@ApiModelProperty(value = "单价", position = 43)
	private String cunitPrice;
	@ApiModelProperty(value = "借方数量", position = 17)
	private String ndS;
	@ApiModelProperty(value = "贷方数量", position = 18)
	private String ncS;

	@ApiModelProperty(value = "本币币种代码", position = 43)
	private String standardCurrency;
	@ApiModelProperty(value = "外币币种代码", position = 43)
	private String foreignCurrency;


	@ApiModelProperty(value = "外币汇率", position = 15)
	private String mdF;
	@ApiModelProperty(value = "外币金额 针对数据库字段被篡改 补充获取对应数据", position = 19)
	private String nfrat;
	@ApiModelProperty(value = "原币借方金额", position = 19)
	private String nfratMd;
	@ApiModelProperty(value = "原币贷方金额", position = 19)
	private String nfratMc;
	//银行对账补充字段
	@ApiModelProperty(value = "对账标识号", hidden = true)
	private String statementNum;
	@ApiModelProperty(value = "累计借方", hidden = true)
	private String ljMd;
	@ApiModelProperty(value = "累计贷方", hidden = true)
	private String ljMc;

	@ApiModelProperty(value = "累计外币借方", hidden = true)
	private String ljWbMd;
	@ApiModelProperty(value = "累计外币贷方", hidden = true)
	private String ljWbMc;
	@ApiModelProperty(value = "累计数量借方", hidden = true)
	private String ljSlMd;
	@ApiModelProperty(value = "累计数量贷方", hidden = true)
	private String ljSlMc;
	/*@ApiModelProperty(value = "累计单价", hidden = true)
	private String ljCunitPrice;
	@ApiModelProperty(value = "累计汇率", hidden = true)
	private String ljMdF;*/

	@ApiModelProperty(value = "对方科目", hidden = true)
	private String oppositeCode;
	@ApiModelProperty(value = "作废人姓名", hidden = true)
	private String icancelName;
	@ApiModelProperty(value = "作废时间", hidden = true)
	private String icancelTime;
	@ApiModelProperty(value = "标错人姓名", hidden = true)
	private String iwrongName;
	@ApiModelProperty(value = "标错时间", hidden = true)
	private String iwrongTime;
	@ApiModelProperty(value = "核销状态 1已核销 2未核销完", hidden = true)
	private String hxStatue;


	@ApiModelProperty(value = "乐观锁", hidden = true)
	private Integer revision;

	@Transient
	@ApiModelProperty(value = "租户ID", hidden = true)
	private String tenantId;
	/*导入补充字段*/
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

	@Transient
	private String remainMoney;
}

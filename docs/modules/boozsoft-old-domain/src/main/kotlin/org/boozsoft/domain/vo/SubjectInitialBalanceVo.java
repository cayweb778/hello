package org.boozsoft.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class SubjectInitialBalanceVo {

	private String fuzhu;

	private String bitem;		//是否项目核算
	private String bsup;		//是否供应商核算
	private String bcus;		//是否客户核算
	private String bperson;		//是否个人核算
	private String bdept;		//是否部门核算

	private String bnum;		//是否数量核算
	private String currency;    //是否外币核算

	private String ccode;		//科目编码
	private String ccodeName;	//科目名称
	private String iyear;		//年度
	private String bprogerty;
	private String bend;		//是否末级
	private String iyperiod;	//会计期间
	private String iperiod;		//月份
	private BigDecimal md;		//借方
	private BigDecimal mc;		//贷方
	private BigDecimal yuemd;	//借方余额
	private BigDecimal yuemc;	//贷方余额
	private String superiorCcode;
	private String igrade;
	private String menterage;
	private String currencyType;
	private String menterageORcurrencyType;
	private String accvouid; // 凭证表ID
	private String cclass; // 科目类型
	private String flag;

	/********************* 辅助期初用到 **********************/
	private String uniqueCode;//凭证唯一码
	private String vouchUnCode;//分录唯一码
	private String dbillDate;//凭证日期
	private String inoId;//凭证号
	private String cdigest;//凭证号

	/********************* 辅助查询用到 **********************/
	@ApiModelProperty(value = "部门唯一码")
	private String cdeptId;
	private String cdeptName;
	@ApiModelProperty(value = "个人唯一码")
	private String cpersonId;
	private String cpersonName;
	@ApiModelProperty(value = "客户唯一码")
	private String ccusId;
	private String ccusName;
	@ApiModelProperty(value = "供应商唯一码")
	private String csupId;
	private String csupName;
	@ApiModelProperty(value = "项目唯一码")
	private String projectId;
	private String projectName;
	@ApiModelProperty(value = "项目大类唯一码")
	private String projectClassId;

	private BigDecimal ndS;// 借方数量
	private BigDecimal ncS;// 贷方数量
	private BigDecimal ncnum;//数量
	private BigDecimal nfratMd;//外币借方金额
	private BigDecimal nfratMc;//外币贷方金额
	private BigDecimal nfrat;//外币金额

	@ApiModelProperty(value = "累计借方", hidden = true)
	private BigDecimal ljMd;
	@ApiModelProperty(value = "累计贷方", hidden = true)
	private BigDecimal ljMc;

	@ApiModelProperty(value = "累计外币借方", hidden = true)
	private BigDecimal ljWbMd;
	@ApiModelProperty(value = "累计外币贷方", hidden = true)
	private BigDecimal ljWbMc;
	@ApiModelProperty(value = "累计数量借方", hidden = true)
	private BigDecimal ljSlMd;
	@ApiModelProperty(value = "累计数量贷方", hidden = true)
	private BigDecimal ljSlMc;
	private BigDecimal yearNum;//累计数量
	private BigDecimal yearNfrat;//累计外币金额
	@ApiModelProperty(value = "期初余额借方", hidden = true)
	private BigDecimal yearMd;
	@ApiModelProperty(value = "期初余额贷方", hidden = true)
	private BigDecimal yearMc;

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

	private String cdfine1Name;
	private String cdfine2Name;
	private String cdfine3Name;
	private String cdfine4Name;
	private String cdfine5Name;
	private String cdfine6Name;
	private String cdfine7Name;
	private String cdfine8Name;
	private String cdfine9Name;
	private String cdfine10Name;
	private String cdfine11Name;
	private String cdfine12Name;
	private String cdfine13Name;
	private String cdfine14Name;
	private String cdfine15Name;
	private String cdfine16Name;
	private String cdfine17Name;
	private String cdfine18Name;
	private String cdfine19Name;
	private String cdfine20Name;
	private String cdfine21Name;
	private String cdfine22Name;
	private String cdfine23Name;
	private String cdfine24Name;
	private String cdfine25Name;
	private String cdfine26Name;
	private String cdfine27Name;
	private String cdfine28Name;
	private String cdfine29Name;
	private String cdfine30Name;

	private String cdfine1Id;
	private String cdfine2Id;
	private String cdfine3Id;
	private String cdfine4Id;
	private String cdfine5Id;
	private String cdfine6Id;
	private String cdfine7Id;
	private String cdfine8Id;
	private String cdfine9Id;
	private String cdfine10Id;
	private String cdfine11Id;
	private String cdfine12Id;
	private String cdfine13Id;
	private String cdfine14Id;
	private String cdfine15Id;
	private String cdfine16Id;
	private String cdfine17Id;
	private String cdfine18Id;
	private String cdfine19Id;
	private String cdfine20Id;
	private String cdfine21Id;
	private String cdfine22Id;
	private String cdfine23Id;
	private String cdfine24Id;
	private String cdfine25Id;
	private String cdfine26Id;
	private String cdfine27Id;
	private String cdfine28Id;
	private String cdfine29Id;
	private String cdfine30Id;
}

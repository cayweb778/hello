package org.boozsoft.domain.entity.fa;


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

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Table ("fa_accvoucher" )
@ApiModel(value="资产凭证表",description="资产凭证表")
public class FaAccvoucher {

	@ApiModelProperty(value = "主键id", position = 0)
	@Id
	@CreatedBy
	@JsonSerialize(using = ToStringSerializer.class)
	private String id;

	@ApiModelProperty(value = "总账唯一编码", position = 1)
	private String uniqueCode;

	@ApiModelProperty(value = "资产唯一编码", position = 1)
	private String manageCode;

	@ApiModelProperty(value = "会计年度", position = 3)
	private String iyear;

	@ApiModelProperty(value = "会计月份", position = 4)
	private String imonth;

	@ApiModelProperty(value = "会计期间年月   (会计年度+会计月份)", position = 5)
	private String iyperiod;

	@ApiModelProperty(value = "会计期间编码（1~12）", position = 5)
	private String iperiod;

	@ApiModelProperty(value = "制单日期", position = 6)
	private String dbillDate;
	@ApiModelProperty(value = "制单人姓名", position = 20)
	private String cbill;

	@ApiModelProperty(value = "凭证号", position = 7)
	private String inoId;

	@ApiModelProperty(value = "分录号", position = 8)
	private String inid;

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

	@ApiModelProperty(value = "项目大类唯一码", position = 36)
	private String projectClassId;

	@ApiModelProperty(value = "项目唯一码", position = 37)
	private String projectId;

	@ApiModelProperty(value = "本币币种代码", position = 43)
	private String standardCurrency;
	@ApiModelProperty(value = "外币币种代码", position = 43)
	private String foreignCurrency;

	@ApiModelProperty(value = "外币汇率", position = 15)
	private String mdF;

	@ApiModelProperty(value = "原币借方金额", position = 19)
	private String nfratMd;
	@ApiModelProperty(value = "原币贷方金额", position = 19)
	private String nfratMc;

	@ApiModelProperty(value = "外部凭证标识符（系统定义：GDZC固定资产）", hidden = true)
	private String externalMark;

	@ApiModelProperty(value = "对方科目", hidden = true)
	private String oppositeCode;

	@Transient
	@ApiModelProperty(value = "租户ID", hidden = true)
	private String tenantId;

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

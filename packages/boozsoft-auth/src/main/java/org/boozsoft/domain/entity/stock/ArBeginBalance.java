package org.boozsoft.domain.entity.stock;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Table ( "ar_begin_balance" )
@ApiModel(value="应收款期初余额主表",description="应收款期初余额主表")
public class ArBeginBalance {

	@Id
	@ApiModelProperty(value = "ID", hidden = true)
	@CreatedBy
	@JsonSerialize(using = ToStringSerializer.class)
	private String id;
	@Transient
	@ApiModelProperty(value = "公司唯一码",hidden = true)
	private String tenantId;
	@ApiModelProperty(value = "年度标志",hidden = true)
	private String iyear;
	@ApiModelProperty(value = "行次唯一码",hidden = true)
	private String lineCode;
	@ApiModelProperty(value = "单据编码",hidden = true)
	private String ccode;
	@ApiModelProperty(value = "应收类型（XSFP销售发票、YSD应收单、XHD销货单、PTSK普通收款、YSK预收款、CGFP采购发票、YFD应付单、DHD到货单、PTFK普通付款、YFK预付款）",hidden = true)
	private String arStyle;
	@ApiModelProperty(value = "单据类型（YSD应收单、SKD收款单、YFD应付单、FKD付款单）",hidden = true)
	private String busStyle;
	@ApiModelProperty(value = "单据类型id（ar应收、ap应付）",hidden = true)
	private String billStyle;
	@ApiModelProperty(value = "单据日期(日期必须小于当前年度启用月)",hidden = true)
	private String ddate;
	@ApiModelProperty(value = "销售客户唯一码",hidden = true)
	private String cvencodeXs;
	@ApiModelProperty(value = "销售结算客户唯一码",hidden = true)
	private String cvencodeJs;
	@ApiModelProperty(value = "项目大类唯一码",hidden = true)
	private String citemClass;
	@ApiModelProperty(value = "项目唯一码",hidden = true)
	private String citemId;
	@ApiModelProperty(value = "部门唯一码",hidden = true)
	private String cdepcode;
	@ApiModelProperty(value = "业务员唯一码",hidden = true)
	private String cmakerId;
	@ApiModelProperty(value = "币种国际编码",hidden = true)
	private String currency;
	@ApiModelProperty(value = "原币预收账款",hidden = true)
	private String ysIsumYuanbi;
	@ApiModelProperty(value = "本币预收账款",hidden = true)
	private String ysIsumBenbi;
	@ApiModelProperty(value = "原币应收账款",hidden = true)
	private String arIsumYuanbi;
	@ApiModelProperty(value = "本币应收账款",hidden = true)
	private String arIsumBenbi;
	@ApiModelProperty(value = "原币期初余额",hidden = true)
	private String isumYuanbi;
	@ApiModelProperty(value = "本币期初余额",hidden = true)
	private String isumBenbi;
	@ApiModelProperty(value = "核销状态（1核销完成，0或其他核销未完成）",hidden = true)
	private String hxFlag;
	@ApiModelProperty(value = "累计核销金额",hidden = true)
	private String hxIsum;
	@ApiModelProperty(value = "累计折扣金额",hidden = true)
	private String hxIdiscount;
	@ApiModelProperty(value = "折扣金额",hidden = true)
	private String idiscount;
	@ApiModelProperty(value = "审核状态（1审核，0或其他未审）",hidden = true)
	private String bcheck;
	@ApiModelProperty(value = "审核时间",hidden = true)
	private String bcheckTime;
	@ApiModelProperty(value = "审核人唯一码（操作员）",hidden = true)
	private String bcheckUser;
	@ApiModelProperty(value = "摘要",hidden = true)
	private String cmemo;
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
	@ApiModelProperty(value = "",hidden = true)
	private String cfree6;
	@ApiModelProperty(value = "",hidden = true)
	private String cfree7;
	@ApiModelProperty(value = "",hidden = true)
	private String cfree8;
	@ApiModelProperty(value = "",hidden = true)
	private String cfree9;
	@ApiModelProperty(value = "",hidden = true)
	private String cfree10;

	@ApiModelProperty(value = "制单日期", hidden = true)
	@CreatedDate
	private LocalDateTime createDate;

}

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
@Table ( "ar_ap_write_off" )
@ApiModel(value="收付款单核销表",description="收付款单核销表")
public class ArApWriteOff {

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
	@ApiModelProperty(value = "收付款单据编号",hidden = true)
	private String ccode;
	@ApiModelProperty(value = "收付款单据日期",hidden = true)
	private String ddate;
	@ApiModelProperty(value = "收付款单行次唯一码(暂不使用)",hidden = true)
	private String lineCode;
	@ApiModelProperty(value = "单据类型id （SKD收款单、FKD付款单）",hidden = true)
	private String billStyle;
	@ApiModelProperty(value = "核销单据类型（XHD销货单，JHD进货单、XSFP销售发票，CGFP采购发票，根据流程设置的核销依据选择，相对固定）",hidden = true)
	private String hxStyle;
	@ApiModelProperty(value = "结算客户/结算供应商唯一码",hidden = true)
	private String cvencode;
	@ApiModelProperty(value = "核销单据编号（销货单、进货单、销售发票、采购发票号码）",hidden = true)
	private String hxCcode;
	@ApiModelProperty(value = "核销单据总金额( 本币）",hidden = true)
	private String hxIsum;
	@ApiModelProperty(value = "本次本币核销金额（本币）",hidden = true)
	private String hxMoney;
	@ApiModelProperty(value = "本次原币核销金额（4位小数点）",hidden = true)
	private String isumYb;
	@ApiModelProperty(value = "折扣本币金额合计",hidden = true)
	private String idiscount;
	@ApiModelProperty(value = "折扣原币金额合计",hidden = true)
	private String idiscountYb;
	@ApiModelProperty(value = "币种国际代码（3位字母）",hidden = true)
	private String currency;
	@ApiModelProperty(value = "表头汇率（默认六位小数点）",hidden = true)
	private String exRate;
	@ApiModelProperty(value = "项目唯一码",hidden = true)
	private String citemcode;
	@ApiModelProperty(value = "项目大类唯一码",hidden = true)
	private String citemClass;
	@ApiModelProperty(value = "是否收款单期初（1期初；0或空非期初）",hidden = true)
	private String skdQichu;
	@ApiModelProperty(value = "是否应收单期初（1期初；0或空非期初）",hidden = true)
	private String ysdQichu;
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

	@ApiModelProperty(value = "核销人",hidden = true)
	private String hxUser;
	@ApiModelProperty(value = "核销时间",hidden = true)
	private String hxDate;

	@ApiModelProperty(value = "冲销类型（YS预收冲应收、YF预付冲应付）",hidden = true)
	private String cxStyle;
	@ApiModelProperty(value = "冲销单号",hidden = true)
	private String cxCcode;
	@ApiModelProperty(value = "冲销日期",hidden = true)
	private String cxDate;

	@ApiModelProperty(value = "制单日期", hidden = true)
	@CreatedDate
	private LocalDateTime createDate;

}

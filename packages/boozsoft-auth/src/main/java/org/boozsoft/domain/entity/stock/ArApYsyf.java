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
@Table ( "ar_ap_ysyf" )
@ApiModel(value="收付款单主表",description="收付款单主表")
public class ArApYsyf {

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
	@ApiModelProperty(value = "单据编号(SK-202204-0001)",hidden = true)
	private String ccode;
	@ApiModelProperty(value = "制单人唯一码（操作员）",hidden = true)
	private String cmaker;
	@ApiModelProperty(value = "单据类型id （SKD收款单、FKD付款单）",hidden = true)
	private String billStyle;
	@ApiModelProperty(value = "单据属性(0:或空蓝字，1:红字)",hidden = true)
	private String bdocumStyle;
	@ApiModelProperty(value = "单据日期",hidden = true)
	private String ddate;
	@ApiModelProperty(value = "业务类型（PTSK普通收款，YSK预收款，PTFK普通付款，YFK预付款）",hidden = true)
	private String busStyle;
	@ApiModelProperty(value = "结算客户（0、应收）/结算供应商唯一码（1、应付）",hidden = true)
	private String cvencode;
	@ApiModelProperty(value = "部门唯一码",hidden = true)
	private String cdepcode;
	@ApiModelProperty(value = "业务员唯一码（员工档案）",hidden = true)
	private String cpersoncode;
	@ApiModelProperty(value = "币种国际代码（3位字母）",hidden = true)
	private String currency;
	@ApiModelProperty(value = "表头汇率（默认六位小数点）",hidden = true)
	private String exRate;
	@ApiModelProperty(value = "审核状态（1审核，0或其他未审）",hidden = true)
	private String bcheck;
	@ApiModelProperty(value = "审核时间",hidden = true)
	private String bcheckTime;
	@ApiModelProperty(value = "审核人唯一码（操作员）",hidden = true)
	private String bcheckUser;
	@ApiModelProperty(value = "核销状态（1已全部核销，0或其他未全部核销）",hidden = true)
	private String writeOffStatus;
	@ApiModelProperty(value = "备注",hidden = true)
	private String cmemo;
	@ApiModelProperty(value = "折扣金额合计",hidden = true)
	private String idiscount;
	@ApiModelProperty(value = "收款金额合计",hidden = true)
	private String isum;
	@ApiModelProperty(value = "累计收款金额合计",hidden = true)
	private String hxIsum;
	@ApiModelProperty(value = "累计折扣金额合计",hidden = true)
	private String hxIdiscount;
	@ApiModelProperty(value = "项目唯一码",hidden = true)
	private String citemcode;
	@ApiModelProperty(value = "项目大类唯一码",hidden = true)
	private String citemClass;
	@ApiModelProperty(value = "销售订单号",hidden = true)
	private String xsddId;
	@ApiModelProperty(value = "销货单号",hidden = true)
	private String xhdId;
	@ApiModelProperty(value = "红字回冲次数",hidden = true)
	private String hzhcNum;
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

	@Transient
	@ApiModelProperty(value = "JSON子集",hidden = true)
	private String entryList;
	@Transient
	@ApiModelProperty(value = "JSON子集",hidden = true)
	private String entryList1;

}

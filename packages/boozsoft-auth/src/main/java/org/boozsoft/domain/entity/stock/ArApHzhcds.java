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
@Table ( "ar_ap_hzhcds" )
@ApiModel(value="红字回冲单子表",description="红字回冲单子表")
public class ArApHzhcds {

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
	@ApiModelProperty(value = "单据编号",hidden = true)
	private String ccode;
	@ApiModelProperty(value = "单据日期",hidden = true)
	private String ddate;
	@ApiModelProperty(value = "行次唯一码",hidden = true)
	private String lineCode;
	@ApiModelProperty(value = "行次(从1开始记数并排序)",hidden = true)
	private String lineId;
	@ApiModelProperty(value = "单据属性(0:或空蓝字，1:红字)",hidden = true)
	private String bdocumStyle;
	@ApiModelProperty(value = "单据类型id （SKD收款单、FKD付款单）",hidden = true)
	private String billStyle;
	@ApiModelProperty(value = "业务类型（SKD收款单、YSD应收单、FKD付款单、YFD应付单）",hidden = true)
	private String busStyle;
	@ApiModelProperty(value = "客户或供应商唯一码",hidden = true)
	private String cvencode;
	@ApiModelProperty(value = "项目唯一码",hidden = true)
	private String citemcode;
	@ApiModelProperty(value = "结算方式（结算方式表）",hidden = true)
	private String cunitid;
	@ApiModelProperty(value = "账号名称（账号档案）",hidden = true)
	private String cunitidF1;
	@ApiModelProperty(value = "项目大类唯一码",hidden = true)
	private String citemClass;
	@ApiModelProperty(value = "币种国际代码（3位字母）",hidden = true)
	private String itaxCode;
	@ApiModelProperty(value = "汇率（6位小数点）",hidden = true)
	private String itaxrate;
	@ApiModelProperty(value = "折扣金额合计",hidden = true)
	private String idiscount;
	@ApiModelProperty(value = "原币金额（4位小数点）",hidden = true)
	private String isumYb;
	@ApiModelProperty(value = "本币金额（4位小数点）",hidden = true)
	private String isum;
	@ApiModelProperty(value = "本次核销金额（本币）",hidden = true)
	private String hxIsum;
	@ApiModelProperty(value = "核销状态（1已全部核销，0或其他未全部核销）",hidden = true)
	private String writeOffStatus;
	@ApiModelProperty(value = "备注",hidden = true)
	private String cmemo;
	@ApiModelProperty(value = "来源单据类型id （YSD应收单、XHD销货单、CGDHD采购到货单、YFD应付单）",hidden = true)
	private String sourcetype;
	@ApiModelProperty(value = "来源单据编码",hidden = true)
	private String sourcecode;
	@ApiModelProperty(value = "来源单据行次唯一码",hidden = true)
	private String sourcedetailId;
	@ApiModelProperty(value = "来源单据日期",hidden = true)
	private String sourcedate;
	@ApiModelProperty(value = "票据编号",hidden = true)
	private String pjCode;
	@ApiModelProperty(value = "票据日期",hidden = true)
	private String pjDate;
	@ApiModelProperty(value = "票据类型(ZZZP转账支票、CDHP承兑汇票)",hidden = true)
	private String pjType;
	@ApiModelProperty(value = "票据兑换日期",hidden = true)
	private String pjDhDate;
	@ApiModelProperty(value = "部门唯一码",hidden = true)
	private String cdepcode;
	@ApiModelProperty(value = "业务员唯一码（员工档案）",hidden = true)
	private String cpersoncode;
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

	@ApiModelProperty(value = "制单日期", hidden = true)
	@CreatedDate
	private LocalDateTime createDate;

}

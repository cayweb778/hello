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
@Table ( "ar_ap_chongxiao" )
@ApiModel(value="统一冲销主表",description="统一冲销主表")
public class ArApChongxiao {

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
	@ApiModelProperty(value = "制单人唯一码（操作员）",hidden = true)
	private String cmaker;
	@ApiModelProperty(value = "单据类型id （应收ar、应付ap）",hidden = true)
	private String billStyle;
	@ApiModelProperty(value = "单据日期",hidden = true)
	private String ddate;
	@ApiModelProperty(value = "业务类型（SS应收冲应收、YS预收冲应收、SF应收冲应付、FF应付冲应付、YF预付冲应付、FS应付冲应收）",hidden = true)
	private String buStyle;
	@ApiModelProperty(value = "转出结算往来单位",hidden = true)
	private String cvencodeZc;
	@ApiModelProperty(value = "转入结算往来单位",hidden = true)
	private String cvencodeZr;
	@ApiModelProperty(value = "转入业务部门唯一码",hidden = true)
	private String cdepcode;
	@ApiModelProperty(value = "转入业务员唯一码（员工档案）",hidden = true)
	private String cpersoncode;
	@ApiModelProperty(value = "币种国际代码（3位字母）",hidden = true)
	private String currency;
	@ApiModelProperty(value = "核销状态（1已全部核销，0或其他未全部核销）",hidden = true)
	private String writeOffStatus;
	@ApiModelProperty(value = "备注",hidden = true)
	private String cmemo;
	@ApiModelProperty(value = "折扣金额合计",hidden = true)
	private String idiscount;
	@ApiModelProperty(value = "收付款金额合计",hidden = true)
	private String isum;
	@ApiModelProperty(value = "累计核销金额",hidden = true)
	private String hxIsum;
	@ApiModelProperty(value = "累计折扣金额",hidden = true)
	private String hxIdiscount;
	@ApiModelProperty(value = "冲销金额",hidden = true)
	private String cxIsum;
	@ApiModelProperty(value = "转入项目唯一码",hidden = true)
	private String citemcode;
	@ApiModelProperty(value = "转入项目大类唯一码",hidden = true)
	private String citemClass;
	@ApiModelProperty(value = "目标单号",hidden = true)
	private String targetCode;
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

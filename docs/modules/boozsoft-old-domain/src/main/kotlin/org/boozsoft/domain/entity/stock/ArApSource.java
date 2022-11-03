package org.boozsoft.domain.entity.stock;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table ( "ar_ap_source" )
@ApiModel(value="收付款单下游单据",description="收付款单下游单据")
public class ArApSource {

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
	@ApiModelProperty(value = "下游单据类型id （JZPZ记账凭证）",hidden = true)
	private String xyBillStyle;
	@ApiModelProperty(value = "本单据类型id （SKD收款单、FKD付款单）",hidden = true)
	private String billStyle;
	@ApiModelProperty(value = "本单单号",hidden = true)
	private String ccode;
	@ApiModelProperty(value = "本单单号日期",hidden = true)
	private String ccodeDate;
	@ApiModelProperty(value = "下游单号",hidden = true)
	private String syccode;
	@ApiModelProperty(value = "下游单号日期",hidden = true)
	private String syccodeDate;

}

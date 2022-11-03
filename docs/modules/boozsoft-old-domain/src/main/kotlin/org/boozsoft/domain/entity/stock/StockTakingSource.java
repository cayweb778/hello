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

import java.util.List;

@Data
@Table ( "stock_taking_source" )
@ApiModel(value="盘点下游单据",description="盘点下游单据")
public class StockTakingSource {


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
	@ApiModelProperty(value = "下游单据类型 （PYRKD盘盈入库单、PKCKD盘亏出库单）",hidden = true)
	private String xyBillStyle;
	@ApiModelProperty(value = "本单据类型 （PDD盘点单）",hidden = true)
	private String billStyle;
	@ApiModelProperty(value = "盘点单编号",hidden = true)
	private String ccode;
	@ApiModelProperty(value = "本单单号日期",hidden = true)
	private String ccodeDate;
	@ApiModelProperty(value = "下游单号",hidden = true)
	private String syccode;
	@ApiModelProperty(value = "下游单号日期",hidden = true)
	private String syccodeDate;






}

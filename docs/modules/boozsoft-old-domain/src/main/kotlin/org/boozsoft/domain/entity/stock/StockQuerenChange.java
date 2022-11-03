package org.boozsoft.domain.entity.stock;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table ( "stock_queren_change" )
@Accessors(chain = true)
@ApiModel(value="出入库确认变动记录",description="出入库确认变动记录表实体")
public class StockQuerenChange {

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
	@ApiModelProperty(value = "类型",hidden = true)
	private String billStyle;
	@ApiModelProperty(value = "销售出库和采购入库单号",hidden = true)
	private String ccode;
	@ApiModelProperty(value = "销售出库和采购入库单日期",hidden = true)
	private String ddate;
	@ApiModelProperty(value = "计量单位",hidden = true)
	private String 	unitId;
	@ApiModelProperty(value = "数量",hidden = true)
	private String 	squantity;
	@ApiModelProperty(value = "确认单数量",hidden = true)
	private String 	squantityQr;
	@ApiModelProperty(value = "存货编码",hidden = true)
	private String 	invoiceId;
	@ApiModelProperty(value = "批号",hidden = true)
	private String 	batchId;
	@ApiModelProperty(value = "生产日期",hidden = true)
	private String 	dpdate;
	@ApiModelProperty(value = "失效日期",hidden = true)
	private String 	dvdate;
	@ApiModelProperty(value = "确认单日期",hidden = true)
	private String 	querdDate;
	@ApiModelProperty(value = "确认单编码",hidden = true)
	private String 	querdId;


	private String 	cfree1;
	private String  cfree2;
	private String 	cfree3;

}
